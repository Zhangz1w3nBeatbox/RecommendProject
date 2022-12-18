package com.zzw.dianping.service.implement;

import com.zzw.dianping.dal.userModelMapper;
import com.zzw.dianping.model.userModel;
import com.zzw.dianping.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class userServiceImp implements UserService {

    @Autowired
    private userModelMapper userModelMapper;

    @Override
    public userModel getUser(Integer id) {
        userModel userModel = userModelMapper.selectByPrimaryKey(id);
        return userModel;
    }
}
