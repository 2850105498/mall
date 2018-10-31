package com.taotao.cart.service.Impl;

import com.taotao.cart.service.CartService;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.util.JsonUtils;
import com.taotao.jedis.JedisClient;
import com.taotao.mapper.TbItemMapper;
import com.taotao.pojo.TbItem;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description:${todo}(用一句话描述该文件做什么)
 * @author:cxg
 * @Date:${time}
 */
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private TbItemMapper tbItemMapper;
    @Autowired
    private JedisClient jedisClient;
    /**
     * 登入状态添加商品到购物车到redis中
     *
     * @param tbItem
     * @param num
     * @param userId
     * @return
     */
    @Override
    public TaotaoResult addItemCartToRedis(TbItem tbItem, Integer num, Long userId) {
        //获取购物车商品信息
        TbItem cartList = getCartListByRedis(tbItem.getId(), userId);
        //判断是否有该商品
        if (cartList!=null){
            //有，设置数量，存入redis中
            //将商品数量添加
            cartList.setNum(cartList.getNum()+num);
            jedisClient.hset("REDIS_CART_KEY"+":"+userId,tbItem.getId()+"",JsonUtils.objectToJson(cartList));
        }else {
            //没有，设置商品数量，图片，存入redis中
            //直接赋值商品数量
            tbItem.setNum(num);
            String image = tbItem.getImage();
            if (StringUtils.isNotBlank(image)){
                String[] images = image.split(",");
                tbItem.setImage(images[0]);
            }
            jedisClient.hset("REDIS_CART_KEY" + ":" + userId, tbItem.getId() + "", JsonUtils.objectToJson(tbItem));

        }
        return TaotaoResult.ok();
    }

    /**
     * 根据用户和商品id获取购物车商品信息，取单条商品信息
     * @param itemId
     * @param userId
     * @return
     */
    @Override
    public TbItem getCartListByRedis(Long itemId, Long userId) {
        //从redis中取出商品信息
        String json = jedisClient.hget("REDIS_CART_KEY" + ":" + userId, itemId + "");
        if (StringUtils.isNotBlank(json)){
            TbItem tbItem = JsonUtils.jsonToPojo(json, TbItem.class);
            return tbItem;
        }
        return null;
    }

    /**
     * 登入状态根据用户id查询购物车商品列表，取多条商品信息
     *
     * @param userId
     * @return
     */
    @Override
    public List<TbItem> getCartItemByUserId(Long userId) {
        //从redis中获取
        Map<String, String> map = jedisClient.hgetAll("REDIS_CART_KEY" + ":" + userId);
        Set<Map.Entry<String, String>> set = map.entrySet();
        if (set != null) {
            List<TbItem> list = new ArrayList<>();
            //循环遍历Map转成pojo的list集合
            for (Map.Entry<String,String> entry:set) {
                TbItem tbItem = JsonUtils.jsonToPojo(entry.getValue(), TbItem.class);
                list.add(tbItem);
            }
            return list;
        }
        return null;
    }

    /**
     * 根据userId和itemId更新redis中商品列表
     *
     * @param userId
     * @param itemId
     * @param num
     * @return
     */
    @Override
    public TaotaoResult updateCartById(Long userId, Long itemId, Integer num) {
        //从redis中获取商品列表
        String json = jedisClient.hget("REDIS_CART_KEY" + ":" + userId, itemId + "");
        //判断是否为空
        if(StringUtils.isNotBlank(json)) {
            TbItem tbItem = JsonUtils.jsonToPojo(json, TbItem.class);
            tbItem.setNum(num);
            jedisClient.hset("REDIS_CART_KEY"+ ":" + userId, itemId + "",JsonUtils.objectToJson(tbItem));
        }
        return TaotaoResult.ok();
    }

    /**
     * 根据id删除redis中的购物车商品
     * @param userId
     * @param itemId
     * @return
     */
    @Override
    public TaotaoResult deleteCartById(Long userId, Long itemId) {
        jedisClient.hdel("REDIS_CART_KEY"+ ":" + userId, itemId + "");
        return TaotaoResult.ok();
    }


}
