/*
 * 文件名：FinPayInterestStdInfoDto.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangnian
 * 修改时间：2018年9月11日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.business.asset.bond.dto;

import com.joyintech.framework.base.BaseEntity;
import com.joyintech.tams.common.base.entity.FinCashPayRule;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import java.util.List;

/**
 * 付息信息
 * @author wangyang1
 * @version 1.0 2022年4月16日
 * @see BondFinPayInterestStdInfoDto
 * @since 1.0
 */
@ApiModel(value="付息信息/",description="付息信息")
public class BondFinPayInterestStdInfoDto extends BaseEntity {

    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(name="finprodId",value="金融产品代码")
    private String finprodId;

    /**
     * 营业日准则
     */
    @ApiModelProperty(name="payTdayRule",value="营业日准则",required=true)
    private String payTdayRule;

    /**
     * 支付频率
     */
    @ApiModelProperty(name="payCycle",value="支付频率",required=true)
    private String payCycle;

    /**
     * 推算方式
     */
    @ApiModelProperty(name="calcRule",value="推算方式",required=true)
    private String calcRule;

    /**
     * 首次支付日
     */
    @ApiModelProperty(name="firstPayDate",value="首次付息日(条件必填)")
    private Date firstPayDate;

    /**
     * 付息计划
     */
    @ApiModelProperty(name="invScheduleList",value="付息计划")
    private List<BondFinCashPlanDto>  invScheduleList;

    public String getPayTdayRule() {
        return payTdayRule;
    }

    public void setPayTdayRule(String payTdayRule) {
        this.payTdayRule = payTdayRule;
    }

    public String getPayCycle() {
        return payCycle;
    }

    public void setPayCycle(String payCycle) {
        this.payCycle = payCycle;
    }

    public String getCalcRule() {
        return calcRule;
    }

    public void setCalcRule(String calcRule) {
        this.calcRule = calcRule;
    }

    public Date getFirstPayDate() {
        return firstPayDate;
    }

    public void setFirstPayDate(Date firstPayDate) {
        this.firstPayDate = firstPayDate;
    }

    public List<BondFinCashPlanDto> getInvScheduleList() {
        return invScheduleList;
    }

    public void setInvScheduleList(List<BondFinCashPlanDto> invScheduleList) {
        this.invScheduleList = invScheduleList;
    }

    public String getFinprodId() {
        return finprodId;
    }

    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
    }

    /**
     * 付息计划调整标志
     */
    @ApiModelProperty(name="adjPlanFlag",value="付息计划调整标志")
    private String adjPlanFlag;

    /**
     * 金融产品类型（投管分类），债券、理财产品、基金、理财产品模板、理财产品分层
     */
    @ApiModelProperty(name="finprodType2",value="金融产品类型（投管分类）")
    private String finprodType2;

    /**
     * 最大交易日
     */
    @ApiModelProperty(name="maxTradeDate",value="最大交易日")
    private Date maxTradeDate;

    /**
     * 是否还本计划生成计划回调付息计划
     */
    @ApiModelProperty(name="iscallbackPayByRepay",value="是否还本计划生成计划回调付息计划")
    private String iscallbackPayByRepay;

    /**
     * 是否重新计算利息
     */
    @ApiModelProperty(name="isAgainCalInt",value="是否重新计算利息(生成计划传false、确认按钮传true)")
    private String isAgainCalInt;

    /**
     * 是否计划区间利息计算
     */
    @ApiModelProperty(name="isSubAmtCal",value="是否计划区间利息计算(默认传false)")
    private String isSubAmtCal;
    /**
     * 现金流支付规则
     */
    private FinCashPayRule finCashPayRule;

    private String operateType;

    private String remark;
    /**
     * 息票品种
     */
    private String  couponSpecies;

    public String getAdjPlanFlag() {
        return adjPlanFlag;
    }

    public void setAdjPlanFlag(String adjPlanFlag) {
        this.adjPlanFlag = adjPlanFlag;
    }

    public String getFinprodType2() {
        return finprodType2;
    }

    public void setFinprodType2(String finprodType2) {
        this.finprodType2 = finprodType2;
    }

    public Date getMaxTradeDate() {
        return maxTradeDate;
    }

    public void setMaxTradeDate(Date maxTradeDate) {
        this.maxTradeDate = maxTradeDate;
    }

    public FinCashPayRule getFinCashPayRule() {
        return finCashPayRule;
    }

    public void setFinCashPayRule(FinCashPayRule finCashPayRule) {
        this.finCashPayRule = finCashPayRule;
    }

    public String getCouponSpecies() {
        return couponSpecies;
    }

    public void setCouponSpecies(String couponSpecies) {
        this.couponSpecies = couponSpecies;
    }

    public String getOperateType() {
        return operateType;
    }

    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getIscallbackPayByRepay() {
        return iscallbackPayByRepay;
    }

    public void setIscallbackPayByRepay(String iscallbackPayByRepay) {
        this.iscallbackPayByRepay = iscallbackPayByRepay;
    }

    public String getIsAgainCalInt() {
        return isAgainCalInt;
    }

    public void setIsAgainCalInt(String isAgainCalInt) {
        this.isAgainCalInt = isAgainCalInt;
    }

    public String getIsSubAmtCal() {
        return isSubAmtCal;
    }

    public void setIsSubAmtCal(String isSubAmtCal) {
        this.isSubAmtCal = isSubAmtCal;
    }
}
