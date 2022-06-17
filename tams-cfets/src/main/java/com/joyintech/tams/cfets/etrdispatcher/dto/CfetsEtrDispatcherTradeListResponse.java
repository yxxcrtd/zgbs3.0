/*
 * 文件名：BussCombDistribtDto.java
 * 版权：Copyright 2020-2022 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管系统V2.0
 * 修改人：王波
 * 修改时间：2021年01月25日
 * 修改内容：新建
 * 系统名称：理财资管系统V2.0
 */

package com.joyintech.tams.cfets.etrdispatcher.dto;

import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.common.base.entity.EtrEntrust;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.BussinessDictCode;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 场外指令分发
 * 指令列表分页查询DTO
 * @author sjj
 * @version 1.0 2021年10月26日
 * @see CfetsEtrDispatcherTradeListResponse
 * @since 1.0
 */
@ApiModel(value="场外指令分发返回数据",description="场外指令分发返回数据")
public class CfetsEtrDispatcherTradeListResponse extends EtrEntrust {

    @ApiModelProperty(name="portfolioName",value="组合名称",example="字符串1")
    private String portfolioName;

    @ApiModelProperty(name="finprodMarketId",value="资产代码",example="字符串1")
    private String finprodMarketId;

    @ApiModelProperty(name="finprodAbbr",value="资产简称",example="字符串1")
    private String finprodAbbr;

    @ApiModelProperty(name="otherPs",value="交易方向",example="买入")
    private String otherPs;

    @ApiModelProperty(name="targetType",value="目标类型",example="A1")
    private String targetType;

    @ApiModelProperty(name="busiPs",value="回购方向",example="回购/拆借方向")
    private String busiPs;

    @ApiModelProperty(name="finishAmt",value="完成额度",example="30%")
    private BigDecimal finishAmt;

    @ApiModelProperty(name="trader",value="交易员",example="admin")
    private String trader;

    @ApiModelProperty(name="traderId",value="交易员ID",example="admin")
    private String traderId;

    @ApiModelProperty(name="secAcctId",value="证券账户",example="XXX证券账户")
    private String secAcctId;

    @ApiModelProperty(name="settleSpeed",value="清算速度",example="字符串1")
    private String settleSpeed;

    @ApiModelProperty(name="termDays",value="期限（天）",example="30")
    private String termDays;

    @ApiModelProperty(name="effectFlag",value="有效状态code",example="01")
    private String effectFlag;

    @ApiModelProperty(name="etrPrice",value="指令价格",example="99")
    private String etrPrice;

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

    @ApiModelProperty(name="calculateField",value="计算方式",example="计算方式供前台判断使用")
    private String calculateField;

    @ApiModelProperty(name = "assetType1", value = "资产二级分类", example = "字符串1")
    private String assetType1;

    @ApiModelProperty(name = "assetType10", value = "资产三级分类", example = "字符串1")
    private String assetType10;

    @ApiModelProperty(name = "assetType11", value = "资产四级分类", example = "字符串1")
    private String assetType11;

    @ApiModelProperty(name = "assetType11", value = "中标状态", example = "字符串1")
    private String isWin;

    @ApiModelProperty(name = "assetType11", value = "中标状态字典值", example = "字符串1")
    private String isWinValue;

    @ApiModelProperty(name="firstBidStatus",value="一级投标中标状态",example="A1")
    private String firstBidStatus;

    @ApiModelProperty(name="firstBidStatusStr",value="一级投标中标状态名称",example="A1")
    private String firstBidStatusStr;

    @ApiModelProperty(name="issueDate",value="投标日/发行日")
    private Date issueDate;

    @ApiModelProperty(name="issueDateStr",value="投标日/发行日字符串")
    private String issueDateStr;

    /**
     * 交易平台
     */
    @ApiModelProperty(name="tradePlat",value="交易平台")
    private String tradePlat;

    @ApiModelProperty(name = "bidModle", value = "投标方式")
    private String bidModle;

    @Override
    public String getTradePlat() {
        return tradePlat;
    }

    @Override
    public void setTradePlat(String tradePlat) {
        this.tradePlat = tradePlat;
    }

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
        else if (StringUtil.equals(super.getFinprodType2(), BussinessDictCodeList.平安资产类型_回购.getValue())
            && StringUtil.equals(getAssetType1(), DictCodeList.回购类型_质押式协议回购.getValue())) {
            return BussinessConstant.NEGOTIATED_REPURCHASE_TEXT;
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

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
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
        if (StringUtil.isEmpty(secAcctId)) {
            return null;
        }
        //去重
        String[] arr = secAcctId.split(",");
        List<String> list = new ArrayList();
        for (int i = 0; i < arr.length; i++) {
            String s = arr[i].trim();
            if (!list.contains(s)) {
                list.add(s);
            }
        }
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < list.size(); i++) {
            buffer.append(list.get(i));
            if (i < list.size() - 1) {
                buffer.append(",");
            }
        }
        return buffer.toString();
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
        if (dealProgress == null) {
            return BigDecimal.ZERO;
        }

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
        if (this.getBusiTypePosition() == null) return null;
        return StringUtil.equals(SysDataUtil.getNameFromDataDict(DictCode.头寸业务类型, this.getBusiTypePosition()), this.getBusiTypePosition()) ?
                SysDataUtil.getNameFromDataDict(DictCode.投管业务类型, this.getBusiTypePosition()) :
                SysDataUtil.getNameFromDataDict(DictCode.头寸业务类型, this.getBusiTypePosition());
    }

    public String getEtrPrice() {
        return etrPrice;
    }

    public void setEtrPrice(String etrPrice) {
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

    public String getCalculateField() {
        return calculateField;
    }

    public void setCalculateField(String calculateField) {
        this.calculateField = calculateField;
    }

    public String getAssetType1() {
        return assetType1;
    }

    public void setAssetType1(String assetType1) {
        this.assetType1 = assetType1;
    }

    public String getAssetType10() {
        return assetType10;
    }

    public void setAssetType10(String assetType10) {
        this.assetType10 = assetType10;
    }

    public String getAssetType11() {
        return assetType11;
    }

    public void setAssetType11(String assetType11) {
        this.assetType11 = assetType11;
    }

    public String getIsWin() {
        return isWin;
    }

    public void setIsWin(String isWin) {
        this.isWin = isWin;
    }

    public String getIsWinValue() {
        if (!StringUtil.isEmpty(this.getIsWin())) {
            return SysDataUtil.getNameFromDataDict(BussinessDictCode.中标状态, this.getIsWin());
        }
        return null;
    }

    public void setIsWinValue(String isWinValue) {
        this.isWinValue = isWinValue;
    }

    public String getFirstBidStatus() {
        return firstBidStatus;
    }

    public void setFirstBidStatus(String firstBidStatus) {
        this.firstBidStatus = firstBidStatus;
    }

    public String getFirstBidStatusStr() {
        if (StringUtil.isEmpty(getFirstBidStatus())) {
            return null;
        }
        return SysDataUtil.getNameFromDataDict(DictCode.一级投标中标状态, getFirstBidStatus());
    }

    public void setFirstBidStatusStr(String firstBidStatusStr) {
        this.firstBidStatusStr = firstBidStatusStr;
    }

    public Date getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    public String getIssueDateStr() {
        return issueDateStr;
    }

    public void setIssueDateStr(String issueDateStr) {
        this.issueDateStr = issueDateStr;
    }

    public String getTraderId() {
        return traderId;
    }

    public void setTraderId(String traderId) {
        this.traderId = traderId;
    }

    public String getBidModle() {
        return bidModle;
    }

    public void setBidModle(String bidModle) {
        this.bidModle = bidModle;
    }
}
