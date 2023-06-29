package com.jointcorp.chronicdisease.data.resp.deviceresp;

import lombok.Data;

/**
 * @Author zHuH1
 * @Date 2023/5/23 17:33
 **/
@Data
public class DeviceTypeQueryResp {

    // 主键
    private String deviceTypeId;

    // 设备类型名称
    private String deviceTypeName;

    // 设备类型标识
    private String deviceTypeCode;

}
