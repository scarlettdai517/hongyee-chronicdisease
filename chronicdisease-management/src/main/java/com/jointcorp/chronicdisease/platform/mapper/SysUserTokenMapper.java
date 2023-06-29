package com.jointcorp.chronicdisease.platform.mapper;

import com.jointcorp.chronicdisease.data.po.SysUserToken;
import com.jointcorp.chronicdisease.platform.base.BaseMapper;
import org.springframework.stereotype.Component;

@Component
public interface SysUserTokenMapper extends BaseMapper<SysUserToken> {

    /**
     * 保存token，如果存在就更新，不存在就新增
     * @param userToken
     * @return
     */
    int insertToken(SysUserToken userToken);

}
