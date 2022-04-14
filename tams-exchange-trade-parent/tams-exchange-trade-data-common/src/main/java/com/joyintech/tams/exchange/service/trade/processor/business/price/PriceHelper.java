package com.joyintech.tams.exchange.service.trade.processor.business.price;

import com.joyintech.tams.exchange.common.bean.TickBean;
import com.joyintech.tams.exchange.common.constant.*;
import com.joyintech.tams.exchange.common.constant.instrument.SubStockType;
import com.joyintech.tams.exchange.common.constant.joyin.InstructionPriceMode;
import com.joyintech.tams.exchange.common.db.domain.Instrument;
import com.joyintech.tams.exchange.common.db.domain.OrderInterface;
import com.joyintech.tams.exchange.common.proto.QuoteProto;
import com.joyintech.tams.exchange.common.utils.IntegratedEntrustUtils;
import com.joyintech.tams.exchange.common.utils.TradeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalTime;

/**
 * create By MAXX on 2020/12/17
 */
@Component
public class PriceHelper {
    private static final Logger logger = LoggerFactory.getLogger(PriceHelper.class);
    private double buyRatio = 1.02;
    private double sellRatio = 0.98;

    //沪深竞价平台
    public boolean checkPrice(BigDecimal entrustPrice, OrderInterface orderInterface, Instrument instrument, TickBean tick, String tradePlat, String entrustBussType, String priceMode) {
        if (TradePlat.NORMAL.value.equals(tradePlat)) {//沪深竞价
            if (SubStockType.ZB.value.equals(instrument.getSubStockType())
                    || SubStockType.ZXQYB.value.equals(instrument.getSubStockType())) {
                return checkPriceForZBA(entrustPrice, orderInterface, instrument, tick);
            }
            if (Market.SZSE.value.equals(instrument.getMarket()) && SubStockType.CYB.value.equals(instrument.getSubStockType())) {
                return checkPriceForCYB(entrustPrice, orderInterface, instrument, tick);
            }
            if (Market.SHSE.value.equals(instrument.getMarket()) && SubStockType.KCB.value.equals(instrument.getSubStockType())) {
                return checkPriceForKCB(entrustPrice, orderInterface, instrument, tick, priceMode);
            }
            if (InstructionBusiType.REPO_BIDDING.value.equals(entrustBussType)) {//债券质押式回购竞价指令
                return checkPriceForBondRepurchase(entrustPrice, orderInterface, instrument, tick);
            }
            if (InvestmentAssetType.BOND.value.equals(instrument.getAssetType())) {
                return checkPriceForBond(entrustPrice, orderInterface, instrument, tick);//沪深竞价平台债券交易
            }
        } else if (IntegratedEntrustUtils.isBulk(orderInterface.getEntrustProp())) {//沪深大宗交易
            return checkPriceForBulk(entrustPrice, orderInterface, instrument, tick);
        }
        return true;
    }

    //科创板
    private boolean checkPriceForKCB(BigDecimal entrustPrice, OrderInterface orderInterface, Instrument instrument, TickBean tick, String priceMode) {
        logger.info("沪深竞价科创板");
        int tradeTimeStage = getTradeTimeStage();
        if (tradeTimeStage == TradeTimeStage.THE_CONTINUOUS_AUCTION.value) {//连续竞价
            if (InstructionPriceMode.FIXED.value.equals(priceMode)) {//限价
                boolean result = false;
                double decimalPrice = 0;
                double ratio = 0;
                if (EntrustBs.BUY.value.equals(orderInterface.getEntrustBs())) {
                    decimalPrice = getBuyStandardPrice(tick);
                    if (Double.compare(decimalPrice, 0.0) > 0) {
                        ratio = buyRatio;
                        result = TradeUtils.bigThan(entrustPrice, TradeUtils.multiply(TradeUtils.getBigDecimal(decimalPrice, 4), TradeUtils.getBigDecimal(buyRatio, 4), 4));
                    }
                } else {
                    decimalPrice = getSaleStandardPrice(tick);
                    if (Double.compare(decimalPrice, 0.0) > 0) {
                        ratio = sellRatio;
                        result = TradeUtils.lessThan(entrustPrice, TradeUtils.multiply(TradeUtils.getBigDecimal(decimalPrice, 4), TradeUtils.getBigDecimal(sellRatio, 4), 4));
                    }
                }
                if (result) {
                    orderInterface.setErrorCode(ErrorCode.INNER_RISK_ERROR.value);
                    orderInterface.setErrorMsg("科创板限价委托价格[" + entrustPrice + "]高于买入或低于卖出基准价格[" + decimalPrice + "]的" + (ratio * 100) + "%");
                    return false;
                }
                return true;
            }
        }
        return true;
    }

    //创业板
    public boolean checkPriceForCYB(BigDecimal entrustPrice, OrderInterface orderInterface, Instrument instrument, TickBean tick) {
        logger.info("沪深竞价创业板");
        //首次公开发行上市的股票，上市后的前五个交易日不设价格涨跌幅限制
        boolean result = false;
        double decimalPrice = 0;
        double ratio = 0;
        if (EntrustBs.BUY.value.equals(orderInterface.getEntrustBs())) {
            decimalPrice = getBuyStandardPrice(tick);
            if (Double.compare(decimalPrice, 0.0) > 0) {
                ratio = buyRatio;
                result = TradeUtils.bigThan(entrustPrice, TradeUtils.multiply(TradeUtils.getBigDecimal(decimalPrice, 4), TradeUtils.getBigDecimal(buyRatio, 4), 4));
            }
        } else {
            decimalPrice = getSaleStandardPrice(tick);
            if (Double.compare(decimalPrice, 0.0) > 0) {
                ratio = sellRatio;
                result = TradeUtils.lessThan(entrustPrice, TradeUtils.multiply(TradeUtils.getBigDecimal(decimalPrice, 4), TradeUtils.getBigDecimal(sellRatio, 4), 4));
            }
        }
        if (result) {
            orderInterface.setErrorCode(ErrorCode.INNER_RISK_ERROR.value);
            orderInterface.setErrorMsg("创业板限价委托价格[" + entrustPrice + "]高于买入或低于卖出基准价格[" + decimalPrice + "]的" + (ratio * 100) + "%");
            return false;
        }
        return true;
    }

    //买入基准价格
    private double getBuyStandardPrice(TickBean tick) {
        double buyStandardPrice = tick.getAskPrice().get(0);
        if (Double.compare(buyStandardPrice, 0.0) > 0) {
            return buyStandardPrice;
        }
        buyStandardPrice = tick.getBidPrice().get(0);
        if (Double.compare(buyStandardPrice, 0.0) > 0) {
            return buyStandardPrice;
        }
        buyStandardPrice = tick.getLastPrice();
        if (Double.compare(buyStandardPrice, 0.0) > 0) {
            return buyStandardPrice;
        }
        buyStandardPrice = tick.getPreClosePrice();
        return buyStandardPrice;
    }

    //卖出基准价格
    private double getSaleStandardPrice(TickBean tick) {
        double saleStandardPrice = tick.getBidPrice().get(0);
        if (Double.compare(saleStandardPrice, 0.0) > 0) {
            return saleStandardPrice;
        }
        saleStandardPrice = tick.getAskPrice().get(0);
        if (Double.compare(saleStandardPrice, 0.0) > 0) {
            return saleStandardPrice;
        }
        saleStandardPrice = tick.getLastPrice();
        if (Double.compare(saleStandardPrice, 0.0) > 0) {
            return saleStandardPrice;
        }
        saleStandardPrice = tick.getPreClosePrice();
        return saleStandardPrice;
    }

    //沪深大宗交易
    public boolean checkPriceForBulk(BigDecimal entrustPrice, OrderInterface orderInterface, Instrument instrument, TickBean tick) {
        logger.info("沪深大宗交易");
        double lowerLimitPrice = 0;
        double upperLimitPrice = 0;
        if (Market.SZSE.value.equals(instrument.getMarket())) {//深交所
            //非股票上市首日
            lowerLimitPrice = TradeUtils.formatDouble(tick.getPreClosePrice() * 0.7, 4);
            upperLimitPrice = TradeUtils.formatDouble(tick.getPreClosePrice() * 1.3, 4);
        } else if (Market.SHSE.value.equals(instrument.getMarket())) {//上交所
            //非股票上市首日
            lowerLimitPrice = TradeUtils.formatDouble(Math.min(tick.getPreClosePrice() * 0.7, tick.getLowPrice()), 4);
            upperLimitPrice = TradeUtils.formatDouble(Math.max(tick.getPreClosePrice() * 1.3, tick.getHighPrice()), 4);
        }
        if ((Double.compare(lowerLimitPrice, 0.0) > 0 && Double.compare(lowerLimitPrice, entrustPrice.doubleValue()) > 0)
                || (Double.compare(upperLimitPrice, 0.0) > 0 && Double.compare(upperLimitPrice, entrustPrice.doubleValue()) < 0)) {
            orderInterface.setErrorCode(ErrorCode.INNER_RISK_ERROR.value);
            orderInterface.setErrorMsg("不满足沪深大宗交易无涨跌停场景价格限制。有效价格申报范围：[" + lowerLimitPrice + "," + upperLimitPrice + "]; 委托价格：[" + entrustPrice + "]");
            return false;
        }
        return true;
    }

    //沪深竞价平台主板A股以及中小企业板
    public boolean checkPriceForZBA(BigDecimal entrustPrice, OrderInterface orderInterface, Instrument instrument, TickBean tick) {
        logger.info("沪深竞价平台主板A股以及中小企业板");
        int tradeTimeStage = getTradeTimeStage();
        double lowerLimitPrice = 0;
        double upperLimitPrice = 0;
        if (Market.SZSE.value.equals(instrument.getMarket())) {//深交所
            if (tradeTimeStage == TradeTimeStage.THE_OPENING_SET.value) {//开盘集合
                lowerLimitPrice = 0;
                upperLimitPrice = TradeUtils.formatDouble(tick.getPreClosePrice() * 9, 4);
                if ((Double.compare(lowerLimitPrice, entrustPrice.doubleValue()) > 0)
                        || (Double.compare(upperLimitPrice, 0.0) > 0 && Double.compare(upperLimitPrice, entrustPrice.doubleValue()) < 0)) {
                    orderInterface.setErrorCode(ErrorCode.INNER_RISK_ERROR.value);
                    orderInterface.setErrorMsg("不满足沪深竞价平台主板A股其它无涨跌停场景价格限制。有效价格申报范围：[" + lowerLimitPrice + "," + upperLimitPrice + "]; 委托价格：[" + entrustPrice + "]");
                    return false;
                }
                return true;
            }
            lowerLimitPrice = TradeUtils.formatDouble(tick.getLastPrice() * 0.9, 4);
            upperLimitPrice = TradeUtils.formatDouble(tick.getLastPrice() * 1.1, 4);
        } else if (Market.SHSE.value.equals(instrument.getMarket())) {//上交所
            if (tradeTimeStage == TradeTimeStage.THE_OPENING_SET.value) {//开盘集合
                lowerLimitPrice = TradeUtils.formatDouble(tick.getPreClosePrice() * 0.5, 4);
                upperLimitPrice = TradeUtils.formatDouble(tick.getPreClosePrice() * 9, 4);
            } else {
                lowerLimitPrice = TradeUtils.formatDouble(tick.getLastPrice() * 0.9, 4);
                upperLimitPrice = TradeUtils.formatDouble(tick.getLastPrice() * 1.1, 4);
            }
        }
        if ((Double.compare(lowerLimitPrice, 0.0) > 0 && Double.compare(lowerLimitPrice, entrustPrice.doubleValue()) > 0)
                || (Double.compare(upperLimitPrice, 0.0) > 0 && Double.compare(upperLimitPrice, entrustPrice.doubleValue()) < 0)) {
            orderInterface.setErrorCode(ErrorCode.INNER_RISK_ERROR.value);
            orderInterface.setErrorMsg("不满足沪深竞价平台主板A股其它无涨跌停场景价格限制。有效价格申报范围：[" + lowerLimitPrice + "," + upperLimitPrice + "]; 委托价格：[" + entrustPrice + "]");
            return false;
        }
        return true;
    }

    public boolean checkPriceForBondRepurchase(BigDecimal entrustPrice, OrderInterface orderInterface, Instrument instrument, TickBean tick) {
        logger.info("沪深竞价平台，债券质押式回购");
        double lowerLimitPrice = 0;
        double upperLimitPrice = 0;
        int tradeTimeStage = getTradeTimeStage();
        if (Market.SZSE.value.equals(instrument.getMarket())) {//深交所
            if (tradeTimeStage == TradeTimeStage.THE_OPENING_SET.value) {//开盘集合
                upperLimitPrice = TradeUtils.formatDouble(tick.getPreClosePrice() * 2, 4);
            } else {
                upperLimitPrice = TradeUtils.formatDouble(tick.getLastPrice() * 2, 4);
            }
            if ((Double.compare(0, entrustPrice.doubleValue()) >= 0)
                    || (Double.compare(upperLimitPrice, 0.0) > 0 && Double.compare(upperLimitPrice, entrustPrice.doubleValue()) < 0)) {
                orderInterface.setErrorCode(ErrorCode.INNER_RISK_ERROR.value);
                orderInterface.setErrorMsg("不满足沪深竞价平台债券质押式回购无涨跌停场景价格限制。有效价格申报范围：(" + lowerLimitPrice + "," + upperLimitPrice + "]; 委托价格：[" + entrustPrice + "]");
                return false;
            }
            return true;
        } else if (Market.SHSE.value.equals(instrument.getMarket())) {//上交所
            if (tradeTimeStage != TradeTimeStage.THE_OPENING_SET.value) {//非开盘集合
                double bidPrice1 = getBidPrice1(tick);//买一价
                double askPrice1 = getAskPrice1(tick);//卖一价
                lowerLimitPrice = TradeUtils.formatDouble(Math.max(bidPrice1 * 0.9, ((bidPrice1 * 0.9 + askPrice1 * 1.1) / 2) * 0.7), 4);
                upperLimitPrice = TradeUtils.formatDouble(Math.min(askPrice1 * 1.1, ((bidPrice1 * 0.9 + askPrice1 * 1.1) / 2) * 1.3), 4);
            }
        }
        if ((Double.compare(lowerLimitPrice, 0.0) > 0 && Double.compare(lowerLimitPrice, entrustPrice.doubleValue()) > 0)
                || (Double.compare(upperLimitPrice, 0.0) > 0 && Double.compare(upperLimitPrice, entrustPrice.doubleValue()) < 0)) {
            orderInterface.setErrorCode(ErrorCode.INNER_RISK_ERROR.value);
            orderInterface.setErrorMsg("不满足沪深竞价平台债券质押式回购无涨跌停场景价格限制。有效价格申报范围：[" + lowerLimitPrice + "," + upperLimitPrice + "]; 委托价格：[" + entrustPrice + "]");
            return false;
        }
        return true;
    }

    //沪深竞价平台债券交易
    public boolean checkPriceForBond(BigDecimal entrustPrice, OrderInterface orderInterface, Instrument instrument, TickBean tick) {
        logger.info("沪深竞价平台债券交易");
        double lowerLimitPrice = 0;
        double upperLimitPrice = 0;
        int tradeTimeStage = getTradeTimeStage();
        if (Market.SZSE.value.equals(instrument.getMarket())) {//深交所
            if (tradeTimeStage == TradeTimeStage.THE_OPENING_SET.value) {//开盘集合
                //非上市首日
                lowerLimitPrice = TradeUtils.formatDouble(tick.getPreClosePrice() * 0.9, 4);
                upperLimitPrice = TradeUtils.formatDouble(tick.getPreClosePrice() * 1.1, 4);
            } else {
                lowerLimitPrice = TradeUtils.formatDouble(tick.getLastPrice() * 0.9, 4);
                upperLimitPrice = TradeUtils.formatDouble(tick.getLastPrice() * 1.1, 4);
            }
        } else if (Market.SHSE.value.equals(instrument.getMarket())) {//上交所
            if (tradeTimeStage == TradeTimeStage.THE_OPENING_SET.value) {//开盘集合
                lowerLimitPrice = TradeUtils.formatDouble(tick.getPreClosePrice() * 0.7, 4);
                upperLimitPrice = TradeUtils.formatDouble(tick.getPreClosePrice() * 1.5, 4);
            } else {
                double bidPrice1 = getBidPrice1(tick);//买一价
                double askPrice1 = getAskPrice1(tick);//卖一价
                lowerLimitPrice = TradeUtils.formatDouble(Math.max(bidPrice1 * 0.9, ((bidPrice1 * 0.9 + askPrice1 * 1.1) / 2) * 0.7), 4);
                upperLimitPrice = TradeUtils.formatDouble(Math.min(askPrice1 * 1.1, ((askPrice1 * 1.1 + bidPrice1 * 0.9) / 2) * 1.3), 4);
            }
        }
        if ((Double.compare(lowerLimitPrice, 0.0) > 0 && Double.compare(lowerLimitPrice, entrustPrice.doubleValue()) > 0)
                || (Double.compare(upperLimitPrice, 0.0) > 0 && Double.compare(upperLimitPrice, entrustPrice.doubleValue()) < 0)) {
            orderInterface.setErrorCode(ErrorCode.INNER_RISK_ERROR.value);
            orderInterface.setErrorMsg("不满足沪深竞价平台债券交易无涨跌停场景价格限制。有效价格申报范围：[" + lowerLimitPrice + "," + upperLimitPrice + "]; 委托价格：[" + entrustPrice + "]");
            return false;
        }
        return true;
    }

    public int getTradeTimeStage() {
        LocalTime now = LocalTime.now().withNano(0);
        int tradeTimeStage = -1;
        if (now.compareTo(LocalTime.parse(TradeTime.THE_OPENING_BEGIN.value)) >= 0
                && now.compareTo(LocalTime.parse(TradeTime.THE_OPENING_END.value)) <= 0) {
            tradeTimeStage = TradeTimeStage.THE_OPENING_SET.value;
        } else if ((now.compareTo(LocalTime.parse(TradeTime.THE_CONTINUOUS_AUCTION_BEGIN1.value)) >= 0
                && now.compareTo(LocalTime.parse(TradeTime.THE_CONTINUOUS_AUCTION_END1.value)) <= 0)
                || (now.compareTo(LocalTime.parse(TradeTime.THE_CONTINUOUS_AUCTION_BEGIN2.value)) >= 0
                && now.compareTo(LocalTime.parse(TradeTime.THE_CONTINUOUS_AUCTION_END2.value)) <= 0)) {
            tradeTimeStage = TradeTimeStage.THE_CONTINUOUS_AUCTION.value;
        } else if (now.compareTo(LocalTime.parse(TradeTime.THE_CLOSED_BEGIN.value)) >= 0
                && now.compareTo(LocalTime.parse(TradeTime.THE_CLOSED_END.value)) <= 0) {
            tradeTimeStage = TradeTimeStage.THE_CLOSED_SET.value;
        }
        return tradeTimeStage;
    }

    //获取买一价
    public double getBidPrice1(TickBean tick) {
        double bidPrice1 = tick.getBidPrice().get(0);
        if (Double.compare(bidPrice1, 0.0) > 0) {
            return bidPrice1;
        }
        bidPrice1 = Math.min(tick.getAskPrice().get(0), tick.getLastPrice());
        if (Double.compare(bidPrice1, 0.0) > 0) {
            return bidPrice1;
        }
        bidPrice1 = tick.getLastPrice();
        if (Double.compare(bidPrice1, 0.0) > 0) {
            return bidPrice1;
        }
        bidPrice1 = tick.getPreClosePrice();
        return bidPrice1;
    }

    //获取卖一价
    public double getAskPrice1(TickBean tick) {
        double askPrice1 = tick.getAskPrice().get(0);
        if (Double.compare(askPrice1, 0.0) > 0) {
            return askPrice1;
        }
        askPrice1 = Math.min(tick.getBidPrice().get(0), tick.getLastPrice());
        if (Double.compare(askPrice1, 0.0) > 0) {
            return askPrice1;
        }
        askPrice1 = tick.getLastPrice();
        if (Double.compare(askPrice1, 0.0) > 0) {
            return askPrice1;
        }
        askPrice1 = tick.getPreClosePrice();
        return askPrice1;
    }
}
