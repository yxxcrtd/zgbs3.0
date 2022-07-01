/*
 * 文件名：StandardTxtReadUtils.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：dongjun
 * 修改时间：2018年10月20日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.common.interf.base.util;

import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.ValInterflog;
import com.joyintech.tams.valuation.common.interf.base.dto.TemplateCheckReturnResult;
import com.joyintech.tams.valuation.common.interf.base.dto.TemplateColMatchDTO;
import com.joyintech.tams.valuation.common.interf.base.dto.TemplateOrderInfoReaderDTO;
import com.joyintech.tams.valuation.common.interf.base.dto.TemplateOrderInfoReaderResult;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 解析dat接口文件
 *
 * @author rendongliang
 * @version 1.0 2018年12月19日
 * @see DatReadUtils
 * @since 1.0
 */
@Service
@Scope("prototype")
public class DatReadUtils {

    private String entityPath = "com.joyintech.tams.valuation.common.interf.tempTables.entity.";

    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(DatReadUtils.class);

    /**
     * txt类型接口文档解析
     * @see
     * @since 1.0
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public <T> TemplateCheckReturnResult readTxt(TemplateOrderInfoReaderResult acctEntity) {

        TemplateCheckReturnResult result = new TemplateCheckReturnResult();
        // 文件路径
        String filePath = acctEntity.getFilePath();
        // 模板英文名
        String tmpEName = acctEntity.getTmpEName();
        // DTO名称
        String dtoName = acctEntity.getDtoName();
        // 将匹配关系表封装成Map
        Map<String, TemplateColMatchDTO> matchs = createMatchMap(acctEntity.getListTemplateColMatchDTO());

        List<TemplateOrderInfoReaderDTO> listTemplateOrderInfoDTO = acctEntity.getListTemplateOrderInfoDTO();
        List<String> dataList = new ArrayList<>();
        List list = new ArrayList();
        List<ValInterflog> valInterflogList = new ArrayList<>();
        tmpEName="LCZZCC";
        // 获取文件绝对路径
        List<String> pathList = SearchFilePath.queryFilePath(filePath, tmpEName);
        //字段，截取长度
        Map<String,List<String>> map = getColsListWithLengthList(pathList);
        // 获取文件绝对路径
        List<String> GZPathList = SearchFilePath.queryFilePath(filePath, tmpEName);
        for (String path : GZPathList) {
            try {
                String suffix = path.substring(path.lastIndexOf(".")+1);
                if(!"dat".equals(suffix))
                    continue;
                String entityClassName = entityPath + dtoName;
                File file = new File(path);
                if (file.isFile() && file.exists()) {
                    InputStreamReader isr = null;
                    BufferedReader br = null;
                    FileInputStream fis = null;
                    try {
                        fis = new FileInputStream(file);
                        isr = new InputStreamReader(fis, "GB2312");
                        br = new BufferedReader(isr);
                        String lineTxt = null; // 读取文件的方法
                        while (!StringUtil.isEmpty(lineTxt = br.readLine())) {
                            dataList.add(lineTxt);
                        }
                    } catch (Exception e) {
                        logger.error("", e);
                    } finally {
                        try {
                            if (br != null) {
                                br.close();
                            }
                            if (fis != null){
                                fis.close();
                            }
                        } catch (Exception e) {
                            logger.error("", e);
                        }
                        try {
                            if (isr != null) {
                                isr.close();
                            }
                        } catch (Exception e) {
                            logger.error("", e);
                        }
                    }
                    if (!dataList.isEmpty() && dataList.size() > 1) {
                        DatParserHandler handler = new DatParserHandler();
                        boolean flag = handler.init(entityClassName, dataList, listTemplateOrderInfoDTO, acctEntity,
                                matchs,map);
                        if (flag) {
                            handler.txtParse();
                            if (handler.getReturnList() != null && !handler.getReturnList().isEmpty()) {
                                list.addAll(handler.getReturnList());
                            }
                        }
                        if (handler.getLogResult() != null && !handler.getLogResult().isEmpty()) {
                            valInterflogList.addAll(handler.getLogResult());
                        }
                    }
                }
            } catch (Exception e) {
                logger.error("", e);
            }
        }
        result.setT(list);
        result.setValInterflog(valInterflogList);
        return result;
    }

    private Map<String, TemplateColMatchDTO> createMatchMap(List<TemplateColMatchDTO> matchList) {
        Map<String, TemplateColMatchDTO> map = new HashMap<String, TemplateColMatchDTO>();
        if (CollectionUtils.isEmpty(matchList)) {
            return map;
        }
        for (TemplateColMatchDTO dto : matchList) {
            map.put(dto.getDtoColEName(), dto);
        }
        return map;
    }

    /**
     * 解析flg文件
     * @param pathList
     * @return
     */
    private Map<String,List<String>> getColsListWithLengthList(List<String> pathList){
        Map<String,List<String>> map = new HashMap<String,List<String>>();
        List<String> colsList = new ArrayList<String>(); //列名
        List<String> lengthList = new ArrayList<String>(); //截取字符串最后一位下标
        int index = 0;
        for (int i = 0; i < pathList.size();i++) {
            String path = pathList.get(i);
            String suffix = path.substring(path.lastIndexOf(".")+1);
            if("flg".equals(suffix)) {
                File file = new File(path);
                if (file.isFile() && file.exists()) {
                    index++;
                    InputStreamReader isr = null;
                    BufferedReader br = null;
                    FileInputStream fis = null;
                    try {
                        fis = new FileInputStream(file);
                        isr = new InputStreamReader(fis, "UTF-8");
                        br = new BufferedReader(isr);
                        String lineTxt = null; // 读取文件的方法
                        boolean flag = false; //下一行是否 是字段名，截取长度等信息
                        int columnCountIndex = 0;
                        int columnCount = 0;
                        while (true) {
                        	if(!StringUtil.isEmpty(lineTxt = br.readLine())){
                        		if (lineTxt.indexOf("=") > 0) {
                                    if ("FILENAME".equals(lineTxt.split("=")[0].trim())) {
                                        if (StringUtils.isNotBlank(lineTxt.split("=")[1])) {
                                        	String gzPath = path.substring(0,path.lastIndexOf(File.separator))+File.separator+lineTxt.split("=")[1];
                                            GZUtils.unGzipFile(gzPath);//对dat文件解压
                                        }
                                    } else if ("COLUMNDESCRIPTION".equals(lineTxt.split("=")[0].trim()) && index == 1) {
                                        flag = true;
                                    } else if ("COLUMNCOUNT".equals(lineTxt.split("=")[0].trim())){
                                    	columnCount = Integer.parseInt(lineTxt.split("=")[1].trim());
                                    }
                                    continue;
                                }
                                if (flag && index == 1) {
                                    String array[] = lineTxt.split("\\$\\$");
                                    colsList.add(array[1]);
                                    if (array[3].contains("(") && array[3].contains(")") && array[3].contains(",")) {
                                    	columnCountIndex++;
                                        String length = array[3].replace("(", "").replace(")", "").split(",")[1];
                                        lengthList.add(length);
                                    }
                                }
                        	}
                            if(columnCountIndex==columnCount && columnCount!=0){
                                break;
                            }

                        }
                    } catch (Exception e) {
                        logger.error("", e);
                    } finally {
                        try {
                            if (br != null) {
                                br.close();
                            }
                            if (isr != null) {
                                isr.close();
                            }
                            if (fis != null){
                                fis.close();
                            }
                        } catch (Exception e) {
                            logger.error("", e);
                        }
                    }
                }
            }
        }
        map.put("colsList",colsList);
        map.put("lengthList",lengthList);
        return map;
    }
}
