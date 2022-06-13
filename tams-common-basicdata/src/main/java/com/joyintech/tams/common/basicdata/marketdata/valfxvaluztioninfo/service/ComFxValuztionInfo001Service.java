/*
 * 文件名：ValBondMarketInfo001Service.java 
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：Administrator
 * 修改时间：2018年7月18日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */
 
package com.joyintech.tams.common.basicdata.marketdata.valfxvaluztioninfo.service;

import java.io.File;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.ExcelUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.basicdata.constant.ServiceApplication;
import com.joyintech.tams.common.basicdata.marketdata.valfxvaluztioninfo.dto.ComFxValuztionInfo001Dto;
import com.joyintech.tams.systemtools.dto.ExcelValidDto;
import com.joyintech.tams.systemtools.util.CommonUtil;

/**
 * 外汇估值信息
 * @author Administrator
 * @version 1.0 2018年7月18日
 * @see ComFxValuztionInfo001Service
 * @since 1.0
 */
@Service
public class ComFxValuztionInfo001Service extends CommonExportService<ComFxValuztionInfo001Dto> {
	
	/**
     * 交易编号获取
     *
     * @return 返回的数据
     */
	public List<SelectOptionDto> getFinprodId2(Map<String, Object> params) {

        return restTemplate.postForList(ServiceApplication.CommonBasicDataService + "/ComFxValuztionInfo001/getFinprodId2",
                params, SelectOptionDto.class);
	}

    /**
     * 分页查询
     * @param params
     * @return
     */
    public Page<ComFxValuztionInfo001Dto> queryPageList(Map<String, Object> params){
        return restTemplate.postForPage(ServiceApplication.CommonBasicDataService + "/ComFxValuztionInfo001/findPage",
    			params, ComFxValuztionInfo001Dto.class);
    }

    /**
     * 查询所有
     * @param params
     * @return
     */
    public List<ComFxValuztionInfo001Dto> queryAllList(Map<String, Object> params){
    	return restTemplate.postForList(
                ServiceApplication.CommonBasicDataService + "/ComFxValuztionInfo001/queryAllList",
                params, ComFxValuztionInfo001Dto.class);
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
            if (flieName.indexOf("外汇估值信息") != -1) {
                List<List<String>> dataList = dataAnalysis(flieName);
                if (null != dataList && dataList.size() > 0) {
                    // 将excel表格中的数据转化为dto
                    List<ExcelValidDto> checkResult = checkExcelData(dataList);
//                    if (checkResult.isEmpty()) {
//                        List<ComFxValuztionInfo001Dto> equityList = converToBean(dataList);
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
        List<ComFxValuztionInfo001Dto> equityList = converToBean(dataList,null);
        ComFxValuztionInfo001Dto ComFxValuztionInfo001Dto = null;
        List<Integer> duplicateDataList=new ArrayList<Integer>();
        if (equityList.size() >= 1) {
            for (int i = 0; i < equityList.size(); i++ ) {
                ComFxValuztionInfo001Dto = equityList.get(i);
                // 检查表内数据每行是否合法
                if (checkExcelCow(i, dataList.get(i)) != null) {
                    checkResult.addAll(checkExcelCow(i, dataList.get(i)));
                }
                if(ComFxValuztionInfo001Dto.getFinprodId2() == null
                		|| ComFxValuztionInfo001Dto.getDatasourceType() == null
                		|| ComFxValuztionInfo001Dto.getValDate() == null
                		|| ComFxValuztionInfo001Dto.getCcy() == null
                		|| duplicateDataList.contains(i)){
                	continue;
                }
                // 检查表内数据是否有主键重复
                if (equityList.size() >= 2) {
                    for (int k = i + 1; k < equityList.size(); k++ ) {
                        if(equityList.get(k).getFinprodId2() == null
                        		|| equityList.get(k).getDatasourceType() == null
                        		|| equityList.get(k).getValDate() == null
                        		|| equityList.get(k).getCcy() == null){
                        	continue;
                        }
                        if (equityList.get(k).getFinprodId2().equals(ComFxValuztionInfo001Dto.getFinprodId2())
                            && equityList.get(k).getDatasourceType().equals(
                                ComFxValuztionInfo001Dto.getDatasourceType())
                            && equityList.get(k).getValDate().equals(
                                ComFxValuztionInfo001Dto.getValDate())
                            && equityList.get(k).getCcy().equals(
                                    ComFxValuztionInfo001Dto.getCcy())) {
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
    public List<ComFxValuztionInfo001Dto> converToBean(List<List<String>> dataList,List<Integer> checkResult)
        throws ParseException {
        // 返回的list定义
        List<ComFxValuztionInfo001Dto> returnInfoList = new ArrayList<ComFxValuztionInfo001Dto>();
        ComFxValuztionInfo001Dto ComFxValuztionInfo001Dto = null;
        for (int i = 0; i < dataList.size(); i++ ) {
        	if(null !=checkResult && checkResult.contains(i)){
        		continue;
        	}
            List<String> list = dataList.get(i);
            ComFxValuztionInfo001Dto = new ComFxValuztionInfo001Dto();
            if (StringUtil.isNotEmpty(list.get(0))) {
                ComFxValuztionInfo001Dto.setFinprodId2(list.get(0).trim());
            }
            if (StringUtil.isNotEmpty(list.get(1))) {
                ComFxValuztionInfo001Dto.setValDate(list.get(1).trim());
            }
            if (StringUtil.isNotEmpty(list.get(2))) {
                ComFxValuztionInfo001Dto.setValuation(new BigDecimal(list.get(2).trim()));
            }
            if (StringUtil.isNotEmpty(list.get(3))) {
                ComFxValuztionInfo001Dto.setCcy(list.get(3).trim().split("：")[0].trim());
            }
            if (StringUtil.isNotEmpty(list.get(4))) {
                ComFxValuztionInfo001Dto.setDatasourceType(list.get(4).trim().split("：")[0].trim());
            }
            if (StringUtil.isNotEmpty(list.get(5))) {
                ComFxValuztionInfo001Dto.setInputType(list.get(5).trim().split("：")[0].trim());
            }
            // Date date = new Date();
            ComFxValuztionInfo001Dto.setCreateUser(CommonUtil.getUserName());
            ComFxValuztionInfo001Dto.setCreateDept(CommonUtil.getDeptCode());
            // ComFxValuztionInfo001Dto.setCreateTime(date);
            ComFxValuztionInfo001Dto.setUpdateUser(CommonUtil.getUserName());
            // ComFxValuztionInfo001Dto.setUpdateTime(date);
            returnInfoList.add(ComFxValuztionInfo001Dto);
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
        // 列数为6
        if (null == list || list.size() != 6) {
            ExcelValidDto dto = new ExcelValidDto(StringUtil.EMPTY, String.valueOf(i), "数据列数错误");
            excelValidDtolist.add(dto);
        }else{
            if (StringUtil.isEmpty(list.get(0))) {
                // 验证导入交易编号是否为空
                ExcelValidDto dto = new ExcelValidDto(list.get(0), String.valueOf(i+2), "交易编号为空");
                excelValidDtolist.add(dto);
            }
            if (StringUtil.isEmpty(list.get(1))) {
                // 验证导入估值日期是否为空
                ExcelValidDto dto = new ExcelValidDto(list.get(0), String.valueOf(i+2), "估值日期为空");
                excelValidDtolist.add(dto);
            }
            if (StringUtil.isEmpty(list.get(2))) {
                // 验证导入估值是否为空
                ExcelValidDto dto = new ExcelValidDto(list.get(0), String.valueOf(i+2), "估值为空");
                excelValidDtolist.add(dto);
            }
            if (StringUtil.isEmpty(list.get(3))) {
                // 验证导入币种是否为空
                ExcelValidDto dto = new ExcelValidDto(list.get(0), String.valueOf(i+2), "币种为空");
                excelValidDtolist.add(dto);
            }
            if (StringUtil.isEmpty(list.get(4))) {
                // 验证导入数据来源是否为空
                ExcelValidDto dto = new ExcelValidDto(list.get(0), String.valueOf(i+2), "数据来源为空");
                excelValidDtolist.add(dto);
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
    public void dealAfterCheck(List<ComFxValuztionInfo001Dto> equityList) {
        restTemplate.postForObject(
            ServiceApplication.CommonBasicDataService + "/ComFxValuztionInfo001/mergeInfoList",
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
    	List<ComFxValuztionInfo001Dto> equityList = converToBean(dataList,null);
    	List<Integer> duplicateData=new ArrayList<Integer>();
      for (int i = 0; i < dataList.size(); i++ ) {
      	ComFxValuztionInfo001Dto ComFxValuztionInfo001Dto = equityList.get(i);
      	if(ComFxValuztionInfo001Dto.getFinprodId2() == null
        		|| ComFxValuztionInfo001Dto.getDatasourceType() == null
        		|| ComFxValuztionInfo001Dto.getValDate() == null
        		|| ComFxValuztionInfo001Dto.getCcy() == null
        		|| duplicateData.contains(i)){
        	continue;
        }
          // 检查表内数据是否有主键重复
          for (int k = i + 1; k < dataList.size(); k++ ) {
        	  if(equityList.get(k).getFinprodId2() == null
              		|| equityList.get(k).getDatasourceType() == null
              		|| equityList.get(k).getValDate() == null
              		|| equityList.get(k).getCcy() == null){
        		  continue;
              }
              if (equityList.get(k).getFinprodId2().equals(
              		ComFxValuztionInfo001Dto.getFinprodId2())
                      && equityList.get(k).getDatasourceType().equals(ComFxValuztionInfo001Dto.getDatasourceType())
                      && equityList.get(k).getValDate().equals(ComFxValuztionInfo001Dto.getValDate())
                      && equityList.get(k).getCcy().equals(ComFxValuztionInfo001Dto.getCcy())) {
              	if(!duplicateData.contains(k)){
              		duplicateData.add(k);
              	}
              }
          }
      }
      if(duplicateData.isEmpty()){
    	  duplicateData.add(-1);
      }
      return duplicateData;
    }

}
