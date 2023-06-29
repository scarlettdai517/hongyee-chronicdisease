package com.jointcorp.chronicdisease.platform.service;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.jointcorp.chronicdisease.data.resp.statictisresp.RangeNumAndDayResp;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author zHuH1
 * @Date 2023/6/15 9:31
 **/
@Service
public class CommonStatictisService {

    /**
     * 获取时间范围内，每天的日期和设备总数
     * @param map
     * @param startTime
     * @param endTime
     * @return
     */
    public RangeNumAndDayResp analyzeIncrNumAndDay(Map<String, Integer> map, Integer priorAmount,
                                                   LocalDateTime startTime, LocalDateTime endTime) {
        RangeNumAndDayResp resp = new RangeNumAndDayResp();
        List<String> dayList = new ArrayList<>();
        List<Integer> numList = new ArrayList<>();

        // 获取时间范围内的天数
        Duration between = LocalDateTimeUtil.between(startTime, endTime);
        int size = (int) between.toDays();
        LocalDate startDay = LocalDate.from(startTime);
        // 日期数组
        for (int i = 0; i <= size; i++) {
            // i天前的日期
            String date = startDay.minusDays(-i).toString();
            dayList.add(date);

            // 以日期作为key,获取这一天的value值
            Integer incrAmount = map.get(date) == null ? 0 : map.get(date);
            // 每天的设备总数 = 前天的设备总数 + 当天新增的设备数量
            priorAmount += incrAmount;
            numList.add(priorAmount);
        }
        resp.setNums(numList);
        resp.setDays(dayList);
        return resp;
    }

    /**
     * 百分比计算
     * @param newestNum
     * @param priorNum
     * @return
     */
    public String calculatePercent(Integer newestNum, Integer priorNum) {
        // 数量百分比
        if (priorNum == 0) {
            return newestNum * 100 + "%";
        } else if (newestNum == 0) {
            return priorNum * -100 + "%";
        } else {
            // 百分比,保留一位小数
            BigDecimal onlinePer = new BigDecimal((double) (newestNum - priorNum) / priorNum + "")
                    .setScale(1, RoundingMode.HALF_UP);
            return onlinePer.movePointRight(2) + "%";
        }
    }

    /**
     * 公共方法:
     * 获取时间范围内，每天的日期和数量
     * @param map
     * @param startTime
     * @param endTime
     * @return
     */
    public RangeNumAndDayResp analyzeNumAndDay(Map<String, Integer> map, LocalDateTime startTime, LocalDateTime endTime) {
        RangeNumAndDayResp resp = new RangeNumAndDayResp();
        List<String> dayList = new ArrayList<>();
        List<Integer> numList = new ArrayList<>();

        // 获取时间范围内的天数
        Duration between = LocalDateTimeUtil.between(startTime, endTime);
        int size = (int) between.toDays();
        LocalDate startDay = LocalDate.from(startTime);
        // 日期数组
        for (int i = 0; i <= size; i++) {
            // i天前的日期
            String date = startDay.minusDays(-i).toString();
            dayList.add(date);
            // 以日期作为key,获取这一天的value值
            numList.add(map.get(date) == null ? 0 : map.get(date));
        }
        resp.setNums(numList);
        resp.setDays(dayList);
        return resp;
    }
}
