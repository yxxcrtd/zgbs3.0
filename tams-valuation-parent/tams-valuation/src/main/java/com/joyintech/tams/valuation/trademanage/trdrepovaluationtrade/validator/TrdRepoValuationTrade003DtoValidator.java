/*
 * 文件名：TrdLendInvestTradeDtoValidator.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：刘二通
 * 修改时间：2018年11月1日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.valuation.trademanage.trdrepovaluationtrade.validator;

import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.BeanValidator;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.valuation.trademanage.trdrepovaluationtrade.dto.TrdRepoValuationTrade003Dto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 〈一句话功能简述〉
 * 〈功能详细描述〉
 * @author 刘二通
 * @version 1.0 2018年11月1日
 * @see TrdRepoValuationTrade003DtoValidator
 * @since 1.0
 */
@Component
public class TrdRepoValuationTrade003DtoValidator implements BeanValidator {


    @Override
    public List<ValidMessage> valid(Object value) {
        TrdRepoValuationTrade003Dto dto = (TrdRepoValuationTrade003Dto) value;
        List<ValidMessage> errorList = new ArrayList<>();
        //交易方向为提前终止时，提前终止利率必填
        if(StringUtil.equals(dto.getPs(), DictCodeList.交易方向_提前终止.getValue())){
            //期限必填
            if(!StringUtil.isNotEmpty(dto.getTerminateRate())){
                errorList.add(new ValidMessage("terminateRate", this.getMessage("BUSS_COM_E_0038",
                        this.getMessage("TrdRepoValuationTrade003.terminateRate"))));
            }
        }
        //交易方向为逾期返售时，逾期利息金额必填
        if(StringUtil.equals(dto.getPs(), DictCodeListExt3.交易方向_投资端_逾期返售.getValue())){
            //期限必填
            if(!StringUtil.isNotEmpty(dto.getOverdueAmt())){
                errorList.add(new ValidMessage("overdueAmt", this.getMessage("BUSS_COM_E_0038",
                        this.getMessage("TrdRepoValuationTrade003.overdueAmt"))));
            }
        }

        return errorList;
    }
}
