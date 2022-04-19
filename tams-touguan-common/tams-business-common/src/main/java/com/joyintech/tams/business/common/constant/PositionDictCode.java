/*
 * 文件名：DictCode.java 版权：Copyright 2016-2017 JoyinTech. Co. Ltd. All Rights Reserved. 描述：中信银行资管系统
 * 修改人：何阳阳 修改时间：2016年10月19日 修改内容：新建 系统名称：中信银行资管系统
 */

package com.joyintech.tams.business.common.constant;

import com.joyintech.framework.base.BaseDictCode;
import com.joyintech.framework.base.intf.DataDict;
import com.joyintech.framework.base.intf.DataDictItem;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.StringUtil;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 头寸数据字典枚举
 *
 */
public enum PositionDictCode implements BaseDictCode {
	/**
	 * 子系统代码
	 */
	子系统代码("SUBSYSTEM_CODE"),

	;
	/**
	 * 对应于数据字典code
	 */
	private String value;

	PositionDictCode(String value) {
		this.value = value;
	}

	/**
	 * @param value
	 *            数据字典值
	 * @return DictCode
	 * @see
	 * @since 1.0
	 */
	public static PositionDictCode get(String value) {
		PositionDictCode[] values = PositionDictCode.values();
		for (PositionDictCode code : values) {
			if (StringUtil.equals(value, code.getValue())) {
				return code;
			}
		}
		return null;
	}

	/**
	 * 获取 value
	 *
	 * @return value.
	 */
	public String getValue() {
		return value;
	}

	/**
	 * 设置 value
	 *
	 * @param value
	 *            value
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * 通过value生成枚举
	 *
	 * @param value
	 *            value值
	 * @return DictCode
	 */
	public static PositionDictCode fromValue(String value) {
		for (PositionDictCode dictCode : PositionDictCode.values()) {
			if (dictCode.getValue().equals(value)) {
				return dictCode;
			}
		}
		return null;
	}
}