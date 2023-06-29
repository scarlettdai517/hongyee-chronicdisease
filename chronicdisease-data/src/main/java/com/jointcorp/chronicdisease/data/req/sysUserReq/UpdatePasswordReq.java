package com.jointcorp.chronicdisease.data.req.sysUserReq;

import com.jointcorp.support.valid.TextSize;
import lombok.Data;

/**
 * @Author: Yulin Dai
 * @CreateTime: 2023-05-18 15:55
 */
@Data
public class UpdatePasswordReq {
    @TextSize(min = 6,message = "密码长度至少6位")
    private String oldPsw;

    @TextSize(min = 6,message = "密码长度至少6位")
    private String newPsw;

}
