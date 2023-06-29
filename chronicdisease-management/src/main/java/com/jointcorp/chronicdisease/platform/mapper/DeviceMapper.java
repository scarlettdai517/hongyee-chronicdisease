package com.jointcorp.chronicdisease.platform.mapper;

import com.jointcorp.chronicdisease.data.po.device.statistics.DateAmount;
import com.jointcorp.chronicdisease.data.resp.statictisresp.PartitionAmountResp;
import com.jointcorp.chronicdisease.data.po.device.Device;
import com.jointcorp.chronicdisease.data.req.deviceReq.DeviceQueryReq;
import com.jointcorp.chronicdisease.data.resp.deviceresp.DeviceListResp;
import com.jointcorp.chronicdisease.data.resp.statictisresp.StatisticByPartitionResp;
import com.jointcorp.chronicdisease.platform.base.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author zHuH1
 * @Date 2023/5/5 16:48
 **/
@Component
public interface DeviceMapper extends BaseMapper<Device> {

    /**
     * 查询设备列表
     * @param req
     * @param deviceIdents
     * @return
     */
    List<DeviceListResp> selectDeviceList(@Param("req") DeviceQueryReq req, @Param("deviceIdents") List<String> deviceIdents);

    /**
     * 查询某天以来新增的设备数量
     * @param deviceIdents
     * @param day
     * @return
     */
    Integer IncrDeviceCountFromDay(@Param("deviceIdents") List<String> deviceIdents, @Param("localDateTime") LocalDateTime day);

    /**
     * 截止至指定时间的设备总数
     * @param deviceIdents
     * @param endTime
     * @return
     */
    Integer deviceTotalNumWithDay(@Param("deviceIdents") List<String> deviceIdents, @Param("endTime") String endTime);

//    /**
//     * 每天的设备数量
//     * @param deviceIdents
//     * @param startTime
//     * @param endTime
//     * @return
//     */
//    List<DateAmount> deviceAmountWithDate(@Param("deviceIdents") List<String> deviceIdents, @Param("startTime") String startTime,
//                                          @Param("endTime") String endTime);

    /**
     * 时间范围内新增的设备总数
     * @param deviceIdents
     * @param endTime
     * @return
     */
    Integer deviceIncrNumWithDay(@Param("deviceIdents") List<String> deviceIdents, @Param("startTime") String startTime,
                                  @Param("endTime") String endTime);


    /**
     * 时间范围内，每天新增的设备数量
     * @param deviceIdents
     * @param startTime
     * @param endTime
     * @return
     */
    List<DateAmount> deviceIncrNumWithDate(@Param("deviceIdents") List<String> deviceIdents, @Param("startTime") String startTime,
                                   @Param("endTime") String endTime);


    /**
     * 根据设备类型查询设备列表
     * @param deviceIdents
     * @param deviceTypeCode
     * @return
     */
    List<String> selectByCodeAndDate(@Param("deviceIdents") List<String> deviceIdents,
                                     @Param("queryEndTime") String queryEndTime,
                                     @Param("deviceTypeCode") String deviceTypeCode);

    /**
     * 时间范围内,总在线设备数量
     * @param deviceIdents
     * @param startTime
     * @param endTime
     * @return
     */
    Integer deviceOnlineNumWithDay(@Param("deviceIdents") List<String> deviceIdents, @Param("startTime") String startTime,
                                      @Param("endTime")String endTime);

    /**
     * 时间范围内,每天在线设备数量
     * @param deviceIdents
     * @param startTime
     * @param endTime
     * @return
     */
    List<DateAmount> deviceOnlineNumWithDate(@Param("deviceIdents") List<String> deviceIdents,
                                             @Param("startTime") String startTime,
                                             @Param("endTime")String endTime);


    /**
     * 时间范围内,总活跃设备数量
     * @param deviceIdents
     * @param startTime
     * @param endTime
     * @return
     */
    Integer deviceActiveNumWithDay(@Param("deviceIdents") List<String> deviceIdents,
                                   @Param("startTime") String startTime,
                                   @Param("endTime")String endTime);

    /**
     * 时间范围内,每天的活跃设备数量
     * @param deviceIdents
     * @param startTime
     * @param endTime
     * @return
     */
    List<DateAmount> deviceActiveNumWithDate(@Param("deviceIdents") List<String> deviceIdents,
                                               @Param("startTime") String startTime,
                                               @Param("endTime")String endTime);


    /**
     * 统计各地区的设备数量
     * @param deviceIdents
     * @return
     */
    List<PartitionAmountResp> selectDeviceRegion(@Param("deviceIdents") List<String> deviceIdents, @Param("total") Integer total);

    /**
     * 统计各地区的设备数据：总数量、新增数量、在线数量、活跃数量
     * @param deviceIdents
     * @param startTime
     * @param endTime
     * @return
     */
    List<StatisticByPartitionResp> queryDeviceStatisticByRegion(@Param("deviceIdents") List<String> deviceIdents,
                                                                @Param("startTime") String startTime,
                                                                @Param("endTime")String endTime);


    /**
     * 批量插入设备数据
     * @param deviceList
     * @return
     */
    int insertDeviceList(@Param("deviceList") List<Device> deviceList);

}
