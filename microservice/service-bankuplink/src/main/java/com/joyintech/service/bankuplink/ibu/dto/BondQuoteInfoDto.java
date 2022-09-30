/*
 * 文件名：BondQuoteInfoDto.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangliang
 * 修改时间：2020年5月15日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.service.bankuplink.ibu.dto;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.invest.common.constant.BussinessDictCode;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.framework.json.BigDecimalSerializer;

/**
 * 现券报价基本信息Dto
 * 
 * @author zhangliang
 * @version 1.0 2020年5月15日
 * @see BondQuoteInfoDto
 * @since 1.0
 */
public class BondQuoteInfoDto extends BaseEntity {

    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    private static final long serialVersionUID = -8967384621506273L;
    /**
     * 报价编号（内部代码）
     */
    private String quoteId;
    /**
     * 本方角色
     */
    private String quoteRoleType;
    /**
     * 交易方向，买入、卖出、收息、还本、申购、赎回等
     */
    private String ps;
    /**
     * 金融产品代码
     */
    private String finprodId;
    /**
     * 金融产品简称
     */
    private String finprodAbbr;
    /**
     * 报价类型，对话报价/请求报价/质押式回购报价
     */
    private String quoteType;
    /**
     * 报价状态，新增中/已新增/修改中/已修改/撤回中/已撤回/成交中/已成交/终止中/已终止/待回复/已撤销/
     */
    private String tradeStatus;
    /**
     * 发送状态，待发送/成功/失败/发送中
     */
    private String sendStatus;
    /**
     * 本方银行间机构，6位编号
     */
    private String ouPartyId;
    /**
     * 交易成员简称(机构简称、交易机构名称、本方银行间机构名称)
     */
    private String partyAbbr;
    /**
     * 本方银行间交易员
     */
    private String ouTraderId;
    /**
     * 本方交易员姓名
     */
    private String ouTraderName;
    /**
     * 对手银行间机构，6位编号
     */
    private String cpPartyId;
    /**
     * 对手银行间机构名称
     */
    private String cpPartyName;
    /**
     * 对手方银行间交易员
     */
    private String cpTrader;
    /**
     * 对手方银行间交易员姓名
     */
    private String cpTraderName;
    /**
     * 请求有效时间
     */
    private String reqEffTime;
    /**
     * 回复有效时间
     */
    private String resEffTime;
    /**
     * 回复量(份额)
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal shareAmt;
    /**
     * 清算速度
     */
    private Long settleSpeed;
    /**
     * 单位净价
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal unitCprice;
    /**
     * 到期收益率
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal yield;
    /**
     * 券面（万）(份额 * 100)
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal prinAmt;
    /**
     * 资产一级分类
     */
    private String assetTypeOne;
    /**
     * CFETS报价编号
     */
    private String cfetsQuoteId;
    /**
     * 成交编号
     */
    private String fTradeId;
    
    /**
     * 有效状态
     */
    private String validStatus;
    
    /**
     * 是否多组合
     */
    private String isDzh;


    /**
     * 待偿期
     */
    private BigDecimal pendingPeriod;

    /**
     * 评级
     */
    private String grade;

    /**
     * 备注
     */
    private String remark;

    /**
     * 行权收益率
     */
    private BigDecimal exerYield;

    public BigDecimal getExerYield() {
        return exerYield;
    }

    public void setExerYield(BigDecimal exerYield) {
        this.exerYield = exerYield;
    }

    public BigDecimal getPendingPeriod() {
        return pendingPeriod;
    }

    public void setPendingPeriod(BigDecimal pendingPeriod) {
        this.pendingPeriod = pendingPeriod;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

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
     * @return quoteRoleType.
     */
    public String getQuoteRoleType() {
        return quoteRoleType;
    }
    
    /**
     * 设置 quoteRoleType
     * @param quoteRoleType quoteRoleType
     */
    public void setQuoteRoleType(String quoteRoleType) {
        this.quoteRoleType = quoteRoleType;
    }
    
    /**
     * 获取 ps
     * @return ps.
     */
    public String getPs() {
        return ps;
    }
    
    /**
     * 设置 ps
     * @param ps ps
     */
    public void setPs(String ps) {
        this.ps = ps;
    }
    
    /**
     * 获取 finprodId
     * @return finprodId.
     */
    public String getFinprodId() {
        return finprodId;
    }
    
    /**
     * 设置 finprodId
     * @param finprodId finprodId
     */
    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
    }
    
    /**
     * 获取 finprodAbbr
     * @return finprodAbbr.
     */
    public String getFinprodAbbr() {
        return finprodAbbr;
    }
    
    /**
     * 设置 finprodAbbr
     * @param finprodAbbr finprodAbbr
     */
    public void setFinprodAbbr(String finprodAbbr) {
        this.finprodAbbr = finprodAbbr;
    }
    
    /**
     * 获取 quoteType
     * @return quoteType.
     */
    public String getQuoteType() {
        return quoteType;
    }
    
    /**
     * 设置 quoteType
     * @param quoteType quoteType
     */
    public void setQuoteType(String quoteType) {
        this.quoteType = quoteType;
    }
    
    /**
     * 获取 tradeStatus
     * @return tradeStatus.
     */
    public String getTradeStatus() {
        return tradeStatus;
    }
    
    /**
     * 设置 tradeStatus
     * @param tradeStatus tradeStatus
     */
    public void setTradeStatus(String tradeStatus) {
        this.tradeStatus = tradeStatus;
    }
    
    /**
     * 获取 sendStatus
     * @return sendStatus.
     */
    public String getSendStatus() {
        return sendStatus;
    }
    
    /**
     * 设置 sendStatus
     * @param sendStatus sendStatus
     */
    public void setSendStatus(String sendStatus) {
        this.sendStatus = sendStatus;
    }
    
    /**
     * 获取 ouPartyId
     * @return ouPartyId.
     */
    public String getOuPartyId() {
        return ouPartyId;
    }
    
    /**
     * 设置 ouPartyId
     * @param ouPartyId ouPartyId
     */
    public void setOuPartyId(String ouPartyId) {
        this.ouPartyId = ouPartyId;
    }
    
    /**
     * 获取 ouTraderId
     * @return ouTraderId.
     */
    public String getOuTraderId() {
        return ouTraderId;
    }
    
    /**
     * 设置 ouTraderId
     * @param ouTraderId ouTraderId
     */
    public void setOuTraderId(String ouTraderId) {
        this.ouTraderId = ouTraderId;
    }
    
    /**
     * 获取 cpPartyId
     * @return cpPartyId.
     */
    public String getCpPartyId() {
        return cpPartyId;
    }
    
    /**
     * 设置 cpPartyId
     * @param cpPartyId cpPartyId
     */
    public void setCpPartyId(String cpPartyId) {
        this.cpPartyId = cpPartyId;
    }
    
    /**
     * 获取 cpTrader
     * @return cpTrader.
     * @param s
     */
    public String getCpTrader() {
        return cpTrader;
    }
    
    /**
     * 设置 cpTrader
     * @param cpTrader cpTrader
     */
    public void setCpTrader(String cpTrader) {
        this.cpTrader = cpTrader;
    }
    
    /**
     * 获取 reqEffTime
     * @return reqEffTime.
     */
    public String getReqEffTime() {
        return reqEffTime;
    }
    
    /**
     * 设置 reqEffTime
     * @param reqEffTime reqEffTime
     */
    public void setReqEffTime(String reqEffTime) {
        this.reqEffTime = reqEffTime;
    }
    
    /**
     * 获取 resEffTime
     * @return resEffTime.
     */
    public String getResEffTime() {
        return resEffTime;
    }
    
    /**
     * 设置 resEffTime
     * @param resEffTime resEffTime
     */
    public void setResEffTime(String resEffTime) {
        this.resEffTime = resEffTime;
    }
    
    /**
     * 获取 shareAmt
     * @return shareAmt.
     * @param shareAmt
     */
    public BigDecimal getShareAmt() {
        return this.shareAmt;
    }
    
    /**
     * 设置 shareAmt
     * @param shareAmt shareAmt
     */
    public void setShareAmt(BigDecimal shareAmt) {
        this.shareAmt = shareAmt;
    }
    
    /**
     * 获取 settleSpeed
     * @return settleSpeed.
     */
    public Long getSettleSpeed() {
        return settleSpeed;
    }
    
    /**
     * 设置 settleSpeed
     * @param settleSpeed settleSpeed
     */
    public void setSettleSpeed(Long settleSpeed) {
        this.settleSpeed = settleSpeed;
    }
    
    /**
     * 获取 unitCprice
     * @return unitCprice.
     */
    public BigDecimal getUnitCprice() {
        return unitCprice;
    }
    
    /**
     * 设置 unitCprice
     * @param unitCprice unitCprice
     */
    public void setUnitCprice(BigDecimal unitCprice) {
        this.unitCprice = unitCprice;
    }
    
    /**
     * 获取 yield
     * @return yield.
     */
    public BigDecimal getYield() {
        return yield;
    }
    
    /**
     * 设置 yield
     * @param yield yield
     */
    public void setYield(BigDecimal yield) {
        this.yield = yield;
    }
    
    /**
     * 获取 prinAmt
     * @return prinAmt.
     */
    public BigDecimal getPrinAmt() {
        return prinAmt;
    }
    
    /**
     * 设置 prinAmt
     * @param prinAmt prinAmt
     */
    public void setPrinAmt(BigDecimal prinAmt) {
        this.prinAmt = prinAmt;
    }
    
    /**
     * 获取 assetTypeOne
     * @return assetTypeOne.
     */
    public String getAssetTypeOne() {
        return assetTypeOne;
    }
    
    /**
     * 设置 assetTypeOne
     * @param assetTypeOne assetTypeOne
     */
    public void setAssetTypeOne(String assetTypeOne) {
        this.assetTypeOne = assetTypeOne;
    }
    
    /**
     * 获取 cfetsQuoteId
     * @return cfetsQuoteId.
     */
    public String getCfetsQuoteId() {
        return cfetsQuoteId;
    }
    
    /**
     * 设置 cfetsQuoteId
     * @param cfetsQuoteId cfetsQuoteId
     */
    public void setCfetsQuoteId(String cfetsQuoteId) {
        this.cfetsQuoteId = cfetsQuoteId;
    }
    
    /**
     * 获取 fTradeId
     * @return fTradeId.
     */
    public String getfTradeId() {
        return fTradeId;
    }
    
    /**
     * 设置 fTradeId
     * @param fTradeId fTradeId
     */
    public void setfTradeId(String fTradeId) {
        this.fTradeId = fTradeId;
    }
    /**
     * 获取 partyAbbr
     * @return partyAbbr.
     */
    public String getPartyAbbr() {
        return partyAbbr;
    }


    
    /**
     * 设置 partyAbbr
     * @param partyAbbr partyAbbr
     */
    public void setPartyAbbr(String partyAbbr) {
        this.partyAbbr = partyAbbr;
    }


    
    /**
     * 获取 ouTraderName
     * @return ouTraderName.
     */
    public String getOuTraderName() {
        return ouTraderName;
    }


    
    /**
     * 设置 ouTraderName
     * @param ouTraderName ouTraderName
     */
    public void setOuTraderName(String ouTraderName) {
        this.ouTraderName = ouTraderName;
    }


    
    /**
     * 获取 cpPartyName
     * @return cpPartyName.
     */
    public String getCpPartyName() {
        return cpPartyName;
    }


    
    /**
     * 设置 cpPartyName
     * @param cpPartyName cpPartyName
     */
    public void setCpPartyName(String cpPartyName) {
        this.cpPartyName = cpPartyName;
    }


    
    /**
     * 获取 cpTraderName
     * @return cpTraderName.
     */
    public String getCpTraderName() {
        return cpTraderName;
    }


    
    /**
     * 设置 cpTraderName
     * @param cpTraderName cpTraderName
     */
    public void setCpTraderName(String cpTraderName) {
        this.cpTraderName = cpTraderName;
    }


    // added by qufangxu at 2020-05-20 start
    
    /**
     * 报价日期
     */
    private Date quoteDate;
    
    /**
     * 债券代码
     */
    private String finprodMarketId;
    
    /**
     * 组合代码
     */
    private String portfolioId;
    
    /**
     * 委托代码
     */
    private String entrustId;

	public Date getQuoteDate() {
		return quoteDate;
	}
	
	public void setQuoteDate(Date quoteDate) {
		this.quoteDate = quoteDate;
	}

	public String getFinprodMarketId() {
		return finprodMarketId;
	}
	
	public void setFinprodMarketId(String finprodMarketId) {
		this.finprodMarketId = finprodMarketId;
	}

	public String getPortfolioId() {
		return portfolioId;
	}
	
	public void setPortfolioId(String portfolioId) {
		this.portfolioId = portfolioId;
	}

	public String getEntrustId() {
		return entrustId;
	}
	
	public void setEntrustId(String entrustId) {
		this.entrustId = entrustId;
	}

    
    // added by qufangxu at 2020-05-20 end
	
	
	
	
	// add by zhangliang 20200601 此处是转换数据字典推送给前台 start
	private String quoteRoleTypeStr;
	
	public String getQuoteRoleTypeStr(){
	    if (StringUtil.isNotEmpty(this.getQuoteRoleType())){
	        return SysDataUtil.getNameFromDataDict(BussinessDictCode.银行间上行本方角色, this.getQuoteRoleType());
	    }
	    return null;
    }
    
	private String psStr;
    public String getPsStr(){
        return SysDataUtil.getNameFromDataDict(BussinessDictCode.交易方向, this.getPs());
    }
    
    private String quoteTypeStr;
    public String getQuoteTypeStr(){
        if (StringUtil.equals(this.getQuoteType(), BussinessDictCodeList.报价系统区分类型_现券对话报价.getValue())){
            return "对话";
        }
        else if (StringUtil.equals(this.getQuoteType(), BussinessDictCodeList.报价系统区分类型_现券请求报价.getValue())){
            return "请求";
        }
        return "";
    }
    
    private String tradeStatusStr;
    public String getTradeStatusStr(){
        return SysDataUtil.getNameFromDataDict(BussinessDictCode.银行间上行报价状态, this.getTradeStatus());
    }
    
    private String sendStatusStr;
    public String getSendStatusStr(){
        return SysDataUtil.getNameFromDataDict(BussinessDictCode.银行间上行发送状态, this.getSendStatus());
    }
    
    private BigDecimal shareAmtHun;
    public BigDecimal getShareAmtHun(){
        if (StringUtil.equals(this.getQuoteType(), BussinessDictCodeList.交易类型_请求报价.getValue())){
            if (this.getShareAmt() != null){
                return ArithmeticUtil.divide(this.getShareAmt(), new BigDecimal(10000), 6);
            }
        }
        return null;
    }
    
    private String settleSpeedStr;
    public String getSettleSpeedStr(){
        if (this.getSettleSpeed() != null && this.getSettleSpeed() == 0L){
            return BussinessDictCodeList.清算速度_T加0.getCodeName();
        }
        else if (this.getSettleSpeed() != null && this.getSettleSpeed() == 1L){
            return BussinessDictCodeList.清算速度_T加1.getCodeName();
        }
        return null;
    }
    
    private BigDecimal prinAmtHun;
    public BigDecimal getPrinAmtHun(){
        if (this.getPrinAmt() != null){
            return ArithmeticUtil.divide(this.getPrinAmt(), new BigDecimal(10000), 6);
        }
        return null;
    }
    
	private String assetTypeOneStr;
	public String getAssetTypeOneStr() {
		return this.getAssetTypeOne();
	}
    
    /**
     * 判断是否显示回复按钮
     * 
     * @return 是否显示回复按钮
     * @see
     * @since 1.0
     */
    private String answerFlag;
    public String getAnswerFlag(){
        String answerFlag = "02";
        if (StringUtil.equals(this.getQuoteRoleType(), BussinessDictCodeList.银行间上行本方角色_回复方.getValue())
            && StringUtil.equals(this.getTradeStatus(), BussinessDictCodeList.银行间上行报价状态_待回复.getValue())
            && !StringUtil.isNotEmpty(this.getSendStatus())){
            answerFlag = "01";
        }
        if (StringUtil.equals(this.getQuoteRoleType(), BussinessDictCodeList.银行间上行本方角色_非做市方.getValue())
            && StringUtil.equals(this.getTradeStatus(), BussinessDictCodeList.银行间上行报价状态_已回复.getValue())
            && !StringUtil.isNotEmpty(this.getSendStatus())){
            answerFlag = "01";
        }
        if (StringUtil.equals(this.getQuoteRoleType(), BussinessDictCodeList.银行间上行本方角色_做市方.getValue())
            && StringUtil.equals(this.getTradeStatus(), BussinessDictCodeList.银行间上行报价状态_待回复.getValue())
            && !StringUtil.isNotEmpty(this.getSendStatus())){
            answerFlag = "01";
        }
        
        return answerFlag;
    }


    
    /**
     * 设置 quoteRoleTypeStr
     * @param quoteRoleTypeStr quoteRoleTypeStr
     */
    public void setQuoteRoleTypeStr(String quoteRoleTypeStr) {
        this.quoteRoleTypeStr = quoteRoleTypeStr;
    }


    
    /**
     * 设置 psStr
     * @param psStr psStr
     */
    public void setPsStr(String psStr) {
        this.psStr = psStr;
    }


    
    /**
     * 设置 quoteTypeStr
     * @param quoteTypeStr quoteTypeStr
     */
    public void setQuoteTypeStr(String quoteTypeStr) {
        this.quoteTypeStr = quoteTypeStr;
    }


    
    /**
     * 设置 tradeStatusStr
     * @param tradeStatusStr tradeStatusStr
     */
    public void setTradeStatusStr(String tradeStatusStr) {
        this.tradeStatusStr = tradeStatusStr;
    }


    
    /**
     * 设置 sendStatusStr
     * @param sendStatusStr sendStatusStr
     */
    public void setSendStatusStr(String sendStatusStr) {
        this.sendStatusStr = sendStatusStr;
    }


    
    /**
     * 设置 shareAmtHun
     * @param shareAmtHun shareAmtHun
     */
    public void setShareAmtHun(BigDecimal shareAmtHun) {
        this.shareAmtHun = shareAmtHun;
    }


    
    /**
     * 设置 settleSpeedStr
     * @param settleSpeedStr settleSpeedStr
     */
    public void setSettleSpeedStr(String settleSpeedStr) {
        this.settleSpeedStr = settleSpeedStr;
    }


    
    /**
     * 设置 prinAmtHun
     * @param prinAmtHun prinAmtHun
     */
    public void setPrinAmtHun(BigDecimal prinAmtHun) {
        this.prinAmtHun = prinAmtHun;
    }


    
    /**
     * 设置 assetTypeOneStr
     * @param assetTypeOneStr assetTypeOneStr
     */
    public void setAssetTypeOneStr(String assetTypeOneStr) {
        this.assetTypeOneStr = assetTypeOneStr;
    }


    
    /**
     * 设置 answerFlag
     * @param answerFlag answerFlag
     */
    public void setAnswerFlag(String answerFlag) {
        this.answerFlag = answerFlag;
    }
    // add by zhangliang 20200601 此处是转换数据字典推送给前台 end


	public String getValidStatus() {
		return validStatus;
	}


	public void setValidStatus(String validStatus) {
		this.validStatus = validStatus;
	}


	public String getIsDzh() {
		return isDzh;
	}


	public void setIsDzh(String isDzh) {
		this.isDzh = isDzh;
	}

    /**
     * 结算方式，券款对付、见券付款、见款付券
     */
    private String deliveryType;

    /**
     * 清算类型
     */
    private String settType;

    public String getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(String deliveryType) {
        this.deliveryType = deliveryType;
    }

    public String getSettType() {
        return settType;
    }

    public void setSettType(String settType) {
        this.settType = settType;
    }
}
