/*
 * 文件名：ResetBaseRuleInfoMapper.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：sjj
 * 修改时间：2021年11月19日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.invest.batch.cfetssettle.dao;

import java.util.List;
import java.util.Map;

import com.joyintech.tams.common.base.entity.PtlSecPosition;
import com.joyintech.tams.common.base.entity.PtlSecPositionFlow;
import com.joyintech.tams.common.base.entity.RegFinProductDeal;
import com.joyintech.tams.common.base.entity.TrdBankManualSettleInfo;
import com.joyintech.tams.invest.batch.cfetssettle.dto.CfetsPledgeInfoResponse;
import com.joyintech.tams.invest.batch.cfetssettle.dto.CfetsSettleDealInfo;
import com.joyintech.tams.invest.batch.cfetssettle.dto.CfetsSettleAllInfoDto;
import com.joyintech.tams.invest.batch.cfetssettle.dto.PtlFlowsDto;
import com.joyintech.tams.invest.common.dto.common.PledgeBondInfoDto;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;


/**
 * reader相关业务处理
 * 〈功能详细描述〉
 * @author sjj
 * @version 1.0 2021年11月19日
 * @see CfetsSettleMapper
 * @since 1.0
 */
@Repository
public interface CfetsSettleMapper {

    /**
     * 查询待交收数据List
     * <功能详细描述>
     * @param paramMap
     * @return
     * @see
     * @since 1.0
     */
    List<CfetsSettleAllInfoDto> selectAllSettleToDoList(Map<String, Object> paramMap);

    /**
     * 删除交收信息表
     * @param paramMap
     * @return
     * @see
     * @since 1.0
     */
    void deleteTodayCfetsSettleInfo(Map<String, Object> paramMap);

    /**
     * 删除交收明细表
     * @param paramMap
     * @return
     * @see
     * @since 1.0
     */
    void deleteTodayCfetsSettleFlow(Map<String, Object> paramMap);

    /**
     * 删除交收流水表
     * @param paramMap
     * @return
     * @see
     * @since 1.0
     */
    void deleteTodayCfetsSettleDetail(Map<String, Object> paramMap);

    /**
     * 插入/更新交收信息表
     * @param trdBankManualSettleInfo
     * @return
     * @see
     * @since 1.0
     */
    void mergeInfo(TrdBankManualSettleInfo trdBankManualSettleInfo);

    /**
     * 成交确认明细查询
     *
     * @param tradeId 参数
     * @return List<CfetsManualSettleDealInfo>
     */
    List<CfetsSettleDealInfo> getDealDetailsByTradeId(@Param("tradeId") String tradeId);

    /**
     * 成交确认明细查询(临时表查询)
     *
     * @param tradeId 参数
     * @return List<CfetsManualSettleDealInfo>
     */
    List<CfetsSettleDealInfo> getTempDealDetailsByTradeId(@Param("tradeId") String tradeId);

    /**
     * 登记编号查询
     *
     * @param tradeId 参数
     * @return String
     */
    String selectRegTradeIdBytradeId(@Param("tradeId") String tradeId);

    /**
     * 报价编号查询
     * @param tradeId1
     * @return
     */
    String selectIbTradeIdBytradeId(@Param("tradeId1") String tradeId1);

    /**
     * 报价编号查询
     *
     * @param tradeId 参数
     * @return String
     */
    String selectQuoteIdBytradeId(@Param("tradeId") String tradeId);

    /**
     * 根据交易编号检索组合持仓流水（持仓类型为实际）
     *
     * @param param param
     * @return List<PtlSecPositionFlowDto>
     * @see
     * @since 1.0
     */
    List<PtlSecPositionFlow> selectPtlSecPositionFlow(Map<String, Object> param);

    void updatePosition(PtlSecPosition position);

    /**
     * 查询证券账户
     * @param param
     * @return
     */
    String qryPortfolioSecAcctId(Map<String, Object> param);

    /**
     * 查询托管户
     * @param param
     * @return
     */
    String qryPortfolioCashAcctId(Map<String, Object> param);
    /**
     * 回购质押券信息
     * @param tradeId
     * @return
     */
    List<PledgeBondInfoDto> getPledgeListByTradeId(@Param("tradeId") String tradeId);

    /**
     * 报价编号查询报价信息
     *
     * @param tradeId
     * @return
     */
    List<PtlFlowsDto> selectIbQuoteBytradeId(@Param("tradeId") String tradeId);

    RegFinProductDeal selectRegFinProductDealByPrimaryKey(String tradeId);
    /**
     * 查询交易质押券,针对交易明细回购
     *
     * @param pageParam 参数
     * @return List<CfetsPledgeInfoResponse>
     */
    List<CfetsPledgeInfoResponse> findRepoPledgeInfoList(Map<String, Object> pageParam);

    /**
     * 成交确认明细查询
     *
     * @param tradeId 参数
     * @return String
     */
    String qryEntrustId(@Param("tradeId") String tradeId);

}
