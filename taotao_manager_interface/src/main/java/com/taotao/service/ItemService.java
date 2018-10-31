package com.taotao.service;


import com.taotao.common.pojo.EasyUIDataGridResult;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.pojo.TbItem;
import com.taotao.pojo.TbItemDesc;

import java.util.List;


/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description:${todo}(用一句话描述该文件做什么)
 * @author:cxg
 * @Date:${time}
 */

public interface ItemService {
    /**
     * 根据商品id查询商品信息
     * @param itemId
     * @return
     */
    TbItem getItemById(long itemId);

    /**
     * 根据商品id查询商品描述信息
     * @param itemId
     * @return
     */
    TbItemDesc getItemDescWeb(long itemId);

    /**
     * 获取商品列表
     * @param page 当前页码
     * @param rows 每页行数
     * @return
     */
    EasyUIDataGridResult getItemList(Integer page, Integer rows);

    /**
     * 添加商品信息
     * @param item 商品
     * @param desc 商品描述
     * @return
     */
    TaotaoResult addItem(TbItem item,String desc);


    /**
     * 改变商品状态
     * @param ids 商品id
     * @param method 商品状态
     * @return
     */
    TaotaoResult updateItemStatus(List<Long> ids, String method);


    /**
     * 根据多个id删除商品信息
     * @param ids
     * @return
     */
    TaotaoResult deleteAll(List<Long> ids);

    /**
     * 根据单个id删除商品信息
     * @param id
     * @return
     */
    TaotaoResult deleteItem(Long id);

    /**
     * 查询商品描述
     * @param id
     * @return
     */
    TaotaoResult getItemDesc(Long id);

    /**
     * 更新商品
     * @param tbItem
     * @param desc
     * @return
     */
    TaotaoResult updateItem(TbItem tbItem, String desc);
}
