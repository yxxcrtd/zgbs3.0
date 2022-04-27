package com.joyintech.tams.exchange.common.db.domain;

import java.math.BigDecimal;

public interface InstrumentInterface {
     String getMarket();

     String getStockCode();

     String getStockName();

     int getBuyUnit();

     BigDecimal getPriceStep();

     Long getHighVolume();

     Integer getLowVolume();

     String getAssetType();


     BigDecimal getLowBalance();

     BigDecimal getHighBalance();


     Integer getMarketLowVolume();

     Long getMarketHighVolume();
}
