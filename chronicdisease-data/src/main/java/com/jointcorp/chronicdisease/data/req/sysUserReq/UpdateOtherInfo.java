package com.jointcorp.chronicdisease.data.req.sysUserReq;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import java.time.LocalDateTime;

/**
 * @Author: Yulin Dai
 * @CreateTime: 2023-05-22 16:30
 */
@Data
public class UpdateOtherInfo {

    private String picLogo;

    private String name;

    private String contactor;

    private String contactPhoneNumber;

    private String contactorCountryCode;

    private String provinceName;

    private String cityName;

    private String districtName;

    private String detailedAddress;

    private String userId;

}
