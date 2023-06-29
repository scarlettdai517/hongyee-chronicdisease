package com.jointcorp.chronicdisease.data.req.partnerReq;

import com.jointcorp.chronicdisease.data.po.Institution;
import com.jointcorp.chronicdisease.data.po.Partner;
import com.jointcorp.chronicdisease.data.req.institutionReq.InstitutionUpdateReq;
import lombok.Data;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;

/**
 * @Author: Yulin Dai
 * @CreateTime: 2023-06-12 14:21
 */
@Data
public class PartnerUpdateReq {

    @NotBlank(message = "合作商ID不能为空")
    private String partnerId;

    @NotBlank(message = "合作商名称不能为空")
    private String partnerName;

    @NotBlank(message = "合作商联系人不能为空")
    private String contactor;

    @NotBlank(message = "合作商联系人电话不能为空")
    private String contactPhoneNumber;

    @NotBlank(message = "合作商联系人电话国家编号不能为空")
    private String contactorCountryCode;

    @NotBlank(message = "省名不能为空")
    private String provinceName;

    @NotBlank(message = "市名不能为空")
    private String cityName;

    @NotBlank(message = "区名不能为空")
    private String districtName;

    @NotBlank(message = "详细地址不能为空")
    private String detailedAddress;

    @NotBlank(message = "合作商logo不能为空")
    private String picLogo;

    public static Partner convert(PartnerUpdateReq partnerUpdateReq){
        Partner partner = new Partner();
        partner.setPartnerId(Long.parseLong(partnerUpdateReq.getPartnerId()));
        partner.setPartnerName(partnerUpdateReq.getPartnerName());
        partner.setPicLogo(partnerUpdateReq.getPicLogo());
        partner.setContactor(partnerUpdateReq.getContactor());
        partner.setContactPhoneNumber(partnerUpdateReq.getContactPhoneNumber());
        partner.setContactorCountryCode(partnerUpdateReq.getContactorCountryCode());
        partner.setProvinceName(partnerUpdateReq.getProvinceName());
        partner.setCityName(partnerUpdateReq.getCityName());
        partner.setDistrictName(partnerUpdateReq.getDistrictName());
        partner.setDetailedAddress(partnerUpdateReq.getDetailedAddress());
        return partner;
    }


}
