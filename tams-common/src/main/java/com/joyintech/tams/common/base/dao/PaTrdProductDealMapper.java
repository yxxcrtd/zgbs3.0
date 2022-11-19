package com.joyintech.tams.common.base.dao;

import com.joyintech.tams.common.base.entity.PaTrdProductDeal;
import com.joyintech.tams.common.base.mapper.BaseBatchMapper;
import org.springframework.stereotype.Repository;

@Repository
public interface PaTrdProductDealMapper extends BaseBatchMapper<PaTrdProductDeal, String> {
    int deleteByPrimaryKey(String tradeId);

    int insert(PaTrdProductDeal record);

    int insertSelective(PaTrdProductDeal record);

    int insertBasicByBasicInfo(PaTrdProductDeal record);

    PaTrdProductDeal selectByPrimaryKey(String tradeId);

    int updateByPrimaryKeySelective(PaTrdProductDeal record);

    int updateByPrimaryKey(PaTrdProductDeal record);
}