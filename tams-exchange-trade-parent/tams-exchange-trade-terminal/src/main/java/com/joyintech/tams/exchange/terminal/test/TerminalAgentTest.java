package com.joyintech.tams.exchange.terminal.test;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.joyintech.tams.exchange.common.bean.*;
import com.joyintech.tams.exchange.common.bean.bond.StandardBondQryBean;
import com.joyintech.tams.exchange.common.constant.EntrustStatus;
import com.joyintech.tams.exchange.common.constant.ErrorCode;
import com.joyintech.tams.exchange.common.constant.TradeType;
import com.joyintech.tams.exchange.common.db.domain.*;
import com.joyintech.tams.exchange.common.net.protocol.FunctionCode;
import com.joyintech.tams.exchange.common.net.protocol.ProtocolMessage;
import com.joyintech.tams.exchange.common.utils.JsonUtils;
import com.joyintech.tams.exchange.common.utils.TradeUtils;
import com.joyintech.tams.exchange.terminal.agent.ITerminalMsgHandler;
import com.joyintech.tams.exchange.terminal.agent.ITerminalMsgNotify;
import com.joyintech.tams.exchange.terminal.agent.TerminalAgent;
import io.netty.channel.ChannelFuture;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.*;


public class TerminalAgentTest extends TerminalAgent implements ITerminalMsgHandler, ITerminalMsgNotify {
    private static final Logger logger = LoggerFactory.getLogger(TerminalAgentTest.class);

    private static Map<String, Long> requestIdAccountMap = Maps.newConcurrentMap();//AccountId-requestId
    private static Map<String, Long> requestIdEntrustMap = Maps.newConcurrentMap();//EntrustId-requestId

    @Override
    public Long getRequestIdByEntrustId(String id) {
        return requestIdEntrustMap.get(id);
    }

    public void setRequestIdEntrustMap(String entrustId, long requestId) {
        requestIdEntrustMap.put(entrustId, requestId);
    }

    private List<EntrustInterface> cancelEntrustList = new ArrayList<>();

    public List<EntrustInterface> getCancelEntrustList() {
        return cancelEntrustList;
    }

    private Map<String, Entrust> entrustMap = new HashMap<>();// 记录下单成功普通委托
    private Map<String, IntegratedEntrust> integratedEntrustMap = new HashMap<>();// 记录下单成功综合委托


    private List<Account> accountList = new ArrayList<>();

    public List<Account> getAccountList() {
        return accountList;
    }

    // 记录server掉线后，未回复的trade回报
    private List<RepairConnectionMsg> respList = new ArrayList<>();

    public List<RepairConnectionMsg> getRespList() {
        return respList;
    }

    public TerminalAgentTest() {
        this.channel = new NioServerSocketChannel();
    }

    @Override
    public Object syncWrite(FunctionCode functionCode, Object object, int dateType, long timeout) throws Exception {
        return null;
    }

    @Override
    public Object syncWrite(FunctionCode functionCode, Object object) throws Exception {
        return null;
    }

    @Override
    public Object syncWrite(FunctionCode functionCode, int dataType, Object object) throws Exception {
        return null;
    }

    @Override
    public ChannelFuture asyncWrite(FunctionCode functionCode, long requestId, int dateType, Object object) {
        // 登录数据接口对比
        if (FunctionCode.RESP_ACCOUNT_LOGIN.value == functionCode.value) {
            logger.info("----------------- 账号登录 -----------");
            ResultBean resultBean = (ResultBean) object;
            logger.info("账号登录结果： {}", resultBean.getErrorMsg());
            if (resultBean.getErrorCode() == ErrorCode.SUCCESS.value) {
                accountList.add((Account) resultBean.getData());
            }
        } else if (FunctionCode.RESP_STANDARD_BONDS.value == functionCode.value) {
            logger.info("标准券信息查询成功： {}", JsonUtils.getJsonFromObject(object));
        } else if (FunctionCode.RESP_PLEDGE_DISCOUNT.value == functionCode.value) {
            logger.info("债券质押率信息查询成功： {}", JsonUtils.getJsonFromObject(object));
        } else if (FunctionCode.RESP_QRY_INSTRUMENT_INFO.value == functionCode.value) {
            logger.info("合约数据查询成功： {}", JsonUtils.getJsonFromObject(object));
        } else if (FunctionCode.RESP_QRY_CUSTOMER_FINANCE.value == functionCode.value) {
            logger.info("客户资产信息查询成功： {}", JsonUtils.getJsonFromObject(object));
        } else if (FunctionCode.RESP_QRY_HISTORY_TRADE.value == functionCode.value) {
            logger.info("对账单信息查询成功： {}", JsonUtils.getJsonFromObject(object));
        } else if (FunctionCode.RESP_QRY_ENABLE.value == functionCode.value) {
            logger.info("最大可交易数量信息查询成功： {}", JsonUtils.getJsonFromObject(object));
        } else if (FunctionCode.RESP_QRY_INTEGRATED_QUOTE.value == functionCode.value) {
            logger.info("综合行情信息查询成功： {}", JsonUtils.getJsonFromObject(object));
        } else if (FunctionCode.RESP_QRY_ENTRUST.value == functionCode.value) {
            logger.info("------------------ 查询柜台委托信息成功 ------------------");
            List<Entrust> entrustList = (List<Entrust>) object;
            if (Objects.nonNull(entrustList)) {
                entrustList.forEach(entrust -> {
                    Entrust entrustLocal = entrustMap.get(entrust.getBrokerEntrustId());
                    if (Objects.nonNull(entrustLocal)) {
                        compareEntrustStatus(entrust, entrustLocal);
                    } else {
                        IntegratedEntrust integratedEntrustLocal = integratedEntrustMap.get(entrust.getBrokerEntrustId());
                        if (Objects.nonNull(integratedEntrustLocal)) {
                            compareEntrustStatus(entrust, integratedEntrustLocal);
                        }
                    }
                });
            }
        } else if (FunctionCode.RESP_QRY_TRADE.value == functionCode.value) {
            logger.info("查询柜台成交信息成功: {}", JsonUtils.getJsonFromObject(object));
        }
        return null;
    }

    private void compareEntrustStatus(EntrustInterface entrust, EntrustInterface entrustLocal) {
        if (entrust.getEntrustStatus() == EntrustStatus.REPORTED.value) {
            logger.info("委托{} -- {} 处于已报状态 !!!", entrustLocal.getId(), entrustLocal.getBrokerEntrustId());
            cancelEntrustList.add(entrustLocal);
        } else if (entrust.getEntrustStatus() == EntrustStatus.REPORTED_TOBE_CANCEL.value) {
            logger.info("委托{} -- {} 处于已报待撤状态 !!!", entrustLocal.getId(), entrustLocal.getBrokerEntrustId());
        } else if (entrust.getEntrustStatus() == EntrustStatus.PART_TRADED_TOBE_CANCELED.value) {
            logger.info("委托{} -- {} 处于部成待撤状态 !!!", entrustLocal.getId(), entrustLocal.getBrokerEntrustId());
        } else if (entrust.getEntrustStatus() == EntrustStatus.PART_SUCCESS.value) {
            logger.info("委托{} -- {} 处于部成状态 !!!", entrustLocal.getId(), entrustLocal.getBrokerEntrustId());
            cancelEntrustList.add(entrustLocal);
        } else if (entrust.getEntrustStatus() == EntrustStatus.PART_TRADED_CANCELED.value) {
            logger.info("委托{} -- {} 处于部撤状态 !!!", entrustLocal.getId(), entrustLocal.getBrokerEntrustId());
        } else if (entrust.getEntrustStatus() == EntrustStatus.CANCELED.value) {
            logger.info("委托{} -- {} 处于已撤状态 !!!", entrustLocal.getId(), entrustLocal.getBrokerEntrustId());
        } else if (entrust.getEntrustStatus() == EntrustStatus.SUCCEEDED.value) {
            logger.info("委托{} -- {} 处于已成状态 !!!", entrustLocal.getId(), entrustLocal.getBrokerEntrustId());
        } else {
            logger.warn("委托{} -- {} 下单失败，订单状态 {} 异常 ---{} --- {} !!!", entrustLocal.getId(), entrustLocal.getBrokerEntrustId(), entrust.getEntrustStatus(), entrust.getErrorCode(), entrust.getErrorMsg());
        }
    }

    @Override
    public void asyncWrite(FunctionCode functionCode, long requestId, int dateType, byte[] object) {

    }

    @Override
    public void asyncWrite(FunctionCode functionCode, long requestId, int dateType, String object) {

    }

    @Override
    public ChannelFuture asyncWrite(FunctionCode functionCode, Object object) {
        return null;
    }

    @Override
    public void asyncWrite(ProtocolMessage message) {

    }

    // 将终端转换并进行装换的参数进行处理对比，判断是否合法
    public void compareEntrust() {
        if (respList.size() > 0) {
            respList.forEach(this::handleRespEntrust);
        }
    }

    // 将终端转换并进行装换的参数进行处理对比，判断是否合法
    public void compareIntegratedEntrustEntrust() {
        if (respList.size() > 0) {
            respList.forEach(this::handleRespEntrust);
        }
    }

    private void handleRespEntrust(RepairConnectionMsg repairConnectionMsg) {
        if (repairConnectionMsg.getFunctionCode().value == FunctionCode.RESP_INTEGRATED_ENTRUST.value
                || repairConnectionMsg.getFunctionCode().value == FunctionCode.RESP_ENTRUST.value) {
            EntrustInterface entrust = (EntrustInterface) repairConnectionMsg.getObj();
            if (entrust.getId().startsWith("e")) {
                logger.info("------------ 处理竞价交易委托返回信息 ------------");
            } else if (entrust.getId().startsWith("i")) {
                logger.info("------------ 处理综合交易委托返回信息 ------------");
            }
            if (entrust.getErrorCode() == ErrorCode.SUCCESS.value) {
                if (Strings.isNullOrEmpty(entrust.getBrokerEntrustId())) {
                    logger.error("柜台接口返回值没有正确填充柜台委托编号 BrokerEntrustId is null or empty !!!");
                } else {
                    logger.info("下单接口对接成功：entrust is {} brokerEntrustId is {}", JsonUtils.getJsonFromObject(entrust), entrust.getBrokerEntrustId());
                    if (entrust.getId().startsWith("e")) {
                        entrustMap.put(entrust.getBrokerEntrustId(), (Entrust) entrust);
                    } else if (entrust.getId().startsWith("i")) {
                        integratedEntrustMap.put(entrust.getBrokerEntrustId(), (IntegratedEntrust) entrust);
                    }
                }
            } else {
                logger.error("请检查下单参数填写是否满足要求：entrust is {} 错误码： {}  错误信息： {}", JsonUtils.getJsonFromObject(entrust), entrust.getErrorCode(), entrust.getErrorMsg());
            }
        }
    }


    @Override
    public void onReqQryEntrusts(String accountId, long requestId) {

    }

    @Override
    public void onReqQryTrades(String accountId, long requestId) {

    }

    @Override
    public void onReqEntrust(Entrust entrust, long requestId) {

    }

    @Override
    public void onReqIntegratedEntrust(IntegratedEntrust integratedEntrust, long requestId) {

    }

    @Override
    public void onReqCancelEntrust(CancelEntrustBean entrust, long requestId) {

    }

    @Override
    public void onReqCancelIntegratedEntrust(CancelEntrustBean cancelEntrustBean, long requestId) {

    }

    @Override
    public void onReqLoginAccount(AccountLoginReqBean accountLoginReqBean, long requestId) {

    }

    @Override
    public void onServiceNode(List<ServiceNode> nodeList, long requestId) {

    }

    @Override
    public void onReqLogoutAccount(Account account, long requestId) {

    }

    @Override
    public void onTerminalLogin(Map<ServerExtendInfo, List<Account>> accountMap) {

    }

    @Override
    public void onReqIntegratedQuotes(QuoteQryBean quoteQryBean, long requestId) {

    }

    @Override
    public void onReqEnable(EnableQryBean qryBean, long requestId) {

    }

    @Override
    public void onReqHistoryTrade(HistoryQryBean historyQryBean, long requestId) {

    }

    @Override
    public void onReqAccountFinance(String accountId, long requestId) {

    }

    @Override
    public void onReqInstrumentInfo(InstrumentQryBean instrumentQryBean, long requestId) {

    }

    @Override
    public void onReqPledgeDiscount(String accountId, String market, String stockCode, long requestId) {

    }

    @Override
    public void onReqStandardBonds(StandardBondQryBean standardBondQryBean, long requestId) {

    }

    @Override
    public void onEntrust(Entrust entrust) {
        logger.info("---------------------- 竞价委托回报通知 ------------------");
        logger.info("主动查询柜台普通委托成功： entrustFromBroker is {}", JsonUtils.getJsonFromObject(entrust));
        Entrust entrustLocal = entrustMap.get(entrust.getBrokerEntrustId());
        if (Objects.nonNull(entrustLocal)) {
            logger.info("本地缓存委托信息： entrustFromLocal is {}", JsonUtils.getJsonFromObject(entrust));
            // 检测报单ID，价格、金额、数量 是否与原委托一致
            if (Objects.isNull(entrust.getId())) {
                logger.warn("主动查询柜台普通委托：  无委托id信息，请确认是否合法！！！");
            } else if (!entrust.getId().equals(entrustLocal.getId())) {
                logger.warn("主动查询柜台普通委托： 委托id信息[{}]和本地委托id[{}]不一致，请确认是否合法！！！", entrust.getId(), entrustLocal.getId());
            }
            if (entrust.getEntrustPrice().compareTo(entrustLocal.getEntrustPrice()) != 0) {
                logger.warn("主动查询柜台普通委托： 委托价格信息[{}]和本地委托价格{{}}不一致，请确认是否合法！！！", entrust.getEntrustPrice(), entrustLocal.getEntrustPrice());
            }
            if (entrust.getEntrustVolume() != entrustLocal.getEntrustVolume()) {
                logger.warn("主动查询柜台普通委托： 委托数量信息[{}]和本地委托数量{{}}不一致，请确认是否合法！！！", entrust.getEntrustVolume(), entrustLocal.getEntrustVolume());
            }
            // 对比订单状态
            compareEntrustStatus(entrust, entrustLocal);
        } else {
            logger.warn("无本地委托缓存信息： brokerEntrustId is {}", entrust.getBrokerEntrustId());
        }
    }

    @Override
    public void onIntegratedEntrust(IntegratedEntrust integratedEntrust) {
        logger.info("------------------ 综合委托回报通知 --------------------");
        logger.info("主动查询柜台综合委托成功： integratedEntrustFromBroker is {}", JsonUtils.getJsonFromObject(integratedEntrust));
        IntegratedEntrust integratedEntrustLocal = integratedEntrustMap.get(integratedEntrust.getBrokerEntrustId());
        if (Objects.nonNull(integratedEntrustLocal)) {
            logger.info("本地缓存委托信息： integratedEntrustFromLocal is {}", JsonUtils.getJsonFromObject(integratedEntrust));
            // 检测报单ID，价格、金额、数量 是否与原委托一致
            if (Objects.isNull(integratedEntrust.getId())) {
                logger.warn("主动查询柜台综合委托：  无委托id信息，请确认是否合法！！！");
            } else if (!integratedEntrust.getId().equals(integratedEntrustLocal.getId())) {
                logger.warn("主动查询柜台综合委托： 委托id信息[{}]和本地委托id[{}]不一致，请确认是否合法！！！", integratedEntrust.getId(), integratedEntrustLocal.getId());
            }
            if (integratedEntrust.getEntrustPrice().compareTo(integratedEntrustLocal.getEntrustPrice()) != 0) {
                logger.warn("主动查询柜台综合委托： 委托价格信息[{}]和本地委托价格{{}}不一致，请确认是否合法！！！", integratedEntrust.getEntrustPrice(), integratedEntrustLocal.getEntrustPrice());
            }
            if (integratedEntrust.getEntrustVolume() != integratedEntrustLocal.getEntrustVolume()) {
                logger.warn("主动查询柜台综合委托： 委托数量信息[{}]和本地委托数量{{}}不一致，请确认是否合法！！！", integratedEntrust.getEntrustVolume(), integratedEntrustLocal.getEntrustVolume());
            }
            if (integratedEntrust.getEntrustBalance().compareTo(integratedEntrustLocal.getEntrustBalance()) != 0) {
                logger.warn("主动查询柜台综合委托： 委托数量信息[{}]和本地委托数量{{}}不一致，请确认是否合法！！！", integratedEntrust.getEntrustBalance(), integratedEntrustLocal.getEntrustBalance());
            }
            // 对比订单状态
            compareEntrustStatus(integratedEntrust, integratedEntrustLocal);
        } else {
            logger.warn("无本地委托缓存信息： brokerEntrustId is {}", integratedEntrust.getBrokerEntrustId());
        }
    }

    @Override
    public void onTrade(Trade trade) {
        logger.info("------------------------------- 成交回报通知 -----------------------------------------");
        logger.info("主动查询柜台成交成功： trade is {}", JsonUtils.getJsonFromObject(trade));
        Entrust entrust = entrustMap.get(trade.getBrokerEntrustId());
        if (Strings.isNullOrEmpty(trade.getBrokerEntrustId())) {
            logger.error("成交信息中没有正确填充柜台委托编号信息: brokerEntrustId is null or empty !");
            return;
        } else {
            if (Objects.isNull(entrust)) {
                IntegratedEntrust integratedEntrust = integratedEntrustMap.get(trade.getBrokerEntrustId());
                if (Objects.isNull(integratedEntrust)) {
                    logger.warn("该成交信息[{}]未匹配到对应的委托信息 ！！！", trade.getBrokerEntrustId());
                    return;
                }
            }
        }
        if (Strings.isNullOrEmpty(trade.getBrokerTradeId())) {
            logger.error("成交信息中没有正确填充成交编号信息: brokerTradeId is null or empty !");
        }
        if (Strings.isNullOrEmpty(trade.getTradingDay())) {
            logger.error("成交信息中没有正确填充交易日期信息: tradingDay is null or empty !");
        }
        if (trade.getTradedVolume() < 0) {
            logger.error("成交信息中没有正确填充成交数量信息: tradedVolume is less than zero !");
        }
        if (Objects.isNull(trade.getTradedPrice())
                || TradeUtils.lessThan(trade.getTradedPrice(), BigDecimal.ZERO)) {
            logger.error("成交信息中没有正确填充成交数量信息: tradedVolume is null or less than zero !");
        }
        if (trade.getTradeType() < 0) {
            logger.error("成交信息中没有正确填充成交类型信息: tradeType is less than zero !");
        } else {
            // 委托状态已成、部成、部撤、已撤、废单判定
            if (trade.getTradeType() == TradeType.NORMAL.value) {
                if (entrust.getEntrustVolume() > trade.getTradedVolume()) {
                    logger.info("该委托状为部成 {}", trade.getBrokerEntrustId());
                } else if (entrust.getEntrustVolume() == trade.getTradedVolume()) {
                    logger.info("该委托状为已成 {}", trade.getBrokerEntrustId());
                }
            } else if (trade.getTradeType() == TradeType.CANCEL.value) {
                if (entrust.getEntrustVolume() > trade.getTradedVolume()) {
                    logger.info("该委托状为部撤 {}", trade.getBrokerEntrustId());
                } else if (entrust.getEntrustVolume() == trade.getTradedVolume()) {
                    logger.info("该委托状为已撤 {}", trade.getBrokerEntrustId());
                }
            } else {
                if (entrust.getEntrustVolume() > trade.getTradedVolume()) {
                    logger.info("该委托状为废单 {}", trade.getBrokerEntrustId());
                }
            }
        }
    }
}
