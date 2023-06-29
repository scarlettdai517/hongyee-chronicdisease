package com.jointcorp.chronicdisease.data.po;

import lombok.Data;

import javax.persistence.Id;
import java.time.LocalDateTime;

/**
 * 会员
 * @Author zHuH1
 * @Date 2023/5/19 15:46
 **/
@Data
public class Member {

    //会员ID
    @Id
    private Long memberId;

    //会员名称
    private String memberName;

    //性别  male男  female女
    private String gender;

    //生日
    private String birthday;

    //邮箱
    private String email;

    //手机号码
    private String phone;

    //密码
    private String password;

    //体重
    private Integer weight;

    //身高
    private Integer stature;

    //第三方唯一id
    private String openId;

    //第三方平台名称
    private String platName;

    //头像
    private String avatar;

    //地址
    private String address;

    //身份证号码
    private String idCard;

    //备注
    private String remark;

    //创建时间
    private LocalDateTime createTime;

    //创建时间
    private LocalDateTime updateTime;

}