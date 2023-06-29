package com.jointcorp.chronicdisease.platform.mapper;

import com.jointcorp.chronicdisease.data.po.Resource;
import com.jointcorp.chronicdisease.platform.base.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface ResourceMapper  extends BaseMapper<Resource> {

    List<Resource> selectResource(@Param("userId") String userId);

    List<Long> selectResourceId(@Param("userId") String userId);

    List<Resource> selectResourceById(@Param("list") List<Long> list);

    int deleteResources (String userId);

    /**
     * 批量插入
     * @param userId
     * @param list
     * @return
     */
    int insertResources (@Param("userId") String userId,@Param("list") List<String> list);

    /**
     * 查询默认的资源
     * @param userId
     * @return
     */
    List<Long> selectResourceByPub();
}
