package com.jointcorp.chronicdisease.data.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.time.Period;

/**
 * 登陆用户token
 */
@Data
@Table(name = "sys_user_token")
public class SysUserToken {
    @Id
    @Column(name = "user_id")
    private Long userId;// 用户id（主键）

    @Column(name = "token_val")
    private String tokenVal;//token值

    @Column(name = "create_time")
    private LocalDateTime createTime;// 生成时间

    @Column(name = "expire_time")
    private LocalDateTime expireTime;// 过期时间
    //
    //@Column(name = "valid_period")
    //private Period validPeriod;//有效时长
}
