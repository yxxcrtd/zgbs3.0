/*
 * 文件名：FinDepositTargetValidator.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangnian
 * 修改时间：2018年10月30日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.valuation.trademanage.trdstockvaluationduration.validator;


import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.BeanValidator;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.valuation.trademanage.trdstockvaluationduration.dto.TrdStockValuationDurationDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 *  利率互换交易--业务验证
 * 〈功能详细描述〉
 * @author sunshuaishaui
 * @version 1.0 2022年02月17日
 * @see TrdStockValuationDurationValidator
 * @since 1.0
 */
@Component
public class TrdStockValuationDurationValidator implements BeanValidator {

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ValidMessage> valid(Object value) {
        TrdStockValuationDurationDto dto = (TrdStockValuationDurationDto)value;
        List<ValidMessage> resultList = new ArrayList<ValidMessage>();
        if("delete".equals(dto.getOperateType())){

        }else{
            if(DictCodeList.股票分红类型_现金分红.getValue().equals(dto.getPs())){
                //权益登记日
                if(!StringUtil.isNotEmpty(dto.getRegDate())){
                    resultList.add(new ValidMessage("regDate",
                            getMessage("VAL_COMMON_E_0002", getMessage("TrdStockValuationDuration.regDate"))));
                }
                //派息日
                if(!StringUtil.isNotEmpty(dto.getDividDate())){
                    resultList.add(new ValidMessage("dividDate",
                            getMessage("VAL_COMMON_E_0002", getMessage("TrdStockValuationDuration.dividDate"))));
                }
                //权益登记日股本
                if(!StringUtil.isNotEmpty(dto.getAmount())){
                    resultList.add(new ValidMessage("amount",
                            getMessage("VAL_COMMON_E_0002", getMessage("TrdStockValuationDuration.amount"))));
                }
                //结算金额
                if(!StringUtil.isNotEmpty(dto.getSettleAmt())){
                    resultList.add(new ValidMessage("settleAmt",
                            getMessage("VAL_COMMON_E_0002", getMessage("TrdStockValuationDuration.settleAmt"))));
                }
                //资金账户
                if(!StringUtil.isNotEmpty(dto.getMyAcctNo())){
                    resultList.add(new ValidMessage("myAcctNo",
                            getMessage("VAL_COMMON_E_0002", getMessage("TrdStockValuationDuration.myAcctNo"))));
                }
            }
            if(DictCodeList.股票分红类型_送转.getValue().equals(dto.getPs())){
                //权益登记日
                if(!StringUtil.isNotEmpty(dto.getRegDate_sz())){
                    resultList.add(new ValidMessage("regDate_sz",
                            getMessage("VAL_COMMON_E_0002", getMessage("TrdStockValuationDuration.regDate"))));
                }
                //派息日
                if(!StringUtil.isNotEmpty(dto.getDividDate_sz())){
                    resultList.add(new ValidMessage("dividDate_sz",
                            getMessage("VAL_COMMON_E_0002", getMessage("TrdStockValuationDuration.dividDate"))));
                }
                //权益登记日股本
                if(!StringUtil.isNotEmpty(dto.getAmount_sz())){
                    resultList.add(new ValidMessage("amount_sz",
                            getMessage("VAL_COMMON_E_0002", getMessage("TrdStockValuationDuration.amount"))));
                }
                //分红股本
                if(!StringUtil.isNotEmpty(dto.getDevidentAmt_sz())){
                    resultList.add(new ValidMessage("devidentAmt_sz",
                            getMessage("VAL_COMMON_E_0002", getMessage("TrdStockValuationDuration.devidentAmt"))));
                }
                //资金账户
                if(!StringUtil.isNotEmpty(dto.getMyAcctNo_sz())){
                    resultList.add(new ValidMessage("myAcctNo_sz",
                            getMessage("VAL_COMMON_E_0002", getMessage("TrdStockValuationDuration.myAcctNo"))));
                }
            }
            if(DictCodeList.股票分红类型_分红送转.getValue().equals(dto.getPs())){
                //权益登记日
                if(!StringUtil.isNotEmpty(dto.getRegDate_fhsz())){
                    resultList.add(new ValidMessage("regDate_fhsz",
                            getMessage("VAL_COMMON_E_0002", getMessage("TrdStockValuationDuration.regDate"))));
                }
                //派息日
                if(!StringUtil.isNotEmpty(dto.getDividDate_fhsz())){
                    resultList.add(new ValidMessage("dividDate_fhsz",
                            getMessage("VAL_COMMON_E_0002", getMessage("TrdStockValuationDuration.dividDate"))));
                }
                //权益登记日股本
                if(!StringUtil.isNotEmpty(dto.getAmount_fhsz())){
                    resultList.add(new ValidMessage("amount_fhsz",
                            getMessage("VAL_COMMON_E_0002", getMessage("TrdStockValuationDuration.amount"))));
                }
                //结算金额
                if(!StringUtil.isNotEmpty(dto.getSettleAmt_fhsz())){
                    resultList.add(new ValidMessage("settleAmt_fhsz",
                            getMessage("VAL_COMMON_E_0002", getMessage("TrdStockValuationDuration.settleAmt"))));
                }
                //资金账户
                if(!StringUtil.isNotEmpty(dto.getMyAcctNo_fhsz())){
                    resultList.add(new ValidMessage("myAcctNo_fhsz",
                            getMessage("VAL_COMMON_E_0002", getMessage("TrdStockValuationDuration.myAcctNo"))));
                }
            }
//            if(!StringUtil.isNotEmpty(dto.getPs())){
//                //权益登记日
//                if(!StringUtil.isNotEmpty(dto.getRegDate())){
//                    resultList.add(new ValidMessage("regDate",
//                            getMessage("VAL_COMMON_E_0002", getMessage("TrdStockValuationDuration.regDate"))));
//                }
//                //派息日
//                if(!StringUtil.isNotEmpty(dto.getDividDate())){
//                    resultList.add(new ValidMessage("dividDate",
//                            getMessage("VAL_COMMON_E_0002", getMessage("TrdStockValuationDuration.dividDate"))));
//                }
//                //权益登记日股本
//                if(!StringUtil.isNotEmpty(dto.getAmount())){
//                    resultList.add(new ValidMessage("amount",
//                            getMessage("VAL_COMMON_E_0002", getMessage("TrdStockValuationDuration.amount"))));
//                }
//                //结算金额
//                if(!StringUtil.isNotEmpty(dto.getSettleAmt())){
//                    resultList.add(new ValidMessage("settleAmt",
//                            getMessage("VAL_COMMON_E_0002", getMessage("TrdStockValuationDuration.settleAmt"))));
//                }
//                //资金账户
//                if(!StringUtil.isNotEmpty(dto.getMyAcctNo())){
//                    resultList.add(new ValidMessage("myAcctNo",
//                            getMessage("VAL_COMMON_E_0002", getMessage("TrdStockValuationDuration.myAcctNo"))));
//                }
//                //权益登记日
//                if(!StringUtil.isNotEmpty(dto.getRegDate_sz())){
//                    resultList.add(new ValidMessage("regDate_sz",
//                            getMessage("VAL_COMMON_E_0002", getMessage("TrdStockValuationDuration.regDate"))));
//                }
//                //派息日
//                if(!StringUtil.isNotEmpty(dto.getDividDate_sz())){
//                    resultList.add(new ValidMessage("dividDate_sz",
//                            getMessage("VAL_COMMON_E_0002", getMessage("TrdStockValuationDuration.dividDate"))));
//                }
//                //权益登记日股本
//                if(!StringUtil.isNotEmpty(dto.getAmount_sz())){
//                    resultList.add(new ValidMessage("amount_sz",
//                            getMessage("VAL_COMMON_E_0002", getMessage("TrdStockValuationDuration.amount"))));
//                }
//                //分红股本
//                if(!StringUtil.isNotEmpty(dto.getDevidentAmt_sz())){
//                    resultList.add(new ValidMessage("devidentAmt_sz",
//                            getMessage("VAL_COMMON_E_0002", getMessage("TrdStockValuationDuration.devidentAmt"))));
//                }
//                //资金账户
//                if(!StringUtil.isNotEmpty(dto.getMyAcctNo_sz())){
//                    resultList.add(new ValidMessage("myAcctNo_sz",
//                            getMessage("VAL_COMMON_E_0002", getMessage("TrdStockValuationDuration.myAcctNo"))));
//                }
//                //权益登记日
//                if(!StringUtil.isNotEmpty(dto.getRegDate_fhsz())){
//                    resultList.add(new ValidMessage("regDate_fhsz",
//                            getMessage("VAL_COMMON_E_0002", getMessage("TrdStockValuationDuration.regDate"))));
//                }
//                //派息日
//                if(!StringUtil.isNotEmpty(dto.getDividDate_fhsz())){
//                    resultList.add(new ValidMessage("dividDate_fhsz",
//                            getMessage("VAL_COMMON_E_0002", getMessage("TrdStockValuationDuration.dividDate"))));
//                }
//                //权益登记日股本
//                if(!StringUtil.isNotEmpty(dto.getAmount_fhsz())){
//                    resultList.add(new ValidMessage("amount_fhsz",
//                            getMessage("VAL_COMMON_E_0002", getMessage("TrdStockValuationDuration.amount"))));
//                }
//                //结算金额
//                if(!StringUtil.isNotEmpty(dto.getSettleAmt_fhsz())){
//                    resultList.add(new ValidMessage("settleAmt_fhsz",
//                            getMessage("VAL_COMMON_E_0002", getMessage("TrdStockValuationDuration.settleAmt"))));
//                }
//                //资金账户
//                if(!StringUtil.isNotEmpty(dto.getMyAcctNo_fhsz())){
//                    resultList.add(new ValidMessage("myAcctNo_fhsz",
//                            getMessage("VAL_COMMON_E_0002", getMessage("TrdStockValuationDuration.myAcctNo"))));
//                }
//            }
        }

         return resultList;
    }

}
