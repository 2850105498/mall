package com.taotao.search.service.impl;

import com.taotao.common.pojo.SearchItem;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.search.mapper.SearchItemMapper;
import com.taotao.search.service.SearchItemService;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description:商品导入索引库
 * @author:cxg
 */
@Service
public class SearchItemServiceImpl implements SearchItemService {
    @Autowired
    private SearchItemMapper searchItemMapper;
    @Autowired
    private  SolrServer solrServer;

    @Override
    public TaotaoResult importItemsToIndex()  {

        try {
            //查询商品数据
            List<SearchItem> itemList = searchItemMapper.getItemList();
            //遍历商品数据添加到索引库
            for ( SearchItem searchItem:itemList) {
                //为每个商品创建一个SolrInputDocument对象。
                SolrInputDocument document = new SolrInputDocument();
                // 为文档添加域
                document.addField("id", searchItem.getId());
                document.addField("item_title", searchItem.getTitle());
                document.addField("item_sell_point", searchItem.getSell_point());
                document.addField("item_price", searchItem.getPrice());
                document.addField("item_image", searchItem.getImage());
                document.addField("item_category_name", searchItem.getCategory_name());
                document.addField("item_desc", searchItem.getItem_desc());
                // 向索引库中添加文档。
                solrServer.add(document);
            }

        }catch (Exception e){
            return TaotaoResult.build(500,"数据导入失败");
        }
        return TaotaoResult.ok();
    }
}
