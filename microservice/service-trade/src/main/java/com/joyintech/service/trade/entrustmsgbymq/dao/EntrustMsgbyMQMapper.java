package com.joyintech.service.trade.entrustmsgbymq.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.joyintech.tams.common.base.entity.*;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.joyintech.service.trade.entrustmsgbymq.dto.TrdTempDto;

@Repository
public interface EntrustMsgbyMQMapper {
    /**
     * 根据金融产品代码查询投管资产大类信息
     * 
     * @param finprodId 金融产品代码
     * @param branch 分支序号
     * @return 投管资产大类
     * @see
     * @since 1.0
     */
    String queryById(@Param("finprodId") String finprodId,@Param("branch") long branch);
    
    /**
     * 根据金融产品代码查询投管资产二级分类
     * 
     * @param finprodId 金融产品代码
     * @param branch 分支序号
     * @return 投管资产大类
     * @see
     * @since 1.0
     */
    String queryType6ById(@Param("finprodId") String finprodId,@Param("branch") long branch);
    /**
     * 根据资金账户代码查询证券账户代码
     * 
     * @param finprodId 金融产品代码
     * @param branch 分支序号
     * @return 投管资产大类
     * @see
     * @since 1.0
     */
    String querySecAcctIdByAcctId(@Param("acctNo") String acctNo);
    
    /**
     * 根据接口成交信息查询证券账户代码
     * 
     * @param param
     * @return 证券账户代码
     * @see
     * @since 1.0
     */
    String queryByAcctNo(Map<String, Object> param);
    
    /**
     * 根据委托代码查询回购类型
     * 
     * @param entrustId 委托代码
     * @return 回购类型
     * @see
     * @since 1.0
     */
    String queryAssetType1(@Param("entrustId") String entrustId);
    
    /**
     * 根据委托代码查询交易员
     * 
     * @param entrustId 委托代码
     * @return 交易员
     * @see
     * @since 1.0
     */
    String queryTradeId(@Param("entrustId") String entrustId);
    
    /**
     * 根据委托代码查询委托信息
     * 
     * @param entrustId 委托代码
     * @return EtrEntrust
     * @see
     * @since 1.0
     */
    EtrEntrust queryEtrEntrust(@Param("entrustId") String entrustId);

    /**
     * 根据委托代码查询委托信息
     *
     * @param entrustId 委托代码
     * @return EtrEntrust
     * @see
     * @since 1.0
     */
    EtrEntrustAdd queryEtrEntrustAdd(@Param("entrustId") String entrustId);

    /**
     * 根据委托代码查询目标类型
     * 
     * @param entrustId 委托代码
     * @return 目标类型
     * @see
     * @since 1.0
     */
    String queryTargetType(@Param("entrustId") String entrustId);

    /**
     * 根据委托代码查询委托金额、份额
     *
     * @param entrustId 委托代码
     * @return EtrEntrust
     * @see
     * @since 1.0
     */
    EtrEntrust queryEntrustNumAndAmt(@Param("entrustId") String entrustId);

    /**
     * 更新委托状态
     * @param
     * @return
     */
    int updateEntrustStatu(EtrEntrust etrEntrust);

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
	 * 组合资产开平仓收益
	 * @param trdTempDto
	 */
	List<Map<String, Object>> getOpenClosePosition(TrdTempDto trdTempDto);
	
	/**
	 * 合约乘数
	 * @param finprodId
	 */
	BigDecimal queryContractMultiplier(@Param("finprodId") String finprodId);

    /**
     * 回购质押品查询
     * @param param
     * @return
     */
    List<FinDealPledge> getFinDealPledgeList(Map<String, Object> param);

    /**
     * 回购质押品删除
     * @param param
     * @return
     */
    int deleteFinDealPledge(Map<String, Object> param);

    void updateTrdProductTempDeal(TrdProductTempDeal trdProductTempDeal);

    void updateTrdProductDeal(TrdProductTempDeal trdProductTempDeal);

	FinDealPledge selectFinDealPledgeByPrimaryKey(FinDealPledgeKey finDealPledgeKey);

    FinProductOthCode selectFinProductOthCode(Map<String, Object> map);

    String selectPledgeId(String getvTradeId);

    // List<TrdProductDeal> getTrdProductDealByFTradeId(String fTradeId);
}