package com.jointcorp.chronicdisease.platform.service;

import com.alibaba.excel.EasyExcel;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jointcorp.chronicdisease.data.SysUserBaseInfo;
import com.jointcorp.chronicdisease.data.consts.Consts;
import com.jointcorp.chronicdisease.data.po.InstitutionMember;
import com.jointcorp.chronicdisease.data.po.Member;
import com.jointcorp.chronicdisease.data.po.MemberDevice;
import com.jointcorp.chronicdisease.data.req.deviceReq.ChangeMemberReq;
import com.jointcorp.chronicdisease.data.req.memberReq.MemberAddReq;
import com.jointcorp.chronicdisease.data.req.memberReq.MemberImportReq;
import com.jointcorp.chronicdisease.data.req.memberReq.MemberQueryReq;
import com.jointcorp.chronicdisease.data.req.memberReq.MemberUpdateReq;
import com.jointcorp.chronicdisease.data.resp.memberResp.MemberQueryResp;
import com.jointcorp.chronicdisease.data.resp.deviceresp.ExcelImportResp;
import com.jointcorp.chronicdisease.data.resp.resourceresp.institution.SubInstitution;
import com.jointcorp.chronicdisease.platform.cache.UserCache;
import com.jointcorp.chronicdisease.platform.interceptor.support.UserTokenUtil;
import com.jointcorp.chronicdisease.platform.mapper.*;
import com.jointcorp.chronicdisease.platform.utils.ExcelUtil;
import com.jointcorp.common.util.SnowflakeIdWorker;
import com.jointcorp.parent.result.ResultCode;
import com.jointcorp.parent.result.ResultData;
import com.jointcorp.parent.result.ResultUtil;
import com.jointcorp.support.vo.PageData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.burningwave.core.assembler.StaticComponentContainer;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author zHuH1
 * @Date 2023/5/26 9:31
 **/
@Slf4j
@Service
public class MemberService {
    @Autowired
    private DeviceService deviceService;
    @Autowired
    private SnowflakeIdWorker snowflakeIdWorker;
    @Autowired
    private MemberDeviceService memberDeviceService;
    @Autowired
    private InstitutionMemberService institutionMemberService;
    @Autowired
    private DeviceMapper deviceMapper;
    @Autowired
    private MemberMapper memberMapper;
    @Autowired
    private InstitutionMemberMapper institutionMemberMapper;
    @Autowired
    private InstitutionMapper institutionMapper;
    @Autowired
    private MemberDeviceMapper memberDeviceMapper;
    @Autowired
    private UserCache userCache;


    /**
     * 查询会员列表
     * @param req
     * @return
     */
    public PageData queryMember(MemberQueryReq req) {
        PageHelper.startPage(req.getPage(), req.getSize());
        // 查询会员信息
        List<MemberQueryResp> respList = queryMemberList(req);
        PageInfo<MemberQueryResp> pageInfo = new PageInfo<>(respList);
        log.info("查询会员信息成功,总条数:{}", pageInfo.getTotal());
        PageData pageData = new PageData(respList, pageInfo.getTotal(), req.getPage(), pageInfo.getPages());
        return pageData;
    }

    /**
     * 查询会员列表
     * @param req
     * @return
     */
    public List<MemberQueryResp> queryMemberList(MemberQueryReq req) {
        log.info("查询会员信息,参数:{}", req);
        // 获取机构列表
        List<Long> institutionIdList;
        if (req.getInstitutionId() != null) {
            institutionIdList = userCache.getSubInstIdList(0L, Consts.USER_TYPE_INS, req.getInstitutionId());
        } else {
            SysUserBaseInfo sysUser = UserTokenUtil.getUser();
            institutionIdList = userCache.getSubInstIdList(sysUser.getUserId(), sysUser.getUserType(), sysUser.getCorporateId());
        }
//        List<Long> memberIdList = institutionMemberService.queryInstitutionMemberList(institutionIdList);

        // 查询会员信息
        List<MemberQueryResp> respList = memberMapper.queryMemberInfo(req, institutionIdList);
        return respList;
    }


    // TODO 会员输入地址时，自定义输入还是限制使用系统定义好的地址
    /**
     * 添加会员
     * @param req
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ResultData addMember(MemberAddReq req) {
        // 添加会员数据
        Member member = new Member();
        BeanUtils.copyProperties(req, member);
        member.setMemberId(snowflakeIdWorker.nextId());
        member.setCreateTime(LocalDateTime.now());
        member.setUpdateTime(LocalDateTime.now());
        log.info("新增会员:{}", member);
        memberMapper.insert(member);

        // 保存机构与会员绑定记录
        institutionMemberService.insertMechanismUser(member.getMemberId(), req.getInstitutionId());

        // 如果会员绑定了设备,保存会员与设备绑定记录
        if (StringUtils.isNotBlank(req.getDeviceIdent())) {
            ChangeMemberReq changeMemberReq = new ChangeMemberReq();
            changeMemberReq.setMemberId(member.getMemberId());
            changeMemberReq.setMemberName(member.getMemberName());
            changeMemberReq.setDeviceIdent(req.getDeviceIdent());
            // 保存会员与设备的绑定记录
            memberDeviceService.insertMemberDeviceRecord(changeMemberReq);
        }
        return ResultUtil.success("添加成功");
    }

    /**
     * 更新会员
     * @param req
     * @return
     */
    public ResultData updateMember(MemberUpdateReq req) {
        Member member = memberMapper.selectByPrimaryKey(req.getMemberId());
        if (member == null) {
             return ResultUtil.build(ResultCode.ERROR,"会员不存在!");
        }
        member.setMemberName(req.getMemberName());
        member.setBirthday(req.getBirthday());
        member.setGender(req.getGender());
        member.setPhone(req.getPhone());
        member.setRemark(req.getRemark());
        member.setAddress(req.getAddress());
        member.setIdCard(req.getIdCard());
        memberMapper.updateByPrimaryKeySelective(member);
        return ResultUtil.success("更新成功!");
    }



    /**
     * 会员数据导出
     * @param req
     * @param response
     */
    public void exportExcel(MemberQueryReq req, HttpServletResponse response) {
        StaticComponentContainer.Modules.exportAllToAll();
        try {
            List<MemberQueryResp> list = queryMemberList(req);
            log.info("导出设备数据:{}", list);
            ExcelUtil.writeExcel(response, list, "会员数据", "会员表", MemberQueryResp.class);
        } catch (Exception e) {
            log.error("会员数据导出失败:{}", e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 下载导入模板
     * @param response
     */
    public void downloadTemplate(HttpServletResponse response) {
        log.info("下载会员数据导入模板");
        String fileName = "会员导入模板";
        String sheetName = "会员导入模板";

        // 获取当前用户下的机构列表
        SysUserBaseInfo sysUser = UserTokenUtil.getUser();
        List<Long> institutionIdList = userCache.getSubInstIdList(sysUser.getUserId(), sysUser.getUserType(), sysUser.getCorporateId());
        // 拼接机构名称与id
        String[] institutionNameWithId = institutionMapper.selectInstIdAndName(institutionIdList).stream()
                .map(i -> {
                    String str = i.getInstitutionName() + "[" + i.getInstitutionId() + "]" ;
                    return str;
                }).toArray(String[]::new);

        Map<Integer, String[]> comboBoxMap = new HashMap<>();
        comboBoxMap.put(4, institutionNameWithId);
        try {
            ExcelUtil.writeExcel(response, null, fileName, sheetName, MemberImportReq.class, comboBoxMap);
        } catch (Exception e) {
            log.info("导入模板下载失败:{}", e.getMessage());
        }
    }

    /**
     * 导入会员
     * @param file
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ExcelImportResp importMember(MultipartFile file) throws Exception {
        log.info("excel批量导入会员");
        StaticComponentContainer.Modules.exportAllToAll();
        // 检查文件格式
        if (!ExcelUtil.checkExcelFile(file)) {
            throw new RuntimeException("文件格式错误!");
        }
        // 读取数据
        List<MemberImportReq> excelList = null;
        try {
            excelList = EasyExcel.read(new BufferedInputStream(file.getInputStream())).head(MemberImportReq.class)
                    .sheet().doReadSync();
        } catch (Exception e) {
            log.error("excel数据读取失败");
            e.printStackTrace();
        }
        // 检查是否大于500条
        if (excelList.size() > 500) {
            throw new RuntimeException("超过最大处理条数");
        }

        //获取当前用户下的机构id列表
        SysUserBaseInfo sysUser = UserTokenUtil.getUser();
        List<Long> instIdList = userCache.getSubInstitution(sysUser.getUserId(), sysUser.getUserType(),
                sysUser.getCorporateId()).stream().map(SubInstitution::getInstitutionId).collect(Collectors.toList());
        // 获取当前机构下的设备列表
        List<String> deviceIdentList = deviceService.getDeviceIdentsByInstitution(sysUser.getCorporateId());

        // 记录正确和错误的行数
        int correctCount = 0;
        int errorCount = 0;
        HashSet<String> identSet = new HashSet<>();
        StringBuilder errorInfo = new StringBuilder();
        List<Member> memberList = new ArrayList<>();
        List<InstitutionMember> institutionMemberList = new ArrayList<>();
        List<MemberDevice> memberDeviceList = new ArrayList<>();
        // 过滤不符合条件的数据
        for(int i = 0; i < excelList.size(); i++) {
            MemberImportReq req = excelList.get(i);
            if (StringUtils.isBlank(req.getMemberName())) {
                errorCount++;
                errorInfo.append("第" + (i+1) + "行会员名称为空;");
                continue;
            }
            if (StringUtils.isBlank(req.getGender())) {
                errorCount++;
                errorInfo.append("第" + (i+1) + "行性别为空;");
                continue;
            }
            if (StringUtils.isBlank(req.getBirthday())) {
                errorCount++;
                errorInfo.append("第" + (i+1) + "行生日为空;");
                continue;
            }
            if (StringUtils.isBlank(req.getPhone())) {
                errorCount++;
                errorInfo.append("第" + (i+1) + "行手机号码为空;");
                continue;
            }
            if (StringUtils.isBlank(req.getInstitution())) {
                errorCount++;
                errorInfo.append("第" + (i+1) + "行机构为空;");
                continue;
            }
            if (StringUtils.isNotBlank(req.getDeviceIdent())) {
                if (!deviceIdentList.contains(req.getDeviceIdent())) {
                    errorCount++;
                    errorInfo.append("第" + (i+1) + "行设备不属于当前用户;");
                    continue;
                }
                // 判断excel数据中是否存在相同的设备地址
                if (!identSet.add(req.getDeviceIdent())) {
                    errorCount++;
                    errorInfo.append("第" + (i+1) + "行设备地址重复上传;");
                    continue;
                }
            }

            // 处理机构数据
            String institution = req.getInstitution();
            int preIndex = institution.indexOf("[");
            int sufIndex = institution.indexOf("]");
            String institutionId = institution.substring(preIndex+1, sufIndex);
            if (!instIdList.contains(Long.valueOf(institutionId))) {
                errorCount++;
                errorInfo.append("第" + (i+1) + "行机构不属于当前用户;");
                continue;
            }

            // 保存会员数据
            Member member = new Member();
            BeanUtils.copyProperties(req, member);
            member.setMemberId(snowflakeIdWorker.nextId());
            member.setCreateTime(LocalDateTime.now());
            member.setUpdateTime(LocalDateTime.now());
            memberList.add(member);

            // 保存机构与会员绑定数据
            InstitutionMember institutionMember = new InstitutionMember();
            institutionMember.setInstitutionMemberId(snowflakeIdWorker.nextId());
            institutionMember.setInstitutionId(Long.valueOf(institutionId));
            institutionMember.setMemberId(member.getMemberId());
            institutionMember.setCreateTime(LocalDateTime.now());
            institutionMember.setUpdateTime(LocalDateTime.now());
            institutionMemberList.add(institutionMember);

            // 如果设备地址不为空，保存用户与设备绑定记录
            if (StringUtils.isNotBlank(req.getDeviceIdent())) {
                MemberDevice memberDevice = new MemberDevice();
                memberDevice.setMemberDeviceId(snowflakeIdWorker.nextId());
                memberDevice.setMemberId(member.getMemberId());
                memberDevice.setMemberName(member.getMemberName());
                memberDevice.setDeviceIdent(req.getDeviceIdent());
                memberDevice.setStartTime(LocalDateTime.now());
                memberDevice.setEndTime(null);
                memberDevice.setCreateTime(LocalDateTime.now());
                memberDevice.setUpdateTime(LocalDateTime.now());
                memberDeviceList.add(memberDevice);
            }
            // 记录正确的数据条数
            correctCount++;
        }

        // 批量插入会员数据
        if (!CollectionUtils.isEmpty(memberList)) {
            log.info("批量插入会员数据:{}", memberList);
            memberMapper.insertMemberList(memberList);
        }
        // 批量插入机构与会员绑定记录
        if (!CollectionUtils.isEmpty(institutionMemberList)) {
            log.info("批量插入机构与会员绑定记录:{}", institutionMemberList);
            institutionMemberMapper.insertBatchList(institutionMemberList);
        }
        // 批量插入会员与设备绑定记录
        if (!CollectionUtils.isEmpty(memberDeviceList)) {
            log.info("批量插入会员与设备绑定记录:{}", memberDeviceList);
            memberDeviceService.batchInsertList(memberDeviceList);
        }
        ExcelImportResp resp = new ExcelImportResp();
        resp.setErrorInfo(errorInfo.toString());
        resp.setCorrectCount(correctCount);
        resp.setErrorCount(errorCount);
        log.info("会员批量导入结果:{}", resp);
        return resp;
    }

}
