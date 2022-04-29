package com.joyintech.tams.exchange.common.db.domain;

import com.joyintech.tams.exchange.common.constant.IDPrefix;

import java.math.BigDecimal;
import java.util.Date;

public interface EntrustInterface extends OrderInterface {
    String getId();

    void setId(String id);

    String getParentOrderId();

    void setParentOrderId(String parentOrderId);

    String getInstructionId();

    void setInstructionId(String instructionId);

    String getPortfolioId();

    void setPortfolioId(String portfolioId);

    String getUserId();

    void setUserId(String userId);

    String getUsername();

    void setUsername(String username);

    String getAccountId();

    void setAccountId(String accountId);

    String getFinanceAccount();

    void setFinanceAccount(String financeAccount);

    String getAccountName();

    void setAccountName(String accountName);

    String getAssetType();

    void setAssetType(String assetType);

    String getMarket();

    void setMarket(String market);

    String getStockCode();

    void setStockCode(String stockCode);

    String getStockName();

    void setStockName(String stockName);

    String getEntrustType();

    void setEntrustType(String entrustType);

    String getEntrustProp();

    void setEntrustProp(String entrustProp);

    String getEntrustBs();

    void setEntrustBs(String entrustBs);

    String getBsFlag();

    void setBsFlag(String bsFlag);

    long getEntrustVolume();

    void setEntrustVolume(long entrustVolume);

    BigDecimal getEntrustPrice();

    BigDecimal getFullPrice();

    void setEntrustPrice(BigDecimal entrustPrice);

    BigDecimal getFrozenBalance();

    void setFrozenBalance(BigDecimal frozenBalance);

    long getFrozenVolume();

    void setFrozenVolume(long frozenVolume);

    BigDecimal getTradedBalance();

    void setTradedBalance(BigDecimal tradedBalance);

    long getTradedVolume();

    void setTradedVolume(long tradedVolume);

    BigDecimal getTradedPrice();

    void setTradedPrice(BigDecimal tradedPrice);

    long getCancelVolume();

    void setCancelVolume(long cancelVolume);

    String getReportTime();

    void setReportTime(String reportTime);

    String getTradeTime();

    void setTradeTime(String tradeTime);

    String getCancelTime();

    void setCancelTime(String cancelTime);

    int getErrorCode();

    void setErrorCode(int errorCode);

    String getErrorMsg();

    void setErrorMsg(String errorMsg);

    String getTradingDay();

    void setTradingDay(String tradingDay);

    String getLocalEntrustId();

    void setLocalEntrustId(String localEntrustId);

    String getBrokerEntrustId();

    void setBrokerEntrustId(String brokerEntrustId);

    String getIpMac();

    void setIpMac(String ipMac);

    String getRemark();

    void setRemark(String remark);

    Date getCreateTime();

    void setCreateTime(Date createTime);

    Date getUpdateTime();

    void setUpdateTime(Date updateTime);

    int getEntrustStatus();

    void setEntrustStatus(int entrustStatus);

    String getOrigEntrustId();

    void setOrigEntrustId(String origEntrustId);

    String getReportNo();

    void setReportNo(String reportNo);

    static boolean isIntegratedEntrust(String entrustId) {
        return !entrustId.startsWith(IDPrefix.NORMAL.value);
    }

    boolean isNoCancelEntrust();

    void setNoCancelEntrust(boolean noCancelEntrust);

    String getOperateUser();

    void setOperateUser(String operateUser);

    String getOffsetFlag();

    void setOffsetFlag(String hedgeFlag);

    String getHedgeFlag();

    void setHedgeFlag(String hedgeFlag);
    
    BigDecimal getEntrustBalance();

    default BigDecimal getBackBalance(){
        return null;
    }

}
