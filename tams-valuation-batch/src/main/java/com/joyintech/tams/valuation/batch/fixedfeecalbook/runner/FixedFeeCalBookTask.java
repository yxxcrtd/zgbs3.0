/*
 * 文件名：FixedFeeCalBookTask.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：weipeng
 * 修改时间：2018年6月4日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.batch.fixedfeecalbook.runner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.valuation.batch.common.cache.service.ValuationBatchCacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.joyintech.batch.batchbase.baseclass.*;
import com.joyintech.batch.batchbase.baseclass.entity.NeedOperateEntity;
import com.joyintech.batch.batchbase.baseclass.entity.NeedToWriteEntity;
import com.joyintech.batch.batchbase.exception.BatchException;
import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.tams.common.base.entity.BokMatchRlat;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.valuation.batch.common.accountingmethod.accounting.BookCommonLogic;
import com.joyintech.tams.valuation.batch.common.accountingmethod.dao.AccountingMethodMapper;
import com.joyintech.tams.valuation.batch.common.bookgenerateresult.BookGenerateResultForCurrentCommonWriterWithBokStatFeePosition;
import com.joyintech.tams.valuation.batch.fixedfeecalbook.processor.FixedFeeCalBookProcessor;
import com.joyintech.tams.valuation.batch.fixedfeecalbook.reader.FixedFeeCalBookReader;
import com.joyintech.tams.valuation.common.constants.BookConstant;

/**
 * TA固定费用计提入账批处理
 * 
 * @author weipeng
 * @version 1.0 2018年6月4日
 * @see FixedFeeCalBookTask
 * @since 1.0
 */
@Service("fixedFeeCalBookTask")
@Scope("prototype")
public class FixedFeeCalBookTask extends BatchBaseService {

    @Autowired
    private BookCommonLogic bookCommonLogic;
    
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * batchBuilder
     */
    @Autowired
    private BatchBuilder<BaseReader<NeedOperateEntity>, BaseProcessor<NeedOperateEntity, NeedToWriteEntity>, BaseWriter<NeedToWriteEntity>> batchBuilder;

    /**
     * step1Reader
     */
    @Autowired
    private FixedFeeCalBookReader step1Reader;

    /**
     * step1Processor
     */
    @Autowired
    private FixedFeeCalBookProcessor step1Processor;

//    /**
//     * step1Writer
//     */
//    @Autowired
//    private BookGenerateResultForCurrentCommonWriter step1Writer;
    
    /**
     * step1Writer
     */
    @Autowired
    private BookGenerateResultForCurrentCommonWriterWithBokStatFeePosition step1Writer;
    @Autowired
    private ValuationBatchCacheService valuationBatchCacheService;

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public JobExecution execute()
        throws BatchException {

        // 获取批处理信息
        SysBatchExecInfo batchExecuteInfo = this.getBatchExecuteInfo();
        if (batchExecuteInfo == null) {
            logger.info("无启动参数，批处理退出。");
            return null;
        }

        // 设置此task的序列任务公用的params参数
        Map<String, Object> params = new HashMap<>();

        // 待处理列表list
        params.put("BATCHINFO", batchExecuteInfo);
        params.put("taskId", batchExecuteInfo.getTaskId());// 任务代码放入params中
        Map<String, String> dimValueMap = bookCommonLogic.getDimensionValue(this.getBatchExecuteInfo());
        params.put(BookConstant.BOOKSET_DATE, DateUtil.parse(dimValueMap.get(BookConstant.BOOKSET_DATE)));
        params.put(BookConstant.BOOK_DATE, DateUtil.parse(dimValueMap.get(BookConstant.BOOK_DATE)));
        params.put(BookConstant.HAPPEN_DATE, DateUtil.parse(dimValueMap.get(BookConstant.HAPPEN_DATE)));
        params.put("cdate", this.getBatchExecuteInfo().getExecuteDate());
        params.put("seqNo", this.getBatchExecuteDetail().getSeqNo());
        params.put("taskId", this.getBatchExecuteInfo().getTaskId());
        params.put(BookConstant.IS_SUB_BOOKSET, dimValueMap.get(BookConstant.IS_SUB_BOOKSET));
        // 现金流类型集合----固定管理费、托管费、销售服务费
        List<String> cashTypeList = new ArrayList<>();
//        cashTypeList.add(DictCodeListExt1.现金流二级类型_固定管理费.getValue());
//        cashTypeList.add(DictCodeListExt1.现金流二级类型_托管费.getValue());
//        cashTypeList.add(DictCodeListExt1.现金流二级类型_销售费.getValue());
//        cashTypeList.add(DictCodeListExt1.现金流二级类型_外包服务费.getValue());
//        cashTypeList.add(DictCodeListExt1.现金流二级类型_代销费.getValue());
        List<String> matchTypeList=Lists.newArrayList();
        matchTypeList.add(DictCodeListExt1.匹配类型_任务代码_计提现金流类型.getValue());
//        List<BokMatchRlat> valMatchRlats = accountingMethodMapper.selectValMatchRlatBySource(
//        		matchTypeList, this.getBatchExecuteInfo().getTaskId(), null);
        List<BokMatchRlat> valMatchRlats = valuationBatchCacheService.getBokMatchRlatFromCache(matchTypeList, this.getBatchExecuteInfo().getTaskId(), null);
        for(BokMatchRlat rlat:valMatchRlats){
            cashTypeList.add(rlat.getTarget());
        }
        params.put("cashTypeList", cashTypeList);

        // 各步骤设置参数
        step1Reader.setParams(params);
        step1Processor.setParams(params);
        step1Writer.setParams(params);

        return batchBuilder.init(this).batch("step1", (BaseReader)step1Reader,
            (BaseProcessor)step1Processor, (BaseWriter)step1Writer).builder(
                this.getBatchTask().getBeanName() + this.getBatchTask().getTaskId()).run();
    }

}
