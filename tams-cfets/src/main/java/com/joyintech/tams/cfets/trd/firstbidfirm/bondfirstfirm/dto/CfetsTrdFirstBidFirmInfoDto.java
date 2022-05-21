package com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstfirm.dto;

import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.cfets.etr.bond.dto.CfetsEtribdoRiskcalResponse;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.invest.common.constant.BussinessDictCode;
import com.joyintech.workflow.engine.annotation.PrimaryKey;
import com.joyintech.workflow.engine.dto.ProcessEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 一级投标确认信息 页面展示及提交用
 * workflowParameter 工作流参数  operType|users|roles|submit|pageId|elementId|isTodoList
 *
 * @author jiangpeng
 * @date 2022/5/16 10:12
 */
@ApiModel(value = "交易信息", description = "一级投标确认信息")
public class CfetsTrdFirstBidFirmInfoDto extends BaseEntity implements ProcessEntity {

    @ApiModelProperty(name = "validStatus", value = "有效状态", example = "01")
    private String validStatus;

    @ApiModelProperty(name = "validStatusStr", value = "有效状态名称,展示用", example = "有效")
    private String validStatusStr;

    @ApiModelProperty(name = "finprodId", value = "资产代码", example = "F01_001.IB")
    private String finprodId;

    @ApiModelProperty(name = "finprodMarketId", value = "市场代码", example = "001.IB")
    private String finprodMarketId;

    @ApiModelProperty(name = "finprodAbbr", value = "资产简称", example = "001债券")
    private String finprodAbbr;

    @ApiModelProperty(name = "couponSpecies", value = "息票品种,计算用不显示")
    private String couponSpecies;

    @ApiModelProperty(name = "counterId", value = "交易对手id", example = "114912384")
    private String counterId;

    @ApiModelProperty(name = "counterName", value = "交易对手名", example = "广发银行")
    private String counterName;

    @ApiModelProperty(name = "ourTrader", value = "本方交易员id(申购人)", example = "001")
    private String ourTrader;

    @ApiModelProperty(name = "ourTraderName", value = "本方交易员名称", example = "交易员1")
    private String ourTraderName;

    @ApiModelProperty(name = "tenderTarget", value = "招标标的")
    private String tenderTarget;

    @ApiModelProperty(name = "tenderTargetStr", value = "招标标的格式化")
    private String tenderTargetStr;

    @ApiModelProperty(name = "ccy", value = "币种")
    private String ccy;

    @ApiModelProperty(name = "ccyStr", value = "币种名称")
    private String ccyStr;

    @ApiModelProperty(name = "bidDateStart", value = "投标日开始")
    private Date bidDateStart;

    @ApiModelProperty(name = "bidDateStartStr", value = "投标日开始格式化,展示用")
    private String bidDateStartStr;

    @ApiModelProperty(name = "bidDateEnd", value = "投标日结束")
    private Date bidDateEnd;

    @ApiModelProperty(name = "bidDateEndStr", value = "投标日结束格式化,展示用")
    private String bidDateEndStr;

    @ApiModelProperty(name = "payEdate", value = "缴款截止日")
    private Date payEdate;

    @ApiModelProperty(name = "payEdateStr", value = "缴款截止日格式化,展示用")
    private String payEdateStr;

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

    @ApiModelProperty(name = "circulationDate", value = "上市流通日 计算用,前台不展示")
    private Date circulationDate;

    @ApiModelProperty(name = "listingDateStr", value = "上市日日格式化,展示用", example = "2021-01-01")
    private String listingDateStr;

    @ApiModelProperty(name = "biddingMethod", value = "投标方式", example = "01")
    private String biddingMethod;

    @ApiModelProperty(name = "biddingMethodStr", value = "投标方式名称,展示用", example = "自主投标")
    private String biddingMethodStr;

    @ApiModelProperty(name = "payType", value = "缴款方式", example = "01")
    private String payType;

    @ApiModelProperty(name = "payTypeStr", value = "缴款方式名称,展示用", example = "一级分销")
    private String payTypeStr;

    @ApiModelProperty(name = "etrBidPriceTotal", value = "指令投标券面总额(万元) 需前端转化", example = "10000.00")
    private BigDecimal etrBidPriceTotal;

    @ApiModelProperty(name = "totalBiddingNum", value = "汇总投标券面总额(万元) 需前端转化", example = "10000.00")
    private BigDecimal totalBiddingNum;

    @ApiModelProperty(name = "bidPriceTotal", value = "券面总额(万元) 需前端转化", example = "10000.00")
    private BigDecimal bidPriceTotal;

    @ApiModelProperty(name = "winPrice", value = "中标标位", example = "100.0000")
    private BigDecimal winPrice;

    @ApiModelProperty(name = "price", value = "净价", example = "1")
    private BigDecimal price;

    @ApiModelProperty(name = "fullPrice", value = "全价", example = "1")
    private BigDecimal fullPrice;

    @ApiModelProperty(name = "intAmt", value = "应计利息")
    private BigDecimal intAmt;

    @ApiModelProperty(name = "intAmtTatol", value = "应计利息总额")
    private BigDecimal intAmtTatol;

    @ApiModelProperty(name = "settFeeAmt", value = "结算服务费", example = "1000.00")
    private BigDecimal settFeeAmt;

    @ApiModelProperty(name = "payableAmount", value = "缴款金额", example = "1000.00")
    private BigDecimal payableAmount;

    @ApiModelProperty(name = "tradeAmt", value = "交易金额", example = "1000.00")
    private BigDecimal tradeAmt;

    @ApiModelProperty(name = "remark", value = "备注", example = "XXX")
    private String remark;

    @ApiModelProperty(name = "adjustStrategy", value = "调整策略", example = "XXX")
    private String adjustStrategy;

    @ApiModelProperty(name = "dipType", value = "分配方式", example = "139")
    private String dipType;

    @PrimaryKey(PrimaryKey.Field.PK_1)
    @ApiModelProperty(name = "bidId", value = "投标编号,编辑页面/提交用", example = "111")
    private String bidId;

    @ApiModelProperty(name = "operateType", value = "操作类型,编辑页面/提交用", example = "NEW")
    private String operateType;

    @ApiModelProperty(name = "elementId", value = "提交要素,编辑页面/提交用", example = "CfetsTrdFirstBidFirm002_btn_save")
    private String elementId;

    @ApiModelProperty(name = "softSkipMode", value = "是否忽略头寸软控,编辑页面/提交用", example = "Y")
    private String softSkipMode;

    @ApiModelProperty(name = "systemAct", value = "头寸_系统动作,编辑页面/提交用", example = "Y")
    private String systemAct;

    @ApiModelProperty(name = "riskcalResponse", value = "风险实体类")
    private CfetsEtribdoRiskcalResponse riskcalResponse;

    @ApiModelProperty(name = "productDtos", value = "投标产品列表")
    private List<CfetsTrdFirstBidFirmProductDto> productDtos;

    @ApiModelProperty(name = "bidTotalList", value = "汇总投标列表")
    private List<CfetsTrdFirstBidFirmTotalDto> bidTotalList;

    @ApiModelProperty(name = "entrustDtos", value = "指令列表")
    private List<CfetsTrdFirstBidFirmEntrustDto> entrustDtos;

    @ApiModelProperty(name = "tradeMarket", value = "交易场所", example = "01")
    private String tradeMarket;

    @ApiModelProperty(name = "registOrg", value = "登记托管机构", example = "01")
    private String registOrg;

    @ApiModelProperty(name = "tradeDate", value = "交易日期 默认平台日")
    private Date tradeDate;

    @ApiModelProperty(name = "editFeild", value = "编辑字段", example = "1 基本信息的汇总投标券面总额联动 2 汇总投标列表的投标标位联动")
    private String editFeild;

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


    public String getOurTrader() {
        return ourTrader;
    }

    public void setOurTrader(String ourTrader) {
        this.ourTrader = ourTrader;
    }

    public String getOurTraderName() {
        return ourTraderName;
    }

    public void setOurTraderName(String ourTraderName) {
        this.ourTraderName = ourTraderName;
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
        return SysDataUtil.getNameFromDataDict(DictCode.招标标的, getCcy());
    }

    public void setTenderTargetStr(String tenderTargetStr) {
        this.tenderTargetStr = tenderTargetStr;
    }

    public String getCcy() {
        return ccy;
    }

    public void setCcy(String ccy) {
        this.ccy = ccy;
    }

    public String getCcyStr() {
        if (StringUtil.isEmpty(getCcy())) {
            return StringUtil.EMPTY;
        }
        return SysDataUtil.getNameFromDataDict(DictCode.币种, getCcy());
    }

    public void setCcyStr(String ccyStr) {
        this.ccyStr = ccyStr;
    }

    public Date getPayEdate() {
        return payEdate;
    }

    public void setPayEdate(Date payEdate) {
        this.payEdate = payEdate;
    }

    public String getPayEdateStr() {
        if (null != this.getPayEdate()) {
            return DateUtil.format(this.getPayEdate());
        }
        return payEdateStr;
    }

    public void setPayEdateStr(String payEdateStr) {
        this.payEdateStr = payEdateStr;
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

    public Date getCirculationDate() {
        return circulationDate;
    }

    public void setCirculationDate(Date circulationDate) {
        this.circulationDate = circulationDate;
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

    public BigDecimal getTotalBiddingNum() {
        return totalBiddingNum;
    }

    public void setTotalBiddingNum(BigDecimal totalBiddingNum) {
        this.totalBiddingNum = totalBiddingNum;
    }

    /**
     * 券面总额
     * @author jiangpeng
     * @date 2022/5/19 11:30
     * @return java.math.BigDecimal
     */
    public BigDecimal getBidPriceTotal() {
        return bidPriceTotal;
    }

    public void setBidPriceTotal(BigDecimal bidPriceTotal) {
        this.bidPriceTotal = bidPriceTotal;
    }

    public BigDecimal getWinPrice() {
        return winPrice;
    }

    public void setWinPrice(BigDecimal winPrice) {
        this.winPrice = winPrice;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getFullPrice() {
        return fullPrice;
    }

    public void setFullPrice(BigDecimal fullPrice) {
        this.fullPrice = fullPrice;
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

    public BigDecimal getTradeAmt() {
        return tradeAmt;
    }

    public void setTradeAmt(BigDecimal tradeAmt) {
        this.tradeAmt = tradeAmt;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getAdjustStrategy() {
        return adjustStrategy;
    }

    public void setAdjustStrategy(String adjustStrategy) {
        this.adjustStrategy = adjustStrategy;
    }

    public String getDipType() {
        return dipType;
    }

    public void setDipType(String dipType) {
        this.dipType = dipType;
    }

    public String getBidId() {
        return bidId;
    }

    public void setBidId(String bidId) {
        this.bidId = bidId;
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

    public String getSoftSkipMode() {
        return softSkipMode;
    }

    public void setSoftSkipMode(String softSkipMode) {
        this.softSkipMode = softSkipMode;
    }

    public String getSystemAct() {
        return systemAct;
    }

    public void setSystemAct(String systemAct) {
        this.systemAct = systemAct;
    }

    public CfetsEtribdoRiskcalResponse getRiskcalResponse() {
        return riskcalResponse;
    }

    public void setRiskcalResponse(CfetsEtribdoRiskcalResponse riskcalResponse) {
        this.riskcalResponse = riskcalResponse;
    }

    public List<CfetsTrdFirstBidFirmProductDto> getProductDtos() {
        return productDtos;
    }

    public void setProductDtos(List<CfetsTrdFirstBidFirmProductDto> productDtos) {
        this.productDtos = productDtos;
    }

    public List<CfetsTrdFirstBidFirmTotalDto> getBidTotalList() {
        return bidTotalList;
    }

    public void setBidTotalList(List<CfetsTrdFirstBidFirmTotalDto> bidTotalList) {
        this.bidTotalList = bidTotalList;
    }

    public String getTradeMarket() {
        return tradeMarket;
    }

    public void setTradeMarket(String tradeMarket) {
        this.tradeMarket = tradeMarket;
    }

    public String getRegistOrg() {
        return registOrg;
    }

    public void setRegistOrg(String registOrg) {
        this.registOrg = registOrg;
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

    public Date getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(Date tradeDate) {
        this.tradeDate = tradeDate;
    }

    public String getCouponSpecies() {
        return couponSpecies;
    }

    public void setCouponSpecies(String couponSpecies) {
        this.couponSpecies = couponSpecies;
    }

    public List<CfetsTrdFirstBidFirmEntrustDto> getEntrustDtos() {
        return entrustDtos;
    }

    public void setEntrustDtos(List<CfetsTrdFirstBidFirmEntrustDto> entrustDtos) {
        this.entrustDtos = entrustDtos;
    }

    public String getEditFeild() {
        return editFeild;
    }

    public void setEditFeild(String editFeild) {
        this.editFeild = editFeild;
    }
}



