/*
 * 文件名：FinPayPlanAdjustmentValidator.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：huwenbin
 * 修改时间：2018年10月22日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.product.messageadjust.finpayplanadjustment.validator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.BeanValidator;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.common.base.entity.FinProduct;
import com.joyintech.tams.component.business.core.dto.WflValidInputParameter;
import com.joyintech.tams.component.business.core.service.MstWorkflowCommonService;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.invest.product.messageadjust.finpayplanadjustment.dto.FinPayPlanAdjustment002Dto;
import com.joyintech.tams.invest.product.messageadjust.finpayplanadjustment.service.FinPayPlanAdjustment002Service;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.workflow.engine.util.WorkFlowUtil;

/**
 * 〈一句话功能简述〉 〈功能详细描述〉
 * 
 * @author yaozhenguo
 * @version 1.0 2019年11月30日
 * @see FinPayPlanAdjustment002Validator
 * @since 1.0
 */
@Component
public class FinPayPlanAdjustment002Validator implements BeanValidator {

	/**
	 * 平台日期组件接口
	 */
	@Autowired(required = false)
	private IPlatformDate iPlatformDate;

	@Autowired
	private MstWorkflowCommonService mstWorkflowCommonService;
	@Autowired
	private FinPayPlanAdjustment002Service finPayPlanAdjustment002Service;

	/**
	 * 信息调整管理_付费计划调整
	 */
	@Override
	public List<ValidMessage> valid(Object value) {
		FinPayPlanAdjustment002Dto finPayPlanAdjustment002Dto = (FinPayPlanAdjustment002Dto) value;
		List<ValidMessage> erroList = new ArrayList<ValidMessage>();
		if (StringUtil.isEmptyList(finPayPlanAdjustment002Dto.getSelectedItem())) {
			validator(finPayPlanAdjustment002Dto, erroList);
		} else {
			// 流程验证
			WflValidInputParameter wflValidInputParameterIssue = new WflValidInputParameter();
			wflValidInputParameterIssue.setPageId(finPayPlanAdjustment002Dto.getPageId());
			wflValidInputParameterIssue.setWorkflowId(finPayPlanAdjustment002Dto.getWorkflowId());
			wflValidInputParameterIssue.setBusinessDataPk1(finPayPlanAdjustment002Dto.getFinprodId());
			if (WorkFlowUtil.isNotFromToDoList()) {
				if (mstWorkflowCommonService.isInWflProcess(wflValidInputParameterIssue)) {
					erroList.add(new ValidMessage(getMessage("FIN_PROD_E_0077", "")));
					return erroList;
				}
			}
			for (FinPayPlanAdjustment002Dto enetity : finPayPlanAdjustment002Dto.getSelectedItem()) {
				validator(enetity, erroList);
				if (!erroList.isEmpty()) {
					break;
				}
			}
			// 费用品种不能重复
			Map<String, Object> map = new HashMap<>();
			List<FinPayPlanAdjustment002Dto> list = new ArrayList<>();
			map.put("finprodId", finPayPlanAdjustment002Dto.getFinprodId());
			list = finPayPlanAdjustment002Service.queryFeeInfoByFinprodId(map);
			for (FinPayPlanAdjustment002Dto enetity : finPayPlanAdjustment002Dto.getSelectedItem()) {
				if (StringUtil.isEmpty(enetity.getCashId())) {
					// 分层产品款型为空所有子产品都使用该费用
					if (StringUtil.equals(finPayPlanAdjustment002Dto.getIsLay(), "Y")
							&& StringUtil.isEmpty(enetity.getProdStyle())) {
						List<FinProduct> products = finPayPlanAdjustment002Service
								.selectLayProductByFinproductId(finPayPlanAdjustment002Dto.getFinprodId());
						if (!StringUtil.isEmptyList(products)) {
							for (FinProduct finProduct : products) {
								FinPayPlanAdjustment002Dto feeInfo = new FinPayPlanAdjustment002Dto();
								feeInfo.setProdStyle(finProduct.getFinprodId());
								feeInfo.setFeeType(enetity.getFeeType());
								list.add(feeInfo);
							}
						}
					} else {
						list.add(enetity);
					}
				}
			}
			Map<String, FinPayPlanAdjustment002Dto> goalInfoMap1 = list.stream().collect(
					Collectors.toMap(FinPayPlanAdjustment002Dto::getProdStyleAndFeeTypeNew, a -> a, (k1, k2) -> k1));
			if (goalInfoMap1.size() != list.size()) {
				erroList.add(new ValidMessage(this.getMessage("FIN_PROD_E_0076")));
			}
		}
		return erroList;
	}

	private void validator(FinPayPlanAdjustment002Dto finPayPlanAdjustment002Dto, List<ValidMessage> erroList) {
		if (StringUtil.isNotEmpty(finPayPlanAdjustment002Dto.getPayFeeRule())
				&& (StringUtil.equals(finPayPlanAdjustment002Dto.getPayFeeRule(), DictCodeList.频率_不规则.getValue()))
		&& StringUtil.equals(finPayPlanAdjustment002Dto.getOperateType(),"preview")) {
			// 一体化对应etliu end
			erroList.add(new ValidMessage(getMessage("FIN_PROD_E_0070")));
		} else {
			// 费用名称不为空
			if (!StringUtil.isNotEmpty(finPayPlanAdjustment002Dto.getFeeId())) {
				erroList.add(
						new ValidMessage(getMessage("BUSS_COM_E_0038", getMessage("BussFinProductFeeInfo.feeName"))));
			}
			// 费用品种不为空
			if (!StringUtil.isNotEmpty(finPayPlanAdjustment002Dto.getFeeType())) {
				erroList.add(
						new ValidMessage(getMessage("BUSS_COM_E_0038", getMessage("BussFinProductFeeInfo.feeType"))));
			}
			//不规则费用非必填
            if(StringUtil.equals(DictCodeList.收费类型_不规则费用.getValue(), finPayPlanAdjustment002Dto.getChargeType())){
                return;
            }
			// 费率不为空
			if (!StringUtil.isNotEmpty(finPayPlanAdjustment002Dto.getFeeRate())) {
				erroList.add(
						new ValidMessage(getMessage("BUSS_COM_E_0038", getMessage("BussFinProductFeeInfo.feeRate"))));
			}
			if (!StringUtils.equals(finPayPlanAdjustment002Dto.getChargeType(), "02")) {
				if (!StringUtil.isNotEmpty(finPayPlanAdjustment002Dto.getBasicBill())) {
					erroList.add(new ValidMessage(
							getMessage("BUSS_COM_E_0038", getMessage("BussFinProductFeeInfo.basicBill"))));
				}
				if (!StringUtil.isNotEmpty(finPayPlanAdjustment002Dto.getBasis())) {
					erroList.add(
							new ValidMessage(getMessage("BUSS_COM_E_0038", getMessage("BussFinProductFeeInfo.basis"))));
				}
			}
			// 付费规则不为空
			if (!StringUtil.isNotEmpty(finPayPlanAdjustment002Dto.getPayFeeRule())) {
				erroList.add(new ValidMessage(
						getMessage("BUSS_COM_E_0038", getMessage("BussFinProductFeeInfo.payFeeRule"))));
			}
			// 计提营业日准则
			if (!StringUtil.isNotEmpty(finPayPlanAdjustment002Dto.getCalcFeeTradeRule())&&(!StringUtil.equals(finPayPlanAdjustment002Dto.getPayFeeRule(), DictCodeList.频率_不规则.getValue()))) {
				erroList.add(new ValidMessage(
						getMessage("BUSS_COM_E_0038", getMessage("FinPayPlanAdjustment002.accrTdayRule"))));
			}
			// 付费营业日准则
			if (!StringUtil.isNotEmpty(finPayPlanAdjustment002Dto.getPayFeeTradeRule())&&(!StringUtil.equals(finPayPlanAdjustment002Dto.getPayFeeRule(), DictCodeList.频率_不规则.getValue()))) {
				erroList.add(new ValidMessage(
						getMessage("BUSS_COM_E_0038", getMessage("BussFinProductFeeInfo.payFeeTradeRule"))));
			}
			// 交易日历不为空
			if (!StringUtil.isNotEmpty(finPayPlanAdjustment002Dto.getDealCalId())) {
				erroList.add(
						new ValidMessage(getMessage("BUSS_COM_E_0038", getMessage("BussFinProductFeeInfo.dealCalId"))));
			}
			// 起息日不为空
			if (!StringUtil.isNotEmpty(finPayPlanAdjustment002Dto.getVdate())) {
				erroList.add(
						new ValidMessage(getMessage("BUSS_COM_E_0038", getMessage("BussFinProductFeeInfo.vdate"))));
			}
			// d到期日不为空
			if (!StringUtil.isNotEmpty(finPayPlanAdjustment002Dto.getMdate())) {
				erroList.add(
						new ValidMessage(getMessage("BUSS_COM_E_0038", getMessage("BussFinProductFeeInfo.mdate"))));
			}
			// 修改
			if (StringUtil.isNotEmpty(finPayPlanAdjustment002Dto.getCashId()) && !StringUtil.equals(finPayPlanAdjustment002Dto.getPayFeeRule(), DictCodeList.频率_不规则.getValue())) {
				// 下一区间开始大于起息日，大于等于平台日,
				if (finPayPlanAdjustment002Dto.getPlanStartDay() == null) {
					erroList.add(new ValidMessage(
							getMessage("BUSS_COM_E_0038", getMessage("FinPayPlanAdjustment002.next.planStartDay"))));
				} else {
					if (DateUtil.compareDate(finPayPlanAdjustment002Dto.getPlanStartDay(),
							finPayPlanAdjustment002Dto.getVdate()) < 0) {
						erroList.add(new ValidMessage(getMessage("FinPayPlanAdjustment002.planStartDay.vdate")));
					} else if (DateUtil.compareDate(finPayPlanAdjustment002Dto.getPlanStartDay(),
							iPlatformDate.getSysDate()) < 0) {
						erroList.add(new ValidMessage(getMessage("FinPayPlanAdjustment002.planStartDay.platDate")));
					}
				}
				// 下一区间结束日必须小于等于清盘日大于下一区间开始日
				if (finPayPlanAdjustment002Dto.getPlanEndDay() == null) {
					erroList.add(new ValidMessage(
							getMessage("BUSS_COM_E_0038", getMessage("FinPayPlanAdjustment002.next.planEndDay"))));
				} else {
					if (DateUtil.compareDate(finPayPlanAdjustment002Dto.getPlanEndDay(),
							finPayPlanAdjustment002Dto.getLiquidationDate()) > 0) {
						erroList.add(
								new ValidMessage(getMessage("FinPayPlanAdjustment002.planEndDay.liquidationDate")));
					} else if (DateUtil.compareDate(finPayPlanAdjustment002Dto.getPlanEndDay(),
							finPayPlanAdjustment002Dto.getPlanStartDay()) <= 0) {
						erroList.add(new ValidMessage(getMessage("FinPayPlanAdjustment002.planEndDay.planStartDate")));
					}
				}
			}
			// 收益类型不为空
			if (!StringUtil.isNotEmpty(finPayPlanAdjustment002Dto.getProfitType())) {
				erroList.add(new ValidMessage(
						getMessage("BUSS_COM_E_0038", getMessage("BussFinProductFeeInfo.profitType"))));
			}
			// 新建费用首次支付日不能为空
			if (StringUtil.isEmpty(finPayPlanAdjustment002Dto.getCashId())) {
				if (finPayPlanAdjustment002Dto.getFirstCalEndDate() == null) {
					erroList.add(new ValidMessage(
							getMessage("BUSS_COM_E_0038", getMessage("FinFloatMgtExpQuotation002.firstPayDate"))));
				}
				//新建首次付费日大于首次计提日
				if (DateUtil.compareDate(finPayPlanAdjustment002Dto.getFirstCalEndDate(),
						finPayPlanAdjustment002Dto.getProvisionFirstDay()) < 0) {
					erroList.add(new ValidMessage(this.getMessage("BUSS_COM_E_0004",
							this.getMessage("FinPayPlanAdjustment002.firstPayDate"),
							this.getMessage("BussFinProductFeeInfo.provisionFirstDay"))));
				}
			}
			// 首次计费结束日
			if (StringUtil.isNotEmpty(finPayPlanAdjustment002Dto.getFirstCalEndDate()) && !StringUtil.equals(finPayPlanAdjustment002Dto.getPayFeeRule(), DictCodeList.频率_不规则.getValue())) {
				if (DateUtil.compareDate(finPayPlanAdjustment002Dto.getVdate(),
						finPayPlanAdjustment002Dto.getFirstCalEndDate()) >= 0) {
					// 首次计费结束日应该大于产品起息日
					erroList.add(new ValidMessage("", getMessage("FIN_PROD_E_0109")));
				}
				if (DateUtil.compareDate(finPayPlanAdjustment002Dto.getLiquidationDate(),
						finPayPlanAdjustment002Dto.getFirstCalEndDate()) <= 0) {
					// 首次计费结束日应该小于产品清盘日
					erroList.add(new ValidMessage("", getMessage("FIN_PROD_E_0110")));

				}
			}
			// 计提结束日
			if (!StringUtil.isNotEmpty(finPayPlanAdjustment002Dto.geteAccrDate())) {
				erroList.add(new ValidMessage(
						this.getMessage("BUSS_COM_E_0038", this.getMessage("BussFinProductFeeInfo.eAccrDate"))));
			} else {
				if (DateUtil.compareDate(finPayPlanAdjustment002Dto.getVdate(),
						finPayPlanAdjustment002Dto.geteAccrDate()) >= 0) {
					// 计提结束日应该大于产品起息日
					erroList.add(new ValidMessage("", getMessage("FIN_PROD_E_0106")));
				}
				if (DateUtil.compareDate(finPayPlanAdjustment002Dto.getLiquidationDate(),
						finPayPlanAdjustment002Dto.geteAccrDate()) <= 0) {
					// 计提结束日应该小于产品清盘日
					erroList.add(new ValidMessage("", getMessage("FIN_PROD_E_0107")));
				}
				if (StringUtil.isEmpty(finPayPlanAdjustment002Dto.getCashId())) {
					if (DateUtil.compareDate(finPayPlanAdjustment002Dto.getProvisionFirstDay(),
							finPayPlanAdjustment002Dto.geteAccrDate()) >= 0) {
						// 计提结束日应该大于计提开始日（首次计提结束日）
						erroList.add(new ValidMessage("", getMessage("FIN_PROD_E_0108")));
					}
				}
			}
			// 计费基数 为资产净值时 资产净值日类型不能为当日
			if (StringUtil.equals(DictCodeList.计费基数_费用品种_资产规模.getValue(), finPayPlanAdjustment002Dto.getBasicBill())
					&& StringUtil.equals(DictCodeList.基数日期类型_当日.getValue(),
							finPayPlanAdjustment002Dto.getNetAssetValueDay())) {
				erroList.add(new ValidMessage("", getMessage("FIN_PROD_E_0113")));
			}
			// 首次计费基数不能为空 yzg end 20191020
			if ((StringUtil.equals(finPayPlanAdjustment002Dto.getNetAssetValueDay(),
					DictCodeList.基数日期类型_前一自然日.getValue())
					|| StringUtil.equals(finPayPlanAdjustment002Dto.getNetAssetValueDay(),
							DictCodeList.基数日期类型_前一工作日.getValue()))
					&& DateUtil.compareDate(finPayPlanAdjustment002Dto.getProvisionFirstDay(),
							finPayPlanAdjustment002Dto.getVdate()) == 0
					&& StringUtil.equals(finPayPlanAdjustment002Dto.getBasicBill(),
							DictCodeList.计费基数_费用品种_资产规模.getValue())) {
				if (!StringUtil.equals(finPayPlanAdjustment002Dto.getFirstBaseType(),
						DictCodeList.计费基数_费用品种_存续本金.getValue())) {
					erroList.add(new ValidMessage(getMessage("BussFinProductFeeInfo.firstBaseType.yes")));
				}
			} else {
				if (StringUtil.isNotEmpty(finPayPlanAdjustment002Dto.getFirstBaseType())) {
					erroList.add(new ValidMessage(getMessage("BussFinProductFeeInfo.firstBaseType.no")));
				}
			}
			// 首次计提日
			if (StringUtil.isNotEmpty(finPayPlanAdjustment002Dto.getProvisionFirstDay())) {
				// 首次计提日不能小于产品起息日
				if (StringUtil.isNotEmpty(finPayPlanAdjustment002Dto.getVdate())
						&& DateUtil.compareDate(finPayPlanAdjustment002Dto.getProvisionFirstDay(),
								finPayPlanAdjustment002Dto.getVdate()) < 0) {
					erroList.add(new ValidMessage(this.getMessage("BUSS_COM_E_0004",
							this.getMessage("BussFinProductFeeInfo.provisionFirstDay"),
							this.getMessage("FinProductBuild002.vdate"))));
				}
				// 首次计提日不能大于产品到期日
				if (StringUtil.isNotEmpty(finPayPlanAdjustment002Dto.getMdate())
						&& DateUtil.compareDate(finPayPlanAdjustment002Dto.getProvisionFirstDay(),
								finPayPlanAdjustment002Dto.getMdate()) > 0) {
					erroList.add(new ValidMessage(this.getMessage("FIN_PROD_E_0017",
							this.getMessage("BussFinProductFeeInfo.provisionFirstDay"),
							this.getMessage("FinProductBuild002.mdate"))));
				}

				//TISHKBS-1164
				//CLONE - 产品起息后，修改产品的费用付费计划信息，点击提交产品报错“产品费用的首次计提日不能小于平台日”

				//新增费用首次计提日不能小于平台日，新增费用从首次计提日开始生效
//				if (StringUtil.isNotEmpty(finPayPlanAdjustment002Dto.getVdate())
//						&& DateUtil.compareDate(finPayPlanAdjustment002Dto.getProvisionFirstDay(),
//								iPlatformDate.getSysDate()) < 0) {
//					erroList.add(new ValidMessage(this.getMessage("BUSS_COM_E_0004",
//							this.getMessage("BussFinProductFeeInfo.provisionFirstDay"),
//							this.getMessage("平台日"))));
//				}
			} else {
				erroList.add(new ValidMessage(this.getMessage("BUSS_COM_E_0038",
						this.getMessage("BussFinProductFeeInfo.provisionFirstDay"))));
			}
		}
	}

}
