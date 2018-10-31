package com.taotao.content.service;

import com.taotao.common.pojo.EasyUIDataGridResult;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.pojo.TbContent;

import java.util.List;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description:内容管理
 * @author:cxg
 * @Date:${time}
 */
public interface ContentService {
    /**
     * 通过分页查询内容管理
     * @param categoryId 要查询的id
     * @param page 当前页码
     * @param rows 每页记录数
     * @return
     */
    EasyUIDataGridResult getContentList(Long categoryId, Integer page, Integer rows);

    /**
     * 新增内容
     *
     * @param tbContent
     * @return
     */
    TaotaoResult saveContent(TbContent tbContent);


    /**
     * 更新内容
     * @param tbContent 内容信息
     * @return
     */
    TaotaoResult updateContent(TbContent tbContent);

    /**
     * 批量删除，循环删除
     * @param ids
     * @return
     */
    TaotaoResult deleteAllContent(List<Long> ids);

    /**
     * 通过id查询轮播图内容列表
     * @param cid
     * @return
     */
    List<TbContent> getContentByCid(Long cid);
}
