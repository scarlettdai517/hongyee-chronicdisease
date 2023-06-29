package com.jointcorp.chronicdisease.platform.config;

import com.jointcorp.common.util.SnowflakeIdWorker;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Xu-xg
 * @date 2021/3/24 18:18
 */
@Configuration
@ConfigurationProperties(prefix = "appconf")
public class AppConfig {

    public static long workerId;
    public static String fileBaseUrl;
    public static String ossDataFilePath;
    public static String ossLogFilePath;
    //活跃时长(3天内登录过）
    public static int activePeriod;
    //在线时长
    public static int onlinePeriod;
    //token有效期，单位 秒
    public static int tokenExpire;

    @Bean
    public SnowflakeIdWorker snowflakeIdWorker() {
        return new SnowflakeIdWorker(workerId, 1);
    }

    public void setWorkerId(long workerId) {
        AppConfig.workerId = workerId;
    }

    public void setFileBaseUrl(String fileBaseUrl) {
        AppConfig.fileBaseUrl = fileBaseUrl;
    }

    public void setOssDataFilePath(String ossDataFilePath) {
        AppConfig.ossDataFilePath = ossDataFilePath;
    }

    public void setOssLogFilePath(String ossLogFilePath) {
        AppConfig.ossLogFilePath = ossLogFilePath;
    }

    public void setActivePeriod(int activePeriod) {
        AppConfig.activePeriod = activePeriod;
    }

    public void setOnlinePeriod(int onlinePeriod) {
        AppConfig.onlinePeriod = onlinePeriod;
    }

    //public void setTokenExpire(int tokenExpire) {
    //    AppConfig.tokenExpire = tokenExpire;
    //}
}