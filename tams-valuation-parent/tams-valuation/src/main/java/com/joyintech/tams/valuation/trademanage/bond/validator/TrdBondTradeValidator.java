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


import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.validation.BeanValidator;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.valuation.trademanage.bond.dto.TrdBondTrade002Dto;
import org.springframework.stereotype.Component;

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
        TrdBondTrade002Dto dto = (TrdBondTrade002Dto)value;
        List<ValidMessage> resultList = new ArrayList<ValidMessage>();
        if("delete".equals(dto.getOperateType())){

        }else{
            //交割日期必须大于等于交易日期
            if(null!=dto.getSettdateDate()&&null!=dto.getTradeDate()){
                if(DateUtil.compareDate(dto.getTradeDate(),dto.getSettdateDate())!=-1
                    && DateUtil.compareDate(dto.getTradeDate(),dto.getSettdateDate())!=0){
                    resultList.add(new ValidMessage("settdateDate",
                            getMessage("BUSS_COM_E_0004", getMessage("TrdBondTrade002.settdateDate"),getMessage("TrdBondTrade002.tradeDate"))));
                }
            }
        }

         return resultList;
    }

}
