/*
 * 文件名：FinDepositTargetValidator.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangnian
 * 修改时间：2018年10月30日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.valuation.trademanage.bond.validator;


import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.BeanValidator;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.valuation.trademanage.bond.dto.TrdBondTradeDto;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 *  债券交易--业务验证
 * 〈功能详细描述〉
 * @author sunshuaishaui
 * @version 1.0 2022年02月17日
 * @see TrdBondTradeValidator
 * @since 1.0
 */
@Component
public class TrdBondTradeValidator implements BeanValidator {

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ValidMessage> valid(Object value) {
        TrdBondTradeDto dto = (TrdBondTradeDto)value;
        List<ValidMessage> resultList = new ArrayList<ValidMessage>();
        if("delete".equals(dto.getOperateType())){

        }else{
            //交割日期必须大于等于交易日期
            if(null!=dto.getSettdateDate()&&null!=dto.getTradeDate()){
                if(DateUtil.compareDate(dto.getTradeDate(),dto.getSettdateDate())!=-1
                    && DateUtil.compareDate(dto.getTradeDate(),dto.getSettdateDate())!=0){
                    resultList.add(new ValidMessage("settdateDate",
                            getMessage("BUSS_COM_E_0004", getMessage("TrdBondTrade.settdateDate"),getMessage("TrdBondTrade.tradeDate"))));
                }
            }
            if(!StringUtil.isNotEmpty(dto.getPs())){
//                // 成交日期
//                if(!StringUtil.isNotEmpty(dto.getTradeDate())){
//                    resultList.add(new ValidMessage("tradeDate",
//                            getMessage("VAL_COMMON_E_0002", getMessage("TrdBondTrade.dealDate"))));
//                }
//                // 交割日期
//                if(!StringUtil.isNotEmpty(dto.getSettdateDate())){
//                    resultList.add(new ValidMessage("settdateDate",
//                            getMessage("VAL_COMMON_E_0002", getMessage("TrdBondTrade.settdateDate"))));
//                }
//                // 资金交收日期
//                if(!StringUtil.isNotEmpty(dto.getFundSettleDate())){
//                    resultList.add(new ValidMessage("fundSettleDate",
//                            getMessage("VAL_COMMON_E_0002", getMessage("TrdBondTrade.fundSettleDate"))));
//                }
//                // 成交数量
//                if(!StringUtil.isNotEmpty(dto.getDealNum())){
//                    resultList.add(new ValidMessage("dealNum",
//                            getMessage("VAL_COMMON_E_0002", getMessage("TrdBondTrade.dealNum"))));
//                }
//                // 交易金额
//                if(!StringUtil.isNotEmpty(dto.getTradeAmt())){
//                    resultList.add(new ValidMessage("tradeAmt",
//                            getMessage("VAL_COMMON_E_0002", getMessage("TrdBondTrade.tradeAmt"))));
//                }
//                // 应收利息总额
//                if(!StringUtil.isNotEmpty(dto.getIntAmt())){
//                    resultList.add(new ValidMessage("intAmt",
//                            getMessage("VAL_COMMON_E_0002", getMessage("TrdBondTrade.intAmt"))));
//                }
//                // 净价
//                if(!StringUtil.isNotEmpty(dto.getCprice())){
//                    resultList.add(new ValidMessage("cprice",
//                            getMessage("VAL_COMMON_E_0002", getMessage("TrdBondTrade.cprice"))));
//                }
//                // 汇率日期
//                if(!StringUtil.isNotEmpty(dto.getExchangeDate())){
//                    resultList.add(new ValidMessage("exchangeDate",
//                            getMessage("VAL_COMMON_E_0002", getMessage("TrdBondTrade.exchangeDate"))));
//                }
//                // 经手费
//                if(!StringUtil.isNotEmpty(dto.getBrokerage())){
//                    resultList.add(new ValidMessage("brokerage",
//                            getMessage("VAL_COMMON_E_0002", getMessage("TrdBondTrade.brokerage"))));
//                }
//                // 结算费
//                if(!StringUtil.isNotEmpty(dto.getStlAmt())){
//                    resultList.add(new ValidMessage("stlAmt",
//                            getMessage("VAL_COMMON_E_0002", getMessage("TrdBondTrade.stlAmt"))));
//                }
//                // 净佣金
//                if(!StringUtil.isNotEmpty(dto.getCommission())){
//                    resultList.add(new ValidMessage("commission",
//                            getMessage("VAL_COMMON_E_0002", getMessage("TrdBondTrade.commission"))));
//                }
//                // 交易手续费
//                if(!StringUtil.isNotEmpty(dto.getDealFeeAmt())){
//                    resultList.add(new ValidMessage("dealFeeAmt",
//                            getMessage("VAL_COMMON_E_0002", getMessage("TrdBondTrade.dealFeeAmt"))));
//                }
//                // 结算手续费
//                if(!StringUtil.isNotEmpty(dto.getStlFeeAmt())){
//                    resultList.add(new ValidMessage("stlFeeAmt",
//                            getMessage("VAL_COMMON_E_0002", getMessage("TrdBondTrade.stlFeeAmt"))));
//                }
//                // 其他费用
//                if(!StringUtil.isNotEmpty(dto.getOtherAmt())){
//                    resultList.add(new ValidMessage("otherAmt",
//                            getMessage("VAL_COMMON_E_0002", getMessage("TrdBondTrade.otherAmt"))));
//                }
//                // 结算金额
//                if(!StringUtil.isNotEmpty(dto.getSettleAmt())){
//                    resultList.add(new ValidMessage("settleAmt",
//                            getMessage("VAL_COMMON_E_0002", getMessage("TrdBondTrade.settleAmt"))));
//                }
//                // 资金账户
//                if(!StringUtil.isNotEmpty(dto.getMyAcctNo())){
//                    resultList.add(new ValidMessage("myAcctNo",
//                            getMessage("VAL_COMMON_E_0002", getMessage("TrdBondTrade.myAcctNo"))));
//                }
//                // 申购日期不能为空
//                if(!StringUtil.isNotEmpty(dto.getPurchDate())){
//                    resultList.add(new ValidMessage("purchDate",
//                            getMessage("VAL_COMMON_E_0002", getMessage("TrdBondTrade.purchDate"))));
//                }
//                // 结算日期不能为空
//                if(!StringUtil.isNotEmpty(dto.getSettleDate())){
//                    resultList.add(new ValidMessage("settleDate",
//                            getMessage("VAL_COMMON_E_0002", getMessage("TrdBondTrade.settleDate"))));
//                }
//                // 结算金额不能为空
//                if(!StringUtil.isNotEmpty(dto.getEntrustAmt())){
//                    resultList.add(new ValidMessage("entrustAmt",
//                            getMessage("VAL_COMMON_E_0002", getMessage("TrdBondTrade.entrustAmt"))));
//                }
//                // 资金账户不能为空
//                if(!StringUtil.isNotEmpty(dto.getMyAcctNo_sq())){
//                    resultList.add(new ValidMessage("myAcctNo_sq",
//                            getMessage("VAL_COMMON_E_0002", getMessage("TrdBondTrade.myAcctNo_sq"))));
//                }
//                // 确认日期不能为空
//                if(!StringUtil.isNotEmpty(dto.getConfirmDate())){
//                    resultList.add(new ValidMessage("confirmDate",
//                            getMessage("VAL_COMMON_E_0002", getMessage("TrdBondTrade.confirmDate"))));
//                }
//                // 中签数量不能为空
//                if(!StringUtil.isNotEmpty(dto.getConfirmNum())){
//                    resultList.add(new ValidMessage("confirmNum",
//                            getMessage("VAL_COMMON_E_0002", getMessage("TrdBondTrade.confirmNum"))));
//                }
//                // 中签金额不能为空
//                if(!StringUtil.isNotEmpty(dto.getConfirmAmt())){
//                    resultList.add(new ValidMessage("confirmAmt",
//                            getMessage("VAL_COMMON_E_0002", getMessage("TrdBondTrade.confirmAmt"))));
//                }
//                // 补款到账日期不能为空
//                if(!StringUtil.isNotEmpty(dto.getReceiptArriveDate())){
//                    resultList.add(new ValidMessage("receiptArriveDate",
//                            getMessage("VAL_COMMON_E_0002", getMessage("TrdBondTrade.receiptArriveDate"))));
//                }
//                // 补款金额不能为空
//                if(!StringUtil.isNotEmpty(dto.getReceiptAmt())){
//                    resultList.add(new ValidMessage("receiptAmt",
//                            getMessage("VAL_COMMON_E_0002", getMessage("TrdBondTrade.receiptAmt"))));
//                }
//                // 返款到账日期不能为空
//                if(!StringUtil.isNotEmpty(dto.getRefundArriveDate())){
//                    resultList.add(new ValidMessage("refundArriveDate",
//                            getMessage("VAL_COMMON_E_0002", getMessage("TrdBondTrade.refundArriveDate"))));
//                }
//                // 返款金额不能为空
//                if(!StringUtil.isNotEmpty(dto.getRefundAmt())){
//                    resultList.add(new ValidMessage("refundAmt",
//                            getMessage("VAL_COMMON_E_0002", getMessage("TrdBondTrade.refundAmt"))));
//                }
//                // 资金账户不能为空
//                if(!StringUtil.isNotEmpty(dto.getMyAcctNo_qr())){
//                    resultList.add(new ValidMessage("myAcctNo_qr",
//                            getMessage("VAL_COMMON_E_0002", getMessage("TrdBondTrade.myAcctNo_qr"))));
//                }
            }else{
                if(DictCodeList.交易方向_申购.getValue().equals(dto.getPs())){
                    // 申购日期不能为空
                    if(!StringUtil.isNotEmpty(dto.getPurchDate())){
                        resultList.add(new ValidMessage("purchDate",
                                getMessage("VAL_COMMON_E_0002", getMessage("TrdBondTrade.purchDate"))));
                    }
                    // 结算日期不能为空
                    if(!StringUtil.isNotEmpty(dto.getSettleDate())){
                        resultList.add(new ValidMessage("settleDate",
                                getMessage("VAL_COMMON_E_0002", getMessage("TrdBondTrade.settleDate"))));
                    }
                    // 结算金额不能为空
                    if(!StringUtil.isNotEmpty(dto.getEntrustAmt())){
                        resultList.add(new ValidMessage("entrustAmt",
                                getMessage("VAL_COMMON_E_0002", getMessage("TrdBondTrade.entrustAmt"))));
                    }
                    // 资金账户不能为空
                    if(!StringUtil.isNotEmpty(dto.getMyAcctNo_sq())){
                        resultList.add(new ValidMessage("myAcctNo_sq",
                                getMessage("VAL_COMMON_E_0002", getMessage("TrdBondTrade.myAcctNo_sq"))));
                    }
                }
                if(DictCodeList.交易方向_申购确认.getValue().equals(dto.getPs())){
                    // 确认日期不能为空
                    if(!StringUtil.isNotEmpty(dto.getConfirmDate())){
                        resultList.add(new ValidMessage("confirmDate",
                                getMessage("VAL_COMMON_E_0002", getMessage("TrdBondTrade.confirmDate"))));
                    }
                    // 中签数量不能为空
                    if(!StringUtil.isNotEmpty(dto.getConfirmNum())){
                        resultList.add(new ValidMessage("confirmNum",
                                getMessage("VAL_COMMON_E_0002", getMessage("TrdBondTrade.confirmNum"))));
                    }
                    // 中签金额不能为空
                    if(!StringUtil.isNotEmpty(dto.getConfirmAmt())){
                        resultList.add(new ValidMessage("confirmAmt",
                                getMessage("VAL_COMMON_E_0002", getMessage("TrdBondTrade.confirmAmt"))));
                    }
                    // 补款到账日期不能为空
                    if(!StringUtil.isNotEmpty(dto.getReceiptArriveDate())){
                        resultList.add(new ValidMessage("receiptArriveDate",
                                getMessage("VAL_COMMON_E_0002", getMessage("TrdBondTrade.receiptArriveDate"))));
                    }
                    // 补款金额不能为空
                    if(!StringUtil.isNotEmpty(dto.getReceiptAmt())){
                        resultList.add(new ValidMessage("receiptAmt",
                                getMessage("VAL_COMMON_E_0002", getMessage("TrdBondTrade.receiptAmt"))));
                    }
                    // 返款到账日期不能为空
                    if(!StringUtil.isNotEmpty(dto.getRefundArriveDate())){
                        resultList.add(new ValidMessage("refundArriveDate",
                                getMessage("VAL_COMMON_E_0002", getMessage("TrdBondTrade.refundArriveDate"))));
                    }
                    // 返款金额不能为空
                    if(!StringUtil.isNotEmpty(dto.getRefundAmt())){
                        resultList.add(new ValidMessage("refundAmt",
                                getMessage("VAL_COMMON_E_0002", getMessage("TrdBondTrade.refundAmt"))));
                    }
                    // 资金账户不能为空
                    if(!StringUtil.isNotEmpty(dto.getMyAcctNo_qr())){
                        resultList.add(new ValidMessage("myAcctNo_qr",
                                getMessage("VAL_COMMON_E_0002", getMessage("TrdBondTrade.myAcctNo_qr"))));
                    }
                }
                if(DictCodeList.交易方向_买入.getValue().equals(dto.getPs())
                        || DictCodeList.交易方向_卖出.getValue().equals(dto.getPs())
                        || DictCodeList.交易方向_行权_回售.getValue().equals(dto.getPs())
                        || DictCodeList.交易方向_行权_赎回.getValue().equals(dto.getPs())){
                    // 成交日期
                    if(!StringUtil.isNotEmpty(dto.getTradeDate())){
                        resultList.add(new ValidMessage("tradeDate",
                                getMessage("VAL_COMMON_E_0002", getMessage("TrdBondTrade.dealDate"))));
                    }
                    // 交割日期
                    if(!StringUtil.isNotEmpty(dto.getSettdateDate())){
                        resultList.add(new ValidMessage("settdateDate",
                                getMessage("VAL_COMMON_E_0002", getMessage("TrdBondTrade.settdateDate"))));
                    }
                    // 资金交收日期
                    if(!StringUtil.isNotEmpty(dto.getFundSettleDate())){
                        resultList.add(new ValidMessage("fundSettleDate",
                                getMessage("VAL_COMMON_E_0002", getMessage("TrdBondTrade.fundSettleDate"))));
                    }
                    // 成交数量
                    if(!StringUtil.isNotEmpty(dto.getDealNum())){
                        resultList.add(new ValidMessage("dealNum",
                                getMessage("VAL_COMMON_E_0002", getMessage("TrdBondTrade.dealNum"))));
                    }
                    // 交易金额
                    if(!StringUtil.isNotEmpty(dto.getTradeAmt())){
                        resultList.add(new ValidMessage("tradeAmt",
                                getMessage("VAL_COMMON_E_0002", getMessage("TrdBondTrade.tradeAmt"))));
                    }
                    // 应收利息总额
                    if(!StringUtil.isNotEmpty(dto.getIntAmt())){
                        resultList.add(new ValidMessage("intAmt",
                                getMessage("VAL_COMMON_E_0002", getMessage("TrdBondTrade.intAmt"))));
                    }
                    // 净价
                    if(!StringUtil.isNotEmpty(dto.getCprice())){
                        resultList.add(new ValidMessage("cprice",
                                getMessage("VAL_COMMON_E_0002", getMessage("TrdBondTrade.cprice"))));
                    }
//                    // 汇率日期
//                    if(!StringUtil.isNotEmpty(dto.getExchangeDate())){
//                        resultList.add(new ValidMessage("exchangeDate",
//                                getMessage("VAL_COMMON_E_0002", getMessage("TrdBondTrade.exchangeDate"))));
//                    }
                    // 经手费
                    if(!StringUtil.isNotEmpty(dto.getBrokerage())){
                        resultList.add(new ValidMessage("brokerage",
                                getMessage("VAL_COMMON_E_0002", getMessage("TrdBondTrade.brokerage"))));
                    }
                    // 结算费
                    if(!StringUtil.isNotEmpty(dto.getStlAmt())){
                        resultList.add(new ValidMessage("stlAmt",
                                getMessage("VAL_COMMON_E_0002", getMessage("TrdBondTrade.stlAmt"))));
                    }
                    // 净佣金
                    if(!StringUtil.isNotEmpty(dto.getCommission())){
                        resultList.add(new ValidMessage("commission",
                                getMessage("VAL_COMMON_E_0002", getMessage("TrdBondTrade.commission"))));
                    }
                    // 交易手续费
                    if(!StringUtil.isNotEmpty(dto.getDealFeeAmt())){
                        resultList.add(new ValidMessage("dealFeeAmt",
                                getMessage("VAL_COMMON_E_0002", getMessage("TrdBondTrade.dealFeeAmt"))));
                    }
                    // 结算手续费
                    if(!StringUtil.isNotEmpty(dto.getStlFeeAmt())){
                        resultList.add(new ValidMessage("stlFeeAmt",
                                getMessage("VAL_COMMON_E_0002", getMessage("TrdBondTrade.stlFeeAmt"))));
                    }
//                    // 其他费用
//                    if(!StringUtil.isNotEmpty(dto.getOtherAmt())){
//                        resultList.add(new ValidMessage("otherAmt",
//                                getMessage("VAL_COMMON_E_0002", getMessage("TrdBondTrade.otherAmt"))));
//                    }
                    // 结算金额
                    if(!StringUtil.isNotEmpty(dto.getSettleAmt())){
                        resultList.add(new ValidMessage("settleAmt",
                                getMessage("VAL_COMMON_E_0002", getMessage("TrdBondTrade.settleAmt"))));
                    }
                    // 资金账户
                    if(!StringUtil.isNotEmpty(dto.getMyAcctNo())){
                        resultList.add(new ValidMessage("myAcctNo",
                                getMessage("VAL_COMMON_E_0002", getMessage("TrdBondTrade.myAcctNo"))));
                    }
                }
                //结算金额校验
                //买入：结算金额=净价成交金额+应收利息总额+经手费+结算费+净佣金+交易手续费+结算服务费
                if(DictCodeList.交易方向_买入.getValue().equals(dto.getPs())){
                    BigDecimal account = ArithmeticUtil.doAdd(dto.getTradeAmt(),dto.getIntAmt(),dto.getBrokerage()
                            ,dto.getStlAmt(),dto.getCommission(),dto.getDealFeeAmt(),dto.getStlFeeAmt());
                    if(account.compareTo(dto.getSettleAmt()) != 0){
                        resultList.add(new ValidMessage("settleAmt",
                                getMessage("结算金额需满足：结算金额=净价成交金额+应收利息总额+经手费+结算费+净佣金+交易手续费+结算服务费")));

                    }
                }//卖出：结算金额=净价成交金额+应收利息总额-经手费-结算费-净佣金-交易手续费-结算服务费
                else if(DictCodeList.交易方向_卖出.getValue().equals(dto.getPs())
                        || DictCodeList.交易方向_行权_回售.getValue().equals(dto.getPs())
                        || DictCodeList.交易方向_行权_赎回.getValue().equals(dto.getPs())){
                    BigDecimal account = ArithmeticUtil.doSub(ArithmeticUtil.doAdd(dto.getTradeAmt(),dto.getIntAmt()),
                            ArithmeticUtil.doAdd(dto.getBrokerage(),dto.getStlAmt(),dto.getCommission(),dto.getDealFeeAmt(),dto.getStlFeeAmt()));
                    if(account.compareTo(dto.getSettleAmt()) != 0){
                        resultList.add(new ValidMessage("settleAmt",
                                getMessage("结算金额需满足：结算金额=净价成交金额+应收利息总额-经手费-结算费-净佣金-交易手续费-结算服务费")));

                    }
                }
            }

        }

         return resultList;
    }

}
