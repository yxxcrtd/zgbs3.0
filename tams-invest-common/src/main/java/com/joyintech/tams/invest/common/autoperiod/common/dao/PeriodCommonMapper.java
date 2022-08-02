/*
 * 文件名：FinProductModule002Mapper.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：何阳阳
 * 修改时间：2018年8月31日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.common.autoperiod.common.dao;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.joyintech.tams.common.base.entity.*;
import com.joyintech.framework.select.SelectOptionDto;

import com.joyintech.tams.invest.common.dto.common.FinAssetIntInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * 债券存续期管理
 *
 * @author 强成西
 * @version 1.0 2018年8月31日
 * @see PeriodCommonMapper
 * @since 1.0
 */
@Repository
public interface PeriodCommonMapper {

    List<TrdProductDeal> findDealByTotalId(Map<String, Object> params);

    void inertRevokeFeeDetailByTradeId(@Param("bizCode") String totleTradeId, @Param("updateUser") String updateUser);

    void insertRevokeStlPayOrdersByTradeId(@Param("bizCode")String totleTradeId, @Param("updateUser")String updateUser);

    String selectProdRegistCode(@Param("portfolioId") String portfolioId, @Param("F16") String value);

    BigDecimal findLatestClosingPrice(Map<String, Object> params);

    void insertRevokePaTrdProductDeal(@Param("bizCode")String tradeId, @Param("updateUser") String updateUser);

    void insertRevokeTrdProductDealAdd(@Param("bizCode")String oldTradeId, @Param("updateUser")String updateUser);

    void insertRevokePtlTrade(@Param("deal") PtlTrade deal, @Param("updateUser")String updateUser);

    String findMaxSecAcctNo(Map<String, Object> param);

	/**
	 * 删除交易报送
	 * <功能详细描述>
	 * @param tradeId 交易编号
	 * @see
	 * @since 1.0
	 */
	void deleteRepTradeByTradeId(String tradeId);

    List<StlPayOrders> findOrdersByOrderId(String bizCode);

    void insertWflTotle(Map<String, Object> params);

    void insertWflTrade(Map<String, Object> params);

    void insertWflPaTotal(Map<String, Object> params);

    void insertWflPaTrade(Map<String, Object> params);

    void insertWflTradeAdd(Map<String, Object> params);

    void insertWflTradeTotalAdd(Map<String, Object> params);




    void insertWflPayOrders(Map<String, Object> params);

    // 非汇总指令时，撤销交易对应的划款指令 add by renping 2019-09-10 start
    void insertWflPayOrdersOfDeal(Map<String, Object> params);
    // 非汇总指令时，撤销交易对应的划款指令 add by renping 2019-09-10 end
    List<WflPaTrdProductDeal> findWflPaByWorkflowId(String workflowId);
    List<WflTrdProductDealAdd> findWflAddByWorkflowId(String workflowId);
    /**
     * 根据交易编号删除数据
     * @param map 参数
     */
    void deleteWflByTradeId (Map<String, Object> map);
    /**
     * 查询后续交易的交易编号
     */
    String findFollowDeal(Map<String, Object> map);

    List<String> findSubTotalTradeId(String bizCode);

    String findInstitutionName(String finprodId);
    /**
     * 
     * 获取业务编号下拉框
     * <功能详细描述>
     * @param param 参数
     * @return 查询结果
     * @see
     * @since 1.0
     */
    List<SelectOptionDto> getBusiCode(Map<String, Object> param);

	/**
	 * 查询对应交易的编号
	 * <功能详细描述>
	 * @param map 参数
	 * @return 查询结果
	 * @see
	 * @since 1.0
	 */
	List<String> findSubTradeId(Map<String, Object> map);

    String getDepoInstitution(String finprodMarketId);

    String getPayType(String chlId);

    List<TrdProductDealAdd> findDealAddByTradeIds(List<TrdProductDeal> trdProductDeals);

    List<FinAssetIntInfo> getPortfolioListInfo(Map<String,Object> map);

    List<TrdProductDeal> findDealByTotalIdForInterf(Map<String, Object> map);

    List<PtlTrade> findPtlTradeByTradeIds(List<TrdProductDeal> trdProductDeals1);

    void inertRevokeFeeTotalByTradeId(@Param("bizCode")String tradeId, @Param("updateUser") String updateUser);

    int countAccNoNum(Map<String, Object> paraMap);

    List<FinCashInfo> getFinCashInfosByFinprodId(@Param("finprodId")String finprodId);

    void deleteFinCashPlanByCashId(@Param("cashId")String cashId);
}
