package com.joyintech.tams.exchange.period.creditprotectioncertificate.dao;

import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.tams.common.base.entity.FinProduct;
import com.joyintech.tams.exchange.period.creditprotectioncertificate.dto.*;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 〈功能详细描述〉
 *
 * @author yangxunpeng
 * @version 1.0 2022年05月16日
 * @since 1.0
 */
@Repository
public interface CreditProtectionCertificatePeriodMapper {

    List<CreditProtectionCertificatePeriod001Response> findPage(Map<String,Object> params);

    List<SelectOptionDto> findBusiCode(Map<String, Object> paramsMap);

    List<SelectOptionDto> findFinprodId(Map<String, Object> paramsMap);

    FinprodLinkDto findFinprodDetail(CreditProtectionCertificatePeriod001Request params);

    CreditProtectionCertificatePeriodSaveDto getEntityByBusinessCode(@Param("businessCode") String businessCode);

    List<BillingDetailsDto> getBillingDetailByBusinessCode(@Param("businessCode") String businessCode);
}
