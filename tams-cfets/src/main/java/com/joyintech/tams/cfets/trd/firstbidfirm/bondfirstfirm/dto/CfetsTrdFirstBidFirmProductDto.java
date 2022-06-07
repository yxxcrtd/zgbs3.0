package com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstfirm.dto;

import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.constant.DictCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author jiangpeng
 * @date 2022年05月16日 11:45
 */
@ApiModel(value = "一级投标确认-交易信息-投标产品列表", description = "一级投标确认信息-投标产品列表")
public class CfetsTrdFirstBidFirmProductDto {
    /**
     * 投资经理
     */
    @ApiModelProperty(name = "entrustId", value = "指令代码", example = "A1")
    private String entrustId;

    @ApiModelProperty(name = "portfolioId", value = "组合代码", example = "A")
    private String portfolioId;

    @ApiModelProperty(name = "portfolioName", value = "组合名称", example = "组合A")
    private String portfolioName;

    @ApiModelProperty(name = "totalBiddingNum", value = "汇总投标券面总额(万元) 需前端转化", example = "10000.00")
    private BigDecimal totalBiddingNum;

    @ApiModelProperty(name = "etrBidPriceTotal", value = "指令投标券面总额 (万元) 需前端转化", example = "10000.00")
    private BigDecimal etrBidPriceTotal;

    @ApiModelProperty(name = "avaNum", value = "剩余指令额度(万元) 需前端转化", example = "10000.00")
    private BigDecimal avaNum;

    /**
     * 计算中间值
     */
    private BigDecimal avaNumTemp;

    @ApiModelProperty(name = "entrustNum", value = "委托券面总额 不显示", example = "10000.00")
    private BigDecimal entrustNum;

    @ApiModelProperty(name = "bidPrinAmt", value = "委托投标券面 不显示", example = "10000.00")
    private BigDecimal bidPrinAmt;

    @ApiModelProperty(name = "bidPriceTotal", value = "券面总额(万元) 需前端转化", example = "10000.00")
    private BigDecimal bidPriceTotal;

    @ApiModelProperty(name = "settFeeAmt", value = "结算服务费（元）", example = "1200")
    private BigDecimal settFeeAmt;

    @ApiModelProperty(name = "tradeAmt", value = "交易金额（元）", example = "1200")
    private BigDecimal tradeAmt;

    @ApiModelProperty(name = "intAmt", value = "应计利息", example = "1200")
    private BigDecimal intAmt;

    @ApiModelProperty(name = "intAmtTatol", value = "应计利息总额（元）", example = "1200")
    private BigDecimal intAmtTatol;

    @ApiModelProperty(name = "payAmt", value = "缴款金额（元）", example = "1200")
    private BigDecimal payAmt;

    @ApiModelProperty(name = "bidPrice", value = "投标标位 利差/利率/价格", example = "1")
    private BigDecimal bidPrice;

    @ApiModelProperty(name = "businessModel", value = "业务模式代码", example = "AC")
    private String businessModel;

    @ApiModelProperty(name = "businessModelStr", value = "业务模式名称,展示用")
    private String businessModelStr;

    @ApiModelProperty(name = "acctNo", value = "资金账户", example = "1")
    private String acctNo;

    @ApiModelProperty(name = "secManageAcctId", value = "证券账户ID", example = "1")
    private String secManageAcctId;

    @ApiModelProperty(name = "secManageAcctIdName", value = "证券账户", example = "银行间证券账户")
    private String secManageAcctIdName;

    @ApiModelProperty(name = "invManagerId", value = "投资经理ID", example = "A1")
    private String invManagerId;

    @ApiModelProperty(name = "invManagerName", value = "投资经理名称", example = "A1")
    private String invManagerName;

    @ApiModelProperty(name = "bidId", value = "投标编号,编辑页面/提交用", example = "111")
    private String bidId;

    @ApiModelProperty(name = "counterId", value = "交易对手id", example = "114912384")
    private String counterId;

    @ApiModelProperty(name = "counterName", value = "交易对手名", example = "广发银行")
    private String counterName;

    @ApiModelProperty(name = "cashT", value = "T+1日可用现金(元)")
    private BigDecimal cashT;

    @ApiModelProperty(name = "cashT1", value = "T日可用现金(元)")
    private BigDecimal cashT1;

    @ApiModelProperty(name = "realCash", value = "托管户账户现金(元)")
    private BigDecimal realCash;

    @ApiModelProperty(name = "secDesirableCash", value = "券商户可取现金(元)")
    private BigDecimal secDesirableCash;

    List<CfetsTrdFirstBidFirmProductDto> list;

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

    public String getPortfolioName() {
        return portfolioName;
    }

    public void setPortfolioName(String portfolioName) {
        this.portfolioName = portfolioName;
    }

    /**
     * 汇总投标券面总额
     *
     * @return java.math.BigDecimal
     * @author jiangpeng
     * @date 2022/5/19 15:26
     */
    public BigDecimal getTotalBiddingNum() {
        return totalBiddingNum;
    }

    /**
     * 汇总投标券面总额
     *
     * @param totalBiddingNum
     * @author jiangpeng
     * @date 2022/5/19 16:14
     */
    public void setTotalBiddingNum(BigDecimal totalBiddingNum) {
        this.totalBiddingNum = totalBiddingNum;
    }

    /**
     * 指令投标券面总额
     *
     * @return java.math.BigDecimal
     * @author jiangpeng
     * @date 2022/5/19 15:27
     */
    public BigDecimal getEtrBidPriceTotal() {
        return etrBidPriceTotal;
    }

    public void setEtrBidPriceTotal(BigDecimal etrBidPriceTotal) {
        this.etrBidPriceTotal = etrBidPriceTotal;
    }

    /**
     * 剩余指令额度
     *
     * @return java.math.BigDecimal
     * @author jiangpeng
     * @date 2022/5/19 11:33
     */
    public BigDecimal getAvaNum() {
        return avaNum;
    }

    public void setAvaNum(BigDecimal avaNum) {
        this.avaNum = avaNum;
    }

    /**
     * 获取计算中间值
     *
     * @return java.math.BigDecimal
     * @author jiangpeng
     * @date 2022/5/19 11:33
     */
    public BigDecimal getAvaNumTemp() {
        return avaNumTemp;
    }

    /**
     * 记录计算中间值
     *
     * @param avaNumTemp
     * @author jiangpeng
     * @date 2022/5/19 11:34
     */
    public void setAvaNumTemp(BigDecimal avaNumTemp) {
        this.avaNumTemp = avaNumTemp;
    }

    public BigDecimal getEntrustNum() {
        return entrustNum;
    }

    public void setEntrustNum(BigDecimal entrustNum) {
        this.entrustNum = entrustNum;
    }

    public BigDecimal getBidPrinAmt() {
        return bidPrinAmt;
    }

    public void setBidPrinAmt(BigDecimal bidPrinAmt) {
        this.bidPrinAmt = bidPrinAmt;
    }

    public BigDecimal getBidPriceTotal() {
        return bidPriceTotal;
    }

    public void setBidPriceTotal(BigDecimal bidPriceTotal) {
        this.bidPriceTotal = bidPriceTotal;
    }

    public BigDecimal getSettFeeAmt() {
        return settFeeAmt;
    }

    public void setSettFeeAmt(BigDecimal settFeeAmt) {
        this.settFeeAmt = settFeeAmt;
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

    public BigDecimal getIntAmtTatol() {
        return intAmtTatol;
    }

    public void setIntAmtTatol(BigDecimal intAmtTatol) {
        this.intAmtTatol = intAmtTatol;
    }

    public BigDecimal getPayAmt() {
        return payAmt;
    }

    public void setPayAmt(BigDecimal payAmt) {
        this.payAmt = payAmt;
    }

    public BigDecimal getBidPrice() {
        return bidPrice;
    }

    public void setBidPrice(BigDecimal bidPrice) {
        this.bidPrice = bidPrice;
    }

    public String getBusinessModel() {
        return businessModel;
    }

    public void setBusinessModel(String businessModel) {
        this.businessModel = businessModel;
    }

    public String getBusinessModelStr() {
        if (StringUtil.isEmpty(getBusinessModel())) {
            return StringUtil.EMPTY;
        }
        return SysDataUtil.getNameFromDataDict(DictCode.业务模式, getBusinessModel());
    }

    public void setBusinessModelStr(String businessModelStr) {
        this.businessModelStr = businessModelStr;
    }

    public String getAcctNo() {
        return acctNo;
    }

    public void setAcctNo(String acctNo) {
        this.acctNo = acctNo;
    }

    public String getSecManageAcctId() {
        return secManageAcctId;
    }

    public void setSecManageAcctId(String secManageAcctId) {
        this.secManageAcctId = secManageAcctId;
    }

    public String getSecManageAcctIdName() {
        return secManageAcctIdName;
    }

    public void setSecManageAcctIdName(String secManageAcctIdName) {
        this.secManageAcctIdName = secManageAcctIdName;
    }

    public String getInvManagerId() {
        return invManagerId;
    }

    public void setInvManagerId(String invManagerId) {
        this.invManagerId = invManagerId;
    }

    public String getInvManagerName() {
        return invManagerName;
    }

    public void setInvManagerName(String invManagerName) {
        this.invManagerName = invManagerName;
    }

    public String getBidId() {
        return bidId;
    }

    public void setBidId(String bidId) {
        this.bidId = bidId;
    }

    public String getCounterId() {
        return counterId;
    }

    public void setCounterId(String counterId) {
        this.counterId = counterId;
    }

    public String getCounterName() {
        return counterName;
    }

    public void setCounterName(String counterName) {
        this.counterName = counterName;
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

    public List<CfetsTrdFirstBidFirmProductDto> getList() {
        return list;
    }

    public void setList(List<CfetsTrdFirstBidFirmProductDto> list) {
        this.list = list;
    }
}
