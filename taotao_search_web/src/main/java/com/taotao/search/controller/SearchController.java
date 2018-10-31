package com.taotao.search.controller;

import com.taotao.common.pojo.SearchResult;
import com.taotao.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description:搜索controller
 * @author:cxg
 * @Date:${time}
 */
@Controller
public class SearchController {

   @Autowired
    private SearchService searchService;

    @Value("${SEARCH_RESULT_ROWS}")
    private Integer SEARCH_RESULT_ROWS;


    @RequestMapping("search")
    public String search(@RequestParam("q") String queryString,
                         @RequestParam(defaultValue = "1") Integer page, Model model)throws Exception {


        queryString=new String(queryString.getBytes("iso8859-1"),"utf-8");
        //调用服务执行查询
        SearchResult searchResult = searchService.search(queryString, page, SEARCH_RESULT_ROWS);
        //传递给页面,query,totalPages,itemList,page在search.jsp中查找
        model.addAttribute("query", queryString);
        //总页数
        model.addAttribute("totalPages", searchResult.getTotalPages());
        //商品列表
        model.addAttribute("itemList", searchResult.getItemList());
        //当前页
        model.addAttribute("page", page);

        return "search";
    }
}
