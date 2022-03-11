/*
 * 文件名：AddTaxRuleDto.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：weipeng
 * 修改时间：2018年8月7日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.batch.vatdraw.dto;

import com.joyintech.tams.common.base.entity.TaxBokBalance;

import java.math.BigDecimal;

/**
 * 增值税余额Dto
 * @author weipeng
 * @version 1.0 2018年8月7日
 * @see TaxBokBalanceDto
 * @since 1.0
 */
public class TaxBokBalanceDto extends TaxBokBalance {

    /**
     * 前一日增值税余额---用与会计引擎升级使用，全资产计算区分投资收益-利息收入-增值税备抵和利息收入-增值税备抵
     */
    private BigDecimal ystTaxBalAmt;

    /**
     * 获取 前一日增值税余额
     * @return 前一日增值税余额
     */
    public BigDecimal getYstTaxBalAmt() {
        return ystTaxBalAmt;
    }

    /**
     * 设置 前一日增值税余额
     * @param ystTaxBalAmt 前一日增值税余额
     */
    public void setYstTaxBalAmt(BigDecimal ystTaxBalAmt) {
        this.ystTaxBalAmt = ystTaxBalAmt;
    }

}
