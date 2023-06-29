package com.jointcorp.chronicdisease.data.resp.resourceresp.institution.bydate;

import lombok.Data;

import java.util.List;

/**
 * @Author: Yulin Dai
 * @CreateTime: 2023-05-09 15:38
 */
@Data
public class InstitutionBoardByDateResp {

    //当天机构总数和机构新增数与同比列表
    private List<Object> insNumByDateAndPer;

    private int onlineCount = 100;

    private int activeCount = 80;

    //当周/月机构总数
    private InsInfoByPeriodWithDate insTotalByPeriodWithDate;

    //当周/月机构新增数
    private InsInfoByPeriodWithDate insIncrByPeriodWithDate;


}
