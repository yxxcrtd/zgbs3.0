package com.joyintech.tams.exchange.common.db.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.tams.exchange.common.utils.DoubleSerializerUtils;
import com.joyintech.tams.exchange.common.utils.TradeUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@ApiModel(value = "历史成交信息", description = "历史成交信息")
public class TradeHistory implements Serializable {
    private static final long serialVersionUID = 8867771423212338029L;

    @ApiModelProperty(value = "主键ID")
    private String id;

    @ApiModelProperty(value = "指令编号")
    private String instructionId;

    @ApiModelProperty(value = "订单编号")
    private String parentOrderId;

    @ApiModelProperty(value = "委托编号")
    private String entrustId;

    @ApiModelProperty(value = "网点号:资金账户编号")
    private String accountId;

    @ApiModelProperty(value = "成交ID")
    private String tradeId;

    @ApiModelProperty(value = "资金账户编号")
    private String financeAccount;

    @ApiModelProperty(value = "资金账户名称")
    private String accountName;

    @ApiModelProperty(value = "交易场所")
    private String market;

    @ApiModelProperty(value = "资产类型")
    private String assetType;

    @ApiModelProperty(value = "证券代码")
    private String stockCode;

    @ApiModelProperty(value = "证券名称")
    private String stockName;

    @ApiModelProperty(value = "业务标识：5 公平；6 批量")
    private String businessFlag;

    @ApiModelProperty(value = "委托类别")
    private String entrustType;

    @ApiModelProperty(value = "委托属性")
    private String entrustProp;

    @ApiModelProperty(value = "委托方向")
    private String entrustBs;

    @ApiModelProperty(value = "委托方向标识")
    private String bsFlag;

    @ApiModelProperty(value = "成交类别")
    private int tradeType;

    @JsonSerialize(using = DoubleSerializerUtils.class)
    @ApiModelProperty(value = "已成交金额")
    private BigDecimal tradedBalance;

    @ApiModelProperty(value = "已成交数量")
    private long tradedVolume;

    @JsonSerialize(using = DoubleSerializerUtils.class)
    @ApiModelProperty(value = "成交价格")
    private BigDecimal tradedPrice;

    @JsonSerialize(using = DoubleSerializerUtils.class)
    @ApiModelProperty(value = "清算金额")
    private BigDecimal clearBalance;

    @ApiModelProperty(value = "成交时间")
    private String tradeTime;

    @JsonSerialize(using = DoubleSerializerUtils.class)
    @ApiModelProperty(value = "质押券折算比例")
    private BigDecimal discount;

    @JsonSerialize(using = DoubleSerializerUtils.class)
    @ApiModelProperty(value = "全价价格")
    private BigDecimal fullPrice;

    @ApiModelProperty(value = "到期续作类型：0 非第三方；1 第三方")
    private String transCode;

    @ApiModelProperty(value = "对手方交易商编号")
    private String propSeatNo;

    @ApiModelProperty(value = "交易日期")
    private String tradingDay;

    @ApiModelProperty(value = "本地委托编号")
    private String localEntrustId;

    @ApiModelProperty(value = "柜台委托编号")
    private String brokerEntrustId;

    @ApiModelProperty(value = "柜台成交编号")
    private String brokerTradeId;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "报文编号")
    private String reportNo;//柜台报盘编号

    @ApiModelProperty(value = "冲正标识")
    private String offsetFlag;

    @ApiModelProperty(value = "套期保值标识")
    private String hedgeFlag;

    @ApiModelProperty(value = "组合编号")
    private String portfolioId;

    @ApiModelProperty(name = "portfolioName", value = "组合名称", hidden = true)
    private String portfolioName;
    @ApiModelProperty(name = "secAcctId", value = "证券账户", hidden = true)
    private String secAcctId;
    @ApiModelProperty(name = "secAcctName", value = "证券账户", hidden = true)
    private String secAcctName;
    @ApiModelProperty(name = "tradePlat", value = "交易平台", hidden = true)
    private String tradePlat;
    @ApiModelProperty(name = "finprodType2", value = "金融产品类型（投管分类）：债券、理财产品、基金、理财产品模板、理财产品分层", hidden = true)
    private String finprodType2;
    @ApiModelProperty(name = "invManagerId", value = "投资经理", hidden = true)
    private String invManagerId;
    @ApiModelProperty(name = "ps", value = "交易方向", hidden = true)
    private String ps;
    @ApiModelProperty(name = "busiTypeTg", value = "投管业务类型")
    private String busiTypeTg;
    @ApiModelProperty(name = "extend1", value = "扩展信息1")
    private String extend1;
    @ApiModelProperty(name = "extend2", value = "扩展信息2")
    private String extend2;

    private String tradeDate;
    private String dealTime;

    private String entrustClass;

    @JsonSerialize(using = DoubleSerializerUtils.class)
    @ApiModelProperty(value = "净价价格")
    private BigDecimal netPrice;

    @JsonSerialize(using = DoubleSerializerUtils.class)
    @ApiModelProperty(value = "净价金额")
    private BigDecimal netBalance;

    @ApiModelProperty(name = "isFullPrice", value = "是否全价（Y:是 ， N：否）")
    private String isFullPrice;

    @Id
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

    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId == null ? null : tradeId.trim();
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

    public BigDecimal getFullPrice() {
        return fullPrice;
    }

    public void setFullPrice(BigDecimal fullPrice) {
        this.fullPrice = fullPrice;
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
        return tradedBalance;
    }

    public void setTradedBalance(BigDecimal tradedBalance) {
        this.tradedBalance = tradedBalance;
    }

    public long getTradedVolume() {
        return tradedVolume;
    }

    public void setTradedVolume(long tradedVolume) {
        this.tradedVolume = tradedVolume;
    }

    public BigDecimal getTradedPrice() {
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

    public String getSecAcctName() {
        return secAcctName;
    }

    public void setSecAcctName(String secAcctName) {
        this.secAcctName = secAcctName;
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

    public String getInvManagerId() {
        return invManagerId;
    }

    public void setInvManagerId(String invManagerId) {
        this.invManagerId = invManagerId;
    }

    public String getPs() {
        return ps;
    }

    public void setPs(String ps) {
        this.ps = ps;
    }

    public String getBusiTypeTg() {
        return busiTypeTg;
    }

    public void setBusiTypeTg(String busiTypeTg) {
        this.busiTypeTg = busiTypeTg;
    }

    public String getExtend1() {
        return extend1;
    }

    public void setExtend1(String extend1) {
        this.extend1 = extend1;
    }

    public String getExtend2() {
        return extend2;
    }

    public void setExtend2(String extend2) {
        this.extend2 = extend2;
    }

    public String getTradeDate() {
        return TradeUtils.formatDate(tradingDay);
    }

    public void setTradeDate(String tradeDate) {
        this.tradeDate = tradeDate;
    }


    public String getDealTime() {
        return DateUtil.format(createTime,"HH:mm:ss");
    }

    public void setDealTime(String dealTime) {
        this.dealTime = dealTime;
    }

    public String getEntrustClass() {
        return entrustClass;
    }

    public void setEntrustClass(String entrustClass) {
        this.entrustClass = entrustClass;
    }

    public BigDecimal getNetPrice() {
        return netPrice;
    }

    public void setNetPrice(BigDecimal netPrice) {
        this.netPrice = netPrice;
    }

    public BigDecimal getNetBalance() {
        return netBalance;
    }

    public void setNetBalance(BigDecimal netBalance) {
        this.netBalance = netBalance;
    }

    public String getIsFullPrice() {
        return isFullPrice;
    }

    public void setIsFullPrice(String isFullPrice) {
        this.isFullPrice = isFullPrice;
    }
}