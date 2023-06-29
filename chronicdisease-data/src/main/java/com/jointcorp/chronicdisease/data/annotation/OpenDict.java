package com.jointcorp.chronicdisease.data.annotation;

import java.lang.annotation.*;

/**
 * @Author zHuH1
 * @Date 2023/4/19 11:56
 **/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OpenDict {

    String description() default "";

}
