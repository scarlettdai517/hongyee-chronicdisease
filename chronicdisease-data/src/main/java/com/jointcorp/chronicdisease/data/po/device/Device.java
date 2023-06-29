package com.jointcorp.chronicdisease.data.po.device;

import com.jointcorp.chronicdisease.data.annotation.DeviceDict;
import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.time.LocalDateTime;

/**
 * 设备
 *
 * @Author zHuH1
 * @Date 2023/5/5 14:16
 **/
@Data
@Table(name = "device")
public class Device {

    // 设备id
    @Id
    private Long deviceId;

    // 设备类型
    @DeviceDict(dictName = "DEVICE_TYPE", dictField = "deviceTypeCodeText")
    private String deviceTypeCode;

    // 设备类型对应的值
    @Transient
    private String deviceTypeCodeText;

    // 设备型号
    @DeviceDict(dictName = "DEVICE_MODEL", dictField = "deviceModelKeyText")
    private Integer deviceModelKey;

    // 设备型号对应的值
    @Transient
    private String deviceModelKeyText;

    // 设备地址（mac\imei）
    private String deviceIdent;

    // 设备名称
    private String deviceName;

    // 激活状态 true是 false否
    private Boolean activeState;

    // 在线状态 true是 false否
    private Boolean onlineState;

    // 删除状态 是ture  否false
    private Boolean deletedState;

    // 创建时间
    private LocalDateTime createTime;

    // 修改时间
    private LocalDateTime updateTime;
}
