package com.joyintech.tams.component.business.riskall.dao;

import com.joyintech.tams.component.business.riskall.dto.RiskAuthorizeDto;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface RiskCommonMapper {

    List<RiskAuthorizeDto> findRiskAuthorize(Map<String, Object> params);
}
