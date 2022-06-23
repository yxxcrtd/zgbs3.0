/*
 * 文件名：CommonUtils.java
 * 版权：Copyright 2016-2017 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：中信银行资管系统
 * 修改人：Administrator
 * 修改时间：2016年11月18日
 * 修改内容：新建
 * 系统名称：中信银行资管系统
 */

package com.joyintech.tams.report.common.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.activiti.engine.impl.util.CollectionUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.joyintech.framework.base.intf.DataDict;
import com.joyintech.framework.base.intf.DataDictItem;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.svreport.chb.util.SvrDataUtil;
import com.joyintech.tams.constant.DictCode;

/**
 * 共通工具类<br>
 * 数据字典工具类
 * @author 何阳阳
 * @version 1.0 2016年11月18日
 * @see SysDataUtil
 * @since 1.0
 */
public final class SysDataUtil {

    /**
     * 日志
     */
    private static Logger logger = LoggerFactory.getLogger(SysDataUtil.class);

//    /**
//     * SysDatadictListMapper
//     */
//    private static ChbSysDatadictListMapper sysDatadictListMapper;

//    /**
//     * 注入SysDatadictListMapper
//     *
//     * @param sysDatadictListMapper SysDatadictListMapper
//     */
//    @Autowired(required=false)
//    public static void setSysDatadictListMapper(ChbSysDatadictListMapper sysDatadictListMapper) {
//        SysDataUtil.sysDatadictListMapper = sysDatadictListMapper;
//    }

    /**
     * 从数据字典中获取参数值
     *
     * @param dictCode 数据字典
     * @param itemCode 数据代码
     * @return 参数值
     * @see
     * @since 1.0
     */
    public static String getNameFromDataDict(final DictCode dictCode, String dictItemCode) {
    		return SvrDataUtil.getNameFromDataDict(dictCode,dictItemCode);
    }

    /**
     * 从数据字典中获取参数值
     *
     * @param dictCode 数据字典
     * @param itemName 数据代码名称
     * @return 参数值
     * @see
     * @since 1.0
     */
    public static String getCodeFromDataDictByName(final DictCode dictCode, String dictItemName) {
    	return SvrDataUtil.getCodeFromDataDictByName(dictCode,dictItemName);
    }

    /**
     * 获取数据字典map
     *
     * @param dictCode 数据字典
     * @return 数据字典map
     * @see
     * @since 1.0
     */
    public static Map<String, String> getSysDictDataMap(final DictCode dictCode) {
        Map<String, String> resultMap = new HashMap<String, String>();

        if (StringUtil.isNotEmpty(dictCode)) {
            // 从缓存中获取所有数据字典
            Map<String, DataDict> dataDictMap = CacheUtil.getAllDictCode();;

            String code = dictCode.getValue();

            DataDict dto = dataDictMap.get(code);

            if (null != dto) {
                List<? extends DataDictItem> items = dto.getItems();
                for (DataDictItem item : items) {
                    resultMap.put(item.getItemCode(), item.getItemValue());
                }
            }
        }

        return resultMap;
    }

    /**
     * 获取中债格式化后的数据字典（数据字典代码+空格+数据字典名称） 如果传过来的是用“,”分割的字符串，则得到用“,”分割的格式化后的数据字典
     *
     * @param dictCode 数据字段
     * @param code 数据字典明细代码
     * @return 格式化后的数据
     * @see
     * @since 1.0
     */
    public static String getZZDictCodeAndName(DictCode dictCode, final String code) {
        if (StringUtil.isEmpty(code)) {
            return null;
        }
        List<String> codeList = StringUtil.splitToList(GlobalConstant.COMMA, code);
        List<String> nameList = new ArrayList<>();
        for (String string : codeList) {
            String name = SysDataUtil.getNameFromDataDict(dictCode, string);
            if (StringUtil.isNotEmpty(name)){
                nameList.add(
                        string + StringUtil.SPACE + name);
            }
        }
        return StringUtil.joinString(nameList, GlobalConstant.COMMA);
    }

    /**
     *
     * 获取中债格式化后的数据字典值，从（数据字典代码+空格+数据字典名称）格式中解析出数据字典CODE
     * @param dictCode 数据字典
     * @param formatStr 格式化后的字符串
     * @return 数据字典代码
     */
    public static String getZZDictCodeFromName(DictCode dictCode, final String formatStr) {
        if (StringUtil.isEmpty(formatStr)) {
            return null;
        }
        String[] codeList = StringUtil.split(formatStr, StringUtil.SPACE);
        return codeList[0];
    }
}
