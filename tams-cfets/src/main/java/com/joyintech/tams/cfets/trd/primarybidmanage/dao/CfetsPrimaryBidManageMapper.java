/*
 * 文件名：EtrBondFirstBiddingMapper.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：yangjm
 * 修改时间：2019年5月6日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.cfets.trd.primarybidmanage.dao;

import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.tams.cfets.trd.primarybidmanage.dto.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.business.core.dto.WflValidInputParameter;
import com.joyintech.tams.invest.common.asset.dto.FinCashPlanDto;
import com.joyintech.tams.invest.common.trade.dto.SecInfoModelDto;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  场内交易指令Mapper
 * 〈功能详细描述〉
 *
 * @author wangyang1
 * @version 1.0 2022年4月13日
 * @see CfetsPrimaryBidManageMapper
 * @since 1.0
 */
@Repository
public interface CfetsPrimaryBidManageMapper {

	List<CfetsPrimaryBidManageCfmResponse> selectCfmForPage(Map<String, Object> pageParam);

	CfetsPrimaryBidManageCfmResponse getBidManageCfmInfo(Map<String, Object> pageParam);

	List<SelectOptionDto> getCounterId(Map<String, Object> map);

	List<BidProductDto> getBidProductList(Map<String, Object> param);

	List<CfetsPrimaryBidManageResponse> selectForPage(Map<String, Object> pageParam);

	List<CfetsPrimaryBidManageResponse> selectDetailForPage(Map<String, Object> pageParam);

	CfetsPrimaryBidManageSaveRequest selectBidOrderInfo(Map<String, Object> param);

	List<CfetsPrimaryBidManageSaveDetailDto> selectBidOrderDtail(Map<String, Object> param);

	CfetsPrimaryBidManageSaveRequest selectBasicInfo(Map<String, Object> param);

	List<CfetsPrimaryBidManageSaveDetailDto> selectBasicDtail(Map<String, Object> param);

	List<String> getBusinessData(WflValidInputParameter parameter);

	List<StlCapacctInfo> selectCounterCapacctInfoList(@Param("counterId") String counterId, @Param("acctType") String acctType, @Param("ccy") String ccy);

    SecInfoModelDto getSecInfoModel(Map<String, Object> tempParamMap);

	List<FinCashPlanDto> getFinCashPlan(HashMap<String, Object> map);

	List<FinCashCalcRule> selectBondsIntResetList(Map<String, Object> param);

	BigDecimal getCurSecAmt(Map<String, Object> param);

	List<TrdFinProductDealTotal> getTrdFinProductDealTotal(@Param("bTradeId") String bTradeId);

	List<TrdProductDeal> queryTrdProductDealByTotleTradeId(@Param("tradeId") String tradeId);

	List<StlOrders> queryStlPayOrdersByTradeId(@Param("tradeId") String tradeId);

	List<PtlTrade> queryPtlTradeByTradeId(@Param("tradeId") String tradeId);

	List<SelectOptionDto>  selectTrader(Map<String, Object> map);
}
