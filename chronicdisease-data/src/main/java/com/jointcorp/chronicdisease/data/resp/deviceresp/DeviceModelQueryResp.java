package com.jointcorp.chronicdisease.data.resp.deviceresp;

import lombok.Data;

/**
 * @Author zHuH1
 * @Date 2023/5/24 8:58
 **/
@Data
public class DeviceModelQueryResp {

    // 主键
    private String deviceModelId;

    // 设备类型
    private String deviceTypeCode;

    // 设备型号value值
    private String deviceModelValue;

    // 设备型号value值描述
    private String deviceModelValueDesc;

}
