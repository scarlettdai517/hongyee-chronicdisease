package com.jointcorp.chronicdisease.data.resp;

import lombok.Data;

import java.util.List;

@Data
public class SysUserLoginResp {
    private String email;
    private String phoneNumber;
    private String picLogo;
    private String userName;
    private String userId;
    private String token;
    private String userType;
    private String corporateId;
    private List<Object> resourceList;

    public SysUserLoginResp() {
    }

    public SysUserLoginResp(String email, String phoneNumber, String picLogo, String userName, String userId, String token, String userType, String corporateId, List<Object> resourceList) {
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.picLogo = picLogo;
        this.userName = userName;
        this.userId = userId;
        this.token = token;
        this.userType = userType;
        this.corporateId = corporateId;
        this.resourceList = resourceList;

    }
}

