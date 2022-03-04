/*
 * 文件名：TrdFixIncomeValuationTradeDtoValidator.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：fanshuanghu
 * 修改时间：2018年11月22日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.valuation.trademanage.trdfixincomevaluationtrade.validate;

import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.BeanValidator;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.valuation.trademanage.trdfixincomevaluationtrade.dto.TrdFixIncomeValuationTradeDto;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description:计划类投资交易
 * TrdFixIncomeValuationTradeDtoValidator
 * @auther: yuzj
 * @date: 2022-1-5
 * @param:
 * @return:
 */
@Transactional
@Component
public class TrdFixIncomeValuationTradeValidator implements BeanValidator {

    public List<ValidMessage> valid(Object value) {
        List<ValidMessage> validMessageList = new ArrayList<ValidMessage>();
        TrdFixIncomeValuationTradeDto dto = (TrdFixIncomeValuationTradeDto)value;

        if(dto.getOperateType().equals("submit") || dto.getOperateType().equals("save")){
            //交割日期必须大于等于交易日期；
            if(StringUtil.isNotEmpty(dto.getTradeDate())){
                if(!(DateUtil.compareDate(dto.getEffDate(), dto.getTradeDate())==1)){
                    String msg = this.getMessage("交割日期必须大于等于交易日期");
                    validMessageList.add(new ValidMessage(msg));
                }
            }
        }


        return validMessageList;
    }

}

