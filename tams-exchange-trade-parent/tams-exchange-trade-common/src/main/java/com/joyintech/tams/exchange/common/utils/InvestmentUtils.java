package com.joyintech.tams.exchange.common.utils;

import cn.hutool.core.util.StrUtil;
import com.google.common.base.Strings;
import com.joyintech.tams.exchange.common.constant.*;
import com.joyintech.tams.exchange.common.db.domain.Instruction;
import com.joyintech.tams.exchange.common.db.domain.Order;
import com.joyintech.tams.exchange.common.enums.invest.InvestAssetTypeEnum;

public class InvestmentUtils {

    public static boolean hasSameDir(Instruction instruction, Order order) {
        if (InvestAssetTypeEnum.FUTURES.value.equals(instruction.getAssetType())) {
            if (EntrustBs.BUY.value.equals(order.getEntrustBs())) {
                return DerivativeBsDir.BUY.value.equals(instruction.getDerivativeBsDir());
            } else if (EntrustBs.SELL.value.equals(order.getEntrustBs())) {
                return DerivativeBsDir.SELL.value.equals(instruction.getDerivativeBsDir());
            }
            return false;
        } else {
            return instruction.getPs().equals(InvestmentUtils.getInstructionBs(order.getAssetType(), order.getEntrustBs()));
        }
    }

    public static boolean hasSameDir(Instruction instruction, String entrustBs) {
        if (InvestAssetTypeEnum.FUTURES.value.equals(instruction.getAssetType())) {
            if (EntrustBs.BUY.value.equals(entrustBs)) {
                return DerivativeBsDir.BUY.value.equals(instruction.getDerivativeBsDir());
            } else if (EntrustBs.SELL.value.equals(entrustBs)) {
                return DerivativeBsDir.SELL.value.equals(instruction.getDerivativeBsDir());
            }
            return false;
        } else {
            return instruction.getPs().equals(InvestmentUtils.getInstructionBs(instruction.getAssetType(), entrustBs));
        }
    }

    public static String getInstructionBs(String assetType, String entrustBS) {
        if (InvestAssetTypeEnum.PLEDGE_REPURCHASE.value.equals(assetType) && EntrustBs.BUY.value.equals(entrustBS)) {
            return InstructionPs.POSITIVEREPO.value;
        } else if (InvestAssetTypeEnum.PLEDGE_REPURCHASE.value.equals(assetType) && EntrustBs.SELL.value.equals(entrustBS)) {
            return InstructionPs.REVERSEDREPO.value;
        } else if (EntrustBs.BUY.value.equals(entrustBS)) {
            return InstructionPs.BUY.value;
        } else if (EntrustBs.SELL.value.equals(entrustBS)) {
            return InstructionPs.SELL.value;
        }
        return "";
    }

    public static boolean isNormalTradeFromInstruction(String tradePlat) {
        return Strings.isNullOrEmpty(tradePlat) || TradePlat.NORMAL.value.equals(tradePlat);
    }

    /**
     * 根据交易平台和交易方式判断是否支持公平交易
     * <p>
     * 1. 交易平台 = {@link com.joyintech.tams.exchange.common.constant.TradePlat#NORMAL} <br>
     * 2. 交易平台 = {@link com.joyintech.tams.exchange.common.constant.TradePlat#NEW_BOND} && 交易方式 in [{@link com.joyintech.tams.exchange.common.constant.EntrustProp#NORMAL},{@link com.joyintech.tams.exchange.common.constant.EntrustProp#UN_MAKE_SURE}]<br/>
     *
     * @param tradePlat 交易平台
     * @param dealMode  交易方式
     * @return 是否支持公平交易
     */
    public static boolean filterFairInstructionByTradePlatAndDealMode(String tradePlat, String dealMode) {
        if (StrUtil.isEmpty(tradePlat)) {
            return false;
        }

        if (TradePlat.NORMAL.codeEquals(tradePlat)) {
            return true;
        }
        if (TradePlat.NEW_BOND.codeEquals(tradePlat)
                && (StrUtil.isEmpty(dealMode) || EntrustProp.NORMAL.codeEquals(dealMode))) {
            return true;
        }
        return false;
    }

    /**
     * 根据交易方向判断是否支持公平交易
     * <p>
     * 交易方向 in [{@link com.joyintech.tams.exchange.common.constant.InstructionPs#BUY}
     * {@link com.joyintech.tams.exchange.common.constant.InstructionPs#SELL}
     * {@link com.joyintech.tams.exchange.common.constant.InstructionPs#POSITIVEREPO}
     * {@link com.joyintech.tams.exchange.common.constant.InstructionPs#REVERSEDREPO}
     * ] <br>
     *
     * @param ps 交易方向
     * @return 是否支持公平交易
     */
    public static boolean filterFairInstructionByPs(String ps) {
        if (StrUtil.isEmpty(ps)) {
            return false;
        }
        return EnumUtil.in(ps, InstructionPs.BUY, InstructionPs.SELL, InstructionPs.POSITIVEREPO, InstructionPs.REVERSEDREPO);
    }

    // 指令进度判断
    public static boolean isInstructionProcessComplete(Instruction instruction) {
        if (instruction.volumeInstruction()) {
            return TradeUtils.add(instruction.getEntrustLeftAmt(), instruction.getFrozenQuantity(), 0).longValue() == instruction.getEntrustNum();
        } else {
            return TradeUtils.add(instruction.getEntrustLeftAmt(), instruction.getFrozenQuantity(), 2).compareTo(instruction.getEntrustAmt()) == 0;
        }
    }

    public static String convertInvestMarketToTrade(String investTradeMarket) {
        if (Market.SHSE.investValue.equals(investTradeMarket)) {
            return Market.SHSE.value;
        } else if (Market.SZSE.investValue.equals(investTradeMarket)) {
            return Market.SZSE.value;
        } else if (Market.CFFEX.investValue.equals(investTradeMarket)) {
            return Market.CFFEX.value;
        } else if (Market.SHFE.investValue.equals(investTradeMarket)) {
            return Market.SHFE.value;
        } else if (Market.DCE.investValue.equals(investTradeMarket)) {
            return Market.DCE.value;
        } else if (Market.CZCE.investValue.equals(investTradeMarket)) {
            return Market.CZCE.value;
        } else if (Market.INE.investValue.equals(investTradeMarket)) {
            return Market.INE.value;
        }
        return investTradeMarket;
    }
}
