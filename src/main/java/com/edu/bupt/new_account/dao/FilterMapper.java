package com.edu.bupt.new_account.dao;

import com.edu.bupt.new_account.model.Filter;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FilterMapper {
    int deleteByPrimaryKey(Integer filterid);

    int insert(Filter record);

    int insertSelective(Filter record);

    Filter selectByPrimaryKey(Integer filterid);

    int updateByPrimaryKeySelective(Filter record);

    int updateByPrimaryKey(Filter record);
}