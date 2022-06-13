/*
 * 文件名：FinProductCommonMapper.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：wangzhiang
 * 修改时间：2018年5月31日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.common.batch.finproduct.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.joyintech.tams.valuation.common.batch.finproduct.entity.*;
import com.joyintech.tams.valuation.common.dto.TrdFinProductBarterDto;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.joyintech.tams.common.base.entity.BokDetailId;
import com.joyintech.tams.common.base.entity.FinExercisePlan;
import com.joyintech.tams.common.base.entity.FinProduct;
import com.joyintech.tams.common.base.entity.FinProductValuation;
import com.joyintech.tams.common.base.entity.MstBondValuationInfo;
import com.joyintech.tams.common.base.entity.MstIntRateInfo;
import com.joyintech.tams.common.base.entity.MstShareLqratio;
import com.joyintech.tams.common.base.entity.MstSharePrice;
import com.joyintech.tams.common.base.entity.MstValFx;
import com.joyintech.tams.common.base.entity.MstValIrs;

/**
 * 标的类共通方法 - 数据库数据获取
 * 
 * @author wangzhiang
 * @version 1.0 2018年5月31日
 * @see FinProductCommonMapper
 * @since 1.0
 */
@Repository("valuationFinProductCommonMapper")
public interface FinProductCommonMapper {

    /**
     * 查询标的信息
     * 
     * @param paMap 传递参数
     * @return TargetFinProduct
     * @see
     * @since 1.0
     */
    List<TargetFinProduct> getTargetFinProduct(Map<String, Object> paMap);

    /**
     * 查询标的现金流计划list
     * 
     * @param paMap 传递参数
     * @return List<TargetCashPlan>
     * @see
     * @since 1.0
     */
    List<TargetCashPlan> getTargetCashPlanList(Map<String, Object> paMap);

    /**
     * 查询标的现金流支付规则list
     * 
     * @param paMap 传递参数
     * @return List<TargetCashPayRule>
     * @see
     * @since 1.0
     */
    List<TargetCashPayRule> getTargetCashPayRuleList(Map<String, Object> paMap);

    /**
     * 查询标的现金流计算规则list
     * 
     * @param paMap 传递参数
     * @return List<TargetCashCalcRule>
     * @see
     * @since 1.0
     */
    List<TargetCashCalcRule> getTargetCashCalcRuleList(Map<String, Object> paMap);

    /**
     * 查询标的业绩报酬计提计划list
     * 
     * @param paMap 传递参数
     * @return List<TargetPerfRewardAccrsche>
     * @see
     * @since 1.0
     */
    List<TargetPerfRewardAccrsche> getTargetPerfRewardAccrscheList(Map<String, Object> paMap);

    /**
     * 查询标的业绩报酬规则list
     * 
     * @param paMap 传递参数
     * @return List<TargetPerfRewardRule>
     * @see
     * @since 1.0
     */
    List<TargetPerfRewardRule> getTargetPerfRewardRuleList(Map<String, Object> paMap);

    /**
     * 查询标的业绩报酬规则list
     * 
     * @param paMap 传递参数
     * @return List<TargetPerfRewardCalRule>
     * @see
     * @since 1.0
     */
    List<TargetPerfRewardCalRule> getTargetPerfRewardCalRuleList(Map<String, Object> paMap);

    /**
     * 查询标的业绩报酬规则明细List
     * 
     * @param cashId 现金流代码
     * @param effDate 生效日期
     * @return List<TargetPerfRewardRuleDetail>
     * @see
     * @since 1.0
     */
    List<TargetPerfRewardCalRuleDetail> getTargetPerfRewardRuleDetailList(@Param("cashId") String cashId,
                                                                          @Param("effDate") Date effDate);

    /**
     * 查询标的业绩回补计算规则List
     * 
     * @param cashId 现金流代码
     * @param effDate 生效日期
     * @return List<TargetPerfRewardRuleDetail>
     * @see
     * @since 1.0
     */
    List<TargetFinClawbackCalcRule> getTargetFinCLawbackList(Map<String, Object> paMap);
    
    /**
     * 查询交易信息List
     * 
     * @param finprodId 金融产品代码
     * @param finprodType 金融产品类型
     * @param branch 分支序号
     * @return 查询结果集(List<交易信息Dto>）
     * @see
     * @since 1.0
     */
    List<TradeInfoDto> getTradeInfoDtoList(@Param("finprodId") String finprodId,
                                           @Param("finprodType") String finprodType,
                                           @Param("branch") Long branch);

    /**
     * 利率行情信息查询
     * 
     * @param selectDate 查询日期
     * @param intRateId 行情代码
     * @return 利率行情信息
     * @see
     * @since 1.0
     */
    List<MstIntRateInfo> getValIntRateInfo(@Param("selectDate") Date selectDate,
                                     @Param("intRateId") String intRateId);

    /**
     * 查询债券行情信息
     * 
     * @param marketCode 市场代码
     * @param queryDate 查询日期
     * @param valueSource 估值来源
     * @return 查询债券行情信息
     * @see
     * @since 1.0
     */
    List<BondValuationInfoDto> getBondValuationInfo(@Param("marketCode") String marketCode,
                                                    @Param("queryDate") Date queryDate,
                                                    @Param("valueSource") String valueSource);

    /**
     * 查询债券行情信息（第三方）
     * 
     * @param marketCode 市场代码
     * @param queryDate 查询日期
     * @param valuePriceType 估值市价类型
     * @return 查询债券行情信息（第三方）
     * @see
     * @since 1.0
     */
    List<BondMarketInfoDto> getBondMarketInfo(@Param("marketCode") String marketCode,
                                              @Param("queryDate") Date queryDate,
                                              @Param("valuePriceType") String valuePriceType);

    /**
     * 查询产品估值方案
     * 
     * @param happenDate 发生日
     * @param secDetailType 证券细分类
     * @param secType 证券品种
     * @param booksetId 账套代码
     * @param schemeType 方案类型
     * @param tradeMarket 交易市场
     * @param invAim 投资目的
     * @return 产品估值方案
     * @see
     * @since 1.0
     */
    List<SchemeDetailDto> getSchemeDetail(@Param("happenDate") Date happenDate,
                                          @Param("secType") String secType,
                                          @Param("booksetId") String booksetId,
                                          @Param("schemeType") String schemeType,
                                          @Param("tradeMarket") String tradeMarket,
                                          @Param("secDetailType") String secDetailType,
                                          @Param("invAim") String invAim
        );

    /**
     * 查询默认的估值方案
     * 
     * @param happenDate 发生日
     * @param isdefault 是否默认方案
     * @param booksetId 账套代码
     * @param tradeMarket 交易市场
     * @param invAim 投资目的
     * @param secDetailType 证券细分类
     * @param secType 证券品种
     * @return 默认的估值方案
     * @see
     * @since 1.0
     */
    List<SchemeDetailDto> getSchemeDetailDefault(@Param("happenDate") Date happenDate,
                                                 @Param("isdefault") String isdefault,
                                                 @Param("booksetId") String booksetId,
                                                 @Param("secType") String secType,
                                                 @Param("tradeMarket") String tradeMarket,
                                                 @Param("secDetailType") String secDetailType,
                                                 @Param("invAim") String invAim);

    /**
     * 查询产品影子定价方案
     * 
     * @param happenDate 发生日
     * @param secDetailType 证券细分类
     * @param secType 证券品种
     * @param booksetId 账套代码
     * @param schemeType 方案类型
     * @param tradeMarket 交易市场
     * @return 产品估值方案
     * @see
     * @since 1.0
     */
    List<SchemeDetailDto> getShadowPriceDetail(@Param("happenDate") Date happenDate,
                                               @Param("secType") String secType,
                                               @Param("booksetId") String booksetId,
                                               @Param("schemeType") String schemeType,
                                               @Param("tradeMarket") String tradeMarket,
                                               @Param("secDetailType") String secDetailType);
    
    /**
     * 查询默认的影子定价方案
     * 
     * @param happenDate 发生日
     * @param isdefault 是否默认方案
     * @param booksetId 账套代码
     * @param tradeMarket 交易市场
     * @param invAim 投资目的
     * @param secDetailType 证券细分类
     * @param secType 证券品种
     * @return 默认的估值方案
     * @see
     * @since 1.0
     */
    List<SchemeDetailDto> getShadowPriceDetailDefault(@Param("happenDate") Date happenDate,
                                                      @Param("isdefault") String isdefault,
                                                      @Param("booksetId") String booksetId,
                                                      @Param("secType") String secType,
                                                      @Param("tradeMarket") String tradeMarket,
                                                      @Param("secDetailType") String secDetailType,
                                                      @Param("schemeType") String schemeType);

    /**
     * 查询场外基金估值行情
     * 
     * @param queryDate 查询日期
     * @param marketCode 市场代码
     * @return 场外基金估值行情
     * @see
     * @since 1.0
     */
    List<FinProductValuationDto> getFinProValuation(@Param("queryDate") Date queryDate,
                                                    @Param("marketCode") String marketCode);

    /**
     * 场外基金估值行情(等于某类金融产品类型)
     * 
     * @param queryDate 查询日期
     * @param marketCode 市场代码
     * @param finprodType 金融产品类型
     * @return 场外基金估值行情
     * @see
     * @since 1.0
     */
    List<FinProductValuationDto> getFundValuationOutsideForWithIn(@Param("queryDate") Date queryDate,
                                                                  @Param("marketCode") String marketCode,
                                                                  @Param("finprodType") String finprodType);

    /**
     * 交易所基金估值行情
     * 
     * @param queryDate 查询日期
     * @param marketCode 市场代码
     * @param valuePriceType 估值市价类型
     * @return 交易所基金估值行情
     * @see
     * @since 1.0
     */
    List<FundValuationDto> getFundValuation(@Param("queryDate") Date queryDate,
                                            @Param("marketCode") String marketCode,
                                            @Param("valuePriceType") String valuePriceType);

    /**
     * 万份收益-基金
     * 
     * @param finprodId 标的代码
     * @param listDates 取值日期
     * @return 万分收益信息List
     * @see
     * @since 1.0
     */
    List<FinProductValuation> getValFinProductValuationList(@Param("finprodId") String finprodId,
                                                            @Param("listDates") List<Date> listDates);

    /**
     * 当前付息区间查询
     * 
     * @param happenDate 发生日
     * @param busiId 业务明细代码
     * @param cashType 现金流类型
     * @return
     * @see
     * @since 1.0
     */
    List<TargetCashPlan> getCurrentIntRange(@Param("happenDate") Date happenDate,
                                            @Param("busiId") String busiId,
                                            @Param("cashType") String cashType);

    /**
     * 影子定价查询
     * 
     * @param secId 债券代码
     * @param valueDate 估值日
     * @param valueSource 估值来源
     * @return 市价净价
     * @see
     * @since 1.0
     */
    MstBondValuationInfo getNetprice(@Param("secId") String secId, @Param("valueDate") Date valueDate,
                           @Param("valueSource") String valueSource,
                           @Param("couponSpecies") String couponSpecies);

    /**
     * 账务明细代码信息查询 <功能详细描述>
     * 
     * @param map
     * @return
     * @see
     * @since 1.0
     */
    List<BokDetailId> bookdetailInforQuery(Map<String, Object> map);

    /**
     * 最大分支序号查询
     * 
     * @param map
     * @return
     * @see
     * @since 1.0
     */
    Long queryValFinProductDealMaxBranch(Map<String, Object> map);

    /**
     * 当前计划对应分支序号查询
     * 
     * @param map
     * @return
     * @see
     * @since 1.0
     */
    Long queryValFinProductDealBranchNow(Map<String, Object> map);

    /**
     * 下一行权计划查询 <功能详细描述>
     * 
     * @param map
     * @return
     * @see
     * @since 1.0
     */
    List<FinExercisePlan> queryFinExercisePlan(Map<String, Object> map);
    
    /**
     * 截止日查询  <功能详细描述>
     * 
     * @param map
     * @return
     * @see
     * @since 1.0
     */
    List<FinCashPlanEx> queryFinCashPlan(@Param("finprodId") String finprodId,
                                         @Param("mdate") Date mdate);
    
    
    /**
     * 核算主体查询 <功能详细描述>
     * 
     * @param map
     * @return
     * @see
     * @since 1.0
     */
    FinProduct queryAcctSubject(@Param("booksetId") String booksetId,
                                         @Param("acctSubjectType") String acctSubjectType);
    
    
    /**
     * 估值方案特殊资产配置查询
     * 
     * @param map
     * @return
     * @see
     * @since 1.0
     */
    List<SchemeDetailDto> getSpecialSchemeDetail(Map<String, Object> map);
    
    /**
     * 质押品信息查询<功能详细描述>
     * 
     * @param map
     * @return
     * @see
     * @since 1.0
     */
    List<PledgeInfoDto> getPledgeInfo(@Param("finprodId") String finprodId,
                                         @Param("finprodType") String finprodType);
    
    /**
     * 万分收益-计划类货币型
     * 
     * @param map
     * @return
     * @see
     * @since 1.0
     */
    ValFinProductValuationDto queryFinProductValuation(Map<String, Object> map);

    /**
     * 汇率行情查询
     *
     * @param map
     * @return
     * @see
     * @since 1.0
     */
    List<CurrencyMarketDto> getExchangeRate(Map<String, Object> map);
    
    /**
     * 询汇率行情信息
     *
     * @param map
     * @return
     * @see
     * @since 1.0
     */
    MstValIrs getCurrencyMarket(Map<String, Object> map);
    
    /**
     * 外汇行情查询
     *
     * @param map
     * @return
     * @see
     * @since 1.0
     */
    MstValFx getForeignMarket(Map<String, Object> map);
    
    /**
     * 股票行情查询
     *
     * @param map
     * @return
     * @see
     * @since 1.0
     */
    MstSharePrice getMstSharePrice(Map<String, Object> map);
    /**
     * 流通性受限股票行情查询
     *
     * @param map
     * @return
     * @see
     * @since 1.0
     */
    MstShareLqratio getMstShareLqratio(Map<String, Object> map);


    List<FinChnlInfoDto> getChnlInfo(@Param("finprodId") String finprodId);
    
    /**
     * 市价查询-场外期权
     * @param map
     * @return
     * @see
     * @since 1.0
     */
    List<MstValIrs> getFundMarketPriceForOtc(Map<String, Object> map);

    /**
     * 查询跑批日当天还本付息交易
     * @param map
     * @return
     */
    Map<String, Object> selectHBFXData(Map<String, Object> map);

    /**
     * 资产是否还本不带息
     * @param finprodId
     * @return
     */
    String getIsPayInt(@Param("finprodId") String finprodId);
	
	TrdFinProductBarterDto selectTrdFinProductBarter(Map<String, Object> map);
    /**
     * 查询标的金融产品类型（投管分类）
     */
    String getfinprodType2(@Param("finprodId") String finprodId);

    /**
     * 估值查詢-利率互換
     * @author 殷亚
     * @date 2022/4/28 15:42
     * @param map
     * @return java.util.List<com.joyintech.tams.common.base.entity.MstValIrs>
     * @see
     * @version 1.0
     */
    List<MstValIrs> getMarketPriceForIrs(Map<String, Object> map);
}
