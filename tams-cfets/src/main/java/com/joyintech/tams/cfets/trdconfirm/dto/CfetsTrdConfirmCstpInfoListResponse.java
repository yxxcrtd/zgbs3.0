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


import com.joyintech.background.component.common.pack.common.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.constant.DictCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 银行间交易成交确认
 * 查询Cstp下行列表返回数据DTO
 * @author 1
 * @version 1.0 2021年10月26日
 * @see CfetsTrdConfirmCstpInfoListResponse
 * @since 1.0
 */
@ApiModel(value="查询Cstp下行列表返回数据",description="查询Cstp下行列表返回数据")
public class CfetsTrdConfirmCstpInfoListResponse {

    @ApiModelProperty(name="execid",value="成交编号",example="A1")
    private String execid;

    @ApiModelProperty(name="businesstypeName",value="资产类型",example="A1")
    private String businesstypeName;

    @ApiModelProperty(name="businesstype",value="资产类型Code",example="A1")
    private String  businesstype;


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


    @ApiModelProperty(name="lastqty",value="券面总额",example="1")
    private BigDecimal lastqty;

    @ApiModelProperty(name="tradeYield",value="成交价%",example="1")
    private BigDecimal tradeYield;

    @ApiModelProperty(name="tradeYieldStr",value="成交价+单位",example="1")
    private String tradeYieldStr;


    @ApiModelProperty(name="termDays",value="期限（天）",example="1")
    private BigDecimal termDays;

    @ApiModelProperty(name="tradeAmt",value="交易金额",example="1")
    private BigDecimal tradeAmt;

    @ApiModelProperty(name="quoteid",value="报价编号",example="T01")
    private String quoteid;

    @ApiModelProperty(name="traderid",value="本方",example="T01")
    private String traderid;
    @ApiModelProperty(name="traderidName",value="本方",example="T01")
    private String traderidName;

    @ApiModelProperty(name="trader",value="本方交易员",example="T01")
    private String trader;

    @ApiModelProperty(name="counterTrader",value="对手方交易员",example="T01")
    private String counterTrader;

    @ApiModelProperty(name="counterId",value="对手方Code",example="T01")
    private String counterId;
    @ApiModelProperty(name="counterIdName",value="对手方",example="T01")
    private String counterIdName;


    @ApiModelProperty(name="clearType",value="清算类型",example="T01")
    private String clearType;
    @ApiModelProperty(name="clearTypeName",value="清算类型",example="T01")
    private String clearTypeName;


    @ApiModelProperty(name="tradeDate",value="成交日期",example="T01")
    private Date tradeDate;

    @ApiModelProperty(name="remark",value="备注",example="T01")
    private String  remark;


    @ApiModelProperty(name="settleDate",value="结算日期",example="T01")
    private Date settleDate;

    @ApiModelProperty(name="tradeMarket",value="交易场所Code",example="T01")
    private String tradeMarket;
    @ApiModelProperty(name="vSettleDate",value="首期结算日",example="字符串1")
    private Date   vsettleDate;
    @ApiModelProperty(name="mSettleDate",value="到期结算日",example="字符串1")
    private Date   msettleDate;
    @ApiModelProperty(name="mtradeAmt",value="到期结算金额",example="字符串1")
    private BigDecimal   mtradeAmt;
    @ApiModelProperty(name="cstpTradeId",value="下行交易编号",example="字符串1")
    private String cstpTradeId;
    @ApiModelProperty(name="deliveryType",value="结算方式",example="字符串1")
    private String  deliveryType;
    @ApiModelProperty(name="pledgeFinprodIds",value="质押券债券",example="字符串1")
    private String   pledgeFinprodIds;
    @ApiModelProperty(name="pledgeRatios",value="质押券折算比例",example="字符串1")
    private String   pledgeRatios;
    @ApiModelProperty(name="pledgeFaceValues",value="质押券面值",example="字符串1")
    private String   pledgeFaceValues;

    @ApiModelProperty(name="pledgeFinprodId",value="质押券债券",example="字符串1")
    private String   pledgeFinprodId;
    @ApiModelProperty(name="pledgeRatio",value="质押券折算比例",example="字符串1")
    private BigDecimal   pledgeRatio;
    @ApiModelProperty(name="pledgeFaceValue",value="质押券面值",example="字符串1")
    private BigDecimal   pledgeFaceValue;
    public String getExecid() {
        return execid;
    }

    public void setExecid(String execid) {
        this.execid = execid;
    }

    public String getBusinesstypeName() {
        if (StringUtil.isEmpty(getBusinesstype())) {
            return StringUtil.EMPTY;
        }
        return SysDataUtil.getNameFromDataDict(DictCode.CSTP资产类型, getBusinesstype());
    }

    public void setBusinesstypeName(String businesstypeName) {
        this.businesstypeName = businesstypeName;
    }

    public String getBusinesstype() {
        return businesstype;
    }

    public void setBusinesstype(String businesstype) {
        this.businesstype = businesstype;
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

    public BigDecimal getLastqty() {
        return lastqty;
    }

    public void setLastqty(BigDecimal lastqty) {
        this.lastqty = lastqty;
    }

    public BigDecimal getTradeYield() {
        return tradeYield;
    }

    public void setTradeYield(BigDecimal tradeYield) {
        this.tradeYield = tradeYield;
    }

    public String getTradeYieldStr() {
        String unit = this.tradeYieldStr == null ? "" : this.tradeYieldStr;
        String tradeYield = "";
        if (getTradeYield() != null) {
            tradeYield = getTradeYield().toPlainString();
        }
        return tradeYield + unit;
    }

    public void setTradeYieldStr(String tradeYieldStr) {
        this.tradeYieldStr = tradeYieldStr;
    }

    public BigDecimal getTermDays() {
        return termDays;
    }

    public void setTermDays(BigDecimal termDays) {
        this.termDays = termDays;
    }

    public BigDecimal getTradeAmt() {
        return tradeAmt;
    }

    public void setTradeAmt(BigDecimal tradeAmt) {
        this.tradeAmt = tradeAmt;
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

    public String getCounterTrader() {
        return counterTrader;
    }

    public void setCounterTrader(String counterTrader) {
        this.counterTrader = counterTrader;
    }



    public Date getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(Date tradeDate) {
        this.tradeDate = tradeDate;
    }


    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date getSettleDate() {
        return settleDate;
    }

    public void setSettleDate(Date settleDate) {
        this.settleDate = settleDate;
    }

    public String getTradeMarket() {
        return tradeMarket;
    }

    public void setTradeMarket(String tradeMarket) {
        this.tradeMarket = tradeMarket;
    }

    public Date getVsettleDate() {
        return vsettleDate;
    }

    public void setVsettleDate(Date vsettleDate) {
        this.vsettleDate = vsettleDate;
    }

    public Date getMsettleDate() {
        return msettleDate;
    }

    public void setMsettleDate(Date msettleDate) {
        this.msettleDate = msettleDate;
    }

    public String getCstpTradeId() {
        return cstpTradeId;
    }

    public void setCstpTradeId(String cstpTradeId) {
        this.cstpTradeId = cstpTradeId;
    }

    public String getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(String deliveryType) {
        this.deliveryType = deliveryType;
    }

    public String getPledgeFinprodId() {
        return pledgeFinprodId;
    }

    public void setPledgeFinprodId(String pledgeFinprodId) {
        this.pledgeFinprodId = pledgeFinprodId;
    }

    public BigDecimal getPledgeRatio() {
        return pledgeRatio;
    }

    public void setPledgeRatio(BigDecimal pledgeRatio) {
        this.pledgeRatio = pledgeRatio;
    }

    public BigDecimal getPledgeFaceValue() {
        return pledgeFaceValue;
    }

    public void setPledgeFaceValue(BigDecimal pledgeFaceValue) {
        this.pledgeFaceValue = pledgeFaceValue;
    }

    public String getAcctNoName() {
        return acctNoName;
    }

    public void setAcctNoName(String acctNoName) {
        this.acctNoName = acctNoName;
    }

    public String getTraderidName() {
        return traderidName;
    }

    public void setTraderidName(String traderidName) {
        this.traderidName = traderidName;
    }

    public String getCounterId() {
        return counterId;
    }

    public void setCounterId(String counterId) {
        this.counterId = counterId;
    }

    public String getCounterIdName() {
        return counterIdName;
    }

    public void setCounterIdName(String counterIdName) {
        this.counterIdName = counterIdName;
    }

    public String getClearType() {
        return clearType;
    }

    public void setClearType(String clearType) {
        this.clearType = clearType;
    }

    public String getClearTypeName() {
        return SysDataUtil.getNameFromDataDict(DictCode.清算类型, getClearType());
    }

    public void setClearTypeName(String clearTypeName) {
        this.clearTypeName = clearTypeName;
    }

    public BigDecimal getMtradeAmt() {
        return mtradeAmt;
    }

    public void setMtradeAmt(BigDecimal mtradeAmt) {
        this.mtradeAmt = mtradeAmt;
    }

    public String getPledgeFinprodIds() {
        return pledgeFinprodIds;
    }

    public void setPledgeFinprodIds(String pledgeFinprodIds) {
        this.pledgeFinprodIds = pledgeFinprodIds;
    }

    public String getPledgeRatios() {
        return pledgeRatios;
    }

    public void setPledgeRatios(String pledgeRatios) {
        this.pledgeRatios = pledgeRatios;
    }

    public String getPledgeFaceValues() {
        return pledgeFaceValues;
    }

    public void setPledgeFaceValues(String pledgeFaceValues) {
        this.pledgeFaceValues = pledgeFaceValues;
    }
}
