/*
 * 文件名：TrdFixIncomeRateInvestTrade001Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：fanshuanghu
 * 修改时间：2018年11月22日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.valuation.trademanage.trdfixincomevaluationtrade.controller;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.RequestUtil;
import com.joyintech.framework.util.SpringContextUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.valuation.component.business.service.CommonBusinessService;
import com.joyintech.tams.valuation.trademanage.trdfixincomevaluationtrade.dto.TrdFixIncomeValuationTradeDto;
import com.joyintech.tams.valuation.trademanage.trdfixincomevaluationtrade.service.TrdFixIncomeValuationTrade002Service;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * @Description:固收类投资交易新建
 * @auther: yuzj
 * @date: 2021/12/30
 * @param:
 * @return:
 */
@Controller
@PageId("TrdFixIncomeValuationTrade002")
@RequestMapping("/valuation/trademanage/trdfixincomevaluationtrade/TrdFixIncomeValuationTrade002")
public class TrdFixIncomeValuationTrade002Controller extends BaseController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
	/**
     * 注入计划类service
     */
    @Autowired
    private TrdFixIncomeValuationTrade002Service trdFixIncomeValuationTrade002Service;

    @Autowired
    private CommonBusinessService commonBusinessService;


    /**
     * 初始化方法
     */
    @Override
    public String init(Model model) {
        this.setSelectOptionToModel(model, DictCode.交易方向, DictCode.币种,DictCode.投资目的);

        String tradeId = getParam("tradeId");
        String workflowId = getParam(GlobalConstant.WORKFLOW_ID);
        if(StringUtil.isNotEmpty(workflowId)){
            TrdFixIncomeValuationTradeDto entity= trdFixIncomeValuationTrade002Service.getBussinessEntityByWorkflowId(workflowId);
            if(!StringUtil.isNotEmpty(entity.getTradeId())){
                entity.setTradeId("提交后自动生成，无需填写");
            }
            model.addAttribute("entity", entity);
            model.addAttribute("workflowId", workflowId);

        }else if (!StringUtil.isNotEmpty(workflowId) && StringUtil.isNotEmpty(tradeId)){
            TrdFixIncomeValuationTradeDto entity = trdFixIncomeValuationTrade002Service.queryTradeInfoByTradeId(tradeId);
            model.addAttribute("entity", entity);
        }

        return super.init(model);
    }

    /**
     * 资产代码下拉框
     *
     * @return 资产代码
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/getAssetId")
    public List<SelectOptionDto> getAssetId() {
        Map<String, Object> param = RequestUtil.getPageParam();
        param.put("assetType","F17");
        param.put("profitType","02");
        return commonBusinessService.getAssetId(param);
    }



    /**
     * @Description:画面点击保存按钮时check
     * @auther: yuzj
     * @date: 2021/12/30
     * @param:
     * @return:
     */
    @ResponseBody
    @RequestMapping("check")
    public AjaxResult check(@RequestBody @Validated({
            ValidateGroupEdit.class}) TrdFixIncomeValuationTradeDto dto, BindingResult result) {
        return commonCheck(result, dto.getOperateType());
    }


    /**
     * @Description:资金账户信息
     * @auther: yuzj
     * @date: 2021/12/30
     * @param:
     * @return:
     */
    @RequestMapping("/getTrdAcctId")
    @ResponseBody
    public List<SelectOptionDto> getTrdAcctId(){
        return commonBusinessService.getTrdAcctId(RequestUtil.getPageParam());
    }


    /**
     * @Description:保存
     * @auther: yuzj
     * @date: 2021/12/30
     * @param:
     * @return:
     */
    @ResponseBody
    @RequestMapping("save")
    public AjaxResult save(@RequestBody @Validated({
            ValidateGroupEdit.class}) TrdFixIncomeValuationTradeDto dto, BindingResult br) throws Exception {
        if (br.hasErrors()) {
            return new AjaxResult(br);
        }
        WorkFlowParameter<TrdFixIncomeValuationTradeDto> params = new WorkFlowParameter<TrdFixIncomeValuationTradeDto>();
        params.setPageId(this.getPageId());
        params.setElementId(dto.getElementId());
        params.setBusinessType(DictCodeListExt3.共通业务类型_固收类资产交易.getValue());

//        //设置主键
//        if(!StringUtil.isNotEmpty(dto.getTradeId())){
//            Date sysDate= DateUtil.platDateFromCache();
//            //新建初始化交易编号
//            dto.setTradeId(trdFixIncomeValuationTrade002Service.createPlanTradeId());
//        }
        params.setBusinessCode(dto.getAssetId());

        // 设置流程业务说明, entity即传入的dto
        params.setComment(
                entity -> SpringContextUtil.getTextValue("资产代码：")+ entity.getAssetId() +"-"+SpringContextUtil.getTextValue("TrdFixIncomeValuationTrade.tradeDate") + ":" + DateUtil.format(entity.getTradeDate())
        );
        //给参数变成ProcessParameter而非WorkFlowParameter
        ProcessParameter<TrdFixIncomeValuationTradeDto> parameter = new ProcessParameter<TrdFixIncomeValuationTradeDto>();
        parameter.setWorkFlowParameter(params);
        //系统页面表IS_NEED_WORKFLOW 是否走流程，01-是，02-否
        String msg = trdFixIncomeValuationTrade002Service.start(parameter, dto);
        return new AjaxResult(msg);
    }

    /**
     * 策略计划类交易代码生成
     * @see
     * @since 1.0
     */
    @PostMapping("createPlanTradeId")
    public String createPlanTradeId() {
        return trdFixIncomeValuationTrade002Service.createPlanTradeId();
    }


}
