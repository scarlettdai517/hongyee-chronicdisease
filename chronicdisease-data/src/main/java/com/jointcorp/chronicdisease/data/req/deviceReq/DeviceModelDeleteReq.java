package com.jointcorp.chronicdisease.data.req.deviceReq;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 更新设备型号请求体
 *
 * @Author zHuH1
 * @Date 2023/5/8 16:54
 **/
@Data
public class DeviceModelDeleteReq {

    @NotNull(message = "设备型号id不能为空")
    private Long deviceModelId;

    @NotNull(message = "设备类型编码不能为空")
    private String deviceTypeCode;

    @NotNull(message = "设备型号key不能为空")
    private Integer deviceModelKey;

}
