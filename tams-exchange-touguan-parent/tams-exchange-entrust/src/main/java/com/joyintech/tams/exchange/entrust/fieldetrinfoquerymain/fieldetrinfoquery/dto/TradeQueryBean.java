package com.joyintech.tams.exchange.entrust.fieldetrinfoquerymain.fieldetrinfoquery.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt2;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.thymeleaf.util.StringUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@ApiModel(value = "成交信息", description = "成交信息")
public class TradeQueryBean implements Serializable {
    private static final long serialVersionUID = 6002608629073057691L;

    @ApiModelProperty(value = "指令编号")
    private String instructionId;

    @ApiModelProperty(value = "委托编号")
    private String brokerEntrustId;

    @ApiModelProperty(value = "成交编号")
    private String brokerTradeId;

    @ApiModelProperty(name = "portfolioId", value = "组合代码")
    private String portfolioId;

    @ApiModelProperty(name = "portfolioName", value = "组合名称")
    private String portfolioName;

    @ApiModelProperty(value = "资金账户")
    private String financeAccount;

    @ApiModelProperty(name = "secAcctId", value = "证券账户")
    private String secAcctId;

    @ApiModelProperty(value = "交易市场")
    private String market;

    @ApiModelProperty(value = "交易市场")
    private String marketStr;

    @ApiModelProperty(name = "tradePlat", value = "交易平台")
    private String tradePlat;

    @ApiModelProperty(name = "tradePlatStr", value = "交易平台")
    private String tradePlatStr;

    @ApiModelProperty(value = "资产代码")
    private String stockCode;

    @ApiModelProperty(value = "资产名称")
    private String stockName;

    @ApiModelProperty(name = "finprodType2", value = "资产类型")
    private String finprodType2;

    @ApiModelProperty(name = "finprodType2Str", value = "资产类型")
    private String finprodType2Str;

    @ApiModelProperty(value = "委托方向")
    private String entrustBs;

    @ApiModelProperty(value = "委托方向")
    private String entrustBsStr;

    @ApiModelProperty(value = "成交数量")
    private long tradedVolume;

    @JsonSerialize(using = DoubleSerializerUtils.class)
    @ApiModelProperty(value = "成交价格")
    private BigDecimal tradedPrice;

    @JsonSerialize(using = DoubleSerializerUtils.class)
    @ApiModelProperty(value = "成交金额")
    private BigDecimal tradedBalance;

    @ApiModelProperty(value = "成交时间")
    private Date createTime;

    @ApiModelProperty(name = "deliveryResult", value = "交收状态")
    private String deliveryResult;

    @ApiModelProperty(name = "deliverySchedule", value = "交收进度")
    private String deliverySchedule;

    /**
     * ///////////////////////////////////查询无用字段
     */
    @ApiModelProperty(value = "交易日期",hidden = true)
    private String tradingDay;

    @ApiModelProperty(value = "委托属性", hidden = true)
    private String entrustProp;

    @ApiModelProperty(value = "委托方向标识", hidden = true)
    private String bsFlag;

    @ApiModelProperty(value = "主键ID", hidden = true)
    private String id;

    @ApiModelProperty(value = "定位串", hidden = true)
    private String positionStr;

    @ApiModelProperty(value = "委托编号", hidden = true)
    private String entrustId;

    @ApiModelProperty(value = "订单编号", hidden = true)
    private String parentOrderId;

    @ApiModelProperty(value = "网点号:资金账户编号", hidden = true)
    private String accountId;

    @ApiModelProperty(value = "资金账户名称", hidden = true)
    private String accountName;

    @ApiModelProperty(value = "资产类型", hidden = true)
    private String assetType;

    @ApiModelProperty(value = "业务标识：5 公平；6 批量", hidden = true)
    private String businessFlag;

    @ApiModelProperty(value = "委托类别", hidden = true)
    private String entrustType;

    @ApiModelProperty(value = "成交类别", hidden = true)
    private int tradeType;

    @JsonSerialize(using = DoubleSerializerUtils.class)
    @ApiModelProperty(value = "全价价格", hidden = true)
    private BigDecimal fullPrice;

    @JsonSerialize(using = DoubleSerializerUtils.class)
    @ApiModelProperty(value = "清算金额", hidden = true)
    private BigDecimal clearBalance;

    @ApiModelProperty(value = "成交时间", hidden = true)
    private String tradeTime;

    @JsonSerialize(using = DoubleSerializerUtils.class)
    @ApiModelProperty(value = "质押券折算比例", hidden = true)
    private BigDecimal discount;

    @ApiModelProperty(value = "到期续作类型：0 非第三方；1 第三方", hidden = true)
    private String transCode;

    @ApiModelProperty(value = "对手方交易商编号", hidden = true)
    private String propSeatNo;

    @ApiModelProperty(value = "本地委托编号", hidden = true)
    private String localEntrustId;

    @ApiModelProperty(value = "更新时间", hidden = true)
    private Date updateTime;

    @ApiModelProperty(value = "记录数", hidden = true)
    public int count;

    @ApiModelProperty(value = "报文编号", hidden = true)
    private String reportNo;

    @ApiModelProperty(value = "无撤销委托标识", hidden = true)
    private boolean noCancelEntrust;

    @ApiModelProperty(value = "冲正标识", hidden = true)
    private String offsetFlag;

    @ApiModelProperty(value = "套期保值标识", hidden = true)
    private String hedgeFlag;

    @ApiModelProperty(value = "错误信息", hidden = true)
    private String errorMsg;

    @ApiModelProperty(value = "成交ID", hidden = true)
    private String tradeId;

    public TradeQueryBean() {
        this.tradedBalance = new BigDecimal("0.00");
        this.tradedPrice = new BigDecimal("0.00");
        this.clearBalance = new BigDecimal("0.00");
        this.discount = new BigDecimal("0.00");
        this.fullPrice = new BigDecimal("0.00");
    }

    public boolean isNoCancelEntrust() {
        return noCancelEntrust;
    }

    public void setNoCancelEntrust(boolean noCancelEntrust) {
        this.noCancelEntrust = noCancelEntrust;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getInstructionId() {
        return instructionId;
    }

    public void setInstructionId(String instructionId) {
        this.instructionId = instructionId == null ? null : instructionId.trim();
    }

    public String getParentOrderId() {
        return parentOrderId;
    }

    public void setParentOrderId(String parentOrderId) {
        this.parentOrderId = parentOrderId == null ? null : parentOrderId.trim();
    }

    public String getEntrustId() {
        return entrustId;
    }

    public void setEntrustId(String entrustId) {
        this.entrustId = entrustId == null ? null : entrustId.trim();
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId == null ? null : accountId.trim();
    }

    public String getFinanceAccount() {
        return financeAccount;
    }

    public void setFinanceAccount(String financeAccount) {
        this.financeAccount = financeAccount == null ? null : financeAccount.trim();
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName == null ? null : accountName.trim();
    }

    public String getMarket() {
        return market;
    }

    public void setMarket(String market) {
        this.market = market == null ? null : market.trim();
    }

    public String getAssetType() {
        return assetType;
    }

    public void setAssetType(String assetType) {
        this.assetType = assetType == null ? null : assetType.trim();
    }

    public String getStockCode() {
        return stockCode;
    }

    public void setStockCode(String stockCode) {
        this.stockCode = stockCode == null ? null : stockCode.trim();
    }

    public String getStockName() {
        return stockName;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName == null ? null : stockName.trim();
    }

    public String getBusinessFlag() {
        return businessFlag;
    }

    public void setBusinessFlag(String businessFlag) {
        this.businessFlag = businessFlag == null ? null : businessFlag.trim();
    }

    public String getEntrustType() {
        return entrustType;
    }

    public void setEntrustType(String entrustType) {
        this.entrustType = entrustType == null ? null : entrustType.trim();
    }

    public String getEntrustProp() {
        return entrustProp;
    }

    public void setEntrustProp(String entrustProp) {
        this.entrustProp = entrustProp == null ? null : entrustProp.trim();
    }

    public String getEntrustBs() {
        return entrustBs;
    }

    public void setEntrustBs(String entrustBs) {
        this.entrustBs = entrustBs == null ? null : entrustBs.trim();
    }

    public String getBsFlag() {
        return bsFlag;
    }

    public void setBsFlag(String bsFlag) {
        this.bsFlag = bsFlag == null ? null : bsFlag.trim();
    }

    public int getTradeType() {
        return tradeType;
    }

    public void setTradeType(int tradeType) {
        this.tradeType = tradeType;
    }

    public BigDecimal getTradedBalance() {
        // 质押券出入库
        if(StringUtils.equals(getEntrustBs(), "T68") || StringUtils.equals(getEntrustBs(), "T69")){
            return null;
        }
        return tradedBalance;
    }

    public void setTradedBalance(BigDecimal tradedBalance) {
        this.tradedBalance = tradedBalance;
    }

    public long getTradedVolume() {
        // 质押券出入库 ZGCPCN-404
        if(StringUtils.equals(getEntrustBs(), "T68") || StringUtils.equals(getEntrustBs(), "T69")){
            return ArithmeticUtil.divide(new BigDecimal(String.valueOf(tradedVolume)),new BigDecimal("100"),2).longValue();
        }
        return tradedVolume;
    }
    public void setTradedVolume(long tradedVolume) {
        this.tradedVolume = tradedVolume;
    }

    public BigDecimal getTradedPrice() {
        // 质押券出入库
        if(StringUtils.equals(getEntrustBs(), "T68") || StringUtils.equals(getEntrustBs(), "T69")){
            return null;
        }
        return tradedPrice;
    }

    public void setTradedPrice(BigDecimal tradedPrice) {
        this.tradedPrice = tradedPrice;
    }

    public BigDecimal getClearBalance() {
        return clearBalance;
    }

    public void setClearBalance(BigDecimal clearBalance) {
        this.clearBalance = clearBalance;
    }

    public BigDecimal getFullPrice() {
        return fullPrice;
    }

    public void setFullPrice(BigDecimal fullPrice) {
        this.fullPrice = fullPrice;
    }

    public String getTradeTime() {
        return tradeTime;
    }

    public void setTradeTime(String tradeTime) {
        this.tradeTime = tradeTime == null ? null : tradeTime.trim();
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public String getTransCode() {
        return transCode;
    }

    public void setTransCode(String transCode) {
        this.transCode = transCode == null ? null : transCode.trim();
    }

    public String getPropSeatNo() {
        return propSeatNo;
    }

    public void setPropSeatNo(String propSeatNo) {
        this.propSeatNo = propSeatNo == null ? null : propSeatNo.trim();
    }

    public String getTradingDay() {
        return tradingDay;
    }

    public void setTradingDay(String tradingDay) {
        this.tradingDay = tradingDay == null ? null : tradingDay.trim();
    }

    public String getLocalEntrustId() {
        return localEntrustId;
    }

    public void setLocalEntrustId(String localEntrustId) {
        this.localEntrustId = localEntrustId == null ? null : localEntrustId.trim();
    }

    public String getBrokerEntrustId() {
        return brokerEntrustId;
    }

    public void setBrokerEntrustId(String brokerEntrustId) {
        this.brokerEntrustId = brokerEntrustId == null ? null : brokerEntrustId.trim();
    }

    public String getBrokerTradeId() {
        return brokerTradeId;
    }

    public void setBrokerTradeId(String brokerTradeId) {
        this.brokerTradeId = brokerTradeId == null ? null : brokerTradeId.trim();
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

    public String getPositionStr() {
        return positionStr;
    }

    public void setPositionStr(String positionStr) {
        this.positionStr = positionStr;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getReportNo() {
        return reportNo;
    }

    public void setReportNo(String reportNo) {
        this.reportNo = reportNo;
    }

    public String getOffsetFlag() {
        return offsetFlag;
    }

    public void setOffsetFlag(String offsetFlag) {
        this.offsetFlag = offsetFlag;
    }

    public String getHedgeFlag() {
        return hedgeFlag;
    }

    public void setHedgeFlag(String hedgeFlag) {
        this.hedgeFlag = hedgeFlag;
    }

    public String getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }

    public String getPortfolioName() {
        return portfolioName;
    }

    public void setPortfolioName(String portfolioName) {
        this.portfolioName = portfolioName;
    }

    public String getSecAcctId() {
        return secAcctId;
    }

    public void setSecAcctId(String secAcctId) {
        this.secAcctId = secAcctId;
    }

    public String getTradePlat() {
        return tradePlat;
    }

    public void setTradePlat(String tradePlat) {
        this.tradePlat = tradePlat;
    }

    public String getFinprodType2() {
        return finprodType2;
    }

    public void setFinprodType2(String finprodType2) {
        this.finprodType2 = finprodType2;
    }

    public String getMarketStr() {
        if(StringUtil.isEmpty(market)){
            return null;
        }
        return Market.getMarketName(market);
    }

    public void setMarketStr(String marketStr) {
        this.marketStr = marketStr;
    }

    public String getTradePlatStr() {
        if(StringUtil.isEmpty(tradePlat)){
            return null;
        }
//        return DictCodeList.from(DictCode.交易平台, tradePlat).getCodeName();
        return SysDataUtil.getNameFromDataDict(DictCode.交易平台, tradePlat);
    }

    public void setTradePlatStr(String tradePlatStr) {
        this.tradePlatStr = tradePlatStr;
    }

    public String getFinprodType2Str() {
        if(StringUtil.isEmpty(finprodType2)){
            return null;
        }
        return SysDataUtil.getNameFromDataDict(DictCode.平安资产类型, finprodType2);
    }

    public void setFinprodType2Str(String finprodType2Str) {
        this.finprodType2Str = finprodType2Str;
    }

    public String getEntrustBsStr() {
        if(StringUtil.isEmpty(entrustBs)){
            return null;
        }
//        return  DictCodeListExt2.from(DictCode.交易方向, entrustBs).getCodeName();
        return SysDataUtil.getNameFromDataDict(DictCode.交易方向, entrustBs);
    }

    public void setEntrustBsStr(String entrustBsStr) {
        this.entrustBsStr = entrustBsStr;
    }

    public String getDeliveryResult() {
        return deliveryResult;
    }

    public void setDeliveryResult(String deliveryResult) {
        this.deliveryResult = deliveryResult;
    }

    public String getDeliverySchedule() {
        return deliverySchedule;
    }

    public void setDeliverySchedule(String deliverySchedule) {
        this.deliverySchedule = deliverySchedule;
    }
}
