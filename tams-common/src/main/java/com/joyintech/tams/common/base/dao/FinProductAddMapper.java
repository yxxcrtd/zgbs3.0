package com.joyintech.tams.common.base.dao;

import com.joyintech.tams.common.base.entity.FinProductAdd;
import org.springframework.stereotype.Repository;

@Repository
public interface FinProductAddMapper {
    int deleteByPrimaryKey(String finprodId);

    int insert(FinProductAdd record);

    int insertSelective(FinProductAdd record);

    int insertBasicByBasicInfo(FinProductAdd record);

    FinProductAdd selectByPrimaryKey(String finprodId);

    int updateByPrimaryKeySelective(FinProductAdd record);

    int updateByPrimaryKey(FinProductAdd record);

    int updateForTenderBound(FinProductAdd record);
}