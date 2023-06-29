package com.jointcorp.chronicdisease.data.req.deviceReq;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 更新设备类型请求体
 *
 * @Author zHuH1
 * @Date 2023/5/5 16:37
 **/
@Data
public class DeviceTypeUpdateReq {

    @NotNull(message = "设备类型id不能为空")
    private Long deviceTypeId;

    @NotBlank(message = "设备类型名称不能为空")
    private String deviceTypeName;

}
