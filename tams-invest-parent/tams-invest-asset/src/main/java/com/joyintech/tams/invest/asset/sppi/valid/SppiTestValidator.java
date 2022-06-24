/*
 * 文件名：SppiTestVValidator
 * 版权：Copyright 2017-2020 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：Jiaoxujin
 * 修改时间：2020-10-14
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
package com.joyintech.tams.invest.asset.sppi.valid;

import com.joyintech.framework.validation.BeanValidator;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.invest.asset.sppi.dto.SppiTemplateDto;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * SPPI测试表单验证类
 *
 * @author Jiaoxujin
 * @version 1.0 2020-10-14
 */
@Component
public class SppiTestValidator implements BeanValidator {
    @Override
    public List<ValidMessage> valid(Object value) {
        List<ValidMessage> resultList = new ArrayList<ValidMessage>();
        SppiTemplateDto dto = (SppiTemplateDto)value;

        return resultList;
    }

    public List<ValidMessage> validData(Object value) {
        List<ValidMessage> resultList = new ArrayList<ValidMessage>();
        SppiTemplateDto dto = (SppiTemplateDto)value;

        if (CollectionUtils.isEmpty(dto.getQaList())) {
            resultList.add(new ValidMessage(this.getMessage("SppiTest002.qaListIsEmpty")));
        }
        else {
            // 对问题列表的必填项进行校验
            if (CollectionUtils.isNotEmpty(dto.getRequiredQaList())) {
                dto.getRequiredQaList().forEach(p -> {
                    long count = dto.getQaList().stream().filter(x -> StringUtils.equals(x.getQuestionNo(), p))
                            .filter(y -> StringUtils.isNotBlank(y.getAnswerValue())).count();
                    // 如果没找到
                    if (count == 0) {
                        String fieldId = p;

                        // 如果是radioBox、checkBox，则Id为其外面的div
//                        String inputType = MapUtils.getString(dto.getInputAttrMap(), p);
//                        switch (inputType) {
//                            case BussinessConstant.INPUT_TYPE_RADIO:
//                                fieldId = fieldId + "_Y";
//                                break;
//                            case BussinessConstant.INPUT_TYPE_CHECKBOX:
//                                fieldId = fieldId + "_Y";
//                                break;
//                        }

                        resultList.add(new ValidMessage(fieldId, getMessage("SppiTest002.ansertIsEmpty", getMessage(
                                StringUtils.replaceOnce(p, "sppiTest002Q", "SppiTest002.q")))));
                    }
                });
            }
        }

        return resultList;
    }
}
