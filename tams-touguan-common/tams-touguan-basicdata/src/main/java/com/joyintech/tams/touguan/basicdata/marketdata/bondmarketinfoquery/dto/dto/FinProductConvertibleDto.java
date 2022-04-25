package com.joyintech.tams.touguan.basicdata.marketdata.bondmarketinfoquery.dto.dto;

import io.swagger.annotations.ApiModel;

import java.math.BigDecimal;
import java.util.Date;


/**
 * 债券市场转股信息DTO
 *
 * @author zhuys
 * @version 1.0 2022年4月18日
 * @see FinBondPayHSrcSecOptionDto
 * @since 1.0
 */
@ApiModel(value = "债券市场转股信息", description = "债券市场转股信息")
public class FinProductConvertibleDto {

    /**
     * 正股代码
     */
    private String shareId;

    /**
     * 正股名称
     */
    private String shareName;

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
     * 转股比例
     */
    private BigDecimal conProportion;

    public String getShareId() {
        return shareId;
    }

    public void setShareId(String shareId) {
        this.shareId = shareId;
    }

    public String getShareName() {
        return shareName;
    }

    public void setShareName(String shareName) {
        this.shareName = shareName;
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

    public BigDecimal getConProportion() {
        return conProportion;
    }

    public void setConProportion(BigDecimal conProportion) {
        this.conProportion = conProportion;
    }
}