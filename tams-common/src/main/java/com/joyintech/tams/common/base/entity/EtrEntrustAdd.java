package com.joyintech.tams.common.base.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.framework.json.CustomDateDeserialize;
import java.math.BigDecimal;
import java.util.Date;

public class EtrEntrustAdd extends BaseEntity {
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
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal exerYield;

    /**
     * 到期收益率
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
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
     * null
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal firstUnitCprice;

    /**
     * null
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal firstUnitCprice2;

    /**
     * 质押券置换安排
     */
    private String exchangePledge;

    /**
     * 外汇交易类型
     */
    private String excDealType;

    /**
     * 货币对
     */
    private String curPair;

    /**
     * 对应货币
     */
    private String corCcy;

    /**
     * 预计对应货币金额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal expCorAmt;

    /**
     * 对应货币可用持仓
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal corCurPos;

    /**
     * 是否差额清算
     */
    private String isDifSqu;

    /**
     * 交易货币账户
     */
    private String dealCurAcct;

    /**
     * 对应货币账户
     */
    private String corCurAcct;

    /**
     * 人民币账户
     */
    private String cnyAcct;

    /**
     * 起息日2
     */
    private Date vdate2;

    /**
     * 到期日2
     */
    private Date mdate2;

    /**
     * 掉期期限类型
     */
    private String excFxsTermType;

    /**
     * 缴款方式
     */
    private String payType;

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
     * 正股价
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal sharePrice;

    /**
     * 预计转股价值
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal expConValue;

    /**
     * 实际持有数量
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal actNum;

    /**
     * 预计转换股数
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal expConNum;

    /**
     * 申购方式
     */
    private String purchModle;

    /**
     * 申购类型
     */
    private String purchType;

    /**
     * 是否协议终止，适用于活期存放
     */
    private String isAgreementEnd;

    /**
     * 本方券委托数量
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal ourEntNum;

    /**
     * 融入券委托数量
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal lendinEntNum;

    /**
     * 本方券可用数量
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal ourAbleNum;

    /**
     * 融入券可用数量
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal lendinAbleNum;

    /**
     * 卖空券可用数量
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal sellAbleNum;

    /**
     * PB是否已导出
     */
    private String pbExportstatus;

    /**
     * 高障碍价
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal highHindPrice;

    /**
     * 低障碍价
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal lowHindPrice;

    /**
     * 高行权价
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal highExercPrice;

    /**
     * 低行权价
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal lowExercPrice;

    /**
     * 执行价
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal exePrice;

    /**
     * 敲出价
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
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
     * 转入资产代码
     */
    private String rFinprodId;

    /**
     * 合同签署结果
     */
    private String contractResult;

    /**
     * 合同签署结果
     */
    private String contractNo;

    /**
     * 缴款日期
     */
    private Date paymentDate;

    /**
     * 上市日期
     */
    private Date listingDate;

    /**
     * 主承销商
     */
    private String mainConsignee;

    /**
     * 担保人
     */
    private String warrantor;

    /**
     * 投标区间%
     */
    private String biddingRange;

    /**
     * 最低成交金额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal minAmt;

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
    @JsonSerialize(using=BigDecimalSerializer.class)
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
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal mIntAmt;

    /**
     * 到期结算金额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal mTradeAmt;

    /**
     * 占款天数
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
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
     * 续做类型
     */
    private String redoType;

    /**
     * 衍生品买卖方向
     */
    private String derivativeBsDir;

    /**
     * 衍生品开平仓标志
     */
    private String derivativeOcFlag;

    /**
     * 报价方式
     */
    private String quoteType;

    /**
     * 港股通市场
     */
    private String hkMarket;

    /**
     * 目标币种成交金额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal entrustAmtTarget;

    /**
     * 质押券库代码
     */
    private String pledgeStockId;

    /**
     * 计算方式
     */
    private String calculateField;

    /**
     * 组合系列
     */
    private String portfolioSeries;

    /**
     * 指令创建来源
     */
    private String entrustCreateSource;

    /**
     * 头寸业务类型
     */
    private String busiTypePosition;

    /**
     * 指令下达时间
     */
    private Date transmitTime;

    /**
     * 权重方式
     */
    private String weighType;

    /**
     * 权重方式
     */
    private String mulDistriClass;

    /**
     * 目标值
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal mulTarget;

    /**
     * 强制完成金额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal forcedFinishAmt;

    /**
     * 已关闭额度
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal closeAmt;

    /**
     * 目标值单位
     */
    private String mulUnit;

    /**
     * 计价方式
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
    @JsonDeserialize(using = CustomDateDeserialize.class)
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
    @JsonDeserialize(using = CustomDateDeserialize.class)
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
     * 取得 null
     * @return null
     */
    public BigDecimal getFirstUnitCprice() {
        return firstUnitCprice;
    }

    /**
     * 设置 null
     * @param firstUnitCprice null
     */
    public void setFirstUnitCprice(BigDecimal firstUnitCprice) {
        this.firstUnitCprice = firstUnitCprice;
    }

    /**
     * 取得 null
     * @return null
     */
    public BigDecimal getFirstUnitCprice2() {
        return firstUnitCprice2;
    }

    /**
     * 设置 null
     * @param firstUnitCprice2 null
     */
    public void setFirstUnitCprice2(BigDecimal firstUnitCprice2) {
        this.firstUnitCprice2 = firstUnitCprice2;
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
     * 取得 对应货币
     * @return 对应货币
     */
    public String getCorCcy() {
        return corCcy;
    }

    /**
     * 设置 对应货币
     * @param corCcy 对应货币
     */
    public void setCorCcy(String corCcy) {
        this.corCcy = corCcy == null ? null : corCcy.trim();
    }

    /**
     * 取得 预计对应货币金额
     * @return 预计对应货币金额
     */
    public BigDecimal getExpCorAmt() {
        return expCorAmt;
    }

    /**
     * 设置 预计对应货币金额
     * @param expCorAmt 预计对应货币金额
     */
    public void setExpCorAmt(BigDecimal expCorAmt) {
        this.expCorAmt = expCorAmt;
    }

    /**
     * 取得 对应货币可用持仓
     * @return 对应货币可用持仓
     */
    public BigDecimal getCorCurPos() {
        return corCurPos;
    }

    /**
     * 设置 对应货币可用持仓
     * @param corCurPos 对应货币可用持仓
     */
    public void setCorCurPos(BigDecimal corCurPos) {
        this.corCurPos = corCurPos;
    }

    /**
     * 取得 是否差额清算
     * @return 是否差额清算
     */
    public String getIsDifSqu() {
        return isDifSqu;
    }

    /**
     * 设置 是否差额清算
     * @param isDifSqu 是否差额清算
     */
    public void setIsDifSqu(String isDifSqu) {
        this.isDifSqu = isDifSqu == null ? null : isDifSqu.trim();
    }

    /**
     * 取得 交易货币账户
     * @return 交易货币账户
     */
    public String getDealCurAcct() {
        return dealCurAcct;
    }

    /**
     * 设置 交易货币账户
     * @param dealCurAcct 交易货币账户
     */
    public void setDealCurAcct(String dealCurAcct) {
        this.dealCurAcct = dealCurAcct == null ? null : dealCurAcct.trim();
    }

    /**
     * 取得 对应货币账户
     * @return 对应货币账户
     */
    public String getCorCurAcct() {
        return corCurAcct;
    }

    /**
     * 设置 对应货币账户
     * @param corCurAcct 对应货币账户
     */
    public void setCorCurAcct(String corCurAcct) {
        this.corCurAcct = corCurAcct == null ? null : corCurAcct.trim();
    }

    /**
     * 取得 人民币账户
     * @return 人民币账户
     */
    public String getCnyAcct() {
        return cnyAcct;
    }

    /**
     * 设置 人民币账户
     * @param cnyAcct 人民币账户
     */
    public void setCnyAcct(String cnyAcct) {
        this.cnyAcct = cnyAcct == null ? null : cnyAcct.trim();
    }

    /**
     * 取得 起息日2
     * @return 起息日2
     */
    public Date getVdate2() {
        return vdate2;
    }

    /**
     * 设置 起息日2
     * @param vdate2 起息日2
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setVdate2(Date vdate2) {
        this.vdate2 = vdate2;
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
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setMdate2(Date mdate2) {
        this.mdate2 = mdate2;
    }

    /**
     * 取得 掉期期限类型
     * @return 掉期期限类型
     */
    public String getExcFxsTermType() {
        return excFxsTermType;
    }

    /**
     * 设置 掉期期限类型
     * @param excFxsTermType 掉期期限类型
     */
    public void setExcFxsTermType(String excFxsTermType) {
        this.excFxsTermType = excFxsTermType == null ? null : excFxsTermType.trim();
    }

    /**
     * 取得 缴款方式
     * @return 缴款方式
     */
    public String getPayType() {
        return payType;
    }

    /**
     * 设置 缴款方式
     * @param payType 缴款方式
     */
    public void setPayType(String payType) {
        this.payType = payType == null ? null : payType.trim();
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
     * 取得 正股价
     * @return 正股价
     */
    public BigDecimal getSharePrice() {
        return sharePrice;
    }

    /**
     * 设置 正股价
     * @param sharePrice 正股价
     */
    public void setSharePrice(BigDecimal sharePrice) {
        this.sharePrice = sharePrice;
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
     * 取得 实际持有数量
     * @return 实际持有数量
     */
    public BigDecimal getActNum() {
        return actNum;
    }

    /**
     * 设置 实际持有数量
     * @param actNum 实际持有数量
     */
    public void setActNum(BigDecimal actNum) {
        this.actNum = actNum;
    }

    /**
     * 取得 预计转换股数
     * @return 预计转换股数
     */
    public BigDecimal getExpConNum() {
        return expConNum;
    }

    /**
     * 设置 预计转换股数
     * @param expConNum 预计转换股数
     */
    public void setExpConNum(BigDecimal expConNum) {
        this.expConNum = expConNum;
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
     * 取得 是否协议终止，适用于活期存放
     * @return 是否协议终止，适用于活期存放
     */
    public String getIsAgreementEnd() {
        return isAgreementEnd;
    }

    /**
     * 设置 是否协议终止，适用于活期存放
     * @param isAgreementEnd 是否协议终止，适用于活期存放
     */
    public void setIsAgreementEnd(String isAgreementEnd) {
        this.isAgreementEnd = isAgreementEnd == null ? null : isAgreementEnd.trim();
    }

    /**
     * 取得 本方券委托数量
     * @return 本方券委托数量
     */
    public BigDecimal getOurEntNum() {
        return ourEntNum;
    }

    /**
     * 设置 本方券委托数量
     * @param ourEntNum 本方券委托数量
     */
    public void setOurEntNum(BigDecimal ourEntNum) {
        this.ourEntNum = ourEntNum;
    }

    /**
     * 取得 融入券委托数量
     * @return 融入券委托数量
     */
    public BigDecimal getLendinEntNum() {
        return lendinEntNum;
    }

    /**
     * 设置 融入券委托数量
     * @param lendinEntNum 融入券委托数量
     */
    public void setLendinEntNum(BigDecimal lendinEntNum) {
        this.lendinEntNum = lendinEntNum;
    }

    /**
     * 取得 本方券可用数量
     * @return 本方券可用数量
     */
    public BigDecimal getOurAbleNum() {
        return ourAbleNum;
    }

    /**
     * 设置 本方券可用数量
     * @param ourAbleNum 本方券可用数量
     */
    public void setOurAbleNum(BigDecimal ourAbleNum) {
        this.ourAbleNum = ourAbleNum;
    }

    /**
     * 取得 融入券可用数量
     * @return 融入券可用数量
     */
    public BigDecimal getLendinAbleNum() {
        return lendinAbleNum;
    }

    /**
     * 设置 融入券可用数量
     * @param lendinAbleNum 融入券可用数量
     */
    public void setLendinAbleNum(BigDecimal lendinAbleNum) {
        this.lendinAbleNum = lendinAbleNum;
    }

    /**
     * 取得 卖空券可用数量
     * @return 卖空券可用数量
     */
    public BigDecimal getSellAbleNum() {
        return sellAbleNum;
    }

    /**
     * 设置 卖空券可用数量
     * @param sellAbleNum 卖空券可用数量
     */
    public void setSellAbleNum(BigDecimal sellAbleNum) {
        this.sellAbleNum = sellAbleNum;
    }

    /**
     * 取得 PB是否已导出
     * @return PB是否已导出
     */
    public String getPbExportstatus() {
        return pbExportstatus;
    }

    /**
     * 设置 PB是否已导出
     * @param pbExportstatus PB是否已导出
     */
    public void setPbExportstatus(String pbExportstatus) {
        this.pbExportstatus = pbExportstatus == null ? null : pbExportstatus.trim();
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
    @JsonDeserialize(using = CustomDateDeserialize.class)
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
     * 取得 转入资产代码
     * @return 转入资产代码
     */
    public String getrFinprodId() {
        return rFinprodId;
    }

    /**
     * 设置 转入资产代码
     * @param rFinprodId 转入资产代码
     */
    public void setrFinprodId(String rFinprodId) {
        this.rFinprodId = rFinprodId == null ? null : rFinprodId.trim();
    }

    /**
     * 取得 合同签署结果
     * @return 合同签署结果
     */
    public String getContractResult() {
        return contractResult;
    }

    /**
     * 设置 合同签署结果
     * @param contractResult 合同签署结果
     */
    public void setContractResult(String contractResult) {
        this.contractResult = contractResult == null ? null : contractResult.trim();
    }

    /**
     * 取得 合同签署结果
     * @return 合同签署结果
     */
    public String getContractNo() {
        return contractNo;
    }

    /**
     * 设置 合同签署结果
     * @param contractNo 合同签署结果
     */
    public void setContractNo(String contractNo) {
        this.contractNo = contractNo == null ? null : contractNo.trim();
    }

    /**
     * 取得 缴款日期
     * @return 缴款日期
     */
    public Date getPaymentDate() {
        return paymentDate;
    }

    /**
     * 设置 缴款日期
     * @param paymentDate 缴款日期
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }

    /**
     * 取得 上市日期
     * @return 上市日期
     */
    public Date getListingDate() {
        return listingDate;
    }

    /**
     * 设置 上市日期
     * @param listingDate 上市日期
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setListingDate(Date listingDate) {
        this.listingDate = listingDate;
    }

    /**
     * 取得 主承销商
     * @return 主承销商
     */
    public String getMainConsignee() {
        return mainConsignee;
    }

    /**
     * 设置 主承销商
     * @param mainConsignee 主承销商
     */
    public void setMainConsignee(String mainConsignee) {
        this.mainConsignee = mainConsignee == null ? null : mainConsignee.trim();
    }

    /**
     * 取得 担保人
     * @return 担保人
     */
    public String getWarrantor() {
        return warrantor;
    }

    /**
     * 设置 担保人
     * @param warrantor 担保人
     */
    public void setWarrantor(String warrantor) {
        this.warrantor = warrantor == null ? null : warrantor.trim();
    }

    /**
     * 取得 投标区间%
     * @return 投标区间%
     */
    public String getBiddingRange() {
        return biddingRange;
    }

    /**
     * 设置 投标区间%
     * @param biddingRange 投标区间%
     */
    public void setBiddingRange(String biddingRange) {
        this.biddingRange = biddingRange == null ? null : biddingRange.trim();
    }

    /**
     * 取得 最低成交金额
     * @return 最低成交金额
     */
    public BigDecimal getMinAmt() {
        return minAmt;
    }

    /**
     * 设置 最低成交金额
     * @param minAmt 最低成交金额
     */
    public void setMinAmt(BigDecimal minAmt) {
        this.minAmt = minAmt;
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

    /**
     * 取得 首次交收日
     * @return 首次交收日
     */
    public Date getVpayDate() {
        return vpayDate;
    }

    /**
     * 设置 首次交收日
     * @param vpayDate 首次交收日
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setVpayDate(Date vpayDate) {
        this.vpayDate = vpayDate;
    }

    /**
     * 取得 到期交收日
     * @return 到期交收日
     */
    public Date getMpayDate() {
        return mpayDate;
    }

    /**
     * 设置 到期交收日
     * @param mpayDate 到期交收日
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setMpayDate(Date mpayDate) {
        this.mpayDate = mpayDate;
    }

    /**
     * 取得 到期应计利息
     * @return 到期应计利息
     */
    public BigDecimal getmIntAmt() {
        return mIntAmt;
    }

    /**
     * 设置 到期应计利息
     * @param mIntAmt 到期应计利息
     */
    public void setmIntAmt(BigDecimal mIntAmt) {
        this.mIntAmt = mIntAmt;
    }

    /**
     * 取得 到期结算金额
     * @return 到期结算金额
     */
    public BigDecimal getmTradeAmt() {
        return mTradeAmt;
    }

    /**
     * 设置 到期结算金额
     * @param mTradeAmt 到期结算金额
     */
    public void setmTradeAmt(BigDecimal mTradeAmt) {
        this.mTradeAmt = mTradeAmt;
    }

    /**
     * 取得 占款天数
     * @return 占款天数
     */
    public BigDecimal getCapDays() {
        return capDays;
    }

    /**
     * 设置 占款天数
     * @param capDays 占款天数
     */
    public void setCapDays(BigDecimal capDays) {
        this.capDays = capDays;
    }

    /**
     * 取得 成交编号/初始交易编号
     * @return 成交编号/初始交易编号
     */
    public String getTradeId() {
        return tradeId;
    }

    /**
     * 设置 成交编号/初始交易编号
     * @param tradeId 成交编号/初始交易编号
     */
    public void setTradeId(String tradeId) {
        this.tradeId = tradeId == null ? null : tradeId.trim();
    }

    /**
     * 取得 请求号
     * @return 请求号
     */
    public String getRequestId() {
        return requestId;
    }

    /**
     * 设置 请求号
     * @param requestId 请求号
     */
    public void setRequestId(String requestId) {
        this.requestId = requestId == null ? null : requestId.trim();
    }

    /**
     * 取得 续做类型
     * @return 续做类型
     */
    public String getRedoType() {
        return redoType;
    }

    /**
     * 设置 续做类型
     * @param redoType 续做类型
     */
    public void setRedoType(String redoType) {
        this.redoType = redoType == null ? null : redoType.trim();
    }

    /**
     * 取得 衍生品买卖方向
     * @return 衍生品买卖方向
     */
    public String getDerivativeBsDir() {
        return derivativeBsDir;
    }

    /**
     * 设置 衍生品买卖方向
     * @param derivativeBsDir 衍生品买卖方向
     */
    public void setDerivativeBsDir(String derivativeBsDir) {
        this.derivativeBsDir = derivativeBsDir == null ? null : derivativeBsDir.trim();
    }

    /**
     * 取得 衍生品开平仓标志
     * @return 衍生品开平仓标志
     */
    public String getDerivativeOcFlag() {
        return derivativeOcFlag;
    }

    /**
     * 设置 衍生品开平仓标志
     * @param derivativeOcFlag 衍生品开平仓标志
     */
    public void setDerivativeOcFlag(String derivativeOcFlag) {
        this.derivativeOcFlag = derivativeOcFlag == null ? null : derivativeOcFlag.trim();
    }

    /**
     * 取得 报价方式
     * @return 报价方式
     */
    public String getQuoteType() {
        return quoteType;
    }

    /**
     * 设置 报价方式
     * @param quoteType 报价方式
     */
    public void setQuoteType(String quoteType) {
        this.quoteType = quoteType == null ? null : quoteType.trim();
    }

    /**
     * 取得 港股通市场
     * @return 港股通市场
     */
    public String getHkMarket() {
        return hkMarket;
    }

    /**
     * 设置 港股通市场
     * @param hkMarket 港股通市场
     */
    public void setHkMarket(String hkMarket) {
        this.hkMarket = hkMarket == null ? null : hkMarket.trim();
    }

    /**
     * 取得 目标币种成交金额
     * @return 目标币种成交金额
     */
    public BigDecimal getEntrustAmtTarget() {
        return entrustAmtTarget;
    }

    /**
     * 设置 目标币种成交金额
     * @param entrustAmtTarget 目标币种成交金额
     */
    public void setEntrustAmtTarget(BigDecimal entrustAmtTarget) {
        this.entrustAmtTarget = entrustAmtTarget;
    }

    /**
     * 取得 质押券库代码
     * @return 质押券库代码
     */
    public String getPledgeStockId() {
        return pledgeStockId;
    }

    /**
     * 设置 质押券库代码
     * @param pledgeStockId 质押券库代码
     */
    public void setPledgeStockId(String pledgeStockId) {
        this.pledgeStockId = pledgeStockId == null ? null : pledgeStockId.trim();
    }

    /**
     * 取得 计算方式
     * @return 计算方式
     */
    public String getCalculateField() {
        return calculateField;
    }

    /**
     * 设置 计算方式
     * @param calculateField 计算方式
     */
    public void setCalculateField(String calculateField) {
        this.calculateField = calculateField == null ? null : calculateField.trim();
    }

    /**
     * 取得 组合系列
     * @return 组合系列
     */
    public String getPortfolioSeries() {
        return portfolioSeries;
    }

    /**
     * 设置 组合系列
     * @param portfolioSeries 组合系列
     */
    public void setPortfolioSeries(String portfolioSeries) {
        this.portfolioSeries = portfolioSeries == null ? null : portfolioSeries.trim();
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

    public String getBusiTypePosition() {
        return busiTypePosition;
    }

    public void setBusiTypePosition(String busiTypePosition) {
        this.busiTypePosition = busiTypePosition;
    }

    public Date getTransmitTime() {
        return transmitTime;
    }

    public void setTransmitTime(Date transmitTime) {
        this.transmitTime = transmitTime;
    }

    public String getWeighType() {
        return weighType;
    }

    public void setWeighType(String weighType) {
        this.weighType = weighType;
    }

    public String getMulDistriClass() {
        return mulDistriClass;
    }

    public void setMulDistriClass(String mulDistriClass) {
        this.mulDistriClass = mulDistriClass;
    }

    public BigDecimal getMulTarget() {
        return mulTarget;
    }

    public void setMulTarget(BigDecimal mulTarget) {
        this.mulTarget = mulTarget;
    }

    public String getMulUnit() {
        return mulUnit;
    }

    public void setMulUnit(String mulUnit) {
        this.mulUnit = mulUnit;
    }

    public BigDecimal getForcedFinishAmt() {
        return forcedFinishAmt;
    }

    public void setForcedFinishAmt(BigDecimal forcedFinishAmt) {
        this.forcedFinishAmt = forcedFinishAmt;
    }

    public BigDecimal getCloseAmt() {
        return closeAmt;
    }

    public void setCloseAmt(BigDecimal closeAmt) {
        this.closeAmt = closeAmt;
    }

    /**
     * 取得 计价方式
     * @return 计价方式
     */
    public String getPriceType() {
        return priceType;
    }

    /**
     * 设置 计价方式
     * @param priceType 计价方式
     */
    public void setPriceType(String priceType) {
        this.priceType = priceType == null ? null : priceType.trim();
    }
}