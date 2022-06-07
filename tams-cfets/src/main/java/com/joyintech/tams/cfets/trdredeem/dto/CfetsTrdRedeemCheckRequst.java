package com.joyintech.tams.cfets.trdredeem.dto;

import com.joyintech.background.component.common.pack.common.base.BaseEntity;
import com.joyintech.workflow.engine.dto.ProcessEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author zznn
 * @date 2021/11/25
 */
@ApiModel(value="赎回登记相关操作检查入参",description="赎回登记相关操作检查入参,如撤回")
public class CfetsTrdRedeemCheckRequst extends BaseEntity implements ProcessEntity {

    @ApiModelProperty(name="remark",value="撤销原因",required = true)
    private String remark;

    @ApiModelProperty(name="cfetsBondRegCheckDetaileRequsts",value="入参明细",required = true)
    private List<CfetsTrdRedeemRegInfoListResponse> cfetsBondRegCheckDetaileRequsts;

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public List<CfetsTrdRedeemRegInfoListResponse> getCfetsBondRegCheckDetaileRequsts() {
        return cfetsBondRegCheckDetaileRequsts;
    }

    public void setCfetsBondRegCheckDetaileRequsts(List<CfetsTrdRedeemRegInfoListResponse> cfetsBondRegCheckDetaileRequsts) {
        this.cfetsBondRegCheckDetaileRequsts = cfetsBondRegCheckDetaileRequsts;
    }
}

