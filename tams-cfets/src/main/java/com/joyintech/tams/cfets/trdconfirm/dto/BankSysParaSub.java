package com.joyintech.tams.cfets.trdconfirm.dto;

import com.joyintech.framework.base.BaseEntity;

/**
 * @author jiangpeng
 * @date 2022年09月27日 10:44
 */
public class BankSysParaSub extends BaseEntity {
    /**
     * 参数代码,对应主表
     */
    private String paraCode;
    /**
     * 参数代码
     */
    private String subParaCode;
    /**
     * 参数名称
     */
    private String subParaName;
    /**
     * 参数值1
     */
    private String paramValue1;
    /**
     * 参数值2,针对二维参数情况,如果为一维参数则,则仅参数值1有值
     */
    private String paramValue2;
    /**
     * 数据类型(01:文字列,02:下拉框,03:日期)
     */
    private String dataType;
    /**
     * '选择范围
     */
    private String dataScope;
    /**
     * '备注
     */
    private String remark;

    public String getParaCode() {
        return paraCode;
    }

    public void setParaCode(String paraCode) {
        this.paraCode = paraCode;
    }

    public String getSubParaCode() {
        return subParaCode;
    }

    public void setSubParaCode(String subParaCode) {
        this.subParaCode = subParaCode;
    }

    public String getSubParaName() {
        return subParaName;
    }

    public void setSubParaName(String subParaName) {
        this.subParaName = subParaName;
    }

    public String getParamValue1() {
        return paramValue1;
    }

    public void setParamValue1(String paramValue1) {
        this.paramValue1 = paramValue1;
    }

    public String getParamValue2() {
        return paramValue2;
    }

    public void setParamValue2(String paramValue2) {
        this.paramValue2 = paramValue2;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getDataScope() {
        return dataScope;
    }

    public void setDataScope(String dataScope) {
        this.dataScope = dataScope;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
