/*
 * 文件名：ReportContentEnum002Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：qiangchengxi
 * 修改时间：2018年11月7日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.disclosure.information.config.reportcontentenum.controller;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
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
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.disclosure.common.common.constant.DisclosureDictCode;
import com.joyintech.tams.disclosure.information.config.reportcontentenum.dto.ReportContentEnum001Dto;
import com.joyintech.tams.disclosure.information.config.reportcontentenum.dto.ReportContentEnum003Dto;
import com.joyintech.tams.disclosure.information.config.reportcontentenum.service.ReportContentEnum001Service;
import com.joyintech.tams.disclosure.information.config.reportcontentenum.service.ReportContentEnum002Service;
import com.joyintech.tams.disclosure.information.manage.workmanage.dto.ReportWorkManage004Dto;
import com.joyintech.tams.disclosure.information.reveal.detailshow.service.RepDetailsShow001Service;
/**
 * 左侧栏新建
 * 〈一句话功能简述〉
 * 〈功能详细描述〉
 * @author lishicheng
 * @version 1.0 2018年11月7日
 * @see ReportContentEnum002Controller
 * @since 1.0
 */
@Controller
@PageId("ReportContentEnum002")
@RequestMapping("/disclosure/information/config/reportcontentenum/ReportContentEnum002")
public class ReportContentEnum002Controller  extends BaseController{
	
	@Autowired
	private ReportContentEnum001Service reportContentEnum001Service;
	
	@Autowired
    private RepDetailsShow001Service repDetailsShow001Service;
	@Autowired
    private ReportContentEnum002Service reportContentEnum002Service;
	/**
	 * 内容初始化
	 * {@inheritDoc}
	 */		
    @Override
    public String init(Model model) {
    	this.setSelectOptionToModel(model, DictCode.作业规则1);
		this.setSelectOptionToModel(model, DictCode.作业规则2);
		this.setSelectOptionToModel(model, DictCode.作业规则3);
		this.setSelectOptionToModel(model, DictCode.收益类型);
		this.setSelectOptionToModel(model, DictCode.募集方式);
		this.setSelectOptionToModel(model, DictCode.发行方式);
		this.setSelectOptionToModel(model, DictCode.发行状态);
		this.setSelectOptionToModel(model, DictCode.开放模式);
		this.setSelectOptionToModel(model, DictCode.是否);
		this.setSelectOptionToModel(model, DisclosureDictCode.产品类型);
    	String id = getParam("id");	  	
		String operateType = getParam("operateType");
		ReportContentEnum001Dto dto = new ReportContentEnum001Dto();
		if (StringUtils.isNotEmpty(id)){
			dto = reportContentEnum001Service.findById(id);
		}
		dto.setOperateType(operateType);
        model.addAttribute("FINPRODID_LIST",JsonUtils.objectArray2Json(this.getFinprodId().toArray()));

		model.addAttribute("entity", dto);
		model.addAttribute("prodInfoList", JsonUtils.objectArray2Json(repDetailsShow001Service.getProdInfo().toArray()));
        return super.init(model);
    }
    /**
     * 新建修改时check
     * <一句话功能简述>
     * <功能详细描述>
     * @param dto
     * @param bindingResult
     * @return
     * @throws Exception
     */
	@ResponseBody
	@RequestMapping("check")
	public AjaxResult check(@RequestBody @Valid ReportContentEnum001Dto  dto, BindingResult bindingResult) {
		String msg = StringUtil.EMPTY;
		String operateType = dto.getOperateType();

//		 唯一性验证,指标名称
		if (OPERATE_TYPE_SUBMIT.equals(dto.getOperateType()) && !bindingResult.hasErrors()) {// submit事件 && 基础验证通过
			//指标名称
			String id = dto.getId();
			List<ReportContentEnum001Dto> list = reportContentEnum001Service.findbyIndexId(id);
			if (CollectionUtils.isNotEmpty(list)) {
				msg = "指标代码已存在！请重新输入。";
				return new AjaxResult(msg, MessageType.错误消息);
			}
			String name = dto.getIndexName();
			List<ReportContentEnum001Dto> list1 = reportContentEnum001Service.findbyIndexName(name);
			if (CollectionUtils.isNotEmpty(list1)) {
				msg = "指标名称已存在！请重新输入。";
				return new AjaxResult(msg, MessageType.错误消息);
			}
		}
		if (OPERATE_TYPE_SAVE.equals(dto.getOperateType()) && !bindingResult.hasErrors()) {// submit事件 && 基础验证通过
			String name = dto.getIndexName();
			List<ReportContentEnum001Dto> list1 = reportContentEnum001Service.findbyIndexName(name);
			if (CollectionUtils.isNotEmpty(list1)) {
				for(ReportContentEnum001Dto searchDto:list1){
					if(!StringUtil.equals(dto.getId(), searchDto.getId())){
						msg = "指标名称已存在！请重新输入。";
						return new AjaxResult(msg, MessageType.错误消息);
					}
				}
			}
		}
		return commonCheck(bindingResult, operateType);
	}
	
	/**
	 * 左侧栏新建
	 * <一句话功能简述>
	 * <功能详细描述>
	 * @param dto
	 * @param bindingResult
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = PAGE_SAVE, method = RequestMethod.POST)
	public AjaxResult save(@RequestBody @Valid ReportContentEnum001Dto dto, BindingResult bindingResult) {
		String msg = StringUtil.EMPTY;
		String operateType = dto.getOperateType();
		if ("submit".equals(operateType)) {			
			reportContentEnum001Service.insert(dto);
			msg = "新建成功！";
		} else if ("save".equals(operateType)) {	
			reportContentEnum001Service.updateByPrimaryKeySelective(dto);
			msg = "修改成功！";
		}

		return new AjaxResult(msg);
	}
	
	/**
     * 产品下拉框数据选择
     *
     * @return 返回的数据
     * @exception Exception
     *                异常
     * @since 1.0
     */
    @RequestMapping("/getFinprodId")
    @ResponseBody
    public List<SelectOptionDto> getFinprodId() {

        List<SelectOptionDto> listSelectDto = new ArrayList<SelectOptionDto>();
        SelectOptionDto dto;
        dto = new SelectOptionDto();dto.setId("001");dto.setText("产品001");listSelectDto.add(dto);
        dto = new SelectOptionDto();dto.setId("002");dto.setText("产品002");listSelectDto.add(dto);
        dto = new SelectOptionDto();dto.setId("003");dto.setText("产品003");listSelectDto.add(dto);
        dto = new SelectOptionDto();dto.setId("004");dto.setText("产品004");listSelectDto.add(dto);
        dto = new SelectOptionDto();dto.setId("005");dto.setText("产品005");listSelectDto.add(dto);
        dto = new SelectOptionDto();dto.setId("006");dto.setText("产品006");listSelectDto.add(dto);
        dto = new SelectOptionDto();dto.setId("007");dto.setText("产品007");listSelectDto.add(dto);
        dto = new SelectOptionDto();dto.setId("008");dto.setText("产品008");listSelectDto.add(dto);
        dto = new SelectOptionDto();dto.setId("009");dto.setText("产品009");listSelectDto.add(dto);
        dto = new SelectOptionDto();dto.setId("0010");dto.setText("产品010");listSelectDto.add(dto);
        dto = new SelectOptionDto();dto.setId("0011");dto.setText("产品011");listSelectDto.add(dto);
        dto = new SelectOptionDto();dto.setId("0012");dto.setText("产品012");listSelectDto.add(dto);
        return listSelectDto;

        // Map<String,Object> map=new HashMap<String,Object>();
        // map.put("finprodType",DictCodeListExt1.金融产品类型_理财产品.getValue());
        // return reportConfirmManage001Service.getFinprodId(map);
    }
    /**
	 * 根据作业编号查询不适用产品
	 * @return
	 */
	@RequestMapping("/findRulePage")
	@ResponseBody
	public List<ReportContentEnum003Dto> findRulePage() {
		if (StringUtil.isNotEmpty(this.getParam("id"))) {
			return reportContentEnum002Service.findRulePage(this.getParam("id"));
		}
		return new ArrayList<ReportContentEnum003Dto>();
	}
}
