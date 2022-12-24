package com.zzw.dianping.controller;

import com.zzw.dianping.common.*;
import com.zzw.dianping.model.userModel;
import com.zzw.dianping.request.loginRequest;
import com.zzw.dianping.request.registerRequest;
import com.zzw.dianping.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

@Controller("/user")
@RequestMapping("/user")
public class userController {

    public static  final String CURRENT_USER_SESSION = "currentUserSession";

    @Autowired
    public UserService userService;

    @Autowired
    private HttpServletRequest httpServletRequest;

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


    //用户注册方法
    @RequestMapping("/register")
    @ResponseBody
    public commonRes register(@Valid @RequestBody registerRequest registerRequest, BindingResult bindingResult) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {

        //参数校验
        if(bindingResult.hasErrors()){
            throw new BusinessException(EmBusinessError.Validate_Parameter_Error, commonUtils.precessErrorString(bindingResult));
        }

        userModel userModel = new userModel();
        userModel.setTelphone(registerRequest.getTelphone());
        userModel.setPassword(registerRequest.getPassword());
        userModel.setNickName(registerRequest.getNickName());
        userModel.setGender(registerRequest.getGender());

        userModel registerUser = userService.registerUser(userModel);

        return commonRes.creat(registerUser);
    }

    @RequestMapping("/login")
    @ResponseBody
    public commonRes login(@Valid @RequestBody loginRequest loginRequest, BindingResult bindingResult) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {

        //参数校验
        if(bindingResult.hasErrors()){
            throw new BusinessException(EmBusinessError.Validate_Parameter_Error, commonUtils.precessErrorString(bindingResult));
        }

        userModel loginUser = userService.login(loginRequest.getTelphone(),loginRequest.getPassword());

        httpServletRequest.getSession().setAttribute(CURRENT_USER_SESSION,loginUser);

        return commonRes.creat(loginUser);
    }



    //用户注销
    @RequestMapping("/logout")
    @ResponseBody
    public commonRes logout(){

        httpServletRequest.getSession().invalidate();

        return commonRes.creat(null);
    }

    //从登陆的用户中获取当前用户的信息
    @RequestMapping("/getCurrentUser")
    @ResponseBody
    public commonRes getCurrentUser(){

        userModel currentUser = (userModel)httpServletRequest.getSession().getAttribute(CURRENT_USER_SESSION);

        return commonRes.creat(currentUser);
    }
}
