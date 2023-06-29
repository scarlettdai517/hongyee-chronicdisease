package com.jointcorp.chronicdisease.data.resp.resourceresp.institution;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jointcorp.chronicdisease.data.po.Institution;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Author: Yulin Dai
 * @CreateTime: 2023-05-05 10:00
 */
@Data
public class InstitutionByPageResp {
    private String institutionId;
    private String parentId;
    private String partnerId;
    private String picLogo;
    private String institutionName;
    private String fullAddress;
    private String contactor;
    private String contactPhoneNumber;
    private String contactorCountryCode;
    private String provinceName;
    private String cityName;
    private String districtName;
    private String detailedAddress;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;// 创建时间


    public static InstitutionByPageResp convert(Institution institution){

        InstitutionByPageResp institutionByPageResp = new InstitutionByPageResp();
        institutionByPageResp.setInstitutionId(institution.getInstitutionId().toString());
        institutionByPageResp.setParentId(institution.getParentId().toString());
        institutionByPageResp.setPartnerId(institution.getPartnerId().toString());
        institutionByPageResp.setPicLogo(institution.getPicLogo());
        institutionByPageResp.setInstitutionName(institution.getInstitutionName());
        institutionByPageResp.setFullAddress(institutionByPageResp.fullAddress(institution));
        institutionByPageResp.setContactor(institution.getContactor());
        institutionByPageResp.setContactPhoneNumber(institution.getContactPhoneNumber());
        institutionByPageResp.setContactorCountryCode(institution.getContactorCountryCode());
        institutionByPageResp.setProvinceName(institution.getProvinceName());
        institutionByPageResp.setCityName(institution.getCityName());
        institutionByPageResp.setDistrictName(institution.getDistrictName());
        institutionByPageResp.setDetailedAddress(institution.getDetailedAddress());
        institutionByPageResp.setCreateTime(institution.getCreateTime());

        return institutionByPageResp;
    }

    public String fullAddress(Institution institution){
        String provinceName = institution.getProvinceName();
        String cityName = institution.getCityName();
        String districtName = institution.getDistrictName();
        String detailedAddress = institution.getDetailedAddress();
        return provinceName + cityName + districtName + detailedAddress;
    }

}
