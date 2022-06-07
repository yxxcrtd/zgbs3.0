/*
 * 文件名：BondAssetValuationService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：admin
 * 修改时间：2020年4月17日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.common.ptlrealtimevaluation.assetvaluation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joyintech.calculation.CalculatorFactory;
import com.joyintech.calculation.bonds.PaperIrReset;
import com.joyintech.calculation.bonds.SecIntSchedule;
import com.joyintech.calculation.bonds.constant.AllocationType;
import com.joyintech.calculation.bonds.constant.Basis;
import com.joyintech.calculation.bonds.constant.IntFrequency;
import com.joyintech.calculation.bonds.constant.IntRateType;
import com.joyintech.calculation.bonds.constant.NextExeCalMethod;
import com.joyintech.calculation.bonds.constant.SecIntSchCalMethod;
import com.joyintech.calculation.bonds.constant.Variety;
import com.joyintech.calculation.shell.AccurIntForBookCalculator;
import com.joyintech.calculation.shell.SecDscYieldCalculator;
import com.joyintech.calculation.shell.param.AccurIntCalInParameter;
import com.joyintech.calculation.shell.param.AccurIntCalOutParameter;
import com.joyintech.calculation.shell.param.SecDscYieldInParameter;
import com.joyintech.calculation.shell.param.SecDscYieldOutParameter;
import com.joyintech.calculation.shell.param.SecInfoModel;
import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.calculation.util.BasicUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.BokTradeOrder;
import com.joyintech.tams.common.base.entity.FinCashCalcRule;
import com.joyintech.tams.common.base.entity.FinCashPayRule;
import com.joyintech.tams.common.base.entity.FinExercisePlan;
import com.joyintech.tams.common.base.entity.FinProduct;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.dto.mock.asset.MockAssetInfoDto;
import com.joyintech.tams.invest.common.dto.mock.asset.MockFinCashPlanDto;
import com.joyintech.tams.invest.common.dto.mock.assetvaluation.MockCalcAssetValuationResult;
import com.joyintech.tams.invest.common.dto.mock.assetvaluation.MockCashChangingKey;
import com.joyintech.tams.invest.common.dto.mock.assetvaluation.MockT1BalanceResult;
import com.joyintech.tams.invest.common.dto.mock.basicdata.MockMstExchargeQuoteDto;
import com.joyintech.tams.invest.common.dto.mock.basicdata.MockQuotationInfoDto;
import com.joyintech.tams.invest.common.dto.mock.portfolio.MockPtlPortfolioDto;
import com.joyintech.tams.invest.common.dto.mock.portfolio.MockPtlSecValuationDto;
import com.joyintech.tams.invest.common.dto.mock.portfolio.MockSingleAssetChanging;
import com.joyintech.tams.invest.common.dto.mock.trade.MockTrdProductDealDto;
import com.joyintech.tams.invest.common.ptlrealtimevaluation.RealTimeValCommonUtil;

/**
 *
 * 债券资产估值 〈功能详细描述〉
 *
 * @author admin
 * @version 1.0 2020年4月17日
 * @see BondAssetValuationService
 * @since 1.0
 */
public class BondAssetValuationService extends BaseAssetValuationService {

	/**
	 * 债券估值方法 <功能详细描述>
	 *
	 * @param 资产基本信息
	 * @param 投资目的
	 * @param 组合基本信息dto
	 * @param 组合资产估值
	 * @param 资产端交易列表
	 * @param 资产端t_N交易列表
	 * @param 测算开始日
	 * @param 测算结束日
	 * @param 基准日期
	 * @param 汇率行情列表
	 * @param t_N现金变化_内部
	 * @param t_N单资产变化_内部
	 * @param 摊销重算map
	 * @param 行情map
	 * @param 现金流计划表map
	 * @param T_N计划
	 * @param 资产端交易map
	 * @param 交易排序规则
	 * @return MockCalcAssetValuationResult
	 * @see
	 * @since 1.0
	 */
	public static MockCalcAssetValuationResult assetValuationProcess(MockAssetInfoDto 资产基本信息, String 投资目的,
																	 MockPtlPortfolioDto 组合基本信息dto, MockPtlSecValuationDto 组合资产估值, List<MockTrdProductDealDto> 资产端交易列表,
																	 List<MockTrdProductDealDto> 资产端t_N交易列表, String 测算开始日, String 测算结束日, String 基准日期,
																	 List<MockMstExchargeQuoteDto> 汇率行情列表, Map<MockCashChangingKey, BigDecimal> t_N现金变化_内部,
																	 Map<String, MockSingleAssetChanging> t_N单资产变化_内部, Map<Date, Boolean> 摊销重算map,
																	 Map<String, MockQuotationInfoDto> 行情map, Map<String, List<MockFinCashPlanDto>> 现金流计划表map,
																	 List<MockFinCashPlanDto> T_N计划, Map<String, List<MockTrdProductDealDto>> 资产端交易map,
																	 List<BokTradeOrder> 交易排序规则) {

		MockCalcAssetValuationResult result = new MockCalcAssetValuationResult();
		// 定义 变量.债券T+1成本处理方式 = 01：交割日处理
		String 债券T_1成本处理方式 = RealTimeValCommonUtil.成本处理方式_交割日处理;

/*		// 交易所债券不处理T+N 都在交易日处理 （宁波个性）
		if (StringUtil.equals(DictCodeListExt1.交易场所_WIND_上交所.getValue(), 资产基本信息.getFinProduct().getTradeMarket())
				|| StringUtil.equals(DictCodeListExt1.交易场所_WIND_深交所.getValue(),
				资产基本信息.getFinProduct().getTradeMarket())
				|| StringUtil.equals(DictCodeListExt1.估值交易市场_上交所.getValue(), 资产基本信息.getFinProduct().getTradeMarket())
				|| StringUtil.equals(DictCodeListExt1.估值交易市场_深交所.getValue(), 资产基本信息.getFinProduct().getTradeMarket())) {
		}*/
//		else {
			计划处理T_N(资产端t_N交易列表, T_N计划, 资产基本信息, 组合资产估值, 投资目的);

			交易处理T_N(资产端t_N交易列表, 基准日期, 资产基本信息, t_N单资产变化_内部, t_N现金变化_内部, 债券T_1成本处理方式, 组合资产估值, 交易排序规则);
//		}
//		BaseAssetValuationService.
		String 测算日期 = 测算开始日;

		while (DateUtil.compareDate(测算日期, 测算结束日) <= 0) {
			try{
				组合资产估值.setCdate(DateUtil.parse(测算日期));
				资产变化处理T_N(t_N单资产变化_内部, 测算日期, 组合资产估值);
				List<MockTrdProductDealDto> 存续期交易列表 = 存续期处理(现金流计划表map, 测算日期, 资产基本信息, 组合资产估值, 投资目的);
				交易处理(存续期交易列表, 资产端交易map.get(测算日期), 测算日期, 组合资产估值, 投资目的, 基准日期, 资产基本信息, t_N单资产变化_内部, t_N现金变化_内部, 摊销重算map,
						交易排序规则);
				BigDecimal 当日利息 = BigDecimal.ZERO;
				当日利息 = 计提(资产基本信息, 投资目的, 测算日期, 组合资产估值, 当日利息);
				摊销(资产基本信息, 投资目的, 测算日期, 组合资产估值, 摊销重算map, 当日利息);
				公允价值变动(资产基本信息, 投资目的, 测算日期, 组合资产估值, 行情map);
//				资产价值计算(组合资产估值, 组合基本信息dto.getCcy(), 汇率行情列表);
				//add by xr  贴现债需特殊处理，因全价已包含利息，所以资产价值需加利息金额
				资产价值计算2(组合资产估值, 组合基本信息dto.getCcy(), 汇率行情列表,投资目的,资产基本信息);
				// 组合资产估值
				MockPtlSecValuationDto 组合资产估值_处理后 = new MockPtlSecValuationDto();
				BeanUtil.copyPropertiesIgnoreNull(组合资产估值_处理后, 组合资产估值);
				if (result.getMockPtlSecValuationDtoList() == null) {
					result.setMockPtlSecValuationDtoList(new ArrayList<>());
				}
				result.getMockPtlSecValuationDtoList().add(组合资产估值_处理后);
				测算日期 = DateUtil.addDay(测算日期, 1);
			}catch (Exception e){
				getLogger().error(测算日期+"债券"+组合资产估值.getFinprodId()+"估值测算报错==========",e);
				return result;
			}
		}
		return result;
	}

	/**
	 * 债券估值 计提 <功能详细描述>
	 *
	 * @param 资产基本信息
	 * @param 投资目的
	 * @param 测算日期
	 * @param 组合资产估值
	 * @param 当日利息
	 * @see
	 * @since 1.0
	 */
	private static BigDecimal 计提(MockAssetInfoDto 资产基本信息, String 投资目的, String 测算日期, MockPtlSecValuationDto 组合资产估值,
						   BigDecimal 当日利息) {
		// 计提数量为空时 不再执行计提
		if(组合资产估值.getAccrualAmt()==null||BigDecimal.ZERO.compareTo(组合资产估值.getAccrualAmt())==0
				||DateUtil.compareDate(资产基本信息.getFinProduct().getMdate(),DateUtil.parse(测算日期))<0){
			return BigDecimal.ZERO;
		}
		if (!(StringUtil.equals(资产基本信息.getFinProduct().getCouponSpecies(), DictCodeList.息票品种_零息.getValue())
				|| StringUtil.equals(资产基本信息.getFinProduct().getCouponSpecies(), DictCodeList.息票品种_贴现.getValue()))
				|| StringUtil.equals(投资目的, DictCodeListExt1.投资目的_以出售该金融资产为目的.getValue())) {
			BigDecimal 单位面值 = calc债券百元面值(资产基本信息.getMockFinCashPlanDtoList(), DateUtil.parse(测算日期));

			List<MockFinCashPlanDto> 付息列表 = new ArrayList<MockFinCashPlanDto>();
			for (MockFinCashPlanDto mockFinCashPlanDto : 资产基本信息.getMockFinCashPlanDtoList()) {
				if (mockFinCashPlanDto == null || mockFinCashPlanDto.getFinCashPlan() == null)
					continue;
				if (StringUtil.equals(mockFinCashPlanDto.getFinCashPlan().getCashType(),
						DictCodeList.现金流类型_中间收息.getValue())
						|| StringUtil.equals(mockFinCashPlanDto.getFinCashPlan().getCashType(),
						DictCodeList.现金流类型_到期收息.getValue())) {
					付息列表.add(mockFinCashPlanDto);
				}
			}

			BigDecimal 百元利息 = calc债券利息(DateUtil.parse(测算日期), 资产基本信息.getFinProduct(), 付息列表,
					资产基本信息.getFinCashCalcRuleList(), 资产基本信息.getFinCashPayRuleList(), 单位面值);
			int 舍位参数 = 12;
/*			if (StringUtil.equals(DictCodeListExt1.交易场所_WIND_上交所.getValue(), 资产基本信息.getFinProduct().getTradeMarket())
					|| StringUtil.equals(DictCodeListExt1.交易场所_WIND_深交所.getValue(),
					资产基本信息.getFinProduct().getTradeMarket())
					|| StringUtil.equals(DictCodeListExt1.估值交易市场_上交所.getValue(), 资产基本信息.getFinProduct().getTradeMarket())
					|| StringUtil.equals(DictCodeListExt1.估值交易市场_深交所.getValue(), 资产基本信息.getFinProduct().getTradeMarket())) {
				舍位参数 = 8;
			}*/
			百元利息 = ArithmeticUtil.divide(ArithmeticUtil.multiply(百元利息, 单位面值), new BigDecimal(100), 舍位参数);
			BigDecimal 利息余额 = ArithmeticUtil.multiply(RealTimeValCommonUtil.AMOUNT_SCALE, RoundingMode.HALF_UP,
					组合资产估值.getAccrualAmt(), 百元利息);
			当日利息 = ArithmeticUtil.doSub(利息余额, 组合资产估值.getAccrualInt());

			组合资产估值.setAccrualInt(ArithmeticUtil.doAdd(组合资产估值.getAccrualInt(), 当日利息));// 计提利息
			组合资产估值.setTdyIntincexp(ArithmeticUtil.doAdd(组合资产估值.getTdyIntincexp(), 当日利息));// 当日应计利息
		}
		return 当日利息;
	}

	/**
	 * 债券估值 摊销 <功能详细描述>
	 *
	 * @param 资产基本信息
	 * @param 投资目的
	 * @param 测算日期
	 * @param 组合资产估值
	 * @param 摊销重算map
	 * @param 当日利息
	 * @see
	 * @since 1.0
	 */
	private static void 摊销(MockAssetInfoDto 资产基本信息, String 投资目的, String 测算日期, MockPtlSecValuationDto 组合资产估值,
						   Map<Date, Boolean> 摊销重算map, BigDecimal 当日利息) {
		// 成本为0时  不进行摊销
		if(组合资产估值.getAccrualCost()==null||BigDecimal.ZERO.compareTo(组合资产估值.getAccrualCost())==0){
			return;
		}
		BigDecimal 单位面值 = calc债券百元面值(资产基本信息.getMockFinCashPlanDtoList(), DateUtil.parse(测算日期));
		if (组合资产估值.getDscCostAmt().compareTo(ArithmeticUtil.multiply(单位面值, 组合资产估值.getShareAmt())) == 0) {
			return;
		}
		if (!StringUtil.equals(投资目的, DictCodeListExt1.投资目的_以出售该金融资产为目的.getValue())) {
			boolean is存在现金流计算规则 = false;
			for (FinCashCalcRule finCashCalcRule : 资产基本信息.getFinCashCalcRuleList()) {
				if (DateUtil.compareDate(finCashCalcRule.getEffDate(), DateUtil.parse(测算日期)) == 0) {
					is存在现金流计算规则 = true;
					break;
				}
			}
			BigDecimal 实际日利率 = 组合资产估值.getActDYield();
			if (is存在现金流计算规则 || 摊销重算map.get(DateUtil.parse(测算日期)) != null || 实际日利率 == null
					|| BigDecimal.ZERO.compareTo(实际日利率) == 0) {
				// 调用共通
//				BigDecimal 单位面值 = calc债券百元面值(资产基本信息.getMockFinCashPlanDtoList(), DateUtil.parse(测算日期));
				BigDecimal 最新报价 = BigDecimal.ZERO;
				for (FinCashCalcRule 报价temp : 资产基本信息.getFinCashCalcRuleList()) {
					if (DateUtil.compareDate(报价temp.getEffDate(), DateUtil.parse(测算日期)) > 0) {
						break;
					}
					最新报价 = 报价temp.getYield();
				}

				List<MockFinCashPlanDto> 付息列表 = new ArrayList<MockFinCashPlanDto>();
				List<MockFinCashPlanDto> 还本列表 = new ArrayList<MockFinCashPlanDto>();
				for (MockFinCashPlanDto mockFinCashPlanDto : 资产基本信息.getMockFinCashPlanDtoList()) {
					if (mockFinCashPlanDto == null || mockFinCashPlanDto.getFinCashPlan() == null)
						continue;
					if (StringUtil.equals(mockFinCashPlanDto.getFinCashPlan().getCashType(), DictCodeList.现金流类型_中间收息.getValue())
							|| StringUtil.equals(mockFinCashPlanDto.getFinCashPlan().getCashType(),
							DictCodeList.现金流类型_到期收息.getValue())) {
						付息列表.add(mockFinCashPlanDto);
					}
					if (StringUtil.equals(mockFinCashPlanDto.getFinCashPlan().getCashType(), DictCodeList.现金流类型_中间还本.getValue())
							|| StringUtil.equals(mockFinCashPlanDto.getFinCashPlan().getCashType(),
							DictCodeList.现金流类型_到期还本.getValue())) {
						还本列表.add(mockFinCashPlanDto);
					}
				}
				// 行权计划查询
				List<FinExercisePlan> 行权计划 = new ArrayList<FinExercisePlan>();
				BigDecimal 行权日百元券面 = new BigDecimal(100);
				if (StringUtil.equals(资产基本信息.getFinProduct().getIsRight(), DictCodeList.是否_是.getValue())) {
					行权计划.addAll(资产基本信息.getFinExercisePlanList());
					if (行权计划 != null && 行权计划.size() > 0 && 行权计划.get(0) != null)
						行权日百元券面 = calc债券百元面值(还本列表, 行权计划.get(0).getExerciseDate());
				}
				实际日利率 = calc摊销日利率(DateUtil.parse(测算日期),组合资产估值.getAccrualAmt(),组合资产估值.getAccrualCost()
						,单位面值,最新报价,资产基本信息.getFinProduct(),付息列表,资产基本信息.getFinCashCalcRuleList()
						,资产基本信息.getFinCashPayRuleList()
						,行权计划, 行权日百元券面
				);
			}
			BigDecimal 摊销额 = ArithmeticUtil.doSub(ArithmeticUtil.multiply(RealTimeValCommonUtil.AMOUNT_SCALE,
					RoundingMode.HALF_UP, 组合资产估值.getAccrualCost(), 实际日利率), 当日利息);
			组合资产估值.setDscCostAmt(ArithmeticUtil.doAdd(组合资产估值.getDscCostAmt(), 摊销额)); // 摊销总成本
			组合资产估值.setAccrualCost(ArithmeticUtil.doAdd(组合资产估值.getAccrualCost(), 摊销额)); // 计提成本
			组合资产估值.setActDYield(实际日利率);// 实际日利率

		}
	}

	/**
	 * 债券估值 公允价值变动 <功能详细描述>
	 *
	 * @param 资产基本信息
	 * @param 投资目的
	 * @param 测算日期
	 * @param 组合资产估值
	 * @param 行情map
	 * @see
	 * @since 1.0
	 */
	private static void 公允价值变动(MockAssetInfoDto 资产基本信息, String 投资目的, String 测算日期, MockPtlSecValuationDto 组合资产估值,
							   Map<String, MockQuotationInfoDto> 行情map) {
		if (!StringUtil.equals(投资目的, DictCodeListExt1.投资目的_以收取该金融资产合同现金流量为目的.getValue())) {
			BigDecimal 市价 = null;
			MockQuotationInfoDto 行情Dto
//					= 行情map.get(测算日期);
//			行情Dto
					// 债券公允价值所用的行情数据与久期行情数据逻辑不一
					= 资产基本信息.getMockQuotationInfoDtoListForGYJZ().stream().max((x,x1)->DateUtil.compareDate(x.getQuotationDate(),x1.getQuotationDate()))
					.orElse(new MockQuotationInfoDto());
			if (行情Dto != null) {
				if ((StringUtil.equals(资产基本信息.getFinProduct().getCouponSpecies(), DictCodeList.息票品种_零息.getValue())
						|| StringUtil.equals(资产基本信息.getFinProduct().getCouponSpecies(),
						DictCodeList.息票品种_贴现.getValue()))
						&& StringUtil.equals(投资目的, DictCodeListExt1.投资目的_以收取该合同现金流量及出售金融资产为目的.getValue())) {
					市价 = 行情Dto.getDirtyPrice();
				} else {
					市价 = 行情Dto.getMarketPrice();
				}
			}
			if (市价 != null) {
				组合资产估值.setTdyFloatIngpl(ArithmeticUtil.doSub(ArithmeticUtil.multiply(RealTimeValCommonUtil.AMOUNT_SCALE,
						RoundingMode.HALF_UP, 组合资产估值.getShareAmt()==null?BigDecimal.ZERO:组合资产估值.getShareAmt(), 市价), 组合资产估值.getDscCostAmt())); // 公允价值变动
			}
			if (组合资产估值.getShareAmt()==null||BigDecimal.ZERO.compareTo(组合资产估值.getShareAmt()) == 0) {
				组合资产估值.setTdyFloatIngpl(BigDecimal.ZERO); // 公允价值变动
			}
		}
	}

	/**
	 *
	 * 债券估值 交易处理 <功能详细描述>
	 *
	 * @param 存续期交易列表
	 * @param 资产端交易列表
	 * @param 测算日期
	 * @param 组合资产估值
	 * @param 投资目的
	 * @param 基准日期
	 * @param 资产基本信息
	 * @param t_N单资产变化_内部
	 * @param t_N现金变化_内部
	 * @param 摊销重算map
	 * @param 交易排序规则
	 * @see
	 * @since 1.0
	 */
	public static void 交易处理(List<MockTrdProductDealDto> 存续期交易列表, List<MockTrdProductDealDto> 资产端交易列表, String 测算日期,
							MockPtlSecValuationDto 组合资产估值, String 投资目的, String 基准日期, MockAssetInfoDto 资产基本信息,
							Map<String, MockSingleAssetChanging> t_N单资产变化_内部, Map<MockCashChangingKey, BigDecimal> t_N现金变化_内部,
							Map<Date, Boolean> 摊销重算map, List<BokTradeOrder> 交易排序规则) {

		Map<String, MockSingleAssetChanging> t_1买入交易 = new HashMap<String, MockSingleAssetChanging>();
		Map<String, MockSingleAssetChanging> t_1卖出交易 = new HashMap<String, MockSingleAssetChanging>();

		// 存续期处理
		// 处理顺序：收息，还本，收息到账，还本到账
		for (MockTrdProductDealDto 存续期交易 : 存续期交易列表) {
			if (存续期交易 == null) {
				continue;
			}
			if (StringUtil.equals(存续期交易.getPs(), DictCodeList.交易方向_收息.getValue())) {
				if (!ArithmeticUtil.equals(BigDecimal.ZERO, 存续期交易.getUnitCprice())) {
					组合资产估值.setTdyIntincexp(ArithmeticUtil.subtract(组合资产估值.getTdyIntincexp(), 存续期交易.getIntAmt())); // 当日应计利息
					组合资产估值.setAccrualInt(ArithmeticUtil.subtract(组合资产估值.getAccrualInt(), 存续期交易.getIntAmt())); // 计提利息
				} else {
					组合资产估值.setTdyIntincexp(BigDecimal.ZERO); // 当日应计利息
					组合资产估值.setAccrualInt(BigDecimal.ZERO); // 计提利息
				}
				组合资产估值.setDelayPayAmt(ArithmeticUtil.doAdd(组合资产估值.getDelayPayAmt(), 存续期交易.getFpriceAmt())); // 待清算资金
			}
			if (StringUtil.equals(存续期交易.getPs(), DictCodeList.交易方向_收息到账.getValue())) {
//				BigDecimal 冲减利息 = 组合资产估值.getDelayPayAmt() == null ? BigDecimal.ZERO : 组合资产估值.getDelayPayAmt();
//				if (冲减利息.compareTo(存续期交易.getTradeAmt() == null ? BigDecimal.ZERO : 存续期交易.getTradeAmt()) > 0)
//					冲减利息 = 存续期交易.getTradeAmt() == null ? BigDecimal.ZERO : 存续期交易.getTradeAmt();
//				if (冲减利息.compareTo(BigDecimal.ZERO) < 0) {
//					冲减利息 = BigDecimal.ZERO;
//				}
				BigDecimal 冲减利息 = 存续期交易.getTradeAmt();

				if (DateUtil.compareDate(存续期交易.getTradeDate(), 存续期交易.getSettleDate()) != 0) {
					MockSingleAssetChanging 资产变动信息 = new MockSingleAssetChanging();
					资产变动信息.setDelayPayAmt(冲减利息.negate());// 清算款
					// 单资产变化赋值T+N
					RealTimeValCommonUtil.单资产变化赋值T_N(t_N单资产变化_内部, DateUtil.format(存续期交易.getSettleDate()), 资产变动信息);
				} else {
					组合资产估值.setDelayPayAmt(ArithmeticUtil.doSub(组合资产估值.getDelayPayAmt(),冲减利息));// 清算款
				}
			}
			if (StringUtil.equals(存续期交易.getPs(), DictCodeList.交易方向_还本.getValue())) {
				组合资产估值.setDscCostAmt(ArithmeticUtil
						.subtract(组合资产估值.getDscCostAmt(),
								ArithmeticUtil.multiply(组合资产估值.getAccrualCost(), 存续期交易.getUnitCprice()))
						.setScale(RealTimeValCommonUtil.AMOUNT_SCALE,RoundingMode.HALF_UP));// 摊销总成本
				组合资产估值.setAccrualCost(ArithmeticUtil
						.subtract(组合资产估值.getAccrualCost(),
								ArithmeticUtil.multiply(组合资产估值.getAccrualCost(), 存续期交易.getUnitCprice()))
						.setScale(RealTimeValCommonUtil.AMOUNT_SCALE,RoundingMode.HALF_UP));// 计提成本
				组合资产估值.setTdyFloatIngpl(ArithmeticUtil
						.subtract(组合资产估值.getTdyFloatIngpl(),
								ArithmeticUtil.multiply(组合资产估值.getTdyFloatIngpl()==null?BigDecimal.ZERO:组合资产估值.getTdyFloatIngpl(), 存续期交易.getUnitCprice()))
						.setScale(RealTimeValCommonUtil.AMOUNT_SCALE,RoundingMode.HALF_UP));// 公允价值变动
				if (DateUtil.compareDate(测算日期, DateUtil.format(资产基本信息.getFinProduct().getMdate())) == 0) {
					组合资产估值.setShareAmt(BigDecimal.ZERO);// 数量
					组合资产估值.setAccrualAmt(BigDecimal.ZERO);// 计提数量
					组合资产估值.setDscCostAmt(BigDecimal.ZERO);// 摊销总成本
					组合资产估值.setAccrualCost(BigDecimal.ZERO);// 计提成本
				}
				组合资产估值.setDelayPayAmt(ArithmeticUtil.doAdd(组合资产估值.getDelayPayAmt(),存续期交易.getFpriceAmt()));// 清算款
			}
			if (StringUtil.equals(存续期交易.getPs(), DictCodeList.交易方向_还本到账.getValue())) {
				if (DateUtil.compareDate(存续期交易.getTradeDate(), 存续期交易.getSettleDate()) != 0) {
					MockSingleAssetChanging 资产变动信息 = new MockSingleAssetChanging();
					资产变动信息.setDelayPayAmt(存续期交易.getTradeAmt() == null ? BigDecimal.ZERO : 存续期交易.getTradeAmt().negate());// 清算款
					// 单资产变化赋值T+N
					RealTimeValCommonUtil.单资产变化赋值T_N(t_N单资产变化_内部, DateUtil.format(存续期交易.getSettleDate()), 资产变动信息);
				} else {
					组合资产估值.setDelayPayAmt(ArithmeticUtil.doSub(组合资产估值.getDelayPayAmt(),存续期交易.getTradeAmt()));// 清算款
				}
			}

			if (StringUtil.equals(存续期交易.getPs(), DictCodeList.交易方向_行权_回售.getValue())
					|| StringUtil.equals(存续期交易.getPs(), DictCodeList.交易方向_行权_赎回.getValue())) {
				组合资产估值.setDscCostAmt(ArithmeticUtil.subtract(组合资产估值.getDscCostAmt(),
						ArithmeticUtil.divide(ArithmeticUtil.multiply(存续期交易.getShareAmt(), 组合资产估值.getAccrualCost()),
								组合资产估值.getAccrualAmt(), RealTimeValCommonUtil.AMOUNT_SCALE)));// 摊销总成本
				组合资产估值.setAccrualCost(ArithmeticUtil.subtract(组合资产估值.getAccrualCost(),
						ArithmeticUtil.divide(ArithmeticUtil.multiply(存续期交易.getShareAmt(), 组合资产估值.getAccrualCost()),
								组合资产估值.getAccrualAmt(), RealTimeValCommonUtil.AMOUNT_SCALE)));// 计提成本
				组合资产估值.setTdyFloatIngpl(ArithmeticUtil.subtract(组合资产估值.getTdyFloatIngpl(),
						ArithmeticUtil.divide(ArithmeticUtil.multiply(存续期交易.getShareAmt(), 组合资产估值.getTdyFloatIngpl()==null?BigDecimal.ZERO:组合资产估值.getTdyFloatIngpl()),
								组合资产估值.getShareAmt(), RealTimeValCommonUtil.AMOUNT_SCALE)));// 公允价值变动
				组合资产估值.setShareAmt(ArithmeticUtil.subtract(组合资产估值.getShareAmt(), 存续期交易.getShareAmt()));// 数量
				组合资产估值.setAccrualAmt(ArithmeticUtil.subtract(组合资产估值.getAccrualAmt(), 存续期交易.getShareAmt()));// 计提数量
				组合资产估值.setTdyIntincexp(ArithmeticUtil.subtract(组合资产估值.getTdyIntincexp(), 存续期交易.getIntAmt()));// 当日应计利息
				组合资产估值.setAccrualInt(ArithmeticUtil.subtract(组合资产估值.getAccrualInt(), 存续期交易.getIntAmt()));// 计提利息
				if (DateUtil.compareDate(测算日期, DateUtil.format(资产基本信息.getFinProduct().getMdate())) == 0) {
					组合资产估值.setShareAmt(BigDecimal.ZERO);// 数量
					组合资产估值.setAccrualAmt(BigDecimal.ZERO);// 计提数量
					组合资产估值.setDscCostAmt(BigDecimal.ZERO);// 摊销总成本
					组合资产估值.setAccrualCost(BigDecimal.ZERO);// 计提成本
				}
				if (DateUtil.compareDate(存续期交易.getTradeDate(), 存续期交易.getSettleDate()) != 0) {
					组合资产估值.setDelayPayAmt(存续期交易.getTradeAmt());// 清算款
					MockSingleAssetChanging 资产变动信息 = new MockSingleAssetChanging();
					资产变动信息.setDelayPayAmt(存续期交易.getTradeAmt() == null ? BigDecimal.ZERO : 存续期交易.getTradeAmt().negate());// 清算款
					// 单资产变化赋值T+N
					RealTimeValCommonUtil.单资产变化赋值T_N(t_N单资产变化_内部, DateUtil.format(存续期交易.getSettleDate()), 资产变动信息);
				}
			}

			RealTimeValCommonUtil.现金变化赋值T_N(t_N现金变化_内部, 基准日期, DictCodeList.是否_否.getValue(),
					new MockCashChangingKey(存续期交易.getSettleDate(), RealTimeValCommonUtil.现金类型_再投, 组合资产估值.getCcy()),
					存续期交易.getTradeAmt());
		}

//    	资产端交易列表按照交易方向排序
		List<MockTrdProductDealDto> 资产端交易列表_排序后 = RealTimeValCommonUtil
				.sortTrdDealDtoByPs(资产端交易列表, 交易排序规则, DictCodeListExt3.业务类型_债券交易.getValue()).getAssetTradeList();
//    	参考估值
		if (!StringUtil.isEmptyList(资产端交易列表_排序后)) {
			for (MockTrdProductDealDto 资产端交易 : 资产端交易列表_排序后) {
/*				// 交易所债券 不考虑T+N   用交易日看作交割日 处理（宁波个性化）
				if(StringUtil.equals(DictCodeListExt1.交易场所_WIND_上交所.getValue(), 资产端交易.getTradeMarket())
						|| StringUtil.equals(DictCodeListExt1.交易场所_WIND_深交所.getValue(), 资产端交易.getTradeMarket())
						|| StringUtil.equals(DictCodeListExt1.估值交易市场_上交所.getValue(), 资产端交易.getTradeMarket())
						|| StringUtil.equals(DictCodeListExt1.估值交易市场_深交所.getValue(), 资产端交易.getTradeMarket())){
					资产端交易.setSettleDate(资产端交易.getTradeDate());
				}*/
				if (StringUtil.equals(资产端交易.getPs(), DictCodeList.交易方向_收息到账.getValue())) {
//					BigDecimal 冲减利息 = 组合资产估值.getDelayPayAmt() == null ? BigDecimal.ZERO : 组合资产估值.getDelayPayAmt();
//					if (冲减利息.compareTo(资产端交易.getTradeAmt() == null ? BigDecimal.ZERO : 资产端交易.getTradeAmt()) > 0)
//						冲减利息 = 资产端交易.getTradeAmt() == null ? BigDecimal.ZERO : 资产端交易.getTradeAmt();
//					if (冲减利息.compareTo(BigDecimal.ZERO) < 0) {
//						冲减利息 = BigDecimal.ZERO;
//					}
					BigDecimal 冲减利息 = 资产端交易.getTradeAmt();
					组合资产估值.setDelayPayAmt(ArithmeticUtil.doSub(组合资产估值.getDelayPayAmt(),冲减利息));// 清算款
					RealTimeValCommonUtil.现金变化赋值T_N(t_N现金变化_内部, 基准日期, 资产端交易.getCashProcessedOrNot(),
							new MockCashChangingKey(资产端交易.getSettleDate(), RealTimeValCommonUtil.现金类型_再投, 组合资产估值.getCcy()),
							资产端交易.getTradeAmt());
				}
				if (StringUtil.equals(资产端交易.getPs(), DictCodeList.交易方向_还本到账.getValue())) {
					组合资产估值.setDelayPayAmt(ArithmeticUtil.doSub(组合资产估值.getDelayPayAmt(),资产端交易.getTradeAmt()));// 清算款
					RealTimeValCommonUtil.现金变化赋值T_N(t_N现金变化_内部, 基准日期, 资产端交易.getCashProcessedOrNot(),
							new MockCashChangingKey(资产端交易.getSettleDate(), RealTimeValCommonUtil.现金类型_再投, 组合资产估值.getCcy()),
							资产端交易.getTradeAmt());
				}

				if (StringUtil.equals(资产端交易.getPs(), DictCodeList.交易方向_买入.getValue())
						|| StringUtil.equals(资产端交易.getPs(), DictCodeList.交易方向_申购确认.getValue())) {
					Date 重算日期 = 资产端交易.getSettleDate();
/*					if ((StringUtil.equals(DictCodeListExt1.交易场所_WIND_上交所.getValue(), 资产端交易.getTradeMarket())
							|| StringUtil.equals(DictCodeListExt1.交易场所_WIND_深交所.getValue(), 资产端交易.getTradeMarket())
							|| StringUtil.equals(DictCodeListExt1.估值交易市场_上交所.getValue(), 资产端交易.getTradeMarket())
							|| StringUtil.equals(DictCodeListExt1.估值交易市场_深交所.getValue(), 资产端交易.getTradeMarket()))
							&& StringUtil.equals(DictCodeListExt1.交易市场_二级市场.getValue(), 资产端交易.getTradeMarketMode())
							&& StringUtil.equals(资产端交易.getPs(), DictCodeList.交易方向_买入.getValue())) {
						重算日期 = DateUtil.addDay(重算日期, 1);
					}*/
					摊销重算map.put(重算日期, true);
					BigDecimal 交易成本 = BigDecimal.ZERO;
					// 投资目的 公允价值变动计入当期损益 FVTPL
					if (!StringUtil.equals(DictCodeListExt1.投资目的_以出售该金融资产为目的.getValue(), 资产端交易.getInvAim())) {
						// 交易成本
						交易成本 = ArithmeticUtil.doAdd(资产端交易.getPrinAmt(), 资产端交易.getFeeAmt());
					} else {
						// 交易成本
						交易成本 = 资产端交易.getPrinAmt();
					}

					组合资产估值.setDscCostAmt(ArithmeticUtil.doAdd(组合资产估值.getDscCostAmt(), 交易成本));// 摊销总成本
					组合资产估值.setShareAmt(ArithmeticUtil.doAdd(组合资产估值.getShareAmt(), 资产端交易.getShareAmt()));// 数量
					组合资产估值.setTdyIntincexp(ArithmeticUtil.doAdd(组合资产估值.getTdyIntincexp(), 资产端交易.getIntAmt()));// 当日应计利息

					if (DateUtil.compareDate(资产端交易.getTradeDate(), 资产端交易.getSettleDate()) != 0) {
						组合资产估值.setDelayPayAmt(ArithmeticUtil.doSub(组合资产估值.getDelayPayAmt(), 资产端交易.getTradeAmt()));// 清算款

						MockSingleAssetChanging 资产变动信息清算款 = new MockSingleAssetChanging();
						资产变动信息清算款.setDelayPayAmt(资产端交易.getTradeAmt() == null ? BigDecimal.ZERO : 资产端交易.getTradeAmt());// 清算款
						// 单资产变化赋值T+N
						RealTimeValCommonUtil.单资产变化赋值T_N(t_N单资产变化_内部, DateUtil.format(资产端交易.getSettleDate()), 资产变动信息清算款);
						if (StringUtil.equals(资产端交易.getPs(), DictCodeList.交易方向_买入.getValue())) {
							MockSingleAssetChanging 资产变动信息 = new MockSingleAssetChanging();
							资产变动信息.setAccrualAmount(资产端交易.getShareAmt() == null ? BigDecimal.ZERO : 资产端交易.getShareAmt());// 计提数量
							资产变动信息.setAccrualCost(交易成本);// 计提成本
							资产变动信息.setAccrualInterest(资产端交易.getIntAmt() == null ? BigDecimal.ZERO : 资产端交易.getIntAmt());// 计提利息
							Date 日期 = 资产端交易.getSettleDate();
/*							if ((StringUtil.equals(DictCodeListExt1.交易场所_WIND_上交所.getValue(), 资产端交易.getTradeMarket())
									|| StringUtil.equals(DictCodeListExt1.交易场所_WIND_深交所.getValue(), 资产端交易.getTradeMarket())
									|| StringUtil.equals(DictCodeListExt1.估值交易市场_上交所.getValue(), 资产端交易.getTradeMarket())
									|| StringUtil.equals(DictCodeListExt1.估值交易市场_深交所.getValue(), 资产端交易.getTradeMarket()))
									&& StringUtil.equals(DictCodeListExt1.交易市场_二级市场.getValue(),
									资产端交易.getTradeMarketMode())) {
								日期 = DateUtil.addDay(日期, 1);
							}*/

							// 单资产变化赋值T+N
							RealTimeValCommonUtil.单资产变化赋值T_N(t_N单资产变化_内部, DateUtil.format(日期), 资产变动信息);
							MockSingleAssetChanging 发生额集 = t_1买入交易.get(DateUtil.format(资产端交易.getSettleDate()));
							if (发生额集 == null) {
								发生额集 = new MockSingleAssetChanging();
								t_1买入交易.put(DateUtil.format(日期), 发生额集);
							}
							发生额集.setInterest(ArithmeticUtil.doAdd(发生额集.getInterest(), 资产端交易.getIntAmt()));// 发生应计利息
							发生额集.setCost(ArithmeticUtil.doAdd(发生额集.getCost(), 交易成本));// 发生成本
							发生额集.setAmount(ArithmeticUtil.doAdd(发生额集.getAmount(), 资产端交易.getShareAmt()));// 发生数量

						}
					} else {
						if ((StringUtil.equals(DictCodeListExt1.交易场所_WIND_上交所.getValue(), 资产端交易.getTradeMarket())
								|| StringUtil.equals(DictCodeListExt1.交易场所_WIND_深交所.getValue(), 资产端交易.getTradeMarket())
								|| StringUtil.equals(DictCodeListExt1.估值交易市场_上交所.getValue(), 资产端交易.getTradeMarket())
								|| StringUtil.equals(DictCodeListExt1.估值交易市场_深交所.getValue(), 资产端交易.getTradeMarket()))
								&& StringUtil.equals(DictCodeListExt1.交易市场_二级市场.getValue(), 资产端交易.getTradeMarketMode())
								&& StringUtil.equals(资产端交易.getPs(), DictCodeList.交易方向_买入.getValue())) {
							MockSingleAssetChanging 资产变动信息 = new MockSingleAssetChanging();
							资产变动信息.setAccrualAmount(资产端交易.getShareAmt() == null ? BigDecimal.ZERO : 资产端交易.getShareAmt());// 计提数量
							资产变动信息.setAccrualCost(交易成本);// 计提成本
							资产变动信息.setAccrualInterest(资产端交易.getIntAmt() == null ? BigDecimal.ZERO : 资产端交易.getIntAmt());// 计提利息
							// 单资产变化赋值T+N
							RealTimeValCommonUtil.单资产变化赋值T_N(t_N单资产变化_内部,
									DateUtil.format(DateUtil.addDay(资产端交易.getSettleDate(), 1)), 资产变动信息);
							MockSingleAssetChanging 发生额集 = t_1买入交易
									.get(DateUtil.format(DateUtil.addDay(资产端交易.getSettleDate(), 1)));
							if (发生额集 == null) {
								发生额集 = new MockSingleAssetChanging();
								t_1买入交易.put(DateUtil.format(DateUtil.addDay(资产端交易.getSettleDate(), 1)), 发生额集);
							}
							发生额集.setInterest(ArithmeticUtil.doAdd(发生额集.getInterest(), 资产端交易.getIntAmt()));// 发生应计利息
							发生额集.setCost(ArithmeticUtil.doAdd(发生额集.getCost(), 交易成本));// 发生成本
							发生额集.setAmount(ArithmeticUtil.doAdd(发生额集.getAmount(), 资产端交易.getShareAmt()));// 发生数量
						} else {
							组合资产估值.setAccrualAmt(ArithmeticUtil.doAdd(组合资产估值.getAccrualAmt(), 资产端交易.getShareAmt()));// 计提数量
							组合资产估值.setAccrualCost(ArithmeticUtil.doAdd(组合资产估值.getAccrualCost(), 交易成本));// 计提成本
							组合资产估值.setAccrualInt(ArithmeticUtil.doAdd(组合资产估值.getAccrualInt(), 资产端交易.getIntAmt()));// 计提利息
						}
					}
					RealTimeValCommonUtil.现金变化赋值T_N(t_N现金变化_内部, 基准日期, 资产端交易.getCashProcessedOrNot(),
							new MockCashChangingKey(资产端交易.getSettleDate(), RealTimeValCommonUtil.现金类型_再投, 组合资产估值.getCcy()),
							资产端交易.getTradeAmt() == null ? BigDecimal.ZERO : 资产端交易.getTradeAmt().negate());
				}
				if (StringUtil.equals(资产端交易.getPs(), DictCodeList.交易方向_卖出.getValue())
						|| StringUtil.equals(资产端交易.getPs(), DictCodeList.交易方向_行权_回售.getValue())
						|| StringUtil.equals(资产端交易.getPs(), DictCodeList.交易方向_行权_赎回.getValue())
						|| StringUtil.equals(资产端交易.getPs(), DictCodeListExt3.交易方向_债转股.getValue())) {
					BigDecimal 利息总额 = 资产端交易.getIntAmt();
//					if (利息总额 == null || BigDecimal.ZERO.compareTo(利息总额) == 0) {
					if (StringUtil.equals(资产端交易.getPs(), DictCodeListExt1.交易方向_债转股.getValue())) {
						利息总额 = ArithmeticUtil.divide(ArithmeticUtil.multiply(资产端交易.getShareAmt(), 组合资产估值.getAccrualInt()),
								组合资产估值.getAccrualAmt(), RealTimeValCommonUtil.AMOUNT_SCALE);
					}

					BigDecimal 数量 = 资产端交易.getShareAmt();
					BigDecimal 冲减成本 = ArithmeticUtil.divide(
							ArithmeticUtil.multiply(组合资产估值.getDscCostAmt(), 资产端交易.getShareAmt()), 组合资产估值.getShareAmt(),
							RealTimeValCommonUtil.AMOUNT_SCALE);
					数量 = ArithmeticUtil.divide(
							ArithmeticUtil.multiply(组合资产估值.getShareAmt(), 资产端交易.getShareAmt()), 组合资产估值.getShareAmt(),
							RealTimeValCommonUtil.AMOUNT_SCALE);
					BigDecimal 冲减应计利息 = 利息总额;
					if ((StringUtil.equals(DictCodeListExt1.交易场所_WIND_上交所.getValue(), 资产端交易.getTradeMarket())
							|| StringUtil.equals(DictCodeListExt1.交易场所_WIND_深交所.getValue(), 资产端交易.getTradeMarket())
							|| StringUtil.equals(DictCodeListExt1.估值交易市场_上交所.getValue(), 资产端交易.getTradeMarket())
							|| StringUtil.equals(DictCodeListExt1.估值交易市场_深交所.getValue(), 资产端交易.getTradeMarket())
							|| DateUtil.compareDate(资产端交易.getTradeDate(), 资产端交易.getSettleDate()) != 0)
							&& StringUtil.equals(DictCodeListExt1.交易市场_二级市场.getValue(), 资产端交易.getTradeMarketMode())
							&& StringUtil.equals(资产端交易.getPs(), DictCodeList.交易方向_卖出.getValue())) {
						Date 交割日 = 资产端交易.getSettleDate();
/*						if (StringUtil.equals(DictCodeListExt1.交易场所_WIND_上交所.getValue(), 资产端交易.getTradeMarket())
								|| StringUtil.equals(DictCodeListExt1.交易场所_WIND_深交所.getValue(), 资产端交易.getTradeMarket())
								|| StringUtil.equals(DictCodeListExt1.估值交易市场_上交所.getValue(), 资产端交易.getTradeMarket())
								|| StringUtil.equals(DictCodeListExt1.估值交易市场_深交所.getValue(), 资产端交易.getTradeMarket())) {
							交割日 = DateUtil.addDay(交割日, 1);
						}*/
						// 调用共同债券T+1处理
						MockT1BalanceResult t_1处理结果集 = 债券T_1处理(组合资产估值.getAccrualAmt(), 组合资产估值.getAccrualCost(),
								组合资产估值.getAccrualInt(), 组合资产估值.getTdyFloatIngpl(), BigDecimal.ZERO, 投资目的,
								资产端交易.getTradeDate(), 交割日, t_1买入交易, t_1卖出交易, 组合资产估值, 资产基本信息, 摊销重算map);
						冲减成本 = ArithmeticUtil.divide(ArithmeticUtil.multiply(t_1处理结果集.getCostBalance(), 资产端交易.getShareAmt()),
								t_1处理结果集.getAmountBalance(), RealTimeValCommonUtil.AMOUNT_SCALE);
					}
					组合资产估值.setDscCostAmt(ArithmeticUtil.doSub(组合资产估值.getDscCostAmt(), 冲减成本));// 摊销总成本
					组合资产估值.setShareAmt(ArithmeticUtil.doSub(组合资产估值.getShareAmt(), 数量));// 数量
					组合资产估值.setTdyIntincexp(ArithmeticUtil.doSub(组合资产估值.getTdyIntincexp(), 冲减应计利息));// 当日应计利息
					组合资产估值.setTdyFloatIngpl(ArithmeticUtil.doSub(组合资产估值.getTdyFloatIngpl(),
							ArithmeticUtil.divide(ArithmeticUtil.multiply(资产端交易.getShareAmt(), 组合资产估值.getTdyFloatIngpl()==null?BigDecimal.ZERO: 组合资产估值.getTdyFloatIngpl()),
									组合资产估值.getShareAmt(), RealTimeValCommonUtil.AMOUNT_SCALE)));// 公允价值变动
					if (DateUtil.compareDate(资产端交易.getTradeDate(), 资产端交易.getSettleDate()) != 0) {
						组合资产估值.setDelayPayAmt(ArithmeticUtil.doAdd(组合资产估值.getDelayPayAmt(), 资产端交易.getTradeAmt()));// 清算款
						MockSingleAssetChanging 资产变动信息清算款 = new MockSingleAssetChanging();
						资产变动信息清算款.setDelayPayAmt(资产端交易.getTradeAmt() == null ? BigDecimal.ZERO : 资产端交易.getTradeAmt().negate());// 清算款
						// 单资产变化赋值T+N
						RealTimeValCommonUtil.单资产变化赋值T_N(t_N单资产变化_内部, DateUtil.format(资产端交易.getSettleDate()), 资产变动信息清算款);

						if (StringUtil.equals(资产端交易.getPs(), DictCodeList.交易方向_卖出.getValue())) {
							MockSingleAssetChanging 资产变动信息 = new MockSingleAssetChanging();
							资产变动信息.setAccrualAmount(数量.negate());// 计提数量
							资产变动信息.setAccrualCost(冲减成本.negate());// 计提成本
							资产变动信息.setAccrualInterest(冲减应计利息.negate());// 计提利息
							Date 日期 = 资产端交易.getSettleDate();
/*							if ((StringUtil.equals(DictCodeListExt1.交易场所_WIND_上交所.getValue(), 资产端交易.getTradeMarket())
									|| StringUtil.equals(DictCodeListExt1.交易场所_WIND_深交所.getValue(), 资产端交易.getTradeMarket())
									|| StringUtil.equals(DictCodeListExt1.估值交易市场_上交所.getValue(), 资产端交易.getTradeMarket())
									|| StringUtil.equals(DictCodeListExt1.估值交易市场_深交所.getValue(), 资产端交易.getTradeMarket()))
									&& StringUtil.equals(DictCodeListExt1.交易市场_二级市场.getValue(),
									资产端交易.getTradeMarketMode())) {
								日期 = DateUtil.addDay(日期, 1);
							}*/
							// 单资产变化赋值T+N
							RealTimeValCommonUtil.单资产变化赋值T_N(t_N单资产变化_内部, DateUtil.format(日期), 资产变动信息);
							MockSingleAssetChanging 发生额集 = t_1卖出交易.get(DateUtil.format(日期));
							if (发生额集 == null) {
								发生额集 = new MockSingleAssetChanging();
								t_1卖出交易.put(DateUtil.format(日期), 发生额集);
							}
							发生额集.setInterest(ArithmeticUtil.doAdd(发生额集.getInterest(), 冲减应计利息));// 发生应计利息
							发生额集.setCost(ArithmeticUtil.doAdd(发生额集.getCost(), 冲减成本));// 发生成本
							发生额集.setAmount(ArithmeticUtil.doAdd(发生额集.getAmount(), 数量));// 发生数量
						}
					} else {
						if ((StringUtil.equals(DictCodeListExt1.交易场所_WIND_上交所.getValue(), 资产端交易.getTradeMarket())
								|| StringUtil.equals(DictCodeListExt1.交易场所_WIND_深交所.getValue(), 资产端交易.getTradeMarket())
								|| StringUtil.equals(DictCodeListExt1.估值交易市场_上交所.getValue(), 资产端交易.getTradeMarket())
								|| StringUtil.equals(DictCodeListExt1.估值交易市场_深交所.getValue(), 资产端交易.getTradeMarket()))
								&& StringUtil.equals(DictCodeListExt1.交易市场_二级市场.getValue(), 资产端交易.getTradeMarketMode())
								&& StringUtil.equals(资产端交易.getPs(), DictCodeList.交易方向_卖出.getValue())) {
							MockSingleAssetChanging 资产变动信息 = new MockSingleAssetChanging();
							资产变动信息.setAccrualAmount(数量.negate());// 计提数量
							资产变动信息.setAccrualCost(冲减成本.negate());// 计提成本
							资产变动信息.setAccrualInterest(冲减应计利息.negate());// 计提利息
							RealTimeValCommonUtil.单资产变化赋值T_N(t_N单资产变化_内部,
									DateUtil.format(DateUtil.addDay(资产端交易.getSettleDate(), 1)), 资产变动信息);
							MockSingleAssetChanging 发生额集 = t_1卖出交易
									.get(DateUtil.format(DateUtil.addDay(资产端交易.getSettleDate(), 1)));
							if (发生额集 == null) {
								发生额集 = new MockSingleAssetChanging();
								t_1卖出交易.put(DateUtil.format(DateUtil.addDay(资产端交易.getSettleDate(), 1)), 发生额集);
							}
							发生额集.setInterest(ArithmeticUtil.doAdd(发生额集.getInterest(), 冲减应计利息));// 发生应计利息
							发生额集.setCost(ArithmeticUtil.doAdd(发生额集.getCost(), 冲减成本));// 发生成本
							发生额集.setAmount(ArithmeticUtil.doAdd(发生额集.getAmount(), 数量));// 发生数量
						} else {
							组合资产估值.setAccrualAmt(ArithmeticUtil.doSub(组合资产估值.getAccrualAmt(), 数量));// 计提数量
							组合资产估值.setAccrualCost(ArithmeticUtil.doSub(组合资产估值.getAccrualCost(), 冲减成本));// 计提成本
							组合资产估值.setAccrualInt(ArithmeticUtil.doSub(组合资产估值.getAccrualInt(), 冲减应计利息));// 计提利息
						}
					}
					RealTimeValCommonUtil.现金变化赋值T_N(t_N现金变化_内部, 基准日期, 资产端交易.getCashProcessedOrNot(),
							new MockCashChangingKey(资产端交易.getSettleDate(), RealTimeValCommonUtil.现金类型_再投, 组合资产估值.getCcy()),
							资产端交易.getTradeAmt() == null ? BigDecimal.ZERO : 资产端交易.getTradeAmt());
				}
				if (StringUtil.equals(资产端交易.getPs(), DictCodeList.交易方向_应收款项转出.getValue())) {
					组合资产估值.setDelayPayAmt(ArithmeticUtil.doSub(组合资产估值.getDelayPayAmt(), 资产端交易.getTradeAmt()));// 清算款
					RealTimeValCommonUtil.现金变化赋值T_N(t_N现金变化_内部, 基准日期, 资产端交易.getCashProcessedOrNot(),
							new MockCashChangingKey(资产端交易.getSettleDate(), RealTimeValCommonUtil.现金类型_再投, 组合资产估值.getCcy()),
							资产端交易.getTradeAmt() == null ? BigDecimal.ZERO : 资产端交易.getTradeAmt());
				}
				if (StringUtil.equals(资产端交易.getPs(), DictCodeList.交易方向_应收款项转入.getValue())) {
					组合资产估值.setDelayPayAmt(ArithmeticUtil.doAdd(组合资产估值.getDelayPayAmt(), 资产端交易.getTradeAmt()));// 清算款
					RealTimeValCommonUtil.现金变化赋值T_N(t_N现金变化_内部, 基准日期, 资产端交易.getCashProcessedOrNot(),
							new MockCashChangingKey(资产端交易.getSettleDate(), RealTimeValCommonUtil.现金类型_再投, 组合资产估值.getCcy()),
							资产端交易.getTradeAmt() == null ? BigDecimal.ZERO : 资产端交易.getTradeAmt().negate());
				}
			}
		}
	}

	/**
	 * 债券估值 债券T_1处理 <功能详细描述>
	 *
	 * @param 数量余额
	 * @param 成本类科目余额
	 * @param 应计利息科目余额
	 * @param 公允价值变动类科目余额
	 * @param 溢折价类科目余额
	 * @param 投资目的
	 * @param 交易日
	 * @param 交割日
	 * @param t_1买入交易
	 * @param t_1卖出交易
	 * @param 组合资产估值
	 * @param 资产基本信息
	 * @param 摊销重算map
	 * @return
	 * @see
	 * @since 1.0
	 */
	public static MockT1BalanceResult 债券T_1处理(BigDecimal 数量余额, BigDecimal 成本类科目余额, BigDecimal 应计利息科目余额,
											  BigDecimal 公允价值变动类科目余额, BigDecimal 溢折价类科目余额, String 投资目的, Date 交易日, Date 交割日,
											  Map<String, MockSingleAssetChanging> t_1买入交易, Map<String, MockSingleAssetChanging> t_1卖出交易,
											  MockPtlSecValuationDto 组合资产估值, MockAssetInfoDto 资产基本信息, Map<Date, Boolean> 摊销重算map) {
		MockT1BalanceResult result = new MockT1BalanceResult();
		result.setTdyFloatIngplBalance(公允价值变动类科目余额);
		result.setAdjustBalance(溢折价类科目余额);
		result.setInterestBalance(应计利息科目余额);
		result.setCostBalance(成本类科目余额);
		result.setAmountBalance(数量余额);
		// 标的类金融产品信息
		FinProduct 标的信息 = 资产基本信息.getFinProduct();
		List<MockFinCashPlanDto> 付息列表 = new ArrayList<MockFinCashPlanDto>();
		List<MockFinCashPlanDto> 还本列表 = new ArrayList<MockFinCashPlanDto>();
		for (MockFinCashPlanDto mockFinCashPlanDto : 资产基本信息.getMockFinCashPlanDtoList()) {
			if (mockFinCashPlanDto == null || mockFinCashPlanDto.getFinCashPlan() == null)
				continue;
			if (StringUtil.equals(mockFinCashPlanDto.getFinCashPlan().getCashType(), DictCodeList.现金流类型_中间收息.getValue())
					|| StringUtil.equals(mockFinCashPlanDto.getFinCashPlan().getCashType(),
					DictCodeList.现金流类型_到期收息.getValue())) {
				付息列表.add(mockFinCashPlanDto);
			}
			if (StringUtil.equals(mockFinCashPlanDto.getFinCashPlan().getCashType(), DictCodeList.现金流类型_中间还本.getValue())
					|| StringUtil.equals(mockFinCashPlanDto.getFinCashPlan().getCashType(),
					DictCodeList.现金流类型_到期还本.getValue())) {
				还本列表.add(mockFinCashPlanDto);
			}
		}
		List<FinCashCalcRule> 报价列表 = 资产基本信息.getFinCashCalcRuleList();
		List<FinCashPayRule> 支付规则 = 资产基本信息.getFinCashPayRuleList();
		// T+1区间内计划查询
		Map<String, MockFinCashPlanDto> t_1区间内付息计划 = new HashMap<String, MockFinCashPlanDto>();
		Map<String, MockFinCashPlanDto> t_1区间内还本计划 = new HashMap<String, MockFinCashPlanDto>();
		付息列表.forEach(entity -> {
			if (DateUtil.compareDate(交易日, entity.getFinCashPlan().getMdate()) < 0
					&& DateUtil.compareDate(交割日, entity.getFinCashPlan().getMdate()) >= 0)
				t_1区间内付息计划.put(DateUtil.format(entity.getFinCashPlan().getMdate()), entity);
		});

		还本列表.forEach(entity -> {
			if (DateUtil.compareDate(交易日, entity.getFinCashPlan().getMdate()) < 0
					&& DateUtil.compareDate(交割日, entity.getFinCashPlan().getMdate()) >= 0)
				t_1区间内还本计划.put(DateUtil.format(entity.getFinCashPlan().getMdate()), entity);
		});
		// 行权计划查询
		List<FinExercisePlan> 行权计划 = new ArrayList<FinExercisePlan>();
		BigDecimal 行权日百元券面 = new BigDecimal(100);
		if (StringUtil.equals(标的信息.getIsRight(), DictCodeList.是否_是.getValue())) {
			行权计划.addAll(资产基本信息.getFinExercisePlanList());
			if (行权计划 != null && 行权计划.size() > 0 && 行权计划.get(0) != null)
				行权日百元券面 = calc债券百元面值(还本列表, 行权计划.get(0).getExerciseDate());
		}
		// 摊销日利率
		BigDecimal 摊销日利率 = 组合资产估值.getActDYield();

		//
		Date 日期 = 交易日;
		while (DateUtil.compareDate(日期, 交割日) < 0) {
			boolean 重算状态 = false;
			BigDecimal 应计利息 = BigDecimal.ZERO;
			BigDecimal 百元券面面值 = calc债券百元面值(还本列表, 日期);
			// 计提处理
			if (!(StringUtil.equals(标的信息.getCouponSpecies(), DictCodeList.息票品种_零息.getValue())
					|| StringUtil.equals(标的信息.getCouponSpecies(), DictCodeList.息票品种_贴现.getValue()))
					|| StringUtil.equals(投资目的, DictCodeListExt1.投资目的_以出售该金融资产为目的.getValue())) {
				if (百元券面面值 != null && BigDecimal.ZERO.compareTo(百元券面面值) != 0) {
					BigDecimal temp = result.getInterestBalance();// 应计利息科目余额
					// 债券应计利息计算
					BigDecimal 应计利息result = calc债券利息(日期, 标的信息, 付息列表, 报价列表, 支付规则, 百元券面面值);
					//
					int 舍位参数 = 12;
/*					if (StringUtil.equals(DictCodeListExt1.交易场所_WIND_上交所.getValue(), 标的信息.getTradeMarket())
							|| StringUtil.equals(DictCodeListExt1.交易场所_WIND_深交所.getValue(), 标的信息.getTradeMarket())
							|| StringUtil.equals(DictCodeListExt1.估值交易市场_上交所.getValue(), 标的信息.getTradeMarket())
							|| StringUtil.equals(DictCodeListExt1.估值交易市场_深交所.getValue(), 标的信息.getTradeMarket())) {
						舍位参数 = 8;
					}*/
					result.setInterestBalance(ArithmeticUtil.divide(
							ArithmeticUtil.multiply(应计利息result, 百元券面面值, result.getAmountBalance()), new BigDecimal(100),
							舍位参数));
					应计利息 = ArithmeticUtil.doSub(result.getInterestBalance(), temp);
				}
			}
			// 摊销处理
			if (!StringUtil.equals(投资目的, DictCodeListExt1.投资目的_以出售该金融资产为目的.getValue())) {
				if (BigDecimal.ZERO.compareTo(ArithmeticUtil.doSub(
						ArithmeticUtil.multiply(百元券面面值, result.getAmountBalance()), result.getCostBalance())) != 0) {
					if (摊销重算map.get(日期) != null && 摊销重算map.get(日期)) {
						重算状态 = true;
					}
					BigDecimal 最新报价 = BigDecimal.ZERO;
					for (FinCashCalcRule 报价temp : 报价列表) {
						if (DateUtil.compareDate(报价temp.getEffDate(), 日期) > 0) {
							break;
						}
						if (DateUtil.compareDate(报价temp.getEffDate(), 日期) == 0
								&& 报价temp.getYield().compareTo(最新报价) != 0) {
							重算状态 = true;
						}
						最新报价 = 报价temp.getYield();
					}

					BigDecimal 利息调整 = BigDecimal.ZERO;
					// if（变量.重算状态 = 重算 || 变量.摊销日利率 = null）
					if (重算状态 || 摊销日利率 == null) {
						// 调用共通
						摊销日利率 = calc摊销日利率(日期, result.getAmountBalance(),
								ArithmeticUtil.doAdd(result.getCostBalance(), result.getAdjustBalance()), 百元券面面值, 最新报价,
								标的信息, 付息列表, 报价列表, 支付规则, 行权计划, 行权日百元券面);
					}

					利息调整 = ArithmeticUtil.doSub(ArithmeticUtil.multiply(摊销日利率,
							ArithmeticUtil.doAdd(result.getCostBalance(), result.getAdjustBalance())), 应计利息);
					result.setCostBalance(ArithmeticUtil.doAdd(result.getCostBalance(), 利息调整));
				}
			}

			日期 = DateUtil.addDay(日期, 1);

/*			if (!StringUtil.equals(DictCodeListExt1.交易场所_WIND_上交所.getValue(), 标的信息.getTradeMarket())
					&& !StringUtil.equals(DictCodeListExt1.交易场所_WIND_深交所.getValue(), 标的信息.getTradeMarket())
					&& !StringUtil.equals(DictCodeListExt1.估值交易市场_上交所.getValue(), 标的信息.getTradeMarket())
					&& !StringUtil.equals(DictCodeListExt1.估值交易市场_深交所.getValue(), 标的信息.getTradeMarket())
					&& DateUtil.compareDate(日期, 交割日) < 0) {
				// 付息计划处理
				if (t_1区间内付息计划.get(DateUtil.format(日期)) != null) {
					result.setInterestBalance(BigDecimal.ZERO);// 应计利息科目余额
				}
				// 还本计划处理
				if (t_1区间内还本计划.get(DateUtil.format(日期)) != null) {
					MockFinCashPlanDto 还本计划 = t_1区间内还本计划.get(DateUtil.format(日期));
					result.setTdyFloatIngplBalance(ArithmeticUtil
							.doSub(result.getTdyFloatIngplBalance(),
									ArithmeticUtil.divide(
											ArithmeticUtil.multiply(result.getTdyFloatIngplBalance(),
													还本计划.getFinCashPlan().getPrinAmt()),
											百元券面面值, RealTimeValCommonUtil.AMOUNT_SCALE))
							.setScale(RealTimeValCommonUtil.AMOUNT_SCALE,RoundingMode.HALF_UP));// 公允价值变动类科目余额
					result.setAdjustBalance(ArithmeticUtil
							.doSub(result.getAdjustBalance(),
									ArithmeticUtil.divide(
											ArithmeticUtil.multiply(result.getAdjustBalance(),
													还本计划.getFinCashPlan().getPrinAmt()),
											百元券面面值, RealTimeValCommonUtil.AMOUNT_SCALE))
							.setScale(RealTimeValCommonUtil.AMOUNT_SCALE,RoundingMode.HALF_UP));// 溢折价类科目余额
					result.setInterestBalance(ArithmeticUtil
							.doSub(result.getInterestBalance(),
									ArithmeticUtil.divide(
											ArithmeticUtil.multiply(result.getInterestBalance(),
													还本计划.getFinCashPlan().getPrinAmt()),
											百元券面面值, RealTimeValCommonUtil.AMOUNT_SCALE))
							.setScale(RealTimeValCommonUtil.AMOUNT_SCALE,RoundingMode.HALF_UP));// 应计利息类科目余额
					result.setCostBalance(ArithmeticUtil
							.doSub(result.getCostBalance(),
									ArithmeticUtil.divide(
											ArithmeticUtil.multiply(result.getCostBalance(),
													还本计划.getFinCashPlan().getPrinAmt()),
											百元券面面值, RealTimeValCommonUtil.AMOUNT_SCALE))
							.setScale(RealTimeValCommonUtil.AMOUNT_SCALE,RoundingMode.HALF_UP));// 成本类科目余额
				}
			}*/
			// 买入交易
			if (t_1买入交易.get(DateUtil.format(日期)) != null) {
				MockSingleAssetChanging 发生额集 = t_1买入交易.get(DateUtil.format(日期));
				result.setInterestBalance(ArithmeticUtil.doAdd(result.getInterestBalance(), 发生额集.getInterest()));// 应计利息类科目余额
				result.setAdjustBalance(ArithmeticUtil.doAdd(result.getAdjustBalance(), 发生额集.getInterestAdjust()));// 溢折价类科目余额
				result.setCostBalance(ArithmeticUtil.doAdd(result.getCostBalance(), 发生额集.getCost()));// 成本类科目余额
				result.setAmountBalance(ArithmeticUtil.doAdd(result.getAmountBalance(), 发生额集.getAmount()));// 数量余额
			}
			// 卖出交易
			if (t_1卖出交易.get(DateUtil.format(日期)) != null) {
				MockSingleAssetChanging 发生额集 = t_1卖出交易.get(DateUtil.format(日期));
				result.setTdyFloatIngplBalance(
						ArithmeticUtil.doSub(result.getTdyFloatIngplBalance(), 发生额集.getTdyFloatIngpl()));// 应计利息类科目余额
				result.setInterestBalance(ArithmeticUtil.doSub(result.getInterestBalance(), 发生额集.getInterest()));// 应计利息类科目余额
				result.setAdjustBalance(ArithmeticUtil.doSub(result.getAdjustBalance(), 发生额集.getInterestAdjust()));// 溢折价类科目余额
				result.setCostBalance(ArithmeticUtil.doSub(result.getCostBalance(), 发生额集.getCost()));// 成本类科目余额
				result.setAmountBalance(ArithmeticUtil.doSub(result.getAmountBalance(), 发生额集.getAmount()));// 数量余额
			}
		}
		return result;
	}

	/**
	 * <一句话功能简述> <功能详细描述>
	 *
	 * @param 现金流列表
	 * @param exerciseDate
	 * @return
	 * @see
	 * @since 1.0
	 */
	private static BigDecimal calc债券百元面值(List<MockFinCashPlanDto> 现金流列表, Date exerciseDate) {

		BigDecimal result = new BigDecimal(100);
		BigDecimal 已支付本金 = BigDecimal.ZERO;
		for (MockFinCashPlanDto entity : 现金流列表) {
			if (DateUtil.compareDate(entity.getFinCashPlan().getMdate(), exerciseDate) <= 0) {
				已支付本金 = ArithmeticUtil.doAdd(已支付本金, entity.getFinCashPlan().getPrinAmt());
			}
		}
		return ArithmeticUtil.doSub(result, 已支付本金);
	}

	/**
	 * 交易处理T_N <功能详细描述>
	 *
	 * @param 资产端t_N交易列表
	 * @param 基准日期
	 * @param 资产基本信息
	 * @param t_N单资产变化
	 * @param t_N现金变化
	 * @param 债券T_1成本处理方式
	 * @param 组合资产估值
	 * @see
	 * @since 1.0
	 */
	public static void 交易处理T_N(List<MockTrdProductDealDto> 资产端t_N交易列表, String 基准日期, MockAssetInfoDto 资产基本信息,
							   Map<String, MockSingleAssetChanging> t_N单资产变化, Map<MockCashChangingKey, BigDecimal> t_N现金变化,
							   String 债券T_1成本处理方式, MockPtlSecValuationDto 组合资产估值, List<BokTradeOrder> 交易排序规则) {
		// 按照交易方向排序
		List<MockTrdProductDealDto> 资产端t_N交易列表_排序后 = RealTimeValCommonUtil
				.sortTrdDealDtoByPs(资产端t_N交易列表, 交易排序规则, DictCodeListExt3.业务类型_债券交易.getValue()).getAssetTradeList();

		for (MockTrdProductDealDto mockTrdProductDealDto : 资产端t_N交易列表_排序后) {
			if (mockTrdProductDealDto == null) {
				continue;
			}
			if (StringUtil.equals(mockTrdProductDealDto.getPs(), DictCodeList.交易方向_收息到账.getValue())
					|| StringUtil.equals(mockTrdProductDealDto.getPs(), DictCodeList.交易方向_还本到账.getValue())
					|| StringUtil.equals(mockTrdProductDealDto.getPs(), DictCodeList.交易方向_行权_回售.getValue())
					|| StringUtil.equals(mockTrdProductDealDto.getPs(), DictCodeList.交易方向_行权_赎回.getValue())
					|| StringUtil.equals(mockTrdProductDealDto.getPs(), DictCodeListExt3.交易方向_债转股.getValue())) {
				MockSingleAssetChanging 资产变动信息 = new MockSingleAssetChanging();
				// 清算款
				资产变动信息.setDelayPayAmt(mockTrdProductDealDto.getTradeAmt() == null ? BigDecimal.ZERO
						: mockTrdProductDealDto.getTradeAmt().negate());
				// 单资产变化赋值T+N
				RealTimeValCommonUtil.单资产变化赋值T_N(t_N单资产变化, DateUtil.format(mockTrdProductDealDto.getSettleDate()),
						资产变动信息);
				// 现金变化赋值T+N
				RealTimeValCommonUtil
						.现金变化赋值T_N(t_N现金变化, 基准日期, mockTrdProductDealDto.getCashProcessedOrNot(),
								new MockCashChangingKey(mockTrdProductDealDto.getSettleDate(),
										RealTimeValCommonUtil.现金类型_再投, mockTrdProductDealDto.getCcy()),
								mockTrdProductDealDto.getTradeAmt());
			}

			if (StringUtil.equals(mockTrdProductDealDto.getPs(), DictCodeList.交易方向_买入.getValue())
					|| StringUtil.equals(mockTrdProductDealDto.getPs(), DictCodeList.交易方向_申购确认.getValue())) {
				BigDecimal 交易成本 = BigDecimal.ZERO;
				// 投资目的 公允价值变动计入当期损益 FVTPL
				if (StringUtil.equals(DictCodeListExt1.投资目的_以出售该金融资产为目的.getValue(),
						mockTrdProductDealDto.getInvAim())) {
					// 交易成本
					交易成本 = ArithmeticUtil.doAdd(mockTrdProductDealDto.getPrinAmt(), mockTrdProductDealDto.getFeeAmt());
				} else {
					// 交易成本
					交易成本 = mockTrdProductDealDto.getPrinAmt();
				}
				MockSingleAssetChanging 资产变动信息 = new MockSingleAssetChanging();

				if (StringUtil.equals(债券T_1成本处理方式, RealTimeValCommonUtil.成本处理方式_交易日处理)) {
					// 清算款
					资产变动信息.setDelayPayAmt(mockTrdProductDealDto.getTradeAmt());
				} else {
					// 数量
					资产变动信息.setAmount(mockTrdProductDealDto.getShareAmt());
					// 成本
					资产变动信息.setCost(交易成本);
					// 利息
					资产变动信息.setInterest(mockTrdProductDealDto.getIntAmt());
					/**T+1模式，待清算款仍然出现问题处理  wupeng 修改 begin**/
					// 清算款
					资产变动信息.setDelayPayAmt(mockTrdProductDealDto.getTradeAmt());
					/**T+1模式，待清算款仍然出现问题处理  wupeng 修改 begin**/
				}

/*				if ((StringUtil.equals(DictCodeListExt1.交易场所_WIND_上交所.getValue(),
						mockTrdProductDealDto.getTradeMarket())
						|| StringUtil.equals(DictCodeListExt1.交易场所_WIND_深交所.getValue(),
						mockTrdProductDealDto.getTradeMarket())
						|| StringUtil.equals(DictCodeListExt1.估值交易市场_上交所.getValue(), mockTrdProductDealDto.getTradeMarket())
						|| StringUtil.equals(DictCodeListExt1.估值交易市场_深交所.getValue(), mockTrdProductDealDto.getTradeMarket()))
						&& StringUtil.equals(DictCodeListExt1.交易市场_二级市场.getValue(),
						mockTrdProductDealDto.getTradeMarketMode())
						&& StringUtil.equals(mockTrdProductDealDto.getPs(), DictCodeList.交易方向_买入.getValue())) {
					MockSingleAssetChanging 资产变动信息T_1 = new MockSingleAssetChanging();
					// 计提数量
					资产变动信息T_1.setAccrualAmount(mockTrdProductDealDto.getShareAmt());
					// 计提成本
					资产变动信息T_1.setAccrualCost(交易成本);
					// 计提利息
					资产变动信息T_1.setAccrualInterest(mockTrdProductDealDto.getIntAmt());
					// 单资产变化赋值T+N
					RealTimeValCommonUtil.单资产变化赋值T_N(t_N单资产变化,
							DateUtil.format(DateUtil.addDay(mockTrdProductDealDto.getSettleDate(), 1)), 资产变动信息T_1);
				} else {*/
					// 计提数量
					资产变动信息.setAccrualAmount(mockTrdProductDealDto.getShareAmt());
					// 计提成本
					资产变动信息.setAccrualCost(交易成本);
					// 计提利息
					资产变动信息.setAccrualInterest(mockTrdProductDealDto.getIntAmt());
//				}
				// 单资产变化赋值T+N
				RealTimeValCommonUtil.单资产变化赋值T_N(t_N单资产变化, DateUtil.format(mockTrdProductDealDto.getSettleDate()),
						资产变动信息);
				// 现金变化赋值T+N
				RealTimeValCommonUtil.现金变化赋值T_N(t_N现金变化, 基准日期, mockTrdProductDealDto.getCashProcessedOrNot(),
						new MockCashChangingKey(mockTrdProductDealDto.getSettleDate(), RealTimeValCommonUtil.现金类型_再投,
								mockTrdProductDealDto.getCcy()),
						mockTrdProductDealDto.getTradeAmt() == null ? BigDecimal.ZERO
								: mockTrdProductDealDto.getTradeAmt().negate());
			}
			// 交易方向为卖出
			if (StringUtil.equals(mockTrdProductDealDto.getPs(), DictCodeList.交易方向_卖出.getValue())) {
				// 利息总额
				BigDecimal 利息总额 = mockTrdProductDealDto.getIntAmt();
//				if (利息总额 == null || BigDecimal.ZERO.compareTo(利息总额) == 0) {
				if (StringUtil.equals(mockTrdProductDealDto.getPs(), DictCodeListExt1.交易方向_债转股.getValue())) {
					利息总额 = ArithmeticUtil.divide(
							ArithmeticUtil.multiply(mockTrdProductDealDto.getShareAmt(), 组合资产估值.getAccrualInt()),
							组合资产估值.getAccrualAmt(), RealTimeValCommonUtil.AMOUNT_SCALE);
				}
				MockSingleAssetChanging 资产变动信息 = new MockSingleAssetChanging();
				if (StringUtil.equals(债券T_1成本处理方式, RealTimeValCommonUtil.成本处理方式_交易日处理)) {
					// 清算款
					资产变动信息.setDelayPayAmt(mockTrdProductDealDto.getTradeAmt() == null ? BigDecimal.ZERO
							: mockTrdProductDealDto.getTradeAmt().negate());
				} else {
					// 数量
					资产变动信息.setAmount(mockTrdProductDealDto.getShareAmt() == null ? BigDecimal.ZERO
							: mockTrdProductDealDto.getShareAmt().negate());
					// 成本
					资产变动信息.setCost(ArithmeticUtil.divide(
							ArithmeticUtil.multiply(mockTrdProductDealDto.getShareAmt(), 组合资产估值.getAccrualCost()),
							组合资产估值.getAccrualAmt(), RealTimeValCommonUtil.AMOUNT_SCALE).negate());
					// 利息
					资产变动信息.setInterest(利息总额.negate());
					// 公允价值
					资产变动信息.setTdyFloatIngpl(ArithmeticUtil.divide(
							ArithmeticUtil.multiply(mockTrdProductDealDto.getShareAmt(), 组合资产估值.getTdyFloatIngpl()),
							组合资产估值.getShareAmt(), RealTimeValCommonUtil.AMOUNT_SCALE).negate());
				}
				// if (交易场所 = "上交所" or "深交所") && 交易市场 ="二级市场" && 交易方向 = "卖出"
/*				if ((StringUtil.equals(DictCodeListExt1.交易场所_WIND_上交所.getValue(),
						mockTrdProductDealDto.getTradeMarket())
						|| StringUtil.equals(DictCodeListExt1.交易场所_WIND_深交所.getValue(),
						mockTrdProductDealDto.getTradeMarket())
						|| StringUtil.equals(DictCodeListExt1.估值交易市场_上交所.getValue(), mockTrdProductDealDto.getTradeMarket())
						|| StringUtil.equals(DictCodeListExt1.估值交易市场_深交所.getValue(), mockTrdProductDealDto.getTradeMarket()))
						&& StringUtil.equals(DictCodeListExt1.交易市场_二级市场.getValue(),
						mockTrdProductDealDto.getTradeMarketMode())
						&& StringUtil.equals(mockTrdProductDealDto.getPs(), DictCodeList.交易方向_卖出.getValue())) {
					MockSingleAssetChanging 资产变动信息T_1 = new MockSingleAssetChanging();
					// 计提数量
					资产变动信息T_1.setAccrualAmount(mockTrdProductDealDto.getShareAmt() == null ? BigDecimal.ZERO
							: mockTrdProductDealDto.getShareAmt().negate());
					// 计提成本
					资产变动信息T_1.setAccrualCost(ArithmeticUtil.divide(
							ArithmeticUtil.multiply(mockTrdProductDealDto.getShareAmt(), 组合资产估值.getAccrualCost()),
							组合资产估值.getAccrualAmt(), RealTimeValCommonUtil.AMOUNT_SCALE).negate());
					// 计提利息
					资产变动信息T_1.setAccrualInterest(利息总额.negate());
					// 公允价值
					资产变动信息T_1.setTdyFloatIngpl(ArithmeticUtil.divide(
							ArithmeticUtil.multiply(mockTrdProductDealDto.getShareAmt(), 组合资产估值.getTdyFloatIngpl()),
							组合资产估值.getShareAmt(), RealTimeValCommonUtil.AMOUNT_SCALE).negate());
					// 单资产变化赋值T+N
					RealTimeValCommonUtil.单资产变化赋值T_N(t_N单资产变化,
							DateUtil.format(DateUtil.addDay(mockTrdProductDealDto.getSettleDate(), 1)), 资产变动信息T_1);
				} else {*/
					// 计提数量
					资产变动信息.setAccrualAmount(mockTrdProductDealDto.getShareAmt() == null ? BigDecimal.ZERO
							: mockTrdProductDealDto.getShareAmt().negate());
					// 计提成本
					资产变动信息.setAccrualCost(ArithmeticUtil.divide(
							ArithmeticUtil.multiply(mockTrdProductDealDto.getShareAmt(), 组合资产估值.getAccrualCost()),
							组合资产估值.getAccrualAmt(), RealTimeValCommonUtil.AMOUNT_SCALE).negate());
					// 计提利息
					资产变动信息.setAccrualInterest(利息总额.negate());
					// 公允价值
					资产变动信息.setTdyFloatIngpl(ArithmeticUtil.divide(
							ArithmeticUtil.multiply(mockTrdProductDealDto.getShareAmt(), 组合资产估值.getTdyFloatIngpl()),
							组合资产估值.getShareAmt(), RealTimeValCommonUtil.AMOUNT_SCALE).negate());
//				}
				// 单资产变化赋值T+N
				RealTimeValCommonUtil.单资产变化赋值T_N(t_N单资产变化, DateUtil.format(mockTrdProductDealDto.getSettleDate()),
						资产变动信息);
				// 现金变化赋值T+N
				RealTimeValCommonUtil.现金变化赋值T_N(t_N现金变化, 基准日期, mockTrdProductDealDto.getCashProcessedOrNot(),
						new MockCashChangingKey(mockTrdProductDealDto.getSettleDate(), RealTimeValCommonUtil.现金类型_再投,
								mockTrdProductDealDto.getCcy()),
						mockTrdProductDealDto.getTradeAmt() == null ? BigDecimal.ZERO
								: mockTrdProductDealDto.getTradeAmt().negate());
			}
		}
	}

	/**
	 * 债券利息计提方法 <功能详细描述>
	 *
	 * @param 计算日期
	 * @param 资产信息
	 * @param 付息计划列表
	 * @param 报价列表
	 * @param 支付规则
	 * @param 债券百元面值
	 * @return 计提利息
	 * @see
	 * @since 1.0
	 */
	private static BigDecimal calc债券利息(Date 计算日期, FinProduct 资产信息, List<MockFinCashPlanDto> 付息计划列表,
									   List<FinCashCalcRule> 报价列表, List<FinCashPayRule> 支付规则, BigDecimal 债券百元面值) {
		// 组装参数
		AccurIntCalInParameter parameter = new AccurIntCalInParameter();
		// 计算日
		parameter.setCalDate(BasicUtil.dateToLocal(计算日期));
		parameter.setSecInfoModel(gen资产信息组装(资产信息, 付息计划列表, 报价列表, 支付规则));
		// 付息计划列表
		List<SecIntSchedule> secIntScheduleList = new ArrayList<SecIntSchedule>();
		if (付息计划列表 != null && 付息计划列表.size() > 0) {
			for (MockFinCashPlanDto mockFinCashPlanDto : 付息计划列表) {
				SecIntSchedule secIntSchedule = new SecIntSchedule();
				// 理论起始日
				secIntSchedule.setTheoryStartDate(mockFinCashPlanDto.getFinCashPlan().getVdateTerm() == null
						? BasicUtil.dateToLocal(mockFinCashPlanDto.getFinCashPlan().getVdate())
						: BasicUtil.dateToLocal(mockFinCashPlanDto.getFinCashPlan().getVdateTerm()));
				// 理论截止日
				secIntSchedule.setTheoryCutoffDate(mockFinCashPlanDto.getFinCashPlan().getMdateTerm() == null
						? BasicUtil.dateToLocal(mockFinCashPlanDto.getFinCashPlan().getMdate())
						: BasicUtil.dateToLocal(mockFinCashPlanDto.getFinCashPlan().getMdateTerm()));
				// 实际起始日
				secIntSchedule
						.setActualStartDate(BasicUtil.dateToLocal(mockFinCashPlanDto.getFinCashPlan().getVdate()));
				// 实际截止日
				secIntSchedule
						.setActualCutoffDate(BasicUtil.dateToLocal(mockFinCashPlanDto.getFinCashPlan().getMdate()));
				// 计息本金
				secIntSchedule.setCorpus(new BigDecimal(100));
				// 利息金额
				secIntSchedule.setAmount(mockFinCashPlanDto.getFinCashPlan().getCashAmt());
				secIntScheduleList.add(secIntSchedule);
			}
			parameter.setSecIntScheduleList(secIntScheduleList);
		}

		// 债券利率重置信息
		List<PaperIrReset> paperIrResetList = new ArrayList<PaperIrReset>();
		if (报价列表 != null && 报价列表.size() > 0) {
			for (FinCashCalcRule finCashCalcRule : 报价列表) {
				PaperIrReset paperIrReset = new PaperIrReset();
				// 票面利率生效日
				paperIrReset.setParRateEffDate(BasicUtil.dateToLocal(finCashCalcRule.getEffDate()));
				// 票面利率
				paperIrReset.setParRate(finCashCalcRule.getYield());
				paperIrResetList.add(paperIrReset);
			}
			parameter.setPaperIrResetList(paperIrResetList);
		}

		// 当前面值
		parameter.setPaperAmt(债券百元面值);
		// 计算百元面值的债券的利息
		AccurIntForBookCalculator calculator2 = CalculatorFactory.createAccurIntForBookCalculator();
		AccurIntCalOutParameter output2 = calculator2.calculate(parameter);
		return output2.getInterest();
	}

	/**
	 * 债券估值 摊销日利率 <功能详细描述>
	 *
	 * @param 计算日期
	 * @param 券面总额
	 * @param 摊销成本净价总额
	 * @param 当日百元券面值
	 * @param 票面利率
	 * @param 资产信息
	 * @param 付息计划列表
	 * @param 报价列表
	 * @param 支付规则
	 * @param 行权计划
	 * @param 行权日百元券面
	 * @return 摊销日利率
	 * @see
	 * @since 1.0
	 */
	private static BigDecimal calc摊销日利率(Date 计算日期, BigDecimal 券面总额, BigDecimal 摊销成本净价总额, BigDecimal 当日百元券面值,
										BigDecimal 票面利率, FinProduct 资产信息, List<MockFinCashPlanDto> 付息计划列表, List<FinCashCalcRule> 报价列表,
										List<FinCashPayRule> 支付规则, List<FinExercisePlan> 行权计划, BigDecimal 行权日百元券面) {

		SecDscYieldInParameter parameter = new SecDscYieldInParameter();
		// 计算日
		parameter.setCalDate(BasicUtil.dateToLocal(计算日期));
		// 券面总额
		parameter.setTotalMoney(ArithmeticUtil.multiply(券面总额,new BigDecimal(100)));
		// 摊余成本净价总额
		parameter.setTotalNetPrice(摊销成本净价总额);
		// 当日百元券面面值
		parameter.setAmount(当日百元券面值);
		// 票面利率
		parameter.setPaperRate(票面利率);
		// 零息贴现计算方案
		parameter.setPlan(com.joyintech.calculation.bonds.constant.GlobalConstant.DIS_DEBT_SCHEME_A);
		// 资产信息
		parameter.setSecInfoModel(gen资产信息组装(资产信息, 付息计划列表, 报价列表, 支付规则));

		// 付息计划
		List<SecIntSchedule> secIntScheduleList = new ArrayList<SecIntSchedule>();
		if (付息计划列表 != null && 付息计划列表.size() > 0) {
			for (MockFinCashPlanDto mockFinCashPlanDto : 付息计划列表) {
				SecIntSchedule secIntSchedule = new SecIntSchedule();
				// 理论起始日
				secIntSchedule.setTheoryStartDate(mockFinCashPlanDto.getFinCashPlan().getVdateTerm() == null
						? BasicUtil.dateToLocal(mockFinCashPlanDto.getFinCashPlan().getVdate())
						: BasicUtil.dateToLocal(mockFinCashPlanDto.getFinCashPlan().getVdateTerm()));
				// 理论截止日
				secIntSchedule.setTheoryCutoffDate(mockFinCashPlanDto.getFinCashPlan().getMdateTerm() == null
						? BasicUtil.dateToLocal(mockFinCashPlanDto.getFinCashPlan().getMdate())
						: BasicUtil.dateToLocal(mockFinCashPlanDto.getFinCashPlan().getMdateTerm()));
				// 实际起始日
				secIntSchedule
						.setActualStartDate(BasicUtil.dateToLocal(mockFinCashPlanDto.getFinCashPlan().getVdate()));
				// 实际截止日
				secIntSchedule
						.setActualCutoffDate(BasicUtil.dateToLocal(mockFinCashPlanDto.getFinCashPlan().getMdate()));
				// 计息本金
				secIntSchedule.setCorpus(new BigDecimal(100));
				// 利息金额
				secIntSchedule.setAmount(mockFinCashPlanDto.getFinCashPlan().getCashAmt());
				secIntScheduleList.add(secIntSchedule);
			}
			parameter.setSecIntScheduleList(secIntScheduleList);
		}
		String exeDate = 资产信息.getExeDate();
		if (StringUtil.isNotEmpty(exeDate)
				&& !DictCodeList.含权债摊销日_到期日.getValue().equals(exeDate)) {
			// 变量.行权计划不为空时传入
			if (!StringUtil.isEmptyList(行权计划) && 行权计划.get(0) != null) {
				// 下一行权日 变量.行权计划.计划行权日
				parameter.setNextExeDate(BasicUtil.dateToLocal(行权计划.get(0).getExerciseDate()));
				// 行权价 变量.行权计划.行权价
				parameter.setNextExePrice(行权计划.get(0).getExercisePrice());
				// 行权日百元券面 变量.行权日百元券面
				parameter.setNextExeAmount(行权日百元券面);
				// 摊销价格模式 系统参数：DSC_TRAGEPRICE_MODE
				parameter.setExePlan(NextExeCalMethod.parse(CacheUtil.getSysParam("DSC_TRAGEPRICE_MODE")));
			}
		}
		SecDscYieldCalculator calculator = CalculatorFactory.createSecDscYieldCalculator();
		SecDscYieldOutParameter output = calculator.calculate(parameter);
		output.setDayRate(output.getDayRate().setScale(12, RoundingMode.HALF_UP));
		return output.getDayRate();
	}

	/**
	 * 债券估值 资产信息组装 <功能详细描述>
	 *
	 * @param 资产信息
	 * @param 付息计划列表
	 * @param 报价列表
	 * @param 支付规则
	 * @return SecInfoModel
	 * @see
	 * @since 1.0
	 */
	private static SecInfoModel gen资产信息组装(FinProduct 资产信息, List<MockFinCashPlanDto> 付息计划列表, List<FinCashCalcRule> 报价列表,
										  List<FinCashPayRule> 支付规则) {
		// 资产信息
		SecInfoModel secInfoModel = new SecInfoModel();
		// 债券起息日
		secInfoModel.setStartDate(BasicUtil.dateToLocal(资产信息.getVdate()));
		// 债券到期日
		secInfoModel.setEndDate(BasicUtil.dateToLocal(资产信息.getActmdate()));
		// 债券首次付息日
		Date firstPayDate = 支付规则 != null && 支付规则.size() > 0 ? 支付规则.get(0).getFirstPayDate() : null;
		secInfoModel.setFirstPayDate(BasicUtil.dateToLocal(firstPayDate));
		// 发行价
		secInfoModel.setIssuePrice(资产信息.getIssuePrice());
		// 债券类型
		Variety bondsType = null;
		if (DictCodeList.息票品种_贴现.getValue().equals(资产信息.getCouponSpecies())) {
			bondsType = Variety.贴现;
		} else if (DictCodeList.息票品种_零息.getValue().equals(资产信息.getCouponSpecies())) {
			bondsType = Variety.零息;
		} else if (DictCodeList.息票品种_附息.getValue().equals(资产信息.getCouponSpecies())) {
			bondsType = Variety.附息;
		} else if (DictCodeList.息票品种_利随本清.getValue().equals(资产信息.getCouponSpecies())) {
			bondsType = Variety.利随本清;
		}
		secInfoModel.setBondsType(bondsType);
		// 计息基础
		Basis basis = null;
		String basisString = 报价列表 != null && 报价列表.size() > 0 ? 报价列表.get(0).getBasis() : null;
		if (DictCodeList.计息基础_A_360.getValue().equals(basisString)) {
			basis = Basis.A_360;
		} else if (DictCodeList.计息基础_A_365.getValue().equals(basisString)) {
			basis = Basis.A_365;
		} else if (DictCodeList.计息基础_A_A_平均分配.getValue().equals(basisString)
				|| DictCodeList.计息基础_A_A_按实际天数分配.getValue().equals(basisString)) {
			basis = Basis.A_A;
		} else if (DictCodeList.计息基础_A_365F.getValue().equals(basisString)) {
			basis = Basis.A_365F;
		} else if (DictCodeList.计息基础_30_360.getValue().equals(basisString)) {
			basis = Basis._30_360;
		} else if (DictCodeListExt3.计息基础_30U_360.getValue().equals(basisString)) {
			basis = Basis._30U_360;
		}

		secInfoModel.setBasis(basis);
		// 利率类型
		IntRateType intRateType = null;
		String intRateTypeString = 报价列表 != null && 报价列表.size() > 0 ? 报价列表.get(0).getCalcType() : null;
		if (DictCodeList.利率类型_固定利率.getValue().equals(intRateTypeString)) {
			intRateType = IntRateType.固定利率;
		} else if (DictCodeList.利率类型_浮动利率.getValue().equals(intRateTypeString)) {
			intRateType = IntRateType.浮动利率;
		}
		secInfoModel.setIntRateType(intRateType);
		// 利息分配方式
		AllocationType allocationType = null;
		if (DictCodeList.计息基础_A_A_平均分配.getValue().equals(basisString)) {
			allocationType = AllocationType.平均分配;
		} else if (DictCodeList.计息基础_A_A_按实际天数分配.getValue().equals(basisString)) {
			allocationType = AllocationType.按实际天数分配;
		} else {
			allocationType = AllocationType.按实际天数分配;
		}
		secInfoModel.setAllocationType(allocationType);
		// 付息计划推算方式
		SecIntSchCalMethod secIntSchCalMethod = null;
		String secIntSchCalMethodString = 支付规则 != null && 支付规则.size() > 0 ? 支付规则.get(0).getCalcRule() : null;
		if (DictCodeList.推算方式_起息日向后.getValue().equals(secIntSchCalMethodString)) {
			secIntSchCalMethod = SecIntSchCalMethod.起息日向后;
		} else if (DictCodeList.推算方式_到期日向前.getValue().equals(secIntSchCalMethodString)) {
			secIntSchCalMethod = SecIntSchCalMethod.到期日向前;
		} else if (DictCodeList.推算方式_首次支付日.getValue().equals(secIntSchCalMethodString)) {
			secIntSchCalMethod = SecIntSchCalMethod.首次付息日;
		}
		secInfoModel.setCalMethod(secIntSchCalMethod);
		// 付息频率
		IntFrequency yearIntRate = null;
		String yearIntRateString = 支付规则 != null && 支付规则.size() > 0 ? 支付规则.get(0).getPayCycle() : null;
		if (DictCodeList.频率_月.getValue().equals(yearIntRateString)) {
			yearIntRate = IntFrequency.月付;
		} else if (DictCodeList.频率_季.getValue().equals(yearIntRateString)) {
			yearIntRate = IntFrequency.季付;
		} else if (DictCodeList.频率_年.getValue().equals(yearIntRateString)) {
			yearIntRate = IntFrequency.年付;
		} else if (DictCodeList.频率_半年.getValue().equals(yearIntRateString)) {
			yearIntRate = IntFrequency.半年付;
		} else {
			yearIntRate = IntFrequency.年付;
		}
		secInfoModel.setYearIntRate(yearIntRate);
		return secInfoModel;
	}
}
