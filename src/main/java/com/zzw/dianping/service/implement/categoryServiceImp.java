package com.zzw.dianping.service.implement;


import com.zzw.dianping.common.BusinessException;
import com.zzw.dianping.common.EmBusinessError;
import com.zzw.dianping.dal.categoryModelMapper;
import com.zzw.dianping.dal.sellerModelMapper;
import com.zzw.dianping.model.categoryModel;
import com.zzw.dianping.model.sellerModel;
import com.zzw.dianping.service.categoryService;
import com.zzw.dianping.service.sellerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
public class categoryServiceImp implements categoryService {

    @Autowired
    private categoryModelMapper categoryModelMapper;

    @Override
    public Integer countAllCategory() {
        return categoryModelMapper.countAllCategory();
    }

    @Override
    @Transactional
    public categoryModel create(categoryModel categoryModel) throws BusinessException {
        categoryModel.setCreatedAt(new Date());
        categoryModel.setUpdatedAt(new Date());


        try{
            categoryModelMapper.insertSelective(categoryModel);
        }catch (DuplicateKeyException exception){
            throw new BusinessException(EmBusinessError.Category_Name_Duplicated);
        }



        return get(categoryModel.getId());
    }



    @Override
    public categoryModel get(Integer id) {
        categoryModel categoryModel = categoryModelMapper.selectByPrimaryKey(id);
        return categoryModel;
    }

    @Override
    public List<categoryModel> selectAll() {
        return categoryModelMapper.selectAll();
    }


}
