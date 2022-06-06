/*
 * 文件名：CfetsTrdConfirmRegInfoListResponse.java
 * 版权：Copyright 2020-2022 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管系统V2.0
 * 修改人：
 * 修改时间：2021年01月25日
 * 修改内容：新建
 * 系统名称：理财资管系统V2.0
 */

package com.joyintech.tams.cfets.trdquery.dto;



import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.invest.common.constant.BussinessDictCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

/**
 * 银行间交易查询
 * 查询报价列表返回数据DTO
 * @author 1
 * @version 1.0 2021年10月26日
 * @see CfetsTrdQueryIbuInfoListResponse
 * @since 1.0
 */
@ApiModel(value="查询报价列表返回数据",description="查询报价列表返回数据")
public class CfetsTrdQueryIbuInfoListResponse {

    @ApiModelProperty(name="effectiveStatus",value="有效状态Code",example="A1")
    private String effectiveStatus;

    @ApiModelProperty(name="effectiveStatusStr",value="有效状态",example="A1")
    private String effectiveStatusStr;

    @ApiModelProperty(name="quotationState",value="报价状态Code",example="A1")
    private String quotationState;

    @ApiModelProperty(name="quotationStateStr",value="报价状态",example="A1")
    private String quotationStateStr;

    @ApiModelProperty(name="sendStatus",value="发送状态Code",example="A1")
    private String sendStatus;

    @ApiModelProperty(name="sendStatusStr",value="发送状态",example="A1")
    private String sendStatusStr;

    @ApiModelProperty(name="quoteRoleType",value="本方角色",example="A1")
    private String quoteRoleType;

    @ApiModelProperty(name="quoteRoleTypeStr",value="本方角色Str",example="A1")
    private String quoteRoleTypeStr;

    @ApiModelProperty(name="ps",value="交易方向Code",example="T01")
    private String ps;

    @ApiModelProperty(name="psStr",value="交易方向",example="T01")
    private String psStr;

    @ApiModelProperty(name="finprodType2",value="资产类型Code",example="A1")
    private String  finprodType2;

    @ApiModelProperty(name="finprodType2Str",value="资产类型",example="A1")
    private String  finprodType2Str;

    @ApiModelProperty(name="finprodId",value="资产代码Id",example="ZC1")
    private String finprodId;

    @ApiModelProperty(name="finprodMarketId",value="资产代码",example="ZC1")
    private String finprodMarketId;

    @ApiModelProperty(name="finprodAbbr",value="资产简称",example="ZC名")
    private String finprodAbbr;

    @ApiModelProperty(name="quoteType",value="报价方式Code",example="字符串1")
    private String quoteType;

    @ApiModelProperty(name="quoteTypeStr",value="报价方式",example="字符串1")
    private String quoteTypeStr;

    @ApiModelProperty(name="traderid",value="本方id",example="字符串1")
    private String traderid;

    @ApiModelProperty(name="traderidStr",value="本方",example="字符串1")
    private String traderidStr;

    @ApiModelProperty(name="trader",value="交易员",example="admin")
    private String trader;

    @ApiModelProperty(name="counterId",value="对手方Id",example="admin")
    private String counterId;

    @ApiModelProperty(name="counterIdStr",value="对手方",example="admin")
    private String counterIdStr;

    @ApiModelProperty(name="counterTrader",value="对手方交易员",example="字符串1")
    private String counterTrader;


    @ApiModelProperty(name="settleSpeed",value="清算速度Code",example="1")
    private String settleSpeed;

    @ApiModelProperty(name="settleSpeedStr",value="清算速度",example="1")
    private String settleSpeedStr;


    @ApiModelProperty(name="secManageAcctId",value="证券账户ID",example="1")
    private String secManageAcctId;

    @ApiModelProperty(name="secManageAcctIdStr",value="证券账户",example="1")
    private String secManageAcctIdStr;


    @ApiModelProperty(name="faceValue",value="券面总额",example="1")
    private BigDecimal faceValue;

    @ApiModelProperty(name="tradeYield",value="成交价",example="1")
    private String tradeYield;

    @ApiModelProperty(name="termDays",value="期限（天）",example="1")
    private BigDecimal termDays;

    @ApiModelProperty(name="tradeAmt",value="交易金额",example="1")
    private BigDecimal tradeAmt;

    @ApiModelProperty(name="quoteId",value="报价编号")
    private String quoteId;

    @ApiModelProperty(name="execid",value="成交编号")
    private String execid;


    @ApiModelProperty(name="portfolioIds",value="组合代码",example="字符串1")
    private String portfolioIds;

    @ApiModelProperty(name="portfolioNames",value="组合名称",example="字符串1")
    private String portfolioNames;

    @ApiModelProperty(name="entrustId",value="指令代码",example="01")
    private String entrustId;

    @ApiModelProperty(name="quoteDate",value="报价日期")
    private String quoteDate;

    @ApiModelProperty(name="tradeDate",value="成交日期")
    private String tradeDate;

    @ApiModelProperty(name="remark",value="备注",example="字符串1")
    private String remark;

    @ApiModelProperty(name="dialogueRecord",value="交谈记录信息",example="字符串1")
    private String dialogueRecord;

    @ApiModelProperty(name="settleDate",value="结算日期",example="字符串1")
    private String settleDate;

    @ApiModelProperty(name="ccy",value="币种",example="字符串1")
    private String ccy;

    @ApiModelProperty(name="ccyStr",value="币种Str",example="字符串1")
    private String ccyStr;

    @ApiModelProperty(name="unitCprice",value="单位净价",example="字符串1")
    private BigDecimal unitCprice;

    @ApiModelProperty(name="yield",value="到期收益率",example="字符串1")
    private BigDecimal yield;

    @ApiModelProperty(name="exerYield",value="行权收益率",example="字符串1")
    private BigDecimal exerYield;

    @ApiModelProperty(name="deliveryTypeStr",value="结算方式",example="字符串1")
    private String deliveryTypeStr;

    @ApiModelProperty(name="deliveryType",value="结算方式Code",example="字符串1")
    private String deliveryType;

    @ApiModelProperty(name="clearTypeStr",value="清算类型",example="字符串1")
    private String clearTypeStr;

    @ApiModelProperty(name="clearType",value="清算类型Code",example="字符串1")
    private String clearType;

    @ApiModelProperty(name="trdFeeAmt",value="交易手续费（元）",example="字符串1")
    private BigDecimal trdFeeAmt;

    @ApiModelProperty(name="settleFeeAmt",value="结算服务费（元）",example="字符串1")
    private BigDecimal settleFeeAmt;

    @ApiModelProperty(name="unitInt",value="应计利息(元)",example="字符串1")
    private BigDecimal unitInt;

    @ApiModelProperty(name="unitFprice",value="全价(元)",example="字符串1")
    private BigDecimal unitFprice;

    @ApiModelProperty(name="cpriceAmt",value="净价总额（元）",example="字符串1")
    private BigDecimal cpriceAmt;

    @ApiModelProperty(name="intAmt",value="应计利息总额（元）",example="字符串1")
    private BigDecimal intAmt;

    @ApiModelProperty(name="fpriceAmt",value="全价总额（元）",example="字符串1")
    private BigDecimal fpriceAmt;

    @ApiModelProperty(name="distriTypeStr",value="分配方式",example="字符串1")
    private String distriTypeStr;

    @ApiModelProperty(name="distriType",value="分配方式Code",example="字符串1")
    private String distriType;

    @ApiModelProperty(name="repoTerm",value="回购期限（天）",example="1")
    private BigDecimal repoTerm;



    @ApiModelProperty(name="secCcyStr",value="债券币种Code",example="字符串1")
    private String secCcyStr;

    @ApiModelProperty(name="secCcy",value="债券币种",example="字符串1")
    private String secCcy;

    @ApiModelProperty(name="settCcy",value="结算币种Code",example="字符串1")
    private String settCcy;

    @ApiModelProperty(name="settCcyStr",value="结算币种",example="字符串1")
    private String settCcyStr;

    @ApiModelProperty(name="disFaceValue",value="质押券折算总额",example="字符串1")
    private BigDecimal   disFaceValue;

    @ApiModelProperty(name="mdeliveryTypeStr",value="到期结算方式",example="字符串1")
    private String mdeliveryTypeStr;

    @ApiModelProperty(name="mdeliveryType",value="到期结算方式Code",example="字符串1")
    private String mdeliveryType;


    @ApiModelProperty(name="vSettleDate",value="首次结算日",example="字符串1")
    private String    vsettleDate;

    @ApiModelProperty(name="mSettleDate",value="到期结算日",example="字符串1")
    private String   msettleDate;

    @ApiModelProperty(name="mtradeAmt",value="到期结算金额",example="字符串1")
    private BigDecimal   mtradeAmt;

    @ApiModelProperty(name="actRepoTerm",value="实际占款天数",example="字符串1")
    private BigDecimal   actRepoTerm;

    @ApiModelProperty(name="termType",value="期限品种",example="ZC1")
    private String termType;

    @ApiModelProperty(name="calMode",value="计算方式",example="计算方式供前台判断使用")
    private String calMode;

    @ApiModelProperty(name="effectFlag",value="有效状态",example="02")
    private String effectFlag;

    public String getEffectiveStatus() {
        return effectiveStatus;
    }

    public void setEffectiveStatus(String effectiveStatus) {
        this.effectiveStatus = effectiveStatus;
    }

    public String getEffectiveStatusStr() {
        return SysDataUtil.getNameFromDataDict(BussinessDictCode.有效状态, getEffectiveStatus());
    }

    public void setEffectiveStatusStr(String effectiveStatusStr) {
        this.effectiveStatusStr = effectiveStatusStr;
    }

    public String getQuotationState() {
        return quotationState;
    }

    public void setQuotationState(String quotationState) {
        this.quotationState = quotationState;
    }

    public String getQuotationStateStr() {
        return SysDataUtil.getNameFromDataDict(BussinessDictCode.银行间成交状态, getQuotationState());
    }

    public void setQuotationStateStr(String quotationStateStr) {
        this.quotationStateStr = quotationStateStr;
    }

    public String getSendStatus() {
        return sendStatus;
    }

    public void setSendStatus(String sendStatus) {
        this.sendStatus = sendStatus;
    }

    public String getSendStatusStr() {
        return SysDataUtil.getNameFromDataDict(BussinessDictCode.银行间上行发送状态, getSendStatus());
    }

    public void setSendStatusStr(String sendStatusStr) {
        this.sendStatusStr = sendStatusStr;
    }

    public String getQuoteRoleType() {
        return quoteRoleType;
    }

    public void setQuoteRoleType(String quoteRoleType) {
        this.quoteRoleType = quoteRoleType;
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

    public String getFinprodType2() {
        return finprodType2;
    }

    public void setFinprodType2(String finprodType2) {
        this.finprodType2 = finprodType2;
    }

    public String getFinprodType2Str() {
        if (StringUtil.isEmpty(getFinprodType2())) {
            return null;
        }
        return SysDataUtil.getNameFromDataDict(DictCode.登记资产类型, getFinprodType2());
    }

    public void setFinprodType2Str(String finprodType2Str) {
        this.finprodType2Str = finprodType2Str;
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

    public String getQuoteType() {
        return quoteType;
    }

    public void setQuoteType(String quoteType) {
        this.quoteType = quoteType;
    }

    public String getQuoteTypeStr() {
        return SysDataUtil.getNameFromDataDict(BussinessDictCode.报价方式, getQuoteType());
    }

    public void setQuoteTypeStr(String quoteTypeStr) {
        this.quoteTypeStr = quoteTypeStr;
    }

    public String getTraderid() {
        return traderid;
    }

    public void setTraderid(String traderid) {
        this.traderid = traderid;
    }

    public String getTraderidStr() {
        return traderidStr;
    }

    public void setTraderidStr(String traderidStr) {
        this.traderidStr = traderidStr;
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

    public String getCounterIdStr() {
        return counterIdStr;
    }

    public void setCounterIdStr(String counterIdStr) {
        this.counterIdStr = counterIdStr;
    }

    public String getCounterTrader() {
        return counterTrader;
    }

    public void setCounterTrader(String counterTrader) {
        this.counterTrader = counterTrader;
    }

    public String getSettleSpeed() {
        return settleSpeed;
    }

    public void setSettleSpeed(String settleSpeed) {
        this.settleSpeed = settleSpeed;
    }

    public String getSettleSpeedStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.清算速度, this.getSettleSpeed());
    }

    public void setSettleSpeedStr(String settleSpeedStr) {
        this.settleSpeedStr = settleSpeedStr;
    }

    public String getSecManageAcctId() {
        return secManageAcctId;
    }

    public void setSecManageAcctId(String secManageAcctId) {
        this.secManageAcctId = secManageAcctId;
    }

    public String getSecManageAcctIdStr() {
        return secManageAcctIdStr;
    }

    public void setSecManageAcctIdStr(String secManageAcctIdStr) {
        this.secManageAcctIdStr = secManageAcctIdStr;
    }

    public BigDecimal getFaceValue() {
        return faceValue;
    }

    public void setFaceValue(BigDecimal faceValue) {
        this.faceValue = faceValue;
    }

    public String getTradeYield() {
        return tradeYield;
    }

    public void setTradeYield(String tradeYield) {
        this.tradeYield = tradeYield;
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

    public String getQuoteId() {
        return quoteId;
    }

    public void setQuoteId(String quoteId) {
        this.quoteId = quoteId;
    }

    public String getExecid() {
        return execid;
    }

    public void setExecid(String execid) {
        this.execid = execid;
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

    public String getEntrustId() {
        return entrustId;
    }

    public void setEntrustId(String entrustId) {
        this.entrustId = entrustId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getDialogueRecord() {
        return dialogueRecord;
    }

    public void setDialogueRecord(String dialogueRecord) {
        this.dialogueRecord = dialogueRecord;
    }


    public String getCcy() {
        return ccy;
    }

    public void setCcy(String ccy) {
        this.ccy = ccy;
    }

    public String getCcyStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.币种, this.getCcy());
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

    public String getDeliveryTypeStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.结算方式, this.getDeliveryType());
    }

    public void setDeliveryTypeStr(String deliveryTypeStr) {
        this.deliveryTypeStr = deliveryTypeStr;
    }

    public String getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(String deliveryType) {
        this.deliveryType = deliveryType;
    }

    public String getClearTypeStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.清算类型, this.getClearType());
    }

    public void setClearTypeStr(String clearTypeStr) {
        this.clearTypeStr = clearTypeStr;
    }

    public String getClearType() {
        return clearType;
    }

    public void setClearType(String clearType) {
        this.clearType = clearType;
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

    public String getDistriTypeStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.分配方式, this.getDistriType());
    }

    public void setDistriTypeStr(String distriTypeStr) {
        this.distriTypeStr = distriTypeStr;
    }

    public String getDistriType() {
        return distriType;
    }

    public void setDistriType(String distriType) {
        this.distriType = distriType;
    }

    public BigDecimal getRepoTerm() {
        return repoTerm;
    }

    public void setRepoTerm(BigDecimal repoTerm) {
        this.repoTerm = repoTerm;
    }

    public String getSecCcyStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.币种, this.getSecCcy());
    }

    public void setSecCcyStr(String secCcyStr) {
        this.secCcyStr = secCcyStr;
    }

    public String getSecCcy() {
        return secCcy;
    }

    public void setSecCcy(String secCcy) {
        this.secCcy = secCcy;
    }

    public String getSettCcy() {
        return settCcy;
    }

    public void setSettCcy(String settCcy) {
        this.settCcy = settCcy;
    }

    public String getSettCcyStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.币种, this.getSettCcy());
    }

    public void setSettCcyStr(String settCcyStr) {
        this.settCcyStr = settCcyStr;
    }

    public BigDecimal getDisFaceValue() {
        return disFaceValue;
    }

    public void setDisFaceValue(BigDecimal disFaceValue) {
        this.disFaceValue = disFaceValue;
    }

    public String getMdeliveryTypeStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.结算方式, this.getMdeliveryType());
    }

    public void setMdeliveryTypeStr(String mdeliveryTypeStr) {
        this.mdeliveryTypeStr = mdeliveryTypeStr;
    }

    public String getMdeliveryType() {
        return mdeliveryType;
    }

    public void setMdeliveryType(String mdeliveryType) {
        this.mdeliveryType = mdeliveryType;
    }

    public BigDecimal getMtradeAmt() {
        return mtradeAmt;
    }

    public void setMtradeAmt(BigDecimal mtradeAmt) {
        this.mtradeAmt = mtradeAmt;
    }

    public BigDecimal getActRepoTerm() {
        return actRepoTerm;
    }

    public void setActRepoTerm(BigDecimal actRepoTerm) {
        this.actRepoTerm = actRepoTerm;
    }

    public String getTermType() {
        return termType;
    }

    public void setTermType(String termType) {
        this.termType = termType;
    }

    public String getQuoteRoleTypeStr() {
        return SysDataUtil.getNameFromDataDict(BussinessDictCode.银行间上行本方角色, getQuoteRoleType());
    }
    public void setQuoteRoleTypeStr(String quoteRoleTypeStr) {
        this.quoteRoleTypeStr = quoteRoleTypeStr;
    }

    public String getQuoteDate() {
        return quoteDate;
    }

    public void setQuoteDate(String quoteDate) {
        this.quoteDate = quoteDate;
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

    public String getVsettleDate() {
        return vsettleDate;
    }

    public void setVsettleDate(String vsettleDate) {
        this.vsettleDate = vsettleDate;
    }

    public String getMsettleDate() {
        return msettleDate;
    }

    public void setMsettleDate(String msettleDate) {
        this.msettleDate = msettleDate;
    }

    public String getCalMode() {
        return calMode;
    }

    public void setCalMode(String calMode) {
        this.calMode = calMode;
    }

    public String getEffectFlag() {
        return effectFlag;
    }

    public void setEffectFlag(String effectFlag) {
        this.effectFlag = effectFlag;
    }
}
