package com.jointcorp.chronicdisease.data.po;

import javax.persistence.Column;
import javax.persistence.Table;

/*
资源-系统用户（系统用户包括 合作商/机构/超级管理员类）
 */
@Table(name = "sys_user_resource")
public class SysUserResource {

    private Long id;

    @Column(name = "user_id")
    private Long userId;//企业Id

    @Column(name = "resource_id")
    private Long resourceId;// 资源id

}
