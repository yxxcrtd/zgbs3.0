package com.joyintech.tams.exchange.service.trade.server.agent.jsd;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.joyintech.tams.exchange.common.bean.AccountFinance;
import com.joyintech.tams.exchange.common.bean.AccountLoginReqBean;
import com.joyintech.tams.exchange.common.bean.EnableQryBean;
import com.joyintech.tams.exchange.common.bean.HistoryQryBean;
import com.joyintech.tams.exchange.common.bean.HistoryTrade;
import com.joyintech.tams.exchange.common.bean.InstrumentQryBean;
import com.joyintech.tams.exchange.common.bean.IntegratedBulkCloseQuoteBean;
import com.joyintech.tams.exchange.common.bean.IntegratedBulkCloseQuoteQryBean;
import com.joyintech.tams.exchange.common.bean.IntegratedBulkQuoteBean;
import com.joyintech.tams.exchange.common.bean.IntegratedBulkQuoteQryBean;
import com.joyintech.tams.exchange.common.bean.IntegratedFixedIncomeQuoteBean;
import com.joyintech.tams.exchange.common.bean.IntegratedFixedIncomeQuoteQryBean;
import com.joyintech.tams.exchange.common.bean.IntegratedQuote;
import com.joyintech.tams.exchange.common.bean.IntegratedRepurchaseQuoteBean;
import com.joyintech.tams.exchange.common.bean.IntegratedRepurchaseQuoteQryBean;
import com.joyintech.tams.exchange.common.bean.NegotiateQuoteQueryBean;
import com.joyintech.tams.exchange.common.bean.NegotiateQuoteReceiveBean;
import com.joyintech.tams.exchange.common.bean.PledgeDiscount;
import com.joyintech.tams.exchange.common.bean.PositionQueryBean;
import com.joyintech.tams.exchange.common.bean.QuoteQryBean;
import com.joyintech.tams.exchange.common.bean.ResultBean;
import com.joyintech.tams.exchange.common.bean.StandardBonds;
import com.joyintech.tams.exchange.common.bean.TerminalId;
import com.joyintech.tams.exchange.common.bean.bond.StandardBondQryBean;
import com.joyintech.tams.exchange.common.constant.AgentType;
import com.joyintech.tams.exchange.common.constant.EntrustBs;
import com.joyintech.tams.exchange.common.constant.EntrustChildBusiness;
import com.joyintech.tams.exchange.common.constant.EntrustProp;
import com.joyintech.tams.exchange.common.constant.EntrustStatus;
import com.joyintech.tams.exchange.common.constant.ErrorCode;
import com.joyintech.tams.exchange.common.constant.Market;
import com.joyintech.tams.exchange.common.constant.QuoteType;
import com.joyintech.tams.exchange.common.db.domain.Account;
import com.joyintech.tams.exchange.common.db.domain.CancelEntrustBean;
import com.joyintech.tams.exchange.common.db.domain.Entrust;
import com.joyintech.tams.exchange.common.db.domain.EntrustInterface;
import com.joyintech.tams.exchange.common.db.domain.Instrument;
import com.joyintech.tams.exchange.common.db.domain.IntegratedEntrust;
import com.joyintech.tams.exchange.common.db.domain.PositionBean;
import com.joyintech.tams.exchange.common.db.domain.PositionStr;
import com.joyintech.tams.exchange.common.db.domain.Trade;
import com.joyintech.tams.exchange.common.net.protocol.FunctionCode;
import com.joyintech.tams.exchange.common.utils.CbptransTypeUtils;
import com.joyintech.tams.exchange.common.utils.EntrustUtils;
import com.joyintech.tams.exchange.common.utils.JsonUtils;
import com.joyintech.tams.exchange.common.utils.StockCodeUtils;
import com.joyintech.tams.exchange.service.trade.dao.domain.TerminalAgentInfo;
import com.joyintech.tams.exchange.common.constant.AccountLoginStatus;
import com.joyintech.tams.exchange.service.trade.server.agent.TradeAgentDsp;
import com.joyintech.tams.exchange.service.trade.server.agent.jsd.resp.header.RespCancelEntrustHeader;
import com.joyintech.tams.exchange.service.trade.server.agent.jsd.resp.header.RespEntrustHeader;
import com.joyintech.tams.exchange.service.trade.server.agent.jsd.resp.header.RespEtfEntrustHeader;
import com.joyintech.tams.exchange.service.trade.server.agent.jsd.resp.header.RespIntegratedEntrustHeader;
import com.joyintech.tams.exchange.service.trade.server.agent.jsd.resp.header.RespQryEnableHeader;

/**
 * @author lifangpeng
 * @date 2021/11/9
 */
@Component("jsd")
public class JsdTradeAgent extends TradeAgentDsp {
    private static final Logger logger = LoggerFactory.getLogger(JsdTradeAgent.class);

    private static final Splitter SPLITTER = Splitter.on("|");
    private volatile Map<String, PositionStr> positionStrMap = Maps.newConcurrentMap();

    @PostConstruct
    public void init() {
        setTerminalName(AgentType.JSD.value);
    }

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
            parameters.put("password", account.getPwd());

            ResultBean resultBean = tradeAgentHelper.doRequest(agentInfo, parameters);
            // 账户登录成功，记录下登录成功的账户
            if (resultBean.getErrorCode() == ErrorCode.SUCCESS.value) {
                String data = JsonUtils.getJsonFromObject(resultBean.getData());
                logger.info("金仕达用户登录信息：{}", data);

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
     * 委托查询-14
     */
    @Override
    public List<Entrust> reqEntrusts(String accountId) throws Exception {
        TerminalAgentInfo agentInfo = terminalAgentInfoService.selectOne(getTerminalName(), FunctionCode.REQ_QRY_ENTRUST.value);
        agentInfo.setAccountId(accountId);
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put("accountId", accountId);
        //0-市场代码、1-委托时间、2-委托时间倒序
        parameters.put("orderType", '1');
        parameters.put("market", ' ');

        ResultBean resultBean = tradeAgentHelper.doRequest(agentInfo, parameters);
        if (resultBean.getErrorCode() != ErrorCode.SUCCESS.value) {
            return Lists.newArrayList();
        }

        return JsdMsgParseHelper.parseEntrusts(resultBean, getAccount(accountId), entrustIdManager);
    }

    @Override
    public List<IntegratedEntrust> reqIntegratedEntrusts(String accountId) throws Exception {
        return null;
    }

    /**
     * 成交查询-122
     */
    @Override
    public List<Trade> reqTrades(String accountId) throws Exception {
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put("accountId", accountId);
        parameters.put("market", ' ');
        parameters.put("searchDirection", '1');
        parameters.put("queryMode", '0');
        parameters.put("queryTYpe", '1');

        TerminalAgentInfo agentInfo = terminalAgentInfoService.selectOne(getTerminalName(), FunctionCode.REQ_QRY_TRADE.value);
        agentInfo.setAccountId(accountId);
        ResultBean resultBean = tradeAgentHelper.doRequest(agentInfo, parameters);
        if (resultBean.getErrorCode() != ErrorCode.SUCCESS.value) {
            return Lists.newArrayList();
        }

        return JsdMsgParseHelper.parseTrades(resultBean, getAccount(accountId));
    }

    /**
     * 委托下单-3
     * ETF申赎-202
     */
    @Override
    public void entrust(Entrust entrust) {
        logger.info("send entrust:{}", entrust.getId());
        String entrustProp = entrust.getEntrustProp();
        TerminalAgentInfo agentInfo = terminalAgentInfoService.selectOne(getTerminalName(), FunctionCode.REQ_ENTRUST.value, entrustProp);
        agentInfo.setAccountId(entrust.getAccountId());
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put("accountId", entrust.getAccountId());
        parameters.put("entrustProp", entrustProp);
        parameters.put("entrustBs", entrust.getEntrustBs());
        parameters.put("bsflag", entrust.getBsFlag());
        parameters.put("market", getAgentMarket(entrust.getMarket()));
        parameters.put("entrustCode", StockCodeUtils.getStockCode(entrust.getStockCode()));
        //ETF申赎
        if (EntrustProp.ETF_APPLY.value.equals(entrustProp)) {
            etfEntrust(entrust, parameters, agentInfo);
            return;
        }

        String direction = JSDConverter.getDirection(entrustProp, entrust.getEntrustBs(), entrust.getMarket());
        if (!"".equals(direction)) {
            parameters.put("direction", direction);
        }
        if (EntrustUtils.isSHZQ(entrust.getMarket(), entrust.getAssetType(), entrust.getStockCode())) {
            parameters.put("volume", entrust.getEntrustVolume() / 10);
        } else {
            parameters.put("volume", entrust.getEntrustVolume());
        }
        parameters.put("price", entrust.getEntrustPrice());
        parameters.put("entrustFlag", '0');
        try {
            ResultBean resultBean = tradeAgentHelper.doRequest(agentInfo, parameters);
            if (resultBean.getErrorCode() == ErrorCode.SUCCESS.value) {
                RespEntrustHeader dataBody = (RespEntrustHeader) resultBean.getData();
                if (null != dataBody) {
                    entrust.setBrokerEntrustId(String.valueOf(dataBody.getEntrustBatch()));
                    entrustIdManager.setBrokeEntrustIdMapping(entrust.getAccountId(), entrust.getBrokerEntrustId(), entrust.getId());
                } else {
                    entrust.setEntrustStatus(EntrustStatus.INVALID.value);
                    entrust.setErrorCode(ErrorCode.TERMINAL_API_ERROR.value);
                    entrust.setErrorMsg(resultBean.getErrorMsg());
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

    /***
     * ETF申赎-202
     */
    private void etfEntrust(Entrust entrust, Map<String, Object> parameters, TerminalAgentInfo agentInfo) {
        if (EntrustBs.BUY.value.equals(entrust.getEntrustBs())) {
            parameters.put("entrustBs", '1');
        } else if (EntrustBs.SELL.value.equals(entrust.getEntrustBs())) {
            parameters.put("entrustBs", '2');
        }
        parameters.put("volume", entrust.getEntrustVolume());
        try {
            ResultBean resultBean = tradeAgentHelper.doRequest(agentInfo, parameters);
            if (resultBean.getErrorCode() == ErrorCode.SUCCESS.value) {
                RespEtfEntrustHeader dataBody = (RespEtfEntrustHeader) resultBean.getData();
                if (null != dataBody) {
                    entrust.setBrokerEntrustId(dataBody.entrustId);
                    entrustIdManager.setBrokeEntrustIdMapping(entrust.getAccountId(), entrust.getBrokerEntrustId(), entrust.getId());
                } else {
                    entrust.setEntrustStatus(EntrustStatus.INVALID.value);
                    entrust.setErrorCode(ErrorCode.TERMINAL_API_ERROR.value);
                    entrust.setErrorMsg(resultBean.getErrorMsg());
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
     * 大宗交易委托下单-340
     */
    @Override
    public void integratedEntrust(IntegratedEntrust entrust) {
        logger.info("send integratedEntrust:{}", entrust.getId());
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put("accountId", entrust.getAccountId());
        parameters.put("entrustProp", entrust.getEntrustProp());
        parameters.put("entrustBs", entrust.getEntrustBs());
        parameters.put("bsflag", entrust.getBsFlag());
        parameters.put("market", getAgentMarket(entrust.getMarket()));
        parameters.put("entrustCode", StockCodeUtils.getStockCode(entrust.getStockCode()));
        String direction = JSDConverter.getDirection(entrust.getEntrustProp(), entrust.getEntrustBs(), entrust.getMarket());
        if (!"".equals(direction)) {
            parameters.put("direction", direction);
        }
        if (EntrustUtils.isSHZQ(entrust.getMarket(), entrust.getAssetType(), entrust.getStockCode())) {
            parameters.put("volume", (int) (entrust.getEntrustVolume() / 10));
        } else {
            parameters.put("volume", (int) entrust.getEntrustVolume());
        }
        parameters.put("price", entrust.getEntrustPrice());
        parameters.put("entrustSubBusiness", JSDConverter.getEntrustSubBusiness(entrust.getEntrustProp()));
        String entrustSubBusiness = JSDConverter.getEntrustSubBusiness(entrust.getEntrustProp());
        if (EntrustChildBusiness.CONFIRM.value.equals(entrustSubBusiness)) {
            parameters.put("appointNum", entrust.getContractId());
            parameters.put("dfjyUnit", entrust.getPropSeatNo());
        }
        parameters.put("entrustFlag", '0');
        if (EntrustChildBusiness.INTENTION.value.equals(entrustSubBusiness)) {
            parameters.put("relationName", entrust.getLiaisonName());
            parameters.put("relationTel", entrust.getLiaisonTel());
        }
        if (EntrustChildBusiness.SH_FIXEDINCOME_BAOJIA_OPPOSITE.value.equals(entrustSubBusiness)) {
            parameters.put("appointNum", entrust.getContractId());
            parameters.put("ourTrader", entrust.getLiaisonTel());
            parameters.put("counterpartyTrader", entrust.getLiaisonName());
        }
        logger.info("报价参数parameters：{}", parameters);

        TerminalAgentInfo agentInfo = terminalAgentInfoService.selectOne(getTerminalName(), FunctionCode.REQ_INTEGRATED_ENTRUST.value);
        agentInfo.setAccountId(entrust.getAccountId());
        try {
            ResultBean resultBean = tradeAgentHelper.doRequest(agentInfo, parameters);
            entrust.setEntrustStatus(EntrustStatus.TOBE_REPORTED.value);
            if (resultBean.getErrorCode() == ErrorCode.SUCCESS.value) {
                RespIntegratedEntrustHeader dataBody = (RespIntegratedEntrustHeader) resultBean.getData();
                if (null != dataBody) {
                    entrust.setBrokerEntrustId(String.valueOf(dataBody.entrustBatch));
                    entrustIdManager.setBrokeEntrustIdMapping(entrust.getAccountId(), entrust.getBrokerEntrustId(), entrust.getId());
                } else {
                    entrust.setEntrustStatus(EntrustStatus.INVALID.value);
                    entrust.setErrorCode(ErrorCode.TERMINAL_API_ERROR.value);
                    entrust.setErrorMsg(resultBean.getErrorMsg());
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
     * 委托撤单-4
     */
    @Override
    public void cancelEntrust(Entrust entrust, EntrustInterface cancelEntrust) {
        CancelEntrustBean cancelEntrustBean = getCancelEntrustBean(entrust, cancelEntrust);
        cancelEntrustBean.setAgentType(AgentType.JSD.value);

        Map<String, String> parameters = Maps.newHashMap();
        parameters.put("accountId", cancelEntrustBean.getAccountId());
        parameters.put("market", getAgentMarket(entrust.getMarket()));
        parameters.put("contractId", cancelEntrustBean.getBrokerEntrustId());

        TerminalAgentInfo agentInfo = terminalAgentInfoService.selectOne(getTerminalName(), FunctionCode.REQ_CANCEL_ENTRUST.value);
        agentInfo.setAccountId(entrust.getAccountId());
        try {
            ResultBean resultBean = tradeAgentHelper.doRequest(agentInfo, parameters);
            if (resultBean.getErrorCode() == ErrorCode.SUCCESS.value) {
                RespCancelEntrustHeader dataBody = (RespCancelEntrustHeader) resultBean.getData();
                if (null != dataBody) {
                    cancelEntrustBean.setCancelBrokerEntrustId(cancelEntrustBean.getCancelId());
                } else {
                    cancelEntrustBean.setEntrustStatus(EntrustStatus.INVALID.value);
                    cancelEntrustBean.setErrorCode(ErrorCode.TERMINAL_API_ERROR.value);
                    cancelEntrustBean.setErrorMsg(resultBean.getErrorMsg());
                }
            } else {
                cancelEntrustBean.setEntrustStatus(EntrustStatus.INVALID.value);
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
     * 大宗交易委托下单-341
     */
    @Override
    public void cancelIntegratedEntrust(IntegratedEntrust integratedEntrust, IntegratedEntrust cancelEntrust) {
        CancelEntrustBean cancelEntrustBean = getCancelEntrustBean(integratedEntrust, cancelEntrust);
        cancelEntrustBean.setAgentType(AgentType.JSD.value);

        Map<String, String> parameters = Maps.newHashMap();
        parameters.put("accountId", cancelEntrustBean.getAccountId());
        parameters.put("market", getAgentMarket(cancelEntrustBean.getMarket()));
        parameters.put("contractId", cancelEntrustBean.getBrokerEntrustId());

        TerminalAgentInfo agentInfo = terminalAgentInfoService.selectOne(getTerminalName(), FunctionCode.REQ_CANCEL_INTEGRATED_ENTRUST.value);
        agentInfo.setAccountId(cancelEntrustBean.getAccountId());
        try {
            ResultBean resultBean = tradeAgentHelper.doRequest(agentInfo, parameters);
            if (resultBean.getErrorCode() == ErrorCode.SUCCESS.value) {
                RespCancelEntrustHeader dataBody = (RespCancelEntrustHeader) resultBean.getData();
                if (null != dataBody) {
                    cancelEntrustBean.setCancelBrokerEntrustId(cancelEntrustBean.getCancelId());
                } else {
                    cancelEntrustBean.setEntrustStatus(EntrustStatus.INVALID.value);
                    cancelEntrustBean.setErrorCode(ErrorCode.TERMINAL_API_ERROR.value);
                    cancelEntrustBean.setErrorMsg(resultBean.getErrorMsg());
                }
            } else {
                cancelEntrustBean.setEntrustStatus(EntrustStatus.INVALID.value);
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
     * 大宗交易行情查询
     * 综合行情查询-345
     * 盘后大宗交易行情查询-346
     */
    @Override
    public List<IntegratedQuote> reqIntegratedQuotes(QuoteQryBean quoteQryBean) throws Exception {
        int quoteType = quoteQryBean.getQuoteType();
        if (QuoteType.BULK_QUOTE.value == quoteType) {
            Map<String, Object> parameters = Maps.newHashMap();
            parameters.put("accountId", quoteQryBean.getAccountId());
            parameters.put("market", getAgentMarket(quoteQryBean.getMarket()));
            if (StringUtils.isNotBlank(quoteQryBean.getStockCode())) {
                parameters.put("stockCode", StockCodeUtils.getStockCode(quoteQryBean.getStockCode()));
            }
            parameters.put("contractNo", quoteQryBean.getContractId());
            parameters.put("recordStatus", '1');

            TerminalAgentInfo agentInfo = terminalAgentInfoService.selectOne(getTerminalName(), FunctionCode.REQ_QRY_INTEGRATED_QUOTE.value);
            agentInfo.setAccountId(quoteQryBean.getAccountId());
            ResultBean resultBean = tradeAgentHelper.doRequest(agentInfo, parameters);
            if (resultBean.getErrorCode() != ErrorCode.SUCCESS.value) {
                return Lists.newArrayList();
            }

            return JsdMsgParseHelper.parseIntegratedQuotes(resultBean, quoteQryBean);
        } else if (QuoteType.AFTER_BULK_QUOTE.value == quoteType) {
            String accountId = quoteQryBean.getAccountId();
            String stockCode = quoteQryBean.getStockCode();
            Map<String, String> parameters = Maps.newHashMap();
            parameters.put("accountId", accountId);
            if (StringUtils.isNotBlank(stockCode)) {
                parameters.put("stockCode", StockCodeUtils.getStockCode(stockCode));
                parameters.put("market", getAgentMarket(StockCodeUtils.getMarket(stockCode)));
            }

            TerminalAgentInfo agentInfo = terminalAgentInfoService.selectOne(getTerminalName(), FunctionCode.REQ_QRY_INTEGRATED_BULK_CLOSE_QUOTE.value);
            agentInfo.setAccountId(accountId);
            ResultBean resultBean = tradeAgentHelper.doRequest(agentInfo, parameters);
            if (resultBean.getErrorCode() != ErrorCode.SUCCESS.value) {
                return Lists.newArrayList();
            }
            return JsdMsgParseHelper.parseAfterIntegratedQuotes(resultBean, quoteQryBean);
        } else {
            logger.info("没有相关接口，quoteType={}", quoteType);
            return Collections.emptyList();
        }
    }

    /**
     * 最大交易数量-17
     */
    @Override
    public int reqEnable(EnableQryBean qryBean) throws Exception {
        String[] marketAndStockCode = StockCodeUtils.getMarketAndStockCode(qryBean.getStockCode());
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put("accountId", qryBean.getAccountId());
        parameters.put("market", getAgentMarket(marketAndStockCode[1]));
        parameters.put("stockCode", marketAndStockCode[0]);
        parameters.put("price", qryBean.getEntrustPrice());
        String direction = JSDConverter.getDirection(qryBean.getEntrustProp(), qryBean.getEntrustBs(), marketAndStockCode[1]);
        if (!"".equals(direction)) {
            parameters.put("direction", direction);
        }

        TerminalAgentInfo agentInfo = terminalAgentInfoService.selectOne(getTerminalName(), FunctionCode.REQ_QRY_TRADE_ENABLE.value);
        agentInfo.setAccountId(qryBean.getAccountId());
        ResultBean resultBean = tradeAgentHelper.doRequest(agentInfo, parameters);
        if (resultBean.getErrorCode() != ErrorCode.SUCCESS.value) {
            return -1;
        }
        RespQryEnableHeader dataBody = (RespQryEnableHeader) resultBean.getData();
        if (null == dataBody) {
            return -1;
        }

        double maxNum = dataBody.maxNum;
        if (EntrustUtils.isSHZQ(marketAndStockCode[1], null, qryBean.getStockCode())) {
            maxNum = maxNum * 10;
        }

        return new Double(maxNum).intValue();
    }

    /**
     * 成交历史查询-62
     */
    @Override
    public List<HistoryTrade> reqHistoryTrade(HistoryQryBean historyQryBean) throws Exception {
        String accountId = historyQryBean.getAccountId();
        Map<String, String> parameters = Maps.newHashMap();
        parameters.put("accountId", accountId);
        parameters.put("currencyCode", "1");
        parameters.put("startDate", String.valueOf(historyQryBean.getStartDay()));
        parameters.put("endDate", String.valueOf(historyQryBean.getEndDay()));
        parameters.put("maxNum", String.valueOf(historyQryBean.getPageSize()));
        parameters.put("serialNumStr", historyQryBean.getPostStr());

        TerminalAgentInfo agentInfo = terminalAgentInfoService.selectOne(getTerminalName(), FunctionCode.REQ_QRY_HISTORY_TRADE.value);
        agentInfo.setAccountId(accountId);
        ResultBean resultBean = tradeAgentHelper.doRequest(agentInfo, parameters);
        if (resultBean.getErrorCode() != ErrorCode.SUCCESS.value) {
            return Lists.newArrayList();
        }

        return JsdMsgParseHelper.parseHistoryTrade(getAccount(accountId), resultBean, historyQryBean);
    }

    /**
     * 查询帐号详情-9
     */
    @Override
    public List<AccountFinance> reqCustomerFinance(String accountId) throws Exception {
        TerminalAgentInfo agentInfo = terminalAgentInfoService.selectOne(getTerminalName(), FunctionCode.REQ_QRY_FINANCE.value);
        agentInfo.setAccountId(accountId);
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put("accountId", accountId);
        parameters.put("currencyCode", '1');
        parameters.put("financingBalanceTag", '0');
        ResultBean resultBean = tradeAgentHelper.doRequest(agentInfo, parameters);
        if (resultBean.getErrorCode() != ErrorCode.SUCCESS.value) {
            return Lists.newArrayList();
        }
        return JsdMsgParseHelper.parseCustomerFinance(resultBean, accountId);
    }

    /**
     * 查询持仓详情-10
     */
    @Override
    public List<PositionBean> reqAccountPosition(PositionQueryBean positionQueryBean) throws Exception {
        if (positionQueryBean.getPageSize() == 0) {
            //多次请求
            int pageSize = 1000;
            int size = pageSize;
            positionQueryBean.setPageSize(pageSize);
            List<PositionBean> reList = new ArrayList<>();
            while (pageSize == size) {
                List<PositionBean> list = sendAccountPosition(positionQueryBean);
                pageSize = CollectionUtils.isEmpty(list) ? 0 : list.size();
                logger.info("pageSize : {}", pageSize);
                reList.addAll(list);
                if (pageSize == size) {
                    PositionBean positionBean = list.get(pageSize - 1);
                    String positionStr = positionBean.getMarket() + ";" + positionBean.getStockCode();
                    positionQueryBean.setPositionStr(positionStr);
                }
            }
            return reList;
        }
        return sendAccountPosition(positionQueryBean);
    }

    private List<PositionBean> sendAccountPosition(PositionQueryBean positionQueryBean) throws Exception {
        String accountId = positionQueryBean.getAccountId();
        TerminalAgentInfo agentInfo = terminalAgentInfoService.selectOne(getTerminalName(), FunctionCode.REQ_QRY_FINANCE.value);
        agentInfo.setAccountId(accountId);
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put("accountId", accountId);
        if (!Strings.isNullOrEmpty(positionQueryBean.getStockCode())) {
            String[] marketAndStockCode = StockCodeUtils.getMarketAndStockCode(positionQueryBean.getStockCode());
            if (Market.SZSE.value.equals(marketAndStockCode[1])) {
                parameters.put("market", '2');
                parameters.put("reqMarket", '2');
            } else if (Market.SHSE.value.equals(marketAndStockCode[1])) {
                parameters.put("market", '1');
                parameters.put("reqMarket", '1');
            }
            parameters.put("reqStockCode", marketAndStockCode[0]);
        }

        if (!Strings.isNullOrEmpty(positionQueryBean.getPositionStr())) {
            String[] positionStr = positionQueryBean.getPositionStr().split(";");
            if (Market.SZSE.value.equals(positionStr[0])) {
                parameters.put("indexMarketCode", '2');
            } else if (Market.SHSE.value.equals(positionStr[0])) {
                parameters.put("indexMarketCode", '1');
            }
            parameters.put("indexStockCode", StockCodeUtils.getStockCode(positionStr[1]));
        }
        parameters.put("maxRetLen", positionQueryBean.getPageSize());
        parameters.put("off", "1");

        ResultBean resultBean = tradeAgentHelper.doRequest(agentInfo, parameters);
        if (resultBean.getErrorCode() != ErrorCode.SUCCESS.value) {
            return Lists.newArrayList();
        }
        return JsdMsgParseHelper.parseAccountPosition(resultBean, getAccount(accountId));
    }

    @Override
    public List<Instrument> reqInstrumentInfo(InstrumentQryBean instrumentQryBean) throws Exception {
        //待实现
        return null;
    }

    @Override
    public List<PledgeDiscount> reqPledgeDiscount(String accountId, String market, String stockCode) throws Exception {
        //待实现
        return null;
    }

    /**
     * 标准券查询-395
     */
    @Override
    public List<StandardBonds> reqStandardBonds(StandardBondQryBean qryBean) throws Exception {
        String accountId = qryBean.getAccountId();
        Map<String, String> parameters = Maps.newHashMap();
        parameters.put("accountId", accountId);
        parameters.put("market", getAgentMarket(qryBean.getMarket()));

        TerminalAgentInfo agentInfo = terminalAgentInfoService.selectOne(getTerminalName(), FunctionCode.REQ_STANDARD_BONDS.value);
        agentInfo.setAccountId(accountId);
        ResultBean resultBean = tradeAgentHelper.doRequest(agentInfo, parameters);
        if (resultBean.getErrorCode() != ErrorCode.SUCCESS.value) {
            return Lists.newArrayList();
        }

        return JsdMsgParseHelper.parseStandardBonds(resultBean, accountId);
    }

    /**
     * 大宗交易行情查询-345
     */
    @Override
    public List<IntegratedBulkQuoteBean> reqIntegratedBulkQuote(IntegratedBulkQuoteQryBean bulkQuoteQryBean) throws Exception {
        String accountId = bulkQuoteQryBean.getAccountId();
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put("accountId", accountId);
        String stockCode = bulkQuoteQryBean.getStockCode();
        if (!Strings.isNullOrEmpty(stockCode)) {
            parameters.put("market", getAgentMarket(StockCodeUtils.getMarket(stockCode)));
            parameters.put("stockCode", StockCodeUtils.getStockCode(stockCode));
        }
        parameters.put("instructionSet", CbptransTypeUtils.getCbptransTypeByEntrustBsAndEntrustProp(bulkQuoteQryBean.getEntrustBs(), bulkQuoteQryBean.getEntrustProp()));
        parameters.put("recordStatus", '1');

        TerminalAgentInfo agentInfo = terminalAgentInfoService.selectOne(getTerminalName(), FunctionCode.REQ_QRY_INTEGRATED_BULK_QUOTE.value);
        agentInfo.setAccountId(accountId);
        ResultBean resultBean = tradeAgentHelper.doRequest(agentInfo, parameters);
        if (resultBean.getErrorCode() != ErrorCode.SUCCESS.value) {
            return Lists.newArrayList();
        }

        return JsdMsgParseHelper.parseIntegratedBulkQuote(resultBean);
    }

    /**
     * 盘后大宗交易行情查询-346
     */
    @Override
    public List<IntegratedBulkCloseQuoteBean> reqIntegratedBulkCloseQuote(IntegratedBulkCloseQuoteQryBean bulkCloseQuoteQryBean) throws Exception {
        String accountId = bulkCloseQuoteQryBean.getAccountId();
        String stockCode = bulkCloseQuoteQryBean.getStockCode();
        Map<String, String> parameters = Maps.newHashMap();
        parameters.put("accountId", accountId);
        if (StringUtils.isNotBlank(stockCode)) {
            parameters.put("stockCode", StockCodeUtils.getStockCode(stockCode));
            parameters.put("market", getAgentMarket(StockCodeUtils.getMarket(stockCode)));
        }

        TerminalAgentInfo agentInfo = terminalAgentInfoService.selectOne(getTerminalName(), FunctionCode.REQ_QRY_INTEGRATED_BULK_CLOSE_QUOTE.value);
        agentInfo.setAccountId(accountId);
        ResultBean resultBean = tradeAgentHelper.doRequest(agentInfo, parameters);
        if (resultBean.getErrorCode() != ErrorCode.SUCCESS.value) {
            return Lists.newArrayList();
        }
        return JsdMsgParseHelper.parseIntegratedBulkCloseQuote(resultBean);
    }

    @Override
    public List<IntegratedRepurchaseQuoteBean> reqIntegratedRepurchaseQuote(IntegratedRepurchaseQuoteQryBean integratedRepurchaseQuoteQryBean) throws Exception {
        //待处理
        return null;
    }

    @Override
    public List<IntegratedFixedIncomeQuoteBean> reqIntegratedFixedIncomeQuote(IntegratedFixedIncomeQuoteQryBean integratedFixedIncomeQuoteQryBean) throws Exception {
        //待处理
        return null;
    }

    @Override
    public void reqReissueEntrust(String accountId) {
        Map<String, EntrustInterface> entrustInterfaceMap = Maps.newHashMap();
        List<Entrust> entrustList = qryEntrust(accountId);
        List<IntegratedEntrust> integratedEntrustList = qryIntegratedOrder(accountId);
        entrustList.forEach(entrust -> {
            entrust.setId(entrustIdManager.getIdByBrokeEntrustId(entrust.getAccountId(), entrust.getBrokerEntrustId()));
            if (!Strings.isNullOrEmpty(entrust.getId())) {
                entrustInterfaceMap.put(entrust.getId(), entrust);
            }
        });
        integratedEntrustList.forEach(integratedEntrust -> {
            integratedEntrust.setId(entrustIdManager.getIdByBrokeEntrustId(integratedEntrust.getAccountId(), integratedEntrust.getBrokerEntrustId()));
            if (!Strings.isNullOrEmpty(integratedEntrust.getId())) {
                entrustInterfaceMap.put(integratedEntrust.getId(), integratedEntrust);
            }
        });

        compareReissueEntrust(accountId, entrustInterfaceMap);
    }

    @Override
    public void reqReissueTrade(String accountId) {
        List<Trade> tradeList = qryTrade(accountId);
        List<Trade> trades = qryProtocolBackTrade(accountId);
        tradeList.addAll(trades);
        if (CollectionUtils.isEmpty(tradeList)) {
            return;
        }
        compareReissueTrade(accountId, tradeList);
    }

    @Override
    public void removeAccount(String accountId) {
        accountMap.remove(accountId);
    }

    @Override
    public void queryNextTrade() {
        //登录成功用户
        if (MapUtils.isEmpty(accountMap)) {
            return;
        }
        for (Map.Entry<String, Account> entry : accountMap.entrySet()) {
            Account account = entry.getValue();
            String accountId = account.getId();
            logger.info("account：{}，status：{}", accountId, getStatus(accountId));
            if (getStatus(accountId) != AccountLoginStatus.TRADING.value) {
                return;
            }
            boolean loginStatus = tradeAgentManager.getAgentLoginStatus(accountId);
            if (loginStatus) {
                executeQueryNextTrade(account);
            }
        }
    }

    private ThreadPoolExecutor poolExecutor = null;

    private void executeQueryNextTrade(Account account) {
        if (null == poolExecutor) {
            poolExecutor = new ThreadPoolExecutor(8, 20, 1000, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>(100), new ThreadPoolExecutor.DiscardPolicy());
            poolExecutor.prestartAllCoreThreads();
        }

        for (QueryEnum queryEnum : QueryEnum.values()) {
            try {
                poolExecutor.execute(new QueryTask(queryEnum, account));
                Thread.sleep(3000);
            } catch (Exception e) {
                logger.error("{}执行异常：{}", queryEnum.name(), e.getMessage(), e);
            }
        }
    }

    enum QueryEnum {
        qryEntrust,
        qryTrade,
        qryIntegratedEntrust,
        qryIntegratedTrade
    }

    class QueryTask implements Runnable {
        private QueryEnum queryEnum;
        private Account account;

        public QueryTask(QueryEnum queryEnum, Account account) {
            this.queryEnum = queryEnum;
            this.account = account;
        }

        @Override
        public void run() {
            System.out.println(this.toString() + " is running!");
            try {
                switch (queryEnum) {
                    case qryEntrust:
                        qryEntrust(account);
                        return;
                    case qryTrade:
                        qryTrade(account);
                        return;
                    case qryIntegratedEntrust:
                        qryIntegratedEntrust(account);
                        return;
                    case qryIntegratedTrade:
                        qryIntegratedTrade(account);
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

    public void qryEntrust(Account account) {
        String accountId = account.getId();
        try {
            List<Entrust> entrustList = qryEntrust(accountId);
            entrustList.forEach(this::onRtnEntrust);
        } catch (Exception e) {
            logger.error("qryEntrust: {} error:{}", accountId, e);
        }

    }

    public void qryTrade(Account account) {
        String accountId = account.getId();
        try {
            List<Trade> trades = qryTrade(accountId);
            trades.forEach(this::onTrade);
        } catch (Exception e) {
            logger.error("qryTrade: {} error:{}", accountId, e);
        }
    }

    public void qryIntegratedEntrust(Account account) {
        String accountId = account.getId();
        try {
            List<IntegratedEntrust> entrustList = qryIntegratedOrder(accountId);
            entrustList.forEach(this::onRtnIntegratedEntrust);
        } catch (Exception e) {
            logger.error("qryIntegratedEntrust: {} error:{}", accountId, e);
        }
    }

    public void qryIntegratedTrade(Account account) {
        String accountId = account.getId();
        try {
            List<Trade> trades = qryProtocolBackTrade(accountId);
            trades.forEach(this::onTrade);
        } catch (Exception e) {
            logger.error("qryIntegratedTrade: {} error:{}", accountId, e);
        }
    }

    private void onTrade(Trade trade) {
        if (Strings.isNullOrEmpty(trade.getBrokerTradeId())) {
            trade.setBrokerTradeId(trade.getEntrustId());
        }
        trade.setNoCancelEntrust(true);
        onRtnTrade(trade);
    }

    public List<Entrust> qryEntrust(String accountId) {
        PositionStr positionStr = positionStrMap.get(accountId);
        if (Objects.isNull(positionStr)) {
            positionStr = new PositionStr();
        }
        SimpleDateFormat tradingDayFormat = new SimpleDateFormat("yyyyMMdd");
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put("accountId", accountId);
        parameters.put("indexContractId", positionStr.getOrderPositionStr());
        parameters.put("indexDate", tradingDayFormat.format(new Date()));
        //0-市场代码、1-委托时间、2-委托时间倒序
        parameters.put("orderType", '1');
        parameters.put("market", ' ');

        TerminalAgentInfo agentInfo = terminalAgentInfoService.selectOne(getTerminalName(), FunctionCode.REQ_QRY_ENTRUST.value);
        agentInfo.setAccountId(accountId);
        ResultBean resultBean = tradeAgentHelper.doRequest(agentInfo, parameters);
        if (resultBean.getErrorCode() != ErrorCode.SUCCESS.value) {
            return Lists.newArrayList();
        }

        List<Entrust> entrustList = JsdMsgParseHelper.parseEntrusts(resultBean, getAccount(accountId), entrustIdManager);
        String position_str = entrustList.get(entrustList.size() - 1).getPositionStr();
        positionStr.setOrderPositionStr(position_str);
        positionStrMap.put(accountId, positionStr);

        return entrustList;
    }

    /**
     * 343
     */
    public List<IntegratedEntrust> qryIntegratedOrder(String accountId) {
        PositionStr positionStr = positionStrMap.get(accountId);
        if (Objects.isNull(positionStr)) {
            positionStr = new PositionStr();
        }
        SimpleDateFormat tradingDayFormat = new SimpleDateFormat("yyyyMMdd");
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put("accountId", accountId);
        parameters.put("orderType", '0');
        parameters.put("market", ' ');
        if (!Strings.isNullOrEmpty(positionStr.getFixedPositionStr())) {
            parameters.put("indexContractId", Integer.parseInt(positionStr.getFixedPositionStr()));
        }

        TerminalAgentInfo agentInfo = terminalAgentInfoService.selectOne(getTerminalName(), FunctionCode.REQ_QRY_ENTRUST.value);
        agentInfo.setAccountId(accountId);
        ResultBean resultBean = tradeAgentHelper.doRequest(agentInfo, parameters);
        if (resultBean.getErrorCode() != ErrorCode.SUCCESS.value) {
            return Lists.newArrayList();
        }

        List<IntegratedEntrust> entrustList = JsdMsgParseHelper.parseIntegratedEntrusts(resultBean, getAccount(accountId));
        String position_str = entrustList.get(entrustList.size() - 1).getPositionStr();
        positionStr.setFixedPositionStr(position_str);
        positionStrMap.put(accountId, positionStr);

        return entrustList;
    }

    /**
     * 成交查询-122
     */
    public List<Trade> qryTrade(String accountId) {
        PositionStr positionStr = positionStrMap.get(accountId);
        if (Objects.isNull(positionStr)) {
            positionStr = new PositionStr();
        }
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put("accountId", accountId);
        parameters.put("orderType", '0');
        parameters.put("market", ' ');
        parameters.put("searchDirection", "1");
        parameters.put("queryMode", "0");
        parameters.put("queryTYpe", '1');
        if (!Strings.isNullOrEmpty(positionStr.getTradePositionStr())) {
            parameters.put("positionStr", positionStr.getTradePositionStr());
        }
        TerminalAgentInfo agentInfo = terminalAgentInfoService.selectOne(getTerminalName(), FunctionCode.REQ_QRY_TRADE.value);
        agentInfo.setAccountId(accountId);
        ResultBean resultBean = tradeAgentHelper.doRequest(agentInfo, parameters);
        if (resultBean.getErrorCode() != ErrorCode.SUCCESS.value) {
            return Lists.newArrayList();
        }

        List<Trade> tradeList = JsdMsgParseHelper.parseTrades(resultBean, getAccount(accountId));
        String position_str = tradeList.get(tradeList.size() - 1).getPositionStr();
        positionStr.setTradePositionStr(position_str);
        positionStrMap.put(accountId, positionStr);

        return tradeList;
    }

    /**
     * 大宗交易当日成交查-344
     */
    public List<Trade> qryProtocolBackTrade(String accountId) {
        PositionStr positionStr = positionStrMap.get(accountId);
        if (Objects.isNull(positionStr)) {
            positionStr = new PositionStr();
        }
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put("accountId", accountId);
        parameters.put("tradeStatusSet", "A");
        parameters.put("market", ' ');
        if (!Strings.isNullOrEmpty(positionStr.getProtocolBackPositionStr())) {
            parameters.put("indexDealNo", positionStr.getProtocolBackPositionStr());
        }
        TerminalAgentInfo agentInfo = terminalAgentInfoService.selectOne(getTerminalName(), FunctionCode.REQ_QRY_TRADE.value);
        agentInfo.setAccountId(accountId);
        ResultBean resultBean = tradeAgentHelper.doRequest(agentInfo, parameters);
        if (resultBean.getErrorCode() != ErrorCode.SUCCESS.value) {
            return Lists.newArrayList();
        }

        List<Trade> tradeList = JsdMsgParseHelper.parseProtocolBackTrades(resultBean, getAccount(accountId));
        String position_str = tradeList.get(tradeList.size() - 1).getPositionStr();
        positionStr.setProtocolBackPositionStr(position_str);
        positionStrMap.put(accountId, positionStr);

        return tradeList;
    }

    private String getAgentMarket(String market) {
        if (Strings.isNullOrEmpty(market)) {
            return null;
        }
        return Market.SHSE.value.equals(market) ? "1" : "2";
    }

	@Override
	public List<NegotiateQuoteReceiveBean> reqNegotiateQuoteReceive(NegotiateQuoteQueryBean negotiateQuoteQueryBean)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
