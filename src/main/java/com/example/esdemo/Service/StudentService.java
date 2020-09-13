package com.example.esdemo.Service;

import com.example.esdemo.entity.Student;

import java.util.List;
import java.util.Map;

public interface StudentService {
    List<Student> findAll();


    String AddDataToES();

    List<Map> FindByWord(String word);
}
