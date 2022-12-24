package com.zzw.dianping.controller.admin;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zzw.dianping.common.*;
import com.zzw.dianping.model.sellerModel;
import com.zzw.dianping.request.pageQuery;
import com.zzw.dianping.request.sellerRequest;
import com.zzw.dianping.service.sellerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import sun.misc.BASE64Encoder;

import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Controller("/admin/seller")
@RequestMapping("/admin/seller")
public class sellerController {

    @Autowired
    public sellerService sellerService;


    //获取所有商户列表
    @RequestMapping("/index")
    @AdminPermission
    public ModelAndView index(pageQuery pageQuery) {

        //分页
        PageHelper.startPage(pageQuery.getPage(),pageQuery.getSize());

        List<sellerModel> sellersList = sellerService.selectAll();

        //封装完后的信息  page size 第几页 total
        PageInfo<sellerModel> pageInfo = new PageInfo<>(sellersList);

        System.out.println(pageInfo.getSize());
        System.out.println(pageInfo.getTotal());



        ModelAndView modelAndView = new ModelAndView("/admin/seller/index.html");
        modelAndView.addObject("data",pageInfo);
        modelAndView.addObject("CONTROLLER_NAME","seller");
        modelAndView.addObject("ACTION_NAME","index");

        return modelAndView;
    }


    //创建商家页面
    @RequestMapping("/createpage")
    @AdminPermission
    public ModelAndView createpage() {

        ModelAndView modelAndView = new ModelAndView("/admin/seller/create.html");
        modelAndView.addObject("CONTROLLER_NAME","seller");
        modelAndView.addObject("ACTION_NAME","index");
        //modelAndView.addObject("data",sellersList);
        return modelAndView;
    }

    //创建商家页面
    @RequestMapping(value = "/create",method = RequestMethod.POST)
    @AdminPermission
    public String create(@Valid sellerRequest sellerRequest, BindingResult bindingResult) throws BusinessException {
        if(bindingResult.hasErrors()){
            throw  new BusinessException(EmBusinessError.Validate_Parameter_Error,commonUtils.precessErrorString(bindingResult));
        }
        sellerModel sellerModel = new sellerModel();
        sellerModel.setName(sellerRequest.getName());
        sellerService.create(sellerModel);
        return "redirect:/admin/seller/index";
    }



    private String encodeByMD5(String password) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        BASE64Encoder base64Encoder = new BASE64Encoder();
        String md5Password = base64Encoder.encode(md5.digest(password.getBytes("utf-8")));
        return md5Password;
    }





    //禁用商家down
    @RequestMapping(value = "/down",method = RequestMethod.POST)
    @AdminPermission
    @ResponseBody
    public commonRes down(@RequestParam(value = "id") Integer id) throws BusinessException {
        sellerModel sellerModel = sellerService.changeStatus(id, 1);


        return commonRes.creat(sellerModel);
    }

    //启用商家up
    @RequestMapping(value = "/up",method = RequestMethod.POST)
    @AdminPermission
    @ResponseBody
    public commonRes up(@RequestParam(value = "id") Integer id) throws BusinessException {
        sellerModel sellerModel = sellerService.changeStatus(id, 0);
        return commonRes.creat(sellerModel);
    }





//    //用户注册方法
//    @RequestMapping("/register")
//    @ResponseBody
//    public commonRes register(@Valid @RequestBody registerRequest registerRequest, BindingResult bindingResult) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {
//
//        //参数校验
//        if(bindingResult.hasErrors()){
//            throw new BusinessException(EmBusinessError.Validate_Parameter_Error, commonUtils.precessErrorString(bindingResult));
//        }
//
//        userModel userModel = new userModel();
//        userModel.setTelphone(registerRequest.getTelphone());
//        userModel.setPassword(registerRequest.getPassword());
//        userModel.setNickName(registerRequest.getNickName());
//        userModel.setGender(registerRequest.getGender());
//
//        userModel registerUser = userService.registerUser(userModel);
//
//        return commonRes.creat(registerUser);
//    }

//
//    //用户注销
//    @RequestMapping("/logout")
//    @ResponseBody
//    public commonRes logout(){
//
//        httpServletRequest.getSession().invalidate();
//
//        return commonRes.creat(null);
//    }


}
