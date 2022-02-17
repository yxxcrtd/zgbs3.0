/*
 * 文件名：TrdPlanInvestTradeDto.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：fanshuanghu
 * 修改时间：2018年11月22日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.valuation.trademanage.trdplanvaluationtrade.dto;

import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.framework.validation.annotation.NotEmpty;
import com.joyintech.framework.validation.annotation.ValidLogic;
import com.joyintech.framework.validation.groups.ValidateGroupDelete;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.valuation.trademanage.trdplanvaluationtrade.validate.TrdPlanValuationTradeDtoValidator;
import com.joyintech.workflow.engine.annotation.PrimaryKey;
import com.joyintech.workflow.engine.annotation.PrimaryKey.Field;
import com.joyintech.workflow.engine.dto.ProcessEntity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 计划类（利率型）投资交易
 * @author fanshuanghu
 * @version 1.0 2018年11月22日
 * @see
 * @since 1.0
 */
@PageId("TrdPlanValuationTrade002")
@ValidLogic(targetClass = TrdPlanValuationTradeDtoValidator.class, groups = {ValidateGroupEdit.class,
    ValidateGroupDelete.class})
public class TrdPlanValuationTradeDto extends WflTrdProductDeal implements ProcessEntity {

    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    private static final long serialVersionUID = 5309538894816479429L;

    /**
     * 产品代码
     */
    private String portfolioId;

    /**
     * 产品名称
     */
    private String portfolioName;

    /**
     * 是否当日到账
     */
    private String  todayArriveFlag;

    /**
     * 交易份额
     */
    private BigDecimal nums;


    /**
     * 确认金额
     */
    private BigDecimal confirmTradeAmt;

    /**
     * (确认)交易份额
     */
    private BigDecimal confirmNums;

    /**
     * 利息确认金额
     */
    private BigDecimal confirmIntAmt;

    /**
     * 返款金额
     */
    private BigDecimal refundAmount;

    /**
     * 利息返还金额
     */
    private BigDecimal rateRefundAmount;

    /**
     * 单位净值
     */
    private BigDecimal unitValue;

    /**
     * 投资顾问费
     */
    private BigDecimal advFeeOut;
    /**
     * 其他费用
     */
    private BigDecimal feeAmtQT;

    /**
     * 结算金额
     */
    private BigDecimal mdateSettleAmt;

    /**
     * 到账金额
     */
    private BigDecimal settleAmt;

    /**
     * 资金账户
     */
    private String trdAcctId;

    /**
     * 外部交易编号
     */
    private String outTradeId;


    /**
     * 金融产品名称
     */
    private String finprodName;
    /**
     * 操作类型
     */
    private String operateType;
    /**
     * 市场机构代码
     */
    private String finprodMarketId;
    /**
     * elementId
     */
    private String elementId;
    /**
     * 计划类实体类
     */
    private List<TrdPlanValuationTradeDto> selectedItem;

    /**
     * 收益类型
     */
    private String profitType;//01净值型，02预期收益型,03货币型



    /**
     * 撤销原因
     */
    private String cancelRemark;
    /**
     * pageModel
     */
    private String pageModel;


    /**
     * 获取 pageModel
     * @return pageModel.
     */
    public String getPageModel() {
        return pageModel;
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
     * 设置 pageModel
     * @param pageModel pageModel
     */
    public void setPageModel(String pageModel) {
        this.pageModel = pageModel;
    }
    /**
     * 金融产品类型
     * {@inheritDoc}
     */
    @Override
    public String getFinprodType(){
        return super.getFinprodType();
    }

    @Override
    @NotEmpty(fieldName="TrdPlanValuationTrade001.tradeDate",groups = {ValidateGroupEdit.class})
    public Date getTradeDate(){
        return super.getTradeDate();
    }




    /**
     * 获取 selectedItem
     * @return selectedItem.
     */
    public List<TrdPlanValuationTradeDto> getSelectedItem() {
        return selectedItem;
    }



    /**
     * 设置 selectedItem
     * @param selectedItem selectedItem
     */
    public void setSelectedItem(List<TrdPlanValuationTradeDto> selectedItem) {
        this.selectedItem = selectedItem;
    }
    /**
     * 设置主键
     * {@inheritDoc}
     */
    @Override
    @PrimaryKey(Field.PK_1)
    public String getTradeId(){
        return super.getTradeId();
    }
    /**
     * 业务类型
     * {@inheritDoc}
     */
    @Override
    @PrimaryKey(Field.PK_2)
    public String getBusiType(){
        return super.getBusiType();
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

    public String getTrdAcctId() {
        return trdAcctId;
    }

    public void setTrdAcctId(String trdAcctId) {
        this.trdAcctId = trdAcctId;
    }

    @Override
    public String getCancelRemark() {
        return cancelRemark;
    }

    @Override
    public void setCancelRemark(String cancelRemark) {
        this.cancelRemark = cancelRemark;
    }

    /**
     * 获取 operatType
     * @return operatType.
     */
    public String getOperateType() {
        return operateType;
    }

    /**
     * 设置 operatType
     * @param operateType operateType
     */
    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }

    /**
     * 币种字符串
     * @return 币种
     * @see
     * @since 1.0
     */
    public String getCcyStr(){
        return SysDataUtil.getNameFromDataDict(DictCode.币种, super.getCcy());
    }
    /**
     * 币种
     * {@inheritDoc}
     */
    @Override
    @NotEmpty(fieldName="TrdPlanValuationTrade001.ccy",groups = {ValidateGroupEdit.class})
    public String getCcy(){
        return super.getCcy();
    }
    /**
     * 交易方向
     * {@inheritDoc}
     */
    @NotEmpty(fieldName="TrdPlanValuationTrade001.ps",groups = {ValidateGroupEdit.class})
    public String getPs(){
        return super.getPs();
    }
    /**
     * 交易方向字符串
     * @return 交易方向
     * @see
     * @since 1.0
     */
    public String getPsStr(){
        return SysDataUtil.getNameFromDataDict(DictCode.交易方向, super.getPs());
    }


    /**
     * 获取 portfolioId
     * @return portfolioId.
     */
    @NotEmpty(fieldName="TrdPlanValuationTrade001.portfolioId",groups = {ValidateGroupEdit.class})
    public String getPortfolioId() {
        return portfolioId;
    }

    /**
     * 设置 portfolioId
     * @param portfolioId portfolioId
     */
    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId;
    }

    /**
     * 获取 portfolioName
     * @return portfolioName.
     */
    @NotEmpty(fieldName="TrdPlanValuationTrade001.portfolioName",groups = {ValidateGroupEdit.class})
    public String getPortfolioName() {
        return portfolioName;
    }

    /**
     * 设置 portfolioNamefinprodMarketId
     * @param portfolioName portfolioName
     */
    public void setPortfolioName(String portfolioName) {
        this.portfolioName = portfolioName;
    }


    /**
     * 获取 finprodName
     * @return finprodName.
     */
    @NotEmpty(fieldName="TrdPlanValuationTrade001.finprodName",groups = {ValidateGroupEdit.class})
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


    public String getOutTradeId() {
        return outTradeId;
    }

    public void setOutTradeId(String outTradeId) {
        this.outTradeId = outTradeId;
    }

    @NotEmpty(fieldName="TrdPlanValuationTrade002.todayArriveFlag",groups = {ValidateGroupEdit.class})
    public String getTodayArriveFlag() {
        return todayArriveFlag;
    }

    public void setTodayArriveFlag(String todayArriveFlag) {
        this.todayArriveFlag = todayArriveFlag;
    }

//    @NotEmpty(fieldName="TrdPlanValuationTrade002.nums",groups = {ValidateGroupEdit.class})
    public BigDecimal getNums() {
        return nums;
    }

    public void setNums(BigDecimal nums) {
        this.nums = nums;
    }

//    @NotEmpty(fieldName="TrdPlanValuationTrade002.confirmTradeAmt",groups = {ValidateGroupEdit.class})
    public BigDecimal getConfirmTradeAmt() {
        return confirmTradeAmt;
    }

    public void setConfirmTradeAmt(BigDecimal confirmTradeAmt) {
        this.confirmTradeAmt = confirmTradeAmt;
    }

//    @NotEmpty(fieldName="TrdPlanValuationTrade002.confirmNums",groups = {ValidateGroupEdit.class})
    public BigDecimal getConfirmNums() {
        return confirmNums;
    }

    public void setConfirmNums(BigDecimal confirmNums) {
        this.confirmNums = confirmNums;
    }

    @NotEmpty(fieldName="TrdPlanValuationTrade002.confirmIntAmt",groups = {ValidateGroupEdit.class})
    public BigDecimal getConfirmIntAmt() {
        return confirmIntAmt;
    }

    public void setConfirmIntAmt(BigDecimal confirmIntAmt) {
        this.confirmIntAmt = confirmIntAmt;
    }
    @NotEmpty(fieldName="TrdPlanValuationTrade002.refundAmount",groups = {ValidateGroupEdit.class})
    public BigDecimal getRefundAmount() {
        return refundAmount;
    }


    public void setRefundAmount(BigDecimal refundAmount) {
        this.refundAmount = refundAmount;
    }

    @NotEmpty(fieldName="TrdPlanValuationTrade002.rateRefundAmount",groups = {ValidateGroupEdit.class})
    public BigDecimal getRateRefundAmount() {
        return rateRefundAmount;
    }

    public void setRateRefundAmount(BigDecimal rateRefundAmount) {
        this.rateRefundAmount = rateRefundAmount;
    }

//    @NotEmpty(fieldName="TrdPlanValuationTrade002.unitValue",groups = {ValidateGroupEdit.class})
    public BigDecimal getUnitValue() {
        return unitValue;
    }

    public void setUnitValue(BigDecimal unitValue) {
        this.unitValue = unitValue;
    }

    @NotEmpty(fieldName="TrdPlanValuationTrade002.advFeeOut",groups = {ValidateGroupEdit.class})
    public BigDecimal getAdvFeeOut() {
        return advFeeOut;
    }

    public void setAdvFeeOut(BigDecimal advFeeOut) {
        this.advFeeOut = advFeeOut;
    }

    @NotEmpty(fieldName="TrdPlanValuationTrade002.feeAmtQT",groups = {ValidateGroupEdit.class})
    public BigDecimal getFeeAmtQT() {
        return feeAmtQT;
    }

    public void setFeeAmtQT(BigDecimal feeAmtQT) {
        this.feeAmtQT = feeAmtQT;
    }

    @NotEmpty(fieldName="TrdPlanValuationTrade002.mdateSettleAmt",groups = {ValidateGroupEdit.class})
    public BigDecimal getMdateSettleAmt() {
        return mdateSettleAmt;
    }

    public void setMdateSettleAmt(BigDecimal mdateSettleAmt) {
        this.mdateSettleAmt = mdateSettleAmt;
    }

//    @NotEmpty(fieldName="TrdPlanValuationTrade002.settleAmt",groups = {ValidateGroupEdit.class})
    public BigDecimal getSettleAmt() {
        return settleAmt;
    }

    public void setSettleAmt(BigDecimal settleAmt) {
        this.settleAmt = settleAmt;
    }

    //交易金额
    @Override
    @NotEmpty(fieldName="TrdPlanValuationTrade001.tradeAmt",groups = {ValidateGroupEdit.class})
    public BigDecimal getTradeAmt() {
        return super.getTradeAmt();
    }
    //交易费用
    @Override
    @NotEmpty(fieldName="TrdPlanValuationTrade002.feeAmt",groups = {ValidateGroupEdit.class})
    public BigDecimal getFeeAmt() {
        return super.getFeeAmt();
    }

    //资产代码
    @Override
    @NotEmpty(fieldName="TrdPlanValuationTrade001.finprodId",groups = {ValidateGroupEdit.class})
    public String getFinprodId() {
        return super.getFinprodId();
    }

    public String getProfitType() {
        return profitType;
    }


    public void setProfitType(String profitType) {
        this.profitType = profitType;
    }
    public String getTradeDateStr() {
        String tradeDateStr="";
        if(super.getTradeDate() !=null){
            tradeDateStr= DateUtil.format(super.getTradeDate());
        }
        return tradeDateStr;
    }



}

