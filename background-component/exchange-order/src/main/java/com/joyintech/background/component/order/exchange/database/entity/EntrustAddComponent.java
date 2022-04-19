package com.joyintech.background.component.order.exchange.database.entity;

import com.joyintech.background.component.common.pack.common.base.BaseEntity;

import java.math.BigDecimal;
import java.util.Date;

public class EntrustAddComponent extends BaseEntity {

    /**
     * 序列号
     */
    private static final long serialVersionUID = -8827296119940593718L;

    /**
     * 委托代码
     */
    private String entrustId;

    /**
     * 清算速度
     */
    private Long settleSpreed;

    /**
     * 行权收益率
     */
    private BigDecimal exerYield;

    /**
     * 到期收益率
     */
    private BigDecimal matureYield;

    /**
     * 起息日
     */
    private Date vdate;

    /**
     * 到期日
     */
    private Date mdate;

    /**
     * 期限天数，到期日-起息日
     */
    private Long termDays;

    /**
     * 期限天数2，输入期限范围时使用
     */
    private Long termDays2;

    /**
     * 期限品种，1天、7天、14天、21天、1个月、2个月、3个月、4个月、6个月、9个月、1年
     */
    private String termType;

    /**
     * 付息频率
     */
    private String payFreq;

    /**
     * 首次付息日
     */
    private Date firstCalDate;

    /**
     * 资产类型，对应金融产品分类的分类8
     */
    private String assetType;

    /**
     * 资产一级分类，委托页面的债券一级分类
     */
    private String assetType1;

    /**
     * 回购/拆借方向，正回购、逆回购、拆入、拆出
     */
    private String busiPs;

    /**
     * 资金可用日
     */
    private Date cashUsedate;

    /**
     * 证券账户代码
     */
    private String secAcctId;

    /**
     * 质押券置换安排
     */
    private String exchangePledge;

    /**
     * 到期日2
     */
    private Date mdate2;

    /**
     * 个性分类三
     */
    private String assetType10;

    /**
     * 个性分类四
     */
    private String assetType11;

    /**
     * 交易方式
     */
    private String dealMode;

    /**
     * 申购方式
     */
    private String purchModle;

    /**
     * 申购类型
     */
    private String purchType;

    /**
     * 高障碍价
     */
    private BigDecimal highHindPrice;

    /**
     * 低障碍价
     */
    private BigDecimal lowHindPrice;

    /**
     * 高行权价
     */
    private BigDecimal highExercPrice;

    /**
     * 低行权价
     */
    private BigDecimal lowExercPrice;

    /**
     * 执行价
     */
    private BigDecimal exePrice;

    /**
     * 敲出价
     */
    private BigDecimal knockPrice;

    /**
     * 行权方式
     */
    private String exerciseType;

    /**
     * 行权基准日
     */
    private Date execBaseDate;

    /**
     * 期权费率
     */
    private String optFeePayType2;

    /**
     * 期权费支付类型
     */
    private String optFeePayType;

    /**
     * null
     */
    private String dealCurAcct;

    /**
     * null
     */
    private BigDecimal expConNum;

    /**
     * 计算方式，净价，全价，到期收益率
     */
    private String calMode;

    /**
     * 席位号
     */
    private String counterSeat;

    /**
     * 约定号
     */
    private String counterConNo;

    /**
     * 全价
     */
    private BigDecimal fullPrice;

    /**
     * 目标类型
     */
    private String targetType;

    /**
     * 对手方交易商
     */
    private String counterOrg;

    /**
     * 本方交易员
     */
    private String trader;
    /**
     * 首次交收日
     */
    private Date vpayDate;
    /**
     * 到期交收日
     */
    private Date mpayDate;
    /**
     * 到期应计利息
     */
    private BigDecimal mIntAmt;
    /**
     * 到期结算金额
     */
    private BigDecimal mTradeAmt;
    /**
     * 占款天数
     */
    private BigDecimal capDays;
    /**
     * 成交编号/初始交易编号
     */
    private String tradeId;
    /**
     * 请求号
     */
    private String requestId;
    
    /**
     * 衍生品买卖方向
     */
    private String derivativeBsDir;
    
    /**
     * 衍生品开平仓标志
     */
    private String derivativeOcFlag;

    private String redoType;

    /**
     * 指令创建来源
     */
    private String entrustCreateSource;
    /**
     * 指令下达时间
     */
    private Date transmitTime;
    /**
     * 投管业务类型
     */
    private String busiTypePosition;
    /**
     *  计价方式
     */
    private String priceType;

    /**
     * 取得 委托代码
     * @return 委托代码
     */
    public String getEntrustId() {
        return entrustId;
    }

    /**
     * 设置 委托代码
     * @param entrustId 委托代码
     */
    public void setEntrustId(String entrustId) {
        this.entrustId = entrustId == null ? null : entrustId.trim();
    }

    /**
     * 取得 清算速度
     * @return 清算速度
     */
    public Long getSettleSpreed() {
        return settleSpreed;
    }

    /**
     * 设置 清算速度
     * @param settleSpreed 清算速度
     */
    public void setSettleSpreed(Long settleSpreed) {
        this.settleSpreed = settleSpreed;
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
     * 取得 到期收益率
     * @return 到期收益率
     */
    public BigDecimal getMatureYield() {
        return matureYield;
    }

    /**
     * 设置 到期收益率
     * @param matureYield 到期收益率
     */
    public void setMatureYield(BigDecimal matureYield) {
        this.matureYield = matureYield;
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
    public void setMdate(Date mdate) {
        this.mdate = mdate;
    }

    /**
     * 取得 期限天数，到期日-起息日
     * @return 期限天数，到期日-起息日
     */
    public Long getTermDays() {
        return termDays;
    }

    /**
     * 设置 期限天数，到期日-起息日
     * @param termDays 期限天数，到期日-起息日
     */
    public void setTermDays(Long termDays) {
        this.termDays = termDays;
    }

    /**
     * 取得 期限天数2，输入期限范围时使用
     * @return 期限天数2，输入期限范围时使用
     */
    public Long getTermDays2() {
        return termDays2;
    }

    /**
     * 设置 期限天数2，输入期限范围时使用
     * @param termDays2 期限天数2，输入期限范围时使用
     */
    public void setTermDays2(Long termDays2) {
        this.termDays2 = termDays2;
    }

    /**
     * 取得 期限品种，1天、7天、14天、21天、1个月、2个月、3个月、4个月、6个月、9个月、1年
     * @return 期限品种，1天、7天、14天、21天、1个月、2个月、3个月、4个月、6个月、9个月、1年
     */
    public String getTermType() {
        return termType;
    }

    /**
     * 设置 期限品种，1天、7天、14天、21天、1个月、2个月、3个月、4个月、6个月、9个月、1年
     * @param termType 期限品种，1天、7天、14天、21天、1个月、2个月、3个月、4个月、6个月、9个月、1年
     */
    public void setTermType(String termType) {
        this.termType = termType == null ? null : termType.trim();
    }

    /**
     * 取得 付息频率
     * @return 付息频率
     */
    public String getPayFreq() {
        return payFreq;
    }

    /**
     * 设置 付息频率
     * @param payFreq 付息频率
     */
    public void setPayFreq(String payFreq) {
        this.payFreq = payFreq == null ? null : payFreq.trim();
    }

    /**
     * 取得 首次付息日
     * @return 首次付息日
     */
    public Date getFirstCalDate() {
        return firstCalDate;
    }

    /**
     * 设置 首次付息日
     * @param firstCalDate 首次付息日
     */
    public void setFirstCalDate(Date firstCalDate) {
        this.firstCalDate = firstCalDate;
    }

    /**
     * 取得 资产类型，对应金融产品分类的分类8
     * @return 资产类型，对应金融产品分类的分类8
     */
    public String getAssetType() {
        return assetType;
    }

    /**
     * 设置 资产类型，对应金融产品分类的分类8
     * @param assetType 资产类型，对应金融产品分类的分类8
     */
    public void setAssetType(String assetType) {
        this.assetType = assetType == null ? null : assetType.trim();
    }

    /**
     * 取得 资产一级分类，委托页面的债券一级分类
     * @return 资产一级分类，委托页面的债券一级分类
     */
    public String getAssetType1() {
        return assetType1;
    }

    /**
     * 设置 资产一级分类，委托页面的债券一级分类
     * @param assetType1 资产一级分类，委托页面的债券一级分类
     */
    public void setAssetType1(String assetType1) {
        this.assetType1 = assetType1 == null ? null : assetType1.trim();
    }

    /**
     * 取得 回购/拆借方向，正回购、逆回购、拆入、拆出
     * @return 回购/拆借方向，正回购、逆回购、拆入、拆出
     */
    public String getBusiPs() {
        return busiPs;
    }

    /**
     * 设置 回购/拆借方向，正回购、逆回购、拆入、拆出
     * @param busiPs 回购/拆借方向，正回购、逆回购、拆入、拆出
     */
    public void setBusiPs(String busiPs) {
        this.busiPs = busiPs == null ? null : busiPs.trim();
    }

    /**
     * 取得 资金可用日
     * @return 资金可用日
     */
    public Date getCashUsedate() {
        return cashUsedate;
    }

    /**
     * 设置 资金可用日
     * @param cashUsedate 资金可用日
     */
    public void setCashUsedate(Date cashUsedate) {
        this.cashUsedate = cashUsedate;
    }

    /**
     * 取得 证券账户代码
     * @return 证券账户代码
     */
    public String getSecAcctId() {
        return secAcctId;
    }

    /**
     * 设置 证券账户代码
     * @param secAcctId 证券账户代码
     */
    public void setSecAcctId(String secAcctId) {
        this.secAcctId = secAcctId == null ? null : secAcctId.trim();
    }

    /**
     * 取得 质押券置换安排
     * @return 质押券置换安排
     */
    public String getExchangePledge() {
        return exchangePledge;
    }

    /**
     * 设置 质押券置换安排
     * @param exchangePledge 质押券置换安排
     */
    public void setExchangePledge(String exchangePledge) {
        this.exchangePledge = exchangePledge == null ? null : exchangePledge.trim();
    }

    /**
     * 取得 到期日2
     * @return 到期日2
     */
    public Date getMdate2() {
        return mdate2;
    }

    /**
     * 设置 到期日2
     * @param mdate2 到期日2
     */
    public void setMdate2(Date mdate2) {
        this.mdate2 = mdate2;
    }

    /**
     * 取得 个性分类三
     * @return 个性分类三
     */
    public String getAssetType10() {
        return assetType10;
    }

    /**
     * 设置 个性分类三
     * @param assetType10 个性分类三
     */
    public void setAssetType10(String assetType10) {
        this.assetType10 = assetType10 == null ? null : assetType10.trim();
    }

    /**
     * 取得 个性分类四
     * @return 个性分类四
     */
    public String getAssetType11() {
        return assetType11;
    }

    /**
     * 设置 个性分类四
     * @param assetType11 个性分类四
     */
    public void setAssetType11(String assetType11) {
        this.assetType11 = assetType11 == null ? null : assetType11.trim();
    }

    /**
     * 取得 交易方式
     * @return 交易方式
     */
    public String getDealMode() {
        return dealMode;
    }

    /**
     * 设置 交易方式
     * @param dealMode 交易方式
     */
    public void setDealMode(String dealMode) {
        this.dealMode = dealMode == null ? null : dealMode.trim();
    }

    /**
     * 取得 申购方式
     * @return 申购方式
     */
    public String getPurchModle() {
        return purchModle;
    }

    /**
     * 设置 申购方式
     * @param purchModle 申购方式
     */
    public void setPurchModle(String purchModle) {
        this.purchModle = purchModle == null ? null : purchModle.trim();
    }

    /**
     * 取得 申购类型
     * @return 申购类型
     */
    public String getPurchType() {
        return purchType;
    }

    /**
     * 设置 申购类型
     * @param purchType 申购类型
     */
    public void setPurchType(String purchType) {
        this.purchType = purchType == null ? null : purchType.trim();
    }

    /**
     * 取得 高障碍价
     * @return 高障碍价
     */
    public BigDecimal getHighHindPrice() {
        return highHindPrice;
    }

    /**
     * 设置 高障碍价
     * @param highHindPrice 高障碍价
     */
    public void setHighHindPrice(BigDecimal highHindPrice) {
        this.highHindPrice = highHindPrice;
    }

    /**
     * 取得 低障碍价
     * @return 低障碍价
     */
    public BigDecimal getLowHindPrice() {
        return lowHindPrice;
    }

    /**
     * 设置 低障碍价
     * @param lowHindPrice 低障碍价
     */
    public void setLowHindPrice(BigDecimal lowHindPrice) {
        this.lowHindPrice = lowHindPrice;
    }

    /**
     * 取得 高行权价
     * @return 高行权价
     */
    public BigDecimal getHighExercPrice() {
        return highExercPrice;
    }

    /**
     * 设置 高行权价
     * @param highExercPrice 高行权价
     */
    public void setHighExercPrice(BigDecimal highExercPrice) {
        this.highExercPrice = highExercPrice;
    }

    /**
     * 取得 低行权价
     * @return 低行权价
     */
    public BigDecimal getLowExercPrice() {
        return lowExercPrice;
    }

    /**
     * 设置 低行权价
     * @param lowExercPrice 低行权价
     */
    public void setLowExercPrice(BigDecimal lowExercPrice) {
        this.lowExercPrice = lowExercPrice;
    }

    /**
     * 取得 执行价
     * @return 执行价
     */
    public BigDecimal getExePrice() {
        return exePrice;
    }

    /**
     * 设置 执行价
     * @param exePrice 执行价
     */
    public void setExePrice(BigDecimal exePrice) {
        this.exePrice = exePrice;
    }

    /**
     * 取得 敲出价
     * @return 敲出价
     */
    public BigDecimal getKnockPrice() {
        return knockPrice;
    }

    /**
     * 设置 敲出价
     * @param knockPrice 敲出价
     */
    public void setKnockPrice(BigDecimal knockPrice) {
        this.knockPrice = knockPrice;
    }

    /**
     * 取得 行权方式
     * @return 行权方式
     */
    public String getExerciseType() {
        return exerciseType;
    }

    /**
     * 设置 行权方式
     * @param exerciseType 行权方式
     */
    public void setExerciseType(String exerciseType) {
        this.exerciseType = exerciseType == null ? null : exerciseType.trim();
    }

    /**
     * 取得 行权基准日
     * @return 行权基准日
     */
    public Date getExecBaseDate() {
        return execBaseDate;
    }

    /**
     * 设置 行权基准日
     * @param execBaseDate 行权基准日
     */
    public void setExecBaseDate(Date execBaseDate) {
        this.execBaseDate = execBaseDate;
    }

    /**
     * 取得 期权费率
     * @return 期权费率
     */
    public String getOptFeePayType2() {
        return optFeePayType2;
    }

    /**
     * 设置 期权费率
     * @param optFeePayType2 期权费率
     */
    public void setOptFeePayType2(String optFeePayType2) {
        this.optFeePayType2 = optFeePayType2 == null ? null : optFeePayType2.trim();
    }

    /**
     * 取得 期权费支付类型
     * @return 期权费支付类型
     */
    public String getOptFeePayType() {
        return optFeePayType;
    }

    /**
     * 设置 期权费支付类型
     * @param optFeePayType 期权费支付类型
     */
    public void setOptFeePayType(String optFeePayType) {
        this.optFeePayType = optFeePayType == null ? null : optFeePayType.trim();
    }

    /**
     * 取得 null
     * @return null
     */
    public String getDealCurAcct() {
        return dealCurAcct;
    }

    /**
     * 设置 null
     * @param dealCurAcct null
     */
    public void setDealCurAcct(String dealCurAcct) {
        this.dealCurAcct = dealCurAcct == null ? null : dealCurAcct.trim();
    }

    /**
     * 取得 null
     * @return null
     */
    public BigDecimal getExpConNum() {
        return expConNum;
    }

    /**
     * 设置 null
     * @param expConNum null
     */
    public void setExpConNum(BigDecimal expConNum) {
        this.expConNum = expConNum;
    }

    /**
     * 取得 计算方式，净价，全价，到期收益率
     * @return 计算方式，净价，全价，到期收益率
     */
    public String getCalMode() {
        return calMode;
    }

    /**
     * 设置 计算方式，净价，全价，到期收益率
     * @param calMode 计算方式，净价，全价，到期收益率
     */
    public void setCalMode(String calMode) {
        this.calMode = calMode == null ? null : calMode.trim();
    }

    /**
     * 取得 席位号
     * @return 席位号
     */
    public String getCounterSeat() {
        return counterSeat;
    }

    /**
     * 设置 席位号
     * @param counterSeat 席位号
     */
    public void setCounterSeat(String counterSeat) {
        this.counterSeat = counterSeat == null ? null : counterSeat.trim();
    }

    /**
     * 取得 约定号
     * @return 约定号
     */
    public String getCounterConNo() {
        return counterConNo;
    }

    /**
     * 设置 约定号
     * @param counterConNo 约定号
     */
    public void setCounterConNo(String counterConNo) {
        this.counterConNo = counterConNo == null ? null : counterConNo.trim();
    }

    /**
     * 取得 全价
     * @return 全价
     */
    public BigDecimal getFullPrice() {
        return fullPrice;
    }

    /**
     * 设置 全价
     * @param fullPrice 全价
     */
    public void setFullPrice(BigDecimal fullPrice) {
        this.fullPrice = fullPrice;
    }

    /**
     * 取得 目标类型
     * @return 目标类型
     */
    public String getTargetType() {
        return targetType;
    }

    /**
     * 设置 目标类型
     * @param targetType 目标类型
     */
    public void setTargetType(String targetType) {
        this.targetType = targetType == null ? null : targetType.trim();
    }

    /**
     * 取得 对手方交易商
     * @return 对手方交易商
     */
    public String getCounterOrg() {
        return counterOrg;
    }

    /**
     * 设置 对手方交易商
     * @param counterOrg 对手方交易商
     */
    public void setCounterOrg(String counterOrg) {
        this.counterOrg = counterOrg == null ? null : counterOrg.trim();
    }

    /**
     * 取得 本方交易员
     * @return 本方交易员
     */
    public String getTrader() {
        return trader;
    }

    /**
     * 设置 本方交易员
     * @param trader 本方交易员
     */
    public void setTrader(String trader) {
        this.trader = trader == null ? null : trader.trim();
    }

	public Date getVpayDate() {
		return vpayDate;
	}

	public void setVpayDate(Date vpayDate) {
		this.vpayDate = vpayDate;
	}

	public Date getMpayDate() {
		return mpayDate;
	}

	public void setMpayDate(Date mpayDate) {
		this.mpayDate = mpayDate;
	}

	public BigDecimal getmIntAmt() {
		return mIntAmt;
	}

	public void setmIntAmt(BigDecimal mIntAmt) {
		this.mIntAmt = mIntAmt;
	}

	public BigDecimal getmTradeAmt() {
		return mTradeAmt;
	}

	public void setmTradeAmt(BigDecimal mTradeAmt) {
		this.mTradeAmt = mTradeAmt;
	}

	public BigDecimal getCapDays() {
		return capDays;
	}

	public void setCapDays(BigDecimal capDays) {
		this.capDays = capDays;
	}

	public String getTradeId() {
		return tradeId;
	}

	public void setTradeId(String tradeId) {
		this.tradeId = tradeId;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getDerivativeBsDir() {
		return derivativeBsDir;
	}

	public void setDerivativeBsDir(String derivativeBsDir) {
		this.derivativeBsDir = derivativeBsDir;
	}

	public String getDerivativeOcFlag() {
		return derivativeOcFlag;
	}

	public void setDerivativeOcFlag(String derivativeOcFlag) {
		this.derivativeOcFlag = derivativeOcFlag;
	}

    public String getRedoType() {
        return redoType;
    }

    public void setRedoType(String redoType) {
        this.redoType = redoType;
    }

    /**
     * 取得 指令创建来源
     * @return 指令创建来源
     */
    public String getEntrustCreateSource() {
        return entrustCreateSource;
    }

    /**
     * 设置 指令创建来源
     * 01：场内簿记
     * 02：场内实时
     * 03：银行间簿记
     * 04：银行间实时
     * @param entrustCreateSource 指令创建来源
     */
    public void setEntrustCreateSource(String entrustCreateSource) {
        this.entrustCreateSource = entrustCreateSource == null ? null : entrustCreateSource.trim();
    }

    public Date getTransmitTime() {
        return transmitTime;
    }

    public void setTransmitTime(Date transmitTime) {
        this.transmitTime = transmitTime;
    }

    public String getBusiTypePosition() {
        return busiTypePosition;
    }

    public void setBusiTypePosition(String busiTypePosition) {
        this.busiTypePosition = busiTypePosition;
    }

    public String getPriceType() {
        return priceType;
    }

    public void setPriceType(String priceType) {
        this.priceType = priceType;
    }
}