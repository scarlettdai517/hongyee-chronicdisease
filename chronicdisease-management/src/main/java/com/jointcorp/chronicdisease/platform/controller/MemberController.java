package com.jointcorp.chronicdisease.platform.controller;

import com.jointcorp.chronicdisease.data.req.memberReq.MemberAddReq;
import com.jointcorp.chronicdisease.data.req.memberReq.MemberQueryReq;
import com.jointcorp.chronicdisease.data.req.memberReq.MemberUpdateReq;
import com.jointcorp.chronicdisease.platform.service.MemberService;
import com.jointcorp.parent.result.ResultData;
import com.jointcorp.parent.result.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * @Author zHuH1
 * @Date 2023/5/26 9:10
 **/
@RestController
@RequestMapping("/member")
public class MemberController {

    @Autowired
    private MemberService memberService;

    /**
     * 查询会员列表
     * @param req
     * @return
     */
    @PostMapping("/query")
    public ResultData queryMember(@RequestBody MemberQueryReq req) {
        return ResultUtil.success(memberService.queryMember(req));
    }

    /**
     * 添加会员
     * @param req
     * @return
     */
    @PostMapping("/add")
    public ResultData addMember(@RequestBody @Valid MemberAddReq req) {
        return memberService.addMember(req);
    }

    /**
     * 更新会员
     * @param req
     * @return
     */
    @PostMapping("/update")
    public ResultData updateMember(@RequestBody @Valid MemberUpdateReq req) {
        return memberService.updateMember(req);
    }

    /**
     * 会员数据导出
     * @param req
     * @param response
     */
    @PostMapping("/exportMember")
    public void exportExcel(@RequestBody MemberQueryReq req, HttpServletResponse response) {
        memberService.exportExcel(req, response);
    }

    /**
     * 下载导入模板
     */
    @GetMapping("/downloadTemplate")
    public void downloadTemplate(HttpServletResponse response) {
        memberService.downloadTemplate(response);
    }

    /**
     * 导入会员
     */
    @PostMapping("/importMember")
    public ResultData importMember(MultipartFile file) throws Exception {
        return ResultUtil.success(memberService.importMember(file));
    }

}
