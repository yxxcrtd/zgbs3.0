package com.joyintech.tams.exchange.common.cache;

import com.joyintech.tams.exchange.common.bean.MinTrdUnitLimit;
import com.joyintech.tams.exchange.common.bean.request.BaseOrderRequest;
import com.joyintech.tams.exchange.common.db.domain.Instruction;
import com.joyintech.tams.exchange.common.db.domain.Instrument;

import java.util.List;

/**
 * @Author: think
 * @Description: 交易时间段
 * @Date: Created in 9:11 2020/11/2
 */
public interface MinTrdUnitLimitService {

    void saveInCache(List<MinTrdUnitLimit> minTrdUnitLimits);

    MinTrdUnitLimit getMinTrdUnitLimit(Instruction instruction, Instrument instrument);

    MinTrdUnitLimit getMinTrdUnitLimit(String tradeMarket, String tradePlat, String  assetType, String assetType1, String assetType2, String ps);
}
