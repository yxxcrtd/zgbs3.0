package com.joyintech.tams.invest.batch.creditprotectioncertificate.dao;

import com.joyintech.tams.invest.batch.creditprotectioncertificate.dto.CreditProtectionCertificatePeriodExpireDataDto;
import com.joyintech.tams.invest.batch.creditprotectioncertificate.dto.CreditProtectionCertificatePeriodReaderDto;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 〈功能详细描述〉
 *
 * @author yangxunpeng
 * @version 1.0 2022年05月25日
 * @since 1.0
 */
@Repository
public interface CreditProtectionCertificatePeriodRunnerMapper {
    List<CreditProtectionCertificatePeriodReaderDto> selectRevokedList(Map<String, Object> params);

    List<CreditProtectionCertificatePeriodReaderDto> selectExpireFinprodIdForOld(@Param("executeDate") Date executeDate);

    List<CreditProtectionCertificatePeriodReaderDto> selectExpireFinprodIdForNew(@Param("executeDate") Date executeDate);

    List<CreditProtectionCertificatePeriodExpireDataDto> selectExpireDataForOld(@Param("executeDate") Date executeDate, @Param("finprodId") String finprodId);

    List<CreditProtectionCertificatePeriodExpireDataDto> selectExpireDataForNew(@Param("executeDate") Date executeDate, @Param("finprodId") String finprodId);

    List<CreditProtectionCertificatePeriodExpireDataDto> selectDetailsByTotleTradeId(@Param("busiCode") String busiCode);
}
