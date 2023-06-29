package com.jointcorp.chronicdisease.data.req.deviceReq;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @Author zHuH1
 * @Date 2023/5/23 17:23
 **/
@Data
public class DeviceTypeAddReq {

    @NotNull(message = "设备类型编码不能为空")
    private String deviceTypeCode;

    @NotBlank(message = "设备类型名称不能为空")
    private String deviceTypeName;
}
