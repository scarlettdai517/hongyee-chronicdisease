package com.jointcorp.chronicdisease.platform.utils;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.jointcorp.chronicdisease.data.po.DateParam;
import com.jointcorp.chronicdisease.data.po.device.statistics.DateCollection;
import com.jointcorp.common.util.DateTimeUtil;
import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;

/**
 * @Author zHuH1
 * @Date 2023/5/12 9:08
 **/
@Slf4j
public class DateUtils {

    /**
     * 计算统计数据的查询范围时间、比较范围时间
     * @param selectDate
     * @param type
     * @param type
     * @return
     */
    public static DateCollection getDateCollection(String selectDate, Integer type) {
        DateCollection dateCollection = new DateCollection();
        // 分析获取查询的起止时间范围
        DateParam dateParam = DateUtils.analyzeDateParam(selectDate, type);
        LocalDateTime queryStartTime = dateParam.getStartTime();
        LocalDateTime queryEndTime = dateParam.getEndTime();
        dateCollection.setQueryStartDateTime(queryStartTime);
        dateCollection.setQueryEndDateTime(queryEndTime);
        if (2 == type) {
            // 按周查询,比较范围:上周
            dateCollection.setCompareStartDateTime(queryStartTime.minusDays(7));
            dateCollection.setCompareEndDateTime(queryEndTime.minusDays(7));
        } else if (3 == type) {
            // 按月查询,比较范围:上个月
            dateCollection.setCompareStartDateTime(queryStartTime.minusMonths(1));
            // 获取时间范围内的天数
            Duration between = LocalDateTimeUtil.between(queryStartTime, queryEndTime);
            int size = (int) between.toDays();
            dateCollection.setCompareEndDateTime(queryEndTime.minusDays(size + 1));
        } else {
            // 按日查询,比较范围:昨天
            dateCollection.setCompareStartDateTime(queryStartTime.minusDays(1));
            dateCollection.setCompareEndDateTime(queryEndTime.minusDays(1));
        }
        return dateCollection;
    }


    /**
     * 分析时间请求参数类型，获取范围
     * 1:按日; 2:按周; 3:按月
     * @param date
     * @param type
     * @return
     */
    public static DateParam analyzeDateParam(String date, Integer type) {
        DateParam dateParam = new DateParam();
        try {
            if (2 == type) {
                // 按周
                LocalDate weekFirstDay = LocalDate.parse(date.substring(0, 10));
                LocalDate weekLastDay = LocalDate.parse(date.substring(11));
                dateParam.setStartTime(LocalDateTime.of(weekFirstDay, LocalTime.MIN));
                dateParam.setEndTime(LocalDateTime.of(weekLastDay, LocalTime.MAX));
            } else if (3 == type) {
                // 按月
                LocalDate selectDay = LocalDate.parse(date + "-01", DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                LocalDate monthFirstDay = selectDay.with(TemporalAdjusters.firstDayOfMonth());
                LocalDate monthLastDay = selectDay.with(TemporalAdjusters.lastDayOfMonth());
                dateParam.setStartTime(LocalDateTime.of(monthFirstDay, LocalTime.MIN));
                dateParam.setEndTime(LocalDateTime.of(monthLastDay, LocalTime.MAX));
            } else {
                // 按日
                LocalDate selectDay = LocalDate.parse(date);
                dateParam.setStartTime(LocalDateTime.of(selectDay, LocalTime.MIN));
                dateParam.setEndTime(LocalDateTime.of(selectDay, LocalTime.MAX));
            }
        } catch (Exception e) {
            log.error("时间请求参数转换失败:{}", e.getMessage());
            e.printStackTrace();
        }
        return dateParam;
    }

    /**
     * 获取x天前的凌晨开始时间
     * @param day
     * @return
     */
    public static LocalDateTime getStartTimeBeforeDay(String date, Integer day) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(sdf.parse(date));
            calendar.add(Calendar.DATE, -day);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return DateTimeUtil.parseLocalDateTime(calendar.getTime());
    }


    /**
     * 获取x天前的凌晨开始时间
     * @param day
     * @return
     */
    public static LocalDateTime getEndTimeBeforeDay(String date, Integer day) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(sdf.parse(date));
            calendar.add(Calendar.DATE, -day);
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return DateTimeUtil.parseLocalDateTime(calendar.getTime());
    }

//    /**
//     * 获取x天前的凌晨开始时间
//     * @param day
//     * @return
//     */
//    public static LocalDateTime getDayStartTime(LocalDate day) {
////        return DateTimeUtil.parseLocalDateTime(day.toString() + " 23:59:59", "yyyy-MM-dd HH:mm:ss");
//        return LocalDateTime.of(day, LocalTime.MAX);
//    }
//
//    /**
//     * 获取x天前的凌晨开始时间
//     * @param day
//     * @return
//     */
//    public static LocalDateTime getDayEndTime(LocalDate day) {
////        return DateTimeUtil.parseLocalDateTime(day.toString() + " 23:59:59", "yyyy-MM-dd HH:mm:ss");
//        return LocalDateTime.of(day, LocalTime.MAX);
//    }

}
