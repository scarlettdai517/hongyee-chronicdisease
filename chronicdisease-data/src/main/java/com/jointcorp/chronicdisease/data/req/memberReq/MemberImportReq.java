package com.jointcorp.chronicdisease.data.req.memberReq;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 添加会员请求类
 * @Author zHuH1
 * @Date 2023/5/26 09:30
 **/
@Data
public class MemberImportReq {

    //会员名
    @ExcelProperty("会员名")
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

    //机构id
    @ExcelProperty("机构")
    private String institution;

    //设备id
    @ExcelProperty("设备地址")
    private String deviceIdent;

    //地址
    @ExcelProperty("地址")
    private String address;

    //身份证
    @ExcelProperty("身份证")
    private String idCard;

    //备注
    @ExcelProperty("备注")
    private String remark;

}
