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

    /**
     * 工作流（留痕操作结束的时候执行的方法）
     */
    @Override
    public void doComplete(TradeInfoProSaveRequest entity, OperationType type){
        if (OperationType.DELETE.equals(type)) {
            tradeInfoProMapper.deleteSelectedItem(entity);
        }
    }

    public TradeInfoProResponse getDealerTraderInfo(TradeInfoProRequest dto) {
        return tradeInfoProMapper.getDealerTraderInfo(dto);
    }
//    public int deleteSelectedItem(TradeInfoProSaveRequest dto) {
//        return tradeInfoProMapper.deleteSelectedItem(dto);
//    }
}
