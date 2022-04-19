package com.joyintech.tams.business.common.constant;

import com.joyintech.framework.base.BaseDictCodeList;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.StringUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据字典明细枚举
 *
 * @author chenzhuo
 * @version 1.0 2016年11月12日
 * @see PositionDictCodeListExt
 * @since 1.0
 */
public enum PositionDictCodeListExt implements BaseDictCodeList {
	子系统代码_委托(PositionDictCode.子系统代码,"ETR","委托"),
	子系统代码_交易(PositionDictCode.子系统代码,"TRD","交易"),
	;

    /**
     * 对应于数据字典code
     */
    private String value;

    /**
     * 数据字典枚举
     */
    private PositionDictCode dictCode;

    /**
     * 明细名称
     */
    private String codeName;

    PositionDictCodeListExt(PositionDictCode dictCode, String value, String codeName) {
        this.dictCode = dictCode;
        this.value = value;
        this.setCodeName(codeName);
    }

    /**
     * 获取 DictCode
     *
     * @return value.
     */
    @Override
    public PositionDictCode getDictCode() {
        return dictCode;
    }

    /**
     * 获取 value
     *
     * @return value.
     */
    @Override
    public String getValue() {
        return value;
    }

    /**
     * 设置 value
     *
     * @param value value
     */
    @Override
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * 获取 codeName
     *
     * @return codeName.
     */
    @Override
    public String getCodeName() {
        return codeName;
    }

	@Override
	public void setCodeName(String codeName) {
		this.codeName = codeName;
	}
}
