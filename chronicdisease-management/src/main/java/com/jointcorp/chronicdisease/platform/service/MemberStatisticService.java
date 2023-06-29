package com.jointcorp.chronicdisease.platform.service;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.jointcorp.chronicdisease.data.SysUserBaseInfo;
import com.jointcorp.chronicdisease.data.consts.Consts;
import com.jointcorp.chronicdisease.data.po.DateParam;
import com.jointcorp.chronicdisease.data.po.device.statistics.DateAmount;
import com.jointcorp.chronicdisease.data.po.device.statistics.DateCollection;
import com.jointcorp.chronicdisease.data.req.deviceReq.DeviceStatisticReq;
import com.jointcorp.chronicdisease.data.resp.memberResp.MemberDataByAgeResp;
import com.jointcorp.chronicdisease.data.resp.statictisresp.*;
import com.jointcorp.chronicdisease.platform.cache.UserCache;
import com.jointcorp.chronicdisease.platform.interceptor.support.UserTokenUtil;
import com.jointcorp.chronicdisease.platform.mapper.MemberMapper;
import com.jointcorp.chronicdisease.platform.mapper.MemberStatisticMapper;
import com.jointcorp.chronicdisease.platform.utils.DateUtils;
import com.jointcorp.common.util.JsonUtil;
import com.jointcorp.redissoncache.client.RedissonCacheClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * 会员看板-数据统计
 *
 * @Author zHuH1
 * @Date 2023/6/14 19:24
 **/
@Slf4j
@Service
public class MemberStatisticService {

//    @Autowired
//    private RedissonCacheClient redissonCacheClient;
    @Autowired
    private UserCache userCache;
    @Autowired
    ThreadPoolExecutor executor;

    @Autowired
    private MemberMapper memberMapper;
    @Autowired
    private MemberStatisticMapper memberStatisticMapper;

    @Autowired
    private CommonStatictisService commonStatictisService;

    /**
     * 会员数据统计
     * @param req
     * @return
     */
    public StatictisResp queryMemberBoardByDate(DeviceStatisticReq req) throws ExecutionException, InterruptedException {
        final StatictisResp statictisResp = new StatictisResp();
        // 获取机构下的所有会员id
        List<Long> memberIdList = getMemberIdListByInstitution(req.getInstitutionId() == null ? null : req.getInstitutionId());
        // 分析时间参数
        DateCollection dateCollection = DateUtils.getDateCollection(req.getSelectDate(), req.getType());

        // 获取会员总数数据:总数，百分比，时间范围内的数据
        CompletableFuture<Void> totalFuture = CompletableFuture.runAsync(() -> {
            AmountStatisticPartition partition = queryMemberTotalData(memberIdList, req.getDeviceTypeCode(),
                    dateCollection.getQueryStartDateTime(), dateCollection.getQueryEndDateTime(), dateCollection.getCompareEndDateTime());
            statictisResp.setTotalAmount(partition.getNum());
            statictisResp.setTotalPer(partition.getPer());
            statictisResp.setTotalByRangeDate(partition.getRangeNumAndDayResp());
            log.info("会员总数统计:总数:{},百分比:{},每天数据:{}", partition.getNum(), partition.getPer(), partition.getRangeNumAndDayResp());
        }, executor);

        // 获取设备新增数据:新增数量，百分比，时间范围内的数据
        CompletableFuture<Void> incrFuture = CompletableFuture.runAsync(() -> {
            AmountStatisticPartition partition = queryMemberIncrData(memberIdList, req.getDeviceTypeCode(),
                    dateCollection.getQueryStartDateTime(), dateCollection.getQueryEndDateTime(),
                    dateCollection.getCompareStartDateTime(), dateCollection.getCompareEndDateTime());
            statictisResp.setIncrAmount(partition.getNum());
            statictisResp.setIncrPer(partition.getPer());
            statictisResp.setIncrByRangeDate(partition.getRangeNumAndDayResp());
            log.info("新增会员统计:数量:{},百分比:{},每天数据:{}", partition.getNum(), partition.getPer(), partition.getRangeNumAndDayResp());
        }, executor);

        // 统计在线设备数据
        // 当deviceListFuture完成后执行此任务
        CompletableFuture<Void> onlineFuture = CompletableFuture.runAsync(() -> {
            AmountStatisticPartition partition = queryMemberOnlineData(memberIdList, req.getDeviceTypeCode(),
                    dateCollection.getQueryStartDateTime(), dateCollection.getQueryEndDateTime(),
                    dateCollection.getCompareStartDateTime(), dateCollection.getCompareEndDateTime());
            statictisResp.setOnlineAmount(partition.getNum());
            statictisResp.setOnlinePer(partition.getPer());
            statictisResp.setOnlineByRangeDate(partition.getRangeNumAndDayResp());
            log.info("在线会员统计:数量:{},百分比:{},每天数据:{}", partition.getNum(), partition.getPer(), partition.getRangeNumAndDayResp());
        }, executor);

        // 统计活跃设备数据
        CompletableFuture<Void> activeFuture = CompletableFuture.runAsync(() -> {
            AmountStatisticPartition partition = queryMemberActiveData(memberIdList, req.getDeviceTypeCode(),
                    dateCollection.getQueryStartDateTime(), dateCollection.getQueryEndDateTime(),
                    dateCollection.getCompareStartDateTime(), dateCollection.getCompareEndDateTime());
            statictisResp.setActiveAmount(partition.getNum());
            statictisResp.setActivePer(partition.getPer());
            statictisResp.setActiveByRangeDate(partition.getRangeNumAndDayResp());
            log.info("活跃设备统计:数量:{},百分比:{},每天数据:{}", partition.getNum(), partition.getPer(), partition.getRangeNumAndDayResp());
        }, executor);

        CompletableFuture<Void> future = CompletableFuture.allOf(totalFuture, incrFuture, onlineFuture, activeFuture);
        future.get();
        return statictisResp;
    }

    /**
     * 获取设备总数数据:总数，百分比，时间范围内的数据
     * @param memberIdList
     * @param deviceTypeCode
     * @param queryStartTime
     * @param queryEndTime
     * @param compareEndTime
     * @return
     */
    public AmountStatisticPartition queryMemberTotalData(List<Long> memberIdList, String deviceTypeCode,
                                                         LocalDateTime queryStartTime, LocalDateTime queryEndTime,
                                                         LocalDateTime compareEndTime) {
        // 指定时间的设备总数
        Integer newestTotalNum = memberStatisticMapper.memberTotalNumWithDay(memberIdList, deviceTypeCode, String.valueOf(queryEndTime));
        // 比较时间的设备总数
        Integer priorTotalNum = memberStatisticMapper.memberTotalNumWithDay(memberIdList, deviceTypeCode, String.valueOf(compareEndTime));
        String percent = commonStatictisService.calculatePercent(newestTotalNum, priorTotalNum);

        // 如果查询的开始结束时间是同一天，说明是按日查询,修改开始时间至六天前,显示近七天的数据
        if (0 == LocalDateTimeUtil.between(queryStartTime, queryEndTime).toDays()) {
            // 设置查询时间从六天前开始
            queryStartTime = queryStartTime.minusDays(6);
            // 查询六天前的会员总数
            priorTotalNum = memberStatisticMapper.memberTotalNumWithDay(memberIdList, deviceTypeCode, String.valueOf(queryEndTime.minusDays(6)));
        }
        // 获取时间范围内，每天新增的设备数量
        Map<String, Integer> map = memberStatisticMapper.memberIncrNumWithDate(memberIdList, deviceTypeCode,
                String.valueOf(queryStartTime), String.valueOf(queryEndTime)).stream()
                .collect(Collectors.toMap(DateAmount::getDate, DateAmount::getCount));
        RangeNumAndDayResp rangeNumAndDayResp = commonStatictisService.analyzeIncrNumAndDay(map, priorTotalNum, queryStartTime, queryEndTime);

        // 封装数据并返回
        return AmountStatisticPartition.partitionConvert(newestTotalNum, percent, rangeNumAndDayResp);
    }

    /**
     * 获取设备新增数据:数量，百分比，时间范围内的数据
     * @param memberIdList
     * @param deviceTypeCode
     * @param queryStartTime
     * @param queryEndTime
     * @param compareStartTime
     * @param compareEndTime
     * @return
     */
    public AmountStatisticPartition queryMemberIncrData(List<Long> memberIdList, String deviceTypeCode,
                                                        LocalDateTime queryStartTime, LocalDateTime queryEndTime,
                                                        LocalDateTime compareStartTime, LocalDateTime compareEndTime) {
        // 查询设备总数和百分比
        NumAndPerResp numAndPer = queryMemberIncrNumAndPer(memberIdList, deviceTypeCode ,queryStartTime, queryEndTime,
                compareStartTime, compareEndTime);
        // 如果查询的开始结束时间是同一天，说明是按日查询,修改开始时间至六天前,显示近七天的数据
        if (0 == LocalDateTimeUtil.between(queryStartTime, queryEndTime).toDays()) {
            queryStartTime = queryStartTime.minusDays(6);
        }
        // 查询时间范围内每天的数据
        RangeNumAndDayResp rangeNumAndDay = queryMemberRangeIncrNumAndDay(memberIdList, deviceTypeCode, queryStartTime, queryEndTime);
        // 封装数据并返回
        return AmountStatisticPartition.partitionConvert(numAndPer.getNum(), numAndPer.getPer(), rangeNumAndDay);
    }

    /**
     * 设备新增数量和百分比
     * @param memberIdList
     * @param deviceTypeCode
     * @param queryStartTime
     * @param queryEndTime
     * @param compareStartTime
     * @param compareEndTime
     * @return
     */
    public NumAndPerResp queryMemberIncrNumAndPer(List<Long> memberIdList, String deviceTypeCode,
                                                  LocalDateTime queryStartTime, LocalDateTime queryEndTime,
                                                  LocalDateTime compareStartTime, LocalDateTime compareEndTime) {
        NumAndPerResp resp = new NumAndPerResp();
        // 指定时间内新增设备数量
        Integer newestIncrNum = memberStatisticMapper.memberIncrNumWithDay(memberIdList, deviceTypeCode,
                String.valueOf(queryStartTime), String.valueOf(queryEndTime));
        // 比较时间的新增设备数量
        Integer priorIncrNum = memberStatisticMapper.memberIncrNumWithDay(memberIdList, deviceTypeCode,
                String.valueOf(compareStartTime), String.valueOf(compareEndTime));
        resp.setNum(newestIncrNum);
        resp.setPer(commonStatictisService.calculatePercent(newestIncrNum, priorIncrNum));
        return resp;
    }

    /**
     * 获取时间范围内，每天的日期和新增设备数量
     * @param memberIdList
     * @param deviceTypeCode
     * @param queryStartTime
     * @param queryEndTime
     * @return
     */
    public RangeNumAndDayResp queryMemberRangeIncrNumAndDay(List<Long> memberIdList, String deviceTypeCode,
                                                            LocalDateTime queryStartTime, LocalDateTime queryEndTime) {
        // 获取时间范围内，日期和设备总数
        Map<String, Integer> map = memberStatisticMapper.memberIncrNumWithDate(memberIdList, deviceTypeCode, String.valueOf(queryStartTime),
                String.valueOf(queryEndTime)).stream().collect(Collectors.toMap(DateAmount::getDate, DateAmount::getCount));
        return commonStatictisService.analyzeNumAndDay(map, queryStartTime, queryEndTime);
    }

    /**
     * 获取设备在线数据:数量，百分比，时间范围内的数据
     * @param memberIdList
     * @param deviceTypeCode
     * @param queryStartTime
     * @param queryEndTime
     * @param compareStartTime
     * @param compareEndTime
     * @return
     */
    public AmountStatisticPartition queryMemberOnlineData(List<Long> memberIdList, String deviceTypeCode,
                                                          LocalDateTime queryStartTime, LocalDateTime queryEndTime,
                                                          LocalDateTime compareStartTime, LocalDateTime compareEndTime) {
        // 查询设备总数和百分比
        NumAndPerResp numAndPer = queryMemberOnlineNumAndPer(memberIdList, deviceTypeCode ,queryStartTime, queryEndTime,
                compareStartTime, compareEndTime);
        // 如果查询的开始结束时间是同一天，说明是按日查询,修改开始时间至六天前,显示近七天的数据
        if (0 == LocalDateTimeUtil.between(queryStartTime, queryEndTime).toDays()) {
            queryStartTime = queryStartTime.minusDays(6);
        }
        // 查询时间范围内每天的数据
        RangeNumAndDayResp rangeNumAndDay = queryMemberRangeOnlineNumAndDay(memberIdList, deviceTypeCode, queryStartTime, queryEndTime);
        // 封装数据并返回
        return AmountStatisticPartition.partitionConvert(numAndPer.getNum(), numAndPer.getPer(), rangeNumAndDay);
    }

    /**
     * 设备在线数量和百分比
     * @param memberIdList
     * @param deviceTypeCode
     * @param queryStartTime
     * @param queryEndTime
     * @param compareStartTime
     * @param compareEndTime
     * @return
     */
    public NumAndPerResp queryMemberOnlineNumAndPer(List<Long> memberIdList, String deviceTypeCode,
                                                    LocalDateTime queryStartTime, LocalDateTime queryEndTime,
                                                    LocalDateTime compareStartTime, LocalDateTime compareEndTime) {
        NumAndPerResp resp = new NumAndPerResp();
        // 指定时间内新增设备数量   `
        Integer newestIncrNum = memberStatisticMapper.memberOnlineNumWithDay(memberIdList, deviceTypeCode,
                String.valueOf(queryStartTime), String.valueOf(queryEndTime));
        // 比较时间的新增设备数量
        Integer priorIncrNum = memberStatisticMapper.memberOnlineNumWithDay(memberIdList, deviceTypeCode,
                String.valueOf(compareStartTime), String.valueOf(compareEndTime));
        resp.setNum(newestIncrNum);
        resp.setPer(commonStatictisService.calculatePercent(newestIncrNum, priorIncrNum));
        return resp;
    }

    /**
     * 获取时间范围内，每天的日期和在线设备数量
     * @param memberIdList
     * @param deviceTypeCode
     * @param queryStartTime
     * @param queryEndTime
     * @return
     */
    public RangeNumAndDayResp queryMemberRangeOnlineNumAndDay(List<Long> memberIdList, String deviceTypeCode,
                                                              LocalDateTime queryStartTime, LocalDateTime queryEndTime) {
        // 获取时间范围内，日期和设备总数
        Map<String, Integer> map = memberStatisticMapper.memberOnlineNumWithDate(memberIdList, deviceTypeCode, String.valueOf(queryStartTime),
                String.valueOf(queryEndTime)).stream().collect(Collectors.toMap(DateAmount::getDate, DateAmount::getCount));
        return commonStatictisService.analyzeNumAndDay(map, queryStartTime, queryEndTime);
    }

    /**
     * 获取设备在线数据:数量，百分比，时间范围内的数据
     * @return
     */
    public AmountStatisticPartition queryMemberActiveData(List<Long> memberIdList, String deviceTypeCode,
                                                          LocalDateTime queryStartTime, LocalDateTime queryEndTime,
                                                          LocalDateTime compareStartTime, LocalDateTime compareEndTime) {
        // 查询设备活跃数量和百分比
        NumAndPerResp numAndPer = queryMemberActiveNumAndPer(memberIdList, deviceTypeCode,queryStartTime.minusDays(2),
                queryEndTime, compareStartTime, compareEndTime);
        // 如果查询的开始结束时间是同一天，说明是按日查询,修改开始时间至六天前,显示近七天的数据
        if (0 == LocalDateTimeUtil.between(queryStartTime, queryEndTime).toDays()) {
            queryStartTime = queryStartTime.minusDays(6);
        }
        // 查询时间范围内每天的数据
        RangeNumAndDayResp rangeNumAndDay = queryMemberRangeActiveNumAndDay(memberIdList, deviceTypeCode,
                queryStartTime.minusDays(2), queryEndTime);
        // 封装数据并返回
        return AmountStatisticPartition.partitionConvert(numAndPer.getNum(), numAndPer.getPer(), rangeNumAndDay);
    }

    /**
     * 设备活跃数量和百分比
     * @param memberIdList
     * @param deviceTypeCode
     * @param queryStartTime
     * @param queryEndTime
     * @param compareStartTime
     * @param compareEndTime
     * @return
     */
    public NumAndPerResp queryMemberActiveNumAndPer(List<Long> memberIdList, String deviceTypeCode,
                                                    LocalDateTime queryStartTime, LocalDateTime queryEndTime,
                                                    LocalDateTime compareStartTime, LocalDateTime compareEndTime) {
        NumAndPerResp resp = new NumAndPerResp();
        // 连续三天为活跃，因此需要从开始时间的前两天开始统计
        // 指定时间内新增设备数量
        Integer newestIncrNum = memberStatisticMapper.memberActiveNumWithDay(memberIdList, deviceTypeCode,
                String.valueOf(queryStartTime), String.valueOf(queryEndTime));
        // 比较时间的新增设备数量
        Integer priorIncrNum = memberStatisticMapper.memberActiveNumWithDay(memberIdList, deviceTypeCode,
                String.valueOf(compareStartTime), String.valueOf(compareEndTime));
        resp.setNum(newestIncrNum);
        resp.setPer(commonStatictisService.calculatePercent(newestIncrNum, priorIncrNum));
        return resp;
    }

    /**
     * 获取时间范围内，每天的日期和活跃会员数量
     * @param memberIdList
     * @param deviceTypeCode
     * @param queryStartTime
     * @param queryEndTime
     * @return
     */
    public RangeNumAndDayResp queryMemberRangeActiveNumAndDay(List<Long> memberIdList, String deviceTypeCode,
                                                              LocalDateTime queryStartTime, LocalDateTime queryEndTime) {
        // 获取时间范围内，日期和设备总数
        Map<String, Integer> map = memberStatisticMapper.memberActiveNumWithDate(memberIdList, deviceTypeCode, String.valueOf(queryStartTime),
                String.valueOf(queryEndTime)).stream().collect(Collectors.toMap(DateAmount::getDate, DateAmount::getCount));
        return commonStatictisService.analyzeNumAndDay(map, queryStartTime.minusDays(-2), queryEndTime);
    }


    /**
     * 获取机构下的所有会员id
     * @return
     */
    public List<Long> getMemberIdListByInstitution(Long instituionId) {
        SysUserBaseInfo sysUser = new SysUserBaseInfo();
        // 如果传入了机构id,则按该机构获取设备地址;未传入则按当前登录用户获取
        if (instituionId != null) {
            sysUser.setUserId(-1L);
            sysUser.setUserType(Consts.USER_TYPE_INS);
            sysUser.setCorporateId(instituionId);
        } else {
            sysUser = UserTokenUtil.getUser();
        }
        List<Long> subInstitutionIdList = userCache.getSubInstIdList(sysUser.getUserId(), sysUser.getUserType(), sysUser.getCorporateId());
        return memberMapper.getMemberIdListByInst(subInstitutionIdList);
    }




    /**
     * 统计各个年龄段的会员数据：总人数、新增人数、在线人数、活跃人数
     * @param req
     * @return
     */
    public MemberDataByAgeResp memberStatisticByAge(DeviceStatisticReq req) {
        // 获取机构下的所有会员id
        List<Long> memberIdList = getMemberIdListByInstitution(req.getInstitutionId() == null ? 500897288739229696L : req.getInstitutionId());
        if (CollectionUtils.isEmpty(memberIdList)) {
            return new MemberDataByAgeResp();
        }
        // 分析获取查询的起止时间范围
        DateParam dateParam = DateUtils.analyzeDateParam(req.getSelectDate(), req.getType());
        // 统计每个年龄段的人数
        List<PartitionAmountResp> partitionAmountResps = memberStatisticMapper.selectMemberAgeNum(memberIdList, String.valueOf(dateParam.getEndTime()));
        // 按年龄段统计会员数据
        List<StatisticByPartitionResp> statisticByPartitionResps = memberStatisticMapper.queryMemberStatisticByAge(memberIdList,
                req.getDeviceTypeCode(),
                String.valueOf(dateParam.getStartTime()),
                String.valueOf(dateParam.getEndTime()));

        MemberDataByAgeResp resp = new MemberDataByAgeResp();
        resp.setPartitionAmountResps(partitionAmountResps);
        resp.setStatisticByPartitionResps(statisticByPartitionResps);
        return resp;
    }
}
