/*
 * 文件名：CreditRightsManagement002Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：huwenbin
 * 修改时间：2019年1月8日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.invest.risk.creditmanagement.creditrightsmanagement.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.joyintech.workflow.engine.exception.WorkFlowException;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.base.dto.UploadFile;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.select.SelectDto;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.risk.creditmanagement.creditrightsmanagement.dto.CreditRightsManagementDto;
import com.joyintech.tams.invest.risk.creditmanagement.creditrightsmanagement.service.CreditRightsManagementService;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;

/**
 * 授信权数管理
 * @author huwenbin
 *
 */

@Controller
@PageId("CreditRightsManagement002")
@RequestMapping("/invest/risk/creditmanagement/creditrightsmanagement/CreditRightsManagement002")
public class CreditRightsManagement002Controller extends BaseController{
	/**
	 * 
	 */
	@Autowired
	private CreditRightsManagementService creditRightsManagementService;
	
	/**
     * 业务编号组件
     */
    @Autowired(required=false)
    private BaseGetBusinessId bussGetBussinessIdService;
	
	@Override
    public String init(Model model) {
		this.setSelectOptionToModel(model, DictCode.平安资产类型);
		List<SelectOptionDto> assetSecTypeList = makeAllAssetSecType();
        model.addAttribute("ASSET_SEC_TYPE", JsonUtils.objectArray2Json(assetSecTypeList.toArray()));
        //
        String seqno = getParam("seqno");
        //
        String workflowId = getParam("workflowId");
        if (StringUtil.isNotEmpty(seqno)) {
            CreditRightsManagementDto creditRightsManagementDto = creditRightsManagementService.selectDetail(seqno);
            if(!StringUtil.isNotEmpty(creditRightsManagementDto.getEffDateOld())){
            	creditRightsManagementDto.setEffDateOld(creditRightsManagementDto.getEffDate());
            }
            model.addAttribute("entity", creditRightsManagementDto);
        }
        else if(StringUtil.isNotEmpty(workflowId)){
        	CreditRightsManagementDto creditRightsManagementDto = creditRightsManagementService.getBussinessEntityByWorkflowId(workflowId);
        	if(!StringUtil.isNotEmpty(creditRightsManagementDto.getEffDateOld())){
            	creditRightsManagementDto.setEffDateOld(creditRightsManagementDto.getEffDate());
            }
        	model.addAttribute("entity", creditRightsManagementDto);
        }
        return super.init(model);
    }
	
	/**
     * 保存按钮点击时的check
     *
     * @param dto 业务实体
     * @param result BindingResult
     * @return check结果
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("check")
    public AjaxResult check(@RequestBody @Validated({ValidateGroupEdit.class}) CreditRightsManagementDto dto, BindingResult result) {
        return commonCheck(result, dto.getOperateType());
    }
    /**
     * 
     * <一句话功能简述>
     * <功能详细描述>
     * @param dto 业务实体
     * @param br AjaxResult
     * @return AjaxResul
     * @throws Exception Exception
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("save")
    public AjaxResult save(@RequestBody @Validated({ValidateGroupEdit.class}) CreditRightsManagementDto dto, BindingResult br) throws WorkFlowException {
        if(br.hasErrors()){
            return new AjaxResult(br);
        }
        if(StringUtil.isEmpty(dto.getSeqno())){
            /*BussinessCodeParameter param = new BussinessCodeParameter();
            param.setCommonBusinessType(DictCodeList.共通业务类型_市场机构管理);
            try {*/
        	//bussGetBussinessIdService !=null ? bussGetBussinessIdService.buildBussinessCode(param):
                String seqno = IDGenerator.generate();
                dto.setSeqno(seqno);
            /*}
            catch (Exception e) {
                logger.error(e.getMessage(),e);
            }*/
        }
        ProcessParameter<CreditRightsManagementDto> processParameter = new ProcessParameter<CreditRightsManagementDto>();
        WorkFlowParameter<CreditRightsManagementDto> params = new WorkFlowParameter<CreditRightsManagementDto>();
        params.setPageId(this.getPageId());
        params.setElementId(dto.getElementId());
        params.setBusinessCode(item->item.getSeqno());
        params.setBusinessType(DictCodeList.共通业务类型_风险权数管理.getValue());

        // 设置流程业务说明, entity即传入的dto
        params.setComment(
            entity -> "资产一级分类"+ "(" + entity.getAssetTypeOneStr()+ ")" 
                +  "-" + "资产二级分类"+ "(" + entity.getAssetTypeTwoStr()+ ")"
            		+ "生效日"+"(" + entity.getEffDateStr() + ")");
        processParameter.setWorkFlowParameter(params);
        String msg = creditRightsManagementService.start(processParameter, dto);
        return new AjaxResult(msg);
    }
	
    /**
     * 
     * 获取资产二级分类
     * <功能详细描述>
     * @return
     * @see
     * @since 1.0
     */
    private List<SelectOptionDto> makeAllAssetSecType() {
        List<SelectOptionDto> listFloatRate = new ArrayList<SelectOptionDto>();
        SelectDto selectDto1 = this.getSelectDataFromDataDic(DictCode.WIND一级分类_含债券一级分类等, null);
        for (SelectOptionDto dto : selectDto1.getOptions()) {
            listFloatRate.add(dto);
        }
        SelectDto selectDto2 = this.getSelectDataFromDataDic(DictCode.存款类资产类型, null);
        for (SelectOptionDto dto : selectDto2.getOptions()) {
            listFloatRate.add(dto);
        }
        SelectDto selectDto3 = this.getSelectDataFromDataDic(DictCode.项目类一级分类, null);
        for (SelectOptionDto dto : selectDto3.getOptions()) {
            listFloatRate.add(dto);
        }
        SelectDto selectDto4 = this.getSelectDataFromDataDic(DictCode.基金类资产类型, null);
        for (SelectOptionDto dto : selectDto4.getOptions()) {
            listFloatRate.add(dto);
        }
        SelectDto selectDto5 = this.getSelectDataFromDataDic(DictCode.计划类资产一级分类, null);
        for (SelectOptionDto dto : selectDto5.getOptions()) {
            listFloatRate.add(dto);
        }
        SelectDto selectDto6 = this.getSelectDataFromDataDic(DictCode.拆借类型, null);
        for (SelectOptionDto dto : selectDto6.getOptions()) {
            listFloatRate.add(dto);
        }
        SelectDto selectDto7 = this.getSelectDataFromDataDic(DictCode.回购类型, null);
        for (SelectOptionDto dto : selectDto7.getOptions()) {
            listFloatRate.add(dto);
        }
        return listFloatRate;
    }
}
