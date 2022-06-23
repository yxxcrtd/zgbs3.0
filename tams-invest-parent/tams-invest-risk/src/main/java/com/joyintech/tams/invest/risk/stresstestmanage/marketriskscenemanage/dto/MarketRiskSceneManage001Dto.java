/*
 * 文件名：MarketRiskSceneManage001Dto.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：汪海
 * 修改时间：2019年9月3日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.invest.risk.stresstestmanage.marketriskscenemanage.dto;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.framework.validation.annotation.NotEmpty;
import com.joyintech.framework.validation.annotation.ValidLogic;
import com.joyintech.framework.validation.groups.ValidateGroupDelete;
import com.joyintech.tams.common.ValidateGroupEffective;
import com.joyintech.tams.common.base.entity.SttFactor;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.invest.risk.stresstestmanage.marketriskscenemanage.valid.MarketRiskSceneManage001Validator;
import com.joyintech.workflow.engine.annotation.PrimaryKey;
import com.joyintech.workflow.engine.annotation.PrimaryKey.Field;
import com.joyintech.workflow.engine.dto.ProcessEntity;


/**
 * 市场性风险情景管理DTO
 * 〈功能详细描述〉
 * @author 汪海
 * @version 1.0 2019年9月3日
 * @see MarketRiskSceneManage001Dto
 * @since 1.0
 */
@PageId("MarketRiskSceneManage001")
@ValidLogic(targetClass = MarketRiskSceneManage001Validator.class, groups = {ValidateGroupEffective.class, ValidateGroupDelete.class})
public class MarketRiskSceneManage001Dto extends BaseEntity implements ProcessEntity {

    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    private static final long serialVersionUID = -6922785830353702570L;
    
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
     * 适用风险分类
     */
    private String riskType;
    /**
     * 适用二级分类
     */
    private String riskType2;
    /**
     * 情景类型
     */
    private String sceneType;
    /**
     * 情景二级类型
     */
    private String sceneType2;
    /**
     * 情景名称
     */
    private String sceneName;
    /**
     * 情景编号
     */
    @PrimaryKey(Field.PK_1)
    private String sceneId;
    /**
     * 启用状态
     */
    private String effStatus;
    /**
     * 适用产品
     */
    private String suitProduct;
    /**
     * 区间开始时间
     */
    private String startDate;
    /**
     * 区间结束时间
     */
    private String endDate;
    /**
     * 数据状态
     */
    private String dataStatus;
    
    /**
     * 情景因子名称
     */
    private String ftrName;
    
    /**
     * 备注
     */
    private String remark;
    
    /**
     * 选择数据
     */
    private List<MarketRiskSceneManage001Dto> selectedItem;

    private String boost;

    
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
     * 获取 operateType
     * @return operateType.
     */
    @NotEmpty(fieldName = "MarketRiskSceneManage001.operateType", groups = {ValidateGroupEffective.class, ValidateGroupDelete.class})
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
     * 获取 pageId
     * @return pageId.
     */
    public String getPageId() {
        return pageId;
    }

    
    /**
     * 设置 pageId
     * @param pageId pageId
     */
    public void setPageId(String pageId) {
        this.pageId = pageId;
    }

    
    /**
     * 获取 riskType
     * @return riskType.
     */
    public String getRiskType() {
        return riskType;
    }

    
    /**
     * 设置 riskType
     * @param riskType riskType
     */
    public void setRiskType(String riskType) {
        this.riskType = riskType;
    }

    
    /**
     * 获取 riskType2
     * @return riskType2.
     */
    public String getRiskType2() {
        return riskType2;
    }

    
    /**
     * 设置 riskType2
     * @param riskType2 riskType2
     */
    public void setRiskType2(String riskType2) {
        this.riskType2 = riskType2;
    }

    
    /**
     * 获取 sceneType
     * @return sceneType.
     */
    public String getSceneType() {
        return sceneType;
    }

    
    /**
     * 设置 sceneType
     * @param sceneType sceneType
     */
    public void setSceneType(String sceneType) {
        this.sceneType = sceneType;
    }

    
    /**
     * 获取 sceneType2
     * @return sceneType2.
     */
    public String getSceneType2() {
        return sceneType2;
    }

    
    /**
     * 设置 sceneType2
     * @param sceneType2 sceneType2
     */
    public void setSceneType2(String sceneType2) {
        this.sceneType2 = sceneType2;
    }

    
    /**
     * 获取 sceneName
     * @return sceneName.
     */
    public String getSceneName() {
        return sceneName;
    }

    
    /**
     * 设置 sceneName
     * @param sceneName sceneName
     */
    public void setSceneName(String sceneName) {
        this.sceneName = sceneName;
    }

    
    /**
     * 获取 sceneId
     * @return sceneId.
     */
    public String getSceneId() {
        return sceneId;
    }

    
    /**
     * 设置 sceneId
     * @param sceneId sceneId
     */
    public void setSceneId(String sceneId) {
        this.sceneId = sceneId;
    }

    
    /**
     * 获取 effStatus
     * @return effStatus.
     */
    @NotEmpty(fieldName = "MarketRiskSceneManage001.effStatus", groups = {ValidateGroupEffective.class})
    public String getEffStatus() {
        return effStatus;
    }

    
    /**
     * 设置 effStatus
     * @param effStatus effStatus
     */
    public void setEffStatus(String effStatus) {
        this.effStatus = effStatus;
    }

    
    /**
     * 获取 startDate
     * @return startDate.
     */
    public String getStartDate() {
        return startDate==null?null:startDate.substring(0, 10);
    }
    
    /**
     * 设置 startDate
     * @param startDate startDate
     */
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    
    /**
	 * 获取 suitProduct
	 * @return suitProduct.
	 */
	public String getSuitProduct() {
		return suitProduct;
	}

	/**
	 * 获取 suitProductName
	 * @return suitProductName.
	 */
	public String getSuitProductName() {
		return SysDataUtil.getNameFromDataDict(DictCode.开放模式,  this.suitProduct);
	} 
	
	/**
	 * 设置 suitProduct
	 * @param suitProduct suitProduct
	 */
	public void setSuitProduct(String suitProduct) {
		this.suitProduct = suitProduct;
	}


	/**
     * 获取 endDate
     * @return endDate.
     */
    public String getEndDate() {
        return endDate==null?null:endDate.substring(0, 10);
    }

    
    /**
     * 设置 endDate
     * @param endDate endDate
     */
    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }


    /**
     * 获取 dataStatus
     * @return dataStatus.
     */
    public String getDataStatus() {
        return dataStatus;
    }

    
    /**
     * 设置 dataStatus
     * @param dataStatus dataStatus
     */
    public void setDataStatus(String dataStatus) {
        this.dataStatus = dataStatus;
    }
    
    /**
	 * 获取 ftrName
	 * @return ftrName.
	 */
	public String getFtrName() {
		return ftrName;
	}


	/**
	 * 设置 ftrName
	 * @param ftrName ftrName
	 */
	public void setFtrName(String ftrName) {
		this.ftrName = ftrName;
	}


	/**
     * 获取 selectedItem
     * @return selectedItem.
     */
    public List<MarketRiskSceneManage001Dto> getSelectedItem() {
        return selectedItem;
    }

    
    /**
     * 设置 selectedItem
     * @param selectedItem selectedItem
     */
    public void setSelectedItem(List<MarketRiskSceneManage001Dto> selectedItem) {
        this.selectedItem = selectedItem;
    }

    
    /**
     * 获取 boost
     * @return boost.
     */
    public String getBoost() {
        return boost;
    }

    
    /**
     * 设置 boost
     * @param boost boost
     */
    public void setBoost(String boost) {
        this.boost = boost;
    }

    public String getRiskTypeName() {
        String ss =  SysDataUtil.getNameFromDataDict(DictCode.情景风险分类,  this.riskType);
        return ss;
    }

    public String getSceneTypeName() {
        return SysDataUtil.getNameFromDataDict(DictCode.情景类型,  this.sceneType);
    }
    
    public String getSceneType2Name() {
        return SysDataUtil.getNameFromDataDict(DictCode.情景二级类型,  this.sceneType2);
    }
    
    public String getEffStatusName() {
        return SysDataUtil.getNameFromDataDict(DictCode.情景启用状态,  this.effStatus);
    }
    
    

    /**
	 * 获取 remark
	 * @return remark.
	 */
	public String getRemark() {
		return remark;
	}


	/**
	 * 设置 remark
	 * @param remark remark
	 */
	public void setRemark(String remark) {
		this.remark = remark;
	}
    
}
