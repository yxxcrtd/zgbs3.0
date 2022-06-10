/*
 * 文件名：ValStringUtil.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：wangzhiang
 * 修改时间：2018年6月8日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.invest.common.util;

import com.joyintech.framework.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 〈一句话功能简述〉 〈功能详细描述〉
 * 
 * @author wangzhiang
 * @version 1.0 2018年6月8日
 * @see ValStringUtil
 * @since 1.0
 */
public class ValStringUtil extends StringUtil {
    
    /**
     * 日志
     */
    private static Logger logger = LoggerFactory.getLogger(ValStringUtil.class);

    /**
     * 百分号
     */
    public static final String PERCENT_SIGN = "%";

    /**
     * 获取对象的非空字符串<br>
     * 如果该对象为空，则返回空字符串
     *
     * @param value 对象
     * @return 字符串
     */
    public static final String getNotNullStr(String value) {
        return getOrElse(value, EMPTY);
    }

    /**
     * SQL中的模糊查询的入参，根据模糊查询的类型，返回%拼接后的字符串。 类型：1代表左模糊查询（%字符串），2代表右模糊查询（字符串%），3代表整个模糊查询（%字符串%）
     *
     * @param str 字符串
     * @param type 模糊查询类型
     * @return 拼接后的字符串
     */
    public static String toLikeString(String str, int type) {
        if (isEmpty(str)) {
            return str;
        }
        if (type == 1 || type == 3) {
            str = PERCENT_SIGN + str;
        }
        if (type == 2 || type == 3) {
            str = str + PERCENT_SIGN;
        }
        return str;
    }
    
    /**
     * 判断thisValue是否与others任意一个相等<br/>
     * 如果thisValue为null，返回false
     *
     * @param thisValue 比较对象
     * @param others 被比较对象
     * @return thisValue是否与others任意一个相等时，返回true
     * @see
     * @since 1.0
     */
    public static boolean equals(String thisValue, String... others) {
        boolean ret = false;
        if (thisValue == null) {
            for (String other : others) {
                if (StringUtil.isEmpty(other)) {
                    ret = true;
                    break;
                }
            }
        } else {
            for (String other : others) {
                if (thisValue.equals(other)) {
                    ret = true;
                    break;
                }
            }
        }
        
        return ret;
    }
    
    /**
     * 将字符串根据指定字符串分隔为两个字符串
     *
     * @param old 源字符串
     * @param splitor 分隔符
     * @return 新的字符串数组
     */
    public static final String[] splitTwo(String old, String splitor) {
        String[] result = new String[2];
        if (isNotEmpty(old)) {
            int index = old.indexOf(splitor);
            if (index > -1) {
                result[0] = old.substring(0, index);
                result[1] = old.substring(index + splitor.length());
            }
            else {
                result[0] = old;
                result[1] = EMPTY;
            }
        }
        return result;
    }
    
    /**
     * 读取字符配置信息
     * 
     * @param config 配置字符
     * @return
     * @see
     * @since 1.0
     */
    public static Map<String,String> readProperty(String config) {
        Map<String,String> res = new HashMap<String,String>();
        Properties proper = new Properties();
        try {
            proper.load(new StringReader(config));
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } //把字符串转为reader
        Enumeration<?> enum1 = proper.propertyNames();
        while (enum1.hasMoreElements()) {
            String strKey = (String) enum1.nextElement();
            String strValue = proper.getProperty(strKey);
            res.put(strKey, strValue);
        }
        return res;
    }
}
