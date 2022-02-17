/*
 * 文件名：TrdPlanRateInvestTradeDtoValidator.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：fanshuanghu
 * 修改时间：2018年11月22日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.valuation.trademanage.trdjihuabonusvaluationtrade.validate;

import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.BeanValidator;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.valuation.trademanage.trdjihuabonusvaluationtrade.dto.TrdJihuaBonusValuationTradeDto;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description:基金存续期
 * TrdJihuaBonusValuationTradeDtoValidator
 * @auther: yuzj
 * @date: 2022/02/11
 * @param:
 * @return:
 */
@Transactional
@Component
public class TrdJihuaBonusValuationTradeDtoValidator implements BeanValidator {

    public List<ValidMessage> valid(Object value) {
        List<ValidMessage> validMessageList = new ArrayList<ValidMessage>();
        TrdJihuaBonusValuationTradeDto dto = (TrdJihuaBonusValuationTradeDto)value;
        if(dto.getOperateType().equals("submit") || dto.getOperateType().equals("save")) {
            //分红份额 - 当交易类型为红利再投时必填
            if (StringUtil.isNotEmpty(dto.getPs()) && StringUtil.equals(dto.getPs(), DictCodeList.交易方向_红利再投.getValue())) {
                if (dto.getNums() == null) {
                    String msg = this.getMessage("当交易类型为红利再投时,分红份额必填");
                    validMessageList.add(new ValidMessage(msg));
                }
            }
        }

        return validMessageList;
    }

}

