package com.joyintech.tams.exchange.common.db.domain;

import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.tams.exchange.common.bean.instrument.MinPriceAndMinTrdUnitLimitBean;
import com.joyintech.tams.exchange.common.constant.joyin.DictCodeListExt3;
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
import java.util.Objects;

@ApiModel(value = "指令信息", description = "指令信息")
public class Instruction implements Serializable, KeyMethodInterface<String>, Cloneable {
    private static final long serialVersionUID = -81616163906470229L;

    @ApiModelProperty(required = false, name = "approverFlage", value = "审批标志")
    @Size(max = 50)
    private String approverFlage;

    @ApiModelProperty(required = false, name = "conShareId", value = "转股代码")
    @Size(max = 50)
    private String conShareId;

    @ApiModelProperty(required = false, name = "conPrice", value = "转股价格")
    @Digits(integer = 30, fraction = 14)
    @JsonSerialize(using = DoubleSerializerUtils.class)
    private BigDecimal conPrice = new BigDecimal("0.0000");

    @ApiModelProperty(required = false, name = "conNum", value = "转股数量")
    @Digits(integer = 30, fraction = 4)
    @JsonSerialize(using = DoubleSerializerUtils.class)
    private BigDecimal conNum = new BigDecimal("0");

    @ApiModelProperty(required = false, name = "entrustId", value = "指令编号")
    @NotNull
    @Size(max = 32)
    @Id
    private String entrustId = "";

    @ApiModelProperty(required = false, name = "portfolioId", value = "组合代码")
    @Size(max = 32)
    private String portfolioId = "";

    @ApiModelProperty(required = false, name = "portfolioName", value = "组合名称")
    @Size(max = 200)
    private String portfolioName = "";

    @ApiModelProperty(required = false, name = "finprodId", value = "金融产品代码")
    @Size(max = 50)
    private String finprodId = "";

    @ApiModelProperty(required = false, name = "finprodAbbr", value = "金融产品简称")
    @Size(max = 200)
    private String finprodAbbr = "";

    @ApiModelProperty(required = false, name = "finprodName", value = "金融产品名称")
    @Size(max = 200)
    private String finprodName = "";

    @ApiModelProperty(required = false, name = "finprodType2", value = "金融产品类型（投管分类）：债券、理财产品、基金、理财产品模板、理财产品分层")
    @Size(max = 64)
    private String finprodType2 = "";

    @ApiModelProperty(required = false, name = "acctNo", value = "网点账号")
    @Size(max = 50)
    private String acctNo = "";

    @ApiModelProperty(required = false, name = "ccy", value = "币种")
    @Size(max = 50)
    private String ccy = "";

    @ApiModelProperty(required = false, name = "busiAcctNo", value = "资金账户代码")
    @Size(max = 30)
    private String busiAcctNo = "";

    @ApiModelProperty(required = false, name = "acctName", value = "资金账户名称")
    @Size(max = 200)
    private String acctName = "";

    @ApiModelProperty(required = false, name = "secAcctId", value = "证券账户代码")
    @Size(max = 32)
    private String secAcctId = "";

    @ApiModelProperty(required = false, name = "secAcctName", value = "证券账户名称")
    @Size(max = 200)
    private String secAcctName = "";

    @ApiModelProperty(required = false, name = "stockHolderNo", value = "股东代码")
    @Size(max = 32)
    private String stockHolderNo = "";

    @ApiModelProperty(required = false, name = "tradeMarket", value = "交易场所")
    @Size(max = 50)
    private String tradeMarket = "";

    @ApiModelProperty(required = false, name = "tradePlat", value = "交易平台")
    @Size(max = 50)
    private String tradePlat = "";

    @ApiModelProperty(required = false, name = "finprodMarketId", value = "市场代码")
    @Size(max = 20)
    private String finprodMarketId = "";

    @ApiModelProperty(required = false, name = "ps", value = "交易方向")
    @Size(max = 50)
    private String ps = "";

    @ApiModelProperty(required = false, name = "dealMode", value = "交易方式")
    @Size(max = 50)
    private String dealMode = "";

    @ApiModelProperty(required = false, name = "counterId", value = "交易对手")
    @Size(max = 32)
    private String counterId = "";

    @ApiModelProperty(required = false, name = "counterName", value = "交易对手名称")
    @Size(max = 200)
    private String counterName = "";

    @ApiModelProperty(required = false, name = "counterSeat", value = "对手席位")
    @Size(max = 10)
    private String counterSeat = "";

    @ApiModelProperty(required = false, name = "counterTrader", value = "对手方交易员")
    @Size(max = 32)
    private String counterTrader = "";

    @ApiModelProperty(required = false, name = "counterInfo", value = "对手方联系方式")
    @Size(max = 1000)
    private String counterInfo = "";

    @ApiModelProperty(required = false, name = "counterDealerId", value = "对手方交易商代码")
    @Size(max = 32)
    private String counterDealerId = "";

    @ApiModelProperty(required = false, name = "appointmentNumber", value = "约定号")
    @Size(max = 10)
    private String appointmentNumber = "";

    @ApiModelProperty(required = false, name = "dealNo", value = "成交编号")
    @Size(max = 50)
    private String dealNo = "";

    @ApiModelProperty(required = false, name = "trdMatchId", value = "初始交易编号")
    @Size(max = 50)
    private String trdMatchId = "";

    @ApiModelProperty(required = false, name = "quoterefId", value = "交易所请求号")
    @Size(max = 10)
    private String quoterefId = "";

    @ApiModelProperty(required = false, name = "tradeDate", value = "委托日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date tradeDate = new Date();

    @ApiModelProperty(required = false, name = "vdate", value = "委托开始日")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date vdate = new Date();

    @ApiModelProperty(required = false, name = "mdate", value = "委托结束日")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date mdate = new Date();

    @ApiModelProperty(required = false, name = "entrustAmt", value = "委托额度")
    @Digits(integer = 30, fraction = 2)
    @JsonSerialize(using = DoubleSerializerUtils.class)
    private BigDecimal entrustAmt = new BigDecimal("0.00");

    @ApiModelProperty(required = false, name = "entrustNum", value = "委托数量")
    @Digits(integer = 30, fraction = 4)
    private long entrustNum = 0;

    @ApiModelProperty(required = false, name = "entrustStatus", value = "委托状态")
    @Size(max = 50)
    private String entrustStatus = "";

    @ApiModelProperty(required = false, name = "entrustPrice", value = "委托单价")
    @Digits(integer = 30, fraction = 14)
    @JsonSerialize(using = DoubleSerializerUtils.class)
    private BigDecimal entrustPrice = new BigDecimal(0);

    @ApiModelProperty(required = false, name = "fullPrice", value = "全价")
    @Digits(integer = 30, fraction = 15)
    @JsonSerialize(using = DoubleSerializerUtils.class)
    private BigDecimal fullPrice = new BigDecimal(0);

    @ApiModelProperty(required = false, name = "priceMode", value = "限价模式")
    @Size(max = 50)
    private String priceMode = "";

    @ApiModelProperty(required = false, name = "entrustLeftAmt", value = "已完成数量 或者金额")
    @Digits(integer = 30, fraction = 4)
    @JsonSerialize(using = DoubleSerializerUtils.class)
    private BigDecimal entrustLeftAmt = new BigDecimal("0.00");

    @ApiModelProperty(required = false, name = "frozenQuantity", value = "冻结数量")
    @Digits(integer = 30, fraction = 4)
    @JsonSerialize(using = DoubleSerializerUtils.class)
    private BigDecimal frozenQuantity = new BigDecimal("0.00");

    @ApiModelProperty(required = false, name = "settleSpeed", value = "清算速度")
    @Digits(integer = 10, fraction = 0)
    @JsonSerialize(using = DoubleSerializerUtils.class)
    private BigDecimal settleSpeed = new BigDecimal(0);

    @ApiModelProperty(required = false, name = "valueDate", value = "起息日")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date valueDate = new Date();

    @ApiModelProperty(required = false, name = "valueDate2", value = "起息日2")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date valueDate2 = new Date();

    @ApiModelProperty(required = false, name = "maturityDate", value = "到期日")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date maturityDate = new Date();

    @ApiModelProperty(required = false, name = "maturityDate2", value = "到期日2")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date maturityDate2 = new Date();

    @ApiModelProperty(required = false, name = "termDays", value = "期限天数")
    @Digits(integer = 10, fraction = 0)
    @JsonSerialize(using = DoubleSerializerUtils.class)
    private BigDecimal termDays = new BigDecimal(0);

    @ApiModelProperty(required = false, name = "termDays2", value = "期限天数2")
    @Digits(integer = 10, fraction = 0)
    @JsonSerialize(using = DoubleSerializerUtils.class)
    private BigDecimal termDays2 = new BigDecimal(0);

    @ApiModelProperty(required = false, name = "purchType", value = "申购类型")
    @Size(max = 50)
    private String purchType = "";

    @ApiModelProperty(required = false, name = "saleCode", value = "配售代码")
    @Size(max = 50)
    private String saleCode = "";

    @ApiModelProperty(required = false, name = "minAmt", value = "最低成交金额")
    @Digits(integer = 30, fraction = 14)
    @JsonSerialize(using = DoubleSerializerUtils.class)
    private BigDecimal minAmt = new BigDecimal(0);

    @ApiModelProperty(required = false, name = "tarFinprodId", value = "目标金融产品代码")
    @Size(max = 50)
    private String tarFinprodId = "";

    @ApiModelProperty(required = false, name = "tarinprodAbbr", value = "目标金融产品简称")
    @Size(max = 50)
    private String tarinprodAbbr = "";

    @ApiModelProperty(required = false, name = "tarinprodMarketId", value = "目标金融产品市场代码")
    @Size(max = 20)
    private String tarinprodMarketId = "";

    @ApiModelProperty(required = false, name = "tradeId", value = "交易员编号")
    @Size(max = 20)
    private String tradeId = "";

    @ApiModelProperty(required = false, name = "traderName", value = "交易员姓名")
    @Size(max = 200)
    private String traderName = "";

    @ApiModelProperty(required = false, name = "invManagerId", value = "投资经理")
    @Size(max = 20)
    private String invManagerId = "";

    @ApiModelProperty(required = false, name = "assetType", value = "资产类型")
    @Size(max = 50)
    private String assetType = "";

    @ApiModelProperty(required = false, name = "assetType1", value = "资产二级分类")
    @Size(max = 50)
    private String assetType1 = "";

    @ApiModelProperty(required = false, name = "assetType10", value = "资产三级分类")
    @Size(max = 50)
    private String assetType10 = "";

    @ApiModelProperty(required = false, name = "assetType11", value = "资产四级分类")
    @Size(max = 50)
    private String assetType11 = "";

    @ApiModelProperty(required = false, name = "ourTrader", value = "本方交易员")
    @Size(max = 32)
    private String ourTrader = "";

    @ApiModelProperty(required = false, name = "invAim", value = "投资目的")
    @Size(max = 128)
    private String invAim = "";

    @ApiModelProperty(required = false, name = "tradersInfo", value = "存储指令分发时，同一条指令的对应的多个trader")
    private String tradersInfo;

    @ApiModelProperty(required = false, name = "isAutoReceive", value = "自动接收标志")
    @Size(max = 1)
    private String isAutoReceive;

    @ApiModelProperty(required = false, name = "vPayDate", value = "首期结算日")
    private Date vPayDate;

    @ApiModelProperty(required = false, name = "mPayDate", value = "到期结算日")
    private Date mPayDate;

    @ApiModelProperty(required = false, name = "capDays", value = "占款天数")
    @NotNull
    @Digits(integer = 10, fraction = 0)
    @JsonSerialize(using = DoubleSerializerUtils.class)
    private BigDecimal capDays = new BigDecimal(0);

    @ApiModelProperty(required = false, name = "mIntAmt", value = "回购利息")
    @NotNull
    @Digits(integer = 30, fraction = 14)
    @JsonSerialize(using = DoubleSerializerUtils.class)
    private BigDecimal mIntAmt = new BigDecimal(0);

    @ApiModelProperty(required = false, name = "mTradeAmt", value = "到期结算金额")
    @NotNull
    @Digits(integer = 30, fraction = 14)
    @JsonSerialize(using = DoubleSerializerUtils.class)
    private BigDecimal mTradeAmt = new BigDecimal(0);

    @ApiModelProperty(required = false, name = "firstRepoPs", value = "首期交易方向")
    @Size(max = 50)
    private String firstRepoPs;

    @ApiModelProperty(required = false, name = "remark", value = "指令备注，补充协议")
    @Size(max = 1000)
    private String remark;

    @ApiModelProperty(required = false, name = "derivativeBsDir", value = "买卖方向")
    @Size(max = 8)
    private String derivativeBsDir;

    @ApiModelProperty(required = false, name = "derivativeOcFlag", value = "开平仓标志")
    @Size(max = 8)
    private String derivativeOcFlag;

    @ApiModelProperty(required = false, name = "minPriceAndMinTrdUnitLimitBean", value = "最小价差、买卖单位及交易上下限控制")
    private MinPriceAndMinTrdUnitLimitBean minPriceAndMinTrdUnitLimitBean;

    @ApiModelProperty(required = false, name = "pledgeeType", value = "质权人类型：协议回购到期续作指令必填 0-非第三方 1-第三方")
    @Size(max = 1)
    private String pledgeeType;

    @ApiModelProperty(required = false, name = "count", value = "数量")
    private int count;

    @ApiModelProperty(required = false, name = "entrustBussType", value = "指令业务类型")
    @NotNull
    @Size(max = 50)
    private String entrustBussType;

    @ApiModelProperty(required = false, name = "deliveryMethod", value = "申赎方式，交割方式")
    @Size(max = 50)
    private String deliveryMethod;

    /**
     * 委托指令类别
     *
     * @author yflv
     * @date 2021/12/27
     */
    @ApiModelProperty(required = false, name = "entrustClass", value = "委托指令类别")
    @Size(max = 50)
    private String entrustClass;

    /**
     * 投管业务类型
     *
     * @author yflv
     * @date 2022/01/19
     */
    @ApiModelProperty(required = false, name = "busiTypeTg", value = "投管业务类型")
    @Size(max = 50)
    private String busiTypeTg;

    @ApiModelProperty(required = false, name = "createTime", value = "创建时间")
    private Date createTime;
    @ApiModelProperty(required = false, name = "updateTime", value = "修改时间")
    private Date updateTime;

    @ApiModelProperty(name = "tradeSide", value = "交易方向")
    private String tradeSide;

    @ApiModelProperty(name = "targetType", value = "目标类型")
    private String targetType;
    @ApiModelProperty(name = "isNoGuard", value = "是否非担保")
    private String isNoGuard;
    @ApiModelProperty(name = "isNetPrice", value = "是否净价交易")
    private String isNetPrice;

    @Id
    public String getEntrustId() {
        return entrustId;
    }

    public void setEntrustId(String entrustId) {
        this.entrustId = entrustId;
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

    public String getFinprodId() {
        return finprodId;
    }

    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
    }

    public String getFinprodAbbr() {
        return finprodAbbr;
    }

    public void setFinprodAbbr(String finprodAbbr) {
        this.finprodAbbr = finprodAbbr;
    }

    public String getFinprodName() {
        return finprodName;
    }

    public void setFinprodName(String finprodName) {
        this.finprodName = finprodName;
    }

    public String getFinprodType2() {
        return finprodType2;
    }

    public void setFinprodType2(String finprodType2) {
        this.finprodType2 = finprodType2;
    }

    public String getAcctNo() {
        return acctNo;
    }

    public void setAcctNo(String acctNo) {
        this.acctNo = acctNo;
    }

    public String getCcy() {
        return ccy;
    }

    public void setCcy(String ccy) {
        this.ccy = ccy;
    }

    public String getBusiAcctNo() {
        return busiAcctNo;
    }

    public void setBusiAcctNo(String busiAcctNo) {
        this.busiAcctNo = busiAcctNo;
    }

    public String getAcctName() {
        return acctName;
    }

    public void setAcctName(String acctName) {
        this.acctName = acctName;
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

    public String getStockHolderNo() {
        return stockHolderNo;
    }

    public void setStockHolderNo(String stockHolderNo) {
        this.stockHolderNo = stockHolderNo;
    }

    public String getTradeMarket() {
        return tradeMarket;
    }

    public void setTradeMarket(String tradeMarket) {
        this.tradeMarket = tradeMarket;
    }

    public String getTradePlat() {
        return tradePlat;
    }

    public void setTradePlat(String tradePlat) {
        this.tradePlat = tradePlat;
    }

    public String getFinprodMarketId() {
        return finprodMarketId;
    }

    public void setFinprodMarketId(String finprodMarketId) {
        this.finprodMarketId = finprodMarketId;
    }

    public String getPs() {
        return ps;
    }

    public void setPs(String ps) {
        this.ps = ps;
    }

    public String getDealMode() {
        return dealMode;
    }

    public void setDealMode(String dealMode) {
        this.dealMode = dealMode;
    }

    public String getCounterId() {
        return counterId;
    }

    public void setCounterId(String counterId) {
        this.counterId = counterId;
    }

    public String getCounterName() {
        return counterName;
    }

    public void setCounterName(String counterName) {
        this.counterName = counterName;
    }

    public String getCounterSeat() {
        return counterSeat;
    }

    public void setCounterSeat(String counterSeat) {
        this.counterSeat = counterSeat;
    }

    public String getCounterTrader() {
        return counterTrader;
    }

    public void setCounterTrader(String counterTrader) {
        this.counterTrader = counterTrader;
    }

    public String getCounterInfo() {
        return counterInfo;
    }

    public void setCounterInfo(String counterInfo) {
        this.counterInfo = counterInfo;
    }

    public String getCounterDealerId() {
        return counterDealerId;
    }

    public void setCounterDealerId(String counterDealerId) {
        this.counterDealerId = counterDealerId;
    }

    public String getAppointmentNumber() {
        return appointmentNumber;
    }

    public void setAppointmentNumber(String appointmentNumber) {
        this.appointmentNumber = appointmentNumber;
    }

    public String getDealNo() {
        return dealNo;
    }

    public void setDealNo(String dealNo) {
        this.dealNo = dealNo;
    }

    public String getTrdMatchId() {
        return trdMatchId;
    }

    public void setTrdMatchId(String trdMatchId) {
        this.trdMatchId = trdMatchId;
    }

    public String getQuoterefId() {
        return quoterefId;
    }

    public void setQuoterefId(String quoterefId) {
        this.quoterefId = quoterefId;
    }

    public Date getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(Date tradeDate) {
        this.tradeDate = tradeDate;
    }

    public Date getVdate() {
        return vdate;
    }

    public void setVdate(Date vdate) {
        this.vdate = vdate;
    }

    public Date getMdate() {
        return mdate;
    }

    public void setMdate(Date mdate) {
        this.mdate = mdate;
    }

    public BigDecimal getEntrustAmt() {
        return entrustAmt;
    }

    public void setEntrustAmt(BigDecimal entrustAmt) {
        this.entrustAmt = entrustAmt;
    }

    public long getEntrustNum() {
        return entrustNum;
    }

    public void setEntrustNum(long entrustNum) {
        this.entrustNum = entrustNum;
    }

    public String getEntrustStatus() {
        return entrustStatus;
    }

    public void setEntrustStatus(String entrustStatus) {
        this.entrustStatus = entrustStatus == null ? null : entrustStatus.trim();
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

    public String getPriceMode() {
        return priceMode;
    }

    public void setPriceMode(String priceMode) {
        this.priceMode = priceMode;
    }

    public BigDecimal getEntrustLeftAmt() {
        return entrustLeftAmt;
    }

    public void setEntrustLeftAmt(BigDecimal entrustLeftAmt) {
        this.entrustLeftAmt = entrustLeftAmt;
    }

    public BigDecimal getFrozenQuantity() {
        return frozenQuantity;
    }

    public void setFrozenQuantity(BigDecimal frozenQuantity) {
        this.frozenQuantity = frozenQuantity;
    }

    public BigDecimal getSettleSpeed() {
        return settleSpeed;
    }

    public void setSettleSpeed(BigDecimal settleSpeed) {
        this.settleSpeed = settleSpeed;
    }

    public Date getValueDate() {
        return valueDate;
    }

    public void setValueDate(Date valueDate) {
        this.valueDate = valueDate;
    }

    public Date getValueDate2() {
        return valueDate2;
    }

    public void setValueDate2(Date valueDate2) {
        this.valueDate2 = valueDate2;
    }

    public Date getMaturityDate() {
        return maturityDate;
    }

    public void setMaturityDate(Date maturityDate) {
        this.maturityDate = maturityDate;
    }

    public Date getMaturityDate2() {
        return maturityDate2;
    }

    public void setMaturityDate2(Date maturityDate2) {
        this.maturityDate2 = maturityDate2;
    }

    public BigDecimal getTermDays() {
        return termDays;
    }

    public void setTermDays(BigDecimal termDays) {
        this.termDays = termDays;
    }

    public BigDecimal getTermDays2() {
        return termDays2;
    }

    public void setTermDays2(BigDecimal termDays2) {
        this.termDays2 = termDays2;
    }

    public String getPurchType() {
        return purchType;
    }

    public void setPurchType(String purchType) {
        this.purchType = purchType;
    }

    public String getSaleCode() {
        return saleCode;
    }

    public void setSaleCode(String saleCode) {
        this.saleCode = saleCode;
    }

    public BigDecimal getMinAmt() {
        return minAmt;
    }

    public void setMinAmt(BigDecimal minAmt) {
        this.minAmt = minAmt;
    }

    public String getTarFinprodId() {
        return tarFinprodId;
    }

    public void setTarFinprodId(String tarFinprodId) {
        this.tarFinprodId = tarFinprodId;
    }

    public String getTarinprodAbbr() {
        return tarinprodAbbr;
    }

    public void setTarinprodAbbr(String tarinprodAbbr) {
        this.tarinprodAbbr = tarinprodAbbr;
    }

    public String getTarinprodMarketId() {
        return tarinprodMarketId;
    }

    public void setTarinprodMarketId(String tarinprodMarketId) {
        this.tarinprodMarketId = tarinprodMarketId;
    }

    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }

    public String getTraderName() {
        return traderName;
    }

    public void setTraderName(String traderName) {
        this.traderName = traderName;
    }

    public String getInvManagerId() {
        return invManagerId;
    }

    public void setInvManagerId(String invManagerId) {
        this.invManagerId = invManagerId;
    }

    public String getAssetType() {
        return assetType;
    }

    public void setAssetType(String assetType) {
        this.assetType = assetType;
    }

    public String getAssetType1() {
        return assetType1;
    }

    public void setAssetType1(String assetType1) {
        this.assetType1 = assetType1;
    }

    public String getAssetType10() {
        return assetType10;
    }

    public void setAssetType10(String assetType10) {
        this.assetType10 = assetType10;
    }

    public String getAssetType11() {
        return assetType11;
    }

    public void setAssetType11(String assetType11) {
        this.assetType11 = assetType11;
    }

    public String getInvAim() {
        return invAim;
    }

    public void setInvAim(String invAim) {
        this.invAim = invAim;
    }

    public String getTradersInfo() {
        return tradersInfo;
    }

    public void setTradersInfo(String tradersInfo) {
        this.tradersInfo = tradersInfo;
    }

    public String getConShareId() {
        return conShareId;
    }

    public void setConShareId(String conShareId) {
        this.conShareId = conShareId;
    }

    public BigDecimal getConPrice() {
        return conPrice;
    }

    public void setConPrice(BigDecimal conPrice) {
        this.conPrice = conPrice;
    }

    public BigDecimal getConNum() {
        return conNum;
    }

    public void setConNum(BigDecimal conNum) {
        this.conNum = conNum;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getApproverFlage() {
        return approverFlage;
    }

    public void setApproverFlage(String approverFlage) {
        this.approverFlage = approverFlage;
    }

    public String getOurTrader() {
        return ourTrader;
    }

    public void setOurTrader(String ourTrader) {
        this.ourTrader = ourTrader;
    }

    public String getIsAutoReceive() {
        return isAutoReceive;
    }

    public void setIsAutoReceive(String isAutoReceive) {
        this.isAutoReceive = isAutoReceive;
    }

    public Date getvPayDate() {
        return vPayDate;
    }

    public void setvPayDate(Date vPayDate) {
        this.vPayDate = vPayDate;
    }

    public Date getmPayDate() {
        return mPayDate;
    }

    public void setmPayDate(Date mPayDate) {
        this.mPayDate = mPayDate;
    }

    public BigDecimal getCapDays() {
        return capDays;
    }

    public void setCapDays(BigDecimal capDays) {
        this.capDays = capDays;
    }

    public BigDecimal getmIntAmt() {
        return mIntAmt;
    }

    public void setmIntAmt(BigDecimal mIntAmt) {
        this.mIntAmt = mIntAmt;
    }

    public BigDecimal getmTradeAmt() {
        return mTradeAmt;
    }

    public void setmTradeAmt(BigDecimal mTradeAmt) {
        this.mTradeAmt = mTradeAmt;
    }

    public String getFirstRepoPs() {
        return firstRepoPs;
    }

    public void setFirstRepoPs(String firstRepoPs) {
        this.firstRepoPs = firstRepoPs;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getDerivativeBsDir() {
        return derivativeBsDir;
    }

    public void setDerivativeBsDir(String derivativeBsDir) {
        this.derivativeBsDir = derivativeBsDir;
    }

    public String getDerivativeOcFlag() {
        return derivativeOcFlag;
    }

    public void setDerivativeOcFlag(String derivativeOcFlag) {
        this.derivativeOcFlag = derivativeOcFlag;
    }

    public String getPledgeeType() {
        return pledgeeType;
    }

    public void setPledgeeType(String pledgeeType) {
        this.pledgeeType = pledgeeType;
    }

    public MinPriceAndMinTrdUnitLimitBean getMinPriceAndMinTrdUnitLimitBean() {
        return minPriceAndMinTrdUnitLimitBean;
    }

    public void setMinPriceAndMinTrdUnitLimitBean(MinPriceAndMinTrdUnitLimitBean minPriceAndMinTrdUnitLimitBean) {
        this.minPriceAndMinTrdUnitLimitBean = minPriceAndMinTrdUnitLimitBean;
    }

    public String getEntrustBussType() {
        return entrustBussType;
    }

    public void setEntrustBussType(String entrustBussType) {
        this.entrustBussType = entrustBussType;
    }

    public String getDeliveryMethod() {
        return deliveryMethod;
    }

    public void setDeliveryMethod(String deliveryMethod) {
        this.deliveryMethod = deliveryMethod;
    }

    public String getEntrustClass() {
        return entrustClass;
    }

    public void setEntrustClass(String entrustClass) {
        this.entrustClass = entrustClass;
    }

    public String getBusiTypeTg() {
        return busiTypeTg;
    }

    public void setBusiTypeTg(String busiTypeTg) {
        this.busiTypeTg = busiTypeTg;
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

    public String getTradeSide() {
        if (DictCodeListExt3.投管业务类型_首期.getValue().equals(busiTypeTg)
                || DictCodeListExt3.投管业务类型_提前购回.getValue().equals(busiTypeTg)
                || DictCodeListExt3.投管业务类型_到期购回.getValue().equals(busiTypeTg)
                || DictCodeListExt3.投管业务类型_到期续做.getValue().equals(busiTypeTg)
                || DictCodeListExt3.投管业务类型_中途换券.getValue().equals(busiTypeTg)
                || DictCodeListExt3.投管业务类型_解除质押.getValue().equals(busiTypeTg)) {
            return firstRepoPs;
        }
        return ps;
    }

    public void setTradeSide(String tradeSide) {
        this.tradeSide = tradeSide;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Instruction that = (Instruction) o;
        return entrustNum == that.entrustNum &&
                count == that.count &&
                Objects.equals(conShareId, that.conShareId) &&
                Objects.equals(conPrice, that.conPrice) &&
                Objects.equals(conNum, that.conNum) &&
                Objects.equals(entrustId, that.entrustId) &&
                Objects.equals(portfolioId, that.portfolioId) &&
                Objects.equals(portfolioName, that.portfolioName) &&
                Objects.equals(finprodId, that.finprodId) &&
                Objects.equals(finprodAbbr, that.finprodAbbr) &&
                Objects.equals(finprodName, that.finprodName) &&
                Objects.equals(finprodType2, that.finprodType2) &&
                Objects.equals(acctNo, that.acctNo) &&
                Objects.equals(ccy, that.ccy) &&
                Objects.equals(busiAcctNo, that.busiAcctNo) &&
                Objects.equals(acctName, that.acctName) &&
                Objects.equals(secAcctId, that.secAcctId) &&
                Objects.equals(secAcctName, that.secAcctName) &&
                Objects.equals(stockHolderNo, that.stockHolderNo) &&
                Objects.equals(tradeMarket, that.tradeMarket) &&
                Objects.equals(tradePlat, that.tradePlat) &&
                Objects.equals(finprodMarketId, that.finprodMarketId) &&
                Objects.equals(ps, that.ps) &&
                Objects.equals(dealMode, that.dealMode) &&
                Objects.equals(counterId, that.counterId) &&
                Objects.equals(counterSeat, that.counterSeat) &&
                Objects.equals(counterTrader, that.counterTrader) &&
                Objects.equals(counterInfo, that.counterInfo) &&
                Objects.equals(counterDealerId, that.counterDealerId) &&
                Objects.equals(appointmentNumber, that.appointmentNumber) &&
                Objects.equals(dealNo, that.dealNo) &&
                Objects.equals(trdMatchId, that.trdMatchId) &&
                Objects.equals(quoterefId, that.quoterefId) &&
                Objects.equals(tradeDate, that.tradeDate) &&
                Objects.equals(vdate, that.vdate) &&
                Objects.equals(mdate, that.mdate) &&
                Objects.equals(entrustAmt, that.entrustAmt) &&
                Objects.equals(entrustStatus, that.entrustStatus) &&
                Objects.equals(entrustPrice, that.entrustPrice) &&
                Objects.equals(fullPrice, that.fullPrice) &&
                Objects.equals(priceMode, that.priceMode) &&
                Objects.equals(entrustLeftAmt, that.entrustLeftAmt) &&
                Objects.equals(frozenQuantity, that.frozenQuantity) &&
                Objects.equals(settleSpeed, that.settleSpeed) &&
                Objects.equals(valueDate, that.valueDate) &&
                Objects.equals(valueDate2, that.valueDate2) &&
                Objects.equals(maturityDate, that.maturityDate) &&
                Objects.equals(maturityDate2, that.maturityDate2) &&
                Objects.equals(termDays, that.termDays) &&
                Objects.equals(termDays2, that.termDays2) &&
                Objects.equals(purchType, that.purchType) &&
                Objects.equals(saleCode, that.saleCode) &&
                Objects.equals(minAmt, that.minAmt) &&
                Objects.equals(tarFinprodId, that.tarFinprodId) &&
                Objects.equals(tarinprodAbbr, that.tarinprodAbbr) &&
                Objects.equals(tarinprodMarketId, that.tarinprodMarketId) &&
                Objects.equals(tradeId, that.tradeId) &&
                Objects.equals(traderName, that.traderName) &&
                Objects.equals(invManagerId, that.invManagerId) &&
                Objects.equals(assetType, that.assetType) &&
                Objects.equals(assetType1, that.assetType1) &&
                Objects.equals(assetType10, that.assetType10) &&
                Objects.equals(assetType11, that.assetType11) &&
                Objects.equals(invAim, that.invAim) &&
                Objects.equals(tradersInfo, that.tradersInfo) &&
                Objects.equals(isAutoReceive, that.isAutoReceive) &&
                Objects.equals(vPayDate, that.vPayDate) &&
                Objects.equals(mPayDate, that.mPayDate) &&
                Objects.equals(capDays, that.capDays) &&
                Objects.equals(mIntAmt, that.mIntAmt) &&
                Objects.equals(mTradeAmt, that.mTradeAmt) &&
                Objects.equals(firstRepoPs, that.firstRepoPs) &&
                Objects.equals(entrustBussType, that.entrustBussType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(conShareId, conPrice, conNum, entrustId, portfolioId, portfolioName, finprodId, finprodAbbr, finprodName, finprodType2, acctNo, ccy, busiAcctNo,
                acctName, secAcctId, secAcctName, stockHolderNo, tradeMarket, tradePlat, finprodMarketId, ps, dealMode, counterId, counterSeat, counterTrader, counterInfo,
                counterDealerId, appointmentNumber, dealNo, trdMatchId, quoterefId, tradeDate, vdate, mdate, entrustAmt, entrustNum, entrustStatus, entrustPrice, fullPrice,
                priceMode, entrustLeftAmt, frozenQuantity, settleSpeed, valueDate, valueDate2, maturityDate, maturityDate2, termDays, termDays2, purchType, saleCode, minAmt,
                tarFinprodId, tarinprodAbbr, tarinprodMarketId, tradeId, traderName, invManagerId, assetType, assetType1, assetType10, assetType11, invAim, tradersInfo,
                isAutoReceive, vPayDate, mPayDate, mIntAmt, mTradeAmt, firstRepoPs, count, entrustBussType);
    }

    @Override
    public String getId() {
        return entrustId;
    }

    @Override
    public void setId(String id) {
        entrustId = id;
    }

    public Instruction cloneInstruction() {
        try {
            return (Instruction) this.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    @Override
    public String toString() {
        return JSONUtil.toJsonStr(this);
    }
}
