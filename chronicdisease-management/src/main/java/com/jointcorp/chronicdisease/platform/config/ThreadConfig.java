package com.jointcorp.chronicdisease.platform.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Author zHuH1
 * @Date 2023/5/17 10:34
 **/
@Configuration
public class ThreadConfig {

    @Value("${thread.coreSize}")
    private Integer coreSize;
    @Value("${thread.maxSize}")
    private Integer maxSize;
    @Value("${thread.keepAliveTime}")
    private Integer keepAliveTime;
    @Value("${thread.dequeSize}")
    private Integer dequeSize;

    @Bean
    public ThreadPoolExecutor threadPoolExecutor() {
        return new ThreadPoolExecutor(coreSize, maxSize, keepAliveTime, TimeUnit.SECONDS, new LinkedBlockingDeque<>(dequeSize),
                Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());
    }

}
