package com.jointcorp.chronicdisease.data.req.partnerReq;

import com.jointcorp.chronicdisease.data.consts.Consts;
import com.jointcorp.chronicdisease.data.po.Partner;
import com.jointcorp.chronicdisease.data.po.SysUser;
import com.jointcorp.support.valid.TextSize;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class PartnerAddReq {

    //合作商名称-也是账号名
    @NotBlank(message = "合作商名称不能为空")
    @TextSize(max = 100)
    private String partnerName;

    private String picLogo = Consts.DEFAULTLOGO;

    //账号密码
    private String password; // 密码

    //登陆用的账号，邮件电话二选一
    private String email; // 账号（支持手机号码和邮箱）

    @Pattern(regexp = "\\d+",message = "手机号码错误")
    @NotBlank(message = "手机号不能为空")
    private String phoneNumber;

    //如果填手机号码时，这里填国际区号
    private String accCountryCode = "86";

    // 用户类型  合作商，机构，超级管理员
    //institution,partner,admin
    private String userType = "partner";

    // 合作商联系人
    @NotBlank(message = "合作商联系人不能为空")
    private String contactor;

    //合作商联系人电话
    @Pattern(regexp = "\\d+",message = "手机号码错误")
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



    public Partner convertToPartner() {
        Partner partner = new Partner();
        partner.setPicLogo(this.getPicLogo());
        partner.setPartnerName(this.getPartnerName());
        partner.setContactor(this.getContactor());
        partner.setContactPhoneNumber(this.getContactPhoneNumber());
        partner.setContactorCountryCode(this.getContactorCountryCode());
        partner.setProvinceName(this.getProvinceName());
        partner.setCityName(this.getCityName());
        partner.setDistrictName(this.getDistrictName());
        partner.setDetailedAddress(this.getDetailedAddress());
        return partner;

    }

    public SysUser convertToSysUser() {
        SysUser sysUser = new SysUser();
        sysUser.setUsername(this.getPartnerName());
        sysUser.setPassword(this.getPassword());
        sysUser.setEmail(this.getEmail());
        sysUser.setPhoneNumber(this.getPhoneNumber());
        sysUser.setCountryCode(this.getAccCountryCode());
        sysUser.setUserType(this.getUserType());

        return sysUser;

    }

}
