package com.jointcorp.chronicdisease.data.req.institutionReq;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 机构数据推送地址配置
 * @Author: Xu-xg
 * @CreateTime: 2023-06-15 09:41
 */
@Data
public class DataPushUrlConfigReq {

    @NotBlank(message = "机构ID不能为空")
    private String institutionId;

    @NotBlank(message = "父机构不能为空")
    private String parentId;

    //数据类别， 1：医疗数据
    private int dataType = 1;

    //推送地址
    @NotBlank(message = "推送地址不能为空")
    private String url;

}
