/*
 * 文件名：InterfAssetInfo.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangrui1
 * 修改时间：2019年9月17日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.service.riskrdsinf.info.rsk099.entity;

import java.math.BigDecimal;
import java.util.Date;

import com.joyintech.framework.base.BaseEntity;
import com.joyintech.service.riskrdsinf.core.annotation.DictLink;
import com.joyintech.service.riskrdsinf.core.annotation.Index;
import com.joyintech.service.riskrdsinf.core.constant.InfDictCode;
import com.joyintech.tams.invest.common.riskinfrds.info.entity.IAssetInfo;

/**
 * 资产基本信息受体类
 * 
 * @author zhangrui1
 * @version 1.0 2019年9月17日
 * @see InterfAssetInfo
 * @since 1.0
 */
public class InterfAssetInfo extends BaseEntity implements IAssetInfo {

    /**
     * 序列号
     */
    private static final long serialVersionUID = -3094202480650667954L;

    /**
     * 资产代码
     */
    private String assetCode;

    /**
     * 资产名称
     */
    private String assetName;

    /**
     * 起息日
     */
    private Date vdate;

    /**
     * 到期日
     */
    private Date mdate;

    /**
     * 系统资产大类
     */
    private String assetType;

    /**
     * 系统资产细类
     */
    private String assetType2;

    /**
     * 监管一级分类
     */
    private String type1;

    /**
     * 监管二级分类
     */
    private String type2;

    /**
     * WIND一级分类
     */
    private String windType1;

    /**
     * WIND二级分类
     */
    private String windType2;

    /**
     * 投资性质
     */
    private String investType;

    /**
     * 交易日历代码
     */
    private String calendarCode;

    /**
     * 市场代码
     */
    private String marketCode;

    /**
     * 交易场所
     */
    private String tradePlace;

    /**
     * 发行人
     */
    private String issuer;

    /**
     * 发行人名称
     */
    private String issuerName;

    /**
     * wind行业一级分类
     */
    private String industry;

    /**
     * wind行业二级分类
     */
    private String industry2;

    /**
     * 金融产品类型
     */
    private String finprodType;

    /**
     * 发行规模
     */
    private BigDecimal issueAmt;

    /**
     * 取得 资产代码
     * 
     * @return 资产代码
     */
    public String getAssetCode() {
        return this.assetCode;
    }

    /**
     * 设置 资产代码
     * 
     * @param assetCode 资产代码
     */
    @Index(2)
    public void setAssetCode(String assetCode) {
        this.assetCode = assetCode;
    }

    /**
     * 取得 资产名称
     * 
     * @return 资产名称
     */
    public String getAssetName() {
        return this.assetName;
    }

    /**
     * 设置 资产名称
     * 
     * @param assetName 资产名称
     */
    @Index(3)
    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    /**
     * 取得 起息日
     * 
     * @return 起息日
     */
    public Date getVdate() {
        return this.vdate;
    }

    /**
     * 设置 起息日
     * 
     * @param vdate 起息日
     */
    @Index(4)
    public void setVdate(Date vdate) {
        this.vdate = vdate;
    }

    /**
     * 取得 到期日
     * 
     * @return 到期日
     */
    public Date getMdate() {
        return this.mdate;
    }

    /**
     * 设置 到期日
     * 
     * @param mdate 到期日
     */
    @Index(5)
    public void setMdate(Date mdate) {
        this.mdate = mdate;
    }

    /**
     * 取得 系统资产大类
     * 
     * @return 系统资产大类
     */
    public String getAssetType() {
        return this.assetType;
    }

    /**
     * 设置 系统资产大类
     * 
     * @param assetType 系统资产大类
     */
    @Index(6)
    public void setAssetType(String assetType) {
        this.assetType = assetType;
    }

    /**
     * 取得 系统资产细类
     * 
     * @return 系统资产细类
     */
    public String getAssetType2() {
        return this.assetType2;
    }

    /**
     * 设置 系统资产细类
     * 
     * @param assetType2 系统资产细类
     */
    @Index(7)
    public void setAssetType2(String assetType2) {
        this.assetType2 = assetType2;
    }

    /**
     * 取得 监管一级分类
     * 
     * @return 监管一级分类
     */
    public String getType1() {
        return this.type1;
    }

    /**
     * 设置 监管一级分类
     * 
     * @param type1 监管一级分类
     */
    @Index(8)
    public void setType1(String type1) {
        this.type1 = type1;
    }

    /**
     * 取得 监管二级分类
     * 
     * @return 监管二级分类
     */
    public String getType2() {
        return this.type2;
    }

    /**
     * 设置 监管二级分类
     * 
     * @param type2 监管二级分类
     */
    @Index(9)
    public void setType2(String type2) {
        this.type2 = type2;
    }

    /**
     * 取得 WIND一级分类
     * 
     * @return WIND一级分类
     */
    public String getWindType1() {
        return this.windType1;
    }

    /**
     * 设置 WIND一级分类
     * 
     * @param windType1 WIND一级分类
     */
    public void setWindType1(String windType1) {
        this.windType1 = windType1;
    }

    /**
     * 取得 WIND二级分类
     * 
     * @return WIND二级分类
     */
    public String getWindType2() {
        return this.windType2;
    }

    /**
     * 设置 WIND二级分类
     * 
     * @param windType2 WIND二级分类
     */
    public void setWindType2(String windType2) {
        this.windType2 = windType2;
    }

    /**
     * 取得 投资性质
     * 
     * @return 投资性质
     */
    public String getInvestType() {
        return this.investType;
    }

    /**
     * 设置 投资性质
     * 
     * @param investType 投资性质
     */
    @DictLink(link = InfDictCode.投资性质)
    public void setInvestType(String investType) {
        this.investType = investType;
    }

    /**
     * 取得 交易日历代码
     * 
     * @return 交易日历代码
     */
    public String getCalendarCode() {
        return this.calendarCode;
    }

    /**
     * 设置 交易日历代码
     * 
     * @param calendarCode 交易日历代码
     */
    @Index(10)
    public void setCalendarCode(String calendarCode) {
        this.calendarCode = calendarCode;
    }

    /**
     * 取得 市场代码
     * 
     * @return 市场代码
     */
    public String getMarketCode() {
        return this.marketCode;
    }

    /**
     * 设置 市场代码
     * 
     * @param marketCode 市场代码
     */
    @Index(11)
    public void setMarketCode(String marketCode) {
        this.marketCode = marketCode;
    }

    /**
     * 取得 交易场所
     * 
     * @return 交易场所
     */
    public String getTradePlace() {
        return this.tradePlace;
    }

    /**
     * 设置 交易场所
     * 
     * @param tradePlace 交易场所
     */
    @Index(12)
    public void setTradePlace(String tradePlace) {
        this.tradePlace = tradePlace;
    }

    /**
     * 获取 发行人
     * 
     * @return 发行人.
     */
    public String getIssuer() {
        return issuer;
    }

    /**
     * 设置 发行人
     * 
     * @param issuer 发行人
     */
    @Index(13)
    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    /**
     * 获取 发行人名称
     * 
     * @return 发行人名称.
     */
    public String getIssuerName() {
        return issuerName;
    }

    /**
     * 设置 发行人名称
     * 
     * @param issuerName 发行人名称
     */
    @Index(14)
    public void setIssuerName(String issuerName) {
        this.issuerName = issuerName;
    }

    /**
     * 获取 wind行业一级分类
     * 
     * @return wind行业一级分类.
     */
    public String getIndustry() {
        return industry;
    }

    /**
     * 设置 wind行业一级分类
     * 
     * @param industry wind行业一级分类
     */
    public void setIndustry(String industry) {
        this.industry = industry;
    }

    /**
     * 获取 wind行业二级分类
     * 
     * @return wind行业二级分类.
     */
    public String getIndustry2() {
        return industry2;
    }

    /**
     * 设置 wind行业二级分类
     * 
     * @param industry2 wind行业二级分类
     */
    public void setIndustry2(String industry2) {
        this.industry2 = industry2;
    }

    /**
     * 获取 金融产品类型
     * 
     * @return 金融产品类型.
     */
    public String getFinprodType() {
        return finprodType;
    }

    /**
     * 设置 金融产品类型
     * 
     * @param finprodType 金融产品类型
     */
    @Index(6)
    @DictLink(link = InfDictCode.资产大类)
    public void setFinprodType(String finprodType) {
        this.finprodType = finprodType;
    }

	/**
	 * 取得 发行总额
	 * 
	 * @return 发行总额
	 */
	public BigDecimal getIssueAmt() {
		return issueAmt;
	}

	/**
	 * 设置 发行总额
	 * 
	 * @param issueAmt 发行总额
	 */
	public void setIssueAmt(BigDecimal issueAmt) {
		this.issueAmt = issueAmt != null ? issueAmt.multiply(new BigDecimal("100000000")) : issueAmt;
	}

	/**
	 * 所属债券评级信息
	 */
	private String bondGradeType;

	@Override
	public String getBondGradeType() {
		return bondGradeType;
	}

	/**
	 * 设置 所属债券评级信息
	 * 
	 * @param bondGradeType 所属债券评级信息
	 */
	public void setBondGradeType(String bondGradeType) {
		this.bondGradeType = bondGradeType;
	}

    /**
     * 所属主体评级信息
     */
    private String bodyGradeType;

    @Override
    public String getBodyGradeType() {
        return bodyGradeType;
    }

    public void setBodyGradeType(String bodyGradeType) {
        this.bodyGradeType = bodyGradeType;
    }

    @Override
    public String getIssueType() {
        return null;
    }

    @Override
    public String getType9() {
        return null;
    }

    @Override
    public String getType10() {
        return null;
    }

    @Override
    public String getIssuerCode() {
        return null;
    }

    @Override
    public String getListLevel() {
        return null;
    }

    @Override
    public String getIsCorOrg() {
        return null;
    }

    // add by zhangrui1 增加资产属性 start
    /**
     * {@inheritDoc}
     */
    @Override
    public String getIsSus() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getIsRight() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Date getVdateLast() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCalcType() {
        return null;
    }
    // add by zhangrui1 增加资产属性 end

    // add by etliu 增加资产属性:是否可提前支取 20200821 start
    /**
     * 取得 是否可提前支取
     *
     * @return 是否可提前支取
     */
    @Override
    public String getIsCanDrawInAdvance() {
        return null;
    }
    // add by etliu 增加资产属性:是否可提前支取 20200821 end

    // add by zhangrui1 增加资产属性:是否流动性受限 start
    /**
     * {@inheritDoc}
     */
    @Override
    public String getIsFlowLimited() {
        return null;
    }
    @Override
    public String getIsLiquidity1() {
        return null;
    }
    @Override
    public String getIsLiquidity2() {
        return null;
    }
    @Override
    public String getIsLiquidity3() {
        return null;
    }
    // add by zhangrui1 增加资产属性:是否流动性受限 end

    // add by zhangrui1 增加资产属性:机构主体类型 start
    /**
     * {@inheritDoc}
     */
    @Override
    public String getCustomerObjType() {
        return null;
    }
    // add by zhangrui1 增加资产属性:机构主体类型 end

    @Override
    public String getType11() {
        return null;
    }

    @Override
    public String getType12() {
        return null;
    }
    @Override
    public String getStockShareType() {
        return null;
    }
    @Override
    public String getStockTradeStatus() {
        return null;
    }
    @Override
    public Date getAstLastRedempDate() {
        return null;
    }

    @Override
    public Date getAstLastRebackDate() {
        return null;
    }

    @Override
    public Date getAstLastPayDate() {
        return null;
    }

    @Override
    public String getIsSevenDayCashable() {
        return null;
    }

    @Override
    public String getIsOpenEveryDay() {
        return null;
    }

    @Override
    public String getIsBadAsset() {
        return null;
    }
}
