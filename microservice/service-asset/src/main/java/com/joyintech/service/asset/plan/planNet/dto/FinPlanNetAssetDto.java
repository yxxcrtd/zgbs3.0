/*
 * 文件名：FinPlanAssetNet001Dto.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：liuwei
 * 修改时间：2018年9月10日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.service.asset.plan.planNet.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.service.asset.common.dto.BussFinRecommentDto;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.invest.common.dto.MstFileInputParameter;

import java.math.BigDecimal;
import java.util.List;
import com.joyintech.service.product.common.dto.BussFinProductOpenPlanDto;
import com.joyintech.tams.invest.common.dto.MstFileInputParameter;
import javax.validation.Valid;

/**
 * 〈一句话功能简述〉
 * 〈功能详细描述〉
 * @author liuwei
 * @version 1.0 2018年9月10日
 * @see FinPlanNetAssetDto
 * @since 1.0
 */
public class FinPlanNetAssetDto extends FinProduct{

    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    private static final long serialVersionUID = 398124932926546323L;

    /**
     * 标的类金融产品entity
     */
    private FinProduct finProduct;
    /**
     * 金融产品附属表entity
     */
    private FinProductAdd finProductAdd;
    //增加投后信息表by赵东东 2019.9.29
    /**
     * 投后信息表entity
     */
    private FinPostInvestInfo finPostInvestInfo;
    /**
     * 投后信息
     * @return
     */
    public FinPostInvestInfo getFinPostInvestInfo() {
		return finPostInvestInfo;
	}

	public void setFinPostInvestInfo(FinPostInvestInfo finPostInvestInfo) {
		this.finPostInvestInfo = finPostInvestInfo;
	}
	/**
     * 金融产品分类表entity
     */
    private FinProductType finProductType;
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
     * 组合entity
     */
    private PtlPortfolio ptlPortfolio;
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
    //增加中债资产其它要素entity by赵东东 start 
    /**
     * 中债资产其它要素entity
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
     * @param repAssetOther repAssetOther
     */
	public void setRepAssetOther(RepAssetOther repAssetOther) {
		this.repAssetOther = repAssetOther;
	}
	//增加中债资产其它要素entity by赵东东 end
	/**
     * 文件上传输入参数
     */
    private MstFileInputParameter mstFileInputParameter;
    /**
     *
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
    // 行内五级分类 zhangpuyu 2019-7-23 start
    /**
     * 行内二级分类
     */
    private String type9;
	/**
     * 行内三级分类
     */
    private String type10;
    /**
     * 行内四级分类
     */
    private String type11;
    /**
     * 行内五级分类
     */
    private String type12;
    /**
     * 申购确认速度，如果资产只有一个确认速度，则存该字段
     */
    private String purSpeed;

    /**
     * 赎回确认速度
     */
    private String redSpeed;
   //增加预警线，止损线，法审编号，业绩基准，超额分成方式 by赵东东 2019.9.30 start
    /**
     * 增加预警线
     */
    private String warnLine;
    /**
     * 止损线
     */
    private String flatLine;
    /**
     * 法审编号
     */
    private String trialNo;
    /**
     * 业绩基准
     */
    private String baseRule;
    /**
     * 超额分成方式
     */
    private String overallocation;

    /**
     * 资产/负债类别
     */
    private String repAssetType;
    /**
     * 获取 flatLine
     * @return flatLine.
     */
   public String getFlatLine() {
		return flatLine;
	}
   /**
    * 设置 flatLine
    * @param flatLine flatLine
    */
	public void setFlatLine(String flatLine) {
		this.flatLine = flatLine;
	}
	/**
     * 获取 trialNo
     * @return trialNo.
     */
	public String getTrialNo() {
		return trialNo;
	}
	/**
	    * 设置 trialNo
	    * @param trialNo trialNo
	    */
	public void setTrialNo(String trialNo) {
		this.trialNo = trialNo;
	}
	/**
     * 获取 baseRule
     * @return baseRule.
     */
	public String getBaseRule() {
		return baseRule;
	}
	/**
	    * 设置 baseRule
	    * @param baseRule baseRule
	    */
	public void setBaseRule(String baseRule) {
		this.baseRule = baseRule;
	}
	/**
     * 获取 overallocation
     * @return overallocation.
     */
	public String getOverallocation() {
		return overallocation;
	}
	/**
	    * 设置 overallocation
	    * @param overallocation overallocation
	    */
	public void setOverallocation(String overallocation) {
		this.overallocation = overallocation;
	}
	/**
     * 获取 warnLine
     * @return warnLine.
     */
    public String getWarnLine() {
		return warnLine;
	}
    /**
	    * 设置 warnLine
	    * @param warnLine warnLine
	    */
	public void setWarnLine(String warnLine) {
		this.warnLine = warnLine;
	}
	//增加预警线，止损线，法审编号，业绩基准，超额分成方式 by赵东东 2019.9.30 end
    /**
     * 获取 type9
     * @return type9.
     */
	public String getType9() {
		return type9;
	}

	/**
     * 设置 type9
     * @param type9 type9
     */
	public void setType9(String type9) {
		this.type9 = type9;
	}

	/**
     * 获取 type10
     * @return type10.
     */
	public String getType10() {
		return type10;
	}


	/**
     * 设置 type10
     * @param type10 type10
     */
	public void setType10(String type10) {
		this.type10 = type10;
	}

	/**
     * 获取 type11
     * @return type11.
     */
	public String getType11() {
		return type11;
	}


	/**
     * 设置 type11
     * @param type11 type11
     */
	public void setType11(String type11) {
		this.type11 = type11;
	}

	/**
     * 获取 type12
     * @return type12.
     */
	public String getType12() {
		return type12;
	}


	/**
     * 设置 type12
     * @param type12 type12
     */
	public void setType12(String type12) {
		this.type12 = type12;
	}
	// 行内五级分类 zhangpuyu 2019-7-23 end

    /**
     * 交易方式
     */
    private String transMode;
    /**
     * 出账编号
     *//*
    private String cmsId;
    *//**
     * BECIF号
     *//*
    private String cmsBecif;
    *//**
     * 关联合同编号
     *//*
    private String cmsContractId;*/
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
    @JsonSerialize(using=BigDecimalSerializer.class)
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
     * 投资性质_固定收益类
     */
    private String investmentAttributes;

    /**
     * 增信措施
     */
    private String creditEnhanceMeasure;

    /**
     * 基金经理姓名
     */
    private String fundMangerName;

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
     * 获取 creditFlag
     * @return creditFlag.
     */
    public String getCreditFlag() {
        return creditFlag;
    }

    
    /**
     * 设置 creditFlag
     * @param creditFlag creditFlag
     */
    public void setCreditFlag(String creditFlag) {
        this.creditFlag = creditFlag;
    }
    
    /**
     * 获取 operateType
     * @return operateType.
     */
    public String getOperateType() {
        return operateType;
    }
    
    /**
     * 设置 operateType
     * @param operateType operateType
     */
    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }
    
    /**
     * 获取 pageModel
     * @return pageModel.
     */
    public String getPageModel() {
        return pageModel;
    }
    
    /**
     * 设置 pageModel
     * @param pageModel pageModel
     */
    public void setPageModel(String pageModel) {
        this.pageModel = pageModel;
    }
    
    /**
     * 获取 selectedItem
     * @return selectedItem.
     */
    public List<FinPlanNetAssetDto> getSelectedItem() {
        return selectedItem;
    }
    
    /**
     * 设置 selectedItem
     * @param selectedItem selectedItem
     */
    public void setSelectedItem(List<FinPlanNetAssetDto> selectedItem) {
        this.selectedItem = selectedItem;
    }
    
    /**
     * 获取 elementId
     * @return elementId.
     */
    public String getElementId() {
        return elementId;
    }
    
    /**
     * 设置 elementId
     * @param elementId elementId
     */
    public void setElementId(String elementId) {
        this.elementId = elementId;
    }
    
    /**
     * 获取 startVDate
     * @return startVDate.
     */
    public String getStartVDate() {
        return startVDate;
    }
    
    /**
     * 设置 startVDate
     * @param startVDate startVDate
     */
    public void setStartVDate(String startVDate) {
        this.startVDate = startVDate;
    }
    
    /**
     * 获取 endVDate
     * @return endVDate.
     */
    public String getEndVDate() {
        return endVDate;
    }
    
    /**
     * 设置 endVDate
     * @param endVDate endVDate
     */
    public void setEndVDate(String endVDate) {
        this.endVDate = endVDate;
    }
    
    /**
     * 获取 startMDate
     * @return startMDate.
     */
    public String getStartMDate() {
        return startMDate;
    }
    
    /**
     * 设置 startMDate
     * @param startMDate startMDate
     */
    public void setStartMDate(String startMDate) {
        this.startMDate = startMDate;
    }
    
    /**
     * 获取 endMDate
     * @return endMDate.
     */
    public String getEndMDate() {
        return endMDate;
    }
    
    /**
     * 设置 endMDate
     * @param endMDate endMDate
     */
    public void setEndMDate(String endMDate) {
        this.endMDate = endMDate;
    }
    
    /**
     * 获取 category
     * @return category.
     */
    public String getCategory() {
        return category;
    }
    
    /**
     * 设置 category
     * @param category category
     */
    public void setCategory(String category) {
        this.category = category;
    }
    
    /**
     * 获取 type
     * @return type.
     */
    public String getType() {
        return type;
    }
    
    /**
     * 设置 type
     * @param type type
     */
    public void setType(String type) {
        this.type = type;
    }
    
    /**
     * 获取 transMode
     * @return transMode.
     */
    public String getTransMode() {
        return transMode;
    }
    
    /**
     * 设置 transMode
     * @param transMode transMode
     */
    public void setTransMode(String transMode) {
        this.transMode = transMode;
    }

    /**
     * 获取 cmsBECIF
     * @return cmsBECIF.
     *//*
    public String getCmsBecif() {
        return cmsBecif;
    }

    *//**
     * 设置 cmsBECIF
     * @param cmsBecif cmsBECIF
     *//*
    public void setCmsBecif(String cmsBecif) {
        this.cmsBecif = cmsBecif;
    }

    *//**
     * 获取 cmsContractId
     * @return cmsContractId.
     *//*
    public String getCmsContractId() {
        return cmsContractId;
    }*/

    /**
     * 设置 cmsContractId
     * @param cmsContractId cmsContractId
     */
    /*public void setCmsContractId(String cmsContractId) {
        this.cmsContractId = cmsContractId;
    }
*/
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
     * 获取 ptlPortfolio
     *
     * @return ptlPortfolio
     */
    public PtlPortfolio getPtlPortfolio() {
        return ptlPortfolio;
    }

    /**
     * 设置 ptlPortfolio
     *
     * @param ptlPortfolio ptlPortfolio
     */
    public void setPtlPortfolio(PtlPortfolio ptlPortfolio) {
        this.ptlPortfolio = ptlPortfolio;
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
    /*public String getCmsId() {
        return cmsId;
    }

    *//**
     * 设置 cmsId
     *
     * @param cmsId cmsId
     *//*
    public void setCmsId(String cmsId) {
        this.cmsId = cmsId;
    }*/

    
    /**
     * 获取 seqno
     * @return seqno.
     */
    public String getSeqno() {
        return seqno;
    }

    
    /**
     * 设置 seqno
     * @param seqno seqno
     */
    public void setSeqno(String seqno) {
        this.seqno = seqno;
    }

    
    /**
     * 获取 creditMstId
     * @return creditMstId.
     */
    public String getCreditMstId() {
        return creditMstId;
    }

    
    /**
     * 设置 creditMstId
     * @param creditMstId creditMstId
     */
    public void setCreditMstId(String creditMstId) {
        this.creditMstId = creditMstId;
    }

    
    /**
     * 获取 customerId
     * @return customerId.
     */
    public String getCustomerId() {
        return customerId;
    }

    
    /**
     * 设置 customerId
     * @param customerId customerId
     */
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    
    /**
     * 获取 pflsh
     * @return pflsh.
     */
    public String getPflsh() {
        return pflsh;
    }

    
    /**
     * 设置 pflsh
     * @param pflsh pflsh
     */
    public void setPflsh(String pflsh) {
        this.pflsh = pflsh;
    }

    
    /**
     * 获取 hapCreditAmt
     * @return hapCreditAmt.
     */
    public BigDecimal getHapCreditAmt() {
        return hapCreditAmt;
    }

    
    /**
     * 设置 hapCreditAmt
     * @param hapCreditAmt hapCreditAmt
     */
    public void setHapCreditAmt(BigDecimal hapCreditAmt) {
        this.hapCreditAmt = hapCreditAmt;
    }

    
    /**
     * 获取 actualCreditProvid
     * @return actualCreditProvid.
     */
    public String getActualCreditProvid() {
        return actualCreditProvid;
    }

    
    /**
     * 设置 actualCreditProvid
     * @param actualCreditProvid actualCreditProvid
     */
    public void setActualCreditProvid(String actualCreditProvid) {
        this.actualCreditProvid = actualCreditProvid;
    }


    /**
     * 获取 investmentAttributes
     * @return investmentAttributes.
     */
    public String getInvestmentAttributes() {
        return investmentAttributes;
    }


    /**
     * 设置 investmentAttributes
     * @param investmentAttributes investmentAttributes
     */
    public void setInvestmentAttributes(String investmentAttributes) {
        this.investmentAttributes = investmentAttributes;
    }
    /**
     * 实际管理人
     */
    private String actualManager;

    /**
     * 获取实际管理人 actualManager
     * @return
     */
    public String getActualManager() {
        return actualManager;
    }

    /**
     * 设置实际管理人 actualManager
     * @param actualManager
     */
    public void setActualManager(String actualManager) {
        this.actualManager = actualManager;
    }

    public String getPurSpeed() {
        return purSpeed;
    }

    public void setPurSpeed(String purSpeed) {
        this.purSpeed = purSpeed;
    }

    public String getRedSpeed() {
        return redSpeed;
    }

    public void setRedSpeed(String redSpeed) {
        this.redSpeed = redSpeed;
    }
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


    public String getRepAssetType() {
        return repAssetType;
    }

    public void setRepAssetType(String repAssetType) {
        this.repAssetType = repAssetType;
    }

    public String getCreditEnhanceMeasure() {
        return creditEnhanceMeasure;
    }

    public void setCreditEnhanceMeasure(String creditEnhanceMeasure) {
        this.creditEnhanceMeasure = creditEnhanceMeasure;
    }

    public String getFundMangerName() {
        return fundMangerName;
    }

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