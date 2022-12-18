package com.zzw.dianping.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Test {


    @GetMapping("/test")
    @ResponseBody
    public String test(){
        return "test";
    }
}
