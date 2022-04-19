package com.joyintech.tams.cfets.trd.bond.dto;

import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.invest.common.constant.BussinessDictCode;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author zznn
 * @date 2021/11/19
 */
@ApiModel(value="交易执行明细",description="交易执行明细")
public class DealTransactionDetailsDto {

    @ApiModelProperty(name="validStatus",value="有效状态")
    private String validStatus;

    @ApiModelProperty(name="validStatusStr",value="有效状态名称")
    private String validStatusStr;

    @ApiModelProperty(name="quoteType",value="交易类型")
    private String quoteType;

    @ApiModelProperty(name="quoteTypeStr",value="交易类型名称")
    private String quoteTypeStr;


    @ApiModelProperty(name="quoteRoleType",value="本方角色")
    private String quoteRoleType;

    @ApiModelProperty(name="quoteRoleTypeStr",value="本方角色名称")
    private String quoteRoleTypeStr;

    @ApiModelProperty(name="settleDate",value="成交日期")
    private Date settleDate;

    @ApiModelProperty(name="settleDateStr",value="成交日期格式化")
    private String settleDateStr;

    @ApiModelProperty(name="ps",value="本方方向")
    private String ps;

    @ApiModelProperty(name="psStr",value="本方方向名称")
    private String psStr;

    @ApiModelProperty(name="finprodId",value="资产代码")
    private String finprodId;

    @ApiModelProperty(name="finprodMarketId",value="市场代码")
    private String finprodMarketId;

    @ApiModelProperty(name="finprodAbbr",value="资产简称")
    private String finprodAbbr;

    @ApiModelProperty(name="tradeStatus",value="报价状态")
    private String tradeStatus;

    @ApiModelProperty(name="tradeStatusStr",value="报价状态名称")
    private String tradeStatusStr;

    @ApiModelProperty(name="sendStatus",value="发送状态")
    private String sendStatus;

    @ApiModelProperty(name="sendStatusStr",value="发送状态名称")
    private String sendStatusStr;

    @ApiModelProperty(name="shareAmt",value="券面总额（万元）")
    private BigDecimal shareAmt;

    @ApiModelProperty(name="price",value="净价（元）")
    private BigDecimal price;

    @ApiModelProperty(name="matureYield",value="到期收益率（%）")
    private BigDecimal matureYield;

    @ApiModelProperty(name="exerYield",value="行权收益率（%）")
    private BigDecimal exerYield;

    @ApiModelProperty(name="settleSpeed",value="清算速度")
    private Long settleSpeed;

    @ApiModelProperty(name="settleSpreedStr",value="清算速度")
    private String settleSpeedStr;

    @ApiModelProperty(name="ouPartyId",value="本方")
    private String ouPartyId;

    @ApiModelProperty(name="ouTraderId",value="本方交易员")
    private String ouTraderId;

    @ApiModelProperty(name="ouPartyName",value="本方名称")
    private String ouPartyName;

    @ApiModelProperty(name="ouTraderName",value="本方交易员名称")
    private String ouTraderName;

    @ApiModelProperty(name="cpPartyId",value="对手方")
    private String cpPartyId;

    @ApiModelProperty(name="cpTraderId",value="对手方交易员")
    private String cpTraderId;

    @ApiModelProperty(name="cpPartyName",value="对手方名称")
    private String cpPartyName;

    @ApiModelProperty(name="cpTraderName",value="对手方交易员名称")
    private String cpTraderName;

    @ApiModelProperty(name="shareAmtHun",value="回复量（万）")
    private BigDecimal shareAmtHun;

    @ApiModelProperty(name="reqEffTime",value="请求有效")
    private String reqEffTime;

    @ApiModelProperty(name="resEffTime",value="回复有效 ")
    private String resEffTime;

    @ApiModelProperty(name="quoteId",value="业务编号")
    private String quoteId;

    @ApiModelProperty(name="trdId",value="成交编号")
    private String trdId;

    @ApiModelProperty(name="remark",value="备注")
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

    public String getQuoteRoleTypeStr() {
        return SysDataUtil.getNameFromDataDict(BussinessDictCode.银行间上行本方角色,
                this.getQuoteRoleType());
    }

    public void setQuoteRoleTypeStr(String quoteRoleTypeStr) {
        this.quoteRoleTypeStr = quoteRoleTypeStr;
    }

    public Date getSettleDate() {
        return settleDate;
    }

    public void setSettleDate(Date settleDate) {
        this.settleDate = settleDate;
    }

    public String getSettleDateStr() {
        if(null!=this.getSettleDate()){
            return DateUtil.format(this.getSettleDate());
        }
        return settleDateStr;
    }

    public void setSettleDateStr(String settleDateStr) {
        this.settleDateStr = settleDateStr;
    }

    public String getPs() {
        return ps;
    }

    public void setPs(String ps) {
        this.ps = ps;
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

    public BigDecimal getShareAmt() {
        return shareAmt;
    }

    public void setShareAmt(BigDecimal shareAmt) {
        this.shareAmt = shareAmt;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getMatureYield() {
        return matureYield;
    }

    public void setMatureYield(BigDecimal matureYield) {
        this.matureYield = matureYield;
    }

    public BigDecimal getExerYield() {
        return exerYield;
    }

    public void setExerYield(BigDecimal exerYield) {
        this.exerYield = exerYield;
    }

    public Long getSettleSpeed() {
        return settleSpeed;
    }

    public void setSettleSpeed(Long settleSpeed) {
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

    public BigDecimal getShareAmtHun() {
        return shareAmtHun;
    }

    public void setShareAmtHun(BigDecimal shareAmtHun) {
        this.shareAmtHun = shareAmtHun;
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
        } else if (StringUtils.equals(this.getValidStatus(), BussinessDictCodeList.有效状态_撤销.getValue())) {
            return BussinessDictCodeList.有效状态_撤销.getCodeName();
        }else{
            return BussinessDictCodeList.有效状态_有效.getCodeName();
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
        if (this.getSettleSpeed()!= null && this.getSettleSpeed() == 0L) {
            return BussinessDictCodeList.清算速度_T加0.getCodeName();
        }
        else if (this.getSettleSpeed() != null && this.getSettleSpeed() == 1L) {
            return BussinessDictCodeList.清算速度_T加1.getCodeName();
        }
        else if (this.getSettleSpeed() != null && this.getSettleSpeed() == 2L) {
            return BussinessDictCodeList.清算速度_T加2.getCodeName();
        }
        return null;
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

    public String getFinprodId() {
        return finprodId;
    }

    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
    }
}

