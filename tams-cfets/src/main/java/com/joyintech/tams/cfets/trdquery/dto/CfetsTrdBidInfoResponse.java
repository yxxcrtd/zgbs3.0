/*
 * 文件名：CfetsTrdQueryRegInfoListResponse.java
 * 版权：Copyright 2020-2022 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管系统V2.0
 * 修改人：
 * 修改时间：2021年01月25日
 * 修改内容：新建
 * 系统名称：理财资管系统V2.0
 */

package com.joyintech.tams.cfets.trdquery.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.constant.DictCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 银行间交易成交确认
 * 查询交易登记列表返回数据DTO
 * @author 1
 * @version 1.0 2021年10月26日
 * @see CfetsTrdBidInfoResponse
 * @since 1.0
 */
@ApiModel(value="交收管理成交数据",description="交收管理成交数据")
public class CfetsTrdBidInfoResponse {

    @ApiModelProperty(name="bidDate",value="投标日",example="A1")
    private String bidDate;

    @ApiModelProperty(name="bidDateStart",value="投标开始日",example="A1")
    private Date bidDateStart;

    @ApiModelProperty(name="bidDateEnd",value="投标结束日",example="A1")
    private Date bidDateEnd;

    @ApiModelProperty(name="finprodId",value="资产代码Id",example="ZC1")
    private String finprodId;

    @ApiModelProperty(name="finprodMarketId",value="资产代码",example="ZC1")
    private String finprodMarketId;

    @ApiModelProperty(name="finprodAbbr",value="资产简称",example="ZC名")
    private String finprodAbbr;

    @ApiModelProperty(name="counterId",value="对手方Code",example="admin")
    private String counterId;

    @ApiModelProperty(name="counterIdStr",value="对手方",example="admin")
    private String counterIdStr;

    @ApiModelProperty(name="tenderTarget",value="招标标的",example="ZC1")
    private String tenderTarget;

    @ApiModelProperty(name="tenderTarget",value="招标标的",example="ZC1")
    private String tenderTargetStr;

    @ApiModelProperty(name="ccy",value="币种",example="字符串1")
    private String ccy;

    @ApiModelProperty(name="ccyStr",value="币种Str",example="字符串1")
    private String ccyStr;

    @ApiModelProperty(name="tradeMarket",value="交易场所Code",example="T01")
    private String tradeMarket;

    @ApiModelProperty(name="tradeMarketStr",value="交易场所",example="T01")
    private String tradeMarketStr;

    @ApiModelProperty(name="tradeMarketMode",value="交易市场",example="ZC1")
    private String tradeMarketMode;

    @ApiModelProperty(name="tradeMarketModeStr",value="交易市场",example="ZC1")
    private String tradeMarketModeStr;

    @ApiModelProperty(name="payEdate",value="缴款截止日")
    private Date payEdate;

    @ApiModelProperty(name="payDate",value="缴款日")
    private Date payDate;

    @ApiModelProperty(name="assignRegdate",value="债权债务登记日")
    private Date assignRegdate;

    @ApiModelProperty(name="onmarketDate",value="上市日")
    private Date onmarketDate;

    @ApiModelProperty(name="bidModle",value="投标方式iD")
    private String bidModle;

    @ApiModelProperty(name="bidModleStr",value="投标方式")
    private String bidModleStr;

    @ApiModelProperty(name="payType",value="缴款方式id")
    private String payType;

    @ApiModelProperty(name="payTypeStr",value="缴款方式")
    private String payTypeStr;

    @ApiModelProperty(name="bidPrinAmt",value="指令投标券面总额")
    private BigDecimal bidPrinAmt;

    @ApiModelProperty(name="prinAmt",value="券面总额")
    private BigDecimal prinAmt;

    @ApiModelProperty(name="winPrice",value="中标标位")
    private BigDecimal winPrice;

    @ApiModelProperty(name="unitCprice",value="中标净价")
    private BigDecimal unitCprice;

    @ApiModelProperty(name="unitInt",value="应计利息(元)",example="字符串1")
    private BigDecimal unitInt;

    @ApiModelProperty(name="settleFeeAmt",value="结算服务费（元）",example="字符串1")
    private BigDecimal settleFeeAmt;

    @ApiModelProperty(name="bidId",value="投标编号")
    private String bidId;

    @ApiModelProperty(name="remark",value="备注")
    private String remark;

    @ApiModelProperty(name="unitFprice",value="全价(元)",example="字符串1")
    private BigDecimal unitFprice;

    @ApiModelProperty(name="cpriceAmt",value="净价总额（元）",example="字符串1")
    private BigDecimal cpriceAmt;

    @ApiModelProperty(name="intAmt",value="应计利息总额（元）",example="字符串1")
    private BigDecimal intAmt;

    @ApiModelProperty(name="tradeAmt",value="缴款金额")
    private BigDecimal tradeAmt;

    @ApiModelProperty(name="ouPartyId",value="本方")
    private String ouPartyId;

    @ApiModelProperty(name="ouPartyIdStr",value="本方")
    private String ouPartyIdStr;

    @ApiModelProperty(name="trader",value="本方交易员ID")
    private String trader;

    @ApiModelProperty(name="traderidStr",value="本方交易员")
    private String traderidStr;

    @ApiModelProperty(name="exeId",value="成交编号")
    private String exeId;

    @ApiModelProperty(name="tradeDate",value="成交日期")
    private Date tradeDate;

    @ApiModelProperty(name="couponSpecies",value="息票品种")
    private String couponSpecies;

    @ApiModelProperty(name="couponSpecies",value="息票品种")
    private String couponSpeciesStr;

    @ApiModelProperty(name="intType",value="利率类型ID")
    private String intType;

    @ApiModelProperty(name="intTypeStr",value="利率类型")
    private String intTypeStr;

    @ApiModelProperty(name="ps",value="交易方向id")
    private String ps;

    @ApiModelProperty(name="psStr",value="交易方向")
    private String psStr;

    @ApiModelProperty(name="tradePlat",value="交易平台Id")
    private String tradePlat;

    @ApiModelProperty(name="tradePlaStr",value="交易平台")
    private String tradePlaStr;

    @ApiModelProperty(name="issueDate",value="发行日")
    private Date issueDate;

    @ApiModelProperty(name="issuer",value="发行人")
    private String issuer;

    @ApiModelProperty(name="issuePrice",value="发行价格")
    private BigDecimal issuePrice;

    @ApiModelProperty(name="rate",value="参考收益率")
    private BigDecimal rate;

    @ApiModelProperty(name="rate",value="参考收益率")
    private String rateStr;

    @ApiModelProperty(name="rateYleid",value="票面利率")
    private BigDecimal rateYleid;

    @ApiModelProperty(name="payableAmount",value="应缴款金额")
    private BigDecimal payableAmount;

    @ApiModelProperty(name="totalWinPrinAmt",value="汇总投标券面总额")
    private BigDecimal totalWinPrinAmt;

    @ApiModelProperty(name="subAmt",value="认购量")
    private BigDecimal subAmt;

    public String getTradeMarketModeStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.交易市场, getTenderTarget());
    }

    public void setTradeMarketModeStr(String tradeMarketModeStr) {
        this.tradeMarketModeStr = tradeMarketModeStr;
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

    public String getTradeMarket() {
        return tradeMarket;
    }

    public void setTradeMarket(String tradeMarket) {
        this.tradeMarket = tradeMarket;
    }

    public String getTradeMarketStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.交易场所, getTradeMarket());
    }

    public void setTradeMarketStr(String tradeMarketStr) {
        this.tradeMarketStr = tradeMarketStr;
    }

    public String getBidDate() {
        return DateUtil.format(this.getBidDateStart(),DateUtil.DEFAULT_DATE_FORMAT_EN)+"~"+DateUtil.format(this.getBidDateEnd(),DateUtil.DEFAULT_DATE_FORMAT_EN);
    }

    public void setBidDate(String bidDate) {
        this.bidDate = bidDate;
    }

    public String getCounterId() {
        return counterId;
    }

    public void setCounterId(String counterId) {
        this.counterId = counterId;
    }

    public String getCounterIdStr() {
        return counterIdStr;
    }

    public void setCounterIdStr(String counterIdStr) {
        this.counterIdStr = counterIdStr;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCcyStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.币种, getCcy());
    }

    public void setCcyStr(String ccyStr) {
        this.ccyStr = ccyStr;
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

    public String getCcy() {
        return ccy;
    }

    public void setCcy(String ccy) {
        this.ccy = ccy;
    }

    public Date getBidDateStart() {
        return bidDateStart;
    }

    public void setBidDateStart(Date bidDateStart) {
        this.bidDateStart = bidDateStart;
    }

    public Date getBidDateEnd() {
        return bidDateEnd;
    }

    public void setBidDateEnd(Date bidDateEnd) {
        this.bidDateEnd = bidDateEnd;
    }

    public String getTenderTarget() {
        return tenderTarget;
    }

    public void setTenderTarget(String tenderTarget) {
        this.tenderTarget = tenderTarget;
    }

    public String getTradeMarketMode() {
        return tradeMarketMode;
    }

    public void setTradeMarketMode(String tradeMarketMode) {
        this.tradeMarketMode = tradeMarketMode;
    }

    public Date getPayDate() {
        return payDate;
    }

    public void setPayDate(Date payDate) {
        this.payDate = payDate;
    }

    public Date getAssignRegdate() {
        return assignRegdate;
    }

    public void setAssignRegdate(Date assignRegdate) {
        this.assignRegdate = assignRegdate;
    }

    public Date getOnmarketDate() {
        return onmarketDate;
    }

    public void setOnmarketDate(Date onmarketDate) {
        this.onmarketDate = onmarketDate;
    }

    public String getBidModle() {
        return bidModle;
    }

    public void setBidModle(String bidModle) {
        this.bidModle = bidModle;
    }

    public String getBidModleStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.投标方式, getTenderTarget());
    }

    public void setBidModleStr(String bidModleStr) {
        this.bidModleStr = bidModleStr;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public String getPayTypeStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.缴款方式, getTenderTarget());
    }

    public void setPayTypeStr(String payTypeStr) {
        this.payTypeStr = payTypeStr;
    }

    public BigDecimal getBidPrinAmt() {
        return bidPrinAmt;
    }

    public void setBidPrinAmt(BigDecimal bidPrinAmt) {
        this.bidPrinAmt = bidPrinAmt;
    }

    public BigDecimal getPrinAmt() {
        return prinAmt;
    }

    public void setPrinAmt(BigDecimal prinAmt) {
        this.prinAmt = prinAmt;
    }

    public BigDecimal getWinPrice() {
        return winPrice;
    }

    public void setWinPrice(BigDecimal winPrice) {
        this.winPrice = winPrice;
    }

    public BigDecimal getUnitInt() {
        return unitInt;
    }

    public void setUnitInt(BigDecimal unitInt) {
        this.unitInt = unitInt;
    }

    public BigDecimal getSettleFeeAmt() {
        return settleFeeAmt;
    }

    public void setSettleFeeAmt(BigDecimal settleFeeAmt) {
        this.settleFeeAmt = settleFeeAmt;
    }

    public String getBidId() {
        return bidId;
    }

    public void setBidId(String bidId) {
        this.bidId = bidId;
    }

    public BigDecimal getTradeAmt() {
        return tradeAmt;
    }

    public void setTradeAmt(BigDecimal tradeAmt) {
        this.tradeAmt = tradeAmt;
    }

    public String getOuPartyId() {
        return ouPartyId;
    }

    public void setOuPartyId(String ouPartyId) {
        this.ouPartyId = ouPartyId;
    }

    public String getTrader() {
        return trader;
    }

    public void setTrader(String trader) {
        this.trader = trader;
    }

    public String getExeId() {
        return exeId;
    }

    public void setExeId(String exeId) {
        this.exeId = exeId;
    }

    public Date getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(Date tradeDate) {
        this.tradeDate = tradeDate;
    }

    public String getCouponSpecies() {
        return couponSpecies;
    }

    public void setCouponSpecies(String couponSpecies) {
        this.couponSpecies = couponSpecies;
    }

    public String getIntType() {
        return intType;
    }

    public void setIntType(String intType) {
        this.intType = intType;
    }

    public String getIntTypeStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.利率类型, getIntType());
    }

    public void setIntTypeStr(String intTypeStr) {
        this.intTypeStr = intTypeStr;
    }

    public String getPs() {
        return ps;
    }

    public void setPs(String ps) {
        this.ps = ps;
    }

    public String getPsStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.交易方向, getPs());
    }

    public void setPsStr(String psStr) {
        this.psStr = psStr;
    }

    public String getTradePlat() {
        return tradePlat;
    }

    public void setTradePlat(String tradePlat) {
        this.tradePlat = tradePlat;
    }

    public String getTradePlaStr() {
         return SysDataUtil.getNameFromDataDict(DictCode.交易平台, getTradePlat());
    }

    public void setTradePlaStr(String tradePlaStr) {
        this.tradePlaStr = tradePlaStr;
    }

    public Date getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public BigDecimal getIssuePrice() {
        return issuePrice;
    }

    public void setIssuePrice(BigDecimal issuePrice) {
        this.issuePrice = issuePrice;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public BigDecimal getSubAmt() {
        return subAmt;
    }

    public void setSubAmt(BigDecimal subAmt) {
        this.subAmt = subAmt;
    }

    public String getTenderTargetStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.招标标的, getTenderTarget());
    }

    public void setTenderTargetStr(String tenderTargetStr) {
        this.tenderTargetStr = tenderTargetStr;
    }

    public String getCouponSpeciesStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.息票品种, getTenderTarget());
    }

    public void setCouponSpeciesStr(String couponSpeciesStr) {
        this.couponSpeciesStr = couponSpeciesStr;
    }

    public String getTraderidStr() {
        return traderidStr;
    }

    public void setTraderidStr(String traderidStr) {
        this.traderidStr = traderidStr;
    }

    @JsonFormat(pattern="yyyy-MM-dd",timezone = "GMT+8")
    public Date getPayEdate() {
        return payEdate;
    }

    public void setPayEdate(Date payEdate) {
        this.payEdate = payEdate;
    }

    public String getRateStr() {
        return rateStr;
    }

    public void setRateStr(String rateStr) {
        this.rateStr = rateStr;
    }

    public BigDecimal getRateYleid() {
        return rateYleid;
    }

    public void setRateYleid(BigDecimal rateYleid) {
        this.rateYleid = rateYleid;
    }

    public BigDecimal getPayableAmount() {
        return payableAmount;
    }

    public void setPayableAmount(BigDecimal payableAmount) {
        this.payableAmount = payableAmount;
    }

    public BigDecimal getTotalWinPrinAmt() {
        return totalWinPrinAmt;
    }

    public void setTotalWinPrinAmt(BigDecimal totalWinPrinAmt) {
        this.totalWinPrinAmt = totalWinPrinAmt;
    }

    public String getOuPartyIdStr() {
        return ouPartyIdStr;
    }

    public void setOuPartyIdStr(String ouPartyIdStr) {
        this.ouPartyIdStr = ouPartyIdStr;
    }
}
