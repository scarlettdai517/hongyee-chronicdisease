package com.jointcorp.chronicdisease.data.resp.resourceresp;

import com.jointcorp.chronicdisease.data.po.Partner;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Author: Yulin Dai
 * @CreateTime: 2023-06-12 11:08
 */
@Data
public class PartnerDetailResp {

    //合作商ID
    private String partnerId;

    //合作商名称-也是账号名
    private String partnerName;

    //合作商logo
    private String picLogo;

    // 合作商联系人
    private String contactor;

    //合作商联系人电话
    private String contactPhoneNumber;

    //合作商联系人电话国家code
    private String contactorCountryCode;

    //省市区组合的地址前缀
    private String provinceName;

    private String cityName;

    private String districtName;

    private String fullAddress;

    //详细地址
    private String detailedAddress;

    // 创建时间
    private LocalDateTime createTime;

    public static PartnerDetailResp convert(Partner partner){
        PartnerDetailResp partnerDetailResp = new PartnerDetailResp();
        partnerDetailResp.setPartnerId(partner.getPartnerId().toString());
        partnerDetailResp.setPartnerName(partner.getPartnerName());
        partnerDetailResp.setPicLogo(partner.getPicLogo());
        partnerDetailResp.setContactor(partner.getContactor());
        partnerDetailResp.setContactPhoneNumber(partner.getContactPhoneNumber());
        partnerDetailResp.setContactorCountryCode(partner.getContactorCountryCode());
        partnerDetailResp.setProvinceName(partner.getProvinceName());
        partnerDetailResp.setCityName(partner.getCityName());
        partnerDetailResp.setDetailedAddress(partner.getDetailedAddress());
        partnerDetailResp.setDistrictName(partner.getDistrictName());
        partnerDetailResp.setFullAddress(fullAddress(partner));
        partnerDetailResp.setCreateTime(partner.getCreateTime());
        return partnerDetailResp;
    }


    public static String fullAddress(Partner partner){
        String provinceName = partner.getProvinceName();
        String cityName = partner.getCityName();
        String districtName = partner.getDistrictName();
        String detailedAddress = partner.getDetailedAddress();
        return provinceName + cityName + districtName + detailedAddress;
    }

}
