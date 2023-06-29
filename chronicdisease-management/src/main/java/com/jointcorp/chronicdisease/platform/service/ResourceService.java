package com.jointcorp.chronicdisease.platform.service;

import com.jointcorp.chronicdisease.data.po.Resource;
import com.jointcorp.chronicdisease.data.req.resourceReq.NewResourceAddReq;
import com.jointcorp.chronicdisease.data.req.resourceReq.ResourceUpdateReq;
import com.jointcorp.chronicdisease.platform.mapper.ResourceMapper;
import com.jointcorp.common.util.SnowflakeIdWorker;
import com.jointcorp.parent.result.ResultData;
import com.jointcorp.parent.result.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ResourceService {

    @Autowired
    private ResourceMapper resourceMapper;

    @Autowired
    private SnowflakeIdWorker snowflakeIdWorker;

    /**
     * 添加资源
     * @param req
     * @return
     */
    public ResultData add(NewResourceAddReq req) {
        Resource resource = req.convert();
        resource.setCreateTime(LocalDateTime.now());
        resource.setUpdateTime(LocalDateTime.now());
        resource.setResourceId(snowflakeIdWorker.nextId(false));
        resourceMapper.insert(resource);
        return ResultUtil.success();
    }

    /**
     * 更新资源
     * @param req
     * @return
     */
    public ResultData update(ResourceUpdateReq req) {
        Resource resource = req.convert();
        resource.setUpdateTime(LocalDateTime.now());
        resourceMapper.updateByPrimaryKeySelective(resource);
        return ResultUtil.success();
    }

    /**
     * 删除资源
     * @param resourceId
     * @return
     */
    public ResultData delResource(String resourceId) {
        resourceMapper.deleteByPrimaryKey(Long.parseLong(resourceId));
        return ResultUtil.success();
    }
}
