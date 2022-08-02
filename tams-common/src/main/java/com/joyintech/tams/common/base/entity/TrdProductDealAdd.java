package com.joyintech.tams.common.base.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.framework.json.CustomDateDeserialize;
import java.math.BigDecimal;
import java.util.Date;

public class TrdProductDealAdd extends BaseEntity {
    /**
     * 交易编号
     */
    private String tradeId;

    /**
     * 投资组合代码
     */
    private String portfolioId;

    /**
     * 自定义现金流类型
     */
    private String customCashType;

    /**
     * 生成方式：接口接入、画面录入、批处理生成
     */
    private String genType;

    /**
     * 预计申购确认日
     */
    private Date purCfmDate;

    /**
     * 预计赎回到账日
     */
    private Date redCfmDate;

    /**
     * 权益登记日
     */
    private Date regDate;

    /**
     * 预计分红日
     */
    private Date bonusCfmDate;

    /**
     * 委托代码
     */
    private String entrustId;

    /**
     * 支付类型，交易方向为分红除权时填写，红利再投、现金分红
     */
    private String payType;

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
     * null
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal regDateAmt;

    /**
     * 主协议金融产品代码
     */
    private String mpFinprodId;

    /**
     * 主协议分支序号
     */
    private Long mpBranch;

    /**
     * 处理模式
     */
    private String dealMode;

    /**
     * 外汇交易类型
     */
    private String excDealType;

    /**
     * 货币对
     */
    private String curPair;

    /**
     * 期限品种
     */
    private String termType;

    /**
     * 折美元金额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal usdAmt;

    /**
     * 我方角色
     */
    private String ourRole;

    /**
     * 报价精度
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal quotPre;

    /**
     * 定价日
     */
    private Date pricedDate;

    /**
     * 定价日即期汇率
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal pricedDateRate;

    /**
     * 差额收付金额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal difPayAmt;

    /**
     * 差额收付币种
     */
    private String difPayCcy;

    /**
     * 差额收付方向
     */
    private String difPs;

    /**
     * 锁定期截止日
     */
    private Date lockMdate;

    /**
     * 是否有锁定期
     */
    private String withLockPeriod;

    /**
     * 差额交割参考汇率
     */
    private String refRate;

    /**
     * 偏离度
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal deviation;

    /**
     * 资产推荐方
     */
    private String assetRecommandOrg;

    /**
     * 预计转股价值
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal expConValue;

    /**
     * 保证金金额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal depositAmt;

    /**
     * 申购方式，网上申购、网下申购
     */
    private String purchModle;

    /**
     * 申购类型，优先配售、普通申购
     */
    private String purchType;

    /**
     * 申购日
     */
    private Date purchDate;

    /**
     * 预计申购缴款日
     */
    private Date purPayDate;

    /**
     * 提前终止利率
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal terminateRate;

    /**
     * 罚息金额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal penaltyIntamt;

    /**
     * 零星股数
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal spoShareNum;

    /**
     * 正股价
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal firstUnitCprice3;

    /**
     * 转股溢价率
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal preb;

    /**
     * 纯债溢价率
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal prec;

    /**
     * 释放状态
     */
    private String releaseStatus;

    /**
     * 是否现金交割
     */
    private String isCashset;

    /**
     * 是否手工释放
     */
    private String isManualRelease;

    /**
     * 转换资产代码
     */
    private String rFinprodId;

    /**
     * 行权状态
     */
    private String exerciseStatus;

    /**
     * 以股代息股本
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal replaceAmt;
    /**
     * 放弃行权日
     */
    private Date cancelExerciseDate;

    /**
     * 本方券交易份额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal ourShareAmt;

    /**
     * 融入券交易份额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal lendinShareAmt;

    /**
     * 本方券净价总额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal ourCpriceAmt;

    /**
     * 融入券净价总额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal lendinCpriceAmt;

    /**
     * 本方券全价总额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal ourFpriceAmt;

    /**
     * 融入券全价总额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal lendinFpriceAmt;

    /**
     * 本方券利息总额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal ourIntAmt;

    /**
     * 融入券利息总额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal lendinIntAmt;

    /**
     * 本方交易商
     */
    private String dealerNo;

    /**
     * 本方交易主体
     */
    private String dealerPartyNo;

    /**
     * 对手方交易商
     */
    private String outDealerNo;

    /**
     * 对手方交易主体
     */
    private String outDealerPartyNo;

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
     * 取得 自定义现金流类型
     * @return 自定义现金流类型
     */
    public String getCustomCashType() {
        return customCashType;
    }

    /**
     * 设置 自定义现金流类型
     * @param customCashType 自定义现金流类型
     */
    public void setCustomCashType(String customCashType) {
        this.customCashType = customCashType == null ? null : customCashType.trim();
    }

    /**
     * 取得 生成方式：接口接入、画面录入、批处理生成
     * @return 生成方式：接口接入、画面录入、批处理生成
     */
    public String getGenType() {
        return genType;
    }

    /**
     * 设置 生成方式：接口接入、画面录入、批处理生成
     * @param genType 生成方式：接口接入、画面录入、批处理生成
     */
    public void setGenType(String genType) {
        this.genType = genType == null ? null : genType.trim();
    }

    /**
     * 取得 预计申购确认日
     * @return 预计申购确认日
     */
    public Date getPurCfmDate() {
        return purCfmDate;
    }

    /**
     * 设置 预计申购确认日
     * @param purCfmDate 预计申购确认日
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setPurCfmDate(Date purCfmDate) {
        this.purCfmDate = purCfmDate;
    }

    /**
     * 取得 预计赎回到账日
     * @return 预计赎回到账日
     */
    public Date getRedCfmDate() {
        return redCfmDate;
    }

    /**
     * 设置 预计赎回到账日
     * @param redCfmDate 预计赎回到账日
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setRedCfmDate(Date redCfmDate) {
        this.redCfmDate = redCfmDate;
    }

    /**
     * 取得 权益登记日
     * @return 权益登记日
     */
    public Date getRegDate() {
        return regDate;
    }

    /**
     * 设置 权益登记日
     * @param regDate 权益登记日
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setRegDate(Date regDate) {
        this.regDate = regDate;
    }

    /**
     * 取得 预计分红日
     * @return 预计分红日
     */
    public Date getBonusCfmDate() {
        return bonusCfmDate;
    }

    /**
     * 设置 预计分红日
     * @param bonusCfmDate 预计分红日
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setBonusCfmDate(Date bonusCfmDate) {
        this.bonusCfmDate = bonusCfmDate;
    }

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
     * 取得 支付类型，交易方向为分红除权时填写，红利再投、现金分红
     * @return 支付类型，交易方向为分红除权时填写，红利再投、现金分红
     */
    public String getPayType() {
        return payType;
    }

    /**
     * 设置 支付类型，交易方向为分红除权时填写，红利再投、现金分红
     * @param payType 支付类型，交易方向为分红除权时填写，红利再投、现金分红
     */
    public void setPayType(String payType) {
        this.payType = payType == null ? null : payType.trim();
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
     * 取得 null
     * @return null
     */
    public BigDecimal getRegDateAmt() {
        return regDateAmt;
    }

    /**
     * 设置 null
     * @param regDateAmt null
     */
    public void setRegDateAmt(BigDecimal regDateAmt) {
        this.regDateAmt = regDateAmt;
    }

    /**
     * 取得 主协议金融产品代码
     * @return 主协议金融产品代码
     */
    public String getMpFinprodId() {
        return mpFinprodId;
    }

    /**
     * 设置 主协议金融产品代码
     * @param mpFinprodId 主协议金融产品代码
     */
    public void setMpFinprodId(String mpFinprodId) {
        this.mpFinprodId = mpFinprodId == null ? null : mpFinprodId.trim();
    }

    /**
     * 取得 主协议分支序号
     * @return 主协议分支序号
     */
    public Long getMpBranch() {
        return mpBranch;
    }

    /**
     * 设置 主协议分支序号
     * @param mpBranch 主协议分支序号
     */
    public void setMpBranch(Long mpBranch) {
        this.mpBranch = mpBranch;
    }

    /**
     * 取得 处理模式
     * @return 处理模式
     */
    public String getDealMode() {
        return dealMode;
    }

    /**
     * 设置 处理模式
     * @param dealMode 处理模式
     */
    public void setDealMode(String dealMode) {
        this.dealMode = dealMode == null ? null : dealMode.trim();
    }

    /**
     * 取得 外汇交易类型
     * @return 外汇交易类型
     */
    public String getExcDealType() {
        return excDealType;
    }

    /**
     * 设置 外汇交易类型
     * @param excDealType 外汇交易类型
     */
    public void setExcDealType(String excDealType) {
        this.excDealType = excDealType == null ? null : excDealType.trim();
    }

    /**
     * 取得 货币对
     * @return 货币对
     */
    public String getCurPair() {
        return curPair;
    }

    /**
     * 设置 货币对
     * @param curPair 货币对
     */
    public void setCurPair(String curPair) {
        this.curPair = curPair == null ? null : curPair.trim();
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
     * 取得 折美元金额
     * @return 折美元金额
     */
    public BigDecimal getUsdAmt() {
        return usdAmt;
    }

    /**
     * 设置 折美元金额
     * @param usdAmt 折美元金额
     */
    public void setUsdAmt(BigDecimal usdAmt) {
        this.usdAmt = usdAmt;
    }

    /**
     * 取得 我方角色
     * @return 我方角色
     */
    public String getOurRole() {
        return ourRole;
    }

    /**
     * 设置 我方角色
     * @param ourRole 我方角色
     */
    public void setOurRole(String ourRole) {
        this.ourRole = ourRole == null ? null : ourRole.trim();
    }

    /**
     * 取得 报价精度
     * @return 报价精度
     */
    public BigDecimal getQuotPre() {
        return quotPre;
    }

    /**
     * 设置 报价精度
     * @param quotPre 报价精度
     */
    public void setQuotPre(BigDecimal quotPre) {
        this.quotPre = quotPre;
    }

    /**
     * 取得 定价日
     * @return 定价日
     */
    public Date getPricedDate() {
        return pricedDate;
    }

    /**
     * 设置 定价日
     * @param pricedDate 定价日
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setPricedDate(Date pricedDate) {
        this.pricedDate = pricedDate;
    }

    /**
     * 取得 定价日即期汇率
     * @return 定价日即期汇率
     */
    public BigDecimal getPricedDateRate() {
        return pricedDateRate;
    }

    /**
     * 设置 定价日即期汇率
     * @param pricedDateRate 定价日即期汇率
     */
    public void setPricedDateRate(BigDecimal pricedDateRate) {
        this.pricedDateRate = pricedDateRate;
    }

    /**
     * 取得 差额收付金额
     * @return 差额收付金额
     */
    public BigDecimal getDifPayAmt() {
        return difPayAmt;
    }

    /**
     * 设置 差额收付金额
     * @param difPayAmt 差额收付金额
     */
    public void setDifPayAmt(BigDecimal difPayAmt) {
        this.difPayAmt = difPayAmt;
    }

    /**
     * 取得 差额收付币种
     * @return 差额收付币种
     */
    public String getDifPayCcy() {
        return difPayCcy;
    }

    /**
     * 设置 差额收付币种
     * @param difPayCcy 差额收付币种
     */
    public void setDifPayCcy(String difPayCcy) {
        this.difPayCcy = difPayCcy == null ? null : difPayCcy.trim();
    }

    /**
     * 取得 差额收付方向
     * @return 差额收付方向
     */
    public String getDifPs() {
        return difPs;
    }

    /**
     * 设置 差额收付方向
     * @param difPs 差额收付方向
     */
    public void setDifPs(String difPs) {
        this.difPs = difPs == null ? null : difPs.trim();
    }

    /**
     * 取得 锁定期截止日
     * @return 锁定期截止日
     */
    public Date getLockMdate() {
        return lockMdate;
    }

    /**
     * 设置 锁定期截止日
     * @param lockMdate 锁定期截止日
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setLockMdate(Date lockMdate) {
        this.lockMdate = lockMdate;
    }

    /**
     * 取得 是否有锁定期
     * @return 是否有锁定期
     */
    public String getWithLockPeriod() {
        return withLockPeriod;
    }

    /**
     * 设置 是否有锁定期
     * @param withLockPeriod 是否有锁定期
     */
    public void setWithLockPeriod(String withLockPeriod) {
        this.withLockPeriod = withLockPeriod == null ? null : withLockPeriod.trim();
    }

    /**
     * 取得 差额交割参考汇率
     * @return 差额交割参考汇率
     */
    public String getRefRate() {
        return refRate;
    }

    /**
     * 设置 差额交割参考汇率
     * @param refRate 差额交割参考汇率
     */
    public void setRefRate(String refRate) {
        this.refRate = refRate == null ? null : refRate.trim();
    }

    /**
     * 取得 偏离度
     * @return 偏离度
     */
    public BigDecimal getDeviation() {
        return deviation;
    }

    /**
     * 设置 偏离度
     * @param deviation 偏离度
     */
    public void setDeviation(BigDecimal deviation) {
        this.deviation = deviation;
    }

    /**
     * 取得 资产推荐方
     * @return 资产推荐方
     */
    public String getAssetRecommandOrg() {
        return assetRecommandOrg;
    }

    /**
     * 设置 资产推荐方
     * @param assetRecommandOrg 资产推荐方
     */
    public void setAssetRecommandOrg(String assetRecommandOrg) {
        this.assetRecommandOrg = assetRecommandOrg == null ? null : assetRecommandOrg.trim();
    }

    /**
     * 取得 预计转股价值
     * @return 预计转股价值
     */
    public BigDecimal getExpConValue() {
        return expConValue;
    }

    /**
     * 设置 预计转股价值
     * @param expConValue 预计转股价值
     */
    public void setExpConValue(BigDecimal expConValue) {
        this.expConValue = expConValue;
    }

    /**
     * 取得 保证金金额
     * @return 保证金金额
     */
    public BigDecimal getDepositAmt() {
        return depositAmt;
    }

    /**
     * 设置 保证金金额
     * @param depositAmt 保证金金额
     */
    public void setDepositAmt(BigDecimal depositAmt) {
        this.depositAmt = depositAmt;
    }

    /**
     * 取得 申购方式，网上申购、网下申购
     * @return 申购方式，网上申购、网下申购
     */
    public String getPurchModle() {
        return purchModle;
    }

    /**
     * 设置 申购方式，网上申购、网下申购
     * @param purchModle 申购方式，网上申购、网下申购
     */
    public void setPurchModle(String purchModle) {
        this.purchModle = purchModle == null ? null : purchModle.trim();
    }

    /**
     * 取得 申购类型，优先配售、普通申购
     * @return 申购类型，优先配售、普通申购
     */
    public String getPurchType() {
        return purchType;
    }

    /**
     * 设置 申购类型，优先配售、普通申购
     * @param purchType 申购类型，优先配售、普通申购
     */
    public void setPurchType(String purchType) {
        this.purchType = purchType == null ? null : purchType.trim();
    }

    /**
     * 取得 申购日
     * @return 申购日
     */
    public Date getPurchDate() {
        return purchDate;
    }

    /**
     * 设置 申购日
     * @param purchDate 申购日
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setPurchDate(Date purchDate) {
        this.purchDate = purchDate;
    }

    /**
     * 取得 预计申购缴款日
     * @return 预计申购缴款日
     */
    public Date getPurPayDate() {
        return purPayDate;
    }

    /**
     * 设置 预计申购缴款日
     * @param purPayDate 预计申购缴款日
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setPurPayDate(Date purPayDate) {
        this.purPayDate = purPayDate;
    }

    /**
     * 取得 提前终止利率
     * @return 提前终止利率
     */
    public BigDecimal getTerminateRate() {
        return terminateRate;
    }

    /**
     * 设置 提前终止利率
     * @param terminateRate 提前终止利率
     */
    public void setTerminateRate(BigDecimal terminateRate) {
        this.terminateRate = terminateRate;
    }

    /**
     * 取得 罚息金额
     * @return 罚息金额
     */
    public BigDecimal getPenaltyIntamt() {
        return penaltyIntamt;
    }

    /**
     * 设置 罚息金额
     * @param penaltyIntamt 罚息金额
     */
    public void setPenaltyIntamt(BigDecimal penaltyIntamt) {
        this.penaltyIntamt = penaltyIntamt;
    }

    /**
     * 取得 零星股数
     * @return 零星股数
     */
    public BigDecimal getSpoShareNum() {
        return spoShareNum;
    }

    /**
     * 设置 零星股数
     * @param spoShareNum 零星股数
     */
    public void setSpoShareNum(BigDecimal spoShareNum) {
        this.spoShareNum = spoShareNum;
    }

    /**
     * 取得 正股价
     * @return 正股价
     */
    public BigDecimal getFirstUnitCprice3() {
        return firstUnitCprice3;
    }

    /**
     * 设置 正股价
     * @param firstUnitCprice3 正股价
     */
    public void setFirstUnitCprice3(BigDecimal firstUnitCprice3) {
        this.firstUnitCprice3 = firstUnitCprice3;
    }

    /**
     * 取得 转股溢价率
     * @return 转股溢价率
     */
    public BigDecimal getPreb() {
        return preb;
    }

    /**
     * 设置 转股溢价率
     * @param preb 转股溢价率
     */
    public void setPreb(BigDecimal preb) {
        this.preb = preb;
    }

    /**
     * 取得 纯债溢价率
     * @return 纯债溢价率
     */
    public BigDecimal getPrec() {
        return prec;
    }

    /**
     * 设置 纯债溢价率
     * @param prec 纯债溢价率
     */
    public void setPrec(BigDecimal prec) {
        this.prec = prec;
    }

    /**
     * 取得 释放状态
     * @return 释放状态
     */
    public String getReleaseStatus() {
        return releaseStatus;
    }

    /**
     * 设置 释放状态
     * @param releaseStatus 释放状态
     */
    public void setReleaseStatus(String releaseStatus) {
        this.releaseStatus = releaseStatus == null ? null : releaseStatus.trim();
    }

    /**
     * 取得 是否现金交割
     * @return 是否现金交割
     */
    public String getIsCashset() {
        return isCashset;
    }

    /**
     * 设置 是否现金交割
     * @param isCashset 是否现金交割
     */
    public void setIsCashset(String isCashset) {
        this.isCashset = isCashset == null ? null : isCashset.trim();
    }

    /**
     * 取得 是否手工释放
     * @return 是否手工释放
     */
    public String getIsManualRelease() {
        return isManualRelease;
    }

    /**
     * 设置 是否手工释放
     * @param isManualRelease 是否手工释放
     */
    public void setIsManualRelease(String isManualRelease) {
        this.isManualRelease = isManualRelease == null ? null : isManualRelease.trim();
    }

    /**
     * 取得 转换资产代码
     * @return 转换资产代码
     */
    public String getrFinprodId() {
        return rFinprodId;
    }

    /**
     * 设置 转换资产代码
     * @param rFinprodId 转换资产代码
     */
    public void setrFinprodId(String rFinprodId) {
        this.rFinprodId = rFinprodId == null ? null : rFinprodId.trim();
    }

    /**
     * 取得 行权状态
     * @return 行权状态
     */
    public String getExerciseStatus() {
        return exerciseStatus;
    }

    /**
     * 设置 行权状态
     * @param exerciseStatus 行权状态
     */
    public void setExerciseStatus(String exerciseStatus) {
        this.exerciseStatus = exerciseStatus == null ? null : exerciseStatus.trim();
    }

    /**
     * 取得 放弃行权日
     * @return 放弃行权日
     */
    public Date getCancelExerciseDate() {
        return cancelExerciseDate;
    }

    /**
     * 设置 放弃行权日
     * @param cancelExerciseDate 放弃行权日
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setCancelExerciseDate(Date cancelExerciseDate) {
        this.cancelExerciseDate = cancelExerciseDate;
    }

    public BigDecimal getReplaceAmt() {
        return replaceAmt;
    }

    public void setReplaceAmt(BigDecimal replaceAmt) {
        this.replaceAmt = replaceAmt;
    }

    public BigDecimal getOurShareAmt() {
        return ourShareAmt;
    }

    public void setOurShareAmt(BigDecimal ourShareAmt) {
        this.ourShareAmt = ourShareAmt;
    }

    public BigDecimal getLendinShareAmt() {
        return lendinShareAmt;
    }

    public void setLendinShareAmt(BigDecimal lendinShareAmt) {
        this.lendinShareAmt = lendinShareAmt;
    }

    public BigDecimal getOurCpriceAmt() {
        return ourCpriceAmt;
    }

    public void setOurCpriceAmt(BigDecimal ourCpriceAmt) {
        this.ourCpriceAmt = ourCpriceAmt;
    }

    public BigDecimal getLendinCpriceAmt() {
        return lendinCpriceAmt;
    }

    public void setLendinCpriceAmt(BigDecimal lendinCpriceAmt) {
        this.lendinCpriceAmt = lendinCpriceAmt;
    }

    public BigDecimal getOurFpriceAmt() {
        return ourFpriceAmt;
    }

    public void setOurFpriceAmt(BigDecimal ourFpriceAmt) {
        this.ourFpriceAmt = ourFpriceAmt;
    }

    public BigDecimal getLendinFpriceAmt() {
        return lendinFpriceAmt;
    }

    public void setLendinFpriceAmt(BigDecimal lendinFpriceAmt) {
        this.lendinFpriceAmt = lendinFpriceAmt;
    }

    public BigDecimal getOurIntAmt() {
        return ourIntAmt;
    }

    public void setOurIntAmt(BigDecimal ourIntAmt) {
        this.ourIntAmt = ourIntAmt;
    }

    public BigDecimal getLendinIntAmt() {
        return lendinIntAmt;
    }

    public void setLendinIntAmt(BigDecimal lendinIntAmt) {
        this.lendinIntAmt = lendinIntAmt;
    }

    public String getDealerNo() {
        return dealerNo;
    }

    public void setDealerNo(String dealerNo) {
        this.dealerNo = dealerNo;
    }

    public String getDealerPartyNo() {
        return dealerPartyNo;
    }

    public void setDealerPartyNo(String dealerPartyNo) {
        this.dealerPartyNo = dealerPartyNo;
    }

    public String getOutDealerNo() {
        return outDealerNo;
    }

    public void setOutDealerNo(String outDealerNo) {
        this.outDealerNo = outDealerNo;
    }

    public String getOutDealerPartyNo() {
        return outDealerPartyNo;
    }

    public void setOutDealerPartyNo(String outDealerPartyNo) {
        this.outDealerPartyNo = outDealerPartyNo;
    }
}