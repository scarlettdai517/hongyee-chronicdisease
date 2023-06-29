package com.jointcorp.chronicdisease.data.resp.resourceresp.institution;

import lombok.Data;

import java.time.LocalDate;

/**
 * @Author: Yulin Dai
 * @CreateTime: 2023-05-06 16:34
 */
//每日新增机构数类
@Data
public class InsNumIncr {

    private Integer increment;
    private LocalDate createDay;
}
