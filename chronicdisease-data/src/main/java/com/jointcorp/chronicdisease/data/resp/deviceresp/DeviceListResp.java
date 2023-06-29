package com.jointcorp.chronicdisease.data.resp.deviceresp;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.jointcorp.chronicdisease.data.annotation.DeviceDict;
import lombok.Data;

/**
 * @Author zHuH1
 * @Date 2023/5/10 9:43
 **/
@Data
public class DeviceListResp {

    // 使用者id
    @ExcelProperty("设备id")
    private String deviceId;

    // 设备地址（mac\imei）
    @ExcelProperty("设备地址")
    private String deviceIdent;

    // 设备名称
    @ExcelProperty("设备名称")
    private String deviceName;

    // 机构
    @ExcelProperty("机构")
    private String institutionId;

    // 机构名称
    @ExcelProperty("机构名称")
    private String institutionName;

    // 会员id
    @ExcelProperty("会员id")
    private String memberId;

    // 会员名称
    @ExcelProperty("会员名称")
    private String memberName;

    // 设备类型
    @DeviceDict(dictName = "DEVICE_TYPE", dictField = "deviceTypeCodeText")
    @ExcelIgnore
    private String deviceTypeCode;

    // 设备类型对应的值
    @ExcelProperty("设备类型")
    private String deviceTypeCodeText;

    // 设备型号
    @DeviceDict(dictName = "DEVICE_MODEL", dictField = "deviceModelKeyText")
    @ExcelIgnore
    private Integer deviceModelKey;

    // 设备型号对应的值
    @ExcelProperty("设备型号")
    private String deviceModelKeyText;

    // 激活状态 true是 false否`
    @ExcelProperty("激活状态")
    private Boolean activeState;

    // 在线状态 true是 false否
    @ExcelProperty("在线状态")
    private Boolean onlineState;

}
