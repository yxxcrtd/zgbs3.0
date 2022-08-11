package com.joyintech.tams.invest.period.trdseclendduration.validator;

import com.joyintech.framework.validation.BeanValidator;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.component.business.trade.BussTrdTransferOrderService;
import com.joyintech.tams.invest.period.common.service.PeriodCommonService;
import com.joyintech.tams.invest.period.trdseclendduration.dto.TrdSecLendDuration002Dto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 债券借贷存续期管理
 *
 * @author 裴家俊
 * @version 1.0 2019年7月01日
 * @see TrdSecLendDuration002DtoValidator
 * @since 1.0
 */
@Component
public class TrdSecLendDuration002DtoValidator implements BeanValidator {

    @Autowired
    private PeriodCommonService periodCommonService;
    @Autowired
    private BussTrdTransferOrderService bussTrdTransferOrderService;

    @Override
    public List<ValidMessage> valid(Object value) {
        TrdSecLendDuration002Dto dto = (TrdSecLendDuration002Dto)value;
        List<ValidMessage> errorList = new ArrayList<>();
        if (!"revoke".equals(dto.getOperateType())) {
            errorList.addAll(bussTrdTransferOrderService.check(dto.getBussTrdTransferOrder()));
        }else{
            periodCommonService.periodCommonRevokeCheck(dto.getStatusValid(), dto.getBusiCode(), errorList);
        }
        return errorList;
    }
}
