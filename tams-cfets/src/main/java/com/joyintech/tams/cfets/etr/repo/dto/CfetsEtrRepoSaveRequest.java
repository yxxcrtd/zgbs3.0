/*
 * 文件名：EtribRepoInfo003Dto.java
 * 版权：Copyright 2020-2022 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管系统V2.0
 * 修改人：王波
 * 修改时间：2021年01月20日
 * 修改内容：新建
 * 系统名称：理财资管系统V2.0
 */

package com.joyintech.tams.cfets.etr.repo.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.tams.common.base.entity.EtrEntrust;
import com.joyintech.tams.component.business.trade.dto.BussEtrInvestPersonInfoDto;
import com.joyintech.tams.invest.common.risktrial.dto.RiskTrialRetDto;
import com.joyintech.workflow.engine.dto.ProcessEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@ApiModel(value="保存提交dto",description="保存提交dto")
public class CfetsEtrRepoSaveRequest extends EtrEntrust implements ProcessEntity {

    /**
     * 按钮id
     */
    private String elementId;

    /**
     * 操作类型
     */
    private String operateType;

    /**
     * 市场代码
     */
    private String finprodMarketId;


    /**
     * 劵面总额
     */
    private BigDecimal tradeAmt;
    
    /**
     * 质押券库
     */
    @ApiModelProperty(name="zyqk",value="质押券库")
    private String zyqk;
    
    
    private long branch;
    @ApiModelProperty(name="pledgeStockId",value="质押券库id")
    private String pledgeStockId;

    /**
     * 清算速度
     */
    @ApiModelProperty(name="settleSpreed",value="清算速度")
    private String settleSpreed;

    /**
     * 交割日期
     */
    private Date settleDate;
    
    /**
     * 回购期限
     */
    @ApiModelProperty(name="repoTerm",value="回购期限")
    private BigDecimal repoTerm;
    /**
     * 期限品种
     */
    @ApiModelProperty(name="termType",value="期限品种")
    private String termType;

    /**
     * 证券托管户
     */
    private String secAcctId;

    @ApiModelProperty(name="etrInvestRepoInfoDtos",value="组合明细")
    List<CfetsEtrRepoSaveRequest> etrInvestRepoInfoDtos;
    /**
     * 汇总券面总额
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    @ApiModelProperty(name="totalAmount",value="汇总券面总额")
    private BigDecimal totalAmount;

    @ApiModelProperty(name="creditIsContinue",value="授信软控是否继续？Y：继续",example="Y")
    private String creditIsContinue;
    /**
     * 本方券券面总额
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal entrustOwnNum;
    /**
     * 融入券/卖空购回券面总额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal entrustIntegrateNum;

    /**
     * 风险试算实体类
     */
    private List<RiskTrialRetDto> riskTrialRetDto;

    /**
     * 多组合汇总指令ID
     */
    private String totalEtrustId;


    /**
     * 资产二级分类（原资产一级分类，委托页面的债券一级分类，修改：王贵奇，2019/9/27）
     */
    private String assetType1;

    /**
     * 投资人员信息DTO
     */
    @Valid
    private BussEtrInvestPersonInfoDto etrInvestPersonInfo;

    /**
     * 委托关闭方式
     */
    private String CloseWay;

    /**
     * 接收状态
     */
    private String recStatus;



    /**
     * 选中的数据
     */
    private transient List<CfetsEtrRepoSaveRequest> selectedItem;

    /**
     * 质押券信息列表明细
     */
//    @ApiModelProperty(name="pledgeBondTotalListDetailed",value="质押券信息列表明细")
    private List<CfetsPledgeBondInfoDto> pledgeBondTotalListDetailed;



    //add by chenjiaxing 多组合本方交易员字段未添加 20210914 start
    /**
     * 本方交易员
     */
    private String trader;

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

    public String getFinprodMarketId() {
        return finprodMarketId;
    }

    public void setFinprodMarketId(String finprodMarketId) {
        this.finprodMarketId = finprodMarketId;
    }

    public BigDecimal getTradeAmt() {
        return tradeAmt;
    }

    public void setTradeAmt(BigDecimal tradeAmt) {
        this.tradeAmt = tradeAmt;
    }

    public String getZyqk() {
        return zyqk;
    }

    public void setZyqk(String zyqk) {
        this.zyqk = zyqk;
    }

    public long getBranch() {
        return branch;
    }

    public void setBranch(long branch) {
        this.branch = branch;
    }

    public String getPledgeStockId() {
        return pledgeStockId;
    }

    public void setPledgeStockId(String pledgeStockId) {
        this.pledgeStockId = pledgeStockId;
    }

    public String getSettleSpreed() {
        return settleSpreed;
    }

    public void setSettleSpreed(String settleSpreed) {
        this.settleSpreed = settleSpreed;
    }

    public Date getSettleDate() {
        return settleDate;
    }

    public void setSettleDate(Date settleDate) {
        this.settleDate = settleDate;
    }

    public BigDecimal getRepoTerm() {
        return repoTerm;
    }

    public void setRepoTerm(BigDecimal repoTerm) {
        this.repoTerm = repoTerm;
    }

    public String getTermType() {
        return termType;
    }

    public void setTermType(String termType) {
        this.termType = termType;
    }

    public String getSecAcctId() {
        return secAcctId;
    }

    public void setSecAcctId(String secAcctId) {
        this.secAcctId = secAcctId;
    }

    public List<CfetsEtrRepoSaveRequest> getEtrInvestRepoInfoDtos() {
        return etrInvestRepoInfoDtos;
    }

    public void setEtrInvestRepoInfoDtos(List<CfetsEtrRepoSaveRequest> etrInvestRepoInfoDtos) {
        this.etrInvestRepoInfoDtos = etrInvestRepoInfoDtos;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getEntrustOwnNum() {
        return entrustOwnNum;
    }

    public void setEntrustOwnNum(BigDecimal entrustOwnNum) {
        this.entrustOwnNum = entrustOwnNum;
    }

    public BigDecimal getEntrustIntegrateNum() {
        return entrustIntegrateNum;
    }

    public void setEntrustIntegrateNum(BigDecimal entrustIntegrateNum) {
        this.entrustIntegrateNum = entrustIntegrateNum;
    }

    public List<RiskTrialRetDto> getRiskTrialRetDto() {
        return riskTrialRetDto;
    }

    public void setRiskTrialRetDto(List<RiskTrialRetDto> riskTrialRetDto) {
        this.riskTrialRetDto = riskTrialRetDto;
    }

    public String getTotalEtrustId() {
        return totalEtrustId;
    }

    public void setTotalEtrustId(String totalEtrustId) {
        this.totalEtrustId = totalEtrustId;
    }

    public String getAssetType1() {
        return assetType1;
    }

    public void setAssetType1(String assetType1) {
        this.assetType1 = assetType1;
    }

    public BussEtrInvestPersonInfoDto getEtrInvestPersonInfo() {
        return etrInvestPersonInfo;
    }

    public void setEtrInvestPersonInfo(BussEtrInvestPersonInfoDto etrInvestPersonInfo) {
        this.etrInvestPersonInfo = etrInvestPersonInfo;
    }

    public String getCloseWay() {
        return CloseWay;
    }

    public void setCloseWay(String closeWay) {
        CloseWay = closeWay;
    }

    public String getRecStatus() {
        return recStatus;
    }

    public void setRecStatus(String recStatus) {
        this.recStatus = recStatus;
    }

    public List<CfetsEtrRepoSaveRequest> getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(List<CfetsEtrRepoSaveRequest> selectedItem) {
        this.selectedItem = selectedItem;
    }

    public String getTrader() {
        return trader;
    }

    public void setTrader(String trader) {
        this.trader = trader;
    }

    public String getCreditIsContinue() {
        return creditIsContinue;
    }

    public void setCreditIsContinue(String creditIsContinue) {
        this.creditIsContinue = creditIsContinue;
    }

    public List<CfetsPledgeBondInfoDto> getPledgeBondTotalListDetailed() {
        return pledgeBondTotalListDetailed;
    }

    public void setPledgeBondTotalListDetailed(List<CfetsPledgeBondInfoDto> pledgeBondTotalListDetailed) {
        this.pledgeBondTotalListDetailed = pledgeBondTotalListDetailed;
    }
}
