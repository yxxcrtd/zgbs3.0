/*
 * 文件名：RskSupervisionIndicatorDto.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：裴家俊
 * 修改时间：2018年10月23日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.riskredis.quota.superviseindicator.dto;

import com.joyintech.tams.common.base.entity.RskIsupIndicator;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.framework.validation.annotation.Length;
import com.joyintech.framework.validation.annotation.NotEmpty;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeRds;

/**
 * 
 * 监督指标基本信息
 * @author 裴家俊
 * @version 1.0 2018年10月23日
 * @see RskSupervisionIndicatorDto
 * @since 1.0
 */
public class RskSupervisionIndicatorDto  extends RskIsupIndicator {

    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    private static final long serialVersionUID = 1L;

    private String portfolioCodeConst;

   	public String getPortfolioCodeConst() {
   		return portfolioCodeConst;
   	}

   	public void setPortfolioCodeConst(String portfolioCodeConst) {
   		this.portfolioCodeConst = portfolioCodeConst;
   	}
   	
    @Override
	@Length(fieldName = "RskSupervisionIndicator002.indName", max = 200,groups={ValidateGroupEdit.class})
	@NotEmpty(fieldName = "RskSupervisionIndicator002.indName",groups={ValidateGroupEdit.class})
	public String getIndName() {
		return super.getIndName();
	}

	
    @Override
	@NotEmpty(fieldName = "RskSupervisionIndicator002.indType",groups={ValidateGroupEdit.class})
	public String getIndType() {
		return super.getIndType();
	}
	
    @Override
   	@NotEmpty(fieldName = "RskSupervisionIndicator002.supType",groups={ValidateGroupEdit.class})
	public String getSupType() {
		return super.getSupType();
	}
  
    @Override
	@NotEmpty(fieldName = "RskSupervisionIndicator002.timePoint",groups={ValidateGroupEdit.class})
	public String getTimePoint() {
		return super.getTimePoint();
	}
    
    @Override
   	@NotEmpty(fieldName = "RskSupervisionIndicator002.isAdjust",groups={ValidateGroupEdit.class})
   	public String getIsAdjust() {
   		return super.getIsAdjust();
   	}
    
    private Long adjust;

   	public Long getAdjust() {
   		return adjust;
   	}

   	public void setAdjust(Long adjust) {
   		this.adjust = adjust;
   	}

    @Override
   	@Length(fieldName = "RskSupervisionIndicator002.expr", max = 600,groups={ValidateGroupEdit.class})
   	@NotEmpty(fieldName = "RskSupervisionIndicator002.expr",groups={ValidateGroupEdit.class})
   	public String getExpr() {
   		return super.getExpr();
   	}
    
    @Override
	@Length(fieldName = "RskSupervisionIndicator002.memo", max = 300,groups={ValidateGroupEdit.class})
	public String getMemo() {
		return super.getMemo();
	}
    
	public String getIndTypeStr() {
		return SysDataUtil.getNameFromDataDict(BussinessDictCodeRds.指标类型, this.getIndType());
	}
	 public String getSupTypeStr(){
    	 return SysDataUtil.getNameFromDataDict(BussinessDictCodeRds.监督类型, this.getSupType());
    }
	 public String getIsEnabledStr(){
    	 return SysDataUtil.getNameFromDataDict(BussinessDictCodeRds.使用状态, this.getIsEnabled());
	 }
}
