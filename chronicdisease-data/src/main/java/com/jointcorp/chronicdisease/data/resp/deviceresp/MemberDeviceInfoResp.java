package com.jointcorp.chronicdisease.data.resp.deviceresp;

import lombok.Data;

/**
 * @Author zHuH1
 * @Date 2023/6/7 10:25
 **/
@Data
public class MemberDeviceInfoResp {

    // 会员id
    private String memberId;

    // 设备地址
    private String deviceIdent;

    // 设备名称
    private String deviceName;

    // 在线状态
    private String onlineState;

}
