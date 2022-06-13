/*
 * 文件名：TrdFxForwardInvestTradeDto.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：fanshuanghu
 * 修改时间：2018年11月22日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.valuation.trademanage.trdfxforwardvaluationtrade.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.framework.validation.annotation.NotEmpty;
import com.joyintech.framework.validation.annotation.ValidLogic;
import com.joyintech.framework.validation.groups.ValidateGroupDelete;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.valuation.trademanage.trdfxforwardvaluationtrade.validate.TrdFxForwardValuationTradeDtoValidator;
import com.joyintech.workflow.engine.annotation.PrimaryKey;
import com.joyintech.workflow.engine.annotation.PrimaryKey.Field;
import com.joyintech.workflow.engine.dto.ProcessEntity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @Description:外汇远期交易
 * @auther: yuzj
 * @date: 2022/1/17
 * @param:
 * @return:
 */
@ValidLogic(targetClass = TrdFxForwardValuationTradeDtoValidator.class, groups = {ValidateGroupEdit.class,
    ValidateGroupDelete.class})
public class TrdFxForwardValuationTradeDto extends WflTrdProductDeal implements ProcessEntity {

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
     * 清算模式
     */
    private String settMode;

    /**
     * 即期汇率
     */
    private BigDecimal unitCprice;


    /**
     * 期限
     */
    private String termDate;

    /**
     * 定价日
     */
    private Date pricedDate;

    /**
     * 差额交割参考汇率
     */
    private String refRate;

    /**
     * 差额收付方向
     */
    private String difPs;

    /**
     * 远期点
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal unitInt;

    /**curPair
     * 远期全价
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal unitFprice;

    /**
     * 定价日即期汇率
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal pricedDateRate;

    /**
     * 差额收付金额
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal difPayAmt;


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
    private List<TrdFxForwardValuationTradeDto> selectedItem;

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
    @NotEmpty(fieldName="TrdFxForwardValuationTrade001.tradeDate",groups = {ValidateGroupEdit.class})
    public Date getTradeDate(){
        return super.getTradeDate();
    }

    /**
     * 获取 selectedItem
     * @return selectedItem.
     */
    public List<TrdFxForwardValuationTradeDto> getSelectedItem() {
        return selectedItem;
    }

    /**
     * 设置 selectedItem
     * @param selectedItem selectedItem
     */
    public void setSelectedItem(List<TrdFxForwardValuationTradeDto> selectedItem) {
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
    public String getCcy(){
        return super.getCcy();
    }
    /**
     * 交易方向
     * {@inheritDoc}
     */
    @NotEmpty(fieldName="TrdFxForwardValuationTrade001.ps",groups = {ValidateGroupEdit.class})
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
    @NotEmpty(fieldName="TrdFxForwardValuationTrade001.portfolioId",groups = {ValidateGroupEdit.class})
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
    @NotEmpty(fieldName="TrdFxForwardValuationTrade001.portfolioName",groups = {ValidateGroupEdit.class})
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

    @NotEmpty(fieldName="TrdFxForwardValuationTrade002.feeAmtQT",groups = {ValidateGroupEdit.class})
    public BigDecimal getFeeAmtQT() {
        return feeAmtQT;
    }

    public void setFeeAmtQT(BigDecimal feeAmtQT) {
        this.feeAmtQT = feeAmtQT;
    }

    @NotEmpty(fieldName="TrdFxForwardValuationTrade002.mdateSettleAmt",groups = {ValidateGroupEdit.class})
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
    public String getFinprodId() {
        return super.getFinprodId();
    }

    @NotEmpty(fieldName="TrdFxForwardValuationTrade002.settleDate",groups = {ValidateGroupEdit.class})
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
    @NotEmpty(fieldName="TrdFxForwardValuationTrade002.dealCur",groups = {ValidateGroupEdit.class})
    public String getDealCur() {
        return dealCur;
    }

    public void setDealCur(String dealCur) {
        this.dealCur = dealCur;
    }
    @NotEmpty(fieldName="TrdFxForwardValuationTrade002.dealCurAmt",groups = {ValidateGroupEdit.class})
    public BigDecimal getDealCurAmt() {
        return dealCurAmt;
    }

    public void setDealCurAmt(BigDecimal dealCurAmt) {
        this.dealCurAmt = dealCurAmt;
    }
    @NotEmpty(fieldName="TrdFxForwardValuationTrade002.corCcy",groups = {ValidateGroupEdit.class})
    public String getCorCcy() {
        return corCcy;
    }

    public void setCorCcy(String corCcy) {
        this.corCcy = corCcy;
    }
    @NotEmpty(fieldName="TrdFxForwardValuationTrade002.corCcyAmt",groups = {ValidateGroupEdit.class})
    public BigDecimal getCorCcyAmt() {
        return corCcyAmt;
    }

    public void setCorCcyAmt(BigDecimal corCcyAmt) {
        this.corCcyAmt = corCcyAmt;
    }
    @NotEmpty(fieldName="TrdFxForwardValuationTrade002.usdAmt",groups = {ValidateGroupEdit.class})
    public BigDecimal getUsdAmt() {
        return usdAmt;
    }

    public void setUsdAmt(BigDecimal usdAmt) {
        this.usdAmt = usdAmt;
    }
    @NotEmpty(fieldName="TrdFxForwardValuationTrade002.procedureAmt",groups = {ValidateGroupEdit.class})
    public BigDecimal getProcedureAmt() {
        return procedureAmt;
    }

    public void setProcedureAmt(BigDecimal procedureAmt) {
        this.procedureAmt = procedureAmt;
    }
    @NotEmpty(fieldName="TrdFxForwardValuationTrade002.settleServiceAmt",groups = {ValidateGroupEdit.class})
    public BigDecimal getSettleServiceAmt() {
        return settleServiceAmt;
    }

    public void setSettleServiceAmt(BigDecimal settleServiceAmt) {
        this.settleServiceAmt = settleServiceAmt;
    }
    @NotEmpty(fieldName="TrdFxForwardValuationTrade002.curPair",groups = {ValidateGroupEdit.class})
    public String getCurPair() {
        return curPair;
    }

    public void setCurPair(String curPair) {
        this.curPair = curPair;
    }
    @NotEmpty(fieldName="TrdFxForwardValuationTrade002.settMode",groups = {ValidateGroupEdit.class})
    public String getSettMode() {
        return settMode;
    }

    public void setSettMode(String settMode) {
        this.settMode = settMode;
    }

    @NotEmpty(fieldName="TrdFxForwardValuationTrade002.unitCprice",groups = {ValidateGroupEdit.class})
    @Override
    public BigDecimal getUnitCprice() {
        return unitCprice;
    }

    @Override
    public void setUnitCprice(BigDecimal unitCprice) {
        this.unitCprice = unitCprice;
    }

    @NotEmpty(fieldName="TrdFxForwardValuationTrade002.termDate",groups = {ValidateGroupEdit.class})
    public String getTermDate() {
        return termDate;
    }

    public void setTermDate(String termDate) {
        this.termDate = termDate;
    }

    public Date getPricedDate() {
        return pricedDate;
    }

    public void setPricedDate(Date pricedDate) {
        this.pricedDate = pricedDate;
    }

    public String getRefRate() {
        return refRate;
    }

    public void setRefRate(String refRate) {
        this.refRate = refRate;
    }

    public String getDifPs() {
        return difPs;
    }

    public void setDifPs(String difPs) {
        this.difPs = difPs;
    }

    @NotEmpty(fieldName="TrdFxForwardValuationTrade002.unitInt",groups = {ValidateGroupEdit.class})
    public BigDecimal getUnitInt() {
        return unitInt;
    }

    public void setUnitInt(BigDecimal unitInt) {
        this.unitInt = unitInt;
    }

    @NotEmpty(fieldName="TrdFxForwardValuationTrade002.unitFprice",groups = {ValidateGroupEdit.class})
    public BigDecimal getUnitFprice() {
        return unitFprice;
    }

    public void setUnitFprice(BigDecimal unitFprice) {
        this.unitFprice = unitFprice;
    }

    public BigDecimal getPricedDateRate() {
        return pricedDateRate;
    }

    public void setPricedDateRate(BigDecimal pricedDateRate) {
        this.pricedDateRate = pricedDateRate;
    }

    public BigDecimal getDifPayAmt() {
        return difPayAmt;
    }

    public void setDifPayAmt(BigDecimal difPayAmt) {
        this.difPayAmt = difPayAmt;
    }

    public String getPricedDateStr() {
        String pricedDateStr="";
        if(this.getPricedDate() !=null){
            pricedDateStr= DateUtil.format(this.getPricedDate());
        }
        return pricedDateStr;
    }

    public String getSettModeStr() {
        String settModeStr="";
        if(this.getSettMode() !=null){
            settModeStr= SysDataUtil.getNameFromDataDict(DictCode.结算方式, this.getSettMode());
        }
        return settModeStr;
    }



}

