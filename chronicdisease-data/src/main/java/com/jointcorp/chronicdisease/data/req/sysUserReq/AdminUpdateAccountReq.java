package com.jointcorp.chronicdisease.data.req.sysUserReq;

import lombok.Data;

/**
 * @Author: Yulin Dai
 * @CreateTime: 2023-06-12 15:51
 */
@Data
public class AdminUpdateAccountReq {
    //账号-邮箱
    private String email; // 账号（支持手机号码和邮箱）

    //账号-手机号
    private String phoneNumber;

    //账号-国际区号
    //如果填手机号码时，这里填国际区号
    private String countryCode = "86";

    private String userId;
}
