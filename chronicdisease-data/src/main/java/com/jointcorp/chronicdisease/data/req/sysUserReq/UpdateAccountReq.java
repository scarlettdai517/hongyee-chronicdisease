package com.jointcorp.chronicdisease.data.req.sysUserReq;

import lombok.Data;

/**
 * @Author: Yulin Dai
 * @CreateTime: 2023-05-22 15:57
 */
@Data
public class UpdateAccountReq {

    //账号-邮箱
    private String email; // 账号（支持手机号码和邮箱）

    //账号-手机号
    private String phoneNumber;

    //账号-国际区号
    //如果填手机号码时，这里填国际区号
    private String countryCode = "86";

}
