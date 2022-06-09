package com.joyintech.tams.cfets.trd.primarybidmanage.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

/**
 * 中标结果维护信息DTO
 *
 * @author wangyang1
 * @version 1.0 2022年5月5日
 * @see SummaryBidDto
 * @since 1.0
 */
@ApiModel(value="汇总投标列表查询返回数据",description="汇总投标列表查询返回数据")
public class SummaryBidDto {

    @ApiModelProperty(name="totalBidPrinamt",value="汇总投标券面总额")
    private BigDecimal totalBidPrinamt;

    @ApiModelProperty(name="winPrinAmt",value="指令投标券面总额(万元)")
    private BigDecimal winPrinAmt;

    @ApiModelProperty(name="winPrice",value="投标标位")
    private String winPrice;

    public BigDecimal getTotalBidPrinamt() {
        return totalBidPrinamt;
    }

    public void setTotalBidPrinamt(BigDecimal totalBidPrinamt) {
        this.totalBidPrinamt = totalBidPrinamt;
    }

    public BigDecimal getWinPrinAmt() {
        return winPrinAmt;
    }

    public void setWinPrinAmt(BigDecimal winPrinAmt) {
        this.winPrinAmt = winPrinAmt;
    }

    public String getWinPrice() {
        return winPrice;
    }

    public void setWinPrice(String winPrice) {
        this.winPrice = winPrice;
    }
}
