package com.zzw.dianping.controller;

import com.zzw.dianping.common.BusinessException;
import com.zzw.dianping.common.EmBusinessError;
import com.zzw.dianping.common.commonError;
import com.zzw.dianping.common.commonRes;
import com.zzw.dianping.model.userModel;
import com.zzw.dianping.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller("/user")
@RequestMapping("/user")
public class userController {

    @Autowired
    public UserService userService;

    @RequestMapping("/get")
    @ResponseBody
    public commonRes get(@RequestParam(name = "id") Integer id) throws BusinessException {
        userModel userModel = userService.getUser(id);

        if(userModel==null){
//            EmBusinessError noObjectFound = EmBusinessError.NO_OBJECT_FOUND;
//            commonError errorData = new commonError(noObjectFound);
//            commonRes failRes = commonRes.creat(errorData, "fail");
//            return failRes;

            throw new BusinessException(EmBusinessError.NO_OBJECT_FOUND);
        }

        return commonRes.creat(userModel);
    }


    //thymeleaf测试
    @RequestMapping("/index")
    public ModelAndView index() {
        ModelAndView modelAndView = new ModelAndView("/index.html");
        return modelAndView;
    }
}
