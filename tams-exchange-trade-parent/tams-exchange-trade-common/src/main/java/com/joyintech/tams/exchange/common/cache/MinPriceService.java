package com.joyintech.tams.exchange.common.cache;

import com.joyintech.tams.exchange.common.bean.MinPrice;
import com.joyintech.tams.exchange.common.db.domain.Instruction;
import com.joyintech.tams.exchange.common.db.domain.Instrument;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author: think
 * @Description: 交易时间段
 * @Date: Created in 9:11 2020/11/2
 */
public interface MinPriceService {

    void saveInCache(List<MinPrice> minPrices);

    MinPrice getMinPrice(Instruction instruction, Instrument instrument);

    MinPrice getMinPrice(String tradeMarket, String tradePlat, String assetType, String assetType1);
}
