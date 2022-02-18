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
import com.joyintech.tams.valuation.trademanage.trdstockvaluationtrade.dto.TrdStockTrade002Dto;
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
        TrdStockTrade002Dto dto = (TrdStockTrade002Dto)value;
        List<ValidMessage> resultList = new ArrayList<ValidMessage>();
        if("delete".equals(dto.getOperateType())){

        }else{
            if(DictCodeList.是否_是.getValue().equals(dto.getIsLock())){
                if(!StringUtil.isNotEmpty(dto.getEndDate())){
                    resultList.add(new ValidMessage("endDate",
                            getMessage("VAL_COMMON_E_0002", getMessage("锁定期截止日"))));
                }
            }
        }

         return resultList;
    }

}
