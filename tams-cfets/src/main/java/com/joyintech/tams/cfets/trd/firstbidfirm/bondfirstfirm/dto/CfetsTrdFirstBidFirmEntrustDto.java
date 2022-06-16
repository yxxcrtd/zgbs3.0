package com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstfirm.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeListExt4;
import com.joyintech.tams.invest.common.constant.BussinessDictCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author jiangpeng
 * @date 2022年05月14日 16:45
 */
@ApiModel(value = "一级投标确认-指令信息", description = "指令列表信息")
public class CfetsTrdFirstBidFirmEntrustDto {

    @ApiModelProperty(name = "entrustStatus", value = "指令状态", example = "04")
    private String entrustStatus;

    @ApiModelProperty(name = "entrustStatusStr", value = "指令状态名称,展示用", example = "已完成")
    private String entrustStatusStr;

    @ApiModelProperty(name = "entrustId", value = "指令代码", example = "WTBOND202204191858")
    private String entrustId;

    @ApiModelProperty(name = "entrustIdKey", value = "指令代码唯一标识：前端区分", example = "WTBOND202204191858")
    private String entrustIdKey;

    @ApiModelProperty(name = "finprodId", value = "资产代码,系统内代码一般不用于显示", example = "F01_aa.IB")
    private String finprodId;

    @ApiModelProperty(name = "finprodMarketId", value = "市场代码,显示用代码", example = "aa.IB")
    private String finprodMarketId;

    @ApiModelProperty(name = "finprodAbbr", value = "资产简称", example = "债券aa")
    private String finprodAbbr;

    @ApiModelProperty(name = "avaNum", value = "剩余指令额度", example = "1000.00")
    private BigDecimal avaNum;

    @ApiModelProperty(name = "avaDealAmt", value = "剩余成交额度", example = "1000.00")
    private BigDecimal avaDealAmt;

    @ApiModelProperty(name = "transactioncom", value = "执行进度", example = "50.00")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal transactioncom;

    @ApiModelProperty(name = "finishAmt", value = "已完成额度", example = "1000.00")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal finishAmt;

    @ApiModelProperty(name = "finishDealAmt", value = "已成交额度", example = "1000.00")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal finishDealAmt;

    @ApiModelProperty(name = "tradeFinishPercent", value = "成交进度", example = "50.00")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal tradeFinishPercent;

    @ApiModelProperty(name="tenderTarget",value="招标标的")
    private String tenderTarget;

    @ApiModelProperty(name="issueClass",value="发行方式")
    private String issueClass;

    @ApiModelProperty(name="tenderTargetStr",value="招标标的字典值")
    private String tenderTargetStr;

    @ApiModelProperty(name = "issueDate", value = "投标日")
    private Date issueDate;

    @ApiModelProperty(name = "issueDateStart", value = "投标日开始日期,字符串", example = "2021-12-21")
    private Date issueDateStart;

    @ApiModelProperty(name = "issueDateEnd", value = "投标日结束日期,字符串", example = "2021-12-21")
    private Date issueDateEnd;

    @ApiModelProperty(name = "issueDateStr", value = "投标日字符串")
    private String issueDateStr;

    @ApiModelProperty(name = "entrustNum", value = "指令额度、券面总额", example = "1000.00")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal entrustNum;

    @ApiModelProperty(name = "bidPrice", value = "投标标位", example = "1")
    private String bidPrice;

    @ApiModelProperty(name = "bidPriceStr", value = "投标标位字符串", example = "1")
    private String bidPriceStr;

    @ApiModelProperty(name = "portfolioId", value = "组合代码", example = "A1")
    private String portfolioId;

    @ApiModelProperty(name = "portfolioName", value = "组合名称", example = "名称A1")
    private String portfolioName;

    @ApiModelProperty(name = "invManagerId", value = "投资经理", example = "A1")
    private String invManagerId;

    @ApiModelProperty(name = "invManagerName", value = "投资经理名称", example = "A1")
    private String invManagerName;

    @ApiModelProperty(name = "tradeMarket", value = "交易场所")
    private String tradeMarket;

    @ApiModelProperty(name = "tradeMarketStr", value = "交易场所名称")
    private String tradeMarketStr;

    @ApiModelProperty(name = "counterId", value = "交易对手", example = "A1")
    private String counterId;

    @ApiModelProperty(name = "counterName", value = "交易对手名称", example = "A1")
    private String counterName;

    @ApiModelProperty(name = "entrustDate", value = "指令日期")
    private Date entrustDate;

    @ApiModelProperty(name = "entrustDateStr", value = "指令日期格式化,展示用")
    private String entrustDateStr;

    @ApiModelProperty(name = "transmitTime", value = "指令下达时间", example = "11:23:45")
    private String transmitTime;

    @ApiModelProperty(name = "remark", value = "备注")
    private String remark;

    @ApiModelProperty(name = "lastModifiedDate", value = "最近修改日期")
    private Date lastModifiedDate;

    @ApiModelProperty(name = "lastModifiedDateStr", value = "最近修改日期格式化,展示用")
    private String lastModifiedDateStr;

    @ApiModelProperty(name = "lastModifiedTime", value = "最近修改时间")
    private Date lastModifiedTime;

    @ApiModelProperty(name = "lastModifiedTimeStr", value = "最近修改时间格式化,展示用")
    private String lastModifiedTimeStr;

    @ApiModelProperty(name = "firstBidStatus", value = "中标状态", example = "A1")
    private String firstBidStatus;

    @ApiModelProperty(name = "firstBidStatusStr", value = "中标状态名称", example = "A1")
    private String firstBidStatusStr;

    @ApiModelProperty(name = "sumFlag", value = "汇总标识", example = "true/fasle")
    private Boolean sumFlag;

    @ApiModelProperty(name = "operateSide", value = "操作方向：01 拖出 02 拖入", example = "01")
    private String operateSide;

    @ApiModelProperty(name = "cfetsTrdFirstBidFirmEntrustDtos", value = "指令明细信息")
    private List<CfetsTrdFirstBidFirmEntrustDto> cfetsTrdFirstBidFirmEntrustDtos;

    @ApiModelProperty(name = "totleId", value = "汇总代码：汇总指令关系表，用于指令汇总", example = "true/fasle")
    private String totleId;

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

    public String getEntrustId() {
        return entrustId;
    }

    public void setEntrustId(String entrustId) {
        this.entrustId = entrustId;
    }

    public String getFinprodId() {
        return finprodId;
    }

    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
    }

    public String getFinprodMarketId() {
        return finprodMarketId;
    }

    public void setFinprodMarketId(String finprodMarketId) {
        this.finprodMarketId = finprodMarketId;
    }

    public String getFinprodAbbr() {
        return finprodAbbr;
    }

    public void setFinprodAbbr(String finprodAbbr) {
        this.finprodAbbr = finprodAbbr;
    }

    public String getTradeMarket() {
        return tradeMarket;
    }

    public void setTradeMarket(String tradeMarket) {
        this.tradeMarket = tradeMarket;
    }
    public String getTradeMarketStr() {
        if (StringUtil.isEmpty(getTradeMarket())) {
            return null;
        }
        return SysDataUtil.getNameFromDataDict(DictCode.交易场所, getTradeMarket());
    }

    public void setTradeMarketStr(String tradeMarketStr) {
        this.tradeMarketStr = tradeMarketStr;
    }

    public Date getEntrustDate() {
        return entrustDate;
    }

    public void setEntrustDate(Date entrustDate) {
        this.entrustDate = entrustDate;
    }

    public String getEntrustDateStr() {
        if (null != this.getEntrustDate()) {
            return DateUtil.format(this.getEntrustDate());
        }
        return entrustDateStr;
    }

    public void setEntrustDateStr(String entrustDateStr) {
        this.entrustDateStr = entrustDateStr;
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

    public BigDecimal getTradeFinishPercent() {
        return tradeFinishPercent;
    }

    public void setTradeFinishPercent(BigDecimal tradeFinishPercent) {
        this.tradeFinishPercent = tradeFinishPercent;
    }

    public String getTenderTarget() {
        return tenderTarget;
    }

    public void setTenderTarget(String tenderTarget) {
        this.tenderTarget = tenderTarget;
    }

    public Date getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    public String getIssueClass() {
        return issueClass;
    }

    public void setIssueClass(String issueClass) {
        this.issueClass = issueClass;
    }

    public String getIssueDateStr() {
        if (null == this.getIssueDateEnd() && null != this.getIssueDateStart()) {
            return DateUtil.format(this.getIssueDateStart());
        }
        if (null != this.getIssueDateStart() && null != this.getIssueDateEnd()) {
            return DateUtil.format(this.getIssueDateStart()) + "~" + DateUtil.format(this.getIssueDateEnd());
        }
        return issueDateStr;
    }

    public void setIssueDateStr(String issueDateStr) {
        this.issueDateStr = issueDateStr;
    }

    public BigDecimal getEntrustNum() {
        return entrustNum;
    }

    public void setEntrustNum(BigDecimal entrustNum) {
        this.entrustNum = entrustNum;
    }

    public String getBidPrice() {
        return bidPrice;
    }

    public void setBidPrice(String bidPrice) {
        this.bidPrice = bidPrice;
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

    public String getInvManagerId() {
        return invManagerId;
    }

    public void setInvManagerId(String invManagerId) {
        this.invManagerId = invManagerId;
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

    public String getTransmitTime() {
        return transmitTime;
    }

    public void setTransmitTime(String transmitTime) {
        this.transmitTime = transmitTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }


    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getLastModifiedDateStr() {
        if (null != this.getLastModifiedDate()) {
            return DateUtil.format(this.getLastModifiedDate());
        }
        //取最近一次修改日期，如未修改，默认为等于指令日期
        if (null != this.getEntrustDate()) {
            return DateUtil.format(this.getEntrustDate());
        }
        return lastModifiedDateStr;
    }

    public void setLastModifiedDateStr(String lastModifiedDateStr) {
        this.lastModifiedDateStr = lastModifiedDateStr;
    }

    public Date getLastModifiedTime() {
        return lastModifiedTime;
    }

    public void setLastModifiedTime(Date lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }

    public String getLastModifiedTimeStr() {
        if (null != this.getLastModifiedDate()) {
            return DateUtil.format(this.getLastModifiedDate(), "HH:mm:ss");
        }
        //取值最近一次修改生效时间，如未修改，默认等于下达时间
        if (null != this.getTransmitTime()) {
            return this.getTransmitTime();
        }
        return lastModifiedTimeStr;
    }

    public void setLastModifiedTimeStr(String lastModifiedTimeStr) {
        this.lastModifiedTimeStr = lastModifiedTimeStr;
    }

    public String getFirstBidStatus() {
        return firstBidStatus;
    }

    public void setFirstBidStatus(String firstBidStatus) {
        this.firstBidStatus = firstBidStatus;
    }

    public String getFirstBidStatusStr() {
        if (StringUtil.isEmpty(getFirstBidStatus())) {
            return "--";
        }
        return SysDataUtil.getNameFromDataDict(DictCode.一级投标中标状态, getFirstBidStatus());
    }

    public void setFirstBidStatusStr(String firstBidStatusStr) {
        this.firstBidStatusStr = firstBidStatusStr;
    }

    public String getTenderTargetStr() {
        if (StringUtil.isEmpty(getTenderTarget())) {
            return "";
        }
        return SysDataUtil.getNameFromDataDict(DictCode.招标标的, getTenderTarget());
    }

    public void setTenderTargetStr(String tenderTargetStr) {
        this.tenderTargetStr = tenderTargetStr;
    }

    public Boolean getSumFlag() {
        return sumFlag;
    }

    public void setSumFlag(Boolean sumFlag) {
        this.sumFlag = sumFlag;
    }

    public List<CfetsTrdFirstBidFirmEntrustDto> getCfetsTrdFirstBidFirmEntrustDtos() {
        return cfetsTrdFirstBidFirmEntrustDtos;
    }

    public void setCfetsTrdFirstBidFirmEntrustDtos(List<CfetsTrdFirstBidFirmEntrustDto> cfetsTrdFirstBidFirmEntrustDtos) {
        this.cfetsTrdFirstBidFirmEntrustDtos = cfetsTrdFirstBidFirmEntrustDtos;
    }

    public String getInvManagerName() {
        return invManagerName;
    }

    public void setInvManagerName(String invManagerName) {
        this.invManagerName = invManagerName;
    }

    public Date getIssueDateStart() {
        return issueDateStart;
    }

    public void setIssueDateStart(Date issueDateStart) {
        this.issueDateStart = issueDateStart;
    }

    public Date getIssueDateEnd() {
        return issueDateEnd;
    }

    public void setIssueDateEnd(Date issueDateEnd) {
        this.issueDateEnd = issueDateEnd;
    }

    public String getBidPriceStr() {
        if (StringUtil.equals(DictCodeListExt4.招标标的_利率.getValue(),this.getTenderTarget())) {
            return getBidPrice() + " %";
        }
        if (StringUtil.equals(DictCodeListExt4.招标标的_价格.getValue(),this.getTenderTarget())) {
            return getBidPrice() + " 元";
        }
        if (StringUtil.equals(DictCodeListExt4.招标标的_利差.getValue(),this.getTenderTarget())) {
            return getBidPrice() + " BP";
        }
        return getBidPrice();
    }

    public void setBidPriceStr(String bidPriceStr) {
        this.bidPriceStr = bidPriceStr;
    }

    public String getEntrustIdKey() {
        return entrustIdKey;
    }

    public void setEntrustIdKey(String entrustIdKey) {
        this.entrustIdKey = entrustIdKey;
    }

    public String getOperateSide() {
        return operateSide;
    }

    public void setOperateSide(String operateSide) {
        this.operateSide = operateSide;
    }

    public String getTotleId() {
        return totleId;
    }

    public void setTotleId(String totleId) {
        this.totleId = totleId;
    }
}
