package com.joyintech.tams.cfets.trd.bond.dao;

import com.joyintech.tams.cfets.trd.bond.dto.*;
import com.joyintech.tams.common.base.entity.*;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Repository
public interface CfetsIbuQuoteBondMapper {

    /**
     * 委托列表初始化
     * @param param
     * @return
     */
    List<CfetsBondDealEntrustOrderDto> listEntrust(Map<String,Object> param);

    /**
     * 是否有行权计划
     * @param param
     * @return
     */
    int countBondHasEexrPlan(Map<String,Object> param);

    /**
     * 获取证券账户
     * @param portfolioId
     * @param finprodId
     * @return
     */
    String getSecAcctId(@Param("portfolioId") String portfolioId,@Param("finprodId") String finprodId,@Param("tradeMarket") String tradeMarket);

    /**
     * 获取资金账户
     * @param portfolioId
     * @param acctType
     * @return
     */
    String getACCTNO(@Param("portfolioId") String portfolioId,@Param("acctType") String acctType);

    List<DealTransactionDetailsDto> listDealTransactionDetails(@Param("entrustId") String entrustId);

    int selectBondHasEexrPlanCount(Map<String, Object> param);

    String getRegistOrg(@Param("secAcctId") String secManageAcctId);

    /**
     * 通过资产代码获取登记托管机构
     * @param finprodId
     * @return
     */
    String getRegistOrgByFinprodId(@Param("finprodId") String finprodId);

    /**
     * 当日登记
     * @param param
     * @return
     */
    List<CfetsBondDayRegDto> listDayReg(Map<String, Object> param);

    int  updateRegFinProductDealTotal(@Param("totleTradeId") String totleTradeId,@Param("remark") String remark);

    int  updateRegFinProductDeal(@Param("totleTradeId") String totleTradeId,@Param("remark") String remark);

    List<RegTradeFeeTotal> listRegTradeFeeTotal(@Param("totleTradeId") String totleTradeId);

    List<WflRegTradeFeeTotal> listWflRegTradeFeeTotal(@Param("totleTradeId") String totleTradeId);

    List<RegFinProductDeal> listRegFinProductDeal(@Param("totleTradeId") String totleTradeId);

    List<WflRegFinProductDeal> listWflRegFinProductDeal(@Param("totleTradeId") String totleTradeId);

    List<RegFinProductDealAdd> listRegFinProductDealAdd(@Param("totleTradeId") String totleTradeId);

    List<WflRegFinProductDealAdd> listWflRegFinProductDealAdd(@Param("totleTradeId") String totleTradeId);

    List<RegTradeFee> listRegTradeFee(@Param("totleTradeId") String totleTradeId);

    List<WflRegTradeFee> listWflRegTradeFee(@Param("totleTradeId") String totleTradeId);

    List<WflRegFinPledge> listWflRegFinPledge(@Param("totleTradeId") String totleTradeId);

    CfetsBondDealQuoteCommonDto getCfetsBondDealQuoteCommonDto(@Param("quoteId") String quoteId);

    CfetsBondDealQuoteCommonDto getWflCfetsBondDealQuoteCommonDto(@Param("quoteId") String quoteId);

    List<PtlAllocationDto> listPtlAllocationDto(@Param("quoteId") String quoteId);

    List<PtlAllocationDto> listWflPtlAllocationDto(@Param("quoteId") String quoteId);

    int countMatchInfo(@Param("quoteId") String quoteId,@Param("CONFIRM_STATUS_1") String CONFIRM_STATUS_1);

    BigDecimal getWflShareAmtByEntrustId(@Param("entrustId") String entrustId);

    BigDecimal getWflShareAmtByEntrustIdAndWorkFlowId(@Param("entrustId") String entrustId,@Param("totleTradeId") String workFlowId);

    String getTradeStatus(@Param("quoteId") String quoteId,@Param("cpPartyId") String cpPartyId);

    String  getIssuserName(@Param("finprodId") String finprodId);

    String  getPartyIdByPtl(@Param("portfolioId") String portfolioId);
}
