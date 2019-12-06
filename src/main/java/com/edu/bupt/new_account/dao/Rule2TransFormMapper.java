package com.edu.bupt.new_account.dao;

import com.edu.bupt.new_account.model.Rule2TransFormKey;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
@Mapper
public interface Rule2TransFormMapper {
    int deleteByPrimaryKey(Rule2TransFormKey key);

    int insert(Rule2TransFormKey record);

    int insertSelective(Rule2TransFormKey record);

    List<Rule2TransFormKey> getBindedR2T(Integer ruleid);
}