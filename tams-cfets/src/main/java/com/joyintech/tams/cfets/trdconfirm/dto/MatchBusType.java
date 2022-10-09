package com.joyintech.tams.cfets.trdconfirm.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author jiangpeng
 * @date 2022年09月27日 9:21
 */
@ApiModel(value = "匹配要素-业务类型", description = "匹配要素-业务类型")
public class MatchBusType {
    @ApiModelProperty(name = "code", value = "字段code", example = "dealdate")
    private String code;

    @ApiModelProperty(name = "name", value = "字段名", example = "成交日期")
    private String name;

    @ApiModelProperty(name = "type", value = "匹配类型", example = "01 自动匹配 02 手工匹配")
    private String type;

    @ApiModelProperty(name = "details", value = "明细配置，对应业务明细字段")
    private List<MatchDetFeatures> details;

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

    public List<MatchDetFeatures> getDetails() {
        return details;
    }

    public void setDetails(List<MatchDetFeatures> details) {
        this.details = details;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
