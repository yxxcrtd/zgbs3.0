package com.joyintech.tams.touguan.basicdata.cfetsbusinesspara.finProductSpecial.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 特殊债券下拉框信息
 *
 * @author zhuys
 * @date 2022-05-06
 */
@ApiModel(value = "特殊债券信息", description = "特殊债券信息")
public class FinProductSpecialSelectOptionDto {

    @ApiModelProperty(name = "id", value = "债券代码")
    private String id;

    @ApiModelProperty(name = "text", value = "债券简称")
    private String text;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}