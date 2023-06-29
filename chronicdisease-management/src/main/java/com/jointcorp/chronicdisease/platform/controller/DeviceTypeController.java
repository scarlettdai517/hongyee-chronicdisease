package com.jointcorp.chronicdisease.platform.controller;

import com.jointcorp.chronicdisease.data.req.deviceReq.DeviceTypeAddReq;
import com.jointcorp.chronicdisease.data.req.deviceReq.DeviceTypeDeleteReq;
import com.jointcorp.chronicdisease.data.req.deviceReq.DeviceTypeUpdateReq;
import com.jointcorp.chronicdisease.platform.service.DeviceTypeService;
import com.jointcorp.parent.result.ResultData;
import com.jointcorp.parent.result.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 设备类型Controller
 *
 * @Author zHuH1
 * @Date 2023/5/5 16:36
 **/
@RestController
@RequestMapping("/device/type")
public class DeviceTypeController {

    @Autowired
    private DeviceTypeService deviceTypeService;

    /**
     * 查询所有设备类型
     * @return
     */
    @GetMapping("/query")
    public ResultData queryDeviceType() {
        return ResultUtil.success(deviceTypeService.queryDeviceType());
    }

    /**
     * 增加设备类型
     * @param req
     * @return
     */
    @PostMapping("/add")
    public ResultData addDeviceType(@RequestBody @Validated DeviceTypeAddReq req) {
        return deviceTypeService.addDeviceType(req);
    }

    /**
     * 修改设备类型
     * @param req
     * @return
     * @throws Exception
     */
    @PostMapping("/update")
    public ResultData updateDeviceType(@RequestBody @Validated DeviceTypeUpdateReq req) throws Exception {
        return deviceTypeService.updateDeviceType(req);
    }

    /**
     * 删除设备类型
     * @param req
     * @return
     */
    @PostMapping("/delete")
    public ResultData addDeviceType(@RequestBody @Validated DeviceTypeDeleteReq req) {
        return deviceTypeService.deleteDeviceType(req);
    }

}
