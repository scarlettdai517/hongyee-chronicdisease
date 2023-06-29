package com.jointcorp.chronicdisease.data.resp.statictisresp;

import lombok.Data;

/**
 * 数据量统计实体类
 *
 * @Author zHuH1
 * @Date 2023/5/12 9:10
 **/
@Data
public class StatictisResp {

    // 总数量
    private Integer totalAmount;

    // 与昨日比较百分比
    private String totalPer;

    // 总数量
    private Integer incrAmount;

    // 与昨日比较百分比
    private String incrPer;

    // 总数量
    private Integer onlineAmount;

    // 与昨日比较百分比
    private String onlinePer;

    // 总数量
    private Integer activeAmount;

    // 与昨日比较百分比
    private String activePer;

    private RangeNumAndDayResp totalByRangeDate;

    private RangeNumAndDayResp incrByRangeDate;

    private RangeNumAndDayResp onlineByRangeDate;

    private RangeNumAndDayResp activeByRangeDate;

}
