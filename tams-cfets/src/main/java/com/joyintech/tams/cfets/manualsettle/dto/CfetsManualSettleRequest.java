package com.joyintech.tams.cfets.manualsettle.dto;

import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.export.paginator.domain.ITableRow;
import com.joyintech.workflow.engine.dto.ProcessEntity;
import io.swagger.annotations.ApiModel;

import java.util.List;

/**
 * 银行间手工交收
 * 银行间手工交收DTO
 * @author sjj
 * @version 1.0 2021年11月12日
 * @see CfetsManualSettleRequest
 * @since 1.0
 */

@ApiModel(value="银行间手工交收参数",description="银行间手工交收参数")
public class CfetsManualSettleRequest extends BaseEntity implements ITableRow, ProcessEntity {

    List<CfetsManualSettleDto> selectedItem;

    public List<CfetsManualSettleDto> getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(List<CfetsManualSettleDto> selectedItem) {
        this.selectedItem = selectedItem;
    }

}
