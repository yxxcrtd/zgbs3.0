package com.joyintech.tams.common.base.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.framework.json.CustomDateDeserialize;

import java.math.BigDecimal;
import java.util.Date;

public class TrdManualSettleInfo extends BaseEntity {
    /**
     * 待交割序号
     */
    private String settleSeqNo;

    /**
     * 交收状态数据字典
        未交收
        已交收
        交收延期
        交收失败
     */
    private String settleStatus;

    /**
     * 指令编号
     */
    private String orderId;

    /**
     * 指令委托Id
     */
    private String entrustOrderId;

    /**
     * 原业务编号
     */
    private String tradeId;

    /**
     * 交易日期
     */
    private Date tradeDate;

    /**
     * 交收日期
     */
    private Date settleDate;

    /**
     * 组合代码
     */
    private String portfolioId;

    /**
     * 资金账户
     */
    private String acctNo;

    /**
     * 证券账户
     */
    private String secAcctId;

    /**
     * 交易场所
     */
    private String tradeMarket;

    /**
     * 交易平台，银行间、上交所固定收益平台、上交所大宗交易平台、深交所大宗平台、深交所综合协议交易平台、其他等
     */
    private String tradePlat;

    /**
     * 金融产品代码
     */
    private String finprodId;

    /**
     * 交割数量
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal settleNum;

    /**
     * 交割金额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal settleAmt;

    /**
     * 业务类别 数据字典
     */
    private String busiType;

    /**
     * 交易对手
     */
    private String counterId;

    /**
     * 委托编号
     */
    private String entrustNo;

    /**
     * 成交编号
     */
    private String dealNo;

    /**
     *回购金额（质押式回购）
     */
    private BigDecimal prin;

    /**
     *到期结算金额（质押式回购）
     */
    private BigDecimal mTradeAmt;

    /**
     * 质押数量（质押式回购）
     */
    private BigDecimal pledgeNumber;

    /**
     * 质押券代码（质押式回购）
     */
    private String pledgeId;

    /**
     * 原质押券代码（质押式回购）
     */
    private String bPledgeId;

    /**
     * 质押券名称（质押式回购）
     */
    private String pledgeName;

    /**
     * 市场代码
     */
    private String finprodMarketId;

    /**
     * 金融产品全称
     */
    private String finprodName;
    /**
     * 组合名称
     */
    private String portfolioName;

    private String counterName;

    public String getEntrustOrderId() {
        return entrustOrderId;
    }

    public void setEntrustOrderId(String entrustOrderId) {
        this.entrustOrderId = entrustOrderId;
    }

    /**
     * 取得 待交割序号
     * @return 待交割序号
     */
    public String getSettleSeqNo() {
        return settleSeqNo;
    }

    /**
     * 设置 待交割序号
     * @param settleSeqNo 待交割序号
     */
    public void setSettleSeqNo(String settleSeqNo) {
        this.settleSeqNo = settleSeqNo == null ? null : settleSeqNo.trim();
    }

    /**
     * 取得 交收状态数据字典
        未交收
        已交收
        交收延期
        交收失败
     * @return 交收状态数据字典
     */
    public String getSettleStatus() {
        return settleStatus;
    }

    /**
     * 设置 交收状态数据字典
        未交收
        已交收
        交收延期
        交收失败
     * @param settleStatus 交收状态数据字典
     */
    public void setSettleStatus(String settleStatus) {
        this.settleStatus = settleStatus == null ? null : settleStatus.trim();
    }

    /**
     * 取得 指令编号
     * @return 指令编号
     */
    public String getOrderId() {
        return orderId;
    }

    /**
     * 设置 指令编号
     * @param orderId 指令编号
     */
    public void setOrderId(String orderId) {
        this.orderId = orderId == null ? null : orderId.trim();
    }

    /**
     * 取得 原业务编号
     * @return 原业务编号
     */
    public String getTradeId() {
        return tradeId;
    }

    /**
     * 设置 原业务编号
     * @param tradeId 原业务编号
     */
    public void setTradeId(String tradeId) {
        this.tradeId = tradeId == null ? null : tradeId.trim();
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
     * 取得 交收日期
     * @return 交收日期
     */
    public Date getSettleDate() {
        return settleDate;
    }

    /**
     * 设置 交收日期
     * @param settleDate 交收日期
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setSettleDate(Date settleDate) {
        this.settleDate = settleDate;
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
     * 取得 交割数量
     * @return 交割数量
     */
    public BigDecimal getSettleNum() {
        return settleNum;
    }

    /**
     * 设置 交割数量
     * @param settleNum 交割数量
     */
    public void setSettleNum(BigDecimal settleNum) {
        this.settleNum = settleNum;
    }

    /**
     * 取得 交割金额
     * @return 交割金额
     */
    public BigDecimal getSettleAmt() {
        return settleAmt;
    }

    /**
     * 设置 交割金额
     * @param settleAmt 交割金额
     */
    public void setSettleAmt(BigDecimal settleAmt) {
        this.settleAmt = settleAmt;
    }

    /**
     * 取得 业务类别 数据字典
     * @return 业务类别 数据字典
     */
    public String getBusiType() {
        return busiType;
    }

    /**
     * 设置 业务类别 数据字典
     * @param busiType 业务类别 数据字典
     */
    public void setBusiType(String busiType) {
        this.busiType = busiType == null ? null : busiType.trim();
    }

    /**
     * 取得 交易对手
     * @return 交易对手
     */
    public String getCounterId() {
        return counterId;
    }

    /**
     * 设置 交易对手
     * @param counterId 交易对手
     */
    public void setCounterId(String counterId) {
        this.counterId = counterId == null ? null : counterId.trim();
    }

    /**
     * 取得 委托编号
     * @return 委托编号
     */
    public String getEntrustNo() {
        return entrustNo;
    }

    /**
     * 设置 委托编号
     * @param entrustNo 委托编号
     */
    public void setEntrustNo(String entrustNo) {
        this.entrustNo = entrustNo == null ? null : entrustNo.trim();
    }

    /**
     * 取得 成交编号
     * @return 成交编号
     */
    public String getDealNo() {
        return dealNo;
    }

    /**
     * 设置 成交编号
     * @param dealNo 成交编号
     */
    public void setDealNo(String dealNo) {
        this.dealNo = dealNo == null ? null : dealNo.trim();
    }

    public BigDecimal getPrin() {
        return prin;
    }

    public void setPrin(BigDecimal prin) {
        this.prin = prin;
    }

    public BigDecimal getmTradeAmt() {
        return mTradeAmt;
    }

    public void setmTradeAmt(BigDecimal mTradeAmt) {
        this.mTradeAmt = mTradeAmt;
    }

    public BigDecimal getPledgeNumber() {
        return pledgeNumber;
    }

    public void setPledgeNumber(BigDecimal pledgeNumber) {
        this.pledgeNumber = pledgeNumber;
    }

    public String getPledgeId() {
        return pledgeId;
    }

    public void setPledgeId(String pledgeId) {
        this.pledgeId = pledgeId;
    }

    public String getbPledgeId() {
        return bPledgeId;
    }

    public void setbPledgeId(String bPledgeId) {
        this.bPledgeId = bPledgeId;
    }

    public String getPledgeName() {
        return pledgeName;
    }

    public void setPledgeName(String pledgeName) {
        this.pledgeName = pledgeName;
    }

    public String getFinprodMarketId() {
        return finprodMarketId;
    }

    public void setFinprodMarketId(String finprodMarketId) {
        this.finprodMarketId = finprodMarketId;
    }

    public String getFinprodName() {
        return finprodName;
    }

    public void setFinprodName(String finprodName) {
        this.finprodName = finprodName;
    }

    public String getPortfolioName() {
        return portfolioName;
    }

    public void setPortfolioName(String portfolioName) {
        this.portfolioName = portfolioName;
    }

    public String getCounterName() {
        return counterName;
    }

    public void setCounterName(String counterName) {
        this.counterName = counterName;
    }
}