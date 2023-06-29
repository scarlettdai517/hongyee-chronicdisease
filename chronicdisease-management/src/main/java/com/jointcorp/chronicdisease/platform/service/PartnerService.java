package com.jointcorp.chronicdisease.platform.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jointcorp.chronicdisease.data.SysUserBaseInfo;
import com.jointcorp.chronicdisease.data.consts.Consts;
import com.jointcorp.chronicdisease.data.po.Institution;
import com.jointcorp.chronicdisease.data.po.Partner;
import com.jointcorp.chronicdisease.data.po.Resource;
import com.jointcorp.chronicdisease.data.po.SysUser;
import com.jointcorp.chronicdisease.data.req.institutionReq.InstitutionUpdateReq;
import com.jointcorp.chronicdisease.data.req.partnerReq.PartnerAddReq;
import com.jointcorp.chronicdisease.data.req.partnerReq.PartnerUpdateReq;
import com.jointcorp.chronicdisease.data.resp.resourceresp.PartnerIdResp;
import com.jointcorp.chronicdisease.data.resp.resourceresp.PartnerDetailResp;
import com.jointcorp.chronicdisease.data.resp.resourceresp.ResourceRespConvert;
import com.jointcorp.chronicdisease.data.resp.resourceresp.institution.SubInstitution;
import com.jointcorp.chronicdisease.platform.cache.UserCache;
import com.jointcorp.chronicdisease.platform.interceptor.support.UserTokenUtil;
import com.jointcorp.chronicdisease.platform.mapper.AddressPrefixMapper;
import com.jointcorp.chronicdisease.platform.mapper.PartnerMapper;
import com.jointcorp.chronicdisease.platform.mapper.ResourceMapper;
import com.jointcorp.chronicdisease.platform.mapper.SysUserMapper;
import com.jointcorp.chronicdisease.platform.utils.MsgConverts;
import com.jointcorp.common.util.SnowflakeIdWorker;
import com.jointcorp.parent.result.ResultData;
import com.jointcorp.parent.result.ResultUtil;
import com.jointcorp.support.vo.PageData;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.Part;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PartnerService {
    @Autowired
    private PartnerMapper partnerMapper;

    @Autowired
    private ResourceMapper resourceMapper;


    @Autowired
    private SnowflakeIdWorker snowflakeIdWorker;

    @Autowired
    private SysUserMapper sysUserMapper;


    @Autowired
    private CommonService commonService;

    @Autowired
    private AddressPrefixMapper commonServiceMapper;

    @Autowired
    private UserCache userCache;

    /**
     * 添加合作商
     * @param req
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ResultData add(PartnerAddReq req) throws Exception {
        long userId = UserTokenUtil.getUser().getUserId();

        Example example = new Example(Partner.class);
        example.createCriteria().andEqualTo("partnerName",req.getPartnerName().trim());
        //select ... for update
        example.setForUpdate(true);
        Partner partner = partnerMapper.selectOneByExample(example);
        if(partner != null) {
            return MsgConverts.OptFail("合作商名字已经存在");
        }

        partner = req.convertToPartner();
        Long partnerId = snowflakeIdWorker.nextId();
        partner.setPartnerId(partnerId);
        partner.setUserId(String.valueOf(userId));

        //添加对应的系统用户
        ResultData resultData = addSysUser(req,partner);
        if(resultData != null) {
            return resultData;
        }
        return ResultUtil.success();
    }

    //添加合作商系统用户
    private ResultData addSysUser(PartnerAddReq req, Partner partner) throws Exception {

        Example example = new Example(SysUser.class);
        if(StringUtils.isNotBlank(req.getPhoneNumber())) {
            example.createCriteria().andEqualTo("phoneNumber",req.getPhoneNumber()).andEqualTo("countryCode",req.getAccCountryCode());
        } else {
            example.createCriteria().andEqualTo("email",req.getEmail().trim());
        }
        //select ... for update
        example.setForUpdate(true);
        SysUser sysUser = sysUserMapper.selectOneByExample(example);
        if(sysUser != null) {
            return MsgConverts.OptFail("系统账号已经存在");
        }
        sysUser = req.convertToSysUser();
        sysUser.setAccountStatus(Consts.ACCOUNT_STATUS_NORMAL);
        sysUser.setCreateTime(LocalDateTime.now());
        sysUser.setUpdateTime(LocalDateTime.now());
        sysUser.setUserId(snowflakeIdWorker.nextId(false));
        sysUser.setCorporateId(partner.getPartnerId());
        sysUserMapper.insert(sysUser);
        partnerMapper.insertSelective(partner);
        return null;
    }

    //系统管理员添加一级机构时，返回可供选择的合作商id
    public ResultData checkPartnerId(){
        SysUserBaseInfo baseUser = UserTokenUtil.getUser();

        if ("admin".equals(baseUser.getUserType())) {
            List<PartnerIdResp> partnerIdResps = partnerMapper.selectAllPartnerIdList();
            return ResultUtil.success(partnerIdResps);
        }else{
            return MsgConverts.OptFail("您无权操作");
        }
    }

    //返回对应id的合作商详情
    public ResultData checkPartnerDetail(String partnerId){
        //登录的账号
        SysUserBaseInfo baseUser = UserTokenUtil.getUser();
        String baseUserType = baseUser.getUserType();
        String baseUserId = String.valueOf(baseUser.getUserId());
        //要查看的账号
        Partner partner = partnerMapper.selectByPrimaryKey(partnerId);
        String parUserId = partner.getUserId();
        //只有超管，或合作商自己才能查看合作商详情
        if ("admin".equals(baseUserType) || ("partner".equals(baseUserType) && baseUserId.equals(parUserId))) {
            PartnerDetailResp partnerDetail = PartnerDetailResp.convert(partner);
            return ResultUtil.success(partnerDetail);
        }else{
            return MsgConverts.OptFail("您无权操作");
        }

    }

    //超级管理员修改其他合作商账号的，以及合作商自己修改自己账号的：其他信息（联系人和地址等）
    public ResultData updateParOtherInfo(PartnerUpdateReq req) {
        Partner newPar = PartnerUpdateReq.convert(req);
        Long partnerId = newPar.getPartnerId();
        SysUser user = sysUserMapper.selectUserByCorporateId(partnerId);
        Long userId = user.getUserId();
        Partner oldPar = partnerMapper.selectByPrimaryKey(partnerId);
        SysUserBaseInfo baseUser = UserTokenUtil.getUser();
        Long baseUserId = baseUser.getUserId();
        String userType = baseUser.getUserType();

        //如果登录的是系统管理员，或者登录的合作商修改的是自己的账号信息，则允许修改
        if ("admin".equals(userType) || ("partner".equals(userType) && baseUserId.equals(userId))){
            //更新合作商账号中的字段
            partnerMapper.updateByPrimaryKeySelective(newPar);
            //更新系统账号名
            String username = newPar.getPartnerName();
            sysUserMapper.updateUserNameByInsName(userId, username);
            return ResultUtil.success("修改成功");

        //否则没有权限
        }else{
            return MsgConverts.OptFail("您无权操作");
        }
    }
    //超管查看合作商详情列表(分页)
    public ResultData checkPartnerDetailList(int page, int limit){
        SysUserBaseInfo baseUser = UserTokenUtil.getUser();
        if ("admin".equals(baseUser.getUserType())) {
            ArrayList<PartnerDetailResp> partnerDetailListResp = new ArrayList<>();
            //分页
            PageHelper.startPage(page,limit);
            List<Partner> partnerList = partnerMapper.selectAll();
            PageInfo<Partner> pageInfo = new PageInfo<>(partnerList);
            for(Partner partner: partnerList){
                PartnerDetailResp partnerDetailResp = PartnerDetailResp.convert(partner);
                partnerDetailListResp.add(partnerDetailResp);
            }
            return ResultUtil.success(new PageData(partnerDetailListResp, pageInfo.getTotal(), page, pageInfo.getPages()));
        }else{
            return MsgConverts.OptFail("您无权操作");
        }

    }


    //更新合作商
    public ResultData update(Partner partner) {
        partnerMapper.updateByPrimaryKey(partner);
        return ResultUtil.success(partner);
    }

    //删除合作商
    public ResultData delete(Partner partner) {
        partnerMapper.delete(partner);
        return ResultUtil.success(partner);
    }

//    //查询合作商
//    //通过合作商编号，返回对象及对应资源列表
//    public ResultData query(Long partnerId) {
//        Partner partner = partnerMapper.selectPartner(partnerId);
//        PartnerQueryResp partnerQueryResp = new PartnerQueryResp();
//        partnerQueryResp.setPartner(partner);
//
//        Long userId = partner.getUserId();
//        List<ResourceResp> resourceResps = resourceMapper.selectResource(String.valueOf(userId));
//        partnerQueryResp.setPartnerResources(resourceResps);
//        return ResultUtil.success(partnerQueryResp);
//    }
}


