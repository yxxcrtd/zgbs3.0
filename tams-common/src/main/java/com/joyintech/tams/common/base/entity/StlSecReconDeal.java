package com.joyintech.tams.common.base.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.framework.json.CustomDateDeserialize;
import java.math.BigDecimal;
import java.util.Date;

public class StlSecReconDeal extends BaseEntity {
    /**
     * 券商对账单流水号
     */
    private String seqNo;

    /**
     * 成交编号
     */
    private String tradeId;

    /**
     * 委托编号
     */
    private String instructionId;

    /**
     * 券商代码
     */
    private String customerId;

    /**
     * 股东代码
     */
    private String tradeNo;

    /**
     * 营业部代码
     */
    private String saleDepid;

    /**
     * 席位号
     */
    private String seatNumber;

    /**
     * 组合代码
     */
    private String portfolioId;

    /**
     * 金融产品代码
     */
    private String finprodId;

    /**
     * 金融产品类型
     */
    private String finprodType;

    /**
     * 市场代码
     */
    private String marketId;

    /**
     * 金融产品名称
     */
    private String finprodName;

    /**
     * 交易场所
     */
    private String tradeMarket;

    /**
     * 交易平台
     */
    private String tradePlat;

    /**
     * 交易日期
     */
    private Date tradeDate;

    /**
     * 交割日期
     */
    private Date settleDate;

    /**
     * 资金账户
     */
    private String accout;

    /**
     * 证券账户
     */
    private String secAcctId;

    /**
     * 交易方向
     */
    private String ps;

    /**
     * 交收方式
     */
    private String settlemenType;

    /**
     * 委托日期
     */
    private Date orderDate;

    /**
     * 交易币种
     */
    private String ccy;

    /**
     * 成交数量
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal tradeNum;

    /**
     * 成交金额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal tradeAmt;

    /**
     * 清算金额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal happenAmt;

    /**
     * 成交净价
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal price;

    /**
     * 成交净价
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal fullPrice;

    /**
     * 单位应计利息
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal unit;

    /**
     * 利息金额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal intAmt;

    /**
     * 到期结算金额，只有回购有
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal hgdqje;

    /**
     * 回购到期日
     */
    private Date hgdqr;

    /**
     * 印花税
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal stampFee;

    /**
     * 经手费
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal jsfAmt;

    /**
     * 过户费
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal ghfAmt;

    /**
     * 证管费
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal zgfAmt;

    /**
     * 手续费
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal sxfAmt;

    /**
     * 结算费
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal settleFee;

    /**
     * 交易规费
对账状态
对账状态
对账状态
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal tradeFee;

    /**
     * 佣金
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal commissionAmt;

    /**
     * 净佣金
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal netCommissionAmt;

    /**
     * 一级总费用
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal totalTeeamt;

    /**
     * 回购类型
     */
    private String repoType;

    public String getRepoType() {
        return repoType;
    }

    public void setRepoType(String repoType) {
        this.repoType = repoType == null ? null : repoType.trim();
    }

    /**
     * 取得 券商对账单流水号
     * @return 券商对账单流水号
     */
    public String getSeqNo() {
        return seqNo;
    }

    /**
     * 设置 券商对账单流水号
     * @param seqNo 券商对账单流水号
     */
    public void setSeqNo(String seqNo) {
        this.seqNo = seqNo == null ? null : seqNo.trim();
    }

    /**
     * 取得 成交编号
     * @return 成交编号
     */
    public String getTradeId() {
        return tradeId;
    }

    /**
     * 设置 成交编号
     * @param tradeId 成交编号
     */
    public void setTradeId(String tradeId) {
        this.tradeId = tradeId == null ? null : tradeId.trim();
    }

    /**
     * 取得 委托编号
     * @return 委托编号
     */
    public String getInstructionId() {
        return instructionId;
    }

    /**
     * 设置 委托编号
     * @param instructionId 委托编号
     */
    public void setInstructionId(String instructionId) {
        this.instructionId = instructionId == null ? null : instructionId.trim();
    }

    /**
     * 取得 券商代码
     * @return 券商代码
     */
    public String getCustomerId() {
        return customerId;
    }

    /**
     * 设置 券商代码
     * @param customerId 券商代码
     */
    public void setCustomerId(String customerId) {
        this.customerId = customerId == null ? null : customerId.trim();
    }

    /**
     * 取得 股东代码
     * @return 股东代码
     */
    public String getTradeNo() {
        return tradeNo;
    }

    /**
     * 设置 股东代码
     * @param tradeNo 股东代码
     */
    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo == null ? null : tradeNo.trim();
    }

    /**
     * 取得 营业部代码
     * @return 营业部代码
     */
    public String getSaleDepid() {
        return saleDepid;
    }

    /**
     * 设置 营业部代码
     * @param saleDepid 营业部代码
     */
    public void setSaleDepid(String saleDepid) {
        this.saleDepid = saleDepid == null ? null : saleDepid.trim();
    }

    /**
     * 取得 席位号
     * @return 席位号
     */
    public String getSeatNumber() {
        return seatNumber;
    }

    /**
     * 设置 席位号
     * @param seatNumber 席位号
     */
    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber == null ? null : seatNumber.trim();
    }

    /**
     * 取得 组合代码
     * @return 组合代码
     */
    public String getPortfolioId() {
        return portfolioId;
    }

    /**
     * 设置 组合代码
     * @param portfolioId 组合代码
     */
    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId == null ? null : portfolioId.trim();
    }

    /**
     * 取得 金融产品代码
     * @return 金融产品代码
     */
    public String getFinprodId() {
        return finprodId;
    }

    /**
     * 设置 金融产品代码
     * @param finprodId 金融产品代码
     */
    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId == null ? null : finprodId.trim();
    }

    /**
     * 取得 金融产品类型
     * @return 金融产品类型
     */
    public String getFinprodType() {
        return finprodType;
    }

    /**
     * 设置 金融产品类型
     * @param finprodType 金融产品类型
     */
    public void setFinprodType(String finprodType) {
        this.finprodType = finprodType == null ? null : finprodType.trim();
    }

    /**
     * 取得 市场代码
     * @return 市场代码
     */
    public String getMarketId() {
        return marketId;
    }

    /**
     * 设置 市场代码
     * @param marketId 市场代码
     */
    public void setMarketId(String marketId) {
        this.marketId = marketId == null ? null : marketId.trim();
    }

    /**
     * 取得 金融产品名称
     * @return 金融产品名称
     */
    public String getFinprodName() {
        return finprodName;
    }

    /**
     * 设置 金融产品名称
     * @param finprodName 金融产品名称
     */
    public void setFinprodName(String finprodName) {
        this.finprodName = finprodName == null ? null : finprodName.trim();
    }

    /**
     * 取得 交易场所
     * @return 交易场所
     */
    public String getTradeMarket() {
        return tradeMarket;
    }

    /**
     * 设置 交易场所
     * @param tradeMarket 交易场所
     */
    public void setTradeMarket(String tradeMarket) {
        this.tradeMarket = tradeMarket == null ? null : tradeMarket.trim();
    }

    /**
     * 取得 交易平台
     * @return 交易平台
     */
    public String getTradePlat() {
        return tradePlat;
    }

    /**
     * 设置 交易平台
     * @param tradePlat 交易平台
     */
    public void setTradePlat(String tradePlat) {
        this.tradePlat = tradePlat == null ? null : tradePlat.trim();
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
     * 取得 资金账户
     * @return 资金账户
     */
    public String getAccout() {
        return accout;
    }

    /**
     * 设置 资金账户
     * @param accout 资金账户
     */
    public void setAccout(String accout) {
        this.accout = accout == null ? null : accout.trim();
    }

    /**
     * 取得 证券账户
     * @return 证券账户
     */
    public String getSecAcctId() {
        return secAcctId;
    }

    /**
     * 设置 证券账户
     * @param secAcctId 证券账户
     */
    public void setSecAcctId(String secAcctId) {
        this.secAcctId = secAcctId == null ? null : secAcctId.trim();
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
     * 取得 交收方式
     * @return 交收方式
     */
    public String getSettlemenType() {
        return settlemenType;
    }

    /**
     * 设置 交收方式
     * @param settlemenType 交收方式
     */
    public void setSettlemenType(String settlemenType) {
        this.settlemenType = settlemenType == null ? null : settlemenType.trim();
    }

    /**
     * 取得 委托日期
     * @return 委托日期
     */
    public Date getOrderDate() {
        return orderDate;
    }

    /**
     * 设置 委托日期
     * @param orderDate 委托日期
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    /**
     * 取得 交易币种
     * @return 交易币种
     */
    public String getCcy() {
        return ccy;
    }

    /**
     * 设置 交易币种
     * @param ccy 交易币种
     */
    public void setCcy(String ccy) {
        this.ccy = ccy == null ? null : ccy.trim();
    }

    /**
     * 取得 成交数量
     * @return 成交数量
     */
    public BigDecimal getTradeNum() {
        return tradeNum;
    }

    /**
     * 设置 成交数量
     * @param tradeNum 成交数量
     */
    public void setTradeNum(BigDecimal tradeNum) {
        this.tradeNum = tradeNum;
    }

    /**
     * 取得 成交金额
     * @return 成交金额
     */
    public BigDecimal getTradeAmt() {
        return tradeAmt;
    }

    /**
     * 设置 成交金额
     * @param tradeAmt 成交金额
     */
    public void setTradeAmt(BigDecimal tradeAmt) {
        this.tradeAmt = tradeAmt;
    }

    /**
     * 取得 清算金额
     * @return 清算金额
     */
    public BigDecimal getHappenAmt() {
        return happenAmt;
    }

    /**
     * 设置 清算金额
     * @param happenAmt 清算金额
     */
    public void setHappenAmt(BigDecimal happenAmt) {
        this.happenAmt = happenAmt;
    }

    /**
     * 取得 成交净价
     * @return 成交净价
     */
    public BigDecimal getPrice() {
        return price;
    }

    /**
     * 设置 成交净价
     * @param price 成交净价
     */
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    /**
     * 取得 成交净价
     * @return 成交净价
     */
    public BigDecimal getFullPrice() {
        return fullPrice;
    }

    /**
     * 设置 成交净价
     * @param fullPrice 成交净价
     */
    public void setFullPrice(BigDecimal fullPrice) {
        this.fullPrice = fullPrice;
    }

    /**
     * 取得 单位应计利息
     * @return 单位应计利息
     */
    public BigDecimal getUnit() {
        return unit;
    }

    /**
     * 设置 单位应计利息
     * @param unit 单位应计利息
     */
    public void setUnit(BigDecimal unit) {
        this.unit = unit;
    }

    /**
     * 取得 利息金额
     * @return 利息金额
     */
    public BigDecimal getIntAmt() {
        return intAmt;
    }

    /**
     * 设置 利息金额
     * @param intAmt 利息金额
     */
    public void setIntAmt(BigDecimal intAmt) {
        this.intAmt = intAmt;
    }

    /**
     * 取得 到期结算金额，只有回购有
     * @return 到期结算金额，只有回购有
     */
    public BigDecimal getHgdqje() {
        return hgdqje;
    }

    /**
     * 设置 到期结算金额，只有回购有
     * @param hgdqje 到期结算金额，只有回购有
     */
    public void setHgdqje(BigDecimal hgdqje) {
        this.hgdqje = hgdqje;
    }

    /**
     * 取得 回购到期日
     * @return 回购到期日
     */
    public Date getHgdqr() {
        return hgdqr;
    }

    /**
     * 设置 回购到期日
     * @param hgdqr 回购到期日
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setHgdqr(Date hgdqr) {
        this.hgdqr = hgdqr;
    }

    /**
     * 取得 印花税
     * @return 印花税
     */
    public BigDecimal getStampFee() {
        return stampFee;
    }

    /**
     * 设置 印花税
     * @param stampFee 印花税
     */
    public void setStampFee(BigDecimal stampFee) {
        this.stampFee = stampFee;
    }

    /**
     * 取得 经手费
     * @return 经手费
     */
    public BigDecimal getJsfAmt() {
        return jsfAmt;
    }

    /**
     * 设置 经手费
     * @param jsfAmt 经手费
     */
    public void setJsfAmt(BigDecimal jsfAmt) {
        this.jsfAmt = jsfAmt;
    }

    /**
     * 取得 过户费
     * @return 过户费
     */
    public BigDecimal getGhfAmt() {
        return ghfAmt;
    }

    /**
     * 设置 过户费
     * @param ghfAmt 过户费
     */
    public void setGhfAmt(BigDecimal ghfAmt) {
        this.ghfAmt = ghfAmt;
    }

    /**
     * 取得 证管费
     * @return 证管费
     */
    public BigDecimal getZgfAmt() {
        return zgfAmt;
    }

    /**
     * 设置 证管费
     * @param zgfAmt 证管费
     */
    public void setZgfAmt(BigDecimal zgfAmt) {
        this.zgfAmt = zgfAmt;
    }

    /**
     * 取得 手续费
     * @return 手续费
     */
    public BigDecimal getSxfAmt() {
        return sxfAmt;
    }

    /**
     * 设置 手续费
     * @param sxfAmt 手续费
     */
    public void setSxfAmt(BigDecimal sxfAmt) {
        this.sxfAmt = sxfAmt;
    }

    /**
     * 取得 结算费
     * @return 结算费
     */
    public BigDecimal getSettleFee() {
        return settleFee;
    }

    /**
     * 设置 结算费
     * @param settleFee 结算费
     */
    public void setSettleFee(BigDecimal settleFee) {
        this.settleFee = settleFee;
    }

    /**
     * 取得 交易规费
对账状态
对账状态
对账状态
     * @return 交易规费
对账状态
对账状态
对账状态
     */
    public BigDecimal getTradeFee() {
        return tradeFee;
    }

    /**
     * 设置 交易规费
对账状态
对账状态
对账状态
     * @param tradeFee 交易规费
对账状态
对账状态
对账状态
     */
    public void setTradeFee(BigDecimal tradeFee) {
        this.tradeFee = tradeFee;
    }

    /**
     * 取得 佣金
     * @return 佣金
     */
    public BigDecimal getCommissionAmt() {
        return commissionAmt;
    }

    /**
     * 设置 佣金
     * @param commissionAmt 佣金
     */
    public void setCommissionAmt(BigDecimal commissionAmt) {
        this.commissionAmt = commissionAmt;
    }

    /**
     * 取得 净佣金
     * @return 净佣金
     */
    public BigDecimal getNetCommissionAmt() {
        return netCommissionAmt;
    }

    /**
     * 设置 净佣金
     * @param netCommissionAmt 净佣金
     */
    public void setNetCommissionAmt(BigDecimal netCommissionAmt) {
        this.netCommissionAmt = netCommissionAmt;
    }

    /**
     * 取得 一级总费用
     * @return 一级总费用
     */
    public BigDecimal getTotalTeeamt() {
        return totalTeeamt;
    }

    /**
     * 设置 一级总费用
     * @param totalTeeamt 一级总费用
     */
    public void setTotalTeeamt(BigDecimal totalTeeamt) {
        this.totalTeeamt = totalTeeamt;
    }
}