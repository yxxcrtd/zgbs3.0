package com.joyintech.tams.cfets.manualsettle.dto;

import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.constant.DictCode;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

//组合分配信息共通DTO
public class PortIntFenPeiInfoDto {

    @ApiModelProperty(name = "tradeId", value = "交易编号")
    private String tradeId;

    @ApiModelProperty(name = "portfolioId", value = "组合代码", example = "A1")
    private String portfolioId;

    @ApiModelProperty(name = "portfolioName", value = "组合名称", example = "A1")
    private String portfolioName;

    @ApiModelProperty(name = "qtyd", value = "券面总额（万元）", example = "A1")
    private BigDecimal qtyd;

    @ApiModelProperty(name = "prinAmtTheoryd", value = "还本金额（元）", example = "A1")
    private BigDecimal prinAmtTheoryd;

    @ApiModelProperty(name = "intAmtTheoryd", value = "理论应收利息（元）", example = "A1")
    private BigDecimal intAmtTheoryd;

    @ApiModelProperty(name = "cpriceAmt", value = "净价总额", example = "100")
    private BigDecimal cpriceAmt;

    @ApiModelProperty(name = "intAmt", value = "应计利息总额（元）", example = "100")
    private BigDecimal intAmt;

    @ApiModelProperty(name = "fpriceAmt", value = "全价总额", example = "1000000")
    private BigDecimal fpriceAmt;

    @ApiModelProperty(name = "jyFeeAmt", value = "交易手续费", example = "1000000")
    private BigDecimal jyFeeAmt;

    @ApiModelProperty(name = "jsFeeAmt", value = "结算手续费", example = "1000000")
    private BigDecimal jsFeeAmt;

    @ApiModelProperty(name = "tradeAmt", value = "交易金额", example = "1000000")
    private BigDecimal tradeAmt;

    @ApiModelProperty(name = "entrustId", value = "委托代码", example = "A1")
    private String entrustId;

    @ApiModelProperty(name = "rowNums", value = "序号", example = "A1")
    private Integer rowNums;

    @ApiModelProperty(name = "unFinshAmt", value = "剩余指令额度（万元）", example = "1000000")
    private BigDecimal unFinshAmt;

    @ApiModelProperty(name = "secAcctId", value = "证券账户", example = "A1")
    private String secAcctId;

    @ApiModelProperty(name = "priceModel", value = "价格模式", example = "01")
    private String priceModel;

    @ApiModelProperty(name = "matureYield", value = "到期收益率", example = "1000000")
    private BigDecimal matureYield;

    @ApiModelProperty(name = "exeYield", value = "行权收益率", example = "1000000")
    private BigDecimal exeYield;

    @ApiModelProperty(name = "busiTypePosition", value = "业务模式", example = "01")
    private String busiTypePosition;

    @ApiModelProperty(name = "unitPrice", value = "净价", example = "1000000")
    private BigDecimal unitPrice;

    @ApiModelProperty(name = "qtydq", value = "持有面额（元）", example = "A1")
    private BigDecimal qtydq;

    @ApiModelProperty(name = "mintAmt", value = "回购利息（元）", example = "100")
    private BigDecimal mintAmt;

    @ApiModelProperty(name = "mtradeAmt", value = "到期结算金额（元）", example = "100")
    private BigDecimal mtradeAmt;

    @ApiModelProperty(name = "disFaceValue", value = "质押券折算总额（元）", example = "100")
    private BigDecimal disFaceValue;

    @ApiModelProperty(name = "rate", value = "回购利率", example = "100")
    private BigDecimal rate;

    private BigDecimal tradeYield;

    @ApiModelProperty(name = "totalAmtTheory", value = "理论交收金额", example = "A1")
    private BigDecimal totalAmtTheory;

    @ApiModelProperty(name = "payAmt", value = "缴款金额（元）")
    private BigDecimal payAmt;

    @ApiModelProperty(name = "bidPrice", value = "投标标位")
    private String bidPrice;

    @ApiModelProperty(name = "winPrice", value = "中标标位")
    private String winPrice;

    @ApiModelProperty(name = "invManagerName", value = "投资经理名称", example = "字符串1")
    private String invManagerName;

    @ApiModelProperty(name = "totalBiddingNum", value = "汇总投标券面总额(万元)")
    private BigDecimal totalBiddingNum;

    @ApiModelProperty(name = "theWinPrinamt", value = "理论中标券面总额 (万元)")
    private BigDecimal theWinPrinamt;

    @ApiModelProperty(name = "actWinPrinamt", value = "实际中标券面总额（万元）")
    private BigDecimal actWinPrinamt;


    public String getPriceModelStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.价格模式, this.getPriceModel());
    }

    public String getBusiTypePositionStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.业务模式, this.getBusiTypePosition());
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

    public BigDecimal getQtyd() {
        return qtyd;
    }

    public void setQtyd(BigDecimal qtyd) {
        this.qtyd = qtyd;
    }

    public BigDecimal getPrinAmtTheoryd() {
        return prinAmtTheoryd;
    }

    public void setPrinAmtTheoryd(BigDecimal prinAmtTheoryd) {
        this.prinAmtTheoryd = prinAmtTheoryd;
    }

    public BigDecimal getIntAmtTheoryd() {
        return intAmtTheoryd;
    }

    public void setIntAmtTheoryd(BigDecimal intAmtTheoryd) {
        this.intAmtTheoryd = intAmtTheoryd;
    }

    public BigDecimal getCpriceAmt() {
        return cpriceAmt;
    }

    public void setCpriceAmt(BigDecimal cpriceAmt) {
        this.cpriceAmt = cpriceAmt;
    }

    public BigDecimal getIntAmt() {
        return intAmt;
    }

    public void setIntAmt(BigDecimal intAmt) {
        this.intAmt = intAmt;
    }

    public BigDecimal getFpriceAmt() {
        return fpriceAmt;
    }

    public void setFpriceAmt(BigDecimal fpriceAmt) {
        this.fpriceAmt = fpriceAmt;
    }

    public BigDecimal getJyFeeAmt() {
        return jyFeeAmt;
    }

    public void setJyFeeAmt(BigDecimal jyFeeAmt) {
        this.jyFeeAmt = jyFeeAmt;
    }

    public BigDecimal getJsFeeAmt() {
        return jsFeeAmt;
    }

    public void setJsFeeAmt(BigDecimal jsFeeAmt) {
        this.jsFeeAmt = jsFeeAmt;
    }

    public BigDecimal getTradeAmt() {
        return tradeAmt;
    }

    public void setTradeAmt(BigDecimal tradeAmt) {
        this.tradeAmt = tradeAmt;
    }

    public String getEntrustId() {
        return entrustId;
    }

    public void setEntrustId(String entrustId) {
        this.entrustId = entrustId;
    }

    public Integer getRowNums() {
        return rowNums;
    }

    public void setRowNums(Integer rowNums) {
        this.rowNums = rowNums;
    }

    public BigDecimal getUnFinshAmt() {
        return unFinshAmt;
    }

    public void setUnFinshAmt(BigDecimal unFinshAmt) {
        this.unFinshAmt = unFinshAmt;
    }

    public String getPriceModel() {
        return priceModel;
    }

    public void setPriceModel(String priceModel) {
        this.priceModel = priceModel;
    }

    public String getSecAcctId() {
        return secAcctId;
    }

    public void setSecAcctId(String secAcctId) {
        this.secAcctId = secAcctId;
    }

    public BigDecimal getMatureYield() {
        return matureYield;
    }

    public void setMatureYield(BigDecimal matureYield) {
        this.matureYield = matureYield;
    }

    public BigDecimal getExeYield() {
        return exeYield;
    }

    public void setExeYield(BigDecimal exeYield) {
        this.exeYield = exeYield;
    }

    public String getBusiTypePosition() {
        return busiTypePosition;
    }

    public void setBusiTypePosition(String busiTypePosition) {
        this.busiTypePosition = busiTypePosition;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getQtydq() {
        return qtydq;
    }

    public void setQtydq(BigDecimal qtydq) {
        this.qtydq = qtydq;
    }

    public BigDecimal getMintAmt() {
        return mintAmt;
    }

    public void setMintAmt(BigDecimal mintAmt) {
        this.mintAmt = mintAmt;
    }

    public BigDecimal getMtradeAmt() {
        return mtradeAmt;
    }

    public void setMtradeAmt(BigDecimal mtradeAmt) {
        this.mtradeAmt = mtradeAmt;
    }

    public BigDecimal getDisFaceValue() {
        return disFaceValue;
    }

    public void setDisFaceValue(BigDecimal disFaceValue) {
        this.disFaceValue = disFaceValue;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public BigDecimal getTradeYield() {
        return tradeYield;
    }

    public void setTradeYield(BigDecimal tradeYield) {
        this.tradeYield = tradeYield;
    }

    public BigDecimal getTotalAmtTheory() {
        return totalAmtTheory;
    }

    public void setTotalAmtTheory(BigDecimal totalAmtTheory) {
        this.totalAmtTheory = totalAmtTheory;
    }

    public BigDecimal getPayAmt() {
        return payAmt;
    }

    public void setPayAmt(BigDecimal payAmt) {
        this.payAmt = payAmt;
    }

    public String getBidPrice() {
        return bidPrice;
    }

    public void setBidPrice(String bidPrice) {
        this.bidPrice = bidPrice;
    }

    public String getWinPrice() {
        return winPrice;
    }

    public void setWinPrice(String winPrice) {
        this.winPrice = winPrice;
    }

    public String getInvManagerName() {
        return invManagerName;
    }

    public void setInvManagerName(String invManagerName) {
        this.invManagerName = invManagerName;
    }

    public BigDecimal getTotalBiddingNum() {
        return totalBiddingNum;
    }

    public void setTotalBiddingNum(BigDecimal totalBiddingNum) {
        this.totalBiddingNum = totalBiddingNum;
    }

    public BigDecimal getTheWinPrinamt() {
        return theWinPrinamt;
    }

    public void setTheWinPrinamt(BigDecimal theWinPrinamt) {
        this.theWinPrinamt = theWinPrinamt;
    }

    public BigDecimal getActWinPrinamt() {
        return actWinPrinamt;
    }

    public void setActWinPrinamt(BigDecimal actWinPrinamt) {
        this.actWinPrinamt = actWinPrinamt;
    }
}
