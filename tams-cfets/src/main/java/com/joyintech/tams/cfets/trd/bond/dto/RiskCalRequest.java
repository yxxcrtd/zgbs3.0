package com.joyintech.tams.cfets.trd.bond.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author zznn
 * @date 2021/11/16
 */
@ApiModel(value="风险试算请求参数",description="风险试算请求参数")
public class RiskCalRequest {

    @ApiModelProperty(required = true,name="finprodId",value="金融产品代码")
    private String finprodId;

    @ApiModelProperty(required = true,name="ps",value="交易方向")
    private String ps;

    @ApiModelProperty(required = true,name="price",value="净价")
    private BigDecimal price;

    @ApiModelProperty(required = true,name="counterId",value="对手方")
    private String counterId;

    @ApiModelProperty(required = true,name="ptlAllocations",value="组合分配")
    private List<PtlAllocationDto> ptlAllocations;

    public String getFinprodId() {
        return finprodId;
    }

    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
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

    public List<PtlAllocationDto> getPtlAllocations() {
        return ptlAllocations;
    }

    public void setPtlAllocations(List<PtlAllocationDto> ptlAllocations) {
        this.ptlAllocations = ptlAllocations;
    }
}

