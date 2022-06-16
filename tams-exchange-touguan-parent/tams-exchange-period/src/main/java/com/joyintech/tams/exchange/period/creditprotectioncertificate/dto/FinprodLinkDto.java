package com.joyintech.tams.exchange.period.creditprotectioncertificate.dto;

import com.joyintech.framework.select.SelectOptionDto;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;
import java.util.List;

/**
 * 〈功能详细描述〉
 *
 * @author yangxunpeng
 * @version 1.0 2022年05月18日
 * @since 1.0
 */
public class FinprodLinkDto {
    /**
     * 金融产品代码
     */
    @ApiModelProperty(name = "finprodId", value = "金融产品代码")
    private String finprodId;

    /**
     * 金融产品类型（估值核算）
     */
    @ApiModelProperty(name = "finprodType", value = "融产品类型（估值核算）")
    private String finprodType;

    /**
     * 金融产品类型（投管分类）
     */
    @ApiModelProperty(name = "finprodType2", value = "金融产品类型（投管分类）")
    private String finprodType2;

    /**
     * 金融产品简称
     */
    @ApiModelProperty(name = "finprodAbbr", value = "金融产品简称")
    private String finprodAbbr;

    /**
     * 金融产品全称
     */
    @ApiModelProperty(name = "finprodName", value = "金融产品全称")
    private String finprodName;
    /**
     * 市场代码
     */
    @ApiModelProperty(name = "finprodMarketId", value = "市场代码")
    private String finprodMarketId;

    /**
     * 交易场所
     */
    @ApiModelProperty(name = "tradeMarket", value = "交易场所")
    private String tradeMarket;

    /**
     * 起息日
     */
    @ApiModelProperty(name = "vdate", value = "起息日")
    private Date vdate;

    /**
     * 到期日
     */
    @ApiModelProperty(name = "mdate", value = "到期日")
    private Date mdate;

    /**
     * 参考债项
     */
    @ApiModelProperty(name = "refSec", value = "参考债项")
    private String refSec;

    /**
     * 结算明细
     */
    @ApiModelProperty(name = "billingDetails", value = "结算明细")
    private List<BillingDetailsDto> billingDetails;

    /**
     * 参考实体
     */
    @ApiModelProperty(name = "refEntity", value = "参考实体")
    private String refEntity;

    /**
     * 参考实体
     */
    @ApiModelProperty(name = "refEntityStr", value = "参考实体Str")
    private String refEntityStr;


    /**
     * 参考债项选择列表
     */
    @ApiModelProperty(name = "refSecs", value = "参考债项选择列表")
    private List<SelectOptionDto> refSecs;

    @ApiModelProperty(name = "settleMethod", value = "结算方式")
    private String settleMethod;

    public String getSettleMethod() {
        return settleMethod;
    }

    public void setSettleMethod(String settleMethod) {
        this.settleMethod = settleMethod;
    }

    public List<SelectOptionDto> getRefSecs() {
        return refSecs;
    }

    public void setRefSecs(List<SelectOptionDto> refSecs) {
        this.refSecs = refSecs;
    }

    public String getRefEntity() {
        return refEntity;
    }

    public void setRefEntity(String refEntity) {
        this.refEntity = refEntity;
    }

    public List<BillingDetailsDto> getBillingDetails() {
        return billingDetails;
    }

    public void setBillingDetails(List<BillingDetailsDto> billingDetails) {
        this.billingDetails = billingDetails;
    }

    public String getRefSec() {
        return refSec;
    }

    public void setRefSec(String refSec) {
        this.refSec = refSec;
    }

    public String getFinprodId() {
        return finprodId;
    }

    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
    }

    public String getFinprodType() {
        return finprodType;
    }

    public void setFinprodType(String finprodType) {
        this.finprodType = finprodType;
    }

    public String getFinprodType2() {
        return finprodType2;
    }

    public void setFinprodType2(String finprodType2) {
        this.finprodType2 = finprodType2;
    }

    public String getFinprodAbbr() {
        return finprodAbbr;
    }

    public void setFinprodAbbr(String finprodAbbr) {
        this.finprodAbbr = finprodAbbr;
    }

    public String getFinprodName() {
        return finprodName;
    }

    public void setFinprodName(String finprodName) {
        this.finprodName = finprodName;
    }

    public String getFinprodMarketId() {
        return finprodMarketId;
    }

    public void setFinprodMarketId(String finprodMarketId) {
        this.finprodMarketId = finprodMarketId;
    }

    public String getTradeMarket() {
        return tradeMarket;
    }

    public void setTradeMarket(String tradeMarket) {
        this.tradeMarket = tradeMarket;
    }

    public Date getVdate() {
        return vdate;
    }

    public void setVdate(Date vdate) {
        this.vdate = vdate;
    }

    public Date getMdate() {
        return mdate;
    }

    public void setMdate(Date mdate) {
        this.mdate = mdate;
    }

    public String getRefEntityStr() {
        return refEntityStr;
    }

    public void setRefEntityStr(String refEntityStr) {
        this.refEntityStr = refEntityStr;
    }
}
