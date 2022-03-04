/*
 * 文件名：TrdRepoTrade001Dto.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：裴家俊
 * 修改时间：2018年12月18日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
package com.joyintech.tams.valuation.trademanage.trdrepovaluationtrade.dto;

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
import com.joyintech.tams.valuation.trademanage.trdrepovaluationtrade.validator.TrdRepoValuationTrade003DtoValidator;
import com.joyintech.workflow.engine.annotation.PrimaryKey;
import com.joyintech.workflow.engine.dto.ProcessEntity;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

@PageId("TrdRepoValuationTrade003")
@ValidLogic(targetClass = TrdRepoValuationTrade003DtoValidator.class, groups = {ValidateGroupEdit.class, ValidateGroupDelete.class})
public class TrdRepoValuationTrade003Dto extends BaseEntity implements ProcessEntity {

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
     *  资产代码
     */
    private String finprodMarketId;

    /**
     *  资产名称
     */
    private String finprodName;

    /**
     * 交易场所
     */
    private String tradeMarket;


    /**
     * 交易对手
     */
    private String counterId;


    /**
     * 交易类型
     */
    private String ps;


    /**
     * 交易日
     */

    private Date tradeDate;

    /**
     * 资金到账日
     */
    private Date settleDate;


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
     * 提前终止利率%
     */
    private BigDecimal terminateRate;

    /**
     * 逾期利息金额
     */
    private BigDecimal overdueAmt;

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
     * 操作类型
     */
    private String operateType;

    /**
     * elementId
     */
    private String elementId;


    /**
     * 质押式回购交易实体类
     */
    private List<TrdRepoValuationTrade003Dto> selectedItem;

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

    @NotEmpty(fieldName = "TrdRepoValuationTrade003.portfolioId", groups = {ValidateGroupEdit.class})
    public String getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId;
    }

    @NotEmpty(fieldName = "TrdRepoValuationTrade003.portfolioName", groups = {ValidateGroupEdit.class})
    public String getPortfolioName() {
        return portfolioName;
    }

    public void setPortfolioName(String portfolioName) {
        this.portfolioName = portfolioName;
    }

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

    @NotEmpty(fieldName = "TrdRepoValuationTrade003.tradeMarket", groups = {ValidateGroupEdit.class})
    public String getTradeMarket() {
        return tradeMarket;
    }

    public void setTradeMarket(String tradeMarket) {
        this.tradeMarket = tradeMarket;
    }

    @NotEmpty(fieldName = "TrdRepoValuationTrade003.counterId", groups = {ValidateGroupEdit.class})
    public String getCounterId() {
        return counterId;
    }

    public void setCounterId(String counterId) {
        this.counterId = counterId;
    }

    @NotEmpty(fieldName = "TrdRepoValuationTrade003.ps", groups = {ValidateGroupEdit.class})
    public String getPs() {
        return ps;
    }

    public void setPs(String ps) {
        this.ps = ps;
    }

    @NotEmpty(fieldName = "TrdRepoValuationTrade003.tradeDate", groups = {ValidateGroupEdit.class})
    public Date getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(Date tradeDate) {
        this.tradeDate = tradeDate;
    }

    @NotEmpty(fieldName = "TrdRepoValuationTrade003.settleDate", groups = {ValidateGroupEdit.class})
    public Date getSettleDate() {
        return settleDate;
    }

    public void setSettleDate(Date settleDate) {
        this.settleDate = settleDate;
    }

    @NotEmpty(fieldName = "TrdRepoValuationTrade003.prinAmt", groups = {ValidateGroupEdit.class})
    public BigDecimal getPrinAmt() {
        return prinAmt;
    }

    public void setPrinAmt(BigDecimal prinAmt) {
        this.prinAmt = prinAmt;
    }

    @NotEmpty(fieldName = "TrdRepoValuationTrade003.rebackPrice", groups = {ValidateGroupEdit.class})
    public BigDecimal getRebackPrice() {
        return rebackPrice;
    }

    public void setRebackPrice(BigDecimal rebackPrice) {
        this.rebackPrice = rebackPrice;
    }

    @NotEmpty(fieldName = "TrdRepoValuationTrade003.intAmt", groups = {ValidateGroupEdit.class})
    public BigDecimal getIntAmt() {
        return intAmt;
    }

    public void setIntAmt(BigDecimal intAmt) {
        this.intAmt = intAmt;
    }

    public BigDecimal getTerminateRate() {
        return terminateRate;
    }

    public void setTerminateRate(BigDecimal terminateRate) {
        this.terminateRate = terminateRate;
    }

    public BigDecimal getOverdueAmt() {
        return overdueAmt;
    }

    public void setOverdueAmt(BigDecimal overdueAmt) {
        this.overdueAmt = overdueAmt;
    }

    @NotEmpty(fieldName = "TrdRepoValuationTrade003.ccy", groups = {ValidateGroupEdit.class})
    public String getCcy() {
        return ccy;
    }

    public void setCcy(String ccy) {
        this.ccy = ccy;
    }

    @NotEmpty(fieldName = "TrdRepoValuationTrade003.otherAmt", groups = {ValidateGroupEdit.class})
    public BigDecimal getOtherAmt() {
        return otherAmt;
    }

    public void setOtherAmt(BigDecimal otherAmt) {
        this.otherAmt = otherAmt;
    }

    @NotEmpty(fieldName = "TrdRepoValuationTrade003.settleAmt", groups = {ValidateGroupEdit.class})
    public BigDecimal getSettleAmt() {
        return settleAmt;
    }

    public void setSettleAmt(BigDecimal settleAmt) {
        this.settleAmt = settleAmt;
    }

    @NotEmpty(fieldName = "TrdRepoValuationTrade003.trdAcctId", groups = {ValidateGroupEdit.class})
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

    public List<TrdRepoValuationTrade003Dto> getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(List<TrdRepoValuationTrade003Dto> selectedItem) {
        this.selectedItem = selectedItem;
    }

    private Date mdate;

    public Date getMdate() {
        return mdate;
    }


    public void setMdate(Date mdate) {
        this.mdate = mdate;
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
