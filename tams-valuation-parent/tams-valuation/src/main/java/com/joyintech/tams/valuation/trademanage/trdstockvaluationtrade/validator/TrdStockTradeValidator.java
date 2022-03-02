/*
 * 文件名：FinDepositTargetValidator.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangnian
 * 修改时间：2018年10月30日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.valuation.trademanage.trdstockvaluationtrade.validator;


import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.BeanValidator;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.valuation.trademanage.trdstockvaluationtrade.dto.TrdStockTradeDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 *  债券交易--业务验证
 * 〈功能详细描述〉
 * @author sunshuaishaui
 * @version 1.0 2022年02月17日
 * @see TrdStockTradeValidator
 * @since 1.0
 */
@Component
public class TrdStockTradeValidator implements BeanValidator {

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ValidMessage> valid(Object value) {
        TrdStockTradeDto dto = (TrdStockTradeDto)value;
        List<ValidMessage> resultList = new ArrayList<ValidMessage>();
        if("delete".equals(dto.getOperateType())){

        }else{
            if(DictCodeList.是否_是.getValue().equals(dto.getIsLock())){
                if(!StringUtil.isNotEmpty(dto.getEndDate())){
                    resultList.add(new ValidMessage("endDate",
                            getMessage("VAL_COMMON_E_0002", getMessage("锁定期截止日"))));
                }
            }

            if(DictCodeList.交易方向_买入.getValue().equals(dto.getPs()) || DictCodeList.交易方向_卖出.getValue().equals(dto.getPs()) ){
                //成交日期
                if(!StringUtil.isNotEmpty(dto.getDealDate())){
                    resultList.add(new ValidMessage("dealDate",
                            getMessage("VAL_COMMON_E_0002", getMessage("TrdStockTrade.dealDate"))));
                }
                //结算日期
                if(!StringUtil.isNotEmpty(dto.getDealDate())){
                    resultList.add(new ValidMessage("dealDate",
                            getMessage("VAL_COMMON_E_0002", getMessage("TrdStockTrade.dealDate"))));
                }
                //资金交收日期
                if(!StringUtil.isNotEmpty(dto.getFundSettleDate())){
                    resultList.add(new ValidMessage("fundSettleDate",
                            getMessage("VAL_COMMON_E_0002", getMessage("TrdStockTrade.fundSettleDate"))));
                }
                //成交数量
                if(!StringUtil.isNotEmpty(dto.getDealNum())){
                    resultList.add(new ValidMessage("dealNum",
                            getMessage("VAL_COMMON_E_0002", getMessage("TrdStockTrade.dealNum"))));
                }
                //成交金额
                if(!StringUtil.isNotEmpty(dto.getTradeAmt())){
                    resultList.add(new ValidMessage("tradeAmt",
                            getMessage("VAL_COMMON_E_0002", getMessage("TrdStockTrade.tradeAmt"))));
                }
                //净佣金
                if(!StringUtil.isNotEmpty(dto.getCommission())){
                    resultList.add(new ValidMessage("commission",
                            getMessage("VAL_COMMON_E_0002", getMessage("TrdStockTrade.commission"))));
                }
                //印花税
                if(!StringUtil.isNotEmpty(dto.getStampDutyAmt())){
                    resultList.add(new ValidMessage("stampDutyAmt",
                            getMessage("VAL_COMMON_E_0002", getMessage("TrdStockTrade.stampDutyAmt"))));
                }
                //经手费
                if(!StringUtil.isNotEmpty(dto.getBrokerage())){
                    resultList.add(new ValidMessage("brokerage",
                            getMessage("VAL_COMMON_E_0002", getMessage("TrdStockTrade.brokerage"))));
                }
                //证管费
                if(!StringUtil.isNotEmpty(dto.getCertfFee())){
                    resultList.add(new ValidMessage("certfFee",
                            getMessage("VAL_COMMON_E_0002", getMessage("TrdStockTrade.certfFee"))));
                }
                //过户费
                if(!StringUtil.isNotEmpty(dto.getGhfAmt())){
                    resultList.add(new ValidMessage("ghfAmt",
                            getMessage("VAL_COMMON_E_0002", getMessage("TrdStockTrade.ghfAmt"))));
                }
                //结算费
                if(!StringUtil.isNotEmpty(dto.getStlAmt())){
                    resultList.add(new ValidMessage("stlAmt",
                            getMessage("VAL_COMMON_E_0002", getMessage("TrdStockTrade.stlAmt"))));
                }
                //其他费用
                if(!StringUtil.isNotEmpty(dto.getOtherAmt())){
                    resultList.add(new ValidMessage("otherAmt",
                            getMessage("VAL_COMMON_E_0002", getMessage("TrdStockTrade.otherAmt"))));
                }
//                //汇率日期
//                if(!StringUtil.isNotEmpty(dto.getExchangeDate())){
//                    resultList.add(new ValidMessage("exchangeDate",
//                            getMessage("VAL_COMMON_E_0002", getMessage("TrdStockTrade.exchangeDate"))));
//                }
                //结算金额
                if(!StringUtil.isNotEmpty(dto.getSettleAmt())){
                    resultList.add(new ValidMessage("settleAmt",
                            getMessage("VAL_COMMON_E_0002", getMessage("TrdStockTrade.settleAmt"))));
                }
                //资金账户
                if(!StringUtil.isNotEmpty(dto.getMyAcctNo())){
                    resultList.add(new ValidMessage("myAcctNo",
                            getMessage("VAL_COMMON_E_0002", getMessage("TrdStockTrade.myAcctNo"))));
                }
            }else if(DictCodeListExt2.交易方向_配股.getValue().equals(dto.getPs()) || DictCodeListExt2.交易方向_增发.getValue().equals(dto.getPs())){
                //配售代码
                if(!StringUtil.isNotEmpty(dto.getSaleCode())){
                    resultList.add(new ValidMessage("saleCode",
                            getMessage("VAL_COMMON_E_0002", getMessage("TrdStockTrade.saleCode"))));
                }
                //结算日期
                if(!StringUtil.isNotEmpty(dto.getSettleDate_pg())){
                    resultList.add(new ValidMessage("settleDate_pg",
                            getMessage("VAL_COMMON_E_0002", getMessage("TrdStockTrade.settleDate"))));
                }
                //资金交收日期
                if(!StringUtil.isNotEmpty(dto.getFundSettleDate_pg())){
                    resultList.add(new ValidMessage("fundSettleDate_pg",
                            getMessage("VAL_COMMON_E_0002", getMessage("TrdStockTrade.fundSettleDate"))));
                }
                //成交数量
                if(!StringUtil.isNotEmpty(dto.getDealNum_pg())){
                    resultList.add(new ValidMessage("dealNum_pg",
                            getMessage("VAL_COMMON_E_0002", getMessage("TrdStockTrade.dealNum"))));
                }
                //成交价格
                if(!StringUtil.isNotEmpty(dto.getTradedPrice())){
                    resultList.add(new ValidMessage("tradedPrice",
                            getMessage("VAL_COMMON_E_0002", getMessage("TrdStockTrade.tradedPrice"))));
                }
                //缴款金额
                if(!StringUtil.isNotEmpty(dto.getPaymentAmt_pg())){
                    resultList.add(new ValidMessage("paymentAmt_pg",
                            getMessage("VAL_COMMON_E_0002", getMessage("TrdStockTrade.paymentAmt"))));
                }
                //资金账户
                if(!StringUtil.isNotEmpty(dto.getMyAcctNo_pg())){
                    resultList.add(new ValidMessage("myAcctNo_pg",
                            getMessage("VAL_COMMON_E_0002", getMessage("TrdStockTrade.myAcctNo"))));
                }
            }else if(DictCodeListExt2.交易方向_新股中签.getValue().equals(dto.getPs())){
                //申购日期
                if(!StringUtil.isNotEmpty(dto.getPurchDate())){
                    resultList.add(new ValidMessage("purchDate",
                            getMessage("VAL_COMMON_E_0002", getMessage("TrdStockTrade.purchDate"))));
                }
                //结算日期
                if(!StringUtil.isNotEmpty(dto.getSettleDate_sg())){
                    resultList.add(new ValidMessage("settleDate_sg",
                            getMessage("VAL_COMMON_E_0002", getMessage("TrdStockTrade.settleDate"))));
                }
                //申购金额
                if(!StringUtil.isNotEmpty(dto.getEntrustAmt())){
                    resultList.add(new ValidMessage("entrustAmt",
                            getMessage("VAL_COMMON_E_0002", getMessage("TrdStockTrade.entrustAmt"))));
                }
                //资金账户
                if(!StringUtil.isNotEmpty(dto.getMyAcctNo_sg())){
                    resultList.add(new ValidMessage("myAcctNo_sg",
                            getMessage("VAL_COMMON_E_0002", getMessage("TrdStockTrade.myAcctNo"))));
                }
            }else if("105".equals(dto.getPs())){
                //确认日期
                if(!StringUtil.isNotEmpty(dto.getConfirmDate())){
                    resultList.add(new ValidMessage("confirmDate",
                            getMessage("VAL_COMMON_E_0002", getMessage("TrdStockTrade.confirmDate"))));
                }
                //中签数量
                if(!StringUtil.isNotEmpty(dto.getConfirmNum())){
                    resultList.add(new ValidMessage("confirmNum",
                            getMessage("VAL_COMMON_E_0002", getMessage("TrdStockTrade.confirmNum"))));
                }
                //中签价格
                if(!StringUtil.isNotEmpty(dto.getConfirmPrice())){
                    resultList.add(new ValidMessage("confirmPrice",
                            getMessage("VAL_COMMON_E_0002", getMessage("TrdStockTrade.confirmPrice"))));
                }
                //中签金额
                if(!StringUtil.isNotEmpty(dto.getConfirmAmt())){
                    resultList.add(new ValidMessage("confirmAmt",
                            getMessage("VAL_COMMON_E_0002", getMessage("TrdStockTrade.confirmAmt"))));
                }
                //资金账户
                if(!StringUtil.isNotEmpty(dto.getMyAcctNo_qr())){
                    resultList.add(new ValidMessage("myAcctNo_qr",
                            getMessage("VAL_COMMON_E_0002", getMessage("TrdStockTrade.myAcctNo"))));
                }
                //缴款日
                if(!StringUtil.isNotEmpty(dto.getPaymentDate())){
                    resultList.add(new ValidMessage("paymentDate",
                            getMessage("VAL_COMMON_E_0002", getMessage("TrdStockTrade.paymentDate"))));
                }
                //缴款金额
                if(!StringUtil.isNotEmpty(dto.getPaymentAmt())){
                    resultList.add(new ValidMessage("paymentAmt",
                            getMessage("VAL_COMMON_E_0002", getMessage("TrdStockTrade.paymentAmt"))));
                }
            }else{
                //成交日期
                if(!StringUtil.isNotEmpty(dto.getDealDate())){
                    resultList.add(new ValidMessage("dealDate",
                            getMessage("VAL_COMMON_E_0002", getMessage("TrdStockTrade.dealDate"))));
                }
                //结算日期
                if(!StringUtil.isNotEmpty(dto.getDealDate())){
                    resultList.add(new ValidMessage("dealDate",
                            getMessage("VAL_COMMON_E_0002", getMessage("TrdStockTrade.dealDate"))));
                }
                //资金交收日期
                if(!StringUtil.isNotEmpty(dto.getFundSettleDate())){
                    resultList.add(new ValidMessage("fundSettleDate",
                            getMessage("VAL_COMMON_E_0002", getMessage("TrdStockTrade.fundSettleDate"))));
                }
                //成交数量
                if(!StringUtil.isNotEmpty(dto.getDealNum())){
                    resultList.add(new ValidMessage("dealNum",
                            getMessage("VAL_COMMON_E_0002", getMessage("TrdStockTrade.dealNum"))));
                }
                //成交金额
                if(!StringUtil.isNotEmpty(dto.getTradeAmt())){
                    resultList.add(new ValidMessage("tradeAmt",
                            getMessage("VAL_COMMON_E_0002", getMessage("TrdStockTrade.tradeAmt"))));
                }
                //净佣金
                if(!StringUtil.isNotEmpty(dto.getCommission())){
                    resultList.add(new ValidMessage("commission",
                            getMessage("VAL_COMMON_E_0002", getMessage("TrdStockTrade.commission"))));
                }
                //印花税
                if(!StringUtil.isNotEmpty(dto.getStampDutyAmt())){
                    resultList.add(new ValidMessage("stampDutyAmt",
                            getMessage("VAL_COMMON_E_0002", getMessage("TrdStockTrade.stampDutyAmt"))));
                }
                //经手费
                if(!StringUtil.isNotEmpty(dto.getBrokerage())){
                    resultList.add(new ValidMessage("brokerage",
                            getMessage("VAL_COMMON_E_0002", getMessage("TrdStockTrade.brokerage"))));
                }
                //证管费
                if(!StringUtil.isNotEmpty(dto.getCertfFee())){
                    resultList.add(new ValidMessage("certfFee",
                            getMessage("VAL_COMMON_E_0002", getMessage("TrdStockTrade.certfFee"))));
                }
                //过户费
                if(!StringUtil.isNotEmpty(dto.getGhfAmt())){
                    resultList.add(new ValidMessage("ghfAmt",
                            getMessage("VAL_COMMON_E_0002", getMessage("TrdStockTrade.ghfAmt"))));
                }
                //结算费
                if(!StringUtil.isNotEmpty(dto.getStlAmt())){
                    resultList.add(new ValidMessage("stlAmt",
                            getMessage("VAL_COMMON_E_0002", getMessage("TrdStockTrade.stlAmt"))));
                }
                //其他费用
                if(!StringUtil.isNotEmpty(dto.getOtherAmt())){
                    resultList.add(new ValidMessage("otherAmt",
                            getMessage("VAL_COMMON_E_0002", getMessage("TrdStockTrade.otherAmt"))));
                }
//                //汇率日期
//                if(!StringUtil.isNotEmpty(dto.getExchangeDate())){
//                    resultList.add(new ValidMessage("exchangeDate",
//                            getMessage("VAL_COMMON_E_0002", getMessage("TrdStockTrade.exchangeDate"))));
//                }
                //结算金额
                if(!StringUtil.isNotEmpty(dto.getSettleAmt())){
                    resultList.add(new ValidMessage("settleAmt",
                            getMessage("VAL_COMMON_E_0002", getMessage("TrdStockTrade.settleAmt"))));
                }
                //资金账户
                if(!StringUtil.isNotEmpty(dto.getMyAcctNo())){
                    resultList.add(new ValidMessage("myAcctNo",
                            getMessage("VAL_COMMON_E_0002", getMessage("TrdStockTrade.myAcctNo"))));
                }
                //配售代码
                if(!StringUtil.isNotEmpty(dto.getSaleCode())){
                    resultList.add(new ValidMessage("saleCode",
                            getMessage("VAL_COMMON_E_0002", getMessage("TrdStockTrade.saleCode"))));
                }
                //结算日期
                if(!StringUtil.isNotEmpty(dto.getSettleDate_pg())){
                    resultList.add(new ValidMessage("settleDate_pg",
                            getMessage("VAL_COMMON_E_0002", getMessage("TrdStockTrade.settleDate"))));
                }
                //资金交收日期
                if(!StringUtil.isNotEmpty(dto.getFundSettleDate_pg())){
                    resultList.add(new ValidMessage("fundSettleDate_pg",
                            getMessage("VAL_COMMON_E_0002", getMessage("TrdStockTrade.fundSettleDate"))));
                }
                //成交数量
                if(!StringUtil.isNotEmpty(dto.getDealNum_pg())){
                    resultList.add(new ValidMessage("dealNum_pg",
                            getMessage("VAL_COMMON_E_0002", getMessage("TrdStockTrade.dealNum"))));
                }
                //成交价格
                if(!StringUtil.isNotEmpty(dto.getTradedPrice())){
                    resultList.add(new ValidMessage("tradedPrice",
                            getMessage("VAL_COMMON_E_0002", getMessage("TrdStockTrade.tradedPrice"))));
                }
                //缴款金额
                if(!StringUtil.isNotEmpty(dto.getPaymentAmt_pg())){
                    resultList.add(new ValidMessage("paymentAmt_pg",
                            getMessage("VAL_COMMON_E_0002", getMessage("TrdStockTrade.paymentAmt"))));
                }
                //资金账户
                if(!StringUtil.isNotEmpty(dto.getMyAcctNo_pg())){
                    resultList.add(new ValidMessage("myAcctNo_pg",
                            getMessage("VAL_COMMON_E_0002", getMessage("TrdStockTrade.myAcctNo"))));
                }
                //申购日期
                if(!StringUtil.isNotEmpty(dto.getPurchDate())){
                    resultList.add(new ValidMessage("purchDate",
                            getMessage("VAL_COMMON_E_0002", getMessage("TrdStockTrade.purchDate"))));
                }
                //结算日期
                if(!StringUtil.isNotEmpty(dto.getSettleDate_sg())){
                    resultList.add(new ValidMessage("settleDate_sg",
                            getMessage("VAL_COMMON_E_0002", getMessage("TrdStockTrade.settleDate"))));
                }
                //申购金额
                if(!StringUtil.isNotEmpty(dto.getEntrustAmt())){
                    resultList.add(new ValidMessage("entrustAmt",
                            getMessage("VAL_COMMON_E_0002", getMessage("TrdStockTrade.entrustAmt"))));
                }
                //资金账户
                if(!StringUtil.isNotEmpty(dto.getMyAcctNo_sg())){
                    resultList.add(new ValidMessage("myAcctNo_sg",
                            getMessage("VAL_COMMON_E_0002", getMessage("TrdStockTrade.myAcctNo"))));
                }
                //确认日期
                if(!StringUtil.isNotEmpty(dto.getConfirmDate())){
                    resultList.add(new ValidMessage("confirmDate",
                            getMessage("VAL_COMMON_E_0002", getMessage("TrdStockTrade.confirmDate"))));
                }
                //中签数量
                if(!StringUtil.isNotEmpty(dto.getConfirmNum())){
                    resultList.add(new ValidMessage("confirmNum",
                            getMessage("VAL_COMMON_E_0002", getMessage("TrdStockTrade.confirmNum"))));
                }
                //中签价格
                if(!StringUtil.isNotEmpty(dto.getConfirmPrice())){
                    resultList.add(new ValidMessage("confirmPrice",
                            getMessage("VAL_COMMON_E_0002", getMessage("TrdStockTrade.confirmPrice"))));
                }
                //中签金额
                if(!StringUtil.isNotEmpty(dto.getConfirmAmt())){
                    resultList.add(new ValidMessage("confirmAmt",
                            getMessage("VAL_COMMON_E_0002", getMessage("TrdStockTrade.confirmAmt"))));
                }
                //资金账户
                if(!StringUtil.isNotEmpty(dto.getMyAcctNo_qr())){
                    resultList.add(new ValidMessage("myAcctNo_qr",
                            getMessage("VAL_COMMON_E_0002", getMessage("TrdStockTrade.myAcctNo"))));
                }
                //缴款日
                if(!StringUtil.isNotEmpty(dto.getPaymentDate())){
                    resultList.add(new ValidMessage("paymentDate",
                            getMessage("VAL_COMMON_E_0002", getMessage("TrdStockTrade.paymentDate"))));
                }
                //缴款金额
                if(!StringUtil.isNotEmpty(dto.getPaymentAmt())){
                    resultList.add(new ValidMessage("paymentAmt",
                            getMessage("VAL_COMMON_E_0002", getMessage("TrdStockTrade.paymentAmt"))));
                }
            }

        }

         return resultList;
    }

}
