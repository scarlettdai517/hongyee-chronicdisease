package com.jointcorp.chronicdisease.platform.controller;

import com.jointcorp.chronicdisease.data.req.memberReq.personalDataReq;
import com.jointcorp.chronicdisease.platform.service.MemberDeviceService;
import com.jointcorp.parent.result.ResultData;
import com.jointcorp.parent.result.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author zHuH1
 * @Date 2023/5/26 10:05
 **/
@RestController
@RequestMapping("/member/device")
public class MemberDeviceController {

    @Autowired
    private MemberDeviceService memberDeviceService;

    /**
     * 根据会员id查询会员与设备绑定数据
     * @param memberId
     * @return
     */
    @GetMapping("/query")
    public ResultData queryMemberDeviceInfo(Long memberId) {
        return ResultUtil.success(memberDeviceService.queryMemberDeviceInfo(memberId));
    }

    /**
     * 统计设备测量的各项数据
     * @param req
     * @return
     */
    @PostMapping("/personal/device/data")
    public ResultData queryPersonalDeviceData(personalDataReq req) {
        return ResultUtil.success(memberDeviceService.queryPersonalDeviceData(req));
    }
}
