package com.joyintech.tams.cfets.etr.bond.dto;


import com.joyintech.tams.common.base.entity.EtrEntrust;
import com.joyintech.workflow.engine.dto.ProcessEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


@ApiModel(value="债券指令保存请求参数",description="债券指令保存请求参数")
public class CfetsEtribSaveRequest extends EtrEntrust implements ProcessEntity {

    @ApiModelProperty(name="operateType",value="操作类型",example="submit/save", required = true)
    private String operateType;

    @ApiModelProperty(name="calculateField",value="计算方式",example="price", required = true)
    private String calculateField;

    @ApiModelProperty(name="finprodId",value="资产代码Id",example="F01_ZC1", required = true)
    private String finprodId;

    @ApiModelProperty(name="finprodMarketId",value="资产代码",example="ZC1")
    private String finprodMarketId;

    @ApiModelProperty(name="priceModel",value="价格模式Code",example="01", required = true)
    private String priceModel;

    @ApiModelProperty(name="tradeAmt",value="券面总额",example="11", required = true)
    private BigDecimal tradeAmt;

    @ApiModelProperty(name="entrustOwnNum",value="本方券券面总额",example="11")
    private BigDecimal entrustOwnNum;

    @ApiModelProperty(name="entrustIntegrateNum",value="融入券/卖空购回券面总额",example="11")
    private BigDecimal entrustIntegrateNum;

    @ApiModelProperty(name="etrDate",value="指令日期",example="2021-07-06", required = true)
    private Date etrDate;

    @ApiModelProperty(name="settleSpeed",value="清算速度Code",example="1")
    private String settleSpeed;

    @ApiModelProperty(name="price",value="净价",example="1")
    private BigDecimal price;

    @ApiModelProperty(name="unitFprice",value="全价",example="1")
    private BigDecimal unitFprice;

    @ApiModelProperty(name="matureYield",value="到期收益率",example="1")
    private BigDecimal matureYield;

    @ApiModelProperty(name="exerYield",value="行权收益率",example="1")
    private BigDecimal exerYield;

    @ApiModelProperty(name="ps",value="交易方向Code",example="T01", required = true)
    private String ps;

    @ApiModelProperty(name="bussCombDistribtList",value="组合分配",example="T01", required = true)
    List<CfetsBussCombDistribtDto> bussCombDistribtList;

    @ApiModelProperty(name="elementId",value="按钮id",example="EtribBondInfo003_btn_submit")
    private String elementId;

    @ApiModelProperty(name="cfetsEtribdoRiskcalReturn",value="风险试算dto",example="CfetsEtribdoRiskcalReturn")
    private CfetsEtribdoRiskcalResponse cfetsEtribdoRiskcalReturn;

    @ApiModelProperty(name="finprodAbbr",value="资产简称",example="1")
    private String finprodAbbr;

    @ApiModelProperty(name="softSkipMode",value="软控是否继续？Y：继续",example="Y")
    private String softSkipMode;

    @ApiModelProperty(name="creditIsContinue",value="授信软控是否继续？Y：继续",example="Y")
    private String creditIsContinue;

   /* @ApiModelProperty(name="entrustId",value="指令代码",example="11")
    private String entrustId;*/
    /**
     * 资产4级分类
     */
    private String assetType11;

    /**
     * 是否审批
     */
    private String rskApproval;

    public String getOperateType() {
        return operateType;
    }

    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }

    public String getCalculateField() {
        return calculateField;
    }

    public void setCalculateField(String calculateField) {
        this.calculateField = calculateField;
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

    public String getPriceModel() {
        return priceModel;
    }

    public void setPriceModel(String priceModel) {
        this.priceModel = priceModel;
    }

    public BigDecimal getTradeAmt() {
        return tradeAmt;
    }

    public void setTradeAmt(BigDecimal tradeAmt) {
        this.tradeAmt = tradeAmt;
    }

    public BigDecimal getEntrustOwnNum() {
        return entrustOwnNum;
    }

    public void setEntrustOwnNum(BigDecimal entrustOwnNum) {
        this.entrustOwnNum = entrustOwnNum;
    }

    public BigDecimal getEntrustIntegrateNum() {
        return entrustIntegrateNum;
    }

    public void setEntrustIntegrateNum(BigDecimal entrustIntegrateNum) {
        this.entrustIntegrateNum = entrustIntegrateNum;
    }

    public Date getEtrDate() {
        return etrDate;
    }

    public void setEtrDate(Date etrDate) {
        this.etrDate = etrDate;
    }



    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getUnitFprice() {
        return unitFprice;
    }

    public void setUnitFprice(BigDecimal unitFprice) {
        this.unitFprice = unitFprice;
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

    public String getPs() {
        return ps;
    }

    public void setPs(String ps) {
        this.ps = ps;
    }



    public String getElementId() {
        return elementId;
    }

    public void setElementId(String elementId) {
        this.elementId = elementId;
    }


    public CfetsEtribdoRiskcalResponse getCfetsEtribdoRiskcalReturn() {
        return cfetsEtribdoRiskcalReturn;
    }

    public void setCfetsEtribdoRiskcalReturn(CfetsEtribdoRiskcalResponse cfetsEtribdoRiskcalReturn) {
        this.cfetsEtribdoRiskcalReturn = cfetsEtribdoRiskcalReturn;
    }

    public String getSettleSpeed() {
        return settleSpeed;
    }

    public void setSettleSpeed(String settleSpeed) {
        this.settleSpeed = settleSpeed;
    }

    public List<CfetsBussCombDistribtDto> getBussCombDistribtList() {
        return bussCombDistribtList;
    }

    public void setBussCombDistribtList(List<CfetsBussCombDistribtDto> bussCombDistribtList) {
        this.bussCombDistribtList = bussCombDistribtList;
    }

    public String getSoftSkipMode() {
        return softSkipMode;
    }

    public void setSoftSkipMode(String softSkipMode) {
        this.softSkipMode = softSkipMode;
    }

    public String getCreditIsContinue() {
        return creditIsContinue;
    }

    public void setCreditIsContinue(String creditIsContinue) {
        this.creditIsContinue = creditIsContinue;
    }

    public String getFinprodAbbr() {
        return finprodAbbr;
    }

    public void setFinprodAbbr(String finprodAbbr) {
        this.finprodAbbr = finprodAbbr;
    }

    public String getAssetType11() {
        return assetType11;
    }

    public void setAssetType11(String assetType11) {
        this.assetType11 = assetType11;
    }

    public String getRskApproval() {
        return rskApproval;
    }

    public void setRskApproval(String rskApproval) {
        this.rskApproval = rskApproval;
    }
}
