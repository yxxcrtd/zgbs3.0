package com.joyintech.tams.exchange.common.db.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.data.annotation.Id;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@ApiModel(value = "算法单返回报文", description = "算法单返回报文")
public class Algorithm implements Serializable, KeyMethodInterface<String>, OrderInterface {
    private static final long serialVersionUID = -5236098880155593231L;

    @ApiModelProperty(required = true, name = "id", value = "算法单ID")
    @Id
    @NotNull
    @Size(max = 32)
    private String id;

    @ApiModelProperty(required = true, name = "algorithmName", value = "算法单名称")
    @NotNull
    @Size(max = 32)
    private String algorithmName;

    @ApiModelProperty(required = true, name = "beginTime", value = "开始时间")
    @NotNull
    private Date beginTime;

    @ApiModelProperty(required = true, name = "endTime", value = "结束时间")
    @NotNull
    private Date endTime;

    @ApiModelProperty(required = true, name = "algorithmStatus", value = "算法单状态")
    @NotNull
    @Size(max = 1)
    private String algorithmStatus;

    @ApiModelProperty(required = true, name = "parameter", value = "参数")
    @Size(max = 1024)
    private String parameter;

    @ApiModelProperty(required = true, name = "stockCode", value = "证券代码")
    @NotNull
    @Size(max = 32)
    private String stockCode;

    @ApiModelProperty(required = true, name = "volume", value = "数量")
    @NotNull
    @Digits(integer = 10, fraction = 0)
    private Long volume;

    @ApiModelProperty(required = true, name = "entrustBs", value = "交易方向")
    @NotNull
    @Size(max = 1)
    private String entrustBs;

    @ApiModelProperty(required = true, name = "offsetFlag", value = "开平仓标志")
    @NotNull
    @Size(max = 1)
    private String offsetFlag;

    @ApiModelProperty(required = true, name = "limitAction", value = "买入若出现跌停时的操作，或者卖出出现涨停时的操作")
    @NotNull
    @Digits(integer = 8, fraction = 0)
    private Integer limitAction;

    @ApiModelProperty(required = true, name = "afterAction", value = "超过时间后是撤单还是继续交易")
    @NotNull
    @Digits(integer = 8, fraction = 0)
    private Integer afterAction;

    @ApiModelProperty(required = true, name = "tradedVolume", value = "成交数量")
    @NotNull
    @Digits(integer = 10, fraction = 0)
    private Long tradedVolume = 0L;

    @ApiModelProperty(required = true, name = "externalId", value = "外部ID，外接算法中心时用")
    @Size(max = 32)
    private String externalId;

    @ApiModelProperty(required = true, name = "instructionId", value = "指令编号")
    @NotNull
    @Size(max = 32)
    private String instructionId;

    @ApiModelProperty(required = true, name = "errorCode", value = "错误码")
    @NotNull
    @Digits(integer = 8, fraction = 0)
    private int errorCode;

    @ApiModelProperty(required = true, name = "errorMsg", value = "错误描述")
    @Size(max = 64)
    private String errorMsg;

    @ApiModelProperty(required = true, name = "tradingDay", value = "交易日期")
    @NotNull
    @Size(max = 10)
    private String tradingDay;

    @ApiModelProperty(required = true, name = "createTime", value = "创建时间")
    @NotNull
    private Date createTime;

    @ApiModelProperty(required = true, name = "updateTime", value = "更新时间")
    @NotNull
    private Date updateTime;

    @ApiModelProperty(required = true, name = "count", value = "记录数")
    private int count;

    @Id
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getAlgorithmName() {
        return algorithmName;
    }

    public void setAlgorithmName(String algorithmName) {
        this.algorithmName = algorithmName == null ? null : algorithmName.trim();
    }

    public Date getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getAlgorithmStatus() {
        return algorithmStatus;
    }

    public void setAlgorithmStatus(String algorithmStatus) {
        this.algorithmStatus = algorithmStatus == null ? null : algorithmStatus.trim();
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter == null ? null : parameter.trim();
    }

    public String getStockCode() {
        return stockCode;
    }

    public void setStockCode(String stockCode) {
        this.stockCode = stockCode == null ? null : stockCode.trim();
    }

    @Override
    public String getStockName() {
        return null;
    }

    @Override
    public void setStockName(String stockName) {

    }

    @Override
    public String getEntrustType() {
        return null;
    }

    @Override
    public void setEntrustType(String entrustType) {

    }

    @Override
    public String getEntrustProp() {
        return null;
    }

    @Override
    public void setEntrustProp(String entrustProp) {

    }

    public Long getVolume() {
        return volume;
    }

    public void setVolume(Long volume) {
        this.volume = volume;
    }

    public String getEntrustBs() {
        return entrustBs;
    }

    public void setEntrustBs(String entrustBs) {
        this.entrustBs = entrustBs == null ? null : entrustBs.trim();
    }

    @Override
    public String getBsFlag() {
        return null;
    }

    @Override
    public void setBsFlag(String bsFlag) {

    }

    @Override
    public long getEntrustVolume() {
        return 0;
    }

    @Override
    public void setEntrustVolume(long entrustVolume) {

    }

    @Override
    public BigDecimal getEntrustPrice() {
        return null;
    }

    @Override
    public void setEntrustPrice(BigDecimal entrustPrice) {

    }

    @Override
    public BigDecimal getFrozenBalance() {
        return null;
    }

    @Override
    public void setFrozenBalance(BigDecimal frozenBalance) {

    }

    @Override
    public long getFrozenVolume() {
        return 0;
    }

    @Override
    public void setFrozenVolume(long frozenVolume) {

    }

    @Override
    public BigDecimal getTradedBalance() {
        return null;
    }

    @Override
    public void setTradedBalance(BigDecimal tradedBalance) {

    }

    public String getOffsetFlag() {
        return offsetFlag;
    }

    public void setOffsetFlag(String offsetFlag) {
        this.offsetFlag = offsetFlag == null ? null : offsetFlag.trim();
    }

    @Override
    public String getHedgeFlag() {
        return null;
    }

    @Override
    public void setHedgeFlag(String hedgeFlag) {

    }

    @Override
    public String getBusiTypeTg() {
        return null;
    }

    public Integer getLimitAction() {
        return limitAction;
    }

    public void setLimitAction(Integer limitAction) {
        this.limitAction = limitAction;
    }

    public Integer getAfterAction() {
        return afterAction;
    }

    public void setAfterAction(Integer afterAction) {
        this.afterAction = afterAction;
    }

    public long getTradedVolume() {
        return tradedVolume;
    }

    @Override
    public BigDecimal getTradedPrice() {
        return null;
    }

    @Override
    public void setTradedPrice(BigDecimal tradedPrice) {

    }

    @Override
    public long getCancelVolume() {
        return 0;
    }

    @Override
    public void setCancelVolume(long cancelVolume) {

    }

    public void setTradedVolume(long tradedVolume) {
        this.tradedVolume = tradedVolume;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId == null ? null : externalId.trim();
    }

    public String getInstructionId() {
        return instructionId;
    }

    public void setInstructionId(String instructionId) {
        this.instructionId = instructionId;
    }

    @Override
    public String getPortfolioId() {
        return null;
    }

    @Override
    public void setPortfolioId(String portfolioId) {

    }

    @Override
    public String getUserId() {
        return null;
    }

    @Override
    public void setUserId(String userId) {

    }

    @Override
    public String getUsername() {
        return null;
    }

    @Override
    public void setUsername(String username) {

    }

    @Override
    public String getAccountId() {
        return null;
    }

    @Override
    public void setAccountId(String accountId) {

    }

    @Override
    public String getFinanceAccount() {
        return null;
    }

    @Override
    public void setFinanceAccount(String financeAccount) {

    }

    @Override
    public String getAccountName() {
        return null;
    }

    @Override
    public void setAccountName(String accountName) {

    }

    @Override
    public String getAssetType() {
        return null;
    }

    @Override
    public void setAssetType(String assetType) {

    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    @Override
    public String getTradingDay() {
        return tradingDay;
    }

    @Override
    public void setTradingDay(String tradingDay) {
        this.tradingDay = tradingDay;
    }

    @Override
    public String getIpMac() {
        return null;
    }

    @Override
    public void setIpMac(String ipMac) {

    }

    @Override
    public String getRemark() {
        return null;
    }

    @Override
    public void setRemark(String remark) {

    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String getIsFullPrice() {
        return null;
    }

    @Override
    public void setIsFullPrice(String isFullPrice) {

    }

    @Override
    public BigDecimal getFullPrice() {
        return null;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}