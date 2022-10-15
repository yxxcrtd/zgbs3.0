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
import com.joyintech.tams.invest.common.constant.BussinessDictCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 银行间交易成交确认
 * 交易回购执行返回数据DTO
 * @author 1
 * @version 1.0 2021年10月26日
 * @see CfetsTrdConfirmExecRepoInfoResponse
 * @since 1.0
 */
@ApiModel(value="查询交易回购执行列表返回数据",description="查询交易回购执行列表返回数据")
public class CfetsTrdConfirmExecRepoInfoResponse {

    @ApiModelProperty(name="busiCode",value="业务编号",example="1")
    private String busiCode;

    @ApiModelProperty(name="tradeDate",value="执行日期",example="admin")
    private Date tradeDate;

    @ApiModelProperty(name="busiType",value="交易类型Code",example="A1")
    private String busiType;

    @ApiModelProperty(name="busiTypeStr",value="交易类型",example="A1")
    private String busiTypeStr;

    @ApiModelProperty(name="termType",value="期限品种",example="ZC1")
    private String termType;

    @ApiModelProperty(name="ps",value="交易方向Code",example="T01")
    private String ps;

    @ApiModelProperty(name="psName",value="交易方向",example="T01")
    private String psName;

    @ApiModelProperty(name="secManageAcctId",value="证券账户ID",example="1")
    private String secManageAcctId;

    @ApiModelProperty(name="secManageAcctIdName",value="证券账户",example="1")
    private String secManageAcctIdName;

    @ApiModelProperty(name="settleSpeed",value="清算速度Code",example="1")
    private String settleSpeed;

    @ApiModelProperty(name="settleSpeedName",value="清算速度",example="1")
    private String settleSpeedName;

    @ApiModelProperty(name="repoTerm",value="回购期限（天）",example="1")
    private BigDecimal repoTerm;

    @ApiModelProperty(name="settleDate",value="结算日期",example="字符串1")
    private Date   settleDate;

    @ApiModelProperty(name="vSettleDate",value="首次结算日",example="字符串1")
    private Date   vsettleDate;

    @ApiModelProperty(name="mSettleDate",value="到期结算日",example="字符串1")
    private Date   msettleDate;

    @ApiModelProperty(name="actRepoTerm",value="实际占款天数",example="字符串1")
    private BigDecimal   actRepoTerm;

    @ApiModelProperty(name="tradeYield",value="回购利率(%)",example="字符串1")
    private BigDecimal   tradeYield;

    @ApiModelProperty(name="tradeAmt",value="交易金额（元）",example="字符串1")
    private BigDecimal   tradeAmt;

    @ApiModelProperty(name="intAmt",value="回购利息（元）",example="字符串1")
    private BigDecimal   intAmt;

    @ApiModelProperty(name="mtradeAmt",value="到期结算金额（元）",example="字符串1")
    private BigDecimal   mtradeAmt;

    @ApiModelProperty(name="trdFeeAmt",value="交易手续费（元）",example="字符串1")
    private BigDecimal trdFeeAmt;

    @ApiModelProperty(name="settleFeeAmt",value="结算服务费（元）",example="字符串1")
    private BigDecimal settleFeeAmt;

    @ApiModelProperty(name="portfolioIds",value="组合代码",example="字符串1")
    private String  portfolioIds;

    @ApiModelProperty(name="portfolioNames",value="组合名称",example="字符串1")
    private String portfolioNames;

    @ApiModelProperty(name="clearType",value="清算类型Code",example="字符串1")
    private String clearType;
    @ApiModelProperty(name="clearTypeName",value="清算类型",example="字符串1")
    private String clearTypeName;

    @ApiModelProperty(name="deliveryType",value="首次结算方式Code",example="字符串1")
    private String deliveryType;
    @ApiModelProperty(name="deliveryTypeName",value="首次结算方式",example="字符串1")
    private String deliveryTypeName;

    @ApiModelProperty(name="mdeliveryType",value="到期结算方式Code",example="字符串1")
    private String mdeliveryType;

    @ApiModelProperty(name="mdeliveryTypeName",value="到期结算方式",example="字符串1")
    private String mdeliveryTypeName;

    @ApiModelProperty(name="ccy",value="币种Code",example="字符串1")
    private String ccy;
    @ApiModelProperty(name="ccyName",value="币种",example="字符串1")
    private String ccyName;

    @ApiModelProperty(name="counterId",value="对手方Code",example="字符串1")
    private String counterId;
    @ApiModelProperty(name="counterIdName",value="对手方",example="字符串1")
    private String counterIdName;

    @ApiModelProperty(name="counterTrader",value="对手方交易员",example="字符串1")
    private String counterTrader;

    @ApiModelProperty(name="remark",value="备注",example="字符串1")
    private String remark;

    @ApiModelProperty(name="totleTradeId",value="登记编号/报价内部编号",example="1")
    private String totleTradeId;

    public String getBusiCode() {
        return busiCode;
    }

    public void setBusiCode(String busiCode) {
        this.busiCode = busiCode;
    }

    public Date getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(Date tradeDate) {
        this.tradeDate = tradeDate;
    }

    public String getBusiType() {
        return busiType;
    }

    public void setBusiType(String busiType) {
        this.busiType = busiType;
    }

    public String getBusiTypeStr() {
        return SysDataUtil.getNameFromDataDict(BussinessDictCode.交易类型, getBusiType());
    }

    public void setBusiTypeStr(String busiTypeStr) {
        this.busiTypeStr = busiTypeStr;
    }

    public String getTermType() {
        return termType;
    }

    public void setTermType(String termType) {
        this.termType = termType;
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

    public String getSecManageAcctId() {
        return secManageAcctId;
    }

    public void setSecManageAcctId(String secManageAcctId) {
        this.secManageAcctId = secManageAcctId;
    }

    public String getSecManageAcctIdName() {
        return secManageAcctIdName;
    }

    public void setSecManageAcctIdName(String secManageAcctIdName) {
        this.secManageAcctIdName = secManageAcctIdName;
    }

    public String getSettleSpeed() {
        return settleSpeed;
    }

    public void setSettleSpeed(String settleSpeed) {
        this.settleSpeed = settleSpeed;
    }

    public String getSettleSpeedName() {
        return "T+" + this.getSettleSpeed();
       // return SysDataUtil.getNameFromDataDict(DictCode.清算速度, getSettleSpeed());
    }

    public void setSettleSpeedName(String settleSpeedName) {
        this.settleSpeedName = settleSpeedName;
    }

    public BigDecimal getRepoTerm() {
        return repoTerm;
    }

    public void setRepoTerm(BigDecimal repoTerm) {
        this.repoTerm = repoTerm;
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

    public BigDecimal getActRepoTerm() {
        return actRepoTerm;
    }

    public void setActRepoTerm(BigDecimal actRepoTerm) {
        this.actRepoTerm = actRepoTerm;
    }

    public BigDecimal getTradeYield() {
        return tradeYield;
    }

    public void setTradeYield(BigDecimal tradeYield) {
        this.tradeYield = tradeYield;
    }

    public BigDecimal getTradeAmt() {
        return tradeAmt;
    }

    public void setTradeAmt(BigDecimal tradeAmt) {
        this.tradeAmt = tradeAmt;
    }


    public BigDecimal getTrdFeeAmt() {
        return trdFeeAmt;
    }

    public void setTrdFeeAmt(BigDecimal trdFeeAmt) {
        this.trdFeeAmt = trdFeeAmt;
    }

    public BigDecimal getSettleFeeAmt() {
        return settleFeeAmt;
    }

    public void setSettleFeeAmt(BigDecimal settleFeeAmt) {
        this.settleFeeAmt = settleFeeAmt;
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

    public String getMdeliveryType() {
        return mdeliveryType;
    }

    public void setMdeliveryType(String mdeliveryType) {
        this.mdeliveryType = mdeliveryType;
    }

    public String getCcy() {
        return ccy;
    }

    public void setCcy(String ccy) {
        this.ccy = ccy;
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

    public BigDecimal getIntAmt() {
        return intAmt;
    }

    public void setIntAmt(BigDecimal intAmt) {
        this.intAmt = intAmt;
    }

    public BigDecimal getMtradeAmt() {
        return mtradeAmt;
    }

    public void setMtradeAmt(BigDecimal mtradeAmt) {
        this.mtradeAmt = mtradeAmt;
    }

    public String getPortfolioIds() {
        return portfolioIds;
    }

    public void setPortfolioIds(String portfolioIds) {
        this.portfolioIds = portfolioIds;
    }

    public String getPortfolioNames() {
        return portfolioNames;
    }

    public void setPortfolioNames(String portfolioNames) {
        this.portfolioNames = portfolioNames;
    }

    public Date getSettleDate() {
        return settleDate;
    }

    public void setSettleDate(Date settleDate) {
        this.settleDate = settleDate;
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

    public String getMdeliveryTypeName() {
        return SysDataUtil.getNameFromDataDict(DictCode.结算方式, getMdeliveryType());
    }

    public void setMdeliveryTypeName(String mdeliveryTypeName) {
        this.mdeliveryTypeName = mdeliveryTypeName;
    }

    public String getCcyName() {
        return SysDataUtil.getNameFromDataDict(DictCode.币种, getCcy());
    }

    public void setCcyName(String ccyName) {
        this.ccyName = ccyName;
    }

    public String getCounterIdName() {
        return counterIdName;
    }

    public void setCounterIdName(String counterIdName) {
        this.counterIdName = counterIdName;
    }

    public String getTotleTradeId() {
        return totleTradeId;
    }

    public void setTotleTradeId(String totleTradeId) {
        this.totleTradeId = totleTradeId;
    }
}
