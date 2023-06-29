package com.jointcorp.chronicdisease.platform.mapper;

import com.jointcorp.chronicdisease.data.po.device.DeviceBindInstitution;
import com.jointcorp.chronicdisease.platform.base.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author zHuH1
 * @Date 2023/5/5 16:48
 **/
@Component
public interface DeviceBindInstitutionMapper extends BaseMapper<DeviceBindInstitution> {

    int unbindDeviceAndInstitution(@Param("deviceIdent") String deviceIdent);

    int insertBatchList(@Param("list") List<DeviceBindInstitution> list);

    /**
     * 查询最近使用过的设备信息
     * @return
     */
    List<DeviceBindInstitution> selectDeviceRecentlyused();

}
