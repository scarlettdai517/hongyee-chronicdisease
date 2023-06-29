package com.jointcorp.chronicdisease.data.po;

import lombok.Data;

import javax.persistence.Id;
import java.time.LocalDateTime;

/**
 * 机构会员
 *
 * @Author zHuH1
 * @Date 2023/5/19 16:02
 **/
@Data
public class InstitutionMember {

    @Id
    private Long institutionMemberId;

    /**
     * 用户id
     */
    private Long memberId;

    /**
     * 用户所绑定的机构
     */
    private Long institutionId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
