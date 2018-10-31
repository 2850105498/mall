package com.taotao.sso.controller;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.util.CookieUtils;
import com.taotao.common.util.JsonUtils;
import com.taotao.pojo.TbUser;
import com.taotao.sso.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description:查询数据是否可用
 * @author:cxg
 * @Date:${time}
 */
@Controller
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 校验数据
     * @param param  校验的数据
     * @param type   类型1,2,3
     * @return
     */
    @RequestMapping("/user/check/{param}/{type}")
    @ResponseBody
    public TaotaoResult checkDate(@PathVariable String param, @PathVariable Integer type) {

        TaotaoResult result = userService.checkData(param, type);
        return result;
    }
    /**
     * 创建用户
     * @param tbUser
     * @return
     */
    @RequestMapping(value = "/user/register",method = RequestMethod.POST)
    @ResponseBody
    public TaotaoResult createUser(TbUser tbUser){
        TaotaoResult result = userService.createUser(tbUser);
        return result;

    }
    /**
     * 登入功能
     * @param username 用户名
     * @param password 密码
     * @return
     */
    @RequestMapping(value = "/user/login",method = RequestMethod.POST)
    @ResponseBody
    public TaotaoResult login(String username, String password, HttpServletRequest request, HttpServletResponse response){

        TaotaoResult result = userService.login(username, password);
        if(result.getStatus()==200){
            String token = result.getData().toString();
            //把token写入到cookie
            CookieUtils.setCookie(request ,response,"TT_TOKEN",token);
        }
        return  result;
    }
    /**
     * 通过token查询用户信息
     * @param token
     * @return
     */
    @RequestMapping(value = "/user/token/{token}",method = RequestMethod.GET
            //指定响应数据的content-type
            ,produces= MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public String getUserByToken(@PathVariable String token,String callback){
        TaotaoResult result = userService.getUserByToken(token);
        //判断是否为jsonp请求
        if(StringUtils.isNotBlank(callback)){
            return callback+"("+ JsonUtils.objectToJson(result)+")";
        }
        return JsonUtils.objectToJson(result);
    }
  /*  用户显示在头部的方法，springmvc4后的版本实现方法
     * 接收token,调用service服务获取用户信息
     * @param token
     * @return
     *//*
    @RequestMapping(value="/user/token/{token}",method=RequestMethod.GET,
            produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public Object getUserByToken(@PathVariable String token,String callback){
        TaotaoResult result = userService.getUserByToken(token);
        //判断是否是jsonp
        if(StringUtils.isNotBlank(callback)){
            MappingJacksonValue value = new MappingJacksonValue(result);
            value.setJsonpFunction(callback);
            return value;
        }
        return result;
    }*/







    /**
     * 退出登入
     *
     * @param token
     * @return
     */
    @RequestMapping(value ="/user/logout/{token}",method = RequestMethod.GET)
    public String loginout(@PathVariable String token){
        userService.loginout(token);

        return "login";
    }




}
