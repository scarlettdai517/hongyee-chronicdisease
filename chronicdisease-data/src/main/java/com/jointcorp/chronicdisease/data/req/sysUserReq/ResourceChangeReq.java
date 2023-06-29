package com.jointcorp.chronicdisease.data.req.sysUserReq;

import lombok.Data;

import javax.validation.constraints.Pattern;
import java.util.List;

/**
 * @Author: Yulin Dai
 * @CreateTime: 2023-04-20 14:14
 */
@Data
public class ResourceChangeReq {

    @Pattern(regexp = "\\d+",message = "用户id错误")
    private String userId;
    private List<String> resourceIds;
}

