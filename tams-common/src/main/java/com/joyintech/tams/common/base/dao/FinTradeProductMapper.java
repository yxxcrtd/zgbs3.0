package com.joyintech.tams.common.base.dao;

import com.joyintech.tams.common.base.entity.FinTradeProduct;
import com.joyintech.tams.common.base.entity.FinTradeProductKey;

import java.util.List;

import com.joyintech.tams.common.base.pojo.OriPledgeInfo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Repository
public interface FinTradeProductMapper {
    int deleteByPrimaryKey(FinTradeProductKey key);

    int insert(FinTradeProduct record);

    int insertSelective(FinTradeProduct record);

    int insertBasicByBasicInfo(FinTradeProduct record);

    FinTradeProduct selectByPrimaryKey(FinTradeProductKey key);

    List<FinTradeProduct> selectByFinprodId(String finprodId);

    int updateByPrimaryKeySelective(FinTradeProduct record);

    int updateByPrimaryKey(FinTradeProduct record);
    
    @Select("select CALENDAR_ID from FIN_TRADE_PRODUCT where finprod_id = #{finprodId,jdbcType=VARCHAR}")
    List<String> getCalendarByFinprodId(String finprodId);

    /**
     * 根据交易编号查询委托编号
     * @param tradeId 交易编号
     * @return 委托编号
     */
    String findEntrustIdByTradeId(String tradeId);

    /**
     * 原质押品信息查询
     */
    List<OriPledgeInfo> getOriFinDealPledgeList(@Param("finprodId") String finprodId);
}