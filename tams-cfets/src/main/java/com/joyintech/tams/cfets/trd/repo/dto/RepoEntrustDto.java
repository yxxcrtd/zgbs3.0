package com.joyintech.tams.cfets.trd.repo.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.invest.common.constant.BussinessDictCode;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 指令列表
 *
 * @author jiangpeng
 * @date 2021/12/9 16:00
 */
@ApiModel(value = "指令列表", description = "本币业务交易-质押式回购-指令列表")
public class RepoEntrustDto {

    @ApiModelProperty(required = false, name = "entrustStatus", value = "指令状态")
    private String entrustStatus;

    @ApiModelProperty(required = false, name = "entrustStatusStr", value = "指令状态")
    private String entrustStatusStr;

    @ApiModelProperty(required = false, name = "finishAmt", value = "已完成额度")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal finishAmt;

    @ApiModelProperty(required = false, name = "finishDealAmt", value = "已成交额度")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal finishDealAmt;

    @ApiModelProperty(required = false, name = "avaNum", value = "剩余指令额度")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal avaNum;

    @ApiModelProperty(required = false, name = "avaDealAmt", value = "剩余成交额度")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal avaDealAmt;

    @ApiModelProperty(required = false, name = "transactioncom", value = "执行进度(%)")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal transactioncom;

    @ApiModelProperty(required = false, name = "tradeFinishPercent", value = "成交进度(%)")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal tradeFinishPercent;

    @ApiModelProperty(required = false, name = "entrustNum", value = "指令额度(万元)")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal entrustNum;

    @ApiModelProperty(required = false, name = "portfolioId", value = "组合代码")
    private String portfolioId;

    @ApiModelProperty(required = false, name = "portfolioName", value = "组合名称")
    private String portfolioName;

    @ApiModelProperty(required = false, name = "finprodIds", value = "质押债券代码")
    private String finprodIds;

    @ApiModelProperty(required = false, name = "finprodMarketNames", value = "质押债券名称")
    private String finprodMarketNames;

    @ApiModelProperty(required = false, name = "pledgeStockId", value = "质押券库代码")
    private String pledgeStockId;

    @ApiModelProperty(required = false, name = "pledgeStockId", value = "质押券库名称")
    private String pledgeStockName;

    @ApiModelProperty(required = false, name = "settleSpreed", value = "清算速度代码")
    private String settleSpreed;

    @ApiModelProperty(required = false, name = "settleSpreedStr", value = "清算速度名称")
    private String settleSpreedStr;

    @ApiModelProperty(required = false, name = "ps", value = "交易方向代码")
    private String ps;

    @ApiModelProperty(required = false, name = "psStr", value = "交易方向名称")
    private String psStr;

    @ApiModelProperty(required = false, name = "priceModel", value = "价格模式代码")
    private String priceModel;

    @ApiModelProperty(required = false, name = "priceModelStr", value = "价格模式名称")
    private String priceModelStr;

    @ApiModelProperty(required = false, name = "repoTerm", value = "回购期限")
    private int repoTerm;

    @ApiModelProperty(required = false, name = "tradeYield", value = "回购利率(%)")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal tradeYield;

    @ApiModelProperty(required = false, name = "price", value = "回购利率(%)")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal price;

    @ApiModelProperty(required = false, name = "invManagerName", value = "投资经理")
    private String invManagerName;

    @ApiModelProperty(required = false, name = "counterName", value = "交易对手")
    private String counterName;

    @ApiModelProperty(required = false, name = "entrustDate", value = "指令日期")
    private Date entrustDate;

    @ApiModelProperty(required = false, name = "entrustDateStr", value = "指令日期")
    private String entrustDateStr;

    @ApiModelProperty(name = "transmitTime", value = "指令下达时间")
    private String transmitTime;

    @ApiModelProperty(required = false, name = "entrustId", value = "指令代码")
    private String entrustId;

    @ApiModelProperty(required = false, name = "remark", value = "备注")
    private String remark;

    @ApiModelProperty(required = false, name = "ccy", value = "币种")
    private String ccy;

    @ApiModelProperty(required = false, name = "ccy", value = "币种名称")
    private String ccyStr;
    @ApiModelProperty(required = false, name = "tradeMarket", value = "交易场所ID")
    private String tradeMarket;

    @ApiModelProperty(required = false, name = "tradeMarketStr", value = "交易场所名称")
    private String tradeMarketStr;

    @ApiModelProperty(required = false, name = "termType", value = "期限品种")
    private String termType;

    @ApiModelProperty(required = false, name = "secManageAcctId", value = "证券账户")
    private String secManageAcctId;

    @ApiModelProperty(required = false, name = "repoEntrustPledgeDtos", value = "质押券信息")
    private List<RepoEntrustPledgeDto> repoEntrustPledgeDtos;

    public String getEntrustStatus() {
        return entrustStatus;
    }

    public void setEntrustStatus(String entrustStatus) {
        this.entrustStatus = entrustStatus;
    }

    public String getEntrustStatusStr() {
        return SysDataUtil.getNameFromDataDict(BussinessDictCode.委托状态, getEntrustStatus());
    }

    public void setEntrustStatusStr(String entrustStatusStr) {
        this.entrustStatusStr = entrustStatusStr;
    }

    public BigDecimal getFinishAmt() {
        return finishAmt;
    }

    public void setFinishAmt(BigDecimal finishAmt) {
        this.finishAmt = finishAmt;
    }

    public BigDecimal getFinishDealAmt() {
        return finishDealAmt;
    }

    public void setFinishDealAmt(BigDecimal finishDealAmt) {
        this.finishDealAmt = finishDealAmt;
    }

    public BigDecimal getAvaNum() {
        return avaNum;
    }

    public void setAvaNum(BigDecimal avaNum) {
        this.avaNum = avaNum;
    }

    public BigDecimal getAvaDealAmt() {
        return avaDealAmt;
    }

    public void setAvaDealAmt(BigDecimal avaDealAmt) {
        this.avaDealAmt = avaDealAmt;
    }

    public BigDecimal getTransactioncom() {
        return transactioncom;
    }

    public void setTransactioncom(BigDecimal transactioncom) {
        this.transactioncom = transactioncom;
    }

    public BigDecimal getTradeFinishPercent() {
        return tradeFinishPercent;
    }

    public void setTradeFinishPercent(BigDecimal tradeFinishPercent) {
        this.tradeFinishPercent = tradeFinishPercent;
    }

    public BigDecimal getEntrustNum() {
        return entrustNum;
    }

    public void setEntrustNum(BigDecimal entrustNum) {
        this.entrustNum = entrustNum;
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

    public String getFinprodIds() {
        return finprodIds;
    }

    public void setFinprodIds(String finprodIds) {
        this.finprodIds = finprodIds;
    }

    public String getFinprodMarketNames() {
        return finprodMarketNames;
    }

    public void setFinprodMarketNames(String finprodMarketNames) {
        this.finprodMarketNames = finprodMarketNames;
    }

    public String getPledgeStockId() {
        return pledgeStockId;
    }

    public void setPledgeStockId(String pledgeStockId) {
        this.pledgeStockId = pledgeStockId;
    }

    public String getPledgeStockName() {
        return pledgeStockName;
    }

    public void setPledgeStockName(String pledgeStockName) {
        this.pledgeStockName = pledgeStockName;
    }

    public String getSettleSpreed() {
        return settleSpreed;
    }

    public void setSettleSpreed(String settleSpreed) {
        this.settleSpreed = settleSpreed;
    }

    public String getSettleSpreedStr() {
        if (StringUtil.equals(this.getSettleSpreed(), "0")
                || StringUtil.equals(this.getSettleSpreed(), BussinessDictCodeList.清算速度_T加0.getValue())) {
            return BussinessDictCodeList.清算速度_T加0.getCodeName();
        } else if (StringUtil.equals(this.getSettleSpreed(), "1")
                || StringUtil.equals(this.getSettleSpreed(), BussinessDictCodeList.清算速度_T加1.getValue())) {
            return BussinessDictCodeList.清算速度_T加1.getCodeName();
        } else if (StringUtil.equals(this.getSettleSpreed(), "2")
                || StringUtil.equals(this.getSettleSpreed(), BussinessDictCodeList.清算速度_T加2.getValue())) {
            return BussinessDictCodeList.清算速度_T加2.getCodeName();
        } else {
            return this.getSettleSpreed();
        }
    }

    public void setSettleSpreedStr(String settleSpreedStr) {
        this.settleSpreedStr = settleSpreedStr;
    }

    public String getPs() {
        return ps;
    }

    public void setPs(String ps) {
        this.ps = ps;
    }

    public String getPsStr() {
        if (StringUtil.isEmpty(getPs())) {
            return StringUtil.EMPTY;
        }
        return SysDataUtil.getNameFromDataDict(BussinessDictCode.交易方向, getPs());
    }

    public void setPsStr(String psStr) {
        this.psStr = psStr;
    }

    public String getPriceModel() {
        return priceModel;
    }

    public void setPriceModel(String priceModel) {
        this.priceModel = priceModel;
    }

    public String getPriceModelStr() {
        if (StringUtil.isEmpty(getPriceModel())) {
            return StringUtil.EMPTY;
        }
        return SysDataUtil.getNameFromDataDict(DictCode.价格模式, getPriceModel());
    }

    public void setPriceModelStr(String priceModelStr) {
        this.priceModelStr = priceModelStr;
    }

    public int getRepoTerm() {
        return repoTerm;
    }

    public void setRepoTerm(int repoTerm) {
        this.repoTerm = repoTerm;
    }

    public BigDecimal getTradeYield() {
        return tradeYield;
    }

    public void setTradeYield(BigDecimal tradeYield) {
        this.tradeYield = tradeYield;
    }

    public BigDecimal getPrice() {
        return this.getTradeYield();
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getInvManagerName() {
        return invManagerName;
    }

    public void setInvManagerName(String invManagerName) {
        this.invManagerName = invManagerName;
    }

    public String getCounterName() {
        return counterName;
    }

    public void setCounterName(String counterName) {
        this.counterName = counterName;
    }

    public Date getEntrustDate() {
        return entrustDate;
    }

    public void setEntrustDate(Date entrustDate) {
        this.entrustDate = entrustDate;
    }

    public String getEntrustDateStr() {
        return DateUtil.format(this.getEntrustDate());
    }

    public void setEntrustDateStr(String entrustDateStr) {
        this.entrustDateStr = entrustDateStr;
    }

    public String getTransmitTime() {
        return transmitTime;
    }

    public void setTransmitTime(String transmitTime) {
        this.transmitTime = transmitTime;
    }

    public String getEntrustId() {
        return entrustId;
    }

    public void setEntrustId(String entrustId) {
        this.entrustId = entrustId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCcy() {
        return ccy;
    }

    public void setCcy(String ccy) {
        this.ccy = ccy;
    }

    public String getCcyStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.币种, getCcy());
    }

    public void setCcyStr(String ccyStr) {
        this.ccyStr = ccyStr;
    }

    public String getTradeMarket() {
        return tradeMarket;
    }

    public void setTradeMarket(String tradeMarket) {
        this.tradeMarket = tradeMarket;
    }

    public String getTradeMarketStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.交易场所, getTradeMarket());
    }

    public void setTradeMarketStr(String tradeMarketStr) {
        this.tradeMarketStr = tradeMarketStr;
    }

    public String getTermType() {
        return termType;
    }

    public void setTermType(String termType) {
        this.termType = termType;
    }

    public String getSecManageAcctId() {
        return secManageAcctId;
    }

    public void setSecManageAcctId(String secManageAcctId) {
        this.secManageAcctId = secManageAcctId;
    }

    public List<RepoEntrustPledgeDto> getRepoEntrustPledgeDtos() {
        return repoEntrustPledgeDtos;
    }

    public void setRepoEntrustPledgeDtos(List<RepoEntrustPledgeDto> repoEntrustPledgeDtos) {
        this.repoEntrustPledgeDtos = repoEntrustPledgeDtos;
    }
}



