package com.joyintech.tams.exchange.basicdata.bussiness.traderinfo.dao;

import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.tams.exchange.basicdata.bussiness.traderinfo.dto.TradeInfoProRequest;
import com.joyintech.tams.exchange.basicdata.bussiness.traderinfo.dto.TradeInfoProResponse;
import com.joyintech.tams.exchange.basicdata.bussiness.traderinfo.dto.TradeInfoProSaveRequest;
import com.joyintech.tams.exchange.basicdata.bussiness.traderinfo.dto.TraderInfoDto;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface TradeInfoProMapper {

    /**
     * 查询交易商名称
     * @param
     * @return
     */
    List<SelectOptionDto> getCustomerName();

    int deleteSelectedItem(TradeInfoProSaveRequest dto);

    List<TradeInfoProResponse> getDealerTraderInfo(Map<String, Object> pageParam);

    String findCustomerIdByDealerName(String dealerName);

    int deleteDealer(TradeInfoProSaveRequest dto);

    int hasDealerDetails(String customerId);

    int deleteDealerDetails(TradeInfoProSaveRequest dto);

    int deleteTrader(TradeInfoProSaveRequest dto);

    int insertDealer(TradeInfoProSaveRequest dto);

    int insertDealerDetails(TradeInfoProSaveRequest dto);

    int updateByCustomerId(TradeInfoProSaveRequest dto);

    int existsDealer(TradeInfoProSaveRequest dto);

    int existsDealerAndTradeMarket(TradeInfoProSaveRequest dto);

    List<TraderInfoDto> findTrader(TradeInfoProResponse tradeInfoProResponse);

    int updateDetails(TradeInfoProSaveRequest dto);

    int updateDetailsSJ(TradeInfoProSaveRequest dto);

    int deleteTraderSJ(TradeInfoProSaveRequest dto);

    int existsTrader(TradeInfoProSaveRequest dto);

    int existsOur(TradeInfoProSaveRequest dto);
}
