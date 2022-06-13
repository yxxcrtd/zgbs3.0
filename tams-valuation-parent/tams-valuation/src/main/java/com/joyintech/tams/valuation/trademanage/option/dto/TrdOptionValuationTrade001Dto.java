package com.joyintech.tams.valuation.trademanage.option.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.framework.validation.annotation.NotEmpty;
import com.joyintech.framework.validation.annotation.ValidLogic;
import com.joyintech.framework.validation.groups.ValidateGroupDelete;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.valuation.trademanage.option.validator.TrdOptionValuationTrade001Validator;
import com.joyintech.workflow.engine.annotation.PrimaryKey;
import com.joyintech.workflow.engine.dto.ProcessEntity;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

/**
 * 期权交易新增dto
 */
@PageId("TrdOptionValuationTrade002")
@ValidLogic(targetClass = TrdOptionValuationTrade001Validator.class, groups = { ValidateGroupEdit.class,
        ValidateGroupDelete.class })
public class TrdOptionValuationTrade001Dto extends BaseEntity implements ProcessEntity {
    /**
     * 操作类型
     */
    private String operateType;

    private String pageModel;

    /**
     * 选择数据
     */
    private List<TrdOptionValuationTrade001Dto> selectedItem;

    /**
     * 要素id
     */
    private String elementId;
    /**
     * 交易编号
     */
    @PrimaryKey(value= PrimaryKey.Field.PK_1)
    private String tradeId;
    /**
     * 外部成交编号
     */
    private String outTradeId;
    /**
     * 金融产品代码
     */
    private String prodId;
    /**
     * 金融产品名称
     */
    private String prodName;
    /**
     * 合约标的
     */
    private String targetId;
    /**
     * 合约类型
     */
    private String contractType;
    /**
     * 交易方向
     */
    private String ps;
    /**
     * 交易日期
     */
    private Date tradeDate;
    /**
     * 借贷期限
     */
    private Long term;
    /**
     * 起息日
     */
    private Date vdate;
    /**
     * 到期日
     */
    private Date mdate;
    /**
     * 名义本金
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal nomCapi;
    /**
     * 行权日基准日
     */
    private Date exerciseDate;
    /**
     * 执行价格
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal exePrice;

    /**
     * 敲出价格
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal knockPrice;
    /**
     * 低障碍价
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal lowHindPrice;
    /**
     * 低行权价
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal lowExercPrice;
    /**
     * 高障碍价
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal highHindPrice;
    /**
     * 高行权价
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal highExercPrice;
    /**
     * 行权方式
     */
    private String exerciseType;
    /**
     * 期权费类型
     */
    private String optFeePayType;
    /**
     * 期权费
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal optFee;
    /**
     * 期权费率
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal optFeeRate;
    /**
     * 币种
     */
    private String ccy;
    /**
     * 交易对手
     */
    private String counterId;
    /**
     * 其他应付费用
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal otherAmt;
    /**
     * 结算金额
     */
    private BigDecimal settleAmt;

    /**
     * 资金账户
     */
    private String acctNo;


    public String getOperateType() {
        return operateType;
    }

    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }

    public String getPageModel() {
        return pageModel;
    }

    public void setPageModel(String pageModel) {
        this.pageModel = pageModel;
    }

    public List<TrdOptionValuationTrade001Dto> getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(List<TrdOptionValuationTrade001Dto> selectedItem) {
        this.selectedItem = selectedItem;
    }

    public String getElementId() {
        return elementId;
    }

    public void setElementId(String elementId) {
        this.elementId = elementId;
    }

    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }

    public String getOutTradeId() {
        return outTradeId;
    }

    public void setOutTradeId(String outTradeId) {
        this.outTradeId = outTradeId;
    }

    @NotEmpty(fieldName = "TrdOptionValuationTrade.prodId", groups = { ValidateGroupEdit.class })
    public String getProdId() {
        return prodId;
    }

    public void setProdId(String prodId) {
        this.prodId = prodId;
    }

    @NotEmpty(fieldName = "TrdOptionValuationTrade.prodName", groups = { ValidateGroupEdit.class })
    public String getProdName() {
        return prodName;
    }

    public void setProdName(String prodName) {
        this.prodName = prodName;
    }

    @NotEmpty(fieldName = "TrdOptionValuationTrade.targetId", groups = { ValidateGroupEdit.class })
    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    @NotEmpty(fieldName = "TrdOptionValuationTrade.contractType", groups = { ValidateGroupEdit.class })
    public String getContractType() {
        return contractType;
    }

    public void setContractType(String contractType) {
        this.contractType = contractType;
    }

    /**
     * 取得 结构类型Fmt
     *
     * @return 结构类型
     */
    public String getContractTypeFmt() {
        return StringUtil.isNotEmpty(SysDataUtil.getNameFromDataDict(DictCode.结构类型, contractType))?
                SysDataUtil.getNameFromDataDict(DictCode.结构类型, contractType):contractType;
    }

    @NotEmpty(fieldName = "TrdOptionValuationTrade.ps", groups = { ValidateGroupEdit.class })
    public String getPs() {
        return ps;
    }

    public void setPs(String ps) {
        this.ps = ps;
    }

    /**
     * 取得 交易方向str
     *
     * @return 交易方向
     */
    public String getPsStr() {
        return StringUtil.isNotEmpty(SysDataUtil.getNameFromDataDict(DictCode.交易方向, ps))?
                SysDataUtil.getNameFromDataDict(DictCode.交易方向, ps):ps;
    }

    @NotEmpty(fieldName = "TrdOptionValuationTrade.tradeDate", groups = { ValidateGroupEdit.class })
    public Date getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(Date tradeDate) {
        this.tradeDate = tradeDate;
    }

    public String getTradeDateStr() {
        return DateUtil.format(this.getTradeDate());
    }

    @NotEmpty(fieldName = "TrdOptionValuationTrade.term", groups = { ValidateGroupEdit.class })
    public Long getTerm() {
        return term;
    }

    public void setTerm(Long term) {
        this.term = term;
    }

    @NotEmpty(fieldName = "TrdOptionValuationTrade.vdate", groups = { ValidateGroupEdit.class })
    public Date getVdate() {
        return vdate;
    }

    public void setVdate(Date vdate) {
        this.vdate = vdate;
    }

    public String getVdateStr() {
        return DateUtil.format(this.getVdate());
    }

    @NotEmpty(fieldName = "TrdOptionValuationTrade.mdate", groups = { ValidateGroupEdit.class })
    public Date getMdate() {
        return mdate;
    }

    public void setMdate(Date mdate) {
        this.mdate = mdate;
    }

    public String getMdateStr() {
        return DateUtil.format(this.getMdate());
    }

    @NotEmpty(fieldName = "TrdOptionValuationTrade.nomCapi", groups = { ValidateGroupEdit.class })
    public BigDecimal getNomCapi() {
        return nomCapi;
    }

    public void setNomCapi(BigDecimal nomCapi) {
        this.nomCapi = nomCapi;
    }

    @NotEmpty(fieldName = "TrdOptionValuationTrade.exerciseDate", groups = { ValidateGroupEdit.class })
    public Date getExerciseDate() {
        return exerciseDate;
    }

    public void setExerciseDate(Date exerciseDate) {
        this.exerciseDate = exerciseDate;
    }

    @NotEmpty(fieldName = "TrdOptionValuationTrade.exePrice", groups = { ValidateGroupEdit.class })
    public BigDecimal getExePrice() {
        return exePrice;
    }

    public void setExePrice(BigDecimal exePrice) {
        this.exePrice = exePrice;
    }

    public BigDecimal getKnockPrice() {
        return knockPrice;
    }

    public void setKnockPrice(BigDecimal knockPrice) {
        this.knockPrice = knockPrice;
    }

    public BigDecimal getLowHindPrice() {
        return lowHindPrice;
    }

    public void setLowHindPrice(BigDecimal lowHindPrice) {
        this.lowHindPrice = lowHindPrice;
    }

    public BigDecimal getLowExercPrice() {
        return lowExercPrice;
    }

    public void setLowExercPrice(BigDecimal lowExercPrice) {
        this.lowExercPrice = lowExercPrice;
    }

    public BigDecimal getHighHindPrice() {
        return highHindPrice;
    }

    public void setHighHindPrice(BigDecimal highHindPrice) {
        this.highHindPrice = highHindPrice;
    }

    public BigDecimal getHighExercPrice() {
        return highExercPrice;
    }

    public void setHighExercPrice(BigDecimal highExercPrice) {
        this.highExercPrice = highExercPrice;
    }

    @NotEmpty(fieldName = "TrdOptionValuationTrade.exerciseType", groups = { ValidateGroupEdit.class })
    public String getExerciseType() {
        return exerciseType;
    }

    public void setExerciseType(String exerciseType) {
        this.exerciseType = exerciseType;
    }

    @NotEmpty(fieldName = "TrdOptionValuationTrade.optFeePayType", groups = { ValidateGroupEdit.class })
    public String getOptFeePayType() {
        return optFeePayType;
    }

    public void setOptFeePayType(String optFeePayType) {
        this.optFeePayType = optFeePayType;
    }

    public BigDecimal getOptFee() {
        return optFee;
    }

    public void setOptFee(BigDecimal optFee) {
        this.optFee = optFee;
    }

    @NotEmpty(fieldName = "TrdOptionValuationTrade.optFeeRate", groups = { ValidateGroupEdit.class })
    public BigDecimal getOptFeeRate() {
        return optFeeRate;
    }

    public void setOptFeeRate(BigDecimal optFeeRate) {
        this.optFeeRate = optFeeRate;
    }

    @NotEmpty(fieldName = "TrdOptionValuationTrade.ccy", groups = { ValidateGroupEdit.class })
    public String getCcy() {
        return ccy;
    }

    public void setCcy(String ccy) {
        this.ccy = ccy;
    }

    /**
     * 取得 币种Fmt
     *
     * @return 币种
     */
    public String getCcyFmt() {
        return StringUtil.isNotEmpty(SysDataUtil.getNameFromDataDict(DictCode.币种, ccy))?
                SysDataUtil.getNameFromDataDict(DictCode.币种, ccy):ccy;
    }

    @NotEmpty(fieldName = "TrdOptionValuationTrade.counterId", groups = { ValidateGroupEdit.class })
    public String getCounterId() {
        return counterId;
    }

    public void setCounterId(String counterId) {
        this.counterId = counterId;
    }

    @NotEmpty(fieldName = "TrdOptionValuationTrade.otherAmt", groups = { ValidateGroupEdit.class })
    public BigDecimal getOtherAmt() {
        return otherAmt;
    }

    public void setOtherAmt(BigDecimal otherAmt) {
        this.otherAmt = otherAmt;
    }

    @NotEmpty(fieldName = "TrdOptionValuationTrade.settleAmt", groups = { ValidateGroupEdit.class })
    public BigDecimal getSettleAmt() {
        return settleAmt;
    }

    public void setSettleAmt(BigDecimal settleAmt) {
        this.settleAmt = settleAmt;
    }

    @NotEmpty(fieldName = "TrdOptionValuationTrade.acctNo", groups = { ValidateGroupEdit.class })
    public String getAcctNo() {
        return acctNo;
    }

    public void setAcctNo(String acctNo) {
        this.acctNo = acctNo;
    }

    public String getNomCapiStr() {
        DecimalFormat decimalFormat = new DecimalFormat("0.00#");
        return this.getNomCapi()==null?"0.00":decimalFormat.format(this.getNomCapi().setScale(2,BigDecimal.ROUND_HALF_UP));
    }

    public String getOptFeeStr() {
        DecimalFormat decimalFormat = new DecimalFormat("0.00#");
        return this.getOptFee()==null?"0.00":decimalFormat.format(this.getOptFee().setScale(2,BigDecimal.ROUND_HALF_UP));
    }

    public String getSettleAmtStr() {
        DecimalFormat decimalFormat = new DecimalFormat("0.00#");
        return this.getSettleAmt()==null?"0.00":decimalFormat.format(this.getSettleAmt().setScale(2,BigDecimal.ROUND_HALF_UP));
    }
}
