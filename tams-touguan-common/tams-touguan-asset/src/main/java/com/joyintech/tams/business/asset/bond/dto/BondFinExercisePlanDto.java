/*
 * 文件名：BussFinRepaymentDto.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangnian
 * 修改时间：2018年10月14日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.business.asset.bond.dto;

import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.validation.annotation.ValidLogic;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.common.base.entity.FinCashPayRule;
import com.joyintech.tams.common.base.entity.FinCashPlan;
import com.joyintech.tams.component.business.asset.dto.FinInterestFactorDto;
import com.joyintech.tams.component.business.asset.dto.FinPayInterestStdInfoDto;
import com.joyintech.tams.component.business.asset.validator.BussFinRepaymentValidator;
import com.joyintech.tams.component.business.project.dto.BussParticipantInfoDto;
import com.joyintech.tams.invest.common.asset.dto.FinCashPlanDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 *  行权计划
 * 〈功能详细描述〉
 * @author wangyang1
 * @version 1.0 2022年4月16日
 * @see BondFinExercisePlanDto
 * @since 1.0
 */
@ApiModel(value="行权计划",description="行权计划")
public class BondFinExercisePlanDto extends BaseEntity {
    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    private static final long serialVersionUID = 1L;

    /**
     * 行权状态，已行权、未行权
     */
    @ApiModelProperty(name="exerciseStatus",value="行权状态",required = true)
    private String exerciseStatus;

    @ApiModelProperty(name="vdate",value="行权申请起始日")
    private Date vdate;

    @ApiModelProperty(name="mdate",value="行权申请终止日")
    private Date mdate;

    @ApiModelProperty(name="exerciseType",value="行权方式",required = true)
    private String exerciseType;

    @ApiModelProperty(name="exerciseDate",value="理论行权日",required = true)
    private Date exerciseDate;

    @ApiModelProperty(name="actExerciseDate",value="实际行权日",required = true)
    private Date actExerciseDate;

    @ApiModelProperty(name="exercisePrice",value="行权净价",required = true)
    private BigDecimal exercisePrice;

    @ApiModelProperty(name="finprodId",value="行权条款 ")
    private String exerciseRemark;

    public String getExerciseStatus() {
        return exerciseStatus;
    }

    public void setExerciseStatus(String exerciseStatus) {
        this.exerciseStatus = exerciseStatus;
    }

    public Date getVdate() {
        return vdate;
    }

    public void setVdate(Date vdate) {
        this.vdate = vdate;
    }

    public Date getMdate() {
        return mdate;
    }

    public void setMdate(Date mdate) {
        this.mdate = mdate;
    }

    public String getExerciseType() {
        return exerciseType;
    }

    public void setExerciseType(String exerciseType) {
        this.exerciseType = exerciseType;
    }

    public Date getExerciseDate() {
        return exerciseDate;
    }

    public void setExerciseDate(Date exerciseDate) {
        this.exerciseDate = exerciseDate;
    }

    public Date getActExerciseDate() {
        return actExerciseDate;
    }

    public void setActExerciseDate(Date actExerciseDate) {
        this.actExerciseDate = actExerciseDate;
    }

    public BigDecimal getExercisePrice() {
        return exercisePrice;
    }

    public void setExercisePrice(BigDecimal exercisePrice) {
        this.exercisePrice = exercisePrice;
    }

    public String getExerciseRemark() {
        return exerciseRemark;
    }

    public void setExerciseRemark(String exerciseRemark) {
        this.exerciseRemark = exerciseRemark;
    }
}
