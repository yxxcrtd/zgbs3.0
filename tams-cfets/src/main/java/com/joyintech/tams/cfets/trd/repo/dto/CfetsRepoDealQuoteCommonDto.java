package com.joyintech.tams.cfets.trd.repo.dto;

import com.joyintech.background.component.common.pack.common.base.BaseEntity;
import com.joyintech.workflow.engine.dto.ProcessEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author jiangpeng
 * @date 2021/12/6 17:20
 */
@ApiModel(value = "质押式回购报价实体(输入输出共用)", description = "质押式回购报价实体(输入输出共用)")
public class CfetsRepoDealQuoteCommonDto extends BaseEntity implements ProcessEntity {

    @ApiModelProperty(name = "quoteId", value = "报价编号(内部)")
    private String quoteId;

    @ApiModelProperty(name = "cfetsQuoteId", value = "报价编号(外部)")
    private String cfetsQuoteId;

    @ApiModelProperty(name = "totleTradeId", value = "交易编号")
    private String totleTradeId;

    @ApiModelProperty(name = "elementId", value = "按钮id")
    private String elementId;

    @ApiModelProperty(name = "operateType", value = "操作类型", example = "save")
    private String operateType;

    @ApiModelProperty(name = "operatorType", value = "操作类型", example = "done")
    private String operatorType;

    @ApiModelProperty(name = "quotaPageModel", value = "报价操作类型(修改,回复,撤回,回退)", example = "copy")
    private String quotaPageModel;

    @ApiModelProperty(name = "validStatus", value = "有效状态", example = "01")
    private String validStatus;

    @ApiModelProperty(name = "openModel", value = "打开入口", example = "copy:表示复制入口")
    private String openModel;

    @ApiModelProperty(name = "quoteStyle", value = "报价方式")
    private String quoteStyle;

    @ApiModelProperty(name = "ps", value = "交易方向", example = "03")
    private String ps;

    @ApiModelProperty(name = "mySide", value = "本方")
    private String mySide;

    @ApiModelProperty(name = "mySideTrader", value = "本方交易员")
    private String mySideTrader;

    @ApiModelProperty(name = "loginUserId", value = "登录用户(系统内)")
    private String loginUserId;

    @ApiModelProperty(name = "ouTraderName", value = "本方交易员名称")
    private String ouTraderName;

    @ApiModelProperty(name = "counterId", value = "对手方")
    private String counterId;

    @ApiModelProperty(name = "counterPartyId", value = "对手方(21位码)")
    private String counterPartyId;

    @ApiModelProperty(name = "counterTrader", value = "对手方交易员")
    private String counterTrader;

    @ApiModelProperty(name = "repoMethod", value = "回购方式", example = "1")
    private String repoMethod;

    @ApiModelProperty(name = "settleSpreedCode", value = "清算速度", example = "01")
    private String settleSpreedCode;

    @ApiModelProperty(name = "ccy", value = "币种", example = "CNY")
    private String ccy;

    @ApiModelProperty(name = "repoTerm", value = "回购期限", example = "7")
    private int repoTerm;

    @ApiModelProperty(name = "price", value = "回购利率%", example = "3")
    private BigDecimal price;

    @ApiModelProperty(name = "settCcy", value = "结算币种", example = "CNY")
    private String settCcy;

    @ApiModelProperty(name = "tradeAmt", value = "交易金额(元)", example = "200000")
    private BigDecimal tradeAmt;

    @ApiModelProperty(name = "shareAmt", value = "券面总额合计(万元)", example = "20")
    private BigDecimal shareAmt;

    @ApiModelProperty(name = "convertAmt", value = "质押券折算总额(元)", example = "200000")
    private BigDecimal convertAmt;

    @ApiModelProperty(name = "deliverytype", value = "首次结算方式", example = "01")
    private String deliverytype;

    @ApiModelProperty(name = "mDeliveryType", value = "到期结算方式", example = "01")
    private String mDeliveryType;

    @ApiModelProperty(name = "settleType", value = "清算类型", example = "01")
    private String settleType;

    @ApiModelProperty(name = "dealFeeAmt", value = "交易手续费", example = "1200")
    private BigDecimal dealFeeAmt;

    @ApiModelProperty(name = "settFeeAmt", value = "结算服务费", example = "1200")
    private BigDecimal settFeeAmt;

    @ApiModelProperty(name = "remark", value = "备注", example = "xxx")
    private String remark;

    @ApiModelProperty(name = "distribution", value = "分配方式", example = "139")
    private String distribution;

    @ApiModelProperty(name = "ptlAllocations", value = "组合分配")
    private List<RepoPtlAllocationDto> ptlAllocations;

    @ApiModelProperty(name = "repoEntrustPledgeDtos", value = "质押券信息")
    private List<RepoEntrustPledgeDto> repoEntrustPledgeDtos;

    @ApiModelProperty(name = "countPledgeBondList", value = "对手方质押券信息")
    private List<RepoEntrustPledgeDto> countPledgeBondList;

    @ApiModelProperty(name = "tradeDate", value = "交易日期", example = "2021-07-08")
    private Date tradeDate;

    @ApiModelProperty(name = "settlementStartDate", value = "首次结算日", example = "2021-07-08")
    private Date settlementStartDate;

    @ApiModelProperty(name = "settlementEndDate", value = "到期结算日", example = "2021-07-15")
    private Date settlementEndDate;

    @ApiModelProperty(name = "mIntAmt", value = "回购利息", example = "1.1")
    private BigDecimal mIntAmt;

    @ApiModelProperty(name = "mtradeAmt", value = "到期结算金额", example = "10001")
    private BigDecimal mtradeAmt;

    @ApiModelProperty(name = "actCapDays", value = "实际占款天数(天)", example = "7")
    private Long actCapDays;

    @ApiModelProperty(name = "termType", value = "期限品种", example = "R007")
    private String termType;

    @ApiModelProperty(name = "tradeMarket", value = "交易场所", example = "01")
    private String tradeMarket;

    @ApiModelProperty(name = "secManageAcctId", value = "证券账户")
    private String secManageAcctId;

    @ApiModelProperty(name = "priceModelClass", value = "价格模式种类,只有指定价格01,只有限价指令02,两种都有03")
    private String priceModelClass;

    @ApiModelProperty(name = "softSkipMode", value = "软控是否跳过校验", example = "Y")
    private String softSkipMode;

    @ApiModelProperty(name = "dialogueRecord", value = "交谈记录", example = "xx")
    private String dialogueRecord;

    @ApiModelProperty(name = "sendStatus", value = "发送状态", example = "01")
    private String sendStatus;

    @ApiModelProperty(name = "tradeStatus", value = "交易状态", example = "01")
    private String tradeStatus;
    /**
     * 决策动作
     */
    private String systemAct;

    /**
     * 版本号
     */
    private Long version;

    /**
     * 前置版本号
     */
    private Long bVersion;

    public String getQuoteId() {
        return quoteId;
    }

    public void setQuoteId(String quoteId) {
        this.quoteId = quoteId;
    }

    public String getCfetsQuoteId() {
        return cfetsQuoteId;
    }

    public void setCfetsQuoteId(String cfetsQuoteId) {
        this.cfetsQuoteId = cfetsQuoteId;
    }

    public String getTotleTradeId() {
        return totleTradeId;
    }

    public void setTotleTradeId(String totleTradeId) {
        this.totleTradeId = totleTradeId;
    }

    public String getOperateType() {
        return operateType;
    }

    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }

    public String getQuotaPageModel() {
        return quotaPageModel;
    }

    public void setQuotaPageModel(String quotaPageModel) {
        this.quotaPageModel = quotaPageModel;
    }

    public String getValidStatus() {
        return validStatus;
    }

    public void setValidStatus(String validStatus) {
        this.validStatus = validStatus;
    }

    public String getOpenModel() {
        return openModel;
    }

    public void setOpenModel(String openModel) {
        this.openModel = openModel;
    }

    public String getQuoteStyle() {
        return quoteStyle;
    }

    public void setQuoteStyle(String quoteStyle) {
        this.quoteStyle = quoteStyle;
    }

    public String getPs() {
        return ps;
    }

    public void setPs(String ps) {
        this.ps = ps;
    }

    public String getMySide() {
        return mySide;
    }

    public void setMySide(String mySide) {
        this.mySide = mySide;
    }

    public String getMySideTrader() {
        return mySideTrader;
    }

    public void setMySideTrader(String mySideTrader) {
        this.mySideTrader = mySideTrader;
    }

    public String getOuTraderName() {
        return ouTraderName;
    }

    public void setOuTraderName(String ouTraderName) {
        this.ouTraderName = ouTraderName;
    }

    public String getCounterId() {
        return counterId;
    }

    public void setCounterId(String counterId) {
        this.counterId = counterId;
    }

    public String getCounterTrader() {
        return counterTrader;
    }

    public void setCounterTrader(String counterTrader) {
        this.counterTrader = counterTrader;
    }

    public String getRepoMethod() {
        return repoMethod;
    }

    public void setRepoMethod(String repoMethod) {
        this.repoMethod = repoMethod;
    }

    public String getSettleSpreedCode() {
        return settleSpreedCode;
    }

    public void setSettleSpreedCode(String settleSpreedCode) {
        this.settleSpreedCode = settleSpreedCode;
    }

    public String getCcy() {
        return ccy;
    }

    public void setCcy(String ccy) {
        this.ccy = ccy;
    }

    public int getRepoTerm() {
        return repoTerm;
    }

    public void setRepoTerm(int repoTerm) {
        this.repoTerm = repoTerm;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getSettCcy() {
        return settCcy;
    }

    public void setSettCcy(String settCcy) {
        this.settCcy = settCcy;
    }

    public BigDecimal getTradeAmt() {
        return tradeAmt;
    }

    public void setTradeAmt(BigDecimal tradeAmt) {
        this.tradeAmt = tradeAmt;
    }

    public BigDecimal getShareAmt() {
        return shareAmt;
    }

    public void setShareAmt(BigDecimal shareAmt) {
        this.shareAmt = shareAmt;
    }

    public BigDecimal getConvertAmt() {
        return convertAmt;
    }

    public void setConvertAmt(BigDecimal convertAmt) {
        this.convertAmt = convertAmt;
    }

    public String getDeliverytype() {
        return deliverytype;
    }

    public void setDeliverytype(String deliverytype) {
        this.deliverytype = deliverytype;
    }

    public String getmDeliveryType() {
        return mDeliveryType;
    }

    public void setmDeliveryType(String mDeliveryType) {
        this.mDeliveryType = mDeliveryType;
    }

    public String getSettleType() {
        return settleType;
    }

    public void setSettleType(String settleType) {
        this.settleType = settleType;
    }

    public BigDecimal getDealFeeAmt() {
        return dealFeeAmt;
    }

    public void setDealFeeAmt(BigDecimal dealFeeAmt) {
        this.dealFeeAmt = dealFeeAmt;
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

    public String getDistribution() {
        return distribution;
    }

    public void setDistribution(String distribution) {
        this.distribution = distribution;
    }

    public List<RepoPtlAllocationDto> getPtlAllocations() {
        return ptlAllocations;
    }

    public void setPtlAllocations(List<RepoPtlAllocationDto> ptlAllocations) {
        this.ptlAllocations = ptlAllocations;
    }

    public List<RepoEntrustPledgeDto> getRepoEntrustPledgeDtos() {
        return repoEntrustPledgeDtos;
    }

    public void setRepoEntrustPledgeDtos(List<RepoEntrustPledgeDto> repoEntrustPledgeDtos) {
        this.repoEntrustPledgeDtos = repoEntrustPledgeDtos;
    }

    public Date getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(Date tradeDate) {
        this.tradeDate = tradeDate;
    }

    public Date getSettlementStartDate() {
        return settlementStartDate;
    }

    public void setSettlementStartDate(Date settlementStartDate) {
        this.settlementStartDate = settlementStartDate;
    }

    public Date getSettlementEndDate() {
        return settlementEndDate;
    }

    public void setSettlementEndDate(Date settlementEndDate) {
        this.settlementEndDate = settlementEndDate;
    }

    public BigDecimal getmIntAmt() {
        return mIntAmt;
    }

    public void setmIntAmt(BigDecimal mIntAmt) {
        this.mIntAmt = mIntAmt;
    }

    public BigDecimal getMtradeAmt() {
        return mtradeAmt;
    }

    public void setMtradeAmt(BigDecimal mtradeAmt) {
        this.mtradeAmt = mtradeAmt;
    }

    public Long getActCapDays() {
        return actCapDays;
    }

    public void setActCapDays(Long actCapDays) {
        this.actCapDays = actCapDays;
    }

    public String getTermType() {
        return termType;
    }

    public void setTermType(String termType) {
        this.termType = termType;
    }

    public String getTradeMarket() {
        return tradeMarket;
    }

    public void setTradeMarket(String tradeMarket) {
        this.tradeMarket = tradeMarket;
    }

    public String getSecManageAcctId() {
        return secManageAcctId;
    }

    public void setSecManageAcctId(String secManageAcctId) {
        this.secManageAcctId = secManageAcctId;
    }

    public String getPriceModelClass() {
        return priceModelClass;
    }

    public void setPriceModelClass(String priceModelClass) {
        this.priceModelClass = priceModelClass;
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

    public String getOperatorType() {
        return operatorType;
    }

    public void setOperatorType(String operatorType) {
        this.operatorType = operatorType;
    }

    public String getDialogueRecord() {
        return dialogueRecord;
    }

    public void setDialogueRecord(String dialogueRecord) {
        this.dialogueRecord = dialogueRecord;
    }

    public List<RepoEntrustPledgeDto> getCountPledgeBondList() {
        return countPledgeBondList;
    }

    public void setCountPledgeBondList(List<RepoEntrustPledgeDto> countPledgeBondList) {
        this.countPledgeBondList = countPledgeBondList;
    }

    public String getCounterPartyId() {
        return counterPartyId;
    }

    public void setCounterPartyId(String counterPartyId) {
        this.counterPartyId = counterPartyId;
    }

    public String getSendStatus() {
        return sendStatus;
    }

    public void setSendStatus(String sendStatus) {
        this.sendStatus = sendStatus;
    }

    public String getTradeStatus() {
        return tradeStatus;
    }

    public void setTradeStatus(String tradeStatus) {
        this.tradeStatus = tradeStatus;
    }

    public String getLoginUserId() {
        return loginUserId;
    }

    public void setLoginUserId(String loginUserId) {
        this.loginUserId = loginUserId;
    }

    public String getSystemAct() {
        return systemAct;
    }

    public void setSystemAct(String systemAct) {
        this.systemAct = systemAct;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Long getbVersion() {
        return bVersion;
    }

    public void setbVersion(Long bVersion) {
        this.bVersion = bVersion;
    }
}

