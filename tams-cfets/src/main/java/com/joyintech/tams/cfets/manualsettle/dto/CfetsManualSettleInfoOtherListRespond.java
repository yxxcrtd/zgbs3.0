package com.joyintech.tams.cfets.manualsettle.dto;

import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.constant.DictCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

/**
 * 银行间手工交收无组合分配信息返回参数DTO
 * @author sjj
 * @version 1.0 2021年11月18日
 * @see CfetsManualSettleInfoOtherListRespond
 * @since 1.0
 */

@ApiModel(value="银行间手工交收无组合分配信息返回参数",description="银行间手工交收无组合分配信息返回参数")
public class CfetsManualSettleInfoOtherListRespond {

    @ApiModelProperty(name="portfolioId",value="组合代码",example="A1")
    private String portfolioId;

    @ApiModelProperty(name="portfolioName",value="组合名称",example="A1")
    private String portfolioName;

    @ApiModelProperty(name="finprodMarketId",value="资产代码",example="字符串1")
    private String finprodMarketId;

    @ApiModelProperty(name="finprodAbbr",value="资产简称",example="字符串1")
    private String finprodAbbr;

    @ApiModelProperty(name="ps",value="交易方向CODE",example="A1")
    private String ps;

    @ApiModelProperty(name="finprodType2",value="资产类型CODE",example="A1")
    private String finprodType2;

    @ApiModelProperty(name="tradePlat",value="交易市场",example="A1")
    private String tradePlat;

    @ApiModelProperty(name="tradeDate",value="成交日期",example="A1")
    private String tradeDate;

    @ApiModelProperty(name="settleDate",value="结算日期",example="A1")
    private String settleDate;

    //理论付息信息
    @ApiModelProperty(name="qty",value="券面总额",example="A1")
    private BigDecimal qty;

    @ApiModelProperty(name="unitPrice",value="净价",example="100")
    private BigDecimal unitPrice;

    @ApiModelProperty(name="unitInt",value="应计利息（元）",example="100")
    private BigDecimal unitInt;

    @ApiModelProperty(name="allPrice",value="全价",example="100")
    private BigDecimal allPrice;

    @ApiModelProperty(name="cpriceAmt",value="净价总额",example="100")
    private BigDecimal cpriceAmt;

    @ApiModelProperty(name="intAmt",value="应计利息总额（元）",example="100")
    private BigDecimal intAmt;

    @ApiModelProperty(name="fpriceAmt",value="全价总额",example="1000000")
    private BigDecimal fpriceAmt;

    @ApiModelProperty(name="tradeAmt",value="交易金额",example="1000000")
    private BigDecimal tradeAmt;

    @ApiModelProperty(name="matureYield",value="到期收益率",example="1000000")
    private BigDecimal matureYield;

    @ApiModelProperty(name="exeYield",value="行权收益率",example="1000000")
    private BigDecimal exeYield;

    @ApiModelProperty(name="settleSpeed",value="清算速度",example="字符串1")
    private String settleSpeed;

    @ApiModelProperty(name="ccy",value="币种",example="CNY")
    private String ccy;

    @ApiModelProperty(name="remark",value="备注",example="A1")
    private String remark;

    @ApiModelProperty(name="regDate",value="登记日期",example="字符串1")
    private String regDate;

    @ApiModelProperty(name="jyFeeAmt",value="交易手续费",example="1000000")
    private BigDecimal jyFeeAmt;

    @ApiModelProperty(name="jsFeeAmt",value="结算手续费",example="1000000")
    private BigDecimal jsFeeAmt;

    @ApiModelProperty(name="shRate",value="赎回比例",example="1000000")
    private BigDecimal shRate;

    @ApiModelProperty(name="tradeMarket",value="交易场所",example="字符串1")
    private String tradeMarket;

    @ApiModelProperty(name="tradeMarketMode",value="一级市场/二级市场",example="字符串1")
    private String tradeMarketMode;

    @ApiModelProperty(name="deliveryType",value="结算方式",example="字符串1")
    private String  deliveryType;

    @ApiModelProperty(name="clearType",value="清算类型",example="字符串1")
    private String clearType;

    @ApiModelProperty(name="ouPartyId",value="本方",example="字符串1")
    private String ouPartyId;

    @ApiModelProperty(name="ouTraderId",value="本方银行间交易员",example="字符串1")
    private String ouTraderId;

    @ApiModelProperty(name="counterId",value="对手方",example="字符串1")
    private String counterId;

    @ApiModelProperty(name="counterTrade",value="对手方交易员",example="字符串1")
    private String counterTrade;

    @ApiModelProperty(name="spanTextOne",value="分配方式",example="字符串1")
    private String spanTextOne;

    @ApiModelProperty(name="exerciseDate",value="理论行权日",example="字符串1")
    private String exerciseDate;

    @ApiModelProperty(name="actExerciseDate",value="实际行权日",example="字符串1")
    private String actExerciseDate;

    @ApiModelProperty(name="exerciseDate1",value="理论行权日",example="字符串1")
    private String exerciseDate1;

    @ApiModelProperty(name="actExerciseDate1",value="实际行权日",example="字符串1")
    private String actExerciseDate1;

    @ApiModelProperty(name="tradeId",value="成交编号",example="字符串1")
    private String tradeId;

    @ApiModelProperty(name="isRight",value="是否含权",example="A1")
    private String isRight;

    @ApiModelProperty(name="accountPosition",value="账户持仓",example="99.99")
    private BigDecimal accountPosition;

    public String getSpanTextOneStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.分配方式, this.getSpanTextOne());
    }

    public String getClearTypeStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.清算类型, this.getClearType());
    }

    public String getDeliveryTypeStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.结算方式, this.getDeliveryType());
    }

    public String getTradeMarketModeStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.交易市场, this.getTradeMarketMode());
    }

    public String getTradePlatStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.交易平台, this.getTradePlat());
    }

    public String getTradeMarketStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.交易场所, this.getTradeMarket());
    }

    public String getCcyStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.币种, this.getCcy());
    }

    public String getSettleSpeedStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.清算速度, this.getSettleSpeed());
    }

    public String getSpanTextOne() {
        return spanTextOne;
    }

    public void setSpanTextOne(String spanTextOne) {
        this.spanTextOne = spanTextOne;
    }

    public String getClearType() {
        return clearType;
    }

    public void setClearType(String clearType) {
        this.clearType = clearType;
    }

    public String getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(String deliveryType) {
        this.deliveryType = deliveryType;
    }

    public String getTradeMarketMode() {
        return tradeMarketMode;
    }

    public void setTradeMarketMode(String tradeMarketMode) {
        this.tradeMarketMode = tradeMarketMode;
    }

    public BigDecimal getShRate() {
        return shRate;
    }

    public void setShRate(BigDecimal shRate) {
        this.shRate = shRate;
    }

    public String getTradeMarket() {
        return tradeMarket;
    }

    public void setTradeMarket(String tradeMarket) {
        this.tradeMarket = tradeMarket;
    }

    public String getCcy() {
        return ccy;
    }

    public void setCcy(String ccy) {
        this.ccy = ccy;
    }

    public BigDecimal getExeYield() {
        return exeYield;
    }

    public void setExeYield(BigDecimal exeYield) {
        this.exeYield = exeYield;
    }

    public String getSettleSpeed() {
        return settleSpeed;
    }

    public void setSettleSpeed(String settleSpeed) {
        this.settleSpeed = settleSpeed;
    }

    public String getFinprodMarketId() {
        return finprodMarketId;
    }

    public void setFinprodMarketId(String finprodMarketId) {
        this.finprodMarketId = finprodMarketId;
    }

    public String getFinprodAbbr() {
        return finprodAbbr;
    }

    public void setFinprodAbbr(String finprodAbbr) {
        this.finprodAbbr = finprodAbbr;
    }

    public String getPs() {
        return ps;
    }

    public void setPs(String ps) {
        this.ps = ps;
    }

    public String getPsStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.交易方向, this.getPs());
    }

    public BigDecimal getQty() {
        return qty;
    }

    public void setQty(BigDecimal qty) {
        this.qty = qty;
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

    public String getFinprodType2() {
        return finprodType2;
    }

    public void setFinprodType2(String finprodType2) {
        this.finprodType2 = finprodType2;
    }

    public String getFinprodType2Str() {
        return SysDataUtil.getNameFromDataDict(DictCode.金融产品类型_投管用, this.getFinprodType2());
    }

    public String getTradePlat() {
        return tradePlat;
    }

    public void setTradePlat(String tradePlat) {
        this.tradePlat = tradePlat;
    }

    public String getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(String tradeDate) {
        this.tradeDate = tradeDate;
    }

    public String getSettleDate() {
        return settleDate;
    }

    public void setSettleDate(String settleDate) {
        this.settleDate = settleDate;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getUnitInt() {
        return unitInt;
    }

    public void setUnitInt(BigDecimal unitInt) {
        this.unitInt = unitInt;
    }

    public BigDecimal getAllPrice() {
        return allPrice;
    }

    public void setAllPrice(BigDecimal allPrice) {
        this.allPrice = allPrice;
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

    public BigDecimal getMatureYield() {
        return matureYield;
    }

    public void setMatureYield(BigDecimal matureYield) {
        this.matureYield = matureYield;
    }

    public BigDecimal getTradeAmt() {
        return tradeAmt;
    }

    public void setTradeAmt(BigDecimal tradeAmt) {
        this.tradeAmt = tradeAmt;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getRegDate() {
        return regDate;
    }

    public void setRegDate(String regDate) {
        this.regDate = regDate;
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

    public String getOuPartyId() {
        return ouPartyId;
    }

    public void setOuPartyId(String ouPartyId) {
        this.ouPartyId = ouPartyId;
    }

    public String getOuTraderId() {
        return ouTraderId;
    }

    public void setOuTraderId(String ouTraderId) {
        this.ouTraderId = ouTraderId;
    }

    public String getCounterId() {
        return counterId;
    }

    public void setCounterId(String counterId) {
        this.counterId = counterId;
    }

    public String getCounterTrade() {
        return counterTrade;
    }

    public void setCounterTrade(String counterTrade) {
        this.counterTrade = counterTrade;
    }

    public String getExerciseDate() {
        return exerciseDate;
    }

    public void setExerciseDate(String exerciseDate) {
        this.exerciseDate = exerciseDate;
    }

    public String getActExerciseDate() {
        return actExerciseDate;
    }

    public void setActExerciseDate(String actExerciseDate) {
        this.actExerciseDate = actExerciseDate;
    }

    public String getExerciseDate1() {
        return exerciseDate1;
    }

    public void setExerciseDate1(String exerciseDate1) {
        this.exerciseDate1 = exerciseDate1;
    }

    public String getActExerciseDate1() {
        return actExerciseDate1;
    }

    public void setActExerciseDate1(String actExerciseDate1) {
        this.actExerciseDate1 = actExerciseDate1;
    }

    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }

    public String getIsRight() {
        return isRight;
    }

    public void setIsRight(String isRight) {
        this.isRight = isRight;
    }

    public BigDecimal getAccountPosition() {
        return accountPosition;
    }

    public void setAccountPosition(BigDecimal accountPosition) {
        this.accountPosition = accountPosition;
    }
}
