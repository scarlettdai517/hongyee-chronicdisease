package com.jointcorp.chronicdisease.platform.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * @Author: Xu-xg
 * @CreateTime: 2023-04-21 09:13
 */
@Configuration
public class CaffeineConfig {

    @Bean
    public Cache<String,Object> caffeineCache() {
        return Caffeine.newBuilder()
                .initialCapacity(32)
                //最大条数
                .maximumSize(128)
                //在最后一次写入缓存后开始计时，在指定的时间后过期。
                .expireAfterWrite(20, TimeUnit.MINUTES)
                .build();
    }

}
