package com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstfirm.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author sjj
 * @version 1.0 2021年11月03日
 * @see CfetsTrdBondFirstBidFirmEtrRequest
 * @since 1.0
 */
@ApiModel(value="指令接收放弃请求参数",description="指令接收放弃请求参数")
public class CfetsTrdBondFirstBidFirmEtrRequest {

    @ApiModelProperty(name="entrustId",value="指令代码",example="A1",required = true)
    private String entrustId;

    @ApiModelProperty(name="finprodType2",value="选择的资产类型",example="A1",required = true)
    private String finprodType2;

    @ApiModelProperty(name="busiTypePosition",value="业务类型",example="债券一级投标/现券买卖CODE",required = true)
    private String busiTypePosition;

    @ApiModelProperty(name="entrustStatus",value="指令状态",example="待分发/待接收",required = true)
    private String entrustStatus;

    @ApiModelProperty(name = "finprodId", value = "产品编码", example = "字符串1")
    private String finprodId;

    @ApiModelProperty(name = "finprodAbbr", value = "资产简称", example = "字符串1")
    private String finprodAbbr;

    public String getEntrustId() {
        return entrustId;
    }

    public void setEntrustId(String entrustId) {
        this.entrustId = entrustId;
    }

    public String getFinprodType2() {
        return finprodType2;
    }

    public void setFinprodType2(String finprodType2) {
        this.finprodType2 = finprodType2;
    }

    public String getBusiTypePosition() {
        return busiTypePosition;
    }

    public void setBusiTypePosition(String busiTypePosition) {
        this.busiTypePosition = busiTypePosition;
    }

    public String getEntrustStatus() {
        return entrustStatus;
    }

    public void setEntrustStatus(String entrustStatus) {
        this.entrustStatus = entrustStatus;
    }

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
}

