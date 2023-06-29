package com.jointcorp.chronicdisease.platform.interceptor.support;

import com.jointcorp.chronicdisease.data.SysUserBaseInfo;

/**
 * @author Xu-xg
 * @date 2020/9/29 10:03
 */
public class UserTokenUtil {

    /**
     * 保存变量的ThreadLocal，保持在同一线程中同步数据.同步用户id
     */
    private static final ThreadLocal<SysUserBaseInfo> USERID_THREADLOCAL = new ThreadLocal<>();

    private UserTokenUtil() {
    }

    public static void bindUser(SysUserBaseInfo user) {
        USERID_THREADLOCAL.set(user);
    }

    public static SysUserBaseInfo getUser() {
        return USERID_THREADLOCAL.get();
    }

    public static void removeUser() {
        USERID_THREADLOCAL.remove();
    }


}
