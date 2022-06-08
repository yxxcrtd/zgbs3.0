/*
 * 文件名：ValBondMarketInfo001Service.java 
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：Administrator
 * 修改时间：2018年7月18日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */
 
package com.joyintech.tams.common.basicdata.marketdata.bondmarketinfo.service;

import java.io.File;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joyintech.tams.common.basicdata.marketdata.bondmarketinfo.dto.ComBondMarketInfo001Dto;
import com.joyintech.tams.common.basicdata.constant.ServiceApplication;
import com.joyintech.tams.common.basicdata.util.ComBasicDataExcelUtil;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.systemtools.dto.ExcelValidDto;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.export.ExportParam;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.ExcelUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.systemtools.util.CommonUtil;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;

/**
 * 每日债券行情
 * @author Administrator
 * @version 1.0 2018年7月18日
 * @see ComBondMarketInfo001Service
 * @since 1.0
 */
@Service
public class ComBondMarketInfo001Service  extends CommonExportService<ComBondMarketInfo001Dto> implements BaseProcessService<ComBondMarketInfo001Dto> {

    /**
     * 使用给定的sqlID检索分页数据。返回类型是Service的泛型。
     * 
     * @param pageParam pageParam
     * @return
     * @see
     * @since 1.0
     */
    public Page<ComBondMarketInfo001Dto> queryPageList(Map<String, Object> params) throws Exception{
//        return super.queryForPage(()->valBondMarketInfo001Mapper.queryPageList(dto));
//        return valBondMarketInfo001Mapper.queryPageList(dto);
    	Page<ComBondMarketInfo001Dto> page = new Page<ComBondMarketInfo001Dto>();
    	page= restTemplate.postForPage(ServiceApplication.CommonBasicDataService + "/ComBondMarketInfo001/findPage",
    			params, ComBondMarketInfo001Dto.class);
    	return page;
    }
    
    
    public List<ComBondMarketInfo001Dto> queryAllList(Map<String, Object> params) throws Exception{
        
//        return valBondMarketInfo001Mapper.queryPageList(dto);
    	return restTemplate.postForList(
                ServiceApplication.CommonBasicDataService + "/ComBondMarketInfo001/queryAllList",
                params, ComBondMarketInfo001Dto.class);
    }

    @Override
    public void doComplete(ComBondMarketInfo001Dto valBondMarketInfo001Dto, OperationType type) {
        String action = StringUtil.EMPTY;
        if (OperationType.DELETE.equals(type)) {
            action = "delete";
        }
        else if (OperationType.NEW.equals(type)) {
            action = "insert";
        }
        else {
            action = "update";
        }
        restTemplate.postForObject(
                ServiceApplication.CommonBasicDataService + "/ComBondMarketInfo001/" + action, valBondMarketInfo001Dto,
                Integer.class);
    }

    /**
     * 查询资产代码
     */
    public Page<SelectOptionDto> getFinProductList(Map<String, Object> params) throws Exception{
        return restTemplate.postForPage(ServiceApplication.CommonBasicDataService + "/ComBondMarketInfo001/getFinProductList",
                params, SelectOptionDto.class);
    }

    /**
     * 点击编辑时查询数据
     */
    public List<ComBondMarketInfo001Dto> queryDtoByKey(Map<String, Object> params){
        return restTemplate.postForList(ServiceApplication.CommonBasicDataService + "/ComBondMarketInfo001/queryDtoByKey",
                params, ComBondMarketInfo001Dto.class);
    }
    
    
    /**
     * excel导入功能
     *
     * @param flieName 文件名称
     * @return 检验list
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    @Transactional(rollbackFor = Exception.class)
    public List<ExcelValidDto> importData(String flieName)
        throws Exception {
        // 检查文件名是否为空
        if (StringUtil.isNotEmpty(flieName)) {
            if (flieName.indexOf("每日债券行情导入") != -1) {
                List<List<String>> dataList = dataAnalysis(flieName);
                if (null != dataList && dataList.size() > 0) {
                    // 将excel表格中的数据转化为dto
                    List<ExcelValidDto> checkResult = checkExcelData(dataList);
//                    if (checkResult.isEmpty()) {
//                        List<ComBondMarketInfo001Dto> equityList = converToBean(dataList);
//                        // 保存，已存在则更新
//                        dealAfterCheck(equityList);
//                    }
                    return checkResult;
                }
            }
            else {
                List<ExcelValidDto> checkResult = new ArrayList<>();
                checkResult.add(new ExcelValidDto(StringUtil.EMPTY, StringUtil.EMPTY,
                		"文件名称错误，请下载模板后上传"));
                return checkResult;
            }
        }
        return null;
    }
    
    /**
     * excel导入数据整理
     *
     * @param flieName 文件名称
     * @return 检验list
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    public List<List<String>> dataAnalysis(String flieName)
        throws Exception {
    	String prefix = CacheUtil.getSysParam(GlobalConstant.COM_PROP_UPLOAD_PATH);
        if(!StringUtil.startsWith(flieName, prefix)
            && !StringUtil.startsWith(flieName, prefix.replace("/", File.separator))) {
     	   flieName = prefix + File.separator + flieName;
       }
         ExcelUtil excelUtil = ExcelUtil.build(flieName, null);
         List<List<String>> dataList = excelUtil.readToList(0, 1, 0);
        return dataList;
    }

    /**
     * 导入文件内容检查 <功能详细描述>
     * 
     * @param dataList 导入数据
     * @return 返回检查结果
     * @throws ParseException 抛出异常
     * @see
     * @since 1.0
     */
    private List<ExcelValidDto> checkExcelData(List<List<String>> dataList)
        throws ParseException {
        List<ExcelValidDto> checkResult = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(dataList)){
            for(int j=0; j<dataList.size(); j++){
                List<ExcelValidDto> result = checkExcelCow(j, dataList.get(j));
                if (CollectionUtils.isNotEmpty(result)) {
                    checkResult.addAll(result);
                }
            }
        }
        if(CollectionUtils.isNotEmpty(checkResult)){
            return checkResult;
        }
        List<ComBondMarketInfo001Dto> equityList = converToBean(dataList,null);
        ComBondMarketInfo001Dto ComBondMarketInfo001Dto = null;
        List<Integer> duplicateDataList=new ArrayList<Integer>();
        if (equityList.size() >= 1) {
            for (int i = 0; i < equityList.size(); i++ ) {
                ComBondMarketInfo001Dto = equityList.get(i);
                // 检查表内数据每行是否合法
//                if (checkExcelCow(i, dataList.get(i)) != null) {
//                    checkResult.addAll(checkExcelCow(i, dataList.get(i)));
//                }
                if(ComBondMarketInfo001Dto.getSecId() == null
                		|| ComBondMarketInfo001Dto.getValueDate() == null
                		|| duplicateDataList.contains(i)){
                	continue;
                }
                // 检查表内数据是否有主键重复
                if (equityList.size() >= 2) {
                    for (int k = i + 1; k < equityList.size(); k++ ) {
                        if(equityList.get(k).getSecId() == null
                        		|| equityList.get(k).getValueDate() == null){
                        	continue;
                        }
                        if (equityList.get(k).getSecId().equals(ComBondMarketInfo001Dto.getSecId())
                            && equityList.get(k).getValueDate().equals(
                                ComBondMarketInfo001Dto.getValueDate())) {
                        	if(!duplicateDataList.contains(k)){
                        		duplicateDataList.add(k);
                        		checkResult.add(new ExcelValidDto(dataList.get(k).get(1),
                                        String.valueOf(k + 2), "表中数据重复"));
                        	}
                        }
                    }
                }
            }
        }
        return checkResult;
    }

    /**
     * list转换为Bean
     *
     * @param dataList excel解析后的list
     * @return List<mstMarketCalendarInfo001Dto>
     * @throws ParseException 抛出异常
     * @see
     * @since 1.0
     */
    public List<ComBondMarketInfo001Dto> converToBean(List<List<String>> dataList,List<Integer> checkResult)
        throws ParseException {
        // 返回的list定义
        List<ComBondMarketInfo001Dto> returnInfoList = new ArrayList<ComBondMarketInfo001Dto>();
        ComBondMarketInfo001Dto ComBondMarketInfo001Dto = null;
        for (int i = 0; i < dataList.size(); i++ ) {
        	if(null !=checkResult && checkResult.contains(i)){
        		continue;
        	}
            List<String> list = dataList.get(i);
            ComBondMarketInfo001Dto = new ComBondMarketInfo001Dto();
            if (StringUtil.isNotEmpty(list.get(0))) {
                //基金标的是否存在
                Map<String, Object> params=new HashMap<>();
                params.put("finprodType", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
                params.put("initValue",list.get(0));
                try {
                    Page<SelectOptionDto> page=getFinProductList(params);
                    if(page.getTotal()==0){
                        continue;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                /** modify by yanya on 20210329 去除截取前四位字符的逻辑**/
                ComBondMarketInfo001Dto.setSecId(list.get(0).trim().substring(4));
                //ComBondMarketInfo001Dto.setSecId(list.get(0).trim());
            }
            if (StringUtil.isNotEmpty(list.get(1))) {
                ComBondMarketInfo001Dto.setFinprodAbbr(list.get(1).trim());
            }
            if (StringUtil.isNotEmpty(list.get(2))&&!ComBasicDataExcelUtil.isErrorDate(list.get(2))) {
                ComBondMarketInfo001Dto.setValueDate(list.get(2).trim());
            }
            if (StringUtil.isNotEmpty(list.get(3))&&CommonUtil.isFloatNumeric(list.get(3))) {
                ComBondMarketInfo001Dto.setCloPrice(new BigDecimal(list.get(3).trim()));
            }
            if (StringUtil.isNotEmpty(list.get(4))&&CommonUtil.isFloatNumeric(list.get(4))) {
                ComBondMarketInfo001Dto.setAvgPrice(new BigDecimal(list.get(4).trim()));
            }
            if (StringUtil.isNotEmpty(list.get(5))&&CommonUtil.isFloatNumeric(list.get(5))) {
                ComBondMarketInfo001Dto.setBuyOnePrice(new BigDecimal(list.get(5).trim()));
            }
            if (StringUtil.isNotEmpty(list.get(6))&&CommonUtil.isFloatNumeric(list.get(6))) {
                ComBondMarketInfo001Dto.setSellOnePrice(new BigDecimal(list.get(6).trim()));
            }
            if (StringUtil.isNotEmpty(list.get(7))&&CommonUtil.isFloatNumeric(list.get(7))) {
                ComBondMarketInfo001Dto.setMaxPrice(new BigDecimal(list.get(7).trim()));
            }
            if (StringUtil.isNotEmpty(list.get(8))&&CommonUtil.isFloatNumeric(list.get(8))) {
                ComBondMarketInfo001Dto.setMinPrice(new BigDecimal(list.get(8).trim()));
            }
            if (StringUtil.isNotEmpty(list.get(9))&&CommonUtil.isFloatNumeric(list.get(9))) {
                ComBondMarketInfo001Dto.setCloPriceFull(new BigDecimal(list.get(9).trim()));
            }
            if (StringUtil.isNotEmpty(list.get(10))&&CommonUtil.isFloatNumeric(list.get(10))) {
                ComBondMarketInfo001Dto.setAvgPriceFull(new BigDecimal(list.get(10).trim()));
            }
            if (StringUtil.isNotEmpty(list.get(11))&&CommonUtil.isFloatNumeric(list.get(11))) {
                ComBondMarketInfo001Dto.setBuyOnePriceFull(new BigDecimal(list.get(11).trim()));
            }
            if (StringUtil.isNotEmpty(list.get(12))&&CommonUtil.isFloatNumeric(list.get(12))) {
                ComBondMarketInfo001Dto.setSellOnePriceFull(new BigDecimal(list.get(12).trim()));
            }
            if (StringUtil.isNotEmpty(list.get(13))&&CommonUtil.isFloatNumeric(list.get(13))) {
                ComBondMarketInfo001Dto.setMaxPriceFull(new BigDecimal(list.get(13).trim()));
            }
            if (StringUtil.isNotEmpty(list.get(14))&&CommonUtil.isFloatNumeric(list.get(14))) {
                ComBondMarketInfo001Dto.setMinPriceFull(new BigDecimal(list.get(14).trim()));
            }
            if (StringUtil.isNotEmpty(list.get(15))&&CommonUtil.isFloatNumeric(list.get(15))) {
                ComBondMarketInfo001Dto.setMarketAmt(new BigDecimal(list.get(15).trim()));
            }
            // Date date = new Date();
            ComBondMarketInfo001Dto.setCreateUser(CommonUtil.getUserName());
            ComBondMarketInfo001Dto.setCreateDept(CommonUtil.getDeptCode());
            // ComBondMarketInfo001Dto.setCreateTime(date);
            ComBondMarketInfo001Dto.setUpdateUser(CommonUtil.getUserName());
            // ComBondMarketInfo001Dto.setUpdateTime(date);
            returnInfoList.add(ComBondMarketInfo001Dto);
        }
        return returnInfoList;
    }

    /**
     * 检查excel是否为空或者传入列是否正确
     *
     * @param i 第几行
     * @param list 每一列解析后的List
     * @return 检查结构
     * @see
     * @since 1.0
     */
    private List<ExcelValidDto> checkExcelCow(int i, List<String> list) {
        List<ExcelValidDto> excelValidDtolist = new ArrayList<ExcelValidDto>();
        // 列数为16
        if (null == list || list.size() != 16) {
            ExcelValidDto dto = new ExcelValidDto(StringUtil.EMPTY, String.valueOf(i), "数据列数错误");
            excelValidDtolist.add(dto);
        }else{
            if (StringUtil.isEmpty(list.get(0))) {
                // 验证导入债券代码是否为空
                ExcelValidDto dto = new ExcelValidDto(list.get(0), String.valueOf(i+2), "债券代码为空");
                excelValidDtolist.add(dto);
            }else{
                //基金标的是否存在
                Map<String, Object> params=new HashMap<>();
                params.put("finprodType", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
                params.put("initValue",list.get(0));
                try {
                    Page<SelectOptionDto> page=getFinProductList(params);
                    if(page.getTotal()==0){
                        ExcelValidDto dto = new ExcelValidDto(list.get(0), String.valueOf(i+2), "标的信息 不存在！");
                        excelValidDtolist.add(dto);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (StringUtil.isEmpty(list.get(1))) {
                // 验证导入估值来源是否为空
                ExcelValidDto dto = new ExcelValidDto(list.get(1), String.valueOf(i+2), "债券简称为空");
                excelValidDtolist.add(dto);
            }
            if (StringUtil.isEmpty(list.get(2))) {
                // 验证导入估值日期是否为空
                ExcelValidDto dto = new ExcelValidDto(list.get(2), String.valueOf(i+2), "估值日期为空");
                excelValidDtolist.add(dto);
            }else{
                if(ComBasicDataExcelUtil.isErrorDate(list.get(2))){
                    ExcelValidDto dto = new ExcelValidDto(list.get(2), String.valueOf(i+2), "估值日期格式不对");
                    excelValidDtolist.add(dto);
                }else{
                    //导入已存在的债券信息
                    Map<String, Object> params=new HashMap<>();
                    params.put("secId", list.get(0).substring(4,list.get(0).length()));
                    params.put("valueDate",list.get(2));
                    List<ComBondMarketInfo001Dto> keyList=queryDtoByKey(params);
                    if(keyList!=null&&keyList.size()>0){
                        ExcelValidDto dto = new ExcelValidDto(list.get(2), String.valueOf(i+2), "债券信息已存在");
                        excelValidDtolist.add(dto);
                    }
                }

            }
            if (StringUtil.isEmpty(list.get(3))) {
                // 验证导入收盘价净价是否为空
                ExcelValidDto dto = new ExcelValidDto(list.get(3), String.valueOf(i+2), "收盘价净价为空");
                excelValidDtolist.add(dto);
            }else{
                if(!CommonUtil.isFloatNumeric(list.get(3))){
                    ExcelValidDto dto = new ExcelValidDto(list.get(3), String.valueOf(i+2), "收盘价净价不为数字");
                    excelValidDtolist.add(dto);
                }
            }
            if (StringUtil.isEmpty(list.get(4))) {
                // 验证导入平均价净价是否为空
                ExcelValidDto dto = new ExcelValidDto(list.get(4), String.valueOf(i+2), "平均价净价为空");
                excelValidDtolist.add(dto);
            }else{
                if(!CommonUtil.isFloatNumeric(list.get(4))){
                    ExcelValidDto dto = new ExcelValidDto(list.get(4), String.valueOf(i+2), "平均价净价不为数字");
                    excelValidDtolist.add(dto);
                }
            }
            if (StringUtil.isEmpty(list.get(5))) {
                // 验证导入买一价净价是否为空
                ExcelValidDto dto = new ExcelValidDto(list.get(5), String.valueOf(i+2), "买一价净价为空");
                excelValidDtolist.add(dto);
            }else{
                if(!CommonUtil.isFloatNumeric(list.get(5))){
                    ExcelValidDto dto = new ExcelValidDto(list.get(5), String.valueOf(i+2), "买一价净价不为数字");
                    excelValidDtolist.add(dto);
                }
            }
            if (StringUtil.isEmpty(list.get(6))) {
                // 验证导入卖一价净价是否为空
                ExcelValidDto dto = new ExcelValidDto(list.get(6), String.valueOf(i+2), "卖一价净价为空");
                excelValidDtolist.add(dto);
            }else{
                if(!CommonUtil.isFloatNumeric(list.get(6))){
                    ExcelValidDto dto = new ExcelValidDto(list.get(6), String.valueOf(i+2), "卖一价净价不为数字");
                    excelValidDtolist.add(dto);
                }
            }
            if (StringUtil.isEmpty(list.get(7))) {
                // 验证导入最高价净价是否为空
                ExcelValidDto dto = new ExcelValidDto(list.get(7), String.valueOf(i+2), "最高价净价为空");
                excelValidDtolist.add(dto);
            }else{
                if(!CommonUtil.isFloatNumeric(list.get(7))){
                    ExcelValidDto dto = new ExcelValidDto(list.get(7), String.valueOf(i+2), "最高价净价不为数字");
                    excelValidDtolist.add(dto);
                }
            }
            if (StringUtil.isEmpty(list.get(8))) {
                // 验证导入最低价净价是否为空
                ExcelValidDto dto = new ExcelValidDto(list.get(8), String.valueOf(i+2), "最低价净价为空");
                excelValidDtolist.add(dto);
            }else{
                if(!CommonUtil.isFloatNumeric(list.get(8))){
                    ExcelValidDto dto = new ExcelValidDto(list.get(8), String.valueOf(i+2), "最低价净价不为数字");
                    excelValidDtolist.add(dto);
                }
            }
            if (StringUtil.isEmpty(list.get(9))) {
                // 验证导入收盘价全价是否为空
                ExcelValidDto dto = new ExcelValidDto(list.get(9), String.valueOf(i+2), "收盘价全价为空");
                excelValidDtolist.add(dto);
            }else{
                if(!CommonUtil.isFloatNumeric(list.get(9))){
                    ExcelValidDto dto = new ExcelValidDto(list.get(9), String.valueOf(i+2), "收盘价全价不为数字");
                    excelValidDtolist.add(dto);
                }
            }
            if (StringUtil.isEmpty(list.get(10))) {
                // 验证导入平均价全价是否为空
                ExcelValidDto dto = new ExcelValidDto(list.get(10), String.valueOf(i+2), "平均价全价为空");
                excelValidDtolist.add(dto);
            }else{
                if(!CommonUtil.isFloatNumeric(list.get(10))){
                    ExcelValidDto dto = new ExcelValidDto(list.get(10), String.valueOf(i+2), "平均价全价不为数字");
                    excelValidDtolist.add(dto);
                }
            }
            if (StringUtil.isEmpty(list.get(11))) {
                // 验证导入买一价全价是否为空
                ExcelValidDto dto = new ExcelValidDto(list.get(10), String.valueOf(i+2), "买一价全价为空");
                excelValidDtolist.add(dto);
            }else{
                if(!CommonUtil.isFloatNumeric(list.get(11))){
                    ExcelValidDto dto = new ExcelValidDto(list.get(11), String.valueOf(i+2), "买一价全价不为数字");
                    excelValidDtolist.add(dto);
                }
            }
            if (StringUtil.isEmpty(list.get(12))) {
                // 验证导入卖一价全价是否为空
                ExcelValidDto dto = new ExcelValidDto(list.get(12), String.valueOf(i+2), "卖一价全价为空");
                excelValidDtolist.add(dto);
            }else{
                if(!CommonUtil.isFloatNumeric(list.get(12))){
                    ExcelValidDto dto = new ExcelValidDto(list.get(12), String.valueOf(i+2), "卖一价全价不为数字");
                    excelValidDtolist.add(dto);
                }
            }
            if (StringUtil.isEmpty(list.get(13))) {
                // 验证导入最高价全价是否为空
                ExcelValidDto dto = new ExcelValidDto(list.get(13), String.valueOf(i+2), "最高价全价为空");
                excelValidDtolist.add(dto);
            }else{
                if(!CommonUtil.isFloatNumeric(list.get(13))){
                    ExcelValidDto dto = new ExcelValidDto(list.get(13), String.valueOf(i+2), "最高价全不为数字");
                    excelValidDtolist.add(dto);
                }
            }
            if (StringUtil.isEmpty(list.get(14))) {
                // 验证导入最低价全价是否为空
                ExcelValidDto dto = new ExcelValidDto(list.get(14), String.valueOf(i+2), "最低价全价为空");
                excelValidDtolist.add(dto);
            }else{
                if(!CommonUtil.isFloatNumeric(list.get(14))){
                    ExcelValidDto dto = new ExcelValidDto(list.get(14), String.valueOf(i+2), "最低价全价不为数字");
                    excelValidDtolist.add(dto);
                }
            }
            if (StringUtil.isEmpty(list.get(15))) {
                // 验证导入债券市场余额是否为空
                ExcelValidDto dto = new ExcelValidDto(list.get(15), String.valueOf(i+2), "债券市场余额为空");
                excelValidDtolist.add(dto);
            }else{
                if(!CommonUtil.isFloatNumeric(list.get(15))){
                    ExcelValidDto dto = new ExcelValidDto(list.get(15), String.valueOf(i+2), "债券市场余额不为数字");
                    excelValidDtolist.add(dto);
                }
            }
        }
        return excelValidDtolist;
    }

    /**
     * check后的保存
     * 
     * @param equityList 需要保存的数据
     * @see
     * @since 1.0
     */
    public void dealAfterCheck(List<ComBondMarketInfo001Dto> equityList) {
        restTemplate.postForObject(
            ServiceApplication.CommonBasicDataService + "/ComBondMarketInfo001/mergeInfoList",
            equityList, Integer.class);
    }
    
    /**
     * 导入文件重复数据检查 <功能详细描述>
     * 
     * @param dataList 导入数据
     * @return 返回检查结果
     * @throws ParseException 抛出异常
     * @see
     * @since 1.0
     */
    public List<Integer> duplicateCheck(List<List<String>> dataList)
        throws ParseException {
    	List<ComBondMarketInfo001Dto> equityList = converToBean(dataList,null);
    	List<Integer> duplicateData=new ArrayList<Integer>();
    	if(!CollectionUtils.isEmpty(equityList)) {
            for (int i = 0; i < dataList.size(); i++) {
                ComBondMarketInfo001Dto ComBondMarketInfo001Dto = equityList.get(i);
                if (ComBondMarketInfo001Dto.getSecId() == null
                        || ComBondMarketInfo001Dto.getValueDate() == null
                        || duplicateData.contains(i)) {
                    continue;
                }
                // 检查表内数据是否有主键重复
                for (int k = i + 1; k < dataList.size(); k++) {
                    if (equityList.get(k).getSecId() == null
                            || equityList.get(k).getValueDate() == null) {
                        continue;
                    }
                    if (equityList.get(k).getSecId().equals(
                            ComBondMarketInfo001Dto.getSecId())
                            && equityList.get(k).getValueDate().equals(ComBondMarketInfo001Dto.getValueDate())) {
                        if (!duplicateData.contains(k)) {
                            duplicateData.add(k);
                        }
                    }
                }
            }
        }
//      if(duplicateData.isEmpty()){
//    	  duplicateData.add(-1);
//      }
      return duplicateData;
    }
}
