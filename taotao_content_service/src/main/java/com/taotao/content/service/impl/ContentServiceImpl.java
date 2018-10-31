package com.taotao.content.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.taotao.common.pojo.EasyUIDataGridResult;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.util.JsonUtils;
import com.taotao.content.service.ContentService;
import com.taotao.jedis.JedisClient;
import com.taotao.mapper.TbContentMapper;
import com.taotao.pojo.TbContent;
import com.taotao.pojo.TbContentExample;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
public class ContentServiceImpl implements ContentService {
    @Autowired
    private TbContentMapper tbContentMapper;
    @Autowired
    private JedisClient jedisClient;

    @Value("${INDEX_CONTENT}")
    private String INDEX_CONTENT;
    /**
     * 查询的内容列表
     *
     * @param categoryId 要查询的id
     * @param page       当前页码
     * @param rows       每页记录数
     * @return
     */
    @Override
    public EasyUIDataGridResult getContentList(Long categoryId, Integer page, Integer rows) {

        //设置分页信息
        PageHelper.startPage(page, rows);
        //根据categoryId封装查询条件
        TbContentExample example = new TbContentExample();
        TbContentExample.Criteria criteria = example.createCriteria();
        criteria.andCategoryIdEqualTo(categoryId);
        //执行查询条件
        List<TbContent> list = tbContentMapper.selectByExample(example);

        //根据list，封装PageInfo
        PageInfo<TbContent> pageInfo = new PageInfo<>(list);
        //输出类型
        EasyUIDataGridResult result = new EasyUIDataGridResult();
        result.setRows(list);
        result.setTotal((int) pageInfo.getTotal());
        return result;
    }

    /**
     * 添加内容
     * @param tbContent
     * @return
     */
    @Override
    public TaotaoResult saveContent(TbContent tbContent) {

        //1.补全tbContent属性
        tbContent.setCreated(new Date());
        tbContent.setUpdated(tbContent.getCreated());
        //2.将tbContent属性，映射到数据库tb_Content表中
        tbContentMapper.insert(tbContent);
        //清空缓存
        try {
            jedisClient.hdel(INDEX_CONTENT, tbContent.toString());
            System.out.println("更新内容时，清空缓存!!!!!!!!!!");
        } catch (Exception e) {
            e.printStackTrace();
        }
        //3.返回结果
        return TaotaoResult.ok();
    }


    /**
     * 更新内容
     * @param tbContent 内容信息
     * @return
     */
    @Override
    public TaotaoResult updateContent(TbContent tbContent) {
        tbContent.setUpdated(new Date());
         tbContentMapper.updateByPrimaryKey(tbContent);
         //清空缓存
        try {
            jedisClient.hdel(INDEX_CONTENT, tbContent.getCategoryId().toString());
            System.out.println("更新内容时，清空缓存!!!!!!!!!!");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return TaotaoResult.ok();
    }

    /**
     * 批量删除，循环删除
     * @param ids
     * @return
     */
    @Override
    public TaotaoResult deleteAllContent(List<Long> ids) {
        //获取tbcontent
        TbContent tbContent = tbContentMapper.selectByPrimaryKey(ids.get(0));
        for (Long id:ids) {
            tbContentMapper.deleteByPrimaryKey(id);
        }
        //清空缓存
        try {
            jedisClient.hdel(INDEX_CONTENT,tbContent.getCategoryId().toString());
            System.out.println("更新内容时，清空缓存!!!!!!!!!!");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return TaotaoResult.ok();
    }

    /**
     * 通过id查询轮播图内容列表
     * @param cid
     * @return
     */
    @Override
    public List<TbContent> getContentByCid(Long cid) {
        //先查询缓存
        //添加缓存不能影响正常业务逻辑
        try {
            //查询缓存
            String json = jedisClient.hget(INDEX_CONTENT, cid + "");
            //查询到结果，把json转成list返回
            if (StringUtils.isNotBlank(json)){
                List<TbContent> list = JsonUtils.jsonToList(json, TbContent.class);
                return list;
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        TbContentExample example=new TbContentExample();
        TbContentExample.Criteria criteria = example.createCriteria();
        criteria.andCategoryIdEqualTo(cid);

        List<TbContent> list = tbContentMapper.selectByExample(example);

        //向缓存中添加数据
        try {
            //cid+"":将其转成字符串类型
            //JsonUtils.objectToJson(list):转成json类型
            jedisClient.hset(INDEX_CONTENT,cid+"", JsonUtils.objectToJson(list));

        }catch (Exception e){
            e.printStackTrace();
        }
        return list;
    }


}
