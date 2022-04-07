package com.joyintech.tams.common.base.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.json.BigDecimalSerializer;

import java.math.BigDecimal;
import java.util.Date;

/**
 * CFETS新债标的发行信息
 */
public class InterfCfetsNewbondIssue extends BaseEntity {
    
    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    private static final long serialVersionUID = 6888665039083565141L;

    /**
     * 工具代码
     */
    private String marketId;

    /**
     * 工具简称
     */
    private String finprodAbbr;

    /**
     * 工具全称
     */
    private String finprodName;

    /**
     * 发行人
     */
    private String issuer;

    /**
     * 发行人21位码
     */
    private String issuerCode;

    /**
     * 发行方式，报价发行/招标发行/簿记建档
     */
    private String issueClass;

    /**
     * 招标方式，单一价格招标/数量招标/荷兰式招标/混合式招标
     */
    private String tenderType;

    /**
     * 招标标的，利率(%)/价格(元)/利差(BP)
     */
    private String tenderTarget;

    /**
     * 期限
     */
    private String period;

    /**
     * 计划发行量
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal planIssueAmt;

    /**
     * 面值
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal parValue;

    /**
     * 发行日
     */
    private Date issueDate;

    /**
     * 起息日
     */
    private Date vdate;

    /**
     * 到期日
     */
    private Date mdate;

    /**
     * 兑付日
     */
    private Date pdate;

    /**
     * 息票类型
     */
    private String couponSpecies;

    /**
     * 票面利率
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal yield;

    /**
     * 发行价格
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal issuePrice;

    /**
     * 付息频率,月付/季付/年付/半年付/到期一次
     */
    private String payCycle;

    /**
     * 基准利率
     */
    private String intRateId;

    /**
     * 基准利率精度
     */
    private String rateGrade;

    /**
     * 基础利率确定方式
     */
    private String intRateConftype;

    /**
     * 利差
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal bp;

    /**
     * 首次利率确定日
     */
    private Date firstConfirmDate;

    /**
     * 首次付息日
     */
    private Date firstIntPaydate;

    /**
     * 计息基准
     */
    private String basis;

    /**
     * 主体评级机构
     */
    private String orgId;

    /**
     * 主体评级等级
     */
    private String orgGrade;

    /**
     * 参考收益率
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal referYield;

    /**
     * 工具状态
     */
    private String issueStatus;

    /**
     * 工具类型
     */
    private String finprodType;

    /**
     * 流通范围
     */
    private String rangeCircle;

    /**
     * 缴款日
     */
    private Date payDate;

    /**
     * 币种
     */
    private String ccy;

    /**
     * 每标位最低投标量
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal minPerTamt;

    /**
     * 每标位最高投标量
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal maxPerUtamt;

    /**
     * 最低标位
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal minTamt;

    /**
     * 最低标位参考收益率
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal minTReferYiled;

    /**
     * 最高标位
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal maxTamt;

    /**
     * 最高标位参考收益率
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal maxTReferYiled;

    /**
     * 投标步长
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal tenderUnit;

    /**
     * 最大投标标位数
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal maxTenderUnit;

    /**
     * 最大投标标位差
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal maxTenderPlus;

    /**
     * 连续投标限制
     */
    private String seriYN;

    /**
     * 最高投标总量
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal maxTotalTender;

    /**
     * 单笔最低认购量
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal minPamt;

    /**
     * 单笔最高认购量
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal maxPamt;

    /**
     * 最高认购总量
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal maxTotalamt;

    /**
     * 联系人1
     */
    private String contactName1;

    /**
     * 电话1
     */
    private String contactPhone1;

    /**
     * 传真1
     */
    private String fax1;

    /**
     * 联系人2
     */
    private String contactName2;

    /**
     * 电话2
     */
    private String contactPhone2;

    /**
     * 传真2
     */
    private String fax2;

    /**
     * 收款账户名称
     */
    private String reAccountName;

    /**
     * 收款人账号
     */
    private String reAccountNo;

    /**
     * 汇入行名称
     */
    private String bankName;

    /**
     * 支付系统清算行行号
     */
    private String payNo;

    /**
     * 备注
     */
    private String remark;


    public String getMarketId() {
        return marketId;
    }

    public void setMarketId(String marketId) {
        this.marketId = marketId;
    }
}