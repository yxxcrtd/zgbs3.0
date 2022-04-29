package com.joyintech.tams.invest.common.risktrial.dto;

import java.util.Date;

import com.joyintech.background.component.common.pack.common.util.StringUtil;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.constant.DictCode;
/**
 * 风险试算结果DTO
 * @author 裴家俊
 * @version 1.0 2018年11月30日
 * @see RiskTrialRetDto
 * @since 1.0
 */
public class RiskTrialRetDto extends BaseEntity {

	/**
	 * <意义，目的和功能，以及可能被用到的地方>
	 */
	private static final long serialVersionUID = 1L;
	/*
	 * 组合(包)代码
	 */
	private String portfolioId;
	/*
	 * 指标编号
	 */
	private String indId;
	/*
	 * 指标名称
	 */
	private String indName;
	/*
	 * 监督日期
	 */
	private Date supDate;
	/*
	 * 监督值
	 */
	private String supVal;
	/*
	 * 监督状态
	 */
	private String supStat;
	
	/*
	 * 指标类型
	 */
	private String indType;
	
	/*
	 * 预警条件，记录当时比较的预警值，防止该指标以后发生变化
	 */
	private String warnCond;
	/*
	 * 违规条件，记录当时比较的违规值，防止该指标以后发生变化
	 */
	private String foulCond;
	/*
	 * 结论
	 */
	private String conclusion;
	/*
	 * 总结论
	 */
	private String totalConclu;
	
	/*
	 * 组合层次
	 */
	private String portfolioLevel;
	
	/**
     * 质押式回购多组合
     */
    private String index;

	/**
	 * 表达式
	 */
	private String expr;


	/**
	 * 表达式(可投金额试算使用新增)
	 * by nixukuan 20191012
	 */
	private String investedAmountExpr;
	
    /**
     * 获取 index
     * @return index.
     */
    public String getIndex() {
        return index;
    }

    
    /**
     * 设置 index
     * @param index index
     */
    public void setIndex(String index) {
        this.index = index;
    }

    /**
     * 获取 totalConclu
     * @return totalConclu.
     */
    public String getTotalConclu() {
        return totalConclu;
    }
    
    /**
     * 设置 totalConclu
     * @param totalConclu totalConclu
     */
    public void setTotalConclu(String totalConclu) {
        this.totalConclu = totalConclu;
    }
    public String getPortfolioId() {
		return portfolioId;
	}
	public void setPortfolioId(String portfolioId) {
		this.portfolioId = portfolioId;
	}
	public String getIndId() {
		return indId;
	}
	public void setIndId(String indId) {
		this.indId = indId;
	}
	public String getIndName() {
		return indName;
	}
	public void setIndName(String indName) {
		this.indName = indName;
	}
	public Date getSupDate() {
		return supDate;
	}
	public void setSupDate(Date supDate) {
		this.supDate = supDate;
	}
	public String getSupVal() {
		return supVal;
	}
	public void setSupVal(String supVal) {
		this.supVal = supVal;
	}
	public String getSupStat() {
		return supStat;
	}
	public void setSupStat(String supStat) {
		this.supStat = supStat;
	}
	public String getIndType() {
		return indType;
	}
	public String getIndTypeStr() {
		return SysDataUtil.getNameFromDataDict(DictCode.指标类型, this.getIndType());
	}
	public void setIndType(String indType) {
		this.indType = indType;
	}
	public String getWarnCond() {
		return warnCond;
	}
	public void setWarnCond(String warnCond) {
		this.warnCond = warnCond;
	}
	public String getFoulCond() {
		return foulCond;
	}
	public void setFoulCond(String foulCond) {
		this.foulCond = foulCond;
	}
	public String getConclusion() {
		return conclusion;
	}
	public void setConclusion(String conclusion) {
		this.conclusion = conclusion;
	}
	
	public String getPortfolioLevel() {
		return portfolioLevel;
	}

	public void setPortfolioLevel(String portfolioLevel) {
		this.portfolioLevel = portfolioLevel;
	}

	public String getSupStatStr() {
		return SysDataUtil.getNameFromDataDict(DictCode.监督状态, this.getSupStat());
	}

	public String getExpr() {
		return expr;
	}

	public void setExpr(String expr) {
		this.expr = expr;
	}


	public String getInvestedAmountExpr() {
		return investedAmountExpr;
	}

	public void setInvestedAmountExpr(String investedAmountExpr) {
		this.investedAmountExpr = investedAmountExpr;
	}



	//--------------add by jjpei 20200116 新风险字段-----------------
	/*
	 * 组合(包)代码
	 */
	private String portfolioCode;
	/*
	 * 组合(包)名称
	 */
	private String portfolioName;
	/*
	 * 指标代码
	 */
	private String kriCode;
	/*
	 * 趋势
	 */
	private String kriTrend;
	/*
	 * 指标名称
	 */
	private String kriName;
	/*
	 * 原值
	 */
	private String oldValue;
	/*
	 * 监督值
	 */
	private String kriValue;
	/*
	 * 监督状态
	 */
	private String kriStatus;

	private String uniteStatus;
	/*
	 * 指标类型
	 */
	private String kriType;

	/*
	 * 预警条件，记录当时比较的预警值，防止该指标以后发生变化
	 */
	private String warningLine;
	/*
	 * 审批条件，记录当时比较的审批值，防止该指标以后发生变化
	 */
	private String approveLine;
	/*
	 * 违规条件，记录当时比较的违规值，防止该指标以后发生变化
	 */
	private String forbiddenLn;
	
	/*
	 *资产代码
	 */
	private String assetCode;
	
	/*
	 * 资产名称
	 */
	private String assetName;

	/*
	 * 指标计算表达式
	 */
	private String kriExpr;

	
	public String getPortfolioCode() {
		return portfolioCode;
	}

	public void setPortfolioCode(String portfolioCode) {
		this.portfolioCode = portfolioCode;
	}

	public String getPortfolioName() {
		return portfolioName;
	}

	public void setPortfolioName(String portfolioName) {
		this.portfolioName = portfolioName;
	}

	public String getKriTrend() {
		return kriTrend;
	}

	public void setKriTrend(String kriTrend) {
		this.kriTrend = kriTrend;
	}

	public String getKriName() {
		return kriName;
	}

	public void setKriName(String kriName) {
		this.kriName = kriName;
	}

	public String getOldValue() {
		return oldValue;
	}

	public void setOldValue(String oldValue) {
		this.oldValue = oldValue;
	}

	public String getKriValue() {
		return kriValue;
	}

	public void setKriValue(String kriValue) {
		this.kriValue = kriValue;
	}

	public String getKriStatus() {
		return kriStatus;
	}

	public String getKriStatusCn() {
		if(StringUtil.equals(getKriStatus(),"0")){
			return "正常";
		}
		if(StringUtil.equals(getKriStatus(),"1")){
			return "预警";
		}
		if(StringUtil.equals(getKriStatus(),"2")){
			return "审批";
		}
		if(StringUtil.equals(getKriStatus(),"3")){
			return "违规";
		}
		if(StringUtil.equals(getKriStatus(),"4")){
			return "异常";
		}
		return getKriStatus();
	}

	public void setKriStatus(String kriStatus) {
		this.kriStatus = kriStatus;
	}

	public String getKriType() {
		return kriType;
	}

	public String getKriTypeCn() {
		return SysDataUtil.getNameFromDataDict(DictCode.指标类型, this.getKriType());
	}

	public void setKriType(String kriType) {
		this.kriType = kriType;
	}

	public String getWarningLine() {
		return warningLine;
	}

	public void setWarningLine(String warningLine) {
		this.warningLine = warningLine;
	}

	public String getApproveLine() {
		return approveLine;
	}

	public void setApproveLine(String approveLine) {
		this.approveLine = approveLine;
	}

	public String getForbiddenLn() {
		return forbiddenLn;
	}

	public void setForbiddenLn(String forbiddenLn) {
		this.forbiddenLn = forbiddenLn;
	}

	public String getUniteStatus() {
		return uniteStatus;
	}

	public void setUniteStatus(String uniteStatus) {
		this.uniteStatus = uniteStatus;
	}

	public String getKriCode() {
		return kriCode;
	}

	public void setKriCode(String kriCode) {
		this.kriCode = kriCode;
	}


    /**
     * 获取 assetCode
     * @return assetCode.
     */
    public String getAssetCode() {
        return assetCode;
    }


    /**
     * 设置 assetCode
     * @param assetCode assetCode
     */
    public void setAssetCode(String assetCode) {
        this.assetCode = assetCode;
    }


    /**
     * 获取 assetName
     * @return assetName.
     */
    public String getAssetName() {
        return assetName;
    }


    /**
     * 设置 assetName
     * @param assetName assetName
     */
    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }
    // add by zhangrui1 增加groupId start
    private int groupId;

    /**
     * 获取 groupId
     * 
     * @return groupId.
     */
    public int getGroupId() {
        return groupId;
    }

    /**
     * 设置 groupId
     * 
     * @param groupId groupId
     */
    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }
    // add by zhangrui1 增加groupId end
	public String getKriExpr() {
		return kriExpr;
	}

	public void setKriExpr(String kriExpr) {
		this.kriExpr = kriExpr;
	}
}
