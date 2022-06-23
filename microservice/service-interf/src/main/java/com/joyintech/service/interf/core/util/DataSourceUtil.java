/*
 * 文件名：DataSourceUtil.java
 * 版权：Copyright 2016-2017 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管业务监管报送平台
 * 修改人：何阳阳
 * 修改时间：2017年11月27日
 * 修改内容：新建
 * 系统名称：理财资管业务监管报送平台
 */

package com.joyintech.service.interf.core.util;

import javax.sql.DataSource;

//import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.alibaba.druid.pool.DruidDataSource;
import com.joyintech.framework.util.StringUtil;

/**
 * 数据源工具类
 * 
 * @author 何阳阳
 * @version 1.0 2017年11月27日
 * @see DataSourceUtil
 * @since 1.0
 */
@Component("backDataSourceUtil")
public final class DataSourceUtil {

    /**
     *
     */
//    private static BasicDataSource dataSource;
	private static DruidDataSource dataSource;

    @Autowired(required = true)
    public void setDataSource(@Qualifier("masterDataSource") DruidDataSource dataSource) {
        DataSourceUtil.dataSource = dataSource;
    }

    public static String getURL() {
        return DataSourceUtil.dataSource.getUrl();
    }

    public static String getUserName() {
        return DataSourceUtil.dataSource.getUsername();
    }

    public static String getPassword() {
        return DataSourceUtil.dataSource.getPassword();
    }

    /**
     * 获取数据库名
     * 
     * @return 数据库名
     */
    public static String getDatabaseName() {
        String dbname = StringUtil.EMPTY;
        String url = getURL();
        // oracle
        if (url.startsWith("jdbc:oracle:thin:@//")) {
            dbname = splitTwo(url, "@//")[1];
        }
        else if (url.startsWith("jdbc:oracle:thin:@")) {
            dbname = splitTwo(url, "@")[1];
        }

        return dbname;
    }

    /**
     * 将字符串根据指定字符串分隔为两个字符串
     *
     * @param old 源字符串
     * @param splitor 分隔符
     * @return 新的字符串数组
     */
    public static String[] splitTwo(String old, String splitor) {
        String[] result = new String[2];
        if (StringUtil.isNotEmpty(old)) {
            int index = old.indexOf(splitor);
            if (index > -1) {
                result[0] = old.substring(0, index);
                result[1] = old.substring(index + splitor.length());
            }
            else {
                result[0] = old;
                result[1] = StringUtil.EMPTY;
            }
        }
        return result;
    }
}
