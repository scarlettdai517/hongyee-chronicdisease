package com.jointcorp.chronicdisease.data.resp.memberResp;

import com.jointcorp.chronicdisease.data.resp.statictisresp.PartitionAmountResp;
import com.jointcorp.chronicdisease.data.resp.statictisresp.StatisticByPartitionResp;
import lombok.Data;

import java.util.List;

/**
 * 按年龄段统计会员数据
 *
 * @Author zHuH1
 * @Date 2023/6/16 16:35
 **/
@Data
public class MemberDataByAgeResp {

    List<PartitionAmountResp> partitionAmountResps;

    List<StatisticByPartitionResp> statisticByPartitionResps;

}
