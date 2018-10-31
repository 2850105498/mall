package com.taotao.search.service.impl;

import com.taotao.common.pojo.SearchResult;
import com.taotao.search.dao.SearchDao;
import com.taotao.search.service.SearchService;
import org.apache.solr.client.solrj.SolrQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description:${todo}(用一句话描述该文件做什么)
 * @author:cxg
 * @Date:${time}
 */
@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    private SearchDao searchDao;

    /**
     *
     * @param queryString 查询条件
     * @param page 当前页码
     * @param rows 每页行数
     * @return
     */
    @Override
    public SearchResult search(String queryString, int page, int rows)throws Exception {
        //创建一个SolrQuery对象
        SolrQuery solrQuery=new SolrQuery();
        //设置查询条件
        solrQuery.setQuery(queryString);

        //设置分页条件
        if(page<1){
            page=1;
        }
        solrQuery.setStart((page-1)*rows);
        if(rows<1){
            rows=10;
        }
        solrQuery.setRows(rows);
        //指定搜索域，df是solr默认
        solrQuery.set("df","item_title");
        //设置高亮,开启高亮true
        solrQuery.setHighlight(true);
        //设置高亮显示的域
        solrQuery.addHighlightField("item_title");
        //高亮的前缀和后缀
        solrQuery.setHighlightSimplePre("<em style=\"color:red\">");
        solrQuery.setHighlightSimplePost("</em>");
        //执行查询，调用SearchDao，得到SearchResult
        SearchResult searchResult = searchDao.search(solrQuery);
        //计算总页数
        long recordCount = searchResult.getRecordCount();
        //总页数
        long pages = recordCount / rows;

        if(recordCount%rows>0){
            pages++;
        }

        //传递总页数
        searchResult.setTotalPages(pages);


        return searchResult;
    }
}
