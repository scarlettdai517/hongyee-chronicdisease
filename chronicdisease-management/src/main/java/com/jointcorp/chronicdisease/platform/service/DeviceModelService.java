package com.jointcorp.chronicdisease.platform.service;

import com.jointcorp.chronicdisease.data.consts.Consts;
import com.jointcorp.chronicdisease.data.po.device.DeviceModel;
import com.jointcorp.chronicdisease.data.req.deviceReq.DeviceModelAddReq;
import com.jointcorp.chronicdisease.data.req.deviceReq.DeviceModelDeleteReq;
import com.jointcorp.chronicdisease.data.req.deviceReq.DeviceModelUpdateReq;
import com.jointcorp.chronicdisease.data.resp.deviceresp.DeviceModelQueryResp;
import com.jointcorp.chronicdisease.platform.cache.DeviceCache;
import com.jointcorp.chronicdisease.platform.mapper.DeviceMapper;
import com.jointcorp.chronicdisease.platform.mapper.DeviceModelMapper;
import com.jointcorp.common.util.SnowflakeIdWorker;
import com.jointcorp.parent.result.ResultCode;
import com.jointcorp.parent.result.ResultData;
import com.jointcorp.parent.result.ResultUtil;
import lombok.Synchronized;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 设备型号Service
 *
 * @Author zHuH1
 * @Date 2023/5/5 17:03
 **/
@Service
public class DeviceModelService {

    @Autowired
    private DeviceModelMapper deviceModelMapper;
    @Autowired
    private SnowflakeIdWorker snowflakeIdWorker;
    @Autowired
    private DeviceCache deviceCache;
    @Autowired
    private DeviceMapper deviceMapper;

    /**
     * 根据设备类型查询设备型号
     * @param deviceTypeCode
     * @return
     */
    public List<DeviceModelQueryResp> queryByType(String deviceTypeCode) {
        List<DeviceModel> deviceModelList = deviceCache.getDeviceModelByTypeCode(deviceTypeCode);
        if (CollectionUtils.isEmpty(deviceModelList)) {
            return new ArrayList<>();
        }
        List<DeviceModelQueryResp> respList = new ArrayList<>();
        for (DeviceModel deviceModel : deviceModelList) {
            DeviceModelQueryResp resp = new DeviceModelQueryResp();
            resp.setDeviceModelId(deviceModel.getDeviceModelId().toString());
            resp.setDeviceTypeCode(deviceModel.getDeviceTypeCode());
            resp.setDeviceModelValue(deviceModel.getDeviceModelValue());
            resp.setDeviceModelValueDesc(deviceModel.getDeviceModelValueDesc());
            respList.add(resp);
        }
        return respList;
    }

    /**
     * 添加设备型号
     * @param req
     * @return
     */
    public ResultData addDeviceModel(DeviceModelAddReq req) {
        // 验证设备型号唯一性
        if (!verifyAddModelValueUnique(req.getDeviceTypeCode(), req.getDeviceModelValue())) {
            return ResultUtil.build(ResultCode.ERROR, "该设备型号已存在");
        }
        // 存入数据库
        DeviceModel deviceModel = new DeviceModel();
        deviceModel.setDeviceModelId(snowflakeIdWorker.nextId());
        deviceModel.setDeviceTypeCode(req.getDeviceTypeCode());
        deviceModel.setDeviceModelKey(queryNextKey(req.getDeviceTypeCode()));
        deviceModel.setDeviceModelValue(req.getDeviceModelValue());
        deviceModel.setDeviceModelValueDesc(req.getDeviceModelDesc());
        deviceModel.setCreateTime(LocalDateTime.now());
        deviceModel.setUpdateTime(LocalDateTime.now());
        int result = deviceModelMapper.insert(deviceModel);
        return analyzeResult(result);
    }

    /**
     * 修改设备型号
     * @param req
     * @return
     */
    public ResultData updateDeviceModel(DeviceModelUpdateReq req) {
        // 验证设备型号唯一性
        if (verifyUpdateModelValueUnique(req.getDeviceModelId(), req.getDeviceTypeCode(), req.getDeviceModelValue())) {
            return ResultUtil.build(ResultCode.ERROR, "该设备型号已存在");
        }
        // 存入数据库
        DeviceModel deviceModel = new DeviceModel();
        BeanUtils.copyProperties(req, deviceModel);
        deviceModel.setUpdateTime(LocalDateTime.now());
        int result = deviceModelMapper.updateByPrimaryKeySelective(deviceModel);
        return analyzeResult(result);
    }

    /**
     * 删除设备型号
     * @param req
     * @return
     */
    public ResultData deleteDeviceModel(DeviceModelDeleteReq req) {
        // 根据设备类型和对应型号key，查询是否存在设备绑定着该型号
        Example example = new Example(DeviceModel.class);
        example.createCriteria().andEqualTo("deviceTypeCode", req.getDeviceTypeCode())
                .andEqualTo("deviceModelKey", req.getDeviceModelKey());
        if (deviceMapper.selectCountByExample(example) > 0) {
            return ResultUtil.build(ResultCode.ERROR, "当前存在设备绑定该设备型号,无法删除!");
        }
        int result = deviceModelMapper.deleteByPrimaryKey(req.getDeviceModelId());
        return analyzeResult(result);
    }

    /**
     * 查询插入数据的最新字典key
     * @param deviceTypeCode
     * @return
     */
    @Synchronized
    public Integer queryNextKey(String deviceTypeCode) {
        // 查询设备类型已被绑定的设备型号的最新key
        List<DeviceModel> deviceModelList = deviceCache.getDeviceModelByTypeCode(deviceTypeCode);
        // list为空说明此设备类型还未绑定设备型号，初始值为0
        if (CollectionUtils.isEmpty(deviceModelList)) {
            return 0;
        }
        Integer maxCode = deviceCache.getDeviceModelByTypeCode(deviceTypeCode).stream()
                .max(Comparator.comparing(x -> x.getDeviceModelKey())).orElse(null).getDeviceModelKey();
        // maxCode存在则+1返回,作为新数据插入的key
        return maxCode + 1;
    }

    /**
     * 添加时验证设备型号唯一性
     * @param deviceTypeCode
     * @param deviceModelValue
     * @return
     */
    public Boolean verifyAddModelValueUnique(String deviceTypeCode, String deviceModelValue) {
        List<DeviceModel> deviceModelList = deviceCache.getDeviceModelByTypeCode(deviceTypeCode);
        if (CollectionUtils.isEmpty(deviceModelList)) {
            return true;
        }
        return deviceModelList.stream().noneMatch(a -> a.getDeviceModelValue().equals(deviceModelValue));
    }

    /**
     * 更新时验证设备型号唯一性
     * @param deviceTypeCode
     * @param deviceModelValue
     * @return
     */
    public Boolean verifyUpdateModelValueUnique(Long deviceModelId, String deviceTypeCode, String deviceModelValue) {
        List<DeviceModel> deviceModelList = deviceCache.getDeviceModelByTypeCode(deviceTypeCode);
        if (CollectionUtils.isEmpty(deviceModelList)) {
            return true;
        }
        // 查询是否已存在相同的设备型号
        DeviceModel deviceModel = deviceModelList.stream().filter(f -> f.getDeviceModelValue().equals(deviceModelValue))
                .findAny().orElse(null);
        // 如果该修改数据的设备型号id，与已存在的旧数据id不一致，说明修改的不是同一条数据
        // 设备型号存在唯一性，无法修改
        if (deviceModel != null && deviceModel.getDeviceModelId() != null) {
            if (!deviceModelId.equals(deviceModel.getDeviceModelId())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 分析操作结果，如果数据改动成功，则删除缓存
     * @param result
     * @return
     */
    public ResultData analyzeResult(int result) {
        if (result > 0) {
            // 更新redis缓存
            deviceCache.clearCache(Consts.DEVICE_MODEL_KEY);
            return ResultUtil.success("操作成功!");
        } else {
            return ResultUtil.build(ResultCode.ERROR, "操作失败");
        }
    }
}
