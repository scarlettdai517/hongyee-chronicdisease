package com.jointcorp.chronicdisease.data.resp;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jointcorp.chronicdisease.data.po.SysUser;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Column;
import java.time.LocalDateTime;

/**
 * @Author: Yulin Dai
 * @CreateTime: 2023-05-17 15:07
 */
@Data
public class SysUserAccountManageResp {
    private String userId;
    private String userName;
    private String userType;
    private Integer accountStatus;
    private String account;
    private String accountType;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime lastLoginTime;

    public static SysUserAccountManageResp convert(SysUser sysuser){
        SysUserAccountManageResp sysUserAccountManageResp = new SysUserAccountManageResp();
        sysUserAccountManageResp.setUserId(sysuser.getUserId().toString());
        sysUserAccountManageResp.setUserName(sysuser.getUsername());
        sysUserAccountManageResp.setUserType(sysuser.getUserType());
        sysUserAccountManageResp.setAccountStatus(sysuser.getAccountStatus());
        sysUserAccountManageResp.setAccount(StringUtils.isBlank(sysuser.getPhoneNumber()) ? sysuser.getEmail() : sysuser.getPhoneNumber());
        sysUserAccountManageResp.setCreateTime(sysuser.getCreateTime());
        sysUserAccountManageResp.setLastLoginTime(sysuser.getLastLoginTime());

        if(StringUtils.isNotBlank(sysuser.getEmail())){
            sysUserAccountManageResp.setAccountType("email");
        }

        if(StringUtils.isNotBlank(sysuser.getPhoneNumber())){
            sysUserAccountManageResp.setAccountType("phoneNumber");
        }

        return sysUserAccountManageResp;
    }


}
