/*
 * 文件名：DateCalcMapper.java 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved. 描述：理财资管管理系统V2.0 修改人：何阳阳 修改时间：2018年9月21日 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.common.trade.dao;

import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.invest.common.dto.common.PledgeBondInfoDto;
import com.joyintech.tams.invest.common.trade.dto.FinProductCredit;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 业务共通组件.交易占用/释放授信共通组件的Mapper
 *
 * @author sjj
 * @version 1.0 2019年1月14日
 * @see CreditLinesDealsMapper
 * @since 1.0
 */
@Repository
public interface CreditLinesDealsMapper {

	int isNbDeal(@Param("counterId") String counterId, @Param("counterType") String counterType);

	List<CreditLines> getCeLinesInfoforasset(Map<String,Object> model);

	List<CreditLines> getCeLinesInfoforOrg01(Map<String,Object> model);
	
	List<CreditLines> getCeLinesInfoforOrg02(Map<String,Object> model);
	
	RiskWeight getRsk(@Param("finprodId")String finprodId, @Param("tradedate")Date tradedate);

	BigDecimal getFaceValue(Object model);

	int updateLinesByPrimaryKeySelective(CreditLines creditLines);
	
	int insertLinesDeal(CreditLinesDeal creditLinesDeal);

	CreditLines isOccupyCreBottom(String finProdId);

	String getTypeByFinprodId(@Param("finprodId") String finprodId);

	int deleteLinesDealByTradeid(@Param("seqno") String seqno,@Param("tradeId") String tradeId);

//	List<CreditLines> getCeLinesInfoforOrg01Rep(@Param("ps") String ps, @Param("counterId") String counterId, @Param("tradeDate")Date tradeDate,@Param("repType")String repType);
	List<CreditLines> getCeLinesInfoforOrg01Rep(Map<String,Object> model);
	
	List<CreditLines> getCeLinesInfoforOrg01Lend(Map<String,Object> model);

	String getPartyIdBycounterId(@Param("counterId")String counterId, @Param("counterType") String counterType);

	String getPartyIdByFinprodId(@Param("finprodId") String finprodId);

	BigDecimal getPledgeNum(@Param("finprodId") String finprodId);

	BigDecimal getPledgeAmt(TrdProductDeal model);

	FinProductCredit getAssetInfoByFinId(@Param("finprodId")String finprodId, @Param("tradedate")Date tradedate, @Param("execmode")String execmode);
	String getMstName(@Param("creditMstId") String creditMstId);
	BigDecimal getPrehapamtForPingan(Map<String, Object> map);

	BigDecimal getPierceTradeAmt(Map<String, Object> map);

	List<CreditLines> getCeLinesInfoforVariety(Map<String, Object> map);

	//add 20191019  股票取标的对应的公司名称
	String getCounterIdbyFinprodId(@Param("finprodId") String finprodId);
	
	int selectCreditDealCount(@Param("seqno") String seqno,@Param("tradeId") String tradeId);
	
	//撤销时根据交易编号查找授信交易
	List<CreditLinesDeal> selectCreditDealByTradeId(@Param("tradeId") String tradeId);

	//到期根据交易编号查找首期授信交易
	List<CreditLinesDeal> selectCreditOriginDealByTradeId(@Param("tradeId") String tradeId);
	
	CreditLines selectCeLinesInfoBySeqno(@Param("seqno") String seqno);
	
	RiskWeight getRskByAssetType(Map<String, Object> map);
	
	RiskWeight getRskBySingleAssetType(@Param("type1")String type1 , @Param("tradedate")Date tradedate);
	
	//回购存续期根据交易编号待处理额度
	String getFinprodIdByTradeId(@Param("tradeId") String tradeId);
	
	/**
	 * 根据前置交易标号查交易编号
	 * @param bTradeId
	 * @return
	 */
	List<String> selectTradeidByBTtradid(@Param("bTradeId") String bTradeId);

	List<CreditLinesPierce> getPierceListByTradeDate(@Param("tradeDate") String tradeDate);

	int deleteLinesSecPositionBySeqno(@Param("seqno")String seqno);

	int insertLinesSecPosition(CreditLinesSecPosition creditLinesSecPosition);

	/**
	 * 查询日之前最大日期的授信明细
	 * @param param
	 * @return
	 */
	List<CreditLinesDeal> selectCreditLinesDeal(Map<String, Object> param);

	List<CreditLinesDeal> selectCreditLinesDealAdd(Map<String, Object> param);

	List<CreditLinesDeal> selectCreditLinesDealAddNew(Map<String, Object> param);

	BigDecimal selectCreditLinesHap(Map<String, Object> param);

	/**
	 * 判断是否是空档期
	 * @param seqno
	 * @param tradeId
	 * @return
	 */
	int isKDQ(@Param("creditmstid") String creditmstid, @Param("customerid") String customerid, @Param("dealdate") Date dealdate, @Param("orgno") String orgno);

	/**
	 * 查询应占机构
	 * @param model
	 * @return
	 */
	String getCustomerid(Map<String,Object> model);

	List<CreditLines> getCeLinesInfoforassetPierce(Map<String,Object> model);
    List<CreditMstInfo> selectCreditMstInfo(String seqno);
	BigDecimal getLastPrice(@Param("finprodId")String finprodId,@Param("sysDate")Date sysDate);

	FinProductType selectByPrimaryKey(FinProductTypeKey key);

	List<PledgeBondInfoDto> findPledgeBondInfo(Map<String, Object> param);

	List<BidOrder> selectBidOrderByFinprodIds(Map<String, Object> param);

	List<BidOrderBidPrice> selectBidOrderBidPriceByFinprodIds(Map<String, Object> param);
}
