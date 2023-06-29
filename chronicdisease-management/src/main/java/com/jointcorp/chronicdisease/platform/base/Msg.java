package com.jointcorp.chronicdisease.platform.base;

/**
 * 返回状态
 *
 * @author Xiao
 * @create 2017-05-17 14:07
 **/
public enum Msg {

    ACCOUNT_PASSWD_ERROR(1013,"账号或密码错误"),
    OPENID_ILLEGAL(1250,"获取openId失败"),
    CPID_INVALID(1510," 无效的cpid"),
    USER_TOKEN_ILLEGAL(1500,"UserToken已失效"),
    ;

    private int code;
    private String info;

    Msg(int code, String info) {
        this.code = code;
        this.info = info;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
