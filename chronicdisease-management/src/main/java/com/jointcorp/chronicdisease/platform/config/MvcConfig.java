package com.jointcorp.chronicdisease.platform.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jointcorp.chronicdisease.platform.interceptor.UserTokenInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.text.SimpleDateFormat;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //可以添加多个拦截器，如果配置多个拦截器，拦截顺序按从上到下执行
        //一个*表示字符串，**表示路径
        registry.addInterceptor(new UserTokenInterceptor()).addPathPatterns("/**").excludePathPatterns("/sysUser/login/**","/institution/queryPushUrl");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}

