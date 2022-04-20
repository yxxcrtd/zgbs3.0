package com.joyintech.tams.exchange.service.trade.processor.business.fund;

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
import com.joyintech.tams.exchange.common.db.domain.Instrument;
import com.joyintech.tams.exchange.common.db.domain.OrderInterface;
import com.joyintech.tams.exchange.common.proto.QuoteProto;
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
 * 基金竞价业务处理
 */

@Component
public class FundHelper {
    private static final Logger logger = LoggerFactory.getLogger(FundHelper.class);

//    @Autowired
//    private QuoteTickService quoteTickService;
    @Autowired
    private QuoteService quoteService;
    @Autowired
    private InstrumentService instrumentService;
    @Autowired
    private PriceHelper priceHelper;

    public boolean checkCommonParam(BigDecimal entrustPrice, long entrustVolume, OrderInterface orderInterface, Instrument instrument, String tradePlat, String instructionPs, BigDecimal entrustBalance, String entrustBussType, String priceMode) {
        if (EntrustBs.BUY.value.equals(orderInterface.getEntrustBs())
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
//            QuoteProto.TickBean tick = quoteTickService.getTickFromCache(bean.getStockCode());
            if (ErrorCode.isSuccess(tickBean.getErrorCode())) {
                TickBean tick = (TickBean)tickBean.getData();
//            QuoteProto.TickBean tick = quoteTickService.getTickFromCache(orderInterface.getStockCode());
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
        }
        // 交易数量及范围检查
        if (TradePlat.NORMAL.value.equals(tradePlat)) {
            MinTrdUnitLimit minTrdUnitLimit = instrumentService.getMinTrdUnitLimit(tradePlat, instrument.getMarket(), instrument, instructionPs);
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
                orderInterface.setErrorMsg("不满足沪深竞价撮合平台基金交易最小买入数量:" + minNum + ",或者金额" + minTrdUnitLimit.getMinAmt());
                return false;
            }
        }
        return true;
    }
}
