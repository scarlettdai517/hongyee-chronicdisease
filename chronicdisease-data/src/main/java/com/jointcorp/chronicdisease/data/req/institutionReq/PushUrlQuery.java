package com.jointcorp.chronicdisease.data.req.institutionReq;

import lombok.Data;

/**
 * 推送地址查询
 * @Author: Xu-xg
 * @CreateTime: 2023-06-19 09:26
 */
@Data
public class PushUrlQuery {

    private String device;
    private String identify;
    //机构id
    private String institutionId;
}
