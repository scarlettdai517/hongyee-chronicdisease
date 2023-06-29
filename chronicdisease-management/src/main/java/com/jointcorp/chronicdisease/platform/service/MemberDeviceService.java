package com.jointcorp.chronicdisease.platform.service;

import com.jointcorp.chronicdisease.data.po.MemberDevice;
import com.jointcorp.chronicdisease.data.req.deviceReq.ChangeMemberReq;
import com.jointcorp.chronicdisease.data.req.memberReq.personalDataReq;
import com.jointcorp.chronicdisease.data.resp.deviceresp.MemberDeviceInfoResp;
import com.jointcorp.chronicdisease.platform.mapper.MemberDeviceMapper;
import com.jointcorp.common.util.SnowflakeIdWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author zHuH1
 * @Date 2023/5/26 10:05
 **/
@Slf4j
@Service
public class MemberDeviceService {

    @Autowired
    private MemberDeviceMapper memberDeviceMapper;
    @Autowired
    private SnowflakeIdWorker snowflakeIdWorker;

    /**
     * 根据会员id查询会员与设备绑定数据
     * @param memberId
     * @return
     */
    public List<MemberDeviceInfoResp> queryMemberDeviceInfo(Long memberId) {
        return memberDeviceMapper.queryMemberDeviceInfo(memberId);
    }

    /**
     * 插入会员设备记录
     * @param req
     * @return
     */
    public int insertMemberDeviceRecord(ChangeMemberReq req) {
        // 处理上一条记录，若结束时间不为空则设置当前时间
        log.info("处理上一条会员设备记录:{}", req);
        memberDeviceMapper.updateBindingEndTime(req.getDeviceIdent());
        // 存入新的记录
        MemberDevice memberDevice = new MemberDevice();
        memberDevice.setMemberDeviceId(snowflakeIdWorker.nextId());
        memberDevice.setMemberId(req.getMemberId());
        memberDevice.setMemberName(req.getMemberName());
        memberDevice.setDeviceIdent(req.getDeviceIdent());
        memberDevice.setStartTime(LocalDateTime.now());
        memberDevice.setEndTime(null);
        memberDevice.setCreateTime(LocalDateTime.now());
        memberDevice.setUpdateTime(LocalDateTime.now());
        log.info("保存会员设备记录:{}", memberDevice);
        return memberDeviceMapper.insert(memberDevice);
    }


    /**
     * 批量插入会员设备记录
     * @param memberDeviceList
     * @return
     */
    public int batchInsertList(List<MemberDevice> memberDeviceList) {
        // 批量处理设备上一条绑定记录
        if (!CollectionUtils.isEmpty(memberDeviceList)) {
            memberDeviceMapper.batchUpdateBindingEndTime(memberDeviceList.stream().map(MemberDevice::getDeviceIdent).collect(Collectors.toList()));
        }
        // 批量插入
        return memberDeviceMapper.batchInsertList(memberDeviceList);
    }

    /**
     * 统计设备测量的各项数据
     * @param req
     * @return
     */
    public Object queryPersonalDeviceData(personalDataReq req) {
        return null;
    }
}
