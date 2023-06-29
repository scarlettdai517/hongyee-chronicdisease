package com.jointcorp.chronicdisease.data.req.resourceReq;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jointcorp.chronicdisease.data.po.Resource;
import com.jointcorp.support.valid.TextSize;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @Author: Yulin Dai
 * @CreateTime: 2023-04-24 10:00
 */
@Data
public class NewResourceAddReq {

    //上级菜单id
    private String parentId;

    // 菜单路径，用于跳转
    private String path;

    // 菜单 name，用于界面 keep-alive 路由缓存。

    private String name;

    // 组件路径
    private String component;

    // 菜单标题
    @NotBlank(message = "资源名称不能为空")
    @TextSize(max = 20)
    private String title;

    // 菜单图标
    private String icon;

    private String redirect;

    //菜单还是按钮
    @NotBlank(message = "资源类型不能为空")
    @Pattern(regexp = "menu|button", message = "资源类型错误")
    private String resourceType;

    // 菜单是否缓存,默认 false
    private boolean keepAlive = false;
    //菜单是否固定，默认值false
    private boolean affix = false;
    // 是否内嵌 ,默认false
    private boolean iframe = false;

    //是否公共的资源，每个用户都有的，true 是，
    private boolean pub = false;

    public Resource convert() {
        Resource resource = new Resource();

        resource.setParentId(Long.parseLong(this.getParentId()));
        resource.setName(this.getName());
        resource.setComponent(this.getComponent());
        resource.setIcon(this.getIcon());
        resource.setRedirect(this.getRedirect());
        resource.setTitle(this.getTitle());
        resource.setResourceType(this.getResourceType());
        resource.setPath(this.getPath());
        resource.setKeepAlive(this.isKeepAlive());
        resource.setAffix(this.isAffix());
        resource.setIframe(this.isIframe());
        resource.setPub(this.isPub());
        return resource;

    }

}

