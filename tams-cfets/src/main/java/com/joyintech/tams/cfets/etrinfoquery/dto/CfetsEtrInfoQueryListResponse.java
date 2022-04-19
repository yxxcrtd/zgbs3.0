/*
 * 文件名：BussCombDistribtDto.java
 * 版权：Copyright 2020-2022 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管系统V2.0
 * 修改人：王波
 * 修改时间：2021年01月25日
 * 修改内容：新建
 * 系统名称：理财资管系统V2.0
 */

package com.joyintech.tams.cfets.etrinfoquery.dto;

import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.common.base.entity.EtrEntrust;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

/**
 * 场外指令分发
 * 指令列表分页查询DTO
 * @author sjj
 * @version 1.0 2021年10月26日
 * @see CfetsEtrInfoQueryListResponse
 * @since 1.0
 */
@ApiModel(value="场外指令分发返回数据",description="场外指令分发返回数据")
public class CfetsEtrInfoQueryListResponse extends EtrEntrust {

    @ApiModelProperty(name="portfolioName",value="组合名称",example="字符串1")
    private String portfolioName;

    @ApiModelProperty(name="finprodMarketId",value="资产代码",example="字符串1")
    private String finprodMarketId;

    @ApiModelProperty(name="finprodAbbr",value="资产简称",example="字符串1")
    private String finprodAbbr;

    @ApiModelProperty(name="otherPs",value="交易方向",example="买入")
    private String otherPs;

    @ApiModelProperty(name="busiPs",value="回购方向",example="回购/拆借方向")
    private String busiPs;

    @ApiModelProperty(name="finishAmt",value="完成额度",example="30%")
    private BigDecimal finishAmt;

    @ApiModelProperty(name="trader",value="交易员",example="admin")
    private String trader;

    @ApiModelProperty(name="secAcctId",value="证券账户",example="XXX证券账户")
    private String secAcctId;

    @ApiModelProperty(name="settleSpeed",value="清算速度",example="字符串1")
    private String settleSpeed;

    @ApiModelProperty(name="settleSpeed",value="期限（天）",example="30")
    private String termDays;

    @ApiModelProperty(name="effectFlag",value="有效状态code",example="01")
    private String effectFlag;

    @ApiModelProperty(name="etrPrice",value="指令价格",example="99")
    private BigDecimal etrPrice;

    @ApiModelProperty(name="exeProgress",value="执行进度%",example="50%")
    private BigDecimal exeProgress;

    @ApiModelProperty(name="dealProgress",value="成交进度%",example="50%")
    private BigDecimal dealProgress;

    @ApiModelProperty(name="busiTypePosition",value="头寸类型",example="01")
    private String busiTypePosition;

    @ApiModelProperty(name="execFinishAmt",value="已完成指令额度",example="1")
    private BigDecimal execFinishAmt;

    @ApiModelProperty(name="execRemainderAmt",value="剩余指令额度",example="1")
    private BigDecimal execRemainderAmt;

    @ApiModelProperty(name="transFinishAmt",value="已成交额度",example="1")
    private BigDecimal transFinishAmt;

    @ApiModelProperty(name="transRemainderAmt",value="剩余成交额度",example="1")
    private BigDecimal transRemainderAmt;

    @ApiModelProperty(name="transmitTime",value="下达时间",example="HH:MM:SS")
    private String transmitTime;

    @ApiModelProperty(name="invManagerName",value="投资经理",example="A1")
    private String invManagerName;

    @ApiModelProperty(name="calculateField",value="计算方式",example="计算方式供前台判断使用")
    private String calculateField;

    /**
     * 获取 otherEntrustStatus
     * @return otherEntrustStatus.
     */
    public String getOtherEntrustStatus() {
        return SysDataUtil.getNameFromDataDict(DictCode.委托状态, super.getEntrustStatus());
    }

    public String getOtherEntrustDate() {
        return DateUtil.format(super.getEntrustDate());
    }

    /**
     * 获取 otherSettleSpeed
     * @return otherSettleSpeed.
     */
    public String getOtherSettleSpeed() {
        return SysDataUtil.getNameFromDataDict(DictCode.清算速度, this.getSettleSpeed());
    }

    /**
     * 获取资产类型 otherFinprodType2
     * @return otherFinprodType2 资产类型对应name
     */
    public String getOtherFinprodType2() {
        if (StringUtil.equals(super.getFinprodType2(), BussinessDictCodeList.金融产品类型_投管用_一级投标债券.getValue())){
            return SysDataUtil.getNameFromDataDict(DictCode.平安资产类型, BussinessDictCodeList.平安资产类型_一级投标债券.getValue());
        }
        return SysDataUtil.getNameFromDataDict(DictCode.平安资产类型, super.getFinprodType2());
    }

    /**
     * 获取 otherTradeMarket
     * @return otherTradeMarket.
     */
    public String getOtherTradeMarket() {
        return SysDataUtil.getNameFromDataDict(DictCode.交易场所, super.getTradeMarket());
    }

    /**
     * 获取 otherTradeMarket
     * @return otherTradeMarket.
     */
    public String getOtherEffectFlag() {
        return SysDataUtil.getNameFromDataDict(DictCode.有效状态, this.getEffectFlag());
    }

    /**
     * 获取 otherCcy
     * @return otherCcy.
     */
    public String getOtherCcy() {
        return SysDataUtil.getNameFromDataDict(DictCode.币种, super.getCcy());
    }

    public String getPortfolioName() {
        return portfolioName;
    }

    public void setPortfolioName(String portfolioName) {
        this.portfolioName = portfolioName;
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

    public String getOtherPs() {
        return otherPs;
    }

    public void setOtherPs(String otherPs) {
        this.otherPs = otherPs;
    }

    public String getBusiPs() {
        return busiPs;
    }

    public void setBusiPs(String busiPs) {
        this.busiPs = busiPs;
    }

    public BigDecimal getFinishAmt() {
        return finishAmt;
    }

    public void setFinishAmt(BigDecimal finishAmt) {
        this.finishAmt = finishAmt;
    }

    public String getTrader() {
        return trader;
    }

    public void setTrader(String trader) {
        this.trader = trader;
    }

    public String getSecAcctId() {
        return secAcctId;
    }

    public void setSecAcctId(String secAcctId) {
        this.secAcctId = secAcctId;
    }

    public String getSettleSpeed() {
        return settleSpeed;
    }

    public void setSettleSpeed(String settleSpeed) {
        this.settleSpeed = settleSpeed;
    }

    public String getTermDays() {
        return termDays;
    }

    public void setTermDays(String termDays) {
        this.termDays = termDays;
    }

    public String getEffectFlag() {
        return effectFlag;
    }

    public void setEffectFlag(String effectFlag) {
        this.effectFlag = effectFlag;
    }

    public BigDecimal getExeProgress() {
        return exeProgress;
    }

    public void setExeProgress(BigDecimal exeProgress) {
        this.exeProgress = exeProgress;
    }

    public BigDecimal getDealProgress() {
        return dealProgress;
    }

    public void setDealProgress(BigDecimal dealProgress) {
        this.dealProgress = dealProgress;
    }

    public String getBusiTypePosition() {
        return busiTypePosition;
    }

    public void setBusiTypePosition(String busiTypePosition) {
        this.busiTypePosition = busiTypePosition;
    }

    public String getOtherBusiTypePosition() {
        return SysDataUtil.getNameFromDataDict(DictCode.头寸业务类型, this.getBusiTypePosition());
    }

    public BigDecimal getEtrPrice() {
        return etrPrice;
    }

    public void setEtrPrice(BigDecimal etrPrice) {
        this.etrPrice = etrPrice;
    }

    public BigDecimal getExecFinishAmt() {
        return execFinishAmt;
    }

    public void setExecFinishAmt(BigDecimal execFinishAmt) {
        this.execFinishAmt = execFinishAmt;
    }

    public BigDecimal getExecRemainderAmt() {
        return execRemainderAmt;
    }

    public void setExecRemainderAmt(BigDecimal execRemainderAmt) {
        this.execRemainderAmt = execRemainderAmt;
    }

    public BigDecimal getTransFinishAmt() {
        return transFinishAmt;
    }

    public void setTransFinishAmt(BigDecimal transFinishAmt) {
        this.transFinishAmt = transFinishAmt;
    }

    public BigDecimal getTransRemainderAmt() {
        return transRemainderAmt;
    }

    public void setTransRemainderAmt(BigDecimal transRemainderAmt) {
        this.transRemainderAmt = transRemainderAmt;
    }

    public String getTransmitTime() {
        return transmitTime;
    }

    public void setTransmitTime(String transmitTime) {
        this.transmitTime = transmitTime;
    }

    public String getInvManagerName() {
        return invManagerName;
    }

    public void setInvManagerName(String invManagerName) {
        this.invManagerName = invManagerName;
    }

    public String getCalculateField() {
        return calculateField;
    }

    public void setCalculateField(String calculateField) {
        this.calculateField = calculateField;
    }
}
