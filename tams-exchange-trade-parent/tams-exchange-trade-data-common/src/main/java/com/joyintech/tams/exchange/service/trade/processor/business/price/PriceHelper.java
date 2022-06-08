package com.joyintech.tams.exchange.service.trade.processor.business.price;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Lists;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.exchange.common.bean.TickBean;
import com.joyintech.tams.exchange.common.bean.dto.OpenCloseTime;
import com.joyintech.tams.exchange.common.bean.request.BaseOrderRequest;
import com.joyintech.tams.exchange.common.cache.TradeTimeService;
import com.joyintech.tams.exchange.common.constant.*;
import com.joyintech.tams.exchange.common.constant.instrument.SubStockType;
import com.joyintech.tams.exchange.common.constant.joyin.InstructionPriceMode;
import com.joyintech.tams.exchange.common.db.domain.Instruction;
import com.joyintech.tams.exchange.common.db.domain.Instrument;
import com.joyintech.tams.exchange.common.db.domain.Order;
import com.joyintech.tams.exchange.common.db.domain.OrderInterface;
import com.joyintech.tams.exchange.common.enums.invest.InvestAssetTypeEnum;
import com.joyintech.tams.exchange.common.enums.invest.InvestRepoTypeEnum;
import com.joyintech.tams.exchange.common.utils.EntrustPropUtils;
import com.joyintech.tams.exchange.common.utils.EnumUtil;
import com.joyintech.tams.exchange.common.utils.IntegratedEntrustUtils;
import com.joyintech.tams.exchange.common.utils.TradeUtils;
import com.joyintech.tams.exchange.service.trade.processor.check.CommonCheckKit;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static com.joyintech.tams.constant.DictCodeList.*;

/**
 * create By MAXX on 2020/12/17
 */
@Component
public class PriceHelper {
    /**
     * 日志对象
     */
    private static final Logger logger = LoggerFactory.getLogger(PriceHelper.class);
    private double buyRatio = 1.02;
    private double sellRatio = 0.98;
    private TradeTimeService tradeTimeService;

    @Autowired
    public void setTradeTimeService(TradeTimeService tradeTimeService) {
        this.tradeTimeService = tradeTimeService;
    }

    public String checkPrice(BaseOrderRequest order, Instruction instruction, Instrument instrument, TickBean tick) {
        OrderInterface orderInterface = new Order();
        orderInterface.setEntrustBs(EntrustPropUtils.getEntrustBs(instruction));
        BigDecimal entrustPrice = order.getEntrustPrice();
        String tradePlat = instruction.getTradePlat();
        String entrustBussType = instruction.getEntrustBussType();
        String priceMode = EntrustPropUtils.getEntrustProp(instruction, order.getEntrustProp());

        int tradeTimeStage = getTradeTimeStage(instruction, order, instrument);

        if (IntegratedEntrustUtils.isBulk(orderInterface.getEntrustProp())) {//沪深大宗交易
            checkPriceForBulk(entrustPrice, orderInterface, instrument, tick);
        } else if (EnumUtil.in(tradePlat, TradePlat.NORMAL, TradePlat.NEW_BOND)) {//沪深竞价,新债
            if (SubStockType.ZB.value.equals(instrument.getSubStockType())
                    || SubStockType.ZXQYB.value.equals(instrument.getSubStockType())) {
                checkPriceForZBA(entrustPrice, orderInterface, instrument, tick, tradeTimeStage);
            } else if (Market.SZSE.value.equals(instrument.getMarket())
                    && SubStockType.CYB.value.equals(instrument.getSubStockType())) {
                checkPriceForCYB(entrustPrice, orderInterface, instrument, tick);
            } else if (Market.SHSE.value.equals(instrument.getMarket())
                    && SubStockType.KCB.value.equals(instrument.getSubStockType())) {
                checkPriceForKCB(entrustPrice, orderInterface, instrument, tick, priceMode, tradeTimeStage);
            } else if (DictCodeList.回购类型_质押式竞价回购.getValue().equals(instruction.getAssetType1())) {//债券质押式回购竞价指令
                checkPriceForBondRepurchase(entrustPrice, orderInterface, instrument, tick, tradeTimeStage);
            } else if (InvestAssetTypeEnum.BOND.codeEquals(instruction.getAssetType())) {
                checkPriceForBond(entrustPrice, orderInterface, instrument, tick, tradeTimeStage);//沪深竞价平台债券交易
            }
        }
        return orderInterface.getErrorMsg();
    }

    //沪深竞价平台
    public boolean checkPrice(BigDecimal entrustPrice, OrderInterface orderInterface, Instrument instrument, TickBean tick, String tradePlat, String entrustBussType, String priceMode) {
        int tradeTimeStage = getTradeTimeStage();
        if (TradePlat.NORMAL.value.equals(tradePlat)) {//沪深竞价
            if (SubStockType.ZB.value.equals(instrument.getSubStockType())
                    || SubStockType.ZXQYB.value.equals(instrument.getSubStockType())) {
                return checkPriceForZBA(entrustPrice, orderInterface, instrument, tick, tradeTimeStage);
            }
            if (Market.SZSE.value.equals(instrument.getMarket()) && SubStockType.CYB.value.equals(instrument.getSubStockType())) {
                return checkPriceForCYB(entrustPrice, orderInterface, instrument, tick);
            }
            if (Market.SHSE.value.equals(instrument.getMarket()) && SubStockType.KCB.value.equals(instrument.getSubStockType())) {
                return checkPriceForKCB(entrustPrice, orderInterface, instrument, tick, priceMode, tradeTimeStage);
            }
            if (InvestAssetTypeEnum.PLEDGE_REPURCHASE.codeEquals(instrument.getAssetType())
                    && InvestRepoTypeEnum.BIDDING_REPO.codeEquals(instrument.getAssetType1())) {//债券质押式回购竞价指令
                return checkPriceForBondRepurchase(entrustPrice, orderInterface, instrument, tick, tradeTimeStage);
            }
            if (InvestAssetTypeEnum.BOND.codeEquals(instrument.getAssetType())) {
                return checkPriceForBond(entrustPrice, orderInterface, instrument, tick, tradeTimeStage);//沪深竞价平台债券交易
            }
        } else if (IntegratedEntrustUtils.isBulk(orderInterface.getEntrustProp())) {//沪深大宗交易
            return checkPriceForBulk(entrustPrice, orderInterface, instrument, tick);
        }
        return true;
    }

    //科创板
    private boolean checkPriceForKCB(BigDecimal entrustPrice, OrderInterface orderInterface, Instrument instrument, TickBean tick, String priceMode, int tradeTimeStage) {
        logger.info("沪深竞价科创板");
        if (tradeTimeStage != TradeTimeStage.THE_CONTINUOUS_AUCTION.value) {//连续竞价
            return true;
        }
        if (InstructionPriceMode.FIXED.codeNotEquals(priceMode)) {
            return true;
        }
        boolean result = false;
        double decimalPrice = 0;
        double ratio = 0;
        if (EntrustBs.BUY.codeEquals(orderInterface.getEntrustBs())) {
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
        /*if (tradeTimeStage != TradeTimeStage.THE_CONTINUOUS_AUCTION.value) {//连续竞价
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
        return true;*/
    }

    //创业板
    private boolean checkPriceForCYB(BigDecimal entrustPrice, OrderInterface orderInterface, Instrument instrument, TickBean tick) {
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

    //沪深大宗交易
    private boolean checkPriceForBulk(BigDecimal entrustPrice, OrderInterface orderInterface, Instrument instrument, TickBean tick) {
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
        return compareLimitPrice(entrustPrice, orderInterface, lowerLimitPrice, upperLimitPrice, instrument.getMarket());
        /*if ((Double.compare(lowerLimitPrice, 0.0) > 0 && Double.compare(lowerLimitPrice, entrustPrice.doubleValue()) > 0)
                || (Double.compare(upperLimitPrice, 0.0) > 0 && Double.compare(upperLimitPrice, entrustPrice.doubleValue()) < 0)) {
            orderInterface.setErrorCode(ErrorCode.INNER_RISK_ERROR.value);
            orderInterface.setErrorMsg("不满足沪深大宗交易无涨跌停场景价格限制。有效价格申报范围：[" + lowerLimitPrice + "," + upperLimitPrice + "]; 委托价格：[" + entrustPrice + "]");
            return false;
        }
        return true;*/
    }

    //沪深竞价平台主板A股以及中小企业板
    private boolean checkPriceForZBA(BigDecimal entrustPrice, OrderInterface orderInterface, Instrument instrument, TickBean tick, int tradeTimeStage) {
        logger.info("沪深竞价平台主板A股以及中小企业板");
        double lowerLimitPrice = 0;
        double upperLimitPrice = 0;
        if (Market.SZSE.value.equals(instrument.getMarket())) {//深交所
            if (tradeTimeStage == TradeTimeStage.THE_OPENING_SET.value) {//开盘集合
                lowerLimitPrice = 0;
                upperLimitPrice = TradeUtils.formatDouble(tick.getPreClosePrice() * 9, 4);
                if ((Double.compare(lowerLimitPrice, entrustPrice.doubleValue()) > 0)
                        || (Double.compare(upperLimitPrice, 0.0) > 0 && Double.compare(upperLimitPrice, entrustPrice.doubleValue()) < 0)) {
                    orderInterface.setErrorCode(ErrorCode.INNER_RISK_ERROR.value);
                    orderInterface.setErrorMsg(dealErrorMsgByMarket(lowerLimitPrice, upperLimitPrice, instrument.getMarket()));
//                    orderInterface.setErrorMsg("不满足沪深竞价平台主板A股其它无涨跌停场景价格限制。有效价格申报范围：[" + lowerLimitPrice + "," + upperLimitPrice + "]; 委托价格：[" + entrustPrice + "]");
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
        return compareLimitPrice(entrustPrice, orderInterface, lowerLimitPrice, upperLimitPrice, instrument.getMarket());
        /*if ((Double.compare(lowerLimitPrice, 0.0) > 0 && Double.compare(lowerLimitPrice, entrustPrice.doubleValue()) > 0)
                || (Double.compare(upperLimitPrice, 0.0) > 0 && Double.compare(upperLimitPrice, entrustPrice.doubleValue()) < 0)) {
            orderInterface.setErrorCode(ErrorCode.INNER_RISK_ERROR.value);
            orderInterface.setErrorMsg("不满足沪深竞价平台主板A股其它无涨跌停场景价格限制。有效价格申报范围：[" + lowerLimitPrice + "," + upperLimitPrice + "]; 委托价格：[" + entrustPrice + "]");
            return false;
        }
        return true;*/
    }

    private boolean checkPriceForBondRepurchase(BigDecimal entrustPrice, OrderInterface orderInterface, Instrument instrument, TickBean tick, int tradeTimeStage) {
        logger.info("沪深竞价平台，债券质押式回购");
        double lowerLimitPrice = 0;
        double upperLimitPrice = 0;

        // [0,前收盘价×200%]	[0，匹配成交最新成交价格+1%]
        if (tradeTimeStage == TradeTimeStage.THE_OPENING_SET.value) {//开盘集合
            upperLimitPrice = TradeUtils.formatDouble(tick.getPreClosePrice() * 2, 4);
        } else {
            upperLimitPrice = TradeUtils.formatDouble(tick.getLastPrice() * 1.01, 4);
        }

        if ((Double.compare(0, entrustPrice.doubleValue()) >= 0)
                || (Double.compare(upperLimitPrice, 0.0) > 0 && Double.compare(upperLimitPrice, entrustPrice.doubleValue()) < 0)) {
            orderInterface.setErrorMsg(dealErrorMsgByMarket(lowerLimitPrice, upperLimitPrice, instrument.getMarket()));
            orderInterface.setErrorCode(ErrorCode.INNER_RISK_ERROR.value);
            return false;
        }
        /*if (Market.SZSE.value.equals(market)) {//深交所
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
        } else if (Market.SHSE.value.equals(market)) {//上交所
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
        }*/
        return true;
    }

    //沪深竞价平台债券交易
    private boolean checkPriceForBond(BigDecimal entrustPrice, OrderInterface orderInterface, Instrument instrument, TickBean tick, int tradeTimeStage) {
        logger.info("沪深竞价平台债券交易");
        double lowerLimitPrice = 0;
        double upperLimitPrice = 0;
        String assetType1 = instrument.getAssetType1();
        String market = instrument.getMarket();
        double lastPrice = tick.getLastPrice();
        double preClosePrice = tick.getPreClosePrice();

        if (WIND一级分类_含债券一级分类等_可转债.getValue().equals(assetType1)) {
            if (Market.SZSE.value.equals(market)) {//深交所
                if (tradeTimeStage == TradeTimeStage.THE_OPENING_SET.value) {//开盘集合
                    //非上市首日
                    lowerLimitPrice = TradeUtils.formatDouble(preClosePrice * 0.9, 4);
                    upperLimitPrice = TradeUtils.formatDouble(preClosePrice * 1.1, 4);
                } else {
                    lowerLimitPrice = TradeUtils.formatDouble(lastPrice * 0.9, 4);
                    upperLimitPrice = TradeUtils.formatDouble(lastPrice * 1.1, 4);
                }
            } else if (Market.SHSE.value.equals(market)) {//上交所
                if (tradeTimeStage == TradeTimeStage.THE_OPENING_SET.value) {//开盘集合
                    lowerLimitPrice = TradeUtils.formatDouble(preClosePrice * 0.7, 4);
                    upperLimitPrice = TradeUtils.formatDouble(preClosePrice * 1.5, 4);
                } else {
                    double bidPrice1 = getBidPrice1(tick);//买一价
                    double askPrice1 = getAskPrice1(tick);//卖一价
                    lowerLimitPrice = TradeUtils.formatDouble(Math.max(bidPrice1 * 0.9, ((bidPrice1 * 0.9 + askPrice1 * 1.1) / 2) * 0.7), 4);
                    upperLimitPrice = TradeUtils.formatDouble(Math.min(askPrice1 * 1.1, ((askPrice1 * 1.1 + bidPrice1 * 0.9) / 2) * 1.3), 4);
                }
            }
        } else {
            // 新债
            // 国债、政府债券、政府支持债券、政策性金融债
            //      [前收盘价×70%,前收盘价×130%]	[匹配成交最新成交价格×90%，匹配成交最新成交价格×110%]
            // 其他债券
            //      [前收盘价×70%,前收盘价×130%]	[匹配成交最新成交价格×80%，匹配成交最新成交价格×120%]
            // 使用 WIND一级分类；WIND二级分类
            // 映射关系：国债就是国债，政府债券就是地方政府债，政府支持债券就是政府支持机构债，政策性金融债是 一级为金融债二级为政策银行债

            if (tradeTimeStage == TradeTimeStage.THE_OPENING_SET.value) {//开盘集合
                lowerLimitPrice = TradeUtils.formatDouble(preClosePrice * 0.7, 4);
                upperLimitPrice = TradeUtils.formatDouble(preClosePrice * 1.3, 4);
            } else if (tradeTimeStage == TradeTimeStage.THE_CONTINUOUS_AUCTION.value) {
                if (WIND一级分类_含债券一级分类等_国债.getValue().equals(assetType1)
                        || WIND一级分类_含债券一级分类等_地方政府债.getValue().equals(assetType1)
                        || WIND一级分类_含债券一级分类等_政府支持机构债.getValue().equals(assetType1)
                        || (WIND一级分类_含债券一级分类等_金融债.getValue().equals(assetType1)
                        && WIND二级分类_含债券二级分类等_政策银行债.getValue().equals(instrument.getSubStockType()))
                ) {
                    lowerLimitPrice = TradeUtils.formatDouble(lastPrice * 0.9, 4);
                    upperLimitPrice = TradeUtils.formatDouble(lastPrice * 1.1, 4);
                } else {
                    lowerLimitPrice = TradeUtils.formatDouble(lastPrice * 0.8, 4);
                    upperLimitPrice = TradeUtils.formatDouble(lastPrice * 1.2, 4);
                }
            }

        }
        return compareLimitPrice(entrustPrice, orderInterface, lowerLimitPrice, upperLimitPrice, market);
    }

    private boolean compareLimitPrice(BigDecimal entrustPrice, OrderInterface orderInterface, double lowerLimitPrice, double upperLimitPrice, String market) {
        if ((Double.compare(lowerLimitPrice, 0.0) > 0 && Double.compare(lowerLimitPrice, entrustPrice.doubleValue()) > 0)
                || (Double.compare(upperLimitPrice, 0.0) > 0 && Double.compare(upperLimitPrice, entrustPrice.doubleValue()) < 0)) {
            orderInterface.setErrorMsg(dealErrorMsgByMarket(lowerLimitPrice, upperLimitPrice, market));
            orderInterface.setErrorCode(ErrorCode.ATTRIBUTE_ERROR.value);
            return false;
        }
        return true;
    }

    private String dealErrorMsgByMarket(double lowerLimitPrice, double upperLimitPrice, String market) {
        String suffix = CommonCheckKit.TRD_PRICE_SCOPE_CONTROL_FORBID.equals(CacheUtil.getSysParam(CommonCheckKit.TRD_PRICE_SCOPE_CONTROL)) ? "无法下达！" : "是否继续下达？";
        if (Market.SHSE.investValue.equals(market)) {
            return StrUtil.format("委托价格在有效价格范围[{}，{}]范围之外，存在委托废单的风险，{}", lowerLimitPrice, upperLimitPrice, suffix);
        } else if (Market.SZSE.investValue.equals(market)) {
            return StrUtil.format("委托价格在有效价格范围[{}，{}]范围之外，存在委托无法成交的风险，{}", lowerLimitPrice, upperLimitPrice, suffix);
        }
        return StrUtil.EMPTY;
    }

    private int getTradeTimeStage(Instruction instruction, BaseOrderRequest order, Instrument instrument) {
        int tradeTimeStage = -1;
        List<OpenCloseTime> tradePeriod = tradeTimeService.getTradePeriod(instruction, order, instrument);
        String localTime = LocalTime.now().toString();
        boolean result = false;
        for (OpenCloseTime openCloseTime : tradePeriod) {
            result = DateUtil.isIn(DateUtil.parseTime(localTime), DateUtil.parseTime(openCloseTime.getOpenTime()), DateUtil.parseTime(openCloseTime.getCloseTime()));
            if (result) {
            	if(StringUtils.isNotEmpty(openCloseTime.getPriceTimeType())) {
            		tradeTimeStage = Integer.parseInt(openCloseTime.getPriceTimeType());
            		break;
            	}
            }
        }
        return tradeTimeStage;
    }

    /**
     * 获取当前所属交易时间段
     */
    private int getTradeTimeStage() {
        LocalTime now = LocalTime.now().withNano(0);
        int tradeTimeStage = -1;
        if (now.compareTo(TradeTime.THE_OPENING_BEGIN.getTime()) >= 0
                && now.compareTo(TradeTime.THE_OPENING_END.getTime()) <= 0) {
            tradeTimeStage = TradeTimeStage.THE_OPENING_SET.value;
        } else if ((now.compareTo(TradeTime.THE_CONTINUOUS_AUCTION_BEGIN1.getTime()) >= 0
                && now.compareTo(TradeTime.THE_CONTINUOUS_AUCTION_END1.getTime()) <= 0)
                || (now.compareTo(TradeTime.THE_CONTINUOUS_AUCTION_BEGIN2.getTime()) >= 0
                && now.compareTo(TradeTime.THE_CONTINUOUS_AUCTION_END2.getTime()) <= 0)) {
            tradeTimeStage = TradeTimeStage.THE_CONTINUOUS_AUCTION.value;
        } else if (now.compareTo(TradeTime.THE_CLOSED_BEGIN.getTime()) >= 0
                && now.compareTo(TradeTime.THE_CLOSED_END.getTime()) <= 0) {
            tradeTimeStage = TradeTimeStage.THE_CLOSED_SET.value;
        }
        /*if (now.compareTo(LocalTime.parse(TradeTime.THE_OPENING_BEGIN.value)) >= 0
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
        }*/
        return tradeTimeStage;
    }

    /**
     * 获取买一价
     * <p/>
     * 规则：
     * <br>1.依次判断买一价、卖一价和最新价中最小值，最新价中大于0.0的值，若大于0，则返回该值。
     * <br>2.若都不符合，则返回昨收盘价
     *
     * @param tick 行情数据
     * @return 买一价
     */
    private double getBidPrice1(TickBean tick) {
        ArrayList<Double> prices = Lists.newArrayList();
        prices.add(tick.getBidPrice().get(0));
        prices.add(Math.min(tick.getAskPrice().get(0), tick.getLastPrice()));
        prices.add(tick.getLastPrice());

        return prices.stream()
                .filter(item -> NumberUtil.compare(item, 0.0) > 0)
                .findFirst()
                .orElse(tick.getPreClosePrice());

        /*// 买一价
        double bidPrice1 = tick.getBidPrice().get(0);
        if (Double.compare(bidPrice1, 0.0) > 0) {
            return bidPrice1;
        }
        // 卖一价和最新价中最小值
        bidPrice1 = Math.min(tick.getAskPrice().get(0), tick.getLastPrice());
        if (Double.compare(bidPrice1, 0.0) > 0) {
            return bidPrice1;
        }
        // 最新价
        bidPrice1 = tick.getLastPrice();
        if (Double.compare(bidPrice1, 0.0) > 0) {
            return bidPrice1;
        }
        // 昨收盘价
        bidPrice1 = tick.getPreClosePrice();
        return bidPrice1;*/
    }

    /**
     * 获取卖一价
     * <p/>
     * 规则：
     * <br>1.依次判断卖一价、买一价和最新价中最小值，最新价中大于0.0的值，若大于0，则返回该值。
     * <br>2.若都不符合，则返回昨收盘价
     *
     * @param tick 行情数据
     * @return 卖一价
     */
    private double getAskPrice1(TickBean tick) {
        ArrayList<Double> prices = Lists.newArrayList();
        prices.add(tick.getAskPrice().get(0));
        prices.add(Math.min(tick.getBidPrice().get(0), tick.getLastPrice()));
        prices.add(tick.getLastPrice());

        return prices.stream()
                .filter(item -> NumberUtil.compare(item, 0.0) > 0)
                .findFirst()
                .orElse(tick.getPreClosePrice());

        /*// 卖一价
        double askPrice1 = tick.getAskPrice().get(0);
        if (Double.compare(askPrice1, 0.0) > 0) {
            return askPrice1;
        }
        // 买一价和最新价中最小值
        askPrice1 = Math.min(tick.getBidPrice().get(0), tick.getLastPrice());
        if (Double.compare(askPrice1, 0.0) > 0) {
            return askPrice1;
        }
        // 最新价
        askPrice1 = tick.getLastPrice();
        if (Double.compare(askPrice1, 0.0) > 0) {
            return askPrice1;
        }
        // 昨收盘价
        askPrice1 = tick.getPreClosePrice();
        return askPrice1;*/
    }

    /**
     * 获取买入基准价
     * <p/>
     * 规则：
     * <br>1.依次判断卖一价、买一价、最新价中大于0.0的值，若大于0，则返回该值。
     * <br>2.若都不符合，则返回昨收盘价
     *
     * @param tick 行情数据
     * @return 买入基准价
     */
    private double getBuyStandardPrice(TickBean tick) {
        ArrayList<Double> prices = Lists.newArrayList();
        prices.add(tick.getAskPrice().get(0));
        prices.add(tick.getBidPrice().get(0));
        prices.add(tick.getLastPrice());

        return prices.stream()
                .filter(item -> NumberUtil.compare(item, 0.0) > 0)
                .findFirst()
                .orElse(tick.getPreClosePrice());

        /*// 卖一价
        double buyStandardPrice = tick.getAskPrice().get(0);
        if (Double.compare(buyStandardPrice, 0.0) > 0) {
            return buyStandardPrice;
        }
        // 买一价
        buyStandardPrice = tick.getBidPrice().get(0);
        if (Double.compare(buyStandardPrice, 0.0) > 0) {
            return buyStandardPrice;
        }
        // 最新价
        buyStandardPrice = tick.getLastPrice();
        if (Double.compare(buyStandardPrice, 0.0) > 0) {
            return buyStandardPrice;
        }
        // 昨收盘价
        buyStandardPrice = tick.getPreClosePrice();
        return buyStandardPrice;*/
    }

    /**
     * 获取卖出基准价格
     * <p/>
     * 规则：
     * <br>1.依次判断买一价、卖一价、最新价中大于0.0的值，若大于0，则返回该值。
     * <br>2.若都不符合，则返回昨收盘价
     *
     * @param tick 行情数据
     * @return 卖出基准价格
     */
    private double getSaleStandardPrice(TickBean tick) {
        ArrayList<Double> prices = Lists.newArrayList();
        prices.add(tick.getBidPrice().get(0));
        prices.add(tick.getAskPrice().get(0));
        prices.add(tick.getLastPrice());

        return prices.stream()
                .filter(item -> NumberUtil.compare(item, 0.0) > 0)
                .findFirst()
                .orElse(tick.getPreClosePrice());

        /*double saleStandardPrice = tick.getBidPrice().get(0);
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
        return saleStandardPrice;*/
    }
}
