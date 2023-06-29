package com.jointcorp.chronicdisease.platform.controller;

import com.jointcorp.chronicdisease.data.req.deviceReq.*;
import com.jointcorp.chronicdisease.platform.service.DeviceService;
import com.jointcorp.chronicdisease.platform.service.DeviceStatisticsService;
import com.jointcorp.parent.result.ResultData;
import com.jointcorp.parent.result.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.ExecutionException;

/**
 * @Author zHuH1
 * @Date 2023/5/5 13:57
 **/
@RestController
@RequestMapping("/device")
public class DeviceController {

    @Autowired
    private DeviceService deviceService;
    @Autowired
    private DeviceStatisticsService deviceStatisticsService;

    /**
     * 查询设备列表
     * @param req
     * @return
     */
    @PostMapping("/query")
    public ResultData query(@RequestBody DeviceQueryReq req) {
        return ResultUtil.success(deviceService.query(req));
    }

    /**
     * 添加设备
     * @param req
     * @return
     */
    @PostMapping("/add")
    public ResultData addDevice(@RequestBody @Validated DeviceAddReq req) {
        return deviceService.addDevice(req);
    }

    /**
     * 更新设备
     * @param req
     * @return
     */
    @PostMapping("/update")
    public ResultData updateDevice(@RequestBody @Validated DeviceUpdateReq req) {
        return deviceService.updateDevice(req);
    }

    /**
     * 删除设备
     * @return
     */
    @PostMapping("/delete")
    public ResultData deleteDevice(@RequestParam Long deviceId) {
        return deviceService.deleteDevice(deviceId);
    }

    /**
     * 统计选定日期的设备数据
     * @param req
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @PostMapping("/queryDeviceBoardByDate")
    public ResultData queryDeviceBoardByDate(@RequestBody @Validated DeviceStatisticReq req) throws ExecutionException, InterruptedException {
        return ResultUtil.success(deviceStatisticsService.queryDeviceBoard(req));
    }


    /**
     * 统计各个地区的设备数量
     * @param req
     * @return
     */
    @PostMapping("/deviceRegionStatistics")
    public ResultData deviceRegionStatistics(@RequestBody @Validated DeviceStatisticReq req) {
        return ResultUtil.success(deviceStatisticsService.deviceRegionStatistics(req));
    }

    /**
     * 统计各个地区的设备数据：总数量、新增数量、在线数量、活跃数量
     * @param req
     * @return
     */
    @PostMapping("/deviceStatisticByRegion")
    public ResultData deviceStatisticByRegion(@RequestBody @Validated DeviceStatisticReq req) {
        return ResultUtil.success(deviceStatisticsService.deviceStatisticByRegion(req));
    }


    /**
     * 更换使用者
     * @param req
     * @return
     */
    @PostMapping("/changeMember")
    public ResultData changeMember(@RequestBody @Validated ChangeMemberReq req) {
        return deviceService.changeMember(req);
    }

    /**
     * excel导出
     * @param req
     */
    @PostMapping("/exportExcel")
    public void exportExcel(@RequestBody DeviceQueryReq req, HttpServletResponse response) {
        deviceService.exportExcel(req, response);
    }

    /**
     * 下载导入模板
     */
    @GetMapping("/downloadTemplate")
    public void downloadTemplate(HttpServletResponse response) {
        deviceService.downloadTemplate(response);
    }

    /**
     * 导入设备
     */
    @PostMapping("/importExcel")
    public ResultData importExcel(MultipartFile file) throws Exception {
        return ResultUtil.success(deviceService.importExcel(file));
    }



}
