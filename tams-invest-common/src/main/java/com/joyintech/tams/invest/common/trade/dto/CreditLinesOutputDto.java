package com.joyintech.tams.invest.common.trade.dto;

import com.joyintech.framework.base.BaseEntity;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.systemtools.util.SysDatadictUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 授信处理结果，出参
 */
public class CreditLinesOutputDto extends BaseEntity {

    public CreditLinesOutputDto(){
        this.confirmMessages = new ArrayList<>();
    }

    /**
     * 编号
     */
    private String tradeId;

    /**
     * 占用类型
     */
    private String occupyType;

    /**
     * 市场机构代码
     */
    private String customerId;

    /**
     * 机构授信-占用前
     */
    private BigDecimal beforeOccupyOrg = BigDecimal.ZERO;

    /**
     * 机构授信-占用
     */
    private BigDecimal occupyAmtOrg = BigDecimal.ZERO;

    /**
     * 机构授信-占用后
     */
    private BigDecimal afterOccupyOrg = BigDecimal.ZERO;

    /**
     * 资产代码
     */
    private String assetCode;
    /**
     * 资产授信-占用前
     */
    private BigDecimal beforeOccupyAst = BigDecimal.ZERO;
    /**
     * 资产授信-占用后
     */
    private BigDecimal afterOccupyAst = BigDecimal.ZERO;
    /**
     * 资产授信-占用
     */
    private BigDecimal occupyAssetAmt = BigDecimal.ZERO;

    /**
     * 处理结果，错误信息
     */
    private String errMessage;

    /**
     * 处理结果，确认信息
     */
    private List<String> confirmMessages;

    /**
     * 试算结果
     */
    private String rskStatus;

    public String getTradeId() {
        return tradeId;
    }

    public String getOccupyType() {
        return occupyType;
    }

    public void setOccupyType(String occupyType) {
        this.occupyType = occupyType;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public BigDecimal getBeforeOccupyOrg() {
        return beforeOccupyOrg;
    }

    public void setBeforeOccupyOrg(BigDecimal beforeOccupyOrg) {
        this.beforeOccupyOrg = beforeOccupyOrg;
    }

    public BigDecimal getOccupyAmtOrg() {
        return occupyAmtOrg;
    }

    public void setOccupyAmtOrg(BigDecimal occupyAmtOrg) {
        this.occupyAmtOrg = occupyAmtOrg;
    }

    public BigDecimal getAfterOccupyOrg() {
        return afterOccupyOrg;
    }

    public void setAfterOccupyOrg(BigDecimal afterOccupyOrg) {
        this.afterOccupyOrg = afterOccupyOrg;
    }

    public String getAssetCode() {
        return assetCode;
    }

    public void setAssetCode(String assetCode) {
        this.assetCode = assetCode;
    }

    public BigDecimal getBeforeOccupyAst() {
        return beforeOccupyAst;
    }

    public void setBeforeOccupyAst(BigDecimal beforeOccupyAst) {
        this.beforeOccupyAst = beforeOccupyAst;
    }

    public BigDecimal getAfterOccupyAst() {
        return afterOccupyAst;
    }

    public void setAfterOccupyAst(BigDecimal afterOccupyAst) {
        this.afterOccupyAst = afterOccupyAst;
    }

    public BigDecimal getOccupyAssetAmt() {
        return occupyAssetAmt;
    }

    public void setOccupyAssetAmt(BigDecimal occupyAssetAmt) {
        this.occupyAssetAmt = occupyAssetAmt;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }

    public String getErrMessage() {
        return errMessage;
    }

    public void setErrMessage(String errMessage) {
        this.errMessage = errMessage;
    }

    public List<String> getConfirmMessages() {
        return confirmMessages;
    }

    public void setConfirmMessages(List<String> confirmMessages) {
        this.confirmMessages = confirmMessages;
    }

    public String getRskStatusStr() {
        return SysDatadictUtil.getNameFromDataDict(DictCode.试算结果,rskStatus);
    }

    public String getRskStatus() {
        return rskStatus;
    }

    public void setRskStatus(String rskStatus) {
        this.rskStatus = rskStatus;
    }
}
