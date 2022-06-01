package com.joyintech.tams.exchange.entrust.etrexchange.cdtprocer.validator;

import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.exchange.entrust.etrexchange.cdtprocer.dto.EtrCreditProtectionCertificateApplyRequest;
import com.joyintech.tams.invest.entrust.common.validator.EtrCommonValidator;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
@Component
public class EtrExchangeTradeCpc001Validator implements EtrCommonValidator {

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ValidMessage> bussValid(Object value) {
        List<ValidMessage> errorList = new ArrayList<>();
        EtrCreditProtectionCertificateApplyRequest dto = (EtrCreditProtectionCertificateApplyRequest) value;
        if (StringUtil.isNotEmpty(dto.getOperateType())&&dto.getOperateType().equals("submit")) {

        }
        return errorList;
    }
}
