/*
 * 文件名：FinPayInterestStdInfoDto.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangnian
 * 修改时间：2018年9月11日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.business.asset.bond.dto;

import com.joyintech.tams.common.base.entity.FinCashPayRule;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 付息信息
 * @author wangyang1
 * @version 1.0 2022年4月16日
 * @see BondFinPayInterestStdInfoResponse
 * @since 1.0
 */
@ApiModel(value="付息计划生成返回数据",description="付息计划生成返回数据")
public class BondFinPayInterestStdInfoResponse {

    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    private static final long serialVersionUID = 1L;


    /**
     * 最大交易日
     */
    @ApiModelProperty(name="maxTradeDate",value="最大交易日")
    private Date  maxTradeDate;

    /**
     * 付息计划
     */
    @ApiModelProperty(name="invScheduleList",value="付息计划")
    private List<BondFinCashPlanDto>  invScheduleList;

    /**
     * 还本计划
     */
    @ApiModelProperty(name="invRepayList",value="还本计划")
    private List<BondFinCashPlanDto> invRepayList;

    public List<BondFinCashPlanDto> getInvScheduleList() {
        return invScheduleList;
    }

    public void setInvScheduleList(List<BondFinCashPlanDto> invScheduleList) {
        this.invScheduleList = invScheduleList;
    }

    public List<BondFinCashPlanDto> getInvRepayList() {
        return invRepayList;
    }

    public void setInvRepayList(List<BondFinCashPlanDto> invRepayList) {
        this.invRepayList = invRepayList;
    }

    public Date getMaxTradeDate() {
        return maxTradeDate;
    }

    public void setMaxTradeDate(Date maxTradeDate) {
        this.maxTradeDate = maxTradeDate;
    }
}
