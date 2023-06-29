package com.jointcorp.chronicdisease.platform.mapper;

import com.jointcorp.chronicdisease.data.po.InstitutionMember;
import com.jointcorp.chronicdisease.platform.base.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author zHuH1
 * @Date 2023/5/5 16:48
 **/
@Component
public interface InstitutionMemberMapper extends BaseMapper<InstitutionMember> {

    /**
     * 批量插入机构与会员绑定记录
     * @param list
     * @return
     */
    int insertBatchList(@Param("list") List<InstitutionMember> list);

}
