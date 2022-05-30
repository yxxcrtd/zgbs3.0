package com.joyintech.tams.exchange.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.List;

@ApiModel(value = "公平分配", description = "公平分配")
public class PortfolioAllocationResponse {

    @ApiModelProperty(value = "指令列表")
    private List<FairPortfolioResponse> list;

    @ApiModelProperty(value = "委托价格")
    private BigDecimal price;

    @ApiModelProperty(value = "试算委托总数量")
    private Long totalEntrustVolume;

    @ApiModelProperty(value = "试算委托总金额")
    private BigDecimal totalEntrustAmt;

    @ApiModelProperty(value = "剩余委托总数量")
    private Long totalLeftVolume;

    @ApiModelProperty(value = "警告信息")
    private String infoDetails;

    @ApiModelProperty(value = "警告信息类别：1 错误；2 警告")
    private String infoLevel;

    public List<FairPortfolioResponse> getList() {
        return list;
    }

    public void setList(List<FairPortfolioResponse> list) {
        this.list = list;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Long getTotalEntrustVolume() {
        return totalEntrustVolume;
    }

    public void setTotalEntrustVolume(Long totalEntrustVolume) {
        this.totalEntrustVolume = totalEntrustVolume;
    }

    public BigDecimal getTotalEntrustAmt() {
        return totalEntrustAmt;
    }

    public void setTotalEntrustAmt(BigDecimal totalEntrustAmt) {
        this.totalEntrustAmt = totalEntrustAmt;
    }

    public Long getTotalLeftVolume() {
        return totalLeftVolume;
    }

    public void setTotalLeftVolume(Long totalLeftVolume) {
        this.totalLeftVolume = totalLeftVolume;
    }

    public String getInfoDetails() {
        return infoDetails;
    }

    public void setInfoDetails(String infoDetails) {
        this.infoDetails = infoDetails;
    }

    public String getInfoLevel() {
        return infoLevel;
    }

    public void setInfoLevel(String infoLevel) {
        this.infoLevel = infoLevel;
    }

    @Override
    public String toString() {
        return "PortfolioAllocationResponse{" +
                "list=" + list +
                ", price=" + price +
                ", totalEntrustVolume=" + totalEntrustVolume +
                ", totalEntrustAmt=" + totalEntrustAmt +
                ", totalLeftVolume=" + totalLeftVolume +
                ", infoDetails='" + infoDetails + '\'' +
                ", infoLevel='" + infoLevel + '\'' +
                '}';
    }
}



