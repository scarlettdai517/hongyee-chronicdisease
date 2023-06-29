package com.jointcorp.chronicdisease.data.req.sysUserReq;

import lombok.Data;

/**
 * @Author: Yulin Dai
 * @CreateTime: 2023-06-01 10:46
 */
@Data
public class AdminUpdatePasswordReq {
    private String updatedUserId;
    private String newPsw;

}
