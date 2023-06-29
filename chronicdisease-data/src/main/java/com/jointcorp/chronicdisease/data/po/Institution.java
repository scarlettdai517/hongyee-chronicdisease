package com.jointcorp.chronicdisease.data.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * 机构
 */
@Data
@Table(name = "institution")
public class Institution {

    @Id
    @Column(name = "institution_id")
    private Long institutionId;//机构id

    //父机构id
    @Column(name = "parent_id")
    private Long parentId;

    //机构logo
    //以地址返回
    @Column(name = "pic_logo")
    private String picLogo;

    //机构名字
    @Column(name = "institution_name")
    private String institutionName;

    @Column(name = "partner_id")
    private Long partnerId;//属于哪个合作商

    // 机构联系人
    @Column(name = "contactor")
    private String contactor;

    //机构联系人电话
    @Column(name = "contact_phone_number")
    private String contactPhoneNumber;

    //机构联系人电话国家code
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

    //创建者
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;// 创建时间

    @Column(name = "update_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;// 修改时间
}
