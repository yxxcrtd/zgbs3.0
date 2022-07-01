/*
 * 文件名：BookDetailCompareIntoWriter.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：wangwei1
 * 修改时间：2018年6月7日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.batch.bookdetailcompareinto.writer;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.joyintech.batch.batchbase.baseclass.BaseWriter;
import com.joyintech.batch.batchbase.baseclass.entity.NeedToWriteEntity;
import com.joyintech.tams.common.base.entity.BokBatchTaskTodo;
import com.joyintech.tams.valuation.batch.bookdetailcompareinto.dao.BookDetailCompareIntoMapper;
import com.joyintech.tams.valuation.batch.common.accountingmethod.accounting.BookCommonLogic;
import com.joyintech.tams.valuation.batch.common.base.entity.extcommon.ValBokBookdetailBatch;
import com.joyintech.tams.valuation.common.constants.BookConstant;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.valuation.common.utils.DataHandleUtil;

/**
 * 明细记账比较批处理 数据库写入
 * 
 * @author wangwei1
 * @version 1.0 2018年6月8日
 * @see BookGenerateResultForCurrentCommonWriter
 * @since 1.0
 */
@Service
@Scope("prototype")
public class BookDetailCompareUpdateWriter extends BaseWriter<NeedToWriteEntity> {
    
    /**
     * 记账公共类
     */
    @Autowired
    private BookCommonLogic bookCommonLogic;

    /**
     * reader方法的 Service
     */
    @Autowired
    private BookDetailCompareIntoMapper bookDetailCompareIntoMapper;

    /**
     * 明细记账比较批处理
     */
    @Override
    public void write(List<? extends NeedToWriteEntity> items)
        throws Exception {
        
        Map<String, Object> params = new HashMap<>();
        params.putAll(this.getParams());
//        params.remove(BookConstant.HAPPEN_DATE);
        
        //取得待处理账套代码列表
        List<BokBatchTaskTodo> listBatchTaskTodo = bookCommonLogic.getTaskTodoScopeInfo(this.getParams());
        //跑批日期
        Date startDate = listBatchTaskTodo.get(0).getHappenDate();
        params.put(BookConstant.HAPPEN_DATE, startDate);
		// 循环 跑批范围信息List，对每个待处理任务进行处理
		// for (BokBatchTaskTodo todoNow : listBatchTaskTodo) {
		// 1、更新历史表比对数据的余额
		params.put("matchType", DictCodeListExt1.匹配类型_任务代码_会计场景.getValue());


        // 与王之昂确认，暂时注释。yangjian 2019/05/21 ----------------------beign
//		List<String> updatSeqNo = bookDetailCompareIntoMapper.getUpdateSeqNo(params);
//        List<List<String>> listNow = DataHandleUtil.splitList(updatSeqNo);
//        for (List<String> listUpdate : listNow) {
//            if (CollectionUtils.isNotEmpty(listUpdate)) {
//                bookDetailCompareIntoMapper.updateDate(listUpdate);
//            }
//        }
//
//		// }
//
//		// 3、更新需要冲销的账务明细(历史表）的反向流水号
//		// for (BokBatchTaskTodo todoNow : listBatchTaskTodo) {
//
//		List<ValBokBookdetailBatch> resultList = bookDetailCompareIntoMapper.selectSeqnoToUpdate(params);
//        List<List<ValBokBookdetailBatch>> list = DataHandleUtil.splitList(resultList);
//        for (List<ValBokBookdetailBatch> listUpdate : list) {
//            if (CollectionUtils.isNotEmpty(listUpdate)) {
//                bookDetailCompareIntoMapper.updateSeqno(listUpdate);
//            }
//        }
        // 与王之昂确认，暂时注释。yangjian 2019/05/21 ----------------------end


//		List<ValBokBookdetailBatch> listInsert = new ArrayList<>();
//		list.forEach(x -> {
//            // 小于最大值
//            if (listInsert.size() < CacheUtil.getAppCache(GlobalConstant.MAX_SQL_NUM, Integer.class)) {
//                listInsert.add(x);
//            }
//            else if (listInsert.size() >= CacheUtil.getAppCache(GlobalConstant.MAX_SQL_NUM, Integer.class)) {
//                bookDetailCompareIntoMapper.updateSeqno(listInsert);
//                listInsert.clear();
//                listInsert.add(x);
//            }
//        });
//        // 如果循环结束，小于最大值
//        if (CollectionUtils.isNotEmpty(listInsert)) {
//            bookDetailCompareIntoMapper.updateSeqno(listInsert);
//        }
    }

}
