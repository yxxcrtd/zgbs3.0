/*
 * 文件名：TrdPlanRateInvestTradeDtoValidator.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：fanshuanghu
 * 修改时间：2018年11月22日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.valuation.trademanage.trdplanvaluationtrade.validate;

import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.BeanValidator;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.valuation.trademanage.trdplanvaluationtrade.dto.TrdPlanValuationTradeDto;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description:计划类投资交易
 * TrdPlanValuationTradeDtoValidator
 * @auther: yuzj
 * @date: 2021/12/30
 * @param:
 * @return:
 */
@Transactional
@Component
public class TrdPlanValuationTradeDtoValidator implements BeanValidator {

    public List<ValidMessage> valid(Object value) {
        List<ValidMessage> resultList = new ArrayList<ValidMessage>();
        TrdPlanValuationTradeDto dto = (TrdPlanValuationTradeDto)value;

        if("delete".equals(dto.getOperateType())){

        }else{
            if(StringUtil.isNotEmpty(dto.getPs())){
                if( StringUtil.equals(dto.getPs(), DictCodeList.交易方向_买入.getValue())
                        || StringUtil.equals(dto.getPs(),DictCodeList.交易方向_卖出.getValue())
                ){
                    //交易日期
                    if(!StringUtil.isNotEmpty(dto.getTradeDate())){
                        resultList.add(new ValidMessage("tradeDate",
                                getMessage("VAL_COMMON_E_0002", getMessage("TrdPlanValuationTrade002.tradeDate"))));
                    }
                    //结算日期
                    if(!StringUtil.isNotEmpty(dto.getSettleDate())){
                        resultList.add(new ValidMessage("settleDate",
                                getMessage("VAL_COMMON_E_0002", getMessage("TrdPlanValuationTrade002.settleDate"))));
                    }
                    //成交数量
                    if(!StringUtil.isNotEmpty(dto.getDealNum())){
                        resultList.add(new ValidMessage("dealNum",
                                getMessage("VAL_COMMON_E_0002", getMessage("TrdPlanValuationTrade002.dealNum"))));
                    }
                    //成交金额
                    if(!StringUtil.isNotEmpty(dto.getTradeAmt())){
                        resultList.add(new ValidMessage("tradeAmt",
                                getMessage("VAL_COMMON_E_0002", getMessage("TrdPlanValuationTrade002.tradeAmt"))));
                    }
                    //资金账户
                    if(!StringUtil.isNotEmpty(dto.getTrdMyAcctNo())){
                        resultList.add(new ValidMessage("trdMyAcctNo",
                                getMessage("VAL_COMMON_E_0002", getMessage("TrdPlanValuationTrade002.myAcctNo"))));
                    }
                }else if(StringUtil.equals(dto.getPs(),DictCodeList.交易方向_认购.getValue())  ){
                    //申请日期
                    if(!StringUtil.isNotEmpty(dto.getApplyDate())){
                        resultList.add(new ValidMessage("applyDate",
                                getMessage("VAL_COMMON_E_0002", getMessage("TrdPlanValuationTrade002.applyDate"))));
                    }
                    //结算日期
                    if(!StringUtil.isNotEmpty(dto.getSubSettleDate())){
                        resultList.add(new ValidMessage("subSettleDate",
                                getMessage("VAL_COMMON_E_0002", getMessage("TrdPlanValuationTrade002.settleDate"))));
                    }
                    //申请金额
                    if(!StringUtil.isNotEmpty(dto.getApplyAmt())){
                        resultList.add(new ValidMessage("applyAmt",
                                getMessage("VAL_COMMON_E_0002", getMessage("TrdPlanValuationTrade002.applyAmt"))));
                    }
                    //资金账户
                    if(!StringUtil.isNotEmpty(dto.getSubMyAcctNo())){
                        resultList.add(new ValidMessage("subMyAcctNo",
                                getMessage("VAL_COMMON_E_0002", getMessage("TrdPlanValuationTrade002.myAcctNo"))));
                    }
                }else if(StringUtil.equals(dto.getPs(),DictCodeList.交易方向_申购.getValue())){
                    //申请日期
                    if(!StringUtil.isNotEmpty(dto.getPurApplyDate())){
                        resultList.add(new ValidMessage("purApplyDate",
                                getMessage("VAL_COMMON_E_0002", getMessage("TrdPlanValuationTrade002.applyDate"))));
                    }
                    //结算日期
                    if(!StringUtil.isNotEmpty(dto.getPurSettleDate())){
                        resultList.add(new ValidMessage("purSettleDate",
                                getMessage("VAL_COMMON_E_0002", getMessage("TrdPlanValuationTrade002.settleDate"))));
                    }
                    //申请金额
                    if(!StringUtil.isNotEmpty(dto.getPurApplyAmt())){
                        resultList.add(new ValidMessage("purApplyAmt",
                                getMessage("VAL_COMMON_E_0002", getMessage("TrdPlanValuationTrade002.applyAmt"))));
                    }
                    //资金账户
                    if(!StringUtil.isNotEmpty(dto.getPurMyAcctNo())){
                        resultList.add(new ValidMessage("purMyAcctNo",
                                getMessage("VAL_COMMON_E_0002", getMessage("TrdPlanValuationTrade002.myAcctNo"))));
                    }
                }else if(StringUtil.equals(dto.getPs(),DictCodeList.交易方向_认购确认.getValue()) ){
                    //确认日期
                    if(!StringUtil.isNotEmpty(dto.getSubConfirmDate())){
                        resultList.add(new ValidMessage("subConfirmDate",
                                getMessage("VAL_COMMON_E_0002", getMessage("TrdPlanValuationTrade002.subConfirmDate"))));
                    }
                    //确认数量
                    if(!StringUtil.isNotEmpty(dto.getSubConfirmNum())){
                        resultList.add(new ValidMessage("subConfirmNum",
                                getMessage("VAL_COMMON_E_0002", getMessage("TrdPlanValuationTrade002.subConfirmNum"))));
                    }
                    //确认金额
                    if(!StringUtil.isNotEmpty(dto.getSubConfirmAmt())){
                        resultList.add(new ValidMessage("subConfirmAmt",
                                getMessage("VAL_COMMON_E_0002", getMessage("TrdPlanValuationTrade002.subConfirmAmt"))));
                    }
                    //募集期利息
                    if(!StringUtil.isNotEmpty(dto.getRaiseInterest())){
                        resultList.add(new ValidMessage("raiseInterest",
                                getMessage("VAL_COMMON_E_0002", getMessage("TrdPlanValuationTrade002.raiseInterest"))));
                    }
                    //认购费
                    if(!StringUtil.isNotEmpty(dto.getSubFee())){
                        resultList.add(new ValidMessage("subFee",
                                getMessage("VAL_COMMON_E_0002", getMessage("TrdPlanValuationTrade002.subFee"))));
                    }
                    //资金账户
                    if(!StringUtil.isNotEmpty(dto.getSubConfirmMyAcctNo())){
                        resultList.add(new ValidMessage("subConfirmMyAcctNo",
                                getMessage("VAL_COMMON_E_0002", getMessage("TrdPlanValuationTrade002.myAcctNo"))));
                    }
                } else if(StringUtil.equals(dto.getPs(),DictCodeList.交易方向_申购确认.getValue()) ){
                    //确认日期
                    if(!StringUtil.isNotEmpty(dto.getPurConfirmDate())){
                        resultList.add(new ValidMessage("purConfirmDate",
                                getMessage("VAL_COMMON_E_0002", getMessage("TrdPlanValuationTrade002.subConfirmDate"))));
                    }
                    //确认数量
                    if(!StringUtil.isNotEmpty(dto.getPurConfirmNum())){
                        resultList.add(new ValidMessage("purConfirmNum",
                                getMessage("VAL_COMMON_E_0002", getMessage("TrdPlanValuationTrade002.subConfirmNum"))));
                    }
                    //申购费
                    if(!StringUtil.isNotEmpty(dto.getPurFee())){
                        resultList.add(new ValidMessage("purFee",
                                getMessage("VAL_COMMON_E_0002", getMessage("TrdPlanValuationTrade002.purFee"))));
                    }
                    //资金账户
                    if(!StringUtil.isNotEmpty(dto.getPurConfirmMyAcctNo())){
                        resultList.add(new ValidMessage("purConfirmMyAcctNo",
                                getMessage("VAL_COMMON_E_0002", getMessage("TrdPlanValuationTrade002.myAcctNo"))));
                    }
                }else if(StringUtil.equals(dto.getPs(),DictCodeList.交易方向_赎回确认.getValue()) ){
                    //确认日期
                    if(!StringUtil.isNotEmpty(dto.getRedConfirmDate())){
                        resultList.add(new ValidMessage("redConfirmDate",
                                getMessage("VAL_COMMON_E_0002", getMessage("TrdPlanValuationTrade002.subConfirmDate"))));
                    }
                    //确认数量
                    if(!StringUtil.isNotEmpty(dto.getRedConfirmNum())){
                        resultList.add(new ValidMessage("redConfirmNum",
                                getMessage("VAL_COMMON_E_0002", getMessage("TrdPlanValuationTrade002.subConfirmNum"))));
                    }
                    //确认金额
                    if(!StringUtil.isNotEmpty(dto.getRedConfirmAmt())){
                        resultList.add(new ValidMessage("redConfirmAmt",
                                getMessage("VAL_COMMON_E_0002", getMessage("TrdPlanValuationTrade002.subConfirmAmt"))));
                    }
                    //赎回费
                    if(!StringUtil.isNotEmpty(dto.getRedFee())){
                        resultList.add(new ValidMessage("redFee",
                                getMessage("VAL_COMMON_E_0002", getMessage("TrdPlanValuationTrade002.redFee"))));
                    }
                    //资金账户
                    if(!StringUtil.isNotEmpty(dto.getRedMyAcctNo())){
                        resultList.add(new ValidMessage("redMyAcctNo",
                                getMessage("VAL_COMMON_E_0002", getMessage("TrdPlanValuationTrade002.myAcctNo"))));
                    }
                }else if(StringUtil.equals(dto.getPs(),DictCodeList.交易方向_赎回到账.getValue())){
                    //确认日期
                    if(!StringUtil.isNotEmpty(dto.getRedConfirmDate())){
                        resultList.add(new ValidMessage("redConfirmDate",
                                getMessage("VAL_COMMON_E_0002", getMessage("TrdPlanValuationTrade002.subConfirmDate"))));
                    }
                    //确认数量
                    if(!StringUtil.isNotEmpty(dto.getRedConfirmNum())){
                        resultList.add(new ValidMessage("redConfirmNum",
                                getMessage("VAL_COMMON_E_0002", getMessage("TrdPlanValuationTrade002.subConfirmNum"))));
                    }
                    //确认金额
                    if(!StringUtil.isNotEmpty(dto.getRedConfirmAmt())){
                        resultList.add(new ValidMessage("redConfirmAmt",
                                getMessage("VAL_COMMON_E_0002", getMessage("TrdPlanValuationTrade002.subConfirmAmt"))));
                    }
                    //赎回费
                    if(!StringUtil.isNotEmpty(dto.getRedFee())){
                        resultList.add(new ValidMessage("redFee",
                                getMessage("VAL_COMMON_E_0002", getMessage("TrdPlanValuationTrade002.redFee"))));
                    }
                    //到账日期
                    if(!StringUtil.isNotEmpty(dto.getArriveDate())){
                        resultList.add(new ValidMessage("arriveDate",
                                getMessage("VAL_COMMON_E_0002", getMessage("TrdPlanValuationTrade002.arriveDate"))));
                    }
                    //到账日期
                    if(!StringUtil.isNotEmpty(dto.getTradeAmtArrive())){
                        resultList.add(new ValidMessage("tradeAmtArrive",
                                getMessage("VAL_COMMON_E_0002", getMessage("TrdPlanValuationTrade002.tradeAmtArrive"))));
                    }
                    //资金账户
                    if(!StringUtil.isNotEmpty(dto.getRedMyAcctNo())){
                        resultList.add(new ValidMessage("redMyAcctNo",
                                getMessage("VAL_COMMON_E_0002", getMessage("TrdPlanValuationTrade002.myAcctNo"))));
                    }
                }
            }
        }

        return resultList;
    }

}

