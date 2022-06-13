/*
 * 文件名：TrdDepositInvestTrade001Dto.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：fanshuanghu
 * 修改时间：2018年10月10日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.valuation.trademanage.trddepovaluationduration.dto;

import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.framework.validation.annotation.NotEmpty;
import com.joyintech.framework.validation.annotation.ValidLogic;
import com.joyintech.framework.validation.groups.ValidateGroupDelete;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.valuation.trademanage.trddepositvaluationtrade.validator.TrdDepositValuationTrade001Validator;

import com.joyintech.tams.valuation.trademanage.trddepovaluationduration.validator.TrdDepoValuationDuration001Validator;
import com.joyintech.workflow.engine.annotation.PrimaryKey;
import com.joyintech.workflow.engine.dto.ProcessEntity;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

/**
 * 存款类存续期实体类
 * @author fanshuanghu
 * @version 1.0 2018年10月10日
 * @see TrdDepoValuationDuration001Dto
 * @since 1.0
 */
@PageId("TrdDepoValuationDuration002")
@ValidLogic(targetClass = TrdDepoValuationDuration001Validator.class, groups = {ValidateGroupEdit.class,
    ValidateGroupDelete.class})
public class TrdDepoValuationDuration001Dto extends BaseEntity implements ProcessEntity {
    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    private static final long serialVersionUID = -716587016375490940L;

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
     * 市场机构代码  资产代码
     */
    private String finprodMarketId;

    /**
     * 金融产品名称  资产名称
     */
    private String finprodName;

    /**
     * 交易日期
     */
    private Date tradeDate;


    /**
     * 交易类型
     */
    private String ps;

    /**
     * 计息结束日
     */
    private Date endDate;

    /**
     * 实际支付日
     */
    private Date payDate;

    /**
     * 实收利息
     */
    private BigDecimal actInt;

    /**
     * 实收本金
     */

    private BigDecimal actCapi;

    /**
     * 币种
     */
    private String ccy;

    /**
     * 其他费用
     */
    private BigDecimal otherAmt;

    /**
     * 结算金额
     */
    private BigDecimal settleAmt;


    /**
     * 资金账户
     */
    private String trdAcctId;

    /**
     * 本币交易金额
     */
    private BigDecimal dealAmt;


    /**
     * 操作类型
     */
    private String operateType;
    /**
     * elementId
     */
    private String elementId;

    /**
     * 存款类存续期实体类
     */
    private List<TrdDepoValuationDuration001Dto> selectedItem;

    public List<TrdDepoValuationDuration001Dto> getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(List<TrdDepoValuationDuration001Dto> selectedItem) {
        this.selectedItem = selectedItem;
    }

    @PrimaryKey(PrimaryKey.Field.PK_1)
//    @NotEmpty(fieldName = "TrdDepoValuationDuration002.tradeId", groups = {ValidateGroupEdit.class})
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

    @NotEmpty(fieldName = "TrdDepoValuationDuration002.portfolioId", groups = {ValidateGroupEdit.class})
    public String getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId;
    }

    @NotEmpty(fieldName = "TrdDepoValuationDuration002.portfolioName", groups = {ValidateGroupEdit.class})
    public String getPortfolioName() {
        return portfolioName;
    }

    public void setPortfolioName(String portfolioName) {
        this.portfolioName = portfolioName;
    }

    @NotEmpty(fieldName = "TrdDepoValuationDuration002.finprodMarketId", groups = {ValidateGroupEdit.class})
    public String getFinprodMarketId() {
        return finprodMarketId;
    }

    public void setFinprodMarketId(String finprodMarketId) {
        this.finprodMarketId = finprodMarketId;
    }

    @NotEmpty(fieldName = "TrdDepoValuationDuration002.finprodName", groups = {ValidateGroupEdit.class})
    public String getFinprodName() {
        return finprodName;
    }

    public void setFinprodName(String finprodName) {
        this.finprodName = finprodName;
    }

    @NotEmpty(fieldName = "TrdDepoValuationDuration002.tradeDate", groups = {ValidateGroupEdit.class})
    public Date getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(Date tradeDate) {
        this.tradeDate = tradeDate;
    }

    @NotEmpty(fieldName = "TrdDepoValuationDuration002.ps", groups = {ValidateGroupEdit.class})
    public String getPs() {
        return ps;
    }

    public void setPs(String ps) {
        this.ps = ps;
    }

    @NotEmpty(fieldName = "TrdDepoValuationDuration002.endDate", groups = {ValidateGroupEdit.class})
    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @NotEmpty(fieldName = "TrdDepoValuationDuration002.payDate", groups = {ValidateGroupEdit.class})
    public Date getPayDate() {
        return payDate;
    }

    public void setPayDate(Date payDate) {
        this.payDate = payDate;
    }

    @NotEmpty(fieldName = "TrdDepoValuationDuration002.actInt", groups = {ValidateGroupEdit.class})
    public BigDecimal getActInt() {
        return actInt;
    }

    public void setActInt(BigDecimal actInt) {
        this.actInt = actInt;
    }

    @NotEmpty(fieldName = "TrdDepoValuationDuration002.actCapi", groups = {ValidateGroupEdit.class})
    public BigDecimal getActCapi() {
        return actCapi;
    }

    public void setActCapi(BigDecimal actCapi) {
        this.actCapi = actCapi;
    }

    @NotEmpty(fieldName = "TrdDepoValuationDuration002.ccy", groups = {ValidateGroupEdit.class})
    public String getCcy() {
        return ccy;
    }

    public void setCcy(String ccy) {
        this.ccy = ccy;
    }

//    @NotEmpty(fieldName = "TrdDepoValuationDuration002.otherAmt", groups = {ValidateGroupEdit.class})
    public BigDecimal getOtherAmt() {
        return otherAmt;
    }

    public void setOtherAmt(BigDecimal otherAmt) {
        this.otherAmt = otherAmt;
    }

    @NotEmpty(fieldName = "TrdDepoValuationDuration002.settleAmt", groups = {ValidateGroupEdit.class})
    public BigDecimal getSettleAmt() {
        return settleAmt;
    }

    public void setSettleAmt(BigDecimal settleAmt) {
        this.settleAmt = settleAmt;
    }

    @NotEmpty(fieldName = "TrdDepoValuationDuration002.trdAcctId", groups = {ValidateGroupEdit.class})
    public String getTrdAcctId() {
        return trdAcctId;
    }

    public void setTrdAcctId(String trdAcctId) {
        this.trdAcctId = trdAcctId;
    }

//    @NotEmpty(fieldName = "TrdDepoValuationDuration002.dealAmt", groups = {ValidateGroupEdit.class})
    public BigDecimal getDealAmt() {
        return dealAmt;
    }

    public void setDealAmt(BigDecimal dealAmt) {
        this.dealAmt = dealAmt;
    }

    public String getOperateType() {
        return operateType;
    }

    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }

    public String getElementId() {
        return elementId;
    }

    public void setElementId(String elementId) {
        this.elementId = elementId;
    }

    //导出转化交易日
    private  String tradeDateStr;

    public String getTradeDateStr() {
        String tradeDateStr=" ";
        if(getTradeDate() !=null){
            tradeDateStr=DateUtil.format(getTradeDate());
        }
        return tradeDateStr;
    }


    //导出转化计息结束日
    private  String endDateStr;

    public String getEndDateStr() {
        String endDateStr=" ";
        if(getEndDate() !=null){
            endDateStr=DateUtil.format(getEndDate());
        }
        return endDateStr;
    }

    //导出转化实际支付日
    private  String payDateStr;

    public String getPayDateStr() {
        String payDateStr=" ";
        if(getPayDate() !=null){
            payDateStr=DateUtil.format(getPayDate());
        }
        return payDateStr;
    }

    /**
     * 取得 交易方向str
     *
     * @return 交易方向
     */
    public String getPsStr() {
        return StringUtil.isNotEmpty(SysDataUtil.getNameFromDataDict(DictCode.交易方向, ps))?
                SysDataUtil.getNameFromDataDict(DictCode.交易方向, ps):ps;
    }

    /**
     * 取得 币种Fmt
     *
     * @return 币种
     */
    public String getCcyFmt() {
        return StringUtil.isNotEmpty(SysDataUtil.getNameFromDataDict(DictCode.币种, ccy))?
                SysDataUtil.getNameFromDataDict(DictCode.币种, ccy):ccy;
    }

    //导出保留小数


    //实收利息 2位
    public String getActIntStr(){
        DecimalFormat decimalFormat = new DecimalFormat("0.00#");
        return this.getActInt()==null?"0.00":decimalFormat.format(this.getActInt().setScale(2,BigDecimal.ROUND_HALF_UP));

    }


    //实收本金 2位
    public String getActCapiStr(){
        DecimalFormat decimalFormat = new DecimalFormat("0.00#");
        return this.getActCapi()==null?"0.00":decimalFormat.format(this.getActCapi().setScale(2,BigDecimal.ROUND_HALF_UP));

    }


    //结算金额 2位
    public String getSettleAmtStr() {
        DecimalFormat decimalFormat = new DecimalFormat("0.00#");
        return this.getSettleAmt()==null?"0.00":decimalFormat.format(this.getSettleAmt().setScale(2,BigDecimal.ROUND_HALF_UP));
    }
}
