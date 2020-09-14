package com.example.esdemo.entity;

import lombok.Data;
import org.apache.ibatis.annotations.Param;

import java.beans.Transient;

@Data
public class StudentDto {

    private String id;


    private String name;
    private String age;
    private String sex;
    private String teacherCode;
    private String interests;


    private String pinyin;
}
