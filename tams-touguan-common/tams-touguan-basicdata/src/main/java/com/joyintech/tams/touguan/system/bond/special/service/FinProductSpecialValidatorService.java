package com.joyintech.tams.touguan.system.bond.special.service;

import com.joyintech.background.component.common.pack.common.util.StringUtil;
import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.framework.validation.BeanValidator;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.component.business.core.dto.WflValidInputParameter;
import com.joyintech.tams.component.business.core.service.MstWorkflowCommonService;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.touguan.system.bond.special.dto.req.FinProductSpecialSaveRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 债券特殊信息 check service
 *
 * @author zhuys
 * @date 2022-05-06
 */
@Component
public class FinProductSpecialValidatorService implements BeanValidator {


    @Autowired
    private MstWorkflowCommonService mstWorkflowCommonService;
    @Autowired
    private FinProductSpecialService finProductSpecialService;

    @Override
    public List<ValidMessage> valid(Object value) {
        List<ValidMessage> resultList = new ArrayList<>();
        FinProductSpecialSaveRequest dto = (FinProductSpecialSaveRequest) value;
        if (StringUtil.isBlank(dto.getFinprodMarketId())) {
            resultList.add(new ValidMessage(getMessage("{债券代码}字段必填!")));
        }
        if (StringUtil.isBlank(dto.getIsAbs())) {
            resultList.add(new ValidMessage(getMessage("{是否资产支持证券}字段必填!")));
        }
        if (StringUtil.isBlank(dto.getIsSpecialDebt())) {
            resultList.add(new ValidMessage(getMessage("{是否特殊债}字段必填!")));
        }
        if (StringUtil.isBlank(dto.getIsDefaultBondIndicator())) {
            resultList.add(new ValidMessage(getMessage("{是否到期违约债}字段必填!")));
        }
        if (DictCodeList.是否_否.getValue().equals(dto.getIsDefaultBondIndicator())
                && DictCodeList.是否_否.getValue().equals(dto.getIsAbs())
                && DictCodeList.是否_否.getValue().equals(dto.getIsSpecialDebt())) {
            resultList.add(new ValidMessage(getMessage("特殊债券标识至少其中一个为是!")));
        }
        //判断特殊资产维护是否存在流程中数据
        WflValidInputParameter parameter = new WflValidInputParameter();
        parameter.setPageId("FinProductSpecial001");
        parameter.setBusinessDataPk1(dto.getFinprodMarketId());
        ProcessMark dataEntityFromProcessMark = mstWorkflowCommonService.getDataEntityFromProcessMark(parameter);
        if (Objects.nonNull(dataEntityFromProcessMark)) {
            resultList.add(new ValidMessage("", getMessage("资产存在流程中数据,不可删除!" + dataEntityFromProcessMark.getWorkflowId())));
        }
        //判断是否流程中的数据
        Integer countFinProdIdProcessing = finProductSpecialService.countFinprodIdProcessing(dto.getFinprodMarketId());
        if (countFinProdIdProcessing > 0) {
            resultList.add(new ValidMessage(getMessage("存在有效或流程中的当日指令及交易数据，不允许修改!")));
        }
        return resultList;
    }
}