package com.zzw.dianping.dal;

import com.zzw.dianping.model.userModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;


@Mapper
@Repository
public interface userModelMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(userModel record);

    int insertSelective(userModel record);

    userModel selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(userModel record);

    int updateByPrimaryKey(userModel record);
}