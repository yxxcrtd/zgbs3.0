package com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstfirm.dto;

import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.invest.common.constant.BussinessDictCode;
import com.joyintech.workflow.engine.dto.ProcessEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 分销买入列表DTO
 *
 * @author jiangpeng
 * @date 2022/5/23 14:26
 */
@ApiModel(value = "一级投标确认 分销买入", description = "一级投标 分销买入列表DTO")
public class CfetsTrdFirstBidFirmDistributionInfoDto extends BaseEntity implements ProcessEntity {

    @ApiModelProperty(name = "entrustId", value = "指令代码", example = "01")
    private String entrustId;

    @ApiModelProperty(name = "validStatus", value = "有效状态", example = "01")
    private String validStatus;

    @ApiModelProperty(name = "validStatusStr", value = "有效状态名称,展示用", example = "有效")
    private String validStatusStr;

    @ApiModelProperty(name = "firstBidStatus", value = "中标状态", example = "A1")
    private String firstBidStatus;

    @ApiModelProperty(name = "firstBidStatusStr", value = "中标状态名称,展示用", example = "A1")
    private String firstBidStatusStr;

    @ApiModelProperty(name = "bidId", value = "投标编号", example = "111")
    private String bidId;

    @ApiModelProperty(name = "finprodId", value = "资产代码", example = "F01_001.IB")
    private String finprodId;

    @ApiModelProperty(name = "finprodMarketId", value = "市场代码", example = "001.IB")
    private String finprodMarketId;

    @ApiModelProperty(name = "finprodAbbr", value = "资产简称", example = "001债券")
    private String finprodAbbr;

    @ApiModelProperty(name = "tenderTarget", value = "招标标的")
    private String tenderTarget;

    @ApiModelProperty(name = "tenderTargetStr", value = "招标标的格式化")
    private String tenderTargetStr;

    @ApiModelProperty(name = "bidDateStart", value = "投标日开始")
    private Date bidDateStart;

    @ApiModelProperty(name = "bidDateStartStr", value = "投标日开始格式化,展示用")
    private String bidDateStartStr;

    @ApiModelProperty(name = "bidDateEnd", value = "投标日结束")
    private Date bidDateEnd;

    @ApiModelProperty(name = "bidDateStr", value = "投标日 拼接,展示用")
    private String bidDateStr;

    @ApiModelProperty(name = "bidDateEndStr", value = "投标日结束格式化,展示用")
    private String bidDateEndStr;

    @ApiModelProperty(name = "etrBidPriceTotal", value = "指令投标券面总额(万元) 需前端转化", example = "10000.00")
    private BigDecimal etrBidPriceTotal;

    @ApiModelProperty(name = "bidPriceTotal", value = "券面总额(万元) 需前端转化", example = "10000.00")
    private BigDecimal bidPriceTotal;

    @ApiModelProperty(name = "biddingMethod", value = "投标方式", example = "01")
    private String biddingMethod;

    @ApiModelProperty(name = "biddingMethodStr", value = "投标方式名称,展示用", example = "自主投标")
    private String biddingMethodStr;

    @ApiModelProperty(name = "payType", value = "缴款方式", example = "01")
    private String payType;

    @ApiModelProperty(name = "payTypeStr", value = "缴款方式名称,展示用", example = "一级分销")
    private String payTypeStr;

    @ApiModelProperty(name = "bidPrice", value = "投标标位,多个字符串分割", example = "字符串1")
    private String bidPrice;

    @ApiModelProperty(name = "winPrice", value = "中标标位", example = "100.0000")
    private String winPrice;

    @ApiModelProperty(name = "price", value = "净价", example = "1")
    private BigDecimal price;

    @ApiModelProperty(name = "payableAmount", value = "缴款金额", example = "1000.00")
    private BigDecimal payableAmount;

    @ApiModelProperty(name = "settFeeAmt", value = "结算服务费", example = "1000.00")
    private BigDecimal settFeeAmt;

    @ApiModelProperty(name = "payDate", value = "缴款日")
    private Date payDate;

    @ApiModelProperty(name = "payDateStr", value = "缴款日格式化,展示用")
    private String payDateStr;

    @ApiModelProperty(name = "assignRegdate", value = "债权债务登记日")
    private Date assignRegdate;

    @ApiModelProperty(name = "assignRegdateStr", value = "债权债务登记日格式化,展示用")
    private String assignRegdateStr;

    @ApiModelProperty(name = "listingDate", value = "上市日")
    private Date listingDate;

    @ApiModelProperty(name = "listingDateStr", value = "上市日日格式化,展示用", example = "2021-01-01")
    private String listingDateStr;

    @ApiModelProperty(name = "tradeMarket", value = "交易场所", example = "01")
    private String tradeMarket;

    @ApiModelProperty(name = "tradeMarketStr", value = "交易场所", example = "01")
    private String tradeMarketStr;

    @ApiModelProperty(name = "counterId", value = "交易对手id", example = "114912384")
    private String counterId;

    @ApiModelProperty(name = "counterName", value = "交易对手名", example = "广发银行")
    private String counterName;

    @ApiModelProperty(name = "bidCfmDateStart", value = "投标确认日开始日")
    private Date bidCfmDateStart;

    @ApiModelProperty(name = "bidCfmDateStartStr", value = "投标确认日开始 格式化")
    private String bidCfmDateStartStr;

    @ApiModelProperty(name = "bidCfmDateEnd", value = "投标确认日结束日")
    private Date bidCfmDateEnd;

    @ApiModelProperty(name = "bidCfmDateEndStr", value = "投标确认日结束日 格式化")
    private String bidCfmDateEndStr;

    @ApiModelProperty(name = "bidCfmDateStr", value = "投标确认日 拼接,展示用")
    private String bidCfmDateStr;

    @ApiModelProperty(name = "dealDate", value = "成交日期")
    private Date dealDate;

    @ApiModelProperty(name = "dealDateStr", value = "成交日期 格式化")
    private String dealDateStr;

    @ApiModelProperty(name = "remark", value = "备注", example = "XXX")
    private String remark;

    public String getEntrustId() {
        return entrustId;
    }

    public void setEntrustId(String entrustId) {
        this.entrustId = entrustId;
    }

    public String getValidStatus() {
        return validStatus;
    }

    public void setValidStatus(String validStatus) {
        this.validStatus = validStatus;
    }

    public String getValidStatusStr() {
        if (StringUtil.isEmpty(getValidStatus())) {
            return StringUtil.EMPTY;
        }
        return SysDataUtil.getNameFromDataDict(DictCode.交易流程状态, getValidStatus());
    }

    public void setValidStatusStr(String validStatusStr) {
        this.validStatusStr = validStatusStr;
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

    public Date getBidDateStart() {
        return bidDateStart;
    }

    public void setBidDateStart(Date bidDateStart) {
        this.bidDateStart = bidDateStart;
    }

    public String getBidDateStartStr() {
        if (null != this.getBidDateStart()) {
            return DateUtil.format(this.getBidDateStart());
        }
        return bidDateStartStr;
    }

    public void setBidDateStartStr(String bidDateStartStr) {
        this.bidDateStartStr = bidDateStartStr;
    }

    public Date getBidDateEnd() {
        return bidDateEnd;
    }

    public void setBidDateEnd(Date bidDateEnd) {
        this.bidDateEnd = bidDateEnd;
    }

    public String getBidDateEndStr() {
        if (null != this.getBidDateEnd()) {
            return DateUtil.format(this.getBidDateEnd());
        }
        return bidDateEndStr;
    }

    public void setBidDateEndStr(String bidDateEndStr) {
        this.bidDateEndStr = bidDateEndStr;
    }

    public String getTenderTarget() {
        return tenderTarget;
    }

    public void setTenderTarget(String tenderTarget) {
        this.tenderTarget = tenderTarget;
    }

    public String getTenderTargetStr() {
        if (StringUtil.isEmpty(getTenderTarget())) {
            return StringUtil.EMPTY;
        }
        return SysDataUtil.getNameFromDataDict(DictCode.招标标的, getTenderTarget());
    }

    public void setTenderTargetStr(String tenderTargetStr) {
        this.tenderTargetStr = tenderTargetStr;
    }


    public Date getPayDate() {
        return payDate;
    }

    public void setPayDate(Date payDate) {
        this.payDate = payDate;
    }

    public String getPayDateStr() {
        if (null != this.getPayDate()) {
            return DateUtil.format(this.getPayDate());
        }
        return payDateStr;
    }

    public void setPayDateStr(String payDateStr) {
        this.payDateStr = payDateStr;
    }

    public Date getAssignRegdate() {
        return assignRegdate;
    }

    public void setAssignRegdate(Date assignRegdate) {
        this.assignRegdate = assignRegdate;
    }

    public String getAssignRegdateStr() {
        if (null != this.getAssignRegdate()) {
            return DateUtil.format(this.getAssignRegdate());
        }
        return assignRegdateStr;
    }

    public void setAssignRegdateStr(String assignRegdateStr) {
        this.assignRegdateStr = assignRegdateStr;
    }

    public Date getListingDate() {
        return listingDate;
    }

    public void setListingDate(Date listingDate) {
        this.listingDate = listingDate;
    }

    public String getListingDateStr() {
        if (null != this.getListingDate()) {
            return DateUtil.format(this.getListingDate());
        }
        return listingDateStr;
    }

    public void setListingDateStr(String listingDateStr) {
        this.listingDateStr = listingDateStr;
    }

    public String getBiddingMethod() {
        return biddingMethod;
    }

    public void setBiddingMethod(String biddingMethod) {
        this.biddingMethod = biddingMethod;
    }

    public String getBiddingMethodStr() {
        if (StringUtil.isEmpty(getBiddingMethod())) {
            return StringUtil.EMPTY;
        }
        return SysDataUtil.getNameFromDataDict(DictCode.投标方式, getBiddingMethod());
    }

    public void setBiddingMethodStr(String biddingMethodStr) {
        this.biddingMethodStr = biddingMethodStr;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public String getPayTypeStr() {
        if (StringUtil.isEmpty(getPayType())) {
            return StringUtil.EMPTY;
        }
        return SysDataUtil.getNameFromDataDict(BussinessDictCode.缴款方式, getPayType());
    }

    public void setPayTypeStr(String payTypeStr) {
        this.payTypeStr = payTypeStr;
    }

    public BigDecimal getEtrBidPriceTotal() {
        return etrBidPriceTotal;
    }

    public void setEtrBidPriceTotal(BigDecimal etrBidPriceTotal) {
        this.etrBidPriceTotal = etrBidPriceTotal;
    }

    /**
     * 券面总额
     *
     * @return java.math.BigDecimal
     * @author jiangpeng
     * @date 2022/5/19 11:30
     */
    public BigDecimal getBidPriceTotal() {
        return bidPriceTotal;
    }

    public void setBidPriceTotal(BigDecimal bidPriceTotal) {
        this.bidPriceTotal = bidPriceTotal;
    }

    public String getWinPrice() {
        return winPrice;
    }

    public void setWinPrice(String winPrice) {
        this.winPrice = winPrice;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getSettFeeAmt() {
        return settFeeAmt;
    }

    public void setSettFeeAmt(BigDecimal settFeeAmt) {
        this.settFeeAmt = settFeeAmt;
    }

    public BigDecimal getPayableAmount() {
        return payableAmount;
    }

    public void setPayableAmount(BigDecimal payableAmount) {
        this.payableAmount = payableAmount;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }


    public String getBidId() {
        return bidId;
    }

    public void setBidId(String bidId) {
        this.bidId = bidId;
    }

    public String getTradeMarket() {
        return tradeMarket;
    }

    public void setTradeMarket(String tradeMarket) {
        this.tradeMarket = tradeMarket;
    }

    public String getTradeMarketStr() {
        if (StringUtil.isEmpty(getTradeMarket())) {
            return StringUtil.EMPTY;
        }
        return SysDataUtil.getNameFromDataDict(BussinessDictCode.交易场所, getTradeMarket());
    }

    public void setTradeMarketStr(String tradeMarketStr) {
        this.tradeMarketStr = tradeMarketStr;
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

    public String getBidPrice() {
        return bidPrice;
    }

    public void setBidPrice(String bidPrice) {
        this.bidPrice = bidPrice;
    }

    public Date getBidCfmDateStart() {
        return bidCfmDateStart;
    }

    public void setBidCfmDateStart(Date bidCfmDateStart) {
        this.bidCfmDateStart = bidCfmDateStart;
    }

    public String getBidCfmDateStartStr() {
        if (null != this.getBidCfmDateStart()) {
            return DateUtil.format(this.getBidCfmDateStart());
        }
        return bidCfmDateStartStr;
    }

    public void setBidCfmDateStartStr(String bidCfmDateStartStr) {
        this.bidCfmDateStartStr = bidCfmDateStartStr;
    }

    public Date getBidCfmDateEnd() {
        return bidCfmDateEnd;
    }

    public void setBidCfmDateEnd(Date bidCfmDateEnd) {
        this.bidCfmDateEnd = bidCfmDateEnd;
    }

    public String getBidCfmDateEndStr() {
        if (null != this.getBidCfmDateEnd()) {
            return DateUtil.format(this.getBidCfmDateEnd());
        }
        return bidCfmDateEndStr;
    }

    public void setBidCfmDateEndStr(String bidCfmDateEndStr) {
        this.bidCfmDateEndStr = bidCfmDateEndStr;
    }

    public Date getDealDate() {
        return dealDate;
    }

    public void setDealDate(Date dealDate) {
        this.dealDate = dealDate;
    }

    public String getDealDateStr() {
        if (null != this.getDealDate()) {
            return DateUtil.format(this.getDealDate());
        }
        return dealDateStr;
    }

    public void setDealDateStr(String dealDateStr) {
        this.dealDateStr = dealDateStr;
    }

    public String getBidDateStr() {
        return this.getBidDateStartStr() + "-" + this.getBidDateEndStr();
    }

    public void setBidDateStr(String bidDateStr) {
        this.bidDateStr = bidDateStr;
    }

    public String getBidCfmDateStr() {
        return this.getBidCfmDateStartStr() + "-" + this.getBidCfmDateEndStr();
    }

    public void setBidCfmDateStr(String bidCfmDateStr) {
        this.bidCfmDateStr = bidCfmDateStr;
    }
}



