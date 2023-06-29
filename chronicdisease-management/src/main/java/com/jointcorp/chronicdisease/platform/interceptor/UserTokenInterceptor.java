package com.jointcorp.chronicdisease.platform.interceptor;

import com.jointcorp.chronicdisease.platform.base.Msg;
import com.jointcorp.chronicdisease.platform.interceptor.support.UserTokenUtil;
import com.jointcorp.chronicdisease.platform.interceptor.support.UserTokens;
import com.jointcorp.chronicdisease.platform.utils.MsgConverts;
import com.jointcorp.common.util.JsonUtil;
import com.jointcorp.parent.result.ResultUtil;
import org.apache.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author: Xu-xg
 * @CreateTime: 2023-04-19 11:30
 */
public class UserTokenInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String userToken = request.getHeader("UserToken");
        if(!UserTokens.verify(userToken)) {
            print(response, Msg.USER_TOKEN_ILLEGAL,200);
            return false;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserTokenUtil.removeUser();
    }

    private void print(HttpServletResponse response,Msg msg,int code) throws IOException {
        response.setHeader("Access-Control-Allow-Origin","*");
        response.setHeader("Access-Control-Allow-Methods","*");
        response.setHeader("Access-Control-Allow-Headers","*");
        response.setHeader("Access-Control-Allow-Credentials","true");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        response.setStatus(code);
        response.getWriter().print(JsonUtil.objectToJson(MsgConverts.businessError(msg)));
    }
}
