package com.joyintech.tams.cfets.manualsettle.dto;

import com.joyintech.tams.business.common.dto.PageMode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 银行间手工交收
 * 银行间手工交收流水信息共通查询条件DTO
 * @author sjj
 * @version 1.0 2021年11月15日
 * @see CfetsManualSettleCommonRequest
 * @since 1.0
 */

@ApiModel(value="银行间手工交收流水信息共通请求参数",description="银行间手工交收流水信息共通请求参数")
public class CfetsManualSettleCommonRequest {

    @ApiModelProperty(name="selectedItem",value="选择的记录",example="List<CfetsManualSettleDto>")
    List<CfetsManualSettleDto> selectedItem;

    @ApiModelProperty(name="pageMode",value="分页对象")
    private PageMode pageMode;

    public List<CfetsManualSettleDto> getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(List<CfetsManualSettleDto> selectedItem) {
        this.selectedItem = selectedItem;
    }

    public PageMode getPageMode() {
        return pageMode;
    }

    public void setPageMode(PageMode pageMode) {
        this.pageMode = pageMode;
    }
}
