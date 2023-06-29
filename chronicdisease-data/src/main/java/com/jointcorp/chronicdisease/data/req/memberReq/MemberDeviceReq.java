package com.jointcorp.chronicdisease.data.req.memberReq;

import lombok.Data;

/**
 * @Author zHuH1
 * @Date 2023/5/26 10:06
 **/
@Data
public class MemberDeviceReq {

    private Long memberId;

    private String memberName;

    private String deviceIdent;

}
