package com.jointcorp.chronicdisease.platform.service;

import com.jointcorp.chronicdisease.data.po.Institution;
import com.jointcorp.chronicdisease.data.po.device.DeviceBindInstitution;
import com.jointcorp.chronicdisease.platform.mapper.DeviceBindInstitutionMapper;
import com.jointcorp.chronicdisease.platform.mapper.InstitutionMapper;
import com.jointcorp.common.util.SnowflakeIdWorker;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;
import java.time.LocalDateTime;

/**
 * @Author zHuH1
 * @Date 2023/5/11 10:06
 **/
@Slf4j
@Service
public class DeviceBindingInstitutionService {

    @Autowired
    private DeviceBindInstitutionMapper deviceBindInstitutionMapper;
    @Autowired
    private SnowflakeIdWorker snowflakeIdWorker;
    @Autowired
    private InstitutionMapper institutionMapper;

    /**
     * 查询设备与机构处于绑定状态的记录
     */
    public DeviceBindInstitution queryDeviceBindingInst(String deviceIdent) {
        Example example = new Example(DeviceBindInstitution.class);
        Example.Criteria criteria = example.createCriteria()
                .andEqualTo("deviceIdent", deviceIdent)
                .andEqualTo("bindingState", true);
        DeviceBindInstitution deviceBindInstitution = deviceBindInstitutionMapper.selectOneByExample(example);
        if (deviceBindInstitution == null) {
            return null;
        }
        return deviceBindInstitution;
    }

    /**
     * 查询设备是否存在绑定中的记录
     */
    public Boolean queryDeviceBindingOrNot(String deviceIdent) {
        Example example = new Example(DeviceBindInstitution.class);
        Example.Criteria criteria = example.createCriteria()
                .andEqualTo("deviceIdent", deviceIdent)
                .andEqualTo("bindingState", true);
        // 存在绑定中记录，返回true
        if (deviceBindInstitutionMapper.selectCountByExample(example) > 0) {
            return true;
        }
        return false;
    }

    public int unbindDeviceAndInstitution(String deviceIdent) {
        return deviceBindInstitutionMapper.unbindDeviceAndInstitution(deviceIdent);
    }

    /**
     * 存储 设备与机构绑定记录
     * @param deviceIdent
     * @param institutionId
     * @return
     */
    public void insertDeviceBindingInstitution(String deviceIdent, String deviceModelValue, Long institutionId) {
        // 根据设备地址查询该设备是否已被其他机构绑定
        Example example = new Example(DeviceBindInstitution.class);
        example.createCriteria().andEqualTo("deviceIdent", deviceIdent).andEqualTo("bindingState", true);
        DeviceBindInstitution bindEntity = deviceBindInstitutionMapper.selectOneByExample(example);
        // 如果已被其他机构绑定，则抛错
        if (bindEntity != null && !bindEntity.getInstitutionId().equals(institutionId)) {
            log.error("添加设备失败:该设备已被其他机构绑定!");
            throw new RuntimeException("该设备已被其他机构绑定,请先解除绑定!");
        }

        example.clear();
        // 根据设备地址和机构id查询是否存在旧绑定记录
        example.createCriteria().andEqualTo("deviceIdent", deviceIdent).andEqualTo("institutionId", institutionId);
        DeviceBindInstitution existEntity = deviceBindInstitutionMapper.selectOneByExample(example);
        // 如果存在旧纪录且不是绑定状态，更新状态
        if (existEntity != null) {
            if (existEntity.getBindingState() != null && !existEntity.getBindingState()) {
                existEntity.setBindingState(true);
                existEntity.setUpdateTime(LocalDateTime.now());
                deviceBindInstitutionMapper.updateByPrimaryKeySelective(existEntity);
                log.info("更新设备与机构绑定记录:{}", existEntity);
            }
        } else {
            // 不存在旧纪录,保存新纪录
            DeviceBindInstitution deviceBindInstitution = new DeviceBindInstitution();
            deviceBindInstitution.setId(snowflakeIdWorker.nextId());
            deviceBindInstitution.setDeviceIdent(deviceIdent);
            deviceBindInstitution.setInstitutionId(institutionId);
            deviceBindInstitution.setDeviceModelValue(deviceModelValue);
            deviceBindInstitution.setBindingState(true);
            deviceBindInstitution.setCreateTime(LocalDateTime.now());
            deviceBindInstitution.setUpdateTime(LocalDateTime.now());
            Institution institution = institutionMapper.selectByPrimaryKey(institutionId);
            deviceBindInstitution.setInstitutionName(StringUtils.isBlank(institution.getInstitutionName()) ? null : institution.getInstitutionName());
            deviceBindInstitutionMapper.insert(deviceBindInstitution);
            log.info("保存设备与机构绑定记录:{}", deviceBindInstitution);
        }
    }

//    /**
//     * 根据设备地址和机构id查询绑定记录
//     * @param deviceIdent
//     * @param institutionId
//     * @return
//     */
//    public DeviceBindInstitution getDeviceBindInstitution(String deviceIdent, Long institutionId) {
//        Example example = new Example(DeviceBindInstitution.class);
//        Example.Criteria criteria = example.createCriteria()
//                .andEqualTo("deviceIdent", deviceIdent)
//                .andEqualTo("institutionId", institutionId);
//        List<DeviceBindInstitution> dataList = deviceBindInstitutionMapper.selectByExample(example);
//        if (!CollectionUtils.isEmpty(dataList)) {
//            return dataList.get(0);
//        }
//        return null;
//    }

}
