package com.joyintech.tams.exchange.common.utils;

import com.joyintech.tams.exchange.common.constant.Market;
import com.joyintech.tams.exchange.common.constant.TradePlat;
import com.joyintech.tams.exchange.common.constant.joyin.InvestmentAssetType1;
import com.joyintech.tams.exchange.common.db.domain.Instruction;

import cn.hutool.core.util.ObjectUtil;

public class TradePlatUtils {

	/**
	 *  指令端交易平台-->交易端交易平台
	 */
	public static String etr2TrdTradePlat(Instruction instruction) {
		if (ObjectUtil.isNull(instruction)) {
			return null;
		}

		return etr2TrdTradePlat(instruction.getTradeMarket(), instruction.getTradePlat());
	}

	/**
     * 指令端交易平台--> 交易端交易平台
     */
    public static String etr2TrdTradePlat(String market,String tradePlat) {
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
	public static String trd2EtrTradePlat(Instruction instruction) {
		if (ObjectUtil.isNull(instruction)) {
			return null;
		}

		return trd2EtrTradePlat(instruction.getTradeMarket(), instruction.getTradePlat(),instruction.getAssetType1(),instruction.getBusiTypeTg());
	}
    
    /**
     *  交易端交易平台-->指令端交易平台
     */
	public static String trd2EtrTradePlat(String market,String tradePlat,String assetType1,String busiTypeTg) {
    	if(Market.SZSE.value.equals(market) && TradePlat.SZ_PROTOCOL.value.equals(tradePlat) && !InvestmentAssetType1.NEGOTIATED_REPO.equals(assetType1)) {
    		return TradePlat.ETR_SCALE.value;
    	}
    	if(Market.SHSE.value.equals(market) && TradePlat.SH_FIXED_INCOME.value.equals(tradePlat) && !InvestmentAssetType1.NEGOTIATED_REPO.equals(assetType1)) {
    		return TradePlat.ETR_FIXED_INCOME.value;
    	}    
    	if(TradePlat.NORMAL.value.equals(tradePlat)) {
    		if(EntrustUtils.isNotTrade(busiTypeTg)) {
    			return TradePlat.ETR_OTHER.value;
    		}
    	}
        return tradePlat;
    }
}
