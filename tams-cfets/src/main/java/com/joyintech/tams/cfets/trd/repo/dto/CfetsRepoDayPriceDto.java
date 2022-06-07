package com.joyintech.tams.cfets.trd.repo.dto;

import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.invest.common.constant.BussinessDictCode;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

/**
 * @author jiangpeng
 * @date 2021/12/25
 */
@ApiModel(value = "当日报价", description = "当日报价")
public class CfetsRepoDayPriceDto {
    @ApiModelProperty(name = "validStatus", value = "有效状态")
    private String validStatus;

    @ApiModelProperty(name = "validStatusStr", value = "有效状态名称")
    private String validStatusStr;

    @ApiModelProperty(name = "tradeStatus", value = "报价状态")
    private String tradeStatus;

    @ApiModelProperty(name = "tradeStatusStr", value = "报价状态名称")
    private String tradeStatusStr;

    @ApiModelProperty(name = "sendStatus", value = "发送状态")
    private String sendStatus;

    @ApiModelProperty(name = "sendStatusStr", value = "发送状态名称")
    private String sendStatusStr;

    @ApiModelProperty(name = "quoteRoleType", value = "本方角色")
    private String quoteRoleType;

    @ApiModelProperty(name = "quoteRoleTypeStr", value = "本方角色名称")
    private String quoteRoleTypeStr;

    @ApiModelProperty(name = "ps", value = "本方方向")
    private String ps;

    @ApiModelProperty(name = "psStr", value = "本方方向名称")
    private String psStr;

    @ApiModelProperty(name = "quoteType", value = "报价方式")
    private String quoteType;

    @ApiModelProperty(name = "quoteTypeStr", value = "报价方式名称")
    private String quoteTypeStr;

    @ApiModelProperty(name = "ouPartyId", value = "本方")
    private String ouPartyId;

    @ApiModelProperty(name = "ouTraderId", value = "本方交易员")
    private String ouTraderId;

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

    @ApiModelProperty(name = "cpTraderName", value = "对手方交易员名称")
    private String cpTraderName;

    @ApiModelProperty(name = "tradeAmt", value = "交易金额")
    private BigDecimal tradeAmt;

    @ApiModelProperty(name = "rate", value = "回购利率")
    private BigDecimal rate;

    @ApiModelProperty(name = "repoTerm", value = "回购期限")
    private Long repoTerm;

    @ApiModelProperty(name = "settleSpeed", value = "清算速度")
    private Long settleSpeed;

    @ApiModelProperty(name = "settleSpeed", value = "清算速度")
    private String settleSpeedStr;

    @ApiModelProperty(name = "settleSpeed", value = "首次结算日")
    private String vSettleDate;

    @ApiModelProperty(name = "mSettleDate", value = "到期结算日")
    private String mSettleDate;

    @ApiModelProperty(name = "reqEffTime", value = "请求有效")
    private String reqEffTime;

    @ApiModelProperty(name = "resEffTime", value = "回复有效 ")
    private String resEffTime;

    @ApiModelProperty(name = "quoteId", value = "业务编号")
    private String quoteId;

    @ApiModelProperty(name = "cfetsQuoteId", value = "CFETS报价编号")
    private String cfetsQuoteId;

    @ApiModelProperty(name = "execid", value = "成交编号")
    private String execid;

    @ApiModelProperty(name = "remark", value = "备注")
    private String remark;

    @ApiModelProperty(name = "answerFlag", value = "是否回复标志 01表示回复")
    private String answerFlag;

    @ApiModelProperty(name = "answerFlag", value = "发送失败时错误信息")
    private String errorMsg;

    public String getValidStatus() {
        return validStatus;
    }

    public void setValidStatus(String validStatus) {
        this.validStatus = validStatus;
    }

    public String getValidStatusStr() {
        if (StringUtils.equals(this.getValidStatus(), BussinessDictCodeList.有效状态_流程中.getValue())) {
            return BussinessDictCodeList.有效状态_流程中.getCodeName();
        } else {
            return BussinessDictCodeList.有效状态_有效.getCodeName();
        }
    }

    public void setValidStatusStr(String validStatusStr) {
        this.validStatusStr = validStatusStr;
    }

    public String getTradeStatus() {
        return tradeStatus;
    }

    public void setTradeStatus(String tradeStatus) {
        this.tradeStatus = tradeStatus;
    }

    public String getTradeStatusStr() {
        return SysDataUtil.getNameFromDataDict(BussinessDictCode.银行间上行报价状态, this.getTradeStatus());
    }

    public void setTradeStatusStr(String tradeStatusStr) {
        this.tradeStatusStr = tradeStatusStr;
    }

    public String getSendStatus() {
        return sendStatus;
    }

    public void setSendStatus(String sendStatus) {
        this.sendStatus = sendStatus;
    }

    public String getSendStatusStr() {
        return SysDataUtil.getNameFromDataDict(BussinessDictCode.银行间上行发送状态, this.getSendStatus());
    }

    public void setSendStatusStr(String sendStatusStr) {
        this.sendStatusStr = sendStatusStr;
    }

    public String getQuoteType() {
        return quoteType;
    }

    public void setQuoteType(String quoteType) {
        this.quoteType = quoteType;
    }

    public String getQuoteTypeStr() {
        if (StringUtil.isNotEmpty(this.getQuoteType())) {
            String quoteTypeStr = SysDataUtil.getNameFromDataDict(BussinessDictCode.报价方式,
                    this.getQuoteType());
            if (quoteTypeStr == null) {
                return this.quoteType;
            }
            return quoteTypeStr;
        }
        return "";
    }

    public void setQuoteTypeStr(String quoteTypeStr) {
        this.quoteTypeStr = quoteTypeStr;
    }

    public String getQuoteRoleType() {
        return quoteRoleType;
    }

    public void setQuoteRoleType(String quoteRoleType) {
        this.quoteRoleType = quoteRoleType;
    }

    public String getQuoteRoleTypeStr() {
        if (StringUtil.isNotEmpty(this.getQuoteRoleType())) {
            return SysDataUtil.getNameFromDataDict(BussinessDictCode.银行间上行本方角色, this.getQuoteRoleType());
        }
        return null;
    }

    public void setQuoteRoleTypeStr(String quoteRoleTypeStr) {
        this.quoteRoleTypeStr = quoteRoleTypeStr;
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

    public String getReqEffTime() {
        return reqEffTime;
    }

    public void setReqEffTime(String reqEffTime) {
        this.reqEffTime = reqEffTime;
    }

    public String getResEffTime() {
        return resEffTime;
    }

    public void setResEffTime(String resEffTime) {
        this.resEffTime = resEffTime;
    }

    public String getQuoteId() {
        return quoteId;
    }

    public void setQuoteId(String quoteId) {
        this.quoteId = quoteId;
    }

    public String getExecid() {
        return execid;
    }

    public void setExecid(String execid) {
        this.execid = execid;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getAnswerFlag() {
        return answerFlag;
    }

    public void setAnswerFlag(String answerFlag) {
        this.answerFlag = answerFlag;
    }

    public String getCfetsQuoteId() {
        return cfetsQuoteId;
    }

    public void setCfetsQuoteId(String cfetsQuoteId) {
        this.cfetsQuoteId = cfetsQuoteId;
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

    public String getvSettleDate() {
        return vSettleDate;
    }

    public void setvSettleDate(String vSettleDate) {
        this.vSettleDate = vSettleDate;
    }

    public String getmSettleDate() {
        return mSettleDate;
    }

    public void setmSettleDate(String mSettleDate) {
        this.mSettleDate = mSettleDate;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}

