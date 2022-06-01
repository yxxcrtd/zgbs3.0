package com.joyintech.tams.business.asset.protect.dto;

import com.joyintech.tams.business.asset.bond.dto.FinBondAssetInsertRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 信用保护凭证标的
 *
 * @author wangshegnzhi
 * @version 1.0 2022年5月17日
 * @see CreditProtectionCertificateInsertRequest
 * @since 1.0
 */
@ApiModel(value="信用保护凭证标的查询参数",description="信用保护凭证标的查询参数")
public class CreditProtectionCertificateInsertRequest {

    @ApiModelProperty(name="finprodId",value="资产简称(方法:getInitPageParams(编辑必填)、getBasicInfo(必填))")
    private String finprodId;

    @ApiModelProperty(name="finprodType",value="资产类型(方法:getInitPageParams(编辑必填))")
    private String finprodType;

    @ApiModelProperty(name="workflowId",value="流程Id(方法:getInitPageParams(流程必填))")
    private String workflowId;

    @ApiModelProperty(name="finprodMarketId",value="市场代码(方法:getInterfBondDto(必填)、getIsNetSett(选填))")
    private String finprodMarketId;


    public String getFinprodId() {
        return finprodId;
    }

    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
    }

    public String getFinprodType() {
        return finprodType;
    }

    public void setFinprodType(String finprodType) {
        this.finprodType = finprodType;
    }

    public String getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
    }

    public String getFinprodMarketId() {
        return finprodMarketId;
    }

    public void setFinprodMarketId(String finprodMarketId) {
        this.finprodMarketId = finprodMarketId;
    }
}
