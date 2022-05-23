package com.joyintech.tams.exchange.service.trade.server.agent.ufx;

import com.alibaba.fastjson.JSONArray;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.joyintech.tams.exchange.common.bean.*;
import com.joyintech.tams.exchange.common.bean.bond.StandardBondQryBean;
import com.joyintech.tams.exchange.common.constant.*;
import com.joyintech.tams.exchange.common.db.domain.*;
import com.joyintech.tams.exchange.common.net.protocol.FunctionCode;
import com.joyintech.tams.exchange.common.utils.*;
import com.joyintech.tams.exchange.service.trade.dao.domain.TerminalAgentInfo;
import com.joyintech.tams.exchange.service.trade.server.agent.TradeAgentDsp;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author lifangpeng
 * @date 2021/10/11
 */
@Component("ufx")
public class UfxTradeAgent extends TradeAgentDsp {
    private static final Logger logger = LoggerFactory.getLogger(UfxTradeAgent.class);

    //entrustId-accountId
    private Map<String, String> accountEntrustMap = Maps.newConcurrentMap();
    //cancelId-cancelEntrust
    private Map<String, CancelEntrustBean> entrustMap = Maps.newConcurrentMap();

    /**
     * 记录已经登录的账号
     */
    private Map<String, LoginInfo> loginAccounts = Maps.newConcurrentMap();

    public LoginInfo getLoginInfo(String accountId) {
        return loginAccounts.get(accountId);
    }

    public void addLoginInfo(String accountId, LoginInfo loginInfo) {
        loginAccounts.put(accountId, loginInfo);
    }

    @PostConstruct
    public void init() {
        setTerminalName(AgentType.UFX.value);
    }

    @Override
    public void removeAccount(String accountId) {
        accountMap.remove(accountId);
        loginAccounts.remove(accountId);
    }


    @Override
    public ResultBean reqLoginAccount(AccountLoginReqBean accountLoginReqBean) {
        Account account = accountLoginReqBean.getAccount();
        // 账户交易密码解密
        account.setPwd(new String(Base64.decodeBase64(account.getPwd())));
        account.setTradePwd(new String(Base64.decodeBase64(account.getTradePwd())));
        String accountId = account.getId();
        TerminalAgentInfo agentInfo = terminalAgentInfoService.selectOne(getTerminalName(), FunctionCode.REQ_ACCOUNT_LOGIN.value);
        agentInfo.setAccountId(accountId);
        Map<String, String> parameters = Maps.newHashMap();
        parameters.put("account", accountId);
        parameters.put("financeAccount", account.getFinanceAccount());
        parameters.put("pwd", account.getTradePwd());
        parameters.put("password", account.getTradePwd());
        ResultBean resultBean = tradeAgentHelper.doRequest(agentInfo, parameters);
        // 账户登录成功，记录下登录成功的账户
        if (resultBean.getErrorCode() == ErrorCode.SUCCESS.value) {
            logger.info("account {} login success", accountId);
            tradeAgentManager.addAccounts(this, accountId);
            tradeAgentManager.addAgentLoginStatus(accountId, true);
            accountManager.addAccounts(account);
            addTradeRequest(accountId, this);
            addAccount(accountId, account);
            setStatus(account.getId(), AccountLoginStatus.LOGGED);

            LoginInfo loginInfo = UfxMsgParseHelper.parseLoginInfo((Map<String, Object>) resultBean.getData());
            addLoginInfo(accountId, loginInfo);
            resultBean.setData(loginInfo);

            //登录成功后发送重传请求
            terminalHelper.reqReissueEntrust(accountId);
            terminalHelper.reqReissueTrade(accountId);
            //账户持仓缓存
            accountPositionHelper.positionCache(this, accountId);
        } else {
            logger.info("account {} login failed {} -- {}", accountId, resultBean.getErrorCode(), resultBean.getErrorMsg());
        }
        return resultBean;
    }

    @Override
    public void reqLogoutAccount(Account account) {

    }

    /**
     * 333101 查询订单(查询证券委托)
     */
    @Override
    public List<Entrust> reqEntrusts(String accountId) throws Exception {
        return getOrder(accountId);
    }

    @Override
    public List<IntegratedEntrust> reqIntegratedEntrusts(String accountId) throws Exception {
        return getIntegratedOrder(accountId);
    }

    /**
     * 333102 查询成交(证券成交查询)
     */
    @Override
    public List<Trade> reqTrades(String accountId) throws Exception {
        return getTrade(accountId);
    }

    private List<Trade> getTrade(String accountId) {
        int pageSize = 1000;
        int size = pageSize;
        String positionStr = "";
        List<Trade> reList = new ArrayList<>();
        while (pageSize == size) {
            List<Trade> list = getQueryTradeReBack(accountId, pageSize, positionStr);
            pageSize = CollectionUtils.isEmpty(list) ? 0 : list.size();
            logger.debug("pageSize : {}", pageSize);
            if (pageSize == size) {
                positionStr = list.get(pageSize - 1).getPositionStr();
            }
            reList.addAll(list);
        }
        return reList;
    }

    private List<Trade> getQueryTradeReBack(String accountId, int requestNum, String positionStr) {
        Map<String, String> parameters = Maps.newHashMap();
        parameters.put("account", accountId);
        parameters.put("password_type", "2");
        parameters.put("position_str", positionStr);
        parameters.put("request_num", String.valueOf(requestNum));
        // '1'-逆序　'0'-正序
        parameters.put("query_direction", "0");

        TerminalAgentInfo agentInfo = terminalAgentInfoService.selectOne(getTerminalName(), FunctionCode.REQ_QRY_TRADE.value, "Bidding");
        agentInfo.setAccountId(accountId);
        ResultBean resultBean = tradeAgentHelper.doRequest(agentInfo, parameters);
        List<Map<String, Object>> resList = (List<Map<String, Object>>) resultBean.getData();

        return UfxMsgParseHelper.parseTrades(accountId, resList);
    }

    /**
     * 竞价平台委托下单
     * 333002 普通委托	用于竞价平台所有委托业务下单
     * 333012 ETF申购
     * 333013 ETF赎回
     */
    @Override
    public void entrust(Entrust entrust) {
        logger.info("send entrust:{}", entrust.getId());

        //竞价平台普通委托
        String entrustProp = "Bidding";
        //ETF申赎
        if (EntrustProp.ETF_APPLY.value.equals(entrust.getEntrustProp())) {
            if (EntrustBs.BUY.value.equals(entrust.getEntrustBs())) {
                entrustProp = "ETF_APPLY_BUY";
            } else if (EntrustBs.SELL.value.equals(entrust.getEntrustBs())) {
                entrustProp = "ETF_APPLY_SELL";
            }
        }

        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put("account", entrust.getAccountId());
        parameters.put("stkcode", StockCodeUtils.getStockCode(entrust.getStockCode()));

        if (Market.SZSE.value.equals(entrust.getMarket())) {
            parameters.put("exchange_type", ExchangeType.SZ.value);
        } else if (Market.SHSE.value.equals(entrust.getMarket())) {
            parameters.put("exchange_type", ExchangeType.SH.value);
        }
        parameters.put("stock_code", StockCodeUtils.getStockCode(entrust.getStockCode()));
        parameters.put("entrust_amount", entrust.getEntrustVolume());
        //全价
        if (FullPriceConst.FULL_PRICE.value.equals(entrust.getIsFullPrice())) {
            parameters.put("entrust_price", entrust.getFullPrice().toPlainString());
        } else {//净价
            parameters.put("entrust_price", entrust.getEntrustPrice().toPlainString());
        }
        parameters.put("entrust_bs", entrust.getEntrustBs());
        parameters.put("entrust_prop", entrust.getEntrustProp());
        parameters.put("batch_no", entrust.getLocalEntrustId());
        parameters.put("entrust_reference", entrust.getId());

        TerminalAgentInfo agentInfo = terminalAgentInfoService.selectOne(getTerminalName(), FunctionCode.REQ_ENTRUST.value, entrustProp);
        agentInfo.setAccountId(entrust.getAccountId());
        parameters.put("password", getAccount(agentInfo.getAccountId()).getTradePwd());
        try {
            ResultBean resultBean = tradeAgentHelper.doRequest(agentInfo, parameters);
            Map<String, Object> ds = (Map<String, Object>) resultBean.getData();

            int errorCode = resultBean.getErrorCode();
            if (errorCode != ErrorCode.SUCCESS.value) {
                entrust.setErrorCode(errorCode);
                entrust.setEntrustStatus(EntrustStatus.INVALID.value);
                entrust.setErrorMsg(ErrorMsgUtils.subErrorMsg((String) ds.get("error_info")));
                logger.warn("integratedEntrust failed ErrorNo:{}，reason:{}", errorCode, ds.get("error_info"));
            } else {
                entrust.setTradingDay((String) ds.get("init_date"));
                entrust.setBrokerEntrustId((String) ds.get("entrust_no"));
                entrust.setReportTime((String) ds.get("entrust_time"));
                entrust.setReportNo((String) ds.get("report_no"));
                entrust.setErrorMsg(ErrorMsgUtils.subErrorMsg((String) ds.get("error_info")));
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
     * 332702	综合业务委托确认	用于深交所综合协议平台报单，上交所大宗交易报单
     * 332722	固收委托确认	   上交所固收平台现券交易委托下单
     * 332400   债券质押协议回购初始交易	深交所质押式协议回购首期报单
     * 332401   债券质押协议回购购回交易	深交所质押式协议回购到期购回、提前购回报单
     * 332410   深圳债券质押协议回购到期续做
     * 332725	协议回购委托确认	上交所质押式协议回购首期、购回、换券、续做、解除质押等所有业务报单
     */
    @Override
    public void integratedEntrust(IntegratedEntrust entrust) {
        logger.info("send integratedEntrust:{}", entrust.getId());
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put("account", entrust.getAccountId());
        parameters.put("stock_code", StockCodeUtils.getStockCode(entrust.getStockCode()));
        String entrustProp = getIntegratedEntrustProp(entrust, parameters);

        if (StringUtils.isBlank(entrustProp)) {
            throw new RuntimeException(ErrorCode.REQ_PARAMETER_ERROR.errorMsg);
        }

        TerminalAgentInfo agentInfo = terminalAgentInfoService.selectOne(getTerminalName(), FunctionCode.REQ_INTEGRATED_ENTRUST.value, entrustProp);
        agentInfo.setAccountId(entrust.getAccountId());
        parameters.put("password", getAccount(agentInfo.getAccountId()).getTradePwd());
        try {
            ResultBean resultBean = tradeAgentHelper.doRequest(agentInfo, parameters);
            Map<String, Object> ds = (Map<String, Object>) resultBean.getData();

            int errorCode = resultBean.getErrorCode();
            if (errorCode != ErrorCode.SUCCESS.value) {
                entrust.setEntrustStatus(EntrustStatus.INVALID.value);
                entrust.setErrorMsg(ErrorMsgUtils.subErrorMsg((String) ds.get("error_info")));
                entrust.setErrorCode(errorCode);
                logger.warn("integratedEntrust failed ErrorNo:{}，reason:{}", errorCode, ds.get("error_info"));
            } else {
                entrust.setBrokerEntrustId((String) ds.get("entrust_no"));
                String initDate = (String) ds.get("init_date");
                if (!Strings.isNullOrEmpty(initDate)) {
                    entrust.setTradingDay(initDate);
                }
                entrust.setErrorMsg(ErrorMsgUtils.subErrorMsg((String) ds.get("error_info")));
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
     * 333017	委托撤单	用于竞价平台所有委托业务撤单
     */
    @Override
    public void cancelEntrust(Entrust entrust, EntrustInterface cancelEntrust) {
        CancelEntrustBean cancelEntrustBean = getCancelEntrustBean(entrust, cancelEntrust);

        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put("account", entrust.getAccountId());
        parameters.put("entrust_no", cancelEntrustBean.getBrokerEntrustId());
        parameters.put("entrust_date", cancelEntrustBean.getTradingDay());

        TerminalAgentInfo agentInfo = terminalAgentInfoService.selectOne(getTerminalName(), FunctionCode.REQ_CANCEL_ENTRUST.value);
        agentInfo.setAccountId(entrust.getAccountId());
        try {
            ResultBean resultBean = tradeAgentHelper.doRequest(agentInfo, parameters);
            Map<String, Object> ds = (Map<String, Object>) resultBean.getData();
            int errorCode = resultBean.getErrorCode();
            cancelEntrustBean.setAgentType(AgentType.UFX.value);
            if (errorCode != ErrorCode.SUCCESS.value) {
                cancelEntrustBean.setErrorCode(errorCode);
                cancelEntrustBean.setEntrustStatus(EntrustStatus.INVALID.value);
                cancelEntrustBean.setErrorMsg(ErrorMsgUtils.subErrorMsg((String) ds.get("error_info")));
            } else {
                cancelEntrustBean.setTradingDay((String) ds.get("init_date"));
                cancelEntrustBean.setCancelBrokerEntrustId((String) ds.get("entrust_no"));
                cancelEntrustBean.setBrokerEntrustId((String) ds.get("entrust_no_old"));
                String exchangeType = (String) ds.get("exchange_type");
                if (ExchangeType.SH.value.equals(exchangeType)) {
                    cancelEntrustBean.setMarket(Market.SHSE.value);
                } else if (ExchangeType.SZ.value.equals(exchangeType)) {
                    cancelEntrustBean.setMarket(Market.SZSE.value);
                }
                cancelEntrustBean.setStockCode((String) ds.get("stock_code"));
                cancelEntrustBean.setErrorMsg(ErrorMsgUtils.subErrorMsg((String) ds.get("error_info")));
            }
            onRspCancelEntrust(cancelEntrustBean);
        } catch (Exception e) {
            logger.error(" cancelEntrust error:{},{}", entrust.getId(), e.getMessage(), e);
            throw new RuntimeException(ErrorCode.TERMINAL_API_ERROR.errorMsg);
        }
    }

    /**
     * 332703	综合业务委托撤单	用于深交所综合协议平台撤单，上交所大宗交易撤单
     * 332723   固收撤回
     * 332402   债券质押式协议回购_深交所撤回
     * 332726   债券质押式协议回购_上交所撤回
     */
    @Override
    public void cancelIntegratedEntrust(IntegratedEntrust integratedEntrust, IntegratedEntrust cancelEntrust) {
        CancelEntrustBean cancelEntrustBean = getCancelEntrustBean(integratedEntrust, cancelEntrust);
        String entrustProp = null;
        if (IntegratedEntrustUtils.isBulk(cancelEntrustBean.getEntrustProp())) {
            //大宗
            entrustProp = "Bulk";
        } else if (Market.SHSE.value.equals(cancelEntrustBean.getMarket())
                && IntegratedEntrustUtils.isFixedIncome(cancelEntrustBean.getEntrustProp())) {
            //固收撤回
            entrustProp = "FixedIncome";
        } else if (IntegratedEntrustUtils.isRepurchaseSZ(cancelEntrustBean.getEntrustProp(),
                cancelEntrustBean.getEntrustBs(), cancelEntrustBean.getMarket())) {
            //债券质押式协议回购_深交所撤回
            entrustProp = "RepurchaseSZ";
        } else if (IntegratedEntrustUtils.isRepurchaseSH(cancelEntrustBean.getEntrustProp(),
                cancelEntrustBean.getEntrustBs(), cancelEntrustBean.getMarket())) {
            //债券质押式协议回购_上交所撤回
            entrustProp = "RepurchaseSH";
        }

        if (Strings.isNullOrEmpty(entrustProp)) {
            cancelEntrustBean.setEntrustStatus(EntrustStatus.INVALID.value);
            cancelEntrustBean.setErrorMsg("bsFlag没有对应的entrustProp");
            cancelEntrustBean.setErrorCode(ErrorCode.REQ_PARAMETER_ERROR.value);

            onRspCancelIntegratedEntrust(cancelEntrustBean);
            return;
        }

        String accountId = integratedEntrust.getAccountId();
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put("account", accountId);
        parameters.put("entrust_no", cancelEntrustBean.getBrokerEntrustId());

        TerminalAgentInfo agentInfo = terminalAgentInfoService.selectOne(getTerminalName(), FunctionCode.REQ_CANCEL_INTEGRATED_ENTRUST.value, entrustProp);
        agentInfo.setAccountId(accountId);
        try {
            ResultBean resultBean = tradeAgentHelper.doRequest(agentInfo, parameters);
            Map<String, Object> ds = (Map<String, Object>) resultBean.getData();
            int errorCode = resultBean.getErrorCode();

            cancelEntrustBean.setAgentType(AgentType.UFX.value);
            if (errorCode != ErrorCode.SUCCESS.value) {
                cancelEntrustBean.setEntrustStatus(EntrustStatus.INVALID.value);
                cancelEntrustBean.setErrorMsg(ErrorMsgUtils.subErrorMsg((String) ds.get("error_info")));
                cancelEntrustBean.setErrorCode(errorCode);
            } else {
                cancelEntrustBean.setCancelBrokerEntrustId((String) ds.get("entrust_no"));
                cancelEntrustBean.setErrorMsg(ErrorMsgUtils.subErrorMsg((String) ds.get("error_info")));
            }

            onRspCancelIntegratedEntrust(cancelEntrustBean);
        } catch (Exception e) {
            logger.error(" cancelIntegratedEntrust error:{},{}", integratedEntrust.getId(), e.getMessage(), e);
            throw new RuntimeException(ErrorCode.TERMINAL_API_ERROR.errorMsg);
        }
    }

    /**
     * 大宗交易行情查询
     * 398	大宗交易行情查询	查询大宗交易行情
     * 361	上海固收业务确定报价行情查询	上交所固收平台现券交易报价行情查询
     * 332728	协议回购行情信息查询	上交所质押式协议回购公开行情、非公开行情查询
     */
    @Override
    public List<IntegratedQuote> reqIntegratedQuotes(QuoteQryBean quoteQryBean) throws Exception {
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put("account", quoteQryBean.getAccountId());
        int quoteType = getQuoteType(quoteQryBean, parameters);
        if (quoteType == 0) {
            logger.error("不支持查询该行情类型，quoteType：{}", quoteQryBean.getQuoteType());
            return Collections.emptyList();
        }
        // xiazelei 修改于 2022/4/29 16:07 置空market参数
        TerminalAgentInfo agentInfo = terminalAgentInfoService.selectOne(getTerminalName(), FunctionCode.REQ_QRY_INTEGRATED_QUOTE.value, quoteType, null);
        agentInfo.setAccountId(quoteQryBean.getAccountId());
        ResultBean resultBean = tradeAgentHelper.doRequest(agentInfo, parameters);
        if (resultBean.getErrorCode() != ErrorCode.SUCCESS.value) {
            return Lists.newArrayList();
        }

        return UfxMsgParseHelper.parseIntegratedQuotes(resultBean, quoteQryBean);
    }

    private int getQuoteType(QuoteQryBean quoteQryBean, Map<String, Object> parameters) {
        int quoteType = 0;
        if (QuoteType.BULK_QUOTE.value == quoteQryBean.getQuoteType()
                || QuoteType.AFTER_BULK_QUOTE.value == quoteQryBean.getQuoteType()) {
            //大宗
            quoteType = 398;

            String market = StockCodeUtils.getMarket(quoteQryBean.getStockCode());
            if (market.equals(Market.SZSE.value)) {
                quoteQryBean.setMarket(Market.SZSE.value);
                parameters.put("exchange_type", ExchangeType.SZ.value);
            } else if (market.equals(Market.SHSE.value)) {
                quoteQryBean.setMarket(Market.SHSE.value);
                parameters.put("exchange_type", ExchangeType.SH.value);
            }
            parameters.put("cbphq_type", "00");
            parameters.put("stock_code", StockCodeUtils.getStockCode(quoteQryBean.getStockCode()));
        } else if (QuoteType.FIXED_INCOME_QUOTE.value == quoteQryBean.getQuoteType()) {
            //固收
            quoteType = 361;
            parameters.put("stock_code", StockCodeUtils.getStockCode(quoteQryBean.getStockCode()));
        } else if (QuoteType.NON_PUBLIC_PROTOCOL_BACK_QUOTE.value == quoteQryBean.getQuoteType()
                || QuoteType.PUBLIC_PROTOCOL_BACK_QUOTE.value == quoteQryBean.getQuoteType()) {
            //上海债券质押式协议回购行情信息查询
            quoteType = 332728;
            if (QuoteType.NON_PUBLIC_PROTOCOL_BACK_QUOTE.value == quoteQryBean.getQuoteType()) {
                //非公开报价行情
                parameters.put("action_in", 1);
            } else if (QuoteType.PUBLIC_PROTOCOL_BACK_QUOTE.value == quoteQryBean.getQuoteType()) {
                //公开报价行情
                parameters.put("action_in", 0);
            }
            parameters.put("position_str", quoteQryBean.getPostStr());
            parameters.put("cbpconfer_id", quoteQryBean.getContractId());
        }

        return quoteType;
    }

    /**
     * 333001	大约可买获取	用于竞价平台所有委托业务可买数量预算
     * 333103	证券持仓快速查询	资金账号持仓记录查询
     */
    @Override
    public int reqEnable(EnableQryBean qryBean) throws Exception {
        //大约可买获取
        String funcId = "333001";
        String entrustBs = qryBean.getEntrustBs();
        if (EntrustBs.SELL.value.equals(entrustBs)) {
            //卖
            funcId = "333103";
        }

        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put("account", qryBean.getAccountId());
        parameters.put("stock_code", StockCodeUtils.getStockCode(qryBean.getStockCode()));
        String market = StockCodeUtils.getMarket(qryBean.getStockCode());

        if (market.equals(Market.SZSE.value)) {
            parameters.put("exchange_type", ExchangeType.SZ.value);
        } else if (market.equals(Market.SHSE.value)) {
            parameters.put("exchange_type", ExchangeType.SH.value);
        }
        //买
        if (EntrustBs.BUY.value.equals(entrustBs)) {
            parameters.put("entrust_price", qryBean.getEntrustPrice());
            parameters.put("entrust_prop", qryBean.getEntrustProp());
        }

        TerminalAgentInfo agentInfo = terminalAgentInfoService.selectOne(getTerminalName(), FunctionCode.REQ_QRY_TRADE_ENABLE.value, funcId);
        agentInfo.setAccountId(qryBean.getAccountId());
        ResultBean resultBean = tradeAgentHelper.doRequest(agentInfo, parameters);
        if (resultBean.getErrorCode() != ErrorCode.SUCCESS.value) {
            return -1;
        }
        Map<String, Object> data = (Map<String, Object>) resultBean.getData();
        if (EntrustBs.BUY.value.equals(entrustBs)) {
            String enableBuyAmount = (String) data.get("enable_buy_amount");
            if (Strings.isNullOrEmpty(enableBuyAmount)) {
                return 0;
            }
            return Integer.parseInt(enableBuyAmount.substring(0, enableBuyAmount.lastIndexOf(".")));
        }

        String enableAmount = (String) data.get("enable_amount");
        if (Strings.isNullOrEmpty(enableAmount)) {
            return 0;
        }
        return Integer.parseInt(enableAmount.substring(0, enableAmount.lastIndexOf(".")));
    }

    /**
     * 339304	历史证券成交查询	竞价平台历史成交流水查询
     */
    @Override
    public List<HistoryTrade> reqHistoryTrade(HistoryQryBean historyQryBean) throws Exception {
        String accountId = historyQryBean.getAccountId();
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put("account", accountId);
        parameters.put("start_date", historyQryBean.getStartDay());
        parameters.put("end_date", historyQryBean.getEndDay());
        parameters.put("request_num", historyQryBean.getPageSize());
        parameters.put("position_str", historyQryBean.getPostStr());

        TerminalAgentInfo agentInfo = terminalAgentInfoService.selectOne(getTerminalName(), FunctionCode.REQ_QRY_HISTORY_TRADE.value);
        agentInfo.setAccountId(accountId);
        ResultBean resultBean = tradeAgentHelper.doRequest(agentInfo, parameters);
        if (resultBean.getErrorCode() != ErrorCode.SUCCESS.value) {
            return Lists.newArrayList();
        }

        return UfxMsgParseHelper.parseHistoryTrade(resultBean, accountId);
    }

    /**
     * 332254	客户资金快速查询	资金账号可用资金、资产查询
     */
    @Override
    public List<AccountFinance> reqCustomerFinance(String accountId) throws Exception {
        TerminalAgentInfo agentInfo = terminalAgentInfoService.selectOne(getTerminalName(), FunctionCode.REQ_QRY_FINANCE.value);
        agentInfo.setAccountId(accountId);
        Map<String, String> parameters = Maps.newHashMap();
        parameters.put("account", accountId);
        ResultBean resultBean = tradeAgentHelper.doRequest(agentInfo, parameters);
        if (resultBean.getErrorCode() != ErrorCode.SUCCESS.value) {
            return Lists.newArrayList();
        }
        return UfxMsgParseHelper.parseCustomerFinance(resultBean, accountId);
    }

    /**
     * 333103	证券持仓快速查询	资金账号持仓记录查询
     */
    @Override
    public List<PositionBean> reqAccountPosition(PositionQueryBean positionQueryBean) throws Exception {
        String accountId = positionQueryBean.getAccountId();
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put("account", accountId);
        parameters.put("request_num", positionQueryBean.getPageSize());

        if (!Strings.isNullOrEmpty(positionQueryBean.getStockCode())) {
            String market = StockCodeUtils.getMarket(positionQueryBean.getStockCode());
            if (market.equals(Market.SZSE.value)) {
                parameters.put("exchange_type", ExchangeType.SZ.value);
            } else if (market.equals(Market.SHSE.value)) {
                parameters.put("exchange_type", ExchangeType.SH.value);
            }
            parameters.put("stock_code", StockCodeUtils.getStockCode(positionQueryBean.getStockCode()));
        }
        //定位串
        parameters.put("position_str", "");
        if (!Strings.isNullOrEmpty(positionQueryBean.getPositionStr())) {
            parameters.put("position_str", positionQueryBean.getPositionStr());
        }

        TerminalAgentInfo agentInfo = terminalAgentInfoService.selectOne(getTerminalName(), FunctionCode.REQ_QRY_POSITION.value);
        agentInfo.setAccountId(accountId);
        ResultBean resultBean = tradeAgentHelper.doRequest(agentInfo, parameters);
        if (resultBean.getErrorCode() != ErrorCode.SUCCESS.value) {
            return Lists.newArrayList();
        }
        return UfxMsgParseHelper.parseAccountPosition(resultBean, accountId);
    }

    /**
     * 333116	质押回购可用查询	用于债券质押式回购可交易额度获取
     */
    @Override
    public List<Instrument> reqInstrumentInfo(InstrumentQryBean instrumentQryBean) throws Exception {
        String accountId = instrumentQryBean.getAccountId();
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put("account", accountId);
        //（0：默认，查询单个代码或重复代码；1：查询全部代码；2：按stock_code条件查询全部代码）
        parameters.put("query_type", 1);
        if (Market.SHSE.value.equals(instrumentQryBean.getMarket())) {
            parameters.put("exchange_type", ExchangeType.SH.value);
        } else if (Market.SZSE.value.equals(instrumentQryBean.getMarket())) {
            parameters.put("exchange_type", ExchangeType.SZ.value);
        } else {
            logger.warn("unknown market {}", instrumentQryBean.getMarket());
        }
        parameters.put("stock_type", "");
        parameters.put("stock_code", "");
        parameters.put("request_num", instrumentQryBean.getRowCount());
        parameters.put("position_str", "");
        if (!Strings.isNullOrEmpty(instrumentQryBean.getPostStr())) {
            parameters.put("position_str", instrumentQryBean.getPostStr());
        }

        TerminalAgentInfo agentInfo = terminalAgentInfoService.selectOne(getTerminalName(), FunctionCode.REQ_QRY_INSTRUMENT_INFO.value);
        agentInfo.setAccountId(accountId);
        ResultBean resultBean = tradeAgentHelper.doRequest(agentInfo, parameters);
        if (resultBean.getErrorCode() != ErrorCode.SUCCESS.value) {
            return Lists.newArrayList();
        }

        return UfxMsgParseHelper.parseInstrumentInfo(resultBean);
    }

    @Override
    public List<PledgeDiscount> reqPledgeDiscount(String accountId, String market, String stockCode) throws Exception {
        //TODO 待做
        return UfxMsgParseHelper.parsePledgeDiscount(null, accountId);
    }

    /**
     * 333116	质押回购可用查询	用于债券质押式回购可交易额度获取
     */
    @Override
    public List<StandardBonds> reqStandardBonds(StandardBondQryBean standardBondQryBean) throws Exception {
        String accountId = standardBondQryBean.getAccountId();
        TerminalAgentInfo agentInfo = terminalAgentInfoService.selectOne(getTerminalName(), FunctionCode.REQ_STANDARD_BONDS.value);
        agentInfo.setAccountId(accountId);
        String marketStr = standardBondQryBean.getMarket();
        if (!Strings.isNullOrEmpty(standardBondQryBean.getStockCode())) {
            marketStr = StockCodeUtils.getMarket(standardBondQryBean.getStockCode());
            if (!Strings.isNullOrEmpty(standardBondQryBean.getMarket()) && !standardBondQryBean.getMarket().equals(marketStr)) {
                logger.error("查询标准券失败 {}", JsonUtils.getJsonFromObject(standardBondQryBean));
                return Collections.emptyList();
            }
        }

        if (Strings.isNullOrEmpty(standardBondQryBean.getMarket()) && Strings.isNullOrEmpty(standardBondQryBean.getStockCode())) {
            List<StandardBonds> szList = getStandardBonds(agentInfo, accountId, Market.SZSE.value);
            List<StandardBonds> shList = getStandardBonds(agentInfo, accountId, Market.SHSE.value);
            szList.addAll(shList);
            return szList;
        }

        return getStandardBonds(agentInfo, accountId, marketStr);
    }

    public List<StandardBonds> getStandardBonds(TerminalAgentInfo agentInfo, String accountId, String market) {
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put("account", accountId);
        if (Market.SZSE.value.equals(market)) {
            parameters.put("exchange_type", ExchangeType.SZ.value);
        } else if (Market.SHSE.value.equals(market)) {
            parameters.put("exchange_type", ExchangeType.SH.value);
        }

        ResultBean resultBean = tradeAgentHelper.doRequest(agentInfo, parameters);
        if (resultBean.getErrorCode() != ErrorCode.SUCCESS.value) {
            return Lists.newArrayList();
        }

        return UfxMsgParseHelper.parseStandardBonds(resultBean, accountId);
    }

    /**
     * 398	大宗交易行情查询	查询大宗交易行情
     */
    @Override
    public List<IntegratedBulkQuoteBean> reqIntegratedBulkQuote(IntegratedBulkQuoteQryBean quoteQryBean) throws Exception {
        String accountId = quoteQryBean.getAccountId();
        TerminalAgentInfo agentInfo = terminalAgentInfoService.selectOne(getTerminalName(), FunctionCode.REQ_QRY_INTEGRATED_BULK_QUOTE.value);
        agentInfo.setAccountId(accountId);
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put("account", accountId);
        String market = "";
        String stockCode = quoteQryBean.getStockCode();
        if (!Strings.isNullOrEmpty(stockCode)) {
            market = StockCodeUtils.getMarket(stockCode);
            if (market.equals(Market.SZSE.value)) {
                parameters.put("exchange_type", ExchangeType.SZ.value);
            } else if (market.equals(Market.SHSE.value)) {
                parameters.put("exchange_type", ExchangeType.SH.value);
            }
            parameters.put("stock_code", StockCodeUtils.getStockCode(stockCode));
        }
        String cbptransType = CbptransTypeUtils.getCbptransTypeByEntrustBsAndEntrustProp(quoteQryBean.getEntrustBs(), quoteQryBean.getEntrustProp());
        parameters.put("cbptrans_type", cbptransType);
        parameters.put("cbphq_type", "00");

        String positionStr = "";
        int pageSize = quoteQryBean.getPageSize();
        //不翻页 组合全部给出
        if (pageSize == 0) {
            pageSize = 1000;
            int size = pageSize;
            List<IntegratedBulkQuoteBean> reList = new ArrayList<>();
            while (pageSize == size) {
                List<IntegratedBulkQuoteBean> list = getQueryIntegratedBulkQuote(agentInfo, parameters, pageSize, positionStr, market);
                pageSize = CollectionUtils.isEmpty(list) ? 0 : list.size();
                logger.debug("pageSize : {}", pageSize);
                if (pageSize == size) {
                    positionStr = list.get(pageSize - 1).getPositionStr();
                }
                reList.addAll(list);
            }
            return reList;
        }

        return getQueryIntegratedBulkQuote(agentInfo, parameters, pageSize, positionStr, market);
    }

    private List<IntegratedBulkQuoteBean> getQueryIntegratedBulkQuote(TerminalAgentInfo agentInfo, Map<String, Object> parameters,
                                                                      int pageSize, String positionStr, String market) {
        parameters.put("request_num", pageSize);
        parameters.put("position_str", positionStr);
        ResultBean resultBean = tradeAgentHelper.doRequest(agentInfo, parameters);
        if (resultBean.getErrorCode() != ErrorCode.SUCCESS.value) {
            return Lists.newArrayList();
        }

        return UfxMsgParseHelper.parseIntegratedBulkQuote(resultBean, market);
    }

    /**
     * 盘后大宗交易行情查询
     * 394
     * 364
     */
    @Override
    public List<IntegratedBulkCloseQuoteBean> reqIntegratedBulkCloseQuote(IntegratedBulkCloseQuoteQryBean bulkCloseQuoteQryBean) throws Exception {
        String accountId = bulkCloseQuoteQryBean.getAccountId();
        String stockCode = bulkCloseQuoteQryBean.getStockCode();
        String market = StockCodeUtils.getMarket(stockCode);

        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put("account", accountId);
        parameters.put("exchange_type", bulkCloseQuoteQryBean.getExchangeType());
        parameters.put("stock_code", StockCodeUtils.getStockCode(stockCode));

        TerminalAgentInfo agentInfo = terminalAgentInfoService.selectOne(getTerminalName(), FunctionCode.REQ_QRY_INTEGRATED_BULK_CLOSE_QUOTE.value, null, market);
        agentInfo.setAccountId(accountId);
        ResultBean resultBean = tradeAgentHelper.doRequest(agentInfo, parameters);
        if (resultBean.getErrorCode() != ErrorCode.SUCCESS.value) {
            return Lists.newArrayList();
        }
        return UfxMsgParseHelper.parseIntegratedBulkCloseQuote(resultBean, market);
    }

    /**
     * 332728 协议回购行情查询
     */
    @Override
    public List<IntegratedRepurchaseQuoteBean> reqIntegratedRepurchaseQuote(IntegratedRepurchaseQuoteQryBean
                                                                                    quoteQryBean) throws Exception {
        String accountId = quoteQryBean.getAccountId();
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put("account", accountId);
        //非公开报价行情
        parameters.put("action_in", quoteQryBean.getActionIn());
        //委托属性
        if (StringUtils.isNoneBlank(quoteQryBean.getEntrustProp())) {
            parameters.put("entrust_prop", quoteQryBean.getEntrustProp());
        }
        int pageSize = quoteQryBean.getPageSize();

        TerminalAgentInfo agentInfo = terminalAgentInfoService.selectOne(getTerminalName(), FunctionCode.REQ_QRY_INTEGRATED_REPURCHASE_QUOTE.value);
        agentInfo.setAccountId(accountId);
        String positionStr = "";
        //不翻页 组合全部给出
        if (pageSize == 0) {
            pageSize = 1000;
            int size = pageSize;
            List<IntegratedRepurchaseQuoteBean> reList = new ArrayList<>();
            while (pageSize == size) {
                List<IntegratedRepurchaseQuoteBean> list = getQueryIntegratedRepurchaseQuote(agentInfo, parameters, pageSize, positionStr);
                pageSize = CollectionUtils.isEmpty(list) ? 0 : list.size();
                logger.debug("pageSize : {}", pageSize);
                if (pageSize == size) {
                    positionStr = list.get(pageSize - 1).getPositionStr();
                }
                reList.addAll(list);
            }
            return reList;
        }

        return getQueryIntegratedRepurchaseQuote(agentInfo, parameters, pageSize, positionStr);
    }

    private List<IntegratedRepurchaseQuoteBean> getQueryIntegratedRepurchaseQuote(TerminalAgentInfo agentInfo,
                                                                                  Map<String, Object> parameters,
                                                                                  int pageSize, String positionStr) {
        //请求行数
        parameters.put("request_num", pageSize);
        //定位串
        parameters.put("position_str", positionStr);

        ResultBean resultBean = tradeAgentHelper.doRequest(agentInfo, parameters);
        if (resultBean.getErrorCode() != ErrorCode.SUCCESS.value) {
            return Lists.newArrayList();
        }

        return UfxMsgParseHelper.parseIntegratedRepurchaseQuote(resultBean);
    }

    /**
     * 361 查询上海固收平台报价行情
     */
    @Override
    public List<IntegratedFixedIncomeQuoteBean> reqIntegratedFixedIncomeQuote(IntegratedFixedIncomeQuoteQryBean
                                                                                      fixedIncomeQuoteQryBean) throws Exception {
        String accountId = fixedIncomeQuoteQryBean.getAccountId();
        String stockCode = fixedIncomeQuoteQryBean.getStockCode();

        Map<String, String> parameters = Maps.newHashMap();
        if (!Strings.isNullOrEmpty(stockCode)) {
            parameters.put("stock_code", StockCodeUtils.getStockCode(stockCode));
        }
        parameters.put("exchange_type", fixedIncomeQuoteQryBean.getExchangeType());

        TerminalAgentInfo agentInfo = terminalAgentInfoService.selectOne(getTerminalName(), FunctionCode.REQ_QRY_INTEGRATED_FIXED_INCOME_QUOTE.value);
        agentInfo.setAccountId(accountId);
        ResultBean resultBean = tradeAgentHelper.doRequest(agentInfo, parameters);
        if (resultBean.getErrorCode() != ErrorCode.SUCCESS.value) {
            return Lists.newArrayList();
        }

        return UfxMsgParseHelper.parseIntegratedFixedIncomeQuote(resultBean, StockCodeUtils.getMarket(stockCode));
    }

    @Override
    public void reqReissueEntrust(String accountId) {
        Map<String, EntrustInterface> entrustInterfaceMap = Maps.newHashMap();

        List<Entrust> entrustList = getOrder(accountId);
        List<IntegratedEntrust> integratedEntrustList = getIntegratedOrder(accountId);
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

        compareReissueEntrust(accountId, entrustInterfaceMap);
    }

    @Override
    public void reqReissueTrade(String accountId) {
        List<Trade> tradeList = Lists.newArrayList();
        List<Trade> trades = getTrade(accountId);
        List<Trade> protocolBackTradeList = getProtocolBackTrade(accountId);
        tradeList.addAll(trades);
        tradeList.addAll(protocolBackTradeList);

        compareReissueTrade(accountId, tradeList);
    }

    private List<Trade> getProtocolBackTrade(String accountId) {
        int pageSize = 1000;
        int size = pageSize;
        String positionStr = "";
        List<Trade> reList = new ArrayList<>();
        while (pageSize == size) {
            List<Trade> list = getProtocolBackTradeReBack(accountId, pageSize, positionStr);
            pageSize = CollectionUtils.isEmpty(list) ? 0 : list.size();
            logger.debug("pageSize : {}", pageSize);
            if (pageSize == size) {
                positionStr = list.get(pageSize - 1).getPositionStr();
            }
            reList.addAll(list);
        }
        return reList;
    }

    private List<Trade> getProtocolBackTradeReBack(String accountId, int pageSize, String positionStr) {
        Map<String, String> parameters = Maps.newHashMap();
        parameters.put("accountId", accountId);
        parameters.put("account", accountId);
        parameters.put("position_str", positionStr);
        //固收 深圳and上海协议回购成交申报
        String entrustProp = "BPN,BPR,BPT,BPB,BPH,BPI,FI1,FI3,FI9,a,b,c,e,AFW,AFC";
        parameters.put("en_entrust_prop", entrustProp);
        parameters.put("request_num", String.valueOf(pageSize));
        parameters.put("query_type", "0");

        TerminalAgentInfo agentInfo = terminalAgentInfoService.selectOne(getTerminalName(), FunctionCode.REQ_QRY_TRADE.value, "ProtocolBack");
        agentInfo.setAccountId(accountId);
        ResultBean resultBean = tradeAgentHelper.doRequest(agentInfo, parameters);
        List<Map<String, Object>> resList = (List<Map<String, Object>>) resultBean.getData();

        return UfxMsgParseHelper.qryProtocolBackTradeBack(accountId, resList);
    }

    @Override
    public void queryNextTrade() {
        //登录成功用户
        if (MapUtils.isEmpty(loginAccounts)) {
            return;
        }
        for (Map.Entry<String, LoginInfo> entry : loginAccounts.entrySet()) {
            String accountId = entry.getKey();
            LoginInfo loginInfo = entry.getValue();
            logger.info("account：{}，status：{}", accountId, getStatus(accountId));
            if (getStatus(accountId) != AccountLoginStatus.TRADING.value) {
                return;
            }
            boolean loginStatus = tradeAgentManager.getAgentLoginStatus(accountId);
            if (loginStatus) {
                executeQueryNextTrade(accountId, loginInfo);
            }
        }
    }

    private ThreadPoolExecutor poolExecutor = null;

    private void executeQueryNextTrade(String accountId, LoginInfo loginInfo) {
        if (null == poolExecutor) {
            poolExecutor = new ThreadPoolExecutor(8, 20, 1000, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>(100), new ThreadPoolExecutor.DiscardPolicy());
            poolExecutor.prestartAllCoreThreads();
        }

        for (QueryEnum queryEnum : QueryEnum.values()) {
            try {
                poolExecutor.execute(new QueryTask(queryEnum, loginInfo, accountId));
                Thread.sleep(3000);
            } catch (Exception e) {
                logger.error("{}执行异常：{}", queryEnum.name(), e.getMessage(), e);
            }
        }

    }

    enum QueryEnum {
        qryOrder,
        queryIntegratedOrder,
        qryTrade,
        qryProtocolBackTrade;
    }

    class QueryTask implements Runnable {
        private QueryEnum queryEnum;
        private LoginInfo loginInfo;
        private String accountId;

        public QueryTask(QueryEnum queryEnum, LoginInfo loginInfo, String accountId) {
            this.queryEnum = queryEnum;
            this.loginInfo = loginInfo;
            this.accountId = accountId;
        }

        @Override
        public void run() {
            System.out.println(this.toString() + " is running!");
            try {
                switch (queryEnum) {
                    case qryOrder:
                        qryOrder(loginInfo, accountId);
                        return;
                    case queryIntegratedOrder:
                        queryIntegratedOrder(loginInfo, accountId);
                        return;
                    case qryTrade:
                        qryTrade(loginInfo, accountId);
                        return;
                    case qryProtocolBackTrade:
                        qryProtocolBackTrade(loginInfo, accountId);
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

    private Map<String, Integer> entrustIdAndStatusMap = Maps.newConcurrentMap();
    private final String entrustPrefix = "e";
    private final String integratedEntrustPrefix = "i";


    public void qryOrder(LoginInfo loginInfo, String accountId) throws Exception {
        //每次都查询全量委托进行比对，如果委托没有处于终态。则通知委托
        List<Entrust> entrustList = getOrder(accountId);
        if (CollectionUtils.isEmpty(entrustList)) {
            return;
        }
        entrustList.forEach(entrust -> {
            //可能不存在id，用前缀区分普通和综合
            String key = entrustPrefix + "_" + entrust.getId() + "_" + entrust.getBrokerEntrustId() + "_" + entrust.getAccountId();
            Integer entrustStatus = entrustIdAndStatusMap.get(key);
            if (Objects.isNull(entrustStatus) || entrustStatus != entrust.getEntrustStatus()) {
                logger.info("委托[{}]新增或状态发生变更，推送委托。oldEntrustStatus [{}]  newEntrustStatus [{}]", key, entrustStatus, entrust.getEntrustStatus());
                logger.info("onEntrust {}", JsonUtils.getJsonFromObject(entrust));
                onRtnEntrust(entrust);
                entrustIdAndStatusMap.put(key, entrust.getEntrustStatus());
            }
        });
    }

    /***
     * 333101 委托主动拉取
     */
    private List<Entrust> getOrder(String accountId) {
        int pageSize = 1000;
        int size = pageSize;
        String positionStr = "";
        List<Entrust> reList = new ArrayList<>();
        while (pageSize == size) {
            List<Entrust> list = getOrderReBack(accountId, pageSize, positionStr);
            pageSize = CollectionUtils.isEmpty(list) ? 0 : list.size();
            logger.debug("pageSize : {}", pageSize);
            if (pageSize == size) {
                positionStr = list.get(pageSize - 1).getPositionStr();
            }
            reList.addAll(list);
        }

        return reList;
    }

    private List<Entrust> getOrderReBack(String accountId, int pageSize, String positionStr) {
        Account account = getAccount(accountId);
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put("account", accountId);
        parameters.put("password", account.getTradePwd());
        parameters.put("request_num", pageSize);
        parameters.put("position_str", positionStr);
        TerminalAgentInfo agentInfo = terminalAgentInfoService.selectOne(getTerminalName(), FunctionCode.REQ_QRY_ENTRUST.value, "Bidding");
        agentInfo.setAccountId(accountId);
        ResultBean resultBean = tradeAgentHelper.doRequest(agentInfo, parameters);
        List<Map<String, Object>> resList = (List<Map<String, Object>>) resultBean.getData();

        return UfxMsgParseHelper.parseEntrust(accountId, resList);
    }

    public void queryIntegratedOrder(LoginInfo loginInfo, String accountId) {
        //每次都查询全量委托进行比对，如果委托没有处于终态。则通知委托
        List<IntegratedEntrust> entrustList = getIntegratedOrder(accountId);
        if (CollectionUtils.isEmpty(entrustList)) {
            return;
        }
        entrustList.forEach(entrust -> {
            //可能不存在id，用前缀区分普通和综合
            String key = integratedEntrustPrefix + "_" + entrust.getId() + "_" + entrust.getBrokerEntrustId() + "_" + entrust.getAccountId();
            Integer entrustStatus = entrustIdAndStatusMap.get(key);
            if (Objects.isNull(entrustStatus) || entrustStatus != entrust.getEntrustStatus()) {
                logger.info("委托[{}]新增或状态发生变更，推送委托。oldEntrustStatus [{}]  newEntrustStatus [{}]", key, entrustStatus, entrust.getEntrustStatus());
                logger.info("onIntegratedEntrust {}", JsonUtils.getJsonFromObject(entrust));
                onRtnIntegratedEntrust(entrust);
                entrustIdAndStatusMap.put(key, entrust.getEntrustStatus());
            }
        });
    }

    /***
     * 332704 协议大宗交易委托查询
     */
    private List<IntegratedEntrust> getIntegratedOrder(String accountId) {
        int pageSize = 1000;
        int size = pageSize;
        String positionStr = "";
        List<IntegratedEntrust> reList = new ArrayList<>();
        while (pageSize == size) {
            List<IntegratedEntrust> list = getIntegrateOrderReBack(accountId, pageSize, positionStr);
            pageSize = CollectionUtils.isEmpty(list) ? 0 : list.size();
            logger.debug("pageSize : {}", pageSize);
            if (pageSize == size) {
                positionStr = list.get(pageSize - 1).getPositionStr();
            }
            reList.addAll(list);
        }
        logger.debug("ReqQueryIntegrateOrder reback : {}", JSONArray.toJSONString(reList));
        return reList;
    }

    private List<IntegratedEntrust> getIntegrateOrderReBack(String accountId, int pageSize, String positionStr) {
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put("account", accountId);
        //FI1（确定报价），FI3（点击成交），FI9（指定对手方报价）
        //‘a,b,c,e,AFW,AFC’即大宗交易
        parameters.put("en_entrust_prop", "BPN,BPR,BPT,BPB,BPH,BPI,FI1,FI3,FI9,a,b,c,e,AFW,AFC");
        parameters.put("request_num", String.valueOf(pageSize));
        parameters.put("position_str", positionStr);
        TerminalAgentInfo agentInfo = terminalAgentInfoService.selectOne(getTerminalName(), FunctionCode.REQ_QRY_ENTRUST.value, "ProtocolBack");
        agentInfo.setAccountId(accountId);
        ResultBean resultBean = tradeAgentHelper.doRequest(agentInfo, parameters);
        List<Map<String, Object>> resList = (List<Map<String, Object>>) resultBean.getData();

        return UfxMsgParseHelper.parseIntegratedEntrust(accountId, resList);
    }

    private volatile Map<String, PositionStr> positionStrMap = Maps.newConcurrentMap();

    /**
     * 333102 成交主动拉取
     */
    public void qryTrade(LoginInfo loginInfo, String accountId) {
        PositionStr positionStr = positionStrMap.get(loginInfo.getFundAccount());
        List<Trade> list = getTrade(accountId, positionStr);
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        String position_str = list.get(list.size() - 1).getPositionStr();
        if (Objects.isNull(positionStr)) {
            positionStr = new PositionStr();
        }
        positionStr.setTradePositionStr(position_str);
        logger.debug("qryTrade position_str : {}", position_str);
        positionStrMap.put(loginInfo.getFundAccount(), positionStr);

        Set<String> businessIdSet = positionStr.getBusinessIdSet();
        list.forEach(trade -> {
            if (!Objects.isNull(businessIdSet) && businessIdSet.contains(trade.getBrokerTradeId())) {
                logger.info("qryTrade 委托已发送，不重复发送:{}", trade.getBrokerTradeId());
                return;
            }
            // 废单成交将成交转换为委托推送 -- 返回废单原因
            if (TradeType.isInvalid(trade.getTradeType())) {
                Entrust entrust = new Entrust();
                BeanUtils.copyProperties(trade, entrust);
                entrust.setId(trade.getEntrustId());
                entrust.setEntrustStatus(EntrustStatus.INVALID.value);
                logger.info("废单成交转换为委托通知  trade：[{}]  entrust: [{}]", JsonUtils.getJsonFromObject(trade), JsonUtils.getJsonFromObject(entrust));
                onRtnEntrust(entrust);
            } else {
                onRtnTrade(trade);
            }
        });
    }

    private List<Trade> getTrade(String accountId, PositionStr positionStr) {
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put("account", accountId);
        parameters.put("password_type", "2");
        parameters.put("password", getAccount(accountId).getTradePwd());
        if (!Objects.isNull(positionStr) && !Objects.isNull(positionStr.getTradePositionStr())) {
            parameters.put("position_str", positionStr.getTradePositionStr());
        }
        parameters.put("query_direction", "0");
        parameters.put("request_num", "1000");
        parameters.put("query_type", "0");
        parameters.put("query_mode", "0");

        TerminalAgentInfo agentInfo = terminalAgentInfoService.selectOne(getTerminalName(), FunctionCode.REQ_QRY_TRADE.value, "Bidding");
        agentInfo.setAccountId(accountId);
        ResultBean resultBean = tradeAgentHelper.doRequest(agentInfo, parameters);
        List<Map<String, Object>> resList = Lists.newArrayList();
        resList = (List<Map<String, Object>>) resultBean.getData();

        return UfxMsgParseHelper.parseTrades(accountId, resList);
    }

    /**
     * 332705 债券质押协议回购成交查询 固收成交查询 大宗
     */
    public void qryProtocolBackTrade(LoginInfo loginInfo, String accountId) {
        PositionStr positionStr = positionStrMap.get(loginInfo.getFundAccount());

        List<Trade> list = getProtocolBackTrade(accountId, positionStr);
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        String position_str = list.get(list.size() - 1).getPositionStr();
        if (Objects.isNull(positionStr)) {
            positionStr = new PositionStr();
        }
        positionStr.setProtocolBackPositionStr(position_str);
        logger.debug("qryProtocolBack_Trade position_str : {}", position_str);
        positionStrMap.put(loginInfo.getFundAccount(), positionStr);

        list.forEach(trade -> {
            // 废单成交将成交转换为委托推送 -- 返回废单原因
            if (TradeType.isInvalid(trade.getTradeType())) {
                IntegratedEntrust entrust = new IntegratedEntrust();
                BeanUtils.copyProperties(trade, entrust);
                entrust.setId(trade.getEntrustId());
                entrust.setEntrustStatus(EntrustStatus.INVALID.value);
                logger.info("废单成交转换为委托通知  trade：[{}]  IntegratedEntrust: [{}]", JsonUtils.getJsonFromObject(trade), JsonUtils.getJsonFromObject(entrust));
                onRtnIntegratedEntrust(entrust);
            } else {
                onRtnTrade(trade);
            }
        });
    }

    private List<Trade> getProtocolBackTrade(String accountId, PositionStr positionStr) {
        Map<String, String> parameters = Maps.newHashMap();
        parameters.put("accountId", accountId);
        parameters.put("account", accountId);
        parameters.put("position_str", "");
        if (!Objects.isNull(positionStr) && !Objects.isNull(positionStr.getProtocolBackPositionStr())) {
            parameters.put("position_str", positionStr.getProtocolBackPositionStr());
        }
        //固收 深圳and上海协议回购成交申报
        String entrustProp = "BPN,BPR,BPT,BPB,BPH,BPI,FI1,FI3,FI9,a,b,c,e,AFW,AFC";
        parameters.put("en_entrust_prop", entrustProp);
        parameters.put("request_num", "1000");
        parameters.put("query_type", "0");

        TerminalAgentInfo agentInfo = terminalAgentInfoService.selectOne(getTerminalName(), FunctionCode.REQ_QRY_TRADE.value, "ProtocolBack");
        agentInfo.setAccountId(accountId);
        ResultBean resultBean = tradeAgentHelper.doRequest(agentInfo, parameters);
        List<Map<String, Object>> resList = (List<Map<String, Object>>) resultBean.getData();

        return UfxMsgParseHelper.qryProtocolBackTradeBack(accountId, resList);
    }

    private String getIntegratedEntrustProp(IntegratedEntrust entrust, Map<String, Object> parameters) {
        if (IntegratedEntrustUtils.isBulk(entrust.getEntrustProp())) {
            //大宗 332702
            bulkIntegratedEntrust(entrust, parameters);
            return "Bulk";
        } else if (Market.SHSE.value.equals(entrust.getMarket())
                && IntegratedEntrustUtils.isFixedIncome(entrust.getEntrustProp())) {
            //固收委托确认 332722
            fixedIncomeEntrust(entrust, parameters);
            return "FixedIncome";
        } else if (IntegratedEntrustUtils.isRepurchaseSQSZ(entrust.getEntrustProp(),
                entrust.getEntrustBs(), entrust.getMarket())) {
            //债券质押式协议回购_首期_深交所 332400
            repurchaseSQSZ(entrust, parameters);
            return "RepurchaseSQSZ";
        } else if (IntegratedEntrustUtils.isRepurchaseBuybackSZ(entrust.getEntrustProp(), entrust.getMarket())) {
            //深圳债券质押协议回购购回交易 332401
            repurchaseBuybackSZ(entrust, parameters);
            return "RepurchaseBuybackSZ";
        } else if (IntegratedEntrustUtils.isRepurchaseRenewalSZ(entrust.getEntrustProp(), entrust.getMarket())) {
            //深圳债券质押协议回购到期续做 332410
            repurchaseRenewalSZ(entrust, parameters);
            return "RepurchaseRenewalSZ";
        } else if (IntegratedEntrustUtils.isRepurchaseSH(entrust.getEntrustProp(),
                entrust.getEntrustBs(), entrust.getMarket())) {
            //债券质押式协议回购_上交所 332725
            repurchaseSH(entrust, parameters);
            return "RepurchaseSH";
        }

        return null;
    }

    private void bulkIntegratedEntrust(IntegratedEntrust integratedEntrust, Map<String, Object> parameters) {
        if (Market.SZSE.value.equals(integratedEntrust.getMarket())) {
            parameters.put("exchange_type", ExchangeType.SZ.value);
        } else if (Market.SHSE.value.equals(integratedEntrust.getMarket())) {
            parameters.put("exchange_type", ExchangeType.SH.value);
        }

        if (FullPriceConst.FULL_PRICE.value.equals(integratedEntrust.getIsFullPrice())) {
            parameters.put("entrust_price", integratedEntrust.getFullPrice().toPlainString());
        } else {
            parameters.put("entrust_price", integratedEntrust.getEntrustPrice().toPlainString());
        }
        parameters.put("entrust_prop", integratedEntrust.getEntrustProp());
        parameters.put("entrust_amount", integratedEntrust.getEntrustVolume());
        parameters.put("entrust_bs", integratedEntrust.getEntrustBs());
        //意向申报/定价/成交申报卖出
        if (!((EntrustBs.SELL.value.equals(integratedEntrust.getEntrustBs()) &&
                (EntrustProp.CONFIRM.value.equals(integratedEntrust.getEntrustProp())
                        || EntrustProp.SET_PRICE.value.equals(integratedEntrust.getEntrustProp())
                        || EntrustProp.INTENTION.value.equals(integratedEntrust.getEntrustProp())))
                || (EntrustBs.BUY.value.equals(integratedEntrust.getEntrustBs()) &&
                (EntrustProp.LARGE_AFTER_CLOSING_WEIGHT_PRICE.value.equals(integratedEntrust.getEntrustProp())
                ))
        )) {
            parameters.put("entrust_balance", integratedEntrust.getEntrustBalance().toPlainString());
        }
        if (IntegratedEntrustUtils.isBulk_YX_DJ(integratedEntrust.getBsFlag())) {
            //意向申报 定价申报
            parameters.put("relation_name", integratedEntrust.getLiaisonName());
            parameters.put("relation_tel", integratedEntrust.getLiaisonTel());
        } else if (IntegratedEntrustUtils.isBulk_DJCJ_CJ(integratedEntrust.getBsFlag())) {
            //点击成交申报
            parameters.put("prop_seat_no", integratedEntrust.getPropSeatNo());
            parameters.put("cbpconfer_id", integratedEntrust.getContractId());
        }
        parameters.put("entrust_reference", integratedEntrust.getId());
    }

    protected void fixedIncomeEntrust(IntegratedEntrust integratedEntrust, Map<String, Object> parameters) {
        if (Market.SZSE.value.equals(integratedEntrust.getMarket())) {
            parameters.put("exchange_type", ExchangeType.SZ.value);
        } else if (Market.SHSE.value.equals(integratedEntrust.getMarket())) {
            parameters.put("exchange_type", ExchangeType.SH.value);
        }
        if (FullPriceConst.FULL_PRICE.value.equals(integratedEntrust.getIsFullPrice())) {
            parameters.put("entrust_price", integratedEntrust.getFullPrice().toPlainString());
        } else {
            parameters.put("entrust_price", integratedEntrust.getEntrustPrice().toPlainString());
        }
        parameters.put("entrust_prop", integratedEntrust.getEntrustProp());
        parameters.put("entrust_amount", integratedEntrust.getEntrustVolume());
        parameters.put("entrust_bs", integratedEntrust.getEntrustBs());
        if (IntegratedEntrustUtils.isFixedIncome_BS(integratedEntrust.getEntrustProp())) {
            //指定对手方
            parameters.put("trader_id", integratedEntrust.getLiaisonTel());
            parameters.put("oppo_trader_id", integratedEntrust.getLiaisonName());
            parameters.put("oppo_agency", integratedEntrust.getPropSeatNo());
            parameters.put("cbpconfer_id", integratedEntrust.getContractId());
        } else if (IntegratedEntrustUtils.isFixedIncome_ChenJiao(integratedEntrust.getEntrustProp())
                || IntegratedEntrustUtils.isFixedIncome_BaoJia(integratedEntrust.getEntrustProp())) {
            //点击成交申报 最优价成交 可转换成交  定价申报
            parameters.put("trader_id", integratedEntrust.getLiaisonTel());
        } else {
            parameters.put("oppo_seatno", integratedEntrust.getPropSeatNo());
        }
    }


    private void repurchaseSQSZ(IntegratedEntrust integratedEntrust, Map<String, Object> parameters) {
        if (Market.SZSE.value.equals(integratedEntrust.getMarket())) {
            parameters.put("exchange_type", ExchangeType.SZ.value);
        } else if (Market.SHSE.value.equals(integratedEntrust.getMarket())) {
            parameters.put("exchange_type", ExchangeType.SH.value);
        }
        parameters.put("cbpconfer_id", integratedEntrust.getContractId());
        parameters.put("entrust_amount", integratedEntrust.getEntrustVolume());
        parameters.put("entrust_bs", integratedEntrust.getEntrustBs());
        parameters.put("entrust_balance", integratedEntrust.getEntrustBalance().toPlainString());
        parameters.put("date_back", integratedEntrust.getBackDate());
        parameters.put("prop_seat_no", integratedEntrust.getPropSeatNo());
        parameters.put("expire_year_rate", convertExpireYearRate(integratedEntrust.getEntrustPrice()));
        parameters.put("back_term", integratedEntrust.getCompactTerm());
        parameters.put("entrust_reference", integratedEntrust.getId());
    }

    private void repurchaseBuybackSZ(IntegratedEntrust integratedEntrust, Map<String, Object> parameters) {
        if (Market.SZSE.value.equals(integratedEntrust.getMarket())) {
            parameters.put("exchange_type", ExchangeType.SZ.value);
        } else if (Market.SHSE.value.equals(integratedEntrust.getMarket())) {
            parameters.put("exchange_type", ExchangeType.SH.value);
        }
        parameters.put("cbpconfer_id", integratedEntrust.getContractId());
        parameters.put("entrust_balance", integratedEntrust.getEntrustBalance().toPlainString());
        parameters.put("entrust_bs", integratedEntrust.getEntrustBs());
        parameters.put("expire_year_rate", convertExpireYearRate(integratedEntrust.getEntrustPrice()));
        parameters.put("orig_report_id", integratedEntrust.getOrigSerialNo());
        parameters.put("original_date", integratedEntrust.getFirstSettleDate());
        parameters.put("compact_term", integratedEntrust.getCompactTerm());
    }

    private void repurchaseRenewalSZ(IntegratedEntrust integratedEntrust, Map<String, Object> parameters) {
        if (Market.SZSE.value.equals(integratedEntrust.getMarket())) {
            parameters.put("exchange_type", ExchangeType.SZ.value);
        } else if (Market.SHSE.value.equals(integratedEntrust.getMarket())) {
            parameters.put("exchange_type", ExchangeType.SH.value);
        }
        parameters.put("cbpconfer_id", integratedEntrust.getContractId());
        parameters.put("entrust_balance", integratedEntrust.getEntrustBalance().toPlainString());
        parameters.put("entrust_bs", integratedEntrust.getEntrustBs());
        parameters.put("expire_year_rate", convertExpireYearRate(integratedEntrust.getEntrustPrice()));
        parameters.put("orig_report_id", integratedEntrust.getOrigSerialNo());
        parameters.put("back_term", integratedEntrust.getCompactTerm());
        parameters.put("original_date", integratedEntrust.getFirstSettleDate());
    }

    protected void repurchaseSH(IntegratedEntrust integratedEntrust, Map<String, Object> parameters) {

        if (Market.SZSE.value.equals(integratedEntrust.getMarket())) {
            parameters.put("exchange_type", ExchangeType.SZ.value);
        } else if (Market.SHSE.value.equals(integratedEntrust.getMarket())) {
            parameters.put("exchange_type", ExchangeType.SH.value);
        }

        parameters.put("entrust_amount", integratedEntrust.getEntrustVolume());
        parameters.put("entrust_bs", integratedEntrust.getEntrustBs());
        //债券质押式协议回购_上交所_确认/拒绝
        if (IntegratedEntrustUtils.isRepurchase_SH_ConfirmOrRefuse(integratedEntrust.getEntrustProp(),
                integratedEntrust.getEntrustBs(), integratedEntrust.getMarket())) {
            parameters.put("cbpconfer_id", integratedEntrust.getContractId());
        }
        // xiazelei 修改于 2022/4/22 10:37 修改所有的.doubleValue() 科学计数法有问题
        parameters.put("entrust_balance", integratedEntrust.getEntrustBalance().toPlainString());
        parameters.put("repurchase_date", integratedEntrust.getBackDate());
        parameters.put("expire_year_rate", convertExpireYearRate(integratedEntrust.getEntrustPrice()));
        parameters.put("entrust_prop", integratedEntrust.getEntrustProp());
        //到期续作
        if (IntegratedEntrustUtils.isRepurchaseRenewalSH(integratedEntrust.getEntrustProp(),
                integratedEntrust.getEntrustBs(), integratedEntrust.getMarket())) {
            //协议回购质权人类型
            parameters.put("bprsrc_status", integratedEntrust.getTransCode());
        }
        //非首期
        if (IntegratedEntrustUtils.isRepurchaseUNSQSH(integratedEntrust.getEntrustProp(),
                integratedEntrust.getEntrustBs(), integratedEntrust.getMarket())) {
            //原约定号
            parameters.put("orig_cbpconfer_id", integratedEntrust.getOrigSerialNo());
            //原申请日期
            parameters.put("orig_entrust_date", integratedEntrust.getFirstSettleDate());
        }
        parameters.put("oppo_agency_name", integratedEntrust.getPropSeatName());
        parameters.put("agency_no", integratedEntrust.getSellerNo().substring(1, 4));
        parameters.put("trader_id", integratedEntrust.getSellerNo());
        parameters.put("oppo_agency", integratedEntrust.getPropSeatNo());
        //借用成交标号 处理对方交易员
        parameters.put("oppo_trader_id", integratedEntrust.getLiaisonTel());
        if (!Strings.isNullOrEmpty(integratedEntrust.getRemark())) {
            parameters.put("remark", integratedEntrust.getRemark());
        }
    }

    private static String convertExpireYearRate(BigDecimal entrustPrice) {
        // xiazelei 修改于 2022/4/22 10:37  保存5位小数
        return TradeUtils.divide(entrustPrice, 100, 5).toPlainString();
    }

    @Override
    public List<NegotiateQuoteReceiveBean> reqNegotiateQuoteReceive(NegotiateQuoteQueryBean negotiateQuoteQueryBean)
            throws Exception {
        // TODO Auto-generated method stub
        return null;
    }
}
