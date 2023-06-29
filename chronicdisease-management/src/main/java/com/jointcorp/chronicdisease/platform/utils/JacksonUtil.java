package com.jointcorp.chronicdisease.platform.utils;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @Author zHuH1
 * @Date 2023/5/8 14:00
 **/
public class JacksonUtil {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public JacksonUtil() {
    }

    static {
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        MAPPER.registerModule(javaTimeModule);
        MAPPER.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public static ObjectMapper objectMapper() {
        return MAPPER;
    }

    public static <T> Map<T, T> jsonToMap(String jsonData) {
        try {
            return MAPPER.readValue(jsonData, Map.class);
        } catch (Exception var4) {
            return null;
        }
    }

    public static <T> Map<String, List<T>> jsonToMapWithList(String jsonData) {
        try {
            return MAPPER.readValue(jsonData, Map.class);
        } catch (Exception var4) {
            return null;
        }
    }

    public static <T> Map<T, Map<T, T>> jsonToDoubleMap(String jsonData) {
        try {
            return MAPPER.readValue(jsonData, Map.class);
        } catch (Exception var4) {
            return null;
        }
    }


}

