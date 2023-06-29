package com.jointcorp.chronicdisease.data.po.device;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @Author zHuH1
 * @Date 2023/5/6 8:48
 **/
@Data
@Table(name = "device_model")
public class DeviceModel implements Serializable {

    @Id
    private Long deviceModelId;

    // 设备类型
    private String deviceTypeCode;

    // 设备型号key
    private Integer deviceModelKey;

    // 设备型号value值
    private String deviceModelValue;

    // 设备型号value值描述
    private String deviceModelValueDesc;

    // 创建时间
    private LocalDateTime createTime;

    // 修改时间
    private LocalDateTime updateTime;

}
