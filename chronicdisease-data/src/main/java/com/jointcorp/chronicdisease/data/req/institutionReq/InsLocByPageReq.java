package com.jointcorp.chronicdisease.data.req.institutionReq;

import lombok.Data;

import java.time.LocalDate;

/**
 * @Author: Yulin Dai
 * @CreateTime: 2023-05-11 08:47
 */
@Data
public class InsLocByPageReq {
    private LocalDate localDate;
    //当前页
    private Integer page;
    //每一页条数
    private Integer limit = 8;
}
