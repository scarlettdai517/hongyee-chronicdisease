package com.jointcorp.chronicdisease.data.resp.resourceresp.institution;

import lombok.Data;

/**
 * @Author: Yulin Dai
 * @CreateTime: 2023-05-10 14:57
 */
@Data
public class InsLocInfoByPage {
    private String provinceName;
    private int provinceTotalCount;
    private int provinceIncrCount;
}
