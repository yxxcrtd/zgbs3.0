package com.joyintech.tams.invest.common.dto.period.trdrepoduration;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.tams.common.base.entity.FinTradeProduct;
import com.joyintech.tams.common.base.entity.TrdProductDeal;

import java.math.BigDecimal;

import java.util.Date;

/**
 * @author : 强成西
 * @version V1.0
 * @Project: tams-invest-parent
 * @Package com.joyintech.period.trdbondduration.dto
 * @Description: 页面查询参数
 * @date Date : 2018年09月25日 9:42
 */
public class TrdRepoDurationDto extends BaseEntity {

    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    private static final long serialVersionUID = 3927267615659013082L;
    /**
     * 提前终止利率
     */
    private BigDecimal terminateRate;

    /**
     * 交易结算费
     */
    private BigDecimal actTradeFee;

    /**
     * 罚息金额
     */
    private BigDecimal  penaltyIntamt;
    /**
     * 类型
     */
    private String ps;
    /**
     * 有效状态
     */
    private String statusValid;

    /**
     * 业务编号
     */
    private String busiCode;

    /**
     * 原始交易编号
     */
    private String pTradeId;

    /**
     * 币种
     */
    private String ccy;

    /**
     * 证券管理账户
     */
    private String secManageAcctId;

    /**
     * 计息基础
     */
    private String basis;

    /**
     * 利率
     */
    private BigDecimal intRate;

    /**
     * 交易对手
     */
    private String counterId;

    /**
     * 交易对手名称
     */
    private String counterName;

    /**
     * 对手方交易员
     */
    private String counterTrader;

    /**
     * 交易员
     */
    private String trader;

    /**
     * 合作资产
     */
    private String chlFinprodId;

    private BigDecimal shareAmt;

    public BigDecimal getShareAmt() {
        return shareAmt;
    }

    public void setShareAmt(BigDecimal shareAmt) {
        this.shareAmt = shareAmt;
    }

    public String getChlFinprodId() {
        return chlFinprodId;
    }

    public void setChlFinprodId(String chlFinprodId) {
        this.chlFinprodId = chlFinprodId;
    }

    /**
     * 产品代码
     */
    private String finprodId;

    /**
     * 组合代码
     */
    private String portfolioId;

    /**
     * 实体组合代码
     */
    private String actPortfolioId;

    /**
     * 组合名称
     */
    private String portfolioName;

    /**
     * 回购类型
     */
    private String type6;

    /**
     * 回购方向
     */
    private String finprodType2;

    /**
     * 交易日
     */
    private String tradeDate;

    /**
     * 起息日
     */
    private String vDate;

    /**
     * 到期日
     */
    private String mDate;

    /**
     * 回购期限
     */
    private String term;

    /**
     * 理论本金
     */
    private BigDecimal theoryPrin;

    /**
     * 交易场所
     */
    private String tradeMarket;

    /**
     * 资金到账日
     */
    private String settleDate;
    /**
     * 终止日
     */
    private String  deadDate;
    /**
     * 撤销原因
     */
    private String revokeReason;

    /**
     * 理论利息
     */
    private BigDecimal theoryInt;

    /**
     * 理论金额
     */
    private BigDecimal theoryAmt;

    /**
     * 实际金额
     */
    private BigDecimal tradeAmt;

    /**
     * 备注
     */
    private String remark;

    /**
     * 操作类型
     */
    private String operateType;


    private String issuePartyId;

    // 通道交易 新增 强成西 2019/03/26 start
    /**
     * 通道代码
     */
    private String chlId;
    //通道费 添加 
    /**
     * 通道费
     */
    private BigDecimal channelFee;
    /**
     * 到期结算金额
     */
    private BigDecimal fpriceAmt;
    /**
     * 通道收取方式
     */
    private String collectType;
    /**
     * 交易标的表
     */
    private FinTradeProduct finTradeProduct;
    /**
     * 交易表
     */
    private TrdProductDeal trdProductDeal;
    /**
     * 交易类型
     */
    private String pstype;
    /**
     * 续做到期日
     */
    private String mDatex;
    /**
     * 续做回购期限
     */
    private String termex;

    /**
     * 续做本金
     */
    private BigDecimal theoryPrinex;
    /**
     * 续做利息
     */
    private BigDecimal theoryIntex;
    /**
     * 续做到期结算金额
     */
    private BigDecimal tradeAmtex;

    /**
     * 佣金
     */
    private BigDecimal commissionAmt;

    /**
     * 结算金额
     */
    private BigDecimal setAmt;
    /**
     * 联系方式
     */
    private String counterContact;

    /**
     * 交易本金，债券券面等
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal prinAmt;

    /**
     * 随交易支付费用
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal feeAmtPay;

    /**
     * 总交易费用
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal feeAmt;

    /**
     * 是否结清，收息付费时用
     */
    private String isClean;

    /**
     * 交易平台，银行间、上交所固定收益平台、上交所大宗交易平台、深交所大宗平台、深交所综合协议交易平台、其他等
     */
    private String tradePlat;

    /**
     * 交易市场，一级市场、二级市场
     */
    private String tradeMarketMode;

    private String deliveryType;

    /**
     * 单位利息
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal unitInt;

    /**
     * 清算速度
     */
    private Long settleSpeed;

    /**
     * 委托代码
     */
    private String entrustId;


    public Long getSettleSpeed() {
        return settleSpeed;
    }

    public void setSettleSpeed(Long settleSpeed) {
        this.settleSpeed = settleSpeed;
    }

    public BigDecimal getPrinAmt() {
        return prinAmt;
    }

    public void setPrinAmt(BigDecimal prinAmt) {
        this.prinAmt = prinAmt;
    }

    public BigDecimal getFeeAmtPay() {
        return feeAmtPay;
    }

    public void setFeeAmtPay(BigDecimal feeAmtPay) {
        this.feeAmtPay = feeAmtPay;
    }

    public BigDecimal getFeeAmt() {
        return feeAmt;
    }

    public void setFeeAmt(BigDecimal feeAmt) {
        this.feeAmt = feeAmt;
    }

    public String getIsClean() {
        return isClean;
    }

    public void setIsClean(String isClean) {
        this.isClean = isClean;
    }

    public String getTradePlat() {
        return tradePlat;
    }

    public void setTradePlat(String tradePlat) {
        this.tradePlat = tradePlat;
    }

    public String getTradeMarketMode() {
        return tradeMarketMode;
    }

    public void setTradeMarketMode(String tradeMarketMode) {
        this.tradeMarketMode = tradeMarketMode;
    }

    public String getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(String deliveryType) {
        this.deliveryType = deliveryType;
    }

    public BigDecimal getUnitInt() {
        return unitInt;
    }

    public void setUnitInt(BigDecimal unitInt) {
        this.unitInt = unitInt;
    }

    /**
     * 获取 counterContact
     *
     * @return counterContact
     */
    public String getCounterContact() {
        return counterContact;
    }

    /**
     * 设置 counterContact
     *
     * @param counterContact counterContact
     */
    public void setCounterContact(String counterContact) {
        this.counterContact = counterContact;
    }

    /**
     * 续做交易对手
     */
    private String counterIdex;

    /**
     * 续做交易对手名称
     */
    private String counterNamex;
    /**
     * 续做对手方交易员
     */
    private String counterTraderx;
    /**
     * 续做交易员
     */
    private String traderx;
    /**
     * 分支序号
     */
    private Long branch;

    /**
     * 续做利率
     */
    private BigDecimal intRatex;
    /**
     * 期限品种
     */
    private String termTypeReback;

    /**
     * 佣金是否随交易支付
     */
    private String isPayWithTrade;

    /**
     * 到期续做的通道费
     */
    private BigDecimal chaneFee;
    
    /**
     * 到期交付日 
     */
    private String mpayDate;
    /**
     * 实际占款天数
     */
    private String actAmtDays;

    /**
     * 获取 chaneFee
     *
     * @return chaneFee
     */
    public BigDecimal getChaneFee() {
        return chaneFee;
    }

    /**
     * 设置 chaneFee
     *
     * @param chaneFee chaneFee
     */
    public void setChaneFee(BigDecimal chaneFee) {
        this.chaneFee = chaneFee;
    }

    /**
     * 获取 isPayWithTrade
     *
     * @return isPayWithTrade
     */
    public String getIsPayWithTrade() {
        return isPayWithTrade;
    }

    /**
     * 设置 isPayWithTrade
     *
     * @param isPayWithTrade isPayWithTrade
     */
    public void setIsPayWithTrade(String isPayWithTrade) {
        this.isPayWithTrade = isPayWithTrade;
    }

    /**
     * 获取 termTypeReback
     *
     * @return termTypeReback
     */
    public String getTermTypeReback() {
        return termTypeReback;
    }

    /**
     * 设置 termTypeReback
     *
     * @param termTypeReback termTypeReback
     */
    public void setTermTypeReback(String termTypeReback) {
        this.termTypeReback = termTypeReback;
    }

    /**
     * 获取 pstype
     *
     * @return pstype
     */
    public String getPstype() {
        return pstype;
    }

    /**
     * 设置 pstype
     *
     * @param pstype pstype
     */
    public void setPstype(String pstype) {
        this.pstype = pstype;
    }

    /**
     * 获取 mDatex
     *
     * @return mDatex
     */
    public String getmDatex() {
        return mDatex;
    }

    /**
     * 设置 mDatex
     *
     * @param mDatex mDatex
     */
    public void setmDatex(String mDatex) {
        this.mDatex = mDatex;
    }

    /**
     * 获取 termex
     *
     * @return termex
     */
    public String getTermex() {
        return termex;
    }

    /**
     * 设置 termex
     *
     * @param termex termex
     */
    public void setTermex(String termex) {
        this.termex = termex;
    }

    /**
     * 获取 theoryPrinex
     *
     * @return theoryPrinex
     */
    public BigDecimal getTheoryPrinex() {
        return theoryPrinex;
    }

    /**
     * 设置 theoryPrinex
     *
     * @param theoryPrinex theoryPrinex
     */
    public void setTheoryPrinex(BigDecimal theoryPrinex) {
        this.theoryPrinex = theoryPrinex;
    }

    /**
     * 获取 theoryIntex
     *
     * @return theoryIntex
     */
    public BigDecimal getTheoryIntex() {
        return theoryIntex;
    }

    /**
     * 设置 theoryIntex
     *
     * @param theoryIntex theoryIntex
     */
    public void setTheoryIntex(BigDecimal theoryIntex) {
        this.theoryIntex = theoryIntex;
    }

    /**
     * 获取 tradeAmtex
     *
     * @return tradeAmtex
     */
    public BigDecimal getTradeAmtex() {
        return tradeAmtex;
    }

    /**
     * 设置 tradeAmtex
     *
     * @param tradeAmtex tradeAmtex
     */
    public void setTradeAmtex(BigDecimal tradeAmtex) {
        this.tradeAmtex = tradeAmtex;
    }

    /**
     * 获取 commissionAmt
     *
     * @return commissionAmt
     */
    public BigDecimal getCommissionAmt() {
        return commissionAmt;
    }

    /**
     * 设置 commissionAmt
     *
     * @param commissionAmt commissionAmt
     */
    public void setCommissionAmt(BigDecimal commissionAmt) {
        this.commissionAmt = commissionAmt;
    }

    /**
     * 获取 setAmt
     *
     * @return setAmt
     */
    public BigDecimal getSetAmt() {
        return setAmt;
    }

    /**
     * 设置 setAmt
     *
     * @param setAmt setAmt
     */
    public void setSetAmt(BigDecimal setAmt) {
        this.setAmt = setAmt;
    }

    /**
     * 获取 counterIdex
     *
     * @return counterIdex
     */
    public String getCounterIdex() {
        return counterIdex;
    }

    /**
     * 设置 counterIdex
     *
     * @param counterIdex counterIdex
     */
    public void setCounterIdex(String counterIdex) {
        this.counterIdex = counterIdex;
    }

    /**
     * 获取 counterNamex
     *
     * @return counterNamex
     */
    public String getCounterNamex() {
        return counterNamex;
    }

    /**
     * 设置 counterNamex
     *
     * @param counterNamex counterNamex
     */
    public void setCounterNamex(String counterNamex) {
        this.counterNamex = counterNamex;
    }

    /**
     * 获取 counterTraderx
     *
     * @return counterTraderx
     */
    public String getCounterTraderx() {
        return counterTraderx;
    }

    /**
     * 设置 counterTraderx
     *
     * @param counterTraderx counterTraderx
     */
    public void setCounterTraderx(String counterTraderx) {
        this.counterTraderx = counterTraderx;
    }

    /**
     * 获取 traderx
     *
     * @return traderx
     */
    public String getTraderx() {
        return traderx;
    }

    /**
     * 设置 traderx
     *
     * @param traderx traderx
     */
    public void setTraderx(String traderx) {
        this.traderx = traderx;
    }

    /**
     * 获取 branch
     *
     * @return branch
     */
    public Long getBranch() {
        return branch;
    }

    /**
     * 设置 branch
     *
     * @param branch branch
     */
    public void setBranch(Long branch) {
        this.branch = branch;
    }

    /**
     * 获取 finTradeProduct
     *
     * @return finTradeProduct
     */
    public FinTradeProduct getFinTradeProduct() {
        return finTradeProduct;
    }

    /**
     * 设置 finTradeProduct
     *
     * @param finTradeProduct finTradeProduct
     */
    public void setFinTradeProduct(FinTradeProduct finTradeProduct) {
        this.finTradeProduct = finTradeProduct;
    }

    public String getDeadDate() {
        return deadDate;
    }

    public void setDeadDate(String deadDate) {
        this.deadDate = deadDate;
    }

    public BigDecimal getActTradeFee() {
        return actTradeFee;
    }

    public void setActTradeFee(BigDecimal actTradeFee) {
        this.actTradeFee = actTradeFee;
    }

    public BigDecimal getPenaltyIntamt() {
        return penaltyIntamt;
    }

    public void setPenaltyIntamt(BigDecimal penaltyIntamt) {
        this.penaltyIntamt = penaltyIntamt;
    }

    public String getPs() {
        return ps;
    }

    public void setPs(String ps) {
        this.ps = ps;
    }

    public BigDecimal getTerminateRate() {
        return terminateRate;
    }

    public void setTerminateRate(BigDecimal terminateRate) {
        this.terminateRate = terminateRate;
    }

    /**
     * 获取 chlId
     *
     * @return chlId
     */
    public String getChlId() {
        return chlId;
    }

    /**
     * 设置 chlId
     *
     * @param chlId chlId
     */
    public void setChlId(String chlId) {
        this.chlId = chlId;
    }

    // 通道交易 新增 强成西 2019/03/26 end
    /**
     * 获取 issuePartyId
     *
     * @return issuePartyId
     */
    public String getIssuePartyId() {
        if (issuePartyId == null || "".equals(issuePartyId))
            return "";
        return issuePartyId;
    }

    /**
     * 设置 issuePartyId
     *
     * @param issuePartyId issuePartyId
     */
    public void setIssuePartyId(String issuePartyId) {
        this.issuePartyId = issuePartyId;
    }

    /**
     * 获取 operateType
     *
     * @return operateType
     */
    public String getOperateType() {
        return operateType;
    }

    /**
     * 设置 operateType
     *
     * @param operateType operateType
     */
    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }

    /**
     * 获取 remark
     *
     * @return remark
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 设置 remark
     *
     * @param remark remark
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    /**
     * 获取 secManageAcctId
     *
     * @return secManageAcctId
     */
    public String getSecManageAcctId() {
        return secManageAcctId;
    }

    /**
     * 设置 secManageAcctId
     *
     * @param secManageAcctId secManageAcctId
     */
    public void setSecManageAcctId(String secManageAcctId) {
        this.secManageAcctId = secManageAcctId;
    }

    /**
     * 获取 statusValid
     *
     * @return statusValid
     */
    public String getStatusValid() {
        return statusValid;
    }

    /**
     * 设置 statusValid
     *
     * @param statusValid statusValid
     */
    public void setStatusValid(String statusValid) {
        this.statusValid = statusValid;
    }

    /**
     * 获取 busiCode
     *
     * @return busiCode
     */
    public String getBusiCode() {
        return busiCode;
    }

    /**
     * 设置 busiCode
     *
     * @param busiCode busiCode
     */
    public void setBusiCode(String busiCode) {
        this.busiCode = busiCode;
    }

    /**
     * 获取 ccy
     *
     * @return ccy
     */
    public String getCcy() {
        return ccy;
    }

    /**
     * 设置 ccy
     *
     * @param ccy ccy
     */
    public void setCcy(String ccy) {
        this.ccy = ccy;
    }

    /**
     * 获取 basis
     *
     * @return basis
     */
    public String getBasis() {
        return basis;
    }

    /**
     * 设置 basis
     *
     * @param basis basis
     */
    public void setBasis(String basis) {
        this.basis = basis;
    }

    /**
     * 获取 intRate
     *
     * @return intRate
     */
    public BigDecimal getIntRate() {
        return intRate;
    }

    /**
     * 设置 intRate
     *
     * @param intRate intRate
     */
    public void setIntRate(BigDecimal intRate) {
        this.intRate = intRate;
    }

    /**
     * 获取 counterId
     *
     * @return counterId
     */
    public String getCounterId() {
        return counterId;
    }

    /**
     * 设置 counterId
     *
     * @param counterId counterId
     */
    public void setCounterId(String counterId) {
        this.counterId = counterId;
    }

    /**
     * 获取 trader
     *
     * @return trader
     */
    public String getTrader() {
        return trader;
    }

    /**
     * 设置 trader
     *
     * @param trader trader
     */
    public void setTrader(String trader) {
        this.trader = trader;
    }

    /**
     * 获取 finprodId
     *
     * @return finprodId
     */
    public String getFinprodId() {
        return finprodId;
    }

    /**
     * 设置 finprodId
     *
     * @param finprodId finprodId
     */
    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
    }

    /**
     * 获取 portfolioId
     *
     * @return portfolioId
     */
    public String getPortfolioId() {
        return portfolioId;
    }

    /**
     * 设置 portfolioId
     *
     * @param portfolioId portfolioId
     */
    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId;
    }

    /**
     * 获取 portfolioName
     *
     * @return portfolioName
     */
    public String getPortfolioName() {
        return portfolioName;
    }

    /**
     * 设置 portfolioName
     *
     * @param portfolioName portfolioName
     */
    public void setPortfolioName(String portfolioName) {
        this.portfolioName = portfolioName;
    }

    /**
     * 获取 type6
     *
     * @return type6
     */
    public String getType6() {
        return type6;
    }

    /**
     * 设置 type6
     *
     * @param type6 type6
     */
    public void setType6(String type6) {
        this.type6 = type6;
    }

    /**
     * 获取 finprodType2
     *
     * @return finprodType2
     */
    public String getFinprodType2() {
        return finprodType2;
    }

    /**
     * 设置 finprodType2
     *
     * @param finprodType2 finprodType2
     */
    public void setFinprodType2(String finprodType2) {
        this.finprodType2 = finprodType2;
    }

    /**
     * 获取 tradeDate
     *
     * @return tradeDate
     */
    public String getTradeDate() {
        return tradeDate;
    }

    /**
     * 设置 tradeDate
     *
     * @param tradeDate tradeDate
     */
    public void setTradeDate(String tradeDate) {
        this.tradeDate = tradeDate;
    }

    /**
     * 获取 vDate
     *
     * @return vDate
     */
    public String getvDate() {
        return vDate;
    }

    /**
     * 设置 vDate
     *
     * @param vDate vDate
     */
    public void setvDate(String vDate) {
        this.vDate = vDate;
    }

    /**
     * 获取 mDate
     *
     * @return mDate
     */
    public String getmDate() {
        return mDate;
    }

    /**
     * 设置 mDate
     *
     * @param mDate mDate
     */
    public void setmDate(String mDate) {
        this.mDate = mDate;
    }

    /**
     * 获取 term
     *
     * @return term
     */
    public String getTerm() {
        return term;
    }

    /**
     * 设置 term
     *
     * @param term term
     */
    public void setTerm(String term) {
        this.term = term;
    }

    /**
     * 获取 theoryPrin
     *
     * @return theoryPrin
     */
    public BigDecimal getTheoryPrin() {
        return theoryPrin;
    }

    /**
     * 设置 theoryPrin
     *
     * @param theoryPrin theoryPrin
     */
    public void setTheoryPrin(BigDecimal theoryPrin) {
        this.theoryPrin = theoryPrin;
    }

    /**
     * 获取 tradeMarket
     *
     * @return tradeMarket
     */
    public String getTradeMarket() {
        return tradeMarket;
    }

    /**
     * 设置 tradeMarket
     *
     * @param tradeMarket tradeMarket
     */
    public void setTradeMarket(String tradeMarket) {
        this.tradeMarket = tradeMarket;
    }

    /**
     * 获取 settleDate
     *
     * @return settleDate
     */
    public String getSettleDate() {
        return settleDate;
    }

    /**
     * 设置 settleDate
     *
     * @param settleDate settleDate
     */
    public void setSettleDate(String settleDate) {
        this.settleDate = settleDate;
    }

    /**
     * 获取 theoryInt
     *
     * @return theoryInt
     */
    public BigDecimal getTheoryInt() {
        return theoryInt;
    }

    /**
     * 设置 theoryInt
     *
     * @param theoryInt theoryInt
     */
    public void setTheoryInt(BigDecimal theoryInt) {
        this.theoryInt = theoryInt;
    }

    /**
     * 获取 theoryAmt
     *
     * @return theoryAmt
     */
    public BigDecimal getTheoryAmt() {
        return theoryAmt;
    }

    /**
     * 设置 theoryAmt
     *
     * @param theoryAmt theoryAmt
     */
    public void setTheoryAmt(BigDecimal theoryAmt) {
        this.theoryAmt = theoryAmt;
    }

    /**
     * 获取 tradeAmt
     *
     * @return tradeAmt
     */
    public BigDecimal getTradeAmt() {
        return tradeAmt;
    }

    /**
     * 设置 tradeAmt
     *
     * @param tradeAmt tradeAmt
     */
    public void setTradeAmt(BigDecimal tradeAmt) {
        this.tradeAmt = tradeAmt;
    }

    /**
     * 获取 pTradeId
     *
     * @return pTradeId
     */
    public String getpTradeId() {
        return pTradeId;
    }

    /**
     * 设置 pTradeId
     *
     * @param pTradeId pTradeId
     */
    public void setpTradeId(String pTradeId) {
        this.pTradeId = pTradeId;
    }

    /**
     * 获取 counterTrader
     *
     * @return counterTrader
     */
    public String getCounterTrader() {
        return counterTrader;
    }

    /**
     * 设置 counterTrader
     *
     * @param counterTrader counterTrader
     */
    public void setCounterTrader(String counterTrader) {
        this.counterTrader = counterTrader;
    }

    /**
     * 获取 revokeReason
     *
     * @return revokeReason
     */
    public String getRevokeReason() {
        return revokeReason;
    }

    /**
     * 设置 revokeReason
     *
     * @param revokeReason revokeReason
     */
    public void setRevokeReason(String revokeReason) {
        this.revokeReason = revokeReason;
    }

    /**
     * 获取 counterName
     *
     * @return counterName
     */
    public String getCounterName() {
        if (counterName == null || "".equals(counterName))
            return "";
        return counterName;
    }

    /**
     * 设置 counterName
     *
     * @param counterName counterName
     */
    public void setCounterName(String counterName) {
        this.counterName = counterName;
    }

    /**
     * 获取 actPortfolioId
     *
     * @return actPortfolioId
     */
    public String getActPortfolioId() {
        return actPortfolioId;
    }

    /**
     * 设置 actPortfolioId
     *
     * @param actPortfolioId actPortfolioId
     */
    public void setActPortfolioId(String actPortfolioId) {
        this.actPortfolioId = actPortfolioId;
    }

	public BigDecimal getChannelFee() {
		return channelFee;
	}

	public void setChannelFee(BigDecimal channelFee) {
		this.channelFee = channelFee;
	}

	public BigDecimal getFpriceAmt() {
		return fpriceAmt;
	}

	public void setFpriceAmt(BigDecimal fpriceAmt) {
		this.fpriceAmt = fpriceAmt;
	}

	public String getCollectType() {
		return collectType;
	}

	public void setCollectType(String collectType) {
		this.collectType = collectType;
	}

    /**
     * 获取 trdProductDeal
     *
     * @return trdProductDeal
     */
    public TrdProductDeal getTrdProductDeal() {
        return trdProductDeal;
    }

    /**
     * 设置 trdProductDeal
     *
     * @param trdProductDeal trdProductDeal
     */
    public void setTrdProductDeal(TrdProductDeal trdProductDeal) {
        this.trdProductDeal = trdProductDeal;
    }

    /**
     * 获取 intRatex
     *
     * @return intRatex
     */
    public BigDecimal getIntRatex() {
        return intRatex;
    }

    /**
     * 设置 intRatex
     *
     * @param intRatex intRatex
     */
    public void setIntRatex(BigDecimal intRatex) {
        this.intRatex = intRatex;
    }

	/**
	 * @return the mpayDate
	 */
	public String getMpayDate() {
		return mpayDate;
	}

	/**
	 * @param mpayDate the mpayDate to set
	 */
	public void setMpayDate(String mpayDate) {
		this.mpayDate = mpayDate;
	}

    public String getActAmtDays() {
        return actAmtDays;
    }

    public void setActAmtDays(String actAmtDays) {
        this.actAmtDays = actAmtDays;
    }

    public String getEntrustId() {
        return entrustId;
    }

    public void setEntrustId(String entrustId) {
        this.entrustId = entrustId;
    }
}
