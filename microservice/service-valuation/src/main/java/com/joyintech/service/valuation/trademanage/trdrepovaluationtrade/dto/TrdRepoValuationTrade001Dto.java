/*
 * 文件名：TrdLendInvestTradeDto.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：杨智
 * 修改时间：2018年10月10日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
package com.joyintech.service.valuation.trademanage.trdrepovaluationtrade.dto;


import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.framework.validation.annotation.NotEmpty;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.common.base.entity.WflTrdProductDeal;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.workflow.engine.annotation.PrimaryKey;
import com.joyintech.workflow.engine.dto.ProcessEntity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


public class TrdRepoValuationTrade001Dto extends WflTrdProductDeal implements ProcessEntity {

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
     * 交易场所
     */
    private String tradeMarket;

    /**
     * 交易类型
     */
    private String ps;

    /**
     * 回购期限
     */
    private BigDecimal term;

    /**
     * 交易日
     */

    private Date tradeDate;

    /**
     * 交割日
     */
    private Date settleDate;

    /**
     * 起息日
     */
    private Date vdate;

    /**
     * 到期日
     */
    private Date mdate;



    /**
     * 实际占款天数
     */
    private BigDecimal actpaydays;


    /**
     * 交易对手
     */
    private String counterId;

    /**
     * 交易本金
     */
    private BigDecimal prinAmt;

    /**
     * 回购利率%
     */
    private BigDecimal rebackPrice;

    /**
     * 应计利息
     */
    private BigDecimal intAmt;

    /**
     * 币种
     */
    private String ccy;

    /**
     * 经手费
     */
    private BigDecimal brokerage;

    /**
     * 结算费
     */
    private BigDecimal stlAmt;

    /**
     * 佣金
     */
    private BigDecimal commission;

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

    public String getElementId() {
        return elementId;
    }

    public void setElementId(String elementId) {
        this.elementId = elementId;
    }

    /**
     * 质押式回购交易实体类
     */
    private List<TrdRepoValuationTrade001Dto> selectedItem;

    public List<TrdRepoValuationTrade001Dto> getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(List<TrdRepoValuationTrade001Dto> selectedItem) {
        this.selectedItem = selectedItem;
    }

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

    @NotEmpty(fieldName = "TrdRepoValuationTrade002.portfolioId", groups = {ValidateGroupEdit.class})
    public String getPortfolioId() {
        return portfolioId;
    }


    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId;
    }

    @NotEmpty(fieldName = "TrdRepoValuationTrade002.portfolioName", groups = {ValidateGroupEdit.class})
    public String getPortfolioName() {
        return portfolioName;
    }

    public void setPortfolioName(String portfolioName) {
        this.portfolioName = portfolioName;
    }

    @NotEmpty(fieldName = "TrdRepoValuationTrade002.tradeMarket", groups = {ValidateGroupEdit.class})
    public String getTradeMarket() {
        return tradeMarket;
    }

    public void setTradeMarket(String tradeMarket) {
        this.tradeMarket = tradeMarket;
    }

    @NotEmpty(fieldName = "TrdRepoValuationTrade002.ps", groups = {ValidateGroupEdit.class})
    public String getPs() {
        return ps;
    }

    public void setPs(String ps) {
        this.ps = ps;
    }

    @NotEmpty(fieldName = "TrdRepoValuationTrade002.term", groups = {ValidateGroupEdit.class})
    public BigDecimal getTerm() {
        return term;
    }

    public void setTerm(BigDecimal term) {
        this.term = term;
    }

    @NotEmpty(fieldName = "TrdRepoValuationTrade002.tradeDate", groups = {ValidateGroupEdit.class})
    public Date getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(Date tradeDate) {
        this.tradeDate = tradeDate;
    }

    @NotEmpty(fieldName = "TrdRepoValuationTrade002.settleDate", groups = {ValidateGroupEdit.class})
    public Date getSettleDate() {
        return settleDate;
    }

    public void setSettleDate(Date settleDate) {
        this.settleDate = settleDate;
    }

    @NotEmpty(fieldName = "TrdRepoValuationTrade002.vdate", groups = {ValidateGroupEdit.class})
    public Date getVdate() {
        return vdate;
    }

    public void setVdate(Date vdate) {
        this.vdate = vdate;
    }

    @NotEmpty(fieldName = "TrdRepoValuationTrade002.mdate", groups = {ValidateGroupEdit.class})
    public Date getMdate() {
        return mdate;
    }

    public void setMdate(Date mdate) {
        this.mdate = mdate;
    }

    @NotEmpty(fieldName = "TrdRepoValuationTrade002.actpaydays", groups = {ValidateGroupEdit.class})
    public BigDecimal getActpaydays() {
        return actpaydays;
    }

    public void setActpaydays(BigDecimal actpaydays) {
        this.actpaydays = actpaydays;
    }

    @NotEmpty(fieldName = "TrdRepoValuationTrade002.counterId", groups = {ValidateGroupEdit.class})
    public String getCounterId() {
        return counterId;
    }

    public void setCounterId(String counterId) {
        this.counterId = counterId;
    }

    @NotEmpty(fieldName = "TrdRepoValuationTrade002.prinAmt", groups = {ValidateGroupEdit.class})
    public BigDecimal getPrinAmt() {
        return prinAmt;
    }

    public void setPrinAmt(BigDecimal prinAmt) {
        this.prinAmt = prinAmt;
    }

    @NotEmpty(fieldName = "TrdRepoValuationTrade002.rebackPrice", groups = {ValidateGroupEdit.class})
    public BigDecimal getRebackPrice() {
        return rebackPrice;
    }

    public void setRebackPrice(BigDecimal rebackPrice) {
        this.rebackPrice = rebackPrice;
    }

    @NotEmpty(fieldName = "TrdRepoValuationTrade002.intAmt", groups = {ValidateGroupEdit.class})
    public BigDecimal getIntAmt() {
        return intAmt;
    }

    public void setIntAmt(BigDecimal intAmt) {
        this.intAmt = intAmt;
    }

    @NotEmpty(fieldName = "TrdRepoValuationTrade002.ccy", groups = {ValidateGroupEdit.class})
    public String getCcy() {
        return ccy;
    }

    public void setCcy(String ccy) {
        this.ccy = ccy;
    }

    @NotEmpty(fieldName = "TrdRepoValuationTrade002.brokerage", groups = {ValidateGroupEdit.class})
    public BigDecimal getBrokerage() {
        return brokerage;
    }

    public void setBrokerage(BigDecimal brokerage) {
        this.brokerage = brokerage;
    }

    @NotEmpty(fieldName = "TrdRepoValuationTrade002.stlAmt", groups = {ValidateGroupEdit.class})
    public BigDecimal getStlAmt() {
        return stlAmt;
    }

    public void setStlAmt(BigDecimal stlAmt) {
        this.stlAmt = stlAmt;
    }

    @NotEmpty(fieldName = "TrdRepoValuationTrade002.commission", groups = {ValidateGroupEdit.class})
    public BigDecimal getCommission() {
        return commission;
    }

    public void setCommission(BigDecimal commission) {
        this.commission = commission;
    }

    @NotEmpty(fieldName = "TrdRepoValuationTrade002.dealFeeAmt", groups = {ValidateGroupEdit.class})
    public BigDecimal getDealFeeAmt() {
        return dealFeeAmt;
    }

    public void setDealFeeAmt(BigDecimal dealFeeAmt) {
        this.dealFeeAmt = dealFeeAmt;
    }

    @NotEmpty(fieldName = "TrdRepoValuationTrade002.settFeeAmt", groups = {ValidateGroupEdit.class})
    public BigDecimal getSettFeeAmt() {
        return settFeeAmt;
    }

    public void setSettFeeAmt(BigDecimal settFeeAmt) {
        this.settFeeAmt = settFeeAmt;
    }

    @NotEmpty(fieldName = "TrdRepoValuationTrade002.otherAmt", groups = {ValidateGroupEdit.class})
    public BigDecimal getOtherAmt() {
        return otherAmt;
    }

    public void setOtherAmt(BigDecimal otherAmt) {
        this.otherAmt = otherAmt;
    }

    @NotEmpty(fieldName = "TrdRepoValuationTrade002.settleAmt", groups = {ValidateGroupEdit.class})
    public BigDecimal getSettleAmt() {
        return settleAmt;
    }

    public void setSettleAmt(BigDecimal settleAmt) {
        this.settleAmt = settleAmt;
    }

    @NotEmpty(fieldName = "TrdRepoValuationTrade002.trdAcctId", groups = {ValidateGroupEdit.class})
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

    /**
     * 市场机构代码  资产代码
     */
    private String finprodMarketId;

    /**
     * 金融产品名称  资产名称
     */
    private String finprodName;

    public String getFinprodMarketId() {
        return finprodMarketId;
    }

    public void setFinprodMarketId(String finprodMarketId) {
        this.finprodMarketId = finprodMarketId;
    }

    public String getFinprodName() {
        return finprodName;
    }

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

    /**
     * 取得 交易场所str
     *
     * @return 交易场所
     */
    public String getTradeMarketStr() {
        return StringUtil.isNotEmpty(SysDataUtil.getNameFromDataDict(DictCode.交易场所, tradeMarket))?
                SysDataUtil.getNameFromDataDict(DictCode.交易场所, tradeMarket):tradeMarket;
    }


    //导出转化起息日
    private  String vdateStr;

    public String getVdateStr() {
        String vdateStr=" ";
        if(getTradeDate() !=null){
            vdateStr= DateUtil.format(getVdate());
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


    /**
     * 回购类型（H01：质押式协议回购；H02：质押式竞价回购）
     */
    private String repoType;

    public String getRepoType() {
        return repoType;
    }

    public void setRepoType(String repoType) {
        this.repoType = repoType;
    }

}
