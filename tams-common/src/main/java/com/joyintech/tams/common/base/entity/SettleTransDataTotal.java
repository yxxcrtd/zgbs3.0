package com.joyintech.tams.common.base.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.framework.json.CustomDateDeserialize;
import java.math.BigDecimal;
import java.util.Date;

public class SettleTransDataTotal extends BaseEntity {
    /**
     * 流水号
     */
    private String seqId;

    /**
     * 执行日期
     */
    private Date dealDate;

    /**
     * 证券品种
     */
    private String secType;

    /**
     * 申请编号
     */
    private String applyTradeId;

    /**
     * 金融产品代码
     */
    private String finprodId;

    /**
     * 金融产品类型(估值核算)
     */
    private String finprodType;

    /**
     * 金融产品类型(投管分类)
     */
    private String finprodType2;

    /**
     * 分支序号（默认为0）
     */
    private Long branch;

    /**
     * 业务类型
     */
    private String busiType;

    /**
     * 交易日期
     */
    private Date tradeDate;

    /**
     * 交割日期
     */
    private Date settleDate;

    /**
     * 起息日
     */
    private Date vdate;

    /**
     * 到期日
     */
    private Date mdate;

    /**
     * 首期交付日
     */
    private Date vpayDate;

    /**
     * 到期交付日
     */
    private Date mpayDate;

    /**
     * 期限品种
     */
    private String termType;

    /**
     * 清算速度
     */
    private Long settleSpeed;

    /**
     * 交易币种
     */
    private String ccy;

    /**
     * 单位净价
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal unitCprice;

    /**
     * 单位利息
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal unitInt;

    /**
     * 单位全价
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal unitFprice;

    /**
     * 单位面值，债券100或者当前面值(还本前)，其他资产1
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal parValue;

    /**
     * 净价总额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal cpriceAmt;

    /**
     * 交易份额，债券数量等
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal shareAmt;

    /**
     * 交易本金，交易成本等
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal prinAmt;

    /**
     * 利息总额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal intAmt;

    /**
     * 全价总额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal fpriceAmt;

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
     * 佣金
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal commissionAmt;

    /**
     * 佣金是否随交易支付
     */
    private String isPayWithTrade;

    /**
     * 交易金额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal tradeAmt;

    /**
     * 交易方向，买入、卖出、收息、还本、申购、赎回等
     */
    private String ps;

    /**
     * 交易场所，银行间、上交所、深交所、柜面等
     */
    private String tradeMarket;

    /**
     * 交易市场，一级市场、二级市场
     */
    private String tradeMarketMode;

    /**
     * 交易平台，银行间、上交所固定收益平台、上交所大宗交易平台、深交所大宗平台、深交所综合协议交易平台、其他等
     */
    private String tradePlat;

    /**
     * 押品只数
     */
    private Long pledgeSecNum;

    /**
     * 席位号
     */
    private String seatNumber;

    /**
     * 组合代码
     */
    private String portfolioId;

    /**
     * 证券账户
     */
    private String secAcctId;

    /**
     * 资金账户
     */
    private String acctNo;

    /**
     * 批次号
     */
    private String batchNumber;

    /**
     * 到期本金
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal mPrinAmt;

    /**
     * 到期利息
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal mIntAmt;

    /**
     * 到期金额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal mTradeAmt;

    /**
     * 利率
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal intRate;

    /**
     * 计息基础
     */
    private String basis;

    /**
     * 业务场景分类
     */
    private String busiSceneCalss;

    /**
     * 交收方式
     */
    private String settleMode;

    /**
     * 交收结果
     */
    private String deliveryResult;

    /**
     * 印花税_原始
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal stampFeeRaw;

    /**
     * 经手费_原始
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal jsfAmtRaw;

    /**
     * 过户费_原始
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal ghfAmtRaw;

    /**
     * 证管费_原始
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal zgfAmtRaw;

    /**
     * 手续费_原始
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal sxfAmtRaw;

    /**
     * 结算费_原始
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal settleFeeRaw;

    /**
     * 佣金_原始
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal commissionAmtRaw;

    /**
     * 交易金额_原始
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal tradeAmtRaw;

    /**
     * 成交编号_原始
     */
    private String tradeIdRaw;

    /**
     * 临时表流水号
     */
    private String seqNo;

    /**
     * 是否优先配售
     */
    private String isPurchTypeYx;

    /**
     * 首期成交编号
     */
    private String fCjbh;

    /**
     * 取得 流水号
     * @return 流水号
     */
    public String getSeqId() {
        return seqId;
    }

    /**
     * 设置 流水号
     * @param seqId 流水号
     */
    public void setSeqId(String seqId) {
        this.seqId = seqId == null ? null : seqId.trim();
    }

    /**
     * 取得 执行日期
     * @return 执行日期
     */
    public Date getDealDate() {
        return dealDate;
    }

    /**
     * 设置 执行日期
     * @param dealDate 执行日期
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setDealDate(Date dealDate) {
        this.dealDate = dealDate;
    }

    /**
     * 取得 证券品种
     * @return 证券品种
     */
    public String getSecType() {
        return secType;
    }

    /**
     * 设置 证券品种
     * @param secType 证券品种
     */
    public void setSecType(String secType) {
        this.secType = secType == null ? null : secType.trim();
    }

    /**
     * 取得 申请编号
     * @return 申请编号
     */
    public String getApplyTradeId() {
        return applyTradeId;
    }

    /**
     * 设置 申请编号
     * @param applyTradeId 申请编号
     */
    public void setApplyTradeId(String applyTradeId) {
        this.applyTradeId = applyTradeId == null ? null : applyTradeId.trim();
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
     * 取得 金融产品类型(估值核算)
     * @return 金融产品类型(估值核算)
     */
    public String getFinprodType() {
        return finprodType;
    }

    /**
     * 设置 金融产品类型(估值核算)
     * @param finprodType 金融产品类型(估值核算)
     */
    public void setFinprodType(String finprodType) {
        this.finprodType = finprodType == null ? null : finprodType.trim();
    }

    /**
     * 取得 金融产品类型(投管分类)
     * @return 金融产品类型(投管分类)
     */
    public String getFinprodType2() {
        return finprodType2;
    }

    /**
     * 设置 金融产品类型(投管分类)
     * @param finprodType2 金融产品类型(投管分类)
     */
    public void setFinprodType2(String finprodType2) {
        this.finprodType2 = finprodType2 == null ? null : finprodType2.trim();
    }

    /**
     * 取得 分支序号（默认为0）
     * @return 分支序号（默认为0）
     */
    public Long getBranch() {
        return branch;
    }

    /**
     * 设置 分支序号（默认为0）
     * @param branch 分支序号（默认为0）
     */
    public void setBranch(Long branch) {
        this.branch = branch;
    }

    /**
     * 取得 业务类型
     * @return 业务类型
     */
    public String getBusiType() {
        return busiType;
    }

    /**
     * 设置 业务类型
     * @param busiType 业务类型
     */
    public void setBusiType(String busiType) {
        this.busiType = busiType == null ? null : busiType.trim();
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
     * 取得 起息日
     * @return 起息日
     */
    public Date getVdate() {
        return vdate;
    }

    /**
     * 设置 起息日
     * @param vdate 起息日
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setVdate(Date vdate) {
        this.vdate = vdate;
    }

    /**
     * 取得 到期日
     * @return 到期日
     */
    public Date getMdate() {
        return mdate;
    }

    /**
     * 设置 到期日
     * @param mdate 到期日
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setMdate(Date mdate) {
        this.mdate = mdate;
    }

    /**
     * 取得 首期交付日
     * @return 首期交付日
     */
    public Date getVpayDate() {
        return vpayDate;
    }

    /**
     * 设置 首期交付日
     * @param vpayDate 首期交付日
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setVpayDate(Date vpayDate) {
        this.vpayDate = vpayDate;
    }

    /**
     * 取得 到期交付日
     * @return 到期交付日
     */
    public Date getMpayDate() {
        return mpayDate;
    }

    /**
     * 设置 到期交付日
     * @param mpayDate 到期交付日
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setMpayDate(Date mpayDate) {
        this.mpayDate = mpayDate;
    }

    /**
     * 取得 期限品种
     * @return 期限品种
     */
    public String getTermType() {
        return termType;
    }

    /**
     * 设置 期限品种
     * @param termType 期限品种
     */
    public void setTermType(String termType) {
        this.termType = termType == null ? null : termType.trim();
    }

    /**
     * 取得 清算速度
     * @return 清算速度
     */
    public Long getSettleSpeed() {
        return settleSpeed;
    }

    /**
     * 设置 清算速度
     * @param settleSpeed 清算速度
     */
    public void setSettleSpeed(Long settleSpeed) {
        this.settleSpeed = settleSpeed;
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
     * 取得 单位净价
     * @return 单位净价
     */
    public BigDecimal getUnitCprice() {
        return unitCprice;
    }

    /**
     * 设置 单位净价
     * @param unitCprice 单位净价
     */
    public void setUnitCprice(BigDecimal unitCprice) {
        this.unitCprice = unitCprice;
    }

    /**
     * 取得 单位利息
     * @return 单位利息
     */
    public BigDecimal getUnitInt() {
        return unitInt;
    }

    /**
     * 设置 单位利息
     * @param unitInt 单位利息
     */
    public void setUnitInt(BigDecimal unitInt) {
        this.unitInt = unitInt;
    }

    /**
     * 取得 单位全价
     * @return 单位全价
     */
    public BigDecimal getUnitFprice() {
        return unitFprice;
    }

    /**
     * 设置 单位全价
     * @param unitFprice 单位全价
     */
    public void setUnitFprice(BigDecimal unitFprice) {
        this.unitFprice = unitFprice;
    }

    /**
     * 取得 单位面值，债券100或者当前面值(还本前)，其他资产1
     * @return 单位面值，债券100或者当前面值(还本前)，其他资产1
     */
    public BigDecimal getParValue() {
        return parValue;
    }

    /**
     * 设置 单位面值，债券100或者当前面值(还本前)，其他资产1
     * @param parValue 单位面值，债券100或者当前面值(还本前)，其他资产1
     */
    public void setParValue(BigDecimal parValue) {
        this.parValue = parValue;
    }

    /**
     * 取得 净价总额
     * @return 净价总额
     */
    public BigDecimal getCpriceAmt() {
        return cpriceAmt;
    }

    /**
     * 设置 净价总额
     * @param cpriceAmt 净价总额
     */
    public void setCpriceAmt(BigDecimal cpriceAmt) {
        this.cpriceAmt = cpriceAmt;
    }

    /**
     * 取得 交易份额，债券数量等
     * @return 交易份额，债券数量等
     */
    public BigDecimal getShareAmt() {
        return shareAmt;
    }

    /**
     * 设置 交易份额，债券数量等
     * @param shareAmt 交易份额，债券数量等
     */
    public void setShareAmt(BigDecimal shareAmt) {
        this.shareAmt = shareAmt;
    }

    /**
     * 取得 交易本金，交易成本等
     * @return 交易本金，交易成本等
     */
    public BigDecimal getPrinAmt() {
        return prinAmt;
    }

    /**
     * 设置 交易本金，交易成本等
     * @param prinAmt 交易本金，交易成本等
     */
    public void setPrinAmt(BigDecimal prinAmt) {
        this.prinAmt = prinAmt;
    }

    /**
     * 取得 利息总额
     * @return 利息总额
     */
    public BigDecimal getIntAmt() {
        return intAmt;
    }

    /**
     * 设置 利息总额
     * @param intAmt 利息总额
     */
    public void setIntAmt(BigDecimal intAmt) {
        this.intAmt = intAmt;
    }

    /**
     * 取得 全价总额
     * @return 全价总额
     */
    public BigDecimal getFpriceAmt() {
        return fpriceAmt;
    }

    /**
     * 设置 全价总额
     * @param fpriceAmt 全价总额
     */
    public void setFpriceAmt(BigDecimal fpriceAmt) {
        this.fpriceAmt = fpriceAmt;
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
     * 取得 佣金是否随交易支付
     * @return 佣金是否随交易支付
     */
    public String getIsPayWithTrade() {
        return isPayWithTrade;
    }

    /**
     * 设置 佣金是否随交易支付
     * @param isPayWithTrade 佣金是否随交易支付
     */
    public void setIsPayWithTrade(String isPayWithTrade) {
        this.isPayWithTrade = isPayWithTrade == null ? null : isPayWithTrade.trim();
    }

    /**
     * 取得 交易金额
     * @return 交易金额
     */
    public BigDecimal getTradeAmt() {
        return tradeAmt;
    }

    /**
     * 设置 交易金额
     * @param tradeAmt 交易金额
     */
    public void setTradeAmt(BigDecimal tradeAmt) {
        this.tradeAmt = tradeAmt;
    }

    /**
     * 取得 交易方向，买入、卖出、收息、还本、申购、赎回等
     * @return 交易方向，买入、卖出、收息、还本、申购、赎回等
     */
    public String getPs() {
        return ps;
    }

    /**
     * 设置 交易方向，买入、卖出、收息、还本、申购、赎回等
     * @param ps 交易方向，买入、卖出、收息、还本、申购、赎回等
     */
    public void setPs(String ps) {
        this.ps = ps == null ? null : ps.trim();
    }

    /**
     * 取得 交易场所，银行间、上交所、深交所、柜面等
     * @return 交易场所，银行间、上交所、深交所、柜面等
     */
    public String getTradeMarket() {
        return tradeMarket;
    }

    /**
     * 设置 交易场所，银行间、上交所、深交所、柜面等
     * @param tradeMarket 交易场所，银行间、上交所、深交所、柜面等
     */
    public void setTradeMarket(String tradeMarket) {
        this.tradeMarket = tradeMarket == null ? null : tradeMarket.trim();
    }

    /**
     * 取得 交易市场，一级市场、二级市场
     * @return 交易市场，一级市场、二级市场
     */
    public String getTradeMarketMode() {
        return tradeMarketMode;
    }

    /**
     * 设置 交易市场，一级市场、二级市场
     * @param tradeMarketMode 交易市场，一级市场、二级市场
     */
    public void setTradeMarketMode(String tradeMarketMode) {
        this.tradeMarketMode = tradeMarketMode == null ? null : tradeMarketMode.trim();
    }

    /**
     * 取得 交易平台，银行间、上交所固定收益平台、上交所大宗交易平台、深交所大宗平台、深交所综合协议交易平台、其他等
     * @return 交易平台，银行间、上交所固定收益平台、上交所大宗交易平台、深交所大宗平台、深交所综合协议交易平台、其他等
     */
    public String getTradePlat() {
        return tradePlat;
    }

    /**
     * 设置 交易平台，银行间、上交所固定收益平台、上交所大宗交易平台、深交所大宗平台、深交所综合协议交易平台、其他等
     * @param tradePlat 交易平台，银行间、上交所固定收益平台、上交所大宗交易平台、深交所大宗平台、深交所综合协议交易平台、其他等
     */
    public void setTradePlat(String tradePlat) {
        this.tradePlat = tradePlat == null ? null : tradePlat.trim();
    }

    /**
     * 取得 押品只数
     * @return 押品只数
     */
    public Long getPledgeSecNum() {
        return pledgeSecNum;
    }

    /**
     * 设置 押品只数
     * @param pledgeSecNum 押品只数
     */
    public void setPledgeSecNum(Long pledgeSecNum) {
        this.pledgeSecNum = pledgeSecNum;
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
     * 取得 资金账户
     * @return 资金账户
     */
    public String getAcctNo() {
        return acctNo;
    }

    /**
     * 设置 资金账户
     * @param acctNo 资金账户
     */
    public void setAcctNo(String acctNo) {
        this.acctNo = acctNo == null ? null : acctNo.trim();
    }

    /**
     * 取得 批次号
     * @return 批次号
     */
    public String getBatchNumber() {
        return batchNumber;
    }

    /**
     * 设置 批次号
     * @param batchNumber 批次号
     */
    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber == null ? null : batchNumber.trim();
    }

    /**
     * 取得 到期本金
     * @return 到期本金
     */
    public BigDecimal getmPrinAmt() {
        return mPrinAmt;
    }

    /**
     * 设置 到期本金
     * @param mPrinAmt 到期本金
     */
    public void setmPrinAmt(BigDecimal mPrinAmt) {
        this.mPrinAmt = mPrinAmt;
    }

    /**
     * 取得 到期利息
     * @return 到期利息
     */
    public BigDecimal getmIntAmt() {
        return mIntAmt;
    }

    /**
     * 设置 到期利息
     * @param mIntAmt 到期利息
     */
    public void setmIntAmt(BigDecimal mIntAmt) {
        this.mIntAmt = mIntAmt;
    }

    /**
     * 取得 到期金额
     * @return 到期金额
     */
    public BigDecimal getmTradeAmt() {
        return mTradeAmt;
    }

    /**
     * 设置 到期金额
     * @param mTradeAmt 到期金额
     */
    public void setmTradeAmt(BigDecimal mTradeAmt) {
        this.mTradeAmt = mTradeAmt;
    }

    /**
     * 取得 利率
     * @return 利率
     */
    public BigDecimal getIntRate() {
        return intRate;
    }

    /**
     * 设置 利率
     * @param intRate 利率
     */
    public void setIntRate(BigDecimal intRate) {
        this.intRate = intRate;
    }

    /**
     * 取得 计息基础
     * @return 计息基础
     */
    public String getBasis() {
        return basis;
    }

    /**
     * 设置 计息基础
     * @param basis 计息基础
     */
    public void setBasis(String basis) {
        this.basis = basis == null ? null : basis.trim();
    }

    /**
     * 取得 业务场景分类
     * @return 业务场景分类
     */
    public String getBusiSceneCalss() {
        return busiSceneCalss;
    }

    /**
     * 设置 业务场景分类
     * @param busiSceneCalss 业务场景分类
     */
    public void setBusiSceneCalss(String busiSceneCalss) {
        this.busiSceneCalss = busiSceneCalss == null ? null : busiSceneCalss.trim();
    }

    /**
     * 取得 交收方式
     * @return 交收方式
     */
    public String getSettleMode() {
        return settleMode;
    }

    /**
     * 设置 交收方式
     * @param settleMode 交收方式
     */
    public void setSettleMode(String settleMode) {
        this.settleMode = settleMode == null ? null : settleMode.trim();
    }

    public String getDeliveryResult() {
        return deliveryResult;
    }

    public void setDeliveryResult(String deliveryResult) {
        this.deliveryResult = deliveryResult;
    }

    /**
     * 取得 印花税_原始
     * @return 印花税_原始
     */
    public BigDecimal getStampFeeRaw() {
        return stampFeeRaw;
    }

    /**
     * 设置 印花税_原始
     * @param stampFeeRaw 印花税_原始
     */
    public void setStampFeeRaw(BigDecimal stampFeeRaw) {
        this.stampFeeRaw = stampFeeRaw;
    }

    /**
     * 取得 经手费_原始
     * @return 经手费_原始
     */
    public BigDecimal getJsfAmtRaw() {
        return jsfAmtRaw;
    }

    /**
     * 设置 经手费_原始
     * @param jsfAmtRaw 经手费_原始
     */
    public void setJsfAmtRaw(BigDecimal jsfAmtRaw) {
        this.jsfAmtRaw = jsfAmtRaw;
    }

    /**
     * 取得 过户费_原始
     * @return 过户费_原始
     */
    public BigDecimal getGhfAmtRaw() {
        return ghfAmtRaw;
    }

    /**
     * 设置 过户费_原始
     * @param ghfAmtRaw 过户费_原始
     */
    public void setGhfAmtRaw(BigDecimal ghfAmtRaw) {
        this.ghfAmtRaw = ghfAmtRaw;
    }

    /**
     * 取得 证管费_原始
     * @return 证管费_原始
     */
    public BigDecimal getZgfAmtRaw() {
        return zgfAmtRaw;
    }

    /**
     * 设置 证管费_原始
     * @param zgfAmtRaw 证管费_原始
     */
    public void setZgfAmtRaw(BigDecimal zgfAmtRaw) {
        this.zgfAmtRaw = zgfAmtRaw;
    }

    /**
     * 取得 手续费_原始
     * @return 手续费_原始
     */
    public BigDecimal getSxfAmtRaw() {
        return sxfAmtRaw;
    }

    /**
     * 设置 手续费_原始
     * @param sxfAmtRaw 手续费_原始
     */
    public void setSxfAmtRaw(BigDecimal sxfAmtRaw) {
        this.sxfAmtRaw = sxfAmtRaw;
    }

    /**
     * 取得 结算费_原始
     * @return 结算费_原始
     */
    public BigDecimal getSettleFeeRaw() {
        return settleFeeRaw;
    }

    /**
     * 设置 结算费_原始
     * @param settleFeeRaw 结算费_原始
     */
    public void setSettleFeeRaw(BigDecimal settleFeeRaw) {
        this.settleFeeRaw = settleFeeRaw;
    }

    /**
     * 取得 佣金_原始
     * @return 佣金_原始
     */
    public BigDecimal getCommissionAmtRaw() {
        return commissionAmtRaw;
    }

    /**
     * 设置 佣金_原始
     * @param commissionAmtRaw 佣金_原始
     */
    public void setCommissionAmtRaw(BigDecimal commissionAmtRaw) {
        this.commissionAmtRaw = commissionAmtRaw;
    }

    /**
     * 取得 交易金额_原始
     * @return 交易金额_原始
     */
    public BigDecimal getTradeAmtRaw() {
        return tradeAmtRaw;
    }

    /**
     * 设置 交易金额_原始
     * @param tradeAmtRaw 交易金额_原始
     */
    public void setTradeAmtRaw(BigDecimal tradeAmtRaw) {
        this.tradeAmtRaw = tradeAmtRaw;
    }

    /**
     * 取得 成交编号_原始
     * @return 成交编号_原始
     */
    public String getTradeIdRaw() {
        return tradeIdRaw;
    }

    /**
     * 设置 成交编号_原始
     * @param tradeIdRaw 成交编号_原始
     */
    public void setTradeIdRaw(String tradeIdRaw) {
        this.tradeIdRaw = tradeIdRaw == null ? null : tradeIdRaw.trim();
    }

    /**
     * 取得 临时表流水号
     * @return 临时表流水号
     */
    public String getSeqNo() {
        return seqNo;
    }

    /**
     * 设置 临时表流水号
     * @param seqNo 临时表流水号
     */
    public void setSeqNo(String seqNo) {
        this.seqNo = seqNo == null ? null : seqNo.trim();
    }

    /**
     * 取得 是否优先配售
     * @return 是否优先配售
     */
    public String getIsPurchTypeYx() {
        return isPurchTypeYx;
    }

    /**
     * 设置 是否优先配售
     * @param isPurchTypeYx 是否优先配售
     */
    public void setIsPurchTypeYx(String isPurchTypeYx) {
        this.isPurchTypeYx = isPurchTypeYx == null ? null : isPurchTypeYx.trim();
    }

    /**
     * 取得 首期成交编号
     * @return 首期成交编号
     */
    public String getfCjbh() {
        return fCjbh;
    }

    /**
     * 设置 首期成交编号
     * @param fCjbh 首期成交编号
     */
    public void setfCjbh(String fCjbh) {
        this.fCjbh = fCjbh == null ? null : fCjbh.trim();
    }
}