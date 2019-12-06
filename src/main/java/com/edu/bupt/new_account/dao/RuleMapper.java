package com.edu.bupt.new_account.dao;

import com.edu.bupt.new_account.model.Rule;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
@Mapper
public interface RuleMapper {
    int deleteByPrimaryKey(Integer ruleid);

    int insert(Rule record);

    int insertSelective(Rule record);

    Rule selectByPrimaryKey(Integer ruleid);

    int updateByPrimaryKeySelective(Rule record);

    int updateByPrimaryKey(Rule record);

    List<Rule> getBindedRules(String gatewayid);
}