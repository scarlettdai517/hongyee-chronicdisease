package com.jointcorp.chronicdisease.data.req.sysUserReq;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @Author: Yulin Dai
 * @CreateTime: 2023-06-13 16:17
 */
@Data
public class ManageAccountReq {

    private int page;
    private int limit;
    private String account;
    private String accountStatus;

}
