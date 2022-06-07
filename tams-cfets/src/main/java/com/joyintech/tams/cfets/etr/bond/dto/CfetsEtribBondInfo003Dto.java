/*
 * 文件名：EtribBondInfo003Dto.java
 * 版权：Copyright 2020-2022 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管系统V2.0
 * 修改人：王波
 * 修改时间：2021年01月20日
 * 修改内容：新建
 * 系统名称：理财资管系统V2.0
 */

package com.joyintech.tams.cfets.etr.bond.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.tams.common.base.entity.EtrEntrust;
import com.joyintech.tams.common.base.entity.SysUploadFileInfo;
import com.joyintech.tams.component.business.trade.dto.BussEtrInvestPersonInfoDto;
import com.joyintech.tams.invest.common.risktrial.dto.RiskTrialRetDto;
import com.joyintech.workflow.engine.annotation.PrimaryKey;
import com.joyintech.workflow.engine.dto.ProcessEntity;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author 王波
 * @date 2021年01月20日
 * @see CfetsEtribBondInfo003Dto
 * @since 1.0
 */
public class CfetsEtribBondInfo003Dto extends EtrEntrust implements ProcessEntity {

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
     * 计算方式
     */
    private String calculateField;

    /**
     * 多指令导入
     */
    private boolean importFlow;

    /**
     * 劵面总额
     */
    private BigDecimal tradeAmt;

    /**
     * 清算速度
     */
    private String settleSpeed;

    /**
     * 交割日期
     */
    private Date settleDate;

    /**
     * 全价
     */
    private BigDecimal fpriceAmt;

    private BigDecimal matureYield;

    /**
     * 资产可用持仓
     */
    private BigDecimal assetAvailablePosition;

    /**
     * 强制完成额度
     */
    private BigDecimal forcedFinishAmt;
    /**
     * 资产起息日
     */
    private Date finvdate;
    /**
     * 资产到期日
     */
    private Date finmdate;

    /**
     * 证券托管户
     */
    private String secAcctId;
    /**
     * 交易方向
     */
    private String ps;
    /**
     * 资产代码
     */
    private String finprodId;
    /**
     * 价格模式
     */
    private String priceModel;

    List<CfetsEtribBondInfo003Dto> etrInvestBondInfoDtos;
    /**
     * 资产类型
     */
    private String assetType;

    /**
     * 资产4级分类
     */
    private String assetType11;

    /**
     * 文件上传dto
     */
    private List<SysUploadFileInfo> fileArray;

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
    @PrimaryKey(value= PrimaryKey.Field.PK_1)
    private String totalEtrustId;

    /**
     * 画面文件上传用的ID
     */
    private String businessId;

    /**
     * 资产二级分类（原资产一级分类，委托页面的债券一级分类，修改：王贵奇，2019/9/27）
     */
    private String assetType1;

    /**
     * 组合系列
     */
    private String portfolioSeries;
    /**
     * 指令日期
     */
    private Date etrDate;
    /**
     * 备注
     */
    private String remark;

    /**
     * 组合分配
     */
    List<CfetsBussCombDistribtDto> bussCombDistribtList;
    /**
     * 利息总额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal intAmt;

    /**
     * 单位利息
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal unitInt;

    /**
     * 单位全价
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal unitFprice;
    /**
     * 计算标志
     */
    private String calflag;

    @ApiModelProperty(name="softSkipMode",value="软控是否继续？Y：继续",example="Y")
    private String softSkipMode;

    /**
     * 资产简称
     */
    private String finprodAbbr;

    /**
     * 资产简称
     */
  //  private Boolean submitFlag;

    private String systemAct;

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public String getAssetType1() {
        return assetType1;
    }

    public void setAssetType1(String assetType1) {
        this.assetType1 = assetType1;
    }

    public boolean isImportFlow() {
        return importFlow;
    }

    public void setImportFlow(boolean importFlow) {
        this.importFlow = importFlow;
    }



    public String getTotalEtrustId() {
        return totalEtrustId;
    }

    public void setTotalEtrustId(String totalEtrustId) {
        this.totalEtrustId = totalEtrustId;
    }

    public List<RiskTrialRetDto> getRiskTrialRetDto() {
        return riskTrialRetDto;
    }

    public void setRiskTrialRetDto(List<RiskTrialRetDto> riskTrialRetDto) {
        this.riskTrialRetDto = riskTrialRetDto;
    }

    public Date getSettleDate() {
        return settleDate;
    }

    public void setSettleDate(Date settleDate) {
        this.settleDate = settleDate;
    }

    public String getElementId() {
        return elementId;
    }

    public void setElementId(String elementId) {
        this.elementId = elementId;
    }

    public BigDecimal getEntrustIntegrateNum() {
        return entrustIntegrateNum;
    }

    public void setEntrustIntegrateNum(BigDecimal entrustIntegrateNum) {
        this.entrustIntegrateNum = entrustIntegrateNum;
    }

    public BigDecimal getEntrustOwnNum() {
        return entrustOwnNum;
    }

    public void setEntrustOwnNum(BigDecimal entrustOwnNum) {
        this.entrustOwnNum = entrustOwnNum;
    }

    public List<SysUploadFileInfo> getFileArray() {
        return fileArray;
    }

    public void setFileArray(List<SysUploadFileInfo> fileArray) {
        this.fileArray = fileArray;
    }

    public List<CfetsEtribBondInfo003Dto> getEtrInvestBondInfoDtos() {
        return etrInvestBondInfoDtos;
    }

    public void setEtrInvestBondInfoDtos(List<CfetsEtribBondInfo003Dto> etrInvestBondInfoDtos) {
        this.etrInvestBondInfoDtos = etrInvestBondInfoDtos;
    }

    public String getSecAcctId() {
        return secAcctId;
    }

    public void setSecAcctId(String secAcctId) {
        this.secAcctId = secAcctId;
    }

    /**
     * 投资人员信息DTO
     */
    @Valid
    private BussEtrInvestPersonInfoDto etrInvestPersonInfo;

    public BussEtrInvestPersonInfoDto getEtrInvestPersonInfo() {
        return etrInvestPersonInfo;
    }

    public void setEtrInvestPersonInfo(BussEtrInvestPersonInfoDto etrInvestPersonInfo) {
        this.etrInvestPersonInfo = etrInvestPersonInfo;
    }

    public Date getFinvdate() {
        return finvdate;
    }

    public void setFinvdate(Date finvdate) {
        this.finvdate = finvdate;
    }

    public Date getFinmdate() {
        return finmdate;
    }

    public void setFinmdate(Date finmdate) {
        this.finmdate = finmdate;
    }

    public BigDecimal getAssetAvailablePosition() {
        return assetAvailablePosition;
    }

    public void setAssetAvailablePosition(BigDecimal assetAvailablePosition) {
        this.assetAvailablePosition = assetAvailablePosition;
    }

    /**
     * 委托关闭方式
     */
    private String CloseWay;

    /**
     * 接收状态
     */
    private String recStatus;

    public String getRecStatus() {
        return recStatus;
    }

    public void setRecStatus(String recStatus) {
        this.recStatus = recStatus;
    }

    public String getCloseWay() {
        return CloseWay;
    }

    public void setCloseWay(String closeWay) {
        CloseWay = closeWay;
    }

    /**
     * 选中的数据
     */
    private transient List<CfetsEtribBondInfo003Dto> selectedItem;

    public List<CfetsEtribBondInfo003Dto> getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(List<CfetsEtribBondInfo003Dto> selectedItem) {
        this.selectedItem = selectedItem;
    }

    /**
     * 行权收益率
     */
    private BigDecimal exerYield;


    @ApiModelProperty(name="priceAmt",value="净价总额",example="1")
    private BigDecimal priceAmt;


    @ApiModelProperty(name="unitIntAmt",value="应计利息总额",example="1")
    private BigDecimal unitIntAmt;


    @ApiModelProperty(name="unitFpriceAmt",value="全价总额",example="1")
    private BigDecimal unitFpriceAmt;

    @ApiModelProperty(name="entrustOwnInt",value="本方券应计利息总额",example="11")
    private BigDecimal entrustOwnInt;

    @ApiModelProperty(name="entrustIntegrateInt",value="卖空购回应计利息总额",example="11")
    private BigDecimal entrustIntegrateInt;

    @ApiModelProperty(name="entrustOwnUnitFpriceAmt",value="本方券全价总额",example="11")
    private BigDecimal entrustOwnUnitFpriceAmt;

    @ApiModelProperty(name="entrustIntegrateUnitFpriceAmt",value="卖空购回全价总额",example="11")
    private BigDecimal entrustIntegrateUnitFpriceAmt;

    @ApiModelProperty(name="transmitTime",value="指令下达时间",example="11")
    private Date transmitTime;

    private String isRight;

    private String profitType;/*收益方式*/

    public String getOperateType() {
        return operateType;
    }

    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }

    public BigDecimal getExerYield() {
        return exerYield;
    }

    public void setExerYield(BigDecimal exerYield) {
        this.exerYield = exerYield;
    }

    public BigDecimal getMatureYield() {
        return matureYield;
    }

    public void setMatureYield(BigDecimal matureYield) {
        this.matureYield = matureYield;
    }

    public BigDecimal getFpriceAmt() {
        return fpriceAmt;
    }

    public void setFpriceAmt(BigDecimal fpriceAmt) {
        this.fpriceAmt = fpriceAmt;
    }

    public String getSettleSpeed() {
        return settleSpeed;
    }

    public void setSettleSpeed(String settleSpeed) {
        this.settleSpeed = settleSpeed;
    }

    public BigDecimal getTradeAmt() {
        return tradeAmt;
    }

    public void setTradeAmt(BigDecimal tradeAmt) {
        this.tradeAmt = tradeAmt;
    }

    public String getFinprodMarketId() {
        return finprodMarketId;
    }

    public void setFinprodMarketId(String finprodMarketId) {
        this.finprodMarketId = finprodMarketId;
    }

    private String parentPortfolioId;


    public String getParentPortfolioId() {
        return parentPortfolioId;
    }

    public void setParentPortfolioId(String parentPortfolioId) {
        this.parentPortfolioId = parentPortfolioId;
    }

	public String getCalculateField() {
		return calculateField;
	}

	public void setCalculateField(String calculateField) {
		this.calculateField = calculateField;
	}

    public String getPortfolioSeries() {
        return portfolioSeries;
    }

    public void setPortfolioSeries(String portfolioSeries) {
        this.portfolioSeries = portfolioSeries;
    }

    public BigDecimal getIntAmt() {
        return intAmt;
    }

    public void setIntAmt(BigDecimal intAmt) {
        this.intAmt = intAmt;
    }

    public BigDecimal getUnitInt() {
        return unitInt;
    }

    public void setUnitInt(BigDecimal unitInt) {
        this.unitInt = unitInt;
    }

    public BigDecimal getUnitFprice() {
        return unitFprice;
    }

    public void setUnitFprice(BigDecimal unitFprice) {
        this.unitFprice = unitFprice;
    }

    public String getCalflag() {
        return calflag;
    }

    public void setCalflag(String calflag) {
        this.calflag = calflag;
    }

    @Override
    public String getPs() {
        return ps;
    }

    @Override
    public void setPs(String ps) {
        this.ps = ps;
    }

    @Override
    public String getFinprodId() {
        return finprodId;
    }

    @Override
    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
    }

    @Override
    public String getPriceModel() {
        return priceModel;
    }

    @Override
    public void setPriceModel(String priceModel) {
        this.priceModel = priceModel;
    }

    public Date getEtrDate() {
        return etrDate;
    }

    public void setEtrDate(Date etrDate) {
        this.etrDate = etrDate;
    }

    @Override
    public String getRemark() {
        return remark;
    }

    @Override
    public void setRemark(String remark) {
        this.remark = remark;
    }
    public List<CfetsBussCombDistribtDto> getBussCombDistribtList() {
        return bussCombDistribtList;
    }

    public void setBussCombDistribtList(List<CfetsBussCombDistribtDto> bussCombDistribtList) {
        this.bussCombDistribtList = bussCombDistribtList;
    }

    public String getSoftSkipMode() {
        return softSkipMode;
    }

    public void setSoftSkipMode(String softSkipMode) {
        this.softSkipMode = softSkipMode;
    }

    public BigDecimal getPriceAmt() {
        return priceAmt;
    }

    public void setPriceAmt(BigDecimal priceAmt) {
        this.priceAmt = priceAmt;
    }

    public BigDecimal getUnitIntAmt() {
        return unitIntAmt;
    }

    public void setUnitIntAmt(BigDecimal unitIntAmt) {
        this.unitIntAmt = unitIntAmt;
    }

    public BigDecimal getUnitFpriceAmt() {
        return unitFpriceAmt;
    }

    public void setUnitFpriceAmt(BigDecimal unitFpriceAmt) {
        this.unitFpriceAmt = unitFpriceAmt;
    }

    public BigDecimal getEntrustOwnInt() {
        return entrustOwnInt;
    }

    public void setEntrustOwnInt(BigDecimal entrustOwnInt) {
        this.entrustOwnInt = entrustOwnInt;
    }

    public BigDecimal getEntrustIntegrateInt() {
        return entrustIntegrateInt;
    }

    public void setEntrustIntegrateInt(BigDecimal entrustIntegrateInt) {
        this.entrustIntegrateInt = entrustIntegrateInt;
    }

    public BigDecimal getEntrustOwnUnitFpriceAmt() {
        return entrustOwnUnitFpriceAmt;
    }

    public void setEntrustOwnUnitFpriceAmt(BigDecimal entrustOwnUnitFpriceAmt) {
        this.entrustOwnUnitFpriceAmt = entrustOwnUnitFpriceAmt;
    }

    public BigDecimal getEntrustIntegrateUnitFpriceAmt() {
        return entrustIntegrateUnitFpriceAmt;
    }

    public void setEntrustIntegrateUnitFpriceAmt(BigDecimal entrustIntegrateUnitFpriceAmt) {
        this.entrustIntegrateUnitFpriceAmt = entrustIntegrateUnitFpriceAmt;
    }

    public Date getTransmitTime() {
        return transmitTime;
    }

    public void setTransmitTime(Date transmitTime) {
        this.transmitTime = transmitTime;
    }

    public String getAssetType() {
        return assetType;
    }

    public void setAssetType(String assetType) {
        this.assetType = assetType;
    }

    public String getFinprodAbbr() {
        return finprodAbbr;
    }

    public void setFinprodAbbr(String finprodAbbr) {
        this.finprodAbbr = finprodAbbr;
    }


    public String getSystemAct() {
        return systemAct;
    }

    public void setSystemAct(String systemAct) {
        this.systemAct = systemAct;
    }

    public String getIsRight() {
        return isRight;
    }

    public void setIsRight(String isRight) {
        this.isRight = isRight;
    }

    public String getProfitType() {
        return profitType;
    }

    public void setProfitType(String profitType) {
        this.profitType = profitType;
    }

    public String getAssetType11() {
        return assetType11;
    }

    public void setAssetType11(String assetType11) {
        this.assetType11 = assetType11;
    }

    public BigDecimal getForcedFinishAmt() {
        return forcedFinishAmt;
    }

    public void setForcedFinishAmt(BigDecimal forcedFinishAmt) {
        this.forcedFinishAmt = forcedFinishAmt;
    }
}
