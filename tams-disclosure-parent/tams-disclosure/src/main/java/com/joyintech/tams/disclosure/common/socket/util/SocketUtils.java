/*
 * 文件名：SocketUtils.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：ZhangYangFei
 * 修改时间：2018年12月10日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.disclosure.common.socket.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.PropertySource;

import com.joyintech.framework.util.SpringContextUtil;


/**
 * Socket工具类
 * 
 * @author ZhangYangFei
 * @version 1.0 2018年12月10日
 * @see SocketUtils
 * @since 1.0
 */
public class SocketUtils {
	
	/**
     * 日志
     */
    private static Logger logger = LoggerFactory.getLogger(SocketUtils.class);

    /**
     * 配置文件
     */
    static PropertySource<?> propertySource;

    /**
     * 获取spring配置文件值
     * 
     * @param key 配置文件key
     * @return
     * @see
     * @since 1.0
     */
    public static String getPropertyValue(String key) {
//        String value = "";
//        try {
//            if (propertySource == null) {
//                PropertySourcesLoader propertiesLoader = new PropertySourcesLoader();
//                ResourceLoader resourceLoader = new DefaultResourceLoader();
//                Resource resource = resourceLoader.getResource("classpath:/application.yml");
//                propertySource = propertiesLoader.load(resource, null, "application", null);
//            }
//            Object profile = propertySource.getProperty(key);
//            if (null != profile) {
//                value = profile.toString();
//            }
//        }
//        catch (IOException e1) {
//        	logger.error(e1.getMessage(),e1);
//        }
//        return value;
    	 return SpringContextUtil.getPropValue(key, String.class);
    }
    
    /**
     * 组装固定长度字符串
     * 
     * @param sendstr 报文字符串
     * @param length 长度字符串占的位数
     * @return 长度字符串
     * @see
     * @since 1.0
     */
    public static String fixedLength(String sendstr, Integer length) {
        // 0
        String str_zero = "0";
        if (sendstr == null) {
            sendstr = "";
        }
        int strlength = sendstr.length() + length;
        String fixed = String.valueOf(strlength);
        for (int i = 0; i < strlength - String.valueOf(strlength).length() - sendstr.length(); i++ ) {
            fixed = str_zero + fixed;
        }
        return fixed;
    }
}
