package com.jointcorp.chronicdisease.platform.controller;

import com.jointcorp.chronicdisease.data.po.Institution;
import com.jointcorp.chronicdisease.data.req.sysUserReq.*;
import com.jointcorp.chronicdisease.platform.service.SysUserService;
import com.jointcorp.parent.config.RequestContext;
import com.jointcorp.parent.result.ResultData;
import com.jointcorp.parent.result.ResultUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/sysUser")
public class SysUserController {

    private Logger logger = LoggerFactory.getLogger(PartnerController.class);

    @Autowired
    private RequestContext requestContext;

    @Autowired
    private SysUserService sysUserService;

//    @PostMapping("/addSysUser")
//    public ResultData addSysUser(@RequestBody @Validated SysUserAddReq req) throws Exception {
//        if (StringUtils.isBlank(req.getEmail()) && StringUtils.isBlank(req.getCountryCode()) && StringUtils.isBlank(req.getPhoneNumber())) {
//            return ResultUtil.argumentNotValid("邮箱和手机号码不能都为空");
//        }
//        return sysUserService.add(req);
//    }

    @PostMapping("/login")
    public ResultData login(@RequestBody @Validated SysUserLoginReq req) {
        return sysUserService.login(req);
    }

    @GetMapping("/checkResourceList")
    public ResultData checkResourceList(String userId){
        return sysUserService.checkResourceList(userId);
    }

    @PostMapping("/changeAuthority")
    public ResultData changeAuthority(@RequestBody @Validated ResourceChangeReq req) {
        return sysUserService.changeAuthority(req);
    }

    //自己修改自己账号的密码
    @PostMapping("/changePassword")
    public ResultData changePassword(@RequestBody @Validated UpdatePasswordReq req){
        return sysUserService.updatePassword(req);
    }

    //超级管理员修改其他账号的密码
    @PostMapping("/adminUpdatePassword")
    public ResultData adminUpdatePassword(@RequestBody @Validated AdminUpdatePasswordReq req){
        return sysUserService.adminUpdatePassword(req);
    }

    //自己修改自己账号的：账号本号（用来登录的邮箱号或手机号）
    @PostMapping("/updateAccount")
    public ResultData updateAccount(@RequestBody @Validated UpdateAccountReq req){

        if(StringUtils.isBlank(req.getEmail()) && StringUtils.isBlank(req.getCountryCode()) && StringUtils.isBlank(req.getPhoneNumber())){
            return ResultUtil.argumentNotValid("邮箱和手机号码不能都为空");
        }
        return sysUserService.updateAccount(req);
    }

    //超级管理员修改其他账号的账号本号（用来登录的邮箱号或手机号）
    @PostMapping("/adminUpdateAccount")
    public ResultData adminUpdateAccount(@RequestBody @Validated AdminUpdateAccountReq req){

        if(StringUtils.isBlank(req.getEmail()) && StringUtils.isBlank(req.getCountryCode()) && StringUtils.isBlank(req.getPhoneNumber())){
            return ResultUtil.argumentNotValid("邮箱和手机号码不能都为空");
        }
        return sysUserService.adminUpdateAccount(req);
    }


    @PostMapping("/logout")
    public ResultData logout(HttpServletRequest request) {
        //通过请求头获得token
        String userToken = request.getHeader("UserToken");
        return sysUserService.logout(userToken);
    }

    //权限用户列表
    @GetMapping("/accountManagement")
    public ResultData accountManagement(@Validated ManageAccountReq req){
        return sysUserService.manageAccount(req);
    }

    //超管冻结账号
    @GetMapping("/changeStatusToLock")
    public ResultData changeStatusToLock (String userId){
        return sysUserService.changeStatusToLock(userId);
    }
    //超管解冻账号
    @GetMapping("/changeStatusToNormal")
    public ResultData changeStatusToNormal (String userId){
        return sysUserService.changeStatusToNormal(userId);
    }




}



