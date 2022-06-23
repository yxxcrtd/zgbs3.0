package com.joyintech.tams.settle.batch.datadown.reader;

import com.joyintech.batch.batchbase.baseclass.BaseReader;
import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.SettleTransPara;
import com.joyintech.tams.common.dbf.DBFReader;
import com.joyintech.tams.common.dbf.DBFRow;
import com.joyintech.tams.settle.batch.datadown.dto.DbfDataRow;
import com.joyintech.tams.settle.batch.datadown.dto.StepContext;
import com.joyintech.tams.settle.batch.datadown.service.StlDataDownService;
import com.joyintech.tools.collectors.JoyinCollectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 何阳阳
 * @date 2020/7/2
 * @see StlDataDownReader
 * @since 1.0
 */
public abstract class StlDataDownReader extends BaseReader<DbfDataRow> {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private StlDataDownService dataDownService;

    @Override
    public List<DbfDataRow> createList() {
        StepContext stepContext = StepContext.get(getStepName());
        dataDownService.loadAllBondSeatPtlMapping();
        // 批处理执行信息
        SysBatchExecInfo execInfo = getBatchExecuteInfo();

        // 此处查询所有导入参数，前台执行导入时，需要校验当导入参数表不存在数据时才允许导入
        List<SettleTransPara> paramList = dataDownService.selectImportParam(execInfo.getExecuteDate(), execInfo.getTaskId());
        // 汇总导入参数，确保一个数据文件只解析一次
        Map<String, List<SettleTransPara>> mapList = paramList.stream()
                .collect(JoyinCollectors.groupingBy(SettleTransPara::getDataFileName));
        // 解析数据文件
        List<DbfDataRow> result = new ArrayList<>();
        mapList.forEach((key, value) -> {
            if(canProcess(key) && value.size() > 0) {
                result.addAll(convertFile(key, value, stepContext));
            }
        });

        stepContext.setTaskId(execInfo.getTaskId());
        stepContext.setImportDate(execInfo.getExecuteDate());
        return result;
    }

    /**
     * 数据文件解析
     *
     * @param fileName 文件名
     * @param param    解析参数
     * @return 解析结果
     */
    private List<DbfDataRow> convertFile(String fileName, List<SettleTransPara> param, StepContext stepContext) {
        stepContext.initLogDto(fileName);
        Date importDate = param.get(0).getImportDate();
        // 清空导入参数
        dataDownService.clearImportParam(param);
        // 组合代码集合
        Set<String> portfolioIdList = param.stream()
                .filter(item -> StringUtil.isNotEmpty(item.getPortfolioId()))
                .map(SettleTransPara::getPortfolioId).collect(Collectors.toSet());
        // 删除临时表相关数据
        removeTempData(importDate, fileName, portfolioIdList);
        // 待解析的数据文件
        File file = getFile(DateUtil.format(importDate, DateUtil.DEFAULT_OTHER_FORMAT) + File.separator + fileName);
        if(null == file) {
            // 数据文件没有读入，则添加一条警告
            stepContext.warningIncrement(fileName);
            return Collections.emptyList();
        }
        DBFReader reader = DBFReader.build(file);
        if(CollectionUtils.isNotEmpty(portfolioIdList)) {
            reader.filter(row -> {
                List<String> ptlId = dataDownService.createPortfolioId(row);
                return null != ptlId && CollectionUtils.containsAny(portfolioIdList, ptlId);
            });
        }
        List<DBFRow> list = reader.charset(Charset.forName("GB18030")).read();
        stepContext.setData(fileName, list);
        if(CollectionUtils.isNotEmpty(portfolioIdList)) {
            stepContext.setPortfolioId(portfolioIdList);
            logger.info("文件{}读入完成，共读取到组合代码[{}]对应的{}条数据.",fileName,StringUtil.join(portfolioIdList.toArray(), ','),list.size());
//            dataDownService.info(String.format("文件%s读入完成，共读取到组合代码[%s]对应的%d条数据."
//                    ,fileName,StringUtil.join(portfolioIdList.toArray(), ','),list.size()));
        }
        else {
            logger.info("文件{}读入完成，共读取到{}条数据.",fileName,list.size());
//            dataDownService.info("文件"+fileName+"读入完成，共读取到"+list.size()+"条数据.");
        }
        return list
                .stream()
                .map(data -> new DbfDataRow(data, fileName))
                .collect(Collectors.toList());
    }

    private File getFile(String fileName) {
        File file = Paths.get(CacheUtil.getSysParam("STL_DATA_DOWN_FILE_PATH"), fileName).toFile();
        if(!file.exists()) {
            logger.warn("导入日期[{}]，的数据文件[{}]未读入", DateUtil.format(getBatchExecuteInfo().getExecuteDate()), fileName);
//            dataDownService.warn(new LogDto("数据文件未读入", null, getBatchExecuteInfo().getTaskId(), getBatchExecuteInfo().getExecuteDate(), fileName, null));
            return null;
        }
        return file;
    }

    private String getStepName() {
        String className = getClass().getSimpleName();
        return StringUtil.replace(className, "Reader", StringUtils.EMPTY);
    }

    /**
     * 处理的文件类型
     * @return
     */
    protected abstract boolean canProcess(String fileName);

    /**
     * 删除临时数据
     * @return
     */
    protected abstract int removeTempData(Date importDate, String dataFile, Set<String> portfolioIdList);

}
