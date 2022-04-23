package com.joyintech.tams.exchange.basicdata.bussiness.traderinfo.dao;

import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.tams.exchange.basicdata.bussiness.traderinfo.dto.TradeInfoProRequest;
import com.joyintech.tams.exchange.basicdata.bussiness.traderinfo.dto.TradeInfoProResponse;
import com.joyintech.tams.exchange.basicdata.bussiness.traderinfo.dto.TradeInfoProSaveRequest;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface TradeInfoProMapper {

    /**
     * 查询交易商名称
     * @param map
     * @return
     */
    List<SelectOptionDto> getCustomerName(Map<String, Object> map);

    int deleteSelectedItem(TradeInfoProSaveRequest dto);

    TradeInfoProResponse getDealerTraderInfo(TradeInfoProRequest dto);

    String findCustomerIdByDealerName(String dealerName);

    int deleteDealer(TradeInfoProSaveRequest dto);

    int hasDealerDetails(String customerId);

    int deleteDealerDetails(String customerId);

    int deleteTrader(TradeInfoProSaveRequest dto);

    int insertDealer(TradeInfoProSaveRequest dto);

    int insertDealerDetails(TradeInfoProSaveRequest dto);
}
