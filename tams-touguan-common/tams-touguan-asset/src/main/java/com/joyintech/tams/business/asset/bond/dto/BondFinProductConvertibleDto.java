package com.joyintech.tams.business.asset.bond.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.business.common.dto.PageMode;
import com.joyintech.tams.common.base.entity.FinProductConvertible;
import com.joyintech.tams.constant.DictCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 债券标的信息DTO
 *
 * @author wangyang1
 * @version 1.0 2022年4月13日
 * @see BondFinProductConvertibleDto
 * @since 1.0
 */
@ApiModel(value="转股信息",description="转债信息")
public class BondFinProductConvertibleDto extends FinProductConvertible {

    @ApiModelProperty(name="finprodId",value="金融产品代码")
    private String finprodId;

    @ApiModelProperty(name="conFinprodId",value="正股代码(条件必填)")
    private String conFinprodId;

    @ApiModelProperty(name="conFinprodName",value="正股名称")
    private String conFinprodName;

    @ApiModelProperty(name="conVdate",value="转股起始日")
    private Date conVdate;

    @ApiModelProperty(name="conPrice",value="转股价格(条件必填)")
    private BigDecimal conPrice;

    @ApiModelProperty(name="conProportion",value="转股比例")
    private BigDecimal conProportion;

    @ApiModelProperty(name="resalePrice",value="回售触发价")
    private BigDecimal resalePrice;

    @ApiModelProperty(name="redeemPrice",value="赎回触发价")
    private BigDecimal redeemPrice;

    public String getFinprodId() {
        return finprodId;
    }

    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
    }

    public String getConFinprodId() {
        return conFinprodId;
    }

    public void setConFinprodId(String conFinprodId) {
        this.conFinprodId = conFinprodId;
    }

    public String getConFinprodName() {
        return conFinprodName;
    }

    public void setConFinprodName(String conFinprodName) {
        this.conFinprodName = conFinprodName;
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

    public BigDecimal getConProportion() {
        return conProportion;
    }

    public void setConProportion(BigDecimal conProportion) {
        this.conProportion = conProportion;
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
}
