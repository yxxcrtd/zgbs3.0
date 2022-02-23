/*
 * 文件名：TrdFixIncomeInvestTradeDto.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：fanshuanghu
 * 修改时间：2018年11月22日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.valuation.trademanage.trdfixincomevaluationtrade.dto;

import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.framework.validation.annotation.NotEmpty;
import com.joyintech.framework.validation.annotation.ValidLogic;
import com.joyintech.framework.validation.groups.ValidateGroupDelete;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.valuation.trademanage.trdfixincomevaluationtrade.validate.TrdFixIncomeValuationTradeDtoValidator;
import com.joyintech.workflow.engine.annotation.PrimaryKey;
import com.joyintech.workflow.engine.annotation.PrimaryKey.Field;
import com.joyintech.workflow.engine.dto.ProcessEntity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @Description:固收类交易
 * @auther: yuzj
 * @date: 2022/1/5
 * @param:
 * @return:
 */
@PageId("TrdFixIncomeValuationTrade002")
@ValidLogic(targetClass = TrdFixIncomeValuationTradeDtoValidator.class, groups = {ValidateGroupEdit.class,
    ValidateGroupDelete.class})
public class TrdFixIncomeValuationTradeDto extends WflTrdProductDeal implements ProcessEntity {

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
     * 全价总额
     */
    private BigDecimal fpriceAmt;

    /**
     * 应收利息总额
     */
    private BigDecimal intAmt;

    /**
     * 净价总额
     */
    private BigDecimal cpriceAmt;

    /**
     * 净价
     */
    private BigDecimal netPrice;

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
    private List<TrdFixIncomeValuationTradeDto> selectedItem;

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
    @NotEmpty(fieldName="TrdFixIncomeValuationTrade001.tradeDate",groups = {ValidateGroupEdit.class})
    public Date getTradeDate(){
        return super.getTradeDate();
    }




    /**
     * 获取 selectedItem
     * @return selectedItem.
     */
    public List<TrdFixIncomeValuationTradeDto> getSelectedItem() {
        return selectedItem;
    }



    /**
     * 设置 selectedItem
     * @param selectedItem selectedItem
     */
    public void setSelectedItem(List<TrdFixIncomeValuationTradeDto> selectedItem) {
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
    @NotEmpty(fieldName="TrdFixIncomeValuationTrade001.ccy",groups = {ValidateGroupEdit.class})
    public String getCcy(){
        return super.getCcy();
    }
    /**
     * 交易方向
     * {@inheritDoc}
     */
    @NotEmpty(fieldName="TrdFixIncomeValuationTrade001.ps",groups = {ValidateGroupEdit.class})
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
    @NotEmpty(fieldName="TrdFixIncomeValuationTrade001.portfolioId",groups = {ValidateGroupEdit.class})
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
    @NotEmpty(fieldName="TrdFixIncomeValuationTrade001.portfolioName",groups = {ValidateGroupEdit.class})
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
    @NotEmpty(fieldName="TrdFixIncomeValuationTrade001.finprodName",groups = {ValidateGroupEdit.class})
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

    @NotEmpty(fieldName="TrdFixIncomeValuationTrade002.feeAmtQT",groups = {ValidateGroupEdit.class})
    public BigDecimal getFeeAmtQT() {
        return feeAmtQT;
    }

    public void setFeeAmtQT(BigDecimal feeAmtQT) {
        this.feeAmtQT = feeAmtQT;
    }

    @NotEmpty(fieldName="TrdFixIncomeValuationTrade002.mdateSettleAmt",groups = {ValidateGroupEdit.class})
    public BigDecimal getMdateSettleAmt() {
        return mdateSettleAmt;
    }

    public void setMdateSettleAmt(BigDecimal mdateSettleAmt) {
        this.mdateSettleAmt = mdateSettleAmt;
    }


    //交易金额
    @Override
    @NotEmpty(fieldName="TrdFixIncomeValuationTrade001.tradeAmt",groups = {ValidateGroupEdit.class})
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
    @NotEmpty(fieldName="TrdFixIncomeValuationTrade001.finprodId",groups = {ValidateGroupEdit.class})
    public String getFinprodId() {
        return super.getFinprodId();
    }

    @NotEmpty(fieldName="TrdFixIncomeValuationTrade002.settleDate",groups = {ValidateGroupEdit.class})
    @Override
    public Date getSettleDate() {
        return settleDate;
    }

    @Override
    public void setSettleDate(Date settleDate) {
        this.settleDate = settleDate;
    }

    @Override
    @NotEmpty(fieldName="TrdFixIncomeValuationTrade002.fpriceAmt",groups = {ValidateGroupEdit.class})
    public BigDecimal getFpriceAmt() {
        return fpriceAmt;
    }

    @Override
    public void setFpriceAmt(BigDecimal fpriceAmt) {
        this.fpriceAmt = fpriceAmt;
    }

    @Override
    @NotEmpty(fieldName="TrdFixIncomeValuationTrade002.intAmt",groups = {ValidateGroupEdit.class})
    public BigDecimal getIntAmt() {
        return intAmt;
    }

    @Override
    public void setIntAmt(BigDecimal intAmt) {
        this.intAmt = intAmt;
    }

    @Override
    @NotEmpty(fieldName="TrdFixIncomeValuationTrade002.cpriceAmt",groups = {ValidateGroupEdit.class})
    public BigDecimal getCpriceAmt() {
        return cpriceAmt;
    }

    @Override
    public void setCpriceAmt(BigDecimal cpriceAmt) {
        this.cpriceAmt = cpriceAmt;
    }

    @NotEmpty(fieldName="TrdFixIncomeValuationTrade002.netPrice",groups = {ValidateGroupEdit.class})
    public BigDecimal getNetPrice() {
        return netPrice;
    }

    public void setNetPrice(BigDecimal netPrice) {
        this.netPrice = netPrice;
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
}

