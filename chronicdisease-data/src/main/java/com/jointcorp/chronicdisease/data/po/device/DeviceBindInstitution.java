package com.jointcorp.chronicdisease.data.po.device;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * @Author zHuH1
 * @Date 2023/5/9 17:12
 **/
@Data
@Table(name = "device_bind_institution")
public class DeviceBindInstitution {

    // 主键id
    @Id
    private Long id;

    // 设备地址（mac\imei）
    private String deviceIdent;

    // 机构id
    private Long institutionId;

    // 机构名称
    private String institutionName;

    // 设备型号
    private String deviceModelValue;

    // 绑定状态
    private Boolean bindingState;

    // 创建时间
    private LocalDateTime createTime;

    // 更新时间
    private LocalDateTime updateTime;

}
