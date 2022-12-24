package com.zzw.dianping.service;

import com.zzw.dianping.common.BusinessException;
import com.zzw.dianping.model.sellerModel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface sellerService {

    sellerModel create(sellerModel sellerModel);
    sellerModel get(Integer id);
    List<sellerModel> selectAll();
    sellerModel changeStatus(Integer id,Integer disableFlag) throws BusinessException;
}
