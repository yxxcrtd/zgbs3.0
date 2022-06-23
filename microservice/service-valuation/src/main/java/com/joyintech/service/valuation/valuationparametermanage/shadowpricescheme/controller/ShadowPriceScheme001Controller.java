/*
 * 文件名：ShadowPriceScheme001Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：qiangchengxi
 * 修改时间：2018年11月22日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.service.valuation.valuationparametermanage.shadowpricescheme.controller;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
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
import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.ExportUtil;
import com.joyintech.framework.util.FileNameUtil;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.RequestUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.groups.ValidateGroupDelete;
import com.joyintech.system.constant.SystemGlobalConstant;
import com.joyintech.service.valuation.assets.fund.dto.ValAssetsFundQuery002Dto;
import com.joyintech.service.valuation.valuationparametermanage.shadowpricescheme.dto.ShadowPriceScheme001Dto;
import com.joyintech.service.valuation.valuationparametermanage.shadowpricescheme.dto.ShadowPriceScheme002Dto;
import com.joyintech.service.valuation.valuationparametermanage.shadowpricescheme.service.ShadowPriceScheme001Service;
import com.joyintech.service.valuation.valuationparametermanage.shadowpricescheme.service.ShadowPriceScheme002Service;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;
import com.joyintech.tams.valuation.common.dto.ClassForStringLIst;
import com.joyintech.workflow.engine.WorkflowContext;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.util.WorkFlowUtil;

/**
 * 影子定价方案左侧controller 〈一句话功能简述〉 〈功能详细描述〉
 * 
 * @author lishicheng
 * @version 1.0 2018年11月22日
 * @see ShadowPriceScheme001Controller
 * @since 1.0
 */
@RestController(value="serviceShadowPriceScheme001Controller")
@RequestMapping("/ShadowPriceScheme001")
public class ShadowPriceScheme001Controller extends BaseController {

	@Autowired
	private ShadowPriceScheme001Service shadowPriceScheme001Service;

	@Autowired
	private ShadowPriceScheme002Service shadowPriceScheme002Service;

	/**
	 * 左侧影子方案查询 <一句话功能简述> <功能详细描述>
	 * 
	 * @return
	 * @throws Exception
	 */
	@PostMapping("findShadowPrice/{page}/{length}")
	public Page<ShadowPriceScheme001Dto> findShadowPrice(@RequestBody Map<String, Object> pageParam,
			@PathVariable("page") int page, @PathVariable("length") int length) {
		return shadowPriceScheme001Service.findShadowPrice(page, length, pageParam);
	}

	/**
	 * 右侧影子方案明细查询 <一句话功能简述> <功能详细描述>
	 * 
	 * @return
	 * @throws Exception
	 */
	@PostMapping("findShadowPriceDetial/{page}/{length}")
	public Page<ShadowPriceScheme002Dto> findShadowPriceDetial(@RequestBody Map<String, Object> pageParam,
			@PathVariable("page") int page, @PathVariable("length") int length){
		return shadowPriceScheme002Service.findShadowPriceDetial(page, length, pageParam);
	}

	/**
	 * 左侧删除检验 <一句话功能简述> <功能详细描述>
	 * 
	 * @param dto
	 * @param result
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/findScheme")
	public List<ShadowPriceScheme001Dto> findScheme(@RequestBody Map<String, Object> params) {
		return shadowPriceScheme001Service.findScheme(params);
	}

	@PostMapping("/selectSchemeBySchId")
	public ShadowPriceScheme001Dto selectSchemeBySchId(@RequestBody String schId) {
		return shadowPriceScheme001Service.selectSchemeBySchId(schId);
	}

	@PostMapping("/getDefaultScheme")
	public ShadowPriceScheme001Dto getDefaultScheme() {
		return shadowPriceScheme001Service.getDefaultScheme();
	}

	/**
	 * 右侧删除检验 <一句话功能简述> <功能详细描述>
	 * 
	 * @param dto
	 * @param result
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/getBusinessDataInFlow")
	public ClassForStringLIst getBusinessDataInFlow(@RequestBody ProcessMark processMark) {
		ClassForStringLIst dto = new ClassForStringLIst();
		dto.setPara(shadowPriceScheme001Service.getBusinessDataInFlow(processMark));
		return dto;
	}

	/**
	 * 导出 <一句话功能简述> <功能详细描述>
	 * 
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/findShadowDetial")
	public List<ShadowPriceScheme002Dto> findShadowDetial(@RequestBody Map<String, Object> pageParam) {
		return shadowPriceScheme002Service.findShadowDetial(pageParam);
	}

	
	@PostMapping("insert")
    public void insert(@RequestBody ShadowPriceScheme001Dto dto) {
		shadowPriceScheme001Service.doComplete(dto,OperationType.NEW);
    }
    
    /**
     * 修改
     * @param dto
     * @throws Exception
     * @see
     * @since 1.0
     */
    @PostMapping("update")
    public void update(@RequestBody ShadowPriceScheme001Dto dto) {
    	shadowPriceScheme001Service.doComplete(dto,OperationType.MODIFY);
    }
    
    /**
     * 删除
     * @param dto
     * @throws Exception
     * @see
     * @since 1.0
     */
    @PostMapping("delete")
    public void delete(@RequestBody ShadowPriceScheme001Dto dto) {
    	shadowPriceScheme001Service.doComplete(dto,OperationType.DELETE);
    }
}
