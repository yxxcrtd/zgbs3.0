package com.joyintech.tams.exchange.common.bean;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.exchange.common.constant.Market;
import com.joyintech.tams.exchange.common.constant.joyin.*;
import com.joyintech.tams.exchange.common.utils.DoubleSerializerUtils;
import com.joyintech.tams.exchange.common.utils.TradeUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

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

    @ApiModelProperty(name = "secAcctName", value = "证券账户", hidden = true)
    private String secAcctName;
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

    @ApiModelProperty(name = "entrustClass", value = "委托指令类别：01-单组合，02-多组合")
    private String entrustClass;

    @ApiModelProperty(name = "busiTypeTgStr", value = "业务类型")
    private String busiTypeTgStr;

    /**
     * ///////////////////////////////////查询无用字段
     */
    @ApiModelProperty(value = "交易日期", hidden = true)
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

    @ApiModelProperty(name = "deliveryResult", value = "交收状态")
    private String deliveryResult;
    @ApiModelProperty(name = "deliverySchedule", value = "交收进度")
    private String deliverySchedule;

    private String tradeDate;
    private String dealTime;

    @JsonSerialize(using = DoubleSerializerUtils.class)
    @ApiModelProperty(value = "净价价格")
    private BigDecimal netPrice;

    @JsonSerialize(using = DoubleSerializerUtils.class)
    @ApiModelProperty(value = "净价金额")
    private BigDecimal netBalance;

    @ApiModelProperty(name = "isFullPrice", value = "是否全价（Y:是 ， N：否）")
    private String isFullPrice;

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
        if (StringUtil.isEmpty(market)) {
            return null;
        }
        return Market.getMarketName(market);
    }

    public void setMarketStr(String marketStr) {
        this.marketStr = marketStr;
    }

    public String getTradePlatStr() {
        if (StringUtil.isEmpty(tradePlat)) {
            return null;
        }
        DictCodeList dictCode = DictCodeList.from(DictCode.交易平台, tradePlat);
        if (Objects.nonNull(dictCode)) {
            return dictCode.getCodeName();
        }
        return tradePlat;
    }

    public void setTradePlatStr(String tradePlatStr) {
        this.tradePlatStr = tradePlatStr;
    }

    public String getFinprodType2Str() {
        if (StringUtil.isEmpty(finprodType2)) {
            return null;
        }
        DictCodeListExt1 dictCode = DictCodeListExt1.from(DictCode.金融产品类型, finprodType2);
        if (Objects.nonNull(dictCode)) {
            return dictCode.getCodeName();
        }
        return finprodType2;
    }

    public void setFinprodType2Str(String finprodType2Str) {
        this.finprodType2Str = finprodType2Str;
    }

    public String getEntrustBsStr() {
        if (StringUtil.isEmpty(ps)) {
            return null;
        }
        DictCodeListExt2 dictCode = DictCodeListExt2.from(DictCode.交易方向, ps);
        if (Objects.nonNull(dictCode)) {
            return dictCode.getCodeName();
        }
        return ps;
    }

    public void setEntrustBsStr(String entrustBsStr) {
        this.entrustBsStr = entrustBsStr;
    }

    public String getSecAcctName() {
        return secAcctName;
    }

    public void setSecAcctName(String secAcctName) {
        this.secAcctName = secAcctName;
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

    public String getBusiTypeTgStr() {
        if (StringUtil.isEmpty(busiTypeTg)) {
            return null;
        }
        DictCodeListExt3 dictCode = DictCodeListExt3.from(DictCode.投管业务类型, busiTypeTg);
        if (Objects.nonNull(dictCode)) {
            return dictCode.getCodeName();
        }
        return busiTypeTg;
    }

    public void setBusiTypeTgStr(String busiTypeTgStr) {
        this.busiTypeTgStr = busiTypeTgStr;
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
        return DateUtil.format(createTime, "HH:mm:ss");
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
