package com.joyintech.tams.invest.asset.bond.dto;

import com.joyintech.framework.export.paginator.domain.ITableRow;
import com.joyintech.tams.common.base.entity.FinProductIssue;
import com.joyintech.tams.invest.asset.channel.dto.ChannelAssetDto;
import com.joyintech.workflow.engine.dto.ProcessEntity;

import java.util.List;

/**
 * 标的类金融产品发行信息dto
 * 〈功能详细描述〉
 *
 * @author zhuys
 * @version 1.0 2022年04月10日
 * @see FinProductIssueAssetDto
 * @since 1.0
 */
public class FinProductIssueAssetDto extends FinProductIssue implements ITableRow, ProcessEntity {

    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    private static final long serialVersionUID = 3308020867788413218L;

    /**
     * 操作类型
     */
    private String operateType;

    /**
     * 操作类型
     */
    private String pageModel;

    /**
     * 选择数据
     */
    private List<ChannelAssetDto> selectedItem;


    public String getOperateType() {
        return operateType;
    }

    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }

    public String getPageModel() {
        return pageModel;
    }

    public void setPageModel(String pageModel) {
        this.pageModel = pageModel;
    }

    public List<ChannelAssetDto> getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(List<ChannelAssetDto> selectedItem) {
        this.selectedItem = selectedItem;
    }
}
