package com.joyintech.tams.cfets.trd.bond.dto;

import com.joyintech.background.component.common.pack.common.base.BaseEntity;
import com.joyintech.workflow.engine.dto.ProcessEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author zznn
 * @date 2021/11/10
 */
@ApiModel(value="债券报价实体(输入输出共用)",description="债券报价实体(输入输出共用)")
public class CfetsBondDealQuoteCommonDto extends BaseEntity implements ProcessEntity {

    private String totleTradeId;

    @ApiModelProperty(name="quoteId",value="报价编号")
    private String quoteId;

    @ApiModelProperty(name="operateType",value="操作类型" ,example = "save,submit")
    private String operatorType;

    @ApiModelProperty(name="quotaPageModel",value="报价操作类型" ,example = "marketResponse")
    private String quotaPageModel;

    @ApiModelProperty(name="validStatus",value="有效状态" ,example = "01")
    private String validStatus;

    @ApiModelProperty(name="openModel",value="打开入口" ,example = "copy:表示复制入口")
    private String openModel;

    @ApiModelProperty(name="quoteType",value="交易类型")
    private String quoteType;

    @ApiModelProperty(name="quoteStyle",value="报价方式")
    private String quoteStyle;

    @ApiModelProperty(name="distribution",value="分配方式")
    private String distribution;

    @ApiModelProperty(name="ps",value="交易方向")
    private String ps;

    @ApiModelProperty(name="finprodId",value="资产代码")
    private String finprodId;

    @ApiModelProperty(name="finprodMarketId",value="市场代码")
    private String finprodMarketId;

    @ApiModelProperty(name="finprodAbbr",value="资产简称")
    private String finprodAbbr;

    @ApiModelProperty(name="mySide",value="本方")
    private String mySide;

    @ApiModelProperty(name="mySideAbbr",value="本方名称")
    private String mySideAbbr;

    @ApiModelProperty(name="mySideTrader",value="本方交易员")
    private String mySideTrader;

    @ApiModelProperty(name="ouTraderName",value="本方交易员名称")
    private String ouTraderName;

    @ApiModelProperty(name="quoteMySideTrader",value="本方交易员(对话、请求)")
    private String quoteMySideTrader;

    @ApiModelProperty(name="quoteOuTraderName",value="本方交易员名称(对话、请求)")
    private String quoteOuTraderName;


    @ApiModelProperty(name="counterId",value="对手方")
    private String counterId;

    @ApiModelProperty(name="counterTrader",value="对手方交易员")
    private String counterTrader;

    @ApiModelProperty(name="counterName",value="对手方名称")
    private String counterName;

    @ApiModelProperty(name="counterTrader",value="对手方交易员名称")
    private String counterTraderName;

    @ApiModelProperty(name="ccy",value="币种")
    private String ccy;

    @ApiModelProperty(name="settleSpreedCode",value="清算速度")
    private String settleSpreedCode;

    @ApiModelProperty(name="tradeDate",value="交易日期")
    private Date tradeDate;

    @ApiModelProperty(name="settleDate",value="结算日期")
    private Date settleDate;

    @ApiModelProperty(name="calculateField",value="锚定值(01表示净价) 02表示到期收益率 03表示行权收益率")
    private String calculateField;

    @ApiModelProperty(name="price",value="净价")
    private BigDecimal price;

    @ApiModelProperty(name="cpriceAmt",value="净价总额")
    private BigDecimal cpriceAmt;

    @ApiModelProperty(name="unitInt",value="应计利息")
    private BigDecimal unitInt;

    @ApiModelProperty(name="intAmt",value="应计利息总额")
    private BigDecimal intAmt;

    @ApiModelProperty(name="unitFprice",value="全价")
    private BigDecimal unitFprice;

    @ApiModelProperty(name="fpriceAmt",value="全价总额")
    private BigDecimal fpriceAmt;

    @ApiModelProperty(name="matureYield",value="到期收益率")
    private BigDecimal matureYield;

    @ApiModelProperty(name="exerYield",value="行权收益率")
    private BigDecimal exerYield;

    @ApiModelProperty(name="shareAmt",value="券面总额")
    private BigDecimal shareAmt;

    @ApiModelProperty(name="outPartySec",value="本方券券面总额")
    private BigDecimal ourEntNum;

    @ApiModelProperty(name="inSec",value="融入券券面总额")
    private BigDecimal lendinEntNum;

    @ApiModelProperty(name="sellRepurchaseSec",value="卖空购回券面总额")
    private BigDecimal sellAbleNum;

    @ApiModelProperty(name="settleMethod",value="结算方式")
    private String  settleMethod;

    @ApiModelProperty(name="settleType",value="清算类型")
    private String  settleType;

    @ApiModelProperty(name="dealFeeAmt",value="交易手续费")
    private BigDecimal  dealFeeAmt;

    @ApiModelProperty(name="settFeeAmt",value="结算服务费")
    private BigDecimal  settFeeAmt;

    @ApiModelProperty(name="remark",value="备注")
    private String  remark;

    @ApiModelProperty(name="ourFpriceAmt",value="本方券全价总额")
    private BigDecimal ourFpriceAmt;

    @ApiModelProperty(name="lendinFpriceAmt",value="融入券全价总额")
    private BigDecimal lendinFpriceAmt;

    @ApiModelProperty(name="saleinFpriceAmt",value="卖空购回全价总额")
    private BigDecimal saleinFpriceAmt;

    @ApiModelProperty(name="ourIntAmt",value="本方券利息总额")
    private BigDecimal ourIntAmt;

    @ApiModelProperty(name="lendinIntAmt",value="融入券利息总额")
    private BigDecimal lendinIntAmt;

    @ApiModelProperty(name="saleinIntAmt",value="卖空购回应计利息总额")
    private BigDecimal saleinIntAmt;

    @ApiModelProperty(name="ptlAllocatios",value="组合分配")
    private List<PtlAllocationDto> ptlAllocatios;

    @ApiModelProperty(name="theoreticalExerciseDate",value="理论行权日")
    private Date theoreticalExerciseDate;

    @ApiModelProperty(name="actSetteDate",value="实际行权日")
    private Date actSetteDate;

    @ApiModelProperty(name="counterInfoList",value="可选机构(请求报价专用)")
    private List<CounterInfoDto> counterInfoList;

    @ApiModelProperty(name="tradeMarket",value="交易场所")
    private String  tradeMarket;

    @ApiModelProperty(name="isRight",value="是否含权债")
    private String isRight;

    @ApiModelProperty(name="isExercisePlan",value="是否有未来回售行权计划")
    private boolean isExercisePlan;

    @ApiModelProperty(name="secManageAcctId",value="证券账户")
    private String secManageAcctId;

    @ApiModelProperty(name="assetTypeOne",value="资产类型  type4")
    private String assetTypeOne;

    @ApiModelProperty(name="parValueAmt",value="本金额(元)")
    private BigDecimal parValueAmt;

    @ApiModelProperty(name="parValue",value="每百元金额(元)")
    private BigDecimal parValue;

    @ApiModelProperty(name="effDate",value="有效时间")
    private String effDate;

    @ApiModelProperty(name="type11",value="资产四级类型")
    private String type11;

    @ApiModelProperty(name="tradeStatus",value="报价状态")
    private String tradeStatus;

    @ApiModelProperty(name="softSkipMode",value="软控,是否确认")
    private String softSkipMode;

    private List<String> counterIdList;

    public String getQuoteId() {
        return quoteId;
    }

    public void setQuoteId(String quoteId) {
        this.quoteId = quoteId;
    }

    public String getOperatorType() {
        return operatorType;
    }

    public void setOperatorType(String operatorType) {
        this.operatorType = operatorType;
    }

    public String getQuotaPageModel() {
        return quotaPageModel;
    }

    public void setQuotaPageModel(String quotaPageModel) {
        this.quotaPageModel = quotaPageModel;
    }

    public String getValidStatus() {
        return validStatus;
    }

    public void setValidStatus(String validStatus) {
        this.validStatus = validStatus;
    }

    public String getOpenModel() {
        return openModel;
    }

    public void setOpenModel(String openModel) {
        this.openModel = openModel;
    }

    public String getPs() {
        return ps;
    }

    public void setPs(String ps) {
        this.ps = ps;
    }

    public String getFinprodId() {
        return finprodId;
    }

    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
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

    public String getMySide() {
        return mySide;
    }

    public void setMySide(String mySide) {
        this.mySide = mySide;
    }

    public String getMySideAbbr() {
        return mySideAbbr;
    }

    public void setMySideAbbr(String mySideAbbr) {
        this.mySideAbbr = mySideAbbr;
    }

    public String getMySideTrader() {
        return mySideTrader;
    }

    public void setMySideTrader(String mySideTrader) {
        this.mySideTrader = mySideTrader;
    }

    public String getOuTraderName() {
        return ouTraderName;
    }

    public void setOuTraderName(String ouTraderName) {
        this.ouTraderName = ouTraderName;
    }

    public String getCounterId() {
        return counterId;
    }

    public void setCounterId(String counterId) {
        this.counterId = counterId;
    }

    public String getCounterTrader() {
        return counterTrader;
    }

    public void setCounterTrader(String counterTrader) {
        this.counterTrader = counterTrader;
    }

    public String getCounterName() {
        return counterName;
    }

    public void setCounterName(String counterName) {
        this.counterName = counterName;
    }

    public String getCounterTraderName() {
        return counterTraderName;
    }

    public void setCounterTraderName(String counterTraderName) {
        this.counterTraderName = counterTraderName;
    }

    public String getCcy() {
        return ccy;
    }

    public void setCcy(String ccy) {
        this.ccy = ccy;
    }

    public String getSettleSpreedCode() {
        return settleSpreedCode;
    }

    public void setSettleSpreedCode(String settleSpreedCode) {
        this.settleSpreedCode = settleSpreedCode;
    }

    public Date getSettleDate() {
        return settleDate;
    }

    public void setSettleDate(Date settleDate) {
        this.settleDate = settleDate;
    }

    public String getCalculateField() {
        return calculateField;
    }

    public void setCalculateField(String calculateField) {
        this.calculateField = calculateField;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getMatureYield() {
        return matureYield;
    }

    public void setMatureYield(BigDecimal matureYield) {
        this.matureYield = matureYield;
    }

    public BigDecimal getExerYield() {
        return exerYield;
    }

    public void setExerYield(BigDecimal exerYield) {
        this.exerYield = exerYield;
    }

    public BigDecimal getShareAmt() {
        return shareAmt;
    }

    public void setShareAmt(BigDecimal shareAmt) {
        this.shareAmt = shareAmt;
    }

    public BigDecimal getOurEntNum() {
        return ourEntNum;
    }

    public void setOurEntNum(BigDecimal ourEntNum) {
        this.ourEntNum = ourEntNum;
    }

    public BigDecimal getLendinEntNum() {
        return lendinEntNum;
    }

    public void setLendinEntNum(BigDecimal lendinEntNum) {
        this.lendinEntNum = lendinEntNum;
    }

    public BigDecimal getSellAbleNum() {
        return sellAbleNum;
    }

    public void setSellAbleNum(BigDecimal sellAbleNum) {
        this.sellAbleNum = sellAbleNum;
    }

    public String getSettleMethod() {
        return settleMethod;
    }

    public void setSettleMethod(String settleMethod) {
        this.settleMethod = settleMethod;
    }

    public String getSettleType() {
        return settleType;
    }

    public void setSettleType(String settleType) {
        this.settleType = settleType;
    }

    public BigDecimal getDealFeeAmt() {
        return dealFeeAmt;
    }

    public void setDealFeeAmt(BigDecimal dealFeeAmt) {
        this.dealFeeAmt = dealFeeAmt;
    }

    public BigDecimal getSettFeeAmt() {
        return settFeeAmt;
    }

    public void setSettFeeAmt(BigDecimal settFeeAmt) {
        this.settFeeAmt = settFeeAmt;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public List<PtlAllocationDto> getPtlAllocatios() {
        return ptlAllocatios;
    }

    public void setPtlAllocatios(List<PtlAllocationDto> ptlAllocatios) {
        this.ptlAllocatios = ptlAllocatios;
    }

    public Date getTheoreticalExerciseDate() {
        return theoreticalExerciseDate;
    }

    public void setTheoreticalExerciseDate(Date theoreticalExerciseDate) {
        this.theoreticalExerciseDate = theoreticalExerciseDate;
    }

    public Date getActSetteDate() {
        return actSetteDate;
    }

    public void setActSetteDate(Date actSetteDate) {
        this.actSetteDate = actSetteDate;
    }

    public Date getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(Date tradeDate) {
        this.tradeDate = tradeDate;
    }

    public BigDecimal getOurFpriceAmt() {
        return ourFpriceAmt;
    }

    public void setOurFpriceAmt(BigDecimal ourFpriceAmt) {
        this.ourFpriceAmt = ourFpriceAmt;
    }

    public BigDecimal getLendinFpriceAmt() {
        return lendinFpriceAmt;
    }

    public void setLendinFpriceAmt(BigDecimal lendinFpriceAmt) {
        this.lendinFpriceAmt = lendinFpriceAmt;
    }

    public BigDecimal getOurIntAmt() {
        return ourIntAmt;
    }

    public void setOurIntAmt(BigDecimal ourIntAmt) {
        this.ourIntAmt = ourIntAmt;
    }

    public BigDecimal getLendinIntAmt() {
        return lendinIntAmt;
    }

    public void setLendinIntAmt(BigDecimal lendinIntAmt) {
        this.lendinIntAmt = lendinIntAmt;
    }

    public BigDecimal getCpriceAmt() {
        return cpriceAmt;
    }

    public void setCpriceAmt(BigDecimal cpriceAmt) {
        this.cpriceAmt = cpriceAmt;
    }

    public BigDecimal getUnitInt() {
        return unitInt;
    }

    public void setUnitInt(BigDecimal unitInt) {
        this.unitInt = unitInt;
    }

    public BigDecimal getIntAmt() {
        return intAmt;
    }

    public void setIntAmt(BigDecimal intAmt) {
        this.intAmt = intAmt;
    }

    public BigDecimal getUnitFprice() {
        return unitFprice;
    }

    public void setUnitFprice(BigDecimal unitFprice) {
        this.unitFprice = unitFprice;
    }

    public BigDecimal getFpriceAmt() {
        return fpriceAmt;
    }

    public void setFpriceAmt(BigDecimal fpriceAmt) {
        this.fpriceAmt = fpriceAmt;
    }

    public BigDecimal getSaleinFpriceAmt() {
        return saleinFpriceAmt;
    }

    public void setSaleinFpriceAmt(BigDecimal saleinFpriceAmt) {
        this.saleinFpriceAmt = saleinFpriceAmt;
    }

    public BigDecimal getSaleinIntAmt() {
        return saleinIntAmt;
    }

    public void setSaleinIntAmt(BigDecimal saleinIntAmt) {
        this.saleinIntAmt = saleinIntAmt;
    }

    public String getDistribution() {
        return distribution;
    }

    public void setDistribution(String distribution) {
        this.distribution = distribution;
    }

    public String getQuoteType() {
        return quoteType;
    }

    public void setQuoteType(String quoteType) {
        this.quoteType = quoteType;
    }

    public String getQuoteStyle() {
        return quoteStyle;
    }

    public void setQuoteStyle(String quoteStyle) {
        this.quoteStyle = quoteStyle;
    }

    public List<CounterInfoDto> getCounterInfoList() {
        return counterInfoList;
    }

    public void setCounterInfoList(List<CounterInfoDto> counterInfoList) {
        this.counterInfoList = counterInfoList;
    }

    public String getTradeMarket() {
        return tradeMarket;
    }

    public void setTradeMarket(String tradeMarket) {
        this.tradeMarket = tradeMarket;
    }

    public String getIsRight() {
        return isRight;
    }

    public void setIsRight(String isRight) {
        this.isRight = isRight;
    }

    public String getSecManageAcctId() {
        return secManageAcctId;
    }

    public void setSecManageAcctId(String secManageAcctId) {
        this.secManageAcctId = secManageAcctId;
    }

    public boolean isExercisePlan() {
        return isExercisePlan;
    }

    public void setExercisePlan(boolean exercisePlan) {
        isExercisePlan = exercisePlan;
    }

    public String getAssetTypeOne() {
        return assetTypeOne;
    }

    public void setAssetTypeOne(String assetTypeOne) {
        this.assetTypeOne = assetTypeOne;
    }

    public String getQuoteMySideTrader() {
        return quoteMySideTrader;
    }

    public void setQuoteMySideTrader(String quoteMySideTrader) {
        this.quoteMySideTrader = quoteMySideTrader;
    }

    public String getQuoteOuTraderName() {
        return quoteOuTraderName;
    }

    public void setQuoteOuTraderName(String quoteOuTraderName) {
        this.quoteOuTraderName = quoteOuTraderName;
    }

    public BigDecimal getParValueAmt() {
        return parValueAmt;
    }

    public void setParValueAmt(BigDecimal parValueAmt) {
        this.parValueAmt = parValueAmt;
    }

    public BigDecimal getParValue() {
        return parValue;
    }

    public void setParValue(BigDecimal parValue) {
        this.parValue = parValue;
    }

    public String getEffDate() {
        return effDate;
    }

    public void setEffDate(String effDate) {
        this.effDate = effDate;
    }

    public String getType11() {
        return type11;
    }

    public void setType11(String type11) {
        this.type11 = type11;
    }

    public String getTradeStatus() {
        return tradeStatus;
    }

    public void setTradeStatus(String tradeStatus) {
        this.tradeStatus = tradeStatus;
    }

    public String getTotleTradeId() {
        return totleTradeId;
    }

    public void setTotleTradeId(String totleTradeId) {
        this.totleTradeId = totleTradeId;
    }

    public List<String> getCounterIdList() {
        return counterIdList;
    }

    public void setCounterIdList(List<String> counterIdList) {
        this.counterIdList = counterIdList;
    }

    public String getSoftSkipMode() {
        return softSkipMode;
    }

    public void setSoftSkipMode(String softSkipMode) {
        this.softSkipMode = softSkipMode;
    }
}

