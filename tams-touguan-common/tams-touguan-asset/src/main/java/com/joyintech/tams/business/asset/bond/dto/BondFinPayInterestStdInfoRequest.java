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
import com.joyintech.tams.component.business.asset.dto.FinInterestFactorDto;
import com.joyintech.tams.invest.common.asset.dto.FinCashPlanDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 付息信息
 * @author wangyang1
 * @version 1.0 2022年4月16日
 * @see BondFinPayInterestStdInfoRequest
 * @since 1.0
 */
@ApiModel(value="付息计划生成请求参数",description="付息计划生成请求参数")
public class BondFinPayInterestStdInfoRequest extends FinCashPayRule {

    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(name="finprodMarketId",value="资产代码",required=true)
    private String finprodMarketId;

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
     * 起息日
     */
    @ApiModelProperty(name="vdate",value="起息日" ,required = true)
    private Date vdate;

    /**
     * 到期日
     */
    @ApiModelProperty(name="mdate",value="到息日",required = true)
    private Date mdate;

    /**
     * 存款本金
     */
    @ApiModelProperty(name="issueAmt",value="发行金额（默认100）")
    private BigDecimal issueAmt;

    /**
     * 交易日历
     */
    @ApiModelProperty(name="calendarId",value="交易日历")
    private String calendarId;

    /**
     * 交易日历
     */
    @ApiModelProperty(name="tradeMarket",value="交易场所")
    private String tradeMarket;

    /**
     * 息票品种
     */
    @ApiModelProperty(name="calendarId",value="交易日历")
    private String  couponSpecies;

    /**
     * 付息计划调整标志
     */
    @ApiModelProperty(name="calendarId",value="付息计划调整标志")
    private String adjPlanFlag;

    /**
     * 最大交易日
     */
    @ApiModelProperty(name="maxTradeDate",value="付息计划调整标志")
    private Date maxTradeDate;

    /**
     * 计息要素
     */
    @ApiModelProperty(name="finInterestFactorDto",value="计息要素")
    private BondFinInterestFactorDto finInterestFactorDto;

    /**
     * 付息计划
     */
    @ApiModelProperty(name="invScheduleList",value="付息计划")
    private List<BondFinCashPlanDto>  invScheduleList;

    /**
     * 还本计划
     */
    @ApiModelProperty(name="invRepayList",value="还本计划")
    private List<BondFinCashPlanDto> invRepayList;

    /**
     * 是否还本不带息场景FLag
     */
    @ApiModelProperty(name="invRepayList",value="是否还本不带息场景FLag(默认N)")
    private String isPayIntFlag;

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

    @ApiModelProperty(name="FinprodType2",value="产品类型",required=true)
    private String FinprodType2;

    @Override
    public String getFinprodType2() {
        return FinprodType2;
    }

    public String getTradeMarket() {
        return tradeMarket;
    }

    public void setTradeMarket(String tradeMarket) {
        this.tradeMarket = tradeMarket;
    }

    @Override
    public void setFinprodType2(String finprodType2) {
        FinprodType2 = finprodType2;
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

    public BigDecimal getIssueAmt() {
        return issueAmt;
    }

    public void setIssueAmt(BigDecimal issueAmt) {
        this.issueAmt = issueAmt;
    }

    public String getCalendarId() {
        return calendarId;
    }

    public void setCalendarId(String calendarId) {
        this.calendarId = calendarId;
    }

    public String getCouponSpecies() {
        return couponSpecies;
    }

    public void setCouponSpecies(String couponSpecies) {
        this.couponSpecies = couponSpecies;
    }

    public String getAdjPlanFlag() {
        return adjPlanFlag;
    }

    public void setAdjPlanFlag(String adjPlanFlag) {
        this.adjPlanFlag = adjPlanFlag;
    }

    public Date getMaxTradeDate() {
        return maxTradeDate;
    }

    public void setMaxTradeDate(Date maxTradeDate) {
        this.maxTradeDate = maxTradeDate;
    }

    @Override
    public String getIsPayIntFlag() {
        return isPayIntFlag;
    }

    @Override
    public void setIsPayIntFlag(String isPayIntFlag) {
        this.isPayIntFlag = isPayIntFlag;
    }

    public BondFinInterestFactorDto getFinInterestFactorDto() {
        return finInterestFactorDto;
    }

    public void setFinInterestFactorDto(BondFinInterestFactorDto finInterestFactorDto) {
        this.finInterestFactorDto = finInterestFactorDto;
    }

    public List<BondFinCashPlanDto> getInvScheduleList() {
        return invScheduleList;
    }

    public void setInvScheduleList(List<BondFinCashPlanDto> invScheduleList) {
        this.invScheduleList = invScheduleList;
    }

    public List<BondFinCashPlanDto> getInvRepayList() {
        return invRepayList;
    }

    public void setInvRepayList(List<BondFinCashPlanDto> invRepayList) {
        this.invRepayList = invRepayList;
    }

    @Override
    public String getPayCycle() {
        return payCycle;
    }

    @Override
    public void setPayCycle(String payCycle) {
        this.payCycle = payCycle;
    }

    @Override
    public String getPayTdayRule() {
        return payTdayRule;
    }

    @Override
    public void setPayTdayRule(String payTdayRule) {
        this.payTdayRule = payTdayRule;
    }

    @Override
    public String getCalcRule() {
        return calcRule;
    }

    @Override
    public void setCalcRule(String calcRule) {
        this.calcRule = calcRule;
    }

    public String getFinprodMarketId() {
        return finprodMarketId;
    }

    public void setFinprodMarketId(String finprodMarketId) {
        this.finprodMarketId = finprodMarketId;
    }
}
