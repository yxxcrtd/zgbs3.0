package com.joyintech.tams.invest.batch.creditprotectioncertificate.reader;

import com.joyintech.batch.batchbase.baseclass.BaseReader;
import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.batch.creditprotectioncertificate.dao.CreditProtectionCertificatePeriodRunnerMapper;
import com.joyintech.tams.invest.batch.creditprotectioncertificate.dto.CreditProtectionCertificatePeriodReaderDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 〈功能详细描述〉
 *  信用保护凭证到期撤销
 *
 * @author yangxunpeng
 * @version 1.0 2022年05月25日
 * @since 1.0
 */
@Service
@Scope("prototype")
public class CreditProtectionCertificatePeriodReader1 extends BaseReader<CreditProtectionCertificatePeriodReaderDto> {

    @Autowired
    private CreditProtectionCertificatePeriodRunnerMapper creditProtectionCertificatePeriodRunnerMapper;

    @Override
    public List<CreditProtectionCertificatePeriodReaderDto> createList() {
        SysBatchExecInfo batchExecuteInfo = getBatchExecuteInfo();
        Date executeDate = batchExecuteInfo.getExecuteDate();
        Map<String,Object> params = new HashMap<>();
        params.put("tradeDate",executeDate);
        params.put("FINPROD_TYPE_TG_F37", DictCodeListExt1.金融产品类型_投管用_信用保护凭证.getValue());
        params.put("PS_T07", DictCodeList.交易方向_到期.getValue());
        params.put("PINAN_BUSINESS_TYPE_209",DictCodeListExt1.平安业务类型_到期.getValue());
        params.put("YSE_OR_NO_NO",DictCodeList.是否_否.getValue());
        params.put("TRADE_STATUS_01", DictCodeListExt1.交易状态_初始.getValue());
        List<CreditProtectionCertificatePeriodReaderDto> dtos = creditProtectionCertificatePeriodRunnerMapper.selectRevokedList(params);
        return dtos;
    }
}
