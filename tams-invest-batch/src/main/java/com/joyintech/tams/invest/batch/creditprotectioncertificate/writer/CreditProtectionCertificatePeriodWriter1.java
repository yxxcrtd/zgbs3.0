package com.joyintech.tams.invest.batch.creditprotectioncertificate.writer;

import com.joyintech.batch.batchbase.baseclass.BaseWriter;
import com.joyintech.tams.invest.batch.creditprotectioncertificate.dto.CreditProtectionCertificatePeriodWriterDto;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.List;

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
public class CreditProtectionCertificatePeriodWriter1 extends BaseWriter<CreditProtectionCertificatePeriodWriterDto> {

    @Override
    public void write(List<? extends CreditProtectionCertificatePeriodWriterDto> list) throws Exception {

    }
}
