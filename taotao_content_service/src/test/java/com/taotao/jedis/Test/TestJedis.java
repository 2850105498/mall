package com.taotao.jedis.Test;

import org.junit.Test;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;

import java.util.HashSet;
import java.util.Set;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description:测试redis
 * @author:cxg
 * @Date:${time}
 */
public class TestJedis {
    @Test
    public void testJedis()throws Exception{
        //创建一个jedis对象，制定服务的ip和端口号
     Jedis jedis= new Jedis("192.168.25.129",6379);
     //直接操作数据库
        jedis.set("cxg1","1000");
        jedis.incr("cxg1");
        String s = jedis.get("cxg1");
        System.out.print(s);
        //关闭jedis
        jedis.close();
    }
    @Test
    public void testJedisCluster() throws Exception {
        // 第一步：使用JedisCluster对象。需要一个Set<HostAndPort>参数。Redis节点的列表。
        Set<HostAndPort> nodes = new HashSet<>();
        nodes.add(new HostAndPort("192.168.25.129", 7001));
        nodes.add(new HostAndPort("192.168.25.129", 7002));
        nodes.add(new HostAndPort("192.168.25.129", 7003));
        nodes.add(new HostAndPort("192.168.25.129", 7004));
        nodes.add(new HostAndPort("192.168.25.129", 7005));
        nodes.add(new HostAndPort("192.168.25.129", 7006));
        JedisCluster jedisCluster = new JedisCluster(nodes);
        // 第二步：直接使用JedisCluster对象操作redis。在系统中单例存在。
        jedisCluster.set("hello", "100");
        String result = jedisCluster.get("hello");
        // 第三步：打印结果
        System.out.println(result);
        // 第四步：系统关闭前，关闭JedisCluster对象。
        jedisCluster.close();
    }


}
