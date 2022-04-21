package com.joyintech.tams.exchange.common.db.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.tams.exchange.common.utils.DoubleSerializerUtils;
import com.joyintech.tams.exchange.common.utils.TradeUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.data.annotation.Id;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@ApiModel(value = "综合业务下单响应报文", description = "综合业务下单响应报文")
public class IntegratedEntrust extends BaseEntity implements Serializable, EntrustInterface, KeyMethodInterface<String> {
    private static final long serialVersionUID = -7278292640597594312L;

    @ApiModelProperty(required = true, name = "id", value = "综合业务委托ID")
    @NotNull
    @Size(max = 32)
    @Id
    private String id;

    @ApiModelProperty(required = true, name = "positionStr", value = "定位串")
    private String positionStr;

    @ApiModelProperty(required = true, name = "parentOrderId", value = "订单编号")
    @Size(max = 32)
    private String parentOrderId;

    @ApiModelProperty(required = true, name = "instructionId", value = "指令编号")
    @NotNull
    @Size(max = 32)
    private String instructionId;

    @ApiModelProperty(required = true, name = "portfolioId", value = "组合代码")
    @NotNull
    @Size(max = 32)
    private String portfolioId;

    @ApiModelProperty(required = true, name = "userId", value = "操作人Id")
    @NotNull
    @Size(max = 18)
    private String userId;

    @ApiModelProperty(required = true, name = "username", value = "操作人名称")
    @NotNull
    @Size(max = 255)
    private String username;

    @ApiModelProperty(required = true, name = "accountId", value = "网点账号")
    @NotNull
    @Size(max = 32)
    private String accountId;

    @ApiModelProperty(required = true, name = "financeAccount", value = "资金账号")
    @NotNull
    @Size(max = 32)
    private String financeAccount;

    @ApiModelProperty(required = true, name = "accountName", value = "网点账号名称")
    @NotNull
    @Size(max = 32)
    private String accountName;

    @ApiModelProperty(required = true, name = "assetType", value = "资产类型")
    @NotNull
    @Size(max = 3)
    private String assetType;

    @ApiModelProperty(required = true, name = "market", value = "市场")
    @NotNull
    @Size(max = 8)
    private String market;

    @ApiModelProperty(required = true, name = "stockCode", value = "证券代码")
    @NotNull
    @Size(max = 32)
    private String stockCode;

    @ApiModelProperty(required = true, name = "stockName", value = "证券名称")
    @NotNull
    @Size(max = 32)
    private String stockName;

    @ApiModelProperty(required = true, name = "entrustType", value = "委托类型")
    @NotNull
    @Size(max = 1)
    private String entrustType;

    @ApiModelProperty(required = true, name = "entrustProp", value = "委托属性")
    @NotNull
    @Size(max = 4)
    private String entrustProp;

    @ApiModelProperty(required = true, name = "entrustBs", value = "交易方向")
    @NotNull
    @Size(max = 1)
    private String entrustBs;

    @ApiModelProperty(required = true, name = "bsFlag", value = "买卖标志")
    @NotNull
    @Size(max = 2)
    private String bsFlag;

    @ApiModelProperty(required = true, name = "entrustVolume", value = "委托数量")
    @NotNull
    @Digits(integer = 20, fraction = 0)
    private long entrustVolume;

    @ApiModelProperty(required = true, name = "entrustPrice", value = "委托价格")
    @NotNull
    @Digits(integer = 20, fraction = 4)
    @JsonSerialize(using = DoubleSerializerUtils.class)
    private BigDecimal entrustPrice;

    @ApiModelProperty(required = true, name = "fullPrice", value = "全价价格")
    @Digits(integer = 20, fraction = 4)
    @JsonSerialize(using = DoubleSerializerUtils.class)
    private BigDecimal fullPrice;

    @ApiModelProperty(required = true, name = "entrustBalance", value = "委托金额")
    @NotNull
    @Digits(integer = 20, fraction = 4)
    @JsonSerialize(using = DoubleSerializerUtils.class)
    private BigDecimal entrustBalance;

    @ApiModelProperty(required = true, name = "discount", value = "质押券折算比例")
    @NotNull
    @Digits(integer = 10, fraction = 4)
    @JsonSerialize(using = DoubleSerializerUtils.class)
    private BigDecimal discount;

    @ApiModelProperty(required = true, name = "transCode", value = "到期续作类型（0-非第三方，1-第三方）")
    @Size(max = 32)
    private String transCode;//借用字段 质权人类型 协议回购到期续作指令必填 0-非第三方 1-第三方

    @ApiModelProperty(required = true, name = "expireYearRate", value = "到期交割比率")
    @NotNull
    @Digits(integer = 10, fraction = 4)
    @JsonSerialize(using = DoubleSerializerUtils.class)
    private BigDecimal expireYearRate;

    @ApiModelProperty(required = true, name = "pretermYearRate", value = "")
    @NotNull
    @Digits(integer = 10, fraction = 4)
    private long pretermYearRate;

    @ApiModelProperty(required = true, name = "ownerType", value = "报价所有者类型")
    private String ownerType;

    @ApiModelProperty(required = true, name = "sellerNo", value = "本方交易员（上海协议回购），本方交易商（深圳协议回购）")
    @Size(max = 32)
    private String sellerNo;

    @ApiModelProperty(required = true, name = "liaisonName", value = "联系人（大宗交易），对手交易员（固收借用）")
    @Size(max = 32)
    private String liaisonName;

    @ApiModelProperty(required = true, name = "liaisonTel", value = "联系方式（大宗交易），本方交易员（固收借用），对手方交易员（上海协议回购借用）")
    @Size(max = 32)
    private String liaisonTel;

    @ApiModelProperty(required = true, name = "propSeatNo", value = "对手方席位（大宗、深圳协议回购），对手方交易商（固收、上海协议回购借用）")
    @Size(max = 16)
    private String propSeatNo;

    @ApiModelProperty(required = true, name = "propSeatName", value = "对手方交易商名称（上海协议回购借用）")
    @Size(max = 32)
    private String propSeatName;

    @ApiModelProperty(required = true, name = "tradeId", value = "本方交易员ID")
    private String tradeId;

    @ApiModelProperty(required = true, name = "dealerId", value = "本方交易商ID")
    private String dealerId;

    @ApiModelProperty(required = true, name = "dealerName", value = "本方交易商名称")
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

    @ApiModelProperty(required = true, name = "contractId", value = "交易所请求编号，约定号")
    @Size(max = 32)
    private String contractId;

    @ApiModelProperty(required = true, name = "firstSettleDate", value = "首次结算日")
    @Size(max = 8)
    private String firstSettleDate;

    @ApiModelProperty(required = true, name = "backDate", value = "到期结算日")
    @Size(max = 8)
    private String backDate;

    @ApiModelProperty(required = true, name = "compactTerm", value = "回购期限")
    @NotNull
    @Digits(integer = 4, fraction = 0)
    private int compactTerm;

    @ApiModelProperty(required = true, name = "backBalance", value = "到期结算金额")
    @NotNull
    @Digits(integer = 20, fraction = 4)
    @JsonSerialize(using = DoubleSerializerUtils.class)
    private BigDecimal backBalance;

    @ApiModelProperty(required = true, name = "origSerialNo", value = "原交易编号")
    @Size(max = 32)
    private String origSerialNo;

    @ApiModelProperty(required = true, name = "reductionType", value = "")
    @NotNull
    @Size(max = 1)
    private String reductionType;

    @ApiModelProperty(required = true, name = "frozenBalance", value = "冻结金额")
    @NotNull
    @Digits(integer = 20, fraction = 4)
    @JsonSerialize(using = DoubleSerializerUtils.class)
    private BigDecimal frozenBalance;

    @ApiModelProperty(required = true, name = "frozenVolume", value = "冻结数量")
    @NotNull
    @Digits(integer = 20, fraction = 0)
    private long frozenVolume;

    @ApiModelProperty(required = true, name = "tradedBalance", value = "成交金额")
    @NotNull
    @Digits(integer = 20, fraction = 4)
    @JsonSerialize(using = DoubleSerializerUtils.class)
    private BigDecimal tradedBalance;

    @ApiModelProperty(required = true, name = "tradedVolume", value = "成交数量")
    @NotNull
    @Digits(integer = 20, fraction = 0)
    private long tradedVolume;

    @ApiModelProperty(required = true, name = "tradedPrice", value = "成交价格")
    @NotNull
    @Digits(integer = 20, fraction = 4)
    @JsonSerialize(using = DoubleSerializerUtils.class)
    private BigDecimal tradedPrice;

    @ApiModelProperty(required = true, name = "cancelVolume", value = "撤销数量")
    @NotNull
    @Digits(integer = 20, fraction = 0)
    private long cancelVolume;

    @ApiModelProperty(required = true, name = "reportTime", value = "申报时间")
    @Size(max = 32)
    private String reportTime;

    @ApiModelProperty(required = true, name = "tradeTime", value = "成交时间")
    @Size(max = 32)
    private String tradeTime;

    @ApiModelProperty(required = true, name = "cancelTime", value = "撤销时间")
    @Size(max = 32)
    private String cancelTime;

    @ApiModelProperty(required = true, name = "errorCode", value = "错误码")
    @NotNull
    @Digits(integer = 8, fraction = 0)
    private int errorCode;

    @ApiModelProperty(required = true, name = "errorMsg", value = "错误描述")
    @Size(max = 128)
    private String errorMsg;

    @ApiModelProperty(required = true, name = "tradingDay", value = "交易日期")
    @NotNull
    @Size(max = 10)
    private String tradingDay;

    @ApiModelProperty(required = true, name = "localEntrustId", value = "本地委托编号")
    @Size(max = 32)
    private String localEntrustId;

    @ApiModelProperty(required = true, name = "brokerEntrustId", value = "柜台委托编号")
    @Size(max = 32)
    private String brokerEntrustId;

    @ApiModelProperty(required = true, name = "ipMac", value = "IP及MAC信息")
    @Size(max = 64)
    private String ipMac;

    @ApiModelProperty(required = true, name = "remark", value = "备注")
    @Size(max = 64)
    private String remark;

    @ApiModelProperty(required = true, name = "createTime", value = "创建时间")
    private Date createTime;

    @ApiModelProperty(required = true, name = "updateTime", value = "修改时间")
    private Date updateTime;

    @ApiModelProperty(required = true, name = "entrustStatus", value = "委托状态")
    @NotNull
    @Digits(integer = 6, fraction = 0)
    private int entrustStatus;

    @ApiModelProperty(required = true, name = "origEntrustId", value = "原委托ID")
    @Size(max = 32)
    private String origEntrustId;

    @ApiModelProperty(required = true, name = "count", value = "记录数")
    private int count;

    @ApiModelProperty(required = true, name = "isFullPrice", value = "是否全价（Y-是 ，N-否）")
    private String isFullPrice;

    @ApiModelProperty(required = true, name = "reportNo", value = "柜台报盘编号")
    @Size(max = 32)
    private String reportNo;

    @ApiModelProperty(required = true, name = "noCancelEntrust", value = "")
    private boolean noCancelEntrust = false;

    @ApiModelProperty(required = true, name = "operateUser", value = "操作人员ID 执行撤销异常委托操作人员ID")
    @Size(max = 20)
    private String operateUser;

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

    @ApiModelProperty(name = "softSkipMode", value = "软控是否继续？Y：继续", example = "Y")
    private String softSkipMode;

    @ApiModelProperty(name = "busiTypeTg", value = "投管业务类型")
    private String busiTypeTg;
    @ApiModelProperty(name = "extend1", value = "扩展信息1")
    private String extend1;
    @ApiModelProperty(name = "extend2", value = "扩展信息2")
    private String extend2;

    @ApiModelProperty(name = "tradeDate", value = "成交时间")
    private String tradeDate;
    @ApiModelProperty(name = "createDateTime", value = "委托时间")
    private String createDateTime;

    @ApiModelProperty(name = "entrustClass", value = "委托指令类别：01-单组合，02-多组合")
    private String entrustClass;

    @ApiModelProperty(name = "targetType", value = "目标类型")
    private String targetType;
    @ApiModelProperty(name = "isNoGuard", value = "是否非担保")
    private String isNoGuard;
    @ApiModelProperty(name = "isNetPrice", value = "是否净价交易")
    private String isNetPrice;

    public String getSoftSkipMode() {
        return softSkipMode;
    }

    public void setSoftSkipMode(String softSkipMode) {
        this.softSkipMode = softSkipMode;
    }

    public boolean isNoCancelEntrust() {
        return noCancelEntrust;
    }

    public void setNoCancelEntrust(boolean noCancelEntrust) {
        this.noCancelEntrust = noCancelEntrust;
    }

    public IntegratedEntrust() {
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

    @Id
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

    public String getInstructionId() {
        return instructionId;
    }

    public void setInstructionId(String instructionId) {
        this.instructionId = instructionId == null ? null : instructionId.trim();
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

    public long getEntrustVolume() {
        return entrustVolume;
    }

    public void setEntrustVolume(long entrustVolume) {
        this.entrustVolume = entrustVolume;
    }

    public BigDecimal getEntrustPrice() {
        return entrustPrice;
    }

    public void setEntrustPrice(BigDecimal entrustPrice) {
        this.entrustPrice = entrustPrice;
    }

    public BigDecimal getFullPrice() {
        return fullPrice;
    }

    public void setFullPrice(BigDecimal fullPrice) {
        this.fullPrice = fullPrice;
    }

    public BigDecimal getEntrustBalance() {
        return entrustBalance;
    }

    public void setEntrustBalance(BigDecimal entrustBalance) {
        this.entrustBalance = entrustBalance;
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

    @Override
    public String getIsFullPrice() {
        return isFullPrice;
    }

    @Override
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

    public String getCreateDateTime() {
        return DateUtil.formatDateTime(createTime);
    }

    public void setCreateDateTime(String createDateTime) {
        this.createDateTime = createDateTime;
    }

    public String getEntrustClass() {
        return entrustClass;
    }

    public void setEntrustClass(String entrustClass) {
        this.entrustClass = entrustClass;
    }

    public String getOwnerType() {
        return ownerType;
    }

    public void setOwnerType(String ownerType) {
        this.ownerType = ownerType;
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

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public String getIsNoGuard() {
        return isNoGuard;
    }

    public void setIsNoGuard(String isNoGuard) {
        this.isNoGuard = isNoGuard;
    }

    public String getIsNetPrice() {
        return isNetPrice;
    }

    public void setIsNetPrice(String isNetPrice) {
        this.isNetPrice = isNetPrice;
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
}