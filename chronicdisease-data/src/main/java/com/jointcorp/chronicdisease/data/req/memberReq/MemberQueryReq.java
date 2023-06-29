package com.jointcorp.chronicdisease.data.req.memberReq;

import com.jointcorp.chronicdisease.data.po.PageRequest;
import lombok.Data;

/**
 * 会员查询请求体
 * @Author zHuH1
 * @Date 2023/5/31 9:23
 **/
@Data
public class MemberQueryReq extends PageRequest {

    // 会员id
    private Long memberId;

    // 手机号
    private String phone;

    // 开始日期
    private String startTime;

    // 结束日期
    private String endTime;

    // 机构id
    private Long institutionId;

}
