package com.taotao.item.controller;

import com.taotao.item.pojo.Item;
import com.taotao.pojo.TbItem;
import com.taotao.pojo.TbItemDesc;
import com.taotao.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description:商品详细页面展示
 * @author:cxg
 * @Date:${time}
 */
@Controller
public class ItemController {
    @Autowired
    private ItemService itemService;

    @RequestMapping("/item/{itemId}")
    public String showItemInfo(@PathVariable Long itemId, Model model) {
        //根据商品id查询商品信息
        TbItem itemById = itemService.getItemById(itemId);
        //把itemById转成item对象
        Item item=new Item(itemById);
        //根据商品id查询商品描述
        TbItemDesc itemDesc = itemService.getItemDescWeb(itemId);
        model.addAttribute("item",item);
        model.addAttribute("itemDesc", itemDesc);
        return "item";
    }

}
