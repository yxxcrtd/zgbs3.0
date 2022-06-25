/*
 * 文件名：CfetsTrdConfirmRegInfoListResponse.java
 * 版权：Copyright 2020-2022 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管系统V2.0
 * 修改人：
 * 修改时间：2021年01月25日
 * 修改内容：新建
 * 系统名称：理财资管系统V2.0
 */

package com.joyintech.tams.cfets.trdconfirm.dto;


import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.constant.DictCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 银行间交易成交确认
 * Cstp债券返回数据DTO
 * @author 1
 * @version 1.0 2021年10月26日
 * @see CfetsTrdConfirmCstpBondInfoResponse
 * @since 1.0
 */
@ApiModel(value="查询Cstp债券成交列表返回数据",description="查询Cstp债券成交列表返回数据")
public class CfetsTrdConfirmCstpBondInfoResponse {

    @ApiModelProperty(name="execid",value="成交编号",example="1")
    private String execid;

    @ApiModelProperty(name="tradeDate",value="成交日期",example="admin")
    private Date tradeDate;

    @ApiModelProperty(name="finprodId",value="资产代码Id",example="ZC1")
    private String finprodId;

    @ApiModelProperty(name="finprodMarketId",value="资产代码",example="ZC1")
    private String finprodMarketId;

    @ApiModelProperty(name="finprodAbbr",value="资产简称",example="ZC名")
    private String finprodAbbr;

    @ApiModelProperty(name="ps",value="交易方向Code",example="T01")
    private String ps;

    @ApiModelProperty(name="psName",value="交易方向",example="T01")
    private String psName;

    @ApiModelProperty(name="settleSpeed",value="清算速度Code",example="1")
    private String settleSpeed;

    @ApiModelProperty(name="settleSpeedName",value="清算速度",example="1")
    private String settleSpeedName;

    @ApiModelProperty(name="acctNo",value="托管账户Code",example="1")
    private String acctNo;

    @ApiModelProperty(name="acctNoName",value="托管账户",example="1")
    private String acctNoName;

    @ApiModelProperty(name="settleDate",value="结算日期",example="admin")
    private Date settleDate;

    @ApiModelProperty(name="unitCprice",value="净价（元）",example="字符串1")
    private BigDecimal unitCprice;

    @ApiModelProperty(name="yield",value="到期收益(率)",example="字符串1")
    private BigDecimal yield;

    @ApiModelProperty(name="exerYield",value="行权收益率(%)",example="字符串1")
    private BigDecimal exerYield;

    @ApiModelProperty(name="lastqty",value="券面总额（万元）",example="字符串1")
    private BigDecimal lastqty;

    @ApiModelProperty(name="unitInt",value="应计利息(元)",example="字符串1")
    private BigDecimal unitInt;

    @ApiModelProperty(name="unitFprice",value="全价(元)",example="字符串1")
    private BigDecimal unitFprice;

    @ApiModelProperty(name="tradeAmt",value="交易金额（元）",example="字符串1")
    private BigDecimal tradeAmt;

    @ApiModelProperty(name="intAmt",value="应计利息总额（元）",example="字符串1")
    private BigDecimal intAmt;

    @ApiModelProperty(name="mtradeAmt",value="结算金额（元）",example="字符串1")
    private BigDecimal mtradeAmt;

    @ApiModelProperty(name="clearType",value="清算类型Code",example="字符串1")
    private String clearType;
    @ApiModelProperty(name="clearTypeName",value="清算类型",example="字符串1")
    private String clearTypeName;

    @ApiModelProperty(name="deliveryType",value="结算方式Code",example="字符串1")
    private String deliveryType;
    @ApiModelProperty(name="deliveryTypeName",value="结算方式",example="字符串1")
    private String deliveryTypeName;

    @ApiModelProperty(name="settCcy",value="结算币种Code",example="字符串1")
    private String settCcy;
    @ApiModelProperty(name="settCcyName",value="结算币种",example="字符串1")
    private String settCcyName;

    @ApiModelProperty(name="quoteid",value="报价编号",example="字符串1")
    private String quoteid;

    @ApiModelProperty(name="traderid",value="本方id",example="字符串1")
    private String traderid;
    @ApiModelProperty(name="traderidName",value="本方",example="字符串1")
    private String traderidName;

    @ApiModelProperty(name="trader",value="本方交易员",example="字符串1")
    private String trader;

    @ApiModelProperty(name="counterId",value="对手方id",example="字符串1")
    private String counterId;
    @ApiModelProperty(name="counterIdName",value="对手方",example="字符串1")
    private String counterIdName;
    @ApiModelProperty(name="counterTrader",value="对手方交易员",example="字符串1")
    private String counterTrader;

    @ApiModelProperty(name="remark",value="备注",example="字符串1")
    private String remark;

    @ApiModelProperty(name="finprodType2",value="资产类型CODE",example="A1")
    private String finprodType2;

    public String getExecid() {
        return execid;
    }

    public void setExecid(String execid) {
        this.execid = execid;
    }

    public Date getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(Date tradeDate) {
        this.tradeDate = tradeDate;
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

    public String getPs() {
        return ps;
    }

    public void setPs(String ps) {
        this.ps = ps;
    }

    public String getPsName() {
        return SysDataUtil.getNameFromDataDict(DictCode.交易方向, getPs());
    }

    public void setPsName(String psName) {
        this.psName = psName;
    }

    public String getSettleSpeed() {
        return settleSpeed;
    }

    public void setSettleSpeed(String settleSpeed) {
        this.settleSpeed = settleSpeed;
    }

    public String getSettleSpeedName() {
        return SysDataUtil.getNameFromDataDict(DictCode.清算速度, getSettleSpeed());
    }

    public void setSettleSpeedName(String settleSpeedName) {
        this.settleSpeedName = settleSpeedName;
    }

    public String getAcctNo() {
        return acctNo;
    }

    public void setAcctNo(String acctNo) {
        this.acctNo = acctNo;
    }

    public Date getSettleDate() {
        return settleDate;
    }

    public void setSettleDate(Date settleDate) {
        this.settleDate = settleDate;
    }

    public BigDecimal getUnitCprice() {
        return unitCprice;
    }

    public void setUnitCprice(BigDecimal unitCprice) {
        this.unitCprice = unitCprice;
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

    public BigDecimal getLastqty() {
        return lastqty;
    }

    public void setLastqty(BigDecimal lastqty) {
        this.lastqty = lastqty;
    }

    public BigDecimal getUnitInt() {
        return unitInt;
    }

    public void setUnitInt(BigDecimal unitInt) {
        this.unitInt = unitInt;
    }

    public BigDecimal getUnitFprice() {
        return unitFprice;
    }

    public void setUnitFprice(BigDecimal unitFprice) {
        this.unitFprice = unitFprice;
    }

    public BigDecimal getTradeAmt() {
        return tradeAmt;
    }

    public void setTradeAmt(BigDecimal tradeAmt) {
        this.tradeAmt = tradeAmt;
    }

    public BigDecimal getIntAmt() {
        return intAmt;
    }

    public void setIntAmt(BigDecimal intAmt) {
        this.intAmt = intAmt;
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

    public String getSettCcy() {
        return settCcy;
    }

    public void setSettCcy(String settCcy) {
        this.settCcy = settCcy;
    }

    public String getQuoteid() {
        return quoteid;
    }

    public void setQuoteid(String quoteid) {
        this.quoteid = quoteid;
    }

    public String getTraderid() {
        return traderid;
    }

    public void setTraderid(String traderid) {
        this.traderid = traderid;
    }

    public String getTrader() {
        return trader;
    }

    public void setTrader(String trader) {
        this.trader = trader;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public BigDecimal getMtradeAmt() {
        return mtradeAmt;
    }

    public void setMtradeAmt(BigDecimal mtradeAmt) {
        this.mtradeAmt = mtradeAmt;
    }

    public String getAcctNoName() {
        return acctNoName;
    }

    public void setAcctNoName(String acctNoName) {
        this.acctNoName = acctNoName;
    }

    public String getClearTypeName() {
        return SysDataUtil.getNameFromDataDict(DictCode.清算类型, getClearType());
    }

    public void setClearTypeName(String clearTypeName) {
        this.clearTypeName = clearTypeName;
    }

    public String getDeliveryTypeName() {
        return SysDataUtil.getNameFromDataDict(DictCode.结算方式, getDeliveryType());
    }

    public void setDeliveryTypeName(String deliveryTypeName) {
        this.deliveryTypeName = deliveryTypeName;
    }

    public String getSettCcyName() {
        return SysDataUtil.getNameFromDataDict(DictCode.币种, getSettCcy());
    }

    public void setSettCcyName(String settCcyName) {
        this.settCcyName = settCcyName;
    }

    public String getTraderidName() {
        return traderidName;
    }

    public void setTraderidName(String traderidName) {
        this.traderidName = traderidName;
    }

    public String getCounterIdName() {
        return counterIdName;
    }

    public void setCounterIdName(String counterIdName) {
        this.counterIdName = counterIdName;
    }

    public String getFinprodType2() {
        return finprodType2;
    }

    public void setFinprodType2(String finprodType2) {
        this.finprodType2 = finprodType2;
    }
}
