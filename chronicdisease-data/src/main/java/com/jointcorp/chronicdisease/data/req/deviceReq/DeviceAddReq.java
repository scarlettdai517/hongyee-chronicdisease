package com.jointcorp.chronicdisease.data.req.deviceReq;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 添加设备请求体
 *
 * @Author zHuH1
 * @Date 2023/5/5 13:59
 **/
@Data
public class DeviceAddReq {

    // 设备类型
    @NotBlank(message = "设备类型不能为空")
    private String deviceTypeCode;

    // 设备型号
    @NotNull(message = "设备型号不能为空")
    private Integer deviceModelKey;

    // 设备地址（mac\imei）
    @NotBlank(message = "设备地址（mac、imei）不能为空")
    private String deviceIdent;

    // 设备名称
    @NotBlank(message = "设备名称不能为空")
    private String deviceName;

    // 是否与机构绑定：true 是 false 否
    private Boolean bindingState;

    // 所属机构
    private Long institutionId;

}
