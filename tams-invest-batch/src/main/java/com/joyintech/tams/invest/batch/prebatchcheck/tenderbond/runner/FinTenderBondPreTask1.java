package com.joyintech.tams.invest.batch.prebatchcheck.tenderbond.runner;

import com.joyintech.batch.batchbase.baseclass.BatchBaseService;
import com.joyintech.batch.batchbase.exception.BatchException;
import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.framework.base.entity.SysBatchgrpExecInfo;
import com.joyintech.framework.constant.BatchDictCodeList;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.batch.prebatchcheck.common.dto.SysPrebatchResultDto;
import com.joyintech.tams.invest.batch.prebatchcheck.common.service.PrebatchCheckCommonService;
import com.joyintech.tams.invest.batch.prebatchcheck.tenderbond.service.FinTenderBondPreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 自动上市流通批前检查
 *
 * @author gwl
 * @date 2022-04-19
 */
@Scope("prototype")
@Service("finTenderBondPreTask1")
public class FinTenderBondPreTask1 extends BatchBaseService {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private PrebatchCheckCommonService prebatchCheckCommonService;

    @Autowired
    private FinTenderBondPreService finTenderBoudPreService;


    @Override
    public JobExecution execute() throws BatchException {
        JobExecution result = new JobExecution(1l);
        result.setStatus(BatchStatus.FAILED);
        SysBatchExecInfo batchExecuteInfo = this.getBatchExecuteInfo();
        SysBatchgrpExecInfo batchExecuteDetail = this.getBatchExecuteDetail();
        if (batchExecuteInfo == null) {
            logger.info("无启动参数，批处理退出。");
            return null;
        }
        if (StringUtil.equals(BatchDictCodeList.批处理启动模式_自动.getValue(), batchExecuteInfo.getStartMode())) {
            Map<String, Object> param = new HashMap<>();
            param.put("checkDate", batchExecuteInfo.getExecuteDate());
            param.put("taskId", batchExecuteInfo.getTaskId());
            prebatchCheckCommonService.deleteResultAndDetailList(param);
            // 查询所有机构  并生成对应的 List<SysPrebatchResultDto>
            List<SysPrebatchResultDto> resultList = prebatchCheckCommonService.selectAllInstAndPackageDto(batchExecuteInfo);
            if (!StringUtil.isEmptyList(resultList)) {
                for (SysPrebatchResultDto dto : resultList) {
                    try {
                        finTenderBoudPreService.execute1(batchExecuteInfo, dto, dto.getInstitutionCode());
                        result.setStatus(BatchStatus.COMPLETED);
                    } catch (Exception e) {
                        dto.setTaskStatus(DictCodeListExt3.批前检查_执行状态_异常.getValue());
                        logger.error(e.getMessage());
                    }
                    dto.setStartTime(null);
                    dto.setEndTime(new Date());
                    prebatchCheckCommonService.updateSysPrebatchResult(dto);
                }
            }
            result.setStatus(BatchStatus.COMPLETED);
            return result;
        }
        // 手动开始执行
        String instCode = batchExecuteDetail.getOtherParam4();
        SysPrebatchResultDto paramdto = new SysPrebatchResultDto();
        paramdto.setBatchSeqNo(batchExecuteInfo.getBatchExedetailSeqNo());
        paramdto.setTaskId(batchExecuteInfo.getTaskId());
        SysPrebatchResultDto dto = prebatchCheckCommonService.selAndUpdPrebatchResult(paramdto);
        if (dto == null || StringUtil.isEmpty(dto.getSeqNo())) {
            logger.info("此次批前检查未找到该启动任务id。");
            return null;
        }
        try {
            finTenderBoudPreService.execute1(batchExecuteInfo, dto, instCode);
            result.setStatus(BatchStatus.COMPLETED);
        } catch (Exception e) {
            dto.setTaskStatus(DictCodeListExt3.批前检查_执行状态_异常.getValue());
            logger.error(e.getMessage());
        }
        dto.setStartTime(null);
        dto.setEndTime(new Date());
        prebatchCheckCommonService.updateSysPrebatchResult(dto);
        return result;
    }

}
