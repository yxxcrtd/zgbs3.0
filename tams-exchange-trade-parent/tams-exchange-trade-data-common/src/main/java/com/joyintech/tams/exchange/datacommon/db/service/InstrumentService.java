package com.joyintech.tams.exchange.datacommon.db.service;

import com.joyintech.tams.exchange.common.bean.MinPrice;
import com.joyintech.tams.exchange.common.bean.MinTrdUnitLimit;
import com.joyintech.tams.exchange.common.bean.instrument.MinPriceAndMinTrdUnitLimitBean;
import com.joyintech.tams.exchange.common.db.domain.Instrument;
import com.joyintech.tams.exchange.datacommon.cache.AsyncService;

import java.util.List;

public interface InstrumentService extends AsyncService<String, Instrument> {
    Instrument queryByInnerCode(String innerCode);

    List<Instrument> queryByMarket(String market);

    void saveMinPrice(List<MinPrice> minPriceList);

    void saveTrdUnitLimit(List<MinTrdUnitLimit> minTrdUnitLimitList);

    MinPrice getMinPrice(String tradePlat, String market, Instrument instrument);

    MinTrdUnitLimit getMinTrdUnitLimit(String tradePlat, String market, Instrument instrument, String instructionPs);

    MinPriceAndMinTrdUnitLimitBean queryMinPriceAndMinTrdUnitLimit(String stockCode, String tradePlat, String instructionPs);

    List<Instrument> queryByAssetType(String assetType);


    void trdUnitLimitFlush();

}
