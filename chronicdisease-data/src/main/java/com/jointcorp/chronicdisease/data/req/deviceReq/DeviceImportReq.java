package com.jointcorp.chronicdisease.data.req.deviceReq;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * 添加设备请求体
 *
 * @Author zHuH1
 * @Date 2023/5/5 13:59
 **/
@Data
public class DeviceImportReq {

    // 设备类型
    @ExcelProperty("设备类型")
    private String deviceTypeCode;

    // 设备型号
    @ExcelProperty("设备型号")
    private String deviceModelKey;

    // 设备地址（mac\imei）
    @ExcelProperty("设备地址")
    private String deviceIdent;

    // 设备名称
    @ExcelProperty("设备名称")
    private String deviceName;

    // 所属机构
    @ExcelProperty("机构")
    private String institution;

}
