package com.joyintech.tams.exchange.common.utils;

import com.joyintech.framework.util.DateUtil;
import com.joyintech.tams.exchange.common.constant.EntrustBs;
import com.joyintech.tams.exchange.common.constant.EntrustProp;
import com.joyintech.tams.exchange.common.enums.invest.InvestAssetTypeEnum;
import com.joyintech.tams.exchange.common.db.domain.FuturesInstrument;
import com.joyintech.tams.exchange.common.db.domain.IntegratedEntrust;
import com.joyintech.tams.exchange.common.db.domain.OrderInterface;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Objects;

public class TradeUtils {
    
    private TradeUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static String formatDate(String dateStr) {
        Date date = DateUtil.parse(dateStr);
        return DateUtil.format(date, "yyyy-MM-dd");
    }

    public static BigDecimal getBigDecimal(Double d, int scale) {
        return BigDecimal.valueOf(d).setScale(scale, RoundingMode.HALF_UP);
    }

    public static double formatDouble(Double d, int scale) {
        return BigDecimal.valueOf(d).setScale(scale, RoundingMode.HALF_UP).doubleValue();
    }

    public static double formatDouble(Double d, int scale, RoundingMode roundingMode) {
        return BigDecimal.valueOf(d).setScale(scale, roundingMode).doubleValue();
    }

    public static Long formatLong(Double d, int scale, RoundingMode roundingMode) {
        return BigDecimal.valueOf(d).setScale(scale, roundingMode).longValue();
    }

    public static BigDecimal add(BigDecimal d, BigDecimal decimal, int scale) {
        return d.add(decimal).setScale(scale, RoundingMode.HALF_UP);
    }

    public static BigDecimal add(BigDecimal d, long volume, int scale) {
        return d.add(BigDecimal.valueOf(volume)).setScale(scale, RoundingMode.HALF_UP);
    }

    public static BigDecimal add(BigDecimal d, BigDecimal decimal, BigDecimal bigDecimal, int scale) {
        return d.add(decimal).add(bigDecimal).setScale(scale, RoundingMode.HALF_UP);
    }

    public static BigDecimal add(Double d, BigDecimal decimal, int scale) {
        return decimal.add(BigDecimal.valueOf(d)).setScale(scale, RoundingMode.HALF_UP);
    }

    public static BigDecimal multiply(BigDecimal d, long num, int scale) {
        return d.multiply(BigDecimal.valueOf(num)).setScale(scale, RoundingMode.HALF_UP);
    }

    public static BigDecimal multiply(BigDecimal d, BigDecimal num, int scale) {
        return d.multiply(num).setScale(scale, RoundingMode.HALF_UP);
    }

    public static BigDecimal divide(BigDecimal d, long num, int scale) {
        return d.divide(BigDecimal.valueOf(num), scale, RoundingMode.HALF_UP);
    }

    public static Double divide(BigDecimal d, BigDecimal num, int scale) {
        return d.divide(num, scale, RoundingMode.DOWN).doubleValue();
    }

    public static BigDecimal divideAndRemainder(BigDecimal d, BigDecimal num) {
        return d.divideAndRemainder(num)[1];
    }

    public static BigDecimal subtract(BigDecimal d, long num, int scale) {
        return subtract(d, BigDecimal.valueOf(num), scale);
    }

    public static BigDecimal subtract(BigDecimal d, BigDecimal num, int round) {
        return d.subtract(num).setScale(round, RoundingMode.HALF_UP);
    }

    public static BigDecimal subtract(BigDecimal d, BigDecimal num, BigDecimal decimal, int round) {
        return d.subtract(num).subtract(decimal).setScale(round, RoundingMode.HALF_UP);
    }

    public static boolean equals(BigDecimal d, BigDecimal decimal) {
        return d.compareTo(decimal) == 0;
    }

    public static boolean bigThan(BigDecimal d, BigDecimal decimal) {
        if (Objects.nonNull(d) && Objects.nonNull(decimal)) {
            return d.compareTo(decimal) > 0;
        }
        return false;
    }

    public static boolean bigThanZero(BigDecimal value) {
        return bigThan(value, BigDecimal.ZERO);
    }

    public static boolean isZero(BigDecimal value) {
        if (Objects.isNull(value)) {
            return true;
        }
        return BigDecimal.ZERO.compareTo(value) == 0;
    }

    public static boolean lessThan(BigDecimal d, BigDecimal decimal) {
        if (Objects.nonNull(d) && Objects.nonNull(decimal)) {
            return d.compareTo(decimal) < 0;
        }
        return false;
    }

    public static boolean lessThanZero(BigDecimal value) {
        return lessThan(value, BigDecimal.ZERO);
    }

    public static BigDecimal subtractMod(BigDecimal value, BigDecimal dividend) {
        if (Objects.isNull(value) || Objects.isNull(dividend)) {
            return BigDecimal.ZERO;
        }

        return value.subtract(value.remainder(dividend));
    }

    // 卖出冻结数量
    public static boolean isFrozenVolume(String entrustProp, String entrustBs, String investmentAssetType) {
        // 意向申报，不需要冻结
        if (EntrustProp.INTENTION.value.equals(entrustProp))
            return false;
            // 协议回购首期报价申报，正回购 冻结数量
        else if (EntrustProp.SH_REPURCHASE_TRADE.value.equals(entrustProp))
            return true;
        else if (EntrustProp.SH_REPURCHASE_TRADE_CONFIRM.value.equals(entrustProp))
            return false;
        else if (EntrustProp.SZ_REPURCHASE_BEGIN.value.equals(entrustProp) && EntrustBs.BUY.value.equals(entrustBs))
            return true;
        else if (EntrustProp.SZ_REPURCHASE_BEGIN.value.equals(entrustProp) && EntrustBs.SELL.value.equals(entrustBs))
            return false;
            // 质押式竞价回购，正回购 冻结数量
        else if (InvestAssetTypeEnum.PLEDGE_REPURCHASE.value.equals(investmentAssetType) && EntrustBs.BUY.value.equals(entrustBs))
            return true;
        else if (InvestAssetTypeEnum.PLEDGE_REPURCHASE.value.equals(investmentAssetType) && EntrustBs.SELL.value.equals(entrustBs))
            return false;
        else
            return EntrustBs.SELL.value.equals(entrustBs);
    }

    // 买入冻结金额
    public static boolean isFrozenBalance(String entrustProp, String entrustBs, String investmentAssetType) {
        // 意向申报，不需要冻结
        if (EntrustProp.INTENTION.value.equals(entrustProp))
            return false;
            // 协议回购首期成交确认，逆回购 冻结金额
        else if (EntrustProp.SH_REPURCHASE_TRADE_CONFIRM.value.equals(entrustProp))
            return true;
        else if (EntrustProp.SH_REPURCHASE_TRADE.value.equals(entrustProp))
            return false;
        else if (EntrustProp.SZ_REPURCHASE_BEGIN.value.equals(entrustProp) && EntrustBs.SELL.value.equals(entrustBs))
            return true;
        else if (EntrustProp.SZ_REPURCHASE_BEGIN.value.equals(entrustProp) && EntrustBs.BUY.value.equals(entrustBs))
            return false;
            // 质押式竞价回购，逆回购 冻结金额
        else if (InvestAssetTypeEnum.PLEDGE_REPURCHASE.value.equals(investmentAssetType) && EntrustBs.SELL.value.equals(entrustBs))
            return true;
        else if (InvestAssetTypeEnum.PLEDGE_REPURCHASE.value.equals(investmentAssetType) && EntrustBs.BUY.value.equals(entrustBs))
            return false;
        else
            return EntrustBs.BUY.value.equals(entrustBs);
    }

    public static BigDecimal calculateBalance(BigDecimal entrustPrice, long entrustVolume, String assetType) {
        return calculateBalance(null, entrustPrice, entrustVolume, assetType, null, null);
    }

    public static BigDecimal calculateBalance(BigDecimal entrustPrice, long entrustVolume, String assetType, OrderInterface orderInterface) {
        return calculateBalance(null, entrustPrice, entrustVolume, assetType, orderInterface, null);
    }

    public static BigDecimal calculateBalance(BigDecimal entrustPrice, long entrustVolume, String assetType, OrderInterface orderInterface, FuturesInstrument futuresInstrument) {
        return calculateBalance(null, entrustPrice, entrustVolume, assetType, orderInterface, futuresInstrument);
    }

    public static BigDecimal calculateBalance(String entrustBs, BigDecimal entrustPrice, long entrustVolume, String assetType, OrderInterface orderInterface) {
        return calculateBalance(entrustBs, entrustPrice, entrustVolume, assetType, orderInterface, null);
        /*//协议回购金额计算
        if (Objects.nonNull(orderInterface) && IntegratedEntrustUtils.isProtocolBack(orderInterface.getEntrustProp())) {
            return ((IntegratedEntrust) orderInterface).getEntrustBalance();
        }
        // 债券按净价冻结 --- 需求新修正为净价 --- > 修正为全价
        // 债券且是交易类
        if (InvestAssetTypeEnum.BOND.value.equals(assetType)
                && Objects.nonNull(orderInterface)
                && EntrustProp.isTrade(orderInterface.getEntrustProp())) {
            return multiply(orderInterface.getFullPrice(), entrustVolume, 2);
        }
        // 质押式竞价回购，金额计算
        if (InvestAssetTypeEnum.PLEDGE_REPURCHASE.value.equals(assetType)) {
            return new BigDecimal(entrustVolume * 100).setScale(2, RoundingMode.HALF_UP);
        }
        // TODO ETF计算
        return multiply(entrustPrice, entrustVolume, 2);*/
    }

    public static BigDecimal calculateBalance(String entrustBs, BigDecimal entrustPrice, long entrustVolume, String assetType, OrderInterface orderInterface, FuturesInstrument futuresInstrument) {
        //协议回购金额计算
        if (Objects.nonNull(orderInterface) && IntegratedEntrustUtils.isProtocolBack(orderInterface.getEntrustProp())) {
            return ((IntegratedEntrust) orderInterface).getEntrustBalance();
        }
        // 债券按净价冻结 --- 需求新修正为净价 --- > 修正为全价
        // 债券且是交易类
        if (InvestAssetTypeEnum.BOND.value.equals(assetType)
                && Objects.nonNull(orderInterface)
                && EntrustProp.isTrade(orderInterface.getEntrustProp())) {
            return multiply(orderInterface.getFullPrice(), entrustVolume, 2);
        }
        // 质押式竞价回购，金额计算
        if (InvestAssetTypeEnum.PLEDGE_REPURCHASE.value.equals(assetType)) {
            return new BigDecimal(entrustVolume * 100).setScale(2, RoundingMode.HALF_UP);
        }
        if (InvestAssetTypeEnum.FUTURES.value.equals(assetType)) {
            BigDecimal res = multiply(entrustPrice, entrustVolume, 2);
            return multiply(res, BigDecimal.valueOf(Objects.nonNull(futuresInstrument) ? futuresInstrument.getVolumeMultiple() : 1), 2);
        }
        // TODO ETF计算
        return multiply(entrustPrice, entrustVolume, 2);
    }

    //投管风控获取全价总额以及净价总额
    public static BigDecimal calculateBalanceForRisk(String entrustBs, BigDecimal entrustPrice, long entrustVolume, String assetType) {
        // 质押式竞价回购，金额计算
        if (InvestAssetTypeEnum.PLEDGE_REPURCHASE.value.equals(assetType)) {
            return new BigDecimal(entrustVolume * 100).setScale(2, RoundingMode.HALF_UP);
        }
        return multiply(entrustPrice, entrustVolume, 2);
    }

    /**
     * 转为百分比格式
     */
    public static String percentFormat(BigDecimal d) {
        NumberFormat percent = NumberFormat.getPercentInstance();
        percent.setMaximumFractionDigits(2);
        return percent.format(d);
    }
}
