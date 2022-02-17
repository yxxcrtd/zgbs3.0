/*
 * 文件名：TrdPlanRateInvestTradeDtoValidator.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：fanshuanghu
 * 修改时间：2018年11月22日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.valuation.trademanage.trdfundbonusvaluationtrade.validate;

import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.BeanValidator;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.valuation.trademanage.trdfundbonusvaluationtrade.dto.TrdFundBonusValuationTradeDto;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description:基金存续期
 * TrdFundBonusValuationTradeDtoValidator
 * @auther: yuzj
 * @date: 2022/02/11
 * @param:
 * @return:
 */
@Transactional
@Component
public class TrdFundBonusValuationTradeDtoValidator implements BeanValidator {

    public List<ValidMessage> valid(Object value) {
        List<ValidMessage> validMessageList = new ArrayList<ValidMessage>();
        TrdFundBonusValuationTradeDto dto = (TrdFundBonusValuationTradeDto)value;

        if(dto.getOperateType().equals("submit") || dto.getOperateType().equals("save")){
            //分红金额 - 当交易类型为现金分红、红利再投时必填
            if(StringUtil.isNotEmpty(dto.getPs()) && (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_现金分红.getValue())
                    || StringUtil.equals(dto.getPs(), DictCodeList.交易方向_红利再投.getValue()))  ){
                if(dto.getBonusAmt() ==null){
                    String msg = this.getMessage("当交易类型为现金分红、红利再投时,分红金额必填");
                    validMessageList.add(new ValidMessage(msg));
                }
            }

            //交易份额 - 当交易类型为红利再投、份额强增、份额强减时必填
            if(StringUtil.isNotEmpty(dto.getPs()) && (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_红利再投.getValue())
                    || StringUtil.equals(dto.getPs(), DictCodeList.交易方向_份额强增.getValue())
                    || StringUtil.equals(dto.getPs(), DictCodeList.交易方向_份额强减.getValue()) )){
                if(dto.getNums() ==null){
                    String msg = this.getMessage("当交易类型为红利再投、份额强增、份额强减时，交易份额必填");
                    validMessageList.add(new ValidMessage(msg));
                }
            }
        }
        return validMessageList;
    }

}

