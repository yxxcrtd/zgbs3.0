/*
 * 文件名：BussFinProductSaleParamController.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangliang
 * 修改时间：2018年10月17日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.component.business.prod.controller;

import com.joyintech.framework.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.component.business.core.service.MstWorkflowCommonService;
import com.joyintech.tams.component.business.prod.BussFinProductSaleParamService;
import com.joyintech.tams.component.business.prod.dto.BussFinProductSaleParamDto;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.util.RequestUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.component.business.report.prod.BussRepFinProductInfoService;
import com.joyintech.tams.component.business.report.prod.dto.BussRepFinProductInfoDto;


/**
 * 销售参数业务组件
 * 
 * @author zhangliang
 * @version 1.0 2018年10月17日
 * @see BussFinProductSaleParamCommonRateController
 * @since 1.0
 */
@Controller
@RequestMapping("/component/prod/BussFinProductSaleParamCommonRate")
public class BussFinProductSaleParamCommonRateController extends BaseController {
    
    /**
     * {@linkplain}
     */
    @Autowired
    private MstWorkflowCommonService mstWorkflowCommonService;
    
    /**
     * {@linkplain}
     */
    @Autowired
    private BussFinProductSaleParamService bussFinProductSaleParamService;
    
    /**
     * {@linkplain}
     */
    @Autowired
    private BussRepFinProductInfoService bussRepFinProductInfoService;

    /**
     * {@inheritDoc}
     */
    @Override
    public String init(Model model) {
        this.setSelectOptionToModel(model, DictCode.销售渠道, DictCode.销售区域_中债,
        		DictCode.币种钞汇标志,DictCode.募集方式,DictCode.销售部门,
        		DictCode.币种,DictCode.目标客户类型,DictCode.是否);
        model.addAttribute("YES_OR_NO_Y", DictCodeList.是否_是.getValue());
        model.addAttribute("YES_OR_NO_N", DictCodeList.是否_否.getValue());
        model.addAttribute("NOTE_EXCHANGE_FLAG_01", "01");
        // 父画面传入的pageId
        model.addAttribute(GlobalConstant.PAGE_ID, this.getParam(GlobalConstant.PAGE_ID));
        // 父画面传入的金融产品代码
        model.addAttribute("finprodId", this.getParam("finprodId"));
        // 父画面传入的产品处理模式id
        model.addAttribute("dealMode", this.getParam("dealMode"));
        // 父画面传入的收益类型id
        model.addAttribute("profitType", this.getParam("profitType"));
        model.addAttribute("operationType", this.getParam("operationType"));//add by yejiejie
        
        model.addAttribute("entityName", StringUtil.defaultString(this.getParam("entityName"), BussFinProductSaleParamService.DEFAULT_ENTITY_NAME));
        // 父画面传入的同业机构
        model.addAttribute("sameOrg", this.getParam("sameOrg"));
        
        //update zhangweiqi 设置默认值 begin
        model.addAttribute("ccy", !StringUtil.isNotEmpty(this.getParam("ccy"))||"null".equals(this.getParam("ccy"))?"CNY":this.getParam("ccy"));
        model.addAttribute("repCashPrinCcy", "CNY");
        model.addAttribute("repCashProfCcy", "CNY");
      //update zhangweiqi 设置默认值 end
        model.addAttribute("issueType", StringUtil.isNotEmpty(this.getParam("issueType")) ? this.getParam("issueType") : DictCodeListExt1.募集方式_公募.getValue());
        model.addAttribute("firstSaleVdate", this.getParam("firstSaleVdate"));
        model.addAttribute("firstSaleMdate", this.getParam("firstSaleMdate"));
        model.addAttribute("saleDepartment", this.getParam("saleDepartment"));
        if (StringUtil.equals(RequestUtil.getParam("isTodoList"), "true")) {
            // 父画面传入的工作流代码
            BussFinProductSaleParamDto dto = mstWorkflowCommonService.getBusinessEntityByWorkFlowId(
                this.getParam(GlobalConstant.WORKFLOW_ID),
                StringUtil.defaultString(this.getParam("entityName"),
                    BussFinProductSaleParamService.DEFAULT_ENTITY_NAME),
                BussFinProductSaleParamDto.class);
            dto.setFirstSaleVdate(DateUtil.parse(this.getParam("firstSaleVdate")));
        	dto.setFirstSaleMdate(DateUtil.parse(this.getParam("firstSaleMdate")));
//        	SimpleDateFormat sdf = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy",Locale.US);        	
//        	try {
//				dto.setFirstSaleVdate(sdf.parse(this.getParam("firstSaleVdate")));
//				dto.setFirstSaleMdate(sdf.parse(this.getParam("firstSaleMdate")));
//			} catch (ParseException e) {
//				logger.error(e.getMessage(),e);
//			}            
        	model.addAttribute(GlobalConstant.ENTITY, dto);
            BussRepFinProductInfoDto repdto = mstWorkflowCommonService.getBusinessEntityByWorkFlowId(
                    this.getParam(GlobalConstant.WORKFLOW_ID),
                    "bussRepFinProductInfoDto",
                        BussRepFinProductInfoDto.class);
            model.addAttribute("repCashPrinCcy", repdto.getRepCashPrinCcy());
            model.addAttribute("repCashProfCcy", repdto.getRepCashProfCcy());
            model.addAttribute("repInvestorType", repdto.getRepInvestorType());
            model.addAttribute("repRaiseAmtPlan", repdto.getRepRaiseAmtPlan());
        }
        else if (StringUtil.isNotEmpty(this.getParam("finprodId"))){
        	
        	BussFinProductSaleParamDto dto=bussFinProductSaleParamService.querySaleParamInfoByFinprodId(this.getParam("finprodId"));
        	dto.setFirstSaleVdate(DateUtil.parse(this.getParam("firstSaleVdate")));
        	dto.setFirstSaleMdate(DateUtil.parse(this.getParam("firstSaleMdate")));
//        	SimpleDateFormat sdf = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy",Locale.US);        	
//        	try {
//				dto.setFirstSaleVdate(sdf.parse(this.getParam("firstSaleVdate")));
//				dto.setFirstSaleMdate(sdf.parse(this.getParam("firstSaleMdate")));
//			} catch (ParseException e) {
//				logger.error(e.getMessage(),e);
//			}            
        	model.addAttribute(GlobalConstant.ENTITY, dto);
            BussRepFinProductInfoDto repdto= bussRepFinProductInfoService.select(this.getParam("finprodId"));
            model.addAttribute("repCashPrinCcy", repdto.getRepCashPrinCcy());
            model.addAttribute("repCashProfCcy", repdto.getRepCashProfCcy());
            model.addAttribute("repInvestorType", dto.getInvestorType());
//            model.addAttribute("repRaiseAmtPlan", repdto.getRepRaiseAmtPlan());
        }
        return "component/prod/BussFinProductSaleParamCommonRate";
    }
    
    /**
     * 根据金融产品代码查询销售参数信息
     * 
     * @param finprodId 金融产品代码
     * @return 销售参数信息
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("querySaleParamInfoByFinprodId")
    public BussFinProductSaleParamDto querySaleParamInfoByFinprodId(String finprodId){
        return bussFinProductSaleParamService.querySaleParamInfoByFinprodId(finprodId);
    }
}
