package com.taotao.controller;

import com.taotao.common.pojo.EasyUIDataGridResult;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.content.service.ContentService;
import com.taotao.pojo.TbContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description:${todo}(用一句话描述该文件做什么)
 * @author:cxg
 * @Date:${time}
 */
@Controller
public class ContentController {

    @Autowired
    private ContentService contentService;

    /**
     * 内容分页管理查询
     * @param categoryId 页面获取的id
     * @param page 当前页码
     * @param rows 每页行数
     * @return
     */
    @RequestMapping("/content/query/list")
    @ResponseBody
    public EasyUIDataGridResult getContentList(Long categoryId, Integer page, Integer rows) {
        EasyUIDataGridResult result = contentService.getContentList(categoryId, page, rows);
        return result;
    }
    /**
     * 保存内容
     * @param tbContent
     * @return
     */
    @RequestMapping("/content/save")
    @ResponseBody
    public TaotaoResult saveContent(TbContent tbContent) {
        TaotaoResult result = contentService.saveContent(tbContent);
        return result;
    }


    /**
     * 更新内容
     * @param tbContent 内容信息
     * @return
     */
    @RequestMapping("/rest/content/edit")
    @ResponseBody
    public TaotaoResult updateContent(TbContent tbContent) {
        TaotaoResult result=contentService.updateContent(tbContent);
        return result;
    }

    /**
     * 批量删除，循环删除
     * @param ids
     * @return
     */
    @RequestMapping("/content/delete")
    @ResponseBody
    public TaotaoResult deleteAllContent(@RequestParam(value = "ids") List<Long> ids) {
        TaotaoResult result=contentService.deleteAllContent(ids);
        return result;
    }
}
