package com.jointcorp.chronicdisease.data.req.deviceReq;

import com.jointcorp.chronicdisease.data.po.PageRequest;
import lombok.Data;

/**
 * 设备查询请求体
 *
 * @Author zHuH1
 * @Date 2023/5/9 13:58
 **/
@Data
public class DeviceQueryReq extends PageRequest {

    // 设备id
    private Long deviceId;

    // 设备类型
    private String deviceTypeCode;

    // 设备型号Key
    private Integer deviceModelKey;

    // 设备地址（mac\imei）
    private String deviceIdent;

    // 设备名称
    private String deviceName;

    // 所属机构
    private Long institutionId;

    // 在线状态 true是 false否
    private Boolean onlineState;

    // 起始时间
    private String startTime;

    // 结束时间
    private String endTime;

}
