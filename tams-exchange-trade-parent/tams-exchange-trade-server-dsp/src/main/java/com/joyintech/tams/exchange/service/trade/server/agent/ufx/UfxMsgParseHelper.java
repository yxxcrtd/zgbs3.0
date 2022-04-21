package com.joyintech.tams.exchange.service.trade.server.agent.ufx;

import com.google.common.base.Strings;
import com.joyintech.tams.exchange.common.bean.AccountFinance;
import com.joyintech.tams.exchange.common.bean.HistoryTrade;
import com.joyintech.tams.exchange.common.bean.IntegratedBulkCloseQuoteBean;
import com.joyintech.tams.exchange.common.bean.IntegratedBulkQuoteBean;
import com.joyintech.tams.exchange.common.bean.IntegratedFixedIncomeQuoteBean;
import com.joyintech.tams.exchange.common.bean.IntegratedQuote;
import com.joyintech.tams.exchange.common.bean.IntegratedRepurchaseQuoteBean;
import com.joyintech.tams.exchange.common.bean.PledgeDiscount;
import com.joyintech.tams.exchange.common.bean.QuoteQryBean;
import com.joyintech.tams.exchange.common.bean.ResultBean;
import com.joyintech.tams.exchange.common.bean.StandardBonds;
import com.joyintech.tams.exchange.common.constant.BSFlag;
import com.joyintech.tams.exchange.common.constant.CbptransType;
import com.joyintech.tams.exchange.common.constant.EntrustBs;
import com.joyintech.tams.exchange.common.constant.EntrustStatus;
import com.joyintech.tams.exchange.common.constant.ExchangeType;
import com.joyintech.tams.exchange.common.constant.FullPriceConst;
import com.joyintech.tams.exchange.common.constant.Market;
import com.joyintech.tams.exchange.common.constant.TradeType;
import com.joyintech.tams.exchange.common.db.domain.Entrust;
import com.joyintech.tams.exchange.common.db.domain.EntrustInterface;
import com.joyintech.tams.exchange.common.db.domain.Instrument;
import com.joyintech.tams.exchange.common.db.domain.IntegratedEntrust;
import com.joyintech.tams.exchange.common.db.domain.LoginInfo;
import com.joyintech.tams.exchange.common.db.domain.PositionBean;
import com.joyintech.tams.exchange.common.db.domain.Trade;
import com.joyintech.tams.exchange.common.utils.CbptransTypeUtils;
import com.joyintech.tams.exchange.common.utils.EntrustUtils;
import com.joyintech.tams.exchange.common.utils.ErrorMsgUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author lifangpeng
 * @date 2021/10/14
 */
public class UfxMsgParseHelper {
    private static final Logger logger = LoggerFactory.getLogger(UfxMsgParseHelper.class);

    /**
     * 登录信息
     */
    public static LoginInfo parseLoginInfo(Map<String, Object> ds) {
        LoginInfo loginInfo = new LoginInfo();
        loginInfo.setInitDate(Integer.parseInt((String) ds.get("init_date")));
        loginInfo.setSysStatus((String) ds.get("sys_status"));
        loginInfo.setCompanyName((String) ds.get("company_name"));
        loginInfo.setContentType((String) ds.get("content_type"));
        loginInfo.setAccountContent((String) ds.get("account_content"));
        loginInfo.setBranchNo(Integer.parseInt((String) ds.get("branch_no")));
        loginInfo.setClientId((String) ds.get("client_id"));
        loginInfo.setClientName((String) ds.get("client_name"));
        loginInfo.setCorpClientGroup(Integer.parseInt((String) ds.get("corp_client_group")));
        loginInfo.setCorpRiskLevel(Integer.parseInt((String) ds.get("corp_risk_level")));
        loginInfo.setCorpEndDate(Integer.parseInt((String) ds.get("corp_end_date")));
        loginInfo.setValidFlag((String) ds.get("valid_flag"));
        loginInfo.setFundaccountCount(Integer.parseInt((String) ds.get("fundaccount_count")));
        loginInfo.setFundAccount((String) ds.get("fund_account"));
        loginInfo.setClientRights((String) ds.get("client_rights"));
        loginInfo.setLastOpEntrustWay((String) ds.get("last_op_entrust_way"));
        loginInfo.setLastOpStation((String) ds.get("last_op_station"));
        loginInfo.setSysnodeId(Integer.parseInt((String) ds.get("sysnode_id")));
        loginInfo.setUserToken((String) ds.get("user_token"));
        loginInfo.setErrorNo(Integer.parseInt((String) ds.get("error_no")));
        loginInfo.setErrorInfo((String) ds.get("error_info"));
        loginInfo.setAssetProp((String) ds.get("asset_prop"));
        loginInfo.setProductFlag((String) ds.get("product_flag"));
        loginInfo.setMessageFlag((String) ds.get("message_flag"));
        loginInfo.setTabconfirmFlag((String) ds.get("tabconfirm_flag"));
        loginInfo.setLastDate(Integer.parseInt((String) ds.get("last_date")));
        loginInfo.setSessionNo(Integer.parseInt((String) ds.get("session_no")));
        loginInfo.setForeignFlag((String) ds.get("foreign_flag"));
        loginInfo.setPasswordRiskTips((String) ds.get("password_risk_tips"));
        loginInfo.setInputContent((String) ds.get("input_content"));
        loginInfo.setTerminalVersion((String) ds.get("terminal_version"));
        loginInfo.setUftSysnodeId(Integer.parseInt((String) ds.get("uft_sysnode_id")));
        return loginInfo;
    }

    public static List<Entrust> parseEntrust(String accountId, List<Map<String, Object>> resList) {
        List<Entrust> list = new ArrayList<>();
        for (Map<String, Object> ds : resList) {
            Entrust entrust = new Entrust();
            entrust.setAccountId(accountId);
            entrust.setPositionStr((String) ds.get("position_str"));
            entrust.setTradingDay((String) ds.get("init_date"));
            entrust.setBrokerEntrustId((String) ds.get("entrust_no"));
            entrust.setTradingDay((String) ds.get("entrust_date"));
            entrust.setReportTime((String) ds.get("entrust_time"));
            entrust.setFinanceAccount((String) ds.get("fund_account"));
            String exchangeType = (String) ds.get("exchange_type");
            if (ExchangeType.SH.value.equals(exchangeType)) {
                entrust.setMarket(Market.SHSE.value);
            } else if (ExchangeType.SZ.value.equals(exchangeType)) {
                entrust.setMarket(Market.SZSE.value);
            }
            entrust.setStockCode((String) ds.get("stock_code"));
            entrust.setStockName((String) ds.get("stock_name"));
            entrust.setEntrustBs((String) ds.get("entrust_bs"));
            entrust.setEntrustType((String) ds.get("entrust_type"));
            entrust.setEntrustProp((String) ds.get("entrust_prop"));
            BigDecimal entrustPrice = new BigDecimal((String) ds.get("entrust_price"));
            if (FullPriceConst.FULL_PRICE.value.equals(entrust.getIsFullPrice()) ||
                    (Strings.isNullOrEmpty(entrust.getIsFullPrice()) &&
                            EntrustUtils.isFullPriceTerminal(entrust.getMarket(), entrust.getStockCode(), entrust.getAssetType(), entrust.getEntrustProp()))) {
                entrust.setFullPrice(entrustPrice);
                entrust.setIsFullPrice(FullPriceConst.FULL_PRICE.value);
            } else {
                entrust.setEntrustPrice(entrustPrice);
                entrust.setIsFullPrice(FullPriceConst.NET_PRICE.value);
            }
            String entrustAmount = (String) ds.get("entrust_amount");
            if (!Strings.isNullOrEmpty(entrustAmount)) {
                entrust.setEntrustVolume(Double.valueOf(entrustAmount).longValue());
            }
            String businessAmount = (String) ds.get("business_amount");
            if (!Strings.isNullOrEmpty(businessAmount)) {
                try {
                    entrust.setTradedVolume(Double.valueOf(businessAmount).longValue());
                } catch (Exception e) {
                    logger.error("businessAmount:{}", e.getMessage());
                }
            }
            entrust.setTradedPrice(new BigDecimal((String) ds.get("business_price")));
            entrust.setReportTime((String) ds.get("report_time"));
            try {
                String entrustStatus = (String) ds.get("entrust_status");
                if (entrustStatus.lastIndexOf(".") == -1) {
                    getEntrustStatusByUfx(entrustStatus, entrust);
                } else {
                    logger.error("entrustStatus:{}", entrustStatus);
                }
            } catch (Exception e) {
                logger.error("entrustStatus:{}", e.getMessage());
            }
            entrust.setErrorMsg(ErrorMsgUtils.subErrorMsg((String) ds.get("cancel_info")));
            String withdrawAmount = (String) ds.get("withdraw_amount");
            if (!Strings.isNullOrEmpty(withdrawAmount)) {
                entrust.setCancelVolume(Double.valueOf(withdrawAmount).longValue());
            }
            entrust.setId((String) ds.get("entrust_reference"));
            entrust.setReportNo((String) ds.get("report_no"));
            list.add(entrust);
        }

        return list;
    }

    public static List<IntegratedEntrust> parseIntegratedEntrust(String accountId, List<Map<String, Object>> resList) {
        List<IntegratedEntrust> list = new ArrayList<IntegratedEntrust>();
        for (Map<String, Object> ds : resList) {
            IntegratedEntrust entrust = new IntegratedEntrust();
            entrust.setAccountId(accountId);
            entrust.setPositionStr((String) ds.get("position_str"));
            entrust.setTradingDay((String) ds.get("init_date"));
            entrust.setBrokerEntrustId((String) ds.get("entrust_no"));
            entrust.setTradingDay((String) ds.get("entrust_date"));
            entrust.setReportTime((String) ds.get("entrust_time"));
            entrust.setFinanceAccount((String) ds.get("fund_account"));
            String exchangeType = (String) ds.get("exchange_type");
            if (ExchangeType.SH.value.equals(exchangeType)) {
                entrust.setMarket(Market.SHSE.value);
            } else if (ExchangeType.SZ.value.equals(exchangeType)) {
                entrust.setMarket(Market.SZSE.value);
            }
            entrust.setReportNo((String) ds.get("report_no"));
            entrust.setStockCode((String) ds.get("stock_code"));
            entrust.setStockName((String) ds.get("stock_name"));
            entrust.setEntrustBs((String) ds.get("entrust_bs"));
            entrust.setEntrustType((String) ds.get("entrust_type"));
            entrust.setEntrustProp((String) ds.get("entrust_prop"));
            if (FullPriceConst.FULL_PRICE.value.equals(entrust.getIsFullPrice()) ||
                    (Strings.isNullOrEmpty(entrust.getIsFullPrice()) &&
                            EntrustUtils.isFullPriceTerminal(entrust.getMarket(), entrust.getStockCode(), entrust.getAssetType(), entrust.getEntrustProp()))) {
                entrust.setFullPrice(BigDecimal.valueOf(Double.parseDouble((String) ds.get("entrust_price"))));
                entrust.setIsFullPrice(FullPriceConst.FULL_PRICE.value);
            } else {
                entrust.setEntrustPrice(BigDecimal.valueOf(Double.parseDouble((String) ds.get("entrust_price"))));
                entrust.setIsFullPrice(FullPriceConst.NET_PRICE.value);
            }
            String entrustAmount = (String) ds.get("entrust_amount");
            if (!Strings.isNullOrEmpty(entrustAmount)) {
                double entrust_amount = Double.parseDouble(entrustAmount);
                entrust.setEntrustVolume((int) entrust_amount);
            }
            String businessAmount = (String) ds.get("business_amount");
            if (!Strings.isNullOrEmpty(businessAmount)) {
                try {
                    if (businessAmount.lastIndexOf(".") != -1) {
                        entrust.setTradedVolume((long) Double.parseDouble(businessAmount));
                    } else {
                        logger.error("businessAmount:{}", businessAmount);
                    }
                } catch (Exception e) {
                    logger.error("businessAmount:{}", e.getMessage());
                }
            }
            entrust.setTradedPrice(BigDecimal.valueOf(Double.parseDouble((String) ds.get("business_price"))));
            entrust.setReportTime((String) ds.get("report_time"));
            try {
                String entrustStatus = (String) ds.get("entrust_status");
                if (entrustStatus.lastIndexOf(".") == -1) {
                    getEntrustStatusByUfx(entrustStatus, entrust);
                } else {
                    logger.error("entrustStatus:{}", entrustStatus);
                }
            } catch (Exception e) {
                logger.error("entrustStatus:{}", e.getMessage());
            }
            String cancelInfo = (String) ds.get("cancel_info");
            String remark = (String) ds.get("remark");
            if (!Strings.isNullOrEmpty(cancelInfo)) {
                entrust.setErrorMsg(ErrorMsgUtils.subErrorMsg(cancelInfo));
            } else if (!Strings.isNullOrEmpty(remark)) {
                entrust.setErrorMsg(ErrorMsgUtils.subErrorMsg(remark));
            }
            String withdrawAmount = (String) ds.get("withdraw_amount");
            if (!Strings.isNullOrEmpty(withdrawAmount)) {
                entrust.setCancelVolume(Long.parseLong(withdrawAmount));
            }

            //确认国金的协议回购和固收委托是否是这个字段
            entrust.setId((String) ds.get("entrust_reference"));

            list.add(entrust);
        }
        return list;
    }

    public static List<Trade> parseTrades(String accountId, List<Map<String, Object>> resList) {
        List<Trade> list = new ArrayList<>();
        for (Map<String, Object> ds : resList) {
            Trade trade = new Trade();
            trade.setPositionStr((String) ds.get("position_str"));
            trade.setAccountId(accountId);
            trade.setTradingDay((String) ds.get("date"));
            String exchangeType = (String) ds.get("exchange_type");
            if (ExchangeType.SH.value.equals(exchangeType)) {
                trade.setMarket(Market.SHSE.value);
            } else if (ExchangeType.SZ.value.equals(exchangeType)) {
                trade.setMarket(Market.SZSE.value);
            }
            trade.setFinanceAccount((String) ds.get("fund_account"));
            trade.setStockCode((String) ds.get("stock_code"));
            trade.setEntrustBs((String) ds.get("entrust_bs"));
            trade.setTradedPrice(new BigDecimal((String) ds.get("business_price")));
            String businessAmount = (String) ds.get("business_amount");
            double business_amount = Double.parseDouble(businessAmount);
            //撤单成交成交数量小于0
            trade.setTradedVolume((long) (business_amount < 0 ? -business_amount : business_amount));
            trade.setTradeTime((String) ds.get("business_time"));
            int realType = Integer.parseInt((String) ds.get("real_type"));
            //处理标志
            int realStatus = Integer.parseInt((String) ds.get("real_status"));
            if (realStatus != 0 && realStatus != 4) {
                trade.setBrokerTradeId((String) ds.get("entrust_no"));
                if (2 == realType) {
                    trade.setTradeType(TradeType.CANCEL_INVALID.value);
                } else if (0 == realType) {
                    trade.setTradeType(TradeType.INVALID.value);
                }
            } else {
                trade.setBrokerTradeId((String) ds.get("business_id"));
                if (2 == realType) {
                    trade.setTradeType(TradeType.CANCEL.value);
                } else if (0 == realType) {
                    trade.setTradeType(TradeType.NORMAL.value);
                }
            }
            trade.setBrokerEntrustId((String) ds.get("entrust_no"));
            trade.setErrorMsg(ErrorMsgUtils.subErrorMsg((String) ds.get("cancel_info")));
            trade.setTradedBalance(new BigDecimal((String) ds.get("business_balance")));
            trade.setStockName((String) ds.get("stock_name"));
            trade.setEntrustProp((String) ds.get("entrust_prop"));
            trade.setReportNo((String) ds.get("report_no"));
            list.add(trade);
        }
        return list;
    }

    public static List<Trade> qryProtocolBackTradeBack(String accountId, List<Map<String, Object>> resList) {
        List<Trade> list = new ArrayList<>();
        for (Map<String, Object> ds : resList) {
            //处理标志
            int realStatus = Integer.parseInt((String) ds.get("real_status"));
            Trade trade = new Trade();
            trade.setAccountId(accountId);
            trade.setPositionStr((String) ds.get("position_str"));
            trade.setTradingDay((String) ds.get("init_date"));
            trade.setBrokerEntrustId((String) ds.get("entrust_no"));
            trade.setFinanceAccount((String) ds.get("fund_account"));
            String exchangeType = (String) ds.get("exchange_type");
            if (ExchangeType.SH.value.equals(exchangeType)) {
                trade.setMarket(Market.SHSE.value);
            } else if (ExchangeType.SZ.value.equals(exchangeType)) {
                trade.setMarket(Market.SZSE.value);
            }
            trade.setStockCode((String) ds.get("stock_code"));
            trade.setStockName((String) ds.get("stock_name"));
            trade.setEntrustBs((String) ds.get("entrust_bs"));
            trade.setEntrustProp((String) ds.get("entrust_prop"));
            trade.setTradedVolume((long) Double.parseDouble((String) ds.get("business_amount")));
            trade.setPropSeatNo((String) ds.get("prop_seat_no"));
            int realType = Integer.parseInt((String) ds.get("real_type"));
            if (realStatus != 0 && realStatus != 4) {
                trade.setBrokerTradeId((String) ds.get("entrust_no"));
                if (2 == realType) {
                    trade.setTradeType(TradeType.CANCEL_INVALID.value);
                } else if (0 == realType) {
                    trade.setTradeType(TradeType.INVALID.value);
                }
            } else {
                trade.setBrokerTradeId((String) ds.get("business_id"));
                if (2 == realType) {
                    trade.setTradeType(TradeType.CANCEL.value);
                } else if (0 == realType) {
                    trade.setTradeType(TradeType.NORMAL.value);
                }
            }
            trade.setTradedPrice(BigDecimal.valueOf(Double.parseDouble((String) ds.get("business_price"))));
            trade.setTradedBalance(BigDecimal.valueOf(Double.parseDouble((String) ds.get("business_balance"))));
            String businessTime = (String) ds.get("business_time");
            if (!Strings.isNullOrEmpty(businessTime)) {
                trade.setTradeTime(businessTime);
            }
            list.add(trade);
        }
        return list;
    }

    public static List<IntegratedQuote> parseIntegratedQuotes(ResultBean resultBean, QuoteQryBean quoteQryBean) {
        List<IntegratedQuote> list = new ArrayList<>();
        List<Map<String, Object>> resList = (List<Map<String, Object>>) resultBean.getData();
        for (Map<String, Object> ds : resList) {
            IntegratedQuote integratedQuoteBt = new IntegratedQuote();
            integratedQuoteBt.setContractId((String) ds.get("cbpconfer_id"));
            integratedQuoteBt.setBrokerEntrustId((String) ds.get("entrust_no"));
            integratedQuoteBt.setBuyConfirmId((String) ds.get("buy_confirm_id"));
            String cbpTransType = (String) ds.get("cbptrans_type");
            if (!Strings.isNullOrEmpty(cbpTransType)) {
                if (CbptransType.YXMR.value.equals(cbpTransType)) {
                    integratedQuoteBt.setBsFlag(BSFlag.YXMR.value);
                } else if (CbptransType.YXMC.value.equals(cbpTransType)) {
                    integratedQuoteBt.setBsFlag(BSFlag.YXMC.value);
                } else if (CbptransType.DJMR.value.equals(cbpTransType)) {
                    integratedQuoteBt.setBsFlag(BSFlag.DJMR.value);
                } else if (CbptransType.DJMC.value.equals(cbpTransType)) {
                    integratedQuoteBt.setBsFlag(BSFlag.DJMC.value);
                }
                integratedQuoteBt.setEntrustProp(EntrustUtils.getEntrustProp(integratedQuoteBt.getBsFlag(), integratedQuoteBt.getMarket()));
                integratedQuoteBt.setEntrustBs(EntrustUtils.getEntrustBs(integratedQuoteBt.getBsFlag()));
            }
            String entrustProp = (String) ds.get("entrust_prop");
            if (!Strings.isNullOrEmpty(entrustProp)) {
                integratedQuoteBt.setEntrustProp(entrustProp);
            }
            String entrustBs = (String) ds.get("entrust_bs");
            if (!Strings.isNullOrEmpty(entrustBs)) {
                integratedQuoteBt.setEntrustBs(entrustBs);
            }
            String exchangeType = (String) ds.get("exchange_type");
            if (!Strings.isNullOrEmpty(exchangeType)) {
                if (ExchangeType.SH.value.equals(exchangeType)) {
                    integratedQuoteBt.setMarket(Market.SHSE.value);
                } else if (ExchangeType.SZ.value.equals(exchangeType)) {
                    integratedQuoteBt.setMarket(Market.SZSE.value);
                }
            }
            integratedQuoteBt.setStockType((String) ds.get("stock_type"));
            integratedQuoteBt.setStockCode((String) ds.get("stock_code"));
            integratedQuoteBt.setStockName((String) ds.get("stock_name"));
            String expireYearRate = (String) ds.get("expire_year_rate");
            if (!Strings.isNullOrEmpty(expireYearRate)) {
                integratedQuoteBt.setExpireIncomeRate(Double.parseDouble(expireYearRate));
            }
            String repoTerm = (String) ds.get("repo_term");
            if (!Strings.isNullOrEmpty(repoTerm)) {
                integratedQuoteBt.setCompactTerm(Integer.parseInt(repoTerm));
            }
            String fundUsedDays = (String) ds.get("fund_used_days");
            if (!Strings.isNullOrEmpty(fundUsedDays)) {
                integratedQuoteBt.setRealOccupyDays(Integer.parseInt(fundUsedDays));
            }
            integratedQuoteBt.setOrigTradeDate((String) ds.get("first_exchdate"));
            integratedQuoteBt.setBackDate((String) ds.get("repurchase_date"));
            integratedQuoteBt.setExpireSettleDate((String) ds.get("settle_end_date"));
            String componentNum = (String) ds.get("component_num");
            if (!Strings.isNullOrEmpty(componentNum)) {
                integratedQuoteBt.setComponentNum(Integer.parseInt(componentNum));
            }
            integratedQuoteBt.setOrigPledgeStampCode((String) ds.get("old_impawn_code"));
            String impawnAmount = (String) ds.get("impawn_amount");
            if (!Strings.isNullOrEmpty(impawnAmount)) {
                integratedQuoteBt.setPledgeStampVolume(Integer.parseInt(impawnAmount.substring(0, impawnAmount.lastIndexOf("."))));
            }
            String funderRatio = (String) ds.get("funder_ratio");
            if (!Strings.isNullOrEmpty(funderRatio)) {
                integratedQuoteBt.setDiscount(Double.parseDouble(funderRatio));
            }
            String impawnBalance = (String) ds.get("impawn_balance");
            if (!Strings.isNullOrEmpty(impawnBalance)) {
                integratedQuoteBt.setImpawnBalance(Double.parseDouble(impawnBalance));
            }
            String prevImpawnBalance = (String) ds.get("prev_impawn_balance");
            if (!Strings.isNullOrEmpty(prevImpawnBalance)) {
                integratedQuoteBt.setPrevImpawnBalance(Double.parseDouble(prevImpawnBalance));
            }
            String profit = (String) ds.get("profit");
            if (!Strings.isNullOrEmpty(profit)) {
                integratedQuoteBt.setBackInterest(Double.parseDouble(profit));
            }
            String settleBalance = (String) ds.get("settle_balance");
            if (!Strings.isNullOrEmpty(settleBalance)) {
                integratedQuoteBt.setExpireSettleBalance(Double.parseDouble(settleBalance));
            }
            String assetNetValue = (String) ds.get("asset_net_value");
            if (!Strings.isNullOrEmpty(assetNetValue)) {
                integratedQuoteBt.setPledgeStampBalance(Double.parseDouble(assetNetValue));
            }
            integratedQuoteBt.setBprsrcStatus((String) ds.get("bprsrc_status"));
            integratedQuoteBt.setOrigEntrustDate((String) ds.get("orig_entrust_date"));
            integratedQuoteBt.setOrigCbpconferId((String) ds.get("orig_cbpconfer_id"));
            String agencyNo = (String) ds.get("agency_no");
            if (!Strings.isNullOrEmpty(agencyNo)) {
                integratedQuoteBt.setAgencyNo(agencyNo);
            }
            integratedQuoteBt.setPropSeatNo((String) ds.get("prop_seat_no"));
            String traderId = (String) ds.get("trader_id");
            if (!Strings.isNullOrEmpty(traderId)) {
                integratedQuoteBt.setLiaisonTel(traderId);
            }
            integratedQuoteBt.setFundAccount((String) ds.get("fund_account"));
            integratedQuoteBt.setStockAccount((String) ds.get("stock_account"));
            integratedQuoteBt.setReportTimeX((String) ds.get("report_time_x"));
            integratedQuoteBt.setReportTime((String) ds.get("report_time"));
            integratedQuoteBt.setPropSeatName((String) ds.get("agency_name"));
            integratedQuoteBt.setRemark((String) ds.get("remark"));
            integratedQuoteBt.setAccountId(quoteQryBean.getAccountId());
            integratedQuoteBt.setOrderNo((String) ds.get("order_no"));
            integratedQuoteBt.setPropAccountName((String) ds.get("prop_account_name"));
            integratedQuoteBt.setSeatNo((String) ds.get("seat_no"));
            String oppoAgency = (String) ds.get("oppo_agency");
            if (!Strings.isNullOrEmpty(oppoAgency)) {
                integratedQuoteBt.setOppositePropSeatNo(oppoAgency);
            }
            String otherAgency = (String) ds.get("other_agency");
            if (!Strings.isNullOrEmpty(otherAgency)) {
                integratedQuoteBt.setOppositePropSeatNo(otherAgency);
            }
            String sellPrice = (String) ds.get("sell_price");
            if (!Strings.isNullOrEmpty(sellPrice)) {
                integratedQuoteBt.setSellPrice(Double.parseDouble(sellPrice));
            }
            String sellQuoteAmount = (String) ds.get("sell_quote_amount");
            if (!Strings.isNullOrEmpty(sellQuoteAmount)) {
                integratedQuoteBt.setSellQuoteAmount(Integer.parseInt(sellQuoteAmount.substring(0, sellQuoteAmount.lastIndexOf("."))));
            }
            String sellQuotePrice = (String) ds.get("sell_quote_price");
            if (!Strings.isNullOrEmpty(sellQuotePrice)) {
                integratedQuoteBt.setSellQuotePrice(Double.parseDouble(sellQuotePrice));
            }
            String sellExchangeRate = (String) ds.get("sell_exchange_rate");
            if (!Strings.isNullOrEmpty(sellExchangeRate)) {
                integratedQuoteBt.setSellExchangeRate(Double.parseDouble(sellExchangeRate));
            }
            String stockInterest = (String) ds.get("stock_interest");
            if (!Strings.isNullOrEmpty(stockInterest)) {
                integratedQuoteBt.setBondAccruedRate(Double.parseDouble(stockInterest));
            }
            integratedQuoteBt.setOppositeLiaisonTel((String) ds.get("oppo_trader_id"));
            integratedQuoteBt.setPosstr((String) ds.get("position_str"));
            integratedQuoteBt.setOppositePropSeatName((String) ds.get("oppo_agency_name"));
            integratedQuoteBt.setPropStockAccount((String) ds.get("prop_stock_account"));
            integratedQuoteBt.setRealDateBack((String) ds.get("real_date_back"));
            integratedQuoteBt.setZQRName((String) ds.get("pledgee_name"));
            String entrustPrice = (String) ds.get("entrust_price");
            if (!Strings.isNullOrEmpty(entrustPrice)) {
                integratedQuoteBt.setEntrustPrice(Double.parseDouble(entrustPrice));
            }
            String buyPrice = (String) ds.get("buy_price");
            if (!Strings.isNullOrEmpty(buyPrice)) {
                integratedQuoteBt.setBuyPrice(Double.parseDouble(buyPrice));
            }
            String buyQuoteAmount = (String) ds.get("buy_quote_amount");
            if (!Strings.isNullOrEmpty(buyQuoteAmount)) {
                integratedQuoteBt.setBuyQuoteAmount(Integer.parseInt(buyQuoteAmount.substring(0, buyQuoteAmount.lastIndexOf("."))));
            }
            String buyQuotePrice = (String) ds.get("buy_quote_price");
            if (!Strings.isNullOrEmpty(buyQuotePrice)) {
                integratedQuoteBt.setBuyQuotePrice(Double.parseDouble(buyQuotePrice));
            }
            String buyExchangeRate = (String) ds.get("buy_exchange_rate");
            if (!Strings.isNullOrEmpty(buyExchangeRate)) {
                integratedQuoteBt.setBuyExchangeRate(Double.parseDouble(buyExchangeRate));
            }
            integratedQuoteBt.setSellConfirmId((String) ds.get("sell_confirm_id"));
            String entrustAmount = (String) ds.get("entrust_amount");
            if (!Strings.isNullOrEmpty(entrustAmount)) {
                integratedQuoteBt.setEntrustVolume(Integer.parseInt(entrustAmount.substring(0, entrustAmount.lastIndexOf("."))));
            }
            integratedQuoteBt.setLiaisonName((String) ds.get("relation_name"));
            String relationTel = (String) ds.get("relation_tel");
            if (!Strings.isNullOrEmpty(relationTel)) {
                integratedQuoteBt.setLiaisonTel(relationTel);
            }
            list.add(integratedQuoteBt);
        }
        return list;
    }

    public static List<HistoryTrade> parseHistoryTrade(ResultBean resultBean, String accountId) {
        List<HistoryTrade> list = new ArrayList<>();
        List<Map<String, Object>> resList = (List<Map<String, Object>>) resultBean.getData();
        for (Map<String, Object> ds : resList) {
            HistoryTrade historyTrade = new HistoryTrade();
            historyTrade.setTradingDay((String) ds.get("init_date"));
            String exchangeType = (String) ds.get("exchange_type");
            if (ExchangeType.SH.value.equals(exchangeType)) {
                historyTrade.setMarket(Market.SHSE.value);
            } else if (ExchangeType.SZ.value.equals(exchangeType)) {
                historyTrade.setMarket(Market.SZSE.value);
            }
            historyTrade.setStockHolderId((String) ds.get("stock_account"));
            historyTrade.setStockCode((String) ds.get("stock_code"));
            historyTrade.setStockType((String) ds.get("stock_type"));
            historyTrade.setStockName((String) ds.get("stock_name"));
            String entrustBs = (String) ds.get("entrust_bs");
            if (EntrustBs.BUY.value.equals(entrustBs)) {
                historyTrade.setBsFlag(BSFlag.ZQMR.value);
            } else if (EntrustBs.SELL.value.equals(entrustBs)) {
                historyTrade.setBsFlag(BSFlag.ZQMC.value);
            }
            historyTrade.setTradePrice(Double.parseDouble((String) ds.get("business_price")));
            historyTrade.setTradeTime((String) ds.get("business_time"));
            historyTrade.setOrderId((String) ds.get("entrust_no"));
            historyTrade.setTradedBalance(Double.parseDouble((String) ds.get("business_balance")));
            historyTrade.setFinanceAmount(Double.parseDouble((String) ds.get("occur_balance")));
            historyTrade.setProcedureCost(Double.parseDouble((String) ds.get("fare0")));
            historyTrade.setPrintingCost(Double.parseDouble((String) ds.get("fare1")));
            historyTrade.setTransferCost(Double.parseDouble((String) ds.get("fare2")));
            historyTrade.setPostStr((String) ds.get("position_str"));
            historyTrade.setBrokerTradeId((String) ds.get("business_id"));
            historyTrade.setOtherCost(Double.parseDouble((String) ds.get("exchange_fare6")));
            historyTrade.setJinShouXuFei(Double.parseDouble((String) ds.get("clear_fare0")));
            historyTrade.setTradingDay((String) ds.get("date_back"));
            historyTrade.setAccountId(accountId);
            historyTrade.setFinanceAccount((String) ds.get("fund_account"));
            String businessAmount = (String) ds.get("business_amount");
            historyTrade.setTradeVolume(Double.valueOf(businessAmount).longValue());
            historyTrade.setBrokerTradeId((String) ds.get("business_no"));
            list.add(historyTrade);
        }
        return list;
    }

    public static List<AccountFinance> parseCustomerFinance(ResultBean resultBean, String accountId) {
        List<AccountFinance> list = new ArrayList<>();
        List<Map<String, Object>> resList = (List<Map<String, Object>>) resultBean.getData();
        for (Map<String, Object> ds : resList) {
            AccountFinance accountFinance = new AccountFinance();
            accountFinance.setAccountId(accountId);
            accountFinance.setMoneyType((String) ds.get("money_type"));
            accountFinance.setFinanceBalance(Double.parseDouble((String) ds.get("current_balance")));
            accountFinance.setEnableBalance(Double.parseDouble((String) ds.get("enable_balance")));
            accountFinance.setTotalFrozenFinance(Double.parseDouble((String) ds.get("frozen_balance")));
            list.add(accountFinance);
        }
        return list;
    }

    public static List<PositionBean> parseAccountPosition(ResultBean resultBean, String accountId) {
        List<PositionBean> list = new ArrayList<>();

        List<Map<String, Object>> resList = (List<Map<String, Object>>) resultBean.getData();
        for (Map<String, Object> ds : resList) {
            PositionBean positionBean = new PositionBean();
            positionBean.setAccountId(accountId);
            positionBean.setFinanceAccount((String) ds.get("fund_account"));
            String exchangeType = (String) ds.get("exchange_type");
            if (ExchangeType.SH.value.equals(exchangeType)) {
                positionBean.setMarket(Market.SHSE.value);
            } else if (ExchangeType.SZ.value.equals(exchangeType)) {
                positionBean.setMarket(Market.SZSE.value);
            }
            positionBean.setStockCode(ds.get("stock_code") + "." + positionBean.getMarket());
            positionBean.setStockName((String) ds.get("stock_name"));
            positionBean.setCostPrice(Double.parseDouble((String) ds.get("cost_price")));
            positionBean.setMarketValue(Double.parseDouble((String) ds.get("market_value")));
            positionBean.setHoldVolume(Long.parseLong((String) ds.get("hold_amount")));
            positionBean.setEnableVolume(Long.parseLong((String) ds.get("enable_amount")));
            positionBean.setPositionStr((String) ds.get("position_str"));
            list.add(positionBean);
        }
        return list;

    }

    public static List<Instrument> parseInstrumentInfo(ResultBean resultBean) {
        List<Instrument> list = new ArrayList<>();
        List<Map<String, Object>> resList = (List<Map<String, Object>>) resultBean.getData();
        for (Map<String, Object> ds : resList) {
            Instrument instrument = new Instrument();
            instrument.setStockCode((String) ds.get("stock_code"));
            String exchangeType = (String) ds.get("exchange_type");
            if (ExchangeType.SH.value.equals(exchangeType)) {
                instrument.setMarket(Market.SHSE.value);
                instrument.setInnerCode(instrument.getStockCode() + "." + Market.SHSE.value);
            } else if (ExchangeType.SZ.value.equals(exchangeType)) {
                instrument.setMarket(Market.SZSE.value);
                instrument.setInnerCode(instrument.getStockCode() + "." + Market.SZSE.value);
            }
            instrument.setStockName((String) ds.get("stock_name"));
            instrument.setBuyUnit(Integer.parseInt((String) ds.get("buy_unit")));
            instrument.setPriceStep(getPriceStep((String) ds.get("price_step")));
            instrument.setStoreUnit(Integer.parseInt((String) ds.get("store_unit")));
            instrument.setUpPrice(BigDecimal.valueOf(Double.parseDouble((String) ds.get("up_price"))));
            instrument.setDownPrice(BigDecimal.valueOf(Double.parseDouble((String) ds.get("down_price"))));
            instrument.setStkcodeStatus((String) ds.get("stkcode_status"));
            instrument.setAssetType((String) ds.get("stock_type"));
            instrument.setHighVolume(Long.parseLong((String) ds.get("high_amount")));
            instrument.setLowVolume(Integer.parseInt((String) ds.get("low_amount")));
            instrument.setTradePlat((String) ds.get("trade_plat"));
            instrument.setPostStr((String) ds.get("position_str"));
            instrument.setParValue(BigDecimal.valueOf(Double.parseDouble((String) ds.get("par_value"))));
            instrument.setStbtransType((String) ds.get("stbtrans_type"));
            instrument.setStbtransStatus((String) ds.get("stbtrans_status"));
            instrument.setSubStockType((String) ds.get("sub_stock_type"));
            instrument.setLowBalance(BigDecimal.valueOf(Double.parseDouble((String) ds.get("low_balance"))));
            instrument.setHighBalance(BigDecimal.valueOf(Double.parseDouble((String) ds.get("high_balance"))));
            instrument.setExdividendFlag((String) ds.get("exdividend_flag"));
            instrument.setStbtransFlag((String) ds.get("stbtrans_flag"));
            instrument.setMarketLowVolume(Integer.parseInt((String) ds.get("market_low_amount")));
            instrument.setMarketHighVolume(Long.parseLong((String) ds.get("market_high_amount")));
            instrument.setBondinvFlag((String) ds.get("bondinv_flag"));
            list.add(instrument);
        }
        return list;
    }

    public static List<PledgeDiscount> parsePledgeDiscount(ResultBean resultBean, String accountId) {
        return null;
    }

    public static List<StandardBonds> parseStandardBonds(ResultBean resultBean, String accountId) {
        List<StandardBonds> list = new ArrayList<StandardBonds>();
        List<Map<String, Object>> resList = (List<Map<String, Object>>) resultBean.getData();
        for (Map<String, Object> ds : resList) {
            StandardBonds standardBonds = new StandardBonds();
            standardBonds.setStockCode((String) ds.get("bond_code"));
            String exchangeType = (String) ds.get("exchange_type");
            if (ExchangeType.SH.value.equals(exchangeType)) {
                standardBonds.setMarket(Market.SHSE.value);
            } else if (ExchangeType.SZ.value.equals(exchangeType)) {
                standardBonds.setMarket(Market.SZSE.value);
            }
            int bondFinAmount = Double.valueOf((String) ds.get("bond_fin_amount")).intValue();
            standardBonds.setBondEnable(bondFinAmount);
            standardBonds.setStockAccount((String) ds.get("stock_account"));
            standardBonds.setStockName((String) ds.get("bond_name"));
            standardBonds.setNetAsset(Double.parseDouble((String) ds.get("net_asset")));
            int enoutAmount = Double.valueOf((String) ds.get("enout_amount")).intValue();
            standardBonds.setBondEnout(enoutAmount);
            list.add(standardBonds);
        }
        return list;
    }

    public static List<IntegratedBulkQuoteBean> parseIntegratedBulkQuote(ResultBean resultBean, String market) {
        List<IntegratedBulkQuoteBean> list = new ArrayList<>();
        List<Map<String, Object>> resList = (List<Map<String, Object>>) resultBean.getData();
        for (Map<String, Object> ds : resList) {
            IntegratedBulkQuoteBean integratedBulkQuoteBean = new IntegratedBulkQuoteBean();
            integratedBulkQuoteBean.setOrderNo((String) ds.get("order_no"));
            integratedBulkQuoteBean.setSeatNo((String) ds.get("seat_no"));
            String cbptransType = (String) ds.get("cbptrans_type");
            integratedBulkQuoteBean.setEntrustBs(CbptransTypeUtils.getEntrustBsByCbptransType(cbptransType));
            integratedBulkQuoteBean.setEntrustProp(CbptransTypeUtils.getEntrustPropByCbptransType(cbptransType));
            String stockCode = (String) ds.get("stock_code");
            if (Strings.isNullOrEmpty(market)) {
                integratedBulkQuoteBean.setStockCode(stockCode);
            } else {
                integratedBulkQuoteBean.setStockCode(stockCode + "." + market);
            }
            integratedBulkQuoteBean.setStockName((String) ds.get("stock_name"));
            integratedBulkQuoteBean.setEntrustPrice(Double.parseDouble((String) ds.get("entrust_price")));
            integratedBulkQuoteBean.setEntrustAmount(Integer.parseInt((String) ds.get("entrust_amount")));
            integratedBulkQuoteBean.setBusinessAmount(Integer.parseInt((String) ds.get("business_amount")));
            integratedBulkQuoteBean.setCompactTerm(Integer.parseInt((String) ds.get("compact_term")));
            integratedBulkQuoteBean.setRelationName((String) ds.get("relation_name"));
            integratedBulkQuoteBean.setRelationTel((String) ds.get("relation_tel"));
            integratedBulkQuoteBean.setCbpconferId((String) ds.get("cbpconfer_id"));
            integratedBulkQuoteBean.setReportTime((String) ds.get("report_time"));
            integratedBulkQuoteBean.setPositionStr((String) ds.get("position_str"));
            integratedBulkQuoteBean.setRemark((String) ds.get("remark"));
            list.add(integratedBulkQuoteBean);
        }
        return list;
    }

    public static List<IntegratedBulkCloseQuoteBean> parseIntegratedBulkCloseQuote(ResultBean resultBean, String market) {
        List<IntegratedBulkCloseQuoteBean> list = new ArrayList<>();
        List<Map<String, Object>> resList = (List<Map<String, Object>>) resultBean.getData();
        for (Map<String, Object> ds : resList) {
            IntegratedBulkCloseQuoteBean integratedBulkCloseQuoteBean = new IntegratedBulkCloseQuoteBean();
            integratedBulkCloseQuoteBean.setExchangeType((String) ds.get("exchange_type"));
            String stockCode = (String) ds.get("stock_code");
            if (Strings.isNullOrEmpty(market)) {
                integratedBulkCloseQuoteBean.setStockCode(stockCode);
            } else {
                integratedBulkCloseQuoteBean.setStockCode(stockCode + "." + market);
            }
            integratedBulkCloseQuoteBean.setStockName((String) ds.get("stock_name"));
            integratedBulkCloseQuoteBean.setClosingPrice(Double.parseDouble((String) ds.get("closing_price")));
            integratedBulkCloseQuoteBean.setBusinessBalance(Double.parseDouble((String) ds.get("business_balance")));
            integratedBulkCloseQuoteBean.setBusinessAmount(Integer.parseInt((String) ds.get("business_amount")));
            integratedBulkCloseQuoteBean.setBuyAmount1(Double.parseDouble((String) ds.get("buy_amount1")));
            integratedBulkCloseQuoteBean.setSaleAmount1(Double.parseDouble((String) ds.get("sale_amount1")));
            integratedBulkCloseQuoteBean.setWeightavgPrice(Double.parseDouble((String) ds.get("weightavg_price")));
            list.add(integratedBulkCloseQuoteBean);
        }
        return list;
    }

    public static List<IntegratedRepurchaseQuoteBean> parseIntegratedRepurchaseQuote(ResultBean resultBean) {
        List<IntegratedRepurchaseQuoteBean> list = new ArrayList<>();
        List<Map<String, Object>> resList = (List<Map<String, Object>>) resultBean.getData();
        for (Map<String, Object> ds : resList) {
            IntegratedRepurchaseQuoteBean integratedRepurchaseQuoteBean = new IntegratedRepurchaseQuoteBean();
            integratedRepurchaseQuoteBean.setCbpconferId((String) ds.get("cbpconfer_id"));
            integratedRepurchaseQuoteBean.setEntrustNo(Integer.parseInt((String) ds.get("entrust_no")));
            integratedRepurchaseQuoteBean.setEntrustProp((String) ds.get("entrust_prop"));
            integratedRepurchaseQuoteBean.setEntrustBs((String) ds.get("entrust_bs"));
            integratedRepurchaseQuoteBean.setExchangeType((String) ds.get("exchange_type"));
            integratedRepurchaseQuoteBean.setStockType((String) ds.get("stock_type"));
            integratedRepurchaseQuoteBean.setStockCode((String) ds.get("stock_code") + "." + getMarket((String) ds.get("exchange_type")));
            integratedRepurchaseQuoteBean.setStockName((String) ds.get("stock_name"));
            integratedRepurchaseQuoteBean.setExpireYearRate(Double.parseDouble((String) ds.get("expire_year_rate")) * 100);
            integratedRepurchaseQuoteBean.setRepoTerm(Integer.parseInt((String) ds.get("repo_term")));
            integratedRepurchaseQuoteBean.setFundUsedDays(Integer.parseInt((String) ds.get("fund_used_days")));
            integratedRepurchaseQuoteBean.setFirstExchdate(Integer.parseInt((String) ds.get("first_exchdate")));
            integratedRepurchaseQuoteBean.setRepurchaseDate(Integer.parseInt((String) ds.get("repurchase_date")));
            integratedRepurchaseQuoteBean.setSettleEndDate(Integer.parseInt((String) ds.get("settle_end_date")));
            integratedRepurchaseQuoteBean.setComponentNum(Integer.parseInt((String) ds.get("component_num")));
            integratedRepurchaseQuoteBean.setOldImpawnCode((String) ds.get("old_impawn_code"));
            integratedRepurchaseQuoteBean.setImpawnAmount(Integer.parseInt((String) ds.get("impawn_amount")));
            integratedRepurchaseQuoteBean.setFunderRatio(Double.parseDouble((String) ds.get("funder_ratio")));
            integratedRepurchaseQuoteBean.setImpawnBalance(Double.parseDouble((String) ds.get("impawn_balance")));
            integratedRepurchaseQuoteBean.setPrevImpawnBalance(Double.parseDouble((String) ds.get("prev_impawn_balance")));
            integratedRepurchaseQuoteBean.setProfit(Double.parseDouble((String) ds.get("profit")));
            integratedRepurchaseQuoteBean.setSettleBalance(Double.parseDouble((String) ds.get("settle_balance")));
            integratedRepurchaseQuoteBean.setAssetNetValue(Double.parseDouble((String) ds.get("asset_net_value")));
            integratedRepurchaseQuoteBean.setBprsrcStatus((String) ds.get("bprsrc_status"));
            integratedRepurchaseQuoteBean.setOrigEntrustDate(Integer.parseInt((String) ds.get("orig_entrust_date")));
            integratedRepurchaseQuoteBean.setOrigCbpconferId((String) ds.get("orig_cbpconfer_id"));
            integratedRepurchaseQuoteBean.setAgencyNo((String) ds.get("agency_no"));
            integratedRepurchaseQuoteBean.setTraderId((String) ds.get("trader_id"));
            integratedRepurchaseQuoteBean.setFundAccount((String) ds.get("fund_account"));
            integratedRepurchaseQuoteBean.setStockAccount((String) ds.get("stock_account"));
            integratedRepurchaseQuoteBean.setSeatNo((String) ds.get("seat_no"));
            integratedRepurchaseQuoteBean.setOppoAgency((String) ds.get("oppo_agency"));
            integratedRepurchaseQuoteBean.setOppoTraderId((String) ds.get("oppo_trader_id"));
            integratedRepurchaseQuoteBean.setAgencyName((String) ds.get("agency_name"));
            integratedRepurchaseQuoteBean.setPositionStr((String) ds.get("position_str"));
            integratedRepurchaseQuoteBean.setOppoAgencyName((String) ds.get("oppo_agency_name"));
            integratedRepurchaseQuoteBean.setPropStockAccount((String) ds.get("prop_stock_account"));
            integratedRepurchaseQuoteBean.setPropSeatNo((String) ds.get("prop_seat_no"));
            integratedRepurchaseQuoteBean.setRemark((String) ds.get("remark"));
            integratedRepurchaseQuoteBean.setPropAccountName((String) ds.get("prop_account_name"));
            integratedRepurchaseQuoteBean.setRealDateBack(Integer.parseInt((String) ds.get("real_date_back")));
            integratedRepurchaseQuoteBean.setPledgeeName((String) ds.get("pledgee_name"));
            list.add(integratedRepurchaseQuoteBean);
        }
        return list;
    }

    public static List<IntegratedFixedIncomeQuoteBean> parseIntegratedFixedIncomeQuote(ResultBean resultBean, String market) {
        List<IntegratedFixedIncomeQuoteBean> list = new ArrayList<IntegratedFixedIncomeQuoteBean>();
        List<Map<String, Object>> resList = (List<Map<String, Object>>) resultBean.getData();
        for (Map<String, Object> ds : resList) {
            IntegratedFixedIncomeQuoteBean integratedFixedIncomeQuoteBean = new IntegratedFixedIncomeQuoteBean();
            integratedFixedIncomeQuoteBean.setStockCode(ds.get("stock_code") + "." + market);
            integratedFixedIncomeQuoteBean.setStockName((String) ds.get("stock_name"));
            integratedFixedIncomeQuoteBean.setOrderNo((String) ds.get("buy_confirm_id"));
            integratedFixedIncomeQuoteBean.setReportTime(Integer.parseInt((String) ds.get("report_time")));
            integratedFixedIncomeQuoteBean.setAgencyName((String) ds.get("agency_name"));
            integratedFixedIncomeQuoteBean.setEntrustPrice(Double.parseDouble((String) ds.get("buy_price")));
            integratedFixedIncomeQuoteBean.setEntrustVolume(Integer.parseInt((String) ds.get("buy_quote_amount")));
            integratedFixedIncomeQuoteBean.setFullPrice(Double.parseDouble((String) ds.get("buy_quote_price")));
            integratedFixedIncomeQuoteBean.setExpireIncomeRate(Double.parseDouble((String) ds.get("buy_exchange_rate")));
            integratedFixedIncomeQuoteBean.setInterest(Double.parseDouble((String) ds.get("stock_interest")));
            integratedFixedIncomeQuoteBean.setEntrustBs(EntrustBs.BUY.value);
            list.add(integratedFixedIncomeQuoteBean);
            if (!Strings.isNullOrEmpty((String) ds.get("sell_price"))) {
                IntegratedFixedIncomeQuoteBean integratedFixedIncomeQuoteBean2 = new IntegratedFixedIncomeQuoteBean();
                integratedFixedIncomeQuoteBean2.setStockCode(ds.get("stock_code") + "." + market);
                integratedFixedIncomeQuoteBean2.setStockName((String) ds.get("stock_name"));
                integratedFixedIncomeQuoteBean2.setOrderNo((String) ds.get("sell_confirm_id"));
                integratedFixedIncomeQuoteBean2.setReportTime(Integer.parseInt((String) ds.get("report_time_x")));
                integratedFixedIncomeQuoteBean2.setAgencyName((String) ds.get("other_agency"));
                integratedFixedIncomeQuoteBean2.setEntrustPrice(Double.parseDouble((String) ds.get("sell_price")));
                integratedFixedIncomeQuoteBean2.setEntrustVolume(Integer.parseInt((String) ds.get("sell_quote_amount")));
                integratedFixedIncomeQuoteBean2.setFullPrice(Double.parseDouble((String) ds.get("sell_quote_price")));
                integratedFixedIncomeQuoteBean2.setExpireIncomeRate(Double.parseDouble((String) ds.get("sell_exchange_rate")));
                integratedFixedIncomeQuoteBean2.setInterest(Double.parseDouble((String) ds.get("stock_interest")));
                integratedFixedIncomeQuoteBean.setEntrustBs(EntrustBs.SELL.value);
                list.add(integratedFixedIncomeQuoteBean2);
            }
        }
        return list;

    }

    private static void getEntrustStatusByUfx(String entrustStatus, EntrustInterface entrust) {
        switch (entrustStatus) {
            case "5":
                entrust.setEntrustStatus(EntrustStatus.PART_TRADED_CANCELED.value);
                break;
            case "6":
                entrust.setEntrustStatus(EntrustStatus.CANCELED.value);
                break;
            case "7":
                entrust.setEntrustStatus(EntrustStatus.PART_SUCCESS.value);
                break;
            case "C":
                entrust.setEntrustStatus(EntrustStatus.TOBE_REPORTED.value);
                break;
            case "D":
                entrust.setEntrustStatus(EntrustStatus.INVALID.value);
                break;
            case "S":
                entrust.setEntrustStatus(EntrustStatus.REPORTED.value);
                break;
            case "T":
                entrust.setEntrustStatus(EntrustStatus.PART_SUCCESS.value);
                break;
            case "U":
                entrust.setEntrustStatus(EntrustStatus.REPORTED_TOBE_CANCEL.value);
                break;
            case "V":
                entrust.setEntrustStatus(EntrustStatus.REPORTED.value);
                break;
            default:
                entrust.setEntrustStatus(Integer.parseInt(entrustStatus));
        }
    }

    public static BigDecimal getPriceStep(String priceUnit) {
        if ("1".equals(priceUnit)) {
            return new BigDecimal("0.001");
        } else if ("10".equals(priceUnit)) {
            return new BigDecimal("0.01");
        } else {
            return BigDecimal.valueOf(Double.parseDouble(priceUnit) / 1000);
        }
    }

    public static String getMarket(String exchangeType) {
        if (StringUtils.equals(exchangeType, "1")) {
            return "SH";
        } else if (StringUtils.equals(exchangeType, "2")) {
            return "SZ";
        }
        return "";
    }


}
