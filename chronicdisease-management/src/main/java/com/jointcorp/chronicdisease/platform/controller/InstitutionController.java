package com.jointcorp.chronicdisease.platform.controller;

import com.jointcorp.chronicdisease.data.po.Institution;
import com.jointcorp.chronicdisease.data.req.institutionReq.*;
import com.jointcorp.chronicdisease.data.req.partnerReq.PartnerAddReq;
import com.jointcorp.chronicdisease.platform.service.InstitutionService;
import com.jointcorp.chronicdisease.platform.service.PartnerService;
import com.jointcorp.chronicdisease.platform.utils.MsgConverts;
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

import java.time.LocalDate;
import java.util.Map;

/**
 * 机构
 */
@RestController
@RequestMapping("/institution")
public class InstitutionController {

    private Logger logger = LoggerFactory.getLogger(InstitutionController.class);

    @Autowired
    private RequestContext requestContext;

    @Autowired
    private InstitutionService institutionService;



    //新增机构
    @PostMapping("/addInstitution")
    public ResultData addInstitution(@RequestBody @Validated InstitutionAddReq req) throws Exception {
        requestContext.setRequestBody(req);
        logger.info(JsonUtil.objectToJson(req));

        if (StringUtils.isBlank(req.getEmail()) && StringUtils.isBlank(req.getAccCountryCode()) && StringUtils.isBlank(req.getPhoneNumber())) {
            return ResultUtil.argumentNotValid("邮箱和手机号码不能都为空");
        }
        return institutionService.add(req);
    }
    //多条件机构列表分页查询
    @GetMapping("/queryInstitutionByPage")
    public ResultData queryInstitutionByPage(@Validated InstitutionQueryReq req) throws Exception{
        logger.info(JsonUtil.objectToJson(req));
        if (req.getLastCreateTime() != null && req.getCreateTime() == null) {
            return ResultUtil.argumentNotValid("请选择起始创建时间");
        }
        return institutionService.select(req);
    }

    @GetMapping("/showInsDetail")
    public ResultData showInsDetail(String institutionId) throws Exception{
        return institutionService.showInsDetail(institutionId);
    }

    //超级管理员修改其他机构的，以及机构修改自己的：（联系人和地址等）
    @PostMapping("/updateInsOtherInfo")
    public ResultData updateInsOtherInfo(@RequestBody @Validated InstitutionUpdateReq institutionUpdateReq){

        return institutionService.updateInsOtherInfo(institutionUpdateReq);
    }


    //根据选定的日期（天）查询权限下的机构信息（包括当天以及所在周的机构总数，新增数，以及日同比） queryInstitutionBoardByDate
    @GetMapping("/queryInstitutionBoardByDate")
    public ResultData queryInstitutionBoardByDate(LocalDate localDate) throws Exception{
        return institutionService.institutionBoardByDate(localDate);
    }


    //权限下的机构信息（机构地区分布） queryInstitutionLocationInfo
    @GetMapping("/queryInstitutionLocationInfo")
    public ResultData queryInstitutionLocationInfo() throws Exception{
        return institutionService.institutionLocationInfo();
    }

    //权限下的机构信息（机构地区分页） queryInstitutionLocationByPage
    @GetMapping ("/queryInstitutionLocationByPage")
    public ResultData queryInstitutionLocationByPage(InsLocByPageReq insLocByPageReq) throws Exception{
        return institutionService.institutionLocationByPage(insLocByPageReq);
    }


    //根据选定的日期（天）查询权限下的机构信息（包括当天以及所在月的机构总数，新增数，以及日同比） queryInstitutionBoardByMonth
    @GetMapping("/queryInstitutionBoardByMonth")
    public ResultData queryInstitutionBoardByMonth(LocalDate localDate) throws Exception{
        return institutionService.institutionBoardByMonth(localDate);
    }

    //超管删除二级机构
    @GetMapping("/deleteIns")
    public ResultData deleteIns(String institutionId) throws Exception{
        return institutionService.deleteIns(institutionId);
    }

    /**
     * 保存推送地址，如果已存在就是更新
     * @param req
     * @return
     * @throws Exception
     */
    @PostMapping("/savePushUrl")
    public ResultData savePushUrl(@Validated @RequestBody DataPushUrlConfigReq req) throws Exception {
        if(!"0".equals(req.getParentId())) {
            return MsgConverts.OptFail("只有一级机构才能配置推送地址");
        }
        return institutionService.savePushUrl(req);
    }

    /**
     * 删除推送地址  key -> institutionId , 机构id
     * @param map
     * @return
     */
    @PostMapping("/delPushUrl")
    public ResultData delPushUrl(@RequestBody Map<String,String> map) {
        return institutionService.delPushUrl(map.get("institutionId"));
    }

    @GetMapping("/queryPushUrl")
    public ResultData queryPushUrl(PushUrlQuery req) throws Exception {
        return institutionService.queryPushUrl(req);
    }
}
