/*
 * 文件名：EtrSecPositionDto.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：Administrator
 * 修改时间：2019年10月14日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.invest.common.trade.dto;

import com.joyintech.tams.common.base.entity.EtrSecPosition;

public class EtrSecPositionDto extends EtrSecPosition{

    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    private static final long serialVersionUID = -3662738469690669899L;

    /**
     * 用于买断式回购取持仓 债券借贷不包含买入券使用
     */
    private String isContain9;

    public String getIsContain9() {
        return isContain9;
    }

    public void setIsContain9(String isContain9) {
        this.isContain9 = isContain9;
    }
}
