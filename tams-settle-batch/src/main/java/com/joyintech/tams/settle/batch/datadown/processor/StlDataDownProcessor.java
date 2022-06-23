package com.joyintech.tams.settle.batch.datadown.processor;

import com.joyintech.batch.batchbase.baseclass.BaseProcessor;
import com.joyintech.batch.batchbase.baseclass.entity.NeedOperateEntity;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.dbf.DBFRow;
import com.joyintech.tams.common.dbf.Field;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.settle.batch.datadown.dto.DataToWriteEntity;
import com.joyintech.tams.settle.batch.datadown.dto.DbfDataRow;
import com.joyintech.tams.settle.batch.datadown.dto.StepContext;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 何阳阳
 * @date 2020/7/2
 * @see StlDataDownProcessor
 * @since 1.0
 */
public abstract class StlDataDownProcessor<T> extends BaseProcessor<NeedOperateEntity, DataToWriteEntity> {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public DataToWriteEntity process(NeedOperateEntity item) throws Exception {
        DbfDataRow dataRow = (DbfDataRow)item;
        DBFRow rowData = dataRow.getRowData();
        StepContext stepContext = StepContext.get(getStepName());
        if(null != rowData) {
            Collection<Field> collection = rowData.values();
            if(CollectionUtils.isNotEmpty(collection)) {
                Map<String, Object> objectMap = new HashMap<>(collection.size());

                collection.forEach(d -> objectMap.put(StringUtil.lowerCase(d.getName()), d.getValue()));

                DataToWriteEntity writeEntity = new DataToWriteEntity();
                try {
                    T data = mapToEntity(objectMap);
                    if(null != data) {
                        BeanUtil.setProperty(data, "seqno", IDGenerator.generate());
                        BeanUtil.setProperty(data, "importDate", stepContext.getImportDate());
                        BeanUtil.setProperty(data, "dataFile", dataRow.getFileName()/*stepContext.getDataFile()*/);
                        BeanUtil.setProperty(data, "isSettleTarget", DictCodeList.是否_是.getValue());
                    }
                    writeEntity.setEntity(data);
                    return writeEntity;
                }
                catch (Exception e) {
                    logger.error("数据转换失败：{}", JsonUtils.object2Json(objectMap));
                    stepContext.errorIncrement(dataRow.getFileName());
//                    dataDownService.error(new LogDto("数据转换失败", writeEntity.getPortfolioId(), writeEntity.getTaskId(), writeEntity.getImportDate(), writeEntity.getDataFile(), objectMap));
                    throw e;
                }
            }
        }
        return null;
    }

    private String getStepName() {
        String className = getClass().getSimpleName();
        return StringUtil.replace(className, "Processor", StringUtils.EMPTY);
    }

    protected abstract T mapToEntity(Map<String, Object> rowData);
}
