package com.joyintech.tams.exchange.service.trade.server.agent.ufx;

import cn.hutool.core.collection.ListUtil;
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
import com.joyintech.tams.exchange.common.utils.*;
import com.joyintech.tams.exchange.service.trade.dao.domain.TerminalAgentInfo;
import com.joyintech.tams.exchange.service.trade.server.agent.TradeAgentDsp;
import com.joyintech.tams.exchange.service.trade.util.AgentUtil;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sound.midi.Instrument;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
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
    private Map<String, Integer> entrustIdAndStatusMap = Maps.newConcurrentMap();
    private final String entrustPrefix = "e";
    private final String integratedEntrustPrefix = "i";

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
        orderAccountMap.remove(accountId);
    }


    @Override
    public ResultBean reqLoginAccount(AccountLoginReqBean accountLoginReqBean) {
        Account account = accountLoginReqBean.getAccount();
        AgentUtil.SetDecodeBase64(account);
        String accountId = account.getId();
        TerminalAgentInfo agentInfo = terminalAgentInfoService.selectOne(getTerminalName(), FunctionCode.REQ_ACCOUNT_LOGIN.value);
        agentInfo.setAccountId(accountId);
        Map<String, String> parameters = Maps.newHashMap();
        parameters.put("account", accountId);
        parameters.put("financeAccount", account.getFinanceAccount());
        parameters.put("pwd", account.getTradePwd());
        parameters.put("password", account.getTradePwd());
        parameters.put("AgentCode", account.getBrokerId());
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

            orderAccountMap.remove(accountId);

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
        //parameters.put("position_str", positionStr);
        parameters.put("position_str", "");
        parameters.put("request_num", String.valueOf(requestNum));
        // '1'-逆序　'0'-正序
        parameters.put("query_direction", "0");

        TerminalAgentInfo agentInfo = terminalAgentInfoService.selectOne(getTerminalName(), FunctionCode.REQ_QRY_TRADE.value, "Bidding");
        agentInfo.setAccountId(accountId);
        ResultBean resultBean = tradeAgentHelper.doRequest(agentInfo, parameters);
        return UfxMsgParseHelper.parseTrades(accountId, AgentUtil.getQueryListUFX(resultBean));
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
        // 场景代码
        int functionCode = FunctionCode.REQ_ENTRUST.value;
        // region  业务场景判断
        // 333002竞价平台普通委托
        // 场景属性
        String entrustProp = "Bidding";
        //ETF申赎
        if (EntrustProp.ETF_APPLY.value.equals(entrust.getEntrustProp())) {
            if (EntrustBs.BUY.value.equals(entrust.getEntrustBs())) {
                entrustProp = "ETF_APPLY_BUY";
            } else if (EntrustBs.SELL.value.equals(entrust.getEntrustBs())) {
                entrustProp = "ETF_APPLY_SELL";
            }
        }
        // endregion

        // region 接口调用
        try {

            // region 参数组装
            Map<String, Object> parameters = Maps.newHashMap();
            parameters.put("account", entrust.getAccountId());
            parameters.put("stkcode", StockCodeUtils.getStockCode(entrust.getStockCode()));
            parameters.put("exchange_type", AgentUtil.ExchangeMarketUFX(entrust.getMarket()));
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
            // endregion

            TerminalAgentInfo agentInfo = terminalAgentInfoService.selectOne(getTerminalName(), functionCode, entrustProp);
            agentInfo.setAccountId(entrust.getAccountId());
            parameters.put("password", getAccount(agentInfo.getAccountId()).getTradePwd());
            ResultBean resultBean = tradeAgentHelper.doRequest(agentInfo, parameters);

            // region 返回结果处理
            Map<String, Object> ds = (Map<String, Object>) resultBean.getData();

            // 获取成功标识
            boolean successFlag = AgentUtil.getOrderSuccessFlagUFX(resultBean, ds);
            if (successFlag) {
                // 成功 已报
                entrust.setTradingDay((String) ds.get("init_date"));
                entrust.setBrokerEntrustId((String) ds.get("entrust_no"));
                entrust.setReportTime((String) ds.get("entrust_time"));
                entrust.setReportNo((String) ds.get("report_no"));
                entrust.setErrorMsg(ErrorMsgUtils.subErrorMsg((String) ds.get("error_info")));

                //保存当前下单账号
                orderAccountMap.put(entrust.getAccountId(), entrust.getBrokerEntrustId());
            } else {
                // 失败 柜台废单 还原持仓
                // yanjianhui 修改于 2022/8/24 11:23  柜台报错返回空报文导致指令进度没有被释放处理。
                String errorMsg = null;
                if (ds != null) {
                    errorMsg = ErrorMsgUtils.subErrorMsg((String) ds.get("error_info"));
                } else {
                    errorMsg = resultBean.getErrorMsg();
                }
                entrust.setErrorCode(ErrorCode.INVALID.value);
                entrust.setEntrustStatus(EntrustStatus.INVALID.value);
                entrust.setErrorMsg(errorMsg);
                logger.warn("integratedEntrust failed ErrorNo:{}，reason:{}", resultBean.getErrorCode(), errorMsg);
            }

            TerminalId terminalId = new TerminalId();
            BeanUtils.copyProperties(entrust, terminalId);
            terminalId.KeyType = "E";
            onRspEntrust(terminalId);
            // endregion

        } catch (Exception e) {
            logger.error("send entrust error:{},{}", entrust.getId(), e.getMessage(), e);
            throw new RuntimeException(ErrorCode.TERMINAL_API_ERROR.errorMsg);
        }
        // endregion
    }

    /**
     * 332702	综合业务委托确认	用于深交所综合协议平台报单，上交所大宗交易报单
     * 332722	固收委托确认	   上交所固收平台现券交易委托下单
     * 332400   债券质押协议回购初始交易	深交所质押式协议回购首期报单
     * 332401   债券质押协议回购购回交易	深交所质押式协议回购到期购回、提前购回报单
     * 332410   深圳债券质押协议回购到期续做
     * yanjianhui 修改于 2022/7/31 15:58 新增交易接口。
     * 332434   债券质押协议回购提前购回委托
     * 332435    债券质押协议回购质押券变更委托
     * 332436   债券质押协议回购解除质押委托
     * 332437   债券质押协议回购逆回购方确认委托
     * 332725	协议回购委托确认	上交所质押式协议回购首期、购回、换券、续做、解除质押等所有业务报单
     * xiazelei 修改于 2022/5/25 14:29 增加新的交易接口
     * 332647   债券现券交易协商委托
     * 332648   债券现券交易协商确认委托
     */
    @Override
    public void integratedEntrust(IntegratedEntrust entrust) {
        logger.info("send integratedEntrust:{}", entrust.getId());
        // 新业务代码场景处理标识；true为新场景代码
        boolean newFlag = false;
        ResultBean resultBean;
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put("account", entrust.getAccountId());
        parameters.put("stock_code", StockCodeUtils.getStockCode(entrust.getStockCode()));
        // 场景属性
        String entrustProp = getIntegratedEntrustProp(entrust, parameters);
        // 场景代码
        int functionCode = FunctionCode.REQ_INTEGRATED_ENTRUST.value;

        AgentUtil.CheckEntrustProp(entrustProp, entrust.getMarket(), entrust.getEntrustProp());

        // xiazelei 修改于 2022/6/24 17:38
        // 332647   深圳-债券现券交易协商委托
        // 332648   深圳-债券现券交易协商确认委托
        // 332400   债券质押协议回购初始交易	深交所质押式协议回购首期报单
        // 332401   债券质押协议回购购回交易	深交所质押式协议回购到期购回、提前购回报单
        if ("BondOrderSZ".equals(entrustProp) || "BondAgreeSZ".equals(entrustProp) || "RepurchaseSQSZ".equals(entrustProp) || "RepurchaseBuybackSZ".equals(entrustProp) || "RepurchaseRenewalSZ".equals(entrustProp) || "RepurchaseBuyBeforeSZ".equals(entrustProp) || "RepurchaseRemovePledgeSZ".equals(entrustProp) || "RepurchaseBondChangeSZ".equals(entrustProp) || "RepurchaseAgreeSZ".equals(entrustProp))
            newFlag = true;

        try {
            if (!newFlag) {
                TerminalAgentInfo agentInfo = terminalAgentInfoService.selectOne(getTerminalName(), functionCode, entrustProp);
                agentInfo.setAccountId(entrust.getAccountId());
                parameters.put("password", getAccount(agentInfo.getAccountId()).getTradePwd());
                resultBean = tradeAgentHelper.doRequest(agentInfo, parameters);

            } else {
                // region 参数组装
                // 自定义参数
                Map<String, Object> customParam = getCustomParam(entrust.getAccountId());
                // endregion
                // 332647   深圳-债券现券交易协商委托 柜台只返回 entrust_no一个字段
                // 332648   深圳-债券现券交易协商确认委托  柜台只返回 entrust_no一个字段
                resultBean = triggerCodeAdapter(functionCode, entrustProp, entrust, customParam);
            }

            Map<String, Object> ds = (Map<String, Object>) resultBean.getData();
            // 获取成功标识
            boolean successFlag = AgentUtil.getOrderSuccessFlagUFX(resultBean, ds);
            if (successFlag) {
                // 成功 已报entrust_price
                entrust.setBrokerEntrustId((String) ds.get("entrust_no"));
                String initDate = (String) ds.get("init_date");
                if (!Strings.isNullOrEmpty(initDate)) {
                    entrust.setTradingDay(initDate);
                }
                entrust.setErrorMsg(ErrorMsgUtils.subErrorMsg((String) ds.get("error_info")));

                //保存当前下单账号
                orderAccountMap.put(entrust.getAccountId(), entrust.getBrokerEntrustId());
            } else {
                // 失败 柜台废单 还原持仓
                // yanjianhui 修改于 2022/8/24 11:23  柜台报错返回空报文导致指令进度没有被释放处理。
                String errorMsg = null;
                if (ds != null) {
                    errorMsg = ErrorMsgUtils.subErrorMsg((String) ds.get("error_info"));
                } else {
                    errorMsg = resultBean.getErrorMsg();
                }
                entrust.setErrorMsg(errorMsg);
                entrust.setEntrustStatus(EntrustStatus.INVALID.value);
                entrust.setErrorCode(ErrorCode.INVALID.value);
                logger.warn("integratedEntrust failed ErrorNo:{}，reason:{}", resultBean.getErrorCode(), errorMsg);
            }
            TerminalId terminalId = new TerminalId();
            BeanUtils.copyProperties(entrust, terminalId);
            terminalId.KeyType = "I";
            onRspIntegratedEntrust(terminalId);
        } catch (Exception e) {
            logger.error("send integratedEntrust error:{},{}", entrust.getId(), e.getMessage(), e);
            throw new RuntimeException(ErrorCode.TERMINAL_API_ERROR.errorMsg);
        }
    }

    /**
     * 333017  委托撤单 用于竞价平台所有委托业务撤单
     */
    @Override
    public void cancelEntrust(Entrust entrust, EntrustInterface cancelEntrust) {
        CancelEntrustBean cancelEntrustBean = getCancelEntrustBean(entrust, cancelEntrust);
        int functionCode = FunctionCode.REQ_CANCEL_ENTRUST.value;
        // 333017  平台委托--委托撤单
        String entrustProp = "Bidding";
        try {
            // region 参数组装
            // 自定义参数
            Map<String, Object> customParam = getCustomParam(entrust.getAccountId());
            customParam.put("cancelId", cancelEntrust.getId());
            customParam.put("entrustDate", cancelEntrustBean.getTradingDay());
            // endregion
            ResultBean resultBean = triggerCodeAdapter(functionCode, entrustProp, entrust, customParam);
            Map<String, Object> ds = (Map<String, Object>) resultBean.getData();

            // 获取成功标识
            boolean successFlag = AgentUtil.getOrderSuccessFlagUFX(resultBean, ds);
            cancelEntrustBean.setAgentType(AgentType.UFX.value);
            if (successFlag) {
                cancelEntrustBean.setCancelBrokerEntrustId((String) ds.get("entrust_no"));
                // 成功 已报
                if ("Bidding".equals(entrustProp)) {
                    // 333017	委托撤单	用于竞价平台所有委托业务撤单
                    cancelEntrustBean.setTradingDay((String) ds.get("init_date"));
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

                //保存当前下单账号
                orderAccountMap.put(cancelEntrustBean.getAccountId(), cancelEntrustBean.getCancelBrokerEntrustId());

                //触发委托查询
                executeQueryWithCancelOrder(cancelEntrustBean.getAccountId(), QueryEnum.qryOrder);
            } else {
                // 失败 柜台废单
                // yanjianhui 修改于 2022/8/24 11:23  柜台报错返回空报文导致指令进度没有被释放处理。
                String errorMsg = null;
                if (ds != null) {
                    errorMsg = ErrorMsgUtils.subErrorMsg((String) ds.get("error_info"));
                } else {
                    errorMsg = resultBean.getErrorMsg();
                }
                cancelEntrustBean.setErrorCode(resultBean.getErrorCode());
                cancelEntrustBean.setEntrustStatus(EntrustStatus.INVALID.value);
                cancelEntrustBean.setErrorMsg(errorMsg);
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
     * xiazelei 修改于 2022/6/26 11:22
     * 332651	 深圳债券现券委托撤单
     */
    @Override
    public void cancelIntegratedEntrust(IntegratedEntrust integratedEntrust, IntegratedEntrust cancelEntrust) {
        CancelEntrustBean cancelEntrustBean = getCancelEntrustBean(integratedEntrust, cancelEntrust);
        // 场景代码
        int functionCode = FunctionCode.REQ_CANCEL_INTEGRATED_ENTRUST.value;
        // 场景属性
        String entrustProp = null;
        if (IntegratedEntrustUtils.isBulk(cancelEntrustBean.getEntrustProp())) {
            //大宗
            entrustProp = "Bulk";
        } else if (Market.SHSE.value.equals(cancelEntrustBean.getMarket()) && IntegratedEntrustUtils.isFixedIncome(cancelEntrustBean.getEntrustProp())) {
            //固收撤回
            entrustProp = "FixedIncome";
        } else if (IntegratedEntrustUtils.isRepurchaseSZ(cancelEntrustBean.getEntrustProp(), cancelEntrustBean.getEntrustBs(), cancelEntrustBean.getMarket())) {
            // 332402 债券质押式协议回购_深交所撤回
            entrustProp = "RepurchaseSZ";
        } else if (IntegratedEntrustUtils.isRepurchaseSH(cancelEntrustBean.getEntrustProp(), cancelEntrustBean.getEntrustBs(), cancelEntrustBean.getMarket())) {
            //债券质押式协议回购_上交所撤回
            entrustProp = "RepurchaseSH";
        } else if (Market.SZSE.value.equals(integratedEntrust.getMarket()) && EntrustProp.SZ_XSCJ_XD.value.equals(integratedEntrust.getEntrustProp())) {
            // xiazelei 修改于 2022/6/24 14:55
            //  332651 深圳债券现券委托撤单-- 332647   深圳-债券现券交易协商委托
            entrustProp = "BondOrderSZ";
        } else if (Market.SZSE.value.equals(integratedEntrust.getMarket()) && EntrustProp.SZ_XSCJ_JS.value.equals(integratedEntrust.getEntrustProp())) {
            // xiazelei 修改于 2022/6/24 14:55
            // 332651  深圳债券现券委托撤单--  332648   深圳-债券现券交易协商确认委托
            entrustProp = "BondAgreeSZ";
        }

        if (Strings.isNullOrEmpty(entrustProp)) {
            cancelEntrustBean.setEntrustStatus(EntrustStatus.INVALID.value);
            cancelEntrustBean.setErrorMsg("bsFlag没有对应的entrustProp");
            cancelEntrustBean.setErrorCode(ErrorCode.REQ_PARAMETER_ERROR.value);
            onRspCancelIntegratedEntrust(cancelEntrustBean);
            return;
        }

        try {
            // region 参数组装
            // 自定义参数
            Map<String, Object> customParam = getCustomParam(integratedEntrust.getAccountId());
            customParam.put("cancelId", cancelEntrust.getId());
            // endregion
            // 332651	 深圳债券现券委托撤单 柜台只返回 entrust_no一个字段
            ResultBean resultBean = triggerCodeAdapter(functionCode, entrustProp, integratedEntrust, customParam);

            Map<String, Object> ds = (Map<String, Object>) resultBean.getData();

            // 获取成功标识
            boolean successFlag = AgentUtil.getOrderSuccessFlagUFX(resultBean, ds);
            cancelEntrustBean.setAgentType(AgentType.UFX.value);
            if (successFlag) {
                // 成功 已报
                cancelEntrustBean.setCancelBrokerEntrustId((String) ds.get("entrust_no"));
                cancelEntrustBean.setErrorMsg(ErrorMsgUtils.subErrorMsg((String) ds.get("error_info")));

                //保存当前下单账号
                orderAccountMap.put(cancelEntrustBean.getAccountId(), cancelEntrustBean.getCancelBrokerEntrustId());

                //触发委托查询
                executeQueryWithCancelOrder(cancelEntrustBean.getAccountId(), QueryEnum.queryIntegratedOrder);
            } else {
                // 失败 柜台废单
                cancelEntrustBean.setEntrustStatus(EntrustStatus.INVALID.value);
                if (ds != null) cancelEntrustBean.setErrorMsg(ErrorMsgUtils.subErrorMsg((String) ds.get("error_info")));
                else cancelEntrustBean.setErrorMsg(resultBean.getErrorMsg());
                cancelEntrustBean.setErrorCode(resultBean.getErrorCode());
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
     * 332728	协议回购行情信息查询	上交所质押式协议回购公开行情、非公开行情查询
     */
    @Override
    public List<IntegratedQuote> reqIntegratedQuotes(QuoteQryBean quoteQryBean) throws Exception {
        // 场景代码
        int functionCode = FunctionCode.REQ_QRY_INTEGRATED_QUOTE.value;
        // 场景属性
        String entrustProp = null;

        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put("account", quoteQryBean.getAccountId());
        entrustProp = getEntrustProp(quoteQryBean, parameters);

        if (entrustProp == null) {
            logger.error("不支持查询该行情类型，quoteType：{}", quoteQryBean.getQuoteType());
            return Lists.newArrayList();
        }
        //  无效查询返回空记录
        if ("IntegratedQuote".equals(entrustProp) && Strings.isNullOrEmpty(quoteQryBean.getStockCode())) {
            return Lists.newArrayList();
        }
        TerminalAgentInfo agentInfo = terminalAgentInfoService.selectOne(getTerminalName(), functionCode, entrustProp);
        agentInfo.setAccountId(quoteQryBean.getAccountId());
        ResultBean resultBean = tradeAgentHelper.doRequest(agentInfo, parameters);
        return UfxMsgParseHelper.parseIntegratedQuotes(AgentUtil.getQueryListUFX(resultBean), quoteQryBean);
    }

    private String getEntrustProp(QuoteQryBean quoteQryBean, Map<String, Object> parameters) {
        String entrustProp = null;
        if (QuoteType.BULK_QUOTE.value == quoteQryBean.getQuoteType() || QuoteType.AFTER_BULK_QUOTE.value == quoteQryBean.getQuoteType()) {
            // 398大宗
            entrustProp = "IntegratedQuote";
            String market = StockCodeUtils.getMarket(quoteQryBean.getStockCode());
            parameters.put("exchange_type", AgentUtil.ExchangeMarketUFX(market));
            quoteQryBean.setMarket(market);
            parameters.put("cbphq_type", "00");
            parameters.put("stock_code", StockCodeUtils.getStockCode(quoteQryBean.getStockCode()));
        } else if (QuoteType.NON_PUBLIC_PROTOCOL_BACK_QUOTE.value == quoteQryBean.getQuoteType() || QuoteType.PUBLIC_PROTOCOL_BACK_QUOTE.value == quoteQryBean.getQuoteType()) {
            //  332728上海债券质押式协议回购行情信息查询
            entrustProp = "RepurchaseSH";
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
        return entrustProp;
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

        parameters.put("exchange_type", AgentUtil.ExchangeMarketUFX(market));

        //买
        if (EntrustBs.BUY.value.equals(entrustBs)) {
            parameters.put("entrust_price", qryBean.getEntrustPrice());
            parameters.put("entrust_prop", qryBean.getEntrustProp());
        }

        TerminalAgentInfo agentInfo = terminalAgentInfoService.selectOne(getTerminalName(), FunctionCode.REQ_QRY_TRADE_ENABLE.value, funcId);
        agentInfo.setAccountId(qryBean.getAccountId());
        ResultBean resultBean = tradeAgentHelper.doRequest(agentInfo, parameters);
        if (!AgentUtil.getQuerySuccessFlagUFX(resultBean)) {
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
        return UfxMsgParseHelper.parseHistoryTrade(AgentUtil.getQueryListUFX(resultBean), accountId);
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
        return UfxMsgParseHelper.parseCustomerFinance(AgentUtil.getQueryListUFX(resultBean), accountId);
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
        String market = StockCodeUtils.getMarket(positionQueryBean.getStockCode());
        parameters.put("exchange_type", AgentUtil.ExchangeMarketUFX(market));
        parameters.put("stock_code", StockCodeUtils.getStockCode(positionQueryBean.getStockCode()));
        //定位串
        parameters.put("position_str", "");
        if (!Strings.isNullOrEmpty(positionQueryBean.getPositionStr())) {
            parameters.put("position_str", positionQueryBean.getPositionStr());
        }

        TerminalAgentInfo agentInfo = terminalAgentInfoService.selectOne(getTerminalName(), FunctionCode.REQ_QRY_POSITION.value);
        agentInfo.setAccountId(accountId);
        ResultBean resultBean = tradeAgentHelper.doRequest(agentInfo, parameters);
        return UfxMsgParseHelper.parseAccountPosition(AgentUtil.getQueryListUFX(resultBean), accountId);
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
        parameters.put("exchange_type", AgentUtil.ExchangeMarketUFX(instrumentQryBean.getMarket()));
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
        return UfxMsgParseHelper.parseInstrumentInfo(AgentUtil.getQueryListUFX(resultBean));
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
                return Lists.newArrayList();
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
        parameters.put("exchange_type", AgentUtil.ExchangeMarketUFX(market));
        ResultBean resultBean = tradeAgentHelper.doRequest(agentInfo, parameters);
        return UfxMsgParseHelper.parseStandardBonds(AgentUtil.getQueryListUFX(resultBean), accountId);
    }

    /**
     * 398	大宗交易行情查询	查询大宗交易行情
     */
    @Override
    public List<IntegratedBulkQuoteBean> reqIntegratedBulkQuote(IntegratedBulkQuoteQryBean quoteQryBean) throws Exception {
        // 场景代码
        int functionCode = FunctionCode.REQ_QRY_INTEGRATED_QUOTE.value;
        // 场景属性
        String entrustProp = "IntegratedQuote";
        String accountId = quoteQryBean.getAccountId();
        TerminalAgentInfo agentInfo = terminalAgentInfoService.selectOne(getTerminalName(), functionCode, entrustProp);
        agentInfo.setAccountId(accountId);
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put("account", accountId);
        String market = "";
        String stockCode = quoteQryBean.getStockCode();
        market = StockCodeUtils.getMarket(stockCode);
        parameters.put("exchange_type", AgentUtil.ExchangeMarketUFX(market));
        parameters.put("stock_code", StockCodeUtils.getStockCode(stockCode));
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

    private List<IntegratedBulkQuoteBean> getQueryIntegratedBulkQuote(TerminalAgentInfo agentInfo, Map<String, Object> parameters, int pageSize, String positionStr, String market) {
        parameters.put("request_num", pageSize);
        parameters.put("position_str", positionStr);
        ResultBean resultBean = tradeAgentHelper.doRequest(agentInfo, parameters);
        return UfxMsgParseHelper.parseIntegratedBulkQuote(AgentUtil.getQueryListUFX(resultBean), market);
    }

    /**
     * 盘后大宗交易行情查询
     * 394	深圳盘后行情查询
     * 364	上海盘后固定价格交易行情查询
     */
    @Override
    public List<IntegratedBulkCloseQuoteBean> reqIntegratedBulkCloseQuote(IntegratedBulkCloseQuoteQryBean bulkCloseQuoteQryBean) throws Exception {
        List<IntegratedBulkCloseQuoteBean> list;
        // 场景代码
        int functionCode = FunctionCode.REQ_QRY_INTEGRATED_BULK_CLOSE_QUOTE.value;
        // 场景属性
        String entrustProp;

        // region 参数组装
        String market = StockCodeUtils.getMarket(bulkCloseQuoteQryBean.getStockCode());
        if ("SH".equals(market)) {
            // 364	上海盘后固定价格交易行情查询
            entrustProp = "IntegratedAfterQuoteSH";
        } else {
            // 394	深圳盘后行情查询
            entrustProp = "IntegratedAfterQuoteSZ";
        }

        // 自定义参数
        Map<String, Object> customParam = getCustomParam(bulkCloseQuoteQryBean.getAccountId());
        // endregion

        ResultBean resultBean = triggerCodeAdapter(functionCode, entrustProp, bulkCloseQuoteQryBean, customParam);
        // region 返回结果处理

        list = UfxMsgParseHelper.parseIntegratedBulkCloseQuote(AgentUtil.getQueryListUFX(resultBean), market);
        // endregion
        return list;
    }

    /**
     * 332728 协议回购行情查询
     */
    @Override
    public List<IntegratedRepurchaseQuoteBean> reqIntegratedRepurchaseQuote(IntegratedRepurchaseQuoteQryBean quoteQryBean) throws Exception {
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

    private List<IntegratedRepurchaseQuoteBean> getQueryIntegratedRepurchaseQuote(TerminalAgentInfo agentInfo, Map<String, Object> parameters, int pageSize, String positionStr) {
        //请求行数
        parameters.put("request_num", pageSize);
        //定位串
        parameters.put("position_str", positionStr);

        ResultBean resultBean = tradeAgentHelper.doRequest(agentInfo, parameters);
        return UfxMsgParseHelper.parseIntegratedRepurchaseQuote(AgentUtil.getQueryListUFX(resultBean));
    }

    @Override
    public List<RepurchaseQuoteReceiveResponse> reqIntegratedRepurchaseQuoteReceive(RepurchaseQuoteReceiveQryRequest bean) throws Exception {
        //待处理
        return ListUtil.list(false);
    }

    /**
     * 固收行情
     * 361 查询上海固收平台报价行情
     * --目前忽略此接口 386 债券现券逐笔委托行情查询
     */
    @Override
    public List<IntegratedFixedIncomeQuoteBean> reqIntegratedFixedIncomeQuote(IntegratedFixedIncomeQuoteQryBean fixedIncomeQuoteQryBean) throws Exception {
        // 场景代码
        int functionCode = FunctionCode.REQ_QRY_INTEGRATED_FIXED_INCOME_QUOTE.value;
        // 场景属性
        String entrustProp;

        String stockCode = fixedIncomeQuoteQryBean.getStockCode();
        //  无效查询返回空记录
        if (Strings.isNullOrEmpty(stockCode)) return Lists.newArrayList();

        String market = StockCodeUtils.getMarket(stockCode);

        if (market.equals(Market.SHSE.value)) {
            fixedIncomeQuoteQryBean.setExchangeType(ExchangeType.SH.value);
            // 361 查询上海固收平台报价行情
            entrustProp = "IntegratedBondQuoteSH";
        } else {
            fixedIncomeQuoteQryBean.setExchangeType(ExchangeType.SZ.value);
            // 386 债券现券逐笔委托行情查询
            entrustProp = "IntegratedBondQuoteSZ";
            // xiazelei 修改于 2022/7/6 17:57   深圳现券协商成交没有公开行情。返回空
            return Lists.newArrayList();
        }

        // region 参数组装
        // 自定义参数
        Map<String, Object> customParam = getCustomParam(fixedIncomeQuoteQryBean.getAccountId());
        // endregion

        ResultBean resultBean = triggerCodeAdapter(functionCode, entrustProp, fixedIncomeQuoteQryBean, customParam);
        return UfxMsgParseHelper.parseIntegratedFixedIncomeQuote(AgentUtil.getQueryListUFX(resultBean), entrustProp);
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

        // xiazelei 修改于 2022/6/30 17:17   移到接口平台配置
        // 固收 深圳and上海协议回购成交申报
        // String entrustProp = "BPN,BPR,BPT,BPB,BPH,BPI,FI1,FI3,FI9,a,b,c,e,AFW,AFC";
        // parameters.put("en_entrust_prop", entrustProp);
        parameters.put("request_num", String.valueOf(pageSize));
        parameters.put("query_type", "0");

        TerminalAgentInfo agentInfo = terminalAgentInfoService.selectOne(getTerminalName(), FunctionCode.REQ_QRY_TRADE.value, "ProtocolBack");
        agentInfo.setAccountId(accountId);
        ResultBean resultBean = tradeAgentHelper.doRequest(agentInfo, parameters);
        return UfxMsgParseHelper.qryProtocolBackTradeBack(accountId, AgentUtil.getQueryListUFX(resultBean));
    }

    @Override
    public void queryNextTrade() {
        //登录成功用户
        if (MapUtils.isEmpty(loginAccounts) || MapUtils.isEmpty(orderAccountMap)) {
            return;
        }

        //下单账号
        Set<String> orderAccounts = orderAccountMap.keySet();
        logger.info("queryNextTrade()，查询成交委托数据：orderAccounts={}", orderAccounts);
        for (String accountId : orderAccounts) {
            LoginInfo loginInfo = loginAccounts.get(accountId);
            logger.info("account：{}，status：{}", accountId, getStatus(accountId));
            boolean loginStatus = tradeAgentManager.getAgentLoginStatus(accountId);
            if (loginStatus) {
                executeQueryNextTrade(accountId, loginInfo);
            }
        }

//        for (Map.Entry<String, LoginInfo> entry : loginAccounts.entrySet()) {
//            String accountId = entry.getKey();
//            LoginInfo loginInfo = entry.getValue();
//            logger.info("account：{}，status：{}", accountId, getStatus(accountId));
////            if (getStatus(accountId) != AccountLoginStatus.TRADING.value) {
////                return;
////            }
//            boolean loginStatus = tradeAgentManager.getAgentLoginStatus(accountId);
//            if (loginStatus) {
//                executeQueryNextTrade(accountId, loginInfo);
//            }
//        }
    }

    private void executeQueryWithCancelOrder(String accountId, QueryEnum queryEnum) {
        logger.info("【撤销委托】触发委托状态查询：accountId={},queryEnum={}", accountId, queryEnum);
        if (null == poolExecutor) {
            poolExecutor = new ThreadPoolExecutor(8, 20, 1000, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>(100), new ThreadPoolExecutor.DiscardPolicy());
            poolExecutor.prestartAllCoreThreads();
        }

        try {
            poolExecutor.execute(new QueryTask(queryEnum, loginAccounts.get(accountId), accountId, cancelOrderInterval));
        } catch (Exception e) {
            logger.error("{}执行异常：{}", queryEnum.name(), e.getMessage(), e);
        }
    }

    private ThreadPoolExecutor poolExecutor = null;

    private void executeQueryNextTrade(String accountId, LoginInfo loginInfo) {
        if (null == poolExecutor) {
            poolExecutor = new ThreadPoolExecutor(8, 20, 1000, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>(100), new ThreadPoolExecutor.DiscardPolicy());
            poolExecutor.prestartAllCoreThreads();
        }

        List<QueryEnum> list = Lists.newArrayList(QueryEnum.qryTrade, QueryEnum.qryProtocolBackTrade);
        for (QueryEnum queryEnum : list) {
            try {
                poolExecutor.execute(new QueryTask(queryEnum, loginInfo, accountId));
            } catch (Exception e) {
                logger.error("{}执行异常：{}", queryEnum.name(), e.getMessage(), e);
            }
        }
    }

    enum QueryEnum {
        qryOrder, queryIntegratedOrder, qryTrade, qryProtocolBackTrade;
    }

    class QueryTask implements Runnable {
        private QueryEnum queryEnum;
        private LoginInfo loginInfo;
        private String accountId;
        private long sleep;

        public QueryTask(QueryEnum queryEnum, LoginInfo loginInfo, String accountId) {
            this.queryEnum = queryEnum;
            this.loginInfo = loginInfo;
            this.accountId = accountId;
        }

        public QueryTask(QueryEnum queryEnum, LoginInfo loginInfo, String accountId, long sleep) {
            this.queryEnum = queryEnum;
            this.loginInfo = loginInfo;
            this.accountId = accountId;
            this.sleep = sleep;
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
                        // 332704综合委托查询
                        queryIntegratedOrder(loginInfo, accountId);
                        return;
                    case qryTrade:
                        qryOrder(loginInfo, accountId);
                        queryIntegratedOrder(loginInfo, accountId);
                        qryTrade(loginInfo, accountId);
                        return;
                    case qryProtocolBackTrade: {
                        queryIntegratedOrder(loginInfo, accountId);
                        // qryProtocolBackTradeSZ(loginInfo, accountId);
                        // 332705 综合成交查询
                        qryProtocolBackTradeSH(loginInfo, accountId);
                        return;
                    }
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
        return UfxMsgParseHelper.parseEntrust(accountId, AgentUtil.getQueryListUFX(resultBean));
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
        // logger.debug("ReqQueryIntegrateOrder reback : {}", JSONArray.toJSONString(reList));
        return reList;
    }

    private List<IntegratedEntrust> getIntegrateOrderReBack(String accountId, int pageSize, String positionStr) {
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put("account", accountId);
        // xiazelei 修改于 2022/6/30 17:17   移到接口平台配置
        // FI1（确定报价），FI3（点击成交），FI9（指定对手方报价）
        // ‘a,b,c,e,AFW,AFC’即大宗交易
        // parameters.put("en_entrust_prop", "BPN,BPR,BPT,BPB,BPH,BPI,FI1,FI3,FI9,a,b,c,e,AFW,AFC");
        parameters.put("request_num", String.valueOf(pageSize));
        parameters.put("position_str", positionStr);
        TerminalAgentInfo agentInfo = terminalAgentInfoService.selectOne(getTerminalName(), FunctionCode.REQ_QRY_ENTRUST.value, "ProtocolBack");
        agentInfo.setAccountId(accountId);
        ResultBean resultBean = tradeAgentHelper.doRequest(agentInfo, parameters);
        return UfxMsgParseHelper.parseIntegratedEntrust(accountId, AgentUtil.getQueryListUFX(resultBean));
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
                trade.KeyType = "E";
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
            // parameters.put("position_str", positionStr.getTradePositionStr());
        }
        // 国海证券 position_str 与广发证券的逻辑不同有问题。暂时先注释
        parameters.put("position_str", "");
        parameters.put("query_direction", "0");
        parameters.put("request_num", "1000");
        parameters.put("query_type", "0");
        parameters.put("query_mode", "0");

        TerminalAgentInfo agentInfo = terminalAgentInfoService.selectOne(getTerminalName(), FunctionCode.REQ_QRY_TRADE.value, "Bidding");
        agentInfo.setAccountId(accountId);
        ResultBean resultBean = tradeAgentHelper.doRequest(agentInfo, parameters);
        return UfxMsgParseHelper.parseTrades(accountId, AgentUtil.getQueryListUFX(resultBean));
    }

    /**
     * 332403债券质押协议回购委托查询 深圳
     */
    public void qryProtocolBackTradeSZ(LoginInfo loginInfo, String accountId) {
        PositionStr positionStr = positionStrMap.get(loginInfo.getFundAccount());

        List<Trade> list = getProtocolBackTrade(accountId, positionStr, "ProtocolBack");
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

    /**
     * 332404 债券质押协议回购合约查询 深圳
     */
    public void qryContractTradeSZ(LoginInfo loginInfo, String accountId) {
        // yanjianhui 修改于 2022/8/1 10:25 还未实现。
        PositionStr positionStr = positionStrMap.get(loginInfo.getFundAccount());

        List<Trade> list = getProtocolBackTrade(accountId, positionStr, "ProtocolBack");
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
                trade.KeyType = "I";
                onRtnTrade(trade);
            }
        });
    }

    /**
     * 332705综合业务成交查询  上海  深圳  债券质押协议回购成交查询 固收成交查询 大宗
     * 成交查询 里包含撤单和普通委托的成功记录需要注意
     */
    public void qryProtocolBackTradeSH(LoginInfo loginInfo, String accountId) {
        PositionStr positionStr = positionStrMap.get(loginInfo.getFundAccount());

        List<Trade> list = getProtocolBackTrade(accountId, positionStr, "ProtocolBack");
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
                trade.KeyType = "I";
                onRtnTrade(trade);
            }
        });
    }

    private List<Trade> getProtocolBackTrade(String accountId, PositionStr positionStr, String entrustProp) {
        Map<String, String> parameters = Maps.newHashMap();
        parameters.put("accountId", accountId);
        parameters.put("account", accountId);
        parameters.put("position_str", "");
        if (!Objects.isNull(positionStr) && !Objects.isNull(positionStr.getProtocolBackPositionStr())) {
            parameters.put("position_str", positionStr.getProtocolBackPositionStr());
        }
        // xiazelei 修改于 2022/6/30 17:17   移到接口平台配置
        // 固收 深圳and上海协议回购成交申报
        // String entrustProp = "BPN,BPR,BPT,BPB,BPH,BPI,FI1,FI3,FI9,a,b,c,e,AFW,AFC";
        // parameters.put("en_entrust_prop", entrustProp);
        parameters.put("request_num", "1000");
        parameters.put("query_type", "0");

        TerminalAgentInfo agentInfo = terminalAgentInfoService.selectOne(getTerminalName(), FunctionCode.REQ_QRY_TRADE.value, entrustProp);
        agentInfo.setAccountId(accountId);
        ResultBean resultBean = tradeAgentHelper.doRequest(agentInfo, parameters);
        return UfxMsgParseHelper.qryProtocolBackTradeBack(accountId, AgentUtil.getQueryListUFX(resultBean));
    }

    private String getIntegratedEntrustProp(IntegratedEntrust entrust, Map<String, Object> parameters) {
        if (IntegratedEntrustUtils.isBulk(entrust.getEntrustProp())) {
            //大宗 332702
            bulkIntegratedEntrust(entrust, parameters);
            return "Bulk";
        } else if (Market.SHSE.value.equals(entrust.getMarket()) && IntegratedEntrustUtils.isFixedIncome(entrust.getEntrustProp())) {
            //固收委托确认 332722
            fixedIncomeEntrust(entrust, parameters);
            return "FixedIncome";
        } else if (IntegratedEntrustUtils.isRepurchaseSQSZ(entrust.getEntrustProp(), entrust.getEntrustBs(), entrust.getMarket())) {
            //债券质押式协议回购_首期_深交所 332400
            return "RepurchaseSQSZ";
        } else if (IntegratedEntrustUtils.isRepurchaseBuybackSZ(entrust.getEntrustProp(), entrust.getMarket())) {
            //深圳债券质押协议回购购回交易 332401
            return "RepurchaseBuybackSZ";
        } else if (IntegratedEntrustUtils.isRepurchaseRenewalSZ(entrust.getEntrustProp(), entrust.getMarket())) {
            //深圳债券质押协议回购到期续做 332410
            return "RepurchaseRenewalSZ";
        } else if (Market.SZSE.value.equals(entrust.getMarket()) && EntrustProp.SZ_REPURCHASE_BUYBEFORE.value.equals(entrust.getEntrustProp())) {
            //债券质押协议回购提前购回委托  332434
            return "RepurchaseBuyBeforeSZ";
        } else if (Market.SZSE.value.equals(entrust.getMarket()) && EntrustProp.SZ_REPURCHASE_BOND_CHANGE.value.equals(entrust.getEntrustProp())) {
            //债券质押协议回购质押券变更委托  332435
            return "RepurchaseBondChangeSZ";
        } else if (Market.SZSE.value.equals(entrust.getMarket()) && EntrustProp.SZ_REPURCHASE_REMOVE_PLEDGE.value.equals(entrust.getEntrustProp())) {
            //债券质押协议回购解除质押委托  332436
            return "RepurchaseRemovePledgeSZ";
        } else if (Market.SZSE.value.equals(entrust.getMarket()) && EntrustProp.SZ_REPURCHASE_AGREE.value.equals(entrust.getEntrustProp())) {
            //债券质押协议回购逆回购方确认委托  332437
            return "RepurchaseAgreeSZ";
        } else if (IntegratedEntrustUtils.isRepurchaseSH(entrust.getEntrustProp(), entrust.getEntrustBs(), entrust.getMarket())) {
            //债券质押式协议回购_上交所 332725
            repurchaseSH(entrust, parameters);
            return "RepurchaseSH";
        } else if (Market.SZSE.value.equals(entrust.getMarket()) && EntrustProp.SZ_XSCJ_XD.value.equals(entrust.getEntrustProp())) {
            // xiazelei 修改于 2022/6/24 14:55
            // 332647   深圳-债券现券交易协商委托
            return "BondOrderSZ";
        } else if (Market.SZSE.value.equals(entrust.getMarket()) && EntrustProp.SZ_XSCJ_JS.value.equals(entrust.getEntrustProp())) {
            // xiazelei 修改于 2022/6/24 14:55
            // 332648   深圳-债券现券交易协商确认委托
            return "BondAgreeSZ";
        }
        return null;
    }

    private void bulkIntegratedEntrust(IntegratedEntrust integratedEntrust, Map<String, Object> parameters) {
        parameters.put("exchange_type", AgentUtil.ExchangeMarketUFX(integratedEntrust.getMarket()));
        if (FullPriceConst.FULL_PRICE.value.equals(integratedEntrust.getIsFullPrice())) {
            parameters.put("entrust_price", integratedEntrust.getFullPrice().toPlainString());
        } else {
            parameters.put("entrust_price", integratedEntrust.getEntrustPrice().toPlainString());
        }
        parameters.put("entrust_prop", integratedEntrust.getEntrustProp());
        parameters.put("entrust_amount", integratedEntrust.getEntrustVolume());
        parameters.put("entrust_bs", integratedEntrust.getEntrustBs());
        //意向申报/定价/成交申报卖出
        if (!((EntrustBs.SELL.value.equals(integratedEntrust.getEntrustBs()) && (EntrustProp.CONFIRM.value.equals(integratedEntrust.getEntrustProp()) || EntrustProp.SET_PRICE.value.equals(integratedEntrust.getEntrustProp()) || EntrustProp.INTENTION.value.equals(integratedEntrust.getEntrustProp()))) || (EntrustBs.BUY.value.equals(integratedEntrust.getEntrustBs()) && (EntrustProp.LARGE_AFTER_CLOSING_WEIGHT_PRICE.value.equals(integratedEntrust.getEntrustProp()))))) {
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
        parameters.put("exchange_type", AgentUtil.ExchangeMarketUFX(integratedEntrust.getMarket()));
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
            // 兼容产品和项目，旧交易对手方取不到，取新的
            parameters.put("oppo_trader_id", StringUtils.isEmpty(integratedEntrust.getLiaisonName()) ? integratedEntrust.getPropTradeId() : integratedEntrust.getLiaisonName());
            parameters.put("oppo_agency", integratedEntrust.getPropSeatNo());
            parameters.put("cbpconfer_id", integratedEntrust.getContractId());
        } else if (IntegratedEntrustUtils.isFixedIncome_ChenJiao(integratedEntrust.getEntrustProp()) || IntegratedEntrustUtils.isFixedIncome_BaoJia(integratedEntrust.getEntrustProp())) {
            //点击成交申报 最优价成交 可转换成交  定价申报
            parameters.put("trader_id", integratedEntrust.getLiaisonTel());
        } else {
            parameters.put("oppo_seatno", integratedEntrust.getPropSeatNo());
        }
    }

    protected void repurchaseSH(IntegratedEntrust integratedEntrust, Map<String, Object> parameters) {
        parameters.put("exchange_type", AgentUtil.ExchangeMarketUFX(integratedEntrust.getMarket()));
        parameters.put("entrust_amount", integratedEntrust.getEntrustVolume());
        parameters.put("entrust_bs", integratedEntrust.getEntrustBs());
        //债券质押式协议回购_上交所_确认/拒绝
        if (IntegratedEntrustUtils.isRepurchase_SH_ConfirmOrRefuse(integratedEntrust.getEntrustProp(), integratedEntrust.getEntrustBs(), integratedEntrust.getMarket())) {
            parameters.put("cbpconfer_id", integratedEntrust.getContractId());
        }
        // xiazelei 修改于 2022/4/22 10:37 修改所有的.doubleValue() 科学计数法有问题
        parameters.put("entrust_balance", integratedEntrust.getEntrustBalance().toPlainString());
        parameters.put("repurchase_date", integratedEntrust.getBackDate());
        parameters.put("expire_year_rate", convertExpireYearRate(integratedEntrust.getEntrustPrice()));
        parameters.put("entrust_prop", integratedEntrust.getEntrustProp());
        //到期续做
        if (IntegratedEntrustUtils.isRepurchaseRenewalSH(integratedEntrust.getEntrustProp(), integratedEntrust.getEntrustBs(), integratedEntrust.getMarket())) {
            //协议回购质权人类型
            parameters.put("bprsrc_status", integratedEntrust.getTransCode());
        }
        //非首期
        if (IntegratedEntrustUtils.isRepurchaseUNSQSH(integratedEntrust.getEntrustProp(), integratedEntrust.getEntrustBs(), integratedEntrust.getMarket())) {
            //原约定号
            parameters.put("orig_cbpconfer_id", integratedEntrust.getOrigSerialNo());
            //原申请日期
            parameters.put("orig_entrust_date", integratedEntrust.getFirstSettleDate());
        }
        parameters.put("oppo_agency_name", integratedEntrust.getDealerName());
        // yanjianhui 修改于 2022/9/2 15:39 交易商代码段为DealerId字段。此判断有待商榷。
        if (integratedEntrust.getSellerNo() == null) {
            parameters.put("agency_no", integratedEntrust.getDealerId());
        } else {
            parameters.put("agency_no", integratedEntrust.getSellerNo().substring(1, 4));
        }
        parameters.put("trader_id", integratedEntrust.getTradeId());
        parameters.put("oppo_agency", integratedEntrust.getPropDealerId());
        //借用成交标号 处理对方交易员
        parameters.put("oppo_trader_id", integratedEntrust.getPropTradeId());
        if (!Strings.isNullOrEmpty(integratedEntrust.getRemark())) {
            parameters.put("remark", integratedEntrust.getRemark());
        }
    }

    private static String convertExpireYearRate(BigDecimal entrustPrice) {
        // xiazelei 修改于 2022/4/22 10:37  保存5位小数
        return TradeUtils.divide(entrustPrice, 100, 5).toPlainString();
    }

    @Override
    public List<NegotiateQuoteReceiveBean> reqNegotiateQuoteReceive(NegotiateQuoteQueryBean queryBean) throws Exception {
        List<NegotiateQuoteReceiveBean> list;
        // 332653   债券现券转发申报信息查询 深圳协商成交 接收对方发起的报文
        // 场景代码
        int functionCode = FunctionCode.REQ_QRY_INTEGRATED_NEGOTIATE_QUOTE_RECEIVE.value;
        // 场景属性
        String entrustProp = "BondConsultQuerySZ";
        // region 参数组装
        // 自定义参数
        Map<String, Object> customParam = getCustomParam(queryBean.getAccountId());
        // endregion
        // 332653   债券现券转发申报信息查询
        ResultBean resultBean = triggerCodeAdapter(functionCode, entrustProp, queryBean, customParam);
        // region 返回结果处理
        list = UfxMsgParseHelper.parseNegotiateQuoteReceiveBean(queryBean.getAccountId(), queryBean.getSide(), AgentUtil.getQueryListUFX(resultBean));
        // endregion
        return list;
    }

}
