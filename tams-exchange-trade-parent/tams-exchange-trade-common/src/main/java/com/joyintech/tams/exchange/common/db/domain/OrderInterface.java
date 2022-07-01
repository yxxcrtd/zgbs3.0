package com.joyintech.tams.exchange.common.db.domain;

import java.math.BigDecimal;
import java.util.Date;

public interface OrderInterface {
    String getId();

    void setId(String id);

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

    int getErrorCode();

    void setErrorCode(int errorCode);

    String getErrorMsg();

    void setErrorMsg(String errorMsg);

    String getTradingDay();

    void setTradingDay(String tradingDay);

    String getIpMac();

    void setIpMac(String ipMac);

    String getRemark();

    void setRemark(String remark);

    Date getCreateTime();

    void setCreateTime(Date createTime);

    Date getUpdateTime();

    void setUpdateTime(Date updateTime);

    String getIsFullPrice();

    void setIsFullPrice(String isFullPrice);

    BigDecimal getFullPrice();

    String getOffsetFlag();

    void setOffsetFlag(String hedgeFlag);

    String getHedgeFlag();

    void setHedgeFlag(String hedgeFlag);

    String getBusiTypeTg();
}
