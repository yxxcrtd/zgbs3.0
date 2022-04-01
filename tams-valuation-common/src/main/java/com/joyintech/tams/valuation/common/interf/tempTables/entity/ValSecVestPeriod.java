package com.joyintech.tams.valuation.common.interf.tempTables.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.framework.json.CustomDateDeserialize;
import java.math.BigDecimal;
import java.util.Date;

public class ValSecVestPeriod extends BaseEntity {
    /**
     * 产品代码
     */
    private String prodId;

    /**
     * 资产代码
     */
    private String assetId;

    /**
     * 债券代码
     */
    private String secId;

    /**
     * 债券简称
     */
    private String secAbbr;

    /**
     * 新增、修改、删除
     */
    private String dataActionSign;

    /**
     * 交易方向
     */
    private String ps;

    /**
     * 是否还本带息
     */
    private String isAmt;

    /**
     * 实收利息
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal actInt;

    /**
     * 实收本金
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal actCapi;

    /**
     * 通道费
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal chlFeeAmt;

    /**
     * 交易金额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal tardeAmt;

    /**
     * 计息开始日
     */
    private Date vdate;

    /**
     * 计息结束日
     */
    private Date mdate;

    /**
     * 投资目的
     */
    private String invAim;

    /**
     * 单位面值
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal parValue;

    /**
     * 交割日期
     */
    private Date settleDate;

    /**
     * 交易日期
     */
    private Date tradeDate;

    /**
     * 是否结清
     */
    private String isClean2;

    /**
     * 币种
     */
    private String ccy;

    /**
     * 交易编号
     */
    private String tradeId;

    /**
     * 有无清算指令
     */
    private String isClean;

    /**
     * 成交数量
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal dealNum;

    /**
     * 通道费是否随交易支付
     */
    private String isChlFeeAmt;

    /**
     * 通道代码
     */
    private String channelId;

    /**
     * 付款账户
     */
    private String myAcctNo;

    /**
     * 收款账户
     */
    private String counterAcctNo;

    /**
     * 资金交收日
     */
    private Date fundSettleDate;

    /**
     * 计提份额
     */
    private BigDecimal accrNum;

    /*add by sunshuaishuai 20220325 交易管理 start*/
    /**
     * 证券账户
     */
    private String secManageAcctId;
    /*add by sunshuaishuai 20220325 end*/

  /**
     * 取得 产品代码
     * @return 产品代码
     */
    public String getProdId() {
        return prodId;
    }

    /**
     * 设置 产品代码
     * @param prodId 产品代码
     */
    public void setProdId(String prodId) {
        this.prodId = prodId == null ? null : prodId.trim();
    }

    /**
     * 取得 资产代码
     * @return 资产代码
     */
    public String getAssetId() {
        return assetId;
    }

    /**
     * 设置 资产代码
     * @param assetId 资产代码
     */
    public void setAssetId(String assetId) {
        this.assetId = assetId == null ? null : assetId.trim();
    }

    /**
     * 取得 债券代码
     * @return 债券代码
     */
    public String getSecId() {
        return secId;
    }

    /**
     * 设置 债券代码
     * @param secId 债券代码
     */
    public void setSecId(String secId) {
        this.secId = secId == null ? null : secId.trim();
    }

    /**
     * 取得 债券简称
     * @return 债券简称
     */
    public String getSecAbbr() {
        return secAbbr;
    }

    /**
     * 设置 债券简称
     * @param secAbbr 债券简称
     */
    public void setSecAbbr(String secAbbr) {
        this.secAbbr = secAbbr == null ? null : secAbbr.trim();
    }

    /**
     * 取得 新增、修改、删除
     * @return 新增、修改、删除
     */
    public String getDataActionSign() {
        return dataActionSign;
    }

    /**
     * 设置 新增、修改、删除
     * @param dataActionSign 新增、修改、删除
     */
    public void setDataActionSign(String dataActionSign) {
        this.dataActionSign = dataActionSign == null ? null : dataActionSign.trim();
    }

    /**
     * 取得 交易方向
     * @return 交易方向
     */
    public String getPs() {
        return ps;
    }

    /**
     * 设置 交易方向
     * @param ps 交易方向
     */
    public void setPs(String ps) {
        this.ps = ps == null ? null : ps.trim();
    }

    /**
     * 取得 是否还本带息
     * @return 是否还本带息
     */
    public String getIsAmt() {
        return isAmt;
    }

    /**
     * 设置 是否还本带息
     * @param isAmt 是否还本带息
     */
    public void setIsAmt(String isAmt) {
        this.isAmt = isAmt == null ? null : isAmt.trim();
    }

    /**
     * 取得 实收利息
     * @return 实收利息
     */
    public BigDecimal getActInt() {
        return actInt;
    }

    /**
     * 设置 实收利息
     * @param actInt 实收利息
     */
    public void setActInt(BigDecimal actInt) {
        this.actInt = actInt;
    }

    /**
     * 取得 实收本金
     * @return 实收本金
     */
    public BigDecimal getActCapi() {
        return actCapi;
    }

    /**
     * 设置 实收本金
     * @param actCapi 实收本金
     */
    public void setActCapi(BigDecimal actCapi) {
        this.actCapi = actCapi;
    }

    /**
     * 取得 通道费
     * @return 通道费
     */
    public BigDecimal getChlFeeAmt() {
        return chlFeeAmt;
    }

    /**
     * 设置 通道费
     * @param chlFeeAmt 通道费
     */
    public void setChlFeeAmt(BigDecimal chlFeeAmt) {
        this.chlFeeAmt = chlFeeAmt;
    }

    /**
     * 取得 交易金额
     * @return 交易金额
     */
    public BigDecimal getTardeAmt() {
        return tardeAmt;
    }

    /**
     * 设置 交易金额
     * @param tardeAmt 交易金额
     */
    public void setTardeAmt(BigDecimal tardeAmt) {
        this.tardeAmt = tardeAmt;
    }

    /**
     * 取得 计息开始日
     * @return 计息开始日
     */
    public Date getVdate() {
        return vdate;
    }

    /**
     * 设置 计息开始日
     * @param vdate 计息开始日
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setVdate(Date vdate) {
        this.vdate = vdate;
    }

    /**
     * 取得 计息结束日
     * @return 计息结束日
     */
    public Date getMdate() {
        return mdate;
    }

    /**
     * 设置 计息结束日
     * @param mdate 计息结束日
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setMdate(Date mdate) {
        this.mdate = mdate;
    }

    /**
     * 取得 投资目的
     * @return 投资目的
     */
    public String getInvAim() {
        return invAim;
    }

    /**
     * 设置 投资目的
     * @param invAim 投资目的
     */
    public void setInvAim(String invAim) {
        this.invAim = invAim == null ? null : invAim.trim();
    }

    /**
     * 取得 单位面值
     * @return 单位面值
     */
    public BigDecimal getParValue() {
        return parValue;
    }

    /**
     * 设置 单位面值
     * @param parValue 单位面值
     */
    public void setParValue(BigDecimal parValue) {
        this.parValue = parValue;
    }

    /**
     * 取得 交割日期
     * @return 交割日期
     */
    public Date getSettleDate() {
        return settleDate;
    }

    /**
     * 设置 交割日期
     * @param settleDate 交割日期
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setSettleDate(Date settleDate) {
        this.settleDate = settleDate;
    }

    /**
     * 取得 交易日期
     * @return 交易日期
     */
    public Date getTradeDate() {
        return tradeDate;
    }

    /**
     * 设置 交易日期
     * @param tradeDate 交易日期
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setTradeDate(Date tradeDate) {
        this.tradeDate = tradeDate;
    }

    /**
     * 取得 是否结清
     * @return 是否结清
     */
    public String getIsClean2() {
        return isClean2;
    }

    /**
     * 设置 是否结清
     * @param isClean2 是否结清
     */
    public void setIsClean2(String isClean2) {
        this.isClean2 = isClean2 == null ? null : isClean2.trim();
    }

    /**
     * 取得 币种
     * @return 币种
     */
    public String getCcy() {
        return ccy;
    }

    /**
     * 设置 币种
     * @param ccy 币种
     */
    public void setCcy(String ccy) {
        this.ccy = ccy == null ? null : ccy.trim();
    }

    /**
     * 取得 交易编号
     * @return 交易编号
     */
    public String getTradeId() {
        return tradeId;
    }

    /**
     * 设置 交易编号
     * @param tradeId 交易编号
     */
    public void setTradeId(String tradeId) {
        this.tradeId = tradeId == null ? null : tradeId.trim();
    }

    /**
     * 取得 有无清算指令
     * @return 有无清算指令
     */
    public String getIsClean() {
        return isClean;
    }

    /**
     * 设置 有无清算指令
     * @param isClean 有无清算指令
     */
    public void setIsClean(String isClean) {
        this.isClean = isClean == null ? null : isClean.trim();
    }

    /**
     * 取得 成交数量
     * @return 成交数量
     */
    public BigDecimal getDealNum() {
        return dealNum;
    }

    /**
     * 设置 成交数量
     * @param dealNum 成交数量
     */
    public void setDealNum(BigDecimal dealNum) {
        this.dealNum = dealNum;
    }

    /**
     * 取得 通道费是否随交易支付
     * @return 通道费是否随交易支付
     */
    public String getIsChlFeeAmt() {
        return isChlFeeAmt;
    }

    /**
     * 设置 通道费是否随交易支付
     * @param isChlFeeAmt 通道费是否随交易支付
     */
    public void setIsChlFeeAmt(String isChlFeeAmt) {
        this.isChlFeeAmt = isChlFeeAmt == null ? null : isChlFeeAmt.trim();
    }

    /**
     * 取得 通道代码
     * @return 通道代码
     */
    public String getChannelId() {
        return channelId;
    }

    /**
     * 设置 通道代码
     * @param channelId 通道代码
     */
    public void setChannelId(String channelId) {
        this.channelId = channelId == null ? null : channelId.trim();
    }

    /**
     * 取得 付款账户
     * @return 付款账户
     */
    public String getMyAcctNo() {
        return myAcctNo;
    }

    /**
     * 设置 付款账户
     * @param myAcctNo 付款账户
     */
    public void setMyAcctNo(String myAcctNo) {
        this.myAcctNo = myAcctNo == null ? null : myAcctNo.trim();
    }

    /**
     * 取得 收款账户
     * @return 收款账户
     */
    public String getCounterAcctNo() {
        return counterAcctNo;
    }

    /**
     * 设置 收款账户
     * @param counterAcctNo 收款账户
     */
    public void setCounterAcctNo(String counterAcctNo) {
        this.counterAcctNo = counterAcctNo == null ? null : counterAcctNo.trim();
    }

    /**
     * 取得 资金交收日
     * @return 资金交收日
     */
    public Date getFundSettleDate() {
        return fundSettleDate;
    }

    /**
     * 设置 资金交收日
     * @param fundSettleDate 资金交收日
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setFundSettleDate(Date fundSettleDate) {
        this.fundSettleDate = fundSettleDate;
    }

    public BigDecimal getAccrNum() {
        return accrNum;
    }

    public void setAccrNum(BigDecimal accrNum) {
        this.accrNum = accrNum;
    }

    /*add by sunshuaishuai 20220325 交易管理 start*/
    /**
     * 取得 证券账户
     * @return 证券账户
     */
    public String getSecManageAcctId() {
        return secManageAcctId;
    }

    /**
     * 设置 证券账户
     * @param secManageAcctId 证券账户
     */
    public void setSecManageAcctId(String secManageAcctId) {
        this.secManageAcctId = secManageAcctId;
    }
    /*add by sunshuaishuai 20220325 交易管理 end*/
}