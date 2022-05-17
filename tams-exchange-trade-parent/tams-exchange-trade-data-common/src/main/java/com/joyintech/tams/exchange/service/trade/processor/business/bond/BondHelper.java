package com.joyintech.tams.exchange.service.trade.processor.business.bond;

import com.joyintech.exchange.service.quote.service.QuoteService;
import com.joyintech.tams.exchange.common.bean.MinPrice;
import com.joyintech.tams.exchange.common.bean.MinTrdUnitLimit;
import com.joyintech.tams.exchange.common.bean.ResultBean;
import com.joyintech.tams.exchange.common.bean.TickBean;
import com.joyintech.tams.exchange.common.constant.*;
import com.joyintech.tams.exchange.common.db.domain.Instruction;
import com.joyintech.tams.exchange.common.db.domain.Instrument;
import com.joyintech.tams.exchange.common.db.domain.OrderInterface;
import com.joyintech.tams.exchange.common.db.domain.Trade;
import com.joyintech.tams.exchange.common.enums.invest.InvestAssetTypeEnum;
import com.joyintech.tams.exchange.common.utils.*;
import com.joyintech.tams.exchange.datacommon.db.service.InstrumentService;
import com.joyintech.tams.exchange.service.trade.management.service.InvestmentManagement;
import com.joyintech.tams.exchange.service.trade.processor.business.price.PriceHelper;
import com.joyintech.tams.exchange.service.trade.server.agent.AccountManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;

/**
 * 债券业务处理
 */

@Component
public class BondHelper {
    private static final Logger logger = LoggerFactory.getLogger(BondHelper.class);

    //    @Autowired
//    private QuoteTickService quoteTickService;
    @Autowired
    private QuoteService quoteService;
    @Autowired
    private InstrumentService instrumentService;
    @Autowired
    private InvestmentManagement investmentManagement;
    @Autowired
    private PriceHelper priceHelper;
    @Autowired
    private AccountManager accountManager;

    public boolean checkCommonParam(BigDecimal fullPrice, BigDecimal entrustPrice, long entrustVolume, OrderInterface orderInterface, Instrument instrument, String tradePlat, String instructionPs, BigDecimal entrustBalance, String entrustBussType, String priceMode) {
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
        boolean isFullPrice = false;
        // 申报类不需要check最小价差和涨跌停,
        // 质押券出入库不需要check最小价差和涨跌停 by lifangpeng 20220319
        if (!EntrustUtils.isNoTrade(orderInterface.getEntrustProp()) && !EntrustProp.ZYCRK.getValue().equals(orderInterface.getEntrustProp())) {
            MinPrice minPrice = instrumentService.getMinPrice(tradePlat, instrument.getMarket(), instrument);
            if (EntrustUtils.isFullPrice(instrument, orderInterface.getAssetType(), orderInterface.getEntrustProp(), orderInterface.getIsFullPrice())) {
                isFullPrice = true;
                if (Double.compare(minPrice.getMinPrice().doubleValue(), 0.0) > 0
                        && TradeUtils.divideAndRemainder(fullPrice, minPrice.getMinPrice()).compareTo(BigDecimal.ZERO) != 0) {
                    orderInterface.setErrorCode(ErrorCode.INNER_RISK_ERROR.value());
                    orderInterface.setErrorMsg("委托全价 {" + fullPrice + "} 不满足最小价差：" + minPrice.getMinPrice().doubleValue());
                    return false;
                }
                orderInterface.setIsFullPrice(FullPriceConst.FULL_PRICE.value);
            } else {
                orderInterface.setIsFullPrice(FullPriceConst.NET_PRICE.value);
            }
            if (Double.compare(minPrice.getMinPrice().doubleValue(), 0.0) > 0
                    && TradeUtils.divideAndRemainder(entrustPrice, minPrice.getMinPrice()).compareTo(BigDecimal.ZERO) != 0) {
                orderInterface.setErrorCode(ErrorCode.INNER_RISK_ERROR.value());
                orderInterface.setErrorMsg("委托价格 {" + entrustPrice + "} 不满足最小价差：" + minPrice.getMinPrice().doubleValue());
                return false;
            }
            ResultBean tickBean = quoteService.getTick(orderInterface.getStockCode());
            if (ErrorCode.isSuccess(tickBean.getErrorCode())) {
                TickBean tick = (TickBean) tickBean.getData();
//            QuoteProto.TickBean tick = quoteTickService.getTickFromCache(orderInterface.getStockCode());
//            if (Objects.nonNull(tick)) {
                double lowerLimitPrice = TradeUtils.formatDouble(tick.getLowerLimitPrice(), 4);
                double upperLimitPrice = TradeUtils.formatDouble(tick.getUpperLimitPrice(), 4);
                if (Double.compare(lowerLimitPrice, 0.0) == 0 && Double.compare(upperLimitPrice, 0.0) == 0) {
                    logger.info("没有涨跌幅");
                    boolean checkPrice;
                    if (!isFullPrice) {
                        checkPrice = priceHelper.checkPrice(entrustPrice, orderInterface, instrument, tick, tradePlat, entrustBussType, priceMode);
                    } else {
                        checkPrice = priceHelper.checkPrice(fullPrice, orderInterface, instrument, tick, tradePlat, entrustBussType, priceMode);
                    }
                    if (!checkPrice) {
                        return false;
                    }
                } else {
                    if (!isFullPrice && (Double.compare(lowerLimitPrice, 0.0) > 0 && Double.compare(lowerLimitPrice, entrustPrice.doubleValue()) > 0)
                            || (Double.compare(upperLimitPrice, 0.0) > 0 && Double.compare(upperLimitPrice, entrustPrice.doubleValue()) < 0)) {
                        orderInterface.setErrorCode(ErrorCode.INNER_RISK_ERROR.value);
                        orderInterface.setErrorMsg("不满足涨跌停限制。 跌停价：[" + lowerLimitPrice + "] -- 涨停价：[" + upperLimitPrice + "]; 委托价格：[" + entrustPrice + "]");
                        return false;
                    }
                    if (isFullPrice && (((Double.compare(lowerLimitPrice, 0.0) > 0 && Double.compare(lowerLimitPrice, fullPrice.doubleValue()) > 0)
                            || (Double.compare(upperLimitPrice, 0.0) > 0 && Double.compare(upperLimitPrice, fullPrice.doubleValue()) < 0)))) {
                        orderInterface.setErrorCode(ErrorCode.INNER_RISK_ERROR.value);
                        orderInterface.setErrorMsg("全价不满足涨跌停限制。 跌停价：[" + lowerLimitPrice + "] -- 涨停价：[" + upperLimitPrice + "]; 委托全价价格：[" + fullPrice + "]");
                        return false;
                    }
                }
            }
        }
        // 交易上下限检查
        if (TradePlat.NORMAL.value.equals(tradePlat) || TradePlat.SH_FIXED_INCOME.value.equals(tradePlat)) {
            String tradePlatName;
            if (TradePlat.NORMAL.value.equals(tradePlat)) {
                tradePlatName = TradePlat.NORMAL.name;
            } else {
                tradePlatName = TradePlat.SH_FIXED_INCOME.name;
            }
            return compareMinOrMaxVolumeAndBalance(orderInterface, entrustBalance, BigInteger.valueOf(entrustVolume), minTrdUnitLimit, tradePlatName);
        }
        return true;
    }

    private boolean compareMinOrMaxVolumeAndBalance(OrderInterface orderInterface, BigDecimal entrustBalance, BigInteger bgEntrustVolume, MinTrdUnitLimit minTrdUnitLimit, String tradePlatName) {
        if (Objects.nonNull(minTrdUnitLimit) && EntrustBs.BUY.value.equals(orderInterface.getEntrustBs())) {
            Integer minNum;
            if (EntrustProp.isMarketPrice(orderInterface.getEntrustProp())) {
                minNum = minTrdUnitLimit.getMinNumMarket();
            } else {
                minNum = minTrdUnitLimit.getMinNum();
            }
            if (bgEntrustVolume.compareTo(BigInteger.valueOf(minNum)) < 0 && entrustBalance.compareTo(minTrdUnitLimit.getMinAmt()) < 0) {
                orderInterface.setErrorCode(ErrorCode.INNER_RISK_ERROR.value);
                orderInterface.setErrorMsg("不满足" + tradePlatName + "债券交易最小买入数量:" + minNum + ",或者金额" + minTrdUnitLimit.getMinAmt());
                return false;
            }
        }
        return true;
    }

    // 债券成交全价处理 ------> 需求确认后恢复为全价
//    public BigDecimal handleTradePriceByFullPrice(Trade trade, String isFullPrice, Instruction instruction) {
//        if (IntegratedEntrustUtils.isProtocolBack(trade.getEntrustProp()) || trade.getTradedPrice().doubleValue() == 0) {
//            return trade.getTradedBalance();
//        }
//        // 对于金证和金仕达柜台 -- 净价报单 净价成交。 ufx按照委托中isFullPrice区分 净价成交 全价成交
//        Account account = accountManager.getAccountById(trade.getAccountId());
//        // 净价成交不需要处理
//        if (Objects.nonNull(account) && AgentType.isNetPriceTrade(account.getAgentType())) {
//            return trade.getTradedBalance();
//        }
//        if (Objects.nonNull(instruction) && EntrustUtils.isFullPrice(instruction.getAssetType(), trade.getEntrustProp(), isFullPrice)) {
//            logger.info("债券全价成交回报价格和成交金额处理，brokerEntrustId {} brokerTradeId {} tradePrice {} tradeBalance {}", trade.getBrokerEntrustId(), trade.getBrokerTradeId(), trade.getTradedPrice(), trade.getTradedBalance());
//            BigDecimal price = investmentManagement.queryEntrustPriceByFullPriceFromInvest(trade.getTradedPrice(), trade.getTradedVolume(), instruction, getPriceRoundByPriceStep(instrumentService.queryByInnerCode(instruction.getFinprodMarketId())));
//            BigDecimal calculateBalance = TradeUtils.multiply(price, trade.getTradedVolume(), 2);
//            logger.info("全价转换为净价完成，brokerEntrustId {} brokerTradeId {} tradePrice {} calculateBalance {} tradeVolume {}", trade.getBrokerEntrustId(), trade.getBrokerTradeId(), price, calculateBalance, trade.getTradedVolume());
//            return calculateBalance;
//        }
//        return trade.getTradedBalance();
//    }

    public BigDecimal handleTradePriceByFullPrice(Trade trade, String isFullPrice, Instruction instruction) {
        if (IntegratedEntrustUtils.isProtocolBack(trade.getEntrustProp()) || trade.getTradedPrice().doubleValue() == 0) {
            return trade.getTradedBalance();
        }

        if (InvestAssetTypeEnum.isBond(instruction.getAssetType())
                && EnumUtil.in(instruction.getPs(), InstructionPs.BUY, InstructionPs.SELL)
                && !EntrustUtils.isFullPrice(instruction.getAssetType(), trade.getEntrustProp(), isFullPrice)) {
            BigDecimal price = investmentManagement.queryFullPriceByEntrustPriceFromInvest(trade.getTradedPrice(), trade.getTradedVolume(), instruction, getPriceRoundByPriceStep(instrumentService.queryByInnerCode(instruction.getFinprodMarketId())));
            logger.info("handleTradePriceByFullPrice  trade fullPrice {}", price);
            return TradeUtils.multiply(price, trade.getTradedVolume(), 2);
        }
        return trade.getTradedBalance();
    }

    /**
     * 债券净价成交转换为全价处理
     */
//    public BigDecimal getFullPriceByTradePrice(Trade trade, String isFullPrice, Instruction instruction) {
//        if (!InvestAssetTypeEnum.isBond(instruction.getAssetType())) {
//            return BigDecimal.ZERO;
//        }
//        if (IntegratedEntrustUtils.isProtocolBack(trade.getEntrustProp()) || trade.getTradedPrice().doubleValue() == 0) {
//            return trade.getTradedPrice();
//        }
//        // 对于金证和金仕达柜台 -- 净价报单 净价成交。
//        // ufx按照委托中isFullPrice区分 净价成交 全价成交
//        Account account = accountManager.getAccountById(trade.getAccountId());
//        // 净价成交 转换为 全价
//        if (Objects.nonNull(account) && AgentType.isNetPriceTrade(account.getAgentType())
//                || (!EntrustUtils.isFullPrice(instruction.getAssetType(), trade.getEntrustProp(), isFullPrice))) {
//            BigDecimal price = investmentManagement.queryFullPriceByEntrustPriceFromInvest(trade.getTradedPrice(), trade.getTradedVolume(), instruction, getPriceRoundByPriceStep(instrumentService.queryByInnerCode(instruction.getFinprodMarketId())));
//            logger.info("getFullPriceByTradePrice  trade fullPrice {}", price);
//            return price;
//        }
//
//        return trade.getTradedPrice();
//    }
    public BigDecimal getFullPriceByTradePrice(Trade trade, String isFullPrice, Instruction instruction) {
        if (!InvestAssetTypeEnum.isBond(instruction.getAssetType())) {
            return BigDecimal.ZERO;
        }
        if (IntegratedEntrustUtils.isProtocolBack(trade.getEntrustProp()) || trade.getTradedPrice().doubleValue() == 0) {
            return trade.getTradedPrice();
        }
        /*
         * 新债调整
         * 1、净价品种
         * 委托记录中：委托价格（净价），买入冻结金额（全价），成交金额（全价）
         * 成交记录中：委托价格（净价），成交价格（净价），成交金额（全价），实时冻结解冻金额（全价）
         * 2、全价品种(可转债-全价品种)
         * 委托记录中：委托价格（全价），买入冻结金额（全价），成交金额（全价）
         * 成交记录中：委托价格（全价），成交价格（全价），成交金额（全价），实时冻结解冻金额（全价）
         */
        if (InvestAssetTypeEnum.isBond(instruction.getAssetType())
                && EnumUtil.in(instruction.getPs(), InstructionPs.BUY, InstructionPs.SELL)
                && !EntrustUtils.isFullPrice(instruction.getAssetType(), trade.getEntrustProp(), isFullPrice)) {
            BigDecimal minPrice = instruction.queryMinPrice(trade.getEntrustProp());
            int round = Objects.isNull(minPrice) ? 4 : minPrice.scale();
            BigDecimal price = investmentManagement.queryFullPriceByEntrustPriceFromInvest(trade.getTradedPrice(), trade.getTradedVolume(), instruction, round);
            logger.info("getFullPriceByTradePrice  trade fullPrice {}", price);
            return price;
        }

        return trade.getTradedPrice();
    }

    public BigDecimal getNetPriceByFullPrice(Trade trade, Instruction instruction) {
        BigDecimal minPrice = instruction.queryMinPrice(trade.getEntrustProp());
        int round = Objects.isNull(minPrice) ? 4 : minPrice.scale();
        BigDecimal price = investmentManagement.queryEntrustPriceByFullPriceFromInvest(trade.getFullPrice(), trade.getTradedVolume(), instruction, round);
        logger.info("getNetPriceByFullPrice  trade netPrice {}", price);
        return price;
    }

    public BigDecimal calculateBalanceForInstruction(String entrustProp, String assetType, BigDecimal entrustPrice, long volume, BigDecimal balance) {
        //协议回购金额计算
        if (IntegratedEntrustUtils.isProtocolBack(entrustProp)) {
            return balance;
        }
        // 质押式竞价回购，金额计算
        if (InvestAssetTypeEnum.PLEDGE_REPURCHASE.value.equals(assetType)) {
            return new BigDecimal(volume * 100).setScale(2, BigDecimal.ROUND_HALF_UP);
        }
        //债券指令 全部按照净价处理
        if (InvestAssetTypeEnum.BOND.value.equals(assetType)) {
            return TradeUtils.multiply(entrustPrice, volume, 2);
        }
        return balance;
    }

    public BigDecimal calculateBalanceForInstruction(OrderInterface orderInterface, long entrustVolume, BigDecimal balance) {
        //协议回购金额计算
        if (IntegratedEntrustUtils.isProtocolBack(orderInterface.getEntrustProp())) {
            return balance;
        }
        // 质押式竞价回购，金额计算
        if (InvestAssetTypeEnum.PLEDGE_REPURCHASE.value.equals(orderInterface.getAssetType())) {
            return new BigDecimal(entrustVolume * 100).setScale(2, BigDecimal.ROUND_HALF_UP);
        }
        //债券指令全部按照全价处理
        if (InvestAssetTypeEnum.BOND.value.equals(orderInterface.getAssetType())) {
            return TradeUtils.multiply(orderInterface.getFullPrice(), entrustVolume, 2);
        }
        return balance;
    }

    // 返回债券精度位数
    public int getPriceRoundByPriceStep(Instrument instrument) {
        return Objects.nonNull(instrument) ? (Objects.isNull(instrument.getPriceStep()) ? 4 : instrument.getPriceStep().scale()) : 4;
    }
}
