/*
 * 文件名：CfetsBondDealEntrustOrderDto.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangliang
 * 修改时间：2020年5月12日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.cfets.trd.bond.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.common.base.entity.EtrEntrust;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.invest.common.constant.BussinessDictCode;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;

import java.math.BigDecimal;

/**
 * 委托指令
 */
public class CfetsBondDealEntrustOrderDto extends EtrEntrust {

    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    private static final long serialVersionUID = -1913728031560090029L;

    /**
     * 交易完成度
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal transactioncom;
    
    /**
     * 成交进度
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal tradeFinishPercent;

    /**
     * 组合名称
     */
    private String portfolioName;

    /**
     * 证券账户名称
     */
    private String secAcctName;

    /**
     * 资产简称
     */
    private String finprodAbbr;

    /**
     * 市场代码
     */
    private String finprodMarketId;
    
    /**
     * 清算速度
     */
    private String settleSpreed;

    /**
     * 剩余额度
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal avaNum;
    
    /**
     * 委托剩余额度
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal entrustAvaNum;
    
    /**
     * 已完成额度
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal finishAmt;

    /**
     * 到期收益率
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal matureYield;

    /**
     * 报价方式
     */
    private String quoteType;

    /**
     * 本方
     */
    private String mySide;
    
    /**
     * 本方简称
     */
    private String mySideAbbr;

    /**
     * 本方交易员
     */
    private String mySideTrader;
    
    /**
     * 本方交易员名称
     */
    private String mySideTraderName;

    /**
     * 行权收益率
     */
    private BigDecimal exerYield;

    /**
     * 计算类型
     */
    private String calculateField;

    /**
     * 是否含权
     */
    private String isRight;

    /**
     * 资产一级分类
     */
    private String assetTypeOne;

    /**
     * 上次 查询时间
     */
    private String lastQueryTime;

    /**
     * 结算币种
     */
    private String settleCcy;

    private Integer rowNumber;

    /**
     * 投资经理姓名
     */
    private String invManagerName;

    /**
     * 交易对手简称
     */
    private String counterName;
    
    /**
     * 对手方交易员名称
     */
    private String counterTraderName;
    
    /**
     * 账户来源
     */
    private String secAcctSource;

    /**
     * 证券账户
     */
    private String secManageAcctId;

    /**
     * 组合系列
     */
    private String portfolioSeries;

    /**
     * 交易成员ID，21位编号
     */
    private String partyId;

    /**
     * 本方券委托数量
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal ourEntNum;

    /**
     * 融入券委托数量
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal lendinEntNum;

    /**
     * 卖空券可用数量
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal sellAbleNum;

    /**
     * 全价
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal unitFprice;

    /**
     * 指令下达时间
     */
    private String transmitTime;

    private String tradeMarketStr;

    private String priceModelStr;

    private String businessModelStr;

    /**
     * 已成交金额
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal finishDealAmt;

    /**
     * 剩余成交额度
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal avaDealAmt;

    /**
     * 资产四级类型
     */
    private String type11;

    public String getPortfolioSeries() {
        return portfolioSeries;
    }

    public void setPortfolioSeries(String portfolioSeries) {
        this.portfolioSeries = portfolioSeries;
    }

    public String getSecManageAcctId() {
        return secManageAcctId;
    }

    public void setSecManageAcctId(String secManageAcctId) {
        this.secManageAcctId = secManageAcctId;
    }

    /**
     * 获取 invManagerName
     * 
     * @return invManagerName.
     */
    public String getInvManagerName() {
        return invManagerName;
    }

    /**
     * 设置 invManagerName
     * 
     * @param invManagerName invManagerName
     */
    public void setInvManagerName(String invManagerName) {
        this.invManagerName = invManagerName;
    }

    /**
     * 获取 counterName
     * 
     * @return counterName.
     */
    public String getCounterName() {
        return counterName;
    }

    /**
     * 设置 counterName
     * 
     * @param counterName counterName
     */
    public void setCounterName(String counterName) {
        this.counterName = counterName;
    }

    /**
     * 获取 transactioncom
     * 
     * @return transactioncom.
     */
    public BigDecimal getTransactioncom() {
        return transactioncom;
    }

    /**
     * 设置 transactioncom
     * 
     * @param transactioncom transactioncom
     */
    public void setTransactioncom(BigDecimal transactioncom) {
        this.transactioncom = transactioncom;
    }

    /**
     * 获取 portfolioName
     * 
     * @return portfolioName.
     */
    public String getPortfolioName() {
        return portfolioName;
    }

    /**
     * 设置 portfolioName
     * 
     * @param portfolioName portfolioName
     */
    public void setPortfolioName(String portfolioName) {
        this.portfolioName = portfolioName;
    }

    /**
     * 获取 secAcctName
     * 
     * @return secAcctName.
     */
    public String getSecAcctName() {
        return secAcctName;
    }

    /**
     * 设置 secAcctName
     * 
     * @param secAcctName secAcctName
     */
    public void setSecAcctName(String secAcctName) {
        this.secAcctName = secAcctName;
    }

    /**
     * 获取 finprodAbbr
     * 
     * @return finprodAbbr.
     */
    public String getFinprodAbbr() {
        return finprodAbbr;
    }

    /**
     * 设置 finprodAbbr
     * 
     * @param finprodAbbr finprodAbbr
     */
    public void setFinprodAbbr(String finprodAbbr) {
        this.finprodAbbr = finprodAbbr;
    }

    
    /**
     * 获取 finprodMarketId
     * @return finprodMarketId.
     */
    public String getFinprodMarketId() {
        return finprodMarketId;
    }


    
    /**
     * 设置 finprodMarketId
     * @param finprodMarketId finprodMarketId
     */
    public void setFinprodMarketId(String finprodMarketId) {
        this.finprodMarketId = finprodMarketId;
    }


    /**
     * 获取 settleSpreed
     * 
     * @return settleSpreed.
     */
    public String getSettleSpreed() {
        return settleSpreed;
    }

    /**
     * 设置 settleSpreed
     * 
     * @param settleSpreed settleSpreed
     */
    public void setSettleSpreed(String settleSpreed) {
        this.settleSpreed = settleSpreed;
    }

    /**
     * 获取 avaNum
     * 
     * @return avaNum.
     */
    public BigDecimal getAvaNum() {
        return avaNum;
    }

    /**
     * 设置 avaNum
     * 
     * @param avaNum avaNum
     */
    public void setAvaNum(BigDecimal avaNum) {
        this.avaNum = avaNum;
    }

    
    /**
     * 获取 finishAmt
     * @return finishAmt.
     */
    public BigDecimal getFinishAmt() {
        return finishAmt;
    }

    
    /**
     * 设置 finishAmt
     * @param finishAmt finishAmt
     */
    public void setFinishAmt(BigDecimal finishAmt) {
        this.finishAmt = finishAmt;
    }

    /**
     * 获取 matureYield
     * 
     * @return matureYield.
     */
    public BigDecimal getMatureYield() {
        return matureYield;
    }

    /**
     * 设置 matureYield
     * 
     * @param matureYield matureYield
     */
    public void setMatureYield(BigDecimal matureYield) {
        this.matureYield = matureYield;
    }

    /**
     * 获取 quoteType
     * 
     * @return quoteType.
     */
    public String getQuoteType() {
        return quoteType;
    }

    /**
     * 设置 quoteType
     * 
     * @param quoteType quoteType
     */
    public void setQuoteType(String quoteType) {
        this.quoteType = quoteType;
    }

    /**
     * 获取 mySide
     * 
     * @return mySide.
     */
    public String getMySide() {
        return mySide;
    }

    /**
     * 设置 mySide
     * 
     * @param mySide mySide
     */
    public void setMySide(String mySide) {
        this.mySide = mySide;
    }

    
    /**
     * 获取 mySideAbbr
     * @return mySideAbbr.
     */
    public String getMySideAbbr() {
        return mySideAbbr;
    }

    
    /**
     * 设置 mySideAbbr
     * @param mySideAbbr mySideAbbr
     */
    public void setMySideAbbr(String mySideAbbr) {
        this.mySideAbbr = mySideAbbr;
    }

    
    /**
     * 获取 mySideTraderName
     * @return mySideTraderName.
     */
    public String getMySideTraderName() {
        return mySideTraderName;
    }

    
    /**
     * 设置 mySideTraderName
     * @param mySideTraderName mySideTraderName
     */
    public void setMySideTraderName(String mySideTraderName) {
        this.mySideTraderName = mySideTraderName;
    }

    
    /**
     * 获取 counterTraderName
     * @return counterTraderName.
     */
    public String getCounterTraderName() {
        return counterTraderName;
    }

    
    /**
     * 设置 counterTraderName
     * @param counterTraderName counterTraderName
     */
    public void setCounterTraderName(String counterTraderName) {
        this.counterTraderName = counterTraderName;
    }

    /**
     * 获取 mySideTrader
     * 
     * @return mySideTrader.
     */
    public String getMySideTrader() {
        return mySideTrader;
    }

    /**
     * 设置 mySideTrader
     * 
     * @param mySideTrader mySideTrader
     */
    public void setMySideTrader(String mySideTrader) {
        this.mySideTrader = mySideTrader;
    }

    public String getSecAcctSource() {
		return secAcctSource;
	}

	public void setSecAcctSource(String secAcctSource) {
		this.secAcctSource = secAcctSource;
	}

	/**
     * 获取 exerYield
     * 
     * @return exerYield.
     */
    public BigDecimal getExerYield() {
        return exerYield;
    }

    /**
     * 设置 exerYield
     * 
     * @param exerYield exerYield
     */
    public void setExerYield(BigDecimal exerYield) {
        this.exerYield = exerYield;
    }

    /**
     * 获取 calculateField
     * 
     * @return calculateField.
     */
    public String getCalculateField() {
        return calculateField;
    }

    /**
     * 设置 calculateField
     * 
     * @param calculateField calculateField
     */
    public void setCalculateField(String calculateField) {
        this.calculateField = calculateField;
    }

    /**
     * 获取 isRight
     * 
     * @return isRight.
     */
    public String getIsRight() {
        return isRight;
    }

    /**
     * 设置 isRight
     * 
     * @param isRight isRight
     */
    public void setIsRight(String isRight) {
        this.isRight = isRight;
    }

    /**
     * 获取 assetTypeOne
     * 
     * @return assetTypeOne.
     */
    public String getAssetTypeOne() {
        return assetTypeOne;
    }

    /**
     * 设置 assetTypeOne
     * 
     * @param assetTypeOne assetTypeOne
     */
    public void setAssetTypeOne(String assetTypeOne) {
        this.assetTypeOne = assetTypeOne;
    }

    /**
     * 获取 lastQueryTime
     * 
     * @return lastQueryTime.
     */
    public String getLastQueryTime() {
        return lastQueryTime;
    }

    /**
     * 设置 lastQueryTime
     * 
     * @param lastQueryTime lastQueryTime
     */
    public void setLastQueryTime(String lastQueryTime) {
        this.lastQueryTime = lastQueryTime;
    }

    public String getEntrustStatusStr() {
        if (StringUtil.isNotEmpty(super.getEntrustStatus())) {
            return SysDataUtil.getNameFromDataDict(BussinessDictCode.委托状态,
                super.getEntrustStatus());
        }
        return "";
    }

    public String getQuoteTypeStr() {
        if (StringUtil.isNotEmpty(this.getQuoteType())) {
            String quoteTypeStr = SysDataUtil.getNameFromDataDict(BussinessDictCode.报价方式,
                this.getQuoteType());
            if (quoteTypeStr == null) {
                return this.quoteType;
            }
            return quoteTypeStr;
        }
        return "";
    }

    public String getPsStr() {
        if (StringUtil.isNotEmpty(super.getPs())) {
            String psStr = SysDataUtil.getNameFromDataDict(BussinessDictCode.交易方向, super.getPs());
            if (psStr == null) {
                return super.getPs();
            }
            return psStr;
        }
        return "";
    }

    public String getSettleSpreedStr() {
		if (StringUtil.equals(this.getSettleSpreed(), "0")
				|| StringUtil.equals(this.getSettleSpreed(), BussinessDictCodeList.清算速度_T加0.getValue())) {
			return BussinessDictCodeList.清算速度_T加0.getCodeName();
		} else if (StringUtil.equals(this.getSettleSpreed(), "1")
				|| StringUtil.equals(this.getSettleSpreed(), BussinessDictCodeList.清算速度_T加1.getValue())) {
			return BussinessDictCodeList.清算速度_T加1.getCodeName();
		} else if (StringUtil.equals(this.getSettleSpreed(), "2")
				|| StringUtil.equals(this.getSettleSpreed(), BussinessDictCodeList.清算速度_T加2.getValue())) {
			return BussinessDictCodeList.清算速度_T加2.getCodeName();
		} else {
			return this.getSettleSpreed();
		}
    }

    public String getAvaNumStr() {
        if (StringUtil.isNotEmpty(this.getAvaNum())) {
        	return this.getAvaNum().toPlainString();
        }
        return null;
    }

    public String getSettleMethodStr() {
        if (StringUtil.equals(this.getSettleMethod(),
            BussinessDictCodeList.结算方式_见券付款.getValue())) {
            return BussinessDictCodeList.结算方式_见券付款.getCodeName();
        }
        else if (StringUtil.equals(this.getSettleMethod(),
            BussinessDictCodeList.结算方式_见款付券.getValue())) {
            return BussinessDictCodeList.结算方式_见款付券.getCodeName();
        }
        else if (StringUtil.equals(this.getSettleMethod(),
            BussinessDictCodeList.结算方式_券款对付.getValue())) {
            return BussinessDictCodeList.结算方式_券款对付.getCodeName();
        }
        else {
            return this.getSettleMethod();
        }
    }
    
    public String getSettleMethodDefault(){
        return BussinessDictCodeList.结算方式_券款对付.getCodeName();
    }

    public String getSettleTypeStr() {
        if (StringUtil.equals(this.getSettleType(), BussinessDictCodeList.清算类型_净额清算.getValue())) {
            return BussinessDictCodeList.清算类型_净额清算.getCodeName();
        }
        else if (StringUtil.equals(this.getSettleType(),
            BussinessDictCodeList.清算类型_全额清算.getValue())) {
            return BussinessDictCodeList.清算类型_全额清算.getCodeName();
        }
        else {
            return this.getSettleType();
        }
    }
    
    public String getSettleTypeDefault(){
        return BussinessDictCodeList.清算类型_全额清算.getCodeName();
    }
    
    /**
     * 获取 settleCcy
     * 
     * @return settleCcy.
     */
    public String getSettleCcy() {
        return settleCcy;
    }

    /**
     * 设置 settleCcy
     * 
     * @param settleCcy settleCcy
     */
    public void setSettleCcy(String settleCcy) {
        this.settleCcy = settleCcy;
    }

    /**
     * 本方角色
     */
    private String quoteRoleType;

    /**
     * 报价状态，新增中/已新增/修改中/已修改/撤回中/已撤回/成交中/已成交/终止中/已终止/待回复/已撤销/
     */
    private String tradeStatus;

    /**
     * 发送状态，待发送/成功/失败/发送中
     */
    private String sendStatus;

    /**
     * 结算方式
     */
    private String settleMethod;

    /**
     * 清算类型
     */
    private String settleType;
    
    /**
     * 报价变化
     */
    private String quoteId;

    
    /**
     * 获取 quoteId
     * @return quoteId.
     */
    public String getQuoteId() {
        return quoteId;
    }

    
    /**
     * 设置 quoteId
     * @param quoteId quoteId
     */
    public void setQuoteId(String quoteId) {
        this.quoteId = quoteId;
    }

    /**
     * 获取 quoteRoleType
     * 
     * @return quoteRoleType.
     */
    public String getQuoteRoleType() {
        return quoteRoleType;
    }

    /**
     * 设置 quoteRoleType
     * 
     * @param quoteRoleType quoteRoleType
     */
    public void setQuoteRoleType(String quoteRoleType) {
        this.quoteRoleType = quoteRoleType;
    }

    /**
     * 获取 tradeStatus
     * 
     * @return tradeStatus.
     */
    public String getTradeStatus() {
        return tradeStatus;
    }

    /**
     * 设置 tradeStatus
     * 
     * @param tradeStatus tradeStatus
     */
    public void setTradeStatus(String tradeStatus) {
        this.tradeStatus = tradeStatus;
    }

    /**
     * 获取 sendStatus
     * 
     * @return sendStatus.
     */
    public String getSendStatus() {
        return sendStatus;
    }

    /**
     * 设置 sendStatus
     * 
     * @param sendStatus sendStatus
     */
    public void setSendStatus(String sendStatus) {
        this.sendStatus = sendStatus;
    }

    /**
     * 获取 settleMethod
     * 
     * @return settleMethod.
     */
    public String getSettleMethod() {
        return settleMethod;
    }

    /**
     * 设置 settleMethod
     * 
     * @param settleMethod settleMethod
     */
    public void setSettleMethod(String settleMethod) {
        this.settleMethod = settleMethod;
    }

    /**
     * 获取 settleType
     * 
     * @return settleType.
     */
    public String getSettleType() {
        return settleType;
    }

    /**
     * 设置 settleType
     * 
     * @param settleType settleType
     */
    public void setSettleType(String settleType) {
        this.settleType = settleType;
    }

    /**
     * 获取 rowNumber
     * 
     * @return rowNumber.
     */
    public Integer getRowNumber() {
        return rowNumber;
    }

    /**
     * 设置 rowNumber
     * 
     * @param rowNumber rowNumber
     */
    public void setRowNumber(Integer rowNumber) {
        this.rowNumber = rowNumber;
    }

    public String getSendStatusStr() {
        return SysDataUtil.getNameFromDataDict(BussinessDictCode.银行间上行发送状态, this.getSendStatus());
    }

	public BigDecimal getTradeFinishPercent() {
		return tradeFinishPercent;
	}

	public void setTradeFinishPercent(BigDecimal tradeFinishPercent) {
		this.tradeFinishPercent = tradeFinishPercent;
	}
    
	
    /**
     * 获取 getPrice
     * @return getPrice.
     */
    public String getPriceStr() {
        if(super.getPrice() != null) {
        	return super.getPrice().setScale(4, BigDecimal.ROUND_HALF_UP).toPlainString();
        }
        return "";
    }
    
    /**
     * 获取 MatureYieldStr
     * @return MatureYieldStr.
     */
    public String getMatureYieldStr() {
        if(this.getMatureYield() != null) {
        	return this.getMatureYield().setScale(4, BigDecimal.ROUND_HALF_UP).toPlainString();
        }
        return "";
    }
    
    /**
     * 获取 getExerYieldStr
     * @return getExerYieldStr.
     */
    public String getExerYieldStr() {
        if(this.getExerYield() != null) {
        	return this.getExerYield().setScale(4, BigDecimal.ROUND_HALF_UP).toPlainString();
        }
        return "";
    }

	public BigDecimal getEntrustAvaNum() {
		return entrustAvaNum;
	}

	public void setEntrustAvaNum(BigDecimal entrustAvaNum) {
		this.entrustAvaNum = entrustAvaNum;
	}

	public String getVdateStr(){
        if(this.getVdate() != null) {
            return DateUtil.format(this.getVdate());
        }
        return "";
    }


    public String getMdateStr(){
        if(this.getMdate() != null) {
            return DateUtil.format(this.getMdate());
        }
        return "";
    }

    public String getPartyId() {
        return partyId;
    }

    public void setPartyId(String partyId) {
        this.partyId = partyId;
    }

    public BigDecimal getOurEntNum() {
        return ourEntNum;
    }

    public void setOurEntNum(BigDecimal ourEntNum) {
        this.ourEntNum = ourEntNum;
    }

    public BigDecimal getLendinEntNum() {
        return lendinEntNum;
    }

    public void setLendinEntNum(BigDecimal lendinEntNum) {
        this.lendinEntNum = lendinEntNum;
    }

    public BigDecimal getSellAbleNum() {
        return sellAbleNum;
    }

    public void setSellAbleNum(BigDecimal sellAbleNum) {
        this.sellAbleNum = sellAbleNum;
    }

    public BigDecimal getUnitFprice() {
        return unitFprice;
    }

    public void setUnitFprice(BigDecimal unitFprice) {
        this.unitFprice = unitFprice;
    }

    public String getTransmitTime() {
        return transmitTime;
    }

    public void setTransmitTime(String transmitTime) {
        this.transmitTime = transmitTime;
    }

    public String getTradeMarketStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.交易场所, getTradeMarket());
    }

    public void setTradeMarketStr(String tradeMarketStr) {
        this.tradeMarketStr = tradeMarketStr;
    }

    public BigDecimal getFinishDealAmt() {
        return finishDealAmt;
    }

    public void setFinishDealAmt(BigDecimal finishDealAmt) {
        this.finishDealAmt = finishDealAmt;
    }

    public void setPriceModelStr(String priceModelStr) {
        this.priceModelStr = priceModelStr;
    }

    public void setBusinessModelStr(String businessModelStr) {
        this.businessModelStr = businessModelStr;
    }

    public String getPriceModelStr() {
        if (StringUtil.isEmpty(getPriceModel())) {
            return StringUtil.EMPTY;
        }
        return SysDataUtil.getNameFromDataDict(DictCode.价格模式, getPriceModel());
    }

    public String getBusinessModelStr() {
        if (StringUtil.isEmpty(getBusinessModel())) {
            return StringUtil.EMPTY;
        }
        return SysDataUtil.getNameFromDataDict(DictCode.业务模式, getBusinessModel());
    }

    public BigDecimal getAvaDealAmt() {
        return avaDealAmt;
    }

    public void setAvaDealAmt(BigDecimal avaDealAmt) {
        this.avaDealAmt = avaDealAmt;
    }

    public String getType11() {
        return type11;
    }

    public void setType11(String type11) {
        this.type11 = type11;
    }
}
