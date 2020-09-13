package com.example.esdemo.controller;

import com.example.esdemo.util.ESConfig;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetItemResponse;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.transport.TransportAddress;

import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.swing.table.TableRowSorter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
public class CRUDController {

    private ESConfig esConfig;

    @RequestMapping("query")
    public String CreateLinks() {
        //指定集群
        Settings settings = Settings.builder().put("cluster.name", "my-application").build();
        TransportClient client = null;
        String response = "查询失败";
        try {
            //连接客户端
            InetSocketTransportAddress node = new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"), 9300);
            client = new PreBuiltTransportClient(settings);
            client.addTransportAddress(node);

            //query操作
            GetResponse queryResponse = client.prepareGet("lib3", "user", "1").execute().actionGet();
            System.out.println("查询出的数据" + queryResponse.getSourceAsString());
            response = queryResponse.getSourceAsString();

        } catch (Exception e) {
            System.out.println("创建ES客户端失败，错误信息为=" + e.getMessage());
            e.printStackTrace();
        } finally {
            if (client != null) {
                client.close();
            }
        }
        return response;
    }


    //新增文档
    @RequestMapping("insert")
    public String insertType() {
        ESConfig esConfig = new ESConfig();
        String response = null;
        try {
            TransportClient client = esConfig.getTransportClient();
            XContentBuilder xContentBuilder = XContentFactory.jsonBuilder().startObject()
                    .field("name", "不周")
                    .field("address", "岳阳")
                    .field("age", "18")
                    .field("birthday", "2002-05-20")
                    .field("interests", "吃饭,睡觉,打豆豆")
                    .endObject();
            //添加文档
            IndexResponse indexResponse = client.prepareIndex("lib3", "user", "24").setSource(xContentBuilder).get();
            System.out.println(indexResponse);
            response = indexResponse.status().toString();
            if (client != null) {
                client.close();
            }
        } catch (UnknownHostException e) {
            System.out.println("连接客户端出错，错误原因：" + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("配置文档数据出错，错误原因：" + e.getMessage());
            e.printStackTrace();
        }
        if (!response.equals("CREATED")) {
            return "数据插入失败";
        } else {
            return "数据插入成功";
        }
    }


    //删除文档
    @RequestMapping("delete")
    public String deleteType() {
        ESConfig esConfig = new ESConfig();
        String response = null;
        try {
            TransportClient client = esConfig.getTransportClient();
            DeleteResponse deleteResponse = client.prepareDelete("lib3", "user", "6").get();
            System.out.println(deleteResponse);
            response = deleteResponse.status().toString();
            if (client!=null){
                client.close();
            }
        } catch (UnknownHostException e) {
            System.out.println("连接客户端出错，错误原因：" + e.getMessage());
            e.printStackTrace();
        }
        if (!response.equals("OK")) {
            return "数据删除失败";
        } else {
            return "数据删除成功";
        }
    }


    //更新文档
    @RequestMapping("update")
    public String update() {
        ESConfig esConfig = new ESConfig();
        String response = null;
        try {
            TransportClient client = esConfig.getTransportClient();
            UpdateRequest updateRequest = new UpdateRequest();
            updateRequest.index("lib3").type("user").id("24")
                    .doc(
                            XContentFactory.jsonBuilder().startObject()
                                    .field("birthday", "2020-05-20")
                                    .endObject()
                    );
            UpdateResponse updateResponse = client.update(updateRequest).get();
            System.out.println("update操作成功，响应报文："+updateResponse);
            response = updateResponse.status().toString();
            if (client!=null){
                client.close();
            }
        } catch (UnknownHostException e) {
            System.out.println("连接客户端出错，错误原因：" + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("添加修改文档错误，错误原因：" + e.getMessage());
            e.printStackTrace();
        } catch (InterruptedException e) {
            System.out.println("请求修改过程中错误，错误原因：" + e.getMessage());
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        if (!response.equals("OK")) {
            return "数据update失败";
        } else {
            return "数据update成功";
        }
    }

    /**
     * @Author:
     * @Data:2020/9/10 upsert 存在即修改，无则添加
     * @Params:[] Description:
     */
    @RequestMapping("upsert")
    public String upsert() {
        ESConfig esConfig = new ESConfig();
        String response = null;
        try {
            TransportClient client = esConfig.getTransportClient();
            //添加文档
            IndexRequest indexRequest = new IndexRequest("lin3", "user", "25")
                    .source(
                            XContentFactory.jsonBuilder()
                                    .startObject()
                                    .field("name", "不语")
                                    .field("address", "荣湾")
                                    .field("age", "18")
                                    .field("birthday", "2002-05-20")
                                    .field("interests", "吃饭,睡觉,打豆豆")
                                    .endObject()
                    );
            //修改文档
            UpdateRequest updateRequest = new UpdateRequest("lib3", "user", "25")
                    .doc(
                            XContentFactory.jsonBuilder().startObject()
                                    .field("address", "荣湾湖")
                                    .endObject()
                    ).upsert(indexRequest);//此处调用upsert操作

            UpdateResponse updateResponse = client.update(updateRequest).get();
            response = updateResponse.status().toString();
            if (client!=null){
                client.close();
            }
        } catch (UnknownHostException e) {
            System.out.println("连接客户端出错，错误原因：" + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("向request填充数据错误，错误原因：" + e.getMessage());
            e.printStackTrace();
        } catch (InterruptedException e) {
            System.out.println("请求upsert数据错误，错误原因：" + e.getMessage());
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        if (response.equals("OK") || response.equals("CREATED")) {
            return "数据upsert成功";
        } else {
            return "数据upsert失败";
        }
    }

    @RequestMapping("multiget")
    public List<String> multiget() {
        ESConfig esConfig = new ESConfig();
        String response = null;
        List<String> responseList = new ArrayList<>();
        try {
            TransportClient client = esConfig.getTransportClient();
            MultiGetResponse multiGetItemResponses = client.prepareMultiGet()
                    .add("lib3", "user", "1", "2", "3")
                    .get();
            for (MultiGetItemResponse multiGetItemRespons : multiGetItemResponses) {
                GetResponse response1 = multiGetItemRespons.getResponse();
                if (response1 != null && response1.isExists()) {
                    System.out.println(response1);
                    responseList.add(response1.toString());
                }
            }
            if (client!=null){
                client.close();
            }
        } catch (UnknownHostException e) {
            System.out.println("连接客户端出错，错误原因：" + e.getMessage());
            e.printStackTrace();
        }
        return responseList;
    }


    @RequestMapping("bulk")
    public String bulk() {
        ESConfig esConfig = new ESConfig();
        String response = null;
        try {
            TransportClient client = esConfig.getTransportClient();
            BulkRequestBuilder bulkRequestBuilder = client.prepareBulk();
            bulkRequestBuilder.add(client.prepareIndex("lib3", "user", "8")
                    .setSource(
                            XContentFactory.jsonBuilder()
                                    .startObject()
                                    .field("name", "韩寒")
                                    .endObject()
                    ));
            //可以批量添加，即bulkRequestBuilder多次添加
            BulkResponse bulkResponse = bulkRequestBuilder.get();
            System.out.println("添加类型响应报文：" + bulkResponse);
            if (bulkResponse.hasFailures()) {
                throw new RuntimeException("请求多次添加失败");
            } else {
                response = bulkResponse.status().toString();
            }
            if (client!=null){
                client.close();
            }
        } catch (UnknownHostException e) {
            System.out.println("连接客户端出错，错误原因：" + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("向request填充数据错误，错误原因：" + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("请求结果：" + response);
        return response;
    }



}



