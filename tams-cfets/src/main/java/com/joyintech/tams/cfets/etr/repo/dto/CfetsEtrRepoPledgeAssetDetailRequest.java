package com.joyintech.tams.cfets.etr.repo.dto;

import com.joyintech.tams.business.common.dto.PageMode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 *
 * 指令下拉框模糊查询
 */
@ApiModel(value="质押券库查询入参",description="质押券库查询入参")
public class CfetsEtrRepoPledgeAssetDetailRequest {

    @ApiModelProperty(name="pageMode",value="分页对象",example="{pageSize: 10,pageNo: 1, sort: 'name', direction: 'asc'}")
    private PageMode pageMode;


    @ApiModelProperty(name="pledgeStockId",value="质押券库id",example="11")
    private String pledgeStockId;


    public PageMode getPageMode() {
        return pageMode;
    }

    public void setPageMode(PageMode pageMode) {
        this.pageMode = pageMode;
    }

	public String getPledgeStockId() {
		return pledgeStockId;
	}

	public void setPledgeStockId(String pledgeStockId) {
		this.pledgeStockId = pledgeStockId;
	}

}
