package com.joyintech.service.trade.transfer.common.dao;

import com.joyintech.service.trade.transfer.common.dto.PortfolioValuationDto;
import com.joyintech.service.trade.transfer.common.dto.TransferCommonDto;
import com.joyintech.tams.common.base.entity.WflTrdProductDeal;
import com.joyintech.tams.invest.common.dto.PtlCashPositionFlowDto;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Repository
public interface TransferCommonMapper {

    List<WflTrdProductDeal> selectDealByworkflowId(String workflowId);

    List<TransferCommonDto> findAvaPortfolioInfo(Map<String, Object> paramMap);

    List<PortfolioValuationDto> getPtlPortfolioValuation(Map<String, Object> paramMap);

    BigDecimal findCarryOverQty(Map<String, Object> paramMap);

    BigDecimal queryUsableCashPosition(PtlCashPositionFlowDto pcpdto);
}
