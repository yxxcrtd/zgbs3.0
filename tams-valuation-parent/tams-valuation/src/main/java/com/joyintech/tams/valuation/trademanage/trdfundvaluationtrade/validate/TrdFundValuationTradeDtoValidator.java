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
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description:基金交易
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
                ){
                    if(dto.getTradeDate() ==null){
                        String msg = this.getMessage("请输入交易日期。");
                        validMessageList.add(new ValidMessage(msg));
                    }
                    if(dto.getSettleDate() ==null){
                        String msg = this.getMessage("请输入结算日期。");
                        validMessageList.add(new ValidMessage(msg));
                    }
                    if(dto.getNums() ==null){
                        String msg = this.getMessage("请输入成交数量。");
                        validMessageList.add(new ValidMessage(msg));
                    }
                    if(dto.getTradeAmt() ==null){
                        String msg = this.getMessage("请输入成交金额。");
                        validMessageList.add(new ValidMessage(msg));
                    }
                    if(dto.getTrdAcctId() ==null){
                        String msg = this.getMessage("请输入资金账户。");
                        validMessageList.add(new ValidMessage(msg));
                    }
                    if(dto.getExchangeAmt() ==null){
                        String msg = this.getMessage("请输入经手费。");
                        validMessageList.add(new ValidMessage(msg));
                    }
                    if(dto.getCommissionFee() ==null){
                        String msg = this.getMessage("请输入净佣金。");
                        validMessageList.add(new ValidMessage(msg));
                    }
                    if(dto.getMdateSettleAmt() ==null){
                        String msg = this.getMessage("请输入结算金额。");
                        validMessageList.add(new ValidMessage(msg));
                    }
                    if(dto.getTrdAcctId() ==null){
                        String msg = this.getMessage("请输入资金账户。");
                        validMessageList.add(new ValidMessage(msg));
                    }

                }else if(StringUtil.equals(dto.getPs(),DictCodeList.交易方向_认购.getValue())  ){
                    if(dto.getRgTradeDate() ==null){
                        String msg = this.getMessage("请输入申请日期。");
                        validMessageList.add(new ValidMessage(msg));
                    }
                    if(dto.getRgSettleDate() ==null){
                        String msg = this.getMessage("请输入结算日期。");
                        validMessageList.add(new ValidMessage(msg));
                    }
                    if(dto.getRgTradeAmt() ==null){
                        String msg = this.getMessage("请输入申请金额。");
                        validMessageList.add(new ValidMessage(msg));
                    }
                    if(dto.getRgTrdAcctId() ==null){
                        String msg = this.getMessage("请输入资金账户。");
                        validMessageList.add(new ValidMessage(msg));
                    }

                }else if(StringUtil.equals(dto.getPs(),DictCodeList.交易方向_申购.getValue())){
                    if(dto.getSqTradeDate() ==null){
                        String msg = this.getMessage("请输入申请日期。");
                        validMessageList.add(new ValidMessage(msg));
                    }
                    if(dto.getSqSettleDate() ==null){
                        String msg = this.getMessage("请输入结算日期。");
                        validMessageList.add(new ValidMessage(msg));
                    }
                    if(dto.getSqTradeAmt() ==null){
                        String msg = this.getMessage("请输入申请金额。");
                        validMessageList.add(new ValidMessage(msg));
                    }
                    if(dto.getSqTrdAcctId() ==null){
                        String msg = this.getMessage("请输入资金账户。");
                        validMessageList.add(new ValidMessage(msg));
                    }
                }else if(StringUtil.equals(dto.getPs(),DictCodeList.交易方向_认购确认.getValue())){
                    if(dto.getRgqrTradeDate() ==null){
                        String msg = this.getMessage("请输入确认日期。");
                        validMessageList.add(new ValidMessage(msg));
                    }
                    if(dto.getRgqrNums() ==null){
                        String msg = this.getMessage("请输入确认数量。");
                        validMessageList.add(new ValidMessage(msg));
                    }
                    if(dto.getSubscribeFee() ==null){
                        String msg = this.getMessage("请输入认购费。");
                        validMessageList.add(new ValidMessage(msg));
                    }
                    if(dto.getRgqrTrdAcctId() ==null){
                        String msg = this.getMessage("请输入资金账户。");
                        validMessageList.add(new ValidMessage(msg));
                    }
                }else if(StringUtil.equals(dto.getPs(),DictCodeList.交易方向_申购确认.getValue()) ){
                    if(dto.getSgqrTradeDate() ==null){
                        String msg = this.getMessage("请输入确认日期。");
                        validMessageList.add(new ValidMessage(msg));
                    }
                    if(dto.getSgqrNums() ==null){
                        String msg = this.getMessage("请输入确认数量。");
                        validMessageList.add(new ValidMessage(msg));
                    }
                    if(dto.getPurchaseFee() ==null){
                        String msg = this.getMessage("请输入申购费。");
                        validMessageList.add(new ValidMessage(msg));
                    }
                    if(dto.getSgqrTrdAcctId() ==null){
                        String msg = this.getMessage("请输入资金账户。");
                        validMessageList.add(new ValidMessage(msg));
                    }
                }else if(StringUtil.equals(dto.getPs(),DictCodeList.交易方向_赎回确认.getValue()) ){
                    if(dto.getShqrTradeDate() ==null){
                        String msg = this.getMessage("请输入确认日期。");
                        validMessageList.add(new ValidMessage(msg));
                    }
                    if(dto.getShqrNums() ==null){
                        String msg = this.getMessage("请输入确认数量。");
                        validMessageList.add(new ValidMessage(msg));
                    }
                    if(dto.getShqrTradeAmt() ==null){
                        String msg = this.getMessage("请输入确认金额。");
                        validMessageList.add(new ValidMessage(msg));
                    }
                    if(dto.getRedemptionFee() ==null){
                        String msg = this.getMessage("请输入赎回费。");
                        validMessageList.add(new ValidMessage(msg));
                    }
                    if(dto.getShqrSettleDate() ==null){
                        String msg = this.getMessage("请输入到账日期。");
                        validMessageList.add(new ValidMessage(msg));
                    }
                    if(dto.getShqrTrdAcctId() ==null){
                        String msg = this.getMessage("请输入资金账户。");
                        validMessageList.add(new ValidMessage(msg));
                    }
                }
            }
        }

        return validMessageList;
    }

}

