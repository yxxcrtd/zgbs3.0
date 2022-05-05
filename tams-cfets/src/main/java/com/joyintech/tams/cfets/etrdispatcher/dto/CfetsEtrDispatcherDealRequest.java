package com.joyintech.tams.cfets.etrdispatcher.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 场外指令分发
 * 指令分发请求参数
 * @author sjj
 * @version 1.0 2021年11月03日
 * @see CfetsEtrDispatcherDealRequest
 * @since 1.0
 */
@ApiModel(value="分发请求参数",description="分发请求参数")
public class CfetsEtrDispatcherDealRequest {

    @ApiModelProperty(name="entrustId",value="指令代码",example="A1",required = true)
    private String entrustId;

    @ApiModelProperty(name="traders",value="选择的交易员",example="A1",required = true)
    private String traders;

    @ApiModelProperty(name="finprodType2",value="选择的资产类型",example="A1",required = true)
    private String finprodType2;

    @ApiModelProperty(name="busiTypePosition",value="业务类型",example="债券一级投标/现券买卖CODE",required = true)
    private String busiTypePosition;

    @ApiModelProperty(name="entrustStatus",value="指令状态",example="待分发/待接收",required = true)
    private String entrustStatus;

    @ApiModelProperty(name="rekReason",value="退回原因",example="A1",required = true)
    private String rekReason;

    @ApiModelProperty(name = "finprodId", value = "产品编码", example = "字符串1")
    private String finprodId;

    public String getEntrustId() {
        return entrustId;
    }

    public void setEntrustId(String entrustId) {
        this.entrustId = entrustId;
    }

    public String getTraders() {
        return traders;
    }

    public void setTraders(String traders) {
        this.traders = traders;
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

    public String getRekReason() {
        return rekReason;
    }

    public void setRekReason(String rekReason) {
        this.rekReason = rekReason;
    }


    public String getFinprodId() {
        return finprodId;
    }

    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
    }
}

