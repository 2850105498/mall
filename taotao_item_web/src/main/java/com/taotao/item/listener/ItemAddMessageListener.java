package com.taotao.item.listener;

import com.taotao.item.pojo.Item;
import com.taotao.pojo.TbItem;
import com.taotao.pojo.TbItemDesc;
import com.taotao.service.ItemService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
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
public class ItemAddMessageListener implements MessageListener {
    @Autowired
    private ItemService itemService;

    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;


    @Value("${HTML_OUT_PATH}")
    private String HTML_OUT_PATH;
    @Override
    public void onMessage(Message message) {
        try {
            //从消息中取商品id
            TextMessage textMessage = (TextMessage) message;
            String strId = textMessage.getText();
            long itemId = Long.parseLong(strId);
            //等待事务的提交
            Thread.sleep(1000);
            //根据商品id查询商品信息及商品描述
            TbItemDesc itemDesc = itemService.getItemDescWeb(itemId);
            TbItem itemById = itemService.getItemById(itemId);
            //将商品信息转成商品详细页面需要的格式
            Item item = new Item(itemById);
            //使用freemarker生成静态页面
            Configuration configuration = freeMarkerConfigurer.getConfiguration();
            //1.创建模板
            //2.加载模板
            Template template = configuration.getTemplate("item.ftl");
            //3.准备模板需要的数据
            Map data=new HashMap<>();
            data.put("item",item);
            data.put("itemDesc",itemDesc);
            //4.指定输出的目录及文件
            Writer out=new FileWriter(new File(HTML_OUT_PATH+strId+".html"));
            //5.生成静态页面
            template.process(data,out);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
