/*
 * 文件名：FinPayPlanAdjustmentService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：huwenbin
 * 修改时间：2018年10月18日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.service.product.messageadjust.finpayplanadjustment.service;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.service.product.common.dao.FinProductFloatFeeMapper;
import com.joyintech.service.product.messageadjust.finpayplanadjustment.dao.FinPayPlanAdjustmentMapper;
import com.joyintech.service.product.messageadjust.finpayplanadjustment.dto.FinPayRuleAdjustmentDto;
import com.joyintech.tams.common.base.dao.FinCashCalcRuleMapper;
import com.joyintech.tams.common.base.dao.FinCashInfoMapper;
import com.joyintech.tams.common.base.dao.FinCashPayRuleMapper;
import com.joyintech.tams.common.base.dao.FinCashPlanMapper;
import com.joyintech.tams.common.base.dao.FinFloatCountingRuleMapper;
import com.joyintech.tams.common.base.dao.FinProductMapper;
import com.joyintech.tams.common.base.dao.FinRateInfoMapper;
import com.joyintech.tams.common.base.entity.FinCashCalcRule;
import com.joyintech.tams.common.base.entity.FinCashCalcRuleKey;
import com.joyintech.tams.common.base.entity.FinCashInfo;
import com.joyintech.tams.common.base.entity.FinCashPayRule;
import com.joyintech.tams.common.base.entity.FinCashPayRuleKey;
import com.joyintech.tams.common.base.entity.FinCashPlan;
import com.joyintech.tams.common.base.entity.FinCashPlanKey;
import com.joyintech.tams.common.base.entity.FinFloatCountingRule;
import com.joyintech.tams.common.base.entity.FinFloatCountingRuleKey;
import com.joyintech.tams.common.base.entity.FinProduct;
import com.joyintech.tams.common.base.entity.FinRateInfo;
import com.joyintech.tams.common.base.entity.FinRateInfoKey;
import com.joyintech.tams.component.base.core.impl.dao.BaseFinProductCashCalcRuleMapper;
import com.joyintech.tams.component.base.core.impl.dao.BaseFinProductCashCountingRuleMapper;
import com.joyintech.tams.component.base.core.impl.dao.BaseFinProductCashRateRuleMapper;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.invest.common.CommonUtil;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tams.invest.common.constant.BussinessConstant;

/**
 * 信息调整管理_付费规则调整 〈功能详细描述〉
 * 
 * @author yaozhenguo
 * @version 1.0 2018年10月18日
 * @see FinPayRuleAdjustmentService
 * @since 1.0
 */
@Service("backFinPayRuleAdjustmentService")
public class FinPayRuleAdjustmentService extends BaseService<FinPayRuleAdjustmentDto> {

	Logger logger = LoggerFactory.getLogger(this.getClass());
	/**
	 * 
	 */
	@Autowired
	private FinPayPlanAdjustmentMapper finPayPlanAdjustmentMapper;
	@Autowired
	private FinProductMapper finProductMapper;
	@Autowired
	private FinCashInfoMapper finCashInfoMapper;
	@Autowired
	private FinCashPayRuleMapper finCashPayRuleMapper;
	@Autowired
	private BaseFinProductCashCalcRuleMapper baseFinProductCashCalcRuleMapper;
	@Autowired
	private FinCashPlanMapper finCashPlanMapper;
	@Autowired
	private BaseFinProductCashCountingRuleMapper baseFinProductCashCountingRuleMapper;
	@Autowired
	private BaseFinProductCashRateRuleMapper baseFinProductCashRateRuleMapper;
	@Autowired
	private FinCashCalcRuleMapper finCashCalcRuleMapper;
	@Autowired
	private FinFloatCountingRuleMapper finFloatCountingRuleMapper;
	@Autowired
	private FinRateInfoMapper finRateInfoMapper;
	@Autowired
	private FinProductFloatFeeMapper finProductFloatFeeMapper;
	@Autowired
    private IPlatformDate iPlatformDate;

	/**
	 * 根据金融产品代码查询费用列表信息 yzg 20191127
	 * 
	 * @param finprodId
	 *            金融产品代码
	 * @return 费用列表信息
	 */
	public List<FinPayRuleAdjustmentDto> queryFeeInfoByFinprodId(Map<String, Object> param) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("finprodId", String.valueOf(param.get("finprodId")));
		map.put("finprodType2", DictCodeListExt1.金融产品类型_投管用_系列模板.getValue());
		map.put("cashType1", DictCodeListExt1.现金流一级类型_费用.getValue());

		map.put("feeTypeTrust", DictCodeList.费用品种类型_托管费.getValue());
		// 新增投资顾问费 add by chendijun 2019/10/15
		map.put("feeTypeInvest", DictCodeListExt2.费用品种类型_投资顾问费.getValue());
		map.put("feeTypeFix", DictCodeList.费用品种类型_固定管理费.getValue());
		map.put("feeTypeSale", DictCodeList.费用品种类型_销售手续费.getValue());
		map.put("feeTypeWBFWF", DictCodeList.费用品种类型_外包服务费.getValue());
		// 一体化对应etliu end
		map.put("feeType10", DictCodeList.费用品种类型_通道托管费.getValue());
		map.put("feeType11", DictCodeList.费用品种类型_通道管理费.getValue());
		map.put("feeType99", DictCodeList.费用品种类型_其他费用.getValue());
		map.put("finprodType", DictCodeListExt1.现金流业务类型_金融产品.getValue());
		map.put("feeTypeOthSale", DictCodeList.费用品种类型_代销费.getValue());
		map.put("feeTypeThService", DictCodeList.费用品种类型_外包服务费.getValue());
		return finPayPlanAdjustmentMapper.queryFeeInfoByFinprodId(map);
	}

	/**
	 * 产品款型下拉框
	 * 
	 * @param page
	 * @param length
	 * @param param
	 * @return
	 */
	public Page<SelectOptionDto> getSelectProdStyle(int page, int length, Map<String, Object> param) {
		param.put("cashType1_03", DictCodeListExt1.现金流一级类型_费用.getValue());
		param.put("pfinprodId", DictCodeListExt1.金融产品类型_理财产品.getValue() + "_" + param.get("finprodMarketId"));
		return super.queryForPageForSelectReturn(page, length,
				() -> finPayPlanAdjustmentMapper.getSelectProdStyle(param));

	}

	/**
	 * 产品费用信息
	 * 
	 * @param finPayRuleAdjustmentDto
	 */
	public void insertFinProductFeeInfo(FinPayRuleAdjustmentDto entity) throws InvocationTargetException, IllegalAccessException {
		Long branch = Long.parseLong(CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE));
		if (entity.getSelectedItem() != null) {
			// 分层产品款型为空所有子产品都使用该费用
			List<FinPayRuleAdjustmentDto> list = new ArrayList<>();
			list.addAll(entity.getSelectedItem());
			for (FinPayRuleAdjustmentDto finPayRuleAdjustmentDto : entity.getSelectedItem()) {
				if (StringUtil.equals(entity.getIsLay(), "Y")
						&& StringUtil.isEmpty(finPayRuleAdjustmentDto.getProdStyle())) {
					List<FinProduct> finProducts = selectLayProductByFinproductId(entity.getFinprodId());
					if (!StringUtil.isEmptyList(finProducts)) {
						for (FinProduct finProduct : finProducts) {
							FinPayRuleAdjustmentDto finPayRule = new FinPayRuleAdjustmentDto();
							BeanUtil.copyProperties(finPayRule, finPayRuleAdjustmentDto);
							finPayRule.setProdStyle(finProduct.getFinprodId());
							list.add(finPayRule);
						}
					}
				}
			}
			for (FinPayRuleAdjustmentDto finPayRuleAdjustmentDto : list) {
				// 分层产品使用子产品Id
				if (StringUtil.equals(entity.getIsLay(), "Y")) {
					if (StringUtil.isNotEmpty(finPayRuleAdjustmentDto.getProdStyle())) {
						entity.setFinprodId(finPayRuleAdjustmentDto.getProdStyle());
					} else {
						continue;
					}
				}
				if (StringUtil.isEmpty(finPayRuleAdjustmentDto.getCashId())) {
					String cashId_fee = "";
					String cashType2 = "";
					// 托管费
					if (StringUtil.equals(DictCodeList.费用品种类型_托管费.getCodeName(), finPayRuleAdjustmentDto.getFeeType())
							|| StringUtil.equals(DictCodeList.费用品种类型_托管费.getValue(),
									finPayRuleAdjustmentDto.getFeeType())) {
						cashId_fee = CommonUtil.generateCashFlowCode(entity.getFinprodId(),
								DictCodeListExt1.现金流业务类型_金融产品.getValue(), branch,
								DictCodeListExt1.现金流二级类型_托管费.getValue());
						cashType2 = DictCodeListExt1.现金流二级类型_托管费.getValue();
					}
					// 固定管理费
					else if (StringUtil.equals(DictCodeList.费用品种类型_固定管理费.getCodeName(),
							finPayRuleAdjustmentDto.getFeeType())
							|| StringUtil.equals(DictCodeList.费用品种类型_固定管理费.getValue(),
									finPayRuleAdjustmentDto.getFeeType())) {
						cashId_fee = CommonUtil.generateCashFlowCode(entity.getFinprodId(),
								DictCodeListExt1.现金流业务类型_金融产品.getValue(), branch,
								DictCodeListExt1.现金流二级类型_固定管理费.getValue());
						cashType2 = DictCodeListExt1.现金流二级类型_固定管理费.getValue();
					}
					// 销售费
					else if (StringUtil.equals(DictCodeList.费用品种类型_销售手续费.getCodeName(),
							finPayRuleAdjustmentDto.getFeeType())
							|| StringUtil.equals(DictCodeList.费用品种类型_销售手续费.getValue(),
									finPayRuleAdjustmentDto.getFeeType())) {
						cashId_fee = CommonUtil.generateCashFlowCode(entity.getFinprodId(),
								DictCodeListExt1.现金流业务类型_金融产品.getValue(), branch,
								DictCodeListExt1.现金流二级类型_销售费.getValue());
						cashType2 = DictCodeListExt1.现金流二级类型_销售费.getValue();
					}
					// 一体化对应etliu start
					else if (StringUtil.equals(DictCodeList.费用品种类型_外包服务费.getCodeName(),
							finPayRuleAdjustmentDto.getFeeType())
							|| StringUtil.equals(DictCodeList.费用品种类型_外包服务费.getValue(),
									finPayRuleAdjustmentDto.getFeeType())) {
						cashId_fee = CommonUtil.generateCashFlowCode(entity.getFinprodId(),
								DictCodeListExt1.现金流业务类型_金融产品.getValue(), branch,
								DictCodeListExt1.现金流二级类型_外包服务费.getValue());
						cashType2 = DictCodeListExt1.现金流二级类型_外包服务费.getValue();
					}
					// 代销费
					else if (StringUtil.equals(DictCodeList.费用品种类型_代销费.getCodeName(),
							finPayRuleAdjustmentDto.getFeeType())
							|| StringUtil.equals(DictCodeList.费用品种类型_代销费.getValue(),
									finPayRuleAdjustmentDto.getFeeType())) {
						cashId_fee = CommonUtil.generateCashFlowCode(entity.getFinprodId(),
								DictCodeListExt1.现金流业务类型_金融产品.getValue(), branch,
								DictCodeListExt1.现金流二级类型_代销费.getValue());
						cashType2 = DictCodeListExt1.现金流二级类型_代销费.getValue();
					}
					// 投资顾问费
					else if (StringUtil.equals(DictCodeListExt2.费用品种类型_投资顾问费.getCodeName(),
							finPayRuleAdjustmentDto.getFeeType())
							|| StringUtil.equals(DictCodeListExt2.费用品种类型_投资顾问费.getValue(),
									finPayRuleAdjustmentDto.getFeeType())) {
						cashId_fee = CommonUtil.generateCashFlowCode(entity.getFinprodId(),
								DictCodeListExt1.现金流业务类型_金融产品.getValue(), branch,
								DictCodeListExt2.现金流二级类型_投资顾问费.getValue());
						cashType2 = DictCodeListExt2.现金流二级类型_投资顾问费.getValue();
					}
					// 一体化对应etliu end
					if (StringUtil.isEmpty(cashId_fee)) {
						logger.error("当前费用类型不在有效范围内:托管费、固定管理费、销售手续费、外包服务费、代销费、投资顾问费，当前费用类型是："
								+ finPayRuleAdjustmentDto.getFeeType());
					} else {
						// 新建
						FinProduct finProduct = finProductMapper.selectByPrimaryKey(entity.getFinprodId());
						// 现金流信息
						FinCashInfo finCashInfo_int = finCashInfoMapper
								.selectByPrimaryKey(cashId_fee);
						if (finCashInfo_int != null) {
							finCashInfoMapper.deleteByPrimaryKey(cashId_fee);
						}else {
							finCashInfo_int = new FinCashInfo();
							BeanUtil.copyProperties(finCashInfo_int, finProduct);
						}
						finCashInfo_int.setCashId(cashId_fee);
						finCashInfo_int.setCashType1(DictCodeListExt1.现金流一级类型_费用.getValue());
						finCashInfo_int.setCashType2(cashType2);
						finCashInfo_int.setCcy(finProduct.getCcy());
						finCashInfo_int.setFinprodId(finProduct.getFinprodId());
						finCashInfo_int.setFinprodType(DictCodeListExt1.现金流业务类型_金融产品.getValue());
						finCashInfo_int.setBranch(branch);
						finCashInfo_int.setFeeId(finPayRuleAdjustmentDto.getFeeId());
						// zhangnn 2019/2/27 start
						// 计提费用 是否参与计算 默认 Y
						if (DictCodeList.收费类型_计提式费用.getValue().equals(finPayRuleAdjustmentDto.getChargeType())) {
							finCashInfo_int.setIsCalc(DictCodeList.是否_是.getValue());
						} else {
							finCashInfo_int.setIsCalc(DictCodeList.是否_否.getValue());
						}
						finCashInfoMapper.insert(finCashInfo_int);

						// 现金流支付规则
						FinCashPayRule finCashPayRule = new FinCashPayRule();
						BeanUtil.copyProperties(finCashPayRule, finProduct);
						finCashPayRule.setCashId(cashId_fee);
						if (StringUtil.isNotEmpty(finPayRuleAdjustmentDto.getProvisionFirstDay())) {
							finCashPayRule.setEffDate(finPayRuleAdjustmentDto.getProvisionFirstDay());
						} else {
							//finCashPayRule.setEffDate(DateUtil.platDateFromCache());
							//modify by cxq 20200116
							finCashPayRule.setEffDate(iPlatformDate.getSysDate());
						}
						finCashPayRule.setFirstPayDate(finPayRuleAdjustmentDto.getFirstCalEndDate());// add by
																										// zhangweiqi
																										// 首次计费结束日
						finCashPayRule.setPayCycle(finPayRuleAdjustmentDto.getPayFeeRule());
						finCashPayRule.setEndTdayRule(finPayRuleAdjustmentDto.getCalcFeeTradeRule());
						finCashPayRule.setPayTdayRule(finPayRuleAdjustmentDto.getPayFeeTradeRule());
						finCashPayRule.setCashUnitType(DictCodeList.现金流单位_总额.getValue());
						finCashPayRule.setFinprodId(finProduct.getFinprodId());
						finCashPayRule.setFinprodType(finProduct.getFinprodType());
						finCashPayRule.setFinprodType2(finProduct.getFinprodType2());
						finCashPayRule.setBranch(branch);
						finCashPayRuleMapper.insert(finCashPayRule);

						// 现金流计算规则
						FinCashCalcRule finCashCalcRule = new FinCashCalcRule();
						BeanUtil.copyProperties(finCashCalcRule, finProduct);
						finCashCalcRule.setCashId(cashId_fee);
						if (StringUtil.isNotEmpty(finPayRuleAdjustmentDto.getProvisionFirstDay())) {
							finCashCalcRule.setEffDate(finPayRuleAdjustmentDto.getProvisionFirstDay());
						} else {
							finCashCalcRule.setEffDate(DateUtil.platDateFromCache());
						}
						finCashCalcRule.setCalcType(DictCodeList.利率类型_固定利率.getValue());
						finCashCalcRule.setBaseType(finPayRuleAdjustmentDto.getBasicBill());
						finCashCalcRule.setBaseDateType(DictCodeList.基数日期类型_当日.getValue());
						if (finPayRuleAdjustmentDto.getNetAssetValueDay() != null) {
							finCashCalcRule.setBaseDateType(finPayRuleAdjustmentDto.getNetAssetValueDay());
						}

						// 一体化对应etliu start
						if (StringUtil.isBlank(finCashCalcRule.getBaseDateType())) {
							finCashCalcRule.setBaseDateType(DictCodeList.基数日期类型_当日.getValue());
						}
						// 一体化对应etliu end

						finCashCalcRule.setBasis(finPayRuleAdjustmentDto.getBasis());
						finCashCalcRule.setYield(ArithmeticUtil.divide(finPayRuleAdjustmentDto.getFeeRate(), 100, 14));
						finCashCalcRule.setIsInitial(DictCodeList.是否_是.getValue());
						finCashCalcRule.setFinprodId(finProduct.getFinprodId());
						finCashCalcRule.setFinprodType(finProduct.getFinprodType());
						finCashCalcRule.setFinprodType2(finProduct.getFinprodType2());
						finCashCalcRule.setBranch(branch);
						baseFinProductCashCalcRuleMapper.insert(finCashCalcRule);

						FinCashCalcRuleKey finCashCalcRuleKey = new FinCashCalcRuleKey();
						finCashCalcRuleKey.setCashId(finCashCalcRule.getCashId());
						finCashCalcRuleKey.setEffDate(finProduct.getVdate());
						FinCashCalcRule finCashCalcRuleVdate = finCashCalcRuleMapper.selectByPrimaryKey(finCashCalcRuleKey);
						//新建费用起息日不存在计算规则插入一条生效日为起息日费率为0的计算规则
						if(finCashCalcRuleVdate == null) {
							finCashCalcRule.setEffDate(finProduct.getVdate());
							finCashCalcRule.setYield(new BigDecimal(0));
							baseFinProductCashCalcRuleMapper.insert(finCashCalcRule);
						}
						
						// 现金流计提规则
						FinFloatCountingRule finCashCountingRule = new FinFloatCountingRule();
						BeanUtil.copyProperties(finCashCountingRule, finProduct);
						finCashCountingRule.setCashId(cashId_fee);
						if (StringUtil.isNotEmpty(finPayRuleAdjustmentDto.getProvisionFirstDay())) {
							finCashCountingRule.setEffDate(finPayRuleAdjustmentDto.getProvisionFirstDay());
						} else {
							finCashCountingRule.setEffDate(DateUtil.platDateFromCache());
						}
						finCashCountingRule.setAccrType(DictCodeList.计提类型_高频.getValue());
						finCashCountingRule.setAccrCycle(new Long(1));
						finCashCountingRule.setAccrCycleUnit(DictCodeListExt1.周期单位_日.getValue());
						finCashCountingRule.setfAccrDate(finPayRuleAdjustmentDto.getProvisionFirstDay());
						finCashCountingRule.seteAccrDate(finPayRuleAdjustmentDto.geteAccrDate());// 计提结束日 add
																									// by
						if (StringUtil.isNotEmpty(finPayRuleAdjustmentDto.getFirstBaseType())) {
							finCashCountingRule.setFirstBaseType(finPayRuleAdjustmentDto.getFirstBaseType());
						}
						baseFinProductCashCountingRuleMapper.insert(finCashCountingRule);

						// 现金流利率信息
						FinRateInfo finRateInfo = new FinRateInfo();
						BeanUtil.copyProperties(finRateInfo, finProduct);
						finRateInfo.setCashId(cashId_fee);
						if (StringUtil.isNotEmpty(finPayRuleAdjustmentDto.getProvisionFirstDay())) {
							finRateInfo.setEffDate(finPayRuleAdjustmentDto.getProvisionFirstDay());
						} else {
							finRateInfo.setEffDate(DateUtil.platDateFromCache());
						}
						// 一体化对应etliu
						// finRateInfo.setRateType(DictCodeList.利率类型_固定利率.getValue());
						finRateInfo.setIntType(DictCodeList.利率类型_固定利率.getValue());
						finRateInfo.setBasis(finPayRuleAdjustmentDto.getBasis());
						finRateInfo.setRate(ArithmeticUtil.divide(finPayRuleAdjustmentDto.getFeeRate(), 100, 14));
						finRateInfo.setFinprodId(finProduct.getFinprodId());
						finRateInfo.setFinprodType(finProduct.getFinprodType());
						finRateInfo.setFinprodType2(finProduct.getFinprodType2());
						finRateInfo.setBranch(branch);
						baseFinProductCashRateRuleMapper.insert(finRateInfo);

						if (!StringUtil.isEmptyList(finPayRuleAdjustmentDto.getFinCashPlanList())) {
							// 根据CashID删除付费计划
							finPayPlanAdjustmentMapper.deleteCashPlanByCashId(cashId_fee);
							for (int i = 1; i <= finPayRuleAdjustmentDto.getFinCashPlanList().size(); i++) {
								FinCashPlan finCashPlan = finPayRuleAdjustmentDto.getFinCashPlanList().get(i - 1);

								finCashPlan.setUpdateUser(finPayRuleAdjustmentDto.getUpdateUser());
								finCashPlan.setCreateUser(finPayRuleAdjustmentDto.getCreateUser());
								finCashPlan.setCreateDept(finPayRuleAdjustmentDto.getCreateDept());
								finCashPlan.setCashId(cashId_fee);
								finCashPlan.setCashNum(new Long(i));
								finCashPlan.setCashType(DictCodeList.现金流类型_费用支付.getValue());
								finCashPlan.setVdateUnadjust(finCashPlan.getVdate());
								finCashPlan.setMdateUnadjust(finCashPlan.getMdate());
								finCashPlan.setPayDateUnadjust(finCashPlan.getPayDate());
								finCashPlan.setFinprodId(finProduct.getFinprodId());
								finCashPlan.setFinprodType(finProduct.getFinprodType());
								finCashPlan.setFinprodType2(finProduct.getFinprodType2());
								finCashPlan.setBranch(branch);
								finCashPlanMapper.insert(finCashPlan);
							}
						}
					}
				} else {
					// 更新
					FinProduct finProduct = finProductMapper.selectByPrimaryKey(entity.getFinprodId());
					// 现金流信息(修改不变动)
					/*FinCashInfo finCashInfo_int = finCashInfoMapper
							.selectByPrimaryKey(finPayRuleAdjustmentDto.getCashId());
					if (finCashInfo_int != null) {
						finCashInfo_int.setFeeId(finPayRuleAdjustmentDto.getFeeId());
						finCashInfoMapper.deleteByPrimaryKey(finPayRuleAdjustmentDto.getCashId());
						finCashInfoMapper.insert(finCashInfo_int);
					}*/

					// 现金流支付规则
					FinCashPayRuleKey finCashPayRuleKey = new FinCashPayRuleKey();
					finCashPayRuleKey.setCashId(finPayRuleAdjustmentDto.getCashId());
					finCashPayRuleKey.setEffDate(finPayRuleAdjustmentDto.getEffDate());
					FinCashPayRule finCashPayRule = finCashPayRuleMapper.selectByPrimaryKey(finCashPayRuleKey);
					if (finCashPayRule != null) {
						finCashPayRule.setFirstPayDate(finPayRuleAdjustmentDto.getFirstCalEndDate());// add by
						if (StringUtil.isNotEmpty(finPayRuleAdjustmentDto.getPlanStartDay())) {
							finCashPayRule.setEffDate(finPayRuleAdjustmentDto.getPlanStartDay());
						} else {
							finCashPayRule.setEffDate(DateUtil.platDateFromCache());
						}																				// zhangweiqi
																										// 首次计费结束日
						finCashPayRule.setPayCycle(finPayRuleAdjustmentDto.getPayFeeRule());
						finCashPayRule.setEndTdayRule(finPayRuleAdjustmentDto.getCalcFeeTradeRule());
						finCashPayRule.setPayTdayRule(finPayRuleAdjustmentDto.getPayFeeTradeRule());
						//删除生效日大于等于下一付费区间开始日的数据
						finCashPayRuleKey.setEffDate(finPayRuleAdjustmentDto.getPlanStartDay());
						finPayPlanAdjustmentMapper.deleteFinCashPayByEffdate(finCashPayRuleKey);
						finCashPayRuleMapper.insert(finCashPayRule);
					}

					// 现金流计算规则
					FinCashCalcRuleKey finCashCalcRuleKey = new FinCashCalcRuleKey();
					finCashCalcRuleKey.setCashId(finPayRuleAdjustmentDto.getCashId());
					finCashCalcRuleKey.setEffDate(finPayRuleAdjustmentDto.getEffDate());
					FinCashCalcRule finCashCalcRule = finCashCalcRuleMapper.selectByPrimaryKey(finCashCalcRuleKey);
					if (finCashCalcRule != null) {
						if (StringUtil.isNotEmpty(finPayRuleAdjustmentDto.getPlanStartDay())) {
							finCashCalcRule.setEffDate(finPayRuleAdjustmentDto.getPlanStartDay());
						} else {
							finCashCalcRule.setEffDate(DateUtil.platDateFromCache());
						}
						finCashCalcRule.setBaseType(finPayRuleAdjustmentDto.getBasicBill());
						if (finPayRuleAdjustmentDto.getNetAssetValueDay() != null) {
							finCashCalcRule.setBaseDateType(finPayRuleAdjustmentDto.getNetAssetValueDay());
						}

						finCashCalcRule.setBasis(finPayRuleAdjustmentDto.getBasis());
						finCashCalcRule.setYield(ArithmeticUtil.divide(finPayRuleAdjustmentDto.getFeeRate(), 100, 14));
						//删除生效日大于等于下一付费区间开始日的数据
						finCashCalcRuleKey.setEffDate(finPayRuleAdjustmentDto.getPlanStartDay());
						finPayPlanAdjustmentMapper.deleteFinCashCalcRuleByEffdate(finCashCalcRuleKey);
						finCashCalcRuleMapper.insert(finCashCalcRule);
					}

					// 现金流计提规则
					FinFloatCountingRuleKey finFloatCountingRuleKey = new FinFloatCountingRuleKey();
					finFloatCountingRuleKey.setCashId(finPayRuleAdjustmentDto.getCashId());
					finFloatCountingRuleKey.setEffDate(finPayRuleAdjustmentDto.getEffDate());
					FinFloatCountingRule finCashCountingRule = finFloatCountingRuleMapper
							.selectByPrimaryKey(finFloatCountingRuleKey);
					if (finCashCountingRule != null) {
						if (StringUtil.isNotEmpty(finPayRuleAdjustmentDto.getPlanStartDay())) {
							finCashCountingRule.setEffDate(finPayRuleAdjustmentDto.getPlanStartDay());
						} else {
							finCashCountingRule.setEffDate(DateUtil.platDateFromCache());
						}
						finCashCountingRule.setfAccrDate(finPayRuleAdjustmentDto.getProvisionFirstDay());
						finCashCountingRule.seteAccrDate(finPayRuleAdjustmentDto.geteAccrDate());// 计提结束日 add
																									// by
						if (StringUtil.isNotEmpty(finPayRuleAdjustmentDto.getFirstBaseType())) {
							finCashCountingRule.setFirstBaseType(finPayRuleAdjustmentDto.getFirstBaseType());
						}
						//删除生效日大于等于下一付费区间开始日的数据
						finFloatCountingRuleKey.setEffDate(finPayRuleAdjustmentDto.getPlanStartDay());
						finPayPlanAdjustmentMapper.deleteFinFloatCountingRuleByEffdate(finFloatCountingRuleKey);
						finFloatCountingRuleMapper.insert(finCashCountingRule);
					}

					// 现金流利率信息
					FinRateInfoKey finRateInfoKey = new FinRateInfoKey();
					finRateInfoKey.setCashId(finPayRuleAdjustmentDto.getCashId());
					finRateInfoKey.setEffDate(finPayRuleAdjustmentDto.getEffDate());
					FinRateInfo finRateInfo = finRateInfoMapper.selectByPrimaryKey(finRateInfoKey);
					if (finRateInfo != null) {
						if (StringUtil.isNotEmpty(finPayRuleAdjustmentDto.getPlanStartDay())) {
							finRateInfo.setEffDate(finPayRuleAdjustmentDto.getPlanStartDay());
						} else {
							finRateInfo.setEffDate(DateUtil.platDateFromCache());
						}
						finRateInfo.setBasis(finPayRuleAdjustmentDto.getBasis());
						finRateInfo.setRate(ArithmeticUtil.divide(finPayRuleAdjustmentDto.getFeeRate(), 100, 14));
						//删除生效日大于等于下一付费区间开始日的数据
						finRateInfoKey.setEffDate(finPayRuleAdjustmentDto.getPlanStartDay());
						finPayPlanAdjustmentMapper.deleteFinRateInfoByEffdate(finRateInfoKey);
						finRateInfoMapper.insert(finRateInfo);
					}

					if (!StringUtil.isEmptyList(finPayRuleAdjustmentDto.getFinCashPlanList())) {
						// 根据CashID删除付费计划
						finPayPlanAdjustmentMapper.deleteCashPlanByCashId(finPayRuleAdjustmentDto.getCashId());
						for (int i = 1; i <= finPayRuleAdjustmentDto.getFinCashPlanList().size(); i++) {
							FinCashPlan finCashPlan = finPayRuleAdjustmentDto.getFinCashPlanList().get(i - 1);
							/*
							 * FinCashPlanKey key = new FinCashPlanKey();
							 * key.setCashId(finPayRuleAdjustmentDto.getCashId());
							 * key.setMdate(finCashPlan.getMdate()); if
							 * (finCashPlanMapper.selectByPrimaryKey(key) != null) { continue; }
							 */

							finCashPlan.setUpdateUser(finPayRuleAdjustmentDto.getUpdateUser());
							finCashPlan.setCreateUser(finPayRuleAdjustmentDto.getCreateUser());
							finCashPlan.setCreateDept(finPayRuleAdjustmentDto.getCreateDept());
							finCashPlan.setCashId(finPayRuleAdjustmentDto.getCashId());
							finCashPlan.setCashNum(new Long(i));
							finCashPlan.setCashType(DictCodeList.现金流类型_费用支付.getValue());
							finCashPlan.setVdateUnadjust(finCashPlan.getVdate());
							finCashPlan.setMdateUnadjust(finCashPlan.getMdate());
							finCashPlan.setPayDateUnadjust(finCashPlan.getPayDate());
							finCashPlan.setFinprodId(finProduct.getFinprodId());
							finCashPlan.setFinprodType(finProduct.getFinprodType());
							finCashPlan.setFinprodType2(finProduct.getFinprodType2());
							finCashPlan.setBranch(branch);
							finCashPlanMapper.insert(finCashPlan);
						}
					}

				}
			}
		}
	}

	/**
	 * 删除产品费用信息
	 * 
	 * @param entity
	 */
	public void deleteFinProdutFeeInfo(FinPayRuleAdjustmentDto finPayRuleAdjustmentDto) {
		if (finPayRuleAdjustmentDto != null) {
			// finProductFloatFeeMapper.deleteCashInfoByCashId(entity.getCashId());
			// finProductFloatFeeMapper.deleteFloatFeeCalcRuleByCashId(entity.getCashId());
			// finProductFloatFeeMapper.deleteFloatFeeCountingRuleByCashId(entity.getCashId());
			// finProductFloatFeeMapper.deleteFloatFeeDetailByCashId(entity.getCashId());
			// finProductFloatFeeMapper.deleteFloatAccrualScheByCashId(entity.getCashId());
			if (!StringUtil.isEmptyList(finPayRuleAdjustmentDto.getSelectedItem())) {
				for (FinPayRuleAdjustmentDto entity : finPayRuleAdjustmentDto.getSelectedItem()) {
					if (entity != null) {
						// 现金流信息删除
						finCashInfoMapper.deleteByPrimaryKey(entity.getCashId());
						// 现金流支付规则删除
						FinCashPayRuleKey finCashPayRuleKey = new FinCashPayRuleKey();
						finCashPayRuleKey.setCashId(entity.getCashId());
						finCashPayRuleKey.setEffDate(entity.getEffDate());
						finCashPayRuleMapper.deleteByPrimaryKey(finCashPayRuleKey);
						// 现金流计算规则删除
						FinCashCalcRuleKey finCashCalcRuleKey = new FinCashCalcRuleKey();
						finCashCalcRuleKey.setCashId(entity.getCashId());
						finCashCalcRuleKey.setEffDate(entity.getEffDate());
						finCashCalcRuleMapper.deleteByPrimaryKey(finCashCalcRuleKey);
						// 现金流利率信息删除
						FinRateInfoKey finRateInfoKey = new FinRateInfoKey();
						finRateInfoKey.setCashId(entity.getCashId());
						finRateInfoKey.setEffDate(entity.getEffDate());
						finRateInfoMapper.deleteByPrimaryKey(finRateInfoKey);
						// 现金流计提规则删除
						FinFloatCountingRuleKey finFloatCountingRuleKey = new FinFloatCountingRuleKey();
						finFloatCountingRuleKey.setCashId(entity.getCashId());
						finFloatCountingRuleKey.setEffDate(entity.getEffDate());
						finFloatCountingRuleMapper.deleteByPrimaryKey(finFloatCountingRuleKey);
					}
				}
			}

		}
	}

	public FinProduct selectFinProductByFinprodId(String finprodId) {
		return finProductMapper.selectByPrimaryKey(finprodId);
	}

	/**
	 * 根据金融产品代码查询子产品
	 * 
	 * @param finprodId
	 * @return
	 */
	public List<FinProduct> selectLayProductByFinproductId(String finprodId) {
		return finPayPlanAdjustmentMapper.selectLayProductByFinproductId(finprodId);
	}
}
