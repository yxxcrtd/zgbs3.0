/*
 * 文件名：TrdLendInvestTradeDto.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：杨智
 * 修改时间：2018年10月10日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
package com.joyintech.tams.valuation.trademanage.trdlendvaluationtrade.dto;

import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.base.dto.UploadFile;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.framework.validation.annotation.NotEmpty;
import com.joyintech.framework.validation.annotation.ValidLogic;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.common.base.entity.*;

import com.joyintech.tams.constant.DictCode;

import com.joyintech.tams.valuation.trademanage.trdlendvaluationtrade.validator.TrdLendValuationTradeDtoValidator;
import com.joyintech.workflow.engine.annotation.PrimaryKey;
import com.joyintech.workflow.engine.dto.ProcessEntity;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

@PageId("TrdLendInvestTrade002")
@ValidLogic(targetClass = TrdLendValuationTradeDtoValidator.class)
public class TrdLendValuationTradeDto extends BaseEntity implements ProcessEntity {

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


    public String getBusiPs() {
        return busiPs;
    }

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
    @NotEmpty(fieldName = "TrdLendValuationTrade002.ps", groups = {ValidateGroupEdit.class})
    private String ps;

    public String getPs() {
        return ps;
    }

    public void setPs(String ps) {
        this.ps = ps;
    }

    /**
     * 取得 交易方向str
     *
     * @return 交易方向
     */
    public String getPsStr() {
        String psName="";
        if(StringUtil.isNotEmpty(ps)){
            if(ps.equals("01")){
                psName = "拆入";
            }else if(ps.equals("02")){
                psName = "拆出";
            }else if(ps.equals("03")){
                psName = "拆入到期";
            }else if(ps.equals("04")){
                psName = "拆出到期";
            }
        }
        return psName;
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

    //导出保留小数位

    //本金金额  2位
    public String getPrinAmtStr() {
        DecimalFormat decimalFormat = new DecimalFormat("0.00#");
        return this.getPrinAmt()==null?"0.00":decimalFormat.format(this.getPrinAmt().setScale(2,BigDecimal.ROUND_HALF_UP));
    }



    //应计利息 2位
    public String getIntAmtStr() {
        DecimalFormat decimalFormat = new DecimalFormat("0.00#");
        return this.getIntAmt()==null?"0.00":decimalFormat.format(this.getIntAmt().setScale(2,BigDecimal.ROUND_HALF_UP));
    }

    //结算金额 2位
    public String getSettleAmtStr() {
        DecimalFormat decimalFormat = new DecimalFormat("0.00#");
        return this.getSettleAmt()==null?"0.00":decimalFormat.format(this.getSettleAmt().setScale(2,BigDecimal.ROUND_HALF_UP));
    }

    //本币交易金额 2位
    public String getDealAmtStr() {
        DecimalFormat decimalFormat = new DecimalFormat("0.00#");
        return this.getDealAmt()==null?"0.00":decimalFormat.format(this.getDealAmt().setScale(2,BigDecimal.ROUND_HALF_UP));
    }

}
