package com.joyintech.tams.touguan.basicdata.marketdata.bondmarketinfoquery.dto.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.base.dto.BaseDto;
import com.joyintech.framework.json.BigDecimalSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 债券行权计划信息DTO
 *
 * @author zhuys
 * @version 1.0 2022年4月18日
 * @see FinBondPayHSrcSecOptionDto
 * @since 1.0
 */
@ApiModel(value = "债券市场行权计划信息", description = "债券市场行权计划信息")
public class FinBondPayHSrcSecOptionDto extends BaseDto {

    /**
     * 行权申请起始日
     */
    @ApiModelProperty(name = "optionStartDay", value = "行权申请起始日")
    private Date optionStartDay;

    /**
     * 行权申请终止日
     */
    @ApiModelProperty(name = "optionEndDay", value = "行权申请终止日")
    private Date optionEndDay;

    /**
     * 理论行权日
     */
    @ApiModelProperty(name = "theOryOptionPayDay", value = "理论行权日")
    private Date theOryOptionPayDay;

    /**
     * 实际行权日
     */
    @ApiModelProperty(name = "optionPayDay", value = "实际行权日")
    private Date optionPayDay;

    /**
     * 行权价(净价)
     */
    @ApiModelProperty(name = "optionPrice", value = "行权价(净价)")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal optionPrice;

    /**
     * 行权类型
     */
    @ApiModelProperty(name = "optionType", value = "行权类型")
    private String optionType;

    /**
     * 行权条款
     */
    @ApiModelProperty(name = "optionClause", value = "行权条款")
    private String optionClause;


    public Date getOptionStartDay() {
        return optionStartDay;
    }

    public void setOptionStartDay(Date optionStartDay) {
        this.optionStartDay = optionStartDay;
    }

    public Date getOptionEndDay() {
        return optionEndDay;
    }

    public void setOptionEndDay(Date optionEndDay) {
        this.optionEndDay = optionEndDay;
    }

    public Date getTheOryOptionPayDay() {
        return theOryOptionPayDay;
    }

    public void setTheOryOptionPayDay(Date theOryOptionPayDay) {
        this.theOryOptionPayDay = theOryOptionPayDay;
    }

    public Date getOptionPayDay() {
        return optionPayDay;
    }

    public void setOptionPayDay(Date optionPayDay) {
        this.optionPayDay = optionPayDay;
    }

    public BigDecimal getOptionPrice() {
        return optionPrice;
    }

    public void setOptionPrice(BigDecimal optionPrice) {
        this.optionPrice = optionPrice;
    }

    public String getOptionType() {
        return optionType;
    }

    public void setOptionType(String optionType) {
        this.optionType = optionType;
    }

    public String getOptionClause() {
        return optionClause;
    }

    public void setOptionClause(String optionClause) {
        this.optionClause = optionClause;
    }
}