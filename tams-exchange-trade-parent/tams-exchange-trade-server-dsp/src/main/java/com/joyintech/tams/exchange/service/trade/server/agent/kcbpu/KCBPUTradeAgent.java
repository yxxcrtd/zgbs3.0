package com.joyintech.tams.exchange.service.trade.server.agent.kcbpu;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.joyintech.tams.exchange.common.bean.*;
import com.joyintech.tams.exchange.common.bean.bond.StandardBondQryBean;
import com.joyintech.tams.exchange.common.bean.request.RepurchaseQuoteReceiveQryRequest;
import com.joyintech.tams.exchange.common.bean.response.quote.repurchase.RepurchaseQuoteReceiveResponse;
import com.joyintech.tams.exchange.common.constant.*;
import com.joyintech.tams.exchange.common.db.domain.*;
import com.joyintech.tams.exchange.common.net.protocol.FunctionCode;
import com.joyintech.tams.exchange.common.utils.IntegratedEntrustUtils;
import com.joyintech.tams.exchange.common.utils.JsonUtils;
import com.joyintech.tams.exchange.service.trade.server.agent.TradeAgentDsp;
import com.joyintech.tams.exchange.service.trade.util.AgentUtil;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component("kcbpu")
public class KCBPUTradeAgent extends TradeAgentDsp {

    // region 私有对象
    private static final Logger logger = LoggerFactory.getLogger(KCBPUTradeAgent.class);
    @Value("${agent.query.count:2}")
    private int kcbpuTradeCount;
    /**
     * 查询线程
     */
    private List<ThreadPoolExecutor> poolExecutors = Lists.newArrayListWithCapacity(kcbpuTradeCount);
    private ThreadPoolExecutor poolExecutor = null;
    private Map<String, Integer> entrustIdAndStatusMap = Maps.newConcurrentMap();
    private volatile Map<String, PositionStr> positionStrMap = Maps.newConcurrentMap();
    private final String entrustPrefix = "e";
    private final String iEntrustPrefix = "i";
    /**
     * 记录已经登录的账号
     */
    private Map<String, KCBPUAccount> kcbpuAccounts = Maps.newConcurrentMap();

    public KCBPUAccount getkcbpuAccount(String accountId) {
        return kcbpuAccounts.get(accountId);
    }

    public void addkcbpuAccount(KCBPUAccount kcbpuAccount) {
        kcbpuAccounts.put(kcbpuAccount.getAccountId(), kcbpuAccount);
    }
    //  endregion

    // region  登录与退出

    @PostConstruct
    public void init() {
        setTerminalName(AgentType.KINGDOM_KCBPU.value);
    }

    /**
     * 301登录 登录资金账户
     *
     * @param accountLoginReqBean
     * @return
     */
    @Override
    public ResultBean reqLoginAccount(AccountLoginReqBean accountLoginReqBean) {
        // 场景代码
        int functionCode = FunctionCode.REQ_ACCOUNT_LOGIN.value;
        // 场景属性
        String entrustProp = "Login";

        try {
            Account account = accountLoginReqBean.getAccount();
            String accountId = account.getId();
            AgentUtil.SetDecodeBase64(account);
            Map<String, Object> customParam = new ConcurrentHashMap<>();
            // 用户标识类型 U:用户代码 Z：资金账号 S:证件号 C：别名 其他：股东账户
            customParam.put("usercls", "Z");
            customParam.put("procName", "安徽兆尹");
            customParam.put("terminalInfo", "投资管理系统");
            ResultBean resultBean = triggerCodeAdapter(functionCode, entrustProp, accountLoginReqBean, customParam);
            //账户登录成功，记录登录成功的账户
            if (resultBean.getErrorCode() == ErrorCode.SUCCESS.value) {
                logger.info("account {} login success", accountId);
                KCBPUAccount kcbpuAccount = KCBPUMsgParseHelper.parseAccount(AgentUtil.getQueryListKCBPW(resultBean), accountId);
                resultBean.setData(kcbpuAccount);
                //保存登录信息
                addkcbpuAccount(kcbpuAccount);
                tradeAgentManager.addAccounts(this, accountId);
                tradeAgentManager.addAgentLoginStatus(accountId, true);
                accountManager.addAccounts(account);
                addTradeRequest(accountId, this);
                addAccount(accountId, account);
                setStatus(account.getId(), AccountLoginStatus.LOGGED);

                //登录成功后发送重传请求
                terminalHelper.reqReissueEntrust(accountId);
                terminalHelper.reqReissueTrade(accountId);

                //账户持仓缓存
                accountPositionHelper.positionCache(this, accountId);
            } else {
                logger.info("account {} login failed {} -- {}", accountId, resultBean.getErrorCode(), resultBean.getErrorMsg());
            }
            return resultBean;
        } catch (Exception e) {
            logger.error("reqLoginAccount: {} error:{}", JsonUtils.getJsonFromObject(accountLoginReqBean), e);
            return new ResultBean(ErrorCode.TERMINAL_API_ERROR, e.getMessage());
        }
    }

    @Override
    public void reqLogoutAccount(Account account) {

    }
    // endregion

    // region  下单与撤单

    /**
     * 403委托卖卖业务
     *
     * @param entrust
     */
    @Override
    public void entrust(Entrust entrust) {
        logger.info("send entrust:{}", entrust.getId());
        // 场景代码
        int functionCode = FunctionCode.REQ_ENTRUST.value;
        // 场景属性
        String entrustProp = "Bidding";

        try {
            // region 参数组装
            // 自定义参数
            Map<String, Object> customParam = getCustomParam(entrust.getAccountId());
            customParam.put("market", AgentUtil.ExchangeMarketKCBPU(entrust.getMarket()));
            customParam.put("bsFlag", AgentUtil.GetBSFlagForKCBPU(entrust.getBsFlag()));
            // endregion
            ResultBean resultBean = triggerCodeAdapter(functionCode, entrustProp, entrust, customParam);

            if (resultBean.getErrorCode() == ErrorCode.SUCCESS.value) {
                List<Map<String, String>> resList = (List<Map<String, String>>) resultBean.getData();
                for (Map<String, String> resMap : resList) {
                    entrust.setBrokerEntrustId(resMap.get("BIZ_NO"));
                    entrust.setReportNo(AgentUtil.GetMapValue(resMap, "ORDER_ID"));
                }
            } else {
                entrust.setEntrustStatus(EntrustStatus.INVALID.value);
                entrust.setErrorCode(resultBean.getErrorCode());
                entrust.setErrorMsg(resultBean.getErrorMsg());
            }
            TerminalId terminalId = new TerminalId();
            BeanUtils.copyProperties(entrust, terminalId);
            terminalId.KeyType = "E";
            onRspEntrust(terminalId);
        } catch (Exception e) {
            logger.error("send entrust error:{},{}", entrust.getId(), e.getMessage(), e);
            throw new RuntimeException(ErrorCode.TERMINAL_API_ERROR.errorMsg);
        }
    }

    /**
     * 403
     * 596
     *
     * @param iEntrust
     */
    @Override
    public void integratedEntrust(IntegratedEntrust iEntrust) {
        logger.info("send IntegratedEntrust:{}", iEntrust.getId());
        // 场景代码
        int functionCode = FunctionCode.REQ_ENTRUST.value;
        // 场景属性
        // 默认 403
        String entrustProp = "Bidding";
        // 596 协议回购 上海 深圳
        if (IntegratedEntrustUtils.isProtocolBack_SZ(iEntrust.getEntrustProp())) {
            entrustProp = "ProtocolBack";
        }
        try {
            // region 参数组装
            // 自定义参数
            Map<String, Object> customParam = getCustomParam(iEntrust.getAccountId());
            // endregion
            ResultBean resultBean = triggerCodeAdapter(functionCode, entrustProp, iEntrust, customParam);

            iEntrust.setEntrustStatus(EntrustStatus.TOBE_REPORTED.value);
            if (resultBean.getErrorCode() == ErrorCode.SUCCESS.value) {
                List<Map<String, String>> resList = (List<Map<String, String>>) resultBean.getData();
                for (Map<String, String> resMap : resList) {
                    iEntrust.setBrokerEntrustId(resMap.get("ordersno"));
                    iEntrust.setReportNo(AgentUtil.GetMapValue(resMap, "orderid"));
                    // 缓存委托的ID
                    entrustIdManager.setKcbpwZHEntrustId(iEntrust.getAccountId(),
                            iEntrust.getBrokerEntrustId() + "|" + iEntrust.getReportNo(), iEntrust.getId());
                }
            } else {
                iEntrust.setErrorCode(resultBean.getErrorCode());
                iEntrust.setErrorMsg(resultBean.getErrorMsg());
            }

            TerminalId terminalId = new TerminalId();
            BeanUtils.copyProperties(iEntrust, terminalId);
            terminalId.KeyType = "I";
            onRspIntegratedEntrust(terminalId);
        } catch (Exception e) {
            logger.error("send integratedEntrust error:{},{}", iEntrust.getId(), e.getMessage(), e);
            throw new RuntimeException(ErrorCode.TERMINAL_API_ERROR.errorMsg);
        }
    }

    /**
     * 404
     *
     * @param entrust
     * @param cancelEntrust
     */
    @Override
    public void cancelEntrust(Entrust entrust, EntrustInterface cancelEntrust) {
        CancelEntrustBean cancelEntrustBean = getCancelEntrustBean(entrust, cancelEntrust);
        logger.info("send entrust:{}", entrust.getId());
        // 场景代码
        int functionCode = FunctionCode.REQ_ENTRUST.value;
        // 场景属性
        // 默认 404
        String entrustProp = "Bidding";
        try {
            // region 参数组装
            // 自定义参数
            Map<String, Object> customParam = getCustomParam(entrust.getAccountId());
            // endregion
            ResultBean resultBean = triggerCodeAdapter(functionCode, entrustProp, entrust, customParam);

            if (resultBean.getErrorCode() == ErrorCode.SUCCESS.value) {
                List<Map<String, String>> resList = (List<Map<String, String>>) resultBean.getData();
                for (Map<String, String> resMap : resList) {
                    cancelEntrustBean.setCancelBrokerEntrustId(resMap.get("ordersno"));
                    // 撤销的orderid 优先取 orderid。没有取cancelorderid
                    String orderid = AgentUtil.GetMapValue(resMap, "orderid");
                    if (Strings.isNullOrEmpty(orderid)) {
                        orderid = AgentUtil.GetMapValue(resMap, "cancelorderid");
                    }
                    // 缓存被撤销与撤销的委托更新
                    entrustIdManager.setKcbpwPTCancelEntrustId(entrust.getAccountId(), entrust.getBrokerEntrustId(),
                            resMap.get("ordersno"), orderid);
                    // 缓存撤销的ID
                    entrustIdManager.setKcbpwPTEntrustId(entrust.getAccountId(),
                            cancelEntrustBean.getCancelBrokerEntrustId() + "|" + entrust.getReportNo(), cancelEntrustBean.getCancelId());
                }
            } else {
                cancelEntrustBean.setErrorCode(resultBean.getErrorCode());
                cancelEntrustBean.setErrorMsg(resultBean.getErrorMsg());
            }
            onRspCancelEntrust(cancelEntrustBean);
        } catch (Exception e) {
            logger.error(" cancelEntrust error:{},{}", entrust.getId(), e.getMessage(), e);
            throw new RuntimeException(ErrorCode.TERMINAL_API_ERROR.errorMsg);
        }
    }

    @Override
    public void cancelIntegratedEntrust(IntegratedEntrust iEntrust, IntegratedEntrust cancelEntrust) {
        CancelEntrustBean cancelEntrustBean = getCancelEntrustBean(iEntrust, cancelEntrust);
        logger.info("send entrust:{}", iEntrust.getId());
        // 场景代码
        int functionCode = FunctionCode.REQ_ENTRUST.value;
        // 场景属性
        // 默认 404
        String entrustProp = "Bidding";
        try {
            // region 参数组装
            // 自定义参数
            Map<String, Object> customParam = getCustomParam(iEntrust.getAccountId());
            // endregion
            ResultBean resultBean = triggerCodeAdapter(functionCode, entrustProp, iEntrust, customParam);
            if (resultBean.getErrorCode() == ErrorCode.SUCCESS.value) {
                List<Map<String, String>> resList = (List<Map<String, String>>) resultBean.getData();
                for (Map<String, String> resMap : resList) {
                    cancelEntrustBean.setCancelBrokerEntrustId(resMap.get("ordersno"));
                    // 撤销的orderid 优先取 orderid。没有取cancelorderid
                    String orderid = AgentUtil.GetMapValue(resMap, "orderid");
                    if (Strings.isNullOrEmpty(orderid)) {
                        orderid = AgentUtil.GetMapValue(resMap, "cancelorderid");
                    }

                    // 缓存被撤销与撤销的委托更新
                    entrustIdManager.setKcbpwZHCancelEntrustId(iEntrust.getAccountId(), iEntrust.getBrokerEntrustId(),
                            resMap.get("ordersno"), orderid);
                    entrustIdManager.setKcbpwZHEntrustId(iEntrust.getAccountId(),
                            cancelEntrustBean.getCancelBrokerEntrustId() + "|" + orderid, cancelEntrustBean.getCancelId());
                }

            } else {
                cancelEntrustBean.setErrorCode(resultBean.getErrorCode());
                cancelEntrustBean.setErrorMsg(resultBean.getErrorMsg());
            }
            onRspCancelIntegratedEntrust(cancelEntrustBean);

        } catch (Exception e) {
            logger.error(" cancelIntegratedEntrust error:{},{}", iEntrust.getId(), e.getMessage(), e);
            throw new RuntimeException(ErrorCode.TERMINAL_API_ERROR.errorMsg);
        }
    }

    // endregion


    /**
     * 查询订单
     * 374	当日委托查询
     */
    @Override
    public List<Entrust> reqEntrusts(String accountId) throws Exception {
        KCBPUAccount kcbpuAccount = new KCBPUAccount();
        kcbpuAccount.setAccountId(accountId);
        return queryEntrust(kcbpuAccount);
    }

    @Override
    public List<IntegratedEntrust> reqIntegratedEntrusts(String accountId) throws Exception {
        //TODO 待实现
        return null;
    }

    /**
     * 查询成交
     */
    @Override
    public List<Trade> reqTrades(String accountId) throws Exception {
        KCBPUAccount kcbpuAccount = new KCBPUAccount();
        kcbpuAccount.setAccountId(accountId);

        List<Trade> res = new ArrayList<>();
        // 376	当日成交查询
        res.addAll(queryTrade(kcbpuAccount));
        // 505 当日委托查询
        // 506 当日成交查询
        res.addAll(queryITrade(kcbpuAccount));
        return res;
    }


    @Override
    public List<IntegratedQuote> reqIntegratedQuotes(QuoteQryBean quoteQryBean) throws Exception {
        return null;
    }

    @Override
    public int reqEnable(EnableQryBean qryBean) throws Exception {
        return 0;
    }

    @Override
    public List<HistoryTrade> reqHistoryTrade(HistoryQryBean historyQryBean) throws Exception {
        return null;
    }

    @Override
    public List<AccountFinance> reqCustomerFinance(String accountId) throws Exception {
        return null;
    }

    @Override
    public List<PositionBean> reqAccountPosition(PositionQueryBean positionQueryBean) throws Exception {
        return null;
    }

    @Override
    public List<Instrument> reqInstrumentInfo(InstrumentQryBean instrumentQryBean) throws Exception {
        return null;
    }

    @Override
    public List<PledgeDiscount> reqPledgeDiscount(String accountId, String market, String stockCode) throws Exception {
        return null;
    }

    @Override
    public List<StandardBonds> reqStandardBonds(StandardBondQryBean standardBondQryBean) throws Exception {
        return null;
    }

    @Override
    public List<IntegratedBulkQuoteBean> reqIntegratedBulkQuote(IntegratedBulkQuoteQryBean integratedBulkQuoteQryBean) throws Exception {
        return null;
    }

    @Override
    public List<IntegratedBulkCloseQuoteBean> reqIntegratedBulkCloseQuote(IntegratedBulkCloseQuoteQryBean integratedBulkCloseQuoteQryBean) throws Exception {
        return null;
    }

    @Override
    public List<IntegratedRepurchaseQuoteBean> reqIntegratedRepurchaseQuote(IntegratedRepurchaseQuoteQryBean integratedRepurchaseQuoteQryBean) throws Exception {
        return null;
    }

    @Override
    public List<RepurchaseQuoteReceiveResponse> reqIntegratedRepurchaseQuoteReceive(RepurchaseQuoteReceiveQryRequest bean) throws Exception {
        return null;
    }

    @Override
    public List<IntegratedFixedIncomeQuoteBean> reqIntegratedFixedIncomeQuote(IntegratedFixedIncomeQuoteQryBean integratedFixedIncomeQuoteQryBean) throws Exception {
        return null;
    }

    /**
     * 1733 协商成交转发行情查询  深圳
     *
     * @param negotiateQuoteQueryBean
     * @return
     * @throws Exception
     */
    @Override
    public List<NegotiateQuoteReceiveBean> reqNegotiateQuoteReceive(NegotiateQuoteQueryBean negotiateQuoteQueryBean) throws Exception {
        // 场景代码
        int functionCode = FunctionCode.REQ_QRY_INTEGRATED_NEGOTIATE_QUOTE_RECEIVE.value;
        // 场景属性
        // 410682 转发成交申报查询  深圳现券协商成交转发成交申报查询
        String entrustProp = "BondConsultQuerySZ";

        // region 参数组装
        // 自定义参数
        Map<String, Object> customParam = getCustomParam(negotiateQuoteQueryBean.getAccountId());
        customParam.put("positionStr", "");
        // endregion

        ResultBean resultBean = triggerCodeAdapter(functionCode, entrustProp, negotiateQuoteQueryBean, customParam);
        // region 返回结果处理
        List<NegotiateQuoteReceiveBean> list = KCBPUMsgParseHelper.parseNegotiateQuoteReceive(AgentUtil.getQueryListKCBPW(resultBean));
        // endregion

        return list;
    }

    // region 委托、成交查询结果处理

    public void queryIncrementEntrust(KCBPUAccount kcbpuAccount) {
        try {
            List<Entrust> entrustList = queryEntrust(kcbpuAccount);
            if (CollectionUtils.isEmpty(entrustList)) {
                return;
            }
            entrustList.forEach(entrust -> {
                // 可执行标识
                Boolean flag = false;

                //可能不存在id，用前缀区分普通和综合
                String key = entrustPrefix + "_" + entrust.getId() + "_" + entrust.getBrokerEntrustId() + "_" + entrust.getAccountId();
                Integer entrustStatus = entrustIdAndStatusMap.get(key);
                if (Objects.isNull(entrustStatus) || entrustStatus != entrust.getEntrustStatus()) {
                    flag = true;
                } else {
                    if (EntrustStatus.PART_SUCCESS.value == entrust.getEntrustStatus()) {
                        String ordersNoAndorderId = entrustIdManager.getKcbpwPTCancelEntrustId(kcbpuAccount.getAccountId(), entrust.getBrokerEntrustId());
                        if (!Strings.isNullOrEmpty(ordersNoAndorderId)) {
                            entrust.setErrorMsg("柜台部撤失败");
                            flag = true;
                        }
                    }
                }

                if (flag) {
                    logger.info("委托[{}]新增或状态发生变更，推送委托。oldEntrustStatus [{}]  newEntrustStatus [{}]", key, entrustStatus, entrust.getEntrustStatus());
                    logger.info("onEntrust {}", JsonUtils.getJsonFromObject(entrust));
                    onRtnEntrust(entrust);
                    entrustIdAndStatusMap.put(key, entrust.getEntrustStatus());
                }
            });
        } catch (Exception e) {
            logger.error("queryIncrementEntrust: {} error:{}", kcbpuAccount.getAccountId(), e);
        }
    }

    public void queryFixedIncomeEntrust(KCBPUAccount kcbpuAccount) {
        try {
            List<IntegratedEntrust> integratedEntrusts = queryIEntrust(kcbpuAccount);
            integratedEntrusts.forEach(this::onRtnIntegratedEntrust);
        } catch (Exception e) {
            logger.error("queryFixedIncomeEntrust: {} error:{}", kcbpuAccount.getAccountId(), e);
        }
    }

    public void queryProtocolBackEntrust(KCBPUAccount kcbpuAccount) {
        try {
            List<IntegratedEntrust> integratedEntrusts = null;// queryProtocolBack_SH_Entrust0(kcbpuAccount);
            integratedEntrusts.forEach(this::onRtnIntegratedEntrust);
        } catch (Exception e) {
            logger.error("queryProtocolBack_SH_Entrust: {} error:{}", kcbpuAccount.getAccountId(), e);
        }
    }

    public void queryIncrementTrade(KCBPUAccount kcbpuAccount) {
        try {
            PositionStr positionStr = positionStrMap.get(kcbpuAccount.getAccountId());
            List<Trade> trades = queryTrade(kcbpuAccount);
            if (CollectionUtils.isEmpty(trades)) {
                return;
            }
            String position_str = trades.get(trades.size() - 1).getPositionStr();
            if (Objects.isNull(positionStr)) {
                positionStr = new PositionStr();
            }
            positionStr.setTradePositionStr(position_str);
            positionStrMap.put(kcbpuAccount.getAccountId(), positionStr);
            Set<String> businessIdSet = positionStr.getBusinessIdSet();
            trades.forEach(trade -> {
                if (!Objects.isNull(businessIdSet) && businessIdSet.contains(trade.getBrokerTradeId())) {
                    logger.info("qryTrade 委托已发送，不重复发送:{}", trade.getBrokerTradeId());
                    return;
                }
                onTrade(trade, kcbpuAccount, true);
            });
        } catch (Exception e) {
            logger.error("queryIncrementTrade: {} error:{}", kcbpuAccount.getAccountId(), e);
        }
    }

    public void queryFixedIncomeTrade(KCBPUAccount kcbpuAccount) {
        try {
            List<Trade> trades = queryITrade(kcbpuAccount);
            trades.forEach(trade -> onTrade(trade, kcbpuAccount, false));
        } catch (Exception e) {
            logger.error("queryFixedIncomeTrade: {} error:{}", kcbpuAccount.getAccountId(), e);
        }
    }

    public void queryProtocolBackTrade(KCBPUAccount kcbpuAccount) {
        try {
            List<Trade> trades = null;// queryProtocolBack_SH_Trade0(kcbpuAccount);
            trades.forEach(trade -> onTrade(trade, kcbpuAccount, false));
        } catch (Exception e) {
            logger.error("queryProtocolBack_SH_Trade: {} error:{}", kcbpuAccount.getAccountId(), e);
        }
    }

    /**
     * 成交处理
     *
     * @param trade
     * @param kcbpuAccount
     * @param flag         true 普通委托， false 综合委托
     */
    private void onTrade(Trade trade, KCBPUAccount kcbpuAccount, Boolean flag) {
        // 废单成交将成交转换为委托推送 -- 返回废单原因
        if (TradeType.isInvalid(trade.getTradeType())) {
            if (flag) {
                // 410411 410413 410510 410436竞价与综合共用此接口.
                Entrust entrust = new Entrust();
                // 获取取消的ordersNo、orderId
                String ordersNoAndorderId = entrustIdManager.getKcbpwPTCancelEntrustId(kcbpuAccount.getAccountId(), trade.getBrokerEntrustId());
                if (!Strings.isNullOrEmpty(ordersNoAndorderId)) {
                    String[] OAO = ordersNoAndorderId.split("\\|");
                    // 修改成撤销的委托编号，同时要把下单的委托状态从部成待撤还原成部成。部成的记录错误信息撤单失败
                    trade.setBrokerEntrustId(OAO[0]);
                    // entrustIdManager.getKcbpwPTEntrustId(trade.getAccountId(), "111|0500000011")
                    // 获取取消的ordersNo、orderId
                    trade.setEntrustId(entrustIdManager.getKcbpwPTEntrustId(trade.getAccountId(), trade.getBrokerEntrustId() + "|" + trade.getReportNo()));
                    trade.setReportNo(OAO[1]);
                }

                BeanUtils.copyProperties(trade, entrust);
                entrust.setId(trade.getEntrustId());
                entrust.setEntrustStatus(EntrustStatus.INVALID.value);
                logger.info("废单成交转换为委托通知  trade：[{}]  entrust: [{}]", JsonUtils.getJsonFromObject(trade), JsonUtils.getJsonFromObject(entrust));
                onRtnEntrust(entrust);
            } else {
                IntegratedEntrust entrust = new IntegratedEntrust();
                BeanUtils.copyProperties(trade, entrust);
                entrust.setId(trade.getEntrustId());
                entrust.setEntrustStatus(EntrustStatus.INVALID.value);
                logger.info("废单成交转换为委托通知  trade：[{}]  IntegratedEntrust: [{}]", JsonUtils.getJsonFromObject(trade), JsonUtils.getJsonFromObject(entrust));
                onRtnIntegratedEntrust(entrust);
            }
        } else {
            if (trade.getTradeType() == 1) {
                String ordersNoAndorderId = null;
                // 撤单成功
                if (flag) {
                    // 410411 410413 410510 410436竞价与综合共用此接口.
                    ordersNoAndorderId = entrustIdManager.getKcbpwPTCancelEntrustId(kcbpuAccount.getAccountId(), trade.getBrokerEntrustId());
                    if (Strings.isNullOrEmpty(ordersNoAndorderId)) {
                        ordersNoAndorderId = entrustIdManager.getKcbpwZHCancelEntrustId(kcbpuAccount.getAccountId(), trade.getBrokerEntrustId());
                    }
                } else {
                    ordersNoAndorderId = entrustIdManager.getKcbpwZHCancelEntrustId(kcbpuAccount.getAccountId(), trade.getBrokerEntrustId());
                }

                if (!Strings.isNullOrEmpty(ordersNoAndorderId)) {
                    String[] OAO = ordersNoAndorderId.split("\\|");
                    trade.setBrokerEntrustId(OAO[0]);
                    trade.setReportNo(OAO[1]);
                }
            }
            if (Strings.isNullOrEmpty(trade.getBrokerTradeId())) {
                trade.setBrokerTradeId(trade.getEntrustId());
            }
            if (flag) {
                trade.KeyType = "E";
            } else {
                trade.KeyType = "I";
            }
            onRtnTrade(trade);
        }
    }
    // endregion

    // region 接口访问

    /**
     * 374 当日委托查询
     */
    private List<Entrust> queryEntrust(KCBPUAccount kcbpuAccount) {
        // 场景代码
        int functionCode = FunctionCode.REQ_QRY_ENTRUST.value;
        // 场景属性
        String entrustProp = "Bidding";

        // region 参数组装
        // 自定义参数
        Map<String, Object> customParam = getCustomParam(kcbpuAccount.getAccountId());
        customParam.put("positionStr", "");
        // endregion
        ResultBean resultBean = triggerCodeAdapter(functionCode, entrustProp, kcbpuAccount, customParam);
        if (resultBean.getErrorCode() == ErrorCode.SUCCESS.value) {
            List<Map<String, String>> resList = AgentUtil.getQueryListKCBPW(resultBean);
            return KCBPUMsgParseHelper.parseEntrusts(resList, kcbpuAccount.getAccountId(), entrustIdManager, "374");
        }
        return new ArrayList<>();
    }

    /**
     * 374 当日委托查询
     */
    private List<IntegratedEntrust> queryIEntrust(KCBPUAccount kcbpuAccount) {
        // 场景代码
        int functionCode = FunctionCode.REQ_QRY_ENTRUST.value;
        // 场景属性
        String entrustProp = "Bidding";

        // region 参数组装
        // 自定义参数
        Map<String, Object> customParam = getCustomParam(kcbpuAccount.getAccountId());
        customParam.put("positionStr", "");
        // endregion
        ResultBean resultBean = triggerCodeAdapter(functionCode, entrustProp, kcbpuAccount, customParam);
        if (resultBean.getErrorCode() == ErrorCode.SUCCESS.value) {
            List<Map<String, String>> resList = AgentUtil.getQueryListKCBPW(resultBean);
            return KCBPUMsgParseHelper.parseIEntrusts(resList, kcbpuAccount.getAccountId(), entrustIdManager, "374");
        }
        return new ArrayList<>();
    }


    /**
     * 376 当日委托查询
     */
    private List<Trade> queryTrade(KCBPUAccount kcbpuAccount) {
        // 场景代码
        int functionCode = FunctionCode.REQ_QRY_TRADE.value;
        // 场景属性
        String entrustProp = "Bidding";

        // region 参数组装
        // 自定义参数
        Map<String, Object> customParam = getCustomParam(kcbpuAccount.getAccountId());
        customParam.put("positionStr", "");
        // endregion
        ResultBean resultBean = triggerCodeAdapter(functionCode, entrustProp, kcbpuAccount, customParam);
        if (resultBean.getErrorCode() == ErrorCode.SUCCESS.value) {
            List<Map<String, String>> resList = AgentUtil.getQueryListKCBPW(resultBean);
            return KCBPUMsgParseHelper.parseTrades(resList, kcbpuAccount.getAccountId(), entrustIdManager, "374");
        }
        return new ArrayList<>();
    }

    /**
     * 376 当日委托查询
     */
    private List<Trade> queryITrade(KCBPUAccount kcbpuAccount) {
        // 场景代码
        int functionCode = FunctionCode.REQ_QRY_TRADE.value;
        // 场景属性
        String entrustProp = "Bidding";

        // region 参数组装
        // 自定义参数
        Map<String, Object> customParam = getCustomParam(kcbpuAccount.getAccountId());
        customParam.put("positionStr", "");
        // endregion
        ResultBean resultBean = triggerCodeAdapter(functionCode, entrustProp, kcbpuAccount, customParam);
        if (resultBean.getErrorCode() == ErrorCode.SUCCESS.value) {
            List<Map<String, String>> resList = AgentUtil.getQueryListKCBPW(resultBean);
            return KCBPUMsgParseHelper.parseTrades(resList, kcbpuAccount.getAccountId(), entrustIdManager, "374");
        }
        return new ArrayList<>();
    }


    // endregion

    // region 定时查询任务

    @Override
    public void queryNextTrade() {
        if (MapUtils.isEmpty(kcbpuAccounts)) {
            return;
        }
        for (Map.Entry<String, KCBPUAccount> entry : kcbpuAccounts.entrySet()) {
            KCBPUAccount kcbpuAccount = entry.getValue();
            String accountId = kcbpuAccount.getAccountId();
            logger.info("account：{}，status：{}", accountId, getStatus(accountId));
//            if (getStatus(accountId) != AccountLoginStatus.TRADING.value) {
//                return;
//            }
            boolean loginStatus = tradeAgentManager.getAgentLoginStatus(accountId);
            if (loginStatus) {
                executeQueryNextTrade(kcbpuAccount);
            }
        }
    }

    private void executeQueryNextTrade(KCBPUAccount kcbpuAccount) {
        if (null == poolExecutor) {
            poolExecutor = new ThreadPoolExecutor(8, 20, 1000, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>(100), new ThreadPoolExecutor.DiscardPolicy());
            poolExecutor.prestartAllCoreThreads();
        }

        for (KCBPUTradeAgent.QueryEnum queryEnum : KCBPUTradeAgent.QueryEnum.getValues()) {
            try {
                poolExecutor.execute(new KCBPUTradeAgent.QueryTask(queryEnum, kcbpuAccount));
                Thread.sleep(500);
            } catch (Exception e) {
                logger.error("{}执行异常：{}", queryEnum.name(), e.getMessage(), e);
            }
        }
    }

    class QueryTask implements Runnable {
        private QueryEnum queryEnum;
        private KCBPUAccount kcbpuAccount;

        public QueryTask(QueryEnum queryEnum, KCBPUAccount kcbpuAccount) {
            this.queryEnum = queryEnum;
            this.kcbpuAccount = kcbpuAccount;
        }

        @Override
        public void run() {
            System.out.println(this.toString() + " is running!");
            try {
                switch (queryEnum) {
                    case queryIncrementEntrust:
                        queryIncrementEntrust(kcbpuAccount);
                        return;
                    case queryFixedIncomeEntrust:
                        queryFixedIncomeEntrust(kcbpuAccount);
                        return;
                    case queryProtocolBackEntrust:
                        queryProtocolBackEntrust(kcbpuAccount);
                        return;
                    case queryIncrementTrade:
                        queryIncrementTrade(kcbpuAccount);
                        return;
                    case queryFixedIncomeTrade:
                        queryFixedIncomeTrade(kcbpuAccount);
                        return;
                    case queryProtocolBackTrade:
                        queryProtocolBackTrade(kcbpuAccount);
                        return;
                    default:
                }
            } catch (Exception e) {
                logger.error("{}执行异常：{}", getName(), e.getMessage(), e);
            }
        }

        public String getName() {
            return queryEnum.name();
        }

        @Override
        public String toString() {
            return "QueryTask [name=" + getName() + "]";
        }
    }

    enum QueryEnum {
        queryIncrementEntrust(1),
        queryFixedIncomeEntrust(2),
        queryProtocolBackEntrust(3),
        // queryProtocolBack_SZ_Entrust(4),
        queryIncrementTrade(5),
        queryFixedIncomeTrade(6),
        queryProtocolBackTrade(7),
        //queryProtocolBack_SH_Trade(8),
        //queryBondNegotiate_SZ_Entrust(9),
        //queryBondNegotiate_SZ_Trade(10),
        ;

        private Integer code;

        QueryEnum(Integer code) {
            this.code = code;
        }

        public Integer getCode() {
            return code;
        }

        public static List<KCBPUTradeAgent.QueryEnum> getValues() {
            return Lists.newArrayList(KCBPUTradeAgent.QueryEnum.values()).stream()
                    .sorted(Comparator.comparing(KCBPUTradeAgent.QueryEnum::getCode)).collect(Collectors.toList());
        }
    }

    // endregion
}
