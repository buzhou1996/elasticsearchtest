package com.example.esdemo.Service;

import com.example.esdemo.entity.Student;

import java.util.List;
import java.util.Map;

public interface StudentService {
    List<Student> findAll();


    String AddDataToES();

    List<Map> FindByWord(String word);

    String updateIndex();

    //20200915 更新用
    String upsertToTWOIndex();

    //20200915 查询用
    List<Map<String,Object>> testSearchAll(String word);
}
