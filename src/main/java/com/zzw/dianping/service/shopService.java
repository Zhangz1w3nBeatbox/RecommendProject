package com.zzw.dianping.service;

import com.zzw.dianping.common.BusinessException;
import com.zzw.dianping.model.shopModel;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public interface shopService {
    shopModel create(shopModel shopModel) throws BusinessException;
    shopModel get(Integer id);
    List<shopModel> selectAll();
    List<shopModel> recommend(BigDecimal longitude,BigDecimal latitude);

    List<Map<String,Object>> searchGroupByTags(String keyword,Integer categoryId,String tags);

    Integer countAllShop();

    //List<shopModel> search(BigDecimal longitude,BigDecimal latitude,String keyword,Integer orderby,Integer categoryId,String tags);

    List<shopModel> search(BigDecimal longitude, BigDecimal latitude, String keyword,Integer orderby,Integer categoryId,String tags);
}
