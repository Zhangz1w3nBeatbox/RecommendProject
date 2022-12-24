package com.zzw.dianping.controller.admin;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zzw.dianping.common.*;
import com.zzw.dianping.model.categoryModel;
import com.zzw.dianping.model.sellerModel;
import com.zzw.dianping.request.categoryRequest;
import com.zzw.dianping.request.pageQuery;
import com.zzw.dianping.request.sellerRequest;
import com.zzw.dianping.service.categoryService;
import com.zzw.dianping.service.sellerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import sun.misc.BASE64Encoder;

import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Controller("/admin/category")
@RequestMapping("/admin/category")
public class categoryController {

    @Autowired
    public categoryService categoryService;


    //获取所有品类列表
    @RequestMapping("/index")
    @AdminPermission
    public ModelAndView index(pageQuery pageQuery) {

        //分页
        PageHelper.startPage(pageQuery.getPage(),pageQuery.getSize());

        List<categoryModel> categoryModels = categoryService.selectAll();

        //封装完后的信息  page size 第几页 total
        PageInfo<categoryModel> pageInfo = new PageInfo<>(categoryModels);


        ModelAndView modelAndView = new ModelAndView("/admin/category/index.html");
        modelAndView.addObject("data",pageInfo);
        modelAndView.addObject("CONTROLLER_NAME","category");
        modelAndView.addObject("ACTION_NAME","index");

        return modelAndView;
    }


    //创建品类页面
    @RequestMapping("/createpage")
    @AdminPermission
    public ModelAndView createpage() {

        ModelAndView modelAndView = new ModelAndView("/admin/category/create.html");
        modelAndView.addObject("CONTROLLER_NAME","category");
        modelAndView.addObject("ACTION_NAME","index");

        return modelAndView;
    }

    //创建商家页面
    @RequestMapping(value = "/create",method = RequestMethod.POST)
    @AdminPermission
    public String create(@Valid categoryRequest categoryRequest, BindingResult bindingResult) throws BusinessException {

        if(bindingResult.hasErrors()){
            throw  new BusinessException(EmBusinessError.Validate_Parameter_Error,commonUtils.precessErrorString(bindingResult));
        }

        categoryModel categoryModel = new categoryModel();
        categoryModel.setName(categoryRequest.getName());

        System.out.println(categoryRequest.getName());

        categoryModel.setIconUrl(categoryRequest.getIconUrl());
        categoryModel.setSort(categoryRequest.getSort());

        categoryService.create(categoryModel);

        return "redirect:/admin/category/index";
    }

}



