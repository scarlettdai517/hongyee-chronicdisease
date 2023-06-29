package com.jointcorp.chronicdisease.platform.interceptor.support;

import com.jointcorp.chronicdisease.data.SysUserBaseInfo;
import com.jointcorp.chronicdisease.platform.cache.UserCache;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import static com.google.common.base.Preconditions.checkState;

public class UserTokens implements ApplicationContextAware {

    private UserCache userCache;

    public static boolean verify(String token) {
        if (StringUtils.isBlank(token)) {
            return false;
        }
        SysUserBaseInfo info = userCache().getUserBaseInfo(token);
        if(info != null) {
            //绑定
            UserTokenUtil.bindUser(info);
            return true;
        }
        return false;
    }

    private ApplicationContext applicationContext;

    private static final UserTokens INSTANCE = new UserTokens();

    public static UserTokens getInstance() {
        return INSTANCE;
    }

    public static UserCache userCache() {
        return INSTANCE.userCache;
    }

    private void initializeInjector() {
        checkState(applicationContext != null, "applicationContext");
        userCache = applicationContext.getBean(UserCache.class);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        initializeInjector();
    }
}
