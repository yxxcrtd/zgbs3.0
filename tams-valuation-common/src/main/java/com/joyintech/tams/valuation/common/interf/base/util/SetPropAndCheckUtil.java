/*
 * 文件名：SetPropAndCheckUtil.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：chenggaoling
 * 修改时间：2018年9月4日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */
 
package com.joyintech.tams.valuation.common.interf.base.util;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.joyintech.framework.util.DateUtil;
import com.joyintech.tams.common.base.entity.ValInterflog;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.valuation.common.interf.base.dto.TemplateOrderInfoReaderDTO;

/**
 * 接口设值和Check共通
 * 〈功能详细描述〉
 * @author chenggaoling
 * @version 1.0 2018年9月4日
 * @see SetPropAndCheckUtil
 * @since 1.0
 */
public class SetPropAndCheckUtil {
//    private static Logger logger = Logger.getLogger(SetPropAndCheckUtil.class);
	
	private static Logger logger = LoggerFactory.getLogger(SetPropAndCheckUtil.class);

    /**
     * 
     * 设值和check
     * <功能详细描述>
     * @param _f 需要设值的Field
     * @param _obj 共通接收类
     * @param _v 值
     * @param check 模版字段check信息
     * @param checkLog 校验log
     * @param checkError 校验错误信息
     * @param o_col 模版字段
     * @throws Exception
     * @see
     * @since 1.0
     */
    public static void setValue(Field _f, Object _obj, String _v, TemplateOrderInfoReaderDTO check,
                          ValInterflog checkLog, StringBuffer checkError, String o_col) throws IllegalAccessException {
        _f.setAccessible(true);
        Class<?> t = _f.getType();
        if (t.toString().contains("java.util.Date")) {
            Date _d = checkDateValue(_v,o_col,check,checkLog,checkError);
            _f.set(_obj, _d);
        }else if (t.toString().contains("java.math.BigDecimal")) {
            BigDecimal _b = checkBigValue(_v,o_col,check,checkLog,checkError);
            _f.set(_obj, _b);
        }else if (t.toString().contains("java.lang.Integer")) {
            Integer _i = checkIntValue(_v,o_col,check,checkLog,checkError);
            _f.set(_obj, _i);
        }else if (t.toString().contains("java.lang.Long")) {
            Long _l = checkLonValue(_v,o_col,check,checkLog,checkError);
            _f.set(_obj, _l);
        }else if(t.toString().contains("java.lang.String")){
            _v = checkStrValue(_v,o_col,check,checkLog,checkError);
            _f.set(_obj, _v);
        }else{
            logger.warn("字段类型无法识别，请联系系统管理员.");
        }
    }


    /**
     * String类型数据校验
     * @param _v
     * @param colName
     * @return
     */
    private static String checkStrValue(String _v, String colName, TemplateOrderInfoReaderDTO check,
                                 ValInterflog checkLog, StringBuffer checkError) {
        if(check != null){
            //非空检查
            if(DictCodeList.是否_是.getValue().equals(check.getIsNull())){
                if(_v==null||_v.isEmpty()){
                    checkLog.setIsCheck(DictCodeList.是否_否.getValue());
                    checkError.append(colName+"："+"字段不可为空;");
                    return null;
                }
            }
            //长度检查
            if(_v!=null&&!_v.isEmpty()){
                BigDecimal _len = new BigDecimal(_v.trim().length());
                if(_len.compareTo(check.getMaxLength())>0){
                    checkLog.setIsCheck(DictCodeList.是否_否.getValue());
                    checkError.append(colName+"：字段长度为"+_len+"超过指定长度"+check.getMaxLength());
                    return null;
                }
            }
        }else{
            checkLog.setIsCheck(DictCodeList.是否_否.getValue());
            checkError.append(colName+"：字段不存在校验规则，需要联系管理员进行配置.");
            logger.warn(colName+"：字段不存在校验规则，需要联系管理员进行配置.");
            return null;
        }
        return _v == null ? null : _v.trim();
    }


    /**
     * Long类型数据校验
     * @param _v
     * @param colName
     * @return
     */
    private static Long checkLonValue(String _v, String colName, TemplateOrderInfoReaderDTO check, ValInterflog checkLog,
                               StringBuffer checkError) {
        Long l = null;
        if(check != null){
            //非空检查
            if(DictCodeList.是否_是.getValue().equals(check.getIsNull())){
                if(_v==null||_v.isEmpty()){
                    checkLog.setIsCheck(DictCodeList.是否_否.getValue());
                    checkError.append(colName+"："+"字段不可为空;");
                    logger.warn(colName+"："+"字段不可为空;");
                    return null;
                }
            }
            //长度检查
            if(_v!=null&&!_v.isEmpty()){
                BigDecimal _len = new BigDecimal(_v.trim().length());
                if(_len.compareTo(check.getMaxLength())>0){
                    checkLog.setIsCheck(DictCodeList.是否_否.getValue());
                    checkError.append(colName+"：字段长度为"+_len+"超过指定长度"+check.getMaxLength()+";");
                    logger.warn(colName+"：字段长度为"+_len+"超过指定长度"+check.getMaxLength()+";");
                    return null;
                }
            }
            //格式检查
            try{
                if(_v==null||_v.trim().isEmpty()){
                    l = null;
                }else{
                    l = new Long(_v.trim());
                }
            }catch (Exception e) {
                checkLog.setIsCheck(DictCodeList.是否_否.getValue());
                checkError.append(colName+"：Long数值错误——"+_v+";");
                logger.warn(colName+"：Long数值错误——"+_v+";");
            }
        }else{
            checkLog.setIsCheck(DictCodeList.是否_否.getValue());
            checkError.append(colName+"：字段不存在校验规则，需要联系管理员进行配置.");
            logger.warn(colName+"：字段不存在校验规则，需要联系管理员进行配置.");
            return null;
        }
        return l;
    }


    /**
     * Integer数据校验
     * @param _v
     * @param colName
     * @return
     */
    private static Integer checkIntValue(String _v, String colName, TemplateOrderInfoReaderDTO check, ValInterflog checkLog,
                                  StringBuffer checkError) {
        Integer l = null;
        if(check != null){
            //非空检查
            if(DictCodeList.是否_是.getValue().equals(check.getIsNull())){
                if(_v==null||_v.isEmpty()){
                    checkLog.setIsCheck(DictCodeList.是否_否.getValue());
                    checkError.append(colName+"："+"字段不可为空;");
                    logger.warn(colName+"："+"字段不可为空;");
                    return null;
                }
            }
            //长度检查
            if(_v!=null&&!_v.isEmpty()){
                BigDecimal _len = new BigDecimal(_v.trim().length());
                if(_len.compareTo(check.getMaxLength())>0){
                    checkLog.setIsCheck(DictCodeList.是否_否.getValue());
                    checkError.append(colName+"：字段长度为"+_len+"超过指定长度"+check.getMaxLength()+";");
                    logger.warn(colName+"：字段长度为"+_len+"超过指定长度"+check.getMaxLength()+";");
                    return null;
                }
            }
            //格式检查
            try{
                if(_v==null||_v.trim().isEmpty()){
                    l = null;
                }else{
                    l = new Integer(_v.trim());
                }
            }catch (Exception e) {
                checkLog.setIsCheck(DictCodeList.是否_否.getValue());
                checkError.append(colName+"：Integer数值错误——"+_v+";");
                logger.warn(colName+"：Integer数值错误——"+_v+";");
            }
        }else{
            checkLog.setIsCheck(DictCodeList.是否_否.getValue());
            checkError.append(colName+"：字段不存在校验规则，需要联系管理员进行配置.");
            logger.warn(colName+"：字段不存在校验规则，需要联系管理员进行配置.");
            return null;
        }
        return l;
    }


    /**
     * BigDecimal数据校验
     * @param _v
     * @param colName
     * @return
     */
    private static BigDecimal checkBigValue(String _v, String colName, TemplateOrderInfoReaderDTO check, ValInterflog checkLog,
                                     StringBuffer checkError) {
        BigDecimal l = null;
        if(check != null){
            //非空检查
            if(DictCodeList.是否_是.getValue().equals(check.getIsNull())){
                if(_v==null||_v.isEmpty()){
                    checkLog.setIsCheck(DictCodeList.是否_否.getValue());
                    checkError.append(colName+"："+"字段不可为空;");
                    logger.warn(colName+"："+"字段不可为空;");
                    return null;
                }
            }
            //长度检查
            if(_v!=null&&!_v.isEmpty()){
                BigDecimal _len = new BigDecimal(_v.trim().length());
                if(_len.compareTo(check.getMaxLength())>0){
                    checkLog.setIsCheck(DictCodeList.是否_否.getValue());
                    checkError.append(colName+"：字段长度为"+_len+"超过指定长度"+check.getMaxLength()+";");
                    logger.warn(colName+"：字段长度为"+_len+"超过指定长度"+check.getMaxLength()+";");
                    return null;
                }
            }
            //格式检查
            try{
                if(_v==null||_v.trim().isEmpty()){
                    l = null;
                }else{
                    l = new BigDecimal(_v.trim());
                }
            }catch (Exception e) {
                checkLog.setIsCheck(DictCodeList.是否_否.getValue());
                checkError.append(colName+"：BigDecimal数值错误——"+_v+";");
                logger.warn(colName+"：BigDecimal数值错误——"+_v+";");
            }
        }else{
            checkLog.setIsCheck(DictCodeList.是否_否.getValue());
            checkError.append(colName+"：字段不存在校验规则，需要联系管理员进行配置.");
            logger.warn(colName+"：字段不存在校验规则，需要联系管理员进行配置.");
            return null;
        }
        return l;
    }


    /**
     * Date类型数据校验
     * @param _v
     * @param colName
     * @return
     */
    private static Date checkDateValue(String _v, String colName, TemplateOrderInfoReaderDTO check, ValInterflog checkLog,
                                StringBuffer checkError) {
        Date l = null;
        if(check != null){
            //非空检查
            if(DictCodeList.是否_是.getValue().equals(check.getIsNull())){
                if(_v==null||_v.isEmpty()){
                    checkLog.setIsCheck(DictCodeList.是否_否.getValue());
                    checkError.append(colName+"："+"字段不可为空;");
                    logger.warn(colName+"："+"字段不可为空;");
                    return null;
                }
            }
            //长度检查
            if(_v!=null&&!_v.isEmpty()){
                BigDecimal _len = new BigDecimal(_v.trim().length());
                if(_len.compareTo(check.getMaxLength())>0){
                    checkLog.setIsCheck(DictCodeList.是否_否.getValue());
                    checkError.append(colName+"：字段长度为"+_len+"超过指定长度"+check.getMaxLength()+";");
                    logger.warn(colName+"：字段长度为"+_len+"超过指定长度"+check.getMaxLength()+";");
                    return null;
                }
            }
            //格式检查
            try{
                if(_v==null||_v.trim().isEmpty()){
                    l = null;
                }else{
                    l = DateUtil.parse(_v.trim());
                }
            }catch (Exception e) {
                checkLog.setIsCheck(DictCodeList.是否_否.getValue());
                checkError.append(colName+"：Date数值错误——"+_v+";");
                logger.warn(colName+"：Date数值错误——"+_v+";");
            }
        }else{
            checkLog.setIsCheck(DictCodeList.是否_否.getValue());
            checkError.append(colName+"：字段不存在校验规则，需要联系管理员进行配置.");
            logger.warn(colName+"：字段不存在校验规则，需要联系管理员进行配置.");
            return null;
        }
        return l;
    }
}
