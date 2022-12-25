package com.zzw.dianping.controller.admin;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zzw.dianping.common.AdminPermission;
import com.zzw.dianping.common.BusinessException;
import com.zzw.dianping.common.EmBusinessError;
import com.zzw.dianping.common.commonUtils;
import com.zzw.dianping.model.shopModel;
import com.zzw.dianping.request.shopRequest;
import com.zzw.dianping.request.pageQuery;
import com.zzw.dianping.service.shopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.List;

@Controller("/admin/shop")
@RequestMapping("/admin/shop")
public class shopController {

    @Autowired
    public shopService shopService;


    //获取所有品类列表
    @RequestMapping("/index")
    @AdminPermission
    public ModelAndView index(pageQuery pageQuery) {

        //分页
        PageHelper.startPage(pageQuery.getPage(),pageQuery.getSize());

        List<shopModel> shopModels = shopService.selectAll();

        //封装完后的信息  page size 第几页 total
        PageInfo<shopModel> pageInfo = new PageInfo<>(shopModels);


        ModelAndView modelAndView = new ModelAndView("/admin/shop/index.html");
        modelAndView.addObject("data",pageInfo);
        modelAndView.addObject("CONTROLLER_NAME","shop");
        modelAndView.addObject("ACTION_NAME","index");

        return modelAndView;
    }


    //创建品类页面
    @RequestMapping("/createpage")
    @AdminPermission
    public ModelAndView createpage() {

        ModelAndView modelAndView = new ModelAndView("/admin/shop/create.html");
        modelAndView.addObject("CONTROLLER_NAME","shop");
        modelAndView.addObject("ACTION_NAME","index");

        return modelAndView;
    }

    //创建商家页面
    @RequestMapping(value = "/create",method = RequestMethod.POST)
    @AdminPermission
    public String create(@Valid shopRequest shopRequest, BindingResult bindingResult) throws BusinessException {

        if(bindingResult.hasErrors()){
            throw  new BusinessException(EmBusinessError.Validate_Parameter_Error,commonUtils.precessErrorString(bindingResult));
        }

        shopModel shopModel = new shopModel();

        shopModel.setName(shopRequest.getName());

        shopModel.setIconUrl(shopRequest.getIconUrl());
        shopModel.setAddress(shopRequest.getAddress());
        shopModel.setCategoryId(shopRequest.getCategoryId());
        shopModel.setEndTime(shopRequest.getEndTime());
        shopModel.setStartTime(shopRequest.getStartTime());
        shopModel.setLongitude(shopRequest.getLongitude());
        shopModel.setLatitude(shopRequest.getLatitude());
        shopModel.setName(shopRequest.getName());
        shopModel.setPricePerMan(shopRequest.getPricePerMan());
        shopModel.setSellerId(shopRequest.getSellerId());


        shopService.create(shopModel);

        return "redirect:/admin/shop/index";
    }

}



