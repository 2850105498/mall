package com.taotao.content.service;

import com.taotao.common.pojo.EasyUITreeNode;
import com.taotao.common.pojo.TaotaoResult;

import java.util.List;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description:内容分类管理
 * @author:cxg
 * @Date:${time}
 */

public interface ContentCategoryService {

    /**
     * 内容分类
     * @param parentId 节点
     * @return
     */
    List<EasyUITreeNode> getContentCategoryList(Long parentId);

    /**
     * 添加节点
     * @param parentId 节点的id
     * @param name  节点名字
     * @return
     */
    TaotaoResult addContentCategory(Long parentId,String name);

    /**
     * 重命名
     *
     * @param id
     * @param name
     * @return
     */
    TaotaoResult updateContentCategory(Long id, String name);

    /**
     * 删除内容分类
     * @param id
     * @return
     */
    TaotaoResult deleteContentCategory(Long id);


}
