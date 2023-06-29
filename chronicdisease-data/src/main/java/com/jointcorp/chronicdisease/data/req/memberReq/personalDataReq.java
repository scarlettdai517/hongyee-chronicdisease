package com.jointcorp.chronicdisease.data.req.memberReq;

import lombok.Data;

/**
 * @Author zHuH1
 * @Date 2023/6/7 15:39
 **/
@Data
public class personalDataReq {

    // 日期类型 1:当天 2:昨天 3:全部
    private Integer type;

    // 会员id
    private String memberId;

    // 设备地址
    private String deviceIdent;

}
