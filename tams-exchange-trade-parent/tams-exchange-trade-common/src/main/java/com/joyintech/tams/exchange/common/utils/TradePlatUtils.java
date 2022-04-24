package com.joyintech.tams.exchange.common.utils;

import com.google.common.base.Strings;
import com.joyintech.tams.exchange.common.constant.Market;
import com.joyintech.tams.exchange.common.constant.TradePlat;

public class TradePlatUtils {

	/**
     * 指令端交易平台--> 交易端交易平台
     */
    public static String etr2TrdTradePlat(String market,String tradePlat) {
    	if (Strings.isNullOrEmpty(market)
                || Strings.isNullOrEmpty(tradePlat)){
    		return tradePlat;
    	}
    	if(Market.SZSE.value.equals(market) && TradePlat.ETR_SCALE.value.equals(tradePlat)) {
    		return TradePlat.SZ_PROTOCOL.value;
    	}
    	if(Market.SHSE.value.equals(market) && TradePlat.ETR_PROTOCOL.value.equals(tradePlat)) {
    		return TradePlat.SH_FIXED_INCOME.value;
    	}    
    	if(TradePlat.ETR_OTHER.value.equals(tradePlat)) {
    		return TradePlat.NORMAL.value;
    	}
        return tradePlat;
    }
    
    /**
     *  交易端交易平台-->指令端交易平台
     */
    public static String trd2EtrTradePlat(String market,String tradePlat) {
    	if (Strings.isNullOrEmpty(market)
                || Strings.isNullOrEmpty(tradePlat)){
    		return tradePlat;
    	}
    	if(Market.SZSE.value.equals(market) && TradePlat.SZ_PROTOCOL.value.equals(tradePlat)) {
    		return TradePlat.ETR_SCALE.value;
    	}
    	if(Market.SHSE.value.equals(market) && TradePlat.SH_FIXED_INCOME.value.equals(tradePlat)) {
    		return TradePlat.ETR_PROTOCOL.value;
    	}    
    	if(TradePlat.NORMAL.value.equals(tradePlat)) {
    		//TODO 添加非交易判断条件
    		return TradePlat.ETR_OTHER.value;
    	}
        return tradePlat;
    }
}
