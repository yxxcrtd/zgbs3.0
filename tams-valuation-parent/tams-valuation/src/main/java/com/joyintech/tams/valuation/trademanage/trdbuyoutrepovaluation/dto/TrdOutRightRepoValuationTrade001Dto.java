/*
 * 文件名：TrdDepositInvestTrade001Dto.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：fanshuanghu
 * 修改时间：2018年10月10日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.valuation.trademanage.trdbuyoutrepovaluation.dto;

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
import com.joyintech.tams.valuation.trademanage.trdbuyoutrepovaluation.validator.TrdOutRightRepoValuationTrade001Validator;
import com.joyintech.workflow.engine.annotation.PrimaryKey;
import com.joyintech.workflow.engine.dto.ProcessEntity;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

/**
 * 买断式回购交易实体类
 */
@PageId("TrdOutRightRepoValuationTrade002")
@ValidLogic(targetClass = TrdOutRightRepoValuationTrade001Validator.class, groups = {ValidateGroupEdit.class,
    ValidateGroupDelete.class})
public class TrdOutRightRepoValuationTrade001Dto extends BaseEntity implements ProcessEntity {
    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    private static final long serialVersionUID = 1L;


    /**
     * 交易编号
     */

    private String tradeId;

    /**
     * 外部交易编号
     */
    private String outTradeId;

    /**
     *   产品代码
     */
    private String portfolioId;


    /**
     *  产品名称
     */
    private String portfolioName;

    /**
     * 标的资产代码
     */
    private String finprodMarketId;

    /**
     * 标的资产名称
     */
    private String finprodName;

    /**
     * 回购期限
     */
    private BigDecimal term;

    /**
     * 交易类型
     */
    private String ps;

    /**
     * 交易日
     */

    private Date tradeDate;


    /**
     * 起息日
     */
    private Date vdate;

    /**
     * 到期日
     */
    private Date mdate;


    /**
     * 资金到账日
     */
    private Date settleDate;

    public Date getSettleDate() {
        return settleDate;
    }

    public void setSettleDate(Date settleDate) {
        this.settleDate = settleDate;
    }

    /**
     * 券面总额  万元
     */
    private BigDecimal prinAmt;

    /**
     * 回购利率%
     */
    private BigDecimal rebackPrice;

    /**
     * 回购利息
     */
    private BigDecimal intAmt;

    /**
     * 首次全价总额
     */
    private BigDecimal fprice;

    /**
     * 到期全价总额
     */
    private BigDecimal mfprice;

    /**
     * 币种
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
     * 资金账户
     */
    private String trdAcctId;

    /**
     * 操作类型
     */
    private String operateType;

    /**
     * elementId
     */
    private String elementId;

    /**
     * 到期标识 （用于识别是否做过到期交易）
     */
    private String dealFlag;

    @PrimaryKey(PrimaryKey.Field.PK_1)
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

    @NotEmpty(fieldName = "TrdOutRightRepoValuationTrade002.portfolioName", groups = {ValidateGroupEdit.class})
    public String getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId;
    }

    @NotEmpty(fieldName = "TrdOutRightRepoValuationTrade002.portfolioId", groups = {ValidateGroupEdit.class})
    public String getPortfolioName() {
        return portfolioName;
    }

    public void setPortfolioName(String portfolioName) {
        this.portfolioName = portfolioName;
    }

    @NotEmpty(fieldName = "TrdOutRightRepoValuationTrade002.finprodName", groups = {ValidateGroupEdit.class})
    public String getFinprodMarketId() {
        return finprodMarketId;
    }

    public void setFinprodMarketId(String finprodMarketId) {
        this.finprodMarketId = finprodMarketId;
    }

    @NotEmpty(fieldName = "TrdOutRightRepoValuationTrade002.portfolioId", groups = {ValidateGroupEdit.class})
    public String getFinprodName() {
        return finprodName;
    }

    public void setFinprodName(String finprodName) {
        this.finprodName = finprodName;
    }

    @NotEmpty(fieldName = "TrdOutRightRepoValuationTrade002.term", groups = {ValidateGroupEdit.class})
    public BigDecimal getTerm() {
        return term;
    }

    public void setTerm(BigDecimal term) {
        this.term = term;
    }

    @NotEmpty(fieldName = "TrdOutRightRepoValuationTrade002.ps", groups = {ValidateGroupEdit.class})
    public String getPs() {
        return ps;
    }

    public void setPs(String ps) {
        this.ps = ps;
    }

    @NotEmpty(fieldName = "TrdOutRightRepoValuationTrade002.tradeDate", groups = {ValidateGroupEdit.class})
    public Date getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(Date tradeDate) {
        this.tradeDate = tradeDate;
    }

    @NotEmpty(fieldName = "TrdOutRightRepoValuationTrade002.vdate", groups = {ValidateGroupEdit.class})
    public Date getVdate() {
        return vdate;
    }

    public void setVdate(Date vdate) {
        this.vdate = vdate;
    }

    @NotEmpty(fieldName = "TrdOutRightRepoValuationTrade002.mdate", groups = {ValidateGroupEdit.class})
    public Date getMdate() {
        return mdate;
    }

    public void setMdate(Date mdate) {
        this.mdate = mdate;
    }

    @NotEmpty(fieldName = "TrdOutRightRepoValuationTrade002.prinAmt", groups = {ValidateGroupEdit.class})
    public BigDecimal getPrinAmt() {
        return prinAmt;
    }

    public void setPrinAmt(BigDecimal prinAmt) {
        this.prinAmt = prinAmt;
    }

    @NotEmpty(fieldName = "TrdOutRightRepoValuationTrade002.rebackPrice", groups = {ValidateGroupEdit.class})
    public BigDecimal getRebackPrice() {
        return rebackPrice;
    }

    public void setRebackPrice(BigDecimal rebackPrice) {
        this.rebackPrice = rebackPrice;
    }

    public BigDecimal getIntAmt() {
        return intAmt;
    }

    public void setIntAmt(BigDecimal intAmt) {
        this.intAmt = intAmt;
    }

    @NotEmpty(fieldName = "TrdOutRightRepoValuationTrade002.fprice", groups = {ValidateGroupEdit.class})
    public BigDecimal getFprice() {
        return fprice;
    }

    public void setFprice(BigDecimal fprice) {
        this.fprice = fprice;
    }

    @NotEmpty(fieldName = "TrdOutRightRepoValuationTrade002.mfprice", groups = {ValidateGroupEdit.class})
    public BigDecimal getMfprice() {
        return mfprice;
    }

    public void setMfprice(BigDecimal mfprice) {
        this.mfprice = mfprice;
    }

    @NotEmpty(fieldName = "TrdOutRightRepoValuationTrade002.ccy", groups = {ValidateGroupEdit.class})
    public String getCcy() {
        return ccy;
    }

    public void setCcy(String ccy) {
        this.ccy = ccy;
    }

    @NotEmpty(fieldName = "TrdOutRightRepoValuationTrade002.dealFeeAmt", groups = {ValidateGroupEdit.class})
    public BigDecimal getDealFeeAmt() {
        return dealFeeAmt;
    }

    public void setDealFeeAmt(BigDecimal dealFeeAmt) {
        this.dealFeeAmt = dealFeeAmt;
    }

    @NotEmpty(fieldName = "TrdOutRightRepoValuationTrade002.settFeeAmt", groups = {ValidateGroupEdit.class})
    public BigDecimal getSettFeeAmt() {
        return settFeeAmt;
    }

    public void setSettFeeAmt(BigDecimal settFeeAmt) {
        this.settFeeAmt = settFeeAmt;
    }

//    @NotEmpty(fieldName = "TrdOutRightRepoValuationTrade002.otherAmt", groups = {ValidateGroupEdit.class})
    public BigDecimal getOtherAmt() {
        return otherAmt;
    }

    public void setOtherAmt(BigDecimal otherAmt) {
        this.otherAmt = otherAmt;
    }

    @NotEmpty(fieldName = "TrdOutRightRepoValuationTrade002.settleAmt", groups = {ValidateGroupEdit.class})
    public BigDecimal getSettleAmt() {
        return settleAmt;
    }

    public void setSettleAmt(BigDecimal settleAmt) {
        this.settleAmt = settleAmt;
    }

    @NotEmpty(fieldName = "TrdOutRightRepoValuationTrade002.trdAcctId", groups = {ValidateGroupEdit.class})
    public String getTrdAcctId() {
        return trdAcctId;
    }

    public void setTrdAcctId(String trdAcctId) {
        this.trdAcctId = trdAcctId;
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


    /**
     * 买断式回购交易实体类
     */
    private List<TrdOutRightRepoValuationTrade001Dto> selectedItem;

    public List<TrdOutRightRepoValuationTrade001Dto> getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(List<TrdOutRightRepoValuationTrade001Dto> selectedItem) {
        this.selectedItem = selectedItem;
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


    //导出转化起息日
    private  String vdateStr;

    public String getVdateStr() {
        String vdateStr=" ";
        if(getTradeDate() !=null){
            vdateStr=DateUtil.format(getVdate());
        }
        return vdateStr;
    }

    //导出转化到期日
    private  String mdateStr;

    public String getMdateStr() {
        String mdateStr=" ";
        if(getTradeDate() !=null){
            mdateStr=DateUtil.format(getMdate());
        }
        return mdateStr;
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

    public String getPrinAmtStr() {
        DecimalFormat decimalFormat = new DecimalFormat("0.00#");
        return this.getPrinAmt()==null?"0.00":decimalFormat.format(this.getPrinAmt().setScale(2,BigDecimal.ROUND_HALF_UP));
    }

    public String getFpriceStr() {
        DecimalFormat decimalFormat = new DecimalFormat("0.00#");
        return this.getFprice()==null?"0.00":decimalFormat.format(this.getFprice().setScale(2,BigDecimal.ROUND_HALF_UP));
    }
    public String getMfpriceStr() {
        DecimalFormat decimalFormat = new DecimalFormat("0.00#");
        return this.getMfprice()==null?"0.00":decimalFormat.format(this.getMfprice().setScale(2,BigDecimal.ROUND_HALF_UP));
    }


    public String getSettleAmtStr() {
        DecimalFormat decimalFormat = new DecimalFormat("0.00#");
        return this.getSettleAmt()==null?"0.00":decimalFormat.format(this.getSettleAmt().setScale(2,BigDecimal.ROUND_HALF_UP));
    }


    /**
     * 交易对手
     */
    private String counterId;

    public String getCounterId() {
        return counterId;
    }

    public void setCounterId(String counterId) {
        this.counterId = counterId;
    }

    public String getDealFlag() {
        return dealFlag;
    }

    public void setDealFlag(String dealFlag) {
        this.dealFlag = dealFlag;
    }

}
