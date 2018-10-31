package com.taotao.portal.controller;

import com.taotao.common.util.JsonUtils;
import com.taotao.content.service.ContentService;
import com.taotao.pojo.TbContent;
import com.taotao.portal.pojo.AD1Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description:首页展示
 * @author:cxg
 * @Date:${time}
 */
@Controller
public class IndexController {
    @Autowired
    private ContentService contentService;

    @Value("${AD1_CATEGORY_ID}")
    private Long AD1_CATEGORY_ID;

    @Value("${AD1_HEIGHT}")
    private Integer AD1_HEIGHT;
    @Value("${AD1_HEIGHT_B}")
    private Integer AD1_HEIGHT_B;
    @Value("${AD1_WIDTH}")
    private Integer AD1_WIDTH;
    @Value("${AD1_WIDTH_B}")
    private Integer AD1_WIDTH_B;

    @RequestMapping("/index")
    public String showIndex(Model model){

        //根据cid=AD1_CATEGORY_ID查询轮播图内容列表
        List<TbContent> contentList = contentService.getContentByCid(AD1_CATEGORY_ID);
        //列表转成AD1Node类型
         List<AD1Node> arrayList= new ArrayList<>();
        //循环遍历
        for ( TbContent tbContent:contentList) {
            AD1Node node=new AD1Node();
            node.setHeight(AD1_HEIGHT);
            node.setHeightB(AD1_HEIGHT_B);
            node.setWidth(AD1_WIDTH);
            node.setWidthB(AD1_WIDTH_B);
            node.setAlt(tbContent.getTitle());
            node.setHref(tbContent.getUrl());
            node.setSrc(tbContent.getPic());
            node.setSrcB(tbContent.getPic2());
            arrayList.add(node);
        }
        //把列表转成json数据
        model.addAttribute("ad1", JsonUtils.objectToJson(arrayList));

        return "index";
    }
}
