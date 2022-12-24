package com.zzw.dianping.service;

import com.zzw.dianping.common.BusinessException;
import com.zzw.dianping.model.userModel;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

@Service
public interface UserService {

    Integer countAllUser();
    userModel getUser(Integer id);
    userModel registerUser(userModel registerUser) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException;
    userModel login(String telphone,String password) throws UnsupportedEncodingException, NoSuchAlgorithmException, BusinessException;
}
