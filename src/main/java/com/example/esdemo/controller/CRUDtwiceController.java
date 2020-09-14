package com.example.esdemo.controller;

import com.example.esdemo.EsdemoApplication;
import com.example.esdemo.util.ESConfig;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.*;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.metrics.max.Max;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.naming.directory.SearchResult;
import javax.print.DocFlavor;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class CRUDtwiceController {

    //查询删除
    @RequestMapping("BulkByScroll")
    public String BulkByScroll() {
        ESConfig esConfig = new ESConfig();
        String response = null;
        try {
            TransportClient client = esConfig.getTransportClient();
            BulkByScrollResponse bulkByScrollResponse = DeleteByQueryAction.INSTANCE
                    .newRequestBuilder(client)
                    .filter(QueryBuilders.matchQuery("name", "安妮宝贝"))
                    .source("lib3")
                    .get();
            long deleted = bulkByScrollResponse.getDeleted();
            response = String.valueOf(deleted);
            System.out.println("删除文档个数=" + deleted);
            //关闭客户端
            if (client != null) {
                client.close();
            }
        } catch (UnknownHostException e) {
            System.out.println("创建客户端连接失败，失败原因：" + e.getMessage());
            e.printStackTrace();
        }
        return response;
    }


    @RequestMapping("matchAll")
    private List<Map<String, Object>> matchAll() {
        ESConfig esConfig = new ESConfig();
        List<Map<String, Object>> responseList = new ArrayList<>();
        Map<String, Object> response = new HashMap<>();
        try {
            TransportClient client = esConfig.getTransportClient();
            QueryBuilder queryBuilder = QueryBuilders.matchAllQuery();
            SearchResponse searchResponse = client.prepareSearch("lib3")
                    .setQuery(queryBuilder)
                    .get();
            SearchHits hits = searchResponse.getHits();
            for (SearchHit hit : hits) {
                System.out.println(hit.getSourceAsString());
                responseList.add(hit.getSourceAsMap());
            }
            if (client != null) {
                client.close();
            }
        } catch (UnknownHostException e) {
            System.out.println("创建客户端连接失败，失败原因：" + e.getMessage());
            e.printStackTrace();
        }
        return responseList;
    }


    //match条件查询
    @RequestMapping("matchQuery")
    public List<String> MatchQuery() {
        ESConfig esConfig = new ESConfig();
        List<String> resopnse = null;
        try {
            TransportClient client = esConfig.getTransportClient();
            QueryBuilder queryBuilder = QueryBuilders.matchQuery("interests","睡觉");
            SearchResponse searchResponse = client.prepareSearch("lib3")
                    .setQuery(queryBuilder)
                    .setSize(3)
                    .get();
            SearchHits hits = searchResponse.getHits();
            for (SearchHit hit : hits) {
                System.out.println("查询到interests字段中包含睡觉的文档有："+hit.getSourceAsString());
                resopnse.add(hit.getSourceAsString());
            }
            if (client != null) {
                client.close();
            }
        } catch (UnknownHostException e) {
            System.out.println("创建客户端连接失败，失败原因：" + e.getMessage());
            e.printStackTrace();
        }
        return resopnse;
    }

    @RequestMapping("multMatchQuery")
    private List<String>  MultMatchQuery(){
        ESConfig esConfig = new ESConfig();
        List<String> response = null;

        try {
            TransportClient client = esConfig.getTransportClient();
            QueryBuilder queryBuilder = QueryBuilders.multiMatchQuery("睡觉","interests","address");
            SearchResponse searchResponse = client.prepareSearch("lib3")
                    .setQuery(queryBuilder)
                    .setSize(5)
                    .get();
            SearchHits hits = searchResponse.getHits();
            for (SearchHit hit : hits) {
                System.out.println("查询到interests字段中包含睡觉的文档有："+hit.getSourceAsString());
                response.add(hit.getSourceAsString());
            }
            if (client != null) {
                client.close();
            }
        } catch (UnknownHostException e) {
            System.out.println("创建客户端连接失败，失败原因：" + e.getMessage());
            e.printStackTrace();
        }
        return response;
    }


    @RequestMapping("term")
    public List<String> Term(){
        ESConfig esConfig = new ESConfig();
        List<String> response = null;
        try {
            TransportClient client = esConfig.getTransportClient();
            TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("name", "zhaoshengmeng");
            SearchResponse searchResponse = client.prepareSearch("lib3").setQuery(termQueryBuilder).setSize(2).get();
            SearchHits hits = searchResponse.getHits();
            for (SearchHit hit : hits) {
                System.out.println(hit.getSourceAsString());
                response.add(hit.getSourceAsString());
            }
        } catch (UnknownHostException e) {
            System.out.println("创建客户端连接失败，失败原因：" + e.getMessage());
            e.printStackTrace();
        }
        return response;
    }

    @RequestMapping("Terms")
    public List<String> Terms(){
        ESConfig esConfig = new ESConfig();
        List<String> response = new ArrayList<>();
        try {
            TransportClient client = esConfig.getTransportClient();
            //QueryBuilder queryBuilder = QueryBuilders.termsQuery("name", "zhaoshengmeng","jiahaoyu");
            //范围查询
            //QueryBuilder queryBuilder = QueryBuilders.rangeQuery("birthday").from("1995-06-20").to("1996-07-24");

            //prefix查询
            //QueryBuilder queryBuilder = QueryBuilders.prefixQuery("name", "zhao");

            //wildcard查询
            QueryBuilder queryBuilder = QueryBuilders.wildcardQuery("name", "zhao*");

            //quzzy查询
            //QueryBuilder queryBuilder = QueryBuilders.fuzzyQuery("name", "zha");

            //

            SearchResponse searchResponse = client.prepareSearch("lib3").setQuery(queryBuilder).setSize(2).get();
            SearchHits hits = searchResponse.getHits();
            for (SearchHit hit : hits) {
                System.out.println(hit.getSourceAsString());
                response.add(hit.getSourceAsString());
            }
        } catch (UnknownHostException e) {
            System.out.println("创建客户端连接失败，失败原因：" + e.getMessage());
            e.printStackTrace();
        }
        return response;
    }


    //聚合查询
    public void juhe(){
        ESConfig esConfig = new ESConfig();
        try {
            TransportClient client = esConfig.getTransportClient();
            AggregationBuilder agg = AggregationBuilders.max("ageMax").field("age");
            SearchResponse response = client.prepareSearch("lib4").addAggregation(agg).get();
            Max max = response.getAggregations().get("ageMax");
            System.out.println(max);
            System.out.println(max.toString());
            System.out.println(max.getValue());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    //query string查询
    public void queryString(){
        ESConfig esConfig = new ESConfig();
        try {
            TransportClient client = esConfig.getTransportClient();
            //全文搜索
            QueryBuilder queryBuilder = QueryBuilders.commonTermsQuery("name","五");
            //严格条件搜索
            QueryBuilder queryBuilder1 = QueryBuilders.queryStringQuery("+五  -段");
            //非严格条件搜索
            QueryBuilder queryBuilder2 = QueryBuilders.simpleQueryStringQuery("+五  -段");


            SearchResponse response = client.prepareSearch("lib4")
                    .setQuery(queryBuilder)
                    .get();

            SearchHits hits = response.getHits();
            for (SearchHit hit : hits) {
                System.out.println(hit.getSourceAsString());
            }
        } catch (UnknownHostException e) {
            System.out.println("连接客户端发生错误，错误原因："+e.getMessage());
            e.printStackTrace();
        }
    }



}
