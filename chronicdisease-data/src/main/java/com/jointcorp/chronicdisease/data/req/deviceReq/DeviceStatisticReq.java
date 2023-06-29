package com.jointcorp.chronicdisease.data.req.deviceReq;

import com.jointcorp.chronicdisease.data.po.PageRequest;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * @Author zHuH1
 * @Date 2023/5/12 14:05
 **/
@Data
public class DeviceStatisticReq extends PageRequest {

    private Long institutionId;

    private String deviceTypeCode;

    @NotNull(message = "日期不能为空")
    private String selectDate;

    private Integer type;
}
