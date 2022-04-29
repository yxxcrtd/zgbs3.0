package com.joyintech.tams.exchange.common.db.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.tams.exchange.common.bean.OrderPlaceBean;
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

import static com.joyintech.tams.exchange.common.constant.joyin.DictCodeListExt3.投管业务类型_通用式质押回购;

@ApiModel(value = "委托报文", description = "委托报文")
public class Entrust extends BaseEntity implements Serializable, EntrustInterface, Cloneable, KeyMethodInterface<String> {
    private static final long serialVersionUID = 4354102783892014792L;
    @NotNull
    @Size(max = 32)
    @Id
    private String id;

    private String positionStr;

    @ApiModelProperty(name = "parentOrderId", value = "父订单ID")
    @Size(max = 32)
    private String parentOrderId;

    @ApiModelProperty(name = "instructionId", value = "指令ID")
    @NotNull
    @Size(max = 32)
    private String instructionId;

    @ApiModelProperty(name = "portfolioId", value = "组合ID")
    @NotNull
    @Size(max = 18)
    private String portfolioId;

    @ApiModelProperty(name = "userId", value = "用户ID")
    @NotNull
    @Size(max = 18)
    private String userId;

    @ApiModelProperty(name = "username", value = "用户名称")
    @NotNull
    @Size(max = 255)
    private String username;

    @ApiModelProperty(name = "accountId", value = "网点号:账号")
    @NotNull
    @Size(max = 32)
    private String accountId;

    @ApiModelProperty(name = "financeAccount", value = "资金账户ID")
    @NotNull
    @Size(max = 32)
    private String financeAccount;

    @ApiModelProperty(name = "accountName", value = "资金账户名称")
    @NotNull
    @Size(max = 32)
    private String accountName;

    @ApiModelProperty(name = "assetType", value = "资产类型：F01 债券；F02 基金；F07 质押式回购；F11 股票")
    @NotNull
    @Size(max = 3)
    private String assetType;

    @ApiModelProperty(name = "market", value = "交易场所")
    @NotNull
    @Size(max = 8)
    private String market;

    @ApiModelProperty(name = "stockCode", value = "证券代码")
    @NotNull
    @Size(max = 32)
    private String stockCode;

    @ApiModelProperty(name = "stockName", value = "证券名称")
    @NotNull
    @Size(max = 32)
    private String stockName;

    @ApiModelProperty(name = "entrustType", value = "委托类别")
    @NotNull
    @Size(max = 1)
    private String entrustType;

    @ApiModelProperty(name = "entrustProp", value = "委托属性Enum：发往券商柜台的交易属性标识")
    @NotNull
    @Size(max = 4)
    private String entrustProp;

    @ApiModelProperty(name = "entrustBs", value = "委托方向: 买入 卖出")
    @NotNull
    @Size(max = 1)
    private String entrustBs;

    @ApiModelProperty(name = "bsFlag", value = "委托方向Enum：发往券商柜台的交易方向标识")
    @NotNull
    @Size(max = 2)
    private String bsFlag;

    @ApiModelProperty(name = "entrustVolume", value = "委托数量")
    @NotNull
    @Digits(integer = 20, fraction = 0)
    private long entrustVolume;

    @ApiModelProperty(name = "entrustPrice", value = "委托价格")
    @NotNull
    @Digits(integer = 20, fraction = 4)
    @JsonSerialize(using = DoubleSerializerUtils.class)
    private BigDecimal entrustPrice;//净价价格

    @ApiModelProperty(name = "fullPrice", value = "全价价格")
    @Digits(integer = 20, fraction = 4)
    @JsonSerialize(using = DoubleSerializerUtils.class)
    private BigDecimal fullPrice;//全价价格

    @ApiModelProperty(name = "expireYearRate", value = "到期收益率")
//    @Digits(integer = 10, fraction = 4)
    @JsonSerialize(using = DoubleSerializerUtils.class)
    private BigDecimal expireYearRate;//到期收益率

    @ApiModelProperty(name = "frozenBalance", value = "冻结金额")
    @NotNull
    @Digits(integer = 20, fraction = 4)
    @JsonSerialize(using = DoubleSerializerUtils.class)
    private BigDecimal frozenBalance;

    @ApiModelProperty(name = "frozenVolume", value = "冻结数量")
    @NotNull
    @Digits(integer = 20, fraction = 0)
    private long frozenVolume;

    @ApiModelProperty(name = "tradedBalance", value = "已成交金额")
    @NotNull
    @Digits(integer = 20, fraction = 4)
    @JsonSerialize(using = DoubleSerializerUtils.class)
    private BigDecimal tradedBalance;

    @ApiModelProperty(name = "tradedVolume", value = "已成交数量")
    @NotNull
    @Digits(integer = 20, fraction = 0)
    private long tradedVolume;

    @ApiModelProperty(name = "tradedPrice", value = "成交价格")
    @NotNull
    @Digits(integer = 20, fraction = 4)
    @JsonSerialize(using = DoubleSerializerUtils.class)
    private BigDecimal tradedPrice;

    @ApiModelProperty(name = "cancelVolume", value = "撤销数量")
    @NotNull
    @Digits(integer = 20, fraction = 0)
    private long cancelVolume;

    @ApiModelProperty(name = "cancelBalance", value = "撤销金额")
    @NotNull
    @Digits(integer = 20, fraction = 4)
    private BigDecimal cancelBalance;

    @ApiModelProperty(name = "reportTime", value = "报文时间")
    private String reportTime;

    @ApiModelProperty(name = "tradeTime", value = "成交时间")
    private String tradeTime;

    @ApiModelProperty(name = "cancelTime", value = "撤销时间")
    private String cancelTime;

    @ApiModelProperty(name = "errorCode", value = "错误码")
    @NotNull
    @Digits(integer = 20, fraction = 0)
    private int errorCode;

    @ApiModelProperty(name = "errorMsg", value = "错误消息")
    @Size(max = 128)
    private String errorMsg;

    @ApiModelProperty(name = "tradingDay", value = "交易日期")
    @NotNull
    @Size(max = 10)
    private String tradingDay;

    @ApiModelProperty(name = "localEntrustId", value = "本地委托ID")
    @Size(max = 32)
    private String localEntrustId;

    @ApiModelProperty(name = "brokerEntrustId", value = "柜台委托ID")
    @Size(max = 32)
    private String brokerEntrustId;

    @ApiModelProperty(name = "ipMac", value = "IP及MAC信息")
    @Size(max = 64)
    private String ipMac;

    @ApiModelProperty(name = "remark", value = "备注")
    @Size(max = 64)
    private String remark;

    @ApiModelProperty(name = "createTime", value = "创建时间")
    private Date createTime;

    @ApiModelProperty(name = "updateTime", value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(name = "entrustStatus", value = "委托状态")
    @NotNull
    @Digits(integer = 6, fraction = 0)
    private int entrustStatus;

    @ApiModelProperty(name = "origEntrustId", value = "原委托ID")
    @Size(max = 32)
    private String origEntrustId;

    @ApiModelProperty(name = "count", value = "count")
    private int count;

    @ApiModelProperty(name = "isFullPrice", value = "全价（Y:是 ， N：否）")
    private String isFullPrice;

    @ApiModelProperty(name = "reportNo", value = "报文ID 柜台报盘编号")
    @Size(max = 32)
    private String reportNo;//柜台报盘编号

    @ApiModelProperty(name = "noCancelEntrust", value = "noCancelEntrust")
    private boolean noCancelEntrust = false;

    /**
     * 操作人员ID 执行撤销异常委托操作人员ID
     */
    @ApiModelProperty(name = "operateUser", value = "操作人员：复核使用")
    private String operateUser;

    @ApiModelProperty(name = "offsetFlag", value = "冲正标识")
    @Size(max = 8)
    private String offsetFlag;

    @ApiModelProperty(name = "hedgeFlag", value = "套期保值标识：期货使用")
    @Size(max = 8)
    private String hedgeFlag;

    @ApiModelProperty(name = "entrustBalance", value = "委托金额")
    @NotNull
    @Digits(integer = 20, fraction = 4)
    @JsonSerialize(using = DoubleSerializerUtils.class)
    private BigDecimal entrustBalance;

    @ApiModelProperty(name = "portfolioName", value = "组合名称", hidden = true)
    private String portfolioName;
    @ApiModelProperty(name = "secAcctId", value = "证券账户", hidden = true)
    private String secAcctId;
    @ApiModelProperty(name = "secAcctName", value = "证券账户名称", hidden = true)
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

    @ApiModelProperty(name = "tradeDate", value = "成交时间")
    private String tradeDate;
    @ApiModelProperty(name = "createDateTime", value = "委托时间")
    private String createDateTime;

    @ApiModelProperty(name = "conNum", value = "可转数量")
    private BigDecimal conNum;
    @ApiModelProperty(name = "entrustClass", value = "委托指令类别：01-单组合，02-多组合")
    private String entrustClass;

    @ApiModelProperty(name = "targetType", value = "目标类型")
    private String targetType;
    @ApiModelProperty(name = "isNoGuard", value = "是否非担保")
    private String isNoGuard;

    public boolean isNoCancelEntrust() {
        return noCancelEntrust;
    }

    public void setNoCancelEntrust(boolean noCancelEntrust) {
        this.noCancelEntrust = noCancelEntrust;
    }

    public Entrust() {
        this.entrustPrice = new BigDecimal("0.00");
        this.fullPrice = new BigDecimal("0.00");
        this.expireYearRate = new BigDecimal("0.00");
        this.frozenBalance = new BigDecimal("0.00");
        this.tradedBalance = new BigDecimal("0.00");
        this.tradedPrice = new BigDecimal("0.00");
        this.entrustBalance = new BigDecimal("0.00");
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

    public BigDecimal getExpireYearRate() {
        return expireYearRate;
    }

    public void setExpireYearRate(BigDecimal expireYearRate) {
        this.expireYearRate = expireYearRate;
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

    public BigDecimal getCancelBalance() {
        if (投管业务类型_通用式质押回购.getValue().equals(busiTypeTg)) {
            return BigDecimal.valueOf(cancelVolume * 100);
        } else if (entrustPrice != null) {
            return entrustPrice.multiply(BigDecimal.valueOf(cancelVolume));
        }

        return cancelBalance;
    }

    public void setCancelBalance(BigDecimal cancelBalance) {
        this.cancelBalance = cancelBalance;
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

    public String getPositionStr() {
        return positionStr;
    }

    public void setPositionStr(String positionStr) {
        this.positionStr = positionStr;
    }

    public String getReportNo() {
        return reportNo;
    }

    public void setReportNo(String reportNo) {
        this.reportNo = reportNo;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
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

    public BigDecimal getConNum() {
        return conNum;
    }

    public void setConNum(BigDecimal conNum) {
        this.conNum = conNum;
    }

    public String getEntrustClass() {
        return entrustClass;
    }

    public void setEntrustClass(String entrustClass) {
        this.entrustClass = entrustClass;
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

    public static Entrust getTemplateEntrust() {
        return templateEntrust;
    }

    public static void setTemplateEntrust(Entrust templateEntrust) {
        Entrust.templateEntrust = templateEntrust;
    }

    @Override
    public String getIsFullPrice() {
        return isFullPrice;
    }

    @Override
    public void setIsFullPrice(String isFullPrice) {
        this.isFullPrice = isFullPrice;
    }

    private static Entrust templateEntrust = new Entrust();

    public static Entrust createEntrust(OrderPlaceBean orderPlaceBean) {
        try {
            Entrust newOrder = (Entrust) templateEntrust.clone();
            newOrder.setInstructionId(orderPlaceBean.getInstructionId());
            newOrder.setPortfolioId(orderPlaceBean.getPortfolioId());
            newOrder.setUserId(orderPlaceBean.getUserId());
            newOrder.setUsername(orderPlaceBean.getUsername());
            newOrder.setAccountId(orderPlaceBean.getAccountId());
            newOrder.setEntrustProp(orderPlaceBean.getEntrustProp());
            newOrder.setEntrustBs(orderPlaceBean.getEntrustBs());
            newOrder.setStockCode(orderPlaceBean.getStockCode());
            newOrder.setEntrustVolume(orderPlaceBean.getEntrustVolume());
            newOrder.setEntrustPrice(orderPlaceBean.getEntrustPrice());
            newOrder.setFullPrice(orderPlaceBean.getFullPrice());
            newOrder.setOffsetFlag(orderPlaceBean.getOffsetFlag());
            newOrder.setHedgeFlag(orderPlaceBean.getHedgeFlag());
            return newOrder;
        } catch (Exception e) {
        }
        return new Entrust();
    }

    public Entrust cloneEntrust() {
        try {
            return (Entrust) this.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    public String getOperateUser() {
        return operateUser;
    }

    public void setOperateUser(String operateUser) {
        this.operateUser = operateUser;
    }

    public BigDecimal getEntrustBalance() {
        return entrustBalance;
    }

    public void setEntrustBalance(BigDecimal entrustBalance) {
        this.entrustBalance = entrustBalance;
    }
}