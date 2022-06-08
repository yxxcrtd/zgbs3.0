package com.joyintech.tams.cfets.risk.dto;

import com.joyintech.tams.cfets.etr.bond.dto.CfetsBussCombDistribtBaseDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


@ApiModel(value="新建指令风险试算请求参数",description="新建指令风险试算请求参数")
public class CfetsEtrRiskcalRequest {

    @ApiModelProperty(name="finprodId",value="资产代码Id",example="ZC1")
    private String finprodId;

    @ApiModelProperty(name="finprodMarketId",value="资产代码",example="ZC1")
    private String finprodMarketId;

    @ApiModelProperty(name="priceModel",value="价格模式Code",example="01")
    private String priceModel;

    @ApiModelProperty(name="shareNum",value="券面总额",example="11")
    private BigDecimal shareNum;

    @ApiModelProperty(name="entrustOwnNum",value="本方券券面总额",example="11")
    private BigDecimal entrustOwnNum;

    @ApiModelProperty(name="entrustIntegrateNum",value="融入券/卖空购回券面总额",example="11")
    private BigDecimal entrustIntegrateNum;

    @ApiModelProperty(name="etrDate",value="指令日期",example="2021-07-06")
    private Date etrDate;

    @ApiModelProperty(name="settleSpreed",value="清算速度Code",example="1")
    private String settleSpreed;

    @ApiModelProperty(name="price",value="单位净价",example="1")
    private BigDecimal price;

    @ApiModelProperty(name="unitFprice",value="单位全价",example="1")
    private BigDecimal unitFprice;

    @ApiModelProperty(name="matureYield",value="到期收益率",example="1")
    private BigDecimal matureYield;

    @ApiModelProperty(name="exerYield",value="行权收益率",example="1")
    private BigDecimal exerYield;

    @ApiModelProperty(name="ps",value="交易方向Code",example="T01")
    private String ps;

    @ApiModelProperty(name="bussCombDistribtList",value="组合分配",example="T01")
    List<CfetsBussCombDistribtBaseDto> bussCombDistribtList;

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

    public BigDecimal getShareNum() {
        return shareNum;
    }

    public void setShareNum(BigDecimal shareNum) {
        this.shareNum = shareNum;
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

    public String getSettleSpreed() {
        return settleSpreed;
    }

    public void setSettleSpreed(String settleSpreed) {
        this.settleSpreed = settleSpreed;
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

    public String getPs() {
        return ps;
    }

    public void setPs(String ps) {
        this.ps = ps;
    }

    public List<CfetsBussCombDistribtBaseDto> getBussCombDistribtList() {
        return bussCombDistribtList;
    }

    public void setBussCombDistribtList(List<CfetsBussCombDistribtBaseDto> bussCombDistribtList) {
        this.bussCombDistribtList = bussCombDistribtList;
    }

    public BigDecimal getUnitFprice() {
        return unitFprice;
    }

    public void setUnitFprice(BigDecimal unitFprice) {
        this.unitFprice = unitFprice;
    }
}
