package com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstsub.dto;

import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.workflow.engine.annotation.PrimaryKey;
import com.joyintech.workflow.engine.dto.ProcessEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 一级申购信息 页面展示及提交用
 * workflowParameter 工作流参数  operType|users|roles|submit|pageId|elementId|isTodoList
 *
 * @author jiangpeng
 * @date 2022/4/29 10:54
 */
@ApiModel(value = "交易信息", description = "一级申购信息")
public class CfetsTrdBondFirstSubInfoDto extends BaseEntity implements ProcessEntity {

    @ApiModelProperty(name = "validStatus", value = "有效状态, pageDaySubDetail 接口", example = "01")
    private String validStatus;

    @ApiModelProperty(name = "validStatusStr", value = "有效状态名称, pageDaySubDetail 接口 展示用", example = "有效")
    private String validStatusStr;

    @ApiModelProperty(name = "finprodId", value = "资产代码", example = "F01_001.IB")
    private String finprodId;

    @ApiModelProperty(name = "finprodMarketId", value = "市场代码", example = "001.IB")
    private String finprodMarketId;

    @ApiModelProperty(name = "finprodAbbr", value = "资产简称", example = "001债券")
    private String finprodAbbr;

    @ApiModelProperty(name = "ouPartyId", value = "本方id", example = "114912384")
    private String ouPartyId;

    @ApiModelProperty(name = "ouPartyIdAbbr", value = "本方简称", example = "广发银行")
    private String ouPartyIdAbbr;

    @ApiModelProperty(name = "ourTrader", value = "本方交易员id(申购人)", example = "001")
    private String ourTrader;

    @ApiModelProperty(name = "ourTraderName", value = "本方交易员名称", example = "交易员1")
    private String ourTraderName;

    @ApiModelProperty(name = "issuePrice", value = "发行价格", example = "99.0000")
    private BigDecimal issuePrice;

    @ApiModelProperty(name = "referenceRate", value = "参考收益率", example = "99.0000")
    private BigDecimal referenceRate;

    @ApiModelProperty(name = "referenceRateStr", value = "参考收益率,当带入资产的息票品种为附息-浮动利率时，显示字段参考收益率，文本，形式为：基准利率+利差BP", example = "个人活期+1")
    private String referenceRateStr;

    @ApiModelProperty(name = "rate", value = "票面利率", example = "99.0000")
    private BigDecimal rate;

    @ApiModelProperty(name = "subscriptionAmount", value = "认购量", example = "1000.00")
    private BigDecimal subscriptionAmount;

    @ApiModelProperty(name = "payableAmount", value = "应缴款金额", example = "1000.00")
    private BigDecimal payableAmount;

    @ApiModelProperty(name = "settFeeAmt", value = "结算服务费", example = "1000.00")
    private BigDecimal settFeeAmt;

    @ApiModelProperty(name = "remark", value = "备注", example = "XXX")
    private String remark;

    @ApiModelProperty(name = "tradeDate", value = "申购日期")
    private Date tradeDate;

    @ApiModelProperty(name = "tradeDateStr", value = "申购日期格式化")
    private String tradeDateStr;

    @ApiModelProperty(name = "dipType", value = "分配方式", example = "139")
    private String dipType;

    @ApiModelProperty(name = "productDtos", value = "申购产品列表")
    private List<CfetsTrdBondFirstSubProductDto> productDtos;

    @ApiModelProperty(name = "entrustDtos", value = "指令列表 addOrDelList接口新增指令时必传")
    private List<CfetsTrdBondFirstSubEntrustDto> entrustDtos;

    @PrimaryKey(PrimaryKey.Field.PK_1)
    @ApiModelProperty(name = "bidId", value = "投标编号,编辑页面/提交用", example = "111")
    private String bidId;

    @ApiModelProperty(name = "operateType", value = "操作类型,编辑页面/提交用", example = "NEW")
    private String operateType;

    @ApiModelProperty(name = "elementId", value = "提交要素,编辑页面/提交用", example = "TenderBondSub002_btn_save")
    private String elementId;

    @ApiModelProperty(name = "softSkipMode", value = "是否忽略头寸软控,编辑页面/提交用", example = "Y")
    private String softSkipMode;

    @ApiModelProperty(name = "systemAct", value = "头寸_系统动作,编辑页面/提交用", example = "Y")
    private String systemAct;

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

    public String getOuPartyId() {
        return ouPartyId;
    }

    public void setOuPartyId(String ouPartyId) {
        this.ouPartyId = ouPartyId;
    }

    public String getOuPartyIdAbbr() {
        return ouPartyIdAbbr;
    }

    public void setOuPartyIdAbbr(String ouPartyIdAbbr) {
        this.ouPartyIdAbbr = ouPartyIdAbbr;
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

    public BigDecimal getIssuePrice() {
        return issuePrice;
    }

    public void setIssuePrice(BigDecimal issuePrice) {
        this.issuePrice = issuePrice;
    }

    public BigDecimal getReferenceRate() {
        return referenceRate;
    }

    public void setReferenceRate(BigDecimal referenceRate) {
        this.referenceRate = referenceRate;
    }

    public String getReferenceRateStr() {
        return referenceRateStr;
    }

    public void setReferenceRateStr(String referenceRateStr) {
        this.referenceRateStr = referenceRateStr;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public BigDecimal getSubscriptionAmount() {
        return subscriptionAmount;
    }

    public void setSubscriptionAmount(BigDecimal subscriptionAmount) {
        this.subscriptionAmount = subscriptionAmount;
    }

    public BigDecimal getPayableAmount() {
        return payableAmount;
    }

    public void setPayableAmount(BigDecimal payableAmount) {
        this.payableAmount = payableAmount;
    }

    public BigDecimal getSettFeeAmt() {
        return settFeeAmt;
    }

    public void setSettFeeAmt(BigDecimal settFeeAmt) {
        this.settFeeAmt = settFeeAmt;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getDipType() {
        return dipType;
    }

    public void setDipType(String dipType) {
        this.dipType = dipType;
    }

    public Date getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(Date tradeDate) {
        this.tradeDate = tradeDate;
    }

    public String getTradeDateStr() {
        if (null != this.getTradeDate()) {
            return DateUtil.format(this.getTradeDate());
        }
        return tradeDateStr;
    }

    public void setTradeDateStr(String tradeDateStr) {
        this.tradeDateStr = tradeDateStr;
    }

    public List<CfetsTrdBondFirstSubProductDto> getProductDtos() {
        return productDtos;
    }

    public void setProductDtos(List<CfetsTrdBondFirstSubProductDto> productDtos) {
        this.productDtos = productDtos;
    }

    public List<CfetsTrdBondFirstSubEntrustDto> getEntrustDtos() {
        return entrustDtos;
    }

    public void setEntrustDtos(List<CfetsTrdBondFirstSubEntrustDto> entrustDtos) {
        this.entrustDtos = entrustDtos;
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
}



