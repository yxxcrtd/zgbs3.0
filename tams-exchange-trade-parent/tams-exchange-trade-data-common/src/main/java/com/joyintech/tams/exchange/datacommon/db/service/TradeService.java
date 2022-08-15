package com.joyintech.tams.exchange.datacommon.db.service;

import com.github.pagehelper.PageInfo;
import com.joyintech.tams.exchange.common.bean.TradeQueryBean;
import com.joyintech.tams.exchange.common.bean.TradeQueryDto;
import com.joyintech.tams.exchange.common.db.domain.Trade;
import com.joyintech.tams.exchange.common.request.PageTradeInfoRequest;
import com.joyintech.tams.exchange.datacommon.cache.AsyncService;

import java.util.List;

public interface TradeService extends AsyncService<String, Trade> {
    @Deprecated
    List<Trade> queryTradeByEntrust(String entrustId);

    List<TradeQueryBean> queryTradeByEntrustId(String entrustId);

    List<Trade> queryTradeByOrder(String orderId);

    List<Trade> queryTradeByInstructionId(String instructionId);

    List<Trade> queryTradeByAccountId(String accountId);

    Trade queryTradeByAccountIdAndBrokerTradeId(String accountId, String key);

    String genDBId();

    @Deprecated
    PageInfo<Trade> queryAllForPage(int pageNum, int pageSize, String userId, String accountId, String market, String stockCode, String entrustProp, List<String> ptlAuthList);

    PageInfo<TradeQueryBean> queryAllForPage(PageTradeInfoRequest request, List<String> ptlAuthList);

    void clearSet(String date);

//    void fillPlatDate(String date);

    /**
     * 投管用成交查询
     *
     * @param instructionId 指令编号
     * @return 成交信息
     */
    List<TradeQueryDto> queryTrade2Touguan(String instructionId);
}
