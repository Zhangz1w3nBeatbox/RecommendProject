package com.zzw.dianping.service.implement;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zzw.dianping.common.BusinessException;
import com.zzw.dianping.common.EmBusinessError;
import com.zzw.dianping.dal.shopModelMapper;
import com.zzw.dianping.model.categoryModel;
import com.zzw.dianping.model.sellerModel;
import com.zzw.dianping.model.shopModel;
import com.zzw.dianping.service.categoryService;
import com.zzw.dianping.service.sellerService;
import com.zzw.dianping.service.shopService;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.core.TimeValue;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
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

//        SearchRequest searchRequest = new SearchRequest("shop");
//        SearchSourceBuilder SourceBuilder = new SearchSourceBuilder();
//        SourceBuilder.query(QueryBuilders.matchQuery("name",keyword));
//        SourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
//        searchRequest.source(SourceBuilder);
//
//        List<Integer> shopIdList =new ArrayList<>();
//
//        SearchResponse searchResponse = highLevelClient.search(searchRequest, RequestOptions.DEFAULT);
//        SearchHit[] hits = searchResponse.getHits().getHits();
//
//        for(SearchHit hit:hits){
//            shopIdList.add(new Integer(hit.getSourceAsMap().get("id").toString()));
//        }
        Request request = new Request("GET", "/shop/_search");
//        String reqJson = "{\n" +
//                " \"_source\": \"*\",\n" +
//                " \"script_fields\": {\n" +
//                "   \"distance\": {\n" +
//                "     \"script\": {\n" +
//                "       \"source\": \"haversin(lat,lon,doc['location'].lat,doc['location'].lon)\",\n" +
//                "       \"lang\": \"expression\",\n" +
//                "       \"params\": {\"lat\":"+latitude.toString()+",\"lon\":"+longitude.toString()+"}\n" +
//                "     }\n" +
//                "   }\n" +
//                " },\n" +
//                " \"query\": {\n" +
//                "   \"function_score\": {\n" +
//                "     \"query\": {\n" +
//                "       \"bool\": {\n" +
//                "         \"must\": [\n" +
//                "           {\"match\": {\"name\": {\"query\": \""+keyword+"\",\"boost\":0.1}}},\n" +
//                "           {\"term\": {\"seller_disabled_flag\": 0}}\n" +
//                "           \n" +
//                "         ]\n" +
//                "       }\n" +
//                "     },\n" +
//                "     \"functions\": [\n" +
//                "       {\n" +
//                "         \"gauss\": {\n" +
//                "           \"location\": {\n" +
//                "             \"origin\": \""+latitude.toString()+","+longitude.toString()+"\",\n" +
//                "             \"scale\": \"100km\",\n" +
//                "             \"offset\": \"0km\",\n" +
//                "             \"decay\": 0.5\n" +
//                "           }\n" +
//                "         },\n" +
//                "         \"weight\": 9\n" +
//                "       },\n" +
//                "       {\n" +
//                "         \"field_value_factor\": {\n" +
//                "           \"field\": \"remark_score\"\n" +
//                "         },\n" +
//                "         \"weight\": 0.2\n" +
//                "       },\n" +
//                "       {\n" +
//                "         \"field_value_factor\": {\n" +
//                "           \"field\": \"seller_remark_score\"\n" +
//                "         },\n" +
//                "         \"weight\": 0.1\n" +
//                "       }\n" +
//                "     ],\n" +
//                "     \"score_mode\": \"sum\",\n" +
//                "     \"boost_mode\": \"sum\"\n" +
//                "   }\n" +
//                " },\n" +
//                " \"sort\": [\n" +
//                "   {\n" +
//                "     \"_score\": {\n" +
//                "       \"order\": \"desc\"\n" +
//                "     }\n" +
//                "   }\n" +
//                " ]\n" +
//                "}";

        // 构建请求
        JSONObject jsonRequestObj = new JSONObject();
        // 构建source
        jsonRequestObj.put("_source", "*");

        // 构建自定义距离
        jsonRequestObj.put("script_fields",new JSONObject());
        jsonRequestObj.getJSONObject("script_fields").put("distance",new JSONObject());
        jsonRequestObj.getJSONObject("script_fields").getJSONObject("distance").put("script",new JSONObject());
        jsonRequestObj.getJSONObject("script_fields").getJSONObject("distance").getJSONObject("script")
                .put("source","haversin(lat, lon, doc['location'].lat, doc['location'].lon)");
        jsonRequestObj.getJSONObject("script_fields").getJSONObject("distance").getJSONObject("script")
                .put("lang","expression");
        jsonRequestObj.getJSONObject("script_fields").getJSONObject("distance").getJSONObject("script")
                .put("params",new JSONObject());
        jsonRequestObj.getJSONObject("script_fields").getJSONObject("distance").getJSONObject("script")
                .getJSONObject("params").put("lat",latitude);
        jsonRequestObj.getJSONObject("script_fields").getJSONObject("distance").getJSONObject("script")
                .getJSONObject("params").put("lon",longitude);


        // 构建query
//        Map<String,Object> cixingMap = analyzeCategoryKeyword(keyword);
//        boolean isAffectFilter = false;
//        boolean isAffectOrder =  true;
        jsonRequestObj.put("query",new JSONObject());


        //构建function score
        jsonRequestObj.getJSONObject("query").put("function_score",new JSONObject());

        //构建function query
        jsonRequestObj.getJSONObject("query").getJSONObject("function_score").put("query",new JSONObject());
        jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").put("bool",new JSONObject());
        jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool").put("must",new JSONArray());
        jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool")
                        .getJSONArray("must").add(new JSONObject());

        int queryIndex=0;

        System.out.println("————————————");

        //TODO：相关性的优化
        Map<String,Object> cixingMap = analyzeCategoryKeyword(keyword);


        System.out.println("————————————");

        System.out.println(cixingMap);

        boolean isAffectFiler = false;//影响召回因子
        boolean isAffectOrder = true;//影响排序因子

        if(cixingMap.keySet().size()>0&&isAffectFiler){//影响召回
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool")
                    .getJSONArray("must").getJSONObject(queryIndex).put("bool",new JSONObject());
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool")
                    .getJSONArray("must").getJSONObject(queryIndex).getJSONObject("bool").put("should",new JSONArray());
            int filterQueryIndex=0;
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool")
                    .getJSONArray("must").getJSONObject(queryIndex).getJSONObject("bool").getJSONArray("should").add(new JSONObject());
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool")
                    .getJSONArray("must").getJSONObject(queryIndex).getJSONObject("bool").getJSONArray("should").getJSONObject(filterQueryIndex)
                    .put("match",new JSONObject());
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool")
                    .getJSONArray("must").getJSONObject(queryIndex).getJSONObject("bool").getJSONArray("should").getJSONObject(filterQueryIndex)
                    .getJSONObject("match").put("name",new JSONObject());
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool")
                    .getJSONArray("must").getJSONObject(queryIndex).getJSONObject("bool").getJSONArray("should").getJSONObject(filterQueryIndex)
                    .getJSONObject("match").getJSONObject("name").put("query",keyword);

            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool")
                    .getJSONArray("must").getJSONObject(queryIndex).getJSONObject("bool").getJSONArray("should").getJSONObject(filterQueryIndex)
                    .getJSONObject("match").getJSONObject("name").put("boost",0.1);


            //categoryId的term的构建
            for(String key :cixingMap.keySet()){

                filterQueryIndex++;

                Integer cixingCategoryId = (Integer) cixingMap.get(key);

                jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool")
                        .getJSONArray("must").getJSONObject(queryIndex).getJSONObject("bool").getJSONArray("should").add(new JSONObject());
                jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool")
                        .getJSONArray("must").getJSONObject(queryIndex).getJSONObject("bool").getJSONArray("should").getJSONObject(filterQueryIndex)
                        .put("term",new JSONObject());
                jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool")
                        .getJSONArray("must").getJSONObject(queryIndex).getJSONObject("bool").getJSONArray("should").getJSONObject(filterQueryIndex)
                        .getJSONObject("term").put("category_id",new JSONObject());
                jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool")
                        .getJSONArray("must").getJSONObject(queryIndex).getJSONObject("bool").getJSONArray("should").getJSONObject(filterQueryIndex)
                        .getJSONObject("term").getJSONObject("category_id").put("value",cixingCategoryId);

                jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool")
                        .getJSONArray("must").getJSONObject(queryIndex).getJSONObject("bool").getJSONArray("should").getJSONObject(filterQueryIndex)
                        .getJSONObject("term").getJSONObject("category_id").put("boost",0);
            }



        }else{
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool")
                    .getJSONArray("must").getJSONObject(queryIndex).put("match",new JSONObject());
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool")
                    .getJSONArray("must").getJSONObject(queryIndex).getJSONObject("match").put("name",new JSONObject());
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool")
                    .getJSONArray("must").getJSONObject(queryIndex).getJSONObject("match").getJSONObject("name").put("query",keyword);
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool")
                    .getJSONArray("must").getJSONObject(queryIndex).getJSONObject("match").getJSONObject("name").put("boost",0.1);
        }




        queryIndex++;

        //第二个query查询条件-seller_disabled_flag
        jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool")
                .getJSONArray("must").add(new JSONObject());
        jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool")
                .getJSONArray("must").getJSONObject(queryIndex).put("term",new JSONObject());
        jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool")
                .getJSONArray("must").getJSONObject(queryIndex).getJSONObject("term").put("seller_disabled_flag",0);

        //选择了筛选标签
        //TODO:mysql的一个门店的tags有很多条 但是mysql检测只能把一整条检索出来 不能检索 类似于 "有wifi 空气好"的为两个tags 而使用es则可以完美结解决-while space

        if(tags!=null){
            queryIndex++;
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool")
                    .getJSONArray("must").add(new JSONObject());
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool")
                    .getJSONArray("must").getJSONObject(queryIndex).put("term",new JSONObject());
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool")
                    .getJSONArray("must").getJSONObject(queryIndex).getJSONObject("term").put("tags",tags);

        }


        if(categoryId!=null){
            queryIndex++;
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool")
                    .getJSONArray("must").add(new JSONObject());
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool")
                    .getJSONArray("must").getJSONObject(queryIndex).put("term",new JSONObject());
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool")
                    .getJSONArray("must").getJSONObject(queryIndex).getJSONObject("term").put("category_id",categoryId);

        }


        jsonRequestObj.getJSONObject("query").getJSONObject("function_score").put("functions",new JSONArray());

        int functionIndex=0;

        if(orderby==null){//默认排序
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").add(new JSONObject());
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").getJSONObject(functionIndex).put("gauss",new JSONObject());
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").getJSONObject(functionIndex).getJSONObject("gauss").put("location",new JSONObject());
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").getJSONObject(functionIndex).getJSONObject("gauss")
                    .getJSONObject("location").put("origin",latitude.toString()+","+longitude.toString());
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").getJSONObject(functionIndex).getJSONObject("gauss")
                    .getJSONObject("location").put("scale","100km");
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").getJSONObject(functionIndex).getJSONObject("gauss")
                    .getJSONObject("location").put("offset","0km");
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").getJSONObject(functionIndex).getJSONObject("gauss")
                    .getJSONObject("location").put("decay","0.5");
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").getJSONObject(functionIndex).put("weight",9);

            functionIndex++;

            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").add(new JSONObject());
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").getJSONObject(functionIndex).put("field_value_factor",new JSONObject());
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").getJSONObject(functionIndex).getJSONObject("field_value_factor")
                    .put("field","remark_score");
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").getJSONObject(functionIndex).put("weight",0.2);


            functionIndex++;

            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").add(new JSONObject());
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").getJSONObject(functionIndex).put("field_value_factor",new JSONObject());
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").getJSONObject(functionIndex).getJSONObject("field_value_factor")
                    .put("field","seller_remark_score");
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").getJSONObject(functionIndex).put("weight",0.1);


            //词性term排序
            if(cixingMap.keySet().size()>0&&isAffectOrder) {
                for (String key : cixingMap.keySet()) {

                    functionIndex++;
                    jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").add(new JSONObject());
                    jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").getJSONObject(functionIndex).put("filter", new JSONObject());
                    jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").getJSONObject(functionIndex).getJSONObject("filter")
                            .put("term", new JSONObject());
                    jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").getJSONObject(functionIndex).getJSONObject("filter")
                            .getJSONObject("term").put("category_id", cixingMap.get(key));
                    jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").getJSONObject(functionIndex).put("weight", 3);

                }
            }

            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").put("score_mode","sum");
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").put("boost_mode","sum");

        }else{
            //低价排序功能
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").add(new JSONObject());
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").getJSONObject(functionIndex).put("field_value_factor",new JSONObject());
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").getJSONObject(functionIndex).getJSONObject("field_value_factor")
                    .put("field","price_per_man");
            //jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").getJSONObject(functionIndex).put("weight",1);
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").put("score_mode","sum");
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").put("boost_mode","replace");
        }

        jsonRequestObj.put("sort",new JSONArray());
        jsonRequestObj.getJSONArray("sort").add(new JSONObject());
        jsonRequestObj.getJSONArray("sort").getJSONObject(0).put("_score",new JSONObject());

        if(orderby==null){
            jsonRequestObj.getJSONArray("sort").getJSONObject(0).getJSONObject("_score").put("order","desc");
        }else{
            jsonRequestObj.getJSONArray("sort").getJSONObject(0).getJSONObject("_score").put("order","asc");
        }

        //聚合
        jsonRequestObj.put("aggs",new JSONObject());
        jsonRequestObj.getJSONObject("aggs").put("group_by_tags",new JSONObject());
        jsonRequestObj.getJSONObject("aggs").getJSONObject("group_by_tags").put("terms",new JSONObject());
        jsonRequestObj.getJSONObject("aggs").getJSONObject("group_by_tags").getJSONObject("terms").put("field","tags");



        String reqJson = jsonRequestObj.toJSONString();

        System.out.println(reqJson);
        request.setJsonEntity(reqJson);

        Response response = highLevelClient.getLowLevelClient().performRequest(request);
        String responseStr = EntityUtils.toString(response.getEntity());
        System.out.println(responseStr);

        JSONObject jsonObject = JSONObject.parseObject(responseStr);
        JSONArray jsonArray = jsonObject.getJSONObject("hits").getJSONArray("hits");


        List<shopModel> shopModelList = new ArrayList<>();

        for(int i=0;i<jsonArray.size();++i){
            JSONObject jsonObj = jsonArray.getJSONObject(i);
            Integer id = new Integer(jsonObj.get("_id").toString());

            BigDecimal distance = new BigDecimal(jsonObj.getJSONObject("fields").getJSONArray("distance").get(0).toString());
            shopModel shopModel = get(id);
            shopModel.setDistance(distance.multiply(new BigDecimal(1000).setScale(0,BigDecimal.ROUND_CEILING)).intValue());
            shopModelList.add(shopModel);
        }


        JSONArray tagsJsonArray = jsonObject.getJSONObject("aggregations").getJSONObject("group_by_tags").getJSONArray("buckets");
        List<Map> tagsList = new ArrayList<>();

        for(int i=0;i<tagsJsonArray.size();++i){
            JSONObject jsonObj = tagsJsonArray.getJSONObject(i);
            HashMap<String, Object> tagMap = new HashMap<>();
            tagMap.put("tags",jsonObj.getString("key"));
            tagMap.put("num",jsonObj.getInteger("doc_count"));
            tagsList.add(tagMap);
        }

        res.put("shop",shopModelList);

        res.put("tags",tagsList);

        return res;
    }

    private Map<Integer,List<String>> categoryWorkMap = new HashMap<>();

    @PostConstruct
    public void init(){
        categoryWorkMap.put(1,new ArrayList<>());
        categoryWorkMap.put(2,new ArrayList<>());

        categoryWorkMap.get(1).add("吃饭");
        categoryWorkMap.get(1).add("下午茶");

        categoryWorkMap.get(2).add("休息");
        categoryWorkMap.get(2).add("住宿");
    }


    //通过传入的关键字 返回出对应的 门店id
    private Integer getCategoryIdByToken(String token){
        for(Integer key:categoryWorkMap.keySet()){
            List<String> tokenList = categoryWorkMap.get(key);
            if(tokenList.contains(token)){
                return key;
            }
        }

        return null;
    }


    //通过构建es语句查询keyword对应的分词
    // 通过es 分词出来的 关键字 去查找对应的门店id

    public Map<String,Object> analyzeCategoryKeyword(String keyword) throws IOException {

        Map<String,Object> res = new HashMap<>();

        Request request = new Request("GET", "/shop/_analyze");
        request.setJsonEntity("{"+"\"field\":\"name\","+" \"text\":\""+keyword+"\"\n"+"}");
        Response response = highLevelClient.getLowLevelClient().performRequest(request);

        String responsStr = EntityUtils.toString(response.getEntity());

        JSONObject jsonObject = JSONObject.parseObject(responsStr);

        JSONArray jsonArray = jsonObject.getJSONArray("tokens");

        for(int i=0;i<jsonArray.size();++i){
            String token = jsonArray.getJSONObject(i).getString("token");
            Integer categoryId = getCategoryIdByToken(token);
            if(categoryId!=null){
                res.put(token,categoryId);
            }

        }

        return res;
    }
}
