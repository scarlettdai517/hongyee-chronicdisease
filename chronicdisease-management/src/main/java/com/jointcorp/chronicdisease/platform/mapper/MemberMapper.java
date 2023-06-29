package com.jointcorp.chronicdisease.platform.mapper;

import com.jointcorp.chronicdisease.data.po.Member;
import com.jointcorp.chronicdisease.data.req.memberReq.MemberQueryReq;
import com.jointcorp.chronicdisease.data.resp.memberResp.MemberQueryResp;
import com.jointcorp.chronicdisease.platform.base.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * @Author zHuH1
 * @Date 2023/5/5 16:48
 **/
@Component
public interface MemberMapper extends BaseMapper<Member> {

    /**
     * 查询会员数据
     * @param req
     * @param institutionIdList
     * @return
     */
    List<MemberQueryResp> queryMemberInfo(@Param("req") MemberQueryReq req, @Param("institutionIdList") List<Long> institutionIdList);


    /**
     * 根据机构查询会员id数组
     * @param institutionIdList
     * @return
     */
    List<Long> getMemberIdListByInst(@Param("institutionIdList") List<Long> institutionIdList);


    /**
     * 批量插入会员数据
     * @param memberList
     * @return
     */
    int insertMemberList(@Param("memberList") List<Member> memberList);

}
