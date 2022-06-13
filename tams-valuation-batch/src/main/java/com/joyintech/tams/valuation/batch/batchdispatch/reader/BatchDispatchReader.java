/*
 * 文件名：BatchDispatchReader.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：wangzhiang
 * 修改时间：2018年4月27日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.batch.batchdispatch.reader;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.joyintech.framework.util.CacheUtil;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.valuation.batch.common.cache.service.ChangingCacheService;
import com.joyintech.tams.valuation.batch.common.cache.service.ValuationBatchCacheService;
import com.joyintech.tams.valuation.common.constants.ValDictCodeList;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.joyintech.batch.batchbase.baseclass.BaseReader;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.tams.valuation.batch.batchdispatch.dao.BatchDispatchMapper;
import com.joyintech.tams.valuation.batch.batchdispatch.dto.BatchDispatchReaderDto;
import com.joyintech.tams.valuation.batch.batchdispatch.dto.BatchTaskInfo;
import com.joyintech.tams.valuation.batch.batchdispatch.dto.DealAccountingTodo;
import com.joyintech.tams.valuation.batch.batchdispatch.service.BatchDispathReaderService;
import com.joyintech.tams.valuation.batch.common.accountingmethod.dao.AccountingMethodMapper;
import com.joyintech.tams.valuation.common.constants.BookConstant;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;
import com.joyintech.tams.common.base.entity.BokBatchTaskLink;
import com.joyintech.tams.common.base.entity.BokBookset;
import com.joyintech.tams.common.base.entity.BokMatchRlat;

/**
 * 整体调度批处理 - reader
 * 
 * @author qiangchengxi
 * @version 1.0 2018年4月27日
 * @see BatchDispatchReader
 * @since 1.0
 */
@Service
@Scope("prototype")
public class BatchDispatchReader extends BaseReader<BatchDispatchReaderDto> {

    /**
     * batchDispatchMapper
     */
    @Autowired
    private BatchDispatchMapper batchDispatchMapper;
    @Autowired
    private BatchDispathReaderService batchDispathReaderService;
    @Autowired
    private ValuationBatchCacheService valuationBatchCacheService;
    @Autowired
    private ChangingCacheService changingCacheService;
    
    /**
     * 账务删除任务组
     */
    private static final String DEL_TASK = "71d2d76a4f5d2ff2e050007f01000e70";
    
    /**
     * 结息日预处理任务组
     */
    private static final String PRE_TASK = "71d2edea97deace9e050007f01001581";
    
    /**
     * 银行账任务组
     */
    private static final String BAN_TASK = "86c8dff4efe26cd6e053d246a8c009bf";
    /**
     * {@inheritDoc}
     */
    @Override
    public List<BatchDispatchReaderDto> createList(){
        Map<String, Object> param = this.getParams();
        // 1. 查询默认批处理组
        // 后期可配置为系统参数
        List<String>  batchGroupSeqNos = new ArrayList<>();
        List<String> matchTypeList = new ArrayList<>();
        matchTypeList.add(DictCodeListExt1.匹配类型_任务代码_批处理任务组SEQ.getValue());
//        List<BokMatchRlat> valMatchRlats = accountingMethodMapper.selectValMatchRlatBySource(
//            matchTypeList, this.getBatchExecuteInfo().getTaskId(), null);
        List<BokMatchRlat> valMatchRlats=valuationBatchCacheService.getBokMatchRlatFromCache( matchTypeList, this.getBatchExecuteInfo().getTaskId(), null);

        valMatchRlats.forEach(x->{
            batchGroupSeqNos.add(x.getTarget());
        });
        List<BatchTaskInfo> taskList = batchDispathReaderService.selectBatchTaskInfo(batchGroupSeqNos);
        // 查询批处理匹配关系
        matchTypeList.clear();
        matchTypeList.add(DictCodeListExt1.匹配类型_任务代码_业务类型.getValue());
        matchTypeList.add(DictCodeListExt1.匹配类型_任务代码_跑批规则.getValue());
        matchTypeList.add(DictCodeListExt1.匹配类型_任务代码_是否全量.getValue());
//        List<BokMatchRlat> valMatchRlats1 = accountingMethodMapper.selectValMatchRlatBySource(
//            matchTypeList, null, null);
        List<BokMatchRlat> valMatchRlats1=valuationBatchCacheService.getBokMatchRlatFromCache( matchTypeList, null, null);
        // 按任务分组
        Map<String, List<BokMatchRlat>> valMatchRlatMap = valMatchRlats1.stream().collect(
            Collectors.groupingBy(x -> x.getSource()));
        // 查询前序任务
        List<BokBatchTaskLink> bokBatchTaskLinkList = batchDispatchMapper.selectBatchTaskLink();
        // 按任务分组
        Map<String, List<BokBatchTaskLink>> bokBatchTaskLinkMap = bokBatchTaskLinkList.stream().collect(
            Collectors.groupingBy(x -> x.getTaskId()));
        // 补充任务信息
        taskList.forEach(x -> {
            if (valMatchRlatMap.containsKey(x.getTaskId())) {
                valMatchRlatMap.get(x.getTaskId()).forEach(y -> {
                    if (DictCodeListExt1.匹配类型_任务代码_业务类型.getValue().equals(y.getMatchType())) {
                        x.setBusiType(y.getTarget());
                    }
                    else if (DictCodeListExt1.匹配类型_任务代码_跑批规则.getValue().equals(y.getMatchType())) {
                        x.setTaskExeRule(y.getTarget());
                    }
                    else if (DictCodeListExt1.匹配类型_任务代码_是否全量.getValue().equals(y.getMatchType())) {
                        x.setAllDetail(y.getTarget());
                    }
                });
            }
            if (bokBatchTaskLinkMap.containsKey(x.getTaskId())) {
                bokBatchTaskLinkMap.get(x.getTaskId()).forEach(y -> {
                    x.setpTaskId(y.getpTaskId());
                });
            }
            x.setBooksetType(DictCodeList.账套类别_理财账套.getValue());
        });
        this.getParams().put(BatchTaskInfo.class.getName(), taskList);
        // 公共批处理
        batchGroupSeqNos.clear();
        batchGroupSeqNos.add(DEL_TASK);
        batchGroupSeqNos.add(PRE_TASK);
        batchGroupSeqNos.add(BAN_TASK);
        List<BatchTaskInfo> commonTaskList = batchDispathReaderService.selectBatchTaskInfo(batchGroupSeqNos);
        commonTaskList.forEach(x-> {
            if (DEL_TASK.equals(x.getBatchSeqNo())) {
                x.setTaskExeRule(DictCodeListExt1.跑批规则_最初执行.getValue());
                x.setAllDetail(DictCodeList.是否_是.getValue());
                x.setBooksetType(DictCodeList.账套类别_理财账套.getValue());
            } else if (PRE_TASK.equals(x.getBatchSeqNo())){
                x.setTaskExeRule(DictCodeListExt1.跑批规则_按账套日最初执行.getValue());
                x.setAllDetail(DictCodeList.是否_是.getValue());
                x.setBooksetType(DictCodeList.账套类别_理财账套.getValue());
            } else if(BAN_TASK.equals(x.getBatchSeqNo())) {
                x.setTaskExeRule(DictCodeListExt1.跑批规则_按账套日最后执行.getValue());
                x.setAllDetail(DictCodeList.是否_是.getValue());
                x.setBooksetType(DictCodeList.账套类别_银行账套.getValue());
            }
        });
        this.getParams().put(BookConstant.COMMON + BatchTaskInfo.class.getName(), commonTaskList);
        // 查询入账信息
        Date startDate = this.getBatchExecuteDetail().getStartDate();
        Date endDate = this.getBatchExecuteDetail().getEndDate();
        List<DealAccountingTodo> dealAccountingTodoList = new ArrayList<>();
        while (!startDate.after(endDate)) {
            param.put("booksetDate", DateUtil.format(startDate));
            List<List<String>> booksetLists = (List<List<String>>) param.get(GlobalConstant.BOOKSETLISTS);
            for(List<String> booksetList: booksetLists){
                param.put(GlobalConstant.BOOKSETS, booksetList);
                dealAccountingTodoList.addAll(batchDispatchMapper.selectDealAccounting(param));
                List<DealAccountingTodo> banTodoList = createBanTodo(startDate, booksetList);
                if (CollectionUtils.isNotEmpty(banTodoList)) {
                    dealAccountingTodoList.addAll(banTodoList);
                }
            }
            startDate = DateUtil.addDay(startDate, 1);
        }
        // 以账套代码分组
        Map<String, List<DealAccountingTodo>> dealAccountingTodoMap = dealAccountingTodoList.stream().collect(
            Collectors.groupingBy(x -> x.getBooksetId()));
        // 组装result
        List<BatchDispatchReaderDto> list = dealAccountingTodoMap.entrySet().stream().map(x -> {
            BatchDispatchReaderDto dto = new BatchDispatchReaderDto();
            dto.setDealAccountingTodoList(x.getValue());
            dto.setBooksetId(x.getKey());
            dto.setDetailSeqNo((String) this.getParams().get("detailSeqNo"));
            return dto;
        }).collect(Collectors.toList());
        return list;
    }
    private List<DealAccountingTodo> createBanTodo(Date date, List<String> booksetList) {
        Map<String, Object> param = new HashMap<>();
        param.put("booksetType", DictCodeList.账套类别_银行账套.getValue());
        param.put(GlobalConstant.BOOKSETS, booksetList);
        //List<BokBookset> booksets = accountingMethodMapper.getBooksetInfoByParam(param);
        List<BokBookset> booksets = changingCacheService.getValBokBookset(booksetList);
        List<DealAccountingTodo> todoList = new ArrayList<>();
        for (BokBookset bookset : booksets) {
            DealAccountingTodo todo = new DealAccountingTodo();
            todo.setBooksetId(bookset.getBooksetId());
            todo.setBookSetDate(date);
            todo.setBookDate(date);
            todo.setHappenDate(date);
            todo.setBooksetType(bookset.getBooksetType());
            todo.setIsSubBookset(ValDictCodeList.是否子账套_否.getValue());
            todo.setStartDate(bookset.getStartDate());
            todo.setEndDate(bookset.getEndDate());
            todoList.add(todo);
        }
        return todoList;
    }
    
//    @Transactional(propagation = Propagation.REQUIRES_NEW)
//    @DataSource(DataSourceType.OTHER)
//    public List<BatchTaskInfo> selectBatchTaskInfo(String batchGroupSeqNo){
//        // 查询批处理任务
//        List<BatchTaskInfo> batchTaskInfoList = batchDispatchMapper.selectBatchJob(
//            batchGroupSeqNo);
//        this.getParams().put(BatchTaskInfo.class.getName(), batchTaskInfoList);
//        return batchTaskInfoList;
//    }

}
