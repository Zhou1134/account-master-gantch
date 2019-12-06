package com.edu.bupt.new_account.dao;

import com.edu.bupt.new_account.model.Rule2FilterKey;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
@Mapper
public interface Rule2FilterMapper {
    int deleteByPrimaryKey(Rule2FilterKey key);

    int insert(Rule2FilterKey record);

    int insertSelective(Rule2FilterKey record);

    List<Rule2FilterKey> getBindedR2F(Integer ruleid);
}