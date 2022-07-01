/*
 * 文件名：ValuationMethodScheme004Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangwei
 * 修改时间：2018年8月28日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.valuation.valuationparametermanage.valuationmethodscheme.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.ModelDictCodeList;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Maps;
import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.RequestUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.valuation.valuationparametermanage.valuationmethodscheme.dto.ValuationMethodScheme001Dto;
import com.joyintech.tams.valuation.valuationparametermanage.valuationmethodscheme.dto.ValuationMethodScheme004Dto;
import com.joyintech.tams.valuation.valuationparametermanage.valuationmethodscheme.service.ValuationMethodScheme001Service;
import com.joyintech.tams.valuation.valuationparametermanage.valuationmethodscheme.service.ValuationMethodScheme004Service;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.util.WorkFlowUtil;

/**
 * 估值方案管理-左侧
 * 
 * @author zhangwei
 * @version 1.0 2018年8月28日
 * @see ValuationMethodScheme004Controller
 * @since 1.0
 */
@Controller
@PageId("ValuationMethodScheme004")
@RequestMapping("/valuation/valuationparametermanage/valuationmethodscheme/ValuationMethodScheme004")
public class ValuationMethodScheme004Controller extends BaseController {

	@Autowired
	private ValuationMethodScheme004Service valuationMethodScheme004Service;

	@Autowired
	private ValuationMethodScheme001Service valuationMethodScheme001Service;

	/**
	 * 页面初始化 {@inheritDoc}
	 */
	@Override
	public String init(Model model) {
		String schemeDetailId = getParam("schemeDetailId");
		String operateType = getParam("operateType");
		String workflowId = this.getParam(GlobalConstant.WORKFLOW_ID);// 工作流ID
		ValuationMethodScheme004Dto dto = new ValuationMethodScheme004Dto();

		if (StringUtils.isNotEmpty(schemeDetailId)) {
			dto = valuationMethodScheme004Service.findBySchemeDetailId(schemeDetailId);
		}else if(StringUtil.isNotEmpty(workflowId)) { // 流程查看页面
            dto = valuationMethodScheme004Service.getBussinessEntityByWorkflowId(workflowId);

            schemeDetailId = dto.getSchemeDetailId(); // 避免流程，再生成主键
        }
		if (StringUtils.isEmpty(schemeDetailId) || "copy".equals(operateType)) { // 新建 和 复制 自动生成主键 并 连接方案
			dto.setSchemeDetailId(IDGenerator.generate());
			dto.setSchemeId(getParam("schemeId"));
		}
		dto.setOperateType(operateType);
		model.addAttribute("entity", dto);

//		super.setSelectOptionToModel(model, DictCode.是否);

		super.setSelectOptionToModel(model, DictCode.估值交易市场);// 交易市场
		super.setSelectOptionToModel(model, DictCode.证券品种);// 债券分类
		super.setSelectOptionToModel(model, DictCode.债券类型);// 债券细分类
		super.setSelectOptionToModel(model, DictCode.投资目的);
		super.setSelectOptionToModel(model, DictCode.估值方法);
		super.setSelectOptionToModel(model, DictCode.估值市价); // 第一估值市价 和 第二估值市价
		super.setSelectOptionToModel(model, DictCode.估值日期);
		super.setSelectOptionToModel(model, DictCode.估值舍入规则);
		super.setSelectOptionToModel(model, DictCode.价格类别);

		return super.init(model);
	}

	/**
	 * 下拉框 ： 产品代码
	 *
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("findFinprod")
	public List<SelectOptionDto> findFinprod() {
		return valuationMethodScheme004Service.findFinprod(RequestUtil.getPageParam());
	}

	/**
	 * 通过产品代码，获取产品信息
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("getFinprodInfo")
	public ValuationMethodScheme004Dto getFinprodInfo() {
		return valuationMethodScheme004Service.getFinprodInfo(RequestUtil.getPageParam());
	}




	@ResponseBody
	@RequestMapping("check")
	public AjaxResult check(@RequestBody @Validated({ValidateGroupEdit.class}) ValuationMethodScheme004Dto dto, BindingResult bindingResult)
			throws ParseException {
//        List<ValuationMethodScheme004Dto> list = listdto.getListValuationMethodSchemeDto();
//        ValuationMethodScheme004Dto dto = list.get(0);
		String msg = StringUtil.EMPTY;
		String operateType = dto.getOperateType();

		if(StringUtils.isEmpty(dto.getSchemeDetailId())) {
			bindingResult.rejectValue("schemeDetailId", "NotBlank", "方案明细代码不能为空！");
		}

		if(StringUtils.isEmpty(dto.getSchemeId())) {
			bindingResult.rejectValue("schemeId", "NotBlank", "方案代码不能为空！");
		}

		if(StringUtils.isEmpty(dto.getFinprodId())) {
			bindingResult.rejectValue("finprodId", "NotBlank", "资产代码不能为空！");
		}

		if(StringUtils.isEmpty(dto.getFinprodName())) {
			bindingResult.rejectValue("finprodName", "NotBlank", "资产名称不能为空！");
		}

		if(StringUtils.isEmpty(dto.getValuationMethod())) {
			bindingResult.rejectValue("valuationMethod", "NotBlank", "估值方法不能为空！");
		}

		if(StringUtils.isEmpty(dto.getVdate())) {
			bindingResult.rejectValue("vdate", "NotBlank", "开始日不能为空！");
		}

		if (GlobalConstant.DETAIL_PAGE_MODEL_NEW.equals(dto.getPageModel()) && !bindingResult.hasErrors()) {// 新建唯一性验证：主键
			ValuationMethodScheme004Dto entity = valuationMethodScheme004Service
					.findBySchemeDetailId(dto.getSchemeDetailId());
			if (entity != null) {
				msg = "方案代码已存在！";
				return new AjaxResult(msg, MessageType.错误消息);
			}
		}

		if (!bindingResult.hasErrors()) {// 新建、修改都有验证
			if (DictCodeListExt1.估值方法_市值成本孰低法.getValue().equals(dto.getValuationMethod())
					|| DictCodeListExt1.估值方法_市值法.getValue().equals(dto.getValuationMethod())) {
				if(StringUtils.isEmpty(dto.getValuationPrice())) {
					msg += "估值第一市价不能为空！</br>";
				}
				if(StringUtils.isEmpty(dto.getValuationDate())) {
					msg += "行情日期不能为空！</br>";
				}
				if(dto.getQuotaDigit() == null) {
					msg += "行情位数不能为空！</br>";
				}
				if(StringUtils.isEmpty(dto.getPriceTailMode())) {
					bindingResult.rejectValue("priceTailMode", "NotBlank", "行情尾数处理方式不能为空！");
				}
                if(DictCodeList.估值市价_收盘价.getValue().equals(dto.getValuationPrice())
                || DictCodeList.估值市价_平均价.getValue().equals(dto.getValuationPrice())
                || DictCodeList.估值市价_最高价.getValue().equals(dto.getValuationPrice())
                || DictCodeList.估值市价_最低价.getValue().equals(dto.getValuationPrice())
                || DictCodeList.估值市价_卖一价.getValue().equals(dto.getValuationPrice())
                || DictCodeList.估值市价_买一价.getValue().equals(dto.getValuationPrice())
                || DictCodeList.估值市价_收盘价.getValue().equals(dto.getValuationPrice2())
                || DictCodeList.估值市价_平均价.getValue().equals(dto.getValuationPrice2())
                || DictCodeList.估值市价_最高价.getValue().equals(dto.getValuationPrice2())
                || DictCodeList.估值市价_最低价.getValue().equals(dto.getValuationPrice2())
                || DictCodeList.估值市价_卖一价.getValue().equals(dto.getValuationPrice2())
                || DictCodeList.估值市价_买一价.getValue().equals(dto.getValuationPrice2())){
                    if(StringUtils.isEmpty(dto.getPriceClass())) {
                        bindingResult.rejectValue("priceClass", "NotBlank", "价格类型不能为空！");
                    }
                }
				if(StringUtils.isNotEmpty(msg)) {
					return new AjaxResult(msg, MessageType.错误消息);
				}
			}

			if (StringUtils.isNotEmpty(dto.getVdate()) && StringUtils.isNotEmpty(dto.getMdate())) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				Date vDate = sdf.parse(dto.getVdate());
				Date mDate = sdf.parse(dto.getMdate());
				if (mDate.before(vDate)) {
					msg = "结束日不能小于开始日！";
					return new AjaxResult(msg, MessageType.错误消息);
				}
			}

			// 唯一性校验：资产代码、投资目的、开始日相同时，不可提交。
			Map<String, Object> params1 = Maps.newHashMap();
			params1.put("schemeId", dto.getSchemeId());
			params1.put("finprodId", dto.getFinprodId());
			params1.put("invAim", dto.getInvAim());
			params1.put("vdate", dto.getVdate());
			params1.put("mdate", dto.getMdate());

			if (GlobalConstant.DETAIL_PAGE_MODEL_EDIT.equals(dto.getPageModel())) {// 修改时忽略本身
				params1.put("owner", dto.getSchemeDetailId());
			}

			// 日期重叠校验
			if(StringUtils.isEmpty(dto.getMdate())) {// 提交没有结束日
				List<ValuationMethodScheme001Dto> list1 = valuationMethodScheme001Service.findSpecialAssetsByPage(params1)
						.getRoot();
				if(CollectionUtils.isNotEmpty(list1)) {
					msg = "在此投资目的下，该资产存在未结束的估值配置，请核对后提交！";
					return new AjaxResult(msg, MessageType.错误消息);
				}
				params1.put("mdate", "checkArea");
				List<ValuationMethodScheme001Dto> list2 = valuationMethodScheme001Service.findSpecialAssetsByPage(params1)
						.getRoot();
				if(CollectionUtils.isNotEmpty(list2)) {
					msg = "在此投资目的下，该资产在此区间内存在估值配置，请核对后提交！";
					return new AjaxResult(msg, MessageType.错误消息);
				}
			}else {// 提交有结束日
				params1.put("vdate", "checkArea");
				List<ValuationMethodScheme001Dto> list2 = valuationMethodScheme001Service.findSpecialAssetsByPage(params1)
						.getRoot();
				if(CollectionUtils.isNotEmpty(list2)) {
					msg = "在此投资目的下，该资产在此区间内存在估值配置，请核对后提交！";
					return new AjaxResult(msg, MessageType.错误消息);
				}

				params1.put("vdate", dto.getVdate());
				List<ValuationMethodScheme001Dto> list1 = valuationMethodScheme001Service.findSpecialAssetsByPage(params1)
						.getRoot();
				if(CollectionUtils.isNotEmpty(list1)) {
					msg = "在此投资目的下，该资产在此区间内存在估值配置，请核对后提交！";
					return new AjaxResult(msg, MessageType.错误消息);
				}

			}

		}

		return commonCheck(bindingResult, operateType);
	}

	/**
	 * 新建/修改, 并启动流程
	 * @param dto
	 * @param bindingResult
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = PAGE_SAVE, method = RequestMethod.POST)
	public AjaxResult save(@RequestBody ValuationMethodScheme004Dto dto, BindingResult bindingResult) {
//        List<ValuationMethodScheme004Dto> list = listdto.getListValuationMethodSchemeDto();
//        ValuationMethodScheme004Dto dto = list.get(0);

		String msg = StringUtil.EMPTY;

		ProcessParameter<ValuationMethodScheme004Dto> processParameter = new ProcessParameter<>();
        WorkFlowParameter<ValuationMethodScheme004Dto> workFlowParameter = new WorkFlowParameter<>();

        try {
            workFlowParameter.setPageId(this.getPageId()); // 页面id
            workFlowParameter.setElementId(this.getParam(GlobalConstant.ELEMENT_ID)); // 元素id
            workFlowParameter.setBusinessCode(dto.getSchemeId());// 业务代码编号
            workFlowParameter.setBusinessType(DictCodeList.共通业务类型_估值方法方案.getValue());// 业务类型
            workFlowParameter.setComment(entity -> WorkFlowUtil.concatComment(entity.getSchemeDetailId(),
                entity.getSchemeId(), "特殊资产估值配置"));// 当前工作流备注
            processParameter.setWorkFlowParameter(workFlowParameter);// 塞入流程参数

            valuationMethodScheme004Service.start(processParameter, dto);
            msg = getMessage("COMMON_I_0001"); // 处理正常结束
        } catch (Exception e) {
            this.getLogger().error("启动估值方案流程失败：", e);
            msg = e.getMessage();
        }

        return new AjaxResult(msg);
	}

//	/**
//	 * 联动：默认债券 - 交易流通场所 获得交易市场
//	 * 变通不使用画面字段过滤
//	 * @return
//	 * @throws Exception
//	 */
//	@ResponseBody
//	@RequestMapping("getTradeMarket")
//	public List<SelectOptionDto> getTradeMarket() {
//		Map<String, Object> param = RequestUtil.getPageParam();
//		param.put("dict_code_parent", DictCode.证券品种.getValue());
//		param.put("dict_code_child", DictCode.估值交易市场.getValue());
//		param.put("item_code_parent", DictCodeListExt1.证券品种_债券.getValue());
//		return valuationMethodScheme001Service.getLinkSelectOption(param);
//	}


	/**
	 * 联动：投资目的 - 估值方法 获得估值方法
	 *
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("getValuationMethod")
	public List<SelectOptionDto> getValuationMethod() {
		List<SelectOptionDto> linkSelectOption = new ArrayList<>();
		Map<String, Object> param = RequestUtil.getPageParam();

		//根据资产代码查询证券品种
		String finprodType = param.get("finprodType").toString();
		String finprodId = param.get("finprodId").toString();
		String secType ="";
		if( StringUtil.isNotEmpty(finprodId)){
			secType = valuationMethodScheme004Service.getSecTypeByFinprodId(finprodId);
		}

		if(StringUtil.isNotEmpty(secType)){
			//如果估值方法需要根据证券品种和投资目的共同联动的，故使用配置不方便，直接后台处理
			param.put("secType",secType);
			linkSelectOption = valuationMethodScheme004Service.getResultByInvAimAndSecType(linkSelectOption,param);
		}else{
			////估值方法根据投资目的联动
			if(StringUtil.isNotEmpty(param.get("invAim"))){
				param.put("dict_code_parent", DictCode.投资目的.getValue());
				param.put("dict_code_child", DictCode.估值方法.getValue());
				param.put("item_code_parent", param.get("invAim"));
				linkSelectOption =valuationMethodScheme004Service.getLinkSelectOption(param);
			}

			//估值方法根据证券品种联动
			//当证券品种为01债券时，使用投资目的进行联动
			if(StringUtil.isNotEmpty(secType)){
				param.put("dict_code_parent", DictCode.证券品种.getValue());
				param.put("dict_code_child", DictCode.估值方法.getValue());
				param.put("item_code_parent", secType);
				linkSelectOption =valuationMethodScheme004Service.getLinkSelectOption(param);
			}
		}
		//投资目的联动处理end
		return linkSelectOption;
	}

	/**
	 * 联动：交易市场 - 估值市价 获得估值市价
	 *
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("getValuationPrice")
	public List<SelectOptionDto> getValuationPrice() {
		Map<String, Object> param = RequestUtil.getPageParam();
		List<SelectOptionDto> selectOptionDtoList = new ArrayList<>();
		String tradeMarket = param.get("tradeMarket").toString();
		//根据资产代码查询证券品种
		String finprodId = param.get("finprodId").toString();
		String secType ="";
		if( StringUtil.isNotEmpty(finprodId)){
			secType = valuationMethodScheme004Service.getSecTypeByFinprodId(finprodId);
		}
		//但证券品种为基金时，且交易场所为场外，市价来源特殊处理为 管理人报价07
		if(StringUtil.isNotEmpty(secType) ){
			if(DictCodeListExt1.证券品种_基金.getValue().equals(secType) && StringUtil.isNotEmpty(tradeMarket) &&
					StringUtil.equals(tradeMarket,DictCodeListExt1.估值交易市场_场外.getValue())){
				//管理人报价07
				SelectOptionDto selectOptionDto = new SelectOptionDto();
				selectOptionDto.setId(DictCodeList.估值市价_管理人报价.getValue());
				selectOptionDto.setText(DictCodeList.估值市价_管理人报价.getCodeName());
				selectOptionDtoList.add(selectOptionDto);
				return selectOptionDtoList;
			}
			param.put("dict_code_parent", DictCode.证券品种.getValue());
			param.put("dict_code_child", DictCode.估值市价.getValue());
			param.put("item_code_parent", secType);
			selectOptionDtoList = valuationMethodScheme004Service.getLinkSelectOption(param);
		}
		return selectOptionDtoList;
	}

	/**
	 * 联动：证券品种 - 获得投资目的
	 *
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("getInvAim")
	public List<SelectOptionDto> getInvAim() {
		Map<String, Object> param = RequestUtil.getPageParam();
		//根据资产代码查询证券品种
		String finprodId = param.get("finprodId").toString();
		String secType ="";
		if( StringUtil.isNotEmpty(finprodId)){
			secType = valuationMethodScheme004Service.getSecTypeByFinprodId(finprodId);
		}
		param.put("dict_code_parent", DictCode.证券品种.getValue());
		param.put("dict_code_child", DictCode.投资目的.getValue());
		param.put("item_code_parent", secType);
		return valuationMethodScheme004Service.getLinkSelectOption(param);
	}


	/**
	 * 根据资产代码获取证券品种
	 *
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("getSecTypeByFinprodId")
	public AjaxResult getSecTypeByFinprodId() {
		AjaxResult ar = new AjaxResult();
		Map<String, Object> param = RequestUtil.getPageParam();
		//根据资产代码查询证券品种
		String finprodId = (String)param.get("finprodId");
		String secType ="";
		if( StringUtil.isNotEmpty(finprodId)){
			secType = valuationMethodScheme004Service.getSecTypeByFinprodId(finprodId);
		}
		ar.setData(secType);
		return ar;
	}



}
