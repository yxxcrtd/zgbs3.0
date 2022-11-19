package com.joyintech.tams.common.base.dao;

import com.joyintech.tams.common.base.entity.TrdProductDeal;

import java.util.List;

import com.joyintech.tams.common.base.mapper.BaseBatchMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TrdProductDealMapper extends BaseBatchMapper<TrdProductDeal, String> {
    int deleteByPrimaryKey(String tradeId);

    int insert(TrdProductDeal record);

    int insertSelective(TrdProductDeal record);

    int insertBasicByBasicInfo(TrdProductDeal record);

    TrdProductDeal selectByPrimaryKey(String tradeId);

    int updateByPrimaryKeySelective(TrdProductDeal record);

    int updateByPrimaryKey(TrdProductDeal record);

    int updateByTotleTradeId(TrdProductDeal record);
    
    List<TrdProductDeal> selectByTrade(TrdProductDeal deal);
    /**
     * 获取资金账户
     * @param portfolioId
     * @param acctType
     * @return
     */
    String getACCTNO(@Param("portfolioId") String portfolioId, @Param("acctType") String acctType);
    /**
     * 获取证券账户
     * @param portfolioId
     * @param finprodId
     * @return
     */
    String getSecAcctId(@Param("portfolioId") String portfolioId,@Param("finprodId") String finprodId,@Param("tradeMarket") String tradeMarket);

}
