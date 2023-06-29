package com.jointcorp.chronicdisease.data.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * 系统用户
 */
@Data
@Table(name = "sys_user")
public class SysUser {

    @Id
    @Column(name = "user_id")
    private Long userId;//用户ID

    private String username;//用户名

    private String password; // 密码

    private String email; // 账号（支持手机号码和邮箱）

    @Column(name = "phone_number")
    private String phoneNumber;

    //如果填手机号码时，这里填国际区号
    @Column(name = "country_code")
    private String countryCode;

    // 用户类型  合作商，机构，超级管理员
    //Institution,Partner,Admin
    @Column(name = "user_type")
    private String userType;

    //1： 正常， 2：锁定（冻结）
    @Column(name = "account_status")
    private Integer accountStatus;//账号状态（正常，锁定，等等）

    //属于哪个机构或者合作商，如果是超级管理员这里填0
    @Column(name = "corporate_id")
    private Long corporateId;

    @Column(name = "create_time")
    private LocalDateTime createTime;// 创建时间

    @Column(name = "update_time")
    private LocalDateTime updateTime;// 修改时间

    @Column(name = "last_login_time")
    private LocalDateTime lastLoginTime;
}