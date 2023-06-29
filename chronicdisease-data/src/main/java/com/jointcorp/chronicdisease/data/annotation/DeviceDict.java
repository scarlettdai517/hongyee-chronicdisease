package com.jointcorp.chronicdisease.data.annotation;

import java.lang.annotation.*;


/**
 * 数据字典注解
 *
 * @Author zHuH1
 * @Date 2023/5/6 14:16
 **/
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DeviceDict {

    /**
     * 字典编码
     */
    String dictName();

    /**
     * 实体类内对应的字段名称
     * 格式为："字段名 + Text"
     */
    String dictField() default "";

}
