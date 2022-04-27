/*
 * 文件名：TargetFinProduct.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：yangjian
 * 修改时间：2018年5月25日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.common.batch.finproduct.entity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 标的信息 - 实体类
 * 
 * @author yangjian
 * @version 1.0 2018年5月25日
 * @see TargetFinProduct
 * @since 1.0
 */
public class TargetFinProduct {

    /**
     * 金融产品代码
     */
    private String finprodId;

    /**
     * 金融产品类型
     */
    private String finprodType;

    /**
     * 金融产品简称
     */
    private String finprodAbbr;

    /**
     * 金融产品全称
     */
    private String finprodName;

    /**
     * 收益类型
     */
    private String profitType;

    /**
     * 息票品种
     */
    private String couponSpecies;

    /**
     * 通道代码
     */
    private String chlFinprodId;

    /**
     * 市场代码
     */
    private String finprodMarketId;

    /**
     * 发行价
     */
    private BigDecimal issuePrice;

    /**
     * 发行规模
     */
    private BigDecimal issueAmt;

    /**
     * 币种
     */
    private String ccy;

    /**
     * 境内外
     */
    private String blnArea;


    /**
     * 期限品种
     */
    private String termType;

    // /**
    // * 关联日历
    // */
    // private String dealCalId;
    /**
     * 交易日历
     */
    private String calendarId;

    /**
     * 交易场所
     */
    private String tradeMarket;

    /**
     * 发行方式
     */
    private String issueType;

    /**
     * 发行人
     */
    private String issuer;

    /**
     * 发行日
     */
    private Date idate;

    /**
     * 起息日
     */
    private Date vdate;

    /**
     * 到期日
     */
    private Date mdate;

    /**
     * 实际到期日
     */
    private Date actmdate;

    /**
     * 是否通道
     */
    private String isChl;

    /**
     * 是否永续
     */
    private String isSus;

    /**
     * 是否含权
     */
    private String isRight;
    /**
     * 含权债摊销日
     */
    private String exeDate;

    /**
     * 母金融产品代码
     */
    private String pFinprodId;

    /**
     * 原金融产品代码
     */
    private String oFinprodId;

    /**
     * 原金融产品类型
     */
    private String oFinprodType;

    /**
     * 销售渠道
     */
/*    private String saleChannel;*/

    /**
     * 销售分层
     */
    private String saleLayer;

    /**
     * 销售期次
     */
    private int salePeriod;

    /**
     * 开放类型
     */
    private String openType;

    /**
     * 基金结转频率
     */
    private String carryoverFreq;

    /**
     * 申购确认速度
     */
    private int purSpeed;

    /**
     * 赎回确认速度
     */
    private int redSpeed;

    /**
     * 是否按日复利
     */
    private String isCompoundInt;

    /**
     * 面值
     */
    private BigDecimal faceValue;

    /**
     * 分类1（资产类型、证券细分类）
     */
    private String type1;

    /**
     * 分类2
     */
    private String type2;

    /**
     * 分类3
     */
    private String type3;

    /**
     * 分类4
     */
    private String type4;

    /**
     * 分类5
     */
    private String type5;

    /**
     * 分类6
     */
    private String type6;
    /**
     * 分类18（证券品种）
     */
    private String type18;

    /**
     * 登记托管机构
     */
    private String registOrg;

    /**
     * 是否其他债权，其他资产都为空，只有项目类资产为：Y；
     */
    private String isQtzq;
    

    /**
     * 分支序号
     */
    private Long branch;
    
    /**
     * 分支序号类型
     */
    private String branchType;


    public String getBranchType() {
		return branchType;
	}

	public void setBranchType(String branchType) {
		this.branchType = branchType;
	}

	public Long getBranch() {
		return branch;
	}

	public void setBranch(Long branch) {
		this.branch = branch;
	}

	/**
     * 取得 登记托管机构
     * @return 登记托管机构
     */
    public String getRegistOrg() {
        return registOrg;
    }

    /**
     * 设置 登记托管机构
     * @param registOrg 登记托管机构
     */
    public void setRegistOrg(String registOrg) {
        this.registOrg = registOrg == null ? null : registOrg.trim();
    }
    
    /**
     * 获取 finprodId
     * 
     * @return finprodId.
     */
    public String getFinprodId() {
        return finprodId;
    }

    /**
     * 设置 finprodId
     * 
     * @param finprodId finprodId
     */
    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
    }

    /**
     * 获取 finprodType
     * 
     * @return finprodType.
     */
    public String getFinprodType() {
        return finprodType;
    }

    /**
     * 设置 finprodType
     * 
     * @param finprodType finprodType
     */
    public void setFinprodType(String finprodType) {
        this.finprodType = finprodType;
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
     * 获取 finprodName
     * 
     * @return finprodName.
     */
    public String getFinprodName() {
        return finprodName;
    }

    /**
     * 设置 finprodName
     * 
     * @param finprodName finprodName
     */
    public void setFinprodName(String finprodName) {
        this.finprodName = finprodName;
    }

    /**
     * 获取 profitType
     * 
     * @return profitType.
     */
    public String getProfitType() {
        return profitType;
    }

    /**
     * 设置 profitType
     * 
     * @param profitType profitType
     */
    public void setProfitType(String profitType) {
        this.profitType = profitType;
    }

    /**
     * 获取 couponSpecies
     * 
     * @return couponSpecies.
     */
    public String getCouponSpecies() {
        return couponSpecies;
    }

    /**
     * 设置 couponSpecies
     * 
     * @param couponSpecies couponSpecies
     */
    public void setCouponSpecies(String couponSpecies) {
        this.couponSpecies = couponSpecies;
    }

    /**
     * 获取 chlFinprodId
     * 
     * @return chlFinprodId.
     */
    public String getChlFinprodId() {
        return chlFinprodId;
    }

    /**
     * 设置 chlFinprodId
     * 
     * @param chlFinprodId chlFinprodId
     */
    public void setChlFinprodId(String chlFinprodId) {
        this.chlFinprodId = chlFinprodId;
    }

    /**
     * 获取 finprodMarketId
     * 
     * @return finprodMarketId.
     */
    public String getFinprodMarketId() {
        return finprodMarketId;
    }

    /**
     * 设置 finprodMarketId
     * 
     * @param finprodMarketId finprodMarketId
     */
    public void setFinprodMarketId(String finprodMarketId) {
        this.finprodMarketId = finprodMarketId;
    }

    /**
     * 获取 issuePrice
     * 
     * @return issuePrice.
     */
    public BigDecimal getIssuePrice() {
        return issuePrice;
    }

    /**
     * 设置 issuePrice
     * 
     * @param issuePrice issuePrice
     */
    public void setIssuePrice(BigDecimal issuePrice) {
        this.issuePrice = issuePrice;
    }

    /**
     * 获取 issueAmt
     * 
     * @return issueAmt.
     */
    public BigDecimal getIssueAmt() {
        return issueAmt;
    }

    /**
     * 设置 issueAmt
     * 
     * @param issueAmt issueAmt
     */
    public void setIssueAmt(BigDecimal issueAmt) {
        this.issueAmt = issueAmt;
    }

    /**
     * 获取 ccy
     * 
     * @return ccy.
     */
    public String getCcy() {
        return ccy;
    }

    /**
     * 设置 ccy
     * 
     * @param ccy ccy
     */
    public void setCcy(String ccy) {
        this.ccy = ccy;
    }

    /**
     * 获取 blnArea
     * 
     * @return blnArea.
     */
    public String getBlnArea() {
        return blnArea;
    }

    /**
     * 设置 blnArea
     * 
     * @param blnArea blnArea
     */
    public void setBlnArea(String blnArea) {
        this.blnArea = blnArea;
    }

    /**
     * 获取 交易日历
     * 
     * @return 交易日历
     */
    public String getCalendarId() {
        return calendarId;
    }

    /**
     * 设置 交易日历
     * 
     * @param calendarId 交易日历
     */
    public void setCalendarId(String calendarId) {
        this.calendarId = calendarId;
    }

    /**
     * 获取 tradeMarket
     * 
     * @return tradeMarket.
     */
    public String getTradeMarket() {
        return tradeMarket;
    }

    /**
     * 设置 tradeMarket
     * 
     * @param tradeMarket tradeMarket
     */
    public void setTradeMarket(String tradeMarket) {
        this.tradeMarket = tradeMarket;
    }

    /**
     * 获取 issueType
     * 
     * @return issueType.
     */
    public String getIssueType() {
        return issueType;
    }

    /**
     * 设置 issueType
     * 
     * @param issueType issueType
     */
    public void setIssueType(String issueType) {
        this.issueType = issueType;
    }

    /**
     * 获取 issuer
     * 
     * @return issuer.
     */
    public String getIssuer() {
        return issuer;
    }

    /**
     * 设置 issuer
     * 
     * @param issuer issuer
     */
    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    /**
     * 获取 idate
     * 
     * @return idate.
     */
    public Date getIdate() {
        return idate;
    }

    /**
     * 设置 idate
     * 
     * @param idate idate
     */
    public void setIdate(Date idate) {
        this.idate = idate;
    }

    /**
     * 获取 vdate
     * 
     * @return vdate.
     */
    public Date getVdate() {
        return vdate;
    }

    /**
     * 设置 vdate
     * 
     * @param vdate vdate
     */
    public void setVdate(Date vdate) {
        this.vdate = vdate;
    }

    /**
     * 获取 mdate
     * 
     * @return mdate.
     */
    public Date getMdate() {
        return mdate;
    }

    /**
     * 设置 mdate
     * 
     * @param mdate mdate
     */
    public void setMdate(Date mdate) {
        this.mdate = mdate;
    }

    /**
     * 获取 actmdate
     * 
     * @return actmdate.
     */
    public Date getActmdate() {
        return actmdate;
    }

    /**
     * 设置 actmdate
     * 
     * @param actmdate actmdate
     */
    public void setActmdate(Date actmdate) {
        this.actmdate = actmdate;
    }

    /**
     * 获取 isChl
     * 
     * @return isChl.
     */
    public String getIsChl() {
        return isChl;
    }

    /**
     * 设置 isChl
     * 
     * @param isChl isChl
     */
    public void setIsChl(String isChl) {
        this.isChl = isChl;
    }

    /**
     * 获取 isSus
     * 
     * @return isSus.
     */
    public String getIsSus() {
        return isSus;
    }

    /**
     * 设置 isSus
     * 
     * @param isSus isSus
     */
    public void setIsSus(String isSus) {
        this.isSus = isSus;
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
     * 获取 pFinprodId
     * 
     * @return pFinprodId.
     */
    public String getpFinprodId() {
        return pFinprodId;
    }

    /**
     * 设置 pFinprodId
     * 
     * @param pFinprodId pFinprodId
     */
    public void setpFinprodId(String pFinprodId) {
        this.pFinprodId = pFinprodId;
    }

    /**
     * 获取 oFinprodId
     * 
     * @return oFinprodId.
     */
    public String getoFinprodId() {
        return oFinprodId;
    }

    /**
     * 设置 oFinprodId
     * 
     * @param oFinprodId oFinprodId
     */
    public void setoFinprodId(String oFinprodId) {
        this.oFinprodId = oFinprodId;
    }

    /**
     * 获取 oFinprodType
     * 
     * @return oFinprodType.
     */
    public String getoFinprodType() {
        return oFinprodType;
    }

    /**
     * 设置 oFinprodType
     * 
     * @param oFinprodType oFinprodType
     */
    public void setoFinprodType(String oFinprodType) {
        this.oFinprodType = oFinprodType;
    }

    /**
     * 获取 saleChannel
     * 
     * @return saleChannel.
     */
/*    public String getSaleChannel() {
        return saleChannel;
    }*/

    /**
     * 设置 saleChannel
     * 
     * @param saleChannel saleChannel
     */
/*    public void setSaleChannel(String saleChannel) {
        this.saleChannel = saleChannel;
    }*/

    /**
     * 获取 saleLayer
     * 
     * @return saleLayer.
     */
    public String getSaleLayer() {
        return saleLayer;
    }

    /**
     * 设置 saleLayer
     * 
     * @param saleLayer saleLayer
     */
    public void setSaleLayer(String saleLayer) {
        this.saleLayer = saleLayer;
    }

    /**
     * 获取 salePeriod
     * 
     * @return salePeriod.
     */
    public int getSalePeriod() {
        return salePeriod;
    }

    /**
     * 设置 salePeriod
     * 
     * @param salePeriod salePeriod
     */
    public void setSalePeriod(int salePeriod) {
        this.salePeriod = salePeriod;
    }

    /**
     * 获取 openType
     * 
     * @return openType.
     */
    public String getOpenType() {
        return openType;
    }

    /**
     * 设置 openType
     * 
     * @param openType openType
     */
    public void setOpenType(String openType) {
        this.openType = openType;
    }

    /**
     * 获取 carryoverFreq
     * 
     * @return carryoverFreq.
     */
    public String getCarryoverFreq() {
        return carryoverFreq;
    }

    /**
     * 设置 carryoverFreq
     * 
     * @param carryoverFreq carryoverFreq
     */
    public void setCarryoverFreq(String carryoverFreq) {
        this.carryoverFreq = carryoverFreq;
    }

    /**
     * 获取 purSpeed
     * 
     * @return purSpeed.
     */
    public int getPurSpeed() {
        return purSpeed;
    }

    /**
     * 设置 purSpeed
     * 
     * @param purSpeed purSpeed
     */
    public void setPurSpeed(int purSpeed) {
        this.purSpeed = purSpeed;
    }

    /**
     * 获取 redSpeed
     * 
     * @return redSpeed.
     */
    public int getRedSpeed() {
        return redSpeed;
    }

    /**
     * 设置 redSpeed
     * 
     * @param redSpeed redSpeed
     */
    public void setRedSpeed(int redSpeed) {
        this.redSpeed = redSpeed;
    }

    /**
     * 获取 是否按日复利
     * 
     * @return 是否按日复利
     */
    public String getIsCompoundInt() {
        return isCompoundInt;
    }

    /**
     * 设置 是否按日复利
     * 
     * @param isCompoundInt 是否按日复利
     */
    public void setIsCompoundInt(String isCompoundInt) {
        this.isCompoundInt = isCompoundInt;
    }

    /**
     * 获取 type1
     * 
     * @return type1.
     */
    public String getType1() {
        return type1;
    }

    /**
     * 设置 type1
     * 
     * @param type1 type1
     */
    public void setType1(String type1) {
        this.type1 = type1;
    }

    /**
     * 获取 type2
     * 
     * @return type2.
     */
    public String getType2() {
        return type2;
    }

    /**
     * 设置 type2
     * 
     * @param type2 type2
     */
    public void setType2(String type2) {
        this.type2 = type2;
    }

    /**
     * 获取 type3
     * 
     * @return type3.
     */
    public String getType3() {
        return type3;
    }

    /**
     * 设置 type3
     * 
     * @param type3 type3
     */
    public void setType3(String type3) {
        this.type3 = type3;
    }

    /**
     * 获取 type4
     * 
     * @return type4.
     */
    public String getType4() {
        return type4;
    }

    /**
     * 设置 type4
     * 
     * @param type4 type4
     */
    public void setType4(String type4) {
        this.type4 = type4;
    }

    /**
     * 获取 type5
     * 
     * @return type5.
     */
    public String getType5() {
        return type5;
    }

    /**
     * 设置 type5
     * 
     * @param type5 type5
     */
    public void setType5(String type5) {
        this.type5 = type5;
    }

    /**
     * 获取 type6
     * 
     * @return type6.
     */
    public String getType6() {
        return type6;
    }

    /**
     * 设置 type6
     * 
     * @param type6 type6
     */
    public void setType6(String type6) {
        this.type6 = type6;
    }

    
    /**
     * 获取 exeDate
     * @return exeDate.
     */
    public String getExeDate() {
        return exeDate;
    }

    
    /**
     * 设置 exeDate
     * @param exeDate exeDate
     */
    public void setExeDate(String exeDate) {
        this.exeDate = exeDate;
    }



    public String getTermType() {
        return termType;
    }


    public void setTermType(String termType) {
        this.termType = termType;
    }

    /**
     * 设置 faceValue 面值
     * @param faceValue
     */
    public BigDecimal getFaceValue() {
        return faceValue;
    }

    /**
     * 获取 faceValue 面值
     * @return faceValue.
     */
    public void setFaceValue(BigDecimal faceValue) {
        this.faceValue = faceValue;
    }

    /**
     * 获取 isQtzq 是否其他债券
     * @return isQtzq.
     */
    public String getIsQtzq() {
        return isQtzq;
    }

    /**
     * 设置 isQtzq 是否其他债券
     * @param isQtzq
     */
    public void setIsQtzq(String isQtzq) {
        this.isQtzq = isQtzq;
    }

    public String getType18() {
        return type18;
    }

    public void setType18(String type18) {
        this.type18 = type18;
    }
}

