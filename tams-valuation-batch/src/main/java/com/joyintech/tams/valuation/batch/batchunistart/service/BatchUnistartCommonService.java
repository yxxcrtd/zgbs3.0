package com.joyintech.tams.valuation.batch.batchunistart.service;

import com.joyintech.framework.base.BaseDictCodeList;
import com.joyintech.framework.base.dao.SysBatchGrpMapper;
import com.joyintech.framework.base.dao.SysBatchTaskMapper;
import com.joyintech.framework.base.dao.SysBatchgrpExecInfoMapper;
import com.joyintech.framework.base.entity.*;
import com.joyintech.framework.constant.BatchDictCodeList;
import com.joyintech.framework.jdbc.datasource.DataSource;
import com.joyintech.framework.jdbc.datasource.DynamicDataSourceHolder;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.system.base.dao.SysParaMapper;
import com.joyintech.system.base.entity.SysPara;
import com.joyintech.system.base.entity.SysParaKey;
import com.joyintech.tams.common.base.dao.SysBatchUnistartDetailMapper;
import com.joyintech.tams.common.base.dao.SysBatchUnistartMapper;
import com.joyintech.tams.common.base.entity.SysBatchUnistart;
import com.joyintech.tams.common.base.entity.SysBatchUnistartDetail;
import com.joyintech.tams.valuation.batch.batchdispatch.dao.BatchDispatchMapper;
import com.joyintech.tams.valuation.batch.batchdispatch.dto.BatchGrpPri;
import com.joyintech.tams.valuation.batch.batchdispatch.dto.DimensionValue;
import com.joyintech.tams.valuation.batch.batchunistart.dao.BatchUnistartMapper;
import com.joyintech.tams.valuation.batch.batchunistart.entity.BatchUnistartGrpconfig;
import com.joyintech.tools.collectors.JoyinCollectors;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Scope("prototype")
public class BatchUnistartCommonService {

    @Autowired
    private BatchUnistartMapper batchUnistartMapper;

    @Autowired
    private SysBatchgrpExecInfoMapper sysBatchgrpExecInfoMapper;

    @Autowired
    private SysBatchGrpMapper sysBatchGrpMapper;

    @Autowired
    private BatchDispatchMapper batchDispatchMapper;

    @Autowired
    private SysBatchUnistartDetailMapper sysBatchUnistartDetailMapper;

    @Autowired
    private SysBatchUnistartMapper sysBatchUnistartMapper;

    @Autowired
    private SysParaMapper sysParaMapper;

    @Autowired
    private SysBatchTaskMapper sysBatchTaskMapper;

    private static String START = "START";

    private static String END = "END";
    /**
     * 获取批处理日志
     * @param params
     * @return
     */
    public List<SysBatchExecInfo> getTaskLog(Map<String, Object> params) {
        DynamicDataSourceHolder.putDataSource(DataSourceType.OTHER);
        List<SysBatchExecInfo> list = batchUnistartMapper.getBatchTaskLogInfo(params);
        DynamicDataSourceHolder.clearDataSource();
        return list;
    }

    /**
     * List转换Map
     * @param list
     * @return
     */
    private Map<String, List<SysBatchExecInfo>> getLogMap(List<SysBatchExecInfo> list){
        Map<String, List<SysBatchExecInfo>> map = list.stream().collect(
            JoyinCollectors.groupingBy(x -> x.getTaskStatus()));
        return map;
    }

    /**
     * 获取批处理明细日志
     * @param params
     * @return
     */
    public List<SysBatchExecInfo> getTaskDetailLog(Map<String, Object> params) {
        DynamicDataSourceHolder.putDataSource(DataSourceType.OTHER);
        List<SysBatchExecInfo> taskLogList = batchUnistartMapper.getBatchTaskDetailLogInfo(params);
        DynamicDataSourceHolder.clearDataSource();
        return taskLogList;
    }

    /**
     * 判断批处理是否结束或异常
     * @param params(seqNo,detailSeqNo,orderNo)
     * @param logCount
     * @return
     */
    public boolean checkTaskLog(SysBatchUnistartDetail sysBatchUnistartDetail, int logCount){

        Map<String, Object> params = new HashMap<>();
        params.put("seqNo", sysBatchUnistartDetail.getSeqNo());
        params.put("detailSeqNo", sysBatchUnistartDetail.getDetailSeqNo());
        params.put("orderNo", sysBatchUnistartDetail.getOrderNo());

        // 查询当前任务的日志
        Map<String, List<SysBatchExecInfo>> taskLogMap = this.getLogMap(this.getTaskLog(params));

        // 判断是否结束
        if (taskLogMap.containsKey(BatchDictCodeList.批处理执行状态_正常结束.getValue())
            && taskLogMap.get(BatchDictCodeList.批处理执行状态_正常结束.getValue()).size() == logCount) {
            // 查询当前任务的日志
            Map<String, List<SysBatchExecInfo>> taskDetailLogMap = this.getLogMap(this.getTaskDetailLog(params));

            // 判断是否异常
            if (taskDetailLogMap.containsKey(BatchDictCodeList.批处理执行状态_异常结束.getValue())
                && taskDetailLogMap.get(BatchDictCodeList.批处理执行状态_异常结束.getValue()).size() > 0) {
                updateBatchUnistartDetail(sysBatchUnistartDetail, BatchDictCodeList.批处理执行状态_异常结束.getValue());
            } else {
                // 全部执行成功
                updateBatchUnistartDetail(sysBatchUnistartDetail, BatchDictCodeList.批处理执行状态_正常结束.getValue());
            }
            // 批处理已结束
            return true;
        }

        // 批处理未结束
        return false;
    }

    /**
     * 批处理执行信息入库
     * @param detail
     */
    public void insertBatchgrpExecInfo(SysBatchgrpExecInfo detail) {
        DynamicDataSourceHolder.putDataSource(DataSourceType.OTHER);
        sysBatchgrpExecInfoMapper.insert(detail);
        DynamicDataSourceHolder.clearDataSource();
    }

    /**
     * 批处理参数入库
     * @param batchGroup
     * @param jobList
     * @param batchGrpPriList
     * @param dimValueList
     */
    public void insertBatchConfig(SysBatchGrp batchGroup, List<SysBatchJob> jobList,
                                  List<BatchGrpPri> batchGrpPriList,
                                  List<DimensionValue> dimValueList) {
        DynamicDataSourceHolder.putDataSource(DataSourceType.OTHER);
        if (batchGroup != null) {
            sysBatchGrpMapper.insert(batchGroup);
        }
        if (CollectionUtils.isNotEmpty(jobList)) {
            batchDispatchMapper.insertBatchJob(jobList);
        }
        if (CollectionUtils.isNotEmpty(batchGrpPriList)) {
            batchDispatchMapper.insertBatchGrpPri(batchGrpPriList);
        }
        if (CollectionUtils.isNotEmpty(dimValueList)) {
            batchDispatchMapper.insertDimValue(dimValueList);
        }
        DynamicDataSourceHolder.clearDataSource();
    }

    /**
     * 更新批处理状态
     * @param sysBatchUnistartDetail
     */
    public void updateBatchUnistartDetail(SysBatchUnistartDetail sysBatchUnistartDetail, String status) {
        SysBatchUnistartDetail item = new SysBatchUnistartDetail();
        item.setSeqNo(sysBatchUnistartDetail.getSeqNo());
        item.setOrderNo(sysBatchUnistartDetail.getOrderNo());
        item.setDetailSeqNo(sysBatchUnistartDetail.getDetailSeqNo());
        if (BatchDictCodeList.批处理执行状态_执行中.getValue().equals(status)) {
            item.setStartTime(DateUtil.now());
        }
        else if (BatchDictCodeList.批处理执行状态_正常结束.getValue().equals(status)
                 || BatchDictCodeList.批处理执行状态_异常结束.getValue().equals(status)) {
            item.setEndTime(DateUtil.now());
        }
        item.setStatus(status);
        item.setUpdateTime(DateUtil.now());
        sysBatchUnistartDetailMapper.updateByPrimaryKeySelective(item);
    }

    /**
     * 更新批处理状态
     * @param sysBatchUnistart
     */
    public void updateBatchUnistart(SysBatchUnistart sysBatchUnistart, String status) {
        SysBatchUnistart item = new SysBatchUnistart();
        item.setSeqNo(sysBatchUnistart.getSeqNo());
        if (BatchDictCodeList.批处理执行状态_执行中.getValue().equals(status)) {
            item.setStartTime(DateUtil.now());
        }
        else if (BatchDictCodeList.批处理执行状态_正常结束.getValue().equals(status)
                 || BatchDictCodeList.批处理执行状态_异常结束.getValue().equals(status)) {
            item.setEndTime(DateUtil.now());
        }
        item.setStatus(status);
        item.setUpdateTime(DateUtil.now());
        sysBatchUnistartMapper.updateByPrimaryKeySelective(item);
    }

    /**
     * 查询统一批处理配置
     * @param configId
     * @return
     */
    public List<BatchUnistartGrpconfig> findGrpconfigById(String configId) {
        DynamicDataSourceHolder.putDataSource(DataSourceType.MASERT);
        List<BatchUnistartGrpconfig> grpConfigs = batchUnistartMapper.findGrpconfigById(configId);
        DynamicDataSourceHolder.clearDataSource();
        return grpConfigs;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @DataSource(DataSourceType.OTHER)
    public SysBatchTask getBatchTaskList(String taskId) {
//        DynamicDataSourceHolder.putDataSource(DataSourceType.OTHER);
        SysBatchTask sysBatchTask = sysBatchTaskMapper.selectByPrimaryKey(taskId);
//        DynamicDataSourceHolder.clearDataSource();
        return sysBatchTask;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @DataSource(DataSourceType.OTHER)
    public List<SysBatchJob> selectBatchJob(String batchgrpSeqNo) {
//        DynamicDataSourceHolder.putDataSource(DataSourceType.OTHER);
        List<SysBatchJob> list = batchUnistartMapper.findAllJobByGroup(batchgrpSeqNo);
//        DynamicDataSourceHolder.clearDataSource();
        return list;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @DataSource(DataSourceType.OTHER)
    public SysPara selectSysPara(SysParaKey key) {
//        DynamicDataSourceHolder.putDataSource(DataSourceType.OTHER);
        SysPara sysPara = sysParaMapper.selectByPrimaryKey(key);
//        DynamicDataSourceHolder.clearDataSource();
        return sysPara;
    }
}
