package com.joyintech.tams.valuation.batch.common.dealinfoselectcom.dto;

import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.TrdTradeFeeDetail;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.SubjectMappingConfig;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 金融产品交易信息DTO 〈功能详细描述〉
 *
 * @author wudebao
 * @version 1.0 2018年5月5日
 * @see FinProductDealInfoComDTO
 * @since 1.0
 */
public class FinProductDealInfoComDTO {

    /**
     * 交易编号
     */
    private String tradeId;

    /**
     * 业务类型
     */
    private String busiType;

    /**
     * 金融产品代码(业务明细代码)
     */
    private String finprodId;

    /**
     * 金融产品类型
     */
    private String finprodType;

    /**
     * 交易日
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
     * 交割日
     */
    private Date settleDate;

    /**
     * 交付日
     */
    private Date payDate;

    /**
     * 交易币种
     */
    private String ccy;

    /**
     * 单位净价
     */
    private BigDecimal unitCprice;

    /**
     * 单位利息
     */
    private BigDecimal unitInt;

    /**
     * 单位全价
     */
    private BigDecimal unitFprice;

    /**
     * 单位面值
     */
    private BigDecimal parValue;

    /**
     * 净价总额
     */
    private BigDecimal cpriceAmt;

    /**
     * 交易份额
     */
    private BigDecimal shareAmt;

    /**
     * 交易本金
     */
    private BigDecimal prinAmt;

    /**
     * 利息总额
     */
    private BigDecimal intAmt;

    /**
     * 全价总额
     */
    private BigDecimal fpriceAmt;

    /**
     * 交易费用
     */
    private BigDecimal feeAmt;

    /**
     * 交易费用
     */
    private BigDecimal feeAmtPay;
    /**
     * 交易金额
     */
    private BigDecimal tradeAmt;

    /**
     * 交易方向
     */
    private String ps;

    /**
     * 到期收益率
     */
    private BigDecimal yield;

    /**
     * 行权收益率
     */
    private BigDecimal exerYield;

    /**
     * 投资目的
     */
    private String invAim;

    /**
     * 交易状态
     */
    private String tradeStatus;

    /**
     * 原交易编号
     */
    private String pTradeId;

    /**
     * 是否结清
     */
    private String isClean;

    /**
     * 净值日
     */
    private Date cpriceDate;

    /**
     * 理财产品代码
     */
    private String prodId;

    /**
     * 通道代码
     */
    private String chlId;

    /**
     * 交易对手
     */
    private String counterId;

//    /**
//     * 交易对手类型
//     */
//    private String counterType;
    
    /**
     * 是否外部交易，是、否
     */
    private String isOutTrade;

    /**
     * 交易场所
     */
    private String tradeMarket;

    /**
     * 交易员
     */
    private String trader;

    /**
     * 本方资金账户
     */
    private String myAcctNo;

    /**
     * 客户类型
     */
    private String customerType;

    /**
     * 资金账户类型
     */
    private String acctType;

    /**
     * 账务明细代码
     */
    private String bokDetailId;

    /**
     * 任务代码
     */
    private String taskId;

    /**
     * 任务优先级
     */
    private Long taskPri;

    /**
     * 账套代码
     */
    private String booksetId;

    /**
     * 会计准则
     */
    private String acctStandard;

    /**
     * 账套日期
     */
    private Date booksetDate;

    /**
     * 发生日期
     */
    private Date happenDate;

    /**
     * 入账日期
     */
    private Date bookDate;

    /**
     * 本位币
     */
    private String standardCcy;

    /**
     * 交易费用明细
     */
    private List<TrdTradeFeeDetail> valTradeFeeDetailList = new ArrayList<TrdTradeFeeDetail>();

    /**
     * 一次性费用类型（请勿使用）
     */
    private String feeType;

    /**
     * 是否随交易支付（请勿使用）
     */
    private String isPayWithTrade;

    /**
     * 费用金额（请勿使用）
     */
    private BigDecimal feeAmtDetail;

    /**
     * 分支序号
     */
    private Long branch;

    /**
     * 证券管理户代码
     */
    private String secManageAcctId;

    /**
     * 费用代码（请勿使用）
     */
    private String feeId;
    /**
     * 投资组合代码
     */
    private String portfolioId;
    
    /**
     * 登记托管机构
     */
    private String registOrg;
    /**
     * 特殊核算模型
     */
    private String bokSpecMode;
    /**
     * 重分类投资目的
     */
    private String reclsInvAim;
    
    /**
     * 交易市场，一级市场、二级市场
     */
    private String tradeMarketMode;
    
    /**
     * 主协议金融产品代码
     */
    private String mpFinprodId;
    
    /**
     * 主协议分支序号
     */
    private String mpBranch;
    
    /**
     * 是否作为待摊
     */
    private String isPrepaid;

    /**
     * 计提份额
     */
    private BigDecimal accrNum;
    
    /**
     * 收付方向
     */
    private String psPayment;
    
    // ADD BY CAOGUO ON 20201015 减值相关修改 START
    /**
     * 减值阶段
     */
    private String lossStage;
    /**
     * 汇总交易编号
     */
    private String totleTradeId;
    // ADD BY CAOGUO ON 20201015 END

    // add by weipeng on 20220301 会计引擎升级改造，增加辅助核算项查询 start
    /**
     *  辅助核算项配置信息
     */
    private SubjectMappingConfig subjectMappingConfig;
    // add by weipeng on 20220301 会计引擎升级改造，增加辅助核算项查询 end

    /**
     * 估值方法
     */
    private String valuationMethod;

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
     * 获取 费用代码（请勿使用）
     *
     * @return feeId.
     */
    public String getFeeId() {
        return feeId;
    }

    /**
     * 设置 费用代码（请勿使用）
     *
     * @param feeId 费用代码（请勿使用）
     */
    public void setFeeId(String feeId) {
        this.feeId = feeId;
    }

    /**
     * 获取 分支序号
     *
     * @return 分支序号.
     */
    public Long getBranch() {
        return branch;
    }

    /**
     * 设置 分支序号
     *
     * @param branch 分支序号
     */
    public void setBranch(Long branch) {
        this.branch = branch;
    }

    /**
     * 获取 证券管理户代码
     *
     * @return 证券管理户代码.
     */
    public String getSecManageAcctId() {
        return secManageAcctId;
    }

    /**
     * 设置 证券管理户代码
     *
     * @param secManageAcctId 证券管理户代码
     */
    public void setSecManageAcctId(String secManageAcctId) {
        this.secManageAcctId = secManageAcctId;
    }

    /**
     * 取得 交易费用明细
     *
     * @return 交易费用明细
     */
    public List<TrdTradeFeeDetail> getValTradeFeeDetailList() {
        return valTradeFeeDetailList;
    }

    /**
     * 设置 交易费用明细
     *
     * @param valTradeFeeDetailList 交易费用明细
     */
    public void setValTradeFeeDetailList(List<TrdTradeFeeDetail> valTradeFeeDetailList) {
        this.valTradeFeeDetailList = valTradeFeeDetailList;
    }

    /**
     * 取得 一次性费用类型（请勿使用）
     *
     * @return 一次性费用类型（请勿使用）
     */
    public String getFeeType() {
        return feeType;
    }

    /**
     * 设置 一次性费用类型（请勿使用）
     *
     * @param feeType 一次性费用类型（请勿使用）
     */
    public void setFeeType(String feeType) {
        this.feeType = feeType;
    }

    /**
     * 取得 是否随交易支付（请勿使用）
     *
     * @return 是否随交易支付（请勿使用）
     */
    public String getIsPayWithTrade() {
        return isPayWithTrade;
    }

    /**
     * 设置 是否随交易支付（请勿使用）
     *
     * @param isPayWithTrade 是否随交易支付（请勿使用）
     */
    public void setIsPayWithTrade(String isPayWithTrade) {
        this.isPayWithTrade = isPayWithTrade;
    }

    /**
     * 取得 费用金额（请勿使用）
     *
     * @return 费用金额（请勿使用）
     */
    public BigDecimal getFeeAmtDetail() {
        return feeAmtDetail;
    }

    /**
     * 设置 费用金额（请勿使用）
     *
     * @param feeAmtDetail 费用金额（请勿使用）
     */
    public void setFeeAmtDetail(BigDecimal feeAmtDetail) {
        this.feeAmtDetail = feeAmtDetail;
    }

    /**
     * 取得 交易编号
     *
     * @return 交易编号
     */
    public String getTradeId() {
        return tradeId;
    }

    /**
     * 设置 交易编号
     *
     * @param tradeId 交易编号
     */
    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }

    /**
     * 取得 业务类型
     *
     * @return 业务类型
     */
    public String getBusiType() {
        return busiType;
    }

    /**
     * 设置 业务类型
     *
     * @param busiType 业务类型
     */
    public void setBusiType(String busiType) {
        this.busiType = busiType;
    }

    /**
     * 取得 金融产品代码(业务明细代码)
     *
     * @return 金融产品代码(业务明细代码)
     */
    public String getFinprodId() {
        return finprodId;
    }

    /**
     * 设置 金融产品代码(业务明细代码)
     *
     * @param finprodId 金融产品代码(业务明细代码)
     */
    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
    }

    /**
     * 取得 金融产品类型
     *
     * @return 金融产品类型
     */
    public String getFinprodType() {
        return finprodType;
    }

    /**
     * 设置 金融产品类型
     *
     * @param finprodType 金融产品类型
     */
    public void setFinprodType(String finprodType) {
        this.finprodType = finprodType;
    }

    /**
     * 取得 交易日
     *
     * @return 交易日
     */
    public Date getTradeDate() {
        return tradeDate;
    }

    /**
     * 设置 交易日
     *
     * @param tradeDate 交易日
     */
    public void setTradeDate(Date tradeDate) {
        this.tradeDate = tradeDate;
    }

    /**
     * 取得 起息日
     *
     * @return 起息日
     */
    public Date getVdate() {
        return vdate;
    }

    /**
     * 设置 起息日
     *
     * @param vdate 起息日
     */
    public void setVdate(Date vdate) {
        this.vdate = vdate;
    }

    /**
     * 取得 到期日
     *
     * @return 到期日
     */
    public Date getMdate() {
        return mdate;
    }

    /**
     * 设置 到期日
     *
     * @param mdate 到期日
     */
    public void setMdate(Date mdate) {
        this.mdate = mdate;
    }

    /**
     * 取得 交割日
     *
     * @return 交割日
     */
    public Date getSettleDate() {
        return settleDate;
    }

    /**
     * 设置 交割日
     *
     * @param settleDate 交割日
     */
    public void setSettleDate(Date settleDate) {
        this.settleDate = settleDate;
    }

    /**
     * 取得 交易币种
     *
     * @return 交易币种
     */
    public String getCcy() {
        return ccy;
    }

    /**
     * 设置 交易币种
     *
     * @param ccy 交易币种
     */
    public void setCcy(String ccy) {
        this.ccy = ccy;
    }

    /**
     * 取得 单位净价
     *
     * @return 单位净价
     */
    public BigDecimal getUnitCprice() {
        return unitCprice;
    }

    /**
     * 设置 单位净价
     *
     * @param unitCprice 单位净价
     */
    public void setUnitCprice(BigDecimal unitCprice) {
        this.unitCprice = unitCprice;
    }

    /**
     * 取得 单位利息
     *
     * @return 单位利息
     */
    public BigDecimal getUnitInt() {
        return unitInt;
    }

    /**
     * 设置 单位利息
     *
     * @param unitInt 单位利息
     */
    public void setUnitInt(BigDecimal unitInt) {
        this.unitInt = unitInt;
    }

    /**
     * 取得 单位全价
     *
     * @return 单位全价
     */
    public BigDecimal getUnitFprice() {
        return unitFprice;
    }

    /**
     * 设置 单位全价
     *
     * @param unitFprice 单位全价
     */
    public void setUnitFprice(BigDecimal unitFprice) {
        this.unitFprice = unitFprice;
    }

    /**
     * 取得 单位面值
     *
     * @return 单位面值
     */
    public BigDecimal getParValue() {
        return parValue;
    }

    /**
     * 设置 单位面值
     *
     * @param parValue 单位面值
     */
    public void setParValue(BigDecimal parValue) {
        this.parValue = parValue;
    }

    /**
     * 取得 净价总额
     *
     * @return 净价总额
     */
    public BigDecimal getCpriceAmt() {
        return cpriceAmt;
    }

    /**
     * 设置 净价总额
     *
     * @param cpriceAmt 净价总额
     */
    public void setCpriceAmt(BigDecimal cpriceAmt) {
        this.cpriceAmt = cpriceAmt;
    }

    /**
     * 取得 交易份额
     *
     * @return 交易份额
     */
    public BigDecimal getShareAmt() {
        return shareAmt;
    }

    /**
     * 设置 交易份额
     *
     * @param shareAmt 交易份额
     */
    public void setShareAmt(BigDecimal shareAmt) {
        this.shareAmt = shareAmt;
    }

    /**
     * 取得 交易本金
     *
     * @return 交易本金
     */
    public BigDecimal getPrinAmt() {
        return prinAmt;
    }

    /**
     * 设置 交易本金
     *
     * @param prinAmt 交易本金
     */
    public void setPrinAmt(BigDecimal prinAmt) {
        this.prinAmt = prinAmt;
    }

    /**
     * 取得 利息总额
     *
     * @return 利息总额
     */
    public BigDecimal getIntAmt() {
        return intAmt;
    }

    /**
     * 设置 利息总额
     *
     * @param intAmt 利息总额
     */
    public void setIntAmt(BigDecimal intAmt) {
        this.intAmt = intAmt;
    }

    /**
     * 取得 全价总额
     *
     * @return 全价总额
     */
    public BigDecimal getFpriceAmt() {
        return fpriceAmt;
    }

    /**
     * 设置 全价总额
     *
     * @param fpriceAmt 全价总额
     */
    public void setFpriceAmt(BigDecimal fpriceAmt) {
        this.fpriceAmt = fpriceAmt;
    }

    /**
     * 取得 交易费用
     *
     * @return 交易费用
     */
    public BigDecimal getFeeAmt() {
        return feeAmt;
    }

    /**
     * 设置 交易费用
     *
     * @param feeAmt 交易费用
     */
    public void setFeeAmt(BigDecimal feeAmt) {
        this.feeAmt = feeAmt;
    }

    /**
     * 取得 交易金额
     *
     * @return 交易金额
     */
    public BigDecimal getTradeAmt() {
        return tradeAmt;
    }

    /**
     * 设置 交易金额
     *
     * @param tradeAmt 交易金额
     */
    public void setTradeAmt(BigDecimal tradeAmt) {
        this.tradeAmt = tradeAmt;
    }

    /**
     * 取得 交易方向
     *
     * @return 交易方向
     */
    public String getPs() {
        return ps;
    }

    /**
     * 设置 交易方向
     *
     * @param ps 交易方向
     */
    public void setPs(String ps) {
        this.ps = ps;
    }

    /**
     * 取得 到期收益率
     *
     * @return 到期收益率
     */
    public BigDecimal getYield() {
        return yield;
    }

    /**
     * 设置 到期收益率
     *
     * @param yield 到期收益率
     */
    public void setYield(BigDecimal yield) {
        this.yield = yield;
    }

    /**
     * 取得 行权收益率
     *
     * @return 行权收益率
     */
    public BigDecimal getExerYield() {
        return exerYield;
    }

    /**
     * 设置 行权收益率
     *
     * @param exerYield 行权收益率
     */
    public void setExerYield(BigDecimal exerYield) {
        this.exerYield = exerYield;
    }

    /**
     * 取得 投资目的
     *
     * @return 投资目的
     */
    public String getInvAim() {
        return invAim;
    }

    /**
     * 设置 投资目的
     *
     * @param invAim 投资目的
     */
    public void setInvAim(String invAim) {
        this.invAim = invAim;
    }

    /**
     * 取得 交易状态
     *
     * @return 交易状态
     */
    public String getTradeStatus() {
        return tradeStatus;
    }

    /**
     * 设置 交易状态
     *
     * @param tradeStatus 交易状态
     */
    public void setTradeStatus(String tradeStatus) {
        this.tradeStatus = tradeStatus;
    }

    /**
     * 取得 原交易编号
     *
     * @return 原交易编号
     */
    public String getPTradeId() {
        return pTradeId;
    }

    /**
     * 设置 原交易编号
     *
     * @param pTradeId 原交易编号
     */
    public void setPTradeId(String pTradeId) {
        this.pTradeId = pTradeId;
    }

    /**
     * 取得 是否结清
     *
     * @return 是否结清
     */
    public String getIsClean() {
        return isClean;
    }

    /**
     * 设置 是否结清
     *
     * @param isClean 是否结清
     */
    public void setIsClean(String isClean) {
        this.isClean = isClean;
    }

    /**
     * 取得 净值日
     *
     * @return 净值日
     */
    public Date getCpriceDate() {
        return cpriceDate;
    }

    /**
     * 设置 净值日
     *
     * @param cpriceDate 净值日
     */
    public void setCpriceDate(Date cpriceDate) {
        this.cpriceDate = cpriceDate;
    }

    /**
     * 取得 理财产品代码
     *
     * @return 理财产品代码
     */
    public String getProdId() {
        return prodId;
    }

    /**
     * 设置 理财产品代码
     *
     * @param prodId 理财产品代码
     */
    public void setProdId(String prodId) {
        this.prodId = prodId;
    }

    /**
     * 取得 通道代码
     *
     * @return 通道代码
     */
    public String getChlId() {
        return chlId;
    }

    /**
     * 设置 通道代码
     *
     * @param chlId 通道代码
     */
    public void setChlId(String chlId) {
        this.chlId = chlId;
    }

    /**
     * 取得 交易对手
     *
     * @return 交易对手
     */
    public String getCounterId() {
        return counterId;
    }

    /**
     * 设置 交易对手
     *
     * @param counterId 交易对手
     */
    public void setCounterId(String counterId) {
        this.counterId = counterId;
    }

//    /**
//     * 取得 交易对手类型
//     *
//     * @return 交易对手类型
//     */
//    public String getCounterType() {
//        return counterType;
//    }
//
//    /**
//     * 设置 交易对手类型
//     *
//     * @param counterType 交易对手类型
//     */
//    public void setCounterType(String counterType) {
//        this.counterType = counterType;
//    }
    
    public String getIsOutTrade() {
		return isOutTrade;
	}

	public void setIsOutTrade(String isOutTrade) {
		this.isOutTrade = isOutTrade;
	}

	/**
     * 取得 交易场所
     *
     * @return 交易场所
     */
    public String getTradeMarket() {
        return tradeMarket;
    }

    /**
     * 设置 交易场所
     *
     * @param tradeMarket 交易场所
     */
    public void setTradeMarket(String tradeMarket) {
        this.tradeMarket = tradeMarket;
    }

    /**
     * 取得 交易员
     *
     * @return 交易员
     */
    public String getTrader() {
        return trader;
    }

    /**
     * 设置 交易员
     *
     * @param trader 交易员
     */
    public void setTrader(String trader) {
        this.trader = trader;
    }

    /**
     * 取得 本方资金账户
     *
     * @return 本方资金账户
     */
    public String getMyAcctNo() {
        return myAcctNo;
    }

    /**
     * 设置 本方资金账户
     *
     * @param myAcctNo 本方资金账户
     */
    public void setMyAcctNo(String myAcctNo) {
        this.myAcctNo = myAcctNo;
    }

    /**
     * 取得 客户类型
     *
     * @return 客户类型
     */
    public String getCustomerType() {
        return customerType;
    }

    /**
     * 设置 客户类型
     *
     * @param customerType 客户类型
     */
    public void setCustomerType(String customerType) {
        this.customerType = customerType;
    }

    /**
     * 获取资金账务类型
     *
     * @return acctType.资金账务类型
     */
    public String getAcctType() {
        return acctType;
    }

    /**
     * 设置 资金账务类型
     *
     * @param acctType 资金账务类型
     */
    public void setAcctType(String acctType) {
        this.acctType = acctType;
    }

    /**
     * 取得 账务明细代码
     *
     * @return 账务明细代码
     */
    public String getBokDetailId() {
        return bokDetailId;
    }

    /**
     * 设置 账务明细代码
     *
     * @param bokDetailId 账务明细代码
     */
    public void setBokDetailId(String bokDetailId) {
        this.bokDetailId = bokDetailId;
    }

    /**
     * 取得 任务代码
     *
     * @return 任务代码
     */
    public String getTaskId() {
        return taskId;
    }

    /**
     * 获取 taskPri
     *
     * @return taskPri.
     */
    public Long getTaskPri() {
        return taskPri;
    }

    /**
     * 设置 taskPri
     *
     * @param taskPri taskPri
     */
    public void setTaskPri(Long taskPri) {
        this.taskPri = taskPri;
    }

    /**
     * 设置 任务代码
     *
     * @param taskId 任务代码
     */
    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    /**
     * 取得 账套代码
     *
     * @return 账套代码
     */
    public String getBooksetId() {
        return booksetId;
    }

    /**
     * 设置 账套代码
     *
     * @param booksetId 账套代码
     */
    public void setBooksetId(String booksetId) {
        this.booksetId = booksetId;
    }

    /**
     * 获取 acctStandard
     *
     * @return acctStandard.
     */
    public String getAcctStandard() {
        return acctStandard;
    }

    /**
     * 设置 acctStandard
     *
     * @param acctStandard acctStandard
     */
    public void setAcctStandard(String acctStandard) {
        this.acctStandard = acctStandard;
    }

    /**
     * 取得 账套日期
     *
     * @return 账套日期
     */
    public Date getBooksetDate() {
        return booksetDate;
    }

    /**
     * 设置 账套日期
     *
     * @param booksetDate 账套日期
     */
    public void setBooksetDate(Date booksetDate) {
        this.booksetDate = booksetDate;
    }

    /**
     * 取得 发生日期
     *
     * @return 发生日期
     */
    public Date getHappenDate() {
        return happenDate;
    }

    /**
     * 设置 发生日期
     *
     * @param happenDate 发生日期
     */
    public void setHappenDate(Date happenDate) {
        this.happenDate = happenDate;
    }

    /**
     * 取得 入账日期
     *
     * @return 入账日期
     */
    public Date getBookDate() {
        return bookDate;
    }

    /**
     * 设置 入账日期
     *
     * @param bookDate 入账日期
     */
    public void setBookDate(Date bookDate) {
        this.bookDate = bookDate;
    }

    /**
     * 取得 本位币
     *
     * @return 本位币
     */
    public String getStandardCcy() {
        return standardCcy;
    }

    /**
     * 设置 本位币
     *
     * @param standardCcy 本位币
     */
    public void setStandardCcy(String standardCcy) {
        this.standardCcy = standardCcy;
    }

    
    /**
     * 获取 feeAmtPay
     * @return feeAmtPay.
     */
    public BigDecimal getFeeAmtPay() {
        return feeAmtPay;
    }

    
    /**
     * 设置 feeAmtPay
     * @param feeAmtPay feeAmtPay
     */
    public void setFeeAmtPay(BigDecimal feeAmtPay) {
        this.feeAmtPay = feeAmtPay;
    }

    /**
     * 获取 portfolioId
     * @return portfolioId.
     */
    public String getPortfolioId() {
        return portfolioId;
    }

    /**
     * 设置 portfolioId
     * @param portfolioId portfolioId
     */
    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId;
    }

    
    /**
     * 获取 registOrg
     * @return registOrg.
     */
    public String getRegistOrg() {
        return registOrg;
    }

    
    /**
     * 设置 registOrg
     * @param registOrg registOrg
     */
    public void setRegistOrg(String registOrg) {
        this.registOrg = registOrg;
    }

    
    /**
     * 获取 bokSpecMode
     * @return bokSpecMode.
     */
    public String getBokSpecMode() {
        return bokSpecMode;
    }

    
    /**
     * 设置 bokSpecMode
     * @param bokSpecMode bokSpecMode
     */
    public void setBokSpecMode(String bokSpecMode) {
        this.bokSpecMode = bokSpecMode;
    }

    /**
     * 获取 reclsInvAim
     * @return reclsInvAim.
     */
    public String getReclsInvAim() {
        return reclsInvAim;
    }

    /**
     * 设置 reclsInvAim
     * @param reclsInvAim reclsInvAim
     */
    public void setReclsInvAim(String reclsInvAim) {
        this.reclsInvAim = reclsInvAim;
    }

    /**
     * 获取 mpFinprodId
     * @return mpFinprodId.
     */
    public String getMpFinprodId() {
        return mpFinprodId;
    }

    /**
     * 设置 mpFinprodId
     * @param mpFinprodId mpFinprodId
     */
    public void setMpFinprodId(String mpFinprodId) {
        this.mpFinprodId = mpFinprodId;
    }

    /**
     * 获取 mpBranch
     * @return mpBranch.
     */
    public String getMpBranch() {
        return mpBranch;
    }

    /**
     * 设置 mpBranch
     * @param mpBranch mpBranch
     */
    public void setMpBranch(String mpBranch) {
        this.mpBranch = mpBranch;
    }

    public Date getPayDate() {
        return payDate;
    }

    public void setPayDate(Date payDate) {
        this.payDate = payDate;
    }

    public String getpTradeId() {
        return pTradeId;
    }

    public void setpTradeId(String pTradeId) {
        this.pTradeId = pTradeId;
    }

    /**
     * 获取 是否作为待摊
     * @return 是否作为待摊
     */
    public String getIsPrepaid() {
        return isPrepaid;
    }

    /**
     * 设置 是否作为待摊
     * @param isPrepaid 是否作为待摊
     */
    public void setIsPrepaid(String isPrepaid) {
        this.isPrepaid = isPrepaid;
    }

    /**
     * 获取 计提份额
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
     * 获取 psPayment
     * @return psPayment.
     */
    public String getPsPayment() {
        return psPayment;
    }

    /**
     * 设置 psPayment
     * @param psPayment psPayment
     */
    public void setPsPayment(String psPayment) {
        this.psPayment = psPayment;
    }

    /**
     * 获取 lossStage
     * 
     * @return lossStage.
     */
    public String getLossStage() {
        return lossStage;
    }

    /**
     * 设置 lossStage
     * 
     * @param lossStage lossStage
     */
    public void setLossStage(String lossStage) {
        this.lossStage = lossStage;
    }

    /**
     * 获取 totleTradeId
     * 
     * @return totleTradeId.
     */
    public String getTotleTradeId() {
        return totleTradeId;
    }

    /**
     * 设置 totleTradeId
     * 
     * @param totleTradeId totleTradeId
     */
    public void setTotleTradeId(String totleTradeId) {
        this.totleTradeId = totleTradeId;
    }

    // add by weipeng on 20220301 会计引擎升级改造，增加辅助核算项查询 start
    /**
     * 获取 辅助核算项配置信息
     * @return 辅助核算项配置信息.
     */
    public SubjectMappingConfig getSubjectMappingConfig() {
        return subjectMappingConfig;
    }

    /**
     * 设置 辅助核算项配置信息
     * @param subjectMappingConfig 辅助核算项配置信息
     */
    public void setSubjectMappingConfig(SubjectMappingConfig subjectMappingConfig) {
        this.subjectMappingConfig = subjectMappingConfig;
    }
    // add by weipeng on 20220301 会计引擎升级改造，增加辅助核算项查询 end
    /**
     * 获取 估值方法
     * @return 估值方法
     */
    public String getValuationMethod() {
        return valuationMethod;
    }

    /**
     * 设置 估值方法
     * @param valuationMethod 估值方法
     */
    public void setValuationMethod(String valuationMethod) {
        this.valuationMethod = valuationMethod;
    }

    /**
     * 是否使用摊余成本法
     * @return 是否使用摊余成本法
     */
    public boolean getIsAcMethod(){
        if(StringUtil.equals(DictCodeListExt1.估值方法_摊余成本法.getValue(), this.getValuationMethod())){
            return true;
        }
        else{
            return false;
        }
    }
}
