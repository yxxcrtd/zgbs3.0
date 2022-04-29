package com.joyintech.tams.common.base.dao;

import com.joyintech.tams.common.base.entity.TrdTradeNewStockLocked;
import org.springframework.stereotype.Repository;

@Repository
public interface TrdTradeNewStockLockedMapper {
    int deleteByPrimaryKey(String tradeId);

    int insert(TrdTradeNewStockLocked record);

    int insertSelective(TrdTradeNewStockLocked record);

    int insertBasicByBasicInfo(TrdTradeNewStockLocked record);

    TrdTradeNewStockLocked selectByPrimaryKey(String tradeId);

    int updateByPrimaryKeySelective(TrdTradeNewStockLocked record);

    int updateByPrimaryKey(TrdTradeNewStockLocked record);
}