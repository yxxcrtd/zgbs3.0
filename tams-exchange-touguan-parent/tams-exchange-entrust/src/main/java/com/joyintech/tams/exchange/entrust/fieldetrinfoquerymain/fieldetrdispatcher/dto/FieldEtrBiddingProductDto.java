package com.joyintech.tams.exchange.entrust.fieldetrinfoquerymain.fieldetrdispatcher.dto;

import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.constant.DictCode;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

/**
 * 组合分配：投标产品列表Dto
 * @Author: kchen
 * @Date: 2022/4/19 14:15
 */
public class FieldEtrBiddingProductDto {

    /**
     * 组合ID
     */
    @ApiModelProperty(name="portfolioId",value="组合ID",example="z001")
    private String portfolioId;

    /**
     * 组合名称
     */
    @ApiModelProperty(name="portfolioName",value="组合名称",example="组合名称1")
    private String portfolioName;

    /**
     * 投标标位
     */
    @ApiModelProperty(name="bidPrice",value="投标标位",example="1")
    private BigDecimal bidPrice;

    /**
     * 投标标位字符串
     */
    @ApiModelProperty(name="bidPriceStr",value="投标标位字符串，前端展示用",example="1")
    private String bidPriceStr;

    /**
     * 投标券面总额
     */
    @ApiModelProperty(name="bidPrinAmt",value="投标券面总额",example="1")
    private BigDecimal bidPrinAmt;

    /**
     * 业务模式
     */
    @ApiModelProperty(name="businessModel",value="业务模式",example="A1")
    private String businessModel;

    @ApiModelProperty(name="businessModelStr",value="业务模式字符串",example="A1")
    private String businessModelStr;

    /**
     * 组合汇总投标券面总额
     */
    @ApiModelProperty(name="portfolioTotalBidPrinAmt",value="组合汇总投标券面总额",example="1")
    private BigDecimal portfolioTotalBidPrinAmt;

    /**
     * 证券托管户
     */
    @ApiModelProperty(name="secAcctId",value="证券托管户",example="1")
    private String secAcctId;

    /**
     * 资金账户
     */
    @ApiModelProperty(name="acctNo",value="资金账户",example="A1", required = true)
    private String acctNo;

    /**
     * T日可用现金（元）
     */
    @ApiModelProperty(name="cashT",value="T日可用现金（元）")
    private BigDecimal cashT;

    /**
     * T+1日可用现金（元）
     */
    @ApiModelProperty(name="cashT1",value="T+1日可用现金（元）")
    private BigDecimal cashT1;

    /**
     * 账户现金（元）
     */
    @ApiModelProperty(name="realCash",value="账户现金（元）")
    private BigDecimal realCash;

    /**
     * 券商户可取现金（元）
     */
    @ApiModelProperty(name="secDesirableCash",value="券商户可取现金（元）")
    private BigDecimal secDesirableCash;

    @ApiModelProperty(name="rownum",value="序号")
    private String rownum;


    public String getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId;
    }

    public String getPortfolioName() {
        return portfolioName;
    }

    public void setPortfolioName(String portfolioName) {
        this.portfolioName = portfolioName;
    }

    public BigDecimal getBidPrice() {
        return bidPrice;
    }

    public void setBidPrice(BigDecimal bidPrice) {
        this.bidPrice = bidPrice;
    }

    public BigDecimal getBidPrinAmt() {
        return bidPrinAmt;
    }

    public void setBidPrinAmt(BigDecimal bidPrinAmt) {
        this.bidPrinAmt = bidPrinAmt;
    }

    public String getBusinessModel() {
        return businessModel;
    }

    public void setBusinessModel(String businessModel) {
        this.businessModel = businessModel;
    }

    public BigDecimal getPortfolioTotalBidPrinAmt() {
        return portfolioTotalBidPrinAmt;
    }

    public void setPortfolioTotalBidPrinAmt(BigDecimal portfolioTotalBidPrinAmt) {
        this.portfolioTotalBidPrinAmt = portfolioTotalBidPrinAmt;
    }

    public String getSecAcctId() {
        return secAcctId;
    }

    public void setSecAcctId(String secAcctId) {
        this.secAcctId = secAcctId;
    }

    public String getAcctNo() {
        return acctNo;
    }

    public void setAcctNo(String acctNo) {
        this.acctNo = acctNo;
    }

    public BigDecimal getCashT() {
        return cashT;
    }

    public void setCashT(BigDecimal cashT) {
        this.cashT = cashT;
    }

    public BigDecimal getCashT1() {
        return cashT1;
    }

    public void setCashT1(BigDecimal cashT1) {
        this.cashT1 = cashT1;
    }

    public BigDecimal getRealCash() {
        return realCash;
    }

    public void setRealCash(BigDecimal realCash) {
        this.realCash = realCash;
    }

    public BigDecimal getSecDesirableCash() {
        return secDesirableCash;
    }

    public void setSecDesirableCash(BigDecimal secDesirableCash) {
        this.secDesirableCash = secDesirableCash;
    }

    public String getBusinessModelStr() {
        if (StringUtil.isEmpty(getBusinessModel())) {
            return null;
        }
        return SysDataUtil.getNameFromDataDict(DictCode.业务模式, getBusinessModel());
    }

    public void setBusinessModelStr(String businessModelStr) {
        this.businessModelStr = businessModelStr;
    }

    public String getRownum() {
        return rownum;
    }

    public void setRownum(String rownum) {
        this.rownum = rownum;
    }

    public String getBidPriceStr() {
        return bidPriceStr;
    }

    public void setBidPriceStr(String bidPriceStr) {
        this.bidPriceStr = bidPriceStr;
    }
}
