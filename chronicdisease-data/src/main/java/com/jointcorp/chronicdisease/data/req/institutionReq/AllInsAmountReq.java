package com.jointcorp.chronicdisease.data.req.institutionReq;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

/**
 * 机构看板总数统计
 * @Author: Xu-xg
 * @CreateTime: 2023-05-06 14:44
 */
@Data
public class AllInsAmountReq {

    private LocalDate day;
}
