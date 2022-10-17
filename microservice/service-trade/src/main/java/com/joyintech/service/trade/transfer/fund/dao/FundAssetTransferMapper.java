package com.joyintech.service.trade.transfer.fund.dao;

import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.service.trade.transfer.common.dto.BondValuationDto;
import com.joyintech.service.trade.transfer.fund.dto.FundAssetTransfer001Dto;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Repository
public interface FundAssetTransferMapper {

    List<SelectOptionDto> selectPortfolioId(Map<String, Object> param);

    BigDecimal findInitCarryOverQty(Map<String, Object> param);

    List<BigDecimal> findUnitCprice(Map<String, Object> param);

    String selProdCode(Map<String, Object> param);

    BigDecimal selCloPrice(Map<String, Object> param);

    int updateRep(Map<String, Object> param);

    List<BondValuationDto> findValuation(Map<String, Object> param);
}
