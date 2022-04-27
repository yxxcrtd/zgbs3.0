package com.joyintech.tams.common.base.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.framework.json.CustomDateDeserialize;
import java.math.BigDecimal;
import java.util.Date;

public class BokStatStockPosition extends BokStatStockPositionKey {
    /**
     * 金融产品代码
     */
    private String finprodId;

    /**
     * 金融产品类型
     */
    private String finprodType;

    /**
     * 持仓份额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal shareAmt;

    /**
     * 成本余额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal costAmt;

    /**
     * 当日发生公允价值变动
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal tdyFloatIngpl;

    /**
     * 累计公允价值变动
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal floatIngplExp;

    /**
     * 当日公允价值变动_本位币
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal tdyFloatIngplB;

    /**
     * 成本余额_本位币
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal costAmtB;

    /**
     * 累计公允价值变动_本位币
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal floatIngplExpB;

    /**
     * 行情日期
     */
    private Date valDate;

    /**
     * 行情价格
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal valPrice;

    /**
     * 币种
     */
    private String ccy;

    /**
     * 本位币
     */
    private String bCcy;

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
     * 取得 金融产品类型
     * @return 金融产品类型
     */
    public String getFinprodType() {
        return finprodType;
    }

    /**
     * 设置 金融产品类型
     * @param finprodType 金融产品类型
     */
    public void setFinprodType(String finprodType) {
        this.finprodType = finprodType == null ? null : finprodType.trim();
    }

    /**
     * 取得 持仓份额
     * @return 持仓份额
     */
    public BigDecimal getShareAmt() {
        return shareAmt;
    }

    /**
     * 设置 持仓份额
     * @param shareAmt 持仓份额
     */
    public void setShareAmt(BigDecimal shareAmt) {
        this.shareAmt = shareAmt;
    }

    /**
     * 取得 成本余额
     * @return 成本余额
     */
    public BigDecimal getCostAmt() {
        return costAmt;
    }

    /**
     * 设置 成本余额
     * @param costAmt 成本余额
     */
    public void setCostAmt(BigDecimal costAmt) {
        this.costAmt = costAmt;
    }

    /**
     * 取得 当日发生公允价值变动
     * @return 当日发生公允价值变动
     */
    public BigDecimal getTdyFloatIngpl() {
        return tdyFloatIngpl;
    }

    /**
     * 设置 当日发生公允价值变动
     * @param tdyFloatIngpl 当日发生公允价值变动
     */
    public void setTdyFloatIngpl(BigDecimal tdyFloatIngpl) {
        this.tdyFloatIngpl = tdyFloatIngpl;
    }

    /**
     * 取得 累计公允价值变动
     * @return 累计公允价值变动
     */
    public BigDecimal getFloatIngplExp() {
        return floatIngplExp;
    }

    /**
     * 设置 累计公允价值变动
     * @param floatIngplExp 累计公允价值变动
     */
    public void setFloatIngplExp(BigDecimal floatIngplExp) {
        this.floatIngplExp = floatIngplExp;
    }

    /**
     * 取得 当日公允价值变动_本位币
     * @return 当日公允价值变动_本位币
     */
    public BigDecimal getTdyFloatIngplB() {
        return tdyFloatIngplB;
    }

    /**
     * 设置 当日公允价值变动_本位币
     * @param tdyFloatIngplB 当日公允价值变动_本位币
     */
    public void setTdyFloatIngplB(BigDecimal tdyFloatIngplB) {
        this.tdyFloatIngplB = tdyFloatIngplB;
    }

    /**
     * 取得 成本余额_本位币
     * @return 成本余额_本位币
     */
    public BigDecimal getCostAmtB() {
        return costAmtB;
    }

    /**
     * 设置 成本余额_本位币
     * @param costAmtB 成本余额_本位币
     */
    public void setCostAmtB(BigDecimal costAmtB) {
        this.costAmtB = costAmtB;
    }

    /**
     * 取得 累计公允价值变动_本位币
     * @return 累计公允价值变动_本位币
     */
    public BigDecimal getFloatIngplExpB() {
        return floatIngplExpB;
    }

    /**
     * 设置 累计公允价值变动_本位币
     * @param floatIngplExpB 累计公允价值变动_本位币
     */
    public void setFloatIngplExpB(BigDecimal floatIngplExpB) {
        this.floatIngplExpB = floatIngplExpB;
    }

    /**
     * 取得 行情日期
     * @return 行情日期
     */
    public Date getValDate() {
        return valDate;
    }

    /**
     * 设置 行情日期
     * @param valDate 行情日期
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setValDate(Date valDate) {
        this.valDate = valDate;
    }

    /**
     * 取得 行情价格
     * @return 行情价格
     */
    public BigDecimal getValPrice() {
        return valPrice;
    }

    /**
     * 设置 行情价格
     * @param valPrice 行情价格
     */
    public void setValPrice(BigDecimal valPrice) {
        this.valPrice = valPrice;
    }

    /**
     * 取得 币种
     * @return 币种
     */
    public String getCcy() {
        return ccy;
    }

    /**
     * 设置 币种
     * @param ccy 币种
     */
    public void setCcy(String ccy) {
        this.ccy = ccy == null ? null : ccy.trim();
    }

    /**
     * 取得 本位币
     * @return 本位币
     */
    public String getbCcy() {
        return bCcy;
    }

    /**
     * 设置 本位币
     * @param bCcy 本位币
     */
    public void setbCcy(String bCcy) {
        this.bCcy = bCcy == null ? null : bCcy.trim();
    }
}