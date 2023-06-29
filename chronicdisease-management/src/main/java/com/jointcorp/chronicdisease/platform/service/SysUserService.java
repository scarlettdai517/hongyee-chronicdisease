package com.jointcorp.chronicdisease.platform.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jointcorp.chronicdisease.data.SysUserBaseInfo;
import com.jointcorp.chronicdisease.data.consts.Consts;
import com.jointcorp.chronicdisease.data.po.*;
import com.jointcorp.chronicdisease.data.req.sysUserReq.*;
import com.jointcorp.chronicdisease.data.resp.SysUserAccountManageResp;
import com.jointcorp.chronicdisease.data.resp.SysUserLoginResp;
import com.jointcorp.chronicdisease.data.resp.resourceresp.ResourceRespConvert;
import com.jointcorp.chronicdisease.data.resp.resourceresp.institution.SubInstitution;
import com.jointcorp.chronicdisease.platform.base.Msg;
import com.jointcorp.chronicdisease.platform.cache.UserCache;
import com.jointcorp.chronicdisease.platform.interceptor.support.UserTokenUtil;
import com.jointcorp.chronicdisease.platform.mapper.*;
import com.jointcorp.chronicdisease.platform.utils.MsgConverts;
import com.jointcorp.parent.result.ResultData;
import com.jointcorp.parent.result.ResultUtil;
import com.jointcorp.parent.utils.UUIDUtil;
import com.jointcorp.support.vo.PageData;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SysUserService {

    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private ResourceMapper resourceMapper ;
    @Autowired
    private SysUserTokenMapper sysUserTokenMapper;
    @Autowired
    private UserCache sysUserCache;
    @Autowired
    private UserCache userCache;
    @Autowired
    private InstitutionMapper institutionMapper;
    @Autowired
    private PartnerMapper partnerMapper;

    /**
     * 用户登陆
     * @param req
     * @return
     */
    public ResultData login(SysUserLoginReq req) {
        SysUser sysUser = sysUserMapper.login(req.getPhoneNumber(), req.getCountryCode(), req.getEmail(), req.getPassword());
        if (sysUser == null) {
            return MsgConverts.businessError(Msg.ACCOUNT_PASSWD_ERROR);
        }
        String userType = sysUser.getUserType();
        Integer accountStatus = sysUser.getAccountStatus();
        //如果是超管，或账号未冻结才允许登录
        if ("admin".equals(userType) || accountStatus == 1) {
            String userId = String.valueOf(sysUser.getUserId());
            String corporateId = String.valueOf(sysUser.getCorporateId());
            String email = req.getEmail();
            String phoneNumber = req.getPhoneNumber();
            //sysUserMapper.loginTimeUpdate(sysUser.getUserId());
            String token = UUIDUtil.createUUID();
            //保存和缓存用户基本信息
            saveAndCacheSysUserBaseInfo(token, sysUser);
            //查询和缓存资源信息
            List<Resource> resources = queryAndCacheResource(userId);
            List<Resource> allResources = resourceMapper.selectAll();
            List<Object> resourceList = ResourceRespConvert.resourceList(resources, allResources);

            //设置默认logo
            String picLogo = Consts.DEFAULTLOGO;
            //分用户类型返回 token，资源，用户类型
            Partner partner = partnerMapper.selectByPrimaryKey(corporateId);
            Institution institution = institutionMapper.selectByPrimaryKey(corporateId);
            //如果登录用户是合作商且有自己的logo
            if (partner != null && partner.getPicLogo() != null) {
                picLogo = partner.getPicLogo();
            }
            //如果登录用户是机构且有自己的logo
            if (institution != null && institution.getPicLogo() != null) {
                picLogo = institution.getPicLogo();
            }
            //如果是超管，或合作商/机构没有自己的logo，直接用默认logo
            SysUserLoginResp sysUserLoginResp = new SysUserLoginResp(email,phoneNumber,picLogo, sysUser.getUsername(), userId, token, sysUser.getUserType(), corporateId, resourceList);
            return ResultUtil.success(sysUserLoginResp);
        }else{
            return MsgConverts.OptFail("账号已被冻结");
        }
    }

    public ResultData checkResourceList(String userId){
        //获取权限
        SysUserBaseInfo baseUser = UserTokenUtil.getUser();
        String userType = baseUser.getUserType();
        long baseUserId = baseUser.getUserId();
        List<SubInstitution> subInstitutions = userCache.getSubInstitution(baseUserId, userType, baseUser.getCorporateId());
        List<Long> validIds = subInstitutions.stream().map(SubInstitution::getInstitutionId).collect(Collectors.toList());
        List<Long> validUserIdList = sysUserMapper.selectUserIdList(validIds);
        if (("admin").equals(userType)){
            validUserIdList = sysUserMapper.selectAllUserId();
        }
        if(("partner").equals(userType)){
            validUserIdList.add(baseUserId);
        }
        if (validUserIdList.contains(Long.valueOf(userId))) {
            List<Resource> resources = queryAndCacheResource(userId);
            List<Resource> allResources = resourceMapper.selectAll();
            List<Object> resourceList = ResourceRespConvert.resourceList(resources, allResources);
            return ResultUtil.success(resourceList);
        }else{
            return MsgConverts.OptFail("您无权操作");
        }
    }

    private void saveAndCacheSysUserBaseInfo(String token,SysUser sysUser) {
        long userId = sysUser.getUserId();
        //1.查询用户下的机构和子机构
        List<Institution> institutionList = userCache.queryAllSubInst(sysUser.getUserId(),sysUser.getUserType(),sysUser.getCorporateId());
        userCache.cacheSubInst(SubInstitution.convert(institutionList),sysUser.getCorporateId());
        //2.先查询该id有没有对应的token，如果存在就删除cache中对应的token
        SysUserToken userToken = sysUserTokenMapper.selectByPrimaryKey(userId);
        if(userToken != null) {
            //删除缓存
            sysUserCache.delToken(userToken.getTokenVal());
        }
        //3.生成token，缓存token和基础用户信息,添加到数据库
        sysUserCache.cacheToken(token, SysUserBaseInfo.convert(sysUser));
        //4.存数据库
        SysUserToken sysUserToken = new SysUserToken();
        sysUserToken.setCreateTime(LocalDateTime.now());
        sysUserToken.setTokenVal(token);
        sysUserToken.setUserId(userId);
        sysUserToken.setExpireTime( LocalDateTime.of(LocalDate.now(), LocalTime.MAX));
        sysUserTokenMapper.insertToken(sysUserToken);
    }

    private List<Resource> queryAndCacheResource(String userId) {
        //放进资源列表信息
        //按照返回信息新建资源列表
        //将从数据库中的到的资源列表，转化成返回需要的资源列表类型
        List<Resource> resources = resourceMapper.selectResource(userId);
        //缓存权限信息
        sysUserCache.cacheResources(userId, resources);
        return resources;
    }


    /**
     * 修改用户资源权限
     * @param req
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ResultData changeAuthority(ResourceChangeReq req){
        //1. 数据库里删除原来的权限资源
        String userId = req.getUserId();
        resourceMapper.deleteResources(userId);
        //2. 数据库里增加新的权限资源
        resourceMapper.insertResources(req.getUserId(),req.getResourceIds());

        //3.更新缓存 key-value
        List<Long> resIds = req.getResourceIds().stream().map(Long::parseLong).collect(Collectors.toList());
        List<Resource> newResourceList = resourceMapper.selectResourceById(resIds);
        sysUserCache.cacheResources(userId, newResourceList);
        return ResultUtil.success();
    }

    //查询所有用户
    public ResultData query(){
        List<SysUser> sysUsers = sysUserMapper.selectAll();
        return ResultUtil.success(sysUsers);
    }

    //更新用户信息
    public ResultData update(SysUserAddReq req){
        SysUser sysUser = req.convert();

        sysUser.setUpdateTime(LocalDateTime.now());
        sysUserMapper.updateByPrimaryKeySelective(sysUser);
        return ResultUtil.success();
    }

    //超级管理员修改其他账号的账号本号（用来登录的邮箱号或手机号）
    public ResultData adminUpdateAccount(AdminUpdateAccountReq req){
        SysUserBaseInfo baseUser = UserTokenUtil.getUser();
        String userType = baseUser.getUserType();
        //权限判断
        if("admin".equals(userType)) {
            Long userId = Long.valueOf(req.getUserId());
            updateAcc(userId,req.getEmail(),req.getPhoneNumber(),req.getCountryCode());
            return ResultUtil.success(sysUserMapper.selectByPrimaryKey(userId));
        }else{
            return MsgConverts.OptFail("您无权操作");
        }
    }

    //自己修改自己账号的：账号本号（用来登录的邮箱号或手机号）
    public ResultData updateAccount(UpdateAccountReq req){
        SysUserBaseInfo baseUser = UserTokenUtil.getUser();
        Long userId = baseUser.getUserId();
        updateAcc(userId,req.getEmail(),req.getPhoneNumber(),req.getCountryCode());
        return ResultUtil.success(sysUserMapper.selectByPrimaryKey(userId));

    }

    //修改账号的【公共方法】
    public void updateAcc(Long userId, String email,String phoneNumber,String countryCode){
        SysUser sysUser = sysUserMapper.selectByPrimaryKey(userId);
        if (StringUtils.isBlank(email)){
            email = sysUser.getEmail();
        }
        if (StringUtils.isBlank(phoneNumber)){
            phoneNumber = sysUser.getPhoneNumber();
        }
        if (StringUtils.isBlank(countryCode)){
            countryCode = sysUser.getCountryCode();
        }
        sysUserMapper.updateAccount(userId,email,phoneNumber,countryCode);
    }


    //超级管理员修改其他账号的密码
    public ResultData adminUpdatePassword(AdminUpdatePasswordReq req){
        SysUserBaseInfo baseUser = UserTokenUtil.getUser();
        String userType = baseUser.getUserType();
        //权限判断
        if("admin".equals(userType)) {
            String updatedUserId = req.getUpdatedUserId();
            String newPsw = req.getNewPsw();
            sysUserMapper.updatePasswordInt(Long.valueOf(updatedUserId), newPsw);
            return ResultUtil.success("密码修改成功");
        }else{
            return MsgConverts.OptFail("您无权操作");
        }
    }

    //自己修改自己账号的密码
    public ResultData updatePassword(UpdatePasswordReq updatePasswordReq){
        SysUserBaseInfo baseUser = UserTokenUtil.getUser();
        Long userId = baseUser.getUserId();
        String oldPsw = updatePasswordReq.getOldPsw();
        String newPsw = updatePasswordReq.getNewPsw();
        String password = sysUserMapper.selectPswById(userId);

        if (!(oldPsw.equals(password))){
            return ResultUtil.argumentNotValid("旧密码输入错误");
        }else{
            sysUserMapper.updatePasswordInt(userId,newPsw);
            return ResultUtil.success("密码修改成功");
        }
    }

    //登出账号
    public ResultData logout(String userToken){
        long userId = UserTokenUtil.getUser().getUserId();
        //从缓存中删除token
        userCache.delToken(userToken);
        //数据库删除token
        sysUserMapper.deleteTokenByUserIdInt(userId);

        return ResultUtil.success();
    }

    //删除用户
    public ResultData delete(SysUserDelReq req){
        sysUserMapper.deleteByPrimaryKey(req.getUserId());
        return ResultUtil.success();
    }

    //返回登录用户权限下的用户列表
    public ResultData manageAccount(ManageAccountReq req) {
        int page = req.getPage();
        int limit = req.getLimit();
        String accountStatus = req.getAccountStatus();
        String account = req.getAccount();

        SysUserBaseInfo baseUser = UserTokenUtil.getUser();
        List<SysUser> userList = null;
        if(Consts.USER_TYPE_ADMIN.equals(baseUser.getUserType())) {
            //超级管理员可以搜索所有的用户信息
            PageHelper.startPage(page, limit);
            userList = sysUserMapper.selectLikeAllUser(account,accountStatus);
        } else {
            List<Institution> institutionList = userCache.queryAllSubInst(baseUser.getUserId(), baseUser.getUserType(), baseUser.getCorporateId());
            //机构Id，一个用户就对应一个机构id
            List<Long> validIds = institutionList.stream().map(Institution::getInstitutionId).collect(Collectors.toList());
            PageHelper.startPage(page, limit);
            userList = sysUserMapper.selectLikeAllUserByInts(account,accountStatus,validIds);
        }
        List<SysUserAccountManageResp> sysUserAccountManageResps = new ArrayList<>();
        PageInfo<SysUser> pageInfo = new PageInfo<>(userList);
        //转换
        for (SysUser user: userList ){
            SysUserAccountManageResp sysUserAccountManageResp = SysUserAccountManageResp.convert(user);
            sysUserAccountManageResps.add(sysUserAccountManageResp);
        }
        return ResultUtil.success(new PageData(sysUserAccountManageResps, pageInfo.getTotal(), page, pageInfo.getPages()));



/*
        int page = req.getPage();
        int limit = req.getLimit();
        String accountStatus = req.getAccountStatus();
        String email = req.getEmail();
        String phoneNumber = req.getPhoneNumber();

        Example example = new Example(SysUser.class);
        Example.Criteria c = example.createCriteria();

        //根据权限筛选要展示的用户id列表
        SysUserBaseInfo baseUser = UserTokenUtil.getUser();
        Long userId = baseUser.getUserId();
        SysUser sysUser = sysUserMapper.selectByPrimaryKey(userId);
        String userType = sysUser.getUserType();
        List<SysUserAccountManageResp> sysUserAccountManageResps = new ArrayList<>();
        List<Institution> institutionList = userCache.queryAllSubInst(sysUser.getUserId(), sysUser.getUserType(), sysUser.getCorporateId());
        List<Long> validIds = institutionList.stream().map(Institution::getInstitutionId).collect(Collectors.toList());
        List<Long> userIdList = sysUserMapper.selectUserIdList(validIds);
        if (("admin").equals(userType)){
            userIdList = sysUserMapper.selectAllUserId();
        }
        if(("partner").equals(userType)){
            userIdList.add(userId);
        }

        //前端传入的账号不分手机或邮箱，会同时传入这两个字段，需通过查找判断是邮箱还是手机，确定最终筛选条件
        //1）如果邮箱中查不到，筛选条件按电话号码查找
        if (StringUtils.isNotBlank(email)) {
            Example example1 = new Example(SysUser.class);
            Example.Criteria c1 = example1.createCriteria();
            c1.andLike("email", "%" + email + "%");
            c1.andIn("userId", userIdList);
            List<SysUser> userList = sysUserMapper.selectByExample(example1);
            if (userList.size() == 0){
                c.andLike("phoneNumber", "%" + phoneNumber + "%");
            }else{
                c.andLike("email", "%" + email + "%");
            }
        }

        //2）如果电话号码查不到，筛选条件按邮箱查找
        if (StringUtils.isNotBlank(phoneNumber)) {
            Example example2 = new Example(SysUser.class);
            Example.Criteria c2 = example2.createCriteria();
            c2.andLike("phoneNumber", "%" + phoneNumber + "%");
            c2.andIn("userId", userIdList);
            List<SysUser> userList = sysUserMapper.selectByExample(example2);
            if (userList.size() == 0){
                c.andLike("email", "%" + email + "%");
            }else{
                c.andLike("phoneNumber", "%" + phoneNumber + "%");
            }
        }
        //3)申请状态
        if (StringUtils.isNotBlank(accountStatus)) {
            c.andEqualTo("accountStatus", accountStatus);
        }

        c.andIn("userId", userIdList);
        PageHelper.startPage(page, limit);
        List<SysUser> userList = sysUserMapper.selectByExample(example);
        PageInfo<SysUser> pageInfo = new PageInfo<>(userList);
        //转换
        for (SysUser user: userList ){
            SysUserAccountManageResp sysUserAccountManageResp = SysUserAccountManageResp.convert(user);
            sysUserAccountManageResps.add(sysUserAccountManageResp);
        }
        return ResultUtil.success(new PageData(sysUserAccountManageResps, pageInfo.getTotal(), page, pageInfo.getPages()));

     */
    }


    //超管冻结账号
    public ResultData changeStatusToLock(String userId){
        SysUserBaseInfo baseUser = UserTokenUtil.getUser();
        String userType = baseUser.getUserType();
        if ("admin".equals(userType)) {
            sysUserMapper.updateAccountStatusInt(userId,Consts.ACCOUNT_STATUS_LOCK);
            return ResultUtil.success("账号已冻结");
        }else{
            return MsgConverts.OptFail("您无权操作");
        }
    }
    //超管解冻账号
    public ResultData changeStatusToNormal(String userId){
        SysUserBaseInfo baseUser = UserTokenUtil.getUser();
        String userType = baseUser.getUserType();
        if ("admin".equals(userType)) {
            sysUserMapper.updateAccountStatusInt(userId,Consts.ACCOUNT_STATUS_NORMAL);
            return ResultUtil.success("账号已解冻");
        }else{
            return MsgConverts.OptFail("您无权操作");
        }
    }
}
