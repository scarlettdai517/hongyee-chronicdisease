package com.jointcorp.chronicdisease.data.po;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 省市区地址
 * @Author: Yulin Dai
 * @CreateTime: 2023-04-26 17:37
 */
@Data
@Table(name = "c_areas")
public class AddressPrefix {

    @Id
    private Long id;

    private String name;

    @Column(name = "parent_id")
    private Long parentId;


}
