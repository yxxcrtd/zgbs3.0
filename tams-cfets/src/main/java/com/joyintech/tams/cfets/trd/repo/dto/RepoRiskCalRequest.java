package com.joyintech.tams.cfets.trd.repo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author jiangpeng
 * @date 2021/12/13
 */
@ApiModel(value="风险试算请求参数",description="质押式回购风险试算请求参数")
public class RepoRiskCalRequest {

    @ApiModelProperty(required = true,name="tradeDate",value="交易日期",example = "2021-07-08")
    private Date tradeDate;

    @ApiModelProperty(required = true,name="ps",value="交易方向",example = "03")
    private String ps;

    @ApiModelProperty(required = true,name="price",value="回购利率",example = "3")
    private BigDecimal price;

    @ApiModelProperty(required = true,name="counterId",value="对手方",example = "001")
    private String counterId;

    @ApiModelProperty(required = true,name="ptlAllocations",value="组合分配")
    private List<RepoPtlAllocationDto> ptlAllocations;

    @ApiModelProperty(name = "settlementStartDate", value = "首次结算日",example = "2021-07-08")
    private Date settlementStartDate;

    @ApiModelProperty(name = "settlementEndDate", value = "到期结算日",example = "2021-07-15")
    private Date settlementEndDate;

    public Date getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(Date tradeDate) {
        this.tradeDate = tradeDate;
    }

    public String getPs() {
        return ps;
    }

    public void setPs(String ps) {
        this.ps = ps;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getCounterId() {
        return counterId;
    }

    public void setCounterId(String counterId) {
        this.counterId = counterId;
    }

    public List<RepoPtlAllocationDto> getPtlAllocations() {
        return ptlAllocations;
    }

    public void setPtlAllocations(List<RepoPtlAllocationDto> ptlAllocations) {
        this.ptlAllocations = ptlAllocations;
    }

    public Date getSettlementStartDate() {
        return settlementStartDate;
    }

    public void setSettlementStartDate(Date settlementStartDate) {
        this.settlementStartDate = settlementStartDate;
    }

    public Date getSettlementEndDate() {
        return settlementEndDate;
    }

    public void setSettlementEndDate(Date settlementEndDate) {
        this.settlementEndDate = settlementEndDate;
    }
}

