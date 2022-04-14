package com.joyintech.tams.common.base.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.framework.json.CustomDateDeserialize;
import java.math.BigDecimal;
import java.util.Date;

public class FinProductAdd extends BaseEntity {
    /**
     * 金融产品代码
     */
    private String finprodId;

    /**
     * 销售部门，零售产品管理部、金融同业部、对公产品和现金管理部
     */
    private String saleDepartment;

    /**
     * 销售分层
     */
    private String saleLayer;

    /**
     * 销售期次
     */
    private Long salePeriod;

    /**
     * 申购确认速度，如果资产只有一个确认速度，则存该字段
     */
    private Long purSpeed;

    /**
     * 赎回确认速度
     */
    private Long redSpeed;

    /**
     * 是否次级债
     */
    private String isSecBond;

    /**
     * WIND对象ID
     */
    private String windId;

    /**
     * 是否接口自动更新
     */
    private String isAutoUpdate;

    /**
     * 投资组合代码
     */
    private String portfolioId;

    /**
     * 产品登记编码
     */
    private String prodRegistCode;

    /**
     * 是否周期型
     */
    private String isCycle;

    /**
     * 是否分层
     */
    private String isLay;

    /**
     * 分层类型，金额分层、期限分层、客户类型
     */
    private String layType;

    /**
     * 投资性质，固定收益类、权益类、商品及金融衍生品类、混合类
     */
    private String investNature;

    /**
     * 收益标识，非保本浮动收益型、保本浮动、保证收益型
     */
    private String profitFlag;

    /**
     * 销售模式，直销、代销、委托
     */
    private String saleMode;

    /**
     * 中债报送编码
     */
    private String chbId;

    /**
     * 产品模板代码
     */
    private String tmplId;

    /**
     * 封闭期限(天)，产品每次开放对应的封闭期限
     */
    private Long closeDays;

    /**
     * 产品发行状态
     */
    private String issueStatus;

    /**
     * 发行状态备注
     */
    private String issueStatusRemark;

    /**
     * 是否按日复利
     */
    private String isCompoundInt;

    /**
     * 期限品种
     */
    private String termType;

    /**
     * 面值，债券单位面值
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal faceValue;

    /**
     * 划款方式
     */
    private String payType;

    /**
     * 是否对账
     */
    private String isReconciliation;

    /**
     * 是否现金管理类
     */
    private String isCashManage;

    /**
     * 产品风险等级
     */
    private String riskLevel;

    /**
     * 处理模式
     */
    private String dealMode;

    /**
     * 是否专属产品
     */
    private String isExclusive;

    /**
     * 投标日期
     */
    private Date bidDate;

    /**
     * 上市板
     */
    private String board;

    /**
     * 是否沪港通或深港通
     */
    private String shOrZh;

    /**
     * 是否融资融券标的
     */
    private String isMarginFinprod;

    /**
     * 城投债级别
     */
    private String cityBondLev;

    /**
     * 是否城投债
     */
    private String isCityBond;

    /**
     * 投资周期
     */
    private String investmentCycle;

    /**
     * 产品经理
     */
    private String prodManager;

    /**
     * 收取方式
     */
    private String collectType;

    /**
     * 监管评级
     */
    private String regulatoryRating;

    /**
     * 产品设计系列编号
     */
    private String prdDesignId;

    /**
     * 补充条款
     */
    private String addition;

    /**
     * 质押券置换安排
     */
    private String isPledgeRep;

    /**
     * 认购起始日
     */
    private Date toffStrdate;

    /**
     * 认购终止日
     */
    private Date toffEnddate;

    /**
     * 交易所基金简称
     */
    private String trmFundAbbr;

    /**
     * 交易所基金类型
     */
    private String trmFundType;

    /**
     * 分级基金
     */
    private String graFundType;

    /**
     * 申购赎回代码
     */
    private String purRedId;

    /**
     * 申购赎回简称
     */
    private String purRedAbbr;

    /**
     * 单笔申购金额下限
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal purStartLow;

    /**
     * 单笔赎回份额下限
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal redStartLow;

    /**
     * ETF类型
     */
    private String etfType;

    /**
     * ETF最小申购赎回单位
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal etfMinPrunit;

    /**
     * 是否有存单
     */
    private String isReceipt;

    /**
     * 是否净价
     */
    private String isNetprice;

    /**
     * 创建机构
     */
    private String createOrg;

    /**
     * 估值机构
     */
    private String valOrg;

    /**
     * 计息利率方式 01买断利率计提 02净投资收益率计提
     */
    private String calRateType;

    /**
     * 行内识别码
     */
    private String repFinprodInsId;

    /**
     * 存款类标的否可提前支取，用于风险限额，不影响存续期操作
     */
    private String isInadvance;

    /**
     * 无固定期限
     */
    private String noFixedTerm;

    /**
     * 是否纳入净额结算 Y:是 N:否
     */
    private String isNetSett;

    /**
     * 是否私募债 Y:是 N:否
     */
    private String isPpb;

    /**
     * 交易平台
     */
    private String tradePlat;

    /**
     * 是否其他债权，其他资产都为空，只有项目类资产为：Y；
     */
    private String isQtzq;

    /**
     * 数据来源
     */
    private String dataSource;

    /**
     * 是否含有利率调整权 'N'-否，'Y'-是
     */
    private String isRateAdjust;

    /**
     * 风险五级分类
     */
    private String bondRiskType;

    /**
     * 增信措施
     */
    private String creditEnhanceMeasure;

    /**
     * 基金经理姓名
     */
    private String fundMangerName;

    /**
     * 允许的分红方式(多个用逗号隔开)
     */
    private String bonusType;

    /**
     * 当日回转交易
     */
    private String tdyRevTrans;

    /**
     * 上市日期
     */
    private Date listingDate;

    /**
     * 最小买卖单位
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal minSaleUnit;

    /**
     * 期货合约标的
     */
    private String futuresContractId;

    /**
     * 最后交易日
     */
    private Date lastTradeDate;

    /**
     * 最后交割日
     */
    private Date lastSettleDate;

    /**
     * 交割方式
     */
    private String deliveryMethod;

    /**
     * 合约乘数
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal contractMultiplier;

    /**
     * 是否包含有效抵质押物
     */
    private String isEffPledge;

    /**
     * 是否发行标的
     */
    private String isIssueAsset;

    /**
     * 上市代码
     */
    private String appearMarketId;

    /**
     * null
     */
    private String windCode;

    /**
     * 跨市场发行
     */
    private String crossMarket;

    /**
     * 跨市场发行代码,多个逗号分割
     */
    private String crossMarketCode;

    /**
     * 取得 金融产品代码
     * @return 金融产品代码
     */
    public String getFinprodId() {
        return finprodId;
    }

    /**
     * 设置 金融产品代码
     * @param finprodId 金融产品代码
     */
    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId == null ? null : finprodId.trim();
    }

    /**
     * 取得 销售部门，零售产品管理部、金融同业部、对公产品和现金管理部
     * @return 销售部门，零售产品管理部、金融同业部、对公产品和现金管理部
     */
    public String getSaleDepartment() {
        return saleDepartment;
    }

    /**
     * 设置 销售部门，零售产品管理部、金融同业部、对公产品和现金管理部
     * @param saleDepartment 销售部门，零售产品管理部、金融同业部、对公产品和现金管理部
     */
    public void setSaleDepartment(String saleDepartment) {
        this.saleDepartment = saleDepartment == null ? null : saleDepartment.trim();
    }

    /**
     * 取得 销售分层
     * @return 销售分层
     */
    public String getSaleLayer() {
        return saleLayer;
    }

    /**
     * 设置 销售分层
     * @param saleLayer 销售分层
     */
    public void setSaleLayer(String saleLayer) {
        this.saleLayer = saleLayer == null ? null : saleLayer.trim();
    }

    /**
     * 取得 销售期次
     * @return 销售期次
     */
    public Long getSalePeriod() {
        return salePeriod;
    }

    /**
     * 设置 销售期次
     * @param salePeriod 销售期次
     */
    public void setSalePeriod(Long salePeriod) {
        this.salePeriod = salePeriod;
    }

    /**
     * 取得 申购确认速度，如果资产只有一个确认速度，则存该字段
     * @return 申购确认速度，如果资产只有一个确认速度，则存该字段
     */
    public Long getPurSpeed() {
        return purSpeed;
    }

    /**
     * 设置 申购确认速度，如果资产只有一个确认速度，则存该字段
     * @param purSpeed 申购确认速度，如果资产只有一个确认速度，则存该字段
     */
    public void setPurSpeed(Long purSpeed) {
        this.purSpeed = purSpeed;
    }

    /**
     * 取得 赎回确认速度
     * @return 赎回确认速度
     */
    public Long getRedSpeed() {
        return redSpeed;
    }

    /**
     * 设置 赎回确认速度
     * @param redSpeed 赎回确认速度
     */
    public void setRedSpeed(Long redSpeed) {
        this.redSpeed = redSpeed;
    }

    /**
     * 取得 是否次级债
     * @return 是否次级债
     */
    public String getIsSecBond() {
        return isSecBond;
    }

    /**
     * 设置 是否次级债
     * @param isSecBond 是否次级债
     */
    public void setIsSecBond(String isSecBond) {
        this.isSecBond = isSecBond == null ? null : isSecBond.trim();
    }

    /**
     * 取得 WIND对象ID
     * @return WIND对象ID
     */
    public String getWindId() {
        return windId;
    }

    /**
     * 设置 WIND对象ID
     * @param windId WIND对象ID
     */
    public void setWindId(String windId) {
        this.windId = windId == null ? null : windId.trim();
    }

    /**
     * 取得 是否接口自动更新
     * @return 是否接口自动更新
     */
    public String getIsAutoUpdate() {
        return isAutoUpdate;
    }

    /**
     * 设置 是否接口自动更新
     * @param isAutoUpdate 是否接口自动更新
     */
    public void setIsAutoUpdate(String isAutoUpdate) {
        this.isAutoUpdate = isAutoUpdate == null ? null : isAutoUpdate.trim();
    }

    /**
     * 取得 投资组合代码
     * @return 投资组合代码
     */
    public String getPortfolioId() {
        return portfolioId;
    }

    /**
     * 设置 投资组合代码
     * @param portfolioId 投资组合代码
     */
    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId == null ? null : portfolioId.trim();
    }

    /**
     * 取得 产品登记编码
     * @return 产品登记编码
     */
    public String getProdRegistCode() {
        return prodRegistCode;
    }

    /**
     * 设置 产品登记编码
     * @param prodRegistCode 产品登记编码
     */
    public void setProdRegistCode(String prodRegistCode) {
        this.prodRegistCode = prodRegistCode == null ? null : prodRegistCode.trim();
    }

    /**
     * 取得 是否周期型
     * @return 是否周期型
     */
    public String getIsCycle() {
        return isCycle;
    }

    /**
     * 设置 是否周期型
     * @param isCycle 是否周期型
     */
    public void setIsCycle(String isCycle) {
        this.isCycle = isCycle == null ? null : isCycle.trim();
    }

    /**
     * 取得 是否分层
     * @return 是否分层
     */
    public String getIsLay() {
        return isLay;
    }

    /**
     * 设置 是否分层
     * @param isLay 是否分层
     */
    public void setIsLay(String isLay) {
        this.isLay = isLay == null ? null : isLay.trim();
    }

    /**
     * 取得 分层类型，金额分层、期限分层、客户类型
     * @return 分层类型，金额分层、期限分层、客户类型
     */
    public String getLayType() {
        return layType;
    }

    /**
     * 设置 分层类型，金额分层、期限分层、客户类型
     * @param layType 分层类型，金额分层、期限分层、客户类型
     */
    public void setLayType(String layType) {
        this.layType = layType == null ? null : layType.trim();
    }

    /**
     * 取得 投资性质，固定收益类、权益类、商品及金融衍生品类、混合类
     * @return 投资性质，固定收益类、权益类、商品及金融衍生品类、混合类
     */
    public String getInvestNature() {
        return investNature;
    }

    /**
     * 设置 投资性质，固定收益类、权益类、商品及金融衍生品类、混合类
     * @param investNature 投资性质，固定收益类、权益类、商品及金融衍生品类、混合类
     */
    public void setInvestNature(String investNature) {
        this.investNature = investNature == null ? null : investNature.trim();
    }

    /**
     * 取得 收益标识，非保本浮动收益型、保本浮动、保证收益型
     * @return 收益标识，非保本浮动收益型、保本浮动、保证收益型
     */
    public String getProfitFlag() {
        return profitFlag;
    }

    /**
     * 设置 收益标识，非保本浮动收益型、保本浮动、保证收益型
     * @param profitFlag 收益标识，非保本浮动收益型、保本浮动、保证收益型
     */
    public void setProfitFlag(String profitFlag) {
        this.profitFlag = profitFlag == null ? null : profitFlag.trim();
    }

    /**
     * 取得 销售模式，直销、代销、委托
     * @return 销售模式，直销、代销、委托
     */
    public String getSaleMode() {
        return saleMode;
    }

    /**
     * 设置 销售模式，直销、代销、委托
     * @param saleMode 销售模式，直销、代销、委托
     */
    public void setSaleMode(String saleMode) {
        this.saleMode = saleMode == null ? null : saleMode.trim();
    }

    /**
     * 取得 中债报送编码
     * @return 中债报送编码
     */
    public String getChbId() {
        return chbId;
    }

    /**
     * 设置 中债报送编码
     * @param chbId 中债报送编码
     */
    public void setChbId(String chbId) {
        this.chbId = chbId == null ? null : chbId.trim();
    }

    /**
     * 取得 产品模板代码
     * @return 产品模板代码
     */
    public String getTmplId() {
        return tmplId;
    }

    /**
     * 设置 产品模板代码
     * @param tmplId 产品模板代码
     */
    public void setTmplId(String tmplId) {
        this.tmplId = tmplId == null ? null : tmplId.trim();
    }

    /**
     * 取得 封闭期限(天)，产品每次开放对应的封闭期限
     * @return 封闭期限(天)，产品每次开放对应的封闭期限
     */
    public Long getCloseDays() {
        return closeDays;
    }

    /**
     * 设置 封闭期限(天)，产品每次开放对应的封闭期限
     * @param closeDays 封闭期限(天)，产品每次开放对应的封闭期限
     */
    public void setCloseDays(Long closeDays) {
        this.closeDays = closeDays;
    }

    /**
     * 取得 产品发行状态
     * @return 产品发行状态
     */
    public String getIssueStatus() {
        return issueStatus;
    }

    /**
     * 设置 产品发行状态
     * @param issueStatus 产品发行状态
     */
    public void setIssueStatus(String issueStatus) {
        this.issueStatus = issueStatus == null ? null : issueStatus.trim();
    }

    /**
     * 取得 发行状态备注
     * @return 发行状态备注
     */
    public String getIssueStatusRemark() {
        return issueStatusRemark;
    }

    /**
     * 设置 发行状态备注
     * @param issueStatusRemark 发行状态备注
     */
    public void setIssueStatusRemark(String issueStatusRemark) {
        this.issueStatusRemark = issueStatusRemark == null ? null : issueStatusRemark.trim();
    }

    /**
     * 取得 是否按日复利
     * @return 是否按日复利
     */
    public String getIsCompoundInt() {
        return isCompoundInt;
    }

    /**
     * 设置 是否按日复利
     * @param isCompoundInt 是否按日复利
     */
    public void setIsCompoundInt(String isCompoundInt) {
        this.isCompoundInt = isCompoundInt == null ? null : isCompoundInt.trim();
    }

    /**
     * 取得 期限品种
     * @return 期限品种
     */
    public String getTermType() {
        return termType;
    }

    /**
     * 设置 期限品种
     * @param termType 期限品种
     */
    public void setTermType(String termType) {
        this.termType = termType == null ? null : termType.trim();
    }

    /**
     * 取得 面值，债券单位面值
     * @return 面值，债券单位面值
     */
    public BigDecimal getFaceValue() {
        return faceValue;
    }

    /**
     * 设置 面值，债券单位面值
     * @param faceValue 面值，债券单位面值
     */
    public void setFaceValue(BigDecimal faceValue) {
        this.faceValue = faceValue;
    }

    /**
     * 取得 划款方式
     * @return 划款方式
     */
    public String getPayType() {
        return payType;
    }

    /**
     * 设置 划款方式
     * @param payType 划款方式
     */
    public void setPayType(String payType) {
        this.payType = payType == null ? null : payType.trim();
    }

    /**
     * 取得 是否对账
     * @return 是否对账
     */
    public String getIsReconciliation() {
        return isReconciliation;
    }

    /**
     * 设置 是否对账
     * @param isReconciliation 是否对账
     */
    public void setIsReconciliation(String isReconciliation) {
        this.isReconciliation = isReconciliation == null ? null : isReconciliation.trim();
    }

    /**
     * 取得 是否现金管理类
     * @return 是否现金管理类
     */
    public String getIsCashManage() {
        return isCashManage;
    }

    /**
     * 设置 是否现金管理类
     * @param isCashManage 是否现金管理类
     */
    public void setIsCashManage(String isCashManage) {
        this.isCashManage = isCashManage == null ? null : isCashManage.trim();
    }

    /**
     * 取得 产品风险等级
     * @return 产品风险等级
     */
    public String getRiskLevel() {
        return riskLevel;
    }

    /**
     * 设置 产品风险等级
     * @param riskLevel 产品风险等级
     */
    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel == null ? null : riskLevel.trim();
    }

    /**
     * 取得 处理模式
     * @return 处理模式
     */
    public String getDealMode() {
        return dealMode;
    }

    /**
     * 设置 处理模式
     * @param dealMode 处理模式
     */
    public void setDealMode(String dealMode) {
        this.dealMode = dealMode == null ? null : dealMode.trim();
    }

    /**
     * 取得 是否专属产品
     * @return 是否专属产品
     */
    public String getIsExclusive() {
        return isExclusive;
    }

    /**
     * 设置 是否专属产品
     * @param isExclusive 是否专属产品
     */
    public void setIsExclusive(String isExclusive) {
        this.isExclusive = isExclusive == null ? null : isExclusive.trim();
    }

    /**
     * 取得 投标日期
     * @return 投标日期
     */
    public Date getBidDate() {
        return bidDate;
    }

    /**
     * 设置 投标日期
     * @param bidDate 投标日期
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setBidDate(Date bidDate) {
        this.bidDate = bidDate;
    }

    /**
     * 取得 上市板
     * @return 上市板
     */
    public String getBoard() {
        return board;
    }

    /**
     * 设置 上市板
     * @param board 上市板
     */
    public void setBoard(String board) {
        this.board = board == null ? null : board.trim();
    }

    /**
     * 取得 是否沪港通或深港通
     * @return 是否沪港通或深港通
     */
    public String getShOrZh() {
        return shOrZh;
    }

    /**
     * 设置 是否沪港通或深港通
     * @param shOrZh 是否沪港通或深港通
     */
    public void setShOrZh(String shOrZh) {
        this.shOrZh = shOrZh == null ? null : shOrZh.trim();
    }

    /**
     * 取得 是否融资融券标的
     * @return 是否融资融券标的
     */
    public String getIsMarginFinprod() {
        return isMarginFinprod;
    }

    /**
     * 设置 是否融资融券标的
     * @param isMarginFinprod 是否融资融券标的
     */
    public void setIsMarginFinprod(String isMarginFinprod) {
        this.isMarginFinprod = isMarginFinprod == null ? null : isMarginFinprod.trim();
    }

    /**
     * 取得 城投债级别
     * @return 城投债级别
     */
    public String getCityBondLev() {
        return cityBondLev;
    }

    /**
     * 设置 城投债级别
     * @param cityBondLev 城投债级别
     */
    public void setCityBondLev(String cityBondLev) {
        this.cityBondLev = cityBondLev == null ? null : cityBondLev.trim();
    }

    /**
     * 取得 是否城投债
     * @return 是否城投债
     */
    public String getIsCityBond() {
        return isCityBond;
    }

    /**
     * 设置 是否城投债
     * @param isCityBond 是否城投债
     */
    public void setIsCityBond(String isCityBond) {
        this.isCityBond = isCityBond == null ? null : isCityBond.trim();
    }

    /**
     * 取得 投资周期
     * @return 投资周期
     */
    public String getInvestmentCycle() {
        return investmentCycle;
    }

    /**
     * 设置 投资周期
     * @param investmentCycle 投资周期
     */
    public void setInvestmentCycle(String investmentCycle) {
        this.investmentCycle = investmentCycle == null ? null : investmentCycle.trim();
    }

    /**
     * 取得 产品经理
     * @return 产品经理
     */
    public String getProdManager() {
        return prodManager;
    }

    /**
     * 设置 产品经理
     * @param prodManager 产品经理
     */
    public void setProdManager(String prodManager) {
        this.prodManager = prodManager == null ? null : prodManager.trim();
    }

    /**
     * 取得 收取方式
     * @return 收取方式
     */
    public String getCollectType() {
        return collectType;
    }

    /**
     * 设置 收取方式
     * @param collectType 收取方式
     */
    public void setCollectType(String collectType) {
        this.collectType = collectType == null ? null : collectType.trim();
    }

    /**
     * 取得 监管评级
     * @return 监管评级
     */
    public String getRegulatoryRating() {
        return regulatoryRating;
    }

    /**
     * 设置 监管评级
     * @param regulatoryRating 监管评级
     */
    public void setRegulatoryRating(String regulatoryRating) {
        this.regulatoryRating = regulatoryRating == null ? null : regulatoryRating.trim();
    }

    /**
     * 取得 产品设计系列编号
     * @return 产品设计系列编号
     */
    public String getPrdDesignId() {
        return prdDesignId;
    }

    /**
     * 设置 产品设计系列编号
     * @param prdDesignId 产品设计系列编号
     */
    public void setPrdDesignId(String prdDesignId) {
        this.prdDesignId = prdDesignId == null ? null : prdDesignId.trim();
    }

    /**
     * 取得 补充条款
     * @return 补充条款
     */
    public String getAddition() {
        return addition;
    }

    /**
     * 设置 补充条款
     * @param addition 补充条款
     */
    public void setAddition(String addition) {
        this.addition = addition == null ? null : addition.trim();
    }

    /**
     * 取得 质押券置换安排
     * @return 质押券置换安排
     */
    public String getIsPledgeRep() {
        return isPledgeRep;
    }

    /**
     * 设置 质押券置换安排
     * @param isPledgeRep 质押券置换安排
     */
    public void setIsPledgeRep(String isPledgeRep) {
        this.isPledgeRep = isPledgeRep == null ? null : isPledgeRep.trim();
    }

    /**
     * 取得 认购起始日
     * @return 认购起始日
     */
    public Date getToffStrdate() {
        return toffStrdate;
    }

    /**
     * 设置 认购起始日
     * @param toffStrdate 认购起始日
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setToffStrdate(Date toffStrdate) {
        this.toffStrdate = toffStrdate;
    }

    /**
     * 取得 认购终止日
     * @return 认购终止日
     */
    public Date getToffEnddate() {
        return toffEnddate;
    }

    /**
     * 设置 认购终止日
     * @param toffEnddate 认购终止日
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setToffEnddate(Date toffEnddate) {
        this.toffEnddate = toffEnddate;
    }

    /**
     * 取得 交易所基金简称
     * @return 交易所基金简称
     */
    public String getTrmFundAbbr() {
        return trmFundAbbr;
    }

    /**
     * 设置 交易所基金简称
     * @param trmFundAbbr 交易所基金简称
     */
    public void setTrmFundAbbr(String trmFundAbbr) {
        this.trmFundAbbr = trmFundAbbr == null ? null : trmFundAbbr.trim();
    }

    /**
     * 取得 交易所基金类型
     * @return 交易所基金类型
     */
    public String getTrmFundType() {
        return trmFundType;
    }

    /**
     * 设置 交易所基金类型
     * @param trmFundType 交易所基金类型
     */
    public void setTrmFundType(String trmFundType) {
        this.trmFundType = trmFundType == null ? null : trmFundType.trim();
    }

    /**
     * 取得 分级基金
     * @return 分级基金
     */
    public String getGraFundType() {
        return graFundType;
    }

    /**
     * 设置 分级基金
     * @param graFundType 分级基金
     */
    public void setGraFundType(String graFundType) {
        this.graFundType = graFundType == null ? null : graFundType.trim();
    }

    /**
     * 取得 申购赎回代码
     * @return 申购赎回代码
     */
    public String getPurRedId() {
        return purRedId;
    }

    /**
     * 设置 申购赎回代码
     * @param purRedId 申购赎回代码
     */
    public void setPurRedId(String purRedId) {
        this.purRedId = purRedId == null ? null : purRedId.trim();
    }

    /**
     * 取得 申购赎回简称
     * @return 申购赎回简称
     */
    public String getPurRedAbbr() {
        return purRedAbbr;
    }

    /**
     * 设置 申购赎回简称
     * @param purRedAbbr 申购赎回简称
     */
    public void setPurRedAbbr(String purRedAbbr) {
        this.purRedAbbr = purRedAbbr == null ? null : purRedAbbr.trim();
    }

    /**
     * 取得 单笔申购金额下限
     * @return 单笔申购金额下限
     */
    public BigDecimal getPurStartLow() {
        return purStartLow;
    }

    /**
     * 设置 单笔申购金额下限
     * @param purStartLow 单笔申购金额下限
     */
    public void setPurStartLow(BigDecimal purStartLow) {
        this.purStartLow = purStartLow;
    }

    /**
     * 取得 单笔赎回份额下限
     * @return 单笔赎回份额下限
     */
    public BigDecimal getRedStartLow() {
        return redStartLow;
    }

    /**
     * 设置 单笔赎回份额下限
     * @param redStartLow 单笔赎回份额下限
     */
    public void setRedStartLow(BigDecimal redStartLow) {
        this.redStartLow = redStartLow;
    }

    /**
     * 取得 ETF类型
     * @return ETF类型
     */
    public String getEtfType() {
        return etfType;
    }

    /**
     * 设置 ETF类型
     * @param etfType ETF类型
     */
    public void setEtfType(String etfType) {
        this.etfType = etfType == null ? null : etfType.trim();
    }

    /**
     * 取得 ETF最小申购赎回单位
     * @return ETF最小申购赎回单位
     */
    public BigDecimal getEtfMinPrunit() {
        return etfMinPrunit;
    }

    /**
     * 设置 ETF最小申购赎回单位
     * @param etfMinPrunit ETF最小申购赎回单位
     */
    public void setEtfMinPrunit(BigDecimal etfMinPrunit) {
        this.etfMinPrunit = etfMinPrunit;
    }

    /**
     * 取得 是否有存单
     * @return 是否有存单
     */
    public String getIsReceipt() {
        return isReceipt;
    }

    /**
     * 设置 是否有存单
     * @param isReceipt 是否有存单
     */
    public void setIsReceipt(String isReceipt) {
        this.isReceipt = isReceipt == null ? null : isReceipt.trim();
    }

    /**
     * 取得 是否净价
     * @return 是否净价
     */
    public String getIsNetprice() {
        return isNetprice;
    }

    /**
     * 设置 是否净价
     * @param isNetprice 是否净价
     */
    public void setIsNetprice(String isNetprice) {
        this.isNetprice = isNetprice == null ? null : isNetprice.trim();
    }

    /**
     * 取得 创建机构
     * @return 创建机构
     */
    public String getCreateOrg() {
        return createOrg;
    }

    /**
     * 设置 创建机构
     * @param createOrg 创建机构
     */
    public void setCreateOrg(String createOrg) {
        this.createOrg = createOrg == null ? null : createOrg.trim();
    }

    /**
     * 取得 估值机构
     * @return 估值机构
     */
    public String getValOrg() {
        return valOrg;
    }

    /**
     * 设置 估值机构
     * @param valOrg 估值机构
     */
    public void setValOrg(String valOrg) {
        this.valOrg = valOrg == null ? null : valOrg.trim();
    }

    /**
     * 取得 计息利率方式 01买断利率计提 02净投资收益率计提
     * @return 计息利率方式 01买断利率计提 02净投资收益率计提
     */
    public String getCalRateType() {
        return calRateType;
    }

    /**
     * 设置 计息利率方式 01买断利率计提 02净投资收益率计提
     * @param calRateType 计息利率方式 01买断利率计提 02净投资收益率计提
     */
    public void setCalRateType(String calRateType) {
        this.calRateType = calRateType == null ? null : calRateType.trim();
    }

    /**
     * 取得 行内识别码
     * @return 行内识别码
     */
    public String getRepFinprodInsId() {
        return repFinprodInsId;
    }

    /**
     * 设置 行内识别码
     * @param repFinprodInsId 行内识别码
     */
    public void setRepFinprodInsId(String repFinprodInsId) {
        this.repFinprodInsId = repFinprodInsId == null ? null : repFinprodInsId.trim();
    }

    /**
     * 取得 存款类标的否可提前支取，用于风险限额，不影响存续期操作
     * @return 存款类标的否可提前支取，用于风险限额，不影响存续期操作
     */
    public String getIsInadvance() {
        return isInadvance;
    }

    /**
     * 设置 存款类标的否可提前支取，用于风险限额，不影响存续期操作
     * @param isInadvance 存款类标的否可提前支取，用于风险限额，不影响存续期操作
     */
    public void setIsInadvance(String isInadvance) {
        this.isInadvance = isInadvance == null ? null : isInadvance.trim();
    }

    /**
     * 取得 无固定期限
     * @return 无固定期限
     */
    public String getNoFixedTerm() {
        return noFixedTerm;
    }

    /**
     * 设置 无固定期限
     * @param noFixedTerm 无固定期限
     */
    public void setNoFixedTerm(String noFixedTerm) {
        this.noFixedTerm = noFixedTerm == null ? null : noFixedTerm.trim();
    }

    /**
     * 取得 是否纳入净额结算 Y:是 N:否
     * @return 是否纳入净额结算 Y:是 N:否
     */
    public String getIsNetSett() {
        return isNetSett;
    }

    /**
     * 设置 是否纳入净额结算 Y:是 N:否
     * @param isNetSett 是否纳入净额结算 Y:是 N:否
     */
    public void setIsNetSett(String isNetSett) {
        this.isNetSett = isNetSett == null ? null : isNetSett.trim();
    }

    /**
     * 取得 是否私募债 Y:是 N:否
     * @return 是否私募债 Y:是 N:否
     */
    public String getIsPpb() {
        return isPpb;
    }

    /**
     * 设置 是否私募债 Y:是 N:否
     * @param isPpb 是否私募债 Y:是 N:否
     */
    public void setIsPpb(String isPpb) {
        this.isPpb = isPpb == null ? null : isPpb.trim();
    }

    /**
     * 取得 交易平台
     * @return 交易平台
     */
    public String getTradePlat() {
        return tradePlat;
    }

    /**
     * 设置 交易平台
     * @param tradePlat 交易平台
     */
    public void setTradePlat(String tradePlat) {
        this.tradePlat = tradePlat == null ? null : tradePlat.trim();
    }

    /**
     * 取得 是否其他债权，其他资产都为空，只有项目类资产为：Y；
     * @return 是否其他债权，其他资产都为空，只有项目类资产为：Y；
     */
    public String getIsQtzq() {
        return isQtzq;
    }

    /**
     * 设置 是否其他债权，其他资产都为空，只有项目类资产为：Y；
     * @param isQtzq 是否其他债权，其他资产都为空，只有项目类资产为：Y；
     */
    public void setIsQtzq(String isQtzq) {
        this.isQtzq = isQtzq == null ? null : isQtzq.trim();
    }

    /**
     * 取得 数据来源
     * @return 数据来源
     */
    public String getDataSource() {
        return dataSource;
    }

    /**
     * 设置 数据来源
     * @param dataSource 数据来源
     */
    public void setDataSource(String dataSource) {
        this.dataSource = dataSource == null ? null : dataSource.trim();
    }

    /**
     * 取得 是否含有利率调整权 'N'-否，'Y'-是
     * @return 是否含有利率调整权 'N'-否，'Y'-是
     */
    public String getIsRateAdjust() {
        return isRateAdjust;
    }

    /**
     * 设置 是否含有利率调整权 'N'-否，'Y'-是
     * @param isRateAdjust 是否含有利率调整权 'N'-否，'Y'-是
     */
    public void setIsRateAdjust(String isRateAdjust) {
        this.isRateAdjust = isRateAdjust == null ? null : isRateAdjust.trim();
    }

    /**
     * 取得 风险五级分类
     * @return 风险五级分类
     */
    public String getBondRiskType() {
        return bondRiskType;
    }

    /**
     * 设置 风险五级分类
     * @param bondRiskType 风险五级分类
     */
    public void setBondRiskType(String bondRiskType) {
        this.bondRiskType = bondRiskType == null ? null : bondRiskType.trim();
    }

    /**
     * 取得 增信措施
     * @return 增信措施
     */
    public String getCreditEnhanceMeasure() {
        return creditEnhanceMeasure;
    }

    /**
     * 设置 增信措施
     * @param creditEnhanceMeasure 增信措施
     */
    public void setCreditEnhanceMeasure(String creditEnhanceMeasure) {
        this.creditEnhanceMeasure = creditEnhanceMeasure == null ? null : creditEnhanceMeasure.trim();
    }

    /**
     * 取得 基金经理姓名
     * @return 基金经理姓名
     */
    public String getFundMangerName() {
        return fundMangerName;
    }

    /**
     * 设置 基金经理姓名
     * @param fundMangerName 基金经理姓名
     */
    public void setFundMangerName(String fundMangerName) {
        this.fundMangerName = fundMangerName == null ? null : fundMangerName.trim();
    }

    /**
     * 取得 允许的分红方式(多个用逗号隔开)
     * @return 允许的分红方式(多个用逗号隔开)
     */
    public String getBonusType() {
        return bonusType;
    }

    /**
     * 设置 允许的分红方式(多个用逗号隔开)
     * @param bonusType 允许的分红方式(多个用逗号隔开)
     */
    public void setBonusType(String bonusType) {
        this.bonusType = bonusType == null ? null : bonusType.trim();
    }

    /**
     * 取得 当日回转交易
     * @return 当日回转交易
     */
    public String getTdyRevTrans() {
        return tdyRevTrans;
    }

    /**
     * 设置 当日回转交易
     * @param tdyRevTrans 当日回转交易
     */
    public void setTdyRevTrans(String tdyRevTrans) {
        this.tdyRevTrans = tdyRevTrans == null ? null : tdyRevTrans.trim();
    }

    /**
     * 取得 上市日期
     * @return 上市日期
     */
    public Date getListingDate() {
        return listingDate;
    }

    /**
     * 设置 上市日期
     * @param listingDate 上市日期
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setListingDate(Date listingDate) {
        this.listingDate = listingDate;
    }

    /**
     * 取得 最小买卖单位
     * @return 最小买卖单位
     */
    public BigDecimal getMinSaleUnit() {
        return minSaleUnit;
    }

    /**
     * 设置 最小买卖单位
     * @param minSaleUnit 最小买卖单位
     */
    public void setMinSaleUnit(BigDecimal minSaleUnit) {
        this.minSaleUnit = minSaleUnit;
    }

    /**
     * 取得 期货合约标的
     * @return 期货合约标的
     */
    public String getFuturesContractId() {
        return futuresContractId;
    }

    /**
     * 设置 期货合约标的
     * @param futuresContractId 期货合约标的
     */
    public void setFuturesContractId(String futuresContractId) {
        this.futuresContractId = futuresContractId == null ? null : futuresContractId.trim();
    }

    /**
     * 取得 最后交易日
     * @return 最后交易日
     */
    public Date getLastTradeDate() {
        return lastTradeDate;
    }

    /**
     * 设置 最后交易日
     * @param lastTradeDate 最后交易日
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setLastTradeDate(Date lastTradeDate) {
        this.lastTradeDate = lastTradeDate;
    }

    /**
     * 取得 最后交割日
     * @return 最后交割日
     */
    public Date getLastSettleDate() {
        return lastSettleDate;
    }

    /**
     * 设置 最后交割日
     * @param lastSettleDate 最后交割日
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setLastSettleDate(Date lastSettleDate) {
        this.lastSettleDate = lastSettleDate;
    }

    /**
     * 取得 交割方式
     * @return 交割方式
     */
    public String getDeliveryMethod() {
        return deliveryMethod;
    }

    /**
     * 设置 交割方式
     * @param deliveryMethod 交割方式
     */
    public void setDeliveryMethod(String deliveryMethod) {
        this.deliveryMethod = deliveryMethod == null ? null : deliveryMethod.trim();
    }

    /**
     * 取得 合约乘数
     * @return 合约乘数
     */
    public BigDecimal getContractMultiplier() {
        return contractMultiplier;
    }

    /**
     * 设置 合约乘数
     * @param contractMultiplier 合约乘数
     */
    public void setContractMultiplier(BigDecimal contractMultiplier) {
        this.contractMultiplier = contractMultiplier;
    }

    /**
     * 取得 是否包含有效抵质押物
     * @return 是否包含有效抵质押物
     */
    public String getIsEffPledge() {
        return isEffPledge;
    }

    /**
     * 设置 是否包含有效抵质押物
     * @param isEffPledge 是否包含有效抵质押物
     */
    public void setIsEffPledge(String isEffPledge) {
        this.isEffPledge = isEffPledge == null ? null : isEffPledge.trim();
    }

    /**
     * 取得 是否发行标的
     * @return 是否发行标的
     */
    public String getIsIssueAsset() {
        return isIssueAsset;
    }

    /**
     * 设置 是否发行标的
     * @param isIssueAsset 是否发行标的
     */
    public void setIsIssueAsset(String isIssueAsset) {
        this.isIssueAsset = isIssueAsset == null ? null : isIssueAsset.trim();
    }

    /**
     * 取得 上市代码
     * @return 上市代码
     */
    public String getAppearMarketId() {
        return appearMarketId;
    }

    /**
     * 设置 上市代码
     * @param appearMarketId 上市代码
     */
    public void setAppearMarketId(String appearMarketId) {
        this.appearMarketId = appearMarketId == null ? null : appearMarketId.trim();
    }

    /**
     * 取得 null
     * @return null
     */
    public String getWindCode() {
        return windCode;
    }

    /**
     * 设置 null
     * @param windCode null
     */
    public void setWindCode(String windCode) {
        this.windCode = windCode == null ? null : windCode.trim();
    }

    /**
     * 取得 跨市场发行
     * @return 跨市场发行
     */
    public String getCrossMarket() {
        return crossMarket;
    }

    /**
     * 设置 跨市场发行
     * @param crossMarket 跨市场发行
     */
    public void setCrossMarket(String crossMarket) {
        this.crossMarket = crossMarket == null ? null : crossMarket.trim();
    }

    /**
     * 取得 跨市场发行代码,多个逗号分割
     * @return 跨市场发行代码,多个逗号分割
     */
    public String getCrossMarketCode() {
        return crossMarketCode;
    }

    /**
     * 设置 跨市场发行代码,多个逗号分割
     * @param crossMarketCode 跨市场发行代码,多个逗号分割
     */
    public void setCrossMarketCode(String crossMarketCode) {
        this.crossMarketCode = crossMarketCode == null ? null : crossMarketCode.trim();
    }
}