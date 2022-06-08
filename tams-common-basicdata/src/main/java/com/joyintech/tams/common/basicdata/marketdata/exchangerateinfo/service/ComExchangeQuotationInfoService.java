/*
 * 文件名：MstYieldCurveInfoService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：qiangchengxi
 * 修改时间：2018年8月8日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.common.basicdata.marketdata.exchangerateinfo.service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joyintech.tams.common.basicdata.constant.ServiceApplication;
import com.joyintech.tams.common.basicdata.marketdata.exchangerateinfo.dto.ComExchangeQuotationInfoDto;
import com.joyintech.tams.common.basicdata.util.ComBasicDataExcelUtil;
import com.joyintech.tams.systemtools.dto.ExcelValidDto;
import org.apache.poi.ss.usermodel.Cell;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
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
 * 汇率行情service 
 * 
 * @author wangzhao
 * @version 1.0 2018年8月8日
 * @see ComExchangeQuotationInfoService
 * @since 1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ComExchangeQuotationInfoService extends CommonExportService<ComExchangeQuotationInfoDto>
    implements BaseProcessService<ComExchangeQuotationInfoDto> {

    /**
     * 汇率行情分页查询 <功能详细描述>
     * 
     * @param pageParam 查询参数
     * @return 返回查询结果
     * @throws Exception 抛出异常
     * @see
     * @since 1.0
     */
    public Page<ComExchangeQuotationInfoDto> selectForPage(Map<String, Object> pageParam){
        return restTemplate.postForPage(
            ServiceApplication.CommonBasicDataService + "/ComExchangeQuotationInfo/selectForPage",
            pageParam, ComExchangeQuotationInfoDto.class);
    }

    /**
     * 查询所有数据验证删除是否存在 <功能详细描述>
     * 
     * @param pageParam 查询参数
     * @return 查询结果
     * @see
     * @since 1.0
     */
    public List<ComExchangeQuotationInfoDto> selectAllList(Map<String, Object> pageParam) {
        return restTemplate.postForList(
            ServiceApplication.CommonBasicDataService + "/ComExchangeQuotationInfo/selectAllList",
            pageParam, ComExchangeQuotationInfoDto.class);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void doComplete(ComExchangeQuotationInfoDto entity, OperationType type) {
        entity.setOperateType(type.toString());
        restTemplate.postForObject(
            ServiceApplication.CommonBasicDataService + "/ComExchangeQuotationInfo/doComplete",
            entity, Integer.class);
    }

    /**
     * 导出
     */
    @Override
    public void buildExportParam(ExportParam exportParam) {
        exportParam.config(
                "/com/joyintech/tams/common/basicdata/marketdata/exchangerateinfo/conf/export_conf.xml").dataSource(DataSourceType.MASERT);
    }
    @Override
    public Page<ComExchangeQuotationInfoDto> selectPageData(Map<String, Object> selectParam) {
        return selectForPage(selectParam);
    }
    /**
     * 设置导出单元格
     */
    @Override
    public void setCellValue(String beanName, Cell cell, String propName, Object value) {
        if (StringUtil.equals(propName, "tradeDate")) {
            cell.setCellValue(DateUtil.format((Date)value));
        }
    }


    // add by zwq 20210222  start
    // 添加批量导入功能
    @Transactional(rollbackFor = Exception.class)
    public List<ExcelValidDto> importData(String flieName)
            throws Exception {
        // 检查文件名是否为空
        if (StringUtil.isNotEmpty(flieName)) {
            if (flieName.indexOf("基准汇率行情") != -1) {
                ExcelUtil excelUtil = ExcelUtil.build(FileNameUtil.getUploadFilePath(flieName),
                        null);
                List<List<String>> dataList = excelUtil.readToList(0, 1, 0);
                if (null != dataList && dataList.size() > 0) {
                    // 将excel表格中的数据转化为dto
                    List<ExcelValidDto> checkResult = checkExcelData(dataList);
                    if (checkResult.isEmpty()) {
                        List<ComExchangeQuotationInfoDto> equityList = converToBean(dataList);
                        // 保存，已存在则更新
                        dealAfterCheck(equityList);
                    }
                    return checkResult;
                }
            }
            else {
                List<ExcelValidDto> checkResult = new ArrayList<>();
                checkResult.add(new ExcelValidDto(StringUtil.EMPTY, StringUtil.EMPTY,
                        getMessage("文件名称错误，请下载模板后上传")));
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
        List<ComExchangeQuotationInfoDto> equityList = converToBean(dataList);
        ComExchangeQuotationInfoDto comExchangeQuotationInfoDto = null;
        if (equityList.size() >= 1) {
            for (int i = 0; i < equityList.size(); i++ ) {
                comExchangeQuotationInfoDto = equityList.get(i);
                // 检查表内数据每行是否合法
                if (checkExcelCow(i, dataList.get(i)) != null) {
                    checkResult.addAll(checkExcelCow(i, dataList.get(i)));
                }
                // 检查表内数据是否有主键重复
                if (equityList.size() >= 2) {
                    for (int k = i + 1; k < equityList.size(); k++ ) {
                        if (equityList.get(k).getExchargeCode().equals(comExchangeQuotationInfoDto.getExchargeCode())
                                &&equityList.get(k).getTradeDate().equals(comExchangeQuotationInfoDto.getTradeDate())){
                            checkResult.add(new ExcelValidDto(dataList.get(i).get(1),
                                    String.valueOf(i + 2), "表中数据重复"));
                        }
                    }
                }
            }
        }
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
    private List<ComExchangeQuotationInfoDto> converToBean(List<List<String>> dataList)
            throws ParseException {
        // 返回的list定义
        List<ComExchangeQuotationInfoDto> returnInfoList = new ArrayList<ComExchangeQuotationInfoDto>();
        ComExchangeQuotationInfoDto comExchangeQuotationInfoDto = null;
        for (int i = 0; i < dataList.size(); i++ ) {
            List<String> list = dataList.get(i);
            comExchangeQuotationInfoDto = new ComExchangeQuotationInfoDto();
            if (StringUtil.isNotEmpty(list.get(0))) {
                comExchangeQuotationInfoDto.setExchargeCode(list.get(0).trim());
            }
            if (StringUtil.isNotEmpty(list.get(1))) {
                comExchangeQuotationInfoDto.setTradeDate(new SimpleDateFormat("yyyy/MM/dd").parse(list.get(1).trim()));
            }
            if (StringUtil.isNotEmpty(list.get(2))) {
                comExchangeQuotationInfoDto.setOpenPrice(new BigDecimal(list.get(2).trim()));
            }
            if (StringUtil.isNotEmpty(list.get(3))) {
                comExchangeQuotationInfoDto.setClosePrice(new BigDecimal(list.get(3).trim()));
            }
            if (StringUtil.isNotEmpty(list.get(4))) {
                comExchangeQuotationInfoDto.setMidPrice(new BigDecimal(list.get(4).trim()));
            }
            if (StringUtil.isNotEmpty(list.get(5))) {
                comExchangeQuotationInfoDto.setHighPrice(new BigDecimal(list.get(5).trim()));
            }
            if (StringUtil.isNotEmpty(list.get(6))) {
                comExchangeQuotationInfoDto.setLowPrice(new BigDecimal(list.get(6).trim()));
            }
            if (StringUtil.isNotEmpty(list.get(7))) {
                comExchangeQuotationInfoDto.setQuotUnit(new Long(list.get(7).trim()));
            }
            if (StringUtil.isNotEmpty(list.get(8))) {
                comExchangeQuotationInfoDto.setDatasourceType(list.get(8).trim().split(":")[0]);
            }
            if (StringUtil.isNotEmpty(list.get(9))) {
                comExchangeQuotationInfoDto.setMarketType(list.get(9).trim().split(":")[0]);
            }
            if (StringUtil.isNotEmpty(list.get(10))) {
                comExchangeQuotationInfoDto.setDeadLine(list.get(10).trim().split(":")[0]);
            }
            comExchangeQuotationInfoDto.setCreateTime(new Date());
            comExchangeQuotationInfoDto.setCreateUser(CommonUtil.getUserName());
            comExchangeQuotationInfoDto.setCreateDept(CommonUtil.getDeptCode());
            comExchangeQuotationInfoDto.setUpdateUser(CommonUtil.getUserName());
            returnInfoList.add(comExchangeQuotationInfoDto);
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
        // 列数为9
        if (null == list || list.size() != 11) {
            ExcelValidDto dto = new ExcelValidDto(StringUtil.EMPTY, String.valueOf(i+2), "数据列数错误");
            excelValidDtolist.add(dto);
        }else{
            if (StringUtil.isEmpty(list.get(0))) {
                // 验证导入货币对是否为空
                ExcelValidDto dto = new ExcelValidDto(list.get(0), String.valueOf(i+2), "货币对为空");
                excelValidDtolist.add(dto);
            }
            if (StringUtil.isEmpty(list.get(1))) {
                // 验证导入交易日期是否为空
                ExcelValidDto dto = new ExcelValidDto(list.get(1), String.valueOf(i+2), "交易日期为空");
                excelValidDtolist.add(dto);
            }
            if (StringUtil.isEmpty(list.get(2))) {
                // 验证导入开盘价（元）是否为空
                ExcelValidDto dto = new ExcelValidDto(list.get(2), String.valueOf(i+2), "开盘价（元）为空");
                excelValidDtolist.add(dto);
            }
            if (StringUtil.isEmpty(list.get(3))) {
                // 验证导入收盘价（元）是否为空
                ExcelValidDto dto = new ExcelValidDto(list.get(3), String.valueOf(i+2), "收盘价（元）为空");
                excelValidDtolist.add(dto);
            }
            if (StringUtil.isEmpty(list.get(4))) {
                // 验证导入中间价（元）是否为空
                ExcelValidDto dto = new ExcelValidDto(list.get(4), String.valueOf(i+2), "中间价（元）为空");
                excelValidDtolist.add(dto);
            }
            if (StringUtil.isEmpty(list.get(5))) {
                // 验证导入最高价（元）是否为空
                ExcelValidDto dto = new ExcelValidDto(list.get(5), String.valueOf(i+2), "最高价（元）为空");
                excelValidDtolist.add(dto);
            }
            if (StringUtil.isEmpty(list.get(6))) {
                // 验证导入最低价（元）是否为空
                ExcelValidDto dto = new ExcelValidDto(list.get(6), String.valueOf(i+2), "最低价（元）为空");
                excelValidDtolist.add(dto);
            }
            if (StringUtil.isEmpty(list.get(7))) {
                // 验证导入报价单位是否为空
                ExcelValidDto dto = new ExcelValidDto(list.get(7), String.valueOf(i+2), "报价单位为空");
                excelValidDtolist.add(dto);
            }
            if (StringUtil.isEmpty(list.get(8))) {
                // 验证导入数据来源是否为空
                ExcelValidDto dto = new ExcelValidDto(list.get(8), String.valueOf(i+2), "数据来源为空");
                excelValidDtolist.add(dto);
            }
            if (StringUtil.isEmpty(list.get(9))) {
                // 验证导入行情类型是否为空
                ExcelValidDto dto = new ExcelValidDto(list.get(9), String.valueOf(i+2), "行情类型为空");
                excelValidDtolist.add(dto);
            }
            /** modify by yanya on 20210326 去除期限校验**/
//        if (StringUtil.isEmpty(list.get(10))) {
//            // 验证导入期限是否为空
//            ExcelValidDto dto = new ExcelValidDto(list.get(10), String.valueOf(i+2), "期限为空");
//            excelValidDtolist.add(dto);
//        }
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
    public void dealAfterCheck(List<ComExchangeQuotationInfoDto> equityList) {
        restTemplate.postForObject(
                ServiceApplication.CommonBasicDataService + "/ComExchangeQuotationInfo/mergeInfoList",
                equityList, Integer.class);
    }
    // add by zwq 20210222  end
}
