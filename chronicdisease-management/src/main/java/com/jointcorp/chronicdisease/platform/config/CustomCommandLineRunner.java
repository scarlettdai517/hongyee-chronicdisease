package com.jointcorp.chronicdisease.platform.config;

import com.jointcorp.chronicdisease.platform.cache.PushUrlCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * @Author: Xu-xg
 * @CreateTime: 2023-06-19 14:31
 */
@Component
public class CustomCommandLineRunner implements CommandLineRunner {

    @Autowired
    private PushUrlCache pushUrlCache;

    private Logger logger = LoggerFactory.getLogger(PushUrlCache.class);

    @Override
    public void run(String... args) throws Exception {
        logger.info("开始缓存推送地址...");
        pushUrlCache.cachePushUrl();
        logger.info("缓存推送地址完成...");
    }

}
