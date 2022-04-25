/*
 * 文件名：FinAssetRateAdjDto.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangnian
 * 修改时间：2018年10月11日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.valuation.common.asset.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.tams.common.base.entity.FinCashCalcRule;
import com.joyintech.tams.common.base.entity.FinCashPlan;
import com.joyintech.tams.common.base.entity.PaFinCashCalcRule;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 利率调整
 * 〈功能详细描述〉
 * @author zhangnian
 * @version 1.0 2018年10月11日
 * @see RateAdjDto
 * @since 1.0
 */
public class RateAdjDto extends FinCashCalcRule{

    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    private static final long serialVersionUID = 1L;
    /**
     * 操作类型
     */
    private String operateType;
    
    private String pageModel;
    
    private String elementId;
    
    /**
     * 区别check
     * 01:点击利率查询页面利率调整
     */
    private String type;
    
    /**
     * 行是否编辑标志
     */
    private String updateFlg;
    
    /**
     * 金融产品全称
     */
    private String finprodName;
    
    
    /**
     * 金融产品简称
     */
    private String finprodAbbr;


    /**
     * 市场代码，可以按规则生成，或者手输，不校验唯一。对于理财产品分层，同母金融产品代码对应的市场代码。
     */
    private String finprodMarketId;
    
    /**
     * 起息日
     */
    private Date vdate;
    
    /**
     * 到期日
     */
    private Date mdate;
    
    /**
     * 平台日
     */
    private Date nowSystemDate;

    /**
     * 交易日历
     */
    private String calendarId;
    
    /**
     * 金融产品类型（投管分类），债券、理财产品、基金、理财产品模板、理财产品分层
     */
    private String finprodType2;

    /**
     * 发行规模
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal issueAmt;
    
    /**
     * 生效日(显示)
     */
    private Date effDateShow;
    
    /**
     * 是否可编辑
     */
    private String isEdit;
    
    /**
     * 买断利率
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal buyOutRate;

    /**
     * 借款利率
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal loadRate;

    /**
     * 净投资收益率
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal investRate;

    /**
     * 资产推荐费率
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal referRate;
    
    /**
     * 选中的记录
     */
    private transient List<RateAdjDto> selectedItem;
    
    /**
     * 计算规则
     */
    List<FinCashCalcRule> finCashCalcRuleList;
    
    
    /**
     * 计算规则扩展
     */
    private List<PaFinCashCalcRule> paFinCashCalcRuleList;
    
    /**
     * 付息计划
     */
    private List<FinCashPlanDto>  scheList;
    
    /**
     * 还本计划
     */
    private List<FinCashPlan>   invRepayList;
    
    /**
     * 现金流计划
     */
    private List<FinCashPlan> finCashPlanList;
    
    /**
     * 费用id
     */
    private String feeId;
    
    /**
     * 获取 buyOutRate
     * @return buyOutRate.
     */
    public BigDecimal getBuyOutRate() {
        return buyOutRate;
    }


    
    /**
     * 设置 buyOutRate
     * @param buyOutRate buyOutRate
     */
    public void setBuyOutRate(BigDecimal buyOutRate) {
        this.buyOutRate = buyOutRate;
    }


    
    /**
     * 获取 loadRate
     * @return loadRate.
     */
    public BigDecimal getLoadRate() {
        return loadRate;
    }


    
    /**
     * 设置 loadRate
     * @param loadRate loadRate
     */
    public void setLoadRate(BigDecimal loadRate) {
        this.loadRate = loadRate;
    }


    
    /**
     * 获取 investRate
     * @return investRate.
     */
    public BigDecimal getInvestRate() {
        return investRate;
    }


    
    /**
     * 设置 investRate
     * @param investRate investRate
     */
    public void setInvestRate(BigDecimal investRate) {
        this.investRate = investRate;
    }


    
    /**
     * 获取 referRate
     * @return referRate.
     */
    public BigDecimal getReferRate() {
        return referRate;
    }


    
    /**
     * 设置 referRate
     * @param referRate referRate
     */
    public void setReferRate(BigDecimal referRate) {
        this.referRate = referRate;
    }


    /**
     * 获取 effDateShow
     * @return effDateShow.
     */
    public Date getEffDateShow() {
        return effDateShow;
    }

    
    /**
     * 设置 effDateShow
     * @param effDateShow effDateShow
     */
    public void setEffDateShow(Date effDateShow) {
        this.effDateShow = effDateShow;
    }

    
    /**
     * 获取 isEdit
     * @return isEdit.
     */
    public String getIsEdit() {
        return isEdit;
    }

    
    /**
     * 设置 isEdit
     * @param isEdit isEdit
     */
    public void setIsEdit(String isEdit) {
        this.isEdit = isEdit;
    }


    
    /**
     * 获取 finprodName
     * @return finprodName.
     */
    public String getFinprodName() {
        return finprodName;
    }


    
    /**
     * 设置 finprodName
     * @param finprodName finprodName
     */
    public void setFinprodName(String finprodName) {
        this.finprodName = finprodName;
    }


    
    /**
     * 获取 finprodMarketId
     * @return finprodMarketId.
     */
    public String getFinprodMarketId() {
        return finprodMarketId;
    }


    
    /**
     * 设置 finprodMarketId
     * @param finprodMarketId finprodMarketId
     */
    public void setFinprodMarketId(String finprodMarketId) {
        this.finprodMarketId = finprodMarketId;
    }

    
    /**
     * 获取 operateType
     * @return operateType.
     */
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
     * 获取 selectedItem
     * @return selectedItem.
     */
    public List<RateAdjDto> getSelectedItem() {
        return selectedItem;
    }

    
    /**
     * 设置 selectedItem
     * @param selectedItem selectedItem
     */
    public void setSelectedItem(List<RateAdjDto> selectedItem) {
        this.selectedItem = selectedItem;
    }


    
    
    
    /**
     * 获取 finCashCalcRuleList
     * @return finCashCalcRuleList.
     */
    public List<FinCashCalcRule> getFinCashCalcRuleList() {
        return finCashCalcRuleList;
    }



    
    /**
     * 设置 finCashCalcRuleList
     * @param finCashCalcRuleList finCashCalcRuleList
     */
    public void setFinCashCalcRuleList(List<FinCashCalcRule> finCashCalcRuleList) {
        this.finCashCalcRuleList = finCashCalcRuleList;
    }



    /**
     * 获取 paFinCashCalcRuleList
     * @return paFinCashCalcRuleList.
     */
    public List<PaFinCashCalcRule> getPaFinCashCalcRuleList() {
        return paFinCashCalcRuleList;
    }



    
    /**
     * 设置 paFinCashCalcRuleList
     * @param paFinCashCalcRuleList paFinCashCalcRuleList
     */
    public void setPaFinCashCalcRuleList(List<PaFinCashCalcRule> paFinCashCalcRuleList) {
        this.paFinCashCalcRuleList = paFinCashCalcRuleList;
    }



    
    /**
     * 获取 updateFlg
     * @return updateFlg.
     */
    public String getUpdateFlg() {
        return updateFlg;
    }



    
    /**
     * 设置 updateFlg
     * @param updateFlg updateFlg
     */
    public void setUpdateFlg(String updateFlg) {
        this.updateFlg = updateFlg;
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
     * 获取 scheList
     * @return scheList.
     */
    public List<FinCashPlanDto> getScheList() {
        return scheList;
    }



    
    /**
     * 设置 scheList
     * @param scheList scheList
     */
    public void setScheList(List<FinCashPlanDto> scheList) {
        this.scheList = scheList;
    }



    
    /**
     * 获取 invRepayList
     * @return invRepayList.
     */
    public List<FinCashPlan> getInvRepayList() {
        return invRepayList;
    }



    
    /**
     * 设置 invRepayList
     * @param invRepayList invRepayList
     */
    public void setInvRepayList(List<FinCashPlan> invRepayList) {
        this.invRepayList = invRepayList;
    }



    
    /**
     * 获取 finprodAbbr
     * @return finprodAbbr.
     */
    public String getFinprodAbbr() {
        return finprodAbbr;
    }



    
    /**
     * 设置 finprodAbbr
     * @param finprodAbbr finprodAbbr
     */
    public void setFinprodAbbr(String finprodAbbr) {
        this.finprodAbbr = finprodAbbr;
    }



    
    /**
     * 获取 finCashPlanList
     * @return finCashPlanList.
     */
    public List<FinCashPlan> getFinCashPlanList() {
        return finCashPlanList;
    }



    
    /**
     * 设置 finCashPlanList
     * @param finCashPlanList finCashPlanList
     */
    public void setFinCashPlanList(List<FinCashPlan> finCashPlanList) {
        this.finCashPlanList = finCashPlanList;
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
     * 获取 type
     * @return type.
     */
    public String getType() {
        return type;
    }



    
    /**
     * 设置 type
     * @param type type
     */
    public void setType(String type) {
        this.type = type;
    }



    
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
     * 获取 nowSystemDate
     * @return nowSystemDate.
     */
    public Date getNowSystemDate() {
        return nowSystemDate;
    }



    
    /**
     * 设置 nowSystemDate
     * @param nowSystemDate nowSystemDate
     */
    public void setNowSystemDate(Date nowSystemDate) {
        this.nowSystemDate = nowSystemDate;
    }



    
    /**
     * 获取 feeId
     * @return feeId.
     */
    public String getFeeId() {
        return feeId;
    }



    
    /**
     * 设置 feeId
     * @param feeId feeId
     */
    public void setFeeId(String feeId) {
        this.feeId = feeId;
    }

    
}
