package com.joyintech.tams.exchange.service.trade.processor;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.google.common.base.Strings;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.exchange.common.bean.TerminalId;
import com.joyintech.tams.exchange.common.bean.TrdExchangeEntrustDto;
import com.joyintech.tams.exchange.common.bean.TrdExchangeIntegratedEntrustDto;
import com.joyintech.tams.exchange.common.bean.WorkflowBean;
import com.joyintech.tams.exchange.common.constant.*;
import com.joyintech.tams.exchange.common.constant.joyin.InvestmentAssetType1;
import com.joyintech.tams.exchange.common.db.domain.*;
import com.joyintech.tams.exchange.common.microservice.ServiceApplication;
import com.joyintech.tams.exchange.common.request.PositionInstructionRequest;
import com.joyintech.tams.exchange.common.utils.*;
import com.joyintech.tams.exchange.datacommon.db.service.*;
import com.joyintech.tams.exchange.service.trade.management.service.InvestRiskManagement;
import com.joyintech.tams.exchange.service.trade.management.service.ReviewManagement;
import com.joyintech.tams.exchange.service.trade.position.service.PositionRequestServiceInstructionRequest;
import com.joyintech.tams.exchange.service.trade.processor.account.AccountPositionHelper;
import com.joyintech.tams.exchange.service.trade.processor.business.bond.BondHelper;
import com.joyintech.tams.exchange.service.trade.processor.business.common.EntrustCancelProcessor;
import com.joyintech.tams.exchange.service.trade.processor.business.common.TradeCallbackHelper;
import com.joyintech.tams.exchange.service.trade.processor.constants.Constants;
import com.joyintech.tams.exchange.service.trade.processor.delay.DelayController;
import com.joyintech.tams.exchange.service.trade.processor.delay.OrderRateController;
import com.joyintech.tams.exchange.service.trade.processor.msg.MsgType;
import com.joyintech.tams.exchange.service.trade.processor.others.SplitOrderHelper;
import com.joyintech.tams.exchange.service.trade.server.agent.TradeAgent;
import com.joyintech.tams.exchange.service.trade.server.agent.TradeAgentManager;
import com.joyintech.tams.exchange.service.trade.transaction.TransactionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.joyintech.tams.constant.DictCodeListExt3.*;
import static com.joyintech.tams.exchange.common.constant.EntrustStatus.*;
import static com.joyintech.tams.exchange.common.constant.EntrustType.CANCEL;

@Component
public class TradeMsgProcessor {
    private static final Logger logger = LoggerFactory.getLogger(TradeMsgProcessor.class);

    @Autowired
    private InstructionService instructionService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private EntrustService entrustService;
    @Autowired
    private IntegratedEntrustService integratedEntrustService;
    @Autowired
    private TradeCallbackHelper tradeCallbackHelper;
    @Autowired
    private TradeAgentManager tradeAgentManager;
    @Autowired
    private DelayController delayController;
    @Autowired
    private TradeHelper tradeHelper;
    @Autowired
    private InstructionPledgeService instructionPledgeService;
    //    @Autowired
//    private MessageSenderService messageSenderService;
    @Autowired
    private TransactionHelper transactionHelper;
    @Autowired
    private AccountRiskParameterService accountRiskParameterService;
    @Autowired
    private OrderRateController orderRateController;
    @Autowired
    private ReviewManagement reviewManagement;
    @Autowired
    private EntrustCancelProcessor entrustCancelProcessor;
    @Autowired
    private SplitOrderHelper splitOrderHelper;
    @Autowired
    private FuturesInstrumentService futuresInstrumentService;
    @Autowired
    private InvestRiskManagement investRiskManagement;
    @Autowired
    private AccountPositionHelper accountPositionHelper;
    @Autowired
    private BondHelper bondHelper;
    @Autowired
    private PositionRequestServiceInstructionRequest positionRequestServiceInstructionRequest;
    @Autowired
    private MessageSenderManagement messageSenderManagement;

    @Value("${yali.open:false}")
    private boolean yaliFlag;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");

    public boolean handleFrozenPendingApprovalEntrust(Entrust entrust) {
        logger.info("handleFrozenPendingApprovalEntrust {}", entrust.getId());
        FuturesInstrument futuresInstrument = futuresInstrumentService.queryByInnerCode(entrust.getStockCode());
        BigDecimal entrustBalance = TradeUtils.calculateBalance(entrust.getEntrustBs(), entrust.getEntrustPrice(), entrust.getEntrustVolume(), entrust.getAssetType(), entrust, futuresInstrument);
        Instruction instruction = instructionService.findById(entrust.getInstructionId());
        transactionHelper.addEntrust(entrust);
        transactionHelper.setInstructionBean(instruction);
        return frozenInstructionAndEntrust(entrust, entrustBalance, instruction);
    }

    private boolean frozenInstructionAndEntrust(Entrust entrust, BigDecimal entrustBalance, Instruction instruction) {
        if (frozenInstructionAndPledge(entrust.getEntrustVolume(), entrustBalance, instruction, entrust)
                && accountPositionHelper.frozenPositionEnable(entrust, entrust.getEntrustVolume())) {
            frozenBalanceOrVolume(entrust, entrustBalance);
            // 处理批量交易订单
            if (!Strings.isNullOrEmpty(entrust.getParentOrderId())) {
                Order order = orderService.findById(entrust.getParentOrderId());
                if (Objects.nonNull(order)) {
                    transactionHelper.addOrder(order);
                    order.setFrozenBalance(TradeUtils.add(order.getFrozenBalance(), entrust.getFrozenBalance(), 2));
                    order.setFrozenVolume(order.getFrozenVolume() + entrust.getFrozenVolume());
                }
            }
            return true;
        } else {
            splitOrderHelper.unFrozenSplitOrder(entrust.getEntrustProp(), entrust.getRemark(), entrust.getEntrustVolume(), entrustBalance);
            entrust.setEntrustStatus(EntrustStatus.SYSTEM_INVALID.value);
            entrust.setCancelVolume(entrust.getEntrustVolume());
            investRiskManagement.riskControlRelease(Arrays.asList(entrust), instruction);
            return false;
        }
    }

    // 检查是否需要复核
    public boolean checkReview(Entrust entrust, Instruction instruction, int processType) {
        String entrustId = entrust.getId();
        WorkflowBean workflow = ReviewManagement.getReviewMap(entrustId);
        // 无需复核
        if (ObjectUtil.isNull(workflow)) {
            return false;
        }
        // 冻结可用
        //经过风控审批的委托已经做过冻结，无需再次冻结
        if (processType != MsgType.EXAMINE_SUCCESS && !handleFrozenPendingApprovalEntrust(entrust)) {
            entrust.setEntrustStatus(EntrustStatus.SYSTEM_INVALID.value);
            investRiskManagement.riskControlRelease(Arrays.asList(entrust), instruction);
            logger.info("handleEntrust review failed {}", entrust.getId());
            return true;
        }
        // 发起复核
        logger.info("handle entrustInterface need review {}", entrustId);
        TrdExchangeEntrustDto dto = BeanUtil.toBean(workflow, TrdExchangeEntrustDto.class);
        dto.setEntrustList(Arrays.asList(entrust));
        dto.setEventId(entrustId);
        boolean processResult = reviewManagement.startWorkflow(dto);
        ReviewManagement.removeReviewMap(entrustId);
        if (processResult) {
            entrust.setEntrustStatus(EntrustStatus.PENDING_REVIEW.value);
            logger.info("handleEntrust review in audit {}", entrustId);
        } else {
            entrust.setEntrustStatus(EntrustStatus.NOT_REVIEW.value);
            entrust.setErrorMsg("复核任务启动失败");
            FuturesInstrument futuresInstrument = futuresInstrumentService.queryByInnerCode(entrust.getStockCode());
            BigDecimal balance = TradeUtils.calculateBalance(entrust.getEntrustBs(), entrust.getEntrustPrice(), entrust.getEntrustVolume(), entrust.getAssetType(), entrust, futuresInstrument);
            entrustCancelProcessor.unFrozenEntrustAndInstructionAndPosition(entrust, entrust.getEntrustVolume(), balance, instruction, true);
            logger.info("handleEntrust review failed {}", entrustId);
        }
        return true;
    }

    public void handleEntrust(Entrust entrust, TradeAgent agent, int processType) {
        logger.info("handle entrust attribute value {}", entrust.getId());
        Instruction instruction = instructionService.findById(entrust.getInstructionId());
        transactionHelper.setInstructionBean(instruction);
        // 复核委托
        if (checkReview(entrust, instruction, processType))
            return;
        //复核完成判断是否需要拆单
        List<Entrust> spiltEntrustList = splitOrderHelper.spiltOrder(entrust, processType);
        spiltEntrustList.forEach(spiltEntrust -> {
            transactionHelper.addEntrust(spiltEntrust);
            //公平和普通逻辑一致，计算冻结 冻结指令
            FuturesInstrument futuresInstrument = futuresInstrumentService.queryByInnerCode(spiltEntrust.getStockCode());
            BigDecimal entrustBalance = TradeUtils.calculateBalance(spiltEntrust.getEntrustBs(), spiltEntrust.getEntrustPrice(), spiltEntrust.getEntrustVolume(), spiltEntrust.getAssetType(), spiltEntrust, futuresInstrument);
            // 区分委托是否是通过风控审批通过后调回的，若是则不需要再次冻结，不是则重新冻结
            if (MsgType.EXAMINE_SUCCESS == processType || frozenInstructionAndEntrust(spiltEntrust, entrustBalance, instruction)) {
                AccountRiskParameter accountRiskParameter = accountRiskParameterService.queryAccountRiskParameterByAccountId(spiltEntrust.getAccountId());
                if (!yaliFlag && Objects.nonNull(accountRiskParameter)) {
                    // 下单流量控制
                    if (!orderRateController.put(spiltEntrust, accountRiskParameter)) {
                        spiltEntrust.setEntrustStatus(EntrustStatus.SYSTEM_INVALID.value);
                        spiltEntrust.setErrorCode(ErrorCode.INNER_RISK_ERROR.value);
                        spiltEntrust.setErrorMsg("账号下单流控限制，请稍后再试");
                        spiltEntrust.setCancelVolume(spiltEntrust.getEntrustVolume());
                        entrustCancelProcessor.unFrozenOrderAndEntrustAndInstruction(spiltEntrust, instruction);
                        return;
                    }
                    // 账户委托总次数限制
                    int totalCount = Objects.isNull(accountRiskParameter.getTotalCount()) ? 0 : accountRiskParameter.getTotalCount();
                    if (entrustService.getAccountTotalCount(spiltEntrust.getAccountId()) > totalCount) {
                        spiltEntrust.setEntrustStatus(EntrustStatus.SYSTEM_INVALID.value);
                        spiltEntrust.setErrorCode(ErrorCode.INNER_RISK_ERROR.value);
                        spiltEntrust.setErrorMsg("账号下单次数已达上限,最大可下单次数为" + accountRiskParameter.getTotalCount());
                        spiltEntrust.setCancelVolume(spiltEntrust.getEntrustVolume());
                        entrustCancelProcessor.unFrozenOrderAndEntrustAndInstruction(spiltEntrust, instruction);
                        return;
                    }
                    //自成交检测
                    //                if (accountRiskParameter.getSelfTrade() && EntrustUtils.isSelfTradeTyper(entrust.getEntrustProp())) {
                    //                    Collection<Entrust> accountEntrustList = entrustService.getAccountSameStockCodeEntrusts(entrust.getAccountId(), entrust.getStockCode());
                    //                    if (checkSelfTrade(entrust, accountEntrustList)) {
                    //                        entrust.setCancelVolume(entrust.getEntrustVolume());
                    //                        unFrozenOrderAndEntrustAndInstruction(entrust, instruction);
                    //                        return;
                    //                    }
                    //                }
                }
                spiltEntrust.setEntrustStatus(EntrustStatus.TOBE_REPORTED.value);
                agent.entrust(spiltEntrust);
            }
        });
    }
    //    private boolean checkSelfTrade(Entrust entrust, Collection<Entrust> accountEntrustList) {
    //        for (EntrustInterface entrust1 : accountEntrustList) {
    //            if ((entrust.getEntrustBs().equals(EntrustBs.BUY.value)
    //                    && entrust1.getEntrustBs().equals(EntrustBs.SELL.value)
    //                    && TradeUtils.compare(entrust.getEntrustPrice(), entrust1.getEntrustPrice()) >= 0)
    //                    || (entrust.getEntrustBs().equals(EntrustBs.SELL.value)
    //                    && entrust1.getEntrustBs().equals(EntrustBs.BUY.value)
    //                    && TradeUtils.compare(entrust.getEntrustPrice(), entrust1.getEntrustPrice()) <= 0)) {
    //                entrust.setErrorCode(ErrorCode.INNER_RISK_ERROR.value());
    //                entrust.setErrorMsg("账户" + entrust.getAccountId() + "委托" + entrust.getId() + "和已存在委托" + entrust1.getId() + "存在自成交风险");
    //                entrust.setEntrustStatus(EntrustStatus.SYSTEM_INVALID.value);
    //                logger.info("new entrust {} and entrust {} exist self-trade risk", entrust.getId(), entrust1.getId());
    //                break;
    //            }
    //        }
    //        return entrust.getErrorCode() != ErrorCode.SUCCESS.value;
    //    }

    public boolean handleFrozenPendingApprovalIntegratedEntrust(IntegratedEntrust integratedEntrust) {
        logger.info("handleFrozenPendingApprovalIntegratedEntrust {}", integratedEntrust.getId());
        BigDecimal entrustBalance;
        if (IntegratedEntrustUtils.isProtocolBack(integratedEntrust.getEntrustProp())) {
            entrustBalance = integratedEntrust.getEntrustBalance();
        } else {
            entrustBalance = TradeUtils.calculateBalance(integratedEntrust.getEntrustBs(), integratedEntrust.getEntrustPrice(), integratedEntrust.getEntrustVolume(), integratedEntrust.getAssetType(), integratedEntrust, null);
        }
        Instruction instruction = instructionService.findById(integratedEntrust.getInstructionId());
        transactionHelper.addIntegratedEntrust(integratedEntrust);
        transactionHelper.setInstructionBean(instruction);
        return frozenInstructionAndIntegratedEntrust(integratedEntrust, entrustBalance, instruction);
    }

    private boolean frozenInstructionAndIntegratedEntrust(IntegratedEntrust integratedEntrust, BigDecimal entrustBalance, Instruction instruction) {
        if (frozenInstructionAndPledge(integratedEntrust.getEntrustVolume(), entrustBalance, instruction, integratedEntrust)) {
            frozenBalanceOrVolume(integratedEntrust, entrustBalance);
            return true;
        } else {
            integratedEntrust.setEntrustStatus(EntrustStatus.SYSTEM_INVALID.value);
            integratedEntrust.setCancelVolume(integratedEntrust.getEntrustVolume());
            if (!EntrustUtils.isIntentionEntrust(integratedEntrust.getEntrustProp())) {//意向委托不需要调用风控释放
                investRiskManagement.riskControlRelease(Arrays.asList(integratedEntrust), instruction);
            }
            return false;
        }
    }

    // 检查是否需要复核
    public boolean checkReview(IntegratedEntrust entrust, Instruction instruction, int processType) {
        String entrustId = entrust.getId();
        WorkflowBean workflow = ReviewManagement.getReviewMap(entrustId);
        // 无需复核
        if (ObjectUtil.isNull(workflow)) {
            return false;
        }
        // 冻结可用
        // 经过风控审批的委托已经做过冻结，无需再次冻结
        if (processType != MsgType.EXAMINE_SUCCESS && !handleFrozenPendingApprovalIntegratedEntrust(entrust)) {
            entrust.setEntrustStatus(EntrustStatus.SYSTEM_INVALID.value);
            investRiskManagement.riskControlRelease(Arrays.asList(entrust), instruction);
            logger.info("handleEntrust review failed {}", entrustId);
            return true;
        }
        logger.info("handle entrustInterface need review {}", entrustId);
        // 发起复核
        TrdExchangeIntegratedEntrustDto dto = BeanUtil.toBean(workflow, TrdExchangeIntegratedEntrustDto.class);
        dto.setEntrustList(Arrays.asList(entrust));
        dto.setEventId(entrustId);
        boolean processResult = reviewManagement.startWorkflow(dto);
        ReviewManagement.removeReviewMap(entrustId);
        if (processResult) {
            // 修改委托状态
            entrust.setEntrustStatus(EntrustStatus.PENDING_REVIEW.value);
            logger.info("handleEntrust review in audit {}", entrustId);
        } else {
            entrust.setEntrustStatus(EntrustStatus.NOT_REVIEW.value);
            entrust.setErrorMsg("复核任务启动失败");
            FuturesInstrument futuresInstrument = futuresInstrumentService.queryByInnerCode(entrust.getStockCode());
            BigDecimal balance = TradeUtils.calculateBalance(entrust.getEntrustBs(), entrust.getEntrustPrice(), entrust.getEntrustVolume(), entrust.getAssetType(), entrust, futuresInstrument);
            entrustCancelProcessor.unFrozenEntrustAndInstructionAndPosition(entrust, entrust.getEntrustVolume(), balance, instruction, true);
            logger.info("handleEntrust review failed {}", entrustId);
        }
        return true;
    }

    public void handIntegratedEntrust(IntegratedEntrust integratedEntrust, TradeAgent agent, int processType) {
        logger.info("handle IntegratedEntrust attribute value {}", integratedEntrust.getId());
        transactionHelper.addIntegratedEntrust(integratedEntrust);
        Instruction instruction = instructionService.findById(integratedEntrust.getInstructionId());
        transactionHelper.setInstructionBean(instruction);
        if (checkReview(integratedEntrust, instruction, processType))
            return;
        BigDecimal entrustBalance;
        if (InvestmentAssetType.PLEDGE_REPURCHASE.value.equals(instruction.getAssetType()) && InvestmentAssetType1.NEGOTIATED_REPO.value.equals(instruction.getAssetType1())) {
            entrustBalance = integratedEntrust.getEntrustBalance();
        } else {
            entrustBalance = TradeUtils.calculateBalance(integratedEntrust.getEntrustBs(), integratedEntrust.getEntrustPrice(), integratedEntrust.getEntrustVolume(), integratedEntrust.getAssetType(), integratedEntrust, null);
        }
        // 区分委托是否是通过风控审批通过后调回的，若是则不需要再次冻结，不是则重新冻结
        if (MsgType.EXAMINE_SUCCESS == processType || frozenInstructionAndIntegratedEntrust(integratedEntrust, entrustBalance, instruction)) {
            integratedEntrust.setEntrustStatus(EntrustStatus.TOBE_REPORTED.value);
            agent.integratedEntrust(integratedEntrust);
        }
    }

    public void frozenBalanceOrVolume(OrderInterface orderInterface, BigDecimal frozenBalance) {
        if (TradeUtils.isFrozenBalance(orderInterface.getEntrustProp(), orderInterface.getEntrustBs(), orderInterface.getAssetType())) {
            orderInterface.setFrozenBalance(frozenBalance);
        }
        if (TradeUtils.isFrozenVolume(orderInterface.getEntrustProp(), orderInterface.getEntrustBs(), orderInterface.getAssetType())) {
            orderInterface.setFrozenVolume(orderInterface.getEntrustVolume());
        }
    }

    private boolean frozenOrderAndInstruction(Order order, Instruction instruction) {
        if (!frozenInstructionAndPledge(order.getOrderVolume(), order.getOrderBalance(), instruction, order)) {
            order.setOrderStatus(OrderStatus.ENTRUST_COMPLETE.value);
            return false;
        }
        if (TradeUtils.isFrozenVolume(order.getEntrustProp(), order.getEntrustBs(), order.getAssetType())) {
            order.setFrozenVolume(order.getOrderVolume());
        }
        if (TradeUtils.isFrozenBalance(order.getEntrustProp(), order.getEntrustBs(), order.getAssetType())) {
            FuturesInstrument futuresInstrument = futuresInstrumentService.queryByInnerCode(order.getStockCode());
            order.setFrozenBalance(TradeUtils.calculateBalance(order.getBsFlag(), order.getOrderPrice(), order.getOrderVolume(), order.getAssetType(), order, futuresInstrument));
        }
        logger.info("frozenInstruction EntrustNum {} EntrustAmt {} EntrustLeftAmt {} FrozenQuantity {} ", instruction.getEntrustNum(), instruction.getEntrustAmt(), instruction.getEntrustLeftAmt(), instruction.getFrozenQuantity());
        return true;
    }

    private boolean frozenInstructionAndPledge(long entrustVolume, BigDecimal entrustBalance, Instruction instruction, OrderInterface orderInterface) {
        if (!Strings.isNullOrEmpty(ServiceApplication.INSTRUCTION_SERVICE) && !EntrustUtils.isIntentionEntrust(orderInterface.getEntrustProp())) {
            if (Objects.isNull(instruction)) {
                orderInterface.setErrorCode(ErrorCode.INSTRUCTIONS_NOT_EXIST.value);
                orderInterface.setErrorMsg(ErrorCode.INSTRUCTIONS_NOT_EXIST.errorMsg);
                return false;
            }
            if (!instruction.getEntrustStatus().trim().equals(InstructionStatus.ALREADY_RECEIVE.value)) {
                orderInterface.setErrorCode(ErrorCode.INSTRUCTIONS_STATUS_ERROR.value);
                orderInterface.setErrorMsg(ErrorCode.INSTRUCTIONS_STATUS_ERROR.errorMsg);
                return false;
            }
            if (InvestmentAssetType1.NEGOTIATED_REPO.value.equals(instruction.getAssetType1())) {
                InstructionPledge instructionPledge = instructionPledgeService.queryInstructionPledgesByInstructionIdAndStockCode(instruction.getEntrustId(), orderInterface.getStockCode());
                if (Objects.nonNull(instructionPledge)) {
                    BigDecimal usedPledgeNumber = TradeUtils.add(instructionPledge.getUsedPledgeNumber(), orderInterface.getEntrustVolume(), 0);
                    if ((usedPledgeNumber.compareTo(instructionPledge.getPledgeNumber()) > 0)) {
                        orderInterface.setErrorCode(ErrorCode.INSTRUCTION_PLEDGE_NUMBER_ERROR.value);
                        orderInterface.setErrorMsg(ErrorCode.INSTRUCTION_PLEDGE_NUMBER_ERROR.errorMsg);
                        return false;
                    } else {
                        transactionHelper.setInstructionPledge(instructionPledge);
                        logger.info("占用抵押券数量 PledgeNumber {} UsedPledgeNumber {} EntrustVolume {}", instructionPledge.getPledgeNumber(), instructionPledge.getUsedPledgeNumber(), orderInterface.getEntrustVolume());
                        instructionPledge.setUsedPledgeNumber(usedPledgeNumber);
                    }
                }
            }
            // 可用数量
            if (instruction.volumeInstruction()) {
                if (instruction.getEntrustNum() < instruction.getEntrustLeftAmt().longValue() + instruction.getFrozenQuantity().longValue() + entrustVolume) {
                    orderInterface.setErrorCode(ErrorCode.INSTRUCTIONS_NUMBER_ERROR.value);
                    orderInterface.setErrorMsg(ErrorCode.INSTRUCTIONS_NUMBER_ERROR.errorMsg);
                    return false;
                }
                instruction.setFrozenQuantity(BigDecimal.valueOf(instruction.getFrozenQuantity().longValue() + entrustVolume));
//                messageSenderService.pushProgressToMQ(instruction);
                messageSenderManagement.pushProgress(instruction);
                logger.info("frozen instruction EntrustNum {} FrozenQuantity {} EntrustLeftAmt {}", instruction.getEntrustNum(), instruction.getFrozenQuantity(), instruction.getEntrustLeftAmt());
                return true;
            }
            // 可用额度
            if (instruction.getEntrustAmt().doubleValue() > 0) {
                // 金额指令全部以净价处理， 需要重新计算指令金额冻结
//                BigDecimal instructionBalance = bondHelper.calculateBalanceForInstruction(orderInterface.getEntrustProp(), orderInterface.getAssetType(), orderInterface.getEntrustPrice(), orderInterface.getEntrustVolume(), entrustBalance);
                BigDecimal instructionBalance = bondHelper.calculateBalanceForInstruction(orderInterface, orderInterface.getEntrustVolume(), entrustBalance);
                if (TradeUtils.lessThan(instruction.getEntrustAmt(), TradeUtils.add(instruction.getEntrustLeftAmt(), instruction.getFrozenQuantity(), instructionBalance, 2))) {
                    orderInterface.setErrorCode(ErrorCode.INSTRUCTIONS_BALANCE_ERROR.value);
                    orderInterface.setErrorMsg(ErrorCode.INSTRUCTIONS_BALANCE_ERROR.errorMsg);
                    return false;
                }
                instruction.setFrozenQuantity(TradeUtils.add(instruction.getFrozenQuantity(), instructionBalance, 2));
//                messageSenderService.pushProgressToMQ(instruction);
                messageSenderManagement.pushProgress(instruction);
                logger.info("frozen instruction EntrustAmt {} FrozenQuantity {} EntrustLeftAmt {}", instruction.getEntrustAmt(), instruction.getFrozenQuantity(), instruction.getEntrustLeftAmt());
            }
        } else if (!Strings.isNullOrEmpty(ServiceApplication.INSTRUCTION_SERVICE) && EntrustUtils.isIntentionEntrust(orderInterface.getEntrustProp())) {
            if (instruction.volumeInstruction()) {
                if (instruction.getEntrustNum() < instruction.getEntrustLeftAmt().longValue() + instruction.getFrozenQuantity().longValue() + entrustVolume) {
                    orderInterface.setErrorCode(ErrorCode.INSTRUCTIONS_NUMBER_ERROR.value);
                    orderInterface.setErrorMsg(ErrorCode.INSTRUCTIONS_NUMBER_ERROR.errorMsg);
                    return false;
                }
                instruction.setFrozenQuantity(BigDecimal.valueOf(instruction.getFrozenQuantity().longValue() + entrustVolume));
//                messageSenderService.pushProgressToMQ(instruction);
                messageSenderManagement.pushProgress(instruction);
                logger.info("frozen instruction EntrustNum {} FrozenQuantity {} EntrustLeftAmt {}", instruction.getEntrustNum(), instruction.getFrozenQuantity(), instruction.getEntrustLeftAmt());
                return true;
            }
            if (instruction.getEntrustAmt().doubleValue() > 0) {
                // 金额指令全部以净价处理， 需要重新计算指令金额冻结
//                BigDecimal instructionBalance = bondHelper.calculateBalanceForInstruction(orderInterface.getEntrustProp(), orderInterface.getAssetType(), orderInterface.getEntrustPrice(), orderInterface.getEntrustVolume(), entrustBalance);
                BigDecimal instructionBalance = bondHelper.calculateBalanceForInstruction(orderInterface, orderInterface.getEntrustVolume(), entrustBalance);
                if (TradeUtils.lessThan(instruction.getEntrustAmt(), TradeUtils.add(instruction.getEntrustLeftAmt(), instruction.getFrozenQuantity(), instructionBalance, 2))) {
                    orderInterface.setErrorCode(ErrorCode.INSTRUCTIONS_BALANCE_ERROR.value);
                    orderInterface.setErrorMsg(ErrorCode.INSTRUCTIONS_BALANCE_ERROR.errorMsg);
                    return false;
                }
                instruction.setFrozenQuantity(TradeUtils.add(instruction.getFrozenQuantity(), instructionBalance, 2));
//                messageSenderService.pushProgressToMQ(instruction);
                messageSenderManagement.pushProgress(instruction);
                logger.info("frozen instruction EntrustAmt {} FrozenQuantity {} EntrustLeftAmt {}", instruction.getEntrustAmt(), instruction.getFrozenQuantity(), instruction.getEntrustLeftAmt());
            }
        }
        return true;
    }

    public EntrustInterface handleExamineEntrust(EntrustInterface entrust, String instructionsStatus, String approveReason, int processType) {
        Instruction instruction = instructionService.findById(entrust.getInstructionId());
        transactionHelper.setInstructionBean(instruction);
        List<Entrust> entrusts = null;
        List<Entrust> approvalList = new ArrayList();
        if (Constants.PROCESS_TYPE_ENTRUST == processType) {
            transactionHelper.addEntrust((Entrust) entrust);
        } else if (Constants.PROCESS_TYPE_INTEGRATED == processType) {
            transactionHelper.addIntegratedEntrust((IntegratedEntrust) entrust);
        } else if (Constants.PROCESS_TYPE_FIRE_ORDER == processType) {
            entrusts = entrustService.queryEntrustByOrderId(entrust.getId());
            entrusts.forEach(entrust1 -> {
                if (entrust1.getEntrustStatus() == EntrustStatus.PENDING_APPROVAL.value) {
                    transactionHelper.addEntrust((Entrust) entrust);
                    approvalList.add(entrust1);
                }
            });
        }
        if (RiskType.EXAMINE_APPROVAL.value.equals(instructionsStatus)) {
            TradeAgent agent = tradeAgentManager.findAgentByAccountId(entrust.getAccountId());
            if (Objects.nonNull(agent)) {
                if (Constants.PROCESS_TYPE_ENTRUST == processType) {
                    handleEntrust((Entrust) entrust, agent, MsgType.EXAMINE_SUCCESS);
                } else if (Constants.PROCESS_TYPE_INTEGRATED == processType) {
                    handIntegratedEntrust((IntegratedEntrust) entrust, agent, MsgType.EXAMINE_SUCCESS);
                } else if (Constants.PROCESS_TYPE_FIRE_ORDER == processType) {
                    if (checkReview(approvalList))
                        return entrust;
                    approvalList.forEach(entrust1 -> handleEntrust(entrust1, agent, MsgType.EXAMINE_SUCCESS));
                }
            } else {
                if (Constants.PROCESS_TYPE_FIRE_ORDER == processType) {
                    approvalList.forEach(entrust1 -> {
                        setEntrustNonlineAndUnFrozen(entrust1, instruction);
                    });
                } else {
                    setEntrustNonlineAndUnFrozen(entrust, instruction);
                }

                logger.info("account agent not online {}", entrust.getId());
            }
        } else {
            setEntrustNotApprovalAndUnFrozen(entrust, instruction, approveReason);
        }
        return entrust;
    }

    private boolean checkReview(List<Entrust> entrustList) {
        WorkflowBean workflow = ReviewManagement.getReviewMap(entrustList.get(0).getParentOrderId());
        // 无需复核
        if (ObjectUtil.isNull(workflow)) {
            return false;
        }

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
                logger.info("handle fair order review in audit {}", entrustId);
            } else {
                entrust.setEntrustStatus(EntrustStatus.NOT_REVIEW.value);
                entrust.setErrorMsg("复核任务启动失败");
                entrust.setCancelVolume(entrust.getEntrustVolume());
                Instruction instruction = instructionService.findById(entrust.getInstructionId());
                investRiskManagement.riskControlRelease(Arrays.asList(entrust), instruction);
                logger.info("handle fair order review failed {}", entrustId);
            }
            entrustService.asyncUpdate(entrust);
        });
        return true;
    }

    private void setEntrustNonlineAndUnFrozen(EntrustInterface entrust, Instruction instruction) {
        entrust.setEntrustStatus(EntrustStatus.SYSTEM_INVALID.value);
        entrust.setErrorCode(ErrorCode.TERMINAL_NOT_ONLINE.value);
        entrust.setErrorMsg(ErrorCode.TERMINAL_NOT_ONLINE.errorMsg);
        entrustCancelProcessor.unFrozenOrderAndEntrustAndInstruction(entrust, instruction);
    }

    private void setEntrustNotApprovalAndUnFrozen(EntrustInterface entrust, Instruction instruction, String approveReason) {
        entrust.setErrorMsg(approveReason);
        entrust.setEntrustStatus(EntrustStatus.NOT_APPROVAL.value);
        entrustCancelProcessor.unFrozenOrderAndEntrustAndInstruction(entrust, instruction, false);
    }

    public void genFirstBatchEntrust(BatchInfo batchInfo) {
        transactionHelper.setBatchInfo(batchInfo);
        Order order = orderService.findById(batchInfo.getOrderId());
        transactionHelper.addOrder(order);
        logger.info("genFirstBatchEntrust order {} batchInfo {}", order.getId(), JsonUtils.getJsonFromObject(batchInfo));
        order.setOrderStatus(OrderStatus.PROGRESSING.value);
        Entrust entrust = tradeHelper.convertOrderToEntrust(order, batchInfo);
        transactionHelper.addEntrust(entrust);
        logger.info("convertOrderToEntrust result {}", JsonUtils.getJsonFromObject(entrust));
        Instruction instruction = instructionService.findById(order.getInstructionId());
        transactionHelper.setInstructionBean(instruction);
        if (frozenOrderAndInstruction(order, instruction)) {
            entrust.setParentOrderId(order.getId());
            TradeAgent agent = tradeAgentManager.findAgentByAccountId(order.getAccountId());
            BigDecimal balance = setEntrustFrozen(entrust);
            if (Objects.nonNull(agent)) {
                order.setEntrustBalance(balance);
                agent.entrust(entrust);
            } else {
                entrust.setEntrustStatus(EntrustStatus.SYSTEM_INVALID.value);
                entrust.setErrorCode(ErrorCode.TERMINAL_NOT_ONLINE.value);
                entrust.setErrorMsg(ErrorCode.TERMINAL_NOT_ONLINE.errorMsg);
                logger.info("handleBatchOrder entrust invalid {}", entrust.getId());
                entrustCancelProcessor.unFrozenEntrustAndInstructionAndPosition(entrust, entrust.getEntrustVolume(), balance, instruction);
            }
            if (batchInfo.getEntrustTimes() > batchInfo.getCompleteTimes()) {
                delayController.put(batchInfo);
            }
        } else {
            logger.info("frozen instruction failed :{}", order.getInstructionId());
            investRiskManagement.riskControlRelease(Arrays.asList(order), instruction);
        }
    }

    private BigDecimal setEntrustFrozen(EntrustInterface entrust) {
        FuturesInstrument futuresInstrument = futuresInstrumentService.queryByInnerCode(entrust.getStockCode());
        BigDecimal balance = TradeUtils.calculateBalance(entrust.getEntrustBs(), entrust.getEntrustPrice(), entrust.getEntrustVolume(), entrust.getAssetType(), entrust, futuresInstrument);
        if (TradeUtils.isFrozenVolume(entrust.getEntrustProp(), entrust.getEntrustBs(), entrust.getAssetType())) {
            entrust.setFrozenVolume(entrust.getEntrustVolume());
        }
        if (TradeUtils.isFrozenBalance(entrust.getEntrustProp(), entrust.getEntrustBs(), entrust.getAssetType())) {
            entrust.setFrozenBalance(balance);
        }
        return balance;
    }

    public void onRspEntrust(TerminalId entrust) {
        logger.info("onRspEntrust entrust {}", JsonUtils.getJsonFromObject(entrust));
        Entrust entrustDb = entrustService.findById(entrust.getId());
        if (Objects.nonNull(entrustDb)) {
            transactionHelper.addEntrust(entrustDb);
            processEntrustRsp(entrust, entrustDb);
        } else {
            logger.warn("entrustResp not found:{}", JsonUtils.getJsonFromObject(entrust));
        }
    }

    public void onRspIntegratedEntrust(TerminalId terminalId) {
        logger.info("onRspIntegratedEntrust integratedEntrustResp {}", JsonUtils.getJsonFromObject(terminalId));
        IntegratedEntrust integratedEntrustDB = integratedEntrustService.findById(terminalId.getId());
        if (Objects.nonNull(integratedEntrustDB)) {
            logger.info("integratedEntrustResp status: {}", integratedEntrustDB.getEntrustStatus());
            transactionHelper.addIntegratedEntrust(integratedEntrustDB);
            processEntrustRsp(terminalId, integratedEntrustDB);
        } else {
            logger.warn("integratedEntrustResp not found:{}", JsonUtils.getJsonFromObject(terminalId));
        }
    }

    public void onRspCancelEntrust(CancelEntrustBean cancelEntrustBean) {
        logger.info("onRspCancelEntrust {} -- {}", cancelEntrustBean.getBrokerEntrustId(), cancelEntrustBean.getCancelId());
        if (cancelEntrustBean.getCancelId().startsWith(IDPrefix.NORMAL.value)) {
            Entrust cancelEntrustDb = entrustService.findById(cancelEntrustBean.getCancelId());
            if (Objects.nonNull(cancelEntrustDb)) {
                Entrust entrustDb = entrustService.findById(cancelEntrustBean.getId());
                if (Objects.nonNull(entrustDb)) {
                    transactionHelper.addEntrust(entrustDb);
                    transactionHelper.addEntrust(cancelEntrustDb);
                    cancelEntrustBean.setId(cancelEntrustDb.getOrigEntrustId());
                    entrustCancelProcessor.processCancelEntrustResp(cancelEntrustBean, entrustDb, cancelEntrustDb);
                }
            }
        } else if (cancelEntrustBean.getCancelId().startsWith(IDPrefix.BULK.value)) {
            onRspCancelIntegratedEntrust(cancelEntrustBean);
        }
    }

    private void processEntrustRsp(TerminalId entrust, EntrustInterface entrustDb) {
        logger.info("processEntrustRsp entrustDb {}", JsonUtils.getJsonFromObject(entrustDb));
        Order order = null;
        if (Objects.nonNull(entrustDb.getParentOrderId())) {
            order = orderService.findById(entrustDb.getParentOrderId());
        }
        Instruction instruction = instructionService.findById(entrustDb.getInstructionId());
        transactionHelper.setInstructionBean(instruction);
        logger.info("processEntrustRsp instruction {}", JsonUtils.getJsonFromObject(instruction));
        if (entrust.getErrorCode() == ErrorCode.SUCCESS.value) {
            entrustDb.setBrokerEntrustId(entrust.getBrokerEntrustId());
            // 可能存在主推消息已经回来改变了委托状态，取两个委托状态中较大的一个
            entrustDb.setEntrustStatus(Math.max(EntrustStatus.REPORTED.value, entrustDb.getEntrustStatus()));
            if (!Strings.isNullOrEmpty(entrust.getReportNo())) {
                entrustDb.setReportNo(entrust.getReportNo());
            }
            if (Objects.nonNull(order) && order.getOrderStatus() < OrderStatus.PROGRESSING.value) {
                transactionHelper.addOrder(order);
                order.setOrderStatus(OrderStatus.PROGRESSING.value);
            }
            logger.info("entrustResp success entrustId and entrustStatus {} -- {}", entrustDb.getId(), entrustDb.getEntrustStatus());
            if (EntrustUtils.isNoTrade(entrustDb.getEntrustProp())) {
                if ((instruction.volumeInstruction() && instruction.getFrozenQuantity().longValue() == instruction.getEntrustNum())
                        || (instruction.getEntrustAmt().doubleValue() > 0 && instruction.getFrozenQuantity().doubleValue() == instruction.getEntrustAmt().doubleValue()))
                    instruction.setEntrustStatus(InstructionStatus.COMPLETE.value);
            }
        } else {
            if (Objects.nonNull(order)) {
//                order.setOrderStatus(OrderStatus.ENTRUST_COMPLETE.value);
//                order.setErrorMsg(ErrorMsgUtils.subErrorMsg(entrust.getErrorMsg()));
                order.setCancelVolume(entrustDb.getEntrustVolume());
                entrustCancelProcessor.unFrozenOrder(order, entrustDb.getFrozenVolume(), entrustDb.getFrozenBalance());
            }
            splitOrderHelper.unFrozenSplitOrder(entrustDb.getEntrustProp(), entrustDb.getRemark(), entrustDb.getFrozenVolume(), entrustDb.getFrozenBalance());
            entrustDb.setEntrustStatus(INVALID.value);
            entrustDb.setErrorMsg(ErrorMsgUtils.subErrorMsg(entrust.getErrorMsg()));
            entrustDb.setCancelVolume(entrustDb.getEntrustVolume());
            FuturesInstrument futuresInstrument = futuresInstrumentService.queryByInnerCode(entrustDb.getStockCode());
            BigDecimal entrustBalance;
            if (InvestmentAssetType.PLEDGE_REPURCHASE.value.equals(instruction.getAssetType()) && InvestmentAssetType1.NEGOTIATED_REPO.value.equals(instruction.getAssetType1())) {
                entrustBalance = ((IntegratedEntrust) entrustDb).getEntrustBalance();
            } else {
                entrustBalance = TradeUtils.calculateBalance(entrustDb.getEntrustBs(), entrustDb.getEntrustPrice(), entrustDb.getEntrustVolume(), entrustDb.getAssetType(), entrustDb, futuresInstrument);
            }
            entrustCancelProcessor.unFrozenEntrustAndInstructionAndPosition(entrustDb, entrustDb.getEntrustVolume(), entrustBalance, instruction);
            entrustDb.setErrorMsg(entrust.getErrorMsg());
            logger.warn("entrustResp fail:{}", JsonUtils.getJsonFromObject(entrustDb));
        }
        entrustDb.setUpdateTime(new java.util.Date(System.currentTimeMillis()));
    }

    private void onRspCancelIntegratedEntrust(CancelEntrustBean cancelEntrustBean) {
        logger.info("onRspCancelIntegratedEntrust {} -- {}", cancelEntrustBean.getBrokerEntrustId(), cancelEntrustBean.getCancelId());
        IntegratedEntrust cancelEntrustDb = integratedEntrustService.findById(cancelEntrustBean.getCancelId());
        if (Objects.nonNull(cancelEntrustDb)) {
            IntegratedEntrust integratedEntrustDb = integratedEntrustService.findById(cancelEntrustBean.getId());
            if (Objects.nonNull(integratedEntrustDb)) {
                transactionHelper.addIntegratedEntrust(cancelEntrustDb);
                transactionHelper.addIntegratedEntrust(integratedEntrustDb);
                entrustCancelProcessor.processCancelEntrustResp(cancelEntrustBean, integratedEntrustDb, cancelEntrustDb);
            }
        }
    }

    public void onTrade(Trade tradeRecord) {
        tradeCallbackHelper.onTrade(tradeRecord);
    }

    public void onRtnEntrust(Entrust entrust) {
        logger.info("onRtnEntrust {}", JsonUtils.getJsonFromObject(entrust));
        Entrust dbEntrust;
        if (!Strings.isNullOrEmpty(entrust.getId())) {
            dbEntrust = entrustService.findById(entrust.getId());
        } else {
            dbEntrust = entrustService.queryEntrustByBrokerEntrustIdAndAccount(entrust.getBrokerEntrustId(), entrust.getAccountId());
        }
        if (Objects.nonNull(dbEntrust)) {
            transactionHelper.addEntrust(dbEntrust);
            if (entrust.isNoCancelEntrust()) {
                List<Entrust> cancelEntrustList = entrustService.queryCancelEntrustByOrigEntrustIdAndAccount(dbEntrust.getId(), dbEntrust.getAccountId());
                Entrust dbCancelEntrust = null;
                if (cancelEntrustList.size() > 0) {
                    dbCancelEntrust = cancelEntrustList.get(0);
                    transactionHelper.addEntrust(dbCancelEntrust);
                    if (cancelEntrustList.size() > 1) {
                        for (int i = 1; i < cancelEntrustList.size(); i++) {
                            cancelEntrustList.get(i).setEntrustStatus(EntrustStatus.SYSTEM_INVALID.value);
                            cancelEntrustList.get(i).setErrorMsg("存在多个撤单委托");
                            transactionHelper.addEntrust(cancelEntrustList.get(i));
                        }
                    }
                }
                if (Objects.nonNull(dbCancelEntrust)) {
                    if (entrust.getEntrustStatus() >= dbEntrust.getEntrustStatus()) {
                        entrust.setInstructionId(dbEntrust.getInstructionId());
                        this.processPosition(entrust);
                    }
                    entrustCancelProcessor.processCancelEntrustByOrigEntrust(entrust, dbEntrust, dbCancelEntrust);
                } else {
                    processRtnEntrust(entrust, dbEntrust);
                }
            } else {
                processRtnEntrust(entrust, dbEntrust);
            }
        } else {
            logger.warn("return entrust not found in DB:{}", JsonUtils.getJsonFromObject(entrust));
        }
    }

    private void processRtnEntrust(EntrustInterface entrust, EntrustInterface dbEntrust) {
        dbEntrust.setReportTime(entrust.getReportTime());
        if (!Strings.isNullOrEmpty(entrust.getReportNo())) {
            dbEntrust.setReportNo(entrust.getReportNo());
        }
        if (entrust.getEntrustStatus() == INVALID.value
                || entrust.getEntrustStatus() == CANCELED.value
                || entrust.getEntrustStatus() == EntrustStatus.SYSTEM_INVALID.value) {
            handleEntrustInvalid(entrust, dbEntrust);
        }
        // 处理订单状态
        if (entrust.getEntrustStatus() >= dbEntrust.getEntrustStatus()) {
            dbEntrust.setEntrustStatus(entrust.getEntrustStatus());
            if (Strings.isNullOrEmpty(dbEntrust.getBrokerEntrustId())) {
                dbEntrust.setBrokerEntrustId(entrust.getBrokerEntrustId());
            }
            entrust.setInstructionId(dbEntrust.getInstructionId());
            this.processPosition(entrust);
        } else {
            logger.info("terminal return entrustStatus < dbEntrustStatus {}", dbEntrust.getId());
        }
    }

    /**
     * 处理头寸
     *
     * @param entrust
     */
    private void processPosition(EntrustInterface entrust) {
        if (CANCEL.value.equals(entrust.getEntrustType())) {
            logger.info("不处理头寸：entrust entrustType is cancel");
            return;
        }
        logger.info("委托回报处理头寸：{}", JsonUtils.getJsonFromObject(entrust));
        Instruction instruction = instructionService.findById(entrust.getInstructionId());
        PositionInstructionRequest instructionClone = new PositionInstructionRequest();
        BeanUtil.copyProperties(instruction, instructionClone);
        instructionClone.setSoftSkipMode(DictCodeList.是否_是.getValue());
        //委托编号
        instructionClone.setEntrustNo(entrust.getId());
//        instructionClone.setEntrustAmt(entrust.getEntrustBalance());
//        if (instructionClone.getEntrustAmt() == null) {
//            instructionClone.setEntrustAmt(entrust.getEntrustPrice().multiply(BigDecimal.valueOf(entrust.getEntrustVolume())));
//        }

        if (InvestmentAssetType1.NEGOTIATED_REPO.value.equals(instruction.getAssetType1())) {
            logger.info("场内协议回购交易下单头寸检查，指令编号：{}，回购金额：{}", entrust.getInstructionId(), entrust.getEntrustBalance());
            instructionClone.setEntrustAmt(entrust.getEntrustBalance());
        } else if (InvestmentAssetType.isBond(entrust.getAssetType()) && Objects.nonNull(entrust.getFullPrice()) && entrust.getFullPrice().compareTo(BigDecimal.ZERO) != 0) {
            //全价金额
            instructionClone.setEntrustAmt(TradeUtils.multiply(entrust.getFullPrice(), entrust.getEntrustVolume(), 2));
        } else if (Objects.isNull(entrust.getEntrustBalance()) || entrust.getEntrustBalance().compareTo(BigDecimal.ZERO) == 0) {
            // 计算委托金额
            instructionClone.setEntrustAmt(TradeUtils.multiply(entrust.getEntrustPrice(), entrust.getEntrustVolume(), 2));
        } else {
            instructionClone.setEntrustAmt(entrust.getEntrustBalance());
        }

        instructionClone.setEntrustNum(entrust.getEntrustVolume());
        instructionClone.setEntrustPrice(entrust.getEntrustPrice());
        instructionClone.setFullPrice(entrust.getFullPrice());
        instructionClone.setEntrustProp(entrust.getEntrustProp());

        int entrustStatus = entrust.getEntrustStatus();
        //撤销委托，调用头寸引擎---start
        if (entrustStatus == CANCELED.value ||
                entrustStatus == PART_TRADED_CANCELED.value) {
            if (交易方向_正回购.getValue().equals(instructionClone.getPs())
                    || 交易方向_逆回购.getValue().equals(instructionClone.getPs())
                    || 交易方向_提前购回.getValue().equals(instructionClone.getPs())
                    || 交易方向_到期购回.getValue().equals(instructionClone.getPs())
                    || 交易方向_中途换券.getValue().equals(instructionClone.getPs())
                    || 交易方向_解除质押.getValue().equals(instructionClone.getPs())
                    || 交易方向_到期续做.getValue().equals(instructionClone.getPs())
                    || 交易方向_质押券出库.getValue().equals(instructionClone.getPs())
                    || 交易方向_质押券入库.getValue().equals(instructionClone.getPs())) {
                instructionClone.setEntrustAmt(entrust.getEntrustBalance());
            } else {
                instructionClone.setEntrustAmt(entrust.getEntrustPrice().multiply(BigDecimal.valueOf(entrust.getEntrustVolume() - entrust.getTradedVolume())));
            }
            instructionClone.setEntrustNum(entrust.getEntrustVolume() - entrust.getTradedVolume());
            positionRequestServiceInstructionRequest.sendRequest(instructionClone,
                    业务动作_撤委托.getValue(), 系统动作_新建.getValue(), 系统动作_新建.getValue());
            return;
        }
        //撤销委托，调用头寸引擎---end
        //下单生成委托，调用头寸引擎---start
        if (!(entrustStatus == INVALID.value
                || entrustStatus == SYSTEM_INVALID.value)) {
            positionRequestServiceInstructionRequest.sendRequest(instructionClone,
                    业务动作_交易下单.getValue(), 系统动作_新建.getValue(), 系统动作_新建.getValue());
        }
        //下单生成委托，调用头寸引擎---end
    }

    private void handleEntrustInvalid(EntrustInterface entrust, EntrustInterface dbEntrust) {
        //不同业务的废单可能有些只有废单委托，没有废单成交，自己模拟成交
        if (!dbEntrust.getEntrustType().equals(CANCEL.value) &&
                entrust.getEntrustStatus() == INVALID.value &&
                dbEntrust.getEntrustStatus() != INVALID.value) {
            Date now = new Date(System.currentTimeMillis());
            Trade tradeRecord = new Trade();
            tradeRecord.setBrokerEntrustId(dbEntrust.getId());
            tradeRecord.setAccountId(dbEntrust.getAccountId());
            tradeRecord.setEntrustBs(dbEntrust.getEntrustBs());
            tradeRecord.setEntrustId(dbEntrust.getId());
            tradeRecord.setBrokerTradeId(String.valueOf(System.currentTimeMillis()));
            tradeRecord.setEntrustProp(dbEntrust.getEntrustProp());
            tradeRecord.setFinanceAccount(dbEntrust.getFinanceAccount());
            tradeRecord.setStockCode(StockCodeUtils.getStockCode(dbEntrust.getStockCode()));
            tradeRecord.setTradeType(TradeType.INVALID.value);
            tradeRecord.setTradeTime(simpleDateFormat.format(now));
            tradeRecord.setMarket(dbEntrust.getMarket());
            tradeRecord.setStockName(dbEntrust.getStockName());
            tradeRecord.setBsFlag(dbEntrust.getBsFlag());
            tradeRecord.setTradedPrice(dbEntrust.getEntrustPrice());
            tradeRecord.setTradedPrice(dbEntrust.getFullPrice());
            tradeRecord.setTradedVolume(dbEntrust.getEntrustVolume());
            logger.info("委托废单，模拟废单成交处理");
            Instruction instruction = instructionService.findById(dbEntrust.getInstructionId());
            transactionHelper.setInstructionBean(instruction);
            tradeCallbackHelper.processEntrustTrade(dbEntrust, tradeRecord, now, instruction);
        }
        dbEntrust.setErrorMsg(entrust.getErrorMsg());
        dbEntrust.setEntrustStatus(entrust.getEntrustStatus());
    }

    public void onRtnIntegratedEntrust(IntegratedEntrust integratedEntrust) {
        logger.info("onRtnIntegratedEntrust {}", JsonUtils.getJsonFromObject(integratedEntrust));
        IntegratedEntrust dbIntegratedEntrust;
        if (Strings.isNullOrEmpty(integratedEntrust.getId())) {
            dbIntegratedEntrust = integratedEntrustService.queryIntegratedEntrustByAccountIdAndBrokerEntrustId(integratedEntrust.getAccountId(), integratedEntrust.getBrokerEntrustId());
        } else {
            dbIntegratedEntrust = integratedEntrustService.findById(integratedEntrust.getId());
        }
        if (Objects.nonNull(dbIntegratedEntrust)) {
            transactionHelper.addIntegratedEntrust(dbIntegratedEntrust);
            if (integratedEntrust.isNoCancelEntrust()) {
                List<IntegratedEntrust> cancelEntrustList = integratedEntrustService.queryCancelIntegratedEntrustByOrigEntrustIdAndAccount(dbIntegratedEntrust.getId(), dbIntegratedEntrust.getAccountId());
                IntegratedEntrust dbCancelEntrust = null;
                if (cancelEntrustList.size() > 0) {
                    dbCancelEntrust = cancelEntrustList.get(0);
                    transactionHelper.addIntegratedEntrust(dbCancelEntrust);
                    if (cancelEntrustList.size() > 1) {
                        for (int i = 1; i < cancelEntrustList.size(); i++) {
                            cancelEntrustList.get(i).setEntrustStatus(EntrustStatus.SYSTEM_INVALID.value);
                            cancelEntrustList.get(i).setErrorMsg("存在多个撤单委托");
                            transactionHelper.addIntegratedEntrust(cancelEntrustList.get(i));
                        }
                    }
                }
                if (Objects.nonNull(dbCancelEntrust)) {
                    entrustCancelProcessor.processCancelEntrustByOrigEntrust(integratedEntrust, dbIntegratedEntrust, dbCancelEntrust);
                } else {
                    processRtnEntrust(integratedEntrust, dbIntegratedEntrust);
                }
            } else {
                processRtnEntrust(integratedEntrust, dbIntegratedEntrust);
            }
        } else {
            logger.warn("return IntegratedEntrust not found in DB:{}", JsonUtils.getJsonFromObject(integratedEntrust));
        }
    }

    public boolean cancelEntrust(EntrustInterface cancelEntrust) {
        String entrustId = cancelEntrust.getOrigEntrustId();
        if (entrustId.startsWith(IDPrefix.NORMAL.value)) {
            Entrust entrust = entrustService.findById(entrustId);
            logger.info("cancelEntrust :{}", entrustId);
            transactionHelper.addEntrust(entrust);
            transactionHelper.addEntrust((Entrust) cancelEntrust);
            return entrustCancelProcessor.processCancelEntrust(entrust, Constants.PROCESS_TYPE_ENTRUST, cancelEntrust);
        }
        if (entrustId.startsWith(IDPrefix.BULK.value)) {
            IntegratedEntrust integratedEntrust = integratedEntrustService.findById(entrustId);
            logger.info("cancelIntegratedEntrustEntrust :{}", entrustId);
            transactionHelper.addIntegratedEntrust(integratedEntrust);
            transactionHelper.addIntegratedEntrust((IntegratedEntrust) cancelEntrust);
            return entrustCancelProcessor.processCancelEntrust(integratedEntrust, Constants.PROCESS_TYPE_INTEGRATED, cancelEntrust);
        }
        return false;
    }

    public void unfrozenForInnerByCancelEntrust(EntrustInterface cancelEntrust) {
        EntrustInterface entrust;
        String entrustId = cancelEntrust.getOrigEntrustId();
        if (entrustId.startsWith(IDPrefix.NORMAL.value)) {
            entrust = entrustService.findById(entrustId);
            transactionHelper.addEntrust((Entrust) entrust);
            transactionHelper.addEntrust((Entrust) cancelEntrust);
        } else {
            entrust = integratedEntrustService.findById(entrustId);
            transactionHelper.addIntegratedEntrust((IntegratedEntrust) entrust);
            transactionHelper.addIntegratedEntrust((IntegratedEntrust) cancelEntrust);
        }
        entrustCancelProcessor.unfrozenForInner(entrust, cancelEntrust);
    }

    public void handleBatchOrder(BatchInfo batchInfo) {
        Order order = orderService.findById(batchInfo.getOrderId());
        if (OrderStatus.isFinished(order.getOrderStatus())) {
            logger.info("orderStatus[{}] is finished status, shutdown batch order", order.getOrderStatus());
            return;
        }
        transactionHelper.setBatchInfo(batchInfo);
        transactionHelper.addOrder(order);
        TradeAgent agent = tradeAgentManager.findAgentByAccountId(order.getAccountId());
        Entrust entrust = tradeHelper.convertOrderToEntrust(order, batchInfo);
        transactionHelper.addEntrust(entrust);
        FuturesInstrument futuresInstrument = futuresInstrumentService.queryByInnerCode(entrust.getStockCode());
        BigDecimal entrustBalance = TradeUtils.calculateBalance(entrust.getEntrustBs(), entrust.getEntrustPrice(), entrust.getEntrustVolume(), entrust.getAssetType(), entrust, futuresInstrument);
        Instruction instruction = instructionService.findById(entrust.getInstructionId());
        if (frozenInstructionAndPledge(entrust.getEntrustVolume(), entrustBalance, instruction, entrust)) {
            BigDecimal balance = setEntrustFrozen(entrust);
            if (Objects.nonNull(agent)) {
                entrust.setEntrustStatus(EntrustStatus.TOBE_REPORTED.value);
                entrust.setParentOrderId(order.getId());
                order.setEntrustBalance(TradeUtils.add(order.getEntrustBalance(), balance, 2));
                logger.info("handleBatchOrder entrust {}", JsonUtils.getJsonFromObject(entrust));
                agent.entrust(entrust);
            } else {
                entrust.setEntrustStatus(EntrustStatus.SYSTEM_INVALID.value);
                entrust.setErrorCode(ErrorCode.TERMINAL_NOT_ONLINE.value);
                entrust.setErrorMsg(ErrorCode.TERMINAL_NOT_ONLINE.errorMsg);
                transactionHelper.setInstructionBean(instruction);
                entrustCancelProcessor.unFrozenEntrustAndInstructionAndPosition(entrust, entrust.getEntrustVolume(), balance, instruction);
                logger.info("agent is null, when batchOrder check {} - {}", order.getAccountName(), JsonUtils.getJsonFromObject(batchInfo));
            }
            if (batchInfo.getEntrustTimes() > batchInfo.getCompleteTimes()) {
                delayController.put(batchInfo);
            } else {
                logger.info("batchInfo finished [{}], entrustTimes {}", order.getId(), batchInfo.getCompleteTimes());
            }
        } else {
            investRiskManagement.riskControlRelease(Arrays.asList(entrust), instruction);
        }
    }

    public EntrustInterface handleReviewExamine(EntrustInterface entrust, String instructionsStatus, String approveReason, int processType) {
        Instruction instructionBean = instructionService.findById(entrust.getInstructionId());
        transactionHelper.setInstructionBean(instructionBean);
        if (Constants.PROCESS_TYPE_ENTRUST == processType) {
            transactionHelper.addEntrust((Entrust) entrust);
        } else if (Constants.PROCESS_TYPE_INTEGRATED == processType) {
            transactionHelper.addIntegratedEntrust((IntegratedEntrust) entrust);
        }
        // 复核通过
        if (ApproveType.EXAMINE_APPROVAL.value.equals(instructionsStatus)) {
            TradeAgent agent = tradeAgentManager.findAgentByAccountId(entrust.getAccountId());
            if (Objects.nonNull(agent)) {
                if (Constants.PROCESS_TYPE_ENTRUST == processType) {
                    handleEntrust((Entrust) entrust, agent, MsgType.EXAMINE_SUCCESS);
                } else if (Constants.PROCESS_TYPE_INTEGRATED == processType) {
                    handIntegratedEntrust((IntegratedEntrust) entrust, agent, MsgType.EXAMINE_SUCCESS);
                }
            } else {
                entrust.setEntrustStatus(EntrustStatus.SYSTEM_INVALID.value);
                entrust.setErrorCode(ErrorCode.TERMINAL_NOT_ONLINE.value);
                entrust.setErrorMsg(ErrorCode.TERMINAL_NOT_ONLINE.errorMsg);
                entrust.setCancelVolume(entrust.getEntrustVolume());
                entrustCancelProcessor.unFrozenOrderAndEntrustAndInstruction(entrust, instructionBean);
                logger.info("account agent not online {}", entrust.getId());
            }
        } else {
            //复核拒绝
            entrust.setErrorMsg(approveReason);
            if (entrust.getEntrustStatus() != CANCELED.value) {
                entrust.setEntrustStatus(EntrustStatus.NOT_APPROVAL.value);
            }
            // 设置撤销数量供风险释放使用
            entrust.setCancelVolume(entrust.getEntrustVolume());
            entrustCancelProcessor.unFrozenOrderAndEntrustAndInstruction(entrust, instructionBean);
        }
        return entrust;
    }

    public EntrustInterface handleRevokeExamine(EntrustInterface cancelEntrust, String instructionsStatus, String approveReason, int processType) {
        String entrustId = cancelEntrust.getOrigEntrustId();
        EntrustInterface entrust = null;
        if (entrustId.startsWith(IDPrefix.NORMAL.value)) {
            entrust = entrustService.findById(entrustId);
        } else if (entrustId.startsWith(IDPrefix.BULK.value)) {
            entrust = integratedEntrustService.findById(entrustId);
        }
        if (Constants.PROCESS_TYPE_ENTRUST == processType) {
            transactionHelper.addEntrust((Entrust) entrust);
            transactionHelper.addEntrust((Entrust) cancelEntrust);
        } else if (Constants.PROCESS_TYPE_INTEGRATED == processType) {
            transactionHelper.addIntegratedEntrust((IntegratedEntrust) entrust);
            transactionHelper.addIntegratedEntrust((IntegratedEntrust) cancelEntrust);
        }
        if (cancelEntrust.getEntrustStatus() != EntrustStatus.PENDING_REVOKE.value) {
            tradeHelper.rollbackCancelStatus(entrust);
            cancelEntrust.setEntrustStatus(EntrustStatus.SYSTEM_INVALID.value);
            cancelEntrust.setErrorMsg("撤单委托状态不是待撤审");
            return entrust;
        }
        Instruction instructionBean = instructionService.findById(cancelEntrust.getInstructionId());
        transactionHelper.setInstructionBean(instructionBean);
        // 撤审通过
        if (ApproveType.EXAMINE_APPROVAL.value.equals(instructionsStatus)) {
            // 判断委托状态
            if (EntrustStatus.isFinished(entrust.getEntrustStatus())) {
                cancelEntrust.setEntrustStatus(EntrustStatus.SYSTEM_INVALID.value);
                cancelEntrust.setErrorMsg("原委托状态已终止");
                return entrust;
            }
            TradeAgent agent = tradeAgentManager.findAgentByAccountId(entrust.getAccountId());
            if (Objects.nonNull(agent)) {
                cancelEntrust.setEntrustStatus(EntrustStatus.UNREPORTED.value);
                if (Constants.PROCESS_TYPE_ENTRUST == processType || Constants.PROCESS_TYPE_INTEGRATED == processType) {
                    cancelEntrust(cancelEntrust);
                } else if (Constants.PROCESS_TYPE_FIRE_ORDER == processType) {
                }
            } else {
                tradeHelper.rollbackCancelStatus(entrust);
                cancelEntrust.setEntrustStatus(EntrustStatus.SYSTEM_INVALID.value);
                cancelEntrust.setErrorMsg("交易网关不在线");
                logger.info("account agent not online {}", entrust.getId());
            }
        } else {
            // 撤审拒绝
            tradeHelper.rollbackCancelStatus(entrust);
            cancelEntrust.setEntrustStatus(EntrustStatus.NOT_REVOKE.value);
            cancelEntrust.setErrorMsg(approveReason);
        }
        return entrust;
    }

    public void closeEntrust(EntrustInterface cancelEntrust) {
        String entrustId = cancelEntrust.getOrigEntrustId();
        if (entrustId.startsWith(IDPrefix.NORMAL.value)) {
            Entrust entrust = entrustService.findById(entrustId);
            logger.info("closeEntrust : {}", entrustId);
            transactionHelper.addEntrust(entrust);
            transactionHelper.addEntrust((Entrust) cancelEntrust);
            entrust.setOperateUser(cancelEntrust.getUserId());
            entrustCancelProcessor.unfrozenForInner(entrust, cancelEntrust, true);
        } else if (entrustId.startsWith(IDPrefix.BULK.value)) {
            IntegratedEntrust integratedEntrust = integratedEntrustService.findById(entrustId);
            logger.info("closeIntegratedEntrust : {}", entrustId);
            transactionHelper.addIntegratedEntrust(integratedEntrust);
            transactionHelper.addIntegratedEntrust((IntegratedEntrust) cancelEntrust);
            integratedEntrust.setOperateUser(cancelEntrust.getUserId());
            entrustCancelProcessor.unfrozenForInner(integratedEntrust, cancelEntrust, true);
        }
    }
}
