package com.joyintech.tams.cfets.trdconfirm.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author jiangpeng
 * @date 2022年09月27日 9:21
 */
@ApiModel(value = "匹配要素-详细字段", description = "匹配要素-详细字段")
public class MatchDetFeatures {
    @ApiModelProperty(name = "code", value = "字段code", example = "dealdate")
    private String code;

    @ApiModelProperty(name = "name", value = "字段名", example = "成交日期")
    private String name;

    @ApiModelProperty(name = "type", value = "选择类型，01为基础要素,02为可选要素", example = "01")
    private String type;

    @ApiModelProperty(name = "isDisabled", value = "是否置灰，01 置灰 02 不置灰", example = "02")
    private String isDisabled;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIsDisabled() {
        return isDisabled;
    }

    public void setIsDisabled(String isDisabled) {
        this.isDisabled = isDisabled;
    }
}
