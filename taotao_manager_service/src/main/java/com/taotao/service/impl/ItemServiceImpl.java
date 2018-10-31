package com.taotao.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.taotao.common.pojo.EasyUIDataGridResult;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.util.IDUtils;
import com.taotao.common.util.JsonUtils;
import com.taotao.jedis.JedisClient;
import com.taotao.mapper.TbItemDescMapper;
import com.taotao.mapper.TbItemMapper;
import com.taotao.pojo.TbItem;
import com.taotao.pojo.TbItemDesc;
import com.taotao.pojo.TbItemDescExample;
import com.taotao.pojo.TbItemExample;
import com.taotao.service.ItemService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.jms.*;

import java.util.Date;
import java.util.List;


/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description:商品管理服务serviceImpl
 * @author:cxg
 * @Date:${time}
 */
@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private TbItemMapper tbItemMapper;
    @Autowired
    private TbItemDescMapper tbItemDescMapper;

    //注入applicationContext-activemq.xml定义的JmsTemplate
    @Autowired
    private JmsTemplate jmsTemplate;
    //注入applicationContext-activemq.xml定义的id=itemAddtopic
    @Resource(name = "itemAddtopic")
    private Destination destination;

    @Autowired
    private JedisClient jedisClient;

    //商品数据在redis中缓存的前缀
    @Value("${ITEM_INFO}")
    private String ITEM_INFO;

    //商品数据缓存时间，默认为一天
    @Value("${TIME_EXPIRE}")
    private Integer TIME_EXPIRE;

    /**
     * 添加redis缓存
     * 根据商品id查询商品信息
     *
     * @param itemId
     * @return
     */
    @Override
    public TbItem getItemById(long itemId) {
        //1、查询数据库之前先查询缓存
        try {
            //取缓存中的数据
            String json = jedisClient.get(ITEM_INFO + ":" + itemId + ":BASE");
            if (StringUtils.isNotBlank(json)) {
                //把json数据转成pojo
                TbItem tbItem = JsonUtils.jsonToPojo(json, TbItem.class);
                return tbItem;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //2、缓存中没有数据，则查询数据库
        TbItem item = tbItemMapper.selectByPrimaryKey(itemId);

        try {
            //把查询结果添加到缓存中，格式为ITEM_INFO:itemID:BASE: 商品信息
            jedisClient.set(ITEM_INFO + ":" + itemId + ":BASE", JsonUtils.objectToJson(item));
            //设置过期时间，提高缓存利用率
            jedisClient.expire(ITEM_INFO + ":" + itemId + ":BASE", TIME_EXPIRE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return item;
    }

    /**
     * 根据商品id查询商品描述信息
     * 添加redis缓存
     * @param itemId
     * @return
     */
    @Override
    public TbItemDesc getItemDescWeb(long itemId) {
        //1、查询缓存中是否有数据
        try {
            //取缓存数据
            String json = jedisClient.get(ITEM_INFO + ":" + itemId + ":DESC");
            if (StringUtils.isNotBlank(json)) {
                TbItemDesc tbItemDesc = JsonUtils.jsonToPojo(json, TbItemDesc.class);
                return tbItemDesc;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //2、缓存中没数据
        TbItemDesc tbItemDesc = tbItemDescMapper.selectByPrimaryKey(itemId);
        try {
            //把缓存中数据放缓存中
            jedisClient.set(ITEM_INFO + ":" + itemId + ":DESC", JsonUtils.objectToJson(tbItemDesc));
            //设置过期时间
            jedisClient.expire(ITEM_INFO+":"+itemId+":DESC",TIME_EXPIRE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tbItemDesc;
    }

    /**
     * 获取商品列表
     *
     * @param page 当前页码
     * @param rows 每页行数
     * @return
     */
    @Override
    public EasyUIDataGridResult getItemList(Integer page, Integer rows) {
        //1、设置分页信息使用pageHelper
        if (page == null) {
            page = 1;
        }
        if (page == null) {
            rows = 30;
        }
        PageHelper.startPage(page, rows);
        //执行查询
        //创建example对象不需要设置查询条件
        TbItemExample example = new TbItemExample();
        List<TbItem> list = tbItemMapper.selectByExample(example);
        //根据list，封装PageInfo
        PageInfo<TbItem> pageInfo = new PageInfo<>(list);
        //输出类型
        EasyUIDataGridResult result = new EasyUIDataGridResult();
        result.setRows(list);
        result.setTotal((int) pageInfo.getTotal());
        return result;
    }

    /**
     * 添加商品
     *
     * @param item 商品信息
     * @param desc 商品描述
     * @return
     */
    @Override
    public TaotaoResult addItem(TbItem item, String desc) {
        //使用工具类IDUtils.java生成商品id
        final long itemId = IDUtils.genItemId();
        //补全商品信息item的属性
        item.setId(itemId);
        //商品状态1-正常，2-下架，3-删除
        item.setStatus((byte) 1);
        item.setUpdated(new Date());
        item.setCreated(new Date());
        //向商品表插入数据
        tbItemMapper.insert(item);
        //创建一个商品描述表对应的pojo,创建一个TbItemDesc对象
        TbItemDesc tbItemDesc = new TbItemDesc();
        //补全商品描述TbItemDesc属性
        tbItemDesc.setItemId(itemId);
        tbItemDesc.setItemDesc(desc);
        tbItemDesc.setCreated(new Date());
        tbItemDesc.setUpdated(new Date());
        //向商品描述表插入
        tbItemDescMapper.insert(tbItemDesc);

        //向activemq发送商品添加信息
        jmsTemplate.send(destination, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {

                TextMessage textMessage = session.createTextMessage(itemId + "");
                return textMessage;
            }
        });
        //返回结果
        return TaotaoResult.ok();
    }

    /**
     * 根据id，更新商品状态1-正常，2-下架，3-删除
     *
     * @param ids    商品id
     * @param method 商品状态
     * @return
     */
    @Override
    public TaotaoResult updateItemStatus(List<Long> ids, String method) {
        TbItem item = new TbItem();
        if (method.equals("reshelf")) {
            // 正常，更新status=3即可
            item.setStatus((byte) 1);
        } else if (method.equals("instock")) {
            // 下架，更新status=3即可
            item.setStatus((byte) 2);
        } else if (method.equals("delete")) {
            // 删除，更新status=3即可
            item.setStatus((byte) 3);
        }

        for (Long id : ids) {
            item.setUpdated(new Date());
            // 创建查询条件，根据id更新
            TbItemExample tbItemExample = new TbItemExample();
            TbItemExample.Criteria criteria = tbItemExample.createCriteria();
            criteria.andIdEqualTo(id);
            // 第一个参数 是要修改的部分值组成的对象，其中有些属性为null则表示该项不修改。
            // 第二个参数 是一个对应的查询条件的类， 通过这个类可以实现 order by 和一部分的where 条件。
            tbItemMapper.updateByExampleSelective(item, tbItemExample);
        }
        return TaotaoResult.ok();
    }

    /**
     * 根据id批量删除商品信息
     *
     * @param ids
     * @return
     */
    @Override
    public TaotaoResult deleteAll(List<Long> ids) {

        for (Long id : ids) {
            tbItemMapper.deleteByPrimaryKey(id);
        }
        return TaotaoResult.ok();
    }

/*
     * 根据id批量删除商品信息
     *
     * @param ids
     * @return
     *
    @Override
    public TaotaoResult deleteAll(List<Long> ids) {

         TbItemExample tbItemExample=new TbItemExample();
        TbItemExample.Criteria criteria=tbItemExample.createCriteria();
        criteria.andIdIn(ids);
        tbItemMapper.deleteByExample(tbItemExample);
        return TaotaoResult.ok();
        return TaotaoResult.ok();
    }*/


    /**
     * 根据单个id删除商品信息
     *
     * @param ids
     * @return
     */
    @Override
    public TaotaoResult deleteItem(Long ids) {
        tbItemMapper.deleteByPrimaryKey(ids);
        return TaotaoResult.ok();
    }

    /**
     * 根据id查询商品描述
     *
     * @param id
     * @return
     */
    @Override
    public TaotaoResult getItemDesc(Long id) {
        TbItemDesc tbItemDesc = tbItemDescMapper.selectByPrimaryKey(id);
        return TaotaoResult.ok(tbItemDesc);
    }

    /**
     * 更新商品
     *
     * @param tbItem 商品信息
     * @param desc   商品描述
     * @return
     */
    @Override
    public TaotaoResult updateItem(TbItem tbItem, String desc) {
        //获取id
        Long id = tbItem.getId();
        // createcriteria，当没有规则时，则加入到现有规则，但有规则时，不再加入到现有规则，只是返回创建的规则
        // update tbItem from tb_item where id=id
        TbItemExample tbItemExample = new TbItemExample();
        TbItemExample.Criteria criteria = tbItemExample.createCriteria();
        criteria.andIdEqualTo(id);
        //更新商品修改时间时间
        tbItem.setUpdated(new Date());
        //更新商品信息
        tbItemMapper.updateByExampleSelective(tbItem, tbItemExample);

        TbItemDesc tbItemDesc = new TbItemDesc();
        tbItemDesc.setItemDesc(desc);
        TbItemDescExample tbItemDescExample = new TbItemDescExample();
        TbItemDescExample.Criteria criteria1 = tbItemDescExample.createCriteria();
        criteria1.andItemIdEqualTo(id);
        //更新商品描述修改时间
        tbItemDesc.setUpdated(new Date());
        //更新商品描述
        tbItemDescMapper.updateByExampleSelective(tbItemDesc, tbItemDescExample);
        return TaotaoResult.ok();
    }
}

