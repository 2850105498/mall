package com.taotao.sso.service;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.pojo.TbUser;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description:user的接口
 * @author:cxg
 * @Date:${time}
 */
public interface UserService {
    /**
     *校验用户名，手机号，邮箱
     * @param data 要判断的数据
     * @param type 类型：1,2,3
     * @return
     */
    TaotaoResult checkData(String data,Integer type);

    /**
     * 创建用户
     * @param tbUser
     * @return
     */
    TaotaoResult createUser(TbUser tbUser);

    /**
     * 登入功能
     * @param username 用户名
     * @param password 密码
     * @return
     */
    TaotaoResult login(String username,String  password);

    /**
     * 通过token查询用户信息
     * @param token
     * @return
     */
    TaotaoResult getUserByToken(String token);

    /**
     * 退出登入
     * @param token
     * @return
     */
    TaotaoResult loginout(String token);
}
