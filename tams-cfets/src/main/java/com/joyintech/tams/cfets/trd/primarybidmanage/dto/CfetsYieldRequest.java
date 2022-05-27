package com.joyintech.tams.cfets.trd.primarybidmanage.dto;

import com.joyintech.tams.business.common.dto.PageMode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.List;

/**
 * 获取到期日接口参数
 *
 * @author wangyang1
 * @version 1.0 2022年5月5日
 * @see CfetsYieldRequest
 * @since 1.0
 */
@ApiModel(value="获取到期日接口参数",description="获取到期日接口参数")
public class CfetsYieldRequest {

    @ApiModelProperty(name="finprodId",value="资产代码")
    private String finprodId;

    @ApiModelProperty(name="winPrice",value="中标标位")
    private BigDecimal winPrice;

    @ApiModelProperty(name="tenderTarget",value="招标标的")
    private String tenderTarget;

    public String getFinprodId() {
        return finprodId;
    }

    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
    }

    public BigDecimal getWinPrice() {
        return winPrice;
    }

    public void setWinPrice(BigDecimal winPrice) {
        this.winPrice = winPrice;
    }

    public String getTenderTarget() {
        return tenderTarget;
    }

    public void setTenderTarget(String tenderTarget) {
        this.tenderTarget = tenderTarget;
    }
}
