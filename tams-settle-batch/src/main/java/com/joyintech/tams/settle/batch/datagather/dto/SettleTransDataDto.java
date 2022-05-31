package com.joyintech.tams.settle.batch.datagather.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.SettleTransData;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author 何阳阳
 * @date 2020/7/17
 * @see SettleTransDataDto
 * @since 1.0
 */
public class SettleTransDataDto extends SettleTransData {
    /**
     * 指令编号
     */
    private String orderId;

    /**
     * 投资目的
     */
    private String invAim;

    /**
     * 回购类型
     */
    private String repoType;

    /**
     * 投管交易编号
     */
    private String tradeIdTg;

    /**
     * 委托代码
     */
    private String entrustId;

    /**
     * 付息截止日
     */
    private Date mdate2;

    /**
     * 持仓份额(回购交易表数据)
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal shareNum;

    /**
     * 明细流水号  处理成交数据与投资关系
     */
    private List<String>  tradeIdList;

    /**
     * 金融产品交易编号
     */
    private String tradeId2;

    private List<TrdProductDealDto> trdProductDealList;

    /**
     * 权益登记日
     */
    private Date regDate;

    /**
     * 除权除息日
     */
    private Date exDevidentDate;

    /**
     * 前置交易编号
     */
    private String bTradeId;

    /**
     * 持仓数量
     */
    private BigDecimal amount;

    /**
     * 申购交易份额
     */
    private BigDecimal shareAmtSg;

    //是否货币基金
    private String isHuobi;

    public String getIsHuobi() {
        return isHuobi;
    }

    public void setIsHuobi(String isHuobi) {
        this.isHuobi = isHuobi;
    }

    /**
     * 本金现金流代码
     */
    private String prinCashId;

    public String getPrinCashId() {
        return prinCashId;
    }

    public void setPrinCashId(String prinCashId) {
        this.prinCashId = prinCashId;
    }

    /**
     * 付息现金流代码
     */
    private String intPayCashId;

    public String getIntPayCashId() {
        return intPayCashId;
    }

    public void setIntPayCashId(String intPayCashId) {
        this.intPayCashId = intPayCashId;
    }

    public BigDecimal getShareAmtSg() {
        return shareAmtSg;
    }

    public void setShareAmtSg(BigDecimal shareAmtSg) {
        this.shareAmtSg = shareAmtSg;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getbTradeId() {
        return bTradeId;
    }

    public void setbTradeId(String bTradeId) {
        this.bTradeId = bTradeId;
    }

    public Date getRegDate() {
        return regDate;
    }

    public void setRegDate(Date regDate) {
        this.regDate = regDate;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getInvAim() {
        return invAim;
    }

    public void setInvAim(String invAim) {
        this.invAim = invAim;
    }

    public String getRepoType() {
        return repoType;
    }

    public void setRepoType(String repoType) {
        this.repoType = repoType;
    }

    public String getTradeIdTg() {
        return tradeIdTg;
    }

    public void setTradeIdTg(String tradeIdTg) {
        this.tradeIdTg = tradeIdTg;
    }

    public String getEntrustId() {
        return entrustId;
    }

    public void setEntrustId(String entrustId) {
        this.entrustId = entrustId;
    }

    public Date getMdate2() {
        return mdate2;
    }

    public void setMdate2(Date mdate2) {
        this.mdate2 = mdate2;
    }

    public List<String> getTradeIdList() {
        return tradeIdList;
    }

    public void setTradeIdList(List<String> tradeIdList) {
        this.tradeIdList = tradeIdList;
    }

    public List<TrdProductDealDto> getTrdProductDealList() {
        return trdProductDealList;
    }

    public void setTrdProductDealList(List<TrdProductDealDto> trdProductDealList) {
        this.trdProductDealList = trdProductDealList;
    }

    public BigDecimal getShareNum() {
        return shareNum;
    }

    public void setShareNum(BigDecimal shareNum) {
        this.shareNum = shareNum;
    }

    public String getTradeId2() {
        return tradeId2;
    }

    public void setTradeId2(String tradeId2) {
        this.tradeId2 = tradeId2;
    }

    public String getBusiKey(){
        return StringUtil.joinString(Arrays.asList(getFinprodId(), DateUtil.format(getMdate()),getSecAcctId(),getSeatNumber(),getAcctNo()),"_");
    }

    private String purchModle;

    private String purchType;

    private Date purchDate;

    private String purchCounter;

    private BigDecimal purchAmt;

    private String tradeTotalId;

    public String getPurchModle() {
        return purchModle;
    }

    public void setPurchModle(String purchModle) {
        this.purchModle = purchModle;
    }

    public String getPurchType() {
        return purchType;
    }

    public void setPurchType(String purchType) {
        this.purchType = purchType;
    }

    public Date getPurchDate() {
        return purchDate;
    }

    public void setPurchDate(Date purchDate) {
        this.purchDate = purchDate;
    }

    public String getPurchCounter() {
        return purchCounter;
    }

    public void setPurchCounter(String purchCounter) {
        this.purchCounter = purchCounter;
    }

    public BigDecimal getPurchAmt() {
        return purchAmt;
    }

    public void setPurchAmt(BigDecimal purchAmt) {
        this.purchAmt = purchAmt;
    }

    public String getTradeTotalId() {
        return tradeTotalId;
    }

    public void setTradeTotalId(String tradeTotalId) {
        this.tradeTotalId = tradeTotalId;
    }

    public Date getExDevidentDate() {
        return exDevidentDate;
    }

    public void setExDevidentDate(Date exDevidentDate) {
        this.exDevidentDate = exDevidentDate;
    }
}
