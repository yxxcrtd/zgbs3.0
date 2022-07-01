package com.joyintech.tams.common.base.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.framework.json.CustomDateDeserialize;
import com.joyintech.framework.util.DateUtil;

import java.math.BigDecimal;
import java.util.Date;

public class TrdFinProductDealTotal extends BaseEntity {
    /**
     * 汇总交易编号
     */
    private String totleTradeId;

    /**
     * 业务类型
     */
    private String busiType;

    /**
     * 金融产品代码
     */
    private String finprodId;

    /**
     * 金融产品类型（估值核算），债券、理财产品、基金、理财产品模板、理财产品分层
     */
    private String finprodType;

    /**
     * 金融产品类型（投管分类），债券、理财产品、基金、理财产品模板、理财产品分层
     */
    private String finprodType2;

    /**
     * 分支序号
     */
    private Long branch;

    /**
     * 交易日期
     */
    private Date tradeDate;

    /**
     * 起息日
     */
    private Date vdate;

    /**
     * 到期日
     */
    private Date mdate;

    /**
     * 交割日期
     */
    private Date settleDate;

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
     * 单位面值，债券100或者当前面值，其他资产1
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal parValue;

    /**
     * 净价总额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal cpriceAmt;

    /**
     * 交易份额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal shareAmt;

    /**
     * 交易本金
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
     * 随交易支付费用
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal feeAmtPay;

    /**
     * 交易金额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal tradeAmt;

    /**
     * 未随交易支付费用
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal feeAmtUnpay;

    /**
     * 总交易费用
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal feeAmt;

    /**
     * 交易方向
     */
    private String ps;


    /**
     * 交易方向转义
     */
    private String psStr;

    /**
     * 到期收益率
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal yield;

    /**
     * 行权收益率
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal exerYield;

    /**
     * 投资目的
     */
    private String invAim;

    /**
     * 交易状态，初始、修改、撤销
     */
    private String tradeStatus;

    /**
     * 原交易编号
     */
    private String pTradeId;

    /**
     * 是否已撤销，默认“否”，在交易发生撤销时更新成“是”
     */
    private String isCancel;

    /**
     * 是否结清，收息付费时用
     */
    private String isClean;

    /**
     * 现金流代码，金融产品代码+金融产品类型+现金流二级类型，还本、付息、付费等时存对应计划的现金流代码
     */
    private String cashId;

    /**
     * 证券管理户代码
     */
    private String secManageAcctId;

    /**
     * 前置交易编号，记录该交易的前置交易编号。比如申购确认时存申购申请交易编号
     */
    private String bTradeId;

    /**
     * 渠道/通道代码
     */
    private String chlId;

    /**
     * 交易对手
     */
    private String counterId;

    /**
     * 是否外部交易，是、否
     */
    private String isOutTrade;

    /**
     * 交易场所，银行间、上交所、深交所、柜面等
     */
    private String tradeMarket;

    /**
     * 交易平台，银行间、上交所固定收益平台、上交所大宗交易平台、深交所大宗平台、深交所综合协议交易平台、其他等
     */
    private String tradePlat;

    /**
     * 交易市场，一级市场、二级市场
     */
    private String tradeMarketMode;

    /**
     * 交易员
     */
    private String trader;

    /**
     * 对手方交易员
     */
    private String counterTrader;

    /**
     * 对手方联系方式
     */
    private String counterContact;

    /**
     * 关联系统交易编号
     */
    private String fTradeId;

    /**
     * 投资组合代码
     */
    private String portfolioId;

    /**
     * 理财产品代码
     */
    private String prodId;

    /**
     * 上级汇总交易编号
     */
    private String pTotleTradeId;

    /**
     * 备注
     */
    private String remark;

    /**
     * 本期认购
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal currentSub;

    /**
     * 上期余额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal lastAmt;

    /**
     * 上期赎回
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal lastRed;

    /**
     * 上期滚入余额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal lastAmtSub;

    /**
     * 到账资金
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal accountingAmt;

    /**
     * 赎回收益
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal redProfit;

    /**
     * 赎回成本
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal redCost;

    /**
     * 到期本金
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal dqprinAmt;

    /**
     * 交付日
     */
    private Date payDate;

    /**
     * 剩余名义本金
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal remainPrin;

    /**
     * 投管业务类型
     */
    private String tgBusiType;

    /**
     * 是否逾期交易
     */
    private String isOverdueDeal;

    /**
     * 支付未支付利息
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal payUnpaidintAmt;

    /**
     * 是否转让还本未支付利息
     */
    private String ispayUnpaidint;

    /**
     * 清算速度
     */
    private Long settleSpeed;

    /**
     * 清算类型
     */
    private String clearType;

    /**
     * 有效时间
     */
    private Date effTime;

    /**
     * 首期结算方式
     */
    private String deliveryType;

    /**
     * 到期结算方式
     */
    private String mDeliveryType;

    /**
     * 利率
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal rate;

    /**
     * 回购期限(天)
     */
    private Long repoTerm;

    /**
     * 期限品种
     */
    private String termType;

    //多级法人改造 add by caiqun 20200615
    /**
     * 机构代码
     */
    private String institutionCode;
    //多级法人改造 add by caiqun 20200615

    /**
     * 机构名称
     */
    private String institutionName;
    
    /**
     * 权益登记日
     */
    private Date regDate;

    /**
     * 权益登记日份额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal regDateAmt;

    /**
     * 除权除息日
     */
    private Date exDevidentDate;

    /**
     * 红股上市日
     */
    private Date redShareListingDate;

    /**
     * 派息日
     */
    private Date devidentDate;



    /**
     * 每股送转
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal perDevidentAmt;

    /**
     * 每股派息
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal perDevidentCash;

    /**
     * 分红金额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal devidentCash;

    /**
     * 分红股本
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal devidentAmt;

    /**
     * 是否含股利选择权
     */
    private String ifDividend;
    /**
     * 派息币种
     */
    private String dividCcy;
    /**
     * 汇率(派息等)
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal exchangeRate;

    /**
     * 以股代息价格
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal replacePrice;

    /**
     * 前一日港股持仓市值
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal yesterdayPosition;

    /**
     * 实付金额(港元)
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal tradeAmtHk;

    /**
     *业务模式
     */
    private String businessModel;

    /**
     * 卖出结算汇兑比率
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal sellSettleRate;
    /**
     * 取得 汇总交易编号
     * @return 汇总交易编号
     */
    public String getTotleTradeId() {
        return totleTradeId;
    }

    /**
     * 设置 汇总交易编号
     * @param totleTradeId 汇总交易编号
     */
    public void setTotleTradeId(String totleTradeId) {
        this.totleTradeId = totleTradeId == null ? null : totleTradeId.trim();
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
     * 取得 金融产品类型（估值核算），债券、理财产品、基金、理财产品模板、理财产品分层
     * @return 金融产品类型（估值核算），债券、理财产品、基金、理财产品模板、理财产品分层
     */
    public String getFinprodType() {
        return finprodType;
    }

    /**
     * 设置 金融产品类型（估值核算），债券、理财产品、基金、理财产品模板、理财产品分层
     * @param finprodType 金融产品类型（估值核算），债券、理财产品、基金、理财产品模板、理财产品分层
     */
    public void setFinprodType(String finprodType) {
        this.finprodType = finprodType == null ? null : finprodType.trim();
    }

    /**
     * 取得 金融产品类型（投管分类），债券、理财产品、基金、理财产品模板、理财产品分层
     * @return 金融产品类型（投管分类），债券、理财产品、基金、理财产品模板、理财产品分层
     */
    public String getFinprodType2() {
        return finprodType2;
    }

    /**
     * 设置 金融产品类型（投管分类），债券、理财产品、基金、理财产品模板、理财产品分层
     * @param finprodType2 金融产品类型（投管分类），债券、理财产品、基金、理财产品模板、理财产品分层
     */
    public void setFinprodType2(String finprodType2) {
        this.finprodType2 = finprodType2 == null ? null : finprodType2.trim();
    }

    /**
     * 取得 分支序号
     * @return 分支序号
     */
    public Long getBranch() {
        return branch;
    }

    /**
     * 设置 分支序号
     * @param branch 分支序号
     */
    public void setBranch(Long branch) {
        this.branch = branch;
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
    public String getMdateStr() {
        return DateUtil.format(getMdate());
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
     * 取得 单位面值，债券100或者当前面值，其他资产1
     * @return 单位面值，债券100或者当前面值，其他资产1
     */
    public BigDecimal getParValue() {
        return parValue;
    }

    /**
     * 设置 单位面值，债券100或者当前面值，其他资产1
     * @param parValue 单位面值，债券100或者当前面值，其他资产1
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
     * 取得 交易份额
     * @return 交易份额
     */
    public BigDecimal getShareAmt() {
        return shareAmt;
    }

    /**
     * 设置 交易份额
     * @param shareAmt 交易份额
     */
    public void setShareAmt(BigDecimal shareAmt) {
        this.shareAmt = shareAmt;
    }

    /**
     * 取得 交易本金
     * @return 交易本金
     */
    public BigDecimal getPrinAmt() {
        return prinAmt;
    }

    /**
     * 设置 交易本金
     * @param prinAmt 交易本金
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
     * 取得 随交易支付费用
     * @return 随交易支付费用
     */
    public BigDecimal getFeeAmtPay() {
        return feeAmtPay;
    }

    /**
     * 设置 随交易支付费用
     * @param feeAmtPay 随交易支付费用
     */
    public void setFeeAmtPay(BigDecimal feeAmtPay) {
        this.feeAmtPay = feeAmtPay;
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
     * 取得 未随交易支付费用
     * @return 未随交易支付费用
     */
    public BigDecimal getFeeAmtUnpay() {
        return feeAmtUnpay;
    }

    /**
     * 设置 未随交易支付费用
     * @param feeAmtUnpay 未随交易支付费用
     */
    public void setFeeAmtUnpay(BigDecimal feeAmtUnpay) {
        this.feeAmtUnpay = feeAmtUnpay;
    }

    /**
     * 取得 总交易费用
     * @return 总交易费用
     */
    public BigDecimal getFeeAmt() {
        return feeAmt;
    }

    /**
     * 设置 总交易费用
     * @param feeAmt 总交易费用
     */
    public void setFeeAmt(BigDecimal feeAmt) {
        this.feeAmt = feeAmt;
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
     * 取得 到期收益率
     * @return 到期收益率
     */
    public BigDecimal getYield() {
        return yield;
    }

    /**
     * 设置 到期收益率
     * @param yield 到期收益率
     */
    public void setYield(BigDecimal yield) {
        this.yield = yield;
    }

    /**
     * 取得 行权收益率
     * @return 行权收益率
     */
    public BigDecimal getExerYield() {
        return exerYield;
    }

    /**
     * 设置 行权收益率
     * @param exerYield 行权收益率
     */
    public void setExerYield(BigDecimal exerYield) {
        this.exerYield = exerYield;
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
     * 取得 交易状态，初始、修改、撤销
     * @return 交易状态，初始、修改、撤销
     */
    public String getTradeStatus() {
        return tradeStatus;
    }

    /**
     * 设置 交易状态，初始、修改、撤销
     * @param tradeStatus 交易状态，初始、修改、撤销
     */
    public void setTradeStatus(String tradeStatus) {
        this.tradeStatus = tradeStatus == null ? null : tradeStatus.trim();
    }

    /**
     * 取得 原交易编号
     * @return 原交易编号
     */
    public String getpTradeId() {
        return pTradeId;
    }

    /**
     * 设置 原交易编号
     * @param pTradeId 原交易编号
     */
    public void setpTradeId(String pTradeId) {
        this.pTradeId = pTradeId == null ? null : pTradeId.trim();
    }

    /**
     * 取得 是否已撤销，默认“否”，在交易发生撤销时更新成“是”
     * @return 是否已撤销，默认“否”，在交易发生撤销时更新成“是”
     */
    public String getIsCancel() {
        return isCancel;
    }

    /**
     * 设置 是否已撤销，默认“否”，在交易发生撤销时更新成“是”
     * @param isCancel 是否已撤销，默认“否”，在交易发生撤销时更新成“是”
     */
    public void setIsCancel(String isCancel) {
        this.isCancel = isCancel == null ? null : isCancel.trim();
    }

    /**
     * 取得 是否结清，收息付费时用
     * @return 是否结清，收息付费时用
     */
    public String getIsClean() {
        return isClean;
    }

    /**
     * 设置 是否结清，收息付费时用
     * @param isClean 是否结清，收息付费时用
     */
    public void setIsClean(String isClean) {
        this.isClean = isClean == null ? null : isClean.trim();
    }

    /**
     * 取得 现金流代码，金融产品代码+金融产品类型+现金流二级类型，还本、付息、付费等时存对应计划的现金流代码
     * @return 现金流代码，金融产品代码+金融产品类型+现金流二级类型，还本、付息、付费等时存对应计划的现金流代码
     */
    public String getCashId() {
        return cashId;
    }

    /**
     * 设置 现金流代码，金融产品代码+金融产品类型+现金流二级类型，还本、付息、付费等时存对应计划的现金流代码
     * @param cashId 现金流代码，金融产品代码+金融产品类型+现金流二级类型，还本、付息、付费等时存对应计划的现金流代码
     */
    public void setCashId(String cashId) {
        this.cashId = cashId == null ? null : cashId.trim();
    }

    /**
     * 取得 证券管理户代码
     * @return 证券管理户代码
     */
    public String getSecManageAcctId() {
        return secManageAcctId;
    }

    /**
     * 设置 证券管理户代码
     * @param secManageAcctId 证券管理户代码
     */
    public void setSecManageAcctId(String secManageAcctId) {
        this.secManageAcctId = secManageAcctId == null ? null : secManageAcctId.trim();
    }

    /**
     * 取得 前置交易编号，记录该交易的前置交易编号。比如申购确认时存申购申请交易编号
     * @return 前置交易编号，记录该交易的前置交易编号。比如申购确认时存申购申请交易编号
     */
    public String getbTradeId() {
        return bTradeId;
    }

    /**
     * 设置 前置交易编号，记录该交易的前置交易编号。比如申购确认时存申购申请交易编号
     * @param bTradeId 前置交易编号，记录该交易的前置交易编号。比如申购确认时存申购申请交易编号
     */
    public void setbTradeId(String bTradeId) {
        this.bTradeId = bTradeId == null ? null : bTradeId.trim();
    }

    /**
     * 取得 渠道/通道代码
     * @return 渠道/通道代码
     */
    public String getChlId() {
        return chlId;
    }

    /**
     * 设置 渠道/通道代码
     * @param chlId 渠道/通道代码
     */
    public void setChlId(String chlId) {
        this.chlId = chlId == null ? null : chlId.trim();
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
     * 取得 是否外部交易，是、否
     * @return 是否外部交易，是、否
     */
    public String getIsOutTrade() {
        return isOutTrade;
    }

    /**
     * 设置 是否外部交易，是、否
     * @param isOutTrade 是否外部交易，是、否
     */
    public void setIsOutTrade(String isOutTrade) {
        this.isOutTrade = isOutTrade == null ? null : isOutTrade.trim();
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
     * 取得 交易员
     * @return 交易员
     */
    public String getTrader() {
        return trader;
    }

    /**
     * 设置 交易员
     * @param trader 交易员
     */
    public void setTrader(String trader) {
        this.trader = trader == null ? null : trader.trim();
    }

    /**
     * 取得 对手方交易员
     * @return 对手方交易员
     */
    public String getCounterTrader() {
        return counterTrader;
    }

    /**
     * 设置 对手方交易员
     * @param counterTrader 对手方交易员
     */
    public void setCounterTrader(String counterTrader) {
        this.counterTrader = counterTrader == null ? null : counterTrader.trim();
    }

    /**
     * 取得 对手方联系方式
     * @return 对手方联系方式
     */
    public String getCounterContact() {
        return counterContact;
    }

    /**
     * 设置 对手方联系方式
     * @param counterContact 对手方联系方式
     */
    public void setCounterContact(String counterContact) {
        this.counterContact = counterContact == null ? null : counterContact.trim();
    }

    /**
     * 取得 关联系统交易编号
     * @return 关联系统交易编号
     */
    public String getfTradeId() {
        return fTradeId;
    }

    /**
     * 设置 关联系统交易编号
     * @param fTradeId 关联系统交易编号
     */
    public void setfTradeId(String fTradeId) {
        this.fTradeId = fTradeId == null ? null : fTradeId.trim();
    }

    /**
     * 取得 投资组合代码
     * @return 投资组合代码
     */
    public String getPortfolioId() {
        return portfolioId;
    }

    /**
     * 设置 投资组合代码
     * @param portfolioId 投资组合代码
     */
    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId == null ? null : portfolioId.trim();
    }

    /**
     * 取得 理财产品代码
     * @return 理财产品代码
     */
    public String getProdId() {
        return prodId;
    }

    /**
     * 设置 理财产品代码
     * @param prodId 理财产品代码
     */
    public void setProdId(String prodId) {
        this.prodId = prodId == null ? null : prodId.trim();
    }

    /**
     * 取得 上级汇总交易编号
     * @return 上级汇总交易编号
     */
    public String getpTotleTradeId() {
        return pTotleTradeId;
    }

    /**
     * 设置 上级汇总交易编号
     * @param pTotleTradeId 上级汇总交易编号
     */
    public void setpTotleTradeId(String pTotleTradeId) {
        this.pTotleTradeId = pTotleTradeId == null ? null : pTotleTradeId.trim();
    }

    /**
     * 取得 备注
     * @return 备注
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 设置 备注
     * @param remark 备注
     */
    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    /**
     * 取得 本期认购
     * @return 本期认购
     */
    public BigDecimal getCurrentSub() {
        return currentSub;
    }

    /**
     * 设置 本期认购
     * @param currentSub 本期认购
     */
    public void setCurrentSub(BigDecimal currentSub) {
        this.currentSub = currentSub;
    }

    /**
     * 取得 上期余额
     * @return 上期余额
     */
    public BigDecimal getLastAmt() {
        return lastAmt;
    }

    /**
     * 设置 上期余额
     * @param lastAmt 上期余额
     */
    public void setLastAmt(BigDecimal lastAmt) {
        this.lastAmt = lastAmt;
    }

    /**
     * 取得 上期赎回
     * @return 上期赎回
     */
    public BigDecimal getLastRed() {
        return lastRed;
    }

    /**
     * 设置 上期赎回
     * @param lastRed 上期赎回
     */
    public void setLastRed(BigDecimal lastRed) {
        this.lastRed = lastRed;
    }

    /**
     * 取得 上期滚入余额
     * @return 上期滚入余额
     */
    public BigDecimal getLastAmtSub() {
        return lastAmtSub;
    }

    /**
     * 设置 上期滚入余额
     * @param lastAmtSub 上期滚入余额
     */
    public void setLastAmtSub(BigDecimal lastAmtSub) {
        this.lastAmtSub = lastAmtSub;
    }

    /**
     * 取得 到账资金
     * @return 到账资金
     */
    public BigDecimal getAccountingAmt() {
        return accountingAmt;
    }

    /**
     * 设置 到账资金
     * @param accountingAmt 到账资金
     */
    public void setAccountingAmt(BigDecimal accountingAmt) {
        this.accountingAmt = accountingAmt;
    }

    /**
     * 取得 赎回收益
     * @return 赎回收益
     */
    public BigDecimal getRedProfit() {
        return redProfit;
    }

    /**
     * 设置 赎回收益
     * @param redProfit 赎回收益
     */
    public void setRedProfit(BigDecimal redProfit) {
        this.redProfit = redProfit;
    }

    /**
     * 取得 赎回成本
     * @return 赎回成本
     */
    public BigDecimal getRedCost() {
        return redCost;
    }

    /**
     * 设置 赎回成本
     * @param redCost 赎回成本
     */
    public void setRedCost(BigDecimal redCost) {
        this.redCost = redCost;
    }

    /**
     * 取得 到期本金
     * @return 到期本金
     */
    public BigDecimal getDqprinAmt() {
        return dqprinAmt;
    }

    /**
     * 设置 到期本金
     * @param dqprinAmt 到期本金
     */
    public void setDqprinAmt(BigDecimal dqprinAmt) {
        this.dqprinAmt = dqprinAmt;
    }

    /**
     * 取得 交付日
     * @return 交付日
     */
    public Date getPayDate() {
        return payDate;
    }

    /**
     * 设置 交付日
     * @param payDate 交付日
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setPayDate(Date payDate) {
        this.payDate = payDate;
    }

    /**
     * 取得 剩余名义本金
     * @return 剩余名义本金
     */
    public BigDecimal getRemainPrin() {
        return remainPrin;
    }

    /**
     * 设置 剩余名义本金
     * @param remainPrin 剩余名义本金
     */
    public void setRemainPrin(BigDecimal remainPrin) {
        this.remainPrin = remainPrin;
    }

    /**
     * 取得 投管业务类型
     * @return 投管业务类型
     */
    public String getTgBusiType() {
        return tgBusiType;
    }

    /**
     * 设置 投管业务类型
     * @param tgBusiType 投管业务类型
     */
    public void setTgBusiType(String tgBusiType) {
        this.tgBusiType = tgBusiType == null ? null : tgBusiType.trim();
    }

    /**
     * 取得 是否逾期交易
     * @return 是否逾期交易
     */
    public String getIsOverdueDeal() {
        return isOverdueDeal;
    }

    /**
     * 设置 是否逾期交易
     * @param isOverdueDeal 是否逾期交易
     */
    public void setIsOverdueDeal(String isOverdueDeal) {
        this.isOverdueDeal = isOverdueDeal == null ? null : isOverdueDeal.trim();
    }

    /**
     * 取得 支付未支付利息
     * @return 支付未支付利息
     */
    public BigDecimal getPayUnpaidintAmt() {
        return payUnpaidintAmt;
    }

    /**
     * 设置 支付未支付利息
     * @param payUnpaidintAmt 支付未支付利息
     */
    public void setPayUnpaidintAmt(BigDecimal payUnpaidintAmt) {
        this.payUnpaidintAmt = payUnpaidintAmt;
    }

    /**
     * 取得 是否转让还本未支付利息
     * @return 是否转让还本未支付利息
     */
    public String getIspayUnpaidint() {
        return ispayUnpaidint;
    }

    /**
     * 设置 是否转让还本未支付利息
     * @param ispayUnpaidint 是否转让还本未支付利息
     */
    public void setIspayUnpaidint(String ispayUnpaidint) {
        this.ispayUnpaidint = ispayUnpaidint == null ? null : ispayUnpaidint.trim();
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
     * 取得 清算类型
     * @return 清算类型
     */
    public String getClearType() {
        return clearType;
    }

    /**
     * 设置 清算类型
     * @param clearType 清算类型
     */
    public void setClearType(String clearType) {
        this.clearType = clearType == null ? null : clearType.trim();
    }

    /**
     * 取得 有效时间
     * @return 有效时间
     */
    public Date getEffTime() {
        return effTime;
    }

    /**
     * 设置 有效时间
     * @param effTime 有效时间
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setEffTime(Date effTime) {
        this.effTime = effTime;
    }

    /**
     * 取得 首期结算方式
     * @return 首期结算方式
     */
    public String getDeliveryType() {
        return deliveryType;
    }

    /**
     * 设置 首期结算方式
     * @param deliveryType 首期结算方式
     */
    public void setDeliveryType(String deliveryType) {
        this.deliveryType = deliveryType == null ? null : deliveryType.trim();
    }

    /**
     * 取得 到期结算方式
     * @return 到期结算方式
     */
    public String getmDeliveryType() {
        return mDeliveryType;
    }

    /**
     * 设置 到期结算方式
     * @param mDeliveryType 到期结算方式
     */
    public void setmDeliveryType(String mDeliveryType) {
        this.mDeliveryType = mDeliveryType == null ? null : mDeliveryType.trim();
    }

    /**
     * 取得 利率
     * @return 利率
     */
    public BigDecimal getRate() {
        return rate;
    }

    /**
     * 设置 利率
     * @param rate 利率
     */
    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    /**
     * 取得 回购期限(天)
     * @return 回购期限(天)
     */
    public Long getRepoTerm() {
        return repoTerm;
    }

    /**
     * 设置 回购期限(天)
     * @param repoTerm 回购期限(天)
     */
    public void setRepoTerm(Long repoTerm) {
        this.repoTerm = repoTerm;
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

    //多级法人改造 add by caiqun 20200615
    /**
     * 机构代码
     * @return
     */
    public String getInstitutionCode() {
        return institutionCode;
    }

    /**
     * 机构代码
     * @param institutionCode
     */
    public void setInstitutionCode(String institutionCode) {
        this.institutionCode = institutionCode;
    }
    //多级法人改造 add by caiqun 20200615


    public String getInstitutionName() {
        return institutionName;
    }

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }

    public String getPsStr() {
        return psStr;
    }

    public void setPsStr(String psStr) {
        this.psStr = psStr;
    }

	public Date getRegDate() {
		return regDate;
	}

	public void setRegDate(Date regDate) {
		this.regDate = regDate;
	}

	public Date getExDevidentDate() {
		return exDevidentDate;
	}

	public void setExDevidentDate(Date exDevidentDate) {
		this.exDevidentDate = exDevidentDate;
	}

	public Date getRedShareListingDate() {
		return redShareListingDate;
	}

	public void setRedShareListingDate(Date redShareListingDate) {
		this.redShareListingDate = redShareListingDate;
	}

	public Date getDevidentDate() {
		return devidentDate;
	}

	public void setDevidentDate(Date devidentDate) {
		this.devidentDate = devidentDate;
	}

	public BigDecimal getPerDevidentAmt() {
		return perDevidentAmt;
	}

	public void setPerDevidentAmt(BigDecimal perDevidentAmt) {
		this.perDevidentAmt = perDevidentAmt;
	}

	public BigDecimal getPerDevidentCash() {
		return perDevidentCash;
	}

	public void setPerDevidentCash(BigDecimal perDevidentCash) {
		this.perDevidentCash = perDevidentCash;
	}

	public BigDecimal getDevidentCash() {
		return devidentCash;
	}

	public void setDevidentCash(BigDecimal devidentCash) {
		this.devidentCash = devidentCash;
	}

	public BigDecimal getDevidentAmt() {
		return devidentAmt;
	}

	public void setDevidentAmt(BigDecimal devidentAmt) {
		this.devidentAmt = devidentAmt;
	}

    public BigDecimal getYesterdayPosition() {
        return yesterdayPosition;
    }

    public void setYesterdayPosition(BigDecimal yesterdayPosition) {
        this.yesterdayPosition = yesterdayPosition;
    }

    public BigDecimal getTradeAmtHk() {
        return tradeAmtHk;
    }

    public void setTradeAmtHk(BigDecimal tradeAmtHk) {
        this.tradeAmtHk = tradeAmtHk;
    }

    public BigDecimal getSellSettleRate() {
        return sellSettleRate;
    }

    public void setSellSettleRate(BigDecimal sellSettleRate) {
        this.sellSettleRate = sellSettleRate;
    }

    public String getIfDividend() {
        return ifDividend;
    }

    public void setIfDividend(String ifDividend) {
        this.ifDividend = ifDividend;
    }

    public String getDividCcy() {
        return dividCcy;
    }

    public void setDividCcy(String dividCcy) {
        this.dividCcy = dividCcy;
    }

    public BigDecimal getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(BigDecimal exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public BigDecimal getReplacePrice() {
        return replacePrice;
    }

    public void setReplacePrice(BigDecimal replacePrice) {
        this.replacePrice = replacePrice;
    }

    public BigDecimal getRegDateAmt() {
        return regDateAmt;
    }

    public void setRegDateAmt(BigDecimal regDateAmt) {
        this.regDateAmt = regDateAmt;
    }

    public String getBusinessModel() {
        return businessModel;
    }

    public void setBusinessModel(String businessModel) {
        this.businessModel = businessModel;
    }
}