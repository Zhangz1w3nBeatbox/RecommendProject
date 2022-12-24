package com.zzw.dianping.dal;

import com.zzw.dianping.model.userModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;


@Mapper
@Repository
public interface userModelMapper {

    Integer countAllUser();
    int deleteByPrimaryKey(Integer id);

    int insert(userModel record);

    int insertSelective(userModel record);

    userModel selectByPrimaryKey(Integer id);


    userModel selectByPhone(String telphone);

    userModel selectByTelphoneAndPassword(@Param("telphone") String telphone,@Param("password") String password);

    int updateByPrimaryKeySelective(userModel record);

    int updateByPrimaryKey(userModel record);
}