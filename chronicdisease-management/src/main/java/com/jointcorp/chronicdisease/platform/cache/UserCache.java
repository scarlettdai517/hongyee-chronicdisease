package com.jointcorp.chronicdisease.platform.cache;

import com.jointcorp.chronicdisease.data.resp.resourceresp.institution.SubInstitution;
import com.jointcorp.chronicdisease.data.SysUserBaseInfo;
import com.jointcorp.chronicdisease.data.consts.Consts;
import com.jointcorp.chronicdisease.data.po.Institution;
import com.jointcorp.chronicdisease.data.po.Resource;
import com.jointcorp.chronicdisease.data.po.SysUser;
import com.jointcorp.chronicdisease.data.po.SysUserToken;
import com.jointcorp.chronicdisease.platform.mapper.InstitutionMapper;
import com.jointcorp.chronicdisease.platform.mapper.SysUserMapper;
import com.jointcorp.chronicdisease.platform.mapper.SysUserTokenMapper;
import com.jointcorp.common.util.JsonUtil;
import com.jointcorp.redissoncache.client.RedissonCacheClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 系统用户相关信息缓存
 * @Author: Xu-xg
 * @CreateTime: 2023-04-18 17:02
 */
@Component
public class UserCache {

    ///**
    // * Caffeine缓存，一级缓存
    // */
    //@Autowired
    //private Cache<String,Object> localCache;

    /**
     * redis缓存，二级缓存
     */
    @Autowired
    private RedissonCacheClient redissonCacheClient;

    @Autowired
    private SysUserTokenMapper userTokenMapper;
    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private InstitutionMapper institutionMapper ;
    /**
     * 缓存user token
     * @param token
     * @param userBaseInfo
     */
    public void cacheToken(String token,SysUserBaseInfo userBaseInfo) {
        String key = Consts.USER_TOKEN_KEY + token;
        //localCache.put(key,userBaseInfo);
        redissonCacheClient.put(key,JsonUtil.objectToJson(userBaseInfo),durationToNext0(), TimeUnit.SECONDS);
    }

    /**
     * 删除token
     * @param token
     */
    public void delToken(String token) {
        String key = Consts.USER_TOKEN_KEY + token;
        //localCache.invalidate(key);
        redissonCacheClient.delete(key);
    }

    /**
     * 缓存用户权限列表信息
     * @param userId
     * @param sysUserResources
     */
    public void cacheResources(String userId, List<Resource>sysUserResources) {
        String key = Consts.USER_RESOURCE_LIST + userId;
        //localCache.put(key,sysUserResources);
        redissonCacheClient.put(key, JsonUtil.objectToJson(sysUserResources),durationToNext0(), TimeUnit.SECONDS) ;
    }

    /**
     * 删除缓存的资源
     * @param userId
     */
    public void delResources(String userId) {
        String key = Consts.USER_RESOURCE_LIST + userId;
        //localCache.invalidate(key);
        redissonCacheClient.delete(key);
    }

    /**
     * 获取缓存用户
     * @param token
     * @return
     */
    public SysUserBaseInfo getUserBaseInfo(String token) {
        String key = Consts.USER_TOKEN_KEY + token;
        //Object value = localCache.get(key, k -> {
        //    String v = redissonCacheClient.get(key);
        //    if(StringUtils.isBlank(v)) {
        //        Example example = new Example(SysUserToken.class);
        //        example.createCriteria().andEqualTo("tokenVal",token);
        //        SysUserToken userToken = userTokenMapper.selectOneByExample(example);
        //        if(userToken == null) {
        //            return null;
        //        }
        //        SysUser sysUser = sysUserMapper.selectByPrimaryKey(userToken.getUserId());
        //        SysUserBaseInfo sysUserBaseInfo = SysUserBaseInfo.convert(sysUser);
        //        redissonCacheClient.put(key,JsonUtil.objectToJson(sysUserBaseInfo),durationToNext0(), TimeUnit.SECONDS);
        //        return sysUserBaseInfo;
        //    }
        //    return JsonUtil.jsonToPojo(v,SysUserBaseInfo.class);
        //});
        //return value == null ? null : (SysUserBaseInfo) value;


        String v = redissonCacheClient.get(key);
        if(StringUtils.isBlank(v)) {
            Example example = new Example(SysUserToken.class);
            example.createCriteria().andEqualTo("tokenVal",token);
            SysUserToken userToken = userTokenMapper.selectOneByExample(example);
            //token不存在或已过期
            if(userToken == null || userToken.getExpireTime().isBefore(LocalDateTime.now())) {
                return null;
            }
            SysUser sysUser = sysUserMapper.selectByPrimaryKey(userToken.getUserId());
            SysUserBaseInfo sysUserBaseInfo = SysUserBaseInfo.convert(sysUser);
            redissonCacheClient.put(key,JsonUtil.objectToJson(sysUserBaseInfo),durationToNext0(), TimeUnit.SECONDS);
            return sysUserBaseInfo;
        }
        return JsonUtil.jsonToPojo(v,SysUserBaseInfo.class);
    }

    /**
     * 缓存用户的机构和子机构
     * @param list
     * @param corporateId  key为机构或者合作商id
     */
    public void cacheSubInst(List<SubInstitution> list,long corporateId) {
        String key = Consts.USER_SUBINST_KEY + corporateId;
        //localCache.put(key,list);
        redissonCacheClient.put(key, JsonUtil.objectToJson(list), durationToNext0(), TimeUnit.SECONDS) ;
    }

    /**
     * 删除缓存的机构和子机构
     * @param id
     */
    public void delSubInst(long id) {
        String key = Consts.USER_SUBINST_KEY + id;
        //localCache.invalidate(key);
        redissonCacheClient.delete(key);
    }

    /**
     * 查询用户的机构和子机构
     * @param userId
     * @param userType
     * @param corporateId
     * @return
     */
    public List<SubInstitution> getSubInstitution(long userId,String userType,long corporateId) {
        String key = Consts.USER_SUBINST_KEY + corporateId;
        //Object value = localCache.get(key, k -> {
        //    String v = redissonCacheClient.get(key);
        //    if(StringUtils.isBlank(v)) {
        //        List<Institution> list = queryAllSubInst(userId,userType,corporateId);
        //        List<SubInstitution> subInstitutions = SubInstitution.convert(list);
        //        redissonCacheClient.put(key,JsonUtil.objectToJson(subInstitutions),durationToNext0(), TimeUnit.SECONDS);
        //        return subInstitutions;
        //    }
        //    return JsonUtil.jsonToList(v,SubInstitution.class);
        //});
        //return value == null ? null : (List<SubInstitution>) value;


        String v = redissonCacheClient.get(key);
        if(StringUtils.isBlank(v)) {
            List<Institution> list = queryAllSubInst(userId,userType,corporateId);
            List<SubInstitution> subInstitutions = SubInstitution.convert(list);
            redissonCacheClient.put(key,JsonUtil.objectToJson(subInstitutions),durationToNext0(), TimeUnit.SECONDS);
            return subInstitutions;
        }
        return JsonUtil.jsonToList(v,SubInstitution.class);
    }

    /**
     * 查询当前用户下的所有机构
     * @param userId
     * @param usertype
     * @param corporateId
     * @return
     */
    public List<Institution>
    queryAllSubInst(long userId,String usertype,long corporateId) {
        Example example = new Example(Institution.class);
        example.selectProperties("institutionId","parentId","partnerId");
        List<Institution> list = new ArrayList<>();
        //合作商
        if(Consts.USER_TYPE_PARTNER.equalsIgnoreCase(usertype)) {
            example.createCriteria().andEqualTo("partnerId",corporateId);
            list = institutionMapper.selectByExample(example);
        }
        //机构
        else if(Consts.USER_TYPE_INS.equalsIgnoreCase(usertype)) {
            List<Long> params = new ArrayList<>();
            params.add(corporateId);
            //查询机构下的子机构
            list = selectSubInst(params,list);
            //查询机构自己, corporateId表示超级管理员
            if(corporateId != 0) {
                Institution institution = institutionMapper.selectByPrimaryKey(corporateId);
                list.add(institution);
            }
        }
        //超级管理员
        else {
            list = institutionMapper.selectByExample(example);
        }
        return list;
    }

    private List<Institution> selectSubInst(List<Long> instIds,List<Institution> insts) {
        List<Institution> list = institutionMapper.selectSubInst(instIds);
        if(CollectionUtils.isEmpty(list)) {
            return insts;
        }
        List<Long> params = new ArrayList<>();
        for(Institution inst : list) {
            instIds.add(inst.getInstitutionId());
            params.add(inst.getInstitutionId());
            insts.add(inst);
        }
        return selectSubInst(params,insts);
    }

    /**
     * 当前时间距离下一个0点的秒数
     * @return
     */
    public static long durationToNext0() {
        //这里实际是23:59:59秒
        LocalDateTime nextDay = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        Duration duration = Duration.between(nextDay, LocalDateTime.now());
        return duration.toMinutes() / 1000;
    }

    /**
     * 获取当前机构下的全部机构id（包括自身）
     * @param userId
     * @param userType
     * @param corporateId
     * @return
     */
    public List<Long> getSubInstIdList(long userId, String userType, long corporateId) {
        List<SubInstitution> subInstitutionList = getSubInstitution(userId, userType, corporateId);
        if (CollectionUtils.isEmpty(subInstitutionList)) {
            return new ArrayList<>();
        }
        return subInstitutionList.stream().map(SubInstitution::getInstitutionId).distinct().collect(Collectors.toList());
    }
}
