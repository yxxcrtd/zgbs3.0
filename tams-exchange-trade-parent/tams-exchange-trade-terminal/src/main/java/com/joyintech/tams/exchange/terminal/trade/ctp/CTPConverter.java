package com.joyintech.tams.exchange.terminal.trade.ctp;

import com.google.common.base.Strings;
import com.joyintech.tams.exchange.common.bean.FuturesInstrumentCommissionRateBean;
import com.joyintech.tams.exchange.common.bean.FuturesPositionBean;
import com.joyintech.tams.exchange.common.bean.FuturesPositionDetailBean;
import com.joyintech.tams.exchange.common.constant.*;
import com.joyintech.tams.exchange.common.db.domain.Entrust;
import com.joyintech.tams.exchange.common.db.domain.FuturesInstrument;
import com.joyintech.tams.exchange.common.db.domain.FuturesInstrumentMarginRateBean;
import com.joyintech.tams.exchange.common.db.domain.Trade;
import com.joyintech.tams.exchange.common.enums.invest.InvestAssetTypeEnum;
import com.joyintech.tams.exchange.common.utils.JsonUtils;
import com.joyintech.tams.exchange.common.utils.StockCodeUtils;
import com.joyintech.tams.exchange.terminal.trade.EntrustIdManager;
import net.jctp.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;


public class CTPConverter {
    private final static Logger logger = LoggerFactory.getLogger(CTPConverter.class);

    public static String formatSysId(String input, int dev) {
        return String.format("% " + String.valueOf(dev) + "d", Integer.valueOf(input));
    }

    public static List<FuturesPositionBean> convertToFuturesPosition(List<Object> objects, String accountId) {
        List<FuturesPositionBean> futuresPositionBeans = new ArrayList();
        for (Object obj : objects) {
            CThostFtdcInvestorPositionField investorPositionField = (CThostFtdcInvestorPositionField) obj;
            FuturesPositionBean futuresPositionBean = new FuturesPositionBean();
            futuresPositionBean.setAccountId(accountId);
            futuresPositionBean.setFinanceAccount(investorPositionField.InvestorID);
            futuresPositionBean.setMarket(investorPositionField.ExchangeID);
            futuresPositionBean.setStockCode(StockCodeUtils.formatStockCode(investorPositionField.InstrumentID, investorPositionField.ExchangeID));
            futuresPositionBean.setHedgeFlag(String.valueOf(investorPositionField.HedgeFlag));
            ///净 '1'
            ///多头 '2'
            ///空头 '3'
            if ('2' == investorPositionField.PosiDirection) {
                futuresPositionBean.setEntrustBs(EntrustBs.BUY.value);
            } else if ('3' == investorPositionField.PosiDirection) {
                futuresPositionBean.setEntrustBs(EntrustBs.SELL.value);
            }
            ///今日持仓'1'
            ///历史持仓 '2'
            futuresPositionBean.setPositionDate(String.valueOf(investorPositionField.PositionDate));
            futuresPositionBean.setYdPosition(investorPositionField.YdPosition);
            futuresPositionBean.setPosition(investorPositionField.Position);
            futuresPositionBean.setLongFrozen(investorPositionField.LongFrozen);
            futuresPositionBean.setShortFrozen(investorPositionField.ShortFrozen);
            futuresPositionBean.setLongFrozenAmount(investorPositionField.LongFrozenAmount);
            futuresPositionBean.setShortFrozenAmount(investorPositionField.ShortFrozenAmount);
            futuresPositionBean.setOpenVolume(investorPositionField.OpenVolume);
            futuresPositionBean.setCloseVolume(investorPositionField.CloseVolume);
            futuresPositionBean.setOpenAmount(investorPositionField.OpenAmount);
            futuresPositionBean.setCloseAmount(investorPositionField.CloseAmount);
            futuresPositionBean.setPositionCost(investorPositionField.PositionCost);
            futuresPositionBean.setPreMargin(investorPositionField.PreMargin);
            futuresPositionBean.setUseMargin(investorPositionField.UseMargin);
            futuresPositionBean.setFrozenMargin(investorPositionField.FrozenMargin);
            futuresPositionBean.setFrozenCash(investorPositionField.FrozenCash);
            futuresPositionBean.setFrozenCommission(investorPositionField.FrozenCommission);
            futuresPositionBean.setCashIn(investorPositionField.CashIn);
            futuresPositionBean.setCommission(investorPositionField.Commission);
            futuresPositionBean.setCloseProfit(investorPositionField.CloseProfit);
            futuresPositionBean.setPositionProfit(investorPositionField.PositionProfit);
            futuresPositionBean.setPreSettlementPrice(investorPositionField.PreSettlementPrice);
            futuresPositionBean.setSettlementPrice(investorPositionField.SettlementPrice);
            futuresPositionBean.setTradingDay(investorPositionField.TradingDay);
            futuresPositionBean.setSettlementID(investorPositionField.InstrumentID);
            futuresPositionBean.setOpenCost(investorPositionField.OpenCost);
            futuresPositionBean.setExchangeMargin(investorPositionField.ExchangeMargin);
            futuresPositionBean.setCombPosition(investorPositionField.CombPosition);
            futuresPositionBean.setCombLongFrozen(investorPositionField.CombLongFrozen);
            futuresPositionBean.setCombShortFrozen(investorPositionField.CombShortFrozen);
            futuresPositionBean.setCloseProfitByDate(investorPositionField.CloseProfitByDate);
            futuresPositionBean.setCloseProfitByTrade(investorPositionField.CloseProfitByTrade);
            futuresPositionBean.setTodayPosition(investorPositionField.TodayPosition);
            futuresPositionBean.setMarginRateByMoney(investorPositionField.MarginRateByMoney);
            futuresPositionBean.setMarginRateByVolume(investorPositionField.MarginRateByVolume);
            futuresPositionBean.setStrikeFrozen(investorPositionField.StrikeFrozen);
            futuresPositionBean.setStrikeFrozenAmount(investorPositionField.StrikeFrozenAmount);
            futuresPositionBean.setAbandonFrozen(investorPositionField.AbandonFrozen);
            futuresPositionBean.setYdStrikeFrozen(investorPositionField.YdStrikeFrozen);
            futuresPositionBean.setInvestUnitID(investorPositionField.InvestUnitID);
            futuresPositionBean.setPositionCostOffset(investorPositionField.PositionCostOffset);

            futuresPositionBeans.add(futuresPositionBean);
        }
        return futuresPositionBeans;
    }

    public static List<FuturesPositionDetailBean> convertToFuturesPositionDetail(List<Object> objects, String accountId) {
        List<FuturesPositionDetailBean> futuresPositionDetailBeans = new ArrayList();
        for (Object obj : objects) {
            CThostFtdcInvestorPositionDetailField investorPositionDetailField = (CThostFtdcInvestorPositionDetailField) obj;
            FuturesPositionDetailBean futuresPositionDetailBean = new FuturesPositionDetailBean();
            futuresPositionDetailBean.setAccountId(accountId);
            futuresPositionDetailBean.setFinanceAccount(investorPositionDetailField.InvestorID);
            futuresPositionDetailBean.setMarket(investorPositionDetailField.ExchangeID);
            futuresPositionDetailBean.setStockCode(StockCodeUtils.formatStockCode(investorPositionDetailField.InstrumentID, investorPositionDetailField.ExchangeID));
            futuresPositionDetailBean.setHedgeFlag(String.valueOf(investorPositionDetailField.HedgeFlag));
            ///买 '0'
            ///卖 '1'
            if ('0' == investorPositionDetailField.Direction) {
                futuresPositionDetailBean.setEntrustBs(EntrustBs.BUY.value);
            } else if ('1' == investorPositionDetailField.Direction) {
                futuresPositionDetailBean.setEntrustBs(EntrustBs.SELL.value);
            }
            futuresPositionDetailBean.setOpenDate(investorPositionDetailField.OpenDate);
            futuresPositionDetailBean.setTradeId(investorPositionDetailField.TradeID);
            futuresPositionDetailBean.setVolume(investorPositionDetailField.Volume);
            futuresPositionDetailBean.setOpenPrice(investorPositionDetailField.OpenPrice);
            futuresPositionDetailBean.setTradingDay(investorPositionDetailField.TradingDay);
            futuresPositionDetailBean.setSettlementID(String.valueOf(investorPositionDetailField.SettlementID));
            futuresPositionDetailBean.setTradeType(String.valueOf(investorPositionDetailField.TradeType));
            futuresPositionDetailBean.setCombInstrumentId(investorPositionDetailField.CombInstrumentID);
            futuresPositionDetailBean.setCloseProfitByDate(investorPositionDetailField.CloseProfitByDate);
            futuresPositionDetailBean.setCloseProfitByTrade(investorPositionDetailField.CloseProfitByTrade);
            futuresPositionDetailBean.setPositionProfitByDate(investorPositionDetailField.PositionProfitByDate);
            futuresPositionDetailBean.setPositionProfitByTrade(investorPositionDetailField.PositionProfitByTrade);
            futuresPositionDetailBean.setMargin(investorPositionDetailField.Margin);
            futuresPositionDetailBean.setExchMargin(investorPositionDetailField.ExchMargin);
            futuresPositionDetailBean.setMarginRateByMoney(investorPositionDetailField.MarginRateByMoney);
            futuresPositionDetailBean.setMarginRateByVolume(investorPositionDetailField.MarginRateByVolume);
            futuresPositionDetailBean.setSettlementPrice(investorPositionDetailField.SettlementPrice);
            futuresPositionDetailBean.setLastSettlementPrice(investorPositionDetailField.LastSettlementPrice);
            futuresPositionDetailBean.setCloseVolume(investorPositionDetailField.CloseVolume);
            futuresPositionDetailBean.setCloseAmount(investorPositionDetailField.CloseAmount);
            futuresPositionDetailBean.setTimeFirstVolume(investorPositionDetailField.TimeFirstVolume);
            futuresPositionDetailBean.setInvestUnitID(investorPositionDetailField.InvestUnitID);

            futuresPositionDetailBeans.add(futuresPositionDetailBean);
        }
        return futuresPositionDetailBeans;
    }

    public static List<FuturesInstrument> convertToFuturesInstrument(List<Object> instrumentFields) {
        List<FuturesInstrument> futuresInstruments = new ArrayList<>();
        for (Object object : instrumentFields) {
            CThostFtdcInstrumentField item = (CThostFtdcInstrumentField) object;
            FuturesInstrument futuresInstrument = new FuturesInstrument();
            futuresInstrument.setInnerCode(StockCodeUtils.formatStockCode(item.InstrumentID, item.ExchangeID));
            futuresInstrument.setInstrumentId(item.InstrumentID);
            futuresInstrument.setExchangeId(item.ExchangeID);
            futuresInstrument.setInstrumentName(item.InstrumentName);
            futuresInstrument.setExchangeInstId(item.ExchangeInstID);
            futuresInstrument.setProductId(item.ProductID);
            futuresInstrument.setProductClass(String.valueOf(item.ProductClass));
            futuresInstrument.setDeliveryYear(item.DeliveryYear);
            futuresInstrument.setDeliveryMonth(item.DeliveryMonth);
            futuresInstrument.setMaxMarketOrderVolume(item.MaxMarketOrderVolume);
            futuresInstrument.setMinMarketOrderVolume(item.MinMarketOrderVolume);
            futuresInstrument.setMaxLimitOrderVolume(item.MaxLimitOrderVolume);
            futuresInstrument.setMinLimitOrderVolume(item.MinLimitOrderVolume);
            futuresInstrument.setVolumeMultiple(item.VolumeMultiple);
            futuresInstrument.setPriceTick(compareDoubleMaxMinValue(item.PriceTick));
            futuresInstrument.setCreateDate(item.CreateDate);
            futuresInstrument.setOpenDate(item.OpenDate);
            futuresInstrument.setExpireDate(item.ExpireDate);
            futuresInstrument.setStartDelivDate(item.StartDelivDate);
            futuresInstrument.setEndDelivDate(item.EndDelivDate);
            futuresInstrument.setInstLifePhase(String.valueOf(item.InstLifePhase));
            futuresInstrument.setIsTrading(item.IsTrading ? 1 : 0);
            futuresInstrument.setPositionType(String.valueOf(item.PositionType));
            futuresInstrument.setPositionDateType(String.valueOf(item.PositionDateType));
            futuresInstrument.setLongMarginRatio(compareDoubleMaxMinValue(item.LongMarginRatio));
            futuresInstrument.setShortMarginRatio(compareDoubleMaxMinValue(item.ShortMarginRatio));
            futuresInstrument.setMaxMarginSIdeAlgorithm(String.valueOf(item.MaxMarginSideAlgorithm));
            futuresInstrument.setUnderlyingInstrId(item.UnderlyingInstrID);
            futuresInstrument.setStrikePrice(compareDoubleMaxMinValue(item.StrikePrice));
            futuresInstrument.setOptionsType(String.valueOf(item.OptionsType));
            futuresInstrument.setUnderlyingMultiple(compareDoubleMaxMinValue(item.UnderlyingMultiple));
            futuresInstrument.setCombinationType(String.valueOf(item.CombinationType));

            futuresInstruments.add(futuresInstrument);
        }
        return futuresInstruments;
    }

    private static BigDecimal compareDoubleMaxMinValue(double doubleValue) {
        return BigDecimal.valueOf((Double.MAX_VALUE == doubleValue || Double.MIN_VALUE == doubleValue) ? 0.00 : doubleValue);
    }

    public static EntrustStatus convertToEntrustStatus(char orderStatus) {
        EntrustStatus entrustStatus;
        if ('0' == orderStatus) {///全部成交
            entrustStatus = EntrustStatus.SUCCEEDED;
        } else if ('1' == orderStatus) { ///部分成交还在队列中
            entrustStatus = EntrustStatus.PART_SUCCESS;
        } else if ('2' == orderStatus) { ///部分成交不在队列中
            entrustStatus = EntrustStatus.PART_SUCCESS;
        } else if ('3' == orderStatus) { ///未成交还在队列中 和撤单提交
            entrustStatus = EntrustStatus.REPORTED;
        } else if ('4' == orderStatus) { ///未成交不在队列中
            entrustStatus = EntrustStatus.REPORTED;
        } else if ('5' == orderStatus) { ///撤单
            entrustStatus = EntrustStatus.CANCELED;
        } else if ('a' == orderStatus) { ///未知
            entrustStatus = EntrustStatus.REPORTED;
        } else if ('b' == orderStatus) { ///尚未触发
            entrustStatus = EntrustStatus.REPORTED;
        } else if ('c' == orderStatus) { ///已触发
            entrustStatus = EntrustStatus.REPORTED;
        } else {
            entrustStatus = EntrustStatus.TOBE_REPORTED;
        }
        return entrustStatus;
    }

    public static Entrust convertToEntrust(CThostFtdcOrderField orderField, EntrustIdManager entrustIdManager, String accountId) {
        Entrust entrust = new Entrust();
        entrust.setAccountId(accountId);
        entrust.setOffsetFlag(orderField.CombOffsetFlag);
        entrust.setHedgeFlag(orderField.CombHedgeFlag);//待数据验证
        if ('0' == orderField.Direction) {
            entrust.setEntrustBs(EntrustBs.BUY.value);
        } else if ('1' == orderField.Direction) {
            entrust.setEntrustBs(EntrustBs.SELL.value);
        }
        entrust.setFinanceAccount(orderField.UserID);
        entrust.setMarket(orderField.ExchangeID);
        entrust.setStockCode(StockCodeUtils.formatStockCode(orderField.InstrumentID, orderField.ExchangeID));
        entrust.setStockName(orderField.InstrumentID);
        entrust.setAccountName(orderField.UserID);
        entrust.setAssetType(AssetType.FUTURES.value);
        entrust.setLocalEntrustId(orderField.OrderRef);
        entrust.setBrokerEntrustId(orderField.OrderSysID);//需要补齐空格
        entrust.setCancelTime(orderField.CancelTime);
        entrust.setAssetType(InvestAssetTypeEnum.FUTURES.value);
        entrust.setNoCancelEntrust(true);
        if (Strings.isNullOrEmpty(entrust.getId())) {
            String id = entrustIdManager.getIdByLocalEntrustId(entrust.getAccountId(), orderField.OrderRef);
            if (Objects.nonNull(id)) {
                entrust.setId(id);
            } else {
                logger.error("can not find id by localEntrustId{},accountId:{}", orderField.OrderRef, entrust.getAccountId());
            }
        }
        logger.info("convertToEntrust:{}", JsonUtils.getJsonFromObject(entrust));
        return entrust;
    }

    public static Trade convertToTrade(CThostFtdcTradeField tradeField, EntrustIdManager entrustIdManager, String accountId) {
        Trade trade = new Trade();
        trade.setAccountId(accountId);
        trade.setOffsetFlag(String.valueOf(tradeField.OffsetFlag));
        trade.setHedgeFlag(String.valueOf(tradeField.HedgeFlag));
        if ('0' == tradeField.Direction) {
            trade.setEntrustBs(EntrustBs.BUY.value);
        } else if ('1' == tradeField.Direction) {
            trade.setEntrustBs(EntrustBs.SELL.value);
        }
        trade.setLocalEntrustId(tradeField.OrderRef);
        trade.setBrokerEntrustId(tradeField.OrderSysID);

        trade.setBrokerTradeId(tradeField.TradeID);
        trade.setTradedVolume(tradeField.Volume);
        trade.setTradedPrice(BigDecimal.valueOf(tradeField.Price));
        trade.setFinanceAccount(tradeField.UserID);
        trade.setMarket(tradeField.ExchangeID);
        trade.setStockCode(tradeField.InstrumentID);
        trade.setTradingDay(tradeField.TradingDay);
        trade.setTradeTime(tradeField.TradeTime);
        trade.setReportNo(String.valueOf(tradeField.SequenceNo));
        trade.setTradeType(TradeType.NORMAL.value);
        trade.setAssetType(InvestAssetTypeEnum.FUTURES.value);
        trade.setNoCancelEntrust(true);
        if (Strings.isNullOrEmpty(trade.getEntrustId())) {
            String id = entrustIdManager.getIdByLocalEntrustId(trade.getAccountId(), tradeField.OrderRef);
            if (Objects.nonNull(id)) {
                trade.setEntrustId(id);
            } else {
                logger.error("can not find id by localEntrustId{},accountId:{}", tradeField.OrderRef, trade.getAccountId());
            }
        }
        logger.info("convertToTrade:{}", JsonUtils.getJsonFromObject(trade));
        return trade;
    }

    public static Trade convertEntrustToTrade(CThostFtdcOrderField orderField, EntrustIdManager entrustIdManager, String accountId) {
        Trade trade = new Trade();
        trade.setAccountId(accountId);
        trade.setOffsetFlag(orderField.CombOffsetFlag);
        trade.setHedgeFlag(orderField.CombHedgeFlag);//待数据验证
        if ('0' == orderField.Direction) {
            trade.setEntrustBs(EntrustBs.BUY.value);
        } else if ('1' == orderField.Direction) {
            trade.setEntrustBs(EntrustBs.SELL.value);
        }

        trade.setLocalEntrustId(orderField.OrderRef);
        trade.setBrokerEntrustId(orderField.OrderSysID);//需要补齐空格
        if (!Strings.isNullOrEmpty(orderField.CancelTime)) {
            trade.setTradeTime(orderField.CancelTime);
        } else {
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
            Date date = new Date(System.currentTimeMillis());
            trade.setTradeTime(formatter.format(date));
        }

        trade.setTradedVolume(orderField.VolumeTotalOriginal - orderField.VolumeTraded);
        trade.setTradedPrice(BigDecimal.valueOf(orderField.LimitPrice));
        trade.setFinanceAccount(orderField.UserID);
        trade.setMarket(orderField.ExchangeID);
        trade.setStockCode(orderField.InstrumentID);
        trade.setTradingDay(orderField.TradingDay);
        trade.setTradeType(TradeType.CANCEL.value);
        trade.setAssetType(InvestAssetTypeEnum.FUTURES.value);
        trade.setNoCancelEntrust(true);
        if (Strings.isNullOrEmpty(trade.getEntrustId())) {
            String id = entrustIdManager.getIdByLocalEntrustId(trade.getAccountId(), orderField.OrderRef);
            if (Objects.nonNull(id)) {
                trade.setEntrustId(id);
            } else {
                logger.error("can not find id by localEntrustId{},accountId:{}", orderField.OrderRef, trade.getAccountId());
            }
        }
        logger.info("convertEntrustToTrade:{}", JsonUtils.getJsonFromObject(trade));
        return trade;
    }

    public static Trade convertInputOrderActionToTrade(CThostFtdcInputOrderActionField inputOrderActionField, String accountId) {
        Trade trade = new Trade();
        trade.setFinanceAccount(inputOrderActionField.InvestorID);
        trade.setAccountId(accountId);
        trade.setLocalEntrustId(inputOrderActionField.OrderRef);
        trade.setBrokerEntrustId(inputOrderActionField.OrderSysID);
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        trade.setTradeTime(formatter.format(date));
        trade.setTradedVolume(inputOrderActionField.VolumeChange);
        trade.setTradedPrice(BigDecimal.valueOf(inputOrderActionField.LimitPrice));
        trade.setFinanceAccount(inputOrderActionField.UserID);
        trade.setMarket(inputOrderActionField.ExchangeID);
        trade.setStockCode(inputOrderActionField.InstrumentID);
        trade.setTradeType(TradeType.CANCEL_INVALID.value);
        trade.setNoCancelEntrust(true);
        return trade;
    }

    public static Trade convertOrderActionToTrade(CThostFtdcOrderActionField var1, String accountId) {
        Trade trade = new Trade();
        trade.setFinanceAccount(var1.InvestorID);
        trade.setAccountId(accountId);
        trade.setLocalEntrustId(var1.OrderRef);
        trade.setBrokerEntrustId(var1.OrderSysID);//需要补齐空格
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        trade.setTradeTime(formatter.format(date));
        trade.setTradedVolume(var1.VolumeChange);
        trade.setTradedPrice(BigDecimal.valueOf(var1.LimitPrice));
        trade.setFinanceAccount(var1.UserID);
        trade.setMarket(var1.ExchangeID);
        trade.setStockCode(var1.InstrumentID);
        trade.setTradeType(TradeType.CANCEL_INVALID.value);
        trade.setNoCancelEntrust(true);
        return trade;
    }

    public static Entrust convertInputOrderToEntrust(CThostFtdcInputOrderField var1, CThostFtdcRspInfoField var2, EntrustIdManager entrustIdManager, String accountId) {
        Entrust entrust = new Entrust();
        entrust.setAccountId(accountId);
        entrust.setErrorCode(var2.ErrorID);
        entrust.setErrorMsg(var2.ErrorMsg);
        entrust.setEntrustStatus(EntrustStatus.INVALID.value);
        entrust.setNoCancelEntrust(true);
        if (Strings.isNullOrEmpty(entrust.getId())) {
            String id = entrustIdManager.getIdByLocalEntrustId(entrust.getAccountId(), var1.OrderRef);
            if (Objects.nonNull(id)) {
                entrust.setId(id);
            } else {
                logger.error("can not find id by localEntrustId{},accountId:{}", var1.OrderRef, entrust.getAccountId());
            }
        }
        return entrust;
    }

    public static List<FuturesInstrumentMarginRateBean> convertToFuturesInstrumentMarginRate(List<Object> instrumentFields, String accountId) {
        List<FuturesInstrumentMarginRateBean> futuresInstrumentMarginRateBeans = new ArrayList<>();
        for (Object object : instrumentFields) {
            CThostFtdcInstrumentMarginRateField item = (CThostFtdcInstrumentMarginRateField) object;
            FuturesInstrumentMarginRateBean futuresInstrumentMarginRateBean = new FuturesInstrumentMarginRateBean();

            futuresInstrumentMarginRateBean.setAccountId(accountId);
            futuresInstrumentMarginRateBean.setFinanceAccount(item.InvestorID);
            futuresInstrumentMarginRateBean.setStockCode(StockCodeUtils.formatStockCode(item.InstrumentID, item.ExchangeID));
            futuresInstrumentMarginRateBean.setMarket(item.ExchangeID);
            futuresInstrumentMarginRateBean.setBrokerId(item.BrokerID);
            futuresInstrumentMarginRateBean.setHedgeFlag(String.valueOf(item.HedgeFlag).replaceAll("[\u0000]", ""));
            futuresInstrumentMarginRateBean.setInvestorRange(String.valueOf(item.InvestorRange));
            futuresInstrumentMarginRateBean.setLongMarginRatioByMoney(item.LongMarginRatioByMoney);
            futuresInstrumentMarginRateBean.setLongMarginRatioByVolume(item.LongMarginRatioByVolume);
            futuresInstrumentMarginRateBean.setShortMarginRatioByMoney(item.ShortMarginRatioByMoney);
            futuresInstrumentMarginRateBean.setShortMarginRatioByVolume(item.ShortMarginRatioByVolume);
            futuresInstrumentMarginRateBean.setRelative(item.IsRelative);
            futuresInstrumentMarginRateBean.setInvestUnitId(item.InvestUnitID);

            futuresInstrumentMarginRateBeans.add(futuresInstrumentMarginRateBean);
        }
        return futuresInstrumentMarginRateBeans;
    }

    public static List<FuturesInstrumentCommissionRateBean> convertToFuturesCommissionRate(List<Object> instrumentFields, String accountId) {
        List<FuturesInstrumentCommissionRateBean> futuresInstrumentCommissionRateBeans = new ArrayList<>();
        for (Object object : instrumentFields) {
            CThostFtdcInstrumentCommissionRateField item = (CThostFtdcInstrumentCommissionRateField) object;
            FuturesInstrumentCommissionRateBean futuresInstrumentCommissionRateBean = new FuturesInstrumentCommissionRateBean();

            futuresInstrumentCommissionRateBean.setAccountId(accountId);
            futuresInstrumentCommissionRateBean.setFinanceAccount(item.InvestorID);
            futuresInstrumentCommissionRateBean.setStockCode(StockCodeUtils.formatStockCode(item.InstrumentID, item.ExchangeID));
            futuresInstrumentCommissionRateBean.setMarket(item.ExchangeID);
            futuresInstrumentCommissionRateBean.setBrokerId(item.BrokerID);
            futuresInstrumentCommissionRateBean.setInvestorRange(String.valueOf(item.InvestorRange));
            futuresInstrumentCommissionRateBean.setOpenRatioByMoney(item.OpenRatioByMoney);
            futuresInstrumentCommissionRateBean.setOpenRatioByVolume(item.OpenRatioByVolume);
            futuresInstrumentCommissionRateBean.setCloseRatioByMoney(item.CloseRatioByMoney);
            futuresInstrumentCommissionRateBean.setCloseRatioByVolume(item.CloseRatioByVolume);
            futuresInstrumentCommissionRateBean.setCloseTodayRatioByMoney(item.CloseTodayRatioByMoney);
            futuresInstrumentCommissionRateBean.setCloseTodayRatioByVolume(item.CloseTodayRatioByVolume);
            futuresInstrumentCommissionRateBean.setBizType(String.valueOf(item.BizType));
            futuresInstrumentCommissionRateBean.setInvestUnitId(item.InvestUnitID);

            futuresInstrumentCommissionRateBeans.add(futuresInstrumentCommissionRateBean);
        }
        return futuresInstrumentCommissionRateBeans;
    }
}
