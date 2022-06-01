package com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstfirm.dto;

import com.joyintech.background.component.common.pack.common.base.BaseEntity;
import com.joyintech.workflow.engine.dto.ProcessEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 一级投标确认相关操作检查入参
 * workflowParameter 工作流参数  operType|users|roles|submit|pageId|elementId|isTodoList
 *
 * @author jiangpeng
 * @date 2022/4/27 10:10
 */
@ApiModel(value = "一级投标确认相关操作检查入参", description = "一级投标确认相关操作检查入参,如撤销")
public class CfetsTrdFirstBidFirmRevokeCheckRequst extends BaseEntity implements ProcessEntity {

    @ApiModelProperty(name = "remark", value = "撤销原因", required = true)
    private String remark;

    @ApiModelProperty(name = "details", value = "入参明细", required = true)
    private List<CfetsTrdFirstBidFirmCheckDetaileRequst> details;

    @ApiModelProperty(name = "type", value = "业务类型 : 01 投标 02 分销买入", example = "Y")
    private String type;

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public List<CfetsTrdFirstBidFirmCheckDetaileRequst> getDetails() {
        return details;
    }

    public void setDetails(List<CfetsTrdFirstBidFirmCheckDetaileRequst> details) {
        this.details = details;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

