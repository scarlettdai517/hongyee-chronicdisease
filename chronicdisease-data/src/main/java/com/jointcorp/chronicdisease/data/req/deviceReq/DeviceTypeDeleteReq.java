package com.jointcorp.chronicdisease.data.req.deviceReq;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 删除设备类型请求体
 *
 * @Author zHuH1
 * @Date 2023/5/9 9:19
 **/
@Data
public class DeviceTypeDeleteReq {

    @NotNull(message = "设备类型id不能为空")
    private Long deviceTypeId;

    @NotBlank(message = "设备类型编码不能为空")
    private String deviceTypeCode;
    
}
