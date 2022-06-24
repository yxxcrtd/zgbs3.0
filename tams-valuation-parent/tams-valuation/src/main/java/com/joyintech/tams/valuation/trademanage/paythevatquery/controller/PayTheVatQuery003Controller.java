/*
 * 文件名：ValNetPlanQuotation003Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：qiangchengxi
 * 修改时间：2018年10月15日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.valuation.trademanage.paythevatquery.controller;

import java.util.List;

import javax.validation.Valid;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.valuation.trademanage.paythevatquery.dto.PayTheVatQuery001Dto;
import com.joyintech.tams.valuation.trademanage.paythevatquery.service.PayTheVatQuery003Service;

import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.util.WorkFlowUtil;


/**
 * 缴税增值税撤销〈一句话功能简述〉 〈功能详细描述〉
 * 
 * @author chufengjie
 * @version 1.0 2019年02月28日
 * @see PayTheVatQuery003Controller
 * @since 1.0
 */
@Controller
@PageId("PayTheVatQuery003")
@RequestMapping("/valuation/trademanage/paythevatquery/PayTheVatQuery003")
public class PayTheVatQuery003Controller extends BaseController {

	@Autowired
	private PayTheVatQuery003Service payTheVatQuery003Service;

	@Override
	public String init(Model model) {
		this.setSelectOptionToModel(model, DictCode.交易方向);

		// 工作流ID
		String workflowId = this.getParam(GlobalConstant.WORKFLOW_ID);
		// 交易编号
		String tradeId = this.getParam("tradeId");
		// 查询界面展示元素
		if (StringUtil.isNotEmpty(tradeId)) {
			List<PayTheVatQuery001Dto> dto = payTheVatQuery003Service.queryBooksetExt(tradeId);
			model.addAttribute("tradeId", dto.get(0).getTradeId());
			model.addAttribute("booksetId", dto.get(0).getBooksetId());
			model.addAttribute("booksetName", dto.get(0).getBooksetName());
			model.addAttribute("vDate", dto.get(0).getvDate());
			model.addAttribute("mDate", dto.get(0).getmDate());
			model.addAttribute("seitleDate", dto.get(0).getSeitleDate());
			model.addAttribute("interestIncome", dto.get(0).getInterestIncome());
			model.addAttribute("dividendIncome", dto.get(0).getDividendIncome());
			model.addAttribute("difbetIncome", dto.get(0).getDifbetIncome());
			model.addAttribute("appreciationTax", dto.get(0).getAppreciationTax());
			model.addAttribute("constructionTax", dto.get(0).getConstructionTax());
			model.addAttribute("educationTax", dto.get(0).getEducationTax());
			model.addAttribute("placeeducationTax", dto.get(0).getPlaceeducationTax());
			model.addAttribute("additionalTax", dto.get(0).getAdditionalTax());
			model.addAttribute("taxidenType", dto.get(0).getTaxidenType());
		}
		if (StringUtil.isNotEmpty(workflowId)) {
			PayTheVatQuery001Dto dto = payTheVatQuery003Service.getBussinessEntityByWorkflowId(workflowId);
			model.addAttribute("tradeId", dto.getTradeId());
			model.addAttribute("booksetId", dto.getBooksetId());
			model.addAttribute("booksetName", dto.getBooksetName());
			model.addAttribute("vDate", dto.getvDate());
			model.addAttribute("mDate", dto.getmDate());
			model.addAttribute("seitleDate", dto.getSeitleDate());
			model.addAttribute("interestIncome", dto.getInterestIncome());
			model.addAttribute("dividendIncome", dto.getDividendIncome());
			model.addAttribute("difbetIncome", dto.getDifbetIncome());
			model.addAttribute("appreciationTax", dto.getAppreciationTax());
			model.addAttribute("constructionTax", dto.getConstructionTax());
			model.addAttribute("educationTax", dto.getEducationTax());
			model.addAttribute("placeeducationTax", dto.getPlaceeducationTax());
			model.addAttribute("additionalTax", dto.getAdditionalTax());
			List<PayTheVatQuery001Dto> dto1 = payTheVatQuery003Service.queryBooksetExt(dto.getTradeId());
			model.addAttribute("taxidenType", dto1.get(0).getTaxidenType());

		}
		// model.addAttribute("workflowId", workflowId);
		String editFlag = this.getParam("editFlag");
		model.addAttribute("editFlag", editFlag);
		return super.init(model);
	}

	/**
	 * 缴税增值税撤销走流程 <一句话功能简述> <功能详细描述>
	 * 
	 * @param dto
	 * @param br
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "doSave", method = RequestMethod.POST)
	public AjaxResult doSave(@RequestBody @Valid PayTheVatQuery001Dto dto, BindingResult br) {
		if (br.hasErrors()) {
			return new AjaxResult(br);
		}
		// 启动流程
		ProcessParameter<PayTheVatQuery001Dto> proParam = new ProcessParameter<>();
		WorkFlowParameter<PayTheVatQuery001Dto> params = new WorkFlowParameter<>();

		// 业务代码（资产代码）
		params.setBusinessCode(dto.getBooksetId());
		// 业务类型
		params.setBusinessType(DictCodeList.共通业务类型_缴纳增值税.getValue());
		// 页面id
		params.setPageId(this.getPageId());
		// 元素id
		params.setElementId(this.getParam(GlobalConstant.ELEMENT_ID));
		// 设置流程业务说明, entity即传入的dto
		params.setComment(entity -> WorkFlowUtil.concatComment("缴纳增值税撤销：", entity.getBooksetId(), // 交易编号
				DateUtil.format(DateUtil.parse(entity.getSeitleDate()), "yyyy-MM-dd"), // 估值日期
				entity.getTradeId()// 产品代码
		));
		// 塞入流程参数
		proParam.setWorkFlowParameter(params);

		String msg = getMessage("COMMON_I_0001");
		try {
			msg = payTheVatQuery003Service.start(proParam, dto);
		} catch (Exception e) {
			getLogger().error("启动流程失败：", e);
			return new AjaxResult(e.getMessage());
		}

		return new AjaxResult(msg);
	}

	/**
	 * 撤销验证：缴税/退税日期要大于等于该产品的账套日期
	 * 
	 * @param dto
	 * @param bindingResult
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("check")
	public AjaxResult check(@RequestBody @Valid PayTheVatQuery001Dto dto, BindingResult bindingResult)
			 {
		String msg = StringUtil.EMPTY;
		String booksetId = dto.getBooksetId();
		String operateType = dto.getOperateType();
		String tradeId = dto.getTradeId();
		// 缴税/退税日期
		String seitleDate = dto.getSeitleDate();
		List<PayTheVatQuery001Dto> list = payTheVatQuery003Service.findFinproduct(booksetId, seitleDate);
		if (CollectionUtils.isNotEmpty(list)) {
			msg = "缴税/退税日期要大于等于该产品的账套日期";
			return new AjaxResult(msg, MessageType.错误消息);
		}
		List<PayTheVatQuery001Dto> list1 = payTheVatQuery003Service.findPtradeid(tradeId);
		if (CollectionUtils.isNotEmpty(list1)) {
			msg = "该产品已撤销。";
			return new AjaxResult(msg, MessageType.错误消息);
		}
		return commonCheck(bindingResult, operateType);
	}
}
