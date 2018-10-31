package com.taotao.controller;

import com.taotao.common.pojo.EasyUITreeNode;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.content.service.ContentCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description:内容分类管理controller
 * @author:cxg
 * @Date:${time}
 */
@Controller
public class ContentCategoryController {
    @Autowired
    private ContentCategoryService contentCategoryService;



    /**
     * 内容分类
     * @param parentId 节点
     * @return
     */
    @RequestMapping("/content/category/list")
    @ResponseBody
    public List<EasyUITreeNode> getContentCategoryList(@RequestParam(value = "id",defaultValue="0")Long parentId){

        List<EasyUITreeNode> List = contentCategoryService.getContentCategoryList(parentId);
        return List;
    }

    /**
     *创建叶子节点
     * @param parentId
     * @param name
     * @return
     */
    @RequestMapping("/content/category/create")
    @ResponseBody
    public TaotaoResult createCategory(Long parentId, String name) {
        TaotaoResult result = contentCategoryService.addContentCategory(parentId, name);
        return result;
    }


    /**
     * 重新命名
     * @param id
     * @param name
     * @return
     */
    @RequestMapping("/content/category/update")
    @ResponseBody
    public TaotaoResult updateContentCategory(Long id, String name) {
        TaotaoResult result =contentCategoryService.updateContentCategory(id,name);
        return result;

    }

    /**
     * 根据id删除
     * @param id
     * @return
     */
    @RequestMapping("/content/category/delete")
    @ResponseBody
    public TaotaoResult deleteContentCategory(Long id) {
        TaotaoResult result = contentCategoryService.deleteContentCategory(id);
        return result;
    }
}
