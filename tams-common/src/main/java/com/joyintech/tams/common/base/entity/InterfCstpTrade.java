package com.joyintech.tams.common.base.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.framework.json.CustomDateDeserialize;
import java.math.BigDecimal;
import java.util.Date;

public class InterfCstpTrade extends BaseEntity {
    /**
     * 报文类型
     */
    private String msgtype;

    /**
     * 报文序列号
     */
    private String msgseqnum;

    /**
     * 发送时间
     */
    private String sendingtime;

    /**
     * 报文最初发送方
     */
    private String onbehalfofcompid;

    /**
     * 数据来源
     */
    private String datasource;

    /**
     * 业务场景（比如BOND，REPO）
     */
    private String businesstype;

    /**
     * 成交编号
     */
    private String execid;

    /**
     * 券面总额合计 单位：元，整数
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal lastqty;

    /**
     * 回购利率 单位：%，精度：4位
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal price;

    /**
     * 交易品种/债券ID
     */
    private String securityid;

    /**
     * 交易方向
     */
    private String side;

    /**
     * 债券名称
     */
    private String symbol;

    /**
     * 补充条款
     */
    private String text;

    /**
     * 业务发生时间
     */
    private String transacttime;

    /**
     * 结算日
     */
    private String settldate;

    /**
     * 成交日期
     */
    private String tradedate;

    /**
     * 报价编号 当成交报文为应急场务操作产生时，报文中将不包含报价编号业务字段
     */
    private String quoteid;

    /**
     * 成交状态 F-成交 4-撤销 5-修改 101-应急
     */
    private String exectype;

    /**
     * 到期结算日
     */
    private String settldate2;

    /**
     * 首次结算方式 0-DVP 4-PUD 5-DUP
     */
    private String deliverytype;

    /**
     * 应计利息总额 单位：元，精度：2位
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal accruedinteresttotalamt;

    /**
     * 实际占款天数 单位：天，整数
     */
    private Integer cashholdingdays;

    /**
     * 到期结算方式 0-DVP 4-PUD 5-DUP
     */
    private String deliverytype2;

    /**
     * 市场标识: 9-质押式回购
     */
    private String marketindicator;

    /**
     * 补充条款标识
     */
    private String remarkindicator;

    /**
     * 到期结算金额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal settlcurramt2;

    /**
     * 交易金额 单位：元，精度：2位
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal tradecashamt;

    /**
     * 回购期限 单位：天，整数
     */
    private Integer tradelimitdays;

    /**
     * 成交时间
     */
    private String tradetime;

    /**
     * 数据类型标识 0-本方数据 2-关联机构数据
     */
    private String datacategoryindicator;

    /**
     * 本方机构代码
     */
    private String ourinstitutioncode;

    /**
     * 119-买入方 120-卖出方
     */
    private String ourinstitutionrole;

    /**
     * 交易员电话
     */
    private String ourtelephone;

    /**
     * 交易员名称
     */
    private String ourtradername;

    /**
     * 地址
     */
    private String ouraddress;

    /**
     * 交易员中文名称
     */
    private String ourtraderchinesename;

    /**
     * 机构中文简称
     */
    private String ourinstitutionchineseshortname;

    /**
     * 机构中文全称
     */
    private String ourinstitutionchinesefullname;

    /**
     * 机构简称
     */
    private String ourinstitutionenglishshortname;

    /**
     * 资金账户户名
     */
    private String ourcapitalaccountname;

    /**
     * 资金开户行
     */
    private String ourcapitalbankname;

    /**
     * 资金账号
     */
    private String ourcapitalaccountnumber;

    /**
     * 支付系统行号
     */
    private String ourpaymentsystemcode;

    /**
     * 托管账户户名
     */
    private String ourdepositaccountname;

    /**
     * 托管机构
     */
    private String ourdepositinstitution;

    /**
     * 托管账号
     */
    private String ourdepositaccountnumber;

    /**
     * 交易成员群组
     */
    private String ourtrademembergroup;

    /**
     * 对方机构代码
     */
    private String counterinstitutioncode;

    /**
     * 对方买卖方类型
     */
    private String counterinstitutionrole;

    /**
     * 对方交易员电话
     */
    private String countertelephone;

    /**
     * 对方交易员名称
     */
    private String countertradername;

    /**
     * 对方地址
     */
    private String counteraddress;

    /**
     * 对方交易员中文名称
     */
    private String countertraderchinesename;

    /**
     * 对方机构中文简称
     */
    private String institutionchineseshortname;

    /**
     * 对方机构中文全称
     */
    private String institutionchinesefullname;

    /**
     * 对方机构简称
     */
    private String institutionenglishshortname;

    /**
     * 对方资金账户户名
     */
    private String countercapitalaccountname;

    /**
     * 对方资金开户行
     */
    private String countercapitalbankname;

    /**
     * 对方资金账号
     */
    private String countercapitalaccountnumber;

    /**
     * 对方支付系统行号
     */
    private String counterpaymentsystemcode;

    /**
     * 对方托管账户户名
     */
    private String counterdepositaccountname;

    /**
     * 对方托管机构
     */
    private String counterdepositinstitution;

    /**
     * 对方托管账号
     */
    private String counterdepositaccountnumber;

    /**
     * 对方交易成员群组
     */
    private String countertrademembergroup;

    /**
     * 结算币种
     */
    private String settlcurrency;

    /**
     * 订单编号
     */
    private String orderid;

    /**
     * 待偿债
     */
    private String termtomaturity;

    /**
     * 结算金额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal settlcurramt;

    /**
     * 应计利息 单位：元，精度：5位
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal accruedinterestamt;

    /**
     * 全价 单位：元，精度：4位
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal dirtyprice;

    /**
     * 成交类别 1-非做市成交 2-做市成交 3-尝试做市成交
     */
    private String tradetype;

    /**
     * 有无保证品 N-无
     */
    private String marginrequired;

    /**
     * null
     */
    private String marginchangeindicator;

    /**
     * 债券代码
     */
    private String underlyingsecurityid;

    /**
     * 债券名称2
     */
    private String underlyingsymbol;

    /**
     * 单个券券面总额 单位：元，整数
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal underlyingqty;

    /**
     * 首次净价 单位：元，精度：4位
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal underlyingpx;

    /**
     * 到期净价 单位：元，精度：4位
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal underlyingpx2;

    /**
     * 首次应计利息 单位：元，精度：5位
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal underlyingaccruedinterestamt;

    /**
     * 到期应计利息 单位：元，精度：5位
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal underlyingaccruedinterestamt2;

    /**
     * 首次全价 单位：元，精度：4位
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal underlyingdirtyprice;

    /**
     * 到期全价 单位：元，精度：4位
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal underlyingdirtyprice2;

    /**
     * 首次收益率 单位：%，精度：4位
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal underlyingyield;

    /**
     * 到期收益率 单位：%，精度：4位
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal underlyingyield2;

    /**
     * 数据更新时间
     */
    private Date updatetime;

    /**
     * 行权收益率
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal strikeyield;

    /**
     * 到期收益率2
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal yield2;

    /**
     * 交易方式
     */
    private String trademethod;

    /**
     * 清算速度
     */
    private String settltype;

    /**
     * 成交报文传输动作状态
     */
    private String dealtranstype;

    /**
     * 客户端参考编号
     */
    private String clordid;

    /**
     * 应急标识
     */
    private String contingencyindicator;

    /**
     * 请求报价编号
     */
    private String reference;

    /**
     * 清算方式  6-集中清算 13-自行清算
     */
    private String clearingmethod;

    /**
     * ABS券每百元本金额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal principal;

    /**
     * ABS券当前本金额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal totalprincipal;

    /**
     * Y-现券 N-ABS券
     */
    private String cashbondindicator;

    /**
     * null
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal miscfeeamt;

    /**
     * null
     */
    private String disputesettlementmethod;

    /**
     * null
     */
    private String couponpaymentindicator;

    /**
     * null
     */
    private String marginreplacement;

    /**
     * null
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal margintotalamt;

    /**
     * null
     */
    private String margintype;

    /**
     * null
     */
    private String startdate;

    /**
     * null
     */
    private String enddate;

    /**
     * null
     */
    private String firstperiodstartdate;

    /**
     * null
     */
    private String leginterestaccrualdate;

    /**
     * null
     */
    private String trdtype;

    /**
     * null
     */
    private String legdaycount;

    /**
     * null
     */
    private String legbenchmarkcurvename;

    /**
     * null
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal legprice;

    /**
     * null
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal legbenchmarkspread;

    /**
     * null
     */
    private String leginterestaccrualfrequency;

    /**
     * null
     */
    private String leginterestaccrualmethod;

    /**
     * null
     */
    private String couponpaymentdatereset;

    /**
     * null
     */
    private String interestaccuraldaysadjustment;

    /**
     * null
     */
    private String calculateagency;

    /**
     * null
     */
    private String legside1;

    /**
     * null
     */
    private String legside2;

    /**
     * null
     */
    private String fixlegdaycount;

    /**
     * null
     */
    private String fltlegdaycount;

    /**
     * null
     */
    private String fixlegcouponpaymentfrequency;

    /**
     * null
     */
    private String fltlegcouponpaymentfrequency;

    /**
     * null
     */
    private String fixlegcouponpaymentdate;

    /**
     * null
     */
    private String fltlegcouponpaymentdate;

    /**
     * 交易号
     */
    private String dealNo;

    /**
     * 交易本金(中债登)
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal cbFpriceAmt;

    /**
     * 交易本金(上清所)
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal scFpriceAmt;

    /**
     * 取得 报文类型
     * @return 报文类型
     */
    public String getMsgtype() {
        return msgtype;
    }

    /**
     * 设置 报文类型
     * @param msgtype 报文类型
     */
    public void setMsgtype(String msgtype) {
        this.msgtype = msgtype == null ? null : msgtype.trim();
    }

    /**
     * 取得 报文序列号
     * @return 报文序列号
     */
    public String getMsgseqnum() {
        return msgseqnum;
    }

    /**
     * 设置 报文序列号
     * @param msgseqnum 报文序列号
     */
    public void setMsgseqnum(String msgseqnum) {
        this.msgseqnum = msgseqnum == null ? null : msgseqnum.trim();
    }

    /**
     * 取得 发送时间
     * @return 发送时间
     */
    public String getSendingtime() {
        return sendingtime;
    }

    /**
     * 设置 发送时间
     * @param sendingtime 发送时间
     */
    public void setSendingtime(String sendingtime) {
        this.sendingtime = sendingtime == null ? null : sendingtime.trim();
    }

    /**
     * 取得 报文最初发送方
     * @return 报文最初发送方
     */
    public String getOnbehalfofcompid() {
        return onbehalfofcompid;
    }

    /**
     * 设置 报文最初发送方
     * @param onbehalfofcompid 报文最初发送方
     */
    public void setOnbehalfofcompid(String onbehalfofcompid) {
        this.onbehalfofcompid = onbehalfofcompid == null ? null : onbehalfofcompid.trim();
    }

    /**
     * 取得 数据来源
     * @return 数据来源
     */
    public String getDatasource() {
        return datasource;
    }

    /**
     * 设置 数据来源
     * @param datasource 数据来源
     */
    public void setDatasource(String datasource) {
        this.datasource = datasource == null ? null : datasource.trim();
    }

    /**
     * 取得 业务场景（比如BOND，REPO）
     * @return 业务场景（比如BOND，REPO）
     */
    public String getBusinesstype() {
        return businesstype;
    }

    /**
     * 设置 业务场景（比如BOND，REPO）
     * @param businesstype 业务场景（比如BOND，REPO）
     */
    public void setBusinesstype(String businesstype) {
        this.businesstype = businesstype == null ? null : businesstype.trim();
    }

    /**
     * 取得 成交编号
     * @return 成交编号
     */
    public String getExecid() {
        return execid;
    }

    /**
     * 设置 成交编号
     * @param execid 成交编号
     */
    public void setExecid(String execid) {
        this.execid = execid == null ? null : execid.trim();
    }

    /**
     * 取得 券面总额合计 单位：元，整数
     * @return 券面总额合计 单位：元，整数
     */
    public BigDecimal getLastqty() {
        return lastqty;
    }

    /**
     * 设置 券面总额合计 单位：元，整数
     * @param lastqty 券面总额合计 单位：元，整数
     */
    public void setLastqty(BigDecimal lastqty) {
        this.lastqty = lastqty;
    }

    /**
     * 取得 回购利率 单位：%，精度：4位
     * @return 回购利率 单位：%，精度：4位
     */
    public BigDecimal getPrice() {
        return price;
    }

    /**
     * 设置 回购利率 单位：%，精度：4位
     * @param price 回购利率 单位：%，精度：4位
     */
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    /**
     * 取得 交易品种/债券ID
     * @return 交易品种/债券ID
     */
    public String getSecurityid() {
        return securityid;
    }

    /**
     * 设置 交易品种/债券ID
     * @param securityid 交易品种/债券ID
     */
    public void setSecurityid(String securityid) {
        this.securityid = securityid == null ? null : securityid.trim();
    }

    /**
     * 取得 交易方向
     * @return 交易方向
     */
    public String getSide() {
        return side;
    }

    /**
     * 设置 交易方向
     * @param side 交易方向
     */
    public void setSide(String side) {
        this.side = side == null ? null : side.trim();
    }

    /**
     * 取得 债券名称
     * @return 债券名称
     */
    public String getSymbol() {
        return symbol;
    }

    /**
     * 设置 债券名称
     * @param symbol 债券名称
     */
    public void setSymbol(String symbol) {
        this.symbol = symbol == null ? null : symbol.trim();
    }

    /**
     * 取得 补充条款
     * @return 补充条款
     */
    public String getText() {
        return text;
    }

    /**
     * 设置 补充条款
     * @param text 补充条款
     */
    public void setText(String text) {
        this.text = text == null ? null : text.trim();
    }

    /**
     * 取得 业务发生时间
     * @return 业务发生时间
     */
    public String getTransacttime() {
        return transacttime;
    }

    /**
     * 设置 业务发生时间
     * @param transacttime 业务发生时间
     */
    public void setTransacttime(String transacttime) {
        this.transacttime = transacttime == null ? null : transacttime.trim();
    }

    /**
     * 取得 结算日
     * @return 结算日
     */
    public String getSettldate() {
        return settldate;
    }

    /**
     * 设置 结算日
     * @param settldate 结算日
     */
    public void setSettldate(String settldate) {
        this.settldate = settldate == null ? null : settldate.trim();
    }

    /**
     * 取得 成交日期
     * @return 成交日期
     */
    public String getTradedate() {
        return tradedate;
    }

    /**
     * 设置 成交日期
     * @param tradedate 成交日期
     */
    public void setTradedate(String tradedate) {
        this.tradedate = tradedate == null ? null : tradedate.trim();
    }

    /**
     * 取得 报价编号 当成交报文为应急场务操作产生时，报文中将不包含报价编号业务字段
     * @return 报价编号 当成交报文为应急场务操作产生时，报文中将不包含报价编号业务字段
     */
    public String getQuoteid() {
        return quoteid;
    }

    /**
     * 设置 报价编号 当成交报文为应急场务操作产生时，报文中将不包含报价编号业务字段
     * @param quoteid 报价编号 当成交报文为应急场务操作产生时，报文中将不包含报价编号业务字段
     */
    public void setQuoteid(String quoteid) {
        this.quoteid = quoteid == null ? null : quoteid.trim();
    }

    /**
     * 取得 成交状态 F-成交 4-撤销 5-修改 101-应急
     * @return 成交状态 F-成交 4-撤销 5-修改 101-应急
     */
    public String getExectype() {
        return exectype;
    }

    /**
     * 设置 成交状态 F-成交 4-撤销 5-修改 101-应急
     * @param exectype 成交状态 F-成交 4-撤销 5-修改 101-应急
     */
    public void setExectype(String exectype) {
        this.exectype = exectype == null ? null : exectype.trim();
    }

    /**
     * 取得 到期结算日
     * @return 到期结算日
     */
    public String getSettldate2() {
        return settldate2;
    }

    /**
     * 设置 到期结算日
     * @param settldate2 到期结算日
     */
    public void setSettldate2(String settldate2) {
        this.settldate2 = settldate2 == null ? null : settldate2.trim();
    }

    /**
     * 取得 首次结算方式 0-DVP 4-PUD 5-DUP
     * @return 首次结算方式 0-DVP 4-PUD 5-DUP
     */
    public String getDeliverytype() {
        return deliverytype;
    }

    /**
     * 设置 首次结算方式 0-DVP 4-PUD 5-DUP
     * @param deliverytype 首次结算方式 0-DVP 4-PUD 5-DUP
     */
    public void setDeliverytype(String deliverytype) {
        this.deliverytype = deliverytype == null ? null : deliverytype.trim();
    }

    /**
     * 取得 应计利息总额 单位：元，精度：2位
     * @return 应计利息总额 单位：元，精度：2位
     */
    public BigDecimal getAccruedinteresttotalamt() {
        return accruedinteresttotalamt;
    }

    /**
     * 设置 应计利息总额 单位：元，精度：2位
     * @param accruedinteresttotalamt 应计利息总额 单位：元，精度：2位
     */
    public void setAccruedinteresttotalamt(BigDecimal accruedinteresttotalamt) {
        this.accruedinteresttotalamt = accruedinteresttotalamt;
    }

    /**
     * 取得 实际占款天数 单位：天，整数
     * @return 实际占款天数 单位：天，整数
     */
    public Integer getCashholdingdays() {
        return cashholdingdays;
    }

    /**
     * 设置 实际占款天数 单位：天，整数
     * @param cashholdingdays 实际占款天数 单位：天，整数
     */
    public void setCashholdingdays(Integer cashholdingdays) {
        this.cashholdingdays = cashholdingdays;
    }

    /**
     * 取得 到期结算方式 0-DVP 4-PUD 5-DUP
     * @return 到期结算方式 0-DVP 4-PUD 5-DUP
     */
    public String getDeliverytype2() {
        return deliverytype2;
    }

    /**
     * 设置 到期结算方式 0-DVP 4-PUD 5-DUP
     * @param deliverytype2 到期结算方式 0-DVP 4-PUD 5-DUP
     */
    public void setDeliverytype2(String deliverytype2) {
        this.deliverytype2 = deliverytype2 == null ? null : deliverytype2.trim();
    }

    /**
     * 取得 市场标识: 9-质押式回购
     * @return 市场标识: 9-质押式回购
     */
    public String getMarketindicator() {
        return marketindicator;
    }

    /**
     * 设置 市场标识: 9-质押式回购
     * @param marketindicator 市场标识: 9-质押式回购
     */
    public void setMarketindicator(String marketindicator) {
        this.marketindicator = marketindicator == null ? null : marketindicator.trim();
    }

    /**
     * 取得 补充条款标识
     * @return 补充条款标识
     */
    public String getRemarkindicator() {
        return remarkindicator;
    }

    /**
     * 设置 补充条款标识
     * @param remarkindicator 补充条款标识
     */
    public void setRemarkindicator(String remarkindicator) {
        this.remarkindicator = remarkindicator == null ? null : remarkindicator.trim();
    }

    /**
     * 取得 到期结算金额
     * @return 到期结算金额
     */
    public BigDecimal getSettlcurramt2() {
        return settlcurramt2;
    }

    /**
     * 设置 到期结算金额
     * @param settlcurramt2 到期结算金额
     */
    public void setSettlcurramt2(BigDecimal settlcurramt2) {
        this.settlcurramt2 = settlcurramt2;
    }

    /**
     * 取得 交易金额 单位：元，精度：2位
     * @return 交易金额 单位：元，精度：2位
     */
    public BigDecimal getTradecashamt() {
        return tradecashamt;
    }

    /**
     * 设置 交易金额 单位：元，精度：2位
     * @param tradecashamt 交易金额 单位：元，精度：2位
     */
    public void setTradecashamt(BigDecimal tradecashamt) {
        this.tradecashamt = tradecashamt;
    }

    /**
     * 取得 回购期限 单位：天，整数
     * @return 回购期限 单位：天，整数
     */
    public Integer getTradelimitdays() {
        return tradelimitdays;
    }

    /**
     * 设置 回购期限 单位：天，整数
     * @param tradelimitdays 回购期限 单位：天，整数
     */
    public void setTradelimitdays(Integer tradelimitdays) {
        this.tradelimitdays = tradelimitdays;
    }

    /**
     * 取得 成交时间
     * @return 成交时间
     */
    public String getTradetime() {
        return tradetime;
    }

    /**
     * 设置 成交时间
     * @param tradetime 成交时间
     */
    public void setTradetime(String tradetime) {
        this.tradetime = tradetime == null ? null : tradetime.trim();
    }

    /**
     * 取得 数据类型标识 0-本方数据 2-关联机构数据
     * @return 数据类型标识 0-本方数据 2-关联机构数据
     */
    public String getDatacategoryindicator() {
        return datacategoryindicator;
    }

    /**
     * 设置 数据类型标识 0-本方数据 2-关联机构数据
     * @param datacategoryindicator 数据类型标识 0-本方数据 2-关联机构数据
     */
    public void setDatacategoryindicator(String datacategoryindicator) {
        this.datacategoryindicator = datacategoryindicator == null ? null : datacategoryindicator.trim();
    }

    /**
     * 取得 本方机构代码
     * @return 本方机构代码
     */
    public String getOurinstitutioncode() {
        return ourinstitutioncode;
    }

    /**
     * 设置 本方机构代码
     * @param ourinstitutioncode 本方机构代码
     */
    public void setOurinstitutioncode(String ourinstitutioncode) {
        this.ourinstitutioncode = ourinstitutioncode == null ? null : ourinstitutioncode.trim();
    }

    /**
     * 取得 119-买入方 120-卖出方
     * @return 119-买入方 120-卖出方
     */
    public String getOurinstitutionrole() {
        return ourinstitutionrole;
    }

    /**
     * 设置 119-买入方 120-卖出方
     * @param ourinstitutionrole 119-买入方 120-卖出方
     */
    public void setOurinstitutionrole(String ourinstitutionrole) {
        this.ourinstitutionrole = ourinstitutionrole == null ? null : ourinstitutionrole.trim();
    }

    /**
     * 取得 交易员电话
     * @return 交易员电话
     */
    public String getOurtelephone() {
        return ourtelephone;
    }

    /**
     * 设置 交易员电话
     * @param ourtelephone 交易员电话
     */
    public void setOurtelephone(String ourtelephone) {
        this.ourtelephone = ourtelephone == null ? null : ourtelephone.trim();
    }

    /**
     * 取得 交易员名称
     * @return 交易员名称
     */
    public String getOurtradername() {
        return ourtradername;
    }

    /**
     * 设置 交易员名称
     * @param ourtradername 交易员名称
     */
    public void setOurtradername(String ourtradername) {
        this.ourtradername = ourtradername == null ? null : ourtradername.trim();
    }

    /**
     * 取得 地址
     * @return 地址
     */
    public String getOuraddress() {
        return ouraddress;
    }

    /**
     * 设置 地址
     * @param ouraddress 地址
     */
    public void setOuraddress(String ouraddress) {
        this.ouraddress = ouraddress == null ? null : ouraddress.trim();
    }

    /**
     * 取得 交易员中文名称
     * @return 交易员中文名称
     */
    public String getOurtraderchinesename() {
        return ourtraderchinesename;
    }

    /**
     * 设置 交易员中文名称
     * @param ourtraderchinesename 交易员中文名称
     */
    public void setOurtraderchinesename(String ourtraderchinesename) {
        this.ourtraderchinesename = ourtraderchinesename == null ? null : ourtraderchinesename.trim();
    }

    /**
     * 取得 机构中文简称
     * @return 机构中文简称
     */
    public String getOurinstitutionchineseshortname() {
        return ourinstitutionchineseshortname;
    }

    /**
     * 设置 机构中文简称
     * @param ourinstitutionchineseshortname 机构中文简称
     */
    public void setOurinstitutionchineseshortname(String ourinstitutionchineseshortname) {
        this.ourinstitutionchineseshortname = ourinstitutionchineseshortname == null ? null : ourinstitutionchineseshortname.trim();
    }

    /**
     * 取得 机构中文全称
     * @return 机构中文全称
     */
    public String getOurinstitutionchinesefullname() {
        return ourinstitutionchinesefullname;
    }

    /**
     * 设置 机构中文全称
     * @param ourinstitutionchinesefullname 机构中文全称
     */
    public void setOurinstitutionchinesefullname(String ourinstitutionchinesefullname) {
        this.ourinstitutionchinesefullname = ourinstitutionchinesefullname == null ? null : ourinstitutionchinesefullname.trim();
    }

    /**
     * 取得 机构简称
     * @return 机构简称
     */
    public String getOurinstitutionenglishshortname() {
        return ourinstitutionenglishshortname;
    }

    /**
     * 设置 机构简称
     * @param ourinstitutionenglishshortname 机构简称
     */
    public void setOurinstitutionenglishshortname(String ourinstitutionenglishshortname) {
        this.ourinstitutionenglishshortname = ourinstitutionenglishshortname == null ? null : ourinstitutionenglishshortname.trim();
    }

    /**
     * 取得 资金账户户名
     * @return 资金账户户名
     */
    public String getOurcapitalaccountname() {
        return ourcapitalaccountname;
    }

    /**
     * 设置 资金账户户名
     * @param ourcapitalaccountname 资金账户户名
     */
    public void setOurcapitalaccountname(String ourcapitalaccountname) {
        this.ourcapitalaccountname = ourcapitalaccountname == null ? null : ourcapitalaccountname.trim();
    }

    /**
     * 取得 资金开户行
     * @return 资金开户行
     */
    public String getOurcapitalbankname() {
        return ourcapitalbankname;
    }

    /**
     * 设置 资金开户行
     * @param ourcapitalbankname 资金开户行
     */
    public void setOurcapitalbankname(String ourcapitalbankname) {
        this.ourcapitalbankname = ourcapitalbankname == null ? null : ourcapitalbankname.trim();
    }

    /**
     * 取得 资金账号
     * @return 资金账号
     */
    public String getOurcapitalaccountnumber() {
        return ourcapitalaccountnumber;
    }

    /**
     * 设置 资金账号
     * @param ourcapitalaccountnumber 资金账号
     */
    public void setOurcapitalaccountnumber(String ourcapitalaccountnumber) {
        this.ourcapitalaccountnumber = ourcapitalaccountnumber == null ? null : ourcapitalaccountnumber.trim();
    }

    /**
     * 取得 支付系统行号
     * @return 支付系统行号
     */
    public String getOurpaymentsystemcode() {
        return ourpaymentsystemcode;
    }

    /**
     * 设置 支付系统行号
     * @param ourpaymentsystemcode 支付系统行号
     */
    public void setOurpaymentsystemcode(String ourpaymentsystemcode) {
        this.ourpaymentsystemcode = ourpaymentsystemcode == null ? null : ourpaymentsystemcode.trim();
    }

    /**
     * 取得 托管账户户名
     * @return 托管账户户名
     */
    public String getOurdepositaccountname() {
        return ourdepositaccountname;
    }

    /**
     * 设置 托管账户户名
     * @param ourdepositaccountname 托管账户户名
     */
    public void setOurdepositaccountname(String ourdepositaccountname) {
        this.ourdepositaccountname = ourdepositaccountname == null ? null : ourdepositaccountname.trim();
    }

    /**
     * 取得 托管机构
     * @return 托管机构
     */
    public String getOurdepositinstitution() {
        return ourdepositinstitution;
    }

    /**
     * 设置 托管机构
     * @param ourdepositinstitution 托管机构
     */
    public void setOurdepositinstitution(String ourdepositinstitution) {
        this.ourdepositinstitution = ourdepositinstitution == null ? null : ourdepositinstitution.trim();
    }

    /**
     * 取得 托管账号
     * @return 托管账号
     */
    public String getOurdepositaccountnumber() {
        return ourdepositaccountnumber;
    }

    /**
     * 设置 托管账号
     * @param ourdepositaccountnumber 托管账号
     */
    public void setOurdepositaccountnumber(String ourdepositaccountnumber) {
        this.ourdepositaccountnumber = ourdepositaccountnumber == null ? null : ourdepositaccountnumber.trim();
    }

    /**
     * 取得 交易成员群组
     * @return 交易成员群组
     */
    public String getOurtrademembergroup() {
        return ourtrademembergroup;
    }

    /**
     * 设置 交易成员群组
     * @param ourtrademembergroup 交易成员群组
     */
    public void setOurtrademembergroup(String ourtrademembergroup) {
        this.ourtrademembergroup = ourtrademembergroup == null ? null : ourtrademembergroup.trim();
    }

    /**
     * 取得 对方机构代码
     * @return 对方机构代码
     */
    public String getCounterinstitutioncode() {
        return counterinstitutioncode;
    }

    /**
     * 设置 对方机构代码
     * @param counterinstitutioncode 对方机构代码
     */
    public void setCounterinstitutioncode(String counterinstitutioncode) {
        this.counterinstitutioncode = counterinstitutioncode == null ? null : counterinstitutioncode.trim();
    }

    /**
     * 取得 对方买卖方类型
     * @return 对方买卖方类型
     */
    public String getCounterinstitutionrole() {
        return counterinstitutionrole;
    }

    /**
     * 设置 对方买卖方类型
     * @param counterinstitutionrole 对方买卖方类型
     */
    public void setCounterinstitutionrole(String counterinstitutionrole) {
        this.counterinstitutionrole = counterinstitutionrole == null ? null : counterinstitutionrole.trim();
    }

    /**
     * 取得 对方交易员电话
     * @return 对方交易员电话
     */
    public String getCountertelephone() {
        return countertelephone;
    }

    /**
     * 设置 对方交易员电话
     * @param countertelephone 对方交易员电话
     */
    public void setCountertelephone(String countertelephone) {
        this.countertelephone = countertelephone == null ? null : countertelephone.trim();
    }

    /**
     * 取得 对方交易员名称
     * @return 对方交易员名称
     */
    public String getCountertradername() {
        return countertradername;
    }

    /**
     * 设置 对方交易员名称
     * @param countertradername 对方交易员名称
     */
    public void setCountertradername(String countertradername) {
        this.countertradername = countertradername == null ? null : countertradername.trim();
    }

    /**
     * 取得 对方地址
     * @return 对方地址
     */
    public String getCounteraddress() {
        return counteraddress;
    }

    /**
     * 设置 对方地址
     * @param counteraddress 对方地址
     */
    public void setCounteraddress(String counteraddress) {
        this.counteraddress = counteraddress == null ? null : counteraddress.trim();
    }

    /**
     * 取得 对方交易员中文名称
     * @return 对方交易员中文名称
     */
    public String getCountertraderchinesename() {
        return countertraderchinesename;
    }

    /**
     * 设置 对方交易员中文名称
     * @param countertraderchinesename 对方交易员中文名称
     */
    public void setCountertraderchinesename(String countertraderchinesename) {
        this.countertraderchinesename = countertraderchinesename == null ? null : countertraderchinesename.trim();
    }

    /**
     * 取得 对方机构中文简称
     * @return 对方机构中文简称
     */
    public String getInstitutionchineseshortname() {
        return institutionchineseshortname;
    }

    /**
     * 设置 对方机构中文简称
     * @param institutionchineseshortname 对方机构中文简称
     */
    public void setInstitutionchineseshortname(String institutionchineseshortname) {
        this.institutionchineseshortname = institutionchineseshortname == null ? null : institutionchineseshortname.trim();
    }

    /**
     * 取得 对方机构中文全称
     * @return 对方机构中文全称
     */
    public String getInstitutionchinesefullname() {
        return institutionchinesefullname;
    }

    /**
     * 设置 对方机构中文全称
     * @param institutionchinesefullname 对方机构中文全称
     */
    public void setInstitutionchinesefullname(String institutionchinesefullname) {
        this.institutionchinesefullname = institutionchinesefullname == null ? null : institutionchinesefullname.trim();
    }

    /**
     * 取得 对方机构简称
     * @return 对方机构简称
     */
    public String getInstitutionenglishshortname() {
        return institutionenglishshortname;
    }

    /**
     * 设置 对方机构简称
     * @param institutionenglishshortname 对方机构简称
     */
    public void setInstitutionenglishshortname(String institutionenglishshortname) {
        this.institutionenglishshortname = institutionenglishshortname == null ? null : institutionenglishshortname.trim();
    }

    /**
     * 取得 对方资金账户户名
     * @return 对方资金账户户名
     */
    public String getCountercapitalaccountname() {
        return countercapitalaccountname;
    }

    /**
     * 设置 对方资金账户户名
     * @param countercapitalaccountname 对方资金账户户名
     */
    public void setCountercapitalaccountname(String countercapitalaccountname) {
        this.countercapitalaccountname = countercapitalaccountname == null ? null : countercapitalaccountname.trim();
    }

    /**
     * 取得 对方资金开户行
     * @return 对方资金开户行
     */
    public String getCountercapitalbankname() {
        return countercapitalbankname;
    }

    /**
     * 设置 对方资金开户行
     * @param countercapitalbankname 对方资金开户行
     */
    public void setCountercapitalbankname(String countercapitalbankname) {
        this.countercapitalbankname = countercapitalbankname == null ? null : countercapitalbankname.trim();
    }

    /**
     * 取得 对方资金账号
     * @return 对方资金账号
     */
    public String getCountercapitalaccountnumber() {
        return countercapitalaccountnumber;
    }

    /**
     * 设置 对方资金账号
     * @param countercapitalaccountnumber 对方资金账号
     */
    public void setCountercapitalaccountnumber(String countercapitalaccountnumber) {
        this.countercapitalaccountnumber = countercapitalaccountnumber == null ? null : countercapitalaccountnumber.trim();
    }

    /**
     * 取得 对方支付系统行号
     * @return 对方支付系统行号
     */
    public String getCounterpaymentsystemcode() {
        return counterpaymentsystemcode;
    }

    /**
     * 设置 对方支付系统行号
     * @param counterpaymentsystemcode 对方支付系统行号
     */
    public void setCounterpaymentsystemcode(String counterpaymentsystemcode) {
        this.counterpaymentsystemcode = counterpaymentsystemcode == null ? null : counterpaymentsystemcode.trim();
    }

    /**
     * 取得 对方托管账户户名
     * @return 对方托管账户户名
     */
    public String getCounterdepositaccountname() {
        return counterdepositaccountname;
    }

    /**
     * 设置 对方托管账户户名
     * @param counterdepositaccountname 对方托管账户户名
     */
    public void setCounterdepositaccountname(String counterdepositaccountname) {
        this.counterdepositaccountname = counterdepositaccountname == null ? null : counterdepositaccountname.trim();
    }

    /**
     * 取得 对方托管机构
     * @return 对方托管机构
     */
    public String getCounterdepositinstitution() {
        return counterdepositinstitution;
    }

    /**
     * 设置 对方托管机构
     * @param counterdepositinstitution 对方托管机构
     */
    public void setCounterdepositinstitution(String counterdepositinstitution) {
        this.counterdepositinstitution = counterdepositinstitution == null ? null : counterdepositinstitution.trim();
    }

    /**
     * 取得 对方托管账号
     * @return 对方托管账号
     */
    public String getCounterdepositaccountnumber() {
        return counterdepositaccountnumber;
    }

    /**
     * 设置 对方托管账号
     * @param counterdepositaccountnumber 对方托管账号
     */
    public void setCounterdepositaccountnumber(String counterdepositaccountnumber) {
        this.counterdepositaccountnumber = counterdepositaccountnumber == null ? null : counterdepositaccountnumber.trim();
    }

    /**
     * 取得 对方交易成员群组
     * @return 对方交易成员群组
     */
    public String getCountertrademembergroup() {
        return countertrademembergroup;
    }

    /**
     * 设置 对方交易成员群组
     * @param countertrademembergroup 对方交易成员群组
     */
    public void setCountertrademembergroup(String countertrademembergroup) {
        this.countertrademembergroup = countertrademembergroup == null ? null : countertrademembergroup.trim();
    }

    /**
     * 取得 结算币种
     * @return 结算币种
     */
    public String getSettlcurrency() {
        return settlcurrency;
    }

    /**
     * 设置 结算币种
     * @param settlcurrency 结算币种
     */
    public void setSettlcurrency(String settlcurrency) {
        this.settlcurrency = settlcurrency == null ? null : settlcurrency.trim();
    }

    /**
     * 取得 订单编号
     * @return 订单编号
     */
    public String getOrderid() {
        return orderid;
    }

    /**
     * 设置 订单编号
     * @param orderid 订单编号
     */
    public void setOrderid(String orderid) {
        this.orderid = orderid == null ? null : orderid.trim();
    }

    /**
     * 取得 待偿债
     * @return 待偿债
     */
    public String getTermtomaturity() {
        return termtomaturity;
    }

    /**
     * 设置 待偿债
     * @param termtomaturity 待偿债
     */
    public void setTermtomaturity(String termtomaturity) {
        this.termtomaturity = termtomaturity == null ? null : termtomaturity.trim();
    }

    /**
     * 取得 结算金额
     * @return 结算金额
     */
    public BigDecimal getSettlcurramt() {
        return settlcurramt;
    }

    /**
     * 设置 结算金额
     * @param settlcurramt 结算金额
     */
    public void setSettlcurramt(BigDecimal settlcurramt) {
        this.settlcurramt = settlcurramt;
    }

    /**
     * 取得 应计利息 单位：元，精度：5位
     * @return 应计利息 单位：元，精度：5位
     */
    public BigDecimal getAccruedinterestamt() {
        return accruedinterestamt;
    }

    /**
     * 设置 应计利息 单位：元，精度：5位
     * @param accruedinterestamt 应计利息 单位：元，精度：5位
     */
    public void setAccruedinterestamt(BigDecimal accruedinterestamt) {
        this.accruedinterestamt = accruedinterestamt;
    }

    /**
     * 取得 全价 单位：元，精度：4位
     * @return 全价 单位：元，精度：4位
     */
    public BigDecimal getDirtyprice() {
        return dirtyprice;
    }

    /**
     * 设置 全价 单位：元，精度：4位
     * @param dirtyprice 全价 单位：元，精度：4位
     */
    public void setDirtyprice(BigDecimal dirtyprice) {
        this.dirtyprice = dirtyprice;
    }

    /**
     * 取得 成交类别 1-非做市成交 2-做市成交 3-尝试做市成交
     * @return 成交类别 1-非做市成交 2-做市成交 3-尝试做市成交
     */
    public String getTradetype() {
        return tradetype;
    }

    /**
     * 设置 成交类别 1-非做市成交 2-做市成交 3-尝试做市成交
     * @param tradetype 成交类别 1-非做市成交 2-做市成交 3-尝试做市成交
     */
    public void setTradetype(String tradetype) {
        this.tradetype = tradetype == null ? null : tradetype.trim();
    }

    /**
     * 取得 有无保证品 N-无
     * @return 有无保证品 N-无
     */
    public String getMarginrequired() {
        return marginrequired;
    }

    /**
     * 设置 有无保证品 N-无
     * @param marginrequired 有无保证品 N-无
     */
    public void setMarginrequired(String marginrequired) {
        this.marginrequired = marginrequired == null ? null : marginrequired.trim();
    }

    /**
     * 取得 null
     * @return null
     */
    public String getMarginchangeindicator() {
        return marginchangeindicator;
    }

    /**
     * 设置 null
     * @param marginchangeindicator null
     */
    public void setMarginchangeindicator(String marginchangeindicator) {
        this.marginchangeindicator = marginchangeindicator == null ? null : marginchangeindicator.trim();
    }

    /**
     * 取得 债券代码
     * @return 债券代码
     */
    public String getUnderlyingsecurityid() {
        return underlyingsecurityid;
    }

    /**
     * 设置 债券代码
     * @param underlyingsecurityid 债券代码
     */
    public void setUnderlyingsecurityid(String underlyingsecurityid) {
        this.underlyingsecurityid = underlyingsecurityid == null ? null : underlyingsecurityid.trim();
    }

    /**
     * 取得 债券名称2
     * @return 债券名称2
     */
    public String getUnderlyingsymbol() {
        return underlyingsymbol;
    }

    /**
     * 设置 债券名称2
     * @param underlyingsymbol 债券名称2
     */
    public void setUnderlyingsymbol(String underlyingsymbol) {
        this.underlyingsymbol = underlyingsymbol == null ? null : underlyingsymbol.trim();
    }

    /**
     * 取得 单个券券面总额 单位：元，整数
     * @return 单个券券面总额 单位：元，整数
     */
    public BigDecimal getUnderlyingqty() {
        return underlyingqty;
    }

    /**
     * 设置 单个券券面总额 单位：元，整数
     * @param underlyingqty 单个券券面总额 单位：元，整数
     */
    public void setUnderlyingqty(BigDecimal underlyingqty) {
        this.underlyingqty = underlyingqty;
    }

    /**
     * 取得 首次净价 单位：元，精度：4位
     * @return 首次净价 单位：元，精度：4位
     */
    public BigDecimal getUnderlyingpx() {
        return underlyingpx;
    }

    /**
     * 设置 首次净价 单位：元，精度：4位
     * @param underlyingpx 首次净价 单位：元，精度：4位
     */
    public void setUnderlyingpx(BigDecimal underlyingpx) {
        this.underlyingpx = underlyingpx;
    }

    /**
     * 取得 到期净价 单位：元，精度：4位
     * @return 到期净价 单位：元，精度：4位
     */
    public BigDecimal getUnderlyingpx2() {
        return underlyingpx2;
    }

    /**
     * 设置 到期净价 单位：元，精度：4位
     * @param underlyingpx2 到期净价 单位：元，精度：4位
     */
    public void setUnderlyingpx2(BigDecimal underlyingpx2) {
        this.underlyingpx2 = underlyingpx2;
    }

    /**
     * 取得 首次应计利息 单位：元，精度：5位
     * @return 首次应计利息 单位：元，精度：5位
     */
    public BigDecimal getUnderlyingaccruedinterestamt() {
        return underlyingaccruedinterestamt;
    }

    /**
     * 设置 首次应计利息 单位：元，精度：5位
     * @param underlyingaccruedinterestamt 首次应计利息 单位：元，精度：5位
     */
    public void setUnderlyingaccruedinterestamt(BigDecimal underlyingaccruedinterestamt) {
        this.underlyingaccruedinterestamt = underlyingaccruedinterestamt;
    }

    /**
     * 取得 到期应计利息 单位：元，精度：5位
     * @return 到期应计利息 单位：元，精度：5位
     */
    public BigDecimal getUnderlyingaccruedinterestamt2() {
        return underlyingaccruedinterestamt2;
    }

    /**
     * 设置 到期应计利息 单位：元，精度：5位
     * @param underlyingaccruedinterestamt2 到期应计利息 单位：元，精度：5位
     */
    public void setUnderlyingaccruedinterestamt2(BigDecimal underlyingaccruedinterestamt2) {
        this.underlyingaccruedinterestamt2 = underlyingaccruedinterestamt2;
    }

    /**
     * 取得 首次全价 单位：元，精度：4位
     * @return 首次全价 单位：元，精度：4位
     */
    public BigDecimal getUnderlyingdirtyprice() {
        return underlyingdirtyprice;
    }

    /**
     * 设置 首次全价 单位：元，精度：4位
     * @param underlyingdirtyprice 首次全价 单位：元，精度：4位
     */
    public void setUnderlyingdirtyprice(BigDecimal underlyingdirtyprice) {
        this.underlyingdirtyprice = underlyingdirtyprice;
    }

    /**
     * 取得 到期全价 单位：元，精度：4位
     * @return 到期全价 单位：元，精度：4位
     */
    public BigDecimal getUnderlyingdirtyprice2() {
        return underlyingdirtyprice2;
    }

    /**
     * 设置 到期全价 单位：元，精度：4位
     * @param underlyingdirtyprice2 到期全价 单位：元，精度：4位
     */
    public void setUnderlyingdirtyprice2(BigDecimal underlyingdirtyprice2) {
        this.underlyingdirtyprice2 = underlyingdirtyprice2;
    }

    /**
     * 取得 首次收益率 单位：%，精度：4位
     * @return 首次收益率 单位：%，精度：4位
     */
    public BigDecimal getUnderlyingyield() {
        return underlyingyield;
    }

    /**
     * 设置 首次收益率 单位：%，精度：4位
     * @param underlyingyield 首次收益率 单位：%，精度：4位
     */
    public void setUnderlyingyield(BigDecimal underlyingyield) {
        this.underlyingyield = underlyingyield;
    }

    /**
     * 取得 到期收益率 单位：%，精度：4位
     * @return 到期收益率 单位：%，精度：4位
     */
    public BigDecimal getUnderlyingyield2() {
        return underlyingyield2;
    }

    /**
     * 设置 到期收益率 单位：%，精度：4位
     * @param underlyingyield2 到期收益率 单位：%，精度：4位
     */
    public void setUnderlyingyield2(BigDecimal underlyingyield2) {
        this.underlyingyield2 = underlyingyield2;
    }

    /**
     * 取得 数据更新时间
     * @return 数据更新时间
     */
    public Date getUpdatetime() {
        return updatetime;
    }

    /**
     * 设置 数据更新时间
     * @param updatetime 数据更新时间
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setUpdatetime(Date updatetime) {
        this.updatetime = updatetime;
    }

    /**
     * 取得 行权收益率
     * @return 行权收益率
     */
    public BigDecimal getStrikeyield() {
        return strikeyield;
    }

    /**
     * 设置 行权收益率
     * @param strikeyield 行权收益率
     */
    public void setStrikeyield(BigDecimal strikeyield) {
        this.strikeyield = strikeyield;
    }

    /**
     * 取得 到期收益率2
     * @return 到期收益率2
     */
    public BigDecimal getYield2() {
        return yield2;
    }

    /**
     * 设置 到期收益率2
     * @param yield2 到期收益率2
     */
    public void setYield2(BigDecimal yield2) {
        this.yield2 = yield2;
    }

    /**
     * 取得 交易方式
     * @return 交易方式
     */
    public String getTrademethod() {
        return trademethod;
    }

    /**
     * 设置 交易方式
     * @param trademethod 交易方式
     */
    public void setTrademethod(String trademethod) {
        this.trademethod = trademethod == null ? null : trademethod.trim();
    }

    /**
     * 取得 清算速度
     * @return 清算速度
     */
    public String getSettltype() {
        return settltype;
    }

    /**
     * 设置 清算速度
     * @param settltype 清算速度
     */
    public void setSettltype(String settltype) {
        this.settltype = settltype == null ? null : settltype.trim();
    }

    /**
     * 取得 成交报文传输动作状态
     * @return 成交报文传输动作状态
     */
    public String getDealtranstype() {
        return dealtranstype;
    }

    /**
     * 设置 成交报文传输动作状态
     * @param dealtranstype 成交报文传输动作状态
     */
    public void setDealtranstype(String dealtranstype) {
        this.dealtranstype = dealtranstype == null ? null : dealtranstype.trim();
    }

    /**
     * 取得 客户端参考编号
     * @return 客户端参考编号
     */
    public String getClordid() {
        return clordid;
    }

    /**
     * 设置 客户端参考编号
     * @param clordid 客户端参考编号
     */
    public void setClordid(String clordid) {
        this.clordid = clordid == null ? null : clordid.trim();
    }

    /**
     * 取得 应急标识
     * @return 应急标识
     */
    public String getContingencyindicator() {
        return contingencyindicator;
    }

    /**
     * 设置 应急标识
     * @param contingencyindicator 应急标识
     */
    public void setContingencyindicator(String contingencyindicator) {
        this.contingencyindicator = contingencyindicator == null ? null : contingencyindicator.trim();
    }

    /**
     * 取得 请求报价编号
     * @return 请求报价编号
     */
    public String getReference() {
        return reference;
    }

    /**
     * 设置 请求报价编号
     * @param reference 请求报价编号
     */
    public void setReference(String reference) {
        this.reference = reference == null ? null : reference.trim();
    }

    /**
     * 取得 清算方式  6-集中清算 13-自行清算
     * @return 清算方式  6-集中清算 13-自行清算
     */
    public String getClearingmethod() {
        return clearingmethod;
    }

    /**
     * 设置 清算方式  6-集中清算 13-自行清算
     * @param clearingmethod 清算方式  6-集中清算 13-自行清算
     */
    public void setClearingmethod(String clearingmethod) {
        this.clearingmethod = clearingmethod == null ? null : clearingmethod.trim();
    }

    /**
     * 取得 ABS券每百元本金额
     * @return ABS券每百元本金额
     */
    public BigDecimal getPrincipal() {
        return principal;
    }

    /**
     * 设置 ABS券每百元本金额
     * @param principal ABS券每百元本金额
     */
    public void setPrincipal(BigDecimal principal) {
        this.principal = principal;
    }

    /**
     * 取得 ABS券当前本金额
     * @return ABS券当前本金额
     */
    public BigDecimal getTotalprincipal() {
        return totalprincipal;
    }

    /**
     * 设置 ABS券当前本金额
     * @param totalprincipal ABS券当前本金额
     */
    public void setTotalprincipal(BigDecimal totalprincipal) {
        this.totalprincipal = totalprincipal;
    }

    /**
     * 取得 Y-现券 N-ABS券
     * @return Y-现券 N-ABS券
     */
    public String getCashbondindicator() {
        return cashbondindicator;
    }

    /**
     * 设置 Y-现券 N-ABS券
     * @param cashbondindicator Y-现券 N-ABS券
     */
    public void setCashbondindicator(String cashbondindicator) {
        this.cashbondindicator = cashbondindicator == null ? null : cashbondindicator.trim();
    }

    /**
     * 取得 null
     * @return null
     */
    public BigDecimal getMiscfeeamt() {
        return miscfeeamt;
    }

    /**
     * 设置 null
     * @param miscfeeamt null
     */
    public void setMiscfeeamt(BigDecimal miscfeeamt) {
        this.miscfeeamt = miscfeeamt;
    }

    /**
     * 取得 null
     * @return null
     */
    public String getDisputesettlementmethod() {
        return disputesettlementmethod;
    }

    /**
     * 设置 null
     * @param disputesettlementmethod null
     */
    public void setDisputesettlementmethod(String disputesettlementmethod) {
        this.disputesettlementmethod = disputesettlementmethod == null ? null : disputesettlementmethod.trim();
    }

    /**
     * 取得 null
     * @return null
     */
    public String getCouponpaymentindicator() {
        return couponpaymentindicator;
    }

    /**
     * 设置 null
     * @param couponpaymentindicator null
     */
    public void setCouponpaymentindicator(String couponpaymentindicator) {
        this.couponpaymentindicator = couponpaymentindicator == null ? null : couponpaymentindicator.trim();
    }

    /**
     * 取得 null
     * @return null
     */
    public String getMarginreplacement() {
        return marginreplacement;
    }

    /**
     * 设置 null
     * @param marginreplacement null
     */
    public void setMarginreplacement(String marginreplacement) {
        this.marginreplacement = marginreplacement == null ? null : marginreplacement.trim();
    }

    /**
     * 取得 null
     * @return null
     */
    public BigDecimal getMargintotalamt() {
        return margintotalamt;
    }

    /**
     * 设置 null
     * @param margintotalamt null
     */
    public void setMargintotalamt(BigDecimal margintotalamt) {
        this.margintotalamt = margintotalamt;
    }

    /**
     * 取得 null
     * @return null
     */
    public String getMargintype() {
        return margintype;
    }

    /**
     * 设置 null
     * @param margintype null
     */
    public void setMargintype(String margintype) {
        this.margintype = margintype == null ? null : margintype.trim();
    }

    /**
     * 取得 null
     * @return null
     */
    public String getStartdate() {
        return startdate;
    }

    /**
     * 设置 null
     * @param startdate null
     */
    public void setStartdate(String startdate) {
        this.startdate = startdate == null ? null : startdate.trim();
    }

    /**
     * 取得 null
     * @return null
     */
    public String getEnddate() {
        return enddate;
    }

    /**
     * 设置 null
     * @param enddate null
     */
    public void setEnddate(String enddate) {
        this.enddate = enddate == null ? null : enddate.trim();
    }

    /**
     * 取得 null
     * @return null
     */
    public String getFirstperiodstartdate() {
        return firstperiodstartdate;
    }

    /**
     * 设置 null
     * @param firstperiodstartdate null
     */
    public void setFirstperiodstartdate(String firstperiodstartdate) {
        this.firstperiodstartdate = firstperiodstartdate == null ? null : firstperiodstartdate.trim();
    }

    /**
     * 取得 null
     * @return null
     */
    public String getLeginterestaccrualdate() {
        return leginterestaccrualdate;
    }

    /**
     * 设置 null
     * @param leginterestaccrualdate null
     */
    public void setLeginterestaccrualdate(String leginterestaccrualdate) {
        this.leginterestaccrualdate = leginterestaccrualdate == null ? null : leginterestaccrualdate.trim();
    }

    /**
     * 取得 null
     * @return null
     */
    public String getTrdtype() {
        return trdtype;
    }

    /**
     * 设置 null
     * @param trdtype null
     */
    public void setTrdtype(String trdtype) {
        this.trdtype = trdtype == null ? null : trdtype.trim();
    }

    /**
     * 取得 null
     * @return null
     */
    public String getLegdaycount() {
        return legdaycount;
    }

    /**
     * 设置 null
     * @param legdaycount null
     */
    public void setLegdaycount(String legdaycount) {
        this.legdaycount = legdaycount == null ? null : legdaycount.trim();
    }

    /**
     * 取得 null
     * @return null
     */
    public String getLegbenchmarkcurvename() {
        return legbenchmarkcurvename;
    }

    /**
     * 设置 null
     * @param legbenchmarkcurvename null
     */
    public void setLegbenchmarkcurvename(String legbenchmarkcurvename) {
        this.legbenchmarkcurvename = legbenchmarkcurvename == null ? null : legbenchmarkcurvename.trim();
    }

    /**
     * 取得 null
     * @return null
     */
    public BigDecimal getLegprice() {
        return legprice;
    }

    /**
     * 设置 null
     * @param legprice null
     */
    public void setLegprice(BigDecimal legprice) {
        this.legprice = legprice;
    }

    /**
     * 取得 null
     * @return null
     */
    public BigDecimal getLegbenchmarkspread() {
        return legbenchmarkspread;
    }

    /**
     * 设置 null
     * @param legbenchmarkspread null
     */
    public void setLegbenchmarkspread(BigDecimal legbenchmarkspread) {
        this.legbenchmarkspread = legbenchmarkspread;
    }

    /**
     * 取得 null
     * @return null
     */
    public String getLeginterestaccrualfrequency() {
        return leginterestaccrualfrequency;
    }

    /**
     * 设置 null
     * @param leginterestaccrualfrequency null
     */
    public void setLeginterestaccrualfrequency(String leginterestaccrualfrequency) {
        this.leginterestaccrualfrequency = leginterestaccrualfrequency == null ? null : leginterestaccrualfrequency.trim();
    }

    /**
     * 取得 null
     * @return null
     */
    public String getLeginterestaccrualmethod() {
        return leginterestaccrualmethod;
    }

    /**
     * 设置 null
     * @param leginterestaccrualmethod null
     */
    public void setLeginterestaccrualmethod(String leginterestaccrualmethod) {
        this.leginterestaccrualmethod = leginterestaccrualmethod == null ? null : leginterestaccrualmethod.trim();
    }

    /**
     * 取得 null
     * @return null
     */
    public String getCouponpaymentdatereset() {
        return couponpaymentdatereset;
    }

    /**
     * 设置 null
     * @param couponpaymentdatereset null
     */
    public void setCouponpaymentdatereset(String couponpaymentdatereset) {
        this.couponpaymentdatereset = couponpaymentdatereset == null ? null : couponpaymentdatereset.trim();
    }

    /**
     * 取得 null
     * @return null
     */
    public String getInterestaccuraldaysadjustment() {
        return interestaccuraldaysadjustment;
    }

    /**
     * 设置 null
     * @param interestaccuraldaysadjustment null
     */
    public void setInterestaccuraldaysadjustment(String interestaccuraldaysadjustment) {
        this.interestaccuraldaysadjustment = interestaccuraldaysadjustment == null ? null : interestaccuraldaysadjustment.trim();
    }

    /**
     * 取得 null
     * @return null
     */
    public String getCalculateagency() {
        return calculateagency;
    }

    /**
     * 设置 null
     * @param calculateagency null
     */
    public void setCalculateagency(String calculateagency) {
        this.calculateagency = calculateagency == null ? null : calculateagency.trim();
    }

    /**
     * 取得 null
     * @return null
     */
    public String getLegside1() {
        return legside1;
    }

    /**
     * 设置 null
     * @param legside1 null
     */
    public void setLegside1(String legside1) {
        this.legside1 = legside1 == null ? null : legside1.trim();
    }

    /**
     * 取得 null
     * @return null
     */
    public String getLegside2() {
        return legside2;
    }

    /**
     * 设置 null
     * @param legside2 null
     */
    public void setLegside2(String legside2) {
        this.legside2 = legside2 == null ? null : legside2.trim();
    }

    /**
     * 取得 null
     * @return null
     */
    public String getFixlegdaycount() {
        return fixlegdaycount;
    }

    /**
     * 设置 null
     * @param fixlegdaycount null
     */
    public void setFixlegdaycount(String fixlegdaycount) {
        this.fixlegdaycount = fixlegdaycount == null ? null : fixlegdaycount.trim();
    }

    /**
     * 取得 null
     * @return null
     */
    public String getFltlegdaycount() {
        return fltlegdaycount;
    }

    /**
     * 设置 null
     * @param fltlegdaycount null
     */
    public void setFltlegdaycount(String fltlegdaycount) {
        this.fltlegdaycount = fltlegdaycount == null ? null : fltlegdaycount.trim();
    }

    /**
     * 取得 null
     * @return null
     */
    public String getFixlegcouponpaymentfrequency() {
        return fixlegcouponpaymentfrequency;
    }

    /**
     * 设置 null
     * @param fixlegcouponpaymentfrequency null
     */
    public void setFixlegcouponpaymentfrequency(String fixlegcouponpaymentfrequency) {
        this.fixlegcouponpaymentfrequency = fixlegcouponpaymentfrequency == null ? null : fixlegcouponpaymentfrequency.trim();
    }

    /**
     * 取得 null
     * @return null
     */
    public String getFltlegcouponpaymentfrequency() {
        return fltlegcouponpaymentfrequency;
    }

    /**
     * 设置 null
     * @param fltlegcouponpaymentfrequency null
     */
    public void setFltlegcouponpaymentfrequency(String fltlegcouponpaymentfrequency) {
        this.fltlegcouponpaymentfrequency = fltlegcouponpaymentfrequency == null ? null : fltlegcouponpaymentfrequency.trim();
    }

    /**
     * 取得 null
     * @return null
     */
    public String getFixlegcouponpaymentdate() {
        return fixlegcouponpaymentdate;
    }

    /**
     * 设置 null
     * @param fixlegcouponpaymentdate null
     */
    public void setFixlegcouponpaymentdate(String fixlegcouponpaymentdate) {
        this.fixlegcouponpaymentdate = fixlegcouponpaymentdate == null ? null : fixlegcouponpaymentdate.trim();
    }

    /**
     * 取得 null
     * @return null
     */
    public String getFltlegcouponpaymentdate() {
        return fltlegcouponpaymentdate;
    }

    /**
     * 设置 null
     * @param fltlegcouponpaymentdate null
     */
    public void setFltlegcouponpaymentdate(String fltlegcouponpaymentdate) {
        this.fltlegcouponpaymentdate = fltlegcouponpaymentdate == null ? null : fltlegcouponpaymentdate.trim();
    }

    /**
     * 取得 交易本金(中债登)
     * @return 交易本金(中债登)
     */
    public BigDecimal getCbFpriceAmt() {
        return cbFpriceAmt;
    }

    /**
     * 设置 交易本金(中债登)
     * @param cbFpriceAmt 交易本金(中债登)
     */
    public void setCbFpriceAmt(BigDecimal cbFpriceAmt) {
        this.cbFpriceAmt = cbFpriceAmt;
    }

    /**
     * 取得 交易本金(上清所)
     * @return 交易本金(上清所)
     */
    public BigDecimal getScFpriceAmt() {
        return scFpriceAmt;
    }

    /**
     * 设置 交易本金(上清所)
     * @param scFpriceAmt 交易本金(上清所)
     */
    public void setScFpriceAmt(BigDecimal scFpriceAmt) {
        this.scFpriceAmt = scFpriceAmt;
    }

    public String getDealNo() {
        return dealNo;
    }

    public void setDealNo(String dealNo) {
        this.dealNo = dealNo;
    }
}