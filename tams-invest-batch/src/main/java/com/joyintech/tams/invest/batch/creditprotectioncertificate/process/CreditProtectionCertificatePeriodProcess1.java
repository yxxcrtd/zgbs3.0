package com.joyintech.tams.invest.batch.creditprotectioncertificate.process;

import com.joyintech.batch.batchbase.baseclass.BaseProcessor;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.invest.batch.creditprotectioncertificate.dto.CreditProtectionCertificatePeriodReaderDto;
import com.joyintech.tams.invest.batch.creditprotectioncertificate.dto.CreditProtectionCertificatePeriodWriterDto;
import com.joyintech.tams.invest.batch.creditprotectioncertificate.service.CreditProtectionCertificatePeriodRunnerService;
import com.joyintech.tams.invest.common.autoperiod.common.service.InvestPeriodCommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * 〈功能详细描述〉
 * 信用保护凭证到期撤销
 *
 * @author yangxunpeng
 * @version 1.0 2022年05月25日
 * @since 1.0
 */
@Service
@Scope("prototype")
public class CreditProtectionCertificatePeriodProcess1 extends BaseProcessor<CreditProtectionCertificatePeriodReaderDto, CreditProtectionCertificatePeriodWriterDto> {

    @Autowired
    private InvestPeriodCommonService investPeriodCommonService;

    @Autowired
    private CreditProtectionCertificatePeriodRunnerService creditProtectionCertificatePeriodRunnerService;

    @Override
    public CreditProtectionCertificatePeriodWriterDto process(CreditProtectionCertificatePeriodReaderDto readerDto) throws Exception {
        CreditProtectionCertificatePeriodWriterDto writerDto = new CreditProtectionCertificatePeriodWriterDto();
        if (null != readerDto && StringUtil.isNotEmpty(readerDto.getBussinessCode())) {
            writerDto.setBussinessCode(readerDto.getBussinessCode());

            investPeriodCommonService.revokeReverseTradeTable(readerDto.getBussinessCode(),
                    readerDto.getCreateUser(), "", null, null,null);
            //头寸处理
            creditProtectionCertificatePeriodRunnerService.doRevokePositionForNew(readerDto.getBussinessCode());
            creditProtectionCertificatePeriodRunnerService.doRevokePositionForOld(readerDto.getBussinessCode());
        }
        return writerDto;
    }
}
