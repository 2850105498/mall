package com.taotao.sso.service.impl;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.util.JsonUtils;
import com.taotao.jedis.JedisClient;
import com.taotao.mapper.TbUserMapper;
import com.taotao.pojo.TbUser;
import com.taotao.pojo.TbUserExample;
import com.taotao.sso.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description:用户处理的service
 * @author:cxg
 * @Date:${time}
 */
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private TbUserMapper tbUserMapper;

    @Autowired
    private JedisClient jedisClient;

    @Value("${USER_SESSION}")
    private String USER_SESSION;

    @Value("${SESSION_EXPIRE}")
    private  Integer SESSION_EXPIRE;
    /**
     *校验用户名，手机号，邮箱
     * @param data 要判断的数据
     * @param type 类型：1,2,3
     * @return
     */
    @Override
    public TaotaoResult checkData(String data, Integer type) {

        //创建条件查询
        TbUserExample example=new TbUserExample();
        TbUserExample.Criteria criteria = example.createCriteria();
        //判断姓名是否可用
        if(type==1){
            criteria.andUsernameEqualTo(data);
        }
        //判断手机是否可用
        else if(type==2){
            criteria.andPhoneEqualTo(data);
        }
        //判断邮箱是否可用
        else if(type==3){
            criteria.andEmailEqualTo(data);
        } 
        //否则就400报错
        else {
            return  TaotaoResult.build(400,"参数不可用");
        }
        List<TbUser> list = tbUserMapper.selectByExample(example);
        //查询到的数据不为空
        if(list!=null&&list.size()>0){
            return TaotaoResult.ok(false);
        }

        return TaotaoResult.ok(true);
    }

    /**
     * 创建用户
     *
     * @param tbUser
     * @return
     */
    @Override
    public TaotaoResult createUser(TbUser tbUser) {
        //1.判断用户名和密码不能为空
        if(StringUtils.isBlank(tbUser.getUsername())){
            return TaotaoResult.build(400,"用户名不能为空");
        }
        if(StringUtils.isBlank(tbUser.getPassword())){
            return TaotaoResult.build(400,"密码不能为空");
        }

        //2.校验数据是否可用
        //2.1获取布尔值
        TaotaoResult result = checkData(tbUser.getUsername(), 1);
        //getData是TaotaoResult中定义的方法，获取数据
        if (!(boolean)result.getData()){
            return TaotaoResult.build(400,"用户存在");
        }

        //2.2判断电话
       if (StringUtils.isNotBlank(tbUser.getPhone())){
           TaotaoResult result1 = checkData(tbUser.getPassword(), 2);
           if (!(boolean)result1.getData()){
               return TaotaoResult.build(400,"电话已经存在");
           }
       }
        //2.3判断邮箱
        if (StringUtils.isNotBlank(tbUser.getEmail())){
            TaotaoResult result2 = checkData(tbUser.getEmail(), 3);
            if (!(boolean)result2.getData()){
                return TaotaoResult.build(400,"邮箱已经存在");
            }
        }
        //3.补全创建时间和修改时间
        tbUser.setUpdated(new Date());
        tbUser.setCreated(new Date());
        //4.密码加密
        String md5Password = DigestUtils.md5DigestAsHex(tbUser.getPassword().getBytes());
        tbUser.setPassword(md5Password);
        //5.执行插入语句
        tbUserMapper.insert(tbUser);

        return TaotaoResult.ok();
    }

    /**
     * 登入功能
     * @param username 用户名
     * @param password 密码
     * @return
     */
    @Override
    public TaotaoResult login(String username, String password) {
        //1.判断用户名和密码是否正确
        // 1.1创建条件查询
        TbUserExample example=new TbUserExample();
        TbUserExample.Criteria criteria = example.createCriteria();
        criteria.andUsernameEqualTo(username);
        List<TbUser> list = tbUserMapper.selectByExample(example);
        //1.2判断list是否为空
        if(list==null||list.size()==0){
            //返回登入失败
            return  TaotaoResult.build(400,"用户名或密码不正确");
        }
        //2.密码要进行md5加密然后在校验
        //2.1取出用户数据
        TbUser tbUser=list.get(0);
        //2.2 password.getBytes()是现在的密码    tbUser.gePassword()是数据库中的密码，两个相互比较
        if(!DigestUtils.md5DigestAsHex(password.getBytes()).equals(tbUser.getPassword())){
            return  TaotaoResult.build(400,"用户名或密码不正确");
        }
        //3.生成token，使用uuid
        String token= UUID.randomUUID().toString();
        //4.把用户信息保存到redis，key就是token，value就是用户信息
        //4.1.清空密码在存到redis
        tbUser.setPassword(null);
        jedisClient.set(USER_SESSION+":"+token, JsonUtils.objectToJson(tbUser));
        //5.设置key的过期时间
        jedisClient.expire(USER_SESSION+":"+token,SESSION_EXPIRE);
        //6.返回登入成功，并把token返回
        return TaotaoResult.ok(token);
    }

    /**
     * 通过token查询用户信息
     * @param token
     * @return
     */
    @Override
    public TaotaoResult getUserByToken(String token) {
        String json = jedisClient.get(USER_SESSION + ":" + token);
        //如果查到数据为空
        if(StringUtils.isBlank(json)){
            return TaotaoResult.build(400,"用户登入过期");
        }
        //如果查到数据，则重新设置过期时间
        jedisClient.expire(USER_SESSION + ":" + token, SESSION_EXPIRE);
        //将数据json转成TbUser类型
        TbUser tbUser = JsonUtils.jsonToPojo(json, TbUser.class);
        return TaotaoResult.ok(tbUser);
    }

    /**
     * 退出登入
     *
     * @param token
     * @return
     */
    @Override
    public TaotaoResult loginout(String token) {
        //将redis中的token有效期时间设置为0
        jedisClient.expire(USER_SESSION+":"+token,0);
        return TaotaoResult.ok();
    }


}
