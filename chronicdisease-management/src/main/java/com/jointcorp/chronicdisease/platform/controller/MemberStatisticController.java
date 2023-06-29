package com.jointcorp.chronicdisease.platform.controller;

import com.jointcorp.chronicdisease.data.req.deviceReq.DeviceStatisticReq;
import com.jointcorp.chronicdisease.platform.service.MemberStatisticService;
import com.jointcorp.parent.result.ResultData;
import com.jointcorp.parent.result.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

/**
 * @Author zHuH1
 * @Date 2023/6/14 19:20
 **/
@RestController
@RequestMapping("/member/statistic")
public class MemberStatisticController {

    @Autowired
    private MemberStatisticService memberStatisticService;

    /**
     * 统计选定日期当天及近七天的会员数据
     * @param req
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @PostMapping("/queryMemberBoardByDate")
    public ResultData queryMemberBoardByDate(@RequestBody @Validated DeviceStatisticReq req) throws ExecutionException, InterruptedException {
        return ResultUtil.success(memberStatisticService.queryMemberBoardByDate(req));
    }

    /**
     * 统计各个年龄段的会员数据：总人数、新增人数、在线人数、活跃人数
     * @param req
     * @return
     */
    @PostMapping("/memberStatisticByAge")
    public ResultData memberStatisticByAge(@RequestBody @Validated DeviceStatisticReq req) {
        return ResultUtil.success(memberStatisticService.memberStatisticByAge(req));
    }

}
