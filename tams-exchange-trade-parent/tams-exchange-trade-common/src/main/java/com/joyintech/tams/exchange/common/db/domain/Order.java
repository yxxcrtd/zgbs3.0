package com.joyintech.tams.exchange.common.db.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.exchange.common.bean.OrderPlaceBean;
import com.joyintech.tams.exchange.common.constant.BatchPriceMode;
import com.joyintech.tams.exchange.common.utils.DoubleSerializerUtils;
import com.joyintech.tams.exchange.common.utils.JsonUtils;
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
import java.util.Map;

@ApiModel(value = "订单报文", description = "订单报文")
public class Order implements Serializable, Cloneable, OrderInterface, KeyMethodInterface<String> {
    private static final long serialVersionUID = 7947633128705147688L;
    @NotNull
    @Size(max = 32)
    @Id
    private String id;

    @ApiModelProperty(name = "instructionId", value = "指令id")
    @NotNull
    @Size(max = 32)
    private String instructionId;

    @ApiModelProperty(name = "portfolioId", value = "组合id")
    @NotNull
    @Size(max = 18)
    private String portfolioId;

    @ApiModelProperty(name = "userId", value = "用户id")
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

    @ApiModelProperty(name = "accountName", value = "资金账户名称")
    @Size(max = 32)
    private String accountName;

    @ApiModelProperty(name = "financeAccount", value = "资金账户ID")
    @Size(max = 32)
    private String financeAccount;

    @ApiModelProperty(name = "assetType", value = "资产类型：F01 债券；F02 基金；F07 质押式回购；F11 股票")
    @NotNull
    @Size(max = 3)
    private String assetType;

    @ApiModelProperty(name = "algorithmId", value = "算法ID")
    @Size(max = 32)
    private String algorithmId;

    @ApiModelProperty(name = "relativeOrderId", value = "关联订单ID")
    @Size(max = 32)
    private String relativeOrderId;

    @ApiModelProperty(name = "businessFlag", value = "交易标识BusinessFlag：5 公平；6 批量")
    @NotNull
    @Size(max = 1)
    private String businessFlag;

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

    @ApiModelProperty(name = "stockCode", value = "证券代码")
    @NotNull
    @Size(max = 32)
    private String stockCode;

    @ApiModelProperty(name = "stockName", value = "证券名称")
    @NotNull
    @Size(max = 32)
    private String stockName;

    @ApiModelProperty(name = "orderStatus", value = "订单状态")
    @NotNull
    @Digits(integer = 6, fraction = 0)
    private int orderStatus;

    @ApiModelProperty(name = "orderVolume", value = "订单数量")
    @NotNull
    @Digits(integer = 20, fraction = 0)
    private long orderVolume;

    @ApiModelProperty(name = "orderPrice", value = "订单价格")
    @Digits(integer = 20, fraction = 4)
    @JsonSerialize(using = DoubleSerializerUtils.class)
    private BigDecimal orderPrice;

    @ApiModelProperty(name = "orderBalance", value = "订单金额")
    @Digits(integer = 20, fraction = 4)
    @JsonSerialize(using = DoubleSerializerUtils.class)
    private BigDecimal orderBalance;

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

    @ApiModelProperty(name = "expireYearRate", value = "收益率")
    private BigDecimal expireYearRate;

    @ApiModelProperty(name = "entrustBalance", value = "委托金额")
    @NotNull
    @Digits(integer = 20, fraction = 4)
    @JsonSerialize(using = DoubleSerializerUtils.class)
    private BigDecimal entrustBalance;

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

    @ApiModelProperty(name = "errorCode", value = "错误码")
    @NotNull
    @Digits(integer = 8, fraction = 0)
    private int errorCode;

    @ApiModelProperty(name = "errorMsg", value = "错误消息")
    @Size(max = 2)
    private String errorMsg;

    @ApiModelProperty(name = "tradingDay", value = "交易日期")
    @NotNull
    @Size(max = 10)
    private String tradingDay;

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

    @ApiModelProperty(name = "ps", value = "投管ps")
    private String ps; //投管ps

    @ApiModelProperty(name = "count", value = "count")
    private int count;

    @ApiModelProperty(name = "isFullPrice", value = "全价(Y:是 ， N：否)")
    private String isFullPrice;

    @ApiModelProperty(name = "offsetFlag", value = "冲正标识")
    private String offsetFlag;

    @ApiModelProperty(name = "hedgeFlag", value = "套期保值标识：期货使用")
    private String hedgeFlag;

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

    @ApiModelProperty(name="softSkipMode",value="软控是否继续？Y：继续",example="Y")
    private String softSkipMode;

    public Order() {
        this.orderPrice = new BigDecimal("0.00");
        this.orderBalance = new BigDecimal("0.00");
        this.entrustPrice = new BigDecimal("0.00");
        this.fullPrice = new BigDecimal("0.00");
        this.entrustBalance = new BigDecimal("0.00");
        this.frozenBalance = new BigDecimal("0.00");
        this.tradedBalance = new BigDecimal("0.00");
        this.tradedPrice = new BigDecimal("0.00");
        this.expireYearRate = new BigDecimal("0.00");
    }

    public String getPs() {
        return ps;
    }

    public void setPs(String ps) {
        this.ps = ps;
    }

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

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName == null ? null : accountName.trim();
    }

    public String getFinanceAccount() {
        return financeAccount;
    }

    public void setFinanceAccount(String financeAccount) {
        this.financeAccount = financeAccount == null ? null : financeAccount.trim();
    }

    public String getAssetType() {
        return assetType;
    }

    public void setAssetType(String assetType) {
        this.assetType = assetType == null ? null : assetType.trim();
    }

    public String getAlgorithmId() {
        return algorithmId;
    }

    public void setAlgorithmId(String algorithmId) {
        this.algorithmId = algorithmId == null ? null : algorithmId.trim();
    }

    public String getRelativeOrderId() {
        return relativeOrderId;
    }

    public void setRelativeOrderId(String relativeOrderId) {
        this.relativeOrderId = relativeOrderId == null ? null : relativeOrderId.trim();
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

    public int getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(int orderStatus) {
        this.orderStatus = orderStatus;
    }

    public long getOrderVolume() {
        return orderVolume;
    }

    public void setOrderVolume(long orderVolume) {
        this.orderVolume = orderVolume;
    }

    public BigDecimal getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(BigDecimal orderPrice) {
        this.orderPrice = orderPrice;
    }

    public BigDecimal getOrderBalance() {
        return orderBalance;
    }

    public void setOrderBalance(BigDecimal orderBalance) {
        this.orderBalance = orderBalance;
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

    @Override
    public String getIsFullPrice() {
        return isFullPrice;
    }

    @Override
    public void setIsFullPrice(String isFullPrice) {
        this.isFullPrice = isFullPrice;
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

    public BigDecimal getEntrustBalance() {
        return entrustBalance;
    }

    public void setEntrustBalance(BigDecimal entrustBalance) {
        this.entrustBalance = entrustBalance;
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

    public String getPriceMode() {
        if (StringUtil.isNotEmpty(extend2) && extend2.contains("priceMode")) {
            Map map = JsonUtils.getObjectFromJson(extend2, Map.class);
            Object priceMode = map.get("priceMode");
            if (priceMode != null) {
                return BatchPriceMode.getBatchPriceMode((int) map.get("priceMode"));
            }
        }
        return "";
    }

    public String getSoftSkipMode() {
        return softSkipMode;
    }

    public void setSoftSkipMode(String softSkipMode) {
        this.softSkipMode = softSkipMode;
    }

    public static Order getTemplateOrder() {
        return templateOrder;
    }

    public static void setTemplateOrder(Order templateOrder) {
        Order.templateOrder = templateOrder;
    }

    private static Order templateOrder = new Order();

    public static Order createOrder(OrderPlaceBean orderPlaceBean) {
        try {
            Order newOrder = (Order) templateOrder.clone();
            newOrder.setInstructionId(orderPlaceBean.getInstructionId());
            newOrder.setPortfolioId(orderPlaceBean.getPortfolioId());
            newOrder.setUserId(orderPlaceBean.getUserId());
            newOrder.setUsername(orderPlaceBean.getUsername());
            newOrder.setAccountId(orderPlaceBean.getAccountId());
            newOrder.setAlgorithmId(orderPlaceBean.getAlgorithmId());
            newOrder.setEntrustProp(orderPlaceBean.getEntrustProp());
            newOrder.setEntrustBs(orderPlaceBean.getEntrustBs());
            newOrder.setStockCode(orderPlaceBean.getStockCode());
            newOrder.setOrderVolume(orderPlaceBean.getEntrustVolume());
            newOrder.setOrderPrice(orderPlaceBean.getEntrustPrice());
            newOrder.setExpireYearRate(orderPlaceBean.getYield());
            newOrder.setOffsetFlag(orderPlaceBean.getOffsetFlag());
            newOrder.setHedgeFlag(orderPlaceBean.getHedgeFlag());
            newOrder.setFullPrice(orderPlaceBean.getFullPrice() == null ? new BigDecimal("0.00") : orderPlaceBean.getFullPrice());
            newOrder.setSoftSkipMode(orderPlaceBean.getSoftSkipMode());
            return newOrder;
        } catch (Exception e) {
        }
        return new Order();
    }

    public Order cloneOrder() {
        try {
            return (Order) this.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
}