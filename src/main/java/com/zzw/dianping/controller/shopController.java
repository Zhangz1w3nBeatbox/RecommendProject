package com.zzw.dianping.controller;

import com.zzw.dianping.common.*;
import com.zzw.dianping.model.categoryModel;
import com.zzw.dianping.model.shopModel;
import com.zzw.dianping.request.categoryRequest;
import com.zzw.dianping.request.pageQuery;
import com.zzw.dianping.service.categoryService;
import com.zzw.dianping.service.shopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;


//给C端用户使用的商家页面
@Controller("/shop")
@RequestMapping("/shop")
public class shopController {

    @Autowired
    public categoryService categoryService;

    @Autowired
    public shopService shopService;


    //推荐门店
    @ResponseBody
    @RequestMapping("/recommend")
    public commonRes list(@RequestParam("longitude")BigDecimal longitude,@RequestParam("latitude")BigDecimal latitude) throws BusinessException {

        if(longitude==null||latitude==null){
            throw new BusinessException(EmBusinessError.Validate_Parameter_Error);
        }

        List<shopModel> recommendShops = shopService.recommend(longitude, latitude);

        return commonRes.creat(recommendShops);
    }




}



