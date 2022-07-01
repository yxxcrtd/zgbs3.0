/*
 * 文件名：BussFinRepaymentValidator.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangnian
 * 修改时间：2018年10月15日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.component.business.asset.validator;

import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.BeanValidator;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.common.base.entity.FinCashPlan;
import com.joyintech.tams.component.business.asset.dto.BussFinRepaymentDto;
import com.joyintech.tams.constant.DictCodeListExt1;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

/**
 * 还本信息--业务验证
 * 〈功能详细描述〉
 *
 * @author zhangnian
 * @version 1.0 2018年10月15日
 * @see BussFinRepaymentValidator
 * @since 1.0
 */
@Component
public class BussFinRepaymentValidator implements BeanValidator {

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ValidMessage> valid(Object value) {
        BussFinRepaymentDto dto = (BussFinRepaymentDto) value;
        List<ValidMessage> resultList = new ArrayList<ValidMessage>();
        Set<Date> set = new HashSet<Date>();
        BigDecimal prinAmtSum = new BigDecimal("0");
        if (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_项目类资产.getValue())
                || StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue())) {
            if (StringUtil.isEmpty(dto.getPayCycle())) {
                resultList.add(new ValidMessage("payCycle",
                        getMessage("COMMON_E_0057", getMessage("BussFinRepayment.payCycle"))));
            }
        }
        //true  确认(重新计算利息) check
        if (Boolean.parseBoolean(dto.getIsAgainCalInt())) {
            if (null != dto.getInvRepayList() && !dto.getInvRepayList().isEmpty()) {
                for (FinCashPlan finCashPlanEntity : dto.getInvRepayList()) {
                    if (null == finCashPlanEntity.getMdate()) {
                        resultList.add(new ValidMessage(
                                getMessage("COMMON_E_0057", getMessage("BussFinRepayment.mdate"))));
                    } else {
                        if (null != dto.getVdate()) {
                            if (DateUtil.compareTo(finCashPlanEntity.getMdate(), dto.getVdate()) == -1
                                    || DateUtil.compareTo(finCashPlanEntity.getMdate(), dto.getMdate()) == 1) {
                                resultList.add(new ValidMessage(
                                        getMessage("FIN_ASSET_E_0016")));
                            }
                        }
                    }
                    if (null == finCashPlanEntity.getPrinAmt()) {
                        resultList.add(new ValidMessage(
                                getMessage("COMMON_E_0057", getMessage("BussFinRepayment.prinAmt"))));
                    } else {
                        if (finCashPlanEntity.getPrinAmt().compareTo(BigDecimal.ZERO) != 1) {
                            resultList.add(new ValidMessage(
                                    getMessage("还本面值或者还本金额必须大于0!")));
                            break;
                        }
                    }
                    //计息截止日不可重复
                    if (!set.add(finCashPlanEntity.getMdate())) {
                        resultList.add(new ValidMessage(
                                getMessage("FIN_ASSET_E_0009", getMessage("BussFinRepayment.mdate"))));
                    }
                    if (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券.getValue())) {
                        if (null != finCashPlanEntity.getPrinAmt()) {
                            prinAmtSum = prinAmtSum.add(new BigDecimal(finCashPlanEntity.getPrinAmt().toString()));
                        }
                        //还本面额/百元>100或还本面额/百元<=0
                        /*if(null!=finCashPlanEntity.getPrinAmt()){
                            if(finCashPlanEntity.getPrinAmt().compareTo(new BigDecimal("100"))==1
                                ||finCashPlanEntity.getPrinAmt().compareTo(new BigDecimal("0"))==-1){
                                resultList.add(new ValidMessage("",
                                    getMessage("FIN_ASSET_E_0015", getMessage("BussFinRepayment.prinAmt"),"0","100")));
                            }
                        }*/
                        //FIN_ASSET_E_0015
                    } else if (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_项目类资产.getValue())
                            || StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue())) {
                        if (null != finCashPlanEntity.getPrinAmt()) {
                            prinAmtSum = prinAmtSum.add(new BigDecimal(finCashPlanEntity.getPrinAmt().toString()));
                        }
                    }
                }
                //债券类资产(还本计划列表中的还本面额/百元之和必须==100)
                if (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券.getValue())) {
                    if (prinAmtSum.compareTo(new BigDecimal("100")) != 0) {
                        resultList.add(new ValidMessage(
                                getMessage("BUSS_COM_E_0048", getMessage("BussFinRepayment.cashAmt.sumCheck"), "100")));
                    }
                }
                //项目类资产--还款周期不可为空
                if (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_项目类资产.getValue())
                        || StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue())) {
                    if (prinAmtSum.compareTo(dto.getIssueAmt()) != 0) {
                        resultList.add(new ValidMessage(
                                getMessage("BUSS_COM_E_0048", getMessage("BussFinRepayment.cashAmt.sumCheck2"), getMessage("FinPayInterestStdInfo.asset.issueAmt2"))));
                    }
                    if (StringUtil.isEmpty(dto.getPayCycle())) {
                        resultList.add(new ValidMessage("payCycle",
                                getMessage("COMMON_E_0057", getMessage("BussFinRepayment.payCycle"))));
                    }
                }
            }
        }
        //保存判断
        /* if(null==finCashPlanEntity.getPayDate()){
             resultList.add(new ValidMessage("",
                 getMessage("COMMON_E_0057", getMessage("BussFinRepayment.payDate"))));
         }*/
        //保存时需要做check
        /*if(null==finCashPlanEntity.getIntAmt()){
            resultList.add(new ValidMessage("",
                getMessage("COMMON_E_0057", getMessage("BussFinRepayment.intAmt"))));
        }
        if(null==finCashPlanEntity.getCashAmt()){
            resultList.add(new ValidMessage("",
                getMessage("COMMON_E_0057", getMessage("BussFinRepayment.cashAmt"))));
        }*/
        return resultList;
    }

}
