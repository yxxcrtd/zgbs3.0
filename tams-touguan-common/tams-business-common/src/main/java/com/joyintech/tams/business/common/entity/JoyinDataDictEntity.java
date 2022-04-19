package com.joyintech.tams.business.common.entity;

import com.joyintech.framework.base.intf.IDictCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "数据字典设置实体类")
public class JoyinDataDictEntity implements IDictCode {


    private  String value;
    @Override
    public String getValue() {
        return value;
    }

    @Override
    public void setValue(String value) {
        this.value = value;
    }

}
