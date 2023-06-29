package com.jointcorp.chronicdisease.data.req.institutionReq;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jointcorp.chronicdisease.data.po.Institution;
import com.jointcorp.chronicdisease.data.resp.resourceresp.institution.InstitutionByPageResp;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

/**
 * @Author: Yulin Dai
 * @CreateTime: 2023-06-07 17:08
 */
@Data
public class InstitutionUpdateReq {
    @NotBlank(message = "机构ID不能为空")
    private String institutionId;
    @NotBlank(message = "父机构不能为空")
    private String parentId;
    @NotBlank(message = "所属合作商不能为空")
    private String partnerId;
    @NotBlank(message = "机构Logo不能为空")
    private String picLogo;
    @NotBlank(message = "机构名不能为空")
    private String institutionName;
    @NotBlank(message = "联系人不能为空")
    private String contactor;
    @NotBlank(message = "联系人电话不能为空")
    private String contactPhoneNumber;
    @NotBlank(message = "电话号码国家编号不能为空")
    private String contactorCountryCode;
    @NotBlank(message = "省不能为空")
    private String provinceName;
    @NotBlank(message = "市能为空")
    private String cityName;
    @NotBlank(message = "区不能为空")
    private String districtName;
    @NotBlank(message = "详细地址不能为空")
    private String detailedAddress;

    public static Institution convert(InstitutionUpdateReq institutionUpdateReq){
        Institution institution = new Institution();
        institution.setInstitutionId(Long.parseLong(institutionUpdateReq.getInstitutionId()));
        institution.setParentId(Long.parseLong(institutionUpdateReq.getParentId()));
        institution.setPartnerId(Long.parseLong(institutionUpdateReq.getPartnerId()));
        institution.setPicLogo(institutionUpdateReq.getPicLogo());
        institution.setInstitutionName(institutionUpdateReq.getInstitutionName());
        institution.setContactor(institutionUpdateReq.getContactor());
        institution.setContactPhoneNumber(institutionUpdateReq.getContactPhoneNumber());
        institution.setContactorCountryCode(institutionUpdateReq.getContactorCountryCode());
        institution.setProvinceName(institutionUpdateReq.getProvinceName());
        institution.setCityName(institutionUpdateReq.getCityName());
        institution.setDistrictName(institutionUpdateReq.getDistrictName());
        institution.setDetailedAddress(institutionUpdateReq.getDetailedAddress());
        return institution;
    }


}
