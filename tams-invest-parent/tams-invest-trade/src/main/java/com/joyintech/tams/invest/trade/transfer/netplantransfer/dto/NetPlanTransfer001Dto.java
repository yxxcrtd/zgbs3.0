package com.joyintech.tams.invest.trade.transfer.netplantransfer.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.tams.invest.common.dto.period.common.BussTrdTransferOrderDto;
import com.joyintech.tams.invest.common.risktrial.dto.RiskTrialRetDto;
import com.joyintech.workflow.engine.annotation.PrimaryKey;
import com.joyintech.workflow.engine.dto.ProcessEntity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 *
 * 净值型计划资产转仓
 * @author liuwei
 * @version 1.0 2019年12月23日
 * @see NetPlanTransfer001Dto
 * @since 1.0
 */
public class NetPlanTransfer001Dto extends BaseEntity implements ProcessEntity {
    /**
     * 实际组合代码
     */
    private String actPortfolioId;
    /**
     * 交易日期
     */
    private Date tradeDate;
    /**
     * 资产代码
     */
    private String finprodMarketId;
    /**
     * 金融产品代码
     */

    private String finprodId;
    /**
     * 资产名称
     */
    private String finprodAbbr;
    /**
     * 币种
     */
    private String ccy;
    /**
     * 通道名称
     */
    private String chlName;
    /**
     * 通道代码
     */
    private String chlId;
    /**
     * 持仓份额
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal posAmt;
    /**
     * 剩余可转仓份额
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal undistributedAmount;
    /**
     * 单位净值
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal unitValue;
    /**
     * 转仓列表dtoList
     */
    private List<com.joyintech.tams.invest.trade.transfer.netplantransfer.dto.NetPlanTransfer002Dto> netPlanTransfer002DtoList;
    /**
     * 待添加组合代码
     */
    private String portfolioId;
    /**
     * 组合代码（转出）
     */
    private String portfolioIdOut;
    /**
     * 证券托管账户
     */
    private String secAcctId;
    /**
     * 交易日历编号
     */
    private String calendarId;
    /**
     * 拆分列表-交易份额
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal tradeAmt;
    /**
     * 拆分列表-交易金额
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal tradeMoney;
    /**
     * 组合可用现金
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal avalibleCash;
    /**
     * 操作方式
     */
    private String operateType;
    /**
     * 画面元素
     */
    private String elementId;
    /**
     * 资产类型7
     */
    private String type7;
    /**
     * 风险试算实体类
     */
    private List<RiskTrialRetDto> riskTrialRetDto;

    /**
     * 触发加签flag，风险岗审批
     */
    private String riskFlag;

    /**
     * 附件编号
     */
    private String businessId;

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    private String fileArrayName;

    public String getFileArrayName() {
        return fileArrayName;
    }

    public void setFileArrayName(String fileArrayName) {
        this.fileArrayName = fileArrayName;
    }

    /**
     * 获取 交易日期
     *
     * @return tradeDate 交易日期
     */
    public Date getTradeDate() {
        return this.tradeDate;
    }

    /**
     * 设置 交易日期
     *
     * @param tradeDate 交易日期
     */
    public void setTradeDate(Date tradeDate) {
        this.tradeDate = tradeDate;
    }

    /**
     * 获取 资产代码
     *
     * @return finprodMarketId 资产代码
     */
    public String getFinprodMarketId() {
        return this.finprodMarketId;
    }

    /**
     * 设置 资产代码
     *
     * @param finprodMarketId 资产代码
     */
    public void setFinprodMarketId(String finprodMarketId) {
        this.finprodMarketId = finprodMarketId;
    }

    /**
     * 获取 资产名称
     *
     * @return finprodAbbr 资产名称
     */
    public String getFinprodAbbr() {
        return this.finprodAbbr;
    }

    /**
     * 设置 资产名称
     *
     * @param finprodAbbr 资产名称
     */
    public void setFinprodAbbr(String finprodAbbr) {
        this.finprodAbbr = finprodAbbr;
    }

    /**
     * 获取 币种
     *
     * @return ccy 币种
     */
    public String getCcy() {
        return this.ccy;
    }

    /**
     * 设置 币种
     *
     * @param ccy 币种
     */
    public void setCcy(String ccy) {
        this.ccy = ccy;
    }

    /**
     * 获取 通道名称
     *
     * @return chlName 通道名称
     */
    public String getChlName() {
        return this.chlName;
    }

    /**
     * 设置 通道名称
     *
     * @param chlName 通道名称
     */
    public void setChlName(String chlName) {
        this.chlName = chlName;
    }

    /**
     * 获取 通道代码
     *
     * @return chlId 通道代码
     */
    public String getChlId() {
        return this.chlId;
    }

    /**
     * 设置 通道代码
     *
     * @param chlId 通道代码
     */
    public void setChlId(String chlId) {
        this.chlId = chlId;
    }

    /**
     * 获取 持仓份额
     *
     * @return posAmt 持仓份额
     */
    public BigDecimal getPosAmt() {
        return this.posAmt;
    }

    /**
     * 设置 持仓份额
     *
     * @param posAmt 持仓份额
     */
    public void setPosAmt(BigDecimal posAmt) {
        this.posAmt = posAmt;
    }

    /**
     * 获取 剩余可转仓份额
     *
     * @return undistributedAmount 剩余可转仓份额
     */
    public BigDecimal getUndistributedAmount() {
        return this.undistributedAmount;
    }

    /**
     * 设置 剩余可转仓份额
     *
     * @param undistributedAmount 剩余可转仓份额
     */
    public void setUndistributedAmount(BigDecimal undistributedAmount) {
        this.undistributedAmount = undistributedAmount;
    }

    /**
     * 获取 转仓列表dtoList
     *
     * @return netPlanTransfer002DtoList 转仓列表dtoList
     */
    public List<com.joyintech.tams.invest.trade.transfer.netplantransfer.dto.NetPlanTransfer002Dto> getNetPlanTransfer002DtoList() {
        return this.netPlanTransfer002DtoList;
    }

    /**
     * 设置 转仓列表dtoList
     *
     * @param netPlanTransfer002DtoList 转仓列表dtoList
     */
    public void setNetPlanTransfer002DtoList(List<com.joyintech.tams.invest.trade.transfer.netplantransfer.dto.NetPlanTransfer002Dto> netPlanTransfer002DtoList) {
        this.netPlanTransfer002DtoList = netPlanTransfer002DtoList;
    }

    /**
     * 获取 单位净值
     *
     * @return unitValue 单位净值
     */
    public BigDecimal getUnitValue() {
        return this.unitValue;
    }

    /**
     * 设置 单位净值
     *
     * @param unitValue 单位净值
     */
    public void setUnitValue(BigDecimal unitValue) {
        this.unitValue = unitValue;
    }

    /**
     * 获取 组合代码
     *
     * @return portfolioId 组合代码
     */
    public String getPortfolioId() {
        return this.portfolioId;
    }

    /**
     * 设置 组合代码
     *
     * @param portfolioId 组合代码
     */
    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId;
    }

    /**
     * 获取 证券托管账户
     *
     * @return secAcctId 证券托管账户
     */
    public String getSecAcctId() {
        return this.secAcctId;
    }

    /**
     * 设置 证券托管账户
     *
     * @param secAcctId 证券托管账户
     */
    public void setSecAcctId(String secAcctId) {
        this.secAcctId = secAcctId;
    }

    /**
     * 获取 交易日历编号
     *
     * @return calendarId 交易日历编号
     */
    public String getCalendarId() {
        return this.calendarId;
    }

    /**
     * 设置 交易日历编号
     *
     * @param calendarId 交易日历编号
     */
    public void setCalendarId(String calendarId) {
        this.calendarId = calendarId;
    }

    /**
     * 获取 组合代码（转出）
     *
     * @return portfolioIdOut 组合代码（转出）
     */
    public String getPortfolioIdOut() {
        return this.portfolioIdOut;
    }

    /**
     * 设置 组合代码（转出）
     *
     * @param portfolioIdOut 组合代码（转出）
     */
    public void setPortfolioIdOut(String portfolioIdOut) {
        this.portfolioIdOut = portfolioIdOut;
    }

    /**
     * 获取 拆分列表-交易份额
     *
     * @return tradeAmt 拆分列表-交易份额
     */
    public BigDecimal getTradeAmt() {
        return this.tradeAmt;
    }

    /**
     * 设置 拆分列表-交易份额
     *
     * @param tradeAmt 拆分列表-交易份额
     */
    public void setTradeAmt(BigDecimal tradeAmt) {
        this.tradeAmt = tradeAmt;
    }

    /**
     * 获取 拆分列表-交易金额
     *
     * @return tradeMoney 拆分列表-交易金额
     */
    public BigDecimal getTradeMoney() {
        return this.tradeMoney;
    }

    /**
     * 设置 拆分列表-交易金额
     *
     * @param tradeMoney 拆分列表-交易金额
     */
    public void setTradeMoney(BigDecimal tradeMoney) {
        this.tradeMoney = tradeMoney;
    }

    /**
     * 获取 组合可用现金
     *
     * @return avalibleCash 组合可用现金
     */
    public BigDecimal getAvalibleCash() {
        return this.avalibleCash;
    }

    /**
     * 设置 组合可用现金
     *
     * @param avalibleCash 组合可用现金
     */
    public void setAvalibleCash(BigDecimal avalibleCash) {
        this.avalibleCash = avalibleCash;
    }

    /**
     * 获取 操作方式
     *
     * @return operateType 操作方式
     */
    public String getOperateType() {
        return this.operateType;
    }

    /**
     * 设置 操作方式
     *
     * @param operateType 操作方式
     */
    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }

    /**
     * 获取 画面元素
     *
     * @return elementId 画面元素
     */
    public String getElementId() {
        return this.elementId;
    }

    /**
     * 设置 画面元素
     *
     * @param elementId 画面元素
     */
    public void setElementId(String elementId) {
        this.elementId = elementId;
    }

    /**
     * 获取 金融产品代码
     *
     * @return finprodId 金融产品代码
     */
    @PrimaryKey(PrimaryKey.Field.PK_1)
    public String getFinprodId() {
        return this.finprodId;
    }

    /**
     * 设置 金融产品代码
     *
     * @param finprodId 金融产品代码
     */
    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
    }

    /**
     * 获取 实际组合代码
     *
     * @return actPortfolioId 实际组合代码
     */
    public String getActPortfolioId() {
        return this.actPortfolioId;
    }

    /**
     * 设置 实际组合代码
     *
     * @param actPortfolioId 实际组合代码
     */
    public void setActPortfolioId(String actPortfolioId) {
        this.actPortfolioId = actPortfolioId;
    }

    /**
     * 获取 资产类型7
     *
     * @return type7 资产类型7
     */
    public String getType7() {
        return this.type7;
    }

    /**
     * 设置 资产类型7
     *
     * @param type7 资产类型7
     */
    public void setType7(String type7) {
        this.type7 = type7;
    }

    /**
     * 获取 风险试算实体类
     *
     * @return riskTrialRetDto 风险试算实体类
     */
    public List<RiskTrialRetDto> getRiskTrialRetDto() {
        return this.riskTrialRetDto;
    }

    /**
     * 设置 风险试算实体类
     *
     * @param riskTrialRetDto 风险试算实体类
     */
    public void setRiskTrialRetDto(List<RiskTrialRetDto> riskTrialRetDto) {
        this.riskTrialRetDto = riskTrialRetDto;
    }

    /**
     * 转仓编号
     */
    private String zcId;

    public String getZcId() {
        return zcId;
    }

    public void setZcId(String zcId) {
        this.zcId = zcId;
    }

    /**
     * 划款指令
     */
    private List<BussTrdTransferOrderDto> bussTrdTransferOrder;

    public List<BussTrdTransferOrderDto> getBussTrdTransferOrder() {
        return bussTrdTransferOrder;
    }

    public void setBussTrdTransferOrder(List<BussTrdTransferOrderDto> bussTrdTransferOrder) {
        this.bussTrdTransferOrder = bussTrdTransferOrder;
    }
    private String checkStatus;

    private String valueStatus;

    public String getCheckStatus() {
        return checkStatus;
    }

    public void setCheckStatus(String checkStatus) {
        this.checkStatus = checkStatus;
    }

    public String getValueStatus() {
        return valueStatus;
    }

    public void setValueStatus(String valueStatus) {
        this.valueStatus = valueStatus;
    }

    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal carryOverQty;

    public BigDecimal getCarryOverQty() {
        return carryOverQty;
    }

    public void setCarryOverQty(BigDecimal carryOverQty) {
        this.carryOverQty = carryOverQty;
    }

    public String getRiskFlag() {
        return riskFlag;
    }

    public void setRiskFlag(String riskFlag) {
        this.riskFlag = riskFlag;
    }
}
