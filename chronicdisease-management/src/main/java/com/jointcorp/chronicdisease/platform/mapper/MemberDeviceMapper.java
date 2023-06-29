package com.jointcorp.chronicdisease.platform.mapper;

import com.jointcorp.chronicdisease.data.po.MemberDevice;
import com.jointcorp.chronicdisease.data.resp.deviceresp.MemberDeviceInfoResp;
import com.jointcorp.chronicdisease.platform.base.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author zHuH1
 * @Date 2023/5/5 16:48
 **/
@Component
public interface MemberDeviceMapper extends BaseMapper<MemberDevice> {

    /**
     * 查询会员与设备绑定信息
     * @param memberId
     * @return
     */
    List<MemberDeviceInfoResp> queryMemberDeviceInfo(@Param("memberId") Long memberId);

    /**
     * 批量插入机构与会员绑定记录
     * @param list
     * @return
     */
    int batchInsertList(@Param("list") List<MemberDevice> list);

    int updateBindingEndTime(@Param("deviceIdent") String deviceIdent);

    int batchUpdateBindingEndTime(@Param("deviceIdentList") List<String> deviceIdentList);

}
