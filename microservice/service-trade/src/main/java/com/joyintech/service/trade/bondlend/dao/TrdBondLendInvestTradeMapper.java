/*
 * 文件名：TrdBondLendInvestTradeMapper.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：裴家俊
 * 修改时间：2019年6月11日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.service.trade.bondlend.dao;

import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.service.trade.bondlend.dto.FinCashPlanDto;
import com.joyintech.service.trade.bondlend.dto.SecInfoModelDto;
import com.joyintech.service.trade.bondlend.dto.TrdBondLendInvestTradeDto;
import com.joyintech.tams.common.base.entity.PtlSecValution;
import com.joyintech.tams.common.base.entity.PtlSecValutionKey;
import com.joyintech.tams.common.base.entity.StlCapacctInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 债券借贷交易Mapper
 *
 * @author 裴家俊
 * @version 1.0 2018年9月7日
 * @see TrdBondLendInvestTradeMapper
 * @since 1.0
 */
@Repository
public interface TrdBondLendInvestTradeMapper {

    /**
     * 检索债券借贷工交易列表
     *
     * @param paraMap 参数
     * @return 债券借贷交易列表
     * @see
     * @since 1.0
     */
    List<TrdBondLendInvestTradeDto> selectDeal(Map<String, Object> paraMap);

    List<SuggestDto> getSecManageAcctId(Map<String, Object> param);

    BigDecimal getAssetAvailablePosition (Map<String, Object> param);
    
    List<SuggestDto> getChannel(Map<String, Object> param);

    List<String> getChannelInfo(Map<String, Object> param);

    List<SelectOptionDto> getFinprodId(Map<String, Object> param);

    TrdBondLendInvestTradeDto getTrdBondLendInvestTradeDto(Map<String, Object> params);

    SecInfoModelDto getSecInfoModel(Map<String, Object> param);

    List<FinCashPlanDto> getFinCashPlan(Map<String, Object> param);


    /**
     * 查询组合资产估值
     *
     * @param paramDto 参数
     * @return 组合资产估值
     * @see
     * @since 1.0
     */
    PtlSecValution selectPtlSecValutionData(PtlSecValutionKey paramDto);

	List<SelectOptionDto> getPortfolioId(Map<String, Object> param);
    
    /**
     * 取得证券托管户的登记托管机构
     * 
     * @param param Map<String, Object>
     * @return 证券托管户的登记托管机构
     */
    String getRegistOrg(Map<String, Object> param);

    /**
     * 检索产品关联资金账户共通
     * @param portfolioId 组合代码
     * @param ccy 币种
     * @param capacctType 资金账户类型
     * @return 产品关联资金账户
     */
    List<StlCapacctInfo> selectProdCapacctInfoList(@Param("portfolioId") String portfolioId, @Param("ccy") String ccy, @Param("capacctType") String capacctType);

    /**
     * 检索交易关联资金账户共通
     * @param secManageAcctId 证券托管户ID
     * @param ccy 币种
     * @return 交易关联资金账户
     */
    List<StlCapacctInfo> selectDealCapacctInfoList(@Param("secManageAcctId") String secManageAcctId, @Param("ccy") String ccy);

    /**
     * 检索交易对手账户共通
     * @param secManageAcctId 交易对手ID
     * @param acctType 账户类型
     * @return 交易对手账户
     */
    List<StlCapacctInfo> selectCounterCapacctInfoList(@Param("counterId") String counterId, @Param("acctType") String acctType);
}
