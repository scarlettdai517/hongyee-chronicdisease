package com.jointcorp.chronicdisease.data.po;

import com.jointcorp.chronicdisease.data.req.institutionReq.DataPushUrlConfigReq;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 机构数据推送地址配置
 * @Author: Xu-xg
 * @CreateTime: 2023-06-15 09:41
 */
@Data
@Table(name = "inst_push_url_config")
public class DataPushUrlConfig {

    @Id
    private Long id;

    @Column(name = "institution_id")
    private Long institutionId;

    @Column(name = "parent_id")
    private Long parentId;

    //数据类别， 1：医疗数据
    @Column(name = "data_type")
    private Integer dataType;

    //推送地址
    private String url;

    private LocalDateTime created;
    private LocalDateTime updated;

    public static DataPushUrlConfig convert(DataPushUrlConfigReq req) {
        DataPushUrlConfig dataPushUrlConfig = new DataPushUrlConfig();
        dataPushUrlConfig.setInstitutionId(Long.valueOf(req.getInstitutionId()));
        dataPushUrlConfig.setParentId(Long.valueOf(req.getParentId()));
        dataPushUrlConfig.setDataType(req.getDataType());
        dataPushUrlConfig.setUrl(req.getUrl());
        dataPushUrlConfig.setUpdated(LocalDateTime.now());
        return dataPushUrlConfig;
    }
}
