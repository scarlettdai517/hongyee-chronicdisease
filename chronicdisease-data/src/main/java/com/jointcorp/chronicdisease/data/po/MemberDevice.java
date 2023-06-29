package com.jointcorp.chronicdisease.data.po;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * 会员设备记录表
 * @Author zHuH1
 * @Date 2023/5/19 15:19
 **/
@Data
@Table(name = "member_device")
public class MemberDevice {

    // 主键id
    @Id
    private Long memberDeviceId;

    // 会员id
    private Long memberId;

    // 会员名称
    private String memberName;

    // 设备地址
    private String deviceIdent;

    // 绑定开始时间
    private LocalDateTime startTime;

    // 绑定结束时间
    private LocalDateTime endTime;

    // 创建时间
    private LocalDateTime createTime;

    // 更新时间
    private LocalDateTime updateTime;

}
