package com.joyintech.tams.common.base.dao;

import com.joyintech.tams.common.base.entity.DebtBaseInfo;
import com.joyintech.tams.common.base.entity.TrdFinProductDealTotal;

import com.joyintech.tams.common.base.mapper.BaseBatchMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TrdFinProductDealTotalMapper extends BaseBatchMapper<TrdFinProductDealTotal, String> {
    int deleteByPrimaryKey(String totleTradeId);

    int insert(TrdFinProductDealTotal record);

    int insertSelective(TrdFinProductDealTotal record);

    int insertBasicByBasicInfo(TrdFinProductDealTotal record);

    TrdFinProductDealTotal selectByPrimaryKey(String totleTradeId);

    int updateByPrimaryKeySelective(TrdFinProductDealTotal record);

    int updateByPrimaryKey(TrdFinProductDealTotal record);
    
    DebtBaseInfo selectIsRight(@Param("finprodId") String finprodId);

	int insertDebtBaseInfo(DebtBaseInfo debtbaseinfo);

	int selectCounterName(@Param("counterName") String counterName);
}