package com.example.esdemo.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.esdemo.Service.StudentService;
import com.example.esdemo.entity.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class ESController {

    @Autowired
    private StudentService studentService;

    //添加
    @RequestMapping("addDataToES")
    public String AddDataToES(){
        return studentService.AddDataToES();
    }


    //查询接口
    @RequestMapping("searchStudentMsg")
    public List<Map> SearchStudentMsg(@RequestBody String data){
        JSONObject jsonObject = JSONObject.parseObject(data);
        System.out.println("请求报文："+data);
        List<Map> studentList = studentService.FindByWord(jsonObject.getString("word"));
        return studentList;
    }

}
