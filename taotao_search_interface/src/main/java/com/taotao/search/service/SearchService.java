package com.taotao.search.service;

import com.taotao.common.pojo.SearchResult;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description:搜索功能
 * @author:cxg
 * @Date:${time}
 */
public interface SearchService {
    /**
     *
     * @param queryString 查询条件
     * @param page 当前页码
     * @param rows 每页行数
     * @return
     */
    SearchResult search(String queryString,int page, int rows)throws  Exception;
}
