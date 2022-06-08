package com.joyintech.tams.invest.batch.creditprotectioncertificate.reader;

import com.joyintech.batch.batchbase.baseclass.BaseReader;
import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.invest.batch.creditprotectioncertificate.dao.CreditProtectionCertificatePeriodRunnerMapper;
import com.joyintech.tams.invest.batch.creditprotectioncertificate.dto.CreditProtectionCertificatePeriodReaderDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 〈功能详细描述〉
 * 信用保护凭证到期筛选
 *
 * @author yangxunpeng
 * @version 1.0 2022年05月25日
 * @since 1.0
 */
@Service
@Scope("prototype")
public class CreditProtectionCertificatePeriodReader2 extends BaseReader<CreditProtectionCertificatePeriodReaderDto> {


    @Autowired
    private CreditProtectionCertificatePeriodRunnerMapper creditProtectionCertificatePeriodRunnerMapper;

    @Override
    public List<CreditProtectionCertificatePeriodReaderDto> createList() {

        SysBatchExecInfo batchExecuteInfo = getBatchExecuteInfo();
        Date executeDate = batchExecuteInfo.getExecuteDate();

        List<CreditProtectionCertificatePeriodReaderDto> result;

        String newSecPositionSwitch = CacheUtil.getSysParam("NEW_SEC_POSITION_SWITCH");
        // 开关配置N查老头存
        if (DictCodeList.是否_否.getValue().equals(newSecPositionSwitch)) {
            result = creditProtectionCertificatePeriodRunnerMapper.selectExpireFinprodIdForOld(executeDate);
        } else {
            // 默认查新头寸
            result = creditProtectionCertificatePeriodRunnerMapper.selectExpireFinprodIdForNew(executeDate);
        }
        return result;
    }
}
