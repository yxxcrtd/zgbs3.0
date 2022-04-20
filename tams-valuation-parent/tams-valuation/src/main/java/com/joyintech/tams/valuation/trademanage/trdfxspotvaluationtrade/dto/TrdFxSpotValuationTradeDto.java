/*
 * 文件名：TrdFxSpotInvestTradeDto.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：fanshuanghu
 * 修改时间：2018年11月22日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.valuation.trademanage.trdfxspotvaluationtrade.dto;

import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.framework.validation.annotation.NotEmpty;
import com.joyintech.framework.validation.annotation.ValidLogic;
import com.joyintech.framework.validation.groups.ValidateGroupDelete;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.valuation.trademanage.trdfxspotvaluationtrade.validate.TrdFxSpotValuationTradeDtoValidator;
import com.joyintech.workflow.engine.annotation.PrimaryKey;
import com.joyintech.workflow.engine.annotation.PrimaryKey.Field;
import com.joyintech.workflow.engine.dto.ProcessEntity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @Description:外汇即期交易
 * @auther: yuzj
 * @date: 2022/1/11
 * @param:
 * @return:
 */
@PageId("TrdFxSpotValuationTrade002")
@ValidLogic(targetClass = TrdFxSpotValuationTradeDtoValidator.class, groups = {ValidateGroupEdit.class,
    ValidateGroupDelete.class})
public class TrdFxSpotValuationTradeDto extends WflTrdProductDeal implements ProcessEntity {

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
     * 其他费用
     */
    private BigDecimal feeAmtQT;

    /**
     * 结算金额
     */
    private BigDecimal mdateSettleAmt;


    /**
     * 资金账户
     */
    private String trdAcctId;

    /**
     * 外部交易编号
     */
    private String outTradeId;

    /**
     * 交割日期
     */
    private Date settleDate;


    /**
     * 金融产品名称
     */
    private String finprodName;

    /**
     * 交易货币
     */
    private String dealCur;
    /**
     * 交易货币金额
     */
    private BigDecimal dealCurAmt;
    /**
     * 对应货币
     */
    private String corCcy;
    /**
     * 对应货币金额
     */
    private BigDecimal corCcyAmt;
    /**
     * 折美元金额
     */
    private BigDecimal usdAmt;
    /**
     * 交易手续费
     */
    private BigDecimal procedureAmt;
    /**
     * 结算服务费
     */
    private BigDecimal settleServiceAmt;
    /**
     * 货币对
     */
    private String curPair;
    /**
     * 交易模式
     */
    private String dealMode;
    /**
     * 清算模式
     */
    private String settMode;

    /**
     * 即期汇率
     */
    private BigDecimal unitCprice;
    /**
     * 我方角色
     */
    private String ourRole;


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
    private List<TrdFxSpotValuationTradeDto> selectedItem;

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
    @NotEmpty(fieldName="TrdFxSpotValuationTrade001.tradeDate",groups = {ValidateGroupEdit.class})
    public Date getTradeDate(){
        return super.getTradeDate();
    }

    /**
     * 获取 selectedItem
     * @return selectedItem.
     */
    public List<TrdFxSpotValuationTradeDto> getSelectedItem() {
        return selectedItem;
    }

    /**
     * 设置 selectedItem
     * @param selectedItem selectedItem
     */
    public void setSelectedItem(List<TrdFxSpotValuationTradeDto> selectedItem) {
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
     * 交易方向
     * {@inheritDoc}
     */
    @NotEmpty(fieldName="TrdFxSpotValuationTrade001.ps",groups = {ValidateGroupEdit.class})
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
    @NotEmpty(fieldName="TrdFxSpotValuationTrade001.portfolioId",groups = {ValidateGroupEdit.class})
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
    @NotEmpty(fieldName="TrdFxSpotValuationTrade001.portfolioName",groups = {ValidateGroupEdit.class})
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

    @NotEmpty(fieldName="TrdFxSpotValuationTrade002.feeAmtQT",groups = {ValidateGroupEdit.class})
    public BigDecimal getFeeAmtQT() {
        return feeAmtQT;
    }

    public void setFeeAmtQT(BigDecimal feeAmtQT) {
        this.feeAmtQT = feeAmtQT;
    }

    @NotEmpty(fieldName="TrdFxSpotValuationTrade002.mdateSettleAmt",groups = {ValidateGroupEdit.class})
    public BigDecimal getMdateSettleAmt() {
        return mdateSettleAmt;
    }

    public void setMdateSettleAmt(BigDecimal mdateSettleAmt) {
        this.mdateSettleAmt = mdateSettleAmt;
    }



    @NotEmpty(fieldName="TrdFxSpotValuationTrade002.settleDate",groups = {ValidateGroupEdit.class})
    @Override
    public Date getSettleDate() {
        return settleDate;
    }

    @Override
    public void setSettleDate(Date settleDate) {
        this.settleDate = settleDate;
    }

    public String getInvAimStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.投资目的, super.getInvAim());
    }

    public String getTradeDateStr() {
        String tradeDateStr="";
        if(super.getTradeDate() !=null){
            tradeDateStr= DateUtil.format(super.getTradeDate());
        }
        return tradeDateStr;
    }

    public String getSettleDateStr() {
        String settleDateStr="";
        if(super.getSettleDate() !=null){
            settleDateStr= DateUtil.format(super.getSettleDate());
        }
        return settleDateStr;
    }
    @NotEmpty(fieldName="TrdFxSpotValuationTrade002.dealCur",groups = {ValidateGroupEdit.class})
    public String getDealCur() {
        return dealCur;
    }

    public void setDealCur(String dealCur) {
        this.dealCur = dealCur;
    }
    @NotEmpty(fieldName="TrdFxSpotValuationTrade002.dealCurAmt",groups = {ValidateGroupEdit.class})
    public BigDecimal getDealCurAmt() {
        return dealCurAmt;
    }

    public void setDealCurAmt(BigDecimal dealCurAmt) {
        this.dealCurAmt = dealCurAmt;
    }
    @NotEmpty(fieldName="TrdFxSpotValuationTrade002.corCcy",groups = {ValidateGroupEdit.class})
    public String getCorCcy() {
        return corCcy;
    }

    public void setCorCcy(String corCcy) {
        this.corCcy = corCcy;
    }
    @NotEmpty(fieldName="TrdFxSpotValuationTrade002.corCcyAmt",groups = {ValidateGroupEdit.class})
    public BigDecimal getCorCcyAmt() {
        return corCcyAmt;
    }

    public void setCorCcyAmt(BigDecimal corCcyAmt) {
        this.corCcyAmt = corCcyAmt;
    }
    @NotEmpty(fieldName="TrdFxSpotValuationTrade002.usdAmt",groups = {ValidateGroupEdit.class})
    public BigDecimal getUsdAmt() {
        return usdAmt;
    }

    public void setUsdAmt(BigDecimal usdAmt) {
        this.usdAmt = usdAmt;
    }
    @NotEmpty(fieldName="TrdFxSpotValuationTrade002.procedureAmt",groups = {ValidateGroupEdit.class})
    public BigDecimal getProcedureAmt() {
        return procedureAmt;
    }

    public void setProcedureAmt(BigDecimal procedureAmt) {
        this.procedureAmt = procedureAmt;
    }
    @NotEmpty(fieldName="TrdFxSpotValuationTrade002.settleServiceAmt",groups = {ValidateGroupEdit.class})
    public BigDecimal getSettleServiceAmt() {
        return settleServiceAmt;
    }

    public void setSettleServiceAmt(BigDecimal settleServiceAmt) {
        this.settleServiceAmt = settleServiceAmt;
    }
    @NotEmpty(fieldName="TrdFxSpotValuationTrade002.curPair",groups = {ValidateGroupEdit.class})
    public String getCurPair() {
        return curPair;
    }

    public void setCurPair(String curPair) {
        this.curPair = curPair;
    }
    @NotEmpty(fieldName="TrdFxSpotValuationTrade002.dealMode",groups = {ValidateGroupEdit.class})
    public String getDealMode() {
        return dealMode;
    }

    public void setDealMode(String dealMode) {
        this.dealMode = dealMode;
    }
    @NotEmpty(fieldName="TrdFxSpotValuationTrade002.settMode",groups = {ValidateGroupEdit.class})
    public String getSettMode() {
        return settMode;
    }

    public void setSettMode(String settMode) {
        this.settMode = settMode;
    }

    @NotEmpty(fieldName="TrdFxSpotValuationTrade002.unitCprice",groups = {ValidateGroupEdit.class})
    @Override
    public BigDecimal getUnitCprice() {
        return unitCprice;
    }

    @Override
    public void setUnitCprice(BigDecimal unitCprice) {
        this.unitCprice = unitCprice;
    }

    @NotEmpty(fieldName="TrdFxSpotValuationTrade002.ourRole",groups = {ValidateGroupEdit.class})
    public String getOurRole() {
        return ourRole;
    }

    public void setOurRole(String ourRole) {
        this.ourRole = ourRole;
    }
}

