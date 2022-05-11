package com.joyintech.tams.touguan.system.bond.special.dto;

import com.joyintech.framework.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 特殊债券信息流程表
 *
 * @author zhuys
 * @date 2022-05-06
 */
@ApiModel(value = "特殊债券信息流程表", description = "特殊债券信息流程表")
public class WflFinProductSpecialDto extends BaseEntity {

    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    private static final long serialVersionUID = 6888665039083565141L;

    /**
     * 流程状态 ※新建流程中，有效，撤销
     */
    private String workflowStatus;

    /**
     * 债券代码
     */
    @ApiModelProperty(name = "finprodMarketId", value = "债券代码")
    private String finprodMarketId;

    /**
     * 是否资产支持证券
     */
    @ApiModelProperty(name = "是否资产支持证券", value = "是否资产支持证券")
    private String isAbs;

    /**
     * 是否特殊债
     */
    @ApiModelProperty(name = "isSpecialDebt", value = "是否特殊债")
    private String isSpecialDebt;

    /**
     * 是否到期违约债
     */
    @ApiModelProperty(name = "tenderType", value = "是否到期违约债")
    private String isDefaultBondIndicator;

    /**
     * 特殊债标识录入方式
     */
    @ApiModelProperty(name = "inputType", value = "特殊债标识录入方式")
    private String inputType;


    public String getWorkflowStatus() {
        return workflowStatus;
    }

    public void setWorkflowStatus(String workflowStatus) {
        this.workflowStatus = workflowStatus;
    }

    public String getFinprodMarketId() {
        return finprodMarketId;
    }

    public void setFinprodMarketId(String finprodMarketId) {
        this.finprodMarketId = finprodMarketId;
    }

    public String getIsAbs() {
        return isAbs;
    }

    public void setIsAbs(String isAbs) {
        this.isAbs = isAbs;
    }

    public String getIsSpecialDebt() {
        return isSpecialDebt;
    }

    public void setIsSpecialDebt(String isSpecialDebt) {
        this.isSpecialDebt = isSpecialDebt;
    }

    public String getIsDefaultBondIndicator() {
        return isDefaultBondIndicator;
    }

    public void setIsDefaultBondIndicator(String isDefaultBondIndicator) {
        this.isDefaultBondIndicator = isDefaultBondIndicator;
    }

    public String getInputType() {
        return inputType;
    }

    public void setInputType(String inputType) {
        this.inputType = inputType;
    }
}