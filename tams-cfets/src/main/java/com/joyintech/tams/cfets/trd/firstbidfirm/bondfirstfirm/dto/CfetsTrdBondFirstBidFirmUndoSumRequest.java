package com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstfirm.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "取消汇总传参", description = "取消汇总传参")
public class CfetsTrdBondFirstBidFirmUndoSumRequest {

    @ApiModelProperty(name = "entrustId", value = "指令代码,字符串", example = "WTBOND20220418")
    private String entrustId;

    @ApiModelProperty(name = "sumFlag", value = "汇总标识：01 取消汇总 02 交易对手不为空和交易对手为空汇总", example = "01")
    private String sumFlag;

    public String getEntrustId() {
        return entrustId;
    }

    public void setEntrustId(String entrustId) {
        this.entrustId = entrustId;
    }

    public String getSumFlag() {
        return sumFlag;
    }

    public void setSumFlag(String sumFlag) {
        this.sumFlag = sumFlag;
    }
}