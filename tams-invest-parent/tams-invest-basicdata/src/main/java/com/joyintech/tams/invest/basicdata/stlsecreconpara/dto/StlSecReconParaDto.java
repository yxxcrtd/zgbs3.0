package com.joyintech.tams.invest.basicdata.stlsecreconpara.dto;

import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.export.paginator.domain.ITableRow;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.framework.validation.annotation.NotEmpty;
import com.joyintech.framework.validation.annotation.ValidLogic;
import com.joyintech.framework.validation.groups.ValidateGroupDelete;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.common.base.entity.StlSecReconPara;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeListStl;
import com.joyintech.tams.invest.basicdata.stlsecreconpara.validator.StlSecReconParaValidator;
import com.joyintech.workflow.engine.annotation.PrimaryKey;
import com.joyintech.workflow.engine.dto.ProcessEntity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 对账参数设置DTO
 */
@PageId("StlSecReconPara002")
@ValidLogic(targetClass = StlSecReconParaValidator.class, groups = { ValidateGroupEdit.class,
        ValidateGroupDelete.class })
public class StlSecReconParaDto extends StlSecReconPara implements ITableRow, ProcessEntity {

    /**
     * 画面选中的项
     */
    private transient List<StlSecReconParaDto> selectedItem;

    /**
     * 操作类型
     */
    private String operateType;

    /**
     * 元素ID
     */
    private String elementId;


    /**
     * 参数代码
     */
    private String paraCodeStr;

    /**
     * 参数值
     */
    private String paraValueStr;

    public List<StlSecReconParaDto> getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(List<StlSecReconParaDto> selectedItem) {
        this.selectedItem = selectedItem;
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

    public String getParaCodeStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.参数代码, getParaCode());
    }

    public void setParaCodeStr(String paraCodeStr) {
        this.paraCodeStr = paraCodeStr;
    }

    public String getParaValueStr() {
        // 返回数据字典值
        if (StringUtil.equals(getParaCode(), DictCodeListStl.参数代码_对账维度.getValue())) {
            return SysDataUtil.getNameFromDataDict(DictCode.对账维度, getParaValue());
        } else if (StringUtil.equals(getParaCode(), DictCodeListStl.参数代码_佣金对账方式.getValue())) {
            return SysDataUtil.getNameFromDataDict(DictCode.佣金对账方式, getParaValue());
        }
        // 误差值
        return getParaValue();
    }

    @Override
    @NotEmpty(fieldName = "StlSecReconPara002.paraCode", groups = {ValidateGroupEdit.class})
    public String getParaCode() {
        return super.getParaCode();
    }

    @Override
    @NotEmpty(fieldName = "StlSecReconPara002.paraValue", groups = {ValidateGroupEdit.class})
    public String getParaValue() {
        return super.getParaValue();
    }

    public void setParaValueStr(String paraValueStr) {
        this.paraValueStr = paraValueStr;
    }

    @Override
    @PrimaryKey(value= PrimaryKey.Field.PK_1)
    public String getSeqNo() {
        return super.getSeqNo();
    }

    /**
     * 误差值
     * @return
     */
    public BigDecimal getDeviation() {
        if (StringUtil.equals(getParaCode(), DictCodeListStl.参数代码_误差值.getValue())) {
            if (StringUtil.isNotEmpty(getParaValue())) {
                return new BigDecimal(getParaValue());
            }
        }
        return null;
    }
}
