package com.joyintech.tams.invest.batch.redisinit;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.invest.batch.riskparamcalc.processor.RskParamReCalcProcessor;
import com.joyintech.tams.invest.batch.riskparamcalc.reader.RskParamReCalcReader;
import com.joyintech.tams.invest.batch.riskparamcalc.writer.RskParamReCalcWriter;
import com.joyintech.tams.invest.common.constantrds.BussinessConstantRds;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.common.risktrialrds.dict.LogLvl;
import com.joyintech.tams.invest.common.risktrialrds.util.RiskUtil;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.joyintech.batch.batchbase.baseclass.BaseProcessor;
import com.joyintech.batch.batchbase.baseclass.BaseReader;
import com.joyintech.batch.batchbase.baseclass.BaseWriter;
import com.joyintech.batch.batchbase.baseclass.BatchBaseService;
import com.joyintech.batch.batchbase.baseclass.BatchBuilder;
import com.joyintech.batch.batchbase.baseclass.entity.NeedOperateEntity;
import com.joyintech.batch.batchbase.baseclass.entity.NeedToWriteEntity;
import com.joyintech.batch.batchbase.exception.BatchException;
import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.framework.web.PlatformDate;
import com.joyintech.tams.invest.batch.common.util.BatchConstant;
import com.joyintech.tams.invest.common.redisinit.dto.ImplCond;
import com.joyintech.tams.invest.common.redisinit.service.RiskRedisInitService;

/**
 * 批处理REDIS初始化
 * @author jjpei
 * @version 1.0 2019/9/2
 * @see RiskRedisInitTask
 * @since 1.0
 **/
@Service(value="riskRedisInitTask")
@Scope(value="prototype")
public class RiskRedisInitTask extends BatchBaseService{
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * PlatformDate
     */
    @Autowired(required = false)
    private RiskRedisInitService riskRedisInitService;
    /**
     * PlatformDate
     */
    @Autowired(required = false)
    private PlatformDate platformDateService;

    /**
     * BatchBuilder
     */
    @Autowired
    private BatchBuilder<RskParamReCalcReader, RskParamReCalcProcessor, RskParamReCalcWriter> batchBuilder;

    /**
     * RskParamReCalcReader
     */
    @Autowired
    private RskParamReCalcReader step1Reader;

    /**
     * RskParamReCalcProcessor
     */
    @Autowired
    private RskParamReCalcProcessor step1Processor;

    /**
     * RskParamReCalcWriter
     */
    @Autowired
    private RskParamReCalcWriter step1Writer;

    @Autowired(required = false)
    private IPlatformDate platDataUtil;

    @Override
    public JobExecution execute() throws BatchException {
        JobExecution result = new JobExecution(1l);
        result.setStatus(BatchStatus.FAILED);
        SysBatchExecInfo batchExecuteInfo = this.getBatchExecuteInfo();
        if (batchExecuteInfo == null) {
            logger.info("无启动参数，批处理退出。");
            return null;
        }
        ImplCond implCond=new ImplCond();
        Map<String,Object> batchSucMap=new HashMap<>();
        try {
            // 设置此task的序列任务公用的params参数
            Map<String, Object> params = new HashMap<>();

            params.put(BatchConstant.BATCH_INFO, batchExecuteInfo);

            if(null!=batchExecuteInfo&&null!=batchExecuteInfo.getExecuteDate()&&!StringUtil.equals("0",batchExecuteInfo.getStartMode())){
                implCond.setPlateDate(batchExecuteInfo.getExecuteDate());
            }
            else{
                implCond.setPlateDate(platformDateService.getSysDate(batchExecuteInfo.getCreateUser()));
            }
            //默认不执行删除
            implCond.setDel(false);
            //默认更新指标参数值
            implCond.setIsUpdParVal(true);
            //默认加上参数中间值
            implCond.setLoadOffset(true);
            batchSucMap=riskRedisInitService.execute(implCond);
            if(StringUtil.isNotEmpty(batchSucMap.get("redisErrorMessageList"))){
                StringBuffer logError=new StringBuffer();
                List<Exception> exceptions=(List<Exception>) batchSucMap.get("redisErrorMessageList");
                exceptions.forEach(item->{
                    String msg=RiskUtil.getLogString(item);
                    logError.append(msg).append("\r\n");
                    riskRedisInitService.insertTrialLog(msg, StringUtil.isNotEmpty(implCond.getPlateDate()) ? implCond.getPlateDate() : new Date(), LogLvl.ERROR.toString(), BussinessDictCodeListRds.批机能类型_REDIS初始化.getValue());
                });
                if(StringUtil.isNotEmpty(logError)) {
                    throw new BatchException(logError.toString());
                }
            }else {
                result.setStatus(BatchStatus.COMPLETED);
            }

            if (StringUtil.isNotEmpty(CacheUtil.getSysParam(BussinessConstantRds.IS_REPEATED_REIDSBATCH))
                && StringUtil.equals(CacheUtil.getSysParam(BussinessConstantRds.IS_REPEATED_REIDSBATCH), DictCodeList.是否_是.getValue()) ) {
                logger.info("批处理-指标参数重新更新redis开始");
                Map<String, Object> paramsTwo = new HashMap<>();
                paramsTwo.put(BatchConstant.KEY_PRODID_LIST, this.getProductIdList());
                paramsTwo.put("sysDate", platDataUtil.getSysDate(batchExecuteInfo.getCreateUser()));
                step1Reader.setParams(paramsTwo);
                step1Processor.setParams(paramsTwo);
                step1Writer.setParams(paramsTwo);
                result = batchBuilder.init(this)
                    .batch("step1", step1Reader, step1Processor, step1Writer)
                    .builder(this.getBatchTask().getBeanName() + this.getBatchTask().getTaskId())
                    .run();
            }
        }
        catch (Exception e) {
            logger.error(e.getMessage(),e);
            riskRedisInitService.insertTrialLog(RiskUtil.getLogString(e), StringUtil.isNotEmpty(implCond.getPlateDate())?implCond.getPlateDate():new Date(), LogLvl.ERROR.toString(), BussinessDictCodeListRds.批机能类型_REDIS初始化.getValue());
            throw new BatchException(RiskUtil.getLogString(e));
        }
        return result;
    }
}
