package com.jointcorp.chronicdisease.data.req.institutionReq;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Author: Yulin Dai
 * @CreateTime: 2023-04-28 08:43
 * 机构名，地区，时间3选n，0 <= n <= 3
 * 最后登录时间不为空时，创建时间必须不为空
 */
@Data
public class InstitutionQueryReq {
    //机构名称
    private String institutionName;

    //地区-省
    private String provinceName;

    //创建开始时间
    private LocalDateTime createTime;

    //创建结束时间
    private LocalDateTime lastCreateTime;

    //当前页
    private Integer page;
    //每一页条数
    private Integer limit = 20;

}
