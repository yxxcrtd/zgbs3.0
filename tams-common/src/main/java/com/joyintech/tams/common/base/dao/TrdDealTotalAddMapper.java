package com.joyintech.tams.common.base.dao;

import com.joyintech.tams.common.base.entity.TrdDealTotalAdd;
import com.joyintech.tams.common.base.mapper.BaseBatchMapper;
import org.springframework.stereotype.Repository;

@Repository
public interface TrdDealTotalAddMapper extends BaseBatchMapper<TrdDealTotalAdd, String> {
    int deleteByPrimaryKey(String totleTradeId);

    int insert(TrdDealTotalAdd record);

    int insertSelective(TrdDealTotalAdd record);

    int insertBasicByBasicInfo(TrdDealTotalAdd record);

    TrdDealTotalAdd selectByPrimaryKey(String totleTradeId);

    int updateByPrimaryKeySelective(TrdDealTotalAdd record);

    int updateByPrimaryKey(TrdDealTotalAdd record);
}