package com.joyintech.tams.cfets.etr.bondfirstbid.dto;


import com.joyintech.tams.cfets.etr.bond.dto.CfetsEtribdoRiskcalResponse;
import com.joyintech.tams.common.base.entity.EtrEntrust;
import com.joyintech.tams.invest.common.risktrial.dto.RiskTrialRetDto;
import com.joyintech.workflow.engine.dto.ProcessEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;
import java.util.List;

/**
 * @Author: kchen
 * @Date: 2022/4/19 9:31
 */
@ApiModel(value="一级投标指令提交类",description="一级投标指令提交类")
public class CfetsEtrBondFirstBid002Dto extends EtrEntrust implements ProcessEntity {

    /**
     * 按钮id
     */
    @ApiModelProperty(name="elementId",value="按钮id")
    private String elementId;

    /**
     * 操作类型
     */
    @ApiModelProperty(name="operateType",value="操作类型")
    private String operateType;

    /**
     *  发行方式
     */
    @ApiModelProperty(name="issueClass",value="发行方式")
    private String issueClass;

    /**
     *  招标标的
     */
    @ApiModelProperty(name="tenderTarget",value="招标标的")
    private String tenderTarget;

    /**
     *  投标起始日
     */
    @ApiModelProperty(name="tenderSdate",value="投标起始日")
    private Date tenderSdate;

    /**
     *  投标截止日
     */
    @ApiModelProperty(name="tenderEdate",value="投标截止日")
    private Date tenderEdate;

    /**
     *  缴款截止日
     */
    @ApiModelProperty(name="payEdate",value="缴款截止日")
    private Date payEdate;

    /**
     *  发行日
     */
    @ApiModelProperty(name="issueDate",value="发行日")
    private Date issueDate;

    /**
     *  缴款日
     */
    @ApiModelProperty(name="payDate",value="缴款日")
    private Date payDate;

    /**
     * 风险试算实体类
     */
    @ApiModelProperty(name="riskTrialRetDto",value="风险试算实体类")
    private List<RiskTrialRetDto> riskTrialRetDto;

    /**
     * 风险试算dto
     */
    @ApiModelProperty(name="cfetsEtribdoRiskcalReturn",value="风险试算dto",example="CfetsEtribdoRiskcalReturn")
    private CfetsEtribdoRiskcalResponse cfetsEtribdoRiskcalReturn;

    /**
     *  汇总投标券面总额
     */
    @ApiModelProperty(name="totalBiddingNum",value="汇总投标券面总额")
    private String totalBiddingNum;

    /**
     * 组合分配：投标产品列表
     */
    @ApiModelProperty(name="biddingProductList",value="组合分配",example="T01", required = true)
    List<CfetsBiddingProductDto> biddingProductList;

    /**
     * 多组合汇总指令ID
     */
    @ApiModelProperty(name="totalEtrustId",value="多组合汇总指令ID")
    private String totalEtrustId;

    /**
     * 软控是否继续 Y：继续
     */
    @ApiModelProperty(name="softSkipMode",value="软控是否继续？Y：继续",example="Y")
    private String softSkipMode;

    List<CfetsEtrBondFirstBid002Dto> etrInvestBondFirstBidDtos;

    /**
     * 资产简称
     */
    @ApiModelProperty(name="finprodAbbr",value="资产简称",example="资产简称")
    private String finprodAbbr;

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

    public String getIssueClass() {
        return issueClass;
    }

    public void setIssueClass(String issueClass) {
        this.issueClass = issueClass;
    }

    public String getTenderTarget() {
        return tenderTarget;
    }

    public void setTenderTarget(String tenderTarget) {
        this.tenderTarget = tenderTarget;
    }

    public Date getTenderSdate() {
        return tenderSdate;
    }

    public void setTenderSdate(Date tenderSdate) {
        this.tenderSdate = tenderSdate;
    }

    public Date getTenderEdate() {
        return tenderEdate;
    }

    public void setTenderEdate(Date tenderEdate) {
        this.tenderEdate = tenderEdate;
    }

    public Date getPayEdate() {
        return payEdate;
    }

    public void setPayEdate(Date payEdate) {
        this.payEdate = payEdate;
    }

    public Date getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    public Date getPayDate() {
        return payDate;
    }

    public void setPayDate(Date payDate) {
        this.payDate = payDate;
    }

    public List<RiskTrialRetDto> getRiskTrialRetDto() {
        return riskTrialRetDto;
    }

    public void setRiskTrialRetDto(List<RiskTrialRetDto> riskTrialRetDto) {
        this.riskTrialRetDto = riskTrialRetDto;
    }

    public CfetsEtribdoRiskcalResponse getCfetsEtribdoRiskcalReturn() {
        return cfetsEtribdoRiskcalReturn;
    }

    public void setCfetsEtribdoRiskcalReturn(CfetsEtribdoRiskcalResponse cfetsEtribdoRiskcalReturn) {
        this.cfetsEtribdoRiskcalReturn = cfetsEtribdoRiskcalReturn;
    }

    public String getTotalBiddingNum() {
        return totalBiddingNum;
    }

    public void setTotalBiddingNum(String totalBiddingNum) {
        this.totalBiddingNum = totalBiddingNum;
    }

    public List<CfetsBiddingProductDto> getBiddingProductList() {
        return biddingProductList;
    }

    public void setBiddingProductList(List<CfetsBiddingProductDto> biddingProductList) {
        this.biddingProductList = biddingProductList;
    }

    public String getTotalEtrustId() {
        return totalEtrustId;
    }

    public void setTotalEtrustId(String totalEtrustId) {
        this.totalEtrustId = totalEtrustId;
    }

    public String getSoftSkipMode() {
        return softSkipMode;
    }

    public void setSoftSkipMode(String softSkipMode) {
        this.softSkipMode = softSkipMode;
    }

    public List<CfetsEtrBondFirstBid002Dto> getEtrInvestBondFirstBidDtos() {
        return etrInvestBondFirstBidDtos;
    }

    public void setEtrInvestBondFirstBidDtos(List<CfetsEtrBondFirstBid002Dto> etrInvestBondFirstBidDtos) {
        this.etrInvestBondFirstBidDtos = etrInvestBondFirstBidDtos;
    }

    public String getFinprodAbbr() {
        return finprodAbbr;
    }

    public void setFinprodAbbr(String finprodAbbr) {
        this.finprodAbbr = finprodAbbr;
    }
}
