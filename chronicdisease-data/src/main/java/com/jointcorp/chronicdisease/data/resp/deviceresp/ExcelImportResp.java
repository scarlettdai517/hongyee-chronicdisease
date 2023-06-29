package com.jointcorp.chronicdisease.data.resp.deviceresp;

import lombok.Data;

/**
 * @Author zHuH1
 * @Date 2023/5/23 16:04
 **/
@Data
public class ExcelImportResp {

    // 正确的行数
    private Integer correctCount;

    // 错误的行数
    private Integer errorCount;

    // 纠错提示
    private String errorInfo;

}
