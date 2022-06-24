/*
 * 文件名：ShadowPriceScheme003Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：qiangchengxi
 * 修改时间：2018年11月24日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.service.valuation.valuationparametermanage.shadowpricescheme.controller;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Maps;
import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.select.SelectDto;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.RequestUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.service.valuation.valuationparametermanage.shadowpricescheme.dto.ShadowPriceScheme001Dto;
import com.joyintech.service.valuation.valuationparametermanage.shadowpricescheme.dto.ShadowPriceScheme002Dto;
import com.joyintech.service.valuation.valuationparametermanage.shadowpricescheme.service.ShadowPriceScheme001Service;
import com.joyintech.service.valuation.valuationparametermanage.shadowpricescheme.service.ShadowPriceScheme002Service;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.util.WorkFlowUtil;

/**
 * 影子方案右侧明细界面新建 〈一句话功能简述〉 〈功能详细描述〉
 * 
 * @author lishicheng
 * @version 1.0 2018年11月24日
 * @see ShadowPriceScheme003Controller
 * @since 1.0
 */
@RestController(value="serviceShadowPriceScheme003Controller")
@RequestMapping("/ShadowPriceScheme003")
public class ShadowPriceScheme003Controller extends BaseController {

	@Autowired
	private ShadowPriceScheme001Service ShadowPriceScheme001Service;

	@Autowired
	private ShadowPriceScheme002Service ShadowPriceScheme002Service;

	@PostMapping("/selectSchemeDetailBySchId")
	public ShadowPriceScheme002Dto selectSchemeDetailBySchId(@RequestBody String schemeDetailId) {
		return ShadowPriceScheme002Service.selectSchemeDetailBySchId(schemeDetailId);
	}

	/**
	 * 下拉框 ： 方案
	 * 
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/getSchemeForSelect")
	public List<SelectOptionDto> getSchemeForSelect(@RequestBody Map<String, Object> parameters) {
		return ShadowPriceScheme001Service.getSchemeForSelect(parameters);
	}

	/**
	 * 新增/修改验证：基础验证、唯一性
	 * 
	 * @param dto
	 * @param bindingResult
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/getschemeIdByname")
	public ShadowPriceScheme001Dto getschemeIdByname(@RequestBody String schemeId) {
		return ShadowPriceScheme002Service.getschemeIdByname(schemeId);
	}

	@PostMapping("/checkShadowPriceDetial")
	public List<ShadowPriceScheme002Dto> checkShadowPriceDetial(@RequestBody Map<String, Object> pageParam) {
		return ShadowPriceScheme002Service.checkShadowPriceDetial(pageParam);
	}
}
