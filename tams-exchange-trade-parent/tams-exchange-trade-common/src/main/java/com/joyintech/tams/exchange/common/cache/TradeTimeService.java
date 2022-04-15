package com.joyintech.tams.exchange.common.cache;

import com.joyintech.tams.exchange.common.bean.dto.OpenCloseTime;
import com.joyintech.tams.exchange.common.bean.investment.TradeTimeBean;
import com.joyintech.tams.exchange.common.bean.request.BaseOrderRequest;
import com.joyintech.tams.exchange.common.db.domain.Instruction;
import com.joyintech.tams.exchange.common.db.domain.TrdPriceTimeConfig;

import java.util.List;

/**
 * @Author: think
 * @Description: 交易时间段
 * @Date: Created in 9:11 2020/11/2
 */
public interface TradeTimeService {

    void saveTradeTimeCache(List<TradeTimeBean> tradeTimeBeanList);

    void saveInCache(List<TrdPriceTimeConfig> trdPriceTimeConfigs);

    List<OpenCloseTime> getTradePeriod(Instruction instruction, BaseOrderRequest order);

    TradeTimeBean getTradeTimeByInstructionAndEntrustProp(Instruction instruction, String assetType1, String assetType2, String entrustProp);
}
