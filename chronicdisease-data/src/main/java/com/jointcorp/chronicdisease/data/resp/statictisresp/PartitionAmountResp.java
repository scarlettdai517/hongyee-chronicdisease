package com.jointcorp.chronicdisease.data.resp.statictisresp;

import lombok.Data;

/**
 * 区分统计数量
 *
 * @Author zHuH1
 * @Date 2023/5/17 9:12
 **/
@Data
public class PartitionAmountResp {

    private String region;

    private Integer Amount;

    private String per;
}
