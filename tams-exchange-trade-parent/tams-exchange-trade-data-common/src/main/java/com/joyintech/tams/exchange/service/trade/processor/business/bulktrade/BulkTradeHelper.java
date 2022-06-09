package com.joyintech.tams.exchange.service.trade.processor.business.bulktrade;

import com.joyintech.exchange.service.quote.service.QuoteService;
import com.joyintech.tams.exchange.common.bean.MinTrdUnitLimit;
import com.joyintech.tams.exchange.common.bean.ResultBean;
import com.joyintech.tams.exchange.common.bean.TickBean;
import com.joyintech.tams.exchange.common.constant.*;
import com.joyintech.tams.exchange.common.constant.joyin.DictCodeList;
import com.joyintech.tams.exchange.common.db.domain.Instruction;
import com.joyintech.tams.exchange.common.db.domain.Instrument;
import com.joyintech.tams.exchange.common.db.domain.OrderInterface;
import com.joyintech.tams.exchange.common.enums.invest.InvestAssetTypeEnum;
import com.joyintech.tams.exchange.common.utils.TradeUtils;
import com.joyintech.tams.exchange.datacommon.db.service.InstrumentService;
import com.joyintech.tams.exchange.service.trade.processor.business.bond.BondHelper;
import com.joyintech.tams.exchange.service.trade.processor.business.fund.FundHelper;
import com.joyintech.tams.exchange.service.trade.processor.business.price.PriceHelper;
import com.joyintech.tams.exchange.service.trade.processor.business.stock.StockHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;

/**
 * @ClassName BulkTrade
 * @Description 大宗交易数检查
 * @Author liujianping
 * @Date 2020/9/14
 * @Version
 **/
@Component
public class BulkTradeHelper {
    private static final Logger logger = LoggerFactory.getLogger(BulkTradeHelper.class);

    @Autowired
    private InstrumentService instrumentService;
//    @Autowired
//    private QuoteTickService quoteTickService;
    @Autowired
    private QuoteService quoteService;
    @Autowired
    private StockHelper stockHelper;
    @Autowired
    private FundHelper fundHelper;
    @Autowired
    private BondHelper bondHelper;
    @Autowired
    private PriceHelper priceHelper;

    public boolean checkCommonParam(BigDecimal entrustPrice, long entrustVolume, OrderInterface orderInterface, Instrument instrument, String market, BigDecimal entrustBalance, Instruction instruction) {
        String assetType = orderInterface.getAssetType();
        BigInteger bgEntrustVolume = BigInteger.valueOf(entrustVolume);
        MinTrdUnitLimit minTrdUnitLimit = instrumentService.getMinTrdUnitLimit(instruction.getTradePlat(), instrument.getMarket(), instrument, instruction.getPs());
        if (Objects.isNull(minTrdUnitLimit)) {
            logger.info("最小交易单位、交易上下限获取失败：TradePlat {}  instrument {}  instructionP {}", instruction.getTradePlat(), instrument.getInnerCode(), instruction.getPs());
        }
        //判断标的为股票、债券、基金、债券质押式回购期限代码
        TradePlat tradePlat = null;
        if (Market.SZSE.value.equals(market)) {
            tradePlat = TradePlat.SZ_PROTOCOL;
        } else if (Market.SHSE.value.equals(market)) {
            tradePlat = TradePlat.SH_SCALE;
        }
        if (InvestAssetTypeEnum.STOCK.value.equals(assetType)) { //股票
            if (!stockHelper.checkCommonParam(entrustPrice, entrustVolume, orderInterface, instrument, instruction.getTradePlat(), instruction.getPs(), entrustBalance, instruction.getEntrustBussType(), instruction.getPriceMode())) {
                return false;
            }
            if (compareMinOrMaxVolumeAndBalance(orderInterface, entrustBalance, bgEntrustVolume, minTrdUnitLimit, InvestAssetTypeEnum.STOCK, tradePlat))
                return false;
        } else if (InvestAssetTypeEnum.FUND.value.equals(assetType)) { //基金
            if (!fundHelper.checkCommonParam(entrustPrice, entrustVolume, orderInterface, instrument, instruction.getTradePlat(), instruction.getPs(), entrustBalance, instruction.getEntrustBussType(), instruction.getPriceMode())) {
                return false;
            }
            if (compareMinOrMaxVolumeAndBalance(orderInterface, entrustBalance, bgEntrustVolume, minTrdUnitLimit, InvestAssetTypeEnum.FUND, tradePlat))
                return false;
        } else if (InvestAssetTypeEnum.BOND.value.equals(assetType)) { //债券
            if (!bondHelper.checkCommonParam(orderInterface.getFullPrice(), entrustPrice, entrustVolume, orderInterface, instrument, instruction.getTradePlat(), instruction.getPs(), entrustBalance, instruction.getEntrustBussType(), instruction.getPriceMode())) {
                return false;
            }
           /* if (!checkBondPriceLimit(orderInterface, entrustPrice)) {
                return false;
            }*/
            if (compareMinOrMaxVolumeAndBalance(orderInterface, entrustBalance, bgEntrustVolume, minTrdUnitLimit, InvestAssetTypeEnum.BOND, tradePlat))
                return false;
        } else if (InvestAssetTypeEnum.PLEDGE_REPURCHASE.value.equals(assetType)) { //质押式回购
            if (!checkUnitVol(bgEntrustVolume, orderInterface, instrument)) {
                return false;
            }
            //回购不区分全价和净价
            if (!checkUnitPrice(entrustPrice, orderInterface, instrument)) {
                return false;
            }
            if (!checkBondPriceLimit(entrustPrice, orderInterface, instrument, instruction.getTradePlat(), instruction.getEntrustBussType(), instruction.getPriceMode())) {
                return false;
            }
            if (Objects.nonNull(minTrdUnitLimit)) {
                if (compareMinOrMaxVolumeAndBalance(orderInterface, entrustBalance, bgEntrustVolume, minTrdUnitLimit, InvestAssetTypeEnum.BOND, tradePlat))
                    return false;
            }
        } else {
            orderInterface.setErrorCode(ErrorCode.BULKTRADE_ASSET_TYPE_ERROR.value);
            orderInterface.setErrorMsg(ErrorCode.BULKTRADE_ASSET_TYPE_ERROR.errorMsg);
            return false;
        }
        //交易平台为上交所大宗交易平台或者深交所综合协议平台
        if (!checkTradePlat(orderInterface, instruction)) {
            return false;
        }
        //资产二级分类不为H01-质押式协议回购
        if (!checkH01(orderInterface, instruction)) {
            return false;
        }
        return true;
    }

    private boolean compareMinOrMaxVolumeAndBalance(OrderInterface orderInterface, BigDecimal entrustBalance, BigInteger bgEntrustVolume, MinTrdUnitLimit minTrdUnitLimit, InvestAssetTypeEnum investAssetTypeEnum, TradePlat tradePlat) {
        if (Objects.nonNull(minTrdUnitLimit)) {
            if (bgEntrustVolume.compareTo(BigInteger.valueOf(minTrdUnitLimit.getMinNum())) < 0 && entrustBalance.compareTo(minTrdUnitLimit.getMinAmt()) < 0) {
                orderInterface.setErrorCode(ErrorCode.INNER_RISK_ERROR.value);
                orderInterface.setErrorMsg("不满足" + tradePlat.name + investAssetTypeEnum.name + "大宗交易最小买入数量:" + minTrdUnitLimit.getMinNum() + ",或者金额" + minTrdUnitLimit.getMinAmt());
                return true;
            }
            if (bgEntrustVolume.compareTo(BigInteger.valueOf(minTrdUnitLimit.getMaxNum())) > 0 || entrustBalance.compareTo(minTrdUnitLimit.getMaxAmt()) > 0) {
                orderInterface.setErrorCode(ErrorCode.INNER_RISK_ERROR.value);
                orderInterface.setErrorMsg("超过" + tradePlat.name + investAssetTypeEnum.name + "大宗交易最大买入数量:" + minTrdUnitLimit.getMaxNum() + ",或者金额" + minTrdUnitLimit.getMaxAmt());
                return true;
            }
        }
        return false;
    }

    //检查报单数量是否为最小报价整数倍
    private boolean checkUnitVol(BigInteger bgEntrustVolume, OrderInterface orderInterface, Instrument instrument) {
        if (EntrustBs.BUY.value.equals(orderInterface.getEntrustBs())
                && instrument.getBuyUnit() > 0
                && bgEntrustVolume.divideAndRemainder(BigInteger.valueOf(instrument.getBuyUnit()))[1].compareTo(BigInteger.ZERO) != 0) {
            orderInterface.setErrorCode(ErrorCode.INNER_RISK_ERROR.value);
            orderInterface.setErrorMsg("不满足最小买入单位的整数倍，报单数量:" + bgEntrustVolume.longValue() + ",买入最小单位:" + instrument.getBuyUnit());
            return false;
        }
        return true;
    }

    //检查报单价格是否为最小报价整数倍
    private boolean checkUnitPrice(BigDecimal entrustPrice, OrderInterface orderInterface, Instrument instrument) {
        if (Double.compare(instrument.getPriceStep().doubleValue(), 0.0) > 0
                && TradeUtils.divideAndRemainder(entrustPrice, instrument.getPriceStep()).compareTo(BigDecimal.ZERO) != 0) {
            orderInterface.setErrorCode(ErrorCode.INNER_RISK_ERROR.value());
            orderInterface.setErrorMsg("委托价格不满足最小价差：" + instrument.getPriceStep().doubleValue());
            return false;
        }
        return true;
    }

    //检查交易方向是否为买入、卖出、正回购、逆回购
    private boolean checkBS(OrderInterface orderInterface) {
        String bs = orderInterface.getEntrustBs();
        if (!EntrustBs.BUY.value.equals(bs)
                && !EntrustBs.SELL.value.equals(bs)) {
            orderInterface.setErrorCode(ErrorCode.BULKTRADE_BS_ERROR.value);
            orderInterface.setErrorMsg(ErrorCode.BULKTRADE_BS_ERROR.errorMsg);
            return false;
        }
        return true;
    }

    //检查交易所平台
    private boolean checkTradePlat(OrderInterface orderInterface, Instruction instruction) {
        if (!TradePlat.SH_SCALE.value.equals(instruction.getTradePlat())
                && !TradePlat.SZ_PROTOCOL.value.equals(instruction.getTradePlat())) {
            orderInterface.setErrorCode(ErrorCode.BULKTRADE_PLAT_ERROR.value);
            orderInterface.setErrorMsg(ErrorCode.BULKTRADE_PLAT_ERROR.errorMsg);
            return false;
        }
        return true;
    }

    //检查资产二级分类（assetType1）不为H01-质押式协议回购
    private boolean checkH01(OrderInterface orderInterface, Instruction instruction) {
        if (DictCodeList.回购类型_质押式协议回购.getValue().equals(instruction.getAssetType1())) {
            orderInterface.setErrorCode(ErrorCode.BULKTRADE_ASSETTYPE1_ERROR.value);
            orderInterface.setErrorMsg(ErrorCode.BULKTRADE_ASSETTYPE1_ERROR.errorMsg);
            return false;
        }
        return true;
    }

    //检查交易价格涨跌停限制
    private boolean checkPriceLimit(OrderInterface orderInterface, BigDecimal entrustPrice) {
        ResultBean tickBean = quoteService.getTick(orderInterface.getStockCode());
        if (ErrorCode.isSuccess(tickBean.getErrorCode())) {
            TickBean tick = (TickBean)tickBean.getData();
//        QuoteProto.TickBean tick = quoteTickService.getTickFromCache(orderInterface.getStockCode());
//        if (Objects.nonNull(tick)) {
            double lowerLimitPrice = TradeUtils.formatDouble(tick.getLowerLimitPrice(), 4);
            double upperLimitPrice = TradeUtils.formatDouble(tick.getUpperLimitPrice(), 4);
            if ((Double.compare(lowerLimitPrice, 0.0) > 0 && Double.compare(lowerLimitPrice, entrustPrice.doubleValue()) > 0)
                    || (Double.compare(upperLimitPrice, 0.0) > 0 && Double.compare(upperLimitPrice, entrustPrice.doubleValue()) < 0)) {
                orderInterface.setErrorCode(ErrorCode.INNER_RISK_ERROR.value);
                orderInterface.setErrorMsg("不满足涨跌停限制。 跌停价：[" + lowerLimitPrice + "] -- 涨停价：[" + upperLimitPrice + "]; 委托价格：[" + entrustPrice + "]");
                return false;
            }
        }
        return true;
    }

    //检查债券交易价格上下限
    private boolean checkBondPriceLimit(OrderInterface orderInterface, BigDecimal entrustPrice) {
        //（min（最低价（债券五档行情中的最低价），收盘价x70%）<=price<=max（最高价（债券五档行情中的最高价），收盘价x130%））
        ResultBean tickBean = quoteService.getTick(orderInterface.getStockCode());
        if (ErrorCode.isSuccess(tickBean.getErrorCode())) {
            TickBean tick = (TickBean)tickBean.getData();
//        QuoteProto.TickBean tick = quoteTickService.getTickFromCache(orderInterface.getStockCode());
//        if (Objects.nonNull(tick)) {
            double lowerLimitPrice = TradeUtils.formatDouble(tick.getPreClosePrice() * 0.7, 4);
            double upperLimitPrice = TradeUtils.formatDouble(tick.getPreClosePrice() * 1.3, 4);
            lowerLimitPrice = Math.min(lowerLimitPrice, TradeUtils.formatDouble(tick.getLowPrice(), 4));
            upperLimitPrice = Math.max(upperLimitPrice, TradeUtils.formatDouble(tick.getHighPrice(), 4));
            String isFullPrice = orderInterface.getIsFullPrice();
            double fullPrice = orderInterface.getFullPrice().doubleValue();
            if (!FullPriceConst.FULL_PRICE.value.equals(isFullPrice) && (Double.compare(lowerLimitPrice, 0.0) > 0 && Double.compare(lowerLimitPrice, entrustPrice.doubleValue()) > 0)
                    || (Double.compare(upperLimitPrice, 0.0) > 0 && Double.compare(upperLimitPrice, entrustPrice.doubleValue()) < 0)) {
                orderInterface.setErrorCode(ErrorCode.INNER_RISK_ERROR.value);
                orderInterface.setErrorMsg("不满足债券大宗交易价格限制。 跌停价：[" + lowerLimitPrice + "] -- 涨停价：[" + upperLimitPrice + "]; 委托价格：[" + entrustPrice + "]");
                return false;
            }
            if (FullPriceConst.FULL_PRICE.value.equals(isFullPrice) && (((Double.compare(lowerLimitPrice, 0.0) > 0 && Double.compare(lowerLimitPrice, fullPrice) > 0)
                    || (Double.compare(upperLimitPrice, 0.0) > 0 && Double.compare(upperLimitPrice, fullPrice) < 0)))) {
                orderInterface.setErrorCode(ErrorCode.INNER_RISK_ERROR.value);
                orderInterface.setErrorMsg("全价不满足债券大宗交易价格限制。 跌停价：[" + lowerLimitPrice + "] -- 涨停价：[" + upperLimitPrice + "]; 委托全价价格：[" + fullPrice + "]");
                return false;
            }
        }
        return true;
    }

    //债券交易价格上下限
    private boolean checkBondPriceLimit(BigDecimal entrustPrice, OrderInterface orderInterface, Instrument instrument, String tradePlat, String entrustBussType, String priceMode) {
        logger.info("没有涨跌幅");
        boolean checkPrice = false;
        String isFullPrice = orderInterface.getIsFullPrice();
        ResultBean tickBean = quoteService.getTick(orderInterface.getStockCode());
        if (ErrorCode.isSuccess(tickBean.getErrorCode())) {
            TickBean tick = (TickBean) tickBean.getData();
//        QuoteProto.TickBean tick = quoteTickService.getTickFromCache(orderInterface.getStockCode());
            if (!FullPriceConst.FULL_PRICE.value.equals(isFullPrice)) {
                checkPrice = priceHelper.checkPrice(entrustPrice, orderInterface, instrument, tick, tradePlat, entrustBussType, priceMode);
            } else {
                checkPrice = priceHelper.checkPrice(orderInterface.getFullPrice(), orderInterface, instrument, tick, tradePlat, entrustBussType, priceMode);
            }
        }
        return !checkPrice;
    }
}
