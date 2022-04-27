package com.joyintech.tams.exchange.basicdata.bussiness.traderinfo.service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.tams.exchange.basicdata.bussiness.traderinfo.dao.TradeInfoProMapper;
import com.joyintech.tams.exchange.basicdata.bussiness.traderinfo.dto.TradeInfoProRequest;
import com.joyintech.tams.exchange.basicdata.bussiness.traderinfo.dto.TradeInfoProResponse;
import com.joyintech.tams.exchange.basicdata.bussiness.traderinfo.dto.TradeInfoProSaveRequest;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class TradeInfoPro001Service extends BaseService implements BaseProcessService<TradeInfoProSaveRequest>{


    @Autowired
    private TradeInfoProMapper tradeInfoProMapper;
    /**
     * 查询交易商名称
     * @param map
     * @return
     */
    public List<SelectOptionDto> getCustomerName(Map<String, Object> map) {
        return tradeInfoProMapper.getCustomerName(map);
    }



    public TradeInfoProResponse getDealerTraderInfo(TradeInfoProRequest dto) {
        return tradeInfoProMapper.getDealerTraderInfo(dto);
    }

    public String findCustomerIdByDealerName(String dealerName) {
        return tradeInfoProMapper.findCustomerIdByDealerName(dealerName);
    }

    public int deleteDealer(TradeInfoProSaveRequest dto) {
        return tradeInfoProMapper.deleteDealer(dto);
    }

    public int hasDealerDetails(String customerId) {
        return tradeInfoProMapper.hasDealerDetails(customerId);
    }

    public int deleteDealerDetails(String customerId) {
        return tradeInfoProMapper.deleteDealerDetails(customerId);
    }

    @Override
    public void doComplete(TradeInfoProSaveRequest tradeInfoProSaveRequest, OperationType operationType) {

    }

    public int deleteTrader(TradeInfoProSaveRequest dto) {
        return tradeInfoProMapper.deleteTrader(dto);
    }

    public int insertDealer(TradeInfoProSaveRequest dto) {
        return tradeInfoProMapper.insertDealer(dto);
    }

    public int insertDealerDetails(TradeInfoProSaveRequest dto) {
        return tradeInfoProMapper.insertDealerDetails(dto);
    }
}
