package com.joyintech.tams.exchange.basicdata.bussiness.traderinfo.service;

import com.joyintech.background.component.common.pack.common.grid.Page;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.business.common.dto.PageMode;
import com.joyintech.tams.exchange.basicdata.bussiness.traderinfo.dao.TradeInfoProMapper;
import com.joyintech.tams.exchange.basicdata.bussiness.traderinfo.dto.TradeInfoProRequest;
import com.joyintech.tams.exchange.basicdata.bussiness.traderinfo.dto.TradeInfoProResponse;
import com.joyintech.tams.exchange.basicdata.bussiness.traderinfo.dto.TradeInfoProSaveRequest;
import com.joyintech.tams.exchange.basicdata.bussiness.traderinfo.dto.TraderInfoDto;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class TradeInfoPro001Service extends BaseService implements BaseProcessService<TradeInfoProSaveRequest>{


    @Autowired
    private TradeInfoProMapper tradeInfoProMapper;
    /**
     * 查询交易商名称
     * @param
     * @return
     */
    public List<SelectOptionDto> getCustomerName() {
        return tradeInfoProMapper.getCustomerName();
    }



    public Page<TradeInfoProResponse> getDealerTraderInfo(TradeInfoProRequest dto) {
        PageMode pageMode = null;
        if (StringUtil.isNotEmpty(dto.getPageMode())){
            pageMode = dto.getPageMode();
            PageMode pageMode2 = new PageMode();
            pageMode2.setPageNo(pageMode.getPageNo());
            pageMode2.setPageSize(pageMode.getPageSize());
            pageMode2.setSort("");
            pageMode2.setDirection("");
            dto.setPageMode(pageMode2);
        }
        Map<String, Object> param = BeanUtil.object2Map(dto);
        Page<TradeInfoProResponse> result = new Page<>();
        //用来装填要发送给前端的数据
        List<TradeInfoProResponse> queryList = tradeInfoProMapper.getDealerTraderInfo(param);
        //用来装填一级信息里面对应的所有二级信息，二级信息用对象装填
//        List<TraderInfoDto> childList = new ArrayList<>();
        List<TraderInfoDto> childList = null;
        TraderInfoDto traderInfoDto = null;

        for (TradeInfoProResponse tradeInfoPro:queryList){
              //根据一级查二级
//              List<TraderInfoDto> dto1 = tradeInfoProMapper.findTrader(tradeInfoProResponse);
            childList = tradeInfoProMapper.findTrader(tradeInfoPro);
//              tradeInfoProResponseList.add(tradeInfoProResponse1);

              if (childList!= null){
                  tradeInfoPro.setTraderInfoDtoList(childList);
              }
        }
        result.setRoot(queryList);
        return result;
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

    public int deleteDealerDetails(TradeInfoProSaveRequest dto) {
        return tradeInfoProMapper.deleteDealerDetails(dto);
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

    public int updateByCustomerId(TradeInfoProSaveRequest dto) {
        return tradeInfoProMapper.updateByCustomerId(dto);
    }

    public int existsDealer(TradeInfoProSaveRequest dto) {
        return tradeInfoProMapper.existsDealer(dto);
    }

    public int existsDealerAndTradeMarket(TradeInfoProSaveRequest dto) {
        return tradeInfoProMapper.existsDealerAndTradeMarket(dto);
    }

    public int updateDetails(TradeInfoProSaveRequest dto) {
        return tradeInfoProMapper.updateDetails(dto);
    }

    public int updateDetailsSJ(TradeInfoProSaveRequest dto) {
        return tradeInfoProMapper.updateDetailsSJ(dto);
    }

    public int deleteTraderSJ(TradeInfoProSaveRequest dto) {
        return tradeInfoProMapper.deleteTraderSJ(dto);
    }

    public int existsTrader(TradeInfoProSaveRequest dto) {
        return tradeInfoProMapper.existsTrader(dto);
    }

    public int existsOur(TradeInfoProSaveRequest dto) {
        return tradeInfoProMapper.existsOur(dto);
    }

    public int existsDealerParty(TradeInfoProSaveRequest dto) {
        return tradeInfoProMapper.existsDealerParty(dto);
    }

    public int hasDealerParty(TradeInfoProSaveRequest dto) {
        return tradeInfoProMapper.hasDealerParty(dto);
    }

    public int hasSameTrader(TradeInfoProSaveRequest dto) {
        return tradeInfoProMapper.hasSameTrader(dto);
    }
    public String hasSameDealerPartyNoDiffType(TradeInfoProSaveRequest dto) {
        return tradeInfoProMapper.hasSameDealerPartyNoDiffType(dto);
    }
    public String hasSameDealerPartyNoTraderNo(TradeInfoProSaveRequest dto) {
        return tradeInfoProMapper.hasSameDealerPartyNoTraderNo(dto);
    }
    public String hasSameTraderSeqNo(TradeInfoProSaveRequest dto) {
        return tradeInfoProMapper.hasSameTraderSeqNo(dto);
    }
    public String existsTraderSingle(TradeInfoProSaveRequest dto) {
        return tradeInfoProMapper.existsTraderSingle(dto);
    }
}
