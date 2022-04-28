/*
 * 文件名：ValBondMarketInfoDto.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：weipeng
 * 修改时间：2018年5月31日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.common.batch.finproduct.entity;

import com.joyintech.tams.common.base.entity.TrdProductDeal;

/**
 * 债券行情（第三方）Dto （债券市场行情表）
 * 
 * @author weipeng
 * @version 1.0 2018年5月31日
 * @see InitTrdProductDealDto
 * @since 1.0
 */
public class InitTrdProductDealDto extends TrdProductDeal {

    /**
     * 账套代码
     */
    private String booksetId;

    public String getBooksetId() {
        return booksetId;
    }

    public void setBooksetId(String booksetId) {
        this.booksetId = booksetId;
    }
}
