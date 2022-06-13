/*
 * 文件名：ValBatchLog001Dto.java
 * 版权：Copyright 2016-2017 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管业务监管报送平台
 * 修改人：倪怀
 * 修改时间：2018年2月23日
 * 修改内容：新建
 * 系统名称：理财资管业务监管报送平台
 */

package com.joyintech.tams.valuation.system.log.dto;

import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.constant.BatchDictCode;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.export.paginator.domain.ITableRow;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.SysDataUtil;

/**
 * 批处理日志扩展明细dto
 * 
 * @author 殷亚
 * @version 1.0 2018年2月23日
 * @see ValBatchLogDetailExDto
 * @since 1.0
 */
public class ValBatchLogDetailExDto extends BaseEntity implements ITableRow {


    private String remark;

    private String value;

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
