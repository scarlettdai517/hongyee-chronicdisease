package com.jointcorp.chronicdisease.data.req.deviceReq;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 设备更换使用者请求体
 * @Author zHuH1
 * @Date 2023/5/19 15:38
 **/
@Data
public class ChangeMemberReq {

    // 设备地址
    @NotBlank(message = "设备地址不能为空")
    private String deviceIdent;

    // 会员id
    @NotNull(message = "会员id不能为空")
    private Long memberId;

    // 会员名称
    @NotBlank(message = "会员名称不能为空")
    private String memberName;

}
