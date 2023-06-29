package com.jointcorp.chronicdisease.data.resp.resourceresp;

import lombok.Data;

import static com.jointcorp.chronicdisease.data.consts.Consts.COMPONENT;

/**
 * @Author: Yulin Dai
 * @CreateTime: 2023-04-24 09:01
 * 后端响应给前端的资源json数据
 */
@Data
public class UpperMenu {

    private String path;
    private String resourceId;
    private String name;
    private String component = COMPONENT;
    private boolean show;
    private Meta meta;

}
