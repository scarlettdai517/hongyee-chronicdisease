package com.jointcorp.chronicdisease.platform.controller;

import com.jointcorp.chronicdisease.data.po.AddressPrefix;
import com.jointcorp.chronicdisease.platform.config.AppConfig;
import com.jointcorp.chronicdisease.platform.interceptor.support.UserTokenUtil;
import com.jointcorp.chronicdisease.platform.service.CommonService;
import com.jointcorp.common.util.RandomUtil;
import com.jointcorp.parent.result.ResultData;
import com.jointcorp.parent.result.ResultUtil;
import com.jointcorp.parent.utils.AliyunOSSUtil;
import com.jointcorp.parent.utils.ErrorUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Yulin Dai
 * @CreateTime: 2023-04-27 10:10
 */

@RestController
@RequestMapping("/common")
public class CommonController {

    @Autowired
    private CommonService commonService;
    /**
     * 上传地址省市区前缀
     * @param addressPrefix
     * @return
     * @throws Exception
     */

    @GetMapping("queryAddressPrefix")
    public ResultData queryAddressPrefix(AddressPrefix addressPrefix) throws Exception{
        return commonService.queryAddress(addressPrefix);
    }

    /**
     * 上传文件
     * @param file
     * @return
     * @throws Exception
     */
    @PostMapping("/uploadFile")
    public ResultData uploadAvatar(MultipartFile file) throws Exception {
        long userId = UserTokenUtil.getUser().getUserId();
        if(!file.isEmpty()) {
            try {
                String fileName = file.getOriginalFilename();
                if(StringUtils.isNotBlank(fileName)) {
                    String newFileName = userId + "_" + RandomUtil.getRandom(111111111, 999999999) + fileName.substring(fileName.lastIndexOf("."));
                    InputStream is = file.getInputStream();
                    AliyunOSSUtil.write(is, AppConfig.ossDataFilePath + newFileName);
                    String url = AppConfig.fileBaseUrl + "/" + AppConfig.ossDataFilePath + newFileName;
                    Map<String,String> map = new HashMap<>();
                    map.put("url",url);
                    return ResultUtil.success(map);
                }
            } catch (Exception e) {
                ErrorUtil.record(e,userId+"");
            }
        }
        return ResultUtil.error();
    }


}
