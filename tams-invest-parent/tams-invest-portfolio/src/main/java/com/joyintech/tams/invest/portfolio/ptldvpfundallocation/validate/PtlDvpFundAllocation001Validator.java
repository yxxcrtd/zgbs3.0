/*
 * 文件名：PtlFundAllocation001Validator.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：etliu
 * 修改时间：2019年2月18日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.portfolio.ptldvpfundallocation.validate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.BeanValidator;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.invest.portfolio.ptldvpfundallocation.dto.PtlDvpFundAllocationDto;

/**
 * Dvp资金划拨检测类
 * 
 * @author etliu
 * @version 1.0 2019年2月18日
 * @see PtlDvpFundAllocation001Validator
 * @since 1.0
 */
@Transactional
@Component
public class PtlDvpFundAllocation001Validator implements BeanValidator {

    public List<ValidMessage> valid(Object value) {
        ArrayList<ValidMessage> errorList = new ArrayList<ValidMessage>();// 生成错误集合
        PtlDvpFundAllocationDto dto = (PtlDvpFundAllocationDto)value;
        if (StringUtil.equals(dto.getOperateType(), "submit")
            || StringUtil.equals(dto.getOperateType(), "save")) {
            
            // 划拨金额必须大于0
            if (dto.getSettleAmt() != null && BigDecimal.ZERO.compareTo(dto.getSettleAmt()) >= 0) {
                errorList.add(new ValidMessage(getMessage("BUSS_COM_E_0036",
                    getMessage("PtlDvpFundAllocation001.settleAmts"), "0")));
            }

//            // 划拨金额不能大于付款账户余额
//            if (dto.getMyAcctAmount() != null
//                && dto.getSettleAmt() != null
//                && dto.getMyAcctAmount().compareTo(dto.getSettleAmt()) < 0) {
//                errorList.add(new ValidMessage(
//                    getMessage("BUSS_COM_E_0003", getMessage("PtlDvpFundAllocation001.settleAmts"),
//                        getMessage("PtlDvpFundAllocation001.myAcctAmount"))));
//            }

            // 划款指令必填
            if (StringUtil.isEmptyList(dto.getBussTrdTransferOrder())) {
                errorList.add(new ValidMessage(
                    getMessage("FW_E_0006", getMessage("PtlActivityPeriod001.stlPayOrders"))));
            }
        }
        return errorList;
    }
}
