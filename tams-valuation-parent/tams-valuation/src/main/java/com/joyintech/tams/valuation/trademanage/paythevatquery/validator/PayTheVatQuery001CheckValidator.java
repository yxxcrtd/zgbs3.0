/*
 * 文件名：PayTheVatQuery002CheckValidator.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：wangqidong
 * 修改时间：2019年2月28日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.trademanage.paythevatquery.validator;

import java.util.ArrayList;
import java.util.List;

import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.joyintech.framework.validation.BeanValidator;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.valuation.trademanage.paythevatquery.dto.PayTheVatQuery001Dto;
import com.joyintech.tams.valuation.trademanage.paythevatquery.service.PayTheVatQuery001Service;
import com.joyintech.tams.valuation.trademanage.paythevatquery.service.PayTheVatQuery003Service;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;

/**
 * 增值税缴纳校验
 * 
 * @author wangqidong
 * @version 1.0 2019年2月28日
 * @see PayTheVatQuery001CheckValidator
 * @since 1.0
 */
@Component
public class PayTheVatQuery001CheckValidator implements BeanValidator {

    @Autowired
    private PayTheVatQuery001Service payTheVatQuery001Service;

    @Autowired
    private PayTheVatQuery003Service payTheVatQuery003Service;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ValidMessage> valid(Object value) {
        PayTheVatQuery001Dto dto = (PayTheVatQuery001Dto)value;

        // 错误信息
        ArrayList<ValidMessage> errorList = new ArrayList<>();

        // 判断当前流程是否存在
        if (payTheVatQuery003Service.checkBusinessDataInFlow(dto)) {
            // 流程存在情况
            errorList.add(new ValidMessage(getMessage("COMMON_E_0021", "标识", dto.getTradeId())));
        }
        if (DictCodeListExt1.交易状态_撤销.getValue().equals(dto.getTradeStatus())) {
            // 当页面选择数据状态为撤销时 提示"选择的有效数据。";
            errorList.add(new ValidMessage(getMessage("VAL_COMMON_E_0070")));
        }
        else {
            // 判断条件: 存在一条数据,数据状态 = '撤销' 并且原交易编号 = 选择行.交易编号.
            List<PayTheVatQuery001Dto> list = payTheVatQuery001Service.findByPTradeId(
                dto.getTradeId(), DictCodeListExt1.交易状态_撤销.getValue());
            if (CollectionUtils.isNotEmpty(list)) {
                // 当页面选择数据已被撤销 提示"选择的数据已经被撤销。";
                errorList.add(new ValidMessage(getMessage("VAL_COMMON_E_0071")));
            }
        }

        return errorList;
    }

}
