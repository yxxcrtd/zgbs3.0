package com.joyintech.tams.touguan.basicdata.cfetsbusinesspara.finProductSpecial.dto;

import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt3;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Objects;

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

    @ApiModelProperty(name = "type", value = "资产二级分类")
    private String type;

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

    public String getIsAbs() {
        if (Objects.nonNull(this.getType()) && this.getType().equals(DictCodeListExt3.行内资产三级分类_资产支持证券.getValue())) {
            return DictCodeList.是否_是.getValue();
        }
        return DictCodeList.是否_否.getValue();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}