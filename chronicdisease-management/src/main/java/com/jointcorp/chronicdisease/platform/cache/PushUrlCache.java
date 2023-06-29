package com.jointcorp.chronicdisease.platform.cache;

import com.jointcorp.chronicdisease.data.consts.Consts;
import com.jointcorp.chronicdisease.data.po.DataPushUrlConfig;
import com.jointcorp.chronicdisease.data.po.Institution;
import com.jointcorp.chronicdisease.data.po.device.DeviceBindInstitution;
import com.jointcorp.chronicdisease.platform.mapper.DataPushUrlConfigMapper;
import com.jointcorp.chronicdisease.platform.mapper.DeviceBindInstitutionMapper;
import com.jointcorp.chronicdisease.platform.mapper.InstitutionMapper;
import com.jointcorp.redissoncache.client.RedissonCacheClient;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 推送地址缓存
 * @Author: Xu-xg
 * @CreateTime: 2023-06-19 17:05
 */
@Component
public class PushUrlCache {

    @Autowired
    private RedissonCacheClient redissonCacheClient;
    @Autowired
    private InstitutionMapper institutionMapper;
    @Autowired
    private DataPushUrlConfigMapper pushUrlConfigMapper;
    @Autowired
    private DeviceBindInstitutionMapper deviceBindInstitutionMapper;

    private Logger logger = LoggerFactory.getLogger(PushUrlCache.class);

    /**
     * 缓存最近使用过的部分设备的地址
     */
    public void cachePushUrl() {
        //最近30天使用过的设备
        //List<DeviceBindInstitution> bindInstitutionList = deviceBindInstitutionMapper.selectDeviceRecentlyused();
        //List<Long> instIds = bindInstitutionList.stream().map(DeviceBindInstitution::getInstitutionId).collect(Collectors.toList());
        ////设备所在机构对应的推送地址
        //Example example = new Example(DataPushUrlConfig.class);
        //example.createCriteria().andIn("institutionId",instIds);
        //List<DataPushUrlConfig> pushUrlConfigList = pushUrlConfigMapper.selectByExample(example);
        ////institutionId -> DataPushUrlConfig
        //Map<Long,DataPushUrlConfig> pushMap = pushUrlConfigList.stream().collect(Collectors.toMap(DataPushUrlConfig::getInstitutionId, b -> b));
        ////
        //for(DeviceBindInstitution deviceBindInstitution : bindInstitutionList) {
        //    DataPushUrlConfig pushUrlConfig = pushMap.get(deviceBindInstitution.getInstitutionId());
        //    put(pushUrlConfig);
        //}




        //
        //
        //List<Institution> institutions = institutionMapper.selectAll();
        //Map<Long,Long> parentMap = new HashMap<>();
        ////所有的一级机构
        //List<Long> topInsts = new ArrayList<>();
        //Map<Long,Institution> allInsts = new HashMap<>(institutions.size());
        //for(Institution inst : institutions) {
        //    allInsts.put(inst.getInstitutionId(),inst);
        //}
        //for (Map.Entry<Long, Institution> entry : allInsts.entrySet()) {
        //    if(entry.getValue().getParentId() == 0) {
        //        parentMap.put(entry.getKey() ,entry.getKey());
        //        topInsts.add(entry.getKey());
        //    } else {
        //        Institution p = getInst(entry.getValue(),allInsts);
        //        parentMap.put(entry.getValue().getInstitutionId(),p.getInstitutionId());
        //    }
        //}
        ////查询所有一级机构的推送地址
        //getPushUrl(topInsts);
    }

    //private static Institution getInst(Institution p,Map<Long,Institution> allInsts) {
    //    if(p.getParentId() == 0) {
    //        return p;
    //    } else {
    //        return getInst(allInsts.get(p.getParentId()),allInsts);
    //    }
    //}

    //private void getPushUrl(List<Long> topInsts) {
    //    Example example = new Example(DataPushUrlConfig.class);
    //    example.createCriteria().andEqualTo("parentId",0);
    //    List<DataPushUrlConfig> urlConfigs = pushUrlConfigMapper.selectAll();
    //    put(urlConfigs);
    //}

    //public void put(List<DataPushUrlConfig> urlConfigs) {
    //    for(DataPushUrlConfig url : urlConfigs) {
    //        redissonCacheClient.put(Consts.PUSH_URL_KEY + url.getInstitutionId(),url.getUrl(),30, TimeUnit.DAYS);
    //    }
    //}

    public void put(String device, DataPushUrlConfig urlConfig) {
        redissonCacheClient.put(Consts.PUSH_URL_KEY + urlConfig.getInstitutionId(),urlConfig.getUrl(),30, TimeUnit.DAYS);
    }

    /**
     * 获取设备的推送地址，推送地址是按照设备保存的
     * @param device 设备型号
     * @param identify 设备编号
     * @return
     */
    public String getUrl(String device,String identify) {
        return redissonCacheClient.get(getKey(device,identify));
    }

    /**
     * 获取设备的推送地址，推送地址是按照设备保存的
     * @param instId 机构id
     * @param device 设备型号
     * @param identify 设备编号
     * @return
     */
    public String getUrl(long instId,String device,String identify) {
        Example example = new Example(DataPushUrlConfig.class);
        example.createCriteria().andEqualTo("institutionId",instId);
        DataPushUrlConfig pushUrlConfig = pushUrlConfigMapper.selectOneByExample(example);
        if(pushUrlConfig == null) {
            return null;
        }
        redissonCacheClient.put(getKey(device,identify),pushUrlConfig.getUrl(),30, TimeUnit.DAYS);
        return pushUrlConfig.getUrl();
    }

    private String getKey(String device, String identify) {
        return Consts.PUSH_URL_KEY + ":" + device + ":" + identify;
    }

}
