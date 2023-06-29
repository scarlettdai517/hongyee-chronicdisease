package com.jointcorp.chronicdisease.platform.service;

import com.jointcorp.chronicdisease.data.po.InstitutionMember;
import com.jointcorp.chronicdisease.platform.mapper.InstitutionMemberMapper;
import com.jointcorp.common.util.SnowflakeIdWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author zHuH1
 * @Date 2023/5/19 16:05
 **/
@Slf4j
@Service
public class InstitutionMemberService {

    @Autowired
    private SnowflakeIdWorker snowflakeIdWorker;

    @Autowired
    private InstitutionMemberMapper institutionMemberMapper;

    /**
     * 根据机构id列表查询会员id
     * @param institutionIdList
     * @return
     */
    public List<Long> queryInstitutionMemberList(List<Long> institutionIdList) {
        Example example = new Example(InstitutionMember.class);
        example.createCriteria().andIn("institutionId", institutionIdList);
        List<InstitutionMember> institutionMemberList = institutionMemberMapper.selectByExample(example);
        if (!CollectionUtils.isEmpty(institutionMemberList)) {
            return institutionMemberList.stream().map(InstitutionMember::getMemberId).distinct().collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * 保存机构与用户关系记录
     *
     * @param memberId
     * @param institutionId
     */
    public void saveInstitutionMember(Long memberId, Long institutionId) {
        //绑定用户与机构关系记录表
        Example example = new Example(InstitutionMember.class);
        example.createCriteria().andEqualTo("memberId", memberId)
                .andEqualTo("institutionId", institutionId);
        //判断是否已存在绑定记录
        if (institutionMemberMapper.selectOneByExample(example) == null) {
            insertMechanismUser(memberId, institutionId);
        }
    }

    /**
     * 存入数据库
     * @param memberId
     * @param institutionId
     */
    public void insertMechanismUser(Long memberId, Long institutionId) {
        InstitutionMember institutionMember = new InstitutionMember();
        institutionMember.setInstitutionMemberId(snowflakeIdWorker.nextId());
        institutionMember.setMemberId(memberId);
        institutionMember.setInstitutionId(institutionId);
        institutionMember.setCreateTime(LocalDateTime.now());
        institutionMember.setUpdateTime(LocalDateTime.now());
        log.info("新增机构与会员绑定记录:{}", institutionMember);
        institutionMemberMapper.insert(institutionMember);
    }
}
