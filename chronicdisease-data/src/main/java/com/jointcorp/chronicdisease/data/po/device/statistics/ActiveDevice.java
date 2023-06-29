package com.jointcorp.chronicdisease.data.po.device.statistics;

import lombok.Data;

/**
 * @Author zHuH1
 * @Date 2023/5/16 16:52
 **/
@Data
public class ActiveDevice {

    // 最新的活跃日期
    private String onlineStartDate;

    // 活跃天数
    private Integer activeDay;

    // 数量
    private Integer amount;

}
