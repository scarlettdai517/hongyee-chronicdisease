package com.jointcorp.chronicdisease.data.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * 合作商用户
 */
@Data
@Table(name = "partner")
public class Partner {
    //合作商ID
    @Id
    @Column(name = "partner_id")
    private Long partnerId;

    //合作商名称-也是账号名
    @Column(name = "partner_name")
    private String partnerName;

    //合作商logo
    @Column(name = "pic_logo")
    private String picLogo;

    // 合作商联系人
    @Column(name = "contactor")
    private String contactor;

    //合作商联系人电话
    @Column(name = "contact_phone_number")
    private String contactPhoneNumber;

    //合作商联系人电话国家code
    @Column(name = "country_code")
    private String contactorCountryCode;

    //省市区组合的地址前缀
    @Column(name = "province_name")
    private String provinceName;

    @Column(name = "city_name")
    private String cityName;

    @Column(name = "district_name")
    private String districtName;

    //详细地址
    @Column(name = "detailed_address")
    private String detailedAddress;

    @Column(name = "user_id")
    private String userId; //创建者

    @Column(name = "create_time")
    private LocalDateTime createTime;// 创建时间

    @Column(name = "update_time")
    private LocalDateTime updateTime;// 修改时间
}
