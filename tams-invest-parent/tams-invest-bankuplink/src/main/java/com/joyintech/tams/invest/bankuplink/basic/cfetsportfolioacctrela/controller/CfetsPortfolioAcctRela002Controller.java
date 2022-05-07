/*
 * 文件名：CfetsPortfolioAcctRela001Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：yufei
 * 修改时间：2020年5月11日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.bankuplink.basic.cfetsportfolioacctrela.controller;

import javax.validation.Valid;

import com.joyintech.framework.util.SysDataUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.bankuplink.basic.cfetsportfolioacctrela.dto.CfetsPortfolioAcctRela001Dto;
import com.joyintech.tams.invest.bankuplink.basic.cfetsportfolioacctrela.service.CfetsPortfolioAcctRelaService;
import com.joyintech.tams.invest.common.constant.BussinessDictCode;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.util.WorkFlowUtil;

/**
 * CFETS账户关联新建
 * 
 * @author yufei
 * @version 1.0 2020年5月11日
 * @see CfetsPortfolioAcctRela002Controller
 * @since 1.0
 */
@Controller
@PageId("CfetsPortfolioAcctRela002")
@RequestMapping("/invest/bankuplink/basic/cfetsportfolioacctrela/CfetsPortfolioAcctRela002")
public class CfetsPortfolioAcctRela002Controller extends BaseController {

    /**
     * {@inheritDoc}
     */
    @Autowired
    private CfetsPortfolioAcctRelaService cfetsPortfolioAcctRelaService;

    /**
     * {@inheritDoc}
     */
    @Override
    public String init(Model model) {
		this.setSelectOptionToModel(model, BussinessDictCode.账户来源);
		String workflowId = getParam("workflowId");
		String partyId = getParam("partyId");
		String portfolioId = getParam("portfolioId");
		String cfetsAcctType = getParam("cfetsAcctType");
		if (!WorkFlowUtil.isNotFromToDoList() && StringUtil.isNotEmpty(workflowId)) {
			CfetsPortfolioAcctRela001Dto dto = cfetsPortfolioAcctRelaService.getBussinessEntityByWorkflowId(workflowId);
			model.addAttribute("entity", dto);
			model.addAttribute("workflowId", workflowId);
		} else if (WorkFlowUtil.isNotFromToDoList() && StringUtil.isNotEmpty(partyId)
				&& StringUtil.isNotEmpty(portfolioId)) {
			CfetsPortfolioAcctRela001Dto dto = cfetsPortfolioAcctRelaService.queryInfoByPrimaryKey(portfolioId, partyId,
					cfetsAcctType);
			model.addAttribute("entity", dto);
		} else {
			CfetsPortfolioAcctRela001Dto dto = new CfetsPortfolioAcctRela001Dto();
			dto.setCfetsAcctType(BussinessDictCodeList.账户来源_理财.getValue());
            model.addAttribute("entity", dto);
		}
		return super.init(model);
    }

    /**
     * 保存时的check
     * 
     * @param dto 验证对象
     * @param result 验证结果
     * @return 验证信息
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("check")
    public AjaxResult check(@Valid @RequestBody CfetsPortfolioAcctRela001Dto dto,
                            BindingResult result) {
        return commonCheck(result, dto.getOperateType());
    }

    /**
     * 保存
     * 
     * @param dto 保存对象
     * @param br 验证信息
     * @return 操作结果
     * @throws Exception 异常信息
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("save")
    public AjaxResult save(@RequestBody CfetsPortfolioAcctRela001Dto dto, BindingResult br)
        throws Exception {
        if (br.hasErrors()) {
            return new AjaxResult(br);
        }
        if(!StringUtil.isNotEmpty(dto.getPartyIdOld())){
            dto.setPartyIdOld(dto.getPartyId());
        }
        if(!StringUtil.isNotEmpty(dto.getPortfolioIdOld())){
            dto.setPortfolioIdOld(dto.getPortfolioId());
        }
        if(!StringUtil.isNotEmpty(dto.getCfetsAcctTypeOld())){
            dto.setCfetsAcctTypeOld(dto.getCfetsAcctType());
        }
        ProcessParameter<CfetsPortfolioAcctRela001Dto> processParameter = new ProcessParameter<CfetsPortfolioAcctRela001Dto>();
        WorkFlowParameter<CfetsPortfolioAcctRela001Dto> params = new WorkFlowParameter<CfetsPortfolioAcctRela001Dto>();
        params.setPageId(this.getPageId());
        params.setElementId(dto.getElementId());
        params.setBusinessCode(dto.getPortfolioId()+"-"+ dto.getPartyId()+"-" + dto.getCfetsAcctType());
        params.setBusinessType(DictCodeListExt3.共通业务类型_CFETS账户关联.getValue());
        params.setComment(entity -> this.getMessage("CfetsPortfolioAcctRela001.portfolioId") + "("
                                    + entity.getPortfolioId() + ")"
                                    + this.getMessage("CfetsPortfolioAcctRela001.partyId") + "("
                                    + entity.getPartyId() + ")"
                                    + this.getMessage("CfetsPortfolioAcctRela001.cfetsAcctType")
                                    + "(" + SysDataUtil.getNameFromDataDict(BussinessDictCode.账户来源,entity.getCfetsAcctType())+ ")");
        processParameter.setWorkFlowParameter(params);
        String msg = cfetsPortfolioAcctRelaService.start(processParameter, dto);
        return new AjaxResult(msg);
    }

    /**
     * 行删除与删除按钮 <一句话功能简述> <功能详细描述>
     * 
     * @param dto 业务实体
     * @param br br
     * @return AjaxResult
     * @throws Exception Exception
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping(ACTION_DELETE)
    public AjaxResult delete(@RequestBody CfetsPortfolioAcctRela001Dto dto, BindingResult br)
        throws Exception {
        if (br.hasErrors()) {
            return new AjaxResult(br);
        }
        if(!StringUtil.isNotEmpty(dto.getPartyIdOld())){
            dto.setPartyIdOld(dto.getPartyId());
        }
        if(!StringUtil.isNotEmpty(dto.getPortfolioIdOld())){
            dto.setPortfolioIdOld(dto.getPortfolioId());
        }
        if(!StringUtil.isNotEmpty(dto.getCfetsAcctTypeOld())){
            dto.setCfetsAcctTypeOld(dto.getCfetsAcctType());
        }
        ProcessParameter<CfetsPortfolioAcctRela001Dto> processParameter = new ProcessParameter<CfetsPortfolioAcctRela001Dto>();
        WorkFlowParameter<CfetsPortfolioAcctRela001Dto> params = new WorkFlowParameter<CfetsPortfolioAcctRela001Dto>();
        params.setPageId(this.getPageId());
        params.setElementId(dto.getElementId());
        params.setBusinessCode(dto.getPortfolioId()+"-"+ dto.getPartyId()+"-" + dto.getCfetsAcctType());
        params.setBusinessType(DictCodeListExt3.共通业务类型_CFETS账户关联.getValue());
        params.setComment(entity -> this.getMessage("CfetsPortfolioAcctRela001.portfolioId") + "("
                                    + entity.getPortfolioId() + ")"
                                    + this.getMessage("CfetsPortfolioAcctRela001.partyId") + "("
                                    + entity.getPartyId() + ")"
                                    + this.getMessage("CfetsPortfolioAcctRela001.cfetsAcctType")
                                    + "(" + SysDataUtil.getNameFromDataDict(BussinessDictCode.账户来源,entity.getCfetsAcctType())+ ")");
        processParameter.setWorkFlowParameter(params);
        String msg = cfetsPortfolioAcctRelaService.start(processParameter, dto);
        return new AjaxResult(msg);
    }

}
