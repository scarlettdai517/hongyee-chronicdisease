package com.jointcorp.chronicdisease.data.po.device;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @Author zHuH1
 * @Date 2023/5/6 8:49
 **/
@Data
@Table(name = "device_type")
public class DeviceType implements Serializable {

    @Id
    private Long deviceTypeId;

    // 设备类型名称
    private String deviceTypeName;

    // 设备类型标识
    private String deviceTypeCode;

    // 创建时间
    private LocalDateTime createTime;

    // 修改时间
    private LocalDateTime updateTime;

}
