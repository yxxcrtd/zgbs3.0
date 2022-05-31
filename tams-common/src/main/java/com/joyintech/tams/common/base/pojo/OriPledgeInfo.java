package com.joyintech.tams.common.base.pojo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.json.BigDecimalSerializer;

import java.math.BigDecimal;

/**
 * 〈功能详细描述〉
 *
 * @author yangxunpeng
 * @version 1.0 2022年05月28日
 * @since 1.0
 */
public class OriPledgeInfo {

    /**
     * 金融产品代码
     */
    private String finprodId;

    /**
     * 质押金融产品代码
     */
    private String pledgeFinprodId;

    /**
     *回购金额（质押式回购）
     */
    private BigDecimal prin;

    /**
     * 劵面总额
     */
    private BigDecimal pledgeTradeAmt;

    /**
     * 质押面值
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal pledgeFaceValue;

    /**
     * 质押数量
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal pledgeNumber;

    /**
     * 质押率
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal pledgeRatio;

    /**
     * 质押金额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal pledgeAmt;

    /**
     * 置换方向
     */
    private String displaceDir;

    public String getDisplaceDir() {
        return displaceDir;
    }

    public void setDisplaceDir(String displaceDir) {
        this.displaceDir = displaceDir;
    }

    /**
     * 取得 质押面值
     * @return 质押面值
     */
    public BigDecimal getPledgeFaceValue() {
        return pledgeFaceValue;
    }

    /**
     * 设置 质押面值
     * @param pledgeFaceValue 质押面值
     */
    public void setPledgeFaceValue(BigDecimal pledgeFaceValue) {
        this.pledgeFaceValue = pledgeFaceValue;
    }

    /**
     * 取得 质押数量
     * @return 质押数量
     */
    public BigDecimal getPledgeNumber() {
        return pledgeNumber;
    }

    /**
     * 设置 质押数量
     * @param pledgeNumber 质押数量
     */
    public void setPledgeNumber(BigDecimal pledgeNumber) {
        this.pledgeNumber = pledgeNumber;
    }

    /**
     * 取得 质押率
     * @return 质押率
     */
    public BigDecimal getPledgeRatio() {
        return pledgeRatio;
    }

    /**
     * 设置 质押率
     * @param pledgeRatio 质押率
     */
    public void setPledgeRatio(BigDecimal pledgeRatio) {
        this.pledgeRatio = pledgeRatio;
    }

    /**
     * 取得 质押金额
     * @return 质押金额
     */
    public BigDecimal getPledgeAmt() {
        return pledgeAmt;
    }

    /**
     * 设置 质押金额
     * @param pledgeAmt 质押金额
     */
    public void setPledgeAmt(BigDecimal pledgeAmt) {
        this.pledgeAmt = pledgeAmt;
    }

    public String getFinprodId() {
        return finprodId;
    }

    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
    }

    public String getPledgeFinprodId() {
        return pledgeFinprodId;
    }

    public void setPledgeFinprodId(String pledgeFinprodId) {
        this.pledgeFinprodId = pledgeFinprodId;
    }

    public BigDecimal getPrin() {
        return prin;
    }

    public void setPrin(BigDecimal prin) {
        this.prin = prin;
    }

    public BigDecimal getPledgeTradeAmt() {
        return pledgeTradeAmt;
    }

    public void setPledgeTradeAmt(BigDecimal pledgeTradeAmt) {
        this.pledgeTradeAmt = pledgeTradeAmt;
    }
}
