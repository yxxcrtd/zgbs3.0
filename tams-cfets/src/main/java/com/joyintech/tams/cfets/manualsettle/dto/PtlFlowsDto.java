package com.joyintech.tams.cfets.manualsettle.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.framework.json.CustomDateDeserialize;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 组合持仓。组合流水共通Dto
 * @Author: kchen
 * @Date: 2022/3/21 21:40
 */
public class PtlFlowsDto extends BaseEntity {

    /**
     * 组合代码
     */
    private String portfolioId;

    /**
     * 证券管理账户/通道代码，无通道无证券管理账户的存999999
     */
    private String secAcctId;

    /**
     * 分支序号
     */
    private Long branch;

    /**
     * 投资目的
     */
    private String invAim;

    /**
     * 持仓类型，可用、实际、在途、质押、融入、融出
     */
    private String hodingType;

    /**
     * 交易编号
     */
    private String tradeId;

    /**
     * 结算日期
     */
    private Date settleDate;

    /**
     * 交易金额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal tradeAmt;

    /**
     * 交易份额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal shareAmt;

    /**
     * 委托编号
     */
    private String entrustId;

    /**
     *数据已入新头寸，Y：已入
     */
    private String isExistNew;

    /**
     * 冲销业务编号
     */
    private String offsetId;

    /**
     * 冲正标识
     */
    private String offsetFlag;

    /**
     * 金融产品代码
     */
    private String finprodId;

    /**
     * 交易方向
     */
    private String ps;

    /**
     * 交易币种
     */
    private String ccy;

    public String getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId;
    }

    public String getSecAcctId() {
        return secAcctId;
    }

    public void setSecAcctId(String secAcctId) {
        this.secAcctId = secAcctId;
    }

    public Long getBranch() {
        return branch;
    }

    public void setBranch(Long branch) {
        this.branch = branch;
    }

    public String getInvAim() {
        return invAim;
    }

    public void setInvAim(String invAim) {
        this.invAim = invAim;
    }

    public String getHodingType() {
        return hodingType;
    }

    public void setHodingType(String hodingType) {
        this.hodingType = hodingType;
    }

    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }

    /**
     * 取得 结算日期
     * @return 结算日期
     */
    public Date getSettleDate() {
        return settleDate;
    }

    /**
     * 设置 结算日期
     * @param settleDate 结算日期
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setSettleDate(Date settleDate) {
        this.settleDate = settleDate;
    }


    public BigDecimal getTradeAmt() {
        return tradeAmt;
    }

    public void setTradeAmt(BigDecimal tradeAmt) {
        this.tradeAmt = tradeAmt;
    }


    public String getEntrustId() {
        return entrustId;
    }

    public void setEntrustId(String entrustId) {
        this.entrustId = entrustId;
    }

    public String getIsExistNew() {
        return isExistNew;
    }

    public void setIsExistNew(String isExistNew) {
        this.isExistNew = isExistNew;
    }

    public String getOffsetId() {
        return offsetId;
    }

    public void setOffsetId(String offsetId) {
        this.offsetId = offsetId;
    }

    public String getOffsetFlag() {
        return offsetFlag;
    }

    public void setOffsetFlag(String offsetFlag) {
        this.offsetFlag = offsetFlag;
    }

    public String getFinprodId() {
        return finprodId;
    }

    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
    }

    public String getPs() {
        return ps;
    }

    public void setPs(String ps) {
        this.ps = ps;
    }

    public String getCcy() {
        return ccy;
    }

    public void setCcy(String ccy) {
        this.ccy = ccy;
    }

    public BigDecimal getShareAmt() {
        return shareAmt;
    }

    public void setShareAmt(BigDecimal shareAmt) {
        this.shareAmt = shareAmt;
    }
}
