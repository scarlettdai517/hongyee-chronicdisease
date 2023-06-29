package com.jointcorp.chronicdisease.data;

import com.jointcorp.chronicdisease.data.po.SysUser;
import lombok.Data;

/**
 * 缓存系统用户基础信息
 * @Author: Xu-xg
 * @CreateTime: 2023-05-06 15:33
 */
@Data
public class SysUserBaseInfo {

    private long userId;
    private String userType;
    private long corporateId;

    public static SysUserBaseInfo convert(SysUser sysUser) {
        SysUserBaseInfo baseInfo = new SysUserBaseInfo();
        baseInfo.setCorporateId(sysUser.getCorporateId());
        baseInfo.setUserId(sysUser.getUserId());
        baseInfo.setUserType(sysUser.getUserType());
        return baseInfo;
    }
}