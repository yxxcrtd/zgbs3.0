package com.joyintech.tams.exchange.service.trade.server.agent;

import cn.hutool.core.bean.BeanUtil;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.joyintech.tams.exchange.common.bean.*;
import com.joyintech.tams.exchange.common.bean.terminal.RTGSStatusBean;
import com.joyintech.tams.exchange.common.constant.AccountLoginStatus;
import com.joyintech.tams.exchange.common.constant.ErrorCode;
import com.joyintech.tams.exchange.common.db.domain.*;
import com.joyintech.tams.exchange.common.utils.JsonUtils;
import com.joyintech.tams.exchange.service.trade.dao.domain.TerminalAgentInfo;
import com.joyintech.tams.exchange.service.trade.processor.TradeHelper;
import com.joyintech.tams.exchange.service.trade.processor.account.AccountPositionHelper;
import com.joyintech.tams.exchange.service.trade.processor.constants.BrokerReissueStatus;
import com.joyintech.tams.exchange.service.trade.processor.constants.ServiceReissueStatus;
import com.joyintech.tams.exchange.service.trade.processor.msg.MsgReceiverManager;
import com.joyintech.tams.exchange.service.trade.processor.msg.MsgType;
import com.joyintech.tams.exchange.service.trade.processor.msg.TradeMsgReceiver;
import com.joyintech.tams.exchange.service.trade.processor.others.TerminalHelper;
import com.joyintech.tams.exchange.service.trade.service.TerminalAgentInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class TradeAgentDsp implements TradeAgent {
    private static final Logger logger = LoggerFactory.getLogger(TradeAgent.class);

    @Autowired
    protected TradeAgentManager tradeAgentManager;
    @Autowired
    protected TradeHelper tradeHelper;
    @Autowired
    protected MsgReceiverManager msgReceiverManager;
    @Autowired
    protected TerminalHelper terminalHelper;
    @Autowired
    protected AccountPositionHelper accountPositionHelper;
    @Autowired
    protected AccountManager accountManager;
    @Autowired
    protected TradeAgentHelper tradeAgentHelper;

    @Autowired
    protected EntrustIdManager entrustIdManager;
    @Autowired
    protected TerminalAgentInfoService terminalAgentInfoService;

    private String terminalName;

    private AtomicInteger localOffset = new AtomicInteger(0);
    private static int timeZoneOffset = 0;

    /**
     * 记录账户对应的TradeAgent
     */
    private Map<String, TradeAgentDsp> tradeRequestMap = Maps.newConcurrentMap();

    protected Map<String, Account> accountMap = Maps.newConcurrentMap();

    protected Map<String, Integer> status = Maps.newConcurrentMap();
    protected Map<String, ServiceReissueStatus> serviceReissueStatusMap = Maps.newConcurrentMap();
    protected Map<String, BrokerReissueStatus> brokerReissueStatusMap = Maps.newConcurrentMap();

    protected Map<String, Map<String, ReissueEntrustBean>> reissueEntrustBeanMap = Maps.newConcurrentMap();
    protected Map<String, Map<String, EntrustInterface>> entrustInterfaceMapByBroker = Maps.newConcurrentMap();

    protected Map<String, List<Trade>> tradeListMapByBroker = new HashMap<>();
    protected Map<String, Map<String, ReissueTradeBean>> reissueTradeBeanMap = Maps.newConcurrentMap();

    public void clearReissueCache(String accountId) {
        reissueEntrustBeanMap.remove(accountId);
        reissueTradeBeanMap.remove(accountId);
        serviceReissueStatusMap.remove(accountId);
        brokerReissueStatusMap.remove(accountId);
        entrustInterfaceMapByBroker.remove(accountId);
        tradeListMapByBroker.remove(accountId);
    }

    @Override
    public String getTerminalName() {
        return terminalName;
    }

    public void setTerminalName(String terminalName) {
        this.terminalName = terminalName;
    }

    public TradeAgent getTradeRequest(String accountId) {
        return tradeRequestMap.get(accountId);
    }

    public void addTradeRequest(String accountId, TradeAgentDsp tradeAgent) {
        tradeRequestMap.put(accountId, tradeAgent);
    }


    public void updateTradeStatus(String accountId, AccountLoginStatus beforeReissuingSuccess, AccountLoginStatus afterReissuingSuccess) {
        logger.info("updateTradeStatus：accountId={}，beforeReissuingSuccess={}，afterReissuingSuccess={}",
                accountId, beforeReissuingSuccess.msg(), afterReissuingSuccess.msg());
        if (getStatus(accountId) == beforeReissuingSuccess.value) {
            setTradeStatus(accountId, afterReissuingSuccess);
            setTradeStatus(accountId, AccountLoginStatus.TRADING);
            clearReissueCache(accountId);
        } else {
            setTradeStatus(accountId, afterReissuingSuccess);
        }
    }

    public int getStatus(String accountId) {
        return status.getOrDefault(accountId, AccountLoginStatus.NOT_LOGGED.value);
    }

    @Override
    public void setStatus(String accountId, AccountLoginStatus status) {
        logger.info("account {} status set to: {}", accountId, status.msg);
        this.status.put(accountId, status.value);
    }

    public void setTradeStatus(String accountId, AccountLoginStatus status) {
        this.status.put(accountId, status.value);
    }

    @Override
    public Map<String, Account> getAccountMap() {
        return accountMap;
    }

    public void addAccount(String accountId, Account account) {
        accountMap.put(accountId, account);
    }

    public Account getAccount(String accountId) {
        return accountMap.get(accountId);
    }

    protected String getKey(String accountId, String type) {
        return accountId + "|" + type;
    }


    /**
     * xiazelei 修改于 2022/5/26 10:31
     * 获取公共自定义参数
     * 返回公共参数map之后可以按照各自业务编码增加新的自定义参数
     *
     * @param params
     * @return
     */
    public Map<String, Object> getCustomParam(Object... params) {
        Map<String, Object> customParam = Maps.newHashMap();
        // 设置密码配置
        customParam.put("password", getAccount(params[0].toString()).getTradePwd());
        return customParam;
    }

    /**
     * xiazelei 修改于 2022/5/26 10:31
     * 接口交易码通用适配器，只适用于functionCode和 entrustProp 的t_terminal_agent_info表配置
     * 通过functionCode和entrustProp  定位唯一的柜台接口码
     * 此逻辑将字段转换工作转移到DSP接口平台的路由中配置映射关系，不再投管系统中编码处理映射关系。
     * @param functionCode
     * @param entrustProp
     * @param param
     * @param customParam
     * @return
     *
     */
    public synchronized ResultBean triggerCodeAdapter(int functionCode, String entrustProp, Object param, Map<String, Object> customParam) {
        // 定位DSP接口平台交易码【触发器编码】
        TerminalAgentInfo agentInfo = terminalAgentInfoService.selectOne(getTerminalName(), functionCode, entrustProp);
        if (agentInfo == null) {
            String busMsg = String.format("配置异常：t_terminal_agent_info 表中未能找到“{%s}”“{%s}”的接口配置！", functionCode, entrustProp);
            return new ResultBean(ErrorCode.TERMINAL_API_ERROR.value, busMsg);
        }
        // 转换参数
        Map<String, Object> paramMap = BeanUtil.beanToMap(param);
        // 合并自定义参数
        paramMap.putAll(customParam);
        agentInfo.setAccountId(String.valueOf(paramMap.get("accountId") ));
        return tradeAgentHelper.doRequest(agentInfo, paramMap);
    }
    /**
     * xiazelei 修改于 2022/5/26 10:31
     * 接口交易码通用适配器，只适用于functionCode和 entrustProp 的t_terminal_agent_info表配置
     * 通过functionCode和entrustProp  定位唯一的柜台接口码
     * 此逻辑将字段转换工作转移到DSP接口平台的路由中配置映射关系，不再投管系统中编码处理映射关系。
     * @param functionCode
     * @param entrustProp
     * @param param
     * @param customParam
     * @return
     */
    public synchronized ResultBean triggerCodeAdapter(int functionCode, String entrustProp, Map<String, Object> param, Map<String, Object> customParam) {
        // 定位DSP接口平台交易码【触发器编码】
        TerminalAgentInfo agentInfo = terminalAgentInfoService.selectOne(getTerminalName(), functionCode, entrustProp);
        if (agentInfo == null) {
            String busMsg = String.format("配置异常：t_terminal_agent_info 表中未能找到“{%s}”“{%s}”的接口配置！", functionCode, entrustProp);
            return new ResultBean(ErrorCode.TERMINAL_API_ERROR.value, busMsg);
        }
        // 合并自定义参数
        param.putAll(customParam);
        agentInfo.setAccountId(String.valueOf(param.get("accountId") ));
        return tradeAgentHelper.doRequest(agentInfo, param);
    }

    public String genLocalId() {
        int tempOffSet = localOffset.incrementAndGet();
        if (tempOffSet > 8999) {
            tempOffSet = 0;
            localOffset.set(0);
        }
        long beginMill = System.currentTimeMillis() / (1000 * 3600 * 24) * (1000 * 3600 * 24) - timeZoneOffset;
        return String.format("%s%04d", (System.currentTimeMillis() - beginMill) / 1000, tempOffSet);
    }

    @Override
    public List<FuturesInstrument> reqFuturesInstrument(FuturesInstrumentQryBean futuresInstrumentQryBean) throws Exception {
        return null;
    }

    @Override
    public List<FuturesPositionBean> reqAccountFuturesPosition(FuturesPositionQryBean futuresPositionQryBean) throws Exception {
        return null;
    }

    @Override
    public List<FuturesPositionDetailBean> reqAccountFuturesPositionDetail(FuturesPositionDetailQryBean futuresPositionDetailQryBean) throws Exception {
        return null;
    }

    @Override
    public void reqAccountFuturesMarginRate(FuturesInstrumentMarginRateQryBean futuresInstrumentMarginRateQryBean) throws Exception {

    }

    @Override
    public void reqAccountFuturesCommissionRate(FuturesInstrumentCommissionRateQryBean futuresInstrumentCommissionRateQryBean) throws Exception {

    }

    public void onRspEntrust(TerminalId terminalId) {
        // 缓存已经回来且有柜台委托编号的委托
        if (!Strings.isNullOrEmpty(terminalId.getBrokerEntrustId())) {
            String key = terminalId.getAccountId() + "_" + terminalId.getBrokerEntrustId();
            tradeHelper.getEntrustMap().put(key, terminalId.getId());
        }
        TradeMsgReceiver msgReceiver = msgReceiverManager.getReceiverByEntrustId(terminalId.getId());
        if (Objects.nonNull(msgReceiver)) {
            msgReceiver.handleRthTerminalId(terminalId);
            // 处理主推委托 在回报之前的情况, 通知补发
            String key = terminalId.getAccountId() + "_" + terminalId.getBrokerEntrustId();
            EntrustInterface entrustInterface = tradeHelper.getEntrustNotifyByAccountIdAndBrokerEntrustId(key);
            if (Objects.nonNull(entrustInterface)) {
                tradeHelper.handleTradeNotifyEntrustRspBeforeOnEntrustRsp(msgReceiver, key, MsgType.NOTIFY_ENTRUST);
            }

            //  处理成交回报 在委托回报之前的情况
            if (tradeHelper.getTradeMap().size() > 0) {
                tradeHelper.handleTradeRspBeforeEntrustRsp(msgReceiver, tradeHelper.getTradeMap(), terminalId);
            }
        } else {
            logger.warn("onRspEntrust terminalId not found receiver: id - brokerEntrustId {} - {}", terminalId.getId(), terminalId.getBrokerEntrustId());
        }
    }

    public void onRspIntegratedEntrust(TerminalId terminalId) {
        // 缓存已经回来且有柜台委托编号的委托
        if (!Strings.isNullOrEmpty(terminalId.getBrokerEntrustId())) {
            String key = terminalId.getAccountId() + "_" + terminalId.getBrokerEntrustId();
            tradeHelper.getEntrustMap().put(key, terminalId.getId());
        }
        TradeMsgReceiver msgReceiver = msgReceiverManager.getReceiverByIntegratedEntrustId(terminalId.getId());
        if (Objects.nonNull(msgReceiver)) {
            msgReceiver.addMsg(MsgType.RESP_INTEGRATED, terminalId);
            // 处理主推委托 在回报之前的情况, 通知补发
            String key = terminalId.getAccountId() + "_" + terminalId.getBrokerEntrustId();
            EntrustInterface entrustInterface = tradeHelper.getEntrustNotifyByAccountIdAndBrokerEntrustId(key);
            if (Objects.nonNull(entrustInterface)) {
                tradeHelper.handleTradeNotifyEntrustRspBeforeOnEntrustRsp(msgReceiver, key, MsgType.NOTIFY_INTEGRATED);
            }

            //  处理成交回报 在委托回报之前的情况
            if (tradeHelper.getTradeMap().size() > 0) {
                tradeHelper.handleTradeRspBeforeEntrustRsp(msgReceiver, tradeHelper.getTradeMap(), terminalId);
            }
        } else {
            logger.warn("onRspIntegratedEntrust terminalId not found receiver: id - brokerEntrustId {} - {}", terminalId.getId(), terminalId.getBrokerEntrustId());
        }
    }

    /**
     * 处理委托下单响应
     */
    public void onRtnEntrust(Entrust entrust) {
        if (Strings.isNullOrEmpty(entrust.getId())) {
            if (Objects.nonNull(entrust.getLocalEntrustId()) || Objects.nonNull(entrust.getBrokerEntrustId())) {
                String id = tradeHelper.getEntrustId(entrust);
                if (Objects.nonNull(id)) {
                    entrust.setId(id);
                } else {
                    // 缓存对当前委托--- 处理委托回报在主推之后回来的情况， 委托回报回来之后再次补发通知（ufx）
                    String accountId = entrust.getAccountId();
                    String localEntrustId = entrust.getLocalEntrustId();
                    String brokerEntrustId = entrust.getBrokerEntrustId();
                    tradeHelper.setEntrustNotifyMap(accountId + "_" + brokerEntrustId, entrust);
                    logger.warn("entrust not has id, accountId - localEntrustId - brokerEntrustId {} - {} - {}", accountId, localEntrustId, brokerEntrustId);
                }
            } else {
                logger.warn("entrust not has id and localEntrustId, accountId - brokerEntrustId {} - {}", entrust.getAccountId(), entrust.getBrokerEntrustId());
            }
        }
        if (Objects.nonNull(entrust.getId())) {
            TradeMsgReceiver msgReceiver = msgReceiverManager.getReceiverByEntrustId(entrust.getId());
            if (Objects.nonNull(msgReceiver)) {
                msgReceiver.handleRtnEntrustDetail(entrust);
            } else {
                logger.warn("entrust not found receiver: id - brokerEntrustId {} - {}", entrust.getId(), entrust.getBrokerEntrustId());
            }
        }
    }

    /**
     * 处理综合业务下单响应
     */
    public void onRtnIntegratedEntrust(IntegratedEntrust integratedEntrust) {
        if (Strings.isNullOrEmpty(integratedEntrust.getId())) {
            if (Objects.nonNull(integratedEntrust.getLocalEntrustId()) || Objects.nonNull(integratedEntrust.getBrokerEntrustId())) {
                String id = tradeHelper.getIntegratedEntrustId(integratedEntrust);
                if (Objects.nonNull(id)) {
                    integratedEntrust.setId(id);
                } else {
                    String accountId = integratedEntrust.getAccountId();
                    String localEntrustId = integratedEntrust.getLocalEntrustId();
                    String brokerEntrustId = integratedEntrust.getBrokerEntrustId();
                    tradeHelper.setEntrustNotifyMap(accountId + "_" + brokerEntrustId, integratedEntrust);
                    logger.warn("integratedEntrust not has id, accontId - localEntrustId - brokerEntrustId {} - {} - {}", accountId, localEntrustId, brokerEntrustId);
                }
            } else {
                logger.warn("integratedEntrust not has id and localEntrustId, accountId - brokerEntrustId {} - {}", integratedEntrust.getAccountId(), integratedEntrust.getBrokerEntrustId());
            }
        }
        if (Objects.nonNull(integratedEntrust.getId())) {
            TradeMsgReceiver msgReceiver = msgReceiverManager.getReceiverByIntegratedEntrustId(integratedEntrust.getId());
            if (Objects.nonNull(msgReceiver)) {
                msgReceiver.handleRtnIntegratedEntrust(integratedEntrust);
            } else {
                logger.warn("integratedEntrust not found receiver: id - brokerEntrustId {} - {}", integratedEntrust.getId(), integratedEntrust.getBrokerEntrustId());
            }
        }
    }

    /**
     * 处理成交通知
     */
    public void onRtnTrade(Trade tradeRecord) {
        String key = tradeRecord.getAccountId() + "_" + tradeRecord.getBrokerEntrustId();
        String entrustId = tradeHelper.getEntrustMap().get(key);
        if (!Strings.isNullOrEmpty(entrustId)) {
            TradeMsgReceiver msgReceiver = msgReceiverManager.getTradeReceiverByEntrustId(entrustId);
            if (Objects.nonNull(msgReceiver)) {
                logger.info("处理成交回报信息：{} - {}", tradeRecord.getAccountId(), tradeRecord.getBrokerEntrustId());
                msgReceiver.handleRtnTradeRecord(tradeRecord);
            } else {
                logger.warn("tradeRecord  not found receiver: {}", JsonUtils.getJsonFromObject(tradeRecord));
            }
        } else {
            logger.info("tradeRecord return before entrust {} - {}", tradeRecord.getAccountId(), tradeRecord.getBrokerEntrustId());
            Map<String, List<Trade>> tradeMap = tradeHelper.getTradeMap();
            List<Trade> tradeList = tradeMap.getOrDefault(key, new ArrayList<>());
            tradeList.add(tradeRecord);
            tradeMap.put(key, tradeList);
        }
    }

    /**
     * 处理RTGS交收状态通知
     */
    public void onRtnRTGSStatus(RTGSStatusBean rtgsStatusBean) {
        tradeHelper.pushRTGSStatusToInvest(rtgsStatusBean);
    }

    /**
     * 处理撤单响应（同步响应）
     */
//    public void onRspCancelEntrust(CancelEntrustBean cancelEntrustBean) {
//        if (!Strings.isNullOrEmpty(cancelEntrustBean.getCancelBrokerEntrustId())) {
//            String key = cancelEntrustBean.getAccountId() + "_" + cancelEntrustBean.getCancelBrokerEntrustId();
//            tradeHelper.getEntrustMap().put(key, cancelEntrustBean.getCancelId());
//        }
//        TradeMsgReceiver msgReceiver = msgReceiverManager.getReceiverByEntrustId(cancelEntrustBean.getCancelId());
//        if (Objects.nonNull(msgReceiver)) {
//            msgReceiver.addMsg(MsgType.RESP_CANCEL, cancelEntrustBean);
//            // 处理主推委托 在回报之前的情况, 通知补发
//            EntrustInterface entrustInterface = tradeHelper.getEntrustNotifyByAccountIdAndBrokerEntrustId(cancelEntrustBean.getAccountId() + "_" + cancelEntrustBean.getBrokerEntrustId());
//            if (Objects.nonNull(entrustInterface)) {
//                logger.info("普通委托主推先于回报返回， 补发主推消息 accountId {}  brokerEntrustId {}", cancelEntrustBean.getAccountId(), cancelEntrustBean.getBrokerEntrustId());
//                onRtnEntrust((Entrust) entrustInterface);
//            }
//
//            //  处理成交回报 在委托回报之前的情况
//            if (tradeHelper.getTradeMap().size() > 0) {
//                tradeHelper.handleCancelTradeRspBeforeEntrustRsp(msgReceiver, tradeHelper.getTradeMap(), cancelEntrustBean);
//            }
//        } else {
//            logger.warn("onRspCancelEntrust terminalId not found receiver: id - brokerEntrustId {} - {}", cancelEntrustBean.getId(), cancelEntrustBean.getBrokerEntrustId());
//        }
//    }
    public void onRspCancelEntrust(CancelEntrustBean cancelEntrustBean) {
        if (!Strings.isNullOrEmpty(cancelEntrustBean.getCancelBrokerEntrustId())) {
            String key = cancelEntrustBean.getAccountId() + "_" + cancelEntrustBean.getCancelBrokerEntrustId();
            tradeHelper.getEntrustMap().put(key, cancelEntrustBean.getCancelId());
        }
        TradeMsgReceiver msgReceiver = msgReceiverManager.getReceiverByEntrustId(cancelEntrustBean.getCancelId());
        if (Objects.nonNull(msgReceiver)) {
            msgReceiver.addMsg(MsgType.RESP_CANCEL, cancelEntrustBean);
            // 处理主推委托 在回报之前的情况, 通知补发
            String key = cancelEntrustBean.getAccountId() + "_" + cancelEntrustBean.getCancelBrokerEntrustId();
            EntrustInterface entrustInterface = tradeHelper.getEntrustNotifyByAccountIdAndBrokerEntrustId(key);
            if (Objects.nonNull(entrustInterface)) {
                tradeHelper.handleTradeNotifyEntrustRspBeforeOnEntrustRsp(msgReceiver, key, MsgType.NOTIFY_ENTRUST);
            }

            //  处理成交回报 在委托回报之前的情况
            if (tradeHelper.getTradeMap().size() > 0) {
                tradeHelper.handleCancelTradeRspBeforeEntrustRsp(msgReceiver, tradeHelper.getTradeMap(), cancelEntrustBean);
            }
        } else {
            logger.warn("onRspCancelEntrust terminalId not found receiver: id - brokerEntrustId {} - {}", cancelEntrustBean.getId(), cancelEntrustBean.getBrokerEntrustId());
        }
    }

    /**
     * 处理综合业务撤单响应（同步响应）
     */
//    public void onRspCancelIntegratedEntrust(CancelEntrustBean cancelEntrustBean) {
//        if (!Strings.isNullOrEmpty(cancelEntrustBean.getCancelBrokerEntrustId())) {
//            String key = cancelEntrustBean.getAccountId() + "_" + cancelEntrustBean.getCancelBrokerEntrustId();
//            tradeHelper.getEntrustMap().put(key, cancelEntrustBean.getCancelId());
//        }
//        TradeMsgReceiver msgReceiver = msgReceiverManager.getReceiverByIntegratedEntrustId(cancelEntrustBean.getCancelId());
//        if (Objects.nonNull(msgReceiver)) {
//            msgReceiver.addMsg(MsgType.RESP_CANCEL, cancelEntrustBean);
//            // 处理主推委托 在回报之前的情况, 通知补发
//            EntrustInterface entrustInterface = tradeHelper.getEntrustNotifyByAccountIdAndBrokerEntrustId(cancelEntrustBean.getAccountId() + "_" + cancelEntrustBean.getBrokerEntrustId());
//            if (Objects.nonNull(entrustInterface)) {
//                logger.info("综合委托主推先于回报返回， 补发主推消息 accountId {}  brokerEntrustId {}", cancelEntrustBean.getAccountId(), cancelEntrustBean.getBrokerEntrustId());
//                onRtnIntegratedEntrust((IntegratedEntrust) entrustInterface);
//            }
//            //  处理成交回报 在委托回报之前的情况
//            if (tradeHelper.getTradeMap().size() > 0) {
//                tradeHelper.handleCancelTradeRspBeforeEntrustRsp(msgReceiver, tradeHelper.getTradeMap(), cancelEntrustBean);
//            }
//        } else {
//            logger.warn("onRspCancelIntegratedEntrust terminalId not found receiver: id - brokerEntrustId {} - {}", cancelEntrustBean.getId(), cancelEntrustBean.getBrokerEntrustId());
//        }
//    }
    public void onRspCancelIntegratedEntrust(CancelEntrustBean cancelEntrustBean) {
        if (!Strings.isNullOrEmpty(cancelEntrustBean.getCancelBrokerEntrustId())) {
            String key = cancelEntrustBean.getAccountId() + "_" + cancelEntrustBean.getCancelBrokerEntrustId();
            tradeHelper.getEntrustMap().put(key, cancelEntrustBean.getCancelId());
        }
        TradeMsgReceiver msgReceiver = msgReceiverManager.getReceiverByIntegratedEntrustId(cancelEntrustBean.getCancelId());
        if (Objects.nonNull(msgReceiver)) {
            msgReceiver.addMsg(MsgType.RESP_CANCEL, cancelEntrustBean);
            // 处理主推委托 在回报之前的情况, 通知补发
            String key = cancelEntrustBean.getAccountId() + "_" + cancelEntrustBean.getCancelBrokerEntrustId();
            EntrustInterface entrustInterface = tradeHelper.getEntrustNotifyByAccountIdAndBrokerEntrustId(key);
            if (Objects.nonNull(entrustInterface)) {
                tradeHelper.handleTradeNotifyEntrustRspBeforeOnEntrustRsp(msgReceiver, key, MsgType.NOTIFY_INTEGRATED);
            }
            //  处理成交回报 在委托回报之前的情况
            if (tradeHelper.getTradeMap().size() > 0) {
                tradeHelper.handleCancelTradeRspBeforeEntrustRsp(msgReceiver, tradeHelper.getTradeMap(), cancelEntrustBean);
            }
        } else {
            logger.warn("onRspCancelIntegratedEntrust terminalId not found receiver: id - brokerEntrustId {} - {}", cancelEntrustBean.getId(), cancelEntrustBean.getBrokerEntrustId());
        }
    }

    protected CancelEntrustBean getCancelEntrustBean(EntrustInterface entrust, EntrustInterface cancelEntrust) {
        CancelEntrustBean cancelEntrustBean = new CancelEntrustBean();
        cancelEntrustBean.setId(entrust.getId());
        cancelEntrustBean.setBrokerEntrustId(entrust.getBrokerEntrustId());
        cancelEntrustBean.setMarket(entrust.getMarket());
        cancelEntrustBean.setEntrustProp(entrust.getEntrustProp());
        cancelEntrustBean.setTradingDay(entrust.getTradingDay());
        cancelEntrustBean.setEntrustStatus(entrust.getEntrustStatus());
        cancelEntrustBean.setBsFlag(entrust.getBsFlag());
        cancelEntrustBean.setEntrustBs(entrust.getEntrustBs());
        cancelEntrustBean.setAccountId(entrust.getAccountId());
        cancelEntrustBean.setStockCode(entrust.getStockCode());
        cancelEntrustBean.setEntrustVolume(entrust.getEntrustVolume());
        cancelEntrustBean.setTradedVolume(entrust.getTradedVolume());
        cancelEntrustBean.setIsFullPrice(entrust.getIsFullPrice());
        cancelEntrustBean.setEntrustPrice(entrust.getEntrustPrice());
        cancelEntrustBean.setFullPrice(entrust.getFullPrice());
        cancelEntrustBean.setCancelId(cancelEntrust.getId());
        cancelEntrustBean.setCancelBrokerEntrustId(cancelEntrust.getBrokerEntrustId());
        return cancelEntrustBean;
    }

    /**
     * 账号委托通知补发申请
     */
    @Override
    public void reqReissueEntrust(ReissueEntrustQryBean reissueEntrustQryBean) throws Exception {
        logger.info("委托对比开始...");
        String accountId = reissueEntrustQryBean.getAccountId();
        List<ReissueEntrustBean> reissueEntrustBeanList = reissueEntrustQryBean.getReissueEntrustBeans();
        TradeAgentDsp tradeAgent = tradeRequestMap.get(accountId);
        String entrustKey = getKey(accountId, "ENTRUST");
        serviceReissueStatusMap.put(entrustKey, ServiceReissueStatus.SERVICE_REISSUING_START);
        if (Objects.nonNull(reissueEntrustBeanList) && !reissueEntrustBeanList.isEmpty()) {
            setTradeStatus(accountId, AccountLoginStatus.ENTRUST_REISSUING);
            logger.info("委托对比：委托对比中");
            boolean isLast = reissueEntrustQryBean.isLast();
            int pageNum = reissueEntrustQryBean.getPageNum();
            Map<String, ReissueEntrustBean> reissueEntrustBeanAccountMap = reissueEntrustBeanMap.get(accountId);
            if (Objects.isNull(reissueEntrustBeanAccountMap)) {
                reissueEntrustBeanAccountMap = Maps.newConcurrentMap();
                reissueEntrustBeanMap.put(accountId, reissueEntrustBeanAccountMap);
            }
            for (ReissueEntrustBean reissueEntrustBean : reissueEntrustBeanList) {
                reissueEntrustBeanAccountMap.put(reissueEntrustBean.getId(), reissueEntrustBean);
            }
            // 当第一笔数据过来时，就开始查询柜台委托
            if (pageNum == 1) {
                brokerReissueStatusMap.put(entrustKey, BrokerReissueStatus.BROKER_REISSUING_START);
                tradeAgent.reqReissueEntrust(accountId);
            }
            if (isLast) {
                serviceReissueStatusMap.put(entrustKey, ServiceReissueStatus.SERVICE_REISSUING_FINNISH);
                if (brokerReissueStatusMap.get(entrustKey).value == BrokerReissueStatus.BROKER_REISSUING_FINNISH.value) {
                    compareReissueEntrust(accountId, entrustInterfaceMapByBroker.get(accountId), tradeAgent);
                }
            }
        } else {
            logger.info("委托对比：委托对比成功");
            serviceReissueStatusMap.put(entrustKey, ServiceReissueStatus.SERVICE_REISSUING_FINNISH);
            setTradeStatus(accountId, AccountLoginStatus.ENTRUST_REISSUING_SUCCESS);
        }
    }

    public void reqReissueEntrust(String accountId) {

    }

    protected void compareReissueEntrust(String accountId, Map<String, EntrustInterface> entrustInterfaceMap) {
        logger.info("委托对比：柜台组装数据");
        String entrustKey = getKey(accountId, "ENTRUST");
        brokerReissueStatusMap.put(entrustKey, BrokerReissueStatus.BROKER_REISSUING_FINNISH);
        if (serviceReissueStatusMap.get(entrustKey).value == ServiceReissueStatus.SERVICE_REISSUING_FINNISH.value) {
            compareReissueEntrust(accountId, entrustInterfaceMap, tradeRequestMap.get(accountId));
        } else {
            entrustInterfaceMapByBroker.put(accountId, entrustInterfaceMap);
        }
    }

    public void compareReissueEntrust(String accountId, Map<String, EntrustInterface> entrustInterfaceMap, TradeAgent tradeAgent) {
        if (!reissueEntrustBeanMap.isEmpty()) {
            Map<String, ReissueEntrustBean> reissueEntrustBeanAccountMap = this.reissueEntrustBeanMap.get(accountId);
            logger.info("开始比对委托信息 accountId {} entrustInterfaceMapByBroker {} reissueEntrustBeanMapCache {} reissueEntrustBeanAccountMap {}",
                    accountId, entrustInterfaceMap.size(), reissueEntrustBeanMap.size(), reissueEntrustBeanAccountMap.size());
            reissueEntrustBeanAccountMap.forEach((s, reissueEntrustBean) -> {
                if (!reissueEntrustBean.isBulk()) {
                    Entrust entrust = (Entrust) entrustInterfaceMap.get(s);
                    if (Objects.nonNull(entrust) && entrust.getEntrustStatus() != reissueEntrustBean.getEntrustStatus()) {
                        onRtnEntrust(entrust);
                    }
                } else {
                    EntrustInterface entrustInterface = entrustInterfaceMap.get(s);
                    IntegratedEntrust integratedEntrust;
                    if (entrustInterface instanceof Entrust) {
                        integratedEntrust = new IntegratedEntrust();
                        BeanUtils.copyProperties(entrustInterface, integratedEntrust);
                    } else {
                        integratedEntrust = (IntegratedEntrust) entrustInterfaceMap.get(s);
                    }
                    if (Objects.nonNull(integratedEntrust) && integratedEntrust.getEntrustStatus() != reissueEntrustBean.getEntrustStatus()) {
                        onRtnIntegratedEntrust(integratedEntrust);
                    }
                }
            });
        }
        logger.info("委托对比：更新状态");
        updateTradeStatus(accountId, AccountLoginStatus.TRADE_REISSUING_SUCCESS, AccountLoginStatus.ENTRUST_REISSUING_SUCCESS);
    }

    /**
     * 账号成交通知补发申请
     */
    @Override
    public void reqReissueTrade(ReissueTradeQryBean reissueTradeQryBean) throws Exception {
        logger.info("成交对比开始....");
        String accountId = reissueTradeQryBean.getAccountId();
        TradeAgentDsp tradeAgent = tradeRequestMap.get(accountId);
        List<ReissueTradeBean> ReissueTradeBeanList = reissueTradeQryBean.getReissueTradeBeans();
        String tradeKey = getKey(accountId, "TRADE");
        serviceReissueStatusMap.put(tradeKey, ServiceReissueStatus.SERVICE_REISSUING_START);
        if (Objects.nonNull(ReissueTradeBeanList) && !ReissueTradeBeanList.isEmpty()) {
            logger.info("成交对比：成交对比中");
            boolean isLast = reissueTradeQryBean.isLast();
            int pageNum = reissueTradeQryBean.getPageNum();
            Map<String, ReissueTradeBean> reissueTradeBeanAccountMap = reissueTradeBeanMap.get(accountId);
            if (Objects.isNull(reissueTradeBeanAccountMap)) {
                reissueTradeBeanAccountMap = Maps.newConcurrentMap();
                reissueTradeBeanMap.put(accountId, reissueTradeBeanAccountMap);
            }
            for (ReissueTradeBean reissueTradeBean : ReissueTradeBeanList) {
                reissueTradeBeanAccountMap.put(reissueTradeBean.getBrokerTradeId(), reissueTradeBean);
            }
            // 当第一笔数据过来时，就开始查询柜台成交
            if (pageNum == 1) {
                brokerReissueStatusMap.put(tradeKey, BrokerReissueStatus.BROKER_REISSUING_START);
                tradeAgent.reqReissueTrade(accountId);
            }
            if (isLast) {
                // service数据组装完成， 可以进行比对
                serviceReissueStatusMap.put(tradeKey, ServiceReissueStatus.SERVICE_REISSUING_FINNISH);
                if (brokerReissueStatusMap.get(tradeKey).value == BrokerReissueStatus.BROKER_REISSUING_FINNISH.value) {
                    compareReissueTrade(accountId, tradeListMapByBroker.get(accountId), tradeAgent);
                }
            }
        } else {
            logger.info("成交对比：成交对比成功");
            serviceReissueStatusMap.put(tradeKey, ServiceReissueStatus.SERVICE_REISSUING_FINNISH);
            updateTradeStatus(accountId, AccountLoginStatus.ENTRUST_REISSUING_SUCCESS, AccountLoginStatus.TRADE_REISSUING_SUCCESS);
        }
    }

    public void reqReissueTrade(String accountId) {

    }

    public void compareReissueTrade(String accountId, List<Trade> tradeList) {
        logger.info("成交对比：柜台组装数据");
        String tradeKey = getKey(accountId, "TRADE");
        brokerReissueStatusMap.put(tradeKey, BrokerReissueStatus.BROKER_REISSUING_FINNISH);
        if (serviceReissueStatusMap.get(tradeKey).value == ServiceReissueStatus.SERVICE_REISSUING_FINNISH.value) {
            compareReissueTrade(accountId, tradeList, tradeRequestMap.get(accountId));
        } else {
            tradeListMapByBroker.put(accountId, tradeList);
        }
    }

    public void compareReissueTrade(String accountId, List<Trade> tradeList, TradeAgentDsp tradeAgent) {
        // 成交比对时， 判断委托是否比对完成
        int status = tradeAgent.getStatus(accountId);
        boolean reissueEntrustSuccess = status == AccountLoginStatus.ENTRUST_REISSUING_SUCCESS.value;
        tradeAgent.setTradeStatus(accountId, AccountLoginStatus.TRADE_REISSUING);
        Map<String, ReissueTradeBean> reissueTradeBeanAccountMap = this.reissueTradeBeanMap.get(accountId);
        logger.info("开始比对成交信息 accountId {} tradeListByBroker {} reissueTradeBeanMapCache {} reissueTradeBeanAccountMap {}",
                accountId, tradeList.size(), reissueTradeBeanMap.size(), reissueTradeBeanAccountMap.size());
        tradeList.forEach(trade -> {
            ReissueTradeBean reissueTradeBean = reissueTradeBeanAccountMap.get(trade.getBrokerTradeId());
            if (Objects.isNull(reissueTradeBean)) {
                onRtnTrade(trade);
            }
        });
        logger.info("成交对比：更新状态");
        updateTradeStatus(accountId, reissueEntrustSuccess ? AccountLoginStatus.TRADE_REISSUING : AccountLoginStatus.ENTRUST_REISSUING_SUCCESS, AccountLoginStatus.TRADE_REISSUING_SUCCESS);
    }

}
