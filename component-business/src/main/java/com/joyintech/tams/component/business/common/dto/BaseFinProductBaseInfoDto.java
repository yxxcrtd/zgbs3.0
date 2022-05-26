/*
 * 文件名：BaseFinProductBaseInfoDto.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：xiexinting
 * 修改时间：2018年9月4日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.component.business.common.dto;

import java.util.List;

import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.base.report.dto.BaseRepAssetEquityInfoDto;

/**
 * 〈一句话功能简述〉
 * 〈功能详细描述〉
 * @author xiexinting
 * @version 1.0 2018年9月4日
 * @see BaseFinProductBaseInfoDto
 * @since 1.0
 */
public class BaseFinProductBaseInfoDto {
    
    /**
     * 标的类金融产品entity
     */
    private FinProduct finProduct;
    /**
     * 金融产品附属表entity
     */
    private FinProductAdd finProductAdd;
    /**
     * 金融产品发行信息
     */
    private FinProductIssue finProductIssue;

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
     * 现金流支付规则dtolist
     */
    private List<FinCashPayRule> finCashPayRuleList;
    /**
     * 现金流计划dtolist
     */
    private List<FinCashPlan> finCashPlanList;
    /**
     * 金融产品行权规则dtolist
     */
    private List<FinExerciseRule> finExerciseRuleList;
    /**
     * 金融产品行权计划dtolist
     */
    private List<FinExercisePlan> finExercisePlanList;
    /**
     * 开放计划dtolist
     */
    private List<FinOpenPlan> finOpenPlanList;
    /**
     * 开放规则dtolist
     */
    private List<FinOpenRule> finOpenRuleList;
    
    /**
     * 基金报送信息 add by shenchen
     */
    private RepFund repfund;
    /**
     * 债券中债报送 机构类别add by shenchen 
     */
    private RepAssetBond repAssetBond;
    /**
     * 债券中债报送 资产负债类别
     * 
     */
    private RepAssetDept repAssetDept;
    
    /**
     * 权益类资产报送
     * 
     */
    private RepAssetEquity repAssetEquity;
    
    /**
     * 债券中债报送--权益类资产报送信息
     * 
     */    
    private BaseRepAssetEquityInfoDto baseRepAssetEquityInfoDto;    

    /**
     * 资产/负债类别
     */
    private String repAssetType;
    /**
     * 债券评级信息list add by shenchen 2019-04-25
     */
    private List<MstBondCreditRating> mstBondCreditRatingList;
    
    /**
     * JXS_ISSUE_INFO
     * 
     */
    private JxsIssueInfo jxsIssueInfo;
    
    /**
     * 获取 标的类金融产品entity
     * @return finProduct.
     */
    public FinProduct getFinProduct() {
        return finProduct;
    }
    
    /**
     * 设置 标的类金融产品entity
     * @param finProduct finProduct
     */
    public void setFinProduct(FinProduct finProduct) {
        this.finProduct = finProduct;
    }
    
    /**
     * 获取 金融产品附属表entity
     * @return finProductAdd.
     */
    public FinProductAdd getFinProductAdd() {
        return finProductAdd;
    }
    
    /**
     * 设置 金融产品附属表entity
     * @param finProductAdd finProductAdd
     */
    public void setFinProductAdd(FinProductAdd finProductAdd) {
        this.finProductAdd = finProductAdd;
    }
    
    /**
     * 获取 金融产品分类表entity
     * @return finProductType.
     */
    public FinProductType getFinProductType() {
        return finProductType;
    }
    
    /**
     * 设置 金融产品分类表entity
     * @param finProductType finProductType
     */
    public void setFinProductType(FinProductType finProductType) {
        this.finProductType = finProductType;
    }
    
   
    
    
    /**
     * 获取 金融产品现金流信息表dtolist
     * @return finCashInfoList.
     */
    public List<FinCashInfo> getFinCashInfoList() {
        return finCashInfoList;
    }

    
    /**
     * 设置 金融产品现金流信息表dtolist
     * @param finCashInfoList finCashInfoList
     */
    public void setFinCashInfoList(List<FinCashInfo> finCashInfoList) {
        this.finCashInfoList = finCashInfoList;
    }

    /**
     * 获取 现金流计算规则dtolist
     * @return finCashCalcRuleList.
     */
    public List<FinCashCalcRule> getFinCashCalcRuleList() {
        return finCashCalcRuleList;
    }
    
    /**
     * 设置 现金流计算规则dtolist
     * @param finCashCalcRuleList finCashCalcRuleList
     */
    public void setFinCashCalcRuleList(List<FinCashCalcRule> finCashCalcRuleList) {
        this.finCashCalcRuleList = finCashCalcRuleList;
    }
    
    /**
     * 获取 现金流利率信息表dtolist
     * @return finRateInfoList.
     */
    public List<FinRateInfo> getFinRateInfoList() {
        return finRateInfoList;
    }
    
    /**
     * 设置 现金流利率信息表dtolist
     * @param finRateInfoList finRateInfoList
     */
    public void setFinRateInfoList(List<FinRateInfo> finRateInfoList) {
        this.finRateInfoList = finRateInfoList;
    }
    
    /**
     * 获取 现金流支付规则dtolist
     * @return finCashPayRuleList.
     */
    public List<FinCashPayRule> getFinCashPayRuleList() {
        return finCashPayRuleList;
    }
    
    /**
     * 设置 现金流支付规则dtolist
     * @param finCashPayRuleList finCashPayRuleList
     */
    public void setFinCashPayRuleList(List<FinCashPayRule> finCashPayRuleList) {
        this.finCashPayRuleList = finCashPayRuleList;
    }
    
    /**
     * 获取现金流计划dtolist
     * @return finCashPlanList.
     */
    public List<FinCashPlan> getFinCashPlanList() {
        return finCashPlanList;
    }
    
    /**
     * 设置 现金流计划dtolist
     * @param finCashPlanList finCashPlanList
     */
    public void setFinCashPlanList(List<FinCashPlan> finCashPlanList) {
        this.finCashPlanList = finCashPlanList;
    }
  
    
    /**
     * 获取 金融产品行权规则dtolist
     * @return finExerciseRuleList.
     */
    public List<FinExerciseRule> getFinExerciseRuleList() {
        return finExerciseRuleList;
    }

    
    /**
     * 设置 金融产品行权规则dtolist
     * @param finExerciseRuleList finExerciseRuleList
     */
    public void setFinExerciseRuleList(List<FinExerciseRule> finExerciseRuleList) {
        this.finExerciseRuleList = finExerciseRuleList;
    }

    /**
     * 获取 金融产品行权计划dtolist
     * @return finExercisePlanList.
     */
    public List<FinExercisePlan> getFinExercisePlanList() {
        return finExercisePlanList;
    }
    
    /**
     * 设置 金融产品行权计划dtolist
     * @param finExercisePlanList finExercisePlanList
     */
    public void setFinExercisePlanList(List<FinExercisePlan> finExercisePlanList) {
        this.finExercisePlanList = finExercisePlanList;
    }

    
    /**
     * 获取 finOpenPlanList
     * @return finOpenPlanList.
     */
    public List<FinOpenPlan> getFinOpenPlanList() {
        return finOpenPlanList;
    }

    
    /**
     * 设置 finOpenPlanList
     * @param finOpenPlanList finOpenPlanList
     */
    public void setFinOpenPlanList(List<FinOpenPlan> finOpenPlanList) {
        this.finOpenPlanList = finOpenPlanList;
    }

    
    /**
     * 获取 finOpenRuleList
     * @return finOpenRuleList.
     */
    public List<FinOpenRule> getFinOpenRuleList() {
        return finOpenRuleList;
    }

    
    /**
     * 设置 finOpenRuleList
     * @param finOpenRuleList finOpenRuleList
     */
    public void setFinOpenRuleList(List<FinOpenRule> finOpenRuleList) {
        this.finOpenRuleList = finOpenRuleList;
    }

	public RepFund getRepfund() {
		return repfund;
	}

	public void setRepfund(RepFund repfund) {
		this.repfund = repfund;
	}

	public RepAssetBond getRepAssetBond() {
		return repAssetBond;
	}

	public void setRepAssetBond(RepAssetBond repAssetBond) {
		this.repAssetBond = repAssetBond;
	}

	public RepAssetDept getRepAssetDept() {
		return repAssetDept;
	}

	public void setRepAssetDept(RepAssetDept repAssetDept) {
		this.repAssetDept = repAssetDept;
	}

	public String getRepAssetType(){return repAssetType;}
	
	public void setRepAssetType(String repAssetType){this.repAssetType = repAssetType;}

	public List<MstBondCreditRating> getMstBondCreditRatingList() {
		return mstBondCreditRatingList;
	}

	public void setMstBondCreditRatingList(List<MstBondCreditRating> mstBondCreditRatingList) {
		this.mstBondCreditRatingList = mstBondCreditRatingList;
	}
   
	public BaseRepAssetEquityInfoDto getBaseRepAssetEquityInfoDto() {
		return baseRepAssetEquityInfoDto;
	}

	public void setBaseRepAssetEquityInfoDto(BaseRepAssetEquityInfoDto baseRepAssetEquityInfoDto) {
		this.baseRepAssetEquityInfoDto = baseRepAssetEquityInfoDto;
	}

	public JxsIssueInfo getJxsIssueInfo() {
		return jxsIssueInfo;
	}

	public void setJxsIssueInfo(JxsIssueInfo jxsIssueInfo) {
		this.jxsIssueInfo = jxsIssueInfo;
	}

	public RepAssetEquity getRepAssetEquity() {
		return repAssetEquity;
	}

	public void setRepAssetEquity(RepAssetEquity repAssetEquity) {
		this.repAssetEquity = repAssetEquity;
	}

    public FinProductIssue getFinProductIssue() {
        return finProductIssue;
    }

    public void setFinProductIssue(FinProductIssue finProductIssue) {
        this.finProductIssue = finProductIssue;
    }
}
