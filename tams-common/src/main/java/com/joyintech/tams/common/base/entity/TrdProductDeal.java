package com.joyintech.tams.common.base.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.framework.json.CustomDateDeserialize;
import java.math.BigDecimal;
import java.util.Date;

public class TrdProductDeal extends BaseEntity {
    /***
     * @author  殷亚
     * @description 是否生成结算指令    区别isClean在实体类中实际的意义是:是否结清
     * @date 2021-12-3 11:04
     */
    private String isOrders;

    public String getIsOrders() {
        return isOrders;
    }

    public void setIsOrders(String isOrders) {
        this.isOrders = isOrders;
    }

    //费用类型
	private String feeType;
	
    public String getFeeType() {
		return feeType;
	}

	public void setFeeType(String feeType) {
		this.feeType = feeType;
	}
	
    /**
     * 交易编号
     */
    private String tradeId;

    /**
     * 业务类型，债券交易、产品交易，估值核算专用
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
     * 清算速度
     */
    private Long settleSpeed;

    /**
     * 收付方向
     */
    private String contractId;

    /**
     * 到期金额
     */
    private BigDecimal dqje;

    /**
     * 交易币种
     */
    private String ccy;

    /**
     * 净值日期
     */
    private Date cpriceDate;

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
     * 交易本金，债券券面等
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
     * 交易方向，买入、卖出、收息、还本、申购、赎回等
     */
    private String ps;

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
     * 交易状态，初始、撤销
     */
    private String tradeStatus;

    /**
     * 是否已撤销，默认“否”，在交易发生撤销时更新成“是”
     */
    private String isCancel;

    /**
     * 原交易编号，撤销时存原来的交易编号
     */
    private String pTradeId;

    /**
     * 是否结清，收息付费时用
     */
    private String isClean;

    /**
     * 汇总交易编号
     */
    private String totleTradeId;

    /**
     * 反向交易编号
     */
    private String rTradeId;

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
     * 本方交易员
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
     * 证券管理户代码
     */
    private String secManageAcctId;

    /**
     * 关联系统交易编号
     */
    private String fTradeId;

    /**
     * 现金流代码，还本、付息、付费等时存对应计划的现金流代码
     */
    private String cashId;

    /**
     * 登记托管机构
     */
    private String registOrg;

    /**
     * 备注
     */
    private String remark;

    /**
     * 撤销说明
     */
    private String cancelRemark;

    /**
     * null
     */
    private String deliveryType;

    /**
     * 交付日
     */
    private Date payDate;

    /**
     * 销售代码
     */
    private String saleCode;

    /**
     * 计提份额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal accrNum;

    /**
     * 投管业务类型
     */
    private String tgBusiType;

    /**
     * 席位号
     */
    private String seatId;

    //add by zhangnian 20200304 start
    private String inputValue1;
    private String inputValue2;
    private String inputValue3;
    //add by zhangnian 20200304 end
    
    /**
     * 持有目的 HOLD_PURPOSE 中债新增 20200409
     */
    private String holdPurpose;
    
    /**
     * 交收方式
     */
    private String settlemenType;
    
    /**
     * 多组合交易使用 最大组合交易金额
     */
    private BigDecimal maxTradeamt;

    
    public String getPsPayment() {
        return psPayment;
    }

    public void setPsPayment(String psPayment) {
        this.psPayment = psPayment;
    }

    /**
     * 收付方向
     */
    private String psPayment;
    /**
     * add by xr
     * 组合代码
     */
    private String portfolioId;


    /**
     *业务模式
     */
    private String businessModel;
    /**
     * 指令编号
     */
    private String entrustId;


    public String getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId;
    }
    /**
     * 港股通市场
     */
    private String hkMarket;
    /**
     * 买入结算汇兑比率
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal buySettleRate;
    
    /**
     * 卖出结算汇兑比率
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal sellSettleRate;
    /**
     * 成交金额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal dealAmt;
    /**
     * 佣金
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal commission;
    /**
     * 印花税
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal stampDuty;
    /**
     * 交易征费
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal transactionLevy;
    /**
     * 交易费
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal transactionFee;
    /**
     * 交易系统使用费
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal transactionSystemFee;
    /**
     * 股份交收费
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal sharePaymentFee;
    /**
     * 前一日港股持仓市值
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal yesterdayPosition;

    /**
     * 撤销日期
     */
    private Date cancelDate;


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
     * 取得 业务类型，债券交易、产品交易，估值核算专用
     * @return 业务类型，债券交易、产品交易，估值核算专用
     */
    public String getBusiType() {
        return busiType;
    }

    /**
     * 设置 业务类型，债券交易、产品交易，估值核算专用
     * @param busiType 业务类型，债券交易、产品交易，估值核算专用
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
     * 取得 净值日期
     * @return 净值日期
     */
    public Date getCpriceDate() {
        return cpriceDate;
    }

    /**
     * 设置 净值日期
     * @param cpriceDate 净值日期
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setCpriceDate(Date cpriceDate) {
        this.cpriceDate = cpriceDate;
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
     * 取得 交易本金，债券券面等
     * @return 交易本金，债券券面等
     */
    public BigDecimal getPrinAmt() {
        return prinAmt;
    }

    /**
     * 设置 交易本金，债券券面等
     * @param prinAmt 交易本金，债券券面等
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
     * 取得 交易状态，初始、撤销
     * @return 交易状态，初始、撤销
     */
    public String getTradeStatus() {
        return tradeStatus;
    }

    /**
     * 设置 交易状态，初始、撤销
     * @param tradeStatus 交易状态，初始、撤销
     */
    public void setTradeStatus(String tradeStatus) {
        this.tradeStatus = tradeStatus == null ? null : tradeStatus.trim();
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
     * 取得 原交易编号，撤销时存原来的交易编号
     * @return 原交易编号，撤销时存原来的交易编号
     */
    public String getpTradeId() {
        return pTradeId;
    }

    /**
     * 设置 原交易编号，撤销时存原来的交易编号
     * @param pTradeId 原交易编号，撤销时存原来的交易编号
     */
    public void setpTradeId(String pTradeId) {
        this.pTradeId = pTradeId == null ? null : pTradeId.trim();
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
     * 取得 反向交易编号
     * @return 反向交易编号
     */
    public String getrTradeId() {
        return rTradeId;
    }

    /**
     * 设置 反向交易编号
     * @param rTradeId 反向交易编号
     */
    public void setrTradeId(String rTradeId) {
        this.rTradeId = rTradeId == null ? null : rTradeId.trim();
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
     * 取得 现金流代码，还本、付息、付费等时存对应计划的现金流代码
     * @return 现金流代码，还本、付息、付费等时存对应计划的现金流代码
     */
    public String getCashId() {
        return cashId;
    }

    /**
     * 设置 现金流代码，还本、付息、付费等时存对应计划的现金流代码
     * @param cashId 现金流代码，还本、付息、付费等时存对应计划的现金流代码
     */
    public void setCashId(String cashId) {
        this.cashId = cashId == null ? null : cashId.trim();
    }

    /**
     * 取得 登记托管机构
     * @return 登记托管机构
     */
    public String getRegistOrg() {
        return registOrg;
    }

    /**
     * 设置 登记托管机构
     * @param registOrg 登记托管机构
     */
    public void setRegistOrg(String registOrg) {
        this.registOrg = registOrg == null ? null : registOrg.trim();
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
     * 取得 撤销说明
     * @return 撤销说明
     */
    public String getCancelRemark() {
        return cancelRemark;
    }

    /**
     * 设置 撤销说明
     * @param cancelRemark 撤销说明
     */
    public void setCancelRemark(String cancelRemark) {
        this.cancelRemark = cancelRemark == null ? null : cancelRemark.trim();
    }

    /**
     * 取得 null
     * @return null
     */
    public String getDeliveryType() {
        return deliveryType;
    }

    /**
     * 设置 null
     * @param deliveryType null
     */
    public void setDeliveryType(String deliveryType) {
        this.deliveryType = deliveryType == null ? null : deliveryType.trim();
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
     * 取得 销售代码
     * @return 销售代码
     */
    public String getSaleCode() {
        return saleCode;
    }

    /**
     * 设置 销售代码
     * @param saleCode 销售代码
     */
    public void setSaleCode(String saleCode) {
        this.saleCode = saleCode == null ? null : saleCode.trim();
    }

    /**
     * 取得 计提份额
     * @return 计提份额
     */
    public BigDecimal getAccrNum() {
        return accrNum;
    }

    /**
     * 设置 计提份额
     * @param accrNum 计提份额
     */
    public void setAccrNum(BigDecimal accrNum) {
        this.accrNum = accrNum;
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

    public BigDecimal getDqje() {
        return dqje;
    }

    public void setDqje(BigDecimal dqje) {
        this.dqje = dqje;
    }
     
    public String getInputValue1() {
        return inputValue1;
    }

    public void setInputValue1(String inputValue1) {
        this.inputValue1 = inputValue1;
    }

    public String getInputValue2() {
        return inputValue2;
    }

    public void setInputValue2(String inputValue2) {
        this.inputValue2 = inputValue2;
    }

    public String getInputValue3() {
        return inputValue3;
    }

    public void setInputValue3(String inputValue3) {
        this.inputValue3 = inputValue3;
    }

	/**
	 * @return the holdPurpose
	 */
	public String getHoldPurpose() {
		return holdPurpose;
	}

	/**
	 * @param holdPurpose the holdPurpose to set
	 */
	public void setHoldPurpose(String holdPurpose) {
		this.holdPurpose = holdPurpose;
	}

    public BigDecimal getMaxTradeamt() {
        return maxTradeamt;
    }

    public void setMaxTradeamt(BigDecimal maxTradeamt) {
        this.maxTradeamt = maxTradeamt;
    }
    // 合并by华兴 start
    /**
     * 拆分前交易编号
     */
    private String splitTradeId;

    /**
     * 是否转仓交易
     */
    private String isSwapTransaction;
    //update by xy for huaxin 20200311 start
    /**
     * 是否需要生成转让交易
     */
    private String isNeedAssign;
    /**
     * 待转让本金
     */
    private BigDecimal assignPrinamt;
    /**
     * 待转让利息
     */
    private BigDecimal assignIntamt;
    /**
     * 利息分支序号
     */
    private Long liBranch;

    public String getSplitTradeId() {
        return splitTradeId;
    }

    public void setSplitTradeId(String splitTradeId) {
        this.splitTradeId = splitTradeId;
    }

    public String getIsSwapTransaction() {
        return isSwapTransaction;
    }

    public void setIsSwapTransaction(String isSwapTransaction) {
        this.isSwapTransaction = isSwapTransaction;
    }

    public String getIsNeedAssign() {
        return isNeedAssign;
    }

    public void setIsNeedAssign(String isNeedAssign) {
        this.isNeedAssign = isNeedAssign;
    }

    public BigDecimal getAssignPrinamt() {
        return assignPrinamt;
    }

    public void setAssignPrinamt(BigDecimal assignPrinamt) {
        this.assignPrinamt = assignPrinamt;
    }

    public BigDecimal getAssignIntamt() {
        return assignIntamt;
    }

    public void setAssignIntamt(BigDecimal assignIntamt) {
        this.assignIntamt = assignIntamt;
    }

    public Long getLiBranch() {
        return liBranch;
    }

    public void setLiBranch(Long liBranch) {
        this.liBranch = liBranch;
    }
    // 合并by华兴 end


    public String getSeatId() {
        return seatId;
    }

    public void setSeatId(String seatId) {
        this.seatId = seatId;
    }

	public String getSettlemenType() {
		return settlemenType;
	}

	public void setSettlemenType(String settlemenType) {
		this.settlemenType = settlemenType;
	}

    public BigDecimal getYesterdayPosition() {
        return yesterdayPosition;
    }

    public void setYesterdayPosition(BigDecimal yesterdayPosition) {
        this.yesterdayPosition = yesterdayPosition;
    }

    public String getHkMarket() {
        return hkMarket;
    }

    public void setHkMarket(String hkMarket) {
        this.hkMarket = hkMarket;
    }

    public BigDecimal getBuySettleRate() {
        return buySettleRate;
    }

    public void setBuySettleRate(BigDecimal buySettleRate) {
        this.buySettleRate = buySettleRate;
    }

    public BigDecimal getSellSettleRate() {
        return sellSettleRate;
    }

    public void setSellSettleRate(BigDecimal sellSettleRate) {
        this.sellSettleRate = sellSettleRate;
    }

    public BigDecimal getDealAmt() {
        return dealAmt;
    }

    public void setDealAmt(BigDecimal dealAmt) {
        this.dealAmt = dealAmt;
    }

    public BigDecimal getCommission() {
        return commission;
    }

    public void setCommission(BigDecimal commission) {
        this.commission = commission;
    }

    public BigDecimal getStampDuty() {
        return stampDuty;
    }

    public void setStampDuty(BigDecimal stampDuty) {
        this.stampDuty = stampDuty;
    }

    public BigDecimal getTransactionLevy() {
        return transactionLevy;
    }

    public void setTransactionLevy(BigDecimal transactionLevy) {
        this.transactionLevy = transactionLevy;
    }

    public BigDecimal getTransactionFee() {
        return transactionFee;
    }

    public void setTransactionFee(BigDecimal transactionFee) {
        this.transactionFee = transactionFee;
    }

    public BigDecimal getTransactionSystemFee() {
        return transactionSystemFee;
    }

    public void setTransactionSystemFee(BigDecimal transactionSystemFee) {
        this.transactionSystemFee = transactionSystemFee;
    }

    public BigDecimal getSharePaymentFee() {
        return sharePaymentFee;
    }

    public String getBusinessModel() {
        return businessModel;
    }

    public void setBusinessModel(String businessModel) {
        this.businessModel = businessModel;
    }

    public void setSharePaymentFee(BigDecimal sharePaymentFee) {
        this.sharePaymentFee = sharePaymentFee;
    }

    public String getEntrustId() {
        return entrustId;
    }

    public void setEntrustId(String entrustId) {
        this.entrustId = entrustId;
    }

    public Date getCancelDate() {
        return cancelDate;
    }

    public void setCancelDate(Date cancelDate) {
        this.cancelDate = cancelDate;
    }
}