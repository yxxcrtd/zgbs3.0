package com.joyintech.tams.invest.risk.quota.rskspecificassettype.dto;

import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.export.paginator.domain.ITableRow;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.framework.validation.annotation.Length;
import com.joyintech.framework.validation.annotation.ValidLogic;
import com.joyintech.framework.validation.groups.ValidateGroupDelete;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.common.base.entity.RskSpecificAssetType;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.invest.risk.quota.rskspecificassettype.validate.RskSpecificAssetTypeValidator;
import com.joyintech.workflow.engine.annotation.PrimaryKey;
import com.joyintech.workflow.engine.dto.ProcessEntity;

import java.util.List;

/**
 * 特定资产类型DTO
 * @author zhangnian
 *
 */
@PageId("RskSpecificAssetType002")
@ValidLogic(targetClass = RskSpecificAssetTypeValidator.class , groups = {ValidateGroupEdit.class, ValidateGroupDelete.class})
public class RskSpecificAssetTypeDto extends RskSpecificAssetType implements ITableRow, ProcessEntity {
	
	private static final long serialVersionUID = 1504491602917193262L;

    /**
     * 操作类型
     */
    private String operateType;

    private String pageModel;

    /**
     * 选择数据
     */
    private List<RskSpecificAssetTypeDto> selectedItem;

    /**
     * 要素id
     */
    private String elementId;

    /**
     * 金融产品全称
     */
    private String finprodName;

    private String isFlowLimited;

    private String isFlowLimitedStr;

    @Override
    public String getIsFlowLimited() {
        return isFlowLimited;
    }

    @Override
    public void setIsFlowLimited(String isFlowLimited) {
        this.isFlowLimited = isFlowLimited;
    }


    public String getIsFlowLimitedStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.是否, this.getIsFlowLimited());
    }
    /**
     * 市场代码
     */
    private String finprodMarketId;

    @Override
    @PrimaryKey(PrimaryKey.Field.PK_1)
    public String getFinprodId() {
        return super.getFinprodId();
    }

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

    public List<RskSpecificAssetTypeDto> getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(List<RskSpecificAssetTypeDto> selectedItem) {
        this.selectedItem = selectedItem;
    }

    public String getElementId() {
        return elementId;
    }

    public void setElementId(String elementId) {
        this.elementId = elementId;
    }

    public String getFinprodName() {
        return finprodName;
    }

    public void setFinprodName(String finprodName) {
        this.finprodName = finprodName;
    }

    public String getFinprodMarketId() {
        return finprodMarketId;
    }

    public void setFinprodMarketId(String finprodMarketId) {
        this.finprodMarketId = finprodMarketId;
    }

    public String getType1Name(){
        return SysDataUtil.getNameFromDataDict(DictCode.授权类型一级, this.getType1());
    }

    public String getType2Name(){
        return SysDataUtil.getNameFromDataDict(DictCode.授权类型二级, this.getType2());
    }

    // 上农商无三级、四级类型，edit by daizhiwei
    public String getType3Name(){
//        return SysDataUtil.getNameFromDataDict(DictCode.授权类型三级, this.getType3());
        return getType3();
    }

    public String getType4Name(){
//        return SysDataUtil.getNameFromDataDict(DictCode.授权类型四级, this.getType4());
        return getType4();
    }

    public String getIsBadassetName(){
        return SysDataUtil.getNameFromDataDict(DictCode.是否, this.getIsBadasset());
    }

    public String getIsRegulationName(){
        return SysDataUtil.getNameFromDataDict(DictCode.是否, this.getIsRegulation());
    }

    public String getIsUnlistedequityName(){
        return SysDataUtil.getNameFromDataDict(DictCode.是否, this.getIsUnlistedequity());
    }

    public String getIsCreditAssetName(){
        return SysDataUtil.getNameFromDataDict(DictCode.是否, this.getIsCreditAsset());
    }

    public String getIsLiquidity1Name(){
        return SysDataUtil.getNameFromDataDict(DictCode.是否, this.getIsLiquidity1());
    }

    public String getIsLiquidity2Name(){
        return SysDataUtil.getNameFromDataDict(DictCode.是否, this.getIsLiquidity2());
    }

    public String getIsLiquidity3Name(){
        return SysDataUtil.getNameFromDataDict(DictCode.是否, this.getIsLiquidity3());
    }
}
