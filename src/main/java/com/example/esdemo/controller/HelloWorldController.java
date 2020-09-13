package com.example.esdemo.controller;

import com.example.esdemo.Service.StudentService;
import com.example.esdemo.entity.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class HelloWorldController {

    @Autowired
    private StudentService studentService;

    @RequestMapping("index")
    public String index(){
        return "HELLO WORLD";
    }

    @RequestMapping("findAll")
    public List<Student> findAll(){
        return studentService.findAll();
    }
}
