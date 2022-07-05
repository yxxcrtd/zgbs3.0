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

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.json.BigDecimalSerializer;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 〈一句话功能简述〉 〈功能详细描述〉
 * 
 * @author meifaxin
 * @version 1.0 2022年6月17日
 * @see EtrExchangeAgreRepCurrResp
 * @since 1.0
 */
public class EtrExchangeAgreRepCurrResp extends BaseEntity {

    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    private static final long serialVersionUID = -1548012609915542103L;





    /**
     * 证券市场代码
     */
    private String finprodMarketId;
    /**
     * 资产名称
     */
    private String finpordName;

    /**
     * 回购类型
     */
    private String repType;
    /**
     * 质押数量
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal repSecNum;

    /**
     * 质押率
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal conRatio;
    /**
     * 质押金额
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal pledgeAmt;
    /**
     * 回购金额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal prin;
    /**
     * 交易对手
     */
    private String counterId;
    /**
     * 产品期限
     */
    private String term;
    /**
     * 固定利率
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal intRate;
    /**
     * 首期交付日
     */
    private Date vpayDate;
    /**
     * 到期交付日
     */
    private Date mpayDate;
    /**
     * 实际占款天数
     */
    private Long actCapDays;
    /**
     * 到期应计利息
     */
    private BigDecimal mIntAmt;
    /**
     * 到期结算金额
     */
    private BigDecimal mTradeAmt;


    public String getFinprodMarketId() {
        return finprodMarketId;
    }

    public void setFinprodMarketId(String finprodMarketId) {
        this.finprodMarketId = finprodMarketId;
    }

    public String getFinpordName() {
        return finpordName;
    }

    public void setFinpordName(String finpordName) {
        this.finpordName = finpordName;
    }

    public String getRepType() {
        return repType;
    }

    public void setRepType(String repType) {
        this.repType = repType;
    }




    public BigDecimal getPledgeAmt() {
        return pledgeAmt;
    }

    public void setPledgeAmt(BigDecimal pledgeAmt) {
        this.pledgeAmt = pledgeAmt;
    }

    public BigDecimal getPrin() {
        return prin;
    }

    public void setPrin(BigDecimal prin) {
        this.prin = prin;
    }

    public String getCounterId() {
        return counterId;
    }

    public void setCounterId(String counterId) {
        this.counterId = counterId;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public BigDecimal getIntRate() {
        return intRate;
    }

    public void setIntRate(BigDecimal intRate) {
        this.intRate = intRate;
    }

    public Date getVpayDate() {
        return vpayDate;
    }

    public void setVpayDate(Date vpayDate) {
        this.vpayDate = vpayDate;
    }

    public Date getMpayDate() {
        return mpayDate;
    }

    public void setMpayDate(Date mpayDate) {
        this.mpayDate = mpayDate;
    }

    public Long getActCapDays() {
        return actCapDays;
    }

    public void setActCapDays(Long actCapDays) {
        this.actCapDays = actCapDays;
    }

    public BigDecimal getmIntAmt() {
        return mIntAmt;
    }

    public void setmIntAmt(BigDecimal mIntAmt) {
        this.mIntAmt = mIntAmt;
    }

    public BigDecimal getmTradeAmt() {
        return mTradeAmt;
    }

    public void setmTradeAmt(BigDecimal mTradeAmt) {
        this.mTradeAmt = mTradeAmt;
    }

    public BigDecimal getRepSecNum() {
        return repSecNum;
    }

    public void setRepSecNum(BigDecimal repSecNum) {
        this.repSecNum = repSecNum;
    }

    public BigDecimal getConRatio() {
        return conRatio;
    }

    public void setConRatio(BigDecimal conRatio) {
        this.conRatio = conRatio;
    }
}
