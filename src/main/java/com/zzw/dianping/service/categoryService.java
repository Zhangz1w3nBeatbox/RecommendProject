package com.zzw.dianping.service;

import com.zzw.dianping.common.BusinessException;
import com.zzw.dianping.model.categoryModel;
import com.zzw.dianping.model.sellerModel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface categoryService {
    Integer countAllCategory();
    categoryModel create(categoryModel categoryModel) throws BusinessException;
    categoryModel get(Integer id);
    List<categoryModel> selectAll();
}
