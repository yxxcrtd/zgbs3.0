package com.joyintech.tams.invest.product.productelement.periodicmanagement.dto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.joyintech.framework.annotation.PageId;

import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.framework.validation.annotation.ValidLogic;
import com.joyintech.framework.validation.groups.ValidateGroupDelete;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.common.base.entity.FinFloatCalcRuleDetail;
import com.joyintech.tams.component.business.prod.dto.BussBenchMarkInfoDto;
import com.joyintech.tams.component.business.prod.dto.BussFinProductFloatFeeDto;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.invest.product.productelement.periodicmanagement.validator.PeriodicManagement001Validator;
import com.joyintech.workflow.engine.annotation.PrimaryKey;
import com.joyintech.workflow.engine.annotation.PrimaryKey.Field;
import com.joyintech.workflow.engine.dto.ProcessEntity;


/**
 * 期次管理查询
 * @author zhaojintao
 */

@PageId("PeriodicManagement002")
@ValidLogic(targetClass = PeriodicManagement001Validator.class, groups = {ValidateGroupEdit.class,
		  ValidateGroupDelete.class})
public class PeriodicManagement001Dto extends BaseEntity implements ProcessEntity {

	private static final long serialVersionUID = 1L;
	
	
	/**
	 * 元素Id
	 */
	private String elementId;
	
	/**
	 * 操作类型
	 */
	private String operateType;
	
	private String pageModel;
	 
	/**
	 * 有效状态
	 */
	private String validStatus;
	
	/**
	 * 产品代码
	 */
	private String finprodId;

	/**
	 * 产品代码
	 */
	private String finprodMarketId;
	
	/**
	 * 期次代码
	 */
	private String periodId;
	
	/**
	 * 期限产品代码
	 */
	private String periodProdId;
	
	/**
	 * 期限现金流Id
	 */
	private String periodCashId;
	
	/**
	 * 费用代码
	 */
	private String feeId;
	
	/**
	 * 产品简称
	 */
	private String finprodAbbr;
	
	/**
	 * 币种
	 */
	private String ccy;
	
	/**
     * 分支序号
     */
    private String branch;
	
	/**
	 * 期次开始日
	 */
	private Date startDate;
	
	/**
	 * 期次结束日
	 */
	private Date endDate;
	
	/**
	 * 利率类型
	 */
	private String rateType;
	
	/**
	 * 业绩基准
	 */
	private String baseRule;
	
	/**
	 * 投资周期
	 */
	private String investCycle;
	
	/**
	 * 投资周期2
	 */
	private String investCycle2;
	
	/**
	 * 期限品种
	 */
	private String termType;
	
	/**
	 * 收益类型
	 */
	private String profitType;
	
	private String publishType;
	
	private BigDecimal rateLower;
	
	private BigDecimal rateLimit;
	
	private BigDecimal rate;
	
	private String indexId;
	
	private BigDecimal coefficient;
	
	private BigDecimal spreadRate;
	
	private String resetType;
	
	private Date effDate;
	/**
	 * 备注
	 */
	private String remark;
	
	/**
	 * 业绩基准
	 */
	private BussBenchMarkInfoDto bussBenchMarkInfoDto;
	
	/**
	 * 浮动管理费
	 */
	private BussFinProductFloatFeeDto bussFinProductFloatFeeDto;
	
	/**
	 * 业绩报酬计算规则明细List
	 */
	private List<FinFloatCalcRuleDetail> finFloatCalcRuleDetailList;
	 
    /**
     * 获取 effDate
     * @return effDate.
     */
    public Date getEffDate() {
        return effDate;
    }

    
    /**
     * 设置 effDate
     * @param effDate effDate
     */
    public void setEffDate(Date effDate) {
        this.effDate = effDate;
    }

    public String getRateTypeStr(){
		return SysDataUtil.getNameFromDataDict(DictCode.利率类型, this.rateType);
	}

	public String getElementId() {
		return elementId;
	}

	public void setElementId(String elementId) {
		this.elementId = elementId;
	}

	public String getOperateType() {
		return operateType;
	}

	public void setOperateType(String operateType) {
		this.operateType = operateType;
	}

	public String getPageModel() {
		return pageModel;
	}

	public void setPageModel(String pageModel) {
		this.pageModel = pageModel;
	}

	public String getValidStatus() {
		return validStatus;
	}

	public void setValidStatus(String validStatus) {
		this.validStatus = validStatus;
	}

	public String getFinprodId() {
		return finprodId;
	}

	public void setFinprodId(String finprodId) {
		this.finprodId = finprodId;
	}

	public String getFinprodMarketId() {
		return finprodMarketId;
	}

	public void setFinprodMarketId(String finprodMarketId) {
		this.finprodMarketId = finprodMarketId;
	}

	public String getPeriodId() {
		return periodId;
	}

	public void setPeriodId(String periodId) {
		this.periodId = periodId;
	}

	@PrimaryKey(Field.PK_1)
	public String getPeriodProdId() {
		return periodProdId;
	}

	public void setPeriodProdId(String periodProdId) {
		this.periodProdId = periodProdId;
	}

	public String getPeriodCashId() {
		return periodCashId;
	}

	public void setPeriodCashId(String periodCashId) {
		this.periodCashId = periodCashId;
	}

	public String getFeeId() {
		return feeId;
	}

	public void setFeeId(String feeId) {
		this.feeId = feeId;
	}

	public String getFinprodAbbr() {
		return finprodAbbr;
	}

	public void setFinprodAbbr(String finprodAbbr) {
		this.finprodAbbr = finprodAbbr;
	}

	public String getCcy() {
		return ccy;
	}

	public void setCcy(String ccy) {
		this.ccy = ccy;
	}

	public Date getStartDate() {
		return startDate;
	}

	@PrimaryKey(Field.PK_2)
	public String getBranch() {
		return branch;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getRateType() {
		return rateType;
	}

	public void setRateType(String rateType) {
		this.rateType = rateType;
	}

	public String getBaseRule() {
		return baseRule;
	}

	public void setBaseRule(String baseRule) {
		this.baseRule = baseRule;
	}

	public String getInvestCycle() {
		return investCycle;
	}

	public void setInvestCycle(String investCycle) {
		this.investCycle = investCycle;
	}

	public String getInvestCycle2() {
		return investCycle2;
	}

	public void setInvestCycle2(String investCycle2) {
		this.investCycle2 = investCycle2;
	}

	public String getTermType() {
		return termType;
	}

	public void setTermType(String termType) {
		this.termType = termType;
	}

	public String getProfitType() {
		return profitType;
	}

	public void setProfitType(String profitType) {
		this.profitType = profitType;
	}

	public String getPublishType() {
		return publishType;
	}

	public void setPublishType(String publishType) {
		this.publishType = publishType;
	}


	public BigDecimal getRateLower() {
		return rateLower;
	}

	public void setRateLower(BigDecimal rateLower) {
		this.rateLower = rateLower;
	}

	public BigDecimal getRateLimit() {
		return rateLimit;
	}

	public void setRateLimit(BigDecimal rateLimit) {
		this.rateLimit = rateLimit;
	}

	public BigDecimal getRate() {
		return rate;
	}

	public void setRate(BigDecimal rate) {
		this.rate = rate;
	}

	public String getIndexId() {
		return indexId;
	}

	public void setIndexId(String indexId) {
		this.indexId = indexId;
	}

	public BigDecimal getCoefficient() {
		return coefficient;
	}

	public void setCoefficient(BigDecimal coefficient) {
		this.coefficient = coefficient;
	}

	public BigDecimal getSpreadRate() {
		return spreadRate;
	}

	public void setSpreadRate(BigDecimal spreadRate) {
		this.spreadRate = spreadRate;
	}

	public String getResetType() {
		return resetType;
	}

	public void setResetType(String resetType) {
		this.resetType = resetType;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public BussBenchMarkInfoDto getBussBenchMarkInfoDto() {
		return bussBenchMarkInfoDto;
	}

	public void setBussBenchMarkInfoDto(BussBenchMarkInfoDto bussBenchMarkInfoDto) {
		this.bussBenchMarkInfoDto = bussBenchMarkInfoDto;
	}

	public BussFinProductFloatFeeDto getBussFinProductFloatFeeDto() {
		return bussFinProductFloatFeeDto;
	}

	public void setBussFinProductFloatFeeDto(BussFinProductFloatFeeDto bussFinProductFloatFeeDto) {
		this.bussFinProductFloatFeeDto = bussFinProductFloatFeeDto;
	}

	public List<FinFloatCalcRuleDetail> getFinFloatCalcRuleDetailList() {
		return finFloatCalcRuleDetailList;
	}

	public void setFinFloatCalcRuleDetailList(List<FinFloatCalcRuleDetail> finFloatCalcRuleDetailList) {
		this.finFloatCalcRuleDetailList = finFloatCalcRuleDetailList;
	}
	
    public String getStartDateStr() {
        return DateUtil.format(this.getStartDate());
    }
    public String getEndDateStr() {
        return DateUtil.format(this.getEndDate());
    }
}
