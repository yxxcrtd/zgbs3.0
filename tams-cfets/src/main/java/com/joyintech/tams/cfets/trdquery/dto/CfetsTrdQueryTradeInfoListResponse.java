/*
 * 文件名：CfetsTrdQueryRegInfoListResponse.java
 * 版权：Copyright 2020-2022 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管系统V2.0
 * 修改人：
 * 修改时间：2021年01月25日
 * 修改内容：新建
 * 系统名称：理财资管系统V2.0
 */

package com.joyintech.tams.cfets.trdquery.dto;

import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.invest.common.constant.BussinessDictCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 银行间交易查询
 * 查询银行间交易列表返回数据DTO
 * @author 1
 * @version 1.0 2021年10月26日
 * @see CfetsTrdQueryTradeInfoListResponse
 * @since 1.0
 */
@ApiModel(value="交易查询列表返回数据",description="交易查询列表返回数据")
public class CfetsTrdQueryTradeInfoListResponse {

    @ApiModelProperty(name="effectiveStatus",value="有效状态Code",example="A1")
    private String effectiveStatus;

    @ApiModelProperty(name="effectiveStatusStr",value="有效状态",example="A1")
    private String effectiveStatusStr;

    @ApiModelProperty(name="cleanStatus",value="清算状态CODE",example="A1,A2")
    private String cleanStatus;

    @ApiModelProperty(name="cleanStatusStr",value="清算状态",example="未交收")
    private String cleanStatusStr;

    @ApiModelProperty(name="settleStatus",value="交收状态CODE",example="01")
    private String settleStatus;

    @ApiModelProperty(name="settleStatusStr",value="交收状态",example="未交收")
    private String settleStatusStr;

    @ApiModelProperty(name="totleTradeId",value="汇总交易编号",example="A1")
    private String totleTradeId;

    @ApiModelProperty(name="finprodType2",value="资产类型CODE",example="A1")
    private String finprodType2;

    @ApiModelProperty(name="finprodType2Str",value="资产类型",example="A1")
    private String finprodType2Str;

    @ApiModelProperty(name="tgBusiType",value="业务类型CODE",example="A1")
    private String tgBusiType;

    @ApiModelProperty(name="tgBusiTypeStr",value="业务类型",example="A1")
    private String tgBusiTypeStr;

    @ApiModelProperty(name="finprodId",value="资产代码Id",example="ZC1")
    private String finprodId;

    @ApiModelProperty(name="finprodMarketId",value="资产代码",example="ZC1")
    private String finprodMarketId;

    @ApiModelProperty(name="finprodAbbr",value="资产简称",example="ZC名")
    private String finprodAbbr;

    @ApiModelProperty(name="ps",value="交易方向CODE",example="A1")
    private String ps;

    @ApiModelProperty(name="psStr",value="交易方向",example="A1")
    private String psStr;

    @ApiModelProperty(name="tradeMarket",value="交易场所CODE",example="01")
    private String tradeMarket;

    @ApiModelProperty(name="tradeMarketStr",value="交易场所",example="银行间")
    private String tradeMarketStr;

    @ApiModelProperty(name="tradeMarketMode",value="交易市场",example="01一级投标02二级交易03行权交易04存续期交易")
    private String tradeMarketMode;

    @ApiModelProperty(name="tradeMarketModeStr",value="交易市场",example="01一级投标02二级交易03行权交易04存续期交易")
    private String tradeMarketModeStr;

    @ApiModelProperty(name="settleSpeed",value="清算速度Code",example="1")
    private String settleSpeed;

    @ApiModelProperty(name="settleSpeedStr",value="清算速度",example="1")
    private String settleSpeedStr;

    @ApiModelProperty(name="secManageAcctId",value="证券账户ID",example="1")
    private String secManageAcctId;

    @ApiModelProperty(name="secManageAcctIdStr",value="证券账户",example="1")
    private String secManageAcctIdStr;

    @ApiModelProperty(name="faceValue",value="券面总额",example="1")
    private BigDecimal faceValue;

    @ApiModelProperty(name="tradeYield",value="成交价",example="1")
    private String tradeYield;

    @ApiModelProperty(name="termDays",value="期限（天）",example="1")
    private BigDecimal termDays;

    @ApiModelProperty(name="tradeAmt",value="交易金额",example="1")
    private BigDecimal tradeAmt;
    @ApiModelProperty(name="ccy",value="币种",example="字符串1")
    private String ccy;

    @ApiModelProperty(name="ccyStr",value="币种Str",example="字符串1")
    private String ccyStr;

    @ApiModelProperty(name="confirmTradeDate",value="成交日期Date",example="admin")
    private String  confirmTradeDate;

    @ApiModelProperty(name="settleDate",value="结算日期",example="字符串1")
    private String settleDate;

    @ApiModelProperty(name="execid",value="成交编号",example="1")
    private String execid;

    @ApiModelProperty(name="counterId",value="交易对手Id",example="字符串1")
    private String counterId;

    @ApiModelProperty(name="counterIdStr",value="交易对手Id",example="字符串1")
    private String counterIdStr;

    @ApiModelProperty(name="remark",value="备注",example="字符串1")
    private String remark;

    @ApiModelProperty(name="busiCode",value="业务编号",example="字符串1")
    private String busiCode;

    @ApiModelProperty(name="entrustId",value="指令代码",example="01")
    private String entrustId;

    @ApiModelProperty(name="portfolioId",value="组合代码",example="A1")
    private String portfolioId;

    @ApiModelProperty(name="portfolioName",value="组合名称",example="A1")
    private String portfolioName;

    @ApiModelProperty(name="tradeDate",value="成交日期",example="admin")
    private Date  tradeDate;

    @ApiModelProperty(name="preTradeId",value="前置成交编号",example="1")
    private String preTradeId;

    @ApiModelProperty(name="calMode",value="计算方式",example="计算方式供前台判断使用")
    private String calMode;

    @ApiModelProperty(name="matchStatus",value="匹配状态",example="matchStatus")
    private String matchStatus;

    @ApiModelProperty(name="trader",value="交易员",example="trader")
    private String trader;

    @ApiModelProperty(name="bidId",value="投标编号")
    private String bidId;

    @ApiModelProperty(name="bidModle",value="交易员")
    private String bidModle;

    @ApiModelProperty(name="selectedItem",value="选择的数据",example="1")
    private transient List<CfetsTrdQueryTradeInfoListResponse> selectedItem;
    @ApiModelProperty(name="operateType",value="操作类型",example="1")
    private String operateType;
    @ApiModelProperty(name="revokeReason",value="撤销原因",example="1")
    private String revokeReason;
    @ApiModelProperty(name="workflowParameter",value="撤销参数",example="1")
    private String workflowParameter;
    public String getCalMode() {
        return calMode;
    }

    public void setCalMode(String calMode) {
        this.calMode = calMode;
    }

    public String getEffectiveStatus() {
        return effectiveStatus;
    }

    public void setEffectiveStatus(String effectiveStatus) {
        this.effectiveStatus = effectiveStatus;
    }

    public String getEffectiveStatusStr() {
        return SysDataUtil.getNameFromDataDict(BussinessDictCode.有效状态, getEffectiveStatus());
    }

    public void setEffectiveStatusStr(String effectiveStatusStr) {
        this.effectiveStatusStr = effectiveStatusStr;
    }

    public String getCleanStatus() {
        return cleanStatus;
    }

    public void setCleanStatus(String cleanStatus) {
        this.cleanStatus = cleanStatus;
    }

    public String getCleanStatusStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.清算状态, getCleanStatus());
    }

    public void setCleanStatusStr(String cleanStatusStr) {
        this.cleanStatusStr = cleanStatusStr;
    }

    public String getSettleStatus() {
        return settleStatus;
    }

    public void setSettleStatus(String settleStatus) {
        this.settleStatus = settleStatus;
    }

    public String getSettleStatusStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.交收状态, getSettleStatus());
    }

    public void setSettleStatusStr(String settleStatusStr) {
        this.settleStatusStr = settleStatusStr;
    }

    public String getTotleTradeId() {
        return totleTradeId;
    }

    public void setTotleTradeId(String totleTradeId) {
        this.totleTradeId = totleTradeId;
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

    public String getFinprodId() {
        return finprodId;
    }

    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
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

    public String getSettleSpeed() {
        return settleSpeed;
    }

    public void setSettleSpeed(String settleSpeed) {
        this.settleSpeed = settleSpeed;
    }

    public String getSettleSpeedStr() {
        return settleSpeedStr;
    }

    public void setSettleSpeedStr(String settleSpeedStr) {
        this.settleSpeedStr = settleSpeedStr;
    }

    public String getSecManageAcctId() {
        return secManageAcctId;
    }

    public void setSecManageAcctId(String secManageAcctId) {
        this.secManageAcctId = secManageAcctId;
    }

    public String getSecManageAcctIdStr() {
        return secManageAcctIdStr;
    }

    public void setSecManageAcctIdStr(String secManageAcctIdStr) {
        this.secManageAcctIdStr = secManageAcctIdStr;
    }

    public BigDecimal getFaceValue() {
        return faceValue;
    }

    public void setFaceValue(BigDecimal faceValue) {
        this.faceValue = faceValue;
    }

    public String getTradeYield() {
        return tradeYield;
    }

    public void setTradeYield(String tradeYield) {
        this.tradeYield = tradeYield;
    }

    public BigDecimal getTermDays() {
        return termDays;
    }

    public void setTermDays(BigDecimal termDays) {
        this.termDays = termDays;
    }

    public BigDecimal getTradeAmt() {
        return tradeAmt;
    }

    public void setTradeAmt(BigDecimal tradeAmt) {
        this.tradeAmt = tradeAmt;
    }

    public String getCcy() {
        return ccy;
    }

    public void setCcy(String ccy) {
        this.ccy = ccy;
    }

    public String getCcyStr() {
        return ccyStr;
    }

    public void setCcyStr(String ccyStr) {
        this.ccyStr = ccyStr;
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

    public String getBusiCode() {
        return busiCode;
    }

    public void setBusiCode(String busiCode) {
        this.busiCode = busiCode;
    }

    public String getEntrustId() {
        return entrustId;
    }

    public void setEntrustId(String entrustId) {
        this.entrustId = entrustId;
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

    public String getCounterIdStr() {
        return counterIdStr;
    }

    public void setCounterIdStr(String counterIdStr) {
        this.counterIdStr = counterIdStr;
    }

    public String getCounterId() {
        return counterId;
    }

    public void setCounterId(String counterId) {
        this.counterId = counterId;
    }

    public String getConfirmTradeDate() {
        return confirmTradeDate;
    }

    public void setConfirmTradeDate(String confirmTradeDate) {
        this.confirmTradeDate = confirmTradeDate;
    }

    public String getSettleDate() {
        return settleDate;
    }

    public void setSettleDate(String settleDate) {
        this.settleDate = settleDate;
    }

    public Date getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(Date tradeDate) {
        this.tradeDate = tradeDate;
    }

    public String getPreTradeId() {
        return preTradeId;
    }

    public void setPreTradeId(String preTradeId) {
        this.preTradeId = preTradeId;
    }

    public String getMatchStatus() {
        return matchStatus;
    }

    public void setMatchStatus(String matchStatus) {
        this.matchStatus = matchStatus;
    }

    public String getTrader() {
        return trader;
    }

    public void setTrader(String trader) {
        this.trader = trader;
    }

    public List<CfetsTrdQueryTradeInfoListResponse> getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(List<CfetsTrdQueryTradeInfoListResponse> selectedItem) {
        this.selectedItem = selectedItem;
    }

    public String getOperateType() {
        return operateType;
    }

    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }

    public String getRevokeReason() {
        return revokeReason;
    }

    public void setRevokeReason(String revokeReason) {
        this.revokeReason = revokeReason;
    }

    public String getWorkflowParameter() {
        return workflowParameter;
    }

    public void setWorkflowParameter(String workflowParameter) {
        this.workflowParameter = workflowParameter;
    }

    public String getBidId() {
        return bidId;
    }

    public void setBidId(String bidId) {
        this.bidId = bidId;
    }

    public String getBidModle() {
        return bidModle;
    }

    public void setBidModle(String bidModle) {
        this.bidModle = bidModle;
    }
}
