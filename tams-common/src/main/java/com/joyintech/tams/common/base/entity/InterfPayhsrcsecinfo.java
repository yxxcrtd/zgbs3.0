package com.joyintech.tams.common.base.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.framework.json.CustomDateDeserialize;

import java.math.BigDecimal;
import java.util.Date;

public class InterfPayhsrcsecinfo extends InterfPayhsrcsecinfoKey {

    /**
     * 债券简称
     */
    private String secname;

    /**
     * 债券code
     */
    private String seccode;

    /**
     * 市场
     */
    private String market;

    /**
     * 债券全称
     */
    private String secfullname;

    /**
     * 计息基准：参考右边明细
     */
    private String ratebasic;

    /**
     * 息票品种 NPV付息 DIS贴现 IAM利随本清 ZCO零息
     */
    private String couponspecies;

    /**
     * 利率类型 FI固定，FL浮动
     */
    private String interestrate;

    /**
     * 起息日
     */
    private Date vdate;

    /**
     * 到期日
     */
    private Date mdate;

    /**
     * 付息频率
     */
    private String paycycle;

    /**
     * 面额 默认100
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal facevalue;

    /**
     * 发行价
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal issueprice;

    /**
     * 票面利率
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal paperir;

    /**
     * 付息区间推算方法 VF起息日向后，MB到期日向前，FD首次付息日
     */
    private String schecalrule;

    /**
     * 首次付息日
     */
    private Date firstrateday;

    /**
     * 营业日准则 P向前调整 S向后调整 M调整的向后 N不调整
     */
    private String workdayrule;

    /**
     * 发行人
     */
    private String issuershort;

    /**
     * 利率调整规则
     */
    private String resettype;

    /**
     * 延后时间
     */
    private Long observebefday;

    /**
     * 延后时间单位
     */
    private String observebefunit;

    /**
     * 利率上限
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal toprate;

    /**
     * 利率下限
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal bottomrate;

    /**
     * 发行总额
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal issueamt;

    /**
     * 浮动利率code
     */
    private String ratecode;

    /**
     * 浮动利差
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal spreadrate8;

    /**
     * 浮动系数
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal coefficient;

    /**
     * 系统债券类型：
     */
    private String sectype;

    /**
     * 系统债券类型：
     */
    private String sectype2;

    /**
     * 中债债券类型：
     */
    private String chbsectype;

    /**
     * 中债债券类型：
     */
    private String chbsectype2;

    /**
     * 人行债券类型：
     */
    private String pbocsectype;

    /**
     * 人行债券类型：
     */
    private String pbocsectype2;

    /**
     * 备注
     */
    private String remark;

    /**
     * 是否次级债,Y是N否
     */
    private String issecbond;

    /**
     * 是否含权债
     */
    private String isRight;

    /**
     * 原始权益人
     */
    private String originalOwner;


    /**
     * 是否永续债,Y是N否
     */
    private String issus;

    /**
     * 是否可赎回,Y是N否
     */
    private String calloption;

    /**
     * 是否可回售,Y是N否
     */
    private String putoption;

    /**
     * 登记托管机构
     */
    private String institution;

    /**
     * 登记托管机构说明
     */
    private String institutiontext;

    /**
     * 交易场所说明
     */
    private String markettext;

    /**
     * 交易流通场所：01 银行间市场、02 商业银行柜台市场、03 上海交易所、04 深圳交易所、99 其它
     */
    private String market2;

    /**
     * "币种
     * CNY 人民币、HKD 港币、JPY 日元、USD 美元"
     */
    private String ccy;

    /**
     * 发行方式
     */
    private String issuetype;

    /**
     * 境内外
     */
    private String blnarea;

    /**
     * 担保人
     */
    private String guarantor;

    /**
     * 担保方式
     */
    private String guartype;

    /**
     * 发行截止日
     */
    private Date pubenddate;

    /**
     * 是否城投债（中债）
     */
    private String cityInvestSecChb;

    /**
     * 城投债省及省会（单列市）
     */
    private String cityInvestSecProvinces;

    /**
     * 城投债地级市
     */
    private String cityInvestSecCities;

    /**
     * 城投债县及县级市
     */
    private String cityInvestSecCounty;

    /**
     * 是否城投债（WIND）
     */
    private String cityInvestSecWind;

    /**
     * 是否城投债（银监）
     */
    private String cityInvestSecCbrc;

    /**
     * 资产/负债类别
     */
    private String assetClass;

    /**
     * 具体类别
     */
    private String concreteClass;

    /**
     * 具体类别说明
     */
    private String concreteClassSm;

    /**
     * 发行机构类型（按规模划分）
     */
    private String issuerScaleType;

    /**
     * 发行机构类型（按技术领域划分）
     */
    private String issuerTechnologyType;

    /**
     * 发行机构类型（按经济领域划分）
     */
    private String issuerEconomicType;

    /**
     * 发行机构类型说明
     */
    private String issuerDesc;

    /**
     * 发行机构所属行业
     */
    private String issuerInstitutions;

    // 20200819 龙江银行修改-查询债券接口信息表增加查询字段 wangwei start
    /**
     * 发行机构所属行业二级分类
     */
    private String issuerinstitutionssecond;
    // 20200819 龙江银行修改-查询债券接口信息表增加查询字段 wangwei end
    /**
     * 是否私募债
     */
    private String isPpb;
    /**
     * 是否净价交易
     */
    private String isNetprice;
    /**
     * 正股代码
     */
    private String conFinprodId;
    /**
     * 转股代码
     */
    private String conShareId;
    /**
     * 转股起始日
     */
    private Date conVdate;

    /**
     * 转股价格
     */
    private BigDecimal conPrice;

    /**
     * 回售触发价
     */
    private BigDecimal resalePrice;

    /**
     * 赎回触发价
     */
    private BigDecimal redeemPrice;

    /**
     * 摘牌日
     */
    private Date delistingDate;

    /**
     * 是否绿色债
     */
    private String isGreenBond;

    /**
     * 跨市场代码
     */
    private String crossMarketCode;

    /**
     * 基准利率精度
     */
    private BigDecimal baseRateAccuracy;

    /**
     * 首次利率确定日
     */
    private Date firstRateDeterminationDate;

    /**
     * 取得 债券简称
     *
     * @return 债券简称
     */
    public String getSecname() {
        return secname;
    }

    /**
     * 设置 债券简称
     *
     * @param secname 债券简称
     */
    public void setSecname(String secname) {
        this.secname = secname == null ? null : secname.trim();
    }

    /**
     * 取得 债券code
     *
     * @return 债券code
     */
    public String getSeccode() {
        return seccode;
    }

    /**
     * 设置 债券code
     *
     * @param seccode 债券code
     */
    public void setSeccode(String seccode) {
        this.seccode = seccode == null ? null : seccode.trim();
    }

    /**
     * 取得 市场
     *
     * @return 市场
     */
    public String getMarket() {
        return market;
    }

    /**
     * 设置 市场
     *
     * @param market 市场
     */
    public void setMarket(String market) {
        this.market = market == null ? null : market.trim();
    }

    /**
     * 取得 债券全称
     *
     * @return 债券全称
     */
    public String getSecfullname() {
        return secfullname;
    }

    /**
     * 设置 债券全称
     *
     * @param secfullname 债券全称
     */
    public void setSecfullname(String secfullname) {
        this.secfullname = secfullname == null ? null : secfullname.trim();
    }

    /**
     * 取得 计息基准：参考右边明细
     *
     * @return 计息基准：参考右边明细
     */
    public String getRatebasic() {
        return ratebasic;
    }

    /**
     * 设置 计息基准：参考右边明细
     *
     * @param ratebasic 计息基准：参考右边明细
     */
    public void setRatebasic(String ratebasic) {
        this.ratebasic = ratebasic == null ? null : ratebasic.trim();
    }

    /**
     * 取得 息票品种 NPV付息 DIS贴现 IAM利随本清 ZCO零息
     *
     * @return 息票品种 NPV付息 DIS贴现 IAM利随本清 ZCO零息
     */
    public String getCouponspecies() {
        return couponspecies;
    }

    /**
     * 设置 息票品种 NPV付息 DIS贴现 IAM利随本清 ZCO零息
     *
     * @param couponspecies 息票品种 NPV付息 DIS贴现 IAM利随本清 ZCO零息
     */
    public void setCouponspecies(String couponspecies) {
        this.couponspecies = couponspecies == null ? null : couponspecies.trim();
    }

    /**
     * 取得 利率类型 FI固定，FL浮动
     *
     * @return 利率类型 FI固定，FL浮动
     */
    public String getInterestrate() {
        return interestrate;
    }

    /**
     * 设置 利率类型 FI固定，FL浮动
     *
     * @param interestrate 利率类型 FI固定，FL浮动
     */
    public void setInterestrate(String interestrate) {
        this.interestrate = interestrate == null ? null : interestrate.trim();
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
    @JsonDeserialize(using = CustomDateDeserialize.class)
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
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setMdate(Date mdate) {
        this.mdate = mdate;
    }

    /**
     * 取得 付息频率
     *
     * @return 付息频率
     */
    public String getPaycycle() {
        return paycycle;
    }

    /**
     * 设置 付息频率
     *
     * @param paycycle 付息频率
     */
    public void setPaycycle(String paycycle) {
        this.paycycle = paycycle == null ? null : paycycle.trim();
    }

    /**
     * 取得 面额 默认100
     *
     * @return 面额 默认100
     */
    public BigDecimal getFacevalue() {
        return facevalue;
    }

    /**
     * 设置 面额 默认100
     *
     * @param facevalue 面额 默认100
     */
    public void setFacevalue(BigDecimal facevalue) {
        this.facevalue = facevalue;
    }

    /**
     * 取得 发行价
     *
     * @return 发行价
     */
    public BigDecimal getIssueprice() {
        return issueprice;
    }

    /**
     * 设置 发行价
     *
     * @param issueprice 发行价
     */
    public void setIssueprice(BigDecimal issueprice) {
        this.issueprice = issueprice;
    }

    /**
     * 取得 票面利率
     *
     * @return 票面利率
     */
    public BigDecimal getPaperir() {
        return paperir;
    }

    /**
     * 设置 票面利率
     *
     * @param paperir 票面利率
     */
    public void setPaperir(BigDecimal paperir) {
        this.paperir = paperir;
    }

    /**
     * 取得 付息区间推算方法 VF起息日向后，MB到期日向前，FD首次付息日
     *
     * @return 付息区间推算方法 VF起息日向后，MB到期日向前，FD首次付息日
     */
    public String getSchecalrule() {
        return schecalrule;
    }

    /**
     * 设置 付息区间推算方法 VF起息日向后，MB到期日向前，FD首次付息日
     *
     * @param schecalrule 付息区间推算方法 VF起息日向后，MB到期日向前，FD首次付息日
     */
    public void setSchecalrule(String schecalrule) {
        this.schecalrule = schecalrule == null ? null : schecalrule.trim();
    }

    /**
     * 取得 首次付息日
     *
     * @return 首次付息日
     */
    public Date getFirstrateday() {
        return firstrateday;
    }

    /**
     * 设置 首次付息日
     *
     * @param firstrateday 首次付息日
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setFirstrateday(Date firstrateday) {
        this.firstrateday = firstrateday;
    }

    /**
     * 取得 营业日准则 P向前调整 S向后调整 M调整的向后 N不调整
     *
     * @return 营业日准则 P向前调整 S向后调整 M调整的向后 N不调整
     */
    public String getWorkdayrule() {
        return workdayrule;
    }

    /**
     * 设置 营业日准则 P向前调整 S向后调整 M调整的向后 N不调整
     *
     * @param workdayrule 营业日准则 P向前调整 S向后调整 M调整的向后 N不调整
     */
    public void setWorkdayrule(String workdayrule) {
        this.workdayrule = workdayrule == null ? null : workdayrule.trim();
    }

    /**
     * 取得 发行人
     *
     * @return 发行人
     */
    public String getIssuershort() {
        return issuershort;
    }

    /**
     * 设置 发行人
     *
     * @param issuershort 发行人
     */
    public void setIssuershort(String issuershort) {
        this.issuershort = issuershort == null ? null : issuershort.trim();
    }

    /**
     * 取得 利率调整规则
     *
     * @return 利率调整规则
     */
    public String getResettype() {
        return resettype;
    }

    /**
     * 设置 利率调整规则
     *
     * @param resettype 利率调整规则
     */
    public void setResettype(String resettype) {
        this.resettype = resettype == null ? null : resettype.trim();
    }

    /**
     * 取得 延后时间
     *
     * @return 延后时间
     */
    public Long getObservebefday() {
        return observebefday;
    }

    /**
     * 设置 延后时间
     *
     * @param observebefday 延后时间
     */
    public void setObservebefday(Long observebefday) {
        this.observebefday = observebefday;
    }

    /**
     * 取得 延后时间单位
     *
     * @return 延后时间单位
     */
    public String getObservebefunit() {
        return observebefunit;
    }

    /**
     * 设置 延后时间单位
     *
     * @param observebefunit 延后时间单位
     */
    public void setObservebefunit(String observebefunit) {
        this.observebefunit = observebefunit == null ? null : observebefunit.trim();
    }

    /**
     * 取得 利率上限
     *
     * @return 利率上限
     */
    public BigDecimal getToprate() {
        return toprate;
    }

    /**
     * 设置 利率上限
     *
     * @param toprate 利率上限
     */
    public void setToprate(BigDecimal toprate) {
        this.toprate = toprate;
    }

    /**
     * 取得 利率下限
     *
     * @return 利率下限
     */
    public BigDecimal getBottomrate() {
        return bottomrate;
    }

    /**
     * 设置 利率下限
     *
     * @param bottomrate 利率下限
     */
    public void setBottomrate(BigDecimal bottomrate) {
        this.bottomrate = bottomrate;
    }

    /**
     * 取得 发行总额
     *
     * @return 发行总额
     */
    public BigDecimal getIssueamt() {
        return issueamt;
    }

    /**
     * 设置 发行总额
     *
     * @param issueamt 发行总额
     */
    public void setIssueamt(BigDecimal issueamt) {
        this.issueamt = issueamt;
    }

    /**
     * 取得 浮动利率code
     *
     * @return 浮动利率code
     */
    public String getRatecode() {
        return ratecode;
    }

    /**
     * 设置 浮动利率code
     *
     * @param ratecode 浮动利率code
     */
    public void setRatecode(String ratecode) {
        this.ratecode = ratecode == null ? null : ratecode.trim();
    }

    /**
     * 取得 浮动利差
     *
     * @return 浮动利差
     */
    public BigDecimal getSpreadrate8() {
        return spreadrate8;
    }

    /**
     * 设置 浮动利差
     *
     * @param spreadrate8 浮动利差
     */
    public void setSpreadrate8(BigDecimal spreadrate8) {
        this.spreadrate8 = spreadrate8;
    }

    /**
     * 取得 浮动系数
     *
     * @return 浮动系数
     */
    public BigDecimal getCoefficient() {
        return coefficient;
    }

    /**
     * 设置 浮动系数
     *
     * @param coefficient 浮动系数
     */
    public void setCoefficient(BigDecimal coefficient) {
        this.coefficient = coefficient;
    }

    /**
     * 取得 系统债券类型：
     *
     * @return 系统债券类型：
     */
    public String getSectype() {
        return sectype;
    }

    /**
     * 设置 系统债券类型：
     *
     * @param sectype 系统债券类型：
     */
    public void setSectype(String sectype) {
        this.sectype = sectype == null ? null : sectype.trim();
    }

    /**
     * 取得 系统债券类型：
     *
     * @return 系统债券类型：
     */
    public String getSectype2() {
        return sectype2;
    }

    /**
     * 设置 系统债券类型：
     *
     * @param sectype2 系统债券类型：
     */
    public void setSectype2(String sectype2) {
        this.sectype2 = sectype2 == null ? null : sectype2.trim();
    }

    /**
     * 取得 中债债券类型：
     *
     * @return 中债债券类型：
     */
    public String getChbsectype() {
        return chbsectype;
    }

    /**
     * 设置 中债债券类型：
     *
     * @param chbsectype 中债债券类型：
     */
    public void setChbsectype(String chbsectype) {
        this.chbsectype = chbsectype == null ? null : chbsectype.trim();
    }

    /**
     * 取得 中债债券类型：
     *
     * @return 中债债券类型：
     */
    public String getChbsectype2() {
        return chbsectype2;
    }

    /**
     * 设置 中债债券类型：
     *
     * @param chbsectype2 中债债券类型：
     */
    public void setChbsectype2(String chbsectype2) {
        this.chbsectype2 = chbsectype2 == null ? null : chbsectype2.trim();
    }

    /**
     * 取得 人行债券类型：
     *
     * @return 人行债券类型：
     */
    public String getPbocsectype() {
        return pbocsectype;
    }

    /**
     * 设置 人行债券类型：
     *
     * @param pbocsectype 人行债券类型：
     */
    public void setPbocsectype(String pbocsectype) {
        this.pbocsectype = pbocsectype == null ? null : pbocsectype.trim();
    }

    /**
     * 取得 人行债券类型：
     *
     * @return 人行债券类型：
     */
    public String getPbocsectype2() {
        return pbocsectype2;
    }

    /**
     * 设置 人行债券类型：
     *
     * @param pbocsectype2 人行债券类型：
     */
    public void setPbocsectype2(String pbocsectype2) {
        this.pbocsectype2 = pbocsectype2 == null ? null : pbocsectype2.trim();
    }

    /**
     * 取得 备注
     *
     * @return 备注
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 设置 备注
     *
     * @param remark 备注
     */
    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    /**
     * 取得 是否次级债,Y是N否
     *
     * @return 是否次级债, Y是N否
     */
    public String getIssecbond() {
        return issecbond;
    }

    /**
     * 设置 是否次级债,Y是N否
     *
     * @param issecbond 是否次级债,Y是N否
     */
    public void setIssecbond(String issecbond) {
        this.issecbond = issecbond == null ? null : issecbond.trim();
    }

    /**
     * 取得 是否永续债,Y是N否
     *
     * @return 是否永续债, Y是N否
     */
    public String getIssus() {
        return issus;
    }

    /**
     * 设置 是否永续债,Y是N否
     *
     * @param issus 是否永续债,Y是N否
     */
    public void setIssus(String issus) {
        this.issus = issus == null ? null : issus.trim();
    }

    /**
     * 取得 是否可赎回,Y是N否
     *
     * @return 是否可赎回, Y是N否
     */
    public String getCalloption() {
        return calloption;
    }

    /**
     * 设置 是否可赎回,Y是N否
     *
     * @param calloption 是否可赎回,Y是N否
     */
    public void setCalloption(String calloption) {
        this.calloption = calloption == null ? null : calloption.trim();
    }

    /**
     * 取得 是否可回售,Y是N否
     *
     * @return 是否可回售, Y是N否
     */
    public String getPutoption() {
        return putoption;
    }

    /**
     * 设置 是否可回售,Y是N否
     *
     * @param putoption 是否可回售,Y是N否
     */
    public void setPutoption(String putoption) {
        this.putoption = putoption == null ? null : putoption.trim();
    }

    /**
     * 取得 登记托管机构
     *
     * @return 登记托管机构
     */
    public String getInstitution() {
        return institution;
    }

    /**
     * 设置 登记托管机构
     *
     * @param institution 登记托管机构
     */
    public void setInstitution(String institution) {
        this.institution = institution == null ? null : institution.trim();
    }

    /**
     * 取得 登记托管机构说明
     *
     * @return 登记托管机构说明
     */
    public String getInstitutiontext() {
        return institutiontext;
    }

    /**
     * 设置 登记托管机构说明
     *
     * @param institutiontext 登记托管机构说明
     */
    public void setInstitutiontext(String institutiontext) {
        this.institutiontext = institutiontext == null ? null : institutiontext.trim();
    }

    /**
     * 取得 交易场所说明
     *
     * @return 交易场所说明
     */
    public String getMarkettext() {
        return markettext;
    }

    /**
     * 设置 交易场所说明
     *
     * @param markettext 交易场所说明
     */
    public void setMarkettext(String markettext) {
        this.markettext = markettext == null ? null : markettext.trim();
    }

    /**
     * 取得 交易流通场所：01 银行间市场、02 商业银行柜台市场、03 上海交易所、04 深圳交易所、99 其它
     *
     * @return 交易流通场所：01 银行间市场、02 商业银行柜台市场、03 上海交易所、04 深圳交易所、99 其它
     */
    public String getMarket2() {
        return market2;
    }

    /**
     * 设置 交易流通场所：01 银行间市场、02 商业银行柜台市场、03 上海交易所、04 深圳交易所、99 其它
     *
     * @param market2 交易流通场所：01 银行间市场、02 商业银行柜台市场、03 上海交易所、04 深圳交易所、99 其它
     */
    public void setMarket2(String market2) {
        this.market2 = market2 == null ? null : market2.trim();
    }

    /**
     * 取得 "币种
     * CNY 人民币、HKD 港币、JPY 日元、USD 美元"
     *
     * @return "币种
     * CNY 人民币、HKD 港币、JPY 日元、USD 美元"
     */
    public String getCcy() {
        return ccy;
    }

    /**
     * 设置 "币种
     * CNY 人民币、HKD 港币、JPY 日元、USD 美元"
     *
     * @param ccy "币种
     *            CNY 人民币、HKD 港币、JPY 日元、USD 美元"
     */
    public void setCcy(String ccy) {
        this.ccy = ccy == null ? null : ccy.trim();
    }

    /**
     * 取得 发行方式
     *
     * @return 发行方式
     */
    public String getIssuetype() {
        return issuetype;
    }

    /**
     * 设置 发行方式
     *
     * @param issuetype 发行方式
     */
    public void setIssuetype(String issuetype) {
        this.issuetype = issuetype == null ? null : issuetype.trim();
    }

    /**
     * 取得 境内外
     *
     * @return 境内外
     */
    public String getBlnarea() {
        return blnarea;
    }

    /**
     * 设置 境内外
     *
     * @param blnarea 境内外
     */
    public void setBlnarea(String blnarea) {
        this.blnarea = blnarea == null ? null : blnarea.trim();
    }

    /**
     * 取得 担保人
     *
     * @return 担保人
     */
    public String getGuarantor() {
        return guarantor;
    }

    /**
     * 设置 担保人
     *
     * @param guarantor 担保人
     */
    public void setGuarantor(String guarantor) {
        this.guarantor = guarantor == null ? null : guarantor.trim();
    }

    /**
     * 取得 担保方式
     *
     * @return 担保方式
     */
    public String getGuartype() {
        return guartype;
    }

    /**
     * 设置 担保方式
     *
     * @param guartype 担保方式
     */
    public void setGuartype(String guartype) {
        this.guartype = guartype == null ? null : guartype.trim();
    }

    /**
     * 取得 发行截止日
     *
     * @return 发行截止日
     */
    public Date getPubenddate() {
        return pubenddate;
    }

    /**
     * 设置 发行截止日
     *
     * @param pubenddate 发行截止日
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setPubenddate(Date pubenddate) {
        this.pubenddate = pubenddate;
    }

    /**
     * 取得 是否城投债（中债）
     *
     * @return 是否城投债（中债）
     */
    public String getCityInvestSecChb() {
        return cityInvestSecChb;
    }

    /**
     * 设置 是否城投债（中债）
     *
     * @param cityInvestSecChb 是否城投债（中债）
     */
    public void setCityInvestSecChb(String cityInvestSecChb) {
        this.cityInvestSecChb = cityInvestSecChb == null ? null : cityInvestSecChb.trim();
    }

    /**
     * 取得 城投债省及省会（单列市）
     *
     * @return 城投债省及省会（单列市）
     */
    public String getCityInvestSecProvinces() {
        return cityInvestSecProvinces;
    }

    /**
     * 设置 城投债省及省会（单列市）
     *
     * @param cityInvestSecProvinces 城投债省及省会（单列市）
     */
    public void setCityInvestSecProvinces(String cityInvestSecProvinces) {
        this.cityInvestSecProvinces = cityInvestSecProvinces == null ? null : cityInvestSecProvinces.trim();
    }

    /**
     * 取得 城投债地级市
     *
     * @return 城投债地级市
     */
    public String getCityInvestSecCities() {
        return cityInvestSecCities;
    }

    /**
     * 设置 城投债地级市
     *
     * @param cityInvestSecCities 城投债地级市
     */
    public void setCityInvestSecCities(String cityInvestSecCities) {
        this.cityInvestSecCities = cityInvestSecCities == null ? null : cityInvestSecCities.trim();
    }

    /**
     * 取得 城投债县及县级市
     *
     * @return 城投债县及县级市
     */
    public String getCityInvestSecCounty() {
        return cityInvestSecCounty;
    }

    /**
     * 设置 城投债县及县级市
     *
     * @param cityInvestSecCounty 城投债县及县级市
     */
    public void setCityInvestSecCounty(String cityInvestSecCounty) {
        this.cityInvestSecCounty = cityInvestSecCounty == null ? null : cityInvestSecCounty.trim();
    }

    /**
     * 取得 是否城投债（WIND）
     *
     * @return 是否城投债（WIND）
     */
    public String getCityInvestSecWind() {
        return cityInvestSecWind;
    }

    /**
     * 设置 是否城投债（WIND）
     *
     * @param cityInvestSecWind 是否城投债（WIND）
     */
    public void setCityInvestSecWind(String cityInvestSecWind) {
        this.cityInvestSecWind = cityInvestSecWind == null ? null : cityInvestSecWind.trim();
    }

    /**
     * 取得 是否城投债（银监）
     *
     * @return 是否城投债（银监）
     */
    public String getCityInvestSecCbrc() {
        return cityInvestSecCbrc;
    }

    /**
     * 设置 是否城投债（银监）
     *
     * @param cityInvestSecCbrc 是否城投债（银监）
     */
    public void setCityInvestSecCbrc(String cityInvestSecCbrc) {
        this.cityInvestSecCbrc = cityInvestSecCbrc == null ? null : cityInvestSecCbrc.trim();
    }

    /**
     * 取得 资产/负债类别
     *
     * @return 资产/负债类别
     */
    public String getAssetClass() {
        return assetClass;
    }

    /**
     * 设置 资产/负债类别
     *
     * @param assetClass 资产/负债类别
     */
    public void setAssetClass(String assetClass) {
        this.assetClass = assetClass == null ? null : assetClass.trim();
    }

    /**
     * 取得 具体类别
     *
     * @return 具体类别
     */
    public String getConcreteClass() {
        return concreteClass;
    }

    /**
     * 设置 具体类别
     *
     * @param concreteClass 具体类别
     */
    public void setConcreteClass(String concreteClass) {
        this.concreteClass = concreteClass == null ? null : concreteClass.trim();
    }

    /**
     * 取得 具体类别说明
     *
     * @return 具体类别说明
     */
    public String getConcreteClassSm() {
        return concreteClassSm;
    }

    /**
     * 设置 具体类别说明
     *
     * @param concreteClassSm 具体类别说明
     */
    public void setConcreteClassSm(String concreteClassSm) {
        this.concreteClassSm = concreteClassSm == null ? null : concreteClassSm.trim();
    }

    /**
     * 取得 发行机构类型（按规模划分）
     *
     * @return 发行机构类型（按规模划分）
     */
    public String getIssuerScaleType() {
        return issuerScaleType;
    }

    /**
     * 设置 发行机构类型（按规模划分）
     *
     * @param issuerScaleType 发行机构类型（按规模划分）
     */
    public void setIssuerScaleType(String issuerScaleType) {
        this.issuerScaleType = issuerScaleType == null ? null : issuerScaleType.trim();
    }

    /**
     * 取得 发行机构类型（按技术领域划分）
     *
     * @return 发行机构类型（按技术领域划分）
     */
    public String getIssuerTechnologyType() {
        return issuerTechnologyType;
    }

    /**
     * 设置 发行机构类型（按技术领域划分）
     *
     * @param issuerTechnologyType 发行机构类型（按技术领域划分）
     */
    public void setIssuerTechnologyType(String issuerTechnologyType) {
        this.issuerTechnologyType = issuerTechnologyType == null ? null : issuerTechnologyType.trim();
    }

    /**
     * 取得 发行机构类型（按经济领域划分）
     *
     * @return 发行机构类型（按经济领域划分）
     */
    public String getIssuerEconomicType() {
        return issuerEconomicType;
    }

    /**
     * 设置 发行机构类型（按经济领域划分）
     *
     * @param issuerEconomicType 发行机构类型（按经济领域划分）
     */
    public void setIssuerEconomicType(String issuerEconomicType) {
        this.issuerEconomicType = issuerEconomicType == null ? null : issuerEconomicType.trim();
    }

    /**
     * 取得 发行机构类型说明
     *
     * @return 发行机构类型说明
     */
    public String getIssuerDesc() {
        return issuerDesc;
    }

    /**
     * 设置 发行机构类型说明
     *
     * @param issuerDesc 发行机构类型说明
     */
    public void setIssuerDesc(String issuerDesc) {
        this.issuerDesc = issuerDesc == null ? null : issuerDesc.trim();
    }

    /**
     * 取得 发行机构所属行业
     *
     * @return 发行机构所属行业
     */
    public String getIssuerInstitutions() {
        return issuerInstitutions;
    }

    /**
     * 设置 发行机构所属行业
     *
     * @param issuerInstitutions 发行机构所属行业
     */
    public void setIssuerInstitutions(String issuerInstitutions) {
        this.issuerInstitutions = issuerInstitutions == null ? null : issuerInstitutions.trim();
    }

    // 20200819 龙江银行修改-查询债券接口信息表增加查询字段 wangwei start

    /**
     * 取得 发行机构所属行业二级分类
     *
     * @return 发行机构所属行业二级分类
     */
    public String getIssuerinstitutionssecond() {
        return issuerinstitutionssecond;
    }

    /**
     * 设置 发行机构所属行业二级分类
     *
     * @param issuerinstitutionssecond 发行机构所属行业二级分类
     */
    public void setIssuerinstitutionssecond(String issuerinstitutionssecond) {
        this.issuerinstitutionssecond = issuerinstitutionssecond;
    }

    // 20200819 龙江银行修改-查询债券接口信息表增加查询字段 wangwei end
    public String getIsPpb() {
        return isPpb;
    }

    public void setIsPpb(String isPpb) {
        this.isPpb = isPpb;
    }

    public String getIsNetprice() {
        return isNetprice;
    }

    public void setIsNetprice(String isNetprice) {
        this.isNetprice = isNetprice;
    }

    public String getConFinprodId() {
        return conFinprodId;
    }

    public void setConFinprodId(String conFinprodId) {
        this.conFinprodId = conFinprodId;
    }

    public String getConShareId() {
        return conShareId;
    }

    public void setConShareId(String conShareId) {
        this.conShareId = conShareId;
    }

    public Date getConVdate() {
        return conVdate;
    }

    public void setConVdate(Date conVdate) {
        this.conVdate = conVdate;
    }

    public BigDecimal getConPrice() {
        return conPrice;
    }

    public void setConPrice(BigDecimal conPrice) {
        this.conPrice = conPrice;
    }

    public BigDecimal getResalePrice() {
        return resalePrice;
    }

    public void setResalePrice(BigDecimal resalePrice) {
        this.resalePrice = resalePrice;
    }

    public BigDecimal getRedeemPrice() {
        return redeemPrice;
    }

    public void setRedeemPrice(BigDecimal redeemPrice) {
        this.redeemPrice = redeemPrice;
    }

    public Date getDelistingDate() {
        return delistingDate;
    }

    public void setDelistingDate(Date delistingDate) {
        this.delistingDate = delistingDate;
    }

    public String getIsGreenBond() {
        return isGreenBond;
    }

    public void setIsGreenBond(String isGreenBond) {
        this.isGreenBond = isGreenBond;
    }

    public String getCrossMarketCode() {
        return crossMarketCode;
    }

    public void setCrossMarketCode(String crossMarketCode) {
        this.crossMarketCode = crossMarketCode;
    }

    public BigDecimal getBaseRateAccuracy() {
        return baseRateAccuracy;
    }

    public void setBaseRateAccuracy(BigDecimal baseRateAccuracy) {
        this.baseRateAccuracy = baseRateAccuracy;
    }

    public Date getFirstRateDeterminationDate() {
        return firstRateDeterminationDate;
    }

    public void setFirstRateDeterminationDate(Date firstRateDeterminationDate) {
        this.firstRateDeterminationDate = firstRateDeterminationDate;
    }

    public String getIsRight() {
        return isRight;
    }

    public void setIsRight(String isRight) {
        this.isRight = isRight;
    }

    public String getOriginalOwner() {
        return originalOwner;
    }

    public void setOriginalOwner(String originalOwner) {
        this.originalOwner = originalOwner;
    }
}