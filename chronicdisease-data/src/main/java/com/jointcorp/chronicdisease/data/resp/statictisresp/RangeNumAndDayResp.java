package com.jointcorp.chronicdisease.data.resp.statictisresp;

import lombok.Data;

import java.util.List;

/**
 * 时间范围内，日期和数量汇总
 *
 * @Author zHuH1
 * @Date 2023/5/12 11:26
 **/
@Data
public class RangeNumAndDayResp {

    // 日期
    List<String> days;

    // 数量
    List<Integer> nums;

}
