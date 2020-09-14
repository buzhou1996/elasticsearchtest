package com.example.esdemo.mapper;

import com.example.esdemo.entity.Student;
import com.example.esdemo.entity.StudentDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface StudentMapper {
    List<Student> findAll();

    int selectMaxSize();

    List<String> getNameList();

    List<StudentDto> findAll2();
}
