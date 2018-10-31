package com.taotao.controller;

import com.taotao.common.pojo.EasyUIDataGridResult;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.pojo.TbItem;
import com.taotao.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品管理
 */

@Controller
public class ItemConntroller {

    @Autowired
    public ItemService itemService;

    /**
     * 根据商品id查询商品信息
     *
     * @param itemId 商品id
     * @return
     */
    @RequestMapping("/item/{itemId}")
    @ResponseBody
    public TbItem getItemById(@PathVariable long itemId) {
        TbItem tbItem = itemService.getItemById(itemId);
        return tbItem;
    }

    /**
     *查询商品信息
     * @param page 当前页码
     * @param rows 每页行数
     * @return
     */

    @RequestMapping(value = "/item/list",method = RequestMethod.GET)
    @ResponseBody
    public EasyUIDataGridResult getItemList(Integer page, Integer rows) {
        EasyUIDataGridResult result = itemService.getItemList(page, rows);
        return result;
    }

    /**
     * 新增商品信息
     * @param item 商品信息
     * @param desc 商品描述
     * @return
     */
    @RequestMapping("/item/save")
    @ResponseBody
    public TaotaoResult saveItem(TbItem item, String desc) {
        TaotaoResult result = itemService.addItem(item, desc);
        return result;
    }

    /**
     * 根据id，更新商品状态1-正常，2-下架，3-删除
     * @param ids 商品id
     * @param method 商品状态
     * @return
     */
    @RequestMapping("/rest/item/{method}")
    @ResponseBody
    public TaotaoResult updateItemStatus(@RequestParam(value="ids")List<Long> ids, @PathVariable String method) {
        TaotaoResult result = itemService.updateItemStatus(ids,method);
        return result;
    }

    /**
     * 根据商品id查询商品描述
     * @param id
     * @return
     */
    @RequestMapping("/item/desc/{id}")
    @ResponseBody
    public TaotaoResult getItemDesc(@PathVariable("id") long id) {
        TaotaoResult result = itemService.getItemDesc(id);
        return result;
    }

    /**
     * 更新商品信息
     * @param tbItem
     * @param desc
     * @return
     */
    @RequestMapping("/item/update")
    @ResponseBody
    public  TaotaoResult updateItems(TbItem tbItem,String desc){
        TaotaoResult result = itemService.updateItem(tbItem, desc);
        return result;
    }


    /**
     *  批量删除：1,2,3,4
     *  当个删除：1 2 3 4
     * @param ids 通过浏览器传递过来的值
     * @return
     */
    @RequestMapping("/item/deleteall/{ids}")
    @ResponseBody
    public  TaotaoResult deleteAll(@PathVariable("ids")List<Long> ids){
        TaotaoResult result= itemService.deleteAll(ids);
        return result;
    }

/*
    **
     *  批量删除：1,2,3,4
     *  当个删除：1 2 3 4
     * @param ids 通过浏览器传递过来的值
     * @return
     *
    @RequestMapping("/item/deleteall/{ids}")
    @ResponseBody
    public  TaotaoResult deleteAll1(@PathVariable("ids")List<Long> ids){
        //批量删除
        TaotaoResult result=null;
        if(ids.contains(",")){
            //多个id
            List<Long> del_ids=new ArrayList<Long>();
            //用","分割
            String[] str_ids=ids.split(",");
            for (String string : str_ids) {
                del_ids.add(Long.parseLong(string));
                result = itemService.deleteAll(del_ids);
            }
        }else {
            //单个删除
            Long id=Long.parseLong(ids);
            result = itemService.deleteItem(id);
        }
        return result;
    }
        return result;
    }*/

}
