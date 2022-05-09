/*
 * 文件名：BussFinProductOpenPlanController.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：张亮
 * 修改时间：2018年9月17日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.component.business.prod.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.validation.Valid;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import org.apache.commons.codec.binary.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.joyintech.tams.common.base.entity.FinOpenPlan;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tams.component.business.core.service.MstWorkflowCommonService;
import com.joyintech.tams.component.business.prod.BussFinProductOpenPlanService;
import com.joyintech.tams.component.business.prod.dto.BussFinProductOpenPlanDto;
import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.SubPageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.RequestUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.workflow.engine.util.WorkFlowUtil;

/**
 * 开放规则
 * 
 * @author 张亮
 * @version 1.0 2018年9月17日
 * @see BussFinProductOpenPlanCommonRateController
 * @since 1.0
 */
@Controller
@RequestMapping("/component/prod/BussFinProductOpenPlanCommonRate")
//一体化对应etliu
@SubPageId("BussFinProductOpenPlan")
public class BussFinProductOpenPlanCommonRateController extends BaseController {

    /**
     * 开放计划组件
     */
    @Autowired
    private BussFinProductOpenPlanService bussFinProductOpenPlanService;;

    /**
     * 平台日基础组件
     */
    @Autowired(required = false)
    private IPlatformDate platformDate;

    /**
     * {@linkplain}
     */
    @Autowired
    private MstWorkflowCommonService mstWorkflowCommonService;

   
    /**
     * {@inheritDoc}
     */
    @Override
    public String init(Model model) {
    	// 一体化对应etliu
//        setSelectOptionToModel(model,
//            new DictCode[] {DictCode.开放模式, DictCode.营业日准则,
//                DictCode.是否, DictCode.周期单位, DictCode.推算规则,
//                DictCode.星期号, DictCode.日期号, DictCode.工作日号});
    	setSelectOptionToModel(model,
                new DictCode[] {DictCode.开放模式, DictCode.营业日准则,
                    DictCode.是否, DictCode.周期单位, DictCode.推算方法,
                    DictCode.星期号, DictCode.日期号, DictCode.工作日号, 
                    DictCode.月周数,DictCode.净值日规则,DictCode.推算方式,
                    DictCode.开放天数类型});

        model.addAttribute("YES", DictCodeList.是否_是.getValue());
        model.addAttribute("OPENMODE_01", DictCodeList.开放模式_定期开放.getValue());
        model.addAttribute("OPENMODE_02", DictCodeList.开放模式_高频开放.getValue());
        model.addAttribute("OPENMODE_03", DictCodeList.开放模式_周期开放.getValue());
        model.addAttribute("DISPOSE_PATTERN_01", DictCodeList.处理模式_普通.getValue());
        model.addAttribute("DISPOSE_PATTERN_05", BussinessDictCodeList.处理模式_开放阶梯.getValue());
        model.addAttribute("DISPOSE_PATTERN_06", BussinessDictCodeList.处理模式_日添利.getValue());
        model.addAttribute("DISPOSE_PATTERN_07", BussinessDictCodeList.处理模式_智能日添利.getValue());
        model.addAttribute("DISPOSE_PATTERN_03", BussinessDictCodeList.处理模式_滚动N天.getValue());
        //add by wm 2019-10-09 start 增加产品处理模式-周期型
        model.addAttribute("DISPOSE_PATTERN_10", DictCodeList.处理模式_周期型.getValue());
        //add by wm 2019-10-09 end 增加产品处理模式-周期型
        model.addAttribute("PERIOD_UNIT_01", DictCodeListExt1.周期单位_日.getValue());
        model.addAttribute("PERIOD_UNIT_02", DictCodeListExt1.周期单位_周.getValue());
        model.addAttribute("PERIOD_UNIT_03", DictCodeListExt1.周期单位_月.getValue());
        model.addAttribute("PERIOD_UNIT_04", DictCodeListExt1.周期单位_年.getValue());
        model.addAttribute("TERM_TYPE_01", DictCodeListExt2.期限类型_单一固定期限.getValue());
        model.addAttribute("TERM_TYPE_02", DictCodeListExt2.期限类型_多固定期限.getValue());
        model.addAttribute("TERM_TYPE_03", DictCodeListExt2.期限类型_无固定期限.getValue());
        
     // 一体化对应etliu
//        model.addAttribute("CALC_METHOD_01", DictCodeList.推算方法_起息日向后.getValue());
//        model.addAttribute("CALC_METHOD_03", DictCodeList.推算方法_首次开放日.getValue());
        model.addAttribute("CALC_RULE_01", DictCodeList.推算方式_起息日向后.getValue());
        model.addAttribute("CALC_RULE_03", DictCodeList.推算方式_首次支付日.getValue());
        model.addAttribute("CALC_METHOD_02", DictCodeList.推算方法_开放开始日.getValue());
        model.addAttribute("CALC_METHOD_01", DictCodeList.推算方法_开放确认日.getValue());
        
        model.addAttribute("TDAY_RULE_02", DictCodeList.营业日准则_向后调整.getValue());
        model.addAttribute("platDate", platformDate.getSysDateStr());
        model.addAttribute("ISSUE_MODE_02", DictCodeList.开放类型_开放式.getValue());
        model.addAttribute("PROFIT_TYPE_01", DictCodeList.收益类型_净值型.getValue());
        model.addAttribute("sameFlag",DictCodeList.是否_是.getValue());
        model.addAttribute("OPEN_DAYS_TYPE_01",DictCodeList.开放天数类型_固定天数.getValue());
        model.addAttribute("OPEN_DAYS_TYPE_02",DictCodeList.开放天数类型_每天.getValue());
        setEntity(model);
     // 收益类型
        model.addAttribute("profitType", StringUtil.defaultString(this.getParam("profitType")));
        // 父画面pageId
        model.addAttribute("pageId", StringUtil.defaultString(this.getParam("pageId")));
        model.addAttribute("pageModel", StringUtil.defaultString(this.getParam("pageModel")));
        // 父画面开放类型Id
        model.addAttribute("operationType", StringUtil.defaultString(this.getParam("operationType")));
        
        // 付息计划付息规则id
        model.addAttribute("payCycle", StringUtil.defaultString(this.getParam("payCycle")));
        model.addAttribute("INTPAY_PERIOD_03", DictCodeListExt1.付息周期_申购同频.getValue());

        // 父画面处理模式Id
        model.addAttribute("dealMode", StringUtil.defaultString(this.getParam("dealMode")));
        // 父画面起息日
        model.addAttribute("vdate", StringUtil.defaultString(this.getParam("vdate")));
        // 父画面到期日
        model.addAttribute("mdate", StringUtil.defaultString(this.getParam("mdate")));
        // 父画面交易日历
        model.addAttribute("tradCalender",
            StringUtil.defaultString(this.getParam("tradCalender")));
        // 父画面募集开始日
        model.addAttribute("raiseStrDate",
            StringUtil.defaultString(this.getParam("raiseStrDate")));
        // 父画面募集结束日
        model.addAttribute("raiseEndDate",
            StringUtil.defaultString(this.getParam("raiseEndDate")));
        // 父画面申购确认天数
        model.addAttribute("purConfDays", StringUtil.defaultString(this.getParam("purConfDays")));
        // 父画面是否周期型
        model.addAttribute("isCycle", StringUtil.defaultString(this.getParam("isCycle")));
        // 父画面封闭期限
        model.addAttribute("closeTerm", StringUtil.defaultString(this.getParam("closeTerm")));

        model.addAttribute("bussFinProductOpenPlanDto",
            StringUtil.defaultString(this.getParam("bussFinProductOpenPlanDto"),
                BussFinProductOpenPlanService.DEFAULT_ENTITY_NAME));
        model.addAttribute("bussFinProductOpenPlanRedDto",
                StringUtil.defaultString(this.getParam("bussFinProductOpenPlanRedDto"),
                    BussFinProductOpenPlanService.DEFAULT_ENTITY_NAME));    
        return "component/prod/BussFinProductOpenPlanCommonRate";
    }

    /**
     * 设置开放计划信息
     * 
     * @param model {@link Model}
     */
    private void setEntity(Model model) {
        String workflowId = this.getParam(GlobalConstant.WORKFLOW_ID);
        String entityName = StringUtil.defaultString(this.getParam("bussFinProductOpenPlanDto"),
            BussFinProductOpenPlanService.DEFAULT_ENTITY_NAME);
        if (!WorkFlowUtil.isNotFromToDoList()) {
            BussFinProductOpenPlanDto dto = mstWorkflowCommonService.getBusinessEntityByWorkFlowId(
                workflowId, entityName, BussFinProductOpenPlanDto.class);
            
            String sameFlag=DictCodeList.是否_否.getValue();
            if(dto!=null&&StringUtils.equals(DictCodeList.申赎开放类型_申赎开放.getValue(),dto.getOpenType()))
				 sameFlag=DictCodeList.是否_是.getValue();
            model.addAttribute("sameFlag",sameFlag);
            model.addAttribute("purSpeed", StringUtil.defaultString(this.getParam("purSpeed")));
            model.addAttribute("redSpeed", StringUtil.defaultString(this.getParam("redSpeed")));
            model.addAttribute(GlobalConstant.ENTITY, dto);
            model.addAttribute("entityName", entityName);
            BussFinProductOpenPlanDto dtored = mstWorkflowCommonService.getBusinessEntityByWorkFlowId(
                    workflowId, "bussFinProductOpenPlanRedDto", BussFinProductOpenPlanDto.class);
                model.addAttribute(GlobalConstant.ENTITY, dto);
            model.addAttribute("redentity", dtored);   
            model.addAttribute(GlobalConstant.WORKFLOW_ID, workflowId);
        }
        else if (StringUtil.isNotEmpty(this.getParam("finprodId"))) {
        	 List<BussFinProductOpenPlanDto> list=bussFinProductOpenPlanService.queryOpenPlanInfoByFinprodId(
                    this.getParam("finprodId"));
        	 BussFinProductOpenPlanDto openPlanDto=null;
        	 BussFinProductOpenPlanDto openPlanRedDto=null;
        	 String sameFlag=DictCodeList.是否_否.getValue();
        	 for(BussFinProductOpenPlanDto d:list){
        		 if(StringUtils.equals(DictCodeList.申赎开放类型_申赎开放.getValue(), d.getOpenType()))
        				 sameFlag=DictCodeList.是否_是.getValue();
        		 if(StringUtils.equals(DictCodeList.申赎开放类型_申购开放.getValue(), d.getOpenType())||
        				 StringUtils.equals(DictCodeList.申赎开放类型_申赎开放.getValue(), d.getOpenType())){
        			 openPlanDto=d;
        		 }else{
        			 openPlanRedDto=d;
        		 }
        	 }
        	 
        	 model.addAttribute("sameFlag",sameFlag);
        	 model.addAttribute("redSpeed", StringUtil.defaultString(this.getParam("redSpeed")));
        	 model.addAttribute("purSpeed", StringUtil.defaultString(this.getParam("purSpeed")));
            model.addAttribute(GlobalConstant.ENTITY,
            		openPlanDto);
            model.addAttribute("redentity",
            		openPlanRedDto);
            model.addAttribute("finprodId", this.getParam("finprodId"));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPageId() {
        return this.getParam(GlobalConstant.PAGE_ID);
    }

    /**
     * 生成计划check
     * 
     * @param dto 开放信息
     * @param result check结果
     * @return check信息
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("checkInputParam")
    public AjaxResult checkInputParam(@Valid @RequestBody BussFinProductOpenPlanDto dto,
                                      BindingResult result) {
        if (result != null && result.hasErrors()) {
            return new AjaxResult(result);
        }
        AjaxResult wr = new AjaxResult();
        
        if (!StringUtil.isNotEmpty(dto.getDealMode())) {
            wr.appendMessage(new ValidMessage("dealMode",
                getMessage("BUSS_COM_E_0038", getMessage("FinProductBuild002.dealMode"))));
        }
        if (StringUtil.equals(dto.getIsCycle(), DictCodeList.是否_是.getValue())){
            if (!StringUtil.isNotEmpty(dto.getCloseTerm())) {
                wr.appendMessage(new ValidMessage("closeDays",
                    getMessage("BUSS_COM_E_0038", getMessage("FinProductBuild002.closeDays"))));
            }
        }
        if (!StringUtil.isNotEmpty(dto.getPurConfDays())) {
            wr.appendMessage(new ValidMessage("bussFinProductOpenPlanDto_purSpeed",
                getMessage("BUSS_COM_E_0038", getMessage("FinProductOpenRule.purSpeed"))));
        }
        if (!StringUtil.isNotEmpty(dto.getTradCalender())) {
            wr.appendMessage(new ValidMessage("calendarId",
                getMessage("BUSS_COM_E_0038", getMessage("FinProductBuild002.calendarId"))));
        }
        if (!StringUtil.isNotEmpty(dto.getVdate())) {
            wr.appendMessage(new ValidMessage("vdate",
                getMessage("BUSS_COM_E_0038", getMessage("FinProductBuild002.vdate"))));
        }
        if (!StringUtil.isNotEmpty(dto.getMdate())) {
            wr.appendMessage(new ValidMessage("mdate",
                getMessage("BUSS_COM_E_0038", getMessage("FinProductBuild002.mdate"))));
        }
        if (StringUtil.isNotEmpty(dto.getVdate())
            && StringUtil.isNotEmpty(dto.getMdate())) {
            if (DateUtil.compareDate(dto.getVdate(),
                dto.getMdate()) >= 0) {
                wr.appendMessage(new ValidMessage("vdate",
                    getMessage("BUSS_COM_E_0045", getMessage("FinProductBuild002.vdate"),
                        getMessage("FinProductBuild002.mdate"))));
            }
        }
        if(StringUtil.equals("02", dto.getOpenType())){
        	//赎回的净值日准则已隐藏不需要校验 add by menghongwei 20190624
	        if (!StringUtil.isNotEmpty(dto.getCpriceRule()) && !StringUtil.equals("FinProductBuild002",dto.getPageId())) {
	            if(StringUtil.isNotEmpty(dto.getProfitType()) && StringUtil.equals("01",dto.getProfitType())){
                    wr.appendMessage(new ValidMessage("bussFinProductOpenPlanRedDto_cpriceRule",
                            getMessage("BUSS_COM_E_0038", getMessage("FinProductOpenRule.cpriceRule"))));
                }
	        }
        }else{
        	if ((!StringUtil.equals(dto.getProfitType(),"03")&&!StringUtil.equals(dto.getProfitType(),"02")) && !StringUtil.isNotEmpty(dto.getCpriceRule()) && !StringUtil.equals("FinProductBuild002",dto.getPageId())) {
	            wr.appendMessage(new ValidMessage("bussFinProductOpenPlanDto_cpriceRule",
	                getMessage("BUSS_COM_E_0038", getMessage("FinProductOpenRule.cpriceRule"))));
	        }
        }
        // 判断首次开放日是否是节假日
        if (bussFinProductOpenPlanService.checkFirstOpenDateIsHoliday(dto)) {
            wr.setData(this.getMessage("BUSS_COM_E_0039",
                this.getMessage("FinProductOpenRule.firstOpenDate")));
        }
        return wr;
    }

    /**
     * 生成开放计划
     * 
     * @return 开放计划
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("previewList")
    public List<FinOpenPlan> previewList() {
        BussFinProductOpenPlanDto dto = BeanUtil.mapToObject(RequestUtil.getPageParam(),
            BussFinProductOpenPlanDto.class);
        if (StringUtil.isNotEmpty(this.getParam("workflowId"))
            && !StringUtil.equals(this.getParam("quickFlag"), "true")) {
            String openType = dto.getOpenType();
            if (!StringUtils.equals(DictCodeList.申赎开放类型_赎回开放.getValue(), openType)) {
                BussFinProductOpenPlanDto bussFinProductOpenPlanDto = mstWorkflowCommonService.getBusinessEntityByWorkFlowId(
                    this.getParam("workflowId"), this.getParam("entityName"),
                    BussFinProductOpenPlanDto.class);
                if (bussFinProductOpenPlanDto != null && !StringUtil.isEmptyList(bussFinProductOpenPlanDto.getFinOpenPlanList())) {
                    return bussFinProductOpenPlanDto.getFinOpenPlanList();
                }
            }
            else {
                BussFinProductOpenPlanDto bussFinProductOpenPlanDto = mstWorkflowCommonService.getBusinessEntityByWorkFlowId(
                    this.getParam("workflowId"), "bussFinProductOpenPlanRedDto",
                    BussFinProductOpenPlanDto.class);
                if (bussFinProductOpenPlanDto != null && !StringUtil.isEmptyList(bussFinProductOpenPlanDto.getFinOpenPlanList())) {
                    return bussFinProductOpenPlanDto.getFinOpenPlanList();
                }
            }
            return Collections.emptyList();
        }
        else if (StringUtil.isNotEmpty(this.getParam("finprodId"))) {
        	List<FinOpenPlan> list=bussFinProductOpenPlanService.queryOpenPlanListByFinprodId(
                    this.getParam("finprodId"));
        	String openType=this.getParam("openType");
        	List<FinOpenPlan> listr=new ArrayList<FinOpenPlan>();
        	for(FinOpenPlan p:list){
        		if(StringUtils.equals(openType, p.getOpenType()))
        			listr.add(p);
        		
        	}
        	
            return listr;
        }
        //check后才能计算计划
        if(!StringUtil.equals("true", this.getParam("initFlag"))){
           return Collections.emptyList();
        }
        
        // 如果是开放规则调整画面，则查询旧规则下的开放规则，用下一开放日截取，再与查询的新开放计划合并
        if(StringUtil.equals(dto.getPageId(), "FinOpenPlanMaintenance002")) {
        	
        	// 库中查询的历史开放计划初始化
        	List<FinOpenPlan> oldResultListInit = new ArrayList<FinOpenPlan>();
        	// 根据新规则生成的开放计划
        	List<FinOpenPlan> newResultList = new ArrayList<FinOpenPlan>();
        	// 最后返回的开放计划
        	List<FinOpenPlan> lastResultList = new ArrayList<FinOpenPlan>();
        	
        	String nextOpenDay = this.getParam("nextOpenDay"); // 下一开放日
        	oldResultListInit = bussFinProductOpenPlanService.queryOpenPlanListByFinprodId(this.getParam("finprodIdCreatePlan"));
        	
        	List<FinOpenPlan> oldResultList = new ArrayList<FinOpenPlan>();
        	if(!StringUtil.equals(dto.getDealMode(), "10")) { // 产品处理模式不为周期型
        		String openType=this.getParam("openType");
            	// 库中查询的历史开放计划
            	for(FinOpenPlan p:oldResultListInit){
            		if(StringUtils.equals(openType, p.getOpenType()))
            			oldResultList.add(p);
            	}
        	}else {
        		String openType = DictCodeList.申赎开放类型_申购开放.getValue();
            	// 库中查询的历史开放计划
            	for(FinOpenPlan p:oldResultListInit){
            		if(StringUtils.equals(openType, p.getOpenType()))
            			oldResultList.add(p);
            	}
        	}
        	
        	dto.setFirstOpenDate(DateUtil.parse(nextOpenDay));
        	dto.setCalcMethod(DictCodeList.推算方式_首次支付日.getValue()); // 推算方法设置为首次支付日
        	newResultList = bussFinProductOpenPlanService.genOpenPlanList(dto);
        	
			String flagDay = ""; // 新旧计划对比变量
			if (StringUtil.equals(dto.getCalcRule(), DictCodeList.推算方法_开放开始日.getValue())) {
				flagDay = this.getParam("nextOpenDay");
			} else if (StringUtil.equals(dto.getCalcRule(), DictCodeList.推算方法_开放确认日.getValue())
					|| StringUtil.equals(dto.getCalcRule(), DictCodeList.推算方法_开放结束日.getValue())) {
				if (newResultList.size() > 1) {
					flagDay = DateUtil.format(newResultList.get(1).getOpenDateStr());
					newResultList.remove(0); // 删掉第一条
				}
			}
        	
			for (FinOpenPlan finOpenPlan : oldResultList) {
				// 下一开放日大于等于开放开始日
				if (DateUtil.compareDate(flagDay, DateUtil.format(finOpenPlan.getOpenDateStr())) == 1
						|| DateUtil.compareDate(flagDay, DateUtil.format(finOpenPlan.getOpenDateStr())) == 0) {

					// 下一开放日大于等于开放结束日
					if (DateUtil.compareDate(flagDay, DateUtil.format(finOpenPlan.getOpenDateEnd())) == 1
							|| DateUtil.compareDate(flagDay, DateUtil.format(finOpenPlan.getOpenDateEnd())) == 0) {
						lastResultList.add(finOpenPlan);
					} else {
						finOpenPlan.setOpenDateEnd(DateUtil.parse(flagDay));
						lastResultList.add(finOpenPlan); // 截取老开放计划
					}
				}
			}
        	
        	FinOpenPlan oldOne = new FinOpenPlan();
        	FinOpenPlan newOne = new FinOpenPlan();
        	FinOpenPlan middleOne = new FinOpenPlan();
        	
        	if(null!=lastResultList && lastResultList.size()>0) {
        		oldOne = lastResultList.get(lastResultList.size() - 1);
        	}
        	
        	if(null!=newResultList && newResultList.size()>0) {
        		newOne = newResultList.get(0);
        	}
        	
			if (null != oldOne.getOpenDateEnd() && null != newOne.getOpenDateStr()
					&& DateUtil.compareDate(oldOne.getOpenDateEnd(), newOne.getOpenDateStr()) == 0) {
        		BeanUtil.copyPropertiesIgnoreNull(middleOne, newOne);
        		middleOne.setOpenDateStr(oldOne.getOpenDateStr());
        		middleOne.setOpenDateEnd(newOne.getOpenDateEnd());
        		if(null!=lastResultList && lastResultList.size()>0)
        			lastResultList.remove(oldOne);
        		if(null!=newResultList && newResultList.size()>0)
        			newResultList.remove(newOne);
        		
        		lastResultList.add(middleOne);
        	}
        	
        	lastResultList.addAll(newResultList); // 新旧开放规则组合到一起
        	
        	return lastResultList;
        }
        
        return bussFinProductOpenPlanService.genOpenPlanList(dto);
    }
    
    /**
     * 根据金融产品代码查询开放计划信息
     * 
     * @param finprodId 金融产品代码
     * @return 开放计划信息
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("queryOpenPlanInfoByFinprodId")
    public List<BussFinProductOpenPlanDto> queryOpenPlanInfoByFinprodId (String finprodId) {
        return bussFinProductOpenPlanService.queryOpenPlanInfoByFinprodId(finprodId);
    }

}
