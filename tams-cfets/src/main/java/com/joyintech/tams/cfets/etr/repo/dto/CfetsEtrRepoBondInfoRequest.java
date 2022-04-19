package com.joyintech.tams.cfets.etr.repo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 添加质押券校验查询
 */
@ApiModel(value = "添加质押券校验查询入参", description = "添加质押券校验查询入参")
public class CfetsEtrRepoBondInfoRequest {
    @ApiModelProperty(name = "cfetsEtrRepoZLCXPositionResponses", value = "质押券管理列表")
    private List<CfetsEtrRepoZLCXPositionResponse> cfetsEtrRepoZLCXPositionResponses;

    @ApiModelProperty(name = "settleSpreed", value = "清算速度", example = "01")
    private String settleSpreed;

    @ApiModelProperty(name = "ps", value = "交易方向", example = "03")
    private String ps;

    @ApiModelProperty(name = "repoTerm", value = "回购期限", example = "7")
    private BigDecimal repoTerm;

    @ApiModelProperty(name = "entrustDate", value = "指令日期", example = "2021-12-21")
    private Date entrustDate;

    public List<CfetsEtrRepoZLCXPositionResponse> getCfetsEtrRepoZLCXPositionResponses() {
        return cfetsEtrRepoZLCXPositionResponses;
    }

    public void setCfetsEtrRepoZLCXPositionResponses(List<CfetsEtrRepoZLCXPositionResponse> cfetsEtrRepoZLCXPositionResponses) {
        this.cfetsEtrRepoZLCXPositionResponses = cfetsEtrRepoZLCXPositionResponses;
    }

    public String getSettleSpreed() {
        return settleSpreed;
    }

    public void setSettleSpreed(String settleSpreed) {
        this.settleSpreed = settleSpreed;
    }

    public String getPs() {
        return ps;
    }

    public void setPs(String ps) {
        this.ps = ps;
    }

    public BigDecimal getRepoTerm() {
        return repoTerm;
    }

    public void setRepoTerm(BigDecimal repoTerm) {
        this.repoTerm = repoTerm;
    }

    public Date getEntrustDate() {
        return entrustDate;
    }

    public void setEntrustDate(Date entrustDate) {
        this.entrustDate = entrustDate;
    }
}
