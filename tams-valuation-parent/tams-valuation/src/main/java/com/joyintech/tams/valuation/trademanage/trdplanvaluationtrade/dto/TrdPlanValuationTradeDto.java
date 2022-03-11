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

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.framework.validation.annotation.NotEmpty;
import com.joyintech.framework.validation.annotation.ValidLogic;
import com.joyintech.framework.validation.groups.ValidateGroupDelete;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.valuation.trademanage.trdplanvaluationtrade.validate.TrdPlanValuationTradeDtoValidator;
import com.joyintech.workflow.engine.annotation.PrimaryKey;
import com.joyintech.workflow.engine.dto.ProcessEntity;

import java.math.BigDecimal;
import java.text.DecimalFormat;
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
public class TrdPlanValuationTradeDto extends BaseEntity implements ProcessEntity {

    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    private static final long serialVersionUID = 5309538894816479429L;

    /**
     * 操作类型
     */
    private String operateType;

    private String pageModel;

    /**
     * 选择数据
     */
    private List<TrdPlanValuationTradeDto> selectedItem;

    /**
     * 要素id
     */
    private String elementId;

    /**
     * 交易编号
     */
    @PrimaryKey(value= PrimaryKey.Field.PK_1)
    private String tradeId;

    /**
     * 外部成交编号
     */
    private String outTradeId;

    /**
     * 产品代码
     */
    private String prodId;

    /**
     * 产品名称
     */
    private String prodName;

    /**
     * 资产代码
     */
    private String assetId;

    /**
     * 资产名称
     */
    private String assetAbbr;

    /**
     * 收益类型
     */
    private String profitType;

    /**
     * 交易类型
     */
    private String ps;
    
    /**
     * 币种
     */
    private String ccy;

    /**
     * 交易日期
     */
    private Date tradeDate;

    /**
     * 结算日期
     */
    private Date settleDate;

    /**
     * 成交数量
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal dealNum;

    /**
     * 成交金额
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal tradeAmt;

    /**
     * 资金账户（交易信息）
     */
    private String trdMyAcctNo;

    /**
     * 投资顾问费
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal advFeeOut;

    /**
     * 其他费用
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal otherAmt;

    /**
     * 结算金额
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal settleAmt;

    /**
     * 申请日期（认购）
     */
    private Date applyDate;

    /**
     * 结算日期（认购）
     */
    private Date subSettleDate;

    /**
     * 申请金额（认购）
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal applyAmt;

    /**
     * 资金账户（认购）
     */
    private String subMyAcctNo;

    /**
     * 申请日期（申购）
     */
    private Date purApplyDate;

    /**
     * 结算日期（申购）
     */
    private Date purSettleDate;

    /**
     * 申请金额（申购）
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal purApplyAmt;

    /**
     * 资金账户（申购）
     */
    private String purMyAcctNo;

    /**
     * 确认日期（认购）
     */
    private Date subConfirmDate;

    /**
     * 确认数量（认购）
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal subConfirmNum;

    /**
     * 确认金额（认购）
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal subConfirmAmt;

    /**
     * 募集期利息（认购）
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal raiseInterest;

    /**
     * 认购费（认购）
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal subFee;

    /**
     * 资金账户（认购）
     */
    private String subConfirmMyAcctNo;

    /**
     * 确认日期（申购）
     */
    private Date purConfirmDate;

    /**
     * 确认数量（申购）
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal purConfirmNum;

    /**
     * 确认金额（申购）
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal purConfirmAmt;

    /**
     * 单位净值（申购）
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal purUnitValue;

    /**
     * 利息收入（申购）
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal interestIncome;

    /**
     * 申购费（申购）
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal purFee;

    /**
     * 资金账户（申购）
     */
    private String purConfirmMyAcctNo;

    /**
     * 确认日期（赎回）
     */
    private Date redConfirmDate;

    /**
     * 确认数量（赎回）
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal redConfirmNum;

    /**
     * 确认金额（赎回）
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal redConfirmAmt;

    /**
     * 单位净值（赎回）
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal redUnitValue;

    /**
     * 赎回费（赎回）
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal redFee;

    /**
     * 到账日期（赎回）
     */
    private Date arriveDate;

    /**
     * 资金账户（赎回）
     */
    private String redMyAcctNo;

    /**
     * 到账金额
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal tradeAmtArrive;

    public String getOperateType() {
        return operateType;
    }

    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }

    public String getPageModel() {
        return pageModel;
    }

    public void setPageModel(String pageModel) {
        this.pageModel = pageModel;
    }

    public List<TrdPlanValuationTradeDto> getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(List<TrdPlanValuationTradeDto> selectedItem) {
        this.selectedItem = selectedItem;
    }

    public String getElementId() {
        return elementId;
    }

    public void setElementId(String elementId) {
        this.elementId = elementId;
    }

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

    @NotEmpty(fieldName = "TrdPlanValuationTrade002.prodId" , groups = { ValidateGroupEdit.class })
    public String getProdId() {
        return prodId;
    }

    public void setProdId(String prodId) {
        this.prodId = prodId;
    }

    @NotEmpty(fieldName = "TrdPlanValuationTrade002.prodName" , groups = { ValidateGroupEdit.class })
    public String getProdName() {
        return prodName;
    }

    public void setProdName(String prodName) {
        this.prodName = prodName;
    }

    @NotEmpty(fieldName = "TrdPlanValuationTrade002.assetId" , groups = { ValidateGroupEdit.class })
    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    @NotEmpty(fieldName = "TrdPlanValuationTrade002.assetAbbr" , groups = { ValidateGroupEdit.class })
    public String getAssetAbbr() {
        return assetAbbr;
    }

    public void setAssetAbbr(String assetAbbr) {
        this.assetAbbr = assetAbbr;
    }

    @NotEmpty(fieldName = "TrdPlanValuationTrade002.ps" , groups = { ValidateGroupEdit.class })
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
        return StringUtil.isNotEmpty(SysDataUtil.getNameFromDataDict(DictCode.交易方向, ps))?
                SysDataUtil.getNameFromDataDict(DictCode.交易方向, ps):ps;
    }

    @NotEmpty(fieldName = "TrdPlanValuationTrade002.ccy" , groups = { ValidateGroupEdit.class })
    public String getCcy() {
        return ccy;
    }

    public void setCcy(String ccy) {
        this.ccy = ccy;
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

    public Date getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(Date tradeDate) {
        this.tradeDate = tradeDate;
    }

    public String getTradeDateStr() {
        return DateUtil.format(this.getTradeDate());
    }

    public Date getSettleDate() {
        return settleDate;
    }

    public void setSettleDate(Date settleDate) {
        this.settleDate = settleDate;
    }

    public String getSettleDateStr() {
        return DateUtil.format(this.getSettleDate());
    }

    public BigDecimal getDealNum() {
        return dealNum;
    }

    public void setDealNum(BigDecimal dealNum) {
        this.dealNum = dealNum;
    }

    public BigDecimal getTradeAmt() {
        return tradeAmt;
    }

    public void setTradeAmt(BigDecimal tradeAmt) {
        this.tradeAmt = tradeAmt;
    }

    public String getTrdMyAcctNo() {
        return trdMyAcctNo;
    }

    public void setTrdMyAcctNo(String trdMyAcctNo) {
        this.trdMyAcctNo = trdMyAcctNo;
    }

    public BigDecimal getAdvFeeOut() {
        return advFeeOut;
    }

    public void setAdvFeeOut(BigDecimal advFeeOut) {
        this.advFeeOut = advFeeOut;
    }

    public BigDecimal getOtherAmt() {
        return otherAmt;
    }

    public void setOtherAmt(BigDecimal otherAmt) {
        this.otherAmt = otherAmt;
    }

    public BigDecimal getSettleAmt() {
        return settleAmt;
    }

    public void setSettleAmt(BigDecimal settleAmt) {
        this.settleAmt = settleAmt;
    }

    public Date getApplyDate() {
        return applyDate;
    }

    public void setApplyDate(Date applyDate) {
        this.applyDate = applyDate;
    }

    public Date getSubSettleDate() {
        return subSettleDate;
    }

    public void setSubSettleDate(Date subSettleDate) {
        this.subSettleDate = subSettleDate;
    }

    public BigDecimal getApplyAmt() {
        return applyAmt;
    }

    public void setApplyAmt(BigDecimal applyAmt) {
        this.applyAmt = applyAmt;
    }

    public String getSubMyAcctNo() {
        return subMyAcctNo;
    }

    public void setSubMyAcctNo(String subMyAcctNo) {
        this.subMyAcctNo = subMyAcctNo;
    }

    public Date getPurApplyDate() {
        return purApplyDate;
    }

    public void setPurApplyDate(Date purApplyDate) {
        this.purApplyDate = purApplyDate;
    }

    public Date getPurSettleDate() {
        return purSettleDate;
    }

    public void setPurSettleDate(Date purSettleDate) {
        this.purSettleDate = purSettleDate;
    }

    public BigDecimal getPurApplyAmt() {
        return purApplyAmt;
    }

    public void setPurApplyAmt(BigDecimal purApplyAmt) {
        this.purApplyAmt = purApplyAmt;
    }

    public String getPurMyAcctNo() {
        return purMyAcctNo;
    }

    public void setPurMyAcctNo(String purMyAcctNo) {
        this.purMyAcctNo = purMyAcctNo;
    }

    public Date getSubConfirmDate() {
        return subConfirmDate;
    }

    public void setSubConfirmDate(Date subConfirmDate) {
        this.subConfirmDate = subConfirmDate;
    }

    public BigDecimal getSubConfirmNum() {
        return subConfirmNum;
    }

    public void setSubConfirmNum(BigDecimal subConfirmNum) {
        this.subConfirmNum = subConfirmNum;
    }

    public BigDecimal getSubConfirmAmt() {
        return subConfirmAmt;
    }

    public void setSubConfirmAmt(BigDecimal subConfirmAmt) {
        this.subConfirmAmt = subConfirmAmt;
    }

    public BigDecimal getRaiseInterest() {
        return raiseInterest;
    }

    public void setRaiseInterest(BigDecimal raiseInterest) {
        this.raiseInterest = raiseInterest;
    }

    public BigDecimal getSubFee() {
        return subFee;
    }

    public void setSubFee(BigDecimal subFee) {
        this.subFee = subFee;
    }

    public String getSubConfirmMyAcctNo() {
        return subConfirmMyAcctNo;
    }

    public void setSubConfirmMyAcctNo(String subConfirmMyAcctNo) {
        this.subConfirmMyAcctNo = subConfirmMyAcctNo;
    }

    public Date getPurConfirmDate() {
        return purConfirmDate;
    }

    public void setPurConfirmDate(Date purConfirmDate) {
        this.purConfirmDate = purConfirmDate;
    }

    public BigDecimal getPurConfirmNum() {
        return purConfirmNum;
    }

    public void setPurConfirmNum(BigDecimal purConfirmNum) {
        this.purConfirmNum = purConfirmNum;
    }

    public BigDecimal getPurConfirmAmt() {
        return purConfirmAmt;
    }

    public void setPurConfirmAmt(BigDecimal purConfirmAmt) {
        this.purConfirmAmt = purConfirmAmt;
    }

    public BigDecimal getPurUnitValue() {
        return purUnitValue;
    }

    public void setPurUnitValue(BigDecimal purUnitValue) {
        this.purUnitValue = purUnitValue;
    }

    public BigDecimal getInterestIncome() {
        return interestIncome;
    }

    public void setInterestIncome(BigDecimal interestIncome) {
        this.interestIncome = interestIncome;
    }

    public BigDecimal getPurFee() {
        return purFee;
    }

    public void setPurFee(BigDecimal purFee) {
        this.purFee = purFee;
    }

    public String getPurConfirmMyAcctNo() {
        return purConfirmMyAcctNo;
    }

    public void setPurConfirmMyAcctNo(String purConfirmMyAcctNo) {
        this.purConfirmMyAcctNo = purConfirmMyAcctNo;
    }

    public Date getRedConfirmDate() {
        return redConfirmDate;
    }

    public void setRedConfirmDate(Date redConfirmDate) {
        this.redConfirmDate = redConfirmDate;
    }

    public BigDecimal getRedConfirmNum() {
        return redConfirmNum;
    }

    public void setRedConfirmNum(BigDecimal redConfirmNum) {
        this.redConfirmNum = redConfirmNum;
    }

    public BigDecimal getRedConfirmAmt() {
        return redConfirmAmt;
    }

    public void setRedConfirmAmt(BigDecimal redConfirmAmt) {
        this.redConfirmAmt = redConfirmAmt;
    }

    public BigDecimal getRedUnitValue() {
        return redUnitValue;
    }

    public void setRedUnitValue(BigDecimal redUnitValue) {
        this.redUnitValue = redUnitValue;
    }

    public BigDecimal getRedFee() {
        return redFee;
    }

    public void setRedFee(BigDecimal redFee) {
        this.redFee = redFee;
    }

    public Date getArriveDate() {
        return arriveDate;
    }

    public void setArriveDate(Date arriveDate) {
        this.arriveDate = arriveDate;
    }

    public String getRedMyAcctNo() {
        return redMyAcctNo;
    }

    public void setRedMyAcctNo(String redMyAcctNo) {
        this.redMyAcctNo = redMyAcctNo;
    }

    public BigDecimal getTradeAmtArrive() {
        return tradeAmtArrive;
    }

    public void setTradeAmtArrive(BigDecimal tradeAmtArrive) {
        this.tradeAmtArrive = tradeAmtArrive;
    }

    public String getProfitType() {
        return profitType;
    }

    public void setProfitType(String profitType) {
        this.profitType = profitType;
    }

    public String getTradeAmtStr() {
        DecimalFormat decimalFormat = new DecimalFormat("0.00#");
        return this.getTradeAmt()==null?"0.00":decimalFormat.format(this.getTradeAmt().setScale(2,BigDecimal.ROUND_HALF_UP));
    }

    public String getDealNumStr() {
        DecimalFormat decimalFormat = new DecimalFormat("0.00#");
        return this.getDealNum()==null?"0.00":decimalFormat.format(this.getDealNum().setScale(2,BigDecimal.ROUND_HALF_UP));
    }

    public String getSubFeeStr() {
        DecimalFormat decimalFormat = new DecimalFormat("0.00#");
        return this.getSubFee()==null?"0.00":decimalFormat.format(this.getSubFee().setScale(2,BigDecimal.ROUND_HALF_UP));
    }

    public String getPurFeeStr() {
        DecimalFormat decimalFormat = new DecimalFormat("0.00#");
        return this.getPurFee()==null?"0.00":decimalFormat.format(this.getPurFee().setScale(2,BigDecimal.ROUND_HALF_UP));
    }

    public String getRedFeeStr() {
        DecimalFormat decimalFormat = new DecimalFormat("0.00#");
        return this.getRedFee()==null?"0.00":decimalFormat.format(this.getRedFee().setScale(2,BigDecimal.ROUND_HALF_UP));
    }

    public String getSettleAmtStr() {
        DecimalFormat decimalFormat = new DecimalFormat("0.00#");
        return this.getSettleAmt()==null?"0.00":decimalFormat.format(this.getSettleAmt().setScale(2,BigDecimal.ROUND_HALF_UP));
    }

}
