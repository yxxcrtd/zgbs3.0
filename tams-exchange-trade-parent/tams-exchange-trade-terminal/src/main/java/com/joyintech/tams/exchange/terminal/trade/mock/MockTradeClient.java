package com.joyintech.tams.exchange.terminal.trade.mock;

import cn.hutool.core.convert.Convert;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.tams.exchange.common.bean.*;
import com.joyintech.tams.exchange.common.bean.bond.StandardBondQryBean;
import com.joyintech.tams.exchange.common.constant.*;
import com.joyintech.tams.exchange.common.db.domain.*;
import com.joyintech.tams.exchange.common.enums.invest.InvestAssetTypeEnum;
import com.joyintech.tams.exchange.common.net.agent.Agent;
import com.joyintech.tams.exchange.common.utils.*;
import com.joyintech.tams.exchange.terminal.agent.ITerminalMsgNotify;
import com.joyintech.tams.exchange.terminal.agent.TerminalAgent;
import com.joyintech.tams.exchange.terminal.constant.AccountLoginStatus;
import com.joyintech.tams.exchange.terminal.trade.ITradeRequest;
import com.joyintech.tams.exchange.terminal.trade.TradeResponseImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * create By MAXX on 2020/6/8
 */
@Component("mock")
@ConditionalOnProperty(value = "agent.type", matchIfMissing = false, havingValue = "mock")
public class MockTradeClient implements ITradeRequest {
    private static final Logger logger = LoggerFactory.getLogger(MockTradeClient.class);
    private static Map<String, EntrustInterface> entrustMap = Maps.newConcurrentMap();//可撤单的委托
    private static Map<String, Entrust> entrustFutureMap = Maps.newConcurrentMap();//可撤单的委托 -- 期货
    private static List<Trade> tradeArray = Lists.newArrayList();//已成部成

    //private static Map<String, EntrustInterface> EntrustMap = Maps.newConcurrentMap();//entrustId-Entrust 已报部成
    private static Agent agent = null;
    @Autowired
    private TradeResponseImpl mockTradeResponse;
    private AtomicInteger brokerTradeId = new AtomicInteger(0);
    private static List<IntegratedEntrust> integratedEntrustQuoteList = new ArrayList<>();//可查询行情map
    private static Map<String, Trade> repurchaseTradeMap = new HashMap(); //协议回购成交map
    @Autowired
    private TradeResponseImpl tradeResponse;
    private ITerminalMsgNotify iTerminalMsgNotify;

    @Autowired
    protected RestTemplate restTemplate;

    private static String URL = "http://127.0.0.1:8080/tams/service/joyin/quote/tick";

    @Override
    public Set<String> getLoginAccount() {
        return new HashSet<>();
    }

    public void setITerminalMsgNotify(ITerminalMsgNotify iTerminalMsgNotify) {
        this.iTerminalMsgNotify = iTerminalMsgNotify;
    }

    @Override
    public void reqLogin(Account account, ServerExtendInfo serverExtendInfo) {
        logger.info("==============login test==============");
        logger.info("account: {}", JSONObject.toJSONString(account));
        mockTradeResponse.resLogin(new ResultBean(ErrorCode.SUCCESS, account), account.getId());
    }

    @Override
    public void reqEntrusts(String accountId, Long requestId) {
    }

    @Override
    public void init(TerminalAgent agent, ServerExtendInfo serverExtendInfo, ITerminalMsgNotify iTerminalMsgNotify) {
        logger.info("mock init start begin");
        this.agent = agent;
        tradeResponse.setTerminalAgent(agent);
        this.iTerminalMsgNotify = iTerminalMsgNotify;
    }

    @Override
    public void reqTrades(String accountId, Long requestId) {
        mockTradeResponse.resQueryTrade(tradeArray, requestId);
    }

    /***
     * @Description: 普通下单
     * @Param: [entrust, requestId]
     * @return: void
     * @Author: MAXX
     * @Date: 2020/6/8
     */
    @Override
    public void entrust(Entrust entrust, Long requestId) {
        if (InvestAssetTypeEnum.FUTURES.value.equals(entrust.getAssetType())) {
            logger.info("期货交易mock 开始 {}", JsonUtils.getJsonFromObject(entrust));
            mockFuturesEntrust(entrust);
            logger.info("... 期货交易mock 结束 ...");
            return;
        }

        logger.info("竞价交易mock 开始:{}", JsonUtils.getJsonFromObject(entrust));
        if (entrust.getEntrustVolume() % 900 == 0) {
            entrust.setErrorCode(ErrorCode.TERMINAL_RETURN_ERROR.value);
            entrust.setEntrustStatus(EntrustStatus.INVALID.value);
            entrust.setErrorMsg("数量为900的倍数，模拟下单失败，废单");
            mockTradeResponse.resEntrust(entrust);
            return;
        }
        entrust.setBrokerEntrustId(entrust.getId().substring(2));
        mockTradeResponse.resEntrust(entrust);
        if (EntrustUtils.isNoTrade(entrust.getEntrustProp())) {
            logger.info("回报即成交业务,不可撤单");
            entrust.setEntrustStatus(EntrustStatus.REPORTED.value);
            entrustMap.put(entrust.getId(), entrust);
            onEntrust(entrust);
            return;
        }
        if (entrust.getEntrustVolume() % 7 == 0
                || EntrustProp.NORMAL_AFTER_SET_PRICE.value.equals(entrust.getEntrustProp())) {
            logger.info("数量是7的倍数或者为盘后定价申报，模拟没有成交");
            entrust.setEntrustStatus(EntrustStatus.REPORTED.value);
            entrustMap.put(entrust.getId(), entrust);
            onEntrust(entrust);
            return;
        }

        //质押券出入库
        if ("EX005".equals(entrust.getBusiTypeTg())) {
            long tradeVolume;
            //全部成交 数量是300的倍数全部成交
            if (entrust.getEntrustVolume() % 300 == 0) {
                entrust.setEntrustStatus(EntrustStatus.SUCCEEDED.value);
                tradeVolume = entrust.getEntrustVolume();
            } else {
                tradeVolume = entrust.getEntrustVolume() / 2;
                entrust.setEntrustStatus(EntrustStatus.PART_SUCCESS.value);
                entrust.setTradedVolume(tradeVolume);
                entrustMap.put(entrust.getId(), entrust);
            }
            Trade trade = getTrade(entrust, Calendar.getInstance(), TradeType.NORMAL.value);
            trade.setTradedPrice(entrust.getEntrustPrice());
            trade.setTradedVolume(tradeVolume);
            trade.setTradedBalance(entrust.getEntrustPrice().multiply(BigDecimal.valueOf(tradeVolume)));
            onEntrust(entrust);
            tradeArray.add(trade);
            onTrade(trade);
            return;
        }

        int tradeVolume;
        if (entrust.getEntrustVolume() % 300 == 0) {//全部成交 数量是300的倍数全部成交
            entrust.setEntrustStatus(EntrustStatus.SUCCEEDED.value);
            tradeVolume = (int) entrust.getEntrustVolume();
        } else {
            tradeVolume = (int) entrust.getEntrustVolume() / 2;
            entrust.setEntrustStatus(EntrustStatus.PART_SUCCESS.value);
            entrust.setTradedVolume(tradeVolume);
            entrustMap.put(entrust.getId(), entrust);
        }
        List<Trade> list = genTradeList(entrust, tradeVolume, 0.00);
        onEntrust(entrust);
        tradeArray.addAll(list);
        list.forEach(this::onTrade);
    }

    private void mockFuturesEntrust(Entrust entrust) {
        if (entrust.getEntrustVolume() == 3) {
            entrust.setErrorCode(ErrorCode.TERMINAL_RETURN_ERROR.value);
            entrust.setEntrustStatus(EntrustStatus.INVALID.value);
            entrust.setErrorMsg("委托为3，模拟下单失败，废单");
            mockTradeResponse.resEntrust(entrust);
            return;
        }
        entrust.setBrokerEntrustId(entrust.getId().substring(2));
        if (entrust.getEntrustVolume() % 7 == 0) {
            logger.info("数量是7的倍数，模拟没有成交");
            entrust.setEntrustStatus(EntrustStatus.REPORTED.value);
            entrustMap.put(entrust.getId(), entrust);
            mockTradeResponse.resEntrust(entrust);
            onEntrust(entrust);
            return;
        }

        int tradeVolume;
        if (entrust.getEntrustVolume() % 5 == 0 || entrust.getEntrustVolume() == 1) {
            entrust.setEntrustStatus(EntrustStatus.SUCCEEDED.value);
            tradeVolume = (int) entrust.getEntrustVolume();
        } else {
            tradeVolume = (int) entrust.getEntrustVolume() / 2;
            entrust.setEntrustStatus(EntrustStatus.PART_SUCCESS.value);
            entrust.setTradedVolume(tradeVolume);
            entrustMap.put(entrust.getId(), entrust);
        }
        List<Trade> list = genTradeList(entrust, tradeVolume, 0.00);
        mockTradeResponse.resEntrust(entrust);
        onEntrust(entrust);
        tradeArray.addAll(list);
        list.forEach(this::onTrade);
    }

    private List<Trade> genTradeList(EntrustInterface entrust, int entrustVolume, double tradeBalance) {
        Calendar now = Calendar.getInstance();
        List<Trade> list = new ArrayList<>();
        int tradeCount = 0;
        while (entrustVolume > 0) {
            Trade trade = getTrade(entrust, now, TradeType.NORMAL.value);
            logger.info("entrustVolume:{}", entrustVolume);
            if (entrustVolume <= 200
                    || IntegratedEntrustUtils.isFixedIncome(entrust.getEntrustProp())
                    || IntegratedEntrustUtils.isProtocolBack_SH(entrust.getEntrustProp())
                    || IntegratedEntrustUtils.isProtocolBack_SZ(entrust.getEntrustProp())) {//不分笔成交
                trade.setTradedVolume(entrustVolume);
                if (tradeBalance != 0) {
                    trade.setTradedBalance(BigDecimal.valueOf(tradeBalance));
                }
                list.add(trade);
                entrustVolume = 0;
                continue;
            }
            //固定分三笔成交
            int TradedVolume = ++tradeCount < 3 ? entrustVolume / 3 : entrustVolume;
            entrustVolume = entrustVolume - TradedVolume;
            trade.setTradedVolume(TradedVolume);
            list.add(trade);
        }
        logger.info("trade list:{}", JSONArray.toJSONString(list));
        return list;
    }

    private Trade getTrade(EntrustInterface entrust, Calendar now, int tradeType) {
        Trade trade = new Trade();
        trade.setAccountId(entrust.getAccountId());
        trade.setTradingDay(entrust.getTradingDay());
        trade.setMarket(entrust.getMarket());
        trade.setFinanceAccount(entrust.getFinanceAccount());
        trade.setStockCode(StockCodeUtils.getStockCode(entrust.getStockCode()));
        trade.setEntrustBs(entrust.getEntrustBs());
        trade.setIsFullPrice(entrust.getIsFullPrice());
        //全价
        if (FullPriceConst.FULL_PRICE.value.equals(entrust.getIsFullPrice())) {
            trade.setTradedPrice(entrust.getFullPrice());
        } else {
            trade.setTradedPrice(entrust.getEntrustPrice());
        }
        trade.setBsFlag(entrust.getBsFlag());
        trade.setAssetType(entrust.getAssetType());
        trade.setTradeType(tradeType);
        trade.setBrokerEntrustId(entrust.getBrokerEntrustId());
        trade.setStockName(entrust.getStockName());
        trade.setEntrustProp(entrust.getEntrustProp());
        if (InvestAssetTypeEnum.FUTURES.value.equals(entrust.getAssetType())) {
            trade.setOffsetFlag(entrust.getOffsetFlag());
            trade.setHedgeFlag(entrust.getHedgeFlag());
        }
        trade.setLocalEntrustId(entrust.getLocalEntrustId());
        trade.setEntrustId(entrust.getId());
        trade.setTradeTime(now.get(Calendar.HOUR_OF_DAY) + "" + now.get(Calendar.MINUTE) + "" + now.get(Calendar.SECOND));
        trade.setBrokerTradeId(trade.getTradeTime() + brokerTradeId.incrementAndGet());
        return trade;
    }

    /***
     * @Description: 大宗下单
     * @Param: [integratedEntrust, requestId]
     * @return: void
     * @Author: MAXX
     * @Date: 2020/6/10
     */
    @Override
    public void integratedEntrust(IntegratedEntrust integratedEntrust, Long requestId) {
        if (IntegratedEntrustUtils.isBulk(integratedEntrust.getEntrustProp())) {
            //            if (Market.SHSE.value.equals(integratedEntrust.getMarket())) {
            //                if (integratedEntrust.getAssetType().equals(InvestAssetTypeEnum.STOCK.value)) {
            //                    handleIntegratedEntrustByAssetType(integratedEntrust, 300000.00, 2000000.00);
            //                } else if (integratedEntrust.getAssetType().equals(InvestAssetTypeEnum.BOND.value)) {
            //                    handleIntegratedEntrustByAssetType(integratedEntrust, 10000.00, 1000000.00);
            //                } else if (integratedEntrust.getAssetType().equals(InvestAssetTypeEnum.FUND.value)) {
            //                    handleIntegratedEntrustByAssetType(integratedEntrust, 300000.00, 300000.00);
            //                }
            //            } else {
            //                if (integratedEntrust.getAssetType().equals(InvestAssetTypeEnum.STOCK.value)) {
            //                    handleIntegratedEntrustByAssetType(integratedEntrust, 300000.00, 2000000.00);
            //                } else if (integratedEntrust.getAssetType().equals(InvestAssetTypeEnum.BOND.value)) {
            //                    handleIntegratedEntrustByAssetType(integratedEntrust, 5000.00, 500000.00);
            //                } else if (integratedEntrust.getAssetType().equals(InvestAssetTypeEnum.FUND.value)) {
            //                    handleIntegratedEntrustByAssetType(integratedEntrust, 2000000.00, 2000000.00);
            //                }
            //            }
            if (mockIntegratedEntrustFailByEntrustVolume(integratedEntrust))
                return;
            if (IntegratedEntrustUtils.isBulk(integratedEntrust.getEntrustProp())) {//大宗
                integratedEntrust.setBrokerEntrustId(integratedEntrust.getId().substring(2));
                mockTradeResponse.resIntegratedEntrust(integratedEntrust);
                logger.info("=========大宗==========");
                if (EntrustProp.INTENTION.value.equals(integratedEntrust.getEntrustProp())) {
                    logger.info("=========意向委托==========");
                    integratedEntrust.setEntrustStatus(EntrustStatus.REPORTED.value);
                    entrustMap.put(integratedEntrust.getId(), integratedEntrust);
                    integratedEntrustQuoteList.add(integratedEntrust);
                    onIntegratedEntrust(integratedEntrust);
                    return;
                }
                if (EntrustProp.SET_PRICE.value.equals(integratedEntrust.getEntrustProp())) {
                    logger.info("=========定价委托==========");
                    integratedEntrustQuoteList.add(integratedEntrust);
                }
                if (mockNoTradeByEntrustVolume(integratedEntrust))
                    return;
                mockTradeOrPartTradeByEntrustVolume(integratedEntrust);
            }
        } else if (IntegratedEntrustUtils.isFixedIncome(integratedEntrust.getEntrustProp())) {
            //            if (!Market.SHSE.value.equals(integratedEntrust.getMarket())
            //                    || !InvestAssetTypeEnum.BOND.value.equals(integratedEntrust.getAssetType())) {
            //                logger.info("固收平台不支持该市场合约 {}", integratedEntrust.getStockCode());
            //                integratedEntrust.setEntrustStatus(EntrustStatus.INVALID.value);
            //                integratedEntrust.setErrorMsg("固收平台不支持该市场合约" + integratedEntrust.getStockCode());
            //                integratedEntrust.setErrorCode(ErrorCode.TERMINAL_RETURN_ERROR.value);
            //                mockTradeResponse.resIntegratedEntrust(integratedEntrust);
            //                return;
            //            }
            //            if (!Strings.isNullOrEmpty(integratedEntrust.getPropSeatNo()) && integratedEntrust.getPropSeatNo().length() != 3) {
            //                logger.info("对手方交易商代码必须为3位");
            //                integratedEntrust.setEntrustStatus(EntrustStatus.INVALID.value);
            //                integratedEntrust.setErrorMsg("对手方交易商代码必须为3位");
            //                integratedEntrust.setErrorCode(ErrorCode.TERMINAL_RETURN_ERROR.value);
            //                mockTradeResponse.resIntegratedEntrust(integratedEntrust);
            //                return;
            //            }
            //            if (!Strings.isNullOrEmpty(integratedEntrust.getLiaisonTel()) && integratedEntrust.getLiaisonTel().length() != 6) {
            //                logger.info("发起方交易员必须为6位代码");
            //                integratedEntrust.setEntrustStatus(EntrustStatus.INVALID.value);
            //                integratedEntrust.setErrorMsg("发起方交易员必须为6位代码");
            //                integratedEntrust.setErrorCode(ErrorCode.TERMINAL_RETURN_ERROR.value);
            //                mockTradeResponse.resIntegratedEntrust(integratedEntrust);
            //                return;
            //            }
            //            if (!Strings.isNullOrEmpty(integratedEntrust.getLiaisonName()) && integratedEntrust.getLiaisonName().length() != 6) {
            //                logger.info("对手方交易员必须为6位代码");
            //                integratedEntrust.setEntrustStatus(EntrustStatus.INVALID.value);
            //                integratedEntrust.setErrorMsg("对手方交易员必须为6位代码");
            //                integratedEntrust.setErrorCode(ErrorCode.TERMINAL_RETURN_ERROR.value);
            //                mockTradeResponse.resIntegratedEntrust(integratedEntrust);
            //                return;
            //            }
            integratedEntrust.setBrokerEntrustId(integratedEntrust.getId().substring(2));
            if (mockIntegratedEntrustFailByEntrustVolume(integratedEntrust))
                return;
            mockTradeResponse.resIntegratedEntrust(integratedEntrust);
            integratedEntrust.setBrokerEntrustId(integratedEntrust.getId().substring(2));
            if (mockNoTradeByEntrustVolume(integratedEntrust))
                return;
            mockTradeOrPartTradeByEntrustVolume(integratedEntrust);
        } else if (EntrustProp.SZ_XSCJ_XD.value.equals(integratedEntrust.getEntrustProp())
                || EntrustProp.SZ_XSCJ_JS.value.equals(integratedEntrust.getEntrustProp())
                || EntrustProp.SZ_XSCJ_JJ.value.equals(integratedEntrust.getEntrustProp())) {//新债 深圳协商成交
            integratedEntrust.setBrokerEntrustId(integratedEntrust.getId().substring(2));
            if (mockIntegratedEntrustFailByEntrustVolume(integratedEntrust))
                return;
            mockTradeResponse.resIntegratedEntrust(integratedEntrust);
            integratedEntrust.setBrokerEntrustId(integratedEntrust.getId().substring(2));
            if (mockNoTradeByEntrustVolume(integratedEntrust))
                return;
            mockTradeOrPartTradeByEntrustVolume(integratedEntrust);
        } else {
            //            if (integratedEntrust.getEntrustBalance().doubleValue() == 0) {
            //                logger.info("融资金额不能为 0");
            //                integratedEntrust.setEntrustStatus(EntrustStatus.INVALID.value);
            //                integratedEntrust.setErrorMsg("融资金额不能为 0");
            //                integratedEntrust.setErrorCode(ErrorCode.TERMINAL_RETURN_ERROR.value);
            //                mockTradeResponse.resIntegratedEntrust(integratedEntrust);
            //                return;
            //            }
            //            if (FullPriceConst.FULL_PRICE.value == integratedEntrust.getIsFullPrice() && integratedEntrust.getFullPrice().doubleValue() == 0) {
            //                logger.info("全价回购利率不能为 0");
            //                integratedEntrust.setEntrustStatus(EntrustStatus.INVALID.value);
            //                integratedEntrust.setErrorMsg("回购利率不能为 0");
            //                integratedEntrust.setErrorCode(ErrorCode.TERMINAL_RETURN_ERROR.value);
            //                mockTradeResponse.resIntegratedEntrust(integratedEntrust);
            //                return;
            //            }
            //            if (FullPriceConst.NET_PRICE.value == integratedEntrust.getIsFullPrice() && integratedEntrust.getEntrustPrice().doubleValue() == 0) {
            //                logger.info("净价回购利率不能为 0");
            //                integratedEntrust.setEntrustStatus(EntrustStatus.INVALID.value);
            //                integratedEntrust.setErrorMsg("回购利率不能为 0");
            //                integratedEntrust.setErrorCode(ErrorCode.TERMINAL_RETURN_ERROR.value);
            //                mockTradeResponse.resIntegratedEntrust(integratedEntrust);
            //                return;
            //            }
            //            if (integratedEntrust.getEntrustBalance().doubleValue() > integratedEntrust.getEntrustVolume() * 100) {
            //                logger.info("融资金额[{}}]不允许超过质押债券面值总额[{}}张 * 100.00 元/张={}元]", integratedEntrust.getEntrustBalance(), integratedEntrust.getEntrustVolume(), integratedEntrust.getEntrustVolume() * 100);
            //                integratedEntrust.setEntrustStatus(EntrustStatus.INVALID.value);
            //                integratedEntrust.setErrorMsg("融资金额[{" + integratedEntrust.getEntrustBalance() + "}}]不允许超过质押债券面值总额[{" + integratedEntrust.getEntrustVolume() + "}}张 * 100.00 元/张={" + integratedEntrust.getEntrustVolume() * 100 + "}元]");
            //                integratedEntrust.setErrorCode(ErrorCode.TERMINAL_RETURN_ERROR.value);
            //                mockTradeResponse.resIntegratedEntrust(integratedEntrust);
            //                return;
            //            }
            //            if (IntegratedEntrustUtils.isProtocolBack_SH(integratedEntrust.getEntrustProp())) {
            //                if (integratedEntrust.getPropSeatNo().length() != 3) {
            //                    logger.info("对手方交易商代码必须为3位");
            //                    integratedEntrust.setEntrustStatus(EntrustStatus.INVALID.value);
            //                    integratedEntrust.setErrorMsg("对手方交易商代码必须为3位");
            //                    integratedEntrust.setErrorCode(ErrorCode.TERMINAL_RETURN_ERROR.value);
            //                    mockTradeResponse.resIntegratedEntrust(integratedEntrust);
            //                    return;
            //                }
            //                if (integratedEntrust.getContractId().length() != 6) {
            //                    logger.info("对手方交易员必须为6位代码");
            //                    integratedEntrust.setEntrustStatus(EntrustStatus.INVALID.value);
            //                    integratedEntrust.setErrorMsg("对手方交易员6位代码");
            //                    integratedEntrust.setErrorCode(ErrorCode.TERMINAL_RETURN_ERROR.value);
            //                    mockTradeResponse.resIntegratedEntrust(integratedEntrust);
            //                    return;
            //                }
            //            }
            //            if (IntegratedEntrustUtils.isProtocolBack_SZ(integratedEntrust.getEntrustProp())) {
            //                if (Integer.parseInt(integratedEntrust.getContractId()) < 100 || Integer.parseInt(integratedEntrust.getContractId()) > 999) {
            //                    logger.info("约定号必须为100到999之间的整数");
            //                    integratedEntrust.setEntrustStatus(EntrustStatus.INVALID.value);
            //                    integratedEntrust.setErrorMsg("约定号必须为100到999之间的整数");
            //                    integratedEntrust.setErrorCode(ErrorCode.TERMINAL_RETURN_ERROR.value);
            //                    mockTradeResponse.resIntegratedEntrust(integratedEntrust);
            //                    return;
            //                }
            //                if (integratedEntrust.getPropSeatNo().length() != 6) {
            //                    logger.info("对方交易单元席位号必须为6位数代码");
            //                    integratedEntrust.setEntrustStatus(EntrustStatus.INVALID.value);
            //                    integratedEntrust.setErrorMsg("对方交易单元席位号必须为6位数代码");
            //                    integratedEntrust.setErrorCode(ErrorCode.TERMINAL_RETURN_ERROR.value);
            //                    mockTradeResponse.resIntegratedEntrust(integratedEntrust);
            //                    return;
            //                }
            //            }
            integratedEntrust.setBrokerEntrustId(integratedEntrust.getId().substring(2));
            if (mockIntegratedEntrustFailByEntrustVolume(integratedEntrust))
                return;
            mockTradeResponse.resIntegratedEntrust(integratedEntrust);
            integratedEntrustQuoteList.add(integratedEntrust);
            if (mockNoTradeByEntrustVolume(integratedEntrust))
                return;
            mockTradeOrPartTradeByEntrustVolume(integratedEntrust);
        }
        logger.info("==================end======================");
    }

    private void mockTradeOrPartTradeByEntrustVolume(IntegratedEntrust integratedEntrust) {
        long tradeVolume;
        double tradeBalance = 0;
        if (integratedEntrust.getEntrustVolume() % 300 == 0
                || IntegratedEntrustUtils.isFixedIncome(integratedEntrust.getEntrustProp())
                || IntegratedEntrustUtils.isProtocolBack_SZ(integratedEntrust.getEntrustProp())
                || IntegratedEntrustUtils.isProtocolBack_SH(integratedEntrust.getEntrustProp())) {//全部成交 数量是300的倍数全部成交
            integratedEntrust.setEntrustStatus(EntrustStatus.SUCCEEDED.value);
            tradeVolume = integratedEntrust.getEntrustVolume();
            if (integratedEntrust.getCompactTerm() != 0) {
                tradeBalance = integratedEntrust.getEntrustBalance().doubleValue();
            }
        } else {
            tradeVolume = integratedEntrust.getEntrustVolume() / 2;
            integratedEntrust.setEntrustStatus(EntrustStatus.PART_SUCCESS.value);
            integratedEntrust.setTradedVolume(tradeVolume);
        }
        entrustMap.put(integratedEntrust.getId(), integratedEntrust);
        List<Trade> list = genTradeList(integratedEntrust, (int) tradeVolume, tradeBalance);
        onIntegratedEntrust(integratedEntrust);
        tradeArray.addAll(list);
        list.forEach(this::onTrade);
    }

    private boolean mockNoTradeByEntrustVolume(IntegratedEntrust integratedEntrust) {
        if (integratedEntrust.getEntrustVolume() % 7 == 0) {
            logger.info("数量是7的倍数，模拟没有成交");
            integratedEntrust.setEntrustStatus(EntrustStatus.REPORTED.value);
            entrustMap.put(integratedEntrust.getId(), integratedEntrust);
            onIntegratedEntrust(integratedEntrust);
            return true;
        }
        return false;
    }

    private boolean mockIntegratedEntrustFailByEntrustVolume(IntegratedEntrust integratedEntrust) {
        if (integratedEntrust.getEntrustVolume() % 9 == 0) {
            logger.info("integratedEntrust数量为9的倍数,模拟下单失败,废单");
            integratedEntrust.setEntrustStatus(EntrustStatus.INVALID.value);
            integratedEntrust.setErrorMsg("integratedEntrust数量为9的倍数,模拟下单失败,废单");
            integratedEntrust.setErrorCode(ErrorCode.TERMINAL_RETURN_ERROR.value);
            mockTradeResponse.resIntegratedEntrust(integratedEntrust);
        }
        return integratedEntrust.getErrorCode() != ErrorCode.SUCCESS.value;
    }

    private void handleIntegratedEntrustByAssetType(IntegratedEntrust integratedEntrust, double lessEntrustVolume, double lessEntrustBalance) {
        if (integratedEntrust.getEntrustVolume() < lessEntrustVolume) {
            if (FullPriceConst.NET_PRICE.value.equals(integratedEntrust.getIsFullPrice()) && (integratedEntrust.getEntrustVolume() * integratedEntrust.getEntrustPrice().doubleValue()) < lessEntrustBalance) {
                logger.info("委托数量必须不小于[{}]或者委托净价金额必须不小于[{}]", lessEntrustVolume, lessEntrustBalance);
                integratedEntrust.setEntrustStatus(EntrustStatus.INVALID.value);
                integratedEntrust.setErrorMsg("委托数量必须不小于" + lessEntrustVolume + "或者委托金额必须不小于 " + lessEntrustBalance);
                integratedEntrust.setErrorCode(ErrorCode.TERMINAL_RETURN_ERROR.value);
                mockTradeResponse.resIntegratedEntrust(integratedEntrust);
            }
            if (FullPriceConst.FULL_PRICE.value.equals(integratedEntrust.getIsFullPrice()) && (integratedEntrust.getEntrustVolume() * integratedEntrust.getFullPrice().doubleValue()) < lessEntrustBalance) {
                logger.info("委托数量必须不小于[{}]或者委托全价金额必须不小于[{}]", lessEntrustVolume, lessEntrustBalance);
                integratedEntrust.setEntrustStatus(EntrustStatus.INVALID.value);
                integratedEntrust.setErrorMsg("委托数量必须不小于" + lessEntrustVolume + "或者委托金额必须不小于 " + lessEntrustBalance);
                integratedEntrust.setErrorCode(ErrorCode.TERMINAL_RETURN_ERROR.value);
                mockTradeResponse.resIntegratedEntrust(integratedEntrust);
            }
        }
    }

    /***
     * @Description: 委托撤单
     * @Param: [entrust, requestId]
     * @return: void
     * @Author: MAXX
     * @Date: 2020/6/9
     */
    @Override
    public void cancelEntrust(CancelEntrustBean cancelEntrustBean, Long requestId) {
        Entrust entrust = (Entrust) entrustMap.get(cancelEntrustBean.getId());
        cancelEntrustBean.setAgentType(AgentType.MOCK.value);
        logger.info("cancelEntrust begin,entrust:{}", JSONObject.toJSONString(entrust));
        if (Objects.isNull(entrust)) {
            cancelEntrustBean.setEntrustStatus(EntrustStatus.INVALID.value);
            cancelEntrustBean.setErrorCode(ErrorCode.REQ_PARAMETER_ERROR.value);
            cancelEntrustBean.setErrorMsg("该委托不可撤");
            mockTradeResponse.resCancelEntrust(cancelEntrustBean);
            return;
        }
        /*cancelEntrustBean.setId(entrust.getId());
        cancelEntrustBean.setBrokerEntrustId(entrust.getBrokerEntrustId());
        cancelEntrustBean.setMarket(entrust.getMarket());
        cancelEntrustBean.setEntrustProp(entrust.getEntrustProp());
        cancelEntrustBean.setTradingDay(entrust.getTradingDay());
        cancelEntrustBean.setEntrustStatus(entrust.getEntrustStatus());
        cancelEntrustBean.setBsFlag(entrust.getBsFlag());
        cancelEntrustBean.setEntrustBs(entrust.getEntrustBs());
        cancelEntrustBean.setAccountId(entrust.getAccountId());
        cancelEntrustBean.setStockCode(entrust.getStockCode());*/
        if (EntrustUtils.isNoTrade(entrust.getEntrustProp())) {
            // 深证转股、配债、配股可以撤单 ------ 回售上海、深证均可撤单
            if (!((EntrustProp.CHANGE_TO_STOCK.value.equals(entrust.getEntrustProp())
                    || EntrustProp.ALLOTMENT.value.equals(entrust.getEntrustProp())
                    || EntrustProp.PS.value.equals(entrust.getEntrustProp()))
                    && Market.SZSE.value.equals(entrust.getMarket())) && !EntrustProp.HS.value.equals(entrust.getEntrustProp())) {
                logger.info("回报即成交业务,不可撤单");
                cancelEntrustBean.setEntrustStatus(EntrustStatus.REPORTED.value);
                cancelEntrustBean.setErrorCode(ErrorCode.REQ_PARAMETER_ERROR.value);
                cancelEntrustBean.setErrorMsg("该类业务不可撤单");
                mockTradeResponse.resCancelEntrust(cancelEntrustBean);
                return;
            }
        }
        Trade trade = getTrade(entrust, Calendar.getInstance(), TradeType.CANCEL.value);
        if (entrust.getEntrustStatus() == EntrustStatus.REPORTED.value) {
            logger.info("====已撤====");
            entrust.setEntrustStatus(EntrustStatus.CANCELED.value);
            cancelEntrustBean.setCancelBrokerEntrustId(cancelEntrustBean.getCancelId());
            trade.setTradeType(TradeType.CANCEL.value);
            trade.setTradedVolume(entrust.getEntrustVolume());
            if (FullPriceConst.FULL_PRICE.value.equals(entrust.getIsFullPrice())) {
                trade.setTradedPrice(entrust.getFullPrice());
            } else {
                trade.setTradedPrice(entrust.getEntrustPrice());
            }
        }
        if (entrust.getEntrustStatus() == EntrustStatus.PART_SUCCESS.value) {
            logger.info("====部成撤单====");
            entrust.setEntrustStatus(EntrustStatus.PART_TRADED_CANCELED.value);
            cancelEntrustBean.setCancelBrokerEntrustId(cancelEntrustBean.getCancelId());
            trade.setTradeType(TradeType.CANCEL.value);
            trade.setTradedVolume(entrust.getEntrustVolume() - entrust.getTradedVolume());
            if (FullPriceConst.FULL_PRICE.value.equals(entrust.getIsFullPrice())) {
                trade.setTradedPrice(entrust.getFullPrice());
            } else {
                trade.setTradedPrice(entrust.getEntrustPrice());
            }
        }
        Entrust cancelEntrust = new Entrust();
        getCancelEntrust(cancelEntrust, entrust, cancelEntrustBean);
        mockTradeResponse.resCancelEntrust(cancelEntrustBean);
        entrustMap.remove(entrust.getId());
        onEntrust(cancelEntrust);
        onEntrust(entrust);
        onTrade(trade);
    }

    private void getCancelEntrust(EntrustInterface cancelEntrust, EntrustInterface entrust, CancelEntrustBean cancelEntrustBean) {
        BeanUtils.copyProperties(entrust, cancelEntrust, "id", "entrustStatus", "frozenBalance", "frozenVolume", "tradedBalance", "tradedPrice", "tradedVolume", "cancelVolume", "entrustType", "localEntrustId", "brokerEntrustId", "origEntrustId");
        cancelEntrust.setEntrustVolume(entrust.getEntrustVolume() - entrust.getTradedVolume());
        cancelEntrust.setId(cancelEntrustBean.getCancelId());
        cancelEntrust.setBrokerEntrustId(cancelEntrustBean.getCancelId().substring(2));
        cancelEntrust.setOrigEntrustId(cancelEntrustBean.getId());
        cancelEntrust.setEntrustType(EntrustType.CANCEL.value);
        cancelEntrust.setEntrustStatus(EntrustStatus.REPORTED.value);
    }

    /***
     * @Description: 撤单
     * @Param: [integratedEntrust, requestId]
     * @return: void
     * @Author: MAXX
     * @Date: 2020/6/10
     */
    @Override
    public void cancelIntegratedEntrust(CancelEntrustBean cancelEntrustBean, Long requestId) {
        cancelEntrustBean.setAgentType(AgentType.MOCK.value);
        IntegratedEntrust integratedEntrust = (IntegratedEntrust) entrustMap.get(cancelEntrustBean.getId());
        logger.info("进行综合业务撤单:{}", JSONObject.toJSONString(integratedEntrust));
        if (Objects.isNull(integratedEntrust)) {
            integratedEntrust = new IntegratedEntrust();
            integratedEntrust.setId(cancelEntrustBean.getId());
            integratedEntrust.setBrokerEntrustId(cancelEntrustBean.getBrokerEntrustId());
            integratedEntrust.setEntrustProp(cancelEntrustBean.getEntrustProp());
            integratedEntrust.setEntrustStatus(cancelEntrustBean.getEntrustStatus());
            integratedEntrust.setEntrustVolume(cancelEntrustBean.getEntrustVolume());
            integratedEntrust.setIsFullPrice(cancelEntrustBean.getIsFullPrice());
            integratedEntrust.setFullPrice(cancelEntrustBean.getFullPrice());
            integratedEntrust.setEntrustPrice(cancelEntrustBean.getEntrustPrice());
            integratedEntrust.setTradedVolume(cancelEntrustBean.getTradedVolume());
        }
        Trade trade = getTrade(integratedEntrust, Calendar.getInstance(), TradeType.CANCEL.value);
        trade.setTradeType(TradeType.CANCEL.value);
        trade.setTradedVolume(integratedEntrust.getEntrustVolume() - integratedEntrust.getTradedVolume());
        if (FullPriceConst.FULL_PRICE.value.equals(integratedEntrust.getIsFullPrice())) {
            trade.setTradedPrice(integratedEntrust.getFullPrice());
        } else {
            trade.setTradedPrice(integratedEntrust.getEntrustPrice());
        }
        if (IntegratedEntrustUtils.isProtocolBack_SZ(integratedEntrust.getEntrustProp())
                || IntegratedEntrustUtils.isProtocolBack_SH(integratedEntrust.getEntrustProp())) {
            trade.setTradedBalance(integratedEntrust.getEntrustBalance());
        }
        IntegratedEntrust cancelEntrust = new IntegratedEntrust();
        getCancelEntrust(cancelEntrust, integratedEntrust, cancelEntrustBean);
        if (EntrustProp.INTENTION.value.equals(integratedEntrust.getEntrustProp())) {
            logger.info("=========大宗意向委托撤单==========");
            integratedEntrust.setEntrustStatus(EntrustStatus.CANCELED.value);
            cancelEntrustBean.setCancelBrokerEntrustId(cancelEntrustBean.getCancelId());
            mockTradeResponse.resCancelIntegratedEntrust(cancelEntrustBean);
            entrustMap.remove(integratedEntrust.getId());
            onIntegratedEntrust(integratedEntrust);
            onIntegratedEntrust(cancelEntrust);
            onTrade(trade);
            return;
        }
        if (integratedEntrust.getEntrustStatus() == EntrustStatus.REPORTED.value
                || integratedEntrust.getEntrustStatus() == EntrustStatus.REPORTED_TOBE_CANCEL.value) {
            logger.info("============已撤==========");
            integratedEntrust.setEntrustStatus(EntrustStatus.CANCELED.value);
            cancelEntrustBean.setCancelBrokerEntrustId(cancelEntrustBean.getCancelId());
        } else if (integratedEntrust.getEntrustStatus() == EntrustStatus.PART_SUCCESS.value
                || integratedEntrust.getEntrustStatus() == EntrustStatus.PART_TRADED_TOBE_CANCELED.value) {
            logger.info("=========部成撤单==========");
            integratedEntrust.setEntrustStatus(EntrustStatus.PART_TRADED_CANCELED.value);
            cancelEntrustBean.setCancelBrokerEntrustId(cancelEntrustBean.getCancelId());
        }
        mockTradeResponse.resCancelIntegratedEntrust(cancelEntrustBean);
        entrustMap.remove(integratedEntrust.getId());
        onIntegratedEntrust(cancelEntrust);
        onIntegratedEntrust(integratedEntrust);
        onTrade(trade);
        logger.info("=============撤单结束===========");
    }

    @Override
    public void queryIntegratedQuotes(QuoteQryBean quoteQryBean, Long requestId) {
        String stockCode = quoteQryBean.getStockCode();
        TickBean tickBean = queryQuoteTick(stockCode);

        List<IntegratedQuote> integratedQuotes = new ArrayList<>();
        if (quoteQryBean.getQuoteType() == QuoteType.BULK_QUOTE.value) {
            if (integratedEntrustQuoteList.size() > 0) {
                integratedEntrustQuoteList.forEach(integratedEntrust -> {
                    if (integratedEntrust.getStockCode().equals(stockCode)) {
                        IntegratedQuote integratedQuote = MockQuoteData.makeIntegratedEntrustQuote(integratedEntrust);
                        integratedQuote.setQuoteType(QuoteType.BULK_QUOTE.value);
                        integratedQuotes.add(integratedQuote);
                    }
                });
            }
            integratedQuotes.addAll(MockQuoteData.makeMockDataForBulk(stockCode, tickBean));
        } else if (quoteQryBean.getQuoteType() == QuoteType.AFTER_BULK_QUOTE.value) {
            integratedQuotes.addAll(MockQuoteData.makeAfterBulkQuote(stockCode, tickBean));
        } else if (quoteQryBean.getQuoteType() == QuoteType.FIXED_INCOME_QUOTE.value) {
            integratedQuotes.addAll(MockQuoteData.makeFixedIncomeQuote(stockCode, tickBean));
        }
        mockTradeResponse.resQueryIntegratedQuotes(integratedQuotes, requestId);
    }

    @Value("${mock.quote-tick-url:http://127.0.0.1:8080/tams/service/joyin/quote/tick}")
    private String quoteTickUrl;

    private TickBean queryQuoteTick(String stockCode) {
        try {
            HttpRequest request = HttpRequest.post(quoteTickUrl)
                    .body(JSONUtil.createObj()
                            .set("userId", "xxx")
                            .set("stockCode", stockCode)
                            .toString());
            String body = request.execute().body();
            logger.info("行情信息：{}", body);
            ResultBean resultBean = JSONUtil.toBean(body, ResultBean.class);
            return Convert.convert(TickBean.class, resultBean.getData());
        } catch (Exception e) {
            logger.error("获取行情失败：stockCode={}", stockCode, e);
        }
        return null;
    }

    @Override
    public void queryEnable(EnableQryBean qryBean, Long requestId) {
    }

    @Override
    public void queryHistoryTrade(HistoryQryBean historyQryBean, Long requestId) {
    }

    @Override
    public void queryCustomerFinance(String accountId, Long requestId) {
    }

    @Override
    public void queryAccountPosition(PositionQueryBean positionQueryBean, Long requestId) {
        mockTradeResponse.resQueryAccountPosition(new ArrayList<>(), requestId);
    }

    @Override
    public void queryPledgeDiscount(String accountId, String market, String stockCode, Long requestId) {
    }

    @Override
    public void queryStandardBonds(StandardBondQryBean standardBondQryBean, Long requestId) {
    }

    @Override
    public void queryInstrumentInfo(InstrumentQryBean instrumentQryBean, Long requestId) {
    }

    @Override
    public void queryNextTrade() {
        //        logger.info("==============queryNextTrade test==============");
    }

    public void onEntrust(Entrust entrust) {
        iTerminalMsgNotify.onEntrust(entrust);
    }

    public void onTrade(Trade trade) {
        if (!(IntegratedEntrustUtils.isProtocolBack_SH(trade.getEntrustProp())
                || IntegratedEntrustUtils.isProtocolBack_SZ(trade.getEntrustProp())) && !InvestAssetTypeEnum.FUTURES.value.equals(trade.getAssetType())) {
            trade.setTradedBalance(TradeUtils.calculateBalanceForRisk(trade.getEntrustBs(), trade.getTradedPrice(), trade.getTradedVolume(), trade.getAssetType()));
        }
        if (Strings.isNullOrEmpty(trade.getBrokerTradeId())) {
            trade.setBrokerTradeId(trade.getEntrustId());
        }
        trade.setNoCancelEntrust(true);
        iTerminalMsgNotify.onTrade(trade);
    }

    public void onIntegratedEntrust(IntegratedEntrust integratedEntrust) {
        iTerminalMsgNotify.onIntegratedEntrust(integratedEntrust);
    }

    @Override
    public void queryIntegratedBulkQuote(IntegratedBulkQuoteQryBean integratedBulkQuoteQryBean, Long requestId) {
        String stockCode = integratedBulkQuoteQryBean.getStockCode();
        TickBean tickBean = queryQuoteTick(stockCode);

        List<IntegratedQuote> integratedQuotes = MockQuoteData.makeMockDataForBulk(stockCode, tickBean);
        mockTradeResponse.resQueryIntegratedBulkQuote(MockConverter.convertIntegratedBulkQuoteBeans(integratedQuotes), requestId);
    }

    @Override
    public void queryIntegratedBulkCloseQuote(IntegratedBulkCloseQuoteQryBean integratedBulkCloseQuoteQryBean, Long requestId) {
        String stockCode = integratedBulkCloseQuoteQryBean.getStockCode();
        TickBean tickBean = queryQuoteTick(stockCode);

        List<IntegratedQuote> integratedQuotes = MockQuoteData.makeAfterBulkQuote(stockCode, tickBean);
        mockTradeResponse.resQueryIntegratedBulkCloseQuote(MockConverter.convertIntegratedBulkCloseQuoteBeans(integratedQuotes), requestId);
    }

    /***
     * @Description: 上海协议回购行情查询
     * @Param: [integratedRepurchaseQuoteQryBean, requestId]
     * @return: void
     * @Author: MAXX
     * @Date: 2020/9/23
     */
    @Override
    public void queryIntegratedRepurchaseQuote(IntegratedRepurchaseQuoteQryBean integratedRepurchaseQuoteQryBean, Long requestId) {
        SimpleDateFormat tradingDayFormat = new SimpleDateFormat("yyyyMMdd");
        List<IntegratedRepurchaseQuoteBean> list = new ArrayList<IntegratedRepurchaseQuoteBean>();
        String[] entrustPropArr = null;
        if (Strings.isNullOrEmpty(integratedRepurchaseQuoteQryBean.getEntrustProp())) {
            entrustPropArr = new String[]{EntrustProp.SH_REPURCHASE_TRADE.value,
                    EntrustProp.SH_REPURCHASE_RENEWAL.value, EntrustProp.SH_REPURCHASE_TERMINATE.value,
                    EntrustProp.SH_REPURCHASE_RELEASE.value, EntrustProp.SH_REPURCHASE_EXCHANGE.value,
                    EntrustProp.SH_REPURCHASE_EXPIRE.value, EntrustProp.SH_REPURCHASE_INTENTION.value};
        } else {
            entrustPropArr = new String[]{integratedRepurchaseQuoteQryBean.getEntrustProp()};
        }
        if (!integratedEntrustQuoteList.isEmpty()) {
            Map<String, Trade> brokerTradeIdMap = tradeArray.stream().filter(trade -> IntegratedEntrustUtils.isProtocolBack(trade.getEntrustProp())).collect(Collectors.toMap(Trade::getBrokerTradeId, trade -> trade, (trade, trade2) -> trade2));
            integratedEntrustQuoteList.forEach(integratedEntrust -> {
                if (IntegratedEntrustUtils.isProtocolBack_SH(integratedEntrust.getEntrustProp())) {
                    IntegratedRepurchaseQuoteBean integratedRepurchaseQuoteBean = new IntegratedRepurchaseQuoteBean();
                    integratedRepurchaseQuoteQryBean.setEntrustProp(integratedEntrust.getEntrustProp());
                    integratedRepurchaseQuoteBean.setCbpconferId(integratedEntrust.getId().substring(integratedEntrust.getId().length() - 6));
                    integratedRepurchaseQuoteBean.setEntrustNo(Integer.parseInt(integratedRepurchaseQuoteBean.getCbpconferId()));
                    integratedRepurchaseQuoteBean.setEntrustProp(integratedRepurchaseQuoteQryBean.getEntrustProp());
                    integratedRepurchaseQuoteBean.setEntrustBs(integratedEntrust.getEntrustBs());
                    integratedRepurchaseQuoteBean.setExchangeType(ExchangeType.SH.value);
                    integratedRepurchaseQuoteBean.setStockType(integratedEntrust.getAssetType());
                    integratedRepurchaseQuoteBean.setStockCode(integratedEntrust.getStockCode());
                    integratedRepurchaseQuoteBean.setStockName(integratedEntrust.getStockName());
                    integratedRepurchaseQuoteBean.setExpireYearRate(integratedEntrust.getEntrustPrice().doubleValue());
                    integratedRepurchaseQuoteBean.setRepoTerm(integratedEntrust.getCompactTerm());
                    if (!Strings.isNullOrEmpty(integratedEntrust.getFirstSettleDate()) && !Strings.isNullOrEmpty(integratedEntrust.getBackDate())) {
                        integratedRepurchaseQuoteBean.setFundUsedDays((int) DateUtil.getBetweenDayNumber(integratedEntrust.getFirstSettleDate(), integratedEntrust.getBackDate()));
                    }
                    if (EntrustProp.SH_REPURCHASE_INTENTION.value.equals(integratedRepurchaseQuoteQryBean.getEntrustProp())
                            || EntrustProp.SH_REPURCHASE_TERMINATE.value.equals(integratedRepurchaseQuoteQryBean.getEntrustProp())
                            || EntrustProp.SH_REPURCHASE_EXCHANGE.value.equals(integratedRepurchaseQuoteQryBean.getEntrustProp())
                            || EntrustProp.SH_REPURCHASE_TRADE.value.equals(integratedRepurchaseQuoteQryBean.getEntrustProp())) {
                        if (!Strings.isNullOrEmpty(integratedEntrust.getFirstSettleDate()))
                            integratedRepurchaseQuoteBean.setFirstExchdate(Integer.parseInt(integratedEntrust.getFirstSettleDate()));
                        if (!Strings.isNullOrEmpty(integratedEntrust.getBackDate()))
                            integratedRepurchaseQuoteBean.setRepurchaseDate(Integer.parseInt(integratedEntrust.getBackDate()));
                        integratedRepurchaseQuoteBean.setSettleEndDate(integratedRepurchaseQuoteBean.getRepurchaseDate());
                        integratedRepurchaseQuoteBean.setOrigEntrustDate(integratedRepurchaseQuoteBean.getSettleEndDate());
                        integratedRepurchaseQuoteBean.setRealDateBack(integratedRepurchaseQuoteBean.getSettleEndDate());
                        integratedRepurchaseQuoteBean.setProfit(TradeUtils.subtract(integratedEntrust.getBackBalance(), integratedEntrust.getEntrustBalance(), 2).doubleValue());
                        integratedRepurchaseQuoteBean.setSettleBalance(integratedEntrust.getBackBalance().doubleValue());
                    } else if (EntrustProp.SH_REPURCHASE_RELEASE.value.equals(integratedRepurchaseQuoteQryBean.getEntrustProp())
                            || EntrustProp.SH_REPURCHASE_RENEWAL.value.equals(integratedRepurchaseQuoteQryBean.getEntrustProp())) {
                        if (!Strings.isNullOrEmpty(integratedEntrust.getBackDate()))
                            integratedRepurchaseQuoteBean.setRealDateBack(Integer.parseInt(integratedEntrust.getBackDate()));
                        if (!Strings.isNullOrEmpty(integratedEntrust.getFirstSettleDate()))
                            integratedRepurchaseQuoteBean.setFirstExchdate(Integer.parseInt(integratedEntrust.getFirstSettleDate()));
                        if (!Strings.isNullOrEmpty(integratedEntrust.getBackDate()))
                            integratedRepurchaseQuoteBean.setRepurchaseDate(Integer.parseInt(integratedEntrust.getBackDate()));
                        integratedRepurchaseQuoteBean.setSettleEndDate(integratedRepurchaseQuoteBean.getRepurchaseDate());
                        integratedRepurchaseQuoteBean.setOrigEntrustDate(integratedRepurchaseQuoteBean.getSettleEndDate());
                        if (EntrustProp.SH_REPURCHASE_RELEASE.value.equals(integratedRepurchaseQuoteQryBean.getEntrustProp())) {
                            integratedRepurchaseQuoteBean.setProfit(0.0);
                            integratedRepurchaseQuoteBean.setSettleBalance(0.0);
                        } else {
                            integratedRepurchaseQuoteBean.setProfit(TradeUtils.subtract(integratedEntrust.getBackBalance(), integratedEntrust.getEntrustBalance(), 2).doubleValue());
                            integratedRepurchaseQuoteBean.setSettleBalance(integratedEntrust.getBackBalance().doubleValue());
                        }
                    } else if (EntrustProp.SH_REPURCHASE_EXPIRE.value.equals(integratedRepurchaseQuoteQryBean.getEntrustProp())) {
                        if (!Strings.isNullOrEmpty(integratedEntrust.getBackDate()))
                            integratedRepurchaseQuoteBean.setRepurchaseDate(Integer.parseInt(integratedEntrust.getBackDate()));
                        if (!Strings.isNullOrEmpty(integratedEntrust.getFirstSettleDate()))
                            integratedRepurchaseQuoteBean.setFirstExchdate(Integer.parseInt(integratedEntrust.getFirstSettleDate()));
                        integratedRepurchaseQuoteBean.setSettleEndDate(integratedRepurchaseQuoteBean.getRepurchaseDate());
                        integratedRepurchaseQuoteBean.setOrigEntrustDate(integratedRepurchaseQuoteBean.getSettleEndDate());
                        integratedRepurchaseQuoteBean.setRealDateBack(integratedRepurchaseQuoteBean.getSettleEndDate());
                        integratedRepurchaseQuoteBean.setProfit(TradeUtils.subtract(integratedEntrust.getBackBalance(), integratedEntrust.getEntrustBalance(), 2).doubleValue());
                        integratedRepurchaseQuoteBean.setSettleBalance(integratedEntrust.getBackBalance().doubleValue());
                    }
                    integratedRepurchaseQuoteBean.setImpawnAmount((int) integratedEntrust.getEntrustVolume());
                    integratedRepurchaseQuoteBean.setFunderRatio(integratedEntrust.getDiscount().doubleValue());
                    integratedRepurchaseQuoteBean.setImpawnBalance(integratedEntrust.getEntrustBalance().doubleValue());
                    if (EntrustProp.SH_REPURCHASE_EXCHANGE.value.equals(integratedRepurchaseQuoteQryBean.getEntrustProp())) {
                        integratedRepurchaseQuoteBean.setOldImpawnCode(integratedEntrust.getStockCode());
                        integratedRepurchaseQuoteBean.setPrevImpawnBalance(integratedEntrust.getEntrustBalance().doubleValue());
                    }
                    integratedRepurchaseQuoteBean.setAssetNetValue(integratedEntrust.getEntrustBalance().doubleValue());
                    if (EntrustProp.SH_REPURCHASE_RENEWAL.value.equals(integratedRepurchaseQuoteQryBean.getEntrustProp())) {
                        integratedRepurchaseQuoteBean.setBprsrcStatus("0");//到期续做填写 非第三方：0 第三方：1
                    }
                    if (EntrustProp.SH_REPURCHASE_RELEASE.value.equals(integratedRepurchaseQuoteQryBean.getEntrustProp())) {
                        Trade trade = brokerTradeIdMap.get(integratedEntrust.getOrigSerialNo());
                        List<IntegratedEntrust> integratedEntrusts = new ArrayList<>();
                        if (Objects.nonNull(trade)) {
                            integratedEntrusts = integratedEntrustQuoteList.stream().filter(integratedEntrust1 -> integratedEntrust1.getId().equals(trade.getEntrustId())).collect(Collectors.toList());
                        }
                        if (!integratedEntrusts.isEmpty()) {
                            IntegratedEntrust next = integratedEntrusts.iterator().next();
                            integratedRepurchaseQuoteBean.setFunderRatio(next.getDiscount().doubleValue());
                            integratedRepurchaseQuoteBean.setImpawnBalance(next.getEntrustBalance().doubleValue());
                            integratedRepurchaseQuoteBean.setExpireYearRate(next.getEntrustPrice().doubleValue());
                            integratedRepurchaseQuoteBean.setProfit(TradeUtils.subtract(next.getBackBalance(), next.getEntrustBalance(), 2).doubleValue());
                            integratedRepurchaseQuoteBean.setSettleBalance(next.getBackBalance().doubleValue());
                        }
                    }
                    if (EntrustProp.SH_REPURCHASE_EXCHANGE.value.equals(integratedRepurchaseQuoteQryBean.getEntrustProp())) {
                        Trade trade = brokerTradeIdMap.get(integratedEntrust.getOrigSerialNo());
                        List<IntegratedEntrust> integratedEntrusts = integratedEntrustQuoteList.stream().filter(integratedEntrust1 -> integratedEntrust1.getId().equals(trade.getEntrustId())).collect(Collectors.toList());
                        if (!integratedEntrusts.isEmpty()) {
                            IntegratedEntrust next = integratedEntrusts.iterator().next();
                            integratedRepurchaseQuoteBean.setOldImpawnCode(next.getStockCode());
                        }
                    }
                    integratedRepurchaseQuoteBean.setOrigCbpconferId(integratedEntrust.getOrigSerialNo());
                    integratedRepurchaseQuoteBean.setAgencyNo(integratedEntrust.getPropSeatNo());
                    integratedRepurchaseQuoteBean.setTraderId(integratedEntrust.getLiaisonTel());
                    integratedRepurchaseQuoteBean.setFundAccount(integratedEntrust.getFinanceAccount());
                    integratedRepurchaseQuoteBean.setStockAccount("B882017345");
                    integratedRepurchaseQuoteBean.setSeatNo("E0989");
                    integratedRepurchaseQuoteBean.setOppoAgency(integratedEntrust.getSellerNo().substring(1, 4));
                    integratedRepurchaseQuoteBean.setOppoTraderId(integratedEntrust.getSellerNo());
                    integratedRepurchaseQuoteBean.setAgencyName("mock");
                    integratedRepurchaseQuoteBean.setPositionStr(integratedRepurchaseQuoteBean.getCbpconferId());
                    integratedRepurchaseQuoteBean.setOppoAgencyName("mock");
                    integratedRepurchaseQuoteBean.setPropStockAccount("A070960131");
                    integratedRepurchaseQuoteBean.setPropSeatNo("E0911");
                    integratedRepurchaseQuoteBean.setPropAccountName("mock");
                    integratedRepurchaseQuoteBean.setPledgeeName("mock");
                    integratedRepurchaseQuoteBean.setRemark(integratedEntrust.getRemark());
//                    list.add(integratedRepurchaseQuoteBean);
                }
            });
        }
        for (String entrustProp : entrustPropArr) {
            integratedRepurchaseQuoteQryBean.setEntrustProp(entrustProp);
            IntegratedRepurchaseQuoteBean integratedRepurchaseQuoteBean = new IntegratedRepurchaseQuoteBean();
            integratedRepurchaseQuoteBean.setCbpconferId((int) ((Math.random() * 9 + 1) * 100000) + "");
            integratedRepurchaseQuoteBean.setEntrustNo(Integer.parseInt(integratedRepurchaseQuoteBean.getCbpconferId()));
            integratedRepurchaseQuoteBean.setEntrustProp(integratedRepurchaseQuoteQryBean.getEntrustProp());
            integratedRepurchaseQuoteBean.setEntrustBs(EntrustBs.BUY.value);
            integratedRepurchaseQuoteBean.setExchangeType(ExchangeType.SH.value);
            integratedRepurchaseQuoteBean.setStockType(AssetType.GUO_ZHAI_HUI_GOU.value);
            integratedRepurchaseQuoteBean.setStockCode("010107.SH");
            integratedRepurchaseQuoteBean.setStockName("21国债(7)");
            integratedRepurchaseQuoteBean.setExpireYearRate(3.22D);
            integratedRepurchaseQuoteBean.setRepoTerm(1);
            integratedRepurchaseQuoteBean.setFundUsedDays(1);
            Calendar c = Calendar.getInstance();
            if (EntrustProp.SH_REPURCHASE_INTENTION.value.equals(integratedRepurchaseQuoteQryBean.getEntrustProp())
                    || EntrustProp.SH_REPURCHASE_TERMINATE.value.equals(integratedRepurchaseQuoteQryBean.getEntrustProp())
                    || EntrustProp.SH_REPURCHASE_EXCHANGE.value.equals(integratedRepurchaseQuoteQryBean.getEntrustProp())
                    || EntrustProp.SH_REPURCHASE_TRADE.value.equals(integratedRepurchaseQuoteQryBean.getEntrustProp())) {
                integratedRepurchaseQuoteBean.setFirstExchdate(Integer.parseInt(tradingDayFormat.format(c.getTime())));
                c.add(Calendar.DAY_OF_MONTH, 1);
                integratedRepurchaseQuoteBean.setRepurchaseDate(Integer.parseInt(tradingDayFormat.format(c.getTime())));
                integratedRepurchaseQuoteBean.setSettleEndDate(integratedRepurchaseQuoteBean.getRepurchaseDate());
                integratedRepurchaseQuoteBean.setOrigEntrustDate(integratedRepurchaseQuoteBean.getSettleEndDate());
                integratedRepurchaseQuoteBean.setRealDateBack(integratedRepurchaseQuoteBean.getSettleEndDate());
                integratedRepurchaseQuoteBean.setProfit(2465.7534);
                integratedRepurchaseQuoteBean.setSettleBalance(30002465.7534);
            } else if (EntrustProp.SH_REPURCHASE_RELEASE.value.equals(integratedRepurchaseQuoteQryBean.getEntrustProp())
                    || EntrustProp.SH_REPURCHASE_RENEWAL.value.equals(integratedRepurchaseQuoteQryBean.getEntrustProp())) {
                integratedRepurchaseQuoteBean.setFundUsedDays(2);
                integratedRepurchaseQuoteBean.setRealDateBack(Integer.parseInt(tradingDayFormat.format(c.getTime())));
                c.add(Calendar.DAY_OF_MONTH, -1);
                integratedRepurchaseQuoteBean.setFirstExchdate(Integer.parseInt(tradingDayFormat.format(c.getTime())));
                c.add(Calendar.DAY_OF_MONTH, 2);
                integratedRepurchaseQuoteBean.setRepurchaseDate(Integer.parseInt(tradingDayFormat.format(c.getTime())));
                integratedRepurchaseQuoteBean.setSettleEndDate(integratedRepurchaseQuoteBean.getRepurchaseDate());
                integratedRepurchaseQuoteBean.setOrigEntrustDate(integratedRepurchaseQuoteBean.getSettleEndDate());
                integratedRepurchaseQuoteBean.setProfit(4931.5068);
                integratedRepurchaseQuoteBean.setSettleBalance(30004931.5068);
            } else if (EntrustProp.SH_REPURCHASE_EXPIRE.value.equals(integratedRepurchaseQuoteQryBean.getEntrustProp())) {
                integratedRepurchaseQuoteBean.setRepurchaseDate(Integer.parseInt(tradingDayFormat.format(c.getTime())));
                c.add(Calendar.DAY_OF_MONTH, -1);
                integratedRepurchaseQuoteBean.setFirstExchdate(Integer.parseInt(tradingDayFormat.format(c.getTime())));
                integratedRepurchaseQuoteBean.setSettleEndDate(integratedRepurchaseQuoteBean.getRepurchaseDate());
                integratedRepurchaseQuoteBean.setOrigEntrustDate(integratedRepurchaseQuoteBean.getSettleEndDate());
                integratedRepurchaseQuoteBean.setRealDateBack(integratedRepurchaseQuoteBean.getSettleEndDate());
                integratedRepurchaseQuoteBean.setProfit(2465.7534);
                integratedRepurchaseQuoteBean.setSettleBalance(30002465.7534);
            }
            integratedRepurchaseQuoteBean.setImpawnAmount(500000);
            integratedRepurchaseQuoteBean.setFunderRatio(0.6);
            integratedRepurchaseQuoteBean.setImpawnBalance(50000000);
            if (EntrustProp.SH_REPURCHASE_EXCHANGE.value.equals(integratedRepurchaseQuoteQryBean.getEntrustProp())) {
                integratedRepurchaseQuoteBean.setOldImpawnCode("019614.SH");
                integratedRepurchaseQuoteBean.setPrevImpawnBalance(50000000);
            }
            integratedRepurchaseQuoteBean.setAssetNetValue(50000000);
            if (EntrustProp.SH_REPURCHASE_EXPIRE.value.equals(integratedRepurchaseQuoteQryBean.getEntrustProp())) {
                integratedRepurchaseQuoteBean.setBprsrcStatus("0");//到期续做填写 非第三方：0 第三方：1
            }
            integratedRepurchaseQuoteBean.setOrigCbpconferId(integratedRepurchaseQuoteBean.getCbpconferId());
            integratedRepurchaseQuoteBean.setAgencyNo("098");
            integratedRepurchaseQuoteBean.setTraderId("Z09876");
            integratedRepurchaseQuoteBean.setFundAccount("40650800");
            integratedRepurchaseQuoteBean.setStockAccount("B882017345");
            integratedRepurchaseQuoteBean.setSeatNo("E0989");
            integratedRepurchaseQuoteBean.setOppoAgency("091");
            integratedRepurchaseQuoteBean.setOppoTraderId("Z09111");
            integratedRepurchaseQuoteBean.setAgencyName("test24");
            integratedRepurchaseQuoteBean.setPositionStr(integratedRepurchaseQuoteBean.getCbpconferId());
            integratedRepurchaseQuoteBean.setOppoAgencyName("test0");
            integratedRepurchaseQuoteBean.setPropStockAccount("A070960131");
            integratedRepurchaseQuoteBean.setPropSeatNo("E0911");
            integratedRepurchaseQuoteBean.setPropAccountName("ufx0923上海");
            integratedRepurchaseQuoteBean.setPledgeeName("test24");
            integratedRepurchaseQuoteBean.setRemark("自动模拟行情");
//            list.add(integratedRepurchaseQuoteBean);
        }
        mockTradeResponse.resQueryIntegratedRepurchaseQuote(list, requestId);
    }

    @Override
    public void queryIntegratedFixedIncomeQuote(IntegratedFixedIncomeQuoteQryBean integratedFixedIncomeQuoteQryBean, Long requestId) {
        String stockCode = integratedFixedIncomeQuoteQryBean.getStockCode();
        TickBean tickBean = queryQuoteTick(stockCode);

        List<IntegratedQuote> integratedQuotes = MockQuoteData.makeFixedIncomeQuote(stockCode, tickBean);
        mockTradeResponse.resQueryIntegratedFixedIncomeQuote(MockConverter.convertIntegratedFixedIncomeQuoteBeans(integratedQuotes), requestId);
    }

    @Override
    public void reqReissueEntrust(String accountId, Long requestId) {

    }

    @Override
    public void reqReissueTrade(String accountId, Long requestId) {

    }

    @Override
    public int getStatus(String accountId) {
        return AccountLoginStatus.TRADING.value;
    }

    @Override
    public void setTradeStatus(String accountId, AccountLoginStatus status) {

    }

    @Override
    public void queryNegotiateQuoteReceive(NegotiateQuoteQueryBean negotiateQuoteQueryBean, Long requestId) {
        // TODO Auto-generated method stub
//        String stockCode = negotiateQuoteQueryBean.getStockCode();
//        TickBean tickBean = queryQuoteTick(stockCode);
        List<NegotiateQuoteReceiveBean> negotiateQuoteReceiveBeans = MockQuoteData.makeNegotiateQuoteReceive(negotiateQuoteQueryBean, null);
        mockTradeResponse.resQueryNegotiateQuoteReceive(negotiateQuoteReceiveBeans, requestId);
    }

}
