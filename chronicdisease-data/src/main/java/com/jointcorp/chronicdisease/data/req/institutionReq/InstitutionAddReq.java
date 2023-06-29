package com.jointcorp.chronicdisease.data.req.institutionReq;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jointcorp.chronicdisease.data.po.Institution;
import com.jointcorp.chronicdisease.data.po.SysUser;
import com.jointcorp.support.valid.TextSize;
import lombok.Data;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;

@Data
public class InstitutionAddReq {

    //合作商名称-也是账号名
    @NotBlank(message = "机构名称不能为空")
    @TextSize(max = 100)
    private String institutionName;

    @NotBlank(message = "所属机构不能为空")
    private String parentId;

    @NotBlank(message = "所属合作商不能为空")
    private String partnerId;

    //账号密码
    private String password; // 密码

    //logo
    private String picLogo;

    //登陆用的账号，邮件电话二选一
    private String email; // 账号（支持手机号码和邮箱）

    @Pattern(regexp = "\\d+", message = "手机号码错误")
    @NotBlank(message = "手机号不能为空")
    private String phoneNumber;

    //如果填手机号码时，这里填国际区号
    private String accCountryCode = "86";

    // 用户类型  合作商，机构，超级管理员
    //institution,partner,admin
    private String userType = "institution";

    // 合作商联系人
    @NotBlank(message = "合作商联系人不能为空")
    private String contactor;

    //合作商联系人电话
    @Pattern(regexp = "\\d+", message = "手机号码错误")
    @NotBlank(message = "手机号不能为空")
    private String contactPhoneNumber;

    //合作商联系人电话国家code
    private String contactorCountryCode = "86";

    //省市区组合的地址前缀
    private String provinceName;

    private String cityName;


    private String districtName;

    //详细地址
    private String detailedAddress;

    public Institution convertToInstitution() {
        Institution institution = new Institution();
        institution.setPicLogo(this.getPicLogo());
        institution.setParentId(Long.parseLong(this.getParentId()));
        institution.setInstitutionName(this.getInstitutionName());
        institution.setContactor(this.getContactor());
        institution.setContactPhoneNumber(this.getContactPhoneNumber());
        institution.setContactorCountryCode(this.getContactorCountryCode());
        institution.setProvinceName(this.getProvinceName());
        institution.setCityName(this.getCityName());
        institution.setDistrictName(this.getDistrictName());
        institution.setDetailedAddress(this.getDetailedAddress());
        return institution;

    }

    public SysUser convertToSysUser() {
        SysUser sysUser = new SysUser();
        sysUser.setUsername(this.getInstitutionName());
        sysUser.setPassword(this.getPassword());
        sysUser.setEmail(this.getEmail());
        sysUser.setPhoneNumber(this.getPhoneNumber());
        sysUser.setCountryCode(this.getAccCountryCode());
        sysUser.setUserType(this.getUserType());
        return sysUser;
    }
}



