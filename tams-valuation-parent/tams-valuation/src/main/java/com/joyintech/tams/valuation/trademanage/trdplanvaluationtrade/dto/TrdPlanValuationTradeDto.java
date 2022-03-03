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
import com.joyintech.tams.valuation.trademanage.trdfundvaluationtrade.dto.TrdFundValuationTradeDto;
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
     * 交易份额
     */
    private BigDecimal nums;

    /**
     * 结算日期
     */
    private Date settleDate;

    /**
     * (确认)交易份额
     */
    private BigDecimal confirmNums;

    /**
     * 待结转份额
     */
    private BigDecimal carryAmt;

    /**
     * 结算金额
     */
    private BigDecimal mdateSettleAmt;

    /**
     * 资金账户
     */
    private String trdAcctId;

    /**
     * 外部
     */
    private String outTradeId;


    /**
     * 交易场所
     */
    private String tradeMarket;
    /**
     * 交易场所String
     */
    private String tradeMarketStr;

    /**
     * 交易信息-投资顾问费
     */
    private BigDecimal advFeeOut;

    /**
     * 交易信息-其他费用
     */
    private BigDecimal feeAmtQT;


    /**
     * 金融产品名称
     */
    private String finprodName;


    /**
     * 认购信息-申请日期
     */
    private Date rgTradeDate;

    /**
     * 认购信息-结算日期
     */
    private Date rgSettleDate;

    /**
     * 认购信息-申请金额
     */
    private BigDecimal rgTradeAmt;

    /**
     * 认购信息-资金账户
     */
    private String rgTrdAcctId;

    /**
     * 申请信息-申请日期
     */
    private Date sqTradeDate;

    /**
     * 申请信息-结算日期
     */
    private Date sqSettleDate;
    /**
     * 申请信息-申请金额
     */
    private BigDecimal sqTradeAmt;

    /**
     * 申请信息-资金账户
     */
    private String sqTrdAcctId;

    /**
     * （认购）确认信息-确认日期
     */
    private Date rgqrTradeDate;
    /**
     * （认购）确认信息-确认数量
     */
    private BigDecimal rgqrNums;
    /**
     * （认购）确认信息-确认金额
     */
    private BigDecimal rgqrTradeAmt;
    /**
     * （认购）确认信息-募集期利息
     */
    private BigDecimal raiseInterest;
    /**
     * （认购）确认信息-认购费
     */
    private BigDecimal subscribeFee;
    /**
     * （认购）确认信息-资金账户
     */
    private String rgqrTrdAcctId;

    /**
     * （申购）确认信息-确认日期
     */
    private Date sgqrTradeDate;
    /**
     * （申购）确认信息-确认数量
     */
    private BigDecimal sgqrNums;
    /**
     * （申购）确认信息-确认金额
     */
    private BigDecimal sgqrTradeAmt;
    /**
     * （申购）确认信息-单位净值
     */
    private BigDecimal sgqrUnitValue;
    /**
     * （申购）确认信息-利息收入
     */
    private BigDecimal interestIncome;
    /**
     * （申购）确认信息-申购费
     */
    private BigDecimal purchaseFee;
    /**
     * （申购）确认信息-资金账户
     */
    private String sgqrTrdAcctId;


    /**
     * 赎回信息-确认日期
     */
    private Date shqrTradeDate;
    /**
     * 赎回信息-确认数量
     */
    private BigDecimal shqrNums;
    /**
     * 赎回信息-确认金额
     */
    private BigDecimal shqrTradeAmt;
    /**
     * 赎回信息-单位净值
     */
    private BigDecimal shqrUnitValue;
    /**
     * 赎回信息-赎回费
     */
    private BigDecimal redemptionFee;
    /**
     * 赎回信息-到账日期
     */
    private Date shqrSettleDate;
    /**
     * 赎回信息-资金账户
     */
    private String shqrTrdAcctId;

    /**
     * 赎回到账-到账日期
     */
    private Date shdzSettleDate ;
    /**
     * 赎回到账-到账金额
     */
    private BigDecimal shdzTradeAmt;
    /**
     * 赎回到账-到账账户
     */
    private String shdzTrdAcctId;




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
    @NotEmpty(fieldName="TrdPlanValuationTrade002.ccy",groups = {ValidateGroupEdit.class})
    public String getCcy(){
        return super.getCcy();
    }
    /**
     * 交易方向
     * {@inheritDoc}
     */
    @NotEmpty(fieldName="TrdPlanValuationTrade002.ps",groups = {ValidateGroupEdit.class})
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

    public String getTradeMarketStr(){
        return SysDataUtil.getNameFromDataDict(DictCode.交易场所, super.getTradeMarket());
    }


    /**
     * 获取 portfolioId
     * @return portfolioId.
     */
    @NotEmpty(fieldName="TrdPlanValuationTrade002.portfolioId",groups = {ValidateGroupEdit.class})
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
    @NotEmpty(fieldName="TrdPlanValuationTrade002.portfolioName",groups = {ValidateGroupEdit.class})
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
    @NotEmpty(fieldName="TrdPlanValuationTrade002.finprodName",groups = {ValidateGroupEdit.class})
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

    public BigDecimal getNums() {
        return nums;
    }

    public void setNums(BigDecimal nums) {
        this.nums = nums;
    }


    public BigDecimal getConfirmNums() {
        return confirmNums;
    }

    public void setConfirmNums(BigDecimal confirmNums) {
        this.confirmNums = confirmNums;
    }

    public BigDecimal getCarryAmt() {
        return carryAmt;
    }

    public void setCarryAmt(BigDecimal carryAmt) {
        this.carryAmt = carryAmt;
    }




    public BigDecimal getMdateSettleAmt() {
        return mdateSettleAmt;
    }

    public void setMdateSettleAmt(BigDecimal mdateSettleAmt) {
        this.mdateSettleAmt = mdateSettleAmt;
    }


    //交易金额
    @Override
    public BigDecimal getTradeAmt() {
        return super.getTradeAmt();
    }
    //交易费用
    @Override
    public BigDecimal getFeeAmt() {
        return super.getFeeAmt();
    }

    //资产代码
    @Override
    @NotEmpty(fieldName="TrdPlanValuationTrade002.finprodId",groups = {ValidateGroupEdit.class})
    public String getFinprodId() {
        return super.getFinprodId();
    }

    @Override
    public String getTradeMarket() {
        return tradeMarket;
    }

    public String getTradeDateStr() {
        String tradeDateStr="";
        if(super.getTradeDate() !=null){
            tradeDateStr= DateUtil.format(super.getTradeDate());
        }
        return tradeDateStr;
    }

    @Override
    public Date getSettleDate() {
        return settleDate;
    }

    @Override
    public void setSettleDate(Date settleDate) {
        this.settleDate = settleDate;
    }

    public BigDecimal getRaiseInterest() {
        return raiseInterest;
    }

    public void setRaiseInterest(BigDecimal raiseInterest) {
        this.raiseInterest = raiseInterest;
    }

    public BigDecimal getSubscribeFee() {
        return subscribeFee;
    }

    public void setSubscribeFee(BigDecimal subscribeFee) {
        this.subscribeFee = subscribeFee;
    }

    public BigDecimal getInterestIncome() {
        return interestIncome;
    }

    public void setInterestIncome(BigDecimal interestIncome) {
        this.interestIncome = interestIncome;
    }

    public BigDecimal getPurchaseFee() {
        return purchaseFee;
    }

    public void setPurchaseFee(BigDecimal purchaseFee) {
        this.purchaseFee = purchaseFee;
    }

    public BigDecimal getRedemptionFee() {
        return redemptionFee;
    }

    public void setRedemptionFee(BigDecimal redemptionFee) {
        this.redemptionFee = redemptionFee;
    }

    public Date getRgTradeDate() {
        return rgTradeDate;
    }

    public void setRgTradeDate(Date rgTradeDate) {
        this.rgTradeDate = rgTradeDate;
    }

    public Date getRgSettleDate() {
        return rgSettleDate;
    }

    public void setRgSettleDate(Date rgSettleDate) {
        this.rgSettleDate = rgSettleDate;
    }

    public BigDecimal getRgTradeAmt() {
        return rgTradeAmt;
    }

    public void setRgTradeAmt(BigDecimal rgTradeAmt) {
        this.rgTradeAmt = rgTradeAmt;
    }

    public String getRgTrdAcctId() {
        return rgTrdAcctId;
    }

    public void setRgTrdAcctId(String rgTrdAcctId) {
        this.rgTrdAcctId = rgTrdAcctId;
    }

    public Date getSqTradeDate() {
        return sqTradeDate;
    }

    public void setSqTradeDate(Date sqTradeDate) {
        this.sqTradeDate = sqTradeDate;
    }

    public Date getSqSettleDate() {
        return sqSettleDate;
    }

    public void setSqSettleDate(Date sqSettleDate) {
        this.sqSettleDate = sqSettleDate;
    }

    public BigDecimal getSqTradeAmt() {
        return sqTradeAmt;
    }

    public void setSqTradeAmt(BigDecimal sqTradeAmt) {
        this.sqTradeAmt = sqTradeAmt;
    }

    public String getSqTrdAcctId() {
        return sqTrdAcctId;
    }

    public void setSqTrdAcctId(String sqTrdAcctId) {
        this.sqTrdAcctId = sqTrdAcctId;
    }

    public Date getRgqrTradeDate() {
        return rgqrTradeDate;
    }

    public void setRgqrTradeDate(Date rgqrTradeDate) {
        this.rgqrTradeDate = rgqrTradeDate;
    }

    public BigDecimal getRgqrNums() {
        return rgqrNums;
    }

    public void setRgqrNums(BigDecimal rgqrNums) {
        this.rgqrNums = rgqrNums;
    }

    public BigDecimal getRgqrTradeAmt() {
        return rgqrTradeAmt;
    }

    public void setRgqrTradeAmt(BigDecimal rgqrTradeAmt) {
        this.rgqrTradeAmt = rgqrTradeAmt;
    }

    public String getRgqrTrdAcctId() {
        return rgqrTrdAcctId;
    }

    public void setRgqrTrdAcctId(String rgqrTrdAcctId) {
        this.rgqrTrdAcctId = rgqrTrdAcctId;
    }

    public Date getSgqrTradeDate() {
        return sgqrTradeDate;
    }

    public void setSgqrTradeDate(Date sgqrTradeDate) {
        this.sgqrTradeDate = sgqrTradeDate;
    }

    public BigDecimal getSgqrNums() {
        return sgqrNums;
    }

    public void setSgqrNums(BigDecimal sgqrNums) {
        this.sgqrNums = sgqrNums;
    }

    public BigDecimal getSgqrTradeAmt() {
        return sgqrTradeAmt;
    }

    public void setSgqrTradeAmt(BigDecimal sgqrTradeAmt) {
        this.sgqrTradeAmt = sgqrTradeAmt;
    }

    public BigDecimal getSgqrUnitValue() {
        return sgqrUnitValue;
    }

    public void setSgqrUnitValue(BigDecimal sgqrUnitValue) {
        this.sgqrUnitValue = sgqrUnitValue;
    }

    public String getSgqrTrdAcctId() {
        return sgqrTrdAcctId;
    }

    public void setSgqrTrdAcctId(String sgqrTrdAcctId) {
        this.sgqrTrdAcctId = sgqrTrdAcctId;
    }

    public Date getShqrTradeDate() {
        return shqrTradeDate;
    }

    public void setShqrTradeDate(Date shqrTradeDate) {
        this.shqrTradeDate = shqrTradeDate;
    }

    public BigDecimal getShqrNums() {
        return shqrNums;
    }

    public void setShqrNums(BigDecimal shqrNums) {
        this.shqrNums = shqrNums;
    }

    public BigDecimal getShqrTradeAmt() {
        return shqrTradeAmt;
    }

    public void setShqrTradeAmt(BigDecimal shqrTradeAmt) {
        this.shqrTradeAmt = shqrTradeAmt;
    }

    public BigDecimal getShqrUnitValue() {
        return shqrUnitValue;
    }

    public void setShqrUnitValue(BigDecimal shqrUnitValue) {
        this.shqrUnitValue = shqrUnitValue;
    }

    public Date getShqrSettleDate() {
        return shqrSettleDate;
    }

    public void setShqrSettleDate(Date shqrSettleDate) {
        this.shqrSettleDate = shqrSettleDate;
    }

    public String getShqrTrdAcctId() {
        return shqrTrdAcctId;
    }

    public void setShqrTrdAcctId(String shqrTrdAcctId) {
        this.shqrTrdAcctId = shqrTrdAcctId;
    }

    public Date getShdzSettleDate() {
        return shdzSettleDate;
    }

    public void setShdzSettleDate(Date shdzSettleDate) {
        this.shdzSettleDate = shdzSettleDate;
    }

    public BigDecimal getShdzTradeAmt() {
        return shdzTradeAmt;
    }

    public void setShdzTradeAmt(BigDecimal shdzTradeAmt) {
        this.shdzTradeAmt = shdzTradeAmt;
    }

    public String getShdzTrdAcctId() {
        return shdzTrdAcctId;
    }

    public void setShdzTrdAcctId(String shdzTrdAcctId) {
        this.shdzTrdAcctId = shdzTrdAcctId;
    }

    public BigDecimal getAdvFeeOut() {
        return advFeeOut;
    }

    public void setAdvFeeOut(BigDecimal advFeeOut) {
        this.advFeeOut = advFeeOut;
    }

    public BigDecimal getFeeAmtQT() {
        return feeAmtQT;
    }

    public void setFeeAmtQT(BigDecimal feeAmtQT) {
        this.feeAmtQT = feeAmtQT;
    }
}
