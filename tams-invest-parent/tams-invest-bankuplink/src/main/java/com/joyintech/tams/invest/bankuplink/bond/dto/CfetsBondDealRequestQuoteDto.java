/*
 * 文件名：CfetsBondDealRequestQuoteDto.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：wangzhuangzhuang
 * 修改时间：2020年5月19日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.bankuplink.bond.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.validation.annotation.NotEmpty;
import com.joyintech.framework.validation.annotation.ValidLogic;
import com.joyintech.tams.common.base.entity.SysUploadFileInfo;
import com.joyintech.tams.invest.bankuplink.bond.validator.CfetsBondDealRequestQuoteDtoValidator;
import com.joyintech.tams.invest.common.risktrial.dto.RiskTrialRetDto;
import com.joyintech.workflow.engine.annotation.PrimaryKey;
import com.joyintech.workflow.engine.dto.ProcessEntity;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 请求报价dto
 * 
 * @author wangzhuangzhuang
 * @version 1.0 2020年5月19日
 * @see CfetsBondDealRequestQuoteDto
 * @since 1.0
 */
//@PageId(value = "CfetsBondDealQuotedPrice003")
@ValidLogic(targetClass = CfetsBondDealRequestQuoteDtoValidator.class)
public class CfetsBondDealRequestQuoteDto extends CfetsBondDealEntrustOrderDto implements ProcessEntity {

    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    private static final long serialVersionUID = -6517441367865558290L;

    /**
     * 报价编号（内部代码）
     */
    @PrimaryKey(PrimaryKey.Field.PK_1)
    private String quoteId;

    /**
     * 交割日期
     */
    @NotEmpty(fieldName = "CfetsBondDealQuotedPrice003.settleDate")
    private Date settleDate;

    /**
     * 生效日期
     */
    private String effDate;

    /**
     * 画面打开模式
     */
    private String quotaPageModel;
    /**
     * 校验类型
     */
    private String checkType;

    /**
     * 对手方列表
     */
    @NotEmpty(fieldName = "CfetsBondDealQuotedPrice003.counterId")
    private List<String> counterIdList;
    /**
     * 对手方信息列表
     */
    private List<CounterInfoDto> counterInfoList;
    /**
     * 明细编号列表
     */
    private List<String> detIdList;
    
    /**
     * 操作类型
     */
    private String operatorType;
    
    /**
     * 页面ID
     */

    private String pageId;
    
    /**
     * 页面元素
     */
    private String elementId;
    
    /**
     * 按钮操作类型
     */
    private String optType;
    
    /**
     * 组合分配信息
     */
    private List<CfetsBondDealRequestQuoteDto> cfetsBondDealRequestQuoteDtoList;
    
    /**
     * 是否多组合
     */
    private String isDzh;


    /**
     * 文件上传dto
     */
    private List<SysUploadFileInfo> fileArray;

    /**
     * 业务编号
     */
    private String businessId;

    /**
     * 交易手续费
     */
    private BigDecimal dealFeeAmt;
    /**
     * 结算服务费
     */
    private BigDecimal  settFeeAmt;

    private BigDecimal ourFpriceAmt;

    private BigDecimal lendinFpriceAmt;

    private BigDecimal saleinFpriceAmt;

    private BigDecimal ourIntAmt;

    private BigDecimal lendinIntAmt;

    private BigDecimal saleinIntAmt;

    private BigDecimal sellAbleNum;

    /**
     * 全价总额
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal fpriceAmt;

    private Date theoreticalExerciseDate;

    /**
     * 报价方式(登记)
     */
    private String quoteStyle;

    private String calculateField;

    private String finprodAbbr;

    private String settleSpreedCode;

    /**
     * 组合分配方式
     */
    private String distribution;

    private BigDecimal unitFprice;

    /**
     * 报价操作主要包含“发起、回复、修改、交谈”
     */
    private String quoteOperatorType;

    private BigDecimal lendinShareAmt;

    private BigDecimal ourShareAmt;
    /**
     *数据已入新头寸，Y：已入
     */
    private String isExistNew;

    @ApiModelProperty(name="softSkipMode",value="软控是否继续？Y：继续",example="Y")
    private String softSkipMode;

    /**
     * 风险试算实体类
     */
    private List<RiskTrialRetDto> riskTrialRetDto;

    public BigDecimal getLendinShareAmt() {
        return lendinShareAmt;
    }

    public void setLendinShareAmt(BigDecimal lendinShareAmt) {
        this.lendinShareAmt = lendinShareAmt;
    }

    public BigDecimal getOurShareAmt() {
        return ourShareAmt;
    }

    public void setOurShareAmt(BigDecimal ourShareAmt) {
        this.ourShareAmt = ourShareAmt;
    }

    @Override
    public String getQuoteId() {
        return quoteId;
    }

    @Override
    public void setQuoteId(String quoteId) {
        this.quoteId = quoteId;
    }

    public BigDecimal getFpriceAmt() {
        return fpriceAmt;
    }

    public void setFpriceAmt(BigDecimal fpriceAmt) {
        this.fpriceAmt = fpriceAmt;
    }

    public BigDecimal getUnitFprice() {
        return unitFprice;
    }

    public void setUnitFprice(BigDecimal unitFprice) {
        this.unitFprice = unitFprice;
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public List<SysUploadFileInfo> getFileArray() {
        return fileArray;
    }

    public void setFileArray(List<SysUploadFileInfo> fileArray) {
        this.fileArray = fileArray;
    }

    /**
     * 获取 settleDate
     * 
     * @return settleDate.
     */
    public Date getSettleDate() {
        return settleDate;
    }

    /**
     * 设置 settleDate
     * 
     * @param settleDate settleDate
     */
    public void setSettleDate(Date settleDate) {
        this.settleDate = settleDate;
    }

    /**
     * {@inheritDoc}
     */
    /*@Override
    @NotEmpty(fieldName = "CfetsBondDealQuotedPrice003.entrustId")
    public String getEntrustId() {
        return super.getEntrustId();
    }*/

    /**
     * {@inheritDoc}
     */
    @Override
//    @NotEmpty(fieldName = "CfetsBondDealQuotedPrice003.portfolioName")
    public String getPortfolioId() {
        return super.getPortfolioId();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    @NotEmpty(fieldName = "CfetsBondDealQuotedPrice003.myTradeId")
    public String getMySideTrader() {
        return super.getMySideTrader();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotEmpty(fieldName = "CfetsBondDealQuotedPrice003.ps")
    public String getPs() {
        return super.getPs();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotEmpty(fieldName = "CfetsBondDealQuotedPrice003.my")
    public String getMySide() {
        return super.getMySide();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotEmpty(fieldName = "CfetsBondDealQuotedPrice003.bond")
    public String getFinprodId() {
        return super.getFinprodId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotEmpty(fieldName = "CfetsBondDealQuotedPrice003.settleSpeed")
    public String getSettleSpreed() {
        return super.getSettleSpreed();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotEmpty(fieldName = "CfetsBondDealQuotedPrice003.bondPrinamt")
    public BigDecimal getAvaNum() {
        return super.getAvaNum();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotEmpty(fieldName = "CfetsBondDealQuotedPrice002.settleMethod")
    public String getSettleMethod() {
        return super.getSettleMethod();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @NotEmpty(fieldName = "CfetsBondDealQuotedPrice002.settleType")
    public String getSettleType() {
        return super.getSettleType();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @NotEmpty(fieldName = "CfetsBondDealQuotedPrice002.settleCcy")
    public String getSettleCcy() {
        return super.getSettleCcy();
    }

    /**
     * 获取 effDate
     * 
     * @return effDate.
     */
    public String getEffDate() {
        return effDate;
    }

    /**
     * 获取 quotaPageModel
     * 
     * @return quotaPageModel.
     */
    public String getQuotaPageModel() {
        return quotaPageModel;
    }

    /**
     * 设置 quotaPageModel
     * 
     * @param quotaPageModel quotaPageModel
     */
    public void setQuotaPageModel(String quotaPageModel) {
        this.quotaPageModel = quotaPageModel;
    }

    /**
     * 设置 effDate
     * 
     * @param effDate effDate
     */
    public void setEffDate(String effDate) {
        this.effDate = effDate;
    }

    /**
     * 获取 counterIdList
     * 
     * @return counterIdList.
     */
    public List<String> getCounterIdList() {
        return counterIdList;
    }

    /**
     * 设置 counterIdList
     * 
     * @param counterIdList counterIdList
     */
    public void setCounterIdList(List<String> counterIdList) {
        this.counterIdList = counterIdList;
    }


    
    /**
     * 获取 counterInfoList
     * @return counterInfoList.
     */
    public List<CounterInfoDto> getCounterInfoList() {
        return counterInfoList;
    }

    
    /**
     * 设置 counterInfoList
     * @param counterInfoList counterInfoList
     */
    public void setCounterInfoList(List<CounterInfoDto> counterInfoList) {
        this.counterInfoList = counterInfoList;
    }

    
    /**
     * 获取 checkType
     * @return checkType.
     */
    public String getCheckType() {
        return checkType;
    }

    
    /**
     * 设置 checkType
     * @param checkType checkType
     */
    public void setCheckType(String checkType) {
        this.checkType = checkType;
    }

    
    /**
     * 获取 detIdList
     * @return detIdList.
     */
    public List<String> getDetIdList() {
        return detIdList;
    }

    
    /**
     * 设置 detIdList
     * @param detIdList detIdList
     */
    public void setDetIdList(List<String> detIdList) {
        this.detIdList = detIdList;
    }


    
    /**
     * 获取 operatorType
     * @return operatorType.
     */
    public String getOperatorType() {
        return operatorType;
    }

    
    /**
     * 设置 operatorType
     * @param operatorType operatorType
     */
    public void setOperatorType(String operatorType) {
        this.operatorType = operatorType;
    }

    /**
     * 获取 settleDate
     * @return settleDate.
     */
    public String getSettleDateStr() {
        return DateUtil.format(this.getSettleDate());
    }

	public String getPageId() {
		return pageId;
	}

	public void setPageId(String pageId) {
		this.pageId = pageId;
	}

	public String getElementId() {
		return elementId;
	}

	public void setElementId(String elementId) {
		this.elementId = elementId;
	}

	public String getOptType() {
		return optType;
	}

	public void setOptType(String optType) {
		this.optType = optType;
	}

	public List<CfetsBondDealRequestQuoteDto> getCfetsBondDealRequestQuoteDtoList() {
		return cfetsBondDealRequestQuoteDtoList;
	}

	public void setCfetsBondDealRequestQuoteDtoList(List<CfetsBondDealRequestQuoteDto> cfetsBondDealRequestQuoteDtoList) {
		this.cfetsBondDealRequestQuoteDtoList = cfetsBondDealRequestQuoteDtoList;
	}

	public String getIsDzh() {
		return isDzh;
	}

	public void setIsDzh(String isDzh) {
		this.isDzh = isDzh;
	}

    /**
     * 可用持仓
     */
	private BigDecimal secManageAvailablePosition;

    public BigDecimal getSecManageAvailablePosition() {
        return secManageAvailablePosition;
    }

    public void setSecManageAvailablePosition(BigDecimal secManageAvailablePosition) {
        this.secManageAvailablePosition = secManageAvailablePosition;
    }

    public BigDecimal getDealFeeAmt() {
        return dealFeeAmt;
    }

    public void setDealFeeAmt(BigDecimal dealFeeAmt) {
        this.dealFeeAmt = dealFeeAmt;
    }

    public BigDecimal getSettFeeAmt() {
        return settFeeAmt;
    }

    public void setSettFeeAmt(BigDecimal settFeeAmt) {
        this.settFeeAmt = settFeeAmt;
    }

    public BigDecimal getOurFpriceAmt() {
        return ourFpriceAmt;
    }

    public void setOurFpriceAmt(BigDecimal ourFpriceAmt) {
        this.ourFpriceAmt = ourFpriceAmt;
    }

    public BigDecimal getLendinFpriceAmt() {
        return lendinFpriceAmt;
    }

    public void setLendinFpriceAmt(BigDecimal lendinFpriceAmt) {
        this.lendinFpriceAmt = lendinFpriceAmt;
    }

    public BigDecimal getSaleinFpriceAmt() {
        return saleinFpriceAmt;
    }

    public void setSaleinFpriceAmt(BigDecimal saleinFpriceAmt) {
        this.saleinFpriceAmt = saleinFpriceAmt;
    }

    public BigDecimal getOurIntAmt() {
        return ourIntAmt;
    }

    public void setOurIntAmt(BigDecimal ourIntAmt) {
        this.ourIntAmt = ourIntAmt;
    }

    public BigDecimal getLendinIntAmt() {
        return lendinIntAmt;
    }

    public void setLendinIntAmt(BigDecimal lendinIntAmt) {
        this.lendinIntAmt = lendinIntAmt;
    }

    public BigDecimal getSaleinIntAmt() {
        return saleinIntAmt;
    }

    public void setSaleinIntAmt(BigDecimal saleinIntAmt) {
        this.saleinIntAmt = saleinIntAmt;
    }

    public BigDecimal getSellAbleNum() {
        return sellAbleNum;
    }

    public void setSellAbleNum(BigDecimal sellAbleNum) {
        this.sellAbleNum = sellAbleNum;
    }

    public Date getTheoreticalExerciseDate() {
        return theoreticalExerciseDate;
    }

    public void setTheoreticalExerciseDate(Date theoreticalExerciseDate) {
        this.theoreticalExerciseDate = theoreticalExerciseDate;
    }

    public String getQuoteStyle() {
        return quoteStyle;
    }

    public void setQuoteStyle(String quoteStyle) {
        this.quoteStyle = quoteStyle;
    }

    @Override
    public String getCalculateField() {
        return calculateField;
    }

    @Override
    public void setCalculateField(String calculateField) {
        this.calculateField = calculateField;
    }

    @Override
    public String getFinprodAbbr() {
        return finprodAbbr;
    }

    @Override
    public void setFinprodAbbr(String finprodAbbr) {
        this.finprodAbbr = finprodAbbr;
    }


    public String getSettleSpreedCode() {
        return settleSpreedCode;
    }

    public void setSettleSpreedCode(String settleSpreedCode) {
        this.settleSpreedCode = settleSpreedCode;
    }

    public String getDistribution() {
        return distribution;
    }

    public void setDistribution(String distribution) {
        this.distribution = distribution;
    }

    public String getQuoteOperatorType() {
        return quoteOperatorType;
    }

    public void setQuoteOperatorType(String quoteOperatorType) {
        this.quoteOperatorType = quoteOperatorType;
    }

    public String getIsExistNew() {
        return isExistNew;
    }

    public void setIsExistNew(String isExistNew) {
        this.isExistNew = isExistNew;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getSoftSkipMode() {
        return softSkipMode;
    }

    public void setSoftSkipMode(String softSkipMode) {
        this.softSkipMode = softSkipMode;
    }

    public List<RiskTrialRetDto> getRiskTrialRetDto() {
        return riskTrialRetDto;
    }

    public void setRiskTrialRetDto(List<RiskTrialRetDto> riskTrialRetDto) {
        this.riskTrialRetDto = riskTrialRetDto;
    }
}
