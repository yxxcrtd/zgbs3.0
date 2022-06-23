package com.joyintech.tams.invest.product.productplanning.assetplannewsdefend.controller;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.util.*;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.product.productplanning.assetplannewsdefend.dto.AssetPlanNewsDefend;
import com.joyintech.tams.invest.product.productplanning.assetplannewsdefend.dto.AssetPlanNewsDefend002Dto;
import com.joyintech.tams.invest.product.productplanning.assetplannewsdefend.service.AssetPlanNewsDefend002Service;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * 资产规划信息维护
 * @author guyunfei
 * 20191226
 */
@Controller
@PageId("AssetPlanNewsDefend002")
@RequestMapping("/invest/product/productplanning/assetplannewsdefend/AssetPlanNewsDefend002")
public class AssetPlanNewsDefend002Controller extends BaseController {

    @Autowired
    private AssetPlanNewsDefend002Service assetPlanNewsDefend002Service;

    /**
     * 主键生成类
     */
    @Autowired(required = false)
    private BaseGetBusinessId baseGetBusinessId;

    @Override
    @RequestMapping(PAGE_INIT)
    public String init(Model model){
        //系统平台日
        model.addAttribute("sysDate",DateUtil.getSysDate());
        String assetPlanCode = this.getParam("assetPlanCode");
        String workflowId = this.getParam("workflowId");
        if(WorkFlowUtil.isNotFromToDoList() && !StringUtil.isNotEmpty(assetPlanCode)){
            //新建
            AssetPlanNewsDefend002Dto entity = new AssetPlanNewsDefend002Dto();
            entity.setBenchDate(DateUtil.platDateFromCache());
            model.addAttribute("entity",entity);
        }else if(WorkFlowUtil.isNotFromToDoList() && StringUtil.isNotEmpty(assetPlanCode)){
            //非流程中的查看
            AssetPlanNewsDefend002Dto entity = assetPlanNewsDefend002Service.queryData(assetPlanCode);
            model.addAttribute("entity",entity);
        }else if(!WorkFlowUtil.isNotFromToDoList() && StringUtil.isNotEmpty(workflowId)){
            //流程中的查看
            AssetPlanNewsDefend002Dto entity = assetPlanNewsDefend002Service.getBussinessEntityByWorkflowId(workflowId);
            model.addAttribute("entity",entity);
        }
        model.addAttribute("workflowId",workflowId);
        return super.init(model);
    }

    /**
     * 生成资产规划明细
     * @return
     */
    @ResponseBody
    @RequestMapping("/getPlans")
    public List<AssetPlanNewsDefend> getPlans(){
        return assetPlanNewsDefend002Service.getPlans(RequestUtil.getPageParam());
    }

    /**
     * 新增、修改时对表单元素的check
     * @param dto
     * @param br
     * @return
     */
    @ResponseBody
    @RequestMapping("check")
    public AjaxResult check(@RequestBody @Validated AssetPlanNewsDefend002Dto dto, BindingResult br){
        if (br != null && br.hasErrors()) {
            return new AjaxResult(br);
        }
        return this.commonCheck(br, dto.getOperateType());
    }

    /**
     * 新增、修改
     * @param dto
     * @param br
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping("/save")
    public AjaxResult save(@RequestBody AssetPlanNewsDefend002Dto dto,BindingResult br) throws WorkFlowException {

        //资产规划编号生成
        if(StringUtil.isEmpty(dto.getAssetPlanCode())){
            BussinessCodeParameter bussinessCodeParameter = new BussinessCodeParameter();
            bussinessCodeParameter.setCommonBusinessType(DictCodeList.共通业务类型_资产规划信息维护);
            if (baseGetBusinessId != null) {
                String assetPlanCode = baseGetBusinessId.buildBussinessCode(bussinessCodeParameter) != null
                        ? baseGetBusinessId.buildBussinessCode(bussinessCodeParameter)
                        : IDGenerator.generate();
                dto.setAssetPlanCode(assetPlanCode);
            }
        }

        List<AssetPlanNewsDefend> assetPlanNewsDefendList = dto.getAssetPlanNewsDefendList();
        if(CollectionUtils.isNotEmpty(assetPlanNewsDefendList)){
            assetPlanNewsDefendList.forEach(e->{
                //为资产规划系列列表 中的明细设置 资产规划编号
                e.setAssetPlanCode(dto.getAssetPlanCode());
            });
        }

        ProcessParameter<AssetPlanNewsDefend002Dto> processParameter = new ProcessParameter<>();
        WorkFlowParameter<AssetPlanNewsDefend002Dto> params = new WorkFlowParameter<>();
        params.setPageId(this.getPageId());
        params.setElementId(dto.getElementId());
        params.setBusinessCode(dto.getAssetPlanCode());
        params.setBusinessType(DictCodeList.共通业务类型_资产规划信息维护.getValue());
        params.setComment(entity-> "资产规划编号" + "(" + dto.getAssetPlanCode() + ")"
                + "规划开始日" + "(" + DateUtil.format(entity.getVdate(),DateUtil.DEFAULT_DATE_FORMAT_EN) + ")"
                + "规划结束日" + "(" + DateUtil.format(entity.getEdate(),DateUtil.DEFAULT_DATE_FORMAT_EN) + ")"
                + "规划基准日" + "(" + DateUtil.format(entity.getBenchDate(),DateUtil.DEFAULT_DATE_FORMAT_EN) + ")"
                + "目标资产规模" + "(" + entity.getAssetTargetScale() + ")");
        processParameter.setWorkFlowParameter(params);

        String msg = assetPlanNewsDefend002Service.start(processParameter, dto);
        return new AjaxResult(msg);
    }
}
