package com.jointcorp.chronicdisease.data.po.device.statistics;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 设备统计时间集合
 * 后缀Date : yyyy-MM-dd
 * 后缀DateTime : yyyy-MM-dd HH:mm:ss
 *
 * @Author zHuH1
 * @Date 2023/5/17 16:27
 **/
@Data
public class DateCollection {

//    // 所选日期时间
//    private String todayDateTime;
//
//    // 所选日期
//    private String todayDate;

    // 查询范围起始时间
    private LocalDateTime queryStartDateTime;

    // 查询范围结束时间
    private LocalDateTime queryEndDateTime;

    // 比较起始时间
    private LocalDateTime compareStartDateTime;

    // 比较范围结束时间
    private LocalDateTime compareEndDateTime;

//    // 七天前的日期开始时间
//    private String weekStartDateTime;

}
