/*
 * 文件名：BussFinProductOpenPlanValidator.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：张亮
 * 修改时间：2018年9月21日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.component.business.prod.validator;

import java.util.ArrayList;
import java.util.List;
import com.joyintech.tams.constant.DictCodeListExt1;
import org.springframework.stereotype.Component;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.BeanValidator;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.component.business.prod.dto.BussFinProductOpenPlanDto;
import com.joyintech.tams.constant.DictCodeList;

/**
 * 产品开发计划业务验证
 * 
 * @author 张亮
 * @version 1.0 2018年9月21日
 * @see BussFinProductOpenPlanValidator
 * @since 1.0
 */
@Component
public class BussFinProductOpenPlanValidator implements BeanValidator {
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<ValidMessage> valid(Object value) {
        BussFinProductOpenPlanDto dto = (BussFinProductOpenPlanDto)value;
        List<ValidMessage> resultList = new ArrayList<ValidMessage>();
        if (StringUtil.equals(DictCodeList.开放类型_开放式.getValue() ,dto.getOperationType()) && StringUtil.equals(dto.getPageId(), "FinProductBuild002") || StringUtil.equals(dto.getPageId(), "FinNetValueProductBuild002")|| StringUtil.equals(dto.getPageId(), "FinPlanNetAsset002")|| StringUtil.equals(dto.getPageId(), "FinPlanAssetC002")|| StringUtil.equals(dto.getPageId(), "FinPlanProfitAsset002")){
            // 开放模式
            if (StringUtil.isNotEmpty(dto.getOpenMode())) {
                if (StringUtil.equals(DictCodeList.开放类型_开放式.getValue() ,dto.getOperationType()) 
                		&& (StringUtil.equals(dto.getOpenMode(), DictCodeList.开放模式_定期开放.getValue())
                    || StringUtil.equals(dto.getOpenMode(), DictCodeList.开放模式_周期开放.getValue()))) {
                    if(!StringUtil.isNotEmpty(dto.getOpenDaysType())){
                        resultList.add(new ValidMessage("openDaysType",
                            getMessage("BUSS_COM_E_0038", getMessage("FinProductOpenRule.openDaysType"))));
                    }
                    if(!StringUtil.isNotEmpty(dto.getOpenDaysType())){
                        resultList.add(new ValidMessage("openDaysType",
                            getMessage("BUSS_COM_E_0038", getMessage("FinProductOpenRule.openDaysType"))));
                    }
                    // 开放天数、开放周期、开放周期单位、推算方法、营业日准则必填
                    if (StringUtil.equals(DictCodeList.开放天数类型_固定天数.getValue(), dto.getOpenDaysType()) && !StringUtil.isNotEmpty(dto.getOpenDays())) {
                        resultList.add(new ValidMessage("openDays",
                            getMessage("BUSS_COM_E_0038", getMessage("FinProductOpenRule.openDays"))));
                    }
                    if (!StringUtil.isNotEmpty(dto.getOpenCycle())) {
                        resultList.add(new ValidMessage("openCycle", getMessage("BUSS_COM_E_0038",
                            getMessage("FinProductOpenRule.openCycle"))));
                    }
                    if (StringUtil.isEmpty(dto.getOpenCycleUnit())) {
                        resultList.add(new ValidMessage("openCycleUnit", getMessage("BUSS_COM_E_0038",
                            getMessage("FinProductOpenRule.openCycleUnit"))));
                    }
                    if (StringUtil.isEmpty(dto.getCalcMethod())) {
                        resultList.add(new ValidMessage("calcMethod", getMessage("BUSS_COM_E_0038",
                            getMessage("FinProductOpenRule.calcMethod"))));
                    }
                    if (StringUtil.isEmpty(dto.getTradedayRule())) {
                        resultList.add(new ValidMessage("tradedayRule", getMessage("BUSS_COM_E_0038",
                            getMessage("FinProductOpenRule.tradedayRule"))));
                    }
                    if (StringUtil.isEmpty(dto.getCalcRule())) {
                        resultList.add(new ValidMessage("calcRule",
                            getMessage("BUSS_COM_E_0038", getMessage("FinProductOpenRule.calcRule"))));
                    }
                    if (StringUtil.equals(dto.getOpenMode(),
                        DictCodeList.开放模式_定期开放.getValue())) {
                        // 开放周期
                        if (StringUtil.isNotEmpty(dto.getOpenCycleUnit()) && StringUtil.equals(
                            dto.getOpenCycleUnit(), DictCodeListExt1.周期单位_周.getValue())) {
                            if (StringUtil.isEmpty(dto.getDayOfWeek())) {
                                resultList.add(new ValidMessage("dayOfWeek", getMessage(
                                    "BUSS_COM_E_0038", getMessage("FinProductOpenRule.week"))));
                            }
                        }
                        if (StringUtil.isNotEmpty(dto.getOpenCycleUnit())
                            && (StringUtil.equals(dto.getOpenCycleUnit(),
                                DictCodeListExt1.周期单位_月.getValue()) || StringUtil.equals(
                                    dto.getOpenCycleUnit(),
                                    DictCodeListExt1.周期单位_年.getValue()))) {
                            if (StringUtil.isEmpty(dto.getDayOfMonth())
                                && StringUtil.isEmpty(dto.getWorkdayOfMonth()) 
                                && StringUtil.isEmpty(dto.getWeekOfMonth()) && StringUtil.isEmpty(dto.getWorkdayOfWeek())) {
                                resultList.add(new ValidMessage("dayOfMonth",
                                    getMessage("BUSS_COM_E_0038", "第几周周几、几号或第几工作日")));
                            }
                        }
                    }
                }
                else if (StringUtil.equals(dto.getOpenMode(),
                    DictCodeList.开放模式_高频开放.getValue())) {
                    // 开放模式为高频开放时，节假日开放必填
                    if (StringUtil.isEmpty(dto.getHdayOpenFlag())) {
                        resultList.add(new ValidMessage("hdayOpenFlag", getMessage("BUSS_COM_E_0038",
                            getMessage("FinProductOpenRule.hdayOpenFlag"))));
                    }
                    if (!StringUtil.isNotEmpty(dto.getCalcMethod())) {
                        resultList.add(new ValidMessage("calcMethod", getMessage("BUSS_COM_E_0038",
                            getMessage("FinProductOpenRule.calcMethod"))));
                    }
                }
            }
            else {
                resultList.add(new ValidMessage("openMode",
                    getMessage("BUSS_COM_E_0038", getMessage("FinProductOpenRule.openMode"))));
            }
            // 推算方法
            if (StringUtil.isNotEmpty(dto.getCalcMethod())) {
            	// 一体化对应etliu
//                if (StringUtil.equals(dto.getCalcMethod(), DictCodeList.推算方法_首次开放日.getValue())
//                    && !StringUtil.isNotEmpty(dto.getFirstOpenDate())) {
            	if (StringUtil.equals(dto.getCalcMethod(), DictCodeList.推算方式_首次支付日.getValue())
                        && !StringUtil.isNotEmpty(dto.getFirstOpenDate())) {
                    resultList.add(new ValidMessage("firstOpenDate", getMessage("BUSS_COM_E_0038",
                        getMessage("FinProductOpenRule.firstOpenDate"))));
                }
            }
            // 首次开放日
            if (StringUtil.isNotEmpty(dto.getFirstOpenDate())) {
                if (StringUtil.equals(DictCodeList.推算方法_开放确认日.getValue(), dto.getCalcRule()) && StringUtil.isNotEmpty(dto.getVdate())
                    && DateUtil.compareDate(dto.getFirstOpenDate(), dto.getVdate()) <= 0) {
                    resultList.add(new ValidMessage("firstOpenDate",
                        getMessage("FIN_PROD_E_0015", getMessage("FinProductOpenRule.firstOpenDate"),
                            getMessage("FinProductBuild002.vdate"))));
                }
                if (StringUtil.isNotEmpty(dto.getMdate())
                    && DateUtil.compareDate(dto.getFirstOpenDate(), dto.getMdate()) > 0) {
                    resultList.add(new ValidMessage("firstOpenDate",
                        getMessage("FIN_PROD_E_0017", getMessage("FinProductOpenRule.firstOpenDate"),
                            getMessage("FinProductBuild002.mdate"))));
                }
            }
          //判断开放天数不为0 yzg 20191030 start 
            if(StringUtil.isNotEmpty(dto.getOpenDays()) && dto.getOpenDays() == 0L) {
            	resultList.add(new ValidMessage("openDays",
    	                getMessage("FinProductOpenRule.openDays.zero")));
            }
          //判断开放天数不为0 yzg 20191030 end 
        }

        return resultList;
    }

}
