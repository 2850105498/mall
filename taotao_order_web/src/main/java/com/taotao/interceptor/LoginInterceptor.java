package com.taotao.interceptor;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.util.CookieUtils;
import com.taotao.sso.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description:判断用户是否登入的拦截器
 * @author:cxg
 * @Date:${time}
 */
public class LoginInterceptor implements HandlerInterceptor {
    @Autowired
    private UserService userService;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //执行handler之前先执行此方法
        //从cookie中获取token
        String token = CookieUtils.getCookieValue(request, "TT_TOKEN");
        //如果取不到token，跳转到sso的登入界面，需要把当前请求的url作为参数传递给sso，sso登入成功之后跳转回请求页面
        if (StringUtils.isBlank(token)){
            //没登入,取当前的url
            response.sendRedirect("http://localhost:8088/page/login?url="
                                        + request.getRequestURL());
            return false;
        }
        //获取用户信息
        TaotaoResult userByToken = userService.getUserByToken(token);
        if (userByToken.getStatus()!=200){
            //登入过期
            response.sendRedirect("http://localhost:8088/page/login?url="
                                    + request.getRequestURL());
            return false;

        }
            //用户已登入放行，将用户信息保存到request中
            request.setAttribute("USER_INFO",userByToken.getData());
            return true;


    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        //handler执行后，modelAndView返回之前
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //modelAndView返回之后，异常处理
    }
}
