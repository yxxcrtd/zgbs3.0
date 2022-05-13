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


import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.invest.common.constant.BussinessDictCode;
import com.joyintech.tams.invest.entrust.etrinvestbondinfo.dto.EtrInvestBondInfoDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 银行间交易成交确认
 * 查询成交确认分页返回数据
 * @author 1
 * @version 1.0 2021年10月26日
 * @see CfetsTrdConfirmTrdCstpMatchResponse
 * @since 1.0
 */
@ApiModel(value="查询匹配确认分页返回数据",description="查询匹配确认分页返回数据")
public class CfetsTrdConfirmTrdCstpMatchResponse {

    @ApiModelProperty(name="matchTypeName",value="匹配方式",example="A1")
    private String matchTypeName;

    @ApiModelProperty(name="matchType",value="匹配方式Code",example="A1")
    private String matchType;

    @ApiModelProperty(name="busiCode",value="业务编号",example="A1")
    private String busiCode;

    @ApiModelProperty(name="execid",value="成交编号",example="字符串1")
    private String execid;

    @ApiModelProperty(name="finprodType2",value="资产类型Code",example="A1")
    private String  finprodType2;

    @ApiModelProperty(name="finprodType2Name",value="资产类型",example="A1")
    private String  finprodType2Name;

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

    @ApiModelProperty(name="secManageAcctId",value="证券账户ID",example="1")
    private String secManageAcctId;

    @ApiModelProperty(name="secManageAcctIdName",value="证券账户",example="1")
    private String secManageAcctIdName;

    @ApiModelProperty(name="printAmt",value="券面总额",example="1")
    private BigDecimal printAmt;

    @ApiModelProperty(name="tradeYield",value="成交价",example="1")
    private String tradeYield;

    @ApiModelProperty(name="termDays",value="期限（天）",example="1")
    private BigDecimal termDays;

    @ApiModelProperty(name="tradeAmt",value="交易金额",example="1")
    private BigDecimal tradeAmt;

    @ApiModelProperty(name="tradeDate",value="成交日期",example="admin")
    private Date tradeDate;

    @ApiModelProperty(name="portfolioIds",value="组合代码",example="字符串1")
    private String portfolioIds;

    @ApiModelProperty(name="portfolioNames",value="组合名称",example="字符串1")
    private String portfolioNames;

    @ApiModelProperty(name="trdFeeAmt",value="交易手续费",example="字符串1")
    private BigDecimal trdFeeAmt;

    @ApiModelProperty(name="settleFeeAmt",value="结算手续费",example="字符串1")
    private BigDecimal settleFeeAmt;

    @ApiModelProperty(name="counterId",value="对手方Code",example="字符串1")
    private String counterId;

    @ApiModelProperty(name="counterIdName",value="对手方",example="字符串1")
    private String counterIdName;


    @ApiModelProperty(name="busiTypeStr",value="交易类型",example="字符串1")
    private String busiTypeStr;

    @ApiModelProperty(name="quoteType",value="报价类型",example="字符串1")
    private String quoteType;

    @ApiModelProperty(name="seqno",value="流水号",example="字符串1")
    private String seqno;

    @ApiModelProperty(name="trdConfStatus",value="成交确认状态",example="字符串1")
    private String trdConfStatus;

    @ApiModelProperty(name="matchStatus",value="匹配状态",example="字符串1")
    private String matchStatus;

    @ApiModelProperty(name="dealType",value="交易类型",example="01")
    private String dealType;

    @ApiModelProperty(name="totleTradeId",value="汇总登记编号",example="字符串1")
    private String totleTradeId;

    @ApiModelProperty(name="cstpTradeId",value="下行交易编号",example="字符串1")
    private String cstpTradeId;

    @ApiModelProperty(name="selectedItem",value="流水号",example="字符串1")
    private transient List<CfetsTrdConfirmTrdCstpMatchResponse> selectedItem;

    public String getMatchTypeName() {
        return SysDataUtil.getNameFromDataDict(DictCode.下行交易匹配类型, getMatchType());
    }

    public void setMatchTypeName(String matchTypeName) {
        this.matchTypeName = matchTypeName;
    }

    public String getMatchType() {
        return matchType;
    }

    public void setMatchType(String matchType) {
        this.matchType = matchType;
    }

    public String getBusiCode() {
        return busiCode;
    }

    public void setBusiCode(String busiCode) {
        this.busiCode = busiCode;
    }



    public String getFinprodType2() {
        return finprodType2;
    }

    public void setFinprodType2(String finprodType2) {
        this.finprodType2 = finprodType2;
    }

    public String getFinprodType2Name() {
        return finprodType2Name;
    }

    public void setFinprodType2Name(String finprodType2Name) {
        this.finprodType2Name = finprodType2Name;
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
        if(StringUtil.isEmpty(this.getSettleSpeed())){
            return StringUtil.EMPTY;
        }
        return "T+" + this.getSettleSpeed();
        //return SysDataUtil.getNameFromDataDict(DictCode.清算速度, getSettleSpeed());
    }

    public void setSettleSpeedName(String settleSpeedName) {
        this.settleSpeedName = settleSpeedName;
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

    public BigDecimal getPrintAmt() {
        return printAmt;
    }

    public void setPrintAmt(BigDecimal printAmt) {
        this.printAmt = printAmt;
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

    public Date getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(Date tradeDate) {
        this.tradeDate = tradeDate;
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

    public String getExecid() {
        return execid;
    }

    public void setExecid(String execid) {
        this.execid = execid;
    }

    public String getCounterId() {
        return counterId;
    }

    public void setCounterId(String counterId) {
        this.counterId = counterId;
    }

    public String getBusiTypeStr() {
        return SysDataUtil.getNameFromDataDict(BussinessDictCode.报价方式, getDealType());

    }

    public void setBusiTypeStr(String busiTypeStr) {
        this.busiTypeStr = busiTypeStr;
    }

    public String getQuoteType() {
        return quoteType;
    }

    public void setQuoteType(String quoteType) {
        this.quoteType = quoteType;
    }

    public String getSeqno() {
        return seqno;
    }

    public void setSeqno(String seqno) {
        this.seqno = seqno;
    }

    public List<CfetsTrdConfirmTrdCstpMatchResponse> getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(List<CfetsTrdConfirmTrdCstpMatchResponse> selectedItem) {
        this.selectedItem = selectedItem;
    }

    public String getTrdConfStatus() {
        return trdConfStatus;
    }

    public void setTrdConfStatus(String trdConfStatus) {
        this.trdConfStatus = trdConfStatus;
    }

    public String getMatchStatus() {
        return matchStatus;
    }

    public void setMatchStatus(String matchStatus) {
        this.matchStatus = matchStatus;
    }

    public String getDealType() {
        return dealType;
    }

    public void setDealType(String dealType) {
        this.dealType = dealType;
    }

    public String getTotleTradeId() {
        return totleTradeId;
    }

    public void setTotleTradeId(String totleTradeId) {
        this.totleTradeId = totleTradeId;
    }

    public String getCstpTradeId() {
        return cstpTradeId;
    }

    public void setCstpTradeId(String cstpTradeId) {
        this.cstpTradeId = cstpTradeId;
    }

    public String getCounterIdName() {
        return counterIdName;
    }

    public void setCounterIdName(String counterIdName) {
        this.counterIdName = counterIdName;
    }
}
