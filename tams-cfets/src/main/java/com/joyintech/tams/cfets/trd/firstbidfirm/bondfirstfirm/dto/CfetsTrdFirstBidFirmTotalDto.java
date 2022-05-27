package com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstfirm.dto;

import com.joyintech.workflow.engine.annotation.PrimaryKey;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

/**
 * @author jiangpeng
 * @date 2022年05月16日 11:46
 */
@ApiModel(value = "交易信息-汇总投标列表", description = "一级投标确认信息-汇总投标列表")
public class CfetsTrdFirstBidFirmTotalDto {
    @ApiModelProperty(name = "bidPrice", value = "投标标位", example = "1")
    private BigDecimal bidPrice;

    @ApiModelProperty(name = "totalBiddingNum", value = "汇总投标券面总额(万元)  需前端转化", example = "1")
    private BigDecimal totalBiddingNum;

    @ApiModelProperty(name = "etrBidPriceTotal", value = "指令投标券面总额(万元) 需前端转化", example = "1")
    private BigDecimal etrBidPriceTotal;

    @ApiModelProperty(name = "bidId", value = "投标编号,编辑页面/提交用", example = "111")
    private String bidId;

    public BigDecimal getBidPrice() {
        return bidPrice;
    }

    public void setBidPrice(BigDecimal bidPrice) {
        this.bidPrice = bidPrice;
    }

    public BigDecimal getTotalBiddingNum() {
        return totalBiddingNum;
    }

    public void setTotalBiddingNum(BigDecimal totalBiddingNum) {
        this.totalBiddingNum = totalBiddingNum;
    }

    public BigDecimal getEtrBidPriceTotal() {
        return etrBidPriceTotal;
    }

    public void setEtrBidPriceTotal(BigDecimal etrBidPriceTotal) {
        this.etrBidPriceTotal = etrBidPriceTotal;
    }

    public String getBidId() {
        return bidId;
    }

    public void setBidId(String bidId) {
        this.bidId = bidId;
    }
}
