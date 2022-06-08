package com.joyintech.tams.common.basicdata.marketdata.mstbondquotationinfo.service;

import java.io.File;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joyintech.tams.common.basicdata.constant.ServiceApplication;
import com.joyintech.tams.common.basicdata.marketdata.mstbondquotationinfo.dto.ComBondQuotationInfoDto;
import com.joyintech.tams.common.basicdata.util.ComBasicDataExcelUtil;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.systemtools.dto.ExcelValidDto;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.joyintech.framework.ajax.AjaxResult;
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
 * @author wangzehui
 */
@Service
public class ComBondQuotationInfoService extends CommonExportService<ComBondQuotationInfoDto> implements BaseProcessService<ComBondQuotationInfoDto> {

    /**
     * 使用给定的sqlID检索分页数据。返回类型是Service的泛型。
     * 
     * @param pageParam pageParam
     * @return Page类 Page
     * @throws Exception 异常信息
     */
    public Page<ComBondQuotationInfoDto> selectForPage(Map<String, Object> pageParam) {
        if (pageParam.get("valueSource") == "MOD") {
            return restTemplate.postForPage(
                    ServiceApplication.CommonBasicDataService + "/ComBondQuotationInfo/findPageformod",
                    pageParam, ComBondQuotationInfoDto.class);
        }

        return restTemplate.postForPage(
            ServiceApplication.CommonBasicDataService + "/ComBondQuotationInfo/findPage",
            pageParam, ComBondQuotationInfoDto.class);
    }

    /**
     * 资产代码的联想下拉框
     * 
     * @param param param
     * @return List
     * @throws Exception 异常
     */
    public List<SuggestDto> getSecIdOption(String param) {
        return restTemplate.getForList(ServiceApplication.CommonBasicDataService
                                       + "/ComBondQuotationInfo/getSecId?secId=" + param,
            SuggestDto.class);
    }

    /**
     * 查询债券简称
     * 
     * @return List
     */
    public Page<SelectOptionDto> getFinprodAbbrOption(Map<String,Object> param) throws Exception{
        return restTemplate.postForPage(ServiceApplication.CommonBasicDataService
                                       + "/ComBondQuotationInfo/getFinprodAbbrOption",param,
            SelectOptionDto.class);
    }

    /**
     * 删除
     * 
     * @param pageId 画面ID
     * @param dto MstBondQuotationInfoDto
     * @return 删除结果
     */
    public String delete(String pageId, ComBondQuotationInfoDto dto) {
        return restTemplate.postForObject(
            ServiceApplication.CommonBasicDataService + "/ComBondQuotationInfo/{pageId}/delete",
            dto, String.class, pageId);
    }

    /**
     * 工作流（留痕操作结束的时候执行的方法）
     */
    @Override
    public void doComplete(ComBondQuotationInfoDto entity, OperationType type) {
        String action = StringUtil.EMPTY;
        // entity.setSystemCode("SYS");
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
            ServiceApplication.CommonBasicDataService + "/ComBondQuotationInfo/" + action, entity,
            Integer.class);
    }

    /**
     * 导出
     */
    @Override
    protected void buildExportParam(ExportParam exportParam) {
        exportParam.config(
                "/com/joyintech/tams/common/basicdata/marketdata/mstbondquotationinfo/conf/export_conf.xml").dataSource(
                DataSourceType.MASERT);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<ComBondQuotationInfoDto> selectPageData(Map<String, Object> selectParam) {
        return selectForPage(selectParam);
    }

    /**
     * 获取债券简称
     * 
     * @param finprodAbbr 债券简称
     * @return List
     */
    public List<SuggestDto> getFinProdAbbr(String finprodAbbr) {
        return restTemplate.getForList(ServiceApplication.CommonBasicDataService
                                       + "/ComBondQuotationInfo/getFinProdAbbr?finprodAbbr="
                                       + finprodAbbr,
            SuggestDto.class);
    }

    /**
     * @param secId secId
     * @return dto
     */
    public ComBondQuotationInfoDto getBySecId(String secId) {
        return restTemplate.getForObject(ServiceApplication.CommonBasicDataService
                                         + "/ComBondQuotationInfo/getBySecId?secId=" + secId,
            ComBondQuotationInfoDto.class);
    }

    /**
     * @param secId secId
     * @param valueDate valueDate
     * @param valueSource valueSource
     * @return dto
     */
    public ComBondQuotationInfoDto selectByKey(String secId, String valueDate, String valueSource) {
        return restTemplate.getForObject(ServiceApplication.CommonBasicDataService
                                         + "/ComBondQuotationInfo/selectByKey?secId="
                                         + secId + "&valueDate=" + valueDate+ "&valueSource=" + valueSource,
            ComBondQuotationInfoDto.class);
    }

    /**
     * 新建画面的资产代码查询
     * 
     * @param secId 债券代码
     * @return list
     * @see
     * @since 1.0
     */
    public List<SuggestDto> getSecIdToNew(String secId) {
        return restTemplate.getForList(
            ServiceApplication.CommonBasicDataService + "/ComBondQuotationInfo/getSecIdToNew?secId="
                                                           + secId,
                                                           SuggestDto.class);
    }

    public List<SelectOptionDto> getAssetCodeList() {
        return restTemplate.getForList(
            ServiceApplication.CommonBasicDataService + "/ComBondQuotationInfo/getFinProdAbbr",
            SelectOptionDto.class);
    }

    /**
     * 查询符合条件的所有数据
     * @param pageParam
     * @return
     */
    public List<ComBondQuotationInfoDto> selectAllList(Map<String, Object> pageParam) {
        return restTemplate.postForList(
                ServiceApplication.CommonBasicDataService + "/ComBondQuotationInfo/selectAllList",
                pageParam, ComBondQuotationInfoDto.class);
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
            if (flieName.indexOf("债券估值行情导入") != -1) {
                List<List<String>> dataList = dataAnalysis(flieName);
                if (null != dataList && dataList.size() > 0) {
                    // 将excel表格中的数据转化为dto
                    List<ExcelValidDto> checkResult = checkExcelData(dataList);
//                    if (checkResult.isEmpty()) {
//                        List<ComBondQuotationInfoDto> equityList = converToBean(dataList);
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
        if (CollectionUtils.isNotEmpty(dataList)) {
            for (int i = 0; i < dataList.size(); i++ ) {
                List<ExcelValidDto> result = checkExcelCow(i, dataList.get(i));
                // 检查表内数据每行是否合法
                if (CollectionUtils.isNotEmpty(result)) {
                    checkResult.addAll(result);
                }
            }
        }
        if (checkResult.size()>0){
            return checkResult;
        }
        List<ComBondQuotationInfoDto> equityList = converToBean(dataList,null);
        ComBondQuotationInfoDto ComBondQuotationInfoDto = null;
        List<Integer> duplicateDataList=new ArrayList<Integer>();
        if (equityList.size() >= 1) {
            for (int i = 0; i < equityList.size(); i++ ) {
                ComBondQuotationInfoDto = equityList.get(i);
                if(ComBondQuotationInfoDto.getSecId() == null
                		|| ComBondQuotationInfoDto.getValueDate() == null
                		|| ComBondQuotationInfoDto.getValueSource() == null
                		|| duplicateDataList.contains(i)){
                	continue;
                }
                // 检查表内数据是否有主键重复
                if (equityList.size() >= 2) {
                    for (int k = i + 1; k < equityList.size(); k++ ) {
                        if(equityList.get(k).getSecId() == null
                        		|| equityList.get(k).getValueDate() == null
                        		|| equityList.get(k).getValueSource() == null){
                        	continue;
                        }
                        if (equityList.get(k).getSecId().equals(ComBondQuotationInfoDto.getSecId())
                            && equityList.get(k).getValueDate().equals(
                                ComBondQuotationInfoDto.getValueDate())
                            && equityList.get(k).getValueSource().equals(
                                ComBondQuotationInfoDto.getValueSource())) {
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
    public List<ComBondQuotationInfoDto> converToBean(List<List<String>> dataList,List<Integer> checkResult)
        throws ParseException {
        // 返回的list定义
        List<ComBondQuotationInfoDto> returnInfoList = new ArrayList<ComBondQuotationInfoDto>();
        ComBondQuotationInfoDto ComBondQuotationInfoDto = null;
        for (int i = 0; i < dataList.size(); i++ ) {
        	if(null !=checkResult && checkResult.contains(i)){
        		continue;
        	}
            List<String> list = dataList.get(i);
            ComBondQuotationInfoDto = new ComBondQuotationInfoDto();
            if (StringUtil.isNotEmpty(list.get(0))) {
                ComBondQuotationInfoDto.setSecId(list.get(0).trim());
            }
            if (StringUtil.isNotEmpty(list.get(1))) {
                ComBondQuotationInfoDto.setFinprodAbbr(list.get(1).trim());
            }
            if (StringUtil.isNotEmpty(list.get(2))) {
                ComBondQuotationInfoDto.setValueSource(list.get(2).trim().split("：")[0].trim());
            }
            if (StringUtil.isNotEmpty(list.get(3))&&!ComBasicDataExcelUtil.isErrorDate(list.get(3))) {
                    ComBondQuotationInfoDto.setValueDate(DateUtil.parse(list.get(3).trim()));
            }
            if (StringUtil.isNotEmpty(list.get(4))) {
                ComBondQuotationInfoDto.setLastPeriod(new BigDecimal(list.get(4).trim()));
            }
            if (StringUtil.isNotEmpty(list.get(5))) {
                ComBondQuotationInfoDto.setPrice(new BigDecimal(list.get(5).trim()));
            }
            if (StringUtil.isNotEmpty(list.get(6))) {
                ComBondQuotationInfoDto.setNetprice(new BigDecimal(list.get(6).trim()));
            }
            if (StringUtil.isNotEmpty(list.get(7))) {
                ComBondQuotationInfoDto.setmDuration(new BigDecimal(list.get(7).trim()));
            }
            if (StringUtil.isNotEmpty(list.get(8))) {
                ComBondQuotationInfoDto.setmConvexity(new BigDecimal(list.get(8).trim()));
            }
            if (StringUtil.isNotEmpty(list.get(9))) {
                ComBondQuotationInfoDto.setBpvalue(new BigDecimal(list.get(9).trim()));
            }
            if (StringUtil.isNotEmpty(list.get(10))) {
                ComBondQuotationInfoDto.setSduration(new BigDecimal(list.get(10).trim()));
            }
            if (StringUtil.isNotEmpty(list.get(11))) {
                ComBondQuotationInfoDto.setScnvxty(new BigDecimal(list.get(11).trim()));
            }
            if (StringUtil.isNotEmpty(list.get(12))) {
                ComBondQuotationInfoDto.setInterestDuration(new BigDecimal(list.get(12).trim()));
            }
            if (StringUtil.isNotEmpty(list.get(13))) {
                ComBondQuotationInfoDto.setInterestCnvxty(new BigDecimal(list.get(13).trim()));
            }
            if (StringUtil.isNotEmpty(list.get(14))) {
                ComBondQuotationInfoDto.setBpyield(
                    new BigDecimal(list.get(14).trim()).divide(new BigDecimal(100)));
            }
            // TISHKS-6:删除估价净价（行权）字段 fuyongsheng 20211112 edit start
//            if (StringUtil.isNotEmpty(list.get(15))) {
//                ComBondQuotationInfoDto.setNetvaluationExercise(new BigDecimal(list.get(15).trim()));
//            }
            // TISHKS-6:删除估价净价（行权）字段 fuyongsheng 20211112 edit end
            // Date date = new Date();
            ComBondQuotationInfoDto.setCreateUser(CommonUtil.getUserName());
            ComBondQuotationInfoDto.setCreateDept(CommonUtil.getDeptCode());
            // ComBondQuotationInfoDto.setCreateTime(date);
            ComBondQuotationInfoDto.setUpdateUser(CommonUtil.getUserName());
            // ComBondQuotationInfoDto.setUpdateTime(date);
            returnInfoList.add(ComBondQuotationInfoDto);
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
        // 列数为15
        if (null == list || list.size() != 15) {
            ExcelValidDto dto = new ExcelValidDto(StringUtil.EMPTY, String.valueOf(i), "数据列数错误");
            excelValidDtolist.add(dto);
        }else{
            if (StringUtil.isEmpty(list.get(0))) {
                // 验证导入债券代码是否为空
                ExcelValidDto dto = new ExcelValidDto(list.get(1), String.valueOf(i+2), "债券代码为空");
                excelValidDtolist.add(dto);
            }
            if (StringUtil.isEmpty(list.get(2))) {
                // 验证导入估值来源是否为空
                ExcelValidDto dto = new ExcelValidDto(list.get(1), String.valueOf(i+2), "估值来源为空");
                excelValidDtolist.add(dto);
            }
            if (StringUtil.isEmpty(list.get(3))) {
                // 验证导入估值日期是否为空
                ExcelValidDto dto = new ExcelValidDto(list.get(1), String.valueOf(i+2), "估值日期为空");
                excelValidDtolist.add(dto);
            }else{
                if(ComBasicDataExcelUtil.isErrorDate(list.get(3))){
                    ExcelValidDto dto = new ExcelValidDto(list.get(1), String.valueOf(i+2), "估值日期格式错误");
                    excelValidDtolist.add(dto);
                }
            }
            if (StringUtil.isEmpty(list.get(5))) {
                // 验证导入市价全价是否为空
                ExcelValidDto dto = new ExcelValidDto(list.get(1), String.valueOf(i+2), "估价全价为空");
                excelValidDtolist.add(dto);
            }else{
                if(!CommonUtil.isNumeric(list.get(5))){
                    ExcelValidDto dto = new ExcelValidDto(list.get(1), String.valueOf(i+2), "估价全价格式错误");
                    excelValidDtolist.add(dto);
                }
            }
            if (StringUtil.isEmpty(list.get(6))) {
                // 验证导入市价净价是否为空
                ExcelValidDto dto = new ExcelValidDto(list.get(1), String.valueOf(i+2), "估价净价为空");
                excelValidDtolist.add(dto);
            }else{
                if(!CommonUtil.isNumeric(list.get(6))){
                    ExcelValidDto dto = new ExcelValidDto(list.get(1), String.valueOf(i+2), "估价净价格式错误");
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
    public void dealAfterCheck(List<ComBondQuotationInfoDto> equityList) {
        restTemplate.postForObject(
            ServiceApplication.CommonBasicDataService + "/ComBondQuotationInfo/mergeInfoList",
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
        List<ExcelValidDto> checkResult = checkExcelData(dataList);
        if(CollectionUtils.isNotEmpty(checkResult)){
            return new ArrayList<>();
        }
    	List<ComBondQuotationInfoDto> equityList = converToBean(dataList,null);
    	List<Integer> duplicateData=new ArrayList<Integer>();
      for (int i = 0; i < dataList.size(); i++ ) {
      	ComBondQuotationInfoDto valBondQuotationInfoDto = equityList.get(i);
      	if(valBondQuotationInfoDto.getSecId() == null
        		|| valBondQuotationInfoDto.getValueDate() == null
        		|| valBondQuotationInfoDto.getValueSource() == null
        		|| duplicateData.contains(i)){
      		continue;
        }
          // 检查表内数据是否有主键重复
          for (int k = i + 1; k < dataList.size(); k++ ) {
        	  if(equityList.get(k).getSecId() == null
              		|| equityList.get(k).getValueDate() == null
              		|| equityList.get(k).getValueSource() == null){
        		  continue;
              }
              if (equityList.get(k).getSecId().equals(
              		valBondQuotationInfoDto.getSecId())
                      && equityList.get(k).getValueSource().equals(valBondQuotationInfoDto.getValueSource())
                      && equityList.get(k).getValueDate().equals(valBondQuotationInfoDto.getValueDate())) {
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
