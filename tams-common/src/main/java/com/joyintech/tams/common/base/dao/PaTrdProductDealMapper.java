package com.joyintech.tams.common.base.dao;

import com.joyintech.tams.common.base.entity.PaTrdProductDeal;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaTrdProductDealMapper {
    int deleteByPrimaryKey(String tradeId);

    int insert(PaTrdProductDeal record);

    int insertSelective(PaTrdProductDeal record);

    int insertBasicByBasicInfo(PaTrdProductDeal record);

    PaTrdProductDeal selectByPrimaryKey(String tradeId);

    int updateByPrimaryKeySelective(PaTrdProductDeal record);

    int updateByPrimaryKey(PaTrdProductDeal record);
}