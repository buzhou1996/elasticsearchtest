package com.example.esdemo.controller;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.net.URL;

import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

   /* @Value("${spring.data.elasticsearch.cluster-nodes}")
    private String clusterNodes;
    private static final Logger LOG = LoggerFactory.getLogger(TestController.class);
    private RestHighLevelClient restHighLevelClient;


    public void destroy() throws Exception {
        try {
            if (restHighLevelClient!=null){
                restHighLevelClient.close();
            }
        }catch (IOException e){
            LOG.error("Error closing ElasticSearch client: ", e);
        }finally {

        }
    }


    public RestHighLevelClient getObject() throws Exception {
        return restHighLevelClient;
    }


    public Class<?> getObjectType() {
        return RestHighLevelClient.class;
    }


    public void afterPropertiesSet() throws Exception {
        String[] urlList = clusterNodes.split(",");
        HttpHost[] nodes = new HttpHost[urlList.length];
        for (int i = 0; i < urlList.length; i++) {
            URL url = new URL(urlList[i]);
            HttpHost node = new HttpHost(url.getHost(),url.getPort(),url.getProtocol());
            nodes[i] = node;
        }
        RestClientBuilder builder = RestClient.builder(nodes);
        restHighLevelClient = new RestHighLevelClient(builder);
    }*/
}
