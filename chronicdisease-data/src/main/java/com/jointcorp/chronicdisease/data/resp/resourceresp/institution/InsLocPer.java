package com.jointcorp.chronicdisease.data.resp.resourceresp.institution;

import lombok.Data;

/**
 * @Author: Yulin Dai
 * @CreateTime: 2023-05-10 14:26
 */
@Data
public class InsLocPer {
    private int provinceCount;
    private String provinceName;
    private String per = "0%";

}
