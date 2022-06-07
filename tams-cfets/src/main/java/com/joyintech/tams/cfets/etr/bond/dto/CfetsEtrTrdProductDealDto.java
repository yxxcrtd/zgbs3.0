package com.joyintech.tams.cfets.etr.bond.dto;

import com.joyintech.framework.annotation.PageId;
import com.joyintech.tams.common.base.entity.TrdProductDeal;


/**
 * 银行间债券指令
 * @author chengzhong
 * @version 1.0 2021年1月19日
 * @see CfetsEtrTrdProductDealDto
 * @since 1.0
 */
@PageId("EtrIbBondinfo001")
public class CfetsEtrTrdProductDealDto extends TrdProductDeal {

    /**
     * 组合代码
     */
    private String portfolioId;


    public String getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId;
    }

}
