/*
 * 文件名：EtrBondFirstBiddingMapper.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：yangjm
 * 修改时间：2019年5月6日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.business.asset.bond.dao;

import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.tams.common.base.entity.SysUploadFileInfo;
import com.joyintech.tams.business.asset.bond.dto.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.business.asset.dto.FinPayInterestStdInfoDto;
import com.joyintech.tams.component.business.core.dto.WflValidInputParameter;
import com.joyintech.tams.invest.asset.common.dto.FinAssetRateAdjDto;
import com.joyintech.tams.invest.common.asset.dto.AssetCommonInfoDto;
import com.joyintech.tams.invest.common.dto.common.FinAssetIntInfo;
import com.joyintech.tams.invest.common.dto.period.trdbondduration.TrdBondDurationDto;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *  场内交易指令Mapper
 * 〈功能详细描述〉
 *
 * @author wangyang1
 * @version 1.0 2022年4月13日
 * @see FinBondAssetMapper
 * @since 1.0
 */
@Repository
public interface FinBondAssetMapper {

	List<FinBondAssetResponse> queryForPage(Map<String, Object> pageParam);

	FinBondAssetSaveRequest getBasicInfo(Map<String, Object> param);

	// 债券新建新增快捷录入功能 add by renping 20191010 end
	int selectCountByfinprodMarketId(@Param("finprodMarketId") String finprodMarketId);

	/**
	 * 获取主体评级机构数据
	 * <功能详细描述>
	 * @param param
	 * @return
	 * @see
	 * @since 1.0
	 */
	List<FinBondAssetResponse> getZTPJ(Map<String, Object> param);

	/**
	 * 获取债项评级机构数据
	 * <功能详细描述>
	 * @param param
	 * @return
	 * @see
	 * @since 1.0
	 */
	List<FinBondAssetResponse> getZXPJ(Map<String, Object> param);

	/**
	 * 获取新建界面的以及市场投标债券代码联想下拉框
	 *
	 * @author wangguiqi
	 * @param param
	 * @return
	 * @see
	 * @since 1.0
	 */
	List<SelectOptionDto> getfinprodIdForAdd(Map<String, Object> param);

	int isHqTrade(@Param("finprodId") String finprodId);

	int selectCountByfinprodId(@Param("finprodId") String finprodId);

	int selectCountByOFinprodId(@Param("oFinprodId") String oFinprodId,@Param("finprodId") String finprodId);

	/**
	 * 根据资产代码查询是否是上市标的
	 *
	 * @param finprodId
	 * @return 是否是上市标的，大于0则为是
	 * @see
	 * @since 1.0
	 */
	int selectIsIssueAsset(@Param("finprodId") String finprodId);
	/**
	 * 查询标的现金流计划list
	 * @author sjj
	 * @param finprodId
	 * @return FinCashPlan
	 * @see
	 * @since 1.0
	 */
	List<FinCashPlan> getFinCashPlanList(@Param("finprodId") String finprodId);

	/**
	 * 查询标的现金流支付规则list
	 * @author sjj
	 * @param finprodId
	 * @return FinCashPlan
	 * @see
	 * @since 1.0
	 */
	List<FinCashPayRule> getFinCashPayRuleList(@Param("finprodId") String finprodId);

	/**
	 * 查询标的现金流计算规则list
	 * @author sjj
	 * @param finprodId
	 * @return FinCashPlan
	 * @see
	 * @since 1.0
	 */
	List<FinCashCalcRule> getTargetCashCalcRuleList(String finprodId);
	/**
	 * 查询标的现金流信息list
	 * @author sjj
	 * @param finprodId
	 * @return FinCashPlan
	 * @see
	 * @since 1.0
	 */
	List<FinCashInfo> selectFinCashInfo(String finprodId);

	/**
	 * 查询标的利率信息list
	 * @author sjj
	 * @param finprodId
	 * @return FinCashPlan
	 * @see
	 * @since 1.0
	 */
	List<FinRateInfo> getFinRateInfoList(String finprodId);

	/**
	 * 查询行权计划list
	 * @author sjj
	 * @param finprodId
	 * @return FinCashPlan
	 * @see
	 * @since 1.0
	 */
	List<FinExercisePlan> selectFinExercisePlanByFinprodId(@Param("finprodId") String finprodId);

	int deletePaTrdFinDealTotalData(Map<String, Object> paramMap);

	int deleteTrdFinProductDealTotalData(Map<String, Object> paramMap);

	int deleteTrdProductDeal(Map<String, Object> paramMap);

	int deleteTrdProductDealAdd(Map<String, Object> paramMap);

	int deleteStlPayOrdersData(Map<String, Object> paramMap);

	List<TrdBondDurationDto> findEffectiveData(Map<String, Object> paramMap);

	List<FinAssetIntInfo> findAssetIntInfo(Map<String, Object> params);

	/**
	 * 获取资产id联想文本框
	 * <功能详细描述>
	 * @param param
	 * @return
	 * @see
	 * @since 1.0
	 */
	List<SelectOptionDto> getAssetId(Map<String, Object> param);

	List<SelectOptionDto> getCrossMarketCode(Map<String, Object> param);

	BondFinInterestFactorDto getFinInterestFactorByProductId(@Param("finprodId")String finprodId, @Param("vdate") String vdate, @Param("cashType") String cashType);

	/**
	 * 操作记录查询
	 *
	 * @param pageParam
	 * @return List<SearchInfoOperateListResponse>
	 * @throws Exception Exception
	 * @see
	 * @since 1.0
	 */
	List<SearchInfoOperateListResponse> getSearchTabInfoQueryOperate(Map<String, Object> pageParam);

	List<FinAssetRateAdjResponse> getfinAssetRateAdjByProductId(Map<String, Object> pageParam);


	List<BondFinCashPlanDto> selectInvScheduleList(Map<String, Object> map);

	List<BondFinExercisePlanDto> selectFinExercisePlanList(Map<String, Object> map);

	InterfCustomerInfoDto selectInterfCustomerInfo(@Param("issuer") String issuer);

	List<SelectOptionDto>  getCustomer(Map<String, Object> map);

	int deleteFinCashInfo(@Param("finprodId") String finprodId);

	int deleteFinCashCalcRule(@Param("finprodId") String finprodId);

	int deleteFinCashPayRule(@Param("finprodId") String finprodId);

	int deleteFinCashPlan(@Param("finprodId") String finprodId);

	int deleteFinRateInfo(@Param("finprodId") String finprodId);

	int deleteFinExercisePlan(@Param("finprodId") String finprodId);
	/**
	 * 根据cashId删除计算规则
	 * <一句话功能简述>
	 * <功能详细描述>
	 * @param key
	 * @return
	 * @see
	 * @since 1.0
	 */
	int deleteCashCalcRuleByCashId(FinCashCalcRuleKey key);

	List<FinAssetRateAdjResponse> selectInitAssetRateAdj(Map<String, Object> param);

	int insertFileSelective(SysUploadFileInfo var1);

	int updateFileByPrimaryKey(SysUploadFileInfo var1);

	FinPayInterestStdInfoDto getFinPayInterestStdInfoByProductId(@Param("finprodId") String finprodId,
																 @Param("vdate") String vdate, @Param("cashType") String cashType,
																 @Param("finprodType") String finprodType);
	/**根据金融产品代码查询是否还本不带息
	 **/
	String getRepayWithoutIntByProductId(@Param("finprodId") String finprodId,
										 @Param("cashType") String cashType);

	List<FinExercisePlan> getfinExercisePlanByProductId(Map<String, Object> param);

	AssetCommonInfoDto getMdateByFinproId(@Param("finprodId")String finprodId);

	AssetCommonInfoDto getAssetInfoToRateAdj(Map<String, Object> param);

	int deleteFileByBussinessId(@Param("finprodId") String finprodId);

	int isTransInAsset(@Param("finprodId") String finProdId);

	Date getMaxTradeDate(@Param("finprodId") String finProdId);

	List<String> getBusinessData(WflValidInputParameter parameter);

    List<SelectOptionDto> getFinProdMarketIdList(Map<String, Object> param);

	FinProductTypeConfig getWindType(Map<String, Object> paramCinfig);

	int deleteCfetsOprHis(@Param("entrustId") String entrustId);

	List<FinProductIssue> selectFinProductIssue(@Param("finprodMarketId") String finprodMarketId);

	int isTransInAssetTwo(@Param("finprodId") String finprodId);
}
