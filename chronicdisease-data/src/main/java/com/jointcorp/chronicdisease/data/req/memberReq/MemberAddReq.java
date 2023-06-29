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
public class MemberAddReq {

    //会员名称
    @NotBlank(message = "用户名不能为空")
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

    //机构id
    @NotNull(message = "机构id不能为空")
    private Long institutionId;

    //设备地址
    private String deviceIdent;

    //地址
    private String address;

    //身份证
    private String idCard;

    //备注
    private String remark;

}
