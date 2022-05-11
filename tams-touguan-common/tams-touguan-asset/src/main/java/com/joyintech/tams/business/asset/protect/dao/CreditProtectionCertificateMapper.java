package com.joyintech.tams.business.asset.protect.dao;

import com.joyintech.tams.business.asset.protect.dto.CreditProtectionCertificateDto;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author wangshengzhi
 * @version 1.0 2022年5月9日
 * @see CreditProtectionCertificateMapper
 * @since 1.0
 */
@Repository
public interface CreditProtectionCertificateMapper {

    List<CreditProtectionCertificateDto> queryForPage(Map<String, Object> pageParam);
}
