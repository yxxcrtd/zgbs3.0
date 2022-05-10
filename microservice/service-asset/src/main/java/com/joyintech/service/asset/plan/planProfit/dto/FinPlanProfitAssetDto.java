/*
 * 文件名：FinPlanProfitAssetDto.java 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved. 描述：理财资管管理系统V2.0 修改人：liuwei 修改时间：2018年9月14日 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.service.asset.plan.planProfit.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.service.asset.common.dto.BussFinRecommentDto;
import com.joyintech.service.asset.plan.planNet.dto.FinPlanNetAssetDto;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.invest.common.dto.MstFileInputParameter;

import java.math.BigDecimal;
import java.util.List;
import com.joyintech.service.product.common.dto.BussFinProductOpenPlanDto;
import javax.validation.Valid;

/**
 * 〈一句话功能简述〉 〈功能详细描述〉
 * 
 * @author liuwei
 * @version 1.0 2018年9月14日
 * @see FinPlanProfitAssetDto
 * @since 1.0
 */
public class FinPlanProfitAssetDto extends FinProduct {

    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    private static final long serialVersionUID = 398124932926546323L;
    /* 中债其他要素entity 20191015*/
    /**
     * 中债其他要素entity
     */
private RepAssetOther repAssetOther;
    /**
     * 获取 repAssetOther
     * @return repAssetOther.
     */
    public RepAssetOther getRepAssetOther() {
        return repAssetOther;
    }
    /**
     * 设置 repAssetOther
     *
     */
    public void setRepAssetOther(RepAssetOther repAssetOther) {
        this.repAssetOther = repAssetOther;
    }
    /* 中债其他要素entity 20191015*/
    /**
     * 标的类金融产品entity
     */
    private FinProduct finProduct;

    /**
     * 金融产品附属表entity
     */
    private FinProductAdd finProductAdd;

    /**
     * 金融产品分类表entity
     */
    private FinProductType finProductType;

    /**
     * 金融产品扩展(平安个性)
     */
    private PaFinProduct paFinProduct;

    /**
     * 金融产品现金流信息表dtolist
     */
    private List<FinCashInfo> finCashInfoList;

    /**
     * 现金流计算规则dtolist
     */
    private List<FinCashCalcRule> finCashCalcRuleList;

    /**
     * 现金流利率信息表dtolist
     */
    private List<FinRateInfo> finRateInfoList;

    /**
     * 现金流支付规则dtolist
     */
    private List<FinCashPayRule> finCashPayRuleList;

    /**
     * 现金流计划dtolist
     */
    private List<FinCashPlan> finCashPlanList;

    /**
     * 现金流计算规则扩展
     */
    private PaFinCashCalcRule paFinCashCalcRule;

    /**
     * 金融产品买断利率表
     */
    private PaFinProductBuyoutRate paFinProductBuyoutRate;

    /**
     * 资产负债报送信息entity
     */
    private RepAssetDept repAssetDept;

    /**
     * 资产管理计划报送信息entity
     */
    private RepAssetMngPlan repAssetMngPlan;

    /**
     * 委外投资—协议方式报送信息entity
     */
    private RepTrustInvestMode repTrustInvestMode;

    // add by dongzhiling start at 2019-01-03
    /**
     * 组合entity
     */
    private PtlPortfolio ptlPortfolio;

    // add end
    /**
     * 文件上传输入参数
     */
    private MstFileInputParameter mstFileInputParameter;

    /**
     * 操作类型
     */
    private String operateType;

    /**
     * 界面类型
     */
    private String pageModel;

    /**
     * 选中的数据列表
     */
    private transient List<FinPlanNetAssetDto> selectedItem;

    /**
     * 画面元素
     */
    private String elementId;

    /**
     * 起息日开始日期
     */
    private String startVDate;

    /**
     * 起息日结束日期
     */
    private String endVDate;

    /**
     * 到息日开始日期
     */
    private String startMDate;

    /**
     * 到息日结束日期
     */
    private String endMDate;

    /**
     * 一级分类
     */
    private String category;

    /**
     * 二级分类
     */
    private String type;

    /**
	 * 资产二级分类
	 */
	private String type9;
	/**
	 * 资产三级分类
	 */
	private String type10;
	/**
	 * 资产四级分类
	 */
	private String type11;
	/**
	 * 资产五级分类
	 */
	private String type12;

    /**
     * 资产/负债类别
     */
    private String repAssetType;

    /**
     * 是否每日开放申赎
     */
    private String isOpenEveryDay;

    /**
     * {@linkplain}
     */
    @Valid
    private BussFinProductOpenPlanDto bussFinProductOpenPlanDto;
    /**
     * add by zhangweiqi
     */
    @Valid
    private BussFinProductOpenPlanDto bussFinProductOpenPlanRedDto;

	/**
	 * 获取 type9
	 * 
	 * @return type9.
	 */
	public String getType9() {
		return type9;
	}

	/**
	 * 设置 type9
	 * 
	 * @param type9
	 *            type9
	 */
	public void setType9(String type9) {
		this.type9 = type9;
	}

	/**
	 * 获取 type10
	 * 
	 * @return type10.
	 */
	public String getType10() {
		return type10;
	}

	/**
	 * 设置 type10
	 * 
	 * @param type10
	 *            type10
	 */
	public void setType10(String type10) {
		this.type10 = type10;
	}

	/**
	 * 获取 type11
	 * 
	 * @return type11.
	 */
	public String getType11() {
		return type11;
	}

	/**
	 * 设置 type11
	 * 
	 * @param type11
	 *            type11
	 */
	public void setType11(String type11) {
		this.type11 = type11;
	}

	/**
	 * 获取 type12
	 * 
	 * @return type12.
	 */
	public String getType12() {
		return type12;
	}

	/**
	 * 设置 type12
	 * 
	 * @param type12
	 *            type12
	 */
	public void setType12(String type12) {
		this.type12 = type12;
	}
    /* end、zhuyongkang、2019.7.19 */ 
	/**
     * 交易方式
     */
    private String transMode;

    /**
     * 出账编号
     */
    private String cmsId;

    /**
     * 信贷分类
     */
    private String cmsCreditType;

    /**
     * BECIF号
     */
    private String cmsBecif;

    /**
     * 关联合同编号
     */
    private String cmsContractId;

    /**
     * 信贷客户编号
     */
    private String creditCustomerNo;

    /**
     * 清算时效（工作日）
     */
    private String settleDays;

    /**
     * 资产提供行
     */
    private String assetProvider;

    /**
     * 资产来源
     */
    private String assetSource;

    /**
     * 资产来源条线
     */
    private String assetSourceUnit;

    /**
     * 放款分行
     */
    private String loadBank;

    private String parentPlan;

    /**
     * 买断利率%
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal buyOutRate;

    /**
     * 授信额度代码
     */
    private String seqno;

    /**
     * 授信配置代码
     */
    private String creditMstId;

    /**
     * 授信客户编号（机构授信）/金融产品代码（资产授信）/资产系列代码（资产系列授信）
     */
    private String customerId;

    /**
     * 批复流水号
     */
    private String pflsh;

    /**
     * 已占用额度，初始值为0
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal hapCreditAmt;

    /**
     * 实际授信主体
     */
    private String actualCreditProvid;

    /**
     * 是否占用主体授信
     */
    private String creditFlag;

    /**
     * 投资性质
     */
    private String investNature;

    /**
     * 法审编号
     */
    private String trialNo;

    // 增加推荐方与推荐费率 add by renping 20191014 start
    /**
     * 推荐方
     */
    private String recommender;

    /**
     * 取得 推荐方
     * @return 推荐方
     */
    public String getRecommender() {
        return recommender;
    }

    /**
     * 设置 推荐方
     * @param recommender 推荐方
     */
    public void setRecommender(String recommender) {
        this.recommender = recommender == null ? null : recommender.trim();
    }
    /**
     * 资产推荐方名称
     */
    private String recommenderName;

    /**
     * 计息利率方式
     * add by jiangPeng on 20200721
     */
    private String calRateType;

    /**
     * 增信措施
     */
    private String creditEnhanceMeasure;

    /**
     * 基金经理姓名
     */
    private String fundMangerName;


    /**
     * 取得 资产推荐方名称
     * @return 资产推荐方名称
     */
    public String getRecommenderName() {
        return recommenderName;
    }

    /**
     * 设置 资产推荐方名称
     * @param recommenderName 资产推荐方名称
     */
    public void setRecommenderName(String recommenderName) {
        this.recommenderName = recommender == null ? null : recommenderName.trim();
    }

    /**
     * 资产推荐方及费率信息
     */
    private BussFinRecommentDto bussFinRecommentDto;

    /**
     * 获取 bussFinRecommentDto
     * 
     * @return bussFinRecommentDto.
     */
    public BussFinRecommentDto getBussFinRecommentDto() {
        return bussFinRecommentDto;
    }

    /**
     * 设置 bussFinRecommentDto
     * 
     * @param bussFinRecommentDto bussFinRecommentDto
     */
    public void setBussFinRecommentDto(BussFinRecommentDto bussFinRecommentDto) {
        this.bussFinRecommentDto = bussFinRecommentDto;
    }
    // 增加推荐方与推荐费率 add by renping 20191014 end
    // 增加人行信托代码 by 赵东东 start 
    /**
     *人行信托代码 
     */
    private String bankTrustCode;
    /**
     * 获取 bankTrustCode
     * 
     * @return bankTrustCode.
     */
    public String getBankTrustCode() {
		return bankTrustCode;
	}
    /**
     * 设置 bankTrustCode
     * 
     * @param bankTrustCode bankTrustCode
     */
	public void setBankTrustCode(String bankTrustCode) {
		this.bankTrustCode = bankTrustCode;
	}
	// 增加人行信托代码 by 赵东东 end
	/**
     * 获取 creditFlag
     * 
     * @return creditFlag.
     */
    public String getCreditFlag() {
        return creditFlag;
    }

    /**
     * 设置 creditFlag
     * 
     * @param creditFlag creditFlag
     */
    public void setCreditFlag(String creditFlag) {
        this.creditFlag = creditFlag;
    }

    /**
     * 获取 operateType
     * 
     * @return operateType.
     */
    public String getOperateType() {
        return operateType;
    }

    /**
     * 设置 operateType
     * 
     * @param operateType operateType
     */
    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }

    /**
     * 获取 pageModel
     * 
     * @return pageModel.
     */
    public String getPageModel() {
        return pageModel;
    }

    /**
     * 设置 pageModel
     * 
     * @param pageModel pageModel
     */
    public void setPageModel(String pageModel) {
        this.pageModel = pageModel;
    }

    /**
     * 获取 selectedItem
     * 
     * @return selectedItem.
     */
    public List<FinPlanNetAssetDto> getSelectedItem() {
        return selectedItem;
    }

    /**
     * 设置 selectedItem
     * 
     * @param selectedItem selectedItem
     */
    public void setSelectedItem(List<FinPlanNetAssetDto> selectedItem) {
        this.selectedItem = selectedItem;
    }

    /**
     * 获取 elementId
     * 
     * @return elementId.
     */
    public String getElementId() {
        return elementId;
    }

    /**
     * 设置 elementId
     * 
     * @param elementId elementId
     */
    public void setElementId(String elementId) {
        this.elementId = elementId;
    }

    /**
     * 获取 startVDate
     * 
     * @return startVDate.
     */
    public String getStartVDate() {
        return startVDate;
    }

    /**
     * 设置 startVDate
     * 
     * @param startVDate startVDate
     */
    public void setStartVDate(String startVDate) {
        this.startVDate = startVDate;
    }

    /**
     * 获取 endVDate
     * 
     * @return endVDate.
     */
    public String getEndVDate() {
        return endVDate;
    }

    /**
     * 设置 endVDate
     * 
     * @param endVDate endVDate
     */
    public void setEndVDate(String endVDate) {
        this.endVDate = endVDate;
    }

    /**
     * 获取 startMDate
     * 
     * @return startMDate.
     */
    public String getStartMDate() {
        return startMDate;
    }

    /**
     * 设置 startMDate
     * 
     * @param startMDate startMDate
     */
    public void setStartMDate(String startMDate) {
        this.startMDate = startMDate;
    }

    /**
     * 获取 endMDate
     * 
     * @return endMDate.
     */
    public String getEndMDate() {
        return endMDate;
    }

    /**
     * 设置 endMDate
     * 
     * @param endMDate endMDate
     */
    public void setEndMDate(String endMDate) {
        this.endMDate = endMDate;
    }

    /**
     * 获取 category
     * 
     * @return category.
     */
    public String getCategory() {
        return category;
    }

    /**
     * 设置 category
     * 
     * @param category category
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * 获取 type
     * 
     * @return type.
     */
    public String getType() {
        return type;
    }

    /**
     * 设置 type
     * 
     * @param type type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * 获取 transMode
     * 
     * @return transMode.
     */
    public String getTransMode() {
        return transMode;
    }

    /**
     * 设置 transMode
     * 
     * @param transMode transMode
     */
    public void setTransMode(String transMode) {
        this.transMode = transMode;
    }

    /**
     * 获取 cmsBecif
     * 
     * @return cmsBecif.
     */
    public String getCmsBecif() {
        return cmsBecif;
    }

    /**
     * 设置 cmsBecif
     * 
     * @param cmsBecif cmsBecif
     */
    public void setCmsBecif(String cmsBecif) {
        this.cmsBecif = cmsBecif;
    }

    /**
     * 获取 cmsContractId
     * 
     * @return cmsContractId.
     */
    public String getCmsContractId() {
        return cmsContractId;
    }

    /**
     * 设置 cmsContractId
     * 
     * @param cmsContractId cmsContractId
     */
    public void setCmsContractId(String cmsContractId) {
        this.cmsContractId = cmsContractId;
    }

    /**
     * 获取 finProduct
     *
     * @return finProduct
     */
    public FinProduct getFinProduct() {
        return finProduct;
    }

    /**
     * 设置 finProduct
     *
     * @param finProduct finProduct
     */
    public void setFinProduct(FinProduct finProduct) {
        this.finProduct = finProduct;
    }

    /**
     * 获取 finProductAdd
     *
     * @return finProductAdd
     */
    public FinProductAdd getFinProductAdd() {
        return finProductAdd;
    }

    /**
     * 设置 finProductAdd
     *
     * @param finProductAdd finProductAdd
     */
    public void setFinProductAdd(FinProductAdd finProductAdd) {
        this.finProductAdd = finProductAdd;
    }

    /**
     * 获取 finProductType
     *
     * @return finProductType
     */
    public FinProductType getFinProductType() {
        return finProductType;
    }

    /**
     * 设置 finProductType
     *
     * @param finProductType finProductType
     */
    public void setFinProductType(FinProductType finProductType) {
        this.finProductType = finProductType;
    }

    /**
     * 获取 finCashInfoList
     *
     * @return finCashInfoList
     */
    public List<FinCashInfo> getFinCashInfoList() {
        return finCashInfoList;
    }

    /**
     * 设置 finCashInfoList
     *
     * @param finCashInfoList finCashInfoList
     */
    public void setFinCashInfoList(List<FinCashInfo> finCashInfoList) {
        this.finCashInfoList = finCashInfoList;
    }

    /**
     * 获取 finCashCalcRuleList
     *
     * @return finCashCalcRuleList
     */
    public List<FinCashCalcRule> getFinCashCalcRuleList() {
        return finCashCalcRuleList;
    }

    /**
     * 设置 finCashCalcRuleList
     *
     * @param finCashCalcRuleList finCashCalcRuleList
     */
    public void setFinCashCalcRuleList(List<FinCashCalcRule> finCashCalcRuleList) {
        this.finCashCalcRuleList = finCashCalcRuleList;
    }

    /**
     * 获取 finRateInfoList
     *
     * @return finRateInfoList
     */
    public List<FinRateInfo> getFinRateInfoList() {
        return finRateInfoList;
    }

    /**
     * 设置 finRateInfoList
     *
     * @param finRateInfoList finRateInfoList
     */
    public void setFinRateInfoList(List<FinRateInfo> finRateInfoList) {
        this.finRateInfoList = finRateInfoList;
    }

    /**
     * 获取 finCashPayRuleList
     *
     * @return finCashPayRuleList
     */
    public List<FinCashPayRule> getFinCashPayRuleList() {
        return finCashPayRuleList;
    }

    /**
     * 设置 finCashPayRuleList
     *
     * @param finCashPayRuleList finCashPayRuleList
     */
    public void setFinCashPayRuleList(List<FinCashPayRule> finCashPayRuleList) {
        this.finCashPayRuleList = finCashPayRuleList;
    }

    /**
     * 获取 finCashPlanList
     *
     * @return finCashPlanList
     */
    public List<FinCashPlan> getFinCashPlanList() {
        return finCashPlanList;
    }

    /**
     * 设置 finCashPlanList
     *
     * @param finCashPlanList finCashPlanList
     */
    public void setFinCashPlanList(List<FinCashPlan> finCashPlanList) {
        this.finCashPlanList = finCashPlanList;
    }

    /**
     * 获取 paFinCashCalcRule
     *
     * @return paFinCashCalcRule
     */
    public PaFinCashCalcRule getPaFinCashCalcRule() {
        return paFinCashCalcRule;
    }

    /**
     * 设置 paFinCashCalcRule
     *
     * @param paFinCashCalcRule paFinCashCalcRule
     */
    public void setPaFinCashCalcRule(PaFinCashCalcRule paFinCashCalcRule) {
        this.paFinCashCalcRule = paFinCashCalcRule;
    }

    /**
     * 获取 repAssetDept
     *
     * @return repAssetDept
     */
    public RepAssetDept getRepAssetDept() {
        return repAssetDept;
    }

    /**
     * 设置 repAssetDept
     *
     * @param repAssetDept repAssetDept
     */
    public void setRepAssetDept(RepAssetDept repAssetDept) {
        this.repAssetDept = repAssetDept;
    }

    /**
     * 获取 repAssetMngPlan
     *
     * @return repAssetMngPlan
     */
    public RepAssetMngPlan getRepAssetMngPlan() {
        return repAssetMngPlan;
    }

    /**
     * 设置 repAssetMngPlan
     *
     * @param repAssetMngPlan repAssetMngPlan
     */
    public void setRepAssetMngPlan(RepAssetMngPlan repAssetMngPlan) {
        this.repAssetMngPlan = repAssetMngPlan;
    }

    /**
     * 获取 repTrustInvestMode
     *
     * @return repTrustInvestMode
     */
    public RepTrustInvestMode getRepTrustInvestMode() {
        return repTrustInvestMode;
    }

    /**
     * 设置 repTrustInvestMode
     *
     * @param repTrustInvestMode repTrustInvestMode
     */
    public void setRepTrustInvestMode(RepTrustInvestMode repTrustInvestMode) {
        this.repTrustInvestMode = repTrustInvestMode;
    }

    /**
     * 获取 settleDays
     *
     * @return settleDays
     */
    public String getSettleDays() {
        return settleDays;
    }

    /**
     * 设置 settleDays
     *
     * @param settleDays settleDays
     */
    public void setSettleDays(String settleDays) {
        this.settleDays = settleDays;
    }

    /**
     * 获取 assetProvider
     *
     * @return assetProvider
     */
    public String getAssetProvider() {
        return assetProvider;
    }

    /**
     * 设置 assetProvider
     *
     * @param assetProvider assetProvider
     */
    public void setAssetProvider(String assetProvider) {
        this.assetProvider = assetProvider;
    }

    /**
     * 获取 assetSourceUnit
     *
     * @return assetSourceUnit
     */
    public String getAssetSourceUnit() {
        return assetSourceUnit;
    }

    /**
     * 设置 assetSourceUnit
     *
     * @param assetSourceUnit assetSourceUnit
     */
    public void setAssetSourceUnit(String assetSourceUnit) {
        this.assetSourceUnit = assetSourceUnit;
    }

    /**
     * 获取 loadBank
     *
     * @return loadBank
     */
    public String getLoadBank() {
        return loadBank;
    }

    /**
     * 设置 loadBank
     *
     * @param loadBank loadBank
     */
    public void setLoadBank(String loadBank) {
        this.loadBank = loadBank;
    }

    /**
     * 获取 paFinProductBuyoutRate
     *
     * @return paFinProductBuyoutRate
     */
    public PaFinProductBuyoutRate getPaFinProductBuyoutRate() {
        return paFinProductBuyoutRate;
    }

    /**
     * 设置 paFinProductBuyoutRate
     *
     * @param paFinProductBuyoutRate paFinProductBuyoutRate
     */
    public void setPaFinProductBuyoutRate(PaFinProductBuyoutRate paFinProductBuyoutRate) {
        this.paFinProductBuyoutRate = paFinProductBuyoutRate;
    }

    /**
     * 获取 buyOutRate
     *
     * @return buyOutRate
     */
    public BigDecimal getBuyOutRate() {
        return buyOutRate;
    }

    /**
     * 设置 buyOutRate
     *
     * @param buyOutRate buyOutRate
     */
    public void setBuyOutRate(BigDecimal buyOutRate) {
        this.buyOutRate = buyOutRate;
    }

    /**
     * 获取 assetSource
     *
     * @return assetSource
     */
    public String getAssetSource() {
        return assetSource;
    }

    /**
     * 设置 assetSource
     *
     * @param assetSource assetSource
     */
    public void setAssetSource(String assetSource) {
        this.assetSource = assetSource;
    }

    /**
     * 获取 mstFileInputParameter
     *
     * @return mstFileInputParameter
     */
    public MstFileInputParameter getMstFileInputParameter() {
        return mstFileInputParameter;
    }

    /**
     * 设置 mstFileInputParameter
     *
     * @param mstFileInputParameter mstFileInputParameter
     */
    public void setMstFileInputParameter(MstFileInputParameter mstFileInputParameter) {
        this.mstFileInputParameter = mstFileInputParameter;
    }

    /**
     * 获取 cmsId
     *
     * @return cmsId
     */
    public String getCmsId() {
        return cmsId;
    }

    /**
     * 设置 cmsId
     *
     * @param cmsId cmsId
     */
    public void setCmsId(String cmsId) {
        this.cmsId = cmsId;
    }

    /**
     * 获取 cmsCreditType
     *
     * @return cmsCreditType
     */
    public String getCmsCreditType() {
        return cmsCreditType;
    }

    /**
     * 设置 cmsCreditType
     *
     * @param cmsCreditType cmsCreditType
     */
    public void setCmsCreditType(String cmsCreditType) {
        this.cmsCreditType = cmsCreditType;
    }

    /**
     * @return the ptlPortfolio
     */
    public PtlPortfolio getPtlPortfolio() {
        return ptlPortfolio;
    }

    /**
     * @param ptlPortfolio the ptlPortfolio to set
     */
    public void setPtlPortfolio(PtlPortfolio ptlPortfolio) {
        this.ptlPortfolio = ptlPortfolio;
    }

    /**
     * 获取 creditCustomerNo
     * 
     * @return creditCustomerNo.
     */
    public String getCreditCustomerNo() {
        return creditCustomerNo;
    }

    /**
     * 设置 creditCustomerNo
     * 
     * @param creditCustomerNo creditCustomerNo
     */
    public void setCreditCustomerNo(String creditCustomerNo) {
        this.creditCustomerNo = creditCustomerNo;
    }

    /**
     * 获取 seqno
     * 
     * @return seqno.
     */
    public String getSeqno() {
        return seqno;
    }

    /**
     * 设置 seqno
     * 
     * @param seqno seqno
     */
    public void setSeqno(String seqno) {
        this.seqno = seqno;
    }

    /**
     * 获取 creditMstId
     * 
     * @return creditMstId.
     */
    public String getCreditMstId() {
        return creditMstId;
    }

    /**
     * 设置 creditMstId
     * 
     * @param creditMstId creditMstId
     */
    public void setCreditMstId(String creditMstId) {
        this.creditMstId = creditMstId;
    }

    /**
     * 获取 customerId
     * 
     * @return customerId.
     */
    public String getCustomerId() {
        return customerId;
    }

    /**
     * 设置 customerId
     * 
     * @param customerId customerId
     */
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    /**
     * 获取 pflsh
     * 
     * @return pflsh.
     */
    public String getPflsh() {
        return pflsh;
    }

    /**
     * 设置 pflsh
     * 
     * @param pflsh pflsh
     */
    public void setPflsh(String pflsh) {
        this.pflsh = pflsh;
    }

    /**
     * 获取 hapCreditAmt
     * 
     * @return hapCreditAmt.
     */
    public BigDecimal getHapCreditAmt() {
        return hapCreditAmt;
    }

    /**
     * 设置 hapCreditAmt
     * 
     * @param hapCreditAmt hapCreditAmt
     */
    public void setHapCreditAmt(BigDecimal hapCreditAmt) {
        this.hapCreditAmt = hapCreditAmt;
    }

    /**
     * 获取 actualCreditProvid
     * 
     * @return actualCreditProvid.
     */
    public String getActualCreditProvid() {
        return actualCreditProvid;
    }

    /**
     * 设置 actualCreditProvid
     * 
     * @param actualCreditProvid actualCreditProvid
     */
    public void setActualCreditProvid(String actualCreditProvid) {
        this.actualCreditProvid = actualCreditProvid;
    }

    /**
     * 获取 parentPlan
     * 
     * @return parentPlan.
     */
    public String getParentPlan() {
        return parentPlan;
    }

    /**
     * 设置 parentPlan
     * 
     * @param parentPlan parentPlan
     */
    public void setParentPlan(String parentPlan) {
        this.parentPlan = parentPlan;
    }

    /**
     * 获取 investNature
     *
     * @return investNature
     */
    public String getInvestNature() {
        return investNature;
    }

    /**
     * 设置 investNature
     *
     * @param investNature investNature
     */
    public void setInvestNature(String investNature) {
        this.investNature = investNature;
    }

    public PaFinProduct getPaFinProduct() {
        return paFinProduct;
    }

    public void setPaFinProduct(PaFinProduct paFinProduct) {
        this.paFinProduct = paFinProduct;
    }

    public String getTrialNo() {
        return trialNo;
    }

    public void setTrialNo(String trialNo) {
        this.trialNo = trialNo;
    }

    public String getRepAssetType() {
        return repAssetType;
    }

    public void setRepAssetType(String repAssetType) {
        this.repAssetType = repAssetType;
    }

    /**
     * 计息利率方式
     * add by jiangPeng on 20200721
     */
    public String getCalRateType() {
        return calRateType;
    }

    /**
     * 计息利率方式
     * add by jiangPeng on 20200721
     */
    public void setCalRateType(String calRateType) {
        this.calRateType = calRateType;
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
        this.creditEnhanceMeasure = creditEnhanceMeasure;
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
        this.fundMangerName = fundMangerName;
    }

    public String getIsOpenEveryDay() {
        return isOpenEveryDay;
    }

    public void setIsOpenEveryDay(String isOpenEveryDay) {
        this.isOpenEveryDay = isOpenEveryDay;
    }

    /**
     * 获取 bussFinProductOpenPlanDto
     * @return bussFinProductOpenPlanDto.
     */
    public BussFinProductOpenPlanDto getBussFinProductOpenPlanDto() {
        return bussFinProductOpenPlanDto;
    }

    /**
     * 设置 bussFinProductOpenPlanDto
     * @param bussFinProductOpenPlanDto bussFinProductOpenPlanDto
     */
    public void setBussFinProductOpenPlanDto(BussFinProductOpenPlanDto bussFinProductOpenPlanDto) {
        this.bussFinProductOpenPlanDto = bussFinProductOpenPlanDto;
    }

    public BussFinProductOpenPlanDto getBussFinProductOpenPlanRedDto() {
        return bussFinProductOpenPlanRedDto;
    }

    public void setBussFinProductOpenPlanRedDto(BussFinProductOpenPlanDto bussFinProductOpenPlanRedDto) {
        this.bussFinProductOpenPlanRedDto = bussFinProductOpenPlanRedDto;
    }
}
