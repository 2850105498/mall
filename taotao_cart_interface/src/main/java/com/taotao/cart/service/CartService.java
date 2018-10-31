package com.taotao.cart.service;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.pojo.TbItem;

import java.util.List;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description:${todo}(用一句话描述该文件做什么)
 * @author:cxg
 * @Date:${time}
 */
public interface CartService {
    /**
     * 登入状态添加商品到购物车的redis中
     * @param tbItem
     * @param num
     * @param userId
     * @return
     */
    TaotaoResult addItemCartToRedis(TbItem tbItem,Integer num,Long userId);



    /**
     * 从redis中获取购物车商品信息
     * @param itemId
     * @param userId
     * @return
     */
    TbItem getCartListByRedis(Long itemId,Long userId);


    /**
     * 根据用户id查询购物车商品列表
     * @param userId
     * @return
     */
    List<TbItem> getCartItemByUserId(Long userId);

    /**
     * 根据userId和itemId更新redis中商品列表
     * @param userId
     * @param itemId
     * @param num
     * @return
     */
    TaotaoResult updateCartById(Long userId,Long itemId,Integer num);

    /**
     * 根据id删除redis中的购物车商品
     * @param userId
     * @param itemId
     * @return
     */
    TaotaoResult deleteCartById(Long userId,Long itemId);
}

