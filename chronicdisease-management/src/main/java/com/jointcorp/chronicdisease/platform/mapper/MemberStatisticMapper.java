package com.jointcorp.chronicdisease.platform.mapper;

import com.jointcorp.chronicdisease.data.po.Member;
import com.jointcorp.chronicdisease.data.po.device.statistics.DateAmount;
import com.jointcorp.chronicdisease.data.resp.statictisresp.PartitionAmountResp;
import com.jointcorp.chronicdisease.data.resp.statictisresp.StatisticByPartitionResp;
import com.jointcorp.chronicdisease.platform.base.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 会员看板 - 会员数据统计
 *
 * @Author zHuH1
 * @Date 2023/5/5 16:48
 **/
@Component
public interface MemberStatisticMapper extends BaseMapper<Member> {

    /**
     * 查询指定时间之前的会员总数量
     * @param memberIdList
     * @param deviceTypeCode
     * @param queryEndTime
     * @return
     */
    Integer memberTotalNumWithDay(@Param("memberIdList") List<Long> memberIdList, @Param("deviceTypeCode") String deviceTypeCode,
                                  @Param("endTime") String queryEndTime);

    /**
     * 时间范围内，新增会员总数
     * @param memberIdList
     * @param deviceTypeCode
     * @param startTime
     * @param endTime
     * @return
     */
    Integer memberIncrNumWithDay(@Param("memberIdList") List<Long> memberIdList, @Param("deviceTypeCode") String deviceTypeCode,
                                 @Param("startTime") String startTime, @Param("endTime") String endTime);

    /**
     * 时间范围内，每天新增的会员数量
     * @param memberIdList
     * @param deviceTypeCode
     * @param startTime
     * @param endTime
     * @return
     */
    List<DateAmount> memberIncrNumWithDate(@Param("memberIdList") List<Long> memberIdList, @Param("deviceTypeCode") String deviceTypeCode,
                                           @Param("startTime") String startTime, @Param("endTime") String endTime);

    /**
     * 时间范围内,在线会员总数
     * @param memberIdList
     * @param deviceTypeCode
     * @param startTime
     * @param endTime
     * @return
     */
    Integer memberOnlineNumWithDay(@Param("memberIdList") List<Long> memberIdList, @Param("deviceTypeCode") String deviceTypeCode,
                                   @Param("startTime") String startTime, @Param("endTime")String endTime);

    /**
     * 时间范围内,每天在线会员数量
     * @param memberIdList
     * @param deviceTypeCode
     * @param startTime
     * @param endTime
     * @return
     */
    List<DateAmount> memberOnlineNumWithDate(@Param("memberIdList") List<Long> memberIdList, @Param("deviceTypeCode") String deviceTypeCode,
                                             @Param("startTime") String startTime, @Param("endTime")String endTime);


    /**
     * 时间范围内,会员设备总数
     * @param memberIdList
     * @param deviceTypeCode
     * @param startTime
     * @param endTime
     * @return
     */
    Integer memberActiveNumWithDay(@Param("memberIdList") List<Long> memberIdList, @Param("deviceTypeCode") String deviceTypeCode,
                                   @Param("startTime") String startTime, @Param("endTime")String endTime);

    /**
     * 时间范围内,每天的活跃会员数量
     * @param memberIdList
     * @param deviceTypeCode
     * @param startTime
     * @param endTime
     * @return
     */
    List<DateAmount> memberActiveNumWithDate(@Param("memberIdList") List<Long> memberIdList, @Param("deviceTypeCode") String deviceTypeCode,
                                             @Param("startTime") String startTime, @Param("endTime")String endTime);

    /**
     * 统计各年龄段的会员人数
     * @param memberIdList
     * @return
     */
    List<PartitionAmountResp> selectMemberAgeNum(@Param("memberIdList") List<Long> memberIdList, @Param("endTime")String endTime);

    /**
     * 统计各个年龄段的会员数据：总人数、新增人数、在线人数、活跃人数
     * @param memberIdList
     * @param deviceTypeCode
     * @param startTime
     * @param endTime
     * @return
     */
    List<StatisticByPartitionResp> queryMemberStatisticByAge(@Param("memberIdList") List<Long> memberIdList,
                                                             @Param("deviceTypeCode") String deviceTypeCode,
                                                             @Param("startTime") String startTime,
                                                             @Param("endTime")String endTime);
}
