/*
 * 文件名：DataArrangeUtil.java
 * 版权：Copyright 2016-2017 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：中信银行资管系统
 * 修改人：杨智
 * 修改时间：2017年6月16日
 * 修改内容：新建
 * 系统名称：中信银行资管系统
 */

package com.joyintech.tams.disclosure.interf.interf.common.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

//import org.apache.commons.dbcp.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.pool.DruidDataSource;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.disclosure.interf.interf.common.dto.InterfEntity;


/**
 * 接口增量数据整理模块
 * 
 * @author 杨智
 * @version 1.0 2017年6月16日
 * @see DataArrangeUtil
 * @since 1.0
 */
public class DataArrangeUtil {

    /**
     * 日志
     */
    private static Logger logger = LoggerFactory.getLogger("InterfCacheManager");

    /**
     * 整理数据
     * 
     * @param targetClass 数据类型
     * @param tableName 表名
     * @param sysDate 平台日期
     * @param basicDataSource 数据源
     * @param <T> 泛型
     * @throws IllegalAccessException IllegalAccessException
     * @throws InstantiationException InstantiationException
     * @see
     * @since 1.0
     */
    public static <T extends InterfEntity> void arrangeData(Class<T> targetClass, String tableName,
                                                            Date sysDate,
                                                            DruidDataSource basicDataSource)
        throws InstantiationException, IllegalAccessException {
        List<String> cons = new ArrayList<String>();
        cons.add("CHB_SYS_DATE = '" + DateUtil.format(sysDate, "yyyyMMdd") + "'");
        cons.add("OPMODE in ('1','2')");
        cons.add("OBJECT_ID in (select OBJECT_ID from " + tableName
                 + " group by OBJECT_ID having count(1) > 1)");
        String sql = SqlUtil.toStringDBSelect(targetClass, tableName, cons);
        logger.info("正在整理:" + tableName);
        List<T> dailyPriceEntityList = DBConnect.selectData(sql, targetClass, basicDataSource);
        try {
            Method tmpMethod;
            try {
                tmpMethod = targetClass.getMethod("getObjectId");
            }
            catch (NoSuchMethodException | SecurityException e) {
                tmpMethod = targetClass.getMethod("getObjectid");
            }
            Method getObjectId = tmpMethod;
            Method getOpmode = targetClass.getMethod("getOpmode");
            Method getOpdate = targetClass.getMethod("getOpdate");
            // 对于每一个增量更新的数据，取出更新、删除的记录中最新数据的ObjectId和操作时间
            List<String[]> cmdList = dailyPriceEntityList.stream().map(item -> {
                try {
                    return new String[] {(String)getObjectId.invoke(item),
                        (String)getOpmode.invoke(item), (String)getOpdate.invoke(item)};
                }
                catch (IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException e) {
                	logger.error("error:",e);
                }
                return null;
            }).filter(item -> item != null).collect(
                Collectors.groupingBy(item -> item[0])).values().stream().map(
                    item -> item.stream().sorted((a, b) -> {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        try {
                            Date date1 = sdf.parse(a[2].replace('T', ' ').substring(0, 19));
                            Date date2 = sdf.parse(b[2].replace('T', ' ').substring(0, 19));
                            return DateUtil.compareDate(date1, date2) * -1;
                        }
                        catch (ParseException e) {
                            if (a[2] == null || StringUtil.equals(a[2], "")) {
                                return 1;
                            }
                        }
                        return 0;
                    }).findFirst().orElse(null)).collect(Collectors.toList());
            // 删除历史数据
            List<String> sqlList = cmdList.stream().map(
                item -> "delete from " + tableName + " where OBJECT_ID = '" + item[0]
                        + "' and OPDATE != '" + item[2] + "'").collect(Collectors.toList());
            sqlList.add("delete from " + tableName + " where OPMODE = '2' and CHB_SYS_DATE = '"
                        + DateUtil.format(sysDate, "yyyyMMdd") + "'");
            DBConnect.insertData(tableName, sqlList, basicDataSource, true);
        }
        catch (NoSuchMethodException | SecurityException e) {
        	logger.error("error:",e);
        }
    }

}
