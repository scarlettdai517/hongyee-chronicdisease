package com.jointcorp.chronicdisease.data.resp.statictisresp;

import lombok.Data;

/**
 * 按地区统计设备数据
 *
 * @Author zHuH1
 * @Date 2023/6/14 16:19
 **/
@Data
public class StatisticByPartitionResp {

    // 地区
    private String partitionName;

    // 总设备数量
    private Integer totalAmount;

    // 新增设备数量
    private Integer incrAmount;

    // 在线设备数量
    private Integer onlineAmount;

    // 活跃设备数量
    private Integer activeAmount;

}
