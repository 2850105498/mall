package com.taotao.content.service.impl;

import com.taotao.common.pojo.EasyUITreeNode;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.content.service.ContentCategoryService;
import com.taotao.mapper.TbContentCategoryMapper;
import com.taotao.pojo.TbContentCategory;
import com.taotao.pojo.TbContentCategoryExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description:内容分类管理
 * @author:cxg
 * @Date:${time}
 */
@Service
public class ContentCategoryServiceImpl implements ContentCategoryService {
    @Autowired
    private TbContentCategoryMapper tbContentCategoryMapper;



    /**
     * 内容管理分类目录
     *
     * @param parentId 节点
     * @return
     */
    @Override
    public List<EasyUITreeNode> getContentCategoryList(Long parentId) {

        //创建查询条件
        TbContentCategoryExample example = new TbContentCategoryExample();
        TbContentCategoryExample.Criteria criteria = example.createCriteria();
        criteria.andParentIdEqualTo(parentId);
        //执行查询条件
        List<TbContentCategory> list = tbContentCategoryMapper.selectByExample(example);

        //把list转成EasyUITreeNode
        List<EasyUITreeNode> resultList = new ArrayList<>();
        for (TbContentCategory tbContentCategory : list) {
            EasyUITreeNode node = new EasyUITreeNode();
            node.setId(tbContentCategory.getId());
            node.setText(tbContentCategory.getName());
            node.setState(tbContentCategory.getIsParent() ? "closed" : "open");
            resultList.add(node);
        }
        return resultList;
    }

    /**
     * 创建叶子节点
     *
     * @param parentId 节点的id
     * @param name     节点名字
     * @return
     */
    @Override
    public TaotaoResult addContentCategory(Long parentId, String name) {
        //创建TBContentCategory对象
        TbContentCategory tbContentCategory = new TbContentCategory();
        //是不是父节点
        tbContentCategory.setIsParent(false);
        //设置parentId值
        tbContentCategory.setParentId(parentId);
        //设置名字
        tbContentCategory.setName(name);
        //设置创建时间
        tbContentCategory.setCreated(new Date());
        //设置修改时间
        tbContentCategory.setUpdated(new Date());
        //设置状态
        tbContentCategory.setStatus(1);
        //排列序号
        tbContentCategory.setSortOrder(1);
        tbContentCategoryMapper.insert(tbContentCategory);
        // 3、判断父节点的isparent是否为true，不是true需要改为true。
        TbContentCategory parentNode = tbContentCategoryMapper.selectByPrimaryKey(parentId);
        if (!parentNode.getIsParent()) {
            //如果父节点为叶子节点应该改为父节点
            parentNode.setIsParent(true);
            //更新父节点
            tbContentCategoryMapper.updateByPrimaryKey(parentNode);
        }
        // 4、需要主键返回。
        // 5、返回TaotaoResult，其中包装TbContentCategory对象


        return TaotaoResult.ok(tbContentCategory);
    }

    /**
     * 重命名
     *
     * @param id
     * @param name
     * @return
     */
    @Override
    public TaotaoResult updateContentCategory(Long id, String name) {
        //创建tbContentCategory对象
        TbContentCategory tbContentCategory = new TbContentCategory();
        //设置id
        tbContentCategory.setId(id);
        //设置名字
        tbContentCategory.setName(name);
        //更新修改时间
        tbContentCategory.setUpdated(new Date());
        //执行查询
        tbContentCategoryMapper.updateByPrimaryKeySelective(tbContentCategory);
        return TaotaoResult.ok();
    }

    /**
     * 删除节点
     */
    @Override
    public TaotaoResult deleteContentCategory(Long id) {
        //1.获取删除节点的is_parent
        TbContentCategory tbContentCategory = tbContentCategoryMapper.selectByPrimaryKey(id);
        //2.如果is_parent=false，说明要删除的节点不是父节点，可以删除。否则不允许删除
        //因为大多数删除节点是父节点的请求，都被js过滤掉了，所以将is_parent=false的情况放在最先执行
        if (!tbContentCategory.getIsParent()) {
            //3、删除

            tbContentCategoryMapper.deleteByPrimaryKey(id);
            //4.判断该节点的父节点是否还有子节点，如果没有需要把父节点的isparent改为false
            //查询根据parentId查询所有子节点节点
            Long parentId = tbContentCategory.getParentId();
            List<TbContentCategory> list = getContentCategoryListByParentId(parentId);
            if (list.size() == 0) {
                TbContentCategory parentNode = tbContentCategoryMapper.selectByPrimaryKey(parentId);
                parentNode.setIsParent(false);
            }
            return TaotaoResult.ok();
        } else {
            String msg = "请先删  " + tbContentCategory.getName() + " 分类下的所有子分类，再删除 " + tbContentCategory.getName() + "分类";
            TaotaoResult result = TaotaoResult.build(500, msg, null);
            return result;
        }
    }


    /**
     * 根据parentId查询子节点列表
     *
     * @param parentId
     * @return
     */
    private List<TbContentCategory> getContentCategoryListByParentId(long parentId) {
        TbContentCategoryExample example = new TbContentCategoryExample();
        TbContentCategoryExample.Criteria criteria = example.createCriteria();
        criteria.andParentIdEqualTo(parentId);
        List<TbContentCategory> list = tbContentCategoryMapper.selectByExample(example);
        return list;
    }
}
