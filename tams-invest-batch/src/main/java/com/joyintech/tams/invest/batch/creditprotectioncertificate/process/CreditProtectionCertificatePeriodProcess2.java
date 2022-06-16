package com.joyintech.tams.invest.batch.creditprotectioncertificate.process;

import com.joyintech.batch.batchbase.baseclass.BaseProcessor;
import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.invest.batch.creditprotectioncertificate.dao.CreditProtectionCertificatePeriodRunnerMapper;
import com.joyintech.tams.invest.batch.creditprotectioncertificate.dto.CreditProtectionCertificatePeriodExpireDataDto;
import com.joyintech.tams.invest.batch.creditprotectioncertificate.dto.CreditProtectionCertificatePeriodReaderDto;
import com.joyintech.tams.invest.batch.creditprotectioncertificate.dto.CreditProtectionCertificatePeriodWriterDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 〈功能详细描述〉
 * 信用保护凭证到期处理
 *
 * @author yangxunpeng
 * @version 1.0 2022年05月25日
 * @since 1.0
 */
@Service
@Scope("prototype")
public class CreditProtectionCertificatePeriodProcess2 extends BaseProcessor<CreditProtectionCertificatePeriodReaderDto, CreditProtectionCertificatePeriodWriterDto> {

    @Autowired
    private CreditProtectionCertificatePeriodRunnerMapper creditProtectionCertificatePeriodRunnerMapper;

    @Override
    public CreditProtectionCertificatePeriodWriterDto process(CreditProtectionCertificatePeriodReaderDto readerDto) throws Exception {
        CreditProtectionCertificatePeriodWriterDto writerDto = new CreditProtectionCertificatePeriodWriterDto();
        if (null != readerDto) {
            SysBatchExecInfo batchExecuteInfo = getBatchExecuteInfo();
            Date executeDate = batchExecuteInfo.getExecuteDate();

            List<CreditProtectionCertificatePeriodExpireDataDto> result;

            String newSecPositionSwitch = CacheUtil.getSysParam("NEW_SEC_POSITION_SWITCH");
            // 开关配置N查老头存
            if (DictCodeList.是否_否.getValue().equals(newSecPositionSwitch)) {
                result = creditProtectionCertificatePeriodRunnerMapper.selectExpireDataForOld(executeDate, readerDto.getFinprodId());
            } else {
                // 默认查新头寸
                result = creditProtectionCertificatePeriodRunnerMapper.selectExpireDataForNew(executeDate, readerDto.getFinprodId());
            }
            writerDto.setFinprodId(readerDto.getFinprodId());
            writerDto.setExpireDatas(result);
        }
        return writerDto;
    }
}
