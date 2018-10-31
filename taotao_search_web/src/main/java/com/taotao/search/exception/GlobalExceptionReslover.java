package com.taotao.search.exception;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description:全局异常处理
 * @author:cxg
 * @Date:${time}
 */
public class GlobalExceptionReslover implements HandlerExceptionResolver {
    /**
     * @param request
     * @param response
     * @param handler  拿到的方法
     * @param ex
     * @return
     */

    //设置异常日志
    private static  final Logger logger = LoggerFactory.getLogger(GlobalExceptionReslover.class);
    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response,
                                         Object handler, Exception ex) {

        logger.info("进入全局");
        //查看handler是什么
        logger.debug(String.valueOf(handler.getClass()));
        //控制台打印异常
        ex.printStackTrace();
        //向日志文件中写入异常
        logger.error("系统发生异常", ex);
        //发邮件，发短信。可以查看Jmail资料
        //展示错误页面
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("message", "系统发生异常，请稍后重试");
        modelAndView.setViewName("error/exception");
        return modelAndView;

    }


}

