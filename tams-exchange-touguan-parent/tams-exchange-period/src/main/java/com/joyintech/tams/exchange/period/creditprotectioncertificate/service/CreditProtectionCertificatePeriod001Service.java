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
import com.joyintech.tams.invest.common.trade.dao.PreTrdCommonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CreditProtectionCertificatePeriod001Service extends CommonExportService<CreditProtectionCertificatePeriod001Response> {

    @Autowired
    private PreTrdCommonMapper preTrdCommonMapper;

    @Autowired
    private CreditProtectionCertificatePeriodMapper creditProtectionCertificatePeriodMapper;

    public Page<CreditProtectionCertificatePeriod001Response> findPage(CreditProtectionCertificatePeriod001Request params) {
        Map<String, Object> paramsMap = BeanUtil.object2Map(params);
        paramsMap.put("FINPROD_TYPE_TG_F37", DictCodeListExt1.金融产品类型_投管用_信用保护凭证.getValue());
        paramsMap.put("YES_OR_NO_N", DictCodeList.是否_否.getValue());

        List<String> finprodIds = StringUtil.splitToList(",", params.getFinprodId());
        List<String> finprodIds2 = new ArrayList();
        finprodIds.forEach(finprodId -> finprodIds2.add(DictCodeListExt1.金融产品类型_投管用_信用保护凭证.getValue() + "_" + finprodId));
        paramsMap.put("finprodIdList", finprodIds2);
        paramsMap.put("busiCodeList", StringUtil.splitToList(",",params.getBusinessCode()));
        paramsMap.put("busiTypeList", params.getBusinessTypeList());
        paramsMap.put("validStatusList", StringUtil.splitToList( ",",params.getValidStatus()));

        return super.queryForPage(params.getPageMode().getPageNo(), params.getPageMode().getPageSize(),
                () -> creditProtectionCertificatePeriodMapper.findPage(paramsMap));
    }

    public Page<SelectOptionDto> findBusiCode(CreditProtectionCertificatePeriod001Request params) {
        Map<String, Object> paramsMap = BeanUtil.object2Map(params);
        paramsMap.put("FINPROD_TYPE_TG_F37", DictCodeListExt1.金融产品类型_投管用_信用保护凭证.getValue());
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

    public boolean isAlreadyRevoked(String busiCode) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("totleTradeId", busiCode);
        paramMap.put("YES_NO_YES", DictCodeList.是否_是.getValue());
        paramMap.put("TRADE_STATUS_03", DictCodeListExt1.交易流程状态_撤销.getValue());
        return preTrdCommonMapper.selectRevokedDataCount(paramMap) > 0;
    }
}
