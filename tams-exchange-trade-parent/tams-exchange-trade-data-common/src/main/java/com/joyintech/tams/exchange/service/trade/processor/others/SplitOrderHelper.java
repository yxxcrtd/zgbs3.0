package com.joyintech.tams.exchange.service.trade.processor.others;

import com.joyintech.tams.exchange.common.bean.MinTrdUnitLimit;
import com.joyintech.tams.exchange.common.bean.investment.ReqRiskOccupy;
import com.joyintech.tams.exchange.common.constant.BusinessFlag;
import com.joyintech.tams.exchange.common.constant.EntrustProp;
import com.joyintech.tams.exchange.common.constant.InvestmentAssetType;
import com.joyintech.tams.exchange.common.constant.OrderStatus;
import com.joyintech.tams.exchange.common.db.domain.*;
import com.joyintech.tams.exchange.common.utils.EntrustUtils;
import com.joyintech.tams.exchange.common.utils.JsonUtils;
import com.joyintech.tams.exchange.common.utils.TradeUtils;
import com.joyintech.tams.exchange.datacommon.db.service.*;
import com.joyintech.tams.exchange.service.trade.processor.TradeMsgProcessor;
import com.joyintech.tams.exchange.service.trade.processor.business.common.EntrustCancelProcessor;
import com.joyintech.tams.exchange.service.trade.processor.msg.MsgType;
import com.joyintech.tams.exchange.service.trade.transaction.TransactionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.joyintech.tams.exchange.common.utils.TradeUtils.multiply;

/**
 * 自动拆单处理
 */
@Component
public class SplitOrderHelper {
    private static final Logger logger = LoggerFactory.getLogger(SplitOrderHelper.class);

    @Autowired
    private InstrumentService instrumentService;
    @Autowired
    private FuturesInstrumentService futuresInstrumentService;
    @Autowired
    private EntrustService entrustService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private EntrustCancelProcessor entrustCancelProcessor;
    @Autowired
    private TradeMsgProcessor tradeMsgProcessor;
    @Autowired
    private TransactionHelper transactionHelper;
    @Autowired
    private InstructionService instructionService;

    public List<Entrust> spiltOrder(Entrust entrust, int processType) {
        List<Entrust> entrustList = new ArrayList<>();
        entrustList.add(entrust);
        if (InvestmentAssetType.FUTURES.value.equals(entrust.getAssetType())) {
            FuturesInstrument futuresInstrument = futuresInstrumentService.queryByInnerCode(entrust.getStockCode());
            if (Objects.isNull(futuresInstrument)) {
                logger.info("委托：{} 没有找到对应的期货合约信息：{}", entrust.getId(), entrust.getStockCode());
                return entrustList;
            }
            long totalVolume = entrust.getEntrustVolume();
            if (EntrustProp.isMarketPrice(entrust.getEntrustProp())) {
                long marketHighVolume = futuresInstrument.getMaxMarketOrderVolume();
                if (marketHighVolume <= 0) {
                    logger.info("合约中市价最大可交易数量为零，不进行拆单操作：{}", JsonUtils.getJsonFromObject(futuresInstrument));
                    return entrustList;
                }
                if (totalVolume > marketHighVolume) {
                    return handleSpiltEntrustVolume(entrust, marketHighVolume, totalVolume, processType, futuresInstrument);
                }
            } else if (EntrustProp.NORMAL.value.equals(entrust.getEntrustProp())) {
                long highVolume = futuresInstrument.getMaxLimitOrderVolume();
                if (highVolume <= 0) {
                    logger.info("合约中限价最大可交易数量为零，不进行拆单操作：{}", JsonUtils.getJsonFromObject(futuresInstrument));
                    return entrustList;
                }
                if (totalVolume > highVolume) {
                    return handleSpiltEntrustVolume(entrust, highVolume, totalVolume, processType, futuresInstrument);
                }
            }
        } else {
            Instrument instrument = instrumentService.findById(entrust.getStockCode());
            if (Objects.isNull(instrument)) {
                logger.info("委托：{} 没有找到对应的合约信息：{}", entrust.getId(), entrust.getStockCode());
                return entrustList;
            }
            Instruction instruction = instructionService.findById(entrust.getInstructionId());
            MinTrdUnitLimit minTrdUnitLimit = instruction.queryMinTrdUnitLimit(entrust.getEntrustProp());
            if (Objects.isNull(minTrdUnitLimit)) {
                logger.error("交易上下限数据不存在，不进行拆单操作：instructionId={}", entrust.getInstructionId());
                return entrustList;
            }
            long totalVolume = entrust.getEntrustVolume();
            if (EntrustProp.isMarketPrice(entrust.getEntrustProp())) {
//                long marketHighVolume = instrument.getMarketHighVolume();
                long marketHighVolume = minTrdUnitLimit.getMaxNumMarket();
                if (marketHighVolume <= 0) {
                    logger.info("合约中市价最大可交易数量为零，不进行拆单操作：{}", JsonUtils.getJsonFromObject(minTrdUnitLimit));
                    return entrustList;
                }
                if (totalVolume > marketHighVolume) {
                    return handleSpiltEntrustVolume(entrust, marketHighVolume, totalVolume, processType, null);
                }
            } else if (EntrustProp.NORMAL.value.equals(entrust.getEntrustProp())) {
//                long highVolume = instrument.getHighVolume();
                long highVolume = minTrdUnitLimit.getMaxNum();
                if (highVolume <= 0) {
                    logger.info("合约中限价最大可交易数量为零，不进行拆单操作：{}", JsonUtils.getJsonFromObject(minTrdUnitLimit));
                    return entrustList;
                }
                if (totalVolume > highVolume) {
                    return handleSpiltEntrustVolume(entrust, highVolume, totalVolume, processType, null);
                }
            }
        }
        return entrustList;
    }

    private List<Entrust> handleSpiltEntrustVolume(Entrust entrust, long maxVolume, long totalVolume, int processType, FuturesInstrument futuresInstrument) {
        Order splitEntrustOrder = createSplitEntrustOrder(entrust, futuresInstrument);
        List<Entrust> spiltEntrustList = new ArrayList<>();
        long leftVolume = totalVolume - maxVolume;
        // 保留原委托
        entrust.setEntrustVolume(maxVolume);
        entrust.setRemark(splitEntrustOrder.getRemark());
        BigDecimal entrustBalance = TradeUtils.calculateBalance(entrust.getEntrustBs(), entrust.getEntrustPrice(), entrust.getEntrustVolume(), entrust.getAssetType(), entrust, futuresInstrument);
        if (MsgType.EXAMINE_SUCCESS == processType) {
            tradeMsgProcessor.frozenBalanceOrVolume(entrust, entrustBalance);
        }
        entrust.setEntrustBalance(entrustBalance);
        spiltEntrustList.add(entrust);
        Date now = new Date();
        // 拆分委托
        while (leftVolume > 0) {
            totalVolume = leftVolume;
            leftVolume = totalVolume - maxVolume;
            Entrust spiltEntrust = entrust.cloneEntrust();
            spiltEntrust.setId(entrustService.genDbId());
            spiltEntrust.setLocalEntrustId(null);
            spiltEntrust.setCreateTime(now);
            if (leftVolume < 0) {
                spiltEntrust.setEntrustVolume(totalVolume);
            } else {
                spiltEntrust.setEntrustVolume(maxVolume);
            }
            BigDecimal balance = TradeUtils.calculateBalance(spiltEntrust.getEntrustBs(), spiltEntrust.getEntrustPrice(), spiltEntrust.getEntrustVolume(), spiltEntrust.getAssetType(), spiltEntrust, futuresInstrument);
            if (MsgType.EXAMINE_SUCCESS == processType) {
                tradeMsgProcessor.frozenBalanceOrVolume(spiltEntrust, balance);
            }
            spiltEntrust.setEntrustBalance(balance);
            spiltEntrustList.add(spiltEntrust);
        }
        logger.info("拆单完成: 委托 {}", JsonUtils.getJsonFromObject(spiltEntrustList));
        entrustService.asyncUpdate(spiltEntrustList);
        return spiltEntrustList;
    }

    private String getRemark(String instructionId, String splitEntrustOrderId, String businessFlag, String entrustId) {
        return instructionId + "|" + splitEntrustOrderId + "|" + businessFlag + "|" + entrustId;
    }

    private Order createSplitEntrustOrder(Entrust entrust, FuturesInstrument futuresInstrument) {
        Order order = new Order();
        BeanUtils.copyProperties(entrust, order, "id");
        order.setBusinessFlag(BusinessFlag.SPLIT.value);
        // 暂时将原委托编号，存入指令编号中
        order.setInstructionId(entrust.getId());
        order.setOrderPrice(entrust.getEntrustPrice());
        order.setOrderVolume(entrust.getEntrustVolume());
        order.setOrderStatus(OrderStatus.ENTRUST_COMPLETE.value);
        BigDecimal orderBalance = TradeUtils.calculateBalance(entrust.getEntrustBs(), entrust.getEntrustPrice(), entrust.getEntrustVolume(), entrust.getAssetType(), entrust, futuresInstrument);
        order.setOrderBalance(orderBalance);
        order.setEntrustBalance(orderBalance);
        order.setId(orderService.genDbId());
        order.setRemark(getRemark(entrust.getInstructionId(), order.getId(), order.getBusinessFlag(), entrust.getId()));
        tradeMsgProcessor.frozenBalanceOrVolume(order, orderBalance);
        logger.info("拆单时生成订单： {}", JsonUtils.getJsonFromObject(order));
        orderService.asyncUpdate(order);
        return order;
    }

    public void unFrozenSplitOrder(String entrustProp, String remark, long unFrozenVolume, BigDecimal unFrozenBalance) {
        if (EntrustUtils.isSplitOrder(entrustProp, remark)) {
            Order order = orderService.findById(EntrustUtils.convertRemarkToOrderId(remark));
            if (Objects.nonNull(order)) {
                transactionHelper.addOrder(order);
                logger.info("解冻拆单订单信息： order {} unFrozenVolume {} unFrozenBalance {}", order.getId(), unFrozenVolume, unFrozenBalance);
                order.setCancelVolume(order.getCancelVolume() + unFrozenVolume);
                entrustCancelProcessor.unFrozenOrder(order, unFrozenVolume, unFrozenBalance);
            }
        }
    }

    public void handleSplitOrderByTrade(EntrustInterface entrust, long tradeVolume, BigDecimal newTrade, BigDecimal unFrozenBalance, Trade trade) {
        if (EntrustUtils.isSplitOrder(entrust.getEntrustProp(), entrust.getRemark())) {
            Order order = orderService.findById(EntrustUtils.convertRemarkToOrderId(entrust.getRemark()));
            if (Objects.nonNull(order)) {
                transactionHelper.addOrder(order);
                logger.info("处理拆单订单信息：order {} entrust {} tradeVolume {} newTrade {} unFrozenBalance {}", order.getId(), entrust.getId(), tradeVolume, newTrade, unFrozenBalance);
                entrustCancelProcessor.unFrozenOrder(order, tradeVolume, unFrozenBalance);
                order.setTradedPrice(TradeUtils.divide(TradeUtils.add(multiply(order.getTradedPrice(), order.getTradedVolume(), 4), newTrade, 4), order.getTradedVolume() + trade.getTradedVolume(), 4));
                order.setTradedVolume(order.getTradedVolume() + tradeVolume);
                order.setTradedBalance(TradeUtils.add(order.getTradedBalance(), trade.getTradedBalance(), 2));
            }
        }
    }

    public void handleRiskReleaseBySplitOrder(OrderInterface orderInterface, ReqRiskOccupy reqRiskOccupy) {
        Order order = orderService.findById(EntrustUtils.convertRemarkToOrderId(orderInterface.getRemark()));
        if (Objects.nonNull(order) && order.getCancelVolume() == order.getEntrustVolume()) {
            reqRiskOccupy.setRevokeFlg("Y");
        } else {
            reqRiskOccupy.setRevokeFlg("N");
        }
    }
}
