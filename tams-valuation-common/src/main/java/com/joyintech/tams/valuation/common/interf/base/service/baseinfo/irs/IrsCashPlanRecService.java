/*
 * 文件名：LendStandardPlanRecService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：qiangchengxi
 * 修改时间：2018年8月25日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.valuation.common.interf.base.service.baseinfo.irs;

import com.joyintech.tams.common.base.entity.FinCashPlan;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.valuation.common.interf.base.constant.InterfCommonConstant;
import com.joyintech.tams.valuation.common.interf.base.dto.*;
import com.joyintech.tams.valuation.common.interf.base.service.common.TemplateAnalysisForXMLService;
import com.joyintech.tams.valuation.common.interf.base.util.InterfHelper;
import com.joyintech.tams.valuation.common.interf.tempTables.entity.ValDlTargetRatePlan;
import com.joyintech.tams.valuation.common.interf.tempTables.entity.ValIrsCashPlan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 利率互换付息计划(VAL_IRS_CASH_PLAN)
 * @author 殷亚
 * @date 2022/4/15 9:46
 * @return
 * @see
 * @version 1.0
 */
@Service
@Scope("prototype")
public class IrsCashPlanRecService {
	 /**
     * TemplateAnalysisForXMLService
     */
    @Autowired
    private TemplateAnalysisForXMLService templateAnalysisForXMLService;    
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public IrsCommonResult execute(TemplateSourceFlgDTO templateSourceFlgDTO,
								   TemplateOrderInfoReaderResult acctEntity) {
		IrsCommonResult result = new IrsCommonResult();
		List<ValIrsCashPlan> listValIrsCashPlan = new ArrayList<ValIrsCashPlan>();
	    TemplateCheckReturnResult returnResult = new TemplateCheckReturnResult();
        // 1、文件读取解析
        returnResult = templateAnalysisForXMLService.execute(acctEntity);
        
        // 2、进行数据有效性的check,并生出进入日志表的数据结构
		listValIrsCashPlan = returnResult.getT(ValIrsCashPlan.class);
        
        // 3、更具有效数据，进行数据的编辑，并生成进入业务表的数据结构
        //现金流计划
        List<FinCashPlan> listValCashPlan = handleValIrsCashPlan(listValIrsCashPlan);
        result.setListValCashPlan(listValCashPlan);
        
        
        // 添加日志
        result.setValInterflog(returnResult.getValInterflog());
        return result;
	}
    /**
     * 更具有效数据，进行数据的编辑，并生成进入业务表的数据结构
     * <一句话功能简述>
     * <功能详细描述>
     * @param listValIrsCashPlan
     * @return 现金流支付计划 list
     */
	private List<FinCashPlan> handleValIrsCashPlan(List<ValIrsCashPlan> listValIrsCashPlan) {
		//现金流计划 list
		List<FinCashPlan> listValCashPlan = new ArrayList<FinCashPlan>();
		for(ValIrsCashPlan dto :listValIrsCashPlan){
			//现金流计划
			FinCashPlan  valCashPlan = this.getValIrsCashPlan(dto);
			listValCashPlan.add(valCashPlan);			
		}
		return listValCashPlan;
	}
	/**
	 * 现金流计划表 接收
	 * <一句话功能简述>
	 * <功能详细描述>
	 * @param dto
	 * @return FinCashPlan
	 */
	private FinCashPlan getValIrsCashPlan(ValIrsCashPlan dto) {
		FinCashPlan valFinCashPlan = new FinCashPlan();
		//现金流序号 = C.现金流序号
		valFinCashPlan.setCashNum(dto.getOrderNo().longValue());
		//未调整的计算开始日 = C.理论开始日
		valFinCashPlan.setVdateUnadjust(dto.getTheVdate());
		//未调整的计算结束日 = C.理论截止日
		valFinCashPlan.setMdateUnadjust(dto.getTheMdate());
		//调整后的计算开始日 = C.实际计息开始日
		valFinCashPlan.setVdate(dto.getActVdate());
		//调整后的计算结束日 = C.实际计息截止日
		valFinCashPlan.setMdate(dto.getActMdate());
		//调整后的计划支付日 = C.支付日
		valFinCashPlan.setPayDate(dto.getPayDay());
		//支付计划
		if(DictCodeListExt1.现金流二级类型_支付.getValue().equals(dto.getDealType())){
			//现金流代码 =InterfHelper.getMd5CashId_（C.拆借标的代码,C.业务类型,数据字典：现金流二级类型.利息）
			valFinCashPlan.setCashId(InterfHelper.getMd5CashId_(dto.getTradeId(),
					DictCodeList.业务类型_利率互换交易.getValue(),DictCodeListExt1.现金流二级类型_支付.getValue()));
		}
		//收取计划
		else{
			//现金流代码 =InterfHelper.getMd5CashId_（C.拆借标的代码,C.业务类型,数据字典：现金流二级类型.本金）
			valFinCashPlan.setCashId(InterfHelper.getMd5CashId_(dto.getTradeId(),
					DictCodeList.业务类型_利率互换交易.getValue(),DictCodeListExt1.现金流二级类型_收取.getValue()));
		}

		valFinCashPlan.setIsPayInt(DictCodeList.是否_是.getValue());
		//现金流单位 = 数据字典：现金流单位.总额
		valFinCashPlan.setCashUnitType(DictCodeList.现金流单位_总额.getValue());
		//金融产品代码  = C.资产代码
		valFinCashPlan.setFinprodId(dto.getTradeId());
		//金融产品类型 = C.业务类型
		valFinCashPlan.setFinprodType(DictCodeListExt1.金融产品类型_利率互换.getValue());
		//分支序号 = 设置全局变量(默认为0）
		valFinCashPlan.setBranch(InterfCommonConstant.分支序号_默认);
		//支付方式 = 数据字典：支付方式.现金
		valFinCashPlan.setPayType(DictCodeList.支付方式_现金.getValue());
		return valFinCashPlan;
	}	
}
