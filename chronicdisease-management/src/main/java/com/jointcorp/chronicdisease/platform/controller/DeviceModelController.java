package com.jointcorp.chronicdisease.platform.controller;

import com.jointcorp.chronicdisease.data.req.deviceReq.DeviceModelAddReq;
import com.jointcorp.chronicdisease.data.req.deviceReq.DeviceModelDeleteReq;
import com.jointcorp.chronicdisease.data.req.deviceReq.DeviceModelUpdateReq;
import com.jointcorp.chronicdisease.platform.service.DeviceModelService;
import com.jointcorp.parent.result.ResultData;
import com.jointcorp.parent.result.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 设备型号controller
 *
 * @Author zHuH1
 * @Date 2023/5/5 16:36
 **/
@RestController
@RequestMapping("/device/model")
public class DeviceModelController {

    @Autowired
    private DeviceModelService deviceModelService;

    /**
     * 根据设备类型查询设备型号
     * @param deviceTypeCode
     * @return
     */
    @GetMapping("/queryByType")
    public ResultData queryByType(@RequestParam String deviceTypeCode) {
        return ResultUtil.success(deviceModelService.queryByType(deviceTypeCode));
    }

    /**
     * 添加设备型号
     * @param req
     * @return
     */
    @PostMapping("/add")
    public ResultData addDeviceModel(@RequestBody @Validated DeviceModelAddReq req) {
        return deviceModelService.addDeviceModel(req);
    }

    /**
     * 修改设备型号
     * @param req
     * @return
     */
    @PostMapping("/update")
    public ResultData updateDeviceModel(@RequestBody @Validated DeviceModelUpdateReq req) {
        return deviceModelService.updateDeviceModel(req);
    }

    /**
     * 删除设备型号
     * @param req
     * @return
     */
    @PostMapping("/delete")
    public ResultData addDeviceType(@RequestBody @Validated DeviceModelDeleteReq req) {
        return deviceModelService.deleteDeviceModel(req);
    }

}
