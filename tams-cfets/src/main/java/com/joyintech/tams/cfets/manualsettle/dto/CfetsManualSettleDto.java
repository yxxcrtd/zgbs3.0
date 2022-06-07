package com.joyintech.tams.cfets.manualsettle.dto;

import com.joyintech.framework.base.BaseEntity;
import com.joyintech.workflow.engine.annotation.PrimaryKey;
import com.joyintech.workflow.engine.dto.ProcessEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

/**
 * 银行间手工交收
 * 银行间手工交收前台返回参数DTO
 * @author sjj
 * @version 1.0 2021年11月12日
 * @see CfetsManualSettleDto
 * @since 1.0
 */

@ApiModel(value="银行间手工交收参数",description="银行间手工交收参数")
public class CfetsManualSettleDto extends BaseEntity implements ProcessEntity {

    @ApiModelProperty(name="settleStatus",value="交收状态CODE",example="01")
    private String settleStatus;

    @ApiModelProperty(name="settleStatusStr",value="交收状态",example="未交收")
    private String settleStatusStr;

    @PrimaryKey(PrimaryKey.Field.PK_1)
    @ApiModelProperty(name="tradeId",value="原业务编号",example="A1")
    private String tradeId;

    @ApiModelProperty(name="tradeDate",value="交易日期",example="2021-01-01")
    private String tradeDate;

    @ApiModelProperty(name="settleDate",value="交收日期",example="2021-01-01",required = true)
    private String settleDate;

    @ApiModelProperty(name="finprodMarketId",value="资产简称",example="字符串1")
    private String finprodMarketId;

    @ApiModelProperty(name="finprodAbbr",value="资产简称",example="字符串1")
    private String finprodAbbr;

    @ApiModelProperty(name="finprodType2",value="资产类型CODE",example="A1")
    private String finprodType2;

    @ApiModelProperty(name="finprodType2Str",value="资产类型",example="A1")
    private String finprodType2Str;

    @ApiModelProperty(name="tgBusiType",value="业务类型CODE",example="A1")
    private String tgBusiType;

    @ApiModelProperty(name="tgBusiTypeStr",value="业务类型",example="A1")
    private String tgBusiTypeStr;

    @ApiModelProperty(name="ps",value="交易方向CODE",example="A1")
    private String ps;

    @ApiModelProperty(name="psStr",value="交易方向",example="A1")
    private String psStr;

    @ApiModelProperty(name="settleNum",value="交收数量",example="A1")
    private BigDecimal settleNum;

    @ApiModelProperty(name="settleAmt",value="交收金额",example="A1")
    private BigDecimal settleAmt;

    @ApiModelProperty(name="portfolioId",value="组合名称集合",example="字符串1")
    private String portfolioId;

    @ApiModelProperty(name="portfolioName",value="组合名称",example="字符串1")
    private String portfolioName;

    @ApiModelProperty(name="cleanStatus",value="清算状态CODE",example="A1,A2")
    private String cleanStatus;

    @ApiModelProperty(name="cleanStatusStr",value="清算状态",example="A1,A2")
    private String cleanStatusStr;

    @ApiModelProperty(name="tradePlat",value="交易场所CODE",example="A1")
    private String tradePlat;

    @ApiModelProperty(name="tradePlatStr",value="交易场所",example="银行间")
    private String tradePlatStr;

    @ApiModelProperty(name="tradeMarket",value="交易市场CODE",example="01")
    private String tradeMarket;

    @ApiModelProperty(name="tradeMarketStr",value="交易市场",example="银行间")
    private String tradeMarketStr;

    @ApiModelProperty(name="secAcctId",value="证券账户",example="XXX证券账户")
    private String secAcctId;

    @ApiModelProperty(name="counterName",value="交易对手",example="admin")
    private String counterName;

    @ApiModelProperty(name="cleanStlStatus",value="结算指令状态CODE",example="A1")
    private String cleanStlStatus;

    @ApiModelProperty(name="cleanStlStatusStr",value="结算指令状态",example="A1")
    private String cleanStlStatusStr;

    @ApiModelProperty(name="cleanArgStatus",value="结算合同状态CODE",example="A1")
    private String cleanArgStatus;

    @ApiModelProperty(name="cleanArgStatusStr",value="结算合同状态",example="A1")
    private String cleanArgStatusStr;

    @ApiModelProperty(name="settleSeqNo",value="交收编号",example="A1")
    private String settleSeqNo;

    @ApiModelProperty(name="finprodId",value="金融产品代码",example="不展示仅供后台使用")
    private String finprodId;

    @ApiModelProperty(name="ccy",value="币种",example="不展示仅供后台使用")
    private String ccy;

    @ApiModelProperty(name="seqNo",value="交收流水序号",example="不展示仅供后台使用")
    private String seqNo;

    @ApiModelProperty(name="tradeMarketMode",value="交易市场",example="01/02")
    private String tradeMarketMode;

    @ApiModelProperty(name="tradeMarketModeStr",value="交易市场",example="一级/二级市场")
    private String tradeMarketModeStr;

    // 要素ID
    private String elementId;

    // 操作类型 保存 提交
    private String operateType;

    // 按钮类型 交收01、回溯02、交收失败04、修改05
    private String buttonType;

    @ApiModelProperty(name="remark",value="交收失败原因",example="A1")
    private String remark;

    @ApiModelProperty(name="busiTypePosition",value="业务类型",example="A1")
    private String busiTypePosition;

    @ApiModelProperty(name="bidModle",value="发行方式",example="A1")
    private String bidModle;

    @ApiModelProperty(name="totleTradeId",value="交易编号",example="A1")
    private String totleTradeId;

    public String getTotleTradeId() {
        return totleTradeId;
    }

    public void setTotleTradeId(String totleTradeId) {
        this.totleTradeId = totleTradeId;
    }

    public String getBidModle() {
        return bidModle;
    }

    public void setBidModle(String bidModle) {
        this.bidModle = bidModle;
    }

    public String getSettleStatus() {
        return settleStatus;
    }

    public void setSettleStatus(String settleStatus) {
        this.settleStatus = settleStatus;
    }

    public String getSettleStatusStr() {
        return settleStatusStr;
    }

    public void setSettleStatusStr(String settleStatusStr) {
        this.settleStatusStr = settleStatusStr;
    }

    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }

    public String getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(String tradeDate) {
        this.tradeDate = tradeDate;
    }

    public String getSettleDate() {
        return settleDate;
    }

    public void setSettleDate(String settleDate) {
        this.settleDate = settleDate;
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

    public String getFinprodType2() {
        return finprodType2;
    }

    public void setFinprodType2(String finprodType2) {
        this.finprodType2 = finprodType2;
    }

    public String getFinprodType2Str() {
        return finprodType2Str;
    }

    public void setFinprodType2Str(String finprodType2Str) {
        this.finprodType2Str = finprodType2Str;
    }

    public String getTgBusiType() {
        return tgBusiType;
    }

    public void setTgBusiType(String tgBusiType) {
        this.tgBusiType = tgBusiType;
    }

    public String getTgBusiTypeStr() {
        return tgBusiTypeStr;
    }

    public void setTgBusiTypeStr(String tgBusiTypeStr) {
        this.tgBusiTypeStr = tgBusiTypeStr;
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

    public BigDecimal getSettleNum() {
        return settleNum;
    }

    public void setSettleNum(BigDecimal settleNum) {
        this.settleNum = settleNum;
    }

    public BigDecimal getSettleAmt() {
        return settleAmt;
    }

    public void setSettleAmt(BigDecimal settleAmt) {
        this.settleAmt = settleAmt;
    }

    public String getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId;
    }

    public String getPortfolioName() {
        return portfolioName;
    }

    public void setPortfolioName(String portfolioName) {
        this.portfolioName = portfolioName;
    }

    public String getCleanStatus() {
        return cleanStatus;
    }

    public void setCleanStatus(String cleanStatus) {
        this.cleanStatus = cleanStatus;
    }

    public String getCleanStatusStr() {
        return cleanStatusStr;
    }

    public void setCleanStatusStr(String cleanStatusStr) {
        this.cleanStatusStr = cleanStatusStr;
    }

    public String getTradePlat() {
        return tradePlat;
    }

    public void setTradePlat(String tradePlat) {
        this.tradePlat = tradePlat;
    }

    public String getTradePlatStr() {
        return tradePlatStr;
    }

    public void setTradePlatStr(String tradePlatStr) {
        this.tradePlatStr = tradePlatStr;
    }

    public String getTradeMarket() {
        return tradeMarket;
    }

    public void setTradeMarket(String tradeMarket) {
        this.tradeMarket = tradeMarket;
    }

    public String getTradeMarketStr() {
        return tradeMarketStr;
    }

    public void setTradeMarketStr(String tradeMarketStr) {
        this.tradeMarketStr = tradeMarketStr;
    }

    public String getSecAcctId() {
        return secAcctId;
    }

    public void setSecAcctId(String secAcctId) {
        this.secAcctId = secAcctId;
    }

    public String getCounterName() {
        return counterName;
    }

    public void setCounterName(String counterName) {
        this.counterName = counterName;
    }

    public String getCleanStlStatus() {
        return cleanStlStatus;
    }

    public void setCleanStlStatus(String cleanStlStatus) {
        this.cleanStlStatus = cleanStlStatus;
    }

    public String getCleanStlStatusStr() {
        return cleanStlStatusStr;
    }

    public void setCleanStlStatusStr(String cleanStlStatusStr) {
        this.cleanStlStatusStr = cleanStlStatusStr;
    }

    public String getCleanArgStatus() {
        return cleanArgStatus;
    }

    public void setCleanArgStatus(String cleanArgStatus) {
        this.cleanArgStatus = cleanArgStatus;
    }

    public String getCleanArgStatusStr() {
        return cleanArgStatusStr;
    }

    public void setCleanArgStatusStr(String cleanArgStatusStr) {
        this.cleanArgStatusStr = cleanArgStatusStr;
    }

    public String getSettleSeqNo() {
        return settleSeqNo;
    }

    public void setSettleSeqNo(String settleSeqNo) {
        this.settleSeqNo = settleSeqNo;
    }

    public String getFinprodId() {
        return finprodId;
    }

    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
    }

    public String getCcy() {
        return ccy;
    }

    public void setCcy(String ccy) {
        this.ccy = ccy;
    }

    public String getSeqNo() {
        return seqNo;
    }

    public void setSeqNo(String seqNo) {
        this.seqNo = seqNo;
    }

    public String getElementId() {
        return elementId;
    }

    public void setElementId(String elementId) {
        this.elementId = elementId;
    }

    public String getOperateType() {
        return operateType;
    }

    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }

    public String getButtonType() {
        return buttonType;
    }

    public void setButtonType(String buttonType) {
        this.buttonType = buttonType;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getTradeMarketMode() {
        return tradeMarketMode;
    }

    public void setTradeMarketMode(String tradeMarketMode) {
        this.tradeMarketMode = tradeMarketMode;
    }

    public String getTradeMarketModeStr() {
        return tradeMarketModeStr;
    }

    public void setTradeMarketModeStr(String tradeMarketModeStr) {
        this.tradeMarketModeStr = tradeMarketModeStr;
    }

    public String getBusiTypePosition() {
        return busiTypePosition;
    }

    public void setBusiTypePosition(String busiTypePosition) {
        this.busiTypePosition = busiTypePosition;
    }
}
