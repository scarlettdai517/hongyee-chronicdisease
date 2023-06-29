package com.jointcorp.chronicdisease.platform.service;

import com.github.pagehelper.PageInfo;
import com.jointcorp.chronicdisease.data.annotation.DeviceDict;
import com.jointcorp.chronicdisease.data.po.device.DeviceType;
import com.jointcorp.chronicdisease.data.resp.deviceresp.DeviceListResp;
import com.jointcorp.chronicdisease.platform.cache.DeviceCache;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author zHuH1
 * @Date 2023/5/6 15:19
 **/
@Slf4j
@Service
public class DeviceDictService {

    @Autowired
    private DeviceCache deviceCache;

    private static final String DICT_FIELD_SUFFIX = "Text";

    public void convertDict(Object target) {
        if (target instanceof PageInfo) {
            PageInfo<?> pageInfo = (PageInfo<?>) target;
            target = pageInfo.getList();
        }
        if (target instanceof List) {
            List<?> objectList = ((List<?>) target);
            if (!CollectionUtils.isEmpty(objectList)) {
                List<DictDefinition> dictDefinitions = getMetadata(objectList.get(0));
                if (CollectionUtils.isEmpty(dictDefinitions)) {
                    return;
                }
                objectList.forEach(t -> doConvertDict(t, dictDefinitions));
            }
        } else {
            List<DictDefinition> dictDefinitions = getMetadata(target);
            if (CollectionUtils.isEmpty(dictDefinitions)) {
                return;
            }
//            List<String> dictNames = dictDefinitions.stream().map(d -> d.getDeviceDict().dictCode()).collect(Collectors.toList());
//            Map<String, Map<String, String>> dictMapMap = getDictMap(dictNames);
            doConvertDict(target, dictDefinitions);
        }
    }

    private List<DictDefinition> getMetadata(Object target) {
        List<DictDefinition> dictDefinitions = new ArrayList<>();
        if (ClassUtils.isPrimitiveOrWrapper(target.getClass()) || target instanceof Map || target instanceof String) {
            return dictDefinitions;
        }
        List<Field> fields = FieldUtils.getAllFieldsList(target.getClass());
        for (Field field : fields) {
            DeviceDict deviceDict = AnnotationUtils.getAnnotation(field, DeviceDict.class);
            if (deviceDict != null) {
                DictDefinition dictDefinition = new DictDefinition();
                dictDefinition.setDeviceDict(deviceDict);
                dictDefinition.setField(field);
                dictDefinitions.add(dictDefinition);
            }
        }
        return dictDefinitions;
    }

    @SneakyThrows
    private void doConvertDict(Object target, List<DictDefinition> dictDefinitionList) {
        Map<String, String> deviceTypeMap = deviceCache.getAllDeviceType().stream().collect(Collectors.toMap(
                DeviceType::getDeviceTypeCode, DeviceType::getDeviceTypeName
        ));
        Map<String, String> deviceModelMap = deviceCache.getDeviceModelKeyValue();
//        Map<String, String> deviceModelMap = deviceCache.getAllDeviceModel().stream().collect(Collectors.toMap(
//                d -> d.getDeviceTypeCode() + ":" + d.getDeviceModelKey(), DeviceModel::getDeviceModelValue
//        ));
        for (DictDefinition definition : dictDefinitionList) {
            DeviceDict deviceDict = definition.getDeviceDict();
            Field field = definition.getField();
            String dictCode = deviceDict.dictName();
            String dictField = StringUtils.isNotBlank(deviceDict.dictField()) ? deviceDict.dictField() : field.getName() + DICT_FIELD_SUFFIX;

            DeviceListResp device = new DeviceListResp();
            BeanUtils.copyProperties(target, device);
            String deviceTypeCode = device.getDeviceTypeCode();
            if (dictCode.equals("DEVICE_TYPE")) {
                log.info("字典赋值{}：{}", dictField, deviceTypeMap.get(deviceTypeCode));
                FieldUtils.writeField(target, dictField, deviceTypeMap.get(deviceTypeCode), true);
            } else if (dictCode.equals("DEVICE_MODEL")) {
                Integer deviceModelKey = device.getDeviceModelKey();
                String value = deviceModelMap.get(deviceTypeCode + ":" + deviceModelKey);
                log.info("字典赋值{}:{}", dictField, value);
                FieldUtils.writeField(target, dictField, value, true);
            }
        }
    }

    @Data
    public class DictDefinition {
        private DeviceDict deviceDict;
        private Field field;
    }

}
