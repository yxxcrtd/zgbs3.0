package com.joyintech.tams.exchange.common.bean.response.quote.repurchase;


import com.google.common.base.MoreObjects;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

@ApiModel(value = "我收到的协议回购行情返回报文", description = "我收到的协议回购行情返回报文")
public class RepurchaseQuoteReceiveResponse {

    @ApiModelProperty(name = "requestNo", value = "请求编号")
    private String requestNo;

    @ApiModelProperty(name = "requestType", value = "对手方请求类型")
    private String requestType;

    @ApiModelProperty(name = "bs", value = "对手方委托方向")
    private String bs;

    @ApiModelProperty(name = "tradeMarket", value = "交易场所")
    private String tradeMarket;

    @ApiModelProperty(name = "stockCode", value = "质押券代码（中途换券此字段为空）")
    private String stockCode;

    @ApiModelProperty(name = "stockName", value = "质押券名称（中途换券此字段为空）")
    private String stockName;

    @ApiModelProperty(name = "pledgeNumber", value = "质押数量")
    private BigDecimal pledgeNumber;

    @ApiModelProperty(name = "repoAmount", value = "回购金额")
    private BigDecimal repoAmount;

    @ApiModelProperty(name = "repoRate", value = "回购利率")
    private BigDecimal repoRate;

    @ApiModelProperty(name = "discount", value = "折算率")
    private BigDecimal discount;

    @ApiModelProperty(name = "firstSettleDate", value = "首次结算日")
    private String firstSettleDate;

    @ApiModelProperty(name = "compactTerm", value = "回购期限")
    private Integer compactTerm;

    @ApiModelProperty(name = "backDate", value = "到期结算日")
    private String backDate;

    @ApiModelProperty(name = "repoMdate", value = "回购到期日")
    private String repoMdate;

    @ApiModelProperty(name = "capDays", value = "占款天数")
    private Integer capDays;

    @ApiModelProperty(name = "swapIntoBonds", value = "换入质押券代码")
    private String swapIntoBonds;

    @ApiModelProperty(name = "swapIntoBondsName", value = "换入质押券名称")
    private String swapIntoBondsName;

    @ApiModelProperty(name = "swapIntoBondsVolume", value = "换入质押券数量")
    private BigDecimal swapIntoBondsVolume;

    @ApiModelProperty(name = "swapOutBonds", value = "换出质押券代码")
    private String swapOutBonds;

    @ApiModelProperty(name = "swapOutBondsName", value = "换出质押券名称")
    private String swapOutBondsName;

    @ApiModelProperty(name = "swapOutBondsVolume", value = "换出质押券数量")
    private BigDecimal swapOutBondsVolume;

    @ApiModelProperty(name = "origBonds", value = "原质押券代码（中途换券展示）")
    private String origBonds;

    @ApiModelProperty(name = "origBondsName", value = "原质押券名称（中途换券展示）")
    private String origBondsName;

    @ApiModelProperty(name = "origBondsVolume", value = "原质押券数量")
    private BigDecimal origBondsVolume;

    @ApiModelProperty(name = "origRepoAmount", value = "原回购金额")
    private BigDecimal origRepoAmount;

    @ApiModelProperty(name = "transCode", value = "到期续作类型（0-非第三方，1-第三方）")
    private String transCode;

    @ApiModelProperty(name = "mIntAmt", value = "回购利息")
    private BigDecimal mIntAmt;

    @ApiModelProperty(name = "backBalance", value = "到期结算金额")
    private BigDecimal backBalance;

    @ApiModelProperty(name = "traderCode", value = "本方交易员")
    private String traderCode;
    @ApiModelProperty(name = "memberId", value = "本方交易商")
    private String memberId;
    @ApiModelProperty(name = "investorId", value = "本方交易主体（深交所展示，否则为空）")
    private String investorId;
    @ApiModelProperty(name = "investorType", value = "本方交易主体类型（深交所展示，否则为空）")
    private String investorType;

    @ApiModelProperty(name = "cpTraderCode", value = "对手方交易员")
    private String cpTraderCode;
    @ApiModelProperty(name = "cpMemberId", value = "对手方交易商")
    private String cpMemberId;
    @ApiModelProperty(name = "cpInvestorId", value = "对手方交易主体（深交所展示，否则为空）")
    private String cpInvestorId;
    @ApiModelProperty(name = "cpInvestorType", value = "对手方交易主体类型（深交所展示，否则为空）")
    private String cpInvestorType;
    @ApiModelProperty(name = "cpClientName", value = "对手方客户名称")
    private String cpClientName;

    @ApiModelProperty(name = "origSerialNo", value = "初始交易编号")
    private String origSerialNo;

    @ApiModelProperty(name = "remark", value = "补充协议")
    private String remark;

    @ApiModelProperty(name = "account", value = "本方资金账号")
    private String account;

    @ApiModelProperty(name = "accountName", value = "本方资金账号名称")
    private String accountName;

    @ApiModelProperty(name = "requestTypeStr", value = "对手方请求类型（首期、提前购回、中途换券、到期续做、解除质押、到期购回）")
    private String requestTypeStr;
    @ApiModelProperty(name = "bsStr", value = "对手方委托方向（1：正回购 2：逆回购）")
    private String bsStr;
    @ApiModelProperty(name = "tradeMarket", value = "交易场所（上海证券交易所、深圳证券交易所）")
    private String tradeMarketStr;
    @ApiModelProperty(name = "pledgeNumberStr", value = "质押数量（单位：万元，保留两位小数）")
    private String pledgeNumberStr;

    @ApiModelProperty(name = "investorTypeStr", value = "本方交易主体类型（1.自营 2.资管 3.机构经纪 4.个人经纪）")
    private String investorTypeStr;
    @ApiModelProperty(name = "cpInvestorTypeStr", value = "对手方交易主体类型（1.自营 2.资管 3.机构经纪 4.个人经纪）")
    private String cpInvestorTypeStr;

    public String getRequestNo() {
        return requestNo;
    }

    public void setRequestNo(String requestNo) {
        this.requestNo = requestNo;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public String getBs() {
        return bs;
    }

    public void setBs(String bs) {
        this.bs = bs;
    }

    public String getTradeMarket() {
        return tradeMarket;
    }

    public void setTradeMarket(String tradeMarket) {
        this.tradeMarket = tradeMarket;
    }

    public String getStockCode() {
        return stockCode;
    }

    public void setStockCode(String stockCode) {
        this.stockCode = stockCode;
    }

    public String getStockName() {
        return stockName;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

    public BigDecimal getPledgeNumber() {
        return pledgeNumber;
    }

    public void setPledgeNumber(BigDecimal pledgeNumber) {
        this.pledgeNumber = pledgeNumber;
    }

    public BigDecimal getRepoAmount() {
        return repoAmount;
    }

    public void setRepoAmount(BigDecimal repoAmount) {
        this.repoAmount = repoAmount;
    }

    public BigDecimal getRepoRate() {
        return repoRate;
    }

    public void setRepoRate(BigDecimal repoRate) {
        this.repoRate = repoRate;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public String getFirstSettleDate() {
        return firstSettleDate;
    }

    public void setFirstSettleDate(String firstSettleDate) {
        this.firstSettleDate = firstSettleDate;
    }

    public Integer getCompactTerm() {
        return compactTerm;
    }

    public void setCompactTerm(Integer compactTerm) {
        this.compactTerm = compactTerm;
    }

    public String getBackDate() {
        return backDate;
    }

    public void setBackDate(String backDate) {
        this.backDate = backDate;
    }

    public String getRepoMdate() {
        return repoMdate;
    }

    public void setRepoMdate(String repoMdate) {
        this.repoMdate = repoMdate;
    }

    public Integer getCapDays() {
        return capDays;
    }

    public void setCapDays(Integer capDays) {
        this.capDays = capDays;
    }

    public String getSwapIntoBonds() {
        return swapIntoBonds;
    }

    public void setSwapIntoBonds(String swapIntoBonds) {
        this.swapIntoBonds = swapIntoBonds;
    }

    public String getSwapIntoBondsName() {
        return swapIntoBondsName;
    }

    public void setSwapIntoBondsName(String swapIntoBondsName) {
        this.swapIntoBondsName = swapIntoBondsName;
    }

    public BigDecimal getSwapIntoBondsVolume() {
        return swapIntoBondsVolume;
    }

    public void setSwapIntoBondsVolume(BigDecimal swapIntoBondsVolume) {
        this.swapIntoBondsVolume = swapIntoBondsVolume;
    }

    public String getSwapOutBonds() {
        return swapOutBonds;
    }

    public void setSwapOutBonds(String swapOutBonds) {
        this.swapOutBonds = swapOutBonds;
    }

    public String getSwapOutBondsName() {
        return swapOutBondsName;
    }

    public void setSwapOutBondsName(String swapOutBondsName) {
        this.swapOutBondsName = swapOutBondsName;
    }

    public BigDecimal getSwapOutBondsVolume() {
        return swapOutBondsVolume;
    }

    public void setSwapOutBondsVolume(BigDecimal swapOutBondsVolume) {
        this.swapOutBondsVolume = swapOutBondsVolume;
    }

    public String getOrigBonds() {
        return origBonds;
    }

    public void setOrigBonds(String origBonds) {
        this.origBonds = origBonds;
    }

    public String getOrigBondsName() {
        return origBondsName;
    }

    public void setOrigBondsName(String origBondsName) {
        this.origBondsName = origBondsName;
    }

    public BigDecimal getOrigBondsVolume() {
        return origBondsVolume;
    }

    public void setOrigBondsVolume(BigDecimal origBondsVolume) {
        this.origBondsVolume = origBondsVolume;
    }

    public BigDecimal getOrigRepoAmount() {
        return origRepoAmount;
    }

    public void setOrigRepoAmount(BigDecimal origRepoAmount) {
        this.origRepoAmount = origRepoAmount;
    }

    public String getTransCode() {
        return transCode;
    }

    public void setTransCode(String transCode) {
        this.transCode = transCode;
    }

    public BigDecimal getmIntAmt() {
        return mIntAmt;
    }

    public void setmIntAmt(BigDecimal mIntAmt) {
        this.mIntAmt = mIntAmt;
    }

    public BigDecimal getBackBalance() {
        return backBalance;
    }

    public void setBackBalance(BigDecimal backBalance) {
        this.backBalance = backBalance;
    }

    public String getTraderCode() {
        return traderCode;
    }

    public void setTraderCode(String traderCode) {
        this.traderCode = traderCode;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getInvestorId() {
        return investorId;
    }

    public void setInvestorId(String investorId) {
        this.investorId = investorId;
    }

    public String getInvestorType() {
        return investorType;
    }

    public void setInvestorType(String investorType) {
        this.investorType = investorType;
    }

    public String getCpTraderCode() {
        return cpTraderCode;
    }

    public void setCpTraderCode(String cpTraderCode) {
        this.cpTraderCode = cpTraderCode;
    }

    public String getCpMemberId() {
        return cpMemberId;
    }

    public void setCpMemberId(String cpMemberId) {
        this.cpMemberId = cpMemberId;
    }

    public String getCpInvestorId() {
        return cpInvestorId;
    }

    public void setCpInvestorId(String cpInvestorId) {
        this.cpInvestorId = cpInvestorId;
    }

    public String getCpInvestorType() {
        return cpInvestorType;
    }

    public void setCpInvestorType(String cpInvestorType) {
        this.cpInvestorType = cpInvestorType;
    }

    public String getCpClientName() {
        return cpClientName;
    }

    public void setCpClientName(String cpClientName) {
        this.cpClientName = cpClientName;
    }

    public String getOrigSerialNo() {
        return origSerialNo;
    }

    public void setOrigSerialNo(String origSerialNo) {
        this.origSerialNo = origSerialNo;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getRequestTypeStr() {
        return requestTypeStr;
    }

    public void setRequestTypeStr(String requestTypeStr) {
        this.requestTypeStr = requestTypeStr;
    }

    public String getBsStr() {
        return bsStr;
    }

    public void setBsStr(String bsStr) {
        this.bsStr = bsStr;
    }

    public String getTradeMarketStr() {
        return tradeMarketStr;
    }

    public void setTradeMarketStr(String tradeMarketStr) {
        this.tradeMarketStr = tradeMarketStr;
    }

    public String getPledgeNumberStr() {
        return pledgeNumberStr;
    }

    public void setPledgeNumberStr(String pledgeNumberStr) {
        this.pledgeNumberStr = pledgeNumberStr;
    }

    public String getInvestorTypeStr() {
        return investorTypeStr;
    }

    public void setInvestorTypeStr(String investorTypeStr) {
        this.investorTypeStr = investorTypeStr;
    }

    public String getCpInvestorTypeStr() {
        return cpInvestorTypeStr;
    }

    public void setCpInvestorTypeStr(String cpInvestorTypeStr) {
        this.cpInvestorTypeStr = cpInvestorTypeStr;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("requestNo", requestNo)
                .add("requestType", requestType)
                .add("bs", bs)
                .add("tradeMarket", tradeMarket)
                .add("stockCode", stockCode)
                .add("stockName", stockName)
                .add("pledgeNumber", pledgeNumber)
                .add("repoAmount", repoAmount)
                .add("repoRate", repoRate)
                .add("discount", discount)
                .add("firstSettleDate", firstSettleDate)
                .add("compactTerm", compactTerm)
                .add("backDate", backDate)
                .add("repoMdate", repoMdate)
                .add("capDays", capDays)
                .add("swapIntoBonds", swapIntoBonds)
                .add("swapIntoBondsName", swapIntoBondsName)
                .add("swapIntoBondsVolume", swapIntoBondsVolume)
                .add("swapOutBonds", swapOutBonds)
                .add("swapOutBondsName", swapOutBondsName)
                .add("swapOutBondsVolume", swapOutBondsVolume)
                .add("origBonds", origBonds)
                .add("origBondsName", origBondsName)
                .add("origBondsVolume", origBondsVolume)
                .add("origRepoAmount", origRepoAmount)
                .add("transCode", transCode)
                .add("mIntAmt", mIntAmt)
                .add("backBalance", backBalance)
                .add("traderCode", traderCode)
                .add("memberId", memberId)
                .add("investorId", investorId)
                .add("investorType", investorType)
                .add("cpTraderCode", cpTraderCode)
                .add("cpMemberId", cpMemberId)
                .add("cpInvestorId", cpInvestorId)
                .add("cpInvestorType", cpInvestorType)
                .add("cpClientName", cpClientName)
                .add("origSerialNo", origSerialNo)
                .add("remark", remark)
                .add("account", account)
                .add("accountName", accountName)
                .add("requestTypeStr", requestTypeStr)
                .add("bsStr", bsStr)
                .add("tradeMarketStr", tradeMarketStr)
                .add("pledgeNumberStr", pledgeNumberStr)
                .add("investorTypeStr", investorTypeStr)
                .add("cpInvestorTypeStr", cpInvestorTypeStr)
                .toString();
    }
}
