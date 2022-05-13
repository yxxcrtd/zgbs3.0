package com.joyintech.tams.business.asset.protect.dao;

import com.joyintech.tams.business.asset.protect.dto.CreditProtectionCertificate002Dto;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author wangshengzhi
 * @version 1.0 2022年5月12日
 * @see InterfCreditInfoMapper
 * @since 1.0
 */
@Repository
public interface InterfCreditInfoMapper {

    CreditProtectionCertificate002Dto getFromInterface(Map<String, String> pageParam);

}
