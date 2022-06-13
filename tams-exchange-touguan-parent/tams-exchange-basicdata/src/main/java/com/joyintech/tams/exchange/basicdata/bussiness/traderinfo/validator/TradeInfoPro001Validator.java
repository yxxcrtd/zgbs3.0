package com.joyintech.tams.exchange.basicdata.bussiness.traderinfo.validator;

import com.joyintech.framework.validation.BeanValidator;
import com.joyintech.framework.validation.ValidMessage;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TradeInfoPro001Validator implements BeanValidator {
    @Override
    public List<ValidMessage> valid(Object o) {
        return null;
    }
}
