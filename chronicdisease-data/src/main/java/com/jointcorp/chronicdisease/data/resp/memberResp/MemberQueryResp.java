package com.jointcorp.chronicdisease.data.resp.memberResp;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @Author zHuH1
 * @Date 2023/5/31 16:27
 **/
@Data
public class MemberQueryResp {

    //会员id
    @ExcelProperty("会员id")
    private String memberId;

    //会员名称
    @ExcelProperty("会员名称")
    private String memberName;

    //性别  male男  female女
    @ExcelProperty("性别")
    private String gender;

    //生日
    @ExcelProperty("生日")
    private String birthday;

    //手机号码
    @ExcelProperty("手机号码")
    private String phone;

    //地址
    @ExcelProperty("地址")
    private String address;

    //身份证
    @ExcelProperty("身份证")
    private String idCard;

    //机构id
    @ExcelProperty("机构id")
    private String institutionId;

    //机构名称
    @ExcelProperty("机构名称")
    private String institutionName;

    //绑定设备数量
    @ExcelProperty("绑定设备数量")
    private String deviceCount;

    // 注册时间
    @ExcelProperty("注册时间")
    private String registrationTime;

    // 备注
    @ExcelProperty("备注")
    private String remark;

}
