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

import java.util.Date;

/**
 * 债券标的信息DTO
 *
 * @author wangyang1
 * @version 1.0 2022年4月13日
 * @see FinBondAssetInsertRequest
 * @since 1.0
 */
@ApiModel(value="债券标的信息查询参数",description="债券标的信息查询参数")
public class FinBondAssetInsertRequest {

    @ApiModelProperty(name="finprodId",value="资产简称(方法:getInitPageParams(编辑必填)、getBasicInfo(必填))")
    private String finprodId;

    @ApiModelProperty(name="finprodType",value="资产类型(方法:getInitPageParams(编辑必填))")
    private String finprodType;

    @ApiModelProperty(name="workflowId",value="流程Id(方法:getInitPageParams(流程必填))")
    private String workflowId;

    @ApiModelProperty(name="finprodMarketId",value="市场代码(方法:getInterfBondDto(必填)、getIsNetSett(选填))")
    private String finprodMarketId;

    @ApiModelProperty(name="tradeMarketId",value="交易市场(方法:getInterfBondDto(必填))")
    private String tradeMarketId;

    @ApiModelProperty(name="tradeMarket",value="交易市场(方法:getIsNetSett(选填))")
    private String tradeMarket;

    @ApiModelProperty(name="type9",value="行内一级类型(方法:getIsNetSett(选填))")
    private String type9;

    @ApiModelProperty(name="isPpb",value="是否私募债(方法:getIsNetSett(选填))")
    private String isPpb;

    @ApiModelProperty(name="oFinprodId",value="原金融产品代码(方法:isSameMdateAndVdate(必填))")
    private String oFinprodId;

    @ApiModelProperty(name="issuer",value="发行人(方法:getFinBondRatingDto(必填))")
    private String issuer;

    @ApiModelProperty(name="vdate",value="起息日(方法:isSameMdateAndVdate(必填))")
    private Date vdate;

    @ApiModelProperty(name="mdate",value="到期日(方法:isSameMdateAndVdate(必填))")
    private Date mdate;

    public Date getVdate() {
        return vdate;
    }

    public void setVdate(Date vdate) {
        this.vdate = vdate;
    }

    public Date getMdate() {
        return mdate;
    }

    public void setMdate(Date mdate) {
        this.mdate = mdate;
    }

    public String getoFinprodId() {
        return oFinprodId;
    }

    public void setoFinprodId(String oFinprodId) {
        this.oFinprodId = oFinprodId;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getFinprodId() {
        return finprodId;
    }

    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
    }

    public String getFinprodType() {
        return finprodType;
    }

    public void setFinprodType(String finprodType) {
        this.finprodType = finprodType;
    }

    public String getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
    }

    public String getFinprodMarketId() {
        return finprodMarketId;
    }

    public void setFinprodMarketId(String finprodMarketId) {
        this.finprodMarketId = finprodMarketId;
    }

    public String getTradeMarketId() {
        return tradeMarketId;
    }

    public void setTradeMarketId(String tradeMarketId) {
        this.tradeMarketId = tradeMarketId;
    }

    public String getTradeMarket() {
        return tradeMarket;
    }

    public void setTradeMarket(String tradeMarket) {
        this.tradeMarket = tradeMarket;
    }

    public String getType9() {
        return type9;
    }

    public void setType9(String type9) {
        this.type9 = type9;
    }

    public String getIsPpb() {
        return isPpb;
    }

    public void setIsPpb(String isPpb) {
        this.isPpb = isPpb;
    }
}
