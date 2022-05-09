package com.joyintech.tams.invest.riskredis.quota.supervisionindividuation.dto;


import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.export.paginator.domain.ITableRow;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.framework.validation.annotation.ValidLogic;
import com.joyintech.framework.validation.groups.ValidateGroupDelete;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeRds;
import com.joyintech.tams.invest.riskredis.quota.supervisionindividuation.valid.RskSupervisionIndividuation002Validator;
import com.joyintech.workflow.engine.annotation.PrimaryKey;
import com.joyintech.workflow.engine.annotation.PrimaryKey.Field;
import com.joyintech.workflow.engine.dto.ProcessEntity;
@PageId("RskSupervisionIndividuation001")
@ValidLogic(targetClass = RskSupervisionIndividuation002Validator.class,groups = {
	    ValidateGroupEdit.class,ValidateGroupDelete.class})
public class RskSupervisionIndividuationDto extends BaseEntity implements ITableRow,ProcessEntity {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 定义被删除的联合编号
	 */
	private String deleteUniteId;
	/**
     * 画面处理模式
     */
    private String pageModel;

    /**
     * 操作类型 - 工作流参数
     */
    private String operateType;

    /**
     * 按钮ID - 工作流参数
     */
    private String elementId;
    /**
     * 画面Id - 工作流参数
     */
    private String pageId;
    
    /**
     * 条件设置
     */
    private List<RskIndividConditionDto> conditionList;
    /**
     * 剔除证券
     */
    private List<RskIndividAssetDelDto> assetDelList;
    /**
     * 特殊阶段
     */
    private List<RskIndividSpecialStageDto> specialStageList;
    /**
     * 特殊资产阙值
     */
    private List<RskIndividSpecialAssetDto> specialAssetList;
    /**
     * 联合设置
     */
    private List<RskIndividUniteDto> uniteList;

	/**
	 * 自定义条件设置
	 */
	private List<RskIndividConditionLineDto> conditionLines;
	/**
	 * 条件设置编号
     */
    private String conditionId;
	/**
     * 组合(包)代码
     */
    private String prtBagId;

    /**
     * 指标编号
     */
    private String indId;
    /**
     * 条件项
     */
    private String conditionItem;

    /**
     * 控制类型
     */
    private String controlType;

    /**
     * 数值(起)
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal valueStr;

    /**
     * 数值(未)
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal valueEnd;

	/**
	 * 日期选项(起)
	 */
	private String dateStr;

	/**
	 * 日期选项(末)
	 */
	private String dateEnd;

	/**
	 * 日期单位(起)
	 */
	private String dateUnitStr;

	/**
	 * 日期单位(末)
	 */
	private String dateUnitEnd;

    /**
     * 剔除编号
     */
    private String delId;

    /**
     * 开始日期
     */
    private Date startDate;

    /**
     * 结束日期
     */
    private Date endDate;

    /**
     * 资产代码
     */
    private String finprodId;
    /**
     * 特殊阶段编号
     */
    private String uniteId;
    /**
     * 联合设置编号
     */
    private String uniteId5;


    /**
     * 阶段类型
     */
    private String uniteType;


    /**
     * 预警条件
     */
    private String warnCond;

    /**
     * 审批条件
     */
    private String approveCond;

    /**
     * 违规条件
     */
    private String foulCond;


    /**
     * 联合指标编号
     */
    private String uniteIndId;

    /**
     * 备注
     */
    private String remark;
    
    private String indType;
    
	public String getDeleteUniteId() {
		return deleteUniteId;
	}

	public void setDeleteUniteId(String deleteUniteId) {
		this.deleteUniteId = deleteUniteId;
	}

	public String getPageModel() {
		return pageModel;
	}

	public void setPageModel(String pageModel) {
		this.pageModel = pageModel;
	}

	public String getOperateType() {
		return operateType;
	}

	public void setOperateType(String operateType) {
		this.operateType = operateType;
	}

	public String getElementId() {
		return elementId;
	}

	public void setElementId(String elementId) {
		this.elementId = elementId;
	}

	public String getPageId() {
		return pageId;
	}

	public void setPageId(String pageId) {
		this.pageId = pageId;
	}

	public List<RskIndividConditionDto> getConditionList() {
		return conditionList;
	}

	public void setConditionList(List<RskIndividConditionDto> conditionList) {
		this.conditionList = conditionList;
	}

	public List<RskIndividAssetDelDto> getAssetDelList() {
		return assetDelList;
	}

	public void setAssetDelList(List<RskIndividAssetDelDto> assetDelList) {
		this.assetDelList = assetDelList;
	}

	public List<RskIndividSpecialStageDto> getSpecialStageList() {
		return specialStageList;
	}

	public void setSpecialStageList(List<RskIndividSpecialStageDto> specialStageList) {
		this.specialStageList = specialStageList;
	}

	public List<RskIndividSpecialAssetDto> getSpecialAssetList() {
		return specialAssetList;
	}

	public void setSpecialAssetList(List<RskIndividSpecialAssetDto> specialAssetList) {
		this.specialAssetList = specialAssetList;
	}

	public List<RskIndividConditionLineDto> getConditionLines() {
		return conditionLines;
	}

	public void setConditionLines(List<RskIndividConditionLineDto> conditionLines) {
		this.conditionLines = conditionLines;
	}

	public List<RskIndividUniteDto> getUniteList() {
		return uniteList;
	}

	public void setUniteList(List<RskIndividUniteDto> uniteList) {
		this.uniteList = uniteList;
	}
	@PrimaryKey(value=Field.PK_1)
	public String getPrtBagId() {
		return prtBagId;
	}

	public void setPrtBagId(String prtBagId) {
		this.prtBagId = prtBagId;
	}
	@PrimaryKey(value=Field.PK_2)
	public String getIndId() {
		return indId;
	}

	public void setIndId(String indId) {
		this.indId = indId;
	}

	

	public String getConditionItem() {
		return conditionItem;
	}
	public String getConditionItemStr() {
		return SysDataUtil.getNameFromDataDict(BussinessDictCodeRds.条件项, this.getConditionItem());
	}
	public void setConditionItem(String conditionItem) {
		this.conditionItem = conditionItem;
	}

	public String getControlType() {
		return controlType;
	}
	public String getControlTypeStr() {
		return SysDataUtil.getNameFromDataDict(BussinessDictCodeRds.控制类型, this.getControlType());
	}
	public void setControlType(String controlType) {
		this.controlType = controlType;
	}

	public BigDecimal getValueStr() {
		return valueStr;
	}

	public void setValueStr(BigDecimal valueStr) {
		this.valueStr = valueStr;
	}

	public BigDecimal getValueEnd() {
		return valueEnd;
	}

	public void setValueEnd(BigDecimal valueEnd) {
		this.valueEnd = valueEnd;
	}

	public String getConditionId() {
		return conditionId;
	}

	public void setConditionId(String conditionId) {
		this.conditionId = conditionId;
	}

	public String getDelId() {
		return delId;
	}

	public void setDelId(String delId) {
		this.delId = delId;
	}

	public Date getStartDate() {
		return startDate;
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

	public String getFinprodId() {
		return finprodId;
	}

	public void setFinprodId(String finprodId) {
		this.finprodId = finprodId;
	}

	public String getUniteId() {
		return uniteId;
	}

	public void setUniteId(String uniteId) {
		this.uniteId = uniteId;
	}
	
	public String getUniteId5() {
		return uniteId5;
	}

	public void setUniteId5(String uniteId5) {
		this.uniteId5 = uniteId5;
	}

	public String getUniteType() {
		return uniteType;
	}

	public void setUniteType(String uniteType) {
		this.uniteType = uniteType;
	}

	public String getWarnCond() {
		return warnCond;
	}

	public void setWarnCond(String warnCond) {
		this.warnCond = warnCond;
	}

	public String getApproveCond() {
		return approveCond;
	}

	public void setApproveCond(String approveCond) {
		this.approveCond = approveCond;
	}

	public String getFoulCond() {
		return foulCond;
	}

	public void setFoulCond(String foulCond) {
		this.foulCond = foulCond;
	}

	public String getUniteIndId() {
		return uniteIndId;
	}

	public void setUniteIndId(String uniteIndId) {
		this.uniteIndId = uniteIndId;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getIndType() {
		return indType;
	}

	public void setIndType(String indType) {
		this.indType = indType;
	}

	public String getDateStr() {
		return dateStr;
	}

	public void setDateStr(String dateStr) {
		this.dateStr = dateStr;
	}

	public String getDateEnd() {
		return dateEnd;
	}

	public void setDateEnd(String dateEnd) {
		this.dateEnd = dateEnd;
	}

	public String getDateUnitStr() {
		return dateUnitStr;
	}

	public void setDateUnitStr(String dateUnitStr) {
		this.dateUnitStr = dateUnitStr;
	}

	public String getDateUnitEnd() {
		return dateUnitEnd;
	}

	public void setDateUnitEnd(String dateUnitEnd) {
		this.dateUnitEnd = dateUnitEnd;
	}
}
