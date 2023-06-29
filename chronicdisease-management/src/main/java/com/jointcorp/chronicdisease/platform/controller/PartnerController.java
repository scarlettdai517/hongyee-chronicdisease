package com.jointcorp.chronicdisease.platform.controller;

import com.jointcorp.chronicdisease.data.req.partnerReq.PartnerAddReq;
import com.jointcorp.chronicdisease.data.req.partnerReq.PartnerUpdateReq;
import com.jointcorp.chronicdisease.data.req.sysUserReq.SysUserAddReq;
import com.jointcorp.chronicdisease.platform.service.PartnerService;
import com.jointcorp.common.util.JsonUtil;
import com.jointcorp.parent.config.RequestContext;
import com.jointcorp.parent.result.ResultData;
import com.jointcorp.parent.result.ResultUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 合作商
 */
@RestController
@RequestMapping("/partner")
public class PartnerController {

    private Logger logger = LoggerFactory.getLogger(PartnerController.class);

    @Autowired
    private RequestContext requestContext;

    @Autowired
    private PartnerService partnerService;


    //系统管理员添加一级机构时，返回可供选择的合作商id
    @GetMapping("/checkPartnerId")
    public ResultData checkPartnerId(){
        return partnerService.checkPartnerId();
    }

    //返回对应id的合作商详情
    @GetMapping("/checkPartnerDetail")
    public ResultData checkPartnerDetail(String partnerId){
        return partnerService.checkPartnerDetail(partnerId);
    }

    //超管查看合作商详情列表(分页)
    @GetMapping("/checkPartnerDetailList")
    public ResultData checkPartnerDetailList(int page, int limit){
        return partnerService.checkPartnerDetailList(page,limit);
    }

    //超级管理员修改其他合作商账号的，以及合作商自己修改自己账号的：其他信息（联系人和地址等）
    @PostMapping("/updateParOtherInfo")
    public ResultData updateParOtherInfo(@RequestBody @Validated PartnerUpdateReq partnerUpdateReq){
        return partnerService.updateParOtherInfo(partnerUpdateReq);
    }


    @PostMapping("/addPartner")
    public ResultData addPartner(@RequestBody @Validated PartnerAddReq req) throws Exception {
        requestContext.setRequestBody(req);
        logger.info(JsonUtil.objectToJson(req));
        if (StringUtils.isBlank(req.getEmail()) && StringUtils.isBlank(req.getAccCountryCode()) && StringUtils.isBlank(req.getPhoneNumber())) {
            return ResultUtil.argumentNotValid("邮箱和手机号码不能都为空");
        }
        return partnerService.add(req);

    }

}
