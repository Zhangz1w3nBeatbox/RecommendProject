package com.zzw.dianping.service.implement;

import com.zzw.dianping.common.BusinessException;
import com.zzw.dianping.common.EmBusinessError;
import com.zzw.dianping.dal.shopModelMapper;
import com.zzw.dianping.model.categoryModel;
import com.zzw.dianping.model.sellerModel;
import com.zzw.dianping.model.shopModel;
import com.zzw.dianping.service.categoryService;
import com.zzw.dianping.service.sellerService;
import com.zzw.dianping.service.shopService;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.core.TimeValue;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class shopServiceImpl implements shopService {


    @Autowired
    private shopModelMapper shopModelMapper;

    @Autowired
    private categoryService categoryService;

    @Autowired
    private sellerService sellerService;

    @Autowired
    private RestHighLevelClient highLevelClient;

    @Override
    @Transactional
    public shopModel create(shopModel shopModel) throws BusinessException {

        //创建门店方法
        shopModel.setCreatedAt(new Date());
        shopModel.setUpdatedAt(new Date());

        //校验商家是否存在正确
        sellerModel sellerModel = sellerService.get(shopModel.getSellerId());

        //不存在商家
        if(sellerModel == null){
            throw new BusinessException(EmBusinessError.Validate_Parameter_Error,"商户不存在");
        }

        //如果存在 还要看是否被禁用
        if(sellerModel.getDisabledFlag().intValue() == 1){
            throw new BusinessException(EmBusinessError.Validate_Parameter_Error,"商户已禁用");
        }

        //校验商品 类别和类目
        categoryModel categoryModel = categoryService.get(shopModel.getCategoryId());

        //类别不存在
        if(categoryModel == null){
            throw new BusinessException(EmBusinessError.Validate_Parameter_Error,"类目不存在");
        }

        //完事后插入
        int i = shopModelMapper.insertSelective(shopModel);

        System.out.println(i);

        return get(shopModel.getId());
    }

    @Override
    public shopModel get(Integer id) {

        shopModel shopModel = shopModelMapper.selectByPrimaryKey(id);

        if(shopModel == null){
            return null;
        }

        // shop 中包含 seller 和 category

        shopModel.setSellerModel(sellerService.get(shopModel.getSellerId()));

        shopModel.setCategoryModel(categoryService.get(shopModel.getCategoryId()));

        return shopModel;
    }

    @Override
    public List<shopModel> selectAll() {

        List<shopModel> shopModelList = shopModelMapper.selectAll();

        shopModelList.forEach(shopModel -> {
            shopModel.setSellerModel(sellerService.get(shopModel.getSellerId()));
            shopModel.setCategoryModel(categoryService.get(shopModel.getCategoryId()));
        });

        return shopModelList;
    }

    @Override
    public List<shopModel> recommend(BigDecimal longitude, BigDecimal latitude) {
        List<shopModel> shopModelList = shopModelMapper.recommend(longitude, latitude);
        shopModelList.forEach(shopModel -> {
            shopModel.setSellerModel(sellerService.get(shopModel.getSellerId()));
            shopModel.setCategoryModel(categoryService.get(shopModel.getCategoryId()));
        });
        return shopModelList;
    }

    @Override
    public List<Map<String, Object>> searchGroupByTags(String keyword, Integer categoryId, String tags) {
        return shopModelMapper.searchGroupByTags(keyword,categoryId,tags);
    }

    @Override
    public Integer countAllShop() {
        return shopModelMapper.countAllShop();
    }

    @Override
    public List<shopModel> search(BigDecimal longitude, BigDecimal latitude, String keyword,Integer orderby,Integer categoryId,String tags) {
        List<shopModel> shopModelList = shopModelMapper.search(longitude, latitude,keyword,orderby,categoryId,tags);
        shopModelList.forEach(shopModel -> {
            shopModel.setSellerModel(sellerService.get(shopModel.getSellerId()));
            shopModel.setCategoryModel(categoryService.get(shopModel.getCategoryId()));
        });
        return shopModelList;
    }

    @Override
    public Map<String, Object> searchES(BigDecimal longitude, BigDecimal latitude, String keyword, Integer orderby, Integer categoryId, String tags) throws IOException {

        Map<String, Object> res = new HashMap<>();

        SearchRequest searchRequest = new SearchRequest("shop");
        SearchSourceBuilder SourceBuilder = new SearchSourceBuilder();
        SourceBuilder.query(QueryBuilders.matchQuery("name",keyword));
        SourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        searchRequest.source(SourceBuilder);

        List<Integer> shopIdList =new ArrayList<>();

        SearchResponse searchResponse = highLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        SearchHit[] hits = searchResponse.getHits().getHits();

        for(SearchHit hit:hits){
            shopIdList.add(new Integer(hit.getSourceAsMap().get("id").toString()));
        }

        List<shopModel> shopModel = shopIdList.stream().map(id -> {
            return get(id);
        }).collect(Collectors.toList());


        res.put("shop",shopModel);

        return res;
    }


}
