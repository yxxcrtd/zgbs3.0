package com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstsub.dto;

import com.joyintech.background.component.common.pack.common.base.BaseEntity;
import com.joyintech.workflow.engine.dto.ProcessEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 当日申购相关操作检查入参
 *
 * @author jiangpeng
 * @date 2022/4/27 10:10
 */
@ApiModel(value = "当日申购相关操作检查入参", description = "当日申购相关操作检查入参,如撤销")
public class CfetsBondFirstSubCheckRequst extends BaseEntity implements ProcessEntity {

    @ApiModelProperty(name = "remark", value = "撤销原因", required = true)
    private String remark;

    @ApiModelProperty(name = "cfetsBondFirstSubCheckDetaileRequsts", value = "入参明细", required = true)
    private List<CfetsBondFirstSubCheckDetaileRequst> cfetsBondFirstSubCheckDetaileRequsts;

    @ApiModelProperty(name = "isSure", value = "是否确认取消匹配,点击确认后传值,没有确认则不需要,Y",example = "Y")
    private String isSure;

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public List<CfetsBondFirstSubCheckDetaileRequst> getCfetsBondFirstSubCheckDetaileRequsts() {
        return cfetsBondFirstSubCheckDetaileRequsts;
    }

    public void setCfetsBondFirstSubCheckDetaileRequsts(List<CfetsBondFirstSubCheckDetaileRequst> cfetsBondFirstSubCheckDetaileRequsts) {
        this.cfetsBondFirstSubCheckDetaileRequsts = cfetsBondFirstSubCheckDetaileRequsts;
    }

    public String getIsSure() {
        return isSure;
    }

    public void setIsSure(String isSure) {
        this.isSure = isSure;
    }
}

