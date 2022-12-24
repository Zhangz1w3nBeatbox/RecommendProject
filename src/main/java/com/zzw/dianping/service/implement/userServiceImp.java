package com.zzw.dianping.service.implement;

import com.zzw.dianping.common.BusinessException;
import com.zzw.dianping.common.EmBusinessError;
import com.zzw.dianping.dal.userModelMapper;
import com.zzw.dianping.model.userModel;
import com.zzw.dianping.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sun.misc.BASE64Encoder;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;


@Service
public class userServiceImp implements UserService {

    @Autowired
    private userModelMapper userModelMapper;

    @Override
    public Integer countAllUser() {
        return userModelMapper.countAllUser();
    }

    @Override
    public userModel getUser(Integer id) {
        userModel userModel = userModelMapper.selectByPrimaryKey(id);
        return userModel;
    }

    @Override
    @Transactional
    public userModel registerUser(userModel registerUser) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {
        //对输入的密码进行加密 再存入数据库
        registerUser.setPassword(encodeByMD5(registerUser.getPassword()));
        registerUser.setCreatedAt(new Date());
        registerUser.setUpdatedAt(new Date());



        try {

            userModelMapper.insertSelective(registerUser);

        }catch (DuplicateKeyException exception){
            throw new BusinessException(EmBusinessError.REGISTER_DUP_FAIL);
        }

        userModel userModel = userModelMapper.selectByPhone(registerUser.getTelphone());

        return userModel;
    }


    //登陆方法
    @Override
    public userModel login(String telphone, String password) throws UnsupportedEncodingException, NoSuchAlgorithmException, BusinessException {
        userModel userModel = userModelMapper.selectByTelphoneAndPassword(telphone,encodeByMD5(password));
        if(userModel==null){
            throw new BusinessException(EmBusinessError.Login_Fail);
        }
        return userModel;
    }

    //密码转换方法
    private String encodeByMD5(String password) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        BASE64Encoder base64Encoder = new BASE64Encoder();
        String md5Password = base64Encoder.encode(md5.digest(password.getBytes("utf-8")));
        return md5Password;
    }
}
