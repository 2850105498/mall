package com.taotao.solrj;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Test;

import java.util.List;
import java.util.Map;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description:测试solrj
 * @author:cxg
 * @Date:${time}
 */
public class TestSolrJ {
    /**
     * 向solr中添加数据
     * @throws Exception
     */
    @Test
    public void testAddDocument() throws Exception {
        //创建一个SolrServer，使用httpSolrServer创建对象,用来写入数据库
        SolrServer solrServer =new HttpSolrServer("http://192.168.25.129:8080/solr/collection1");
        //创建一个文档对象SolrInputDocument对象
        SolrInputDocument document=new SolrInputDocument();
        //向文档中添加域，必须有id域，域的名称必须在schema.xml
        document.addField("id", "test001");
        document.addField("item_title", "测试商品");
        document.addField("item_price", "199");
        // 第五步：把文档添加到索引库中。
        solrServer.add(document);
        // 第六步：提交。
        solrServer.commit();

    }

    /**
     * 根据id删除数据
     * @throws Exception
     */
    @Test
    public void deleteDocumentById() throws Exception {
        // 第一步：创建一个SolrServer对象。
        SolrServer solrServer = new HttpSolrServer("http://192.168.25.129:8080/solr/collection1");
        // 第二步：调用SolrServer对象的根据id删除的方法。
        solrServer.deleteById("test001");
        // 第三步：提交。
        solrServer.commit();
    }

    /**
     * 选择id删除
     * @throws Exception
     */
    @Test
    public void deleteDocumentByQuery() throws Exception {
        SolrServer solrServer = new HttpSolrServer("http://192.168.25.129:8080/solr/collection1");
        //根据条件删除id为123的
        solrServer.deleteByQuery("title:123");
        solrServer.commit();
    }
    @Test
    public void queryDocument() throws Exception {
        // 第一步：创建一个SolrServer对象
        SolrServer solrServer = new HttpSolrServer("http://192.168.25.129:8080/solr/collection1");
        // 第二步：创建一个SolrQuery对象。
        SolrQuery query = new SolrQuery();
        // 第三步：向SolrQuery中添加查询条件、过滤条件。。。
        query.setQuery("*:*");
        // 第四步：执行查询。得到一个Response对象。
        QueryResponse response = solrServer.query(query);
        // 第五步：取查询结果。
        SolrDocumentList solrDocumentList = response.getResults();
        System.out.println("查询结果的总记录数：" + solrDocumentList.getNumFound());
        // 第六步：遍历结果并打印。
        for (SolrDocument solrDocument : solrDocumentList) {
            System.out.println(solrDocument.get("id"));
            System.out.println(solrDocument.get("item_title"));
            System.out.println(solrDocument.get("item_price"));
        }


    }


    @Test
    public void searchDocument() throws Exception {
        // 第一步：创建一个SolrServer对象
        SolrServer solrServer = new HttpSolrServer("http://192.168.25.129:8080/solr/collection1");
        // 第二步：创建一个SolrQuery对象。
        SolrQuery query = new SolrQuery();
        // 第三步：向SolrQuery中添加查询条件、过滤条件，分页条件，高亮。。
        query.setQuery("手机");
        //分页条件
        query.setStart(0);
        query.setRows(20);
        //设置默认搜索域
        query.set("df","item_keywords");
        //设置高亮
        query.setHighlight(true);
        //高亮显示的域
        query.addHighlightField("item_title");
        //高亮显示前缀，后缀
        query.setHighlightSimplePre("<div>");
        query.setHighlightSimplePost("</div>");

        // 第四步：执行查询。得到一个Response对象。
        QueryResponse response = solrServer.query(query);
        // 第五步：取查询结果。
        SolrDocumentList solrDocumentList = response.getResults();
        System.out.println("查询结果的总记录数：" + solrDocumentList.getNumFound());
        // 第六步：遍历结果并打印。
        for (SolrDocument solrDocument : solrDocumentList) {
            System.out.println(solrDocument.get("id"));
            //取高亮
            Map<String, Map<String, List<String>>> highlighting = response.getHighlighting();
            List<String> list = highlighting.get(solrDocument.get("id")).get("item_title");
            String itemTile="";
            if(list!=null&&list.size()>0){
                itemTile=list.get(0);
            }else {
                itemTile= (String) solrDocument.get("item_title");
            }
            System.out.println(solrDocument.get("itemTile"));
            System.out.println(solrDocument.get("item_price"));
            System.out.println(solrDocument.get("item_sell_point"));
            System.out.println(solrDocument.get("item_image"));
            System.out.println(solrDocument.get("item_category_name"));
            System.out.println("==============================");
        }
    }
















}
