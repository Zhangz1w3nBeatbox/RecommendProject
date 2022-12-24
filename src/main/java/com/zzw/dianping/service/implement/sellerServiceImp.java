package com.zzw.dianping.service.implement;


import com.zzw.dianping.common.BusinessException;
import com.zzw.dianping.common.EmBusinessError;
import com.zzw.dianping.dal.sellerModelMapper;
import com.zzw.dianping.model.sellerModel;
import com.zzw.dianping.service.sellerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
public class sellerServiceImp implements sellerService {

    @Autowired
    private sellerModelMapper sellerModelMapper;

    @Override
    @Transactional
    public sellerModel create(sellerModel sellerModel) {
        sellerModel.setCreatedAt(new Date());
        sellerModel.setUpdatedAt(new Date());
        sellerModel.setRemarkScore(new BigDecimal(0));
        sellerModel.setDisabledFlag(0);
        sellerModelMapper.insertSelective(sellerModel);
        return get(sellerModel.getId());
    }

    @Override
    public sellerModel get(Integer id) {
        sellerModel seller = sellerModelMapper.selectByPrimaryKey(id);
        return seller;
    }

    @Override
    public List<sellerModel> selectAll() {
        return sellerModelMapper.selectAll();
    }

    @Override
    public sellerModel changeStatus(Integer id, Integer disableFlag) throws BusinessException {
        sellerModel sellerModel = get(id);
        if(sellerModel==null){
            throw new BusinessException(EmBusinessError.Validate_Parameter_Error);
        }
        sellerModel.setDisabledFlag(disableFlag);
        sellerModelMapper.updateByPrimaryKeySelective(sellerModel);
        return sellerModel;
    }
}
