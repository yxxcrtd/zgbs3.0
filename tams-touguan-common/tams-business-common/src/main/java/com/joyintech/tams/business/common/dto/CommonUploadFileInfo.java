package com.joyintech.tams.business.common.dto;

import com.joyintech.tams.common.base.entity.SysUploadFileInfo;

import java.math.BigDecimal;

/**
 * 文件信息
 */
public class CommonUploadFileInfo extends SysUploadFileInfo {
    private BigDecimal size;

    public BigDecimal getSize() {
        return this.size;
    }

    public void setSize(BigDecimal size) {
        this.size = size;
    }
}
