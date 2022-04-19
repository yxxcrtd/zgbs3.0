package com.joyintech.tams.exchange.entrust.etrexchange.secrepo.dto;


import com.joyintech.background.component.common.pack.common.util.StringUtil;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;


/**
 * 风险试算结果DTO
 * @author 裴家俊
 * @version 1.0 2018年11月30日
 * @see EtrExchangeTradeRiskcalSecRepoResponse
 * @since 1.0
 */
public class EtrExchangeTradeRiskcalSecRepoResponse {

	@ApiModelProperty(name="riskMathFlag",value="02：已点击风险试算",example="02")
	private String riskMathFlag;

	@ApiModelProperty(name="riskTrialSpan",value="运算结果",example="02")
	private String riskTrialSpan;

	@ApiModelProperty(name="rskStatus",value="指标状态",example="3")
	private String rskStatus;

	@ApiModelProperty(name="stdrtninfo",value="处理结果",example="3")
	private String stdrtninfo;

	@ApiModelProperty(name="riskcalReturnList",value="风险运算结果",example="02")
	List<EtrExchangeTradeRiskcalSecRepoResponse> riskcalReturnList;

	@ApiModelProperty(name="kriCode",value="指标代码",example="80434e46d8bd490cb75b2418bfe53b02")
	private String kriCode;

	@ApiModelProperty(name="portfolioCode",value="产品代码",example="AAA1")
	private String portfolioCode;

	@ApiModelProperty(name="portfolioName",value="产品名称",example="AAA1")
	private String portfolioName;

	@ApiModelProperty(name="kriTypeCn",value="指标类型",example="流动性管理类")
	private String kriTypeCn;

	@ApiModelProperty(name="assetName",value="资产名称",example="A1")
	private String assetName;

	@ApiModelProperty(name="warningLine",value="预警阈值",example=">=2700000.0000万元")
	private String warningLine;

	@ApiModelProperty(name="approveLine",value="审批线",example=">=3000000.0000万元")
	private String approveLine;

	@ApiModelProperty(name="forbiddenLn",value="违规阈值",example="<10%")
	private String forbiddenLn;

	@ApiModelProperty(name="kriValue",value="当前实际值",example="<10%")
	private String kriValue;

	@ApiModelProperty(name="oldValue",value="原值",example="<10%")
	private String oldValue;

	@ApiModelProperty(name="kriTrend",value="趋势",example="向好")
	private String kriTrend;

	@ApiModelProperty(name="kriStatus",value="指标状态Code",example="03")
	private String kriStatus;

	@ApiModelProperty(name="totalConclu",value="运算结果",example="试算不通过")
	private String totalConclu;

	@ApiModelProperty(name="kriStatusCn",value="指标状态",example="3")
	private String kriStatusCn;

	@ApiModelProperty(name="kriName",value="指标名称",example="3")
	private String kriName;

	private String groupId;

	@ApiModelProperty(name="kriType",value="指标类型Code",example="3")
	private String kriType;

	@ApiModelProperty(name="kriExpr",value="'指标计算表达式'",example="3")
	private String kriExpr;

	@ApiModelProperty(name="kriOldExpr",value="原值Old",example="3")
	private String kriOldExpr;
	public String getRiskMathFlag() {
		return riskMathFlag;
	}

	public void setRiskMathFlag(String riskMathFlag) {
		this.riskMathFlag = riskMathFlag;
	}

	public String getRiskTrialSpan() {
		return riskTrialSpan;
	}

	public void setRiskTrialSpan(String riskTrialSpan) {
		this.riskTrialSpan = riskTrialSpan;
	}

	public String getRskStatus() {
		return rskStatus;
	}

	public void setRskStatus(String rskStatus) {
		this.rskStatus = rskStatus;
	}

	public String getStdrtninfo() {
		return stdrtninfo;
	}

	public void setStdrtninfo(String stdrtninfo) {
		this.stdrtninfo = stdrtninfo;
	}

	public List<EtrExchangeTradeRiskcalSecRepoResponse> getRiskcalReturnList() {
		return riskcalReturnList;
	}

	public void setRiskcalReturnList(List<EtrExchangeTradeRiskcalSecRepoResponse> riskcalReturnList) {
		this.riskcalReturnList = riskcalReturnList;
	}

	public String getKriCode() {
		return kriCode;
	}

	public void setKriCode(String kriCode) {
		this.kriCode = kriCode;
	}

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

	public String getKriTypeCn() {
		return kriTypeCn;
	}

	public void setKriTypeCn(String kriTypeCn) {
		this.kriTypeCn = kriTypeCn;
	}

	public String getAssetName() {
		return assetName;
	}

	public void setAssetName(String assetName) {
		this.assetName = assetName;
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

	public String getKriValue() {
		return kriValue;
	}

	public void setKriValue(String kriValue) {
		this.kriValue = kriValue;
	}

	public String getOldValue() {
		return oldValue;
	}

	public void setOldValue(String oldValue) {
		this.oldValue = oldValue;
	}

	public String getKriTrend() {
		return kriTrend;
	}

	public void setKriTrend(String kriTrend) {
		this.kriTrend = kriTrend;
	}

	public String getKriStatus() {
		return kriStatus;
	}

	public void setKriStatus(String kriStatus) {
		this.kriStatus = kriStatus;
	}

	public String getTotalConclu() {
		return totalConclu;
	}

	public void setTotalConclu(String totalConclu) {
		this.totalConclu = totalConclu;
	}

	public String getKriStatusCn() {
		if(StringUtil.isNotEmpty(this.getKriStatus())){
			if(StringUtil.equals(this.getKriStatus(),0)){
				return "正常";
			}else if(StringUtil.equals(this.getKriStatus(),1)){
				return "预警";
			}else  if(StringUtil.equals(this.getKriStatus(),2)){
				return "审批";
			}else  if(StringUtil.equals(this.getKriStatus(),3)){
				return "违规";
			}else  if(StringUtil.equals(this.getKriStatus(),4)){
				return "异常";
			}
		}
		return kriStatusCn;
	}

	public void setKriStatusCn(String kriStatusCn) {
		this.kriStatusCn = kriStatusCn;
	}

	public String getKriName() {
		return kriName;
	}

	public void setKriName(String kriName) {
		this.kriName = kriName;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getKriType() {
		return kriType;
	}

	public void setKriType(String kriType) {
		this.kriType = kriType;
	}

	public String getKriExpr() {
		return kriExpr;
	}

	public void setKriExpr(String kriExpr) {
		this.kriExpr = kriExpr;
	}

	public String getKriOldExpr() {
		return kriOldExpr;
	}

	public void setKriOldExpr(String kriOldExpr) {
		this.kriOldExpr = kriOldExpr;
	}




}
