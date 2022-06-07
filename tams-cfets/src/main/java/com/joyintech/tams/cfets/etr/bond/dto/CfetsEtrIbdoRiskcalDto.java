package com.joyintech.tams.cfets.etr.bond.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;


@ApiModel(value="新建指令风险试算全部参数",description="新建指令风险试算全部参数")
public class CfetsEtrIbdoRiskcalDto {

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

    @ApiModelProperty(name="price",value="净价",example="1")
    private BigDecimal price;

    @ApiModelProperty(name="unitFprice",value="全价",example="1")
    private BigDecimal unitFprice;

    @ApiModelProperty(name="matureYield",value="到期收益率",example="1")
    private String matureYield;

    @ApiModelProperty(name="exerYield",value="行权收益率",example="1")
    private String exerYield;

    @ApiModelProperty(name="ps",value="交易方向Code",example="T01")
    private String ps;


    @ApiModelProperty(name="bussTrdTransfenpeiDto",value="组合分配",example="T01")
    List<Map> bussTrdTransfenpeiDto;

    @ApiModelProperty(name="busiType",value="业务类型",example="02")
    private String busiType;

    @ApiModelProperty(name="finprodType2",value="金融产品分类",example="F01")
    private String finprodType2;

    @ApiModelProperty(name="tradeMarket",value="交易场所",example="01")
    private String tradeMarket;

    @ApiModelProperty(name="vdate",value="指令开始日",example="2021-10-18")
    private Date vdate;

    @ApiModelProperty(name="mdate",value="指令结束日",example="2021-10-18")
    private Date mdate;

    @ApiModelProperty(name="ccy",value="币种",example="CNY")
    private String ccy;

    @ApiModelProperty(name="portfolioId",value="组合代码",example="A1")
    private String portfolioId;
    @ApiModelProperty(name="businessModel",value="业务模式",example="A1")
    private String businessModel;

    @ApiModelProperty(name="entrustNum",value="券面总额",example="11")
    private BigDecimal entrustNum;

    @ApiModelProperty(name="invAim",value="投资目的",example="AC")
    private String invAim;

    @ApiModelProperty(name="tradeMarketMode",value="交易市场",example="02")
    private String tradeMarketMode;

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


    public String getPs() {
        return ps;
    }

    public void setPs(String ps) {
        this.ps = ps;
    }


    public String getBusiType() {
        return busiType;
    }

    public void setBusiType(String busiType) {
        this.busiType = busiType;
    }

    public String getFinprodType2() {
        return finprodType2;
    }

    public void setFinprodType2(String finprodType2) {
        this.finprodType2 = finprodType2;
    }

    public String getTradeMarket() {
        return tradeMarket;
    }

    public void setTradeMarket(String tradeMarket) {
        this.tradeMarket = tradeMarket;
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

    public String getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId;
    }

    public String getBusinessModel() {
        return businessModel;
    }

    public void setBusinessModel(String businessModel) {
        this.businessModel = businessModel;
    }

    public BigDecimal getEntrustNum() {
        return entrustNum;
    }

    public void setEntrustNum(BigDecimal entrustNum) {
        this.entrustNum = entrustNum;
    }

    public String getCcy() {
        return ccy;
    }

    public void setCcy(String ccy) {
        this.ccy = ccy;
    }

    public BigDecimal getUnitFprice() {
        return unitFprice;
    }

    public void setUnitFprice(BigDecimal unitFprice) {
        this.unitFprice = unitFprice;
    }
    public List<Map> getBussTrdTransfenpeiDto() {
        return bussTrdTransfenpeiDto;
    }

    public void setBussTrdTransfenpeiDto(List<Map> bussTrdTransfenpeiDto) {
        this.bussTrdTransfenpeiDto = bussTrdTransfenpeiDto;
    }

    public String getMatureYield() {
        return matureYield;
    }

    public void setMatureYield(String matureYield) {
        this.matureYield = matureYield;
    }

    public String getExerYield() {
        return exerYield;
    }

    public void setExerYield(String exerYield) {
        this.exerYield = exerYield;
    }

    public String getInvAim() {
        return invAim;
    }

    public void setInvAim(String invAim) {
        this.invAim = invAim;
    }

    public String getTradeMarketMode() {
        return tradeMarketMode;
    }

    public void setTradeMarketMode(String tradeMarketMode) {
        this.tradeMarketMode = tradeMarketMode;
    }
}
