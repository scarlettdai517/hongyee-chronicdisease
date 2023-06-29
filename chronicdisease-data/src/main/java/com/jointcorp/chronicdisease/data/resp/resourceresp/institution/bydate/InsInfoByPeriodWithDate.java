package com.jointcorp.chronicdisease.data.resp.resourceresp.institution.bydate;

import lombok.Data;

import java.time.LocalDate;

/**
 * @Author: Yulin Dai
 * @CreateTime: 2023-05-06 15:58
 */
//当周机构总数
@Data
public class InsInfoByPeriodWithDate {

    //横坐标
    private LocalDate[] days;
    //纵坐标
    private int[] nums;



}
