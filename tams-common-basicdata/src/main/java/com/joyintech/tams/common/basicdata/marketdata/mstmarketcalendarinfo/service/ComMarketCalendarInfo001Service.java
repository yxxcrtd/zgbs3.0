/*
 * 文件名：mstMarketCalendarInfo001Service.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：xiexinting
 * 修改时间：2018年8月3日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.common.basicdata.marketdata.mstmarketcalendarinfo.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.joyintech.tams.common.basicdata.constant.ServiceApplication;
import com.joyintech.tams.common.basicdata.marketdata.mstmarketcalendarinfo.dto.ComCalendarInfoDto;
import com.joyintech.tams.common.basicdata.marketdata.mstmarketcalendarinfo.dto.ComMarketCalendarInfo001Dto;
import com.joyintech.tams.common.basicdata.util.ComBasicDataExcelUtil;
import com.joyintech.tams.systemtools.dto.ExcelValidDto;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.export.ExportParam;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.constant.DictCodeList;

/**
 * 市场日历信息service层 〈功能详细描述〉
 * 
 * @author xiexinting
 * @version 1.0 2018年8月20日
 * @see ComMarketCalendarInfo001Service
 * @since 1.0
 */
@Service
public class ComMarketCalendarInfo001Service extends CommonExportService<ComMarketCalendarInfo001Dto> {

    /**
     * 市场日历信息查询 <功能详细描述>
     * 
     * @param param 查询参数
     * @return 查询结果
     * @see
     * @since 1.0
     */
    public Page<ComMarketCalendarInfo001Dto> selectInfo(Map<String, Object> param) {
        if (StringUtil.equals(param.get("ishDay"), DictCodeList.是否_是.getValue())) {
            param.put("ishDay", 1);
        }
        else if (StringUtil.equals(param.get("ishDay"), DictCodeList.是否_否.getValue())) {
            param.put("ishDay", 0);
        }
        String calendarType = (String)param.get("calendarType");
        if (StringUtil.isNotEmpty(calendarType)) {
            param.put("calendarTypeList", StringUtil.split(calendarType, GlobalConstant.COMMA));
        }
        return restTemplate.postForPage(
            ServiceApplication.CommonBasicDataService + "/ComMarketCalendarInfo/selectInfo", param,
            ComMarketCalendarInfo001Dto.class);
    }

    /**
     * 市场日历类型下拉框查询 <功能详细描述>
     * 
     * @param map 参数
     * @return 返回结果
     * @see
     * @since 1.0
     */
    public List<ComCalendarInfoDto> selectCalendarInfo(Map<String, Object> map) {
        map.put("no", DictCodeList.是否_否.getValue());
        return restTemplate.postForList(ServiceApplication.CommonBasicDataService
                                        + "/ComMarketCalendarInfo/selectCalendarInfo",
            map, ComCalendarInfoDto.class);
    }

    /**
     * 修改日历状态 <功能详细描述>
     * 
     * @param entity 实体类
     * @see
     * @since 1.0
     */
    public void doComplete(ComMarketCalendarInfo001Dto entity) {
        restTemplate.postForObject(
            ServiceApplication.CommonBasicDataService + "/ComMarketCalendarInfo/doComplete",
            entity, Integer.class);
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
            // 检查文件名是否包含市场日历信息导入
            if (flieName.indexOf("市场日历信息导入") != -1) {
//                ComBasicDataExcelUtil excelUtil = ComBasicDataExcelUtil.build(FileNameUtil.getUploadFilePath(flieName),
//                    null);
//                List<List<String>> dataList = excelUtil.readToList(0, 1, 0);
                //TODO 此处需要确认下有没有问题
                List<List<String>> dataList = ComBasicDataExcelUtil.getDataList(flieName);
                // 检查读出信息是否为空
                if (null != dataList && dataList.size() > 0) {
                    // 将excel表格中的数据转化为dto
                    List<ExcelValidDto> checkResult = checkExcelData(dataList);
                    if (checkResult.isEmpty()) {
                        List<ComMarketCalendarInfo001Dto> equityList = converToBean(dataList);
                        dealAfterCheck(equityList);
                    }
                    return checkResult;
                }
//                else {
//                    List<ExcelValidDto> checkResult = new ArrayList<>();
//                    checkResult.add(new ExcelValidDto(StringUtil.EMPTY, StringUtil.EMPTY,
//                        "导入文件为空"));
//                    return checkResult;
//                }
            }
            else {
                List<ExcelValidDto> checkResult = new ArrayList<>();
                checkResult.add(new ExcelValidDto(StringUtil.EMPTY, StringUtil.EMPTY,
                    getMessage("BUSS_COM_E_0002", "市场日历信息导入")));
                return checkResult;
            }
        }
        return null;
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
        // TISHKS-4：先校验导入数据格式，再转bean fuyongsheng 20211108 edit start
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
        List<ComMarketCalendarInfo001Dto> equityList = converToBean(dataList);
        ComMarketCalendarInfo001Dto comMarketCalendarInfo001Dto = null;
        // 检查表中是否有数据
        if (equityList.size() >= 1) {
            for (int i = 0; i < equityList.size(); i++ ) {
                comMarketCalendarInfo001Dto = equityList.get(i);
//                // 检查表内数据每行是否合法
//                if (checkExcelCow(i, dataList.get(i)) != null) {
//                    checkResult.addAll(checkExcelCow(i, dataList.get(i)));
//                }
                // 检查表内数据是否有主键重复
                if (equityList.size() >= 2) {
                    for (int k = i + 1; k < equityList.size(); k++ ) {

                        if (equityList.get(k).getCalendarId().equals(
                            comMarketCalendarInfo001Dto.getCalendarId())
                            && equityList.get(k).getCdate().equals(
                                comMarketCalendarInfo001Dto.getCdate())) {
                            checkResult.add(
                                new ExcelValidDto(dataList.get(i).get(1), String.valueOf(i + 2),
                                    getMessage("ComMarketCalendarInfo001.repeat")));
                        }
                    }
                }
            }
        }
        // TISHKS-4：先校验导入数据格式，再转bean fuyongsheng 20211108 edit end
        return checkResult;
    }

    /**
     * 导出
     */
    @Override
    protected void buildExportParam(ExportParam exportParam) {
        exportParam.config(
                "/com/joyintech/tams/common/basicdata/marketdata/mstmarketcalendarinfo/conf/export_conf.xml").dataSource(
                DataSourceType.MASERT);
    }

    /**
     * 给单元格设定值
     */
    @Override
    public void setCellValue(String beanName, Cell cell, String propName, Object value) {
        if (StringUtil.equals(propName, "cdate")) {
            cell.setCellValue(DateUtil.format((Date)value));
        }
//        if (StringUtil.equals(propName, "isHoliday")) {
//            if ((long)value == 1) {
//                cell.setCellValue("1 节假日");
//            }
//            if ((long)value == 0) {
//                cell.setCellValue("0 工作日");
//            }
//        }
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
    private List<ComMarketCalendarInfo001Dto> converToBean(List<List<String>> dataList)
        throws ParseException {
        // 返回的list定义
        List<ComMarketCalendarInfo001Dto> returnInfoList = new ArrayList<ComMarketCalendarInfo001Dto>();
        ComMarketCalendarInfo001Dto comMarketCalendarInfo001Dto = null;
        for (int i = 0; i < dataList.size(); i++ ) {
            List<String> list = dataList.get(i);
            comMarketCalendarInfo001Dto = new ComMarketCalendarInfo001Dto();
            comMarketCalendarInfo001Dto.setCdate(DateUtil.parse(list.get(0)));
            comMarketCalendarInfo001Dto.setCalendarId(list.get(1).split(" ")[0]);
            comMarketCalendarInfo001Dto.setIsHoliday(Long.parseLong(list.get(2).split(" ")[0]));
            returnInfoList.add(comMarketCalendarInfo001Dto);
        }
        return returnInfoList;
    }

    /**
     * check后的保存 <功能详细描述>
     * 
     * @param equityList 需要保存的数据
     * @see
     * @since 1.0
     */
    public void dealAfterCheck(List<ComMarketCalendarInfo001Dto> equityList) {
        List<ComMarketCalendarInfo001Dto> listN = new ArrayList<ComMarketCalendarInfo001Dto>();
        List<ComMarketCalendarInfo001Dto> listA = new ArrayList<ComMarketCalendarInfo001Dto>();
        for (int i = 0; i < equityList.size(); i++ ) {
            ComMarketCalendarInfo001Dto comMarketCalendarInfo001Dto = equityList.get(i);
            ComMarketCalendarInfo001Dto equityInfoCheck = getComMarketCalendarInfo001DtoByKey(
                    comMarketCalendarInfo001Dto);
            if (equityInfoCheck == null) {
                listN.add(comMarketCalendarInfo001Dto);
            }
            else {
                listA.add(comMarketCalendarInfo001Dto);
            }

        }
        if (listN != null && listN.size() > 0) {
            this.insertBtachEquityInfoByPart(listN);
        }
        if (listA != null && listA.size() > 0) {
            this.updateBtachEquityInfoByPart(listA);
        }
    }

    /**
     * 根据key获取dto <功能详细描述>
     * 
     * @param dto key
     * @return 数据
     * @see
     * @since 1.0
     */
    private ComMarketCalendarInfo001Dto getComMarketCalendarInfo001DtoByKey(ComMarketCalendarInfo001Dto dto) {
        return restTemplate.postForObject(ServiceApplication.CommonBasicDataService
                                          + "/ComMarketCalendarInfo/getMstMarketCalendarInfo001DtoByKey",
            dto, ComMarketCalendarInfo001Dto.class);
    }

    /**
     * 插入数据 <功能详细描述>
     * 
     * @param list 参数
     * @see
     * @since 1.0
     */
    private void insertBtachEquityInfo(List<ComMarketCalendarInfo001Dto> list) {
        restTemplate.postForObject(ServiceApplication.CommonBasicDataService
                                   + "/ComMarketCalendarInfo/insertBtachEquityInfo",
            list, Integer.class);
    }

    /**
     * 更新数据 <功能详细描述>
     * 
     * @param list 参数
     * @see
     * @since 1.0
     */
    private void updateBtachEquityInfo(List<ComMarketCalendarInfo001Dto> list) {
        restTemplate.postForObject(ServiceApplication.CommonBasicDataService
                                   + "/ComMarketCalendarInfo/updateBtachEquityInfo",
            list, Integer.class);
    }

    /**
     * 分批次执行insertBtachEquityInfo
     *
     * @param dataList 数据
     * @see
     * @since 1.0
     */
    private void insertBtachEquityInfoByPart(List<ComMarketCalendarInfo001Dto> dataList) {
        if (StringUtil.isEmptyList(dataList)) {
            return;
        }
        else {

            int maxDataCountOneTime = 20;
            int dataCount = dataList.size();
            if (dataCount <= maxDataCountOneTime) {
                insertBtachEquityInfo(dataList);
            }
            else {
                List<ComMarketCalendarInfo001Dto> newDataList = new ArrayList<ComMarketCalendarInfo001Dto>();
                newDataList.addAll(dataList);

                // 分批处理
                int part = dataCount / maxDataCountOneTime; // 分批数
                for (int i = 0; i < part; i++ ) {
                    insertBtachEquityInfo(newDataList.subList(0, maxDataCountOneTime));
                    newDataList.subList(0, maxDataCountOneTime).clear();
                }
                // 最后剩下的数据
                if (CollectionUtils.isNotEmpty(newDataList)) {
                    insertBtachEquityInfo(newDataList);
                }
            }
        }
    }

    /**
     * 分批次执行updateBtachEquityInfo
     *
     * @param dataList 数据
     * @see
     * @since 1.0
     */
    private void updateBtachEquityInfoByPart(List<ComMarketCalendarInfo001Dto> dataList) {
        if (StringUtil.isEmptyList(dataList)) {
            return;
        }

        int maxDataCountOneTime = 20;
        int dataCount = dataList.size();
        if (dataCount <= maxDataCountOneTime) {
            updateBtachEquityInfo(dataList);
        }
        else {

            List<ComMarketCalendarInfo001Dto> newDataList = new ArrayList<ComMarketCalendarInfo001Dto>();
            newDataList.addAll(dataList);

            // 分批处理
            int part = dataCount / maxDataCountOneTime; // 分批数
            for (int i = 0; i < part; i++ ) {
                updateBtachEquityInfo(newDataList.subList(0, maxDataCountOneTime));
                newDataList.subList(0, maxDataCountOneTime).clear();
            }
            // 最后剩下的数据
            if (CollectionUtils.isNotEmpty(newDataList)) {
                updateBtachEquityInfo(newDataList);
            }
        }
    }

    /**
     * 检查excel是否为空或者传入列是否正确
     *
     * @param i 第几行
     * @param list 每一列解析后的List<String>
     * @return 检查结构
     * @see
     * @since 1.0
     */
    private List<ExcelValidDto> checkExcelCow(int i, List<String> list) {
        List<ExcelValidDto> excelValidDtolist = new ArrayList<ExcelValidDto>();
        // 删除数据列 SPV 登记编码
        // 列数变为3
        if (null == list || list.size() != 3) {
            ExcelValidDto dto = new ExcelValidDto(StringUtil.EMPTY, String.valueOf(i+2),
                this.getMessage("COM_INFO_E_0001"));
            excelValidDtolist.add(dto);
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        Date date = null;
        // 使用日期格式化捕获异常来判断日期格式是否合法
        try {
            date = list==null?null:sdf.parse(list.get(0));
            // 获取平台日期
            Date sysDate = DateUtil.platDateFromCache();
            if(date != null){
                if (date.before(sysDate)) {
                    // 验证导入数据日期是否小于平台日期
                    ExcelValidDto dto = new ExcelValidDto(list.get(0), String.valueOf(i+2),
                            this.getMessage("BUSS_COM_E_0003", "平台日期", "导入数据的日期"));
                    excelValidDtolist.add(dto);
                }
            }
            // 验证节假日类型是否存在
            // Map<String, Object> parm = new HashMap<>();
            // parm.put("calendarType", list.get(1).split(" ")[0]);
            // if (selectCalendarInfo(parm).size() == 0) {
            // ExcelValidDto dto = new ExcelValidDto(list.get(1), String.valueOf(i),
            // this.getMessage("COM_INFO_E_0001"));
            // excelValidDtolist.add(dto);
            // }
        }
        catch (ParseException e) {
            ExcelValidDto dto = new ExcelValidDto(list.get(0), String.valueOf(i+2),
                this.getMessage("COM_INFO_E_0001"));
            excelValidDtolist.add(dto);
        }
        return excelValidDtolist;
    }

    /**
     * 导出 {@inheritDoc}
     */
    @Override
    public Page<ComMarketCalendarInfo001Dto> selectPageData(Map<String, Object> selectParam) {
        return selectInfo(selectParam);
    }
}
