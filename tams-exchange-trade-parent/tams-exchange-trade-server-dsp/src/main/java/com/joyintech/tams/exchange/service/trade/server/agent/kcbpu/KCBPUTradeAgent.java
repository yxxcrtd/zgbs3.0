package com.joyintech.tams.exchange.service.trade.server.agent.kcbpu;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.joyintech.tams.exchange.common.bean.*;
import com.joyintech.tams.exchange.common.bean.bond.StandardBondQryBean;
import com.joyintech.tams.exchange.common.bean.request.RepurchaseQuoteReceiveQryRequest;
import com.joyintech.tams.exchange.common.bean.response.quote.repurchase.RepurchaseQuoteReceiveResponse;
import com.joyintech.tams.exchange.common.constant.AccountLoginStatus;
import com.joyintech.tams.exchange.common.constant.AgentType;
import com.joyintech.tams.exchange.common.constant.EntrustStatus;
import com.joyintech.tams.exchange.common.constant.ErrorCode;
import com.joyintech.tams.exchange.common.db.domain.*;
import com.joyintech.tams.exchange.common.net.protocol.FunctionCode;
import com.joyintech.tams.exchange.common.utils.IntegratedEntrustUtils;
import com.joyintech.tams.exchange.common.utils.JsonUtils;
import com.joyintech.tams.exchange.service.trade.dao.domain.TerminalAgentInfo;
import com.joyintech.tams.exchange.service.trade.server.agent.TradeAgentDsp;
import com.joyintech.tams.exchange.service.trade.server.agent.kcbpw.KCBPWMsgParseHelper;
import com.joyintech.tams.exchange.service.trade.util.AgentUtil;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component("kcbpu")
public class KCBPUTradeAgent extends TradeAgentDsp {

    private static final Logger logger = LoggerFactory.getLogger(KCBPUTradeAgent.class);

    /**
     * 记录已经登录的账号
     */
    private Map<String, KCBPUAccount> kcbpuAccounts = Maps.newConcurrentMap();

    public KCBPUAccount getKCBPAccount(String accountId) {
        return kcbpuAccounts.get(accountId);
    }

    public void addKCBPAccount(KCBPUAccount kcbpAccount) {
        kcbpuAccounts.put(kcbpAccount.getAccountId(), kcbpAccount);
    }

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
            // 账户交易密码解密
            account.setPwd(new String(Base64.decodeBase64(account.getPwd())));
            account.setTradePwd(new String(Base64.decodeBase64(account.getTradePwd())));

            Map<String, Object> customParam = new ConcurrentHashMap<>();
            customParam.put("procName", "");
            customParam.put("terminalinfo", "");

            ResultBean resultBean = triggerCodeAdapter(functionCode, entrustProp, accountLoginReqBean, customParam);

            //账户登录成功，记录登录成功的账户
            if (resultBean.getErrorCode() == ErrorCode.SUCCESS.value) {
                String data = JsonUtils.getJsonFromObject(resultBean.getData());
                logger.info("金证(U版)用户登录信息：{}", data);
                KCBPUAccount kcbpAccount = JsonUtils.getObjectFromJson(data, KCBPUAccount.class);
                kcbpAccount.setAgentType(AgentType.KINGDOM_KCBPU.value);
                resultBean.setData(kcbpAccount);

                //保存登录信息
                addKCBPAccount(kcbpAccount);
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


    /**
     * 查询订单
     * 374	当日委托查询
     */
    @Override
    public List<Entrust> reqEntrusts(String accountId) throws Exception {

        // 场景代码
        int functionCode = FunctionCode.REQ_QRY_INTEGRATED_NEGOTIATE_QUOTE_RECEIVE.value;
        // 场景属性
        // 410682 转发成交申报查询  深圳现券协商成交转发成交申报查询
        String entrustProp = "BondConsultQuerySZ";

        Entrust entrust = new Entrust();
        // 自定义参数
        Map<String, Object> customParam = getCustomParam(entrust.getAccountId());

        ResultBean resultBean = triggerCodeAdapter(functionCode, entrustProp, accountId, customParam);

        return KCBPUMsgParseHelper.parseEntrusts(AgentUtil.getQueryListKCBPW(resultBean), accountId, entrustIdManager, "374");
    }

    @Override
    public List<IntegratedEntrust> reqIntegratedEntrusts(String accountId) throws Exception {
        return null;
    }


    /**
     * 查询成交
     * 376	当日成交明细查询
     * 377	历史成交明细查询
     * 1733	协商成交转发行情查询
     * 1734	点击成交转发转发行情查询
     */
    @Override
    public List<Trade> reqTrades(String accountId) throws Exception {
        return null;
    }

    /**
     * 376 当日成交明细查询
     *
     * @param accountId
     * @return
     */
    private List<Trade> getTrades(String accountId) {

        // 场景代码
        int functionCode = FunctionCode.REQ_QRY_INTEGRATED_NEGOTIATE_QUOTE_RECEIVE.value;
        // 场景属性
        // 410682 转发成交申报查询  深圳现券协商成交转发成交申报查询
        String entrustProp = "BondConsultQuerySZ";
        Trade trade = new Trade();
        // 自定义参数
        Map<String, Object> customParam = getCustomParam(trade.getAccountId());

        try {
            ResultBean resultBean = triggerCodeAdapter(functionCode, entrustProp, accountId, customParam);
            if (resultBean.getErrorCode() == ErrorCode.SUCCESS.value) {
                List<Map<String, String>> resList = (List<Map<String, String>>) resultBean.getData();
                return KCBPUMsgParseHelper.parseTrades(resList, accountId, entrustIdManager, "376");
            }
        } catch (Exception e) {
            logger.error("查询委托成交异常：functionCode:{}", functionCode, e);
        }
        return Collections.emptyList();

    }

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


    @Override
    public void reqLogoutAccount(Account account) {

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

    @Override
    public List<NegotiateQuoteReceiveBean> reqNegotiateQuoteReceive(NegotiateQuoteQueryBean negotiateQuoteQueryBean) throws Exception {
        return null;
    }
}
