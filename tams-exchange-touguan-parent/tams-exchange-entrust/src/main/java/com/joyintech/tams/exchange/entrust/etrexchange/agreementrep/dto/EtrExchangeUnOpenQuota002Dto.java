/*
 * 文件名：EtrExchangeTradeDto002.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：chendijun
 * 修改时间：2020年7月2日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.exchange.entrust.etrexchange.agreementrep.dto;

import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.systemtools.util.SysDatadictUtil;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

/**
 * 〈一句话功能简述〉 〈功能详细描述〉
 *
 * @author lby
 * @version 1.0 2020年9月17日
 * @see EtrExchangeUnOpenQuota002Dto
 * @since 1.0
 */
@PageId("EtrExchangeUnOpenQuota002Dto")
public class EtrExchangeUnOpenQuota002Dto extends BaseEntity {

    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    private static final long serialVersionUID = -8997549399769922323L;

    /**
     * 请求编号
     */
    private String reqOnId;
    /**
     * 请求类型
     */
    private String reqType;

    private String reqTypeStr;

    /**
     * 交易方向
     */
    private String ps;
    /**
     * 交易方向
     */
    private String psStr;
    /**
     * 质押券代码
     */
    private String repSecId;
    /**
     * 质押券名称
     */
    private String pledgeStampName;
    /**
     * 原质押代码
     */
    private String oriRepSecId;
    /**
     * 质押数量
     */
    private BigDecimal repSecNum;
    /**
     * 质押券面总额合计
     */
    private BigDecimal pledgeStampBalance;

    /**
     * 折算比例
     */
    private BigDecimal conRatio;
    /**
     * 回购金额
     */
    private BigDecimal tradeAmt;
    /**
     * 回购利率
     */
    private BigDecimal yield;
    /**
     * 回购期限
     */
    private Integer term;
    /**
     * 实际占款天数
     */
    private Integer occupyDays;
    /**
     * 首次结算日
     */
    private String fristSettleDate;

    /**
     * 回购到期日
     */
    private String backDate;

    /**
     * 到期结算日
     */
    private String expireSettleDate;
    @ApiModelProperty(name = "newRepSecId", value = "换入质押券代码")
    private String newRepSecId;

    @ApiModelProperty(name = "swapIntoBondsName", value = "换入质押券名称")
    private String newRepSecName;

    @ApiModelProperty(name = "swapIntoBondsVolume", value = "换入质押券数量")
    private BigDecimal newRepSecNum;

    @ApiModelProperty(name = "outRepSecId", value = "换出质押券代码")
    private String outRepSecId;

    @ApiModelProperty(name = "outRepSecName", value = "换出质押券名称")
    private String outRepSecName;

    @ApiModelProperty(name = "outRepSecNum", value = "换出质押券数量")
    private BigDecimal outRepSecNum;

    /**
     * 原质押券代码
     */
    private String origPledgeStampCode;

    /**
     * 原质押券名称
     */
    private String origPledgeStampName;
    /**
     * 原质押券名称
     */
    private String origPledgeStampNum;
    /**
     * 原回购金额
     */
    private BigDecimal origTradeAmt;
    /**
     * 到期续作类型（0-非第三方，1-第三方）
     */
    private String  transCode;
    /**
     * 到期续作类型（0-非第三方，1-第三方）
     */
    private String  transCodeStr;

    /**
     * 回购利息
     */
    private BigDecimal intAmt;

    /**
     * 到期结算金额
     */
    private BigDecimal expireSettleAmt;
    /**
     * 成交编号
     */
    private String tradeId;
    /**
     * 本方交易员代码
     */
    private String sponsorTraderCode;

    /**
     * 本方方交易商代码
     */
    private String sponsorDealerCode;
    @ApiModelProperty(name = "traderCode", value = "本方交易员")
    private String traderCode;
    @ApiModelProperty(name = "memberId", value = "本方交易商")
    private String memberId;
    @ApiModelProperty(name = "investorId", value = "本方交易主体（深交所展示，否则为空）")
    private String investorId;
    @ApiModelProperty(name = "investorType", value = "本方交易主体类型（深交所展示，否则为空）")
    private String investorType;

    @ApiModelProperty(name = "cpTraderCode", value = "对手方交易员")
    private String cpTraderCode;
    @ApiModelProperty(name = "cpMemberId", value = "对手方交易商")
    private String cpMemberId;
    @ApiModelProperty(name = "cpInvestorId", value = "对手方交易主体（深交所展示，否则为空）")
    private String cpInvestorId;
    @ApiModelProperty(name = "cpInvestorType", value = "对手方交易主体类型（深交所展示，否则为空）")
    private String cpInvestorType;
    @ApiModelProperty(name = "cpClientName", value = "对手方客户名称")
    private String cpClientName;
    /**
     * 补充协议
     */
    private String addCla;

    /**
     * 资金账号
     */
    private String acctNo;
    /**
     * 资金账号名称
     */
    private String acctName;

    private String redoType;

    /**
     * 交易场所
     */
    private String tradeMarket;

    /**
     * 交易场所
     */
    private String tradeMarketStr;

    public String getReqOnId() {
        return reqOnId;
    }

    public void setReqOnId(String reqOnId) {
        this.reqOnId = reqOnId;
    }

    public String getReqType() {
        return reqType;
    }

    public void setReqType(String reqType) {
        this.reqType = reqType;
    }

    public String getRepSecId() {
        return repSecId;
    }

    public void setRepSecId(String repSecId) {
        this.repSecId = repSecId;
    }

    public String getPledgeStampName() {
        return pledgeStampName;
    }

    public void setPledgeStampName(String pledgeStampName) {
        this.pledgeStampName = pledgeStampName;
    }

    public String getOriRepSecId() {
        return oriRepSecId;
    }

    public void setOriRepSecId(String oriRepSecId) {
        this.oriRepSecId = oriRepSecId;
    }

    public BigDecimal getRepSecNum() {
        return repSecNum;
    }

    public void setRepSecNum(BigDecimal repSecNum) {
        this.repSecNum = repSecNum;
    }

    public BigDecimal getPledgeStampBalance() {
        return pledgeStampBalance;
    }

    public void setPledgeStampBalance(BigDecimal pledgeStampBalance) {
        this.pledgeStampBalance = pledgeStampBalance;
    }

    public BigDecimal getConRatio() {
        return conRatio;
    }

    public void setConRatio(BigDecimal conRatio) {
        this.conRatio = conRatio;
    }

    public BigDecimal getTradeAmt() {
        return tradeAmt;
    }

    public void setTradeAmt(BigDecimal tradeAmt) {
        this.tradeAmt = tradeAmt;
    }

    public BigDecimal getYield() {
        return yield;
    }

    public void setYield(BigDecimal yield) {
        this.yield = yield;
    }

    public Integer getTerm() {
        return term;
    }

    public void setTerm(Integer term) {
        this.term = term;
    }

    public Integer getOccupyDays() {
        return occupyDays;
    }

    public void setOccupyDays(Integer occupyDays) {
        this.occupyDays = occupyDays;
    }

    public String getFristSettleDate() {
        return fristSettleDate;
    }

    public void setFristSettleDate(String fristSettleDate) {
        this.fristSettleDate = fristSettleDate;
    }

    public String getBackDate() {
        return backDate;
    }

    public void setBackDate(String backDate) {
        this.backDate = backDate;
    }

    public String getExpireSettleDate() {
        return expireSettleDate;
    }

    public void setExpireSettleDate(String expireSettleDate) {
        this.expireSettleDate = expireSettleDate;
    }

    public String getOrigPledgeStampCode() {
        return origPledgeStampCode;
    }

    public void setOrigPledgeStampCode(String origPledgeStampCode) {
        this.origPledgeStampCode = origPledgeStampCode;
    }

    public BigDecimal getIntAmt() {
        return intAmt;
    }

    public void setIntAmt(BigDecimal intAmt) {
        this.intAmt = intAmt;
    }

    public BigDecimal getExpireSettleAmt() {
        return expireSettleAmt;
    }

    public void setExpireSettleAmt(BigDecimal expireSettleAmt) {
        this.expireSettleAmt = expireSettleAmt;
    }

    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }

    public String getSponsorTraderCode() {
        return sponsorTraderCode;
    }

    public void setSponsorTraderCode(String sponsorTraderCode) {
        this.sponsorTraderCode = sponsorTraderCode;
    }

    public String getSponsorDealerCode() {
        return sponsorDealerCode;
    }

    public void setSponsorDealerCode(String sponsorDealerCode) {
        this.sponsorDealerCode = sponsorDealerCode;
    }

    public String getTraderCode() {
        return traderCode;
    }

    public void setTraderCode(String traderCode) {
        this.traderCode = traderCode;
    }

    public String getAddCla() {
        return addCla;
    }

    public void setAddCla(String addCla) {
        this.addCla = addCla;
    }

    public String getReqTypeStr() {
        return this.reqTypeStr;
    }

    public String getAcctNo() {
        return acctNo;
    }

    public void setAcctNo(String acctNo) {
        this.acctNo = acctNo;
    }

    public String getRedoType() {
        return redoType;
    }

    public void setRedoType(String redoType) {
        this.redoType = redoType;
    }

    public String getRedoTypeStr() {
        if ("0".equals(redoType)){
            return "非三方续做";
        }else if ("1".equals(redoType)){
            return "三方续做";
        }else {
            return "";
        }
    }

    public String getTradeMarket() {
        return tradeMarket;
    }

    public void setTradeMarket(String tradeMarket) {
        this.tradeMarket = tradeMarket;
    }

    public String getTradeMarketStr() {
        return this.tradeMarketStr;
    }

    public String getOrigPledgeStampName() {
        return origPledgeStampName;
    }

    public void setOrigPledgeStampName(String origPledgeStampName) {
        this.origPledgeStampName = origPledgeStampName;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getInvestorId() {
        return investorId;
    }

    public void setInvestorId(String investorId) {
        this.investorId = investorId;
    }

    public String getInvestorType() {
        return investorType;
    }

    public void setInvestorType(String investorType) {
        this.investorType = investorType;
    }

    public String getCpTraderCode() {
        return cpTraderCode;
    }

    public void setCpTraderCode(String cpTraderCode) {
        this.cpTraderCode = cpTraderCode;
    }

    public String getCpMemberId() {
        return cpMemberId;
    }

    public void setCpMemberId(String cpMemberId) {
        this.cpMemberId = cpMemberId;
    }

    public String getCpInvestorId() {
        return cpInvestorId;
    }

    public void setCpInvestorId(String cpInvestorId) {
        this.cpInvestorId = cpInvestorId;
    }

    public String getCpInvestorType() {
        return cpInvestorType;
    }

    public void setCpInvestorType(String cpInvestorType) {
        this.cpInvestorType = cpInvestorType;
    }

    public String getCpClientName() {
        return cpClientName;
    }

    public void setCpClientName(String cpClientName) {
        this.cpClientName = cpClientName;
    }

    public void setReqTypeStr(String reqTypeStr) {
        this.reqTypeStr = reqTypeStr;
    }

    public void setTradeMarketStr(String tradeMarketStr) {
        this.tradeMarketStr = tradeMarketStr;
    }

    public String getPs() {
        return ps;
    }

    public void setPs(String ps) {
        this.ps = ps;
    }

    public String getPsStr() {
        return psStr;
    }

    public void setPsStr(String psStr) {
        this.psStr = psStr;
    }

    public String getNewRepSecId() {
        return newRepSecId;
    }

    public void setNewRepSecId(String newRepSecId) {
        this.newRepSecId = newRepSecId;
    }

    public String getNewRepSecName() {
        return newRepSecName;
    }

    public void setNewRepSecName(String newRepSecName) {
        this.newRepSecName = newRepSecName;
    }

    public BigDecimal getNewRepSecNum() {
        return newRepSecNum;
    }

    public void setNewRepSecNum(BigDecimal newRepSecNum) {
        this.newRepSecNum = newRepSecNum;
    }

    public String getOutRepSecId() {
        return outRepSecId;
    }

    public void setOutRepSecId(String outRepSecId) {
        this.outRepSecId = outRepSecId;
    }

    public String getOutRepSecName() {
        return outRepSecName;
    }

    public void setOutRepSecName(String outRepSecName) {
        this.outRepSecName = outRepSecName;
    }

    public BigDecimal getOutRepSecNum() {
        return outRepSecNum;
    }

    public void setOutRepSecNum(BigDecimal outRepSecNum) {
        this.outRepSecNum = outRepSecNum;
    }

    public String getOrigPledgeStampNum() {
        return origPledgeStampNum;
    }

    public void setOrigPledgeStampNum(String origPledgeStampNum) {
        this.origPledgeStampNum = origPledgeStampNum;
    }

    public BigDecimal getOrigTradeAmt() {
        return origTradeAmt;
    }

    public void setOrigTradeAmt(BigDecimal origTradeAmt) {
        this.origTradeAmt = origTradeAmt;
    }

    public String getAcctName() {
        return acctName;
    }

    public void setAcctName(String acctName) {
        this.acctName = acctName;
    }

    public String getTransCode() {
        return transCode;
    }

    public void setTransCode(String transCode) {
        this.transCode = transCode;
    }

    public String getTransCodeStr() {
        if(StringUtil.isNotEmpty(this.transCode)){
            return "0".equals(this.transCode)?"非第三方":"第三方";
        }
        return "";
    }

    public void setTransCodeStr(String transCodeStr) {
        this.transCodeStr = transCodeStr;
    }
}
