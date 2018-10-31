package com.taotao.freemarker;


import freemarker.template.Configuration;
import freemarker.template.Template;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description:Freemarker测试
 * @author:cxg
 * @Date:${time}
 */
public class TestFreemarker {
    @Test
    public void testFreemarker()throws Exception{
        //1.创建一个模板文件
        //2.创建一个Configuration对象,构造方法的参数就是freemarker对于的版本号。
        Configuration configuration=new Configuration(Configuration.getVersion());
        //3.设置模板所在的路径
        configuration.setDirectoryForTemplateLoading(new File("G:/Java-webspace/taotao_cxg/taotao_item_web/src/main/webapp/WEB-INF/ftl"));
        //4.设置模板的字符集，一般utf-8
        configuration.setDefaultEncoding("utf-8");
        //5.使用Configuration对象加载一个模板文件，需要指定模板文件的文件名
        Template template = configuration.getTemplate("student.ftl");
        //6.创建一个模板数据集，可以是pojo也可以是map，推荐使用map
        Map data=new HashMap<>();
        //向数据集中添加数据
        data.put("hello","hello freemarker");
        //7.创建一个Writer对象，指定输出文件的路径及文件名
        Writer out=new FileWriter(new File("G:/Java-webspace/taotao_test_out/hello.text"));
        //8.使用模板对象的process方法输出文件
        template.process(data,out);
        //9.关闭
        out.close();
    }


    @Test
    public void testStudent()throws Exception{
        //1.创建一个模板文件
        //2.创建一个Configuration对象,构造方法的参数就是freemarker对于的版本号。
        Configuration configuration=new Configuration(Configuration.getVersion());
        //3.设置模板所在的路径
        configuration.setDirectoryForTemplateLoading(new File("G:/Java-webspace/taotao_cxg/taotao_item_web/src/main/webapp/WEB-INF/ftl"));
        //4.设置模板的字符集，一般utf-8
        configuration.setDefaultEncoding("utf-8");
        //5.使用Configuration对象加载一个模板文件，需要指定模板文件的文件名
        Template template = configuration.getTemplate("student.ftl");
        //6.创建一个模板数据集，可以是pojo也可以是map，推荐使用map
        Map data=new HashMap<>();
        //向数据集中添加数据
        data.put("hello","hello freemarker");
        //学生
        Student student=new Student(1,"小米",11,"背景");
        data.put("student",student);
        //7.创建一个Writer对象，指定输出文件的路径及文件名
        Writer out=new FileWriter(new File("G:/Java-webspace/taotao_test_out/student.html"));
        //8.使用模板对象的process方法输出文件
        template.process(data,out);
        //9.关闭
        out.close();
    }

    @Test
    public void testList()throws Exception{
        //1.创建一个模板文件
        //2.创建一个Configuration对象,构造方法的参数就是freemarker对于的版本号。
        Configuration configuration=new Configuration(Configuration.getVersion());
        //3.设置模板所在的路径
        configuration.setDirectoryForTemplateLoading(new File("G:/Java-webspace/taotao_cxg/taotao_item_web/src/main/webapp/WEB-INF/ftl"));
        //4.设置模板的字符集，一般utf-8
        configuration.setDefaultEncoding("utf-8");
        //5.使用Configuration对象加载一个模板文件，需要指定模板文件的文件名
        Template template = configuration.getTemplate("student.ftl");
        //6.创建一个模板数据集，可以是pojo也可以是map，推荐使用map
        Map data=new HashMap<>();
        //向数据集中添加数据
        data.put("hello","hello freemarker");
        //学生
        /**循环取list集合中的数据，并设置下标*/
        Student student=new Student(1,"小米",11,"背景");
        data.put("student",student);
        List<Student> stuList = new ArrayList<>();
        stuList.add(new Student(1,"小米1",11,"张三"));
        stuList.add(new Student(1,"小米1",11,"张三"));
        stuList.add(new Student(1,"小米1",11,"张三"));
        stuList.add(new Student(1,"小米1",11,"张三"));
        data.put("stuList", stuList);

        //7.创建一个Writer对象，指定输出文件的路径及文件名
        Writer out=new FileWriter(new File("G:/Java-webspace/taotao_test_out/student.html"));
        //8.使用模板对象的process方法输出文件
        template.process(data,out);
        //9.关闭
        out.close();
    }
}
