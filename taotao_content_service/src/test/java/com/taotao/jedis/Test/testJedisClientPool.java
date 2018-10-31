package com.taotao.jedis.Test;

import com.taotao.jedis.JedisClient;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description:${todo}(用一句话描述该文件做什么)
 * @author:cxg
 * @Date:${time}
 */
public class testJedisClientPool {
    @Test
    public void testJedisClientPool() throws Exception {
/*        //初始化spring容器
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("class:spring/applicationContext-*.xml");
        //容器中获得JedisClient对象
        JedisClient jedisClient=applicationContext.getBean(JedisClient.class);
        //使用JedisClient对象操作redis
        jedisClient.set("jedisclient","mytest");
        //取值
        String result=jedisClient.get("jedisclient");
        System.out.print(result);*/
        //初始化Spring容器
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-*.xml");
        //从容器中获得JedisClient对象
        JedisClient jedisClient = applicationContext.getBean(JedisClient.class);
        jedisClient.set("first", "陈新贵在努力");
        String result = jedisClient.get("first");
        System.out.println(result);

    }
}
