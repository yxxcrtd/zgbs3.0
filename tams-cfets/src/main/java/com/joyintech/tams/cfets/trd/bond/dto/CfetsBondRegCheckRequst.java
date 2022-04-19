package com.joyintech.tams.cfets.trd.bond.dto;

import com.joyintech.background.component.common.pack.common.base.BaseEntity;
import com.joyintech.workflow.engine.dto.ProcessEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author zznn
 * @date 2021/11/25
 */
@ApiModel(value="登记相关操作检查入参",description="登记报价相关操作检查入参,如撤回")
public class CfetsBondRegCheckRequst extends BaseEntity implements ProcessEntity {

    @ApiModelProperty(name="remark",value="撤销原因",required = true)
    private String remark;

    @ApiModelProperty(name="cfetsBondRegCheckDetaileRequsts",value="入参明细",required = true)
    private List<CfetsBondRegCheckDetaileRequst> cfetsBondRegCheckDetaileRequsts;

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public List<CfetsBondRegCheckDetaileRequst> getCfetsBondRegCheckDetaileRequsts() {
        return cfetsBondRegCheckDetaileRequsts;
    }

    public void setCfetsBondRegCheckDetaileRequsts(List<CfetsBondRegCheckDetaileRequst> cfetsBondRegCheckDetaileRequsts) {
        this.cfetsBondRegCheckDetaileRequsts = cfetsBondRegCheckDetaileRequsts;
    }
}

