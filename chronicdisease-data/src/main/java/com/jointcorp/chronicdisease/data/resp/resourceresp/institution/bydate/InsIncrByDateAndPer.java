package com.jointcorp.chronicdisease.data.resp.resourceresp.institution.bydate;

import lombok.Data;

/**
 * @Author: Yulin Dai
 * @CreateTime: 2023-05-09 16:03
 */
//当天机构新增和同比
@Data
public class InsIncrByDateAndPer {
    private int amount;
    private String percent;
    private String type = "新增机构数";
}
