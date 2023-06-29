package com.jointcorp.chronicdisease.data.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * 资源
 */
@Data
@Table(name = "resource")
public class Resource {

    @Id
    @Column(name = "resource_id")
    private Long resourceId;

    @Column(name = "create_time")
    private LocalDateTime createTime;// 创建时间

    @Column(name = "update_time")
    private LocalDateTime updateTime;// 修改时间


    //上级菜单id
    @Column(name = "parent_id")
    private Long parentId;

    // 菜单路径，用于跳转
    private String path;

    // 菜单 name，用于界面 keep-alive 路由缓存。
    private String name;

    // 组件路径
    private String component;

    // 菜单标题
    private String title;

    // 菜单图标
    private String icon;

    private String redirect;

    //菜单还是按钮
    @Column(name = "resource_type")
    private String resourceType;

    // 菜单是否缓存,默认 false
    private boolean keepAlive;
    //菜单是否固定，默认值false
    private boolean affix;
    // 是否内嵌 ,默认false
    private boolean iframe;

    //是否公共的资源，每个用户都有的，true 有，
    private boolean pub;
}
