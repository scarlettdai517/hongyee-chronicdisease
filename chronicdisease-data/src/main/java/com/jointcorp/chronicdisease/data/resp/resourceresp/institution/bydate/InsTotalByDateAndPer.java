package com.jointcorp.chronicdisease.data.resp.resourceresp.institution.bydate;

import lombok.Data;

/**
 * @Author: Yulin Dai
 * @CreateTime: 2023-05-06 10:54
 */
@Data
//当天机构总数和同比
public class InsTotalByDateAndPer {
    private int amount;
    private String percent;
    private String type = "总机构数";

}
