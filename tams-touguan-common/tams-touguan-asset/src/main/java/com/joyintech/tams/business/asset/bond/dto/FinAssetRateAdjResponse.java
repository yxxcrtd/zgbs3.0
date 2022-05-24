package com.joyintech.tams.business.asset.bond.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.export.paginator.domain.ITableRow;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.tams.common.base.entity.FinCashCalcRule;
import com.joyintech.tams.common.base.entity.FinCashPlan;
import com.joyintech.tams.common.base.entity.PaFinCashCalcRule;
import com.joyintech.workflow.engine.annotation.PrimaryKey;
import com.joyintech.workflow.engine.annotation.PrimaryKey.Field;
import com.joyintech.workflow.engine.dto.ProcessEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 利率调整信息DTO
 *
 * @author wangyang1
 * @version 1.0 2022年4月19日
 * @see FinAssetRateAdjResponse
 * @since 1.0
 */
@PageId("FinBondAsset003")
@ApiModel(value="利率调整信息查询返回参数",description="利率调整信息查询返回参数")
public class FinAssetRateAdjResponse  extends FinCashCalcRule implements ITableRow, ProcessEntity {


    @ApiModelProperty(name="pageId",value="画面Id")
    private String pageId;

    @ApiModelProperty(name="elementId",value="元素Id")
    private String elementId;

    @ApiModelProperty(name="operateType",value="操作类型")
    private String operateType;

    @ApiModelProperty(name="finprodMarketId",value="市场代码")
    private String finprodMarketId;

    @ApiModelProperty(name="finprodName",value="金融产品全称")
    private String finprodName;

    @ApiModelProperty(name="finprodAbbr",value="金融产品简称")
    private String finprodAbbr;

    @ApiModelProperty(name="effDateShow",value="生效日")
    private String effDateShow;

    @ApiModelProperty(name="effDate",value="生效日")
    private Date effDate;

    @ApiModelProperty(name="isInitial",value="生效日")
    private String isInitial;

    @ApiModelProperty(name="yield",value="利率")
    private BigDecimal yield;

    @ApiModelProperty(name="finprodId",value="产品代码")
    private String finprodId;

    @ApiModelProperty(name="finprodType2",value="资产类")
    private String finprodType2;

    @ApiModelProperty(name="selectedItem",value="选择数据")
    private List<FinAssetRateAdjResponse> selectedItem;

    @ApiModelProperty(name="feeId",value="费用id")
    private String feeId;

    @ApiModelProperty(name="issueAmt",value="发行规模")
    private BigDecimal issueAmt;

    private String cashId;

    private String basis;

    private String marketBasis;

    private String couponSpecies;

    @ApiModelProperty(name="paFinCashCalcRuleList",value="计算规则扩展")
    private List<PaFinCashCalcRule> paFinCashCalcRuleList;

    @ApiModelProperty(name="finCashCalcRuleList",value="计算规则")
    private List<FinCashCalcRule> finCashCalcRuleList;

    @ApiModelProperty(name="finCashPlanList",value="现金流计划")
    private List<FinCashPlan> finCashPlanList;


    public List<FinAssetRateAdjResponse> getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(List<FinAssetRateAdjResponse> selectedItem) {
        this.selectedItem = selectedItem;
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

    @PrimaryKey(PrimaryKey.Field.PK_1)
    public String getFinprodMarketId() {
        return finprodMarketId;
    }

    public void setFinprodMarketId(String finprodMarketId) {
        this.finprodMarketId = finprodMarketId;
    }

    public String getFinprodName() {
        return finprodName;
    }

    public void setFinprodName(String finprodName) {
        this.finprodName = finprodName;
    }

    public String getFinprodAbbr() {
        return finprodAbbr;
    }

    public void setFinprodAbbr(String finprodAbbr) {
        this.finprodAbbr = finprodAbbr;
    }

    public String getEffDateShow() {
        return effDateShow;
    }

    public void setEffDateShow(String effDateShow) {
        this.effDateShow = effDateShow;
    }

    public Date getEffDate() {
        return effDate;
    }

    public void setEffDate(Date effDate) {
        this.effDate = effDate;
    }

    public String getIsInitial() {
        return isInitial;
    }

    public void setIsInitial(String isInitial) {
        this.isInitial = isInitial;
    }

    public String getFinprodId() {
        return finprodId;
    }

    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
    }

    public String getFinprodType2() {
        return finprodType2;
    }

    public void setFinprodType2(String finprodType2) {
        this.finprodType2 = finprodType2;
    }

    public String getPageId() {
        return pageId;
    }

    public void setPageId(String pageId) {
        this.pageId = pageId;
    }

    public String getFeeId() {
        return feeId;
    }

    public void setFeeId(String feeId) {
        this.feeId = feeId;
    }

    public BigDecimal getYield() {
        return yield;
    }

    public void setYield(BigDecimal yield) {
        this.yield = yield;
    }

    public BigDecimal getIssueAmt() {
        return issueAmt;
    }

    public void setIssueAmt(BigDecimal issueAmt) {
        this.issueAmt = issueAmt;
    }

    public String getCashId() {
        return cashId;
    }

    public void setCashId(String cashId) {
        this.cashId = cashId;
    }

    public String getBasis() {
        return basis;
    }

    public void setBasis(String basis) {
        this.basis = basis;
    }

    public List<PaFinCashCalcRule> getPaFinCashCalcRuleList() {
        return paFinCashCalcRuleList;
    }

    public void setPaFinCashCalcRuleList(List<PaFinCashCalcRule> paFinCashCalcRuleList) {
        this.paFinCashCalcRuleList = paFinCashCalcRuleList;
    }

    public List<FinCashCalcRule> getFinCashCalcRuleList() {
        return finCashCalcRuleList;
    }

    public void setFinCashCalcRuleList(List<FinCashCalcRule> finCashCalcRuleList) {
        this.finCashCalcRuleList = finCashCalcRuleList;
    }

    public List<FinCashPlan> getFinCashPlanList() {
        return finCashPlanList;
    }

    public void setFinCashPlanList(List<FinCashPlan> finCashPlanList) {
        this.finCashPlanList = finCashPlanList;
    }

    @Override
    public String getMarketBasis() {
        return marketBasis;
    }

    @Override
    public void setMarketBasis(String marketBasis) {
        this.marketBasis = marketBasis;
    }

    /**
     * 净投资收益率
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal investRate;

    public BigDecimal getInvestRate() {
        return investRate;
    }

    public void setInvestRate(BigDecimal investRate) {
        this.investRate = investRate;
    }

    public String getCouponSpecies() {
        return couponSpecies;
    }

    public void setCouponSpecies(String couponSpecies) {
        this.couponSpecies = couponSpecies;
    }
}
