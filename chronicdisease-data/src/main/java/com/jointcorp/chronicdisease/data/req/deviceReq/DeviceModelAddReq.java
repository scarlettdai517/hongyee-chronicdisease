package com.jointcorp.chronicdisease.data.req.deviceReq;

import lombok.Data;

import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 添加设备型号请求体
 *
 * @Author zHuH1
 * @Date 2023/5/6 8:48
 **/
@Data
public class DeviceModelAddReq {

    // 设备类型编码
    @NotNull(message = "设备类型编码不能为空")
    private String deviceTypeCode;

    // 设备型号名称
    @NotBlank(message = "设备型号不能为空")
    private String deviceModelValue;

    // 设备型号备注
    private String deviceModelDesc;

}
