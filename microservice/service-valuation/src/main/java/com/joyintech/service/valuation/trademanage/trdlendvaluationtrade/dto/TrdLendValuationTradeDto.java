/*
 * 文件名：TrdLendInvestTradeDto.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：杨智
 * 修改时间：2018年10月10日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
package com.joyintech.service.valuation.trademanage.trdlendvaluationtrade.dto;


import com.joyintech.framework.base.dto.UploadFile;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.framework.validation.annotation.NotEmpty;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.workflow.engine.annotation.PrimaryKey;
import com.joyintech.workflow.engine.dto.ProcessEntity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


public class TrdLendValuationTradeDto extends WflTrdProductDeal implements ProcessEntity {

    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    private static final long serialVersionUID = -6093478793142816692L;

    /**
     * 交易编号
     */
    private String tradeId;

    /**
     * 外部交易编号
     */
    private String outTradeId;

    /**
     * 组合代码  产品代码
     */
    private String portfolioId;


    /**
     * 组合名称  产品名称
     */
    private String portfolioName;


    /**
     * 交易类型
     */
    private String busiPs;

    /**
     * 计息基础
     */
    private String basis;


    /**
     * 交易日
     */

    private Date tradeDate;

    /**
     * 交割日
     */
    private Date settDate;

    /**
     * 起息日
     */
    private Date vdate;

    /**
     * 到期日
     */
    private Date mdate;

    /**
     * 本金金额
     */
    private BigDecimal prinAmt;

    /**
     * 拆借利率
     */
    private BigDecimal intRate;

    /**
     * 应计利息
     */
    private BigDecimal intAmt;


    /**
     * 本金币种
     */
    private String ccy;

    /**
     * 交易手续费
     */
    private BigDecimal dealFeeAmt;

    /**
     * 结算服务费
     */
    private BigDecimal settFeeAmt;

    /**
     * 其他费用
     */
    private BigDecimal otherAmt;


    /**
     * 结算金额
     */
    private BigDecimal settleAmt;

    /**
     * 本币交易金额
     */
    private BigDecimal dealAmt;


    /**
     * 资金账户
     */
    private String trdAcctId;

    @PrimaryKey(PrimaryKey.Field.PK_1)
//    @NotEmpty(fieldName = "TrdLendValuationTrade002.tradeId", groups = {ValidateGroupEdit.class})
    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }

    public String getOutTradeId() {
        return outTradeId;
    }

    public void setOutTradeId(String outTradeId) {
        this.outTradeId = outTradeId;
    }

    @NotEmpty(fieldName = "TrdLendValuationTrade002.basis", groups = {ValidateGroupEdit.class})
    public String getBasis() {
        return basis;
    }

    public void setBasis(String basis) {
        this.basis = basis;
    }

    @NotEmpty(fieldName = "TrdLendValuationTrade002.tradeDate", groups = {ValidateGroupEdit.class})
    public Date getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(Date tradeDate) {
        this.tradeDate = tradeDate;
    }

    @NotEmpty(fieldName = "TrdLendValuationTrade002.settDate", groups = {ValidateGroupEdit.class})
    public Date getSettDate() {
        return settDate;
    }

    public void setSettDate(Date settDate) {
        this.settDate = settDate;
    }

    @NotEmpty(fieldName = "TrdLendValuationTrade002.vdate", groups = {ValidateGroupEdit.class})
    public Date getVdate() {
        return vdate;
    }

    public void setVdate(Date vdate) {
        this.vdate = vdate;
    }

    @NotEmpty(fieldName = "TrdLendValuationTrade002.mdate", groups = {ValidateGroupEdit.class})
    public Date getMdate() {
        return mdate;
    }

    public void setMdate(Date mdate) {
        this.mdate = mdate;
    }

    @NotEmpty(fieldName = "TrdLendValuationTrade002.prinAmt", groups = {ValidateGroupEdit.class})
    public BigDecimal getPrinAmt() {
        return prinAmt;
    }

    public void setPrinAmt(BigDecimal prinAmt) {
        this.prinAmt = prinAmt;
    }

    @NotEmpty(fieldName = "TrdLendValuationTrade002.intRate", groups = {ValidateGroupEdit.class})
    public BigDecimal getIntRate() {
        return intRate;
    }

    public void setIntRate(BigDecimal intRate) {
        this.intRate = intRate;
    }

    @NotEmpty(fieldName = "TrdLendValuationTrade002.intAmt", groups = {ValidateGroupEdit.class})
    public BigDecimal getIntAmt() {
        return intAmt;
    }

    public void setIntAmt(BigDecimal intAmt) {
        this.intAmt = intAmt;
    }

    @NotEmpty(fieldName = "TrdLendValuationTrade002.ccy", groups = {ValidateGroupEdit.class})
    public String getCcy() {
        return ccy;
    }

    public void setCcy(String ccy) {
        this.ccy = ccy;
    }

    @NotEmpty(fieldName = "TrdLendValuationTrade002.dealFeeAmt", groups = {ValidateGroupEdit.class})
    public BigDecimal getDealFeeAmt() {
        return dealFeeAmt;
    }

    public void setDealFeeAmt(BigDecimal dealFeeAmt) {
        this.dealFeeAmt = dealFeeAmt;
    }

    @NotEmpty(fieldName = "TrdLendValuationTrade002.settFeeAmt", groups = {ValidateGroupEdit.class})
    public BigDecimal getSettFeeAmt() {
        return settFeeAmt;
    }

    public void setSettFeeAmt(BigDecimal settFeeAmt) {
        this.settFeeAmt = settFeeAmt;
    }

    @NotEmpty(fieldName = "TrdLendValuationTrade002.otherAmt", groups = {ValidateGroupEdit.class})
    public BigDecimal getOtherAmt() {
        return otherAmt;
    }

    public void setOtherAmt(BigDecimal otherAmt) {
        this.otherAmt = otherAmt;
    }

    @NotEmpty(fieldName = "TrdLendValuationTrade002.settleAmt", groups = {ValidateGroupEdit.class})
    public BigDecimal getSettleAmt() {
        return settleAmt;
    }

    public void setSettleAmt(BigDecimal settleAmt) {
        this.settleAmt = settleAmt;
    }

    @NotEmpty(fieldName = "TrdLendValuationTrade002.dealAmt", groups = {ValidateGroupEdit.class})
    public BigDecimal getDealAmt() {
        return dealAmt;
    }

    public void setDealAmt(BigDecimal dealAmt) {
        this.dealAmt = dealAmt;
    }

    public String getTrdAcctId() {
        return trdAcctId;
    }

    public void setTrdAcctId(String trdAcctId) {
        this.trdAcctId = trdAcctId;
    }





    /**
     * 有效状态
     */
    private String validStatus;

    /**
     * 有效状态
     */
    private String validStatusText;

    /**
     * 委托代码
     */
    private String entrustId;



    /**
     * 实体组合代码
     */
    private String actPortfolioId;






    /**
     * 操作类型
     */
    private String operateType;


    /**
     * 获取 portfolioId
     *
     * @return portfolioId
     */
    @NotEmpty(fieldName = "TrdLendValuationTrade002.portfolioId", groups = {ValidateGroupEdit.class})
    public String getPortfolioId() {
        return portfolioId;
    }

    /**
     * 设置 portfolioId
     *
     * @param portfolioId portfolioId
     */
    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId;
    }

    /**
     * 获取 portfolioName
     *
     * @return portfolioName
     */
    @NotEmpty(fieldName = "TrdLendValuationTrade002.portfolioName", groups = {ValidateGroupEdit.class})
    public String getPortfolioName() {
        return portfolioName;
    }

    /**
     * 设置 portfolioName
     *
     * @param portfolioName portfolioName
     */
    public void setPortfolioName(String portfolioName) {
        this.portfolioName = portfolioName;
    }

    /**
     * 获取 operateType
     *
     * @return operateType
     */
    public String getOperateType() {
        return operateType;
    }

    /**
     * 设置 operateType
     *
     * @param operateType operateType
     */
    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }



    /**
     * 获取 busiPs
     *
     * @return busiPs.
     */
    @NotEmpty(fieldName = "TrdLendValuationTrade002.busiPs", groups = {ValidateGroupEdit.class})
    public String getBusiPs() {
        return busiPs;
    }

    /**
     * 设置 busiPs
     *
     * @param busiPs busiPs
     */
    public void setBusiPs(String busiPs) {
        this.busiPs = busiPs;
    }


    /**
     * 产品类型
     */
    private String finprodType;

    public String getFinprodType() {
        return finprodType;
    }

    public void setFinprodType(String finprodType) {
        this.finprodType = finprodType;
    }


    /**
     * 交易方向
     */
    private String ps;

    public String getPs() {
        return ps;
    }

    public void setPs(String ps) {
        this.ps = ps;
    }


    /**
     * elementId
     */
    private String elementId;

    public String getElementId() {
        return elementId;
    }

    public void setElementId(String elementId) {
        this.elementId = elementId;
    }

    /**
     * 信用拆借交易实体类
     */
    private List<TrdLendValuationTradeDto> selectedItem;

    public List<TrdLendValuationTradeDto> getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(List<TrdLendValuationTradeDto> selectedItem) {
        this.selectedItem = selectedItem;
    }


}
