package com.taotao.pagehelper;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.taotao.mapper.TbItemMapper;
import com.taotao.pojo.TbItem;
import com.taotao.pojo.TbItemExample;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description:${todo}(用一句话描述该文件做什么)
 * @author:cxg
 * @Date:${time}
 */
public class TestPageHelper {
    @Test
    public void testPageHelper()throws Exception{
        //1、在SqlMapConfig.xml中配置分页插件
        //2、在执行查询之前配置分页条件。使用PageHelper静态方法:pageNum——页数，pageSize——一页多少条记录
        PageHelper.startPage(1,10);
        //3、执行查询
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-dao.xml");
        //代理对象
        TbItemMapper itemMapper = applicationContext.getBean(TbItemMapper.class);
        //创建example对象不需要设置查询条件
        TbItemExample example=new TbItemExample();
        List<TbItem> list=itemMapper.selectByExample(example);
        //4、取分页信息，，使用PageInfo对象取
        PageInfo<TbItem> pageInfo=new PageInfo<TbItem>(list);
        System.out.println("总记录数"+pageInfo.getTotal());
        System.out.println("总页数"+pageInfo.getPages());
        System.out.println("返回记录数"+list.size());
    }
}

