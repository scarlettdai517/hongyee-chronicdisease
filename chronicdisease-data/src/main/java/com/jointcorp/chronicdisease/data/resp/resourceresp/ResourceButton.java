package com.jointcorp.chronicdisease.data.resp.resourceresp;

import lombok.Data;

/**
 * @Author: Xu-xg
 * @CreateTime: 2023-06-12 11:06
 */
@Data
public class ResourceButton {

    private String resourceId;
    private String parentId;
    private String name;
    private boolean show;
}
