package com.joyintech.tams.exchange.service.trade.management.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.joyintech.tams.exchange.common.enums.invest.InvestAssetTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Strings;
import com.joyintech.tams.exchange.common.bean.AccountFinance;
import com.joyintech.tams.exchange.common.bean.FuturesPositionBean;
import com.joyintech.tams.exchange.common.bean.FuturesPositionDetailBean;
import com.joyintech.tams.exchange.common.bean.FuturesPositionDetailQryBean;
import com.joyintech.tams.exchange.common.bean.FuturesPositionQryBean;
import com.joyintech.tams.exchange.common.bean.PositionQueryBean;
import com.joyintech.tams.exchange.common.bean.ResultBean;
import com.joyintech.tams.exchange.common.constant.CompareType;
import com.joyintech.tams.exchange.common.constant.ErrorCode;
import com.joyintech.tams.exchange.common.db.domain.Account;
import com.joyintech.tams.exchange.common.db.domain.Entrust;
import com.joyintech.tams.exchange.common.db.domain.PositionBean;
import com.joyintech.tams.exchange.common.db.domain.Trade;
import com.joyintech.tams.exchange.common.utils.JsonUtils;
import com.joyintech.tams.exchange.datacommon.db.service.EntrustService;
import com.joyintech.tams.exchange.datacommon.db.service.TradeService;
import com.joyintech.tams.exchange.service.trade.processor.others.TerminalHelper;
import com.joyintech.tams.exchange.service.trade.server.agent.AccountManager;
import com.joyintech.tams.exchange.service.trade.server.agent.TradeAgent;
import com.joyintech.tams.exchange.service.trade.server.agent.TradeAgentManager;

@Component
public class AccountManagement {
    private static final Logger logger = LoggerFactory.getLogger(AccountManagement.class);

    @Autowired
    private EntrustService entrustService;
    @Autowired
    private TradeService tradeService;
    @Autowired
    private TerminalHelper terminalHelper;
    @Autowired
    private TradeAgentManager tradeAgentManager;
    @Autowired
    private AccountManager accountManager;

    @Autowired
    private InvestmentManagement investmentManagement;

    // 账户现存于投管，查询时调用投管服务
    public ResultBean queryAccount(String id) {
        if (!Strings.isNullOrEmpty(id)) {
            return new ResultBean(ErrorCode.SUCCESS, investmentManagement.queryCapitalAccountByIdOrAgentType(id, null));
        } else {
            return new ResultBean(ErrorCode.SUCCESS, investmentManagement.queryCapitalAccountAll());
        }
    }

    public ResultBean connectAccount(String accountId, int connectStatus) {
        return terminalHelper.connectAccount(accountId, connectStatus);
    }

    public ResultBean compareAccount(String accountId, int compareType) {
        Account account = accountManager.getAccountById(accountId);
        if (Objects.isNull(account)) return new ResultBean(ErrorCode.ACCOUNT_NOT_LOGIN);
            try {
                if (CompareType.COMPARE_ALL.value == compareType) {
                    List<Entrust> entrustList = terminalHelper.queryEntrustFromTerminal(accountId);
                    List<Trade> tradeList = terminalHelper.queryTradeFromTerminal(accountId);
                    compareEntrust(account, entrustList, true);
                    compareTrade(account, tradeList, true);
                } else if (CompareType.COMPARE_ENTRUST.value == compareType) {
                    List<Entrust> entrustList = terminalHelper.queryEntrustFromTerminal(accountId);
                    compareEntrust(account, entrustList, true);
                } else if (CompareType.COMPARE_TRADE.value == compareType) {
                    List<Trade> tradeList = terminalHelper.queryTradeFromTerminal(accountId);
                    compareTrade(account, tradeList, true);
                } else {
                    List<Entrust> entrustList = terminalHelper.queryEntrustFromTerminal(accountId);
                    List<Trade> tradeList = terminalHelper.queryTradeFromTerminal(accountId);
                    compareEntrust(account, entrustList, false);
                    compareTrade(account, tradeList, false);
                }
            } catch (Exception e) {
                return new ResultBean(ErrorCode.TERMINAL_API_ERROR.value, e.toString());
            }
        return new ResultBean(ErrorCode.SUCCESS);
    }

    private void compareEntrust(Account account, List<Entrust> entrustList, boolean overWrite) {
        List<Entrust> updateList = new ArrayList<>();
        Map<String, Entrust> dbEntrusts = entrustService.queryEntrustByAccountId(account.getId()).stream().collect(Collectors.toMap(Entrust::getBrokerEntrustId, entrust -> entrust));
        entrustList.forEach(entrust -> {
            Entrust dbEntrust = dbEntrusts.get(entrust.getBrokerEntrustId());
            if (Objects.nonNull(dbEntrust)) {
                if (dbEntrust.getEntrustStatus() != entrust.getEntrustStatus()
                        || dbEntrust.getTradedVolume() != entrust.getTradedVolume()
                        || dbEntrust.getTradedPrice() != entrust.getTradedPrice()) {
                    logger.info("dbEntrust not same with terminal: {}-{} entrustStatus:{}-{},TradedVolume:{}-{},TradedPrice:{}-{}",
                            dbEntrust.getId(), dbEntrust.getStockCode(), dbEntrust.getEntrustStatus(), entrust.getEntrustStatus(), dbEntrust.getTradedVolume(), entrust.getTradedVolume(), dbEntrust.getTradedPrice(), entrust.getTradedPrice());
                    if (overWrite) {
                        dbEntrust.setEntrustStatus(entrust.getEntrustStatus());
                        dbEntrust.setTradedVolume(entrust.getTradedVolume());
                        dbEntrust.setTradedPrice(entrust.getTradedPrice());
                        updateList.add(dbEntrust);
                    }
                }
            } else {
                logger.info("entrust not found in com.joyintech.tams.exchange.data.common.db: {}", JsonUtils.getJsonFromObject(entrust));
                if (overWrite) {
                    entrust.setAccountId(account.getId());
                    entrust.setAccountName(account.getAccountName());
                    entrust.setFinanceAccount(account.getFinanceAccount());
                    entrust.setInstructionId("");
                    entrust.setPortfolioId("");
                    entrust.setUserId("");
                    entrust.setUsername("system_compare");
                    entrust.setAssetType(InvestAssetTypeEnum.STOCK.value);
                    entrustService.asyncUpdate(entrust);
                }
            }
        });
        if (!updateList.isEmpty()) entrustService.asyncUpdate(updateList);
    }

    private void compareTrade(Account account, List<Trade> tradeList, boolean overWrite) {
        List<Trade> updateList = new ArrayList<>();
        Map<String, Trade> dbTrades = tradeService.queryTradeByAccountId(account.getId()).stream().collect(Collectors.toMap(Trade::getBrokerTradeId, trade -> trade));
        tradeList.forEach(trade -> {
            Trade dbTrade = dbTrades.get(trade.getBrokerTradeId());
            if (Objects.nonNull(dbTrade)) {
                if (dbTrade.getTradedVolume() != trade.getTradedVolume()
                        || dbTrade.getTradedPrice() != trade.getTradedPrice()) {
                    logger.info("dbTrade not same with terminal: TradedVolume:{}-{},TradedPrice:{}-{}",
                            dbTrade.getTradedVolume(), trade.getTradedVolume(), dbTrade.getTradedPrice(), trade.getTradedPrice());
                    if (overWrite) {
                        dbTrade.setTradedVolume(trade.getTradedVolume());
                        dbTrade.setTradedPrice(trade.getTradedPrice());
                        updateList.add(dbTrade);
                    }
                }
            } else {
                logger.info("trade not found in com.joyintech.tams.exchange.data.common.db: {}", JsonUtils.getJsonFromObject(trade));
                if (overWrite) {
                    trade.setAccountId(account.getId());
                    trade.setAccountName(account.getAccountName());
                    trade.setFinanceAccount(account.getFinanceAccount());
                    trade.setInstructionId("");
                    trade.setAssetType(InvestAssetTypeEnum.STOCK.value);
                    tradeService.asyncUpdate(trade);
                }
            }
        });
        if (!updateList.isEmpty()) tradeService.asyncUpdate(updateList);
    }

    public ResultBean queryAccountFund(String accountId) {
        if (Objects.nonNull(accountId)) {
            try {
                TradeAgent tradeAgent = tradeAgentManager.findAgentByAccountId(accountId);
                List<AccountFinance> accountFinance = tradeAgent.reqCustomerFinance(accountId);
                if (Objects.nonNull(accountFinance)) {
                    return new ResultBean(ErrorCode.SUCCESS, accountFinance);
                } else {
                    return new ResultBean(ErrorCode.ACCOUNT_FUND_NOT_FOUND);
                }
            } catch (Exception e) {
                logger.info("queryAccountFund error:{}", e);
                return new ResultBean(ErrorCode.TERMINAL_API_ERROR, e);
            }
        } else {
            return new ResultBean(ErrorCode.REQ_PARAMETER_ERROR);
        }
    }

    public ResultBean queryAccountPosition(String accountId, String stockCode, int pageSize, String positionStr) {
        if (Objects.nonNull(accountId)) {
            try {
                TradeAgent tradeAgent = tradeAgentManager.findAgentByAccountId(accountId);
                List<PositionBean> positionBeans = tradeAgent.reqAccountPosition(new PositionQueryBean(accountId, stockCode, pageSize, positionStr));
                if (Objects.nonNull(positionBeans)) {
                    return new ResultBean(ErrorCode.SUCCESS, positionBeans);
                } else {
                    return new ResultBean(ErrorCode.ACCOUNT_POSITION_NOT_FOUND);
                }
            } catch (Exception e) {
                logger.info("queryAccountFund error:{}", e);
                return new ResultBean(ErrorCode.TERMINAL_API_ERROR, e);
            }
        } else {
            return new ResultBean(ErrorCode.REQ_PARAMETER_ERROR);
        }
    }

    public ResultBean queryAccountDiscount(String accountId, String market, String stockCode) {
        return terminalHelper.queryAccountDiscount(accountId, market, stockCode);
    }

    public ResultBean queryAccountStandardBonds(String accountId, String market, String stockCode) {
        return terminalHelper.queryAccountStandardBonds(accountId, market, stockCode);
    }

    public ResultBean queryAccountAvailableStandardBonds(String accountId, String market) {
        return terminalHelper.queryAccountAvailableStandardBonds(accountId, market);
    }

    public ResultBean queryAccountFuturesPosition(String accountId, String stockCode, int pageSize, String positionStr) {
        if (Objects.nonNull(accountId)) {
            try {
                TradeAgent tradeAgent = tradeAgentManager.findAgentByAccountId(accountId);
                List<FuturesPositionBean> futuresPositionBeans = tradeAgent.reqAccountFuturesPosition(new FuturesPositionQryBean(accountId, stockCode, pageSize, positionStr));
                if (Objects.nonNull(futuresPositionBeans)) {
                    return new ResultBean(ErrorCode.SUCCESS, futuresPositionBeans);
                } else {
                    return new ResultBean(ErrorCode.ACCOUNT_POSITION_NOT_FOUND);
                }
            } catch (Exception e) {
                logger.info("queryAccountFuturesPosition error:{}", e);
                return new ResultBean(ErrorCode.TERMINAL_API_ERROR, e.getMessage());
            }
        } else {
            return new ResultBean(ErrorCode.REQ_PARAMETER_ERROR);
        }
    }

    public ResultBean queryAccountFuturesPositionDetail(String accountId, String stockCode, int pageSize, String positionStr) {
        if (Objects.nonNull(accountId)) {
            try {
                TradeAgent tradeAgent = tradeAgentManager.findAgentByAccountId(accountId);
                List<FuturesPositionDetailBean> futuresPositionDetailBeans = tradeAgent.reqAccountFuturesPositionDetail(new FuturesPositionDetailQryBean(accountId, stockCode, pageSize, positionStr));
                if (Objects.nonNull(futuresPositionDetailBeans)) {
                    return new ResultBean(ErrorCode.SUCCESS, futuresPositionDetailBeans);
                } else {
                    return new ResultBean(ErrorCode.ACCOUNT_POSITION_NOT_FOUND);
                }
            } catch (Exception e) {
                logger.info("queryAccountFuturesPositionDetail error:{}", e);
                return new ResultBean(ErrorCode.TERMINAL_API_ERROR, e.getMessage());
            }
        } else {
            return new ResultBean(ErrorCode.REQ_PARAMETER_ERROR);
        }
    }
}
