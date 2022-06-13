/*
 * 文件名：FinBondTargetAssetDto.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangnian
 * 修改时间：2018年9月20日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.business.asset.bond.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 债券标的信息DTO
 *
 * @author wangyang1
 * @version 1.0 2022年4月13日
 * @see FinAssetRateAdjRequest
 * @since 1.0
 */
@ApiModel(value="利率调整信息查询参数",description="利率调整信息查询参数")
public class FinAssetRateAdjRequest {

    @ApiModelProperty(name="search",value="关键字",example="A1")
    private String search;

    @ApiModelProperty(name="finprodId",value="资产代码",example="A1")
    private String finprodId;

    @ApiModelProperty(name="finprodAbbr",value="资产简称")
    private String finprodAbbr;

    @ApiModelProperty(name="finprodMarketId",value="市场代码")
    private String finprodMarketId;

    @ApiModelProperty(name="rateType",value="利率类型")
    private String rateType;

    @ApiModelProperty(name="finprodType2",value="资产类型")
    private String finprodType2;

    @ApiModelProperty(name="workflowId",value="流程id")
    private String workflowId;

    public String getFinprodId() {
        return finprodId;
    }

    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
    }

    public String getFinprodAbbr() {
        return finprodAbbr;
    }

    public void setFinprodAbbr(String finprodAbbr) {
        this.finprodAbbr = finprodAbbr;
    }

    public String getFinprodMarketId() {
        return finprodMarketId;
    }

    public void setFinprodMarketId(String finprodMarketId) {
        this.finprodMarketId = finprodMarketId;
    }

    public String getRateType() {
        return rateType;
    }

    public void setRateType(String rateType) {
        this.rateType = rateType;
    }

    public String getFinprodType2() {
        return finprodType2;
    }

    public void setFinprodType2(String finprodType2) {
        this.finprodType2 = finprodType2;
    }

    public String getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
    }
}
