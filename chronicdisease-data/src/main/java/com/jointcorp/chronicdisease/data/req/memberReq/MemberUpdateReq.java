package com.jointcorp.chronicdisease.data.req.memberReq;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 添加会员请求类
 * @Author zHuH1
 * @Date 2023/5/26 09:30
 **/
@Data
public class MemberUpdateReq {

    @NotNull(message = "会员id不能为空")
    private Long memberId;

    //用户名
    @NotBlank(message = "会员名称不能为空")
    private String memberName;

    //性别  male男  female女
    @NotBlank(message = "性别不能为空")
    private String gender;

    //生日
    @NotBlank(message = "生日不能为空")
    private String birthday;

    //手机号码
    @NotBlank(message = "手机号码不能为空")
    private String phone;

    //地址
    private String address;

    //身份证
    private String idCard;

    // 备注
    private String remark;

}
