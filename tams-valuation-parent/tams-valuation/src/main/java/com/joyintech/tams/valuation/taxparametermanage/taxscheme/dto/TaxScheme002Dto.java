/*
 * 文件名：TaxScheme002Dto.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangwei
 * 修改时间：2018年10月12日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.valuation.taxparametermanage.taxscheme.dto;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.NotBlank;

import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.validation.annotation.ValidLogic;
import com.joyintech.framework.validation.groups.ValidateGroupDelete;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.valuation.taxparametermanage.taxscheme.validator.TaxScheme002DtoValidator;
import com.joyintech.workflow.engine.annotation.PrimaryKey;
import com.joyintech.workflow.engine.annotation.PrimaryKey.Field;
import com.joyintech.workflow.engine.dto.ProcessEntity;

/**
 * 税务方案
 * @author zhangwei
 * @version 1.0 2018年10月12日
 * @see TaxScheme002Dto
 * @since 1.0
 */
@PageId("TaxScheme002")
@ValidLogic(targetClass = TaxScheme002DtoValidator.class, groups = {ValidateGroupEdit.class,ValidateGroupDelete.class})
public class TaxScheme002Dto extends BaseEntity implements ProcessEntity{

	/**
     * 方案代码
     */
    @PrimaryKey(value=Field.PK_1)
	@NotBlank(message="税务方案代码不能为空！", groups= {ValidateGroupEdit.class})
    private String schId;
    
    /**
     * 方案名称
     */
    @PrimaryKey(value=Field.PK_2)
	@NotBlank(message="税务方案名称不能为空！", groups= {ValidateGroupEdit.class})
    private String schName;
    
	/**
     * 方案类型
     */
    private String schemeType;
	
    /**
     * 是否默认
     */
    @NotBlank(message="是否默认不能为空！", groups= {ValidateGroupEdit.class})
    private String isDefault;
    
    /**
     * 方案描述
     */
    private String remark;

    /**
     * 处理操作
     */
    private String operateType;
    
    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    private String pageModel;
    
    /**
     * 被拷贝 方案代码
     */
    private String refSchId;
	
	/**
	 * 复制范围
	 */
	private List<String> copyRange;
	
	/**
     * 复制范围(回显)
     */
    private String copyRanges;
    
	public String getSchId() {
		return schId;
	}

	public void setSchId(String schId) {
		this.schId = schId;
	}

	public String getSchName() {
		return schName;
	}

	public void setSchName(String schName) {
		this.schName = schName;
	}

	public String getIsDefault() {
		return isDefault;
	}

	public void setIsDefault(String isDefault) {
		this.isDefault = isDefault;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	
	
	public String getSchemeType() {
		return schemeType;
	}

	public void setSchemeType(String schemeType) {
		this.schemeType = schemeType;
	}

	public String getOperateType() {
		return operateType;
	}

	public void setOperateType(String operateType) {
		this.operateType = operateType;
	}

	public String getRefSchId() {
		return refSchId;
	}

	public void setRefSchId(String refSchId) {
		this.refSchId = refSchId;
	}

	public List<String> getCopyRange() {
		return copyRange;
	}

	public void setCopyRange(List<String> copyRange) {
		this.copyRange = copyRange;
	}

    
    public String getPageModel() {
        return pageModel;
    }

    
    public void setPageModel(String pageModel) {
        this.pageModel = pageModel;
    }

    
    public String getCopyRanges() {
        String copyRanges = StringUtils.EMPTY;
        if(CollectionUtils.isNotEmpty(copyRange)) {
            copyRanges = StringUtils.join(copyRange.toArray(), ",");
        }
        return copyRanges;
    }

    
    public void setCopyRanges(String copyRanges) {
        this.copyRanges = copyRanges;
    }

	
    
    
    
	
}
