/*
 * 文件名：TrdBondInvestTradeMapper.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：Zane.Y
 * 修改时间：2018年9月7日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.service.trade.bond.dao;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.joyintech.service.trade.bond.dto.TrdBondInvestTradePdfDto;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.invest.common.dto.cstp.EtrEntrustExt;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.service.trade.bond.dto.SecCpriceInfoDto;
import com.joyintech.service.trade.bond.dto.TrdBondInvestTradeDto;
import com.joyintech.tams.invest.common.trade.dto.FinCashPlanDto;
import com.joyintech.tams.invest.common.trade.dto.SecInfoModelDto;

/**
 * 债券交易Mapper
 *
 * @author Zane.Y
 * @version 1.0 2018年9月7日
 * @see TrdBondInvestTradeMapper
 * @since 1.0
 */
@Repository
public interface TrdBondInvestTradeMapper {

    /**
     * 检索债券交易列表
     *
     * @param paraMap 参数
     * @return 债券交易列表
     * @see
     * @since 1.0
     */
    List<TrdBondInvestTradeDto> selectDeal(Map<String, Object> paraMap);

    List<SuggestDto> getSecManageAcctId(Map<String, Object> param);
    
    
    List<SuggestDto> getChannel(Map<String, Object> param);

    List<String> getChannelInfo(Map<String, Object> param);
    
    BigDecimal getLastNetPrice(Map<String, Object> param);

    BigDecimal getUnitPrice(Map<String, Object> param);

    List<SelectOptionDto> getFinprodId(Map<String, Object> param);
    //    资产推荐方 新增 zhuyongkang 20191023 end
    /**
     * 构建推荐方下拉框
     *
     * @param param 参数
     * @return 下拉框查询结果
     * @see
     * @since 1.0
     */
    List<SelectOptionDto> getAssetRecommandOrg(Map<String, Object> param);
    //    资产推荐方 新增 zhuyongkang 20191023 end
    /**
     * 查询债券未来行权计划的条数
     *
     * @param param 参数
     * @return 债券未来行权计划的条数
     * @see
     * @since 1.0
     */
    int selectBondHasEexrPlanCount(Map<String, Object> param);

    /**
     * 查询债券行权计划中某一天是否是行权日
     *
     * @param param 参数
     * @return 结果
     * @see
     * @since 1.0
     */
    int selectBondEexrPlanCountForOneDay(Map<String, Object> param);

    TrdBondInvestTradeDto getTrdBondInvestTradeDto(Map<String, Object> params);

    SecInfoModelDto getSecInfoModel(Map<String, Object> param);

    List<FinCashPlanDto> getFinCashPlan(Map<String, Object> param);

    /**
     * a.当前债券面额取得（根据还本计划计算）
     *
     * @param param 参数
     * @return a.当前债券面额取得（根据还本计划计算）
     * @see
     * @since 1.0
     */
    BigDecimal getCurSecAmt(Map<String, Object> param);

    /**
     * 查询债券利率重置信息
     *
     * @param param 参数
     * @return 债券利率重置信息
     * @see
     * @since 1.0
     */
    List<FinCashCalcRule> selectBondsIntResetList(Map<String, Object> param);

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
	
    List<SelectOptionDto> selectPortfolioId(Map<String, Object> param);	
    
    /**
     * 取得证券托管户的登记托管机构
     * 
     * @param param Map<String, Object>
     * @return 证券托管户的登记托管机构
     * @see
     * @since 1.0
     */
    String getRegistOrg(Map<String, Object> param);

    /**
     * 查询债券cstp交易下拉框
     * @param param 输入参数
     * @return
     */
    List<SelectOptionDto> querySecCstpInfoList(Map<String, Object> param);
    /**
     * 根据成交编号查询cstp交易信息
     * @param tradeId 成交编号
     * @return cstp交易信息
     */
    InterfTradSecdeal querySecCstpInfoByNo(@Param("fedealno") String fedealno,
                                           @Param("entrustId") String entrustId,
                                           @Param("preTradeId") String preTradeId,
                                           @Param("isCstpSure") String isCstpSure);
    /**
     * 根据市场代码和资产分类查询基本信息
     * @param finprodMarketId 市场代码
     * @param finprodType2 资产分类
     * @return 基本信息
     */
    FinProduct queryFinProductInfoByMark(@Param("finprodMarketId") String finprodMarketId, @Param("finprodType2") String finprodType2);
    /**
     * 根据交易对手名称查询交易对手信息
     *
     * @param counterName 交易对手名称
     * @return 交易对手信息
     * @see
     * @since 1.0
     */
    MstCounterParty queryMstCounterPartyByCounterName(@Param("counterName") String counterName);

    /**
     * 根据证券账户查询有用户权限的组合
     *
     * @param loginUserName 登录用户
     * @param secAcctId 证券账户
     * @return
     * @see
     * @since 1.0
     */
    List<String> queryPortfolioIdBySecAcctId(@Param("loginUserName") String loginUserName, @Param("secAcctId") String secAcctId);

    BigDecimal getTongDaoCash(Map<String, Object> param);

    List<TrdProductDeal> getTrdProductDealByFTradeId(@Param("fTradeId") String fTradeId,@Param("finprodType2") String finprodType2);

    List<WflTrdProductDeal>  getWftTrdProductDealByFTradeId(@Param("fTradeId") String fTradeId,@Param("finprodType2") String finprodType2,@Param("workflowId") String workflowId);


    /**
     * 根据资产代码获取相关交易信息列表
     * @param map
     * @return
     */
    List<TrdProductDeal> getTradeListByFinprodId(Map<String, Object> map);


    /**
     * 根据资产代码、起息日、分支序号获取计息基础
     * @param map
     * @return
     */
  FinCashCalcRule getBasisByFinprodId(Map<String, Object> map);

    /**
     * 根据资产代码、查询日、分支序号获取票面利率
     * @param map
     * @return
     */
    FinCashCalcRule getYieldByFinprodId(Map<String, Object> map);


    /**
     * 根据资产代码、查询日、行权方式、资产类型查询下一行权日
     * @param map
     * @return
     */
    List<FinExercisePlan> getExerciseDate(Map<String, Object> map);

    /**
     * 根据资产代码查询昨日指标信息的持仓数量和综合成本收益率
     * @param map
     * @return
     */
     AssetTargetDb  getYesterdayTarget(Map<String, Object> map);

    /**
     * 根据资产代码查询昨日指标信息的持仓数量和综合成本收益率
     * @param map
     * @return
     */
    List<AssetTargetDb>  getAllYesterdayTarget(Map<String, Object> map);

    /**
     * 查询当前时间的持仓市值和应计利息汇总值
     *资产类型为债券，查询日期
     * @paramFAIRE_PRICE
     * @return
     */
    AssetTargetDb getSumPrice(Map<String, Object> map);
    
    /**
     * 根据交易对手id获取内部交易对手的收益类型（吉林银行产品化需求）
     * @param counterId 交易对手id
     * @return
     * @see
     * @since 1.0 malinlin 2019.12.2
     */
    String getProdProfitType(@Param("counterId") String counterId);
    
    /**
     * 查询交易页面债券的市值净价和摊销净价（吉林银行产品化需求）
     * @param params
     * @return
     * @see
     * @since 1.0 malinlin 2019.12.2
     */
    SecCpriceInfoDto getCprice(Map<String, Object> param);
    /**
     * 查询交易对手是否为净值高频或者货币
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    String getCounterId(Map<String, Object> param);
    
  //货币型产品投资目的默认显示摊余成本 add by liupeilin 2020-01-15 begin
    /**
     * 查询产品类型
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    String getProfitType(@Param("portfolioId") String portfolioId);
    //货币型产品投资目的默认显示摊余成本 add by liupeilin 2020-01-15 end
    
    String selectCeTradeId(Map<String, Object> param);

    String getReverseTradeDtoInvAim(@Param("tradeId") String tradeId);

    /**
     * 查询买入交易后续的卖出交易
     * @param tradeId
     * @return
     * @see
     * @since 1.0
     */
    int selectTradeT02(@Param("tradeId") String tradeId);

    /* add by msk 新增查询通道划款交易 20201111 TISHKBS-1038 start */
    /**
     * 查询通道划款交易
     * @param map
     * @return
     * @see
     * @since 1.0
     */
    int selectchlTrade(Map<String,Object> map);
    /* add by msk 新增查询通道划款交易 20201111 TISHKBS-1038 end */

    String getType7(TrdBondInvestTradeDto dto);
	
	InterfTradSecdeal selectInterfTradSecdeal(@Param("fTradeId") String fTradeId);
	
	String selectCounterId(@Param("cpPartyId") String cpPartyId);
	List<TrdTradeFeeDetail> getAllFeeByFTradeId(Map<String, Object> map);

	EtrEntrustExt queryRepoEntrustInfo(@Param("oritradeId") String oritradeId);

    EtrEntrustExt queryBondEntrustInfo(@Param("oritradeId") String oritradeId);
	
	BigDecimal selectMatchNum(@Param("fedealno") String fedealno,@Param("entrustId") String entrustId);
	 List<PtlPortfolio> queryPortfolioIdByFedealno(@Param("loginUserName") String loginUserName, @Param("fedealno") String fedealno);

    /**
     * 查询导出pdf数据 add by zengchengfa
     * @param param
     * @return
     */
    List<TrdBondInvestTradePdfDto> selectExportPdf(Map<String, Object> param);

    BigDecimal getResidualFace(Map<String,Object> param);
}
