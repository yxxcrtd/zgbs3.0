/*
 * 文件名：BooksetTaxConfigDto.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：niuqianyu
 * 修改时间：2018年11月15日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.service.valuation.datatransplant.dto;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

import com.github.pagehelper.util.StringUtil;
import com.joyintech.framework.base.intf.DataDictItem;
import com.joyintech.framework.util.SysDataUtil;

/**
 * 账套税务配置
 * 
 * @author niuqianyu
 * @version 1.0 2018年11月15日
 * @see BooksetTaxConfigDto
 * @since 1.0
 */
public class BooksetTaxConfigDto {

    /**
     * 配置项代码
     */
    private String configId;

    /**
     * 取数据字典值
     */
    private String dictCode;

    /**
     * 配置项值
     */
    private String configValue;

    /**
     * 获取 配置项代码
     * 
     * @return 配置项代码
     */
    public String getConfigId() {
        return configId;
    }

    /**
     * 设置 配置项代码
     * 
     * @param configId 配置项代码
     */
    public void setConfigId(String configId) {
        this.configId = configId;
    }

    /**
     * 获取 取值数据字典
     * 
     * @return 取值数据字典
     */
    public String getDictCode() {
        return dictCode;
    }

    /**
     * 设置 取值数据字典
     * 
     * @param dictCode 取值数据字典
     */
    public void setDictCode(String dictCode) {
        this.dictCode = dictCode;
    }

    /**
     * 获取 配置项值
     * 
     * @return 配置项值
     */
    public String getConfigValue() {
        return configValue;
    }

    /**
     * 设置 配置项值
     * 
     * @param configValue 配置项值
     */
//    public void setConfigValue(String configValue) {
//        if (StringUtil.isEmpty(configValue)) {
//            List<? extends DataDictItem> items = SysDataUtil.getDataDictItem(this.dictCode);
//            if (CollectionUtils.isNotEmpty(items)) {
//                this.configValue = items.get(0).getDefaultValue();
//            }
//        }
//        this.configValue = configValue;
//    }

}
