package com.jointcorp.chronicdisease.platform.utils;

import com.jointcorp.chronicdisease.platform.base.Msg;
import com.jointcorp.parent.result.ResultData;
import com.jointcorp.parent.result.ResultUtil;

/**
 * @author Xu-xg
 * @date 2021/2/6 16:22
 */
public class MsgConverts {

    public static ResultData businessError(Msg msg) {
        return ResultUtil.build(msg.getCode(),msg.getInfo(),null);
    }

    public static ResultData OptFail(String msg) {
        return ResultUtil.build(2300,msg,null);
    }
}
