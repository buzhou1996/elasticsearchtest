package com.example.esdemo.Service.impl;

import com.example.esdemo.Service.StudentService;
import com.example.esdemo.entity.Student;
import com.example.esdemo.entity.StudentDto;
import com.example.esdemo.mapper.StudentMapper;
import com.example.esdemo.util.ESConfig;
import com.example.esdemo.util.common.InitailOrderUtils;
import com.example.esdemo.util.common.constant;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateRequestBuilder;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
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


    //更新索引文档内容
    public String updateIndex() {
        //int i = studentMapper.selectMaxSize();
        ESConfig esConfig = new ESConfig();
        //查询索引里的文档个数
        //如无文档，就新增所有数据到文档中
        //如有文档，对比两者文档数，不一致即更新

        //尝试upsert
        List<Student> studentList = studentMapper.findAll();
        List<StudentDto> convert = convert(studentList);
        IndexResponse indexResponse = null;
        try {
            TransportClient client = esConfig.getTransportClient();
            IndexRequestBuilder indexRequestBuilder = client.prepareIndex("lib4", "student");
            for (StudentDto student : convert) {
                if (student.getSex() == "0" || student.getSex().equals("0")) {
                    student.setSex(constant.SEX_CONDE_ZERO);
                } else if (student.getSex() == "1" || student.getSex().equals("1")) {
                    student.setSex(constant.SEX_CONDE_ONE);
                }
                indexRequestBuilder.setId(student.getId()).setSource(
                        XContentFactory.jsonBuilder().startObject()
                                .field("id", student.getId())
                                .field("name", student.getName())
                                .field("age", student.getAge())
                                .field("sex", student.getSex())
                                .field("teacherCode", student.getTeacherCode())
                                .field("interests", student.getInterests())
                                .field("pinyin",student.getPinyin())
                                .endObject()

                );
                indexResponse = indexRequestBuilder.get();
                System.out.println("添加数据的响应报文：" + indexResponse.toString());
            }
        } catch (UnknownHostException e) {
            System.out.println("创建客户端错误，错误信息为：" + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("插入数据错误，错误信息为：" + e.getMessage());
            e.printStackTrace();
        }
        if ("CREATED" != indexResponse.status().toString()) {
            return constant.ADD_FAILD_MSG;
        } else {
            return constant.ADD_SUCC_MSG;
        }
    }



    public List<StudentDto> convert(List<Student> studentList){
        List<String> nameList = studentMapper.getNameList();
        List<StudentDto> studentDtos = new ArrayList<>();
        for (Student student : studentList) {
            String pingYin = InitailOrderUtils.getPingYin(student.getStuName());
            StudentDto dto = new StudentDto();
            dto.setPinyin(pingYin);
            dto.setId(student.getId());
            dto.setName(student.getStuName());
            dto.setAge(student.getAge());
            dto.setSex(student.getSex());
            dto.setTeacherCode(student.getTeacherCode());
            dto.setInterests(student.getInterests());
            studentDtos.add(dto);
        }
        return studentDtos;
    }

    //新增
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
                UpdateRequestBuilder updateRequestBuilder = client.prepareUpdate();
                //updateRequestBuilder.setDocAsUpsert();
                bulkRequestBuilder.add(client.prepareIndex("lib4", "student", student.getId())
                        .setSource(
                                XContentFactory.jsonBuilder()
                                        .startObject()
                                        .field("id", student.getId())
                                        .field("name", student.getStuName())
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

        ESConfig esConfig = new ESConfig();
        int maxSize = studentMapper.selectMaxSize();
        List<Map> studentList = new ArrayList<>();
        //判断是否全汉字
        Boolean flag = InitailOrderUtils.checkChineseWord(word);
        //汉字
        if (flag){
            //直接走模糊查询

        }else {
            //非全汉字
            //查询pinyin字段和首字母的字段两个字段，根据


        }
        try {
            /*TransportClient client = esConfig.getTransportClient();
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
            }*/
            TransportClient client = esConfig.getTransportClient();
            studentList = searchByName(word, client);
            if (client != null) {
                client.close();
            }
        } catch (UnknownHostException e) {
            System.out.println("链接客户端错误，错误原因：" + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("返回的报文为；" + studentList.toString());
        return studentList;
    }


    public void testSearchAll() {

    }


    public List<Map> searchByName(String name, TransportClient client) {
        System.out.println("根据名字模糊查询");
        List<Map> responseList = new ArrayList<>();
        //使用模糊匹配查询
        SearchRequestBuilder searchRequestBuilder = client.prepareSearch("lib4").setTypes("student");
        BoolQueryBuilder bqd = QueryBuilders.boolQuery();

        QueryBuilder queryBuilder1 = QueryBuilders.matchPhraseQuery("name", name);
        //QueryBuilder queryBuilder2 = QueryBuilders.matchPhraseQuery();
        BoolQueryBuilder bqd1 = QueryBuilders.boolQuery().must(queryBuilder1);

        bqd.should(bqd1);
        searchRequestBuilder.setQuery(bqd);
        SearchResponse searchResponse = searchRequestBuilder.setFrom(0).setSize(1000).execute().actionGet();
        List<HashMap> list = new ArrayList<>();
        for (SearchHit hit : searchResponse.getHits()) {
            list.add((HashMap) hit.getSourceAsMap());
        }

        for (HashMap hashMap : list) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("studentName", hashMap.get("name"));
            map.put("id",hashMap.get("id"));
            map.put("interests",hashMap.get("interests"));
            responseList.add(map);
        }
        System.out.println("返回的数据" + responseList);
        return responseList;
    }




    //测试
    @Test
    public void test(){
        String a = "wangzherongyao";
        String cnASCII = InitailOrderUtils.getCnASCII(a);
        System.out.println(cnASCII);
    }
}
