package com.jointcorp.chronicdisease.data.req.institutionReq;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * @Author: Yulin Dai
 * @CreateTime: 2023-05-08 16:30
 */
@Data
public class ValidInsReq {

    private LocalDate monday;
    private LocalDate sunday;
    private List<Long> validIds;
}
