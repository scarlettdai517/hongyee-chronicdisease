package com.jointcorp.chronicdisease.data.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** 操作记录表
 * @Author: Xu-xg
 * @CreateTime: 2023-05-06 15:04
 */
@Data
@Table(name = "sys_operation_record")
public class OperationRecord {

    @Id
    @Column(name = "operation_id")
    private Long operationId;

    @Column(name = "user_id")
    private String userId;

    /**
     * 机构或者合作商Id,或者超管
     */
    @Column(name = "corporate_id")
    private String corporateId;

    /**
     * 操作类型
     * 1： 登陆 ， 2：修改密码，
     */
    @Column(name = "operation_type")
    private String operationType;

    @Column(name = "operation_day")
    private LocalDate operationDay;

    @Column(name = "operation_time")
    private LocalDateTime operationTime;
}
