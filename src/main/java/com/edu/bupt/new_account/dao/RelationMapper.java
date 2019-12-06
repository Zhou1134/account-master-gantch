package com.edu.bupt.new_account.dao;

import com.edu.bupt.new_account.model.Relation;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RelationMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Relation record);

    int insertSelective(Relation record);

    Relation selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Relation record);

    int updateByPrimaryKey(Relation record);

    List<Relation> getBindedRelations(int bindedId);

    Relation getRelationBy2Bind(Relation re);

    List<Relation> getRelationsByBinderId(int binderId);
}