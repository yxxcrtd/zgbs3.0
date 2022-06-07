package com.joyintech.tams.cfets.trd.repo.dto;

import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.invest.common.constant.BussinessDictCode;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Date;

/**
 * @author jiangpeng
 * @date 2021/12/14
 */
@ApiModel(value = "当日登记", description = "当日登记")
public class CfetsRepoDayRegDto {
    @ApiModelProperty(name = "validStatus", value = "有效状态")
    private String validStatus;

    @ApiModelProperty(name = "validStatusStr", value = "有效状态名称")
    private String validStatusStr;

    @ApiModelProperty(name = "quoteType", value = "报价方式")
    private String quoteType;

    @ApiModelProperty(name = "quoteTypeStr", value = "报价方式名称")
    private String quoteTypeStr;

    @ApiModelProperty(name = "tradeAmt", value = "交易金额(元)")
    private BigDecimal tradeAmt;

    @ApiModelProperty(name = "rate", value = "回购利率(%)")
    private BigDecimal rate;

    @ApiModelProperty(name = "repoTerm", value = "回购期限(天)")
    private Long repoTerm;

    @ApiModelProperty(name = "ps", value = "交易方向")
    private String ps;

    @ApiModelProperty(name = "psStr", value = "本方方向名称")
    private String psStr;

    @ApiModelProperty(name = "ouPartyId", value = "本方")
    private String ouPartyId;

    @ApiModelProperty(name = "ouTraderId", value = "本方交易员")
    private String ouTraderId;

    @ApiModelProperty(name = "repoType", value = "回购方式")
    private String repoType;

    @ApiModelProperty(name = "repoTypeStr", value = "回购方式")
    private String repoTypeStr;

    @ApiModelProperty(name = "ouPartyName", value = "本方名称")
    private String ouPartyName;

    @ApiModelProperty(name = "ouTraderName", value = "本方交易员名称")
    private String ouTraderName;

    @ApiModelProperty(name = "cpPartyId", value = "对手方")
    private String cpPartyId;

    @ApiModelProperty(name = "cpTraderId", value = "对手方交易员")
    private String cpTraderId;

    @ApiModelProperty(name = "cpPartyName", value = "对手方名称")
    private String cpPartyName;

    @ApiModelProperty(name = "ouTraderName", value = "对手方交易员名称")
    private String cpTraderName;

    @ApiModelProperty(name = "settleSpeed", value = "清算速度")
    private Long settleSpeed;

    @ApiModelProperty(name = "settleSpeedStr", value = "清算速度")
    private String settleSpeedStr;

    @ApiModelProperty(name = "vSettleDate", value = "首次结算日")
    private Date vSettleDate;

    @ApiModelProperty(name = "mSettleDate", value = "到期结算日")
    private Date mSettleDate;

    @ApiModelProperty(name = "tradeDate", value = "登记日期")
    private Date tradeDate;

    @ApiModelProperty(name = "dealDate", value = "成交日期")
    private Date dealDate;

    @ApiModelProperty(name = "statisticalTimes", value = "导出次数")
    private int statisticalTimes;

    @ApiModelProperty(name = "quoteId", value = "业务编号")
    private String quoteId;

    @ApiModelProperty(name = "quoteIdStr", value = "业务编号")
    private String quoteIdStr;

    @ApiModelProperty(name = "exeId", value = "成交编号")
    private String trdId;
    //本币导出
    @ApiModelProperty(name = "finprodMarketId", value = "债券代码")
    private String finprodMarketId;

    @ApiModelProperty(name = "finprodName", value = "债券简称")
    private String finprodName;

    @ApiModelProperty(name = "pledgePrin", value = "券面总额(万)")
    private BigDecimal pledgePrin;

    @ApiModelProperty(name = "pledgePrinStr", value = "券面总额(万)")
    private String pledgePrinStr;

    @ApiModelProperty(name = "pledgeRatio", value = "折算比例(%)")
    private BigDecimal pledgeRatio;

    @ApiModelProperty(name = "settleSpeedName", value = "清算速度")
    private String settleSpeedName;

    @ApiModelProperty(name = "settleType", value = "清算类型")
    private String settleType;

    @ApiModelProperty(name = "settleTypeStr", value = "清算类型")
    private String settleTypeStr;

    @ApiModelProperty(name = "deliverytype", value = "首次结算方式")
    private String deliverytype;

    @ApiModelProperty(name = "deliverytypeStr", value = "首次结算方式")
    private String deliverytypeStr;

    @ApiModelProperty(name = "mDeliveryType", value = "到期结算方式")
    private String mDeliveryType;

    @ApiModelProperty(name = "mDeliveryTypeStr", value = "到期结算方式")
    private String mDeliveryTypeStr;

    @ApiModelProperty(name = "ccy", value = "债券币种")
    private String ccy;

    @ApiModelProperty(name = "ccyStr", value = "债券币种")
    private String ccyStr;

    @ApiModelProperty(name = "settCcy", value = "债券币种")
    private String settCcy;

    @ApiModelProperty(name = "settCcyStr", value = "结算币种")
    private String settCcyStr;

    @ApiModelProperty(name = "excRate", value = "汇率")
    private BigDecimal excRate;

    @ApiModelProperty(name = "status", value = "本方状态")
    private String status;

    @ApiModelProperty(name = "mIntAmt", value = "回购利息")
    private BigDecimal mIntAmt;

    @ApiModelProperty(name = "mtradeAmt", value = "到期结算金额")
    private BigDecimal mtradeAmt;

    @ApiModelProperty(name = "updateTime", value = "最后更新时间")
    private Date updateTime;

    @ApiModelProperty(name = "partyId", value = "银行间机构21位码")
    private String partyId;

    public String getValidStatus() {
        return validStatus;
    }

    public void setValidStatus(String validStatus) {
        this.validStatus = validStatus;
    }

    public String getValidStatusStr() {
        if (StringUtils.equals(this.getValidStatus(), BussinessDictCodeList.有效状态_流程中.getValue())) {
            return BussinessDictCodeList.有效状态_流程中.getCodeName();
        } else if (StringUtils.equals(this.getValidStatus(), BussinessDictCodeList.有效状态_撤销.getValue())) {
            return BussinessDictCodeList.有效状态_撤销.getCodeName();
        } else {
            return BussinessDictCodeList.有效状态_有效.getCodeName();
        }
    }

    public String getQuoteType() {
        return quoteType;
    }

    public void setQuoteType(String quoteType) {
        this.quoteType = quoteType;
    }

    public String getPs() {
        return ps;
    }

    public void setPs(String ps) {
        this.ps = ps;
    }

    public String getPsStr() {
        return SysDataUtil.getNameFromDataDict(BussinessDictCode.交易方向, this.getPs());
    }

    public void setPsStr(String psStr) {
        this.psStr = psStr;
    }


    public Long getSettleSpeed() {
        return settleSpeed;
    }

    public void setSettleSpeed(Long settleSpeed) {
        this.settleSpeed = settleSpeed;
    }

    public String getSettleSpeedStr() {
        if (this.getSettleSpeed() != null && this.getSettleSpeed() == 0L) {
            return BussinessDictCodeList.清算速度_T加0.getCodeName();
        } else if (this.getSettleSpeed() != null && this.getSettleSpeed() == 1L) {
            return BussinessDictCodeList.清算速度_T加1.getCodeName();
        } else if (this.getSettleSpeed() != null && this.getSettleSpeed() == 2L) {
            return BussinessDictCodeList.清算速度_T加2.getCodeName();
        }
        return null;
    }

    public void setSettleSpeedStr(String settleSpeedStr) {
        this.settleSpeedStr = settleSpeedStr;
    }

    public String getOuPartyId() {
        return ouPartyId;
    }

    public void setOuPartyId(String ouPartyId) {
        this.ouPartyId = ouPartyId;
    }

    public String getOuTraderId() {
        return ouTraderId;
    }

    public void setOuTraderId(String ouTraderId) {
        this.ouTraderId = ouTraderId;
    }

    public String getOuPartyName() {
        return ouPartyName;
    }

    public void setOuPartyName(String ouPartyName) {
        this.ouPartyName = ouPartyName;
    }

    public String getOuTraderName() {
        return ouTraderName;
    }

    public void setOuTraderName(String ouTraderName) {
        this.ouTraderName = ouTraderName;
    }

    public String getCpPartyId() {
        return cpPartyId;
    }

    public void setCpPartyId(String cpPartyId) {
        this.cpPartyId = cpPartyId;
    }

    public String getCpTraderId() {
        return cpTraderId;
    }

    public void setCpTraderId(String cpTraderId) {
        this.cpTraderId = cpTraderId;
    }

    public String getCpPartyName() {
        return cpPartyName;
    }

    public void setCpPartyName(String cpPartyName) {
        this.cpPartyName = cpPartyName;
    }

    public String getCpTraderName() {
        return cpTraderName;
    }

    public void setCpTraderName(String cpTraderName) {
        this.cpTraderName = cpTraderName;
    }

    public void setValidStatusStr(String validStatusStr) {
        this.validStatusStr = validStatusStr;
    }


    public Date getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(Date tradeDate) {
        this.tradeDate = tradeDate;
    }

    public Date getDealDate() {
        return dealDate;
    }

    public void setDealDate(Date dealDate) {
        this.dealDate = dealDate;
    }

    public String getDealDateStr() {
        return DateUtil.format(this.getDealDate());
    }

    public String getQuoteId() {
        return quoteId;
    }

    public void setQuoteId(String quoteId) {
        this.quoteId = quoteId;
    }

    public String getQuoteTypeStr() {
        if(StringUtil.isNotEmpty(quoteTypeStr)){
            return quoteTypeStr;
        }else{
            return SysDataUtil.getNameFromDataDict(BussinessDictCode.报价方式_登记, this.getQuoteType());
        }
    }

    public void setQuoteTypeStr(String quoteTypeStr) {
        this.quoteTypeStr = quoteTypeStr;
    }


    public int getStatisticalTimes() {
        return statisticalTimes;
    }

    public void setStatisticalTimes(int statisticalTimes) {
        this.statisticalTimes = statisticalTimes;
    }


    public String getTrdId() {
        return trdId;
    }

    public void setTrdId(String trdId) {
        this.trdId = trdId;
    }

    public BigDecimal getTradeAmt() {
        return tradeAmt;
    }

    public void setTradeAmt(BigDecimal tradeAmt) {
        this.tradeAmt = tradeAmt;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public Long getRepoTerm() {
        return repoTerm;
    }

    public void setRepoTerm(Long repoTerm) {
        this.repoTerm = repoTerm;
    }

    public Date getvSettleDate() {
        return vSettleDate;
    }

    public void setvSettleDate(Date vSettleDate) {
        this.vSettleDate = vSettleDate;
    }

    public Date getmSettleDate() {
        return mSettleDate;
    }

    public void setmSettleDate(Date mSettleDate) {
        this.mSettleDate = mSettleDate;
    }

    public String getFinprodMarketId() {
        return finprodMarketId;
    }

    public void setFinprodMarketId(String finprodMarketId) {
        this.finprodMarketId = finprodMarketId;
    }

    public String getFinprodName() {
        return finprodName;
    }

    public void setFinprodName(String finprodName) {
        this.finprodName = finprodName;
    }

    public BigDecimal getPledgePrin() {
        return pledgePrin;
    }

    public void setPledgePrin(BigDecimal pledgePrin) {
        this.pledgePrin = pledgePrin;
    }

    public String getPledgePrinStr() {
        if (StringUtil.isNotEmpty(this.getPledgePrin())) {
            DecimalFormat df = new DecimalFormat("###,###");
            return df.format(ArithmeticUtil.divide(this.getPledgePrin(), new BigDecimal(10000), 2, RoundingMode.HALF_UP));
        }
        return null;
    }

    public void setPledgePrinStr(String pledgePrinStr) {
        this.pledgePrinStr = pledgePrinStr;
    }

    public BigDecimal getPledgeRatio() {
        return pledgeRatio;
    }

    public void setPledgeRatio(BigDecimal pledgeRatio) {
        this.pledgeRatio = pledgeRatio;
    }

    public String getSettleSpeedName() {
        if (this.getSettleSpeed() != null && this.getSettleSpeed() == 0L) {
            return "+0";
        } else if (this.getSettleSpeed() != null && this.getSettleSpeed() == 1L) {
            return "+1";
        }
        return null;
    }

    public void setSettleSpeedName(String settleSpeedName) {
        this.settleSpeedName = settleSpeedName;
    }

    public String getSettleType() {
        return settleType;
    }

    public void setSettleType(String settleType) {
        this.settleType = settleType;
    }

    public String getSettleTypeStr() {
        return SysDataUtil.getNameFromDataDict(BussinessDictCode.清算类型, this.getSettleType());
    }

    public void setSettleTypeStr(String settleTypeStr) {
        this.settleTypeStr = settleTypeStr;
    }

    public String getDeliverytype() {
        return deliverytype;
    }

    public void setDeliverytype(String deliverytype) {
        this.deliverytype = deliverytype;
    }

    public String getDeliverytypeStr() {
        return SysDataUtil.getNameFromDataDict(BussinessDictCode.结算方式, this.getDeliverytype());
    }

    public void setDeliverytypeStr(String deliverytypeStr) {
        this.deliverytypeStr = deliverytypeStr;
    }

    public String getmDeliveryType() {
        return mDeliveryType;
    }

    public void setmDeliveryType(String mDeliveryType) {
        this.mDeliveryType = mDeliveryType;
    }

    public String getmDeliveryTypeStr() {
        return SysDataUtil.getNameFromDataDict(BussinessDictCode.结算方式, this.getmDeliveryType());
    }

    public void setmDeliveryTypeStr(String mDeliveryTypeStr) {
        this.mDeliveryTypeStr = mDeliveryTypeStr;
    }

    public String getCcy() {
        return ccy;
    }

    public void setCcy(String ccy) {
        this.ccy = ccy;
    }

    public String getCcyStr() {
        return SysDataUtil.getNameFromDataDict(BussinessDictCode.币种, this.getCcy());
    }

    public void setCcyStr(String ccyStr) {
        this.ccyStr = ccyStr;
    }

    public String getSettCcy() {
        return settCcy;
    }

    public void setSettCcy(String settCcy) {
        this.settCcy = settCcy;
    }

    public String getSettCcyStr() {
        return SysDataUtil.getNameFromDataDict(BussinessDictCode.币种, this.getSettCcy());
    }

    public void setSettCcyStr(String settCcyStr) {
        this.settCcyStr = settCcyStr;
    }

    public BigDecimal getExcRate() {
        return excRate;
    }

    public void setExcRate(BigDecimal excRate) {
        this.excRate = excRate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getRepoType() {
        return repoType;
    }

    public void setRepoType(String repoType) {
        this.repoType = repoType;
    }

    public String getRepoTypeStr() {
        return repoTypeStr;
    }

    public void setRepoTypeStr(String repoTypeStr) {
        this.repoTypeStr = repoTypeStr;
    }

    public String getPartyId() {
        return partyId;
    }

    public void setPartyId(String partyId) {
        this.partyId = partyId;
    }

    public String getQuoteIdStr() {
        return quoteIdStr;
    }

    public void setQuoteIdStr(String quoteIdStr) {
        this.quoteIdStr = quoteIdStr;
    }
}

