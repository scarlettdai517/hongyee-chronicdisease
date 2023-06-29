package com.jointcorp.chronicdisease.platform.aop;

import com.jointcorp.chronicdisease.platform.service.DeviceDictService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 数据字典切面
 *
 * @Author zHuH1
 * @Date 2023/5/6 15:09
 **/
@Slf4j
@Aspect
@Component
public class DictAspect {

    @Autowired
    private DeviceDictService deviceDictService;

    @Pointcut("@annotation(com.jointcorp.chronicdisease.data.annotation.OpenDict)")
    public void openDict() {
    }

    @AfterReturning(pointcut = "openDict()", returning = "result")
    public void doAfterReturning(Object result) {
        try {
            deviceDictService.convertDict(result);
        } catch (Exception e) {
            log.error("设备查询结果字典转换失败", e);
        }
    }

}

