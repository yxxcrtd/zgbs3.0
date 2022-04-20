package com.joyintech.tams.exchange.service.trade.processor.business.stock;

import com.google.common.base.Strings;
import com.joyintech.exchange.service.quote.service.QuoteService;
import com.joyintech.tams.exchange.common.bean.MinPrice;
import com.joyintech.tams.exchange.common.bean.MinTrdUnitLimit;
import com.joyintech.tams.exchange.common.bean.ResultBean;
import com.joyintech.tams.exchange.common.bean.TickBean;
import com.joyintech.tams.exchange.common.cache.QuoteTickService;
import com.joyintech.tams.exchange.common.constant.EntrustBs;
import com.joyintech.tams.exchange.common.constant.EntrustProp;
import com.joyintech.tams.exchange.common.constant.ErrorCode;
import com.joyintech.tams.exchange.common.constant.TradePlat;
import com.joyintech.tams.exchange.common.constant.instrument.SubStockType;
import com.joyintech.tams.exchange.common.db.domain.Instrument;
import com.joyintech.tams.exchange.common.db.domain.OrderInterface;
import com.joyintech.tams.exchange.common.proto.QuoteProto;
import com.joyintech.tams.exchange.common.utils.CheckParamUtils;
import com.joyintech.tams.exchange.common.utils.EntrustUtils;
import com.joyintech.tams.exchange.common.utils.TradeUtils;
import com.joyintech.tams.exchange.datacommon.db.service.InstrumentService;
import com.joyintech.tams.exchange.service.trade.processor.business.price.PriceHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;

/**
 * 股票竞价业务处理
 */

@Component
public class StockHelper {
    private static final Logger logger = LoggerFactory.getLogger(StockHelper.class);

//    @Autowired
//    private QuoteTickService quoteTickService;
    @Autowired
    private QuoteService quoteService;
    @Autowired
    private InstrumentService instrumentService;
    @Autowired
    private PriceHelper priceHelper;
//    private double buyRatio = 1.02;
//    private double sellRatio = 0.98;
//    private long minVolume_KCB = 200;
//    private long maxVolume_KCB = 100000;
//    private long maxMarketVolume_KCB = 50000;

    public boolean checkCommonParam(BigDecimal entrustPrice, long entrustVolume, OrderInterface orderInterface, Instrument instrument, String tradePlat, String instructionPs, BigDecimal entrustBalance, String entrustBussType, String priceMode) {
        MinTrdUnitLimit minTrdUnitLimit = instrumentService.getMinTrdUnitLimit(tradePlat, instrument.getMarket(), instrument, instructionPs);
        if (EntrustUtils.isNoTrade(orderInterface.getEntrustProp())) {
            if (Objects.nonNull(minTrdUnitLimit)) {
                if (!CheckParamUtils.checkBuyUnit(orderInterface, entrustVolume, minTrdUnitLimit.getMinStep()))
                    return false;
            } else {
                logger.info("最小交易单位、交易上下限获取失败：TradePlat {}  instrument {}  instructionP {}", tradePlat, instrument.getInnerCode(), instructionPs);
            }
        } else if (EntrustBs.BUY.value.equals(orderInterface.getEntrustBs())
                && instrument.getBuyUnit() > 0
                && entrustVolume % instrument.getBuyUnit() != 0) {
            orderInterface.setErrorCode(ErrorCode.INNER_RISK_ERROR.value);
            orderInterface.setErrorMsg("不满足最小买入单位:" + instrument.getBuyUnit());
            return false;
        }
        // 申报类不需要check最小价差和涨跌停
        if (!EntrustUtils.isNoTrade(orderInterface.getEntrustProp())) {
            MinPrice minPrice = instrumentService.getMinPrice(tradePlat, instrument.getMarket(), instrument);
            if (Double.compare(minPrice.getMinPrice().doubleValue(), 0.0) > 0
                    && TradeUtils.divideAndRemainder(entrustPrice, minPrice.getMinPrice()).compareTo(BigDecimal.ZERO) != 0) {
                orderInterface.setErrorCode(ErrorCode.INNER_RISK_ERROR.value());
                orderInterface.setErrorMsg("委托价格不满足最小价差：" + minPrice.getMinPrice().doubleValue());
                return false;
            }
            ResultBean tickBean = quoteService.getTick(orderInterface.getStockCode());
            if (ErrorCode.isSuccess(tickBean.getErrorCode())) {
                TickBean tick = (TickBean)tickBean.getData();
//            QuoteProto.TickBean tick = quoteTickService.findFromRedisById(orderInterface.getStockCode());
//            if (Objects.nonNull(tick)) {
                double lowerLimitPrice = TradeUtils.formatDouble(tick.getLowerLimitPrice(), 4);
                double upperLimitPrice = TradeUtils.formatDouble(tick.getUpperLimitPrice(), 4);
                if (Double.compare(lowerLimitPrice, 0.0) == 0 && Double.compare(upperLimitPrice, 0.0) == 0) {
                    logger.info("没有涨跌幅");
                    boolean checkPrice = priceHelper.checkPrice(entrustPrice, orderInterface, instrument, tick, tradePlat, entrustBussType, priceMode);
                    if (!checkPrice) {
                        return false;
                    }
                } else {
                    if ((Double.compare(lowerLimitPrice, 0.0) > 0 && Double.compare(lowerLimitPrice, entrustPrice.doubleValue()) > 0)
                            || (Double.compare(upperLimitPrice, 0.0) > 0 && Double.compare(upperLimitPrice, entrustPrice.doubleValue()) < 0)) {
                        orderInterface.setErrorCode(ErrorCode.INNER_RISK_ERROR.value);
                        orderInterface.setErrorMsg("不满足涨跌停限制。 跌停价：[" + lowerLimitPrice + "] -- 涨停价：[" + upperLimitPrice + "]; 委托价格：[" + entrustPrice + "]");
                        return false;
                    }
                }
            }
            return checkPriceForSTIB(entrustPrice, entrustVolume, orderInterface, instrument, null);
        }
        // 交易数量及范围检查
        if (TradePlat.NORMAL.value.equals(tradePlat)) {
            return compareMinOrMaxVolumeAndBalance(orderInterface, entrustBalance, BigInteger.valueOf(entrustVolume), minTrdUnitLimit);
        }
        return true;
    }

    private boolean compareMinOrMaxVolumeAndBalance(OrderInterface orderInterface, BigDecimal entrustBalance, BigInteger bgEntrustVolume, MinTrdUnitLimit minTrdUnitLimit) {
        if (Objects.nonNull(minTrdUnitLimit) && EntrustBs.BUY.value.equals(orderInterface.getEntrustBs())) {
            Integer minNum = 0;
            if (EntrustProp.isMarketPrice(orderInterface.getEntrustProp())) {
                minNum = minTrdUnitLimit.getMinNumMarket();
            } else if (EntrustProp.NORMAL.value.equals(orderInterface.getEntrustProp())) {
                minNum = minTrdUnitLimit.getMinNum();
            }
            if (bgEntrustVolume.compareTo(BigInteger.valueOf(minNum)) < 0 && entrustBalance.compareTo(minTrdUnitLimit.getMinAmt()) < 0) {
                orderInterface.setErrorCode(ErrorCode.INNER_RISK_ERROR.value);
                orderInterface.setErrorMsg("不满足沪深竞价撮合平台股票交易最小买入数量:" + minNum + ",或者金额" + minTrdUnitLimit.getMinAmt());
                return false;
            }
        }
        return true;
    }

    // 科创板股票 限价申报价格限制
    // 科创板股票 限价申报价格限制
    public boolean checkPriceForSTIB(BigDecimal entrustPrice, long entrustVolume, OrderInterface orderInterface, Instrument instrument, TickBean tick) {
        String subStockType = instrument.getSubStockType();
        if (!Strings.isNullOrEmpty(subStockType)) {
            if (SubStockType.KCB.value.equals(instrument.getSubStockType())) {
                Integer minVolume_KCB = instrument.getLowVolume();
                Integer marketLowVolume_KCB = instrument.getMarketLowVolume();
                if (EntrustProp.NORMAL.value.equals(orderInterface.getEntrustProp())
                        && entrustVolume < minVolume_KCB) {
                    orderInterface.setErrorCode(ErrorCode.INNER_RISK_ERROR.value);
                    orderInterface.setErrorMsg("科创板交易限价委托数量[" + entrustVolume + "]不应该小于[" + minVolume_KCB + "]");
                    return false;
                } else if (EntrustProp.isMarketPrice(orderInterface.getEntrustProp())
                        && entrustVolume < marketLowVolume_KCB) {
                    orderInterface.setErrorCode(ErrorCode.INNER_RISK_ERROR.value);
                    orderInterface.setErrorMsg("科创板交易市价委托数量[" + entrustVolume + "]不应该小于[" + marketLowVolume_KCB + "]");
                    return false;
                }
                /*if (EntrustProp.NORMAL.value.equals(orderInterface.getEntrustProp())) {
                    if (EntrustBs.BUY.value.equals(orderInterface.getEntrustBs())) {
                        if (tick.getAskPrice(0) != 0) {
                            return checkNormalPriceLimit(orderInterface, entrustPrice, tick.getAskPrice(0), buyRatio);
                        } else if (tick.getBidPrice(0) != 0) {
                            return checkNormalPriceLimit(orderInterface, entrustPrice, tick.getBidPrice(0), buyRatio);
                        } else if (tick.getLastPrice() != 0) {
                            return checkNormalPriceLimit(orderInterface, entrustPrice, tick.getLastPrice(), buyRatio);
                        } else if (tick.getPreClosePrice() != 0) {
                            return checkNormalPriceLimit(orderInterface, entrustPrice, tick.getPreClosePrice(), buyRatio);
                        } else {
                            logger.info("买入订单 {} 未找到基准价格！！！", orderInterface.getId());
                        }
                    } else {
                        if (tick.getBidPrice(0) != 0) {
                            return checkNormalPriceLimit(orderInterface, entrustPrice, tick.getBidPrice(0), sellRatio);
                        } else if (tick.getAskPrice(0) != 0) {
                            return checkNormalPriceLimit(orderInterface, entrustPrice, tick.getAskPrice(0), sellRatio);
                        } else if (tick.getLastPrice() != 0) {
                            return checkNormalPriceLimit(orderInterface, entrustPrice, tick.getLastPrice(), sellRatio);
                        } else if (tick.getPreClosePrice() != 0) {
                            return checkNormalPriceLimit(orderInterface, entrustPrice, tick.getPreClosePrice(), sellRatio);
                        } else {
                            logger.info("卖出入订单 {} 未找到基准价格！！！", orderInterface.getId());
                        }
                    }
                }*/
            }
        } else {
            logger.info("该证券代码没有证券子类信息！");
        }
        return true;
    }

    private boolean checkNormalPriceLimit(OrderInterface orderInterface, BigDecimal entrustPrice, double price, double ratio) {
        BigDecimal decimalPrice = TradeUtils.getBigDecimal(price, 4);
        BigDecimal decimalRatio = TradeUtils.getBigDecimal(ratio, 4);
        boolean result;
        if (EntrustBs.BUY.value.equals(orderInterface.getEntrustBs())) {
            result = TradeUtils.lessThan(entrustPrice, TradeUtils.multiply(decimalPrice, decimalRatio, 4));
        } else {
            result = TradeUtils.bigThan(entrustPrice, TradeUtils.multiply(decimalPrice, decimalRatio, 4));
        }
        if (!result) {
            orderInterface.setErrorCode(ErrorCode.INNER_RISK_ERROR.value);
            orderInterface.setErrorMsg("科创板限价委托价格[" + entrustPrice + "]高于买入或低于卖出基准价格[" + decimalPrice + "]的" + ratio * 100 + "%");
            return false;
        }
        return true;
    }
}
