/*
 * 文件名：MstYieldCurveInfoService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：wangzhao
 * 修改时间：2018年8月8日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.common.basicdata.marketdata.yieldcurveinfo.service;

import java.io.File;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.export.ExportParam;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.ExcelUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.basicdata.constant.ServiceApplication;
import com.joyintech.tams.common.basicdata.marketdata.yieldcurveinfo.dto.ComYieldCurveInfoDto;
import com.joyintech.tams.systemtools.dto.ExcelValidDto;
import com.joyintech.tams.systemtools.util.CommonUtil;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;

/**
 * 收益率曲线service
 * 〈一句话功能简述〉
 * 〈功能详细描述〉
 * @author wangzhao
 * @version 1.0 2018年8月8日
 * @see ComYieldCurveInfoService
 * @since 1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ComYieldCurveInfoService extends CommonExportService<ComYieldCurveInfoDto> implements BaseProcessService<ComYieldCurveInfoDto>{
    /**
     * 
     * 查询
     * <功能详细描述>
     * @param pageParam Map<String, Object>
     * @return Page<MstYieldCurveInfoDto>
     * @throws Exception Exception
     * @see
     * @since 1.0
     */
    public Page<ComYieldCurveInfoDto> selectForPage(Map<String, Object> pageParam){
        return restTemplate.postForPage(ServiceApplication.CommonBasicDataService + "/ComYieldCurveInfo/selectForPage",
            pageParam, ComYieldCurveInfoDto.class);
    }
    /**
     * 
     * 获取所有的数据
     * <功能详细描述>
     * @param pageParam Map<String, Object>
     * @return List<MstYieldCurveInfoDto>
     * @see
     * @since 1.0
     */
    public List<ComYieldCurveInfoDto> selectAllList(Map<String, Object> pageParam) {
//        return mstYieldCurveInfo001Mapper.select(pageParam);
        return restTemplate.postForList(ServiceApplication.CommonBasicDataService + "/ComYieldCurveInfo/selectAllList",
            pageParam, ComYieldCurveInfoDto.class);
    }
    
    @Override
    @Transactional(propagation=Propagation.REQUIRES_NEW)
    public void doComplete(ComYieldCurveInfoDto entity, OperationType type) {
        entity.setOperateType(type.toString());
        restTemplate.postForList(ServiceApplication.CommonBasicDataService + "/ComYieldCurveInfo/doComplete",
            entity, null);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setCellValue(String beanName, Cell cell, String propName, Object value) {
        if(StringUtil.equals(propName, "curveDate")){
            cell.setCellValue(DateUtil.format((Date)value));
        }
    }
    /**
     * 导出
     * 
     * @param exportParam 导出信息
     * @see
     * @since 1.0
     */
    @Override
    protected void buildExportParam(ExportParam exportParam) {
        exportParam.config("/com/joyintech/tams/common/basicdata/marketdata/yieldcurveinfo/conf/export_conf.xml")
            .dataSource(DataSourceType.MASERT);
    }
    @Override
    public Page<ComYieldCurveInfoDto> selectPageData(Map<String, Object> selectParam) {
        return selectForPage(selectParam);
    }

    /**
     * 曲线名称的联想下拉框事件
     * @param map
     * @return
     */
    public Page<SelectOptionDto> getCurveName(Map<String, Object> map) {
        return restTemplate.postForPage(ServiceApplication.CommonBasicDataService
                        + "/ComYieldCurveInfo/getCurveName", map,
                SelectOptionDto.class);
    }

    public ComYieldCurveInfoDto selectNK(ComYieldCurveInfoDto comYieldCurveInfoDto) {
        return restTemplate.postForObject(ServiceApplication.CommonBasicDataService + "/ComYieldCurveInfo/selectNK",
                comYieldCurveInfoDto, ComYieldCurveInfoDto.class);
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
            if (flieName.indexOf("收益率曲线信息导入") != -1) {
                List<List<String>> dataList = dataAnalysis(flieName);
                if (null != dataList && dataList.size() > 0) {
                    // 将excel表格中的数据转化为dto
                    List<ExcelValidDto> checkResult = checkExcelData(dataList);
//                    if (checkResult.isEmpty()) {
//                        List<ComYieldCurveInfoDto> equityList = converToBean(dataList);
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
        List<ComYieldCurveInfoDto> equityList = converToBean(dataList,null);
        ComYieldCurveInfoDto ComYieldCurveInfoDto = null;
        List<Integer> duplicateDataList=new ArrayList<Integer>();
        if (equityList.size() >= 1) {
            for (int i = 0; i < equityList.size(); i++ ) {
                ComYieldCurveInfoDto = equityList.get(i);
                // 检查表内数据每行是否合法
                if (checkExcelCow(i, dataList.get(i)) != null) {
                    checkResult.addAll(checkExcelCow(i, dataList.get(i)));
                }
                if(ComYieldCurveInfoDto.getCurveDate() == null
                		|| ComYieldCurveInfoDto.getCurveCode() == null
                		|| ComYieldCurveInfoDto.getKeyPoint() == null
                		|| duplicateDataList.contains(i)){
                	continue;
                }
                // 检查表内数据是否有主键重复
                if (equityList.size() >= 2) {
                    for (int k = i + 1; k < equityList.size(); k++ ) {
                        if(equityList.get(k).getCurveDate() == null
                        		|| equityList.get(k).getCurveCode() == null
                        		|| equityList.get(k).getKeyPoint() == null){
                        	continue;
                        }
                        if (equityList.get(k).getCurveDate().equals(ComYieldCurveInfoDto.getCurveDate())
                            && equityList.get(k).getCurveCode().equals(
                                ComYieldCurveInfoDto.getCurveCode())
                            && equityList.get(k).getKeyPoint().equals(
                                ComYieldCurveInfoDto.getKeyPoint())) {
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
    public List<ComYieldCurveInfoDto> converToBean(List<List<String>> dataList,List<Integer> checkResult)
        throws ParseException {
        // 返回的list定义
        List<ComYieldCurveInfoDto> returnInfoList = new ArrayList<ComYieldCurveInfoDto>();
        ComYieldCurveInfoDto ComYieldCurveInfoDto = null;
        for (int i = 0; i < dataList.size(); i++ ) {
        	if(null !=checkResult && checkResult.contains(i)){
        		continue;
        	}
            List<String> list = dataList.get(i);
            ComYieldCurveInfoDto = new ComYieldCurveInfoDto();
            if (StringUtil.isNotEmpty(list.get(0))) {
                ComYieldCurveInfoDto.setCurveDate(DateUtil.parse(list.get(0).trim()));
            }
            if (StringUtil.isNotEmpty(list.get(1))) {
                ComYieldCurveInfoDto.setCurveCode(list.get(1).trim());
            }
            if (StringUtil.isNotEmpty(list.get(2))) {
                ComYieldCurveInfoDto.setCurveName(list.get(2).trim());
            }
            if (StringUtil.isNotEmpty(list.get(3))) {
                ComYieldCurveInfoDto.setKeyPoint(new BigDecimal(list.get(3).trim()));
            }
            if (StringUtil.isNotEmpty(list.get(4))) {
                ComYieldCurveInfoDto.setYield(new BigDecimal(list.get(4).trim()));
            }
            if (StringUtil.isNotEmpty(list.get(5))) {
                ComYieldCurveInfoDto.setCurveType(list.get(5).trim().split("：")[0].trim());
            }
            if (StringUtil.isNotEmpty(list.get(6))) {
                ComYieldCurveInfoDto.setN(new BigDecimal(list.get(6).trim()));
            }
            if (StringUtil.isNotEmpty(list.get(7))) {
                ComYieldCurveInfoDto.setK(new BigDecimal(list.get(7).trim()));
            }
            // Date date = new Date();
            ComYieldCurveInfoDto.setCreateUser(CommonUtil.getUserName());
            ComYieldCurveInfoDto.setCreateDept(CommonUtil.getDeptCode());
            // ComYieldCurveInfoDto.setCreateTime(date);
            ComYieldCurveInfoDto.setUpdateUser(CommonUtil.getUserName());
            // ComYieldCurveInfoDto.setUpdateTime(date);
            returnInfoList.add(ComYieldCurveInfoDto);
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
        // 列数为7
        if (null == list || list.size() != 8) {
            ExcelValidDto dto = new ExcelValidDto(StringUtil.EMPTY, String.valueOf(i), "数据列数错误");
            excelValidDtolist.add(dto);
        }else{
            if (StringUtil.isEmpty(list.get(0))) {
                // 验证导入曲线日期是否为空
                ExcelValidDto dto = new ExcelValidDto(list.get(1), String.valueOf(i+2), "曲线日期为空");
                excelValidDtolist.add(dto);
            }
            if (StringUtil.isEmpty(list.get(1))) {
                // 验证导入曲线名称是否为空
                ExcelValidDto dto = new ExcelValidDto(list.get(1), String.valueOf(i+2), "曲线代码为空");
                excelValidDtolist.add(dto);
            }
            if (StringUtil.isEmpty(list.get(3))) {
                // 验证导入关键年限是否为空
                ExcelValidDto dto = new ExcelValidDto(list.get(1), String.valueOf(i+3), "关键年限为空");
                excelValidDtolist.add(dto);
            }
            if (StringUtil.isEmpty(list.get(4))) {
                // 验证导入收益率%是否为空
                ExcelValidDto dto = new ExcelValidDto(list.get(1), String.valueOf(i+4), "收益率%为空");
                excelValidDtolist.add(dto);
            }
            if (StringUtil.isEmpty(list.get(5))) {
                // 验证导入曲线类型是否为空
                ExcelValidDto dto = new ExcelValidDto(list.get(1), String.valueOf(i+5), "曲线类型为空");
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
    public void dealAfterCheck(List<ComYieldCurveInfoDto> equityList) {
        restTemplate.postForObject(
            ServiceApplication.CommonBasicDataService + "/ComYieldCurveInfo/mergeInfoList",
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
    	List<ComYieldCurveInfoDto> equityList = converToBean(dataList,null);
    	List<Integer> duplicateData=new ArrayList<Integer>();
      for (int i = 0; i < dataList.size(); i++ ) {
      	ComYieldCurveInfoDto ComYieldCurveInfoDto = equityList.get(i);
      	if(ComYieldCurveInfoDto.getCurveDate() == null
        		|| ComYieldCurveInfoDto.getCurveCode() == null
        		|| ComYieldCurveInfoDto.getKeyPoint() == null
        		|| duplicateData.contains(i)){
      		continue;
        }
          // 检查表内数据是否有主键重复
          for (int k = i + 1; k < dataList.size(); k++ ) {
        	  if(equityList.get(k).getCurveDate() == null
              		|| equityList.get(k).getCurveCode() == null
              		|| equityList.get(k).getKeyPoint() == null){
        		  continue;
              }
              if (equityList.get(k).getCurveDate().equals(
              		ComYieldCurveInfoDto.getCurveDate())
                      && equityList.get(k).getCurveCode().equals(ComYieldCurveInfoDto.getCurveCode())
                      && equityList.get(k).getKeyPoint().equals(ComYieldCurveInfoDto.getKeyPoint())) {
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
