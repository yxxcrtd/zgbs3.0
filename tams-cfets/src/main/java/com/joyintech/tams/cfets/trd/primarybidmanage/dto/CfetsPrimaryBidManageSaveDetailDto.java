package com.joyintech.tams.cfets.trd.primarybidmanage.dto;

import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.constant.DictCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 中标结果维护分配明细
 * @author wangyang1
 * @version 1.0 2022年4月14日
 * @see CfetsPrimaryBidManageSaveDetailDto
 * @since 1.0
 */
@ApiModel(value="债券新建画面表单",description="债券新建画面表单")
public class CfetsPrimaryBidManageSaveDetailDto {
    /**
     * 汇总中标编号
     */
    private String totalWinId;

    /**中标方式*/
    @ApiModelProperty(name="bidType",value="中标方式")
    private String bidType;

    private String bidTypeStr;
    /**自动分配*/
    /**指令代码*/
    @ApiModelProperty(name="entrustId",value="指令代码")
    private String entrustId;

    /**组合名称*/
    @ApiModelProperty(name="portfolioId",value="组合名称")
    private String portfolioId;

    /**汇总投标券面总额（万元）*/
    @ApiModelProperty(name="totalBidPrinamt",value="汇总投标券面总额（万元）")
    private BigDecimal totalBidPrinamt;

    /**理论中标券面总额（万元）*/
    @ApiModelProperty(name="theWinPrinamt",value="理论中标券面总额（万元）")
    private BigDecimal theWinPrinamt;

    /**实际中标券面总额（万元）*/
    @ApiModelProperty(name="actWinPrinamt",value="实际中标券面总额（万元）")
    private BigDecimal actWinPrinamt;

    /**投标标位*/
    @ApiModelProperty(name="bidPrice",value="投标标位")
    private BigDecimal bidPrice;

    /**中标标位*/
    @ApiModelProperty(name="winPrice",value="中标标位")
    private BigDecimal winPrice;

    /**中标净价（元）*/
    @ApiModelProperty(name="winCprice",value="中标净价（元）")
    private BigDecimal winCprice;

    /**应计利息（元）*/
    @ApiModelProperty(name="intAmt",value="应计利息（元）")
    private BigDecimal intAmt;

    /**缴款金额（元）*/
    @ApiModelProperty(name="payAmt",value="缴款金额（元）")
    private BigDecimal payAmt;

    /**结算服务费（元）*/
    @ApiModelProperty(name="settFeeAmt",value="结算服务费（元）")
    private BigDecimal settFeeAmt;

    /**业务模式*/
    @ApiModelProperty(name="invAim",value="业务模式")
    private String invAim;

    /**证券账户*/
    @ApiModelProperty(name="secAcctId",value="证券账户")
    private String secAcctId;

    /**投资经理*/
    @ApiModelProperty(name="invManagerId",value="投资经理")
    private String invManagerId;

    /**资金账户*/
    @ApiModelProperty(name="acctNo",value="资金账户")
    private String acctNo;

    /**净价*/
    @ApiModelProperty(name="unitCprice",value="净价（元）")
    private BigDecimal unitCprice;

    /**交易金额*/
    @ApiModelProperty(name="tradeAmt",value="交易金额（元）")
    private BigDecimal tradeAmt;

    @ApiModelProperty(name="bobpId",value="组合+指令代码")
    private String bobpId;

    private String finprodId;

    public String getTotalWinId() {
        return totalWinId;
    }

    public void setTotalWinId(String totalWinId) {
        this.totalWinId = totalWinId;
    }

    public String getBidType() {
        return bidType;
    }

    public void setBidType(String bidType) {
        this.bidType = bidType;
    }

    public String getEntrustId() {
        return entrustId;
    }

    public void setEntrustId(String entrustId) {
        this.entrustId = entrustId;
    }

    public String getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId;
    }

    public BigDecimal getTotalBidPrinamt() {
        return totalBidPrinamt;
    }

    public void setTotalBidPrinamt(BigDecimal totalBidPrinamt) {
        this.totalBidPrinamt = totalBidPrinamt;
    }

    public BigDecimal getTheWinPrinamt() {
        return theWinPrinamt;
    }

    public void setTheWinPrinamt(BigDecimal theWinPrinamt) {
        this.theWinPrinamt = theWinPrinamt;
    }

    public BigDecimal getActWinPrinamt() {
        return actWinPrinamt;
    }

    public void setActWinPrinamt(BigDecimal actWinPrinamt) {
        this.actWinPrinamt = actWinPrinamt;
    }

    public BigDecimal getBidPrice() {
        return bidPrice;
    }

    public void setBidPrice(BigDecimal bidPrice) {
        this.bidPrice = bidPrice;
    }

    public BigDecimal getWinPrice() {
        return winPrice;
    }

    public void setWinPrice(BigDecimal winPrice) {
        this.winPrice = winPrice;
    }

    public BigDecimal getWinCprice() {
        return winCprice;
    }

    public void setWinCprice(BigDecimal winCprice) {
        this.winCprice = winCprice;
    }

    public BigDecimal getIntAmt() {
        return intAmt;
    }

    public void setIntAmt(BigDecimal intAmt) {
        this.intAmt = intAmt;
    }

    public BigDecimal getPayAmt() {
        return payAmt;
    }

    public void setPayAmt(BigDecimal payAmt) {
        this.payAmt = payAmt;
    }

    public BigDecimal getSettFeeAmt() {
        return settFeeAmt;
    }

    public void setSettFeeAmt(BigDecimal settFeeAmt) {
        this.settFeeAmt = settFeeAmt;
    }

    public String getInvAim() {
        return invAim;
    }

    public void setInvAim(String invAim) {
        this.invAim = invAim;
    }

    public String getSecAcctId() {
        return secAcctId;
    }

    public void setSecAcctId(String secAcctId) {
        this.secAcctId = secAcctId;
    }

    public String getInvManagerId() {
        return invManagerId;
    }

    public void setInvManagerId(String invManagerId) {
        this.invManagerId = invManagerId;
    }

    public String getAcctNo() {
        return acctNo;
    }

    public void setAcctNo(String acctNo) {
        this.acctNo = acctNo;
    }

    public BigDecimal getUnitCprice() {
        return unitCprice;
    }

    public void setUnitCprice(BigDecimal unitCprice) {
        this.unitCprice = unitCprice;
    }

    public BigDecimal getTradeAmt() {
        return tradeAmt;
    }

    public void setTradeAmt(BigDecimal tradeAmt) {
        this.tradeAmt = tradeAmt;
    }

    public String getBidTypeStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.中标方式, this.getBidType());
    }

    public void setBidTypeStr(String bidTypeStr) {
        this.bidTypeStr = bidTypeStr;
    }

    public String getBobpId() {
        return this.getPortfolioId()+"_"+this.getEntrustId();
    }

    public void setBobpId(String bobpId) {
        this.bobpId = bobpId;
    }

    private Date bidDate;

    private String bidId;

    public Date getBidDate() {
        return bidDate;
    }

    public void setBidDate(Date bidDate) {
        this.bidDate = bidDate;
    }

    public String getBidId() {
        return bidId;
    }

    public void setBidId(String bidId) {
        this.bidId = bidId;
    }

    public String getFinprodId() {
        return finprodId;
    }

    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
    }
}
