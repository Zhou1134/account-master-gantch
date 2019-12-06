package com.edu.bupt.new_account.dao;

import com.edu.bupt.new_account.model.Transform;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TransformMapper {
    int deleteByPrimaryKey(Integer transformid);

    int insert(Transform record);

    int insertSelective(Transform record);

    Transform selectByPrimaryKey(Integer transformid);

    int updateByPrimaryKeySelective(Transform record);

    int updateByPrimaryKey(Transform record);
}