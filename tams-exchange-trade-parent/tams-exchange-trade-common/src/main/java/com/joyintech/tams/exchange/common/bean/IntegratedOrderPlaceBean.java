package com.joyintech.tams.exchange.common.bean;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.tams.exchange.common.bean.request.BaseOrderRequest;
import com.joyintech.tams.exchange.common.utils.DoubleSerializerUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@ApiModel(value = "综合业务下单请求报文", description = "综合业务下单请求报文")
public class IntegratedOrderPlaceBean extends BaseOrderRequest implements Serializable {
    private static final long serialVersionUID = -8967119294934066076L;

    @ApiModelProperty(required = true, name = "id", value = "综合业务委托ID")
    private String id;

    @ApiModelProperty(required = true, name = "positionStr", value = "定位串")
    private String positionStr;

    @ApiModelProperty(required = true, name = "parentOrderId", value = "订单编号")
    private String parentOrderId;

//    @ApiModelProperty(required = true, name = "instructionId", value = "指令编号")
//    @NotBlank(message = "指令编号不能为空")
//    private String instructionId;

    @ApiModelProperty(required = true, name = "portfolioId", value = "组合代码")
    @NotBlank(message = "组合代码不能为空")
    private String portfolioId;

    @ApiModelProperty(required = true, name = "userId", value = "操作人Id")
    @NotBlank(message = "操作人Id不能为空")
    private String userId;

    @ApiModelProperty(required = true, name = "username", value = "操作人名称")
    @NotBlank(message = "操作人名称不能为空")
    private String username;

    @ApiModelProperty(required = true, name = "accountId", value = "网点账号")
    @NotBlank(message = "网点账号不能为空")
    private String accountId;

    @ApiModelProperty(required = true, name = "financeAccount", value = "资金账号")
    private String financeAccount;

    @ApiModelProperty(required = true, name = "accountName", value = "网点账号名称")
    private String accountName;

    @ApiModelProperty(required = true, name = "assetType", value = "资产类型")
    private String assetType;

    @ApiModelProperty(required = true, name = "market", value = "市场")
    private String market;

    @ApiModelProperty(required = true, name = "stockCode", value = "证券代码")
    @NotBlank(message = "证券代码不能为空")
    private String stockCode;

    @ApiModelProperty(required = true, name = "stockName", value = "证券名称")
    private String stockName;

    @ApiModelProperty(required = true, name = "entrustType", value = "委托类型")
    private String entrustType;

    @ApiModelProperty(required = true, name = "entrustBs", value = "交易方向")
    private String entrustBs;

    @ApiModelProperty(required = true, name = "bsFlag", value = "买卖标志")
    private String bsFlag;

    @ApiModelProperty(required = true, name = "discount", value = "质押券折算比例（协议回购）")
    @Digits(integer = 10, fraction = 4)
    @JsonSerialize(using = DoubleSerializerUtils.class)
    private BigDecimal discount;

    @ApiModelProperty(required = true, name = "transCode", value = "到期续作类型（0-非第三方，1-第三方）")
    private String transCode;//借用字段 质权人类型 协议回购到期续作指令必填 0-非第三方 1-第三方

    @ApiModelProperty(required = true, name = "expireYearRate", value = "到期交割比率")
    @Digits(integer = 10, fraction = 4)
    @JsonSerialize(using = DoubleSerializerUtils.class)
    private BigDecimal expireYearRate;

    @ApiModelProperty(required = true, name = "pretermYearRate", value = "")
    @Digits(integer = 10, fraction = 4)
    private long pretermYearRate;

    @ApiModelProperty(required = true, name = "liaisonName", value = "联络人姓名")
    private String liaisonName;

    @ApiModelProperty(required = true, name = "liaisonTel", value = "对手方交易员ID")
    private String liaisonTel;

    @ApiModelProperty(required = true, name = "propSeatNo", value = "对手方交易商ID")
    private String propSeatNo;

    @ApiModelProperty(required = true, name = "sellerNo", value = "本方交易员ID")
    private String sellerNo;

    @ApiModelProperty(required = true, name = "propSeatName", value = "对手方交易商名称")
    private String propSeatName;

    // 协商成交、协议回购使用 --start
    @ApiModelProperty(required = true, name = "tradeId", value = "本方交易员ID")
    @Size(max = 32)
    private String tradeId;

    @ApiModelProperty(required = true, name = "dealerId", value = "本方交易商ID")
    @Size(max = 32)
    private String dealerId;

    @ApiModelProperty(required = true, name = "dealerName", value = "本方交易商名称")
    @Size(max = 32)
    private String dealerName;

    @ApiModelProperty(required = true, name = "investorId", value = "本方交易主体ID")
    private String investorId;

    @ApiModelProperty(required = true, name = "investorName", value = "本方交易主体名称")
    private String investorName;

    @ApiModelProperty(required = false, name = "counterId", value = "交易对手")
    private String counterId;

    @ApiModelProperty(required = true, name = "propTradeId", value = "对手方交易员")
    private String propTradeId;

    @ApiModelProperty(required = true, name = "propDealerId", value = "对手方交易商")
    private String propDealerId;

    @ApiModelProperty(required = true, name = "propDealerName", value = "对手方交易商名称")
    private String propDealerName;

    @ApiModelProperty(required = true, name = "propInvestorId", value = "对手方交易主体ID")
    private String propInvestorId;

    @ApiModelProperty(required = true, name = "propInvestorName", value = "对手方交易主体名称")
    private String propInvestorName;

    @ApiModelProperty(required = true, name = "propOwnerType", value = "对手方报价所有者类型")
    private String propOwnerType;
    // 协商成交、协议回购使用 --end

    @ApiModelProperty(required = true, name = "contractId", value = "交易所请求编号")
    private String contractId;

    @ApiModelProperty(required = true, name = "firstSettleDate", value = "首次结算日")
    private String firstSettleDate;

    @ApiModelProperty(required = true, name = "backDate", value = "到期结算日")
    private String backDate;

    @ApiModelProperty(required = true, name = "compactTerm", value = "回购期限")
    @Digits(integer = 4, fraction = 0)
    private int compactTerm;

    @ApiModelProperty(required = true, name = "backBalance", value = "到期结算金额")
    @Digits(integer = 20, fraction = 4)
    @JsonSerialize(using = DoubleSerializerUtils.class)
    private BigDecimal backBalance;

    @ApiModelProperty(required = true, name = "origSerialNo", value = "原交易编号")
    private String origSerialNo;

    @ApiModelProperty(required = true, name = "reductionType", value = "")
    private String reductionType;

    @ApiModelProperty(required = true, name = "frozenBalance", value = "冻结金额")
    @Digits(integer = 20, fraction = 4)
    @JsonSerialize(using = DoubleSerializerUtils.class)
    private BigDecimal frozenBalance;

    @ApiModelProperty(required = true, name = "frozenVolume", value = "冻结数量")
    @Digits(integer = 20, fraction = 0)
    private long frozenVolume;

    @ApiModelProperty(required = true, name = "tradedBalance", value = "成交金额")
    @Digits(integer = 20, fraction = 4)
    @JsonSerialize(using = DoubleSerializerUtils.class)
    private BigDecimal tradedBalance;

    @ApiModelProperty(required = true, name = "tradedVolume", value = "成交数量")
    @Digits(integer = 20, fraction = 0)
    private long tradedVolume;

    @ApiModelProperty(required = true, name = "tradedPrice", value = "成交价格")
    @Digits(integer = 20, fraction = 4)
    @JsonSerialize(using = DoubleSerializerUtils.class)
    private BigDecimal tradedPrice;

    @ApiModelProperty(required = true, name = "cancelVolume", value = "撤销数量")
    @Digits(integer = 20, fraction = 0)
    private long cancelVolume;

    @ApiModelProperty(required = true, name = "reportTime", value = "申报时间")
    private String reportTime;

    @ApiModelProperty(required = true, name = "tradeTime", value = "成交时间")
    private String tradeTime;

    @ApiModelProperty(required = true, name = "cancelTime", value = "撤销时间")
    private String cancelTime;

    @ApiModelProperty(required = true, name = "errorCode", value = "错误码")
    private int errorCode;

    @ApiModelProperty(required = true, name = "errorMsg", value = "错误描述")
    private String errorMsg;

    @ApiModelProperty(required = true, name = "tradingDay", value = "交易日期")
    private String tradingDay;

    @ApiModelProperty(required = true, name = "localEntrustId", value = "本地委托编号")
    private String localEntrustId;

    @ApiModelProperty(required = true, name = "brokerEntrustId", value = "柜台委托编号")
    private String brokerEntrustId;

    @ApiModelProperty(required = true, name = "ipMac", value = "IP及MAC信息")
    private String ipMac;

    @ApiModelProperty(required = true, name = "remark", value = "备注")
    private String remark;

    @ApiModelProperty(required = true, name = "createTime", value = "创建时间")
    private Date createTime;

    @ApiModelProperty(required = true, name = "updateTime", value = "修改时间")
    private Date updateTime;

    @ApiModelProperty(required = true, name = "entrustStatus", value = "委托状态")
    private int entrustStatus;

    @ApiModelProperty(required = true, name = "origEntrustId", value = "原委托ID")
    private String origEntrustId;

    @ApiModelProperty(required = true, name = "count", value = "记录数")
    private int count;

    @ApiModelProperty(required = true, name = "isFullPrice", value = "是否全价（Y-是 ，N-否）")
    private String isFullPrice;

    @ApiModelProperty(required = true, name = "reportNo", value = "柜台报盘编号")
    private String reportNo;

    @ApiModelProperty(required = true, name = "noCancelEntrust", value = "")
    private boolean noCancelEntrust = false;

    @ApiModelProperty(required = true, name = "operateUser", value = "操作人员ID 执行撤销异常委托操作人员ID")
    private String operateUser;

    @ApiModelProperty( name = "execId", value = "执行编号")
    private String execId;


    public boolean isNoCancelEntrust() {
        return noCancelEntrust;
    }

    public void setNoCancelEntrust(boolean noCancelEntrust) {
        this.noCancelEntrust = noCancelEntrust;
    }

    public IntegratedOrderPlaceBean() {
        this.fullPrice = new BigDecimal("0.00");
        this.entrustPrice = new BigDecimal("0.00");
        this.entrustBalance = new BigDecimal("0.00");
        this.discount = new BigDecimal("0.00");
        this.expireYearRate = new BigDecimal("0.00");
        this.backBalance = new BigDecimal("0.00");
        this.frozenBalance = new BigDecimal("0.00");
        this.tradedBalance = new BigDecimal("0.00");
        this.tradedPrice = new BigDecimal("0.00");
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getParentOrderId() {
        return parentOrderId;
    }

    public void setParentOrderId(String parentOrderId) {
        this.parentOrderId = parentOrderId == null ? null : parentOrderId.trim();
    }

    public String getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId == null ? null : portfolioId.trim();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId == null ? null : userId.trim();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username == null ? null : username.trim();
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

    public String getAssetType() {
        return assetType;
    }

    public void setAssetType(String assetType) {
        this.assetType = assetType == null ? null : assetType.trim();
    }

    public String getMarket() {
        return market;
    }

    public void setMarket(String market) {
        this.market = market == null ? null : market.trim();
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

    public String getEntrustType() {
        return entrustType;
    }

    public void setEntrustType(String entrustType) {
        this.entrustType = entrustType == null ? null : entrustType.trim();
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

    public BigDecimal getExpireYearRate() {
        return expireYearRate;
    }

    public void setExpireYearRate(BigDecimal expireYearRate) {
        this.expireYearRate = expireYearRate;
    }

    public long getPretermYearRate() {
        return pretermYearRate;
    }

    public void setPretermYearRate(long pretermYearRate) {
        this.pretermYearRate = pretermYearRate;
    }

    public String getLiaisonName() {
        return liaisonName;
    }

    public void setLiaisonName(String liaisonName) {
        this.liaisonName = liaisonName == null ? null : liaisonName.trim();
    }

    public String getLiaisonTel() {
        return liaisonTel;
    }

    public void setLiaisonTel(String liaisonTel) {
        this.liaisonTel = liaisonTel == null ? null : liaisonTel.trim();
    }

    public String getPropSeatNo() {
        return propSeatNo;
    }

    public void setPropSeatNo(String propSeatNo) {
        this.propSeatNo = propSeatNo == null ? null : propSeatNo.trim();
    }

    public String getSellerNo() {
        return sellerNo;
    }

    public void setSellerNo(String sellerNo) {
        this.sellerNo = sellerNo == null ? null : sellerNo.trim();
    }

    public String getPropSeatName() {
        return propSeatName;
    }

    public void setPropSeatName(String propSeatName) {
        this.propSeatName = propSeatName == null ? null : propSeatName.trim();
    }

    public String getContractId() {
        return contractId;
    }

    public void setContractId(String contractId) {
        this.contractId = contractId == null ? null : contractId.trim();
    }

    public String getFirstSettleDate() {
        return firstSettleDate;
    }

    public void setFirstSettleDate(String firstSettleDate) {
        this.firstSettleDate = firstSettleDate == null ? null : firstSettleDate.trim();
    }

    public String getBackDate() {
        return backDate;
    }

    public void setBackDate(String backDate) {
        this.backDate = backDate == null ? null : backDate.trim();
    }

    public int getCompactTerm() {
        return compactTerm;
    }

    public void setCompactTerm(int compactTerm) {
        this.compactTerm = compactTerm;
    }

    public BigDecimal getBackBalance() {
        return backBalance;
    }

    public void setBackBalance(BigDecimal backBalance) {
        this.backBalance = backBalance;
    }

    public String getOrigSerialNo() {
        return origSerialNo;
    }

    public void setOrigSerialNo(String origSerialNo) {
        this.origSerialNo = origSerialNo == null ? null : origSerialNo.trim();
    }

    public String getReductionType() {
        return reductionType;
    }

    public void setReductionType(String reductionType) {
        this.reductionType = reductionType == null ? null : reductionType.trim();
    }

    public BigDecimal getFrozenBalance() {
        return frozenBalance;
    }

    public void setFrozenBalance(BigDecimal frozenBalance) {
        this.frozenBalance = frozenBalance;
    }

    public long getFrozenVolume() {
        return frozenVolume;
    }

    public void setFrozenVolume(long frozenVolume) {
        this.frozenVolume = frozenVolume;
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

    public long getCancelVolume() {
        return cancelVolume;
    }

    public void setCancelVolume(long cancelVolume) {
        this.cancelVolume = cancelVolume;
    }

    public String getReportTime() {
        return reportTime;
    }

    public void setReportTime(String reportTime) {
        this.reportTime = reportTime == null ? null : reportTime.trim();
    }

    public String getTradeTime() {
        return tradeTime;
    }

    public void setTradeTime(String tradeTime) {
        this.tradeTime = tradeTime == null ? null : tradeTime.trim();
    }

    public String getCancelTime() {
        return cancelTime;
    }

    public void setCancelTime(String cancelTime) {
        this.cancelTime = cancelTime == null ? null : cancelTime.trim();
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
        this.errorMsg = errorMsg == null ? null : errorMsg.trim();
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

    public String getIpMac() {
        return ipMac;
    }

    public void setIpMac(String ipMac) {
        this.ipMac = ipMac == null ? null : ipMac.trim();
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
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

    public int getEntrustStatus() {
        return entrustStatus;
    }

    public void setEntrustStatus(int entrustStatus) {
        this.entrustStatus = entrustStatus;
    }

    public String getOrigEntrustId() {
        return origEntrustId;
    }

    public void setOrigEntrustId(String origEntrustId) {
        this.origEntrustId = origEntrustId == null ? null : origEntrustId.trim();
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getPositionStr() {
        return positionStr;
    }

    public void setPositionStr(String positionStr) {
        this.positionStr = positionStr;
    }

    public String getIsFullPrice() {
        return isFullPrice;
    }

    public void setIsFullPrice(String isFullPrice) {
        this.isFullPrice = isFullPrice;
    }

    public String getReportNo() {
        return reportNo;
    }

    public void setReportNo(String reportNo) {
        this.reportNo = reportNo;
    }

    public String getOperateUser() {
        return operateUser;
    }

    public void setOperateUser(String operateUser) {
        this.operateUser = operateUser;
    }

    public String getOffsetFlag() {
        return null;
    }

    public void setOffsetFlag(String offsetFlag) {

    }

    public String getHedgeFlag() {
        return null;
    }

    public void setHedgeFlag(String hedgeFlag) {

    }

    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }

    public String getDealerId() {
        return dealerId;
    }

    public void setDealerId(String dealerId) {
        this.dealerId = dealerId;
    }

    public String getDealerName() {
        return dealerName;
    }

    public void setDealerName(String dealerName) {
        this.dealerName = dealerName;
    }

    public String getInvestorId() {
        return investorId;
    }

    public void setInvestorId(String investorId) {
        this.investorId = investorId;
    }

    public String getInvestorName() {
        return investorName;
    }

    public void setInvestorName(String investorName) {
        this.investorName = investorName;
    }

    public String getCounterId() {
        return counterId;
    }

    public void setCounterId(String counterId) {
        this.counterId = counterId;
    }

    public String getPropTradeId() {
        return propTradeId;
    }

    public void setPropTradeId(String propTradeId) {
        this.propTradeId = propTradeId;
    }

    public String getPropDealerId() {
        return propDealerId;
    }

    public void setPropDealerId(String propDealerId) {
        this.propDealerId = propDealerId;
    }

    public String getPropDealerName() {
        return propDealerName;
    }

    public void setPropDealerName(String propDealerName) {
        this.propDealerName = propDealerName;
    }

    public String getPropInvestorId() {
        return propInvestorId;
    }

    public void setPropInvestorId(String propInvestorId) {
        this.propInvestorId = propInvestorId;
    }

    public String getPropInvestorName() {
        return propInvestorName;
    }

    public void setPropInvestorName(String propInvestorName) {
        this.propInvestorName = propInvestorName;
    }

    public String getPropOwnerType() {
        return propOwnerType;
    }

    public void setPropOwnerType(String propOwnerType) {
        this.propOwnerType = propOwnerType;
    }

    public String getExecId() {
        return execId;
    }

    public void setExecId(String execId) {
        this.execId = execId;
    }
}
