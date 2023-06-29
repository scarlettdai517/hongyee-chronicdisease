package com.jointcorp.chronicdisease.platform.mapper;

import com.jointcorp.chronicdisease.data.resp.resourceresp.institution.InsLocInfoByPage;
import com.jointcorp.chronicdisease.data.resp.resourceresp.institution.InsLocPer;
import com.jointcorp.chronicdisease.data.resp.resourceresp.institution.InsNumIncr;
import com.jointcorp.chronicdisease.data.po.Institution;
import com.jointcorp.chronicdisease.data.req.institutionReq.ValidInsReq;
import com.jointcorp.chronicdisease.platform.base.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
public interface InstitutionMapper extends BaseMapper<Institution> {

    List<Institution> selectByCreateTimeInstitutionList (LocalDateTime createTime);

    List<Institution> selectByNameAndProvince (@Param("institutionName") String institutionName, @Param("provinceName")String provinceName);

    List<Institution> selectByNameAndTime (@Param("institutionName")String institutionName, @Param("createTime")LocalDateTime createTime);

    List<Institution> selectByProvinceAndTime (@Param("provinceName")String provinceName,@Param("createTime")LocalDateTime createTime );

    //查给定当天的机构总数
    int selectAllInsAmount(@Param("localDate")LocalDate localDate, @Param("validIds")List<Long> validIds);


    List<InsNumIncr> selectInstNumTotal(ValidInsReq validInsReq);

    /**
     * 查询机构下的子机构
     * @param list 当前要查询的机构id集合
     * @return
     */
    List<Institution> selectSubInst(@Param("list") List<Long> list);

    List<Long> selectByCreateDay (@Param("monday") LocalDate monday,@Param("sunday") LocalDate sunday);

    //当天新增机构数量
    int selectIncrInsAmount(@Param("localDate")LocalDate localDate, @Param("validIds")List<Long> validIds);

    List<InsLocPer> selectInsLocInfo(@Param("validIds")List<Long> validIds);


    List<InsLocPer> selectInsLocIncr(@Param("localDate")LocalDate localDate, @Param("validIds")List<Long> validIds);

    List<Long> selectSuperiorInst(@Param("institutionId") Long institutionId);


    List<Institution> selectInstIdAndName(@Param("list") List<Long> list);
}
