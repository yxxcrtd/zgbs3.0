/*
 * 文件名：FinDepositTargetValidator.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangnian
 * 修改时间：2018年10月30日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.valuation.trademanage.trdfixincomevaluduration.validator;


import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.BeanValidator;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.valuation.component.common.dto.FinCashPlanDto;
import com.joyintech.tams.valuation.trademanage.trdbondvaluationduration.dto.TrdBondValuationDurationDto;
import com.joyintech.tams.valuation.trademanage.trdbondvaluationduration.service.TrdBondValuationDuration002Service;
import com.joyintech.tams.valuation.trademanage.trdfixincomevaluduration.dto.TrdFixIncomeValuDuration001Dto;
import com.joyintech.tams.valuation.trademanage.trdfixincomevaluduration.service.TrdFixIncomeValuDuration002Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  固收类存续期--业务验证
 * 〈功能详细描述〉
 * @author sunshuaishaui
 * @version 1.0 2022年02月17日
 * @see TrdFixIncomeValuDurationValidator
 * @since 1.0
 */
@Component
public class TrdFixIncomeValuDurationValidator implements BeanValidator {

    @Autowired
    private TrdFixIncomeValuDuration002Service fixIncomeValuDuration002Service;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ValidMessage> valid(Object value) {
        TrdFixIncomeValuDuration001Dto dto = (TrdFixIncomeValuDuration001Dto)value;
        List<ValidMessage> resultList = new ArrayList<ValidMessage>();
        if("delete".equals(dto.getOperateType())){

        }else{
            //校验标的付息开始日是否存在
            //查询债券的付息计划
            Map<String,Object> hashMap = new HashMap<>();
            hashMap.put("finprodId",dto.getAssetId());
            hashMap.put("mdate", DateUtil.format(dto.getTradeDate()));
            List<FinCashPlanDto> list = fixIncomeValuDuration002Service.getFinCashPlanByProductId(hashMap);
            if(StringUtil.isEmptyList(list)){
                resultList.add(new ValidMessage("mdate",
                        getMessage("未匹配到对用的标的付息计划，请维护标的付息计划或者更改计息结束日")));
            }
        }

         return resultList;
    }

}
