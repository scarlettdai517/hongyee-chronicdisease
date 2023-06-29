package com.jointcorp.chronicdisease.data.req.sysUserReq;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 添加系统用户
 */
@Data
public class SysUserLoginReq {

    @NotBlank(message = "密码不能为空")
    private String password; // 密码

    //账号-邮箱
    private String email; // 账号（支持手机号码和邮箱）

    //账号-手机号
    private String phoneNumber;

    //账号-国际区号
    //如果填手机号码时，这里填国际区号
    private String countryCode = "86";

}
