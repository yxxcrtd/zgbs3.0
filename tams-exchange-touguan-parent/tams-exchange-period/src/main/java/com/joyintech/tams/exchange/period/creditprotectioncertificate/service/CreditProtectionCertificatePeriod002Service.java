package com.joyintech.tams.exchange.period.creditprotectioncertificate.service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.tams.exchange.period.creditprotectioncertificate.dao.CreditProtectionCertificatePeriodMapper;
import com.joyintech.tams.exchange.period.creditprotectioncertificate.dto.CreditProtectionCertificatePeriodSaveDto;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CreditProtectionCertificatePeriod002Service extends BaseService<CreditProtectionCertificatePeriodSaveDto> implements BaseProcessService<CreditProtectionCertificatePeriodSaveDto> {

    @Autowired
    private CreditProtectionCertificatePeriodMapper creditProtectionCertificatePeriodMapper;


    @Override
    public void doComplete(CreditProtectionCertificatePeriodSaveDto creditProtectionCertificatePeriodSaveDto, OperationType operationType) {

    }
}
