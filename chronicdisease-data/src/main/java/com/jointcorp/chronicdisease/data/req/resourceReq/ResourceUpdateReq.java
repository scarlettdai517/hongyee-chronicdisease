package com.jointcorp.chronicdisease.data.req.resourceReq;

import com.jointcorp.chronicdisease.data.po.Resource;
import com.jointcorp.support.valid.TextSize;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 编辑菜单
 * @Author: Yulin Dai
 * @CreateTime: 2023-04-24 10:00
 */
@Data
public class ResourceUpdateReq {

    //上级菜单id
    private String resourceId;

    // 菜单路径，用于跳转
    private String path;

    // 菜单 name，用于界面 keep-alive 路由缓存。
    private String name;

    // 菜单标题
    @NotBlank(message = "资源名称不能为空")
    @TextSize(max = 20)
    private String title;

    //是否公共的资源，每个用户都有的，true 是，
    private boolean pub = false;

    public Resource convert() {
        Resource resource = new Resource();
        resource.setResourceId(Long.parseLong(this.getResourceId()));
        resource.setName(this.getName());
        resource.setTitle(this.getTitle());
        resource.setPath(this.getPath());
        resource.setPub(this.isPub());
        return resource;
    }

}

