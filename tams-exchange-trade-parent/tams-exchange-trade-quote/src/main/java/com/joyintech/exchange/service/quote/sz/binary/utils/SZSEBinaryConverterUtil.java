package com.joyintech.exchange.service.quote.sz.binary.utils;

import com.alibaba.fastjson.JSON;
import com.joyintech.exchange.service.quote.sz.binary.bean.SZSEBinaryLevel1MarketSnapshot;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.exchange.common.bean.TickBean;
import com.joyintech.tams.exchange.common.constant.Market;
import com.joyintech.tams.exchange.common.utils.StockCodeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SZSEBinaryConverterUtil {

    private static final Logger logger = LoggerFactory.getLogger(SZSEBinaryConverterUtil.class);

    public static TickBean convertTick(String dataStr) {
        TickBean tick = new TickBean();
        if (StringUtil.isEmpty(dataStr)) {
            return null;
        }
        SZSEBinaryLevel1MarketSnapshot data = JSON.parseObject(dataStr, SZSEBinaryLevel1MarketSnapshot.class);
        tick.setExchange(Market.SZSE.value);
        String stockCode = StockCodeUtils.formatStockCode(data.getSecurityId(), Market.SZSE.value);
        tick.setInstrumentID(stockCode);
        tick.setLastPrice(data.getLastPx());
        tick.setOpenPrice(data.getOpenPx());
        tick.setHighPrice(data.getHighPx());
        tick.setLowPrice(data.getLowPx());
        tick.setVolume(data.getTotalVolumeTrade());
        tick.setTurnover(0); //nsq 没有换手率
        tick.setTradeBalance(data.getTotalValueTrade());
        tick.setUpperLimitPrice(data.getHighLimitPx());
        tick.setLowerLimitPrice(data.getLowLimitPx());
        tick.setPreClosePrice(data.getPrevClosePx());
        tick.setClosePrice(data.getClosePrice());// 收盘价
        tick.setAverageDailyPrice(data.getAverageDailyPrice());// 日均价
        tick.setSettlePrice(data.getClearingPrice());
        for (int i = 0; i < data.getOfferPrices().size(); i++) {
            tick.addAskPrice(data.getOfferPrices().get(i));
        }
        for (int i = 0; i < data.getOfferVolumes().size(); i++) {
            tick.addAskVolume(data.getOfferVolumes().get(i).intValue());
        }
        for (int i = 0; i < data.getBidPrices().size(); i++) {
            tick.addBidPrice(data.getBidPrices().get(i));
        }
        for (int i = 0; i < data.getBidVolumes().size(); i++) {
            tick.addBidVolume(data.getBidVolumes().get(i).intValue());
        }
        tick.setUpdateTime(data.getOrigTime());
        return tick;
    }
}
