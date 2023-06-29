package com.jointcorp.chronicdisease.data.req.sysUserReq;

import com.jointcorp.chronicdisease.data.po.SysUser;
import com.jointcorp.support.valid.TextSize;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * 添加系统用户
 */
@Data
public class SysUserAddReq {

    @NotBlank(message = "用户名不能为空")
    @TextSize(max = 20,message = "用户名不能超过10个字符")
    private String username;//用户名

    @NotBlank(message = "密码不能为空")
    private String password; // 密码

    //账号-邮箱
    private String email; // 账号（支持手机号码和邮箱）

    //账号-手机号
    private String phoneNumber;

    //账号-国际区号
    //如果填手机号码时，这里填国际区号
    private String countryCode = "86";

    @NotBlank(message = "用户类型不能为空")
    @Pattern(regexp = "institution|partner|admin",message = "用户类型错误")
    private String userType; // 用户类型  合作商，机构，超级管理员

    //属于哪个机构或者合作商，如果是超级管理员这里填0
    @Pattern(regexp = "\\d+",message = "corporateId错误")
    private String corporateId;

    public SysUser convert() {
        SysUser sysUser = new SysUser();
        sysUser.setUsername(this.getUsername());
        sysUser.setPassword(this.getPassword());
        sysUser.setEmail(this.getEmail());
        sysUser.setPhoneNumber(this.getPhoneNumber());
        sysUser.setCountryCode(this.getCountryCode());
        sysUser.setUserType(this.getUserType());
        sysUser.setCorporateId(Long.parseLong(this.getCorporateId()));
        return sysUser;
    }


}
