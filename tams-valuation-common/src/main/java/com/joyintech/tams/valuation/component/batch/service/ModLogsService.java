package com.joyintech.tams.valuation.component.batch.service;

import com.joyintech.framework.constant.BatchDictCodeList;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.dao.ModBatchLogsMapper;
import com.joyintech.tams.common.base.entity.ModBatchLogs;
import com.joyintech.tams.constant.ModelDictCodeList;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 模型 日志服务
 * @author 王波
 * @date 2021-07-22
 * @see ModLogsService
 * @since 1.0
 */
@Service
public class ModLogsService {

    private static final Logger logger = LoggerFactory.getLogger(ModLogsService.class);

    private static final String ENDING = "<br />";

    @Autowired
    private SqlSessionTemplate sqlSessionTemplate;

//    @Autowired
//    private ModBatchLogsMapper modBatchLogsMapper;

    /**
     * 批量保存日志
     * @param logs
     */
    public void saveBatch(List<ModBatchLogs> logs) {
        SqlSession sqlSession = sqlSessionTemplate.getSqlSessionFactory().openSession(ExecutorType.BATCH, false);
        try {
            ModBatchLogsMapper mapper = sqlSession.getMapper(ModBatchLogsMapper.class);
            int count = 0;
            for (ModBatchLogs log : logs) {
                // 每1000条提交一次
                if (count % 1000 == 0) {
                    sqlSession.commit();
                    sqlSession.clearCache();
                }
                mapper.insertSelective(log);
                count++;
            }

        } finally {
            sqlSession.commit();
            sqlSession.close();
        }
    }
    /**
     *
     * @param taskId
     * @param batchExeDetailSeqno
     * @param finprodId
     */
    public ModBatchLogs initLog(String taskId, String batchExeDetailSeqno, String finprodId){
        ModBatchLogs log = new ModBatchLogs();
        log.setSeqno(IDGenerator.generate());
        log.setFinprodId(finprodId);
        log.setBatchExecSeqno(batchExeDetailSeqno);
        log.setTaskId(taskId);
        log.setExecStatus(ModelDictCodeList.模型计算结果_成功.getValue());
        return log;
    }
    /**
     *
     * @param taskId
     * @param batchExeDetailSeqno
     * @param finprodIds
     */
//    public void batchInitLogs(String taskId, String batchExeDetailSeqno, List<String> finprodIds){
//        List<ModBatchLogs> listLog = new ArrayList<ModBatchLogs>();
//        if (CollectionUtils.isNotEmpty(finprodIds)) {
//            finprodIds.forEach((x) -> {
//                ModBatchLogs log = new ModBatchLogs();
//                log.setSeqno(IDGenerator.generate());
//                log.setFinprodId(x);
//                log.setBatchExecSeqno(batchExeDetailSeqno);
//                log.setTaskId(taskId);
//                listLog.add(log);
//            });
//            this.saveBatch(listLog);
//        }
//    }

    /**
     * 警告信息
     * @param finprodId 资产代码
     * @param batchExecSeq 批处理Seq
     * @param msg 消息
     */
//    public synchronized void warn(String finprodId, String batchExecSeq, String taskId,  String msg) {
//        if (StringUtil.isBlank(finprodId) || StringUtil.isEmpty(batchExecSeq)) {
//            logger.error("资产代码或批处理序号不能为空");
//            return;
//        }
//        // 条件
////        ModBatchLogsExample example = new ModBatchLogsExample();
////        ModBatchLogsExample.Criteria criteria = example.createCriteria();
////        criteria.andFinprodIdEqualTo(finprodId);
////        criteria.andBatchExecSeqnoEqualTo(batchExecSeq);
////        criteria.andTaskIdEqualTo(taskId);
//        // 查询
////        List<ModBatchLogs> modBatchLogs = modBatchLogsMapper.selectByExample(example);
//        if (modBatchLogs.size() != 1) {
//            logger.error("不正确的日志记录:资产代码【{}】, 批处理序列号【{}】", finprodId, batchExecSeq);
//            return;
//        }
//        ModBatchLogs modBatchLog = modBatchLogs.get(0);
//        String detailMsg = modBatchLog.getExecDetail() == null ? "": modBatchLog.getExecDetail();
//        modBatchLog.setExecDetail(detailMsg + msg + ENDING);
//        modBatchLog.setExecStatus(ModelDictCodeList.模型计算结果_警告.getValue());
//        modBatchLog.setUpdateTime(new Date());
//        List<String> execResult = new ArrayList<>(2);
//        execResult.add(ModelDictCodeList.模型计算结果_成功.getValue());
//        execResult.add(ModelDictCodeList.模型计算结果_失败.getValue());
//        criteria.andExecStatusNotIn(execResult);
//        ModBatchLogsExample.Criteria orCriteria = example.createCriteria();
//        orCriteria.andExecStatusIsNull();
//        orCriteria.andFinprodIdEqualTo(finprodId);
//        orCriteria.andBatchExecSeqnoEqualTo(batchExecSeq);
//        orCriteria.andTaskIdEqualTo(taskId);
//        example.or(orCriteria);
//        modBatchLogsMapper.updateByExampleWithBLOBs(modBatchLog,example);
//    }

    /**
     * 失败信息
     * @param finprodId 资产代码
     * @param batchExecSeq 批处理Seq
     * @param msg 消息
     */
//    public synchronized void error(String finprodId, String batchExecSeq, String taskId, String msg) {
//        if (StringUtil.isBlank(finprodId) || StringUtil.isEmpty(batchExecSeq)) {
//            logger.error("资产代码或批处理序号不能为空");
//            return;
//        }
//        // 条件
//        ModBatchLogsExample example = new ModBatchLogsExample();
//        ModBatchLogsExample.Criteria criteria = example.createCriteria();
//        criteria.andFinprodIdEqualTo(finprodId);
//        criteria.andBatchExecSeqnoEqualTo(batchExecSeq);
//        criteria.andTaskIdEqualTo(taskId);
//        // 查询
//        List<ModBatchLogs> modBatchLogs = modBatchLogsMapper.selectByExample(example);
//        if (modBatchLogs.size() != 1) {
//            logger.error("不正确的日志记录:资产代码【{}】, 批处理序列号【{}】", finprodId, batchExecSeq);
//            return;
//        }
//        ModBatchLogs modBatchLog = modBatchLogs.get(0);
//        String detailMsg = modBatchLog.getExecDetail() == null ? "": modBatchLog.getExecDetail();
//        modBatchLog.setExecDetail(detailMsg + msg + ENDING);
//        modBatchLog.setExecStatus(ModelDictCodeList.模型计算结果_失败.getValue());
//        modBatchLog.setUpdateTime(new Date());
//        modBatchLogsMapper.updateByExampleWithBLOBs(modBatchLog,example);
//    }

    /**
     * 成功信息
     * @param finprodId 资产代码
     * @param batchExecSeq 批处理Seq
     * @param msg 消息
     */
//    public synchronized void success(String finprodId, String batchExecSeq, String taskId, String msg) {
//        if (StringUtil.isBlank(finprodId) || StringUtil.isEmpty(batchExecSeq)) {
//            logger.error("资产代码或批处理序号不能为空");
//            return;
//        }
//        // 条件
//        ModBatchLogsExample example = new ModBatchLogsExample();
//        ModBatchLogsExample.Criteria criteria = example.createCriteria();
//        criteria.andFinprodIdEqualTo(finprodId);
//        criteria.andBatchExecSeqnoEqualTo(batchExecSeq);
//        criteria.andTaskIdEqualTo(taskId);
//        // 查询
//        List<ModBatchLogs> modBatchLogs = modBatchLogsMapper.selectByExample(example);
//        if (modBatchLogs.size() != 1) {
//            logger.error("不正确的日志记录:资产代码【{}】, 批处理序列号【{}】", finprodId, batchExecSeq);
//            return;
//        }
//        ModBatchLogs modBatchLog = modBatchLogs.get(0);
//        String detailMsg = modBatchLog.getExecDetail() == null ? "": modBatchLog.getExecDetail();
//        modBatchLog.setExecDetail(detailMsg + msg + ENDING);
//        modBatchLog.setExecStatus(ModelDictCodeList.模型计算结果_成功.getValue());
//        modBatchLog.setUpdateTime(new Date());
//
//        // 只能对未修改状态的进行更新
//        criteria.andExecStatusIsNull();
//        modBatchLogsMapper.updateByExampleWithBLOBs(modBatchLog,example);
//    }

    /**
     *
     * @param logsMap
     * @param finProdId
     * @param msg
     */
    public synchronized void success(Map<String,ModBatchLogs> logsMap,String finProdId,String msg) {
        if(logsMap.containsKey(finProdId)){
            logsMap.get(finProdId).setExecStatus(ModelDictCodeList.模型计算结果_成功.getValue());
            logsMap.get(finProdId).setExecDetail(msg);
        }else{
            logger.error("资产代码："+finProdId+" 未初始化执行日志.");
        }
    }

    /**
     *
     * @param logsMap
     * @param finProdId
     * @param msg
     */
    public synchronized void error(Map<String,ModBatchLogs> logsMap,String finProdId,String msg) {
        if(logsMap.containsKey(finProdId)){
            logsMap.get(finProdId).setExecStatus(ModelDictCodeList.模型计算结果_失败.getValue());
            logsMap.get(finProdId).setExecDetail(msg);
        }else{
            logger.error("资产代码："+finProdId+" 未初始化执行日志.");
        }
    }
    /**
     *
     * @param logsMap
     * @param finProdId
     * @param msg
     */
    public synchronized void info(Map<String,ModBatchLogs> logsMap,String finProdId,String msg) {
        if(logsMap.containsKey(finProdId)){
            if(logsMap.get(finProdId).getExecDetail() == null){
                logsMap.get(finProdId).setExecDetail(msg);
            }
            else{
                logsMap.get(finProdId).setExecDetail(logsMap.get(finProdId).getExecDetail()+";\r"+msg);
            }
        }else{
            logger.error("资产代码："+finProdId+" 未初始化执行日志.");
        }
    }
    /**
     *
     * @param logsMap
     * @param finProdId
     * @param msg
     */
    public synchronized void warn(Map<String,ModBatchLogs> logsMap,String finProdId,String msg) {
        if(logsMap.containsKey(finProdId)){
            logsMap.get(finProdId).setExecStatus(ModelDictCodeList.模型计算结果_警告.getValue());
            if(logsMap.get(finProdId).getExecDetail() == null){
                logsMap.get(finProdId).setExecDetail(msg);
            }
            else{
                logsMap.get(finProdId).setExecDetail(logsMap.get(finProdId).getExecDetail()+";"+msg);
            }
        }else{
            logger.error("资产代码："+finProdId+" 未初始化执行日志.");
        }
    }

}
