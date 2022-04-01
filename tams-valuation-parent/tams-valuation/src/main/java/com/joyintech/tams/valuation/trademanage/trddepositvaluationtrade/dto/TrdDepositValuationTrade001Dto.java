/*
 * 文件名：TrdDepositInvestTrade001Dto.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：fanshuanghu
 * 修改时间：2018年10月10日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.valuation.trademanage.trddepositvaluationtrade.dto;

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
import com.joyintech.workflow.engine.annotation.PrimaryKey;
import com.joyintech.workflow.engine.dto.ProcessEntity;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

/**
 * 存款类投资交易实体类
 * @author fanshuanghu
 * @version 1.0 2018年10月10日
 * @see TrdDepositValuationTrade001Dto
 * @since 1.0
 */
@PageId("TrdDepositValuationTrade002")
@ValidLogic(targetClass = TrdDepositValuationTrade001Validator.class, groups = {ValidateGroupEdit.class,
    ValidateGroupDelete.class})
public class TrdDepositValuationTrade001Dto extends BaseEntity implements ProcessEntity {
    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    private static final long serialVersionUID = -4814792523354012630L;



    /**
     * 交易方向
     */
    private String ps;

    /**
     * 交易编号
     */
    private String tradeId;

    /**
     * 外部交易编号
     */
    private String outTradeId;

    public String getOutTradeId() {
        return outTradeId;
    }

    public void setOutTradeId(String outTradeId) {
        this.outTradeId = outTradeId;
    }

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


    //交易日
    private Date tradeDate;

    /**
     * 交割日
     */
    private Date settDate;

    /**
     * 交易本金
     */

    private BigDecimal prinAmt;

    @NotEmpty(fieldName = "TrdDepositValuation001.prinAmt", groups = {ValidateGroupEdit.class})
    public BigDecimal getPrinAmt() {
        return prinAmt;
    }

    public void setPrinAmt(BigDecimal prinAmt) {
        this.prinAmt = prinAmt;
    }

    //其他费用
    private BigDecimal otherAmt;

    @NotEmpty(fieldName = "TrdDepositValuation001.otherAmt", groups = {ValidateGroupEdit.class})
    public BigDecimal getOtherAmt() {
        return otherAmt;
    }

    public void setOtherAmt(BigDecimal otherAmt) {
        this.otherAmt = otherAmt;
    }



    /**
     * 应记利息总额
     */
    private BigDecimal intAmt;

    /**
     * 交易金额  本币交易金额
     */
    private BigDecimal dealAmt;

    /**
     * 交易对手
     */
    private String counterId;

    /**
     * 币种
     */
    private String ccy;

    //结算金额
    private BigDecimal settleAmt;

    @NotEmpty(fieldName = "TrdDepositValuation001.settleAmt", groups = {ValidateGroupEdit.class})
    public BigDecimal getSettleAmt() {
        return settleAmt;
    }

    public void setSettleAmt(BigDecimal settleAmt) {
        this.settleAmt = settleAmt;
    }



    @NotEmpty(fieldName = "TrdDepositValuation001.tradeDate", groups = {ValidateGroupEdit.class})
    public Date getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(Date tradeDate) {
        this.tradeDate = tradeDate;
    }


    @NotEmpty(fieldName = "TrdDepositValuation001.ps", groups = {ValidateGroupEdit.class})
    public String getPs() {
        return ps;
    }

    public void setPs(String ps) {
        this.ps = ps;
    }

    @PrimaryKey(PrimaryKey.Field.PK_1)
//    @NotEmpty(fieldName = "TrdDepositValuation001.tradeId", groups = {ValidateGroupEdit.class})
    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }


    @NotEmpty(fieldName = "TrdDepositValuation001.settDate", groups = {ValidateGroupEdit.class})
    public Date getSettDate() {
        return settDate;
    }

    public void setSettDate(Date settDate) {
        this.settDate = settDate;
    }



    @NotEmpty(fieldName = "TrdDepositValuation001.intAmt", groups = {ValidateGroupEdit.class})
    public BigDecimal getIntAmt() {
        return intAmt;
    }

    public void setIntAmt(BigDecimal intAmt) {
        this.intAmt = intAmt;
    }

//    @NotEmpty(fieldName = "TrdDepositValuation001.dealAmt", groups = {ValidateGroupEdit.class})
    public BigDecimal getDealAmt() {
        return dealAmt;
    }

    public void setDealAmt(BigDecimal dealAmt) {
        this.dealAmt = dealAmt;
    }

    public String getCounterId() {
        return counterId;
    }

    public void setCounterId(String counterId) {
        this.counterId = counterId;
    }

    @NotEmpty(fieldName = "TrdDepositValuationTrade002.ccy", groups = {ValidateGroupEdit.class})
    public String getCcy() {
        return ccy;
    }

    public void setCcy(String ccy) {
        this.ccy = ccy;
    }



    //资金账户
    private String trdAcctId;
    @NotEmpty(fieldName = "TrdDepositValuation001.trdAcctId", groups = {ValidateGroupEdit.class})
    public String getTrdAcctId() {
        return trdAcctId;
    }

    public void setTrdAcctId(String trdAcctId) {
        this.trdAcctId = trdAcctId;
    }






    /**
     * 操作类型
     */
    private String operateType;
    /**
     * elementId
     */
    private String elementId;



    /**
     * 获取 finprodMarketId
     * @return finprodMarketId.
     */
    @NotEmpty(fieldName = "TrdDepositValuation001.finprodMarketId", groups = {ValidateGroupEdit.class})
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

    /**
     * 获取 operatType
     * @return operatType.
     */
    public String getOperateType() {
        return operateType;
    }

    /**
     * 设置 operatType
     * @param operateType operatType
     */
    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }

    /**
     * 获取 portfolioId
     * @return portfolioId.
     */
    @NotEmpty(fieldName = "TrdDepositValuation001.portfolioId", groups = {ValidateGroupEdit.class})
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
    @NotEmpty(fieldName = "TrdDepositValuation001.portfolioName", groups = {ValidateGroupEdit.class})
    public String getPortfolioName() {
        return portfolioName;
    }

    /**
     * 设置 portfolioName
     * @param portfolioName portfolioName
     */
    public void setPortfolioName(String portfolioName) {
        this.portfolioName = portfolioName;
    }


    /**
     * 获取 finprodName
     * @return finprodName.
     */
    @NotEmpty(fieldName = "TrdDepositValuation001.finprodName", groups = {ValidateGroupEdit.class})
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


    //导出转化交易日
    private  String tradeDateStr;

    public String getTradeDateStr() {
        String tradeDateStr=" ";
        if(getTradeDate() !=null){
            tradeDateStr=DateUtil.format(getTradeDate());
        }
        return tradeDateStr;
    }

    //导出转化交割日
    private String settDateStr;
    public String getSettDateStr(){
        String settDateStr=" ";
        if(getSettDate() !=null){
            settDateStr=DateUtil.format(getSettDate());
        }
        return settDateStr;
    }


    /**
     * 存款类交易实体类
     */
    private List<TrdDepositValuationTrade001Dto> selectedItem;

    public List<TrdDepositValuationTrade001Dto> getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(List<TrdDepositValuationTrade001Dto> selectedItem) {
        this.selectedItem = selectedItem;
    }

    //导出保留小数位

    //交易本金
    public String getPrinAmtStr() {
        DecimalFormat decimalFormat = new DecimalFormat("0.00#");
        return this.getPrinAmt()==null?"0.00":decimalFormat.format(this.getPrinAmt().setScale(2,BigDecimal.ROUND_HALF_UP));
    }

    //应计利息
    public String getIntAmtStr() {
        DecimalFormat decimalFormat = new DecimalFormat("0.00#");
        return this.getIntAmt()==null?"0.00":decimalFormat.format(this.getIntAmt().setScale(2,BigDecimal.ROUND_HALF_UP));
    }

    //结算金额
    public String getSettleAmtStr() {
        DecimalFormat decimalFormat = new DecimalFormat("0.00#");
        return this.getSettleAmt()==null?"0.00":decimalFormat.format(this.getSettleAmt().setScale(2,BigDecimal.ROUND_HALF_UP));
    }

    //本币交易金额 dealAmt
    public String getDealAmtStr() {
        DecimalFormat decimalFormat = new DecimalFormat("0.00#");
        return this.getDealAmt()==null?"0.00":decimalFormat.format(this.getDealAmt().setScale(2,BigDecimal.ROUND_HALF_UP));
    }
}
