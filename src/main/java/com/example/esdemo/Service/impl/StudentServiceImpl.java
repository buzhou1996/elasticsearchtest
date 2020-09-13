package com.example.esdemo.Service.impl;

import com.example.esdemo.Service.StudentService;
import com.example.esdemo.entity.Student;
import com.example.esdemo.mapper.StudentMapper;
import com.example.esdemo.util.ESConfig;
import com.example.esdemo.util.common.constant;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service("StudentService")
public class StudentServiceImpl implements StudentService {

    @Autowired(required = false)
    private StudentMapper studentMapper;

    @Override
    public List<Student> findAll() {
        List<Student> list = studentMapper.findAll();
        System.out.println(list);
        return list;
    }

    @Override
    public String AddDataToES() {
        List<Student> studentList = studentMapper.findAll();
        ESConfig esConfig = new ESConfig();
        BulkResponse response = null;
        try {
            TransportClient client = esConfig.getTransportClient();
            BulkRequestBuilder bulkRequestBuilder = client.prepareBulk();

            Class<Student> studentClass = Student.class;


            //将查询到的list<Student>数据添加到es中
            for (Student student : studentList) {
                if (student.getSex() == "0" || student.getSex().equals("0")) {
                    student.setSex(constant.SEX_CONDE_ZERO);
                } else if (student.getSex() == "1" || student.getSex().equals("1")) {
                    student.setSex(constant.SEX_CONDE_ONE);
                }
                bulkRequestBuilder.add(client.prepareIndex("lib4", "student", student.getId())
                        .setSource(
                                XContentFactory.jsonBuilder()
                                        .startObject()
                                        .field("id", student.getId())
                                        .field("name", student.getName())
                                        .field("age", student.getAge())
                                        .field("sex", student.getSex())
                                        .field("teacherCode", student.getTeacherCode())
                                        .field("interests", student.getInterests())
                                        .endObject()
                        )
                );
            }
            response = bulkRequestBuilder.get();
            System.out.println("添加响应报文：" + response.toString());
            System.out.println("结果标识：" + response.status());

        } catch (UnknownHostException e) {
            System.out.println("链接客户端错误，错误原因：" + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("添加数据错误，错误原因：" + e.getMessage());
            e.printStackTrace();
        }

        if (response.hasFailures()) {
            return constant.ADD_FAILD_MSG;
        } else {
            return constant.ADD_SUCC_MSG;
        }
    }

    @Override
    public List<Map> FindByWord(String word) {
        //判断是初始化操作还是搜索操作
        /*if (word==null||word.length()==0){

        }*/
        //先模糊查询
        ESConfig esConfig = new ESConfig();
        int maxSize = studentMapper.selectMaxSize();
        List<Map> studentList = new ArrayList<>();
        try {
            TransportClient client = esConfig.getTransportClient();
            QueryBuilder fuzzyQueryBuilder = QueryBuilders.fuzzyQuery("interests", "豆豆");
            //QueryBuilder queryBuilder = QueryBuilders.wildcardQuery("name", "zha*");
            SearchResponse searchResponse = client.prepareSearch("lib3").setQuery(fuzzyQueryBuilder).setSize(maxSize).get();
            SearchHits hits = searchResponse.getHits();
            for (SearchHit hit : hits) {
                Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                System.out.println("响应报文："+hit.getSourceAsString());
                if (!sourceAsMap.isEmpty()) {
                    studentList.add(sourceAsMap);
                }
            }
            if (client!=null){
                client.close();
            }
        } catch (UnknownHostException e) {
            System.out.println("链接客户端错误，错误原因：" + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("返回的数据为；"+studentList.toString());
        return studentList;
    }
}
