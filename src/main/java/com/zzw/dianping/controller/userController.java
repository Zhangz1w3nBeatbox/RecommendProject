package com.zzw.dianping.controller;

import com.zzw.dianping.model.userModel;
import com.zzw.dianping.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Controller("/user")
@RequestMapping("/user")
public class userController {

    @Autowired
    public UserService userService;

    @RequestMapping("/get")
    @ResponseBody
    public userModel get(@RequestParam(name = "id") Integer id){

        System.out.println(id);
        return userService.getUser(id);
    }
}
