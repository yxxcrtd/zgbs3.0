/*
 * 文件名：ValNetPlanQuotation003Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：qiangchengxi
 * 修改时间：2018年10月15日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.product.paythevatquery.controller;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import com.joyintech.framework.constant.GlobalConstant;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.product.paythevatquery.dto.PayTheVatQuery001Dto;
import com.joyintech.tams.invest.product.paythevatquery.service.PayTheVatQuery002Service;
import com.joyintech.tams.invest.product.paythevatquery.service.PayTheVatQuery003Service;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.service.LeaveMarkProcessService;
import com.joyintech.workflow.engine.util.WorkFlowUtil;


/**
 * 缴税增值税撤销〈一句话功能简述〉 〈功能详细描述〉
 * 
 * @author chufengjie
 * @version 1.0 2019年02月28日
 * @see PayTheVatQuery003Controller
 * @since 1.0
 */
@Controller("finproductPayTheVatQuery003Controller")
@PageId("FinproductPayTheVatQuery003")
@RequestMapping("/invest/product/paythevatquery/PayTheVatQuery003")
public class PayTheVatQuery003Controller extends BaseController {

    @Autowired
    private PayTheVatQuery003Service payTheVatQuery003Service;
    
    @Autowired
    private PayTheVatQuery002Service payTheVatQuery002Service;
    
    @SuppressWarnings("rawtypes")
    @Autowired(required=false)
    private LeaveMarkProcessService leaveMarkProcessService;
    
    @Override
    public String init(Model model) {
        this.setSelectOptionToModel(model, DictCode.交易方向,DictCode.增值税品种);

        // 工作流ID
        String workflowId = this.getParam(GlobalConstant.WORKFLOW_ID);
        // 交易编号
        String totleTradeId = this.getParam("totleTradeId");
        //增值税品种
        String taxClass = this.getParam("taxClass");
        // 查询界面展示元素
        //组合代码list
        List<String> listProdId =  new ArrayList<String>();
        String queryDate = null;
        if (StringUtil.isNotEmpty(totleTradeId)) {
            List<PayTheVatQuery001Dto> dto = payTheVatQuery003Service.queryTotleTradeId(totleTradeId);
            model.addAttribute("totleTradeId", totleTradeId);
            model.addAttribute("tradeId", dto.get(0).getTradeId());
            model.addAttribute("finprodId", dto.get(0).getFinprodId());
            model.addAttribute("portfolioId", dto.get(0).getPortfolioId());
            model.addAttribute("portfolioName", dto.get(0).getPortfolioName());
            model.addAttribute("finprodMarketId", dto.get(0).getFinprodMarketId());
            model.addAttribute("finprodAbbr", dto.get(0).getFinprodAbbr());
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
            model.addAttribute("waterTrafficTax", dto.get(0).getWaterTrafficTax());
            model.addAttribute("additionalTax", dto.get(0).getAdditionalTax());
            model.addAttribute("taxTotal", dto.get(0).getTaxTotal());
            model.addAttribute("taxidenType", dto.get(0).getTaxidenType());
            model.addAttribute("taxidenType", dto.get(0).getTaxidenType());
            model.addAttribute("remark", dto.get(0).getRemark());
            //新增通道增值税缴纳的字段
            model.addAttribute("taxClass",taxClass);
            model.addAttribute("channelId",dto.get(0).getChannelId());
            model.addAttribute("channelName",dto.get(0).getChannelName());
            model.addAttribute("profitType",dto.get(0).getProfitType());
            model.addAttribute("payType",dto.get(0).getPayType());
            
            listProdId.add(dto.get(0).getPortfolioId());
            
            queryDate = dto.get(0).getSeitleDate();
        }
        if (StringUtil.isNotEmpty(workflowId)) {
            PayTheVatQuery001Dto dto = payTheVatQuery003Service.getBussinessEntityByWorkflowId(workflowId);
            model.addAttribute("tradeId", dto.getTradeId());
            model.addAttribute("finprodId", dto.getFinprodId());
			model.addAttribute("portfolioId", dto.getPortfolioId());
			model.addAttribute("portfolioName", dto.getPortfolioName());
            model.addAttribute("finprodMarketId", dto.getFinprodMarketId());
            model.addAttribute("finprodAbbr", dto.getFinprodAbbr());
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
            model.addAttribute("waterTrafficTax", dto.getWaterTrafficTax());
            model.addAttribute("additionalTax", dto.getAdditionalTax());
            model.addAttribute("totleTradeId", dto.getTotleTradeId());
            model.addAttribute("taxTotal", dto.getTaxTotal());
//          List<PayTheVatQuery001Dto> dto1 = payTheVatQuery003Service.queryTotleTradeId(dto.getTotleTradeId());
            model.addAttribute("taxidenType", dto.getTaxidenType());
            model.addAttribute("remark", dto.getRemark());
            
            //新增通道增值税缴纳的字段
            model.addAttribute("taxClass",dto.getTaxClass());//modify by jiangpeng 20210513 ZGBSSE-380
            model.addAttribute("channelId",dto.getChannelId());
            model.addAttribute("channelName",dto.getChannelName());
            model.addAttribute("profitType",dto.getProfitType());
            model.addAttribute("payType",dto.getPayType());
            
            listProdId.add(dto.getPortfolioId());
            queryDate = dto.getSeitleDate() ;
        }
        // model.addAttribute("workflowId", workflowId);
        String editFlag = this.getParam("editFlag");
        //OPEX-650【产品缴纳】缴纳查询界面，选择纳税日期查询条件进行查询，报数据接口异常 guyunfei 20191227 start
        List<String> addTaxTypeList= payTheVatQuery002Service.getAddTaxType(listProdId,
                BussinessDictCodeList.金融产品类型_投资组合.getValue(), queryDate,null,null);
        //OPEX-650【产品缴纳】缴纳查询界面，选择纳税日期查询条件进行查询，报数据接口异常 guyunfei 20191227 end
        model.addAttribute("isShowWaterTrafficTax",!StringUtil.isEmptyList(addTaxTypeList)
            &&addTaxTypeList.contains(BussinessDictCodeList.税类型_水利基金税.getValue()));
        model.addAttribute("editFlag", editFlag);
        return super.init(model);
    }

    private Object getParams() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * 缴税增值税撤销走流程 <一句话功能简述> <功能详细描述>
     * 
     * @param dto
     * @param br
     * @return
     */
    
    @ResponseBody
    @RequestMapping("doSave")
    public AjaxResult doSave(@RequestBody @Valid PayTheVatQuery001Dto dto, BindingResult br) {
        if (br.hasErrors()) {
            return new AjaxResult(br);
        }
        // 启动流程
        ProcessParameter<PayTheVatQuery001Dto> proParam = new ProcessParameter<>();
        WorkFlowParameter<PayTheVatQuery001Dto> params = new WorkFlowParameter<>();

        // 业务代码（资产代码）
        params.setBusinessCode(dto.getTotleTradeId());
        // 业务类型
        params.setBusinessType(DictCodeList.共通业务类型_缴纳增值税.getValue());
        
        // 页面id
        params.setPageId(this.getPageId());
        // 元素id
        params.setElementId(this.getParam(GlobalConstant.ELEMENT_ID));
        
        if(StringUtil.equals(dto.getTaxClass(), DictCodeListExt3.增值税品种_通道增值税.getValue())) {
            params.setComment(entity -> WorkFlowUtil.concatComment(entity.getChannelName()
                +"("+(entity.getChannelId())  +")" , "交易金额("+entity.getTaxTotal()+")" , "交易日期(" + DateUtil.format(DateUtil.parse(entity.getSeitleDate()), "yyyy-MM-dd")+")",
                    "交易编号("+dto.getTotleTradeId()+")"));            
        } else {
            params.setComment(entity -> WorkFlowUtil.concatComment((StringUtil.isNotEmpty(entity.getFinprodAbbr()) ? entity.getFinprodAbbr() : entity.getPortfolioId())
                +"("+(StringUtil.isNotEmpty(entity.getFinprodMarketId()) ? entity.getFinprodMarketId() : entity.getPortfolioName())  +")" , "交易金额("+entity.getTaxTotal()+")" , "交易日期(" + DateUtil.format(DateUtil.parse(entity.getSeitleDate()), "yyyy-MM-dd")+")",
                    "交易编号("+dto.getTotleTradeId()+")"));            
        }
        // 设置流程业务说明, entity即传入的dto

        // 塞入流程参数
        proParam.setWorkFlowParameter(params);
        proParam.setOperationType(OperationType.REVOKE);

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
    @SuppressWarnings("unchecked")
    @ResponseBody
    @RequestMapping("check")
    public AjaxResult check(@RequestBody @Valid PayTheVatQuery001Dto dto, BindingResult bindingResult){
        String msg = StringUtil.EMPTY;
        String operateType = null;
        String tradeId = dto.getTradeId();
        
		if (WorkFlowUtil.isNotFromToDoList()) {
			//判断方案是否在流程中
			ProcessMark processMarkb = new ProcessMark();
			processMarkb.setPageId("FinproductPayTheVatQuery003");
			processMarkb.setBusinessDataPk1(dto.getTotleTradeId());
			List<String> resultListb = leaveMarkProcessService.getBusinessDataInFlow(processMarkb);
			if (!StringUtil.isEmptyList(resultListb)) {
				msg = this.getMessage("COMMON_E_0021", dto.getTotleTradeId(), dto.getFinprodId());
				return new AjaxResult(msg, MessageType.错误消息);
			}
		}
        // 缴税/退税日期
        String seitleDate = dto.getSeitleDate();
        String pltDate = DateUtil.format(DateUtil.platDateFromCache(), DateUtil.DEFAULT_DATE_FORMAT_EN);
        List<PayTheVatQuery001Dto> list = payTheVatQuery003Service.findFinproduct(dto.getFinprodId(), seitleDate);
        List<PayTheVatQuery001Dto> list1 = payTheVatQuery003Service.findPtradeid(tradeId);
        //20210331 wyn ZGBSS-1328
//        if (CollectionUtils.isNotEmpty(list)) {
//            msg = "缴税/退税日期要大于等于该产品的账套日期";
//            return new AjaxResult(msg, MessageType.错误消息);
//        }
        if (CollectionUtils.isNotEmpty(list1)) {
            msg = "该产品已撤销。";
            return new AjaxResult(msg, MessageType.错误消息);
        }
        if(DateUtil.compareDate(seitleDate, pltDate)<0) {
            msg = "不可以撤销缴纳日期小于平台日的交易。";
            return new AjaxResult(msg, MessageType.错误消息);
        }
        return commonCheck(bindingResult, operateType);
    }
}
