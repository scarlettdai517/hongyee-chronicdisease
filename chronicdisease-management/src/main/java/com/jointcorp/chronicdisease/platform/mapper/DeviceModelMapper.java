package com.jointcorp.chronicdisease.platform.mapper;

import com.jointcorp.chronicdisease.data.po.device.DeviceModel;
import com.jointcorp.chronicdisease.platform.base.BaseMapper;
import org.springframework.stereotype.Component;

/**
 * @Author zHuH1
 * @Date 2023/5/5 16:48
 **/
@Component
public interface DeviceModelMapper extends BaseMapper<DeviceModel> {

    Integer selectMaxKey(String deviceTypeCode);
}
