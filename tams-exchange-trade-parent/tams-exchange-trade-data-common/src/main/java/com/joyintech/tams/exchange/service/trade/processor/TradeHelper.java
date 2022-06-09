package com.joyintech.tams.exchange.service.trade.processor;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.joyintech.exchange.service.quote.service.QuoteService;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.exchange.common.BusinessResultException;
import com.joyintech.tams.exchange.common.bean.*;
import com.joyintech.tams.exchange.common.bean.investment.ReqRiskTrigger;
import com.joyintech.tams.exchange.common.bean.investment.RiskOccupyBean;
import com.joyintech.tams.exchange.common.bean.request.TradeInfoProRequest;
import com.joyintech.tams.exchange.common.bean.response.TradeInfoProResponse;
import com.joyintech.tams.exchange.common.bean.terminal.RTGSStatusBean;
import com.joyintech.tams.exchange.common.constant.*;
import com.joyintech.tams.exchange.common.constant.instrument.SubStockType;
import com.joyintech.tams.exchange.common.constant.joyin.DictCodeListExt2;
import com.joyintech.tams.exchange.common.constant.joyin.InstructionPriceMode;
import com.joyintech.tams.exchange.common.db.domain.*;
import com.joyintech.tams.exchange.common.dto.EntrustPriceAndYieldDto;
import com.joyintech.tams.exchange.common.request.PositionInstructionRequest;
import com.joyintech.tams.exchange.common.utils.*;
import com.joyintech.tams.exchange.datacommon.db.service.*;
import com.joyintech.tams.exchange.service.trade.management.service.*;
import com.joyintech.tams.exchange.service.trade.processor.account.AccountPositionHelper;
import com.joyintech.tams.exchange.service.trade.processor.business.bond.BondHelper;
import com.joyintech.tams.exchange.service.trade.processor.business.bulktrade.BulkTradeHelper;
import com.joyintech.tams.exchange.service.trade.processor.business.common.CommonCheckHelper;
import com.joyintech.tams.exchange.service.trade.processor.business.fund.FundHelper;
import com.joyintech.tams.exchange.service.trade.processor.business.futures.FuturesHelper;
import com.joyintech.tams.exchange.service.trade.processor.business.stock.StockHelper;
import com.joyintech.tams.exchange.service.trade.processor.business.xyhg.RepoHelper;
import com.joyintech.tams.exchange.service.trade.processor.constants.Constants;
import com.joyintech.tams.exchange.service.trade.processor.delay.DelayController;
import com.joyintech.tams.exchange.service.trade.processor.msg.MsgReceiverManager;
import com.joyintech.tams.exchange.service.trade.processor.msg.MsgType;
import com.joyintech.tams.exchange.service.trade.processor.msg.TradeMsgReceiver;
import com.joyintech.tams.exchange.service.trade.server.agent.AccountManager;
import com.joyintech.tams.exchange.service.trade.server.agent.TradeAgent;
import com.joyintech.tams.exchange.service.trade.server.agent.TradeAgentManager;
import com.joyintech.tams.exchange.service.trade.transaction.TransactionHelper;
import com.joyintech.tams.invest.common.entrust.inter.EtrTradeOtherCommonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.joyintech.tams.constant.DictCodeListExt3.交易方向_质押券入库;
import static com.joyintech.tams.constant.DictCodeListExt3.交易方向_质押券出库;
import static com.joyintech.tams.exchange.common.constant.EntrustProp.*;
import static com.joyintech.tams.exchange.common.constant.ErrorCode.ENTRUST_BSFLAG_PARSE_ERROR;
import static com.joyintech.tams.exchange.common.constant.OthCodeType.*;
import static com.joyintech.tams.exchange.common.constant.joyin.DictCodeListExt3.投管业务类型_现券交易;
import static com.joyintech.tams.exchange.common.constant.joyin.InvestmentAssetType1.NEGOTIATED_REPO;

@Service
public class TradeHelper {
    private static final Logger logger = LoggerFactory.getLogger(TradeHelper.class);

    @Autowired
    private TradeAgentManager tradeAgentManager;
    @Autowired
    private AccountManager accountManager;
    @Autowired
    private EntrustService entrustService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private IntegratedEntrustService integratedEntrustService;
    @Autowired
    private InstrumentService instrumentService;
    @Autowired
    FuturesInstrumentService futuresInstrumentService;
    @Autowired
    private InvestmentManagement investmentManagement;
    @Autowired
    private InstructionService instructionService;
    @Autowired
    private DelayController delayController;
    @Autowired
    private BatchInfoService batchInfoService;
    @Autowired
    private FairOrderHelper fairOrderHelper;
    @Autowired
    private MsgReceiverManager msgReceiverManager;
    @Autowired
    private EntrustManagement entrustManagement;
    //    @Autowired
//    private QuoteTickService quoteTickService;
    @Autowired
    private QuoteService quoteService;
    @Autowired
    private ReviewManagement reviewManagement;
    @Autowired
    private StockHelper stockHelper;
    @Autowired
    private BondHelper bondHelper;
    @Autowired
    private FundHelper fundHelper;
    @Autowired
    private BulkTradeHelper bulkTradeHelper;
    @Autowired
    private CommonCheckHelper commonCheckHelper;
    @Autowired
    private InvestRiskManagement investRiskManagement;
    @Autowired
    private CommonManagement commonManagement;
    @Autowired
    private RepoHelper repoHelper;
    @Autowired
    private AccountPositionHelper accountPositionHelper;

    @Autowired
    private FuturesHelper futuresHelper;

    @Autowired
    private FuturesInstrumentMarginRateService futuresInstrumentMarginRateService;
    // 在trade掉线后恢复未完成的委托
    private Map<String, String> entrustMap = new ConcurrentHashMap<>();

    private Map<String, List<Trade>> tradeMap = new ConcurrentHashMap<>();

    // 记录主推消息先于委托回报回来的主推委托信息
    private Map<String, EntrustInterface> entrustNotifyMap = Maps.newConcurrentMap();

    @Autowired
    private TransactionHelper transactionHelper;
    @Autowired
    private ExpireIncomeRateHelper expireIncomeRateHelper;
    @Autowired
    private EtrTradeOtherCommonService etrTradeOtherCommonService;

    public Map<String, List<Trade>> getTradeMap() {
        return tradeMap;
    }

    public EntrustInterface getEntrustNotifyByAccountIdAndBrokerEntrustId(String key) {
        return entrustNotifyMap.get(key);
    }

    public void setEntrustNotifyMap(String key, EntrustInterface entrustInterface) {
        this.entrustNotifyMap.put(key, entrustInterface);
    }

    public void clearTradeAndEntrustCache() {
        logger.info("tradeHelper clear tradeMap cache, size:{}", tradeMap.size());
        tradeMap.clear();
        logger.info("tradeHelper clear entrustMap cache, size:{}", entrustMap.size());
        entrustMap.clear();
        logger.info("tradeHelper clear entrustNotifyMap cache, size:{}", entrustNotifyMap.size());
        entrustNotifyMap.clear();
    }

    @PostConstruct
    public void cacheUnFinishedEntrust() {
        List<Entrust> entrustList = entrustService.findAll();
        if (Objects.nonNull(entrustList)) {
            entrustList.forEach(entrust -> {
                if (EntrustStatus.isUnCompleteCancelEntrust(entrust.getEntrustStatus())
                        || entrust.getEntrustStatus() == EntrustStatus.SUCCEEDED.value) {
                    String key = entrust.getAccountId() + "_" + entrust.getBrokerEntrustId();
                    entrustMap.put(key, entrust.getId());
                }
            });
        }
        List<IntegratedEntrust> integratedEntrustList = integratedEntrustService.findAll();
        if (Objects.nonNull(integratedEntrustList)) {
            integratedEntrustList.forEach(integratedEntrust -> {
                if (EntrustStatus.isUnCompleteCancelEntrust(integratedEntrust.getEntrustStatus())
                        || integratedEntrust.getEntrustStatus() == EntrustStatus.SUCCEEDED.value) {
                    String key = integratedEntrust.getAccountId() + "_" + integratedEntrust.getBrokerEntrustId();
                    entrustMap.put(key, integratedEntrust.getId());
                }
            });
        }
    }

    public Map<String, String> getEntrustMap() {
        return entrustMap;
    }

    public Entrust convertOrderToEntrust(Order order, BatchInfo batchInfo) {
        return convertOrderToEntrust(order, batchInfo, null, false);
    }

    public Entrust convertOrderToEntrust(Order order, BatchInfo batchInfo, boolean copyFlag) {
        return convertOrderToEntrust(order, batchInfo, null, copyFlag);
    }

    public void handleEntrust(EntrustInterface entrust, Instruction instruction, int msgType) {
        logger.info("准备通过风控：instruction {}  entrust {}", instruction.getEntrustId(), entrust.getId());
        if (checkCommonParam(entrust.getFullPrice(), entrust.getEntrustPrice(), entrust.getEntrustVolume(), entrust, instruction)) {
            //意向单无需进风控
            if (EntrustUtils.isIntentionEntrust(entrust.getEntrustProp())) {
                addEntrustToReceiver(entrust, instruction, msgType);
            } else {
                RiskOccupyBean riskOccupyBean = investRiskManagement.riskCheck(Arrays.asList(entrust));
                if (Objects.isNull(riskOccupyBean) || RiskStatus.PASS.value.equals(riskOccupyBean.getRskStatus()) || RiskStatus.ALERT.value.equals(riskOccupyBean.getRskStatus())) {
                    addEntrustToReceiver(entrust, instruction, msgType);
                } else if (RiskStatus.NEED_APPROVE.value.equals(riskOccupyBean.getRskStatus())) {
                    processRiskApprove(entrust, instruction, msgType);
                } else {
                    entrust.setEntrustStatus(EntrustStatus.NOT_RISK.value);
                    entrust.setErrorMsg(riskOccupyBean.getMessage());
                }
            }
        } else {
            entrust.setEntrustStatus(EntrustStatus.SYSTEM_INVALID.value);
        }
        if (MsgType.REQ_ENTRUST == msgType) {
            entrustService.asyncUpdate((Entrust) entrust);
        } else {
            integratedEntrustService.asyncUpdate((IntegratedEntrust) entrust);
        }
    }

    /**
     * 批量委托下单
     */
    public void handleBatchEntrust(EntrustInterface entrust, Instruction instruction, int msgType) {
        //批量委托检查委托价格
        checkPrice(entrust, instruction);
        //委托下单
        handleEntrust(entrust, instruction, msgType);
    }

    public void addEntrustToReceiver(EntrustInterface entrust, Instruction instruction, int msgType) {
        TradeAgent agent = tradeAgentManager.findAgentByAccountId(entrust.getAccountId());
        if (Objects.nonNull(agent)) {
            TradeMsgReceiver tradeMsgReceiver = msgReceiverManager.getMsgReceiverByInstruction(entrust.getInstructionId());
            if (Constants.PROCESS_TYPE_ENTRUST == msgType) {
                tradeMsgReceiver.addMsg(MsgType.REQ_ENTRUST, entrust, agent);
            } else if (Constants.PROCESS_TYPE_INTEGRATED == msgType) {
                tradeMsgReceiver.addMsg(MsgType.REQ_INTEGRATED, entrust, agent);
            }
        } else {
            entrust.setEntrustStatus(EntrustStatus.SYSTEM_INVALID.value);
            entrust.setErrorCode(ErrorCode.TERMINAL_NOT_ONLINE.value);
            entrust.setErrorMsg(ErrorCode.TERMINAL_NOT_ONLINE.errorMsg);
            entrust.setCancelVolume(entrust.getEntrustVolume());
            if (!EntrustUtils.isIntentionEntrust(entrust.getEntrustProp())) {//意向委托不需要调用风控释放
                investRiskManagement.riskControlRelease(Collections.singletonList(entrust), instruction);
            }
        }
    }

    public void processRiskApprove(EntrustInterface approveEntrust, Instruction instruction, int msgType) {
        String triggerRisk = investRiskManagement.triggerRiskExamine(Arrays.asList(approveEntrust), instruction);
        if (RiskType.STARTUP_FAILURE.value.equals(triggerRisk)) {
            approveEntrust.setEntrustStatus(EntrustStatus.NOT_APPROVAL.value);
            approveEntrust.setErrorMsg("审批任务启动失败");
            logger.info("handleOrder risk approve failed {}", approveEntrust.getId());
        } else {
            approveEntrust.setEntrustStatus(EntrustStatus.PENDING_APPROVAL.value);
            TradeMsgReceiver tradeMsgReceiver = msgReceiverManager.getMsgReceiverByInstruction(approveEntrust.getInstructionId());
            if (Constants.PROCESS_TYPE_ENTRUST == msgType) {
                tradeMsgReceiver.addMsg(MsgType.FROZEN_PENDING_ENTRUST, approveEntrust);
            } else if (Constants.PROCESS_TYPE_INTEGRATED == msgType) {
                tradeMsgReceiver.addMsg(MsgType.FROZEN_PENDING_INTEGRATED, approveEntrust);
            }
            logger.info("handleOrder risk in audit {}", approveEntrust.getId());
        }
    }

    public void processFairRiskApprove(Collection<Entrust> entrustList, Order order, RiskOccupyBean riskOccupyBean, List<ReqRiskTrigger> parameterList) {
        if (riskOccupyBean.getRskStatus().equals(RiskStatus.NEED_APPROVE.value)) {
            order.setOrderStatus(OrderStatus.PROGRESSING.value);
            String triggerRisk = investRiskManagement.triggerRiskExamine(order.getId(), parameterList);
            if (RiskType.STARTUP_FAILURE.value.equals(triggerRisk)) {
                entrustList.forEach(entrust -> {
                    entrust.setEntrustStatus(EntrustStatus.NOT_APPROVAL.value);
                    entrust.setErrorMsg("审批任务启动失败");
                    logger.info("handleOrder risk approve failed {}", entrust.getId());
                });
                order.setOrderStatus(OrderStatus.ENTRUST_COMPLETE.value);
            } else {
                entrustList.forEach(entrust -> {
                    entrust.setEntrustStatus(EntrustStatus.PENDING_APPROVAL.value);
                    TradeMsgReceiver tradeMsgReceiver = msgReceiverManager.getMsgReceiverByInstruction(entrust.getInstructionId());
                    tradeMsgReceiver.addMsg(MsgType.FROZEN_PENDING_ENTRUST, entrust);
                    logger.info("handleOrder risk in audit {}", entrust.getId());
                });
            }
            entrustService.asyncUpdate(entrustList);
            orderService.asyncUpdate(order);
        } else if (riskOccupyBean.getRskStatus().equals(RiskStatus.FORBIDDEN.value)) {
            logger.error("riskCheck error, some entrust has been forbidden [{}]", parameterList.get(0).getEventId());
        }
    }

    public void handleBatchOrder(BatchInfo batchInfo, Order order) {
        handleBatchOrder(batchInfo, order, null);
    }

    public Order handleOrder(OrderPlaceBean orderPlaceBean) {
        return handleOrderWithReview(orderPlaceBean, null);
    }

    private boolean getOthCodeFromInvestment(OrderPlaceBean order, Instruction instruction) {
        String othCodeType = "";
        String entrustProp = order.getEntrustProp();
        if (HS.value.equals(entrustProp)) {
            othCodeType = HSDM.value;
        } else if (ZYCRK.value.equals(entrustProp)) {
            othCodeType = ZYDM.value;
        } else if (CHANGE_TO_STOCK.value.equals(entrustProp)) {
            othCodeType = KZZZGDM.value;
        } else if (PS.value.equals(entrustProp)) {
            othCodeType = PZDM.value;
        } else if (ALLOTMENT.value.equals(entrustProp)) {
            othCodeType = PGDM.value;
        }
        //申报代码获取
        ResultBean resultBean = entrustManagement.getOthCode(instruction.getFinprodId(), othCodeType);
        if (resultBean.getErrorCode() == ErrorCode.SUCCESS.value) {
            List<OthCodeBean> list = (List<OthCodeBean>) resultBean.getData();
            if (Objects.nonNull(list) && list.size() > 0) {
                //03-可转债信用申购代码 or 07-新股申购代码
                if (APPLY.value.equals(entrustProp) || ETF_APPLY.value.equals(entrustProp)) {
                    for (OthCodeBean othCodeBean : list) {
                        if (KZZXYSGDM.value.equals(othCodeBean.getOthCodeType()) || XGSGDM.value.equals(othCodeBean.getOthCodeType())) {
                            order.setStockCode(othCodeBean.getOthCode());
                            break;
                        }
                    }
                } else {
                    order.setStockCode(list.get(0).getOthCode());
                }
                return true;
            }
        }
        return false;
    }

    public IntegratedEntrust handIntegratedEntrust(IntegratedEntrust integratedEntrust) {
        return handleIntegratedEntrustWithReview(integratedEntrust, null);
    }

    private boolean createOrderByAccount(OrderInterface order, String tradingDay, Date now) {
        order.setCreateTime(now);
        order.setUpdateTime(now);
        order.setTradingDay(tradingDay);
        Account account = accountManager.getAccountById(order.getAccountId());
        if (Objects.isNull(account)) {
            order.setErrorCode(ErrorCode.ACCOUNT_NOT_LOGIN.value);
            order.setErrorMsg(ErrorCode.ACCOUNT_NOT_LOGIN.errorMsg);
            logger.warn("未找到相关账号信息 {}", order.getAccountId());
            return true;
        }
        order.setFinanceAccount(account.getFinanceAccount());
        order.setAccountName(account.getAccountName());
        return false;
    }

    private boolean createOrderByInstrument(OrderInterface order, Instrument instrument, FuturesInstrument futuresInstrument) {
        if (Market.isFutures(StockCodeUtils.getMarket(order.getStockCode()))) {
            //期货合约
            if (Objects.nonNull(futuresInstrument)) {
                logger.info("futuresInstrument {} is futures contract", futuresInstrument.getInnerCode());
                order.setAssetType(InvestmentAssetType.FUTURES.value);
                order.setStockName(futuresInstrument.getInstrumentName());
            } else {
                order.setAssetType(InvestmentAssetType.FUTURES.value);
                order.setStockName(order.getStockCode());
                order.setErrorCode(ErrorCode.DERIVATIVE_INSTRUMENT_ERROR.value);
                order.setErrorMsg(ErrorCode.DERIVATIVE_INSTRUMENT_ERROR.msg());
                return false;
            }
        } else {
            if (Objects.nonNull(instrument)) {
                logger.info("合约 {}, 资产类型 {}", instrument.getInnerCode(), instrument.getAssetType());
                order.setAssetType(instrument.getAssetType());
                order.setStockName(instrument.getStockName());
            } else {
                order.setStockName(order.getStockCode());
                order.setAssetType(InvestmentAssetType.STOCK.value);
                logger.warn("未找到相关合约信息 {}", order.getStockCode());
            }
        }
        return true;
    }

    boolean handleCancelOrder(Order order) {
        // 不可撤单状态
        if (order.getOrderStatus() == OrderStatus.ENTRUST_COMPLETE.value || order.getOrderStatus() == OrderStatus.STOP.value) {
            logger.info("order can not cancel at [{}]", order.getOrderStatus());
            return false;
        } else {
            BatchInfo batchInfo = batchInfoService.findByOrderId(order.getId());
            if (Objects.nonNull(batchInfo)) {
                delayController.remove(batchInfo);
                order.setOrderStatus(OrderStatus.STOP.value);
                orderService.asyncUpdate(order);
            } else {
                logger.info("batchInfo is null {}", order.getId());
                return false;
            }
        }
        return true;
    }

    public boolean handleCancelOrder(String orderId) {
        Order order = orderService.findById(orderId);
        logger.info("cancelOrder :{}", orderId);
        if (Objects.nonNull(order)) {
            if (order.getBusinessFlag().equals(BusinessFlag.FAIR.value)) {
                logger.info("cancelFairOrder :{}", order.getId());
                return fairOrderHelper.handleCancelFairOrder(order);
            }
            return handleCancelOrder(order);
        }
        return false;
    }

    //  处理成交回报 在委托回报之前的情况
    public void handleTradeRspBeforeEntrustRsp(TradeMsgReceiver msgReceiver, Map<String, List<Trade>> tradeMap, TerminalId terminalId) {
        String key = terminalId.getAccountId() + "_" + terminalId.getBrokerEntrustId();
        List<Trade> tradeList = tradeMap.get(key);
        if (Objects.nonNull(tradeList) && !tradeList.isEmpty()) {
            logger.info("process account trade cache accountId_brokerEntrustId: {}", key);
            tradeList.forEach(trade -> {
                msgReceiver.handleRtnTradeRecord(trade);
                logger.info("process trade cache brokerTradeId: {}", trade.getBrokerTradeId());
            });
            tradeMap.remove(key);
        }
    }

    // 处理委托主推回报  --在委托回报之前的情况
    public void handleTradeNotifyEntrustRspBeforeOnEntrustRsp(TradeMsgReceiver msgReceiver, String key, int msgType) {
        EntrustInterface entrustInterface = entrustNotifyMap.get(key);
        if (Objects.nonNull(entrustInterface)) {
            if (msgType == MsgType.NOTIFY_ENTRUST) {
                msgReceiver.handleRtnEntrustDetail((Entrust) entrustInterface);
                logger.info("普通委托主推先于回报返回， 补发主推消息 accountId —— brokerEntrustId {}", key);
            } else if (msgType == MsgType.NOTIFY_INTEGRATED) {
                msgReceiver.handleRtnIntegratedEntrust((IntegratedEntrust) entrustInterface);
                logger.info("综合委托主推先于回报返回， 补发主推消息 accountId —— brokerEntrustId {}", key);
            }
            entrustNotifyMap.remove(key);
        }
    }

    //  处理成交回报 在委托回报之前的情况
    public void handleCancelTradeRspBeforeEntrustRsp(TradeMsgReceiver msgReceiver, Map<String, List<Trade>> tradeMap, CancelEntrustBean cancelEntrustBean) {
        String key = cancelEntrustBean.getAccountId() + "_" + cancelEntrustBean.getCancelBrokerEntrustId();
        List<Trade> tradeList = tradeMap.get(key);
        if (Objects.nonNull(tradeList) && !tradeList.isEmpty()) {
            logger.info("process account trade cache accountId_cancelBrokerEntrustId: {}", key);
            tradeList.forEach(trade -> {
                msgReceiver.handleRtnTradeRecord(trade);
                logger.info("process trade cache brokerTradeId: {}", trade.getBrokerTradeId());
            });
            tradeMap.remove(key);
        }
    }

    public boolean checkCommonParam(BigDecimal fullPrice, BigDecimal entrustPrice, long entrustVolume, OrderInterface orderInterface) {
        return checkCommonParam(fullPrice, entrustPrice, entrustVolume, orderInterface, null);
    }

    public boolean checkCommonParam(BigDecimal fullPrice, BigDecimal entrustPrice, long entrustVolume, OrderInterface orderInterface, Instruction instruction) {
        /*if (Objects.nonNull(instruction)) {
            if (InvestmentAssetType.FUTURES.value.equals(instruction.getAssetType())) {
                return futuresHelper.checkCommonParam(entrustPrice, entrustVolume, orderInterface, instruction);
            }
            Instrument instrument = instrumentService.queryByInnerCode(orderInterface.getStockCode());
            if (commonCheckHelper.checkTradeTime(instruction, instrument, orderInterface)) {
                return false;
            }
            if (NEGOTIATED_REPO.value.equals(instruction.getAssetType1())) {
                return repoHelper.checkCommonParam(entrustPrice, entrustVolume, (IntegratedEntrust) orderInterface, instruction);
            } else if (Objects.nonNull(instrument)) {
                FuturesInstrument futuresInstrument = futuresInstrumentService.queryByInnerCode(orderInterface.getStockCode());
                BigDecimal entrustBalance = TradeUtils.calculateBalance(orderInterface.getEntrustBs(), entrustPrice, entrustVolume, orderInterface.getAssetType(), orderInterface, futuresInstrument);
                String market = StockCodeUtils.getMarket(orderInterface.getStockCode());
                String entrustProp = orderInterface.getEntrustProp();
                if (accountPositionHelper.checkLooseStocksByPosition(orderInterface, entrustVolume, instrument.getBuyUnit())) {
                    if (IntegratedEntrustUtils.isBulk(entrustProp)) {
                        return bulkTradeHelper.checkCommonParam(entrustPrice, entrustVolume, orderInterface, instrument, market, entrustBalance, instruction);
                    } else {
                        if (InvestmentAssetType.STOCK.value.equals(orderInterface.getAssetType())) {
                            return stockHelper.checkCommonParam(entrustPrice, entrustVolume, orderInterface, instrument, instruction.getTradePlat(), instruction.getPs(), entrustBalance, instruction.getEntrustBussType(), instruction.getPriceMode());
                        } else if (InvestmentAssetType.BOND.value.equals(orderInterface.getAssetType())) {
                            return bondHelper.checkCommonParam(fullPrice, entrustPrice, entrustVolume, orderInterface, instrument, instruction.getTradePlat(), instruction.getPs(), entrustBalance, instruction.getEntrustBussType(), instruction.getPriceMode());
                        } else if (InvestmentAssetType.FUND.value.equals(orderInterface.getAssetType())) {
                            return fundHelper.checkCommonParam(entrustPrice, entrustVolume, orderInterface, instrument, instruction.getTradePlat(), instruction.getPs(), entrustBalance, instruction.getEntrustBussType(), instruction.getPriceMode());
                        } else {
                            return commonCheckHelper.checkCommonParam(entrustPrice, entrustVolume, orderInterface, instrument, instruction.getTradePlat(), instruction.getPs(), entrustBalance, instruction.getEntrustBussType(), instruction.getPriceMode());
                        }
                    }
                } else {
                    return false;
                }
            }
        }*/
        return true;
    }

    /**
     * 检查委托价格
     * 1、是否满足指令价格限定；
     * 2、是否满足行情涨跌停价限制
     */
    private void checkPrice(OrderInterface entrust, Instruction instruction) {
        BigDecimal entrustPrice = entrust.getEntrustPrice();
        BigDecimal price = instruction.getEntrustPrice();
        //检查指令价格限定
        if ((instruction.getPriceMode().equals(InstructionPriceMode.SPECIFIED.value) && !TradeUtils.equals(entrustPrice, price))
                || (instruction.getPriceMode().equals(InstructionPriceMode.FIXED.value) && checkPriceFixed(entrust, price, entrustPrice))) {
            logger.info("检查【指令价格限定】，指令价格：[{}]，委托价格：[{}]", price, entrustPrice);
            entrust.setEntrustPrice(price);
        }
        ResultBean tickBean = quoteService.getTick(entrust.getStockCode());
        if (ErrorCode.isSuccess(tickBean.getErrorCode())) {
            TickBean tick = (TickBean) tickBean.getData();
//        QuoteProto.TickBean tick = quoteTickService.findFromRedisById(entrust.getStockCode());
//        if (Objects.nonNull(tick)) {
            double lowerLimitPrice = TradeUtils.formatDouble(tick.getLowerLimitPrice(), 4);
            double upperLimitPrice = TradeUtils.formatDouble(tick.getUpperLimitPrice(), 4);
            if (InvestmentAssetType.STOCK.value.equals(entrust.getAssetType())) {
                logger.info("检查【股票】涨停价格。。。");
                fillEntrustPrice(entrust, lowerLimitPrice, upperLimitPrice, entrustPrice);
            } else if (InvestmentAssetType.BOND.value.equals(entrust.getAssetType())) {
                logger.info("检查【债券】涨停价格。。。");
                fillEntrustPrice(entrust, lowerLimitPrice, upperLimitPrice, entrustPrice);
//                boolean isFullPrice = false;
//                Instrument instrument = instrumentService.queryByInnerCode(entrust.getStockCode());
//                if (EntrustUtils.isFullPrice(instrument, entrust.getAssetType(), entrust.getEntrustProp(), entrust.getIsFullPrice())) {
//                    isFullPrice = true;
//                }
//                if (isFullPrice) {
//                    fillEntrustPrice(entrust, lowerLimitPrice, upperLimitPrice, entrust.getFullPrice());
//                } else {
//                    fillEntrustPrice(entrust, lowerLimitPrice, upperLimitPrice, entrustPrice);
//                }
            } else if (InvestmentAssetType.FUND.value.equals(entrust.getAssetType())) {
                logger.info("检查【基金】涨停价格。。。");
                fillEntrustPrice(entrust, lowerLimitPrice, upperLimitPrice, entrustPrice);
            } else {
                logger.info("检查涨停价格。。。");
                fillEntrustPrice(entrust, lowerLimitPrice, upperLimitPrice, entrustPrice);
            }
        }
    }

    private void fillEntrustPrice(OrderInterface entrust, double lowerLimitPrice, double upperLimitPrice, BigDecimal price) {
        if (entrust.getEntrustBs().equals(EntrustBs.SELL.value)) {
            //卖出，委托价格不得低于跌停价
            if (Double.compare(lowerLimitPrice, 0.0) > 0 && Double.compare(lowerLimitPrice, price.doubleValue()) > 0) {
                logger.info("卖出，委托价格不得低于跌停价。跌停价：[" + lowerLimitPrice + "]，委托价格：[" + price + "]");
                entrust.setEntrustPrice(TradeUtils.getBigDecimal(lowerLimitPrice, 4));
            }
        } else {
            //买入，委托价格不得高于涨停价
            if (Double.compare(upperLimitPrice, 0.0) > 0 && Double.compare(upperLimitPrice, price.doubleValue()) < 0) {
                logger.info("买入，委托价格不得高于涨停价。涨停价：[" + upperLimitPrice + "]，委托价格：[" + price + "]");
                entrust.setEntrustPrice(TradeUtils.getBigDecimal(upperLimitPrice, 4));
            }
        }
    }

    private boolean checkPriceFixed(OrderInterface order, BigDecimal fixedPrice, BigDecimal orderPrice) {
        return order.getEntrustBs().equals(EntrustBs.SELL.value) && TradeUtils.bigThan(fixedPrice, orderPrice)
                || order.getEntrustBs().equals(EntrustBs.BUY.value) && TradeUtils.lessThan(fixedPrice, orderPrice);
    }

    public void rollbackCancelStatus(EntrustInterface entrust) {
        if (entrust.getEntrustStatus() == EntrustStatus.REPORTED_TOBE_CANCEL.value) {
            entrust.setEntrustStatus(EntrustStatus.REPORTED.value);
        } else if (entrust.getEntrustStatus() == EntrustStatus.PART_TRADED_TOBE_CANCELED.value) {
            entrust.setEntrustStatus(EntrustStatus.PART_SUCCESS.value);
        }
    }

    public Order handleOrderWithReview(OrderPlaceBean orderPlaceBean, WorkflowBean workflow) {
        ReviewDto dto = BeanUtil.toBean(orderPlaceBean, ReviewDto.class);
        Order order;
        if (Strings.isNullOrEmpty(orderPlaceBean.getAlgorithmId())) {
            order = createOrder(dto);
        } else {
            Order orderDb = orderService.queryOrderByAlgorithmId(orderPlaceBean.getAlgorithmId());
            if (Objects.isNull(orderDb)) {
                order = new Order();
                order.setErrorCode(ErrorCode.ALGORITHM_NOT_EXIST.value);
                order.setErrorMsg(ErrorCode.ALGORITHM_NOT_EXIST.errorMsg);
            } else {
                order = createOrder(dto);
                order.setId(orderDb.getId());
                order.setAlgorithmId(orderDb.getAlgorithmId());
            }
        }
        if (order.getErrorCode() == ErrorCode.SUCCESS.value
                && checkFairInstructionNotReceive(order)
                && checkBatchReview(order, orderPlaceBean, workflow)) {
            handleOrder(order, workflow);
        }
        return order;
    }

    public Order handleOrderWithReview(ReviewDto dto) {
        WorkflowBean workflowBean = BeanUtil.toBean(dto, WorkflowBean.class);
        return handleOrderWithReview(dto, workflowBean);
    }

    private boolean checkInstruction(Instruction instruction, Order order) {
        if (Objects.nonNull(instruction)) {
            return true;
        }
        logger.info("未找到相关指令信息：InstructionId [{}] ", order.getInstructionId());
        order.setErrorCode(ErrorCode.INSTRUCTIONS_NOT_EXIST.value);
        order.setErrorMsg(ErrorCode.INSTRUCTIONS_NOT_EXIST.errorMsg);
        return false;
    }

    private boolean checkOthCode(ReviewDto dto, Order order, Instruction instruction) {
        if (EntrustUtils.needOthCode(dto.getEntrustProp())) {
            String stockCode = order.getStockCode();
            String entrustProp = dto.getEntrustProp();
            // add by lifangpeng 20220107 回售、标准券出入库、可转债转股不检查申报信息 start
            if (HS.value.equals(entrustProp) || ZYCRK.value.equals(entrustProp) || CHANGE_TO_STOCK.value.equals(entrustProp)) {
                return true;
            }
            // add by lifangpeng 20220107 回售、标准券出入库、可转债转股不检查申报信息 end
            if (EntrustProp.ETF_APPLY.value.equals(dto.getEntrustProp())) {
                Instrument instrument = instrumentService.findById(stockCode);
                if (Objects.isNull(instrument) || Strings.isNullOrEmpty(instrument.getPurRedId())) {
                    logger.warn("业务：{} 合约代码：{} 获取申报代码信息失败", entrustProp, stockCode);

                    // add by lifangpeng 20211230 填充委托错误码和描述 start
                    order.setErrorCode(ErrorCode.INSTRUMENT_INFO_NOT_EXIT.value);
                    order.setErrorMsg(ErrorCode.INSTRUMENT_INFO_NOT_EXIT.errorMsg);
                    // add by lifangpeng 20211230 填充委托错误码和描述 end

                    return false;
                } else {
                    logger.info("业务：{} 合约代码：{} 获取到申报代码信息：{}", entrustProp, stockCode, dto.getStockCode());
                    String purRedId = instrument.getPurRedId();
                    dto.setStockCode(purRedId);
                    order.setStockCode(StockCodeUtils.formatStockCode(purRedId.trim(), StockCodeUtils.getMarket(stockCode)));
                }
            } else if (getOthCodeFromInvestment(dto, instruction)) {
                String othCode = dto.getStockCode();
                logger.info("业务：{} 合约代码：{} 获取到申报代码信息：{}", entrustProp, stockCode, dto.getStockCode());
                if (!Strings.isNullOrEmpty(othCode)) {
                    order.setStockCode(StockCodeUtils.formatStockCode(othCode.trim(), StockCodeUtils.getMarket(stockCode)));
                }
            } else {
                logger.warn("业务：{} 合约代码：{} 获取申报代码信息失败", entrustProp, stockCode);
                // add by lifangpeng 20211230 填充委托错误码和描述 start
                order.setErrorCode(ErrorCode.INSTRUMENT_INFO_NOT_EXIT.value);
                order.setErrorMsg(ErrorCode.INSTRUMENT_INFO_NOT_EXIT.errorMsg);
                // add by lifangpeng 20211230 填充委托错误码和描述 end
                return false;
            }
        }
        return true;
    }

    public Order createOrder(ReviewDto dto) {
        logger.info("开始创建订单 {}", JsonUtils.getJsonFromObject(dto));
        Order order = Order.createOrder(dto);
        Instrument instrument = instrumentService.queryByInnerCode(order.getStockCode());
        FuturesInstrument futuresInstrument = futuresInstrumentService.queryByInnerCode(order.getStockCode());
        if (!createOrderByInstrument(order, instrument, futuresInstrument)) {
            return order;
        }
        Instruction instruction = instructionService.findById(dto.getInstructionId());
        if (!checkInstruction(instruction, order)) {
            return order;
        }

        //科创板更新委托属性
        if (Objects.nonNull(instrument) && SubStockType.KCB.value.equals(instrument.getSubStockType())
                && DictCodeListExt2.价格模式_收盘价固定价格.getValue().equals(instruction.getPriceMode())) {
            order.setEntrustProp(NORMAL_AFTER_SET_PRICE.getValue());
        }
        //市价委托填充反向涨跌停价，净价、全价
        commonCheckHelper.marketPriceCheck(order, instrument, instruction);

        String tradingDay = commonManagement.getTradingDay();

        BigDecimal orderPrice = order.getOrderPrice();
        if (InvestmentAssetType.BOND.codeEquals(instruction.getAssetType())
                && EnumUtil.in(instruction.getPs(), InstructionPs.BUY, InstructionPs.SELL)) {
            if (StringUtil.equals("Y", instruction.getIsNetPrice())) {
                order.setIsFullPrice(FullPriceConst.NET_PRICE.value);
            } else {
                order.setIsFullPrice(FullPriceConst.FULL_PRICE.value);
                orderPrice = order.getFullPrice();
            }
        }


        // 指令检查(包括状态,日期,接收人) 价格限定
//        ErrorCode errorCode = commonCheckHelper.instructionCheck(instruction, order, orderPrice, tradingDay);
//        if (!ErrorCode.isSuccess(errorCode.value)) {
//            logger.info("内部指令信息检查未通过：instructionId [{}] error:{}-{}", dto.getInstructionId(), errorCode.value, errorCode.errorMsg);
//            order.setErrorCode(errorCode.value);
//            order.setErrorMsg(errorCode.errorMsg);
//            return order;
//        }
        if (!checkOthCode(dto, order, instruction)) {
            return order;
        }
        order.setOrderStatus(OrderStatus.CREATE.value);
        logger.info("指令检查完成，当前指令基本信息： instruction {}, entrustNum:{}, entrustAmt:{}, frozen:{}, finished:{}", instruction.getEntrustId(), instruction.getEntrustNum(), instruction.getEntrustAmt(), instruction.getFrozenQuantity(), instruction.getEntrustLeftAmt());
        // 若账号不存在，直接返回订单
        if (createOrderByAccount(order, tradingDay, new Date(System.currentTimeMillis()))) {
            return order;
        }
        order.setBusinessFlag(BusinessFlag.NORMAL.value);
        order.setBsFlag(EntrustUtils.getBsFlagForNormal(order.getEntrustProp(), order.getEntrustBs(), order.getAssetType(), instrument, instruction.getDeliveryMethod()));
        order.setEntrustType(EntrustUtils.getEntrustType(order.getBsFlag()));
        order.setOrderBalance(TradeUtils.calculateBalance(order.getOrderPrice(), order.getOrderVolume(), order.getAssetType(), order, futuresInstrument));
        order.setPortfolioName(instruction.getPortfolioName());
        order.setSecAcctId(instruction.getSecAcctId());
        order.setSecAcctName(instruction.getSecAcctName());
        order.setTradePlat(instruction.getTradePlat());
        order.setFinprodType2(instruction.getFinprodType2());
        order.setInvManagerId(instruction.getInvManagerId());
        order.setPs(instruction.getTradeSide());
        order.setBusiTypeTg(instruction.getBusiTypeTg());
        order.setConNum(instruction.getConNum());
        order.setEntrustClass(instruction.getEntrustClass());
        order.setTargetType(instruction.getTargetType());
        order.setIsNoGuard(instruction.getIsNoGuard());

        //1.公平下单
        if (fairOrderHelper.createFairOrder(order, instruction)) {
            return order;
        }

        // 指令检查(包括状态,日期,接收人) 价格限定
        ErrorCode errorCode = commonCheckHelper.instructionCheck(instruction, order, orderPrice, tradingDay);
        if (!ErrorCode.isSuccess(errorCode.value)) {
            logger.info("内部指令信息检查未通过：instructionId [{}] error:{}-{}", dto.getInstructionId(), errorCode.value, errorCode.errorMsg);
            order.setErrorCode(errorCode.value);
            order.setErrorMsg(errorCode.errorMsg);
            return order;
        }

        //2.批量下单
        BatchInfo batchInfo = dto.getBatchInfo();
        if (Objects.nonNull(batchInfo)) {
            logger.info("开始批量交易下单...");

            Map<String, Object> extend2 = new HashMap<>();
            extend2.put("priceMode", batchInfo.getPriceMode());
            order.setExtend2(JsonUtils.getJsonFromObject(extend2));

            order.setBusinessFlag(BusinessFlag.BATCH.value);
            if (batchInfo.getPriceMode() == BatchPriceMode.LAST_PRICE.value) {
                order.setEntrustProp(Market.SHSE.value.equals(StockCodeUtils.getMarket(order.getStockCode())) ? BEST5_TO_LIMIT.value : COUNTERPARTY.value);
                commonCheckHelper.marketPriceCheck(order, instrument, instruction);
            } else {
                order.setEntrustProp(NORMAL.value);
            }
            order.setId(orderService.genDbId());

            BigDecimal minPrice = instruction.queryMinPrice(order.getEntrustProp());
            int round = Objects.nonNull(minPrice) ? minPrice.scale() : 4;
            setBatchOrderPrice(order, batchInfo, instruction, round);
            order.setOrderBalance(TradeUtils.calculateBalance(order.getOrderPrice(), order.getOrderVolume(), order.getAssetType(), order, futuresInstrument));

            batchInfo.setOrderId(order.getId());
            batchInfo.setId(null);
            batchInfoService.asyncUpdate(batchInfo);
            orderService.asyncUpdate(order);
        }
        //3.普通下单
        return order;
    }

    private boolean checkBatchReview(Order order, OrderPlaceBean orderPlaceBean, WorkflowBean workflow) {
        if (ObjectUtil.isNotNull(orderPlaceBean.getBatchInfo()) && ObjectUtil.isNotNull(workflow)) {
            String orderId = order.getId();
            logger.info("handle batch order need review {}", orderId);
            // 发起复核
            TrdExchangeEntrustDto dto = BeanUtil.toBean(workflow, TrdExchangeEntrustDto.class);
            dto.setEventId(orderId);
            dto.setOrderPlaceBean(orderPlaceBean);
            boolean processResult = reviewManagement.startWorkflow(dto);
            if (processResult) {
                // 修改委托状态
                order.setOrderStatus(OrderStatus.PENDING_REVIEW.value);
                logger.info("handleBatch review in audit {}", orderId);
            } else {
                order.setOrderStatus(OrderStatus.NOT_REVIEW.value);
                order.setErrorMsg("复核任务启动失败");
                logger.info("handleBatch review failed {}", orderId);
            }
            orderService.asyncUpdate(order);
            return false;
        }
        return true;
    }

    // 检查公平交易情况下，是否有未接收指令
    public boolean checkFairInstructionNotReceive(Order order) {
        if (EntrustUtils.isFairOrder(order.getEntrustProp()) && EntrustUtils.isFairSwitchOpen(commonManagement.getFairCode()) && EntrustUtils.isfairModeWithUnacceptInstructionForbid(commonManagement.getFairModeWithUnacceptInstruction())) {
            //公平交易开启
            Map<String, Instruction> instructionBeanMap = instructionService.queryInstructionByUserIdAndStockCode(order.getUserId(), order.getStockCode()).stream().filter(instruction -> InvestmentUtils.hasSameDir(instruction, order) && InstructionStatus.WAIT_RECEIVE.value.equals(instruction.getEntrustStatus()) && InvestmentUtils.isNormalTradeFromInstruction(instruction.getTradePlat()) && !InvestmentUtils.isInstructionProcessComplete(instruction)).collect(Collectors.toMap(Instruction::getEntrustId, instructionBean -> instructionBean));
            if (instructionBeanMap.size() > 0) {
                logger.debug("未接收且影响公平交易的相关指令: {}", JsonUtils.getJsonFromObject(instructionBeanMap));
                order.setErrorMsg("存在公平指令未接收");
                order.setErrorCode(ErrorCode.FAIR_INSTRUCTIONS_UNACCEPT_ERROR.value);
                return false;
            }
        }
        return true;
    }

    public void handleOrder(Order order, WorkflowBean workflow) {
        logger.info("start handleOrder {}", JsonUtils.getJsonFromObject(order));
        if (BusinessFlag.FAIR.value.equals(order.getBusinessFlag())) {
            // 公平订单
            fairOrderHelper.handleFairOrder(order, workflow);
        } else if (BusinessFlag.BATCH.value.equals(order.getBusinessFlag())) {
            // 批量订单
            BatchInfo batchInfo = batchInfoService.findByOrderId(order.getId());
            handleBatchOrder(batchInfo, order, workflow);
        } else {
            //普通订单
            Entrust entrust = convertOrderToEntrust(order, workflow);
            Instruction instruction = instructionService.findById(order.getInstructionId());
            handleEntrust(entrust, instruction, Constants.PROCESS_TYPE_ENTRUST);
        }
    }

//    public Entrust convertOrderToEntrust(Order order, BatchInfo batchInfo, WorkflowBean workflow) {
//        return convertOrderToEntrust(order, batchInfo, workflow, false);
//    }

    private void setBatchOrderPrice(Order order, BatchInfo batchInfo, Instruction instruction, int round) {
        // 市价委托
        if (isMarketPrice(order.getEntrustProp())) {
            return;
        }
        // 价格类型：固定递增
        if (batchInfo.getPriceMode() == BatchPriceMode.FIX_AND_INCREASE.value) {
            return;
        }
        ResultBean tickBean = quoteService.getTick(order.getStockCode());
        if (ErrorCode.isSuccess(tickBean.getErrorCode())) {
            TickBean tick = (TickBean) tickBean.getData();
            if (batchInfo.getPriceMode() == BatchPriceMode.BID_OFFSET.value) {
                if (tick.getBidPrice().get(0) > 0) {
                    order.setOrderPrice(TradeUtils.add(tick.getBidPrice().get(0), batchInfo.getPriceOffset(), round));
                } else if (tick.getLastPrice() > 0) {
                    order.setOrderPrice(TradeUtils.add(tick.getLastPrice(), batchInfo.getPriceOffset(), round));
                }
            } else if (batchInfo.getPriceMode() == BatchPriceMode.ASK_OFFSET.value) {
                if (tick.getAskPrice().get(0) > 0) {
                    order.setOrderPrice(TradeUtils.add(tick.getAskPrice().get(0), batchInfo.getPriceOffset(), round));
                } else if (tick.getLastPrice() > 0) {
                    order.setOrderPrice(TradeUtils.add(tick.getLastPrice(), batchInfo.getPriceOffset(), round));
                }
            } else if (tick.getLastPrice() > 0) {
                if (batchInfo.getPriceMode() == BatchPriceMode.LAST_OFFSET.value) {
                    order.setOrderPrice(TradeUtils.add(tick.getLastPrice(), batchInfo.getPriceOffset(), round));
                } else {
                    order.setOrderPrice(TradeUtils.getBigDecimal(tick.getLastPrice(), round));
                }
            } else {
                logger.info("lastPrice not greater than zero {}", order.getStockCode());
            }
            if (TradeUtils.bigThan(order.getEntrustPrice(), TradeUtils.getBigDecimal(tick.getUpperLimitPrice(), round))) {
                order.setOrderPrice(TradeUtils.getBigDecimal(tick.getUpperLimitPrice(), round));
            } else if (TradeUtils.lessThan(order.getEntrustPrice(), TradeUtils.getBigDecimal(tick.getLowerLimitPrice(), round))) {
                order.setOrderPrice(TradeUtils.getBigDecimal(tick.getLowerLimitPrice(), round));
            }
        }
    }

    private void setBatchEntrustPrice(Order order, BatchInfo batchInfo, Entrust entrust, Instruction instruction, int round) {
        // 市价委托：填充反向涨跌停价
        if (isMarketPrice(order.getEntrustProp())) {
            entrust.setEntrustPrice(order.getOrderPrice());
            return;
        }
        // 价格类型：固定递增
        if (batchInfo.getPriceMode() == BatchPriceMode.FIX_AND_INCREASE.value) {
            entrust.setEntrustPrice(TradeUtils.add(batchInfo.getBeginPrice(), TradeUtils.multiply(batchInfo.getPriceOffset(), batchInfo.getCompleteTimes(), 4), 4));
            return;
        }
        ResultBean tickBean = quoteService.getTick(entrust.getStockCode());
        if (ErrorCode.isSuccess(tickBean.getErrorCode())) {
            TickBean tick = (TickBean) tickBean.getData();
//        QuoteProto.TickBean tick = quoteTickService.findFromRedisById(entrust.getStockCode());
//        if (Objects.nonNull(tick)) {
            if (batchInfo.getPriceMode() == BatchPriceMode.BID_OFFSET.value) {
                if (tick.getBidPrice().get(0) > 0) {
                    entrust.setEntrustPrice(TradeUtils.add(tick.getBidPrice().get(0), batchInfo.getPriceOffset(), round));
                } else {
                    logger.info("bidPrice not greater than zero {}, try lastPrice", entrust.getStockCode());
                    if (tick.getLastPrice() > 0) {
                        entrust.setEntrustPrice(TradeUtils.add(tick.getLastPrice(), batchInfo.getPriceOffset(), round));
                    }
                }
            } else if (batchInfo.getPriceMode() == BatchPriceMode.ASK_OFFSET.value) {
                if (tick.getAskPrice().get(0) > 0) {
                    entrust.setEntrustPrice(TradeUtils.add(tick.getAskPrice().get(0), batchInfo.getPriceOffset(), round));
                } else {
                    logger.info("askPrice not greater than zero {}, try lastPrice", entrust.getStockCode());
                    if (tick.getLastPrice() > 0) {
                        entrust.setEntrustPrice(TradeUtils.add(tick.getLastPrice(), batchInfo.getPriceOffset(), round));
                    }
                }
            } else if (tick.getLastPrice() > 0) {
                if (batchInfo.getPriceMode() == BatchPriceMode.LAST_OFFSET.value) {
                    entrust.setEntrustPrice(TradeUtils.add(tick.getLastPrice(), batchInfo.getPriceOffset(), round));
                } else {
                    entrust.setEntrustPrice(TradeUtils.getBigDecimal(tick.getLastPrice(), round));
                }
            } else {
                logger.info("lastPrice not greater than zero {}", entrust.getStockCode());
            }
            if (TradeUtils.bigThan(entrust.getEntrustPrice(), TradeUtils.getBigDecimal(tick.getUpperLimitPrice(), round))) {
                logger.info("batch {} entrust price {} greater than uplimit {}, cover to uplimit", batchInfo.getOrderId(), entrust.getEntrustPrice(), tick.getUpperLimitPrice());
                entrust.setEntrustPrice(TradeUtils.getBigDecimal(tick.getUpperLimitPrice(), round));
            } else if (TradeUtils.lessThan(entrust.getEntrustPrice(), TradeUtils.getBigDecimal(tick.getLowerLimitPrice(), round))) {
                logger.info("batch {} entrust price {} less than lowlimit {}, cover to lowlimit", batchInfo.getOrderId(), entrust.getEntrustPrice(), tick.getLowerLimitPrice());
                entrust.setEntrustPrice(TradeUtils.getBigDecimal(tick.getLowerLimitPrice(), round));
            }
        }
    }

    public Entrust convertOrderToEntrust(Order order) {
        Entrust entrust = new Entrust();
        BeanUtils.copyProperties(order, entrust, "id", "entrustStatus", "frozenBalance", "frozenVolume", "tradedBalance", "tradedPrice", "tradedVolume", "createTime", "instructionId");
        entrust.setCreateTime(new Date(System.currentTimeMillis()));

        fillEntrust(entrust, order, null);

        return entrust;
    }

    /**
     * 普通委托组装
     */
    public Entrust convertOrderToEntrust(Order order, WorkflowBean workflow) {
        Entrust entrust = new Entrust();
        BeanUtils.copyProperties(order, entrust, "id", "entrustStatus", "frozenBalance", "frozenVolume", "tradedBalance", "tradedPrice", "tradedVolume", "createTime");
        entrust.setCreateTime(new Date(System.currentTimeMillis()));

        entrust.setEntrustVolume(order.getOrderVolume());
        entrust.setEntrustPrice(order.getOrderPrice());
        entrust.setFullPrice(order.getFullPrice());
        FuturesInstrument futuresInstrument = futuresInstrumentService.queryByInnerCode(entrust.getStockCode());
        BigDecimal entrustBalance = TradeUtils.calculateBalance(entrust.getEntrustBs(), entrust.getEntrustPrice(), entrust.getEntrustVolume(), entrust.getAssetType(), entrust, futuresInstrument);
        entrust.setEntrustBalance(entrustBalance);

        Instruction instruction = instructionService.findById(order.getInstructionId());
        if (交易方向_质押券出库.getValue().equals(instruction.getPs())
                || 交易方向_质押券入库.getValue().equals(instruction.getPs())) {
            Map<String, Object> params = new HashMap<>();
            params.put("finprodId", instruction.getFinprodId());
            BigDecimal faceValue = etrTradeOtherCommonService.selectFaceValue(params);
            entrust.setEntrustBalance(entrustBalance.multiply(faceValue).divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_UP));
        }

        fillEntrust(entrust, order, workflow);

        return entrust;
    }

    public Entrust convertOrderToEntrust(Order order, BatchInfo batchInfo, WorkflowBean workflow) {
        Entrust entrust = new Entrust();
        BeanUtils.copyProperties(order, entrust, "id", "entrustStatus", "frozenBalance", "frozenVolume", "tradedBalance", "tradedPrice", "tradedVolume", "createTime");
        entrust.setCreateTime(new Date(System.currentTimeMillis()));
        int entrustVolume = batchInfo.getBeginVolume() + batchInfo.getVolumeOffset() * batchInfo.getCompleteTimes();
        entrust.setEntrustVolume(entrustVolume);

        Instruction instruction = instructionService.findById(order.getInstructionId());
        BigDecimal minPrice = instruction.queryMinPrice(order.getEntrustProp());
        int round = Objects.nonNull(minPrice) ? minPrice.scale() : 4;
        setBatchEntrustPrice(order, batchInfo, entrust, instruction, round);

        //资产类型是债券，处理净价、全价
        if (InvestmentAssetType.isBond(order.getAssetType())) {
            EntrustPriceAndYieldDto dto = new EntrustPriceAndYieldDto();
            dto.setPrice(entrust.getEntrustPrice());
            dto.setEntrustVolume(entrust.getEntrustVolume());
            dto.setRound(round);

            //判断是否全价交易
            boolean isFullPrice = FullPriceConst.FULL_PRICE.value.equals(order.getIsFullPrice());
            investmentManagement.getAllPriceAndYield(instruction, dto, isFullPrice);
            entrust.setEntrustPrice(dto.getNetPrice());
            entrust.setFullPrice(dto.getFullPrice());
            entrust.setExpireYearRate(dto.getYield());
        }

        batchInfo.setOrderVolume(batchInfo.getOrderVolume() + (int) entrust.getEntrustVolume());
        batchInfo.setCompleteTimes(batchInfo.getCompleteTimes() + 1);
        batchInfo.setUpdateTime(new Date(System.currentTimeMillis()));

        FuturesInstrument futuresInstrument = futuresInstrumentService.queryByInnerCode(entrust.getStockCode());
        BigDecimal entrustBalance = TradeUtils.calculateBalance(entrust.getEntrustBs(), entrust.getEntrustPrice(), entrust.getEntrustVolume(), entrust.getAssetType(), entrust, futuresInstrument);
        entrust.setEntrustBalance(entrustBalance);
        BigDecimal oldBalance = TradeUtils.multiply(order.getOrderPrice(), order.getEntrustVolume(), 4);
        order.setEntrustBalance(TradeUtils.add(order.getEntrustBalance(), entrustBalance, 2));
        order.setOrderBalance(order.getEntrustBalance());
        order.setEntrustVolume(order.getEntrustVolume() + entrust.getEntrustVolume());
        //order上取平均委托价格
        if (InvestmentAssetType.isBond(order.getAssetType()) && FullPriceConst.FULL_PRICE.value.equals(order.getIsFullPrice())) {
            order.setEntrustPrice(TradeUtils.divide(oldBalance.add(TradeUtils.multiply(entrust.getFullPrice(), entrust.getEntrustVolume(), 4)), order.getEntrustVolume(), 4));
        } else {
            order.setEntrustPrice(TradeUtils.divide(oldBalance.add(TradeUtils.multiply(entrust.getEntrustPrice(), entrust.getEntrustVolume(), 4)), order.getEntrustVolume(), 4));
        }
        order.setOrderVolume(order.getEntrustVolume());
        order.setOrderPrice(order.getEntrustPrice());

        fillEntrust(entrust, order, workflow);

        return entrust;
    }

    private void fillEntrust(Entrust entrust, Order order, WorkflowBean workflow) {
        if (Objects.nonNull(order.getStockCode())) {
            entrust.setMarket(StockCodeUtils.getMarket(order.getStockCode()));
        }
        entrust.setStockCode(order.getStockCode());
        entrust.setEntrustStatus(EntrustStatus.CREATED.value);
        entrust.setId(entrustService.genDbId());
        entrust.setParentOrderId(order.getId());
        handleWorkflow(order.getId(), entrust, workflow);
    }

    public Entrust convertOrderToEntrust(Order order, BatchInfo batchInfo, WorkflowBean workflow, boolean copyFlag) {
        Entrust entrust = new Entrust();
        entrust.setCreateTime(new Date(System.currentTimeMillis()));
        if (copyFlag) {
            BeanUtils.copyProperties(order, entrust, "id", "entrustStatus", "frozenBalance", "frozenVolume", "tradedBalance", "tradedPrice", "tradedVolume", "createTime", "instructionId");
        } else {
            BeanUtils.copyProperties(order, entrust, "id", "entrustStatus", "frozenBalance", "frozenVolume", "tradedBalance", "tradedPrice", "tradedVolume", "createTime");
            Instruction instruction = instructionService.findById(order.getInstructionId());
            if (Objects.nonNull(batchInfo)) {
                entrust.setEntrustVolume(batchInfo.getBeginVolume() + batchInfo.getVolumeOffset() * batchInfo.getCompleteTimes());
                BigDecimal minPrice = instruction.queryMinPrice(order.getEntrustProp());
                int round = Objects.nonNull(minPrice) ? minPrice.scale() : 4;
                setBatchEntrustPrice(order, batchInfo, entrust, instruction, round);
//                // 处理债券
//                if (InvestmentAssetType.isBond(order.getAssetType())) {
//                    // 根据字段区分“起始净价”或者“起始全价”
//                    if (EntrustUtils.isFullPrice(instrument, order.getAssetType(), order.getEntrustProp(), order.getIsFullPrice())) {
//                        investmentManagement.setYeildAndNetPriceFromAllPrice(instruction, entrust, round);
//                        order.setIsFullPrice(FullPriceConst.FULL_PRICE.value);
//                    } else {
//                        investmentManagement.setYieldAndAllPriceFromNetPrice(instruction, entrust, round);
//                        order.setIsFullPrice(FullPriceConst.NET_PRICE.value);
//                    }
//                }

                //资产类型是债券，处理净价、全价
                if (InvestmentAssetType.isBond(order.getAssetType())) {
                    EntrustPriceAndYieldDto dto = new EntrustPriceAndYieldDto();
                    dto.setPrice(entrust.getEntrustPrice());
                    dto.setEntrustVolume(entrust.getEntrustVolume());
                    dto.setRound(round);

                    //判断是否全价交易
                    boolean isFullPrice = FullPriceConst.FULL_PRICE.value.equals(order.getIsFullPrice());
                    investmentManagement.getAllPriceAndYield(instruction, dto, isFullPrice);
                    entrust.setEntrustPrice(dto.getNetPrice());
                    entrust.setFullPrice(dto.getFullPrice());
                    entrust.setExpireYearRate(dto.getYield());
                }
                batchInfo.setOrderVolume(batchInfo.getOrderVolume() + (int) entrust.getEntrustVolume());
                batchInfo.setCompleteTimes(batchInfo.getCompleteTimes() + 1);
                batchInfo.setUpdateTime(new Date(System.currentTimeMillis()));
                FuturesInstrument futuresInstrument = futuresInstrumentService.queryByInnerCode(entrust.getStockCode());
                BigDecimal entrustBalance = TradeUtils.calculateBalance(entrust.getEntrustBs(), entrust.getEntrustPrice(), entrust.getEntrustVolume(), entrust.getAssetType(), entrust, futuresInstrument);
                entrust.setEntrustBalance(entrustBalance);
                BigDecimal oldBalance = TradeUtils.multiply(order.getOrderPrice(), order.getEntrustVolume(), 4);
                order.setEntrustBalance(TradeUtils.add(order.getEntrustBalance(), entrustBalance, 2));
                order.setOrderBalance(order.getEntrustBalance());
                order.setEntrustVolume(order.getEntrustVolume() + entrust.getEntrustVolume());
                //order上取平均委托价格
                if (InvestmentAssetType.isBond(order.getAssetType()) && FullPriceConst.FULL_PRICE.value.equals(order.getIsFullPrice())) {
                    order.setEntrustPrice(TradeUtils.divide(oldBalance.add(TradeUtils.multiply(entrust.getFullPrice(), entrust.getEntrustVolume(), 4)), order.getEntrustVolume(), 4));
                } else {
                    order.setEntrustPrice(TradeUtils.divide(oldBalance.add(TradeUtils.multiply(entrust.getEntrustPrice(), entrust.getEntrustVolume(), 4)), order.getEntrustVolume(), 4));
                }
                order.setOrderVolume(order.getEntrustVolume());
                order.setOrderPrice(order.getEntrustPrice());
            } else {
                entrust.setEntrustVolume(order.getOrderVolume());
                entrust.setEntrustPrice(order.getOrderPrice());
                entrust.setFullPrice(order.getFullPrice());
                FuturesInstrument futuresInstrument = futuresInstrumentService.queryByInnerCode(entrust.getStockCode());
                BigDecimal entrustBalance = TradeUtils.calculateBalance(entrust.getEntrustBs(), entrust.getEntrustPrice(), entrust.getEntrustVolume(), entrust.getAssetType(), entrust, futuresInstrument);
                entrust.setEntrustBalance(entrustBalance);
                if (交易方向_质押券出库.getValue().equals(instruction.getPs())
                        || 交易方向_质押券入库.getValue().equals(instruction.getPs())) {
                    Map<String, Object> params = new HashMap<>();
                    params.put("finprodId", instruction.getFinprodId());
                    BigDecimal faceValue = etrTradeOtherCommonService.selectFaceValue(params);
                    entrust.setEntrustBalance(entrustBalance.multiply(faceValue).divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_UP));
                }
            }
        }
        if (Objects.nonNull(order.getStockCode())) {
            entrust.setMarket(StockCodeUtils.getMarket(order.getStockCode()));
            entrust.setStockCode(order.getStockCode());
            entrust.setEntrustStatus(EntrustStatus.CREATED.value);
        }
        entrust.setId(entrustService.genDbId());
        entrust.setParentOrderId(order.getId());
        handleWorkflow(order.getId(), entrust, workflow);
        return entrust;
    }

    private void handleWorkflow(String orderId, EntrustInterface entrust, WorkflowBean workflow) {
        if (ObjectUtil.isNotEmpty(workflow)) {
            workflow.setEntrustId(entrust.getId());
            workflow.setOrderId(orderId);
            workflow.setInstructionId(entrust.getInstructionId());
            ReviewManagement.putReviewMap(entrust.getId(), workflow);
        }
    }

    public void addFairEntrustToReceiver(List<Entrust> entrustList) {
        logger.info("handle fair entrust attribute value {}", entrustList.get(0).getId());
        if (checkReview(entrustList)) {
            return;
        }
        entrustList.forEach(entrust -> addEntrustToReceiver(entrust, instructionService.findById(entrust.getInstructionId()), MsgType.REQ_ENTRUST));
    }

    public boolean checkReview(List<Entrust> entrustList) {
        WorkflowBean workflow = ReviewManagement.getReviewMap(entrustList.get(0).getParentOrderId());
        if (ObjectUtil.isNotEmpty(workflow)) {
            logger.info("handle fair order need review {}", entrustList.get(0).getParentOrderId());
            // 发起复核
            TrdExchangeEntrustDto dto = BeanUtil.toBean(workflow, TrdExchangeEntrustDto.class);
            dto.setEntrustList(entrustList);
            dto.setEventId(entrustList.get(0).getParentOrderId());
            boolean processResult = reviewManagement.startWorkflow(dto);
            ReviewManagement.removeReviewMap(entrustList.get(0).getParentOrderId());
            entrustList.forEach(entrust -> {
                String entrustId = entrust.getId();
                if (processResult) {
                    // 修改委托状态
                    entrust.setEntrustStatus(EntrustStatus.PENDING_REVIEW.value);
                    TradeMsgReceiver tradeMsgReceiver = msgReceiverManager.getMsgReceiverByInstruction(entrust.getInstructionId());
                    tradeMsgReceiver.addMsg(MsgType.FROZEN_PENDING_ENTRUST, entrust);
                    logger.info("handleEntrust review in audit {}", entrustId);
                } else {
                    entrust.setEntrustStatus(EntrustStatus.NOT_REVIEW.value);
                    entrust.setErrorMsg("复核任务启动失败");
                    entrust.setCancelVolume(entrust.getEntrustVolume());
                    Instruction instruction = instructionService.findById(entrust.getInstructionId());
                    investRiskManagement.riskControlRelease(Arrays.asList(entrust), instruction);
                    logger.info("handleEntrust review failed {}", entrustId);
                }
                entrustService.asyncUpdate(entrust);
            });
            return true;
        }
        return false;
    }

    public void handleBatchOrder(BatchInfo batchInfo, Order order, WorkflowBean workflow) {
        String orderId = batchInfo.getOrderId();
        logger.info("order :{}, bach entrust, entrustTimes: {}, has completeTimes: {}", orderId, batchInfo.getEntrustTimes(), batchInfo.getCompleteTimes());
        if (ObjectUtil.isNotNull(workflow) && batchInfo.getCompleteTimes() == 0) {
            ReviewManagement.putReviewMap(orderId, workflow);
        }
        if (ObjectUtil.isNull(workflow)) {
            workflow = ReviewManagement.getReviewMap(orderId);
        }
        transactionHelper.setBatchInfo(batchInfo);
        if (Objects.isNull(order)) {
            order = orderService.findById(batchInfo.getOrderId());
        }
        if (OrderStatus.isFinished(order.getOrderStatus())) {
            logger.info("orderStatus[{}] is finished status, shutdown batch order", order.getOrderStatus());
            return;
        }
        Entrust entrust = convertOrderToEntrust(order, batchInfo, workflow);
        entrust.setParentOrderId(order.getId());
        Instruction instructionBean = instructionService.findById(order.getInstructionId());
        if (batchInfo.getEntrustTimes() > batchInfo.getCompleteTimes() && entrust.getEntrustVolume() > 0) {
            delayController.put(batchInfo);
            order.setOrderStatus(OrderStatus.PROGRESSING.value);
        } else { // 判断委托是否已经下完
            order.setOrderStatus(OrderStatus.ENTRUST_COMPLETE.value);
            ReviewManagement.removeReviewMap(orderId);
        }
        orderService.asyncUpdate(order);
        if (entrust.getEntrustVolume() > 0) {
//            handleEntrust(entrust, instructionBean, Constants.PROCESS_TYPE_ENTRUST);
            handleBatchEntrust(entrust, instructionBean, Constants.PROCESS_TYPE_ENTRUST);
        }
    }

    public IntegratedEntrust handleIntegratedEntrustWithReview(TrdExchangeDto dto) {
        WorkflowBean workflowBean = BeanUtil.toBean(dto, WorkflowBean.class);
        return handleIntegratedEntrustWithReview(dto, workflowBean);
    }

    public IntegratedEntrust handleIntegratedEntrustWithReview(IntegratedEntrust integratedEntrust, WorkflowBean workflow) {
        logger.info("开始处理综合业务委托信息：{}", JsonUtils.getJsonFromObject(integratedEntrust));
        Instrument instrument = instrumentService.queryByInnerCode(integratedEntrust.getStockCode());
        createOrderByInstrument(integratedEntrust, instrument, null);
        String tradingDay = commonManagement.getTradingDay();
        Instruction instruction = instructionService.findById(integratedEntrust.getInstructionId());

        BigDecimal entrustPrice = integratedEntrust.getEntrustPrice();
        if (InvestmentAssetType.BOND.codeEquals(instruction.getAssetType())
                && EnumUtil.in(instruction.getPs(), InstructionPs.BUY, InstructionPs.SELL)) {
            if (StringUtil.equals("Y", instruction.getIsNetPrice())) {
                integratedEntrust.setIsFullPrice(FullPriceConst.NET_PRICE.value);
            } else {
                integratedEntrust.setIsFullPrice(FullPriceConst.FULL_PRICE.value);
                entrustPrice = integratedEntrust.getFullPrice();
            }
        }

        ErrorCode errorCode = commonCheckHelper.instructionCheck(instruction, integratedEntrust, entrustPrice, tradingDay);
        if (ErrorCode.isSuccess(errorCode.value)) {
            Date now = new Date(System.currentTimeMillis());
            integratedEntrust.setEntrustStatus(EntrustStatus.CREATED.value);
            integratedEntrust.setEntrustType(EntrustType.LARGE.value);
            integratedEntrust.setTransCode(instruction.getPledgeeType());
            String busiTypeTg = instruction.getBusiTypeTg();
            //协议回购
            if (EntrustUtils.isPledgeAgreementRepurchase(busiTypeTg)) {
                integratedEntrust.setBsFlag(EntrustUtils.getPledgeAgreementBsFlag(busiTypeTg, integratedEntrust.getEntrustProp(), integratedEntrust.getEntrustBs(), integratedEntrust.getConformOrRefuse()));
            } else {
                integratedEntrust.setBsFlag(EntrustUtils.getBsFlag(integratedEntrust.getEntrustProp(), integratedEntrust.getEntrustBs()));
            }
            if (StringUtil.isEmpty(integratedEntrust.getBsFlag())) {
                logger.info("handIntegratedEntrust 委托买卖类别解析异常：[{}]", integratedEntrust.getInstructionId());
                integratedEntrust.setErrorCode(ENTRUST_BSFLAG_PARSE_ERROR.value);
                integratedEntrust.setErrorMsg(ENTRUST_BSFLAG_PARSE_ERROR.errorMsg);
                return integratedEntrust;
            }

            integratedEntrust.setReductionType("0");
            if (Objects.nonNull(integratedEntrust.getStockCode())) {
                integratedEntrust.setMarket(StockCodeUtils.getMarket(integratedEntrust.getStockCode()));
                integratedEntrust.setStockCode(integratedEntrust.getStockCode());
            }
            // 若账号不存在，直接返回订单
            if (createOrderByAccount(integratedEntrust, tradingDay, now)) {
                return integratedEntrust;
            }
            FuturesInstrument futuresInstrument = futuresInstrumentService.queryByInnerCode(integratedEntrust.getStockCode());
            // 债券质押式协议回购，entrustBalance为入参，不计算覆盖
            if (!NEGOTIATED_REPO.equals(instruction.getAssetType1())) {
                // 计算委托金额
                BigDecimal entrustBalance = TradeUtils.calculateBalance(integratedEntrust.getEntrustBs(), integratedEntrust.getEntrustPrice(), integratedEntrust.getEntrustVolume(), integratedEntrust.getAssetType(), integratedEntrust, futuresInstrument);
                integratedEntrust.setEntrustBalance(entrustBalance);
            }

            integratedEntrust.setPortfolioName(instruction.getPortfolioName());
            integratedEntrust.setSecAcctId(instruction.getSecAcctId());
            integratedEntrust.setSecAcctName(instruction.getSecAcctName());
            integratedEntrust.setTradePlat(instruction.getTradePlat());
            integratedEntrust.setFinprodType2(instruction.getFinprodType2());
            integratedEntrust.setInvManagerId(instruction.getInvManagerId());
            integratedEntrust.setPs(instruction.getTradeSide());
            integratedEntrust.setBusiTypeTg(busiTypeTg);
            integratedEntrust.setEntrustClass(instruction.getEntrustClass());
            integratedEntrust.setAccountName(instruction.getAcctName());

            fillSelfDealerTradeInfo(integratedEntrust);
            fillPropDealerTradeInfo(integratedEntrust);

            integratedEntrustService.asyncUpdate(integratedEntrust);
            handleWorkflow(null, integratedEntrust, workflow);
            logger.info("start handle integratedEntrust {}", integratedEntrust.getId());
            handleEntrust(integratedEntrust, instruction, Constants.PROCESS_TYPE_INTEGRATED);
        } else {
            logger.info("handIntegratedEntrust [{}] error:{}-{}", integratedEntrust.getInstructionId(), errorCode.value, errorCode.errorMsg);
            integratedEntrust.setErrorCode(errorCode.value);
            integratedEntrust.setErrorMsg(errorCode.errorMsg);
        }
        return integratedEntrust;
    }

    private void fillSelfDealerTradeInfo(IntegratedEntrust integratedEntrust) {
        if (!StringUtil.equals(投管业务类型_现券交易.getValue(), integratedEntrust.getBusiTypeTg())
                || !EntrustProp.isConsultingTrade(integratedEntrust.getEntrustProp())) {
            return;
        }
        TradeInfoProRequest dto = new TradeInfoProRequest();
        dto.setAcctNo(integratedEntrust.getAccountId());
        dto.setTradePlace(Market.getInvestValue(integratedEntrust.getMarket()));
        dto.setIsOur("Y");
        List<TradeInfoProResponse> dealerTraderList = commonManagement.getDealerTraderInfo(dto);

        if (CollectionUtils.isEmpty(dealerTraderList)) {
            logger.error("fillSelfDealerTradeInfo error: 本方交易商、交易主体、交易员信息不存在");
            return;
        }
        TradeInfoProResponse tradeInfo = dealerTraderList.get(0);
        integratedEntrust.setDealerId(tradeInfo.getDealerNo());
        integratedEntrust.setDealerName(tradeInfo.getDealerName());
        integratedEntrust.setInvestorId(tradeInfo.getDealerPartyNo());
        integratedEntrust.setInvestorName(tradeInfo.getDealerPartyName());
        integratedEntrust.setOwnerType(tradeInfo.getDealerPartyType());
        integratedEntrust.setTradeId(tradeInfo.getTraderNo());
        integratedEntrust.setTradeName(tradeInfo.getTraderName());
    }

    private void fillPropDealerTradeInfo(IntegratedEntrust integratedEntrust) {
        if (!StringUtil.equals(投管业务类型_现券交易.getValue(), integratedEntrust.getBusiTypeTg())
                || !EntrustProp.isConsultingTrade(integratedEntrust.getEntrustProp())) {
            return;
        }

        integratedEntrust.setPropDealerName(integratedEntrust.getPropDealerId());
        integratedEntrust.setPropInvestorName(integratedEntrust.getPropInvestorId());
        integratedEntrust.setPropTradeName(integratedEntrust.getPropTradeId());

        TradeInfoProRequest dto = new TradeInfoProRequest();
        dto.setTradePlace(Market.getInvestValue(integratedEntrust.getMarket()));
        dto.setDealerNo(integratedEntrust.getPropDealerId());
        List<TradeInfoProResponse> list = commonManagement.getCounterDealerTraderInfo(dto);
        if (CollectionUtils.isEmpty(list)) {
            logger.error("fillPropDealerTradeInfo error: 对方交易商、交易主体、交易员信息不存在");
            return;
        }
        TradeInfoProResponse proResponse = list.get(0);
        integratedEntrust.setPropDealerName(proResponse.getDealerName());

        if (StringUtil.isNotEmpty(integratedEntrust.getPropInvestorId())) {
            Optional<TradeInfoProResponse> response = list.stream()
                    .filter(o -> StringUtil.equals(o.getDealerPartyNo(), integratedEntrust.getPropInvestorId())).findFirst();
            response.ifPresent(tradeInfoProResponse -> integratedEntrust.setPropInvestorName(tradeInfoProResponse.getDealerPartyName()));
        }
        Optional<TradeInfoProResponse> propTrade = list.stream()
                .filter(o -> StringUtil.equals(o.getTraderNo(), integratedEntrust.getPropTradeId())).findFirst();
        propTrade.ifPresent(response -> integratedEntrust.setPropTradeName(response.getTraderName()));
    }

    public String getEntrustId(Entrust entrust) {
        Entrust dbEntrust = null;
        if (Objects.nonNull(entrust.getLocalEntrustId())) {
            dbEntrust = entrustService.queryEntrustByLocalEntrustIdAndAccount(entrust.getLocalEntrustId(), entrust.getAccountId());
        } else if (Objects.nonNull(entrust.getBrokerEntrustId())) {
            dbEntrust = entrustService.queryEntrustByBrokerEntrustIdAndAccount(entrust.getBrokerEntrustId(), entrust.getAccountId());
        }
        return Objects.nonNull(dbEntrust) ? dbEntrust.getId() : null;
    }

    public String getIntegratedEntrustId(IntegratedEntrust entrust) {
        IntegratedEntrust dbEntrust = null;
        if (Objects.nonNull(entrust.getLocalEntrustId())) {
            dbEntrust = integratedEntrustService.queryIntegratedEntrustByLocalEntrustIdAndAccount(entrust.getLocalEntrustId(), entrust.getAccountId());
        } else if (Objects.nonNull(entrust.getBrokerEntrustId())) {
            dbEntrust = integratedEntrustService.queryIntegratedEntrustByAccountIdAndBrokerEntrustId(entrust.getAccountId(), entrust.getBrokerEntrustId());
        }
        return Objects.nonNull(dbEntrust) ? dbEntrust.getId() : null;
    }

    public void handleMarginRate(List<FuturesInstrumentMarginRateBean> marginRateBeans) {
        if (!CollectionUtils.isEmpty(marginRateBeans)) {
            List<FuturesInstrumentMarginRateBean> delList = futuresInstrumentMarginRateService.queryAllByAccountId(marginRateBeans.iterator().next().getAccountId());
            futuresInstrumentMarginRateService.asyncDel(delList);
            futuresInstrumentMarginRateService.asyncUpdate(marginRateBeans);
            logger.info("account  marginRate flush finished!");
        } else {
            logger.info("query account marginRateBeans is empty,keep previous data!");
        }
    }

    public void pushRTGSStatusToInvest(RTGSStatusBean rtgsStatusBean) {
        logger.info("推送交手状态到投管， {}", JsonUtils.getJsonFromObject(rtgsStatusBean));
    }

    /**
     * 判断是否普通交易
     */
    public boolean isOrdinaryTransaction(OrderPlaceBean orderPlaceBean, Instruction instruction) {
        if (isBatchTransaction(orderPlaceBean)) {
            return false;
        } else if (EntrustProp.isMarketPrice(orderPlaceBean.getEntrustProp())) {
            return false;
        }

        return true;
    }

    /**
     * 判断是否公平交易
     */
    public boolean isFairTransaction(OrderPlaceBean orderPlaceBean, Instruction instruction) {
        if (!Strings.isNullOrEmpty(orderPlaceBean.getAlgorithmId())
                || !EntrustUtils.isFairOrder(orderPlaceBean.getEntrustProp())
                || !EntrustUtils.isFairSwitchOpen(commonManagement.getFairCode())) {
            return false;
        }

        return fairOrderHelper.isFairOrder(orderPlaceBean);
    }

    /**
     * 判断是否批量委托
     */
    private boolean isBatchTransaction(OrderPlaceBean orderPlaceBean) {
        BatchInfo batchInfo = orderPlaceBean.getBatchInfo();
        return Objects.nonNull(batchInfo);
    }

    /**
     * 填充价格和金额
     */
    public void fillEntrustPriceAndAmt(OrderPlaceBean orderPlaceBean, PositionInstructionRequest instructionClone,
                                       Instruction instruction) {
        String entrustProp = orderPlaceBean.getEntrustProp();

        //价格精度
        BigDecimal minPrice = instruction.queryMinPrice(entrustProp);
        int round = Objects.isNull(minPrice) ? 4 : minPrice.scale();

        if (isBatchTransaction(orderPlaceBean)) {
            logger.info("批量委托头寸检查...");
            BatchInfo batchInfo = orderPlaceBean.getBatchInfo();
            //下单笔数
            int entrustTimes = batchInfo.getEntrustTimes();
            //起始数量
            int beginVolume = batchInfo.getBeginVolume();
            //数量偏移
            int volumeOffset = batchInfo.getVolumeOffset();
            //价格偏移
            BigDecimal priceOffset = batchInfo.getPriceOffset() == null ? BigDecimal.ZERO : batchInfo.getPriceOffset();

            //价格类型
            int priceMode = batchInfo.getPriceMode();
            int total = (entrustTimes - 1) * entrustTimes / 2;
            //批量总数量
            int totalVolume = beginVolume * entrustTimes + volumeOffset * total;
            instructionClone.setEntrustNum(totalVolume);

            //起始价格
            BigDecimal beginPrice;
            if (BatchPriceMode.FIX_AND_INCREASE.value == priceMode) {
                //起始价格
                beginPrice = batchInfo.getBeginPrice();
            } else {
                //取行情涨跌停价
                beginPrice = this.queryQuotePrice(orderPlaceBean);
            }
            BigDecimal totalEntrustAmt = BigDecimal.ZERO;
            for (int i = 0; i < entrustTimes; i++) {
                int entrustVolume = beginVolume + volumeOffset * i;
                BigDecimal entrustPrice = beginPrice.add(priceOffset.multiply(BigDecimal.valueOf(i)));
                totalEntrustAmt = totalEntrustAmt.add(entrustPrice.multiply(BigDecimal.valueOf(entrustVolume)));
            }
            //指令金额
            instructionClone.setEntrustAmt(totalEntrustAmt);

            //平均价格
            BigDecimal averagePrice = totalEntrustAmt.divide(BigDecimal.valueOf(totalVolume), round, RoundingMode.HALF_UP);
            instructionClone.setEntrustPrice(averagePrice);
        }
        //市价
        else if (EntrustProp.isMarketPrice(entrustProp)) {
            logger.info("市价交易头寸检查...");
            instructionClone.setEntrustNum(orderPlaceBean.getEntrustVolume());
            //取行情涨跌停价
            BigDecimal quotePrice = this.queryQuotePrice(orderPlaceBean);
            instructionClone.setEntrustPrice(quotePrice);
            instructionClone.setEntrustAmt(instructionClone.getEntrustPrice().multiply(BigDecimal.valueOf(instructionClone.getEntrustNum())));
        }

        if (instructionClone.getEntrustPrice() == null) {
            throw BusinessResultException.create(ErrorCode.REQ_PARAMETER_ERROR, "委托价格取值为空");
        }

        //资产类型是债券
        if (InvestmentAssetType.isBond(instructionClone.getAssetType())) {
            EntrustPriceAndYieldDto dto = new EntrustPriceAndYieldDto();
            dto.setPrice(instructionClone.getEntrustPrice());
            dto.setEntrustVolume(instructionClone.getEntrustNum());
            dto.setRound(round);

            //判断是否全价交易
            boolean isFullPrice = FullPriceConst.NET_PRICE.value.equals(instruction.getIsNetPrice());
            investmentManagement.getAllPriceAndYield(instruction, dto, isFullPrice);
            instructionClone.setEntrustPrice(dto.getNetPrice());
            instructionClone.setFullPrice(dto.getFullPrice());
        }

    }

    public BigDecimal queryQuotePrice(OrderPlaceBean orderPlaceBean) {
        ResultBean tickBean = quoteService.getTick(orderPlaceBean.getStockCode());
        if (ErrorCode.isSuccess(tickBean.getErrorCode())) {
            TickBean tick = (TickBean) tickBean.getData();
//        QuoteProto.TickBean tick = quoteTickService.getTickFromCache(orderPlaceBean.getStockCode());
//        if (Objects.nonNull(tick)) {
            BigDecimal entrustPrice;
            if (orderPlaceBean.getEntrustBs().equals(EntrustBs.SELL.value)) {
                entrustPrice = TradeUtils.getBigDecimal(tick.getLowerLimitPrice(), 4);
            } else {
                entrustPrice = TradeUtils.getBigDecimal(tick.getUpperLimitPrice(), 4);
            }
            return entrustPrice;
        } else {
            throw BusinessResultException.create(ErrorCode.QUOTATION_NOT_EXIST, "未获取到当前资产行情信息");
        }
    }


    public Map<String, Instruction> getInstructionBeans(Order order) {
        Map<String, Instruction> instructionMap;
        if (EntrustUtils.isfairModeEntrustPriceBestThanInstructionForbid(commonManagement.getFairModeEntrustPriceBestThanInstruction())) {
            instructionMap = instructionService.queryInstructionByUserIdAndStockCode(order.getUserId(), order.getStockCode())
                    .stream().filter(instruction -> InvestmentUtils.hasSameDir(instruction, order)
                            && InstructionStatus.ALREADY_RECEIVE.value.equals(instruction.getEntrustStatus())
                            && InvestmentUtils.isNormalTradeFromInstruction(instruction.getTradePlat())
                            && !InvestmentUtils.isInstructionProcessComplete(instruction))
                    .collect(Collectors.toMap(Instruction::getEntrustId, instructionBean -> instructionBean));
            // 市价委托，需检查限价委托是否超过涨跌停
            if (EntrustProp.isMarketPrice(order.getEntrustProp())) {
                ResultBean tickBean = quoteService.getTick(order.getStockCode());
                if (ErrorCode.isSuccess(tickBean.getErrorCode())) {
                    TickBean tick = (TickBean) tickBean.getData();
//                QuoteProto.TickBean tick = quoteTickService.getTickFromCache(order.getStockCode());
//                if (Objects.nonNull(tick)) {
                    double lowerLimitPrice = TradeUtils.formatDouble(tick.getLowerLimitPrice(), 4);
                    double upperLimitPrice = TradeUtils.formatDouble(tick.getUpperLimitPrice(), 4);
                    boolean flag = false;
                    if (EntrustBs.BUY.value.equals(order.getEntrustBs())) {
                        // 买入涨停价
                        flag = instructionMap.entrySet().stream().anyMatch(entry ->
                                InstructionPriceMode.FIXED.value.equals(entry.getValue().getPriceMode())
                                        && Double.compare(upperLimitPrice, entry.getValue().getEntrustPrice().doubleValue()) > 0);
                    } else if (EntrustBs.SELL.value.equals(order.getEntrustBs())) {
                        // 卖出跌停价
                        flag = instructionMap.entrySet().stream().anyMatch(entry ->
                                InstructionPriceMode.FIXED.value.equals(entry.getValue().getPriceMode())
                                        && Double.compare(lowerLimitPrice, entry.getValue().getEntrustPrice().doubleValue()) < 0);
                    }
                    if (flag) {
                        logger.info("指令中包含了限价指令，不允许下市价委托");
                        instructionMap.clear();
                    }
                }
            }
        } else {
            // 委托价格不优于指令价格系统参数 02时，若委托价格为市价，则限价指令不参与公平委托
            instructionMap = instructionService.queryInstructionByUserIdAndStockCode(order.getUserId(), order.getStockCode())
                    .stream().filter(instruction -> InvestmentUtils.hasSameDir(instruction, order)
                            && InstructionStatus.ALREADY_RECEIVE.value.equals(instruction.getEntrustStatus())
                            && InvestmentUtils.isNormalTradeFromInstruction(instruction.getTradePlat())
                            && !InvestmentUtils.isInstructionProcessComplete(instruction)
                            && !InstructionPriceMode.MARKET.value.equals(instruction.getPriceMode()))
                    .collect(Collectors.toMap(Instruction::getEntrustId, instructionBean -> instructionBean));
        }
        logger.debug("公平委托相关指令: {}", JsonUtils.getJsonFromObject(instructionMap));
        return instructionMap;
    }

    public void marketPriceCheck(OrderPlaceBean order, Instruction instruction) {
        // 科创板 盘后定价 填写保护价格 不需要反向填充涨跌停价格
        if (EntrustProp.NORMAL_AFTER_SET_PRICE.getValue().equals(order.getEntrustProp())) {
            return;
        }

        //市价委托填充反向涨跌停价
        if (EntrustProp.isMarketPrice(order.getEntrustProp())) {
            ResultBean tickBean = quoteService.getTick(order.getStockCode());
            if (ErrorCode.isSuccess(tickBean.getErrorCode())) {
                TickBean tick = (TickBean) tickBean.getData();
                BigDecimal minPrice = instruction.queryMinPrice(order.getEntrustProp());
                int round = Objects.nonNull(minPrice) ? minPrice.scale() : 4;
                BigDecimal price;
                if (order.getEntrustBs().equals(EntrustBs.SELL.value)) {
                    price = TradeUtils.getBigDecimal(tick.getLowerLimitPrice(), round, RoundingMode.UP);
                } else {
                    price = TradeUtils.getBigDecimal(tick.getUpperLimitPrice(), round, RoundingMode.DOWN);
                }
                order.setEntrustPrice(price);
            }
        }
    }

    public void fillFullPrice(OrderPlaceBean order, Instruction instruction) {
        if (Objects.nonNull(order.getFullPrice()) && order.getFullPrice().compareTo(BigDecimal.ZERO) > 0) {
            return;
        }

        BigDecimal minPrice = instruction.queryMinPrice(order.getEntrustProp());
        int round = Objects.nonNull(minPrice) ? minPrice.scale() : 4;

        //资产类型是债券
        if (InvestmentAssetType.isBond(instruction.getAssetType())
                && EnumUtil.in(instruction.getPs(), InstructionPs.BUY, InstructionPs.SELL)) {
            EntrustPriceAndYieldDto dto = new EntrustPriceAndYieldDto();
            dto.setPrice(order.getEntrustPrice());
            dto.setEntrustVolume(order.getEntrustVolume());
            dto.setRound(round);

            //判断是否全价交易
            boolean isFullPrice = FullPriceConst.NET_PRICE.value.equals(instruction.getIsNetPrice());
            investmentManagement.getAllPriceAndYield(instruction, dto, isFullPrice);
            order.setEntrustPrice(dto.getNetPrice());
            order.setFullPrice(dto.getFullPrice());
            order.setYield(dto.getYield());
        }
    }


}