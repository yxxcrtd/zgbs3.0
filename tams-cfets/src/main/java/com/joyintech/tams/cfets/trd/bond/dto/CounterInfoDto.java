package com.joyintech.tams.cfets.trd.bond.dto;

import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.invest.common.constant.BussinessDictCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

/**
 *
 * @author zznn
 * @date 2021/11/23
 */
@ApiModel(value="请求报价可选机构",description="请求报价可选机构")
public class CounterInfoDto {

    @ApiModelProperty(name="detId",value="明细编号")
    private String detId;

    @ApiModelProperty(name="counterValue",value="对手方名称")
    private String counterValue;

    @ApiModelProperty(name="counterId",value="对手方Id")
    private String counterId;

    @ApiModelProperty(name="cpPartyId",value="对手银行间机构，21位编号")
    private String cpPartyId;

    @ApiModelProperty(name="cpTrader",value="对手方交易员")
    private String cpTrader;

    @ApiModelProperty(name="tradeStatus",value="交易状态")
    private String tradeStatus;

    @ApiModelProperty(name="tradeStatus",value="交易状态名称")
    private String tradeStatusStr;

    @ApiModelProperty(name="shareAmt",value="回复量(万元)")
    private BigDecimal shareAmt;

    @ApiModelProperty(name="unitCprice",value="净价")
    private BigDecimal unitCprice;

    @ApiModelProperty(name="unitFprice",value="全价")
    private BigDecimal unitFprice;

    @ApiModelProperty(name="yield",value="到期收益率(%)")
    private BigDecimal yield;

    @ApiModelProperty(name="exerYield",value="行权收益率(%")
    private BigDecimal exerYield;

    @ApiModelProperty(name="cpriceAmt",value="净价总额")
    private BigDecimal cpriceAmt;

    @ApiModelProperty(name="cpriceAmt",value="利息总额")
    private BigDecimal intAmt;

    @ApiModelProperty(name="cpriceAmt",value="全价总额")
    private BigDecimal fpriceAmt;

    public String getDetId() {
        return detId;
    }

    public void setDetId(String detId) {
        this.detId = detId;
    }

    public String getCounterValue() {
        return counterValue;
    }

    public void setCounterValue(String counterValue) {
        this.counterValue = counterValue;
    }

    public String getCounterId() {
        return counterId;
    }

    public void setCounterId(String counterId) {
        this.counterId = counterId;
    }

    public String getCpPartyId() {
        return cpPartyId;
    }

    public void setCpPartyId(String cpPartyId) {
        this.cpPartyId = cpPartyId;
    }

    public String getCpTrader() {
        return cpTrader;
    }

    public void setCpTrader(String cpTrader) {
        this.cpTrader = cpTrader;
    }

    public String getTradeStatus() {
        return tradeStatus;
    }

    public void setTradeStatus(String tradeStatus) {
        this.tradeStatus = tradeStatus;
    }

    public String getTradeStatusStr() {
        return SysDataUtil.getNameFromDataDict(BussinessDictCode.银行间上行报价状态, this.getTradeStatus());
    }

    public void setTradeStatusStr(String tradeStatusStr) {
        this.tradeStatusStr = tradeStatusStr;
    }

    public BigDecimal getShareAmt() {
        return shareAmt;
    }

    public void setShareAmt(BigDecimal shareAmt) {
        this.shareAmt = shareAmt;
    }

    public BigDecimal getUnitCprice() {
        return unitCprice;
    }

    public void setUnitCprice(BigDecimal unitCprice) {
        this.unitCprice = unitCprice;
    }

    public BigDecimal getUnitFprice() {
        return unitFprice;
    }

    public void setUnitFprice(BigDecimal unitFprice) {
        this.unitFprice = unitFprice;
    }

    public BigDecimal getYield() {
        return yield;
    }

    public void setYield(BigDecimal yield) {
        this.yield = yield;
    }

    public BigDecimal getExerYield() {
        return exerYield;
    }

    public void setExerYield(BigDecimal exerYield) {
        this.exerYield = exerYield;
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
}

