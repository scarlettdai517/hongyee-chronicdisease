package com.jointcorp.chronicdisease.platform.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jointcorp.chronicdisease.data.po.device.DeviceBindInstitution;
import com.jointcorp.chronicdisease.data.req.institutionReq.*;
import com.jointcorp.chronicdisease.data.resp.resourceresp.institution.*;
import com.jointcorp.chronicdisease.data.SysUserBaseInfo;
import com.jointcorp.chronicdisease.data.consts.Consts;
import com.jointcorp.chronicdisease.data.po.*;
import com.jointcorp.chronicdisease.data.resp.resourceresp.institution.bydate.InsIncrByDateAndPer;
import com.jointcorp.chronicdisease.data.resp.resourceresp.institution.bydate.InsInfoByPeriodWithDate;
import com.jointcorp.chronicdisease.data.resp.resourceresp.institution.bydate.InsTotalByDateAndPer;
import com.jointcorp.chronicdisease.data.resp.resourceresp.institution.bydate.InstitutionBoardByDateResp;
import com.jointcorp.chronicdisease.platform.cache.PushUrlCache;
import com.jointcorp.chronicdisease.platform.cache.UserCache;
import com.jointcorp.chronicdisease.platform.interceptor.support.UserTokenUtil;
import com.jointcorp.chronicdisease.platform.mapper.*;
import com.jointcorp.chronicdisease.platform.utils.MsgConverts;
import com.jointcorp.common.util.SnowflakeIdWorker;
import com.jointcorp.parent.result.ResultData;
import com.jointcorp.parent.result.ResultUtil;
import com.jointcorp.support.vo.PageData;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.text.NumberFormat;
import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class InstitutionService {


    @Autowired
    private InstitutionMapper institutionMapper;

    @Autowired
    private SnowflakeIdWorker snowflakeIdWorker;

    @Autowired
    private DataPushUrlConfigMapper pushUrlConfigMapper;

    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private UserCache userCache;
    @Autowired
    private PushUrlCache pushUrlCache;
    @Autowired
    private ResourceMapper resourceMapper;
    @Autowired
    private PartnerMapper partnerMapper;
    @Autowired
    private DeviceBindInstitutionMapper deviceBindInstitutionMapper;


    /**
     * 添加机构
     *
     * @param req
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ResultData add(InstitutionAddReq req) throws Exception {
        SysUserBaseInfo user = UserTokenUtil.getUser();
        Example example = new Example(Institution.class);
        example.createCriteria().andEqualTo("institutionName", req.getInstitutionName().trim());
        //select ... for update
        example.setForUpdate(true);
        Institution institution = institutionMapper.selectOneByExample(example);
        if (institution != null) {
            return MsgConverts.OptFail("机构名字已经存在");
        }

        institution = req.convertToInstitution();
        Long institutionId = snowflakeIdWorker.nextId();
        institution.setInstitutionId(institutionId);
        institution.setUserId(user.getUserId());

        //添加对应的系统用户
        ResultData resultData = addSysUser(req, institution);
        if (null != resultData) {
            return resultData;
        }
        //系统管理员添加一级机构
        if (Consts.USER_TYPE_ADMIN.equals(user.getUserType())){
            institution.setPartnerId(Long.parseLong(req.getPartnerId()));
        }
        //合作商添加一级机构
        else if (Consts.USER_TYPE_PARTNER.equals((user.getUserType()))){
            institution.setPartnerId(user.getCorporateId());
        } else {
            //机构添加
            Long parentId = institution.getParentId();
            Institution parentIns = institutionMapper.selectByPrimaryKey(parentId);
            institution.setPartnerId(parentIns.getPartnerId());
        }
        //清空缓存
        clearIntsCache(user, Long.parseLong(req.getParentId()));
        return ResultUtil.success();
    }

    //添加机构系统用户
    private ResultData addSysUser(InstitutionAddReq req, Institution institution) throws Exception {

        Example example = new Example(SysUser.class);
        if (StringUtils.isNotBlank(req.getPhoneNumber())) {
            example.createCriteria().andEqualTo("phoneNumber", req.getPhoneNumber()).andEqualTo("countryCode", req.getAccCountryCode());
        } else {
            example.createCriteria().andEqualTo("email", req.getEmail().trim());
        }
        //select ... for update
        example.setForUpdate(true);
        SysUser sysUser = sysUserMapper.selectOneByExample(example);
        if (sysUser != null) {
            return MsgConverts.OptFail("系统账号已经存在");
        }
        sysUser = req.convertToSysUser();
        sysUser.setAccountStatus(Consts.ACCOUNT_STATUS_NORMAL);
        sysUser.setCreateTime(LocalDateTime.now());
        sysUser.setUpdateTime(LocalDateTime.now());
        Long userId = snowflakeIdWorker.nextId(false);
        sysUser.setUserId(userId);
        sysUser.setCorporateId(institution.getInstitutionId());
        sysUserMapper.insert(sysUser);
        institutionMapper.insertSelective(institution);
        //找到父机构对应的权限，写入账号权限表
        List<Long> resourceIds = null;
        //机构的父id
        Long parentId = institution.getParentId();
        //如果父Id是0，那么该用户属于一级机构，这里先添加公共的资源给该用户
        if(parentId == 0) {
            resourceIds = resourceMapper.selectResourceByPub();
        } else {
            //用户的父id
            Long parentUserId = sysUserMapper.selectUserByCorporateId(parentId).getUserId();
            //把用户父级的资源赋给该用户
            resourceIds = resourceMapper.selectResourceId(String.valueOf(parentUserId));
        }
        List<String> list = new ArrayList<>();
        for (Long resourceId: resourceIds){
            String s = resourceId.toString();
            list.add(s);
        }
        resourceMapper.insertResources(userId.toString(),list);
        return null;
    }

    private void clearIntsCache(SysUserBaseInfo user, Long parentId) {
        /*
         * 新增机构时只需要清空上级、上级的上级机构缓存的数据，上级机构就是参数中的parentId，只需要通过parentId在继续找上级，直到parentId是0为止
         */
        List<SubInstitution> subInstitutions = userCache.getSubInstitution(user.getUserId(), user.getUserType(), user.getCorporateId());
        List<Long> ids = new ArrayList<>();
        ids = getIntsId(ids, parentId, subInstitutions);
        //添加超级管理员，超级管理员权限都一样，缓存的Key为0，所有超管共用
        ids.add(0L);
        for (Long id : ids) {
            userCache.delSubInst(id);
        }
    }

    private List<Long> getIntsId(List<Long> list, long parentId, List<SubInstitution> subInstitutions) {
        long p = parentId;
        for (SubInstitution sub : subInstitutions) {
            if (sub.getInstitutionId() == parentId) {
                list.add(sub.getInstitutionId());
                p = sub.getParentId();
                if (sub.getParentId() == 0) {
                    return list;
                }
            }
        }
        if(p == parentId) {
            return list;
        }
        return getIntsId(list, p, subInstitutions);
    }

    /**
     * 通过机构名称，地区，创建时间来查询机构
     *
     * @param req
     * @return
     */
    //多条件机构列表分页查询
    public ResultData select(InstitutionQueryReq req) throws Exception {

        String institutionName = req.getInstitutionName();
        String provinceName = req.getProvinceName();
        LocalDateTime createTime = req.getCreateTime();
        LocalDateTime lastCreateTime = req.getLastCreateTime();
        //获取所有的机构id
        SysUserBaseInfo baseUser = UserTokenUtil.getUser();
        List<SubInstitution> subInstitutions = userCache.getSubInstitution(baseUser.getUserId(), baseUser.getUserType(), baseUser.getCorporateId());
        if (CollectionUtils.isEmpty(subInstitutions)) {
            return ResultUtil.success(new PageData(new ArrayList<>(), 0, req.getPage(), 0));
        }
        List<Long> validIds = subInstitutions.stream().map(SubInstitution::getInstitutionId).collect(Collectors.toList());

        Example example = new Example(Institution.class);
        Example.Criteria c = example.createCriteria();
        //有机构名称 - 只要有机构名就会查出固定的一条
        if (StringUtils.isNotBlank(institutionName)) {
            c.andLike("institutionName", "%" + institutionName + "%");
        }
        //只有地区
        if (StringUtils.isNotBlank(provinceName)) {
            c.andEqualTo("provinceName", provinceName);
        }
        //只有时间
        if (createTime != null) {
            c.andGreaterThanOrEqualTo("createTime", createTime);
        }
        if (lastCreateTime != null) {
            c.andLessThanOrEqualTo("createTime", lastCreateTime);
        }
        //添加能查看的机构和子机构id
        c.andIn("institutionId", validIds);
        PageHelper.startPage(req.getPage(), req.getLimit());
        List<Institution> allInstitutions = institutionMapper.selectByExample(example);
        PageInfo<Institution> pageInfo = new PageInfo<>(allInstitutions);
        //转换
        List<InstitutionByPageResp> institutionByPageResps = new ArrayList<>();
        for (Institution institution : allInstitutions) {
            InstitutionByPageResp institutionByPageResp = InstitutionByPageResp.convert(institution);
            institutionByPageResps.add(institutionByPageResp);
        }
        return ResultUtil.success(new PageData(institutionByPageResps, pageInfo.getTotal(), req.getPage(), pageInfo.getPages()));
    }

    //返回指定机构的详情
    public ResultData showInsDetail(String institutionId){
        Institution institution = institutionMapper.selectByPrimaryKey(institutionId);
        InstitutionByPageResp institutionDetail = InstitutionByPageResp.convert(institution);
        return ResultUtil.success(institutionDetail);
    }


    //超级管理员修改其他账号的，以及自己修改自己账号的：其他信息（联系人和地址等）
    public ResultData updateInsOtherInfo(InstitutionUpdateReq institutionUpdateReq) {
        Institution newIns = InstitutionUpdateReq.convert(institutionUpdateReq);
        Long institutionId = newIns.getInstitutionId();
        SysUser user = sysUserMapper.selectUserByCorporateId(institutionId);
        Long userId = user.getUserId();
        Institution oldIns = institutionMapper.selectByPrimaryKey(institutionId);

        SysUserBaseInfo baseUser = UserTokenUtil.getUser();
        String userType = baseUser.getUserType();

        if(!("admin".equals(userType)) &&
                ((!(newIns.getPartnerId().equals(oldIns.getPartnerId()))) ||
                        (!(newIns.getInstitutionName().equals(oldIns.getInstitutionName()))))){
            return MsgConverts.OptFail("您无权操作");
        }else {
            //更新机构账号中的字段
            institutionMapper.updateByPrimaryKeySelective(newIns);
            //如果系统管理员修改了机构名则更新对应系统账号中的字段
            if("admin".equals(userType) && !(newIns.getInstitutionName().equals(oldIns.getInstitutionName()))) {
                String username = newIns.getInstitutionName();
                sysUserMapper.updateUserNameByInsName(userId, username);
            }
            return ResultUtil.success("修改成功");
        }

    }

    //1. 获取当天机构总数和同比 insTotalByDateAndPer【公共方法】
    public InsTotalByDateAndPer getInsTotalByDateAndPer (LocalDate localDate, List<Long> validIds){
        InsTotalByDateAndPer insTotalByDateAndPer = new InsTotalByDateAndPer();

        int dateTotalAmount = institutionMapper.selectAllInsAmount(localDate, validIds);
        int dayBeforeTotalAmount = institutionMapper.selectAllInsAmount(localDate.minusDays(1), validIds);

        String totalPer = "100%";
        if (dayBeforeTotalAmount != 0){
            totalPer = getPer(dateTotalAmount, dayBeforeTotalAmount);
        }

        insTotalByDateAndPer.setAmount(dateTotalAmount);
        insTotalByDateAndPer.setPercent(totalPer);
        return insTotalByDateAndPer;
    }
    //2. 获取当月/周机构总数 insTotalByMonthWithDate【公共方法】
    public InsInfoByPeriodWithDate getInsTotalByPeriodWithDate(int length, LocalDate[] days, LocalDate firstDay, LocalDate lastDay, List<Long> validIds){
        InsInfoByPeriodWithDate insTotalByPeriodWithDate = new InsInfoByPeriodWithDate();

        int opening = institutionMapper.selectAllInsAmount(firstDay.minusDays(1),validIds);
        int[] totalNums = new int[length];
        for (int i = 0; i < length; i++) {
            totalNums[i] = opening;
        }
        //返回能查看的机构中的在给定时间范围内的新增数量
        ValidInsReq validInsReq = new ValidInsReq();
        validInsReq.setValidIds(validIds);
        validInsReq.setMonday(firstDay);
        validInsReq.setSunday(lastDay);
        List<InsNumIncr> insNumIncrs = institutionMapper.selectInstNumTotal(validInsReq);

        int temp = opening;
        for(InsNumIncr incrRecord : insNumIncrs) {
            temp += incrRecord.getIncrement();
            int dayIndex;
            if(length == 7){
                dayIndex = incrRecord.getCreateDay().getDayOfWeek().getValue();
            }else {
                dayIndex = incrRecord.getCreateDay().getDayOfMonth();
            }
            totalNums[dayIndex - 1] = temp;
            fill(totalNums,dayIndex,temp);
        }
        insTotalByPeriodWithDate.setDays(days);
        insTotalByPeriodWithDate.setNums(totalNums);
        return insTotalByPeriodWithDate;
    }

    //4. 获取当月/周机构新增数 insIncrByWeekWithDate【公共方法】
    public InsInfoByPeriodWithDate getInsIncrByPeriodWithDate(int length, LocalDate[] days, LocalDate firstDay, LocalDate lastDay, List<Long> validIds){
        InsInfoByPeriodWithDate insIncrByPeriodWithDate = new InsInfoByPeriodWithDate();

        int[] incrNums = new int[length];
        for (int i = 0; i < length; i++) {
            incrNums[i] = 0;
        }
        //返回能查看的机构中的在给定时间范围内的新增数量
        ValidInsReq validInsReq = new ValidInsReq();
        validInsReq.setValidIds(validIds);
        validInsReq.setMonday(firstDay);
        validInsReq.setSunday(lastDay);
        List<InsNumIncr> insNumIncrs = institutionMapper.selectInstNumTotal(validInsReq);

        for(InsNumIncr incrRecord : insNumIncrs) {
            Integer increment = incrRecord.getIncrement();
            int dayIndex;
            if(length == 7){
                dayIndex = incrRecord.getCreateDay().getDayOfWeek().getValue();
            }else {
                dayIndex = incrRecord.getCreateDay().getDayOfMonth();
            }
            incrNums[dayIndex - 1] = increment;
        }
        insIncrByPeriodWithDate.setDays(days);
        insIncrByPeriodWithDate.setNums(incrNums);
        return insIncrByPeriodWithDate;
    }

    //生成返回类对象【公共方法】
    public InstitutionBoardByDateResp getInstitutionBoardByDateResp (InsTotalByDateAndPer insTotalByDateAndPer, InsInfoByPeriodWithDate insTotalByPeriodWithDate,
                                                                     InsIncrByDateAndPer insIncrByDateAndPer, InsInfoByPeriodWithDate insIncrByPeriodWithDate){
        InstitutionBoardByDateResp institutionBoardByDateResp = new InstitutionBoardByDateResp();
        List<Object> insNumByDateAndPer = new ArrayList<>();
        insNumByDateAndPer.add(insTotalByDateAndPer);
        insNumByDateAndPer.add(insIncrByDateAndPer);
        institutionBoardByDateResp.setInsTotalByPeriodWithDate(insTotalByPeriodWithDate);
        institutionBoardByDateResp.setInsNumByDateAndPer(insNumByDateAndPer);
        institutionBoardByDateResp.setInsIncrByPeriodWithDate(insIncrByPeriodWithDate);
        return institutionBoardByDateResp;
    }

    //根据选定的日期（天）查询权限下的机构信息（包括所在月以及该月最后一天的机构总数，新增数，以及日同比） institutionBoardByMonth
    public ResultData institutionBoardByMonth(LocalDate localDate){
        //权限获取
        SysUserBaseInfo baseUser = UserTokenUtil.getUser();
        List<SubInstitution> subInstitutions = userCache.getSubInstitution(baseUser.getUserId(), baseUser.getUserType(), baseUser.getCorporateId());
        List<Long> validIds = subInstitutions.stream().map(SubInstitution::getInstitutionId).collect(Collectors.toList());
        //获取给定日期所在自然月的第一天和最后一天
        LocalDate firstDay = localDate.with(TemporalAdjusters.firstDayOfMonth()); // 获取当前月的第一天
        LocalDate lastDay = localDate.with(TemporalAdjusters.lastDayOfMonth()); // 获取当前月的最后一天

        //给日期数组赋值
        int lengthOfMonth = localDate.lengthOfMonth();
        LocalDate[] days = new LocalDate[lengthOfMonth];
        days[0] = firstDay;
        for (int i = 1; i < lengthOfMonth; i++) {
            days[i] = days[i-1].plusDays(1);
        }

        //1. 获取当天机构总数和同比 insTotalByDateAndPer
        InsTotalByDateAndPer insTotalByDateAndPer = getInsTotalByDateAndPer(lastDay, validIds);

        //2. 获取当月机构总数 insTotalByMonthWithDate
        InsInfoByPeriodWithDate insTotalByMonthWithDate = getInsTotalByPeriodWithDate(lengthOfMonth,days,firstDay, lastDay, validIds);

        //3. 获取当月机构新增总数和同比 insIncrByDateAndPer
        InsIncrByDateAndPer insIncrByDateAndPer = new InsIncrByDateAndPer();

        LocalDate firstDayOfLastMonth = firstDay.minusDays(1).with(TemporalAdjusters.firstDayOfMonth());
        int lastDayofEarlyMonth = institutionMapper.selectAllInsAmount(firstDayOfLastMonth.minusDays(1),validIds);
        int lastDayofLastMonth = institutionMapper.selectAllInsAmount(firstDay.minusDays(1),validIds);
        int lastDayofThisMonth = institutionMapper.selectAllInsAmount(lastDay,validIds);
        int IncrAmountOfLastMonth = lastDayofLastMonth-lastDayofEarlyMonth;
        int IncrAmountOfThisMonth = lastDayofThisMonth-lastDayofLastMonth;

        String incrPer = "100%";
        if (IncrAmountOfLastMonth == 0 && IncrAmountOfThisMonth == 0){
            incrPer = "0%";
        }
        if (IncrAmountOfLastMonth != 0){
            incrPer = getPer(IncrAmountOfThisMonth, IncrAmountOfLastMonth);
        }
        insIncrByDateAndPer.setAmount(IncrAmountOfThisMonth);
        insIncrByDateAndPer.setPercent(incrPer);

        //4. 获取当月机构新增数 insIncrByMonthWithDate
        InsInfoByPeriodWithDate insIncrByMonthWithDate = getInsIncrByPeriodWithDate(lengthOfMonth, days, firstDay, lastDay, validIds);

        //生成返回类对象
        InstitutionBoardByDateResp institutionBoardByDateResp = getInstitutionBoardByDateResp(insTotalByDateAndPer, insTotalByMonthWithDate, insIncrByDateAndPer, insIncrByMonthWithDate);
        return ResultUtil.success(institutionBoardByDateResp);

    }

    //根据选定的日期（天）查询权限下的机构信息（包括当天以及所在周的机构总数，新增数，以及日同比） queryInstitutionBoardByDate
    public ResultData institutionBoardByDate(LocalDate localDate) {
        //权限获取
        SysUserBaseInfo baseUser = UserTokenUtil.getUser();
        List<SubInstitution> subInstitutions = userCache.getSubInstitution(baseUser.getUserId(), baseUser.getUserType(), baseUser.getCorporateId());
        List<Long> validIds = subInstitutions.stream().map(SubInstitution::getInstitutionId).collect(Collectors.toList());

        //获取给定日期所在自然周的周一和周日
        LocalDate monday = localDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate sunday = localDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        //给日期数组赋值
        LocalDate[] days = new LocalDate[]{monday, monday.plusDays(1), monday.plusDays(2), monday.plusDays(3), monday.plusDays(4), monday.plusDays(5), monday.plusDays(6)};

        //1. 获取当天机构总数和同比 insTotalByDateAndPer
        InsTotalByDateAndPer insTotalByDateAndPer = getInsTotalByDateAndPer(localDate, validIds);

        //2. 获取当周机构总数 insTotalByWeekWithDate
        InsInfoByPeriodWithDate insTotalByWeekWithDate = getInsTotalByPeriodWithDate(7, days, monday, sunday, validIds);

        //3. 获取当天机构新增数和同比 insIncrByDateAndPer
        InsIncrByDateAndPer insIncrByDateAndPer = new InsIncrByDateAndPer();

        int dateIncrAmount = institutionMapper.selectIncrInsAmount(localDate, validIds);
        int dayBeforeIncrAmount = institutionMapper.selectIncrInsAmount(localDate.minusDays(1), validIds);

        String incrPer = "100%";
        if(dateIncrAmount == 0 && dayBeforeIncrAmount == 0){
            incrPer = "0%";
        }
        if (dayBeforeIncrAmount != 0){
            incrPer = getPer(dateIncrAmount, dayBeforeIncrAmount);
        }
        insIncrByDateAndPer.setAmount(dateIncrAmount);
        insIncrByDateAndPer.setPercent(incrPer);

        //4. 获取当周机构新增数 insIncrByWeekWithDate
        InsInfoByPeriodWithDate insIncrByWeekWithDate = getInsIncrByPeriodWithDate(7, days, monday, sunday, validIds);

        //生成返回类对象
        InstitutionBoardByDateResp institutionBoardByDateResp = getInstitutionBoardByDateResp(insTotalByDateAndPer, insTotalByWeekWithDate, insIncrByDateAndPer, insIncrByWeekWithDate);
        return ResultUtil.success(institutionBoardByDateResp);
    }


    //权限下的机构信息（机构地区分布）
    public ResultData institutionLocationInfo() {

        //权限获取
        SysUserBaseInfo baseUser = UserTokenUtil.getUser();
        List<SubInstitution> subInstitutions = userCache.getSubInstitution(baseUser.getUserId(), baseUser.getUserType(), baseUser.getCorporateId());
        List<Long> validIds = subInstitutions.stream().map(SubInstitution::getInstitutionId).collect(Collectors.toList());

        //获取地区分布百分比列表
        List<InsLocPer> insLocPers = institutionMapper.selectInsLocInfo(validIds);

        for (InsLocPer insLocPer: insLocPers){
            int countTotal = validIds.size();
            int provinceCount = insLocPer.getProvinceCount();
            float res = ((float)provinceCount) / (float)countTotal ;
            NumberFormat nt = NumberFormat.getPercentInstance();
            nt.setMinimumFractionDigits(1);
            insLocPer.setPer(nt.format(res));
        }
        return ResultUtil.success(insLocPers);
    }

    //权限下的机构信息（机构地区分页）
    public ResultData institutionLocationByPage(InsLocByPageReq insLocByPageReq){
        LocalDate localDate = insLocByPageReq.getLocalDate();
        Integer page = insLocByPageReq.getPage();
        Integer limit = insLocByPageReq.getLimit();

        //权限获取
        SysUserBaseInfo baseUser = UserTokenUtil.getUser();
        List<SubInstitution> subInstitutions = userCache.getSubInstitution(baseUser.getUserId(), baseUser.getUserType(), baseUser.getCorporateId());
        List<Long> validIds = subInstitutions.stream().map(SubInstitution::getInstitutionId).collect(Collectors.toList());

        //获取分页信息
        PageHelper.startPage(page, limit);
        List<InsLocPer> insLocPerTotals = institutionMapper.selectInsLocInfo(validIds);
        List<InsLocPer> insLocPerIncrs = institutionMapper.selectInsLocIncr(localDate, validIds);
        ArrayList<InsLocInfoByPage> insLocInfoByPages = new ArrayList<>();

        for (InsLocPer insLocTotal: insLocPerTotals){
            InsLocInfoByPage insLocInfoByPage = new InsLocInfoByPage();
            insLocInfoByPage.setProvinceTotalCount(insLocTotal.getProvinceCount());
            insLocInfoByPage.setProvinceName(insLocTotal.getProvinceName());
            insLocInfoByPage.setProvinceIncrCount(0);

            //如果有新增才进行如下步骤
            if (!(insLocPerIncrs.size() == 1 && insLocPerIncrs.get(0).getProvinceName() == null)) {
                for (int i = 0; i < insLocPerIncrs.size(); i++) {
                    InsLocPer insLocIncr = insLocPerIncrs.get(i);
                    if (insLocIncr.getProvinceName().equals(insLocTotal.getProvinceName())) {
                        insLocInfoByPage.setProvinceIncrCount(insLocIncr.getProvinceCount());
                        break;
                    }
                }
            }
            insLocInfoByPages.add(insLocInfoByPage);
        }

        PageInfo<InsLocInfoByPage> pageInfo = new PageInfo<>(insLocInfoByPages);
       return ResultUtil.success(new PageData(insLocInfoByPages, pageInfo.getTotal(), page, pageInfo.getPages()));
    }

    //辅助方法1
    private static void fill(int[] nums,int dayIndex,int res) {
        for(int i = dayIndex; i < nums.length;i++) {
            nums[i] = res;
        }
    }
    //辅助方法2 - 得到日同比
    public static String getPer(float dateAmount, float dateBeforeAmount){
        double res = (dateAmount - dateBeforeAmount) / dateBeforeAmount ;
        NumberFormat nt = NumberFormat.getPercentInstance();
        nt.setMinimumFractionDigits(1);
        return nt.format(res);
    }

    /**
     * 超级管理员删除二级机构
     * @param institutionId 需要删除的机构id
     * @return
     */

    public ResultData deleteIns(String institutionId) {
        SysUserBaseInfo user = UserTokenUtil.getUser();
        String userType = user.getUserType();
        //验证权限
        if("admin".equals(userType)) {
            Institution institution = institutionMapper.selectByPrimaryKey(institutionId);
            Long parentId = institution.getParentId();
            //如果是一级机构则无法删除
            if (parentId == 0) {
                return MsgConverts.OptFail("该机构名下仍拥有子机构，无法删除");
            }
            //删除二级机构账号
            institutionMapper.delete(institution);
            //删除对应的系统账号
            Long userId = institution.getUserId();
            SysUser sysUser = sysUserMapper.selectByPrimaryKey(userId);
            sysUserMapper.delete(sysUser);
            return ResultUtil.success("删除成功");
        }else{
            return MsgConverts.OptFail("您无权操作");
        }
    }

    /**
     * 获取指定机构的父级机构id列表（包含指定机构本身）
     * @param institutionId
     * @return
     */
    public List<Long> selectSuperiorInst(Long institutionId) {
        return institutionMapper.selectSuperiorInst(institutionId);
    }

    /**
     * 保存推送地址，如果已存在，就是更新
     * @param req
     * @return
     */
    public ResultData savePushUrl(DataPushUrlConfigReq req) {
        Example example = new Example(DataPushUrlConfig.class);
        example.createCriteria().andEqualTo("institutionId", Long.valueOf(req.getInstitutionId())).andEqualTo("dataType",req.getDataType());
        DataPushUrlConfig pushUrlConfig = pushUrlConfigMapper.selectOneByExample(example);
        if(pushUrlConfig == null) {
            pushUrlConfig = DataPushUrlConfig.convert(req);
            pushUrlConfigMapper.insert(pushUrlConfig);
        } else {
            pushUrlConfig.setUrl(req.getUrl());
            pushUrlConfig.setUpdated(LocalDateTime.now());
            pushUrlConfigMapper.updateByPrimaryKey(pushUrlConfig);
        }
        return ResultUtil.success();
    }

    public ResultData delPushUrl(String instId) {
        Example example = new Example(DataPushUrlConfig.class);
        example.createCriteria().andEqualTo("institutionId", Long.valueOf(instId));
        pushUrlConfigMapper.deleteByExample(example);
        return ResultUtil.success();
    }


    /**
     * 查询配置的推送地址
     * @param req
     * @return
     */
    public ResultData queryPushUrl(PushUrlQuery req) {
        Map<String,String> map = new HashMap<>();

        String url = pushUrlCache.getUrl(req.getDevice(),req.getIdentify());
        if(StringUtils.isBlank(url)) {
            long instId = 0;
            if(StringUtils.isNotBlank(req.getInstitutionId())) {
                instId = Long.parseLong(req.getInstitutionId());
            } else {
                Example example = new Example(DeviceBindInstitution.class);
                example.createCriteria().andEqualTo("deviceIdent",req.getIdentify()).andEqualTo("bindingState",1);
                DeviceBindInstitution deviceBindInstitution = deviceBindInstitutionMapper.selectOneByExample(example);
                if(deviceBindInstitution == null) {
                    return ResultUtil.success();
                }
                instId = deviceBindInstitution.getInstitutionId();
            }
            url = pushUrlCache.getUrl(instId,req.getDevice(),req.getIdentify());
        }
        map.put("url",url);
        return ResultUtil.success(map);
    }
}
