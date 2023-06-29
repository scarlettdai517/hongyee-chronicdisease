package com.jointcorp.chronicdisease.platform.service;

import com.jointcorp.chronicdisease.data.consts.Consts;
import com.jointcorp.chronicdisease.data.po.device.DeviceType;
import com.jointcorp.chronicdisease.data.req.deviceReq.DeviceTypeAddReq;
import com.jointcorp.chronicdisease.data.req.deviceReq.DeviceTypeDeleteReq;
import com.jointcorp.chronicdisease.data.req.deviceReq.DeviceTypeUpdateReq;
import com.jointcorp.chronicdisease.data.resp.deviceresp.DeviceTypeQueryResp;
import com.jointcorp.chronicdisease.platform.cache.DeviceCache;
import com.jointcorp.chronicdisease.platform.mapper.DeviceTypeMapper;
import com.jointcorp.common.util.SnowflakeIdWorker;
import com.jointcorp.parent.result.ResultCode;
import com.jointcorp.parent.result.ResultData;
import com.jointcorp.parent.result.ResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 设备类型Service
 * @Author zHuH1
 * @Date 2023/5/5 17:03
 **/
@Slf4j
@Service
public class DeviceTypeService {

    @Autowired
    private DeviceTypeMapper deviceTypeMapper;
    @Autowired
    private SnowflakeIdWorker snowflakeIdWorker;
    @Autowired
    private DeviceCache deviceCache;

    /**
     * 查询所有设备类型
     * @return
     */
    public List<DeviceTypeQueryResp> queryDeviceType() {
        List<DeviceType> deviceTypeList = deviceCache.getAllDeviceType();
        if (CollectionUtils.isEmpty(deviceTypeList)) {
            return new ArrayList<>();
        }
        List<DeviceTypeQueryResp> respList = new ArrayList<>();
        for (DeviceType deviceType : deviceTypeList) {
            DeviceTypeQueryResp resp = new DeviceTypeQueryResp();
            resp.setDeviceTypeId(deviceType.getDeviceTypeId().toString());
            resp.setDeviceTypeCode(deviceType.getDeviceTypeCode());
            resp.setDeviceTypeName(deviceType.getDeviceTypeName());
            respList.add(resp);
        }
        return respList;
    }

    /**
     * 增加设备类型
     * @param req
     * @return
     */
    public ResultData addDeviceType(DeviceTypeAddReq req) {
        // 验证设备类型名称是否唯一
        if (verifyAddNameUnique(req.getDeviceTypeName())) {
            return ResultUtil.build(ResultCode.ERROR, "设备类型名称已存在");
        }
        // 验证设备类型编码是否唯一
        if (verifyAddCodeUnique(req.getDeviceTypeCode())) {
            return ResultUtil.build(ResultCode.ERROR, "设备类型编码已存在");
        }
        // 存入数据库
        DeviceType deviceType = new DeviceType();
        deviceType.setDeviceTypeId(snowflakeIdWorker.nextId());
        deviceType.setDeviceTypeCode(req.getDeviceTypeCode());
        deviceType.setDeviceTypeName(req.getDeviceTypeName());
        deviceType.setCreateTime(LocalDateTime.now());
        deviceType.setUpdateTime(LocalDateTime.now());
        log.info("新增设备类型:{}", deviceType);
        int result = deviceTypeMapper.insert(deviceType);
        return analyzeResult(result);
    }

    /**
     * 修改设备类型
     * @param req
     * @return
     */
    public ResultData updateDeviceType(DeviceTypeUpdateReq req) {
        // 验证设备类型名称是否唯一
        if (verifyUpdateNameUnique(req)) {
            return ResultUtil.build(ResultCode.ERROR, "设备类型名称已存在");
        }
//        DeviceType deviceType = deviceTypeMapper.selectByPrimaryKey(req.getDeviceTypeId());
        DeviceType deviceType = queryDeviceTypeById(req.getDeviceTypeId());
        deviceType.setDeviceTypeName(req.getDeviceTypeName());
        deviceType.setUpdateTime(LocalDateTime.now());
        int result = deviceTypeMapper.updateByPrimaryKeySelective(deviceType);
        return analyzeResult(result);
    }

    /**
     * 删除设备类型
     * @param req
     * @return
     */
    public ResultData deleteDeviceType(DeviceTypeDeleteReq req) {
        log.info("删除设备类型:{}", req);
        if (StringUtils.isBlank(req.getDeviceTypeCode())) {
            req.setDeviceTypeCode(queryDeviceTypeById(req.getDeviceTypeId()).getDeviceTypeCode());
        }
        // 如果设备类型和设备型号存在绑定关系，则无法删除
        if (!CollectionUtils.isEmpty(deviceCache.getDeviceModelByTypeCode(req.getDeviceTypeCode()))) {
            return ResultUtil.build(ResultCode.ERROR, "该设备类型与型号存在绑定关系, 无法删除!");
        } else {
            int result = deviceTypeMapper.deleteByPrimaryKey(req.getDeviceTypeId());
            return analyzeResult(result);
        }
    }

    /**
     * 添加设备类型时验证编码唯一性
     * @param deviceTypeCode
     * @return
     */
    public Boolean verifyAddCodeUnique(String deviceTypeCode) {
        return deviceCache.getAllDeviceType().stream().anyMatch(a -> a.getDeviceTypeCode().equals(deviceTypeCode));
    }

    /**
     * 添加设备类型时验证名称唯一性
     * @param deviceTypeName
     * @return
     */
    public Boolean verifyAddNameUnique(String deviceTypeName) {
        return deviceCache.getAllDeviceType().stream().anyMatch(a -> a.getDeviceTypeName().equals(deviceTypeName));
    }

    /**
     * 更新设备类型时验证名称唯一性
     * @param req
     * @return
     */
    public Boolean verifyUpdateNameUnique(DeviceTypeUpdateReq req) {
        // 查询是否已存在相同的设备类型名称
        DeviceType deviceType = deviceCache.getAllDeviceType().stream().filter(f -> f.getDeviceTypeName()
                .equals(req.getDeviceTypeName())).findAny().orElse(null);
        if (deviceType != null && deviceType.getDeviceTypeId() != null) {
            // 如果该修改数据的设备类型id，与已存在的旧数据id不一致，说明修改的不是同一条数据
            // 设备类型名称存在唯一性，无法修改
            if (!req.getDeviceTypeId().equals(deviceType.getDeviceTypeId())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 根据主键id查询设备类型数据
     * @param deviceTypeId
     * @return
     */
    public DeviceType queryDeviceTypeById(Long deviceTypeId) {
        return deviceCache.getAllDeviceType().stream().filter(f -> f.getDeviceTypeId().equals(deviceTypeId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("数据不存在"));
    }

    /**
     * 分析操作结果，如果数据改动成功，则删除缓存
     * @param result
     * @return
     */
    public ResultData analyzeResult(int result) {
        if (result > 0) {
            // 删除缓存
            deviceCache.clearCache(Consts.DEVICE_TYPE_KEY);
            return ResultUtil.success("操作成功!");
        } else {
            return ResultUtil.build(ResultCode.ERROR, "操作失败");
        }
    }


}
