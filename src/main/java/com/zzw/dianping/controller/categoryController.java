package com.zzw.dianping.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zzw.dianping.common.*;
import com.zzw.dianping.model.categoryModel;
import com.zzw.dianping.request.categoryRequest;
import com.zzw.dianping.request.pageQuery;
import com.zzw.dianping.service.categoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.List;


//给C端用户使用的商品品类页面
@Controller("/category")
@RequestMapping("/category")
public class categoryController {

    @Autowired
    public categoryService categoryService;


    //获取所有品类列表
    @ResponseBody
    @RequestMapping("/list")
    public commonRes list(pageQuery pageQuery) {

        List<categoryModel> categoryModels = categoryService.selectAll();


        return commonRes.creat(categoryModels);
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



