package com.joyintech.tams.common.base.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.framework.json.CustomDateDeserialize;
import com.joyintech.tams.common.base.pojo.PledgeListDto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class InfContractDetailfile extends BaseEntity {
    /**
     * 流水号
     */
    private String seqno;

    /**
     * 委托代码，就是投管的指令
     */
    private String entrustId;

    /**
     * 订单代码
     */
    private String orderId;

    /**
     * 交易委托编号，实际发到交易所的编号
     */
    private String orderNo;

    /**
     * 成交编号
     */
    private String dealNo;

    /**
     * 组合代码
     */
    private String portfolioId;

    /**
     * 资金账号，券商资金账户
     */
    private String acctNo;

    /**
     * 股东代码
     */
    private String bondId;

    /**
     * 金融产品代码
     */
    private String finprodId;

    /**
     * 交易方向，买入、卖出等
     */
    private String ps;

    /**
     * 交易场所，银行间、上交所、深交所、柜面等
     */
    private String tradeMarket;

    /**
     * 交易平台，银行间、上交所固定收益平台、上交所大宗交易平台、深交所大宗平台、深交所综合协议交易平台、其他等
     */
    private String tradePlat;

    /**
     * 市场代码
     */
    private String finprodMarketId;

    /**
     * 成交数量，债券数量等
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal shareAmt;

    /**
     * 成交净价
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal unitCprice;

    /**
     * 成交全价
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal unitFprice;

    /**
     * 交易金额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal tradeAmt;

    /**
     * 交易日期
     */
    private Date tradeDate;

    /**
     * 首次结算日
     */
    private Date vdate;

    /**
     * 到期结算日
     */
    private Date mdate;

    /**
     * 首期交易编号
     */
    private String vTradeId;

    /**
     * 交易币种
     */
    private String ccy;

    /**
     * 折算比例
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal scale;

    /**
     * 交易对手
     */
    private String counterId;

    /**
     * 交收日
     */
    private Date settleDay;

    /**
     * 业务类别,数据字典DELIVERY_BUS_TYPE;01 上交所固定收益平台债券买入;02 上交所固定收益平台债券卖出
     */
    private String busiType;

    /**
     * 质押券列表
     */
    private List<PledgeListDto> pledgeList;

    public List<PledgeListDto> getPledgeList() {
        return pledgeList;
    }

    public void setPledgeList(List<PledgeListDto> pledgeList) {
        this.pledgeList = pledgeList;
    }

    /**
     * 取得 流水号
     * @return 流水号
     */
    public String getSeqno() {
        return seqno;
    }

    /**
     * 设置 流水号
     * @param seqno 流水号
     */
    public void setSeqno(String seqno) {
        this.seqno = seqno == null ? null : seqno.trim();
    }

    /**
     * 取得 委托代码，就是投管的指令
     * @return 委托代码，就是投管的指令
     */
    public String getEntrustId() {
        return entrustId;
    }

    /**
     * 设置 委托代码，就是投管的指令
     * @param entrustId 委托代码，就是投管的指令
     */
    public void setEntrustId(String entrustId) {
        this.entrustId = entrustId == null ? null : entrustId.trim();
    }

    /**
     * 取得 订单代码
     * @return 订单代码
     */
    public String getOrderId() {
        return orderId;
    }

    /**
     * 设置 订单代码
     * @param orderId 订单代码
     */
    public void setOrderId(String orderId) {
        this.orderId = orderId == null ? null : orderId.trim();
    }

    /**
     * 取得 交易委托编号，实际发到交易所的编号
     * @return 交易委托编号，实际发到交易所的编号
     */
    public String getOrderNo() {
        return orderNo;
    }

    /**
     * 设置 交易委托编号，实际发到交易所的编号
     * @param orderNo 交易委托编号，实际发到交易所的编号
     */
    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo == null ? null : orderNo.trim();
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
     * 取得 资金账号，券商资金账户
     * @return 资金账号，券商资金账户
     */
    public String getAcctNo() {
        return acctNo;
    }

    /**
     * 设置 资金账号，券商资金账户
     * @param acctNo 资金账号，券商资金账户
     */
    public void setAcctNo(String acctNo) {
        this.acctNo = acctNo == null ? null : acctNo.trim();
    }

    /**
     * 取得 股东代码
     * @return 股东代码
     */
    public String getBondId() {
        return bondId;
    }

    /**
     * 设置 股东代码
     * @param bondId 股东代码
     */
    public void setBondId(String bondId) {
        this.bondId = bondId == null ? null : bondId.trim();
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
     * 取得 交易方向，买入、卖出等
     * @return 交易方向，买入、卖出等
     */
    public String getPs() {
        return ps;
    }

    /**
     * 设置 交易方向，买入、卖出等
     * @param ps 交易方向，买入、卖出等
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
     * 取得 市场代码
     * @return 市场代码
     */
    public String getFinprodMarketId() {
        return finprodMarketId;
    }

    /**
     * 设置 市场代码
     * @param finprodMarketId 市场代码
     */
    public void setFinprodMarketId(String finprodMarketId) {
        this.finprodMarketId = finprodMarketId == null ? null : finprodMarketId.trim();
    }

    /**
     * 取得 成交数量，债券数量等
     * @return 成交数量，债券数量等
     */
    public BigDecimal getShareAmt() {
        return shareAmt;
    }

    /**
     * 设置 成交数量，债券数量等
     * @param shareAmt 成交数量，债券数量等
     */
    public void setShareAmt(BigDecimal shareAmt) {
        this.shareAmt = shareAmt;
    }

    /**
     * 取得 成交净价
     * @return 成交净价
     */
    public BigDecimal getUnitCprice() {
        return unitCprice;
    }

    /**
     * 设置 成交净价
     * @param unitCprice 成交净价
     */
    public void setUnitCprice(BigDecimal unitCprice) {
        this.unitCprice = unitCprice;
    }

    /**
     * 取得 成交全价
     * @return 成交全价
     */
    public BigDecimal getUnitFprice() {
        return unitFprice;
    }

    /**
     * 设置 成交全价
     * @param unitFprice 成交全价
     */
    public void setUnitFprice(BigDecimal unitFprice) {
        this.unitFprice = unitFprice;
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
     * 取得 首次结算日
     * @return 首次结算日
     */
    public Date getVdate() {
        return vdate;
    }

    /**
     * 设置 首次结算日
     * @param vdate 首次结算日
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setVdate(Date vdate) {
        this.vdate = vdate;
    }

    /**
     * 取得 到期结算日
     * @return 到期结算日
     */
    public Date getMdate() {
        return mdate;
    }

    /**
     * 设置 到期结算日
     * @param mdate 到期结算日
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setMdate(Date mdate) {
        this.mdate = mdate;
    }

    /**
     * 取得 首期交易编号
     * @return 首期交易编号
     */
    public String getvTradeId() {
        return vTradeId;
    }

    /**
     * 设置 首期交易编号
     * @param vTradeId 首期交易编号
     */
    public void setvTradeId(String vTradeId) {
        this.vTradeId = vTradeId == null ? null : vTradeId.trim();
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
     * 取得 折算比例
     * @return 折算比例
     */
    public BigDecimal getScale() {
        return scale;
    }

    /**
     * 设置 折算比例
     * @param scale 折算比例
     */
    public void setScale(BigDecimal scale) {
        this.scale = scale;
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
     * 取得 交收日
     * @return 交收日
     */
    public Date getSettleDay() {
        return settleDay;
    }

    /**
     * 设置 交收日
     * @param settleDay 交收日
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setSettleDay(Date settleDay) {
        this.settleDay = settleDay;
    }

    /**
     * 取得 业务类别,数据字典DELIVERY_BUS_TYPE;01 上交所固定收益平台债券买入;02 上交所固定收益平台债券卖出
     * @return 业务类别,数据字典DELIVERY_BUS_TYPE;01 上交所固定收益平台债券买入;02 上交所固定收益平台债券卖出
     */
    public String getBusiType() {
        return busiType;
    }

    /**
     * 设置 业务类别,数据字典DELIVERY_BUS_TYPE;01 上交所固定收益平台债券买入;02 上交所固定收益平台债券卖出
     * @param busiType 业务类别,数据字典DELIVERY_BUS_TYPE;01 上交所固定收益平台债券买入;02 上交所固定收益平台债券卖出
     */
    public void setBusiType(String busiType) {
        this.busiType = busiType == null ? null : busiType.trim();
    }


    private String tradeId;         //本方交易员

    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }
}