/*
 * 文件名：ResetBaseRuleInfoTask.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangnian
 * 修改时间：2019年2月16日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.invest.batch.mproddivreindealgen.runner;

import java.util.*;

import com.joyintech.framework.constant.BatchDictCodeList;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.invest.batch.mproddivreindealgen.dao.MProdDivReinDealGenMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.joyintech.batch.batchbase.baseclass.*;
import com.joyintech.batch.batchbase.baseclass.entity.NeedOperateEntity;
import com.joyintech.batch.batchbase.baseclass.entity.NeedToWriteEntity;
import com.joyintech.batch.batchbase.exception.BatchException;
import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.framework.web.PlatformDate;
import com.joyintech.tams.invest.batch.common.util.BatchConstant;
import com.joyintech.tams.invest.batch.mproddivreindealgen.processor.MProdDivReinDealGenDelProcessor;
import com.joyintech.tams.invest.batch.mproddivreindealgen.processor.MProdDivReinDealGenProcessor;
import com.joyintech.tams.invest.batch.mproddivreindealgen.reader.MProdDivReinDealGenDelReader;
import com.joyintech.tams.invest.batch.mproddivreindealgen.reader.MProdDivReinDealGenReader;
import com.joyintech.tams.invest.batch.mproddivreindealgen.writer.MProdDivReinDealGenDelWriter;
import com.joyintech.tams.invest.batch.mproddivreindealgen.writer.MProdDivReinDealGenWriter;

/**
 *  业绩基准重置
 * 〈功能详细描述〉
 * @author zhangnian
 * @version 1.0 2019年2月16日
 * @see MProdDivReinDealGenTask
 * @since 1.0
 */
@Service("mProdDivReinDealGenTask")
@Scope("prototype")
public class MProdDivReinDealGenTask extends BatchBaseService{

    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * BatchBuilder
     */
    @Autowired
    private BatchBuilder<BaseReader<NeedOperateEntity>, BaseProcessor<NeedOperateEntity, NeedToWriteEntity>, BaseWriter<NeedToWriteEntity>> batchBuilder;

    /**
     * PlatformDate
     */
    @Autowired(required = false)
    private PlatformDate platformDateService;

    /**
     * ResetBaseRuleInfoDelReader
     */
    @Autowired
    private MProdDivReinDealGenDelReader step1Reader;

    /**
     * ResetBaseRuleInfoDelProcessor
     */
    @Autowired
    private MProdDivReinDealGenDelProcessor step1Processor;

    /**
     * ResetBaseRuleInfoDelWriter
     */
    @Autowired
    private MProdDivReinDealGenDelWriter step1Writer;

    /**
     * ResetBaseRuleInfoReader
     */
    @Autowired
    private MProdDivReinDealGenReader step2Reader;

    /**
     * ResetBaseRuleInfoProcessor
     */
    @Autowired
    private MProdDivReinDealGenProcessor step2Processor;

    /**
     * ResetBaseRuleInfoWriter
     */
    @Autowired
    private MProdDivReinDealGenWriter step2Writer;

    //add by mashukai TISHKBS-1640  高频开放货币型产品跑批过滤节假日 start
    @Autowired
    private MProdDivReinDealGenMapper mProdDivReinDealGenMapper;
    //add by mashukai TISHKBS-1640  高频开放货币型产品跑批过滤节假日 end
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public JobExecution execute()
        throws BatchException {

        SysBatchExecInfo batchExecuteInfo = this.getBatchExecuteInfo();
        if (batchExecuteInfo == null) {
            logger.info("无启动参数，批处理退出。");
            return null;
        }

        if (StringUtil.equals(BatchDictCodeList.批处理启动模式_自动.getValue(),
                batchExecuteInfo.getStartMode())) {
            batchExecuteInfo.setExecuteDate(platformDateService.getSysDate());
        }

        //add by mashukai TISHKBS-1640  高频开放货币型产品跑批过滤节假日 start
        if (StringUtil.equals(CacheUtil.getSysParam("HOLIDAY_IS_DIVIDEND"),"N") ){
            Map<String ,Object> dateMap = new HashMap<String ,Object>();
            Date executeDate = batchExecuteInfo.getExecuteDate();
            String executeDateDtr = DateUtil.format(executeDate);
            dateMap.put("startDate",executeDate);
            dateMap.put("endDate",executeDate);
            List<String> calList = new ArrayList<String>();
            List<Map<String,String>> holiday = mProdDivReinDealGenMapper.getIsHoliday(dateMap);
            for (Map<String,String> map : holiday){
                if (StringUtil.equals(executeDateDtr,map.get("holiDate"))){
                    calList.add(map.get("calendarID"));
                }
            }
            if(calList != null && calList.size() > 0){
                logger.info("系统参数控制货币型高频开放产品节假日不自动进行红利再投");
                Map<String,Object> serchMap =new HashMap<String,Object>();
                serchMap.put("productIdList",this.getProductIdList());
                serchMap.put("calList",calList);
                List<String> productIdList = mProdDivReinDealGenMapper.selectProdList(serchMap);
                if (productIdList.size() < 1){
                    JobExecution result = new JobExecution(1l);
                    result.setStatus(BatchStatus.COMPLETED);
                    return result;
                }else {
                    this.setProductIdList(productIdList);
                }
            }
        }
        //add by mashukai TISHKBS-1640  高频开放货币型产品跑批过滤节假日 end

        // 设置此task的序列任务公用的params参数
        Map<String, Object> params = new HashMap<>();

        params.put(BatchConstant.BATCH_INFO, batchExecuteInfo);
        params.put(BatchConstant.PLATE_DATE, platformDateService.getSysDate(batchExecuteInfo.getCreateUser()));
        params.put("productIdList",this.getProductIdList());

        // 各步骤设置参数
        step1Reader.setParams(params);
        step1Processor.setParams(params);
        step1Writer.setParams(params);
        step2Reader.setParams(params);
        step2Processor.setParams(params);
        step2Writer.setParams(params);

        return batchBuilder.init(this).batch("step1", (BaseReader)step1Reader,
            (BaseProcessor)step1Processor, (BaseWriter)step1Writer).batch("step2", (BaseReader)step2Reader,
                (BaseProcessor)step2Processor, (BaseWriter)step2Writer).builder(
                    this.getBatchTask().getBeanName() + this.getBatchTask().getTaskId()).run();
    }
    
}
