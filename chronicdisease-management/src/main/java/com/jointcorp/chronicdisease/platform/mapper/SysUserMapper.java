package com.jointcorp.chronicdisease.platform.mapper;

import com.jointcorp.chronicdisease.data.po.Institution;
import com.jointcorp.chronicdisease.data.po.SysUser;
import com.jointcorp.chronicdisease.data.resp.SysUserAccountManageResp;
import com.jointcorp.chronicdisease.platform.base.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface SysUserMapper extends BaseMapper<SysUser> {

    SysUser login(@Param("phoneNumber") String phoneNumber,@Param("countryCode") String countryCode, @Param("email") String email, @Param("password") String password);


    int loginTimeUpdate(@Param("userId") Long userId);

    int deleteTokenByUserIdInt(Long userId);

    SysUser selectUserByCorporateId(Long corporateId);

    List<SysUserAccountManageResp> selectValidUser (@Param("userIdList")List<Long> userIdList);

    List<Long> selectUserIdList  (@Param("validIds")List<Long> validIds);

    List<Long> selectAllUserId ();

    String selectPswById(Long userId);

    int updatePasswordInt(@Param("userId") Long userId, @Param("newPsw") String newPsw);

    int updateAccount(@Param("userId") Long userId, @Param("email") String email, @Param("phoneNumber") String phoneNumber, @Param("countryCode") String countryCode);

    int updateUserNameByInsName(@Param("userId") Long userId, @Param("userName") String userName);

    int updateAccountStatusInt(@Param("userId") String userId, @Param("accountStatus") Integer accountStatus);

    /**
     * 根据账号模糊搜索所有用户信息
     * @param account
     */
    List<SysUser> selectLikeAllUser(@Param("account") String account,@Param("accountStatus") String accountStatus) ;


    List<SysUser> selectLikeAllUserByInts(@Param("account") String account, @Param("accountStatus") String accountStatus,@Param("ints") List<Long> validIds);
}
