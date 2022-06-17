package com.joyintech.tams.exchange.common.db.domain;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.tams.exchange.common.bean.instrument.MinPriceAndMinTrdUnitLimitBean;
import com.joyintech.tams.exchange.common.constant.joyin.DictCodeList;
import com.joyintech.tams.exchange.common.constant.InvestmentAssetType;
import com.joyintech.tams.exchange.common.constant.TradePlat;
import com.joyintech.tams.exchange.common.constant.instrument.SecondAssetType;
import com.joyintech.tams.exchange.common.enums.exchange.TargetTypeEnum;
import com.joyintech.tams.exchange.common.constant.joyin.DictCodeListExt3;
import com.joyintech.tams.exchange.common.utils.DoubleSerializerUtils;
import com.joyintech.tams.exchange.common.utils.TradeUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.data.annotation.Id;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@ApiModel(value = "指令导出信息", description = "指令导出信息")
public class InstructionExportVo {
    @ApiModelProperty(name = "approverFlage", value = "审批标志")
    @Size(max = 50)
    private String approverFlage;

    @ApiModelProperty(name = "conShareId", value = "转股代码")
    @Size(max = 50)
    private String conShareId;

    @ApiModelProperty(name = "conPrice", value = "转股价格")
    @Digits(integer = 30, fraction = 14)
    @JsonSerialize(using = DoubleSerializerUtils.class)
    private BigDecimal conPrice = new BigDecimal("0.0000");

    @ApiModelProperty(name = "conNum", value = "转股数量")
    @Digits(integer = 30, fraction = 4)
    @JsonSerialize(using = DoubleSerializerUtils.class)
    private BigDecimal conNum = new BigDecimal("0");

    @ApiModelProperty(name = "entrustId", value = "指令编号")
    @NotNull
    @Size(max = 32)
    @Id
    private String entrustId = "";

    @ApiModelProperty(name = "portfolioId", value = "组合代码")
    @Size(max = 200)
    private String portfolioId = "";

    @ApiModelProperty(name = "portfolioName", value = "组合名称")
    @Size(max = 200)
    private String portfolioName = "";

    @ApiModelProperty(name = "finprodId", value = "金融产品代码")
    @Size(max = 50)
    private String finprodId = "";

    @ApiModelProperty(name = "finprodAbbr", value = "金融产品简称")
    @Size(max = 200)
    private String finprodAbbr = "";

    @ApiModelProperty(name = "finprodName", value = "金融产品名称")
    @Size(max = 200)
    private String finprodName = "";

    @ApiModelProperty(name = "finprodType2", value = "金融产品类型（投管分类）：债券、理财产品、基金、理财产品模板、理财产品分层")
    @Size(max = 64)
    private String finprodType2 = "";

    @ApiModelProperty(name = "acctNo", value = "网点账号")
    @Size(max = 50)
    private String acctNo = "";

    @ApiModelProperty(name = "ccy", value = "币种")
    @Size(max = 50)
    private String ccy = "";

    @ApiModelProperty(name = "busiAcctNo", value = "资金账号")
    @Size(max = 30)
    private String busiAcctNo = "";

    @ApiModelProperty(name = "acctName", value = "户名")
    @Size(max = 200)
    private String acctName = "";

    @ApiModelProperty(name = "secAcctId", value = "证券账户代码")
    @Size(max = 32)
    private String secAcctId = "";

    @ApiModelProperty(name = "secAcctName", value = "证券账户名称")
    @Size(max = 200)
    private String secAcctName = "";

    @ApiModelProperty(name = "stockHolderNo", value = "股东代码")
    @Size(max = 32)
    private String stockHolderNo = "";

    @ApiModelProperty(name = "tradeMarket", value = "交易场所")
    @Size(max = 50)
    private String tradeMarket = "";

    @ApiModelProperty(name = "tradePlat", value = "交易平台")
    @Size(max = 50)
    private String tradePlat = "";

    @ApiModelProperty(name = "finprodMarketId", value = "市场代码")
    @Size(max = 20)
    private String finprodMarketId = "";

    @ApiModelProperty(name = "ps", value = "交易方向")
    @Size(max = 50)
    private String ps = "";

    @ApiModelProperty(name = "tradeSide", value = "交易方向")
    private String tradeSide;

    @ApiModelProperty(name = "dealMode", value = "交易方式")
    @Size(max = 50)
    private String dealMode = "";

    @ApiModelProperty(name = "counterId", value = "交易对手")
    @Size(max = 32)
    private String counterId = "";

    @ApiModelProperty(name = "counterName", value = "交易对手名称")
    @Size(max = 200)
    private String counterName = "";

    @ApiModelProperty(name = "counterSeat", value = "对手席位")
    @Size(max = 10)
    private String counterSeat = "";

    @ApiModelProperty(name = "counterTrader", value = "对手方交易员")
    @Size(max = 32)
    private String counterTrader = "";

    @ApiModelProperty(name = "counterInfo", value = "对手方联系方式")
    @Size(max = 1000)
    private String counterInfo = "";

    @ApiModelProperty(name = "counterDealerId", value = "对手方交易商代码")
    @Size(max = 32)
    private String counterDealerId = "";

    @ApiModelProperty(name = "counterInvestorId", value = "对手方交易主体代码")
    @Size(max = 32)
    private String counterInvestorId = "";

    @ApiModelProperty(name = "counterInvestorType", value = "对手方交易主体类型")
    @Size(max = 32)
    private String counterInvestorType = "";

    @ApiModelProperty(name = "appointmentNumber", value = "约定号")
    @Size(max = 10)
    private String appointmentNumber = "";

    @ApiModelProperty(name = "dealNo", value = "成交编号")
    @Size(max = 50)
    private String dealNo = "";

    @ApiModelProperty(name = "trdMatchId", value = "初始交易编号")
    @Size(max = 50)
    private String trdMatchId = "";

    @ApiModelProperty(name = "quoterefId", value = "交易所请求号")
    @Size(max = 10)
    private String quoterefId = "";

    @ApiModelProperty(name = "tradeDate", value = "委托日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date tradeDate = new Date();

    @ApiModelProperty(name = "vdate", value = "委托开始日")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date vdate = new Date();

    @ApiModelProperty(name = "mdate", value = "委托结束日")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date mdate = new Date();

    @ApiModelProperty(name = "entrustAmt", value = "委托额度")
    @Digits(integer = 30, fraction = 2)
    @JsonSerialize(using = DoubleSerializerUtils.class)
    private BigDecimal entrustAmt = new BigDecimal("0.00");

    @ApiModelProperty(name = "entrustNum", value = "委托数量")
    @Digits(integer = 30, fraction = 4)
    private long entrustNum = 0;

    @ApiModelProperty(name = "entrustStatus", value = "委托状态")
    @Size(max = 50)
    private String entrustStatus = "";

    @ApiModelProperty(name = "entrustPrice", value = "委托单价")
    @Digits(integer = 30, fraction = 14)
    @JsonSerialize(using = DoubleSerializerUtils.class)
    private BigDecimal entrustPrice = new BigDecimal(0);

    @ApiModelProperty(name = "fullPrice", value = "全价")
    @Digits(integer = 30, fraction = 15)
    @JsonSerialize(using = DoubleSerializerUtils.class)
    private BigDecimal fullPrice = new BigDecimal(0);

    @ApiModelProperty(name = "priceMode", value = "限价模式")
    @Size(max = 50)
    private String priceMode = "";

    @ApiModelProperty(name = "entrustLeftAmt", value = "已完成数量 或者金额")
    @Digits(integer = 30, fraction = 4)
    @JsonSerialize(using = DoubleSerializerUtils.class)
    private BigDecimal entrustLeftAmt = new BigDecimal("0.00");

    @ApiModelProperty(name = "frozenQuantity", value = "冻结数量")
    @Digits(integer = 30, fraction = 4)
    @JsonSerialize(using = DoubleSerializerUtils.class)
    private BigDecimal frozenQuantity = new BigDecimal("0.00");

    @ApiModelProperty(name = "settleSpeed", value = "清算速度")
    @Digits(integer = 10, fraction = 0)
    @JsonSerialize(using = DoubleSerializerUtils.class)
    private BigDecimal settleSpeed = new BigDecimal(0);

    @ApiModelProperty(name = "valueDate", value = "起息日")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date valueDate = new Date();

    @ApiModelProperty(name = "valueDate2", value = "起息日2")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date valueDate2 = new Date();

    @ApiModelProperty(name = "maturityDate", value = "到期日")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date maturityDate = new Date();

    @ApiModelProperty(name = "maturityDate2", value = "到期日2")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date maturityDate2 = new Date();

    @ApiModelProperty(name = "termDays", value = "期限天数")
    @Digits(integer = 10, fraction = 0)
    @JsonSerialize(using = DoubleSerializerUtils.class)
    private BigDecimal termDays = new BigDecimal(0);

    @ApiModelProperty(name = "termDays2", value = "期限天数2")
    @Digits(integer = 10, fraction = 0)
    @JsonSerialize(using = DoubleSerializerUtils.class)
    private BigDecimal termDays2 = new BigDecimal(0);

    @ApiModelProperty(name = "purchType", value = "申购类型")
    @Size(max = 50)
    private String purchType = "";

    @ApiModelProperty(name = "saleCode", value = "配售代码")
    @Size(max = 50)
    private String saleCode = "";

    @ApiModelProperty(name = "minAmt", value = "最低成交金额")
    @Digits(integer = 30, fraction = 14)
    @JsonSerialize(using = DoubleSerializerUtils.class)
    private BigDecimal minAmt = new BigDecimal(0);

    @ApiModelProperty(name = "tarFinprodId", value = "目标金融产品代码")
    @Size(max = 50)
    private String tarFinprodId = "";

    @ApiModelProperty(name = "tarinprodAbbr", value = "目标金融产品简称")
    @Size(max = 50)
    private String tarinprodAbbr = "";

    @ApiModelProperty(name = "tarinprodMarketId", value = "目标金融产品市场代码")
    @Size(max = 20)
    private String tarinprodMarketId = "";

    @ApiModelProperty(name = "tradeId", value = "交易员编号")
    @Size(max = 20)
    private String tradeId = "";

    @ApiModelProperty(name = "traderName", value = "交易员姓名")
    @Size(max = 200)
    private String traderName = "";

    @ApiModelProperty(name = "invManagerId", value = "投资经理")
    @Size(max = 20)
    private String invManagerId = "";

    @ApiModelProperty(name = "assetType", value = "资产类型")
    @Size(max = 50)
    private String assetType = "";

    @ApiModelProperty(name = "assetType1", value = "资产二级分类")
    @Size(max = 50)
    private String assetType1 = "";

    @ApiModelProperty(name = "assetType10", value = "资产三级分类")
    @Size(max = 50)
    private String assetType10 = "";

    @ApiModelProperty(name = "assetType11", value = "资产四级分类")
    @Size(max = 50)
    private String assetType11 = "";

    @ApiModelProperty(name = "ourTrader", value = "本方交易员")
    @Size(max = 32)
    private String ourTrader = "";

    @ApiModelProperty(name = "invAim", value = "投资目的")
    @Size(max = 128)
    private String invAim = "";

    @ApiModelProperty(name = "invAim", value = "存储指令分发时，同一条指令的对应的多个trader")
    private String tradersInfo;

    @ApiModelProperty(name = "isAutoReceive", value = "自动接收标志")
    @Size(max = 1)
    private String isAutoReceive;

    @ApiModelProperty(name = "vPayDate", value = "首期结算日")
    private Date vPayDate;

    @ApiModelProperty(name = "mPayDate", value = "到期结算日")
    private Date mPayDate;

    @ApiModelProperty(name = "capDays", value = "占款天数")
    @NotNull
    @Digits(integer = 10, fraction = 0)
    @JsonSerialize(using = DoubleSerializerUtils.class)
    private BigDecimal capDays = new BigDecimal(0);

    @ApiModelProperty(name = "mIntAmt", value = "回购利息")
    @NotNull
    @Digits(integer = 30, fraction = 14)
    @JsonSerialize(using = DoubleSerializerUtils.class)
    private BigDecimal mIntAmt = new BigDecimal(0);

    @ApiModelProperty(name = "mTradeAmt", value = "到期结算金额")
    @NotNull
    @Digits(integer = 30, fraction = 14)
    @JsonSerialize(using = DoubleSerializerUtils.class)
    private BigDecimal mTradeAmt = new BigDecimal(0);

    @ApiModelProperty(name = "firstRepoPs", value = "首期交易方向")
    @Size(max = 50)
    private String firstRepoPs;

    @ApiModelProperty(name = "remark", value = "指令备注，补充协议")
    @Size(max = 1000)
    private String remark;

    @ApiModelProperty(name = "derivativeBsDir", value = "买卖方向")
    @Size(max = 8)
    private String derivativeBsDir;

    @ApiModelProperty(name = "derivativeOcFlag", value = "开平仓标志")
    @Size(max = 8)
    private String derivativeOcFlag;

    @ApiModelProperty(name = "pledgeeType", value = "质权人类型：协议回购到期续作指令必填 0-非第三方 1-第三方")
    @Size(max = 1)
    private String pledgeeType;

    @ApiModelProperty(name = "count", value = "数量")
    private int count;

    @ApiModelProperty(name = "entrustBussType", value = "指令业务类型")
    @NotNull
    @Size(max = 50)
    private String entrustBussType;

    @ApiModelProperty(name = "deliveryMethod", value = "申赎方式，交割方式")
    @Size(max = 50)
    private String deliveryMethod;

    @ApiModelProperty(name = "entrustClass", value = "委托指令类别")
    @Size(max = 50)
    private String entrustClass;

    @ApiModelProperty(name = "busiTypeTg", value = "投管业务类型")
    @Size(max = 50)
    private String busiTypeTg;

    @ApiModelProperty(name = "progress", value = "执行进度")
    private String progress;

    @ApiModelProperty(name = "dealProgress", value = "成交进度")
    private String dealProgress;

    @ApiModelProperty(name = "leftVolume", value = "剩余数量")
    private long leftVolume;

    @ApiModelProperty(name = "leftVolume", value = "剩余金额")
    private BigDecimal leftAmount;

    @ApiModelProperty(name = "targetType", value = "目标类型")
    private String targetType;
    @ApiModelProperty(name = "isNoGuard", value = "是否非担保")
    private String isNoGuard;
    @ApiModelProperty(name = "isNetPrice", value = "是否净价交易")
    private String isNetPrice;

    @ApiModelProperty(name = "createTime", value = "创建时间")
    private Date createTime;
    @ApiModelProperty(name = "updateTime", value = "修改时间")
    private Date updateTime;

    @ApiModelProperty(name = "entrustNumView", value = "指令数量(展示)")
    private BigDecimal entrustNumView;

    @ApiModelProperty(name = "leftVolumeView", value = "剩余数量(展示)")
    private BigDecimal leftVolumeView;

    @ApiModelProperty(name = "entrustPriceView", value = "指令价格(展示)")
    private BigDecimal entrustPriceView;

    @ApiModelProperty(name = "quoteRequestId", value = "行情请求编号")
    private String quoteRequestId;

    @ApiModelProperty(name = "minPriceAndMinTrdUnitLimitBean", value = "最小价差、买卖单位及交易上下限控制")
    private MinPriceAndMinTrdUnitLimitBean minPriceAndMinTrdUnitLimitBean;

    @ApiModelProperty(name = "pledges", value = "质押券信息")
    public List<InstructionPledge> pledges;

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

    public String getCounterInvestorId() {
        return counterInvestorId;
    }

    public void setCounterInvestorId(String counterInvestorId) {
        this.counterInvestorId = counterInvestorId;
    }

    public String getCounterInvestorType() {
        return counterInvestorType;
    }

    public void setCounterInvestorType(String counterInvestorType) {
        this.counterInvestorType = counterInvestorType;
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

    public BigDecimal getEntrustNumView() {
        return entrustNumView;
    }

    public void setEntrustNumView(BigDecimal entrustNumView) {
        this.entrustNumView = entrustNumView;
    }

    public BigDecimal getLeftVolumeView() {
        return leftVolumeView;
    }

    public void setLeftVolumeView(BigDecimal leftVolumeView) {
        this.leftVolumeView = leftVolumeView;
    }

    public MinPriceAndMinTrdUnitLimitBean getMinPriceAndMinTrdUnitLimitBean() {
        return minPriceAndMinTrdUnitLimitBean;
    }

    public void setMinPriceAndMinTrdUnitLimitBean(MinPriceAndMinTrdUnitLimitBean minPriceAndMinTrdUnitLimitBean) {
        this.minPriceAndMinTrdUnitLimitBean = minPriceAndMinTrdUnitLimitBean;
    }

    /**
     * 执行进度
     */
    public String getProgress() {
        if (StrUtil.isNotEmpty(this.progress)) {
            return this.progress;
        }
        return TradeUtils.percentFormat(this.calcPercent(this.executeAmt()));
    }

    public void setProgress(String progress) {
        this.progress = progress;
    }

    /**
     * 成交进度
     */
    public String getDealProgress() {

        if (StrUtil.isNotEmpty(this.dealProgress)) {
            return this.dealProgress;
        }
        return TradeUtils.percentFormat(this.calcPercent(this.getEntrustLeftAmt()));
    }

    public void setDealProgress(String dealProgress) {
        this.dealProgress = dealProgress;
    }

    private BigDecimal calcPercent(BigDecimal amt) {
        if (this.getEntrustNum() == 0L && BigDecimal.ZERO.compareTo(this.getEntrustAmt()) == 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal percent = this.volumeInstruction() ? NumberUtil.div(amt, this.getEntrustNum())
                : NumberUtil.div(amt, this.getEntrustAmt());
        return NumberUtil.round(NumberUtil.min(BigDecimal.ONE, percent), 4);
    }

    /**
     * 剩余数量
     */
    public Long getLeftVolume() {
        if (Objects.nonNull(this.leftVolume)) {
            return this.leftVolume;
        }

        if (volumeInstruction()) {
            return getEntrustNum() - getEntrustLeftAmt().longValue() - getFrozenQuantity().longValue();
        }
        return 0L;
    }

    public void setLeftVolume(Long leftVolume) {
        this.leftVolume = leftVolume;
    }

    /**
     * 剩余金额
     */
    public BigDecimal getLeftAmount() {
        if (Objects.nonNull(this.leftAmount)) {
            return this.leftAmount;
        }

        if (TradeUtils.bigThanZero(getEntrustAmt())) {
            return NumberUtil.round(NumberUtil.sub(getEntrustAmt(), getEntrustLeftAmt(), getFrozenQuantity()), 2);
        }
        return BigDecimal.ZERO;
    }

    public void setLeftAmount(BigDecimal leftAmount) {
        this.leftAmount = leftAmount;
    }


    /**
     * 新债-非可转债
     */
    public boolean newBond() {
        return InvestmentAssetType.BOND.codeEquals(this.getAssetType())
                && SecondAssetType.KZZ.codeNotEquals(this.getAssetType1())
                && TradePlat.NEW_BOND.codeEquals(this.getTradePlat());
    }

    /**
     * 是否是数量指令
     */
    public boolean volumeInstruction() {
        return this.getEntrustNum() > 0L;
    }

    /**
     * 是否是金额指令
     */
    public boolean amtInstruction() {
        return !volumeInstruction();
    }

    /**
     * 已执行数量/金额
     */
    public BigDecimal executeAmt() {
        return NumberUtil.add(this.getEntrustLeftAmt(), this.getFrozenQuantity());
    }

    public BigDecimal getEntrustPriceView() {
        return entrustPriceView;
    }

    public void setEntrustPriceView(BigDecimal entrustPriceView) {
        this.entrustPriceView = entrustPriceView;
    }

    public String getQuoteRequestId() {
        return quoteRequestId;
    }

    public void setQuoteRequestId(String quoteRequestId) {
        this.quoteRequestId = quoteRequestId;
    }

    public List<InstructionPledge> getPledges() {
        return pledges;
    }

    public void setPledges(List<InstructionPledge> pledges) {
        this.pledges = pledges;
    }

    public void setLeftVolume(long leftVolume) {
        this.leftVolume = leftVolume;
    }
}
