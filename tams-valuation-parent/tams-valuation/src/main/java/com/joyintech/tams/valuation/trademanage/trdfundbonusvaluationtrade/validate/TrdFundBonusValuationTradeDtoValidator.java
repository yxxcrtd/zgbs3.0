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
        List<ValidMessage> resultList = new ArrayList<ValidMessage>();
        TrdFundBonusValuationTradeDto dto = (TrdFundBonusValuationTradeDto)value;

        if("delete".equals(dto.getOperateType())){

        }else{
            //交易类型为现金分红
            if(DictCodeList.交易方向_现金分红.getValue().equals(dto.getPs())){
                //交易日期
                if(!StringUtil.isNotEmpty(dto.getTradeDate())){
                    resultList.add(new ValidMessage("tradeDate",
                            getMessage("VAL_COMMON_E_0002", getMessage("TrdFundBonusValuationTrade002.tradeDate"))));
                }
                //权益登记日
                if(!StringUtil.isNotEmpty(dto.getMdate())){
                    resultList.add(new ValidMessage("mdate",
                            getMessage("VAL_COMMON_E_0002", getMessage("TrdFundBonusValuationTrade002.mdate"))));
                }
                //资金到账日
                if(!StringUtil.isNotEmpty(dto.getSettleDate())){
                    resultList.add(new ValidMessage("settleDate",
                            getMessage("VAL_COMMON_E_0002", getMessage("TrdFundBonusValuationTrade002.settleDate"))));
                }
//                //结算金额
//                if(!StringUtil.isNotEmpty(dto.getSettleAmt())){
//                    resultList.add(new ValidMessage("settleAmt",
//                            getMessage("VAL_COMMON_E_0002", getMessage("TrdFundBonusValuationTrade002.settleAmt"))));
//                }
                //资金账户
                if(!StringUtil.isNotEmpty(dto.getMyAcctNo())){
                    resultList.add(new ValidMessage("myAcctNo",
                            getMessage("VAL_COMMON_E_0002", getMessage("TrdFundBonusValuationTrade002.myAcctNo"))));
                }
            }else if(DictCodeList.交易方向_红利再投.getValue().equals(dto.getPs())){
                //交易日期
                if(!StringUtil.isNotEmpty(dto.getTradeDateReInvest())){
                    resultList.add(new ValidMessage("tradeDateReInvest",
                            getMessage("VAL_COMMON_E_0002", getMessage("TrdFundBonusValuationTrade002.tradeDate"))));
                }
                //交易日期
                if(!StringUtil.isNotEmpty(dto.getMdateInvest())){
                    resultList.add(new ValidMessage("mdateInvest",
                            getMessage("VAL_COMMON_E_0002", getMessage("TrdFundBonusValuationTrade002.mdate"))));
                }
                //分红金额
                if(!StringUtil.isNotEmpty(dto.getBonusAmtReInvest())){
                    resultList.add(new ValidMessage("bonusAmtReInvest",
                            getMessage("VAL_COMMON_E_0002", getMessage("TrdFundBonusValuationTrade002.bonusAmt"))));
                }
                //交易份额
                if(!StringUtil.isNotEmpty(dto.getDealNumReInvest())){
                    resultList.add(new ValidMessage("dealNumReInvest",
                            getMessage("VAL_COMMON_E_0002", getMessage("TrdFundBonusValuationTrade002.dealNum"))));
                }
                //单位分红净值
                if(!StringUtil.isNotEmpty(dto.getUnitBonusUnitValue())){
                    resultList.add(new ValidMessage("unitBonusUnitValue",
                            getMessage("VAL_COMMON_E_0002", getMessage("TrdFundBonusValuationTrade002.unitBonusUnitValue"))));
                }
//                //结算金额
//                if(!StringUtil.isNotEmpty(dto.getSettleAmtReInvest())){
//                    resultList.add(new ValidMessage("settleAmtReInvest",
//                            getMessage("VAL_COMMON_E_0002", getMessage("TrdFundBonusValuationTrade002.settleAmtReInvest"))));
//                }
//                //资金账户
//                if(!StringUtil.isNotEmpty(dto.getMyAcctNoReInvest())){
//                    resultList.add(new ValidMessage("myAcctNoReInvest",
//                            getMessage("VAL_COMMON_E_0002", getMessage("TrdFundBonusValuationTrade002.myAcctNo"))));
//                }
            }
            //当交易类型为份额强增、份额强减
           else if( DictCodeList.交易方向_份额强增.getValue().equals(dto.getPs())
                    || DictCodeList.交易方向_份额强减.getValue().equals(dto.getPs())){
                //交易日期
                if(!StringUtil.isNotEmpty(dto.getTradeDateStrengthen())){
                    resultList.add(new ValidMessage("tradeDateStrengthen",
                            getMessage("VAL_COMMON_E_0002", getMessage("TrdFundBonusValuationTrade002.tradeDate"))));
                }
                //分红金额
                if(!StringUtil.isNotEmpty(dto.getDealNumStrengthen())){
                    resultList.add(new ValidMessage("dealNumStrengthen",
                            getMessage("VAL_COMMON_E_0002", getMessage("TrdFundBonusValuationTrade002.dealNum"))));
                }
                //资金账户
                if(!StringUtil.isNotEmpty(dto.getMyAcctNoStrengthen())){
                    resultList.add(new ValidMessage("myAcctNoStrengthen",
                            getMessage("VAL_COMMON_E_0002", getMessage("TrdFundBonusValuationTrade002.myAcctNo"))));
                }
            }
        }
        return resultList;
    }

}

