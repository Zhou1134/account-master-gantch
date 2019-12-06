package com.edu.bupt.new_account.dao;

import com.edu.bupt.new_account.model.Tenant;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TenantMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Tenant record);

    int insertSelective(Tenant record);

    Tenant selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Tenant record);

    int updateByPrimaryKey(Tenant record);

    Tenant selectByNameAndPassword(Tenant tenant);
}