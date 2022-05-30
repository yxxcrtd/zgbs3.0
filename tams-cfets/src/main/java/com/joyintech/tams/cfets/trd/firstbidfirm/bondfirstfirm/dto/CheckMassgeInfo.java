package com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstfirm.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author jiangpeng
 * @date 2022年05月25日 10:35
 */
@ApiModel(value = "一级投标确认校验提示信息", description = "一级投标确认校验提示信息")
public class CheckMassgeInfo {


    @ApiModelProperty(name = "primaryKey", value = "主键", example = "1")
    private String primaryKey;

    @ApiModelProperty(name = "feild", value = "字段", example = "bidPrice")
    private String feild;

    @ApiModelProperty(name = "message", value = "提示信息", example = "1 基本信息的汇总投标券面总额联动 2 汇总投标列表的投标标位联动")
    private String message;

    public String getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(String primaryKey) {
        this.primaryKey = primaryKey;
    }

    public String getFeild() {
        return feild;
    }

    public void setFeild(String feild) {
        this.feild = feild;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
