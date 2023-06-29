package com.jointcorp.chronicdisease.platform.service;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jointcorp.chronicdisease.data.po.DateParam;
import com.jointcorp.chronicdisease.data.po.device.statistics.*;
import com.jointcorp.chronicdisease.data.resp.statictisresp.*;
import com.jointcorp.chronicdisease.data.req.deviceReq.DeviceStatisticReq;
import com.jointcorp.chronicdisease.platform.mapper.DeviceMapper;
import com.jointcorp.chronicdisease.platform.utils.DateUtils;
import com.jointcorp.support.vo.PageData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * @Author zHuH1
 * @Date 2023/5/12 16:15
 **/
@Slf4j
@Service
public class DeviceStatisticsService {

    @Autowired
    private DeviceMapper deviceMapper;
    @Autowired
    private DeviceService deviceService;
    @Autowired
    ThreadPoolExecutor executor;
    @Autowired
    private CommonStatictisService commonStatictisService;

    /**
     * 设备数据统计
     * @param req
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public StatictisResp queryDeviceBoard(DeviceStatisticReq req) throws ExecutionException, InterruptedException {
        final StatictisResp statictisResp = new StatictisResp();
        // 分析时间参数
        DateCollection dateCollection = DateUtils.getDateCollection(req.getSelectDate(), req.getType());
        log.info("日期集合:{}", dateCollection);

        // 获取设备地址列表
        List<String> identList = deviceService.getDeviceIdentsByInstitution(req.getInstitutionId() == null ? null : req.getInstitutionId());
        // 进一步根据类型进行筛选查询
        identList = deviceMapper.selectByCodeAndDate(identList, String.valueOf(dateCollection.getQueryEndDateTime()),
                StringUtils.isBlank(req.getDeviceTypeCode()) ? null : req.getDeviceTypeCode());
        log.info("指定条件下的设备列表:{}", identList);
        if (CollectionUtils.isEmpty(identList)) {
            return new StatictisResp();
        }

        final List<String> deviceIdentList = identList;
        // 获取设备总数数据:总数，百分比，时间范围内的数据
        CompletableFuture<Void> totalFuture = CompletableFuture.runAsync(() -> {
            AmountStatisticPartition partition = queryDeviceTotalData(deviceIdentList, dateCollection.getQueryStartDateTime(),
                    dateCollection.getQueryEndDateTime(), dateCollection.getCompareEndDateTime());
            statictisResp.setTotalAmount(partition.getNum());
            statictisResp.setTotalPer(partition.getPer());
            statictisResp.setTotalByRangeDate(partition.getRangeNumAndDayResp());
            log.info("设备总数统计:总数:{},百分比:{},每天数据:{}", partition.getNum(), partition.getPer(), partition.getRangeNumAndDayResp());
        }, executor);


        // 获取设备新增数据:新增数量，百分比，时间范围内的数据
        CompletableFuture<Void> incrFuture = CompletableFuture.runAsync(() -> {
            AmountStatisticPartition partition = queryDeviceIncrData(deviceIdentList, dateCollection.getQueryStartDateTime(),
                    dateCollection.getQueryEndDateTime(), dateCollection.getCompareStartDateTime(),
                    dateCollection.getCompareEndDateTime());
            statictisResp.setIncrAmount(partition.getNum());
            statictisResp.setIncrPer(partition.getPer());
            statictisResp.setIncrByRangeDate(partition.getRangeNumAndDayResp());
            log.info("新增设备统计:数量:{},百分比:{},每天数据:{}", partition.getNum(), partition.getPer(), partition.getRangeNumAndDayResp());
        }, executor);

        // 统计在线设备数据
        // 当deviceListFuture完成后执行此任务
        CompletableFuture<Void> onlineFuture = CompletableFuture.runAsync(() -> {
            AmountStatisticPartition partition = queryDeviceOnlineData(deviceIdentList, dateCollection.getQueryStartDateTime(),
                    dateCollection.getQueryEndDateTime(), dateCollection.getCompareStartDateTime(),
                    dateCollection.getCompareEndDateTime());
            statictisResp.setOnlineAmount(partition.getNum());
            statictisResp.setOnlinePer(partition.getPer());
            statictisResp.setOnlineByRangeDate(partition.getRangeNumAndDayResp());
            log.info("在线设备统计:数量:{},百分比:{},每天数据:{}", partition.getNum(), partition.getPer(), partition.getRangeNumAndDayResp());
        }, executor);

        // 统计活跃设备数据
        CompletableFuture<Void> activeFuture = CompletableFuture.runAsync(() -> {
            AmountStatisticPartition partition = queryDeviceActiveData(deviceIdentList, dateCollection.getQueryStartDateTime(),
                    dateCollection.getQueryEndDateTime(), dateCollection.getCompareStartDateTime(),
                    dateCollection.getCompareEndDateTime());
            statictisResp.setActiveAmount(partition.getNum());
            statictisResp.setActivePer(partition.getPer());
            statictisResp.setActiveByRangeDate(partition.getRangeNumAndDayResp());
            log.info("活跃设备统计:数量:{},百分比:{},每天数据:{}", partition.getNum(), partition.getPer(), partition.getRangeNumAndDayResp());
        }, executor);

        // 等待所有任务执行完成
        CompletableFuture<Void> future = CompletableFuture.allOf(totalFuture, incrFuture, onlineFuture, activeFuture);
        future.get();
        log.info("设备数据汇总:{}", statictisResp);
        return statictisResp;
    }

    /**
     * 获取设备总数数据:总数，百分比，时间范围内的数据
     * @return
     */
    public AmountStatisticPartition queryDeviceTotalData(List<String> identList, LocalDateTime queryStartTime,
                                             LocalDateTime queryEndTime, LocalDateTime compareEndTime) {
        // 指定时间的设备总数
        Integer newestTotalNum = deviceMapper.deviceTotalNumWithDay(identList, String.valueOf(queryEndTime));
        // 比较时间的设备总数
        Integer priorTotalNum = deviceMapper.deviceTotalNumWithDay(identList, String.valueOf(compareEndTime));
        String percent = commonStatictisService.calculatePercent(newestTotalNum, priorTotalNum);

        // 如果查询的开始结束时间是同一天，说明是按日查询,修改开始时间至六天前,显示近七天的数据
        if (0 == LocalDateTimeUtil.between(queryStartTime, queryEndTime).toDays()) {
            queryStartTime = queryStartTime.minusDays(6);
        }
        // 获取时间范围内，每天新增的设备数量
        Map<String, Integer> map = deviceMapper.deviceIncrNumWithDate(identList, String.valueOf(queryStartTime),
                String.valueOf(queryEndTime)).stream().collect(Collectors.toMap(DateAmount::getDate, DateAmount::getCount));
        RangeNumAndDayResp rangeNumAndDayResp = commonStatictisService.analyzeIncrNumAndDay(map, priorTotalNum, queryStartTime, queryEndTime);

        // 封装数据并返回
        return AmountStatisticPartition.partitionConvert(newestTotalNum, percent, rangeNumAndDayResp);
    }

    /**
     * 获取设备新增数据:数量，百分比，时间范围内的数据
     * @return
     */
    public AmountStatisticPartition queryDeviceIncrData(List<String> identList, LocalDateTime queryStartTime, LocalDateTime queryEndTime,
                                                        LocalDateTime compareStartTime, LocalDateTime compareEndTime) {
        // 查询设备总数和百分比
        NumAndPerResp numAndPer = queryDeviceIncrNumAndPer(identList, queryStartTime, queryEndTime, compareStartTime, compareEndTime);
        // 如果查询的开始结束时间是同一天，说明是按日查询,修改开始时间至六天前,显示近七天的数据
        if (0 == LocalDateTimeUtil.between(queryStartTime, queryEndTime).toDays()) {
            queryStartTime = queryStartTime.minusDays(6);
        }
        // 查询时间范围内每天的数据
        RangeNumAndDayResp rangeNumAndDay = queryDeviceRangeIncrNumAndDay(identList, queryStartTime, queryEndTime);
        // 封装数据并返回
        return AmountStatisticPartition.partitionConvert(numAndPer.getNum(), numAndPer.getPer(), rangeNumAndDay);
    }

    /**
     * 设备新增数量和百分比
     * @return
     */
    public NumAndPerResp queryDeviceIncrNumAndPer(List<String> identList, LocalDateTime queryStartTime, LocalDateTime queryEndTime,
                                            LocalDateTime compareStartTime, LocalDateTime compareEndTime) {
        NumAndPerResp resp = new NumAndPerResp();
        // 指定时间内新增设备数量
        Integer newestIncrNum = deviceMapper.deviceIncrNumWithDay(identList, String.valueOf(queryStartTime), String.valueOf(queryEndTime));
        // 比较时间的新增设备数量
        Integer priorIncrNum = deviceMapper.deviceIncrNumWithDay(identList, String.valueOf(compareStartTime), String.valueOf(compareEndTime));
        resp.setNum(newestIncrNum);
        resp.setPer(commonStatictisService.calculatePercent(newestIncrNum, priorIncrNum));
        return resp;
    }

    /**
     * 获取时间范围内，每天的日期和新增设备数量
     * @param identList
     * @param queryStartTime
     * @param queryEndTime
     * @return
     */
    public RangeNumAndDayResp queryDeviceRangeIncrNumAndDay(List<String> identList, LocalDateTime queryStartTime, LocalDateTime queryEndTime) {
        // 获取时间范围内，日期和设备总数
        Map<String, Integer> map = deviceMapper.deviceIncrNumWithDate(identList, String.valueOf(queryStartTime),
                String.valueOf(queryEndTime)).stream().collect(Collectors.toMap(DateAmount::getDate, DateAmount::getCount));
        return commonStatictisService.analyzeNumAndDay(map, queryStartTime, queryEndTime);
    }

    /**
     * 获取设备在线数据:数量，百分比，时间范围内的数据
     * @return
     */
    public AmountStatisticPartition queryDeviceOnlineData(List<String> identList, LocalDateTime queryStartTime, LocalDateTime queryEndTime,
                                                          LocalDateTime compareStartTime, LocalDateTime compareEndTime) {
        // 查询设备总数和百分比
        NumAndPerResp numAndPer = queryDeviceOnlineNumAndPer(identList, queryStartTime, queryEndTime, compareStartTime, compareEndTime);
        // 如果查询的开始结束时间是同一天，说明是按日查询,修改开始时间至六天前,显示近七天的数据
        if (0 == LocalDateTimeUtil.between(queryStartTime, queryEndTime).toDays()) {
            queryStartTime = queryStartTime.minusDays(6);
        }
        // 查询时间范围内每天的数据
        RangeNumAndDayResp rangeNumAndDay = queryDeviceRangeOnlineNumAndDay(identList, queryStartTime, queryEndTime);
        // 封装数据并返回
        return AmountStatisticPartition.partitionConvert(numAndPer.getNum(), numAndPer.getPer(), rangeNumAndDay);
    }

    /**
     * 设备在线数量和百分比
     * @return
     */
    public NumAndPerResp queryDeviceOnlineNumAndPer(List<String> identList, LocalDateTime queryStartTime, LocalDateTime queryEndTime,
                                              LocalDateTime compareStartTime, LocalDateTime compareEndTime) {
        NumAndPerResp resp = new NumAndPerResp();
        // 指定时间内新增设备数量
        Integer newestIncrNum = deviceMapper.deviceOnlineNumWithDay(identList, String.valueOf(queryStartTime), String.valueOf(queryEndTime));
        // 比较时间的新增设备数量
        Integer priorIncrNum = deviceMapper.deviceOnlineNumWithDay(identList, String.valueOf(compareStartTime), String.valueOf(compareEndTime));
        resp.setNum(newestIncrNum);
        resp.setPer(commonStatictisService.calculatePercent(newestIncrNum, priorIncrNum));
        return resp;
    }

    /**
     * 获取时间范围内，每天的日期和在线设备数量
     * @param identList
     * @param queryStartTime
     * @param queryEndTime
     * @return
     */
    public RangeNumAndDayResp queryDeviceRangeOnlineNumAndDay(List<String> identList, LocalDateTime queryStartTime,
                                                        LocalDateTime queryEndTime) {
        // 获取时间范围内，日期和设备总数
        Map<String, Integer> map = deviceMapper.deviceOnlineNumWithDate(identList, String.valueOf(queryStartTime),
                String.valueOf(queryEndTime)).stream().collect(Collectors.toMap(DateAmount::getDate, DateAmount::getCount));
        return commonStatictisService.analyzeNumAndDay(map, queryStartTime, queryEndTime);
    }


    /**
     * 获取设备在线数据:数量，百分比，时间范围内的数据
     * @return
     */
    public AmountStatisticPartition queryDeviceActiveData(List<String> identList, LocalDateTime queryStartTime, LocalDateTime queryEndTime,
                                                        LocalDateTime compareStartTime, LocalDateTime compareEndTime) {
        // 查询设备活跃数量和百分比
        NumAndPerResp numAndPer = queryDeviceActiveNumAndPer(identList, queryStartTime.minusDays(2), queryEndTime, compareStartTime, compareEndTime);
        // 如果查询的开始结束时间是同一天，说明是按日查询,修改开始时间至六天前,显示近七天的数据
        if (0 == LocalDateTimeUtil.between(queryStartTime, queryEndTime).toDays()) {
            queryStartTime = queryStartTime.minusDays(6);
        }
        // 查询时间范围内每天的数据
        RangeNumAndDayResp rangeNumAndDay = queryDeviceRangeActiveNumAndDay(identList, queryStartTime.minusDays(2), queryEndTime);
        // 封装数据并返回
        return AmountStatisticPartition.partitionConvert(numAndPer.getNum(), numAndPer.getPer(), rangeNumAndDay);
    }

    /**
     * 设备在线数量和百分比
     * @return
     */
    public NumAndPerResp queryDeviceActiveNumAndPer(List<String> identList, LocalDateTime queryStartTime, LocalDateTime queryEndTime,
                                            LocalDateTime compareStartTime, LocalDateTime compareEndTime) {
        NumAndPerResp resp = new NumAndPerResp();
        // 连续三天为活跃，因此需要从开始时间的前两天开始统计
        // 指定时间内新增设备数量
        Integer newestIncrNum = deviceMapper.deviceActiveNumWithDay(identList, String.valueOf(queryStartTime),
                String.valueOf(queryEndTime));
        // 比较时间的新增设备数量
        Integer priorIncrNum = deviceMapper.deviceActiveNumWithDay(identList, String.valueOf(compareStartTime),
                String.valueOf(compareEndTime));
        resp.setNum(newestIncrNum);
        resp.setPer(commonStatictisService.calculatePercent(newestIncrNum, priorIncrNum));
        return resp;
    }

    /**
     * 获取时间范围内，每天的日期和活跃设备数量
     * @param identList
     * @param queryStartTime
     * @param queryEndTime
     * @return
     */
    public RangeNumAndDayResp queryDeviceRangeActiveNumAndDay(List<String> identList, LocalDateTime queryStartTime,
                                                        LocalDateTime queryEndTime) {
        // 获取时间范围内，日期和设备总数
        Map<String, Integer> map = deviceMapper.deviceActiveNumWithDate(identList, String.valueOf(queryStartTime),
                String.valueOf(queryEndTime)).stream().collect(Collectors.toMap(DateAmount::getDate, DateAmount::getCount));
        return commonStatictisService.analyzeNumAndDay(map, queryStartTime.minusDays(-2), queryEndTime);
    }


    /**
     * 统计各个地区的设备数量
     * @param req
     * @return
     */
    public List<PartitionAmountResp> deviceRegionStatistics(DeviceStatisticReq req) {
        // 分析获取查询的起止时间范围
        DateParam dateParam = DateUtils.analyzeDateParam(req.getSelectDate(), req.getType());

        // 获取设备地址列表
        List<String> identList = deviceService.getDeviceIdentsByInstitution(req.getInstitutionId() == null ? null : req.getInstitutionId());
        // 进一步根据其他进行筛选查询
        identList = deviceMapper.selectByCodeAndDate(identList, String.valueOf(dateParam.getEndTime()),
                StringUtils.isBlank(req.getDeviceTypeCode()) ? null : req.getDeviceTypeCode());
        if (CollectionUtils.isEmpty(identList)) {
            return new ArrayList<>();
        }
        List<PartitionAmountResp> partitionAmountResps = deviceMapper.selectDeviceRegion(identList, identList.size());
        log.info("查询设备地区分布数据:{}", partitionAmountResps);
        return partitionAmountResps;
    }

    /**
     * 统计各个地区的设备数据：总数量、新增数量、在线数量、活跃数量
     * @param req
     * @return
     */
    public PageData deviceStatisticByRegion(DeviceStatisticReq req) {
        // 分析获取查询的起止时间范围
        DateParam dateParam = DateUtils.analyzeDateParam(req.getSelectDate(), req.getType());
        // 获取设备地址列表
        List<String> identList = deviceService.getDeviceIdentsByInstitution(req.getInstitutionId() == null ? null : req.getInstitutionId());
        // 进一步根据其他进行筛选查询
        identList = deviceMapper.selectByCodeAndDate(identList, String.valueOf(dateParam.getEndTime()),
                StringUtils.isBlank(req.getDeviceTypeCode()) ? null : req.getDeviceTypeCode());
        if (CollectionUtils.isEmpty(identList)) {
            return null;
        }

        PageHelper.startPage(req.getPage(), req.getSize());
        // 查询会员信息
        List<StatisticByPartitionResp> respList = deviceMapper.queryDeviceStatisticByRegion(identList,
                String.valueOf(dateParam.getStartTime()), String.valueOf(dateParam.getEndTime()));
        PageInfo<StatisticByPartitionResp> pageInfo = new PageInfo<>(respList);
        log.info("查询各地区设备统计数据:{}", respList);
        PageData pageData = new PageData(respList, pageInfo.getTotal(), req.getPage(), pageInfo.getPages());
        return pageData;
    }

}
