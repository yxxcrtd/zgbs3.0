package com.joyintech.tams.invest.trade.exchangetransmatch.common.dao;

import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.invest.common.transmatch.dto.ExchangeTransMatchCommonEntrustDetailDto;
import com.joyintech.tams.invest.common.transmatch.dto.ExchangeTransMatchCommonEntrustDto;
import com.joyintech.tams.invest.common.transmatch.dto.ExchangeTransMatchCommonRevokeDto;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public interface ExchangeTransMatchCommonMapper {

    /**
     * 查询委托信息--根据idlIST
     * @param entrustMap
     * @return
     */
    List<ExchangeTransMatchCommonEntrustDto> getEtrEntrustByIdList(Map<String,Object> entrustMap);

    /**
     * 查询回购质押品汇总信息信息
     * @param entrustMap
     * @return
     */
    List<SettleTransDataTotalPledge> getSettleTransDataTotalPledgeList(Map<String,Object> entrustMap);

    /**
     * 获取下一工作日
     */
    Date getNextWorkDay(@Param("nowday") Date nowday, @Param("calendarId") String calendarId);

    /**
     * 查询前置交易编号
     * @param paraMap 查询参数
     * @return 前置交易编号
     */
    String selectBTradeId(Map<String, String> paraMap);

    /**
     * 根据交易编号查询汇总附属信息
     *
     * @param tradeId 交易编号
     * @return 汇总附属信息
     * @see
     * @since 1.0
     */
    TrdDealTotalAdd queryTotalAddByTradeId(@Param("tradeId") String tradeId);

    /**
     * 根据汇总流水号获取成交明细
     *
     * @param paraMap 流水号
     * @return 汇总附属信息
     * @see
     * @since 1.0
     */
    List<SettleTransData> getSettleTransDataList(Map<String, Object> paraMap);


    /**
     * 汇总成交数据匹配结果表（SETTLE_TRANS_DATA_TOTAL_MATCH）：数据删除
     * @param paraMap
     */
    void deleteSettleTransDataTotalMatch(Map<String, String> paraMap);

    /**
     * 删除.成交数据与投管交易关系（SETTLE_TRANS_TG_MAPPING）
     * @param tradeId
     */
    void deleteSettleTransTgMapping(@Param("tradeId") String tradeId);

    /**
     * 获取汇总流水号
     * @param tradeId
     * @return
     */
    String getTotalSeqId(@Param("tradeId") String tradeId);

    /**
     * 获取指令编号
     * @param tradeId
     * @return
     */
    String getEntrunstId(@Param("tradeId") String tradeId);

    /**
     * 获取剩余份额
     * @return
     */
    BigDecimal getRemainNum(@Param("totalSeqId") String totalSeqId);

    /**
     * 查询成交数据与投管交易关系（SETTLE_TRANS_TG_MAPPING）
     * @param paraMap
     * @return
     */
    List<ExchangeTransMatchCommonRevokeDto> getSettleTransTgMapping(Map<String, Object> paraMap);

    List<ExchangeTransMatchCommonEntrustDetailDto> getEntrustDetailList(@Param("entrustId") String entrustId);

    /**
     * 获取首期交易的质押品信息
     * @param paraMap
     * @return
     */
    List<FinDealPledge> getFirstRepoFinDealPledge(Map<String, Object> paraMap);

    /**
     * 根据汇总交易编号获取质押券置换结果list
     * @param paraMap
     */
    List<FinPledgeDisplaceResult> getFinPledgeDisplaceResult (Map<String, Object> paraMap);


    /**
     * 删除汇总成交质押品匹配结果表数据
     * @param paraMap
     * @return
     */
    int deleteSettleDataTotalPledgeMatch(Map<String, Object> paraMap);

    /**
     * 批量更新指令状态
     * @param paraMap
     * @return
     */
    int updateEntrustStatus(Map<String, Object> paraMap);

    // modify by etliu 20220110 start
    // GZYHTZJY-1032: 协议回购的头寸处理，需要走PTL_POSITION_FLOW_CFG配置
    FinDealPledge selectFinDealPledgeByPrimaryKey(FinDealPledgeKey finDealPledgeKey);

    /**
     * 查询首期质押券信息
     * @param finDealPledgeKey
     * @return
     */
    List<FinDealPledge> selectFinDealPledges(FinDealPledgeKey finDealPledgeKey);

//    /**
//     * 根据首期成交编号查询协议回购首期的交易编号
//     * @param paraMap 参数
//     * @return 协议回购首期的交易编号
//     */
//    String selectHgsqFinprodId(Map<String, Object> paraMap);
    // modify by etliu 20220110 end

    String getTradeIdByTotalTradeId(@Param("totalTradeId") String totalTradeId);

    List<PtlSecPosition> searchPtlSecPositionList(Map<String, Object> paraMap);

    /**
     * 根据金融产品代码，日期，查询出当日折算率
     *
     * @param finprodId 金融产品代码
     * @return EtrEntrust
     * @see
     * @since 1.0
     */
    BigDecimal queryConverseRadio(@Param("finprodId") String finprodId, @Param("vdate") Date vdate);

    /**
     * 查询资金账户
     * @param secAcctId
     * @return
     */
    String queryAccountNo(@Param("secAcctId") String secAcctId);

    /**
     * 查询质押品信息
     * @param tradeId
     * @return
     */
    List<FinDealPledge> getFinDealPledge(@Param("tradeId") String tradeId);

    /**
     * 金融产品交易表
     * @param finprodId
     * @return
     */
    FinTradeProduct getFinTradeProduct(@Param("finprodId") String finprodId);

    /**
     * 根据指令编号查询交易
     * @param entrustId
     * @return
     */
    List<TrdProductDeal> getTrdProductDealsByEntrustId(@Param("entrustId") String entrustId);
}
