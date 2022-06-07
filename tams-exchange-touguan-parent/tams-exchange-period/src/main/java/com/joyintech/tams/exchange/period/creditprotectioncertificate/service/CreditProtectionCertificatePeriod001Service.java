package com.joyintech.tams.exchange.period.creditprotectioncertificate.service;

import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.exchange.period.creditprotectioncertificate.dao.CreditProtectionCertificatePeriodMapper;
import com.joyintech.tams.exchange.period.creditprotectioncertificate.dto.CreditProtectionCertificatePeriod001Request;
import com.joyintech.tams.exchange.period.creditprotectioncertificate.dto.CreditProtectionCertificatePeriod001Response;
import com.joyintech.tams.exchange.period.creditprotectioncertificate.dto.CreditProtectionCertificatePeriodSaveDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CreditProtectionCertificatePeriod001Service extends CommonExportService<CreditProtectionCertificatePeriod001Response> {

    @Autowired
    private CreditProtectionCertificatePeriodMapper creditProtectionCertificatePeriodMapper;

    public Page<CreditProtectionCertificatePeriod001Response> findPage(CreditProtectionCertificatePeriod001Request params) {
        Map<String, Object> paramsMap = BeanUtil.object2Map(params);
        paramsMap.put("FINPROD_TYPE_TG_F36", DictCodeListExt1.金融产品类型_投管用_信用保护凭证.getValue());
        paramsMap.put("YES_OR_NO_N", DictCodeList.是否_否.getValue());

        paramsMap.put("finprodIdList", StringUtil.splitToList(params.getFinprodId(), ","));
        paramsMap.put("busiCodeList", StringUtil.splitToList(params.getBusinessCode(), ","));
        paramsMap.put("busiTypeList", StringUtil.splitToList(params.getBusinessType(), ","));
        paramsMap.put("validStatusList", StringUtil.splitToList(params.getValidStatus(), ","));

        return super.queryForPage(params.getPageMode().getPageNo(), params.getPageMode().getPageSize(),
                () -> creditProtectionCertificatePeriodMapper.findPage(paramsMap));
    }

    public Page<SelectOptionDto> findBusiCode(CreditProtectionCertificatePeriod001Request params) {
        Map<String, Object> paramsMap = BeanUtil.object2Map(params);
        paramsMap.put("FINPROD_TYPE_TG_F36", DictCodeListExt1.金融产品类型_投管用_信用保护凭证.getValue());
        paramsMap.put("YES_OR_NO_N", DictCodeList.是否_否.getValue());
        return super.queryForPageForSelectReturn(params.getPageMode().getPageNo(), params.getPageMode().getPageSize(),
                () -> creditProtectionCertificatePeriodMapper.findBusiCode(paramsMap));
    }

    public Page<SelectOptionDto> findFinprodId(CreditProtectionCertificatePeriod001Request params) {
        Map<String, Object> paramsMap = BeanUtil.object2Map(params);
        paramsMap.put("finprodType2",DictCodeListExt1.金融产品类型_投管用_信用保护凭证.getValue());
        return super.queryForPageForSelectReturn(params.getPageMode().getPageNo(), params.getPageMode().getPageSize(),
                () -> creditProtectionCertificatePeriodMapper.findFinprodId(paramsMap));
    }
}
