package com.joyintech.tams.exchange.period.creditprotectioncertificate.dao;

import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.tams.common.base.entity.FinProduct;
import com.joyintech.tams.exchange.period.creditprotectioncertificate.dto.*;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
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

    /**
     * 新头寸 查询新建明细
     * @param params
     * @return
     */
    List<BillingDetailsDto> getBillingDetailsByFinprodMarketId(CreditProtectionCertificatePeriod001Request params);

    /**
     * 老头寸 查询新建明细
     * @param params
     * @return
     */
    List<BillingDetailsDto> getBillingDetailsByFinprodMarketIdOld(CreditProtectionCertificatePeriod001Request params);

    /**
     * 发行人=参考实体的债券
     * @param refEntity
     * @return
     */
    List<String> getRefSecsByRefEntity(@Param("refEntityArray") String[] refEntityArray);

    /**
     * 通过组合代码查询可用数量
     * @param portfolioId
     * @param iplatDate
     * @return
     */
    BigDecimal getAmountByPortfolioId(@Param("finprodMarketId") String finprodMarketId, @Param("portfolioId") String portfolioId, @Param("tradeDate") Date tradeDate);
}
