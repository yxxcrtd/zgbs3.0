/*
 * 文件名：TxtReadUtils.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：wangwei1
 * 修改时间：2018年6月26日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */
 
package com.joyintech.tams.valuation.common.interf.base.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.joyintech.tams.common.base.entity.ValInterflog;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.valuation.common.interf.base.constant.TempConstant;
import com.joyintech.tams.valuation.common.interf.base.dto.TemplateCheckReturnResult;
import com.joyintech.tams.valuation.common.interf.base.dto.TemplateColMatchDTO;
import com.joyintech.tams.valuation.common.interf.base.dto.TemplateOrderInfoReaderDTO;
import com.joyintech.tams.valuation.common.interf.base.dto.TemplateOrderInfoReaderResult;
import com.linuxense.javadbf.DBFField;
import com.linuxense.javadbf.DBFReader;
/**
 * 解析dbf接口文件
 * @author wangwei1
 * @version 1.0 2018年6月26日
 * @see DbfReadUtils
 * @since 1.0
 */
@Service
@Scope("prototype")
public class DbfReadUtils {
//    Logger logger = Logger.getLogger(DbfReadUtils.class);
	private static Logger logger = LoggerFactory.getLogger(DbfReadUtils.class);

    private final static  String CHARSET = "GB2312";
    
    /**
     * dbf类型接口文档解析
     * @param acctEntity TemplateOrderInfoReaderResult
     * @see
     * @since 1.0
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public <T> TemplateCheckReturnResult readDbf(TemplateOrderInfoReaderResult acctEntity) {
        TemplateCheckReturnResult result = new TemplateCheckReturnResult();
        // 文件路径
        String filePath = acctEntity.getFilePath();
        // 模板英文名
        String tmpEName = acctEntity.getTmpEName();
        List<T> listAll = new ArrayList();
        // 获取文件绝对路径
        List<String> pathList = SearchFilePath.queryFilePath(filePath, tmpEName);
        // 日志记录list
        List<ValInterflog> valInterflogList = new ArrayList<ValInterflog>();
        for (String path : pathList) {
            List list = new ArrayList();
            try {
                File file = new File(path);
                // 初始化接收数据DTO
                Class<?> clazz = Class.forName(TempConstant.ENTITY_PATH + acctEntity.getDtoName());
                list = readDbf(file, clazz, acctEntity, valInterflogList);
            }
            catch (Exception e) {
            	logger.error("",e);
            }
            listAll.addAll(list);
        }
        result.setT(listAll);
        result.setValInterflog(valInterflogList);
        return result;
    }

    public <T> List<T> readDbf(File file, Class<T> clazz, TemplateOrderInfoReaderResult acctEntity,
                               List<ValInterflog> valList) throws IOException {
        FileInputStream fis = null;
        DBFReader reader = null;
        List<T> records = new ArrayList<T>();
        try {
            fis = new FileInputStream(file);
            reader = new DBFReader(fis);
            reader.setCharactersetName(CHARSET);

            // 目标DTO字段
            Field[] fields = clazz.getDeclaredFields();
            // 将匹配关系表封装成Map
            Map<String, TemplateColMatchDTO> matchs = createMatchMap(acctEntity.getListTemplateColMatchDTO());
            // 构建checkMap
            Map<String, TemplateOrderInfoReaderDTO> checks = createCheckMap(acctEntity.getListTemplateOrderInfoDTO());
            // 按匹配关系设置对应的取值
            int fieldsCount = reader.getFieldCount();
            for (Field field : fields) {
                for (int i = 0; i < fieldsCount; i++) {
                    DBFField dbdfield = reader.getField(i);
                    if (matchs.containsKey(dbdfield.getName())) {
                        if (matchs.get(dbdfield.getName()).getDtoColEName().equals(field.getName())) {
                            matchs.get(dbdfield.getName()).setIndex(i);
                            break;
                        }
                    }
                }
            }

//            List<T> records = new ArrayList<T>();
            Object[] rowValues;
            int index = 0;
            while ((rowValues = reader.nextRecord()) != null) {
                // 判断数据是否需要接收
                ValInterflog checkLog = new ValInterflog();
                StringBuffer checkError = new StringBuffer();
                StringBuffer info = new StringBuffer();
                T t = readLine(clazz, rowValues, matchs, checks, checkLog, checkError, info);
                if (t != null) {
                    records.add(t);
                    checkLog.setIsCheck(DictCodeList.是否_是.getValue());
                }
                //            records.add(readLine(clazz, reader.nextRecord(), matchs, checks, checkLog, checkError,info));

                checkLog.setTmpName(acctEntity.getTmpEName());// 模板名称
                checkLog.setFileName(acctEntity.getTmpCName());// 文件名称
                checkLog.setExDate(new Date());// 日期
                checkLog.setParentSeqno(acctEntity.getParentSeqno());// 主副日志表连接键
                checkLog.setDetail(info.toString());
                checkLog.setNum((long)++index);
                checkLog.setRemark(checkError.toString());
                valList.add(checkLog);
            }
        }
        catch (IOException e) {
            throw e;
        } catch (IllegalAccessException e) {
        	logger.error(e.getMessage(),e);
        } catch (InstantiationException e) {
        	logger.error(e.getMessage(),e);
        } catch (NoSuchFieldException e) {
        	logger.error(e.getMessage(),e);
        } finally {
            if (reader != null) {
                reader.close();
            }
            if (fis != null) {
                fis.close();
            }
        }
        return records;
    }
    
    private Map<String, TemplateColMatchDTO> createMatchMap(List<TemplateColMatchDTO> matchList) {
        Map<String, TemplateColMatchDTO> map = new HashMap<String, TemplateColMatchDTO>();
        if (CollectionUtils.isEmpty(matchList)) {
            return map;
        }
        for (TemplateColMatchDTO dto : matchList) {
            map.put(dto.getTmpColEName(), dto);
        }
        return map;
    }
    
    /**
     * 构建校验信息字典
     * @param checkList
     * @return
     */
    private Map<String, TemplateOrderInfoReaderDTO> createCheckMap(List<TemplateOrderInfoReaderDTO> checkList) {
        Map<String, TemplateOrderInfoReaderDTO> checkIndex = new HashMap<String, TemplateOrderInfoReaderDTO>();
        if(checkList!=null&&!checkList.isEmpty()){
            for(TemplateOrderInfoReaderDTO check : checkList){
                checkIndex.put(check.getColEName(), check);
            }
        }
        return checkIndex;
    }
    
    /**
     * 读取一行数据
     * 
     * @param clazz
     * @param values
     * @param matchs
     * @param checks
     * @param checkLog
     * @param checkError
     * @param info
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     */
    private <T> T readLine(Class<T> clazz, Object[] values,
                           Map<String, TemplateColMatchDTO> matchs,
                           Map<String, TemplateOrderInfoReaderDTO> checks,
                           ValInterflog checkLog,
                           StringBuffer checkError,
                           StringBuffer info) throws IllegalAccessException, InstantiationException, NoSuchFieldException {
        T bean = clazz.newInstance();
        for (Entry<String, TemplateColMatchDTO> entry : matchs.entrySet()) {
            if (entry.getValue().getIndex() != null) {
                int index = entry.getValue().getIndex();
                String value = values[index] == null ? null : values[index].toString();
                Field field = clazz.getDeclaredField(entry.getValue().getDtoColEName());
                info.append(entry.getKey()+"-"+value+";");
                SetPropAndCheckUtil.setValue(field, bean, value, checks.get(entry.getKey()), checkLog, checkError,
                    entry.getKey());
            }
        }
        if (checkError.length() > 0) {
            return null;
        }
        return bean;
    }
}
