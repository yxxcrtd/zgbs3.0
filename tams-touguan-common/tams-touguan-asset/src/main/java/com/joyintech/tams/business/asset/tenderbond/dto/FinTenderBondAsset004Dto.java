package com.joyintech.tams.business.asset.tenderbond.dto;

import com.joyintech.framework.base.BaseEntity;
import com.joyintech.workflow.engine.dto.ProcessEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 新债标的删除dto
 */
@ApiModel(value="FinTenderBondAsset004Dto",description="新债标的删除dto")
public class FinTenderBondAsset004Dto extends BaseEntity implements ProcessEntity {
    /**
     * 金融产品代码
     */
    @ApiModelProperty(name="finprodId",value="金融产品代码")
    private String finprodId;

    /**
     * 备注
     */
    @ApiModelProperty(name="remark",value="备注")
    private String remark;


    /**
     * 操作类型：NEW,MODIFY,DELETE,REVOKE
     */
    @ApiModelProperty(name="operateType",value="操作类型：NEW,MODIFY,DELETE,REVOKE")
    private String operateType;

    /**
     * elementId
     */
    @ApiModelProperty(name="elementId",value="要素id")
    private String elementId;

    public String getFinprodId() {
        return finprodId;
    }

    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getOperateType() {
        return operateType;
    }

    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }

    public String getElementId() {
        return elementId;
    }

    public void setElementId(String elementId) {
        this.elementId = elementId;
    }
}
