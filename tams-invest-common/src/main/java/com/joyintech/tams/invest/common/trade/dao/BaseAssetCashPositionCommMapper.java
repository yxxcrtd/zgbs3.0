/*
 * 文件名：DateCalcMapper.java 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights
 * Reserved. 描述：理财资管管理系统V2.0 修改人：何阳阳 修改时间：2018年9月21日 修改内容：新建 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.common.trade.dao;

import java.util.List;
import java.util.Map;

import com.joyintech.tams.common.base.entity.TrdProductDeal;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.joyintech.tams.common.base.entity.PtlCashPositionFlow;
import com.joyintech.tams.common.base.entity.PtlSecPositionFlow;
import com.joyintech.tams.common.base.entity.WflTrdProductDealAdd;
import com.joyintech.tams.invest.common.dto.PtlCashPositionFlowDto;
import com.joyintech.tams.invest.common.dto.PtlSecPositionFlowDto;
import com.joyintech.tams.invest.common.dto.etr.EtrCashPositionFlowDto;
import com.joyintech.tams.invest.common.dto.etr.EtrSecPositionFlowDto;

/**
 * 业务共通组件.资产头寸现金持仓共通组件的Mapper
 *
 * @author 强成西
 * @version 1.0 2018年9月21日
 * @see BaseAssetCashPositionCommMapper
 * @since 1.0
 */
@Repository
public interface BaseAssetCashPositionCommMapper {

    List<String> selectAccountType(@Param("portfolioId") String portfolioId, @Param("myAcctNo") String myAcctNo);

    Map<String, Object> selectVDateNato(String finprodId);

    String selectFundType(String finprodId);

    String selectPortfolioIdByOrderId(String orderId);

    String getPayType(@Param("chlId") String chlId, @Param("myAcctNo") String myAcctNo);

//    String selectAccountTypeByPortAndChl(@Param("portfolioId") String portfolioId, @Param("chlId") String chlId);

    List<PtlSecPositionFlow> selectAllPosition(String tradeId);

    List<PtlSecPositionFlow> selectAllPositionByOffsetFlag(@Param("tradeId") String tradeId, @Param("offsetFlag") String offsetFlag);

    int deletePositionByTradeId(String tradeId);

    int deleteEtrPositionByTradeId(String tradeId);

    void updateSecPositionAmt(PtlSecPositionFlow ptlSecPositionFlow);

    List<String> selectOrderIdsByTradeId(String tradeId);

    List<String> selectOrderIdsByPTotalTradeId(String tradeId);

    List<PtlCashPositionFlow> selectAllCashPosition(@Param("orderIds") List<String> orderIds);

    int deleteCashPositionByOrderIds(@Param("orderIds") List<String> orderIds);

    void updateCashPositionAmt(PtlCashPositionFlow ptlCashPositionFlow);

    List<PtlSecPositionFlowDto> selectAllPositionByTotle(String tradeId);

    List<PtlCashPositionFlowDto> selectAllCashPositionByTotle(String tradeId);

    List<Map<String, Object>> getSubPortfolioDispercent(String portfolioId);

    /**
     * 数量反向
     * @param tradeId
     * @return
     */
    List<EtrSecPositionFlowDto> selectAllEtrPosition(String tradeId);

    /**
     * 数量反向
     * @param tradeId
     * @return
     */
    List<EtrSecPositionFlowDto> selectAllEtrPositionByOffsetFlag(@Param("tradeId") String tradeId, @Param("offsetFlag") String offsetFlag);

    /**
     * 数量反向
     * @param orderIds
     * @return
     */
    List<EtrCashPositionFlowDto> selectAllEtrCashPosition(List<String> orderIds);

    WflTrdProductDealAdd selectWflTradeByTradeId(String tradeId);

    /**
     * 根据汇总交易编号查询交易扩展表
     * @param tradeId
     * @return
     */
    List<WflTrdProductDealAdd> selectWflTradeByTotalTradeId(String tradeId);
    
    Map<String, Object> selectTradeReleaseStatus(Map<String, Object> map);

    int deleteCashPositionByOrderId(@Param("orderId") String orderId);

    /**
     * 根据通道id,查询通道的划款方式
     * @param chlId
     * @return
     */
    String findPayTypeByProductId(String chlId);

    /**
     * 查询资金账户
     * @param secAcctId
     * @return
     */
    String queryAccountNo(@Param("secAcctId") String secAcctId);

    /**
     * 根据汇总交易编号查询交易
     * @param totleTradeId
     * @return
     */
    List<TrdProductDeal> selectTrdProductDealByTotleTradeId(@Param("totleTradeId") String totleTradeId);
}
