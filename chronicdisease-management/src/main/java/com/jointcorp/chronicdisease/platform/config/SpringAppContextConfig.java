package com.jointcorp.chronicdisease.platform.config;

import com.jointcorp.chronicdisease.platform.interceptor.support.UserTokens;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class SpringAppContextConfig implements ApplicationContextAware, EnvironmentAware {

    protected ApplicationContext applicationContext;

    protected Environment environment;

    @Bean
    public UserTokens tokens() {
        return UserTokens.getInstance();
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}