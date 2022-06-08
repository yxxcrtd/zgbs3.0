package com.joyintech.tams.common.basicdata.marketdata.mstinterestratequotationinfo.service;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joyintech.tams.common.basicdata.constant.ServiceApplication;
import com.joyintech.tams.common.basicdata.marketdata.mstinterestratequotationinfo.dto.ComInterestRateQuotationInfoDto;
import com.joyintech.tams.common.basicdata.util.ComBasicDataExcelUtil;
import com.joyintech.tams.systemtools.dto.ExcelValidDto;
import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.export.ExportParam;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.ExcelUtil;
import com.joyintech.framework.util.FileNameUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.systemtools.util.CommonUtil;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
/**
 * 利率行情信息service
 * @author fanshuanghu
 * @version 1.0 2018年8月20日
 * @see ComInterestRateQuotationInfoService
 * @since 1.0
 */
@Service
@Transactional
public class ComInterestRateQuotationInfoService extends CommonExportService<ComInterestRateQuotationInfoDto>
    implements BaseProcessService<ComInterestRateQuotationInfoDto> {

    /**
     * 分页查询
     * 
     * @param pageParam 传入的查询条件
     * @return Page<ComInterestRateQuotationInfoDto> page对象
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    public Page<ComInterestRateQuotationInfoDto> selectForPage(Map<String, Object> pageParam){
        return restTemplate.postForPage(
            ServiceApplication.CommonBasicDataService + "/ComInterestRateQuotationInfo/selectForPage",
            pageParam, ComInterestRateQuotationInfoDto.class);
    };

    public List<ComInterestRateQuotationInfoDto> selectForList(Map<String, Object> pageParam){
        return restTemplate.postForList(
                ServiceApplication.CommonBasicDataService + "/ComInterestRateQuotationInfo/selectForList",
                pageParam, ComInterestRateQuotationInfoDto.class);
    }

    /**
     * 根据主键查询
     * @param map 查询条件
     * @return ComInterestRateQuotationInfoDto 实体类
     */
    public ComInterestRateQuotationInfoDto selectByPrimaryKey(Map<String, Object> map) {
        return restTemplate.postForObject(
            ServiceApplication.CommonBasicDataService + "/ComInterestRateQuotationInfo/selectByPrimaryKey",
            map, ComInterestRateQuotationInfoDto.class);
    }

    /**
     * 导出 {@inheritDoc}
     */
    @Override
    protected void buildExportParam(ExportParam exportParam) {
        exportParam.config(
                "/com/joyintech/tams/common/basicdata/marketdata/mstinterestratequotationinfo/conf/export_conf.xml").dataSource(DataSourceType.MASERT);
    }
    /**
     * 导出
     */
    @Override
    public Page<ComInterestRateQuotationInfoDto> selectPageData(Map<String, Object> selectParam) {
        return selectForPage(selectParam);
    }
    /**
     * 设置日期格式
     * {@inheritDoc}
     */
    @Override
    public void setCellValue(String beanName, Cell cell, String propName, Object value) {
        if(("relaeseDate".equals(propName) || "effDate".equals(propName)) && value != null){
            cell.setCellValue(DateUtil.format((Date)value));
        }
    }
    /**
     * 流程实现方法 {@inheritDoc}
     */
    @Override
    public void doComplete(ComInterestRateQuotationInfoDto entity, OperationType type) {
        entity.setOperateType(type.toString());
        entity.setIntRate(entity.getIntRate().divide(new BigDecimal("100")));
        restTemplate.postForObject(
            ServiceApplication.CommonBasicDataService + "/ComInterestRateQuotationInfo/doComplete",
            entity, Integer.class);
    }

    // add by zwq 20210222  start
    // 添加批量导入功能
    @Transactional(rollbackFor = Exception.class)
    public List<ExcelValidDto> importData(String flieName)
            throws Exception {
        // 检查文件名是否为空
        if (StringUtil.isNotEmpty(flieName)) {
            if (flieName.indexOf("基准利率行情") != -1) {
                ExcelUtil excelUtil = ExcelUtil.build(FileNameUtil.getUploadFilePath(flieName),
                        null);
                List<List<String>> dataList = excelUtil.readToList(0, 1, 0);
                if (null != dataList && dataList.size() > 0) {
                    // 将excel表格中的数据转化为dto
                    List<ExcelValidDto> checkResult = checkExcelData(dataList);
                    if (checkResult.isEmpty()) {
                        List<ComInterestRateQuotationInfoDto> equityList = converToBean(dataList);
                        // 保存，已存在则更新
                        dealAfterCheck(equityList);
                    }
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
    // add by zwq 20210222  end

    // add by zwq 20210222  start
    /**
     * 导入文件内容检查 <功能详细描述>
     */
    private List<ExcelValidDto> checkExcelData(List<List<String>> dataList)
            throws ParseException {
        List<ExcelValidDto> checkResult = new ArrayList<>();
        // TISHKS-61:基准利率行情信息导入先校验数据是否合法,再转成bean fuyongsheng 20211108 edit start
//        List<ComInterestRateQuotationInfoDto> equityList = converToBean(dataList);
//        ComInterestRateQuotationInfoDto valInterestRateQuotationInfoDto = null;
//        if (equityList.size() >= 1) {
//            for (int i = 0; i < equityList.size(); i++ ) {
//                valInterestRateQuotationInfoDto = equityList.get(i);
//                // 检查表内数据每行是否合法
//                if (checkExcelCow(i, dataList.get(i)) != null) {
//                    checkResult.addAll(checkExcelCow(i, dataList.get(i)));
//                }
//            }
//        }
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
        List<ComInterestRateQuotationInfoDto> equityList = converToBean(dataList);
        if (equityList.size()>=1){
            for (int i = 0; i < equityList.size(); i++ ) {
                ComInterestRateQuotationInfoDto valInterestRateQuotationInfoDto = equityList.get(i);
                // 检查表内数据是否有主键重复
                if (equityList.size() >= 2) {
                    for (int k = i + 1; k < equityList.size(); k++ ) {
                        if (equityList.get(k).getEffDate().equals(valInterestRateQuotationInfoDto.getEffDate())
                                &&equityList.get(k).getIntRateId().equals(valInterestRateQuotationInfoDto.getIntRateId())){
                            checkResult.add(new ExcelValidDto(dataList.get(i).get(1),
                                    String.valueOf(i + 2), "表中数据重复"));
                        }
                    }
                }
            }
        }
        // TISHKS-61:基准利率行情信息导入先校验数据是否合法,再转成bean fuyongsheng 20211108 edit end
        return checkResult;
    }
    // add by zwq 20210222  end


    // add by zwq 20210222  start
    /**
     * list转换为Bean
     *
     * @param dataList excel解析后的list
     * @see
     * @since 1.0
     */
    private List<ComInterestRateQuotationInfoDto> converToBean(List<List<String>> dataList)
            throws ParseException {
        // 返回的list定义
        List<ComInterestRateQuotationInfoDto> returnInfoList = new ArrayList<ComInterestRateQuotationInfoDto>();
        ComInterestRateQuotationInfoDto valInterestRateQuotationInfoDto = null;
        for (int i = 0; i < dataList.size(); i++ ) {
            List<String> list = dataList.get(i);
            valInterestRateQuotationInfoDto = new ComInterestRateQuotationInfoDto();
            if (StringUtil.isNotEmpty(list.get(0))) {
                valInterestRateQuotationInfoDto.setRelaeseDate(new SimpleDateFormat("yyyy/MM/dd").parse(list.get(0).trim()));
            }
            if (StringUtil.isNotEmpty(list.get(1))) {
                String intRateName = list.get(1).trim();
                String intRateId = restTemplate.postForObject(
                        ServiceApplication.CommonBasicDataService + "/ComInterestRateQuotationInfo/getIntRateId",
                        intRateName, String.class);
                valInterestRateQuotationInfoDto.setIntRateId(intRateId);
            }
            if (StringUtil.isNotEmpty(list.get(2))) {
                valInterestRateQuotationInfoDto.setCcy(list.get(2).trim().split(":")[0]);
            }
            if (StringUtil.isNotEmpty(list.get(3))) {
                BigDecimal tempIntRate = new BigDecimal(list.get(3).trim());
                valInterestRateQuotationInfoDto.setIntRate(tempIntRate.divide(new BigDecimal("100")));
            }
            if (StringUtil.isNotEmpty(list.get(4))) {
                valInterestRateQuotationInfoDto.setEffDate(new SimpleDateFormat("yyyy/MM/dd").parse(list.get(4).trim()));
            }
             Date date = new Date();
            valInterestRateQuotationInfoDto.setCreateUser(CommonUtil.getUserName());
            valInterestRateQuotationInfoDto.setCreateDept(CommonUtil.getDeptCode());
            valInterestRateQuotationInfoDto.setCreateTime(date);
            valInterestRateQuotationInfoDto.setUpdateUser(CommonUtil.getUserName());
            valInterestRateQuotationInfoDto.setUpdateTime(date);
            returnInfoList.add(valInterestRateQuotationInfoDto);
        }
        return returnInfoList;
    }
    // add by zwq 20210222  end

    // add by zwq 20210222  start
    /**
     * 检查excel是否为空或者传入列是否正确
     * @param i 第几行
     * @param list 每一列解析后的List
     * @return 检查结构
     * @see
     * @since 1.0
     */
    private List<ExcelValidDto> checkExcelCow(int i, List<String> list) {
        List<ExcelValidDto> excelValidDtolist = new ArrayList<ExcelValidDto>();
        // 列数为5
        if (null == list || list.size() != 5) {
            ExcelValidDto dto = new ExcelValidDto(StringUtil.EMPTY, String.valueOf(i+2), "数据列数错误");
            excelValidDtolist.add(dto);
        }else{
            if (StringUtil.isEmpty(list.get(0))) {
                // 验证导入发布日是否为空
                ExcelValidDto dto = new ExcelValidDto(list.get(0), String.valueOf(i+2), "发布日为空");
                excelValidDtolist.add(dto);
            }else{
                // 验证导入发布日格式是否正确
                if(ComBasicDataExcelUtil.isErrorDate(list.get(0))){
                    ExcelValidDto dto = new ExcelValidDto(list.get(0), String.valueOf(i+2), "日期格式错误");
                    excelValidDtolist.add(dto);
                }
            }
            if (StringUtil.isEmpty(list.get(1))) {
                // 验证导入利率名称是否为空
                ExcelValidDto dto = new ExcelValidDto(list.get(1), String.valueOf(i+2), "利率名称为空");
                excelValidDtolist.add(dto);
            }
            if (StringUtil.isEmpty(list.get(2))) {
                // 验证导入币种是否为空
                ExcelValidDto dto = new ExcelValidDto(list.get(2), String.valueOf(i+2), "币种为空");
                excelValidDtolist.add(dto);
            }
            if (StringUtil.isEmpty(list.get(3))) {
                // 验证导入利率值是否为空
                ExcelValidDto dto = new ExcelValidDto(list.get(3), String.valueOf(i+2), "利率值为空");
                excelValidDtolist.add(dto);
            }else{
                // TISHKS-61:增加利率值格式校验 fuyongsheng 20211112 edit start
                if(!checkFigure(list.get(3))){
                    // 验证导入利率值是否为数字
                    ExcelValidDto dto = new ExcelValidDto(list.get(3), String.valueOf(i+2), "利率值格式错误");
                    excelValidDtolist.add(dto);
                }
                // TISHKS-61:增加利率值格式校验 fuyongsheng 20211112 edit end
            }
            if (StringUtil.isEmpty(list.get(4))) {
                // 验证导入生效日是否为空
                ExcelValidDto dto = new ExcelValidDto(list.get(4), String.valueOf(i+2), "生效日为空");
                excelValidDtolist.add(dto);
            }else{
                // 验证导入生效日格式是否正确
                if(ComBasicDataExcelUtil.isErrorDate(list.get(4))){
                    ExcelValidDto dto = new ExcelValidDto(list.get(4), String.valueOf(i+2), "日期格式错误");
                    excelValidDtolist.add(dto);
                }
            }
        }
        return excelValidDtolist;
    }
    // add by zwq 20210222  end

    // add by zwq 20210222  start
    /**
     * check后的保存
     *
     * @param equityList 需要保存的数据
     * @see
     * @since 1.0
     */
    public void dealAfterCheck(List<ComInterestRateQuotationInfoDto> equityList) {
        restTemplate.postForObject(
                ServiceApplication.CommonBasicDataService + "/ComInterestRateQuotationInfo/mergeInfoList",
                equityList, Integer.class);
    }
    // add by zwq 20210222  end

    /**
     * 判断是否为数字
     *
     * @param
     * @return 返回boolean
     */
    private boolean checkFigure(String figure) {
        try{
            new BigDecimal(figure);
            return true;
        }catch(Exception e){
            return false;
        }
    }
}
