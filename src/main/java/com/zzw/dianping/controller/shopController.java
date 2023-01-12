package com.zzw.dianping.controller;

import com.zzw.dianping.common.*;
import com.zzw.dianping.model.categoryModel;
import com.zzw.dianping.model.shopModel;
import com.zzw.dianping.model.userModel;
import com.zzw.dianping.request.categoryRequest;
import com.zzw.dianping.request.pageQuery;
import com.zzw.dianping.service.UserService;
import com.zzw.dianping.service.categoryService;
import com.zzw.dianping.service.shopService;
import org.apache.mahout.cf.taste.common.TasteException;
import org.aspectj.apache.bcel.classfile.Module;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


//给C端用户使用的商家页面
@Controller("/shop")
@RequestMapping("/shop")
public class shopController {
    public static  final String CURRENT_USER_SESSION = "currentUserSession";

    @Autowired
    public UserService userService;

    @Autowired
    private HttpServletRequest httpServletRequest;

    @Autowired
    public categoryService categoryService;

    @Autowired
    public shopService shopService;


    //推荐门店1.0
    @ResponseBody
    @RequestMapping("/recommend")
    public commonRes list(@RequestParam("longitude")BigDecimal longitude,@RequestParam("latitude")BigDecimal latitude) throws BusinessException {

        if(longitude==null||latitude==null){
            throw new BusinessException(EmBusinessError.Validate_Parameter_Error);
        }

        List<shopModel> recommendShops = shopService.recommend(longitude, latitude);

        return commonRes.creat(recommendShops);
    }

    //推荐2.0 基于mahout协同过滤算法
    @ResponseBody
    @RequestMapping("/recommendMahout")
    public commonRes recommend(@RequestParam("longitude")BigDecimal longitude,@RequestParam("latitude")BigDecimal latitude) throws BusinessException, TasteException {

        if(longitude==null||latitude==null){
            throw new BusinessException(EmBusinessError.Validate_Parameter_Error);
        }

        userModel currentUser = (userModel)httpServletRequest.getSession().getAttribute(CURRENT_USER_SESSION);

        Integer userId = currentUser.getId();

        System.out.println(userId);

        List<shopModel> recommendShops = shopService.recommendByMahout(userId,longitude,latitude);

        return commonRes.creat(recommendShops);
    }

    //
    //搜索门店
    @ResponseBody
    @RequestMapping(value = "/search",method = RequestMethod.POST)
    public commonRes search(@RequestParam("longitude")BigDecimal longitude,
                            @RequestParam("latitude")BigDecimal latitude,
                            @RequestParam("keyword") String keyword,
                            @RequestParam(value = "orderby",required = false) Integer orderby,
                            @RequestParam(value = "categoryId",required = false) Integer categoryId,
                            @RequestParam(value = "tags",required = false) String tags) throws BusinessException, IOException {

        if(StringUtils.isEmpty(keyword)||longitude==null||latitude==null){
            throw new BusinessException(EmBusinessError.Validate_Parameter_Error);
        }

        //List<shopModel> shopModels = shopService.search(longitude,latitude,keyword,orderby,categoryId,tags);
        Map<String, Object> resMap = shopService.searchES(longitude, latitude, keyword, orderby, categoryId, tags);

        List<shopModel> shopModelsEs = (List<shopModel>)resMap.get("shop");
        List<Map<String, Object>> tagsAggregation = (List<Map<String, Object>>)resMap.get("tags");

        List<categoryModel> categoryModels = categoryService.selectAll();

        //List<Map<String, Object>> tagsAggregation = shopService.searchGroupByTags(keyword, categoryId, tags);

        HashMap<String, Object> res = new HashMap<>();

        res.put("shop",shopModelsEs);
        res.put("category",categoryModels);
        res.put("tags",tagsAggregation);

        return commonRes.creat(res);
    }

}



