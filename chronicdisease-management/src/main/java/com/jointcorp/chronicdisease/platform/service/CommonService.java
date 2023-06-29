package com.jointcorp.chronicdisease.platform.service;

import com.jointcorp.chronicdisease.data.po.AddressPrefix;
import com.jointcorp.chronicdisease.platform.mapper.AddressPrefixMapper;
import com.jointcorp.parent.result.ResultData;
import com.jointcorp.parent.result.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * @Author: Yulin Dai
 * @CreateTime: 2023-04-26 17:39
 */
@Service
public class CommonService {

    @Autowired
    private AddressPrefixMapper commonServiceMapper;


    //通过选择的父ID，筛选子地址集合返回（返回省需要父ID为0）
    public ResultData queryAddress(AddressPrefix addressPrefix) {

        Long parentId = addressPrefix.getId();

        Example example = new Example(AddressPrefix.class);
        example.createCriteria().andEqualTo("parentId", parentId);

        List<AddressPrefix> addressList = commonServiceMapper.selectByExample(example);

        return ResultUtil.success(addressList);
    }

}