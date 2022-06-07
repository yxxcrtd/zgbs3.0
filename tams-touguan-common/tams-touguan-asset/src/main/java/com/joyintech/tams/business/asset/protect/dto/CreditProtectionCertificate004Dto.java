package com.joyintech.tams.business.asset.protect.dto;

import com.joyintech.framework.base.BaseEntity;
import com.joyintech.workflow.engine.dto.ProcessEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 删除dto
 */
@ApiModel(value="FinTenderBondAsset004Dto",description="删除dto")
public class CreditProtectionCertificate004Dto  extends BaseEntity implements ProcessEntity {
    /**
     * 金融产品代码
     */
    @ApiModelProperty(name="finprodId",value="金融产品代码")
    private String finprodId;

    /**
     * 金融产品代码
     */
    @ApiModelProperty(name="finprodMarketId",value="金融产品代码")
    private String finprodMarketId;

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

    private List<CreditProtectionCertificate002Dto> selectedItem;

    public List<CreditProtectionCertificate002Dto> getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(List<CreditProtectionCertificate002Dto> selectedItem) {
        this.selectedItem = selectedItem;
    }

    public String getFinprodId() {
        return finprodId;
    }

    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
    }

    public String getFinprodMarketId() {
        return finprodMarketId;
    }

    public void setFinprodMarketId(String finprodMarketId) {
        this.finprodMarketId = finprodMarketId;
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
