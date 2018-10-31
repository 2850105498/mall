package com.taotao.search.dao;

import com.taotao.common.pojo.SearchItem;
import com.taotao.common.pojo.SearchResult;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description:查询索引库dao
 * @author:cxg
 * @Date:${time}
 */
@Repository
public class SearchDao {

    @Autowired
    private SolrServer solrServer;

    public SearchResult search(SolrQuery query) throws Exception {
        //根据query对象查询索引库
        QueryResponse response = solrServer.query(query);
        //取商品列表
        SolrDocumentList solrDocumentList = response.getResults();
        //取查询结果总记录数
        long numFound = solrDocumentList.getNumFound();
        SearchResult result=new SearchResult();
        result.setRecordCount(numFound);

        //商品列表
        List<SearchItem> itemList = new ArrayList<>();
        //循环遍历商品列表
        for (SolrDocument solrDocument : solrDocumentList) {
            SearchItem item = new SearchItem();
            item.setId((String) solrDocument.get("id"));
            item.setCategory_name((String) solrDocument.get("item_category_name"));
            //取一张图片
            String image= (String) solrDocument.get("item_image");
            if(StringUtils.isNotBlank(image)){
                image=image.split(",")[0];
            }
            item.setImage(image);

            item.setPrice((long) solrDocument.get("item_price"));
            item.setSell_point((String) solrDocument.get("item_sell_point"));
            //取高亮显示
            Map<String, Map<String, List<String>>> highlighting = response.getHighlighting();
            List<String> list = highlighting.get(solrDocument.get("id")).get("item_title");
            String title = "";
            //有高亮显示的内容时。
            if (list != null && list.size() > 0) {
                title = list.get(0);
            } else {
                title = (String) solrDocument.get("item_title");
            }
            item.setTitle(title);
            //添加到商品列表
            itemList.add(item);
        }

        //商品列表
        result.setItemList(itemList);
        //总记录数
        result.setRecordCount(solrDocumentList.getNumFound());

        return result;
    }
}

