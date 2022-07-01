/*
 * 文件名：BussFinIntPayValidator.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：qiangchengxi
 * 修改时间：2018年11月8日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.component.business.asset.validator;

import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.BeanValidator;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.component.business.asset.dto.BussFinIntPayDto;
import com.joyintech.tams.constant.DictCodeList;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 〈一句话功能简述〉
 * 〈功能详细描述〉
 *
 * @author qiangchengxi
 * @version 1.0 2018年11月8日
 * @see BussFinIntPayValidator
 * @since 1.0
 */
@Component
public class BussFinIntPayValidator implements BeanValidator {

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ValidMessage> valid(Object value) {
        BussFinIntPayDto dto = (BussFinIntPayDto) value;
        List<ValidMessage> resultList = new ArrayList<>();
        if (StringUtil.equals(dto.getPayCycle(), DictCodeList.收益结转频率_按月结转.getValue())) {
            if (null == dto.getDayValue() || "".equals(dto.getDayValue())) {
                resultList.add(new ValidMessage("dayValue",
                        getMessage("COMMON_E_0057", getMessage("BussFinIntPay.dayValue"))));
            }
        }
        if (StringUtil.equals(dto.getFundType(), DictCodeList.基金类资产类型_货币基金.getValue()) &&
                StringUtil.equals(dto.getPayCycle(), DictCodeList.收益结转频率_按月结转.getValue())) {
            if (dto.getEndTdayRule() == null
                    || "".equals(dto.getEndTdayRule())) {
                resultList.add(new ValidMessage("endTdayRule",
                        getMessage("COMMON_E_0057", getMessage("BussFinIntPay.endTdayRule"))));
            }
            if (dto.getEndTdayRule() == null
                    || "".equals(dto.getPayTdayRule())) {
                resultList.add(new ValidMessage("payTdayRule",
                        getMessage("COMMON_E_0057", getMessage("BussFinIntPay.payTdayRule"))));
            }
        }
        return resultList;
    }

}
