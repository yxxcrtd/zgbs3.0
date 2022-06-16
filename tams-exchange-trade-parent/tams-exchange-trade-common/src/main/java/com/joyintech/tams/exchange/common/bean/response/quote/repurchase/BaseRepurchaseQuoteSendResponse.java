package com.joyintech.tams.exchange.common.bean.response.quote.repurchase;


import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.base.MoreObjects;
import com.joyintech.tams.exchange.common.enums.exchange.QuoteStatusEnum;
import com.joyintech.tams.exchange.common.enums.exchange.RepoBsEnum;
import com.joyintech.tams.exchange.common.enums.exchange.RepurchaseTypeEnum;
import com.joyintech.tams.exchange.common.utils.EnumUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

@ApiModel(value = "我发起的协议回购行情返回报文", description = "我发起的协议回购行情返回报文")
public class BaseRepurchaseQuoteSendResponse {

    @ApiModelProperty(name = "requestStatus", value = "请求状态")
    private Integer requestStatus;

    @ApiModelProperty(name = "requestType", value = "本方请求类型")
    private String requestType;

    @ApiModelProperty(name = "bs", value = "本方委托方向")
    private String bs;

    @ApiModelProperty(name = "stockCode", value = "质押券代码")
    private String stockCode;

    @ApiModelProperty(name = "stockName", value = "质押券名称")
    private String stockName;

    @ApiModelProperty(name = "pledgeNumber", value = "质押数量")
    private BigDecimal pledgeNumber;

    @ApiModelProperty(name = "counterId", value = "交易对手")
    private String counterId;

    @ApiModelProperty(name = "traderCode", value = "本方交易员")
    private String traderCode;
    @ApiModelProperty(name = "traderCodeStr", value = "本方交易员名称")
    private String traderCodeStr;
    @ApiModelProperty(name = "memberId", value = "本方交易商")
    private String memberId;
    @ApiModelProperty(name = "memberIdStr", value = "本方交易商名称")
    private String memberIdStr;
    @ApiModelProperty(name = "investorId", value = "本方交易主体（深交所展示，否则为空）")
    private String investorId;
    @ApiModelProperty(name = "investorIdStr", value = "本方交易主体（深交所展示，否则为空）")
    private String investorIdStr;
    @ApiModelProperty(name = "investorType", value = "本方交易主体类型（深交所展示，否则为空）")
    private String investorType;
    @ApiModelProperty(name = "investorTypeStr", value = "本方交易主体类型（1.自营 2.资管 3.机构经纪 4.个人经纪）")
    private String investorTypeStr;

    @ApiModelProperty(name = "cpTraderCode", value = "对手方交易员")
    private String cpTraderCode;
    @ApiModelProperty(name = "cpTraderCodeStr", value = "对手方交易员名称")
    private String cpTraderCodeStr;
    @ApiModelProperty(name = "cpMemberId", value = "对手方交易商")
    private String cpMemberId;
    @ApiModelProperty(name = "cpMemberIdStr", value = "对手方交易商名称")
    private String cpMemberIdStr;
    @ApiModelProperty(name = "cpInvestorId", value = "对手方交易主体（深交所展示，否则为空）")
    private String cpInvestorId;
    @ApiModelProperty(name = "cpInvestorIdStr", value = "对手方交易主体（深交所展示，否则为空）")
    private String cpInvestorIdStr;
    @ApiModelProperty(name = "cpInvestorType", value = "对手方交易主体类型（深交所展示，否则为空）")
    private String cpInvestorType;
    @ApiModelProperty(name = "cpInvestorTypeStr", value = "对手方交易主体类型（1.自营 2.资管 3.机构经纪 4.个人经纪）")
    private String cpInvestorTypeStr;
    @ApiModelProperty(name = "cpClientName", value = "对手方客户名称")
    private String cpClientName;

    @ApiModelProperty(name = "remark", value = "补充协议")
    private String remark;

    @ApiModelProperty(name = "requestStatusStr", value = "请求状态（待确认、已成交、已撤销）")
    private String requestStatusStr;
    @ApiModelProperty(name = "requestTypeStr", value = "本方请求类型（首期、提前购回、中途换券、到期续做、解除质押、到期购回）")
    private String requestTypeStr;
    @ApiModelProperty(name = "bsStr", value = "本方委托方向（1：正回购 2：逆回购）")
    private String bsStr;
    @ApiModelProperty(name = "pledgeNumberStr", value = "质押数量（单位：万元，保留两位小数）")
    private String pledgeNumberStr;

    public Integer getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(Integer requestStatus) {
        this.requestStatus = requestStatus;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public String getBs() {
        return bs;
    }

    public void setBs(String bs) {
        this.bs = bs;
    }

    public String getStockCode() {
        return stockCode;
    }

    public void setStockCode(String stockCode) {
        this.stockCode = stockCode;
    }

    public String getStockName() {
        return stockName;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

    public BigDecimal getPledgeNumber() {
        return pledgeNumber;
    }

    public void setPledgeNumber(BigDecimal pledgeNumber) {
        this.pledgeNumber = pledgeNumber;
    }

    public String getCounterId() {
        return counterId;
    }

    public void setCounterId(String counterId) {
        this.counterId = counterId;
    }

    public String getTraderCode() {
        return traderCode;
    }

    public void setTraderCode(String traderCode) {
        this.traderCode = traderCode;
    }

    public String getTraderCodeStr() {
        return traderCodeStr;
    }

    public void setTraderCodeStr(String traderCodeStr) {
        this.traderCodeStr = traderCodeStr;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getMemberIdStr() {
        return memberIdStr;
    }

    public void setMemberIdStr(String memberIdStr) {
        this.memberIdStr = memberIdStr;
    }

    public String getInvestorId() {
        return investorId;
    }

    public void setInvestorId(String investorId) {
        this.investorId = investorId;
    }

    public String getInvestorIdStr() {
        return investorIdStr;
    }

    public void setInvestorIdStr(String investorIdStr) {
        this.investorIdStr = investorIdStr;
    }

    public String getInvestorType() {
        return investorType;
    }

    public void setInvestorType(String investorType) {
        this.investorType = investorType;
    }

    public String getInvestorTypeStr() {
        return investorTypeStr;
    }

    public void setInvestorTypeStr(String investorTypeStr) {
        this.investorTypeStr = investorTypeStr;
    }

    public String getCpTraderCode() {
        return cpTraderCode;
    }

    public void setCpTraderCode(String cpTraderCode) {
        this.cpTraderCode = cpTraderCode;
    }

    public String getCpTraderCodeStr() {
        return cpTraderCodeStr;
    }

    public void setCpTraderCodeStr(String cpTraderCodeStr) {
        this.cpTraderCodeStr = cpTraderCodeStr;
    }

    public String getCpMemberId() {
        return cpMemberId;
    }

    public void setCpMemberId(String cpMemberId) {
        this.cpMemberId = cpMemberId;
    }

    public String getCpMemberIdStr() {
        return cpMemberIdStr;
    }

    public void setCpMemberIdStr(String cpMemberIdStr) {
        this.cpMemberIdStr = cpMemberIdStr;
    }

    public String getCpInvestorId() {
        return cpInvestorId;
    }

    public void setCpInvestorId(String cpInvestorId) {
        this.cpInvestorId = cpInvestorId;
    }

    public String getCpInvestorIdStr() {
        return cpInvestorIdStr;
    }

    public void setCpInvestorIdStr(String cpInvestorIdStr) {
        this.cpInvestorIdStr = cpInvestorIdStr;
    }

    public String getCpInvestorType() {
        return cpInvestorType;
    }

    public void setCpInvestorType(String cpInvestorType) {
        this.cpInvestorType = cpInvestorType;
    }

    public String getCpInvestorTypeStr() {
        return cpInvestorTypeStr;
    }

    public void setCpInvestorTypeStr(String cpInvestorTypeStr) {
        this.cpInvestorTypeStr = cpInvestorTypeStr;
    }

    public String getCpClientName() {
        return cpClientName;
    }

    public void setCpClientName(String cpClientName) {
        this.cpClientName = cpClientName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getRequestStatusStr() {
        if (ObjectUtil.isEmpty(this.getRequestStatus())) {
            return null;
        }
        String description = EnumUtil.getDescriptionByCode(QuoteStatusEnum.class, this.getRequestStatus());
        return description == null ? String.valueOf(this.getRequestStatus()) : description;
    }

    public String getRequestTypeStr() {
        if (StrUtil.isEmpty(this.getRequestType())) {
            return null;
        }
        String description = EnumUtil.getDescriptionByCode(RepurchaseTypeEnum.class, this.getRequestType());
        return description == null ? this.getRequestType() : description;
    }


    public String getBsStr() {
        if (StrUtil.isEmpty(this.getBs())) {
            return null;
        }
        String description = EnumUtil.getDescriptionByCode(RepoBsEnum.class, this.getBs());
        return description == null ? this.getBs() : description;
    }

    public String getPledgeNumberStr() {
        return pledgeNumberStr;
    }

    public void setPledgeNumberStr(String pledgeNumberStr) {
        this.pledgeNumberStr = pledgeNumberStr;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("requestStatus", requestStatus)
                .add("requestType", requestType)
                .add("bs", bs)
                .add("stockCode", stockCode)
                .add("stockName", stockName)
                .add("pledgeNumber", pledgeNumber)
                .add("counterId", counterId)
                .add("traderCode", traderCode)
                .add("traderCodeStr", traderCodeStr)
                .add("memberId", memberId)
                .add("memberIdStr", memberIdStr)
                .add("investorId", investorId)
                .add("investorIdStr", investorIdStr)
                .add("investorType", investorType)
                .add("investorTypeStr", investorTypeStr)
                .add("cpTraderCode", cpTraderCode)
                .add("cpTraderCodeStr", cpTraderCodeStr)
                .add("cpMemberId", cpMemberId)
                .add("cpMemberIdStr", cpMemberIdStr)
                .add("cpInvestorId", cpInvestorId)
                .add("cpInvestorIdStr", cpInvestorIdStr)
                .add("cpInvestorType", cpInvestorType)
                .add("cpInvestorTypeStr", cpInvestorTypeStr)
                .add("cpClientName", cpClientName)
                .add("remark", remark)
                .add("requestStatusStr", requestStatusStr)
                .add("requestTypeStr", requestTypeStr)
                .add("bsStr", bsStr)
                .add("pledgeNumberStr", pledgeNumberStr)
                .toString();
    }
}
