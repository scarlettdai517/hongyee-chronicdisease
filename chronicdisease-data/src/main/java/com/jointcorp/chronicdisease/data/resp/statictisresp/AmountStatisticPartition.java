package com.jointcorp.chronicdisease.data.resp.statictisresp;

import lombok.Data;

/**
 * @Author zHuH1
 * @Date 2023/6/12 17:11
 **/
@Data
public class AmountStatisticPartition {

    // 数量
    private Integer num;

    // 百分比
    private String per;

    // 日期
    private RangeNumAndDayResp rangeNumAndDayResp;

    public static AmountStatisticPartition partitionConvert(Integer num, String per, RangeNumAndDayResp rangeNumAndDayResp) {
        AmountStatisticPartition partition = new AmountStatisticPartition();
        partition.setNum(num);
        partition.setPer(per);
        partition.setRangeNumAndDayResp(rangeNumAndDayResp);
        return partition;
    }

}
