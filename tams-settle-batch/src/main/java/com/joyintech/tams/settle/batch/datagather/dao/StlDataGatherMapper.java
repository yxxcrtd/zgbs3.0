package com.joyintech.tams.settle.batch.datagather.dao;

import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.settle.batch.datagather.dto.EtrEntrustDto;
import com.joyintech.tams.settle.batch.datagather.dto.SettleDelDealDetailDto;
import com.joyintech.tams.settle.batch.datagather.dto.SettleTransDataDto;
import com.joyintech.tams.settle.batch.datagather.dto.TotalTrdProductDealDto;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author 何阳阳
 * @date 2020/7/17
 * @see StlDataGatherMapper
 * @since 1.0
 */
@Repository
public interface StlDataGatherMapper {

    /**
     * 删除参数
     */
    int deleteParam(@Param("dealDate") Date dealDate);
    /**
     * 获取成交明细数据
     * @param paraMap 查询参数
     * @return 成交明细列表
     */
    List<SettleTransDataDto> selectSettleTransData(Map<String, Object> paraMap);

    /**
     * 获取投管交易编号
     * @param 查询参数
     * @return 投管交易编号
     */
    List<String> selectTradeIdTgs(Map<String, Object> paraMap);
    /**
     * 查询前置交易编号
     * @param paraMap 查询参数
     * @return 前置交易编号
     */
    String selectBTradeId(Map<String, String> paraMap);

    /**
     * 获取成交明细数据
     * @param paraMap 查询参数
     * @return 成交明细列表
     */
    List<SettleTransDataDto> selectContinuationSettleTransData(Map<String, Object> paraMap);

    /**
     * 获取成交明细数据
     * @param paraMap 查询参数
     * @return 成交明细列表
     */
    List<SettleTransDataDto> selectEndSettleTransData(Map<String, Object> paraMap);

    /**
     * 获取成交明细数据
     * @param paraMap 查询参数
     * @return 成交明细列表
     */
    List<SettleTransDataDto> selectBiddingSettleTransData(Map<String, Object> paraMap);

    /**
     * 获取基金除权成交明细数据
     * @param paraMap 查询参数
     * @return 成交明细列表
     */
    List<SettleDelDealDetailDto> selectFundDelSettleTransData(Map<String, Object> paraMap);

    // ZGTFCP-1363:通过开关控制查询新老头寸,默认查询新头寸 modify by fuyongsheng 20220321 start
    /**
     * 获取基金除权成交明细数据(新头寸)
     * @param paraMap 查询参数
     * @return 成交明细列表
     */
    List<SettleDelDealDetailDto> selectFundDelSettleTransDataNewPosition(Map<String, Object> paraMap);
    // ZGTFCP-1363:通过开关控制查询新老头寸,默认查询新头寸 modify by fuyongsheng 20220321 end

    /**
     * 获取股票除权交易明细
     * @param paraMap 查询参数
     * @return 除权交易明细
     */
    List<SettleDelDealDetailDto> selectSettleDelDealDetail(Map<String, Object> paraMap);

    /**
     * 获取现金分红成交明细数据
     * @param paraMap
     */
    List<SettleTransDataDto> selectCashBonusSettleTransData(Map<String, Object> paraMap);

    /**
     * 获取现金分红成交明细数据
     * @param paraMap
     */
    List<SettleTransDataDto> selectCashBonusSettleTransDataNewPosition(Map<String, Object> paraMap);

    /**
     * 获取申购确认成交明细数据
     * @param paraMap
     */
    List<SettleTransDataDto> selectCashBonusSettleTransData1(Map<String, Object> paraMap);
    
    /**
     * 更新SETTLE_TRANS_TG_MAPPING
     * @param paraMap 更新参数
     * @return 更新结果
     */
    int updateSettleTransTgMapping(Map<String, Object> paraMap);
    
    /**
     * 检索非货基现金分红对应的除权交易
     * @param paraMap Map<String, Object>
     * @return 非货基现金分红对应的除权交易
     */
    List<TotalTrdProductDealDto> selectNhfTotalTrdProductDealDto(Map<String, Object> paraMap);
    
    /**
     * 根据交易编号查询汇总附属信息
     * 
     * @param tradeId 交易编号
     * @return 汇总附属信息
     * @see
     * @since 1.0
     */
    TrdDealTotalAdd queryTotalAddByTradeId(@Param("tradeId") String tradeId);


    List<EtrEntrustDto> listEtrEntrust(@Param("finprodType") List<String> finprodType,@Param("entrustDate") Date entrustDate);

    /**
     * 债券存续期
     * @param paraMap
     * @return
     */
    List<SettleTransDataDto> listBondSettleTransData(Map<String, Object> paraMap);

    /**
     * 竞价回购存续期
     * @param paraMap
     * @return
     */
    List<SettleTransDataDto> listRepoSettleTransData(Map<String, Object> paraMap);

    /**
     * 协议回购到期成交明细数据
     * @param paraMap
     * @return
     */
    List<SettleTransDataDto> listRepoH01MaturitySettleTransData(Map<String, Object> paraMap);
    /**
     * 持仓余额
     * @param param
     * @return
     */
    List<PtlSecPosition> listPtlSecPosition(Map<String,Object> param);

    // ZGTFCP-1363:通过开关控制查询新老头寸,默认查询新头寸 modify by fuyongsheng 20220321 start
    /**
     * 新持仓余额
     * @param param
     * @return
     */
    List<PtlSecPosition> listPtlSecPositionRule(Map<String,Object> param);
    // ZGTFCP-1363:通过开关控制查询新老头寸,默认查询新头寸 modify by fuyongsheng 20220321 end

    /**
     * 债券申购确认
     * @param paraMap
     * @return
     */
    List<SettleTransDataDto> listBondPurConfirSettleTransData(Map<String, Object> paraMap);

    /**
     * 债券股票上市流通
     * @param paraMap
     * @return
     */
    List<SettleTransDataDto> listBoundSharesMarketTradeData(Map<String, Object> paraMap);

    /**
     * 组合持仓流水
     * @param param
     * @return
     */
    List<PtlSecPositionFlow> listPtlSecPositionFolw(Map<String, Object> param);

    /**
     * 组合持仓新头寸流水
     * @param param
     * @return
     */
    List<PtlSecPositionFlow> listPtlSecPositionFolwRule(Map<String, Object> param);

    /**
     * 获取现金分红成交明细数据
     * @param paraMap
     */
    List<SettleTransDataDto> selectFundSettleTransData(Map<String, Object> paraMap);

    List<SettleTransDataDto> selectFundSettleTransData1(SettleTransDataDto dto);

    List<SettleTransDataDto> selectFundSettleTransData1NewPosition(SettleTransDataDto dto);

    List<SettleTransDataDto> selectFundSettleTransData2(SettleTransDataDto dto);

    List<SettleTransDataDto> selectFundSettleTransData2NewPosition(SettleTransDataDto dto);

    List<String> selectCqTotleTradeId(Map<String, Object> map);

    List<String> selectCqtradeId(String id);

    void deleteCqTrdProductDealAdd(String tradeId);

    void deleteCqPaTrdProductDeal(String tradeId);

    void deleteCqPtlTrade(String tradeId);

    void deleteCqTrdFinProductDealTotal(String totleTradeId);

    void deleteCqTrdProductDeal(String totleTradeId);

    void deleteCqPaTrdFinProductDealTotal(String totleTradeId);

    void deleteCqTrdDealTotalAdd(String totleTradeId);

    String selectCqBtradeId(@Param("secAcctId") String secAcctId, @Param("finprodId") String finprodId,@Param("dealDate") Date dealDate);

    List<SettleTransDataTotalPledge> selectSettleTransDataTotalPledge(String seqId);

    // modify by etliu 20211227 start
    // GZYHTZJY-777:回售交易当日无法得到成交的清算数据，导致无法匹配到对应的上行委托或指令

    /**
     * 查询债回售、可转债申购、新股申购、配债、配股对应指令
     * @param paramMap paramMap
     * @return 债回售、可转债申购、新股申购、配债、配股对应指令
     */
    List<EtrEntrustDto> selectSpecialEtrEntrustData(Map<String, Object> paramMap);

    /**
     * 查询行权-回售计划
     * @param paramMap paramMap
     * @return 行权-回售计划
     */
    List<FinExercisePlan> selectFinExercisePlanData(Map<String, Object> paramMap);

    /**
     * 查询配股、配债对应申报信息
     * @param paramMap paramMap
     * @return 配股、配债对应申报信息
     */
    List<FinProductOthCode> selectFinProductOthCodeData(Map<String, Object> paramMap);
    // modify by etliu 20211227 end

    /**
     * 查询指令质押券信息
     * @param entrustId
     * @param finProdId
     * @return
     */
    List<EtrPledge> selectEtrPledges(@Param("entrustId")String entrustId, @Param("finProdId")String finProdId);

    int selectDzcjList(Map<String, Object> searchParam);
    /**
     * 根据汇总交易编号查询出所有汇总交易
     * @param tradeIdTgList
     * @return 所有汇总交易
     */
    List<TrdFinProductDealTotal>  selectTotalTradesBytotleTradeId(@Param("tradeIdTgList") List<String> tradeIdTgList);

    /**
     * 根据汇总的成交明细查询出原成交明细列表
     * @param stdMap
     * @return成交明细列表
     */
    List<SettleTransDataDto> selectSettleTransDatas(Map<String, Object> stdMap);

    /**
     * 查询一级投标债券对应信息
     * @param searchParam
     * @return
     */
    List<SettleTransDataDto> selectBondFirstBiddingData(Map<String, Object> searchParam);

    List<BidOrderBidPrice> selectBidOrderBidPrices(Map<String, Object> searchParam);
}
