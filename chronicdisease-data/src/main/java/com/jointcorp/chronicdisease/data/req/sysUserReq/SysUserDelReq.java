package com.jointcorp.chronicdisease.data.req.sysUserReq;

import lombok.Data;

import javax.validation.constraints.Pattern;

/**
 * 添加系统用户
 */
@Data
public class SysUserDelReq {

    //属于哪个机构或者合作商，如果是超级管理员这里填0
    @Pattern(regexp = "\\d+",message = "userId错误")
    private String userId;

}
