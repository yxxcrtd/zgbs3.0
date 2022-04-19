package com.joyintech.tams.cfets.manualsettle.dto;

import com.joyintech.framework.base.BaseEntity;
import com.joyintech.workflow.engine.annotation.PrimaryKey;
import com.joyintech.workflow.engine.dto.ProcessEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.List;

/**
 * 银行间手工交收
 * 银行间手工交收修改参数
 * @author sjj
 * @version 1.0 2021年11月22日
 * @see CfetsManualSettleUpdateRequest
 * @since 1.0
 */

@ApiModel(value="银行间手工交收修改参数",description="银行间手工交收修改参数")
public class CfetsManualSettleUpdateRequest extends BaseEntity implements ProcessEntity{
    // 要素ID
    private String elementId;

    // 操作类型 保存 提交
    private String operateType;

    // 按钮类型 交收01、交收失败02、回溯04、修改05
    private String buttonType;

    @ApiModelProperty(name="settleStatus",value="交收状态CODE",example="01")
    private String settleStatus;

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

    @ApiModelProperty(name="cleanStatus",value="清算状态CODE",example="A1,A2")
    private String cleanStatus;

    @ApiModelProperty(name="tradePlat",value="交易场所CODE",example="A1")
    private String tradePlat;

    @ApiModelProperty(name="tradeMarket",value="交易市场CODE",example="01")
    private String tradeMarket;

    @ApiModelProperty(name="secAcctId",value="证券账户",example="XXX证券账户")
    private String secAcctId;

    @ApiModelProperty(name="counterName",value="交易对手",example="admin")
    private String counterName;

    @ApiModelProperty(name="cleanStlStatus",value="结算指令状态CODE",example="A1")
    private String cleanStlStatus;

    @ApiModelProperty(name="cleanArgStatus",value="结算合同状态CODE",example="A1")
    private String cleanArgStatus;

    @ApiModelProperty(name="settleSeqNo",value="交收编号",example="A1")
    private String settleSeqNo;

    @ApiModelProperty(name="finprodId",value="金融产品代码",example="不展示仅供后台使用")
    private String finprodId;

    @ApiModelProperty(name="ccy",value="币种",example="不展示仅供后台使用")
    private String ccy;

//    CfetsManualSettleDto selectedItem;

    @ApiModelProperty(name="settleNumT",value="汇总交收数量",example="A1")
    private BigDecimal settleNumT;

    @ApiModelProperty(name="settleAmtT",value="汇总交收金额",example="A1")
    private BigDecimal settleAmtT;

    //页面组合信息
    List<PortSettleinfoDto> portSettleinfoDtoList;

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

    public List<PortSettleinfoDto> getPortSettleinfoDtoList() {
        return portSettleinfoDtoList;
    }

    public void setPortSettleinfoDtoList(List<PortSettleinfoDto> portSettleinfoDtoList) {
        this.portSettleinfoDtoList = portSettleinfoDtoList;
    }

    public BigDecimal getSettleNumT() {
        return settleNumT;
    }

    public void setSettleNumT(BigDecimal settleNumT) {
        this.settleNumT = settleNumT;
    }

    public BigDecimal getSettleAmtT() {
        return settleAmtT;
    }

    public void setSettleAmtT(BigDecimal settleAmtT) {
        this.settleAmtT = settleAmtT;
    }

    public String getSettleStatus() {
        return settleStatus;
    }

    public void setSettleStatus(String settleStatus) {
        this.settleStatus = settleStatus;
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

    public String getTgBusiType() {
        return tgBusiType;
    }

    public void setTgBusiType(String tgBusiType) {
        this.tgBusiType = tgBusiType;
    }

    public String getPs() {
        return ps;
    }

    public void setPs(String ps) {
        this.ps = ps;
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

    public String getCleanStatus() {
        return cleanStatus;
    }

    public void setCleanStatus(String cleanStatus) {
        this.cleanStatus = cleanStatus;
    }

    public String getTradePlat() {
        return tradePlat;
    }

    public void setTradePlat(String tradePlat) {
        this.tradePlat = tradePlat;
    }

    public String getTradeMarket() {
        return tradeMarket;
    }

    public void setTradeMarket(String tradeMarket) {
        this.tradeMarket = tradeMarket;
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

    public String getCleanArgStatus() {
        return cleanArgStatus;
    }

    public void setCleanArgStatus(String cleanArgStatus) {
        this.cleanArgStatus = cleanArgStatus;
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

    public String getTgBusiTypeStr() {
        return tgBusiTypeStr;
    }

    public void setTgBusiTypeStr(String tgBusiTypeStr) {
        this.tgBusiTypeStr = tgBusiTypeStr;
    }

    public String getPsStr() {
        return psStr;
    }

    public void setPsStr(String psStr) {
        this.psStr = psStr;
    }
}
