package com.joyintech.tams.cfets.trdquery.dto;

import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.constant.DictCode;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

//组合分配信息共通DTO
public class BidPortIntFenPeiInfoDto {



    @ApiModelProperty(name="adjustmentStrategy",value="调整策略")
    private String adjustmentStrategy;

    //分配方式
    @ApiModelProperty(name="dipType",value="分配方式")
    private String dipType;

    @ApiModelProperty(name="dipType",value="分配方式")
    private String dipTypeStr;

    //指令代码
    @ApiModelProperty(name="entrustId",value="指令代码")
    private String entrustId;

    //组合名称
    @ApiModelProperty(name="portfolioName",value="组合名称")
    private String portfolioName;

    //中标标位
    @ApiModelProperty(name="winPrice",value="中标标位")
    private BigDecimal winPrice;

    @ApiModelProperty(name="winCprice",value="中标净价(元)")
    private BigDecimal winCprice;

    //券面总额(万元)
    @ApiModelProperty(name="prinAmt",value="券面总额(万元)")
    private BigDecimal prinAmt;
    //结算服务费（元）
    @ApiModelProperty(name="settleFeeAmt",value="结算服务费（元）")
    private BigDecimal settleFeeAmt;
    //交易金额（元）
    @ApiModelProperty(name="tradeAmt",value="交易金额（元）")
    private BigDecimal tradeAmt;
    //应计利息总额（元）
    @ApiModelProperty(name="intAmt",value="应计利息总额（元）",example="字符串1")
    private BigDecimal intAmt;
    //缴款金额（元）
    @ApiModelProperty(name="payAmt",value="缴款金额")
    private BigDecimal payAmt;
    //投标标位
    @ApiModelProperty(name="bidPrice",value="投标标位")
    private String bidPrice;
    //业务模式
    @ApiModelProperty(name="invAim",value="业务模式")
    private String invAim;

    //业务模式
    @ApiModelProperty(name="invAimStr",value="业务模式")
    private String invAimStr;

    //证券账户
    @ApiModelProperty(name="invAim",value="证券账户")
    private String secManageAcctId;

    @ApiModelProperty(name="invAim",value="证券账户")
    private String secManageAcctIdStr;
    //投资经理
    @ApiModelProperty(name="invManagerId",value="投资经理")
    private String invManagerId;

    @ApiModelProperty(name="winPrinAmt",value="汇总投标券面总额(万元)")
    private BigDecimal winPrinAmt;

    @ApiModelProperty(name="theWinPrinamt",value="理论中标券面总额 (万元)")
    private BigDecimal theWinPrinamt;

    @ApiModelProperty(name="actWinPrinamt",value="实际中标券面总额（万元）")
    private BigDecimal actWinPrinamt;

    @ApiModelProperty(name="feeAmt",value="总交易费用")
    private BigDecimal feeAmt;

    @ApiModelProperty(name="seqNo",value="序号")
    private String seqNo;

    @ApiModelProperty(name="subAmt",value="认购量")
    private String subAmt;

    @ApiModelProperty(name="payableAmount",value="应缴款金额")
    private String payableAmount;

    @ApiModelProperty(name="totalWinPrinAmt",value="汇总投标券面总额")
    private BigDecimal totalWinPrinAmt;

    @ApiModelProperty(name="avaNum",value="剩余指令额度")
    private BigDecimal avaNum;

    @ApiModelProperty(name="bidPrinAmt",value="指令投标券面总额")
    private BigDecimal bidPrinAmt;

    public String getDipType() {
        return dipType;
    }

    public void setDipType(String dipType) {
        this.dipType = dipType;
    }

    public String getEntrustId() {
        return entrustId;
    }

    public void setEntrustId(String entrustId) {
        this.entrustId = entrustId;
    }

    public String getPortfolioName() {
        return portfolioName;
    }

    public void setPortfolioName(String portfolioName) {
        this.portfolioName = portfolioName;
    }

    public BigDecimal getPrinAmt() {
        return prinAmt;
    }

    public void setPrinAmt(BigDecimal prinAmt) {
        this.prinAmt = prinAmt;
    }

    public BigDecimal getSettleFeeAmt() {
        return settleFeeAmt;
    }

    public void setSettleFeeAmt(BigDecimal settleFeeAmt) {
        this.settleFeeAmt = settleFeeAmt;
    }

    public BigDecimal getTradeAmt() {
        return tradeAmt;
    }

    public void setTradeAmt(BigDecimal tradeAmt) {
        this.tradeAmt = tradeAmt;
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

    public String getInvAim() {
        return invAim;
    }

    public void setInvAim(String invAim) {
        this.invAim = invAim;
    }

    public String getSecManageAcctId() {
        return secManageAcctId;
    }

    public void setSecManageAcctId(String secManageAcctId) {
        this.secManageAcctId = secManageAcctId;
    }

    public String getSecManageAcctIdStr() {
        return secManageAcctIdStr;
    }

    public void setSecManageAcctIdStr(String secManageAcctIdStr) {
        this.secManageAcctIdStr = secManageAcctIdStr;
    }

    public String getInvManagerId() {
        return invManagerId;
    }

    public void setInvManagerId(String invManagerId) {
        this.invManagerId = invManagerId;
    }

    public String getAdjustmentStrategy() {
        return SysDataUtil.getNameFromDataDict(DictCode.调整策略, getDipType());
    }

    public void setAdjustmentStrategy(String adjustmentStrategy) {
        this.adjustmentStrategy = adjustmentStrategy;
    }

    public String getDipTypeStr() {
         return SysDataUtil.getNameFromDataDict(DictCode.调整策略, getDipType());
    }

    public void setDipTypeStr(String dipTypeStr) {
        this.dipTypeStr = dipTypeStr;
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

    public String getBidPrice() {
        return bidPrice;
    }

    public void setBidPrice(String bidPrice) {
        this.bidPrice = bidPrice;
    }

    public String getInvAimStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.业务模式, getInvAim());
    }

    public void setInvAimStr(String invAimStr) {
        this.invAimStr = invAimStr;
    }

    public BigDecimal getWinPrinAmt() {
        return winPrinAmt;
    }

    public void setWinPrinAmt(BigDecimal winPrinAmt) {
        this.winPrinAmt = winPrinAmt;
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

    public BigDecimal getFeeAmt() {
        return feeAmt;
    }

    public void setFeeAmt(BigDecimal feeAmt) {
        this.feeAmt = feeAmt;
    }

    public String getSeqNo() {
        return seqNo;
    }

    public void setSeqNo(String seqNo) {
        this.seqNo = seqNo;
    }

    public String getSubAmt() {
        return subAmt;
    }

    public void setSubAmt(String subAmt) {
        this.subAmt = subAmt;
    }

    public String getPayableAmount() {
        return payableAmount;
    }

    public void setPayableAmount(String payableAmount) {
        this.payableAmount = payableAmount;
    }

    public BigDecimal getTotalWinPrinAmt() {
        return totalWinPrinAmt;
    }

    public void setTotalWinPrinAmt(BigDecimal totalWinPrinAmt) {
        this.totalWinPrinAmt = totalWinPrinAmt;
    }

    public BigDecimal getAvaNum() {
        return avaNum;
    }

    public void setAvaNum(BigDecimal avaNum) {
        this.avaNum = avaNum;
    }

    public BigDecimal getBidPrinAmt() {
        return bidPrinAmt;
    }

    public void setBidPrinAmt(BigDecimal bidPrinAmt) {
        this.bidPrinAmt = bidPrinAmt;
    }
}
