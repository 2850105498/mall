package com.taotao.order.service.Impl;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.jedis.JedisClient;
import com.taotao.mapper.TbOrderItemMapper;
import com.taotao.mapper.TbOrderMapper;
import com.taotao.mapper.TbOrderShippingMapper;
import com.taotao.order.pojo.OrderInfo;
import com.taotao.order.service.OrderService;
import com.taotao.pojo.TbOrderItem;
import com.taotao.pojo.TbOrderShipping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description:${todo}(用一句话描述该文件做什么)
 * @author:cxg
 * @Date:${time}
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private TbOrderItemMapper tbOrderItemMapper;

    @Autowired
    private TbOrderShippingMapper tbOrderShippingMapper;
    @Autowired
    private TbOrderMapper tbOrderMapper;


    //ORDER_ID_KEY：订单号key
    //ORDER_BEGIN_ID_KEY：订单初始值1006
    //ORDER_ITEM_ID_KEY:订单明细表key
    @Autowired
    private JedisClient jedisClient;
    @Override
    public TaotaoResult createOrder(OrderInfo orderInfo) {
        //1 判断订单key是否存在
        if(!jedisClient.exists("ORDER_ID")){
            //1.1 设置订单初始值
            jedisClient.set("ORDER_ID", "89382");
        }
        //2 生成订单号,可以使用redis的incr
        jedisClient.incr("ORDER_ID");
        String orderId=jedisClient.get("ORDER_ID");
        //3 向订单表插入数据，需要补全pojo属性
        orderInfo.setOrderId(orderId);
        orderInfo.setOrderId(orderId);
        orderInfo.setPostFee("0");
        //3.1 未付款，2、已付款，3、未发货，4、已发货，5、交易成功，6、交易关闭
        orderInfo.setStatus(1);
        orderInfo.setCreateTime(new Date());
        orderInfo.setUpdateTime(new Date());
        //3.2 插入订单表
        tbOrderMapper.insert(orderInfo);
        //4 向订单明细表插入数据
        //4.1 从orderInfo中取商品明细
        List<TbOrderItem> orderItems = orderInfo.getOrderItems();
        //4.2 循环生成ORDER_ITEM_ID_KEY（订单商品明细id）
        for (TbOrderItem tbOrderItem:orderItems) {
            jedisClient.incr("ORDER_ITEM_ID_KEY");
            String orderItemId =jedisClient.get("ORDER_ITEM_ID_KEY");
                    tbOrderItem.setId(orderItemId);
            tbOrderItem.setOrderId(orderId);
            tbOrderItemMapper.insert(tbOrderItem);

        }
        //5 物流订单表
        //5.1 从orderInfo中获取物流订单数据
        TbOrderShipping orderShipping = orderInfo.getOrderShipping();
        orderShipping.setOrderId(orderId);
        orderShipping.setCreated(new Date());
        orderShipping.setUpdated(new Date());
        tbOrderShippingMapper.insert(orderShipping);
        // 6、返回TaotaoResult。
        return TaotaoResult.ok(orderId);

    }
}
