package com.joyintech.tams.exchange.common.utils;

import com.google.common.base.Strings;
import com.joyintech.tams.exchange.common.constant.*;
import com.joyintech.tams.exchange.common.db.domain.Instruction;
import com.joyintech.tams.exchange.common.db.domain.Order;

public class InvestmentUtils {

    public static boolean hasSameDir(Instruction instruction, Order order) {
        if (InvestmentAssetType.FUTURES.value.equals(instruction.getAssetType())) {
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
        if (InvestmentAssetType.FUTURES.value.equals(instruction.getAssetType())) {
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
        if (InvestmentAssetType.PLEDGE_REPURCHASE.value.equals(assetType) && EntrustBs.BUY.value.equals(entrustBS)) {
            return InstructionPs.POSITIVEREPO.value;
        } else if (InvestmentAssetType.PLEDGE_REPURCHASE.value.equals(assetType) && EntrustBs.SELL.value.equals(entrustBS)) {
            return InstructionPs.REVERSEDREPO.value;
        } else if (EntrustBs.BUY.value.equals(entrustBS)) {
            return InstructionPs.BUY.value;
        } else if (EntrustBs.SELL.value.equals(entrustBS)) {
            return InstructionPs.SELL.value;
        }
        return "";
    }

    public static boolean isNormalTradeFromInstruction(String tradePlat) {
        if (Strings.isNullOrEmpty(tradePlat) || TradePlat.NORMAL.value.equals(tradePlat)) return true;
        else return false;
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
