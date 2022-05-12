package com.joyintech.tams.touguan.system.bond.special.dto;

import com.joyintech.framework.base.BaseEntity;
import com.joyintech.tams.component.business.riskrds.dto.PageModel;
import com.joyintech.workflow.engine.annotation.PrimaryKey;
import com.joyintech.workflow.engine.dto.ProcessEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


/**
 * 特殊债券信息
 *
 * @author zhuys
 * @date 2022-05-06
 */
@ApiModel(value = "特殊债券信息", description = "特殊债券信息")
public class FinProductSpecialDto extends BaseEntity implements ProcessEntity {

    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    private static final long serialVersionUID = 6888665039083565141L;

    @ApiModelProperty(name = "operateType", value = "操作类型", example = "submit/save", required = true)
    private String operateType;

    @ApiModelProperty(name = "operateStatus", value = "操作状态", required = true)
    private String operateStatus;

    @ApiModelProperty(name = "elementId", value = "按钮id", example = "FinBondAsset002_btn_submit/FinBondAsset002_btn_save")
    private String elementId;

    @ApiModelProperty(name = "pageId", value = "画面ID", example = "FinBondAsset002")
    private String pageId;

    @ApiModelProperty(name = "pageModel", value = "分页对象")
    private PageModel pageModel;

    /**
     * 债券代码
     */
    @ApiModelProperty(name = "finprodMarketId", value = "债券代码")
    private String finprodMarketId;

    /**
     * 债券简称
     */
    @ApiModelProperty(name = "finprodAbbr", value = "债券简称")
    private String finprodAbbr;

    /**
     * 是否资产支持证券
     */
    @ApiModelProperty(name = "isAbs", value = "是否资产支持证券")
    private String isAbs;

    /**
     * 是否特殊债
     */
    @ApiModelProperty(name = "isSpecialDebt", value = "是否特殊债")
    private String isSpecialDebt;

    /**
     * 是否到期违约债
     */
    @ApiModelProperty(name = "tenderType", value = "是否到期违约债")
    private String isDefaultBondIndicator;

    /**
     * 特殊债标识录入方式
     */
    @ApiModelProperty(name = "inputType", value = "特殊债标识录入方式")
    private String inputType;

    @PrimaryKey(PrimaryKey.Field.PK_1)
    public String getFinprodMarketId() {
        return finprodMarketId;
    }

    public void setFinprodMarketId(String finprodMarketId) {
        this.finprodMarketId = finprodMarketId;
    }

    public String getIsAbs() {
        return isAbs;
    }

    public void setIsAbs(String isAbs) {
        this.isAbs = isAbs;
    }

    public String getIsSpecialDebt() {
        return isSpecialDebt;
    }

    public void setIsSpecialDebt(String isSpecialDebt) {
        this.isSpecialDebt = isSpecialDebt;
    }

    public String getIsDefaultBondIndicator() {
        return isDefaultBondIndicator;
    }

    public void setIsDefaultBondIndicator(String isDefaultBondIndicator) {
        this.isDefaultBondIndicator = isDefaultBondIndicator;
    }

    public String getInputType() {
        return inputType;
    }

    public void setInputType(String inputType) {
        this.inputType = inputType;
    }

    public String getOperateType() {
        return operateType;
    }

    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }

    public String getOperateStatus() {
        return operateStatus;
    }

    public void setOperateStatus(String operateStatus) {
        this.operateStatus = operateStatus;
    }

    public String getElementId() {
        return elementId;
    }

    public void setElementId(String elementId) {
        this.elementId = elementId;
    }

    public String getPageId() {
        return pageId;
    }

    public void setPageId(String pageId) {
        this.pageId = pageId;
    }

    public PageModel getPageModel() {
        return pageModel;
    }

    public void setPageModel(PageModel pageModel) {
        this.pageModel = pageModel;
    }

    public String getFinprodAbbr() {
        return finprodAbbr;
    }

    public void setFinprodAbbr(String finprodAbbr) {
        this.finprodAbbr = finprodAbbr;
    }
}