package com.joyintech.tams.cfets.trd.repo.dto;

import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.invest.common.constant.BussinessDictCode;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author jp
 * @date 2021/12/14
 */
@ApiModel(value = "交易执行明细", description = "交易执行明细")
public class RepoDealTransactionDetailsDto {

    @ApiModelProperty(name = "validStatus", value = "有效状态")
    private String validStatus;

    @ApiModelProperty(name = "validStatusStr", value = "有效状态名称")
    private String validStatusStr;

    @ApiModelProperty(name = "quoteType", value = "交易类型")
    private String quoteType;

    @ApiModelProperty(name = "quoteTypeStr", value = "交易类型名称")
    private String quoteTypeStr;

    @ApiModelProperty(name = "tradeDate", value = "成交日期")
    private Date tradeDate;

    @ApiModelProperty(name = "ps", value = "本方方向")
    private String ps;

    @ApiModelProperty(name = "psStr", value = "本方方向名称")
    private String psStr;

    @ApiModelProperty(name = "tradeStatus", value = "报价状态")
    private String tradeStatus;

    @ApiModelProperty(name = "tradeStatusStr", value = "报价状态名称")
    private String tradeStatusStr;

    @ApiModelProperty(name = "sendStatus", value = "发送状态")
    private String sendStatus;

    @ApiModelProperty(name = "sendStatusStr", value = "发送状态名称")
    private String sendStatusStr;

    @ApiModelProperty(name = "tradeAmt", value = "交易金额（元）")
    private BigDecimal tradeAmt;

    @ApiModelProperty(name = "rate", value = "回购利率（%）")
    private BigDecimal rate;

    @ApiModelProperty(name = "repoTerm", value = "回购期限")
    private Long repoTerm;

    @ApiModelProperty(name = "quoteRoleType", value = "本方角色")
    private String quoteRoleType;

    @ApiModelProperty(name = "quoteRoleTypeStr", value = "本方角色")
    private String quoteRoleTypeStr;

    @ApiModelProperty(name = "ouPartyId", value = "本方")
    private String ouPartyId;

    @ApiModelProperty(name = "ouPartyName", value = "本方名称")
    private String ouPartyName;

    @ApiModelProperty(name = "ouTraderId", value = "本方交易员")
    private String ouTraderId;

    @ApiModelProperty(name = "ouTraderName", value = "本方交易员名称")
    private String ouTraderName;

    @ApiModelProperty(name = "cpPartyId", value = "对手方")
    private String cpPartyId;

    @ApiModelProperty(name = "cpPartyName", value = "对手方名称")
    private String cpPartyName;

    @ApiModelProperty(name = "cpTraderId", value = "对手方交易员")
    private String cpTraderId;

    @ApiModelProperty(name = "cpTraderName", value = "对手方交易员名称")
    private String cpTraderName;

    @ApiModelProperty(name = "settleSpeed", value = "清算速度")
    private String settleSpeed;

    @ApiModelProperty(name = "settleSpreedStr", value = "清算速度")
    private String settleSpeedStr;

    @ApiModelProperty(name = "vSettleDate", value = "首次结算日")
    private Date vSettleDate;

    @ApiModelProperty(name = "mSettleDate", value = "到期结算日")
    private Date mSettleDate;

    @ApiModelProperty(name = "quoteId", value = "业务编号")
    private String quoteId;

    @ApiModelProperty(name = "trdId", value = "成交编号")
    private String trdId;

    @ApiModelProperty(name = "remark", value = "备注")
    private String remark;

    public String getValidStatus() {
        return validStatus;
    }

    public void setValidStatus(String validStatus) {
        this.validStatus = validStatus;
    }

    public String getQuoteType() {
        return quoteType;
    }

    public void setQuoteType(String quoteType) {
        this.quoteType = quoteType;
    }

    public String getQuoteRoleType() {
        return quoteRoleType;
    }

    public void setQuoteRoleType(String quoteRoleType) {
        this.quoteRoleType = quoteRoleType;
    }

    public Date getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(Date tradeDate) {
        this.tradeDate = tradeDate;
    }

    public String getPs() {
        return ps;
    }

    public void setPs(String ps) {
        this.ps = ps;
    }


    public String getTradeStatus() {
        return tradeStatus;
    }

    public void setTradeStatus(String tradeStatus) {
        this.tradeStatus = tradeStatus;
    }

    public String getSendStatus() {
        return sendStatus;
    }

    public void setSendStatus(String sendStatus) {
        this.sendStatus = sendStatus;
    }


    public String getSettleSpeed() {
        return settleSpeed;
    }

    public void setSettleSpeed(String settleSpeed) {
        this.settleSpeed = settleSpeed;
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


    public String getQuoteId() {
        return quoteId;
    }

    public void setQuoteId(String quoteId) {
        this.quoteId = quoteId;
    }

    public String getTrdId() {
        return trdId;
    }

    public void setTrdId(String trdId) {
        this.trdId = trdId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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

    public String getValidStatusStr() {
        if (StringUtils.equals(this.getValidStatus(), BussinessDictCodeList.有效状态_流程中.getValue())) {
            return BussinessDictCodeList.有效状态_流程中.getCodeName();
        } else if (StringUtils.equals(this.getValidStatus(), BussinessDictCodeList.有效状态_有效.getValue())) {
            return BussinessDictCodeList.有效状态_有效.getCodeName();
        } else if (StringUtils.equals(this.getValidStatus(), BussinessDictCodeList.有效状态_撤销.getValue())) {
            return BussinessDictCodeList.有效状态_撤销.getCodeName();
        }else{
            return this.getValidStatus();
        }
    }

    public void setValidStatusStr(String validStatusStr) {
        this.validStatusStr = validStatusStr;
    }

    public String getPsStr() {
        return SysDataUtil.getNameFromDataDict(BussinessDictCode.交易方向, this.getPs());
    }

    public void setPsStr(String psStr) {
        this.psStr = psStr;
    }

    public String getTradeStatusStr() {
        return SysDataUtil.getNameFromDataDict(BussinessDictCode.银行间上行报价状态, this.getTradeStatus());
    }

    public void setTradeStatusStr(String tradeStatusStr) {
        this.tradeStatusStr = tradeStatusStr;
    }

    public String getSendStatusStr() {
        return SysDataUtil.getNameFromDataDict(BussinessDictCode.银行间上行发送状态, this.getSendStatus());
    }

    public void setSendStatusStr(String sendStatusStr) {
        this.sendStatusStr = sendStatusStr;
    }

    public String getSettleSpeedStr() {
        return SysDataUtil.getNameFromDataDict(BussinessDictCode.清算速度, this.getSettleSpeed());
    }

    public void setSettleSpeedStr(String settleSpeedStr) {
        this.settleSpeedStr = settleSpeedStr;
    }

    public String getQuoteTypeStr() {
        return SysDataUtil.getNameFromDataDict(BussinessDictCode.报价方式, this.getQuoteType());
    }

    public void setQuoteTypeStr(String quoteTypeStr) {
        this.quoteTypeStr = quoteTypeStr;
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

    public String getQuoteRoleTypeStr() {
        return SysDataUtil.getNameFromDataDict(BussinessDictCode.银行间上行本方角色, this.getQuoteRoleType());
    }

    public void setQuoteRoleTypeStr(String quoteRoleTypeStr) {
        this.quoteRoleTypeStr = quoteRoleTypeStr;
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
}

