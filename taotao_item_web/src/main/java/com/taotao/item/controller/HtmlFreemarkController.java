package com.taotao.item.controller;

import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description:${todo}(用一句话描述该文件做什么)
 * @author:cxg
 * @Date:${time}
 */
@Controller
public class HtmlFreemarkController {
    //从spring容器中获取FreeMarkerConfigurer
    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;

    @RequestMapping("/genhtml")
    @ResponseBody
    public String getHtml() throws Exception {
        //从FreeMarkerConfigurer中获取Configuration
        Configuration configuration = freeMarkerConfigurer.getConfiguration();
        //从configuration中获取Template模板对象
        Template template = configuration.getTemplate("hello.flt");
        //创建数据集
        Map data=new HashMap<>();
        data.put("hello","1000");
        //创建输出Writer
        Writer out=new FileWriter(new File("G:/Java-webspace/taotao_test_out/spring-test.html"));
        //调用template模板对象的process方法生成文件
        template.process(data,out);
        //关闭
        out.close();
        //

        return "OK";
    }
}
