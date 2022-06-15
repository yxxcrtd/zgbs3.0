package com.joyintech.tams.exchange.entrust.etrexchange.cdtprocer.validator;

import com.joyintech.background.component.common.pack.common.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.exchange.entrust.etrexchange.cdtprocer.dto.EtrCreditProtectionCertificateApplyRequest;
import com.joyintech.tams.exchange.entrust.etrexchange.cdtprocer.service.EtrExchangeTradeCpc001Service;
import com.joyintech.tams.invest.entrust.common.validator.EtrCommonValidator;
import org.apache.shiro.crypto.hash.Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class EtrExchangeTradeCpc001Validator implements EtrCommonValidator {

    @Autowired
    private EtrExchangeTradeCpc001Service etrExchangeTradeCpc001Service;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ValidMessage> bussValid(Object value) {
        List<ValidMessage> errorList = new ArrayList<>();
        EtrCreditProtectionCertificateApplyRequest dto = (EtrCreditProtectionCertificateApplyRequest) value;
        if (StringUtil.isNotEmpty(dto.getOperateType())&&dto.getOperateType().equals("submit")) {
            Map<String, Object> map = new HashMap<>();
            map.put("finprodId",dto.getFinprodId());
            map.put("nowDate", DateUtil.getCurrentDate());
            List<String> list = etrExchangeTradeCpc001Service.isOverMDate(map);
            if(list.size()>0){
                errorList.add(new ValidMessage("finprodId", "["+dto.getFinprodId()+"]：该信资产代码已过期，请重新选择！"));
            }
        }
        return errorList;
    }
}
