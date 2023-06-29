package com.jointcorp.chronicdisease.data.po;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 日期请求参数
 *
 * @Author zHuH1
 * @Date 2023/6/12 14:23
 **/
@Data
public class DateParam {

    // 开始时间
    private LocalDateTime startTime;

    // 结束时间
    private LocalDateTime endTime;

}
