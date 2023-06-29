package com.jointcorp.chronicdisease.platform.cache;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.benmanes.caffeine.cache.Cache;
import com.jointcorp.chronicdisease.data.consts.Consts;
import com.jointcorp.chronicdisease.data.po.device.DeviceModel;
import com.jointcorp.chronicdisease.data.po.device.DeviceType;
import com.jointcorp.chronicdisease.platform.mapper.DeviceModelMapper;
import com.jointcorp.chronicdisease.platform.mapper.DeviceTypeMapper;
import com.jointcorp.chronicdisease.platform.service.InstitutionService;
import com.jointcorp.chronicdisease.platform.utils.JacksonUtil;
import com.jointcorp.common.util.JsonUtil;
import com.jointcorp.redissoncache.client.RedissonCacheClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Author zHuH1
 * @Date 2023/5/8 10:42
 **/
@Component
public class DeviceCache implements ApplicationRunner {

    @Autowired
    private InstitutionService institutionService;
    @Autowired
    private Cache<String, Object> localCache;
    @Autowired
    private RedissonCacheClient redissonCacheClient;

    @Autowired
    private DeviceTypeMapper deviceTypeMapper;
    @Autowired
    private DeviceModelMapper deviceModelMapper;

    /**
     * 缓存机构下的设备数据
     * @param corporateId
     * @param userSubDeviceIdents
     */
    public void cacheUserDeviceIdents(long corporateId, List<String> userSubDeviceIdents) {
        String key = Consts.USER_SUBDEVICE_KEY + corporateId;
        redissonCacheClient.put(key, JsonUtil.objectToJson(userSubDeviceIdents), 30, TimeUnit.DAYS);
    }

    /**
     * 获取所有设备类型
     * @return
     */
    public List<DeviceType> getAllDeviceType () {
        return (List<DeviceType>) localCache.get(Consts.DEVICE_TYPE_KEY, k -> getRedisDeviceType());
    }

    /**
     * 根据设备类型获取对应的设备型号
     * @param typeCode
     * @return
     */
    public List<DeviceModel> getDeviceModelByTypeCode(String typeCode) {
        Map<String, List<DeviceModel>> map = (Map<String, List<DeviceModel>>) localCache.get(Consts.DEVICE_MODEL_KEY,
                k -> getRedisDeviceModel());
        List<DeviceModel> data = new ArrayList<>();
        if (!CollectionUtils.isEmpty(map)) {
            data = map.get(typeCode);
        }
        return JacksonUtil.objectMapper().convertValue(data, new TypeReference<List<DeviceModel>>() {});
    }

    /**
     * 获取所有设备型号
     * @return
     */
    public List<DeviceModel> getAllDeviceModel() {
        List<DeviceType> deviceTypeList = getAllDeviceType();
        List<DeviceModel> deviceModelList = new ArrayList<>();
        for (DeviceType deviceType : deviceTypeList) {
            Map<String, List<DeviceModel>> map = (Map<String, List<DeviceModel>>) localCache.get(Consts.DEVICE_MODEL_KEY,
                    k -> getRedisDeviceModel());
            if (!CollectionUtils.isEmpty(map.get(deviceType.getDeviceTypeCode()))) {
                deviceModelList.addAll(map.get(deviceType.getDeviceTypeCode()));
            }
        }
        // 解决返回的list中值是LinkedHashMap类型
        return JacksonUtil.objectMapper().convertValue(deviceModelList, new TypeReference<List<DeviceModel>>() {});
    }

    /**
     * 获取【"code:key" : "value"】格式的设备型号
     * @return
     */
    public Map<String, String> getDeviceModelKeyValue() {
        String value = redissonCacheClient.get(Consts.DEVICE_MODEL_MAP_KEY);
        if (StringUtils.isNotBlank(value)) {
            return JacksonUtil.jsonToMap(value);
        } else {
            List<DeviceModel> deviceModelList = getAllDeviceModel();
            if (CollectionUtils.isEmpty(deviceModelList)) {
                return null;
            }
            Map<String, String> map =  deviceModelList.stream().collect(Collectors.toMap(
                    item -> item.getDeviceTypeCode()+ ":" + item.getDeviceModelKey(), DeviceModel::getDeviceModelValue));
            redissonCacheClient.put(Consts.DEVICE_MODEL_MAP_KEY, JsonUtil.objectToJson(map), 30, TimeUnit.DAYS);
            return map;
        }
    }

    /**
     * 缓存最新的设备类型数据
     */
    public void cacheDeviceType() {
        List<DeviceType> deviceTypeList = deviceTypeMapper.selectAll();
        if (!CollectionUtils.isEmpty(deviceTypeList)) {
            redissonCacheClient.put(Consts.DEVICE_TYPE_KEY, JsonUtil.objectToJson(deviceTypeList), 30, TimeUnit.DAYS);
            localCache.put(Consts.DEVICE_TYPE_KEY, deviceTypeList);
        }
    }

    /**
     * 缓存最新的设备型号数据
     */
    public void cacheDeviceModel() {
        List<DeviceModel> deviceModelList = deviceModelMapper.selectAll();
        if (!CollectionUtils.isEmpty(deviceModelList)) {
            Map<String, List<DeviceModel>> deviceModelMap = deviceModelList.stream().collect(Collectors.groupingBy(DeviceModel::getDeviceTypeCode));
            redissonCacheClient.put(Consts.DEVICE_MODEL_KEY, JsonUtil.objectToJson(deviceModelMap), 30, TimeUnit.DAYS);
            localCache.put(Consts.DEVICE_MODEL_KEY, deviceModelMap);
        }
    }

    /**
     * 获取redis中的设备类型数据
     * @return
     */
    public List<DeviceType> getRedisDeviceType() {
        List<DeviceType> list = new ArrayList<>();
        String jsonDeviceType = redissonCacheClient.get(Consts.DEVICE_TYPE_KEY);
        if (StringUtils.isNotBlank(jsonDeviceType)) {
            list = JsonUtil.jsonToList(jsonDeviceType, DeviceType.class);
        } else {
            List<DeviceType> deviceTypeList = deviceTypeMapper.selectAll();
            if (!CollectionUtils.isEmpty(deviceTypeList)) {
                list = deviceTypeList;
                redissonCacheClient.put(Consts.DEVICE_TYPE_KEY, JsonUtil.objectToJson(list), 30, TimeUnit.DAYS);
            }
        }
        return list;
    }

    /**
     * 获取redis中的设备型号数据
     * @return
     */
    public Map<String, List<DeviceModel>> getRedisDeviceModel() {
        Map<String, List<DeviceModel>> deviceModelMap = new HashMap<>();
        String jsonDeviceModel = redissonCacheClient.get(Consts.DEVICE_MODEL_KEY);
        if (StringUtils.isNotBlank(jsonDeviceModel)) {
            deviceModelMap = JacksonUtil.jsonToMapWithList(jsonDeviceModel);
        } else {
            List<DeviceModel> deviceModelList = deviceModelMapper.selectAll();
            if (!CollectionUtils.isEmpty(deviceModelList)) {
                deviceModelMap = deviceModelList.stream().collect(Collectors.groupingBy(DeviceModel::getDeviceTypeCode));
                redissonCacheClient.put(Consts.DEVICE_MODEL_KEY, JsonUtil.objectToJson(deviceModelMap), 30, TimeUnit.DAYS);
            }
        }
        return deviceModelMap;
    }

    /**
     * 清除缓存
     * @param key
     */
    public void clearCache(String key) {
        localCache.invalidate(key);
        redissonCacheClient.delete(key);
    }

    /**
     * 清除指定机构及其父级机构的设备列表缓存
     * @param institutionId
     */
    public void clearDeviceCache(Long institutionId) {
        List<Long> superiorInstitutionId = institutionService.selectSuperiorInst(institutionId);
        for(Long id : superiorInstitutionId) {
            redissonCacheClient.delete(Consts.USER_SUBDEVICE_KEY + id);
        }
    }

    /**
     * 批量清除机构及其父级机构的设备列表缓存
     * @param instIdList
     */
    public void batchClearDeviceCache(List<Long> instIdList) {
        List<Long> totalList = new ArrayList<>();
        for (Long instId : instIdList) {
            List<Long> superiorInstitutionId = institutionService.selectSuperiorInst(instId);
            totalList.addAll(superiorInstitutionId);
        }
        // 去重,再遍历清除缓存
        for(Long id : totalList.stream().distinct().collect(Collectors.toList())) {
            redissonCacheClient.delete(Consts.USER_SUBDEVICE_KEY + id);
        }
    }


    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 设备类型数据
        List<DeviceType> deviceTypeList = getRedisDeviceType();
        localCache.put(Consts.DEVICE_TYPE_KEY, deviceTypeList);
        System.out.println(localCache.get(Consts.DEVICE_TYPE_KEY, k -> k));

        // 设备型号数据List格式
        Map<String, List<DeviceModel>> deviceModelByTypeCode = getRedisDeviceModel();
        localCache.put(Consts.DEVICE_MODEL_KEY, deviceModelByTypeCode);
        System.out.println(localCache.get(Consts.DEVICE_MODEL_KEY, k -> k));
    }

}
