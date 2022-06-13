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

import com.joyintech.tams.common.base.entity.FinCashPayRule;
import com.joyintech.tams.common.base.entity.FinCashPlan;
import com.joyintech.tams.component.business.project.dto.BussParticipantInfoDto;
import com.joyintech.tams.invest.common.asset.dto.FinCashPlanDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 *  还本信息
 * 〈功能详细描述〉
 * @author zhangnian
 * @version 1.0 2018年10月14日
 * @see BondBussFinRepaymentDto
 * @since 1.0
 */
@ApiModel(value="还本信息参数",description="还本信息参数")
public class BondBussFinRepaymentDto{
    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    private static final long serialVersionUID = 1L;

    private String pageModel;

    /**
     * 是否初始化
     */

    private String initFlag;

    /**
     * 是否重新计算利息
     */
    @ApiModelProperty(name="isAgainCalInt",value="是否重新计算利息")
    private String isAgainCalInt;
    
    /**
     * entityName
     */
    private String entityName;
    
    /**
     * 金融产品类型（投管分类），债券、理财产品、基金、理财产品模板、理财产品分层
     */
    @ApiModelProperty(name="finprodType2",value="金融产品类型（投管分类）")
    private String finprodType2;
    /**
     * 起息日
     */
    @ApiModelProperty(name="vdate",value="起息日")
    private Date vdate;

    /**
     * 到期日
     */
    @ApiModelProperty(name="mdate",value="起息日")
    private Date mdate;
    
    /**
     * 交易日历
     */
    @ApiModelProperty(name="calendarId",value="交易日历")
    private String calendarId;
    
    /**
     * 息票品种
     */
    @ApiModelProperty(name="couponSpecies",value="息票品种")
    private String  couponSpecies;
    
    /**
     * 发行规模
     */
    @ApiModelProperty(name="issueAmt",value="发行规模")
    private BigDecimal issueAmt;
    
    /**
     * 付息计划调整标志
     */
    @ApiModelProperty(name="adjPlanFlag",value="付息计划调整标志")
    private String adjPlanFlag;
    
    /**
     * 计息要素
     */
    @ApiModelProperty(name="finInterestFactorDto",value="计息要素")
    private BondFinInterestFactorDto finInterestFactorDto;
    
    /**
     * 付息信息要素
     */
    @ApiModelProperty(name="finInterestFactorDto",value="付息信息要素")
    private BondFinPayInterestStdInfoDto finPayInterestStdInfoDto;
    
    /**
     * 参与方信息(通道费)
     */
    private List<BussParticipantInfoDto> bussParticipantInfoDtoList;
    
    /**
     * 还本计划
     */
    @ApiModelProperty(name="invRepayList",value="还本计划")
    private List<BondFinCashPlanDto> invRepayList;
    
    /**
     * 付息计划
     */
    @ApiModelProperty(name="invScheduleList",value="付息计划")
    private List<BondFinCashPlanDto>  invScheduleList;
    
    /**
     * 现金流计划(前台传入)
     */
    private List<FinCashPlanDto> finCashPlanList;
    
    /**
     * 现金流计划(入库)
     */
    private List<FinCashPlan> finCashPlan2List;
    
    /**
     * 计算规则(项目类资产登陆时需要)
     */
    private FinCashPayRule finCashPayRule;

    /**
     * 是否还本不带息(项目类资产，计划类利率型资产需要)
     */
    private String isPayInt;
    
    
    /**
     * 获取 finprodType2
     * @return finprodType2.
     */
    public String getFinprodType2() {
        return finprodType2;
    }

    
    /**
     * 设置 finprodType2
     * @param finprodType2 finprodType2
     */
    public void setFinprodType2(String finprodType2) {
        this.finprodType2 = finprodType2;
    }

    /**
     * 获取 vdate
     * @return vdate.
     */
    public Date getVdate() {
        return vdate;
    }

    /**
     * 设置 vdate
     * @param vdate vdate
     */
    public void setVdate(Date vdate) {
        this.vdate = vdate;
    }

    /**
     * 获取 mdate
     * @return mdate.
     */
    public Date getMdate() {
        return mdate;
    }

    /**
     * 设置 mdate
     * @param mdate mdate
     */
    public void setMdate(Date mdate) {
        this.mdate = mdate;
    }


    /**
     * 获取 finCashPlanList
     * @return finCashPlanList.
     */
    public List<FinCashPlanDto> getFinCashPlanList() {
        return finCashPlanList;
    }
    
    /**
     * 设置 finCashPlanList
     * @param finCashPlanList finCashPlanList
     */
    public void setFinCashPlanList(List<FinCashPlanDto> finCashPlanList) {
        this.finCashPlanList = finCashPlanList;
    }

    /**
     * 获取 calendarId
     * @return calendarId.
     */
    public String getCalendarId() {
        return calendarId;
    }

    /**
     * 设置 calendarId
     * @param calendarId calendarId
     */
    public void setCalendarId(String calendarId) {
        this.calendarId = calendarId;
    }

    /**
     * 获取 couponSpecies
     * @return couponSpecies.
     */
    public String getCouponSpecies() {
        return couponSpecies;
    }

    /**
     * 设置 couponSpecies
     * @param couponSpecies couponSpecies
     */
    public void setCouponSpecies(String couponSpecies) {
        this.couponSpecies = couponSpecies;
    }

    /**
     * 获取 initFlag
     * @return initFlag.
     */
    public String getInitFlag() {
        return initFlag;
    }


    
    /**
     * 设置 initFlag
     * @param initFlag initFlag
     */
    public void setInitFlag(String initFlag) {
        this.initFlag = initFlag;
    }


    
    /**
     * 获取 entityName
     * @return entityName.
     */
    public String getEntityName() {
        return entityName;
    }


    
    /**
     * 设置 entityName
     * @param entityName entityName
     */
    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }


    
    /**
     * 获取 bussParticipantInfoDtoList
     * @return bussParticipantInfoDtoList.
     */
    public List<BussParticipantInfoDto> getBussParticipantInfoDtoList() {
        return bussParticipantInfoDtoList;
    }


    
    /**
     * 设置 bussParticipantInfoDtoList
     * @param bussParticipantInfoDtoList bussParticipantInfoDtoList
     */
    public void setBussParticipantInfoDtoList(List<BussParticipantInfoDto> bussParticipantInfoDtoList) {
        this.bussParticipantInfoDtoList = bussParticipantInfoDtoList;
    }


    
    /**
     * 获取 issueAmt
     * @return issueAmt.
     */
    public BigDecimal getIssueAmt() {
        return issueAmt;
    }


    
    /**
     * 设置 issueAmt
     * @param issueAmt issueAmt
     */
    public void setIssueAmt(BigDecimal issueAmt) {
        this.issueAmt = issueAmt;
    }


    
    /**
     * 获取 isAgainCalInt
     * @return isAgainCalInt.
     */
    public String getIsAgainCalInt() {
        return isAgainCalInt;
    }


    
    /**
     * 设置 isAgainCalInt
     * @param isAgainCalInt isAgainCalInt
     */
    public void setIsAgainCalInt(String isAgainCalInt) {
        this.isAgainCalInt = isAgainCalInt;
    }

    /**
     * 获取 finCashPayRule
     * @return finCashPayRule.
     */
    public FinCashPayRule getFinCashPayRule() {
        return finCashPayRule;
    }

    /**
     * 设置 finCashPayRule
     * @param finCashPayRule finCashPayRule
     */
    public void setFinCashPayRule(FinCashPayRule finCashPayRule) {
        this.finCashPayRule = finCashPayRule;
    }

    /**
     * 获取 finCashPlan2List
     * @return finCashPlan2List.
     */
    public List<FinCashPlan> getFinCashPlan2List() {
        return finCashPlan2List;
    }

    /**
     * 设置 finCashPlan2List
     * @param finCashPlan2List finCashPlan2List
     */
    public void setFinCashPlan2List(List<FinCashPlan> finCashPlan2List) {
        this.finCashPlan2List = finCashPlan2List;
    }


    
    /**
     * 获取 adjPlanFlag
     * @return adjPlanFlag.
     */
    public String getAdjPlanFlag() {
        return adjPlanFlag;
    }


    
    /**
     * 设置 adjPlanFlag
     * @param adjPlanFlag adjPlanFlag
     */
    public void setAdjPlanFlag(String adjPlanFlag) {
        this.adjPlanFlag = adjPlanFlag;
    }


    public String getPageModel() {
        return pageModel;
    }

    public void setPageModel(String pageModel) {
        this.pageModel = pageModel;
    }

    /**
     * 获取 isPayInt
     *
     * @return isPayInt
     */
    public String getIsPayInt() {
        return isPayInt;
    }

    /**
     * 设置 isPayInt
     *
     * @param isPayInt isPayInt
     */
    public void setIsPayInt(String isPayInt) {
        this.isPayInt = isPayInt;
    }

    public BondFinInterestFactorDto getFinInterestFactorDto() {
        return finInterestFactorDto;
    }

    public void setFinInterestFactorDto(BondFinInterestFactorDto finInterestFactorDto) {
        this.finInterestFactorDto = finInterestFactorDto;
    }

    public BondFinPayInterestStdInfoDto getFinPayInterestStdInfoDto() {
        return finPayInterestStdInfoDto;
    }

    public void setFinPayInterestStdInfoDto(BondFinPayInterestStdInfoDto finPayInterestStdInfoDto) {
        this.finPayInterestStdInfoDto = finPayInterestStdInfoDto;
    }

    public List<BondFinCashPlanDto> getInvRepayList() {
        return invRepayList;
    }

    public void setInvRepayList(List<BondFinCashPlanDto> invRepayList) {
        this.invRepayList = invRepayList;
    }

    public List<BondFinCashPlanDto> getInvScheduleList() {
        return invScheduleList;
    }

    public void setInvScheduleList(List<BondFinCashPlanDto> invScheduleList) {
        this.invScheduleList = invScheduleList;
    }
}
