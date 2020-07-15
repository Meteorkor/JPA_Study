package com.meteor.app.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmpController {

    @GetMapping(value = "/index", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object index(){

        return "hello";
    }
    @GetMapping("/api/v1/emps")
    public Object emps(){

        Emp emp = new Emp(0,"kim");
        return emp;
    }


    @Data
    @AllArgsConstructor
    static class Emp{
        private long empno;
        private String ename;
    }

}
