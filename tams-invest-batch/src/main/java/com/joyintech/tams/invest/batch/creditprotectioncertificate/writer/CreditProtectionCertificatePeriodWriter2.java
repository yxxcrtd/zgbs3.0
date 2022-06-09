package com.joyintech.tams.invest.batch.creditprotectioncertificate.writer;

import com.joyintech.batch.batchbase.baseclass.BaseWriter;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.invest.batch.creditprotectioncertificate.dto.CreditProtectionCertificatePeriodWriterDto;
import com.joyintech.tams.invest.batch.creditprotectioncertificate.service.CreditProtectionCertificatePeriodRunnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * 〈功能详细描述〉
 * 信用保护凭证到期入库
 *
 * @author yangxunpeng
 * @version 1.0 2022年05月25日
 * @since 1.0
 */
@Service
@Scope("prototype")
public class CreditProtectionCertificatePeriodWriter2 extends BaseWriter<CreditProtectionCertificatePeriodWriterDto> {

    @Autowired
    private CreditProtectionCertificatePeriodRunnerService creditProtectionCertificatePeriodRunnerService;

    @Override
    public void write(List<? extends CreditProtectionCertificatePeriodWriterDto> list) throws Exception {
        if (!CollectionUtils.isEmpty(list)) {
            for (CreditProtectionCertificatePeriodWriterDto writerDto : list) {
                if (StringUtil.isNotEmpty(writerDto.getFinprodId()) && !CollectionUtils.isEmpty(writerDto.getExpireDatas())) {
                    creditProtectionCertificatePeriodRunnerService.dealExpireData(writerDto);
                }
            }
        }
    }
}
