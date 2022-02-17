/*
 * 文件名：TrdPlanRateInvestTradeDtoValidator.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：fanshuanghu
 * 修改时间：2018年11月22日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.valuation.trademanage.trdfundvaluationtrade.validate;

import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.BeanValidator;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.valuation.trademanage.trdfundvaluationtrade.dto.TrdFundValuationTradeDto;
import com.joyintech.tams.valuation.trademanage.trdplanvaluationtrade.dto.TrdPlanValuationTradeDto;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description:计划类投资交易
 * TrdFundValuationTradeDtoValidator
 * @auther: yuzj
 * @date: 2021/12/30
 * @param:
 * @return:
 */
@Transactional
@Component
public class TrdFundValuationTradeDtoValidator implements BeanValidator {

    public List<ValidMessage> valid(Object value) {
        List<ValidMessage> validMessageList = new ArrayList<ValidMessage>();
        TrdFundValuationTradeDto dto = (TrdFundValuationTradeDto)value;

        if(dto.getOperateType().equals("submit") || dto.getOperateType().equals("save")) {
            if(StringUtil.isNotEmpty(dto.getPs())){
                if( StringUtil.equals(dto.getPs(), DictCodeList.交易方向_买入.getValue())
                        || StringUtil.equals(dto.getPs(),DictCodeList.交易方向_卖出.getValue())
                        || StringUtil.equals(dto.getPs(),DictCodeList.交易方向_认购.getValue())
                        || StringUtil.equals(dto.getPs(),DictCodeList.交易方向_申购.getValue())
                ){
                    if(dto.getTradeAmt() ==null){
                        String msg = this.getMessage("请输入交易金额。");
                        validMessageList.add(new ValidMessage(msg));
                    }
                    if(dto.getNums() ==null){
                        String msg = this.getMessage("请输入交易份额。");
                        validMessageList.add(new ValidMessage(msg));
                    }
                    if(dto.getCarryAmt() ==null){
                        String msg = this.getMessage("请输入待结转份额。");
                        validMessageList.add(new ValidMessage(msg));
                    }

                }else if(StringUtil.equals(dto.getPs(),DictCodeList.交易方向_认购确认.getValue())
                        || StringUtil.equals(dto.getPs(),DictCodeList.交易方向_申购确认.getValue())
                        || StringUtil.equals(dto.getPs(),DictCodeList.交易方向_赎回确认.getValue())
                ){
                    if(dto.getConfirmTradeAmt() ==null){
                        String msg = this.getMessage("请输入确认金额。");
                        validMessageList.add(new ValidMessage(msg));
                    }
                    if(dto.getReceivableBonus() ==null){
                        String msg = this.getMessage("请输入应收红利。");
                        validMessageList.add(new ValidMessage(msg));
                    }
                    if(dto.getUnitValue() ==null){
                        String msg = this.getMessage("请输入单位净值。");
                        validMessageList.add(new ValidMessage(msg));
                    }
                }else if(StringUtil.equals(dto.getPs(),DictCodeList.交易方向_赎回到账.getValue())
                ){
                    if(dto.getSettleAmt() ==null){
                        String msg = this.getMessage("请输入到账金额。");
                        validMessageList.add(new ValidMessage(msg));
                    }
                }
            }
        }

        return validMessageList;
    }

}

