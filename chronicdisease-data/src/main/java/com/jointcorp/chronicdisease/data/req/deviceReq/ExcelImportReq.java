package com.jointcorp.chronicdisease.data.req.deviceReq;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @Author zHuH1
 * @Date 2023/5/23 17:00
 **/
@Data
public class ExcelImportReq {

    // 设备类型
    private String deviceTypeCode;

    // 设备型号值
    private String deviceModelKey;

    // 设备地址
    private String deviceIdent;

    // 设备名称
    private String deviceName;

    // 所属机构
    private Long institutionId;
}
