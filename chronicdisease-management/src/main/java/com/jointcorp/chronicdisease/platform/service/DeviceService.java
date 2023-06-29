package com.jointcorp.chronicdisease.platform.service;

import com.alibaba.excel.EasyExcel;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jointcorp.chronicdisease.data.SysUserBaseInfo;
import com.jointcorp.chronicdisease.data.annotation.OpenDict;
import com.jointcorp.chronicdisease.data.consts.Consts;
import com.jointcorp.chronicdisease.data.po.Institution;
import com.jointcorp.chronicdisease.data.po.Member;
import com.jointcorp.chronicdisease.data.po.MemberDevice;
import com.jointcorp.chronicdisease.data.po.device.Device;
import com.jointcorp.chronicdisease.data.po.device.DeviceBindInstitution;
import com.jointcorp.chronicdisease.data.po.device.DeviceModel;
import com.jointcorp.chronicdisease.data.po.device.DeviceType;
import com.jointcorp.chronicdisease.data.req.deviceReq.*;
import com.jointcorp.chronicdisease.data.resp.deviceresp.DeviceListResp;
import com.jointcorp.chronicdisease.data.resp.deviceresp.ExcelImportResp;
import com.jointcorp.chronicdisease.data.resp.resourceresp.institution.SubInstitution;
import com.jointcorp.chronicdisease.platform.cache.DeviceCache;
import com.jointcorp.chronicdisease.platform.cache.UserCache;
import com.jointcorp.chronicdisease.platform.interceptor.support.UserTokenUtil;
import com.jointcorp.chronicdisease.platform.mapper.*;
import com.jointcorp.chronicdisease.platform.utils.ExcelUtil;
import com.jointcorp.common.util.JsonUtil;
import com.jointcorp.common.util.SnowflakeIdWorker;
import com.jointcorp.parent.result.ResultCode;
import com.jointcorp.parent.result.ResultData;
import com.jointcorp.parent.result.ResultUtil;
import com.jointcorp.redissoncache.client.RedissonCacheClient;
import com.jointcorp.support.vo.PageData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.burningwave.core.assembler.StaticComponentContainer;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author zHuH1
 * @Date 2023/5/6 17:52
 **/
@Slf4j
@Service
@EnableAspectJAutoProxy(exposeProxy = true)
public class DeviceService {

    @Autowired
    private RedissonCacheClient redissonCacheClient;
    @Autowired
    private UserCache userCache;
    @Autowired
    private DeviceCache deviceCache;
    @Autowired
    private SnowflakeIdWorker snowflakeIdWorker;

    @Autowired
    private DeviceBindingInstitutionService deviceBindingInstitutionService;
    @Autowired
    private InstitutionService institutionService;
    @Autowired
    private InstitutionMemberService institutionMemberService;
    @Autowired
    private MemberDeviceService memberDeviceService;

    @Autowired
    private DeviceMapper deviceMapper;
    @Autowired
    private DeviceBindInstitutionMapper deviceBindInstitutionMapper;
    @Autowired
    private MemberDeviceMapper memberDeviceMapper;
    @Autowired
    private MemberMapper memberMapper;
    @Autowired
    private InstitutionMapper institutionMapper;

    /**
     * 查询设备
     * @param req
     * @return
     */
    public PageData query(DeviceQueryReq req) {
        // 如果传入了机构id,则按该机构获取设备地址;未传入则按当前登录用户获取
        List<String> deviceIdents = getDeviceIdentsByInstitution(req.getInstitutionId() == null ? null : req.getInstitutionId());
        if (CollectionUtils.isEmpty(deviceIdents)) {
            return null;
        }
        PageHelper.startPage(req.getPage(), req.getSize());
        // 查询设备数据
        List<DeviceListResp> deviceListRespList = ((DeviceService) AopContext.currentProxy()).queryDeviceList(req, deviceIdents);
        // 分页
        PageInfo<DeviceListResp> pageInfo = new PageInfo<>(deviceListRespList);
        PageData pageData = new PageData(deviceListRespList, pageInfo.getTotal(), req.getPage(), pageInfo.getPages());
        return pageData;
    }


    /**
     * 获取设备数据列表
     * (此方法会拦截并对设备的类型和型号进行值转换)
     * @param req
     * @param deviceIdents
     * @return
     */
    @OpenDict
    public List<DeviceListResp> queryDeviceList(DeviceQueryReq req, List<String> deviceIdents) {
        return deviceMapper.selectDeviceList(req, deviceIdents);
    }

    /**
     * 获取机构下的所有设备
     * @return
     */
    public List<String> getDeviceIdentsByInstitution(Long instituionId) {
        SysUserBaseInfo sysUser = new SysUserBaseInfo();
        // 如果传入了机构id,则按该机构获取设备地址;未传入则按当前登录用户获取
        if (instituionId != null) {
            sysUser.setUserId(-1L);
            sysUser.setUserType(Consts.USER_TYPE_INS);
            sysUser.setCorporateId(instituionId);
        } else {
            sysUser = UserTokenUtil.getUser();
        }
        String value = redissonCacheClient.get(Consts.USER_SUBDEVICE_KEY + sysUser.getCorporateId());
        if (StringUtils.isNotBlank(value)) {
            return JsonUtil.jsonToList(value, String.class);
        } else {
            List<String> userSubDeviceIdents = getDeviceIdentsByInstitutionFromSql(sysUser.getUserId(),
                    sysUser.getUserType(), sysUser.getCorporateId());
            deviceCache.cacheUserDeviceIdents(sysUser.getCorporateId(), userSubDeviceIdents);
            return userSubDeviceIdents;
        }
    }

//    /**
//     * 获取机构下的所有设备
//     * @return
//     */
//    public List<String> getDeviceIdentsByInstitution(long userId, String userType, long corporateId) {
//        String value = redissonCacheClient.get(Consts.USER_SUBDEVICE_KEY + corporateId);
//        if (StringUtils.isNotBlank(value)) {
//            return JsonUtil.jsonToList(value, String.class);
//        } else {
//            List<String> userSubDeviceIdents = getDeviceIdentsByInstitutionFromSql(userId, userType, corporateId);
//            deviceCache.cacheUserDeviceIdents(corporateId, userSubDeviceIdents);
//            return userSubDeviceIdents;
//        }
//    }

    /**
     * 获取机构下的所有设备
     * @param userId
     * @param userType
     * @param corporateId
     * @return
     */
    public List<String> getDeviceIdentsByInstitutionFromSql(long userId,String userType,long corporateId) {
        List<Long> subInstitutionIdList = userCache.getSubInstIdList(userId, userType, corporateId);
        Example example = new Example(DeviceBindInstitution.class);
        example.createCriteria().andIn("institutionId", subInstitutionIdList).andEqualTo("bindingState", 1);
        List<DeviceBindInstitution> bindingRecordList = deviceBindInstitutionMapper.selectByExample(example);
        if (!CollectionUtils.isEmpty(bindingRecordList)) {
            return bindingRecordList.stream().map(DeviceBindInstitution::getDeviceIdent).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    /**
     * 添加设备
     * @param req
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ResultData addDevice(DeviceAddReq req) {
        int result = 0;
        if (!verifyDeviceIdentUnique(req.getDeviceIdent())) {
            return ResultUtil.build(ResultCode.ERROR, "设备地址已存在!");
        } else {
            // 新设备地址，直接存入数据库
            Device device = new Device();
            BeanUtils.copyProperties(req, device);
            device.setDeviceId(snowflakeIdWorker.nextId());
            device.setActiveState(false);
            device.setOnlineState(false);
            device.setDeletedState(false);
            device.setCreateTime(LocalDateTime.now());
            device.setUpdateTime(LocalDateTime.now());
            log.info("添加设备:{}", device);
            result = deviceMapper.insert(device);
        }
        // 如果设备绑定了机构，记录绑定数据
        if (req.getBindingState()) {
            Map<String, String> deviceModelKVMap = deviceCache.getDeviceModelKeyValue();
            String deviceModelValue = deviceModelKVMap.get(req.getDeviceModelKey() + ":" + req.getDeviceModelKey());
            deviceBindingInstitutionService.insertDeviceBindingInstitution(req.getDeviceIdent(), deviceModelValue, req.getInstitutionId());
        }
        // 分析添加结果,成功则清除redis缓存
        if (!analyzeResult(result, req.getInstitutionId())) {
            throw new RuntimeException("添加失败");
        }
        return ResultUtil.success("保存成功!");
    }

    /**
     * 更新设备
     * @param req
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ResultData updateDevice(DeviceUpdateReq req) {
        Device device = deviceMapper.selectByPrimaryKey(req.getDeviceId());
        String reqDeviceIdent = req.getDeviceIdent();
        int result;
        // 如果修改了设备地址
        if (!reqDeviceIdent.equals(device.getDeviceIdent())) {
            // 判断设备地址是否已存在
            if (!verifyDeviceIdentUnique(req.getDeviceIdent())) {
                return ResultUtil.build(ResultCode.ERROR, "设备地址已存在!");
            }
        }
        BeanUtils.copyProperties(req, device);
        device.setUpdateTime(LocalDateTime.now());
        result = deviceMapper.updateByPrimaryKeySelective(device);
        // 如果设备绑定了机构，记录绑定数据
        if (req.getBindingState()) {
            Map<String, String> deviceModelKVMap = deviceCache.getDeviceModelKeyValue();
            String deviceModelValue = deviceModelKVMap.get(req.getDeviceModelKey() + ":" + req.getDeviceModelKey());
            deviceBindingInstitutionService.insertDeviceBindingInstitution(req.getDeviceIdent(), deviceModelValue, req.getInstitutionId());
        }
        // 分析更新结果,成功则清除redis缓存
        if (!analyzeResult(result, req.getInstitutionId())) {
            throw new RuntimeException("更新失败");
        }
        return ResultUtil.success("更新成功!");
    }

    /**
     * 删除设备
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ResultData deleteDevice(Long deviceId) {
        Device device = deviceMapper.selectByPrimaryKey(deviceId);
        Example example = new Example(MemberDevice.class);
        example.createCriteria().andEqualTo("deviceIdent", device.getDeviceIdent()).andIsNull("endTime");
        if (memberDeviceMapper.selectCountByExample(example) > 0) {
            return ResultUtil.build(ResultCode.ERROR, "当前存在会员与该设备绑定,请先解除绑定!");
        }

        // 将删除状态设为true
        device.setDeletedState(true);
        device.setUpdateTime(LocalDateTime.now());
        int result = deviceMapper.updateByPrimaryKeySelective(device);

        // 通过设备与机构绑定记录，获取机构id
        DeviceBindInstitution deviceBindInstitution = deviceBindingInstitutionService.queryDeviceBindingInst(device.getDeviceIdent());
        if (deviceBindInstitution != null) {
            // 分析删除结果,成功则清除redis缓存
            if (!analyzeResult(result, deviceBindInstitution.getInstitutionId())) {
                throw new RuntimeException("删除失败");
            }
        }
        // 解绑设备与机构的绑定记录
        deviceBindingInstitutionService.unbindDeviceAndInstitution(device.getDeviceIdent());
        return ResultUtil.success("删除成功!");
    }

    /**
     * 更换使用者
     * @param req
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ResultData changeMember(ChangeMemberReq req) {
        Example example = new Example(Device.class);
        example.createCriteria().andEqualTo("deviceIdent", req.getDeviceIdent())
                .andEqualTo("deletedState", false);
        Device device = deviceMapper.selectOneByExample(example);
        if (device == null) {
            return ResultUtil.build(ResultCode.ERROR, "设备不存在!");
        }

        // 预防会员名称为空
        if (StringUtils.isBlank(req.getMemberName())) {
            Member member = memberMapper.selectByPrimaryKey(req.getMemberId());
            req.setMemberName(StringUtils.isBlank(member.getMemberName()) ? null : member.getMemberName());
        }
        // 保存会员与设备的绑定记录
        memberDeviceService.insertMemberDeviceRecord(req);

        // 根据设备与机构绑定记录,获取机构id; 将用户与机构进行绑定
        DeviceBindInstitution deviceBindInstitution = deviceBindingInstitutionService.queryDeviceBindingInst(req.getDeviceIdent());
        if (deviceBindInstitution != null && deviceBindInstitution.getInstitutionId() != null) {
            institutionMemberService.saveInstitutionMember(req.getMemberId(), deviceBindInstitution.getInstitutionId());
        }
        return ResultUtil.success("操作成功!");
    }


    /**
     * 导出设备数据
     * @param req
     * @param response
     */
    public void exportExcel(DeviceQueryReq req, HttpServletResponse response) {
        StaticComponentContainer.Modules.exportAllToAll();
        try {
            // 如果传入了机构id,则按该机构获取设备地址;未传入则按当前登录用户获取
            List<String> deviceIdents = getDeviceIdentsByInstitution(req.getInstitutionId() == null ? null : req.getInstitutionId());
            if (CollectionUtils.isEmpty(deviceIdents)) {
                return;
            }
            List<DeviceListResp> list = ((DeviceService) AopContext.currentProxy()).queryDeviceList(req, deviceIdents);
            log.info("导出设备数据:{}", list);
            ExcelUtil.writeExcel(response, list, "设备数据", "设备表", DeviceListResp.class);
        } catch (Exception e) {
            log.error("设备数据导出失败:{}", e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 下载导入模板
     * @param response
     */
    public void downloadTemplate(HttpServletResponse response) {
        String fileName = "导入设备模板";
        String sheetName = "导入设备模板";
        // 设备类型列表
        String[] deviceTypeList  = deviceCache.getAllDeviceType().stream().map(DeviceType::getDeviceTypeName)
                .toArray(String[]::new);
        // 设备型号列表
        String[] deviceModelList = deviceCache.getAllDeviceModel().stream().map(DeviceModel::getDeviceModelValue)
                .toArray(String[]::new);
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
        comboBoxMap.put(0, deviceTypeList);
        comboBoxMap.put(1, deviceModelList);
        comboBoxMap.put(4, institutionNameWithId);
        try {
            ExcelUtil.writeExcel(response, null, fileName, sheetName, DeviceImportReq.class, comboBoxMap);
        } catch (Exception e) {
            log.info("导入模板下载失败:{}", e.getMessage());
        }
    }

    /**
     * 批量插入设备数据
     * @param file
     * @return
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class)
    // TODO excel中存在错误数据时,全部不插入or插入正确的
    public ExcelImportResp importExcel(MultipartFile file) throws Exception {
        log.info("excel批量导入设备");
        StaticComponentContainer.Modules.exportAllToAll();
        // 检查文件格式
        if (!ExcelUtil.checkExcelFile(file)) {
            throw new RuntimeException("文件格式错误!");
        }
        // 读取数据
        List<DeviceImportReq> excelList = null;
        try {
            excelList = EasyExcel.read(new BufferedInputStream(file.getInputStream())).head(DeviceImportReq.class)
                    .sheet().doReadSync();
        } catch (Exception e) {
            log.error("excel数据读取失败");
            e.printStackTrace();
        }
        // 检查是否大于500条
        if (excelList.size() > 500) {
            throw new RuntimeException("超过最大处理条数");
        }

        // 全部设备类型
        Map<String, String> deviceTypeMap = deviceCache.getAllDeviceType().stream().collect(Collectors.toMap(
                DeviceType::getDeviceTypeName, DeviceType::getDeviceTypeCode));
        // 全部设备型号
        Map<String, Integer> deviceModelMap = deviceCache.getAllDeviceModel().stream().collect(Collectors.toMap(
                DeviceModel::getDeviceModelValue, DeviceModel::getDeviceModelKey));
        // "code:key" : "value" 格式的设备型号Map
        Map<String, String> deviceModelKeyValue = deviceCache.getDeviceModelKeyValue();

        List<DeviceBindInstitution> bindWithInstList = new ArrayList<>();
        List<Device> deviceList = new ArrayList<>();
        StringBuilder errorInfo = new StringBuilder();
        HashSet<String> identSet = new HashSet<>();
        // 获取当前用户下的机构列表
        SysUserBaseInfo sysUser = UserTokenUtil.getUser();
        List<Long> instIdList = userCache.getSubInstitution(sysUser.getUserId(), sysUser.getUserType(),
                sysUser.getCorporateId()).stream().map(SubInstitution::getInstitutionId).collect(Collectors.toList());

        // 记录正确和错误的行数
        int correctCount = 0;
        int errorCount = 0;
        // 过滤不符合条件的数据
        for(int i = 0; i < excelList.size(); i++) {
            DeviceImportReq req = excelList.get(i);
            if (StringUtils.isBlank(req.getDeviceTypeCode())) {
                errorCount++;
                errorInfo.append("第" + (i+1) + "行设备类型为空;");
                continue;
            }
            if (req.getDeviceModelKey() == null) {
                errorCount++;
                errorInfo.append("第" + (i+1) + "行设备型号为空;");
                continue;
            }
            if (StringUtils.isBlank(req.getDeviceIdent())) {
                errorCount++;
                errorInfo.append("第" + (i+1) + "行设备地址为空;");
                continue;
            }
            if (StringUtils.isBlank(req.getDeviceName())) {
                errorCount++;
                errorInfo.append("第" + (i+1) + "行设备名称为空;");
                continue;
            }
            // 判断设备类型与设备型号是否匹配
            String deviceTypeCode = deviceTypeMap.get(req.getDeviceTypeCode());
            Integer deviceModelKey = deviceModelMap.get(req.getDeviceModelKey());
            if (!req.getDeviceModelKey().equals(deviceModelKeyValue.get(deviceTypeCode + ":" + deviceModelKey))) {
                errorCount++;
                errorInfo.append("第" + (i+1) + "行设备类型与型号匹配错误;");
                continue;
            }
            // 判断设备地址是否存在
            if (!verifyDeviceIdentUnique(req.getDeviceIdent())) {
                errorCount++;
                errorInfo.append("第" + (i+1) + "行设备地址已存在;");
                continue;
            }
            // 判断excel数据中是否存在相同的设备地址
            if (!identSet.add(req.getDeviceIdent())) {
                errorCount++;
                errorInfo.append("第" + (i+1) + "行设备地址重复上传;");
                continue;
            }
            // 判断机构id是否是当前用户下的
            if (StringUtils.isNotBlank(req.getInstitution())) {
                // 切割机构id与名称 (格式: "机构名称[机构id]")
                String institution = req.getInstitution();
                int preIndex = institution.indexOf("[");
                int sufIndex = institution.indexOf("]");
                String institutionId = institution.substring(preIndex+1, sufIndex);
                String institutionName = institution.substring(0, preIndex);
                if (!instIdList.contains(Long.valueOf(institutionId))) {
                    errorCount++;
                    errorInfo.append("第" + (i+1) + "行机构不属于当前用户;");
                    continue;
                }
                // 保存设备与机构数据
                DeviceBindInstitution bindWithInst = new DeviceBindInstitution();
                bindWithInst.setDeviceIdent(req.getDeviceIdent());
                bindWithInst.setInstitutionId(Long.valueOf(institutionId));
                bindWithInst.setInstitutionName(institutionName);
                bindWithInstList.add(bindWithInst);
            }

            // 保存设备数据list
            Device device = new Device();
            BeanUtils.copyProperties(req, device);
            device.setDeviceId(snowflakeIdWorker.nextId());
            device.setCreateTime(LocalDateTime.now());
            device.setUpdateTime(LocalDateTime.now());
            device.setDeviceTypeCode(deviceTypeMap.get(req.getDeviceTypeCode()));
            device.setDeviceModelKey(deviceModelMap.get(req.getDeviceModelKey()));
            device.setDeletedState(false);
            device.setOnlineState(false);
            device.setActiveState(false);
            deviceList.add(device);
            // 记录正确的数据条数
            correctCount++;
        }

        // 批量插入设备数据(存入数据库)
        if (!CollectionUtils.isEmpty(deviceList)) {
            log.info("批量插入设备数据:{}", deviceList);
            int result = deviceMapper.insertDeviceList(deviceList);
            if (result > 0) {
                // 清除机构的设备缓存
                deviceCache.batchClearDeviceCache(instIdList);
            }
        }
        // 批量插入设备与机构绑定记录(存入数据库)
        if (!CollectionUtils.isEmpty(bindWithInstList)) {
            insertDeviceBindingInst(bindWithInstList, instIdList);
        }

        ExcelImportResp resp = new ExcelImportResp();
        resp.setErrorInfo(errorInfo.toString());
        resp.setCorrectCount(correctCount);
        resp.setErrorCount(errorCount);
        return resp;
    }

    public int insertDeviceBindingInst(List<DeviceBindInstitution> bindWithInstList, List<Long> instIdList) {
        Example example = new Example(Institution.class);
        example.createCriteria().andIn("institutionId", instIdList);
        List<DeviceBindInstitution> list = new ArrayList<>();
        for (DeviceBindInstitution bindWithInst : bindWithInstList) {
            bindWithInst.setId(snowflakeIdWorker.nextId());
            bindWithInst.setBindingState(true);
            bindWithInst.setCreateTime(LocalDateTime.now());
            bindWithInst.setUpdateTime(LocalDateTime.now());
            list.add(bindWithInst);
        }
        if (!CollectionUtils.isEmpty(list)) {
            log.info("批量插入设备与机构绑定记录:{}", list);
            return deviceBindInstitutionMapper.insertBatchList(list);
        }
        return 0;
    }

    /**
     * 验证设备地址是否唯一
     * @return
     */
    public Boolean verifyDeviceIdentUnique(String deviceIdent) {
        Example example = new Example(Device.class);
        Example.Criteria criteria = example.createCriteria()
                .andEqualTo("deviceIdent", deviceIdent)
                .andEqualTo("deletedState", false);
        // 已存在相同的设备地址，返回false
        if (deviceMapper.selectCountByExample(example) > 0) {
            return false;
        }
        return true;
    }

    /**
     * 分析操作结果，成功则清除缓存
     * @param result
     * @return
     */
    public Boolean analyzeResult(int result, Long institutionId) {
        if (result > 0) {
            // 清除redis缓存
            deviceCache.clearDeviceCache(institutionId);
            return true;
        } else {
            return false;
        }
    }

}
