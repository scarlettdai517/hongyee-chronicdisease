package com.jointcorp.chronicdisease.data.resp.resourceresp;

import lombok.Data;

import java.util.List;

import static com.jointcorp.chronicdisease.data.consts.Consts.COMPONENT;
import static com.jointcorp.chronicdisease.data.consts.Consts.REDIRECT;

/**
 * @Author: Yulin Dai
 * @CreateTime: 2023-04-24 09:01
 * 后端响应给前端的资源json数据
 */
@Data
public class UpperMenuWithRedChild {

    private String path;
    private String resourceId;
    private String name;
    private String component = COMPONENT;
    private String redirect = REDIRECT;

    private List<ResourceButton> buttons;
    private Meta meta;
    private boolean show;

    //Object 可能是UpperMenu或者UpperMenuRed
    private List<Object> children;

}
