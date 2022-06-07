package com.joyintech.tams.common.base.dao;

import com.joyintech.tams.common.base.entity.TrdTempDealTotalAdd;
import org.springframework.stereotype.Repository;

@Repository
public interface TrdTempDealTotalAddMapper {
    int deleteByPrimaryKey(String totleTradeId);

    int insert(TrdTempDealTotalAdd record);

    int insertSelective(TrdTempDealTotalAdd record);

    TrdTempDealTotalAdd selectByPrimaryKey(String totleTradeId);

    int updateByPrimaryKeySelective(TrdTempDealTotalAdd record);

    int updateByPrimaryKey(TrdTempDealTotalAdd record);
}