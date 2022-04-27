package com.joyintech.tams.exchange.service.trade.server.agent.kcbp;

import cn.hutool.core.date.DateUtil;
import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.joyintech.tams.exchange.common.bean.*;
import com.joyintech.tams.exchange.common.bean.bond.StandardBondQryBean;
import com.joyintech.tams.exchange.common.bean.terminal.RTGSStatusBean;
import com.joyintech.tams.exchange.common.constant.*;
import com.joyintech.tams.exchange.common.db.domain.*;
import com.joyintech.tams.exchange.common.net.protocol.FunctionCode;
import com.joyintech.tams.exchange.common.utils.EntrustUtils;
import com.joyintech.tams.exchange.common.utils.IntegratedEntrustUtils;
import com.joyintech.tams.exchange.common.utils.JsonUtils;
import com.joyintech.tams.exchange.common.utils.StockCodeUtils;
import com.joyintech.tams.exchange.service.trade.dao.domain.TerminalAgentInfo;
import com.joyintech.tams.exchange.service.trade.server.agent.TradeAgentDsp;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author lifangpeng
 * @date 2021/10/11
 */
@Component("kcbp")
public class KCBPTradeAgent extends TradeAgentDsp {
    private static final Logger logger = LoggerFactory.getLogger(KCBPTradeAgent.class);

    @Value("${agent.query.count:2}")
    private int kcbpTradeCount;

    /**
     * 记录已经登录的账号
     */
    private Map<String, KCBPAccount> kcbpAccounts = Maps.newConcurrentMap();

    /**
     * 查询线程
     */
    private List<ThreadPoolExecutor> poolExecutors = Lists.newArrayListWithCapacity(kcbpTradeCount);
    private ThreadPoolExecutor poolExecutor = null;

    public KCBPAccount getKCBPAccount(String accountId) {
        return kcbpAccounts.get(accountId);
    }

    public void addKCBPAccount(KCBPAccount kcbpAccount) {
        kcbpAccounts.put(kcbpAccount.getAccountId(), kcbpAccount);
    }

    @PostConstruct
    public void init() {
        setTerminalName(AgentType.KINGDOM_KCBP.value);
    }

    @Override
    public void removeAccount(String accountId) {
        accountMap.remove(accountId);
        kcbpAccounts.remove(accountId);
    }

    /**
     * 同步新建终端连接
     * 410301  客户登录	登录资金账号
     */
    @Override
    public ResultBean reqLoginAccount(AccountLoginReqBean accountLoginReqBean) {
        try {
            Account account = accountLoginReqBean.getAccount();
            // 账户交易密码解密
            account.setPwd(new String(Base64.decodeBase64(account.getPwd())));
            account.setTradePwd(new String(Base64.decodeBase64(account.getTradePwd())));
            String accountId = account.getId();
            TerminalAgentInfo agentInfo = terminalAgentInfoService.selectOne(getTerminalName(), FunctionCode.REQ_ACCOUNT_LOGIN.value);
            agentInfo.setAccountId(accountId);
            Map<String, String> parameters = Maps.newHashMap();
            parameters.put("accountId", accountId);
            parameters.put("financeAccount", account.getFinanceAccount());
            parameters.put("tradePwd", account.getTradePwd());

            ResultBean resultBean = tradeAgentHelper.doRequest(agentInfo, parameters);
            // 账户登录成功，记录下登录成功的账户
            if (resultBean.getErrorCode() == ErrorCode.SUCCESS.value) {
                String data = JsonUtils.getJsonFromObject(resultBean.getData());
                logger.info("金证(W版)用户登录信息：{}", data);
                KCBPAccount kcbpAccount = JsonUtils.getObjectFromJson(data, KCBPAccount.class);
                kcbpAccount.setAgentType(AgentType.KINGDOM_KCBP.value);
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

    @Override
    public void reqLogoutAccount(Account account) {

    }

    /**
     * 查询订单
     * 410510	当日委托明细查询	用于竞价平台所有当日委托业务查询
     */
    @Override
    public List<Entrust> reqEntrusts(String accountId) throws Exception {
        TerminalAgentInfo agentInfo = terminalAgentInfoService.selectOne(getTerminalName(), FunctionCode.REQ_QRY_ENTRUST.value);
        agentInfo.setAccountId(accountId);

        Map<String, String> parameters = Maps.newHashMap();
        parameters.put("accountId", accountId);
        parameters.put("market", "");
        parameters.put("secuid", "");
        parameters.put("stkcode", "");
        parameters.put("bankcode", "");
        parameters.put("ordersno", "");
        parameters.put("count", "100");
        parameters.put("qryflag", "1");
        parameters.put("poststr", "");

        ResultBean resultBean = tradeAgentHelper.doRequest(agentInfo, parameters);
        if (resultBean.getErrorCode() != ErrorCode.SUCCESS.value) {
            return Lists.newArrayList();
        }

        return KCBPMsgParseHelper.parseEntrusts(resultBean, accountId, entrustIdManager);
    }

    @Override
    public List<IntegratedEntrust> reqIntegratedEntrusts(String accountId) throws Exception {
        //TODO 待实现
        return null;
    }

    /**
     * 查询成交
     * 410436	当日成交明细查询	用于竞价平台所有当日成交信息查询
     * 410436	当日成交明细查询	用于深交所综合协议平台成交查询，上交所大宗交易成交查询
     * 460003	固收平台交易成交查询	上交所固收平台现券交易成交信息查询
     * 411003	债券协议回购成交查询	深交所质押式协议回购成交信息查询
     * 411007	上海债券协议回购成交查询	上交所质押式协议回购成交信息查询
     */
    @Override
    public List<Trade> reqTrades(String accountId) throws Exception {
        List<Trade> res = new ArrayList<>();
        res.addAll(getTrades(accountId));
        res.addAll(getFixedIncomeTrades(accountId));
        res.addAll(getProtocolBack_SZ_Trades(accountId));
        res.addAll(getProtocolBack_SH_Trades(accountId));
        return res;
    }

    /**
     * 410436	当日成交明细查询	用于竞价平台所有当日成交信息查询
     * 410436	当日成交明细查询	用于深交所综合协议平台成交查询，上交所大宗交易成交查询
     */
    private List<Trade> getTrades(String accountId) {
        Map<String, String> parameters = Maps.newHashMap();
        parameters.put("accountId", accountId);
        parameters.put("market", "");
        parameters.put("secuid", "");
        parameters.put("stkcode", "");
        parameters.put("bankcode", "");
        parameters.put("ordersno", "");
        parameters.put("count", "100");
        parameters.put("qryflag", "1");
        parameters.put("poststr", "");

        TerminalAgentInfo agentInfo = terminalAgentInfoService.selectOne(getTerminalName(), FunctionCode.REQ_QRY_TRADE.value, "Bidding");
        agentInfo.setAccountId(accountId);
        try {
            ResultBean resultBean = tradeAgentHelper.doRequest(agentInfo, parameters);
            if (resultBean.getErrorCode() == ErrorCode.SUCCESS.value) {
                List<Map<String, String>> resList = (List<Map<String, String>>) resultBean.getData();
                return KCBPMsgParseHelper.parseTrades(resList, accountId, entrustIdManager);
            }
        } catch (Exception e) {
            logger.error("查询委托成交异常：gpCod={},triggerCode={}", agentInfo.getGpCode(), agentInfo.getTriggerCode(), e);
        }
        return Collections.emptyList();
    }

    /**
     * 460003	固收平台交易成交查询	上交所固收平台现券交易成交信息查询
     */
    private List<Trade> getFixedIncomeTrades(String accountId) {
        Map<String, String> parameters = Maps.newHashMap();
        parameters.put("accountId", accountId);
        parameters.put("market", "");
        parameters.put("secuid", "");
        parameters.put("stkcode", "");
        parameters.put("ordersno", "");
        parameters.put("count", "100");
        parameters.put("qryflag", "1");
        parameters.put("poststr", "");

        TerminalAgentInfo agentInfo = terminalAgentInfoService.selectOne(getTerminalName(), FunctionCode.REQ_QRY_TRADE.value, "FixedIncome");
        agentInfo.setAccountId(accountId);
        try {
            ResultBean resultBean = tradeAgentHelper.doRequest(agentInfo, parameters);
            if (resultBean.getErrorCode() == ErrorCode.SUCCESS.value) {
                List<Map<String, String>> resList = (List<Map<String, String>>) resultBean.getData();
                return KCBPMsgParseHelper.parseTrades(resList, accountId, entrustIdManager);
            }
        } catch (Exception e) {
            logger.error("查询委托成交异常：gpCod={},triggerCode={}", agentInfo.getGpCode(), agentInfo.getTriggerCode(), e);
        }
        return Collections.emptyList();
    }

    /**
     * 411003	债券协议回购成交查询	深交所质押式协议回购成交信息查询
     */
    private List<Trade> getProtocolBack_SZ_Trades(String accountId) {
        String beginDate = DateUtil.format(LocalDateTime.now(), "yyyyMMdd");
        Map<String, String> parameters = Maps.newHashMap();
        parameters.put("accountId", accountId);
        parameters.put("market", "0");
        parameters.put("secuid", "");
        parameters.put("ordersno", "");
        parameters.put("count", "100");
        parameters.put("posstr", "");
        parameters.put("begindate", beginDate);
        parameters.put("enddate", beginDate);

        TerminalAgentInfo agentInfo = terminalAgentInfoService.selectOne(getTerminalName(), FunctionCode.REQ_QRY_TRADE.value, "ProtocolBack_SZ");
        agentInfo.setAccountId(accountId);
        try {
            ResultBean resultBean = tradeAgentHelper.doRequest(agentInfo, parameters);
            if (resultBean.getErrorCode() == ErrorCode.SUCCESS.value) {
                List<Map<String, String>> resList = (List<Map<String, String>>) resultBean.getData();
                return KCBPMsgParseHelper.parseTrades(resList, accountId, entrustIdManager);
            }
        } catch (Exception e) {
            logger.error("查询委托成交异常：gpCod={},triggerCode={}", agentInfo.getGpCode(), agentInfo.getTriggerCode(), e);
        }
        return Collections.emptyList();
    }

    /**
     * 411007	上海债券协议回购成交查询	上交所质押式协议回购成交信息查询
     */
    private List<Trade> getProtocolBack_SH_Trades(String accountId) {
        String beginDate = DateUtil.format(LocalDateTime.now(), "yyyyMMdd");
        Map<String, String> parameters = Maps.newHashMap();
        parameters.put("accountId", accountId);
        parameters.put("market", "1");
        parameters.put("fundid", "");
        parameters.put("secuid", "");
        parameters.put("stkcode", "");
        parameters.put("ordersno", "");
        parameters.put("count", "100");
        parameters.put("posstr", "");
        parameters.put("begindate", beginDate);
        parameters.put("enddate", beginDate);

        TerminalAgentInfo agentInfo = terminalAgentInfoService.selectOne(getTerminalName(), FunctionCode.REQ_QRY_TRADE.value, "ProtocolBack_SH");
        agentInfo.setAccountId(accountId);
        try {
            ResultBean resultBean = tradeAgentHelper.doRequest(agentInfo, parameters);
            if (resultBean.getErrorCode() == ErrorCode.SUCCESS.value) {
                List<Map<String, String>> resList = (List<Map<String, String>>) resultBean.getData();
                return KCBPMsgParseHelper.parseTrades(resList, accountId, entrustIdManager);
            }
        } catch (Exception e) {
            logger.error("查询委托成交异常：gpCod={},triggerCode={}", agentInfo.getGpCode(), agentInfo.getTriggerCode(), e);
        }
        return Collections.emptyList();
    }


    /**
     * 委托下单
     * 410411 用于竞价平台所有委托业务下单
     */
    @Override
    public void entrust(Entrust entrust) {
        logger.info("send entrust:{}", entrust.getId());
        entrust.setLocalEntrustId(genLocalId());
        entrustIdManager.setLocalEntrustIdMapping(entrust.getAccountId(), entrust.getLocalEntrustId(), entrust.getId());
        TerminalAgentInfo agentInfo = terminalAgentInfoService.selectOne(getTerminalName(), FunctionCode.REQ_ENTRUST.value);
        agentInfo.setAccountId(entrust.getAccountId());
        try {
            Map<String, String> parameters = Maps.newHashMap();
            parameters.put("accountId", entrust.getAccountId());
            parameters.put("entrustProp", entrust.getEntrustProp());
            parameters.put("entrustBs", entrust.getEntrustBs());
            parameters.put("market", getAgentMarket(entrust.getMarket()));
            parameters.put("stkcode", StockCodeUtils.getStockCode(entrust.getStockCode()));
            parameters.put("bsflag", entrust.getBsFlag());
            parameters.put("price", String.valueOf(entrust.getEntrustPrice()));
            parameters.put("qty", String.valueOf(entrust.getEntrustVolume()));
            parameters.put("ordergroup", MoreObjects.firstNonNull(entrust.getLocalEntrustId(), "0"));
            parameters.put("bankcode", "");
            parameters.put("remark", "");

            ResultBean resultBean = tradeAgentHelper.doRequest(agentInfo, parameters);
            if (resultBean.getErrorCode() == ErrorCode.SUCCESS.value) {
                List<Map<String, String>> resList = (List<Map<String, String>>) resultBean.getData();
                for (Map<String, String> resMap : resList) {
                    entrust.setBrokerEntrustId(resMap.get("ordersno"));
                    entrust.setReportNo(resMap.get("orderid"));
                }
            } else {
                entrust.setEntrustStatus(EntrustStatus.INVALID.value);
                entrust.setErrorCode(resultBean.getErrorCode());
                entrust.setErrorMsg(resultBean.getErrorMsg());
            }
            TerminalId terminalId = new TerminalId();
            BeanUtils.copyProperties(entrust, terminalId);
            onRspEntrust(terminalId);
        } catch (Exception e) {
            logger.error("send entrust error:{},{}", entrust.getId(), e.getMessage(), e);
            throw new RuntimeException(ErrorCode.TERMINAL_API_ERROR.errorMsg);
        }
    }

    /**
     * 综合业务委托下单
     * 410411	用于深交所综合协议平台报单，上交所大宗交易报单
     * 460010	固收平台委托买卖交易
     * 460013	固收平台确定报价报价申报
     * 460014	固收平台确定报价成交申报
     * 411031	上海债券协议回购意向申报
     * 411032	上海债券协议回购报价申报
     * 411024	上海债券协议回购成交申报
     * 411000	债券协议回购委托申报
     * 410677   新债 深圳 协商成交下单
     * 410678   新债 深圳 协商成交回复
     */
    @Override
    public void integratedEntrust(IntegratedEntrust entrust) {
        logger.info("send integratedEntrust:{}", entrust.getId());
        entrust.setLocalEntrustId(genLocalId());
        entrustIdManager.setLocalEntrustIdMapping(entrust.getAccountId(), entrust.getLocalEntrustId(), entrust.getId());

        Map<String, String> parameters = Maps.newHashMap();
        parameters.put("accountId", entrust.getAccountId());
        parameters.put("entrustProp", entrust.getEntrustProp());
        parameters.put("entrustBs", entrust.getEntrustBs());
        parameters.put("market", getAgentMarket(entrust.getMarket()));
        parameters.put("bsflag", entrust.getBsFlag());
        parameters.put("stkcode", StockCodeUtils.getStockCode(entrust.getStockCode()));
        String entrustProp = getIntegratedEntrustProp(entrust, parameters);

        logger.info("报价参数parameters：{}", parameters);

        TerminalAgentInfo agentInfo = terminalAgentInfoService.selectOne(getTerminalName(), FunctionCode.REQ_INTEGRATED_ENTRUST.value, entrustProp);
        agentInfo.setAccountId(entrust.getAccountId());
        try {
            ResultBean resultBean = tradeAgentHelper.doRequest(agentInfo, parameters);
            entrust.setEntrustStatus(EntrustStatus.TOBE_REPORTED.value);
            if (resultBean.getErrorCode() == ErrorCode.SUCCESS.value) {
                List<Map<String, String>> resList = (List<Map<String, String>>) resultBean.getData();
                for (Map<String, String> resMap : resList) {
                    entrust.setBrokerEntrustId(resMap.get("ordersno"));
                    entrust.setReportNo(resMap.get("orderid"));
                }
            } else {
                entrust.setErrorCode(resultBean.getErrorCode());
                entrust.setErrorMsg(resultBean.getErrorMsg());
            }

            TerminalId terminalId = new TerminalId();
            BeanUtils.copyProperties(entrust, terminalId);
            onRspIntegratedEntrust(terminalId);
        } catch (Exception e) {
            logger.error("send integratedEntrust error:{},{}", entrust.getId(), e.getMessage(), e);
            throw new RuntimeException(ErrorCode.TERMINAL_API_ERROR.errorMsg);

        }
    }

    /**
     * 委托撤单
     * 410413	用于竞价平台所有委托业务撤单
     */
    @Override
    public void cancelEntrust(Entrust entrust, EntrustInterface cancelEntrust) {
        CancelEntrustBean cancelEntrustBean = getCancelEntrustBean(entrust, cancelEntrust);

        Map<String, String> parameters = Maps.newHashMap();
        parameters.put("accountId", cancelEntrustBean.getAccountId());
        parameters.put("orderdate", cancelEntrustBean.getTradingDay());
        parameters.put("ordersno", cancelEntrustBean.getBrokerEntrustId());
        parameters.put("bsflag", cancelEntrustBean.getBsFlag());

        TerminalAgentInfo agentInfo = terminalAgentInfoService.selectOne(getTerminalName(), FunctionCode.REQ_CANCEL_ENTRUST.value);
        agentInfo.setAccountId(cancelEntrustBean.getAccountId());
        try {
            ResultBean resultBean = tradeAgentHelper.doRequest(agentInfo, parameters);
            if (resultBean.getErrorCode() == ErrorCode.SUCCESS.value) {
                List<Map<String, String>> resList = (List<Map<String, String>>) resultBean.getData();
                for (Map<String, String> resMap : resList) {
                    cancelEntrustBean.setCancelBrokerEntrustId(resMap.get("ordersno"));
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

    /**
     * 委托撤单
     * 410413	用于深交所综合协议平台撤单，上交所大宗交易撤单
     * 411001	债券协议回购委托撤单申报  深交所质押式协议回购委托撤单
     * 411025	上海债券协议回购撤单申报	 上交所质押式协议回购委托撤单
     * 460011	固收平台撤单委托  上交所固收平台现券交易委托撤单
     */
    @Override
    public void cancelIntegratedEntrust(IntegratedEntrust integratedEntrust, IntegratedEntrust cancelEntrust) {
        CancelEntrustBean cancelEntrustBean = getCancelEntrustBean(integratedEntrust, cancelEntrust);
        String entrustProp = null;
        if (IntegratedEntrustUtils.isBulk(cancelEntrustBean.getEntrustProp())) {
            entrustProp = "Bulk";
        } else if (IntegratedEntrustUtils.isProtocolBack_SZ(cancelEntrustBean.getEntrustProp())) {
            entrustProp = "ProtocolBack_SZ";
        } else if (IntegratedEntrustUtils.isProtocolBack_SH(cancelEntrustBean.getEntrustProp())) {
            entrustProp = "ProtocolBack_SH";
        } else if (IntegratedEntrustUtils.isFixedIncome(cancelEntrustBean.getEntrustProp())) {
            entrustProp = "FixedIncome";
        }
        Map<String, String> parameters = Maps.newHashMap();
        parameters.put("accountId", cancelEntrustBean.getAccountId());
        parameters.put("market", cancelEntrustBean.getMarket());
        parameters.put("orderdate", cancelEntrustBean.getTradingDay());
        parameters.put("ordersno", cancelEntrustBean.getBrokerEntrustId());
        parameters.put("bsflag", cancelEntrustBean.getBsFlag());

        TerminalAgentInfo agentInfo = terminalAgentInfoService.selectOne(getTerminalName(), FunctionCode.REQ_CANCEL_INTEGRATED_ENTRUST.value, entrustProp);
        agentInfo.setAccountId(cancelEntrustBean.getAccountId());
        try {
            ResultBean resultBean = tradeAgentHelper.doRequest(agentInfo, parameters);
            if (resultBean.getErrorCode() == ErrorCode.SUCCESS.value) {
                List<Map<String, String>> resList = (List<Map<String, String>>) resultBean.getData();
                for (Map<String, String> resMap : resList) {
                    cancelEntrustBean.setCancelBrokerEntrustId(resMap.get("ordersno"));
                }
            } else {
                cancelEntrustBean.setErrorCode(resultBean.getErrorCode());
                cancelEntrustBean.setErrorMsg(resultBean.getErrorMsg());
            }
            onRspCancelIntegratedEntrust(cancelEntrustBean);
        } catch (Exception e) {
            logger.error(" cancelIntegratedEntrust error:{},{}", integratedEntrust.getId(), e.getMessage(), e);
            throw new RuntimeException(ErrorCode.TERMINAL_API_ERROR.errorMsg);
        }
    }

    /**
     * 查询大宗行情
     * 410995	深证大宗交易行情查询	查询深证大宗交易行情
     * 410996	上海大宗交易行情查询	查询上海大宗交易行情
     * 411027	上海债券协议回购非公开报价行情查询	上交所质押式协议回购非公开报价行情查询
     * 411029	上海债券协议回购公开报价行情查询	上交所质押式协议回购公开报价行情查询
     * 460018	固收平台确定报价行情查询	上交所固收平台现券交易报价行情查询
     * 410556   上海盘后固定价格交易行情查询
     * 410559   深圳盘后固定价格交易行情查询
     */
    @Override
    public List<IntegratedQuote> reqIntegratedQuotes(QuoteQryBean quoteQryBean) throws Exception {
        Map<String, String> parameters = Maps.newHashMap();
        parameters.put("accountId", quoteQryBean.getAccountId());
        int quoteType = getQuoteType(quoteQryBean, parameters);

        TerminalAgentInfo agentInfo = terminalAgentInfoService.selectOne(getTerminalName(),
                FunctionCode.REQ_QRY_INTEGRATED_QUOTE.value, quoteType, quoteQryBean.getMarket());
        agentInfo.setAccountId(quoteQryBean.getAccountId());
        ResultBean resultBean = tradeAgentHelper.doRequest(agentInfo, parameters);
        if (resultBean.getErrorCode() != ErrorCode.SUCCESS.value) {
            return Lists.newArrayList();
        }

        return KCBPMsgParseHelper.parseIntegratedQuotes(resultBean, quoteQryBean);
    }

    /**
     * 查询最大交易数量
     * 410410	用于竞价平台所有委托业务可买数量预算
     */
    @Override
    public int reqEnable(EnableQryBean qryBean) throws Exception {
        String[] marketAndStockCode = StockCodeUtils.getMarketAndStockCode(qryBean.getStockCode());
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put("accountId", qryBean.getAccountId());
        parameters.put("market", getAgentMarket(marketAndStockCode[1]));
        parameters.put("stockCode", marketAndStockCode[0]);
        parameters.put("bsflag", EntrustUtils.getBsFlag(qryBean.getEntrustProp(), qryBean.getEntrustBs()));
        parameters.put("price", qryBean.getEntrustPrice());

        TerminalAgentInfo agentInfo = terminalAgentInfoService.selectOne(getTerminalName(), FunctionCode.REQ_QRY_TRADE_ENABLE.value);
        agentInfo.setAccountId(qryBean.getAccountId());
        ResultBean resultBean = tradeAgentHelper.doRequest(agentInfo, parameters);
        if (resultBean.getErrorCode() != ErrorCode.SUCCESS.value) {
            return -1;
        }
        List<Map<String, String>> resList = (List<Map<String, String>>) resultBean.getData();
        int amount = 0;
        for (Map<String, String> resMap : resList) {
            amount = Integer.parseInt(resMap.get("maxstkqty"));
        }
        return amount;
    }

    /**
     * 查询历史交易明细
     * 411513	历史成交明细	竞价平台历史成交流水查询
     */
    @Override
    public List<HistoryTrade> reqHistoryTrade(HistoryQryBean historyQryBean) throws Exception {
        String accountId = historyQryBean.getAccountId();
        Map<String, String> parameters = Maps.newHashMap();
        parameters.put("accountId", accountId);
        parameters.put("strdate", String.valueOf(historyQryBean.getStartDay()));
        parameters.put("enddate", String.valueOf(historyQryBean.getEndDay()));
        //0:当前合约   1:历史合约
        parameters.put("qryflag", "1");
        parameters.put("count", String.valueOf(historyQryBean.getPageSize()));
        parameters.put("market", "");
        parameters.put("stkcode", "");
        parameters.put("bankcode", "");
        parameters.put("postStr", historyQryBean.getPostStr());

        TerminalAgentInfo agentInfo = terminalAgentInfoService.selectOne(getTerminalName(), FunctionCode.REQ_QRY_HISTORY_TRADE.value);
        agentInfo.setAccountId(accountId);
        ResultBean resultBean = tradeAgentHelper.doRequest(agentInfo, parameters);
        if (resultBean.getErrorCode() != ErrorCode.SUCCESS.value) {
            return Lists.newArrayList();
        }

        return KCBPMsgParseHelper.parseHistoryTrade(resultBean, accountId);
    }

    /**
     * 查询帐号详情
     * 410502	资金查询	查询客户的资金情况
     */
    @Override
    public List<AccountFinance> reqCustomerFinance(String accountId) throws Exception {
        TerminalAgentInfo agentInfo = terminalAgentInfoService.selectOne(getTerminalName(), FunctionCode.REQ_QRY_FINANCE.value);
        agentInfo.setAccountId(accountId);
        Map<String, String> parameters = Maps.newHashMap();
        parameters.put("accountId", accountId);
        parameters.put("fortuneid", "-1");
        parameters.put("moneytype", "");
        parameters.put("bsflag", "");
        ResultBean resultBean = tradeAgentHelper.doRequest(agentInfo, parameters);
        if (resultBean.getErrorCode() != ErrorCode.SUCCESS.value) {
            return Lists.newArrayList();
        }
        return KCBPMsgParseHelper.parseCustomerFinance(resultBean, accountId);
    }

    /**
     * 查询持仓详情
     * 410504 证券持仓快速查询
     */
    @Override
    public List<PositionBean> reqAccountPosition(PositionQueryBean positionQueryBean) throws Exception {
        String accountId = positionQueryBean.getAccountId();
        String stockCode = positionQueryBean.getStockCode();
        Map<String, String> parameters = Maps.newHashMap();
        parameters.put("accountId", accountId);
        if (!Strings.isNullOrEmpty(stockCode)) {
            String market = StockCodeUtils.getMarket(stockCode);
            parameters.put("stkcode", StockCodeUtils.getMarket(stockCode));
            if (market.equals(Market.SZSE.value)) {
                parameters.put("market", "0");
            } else if (market.equals(Market.SHSE.value)) {
                parameters.put("market", "1");
            }
        } else {
            parameters.put("market", "");
            parameters.put("stkcode", "");
        }

        TerminalAgentInfo agentInfo = terminalAgentInfoService.selectOne(getTerminalName(), FunctionCode.REQ_QRY_POSITION.value);
        agentInfo.setAccountId(accountId);
        ResultBean resultBean = tradeAgentHelper.doRequest(agentInfo, parameters);
        if (resultBean.getErrorCode() != ErrorCode.SUCCESS.value) {
            return Lists.newArrayList();
        }
        return KCBPMsgParseHelper.parseAccountPosition(resultBean, accountId);
    }

    /**
     * 查询合约数据
     * 410203	证券信息查询	竞价平台委托查询
     */
    @Override
    public List<Instrument> reqInstrumentInfo(InstrumentQryBean instrumentQryBean) throws Exception {
        String accountId = instrumentQryBean.getAccountId();
        Map<String, String> parameters = Maps.newHashMap();
        parameters.put("accountId", accountId);
        parameters.put("market", getAgentMarket(instrumentQryBean.getMarket()));
        parameters.put("stkcode", "");
        parameters.put("rowcount", String.valueOf(instrumentQryBean.getRowCount()));
        parameters.put("poststr", instrumentQryBean.getPostStr());

        TerminalAgentInfo agentInfo = terminalAgentInfoService.selectOne(getTerminalName(), FunctionCode.REQ_QRY_INSTRUMENT_INFO.value);
        agentInfo.setAccountId(accountId);
        ResultBean resultBean = tradeAgentHelper.doRequest(agentInfo, parameters);
        if (resultBean.getErrorCode() != ErrorCode.SUCCESS.value) {
            return Lists.newArrayList();
        }

        return KCBPMsgParseHelper.parseInstrumentInfo(resultBean);
    }

    /**
     * 债券质押率
     * 410575	质押库明细查询	用于债券质押式回购可交易额度获取
     */
    @Override
    public List<PledgeDiscount> reqPledgeDiscount(String accountId, String market, String stockCode) throws Exception {
        String marketStr = market;
        Map<String, String> parameters = Maps.newHashMap();
        parameters.put("accountId", accountId);
        if (!Strings.isNullOrEmpty(stockCode)) {
            String[] marketAndStockCode = StockCodeUtils.getMarketAndStockCode(stockCode);
            if (!Strings.isNullOrEmpty(market) && !market.equals(marketAndStockCode[1])) {
                return Lists.newArrayList();
            }
            marketStr = marketAndStockCode[1];
            parameters.put("stkcode", marketAndStockCode[0]);
        }
        if (!Strings.isNullOrEmpty(marketStr)) {
            parameters.put("market", getAgentMarket(marketStr));
        }
        parameters.put("qryflag", "1");
        parameters.put("count", "100");
        parameters.put("poststr", "");

        TerminalAgentInfo agentInfo = terminalAgentInfoService.selectOne(getTerminalName(), FunctionCode.REQ_PLEDGE_DISCOUNT.value);
        agentInfo.setAccountId(accountId);
        ResultBean resultBean = tradeAgentHelper.doRequest(agentInfo, parameters);
        if (resultBean.getErrorCode() != ErrorCode.SUCCESS.value) {
            return Lists.newArrayList();
        }
        return KCBPMsgParseHelper.parsePledgeDiscount(resultBean, accountId);
    }

    /**
     * 标准券查询
     * 410574	标准券汇总查询	用于质押出入库可用标准券额度获取
     */
    @Override
    public List<StandardBonds> reqStandardBonds(StandardBondQryBean standardBondQryBean) throws Exception {
        String accountId = standardBondQryBean.getAccountId();
        String marketStr = standardBondQryBean.getMarket();
        Map<String, String> parameters = Maps.newHashMap();
        parameters.put("accountId", accountId);
        String stockCode = standardBondQryBean.getStockCode();
        if (!Strings.isNullOrEmpty(stockCode)) {
            marketStr = StockCodeUtils.getMarket(stockCode);
            if (!Strings.isNullOrEmpty(standardBondQryBean.getMarket()) && !standardBondQryBean.getMarket().equals(marketStr)) {
                return Lists.newArrayList();
            }
            parameters.put("stkcode", StockCodeUtils.getStockCode(stockCode));
        }
        if (!Strings.isNullOrEmpty(marketStr)) {
            parameters.put("market", getAgentMarket(marketStr));
        }
        parameters.put("qryflag", "1");
        parameters.put("count", "100");
        parameters.put("poststr", "");

        TerminalAgentInfo agentInfo = terminalAgentInfoService.selectOne(getTerminalName(), FunctionCode.REQ_STANDARD_BONDS.value);
        agentInfo.setAccountId(accountId);
        ResultBean resultBean = tradeAgentHelper.doRequest(agentInfo, parameters);
        if (resultBean.getErrorCode() != ErrorCode.SUCCESS.value) {
            return Lists.newArrayList();
        }

        return KCBPMsgParseHelper.parseStandardBonds(resultBean, accountId);
    }

    /**
     * 查询大宗行情
     * 410995	深证大宗交易行情查询	查询深证大宗交易行情
     * 410996	上海大宗交易行情查询	查询上海大宗交易行情
     */
    @Override
    public List<IntegratedBulkQuoteBean> reqIntegratedBulkQuote(IntegratedBulkQuoteQryBean bulkQuoteQryBean) throws Exception {
        String accountId = bulkQuoteQryBean.getAccountId();
        Map<String, String> parameters = Maps.newHashMap();
        parameters.put("accountId", accountId);
        String stockCode = bulkQuoteQryBean.getStockCode();
        String market = StockCodeUtils.getMarket(stockCode);
        parameters.put("stkcode", StockCodeUtils.getStockCode(stockCode));
        String bsFlag = "";
        String entrustProp = bulkQuoteQryBean.getEntrustProp();
        String entrustBs = bulkQuoteQryBean.getEntrustBs();
        if (!Strings.isNullOrEmpty(entrustProp) && !Strings.isNullOrEmpty(entrustBs)) {
            if (Market.SZSE.value.equals(market) || Market.SHSE.value.equals(market)) {
                if (entrustProp.equals(EntrustProp.INTENTION.value) && entrustBs.equals(EntrustBs.BUY.value)) {
                    bsFlag = BSFlag.YXMR.value;
                } else if (entrustProp.equals(EntrustProp.INTENTION.value) && entrustBs.equals(EntrustBs.SELL.value)) {
                    bsFlag = BSFlag.YXMC.value;
                }
            }
            if (Market.SZSE.value.equals(market)) {
                if (entrustProp.equals(EntrustProp.SET_PRICE.value) && entrustBs.equals(EntrustBs.BUY.value)) {
                    bsFlag = BSFlag.DJMR.value;
                } else if (entrustProp.equals(EntrustProp.SET_PRICE.value) && entrustBs.equals(EntrustBs.SELL.value)) {
                    bsFlag = BSFlag.DJMC.value;
                }
            }
        }
        parameters.put("bsflag", bsFlag);

        TerminalAgentInfo agentInfo = terminalAgentInfoService.selectOne(getTerminalName(), FunctionCode.REQ_QRY_INTEGRATED_BULK_QUOTE.value, null, market);
        agentInfo.setAccountId(accountId);
        ResultBean resultBean = tradeAgentHelper.doRequest(agentInfo, parameters);
        if (resultBean.getErrorCode() != ErrorCode.SUCCESS.value) {
            return Lists.newArrayList();
        }

        return KCBPMsgParseHelper.parseIntegratedBulkQuote(resultBean, market);
    }

    /**
     * 查询盘后大宗行情
     * 410556 上海盘后大宗交易行情查询  上海盘后固定价格交易行情查询
     * 410559 深圳盘后大宗交易行情查询  深圳盘后固定价格交易行情查询
     */
    @Override
    public List<IntegratedBulkCloseQuoteBean> reqIntegratedBulkCloseQuote(IntegratedBulkCloseQuoteQryBean bulkCloseQuoteQryBean) throws Exception {
        String accountId = bulkCloseQuoteQryBean.getAccountId();
        Map<String, String> parameters = Maps.newHashMap();
        parameters.put("accountId", accountId);
        String stockCode = bulkCloseQuoteQryBean.getStockCode();
        parameters.put("stkcode", StockCodeUtils.getStockCode(stockCode));
        String market = StockCodeUtils.getMarket(stockCode);
        parameters.put("count", String.valueOf(bulkCloseQuoteQryBean.getPageSize()));
        parameters.put("bsflag", "");
        parameters.put("poststr", "");

        TerminalAgentInfo agentInfo = terminalAgentInfoService.selectOne(getTerminalName(), FunctionCode.REQ_QRY_INTEGRATED_BULK_CLOSE_QUOTE.value, null, market);
        agentInfo.setAccountId(accountId);
        ResultBean resultBean = tradeAgentHelper.doRequest(agentInfo, parameters);
        if (resultBean.getErrorCode() != ErrorCode.SUCCESS.value) {
            return Lists.newArrayList();
        }
        return KCBPMsgParseHelper.parseIntegratedBulkCloseQuote(resultBean, market);
    }

    /**
     * 查询协议回购行情
     * 411027	上海债券协议回购非公开报价行情查询	上交所质押式协议回购非公开报价行情查询
     * 411029	上海债券协议回购公开报价行情查询	上交所质押式协议回购公开报价行情查询
     */
    @Override
    public List<IntegratedRepurchaseQuoteBean> reqIntegratedRepurchaseQuote(IntegratedRepurchaseQuoteQryBean repurchaseQuoteQryBean) throws Exception {
        String accountId = repurchaseQuoteQryBean.getAccountId();
        Map<String, String> parameters = Maps.newHashMap();
        parameters.put("accountId", accountId);
        int actionIn = repurchaseQuoteQryBean.getActionIn();
        if (ActionIn.NON_PUBLIC_QUOTE.value == actionIn) {
            parameters.put("ordtype", getOrdtype(repurchaseQuoteQryBean.getEntrustProp()));
            parameters.put("stockCode", "");
        }
        parameters.put("qryrec", String.valueOf(repurchaseQuoteQryBean.getPageSize()));
        parameters.put("posstr", "");

        TerminalAgentInfo agentInfo = terminalAgentInfoService.selectOne(getTerminalName(), FunctionCode.REQ_QRY_INTEGRATED_REPURCHASE_QUOTE.value, actionIn, null);
        agentInfo.setAccountId(accountId);
        ResultBean resultBean = tradeAgentHelper.doRequest(agentInfo, parameters);
        if (resultBean.getErrorCode() != ErrorCode.SUCCESS.value) {
            return Lists.newArrayList();
        }

        return KCBPMsgParseHelper.parseIntegratedRepurchaseQuote(resultBean);
    }

    /**
     * 查询上海固收平台报价行情
     * 460018	固收平台确定报价行情查询	上交所固收平台现券交易报价行情查询
     */
    @Override
    public List<IntegratedFixedIncomeQuoteBean> reqIntegratedFixedIncomeQuote(IntegratedFixedIncomeQuoteQryBean fixedIncomeQuoteQryBean) throws Exception {
        String accountId = fixedIncomeQuoteQryBean.getAccountId();
        String stockCode = fixedIncomeQuoteQryBean.getStockCode();

        Map<String, String> parameters = Maps.newHashMap();
        if (!Strings.isNullOrEmpty(stockCode)) {
            parameters.put("stkcode", StockCodeUtils.getStockCode(stockCode));
        }
        parameters.put("market", "1");
        parameters.put("count", String.valueOf(fixedIncomeQuoteQryBean.getPageSize()));
        parameters.put("posstr", "");

        TerminalAgentInfo agentInfo = terminalAgentInfoService.selectOne(getTerminalName(), FunctionCode.REQ_QRY_INTEGRATED_FIXED_INCOME_QUOTE.value);
        agentInfo.setAccountId(accountId);
        ResultBean resultBean = tradeAgentHelper.doRequest(agentInfo, parameters);
        if (resultBean.getErrorCode() != ErrorCode.SUCCESS.value) {
            return Lists.newArrayList();
        }

        return KCBPMsgParseHelper.parseIntegratedFixedIncomeQuote(resultBean);
    }

    @Override
    public void reqReissueEntrust(String accountId) {
        Map<String, EntrustInterface> entrustInterfaceMap = getAllReissueEntrusts(accountId);
        compareReissueEntrust(accountId, entrustInterfaceMap);
    }

    private Map<String, EntrustInterface> getAllReissueEntrusts(String accountId) {
        Map<String, EntrustInterface> entrustInterfaceMap = Maps.newHashMap();
        KCBPAccount kcbpAccount = getKCBPAccount(accountId);
        if (null == kcbpAccount) {
            logger.error(("登录账户信息不存在：accountId={}"), accountId);
            return entrustInterfaceMap;
        }

        List<Entrust> entrustList = queryIncrementEntrust0(kcbpAccount);
        if (Objects.isNull(entrustList)) {
            entrustList = new ArrayList<>();
        }
        List<IntegratedEntrust> integratedEntrustList = getAllReissueIntegratedEntrust(kcbpAccount);
        entrustList.forEach(res -> {
            if (!Strings.isNullOrEmpty(res.getId())) {
                entrustInterfaceMap.put(res.getId(), res);
            }
        });
        integratedEntrustList.forEach(res -> {
            if (!Strings.isNullOrEmpty(res.getId())) {
                entrustInterfaceMap.put(res.getId(), res);
            }
        });

        return entrustInterfaceMap;
    }


    private List<IntegratedEntrust> getAllReissueIntegratedEntrust(KCBPAccount kcbpAccount) {
        List<IntegratedEntrust> res = new ArrayList<>();
        res.addAll(queryFixedIncomeEntrust0(kcbpAccount));
        res.addAll(queryProtocolBack_SH_Entrust0(kcbpAccount));
        res.addAll(queryProtocolBack_SZ_Entrust0(kcbpAccount));
        res.addAll(queryBondNegotiate_SZ_Entrust0(kcbpAccount));
        return res;
    }

    @Override
    public void reqReissueTrade(String accountId) {
        List<Trade> tradeList = getAllReissueTrades(accountId);
        compareReissueTrade(accountId, tradeList);
    }

    private List<Trade> getAllReissueTrades(String accountId) {
        KCBPAccount kcbpAccount = getKCBPAccount(accountId);
        if (null == kcbpAccount) {
            logger.error(("登录账户信息不存在：accountId={}"), accountId);
            return Collections.emptyList();
        }
        List<Trade> res = new ArrayList<>();
        res.addAll(queryIncrementTrade0(kcbpAccount));
        res.addAll(queryFixedIncomeTrade0(kcbpAccount));
        res.addAll(queryProtocolBack_SZ_Trade0(kcbpAccount));
        res.addAll(queryProtocolBack_SH_Trade0(kcbpAccount));
        res.addAll(queryBondNegotiate_SZ_Trade0(kcbpAccount));
        return res;
    }

    @Override
    public void queryNextTrade() {
        if (MapUtils.isEmpty(kcbpAccounts)) {
            return;
        }
        for (Map.Entry<String, KCBPAccount> entry : kcbpAccounts.entrySet()) {
            KCBPAccount kcbpAccount = entry.getValue();
            String accountId = kcbpAccount.getAccountId();
            logger.info("account：{}，status：{}", accountId, getStatus(accountId));
            if (getStatus(accountId) != AccountLoginStatus.TRADING.value) {
                return;
            }
            boolean loginStatus = tradeAgentManager.getAgentLoginStatus(accountId);
            if (loginStatus) {
                executeQueryNextTrade(kcbpAccount);
            }
        }
    }

    private void executeQueryNextTrade(KCBPAccount kcbpAccount) {
        if (null == poolExecutor) {
            poolExecutor = new ThreadPoolExecutor(8, 20, 1000, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>(100), new ThreadPoolExecutor.DiscardPolicy());
            poolExecutor.prestartAllCoreThreads();
        }

        for (QueryEnum queryEnum : QueryEnum.getValues()) {
            try {
                poolExecutor.execute(new QueryTask(queryEnum, kcbpAccount));
                Thread.sleep(500);
            } catch (Exception e) {
                logger.error("{}执行异常：{}", queryEnum.name(), e.getMessage(), e);
            }
        }
    }

    enum QueryEnum {
        queryIncrementEntrust(1),
        queryFixedIncomeEntrust(2),
        queryProtocolBack_SH_Entrust(3),
        queryProtocolBack_SZ_Entrust(4),
        queryIncrementTrade(5),
        queryFixedIncomeTrade(6),
        queryProtocolBack_SZ_Trade(7),
        queryProtocolBack_SH_Trade(8),
        queryBondNegotiate_SZ_Entrust(9),
        queryBondNegotiate_SZ_Trade(10),
        ;

        private Integer code;

        QueryEnum(Integer code) {
            this.code = code;
        }

        public Integer getCode() {
            return code;
        }

        public static List<QueryEnum> getValues(){
            return Lists.newArrayList(QueryEnum.values()).stream()
                    .sorted(Comparator.comparing(QueryEnum::getCode)).collect(Collectors.toList());
        }
    }

    class QueryTask implements Runnable {
        private QueryEnum queryEnum;
        private KCBPAccount kcbpAccount;

        public QueryTask(QueryEnum queryEnum, KCBPAccount kcbpAccount) {
            this.queryEnum = queryEnum;
            this.kcbpAccount = kcbpAccount;
        }

        @Override
        public void run() {
            System.out.println(this.toString() + " is running!");
            try {
                switch (queryEnum) {
                    case queryIncrementEntrust:
                        queryIncrementEntrust(kcbpAccount);
                        return;
                    case queryFixedIncomeEntrust:
                        queryFixedIncomeEntrust(kcbpAccount);
                        return;
                    case queryProtocolBack_SH_Entrust:
                        queryProtocolBack_SH_Entrust(kcbpAccount);
                        return;
                    case queryProtocolBack_SZ_Entrust:
                        queryProtocolBack_SZ_Entrust(kcbpAccount);
                        return;
                    case queryIncrementTrade:
                        queryIncrementTrade(kcbpAccount);
                        return;
                    case queryFixedIncomeTrade:
                        queryFixedIncomeTrade(kcbpAccount);
                        return;
                    case queryProtocolBack_SH_Trade:
                        queryProtocolBack_SH_Trade(kcbpAccount);
                        return;
                    case queryProtocolBack_SZ_Trade:
                        queryProtocolBack_SZ_Trade(kcbpAccount);
                        return;
                    case queryBondNegotiate_SZ_Entrust:
                    	queryBondNegotiate_SZ_Entrust(kcbpAccount);
                        return;
                    case queryBondNegotiate_SZ_Trade:
                    	queryBondNegotiate_SZ_Trade(kcbpAccount);
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

    public void queryIncrementTrade(KCBPAccount kcbpAccount) {
        try {
            List<Trade> trades = queryIncrementTrade0(kcbpAccount);
            trades.forEach(this::onTrade);
        } catch (Exception e) {
            logger.error("queryIncrementTrade: {} error:{}", kcbpAccount.getAccountId(), e);
        }
    }

    public void queryFixedIncomeTrade(KCBPAccount kcbpAccount) {
        try {
            List<Trade> trades = queryFixedIncomeTrade0(kcbpAccount);
            trades.forEach(this::onTrade);
        } catch (Exception e) {
            logger.error("queryFixedIncomeTrade: {} error:{}", kcbpAccount.getAccountId(), e);
        }
    }

    public void queryProtocolBack_SZ_Trade(KCBPAccount kcbpAccount) {
        try {
            List<Trade> trades = queryProtocolBack_SZ_Trade0(kcbpAccount);
            trades.forEach(this::onTrade);
        } catch (Exception e) {
            logger.error("queryProtocolBack_SZ_Trade: {} error:{}", kcbpAccount.getAccountId(), e);
        }
    }

    public void queryProtocolBack_SH_Trade(KCBPAccount kcbpAccount) {
        try {
            List<Trade> trades = queryProtocolBack_SH_Trade0(kcbpAccount);
            trades.forEach(this::onTrade);
        } catch (Exception e) {
            logger.error("queryProtocolBack_SH_Trade: {} error:{}", kcbpAccount.getAccountId(), e);
        }
    }
    
    public void queryBondNegotiate_SZ_Trade(KCBPAccount kcbpAccount) {
        try {
            List<Trade> trades = queryBondNegotiate_SZ_Trade0(kcbpAccount);
            trades.forEach(this::onTrade);
        } catch (Exception e) {
            logger.error("queryBondNegotiate_SZ_Trade: {} error:{}", kcbpAccount.getAccountId(), e);
        }
    }

    private void onTrade(Trade trade) {
        if (Strings.isNullOrEmpty(trade.getBrokerTradeId())) {
            trade.setBrokerTradeId(trade.getEntrustId());
        }
        onRtnTrade(trade);
    }

    public void queryIncrementEntrust(KCBPAccount kcbpAccount) {
        try {
            List<Entrust> entrustList = queryIncrementEntrust0(kcbpAccount);
            entrustList.forEach(entrust -> {
                if (!Strings.isNullOrEmpty(entrust.getId())) {
                    if (entrust.getId().startsWith(IDPrefix.NORMAL.value)) {
                        onRtnEntrust(entrust);
                    } else {
                        IntegratedEntrust integratedEntrust = new IntegratedEntrust();
                        BeanUtils.copyProperties(entrust, integratedEntrust);
                        onRtnIntegratedEntrust(integratedEntrust);
                    }
                } else {
                    logger.info("kcbp_API queryIncrementEntrust has no entrustId {}", JsonUtils.getJsonFromObject(entrust));
                }
            });
        } catch (Exception e) {
            logger.error("queryIncrementEntrust: {} error:{}", kcbpAccount.getAccountId(), e);
        }
    }

    public void queryProtocolBack_SH_Entrust(KCBPAccount kcbpAccount) {
        try {
            List<IntegratedEntrust> integratedEntrusts = queryProtocolBack_SH_Entrust0(kcbpAccount);
            integratedEntrusts.forEach(this::onRtnIntegratedEntrust);
        } catch (Exception e) {
            logger.error("queryProtocolBack_SH_Entrust: {} error:{}", kcbpAccount.getAccountId(), e);
        }
    }

    public void queryProtocolBack_SZ_Entrust(KCBPAccount kcbpAccount) {
        try {
            List<IntegratedEntrust> integratedEntrusts = queryProtocolBack_SZ_Entrust0(kcbpAccount);
            integratedEntrusts.forEach(this::onRtnIntegratedEntrust);
        } catch (Exception e) {
            logger.error("queryProtocolBack_SZ_Entrust: {} error:{}", kcbpAccount.getAccountId(), e);
        }
    }
    
    /**
     * 410680  深圳现券协商成交、竞买发起、竞买应价委托查询
     * @param kcbpAccount
     */
    public void queryBondNegotiate_SZ_Entrust(KCBPAccount kcbpAccount) {
        try {
            List<IntegratedEntrust> integratedEntrusts = queryBondNegotiate_SZ_Entrust0(kcbpAccount);
            integratedEntrusts.forEach(this::onRtnIntegratedEntrust);
        } catch (Exception e) {
            logger.error("queryBondNegotiate_SZ_Entrust: {} error:{}", kcbpAccount.getAccountId(), e);
        }
    }

    public void queryFixedIncomeEntrust(KCBPAccount kcbpAccount) {
        try {
            List<IntegratedEntrust> integratedEntrusts = queryFixedIncomeEntrust0(kcbpAccount);
            integratedEntrusts.forEach(this::onRtnIntegratedEntrust);
        } catch (Exception e) {
            logger.error("queryFixedIncomeEntrust: {} error:{}", kcbpAccount.getAccountId(), e);
        }
    }

    /**
     * 410510 当日委托明细查询
     */
    private List<Entrust> queryIncrementEntrust0(KCBPAccount kcbpAccount) {
        String accountId = kcbpAccount.getAccountId();
        Map<String, String> parameters = Maps.newHashMap();
        parameters.put("accountId", accountId);
        parameters.put("market", "");
        parameters.put("secuid", "");
        parameters.put("stkcode", "");
        parameters.put("bankcode", "");
        parameters.put("ordersno", "");
        parameters.put("count", "100");
        parameters.put("qryflag", "1");
//        parameters.put("poststr", "");
        parameters.put("poststr", kcbpAccount.getEntrustPositionStr());

        TerminalAgentInfo agentInfo = terminalAgentInfoService.selectOne(getTerminalName(), FunctionCode.REQ_ACCOUNT_ENTRUST_REISSUE.value, "Bidding");
        agentInfo.setAccountId(accountId);
        ResultBean resultBean = tradeAgentHelper.doRequest(agentInfo, parameters);
        if (resultBean.getErrorCode() == ErrorCode.SUCCESS.value) {
            List<Map<String, String>> resList = (List<Map<String, String>>) resultBean.getData();
            if (resList.size() > 0) {
                String newPositionStr = resList.get(resList.size() - 1).get("poststr");
                kcbpAccount.setEntrustPositionStr(newPositionStr);
                logger.info("queryIncrementEntrust newPositionStr:{}", newPositionStr);
            }
            return KCBPMsgParseHelper.parseEntrusts(resultBean, accountId, entrustIdManager);
        }

        return new ArrayList<>();
    }

    /**
     * 460001	固收平台交易委托明细查询
     */
    private List<IntegratedEntrust> queryFixedIncomeEntrust0(KCBPAccount kcbpAccount) {
        String accountId = kcbpAccount.getAccountId();
        Map<String, String> parameters = Maps.newHashMap();
        parameters.put("accountId", accountId);
        parameters.put("market", "1");
        parameters.put("secuid", "");
        parameters.put("stkcode", "");
        parameters.put("ordersno", "");
        parameters.put("count", "100");
        parameters.put("qryflag", "1");
//        parameters.put("poststr", "");
        parameters.put("poststr", kcbpAccount.getFixedIncome_PositionStr_entrust());

        TerminalAgentInfo agentInfo = terminalAgentInfoService.selectOne(getTerminalName(), FunctionCode.REQ_ACCOUNT_ENTRUST_REISSUE.value, "FixedIncome");
        agentInfo.setAccountId(accountId);
        ResultBean resultBean = tradeAgentHelper.doRequest(agentInfo, parameters);
        if (resultBean.getErrorCode() == ErrorCode.SUCCESS.value) {
            List<Map<String, String>> resList = (List<Map<String, String>>) resultBean.getData();
            if (resList.size() > 0) {
                String newPositionStr = resList.get(resList.size() - 1).get("poststr");
                kcbpAccount.setFixedIncome_PositionStr_entrust(newPositionStr);
                logger.info("queryFixedIncomeEntrust newPositionStr :{}", newPositionStr);
            }
            return KCBPMsgParseHelper.parseIntegratedEntrusts(resultBean, accountId, entrustIdManager);
        }
        return new ArrayList<>();
    }

    /**
     * 411006	上海债券协议回购委托流水查询
     */
    private List<IntegratedEntrust> queryProtocolBack_SH_Entrust0(KCBPAccount kcbpAccount) {
        String accountId = kcbpAccount.getAccountId();
        String beginDate = DateUtil.format(LocalDateTime.now(), "yyyyMMdd");
        Map<String, String> parameters = Maps.newHashMap();
        parameters.put("accountId", accountId);
        parameters.put("market", "1");
        parameters.put("secuid", "");
        parameters.put("fundid", "");
        parameters.put("stkcode", "");
        parameters.put("ordersno", "");
        parameters.put("qryflag", "1");
        parameters.put("begindate", beginDate);
        parameters.put("enddate", beginDate);
        parameters.put("count", "100");
//        parameters.put("posstr", "");
        parameters.put("posstr", kcbpAccount.getProtocolBack_SH_PositionStr_entrust());

        TerminalAgentInfo agentInfo = terminalAgentInfoService.selectOne(getTerminalName(), FunctionCode.REQ_ACCOUNT_ENTRUST_REISSUE.value, "ProtocolBack_SH");
        agentInfo.setAccountId(accountId);
        ResultBean resultBean = tradeAgentHelper.doRequest(agentInfo, parameters);
        if (resultBean.getErrorCode() == ErrorCode.SUCCESS.value) {
            List<Map<String, String>> resList = (List<Map<String, String>>) resultBean.getData();
            if (resList.size() > 0) {
                String newPositionStr = resList.get(resList.size() - 1).get("posstr");
                kcbpAccount.setProtocolBack_SH_PositionStr_entrust(newPositionStr);
                logger.info("queryProtocolBack_SH_Entrust newPositionStr :{}", newPositionStr);
            }
            return KCBPMsgParseHelper.parseIntegratedEntrusts(resultBean, accountId, entrustIdManager);
        }
        return new ArrayList<>();
    }

    /**
     * 411002	深圳债券协议回购委托流水查询
     */
    private List<IntegratedEntrust> queryProtocolBack_SZ_Entrust0(KCBPAccount kcbpAccount) {
        String accountId = kcbpAccount.getAccountId();
        String beginDate = DateUtil.format(LocalDateTime.now(), "yyyyMMdd");
        Map<String, String> parameters = Maps.newHashMap();
        parameters.put("accountId", accountId);
        parameters.put("market", "");
        parameters.put("secuid", "");
        parameters.put("fundid", "");
        parameters.put("ordersno", "");
        parameters.put("stkcode", "");
        parameters.put("count", "100");
//        parameters.put("posstr", "");
        parameters.put("posstr", kcbpAccount.getProtocolBack_SZ_PositionStr_entrust());
        parameters.put("begindate", beginDate);
        parameters.put("enddate", beginDate);

        TerminalAgentInfo agentInfo = terminalAgentInfoService.selectOne(getTerminalName(), FunctionCode.REQ_ACCOUNT_ENTRUST_REISSUE.value, "ProtocolBack_SZ");
        agentInfo.setAccountId(accountId);
        ResultBean resultBean = tradeAgentHelper.doRequest(agentInfo, parameters);
        if (resultBean.getErrorCode() == ErrorCode.SUCCESS.value) {
            List<Map<String, String>> resList = (List<Map<String, String>>) resultBean.getData();
            if (resList.size() > 0) {
                String newPositionStr = resList.get(resList.size() - 1).get("posstr");
                kcbpAccount.setProtocolBack_SZ_PositionStr_entrust(newPositionStr);
                logger.info("queryProtocolBack_SZ_Entrust newPositionStr :{}", newPositionStr);
            }
            return KCBPMsgParseHelper.parseIntegratedEntrusts(resultBean, accountId, entrustIdManager);
        }
        return new ArrayList<>();
    }
    
    /**
     * 410680	深圳现券协商成交、竞买发起、竞买应价委托查询
     */
    private List<IntegratedEntrust> queryBondNegotiate_SZ_Entrust0(KCBPAccount kcbpAccount) {
        String accountId = kcbpAccount.getAccountId();
        String beginDate = DateUtil.format(LocalDateTime.now(), "yyyyMMdd");
        Map<String, String> parameters = Maps.newHashMap();
        parameters.put("accountId", accountId);
        parameters.put("market", "");
        parameters.put("secuid", "");
        parameters.put("fundid", "");
        parameters.put("ordersno", "");
        parameters.put("stkcode", "");
        parameters.put("count", "100");
        parameters.put("posstr", kcbpAccount.getBondNegotiate_SZ_PositionStr_Entrust());
        parameters.put("begindate", beginDate);
        parameters.put("enddate", beginDate);

        TerminalAgentInfo agentInfo = terminalAgentInfoService.selectOne(getTerminalName(), FunctionCode.REQ_ACCOUNT_ENTRUST_REISSUE.value, "Negotiate_SZ");
        agentInfo.setAccountId(accountId);
        ResultBean resultBean = tradeAgentHelper.doRequest(agentInfo, parameters);
        if (resultBean.getErrorCode() == ErrorCode.SUCCESS.value) {
            List<Map<String, String>> resList = (List<Map<String, String>>) resultBean.getData();
            if (resList.size() > 0) {
                String newPositionStr = resList.get(resList.size() - 1).get("posstr");
                kcbpAccount.setBondNegotiate_SZ_PositionStr_Entrust(newPositionStr);
                logger.info("queryBondNegotiate_SZ_Entrust newPositionStr :{}", newPositionStr);
            }
            return KCBPMsgParseHelper.parseIntegratedEntrusts(resultBean, accountId, entrustIdManager);
        }
        return new ArrayList<>();
    }

    /**
     * 410436	当日成交明细查询
     */
    private List<Trade> queryIncrementTrade0(KCBPAccount kcbpAccount) {
        String accountId = kcbpAccount.getAccountId();
        Map<String, String> parameters = Maps.newHashMap();
        parameters.put("accountId", accountId);
        parameters.put("market", "");
        parameters.put("secuid", "");
        parameters.put("stkcode", "");
        parameters.put("bankcode", "");
        parameters.put("ordersno", "");
        parameters.put("count", "100");
        parameters.put("qryflag", "1");
//        parameters.put("poststr", "");
        parameters.put("poststr", kcbpAccount.getTradePositionStr());

        TerminalAgentInfo agentInfo = terminalAgentInfoService.selectOne(getTerminalName(), FunctionCode.REQ_ACCOUNT_TRADE_REISSUE.value, "Bidding");
        agentInfo.setAccountId(accountId);
        ResultBean resultBean = tradeAgentHelper.doRequest(agentInfo, parameters);
        if (resultBean.getErrorCode() == ErrorCode.SUCCESS.value) {
            List<Map<String, String>> resList = (List<Map<String, String>>) resultBean.getData();
            if (resList.size() > 0) {
                String newPositionStr = resList.get(resList.size() - 1).get("poststr");
                kcbpAccount.setTradePositionStr(newPositionStr);
                logger.info("queryIncrementTrade newPositionStr :{}", newPositionStr);
            }
            return KCBPMsgParseHelper.parseTrades(resList, accountId, entrustIdManager);
        }
        return new ArrayList<>();
    }

    /**
     * 460003	固收平台交易成交查询
     */
    private List<Trade> queryFixedIncomeTrade0(KCBPAccount kcbpAccount) {
        String accountId = kcbpAccount.getAccountId();
        Map<String, String> parameters = Maps.newHashMap();
        parameters.put("accountId", accountId);
        parameters.put("market", "");
        parameters.put("secuid", "");
        parameters.put("stkcode", "");
        parameters.put("bankcode", "");
        parameters.put("ordersno", "");
        parameters.put("count", "100");
        parameters.put("qryflag", "1");
//        parameters.put("poststr", "");
        parameters.put("poststr", kcbpAccount.getFixedIncome_PositionStr_trade());

        TerminalAgentInfo agentInfo = terminalAgentInfoService.selectOne(getTerminalName(), FunctionCode.REQ_ACCOUNT_TRADE_REISSUE.value, "FixedIncome");
        agentInfo.setAccountId(accountId);
        ResultBean resultBean = tradeAgentHelper.doRequest(agentInfo, parameters);
        if (resultBean.getErrorCode() == ErrorCode.SUCCESS.value) {
            List<Map<String, String>> resList = (List<Map<String, String>>) resultBean.getData();
            if (resList.size() > 0) {
                String newPositionStr = resList.get(resList.size() - 1).get("poststr");
                kcbpAccount.setFixedIncome_PositionStr_trade(newPositionStr);
                logger.info("queryIncrementFixedIncomeTrade newPositionStr :{}", newPositionStr);
            }
            return KCBPMsgParseHelper.parseTrades(resList, accountId, entrustIdManager);
        }
        return new ArrayList<>();
    }

    /**
     * 411003	深圳债券协议回购成交查询
     */
    private List<Trade> queryProtocolBack_SZ_Trade0(KCBPAccount kcbpAccount) {
        String accountId = kcbpAccount.getAccountId();
        String beginDate = DateUtil.format(LocalDateTime.now(), "yyyyMMdd");
        Map<String, String> parameters = Maps.newHashMap();
        parameters.put("accountId", accountId);
        parameters.put("market", "0");
        parameters.put("secuid", "");
        parameters.put("fundid", "");
        parameters.put("ordersno", "");
        parameters.put("count", "100");
//        parameters.put("posstr", "");
        parameters.put("posstr", kcbpAccount.getProtocolBack_SZ_PositionStr_trade());
        parameters.put("begindate", beginDate);
        parameters.put("enddate", beginDate);

        TerminalAgentInfo agentInfo = terminalAgentInfoService.selectOne(getTerminalName(), FunctionCode.REQ_ACCOUNT_TRADE_REISSUE.value, "ProtocolBack_SZ");
        agentInfo.setAccountId(accountId);
        ResultBean resultBean = tradeAgentHelper.doRequest(agentInfo, parameters);
        if (resultBean.getErrorCode() == ErrorCode.SUCCESS.value) {
            List<Map<String, String>> resList = (List<Map<String, String>>) resultBean.getData();
            if (resList.size() > 1) {
                String newPositionStr = resList.get(resList.size() - 1).get("posstr");
                kcbpAccount.setProtocolBack_SZ_PositionStr_trade(newPositionStr);
                logger.info("queryIncrementProtocolBack_SZ_Trade newPositionStr :{}", newPositionStr);
            }
            return KCBPMsgParseHelper.parseTrades(resList, accountId, entrustIdManager);
        }
        return new ArrayList<>();
    }
    
    /**
     * 410681	深圳现券协商成交、竞买发起、竞买应价成交查询
     */
    private List<Trade> queryBondNegotiate_SZ_Trade0(KCBPAccount kcbpAccount) {
        String accountId = kcbpAccount.getAccountId();
        String beginDate = DateUtil.format(LocalDateTime.now(), "yyyyMMdd");
        Map<String, String> parameters = Maps.newHashMap();
        parameters.put("accountId", accountId);
        parameters.put("market", "0");
        parameters.put("secuid", "");
        parameters.put("fundid", "");
        parameters.put("ordersno", "");
        parameters.put("count", "100");
        parameters.put("posstr", kcbpAccount.getBondNegotiate_SZ_PositionStr_Trade());
        parameters.put("begindate", beginDate);
        parameters.put("enddate", beginDate);

        TerminalAgentInfo agentInfo = terminalAgentInfoService.selectOne(getTerminalName(), FunctionCode.REQ_ACCOUNT_TRADE_REISSUE.value, "Negotiate_SZ");
        agentInfo.setAccountId(accountId);
        ResultBean resultBean = tradeAgentHelper.doRequest(agentInfo, parameters);
        if (resultBean.getErrorCode() == ErrorCode.SUCCESS.value) {
            List<Map<String, String>> resList = (List<Map<String, String>>) resultBean.getData();
            if (resList.size() > 1) {
                String newPositionStr = resList.get(resList.size() - 1).get("posstr");
                kcbpAccount.setBondNegotiate_SZ_PositionStr_Trade(newPositionStr);
                logger.info("queryBondNegotiate_SZ_Trade newPositionStr :{}", newPositionStr);
            }
            return KCBPMsgParseHelper.parseTrades(resList, accountId, entrustIdManager);
        }
        return new ArrayList<>();
    }

    /**
     * 411007	上海债券协议回购成交查询
     */
    private List<Trade> queryProtocolBack_SH_Trade0(KCBPAccount kcbpAccount) {
        String accountId = kcbpAccount.getAccountId();
        String beginDate = DateUtil.format(LocalDateTime.now(), "yyyyMMdd");
        Map<String, String> parameters = Maps.newHashMap();
        parameters.put("accountId", accountId);
        parameters.put("market", "1");
        parameters.put("secuid", "");
        parameters.put("fundid", "");
        parameters.put("stkcode", "");
        parameters.put("ordersno", "");
        parameters.put("begindate", beginDate);
        parameters.put("enddate", beginDate);
        parameters.put("count", "100");
//        parameters.put("posstr", "");
        parameters.put("posstr", kcbpAccount.getProtocolBack_SH_PositionStr_trade());

        TerminalAgentInfo agentInfo = terminalAgentInfoService.selectOne(getTerminalName(), FunctionCode.REQ_ACCOUNT_TRADE_REISSUE.value, "ProtocolBack_SH");
        agentInfo.setAccountId(accountId);
        ResultBean resultBean = tradeAgentHelper.doRequest(agentInfo, parameters);
        if (resultBean.getErrorCode() == ErrorCode.SUCCESS.value) {
            List<Map<String, String>> resList = (List<Map<String, String>>) resultBean.getData();
            if (resList.size() > 1) {
                String newPositionStr = resList.get(resList.size() - 1).get("posstr");
                kcbpAccount.setProtocolBack_SH_PositionStr_trade(newPositionStr);
                logger.info("queryIncrementProtocolBack_SH_Trade newPositionStr:{}", newPositionStr);
            }
            return KCBPMsgParseHelper.parseTrades(resList, accountId, entrustIdManager);
        }
        return new ArrayList<>();
    }

    /**
     * 411552 RTGS交收状态
     */
    private void queryRTGSStatus(KCBPAccount kcbpAccount) {
        String accountId = kcbpAccount.getAccountId();
        Map<String, String> parameters = Maps.newHashMap();
        parameters.put("accountId", accountId);
        parameters.put("market", "");
        parameters.put("secuid", "");
        parameters.put("stkcode", "");
        parameters.put("status", "");
        parameters.put("orderid", "");
        parameters.put("count", "100");
        parameters.put("poststr", kcbpAccount.getRTGS_PositionStr());
        TerminalAgentInfo agentInfo = terminalAgentInfoService.selectOne(getTerminalName(), 411552);
        agentInfo.setAccountId(accountId);
        try {
            ResultBean resultBean = tradeAgentHelper.doRequest(agentInfo, parameters);
            if (resultBean.getErrorCode() == ErrorCode.SUCCESS.value) {
                List<Map<String, String>> resList = (List<Map<String, String>>) resultBean.getData();
                List<RTGSStatusBean> rtgsStatusBeanList = KCBPMsgParseHelper.parseRTGS(accountId, resList);
                rtgsStatusBeanList.forEach(rtgsStatusBean -> {
                    String reportNo = rtgsStatusBean.getReportNo();
                    String reportNoByRTGS = entrustIdManager.getReportNoByRTGS(accountId, reportNo);
                    if (Strings.isNullOrEmpty(reportNoByRTGS)) {
                        onRtnRTGSStatus(rtgsStatusBean);
                        entrustIdManager.setReportNo(accountId, reportNo);
                    } else {
                        logger.info("当前订单已推送交收，{}", reportNo);
                    }
                });
                if (resList.size() > 0) {
                    String newPositionStr = resList.get(resList.size() - 1).get("poststr");
                    kcbpAccount.setRTGS_PositionStr(newPositionStr);
                    logger.info("查询RTGS交收信息， newPositionStr :{}", newPositionStr);
                }
            } else {
                logger.warn("KCBP RTGS交收接口调用失败，errorCode {} errorMsg {}", resultBean.getErrorCode(), resultBean.getErrorMsg());
            }
        } catch (Exception e) {
            logger.error("KCBP RTGS交收接口调用失败，errorCode {}，errorMsg {}", accountId, e.getMessage(), e);
        }

    }

    public void querySystemStatus() throws Exception {
        if (kcbpAccounts.size() > 0) {
            for (Map.Entry<String, KCBPAccount> entry : kcbpAccounts.entrySet()) {
                querySystemStatus(entry.getValue());
            }
        }
    }

    /**
     * 410232 查询系统状态
     */
    private void querySystemStatus(KCBPAccount kcbpAccount) throws Exception {
        String accountId = kcbpAccount.getAccountId();
        Map<String, String> parameters = Maps.newHashMap();
        parameters.put("accountId", accountId);

        KCBPAccount account = getKCBPAccount(accountId);
        if (null == account) {
            logger.error(("登录账户信息不存在：accountId={}"), accountId);
            return;
        }
        try {
            TerminalAgentInfo agentInfo = terminalAgentInfoService.selectOne(getTerminalName(), 410232);
            agentInfo.setAccountId(accountId);
            ResultBean resultBean = tradeAgentHelper.doRequest(agentInfo, parameters);
            int errorCode = resultBean.getErrorCode();
//            if (errorCode == ErrorCode.SUCCESS.value) {
//                setStatus(accountId, AccountLoginStatus.NOT_LOGGED);
//                tradeAgentManager.addAgentLoginStatus(accountId, false);
//                accountMap.remove(accountId);
//                List<String> accountIds = Lists.newArrayList(accountId);
//                tradeAgentManager.removeAccounts(accountIds);
//                accountManager.clearAccountMaps(accountIds);
//
//                logger.error("{}终端登录状态异常：{}，{}", account.getAgentType(), accountId, JsonUtils.getJsonFromObject(resultBean));
//
//                terminalHelper.connectAccount(accountId, 1);
//            } else
            if (errorCode == 2001) {
                logger.error("{}终端发送异常：{}，{}", account.getAgentType(), accountId, JsonUtils.getJsonFromObject(resultBean));
            } else if (errorCode == 2054) {
                logger.error("{}服务不可用：{}，{}", account.getAgentType(), accountId, JsonUtils.getJsonFromObject(resultBean));
            }
        } catch (Exception e) {
            logger.error(("查询终端登录状态失败：accountId={}"), accountId, e);
        }
    }

    private String getAgentMarket(String market) {
        if (Strings.isNullOrEmpty(market)) {
            return null;
        }
        return Market.SHSE.value.equals(market) ? "1" : "0";
    }

    private int getQuoteType(QuoteQryBean quoteQryBean, Map<String, String> parameters) {
        int quoteType = quoteQryBean.getQuoteType();
        if (quoteType == QuoteType.BULK_QUOTE.value) {
            String market = StockCodeUtils.getMarket(quoteQryBean.getStockCode());
            if (market.equals(Market.SZSE.value)) {
                //410995
                quoteQryBean.setMarket(Market.SZSE.value);
            } else if (market.equals(Market.SHSE.value)) {
                //410996
                quoteQryBean.setMarket(Market.SHSE.value);
            }
            parameters.put("stkcode", StockCodeUtils.getStockCode(quoteQryBean.getStockCode()));
            parameters.put("confermnum", "");
            parameters.put("bsflag", "");
            parameters.put("status", "");
        } else if (quoteType == QuoteType.NON_PUBLIC_PROTOCOL_BACK_QUOTE.value) {
            // 上海债券协议回购非公开报价行情 411027
            quoteQryBean.setMarket(Market.SHSE.value);

            parameters.put("ordtype", getOrdtype(quoteQryBean.getEntrustProp()));
            parameters.put("quoterefid", "");
            parameters.put("stkcode", "");
            parameters.put("qryrec", "100");
            parameters.put("posstr", "");
        } else if (quoteType == QuoteType.PUBLIC_PROTOCOL_BACK_QUOTE.value) {
            // 上交所质押式协议回购公开报价行情查询 411029
            quoteQryBean.setMarket(Market.SHSE.value);

            parameters.put("qryrec", "100");
            parameters.put("posstr", "");
        } else if (quoteType == QuoteType.FIXED_INCOME_QUOTE.value) {
            // 固收平台确定报价行情查询  460018
            quoteQryBean.setMarket(Market.SHSE.value);
            if (!Strings.isNullOrEmpty(quoteQryBean.getStockCode())) {
                parameters.put("stkcode", StockCodeUtils.getStockCode(quoteQryBean.getStockCode()));
            }
            parameters.put("market", "1");
            parameters.put("bsflag", "");
            parameters.put("count", "100");
            parameters.put("posstr", "");
        } else if (quoteType == QuoteType.AFTER_BULK_QUOTE.value) {
            String market = StockCodeUtils.getMarket(quoteQryBean.getStockCode());
            if (market.equals(Market.SZSE.value)) {
                quoteQryBean.setMarket(Market.SZSE.value);
            } else if (market.equals(Market.SHSE.value)) {
                //上海盘后固定价格交易行情查询 410556
                quoteQryBean.setMarket(Market.SHSE.value);
            }
            parameters.put("stkcode", StockCodeUtils.getStockCode(quoteQryBean.getStockCode()));
            parameters.put("count", "100");
            parameters.put("poststr", "");
        }
        return quoteType;
    }

    private String getIntegratedEntrustProp(IntegratedEntrust entrust, Map<String, String> parameters) {
        String entrustProp = null;
        if (IntegratedEntrustUtils.isBulk(entrust.getEntrustProp())) {
            // 410411	用于深交所综合协议平台报单，上交所大宗交易报单
            entrustProp = "Bulk";

            buildBulkParameters(entrust, parameters);
        } else if (StringUtils.equals(entrust.getMarket(), Market.SHSE.value)) {
            // 上交所固收平台业务
            if (IntegratedEntrustUtils.isFixedIncome_BS(entrust.getEntrustProp())) {
                // 固收平台委托买卖交易 460010
                entrustProp = "FixedIncomeBS";

                buildFixedIncomeBSParameters(entrust, parameters);
            } else if (IntegratedEntrustUtils.isFixedIncome_BaoJia(entrust.getEntrustProp())) {
                //固收平台确定报价报价申报 460013
                entrustProp = "FixedIncomeBaoJia";

                buildFixedIncomeBaoJiaParameters(entrust, parameters);
            } else if (IntegratedEntrustUtils.isFixedIncome_ChenJiao(entrust.getEntrustProp())) {
                //固收平台确定报价成交申报 460014
                entrustProp = "FixedIncomeChenJiao";

                buildFixedIncomeChenJiaoParameters(entrust, parameters);
            } else if (IntegratedEntrustUtils.isProtocolBack_SH_YiXiang(entrust.getEntrustProp())) {
                // 质押式协议回购
                //上海债券协议回购意向申报 411031
                entrustProp = "ProtocolBackSHYiXiang";

                buildProtocolBackSHYiXiangParameters(entrust, parameters);
            } else if (IntegratedEntrustUtils.isProtocolBack_SH_BaoJia(entrust.getEntrustProp())) {
                //上海债券协议回购报价申报 411032
                entrustProp = "ProtocolBack_SH_BaoJia";

                buildProtocolBack_SH_BaoJiaParameters(entrust, parameters);
            } else if (IntegratedEntrustUtils.isProtocolBack_SH_ChenJiao(entrust.getEntrustProp())) {
                //上海债券协议回购成交申报 411024
                entrustProp = "ProtocolBack_SH_ChenJiao";

                buildProtocolBack_SH_ChenJiaoParameters(entrust, parameters);
            }
        } else if (StringUtils.equals(entrust.getMarket(), Market.SZSE.value)) {
            //深证债券质押式协议回购
            // 深证债券协议回购委托申报 411000
            if (IntegratedEntrustUtils.isProtocolBack_SZ(entrust.getEntrustProp())) {
                entrustProp = "ProtocolBack_SZ";

                buildProtocolBack_SZParameters(entrust, parameters);
            }else if(IntegratedEntrustUtils.isNegotiateXD_SZ(entrust.getEntrustProp())) {
            	//新债 深圳 协商成交下单 410677
            	entrustProp = "Negotiate_XD_SZ";
            	buildNegotiate_XD_SZParameters(entrust, parameters);
            }else if(IntegratedEntrustUtils.isNegotiateHF_SZ(entrust.getEntrustProp())) {
            	//新债 深圳 协商成交回复 410678
            	entrustProp = "Negotiate_HF_SZ";
            	buildNegotiate_HF_SZParameters(entrust, parameters);
            }
        } else {
            logger.error("unknown market {}", entrust.getMarket());
        }
        if (Strings.isNullOrEmpty(entrustProp)) {
            logger.error("不支持当前委托属性，market={}，entrustProp={}", entrust.getMarket(), entrust.getEntrustProp());
            throw new RuntimeException("不支持当前委托属性，entrustProp：" + entrust.getEntrustProp());
        }
        return entrustProp;
    }
    
    /**
     * 新债 深圳 协商成交下单 410677
     * @param entrust
     * @param parameters
     */
    private void buildNegotiate_XD_SZParameters(IntegratedEntrust entrust, Map<String, String> parameters) {
        //报价所有者类型
        parameters.put("ownertype", entrust.getSellerNo());
        //委托价格
        parameters.put("orderprice", String.valueOf(entrust.getEntrustPrice()));
        //委托数量
        parameters.put("orderqty", String.valueOf(entrust.getEntrustVolume()));
        //本方交易员代码
        parameters.put("tradercode", entrust.getTradeId());
        //本方交易主体代码
        parameters.put("investorid", entrust.getInvestorId());
        //对手方交易商代码
        parameters.put("cpmemberid", entrust.getPropDealerId());
        //对手方交易员代码
        parameters.put("cptradercode", entrust.getPropTradeId());
        //对手方交易主体代码
        parameters.put("cpinvestorid", entrust.getPropInvestorId());
        //约定号
        parameters.put("confirmid", entrust.getContractId());
        //备注
        parameters.put("memo", "");
        //风险揭示签署流水号
        parameters.put("risksignsno", "");
    }
    
    /**
     * 新债 深圳 协商成交回复 410678
     * @param entrust
     * @param parameters
     */
    private void buildNegotiate_HF_SZParameters(IntegratedEntrust entrust, Map<String, String> parameters) {
    	//成交申报请求编号
        parameters.put("trdreportid", entrust.getSellerNo());//转发成交申报请求编号
        //报价所有者类型
        parameters.put("ownertype", entrust.getSellerNo());
        //委托价格
        parameters.put("orderprice", String.valueOf(entrust.getEntrustPrice()));
        //委托数量
        parameters.put("orderqty", String.valueOf(entrust.getEntrustVolume()));
        //本方交易员代码
        parameters.put("tradercode", entrust.getSellerNo());
        //本方交易主体代码
        parameters.put("investorid", entrust.getLiaisonTel());
        //对手方交易商代码
        parameters.put("cpmemberid", entrust.getPropSeatNo());
        //对手方交易员代码
        parameters.put("cptradercode", entrust.getLiaisonTel());
        //对手方交易主体代码
        parameters.put("cpinvestorid", entrust.getPropInvestorId());
        //对手方交易主体类型
        parameters.put("cpinvestortype", entrust.getPropInvestorId());
        //约定号
        parameters.put("confirmid", entrust.getContractId());
        //备注
        parameters.put("memo", "");
        //风险揭示签署流水号
        parameters.put("risksignsno", "");
    }

    private void buildProtocolBack_SZParameters(IntegratedEntrust entrust, Map<String, String> parameters) {
        //委托数量
        parameters.put("orderqty", String.valueOf(entrust.getEntrustVolume()));
        //深到期续做为续做利息
        if (EntrustProp.SZ_REPURCHASE_RENEWAL.value.equals(entrust.getEntrustProp())) {
            //利息按照回购金额*回购利率*占款天数÷365计算得到
            double orderamt = entrust.getEntrustBalance().doubleValue() * entrust.getEntrustPrice().doubleValue() * entrust.getCompactTerm() / 365.0000;
            parameters.put("orderamt", String.valueOf(orderamt));
        } else {
            //融资金额
            parameters.put("orderamt", String.valueOf(entrust.getEntrustBalance()));
        }
        //产品期限
        parameters.put("ghdays", String.valueOf(entrust.getCompactTerm()));
        //年利率
        parameters.put("duerate", String.valueOf(entrust.getEntrustPrice()));
        //对方交易单元
        parameters.put("targetseat", String.valueOf(entrust.getPropSeatNo()));
        //约定号
        parameters.put("confernum", String.valueOf(entrust.getContractId()));
        //订单所有者类型
        parameters.put("ownertype", "");

        String bsFlag = entrust.getBsFlag();
        String stockCode = entrust.getStockCode();
        String entrustBs = entrust.getEntrustBs();

        //质押证券代码	2C初始交易和2Q换券申报换入质押券时需填写
        parameters.put("stkcodein", "");
        //质押委托数量	2C初始交易和2Q换券申报换入质押券时需填写
        parameters.put("orderqtyin", "");
        //质押股份性质	2C初始交易和2Q换券申报换入质押券时需填写00:无限售流通股 01:首发后限售股
        parameters.put("stkpropin", "");
        if (StringUtils.equals(BSFlag.CSJYRRZJ.value, bsFlag)) {
            parameters.put("stkcodein", StockCodeUtils.getStockCode(stockCode));
            parameters.put("orderqtyin", String.valueOf(entrust.getEntrustVolume()));
            parameters.put("stkpropin", "00");
        } else if (StringUtils.equals(BSFlag.HQSB.value, bsFlag) && StringUtils.equals(EntrustBs.BUY.value, entrustBs)) {
            parameters.put("stkcodein", StockCodeUtils.getStockCode(stockCode));
            parameters.put("orderqtyin", String.valueOf(entrust.getEntrustVolume()));
            parameters.put("stkpropin", "00");
        }

        //解押证券代码	2Q换券申报换出质押券时需填写
        parameters.put("stkcodeout", "");
        //解押委托数量	2Q换券申报换出质押券时需填写
        parameters.put("orderqtyout", "");
        //解押股份性质	2C初始交易和2Q换券申报换入质押券时需填写00:无限售流通股 01:首发后限售股
        parameters.put("stkpropout", "");
        if (StringUtils.equals(BSFlag.HQSB.value, bsFlag) && StringUtils.equals(EntrustBs.SELL.value, entrustBs)) {
            parameters.put("stkcodeout", StockCodeUtils.getStockCode(stockCode));
            parameters.put("orderqtyout", String.valueOf(entrust.getEntrustVolume()));
            parameters.put("stkpropout", "00");
        }

        //到期续做类型	xzflag
        if (EntrustProp.SH_REPURCHASE_RENEWAL.value.equals(entrust.getEntrustProp())) {
            //到期续作类型 N:非第三方续做  Y:第三方续做
            if (PledgeeType.UN_THIRD_PARTY.value.equals(entrust.getTransCode())) {
                parameters.put("xzflag", "N");
            } else {
                parameters.put("xzflag", "Y");
            }
        }

        //交易员代码
        parameters.put("tradercode", entrust.getSellerNo());
        //交易主体
        parameters.put("investorid", "");
        //对手方交易商
        parameters.put("cpmemberid", entrust.getPropSeatNo());
        //对手方交易员
        parameters.put("cptradercode", entrust.getLiaisonTel());
        //对手方交易主体
        parameters.put("cpinvestorid", "");

        //初始合约号
        parameters.put("zqzysno", "");
        if (EntrustProp.SZ_REPURCHASE_RENEWAL.value.equals(entrust.getEntrustProp())
                || EntrustProp.SZ_REPURCHASE_BUYBACK.value.equals(entrust.getEntrustProp())) {
            parameters.put("zqzysno", entrust.getOrigSerialNo());
        }
        //本方客户姓名	2C初始交易和2K到期续做时可选填写
        parameters.put("custfullname", "");
        //风险揭示签署流水号
        parameters.put("risksignsno", "");
    }

    private void buildProtocolBack_SH_ChenJiaoParameters(IntegratedEntrust entrust, Map<String, String> parameters) {
        //委托数量
        parameters.put("orderqty", String.valueOf(entrust.getEntrustVolume()));
        if (EntrustProp.SH_REPURCHASE_RENEWAL_CONFIRM.value.equals(entrust.getEntrustProp())
                || EntrustProp.SH_REPURCHASE_RENEWAL_REFUSE.value.equals(entrust.getEntrustProp())) {
            //到期续作类型 N:非第三方续做  Y:第三方续做
            if (PledgeeType.UN_THIRD_PARTY.value.equals(entrust.getTransCode())) {
                parameters.put("orderRestrictions", "N");
            } else {
                parameters.put("orderRestrictions", "Y");
            }
        }
        //本方交易员代码
        parameters.put("companyoperid", entrust.getSellerNo());
        //对手方交易员代码
        parameters.put("taroperid", entrust.getLiaisonTel());
        //对手方交易商代码
        parameters.put("tarcompany", entrust.getPropSeatNo());
        //初始交易日期
        parameters.put("tradedate", entrust.getFirstSettleDate());
        //成交编号
        parameters.put("tradematchcode", entrust.getOrigSerialNo());
        //交易所请求编号
        parameters.put("quoterefid", entrust.getContractId());
        //报价申报类型，F:成交申报、 X:到期续做、 C:解除质押、 H:换券申报、 Z:提前终止
        parameters.put("ordtype", getOrdtype(entrust.getEntrustProp()));
        //质权人
        parameters.put("pledgee", entrust.getPortfolioId());
        //风险揭示签署流水号
        parameters.put("risksignsno", "");
    }

    private void buildProtocolBack_SH_BaoJiaParameters(IntegratedEntrust entrust, Map<String, String> parameters) {
        String bsFlag = entrust.getBsFlag();
        //委托数量 初始交易2C 换券申报2Q 填写
        if (StringUtils.equals(bsFlag, BSFlag.CSJYRRZJ.value) || StringUtils.equals(bsFlag, BSFlag.HQSB.value)) {
            parameters.put("orderqty", String.valueOf(entrust.getEntrustVolume()));
        }
        //融资金额 初始交易2C、到期续做2K 填写
        //回购天数 初始交易2C、到期续做2K 填写
        //年利率 初始交易2C、到期续做2K 需填写
        if (StringUtils.equals(bsFlag, BSFlag.CSJYRRZJ.value) || StringUtils.equals(bsFlag, BSFlag.DQXZ.value)) {
            parameters.put("orderamt", String.valueOf(entrust.getEntrustBalance()));
            parameters.put("ghdays", String.valueOf(entrust.getCompactTerm()));
            parameters.put("duerate", String.valueOf(entrust.getEntrustPrice()));
        }
        //年利率 提前终止融入资金2M  提前终止融出资金2N 需填写
        if (StringUtils.equals(bsFlag, BSFlag.TQZZRRZJ.value) || StringUtils.equals(bsFlag, BSFlag.TQZZRCZJ.value)) {
            parameters.put("duerate", String.valueOf(entrust.getEntrustPrice()));
        }
        if (EntrustProp.SH_REPURCHASE_RENEWAL.value.equals(entrust.getEntrustProp())) {
            //到期续作类型 N:非第三方续做  Y:第三方续做
            if (PledgeeType.UN_THIRD_PARTY.value.equals(entrust.getTransCode())) {
                parameters.put("orderRestrictions", "N");
            } else {
                parameters.put("orderRestrictions", "Y");
            }
        }
        //本方交易员代码
        parameters.put("companyoperid", entrust.getSellerNo());
        //对手方交易员代码
        parameters.put("taroperid", entrust.getLiaisonTel());
        //对手方交易商代码
        parameters.put("tarcompany", entrust.getPropSeatNo());
        //初始交易日期
        parameters.put("tradedate", entrust.getFirstSettleDate());
        //成交编号
        parameters.put("tradematchcode", "");
        if (!Strings.isNullOrEmpty(entrust.getOrigSerialNo())) {
            parameters.put("tradematchcode", entrust.getOrigSerialNo());
        }
        if (!Strings.isNullOrEmpty(entrust.getRemark())) {
            //补充条款
            parameters.put("remark", entrust.getRemark());
        }
        //风险揭示签署流水号
        parameters.put("risksignsno", "");
    }

    private void buildProtocolBackSHYiXiangParameters(IntegratedEntrust entrust, Map<String, String> parameters) {
        //委托数量
        parameters.put("orderqty", String.valueOf(entrust.getEntrustVolume()));
        //融资金额
        parameters.put("orderamt", String.valueOf(entrust.getEntrustBalance()));
        //回购天数
        parameters.put("ghdays", String.valueOf(entrust.getCompactTerm()));
        //年利率
        parameters.put("duerate", String.valueOf(entrust.getEntrustPrice()));
        ///交易日期
        parameters.put("begindate", entrust.getFirstSettleDate());
        //交易员代码
        parameters.put("companyoperid", entrust.getSellerNo());
        if (!Strings.isNullOrEmpty(entrust.getRemark())) {
            //补充条款
            parameters.put("remark", entrust.getRemark());
        }
        //风险揭示签署流水号
        parameters.put("risksignsno", "");
    }

    private void buildFixedIncomeChenJiaoParameters(IntegratedEntrust entrust, Map<String, String> parameters) {
        // 委托价格
        parameters.put("orderprice", String.valueOf(entrust.getEntrustPrice()));
        // 委托数量
        parameters.put("orderqty", String.valueOf(entrust.getEntrustVolume()));
        // 字段借用  点击成交时必填
        if (!Strings.isNullOrEmpty(entrust.getContractId())) {
            //交易所订单编号
            parameters.put("quoterefid", entrust.getContractId());
        }
        //交易员代码
        parameters.put("traderid", entrust.getLiaisonTel());
        if (!Strings.isNullOrEmpty(entrust.getPropSeatNo())) {
            //对手方交易商代码
            parameters.put("targetcompany", entrust.getPropSeatNo());
        }
        //风险揭示签署流水号，调用411017获取
        parameters.put("risksignsno", "");
    }

    private void buildFixedIncomeBaoJiaParameters(IntegratedEntrust entrust, Map<String, String> parameters) {
        if (EntrustBs.BUY.value.equals(entrust.getEntrustBs())) {
            // 委托买入价格
            parameters.put("orderbuyprice", String.valueOf(entrust.getEntrustPrice()));
            // 委托买入数量
            parameters.put("orderbuyqty", String.valueOf(entrust.getEntrustVolume()));
        } else {
            //委托卖出价格
            parameters.put("ordersaleprice", String.valueOf(entrust.getEntrustPrice()));
            //委托卖出数量
            parameters.put("ordersaleqty", String.valueOf(entrust.getEntrustVolume()));
        }
        //冰山订单数量
        parameters.put("displayqty", String.valueOf(entrust.getDiscount()));
        //匿名报价， 1:匿名  0:具名
        parameters.put("trdanonymous", "1");
        //做市报价， 5:做市报价  0:非做市报价
        parameters.put("orderrestric", "0");
        //交易员代码
        parameters.put("traderid", entrust.getLiaisonTel());
        //风险揭示签署流水号，调用411017获取
        parameters.put("risksignsno", "");
    }

    private void buildFixedIncomeBSParameters(IntegratedEntrust entrust, Map<String, String> parameters) {
        // 价格
        parameters.put("price", String.valueOf(entrust.getEntrustPrice()));
        // 数量
        parameters.put("qty", String.valueOf(entrust.getEntrustVolume()));
        //约定号
        parameters.put("confernum", String.valueOf(entrust.getContractId()));
        // 对手方交易员 2X,2Y 业务必填
        parameters.put("targettrader", entrust.getLiaisonName());
        //对手方交易商
        parameters.put("targetcompany", entrust.getPropSeatNo());
        //发起方交易员
        parameters.put("traderid", entrust.getLiaisonTel());
        //录入类型： 协议交易业务4J,4K 时必须输入，1 客户录入  2 交易商录入
        parameters.put("accounttype", "2");
        //补充协议
        parameters.put("supplemental", "");
        //风险揭示签署流水号
        parameters.put("risksignsno", "");
    }

    private void buildBulkParameters(IntegratedEntrust entrust, Map<String, String> parameters) {
        // 深证成交申报为 配对买卖 点击成交申报为 确认买卖
        if (EntrustProp.CONFIRM.value.equals(entrust.getEntrustProp())
                && Market.SZSE.value.equals(StockCodeUtils.getMarket(entrust.getStockCode()))) {
            if (EntrustBs.BUY.value.equals(entrust.getEntrustBs())) {
                parameters.put("bsflag", BSFlag.PDMR.value);
            } else {
                parameters.put("bsflag", BSFlag.PDMC.value);
            }
        } else if (EntrustProp.MUTUAL_TRADE.value.equals(entrust.getEntrustProp())
                && Market.SZSE.value.equals(StockCodeUtils.getMarket(entrust.getStockCode()))) {
            if (EntrustBs.BUY.value.equals(entrust.getEntrustBs())) {
                parameters.put("bsflag", BSFlag.QRMR.value);
            } else {
                parameters.put("bsflag", BSFlag.QRMC.value);
            }
        }
        parameters.put("price", String.valueOf(entrust.getEntrustPrice()));
        parameters.put("qty", String.valueOf(entrust.getEntrustVolume()));
        parameters.put("bankcode", "");
        parameters.put("remark", "");
        //关联市场
        parameters.put("linkmarket", "");
        parameters.put("ordergroup", MoreObjects.firstNonNull(entrust.getLocalEntrustId(), "0"));
        if (!Strings.isNullOrEmpty(entrust.getContractId())) {
            //综合协议约定号
            parameters.put("promiseno", entrust.getContractId());
        }
        if (!Strings.isNullOrEmpty(entrust.getPropSeatNo())) {
            //对方席位
            parameters.put("targetseat", entrust.getPropSeatNo());
        }
        if (!Strings.isNullOrEmpty(entrust.getLiaisonName())) {
            //联系人
            parameters.put("linkman", entrust.getLiaisonName());
        }
        if (!Strings.isNullOrEmpty(entrust.getLiaisonTel())) {
            //联系方式
            parameters.put("linkway", entrust.getLiaisonTel());
        }
        if (EntrustProp.LARGE_AFTER_CLOSING_PRICE.value.equals(entrust.getEntrustProp())) {
            parameters.put("pricetype", "1");
        } else if (EntrustProp.LARGE_AFTER_CLOSING_WEIGHT_PRICE.value.equals(entrust.getEntrustProp())) {
            parameters.put("pricetype", "2");
        }
    }

    /**
     * 获取报价申报类型
     */
    private String getOrdtype(String entrustProp) {
        if (EntrustProp.SH_REPURCHASE_TRADE_CONFIRM.value.equals(entrustProp)
                || EntrustProp.SH_REPURCHASE_TRADE_REFUSE.value.equals(entrustProp)
                || EntrustProp.SH_REPURCHASE_TRADE.value.equals(entrustProp)) {
            //成交申报
            return "F";
        } else if (EntrustProp.SH_REPURCHASE_RENEWAL_CONFIRM.value.equals(entrustProp)
                || EntrustProp.SH_REPURCHASE_RENEWAL_REFUSE.value.equals(entrustProp)
                || EntrustProp.SH_REPURCHASE_RENEWAL.value.equals(entrustProp)) {
            //到期续做
            return "X";
        } else if (EntrustProp.SH_REPURCHASE_RELEASE_CONFIRM.value.equals(entrustProp)
                || EntrustProp.SH_REPURCHASE_RELEASE_REFUSE.value.equals(entrustProp)
                || EntrustProp.SH_REPURCHASE_RELEASE.value.equals(entrustProp)) {
            //解除质押
            return "C";
        } else if (EntrustProp.SH_REPURCHASE_EXCHANGE_CONFIRM.value.equals(entrustProp)
                || EntrustProp.SH_REPURCHASE_EXCHANGE_REFUSE.value.equals(entrustProp)
                || EntrustProp.SH_REPURCHASE_EXCHANGE.value.equals(entrustProp)) {
            //换券申报
            return "H";
        } else if (EntrustProp.SH_REPURCHASE_TERMINATE_CONFIRM.value.equals(entrustProp)
                || EntrustProp.SH_REPURCHASE_TERMINATE_REFUSE.value.equals(entrustProp)
                || EntrustProp.SH_REPURCHASE_TERMINATE.value.equals(entrustProp)) {
            //提前终止
            return "Z";
        }
        return "";
    }


    private void queryNextTrade1() {
        if (CollectionUtils.isEmpty(poolExecutors)) {
            for (int i = 0; i < kcbpTradeCount; i++) {
                poolExecutors.add(new ThreadPoolExecutor(10, 1, 0, TimeUnit.MILLISECONDS,
                        new LinkedBlockingDeque<>(100), new ThreadPoolExecutor.DiscardPolicy()));
            }
        }
        for (Map.Entry<String, KCBPAccount> entry : kcbpAccounts.entrySet()) {
            KCBPAccount kcbpAccount = entry.getValue();
            int index = (kcbpAccount.getAccountId().hashCode() & Integer.MAX_VALUE) % kcbpTradeCount;
//            if (getStatus(kcbpAccount.getAccountId()) == AccountLoginStatus.LOGGED.value) {
            poolExecutors.get(index).submit(() -> {
                //查询当日成交明细查询
                queryIncrementTrade(kcbpAccount);
                //查询固收平台交易成交查询
                queryFixedIncomeTrade(kcbpAccount);
                //深圳债券协议回购成交查询
                queryProtocolBack_SZ_Trade(kcbpAccount);
                //上海债券协议回购成交查询
                queryProtocolBack_SH_Trade(kcbpAccount);

//                    queryRTGSStatus(kcbpAccount);

                //当日委托明细查询
                queryIncrementEntrust(kcbpAccount);
                //固收平台交易委托明细查询
                queryFixedIncomeEntrust(kcbpAccount);
                //上海债券协议回购委托流水查询
                queryProtocolBack_SH_Entrust(kcbpAccount);
                //深圳债券协议回购委托流水查询
                queryProtocolBack_SZ_Entrust(kcbpAccount);
            });
//            }
        }
    }

	@Override
	public List<NegotiateQuoteReceiveBean> reqNegotiateQuoteReceive(NegotiateQuoteQueryBean negotiateQuoteQueryBean)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
